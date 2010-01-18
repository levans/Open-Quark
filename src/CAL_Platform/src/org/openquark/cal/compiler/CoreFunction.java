/*
 * Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *  
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *  
 *     * Neither the name of Business Objects nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


/*
 * CoreFunction.java
 * Created: March 7, 2001
 * By: Luke Evans
 */
package org.openquark.cal.compiler; 

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * CoreFunction holds a desugared representation of CAL functions (and other
 * functional agents such as data constructors and class methods) suitable for use by run-times
 * or low level optimizers such as ExpressionAnalyzer.
 * <p>
 * CoreFunction is intended to remain immutable to clients (so that there are no public mutators). The idea
 * here is that the core function is that part of desugared CAL that is determined by the compiler.
 * 
 * @author LWE
 */
public final class CoreFunction {
      
    private static final int serializationSchema = 1;

    // Constants used to indicate the type of the literal
    // value when serializing.
    private static final byte LITERAL_TYPE_CHARACTER = 0;
    private static final byte LITERAL_TYPE_BOOLEAN = 1;
    private static final byte LITERAL_TYPE_INTEGER = 2;
    private static final byte LITERAL_TYPE_DOUBLE = 3;
    private static final byte LITERAL_TYPE_BYTE = 4;
    private static final byte LITERAL_TYPE_SHORT = 5;
    private static final byte LITERAL_TYPE_FLOAT = 6;
    private static final byte LITERAL_TYPE_LONG = 7;
    private static final byte LITERAL_TYPE_STRING = 8;
    private static final byte LITERAL_TYPE_BIG_INTEGER = 9;        
    
    // Constants used to indicate the type of the function.
    private static final byte PRIMITIVE_FUNCTION_TYPE = 0;
    private static final byte FOREIGN_FUNCTION_TYPE = 1;
    private static final byte CAL_FUNCTION_TYPE = 2;
    private static final byte CAL_DATA_CONSTRUCTOR_TYPE = 3;
    
    /** 
     * The name of the core function. For functions corresponding to top level CAL language functions, this will
     * be the top-level CAL language function name.
     */
    private final QualifiedName name;    

    /** 
     * Holds the unqualified formal parameters names. They belong to the same module as the sc
     * and so a qualification is unnecessary. Note: the formal parameters are the formal parameters
     * that appear explicitly in the textual definition of this function, as well as the parameters
     * added because of overload resolution. So for example, 
     * f x y z = [x, y, z] // 3 formal parameters x, y, z
     * g x = f x // 1 formal parameter x (even though g takes 3 arguments)
     * f xs = sum xs // 2 formal parameters. 1 for the hidden dictionary argument to resolve the overloading for sum. 1 for xs.
     */
    private final String[] formalParameters; 
    
    private static final String[] NO_ARGS_FORMAL_PARAMETERS = new String[0];
    
    /**
     * ith element is true if the ith argument is strict. Same length as formalParameters.
     */
    private boolean[] parameterStrictness;
    
    private static final boolean[] NO_ARGS_PARAMETER_STRICTNESS = new boolean[0];
    
    /**
     * Same length as formalParameters. 
     * the ith element is the type of the ith formal parameter.
     * if the type is not determinable, the value of that element of the array is null, For example, this is mostly the
     *    case with arguments corresponding to overload resolution variables. 
     */
    private final TypeExpr[] parameterTypes;
    
    private static final TypeExpr[] NO_ARGS_PARAMETER_TYPES = new TypeExpr[0];

    /**
     * The result type of the expression.
     */
    
    private final TypeExpr resultType;
    
    private Expression expression;          
    
    private long timeStamp;

    /** true if this core function contains a fully saturated call to itself. */
    private boolean isTailRecursive = false;
    
    /** True if this CoreFunction represents a function in an adjunct. */
    private boolean isForAdjunct = false;

    /**
     * Flag indication that the expression used to have a call to unsafeCoerce
     */
    private boolean hadUnsafeCoerce = false;
        
