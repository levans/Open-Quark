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
 * Function.java
 * Created: June 6, 2001
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;
import org.openquark.cal.util.ArrayMap;


/**
 * Provides an implementation of FunctionalAgent suitable for use in the type checker
 * with functions or pattern bound variables.
 * <P>
 * Creation date: (6/6/01 2:00:01 PM)
 * @author Bo Ilic
 */
public final class Function extends FunctionalAgent {
    
    private static final int serializationSchema = 0;
    
    private FunctionalAgent.Form form;
      
    /** the foreign function info of the entity, or null if not a foreign function. */
    private ForeignFunctionInfo foreignFunctionInfo;
    
    /** indicates whether this function is declared as 'primitive' in the source. */
    private boolean isDeclaredPrimitive;

    /** the level at which this entity was defined in the source code. */
    private int nestingLevel;

    /** This is used when a function is being typechecked. For a function defined in a let, the flag is
     *  removed when the function's definition has been typechecked i.e. the containing function does
     *  not need to be finished with typechecking, only the body of the expression defining the function.
     */
    private boolean typeCheckingDone;
    
    /** (QualifiedName -> Integer) 
     * Map from gem name K to number of times that this function refers to K in its body. 
     */
    private Map<QualifiedName, Integer> dependeeToFrequencyMap;
    
    /**
     * If this function was defined at a nestingLevel > 0, then this is an identifier that
     * uniquely specifies this local function.  If it was defined at the toplevel, then
     * localFunctionIdentifier must be null.
     */
    private LocalFunctionIdentifier localFunctionIdentifier;
    
    /** 
     * (LocalFunctionIdentifier -> Function) Map containing all of the local functions that are defined in this
     * function.  This map will be empty for all non-toplevel functions (ie, those with nestingLevel > 0). 
     */
    private ArrayMap<LocalFunctionIdentifier, Function> localFunctionsMap;
    
    /**
     * True if TypeExpr contains uninstantiated non-generic TypeVars or RecordVars.
     * 
     * We need a separate flag for this because the TypeExpr does not contain enough
     * information to deduce which uninstantiated variables are non-generic (since a 
     * variable is only non-generic with respect to a certain set of non-generics).
     * 
     * The TypeDeclarationInserter uses this to determine when it is possible to create
     * an explicit textual description of the type for this function (it's not possible
     * to write an explicit type declaration that distinguishes generic type variables
     * from non-generic type variables, so all type variables in a type declaration are
     * treated as generic). 
     */
    private boolean typeContainsUninstantiatedNonGenerics;
    
    /**
     * Construct a function entity.
     * Creation date: (7/21/00 2:33:25 PM)
     *    
     * @param functionName the name of the function
     * @param scope the scope of the function
     * @param namedArguments the explicitly named arguments of the function
     *   Can be null for entities with no explicitly specified argument names,
     *   or contain nulls for argument names which aren't specified.
     * @param typeExpr the type of the function
     * @param form the kind or type of entity being added     
     * @param nestingLevel the level at which this entity is defined in CAL source.
     */
    Function(QualifiedName functionName, Scope scope, String[] namedArguments, TypeExpr typeExpr, FunctionalAgent.Form form, int nestingLevel) {

        super(functionName, scope, namedArguments, typeExpr, null); 
        
        if (form != FunctionalAgent.Form.PATTERNVAR && form != FunctionalAgent.Form.FUNCTION) {
            throw new IllegalArgumentException("Function constructor: invalid value for 'form'");
        }
        
        //todoBI Prelude.if should not be treated as a function entity
        /*     
        if (!LanguageInfo.isValidFunctionName(functionName.getUnqualifiedName())) {
            throw new IllegalArgumentException("Function constructor: the argument 'functionName' is invalid.");             
        }
        */
                                      
        this.form = form;      
        this.isDeclaredPrimitive = false;
        this.nestingLevel = nestingLevel;
        this.typeCheckingDone = form != FunctionalAgent.Form.FUNCTION;
        
        this.dependeeToFrequencyMap = new HashMap<QualifiedName, Integer>();
        this.localFunctionsMap = new ArrayMap<LocalFunctionIdentifier, Function>();
    }
       
    // Zero argument constructor for serialization.
    private Function () {
        
    }
    
