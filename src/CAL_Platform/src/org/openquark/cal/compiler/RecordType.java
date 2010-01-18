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
 * RecordType.java
 * Created: Feb 17, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;


/**
 * A class used to represent record types such as:
 * literal (non record-polymorphic) records:
 * <ul>
 *    <li> {name :: String, height :: Double}
 *    <li> Num a =>  {foo :: [a], bar :: Char, zap :: Maybe a}
 * </ul>
 * record-polymorphic records:
 * <ul>
 *    <li> r\field => {r | field1 :: Char}
 *    <li> (r\name, r\height) => {r | name :: String, height :: Double}
 *  </ul>
 * <p>
 * Implementation note:
 * RecordType objects can be though of as defining a finite sequence of extensions from a base record variable:
 * the non-record polymorphic records are: {NO_FIELDS | extensionFieldsMap1 | extensionFieldsMap2 | ... | extensionFieldsMapN}
 * the record-polymorphic records are: r\lacksFields => {r | extensionFieldsMap1 | extensionFieldsMap2 | ... | extensionFieldsMapN}
 * Where the extensionFieldsMaps are restricted by construction to having distinct domains (i.e. the fields don't overlap) and there
 * are similar consistency constraints on the record variable r.
 * <p>
 * After type-checking however, we can reduce these representations to the simpler cannonical forms:
 * {NO_FIELDS | hasFieldsMap}
 * r\lacksFields => {r | hasFieldsMap}
 * <p>
 * This is similar to how TypeExpr values after type-checking are reduced to having no instantiated type variables (deep pruning). 
 * 
 * @author Bo Ilic
 */
public final class RecordType extends TypeExpr {

    private static final int serializationSchema = 0;
    
    /** 
     * Corresponds to what is on the left hand side of the topmost extension operator (|) in a record type.  
     * If the record type is e.g. (r\field1, r\field2, r\field3, r\foo) => {r | field1 :: Int, field2 :: Char, field3 :: Boolean}
     * then recordVar corresponds to the "r" part.  
     * If the record type is e.g. {{field1 :: Int, field2 :: Char} | field3 :: Boolean}, then this corresponds to a RecordVar
     * which is instantiated to {field1 :: Int, field2 :: Char}.
     */
    private RecordVar baseRecordVar;
        
    /**
     * (FieldName -> TypeExpr) the baseRecordVar is extended with additional fields, and this map holds onto the fields
     * along with their associated types. By construction, the fields in the extension cannot belong to the base.
     *
     * For example, for the record
     * (r\field1, r\field2, r\field3, r\foo) => {r | field1 :: Int, field2 :: Char, field3 :: Boolean}
     * this is the map: [(field1, Int), (field2, Char), (field3, Boolean)].   
     *  
     * For the record
     * {{field1 :: Int, field2 :: Char} | field3 :: Boolean}, this is the map: [(field3, Boolean)]. 
     *
     * For the record:
     * (r\field1, r\field2) -> { {r | field1 :: Int} | field2 :: Char} this is the map [(field2, Char)].      
     */   
    private Map<FieldName, TypeExpr> extensionFieldsMap;
    
    /**  
     * Note that all the fields in the extensionFieldsMap must be
     * lacks constraints in the pruned recordVar if this is a polymorphic
     * record extension (although the recordVar can have more lacks constraints).
     * 
     * @param recordVar RecordVar
     * @param extensionFieldsMap Map (FieldName -> TypeExpr)
     */
    RecordType (RecordVar recordVar, Map<FieldName, TypeExpr> extensionFieldsMap) {
        if (recordVar == null || extensionFieldsMap == null) {
            throw new NullPointerException();
        }
        
        //a sanity check- it is a programming error if this exception is thrown.
        RecordVar prunedRecordVar = recordVar.prune();
        if (!prunedRecordVar.isNoFields() &&
            !prunedRecordVar.getLacksFieldsSet().containsAll(extensionFieldsMap.keySet())) {
            
            //Can't have extensions such as {r | field1 :: Int}. r must have a field1 lacks constraint.               
            Set<FieldName> missingLacksConstraints = new HashSet<FieldName>(extensionFieldsMap.keySet());
            missingLacksConstraints.removeAll(prunedRecordVar.getLacksFieldsSet());
            throw new IllegalArgumentException("The extension fields " + missingLacksConstraints +
                " must be lacks constraints on the record variable." );            
        }        
        
        this.baseRecordVar = recordVar;
        this.extensionFieldsMap = extensionFieldsMap;
    }
    
    // private constructor used by serialization code.
    private RecordType () {
    }
    
    /**     
     * @return The base record type, or null if the baseRecordVar is NO_FIELDS or an uninstantiated record var.
     */   
    RecordType getBaseRecordType() {        
        return baseRecordVar.getInstance();        
    }
    
    
    /** 
     * If baseRecordVar is instantiated (i.e. it is not NO_FIELDS or an uninstantiated record
     * variable), then this function follows the chain of instantiations to get to a RecordVar
     * which is NO_FIELDS or uninstantiated.
     * 
     * @return follows the chain of instantiated RecordVars to reach an uninstantiated RecordVar
     *     or NO_FIELDS. The returned RecordVar may be this.baseRecordVar if this.baseRecordVar
     *     is already uninstantiated or NO_FIELDS.
     */    
    RecordVar getPrunedRecordVar() {
        return baseRecordVar.prune();
    }
    
    /**    
     * @return (FieldName -> TypeExpr) an unmodifiable version of extensionFieldsMap
     */
    Map<FieldName, TypeExpr> getExtensionFieldsMap() {
        return Collections.unmodifiableMap(extensionFieldsMap);
    }
    
    /** 
     * Collects the full mapping of fields to type expressions specified by this record. In other words, the domain of this
     * map is the fields that this record is asserted to have. The returned map is a copy, and can be freely modified
     * by callers.
     * @return SortedMap 
     */
    public SortedMap<FieldName, TypeExpr> getHasFieldsMap() {
        
        SortedMap<FieldName, TypeExpr> hasFieldsMap = new TreeMap<FieldName, TypeExpr>(extensionFieldsMap);
                
        for (RecordType baseRecordType = getBaseRecordType();
             baseRecordType != null;
             baseRecordType = baseRecordType.getBaseRecordType()) {
            hasFieldsMap.putAll(baseRecordType.extensionFieldsMap);
        }
        
        return hasFieldsMap;
    }
    
    /**
     * The fields that this record is asserted to have. The returned Set is a copy, and can be
     * freely modified by callers.
     * @return SortedSet (of FieldName objects)
     */
    SortedSet<FieldName> getHasFields() {
        SortedSet<FieldName> hasFieldsSet = new TreeSet<FieldName>(extensionFieldsMap.keySet());
                
        for (RecordType baseRecordType = getBaseRecordType();
             baseRecordType != null;
             baseRecordType = baseRecordType.getBaseRecordType()) {
            hasFieldsSet.addAll(baseRecordType.extensionFieldsMap.keySet());
        }
        
        return hasFieldsSet;        
    }
    