    /** 
     * If this function can be considered an alias of another function then aliasOf
     * will hold the name of another function.  
     * 
     * One function is an alias of another if all references to the alias can be 
     * safely replaced by references to the aliased function.  This is true when:
     * 
     *   1. the body of the alias function consists solely of a call to the aliased
     *      function.
     *   2. All the arguments to the alias are passed to the aliased function in the
     *      same order as the alias's argument list 
     *   3. the aliased function and the alias have the same arity
     *   4. the aliased function and the alias have compatible strictness (see below)
     *   5. the aliased function and the alias aren't defined in terms of each other
     *      (ie, there is no cycle between the two)
     *   6. the aliased function is not a 0-arity foreign function
     * 
     * Ex, in the following:
     *   
     *   foo x y = bar x y;
     *   
     *   bar a b = baz a b;
     *   
     *   baz i j k = quux i j k;
     *   
     *   quux m n o = quux2 m n o;
     *   
     *   quux2 p q r = baz p q r + baz r q p;
     *   
     * foo is an alias of bar.
     * bar is _not_ an alias of baz, because bar and baz have different arities.
     * baz is _not_ an alias of quux, because quux is defined in terms of quux2,
     * which is defined in terms of baz.  
     * 
     * The strictness of an alias is compatible with that of an aliased function
     * when the same arguments are plinged, up to the last plinged argument of the
     * alias.  In other words, it's okay for an aliased function to have extra 
     * plinged arguments to the right of the last pling on the alias, but otherwise
     * they must match exactly.  Ex:
     * 
     *   alpha x y z = delta x y z;
     *   
     *   beta x !y z = delta x y z;
     *   
     *   gamma !x y z = delta x y z;
     *   
     *   delta x !y !z = ...;
     *   
     *   
     *   epsilon x y !z = zeta x y z;
     *   
     *   zeta !x y !z = ...;
     *   
     * In the above code, alpha has compatible strictness with delta, because
     * alpha doesn't have any plinged arguments.
     * 
     * beta has compatible strictness with delta, because the first and second
     * arguments have the same plings; the third argument doesn't need to have
     * the same strictness because the second argument is beta's last plinged
     * argument.
     * 
     * gamma and delta do _not_ have compatible strictness, because gamma's 
     * first argument is plinged and delta's is not.
     * 
     * epsilon and zeta also don't have compatible strictness, because the
     * first argument's strictness does not match (which is important because
     * the third argument is epsilon's rightmost strict argument).
     *    
     */
    private QualifiedName aliasOf;

    /**
     * If this function is a literal value. (e.g. foo = 1.0;) this member will
     * hold the literal value, null otherwise.
     */
    private Object literalValue = null;
    
    /** Set of names (i.e. String) comprising the names of functions that are strongly connected to this function.*/
    private Set<String> connectedComponents = new HashSet<String>();
    
    /** Flag indicating the type of the function. This will be one of the constant values:
     * CAL_FUNCTION_TYPE, FOREIGN_FUNCTION_TYPE, or PRIMITIVE_FUNCTION_TYPE */
    private final byte functionType;
    
    /**
     * This flag indicates whether the function can be eagerly evaluated.
     * If true it means that we will evaluate the function in any situation where it
     * can be determined that each function argument is already in WHNF or is an 
     * expression for which we can ignore laziness.
     */
    private final boolean functionCanBeEagerlyEvaluated;
    
    /**
     * Factory method for creating a CoreFunction instance for a DataConstructor
     * @param dc
     * @param timeStamp
     * @return the new CoreFunction
     */
    static final CoreFunction makeDataConstructorCoreFunction (DataConstructor dc, long timeStamp) {
        return new CoreFunction (dc, timeStamp);
    }
    