    /**
     * Returns the kind of entity this is. "Form" is used as a synonym for "kind" or "type"
     * because of the overloaded meanings of the last 2 terms in the type checker!
     * Creation date: (6/4/01 1:49:57 PM)
     * @return FunctionalAgent.Fom
     */
    @Override
    public FunctionalAgent.Form getForm() {
        return form;
    }
    
    /**    
     * @return ForeignFunctionInfo the foreign function info of this foreign 
     *      function entity and null if it is not a foreign function entity.
     */
    public final ForeignFunctionInfo getForeignFunctionInfo() {
        return foreignFunctionInfo;
    }
    
    /**
     * Creation date: (May 2, 2002)
     * @param foreignFunctionInfo
     */    
    final void setForeignFunctionInfo(ForeignFunctionInfo foreignFunctionInfo) {
        this.foreignFunctionInfo = foreignFunctionInfo;
    }
    
    /**
     * Sets the LocalFunctionIdentifier for this local function.  Nesting level must
     * be >0; attempting to set a LocalFunctionIdentifier on a toplevel (ie, nesting level == 0)
     * function will result in an IllegalArgumentException.
     * @param localFunctionIdentifier
     */
    void setLocalFunctionIdentifier(LocalFunctionIdentifier localFunctionIdentifier) {
        if(nestingLevel == 0) {
            throw new IllegalArgumentException("Toplevel functions cannot have a LocalFunctionIdentifier");
        }
        this.localFunctionIdentifier = localFunctionIdentifier;
    }
    
    /**
     * @return The LocalFunctionIdentifier for this function if it has one, or
     *          null otherwise.
     */
    LocalFunctionIdentifier getLocalFunctionIdentifier() {
        return localFunctionIdentifier;
    }
    
    /**
     * Add a local function to the list of local functions defined in this function.
     * @param localFunction LocalFunction
     */
    void addLocalFunction(Function localFunction) {
        if(nestingLevel > 0) {
            throw new IllegalArgumentException("Only toplevel functions can contain local functions");
        }
        localFunctionsMap.put(localFunction.getLocalFunctionIdentifier(), localFunction);
    }
    
    /**
     * Returns the local function whose identifier is identifier, or null if no such
     * local function is defined in this function.
     * @param identifier LocalFunctionIdentifier of local function to retrieve
     * @return LocalFunction
     */
    public Function getLocalFunction(LocalFunctionIdentifier identifier) {
        return localFunctionsMap.get(identifier);
    }
    
    /**
     * Returns the local function whose identifier is identifier, or null if no such
     * local function is defined in this function.
     * @return LocalFunction
     */
    public Function getLocalFunction(QualifiedName qn, String identifier, int index) {
        return localFunctionsMap.get(new LocalFunctionIdentifier(qn, identifier, index));
    }
    
    /**
     * @param n 0-based index
     * @return Function of the nth local function defined within this function
     */
    public Function getNthLocalFunction(int n) {
        return localFunctionsMap.getNthValue(n);
    }
    
    /**
     * @return Number of local functions defined within this function
     */
    public int getNLocalFunctions() {
        return localFunctionsMap.size();
    }

    /**
     * Sets the flag that specifies whether the TypeExpr for this function contains 
     *          uninstantiated nongeneric RecordVars or TypeVars.
     * @param typeContainsUninstantiatedNonGenerics
     */
    void setTypeContainsUninstantiatedNonGenerics(boolean typeContainsUninstantiatedNonGenerics) {
        this.typeContainsUninstantiatedNonGenerics = typeContainsUninstantiatedNonGenerics;
    }
    
    /**
     * @return True if the TypeExpr for this function contains uninstantiated
     *          nongeneric RecordVars or TypeVars.
     *          
     *          We need a separate flag for this because the TypeExpr does not contain enough
     *          information to deduce which uninstantiated variables are non-generic (since a 
     *          variable is only non-generic with respect to a certain set of non-generics).
     * 
     *          The TypeDeclarationInserter uses this to determine when it is possible to create
     *          an explicit textual description of the type for this function (it's not possible
     *          to write an explicit type declaration that distinguishes generic type variables
     *          from non-generic type variables, so all type variables in a type declaration are
     *          treated as generic). 
     */
    boolean typeContainsUninstantiatedNonGenerics() {
        return typeContainsUninstantiatedNonGenerics;
    }
    
    /**
     * @return whether the function is declared as 'primitive' in CAL source
     */
    public boolean isPrimitive() {
        return isDeclaredPrimitive;
    }
    
