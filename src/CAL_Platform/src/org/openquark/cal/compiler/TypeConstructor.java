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
 * TypeConstructor.java
 * Created: July 24, 2001
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.util.ArrayMap;


/**
 * Represents type constructors in CAL e.g. "Cal.Core.Prelude.Int", "Cal.Core.Prelude.Maybe".
 * <P>
 * Creation date: (July 24, 2001)
 * @author Bo Ilic
 */
public final class TypeConstructor extends ScopedEntity {
       
    private static final int serializationSchema = 0;
    
    /**
     * This class is used to store information about a deriving clause element
     * It records the name of class instance and the position in the source code
     * of that name
     * @author mbyne
     */
    final static class DerivingClauseInfo {
        /** the name of the type - this cannot be null*/
        final private QualifiedName name;
        
        /** the source range of the deriving type - this may be null*/
        final private SourceRange range;

        DerivingClauseInfo(QualifiedName name, SourceRange range) {
            if (name == null) {
                throw new NullPointerException("The argument name cannot be null");
            }
            
            this.name = name;
            this.range = range;
        }

        /** get the source range of the name, this may be null if it is not available*/
        SourceRange getSourceRange() {
            return range;
        }

        /** get the name of the class*/
        QualifiedName getName() {
            return name;
        }
    }
    
    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #loadInit} and {@link #loadFinal} methods.
     */
    private static final short[] TYPE_CONSTRUCTOR_ENTITY_RECORD_TAGS = new short[] {
        ModuleSerializationTags.TYPE_CONSTRUCTOR_ENTITY,
        ModuleSerializationTags.FOREIGN_TYPE_CONSTRUCTOR_ENTITY
    };
    
    /**
     * the kind of this TypeConstructor. For example, Prelude.List has kind * -> * while Prelude.Ordering has kind *.
     * For type constuctor entities that have successfully passed type checking, this will not have any kind variables in it.
     */
    private KindExpr kindExpr;
    
    /**
     * extra information about the Java implementation of this type if it was defined using a foreign data declaration. 
     * null if this is not a foreign type. 
     */
    private ForeignTypeInfo foreignTypeInfo;
    
    /** 
     * (String -> DataConstructor)
     * the data constructors defined in the data declaration for the type. They are ordered in the same
     * order as in the data declaration.
     */
    private final ArrayMap<String, DataConstructor> dataConstructorMap;
    
    /**
     * Names and positions of the type classes appearing in the "deriving" clause of the type.
     * These define the instances that are automatically derived for the type.
     * They are ordered in the order that they appear textually in the deriving clause.
     * There are no duplicates.
     */
    private DerivingClauseInfo[] derivingClauseTypeClassInfos;
    
    static private final DerivingClauseInfo[] NO_DERIVING_CLAUSE = new DerivingClauseInfo[0];   
      
    /** the TypeConstructor for the Prelude.Function type */
    static final TypeConstructor FUNCTION = new TypeConstructor(CAL_Prelude.TypeConstructors.Function, Scope.PUBLIC, KindExpr.makeKindChain(3), null, NO_DERIVING_CLAUSE, null);
        
    /**
     * map from the QualifiedName of the built-in type constructor to its TypeConstructor. Note: the name is an internal name for the
     * type constructors that do not have CAL source names.
     */
    private static final Map<QualifiedName, TypeConstructor> builtInTypeConstructors = makeBuiltInTypeConstructors();

    /**
     * TypeConstructor constructor comment.
     * @param name
     * @param scope
     * @param kindExpr
     * @param foreignTypeInfo
     * @param derivingClauseTypeClassNames
     * @param calDocComment
     */
    private TypeConstructor(QualifiedName name, Scope scope,
            KindExpr kindExpr, ForeignTypeInfo foreignTypeInfo,
            DerivingClauseInfo[] derivingClauseTypeClassNames,
            CALDocComment calDocComment) {

        super(name, scope, calDocComment);
        
        if (kindExpr == null || derivingClauseTypeClassNames == null) {
            throw new NullPointerException();  
        }
        
        if (foreignTypeInfo != null && kindExpr != KindExpr.STAR) {
            throw new IllegalArgumentException ("foreign type constructors must have kind *.");
        }
             
        this.kindExpr = kindExpr;
        this.foreignTypeInfo = foreignTypeInfo;
        this.derivingClauseTypeClassInfos = derivingClauseTypeClassNames;
        
        this.dataConstructorMap = new ArrayMap<String, DataConstructor>();
    }
    
    // zero argument constructor used for serialization.
    private TypeConstructor() {
        this.dataConstructorMap = new ArrayMap<String, DataConstructor>();
    }
          