    /**
     * Factory method for creating a CoreFunction for a primitive function
     * @param name
     * @param formalParameters
     * @param parameterStrictness
     * @param parameterTypes
     * @param resultType
     * @param timeStamp
     * @return the new CoreFunction
     */
    static final CoreFunction makePrimitiveCoreFunction(QualifiedName name,
                                                        String[] formalParameters,
                                                        boolean[] parameterStrictness,
                                                        TypeExpr[] parameterTypes,
                                                        TypeExpr resultType,
                                                        long timeStamp) {
        return new CoreFunction (name, 
                                 formalParameters, 
                                 parameterStrictness, 
                                 parameterTypes, 
                                 resultType, 
                                 PRIMITIVE_FUNCTION_TYPE,
                                 EagerFunctionInfo.canFunctionBeEagerlyEvaluated(name),
                                 timeStamp);
    }

    /**
     * Factory method for creating a CoreFunction for a foreign function.
     * @param name
     * @param formalParameters
     * @param parameterStrictness
     * @param parameterTypes
     * @param resultType
     * @param timeStamp
     * @return the new CoreFunction
     */
    static final CoreFunction makeForeignCoreFunction(QualifiedName name,
                                                      String[] formalParameters,
                                                      boolean[] parameterStrictness,
                                                      TypeExpr[] parameterTypes,
                                                      TypeExpr resultType,
                                                      long timeStamp) {
        return new CoreFunction (name, 
                                 formalParameters, 
                                 parameterStrictness, 
                                 parameterTypes, 
                                 resultType, 
                                 FOREIGN_FUNCTION_TYPE,
                                 EagerFunctionInfo.canFunctionBeEagerlyEvaluated(name),
                                 timeStamp);
    }

    /**
     * Factory method for creating a CoreFunction for a CAL function.
     * @param name
     * @param formalParameters
     * @param parameterStrictness
     * @param parameterTypes
     * @param resultType
     * @param timeStamp
     * @return the new CoreFunction
     */
    static final CoreFunction makeCALCoreFunction(QualifiedName name,
                                                  String[] formalParameters,
                                                  boolean[] parameterStrictness,
                                                  TypeExpr[] parameterTypes,
                                                  TypeExpr resultType,
                                                  long timeStamp) {
        return new CoreFunction (name, 
                                 formalParameters, 
                                 parameterStrictness, 
                                 parameterTypes, 
                                 resultType, 
                                 CAL_FUNCTION_TYPE,
                                 EagerFunctionInfo.canFunctionBeEagerlyEvaluated(name),
                                 timeStamp);
    }
    
    /**
     * The SupercombinatorActivationRecord.
     * Creation date: (3/7/00 4:11:07 PM)
     * @param name
     * @param formalParameters
     * @param parameterStrictness
     * @param parameterTypes
     * @param resultType
     * @param functionType
     * @param functionCanBeEagerlyEvaluated
     * @param timeStamp
     */
    private CoreFunction(QualifiedName name,
            String[] formalParameters,
            boolean[] parameterStrictness,
            TypeExpr[] parameterTypes,
            TypeExpr resultType,
            byte functionType,
            boolean functionCanBeEagerlyEvaluated,
            long timeStamp) {
        
        if (name == null || resultType == null) {
            throw new IllegalArgumentException();
        }
        
        final int nArgs = formalParameters.length;
                        
        if (nArgs != parameterStrictness.length || 
            nArgs != parameterTypes.length) {            
            throw new IllegalArgumentException();
        }
        
        if (nArgs == 0) {
            this.formalParameters = NO_ARGS_FORMAL_PARAMETERS;
            this.parameterStrictness = NO_ARGS_PARAMETER_STRICTNESS;
            this.parameterTypes = NO_ARGS_PARAMETER_TYPES;
        } else {
            this.formalParameters = formalParameters;
            this.parameterStrictness = parameterStrictness;
            this.parameterTypes = parameterTypes;        
        }
                 
        this.name = name;           
        this.resultType = resultType;
        this.functionType = functionType;
        this.functionCanBeEagerlyEvaluated = functionCanBeEagerlyEvaluated;
        this.timeStamp = timeStamp;    
    }      