    /**
     * Sets this entity as representing a function that is declared as 'primitive' in the source
     */
    void setAsPrimitive() {
        isDeclaredPrimitive = true;
    }

    /**
     * Returns the nesting level at which the entity is defined. Top level function are defined at
     * level 0. Functions defined within a top level let are at level 1, ...
     * Creation date: (4/18/01 5:21:58 PM)
     * @return int
     */
    @Override
    int getNestingLevel() {
        return nestingLevel;
    }
               
    /**  
     * Creation date: (6/6/01 2:48:40 PM)
     * @return boolean returns false if the entity is in the process of being type checked
     *                 and thus subject to a change in type.
     */
    @Override
    boolean isTypeCheckingDone() {
        return typeCheckingDone;
    }
    
    /**
     * Finished type checking this entity, and so its type should now only
     * ever be used in a copied form.
     * Creation date: (6/6/01 2:49:12 PM)
     */
    @Override
    void setTypeCheckingDone() {
        super.setTypeCheckingDone(); 
        typeCheckingDone = true;
    }    
        
    /**
     * Make a top-level function.
     *
     * @return Function
     * @param functionName the function's name 
     * @param typeExpr TypeExpr the type expression
     * @param scope Scope the scope of the function
     */
    static Function makeTopLevelFunction(QualifiedName functionName, TypeExpr typeExpr, Scope scope) {
        return makeTopLevelFunction(functionName, null, typeExpr, scope);
    }