    /**
     * The factory method for TypeConstructor. 
     * @param name
     * @param scope
     * @param kindExpr
     * @param foreignTypeInfo use null if this is not a foreign data type.
     * @param derivingClauseTypeClassNames
     * @param calDocComment the CALDoc associated with this entity, or null if there is none.
     * @return TypeConstructor
     */
    static final TypeConstructor makeTypeConstructor(QualifiedName name, Scope scope, KindExpr kindExpr, ForeignTypeInfo foreignTypeInfo, DerivingClauseInfo[] derivingClauseTypeClassNames, CALDocComment calDocComment) {
                               
        TypeConstructor typeCons = builtInTypeConstructors.get(name);
        if (typeCons != null) {
            return typeCons;
        }
            
        return new TypeConstructor (name, scope, kindExpr, foreignTypeInfo, derivingClauseTypeClassNames, calDocComment);
    }   
    
    /**
     * The kind of this TypeConstructor. 
     * For example, the kind of Prelude.Function this will be * -> * -> *.
     * The kind of Prelude.Maybe is * -> *. 
     * The kind of Prelude.Int it is *.
     * One important point to note is that the kind of a TypeConstructor will not involve kind variables.  
     * @return KindExpr
     */
    KindExpr getKindExpr() {
        return kindExpr;
    }
    
    /**
     * The number of type variable arguments taken by the fully saturated type constructor.
     * For example,
     * the type arities of Int, String, Char are 0
     * the type arity of List is 1.
     * the type arity of Either is 2.
     *     
     * For example, if the kind is *->*->*, then this is 2.
     * @return int the number of type variable arguments taken by the fully saturated type constructor.
     */
    public int getTypeArity() {
        return kindExpr.getNArguments();
    }
          
    /**    
     * @return ForeignTypeInfo information about the Java type corresponding to this foreign type if
     *      this is a foreign type, or null if not a foreign type.
     */
    public final ForeignTypeInfo getForeignTypeInfo() {
        return foreignTypeInfo;
    } 
               
    /**
     * Returns a map from the built-in type names to their TypeConstructor objects.
     * All built-in types must belong to the Prelude module.
     *    
     * "Cal.Core.Prelude.Function a b" is equivalent to "a -> b"     
     * Type application uses the special syntax of juxtiposition e.g. Maybe Int.
     *  
     * @return Map (QualifiedName->TypeConstructor)
     */
    private static final Map<QualifiedName, TypeConstructor> makeBuiltInTypeConstructors() {
        
        Map<QualifiedName, TypeConstructor> tcs = new LinkedHashMap<QualifiedName, TypeConstructor> ();
                    
        //reason for being built-in: Function is neither an algebraic type, nor a foreign type. It is truly something special.                    
        addBuiltInTypeConstructor(tcs, TypeConstructor.FUNCTION);                            
        
        return tcs;                                
    }
    
    /**
     * Method addBuiltInTypeConstructor.
     * @param tcs
     * @param typeConstructor
     */
    private static final void addBuiltInTypeConstructor(Map<QualifiedName, TypeConstructor> tcs, TypeConstructor typeConstructor){
        
        tcs.put(typeConstructor.getName(), typeConstructor);
    }
    
    /**
     * Adds all the built-in types to the ModuleTypeInfo object.
     * @param moduleTypeInfo the module in which to add the built-in types to. Should be the Prelude module.
     */
    static final void addBuiltInTypes(ModuleTypeInfo moduleTypeInfo) {
        
        if (!moduleTypeInfo.getModuleName().equals(CAL_Prelude.MODULE_NAME)) {
            throw new IllegalArgumentException ("the built-in type constructors belong to the Prelude module.");
        }
                                
        for (final QualifiedName qualifiedName : builtInTypeConstructors.keySet()) {     
                    
            moduleTypeInfo.addTypeConstructor(builtInTypeConstructors.get(qualifiedName));
        }        
    }
    
    /**
     * Note: all built-in types belong to the Prelude module.
     * @param typeName name of the built-in type.
     * @return TypeConstructor entity for the built-in type of null if it does not exist
     */
    static final TypeConstructor getBuiltInType(QualifiedName typeName) {
        return builtInTypeConstructors.get(typeName);
    }
    
    /**
     * Call this method when kind checking for this TypeConstructor is complete and
     * prior to kind checking later dependency groups.
     * The effect is to ground the remaining kind variables by * after kind inference
     * has determined the most general kind of this TypeConstructor. This is because CAL does
     * not support polymorphic kinds.
     */
    void finishedKindChecking() {
        kindExpr = kindExpr.bindKindVariablesToConstant();
    }
                   
    /**    
     * @return textual description of this type constructor entity.
     */
    @Override
    public String toString() {
        return super.toString() + " hasKind " + kindExpr + " foreignInfo " + foreignTypeInfo;
    }
       