    /**
     * Create a CoreFunction instance for a data constructor.
     * @param dc
     * @param timeStamp
     */
    private CoreFunction (DataConstructor dc, long timeStamp) {
        if (dc == null) {
            throw new IllegalArgumentException();
        }

        this.name = dc.getName();  
        
        final int nArgs = dc.getArity();

        if (nArgs == 0) {
            this.formalParameters = NO_ARGS_FORMAL_PARAMETERS;
            this.parameterStrictness = NO_ARGS_PARAMETER_STRICTNESS;
            this.parameterTypes = NO_ARGS_PARAMETER_TYPES;
        } else {
            this.formalParameters = new String [nArgs];
            for (int i = 0; i < nArgs; ++i) {
                this.formalParameters [i] = dc.getNthFieldName(i).toString();
            }
            this.parameterStrictness = dc.getArgStrictness();
            this.parameterTypes = new TypeExpr [nArgs];
        }
               
        TypeExpr dcType = dc.getTypeExpr();
        TypeExpr[] tft = dcType.getTypePieces(nArgs);
        System.arraycopy(tft, 0, this.parameterTypes, 0, this.parameterTypes.length);
        this.resultType = tft[tft.length-1];
                 
        this.functionType = CAL_DATA_CONSTRUCTOR_TYPE;
        this.functionCanBeEagerlyEvaluated = false;
        this.timeStamp = timeStamp;    
        
    }
    
    /**
     * Return rich diagnostic information about the supercombinator.
     * @return a MessageKind object containing the information about this supercombinator
     */
    MessageKind dump() {
        if( getNFormalParameters() == 0) {
            return new MessageKind.Info.SupercombinatorInfoDumpCAF(toString(), formalParameters.toString());
        } else {
            return new MessageKind.Info.SupercombinatorInfoDumpNonCAF(toString(), formalParameters.toString());
        }        
    }
    
    public String[] getFormalParameters() {
        if (formalParameters.length == 0) {
            return NO_ARGS_FORMAL_PARAMETERS;
        }
        
        return formalParameters.clone();
    }
         
    /**
     * Get the number of formal arguments for this supercombinator.
     * Creation date: (3/9/00 3:30:22 PM)
     * @return int
     */
    public int getNFormalParameters() {
        return formalParameters.length;
    } 
    
    public String getNthFormalParameter(int n) {
        return formalParameters[n];            
    }
    
    /**
     * Get the ordinal (0 based) for the named formal parameter.    
     * @return int the  ordinal or -1 if not found  
     * @param parameterName the name of the argument variable    
     */
    public int getFormalParameter(String parameterName) {
        for (int i = 0, n = getNFormalParameters(); i < n; ++i) {
            if (parameterName.equals(formalParameters[i])) {
                return i;
            }
        }
        
        return -1;                    
    }    
    
    public boolean isNthFormalParameterStrict(int paramN) {
        return parameterStrictness[paramN];
    }
    
    /**
     * @return - true if any of the arguments are strict.
     */
    public boolean hasStrictArguments() {
        for (int i = 0, n = getNFormalParameters(); i < n; ++i) {
            if (isNthFormalParameterStrict(i)) {
                return true;
            }
        }
        
        return false;       
    }

    public boolean[] getParameterStrictness(){
        if (parameterStrictness.length == 0) {
            return NO_ARGS_PARAMETER_STRICTNESS;
        }
        
        return parameterStrictness.clone();
    }
    
    public TypeExpr[] getParameterTypes() {
        if (parameterTypes.length == 0) {
            return NO_ARGS_PARAMETER_TYPES;
        }
        return TypeExpr.copyTypeExprs(parameterTypes);
    }
    
    public TypeExpr getResultType() {
        return resultType;
    }
    
