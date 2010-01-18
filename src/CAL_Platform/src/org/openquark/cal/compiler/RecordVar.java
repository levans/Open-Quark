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
 * RecordVar.java
 * Created: Feb 17, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Represents a variable value for a record (i.e. a finite set of Fields, along with their associated types).
 * This variable can have constraints on it so that the RecordType that this RecordVar can be
 * instantiated to are not allowed to include certain field names.
 * 
 * @author Bo Ilic
 */
public class RecordVar implements PolymorphicVar {
        
    private static final int serializationSchema = 0;
    
    private static final int alreadyVisitedRecordVarSerializationSchema = 0;
    
    // Serialization flags
    private static final byte HAS_NON_NULL_INSTANCE = 0x01;
    private static final byte HAS_LACKS_FIELD_CONSTRAINTS = 0x02;
    private static final byte HAS_TYPE_CLASS_CONSTRAINTS = 0x04;
    
    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #load} method.
     */
    private static final short[] RECORD_VAR_RECORD_TAGS = new short[] {
        ModuleSerializationTags.RECORD_VAR,
        ModuleSerializationTags.ALREADY_VISITED_RECORD_VAR
    };
    
    static final Set<FieldName> NO_LACKS_FIELDS = Collections.<FieldName>emptySet();
    
    /** 
     * An instantiated RecordVar (i.e. instance != null) behaves like its instantiation.
     * There are consistency requirements for the instantiation:
     * instance.baseRecordVar.lacksFieldsSet must contain this.lacksFieldsSet
     * instance.hasFieldsMap must not contain any of the fields in this.lacksFieldsSet      
     */         
    private RecordType instance;  
    
    /**
     * The preferred textual name of this RecordVar. null if there is no preferredName. 
     * When a record variable is introduced via a textual declaration, such as with a type declaration, a class instance
     * definition, or a data declaration, then we can preserve the user supplied name, and make use of it whenever
     * possible in displaying String representations of types involving this RecordVar.     
     */
    private String preferredName;    
        
    /**
     * (FieldName Set) fields that this RecordVar is constrained not have. For example, in the type
     * (r\field1, r\field2, r\field3) => {r | field1 :: Int, field2 :: Char}, then for the record variable r,
     * this set is {field1, field2, field3}.
     */
    private Set<FieldName> lacksFieldsSet;
    
    /**
     * (TypeClass SortedSet). an uninstantiated record variable can be constrained to be instantiated to a record type
     * such that the types of all the fields in the record type satisfy all the type class constraints in
     * typeClassConstraintSet. 
     * Note that if a class B inherits from A and B is in the typeClassConstraintSet, then A will not be-
     * the constraint is implicit.
     * If typeClassConstraintSet is the empty set, then the record variable can be instantiated to any recordVariable,
     * subject to the constraints of the lacksFieldsSet.
     */
    private SortedSet<TypeClass> typeClassConstraintSet;         
    
    /**
     * A special RecordVar corresponding to a record variable that is constrained so that lacksFieldsSet includes
     * all fields. In our implementation:
     * {NO_FIELDS | foo :: Int, bar :: Char} is what we mean by {foo :: Int, bar :: Char} 
     */
    static final RecordVar NO_FIELDS = new RecordVar();
    
    /**
     * A special constructor used for constructing the special NO_FIELDS value of RecordVar and for
     * constructing blank RecordVars during deserialization.    
     */
    private RecordVar () { 
        lacksFieldsSet = null;
        typeClassConstraintSet = TypeClass.NO_CLASS_CONSTRAINTS;     
    }
    
    private RecordVar(String preferredName, Set<FieldName> lacksFieldsSet, SortedSet<TypeClass> typeClassConstraintSet) {
        if (lacksFieldsSet == null || typeClassConstraintSet == null) {
            throw new NullPointerException();
        }
        
        if (preferredName != null && !LanguageInfo.isValidTypeVariableName(preferredName)) {
            throw new IllegalArgumentException("invalid preferred name " + preferredName);
        }
        
        this.preferredName = preferredName;
        this.lacksFieldsSet = lacksFieldsSet;
        this.typeClassConstraintSet = typeClassConstraintSet; 
    }
    