    /**
     * Method getDataConstructor.
     * @param dataConstructorName the unqualified data constructor name constructing values of this type
     * @return DataConstructor null if this type does not define a data constructor having the given name  
     */
    public DataConstructor getDataConstructor(String dataConstructorName) {

        return dataConstructorMap.get(dataConstructorName);
    }        
    
    /**
     * Method getNDataConstructors.
     * @return int the number of data constructors defined for this type in its data declaration. Will be 0 for foreign types.
     */
    public int getNDataConstructors() {
        return dataConstructorMap.size();
    }
    
    /**
     * Method getNthDataConstructor.
     * @param n zero based index
     * @return DataConstructor the nth data constructor defined in the data declaration for the type.
     */
    public DataConstructor getNthDataConstructor(int n) {
        
        return dataConstructorMap.getNthValue(n);                   
    } 
    
    /**
     * Method addDataConstructor.
     * @param dataConstructor as defined within a data declaration for this type. Add in order of their declaration.  
     */
    void addDataConstructor(DataConstructor dataConstructor) {

        QualifiedName dataConstructorName = dataConstructor.getName();
       
        if (!getName().getModuleName().equals(dataConstructorName.getModuleName())) {

            throw new IllegalArgumentException("the data constructor added must belong to the same module as the type.");
        }
                
        if(dataConstructorMap.put(dataConstructorName.getUnqualifiedName(), dataConstructor) != null) {
             throw new IllegalArgumentException("The type " + this + " already defines the data constructor " + dataConstructorName.getUnqualifiedName() + ".");
        }        
    }
    
    /**     
     * Note: do not expose as public. Clients do not need to know if an instance declaration is defined via a deriving clause
     * or via a "regular" instance declaration.
     * 
     * @return int the number of type class names in the deriving clause. May be 0.
     */
    int getNDerivedInstances() {        
        return derivingClauseTypeClassInfos.length;
    }
    
    /**     
     * Note: do not expose as public. Clients do not need to know if an instance declaration is defined via a deriving clause
     * or via a "regular" instance declaration.
     * 
     * @param n index where 0 <= n < getNDerivedInstances.
     * @return QualifiedName the type class name.
     */    
    QualifiedName getDerivingClauseTypeClassName(int n) {
        return derivingClauseTypeClassInfos[n].getName();
    }

    /**     
     * Note: do not expose as public. Clients do not need to know if an instance declaration is defined via a deriving clause
     * or via a "regular" instance declaration.
     * 
     * returns the source range for the code that requests the derived instance.
     * 
     * @param n index where 0 <= n < getNDerivedInstances.
     * @return the source range of the declaration
     */    
    SourceRange getDerivingClauseTypeClassPosition(int n) {
        if (derivingClauseTypeClassInfos[n].getSourceRange() == null)
            return null;
        
        return derivingClauseTypeClassInfos[n].getSourceRange();
    }
    