    /**
     * Make a top-level function.
     *
     * @return Function
     * @param functionName the function's name 
     * @param argumentNames the names of the arguments to the entity.  Null if none are known.
     * @param typeExpr TypeExpr the type expression
     * @param scope Scope the scope of the function
     */
    static Function makeTopLevelFunction(QualifiedName functionName, String[] argumentNames, TypeExpr typeExpr, Scope scope) {
        //the function is defined at the top-level
        final int nestingLevel = 0;

        return new Function(functionName, scope, argumentNames, typeExpr, FunctionalAgent.Form.FUNCTION, nestingLevel);
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (6/6/01 2:53:31 PM)
     * @return String
     */
    @Override
    public String toString() {
        return super.toString();
    }
    
    /**
     * The FreeVariableFinder makes local names unique by changing a local name such as x occurring in the definition 
     * of f to something like f$x$9. We want to get back to the original local name x. For a top-level function this
     * method simply returns its name.
     * @return the unqualified display name for this entity.
     */
    public String getUnqualifiedDisplayName() {
        return FreeVariableFinder.getDisplayName(getName().getUnqualifiedName());
    }
    
    /**
     * @return (QualifiedName -> Integer) Unmodifiable Map from dependees of this gem to number of times they
     * are referenced. 
     */
    Map<QualifiedName, Integer> getDependeeToFrequencyMap() {
        return Collections.unmodifiableMap(dependeeToFrequencyMap);
    }
    
    /**
     * Add a dependee reference to the raw metric data for this function.  If the dependee
     * already has an entry in the raw data, its frequency is incremented by referenceCount;
     * otherwise, it is added to the dependee frequency map with frequency of refrenceCount.
     * @param dependee QualifiedName of a gem to which this function refers
     * @param referenceCount Number of references to add for this dependee
     */
    void addDependee(QualifiedName dependee, int referenceCount) {
        Integer currentCount = dependeeToFrequencyMap.get(dependee);
        if (currentCount != null) {
            dependeeToFrequencyMap.put(dependee, Integer.valueOf(currentCount.intValue() + referenceCount));
        } else {
            dependeeToFrequencyMap.put(dependee, Integer.valueOf(referenceCount));
        }
    }
    
    /**
     * Write this instance of Function to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    @Override
    final void write (RecordOutputStream s) throws IOException {
        
        s.startRecord(ModuleSerializationTags.FUNCTION_ENTITY, serializationSchema);
        super.writeContent(s);
        s.writeShortCompressed(nestingLevel);
        byte[] flags = RecordOutputStream.booleanArrayToBitArray(new boolean[]{
                typeCheckingDone, 
                foreignFunctionInfo != null, 
                isDeclaredPrimitive, 
                localFunctionIdentifier != null, 
                typeContainsUninstantiatedNonGenerics
        });
        
        if (flags.length != 1) {
            throw new IOException("Unexpected number of flag bytes saveing Function.");
        }
        s.writeByte(flags[0]);
        
        form.write(s);
        if (foreignFunctionInfo != null) {
            try {
                foreignFunctionInfo.write(s);
            } catch (UnableToResolveForeignEntityException e) {
                // If we are serializing the info, then it must not have been originally deserialized lazily
                final IllegalStateException illegalStateException = new IllegalStateException("Java entities in the ForeignFunctionInfo should have been eagerly resolved during the compilation process");
                illegalStateException.initCause(e);
                throw illegalStateException;
            }
        }

        s.writeInt(dependeeToFrequencyMap.size());
        for (final Map.Entry<QualifiedName, Integer> entry : dependeeToFrequencyMap.entrySet()) {
            QualifiedName key = entry.getKey();
            Integer frequency = entry.getValue();
            
            s.writeQualifiedName(key);
            s.writeInt(frequency.intValue());
        }

        int nLocalFunctions = localFunctionsMap.size(); 
        s.writeInt(nLocalFunctions);
        for(int i = 0; i < nLocalFunctions; i++) {
            Function localFunction = localFunctionsMap.getNthValue(i);
            localFunction.write(s);
        }
        
        if(localFunctionIdentifier != null) {
            localFunctionIdentifier.write(s);
        }
        
        s.endRecord();
    }
    
    /**
     * Load an instance of Function from the RecordInputStream.
     * Read position will be before the record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of Function, or null if there was a problem resolving classes.
     * @throws IOException
     */
    static final Function load (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        int nErrorsBeforeLoad = msgLogger.getNErrors();
        
        Function function = new Function();
        try {
            function.read(s, mti, msgLogger);
        } catch (IOException e) {
            // Try to add context to the error message.
            QualifiedName functionName = function.getName();
            throw new IOException ("Error loading Function " + (functionName == null ? "" : functionName.getQualifiedName()) + ": " +  e.getLocalizedMessage());
        }
        
        // Check if entity state would be inconsistent by looking for logger errors.
        if (msgLogger.getNErrors() > nErrorsBeforeLoad) {
            return null;
        }
        
        return function;
    }
    
    /**
     * Load the content of this instance of Function from the RecordInputStream.
     * Read position will be before the record header.
     * 
     * State may be inconsistent if there was a problem during deserialization.
     * In this case, problems will have been added to the logger.
     * 
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    private final void read (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for Record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.FUNCTION_ENTITY);
        if (rhi == null) {
            throw new IOException("Unable to find Function record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "Function", msgLogger);
        
        // Read the EnvironmentEntity content
        super.readContent(s, mti, msgLogger);
        
        nestingLevel = s.readShortCompressed();
        
        byte flags = s.readByte();
        typeCheckingDone = (flags & 0x01) > 0;
        boolean hasFFI = (flags & 0x02) > 0;
        isDeclaredPrimitive = (flags & 0x04) > 0;
        boolean hasLocalFunctionIdentifier = (flags & 0x08) > 0;
        typeContainsUninstantiatedNonGenerics = (flags & 0x10) > 0;
        
        form = FunctionalAgent.Form.load(s);
        
        if (hasFFI) {
            // This can return null, leaving the function entity in an inconsistent state:
            foreignFunctionInfo = ForeignFunctionInfo.load(s, mti.getModuleName(), mti.getModule().getForeignClassLoader(), msgLogger);  
        }
        
        int nDependees = s.readInt();
        dependeeToFrequencyMap = new HashMap<QualifiedName, Integer>();
        for (int i = 0; i < nDependees; i++) {
            QualifiedName key = s.readQualifiedName();
            Integer frequency = Integer.valueOf(s.readInt());
            
            dependeeToFrequencyMap.put(key, frequency);
        }
        
        int nLocalFunctions = s.readInt();
        localFunctionsMap = new ArrayMap<LocalFunctionIdentifier, Function>();
        for(int i = 0; i < nLocalFunctions; i++) {
            Function localFunction = Function.load(s, mti, msgLogger);
            localFunctionsMap.put(localFunction.getLocalFunctionIdentifier(), localFunction);
        }
        
        if(hasLocalFunctionIdentifier) {
            localFunctionIdentifier = LocalFunctionIdentifier.load(s, mti.getModuleName(), msgLogger);
        }
        
        s.skipRestOfRecord();
    }
}