    /**     
     * @param preferredName the preferred textual name of this RecordVar. null if there is no preferredName. 
     * @return an unconstrained polymorphic RecordVar that can be further instantiated. Its type is {r}.
     */
    static RecordVar makePolymorphicRecordVar(String preferredName) {
        return new RecordVar(preferredName, NO_LACKS_FIELDS, TypeClass.NO_CLASS_CONSTRAINTS);
    }
    
    /**
     * General factory method for creating a new RecordVar.
     *    
     * @param preferredName the preferred textual name of this RecordVar. null if there is no preferredName. 
     * @param lacksFieldsSet (FieldName Set) fields that this RecordVar is constrained not have.
     * @param typeClassConstraintSet (TypeClass Set) an uninstantiated record variable can be constrained to be
     *      instantiated to a record type such that the types of all the fields in the record type satisfy all the
     *      type class constraints in typeClassConstraintSet. constraints set should initially be
     *      created with TypeClass.makeNewClassConstraintSet().
     * @param validateTypeClassConstraintSet remove redundant superclass constaints, and make the set unmodifiable. This
     *      is not necessary if reusing another TypeVar's or RecordVar's typeClassConstraintSet.
     * @return the new RecordVar. It can be instantiated if needed.
     */   
    static RecordVar makeRecordVar(String preferredName, Set<FieldName> lacksFieldsSet, SortedSet<TypeClass> typeClassConstraintSet, boolean validateTypeClassConstraintSet) {
        
        if (lacksFieldsSet.isEmpty() && typeClassConstraintSet.isEmpty()) {
            return makePolymorphicRecordVar(preferredName);
        }
          
        if (validateTypeClassConstraintSet) {        
            //remove redundant superclass constraints (so that the type class constraint set
            //is in a canonical form.
            TypeClass.removeSuperclassConstraints(typeClassConstraintSet);
        
            return new RecordVar (preferredName, Collections.unmodifiableSet(lacksFieldsSet),
                Collections.unmodifiableSortedSet(typeClassConstraintSet));
        } else {
            return new RecordVar (preferredName, Collections.unmodifiableSet(lacksFieldsSet), typeClassConstraintSet);        
        }
    }

    /**    
     * @return an efficient copy of this uninstantiated record variable.
     */
    RecordVar copyUninstantiatedRecordVar() {
        if (isNoFields()) {
            return this;
        } else {
            //since both lacksFieldsSet and typeClassConstraintSet are final and unmodifiable, can use the same instances.
            return new RecordVar(preferredName, lacksFieldsSet, typeClassConstraintSet);
        }
    }
    
    /**
     * If this RecordVar is instantiated, then adds all the uninstantiated RecordVars contained in its instance to varSet.
     * Otherwise adds itself to varSet.  Don't depend on varSet coming out in any particular order.
     * @param varSet Set
     */
    void getUninstantiatedRecordVars(Set<RecordVar> varSet) {
        if(instance != null) {
            instance.getUninstantiatedRecordVars(varSet);
            return;
        }
        
        // NO_FIELDS doesn't really count as an uninstantiated record var, because
        // it is instantiated as {} (in a sense)
        if(!isNoFields()) {
            varSet.add(this);
        }
    }
    
    /** 
     * If this RecordVar is instantiated (i.e. it is not NO_FIELDS or an uninstantiated record
     * variable), then this function follows the chain of instantiations to get to a RecordVar
     * which is NO_FIELDS or uninstantiated.
     * @return follows the chain of instantiated RecordVars to reach an uninstantiated RecordVar
     *     or NO_FIELDS. The returned RecordVar may be this if this RecordVar is already uninstantiated
     *     or NO_FIELDS.
     */ 
    RecordVar prune() {

        if (instance != null) {                
            return instance.getPrunedRecordVar();
        }

        return this;
    }
    