    boolean hasDerivingClause(QualifiedName typeClassName) {
              
        for (int i = 0, nDerivedInstances = getNDerivedInstances(); i < nDerivedInstances; ++i) {
            if (getDerivingClauseTypeClassName(i).equals(typeClassName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Write this instance of TypeConstructor to the RecordOutputStream
     * @param s
     * @throws IOException
     */
    @Override
    void write (RecordOutputStream s) throws IOException {
        if (foreignTypeInfo == null) {
            s.startRecord(ModuleSerializationTags.TYPE_CONSTRUCTOR_ENTITY, serializationSchema);
        } else {
            s.startRecord(ModuleSerializationTags.FOREIGN_TYPE_CONSTRUCTOR_ENTITY, serializationSchema);
        }
        
        super.writeContent(s);
        
        kindExpr.write(s);
        
        if (foreignTypeInfo != null) {
            try {
                foreignTypeInfo.write(s);
            } catch (UnableToResolveForeignEntityException e) {
                // If we are serializing the info, then it must not have been originally deserialized lazily
                final IllegalStateException illegalStateException = new IllegalStateException("Java entities in the ForeignTypeInfo should have been eagerly resolved during the compilation process");
                illegalStateException.initCause(e);
                throw illegalStateException;
            }
        }

        final int nDataConstructors = dataConstructorMap.size();
        s.writeShortCompressed(nDataConstructors);
        for (int i = 0; i < nDataConstructors; ++i) {
            DataConstructor dc = dataConstructorMap.getNthValue(i);
            dc.write(s);
        }
        
        //serialize the field derivingClauseTypeClassNames
        final int nDerivingClauseTypeClassNames = derivingClauseTypeClassInfos.length;        
        s.writeShortCompressed(nDerivingClauseTypeClassNames);
        for (int i = 0; i < nDerivingClauseTypeClassNames; ++i) {
            QualifiedName typeClassName = derivingClauseTypeClassInfos[i].getName();
            s.writeQualifiedName(typeClassName);
        }
        
        s.endRecord();
    }

    /**
     * Load an instance of TypeConstructor from the RecordInputStream
     * This simply creates an instance of the TypeConstructor and 
     * loads the members of the ScopedEntityImpl base class.  The rest of 
     * the member have to be resolved through loadFinal().
     * This is done so that the set of TypeConstructor instances can be 
     * build up before loading the members that can have mutually recursive and out-of-order
     * references. 
     * @param s    
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @return the instance of TypeConstructor with members of the ScopedEntityImpl base class loaded
     * @throws IOException
     */
    static TypeConstructor loadInit (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(TYPE_CONSTRUCTOR_ENTITY_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException ("Unable to find TypeConstructor record.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "TypeConstructor", msgLogger);

        TypeConstructor typeCons = new TypeConstructor();
        try {
            // Note we must do a reassignment of the local variable tce since readInitContent 
            // may return a different TypeConstructor if this is a built-in type.
            typeCons = typeCons.readInitContent(s, rhi.getSchema(), mti, msgLogger);
        } catch (IOException e) {
            QualifiedName qn = typeCons.getName();
            throw new IOException ("Error loading TypeConstructor " + (qn == null ? "" : qn.getQualifiedName()) + ": " + e.getLocalizedMessage());
        }
        
        return typeCons;
    }

    /**
     * Load an instance of TypeConstructor from the RecordInputStream
     * This simply creates an instance of the TypeConstructor and 
     * loads the members of the ScopedEntityImpl base class.  The rest of 
     * the member have to be resolved through finalLoad().
     * @param s     
     * @param mti     
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    void loadFinal (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(TYPE_CONSTRUCTOR_ENTITY_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException ("Unable to find TypeConstructor record.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "TypeConstructor", msgLogger);

        try {
            super.readContent (s, mti, msgLogger);
            
            // QualifiedName
            QualifiedName name = this.getName();
            TypeConstructor builtIn = getBuiltInType(name);
            if (builtIn != null) {
                s.skipRestOfRecord();
                return;
            }
            
            // kindExpr
            kindExpr = KindExpr.load(s, mti.getModuleName(), msgLogger);
            
            // ForeignTypeInfo   
            if (rhi.getRecordTag() == ModuleSerializationTags.FOREIGN_TYPE_CONSTRUCTOR_ENTITY) {
                foreignTypeInfo = ForeignTypeInfo.load(s, mti.getModuleName(), mti.getModule().getForeignClassLoader(), msgLogger);
            }
            
            int nDCs = s.readShortCompressed();
            for (int i = 0; i < nDCs; ++i) {
                DataConstructor dc = DataConstructor.load(s, mti, msgLogger);
                addDataConstructor(dc);
            }
            
            //load the field derivingClauseTypeClassNames
            final int nDerivingClauseTypeClassNames = s.readShortCompressed();   
            if (nDerivingClauseTypeClassNames == 0) {
                derivingClauseTypeClassInfos = NO_DERIVING_CLAUSE;
            } else {
                derivingClauseTypeClassInfos = new DerivingClauseInfo[nDerivingClauseTypeClassNames];
                for (int i = 0; i < nDerivingClauseTypeClassNames; ++i) {                
                    QualifiedName typeClassName = s.readQualifiedName();
                    derivingClauseTypeClassInfos[i] = new DerivingClauseInfo(typeClassName, null);
                } 
            }
            
            s.skipRestOfRecord();
        } catch (IOException e) {
            QualifiedName qn = getName();
            throw new IOException ("Error loading TypeConstructor " + (qn == null ? "" : qn.getQualifiedName()) + ": " + e.getLocalizedMessage());
        }
    }
   
    /**
     * Do an initial read to establish the existence of this TypeConstructor.  The reading of the
     * content has to be split into two stages since you can have a closely connected set of TypeConstructor.
     * @param s - the RecordInputStream
     * @param schema - schema of the record in the stream
     * @param mti - current ModuleTypeInfo
     * @param msgLogger the logger to which to log deserialization messages.
     * @return - 'this' if the type is not a built-in.  Otherwise returns the built-in TypeConstructor.
     * @throws IOException
     */
    private TypeConstructor readInitContent (RecordInputStream s, int schema, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {

        super.readContent (s, mti, msgLogger);
        
        // QualifiedName
        QualifiedName name = this.getName();
        TypeConstructor builtIn = getBuiltInType(name);
        if (builtIn != null) {
            s.skipRestOfRecord();
            return builtIn;
        }
        
        // Now that we've loaded the name of the TypeConstructor we can add it to the ModuleTypeInfo.
        mti.addTypeConstructor(this);
        s.skipRestOfRecord();
        
        return this;
    }
}