    /**
     * Returns the field names that this record is asserted to have, ordered by the ordering on FieldName
     * (which is ordinal field names first, in numerical order, followed by textual field names in
     * alphabetic order).
     * 
     * For example, for the record type
     * r\name => {title :: String, author :: {r | name :: [Char]}, #5 :: Int}, 
     * this returns the list ["#5", "author", "title"].
     * 'name' is not part of this list because it is not part of the outermost record type.
     *  
     * @return List (FieldName) of has field names, ordered in ascending FieldName order.
     *    This is a copy, and can be freely modified by the caller.
     */
    public List<FieldName> getHasFieldNames() {
        
        //todoBI why was this function added? The list does not have duplicates so why not just return a SortedSet?        
        return new ArrayList<FieldName>(getHasFields());
    } 
    
    /**
     * Returns the number of field that this record type is asserted to have.
     * 
     * For example, for the record type
     * (r\name) => {title :: String, author :: {r | name :: [Char]}},
     * this returns 2, counting 'title' and 'author', but not 'name'.
     *  
     * @return the number of fields that this RecordType is asserted to have.
     */
    public int getNHasFields() {
        
        int nHasFields = extensionFieldsMap.size();
        
        for (RecordType baseRecordType = getBaseRecordType();
             baseRecordType != null;
             baseRecordType = baseRecordType.getBaseRecordType()) {
                        
            nHasFields += baseRecordType.extensionFieldsMap.size();
        }
        
        return nHasFields;
    }
    
   /**
     * Get the type expression for a has field of this record
     * 
     * Ex: for the record type
     * (r\name) => {title :: String, author :: {r | name :: [Char]}},
     * the field "title" has type String, but field "name" is not a has field of this record
     * and will return null.
     * 
     * @param fieldName
     * @return TypeExpr type expression of the specified field, or null if the field is not a has field.
     */
    public TypeExpr getHasFieldType(FieldName fieldName) {
              
        if (fieldName == null) {
            throw new NullPointerException();
        }
        
        TypeExpr fieldType = extensionFieldsMap.get(fieldName);
        if (fieldType != null) {
            return fieldType;
        }
        
        for (RecordType baseRecordType = getBaseRecordType();
             baseRecordType != null;
             baseRecordType = baseRecordType.getBaseRecordType()) {
                        
            fieldType = baseRecordType.extensionFieldsMap.get(fieldName);
            if (fieldType != null) {
                return fieldType;
            }
        }
        
        return null;               
    }    
                   
    /** {@inheritDoc} */
    @Override
    public boolean containsTypeExpr(TypeExpr searchTypeExpr) {
        
        if (this == searchTypeExpr) {
            return true;
        }
        
        RecordType baseRecordType = getBaseRecordType();
        if (baseRecordType != null) {
            if (baseRecordType.containsTypeExpr(searchTypeExpr)) {
                return true;
            }
        }
        
        for (final Map.Entry<FieldName, TypeExpr> entry : extensionFieldsMap.entrySet()) {
        
            TypeExpr fieldTypeExpr = entry.getValue();
            if (fieldTypeExpr.containsTypeExpr(searchTypeExpr)) {
                return true;         
            }
        }
               
        return false;               
    }
    
    /** {@inheritDoc} */    
    @Override
    boolean containsRecordVar(RecordVar searchRecordVar) {

        if (this.baseRecordVar == searchRecordVar) {
            return true;
        }

        RecordType baseRecordType = getBaseRecordType();
        if (baseRecordType != null) {
            if (baseRecordType.containsRecordVar(searchRecordVar)) {
                return true;
            }
        }

        for (final Map.Entry<FieldName, TypeExpr> entry : extensionFieldsMap.entrySet()) {
            
            TypeExpr fieldTypeExpr = entry.getValue();
            if (fieldTypeExpr.containsRecordVar(searchRecordVar)) {
                return true;
            }
        }

        return false;
    }   