    boolean isNoFields() {
        return this == NO_FIELDS;    
    }
    
    /** 
     * The returned lacks field set cannot be modified.
     * @return (FieldName Set) this RecordVar cannot be instantiated to anything that contains any of the lacks fields.
     */
    Set<FieldName> getLacksFieldsSet () {
        return lacksFieldsSet;
    }
    
    RecordType getInstance() { 
        return instance;     
    }
        
    void setInstance(RecordType instance) {
        if (this.instance != null || isNoFields()) {
            //can't instantiate an already instantiated RecordVar or modify the special NO_FIELDS recordVar.
            throw new IllegalStateException();    
        }
        
        this.instance = instance;
    }
    
    /**{@inheritDoc} */
    public String getPreferredName() {
        return preferredName;
    }        
        
    /**
     * @return boolean checks whether this record var has an empty type class constraint set.
     */
    boolean noClassConstraints(){
        return typeClassConstraintSet.isEmpty();
    }
    
    /**
     * Represents the type class constraints on this record variable. This affects the allowable instantiations
     * of the record variable.        
     * @return SortedSet of TypeClass ordered alphabetically by fully qualified name.
     *      Will always return a non-null value. Note that this set is unmodifiable.
     */    
    SortedSet<TypeClass> getTypeClassConstraintSet() {       
        return typeClassConstraintSet;
    }              
    
    String toString(PolymorphicVarContext polymorphicVarContext) {
       polymorphicVarContext.addPolymorphicVar(this);
       return polymorphicVarContext.getPolymorphicVarName(this);
    }
    
    @Override
    public String toString() {
        if (isNoFields()) {
            return "NoFields";            
        }
                
        return "lacks=" + String.valueOf(prune().lacksFieldsSet);
    }
    