    /**
     * Get the type signature for the function. This is arguments and return type.
     * @return The function type signature.
     */
    public TypeExpr[] getType(){
        TypeExpr[] argAndResultTypes = new TypeExpr[parameterTypes.length + 1];
        for(int i = 0; i < parameterTypes.length; ++i){
            argAndResultTypes[i] = parameterTypes[i];
        }
        argAndResultTypes[parameterTypes.length] = resultType;
        
        argAndResultTypes = TypeExpr.copyTypeExprs(argAndResultTypes);

        return argAndResultTypes;
    }
      
    /**
     * Return this supercombinator's name.
     * Creation date: (3/17/00 3:50:52 PM)
     * @return QualifiedName the name
     */
    public QualifiedName getName() {
        return name;
    }              
         
    /**
     * Return description of CoreFunction.
     * @return a string description of this CoreFunction
     */
    @Override
    public String toString() {
        
        StringBuilder result = new StringBuilder(name.getQualifiedName());
        result.append('\n');
        for (int i = 0, nArgs = getNFormalParameters(); i < nArgs; ++i) {
            result.append("arg#").append(i).append(" = "); 
            if (isNthFormalParameterStrict(i)) {
                result.append('!');
            }
            result.append(getNthFormalParameter(i));
            result.append(" :: ");
            result.append(parameterTypes[i]);
            result.append('\n');            
        }
        result.append('\n');             
        return result.toString();
    }
    
    /**
     * Set the expression form for the associated supercombinator.
     * @param e
     */
    public void setExpression (Expression e) {
        expression = e;
        if (expression != null) {
            if (expression.asLiteral() != null && getNFormalParameters() == 0) {
                literalValue = expression.asLiteral().getLiteral();
                aliasOf = null;
            } else {
                literalValue = null;
            }
        }
    }
    
    
    /**
     * Reset the strictness of the arguments. This is only for use in the compiler. Current needed
     * by the CAL optimizer to updated the strictness as part of the optimization that moves Seq'ed
     * arguments to plinged arguments.
     * 
     * @param parameterStrictness The new array of strictness values.
     */
    void setParameterStrictness(boolean[] parameterStrictness){
        if (formalParameters.length != parameterStrictness.length){ 
            throw new IllegalArgumentException();
        }

        this.parameterStrictness = parameterStrictness;
    }    
    
    /**
     * Get the expressoin form for the associated supercombinator.
     * @return Expression
     */
    public Expression getExpression () {
        return expression;
    }
    
    /**
     * Return the timestamp associated with this record.
     * @return long
     */
    public long getTimeStamp () {
        return timeStamp;
    }
    /**
     * @return Returns the isTailRecursive.
     */
    public boolean isTailRecursive() {
        return isTailRecursive;
    }
    /**
     * @param isTailRecursive The isTailRecursive to set.
     */
    public void setIsTailRecursive(boolean isTailRecursive) {
        this.isTailRecursive = isTailRecursive;
    }
    /**
     * @return Returns the aliasOf.
     */
    public QualifiedName getAliasOf() {
        return aliasOf;
    }
    /**
     * @param aliasOf The aliasOf to set.
     */
    public void setAliasOf(QualifiedName aliasOf) {
        this.aliasOf = aliasOf;
    }

    /**
     * @return (String set) the connectedComponents. Will be unmodifiable and not null.
     */
    public Set<String> getStronglyConnectedComponents() {
        return Collections.unmodifiableSet(connectedComponents);
    }
    
    /**
     * @param connectedComponents (String set) The connectedComponents to set. Cannot be null.
     */
    public void setStronglyConnectedComponents(Set<String> connectedComponents) {
        if (connectedComponents == null) {
            throw new NullPointerException();
        }
        this.connectedComponents = connectedComponents;
    }
    
    /**
     * @param function name of the function, assumed to be in the same module as this function
     * @return true if this is strongly connected to function.
     */
    public boolean isStronglyConnectedTo (String function) {
        if (connectedComponents.contains(function)) {
            return true;
        }
        return false;
    }    
    