    /** {@inheritDoc} */
    @Override
    public boolean isPolymorphic() {

        RecordType baseRecordType = getBaseRecordType();
        if (baseRecordType != null) {
            if (baseRecordType.isPolymorphic()) {
                return true;
            }
        } else {
            if (!baseRecordVar.isNoFields()) {
                //an uninstantiated polymorphic row variable
                return true;
            }
        }

        for (final Map.Entry<FieldName, TypeExpr> entry : extensionFieldsMap.entrySet()) {
            
            TypeExpr fieldTypeExpr = entry.getValue();
            if (fieldTypeExpr.isPolymorphic()) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    void getGenericClassConstrainedPolymorphicVars(Set<PolymorphicVar> varSet, NonGenericVars nonGenericVars) {
                
        RecordVar recordVar = this.getPrunedRecordVar();
        if (!recordVar.isNoFields()) {
            //a record-polymorphic type
            
            if (!recordVar.noClassConstraints() &&
                (nonGenericVars == null || nonGenericVars.isGenericRecordVar(recordVar))) {
                
                varSet.add(baseRecordVar);               
            }
        }
        
        //important!!!
        //need to sort by field name so that varSet has a definite order (which is required by the contract of the function).
        SortedMap<FieldName, TypeExpr> hasFieldsMap = getHasFieldsMap();   
        
        for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
        
            TypeExpr fieldTypeExpr = entry.getValue();
            fieldTypeExpr.getGenericClassConstrainedPolymorphicVars(varSet, nonGenericVars);
        }        
    }

    /** {@inheritDoc} */
    @Override
    void getUninstantiatedTypeVars(Set<TypeVar> varSet) {

        RecordType baseRecordType = getBaseRecordType();
        if (baseRecordType != null) {
            baseRecordType.getUninstantiatedTypeVars(varSet);
        }

        for (final Map.Entry<FieldName, TypeExpr> entry : extensionFieldsMap.entrySet()) {
                 
            TypeExpr fieldTypeExpr = entry.getValue();
            fieldTypeExpr.getUninstantiatedTypeVars(varSet);
        }
    }

    /** {@inheritDoc} */
    @Override
    void getUninstantiatedRecordVars(Set<RecordVar> varSet) {
        if(baseRecordVar != null) {
            baseRecordVar.getUninstantiatedRecordVars(varSet);
        }
        
        RecordType baseRecordType = getBaseRecordType();
        if(baseRecordType != null) {
            baseRecordType.getUninstantiatedRecordVars(varSet);
        }
        
        for(final TypeExpr fieldTypeExpr : extensionFieldsMap.values()) {            
            fieldTypeExpr.getUninstantiatedRecordVars(varSet);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public TypeExpr getCorrespondingTypeExpr(TypeExpr correspondingSuperType, TypeExpr typeToFind) {       
        
        if (this == typeToFind) {
            return correspondingSuperType;
        }
        
        RecordType correspondingRecordType = correspondingSuperType.rootRecordType();
        if (correspondingRecordType == null) {
            return null;
        }
        
        Map<FieldName, TypeExpr> hasFieldsMap = getHasFieldsMap();
        Map<FieldName, TypeExpr> correspondingHasFieldsMap = correspondingRecordType.getHasFieldsMap();

        for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
               
            FieldName fieldName = entry.getKey();
            TypeExpr fieldType = entry.getValue();            
            TypeExpr correspondingFieldType = correspondingHasFieldsMap.get(fieldName);            

            //if the field does not exist in the super type record skip it -
            //provided the super type record is polymorphic it need not have all the fields of the specialized type
            if (correspondingFieldType == null) {
                continue;
            }
            
            TypeExpr correspondingType = fieldType.getCorrespondingTypeExpr(correspondingFieldType, typeToFind);
            if (correspondingType != null) {
                return correspondingType;
            }                               
        }
        
        return null;        
    }

    /** {@inheritDoc} */
    @Override
    public int getArity() {        
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    void patternMatch(TypeExpr anotherTypeExpr, PatternMatchContext patternMatchContext) throws TypeException {
               
        if (anotherTypeExpr instanceof TypeVar) {
            TypeVar anotherTypeVar = (TypeVar)anotherTypeExpr;
            
            //anotherTypeVar must be uninstantiated.
            if (anotherTypeVar.getInstance()!= null) {
                throw new IllegalArgumentException("RecordType.patternMatch: programming error.");
            }
            
            //Can't instantiate a type variable from the declared type expression.
            //For example, this error occurs with the following declaration.
            //funnyFelix :: r\felix => {r | felix :: a} -> {r | };
            //public funnyFelix r = r.felix;
            if (patternMatchContext.isUninstantiableTypeVar(anotherTypeVar)) {
                throw new TypeException("Attempt to instantiate a type variable from the declared type.");
            }
            
            //todoBI update to an example involving record types. The reasoning is still the same...
            //can't pattern match a nongeneric type variable to a type constructor involving uninstantiated type variables.
            //For example, for the function:
            //g x = let y :: [a]; y = x; in y;
            //we attempt to instantiate a non-generic type variables (corresponding to x) to [a].
            //It is OK though to instantiate to a concrete type e.g.
            //g x = let y :: [Int]; y = x; in y; 
            NonGenericVars nonGenericVars = patternMatchContext.getNonGenericVars();                 
            if (nonGenericVars != null &&
                !nonGenericVars.isGenericTypeVar(anotherTypeVar) &&
                !this.getUninstantiatedTypeVars().isEmpty()) {
                    
                throw new TypeException("Attempt to match a non-generic type variable to a type involving type variables.");
            }            

            // check for infinite types e.g. an attempt to pattern match a to (a->Int).
            if (containsUninstantiatedTypeVar(anotherTypeVar)) {
                throw new TypeException("Type clash: attempt to create an infinite type.");
            }

            if (!anotherTypeVar.noClassConstraints()) {

                //check that there is a record instance for each of the class constraints on anotherTypeVar
                for (final TypeClass typeClass : anotherTypeVar.getTypeClassConstraintSet()) {
                    
                    ClassInstance classInstance = patternMatchContext.getModuleTypeInfo().getVisibleClassInstance(new ClassInstanceIdentifier.UniversalRecordInstance(typeClass.getName()));      
                    if (classInstance == null) {
                        throw new TypeException("Type clash: the record type " + this +" not a member of the type class " + typeClass.getName() + ".");
                    }
                }

                //create an intermediate record type whose recordVar and extension fields map all have
                //the type class constraint set of anotherTypeVar. This is because instances of records all have the
                //simple form "instance C a => C {a}" .
                
                //For example, to pattern match
                //{field1 :: Char, field2 :: Boolean}
                //and
                //(Eq a, Outputable a) => a
                //we create the intermediate record
                //(Eq r, Outputable r, r\field1, r\field2, Eq b, Outputable b, Eq c, Outputable c) =>  {r | field1 :: b, field2 :: c}
                //and then pattern match
                //{field1 :: Char, field2 :: Boolean}
                //and the intermediate record.    
                //(In reality, we short circuit creation of a polymorphic intermediate record if possible).          
                                                            
                Map<FieldName, TypeExpr> constrainedExtensionFieldsMap = new HashMap<FieldName, TypeExpr>();
                SortedSet<TypeClass> anotherTypeClassConstraintSet = anotherTypeVar.getTypeClassConstraintSet();
                Set<FieldName> hasFieldsSet = this.getHasFieldsMap().keySet();

                for (final FieldName fieldName : hasFieldsSet) {
                    
                    constrainedExtensionFieldsMap.put(fieldName, TypeVar.makeTypeVar(null, anotherTypeClassConstraintSet, false));
                }

                RecordVar constrainedRecordVar;
                RecordVar recordVar = this.getPrunedRecordVar();
                if (recordVar.isNoFields()) {
                    constrainedRecordVar = RecordVar.NO_FIELDS;
                } else {
                    constrainedRecordVar = RecordVar.makeRecordVar(null, hasFieldsSet, anotherTypeClassConstraintSet, false);
                }

                RecordType constrainedRecordType = new RecordType(constrainedRecordVar, constrainedExtensionFieldsMap);

                anotherTypeVar.setInstance(constrainedRecordType);
                
                patternMatch(constrainedRecordType, patternMatchContext);
                return;
            }

            anotherTypeVar.setInstance(this);
            return;  
                                                                       
        } else if (anotherTypeExpr instanceof TypeConsApp || anotherTypeExpr instanceof TypeApp) {  
                      
            throw new TypeException("Type clash: The type declaration " + toString() + " does not match the type " + anotherTypeExpr + ".");
            
        } else if (anotherTypeExpr instanceof RecordType) { 
                                   
            RecordType anotherRecordType = (RecordType)anotherTypeExpr;
            
            RecordVar recordVar = getPrunedRecordVar();            
            RecordVar anotherRecordVar = anotherRecordType.getPrunedRecordVar();                      
            
            if (recordVar.isNoFields()) {
                if (anotherRecordVar.isNoFields()) {
                    //2 literal (non-polymorphic) records.
                    //the fields must match exactly
                                        
                    Set<FieldName> hasFieldsSet = this.getHasFieldsMap().keySet();
                    Set<FieldName> anotherHasFieldsSet = anotherRecordType.getHasFieldsMap().keySet();
                    
                    if (!hasFieldsSet.equals(anotherHasFieldsSet)) { 
                                                                                                              
                        throw new TypeException("the fields of the two record types " + this + " and " + anotherRecordType + " must match exactly.");
                    }
                    
                    pairwisePatternMatch(hasFieldsSet, anotherRecordType, patternMatchContext);
                    
                    return;
                }
                
                
                //this RecordType is not polymorphic, anotherRecordType is. If they pattern match, then anotherRecordType will
                //be non-polymorphic with the same fields as this RecordType.
                
                //Can't instantiate a record variable from the declared type expression.
                //For example, this error occurs with the following declaration.
                //abcFunction :: (r\abc) => {r | } -> {abc :: Prelude.String};
                //abcFunction x = {x | abc = "abc"};
                if (patternMatchContext.isUninstantiableRecordVar(anotherRecordVar)) {
                    throw new TypeException("Attempt to instantiate a record variable from the declared type.");
                }
                
                //the hasFields of this RecordType must contain all the hasFields of anotherRecordType.               
                containsHasFieldsOf(anotherRecordType);
                
                //the fields in the hasFields of this RecordType that are not in the hasFields of anotherRecordType
                //must not be in the lacks fields of anotherRecordType's recordVar.                    
                //for example, {foo :: Char, bar :: Int} and (r\foo, r\bar) => {r | bar :: Int} can't pattern match 
                //because to do so, we must set r = foo, but this is disallowed by the lacks constraint on r.
                checkHasLacksCompatibility(anotherRecordType);
                
                //the hasFields in common must pairwise pattern match
                Set<FieldName> anotherHasFieldsSet = anotherRecordType.getHasFieldsMap().keySet();
                pairwisePatternMatch(anotherHasFieldsSet, anotherRecordType, patternMatchContext);
                
                //add the missing (fieldName, type) bindings to anotherRecordType
                Map<FieldName, TypeExpr> neededHasFieldsForAnotherRecordType = getNeededHasFields(anotherRecordType);
                
                //the needed has fields must satisfy the type class constraints of anotherRecordVar
                //for example, this check will prevent the pattern match of
                //{field1 :: Double -> Double}
                //and
                //Eq a => a
                //since the function type is not in Eq.
                patternMatchRecordVarConstraints(anotherRecordVar, neededHasFieldsForAnotherRecordType, patternMatchContext);
                
                //bind the recordVar for anotherRecordType to NO_FIELDS to make it non-polymorphic.
                anotherRecordVar.setInstance(new RecordType(RecordVar.NO_FIELDS, neededHasFieldsForAnotherRecordType));                  
               
                return;
           
                
            } else {
                
                if (anotherRecordVar.isNoFields()) {
                    //this RecordType is polymorphic, anotherRecordType is not. anotherRecordType cannot specialize to
                    //this RecordType.

                    throw new TypeException("Type clash: The type " + anotherRecordType + " cannot specialize to " + this +".");
                }

                //both this RecordType and anotherRecordType are polymorphic. The pattern match, if possible, will be polymorphic.                 

                //the hasFields of this recordType must contain all the hasFields of anotherRecordType.
                containsHasFieldsOf(anotherRecordType);               

                //the fields in the hasFields of this RecordType that are not in the hasFields of anotherRecordType
                //must not be in the lacks fields of anotherRecordType's recordVar.                    
                //for example, {foo :: Char, bar :: Int} and (r\foo, r\bar) => {r | bar :: Int} can't pattern match 
                //because to do so, we must set r = foo, but this is disallowed by the lacks constraint on r.
                checkHasLacksCompatibility(anotherRecordType);

                //the lacks constraints on anotherRecordType.recordVar must be contained in the lacks constraints on this RecordTypes's recordVar.
               
                Set<FieldName> lacksFieldsSet = recordVar.getLacksFieldsSet();
                Set<FieldName> anotherLacksFieldsSet = anotherRecordVar.getLacksFieldsSet();
                if (!lacksFieldsSet.containsAll(anotherLacksFieldsSet)) {

                    SortedSet<FieldName> missingFieldsSet = new TreeSet<FieldName>(anotherLacksFieldsSet);
                    missingFieldsSet.removeAll(lacksFieldsSet);
                    throw new TypeException("the record type " + anotherRecordType + " has lacks fields " + missingFieldsSet + " not included in the record type " + this +".");
                }

                //the hasFields in common must pairwise pattern match
                Set<FieldName> anotherHasFieldsSet = anotherRecordType.getHasFieldsMap().keySet();
                pairwisePatternMatch(anotherHasFieldsSet, anotherRecordType, patternMatchContext);

                //now we know that things pattern match. Must patch up anotherRecordType.

                //the has fields of both records will be the same.               
                Map<FieldName, TypeExpr> neededHasFieldsForAnotherRecordType = getNeededHasFields(anotherRecordType);

                //the recordVar will be the same, in fact the very same instance... 
                
                if (recordVar == anotherRecordVar) {
                    if (neededHasFieldsForAnotherRecordType.isEmpty()) {
                        //nothing needs to be done
                        return;                
                    } else {
                        //we want to set: anotherRecordVar.setInstance(new RecordType(recordVar, neededHasFieldsForAnotherRecordType));
                        //however, this will create a circular instantiation
                        //todoBI probably more checks needed to avoid an infinite record typoe.
                        throw new TypeException("Attempt to instantiate a record variable to itself");
                    }
                }
                
                if (patternMatchContext.isUninstantiableRecordVar(anotherRecordVar)) {
                     throw new TypeException("Attempt to instantiate a record variable from the declared type.");
                }
                            
                //can't pattern match a nongeneric record variable to a record-polymorphic record type.
                //For example, for the function:                               
                //gb s =    
                //    let
                //        y :: (r\#1, r\#2) => {r | #1 :: Prelude.Double, #2 :: Prelude.Char};                
                //        y = {s | #1 = 10.0, #2 = 'y'};
                //    in 
                //        y;  
                //we should get a compile-time error at this point.
                //Intuitively, the problem is that the type signature for y declares r to be record-polymorphic
                //whereas in fact r is a non-generic record variable depending on the type of s and is not
                //free to be specialized independently of s.                            
                NonGenericVars nonGenericVars = patternMatchContext.getNonGenericVars();                 
                if (nonGenericVars != null &&
                    !nonGenericVars.isGenericRecordVar(anotherRecordVar)) {

                    throw new TypeException("Attempt to specialize a non-generic record variable to a record-polymorphic type.");
                }                 
                
                //the needed has fields must satisfy the type class constraints of anotherRecordVar    
                //for example, this check will prevent the pattern match of
                //Eq r => {r | field1 :: Double -> Double}
                //and
                //Eq a => a
                //since the function type is not in Eq.                           
                patternMatchRecordVarConstraints(anotherRecordVar, neededHasFieldsForAnotherRecordType, patternMatchContext);
                
                //the class constraints on this RecordVar must be a superset of those on anotherRecordVar.
                //this prevents pattern matching things like:
                //Eq r => {r}
                //and
                //(Eq s, Outputable s) => {s}
                //(although the other was around would work fine).
                TypeVar.makeTypeVar(null, recordVar.getTypeClassConstraintSet(), false).patternMatch(TypeVar.makeTypeVar(null, anotherRecordVar.getTypeClassConstraintSet(), false), patternMatchContext);                  
                   
                anotherRecordVar.setInstance(new RecordType(recordVar, neededHasFieldsForAnotherRecordType));                

                return;       
            }
                 
        }
        
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    TypeExpr prune() {       
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    TypeExpr deepPrune() {
        
        RecordVar prunedRecordVar = getPrunedRecordVar();
        if (this.baseRecordVar != prunedRecordVar) {
            //after deep pruning, baseRecordVar is either NO_FIELDS or uninstantiated. Also, the hasFields are the same as the
            //extension fields            
            extensionFieldsMap.putAll(baseRecordVar.getInstance().getHasFieldsMap());
            baseRecordVar = prunedRecordVar;
        }

        if (!extensionFieldsMap.isEmpty()) {

            Map<FieldName, TypeExpr> updatedExtensionFields = new HashMap<FieldName, TypeExpr>();

            for (final Map.Entry<FieldName, TypeExpr> entry : extensionFieldsMap.entrySet()) {
                
                FieldName extensionFieldName = entry.getKey();
                TypeExpr fieldTypeExpr = entry.getValue();
                TypeExpr deepPrunedFieldTypeExpr = fieldTypeExpr.deepPrune();

                if (fieldTypeExpr != deepPrunedFieldTypeExpr) {
                    updatedExtensionFields.put(extensionFieldName, deepPrunedFieldTypeExpr);
                }
            }

            extensionFieldsMap.putAll(updatedExtensionFields);
        }

        return this;
    }
    

    /** {@inheritDoc} */
    @Override
    TypeExpr normalize() {
                
        RecordVar normalizedRecordVar = this.getPrunedRecordVar();
        Map<FieldName, TypeExpr> normalizedExtensionFieldsMap = new HashMap<FieldName, TypeExpr>(getHasFieldsMap()); //FieldName -> TypeExpr            
             
        for (final Map.Entry<FieldName, TypeExpr> entry : normalizedExtensionFieldsMap.entrySet()) {
           
            FieldName extensionFieldName = entry.getKey();
            TypeExpr fieldTypeExpr = entry.getValue();
            TypeExpr normalizedFieldTypeExpr = fieldTypeExpr.normalize();

            if (fieldTypeExpr != normalizedFieldTypeExpr) {
                normalizedExtensionFieldsMap.put(extensionFieldName, normalizedFieldTypeExpr);
            }
        }
        
        return new RecordType(normalizedRecordVar, normalizedExtensionFieldsMap);
    }
    
    boolean isEmptyRecord() {
        return getNHasFields() == 0 && !isRecordPolymorphic();
    }
    
    @Override
    public boolean sameType(TypeExpr anotherTypeExpr) {
                 
        anotherTypeExpr = anotherTypeExpr.prune();
        
        if (anotherTypeExpr instanceof RecordType) {                                      
            return this.toString().equals(anotherTypeExpr.toString());                                      
        }
        
        if (anotherTypeExpr instanceof TypeConsApp ||
            anotherTypeExpr instanceof TypeVar ||
            anotherTypeExpr instanceof RecordType) {
                       
            return false;
        }
        
        throw new IllegalStateException();
    }        
    
    /** {@inheritDoc} */
    @Override
    void toSourceText(StringBuilder sb, PolymorphicVarContext polymorphicVarContext, ParenthesizationInfo parenthesizationInfo, ScopedEntityNamingPolicy namingPolicy) {
        
        RecordVar recordVar = this.getPrunedRecordVar();
        
        //need to sort by field name so that the toString has a definite order.
        SortedMap<FieldName, TypeExpr> hasFieldsMap = getHasFieldsMap();
                                        
        final int nFields = hasFieldsMap.size(); 
        
        //if the record type represents a tuple (the fields are #1, #2, ..., #n with no gaps, and n >= 2), then display the type
        //using tuple notation.
        if (recordVar.isNoFields() && nFields >= 2) { 
            
            FieldName lastFieldName = hasFieldsMap.lastKey();            
            if (lastFieldName instanceof FieldName.Ordinal) {
                
                FieldName.Ordinal lastOrdinalFieldName = (FieldName.Ordinal)lastFieldName;
                if (lastOrdinalFieldName.getOrdinal() == nFields) {
                    
                    sb.append('(');
                    int fieldN = 0;
                    for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                                                                  
                        TypeExpr fieldTypeExpr = entry.getValue();  
                        
                        fieldTypeExpr.toSourceText(sb, polymorphicVarContext, TypeExpr.ParenthesizationInfo.NONE, namingPolicy); 
                        
                        if (fieldN < nFields - 1) {
                            sb.append(", ");
                        }
                        
                        ++fieldN;                 
                    }
                    
                    sb.append(')');
                    return;                    
                }
            }                                            
        }
        
        sb.append('{');
        if (!recordVar.isNoFields()) {
            sb.append(recordVar.toString(polymorphicVarContext));            
            if (nFields > 0) {            
                sb.append(" | ");
            }
        }
               
        int fieldN = 0;
        
        for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                       
            FieldName hasFieldName = entry.getKey();
            TypeExpr fieldTypeExpr = entry.getValue();  
            
            sb.append(hasFieldName.getCalSourceForm());
            sb.append(" :: ");
            fieldTypeExpr.toSourceText(sb, polymorphicVarContext, TypeExpr.ParenthesizationInfo.NONE, namingPolicy); 
            
            if (fieldN < nFields - 1) {
                sb.append(", ");
            }
            
            ++fieldN;                 
        }
        
        sb.append('}');     
    }
    
    /** {@inheritDoc} */
    @Override
    SourceModel.TypeExprDefn makeDefinitionSourceModel(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {
        
        RecordVar recordVar = this.getPrunedRecordVar();
        
        //need to sort by field name so that the toString has a definite order.
        SortedMap<FieldName, TypeExpr> hasFieldsMap = getHasFieldsMap();
                
        final int nFields = hasFieldsMap.size(); 
        
        //if the record type represents a tuple (the fields are #1, #2, ..., #n with no gaps, and n >= 2), then display the type
        //using tuple notation.
        if (recordVar.isNoFields() && nFields >= 2) { 
            
            FieldName lastFieldName = hasFieldsMap.lastKey();            
            if (lastFieldName instanceof FieldName.Ordinal) {
                
                FieldName.Ordinal lastOrdinalFieldName = (FieldName.Ordinal)lastFieldName;
                if (lastOrdinalFieldName.getOrdinal() == nFields) {
                                              
                    SourceModel.TypeExprDefn[] components = new SourceModel.TypeExprDefn[nFields];

                    int fieldN = 0;
                    for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                                                               
                        TypeExpr fieldTypeExpr = entry.getValue(); 
                        components[fieldN] = fieldTypeExpr.makeDefinitionSourceModel(polymorphicVarContext, namingPolicy);
                        
                        ++fieldN;
                    }
            
                    return SourceModel.TypeExprDefn.Tuple.make(components);            
                }
            }                                            
        }        
                
        SourceModel.TypeExprDefn.TypeVar baseRecordVar = recordVar.isNoFields()
                ? null
                : SourceModel.TypeExprDefn.TypeVar.make(SourceModel.Name.TypeVar.make(recordVar.toString(polymorphicVarContext)));
               
        ArrayList<SourceModel.TypeExprDefn.Record.FieldTypePair> extensionFields = 
            new ArrayList<SourceModel.TypeExprDefn.Record.FieldTypePair>();

        for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                       
            FieldName hasFieldName = entry.getKey();
            TypeExpr fieldTypeExpr = entry.getValue();  
            
            extensionFields.add(SourceModel.TypeExprDefn.Record.FieldTypePair.make(
                SourceModel.Name.Field.make(hasFieldName), fieldTypeExpr.makeDefinitionSourceModel(polymorphicVarContext, namingPolicy)));
        }
        
        return SourceModel.TypeExprDefn.Record.make(
            baseRecordVar,
            extensionFields.toArray(
                new SourceModel.TypeExprDefn.Record.FieldTypePair[0]));
    }
    
    /** {@inheritDoc} */
    @Override
    int unifyType(TypeExpr anotherTypeExpr, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
        if (anotherTypeExpr instanceof TypeVar ||
            anotherTypeExpr instanceof TypeConsApp ||
            anotherTypeExpr instanceof TypeApp) {
            
            return TypeExpr.unifyType(anotherTypeExpr, this, contextModuleTypeInfo);
            
        } else if (anotherTypeExpr instanceof RecordType) {                       
            
            RecordType anotherRecordType = (RecordType)anotherTypeExpr;
            
            RecordVar recordVar = this.getPrunedRecordVar();            
            RecordVar anotherRecordVar = anotherRecordType.getPrunedRecordVar();                          
            
            if (recordVar.isNoFields()) {
                
                if (anotherRecordVar.isNoFields()) {
                    //2 literal (non-polymorphic) records.
                    //the fields must match exactly
                                        
                    Set<FieldName> hasFieldsSet = this.getHasFieldsMap().keySet();
                    Set<FieldName> anotherHasFieldsSet = anotherRecordType.getHasFieldsMap().keySet();
                    
                    if (!hasFieldsSet.equals(anotherHasFieldsSet)) {
                        throw new TypeException("the fields of the two record type " + this + " and " +
                            anotherRecordType + " must match exactly.");
                    }
                    
                    return pairwiseUnify(hasFieldsSet, anotherRecordType, contextModuleTypeInfo);
                }
                
                //this RecordType is not polymorphic, anotherRecordType is. If they can unify, they will both be
                //non-polymorphic with the same fields as this RecordType.                
                    
                //the hasFields of this RecordType must contain all the hasFields of anotherRecordType.               
                //for example, {zap :: Boolean} fails to unify with (r\foo) => {r | foo :: Int}  
                containsHasFieldsOf(anotherRecordType);
                
                //the fields in the hasFields of this RecordType that are not in the hasFields of anotherRecordType
                //must not be in the lacks fields of anotherRecordType's recordVar.                
                //for example, {foo :: Char, bar :: Int} and (r\foo, r\bar) => {r | bar :: Int} can't unify 
                //because to do so, we must set r = foo, but this is disallowed by the lacks constraint on r.                
                checkHasLacksCompatibility(anotherRecordType);
                                    
                //the hasFields in common must pairwise unify
                Set<FieldName> anotherHasFieldsSet = anotherRecordType.getHasFieldsMap().keySet();
                int typeCloseness = pairwiseUnify(anotherHasFieldsSet, anotherRecordType, contextModuleTypeInfo);

                //the keys of this map are the hasFields of this RecordType that are not hasFields of anotherRecordType                                
                Map<FieldName, TypeExpr> neededHasFieldsMap = getNeededHasFields(anotherRecordType); //String -> TypeExpr
                
                //the needed has fields must satisfy the type class constraints of anotherRecordVar
                typeCloseness += unifyRecordVarConstraints(anotherRecordVar, neededHasFieldsMap, contextModuleTypeInfo);               

                //instantiate anotherRecordVar to complete the unification.
                //instantiate to {NO_FIELDS | the missing (fieldName, type) binding}                               
                          
                RecordType newBaseRecordType = new RecordType(RecordVar.NO_FIELDS, neededHasFieldsMap);                            
                anotherRecordVar.setInstance(newBaseRecordType);
                    
                return typeCloseness;                           
                
            } else {
                
                if (anotherRecordVar.isNoFields()) {
                       
                    return anotherTypeExpr.unifyType(this, contextModuleTypeInfo);
                }
                
                //both this RecordType and anotherRecordType are polymorphic. The unification (if possible) will be polymorphic.
                
                //check the compatibility of has and lacks constraints, in both directions
                checkHasLacksCompatibility(anotherRecordType);                
                anotherRecordType.checkHasLacksCompatibility(this);
            
                Set<FieldName> hasFieldsSet = this.getHasFieldsMap().keySet();
                Set<FieldName> anotherHasFieldsSet = anotherRecordType.getHasFieldsMap().keySet();
                
                //check that the hasFields in common pairwise unify
                SortedSet<FieldName> commonFields = new TreeSet<FieldName>(hasFieldsSet);
                commonFields.retainAll(anotherHasFieldsSet);
                int typeCloseness = pairwiseUnify(commonFields, anotherRecordType, contextModuleTypeInfo);
 
                //the has fields of both records will be the same.
                Map<FieldName, TypeExpr> neededHasFieldsForAnotherRecordType = getNeededHasFields(anotherRecordType);
                Map<FieldName, TypeExpr> neededHasFieldsForThisRecordType = anotherRecordType.getNeededHasFields(this); 
                 
                //the needed has fields must satisfy the type class constraints of anotherRecordVar, and vice-versa
                typeCloseness += unifyRecordVarConstraints(anotherRecordVar, neededHasFieldsForThisRecordType, contextModuleTypeInfo);
                typeCloseness += unifyRecordVarConstraints(recordVar, neededHasFieldsForAnotherRecordType, contextModuleTypeInfo); 
            
                //now we know that things unify. Must patch up the 2 records.               
                                
                //the recordVar will be the same, in fact the very same instance...
                Set<FieldName> newLacksFields = new HashSet<FieldName>(recordVar.getLacksFieldsSet());
                newLacksFields.addAll(anotherRecordVar.getLacksFieldsSet());
                newLacksFields.addAll(hasFieldsSet);
                
                //the class constraints on the 2 record vars will be the same.
                TypeVar typeVar1 = TypeVar.makeTypeVar(null, recordVar.getTypeClassConstraintSet(), false);
                TypeVar typeVar2 = TypeVar.makeTypeVar(null, anotherRecordVar.getTypeClassConstraintSet(), false);
                //this should never fail- we just reuse the code for type variables since the type closeness stuff 
                //is not completely trivial.
                typeCloseness += TypeExpr.unifyType(typeVar1, typeVar2, contextModuleTypeInfo);
                SortedSet<TypeClass> newTypeClassConstraintSet = typeVar1.rootTypeVar().getTypeClassConstraintSet();
                                
                RecordVar newRowVar = RecordVar.makeRecordVar(null, newLacksFields, newTypeClassConstraintSet, false);
                recordVar.setInstance(new RecordType(newRowVar, neededHasFieldsForThisRecordType));
                
                if (recordVar != anotherRecordVar) {
                    anotherRecordVar.setInstance(new RecordType(newRowVar, neededHasFieldsForAnotherRecordType));
                }
                
                return typeCloseness;                
            }
                       
        }
        
        throw new IllegalStateException();
    }
    
    /**
     * A helper function that verifies that the type of each field in fieldToTypeMap satisfies all the
     * type class constraints in recordVar.
     * @param recordVar
     * @param fieldToTypeMap
     * @param contextModuleTypeInfo
     * @return int typeCloseness
     * @throws TypeException
     */
    private int unifyRecordVarConstraints(RecordVar recordVar, Map<FieldName, TypeExpr> fieldToTypeMap, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
        int typeCloseness = 0;
        SortedSet<TypeClass> constraintSet = recordVar.getTypeClassConstraintSet();
        
        for (final Map.Entry<FieldName, TypeExpr> entry : fieldToTypeMap.entrySet()) {
                       
            FieldName fieldName = entry.getKey();
            TypeExpr fieldType = entry.getValue();                                
            TypeVar typeVar = TypeVar.makeTypeVar(null, constraintSet, false);                    
                        
            try {                                           
                typeCloseness += TypeExpr.unifyType(typeVar, fieldType, contextModuleTypeInfo);
            } catch (TypeException te) {
                throw new TypeException ("record type unification failed at field " + fieldName + ".", te);
            }   
        }
        
        return typeCloseness;        
    }
    
    /**
     * A helper function that verifies that the type of each field in fieldToTypeMap satisifies all the
     * type class constraints in recordVar.
     * @param recordVar
     * @param fieldToTypeMap
     * @param patternMatchContext
     * @throws TypeException
     */
    private void patternMatchRecordVarConstraints(RecordVar recordVar, Map<FieldName, TypeExpr> fieldToTypeMap, PatternMatchContext patternMatchContext) throws TypeException {
        
        SortedSet<TypeClass> constraintSet = recordVar.getTypeClassConstraintSet();
        
        for (final Map.Entry<FieldName, TypeExpr> entry : fieldToTypeMap.entrySet()) {
                        
            FieldName fieldName = entry.getKey();
            TypeExpr fieldType = entry.getValue();                                
            TypeVar typeVar = TypeVar.makeTypeVar(null, constraintSet, false);                    
                        
            try {                                           
                fieldType.patternMatch(typeVar, patternMatchContext);
            } catch (TypeException te) {
                throw new TypeException ("record pattern matching failed at field " + fieldName + ".", te);
            }   
        }                   
    }    
          
    /**
     * A helper function that returns the has fields in this RecordType that are not has fields
     * in anotherRecordType. 
     * 
     * @param anotherRecordType
     * @return Map (FieldName -> TypeExpr) the extension fields needed by anotherRecordType 
     */
    private Map<FieldName, TypeExpr> getNeededHasFields(RecordType anotherRecordType) {
        
        Map<FieldName, TypeExpr> hasFieldsMap = getHasFieldsMap();                         
        Map<FieldName, TypeExpr> anotherHasFieldsMap = anotherRecordType.getHasFieldsMap();
        
        Map<FieldName, TypeExpr> neededHasFields = new HashMap<FieldName, TypeExpr>();
        
        for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                       
            FieldName neededFieldName = entry.getKey();
            
            if (!anotherHasFieldsMap.containsKey(neededFieldName)) {
                neededHasFields.put(neededFieldName, entry.getValue());
            }
        } 
        
        return neededHasFields;                    
    }
     
    /**
     * A helper for record unification and pattern matching.
     * 
     * The fields in the hasFields of this RecordType that are not in the hasFields of anotherRecordType
     * must not be in the lacks fields of anotherRecordType's recordVar.
     *
     * For example, {foo :: Char, bar :: Int} and (r\foo, r\bar) => {r | bar :: Int} can't unify 
     * because to do so, we must set r = foo, but this is disallowed by the lacks constraint on r.
     * 
     * @param anotherRecordType
     * @throws TypeException
     */
    private void checkHasLacksCompatibility(RecordType anotherRecordType) throws TypeException {
                                 
        Set<FieldName> illegalNeededFieldsSet = getHasFieldsMap().keySet(); //will iterate in FieldName order
        illegalNeededFieldsSet.removeAll(anotherRecordType.getHasFieldsMap().keySet());
        illegalNeededFieldsSet.retainAll(anotherRecordType.getPrunedRecordVar().getLacksFieldsSet());                   
                           
        if (!illegalNeededFieldsSet.isEmpty()) {

            throw new TypeException("the record type " + anotherRecordType + " cannot unify or pattern match with " + this + " because the needed fields " + illegalNeededFieldsSet + " are not allowed by its lacks constraint.");                        
        }        
    }    
    
    /**
     * A helper function that does pairwise unification of the "hasFields" in 2 record types. 
     *   
     * @param fieldSet fields to iterate over for the pairwise unification   
     * @param anotherRecordType
     * @param contextModuleTypeInfo
     * @return int type closeness measure for the unification
     * @throws TypeException
     */
    private int pairwiseUnify(Set<FieldName> fieldSet, RecordType anotherRecordType, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
        // get 1 type closeness point for the fact that the 2 records match i.e. the "Record" type constructors match.
        // get an additional point for each field that the 2 records have in common
        int typeCloseness = 1 + fieldSet.size();
        
        if (!fieldSet.isEmpty()) {
        
            Map<FieldName, TypeExpr> hasFieldsMap = this.getHasFieldsMap();
            Map<FieldName, TypeExpr> anotherHasFieldsMap = anotherRecordType.getHasFieldsMap();
            
            for (final FieldName hasFieldName : fieldSet) {
                                           
                TypeExpr fieldTypeExpr = hasFieldsMap.get(hasFieldName);
                TypeExpr anotherFieldTypeExpr = anotherHasFieldsMap.get(hasFieldName);            
                               
                try {                                                                                                         
                    typeCloseness += TypeExpr.unifyType(fieldTypeExpr, anotherFieldTypeExpr, contextModuleTypeInfo);
                } catch (TypeException te) {
                    throw new TypeException ("record type unification failed at field " + hasFieldName + ".", te);
                }
            }
        }
                     
        return typeCloseness;
    }
    
    /**
     * A helper function that does pairwise pattern matching of the "hasFields" in 2 record types. 
     *   
     * @param fieldSet (FieldName Set) field names to iterate over for the pairwise unification   
     * @param anotherRecordType
     * @param patternMatchContext     
     * @throws TypeException
     */
    private void pairwisePatternMatch(Set<FieldName> fieldSet, RecordType anotherRecordType, PatternMatchContext patternMatchContext) throws TypeException {
        
        if (!fieldSet.isEmpty()) {
        
            Map<FieldName, TypeExpr> hasFieldsMap = this.getHasFieldsMap();
            Map<FieldName, TypeExpr> anotherHasFieldsMap = anotherRecordType.getHasFieldsMap();
            
            for (final FieldName hasFieldName : fieldSet) {
                                          
                TypeExpr fieldTypeExpr = hasFieldsMap.get(hasFieldName).prune();
                TypeExpr anotherFieldTypeExpr = anotherHasFieldsMap.get(hasFieldName).prune();            
                               
                try {                                                                                                         
                    fieldTypeExpr.patternMatch(anotherFieldTypeExpr, patternMatchContext);
                } catch (TypeException te) {
                    throw new TypeException ("record type unification failed at field " + hasFieldName + ".", te);
                }
            }  
        }                        
    }     
    
    /**
      * A helper functions that fails in an error if the hasFields of anotherRecordType 
      * are not all contained in the hasFields of this RecordType.
      * 
      * @param anotherRecordType
      * @throws TypeException
      */    
    private void containsHasFieldsOf(RecordType anotherRecordType) throws TypeException {
                                                          
        Set<FieldName> hasFieldsSet = this.getHasFieldsMap().keySet();
        Set<FieldName> anotherHasFieldsSet = anotherRecordType.getHasFieldsMap().keySet();
        if (!hasFieldsSet.containsAll(anotherHasFieldsSet)) {

            SortedSet<FieldName> missingFieldsSet = new TreeSet<FieldName>(anotherHasFieldsSet);
            missingFieldsSet.removeAll(hasFieldsSet);
            throw new TypeException("the record type " + this + " is missing the fields " + missingFieldsSet + " from the record type " + anotherRecordType +".");
        }
    }
       

    /** {@inheritDoc} */
    @Override
    public boolean usesForeignType() {
        
        RecordType baseRecordType = getBaseRecordType();
        if (baseRecordType != null) {
            if (baseRecordType.usesForeignType()) {
                return true;            
            }
        }   
                
        for (final Map.Entry<FieldName, TypeExpr> entry : extensionFieldsMap.entrySet()) {
                         
            TypeExpr fieldTypeExpr = entry.getValue();
            if (fieldTypeExpr.usesForeignType()) {
                return true;
            }           
        }
        
        return false;
    }
    
    /**
     * Forms the record type: {baseType | extensionFieldToTypeMap}, if this is possible,
     * and fails in a TypeException otherwise.
     * @param baseType must be a RecordType or TypeVar to succeed (there are other conditions on these as well).
     * @param extensionFieldsMap (FieldName -> TypeExpr)
     * @return RecordType type of the extended record
     * @throws TypeException
     */
    static RecordType recordExtension(TypeExpr baseType, Map<FieldName, TypeExpr> extensionFieldsMap) throws TypeException {
        
        baseType = baseType.prune();
        
        if (baseType instanceof RecordType) {
                                                 
            RecordType baseRecordType = (RecordType)baseType;
            
            //todoBI clean this up            
            //handle the case {} specially to keep the empty record as a singleton.
            if (baseRecordType == TypeExpr.EMPTY_RECORD && extensionFieldsMap.isEmpty()) {
                return baseRecordType;
            }
            
            //the baseRecordType cannot have hasFields that are in the extensionFieldToTypeMap. 
            //For example, {{colour :: Colour} | a :: String, colour :: Colour} results in a TypeException.       
            SortedSet<FieldName> overlappingHasFieldsSet = new TreeSet<FieldName>(baseRecordType.getHasFieldsMap().keySet());
            overlappingHasFieldsSet.retainAll(extensionFieldsMap.keySet());
            
            if (!overlappingHasFieldsSet.isEmpty()) {
                throw new TypeException(baseRecordType + " has fields " + overlappingHasFieldsSet + " overlapping with the record extension fields.");
            }
            
            //if we are extending a record-polymorphic record, then the lacks constraint must add all the extension fields.
            //e.g. {{r\field1 | field1 :: Char} | field2 :: Char} ----> {s\field1, s\field2 | field1 :: Char, field2 :: Char} 
                   
            RecordVar recordVar = baseRecordType.getPrunedRecordVar();           
            if (!recordVar.isNoFields()) {                 
                Set<FieldName> extendedLacksFieldsSet = new HashSet<FieldName>(recordVar.getLacksFieldsSet());
                extendedLacksFieldsSet.addAll(extensionFieldsMap.keySet());                
                recordVar.setInstance(new RecordType(RecordVar.makeRecordVar(null, extendedLacksFieldsSet, TypeClass.NO_CLASS_CONSTRAINTS, false), new HashMap<FieldName, TypeExpr>()));          
            }                                         
            
            RecordVar recordVarInstantiatedToBaseRecord = RecordVar.makePolymorphicRecordVar(null);
            recordVarInstantiatedToBaseRecord.setInstance(baseRecordType);
            return new RecordType(recordVarInstantiatedToBaseRecord, extensionFieldsMap);
              
        } else if (baseType instanceof TypeVar) {
            
            TypeVar baseTypeVar = (TypeVar)baseType;
            
            //since record types cannot be instances of type classes (currently) we cannot instantiate a constrained type variable
            if (!baseTypeVar.noClassConstraints()) {
                throw new TypeException(baseTypeVar + " is a constrained type variable and cannot be the base type of a record extension.");
            }
            
            //baseTypeVar will be instantiated to {r|} where the lacks constraints on r come from the extensionFieldToTypeMap.
            
            RecordVar recordVar = RecordVar.makeRecordVar(null, new HashSet<FieldName>(extensionFieldsMap.keySet()), TypeClass.NO_CLASS_CONSTRAINTS, false);
            RecordType baseRecordType = new RecordType(recordVar, new HashMap<FieldName, TypeExpr>());
            
            baseTypeVar.setInstance(baseRecordType);
            
            return RecordType.recordExtension(baseRecordType, extensionFieldsMap);            
        }
        
        TypeConsApp baseTypeConsApp = (TypeConsApp)baseType;
                
        throw new TypeException(baseTypeConsApp + " a type constructor cannot be the base of a record extension.");        
                
    }    
    
    /**
     * Returns the names of fields which this record type lacks.
     * Note: For non record-polymorphic records, this returns an empty set. 
     * 
     * Ex: for the record type (r\field1, r\field2, r2\name) => {r | field2 = {r2 | name :: [Char]}},
     * this returns the set [field1, field2, field3]
     * 
     * Ex: for the record type {title :: String, author :: (r\name) => {r | name :: [Char]}},
     * this returns the empty set 
     * 
     * @return Set field names which the record lacks
     */
    public Set<FieldName> getLacksFieldsSet() {
        
        RecordVar baseRecordVar = getPrunedRecordVar();
        
        if (baseRecordVar.isNoFields()) {
            return Collections.emptySet();
            
        } else {
            return baseRecordVar.getLacksFieldsSet();
        }
    }
    
    /**
     * Indicates whether this type is a record-polymorphic record
     * 
     * Ex: (r\name) => {r | name :: [Char]} is record polymorphic.
     * 
     * Ex: {name :: [Char]} is non record polymorphic.
     * 
     * Ex: (Eq a) => {age :: a, height :: Double} is non record polymorphic, 
     *     even though one of its fields is polymorphic.
     *  
     * Ex: (r\name) => {age :: Int, author :: {r | name :: [Char]}} ) is non record polymorphic, 
     *     even though one of its fields is.
     *  
     * @return whether the base record variable of this type is polymorphic.
     */ 
    public boolean isRecordPolymorphic() {
        
        RecordVar baseRecordVar = getPrunedRecordVar();
        return !baseRecordVar.isNoFields();             
    }
    
    /** {@inheritDoc} */
    @Override
    void writeActual (RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException {
        s.startRecord(ModuleSerializationTags.RECORD_TYPE, serializationSchema);
        baseRecordVar.write(s, visitedTypeExpr, visitedRecordVar);
        
        s.writeShortCompressed(extensionFieldsMap.size());
        for (final Map.Entry<FieldName, TypeExpr> entry : extensionFieldsMap.entrySet()) {           
            FieldName fn = entry.getKey();
            TypeExpr te = entry.getValue();
            FieldNameIO.writeFieldName(fn, s);
            te.write(s, visitedTypeExpr, visitedRecordVar);
        }
        
        s.endRecord();
    }
    
    /**
     * Load an instance of RecordType from the RecordInputStream.
     * The read position in the stream will be after the RecordType record header.
     * @param s
     * @param schema
     * @param mti
     * @param visitedTypeExpr
     * @param visitedRecordVar
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of RecordType.
     * @throws IOException
     */
    static TypeExpr load (RecordInputStream s, int schema, ModuleTypeInfo mti, Map<Short, TypeExpr> visitedTypeExpr, Map<Short, RecordVar> visitedRecordVar, CompilerMessageLogger msgLogger) throws IOException {
        DeserializationHelper.checkSerializationSchema(schema, serializationSchema, mti.getModuleName(), "RecordType", msgLogger);

        // We want to create this RecordType instance and add it to the visitedTypeExpr
        // map before we load anything that might contain a TypeExpr which refers to this instance.
        RecordType rt = new RecordType();
        visitedTypeExpr.put(new Short((short)visitedTypeExpr.size()), rt);
        
        RecordVar rv = RecordVar.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
        
        Map<FieldName, TypeExpr> extensionFields = new HashMap<FieldName, TypeExpr>();
        int nExtensionFields = s.readShortCompressed();
        for (int i = 0; i < nExtensionFields; ++i) {
            FieldName fn = FieldNameIO.load(s, mti.getModuleName(), msgLogger);
            TypeExpr te = TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
            extensionFields.put(fn, te);
        }
        
        s.skipRestOfRecord();
        
        rt.baseRecordVar = rv;
        rt.extensionFieldsMap = extensionFields;
        return rt;
    }
}