    /**
     * Write an instance of RecordVar to a RecordOutputStream.
     * @param s
     * @param visitedTypeExpr
     * @throws IOException
     */
    void write (RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException {
        Short key = visitedRecordVar.get(this);
        
        if(key != null) {
            s.startRecord(ModuleSerializationTags.ALREADY_VISITED_RECORD_VAR, alreadyVisitedRecordVarSerializationSchema);
            s.writeShortCompressed(key.shortValue());
            s.endRecord();

        } else {
            key = new Short((short)visitedRecordVar.size());
            visitedRecordVar.put(this, key);
            
            writeActual(s, visitedTypeExpr, visitedRecordVar);
        }
    }
        
    void writeActual(RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException {
        s.startRecord(ModuleSerializationTags.RECORD_VAR, serializationSchema);
        byte flags = 0;
        if (instance != null) {
            flags |= HAS_NON_NULL_INSTANCE;
        }
        if (lacksFieldsSet != null) {
            flags |= HAS_LACKS_FIELD_CONSTRAINTS;
        }
        if (!typeClassConstraintSet.isEmpty()) {
            flags |= HAS_TYPE_CLASS_CONSTRAINTS;
        } 
        s.writeByte(flags);
        
        if (instance != null) {
            instance.write(s, visitedTypeExpr, visitedRecordVar);
        }
        
        if (lacksFieldsSet != null) {
            s.writeShortCompressed(lacksFieldsSet.size());            
            for (final FieldName fn : lacksFieldsSet) {
                FieldNameIO.writeFieldName(fn, s);
            }
        }
        
        if (!typeClassConstraintSet.isEmpty()) {
            s.writeShortCompressed(typeClassConstraintSet.size());        
            for (final TypeClass tc : typeClassConstraintSet) {
                s.writeQualifiedName(tc.getName());
            }
        }
        
        s.writeUTF(preferredName);
        
        s.endRecord();
    }

    /**
     * Load an instance of RecordVar from a RecordInputStream.
     * @param s
     * @param mti
     * @param visitedTypeExpr
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of RecordVar.
     */
    static RecordVar load (RecordInputStream s, ModuleTypeInfo mti, Map<Short, TypeExpr> visitedTypeExpr, Map<Short, RecordVar> visitedRecordVar, CompilerMessageLogger msgLogger) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(RECORD_VAR_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException("Unable to find RecordVar record header.");
        }
        
        switch(rhi.getRecordTag()) {
            
            case ModuleSerializationTags.ALREADY_VISITED_RECORD_VAR:
            {
                DeserializationHelper.checkSerializationSchema(rhi.getSchema(), alreadyVisitedRecordVarSerializationSchema, mti.getModuleName(), "RecordVar", msgLogger);

                // Read the key and do the lookup.
                short id = s.readShortCompressed();
                RecordVar recordVar = visitedRecordVar.get(new Short(id));
                if (recordVar == null) {
                    throw new IOException ("Unable to resolve previously encountered RecordVar instance in RecordVar.");
                }
                s.skipRestOfRecord();
                return recordVar;
            }
        
            case ModuleSerializationTags.RECORD_VAR:
            {
                DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "RecordVar", msgLogger);
        
                byte flags = s.readByte();
                
                // Short-circuit for the special NO_FIELDS case
                if((flags & (HAS_LACKS_FIELD_CONSTRAINTS | HAS_TYPE_CLASS_CONSTRAINTS)) == 0) {
                    s.skipRestOfRecord();
                    visitedRecordVar.put(new Short((short)visitedRecordVar.size()), NO_FIELDS);
                    return NO_FIELDS;
                }
                
                RecordVar rv = new RecordVar();
                visitedRecordVar.put(new Short((short)visitedRecordVar.size()), rv);
                
                RecordType instance = null;
                if ((flags & HAS_NON_NULL_INSTANCE) > 0) {
                    instance = (RecordType)TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
                }
                
                Set<FieldName> lacksFieldSet = null;
                if ((flags & HAS_LACKS_FIELD_CONSTRAINTS) > 0) {
                    int nLacksFields = s.readShortCompressed();
                    if (nLacksFields > 0) {
                        lacksFieldSet = new HashSet<FieldName>();
                        for (int i = 0; i < nLacksFields; ++i) {
                            FieldName fn = FieldNameIO.load(s, mti.getModuleName(), msgLogger);
                            lacksFieldSet.add(fn);
                        }
                    } else {
                        lacksFieldSet = NO_LACKS_FIELDS;
                    }
                }
                
                SortedSet<TypeClass> typeClassConstraintSet = TypeClass.NO_CLASS_CONSTRAINTS;
                if((flags & HAS_TYPE_CLASS_CONSTRAINTS) > 0) {
                    int nTC = s.readShortCompressed();
                    if (nTC > 0) {
                        typeClassConstraintSet = TypeClass.makeNewClassConstraintSet();
                        for (int i = 0; i < nTC; ++i) {
                            QualifiedName qn = s.readQualifiedName();
                            TypeClass tc = mti.getReachableTypeClass(qn);
                            // This could be a type from a non-direct dependee module.
                            if (tc == null) {
                                IOException ioe = new IOException("Unable to resolve TypeClass " + qn + " while loading RecordVar."); 
                                throw ioe;
                            }
                            typeClassConstraintSet.add(tc);
                        }
                    }
                }
                
                rv.lacksFieldsSet = lacksFieldSet;
                rv.typeClassConstraintSet = typeClassConstraintSet;
                
                if (instance != null) {
                    rv.setInstance(instance);
                }
                
                rv.preferredName = s.readUTF();                                                             
                
                s.skipRestOfRecord();
                
                return rv;
            }
            
            default:
            {
                throw new IOException("Unexpected record tag " + rhi.getRecordTag() + " found when looking for RecordVar.");
            }
        }
        
    }
}