    /**
     * Write this instance of CoreFunction to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    public final void write (RecordOutputStream s) throws IOException {
        s.startRecord (ModuleSerializationTags.CORE_FUNCTION, serializationSchema);

        // name
        s.writeQualifiedName(name);
        
        // flags for existence of 'aliasOf', tail recursion, and safety.
        byte[] flags = RecordOutputStream.booleanArrayToBitArray(new boolean[]{aliasOf != null, isTailRecursive, canFunctionBeEagerlyEvaluated()});
        if (flags.length != 1) {
            throw new IOException("Unexpected number of bytes for flags in CoreFunction: " + flags.length);
        }
        s.writeByte(flags[0]);
        
        // formalParameters
        s.writeShortCompressed(formalParameters.length);
        for (int i = 0; i < formalParameters.length; ++i) {            
            s.writeUTF (formalParameters [i]);
        }

        // parameterStrictnss
        int nBytesForArgFlags = ((formalParameters.length) + 7) / 8;
        byte[] bytesForArgFlags = RecordOutputStream.booleanArrayToBitArray(parameterStrictness);
        if (bytesForArgFlags.length != nBytesForArgFlags) {
            throw new IOException("Unexpected number of bytes for strictness flags in CoreFunction.");
        }
        s.write(bytesForArgFlags);
        
        // parameterTypes
        boolean[] typeExistence = new boolean[formalParameters.length]; 
        for (int i = 0; i < formalParameters.length; ++i) {
            typeExistence[i] = parameterTypes[i] != null;
        }
        bytesForArgFlags = RecordOutputStream.booleanArrayToBitArray(typeExistence);
        if (bytesForArgFlags.length != nBytesForArgFlags) {
            throw new IOException("Unexpected number of bytes for strictness flags in CoreFunction.");
        }
        s.write(bytesForArgFlags);

        // Write the function type
        {
            ArrayList<TypeExpr> types = new ArrayList<TypeExpr>(formalParameters.length+1);
            for (int i = 0; i < formalParameters.length; ++i) {            
                if (parameterTypes[i] != null) {
                    types.add(parameterTypes[i]);
                }
            }
            types.add(resultType);
            TypeExpr.write(s, types);
        }

        // aliasOf
        if (aliasOf != null) {
            s.writeQualifiedName(aliasOf);
        }
        
        // timestamp
        s.writeLong (timeStamp);
        
        s.writeShortCompressed(connectedComponents.size());
        for (final String componentName : connectedComponents) {
            s.writeUTF (componentName);
        }

        // Write out the function type (i.e. primitive, foreign, cal
        s.writeByte(functionType);
        
        
        s.writeBoolean(literalValue != null);
        if (literalValue != null) {
            Class<? extends Object> valueClass = literalValue.getClass();
            if (valueClass == java.lang.Character.class) {
                s.writeByte(LITERAL_TYPE_CHARACTER);
                s.writeChar(((Character)literalValue).charValue());
            } else if (valueClass == java.lang.Boolean.class) {
                s.writeByte(LITERAL_TYPE_BOOLEAN);
                s.writeBoolean(((Boolean)literalValue).booleanValue());
            } else if (valueClass == java.lang.Integer.class) {
                s.writeByte(LITERAL_TYPE_INTEGER);
                s.writeInt(((Integer)literalValue).intValue());
            } else if (valueClass == java.lang.Double.class) {
                s.writeByte(LITERAL_TYPE_DOUBLE);
                s.writeDouble(((Double)literalValue).doubleValue());
            } else if (valueClass == java.lang.Byte.class) {
                s.writeByte(LITERAL_TYPE_BYTE);
                s.writeByte(((Byte)literalValue).byteValue());
            } else if (valueClass == java.lang.Short.class) {
                s.writeByte(LITERAL_TYPE_SHORT);
                s.writeShort(((Short)literalValue).shortValue());
            } else if (valueClass == java.lang.Float.class) {
                s.writeByte(LITERAL_TYPE_FLOAT);
                s.writeFloat(((Float)literalValue).floatValue());
            } else if (valueClass == java.lang.Long.class) {
                s.writeByte(LITERAL_TYPE_LONG);
                s.writeLong(((Long)literalValue).longValue());
            } else if (valueClass == String.class) {
                s.writeByte(LITERAL_TYPE_STRING);
                s.writeUTF((String)literalValue);
            } else if (valueClass == BigInteger.class) {
                s.writeByte(LITERAL_TYPE_BIG_INTEGER);
                String ds = ((BigInteger)literalValue).toString();
                s.writeUTF(ds);
            }  else {
                throw new IOException ("Attemp to write unhandled literal value of type: " + literalValue.getClass().getName());
            }
        }
        
        s.writeBoolean(isForAdjunct);
        s.writeBoolean(hadUnsafeCoerce);
        
        expression.write(s);
        
        s.endRecord ();
    }
    
    /**
     * Load an instance of CoreFunction from the RecordInputStream.
     * The read position of the RecordInputStream will be before the record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of CoreFunction.
     * @throws IOException
     */
    public static final CoreFunction load (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.CORE_FUNCTION);
        if (rhi == null) {
            throw new IOException("Unable to find CoreFunction record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "CoreFunction", msgLogger);
        
        QualifiedName name = s.readQualifiedName();
        try {
            byte flags = s.readByte();
            boolean aliasOfExists = (flags & 0x01) > 0;
            boolean isTailRecursive = (flags & 0x02) > 0;
            boolean functionCanBeEagerlyEvaluated = (flags &0x04) > 0;
            
            int nArgs = s.readShortCompressed();
            String[] argNames = new String[nArgs];
            for (int i = 0; i < nArgs; ++i) {
                argNames[i] = s.readUTF();
            }
            
            int nBytesForArgFlags = (nArgs + 7) / 8;
            byte[] argFlags = new byte[nBytesForArgFlags];
            for (int i = 0; i < argFlags.length; ++i) {
                argFlags[i] = s.readByte();
            }
            
            boolean[] argStrictness = RecordInputStream.bitArrayToBooleans(argFlags, nArgs);
            
            TypeExpr[] argTypes = new TypeExpr[nArgs];
            for (int i = 0; i < argFlags.length; ++i) {
                argFlags[i] = s.readByte();
            }
            boolean[] typeExistence = RecordInputStream.bitArrayToBooleans(argFlags, nArgs);
            int nArgsPresent = 0;
            for (int i = 0; i < nArgs; ++i) {
                if (typeExistence[i]) {
                    nArgsPresent++;
                }
            }
            
            TypeExpr[] types = TypeExpr.load(s, mti, nArgsPresent+1, msgLogger);
            int iSource = 0;
            // length of type existence is the number of args
            for(int iDest = 0; iDest < nArgs; ++iDest){
                if (typeExistence[iDest]){
                    argTypes[iDest] = types[iSource++];
                }
            }
            
            TypeExpr resultType = types[nArgsPresent];
            
            QualifiedName aliasOf = null;
            if (aliasOfExists) {
                aliasOf = s.readQualifiedName();
            }
            
            long timeStamp = s.readLong();
            
            int nConnectedComponents = s.readShortCompressed();
            Set<String> connectedComponents = new HashSet<String>();
            for (int i = 0; i < nConnectedComponents; ++i) {
                String componentName = s.readUTF();
                connectedComponents.add(componentName);
            }
            
            byte type = s.readByte();
            
            CoreFunction cf = new CoreFunction (name, 
                                                argNames, 
                                                argStrictness, 
                                                argTypes, 
                                                resultType, 
                                                type, 
                                                functionCanBeEagerlyEvaluated,
                                                timeStamp);
            
            cf.setIsTailRecursive(isTailRecursive);
            cf.setAliasOf(aliasOf);
            cf.setStronglyConnectedComponents(connectedComponents);
            
            if (!s.atEndOfRecord()) {
                boolean b = s.readBoolean();
                if (b) {
                    byte typeByte = s.readByte();
                    if (typeByte == LITERAL_TYPE_CHARACTER) {
                        cf.literalValue = Character.valueOf(s.readChar());
                    } else if (typeByte == LITERAL_TYPE_BOOLEAN) {
                        cf.literalValue = Boolean.valueOf(s.readBoolean());
                    } else if (typeByte == LITERAL_TYPE_INTEGER) {
                        cf.literalValue = Integer.valueOf(s.readInt());
                    } else if (typeByte == LITERAL_TYPE_DOUBLE) {
                        cf.literalValue = Double.valueOf(s.readDouble()); 
                    } else if (typeByte == LITERAL_TYPE_BYTE) {
                        cf.literalValue = Byte.valueOf(s.readByte());
                    } else if (typeByte == LITERAL_TYPE_SHORT) {
                        cf.literalValue = Short.valueOf(s.readShort());
                    } else if (typeByte == LITERAL_TYPE_FLOAT) {
                        cf.literalValue = Float.valueOf(s.readFloat());
                    } else if (typeByte == LITERAL_TYPE_LONG) {
                        cf.literalValue = Long.valueOf(s.readLong());
                    } else if (typeByte == LITERAL_TYPE_STRING) {
                        cf.literalValue = s.readUTF();
                    } else if (typeByte == LITERAL_TYPE_BIG_INTEGER) {
                        String ds = s.readUTF();
                        cf.literalValue = new BigInteger(ds);
                    }  else {
                        throw new IOException ("Attemp to read unhandled literal value.");
                    }
                    
                }
            }
            
            boolean isForAdjunct = s.readBoolean();
            cf.setForAdjunct(isForAdjunct);
            
            if (rhi.getSchema() >= serializationSchema){
                boolean hadUnsafeCoerce = s.readBoolean();
                if (hadUnsafeCoerce){
                    cf.setHadUnsafeCoerce();
                }
            }
            
            if (mti.getModule().mustLoadExpressions()) {
                Expression expr = Expression.load (s, mti, msgLogger);
                cf.setExpression (expr);
            }
            
            return cf;
        
        } catch (IOException e) {
            throw new IOException ("Error loading CoreFunction " + name.getQualifiedName() + ": " + e.getLocalizedMessage());
        } finally {
            s.skipRestOfRecord();
        }
    }

    /**
     * @return literal value if this supercombinator is defined as a literal value, null otherwise
     */
    public Object getLiteralValue() {
        return literalValue;
    }
    
    /** 
     * @return true if this is a primitive function, false otherwise.
     */
    public boolean isPrimitiveFunction() {
        return functionType == PRIMITIVE_FUNCTION_TYPE;
    }
    
    /**
     * @return true if this is a foreign function, false otherwise.
     */
    public boolean isForeignFunction () {
        return functionType == FOREIGN_FUNCTION_TYPE;
    }
    
    /**
     * @return true if this is a CAL function (i.e. not primitive and not foreign), false otherwise.
     */
    public boolean isCALFunction () {
        return functionType == CAL_FUNCTION_TYPE;
    }
    
    /**
     * @return true if this is a CAL Data Constructor.
     */
    public boolean isDataConstructor () {
        return functionType == CAL_DATA_CONSTRUCTOR_TYPE;
    }
    
    /**
     * @return true if this CAL function is marked as being valid for
     * eager evaluation. 
     */
    public boolean canFunctionBeEagerlyEvaluated () {
        return functionCanBeEagerlyEvaluated;
    }

    public boolean isForAdjunct() {
        return isForAdjunct;
    }

    public void setForAdjunct(boolean isForAdjunct) {
        this.isForAdjunct = isForAdjunct;
    }
    
    /** 
     * @return True if the function contained a call to unsafeCoerce.
     */
    public boolean getHadUnsafeCoerce(){
        return hadUnsafeCoerce;
    }

    /** 
     * Mark the expression as having contained an unsafeCoerce call.
     */
    public void setHadUnsafeCoerce(){
        hadUnsafeCoerce = true;
    }
}