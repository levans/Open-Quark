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
 * TypeVar.java
 * Created: Sep 24, 2003
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
import java.util.SortedSet;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;


/**
 * A type variable. It is uninstantiated when the instance field is null and instantiated
 * otherwise. An instantiated type variable behaves like its instantiation.
 * @author Bo Ilic
 */
public final class TypeVar extends TypeExpr implements PolymorphicVar {

    private static final int serializationSchema = 0;
    
    /** 
     * If null, then this TypeVar is not instantiated. If non-null, then this TypeVar has been instantiated
     * and can be thought of as being equivalent to the TypeExpr that it has been instantiated to.
     */
    private TypeExpr instance;

    /**
     * The preferred textual name of this TypeVar. null if there is no preferredName. 
     * When a type variable is introduced via a textual declaration, such as with a type declaration, a class instance
     * definition, or a data declaration, then we can preserve the user supplied name, and make use of it whenever
     * possible in displaying String representations of types involving this TypeVar.     
     */
    private String preferredName;
    
    /**
     * (TypeClass). an uninstantiated type variable can be constrained to be instantiated to a type that is a member of
     * all the type classes listed in the typeClassConstraintSet. Note that if a class B inherits from A
     * and B is in the typeClassConstraintSet, then A will not be- the constraint is implicit.
     * If typeClassConstraintSet is the empty set, then the type variable can be instantiated to any typeExpr.
     */
    private SortedSet<TypeClass> typeClassConstraintSet; 
       
    
    /**         
     * Make an unconstrained (i.e. no type context in its type) and uninstantiated type variable 
     */
    TypeVar() {  
        this((String)null);       
    }
    
    /**         
     * Make an unconstrained (i.e. no type context in its type) and uninstantiated type variable 
     * @param preferredName preferred textual name of this TypeVar. null if there is no preferredName. 
     */
    TypeVar(String preferredName) {   
        this(preferredName, TypeClass.NO_CLASS_CONSTRAINTS);       
    }    
         
    /**
     * Keep this constructor private!
     * <p>
     * Note that the typeClassConstraintSet is assumed to:
     * <ol>
     *   <li> not to contain redundant superclass constraints (see TypeClass.removeSuperclassConstraints)
     *   <li> be unmodifiable
     *   <li> initially created with TypeClass.makeNewClassConstraintSet() so that it has the right comparator.
     * </ol> 
     * <p>
     * @param preferredName preferred textual name of this TypeVar. null if there is no preferredName. 
     * @param typeClassConstraintSet (TypeClass SortedSet) constraints set should initially be
     *      created with TypeClass.makeNewClassConstraintSet().
     */
    private TypeVar(String preferredName, SortedSet<TypeClass> typeClassConstraintSet) { 
        if (typeClassConstraintSet == null) {
            throw new NullPointerException();
        }
        
        if (preferredName != null && !LanguageInfo.isValidTypeVariableName(preferredName)) {
            throw new IllegalArgumentException("invalid preferred name " + preferredName);
        }
        
        this.preferredName = preferredName;
        this.typeClassConstraintSet = typeClassConstraintSet;       
   }    
    
    /** 
     * General factory method for creating a new TypeVar.    
     * Note that redundant superclass constraints in typeClassConstraintSet will be removed by this
     * method so this does not need to be done by the caller. 
     * 
     * @param preferredName preferred textual name of this TypeVar. null if there is no preferredName.        
     * @param typeClassConstraintSet (TypeClass SortedSet) constraints set should initially be created with TypeClass.makeNewClassConstraintSet().
     * @param validateTypeClassConstraintSet remove redundant superclass constaints, and make the set unmodifiable. This
     *      is not necessary if reusing another TypeVar's or RecordVar's typeClassConstraintSet.
     */
    static TypeVar makeTypeVar(String preferredName, SortedSet<TypeClass> typeClassConstraintSet, boolean validateTypeClassConstraintSet) {
        if (typeClassConstraintSet.isEmpty()) {
            return new TypeVar(preferredName);
        }
        
        if (validateTypeClassConstraintSet) {                
            TypeClass.removeSuperclassConstraints(typeClassConstraintSet);
            return new TypeVar(preferredName, Collections.unmodifiableSortedSet(typeClassConstraintSet));
        } else {
            return new TypeVar(preferredName, typeClassConstraintSet);
        }
    }
    
    /**   
     * @return an efficient copy of this uninstantiated type variable.
     */
    TypeVar copyUninstantiatedTypeVar() {
        if (instance != null) {
            throw new IllegalStateException("this TypeVar is not uninstantiated");          
        }
        
        if (noClassConstraints()) {
            return new TypeVar(preferredName);
        }
        
        return new TypeVar(preferredName, typeClassConstraintSet);              
    }
          
   /**
    * @return the type expression that this type variable has been instantiated to, or null if this is an uninstantiated type variable.
    */
    TypeExpr getInstance() {
        return instance;
    }

    void setInstance(TypeExpr instance) {

        if (this.instance != null) {            
            throw new IllegalStateException("TypeExpr.setInstance: Programming Error- can't set the instance of an instantiated type variable.");
        }

        this.instance = instance;  
    }
           
    /**{@inheritDoc} */
    public String getPreferredName() {
        return preferredName;
    }
      
    
    /**
     * A helper for selecting a preferred name from 2 possibly different preferred names. 
     * 
     * @param preferredName1
     * @param preferredName2
     * @return a new preferredName. Will be one of preferredName1, preferredName2.
     */
    private static String selectPreferredName(String preferredName1, String preferredName2) {
        if (preferredName1 == null) {
            return preferredName2;
        }
        
        if (preferredName2 == null) {
            return preferredName1;
        }
        
        //otherwise, arbitrarily select the first of the 2 preferred names. There is no particular reason to favour
        //one preferred name over another, but picking *a* preferred name, instead of just returning null, will likely
        //give a more readable type string.
        
       return preferredName1;              
    }
    
    /**
     * @return boolean checks whether this type var has an empty type class constraint set.
     */
    boolean noClassConstraints(){
        return typeClassConstraintSet.isEmpty();
    }
      
    /**
     * Represents the type class constraints on this type variable. This affects the allowable instantiations
     * of the type variable.        
     * @return SortedSet of TypeClass ordered alphabetically by fully qualified name.
     *      Will always return a non-null value. Note that this set is unmodifiable.
     */    
    SortedSet<TypeClass> getTypeClassConstraintSet() {       
        return typeClassConstraintSet;
    }          
     
    /** {@inheritDoc} */
    @Override
    TypeExpr prune() {

        if (instance != null) {            
            return instance.prune();
        }

        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    TypeExpr deepPrune() {

        if (instance != null) {
            return instance.deepPrune();
        }

        return this;
    }
        
    /** {@inheritDoc} */
    @Override
    TypeExpr normalize() {

        if (instance != null) {
            return instance.normalize();
        }

        return this;
    }
    
    @Override
    public boolean sameType(TypeExpr anotherTypeExpr) {
        
        if (instance != null) {
            return instance.sameType(anotherTypeExpr);
        }
        
        anotherTypeExpr = anotherTypeExpr.prune();
        
        if (anotherTypeExpr instanceof TypeVar) {
            
            TypeVar anotherTypeVar = (TypeVar)anotherTypeExpr; 
            
            int nConstraints = this.typeClassConstraintSet.size();
            if (nConstraints == anotherTypeVar.typeClassConstraintSet.size()) {
                
                if (nConstraints == 0) {
                    return true;
                }
                
                return this.toString().equals(anotherTypeVar.toString());                
            }
            
            return false;
            
        } 
        
        if (anotherTypeExpr instanceof TypeConsApp ||
            anotherTypeExpr instanceof RecordType ||
            anotherTypeExpr instanceof TypeApp) {
        
            return false;
        }
        
        throw new IllegalStateException();
    }    

    /** {@inheritDoc} */
    @Override
    void getGenericClassConstrainedPolymorphicVars(Set<PolymorphicVar> varSet, NonGenericVars nonGenericVars) {

        if (instance != null) {
            instance.getGenericClassConstrainedPolymorphicVars(varSet, nonGenericVars);
            return;
        }

        if (!noClassConstraints() && (nonGenericVars == null || nonGenericVars.isGenericTypeVar(this))) {
            varSet.add(this);               
        }
    }
    
    /** {@inheritDoc} */
    @Override
    void getUninstantiatedTypeVars(Set<TypeVar> varSet) {
        if (instance != null) {
            instance.getUninstantiatedTypeVars(varSet);
            return;
        }
        
        varSet.add(this);
    }
    

    /** {@inheritDoc} */
    @Override
    void getUninstantiatedRecordVars(Set<RecordVar> varSet) {
        if (instance != null) {
            instance.getUninstantiatedRecordVars(varSet);
            return;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public TypeExpr getCorrespondingTypeExpr(TypeExpr correspondingSuperType, TypeExpr typeToFind){
        if (this == typeToFind) {
            return correspondingSuperType;
        }
        
        if (instance == null) {
            return null;
        }
        
        return instance.getCorrespondingTypeExpr(correspondingSuperType, typeToFind);
    }

    /** {@inheritDoc} */
    @Override
    void toSourceText(StringBuilder sb, PolymorphicVarContext polymorphicVarContext, ParenthesizationInfo parenthesizationInfo, ScopedEntityNamingPolicy namingPolicy) {

        if (instance != null) {        
            instance.toSourceText(sb, polymorphicVarContext, parenthesizationInfo, namingPolicy);
            return;
        }
        
        polymorphicVarContext.addPolymorphicVar(this);
        
        String typeVarName = polymorphicVarContext.getPolymorphicVarName(this);
       
        if (DEBUG_INFO) {
            sb.append(typeVarName).append(toAddressString());
            return;
        }
        
        sb.append(typeVarName);
        return;
    }      

    /** {@inheritDoc} */
    @Override
    SourceModel.TypeExprDefn makeDefinitionSourceModel(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {

        if (instance != null) {        
            return instance.makeDefinitionSourceModel(polymorphicVarContext, namingPolicy);
        }
        
        polymorphicVarContext.addPolymorphicVar(this);
        
        return SourceModel.TypeExprDefn.TypeVar.make(SourceModel.Name.TypeVar.make(polymorphicVarContext.getPolymorphicVarName(this)));
    }
    
    /** {@inheritDoc} */
    @Override
    public int getArity() {

        if (instance != null) {
            return instance.getArity();
        }

        return 0;
    }

    /** {@inheritDoc} */ 
    @Override
    int unifyType(TypeExpr anotherTypeExpr, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {

        //this TypeVar must be uninstantiated.
        if (instance != null) {
            throw new IllegalStateException("TypeVar.unifyType: programming error- instantiated type variable encountered.");
        }

        // check for infinite types e.g. an attempt to unify a to (a->Int).

        if (anotherTypeExpr.containsUninstantiatedTypeVar(this)) {

            if (this != anotherTypeExpr) {
                throw new TypeException("Type clash: attempt to create an infinite type.");
            }

            // a type variable unifies with itself.
            //get 1 type closeness point for the same type variable in the 2 type expressions
            return 1;
        }

        if (anotherTypeExpr instanceof TypeVar) {

            TypeVar anotherTypeVar = (TypeVar) anotherTypeExpr;

            //anotherTypeVar must be uninstantiated.
            if (anotherTypeVar.instance != null) {
                throw new IllegalArgumentException("TypeVar.unifyType: programming error- instantiated type variable encountered.");
            }

            //if anotherTypeVar is less specialized than this TypeVar (i.e. this TypeVar has all the
            //class constraints of anotherTypeVar) then specialize anotherTypeVar to this TypeVar.

//            if (isClassConstraintSpecialization(anotherTypeVar)) {
//                
//                anotherTypeVar.setInstance(this);
//                //get 0 type closeness points when a specialization of an unconstrained type variable is required.  
//                //get 1 type closeness point when a constrained type var matches another constrained type var.
//                return noClassConstraints() || anotherTypeVar.noClassConstraints() ? 0 : 1;                   
//            }
//
//            if (anotherTypeVar.isClassConstraintSpecialization(this)) {
//                
//                setInstance(anotherTypeVar);
//                //get 0 type closeness points when a specialization of an unconstrained type variable is required.  
//                //get 1 type closeness point when a constrained type var matches another constrained type var.
//                return noClassConstraints() || anotherTypeVar.noClassConstraints() ? 0 : 1; 
//            }
                            
            //For example, Unify (Enum a => a, Num a => a) --> (Enum a, Num a) => a
            TypeVar spanningTypeVar = TypeVar.spanningTypeVar(this, anotherTypeVar);
            setInstance(spanningTypeVar);
            anotherTypeVar.setInstance(spanningTypeVar); 
            
            //get 1 type closeness point if there are dependencies between the constraints. e.g.
            //Unify ((Enum a, Ord a) => a, (Outputable a, Num a) => a) --> (Enum a, Outputable a, Num a) => a.
            //the unification doesn't have 2 + 2 = 4 constraints, but rather only 3, because of dependencies.
            if (spanningTypeVar.typeClassConstraintSet.size() <
                this.typeClassConstraintSet.size() + anotherTypeVar.typeClassConstraintSet.size()) {
                return 1;          
            } 
            
            return 0;
                                           
        } else if (anotherTypeExpr instanceof TypeConsApp) { 
        
            TypeConsApp typeConsApp = (TypeConsApp) anotherTypeExpr;
            
            if (noClassConstraints()) {                               
                setInstance(typeConsApp);
                //get 0 type closeness points when a specialization of an unconstrained type variable is required.
                return 0;
            }
                
            return unifyConstrainedTypeVarWithTypeCons(typeConsApp, contextModuleTypeInfo);                                
            
        } else if (anotherTypeExpr instanceof TypeApp) {
            
            final TypeApp typeApp = (TypeApp)anotherTypeExpr;
            
            if (noClassConstraints()) {
                setInstance(typeApp);
                //get 0 type closeness points when a specialization of an unconstrained type variable is required.
                return 0;
            }
            
            //want to handle cases such as:
            //unify (Eq a => a, (TypeApp (TypeApp (TypeConsApp Either) b) c))
                                          
            int nArgs = 1;
            TypeExpr operatorType = typeApp.getOperatorType().prune();           
            while (operatorType instanceof TypeApp) {
                ++nArgs;
                operatorType = ((TypeApp)operatorType).getOperatorType().prune();                
            }
            
            if (!(operatorType instanceof TypeConsApp)) {
                //for example,
                //unify (Eq a => a, TypeApp b c)
                //will result in a type clash.
                throw new TypeException("Type clash: a constrained type variable cannot unify with a type application not rooted in a type constructor.");
            }
            
            TypeConsApp rootTypeConsApp = (TypeConsApp)operatorType;
            nArgs += rootTypeConsApp.getNArgs();
            
            TypeExpr[] args = new TypeExpr[nArgs];
            
            //fill in the arguments from the partially saturated TypeConsApp.
            System.arraycopy(rootTypeConsApp.getArgs(), 0, args, 0, rootTypeConsApp.getNArgs());
            
            //fill in the arguments from the chain of TypeApp nodes.
            int currentArgIndex = nArgs - 1;
            operatorType = typeApp;
            while (operatorType instanceof TypeApp) {
                TypeApp typeAppOperatorType = (TypeApp)operatorType;
                args[currentArgIndex] = typeAppOperatorType.getOperandType();
                operatorType = typeAppOperatorType.getOperatorType().prune();
                --currentArgIndex;
            }   
                        
            TypeConsApp typeConsApp = new TypeConsApp(rootTypeConsApp.getRoot(), args);
            
            return unifyConstrainedTypeVarWithTypeCons(typeConsApp, contextModuleTypeInfo);
                                   
        } else if (anotherTypeExpr instanceof RecordType) {
            
            RecordType recordType = (RecordType) anotherTypeExpr;
            
            if (!noClassConstraints()) {                
                return unifyConstrainedTypeVarWithRecordType(recordType, contextModuleTypeInfo);                               
            }

            setInstance(recordType);
            //get 0 type closeness points when a specialization of an unconstrained type variable is required.
            return 0;  
            
        }
        
        throw new IllegalArgumentException("programming error");
    }
    
    /**
     * A helper function to unify a type constructor when this TypeVar is a constrained uninstantiated type variable.
     * For example, Unify (Eq a => a, (b, c)) ----> (Eq a, Eq b) => (a, b)
     * @param typeConsApp
     * @param contextModuleTypeInfo
     * @return int type closeness
     */
    private int unifyConstrainedTypeVarWithTypeCons(TypeConsApp typeConsApp, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
        if (instance != null || noClassConstraints()) {
            //this TypeVar must be uninstantiated and constrained.
            throw new IllegalStateException();
        }
        
        TypeConstructor typeCons = typeConsApp.getRoot();        
        final int nArgs = typeConsApp.getNArgs(); 
                
        List<SortedSet<TypeClass>> argConstraints = new ArrayList<SortedSet<TypeClass>>(nArgs);
        for (int i = 0; i < nArgs; ++i) {            
            argConstraints.add(i, TypeClass.makeNewClassConstraintSet());
        }
        
        for (final TypeClass typeClass : typeClassConstraintSet) {
           
            ClassInstance classInstance = contextModuleTypeInfo.getVisibleClassInstance(typeClass, typeCons);                                                
                            
            if (classInstance == null) {
                throw new TypeException("Type clash: type " + typeConsApp.toString() + " is not a member of type class " + typeClass.getName() + ".");                    
            }
            
            //For example, this could be (Eq a, Eq b) => LegacyTuple.Tuple2 a b
            TypeConsApp classInstanceType = (TypeConsApp)classInstance.getType();
                           
            //The type arguments of the type constructor must satisfy additional constraints implied by the class instance.
            //Here we calculate what those constraints are.                                                                            
            for (int i = 0; i < nArgs; ++i) {
                //we know from how instances are defined that the argument types are all uninstantiated
                //type variables (possibly constrained though).
                TypeVar classInstanceArgTypeVar = (TypeVar)classInstanceType.getArg(i);                    
                if (!classInstanceArgTypeVar.noClassConstraints()) { 
                    argConstraints.get(i).addAll(classInstanceArgTypeVar.getTypeClassConstraintSet());                                          
                }            
            }                               
        }
        
        //initialize args.
        TypeExpr[] args = new TypeExpr[nArgs];
        for (int i = 0; i < nArgs; ++i) {            
            args[i] = TypeVar.makeTypeVar(null, argConstraints.get(i), true);                                    
        }
        
        //unification proceeds in 2 steps e.g. For unify (Eq a => a, Tuple2 Char Int)
        //step1: instantiate Eq a => a to (Eq b Eq c => Tuple2 b c)
        //step2: unify (Eq b Eq c => Tuple2 b c, Tuple2 Char Int)                              
        TypeConsApp constrainedTypeConsApp = new TypeConsApp(typeCons, args);               
        setInstance(constrainedTypeConsApp);

        // We do not award any type closeness points for the fact that the type classes matched, because the 
        // instantiated variable is guaranteed to have at least one feature that will be awarded
        // a point.
        return TypeExpr.unifyType(constrainedTypeConsApp, typeConsApp, contextModuleTypeInfo);                         
    }
    
    /**
     * A helper function to unify a record type when this TypeVar is a constrained uninstantiated type variable.
     * For example, Unify (Eq a => a, {r | field1 :: [b]} ----> (Eq s, Eq c) => {s | field1 :: [c]}
     * @param recordType
     * @param contextModuleTypeInfo
     * @return int type closeness
     */
    private int unifyConstrainedTypeVarWithRecordType(RecordType recordType, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {

        if (instance != null || noClassConstraints()) {
            //this TypeVar must be uninstantiated and constrained.
            throw new IllegalStateException();
        }

        //unification proceeds in 3 steps.
        //For example, when unifying:
        //(Eq r, Outputable r) => r 
        //and
        //{s | field1 :: [a], field2 :: Char}
        //first check that there is a record instance for each of the class constraints on r i.e.
        //that there are instance Eq {r} and Outputable {r}.
        //
        //Then instantiate r to:
        //(Eq t, Outputable t, t\field1, t\field2, Eq u, Outputable u, Eq v, Outputable v) => {t | field1 :: u, field2 :: v}
        //
        //then unify
        //(Eq t, Outputable t, Eq u, Outputable u, Eq v, Outputable v) => {t | field1 :: u, field2 :: v}
        //and
        //{s | field1 :: [a], field2 :: Char}
        //which reduces this case to the unification of 2 record types.
        
        //For example, unifying (Eq a, Chartable a} => a
        //with {r | field1 :: b}
        //would result in
        //(Eq r, ChartItem r, Eq c, ChartItem c) => {r | field1 :: c}
        //the interesting thing here is that because the instance declaration is
        //instance ChartItem r => Chartable {r} where ...
        //the constraints on the records and fields are different from the constraints on the type variable a.
        SortedSet<TypeClass> recordTypeClassConstraintSet = TypeClass.makeNewClassConstraintSet();
        
        //check that there is a record instance for each of the class constraints on this type variable
        for (final TypeClass typeClass : typeClassConstraintSet) {
           
            ClassInstance classInstance = contextModuleTypeInfo.getVisibleClassInstance(new ClassInstanceIdentifier.UniversalRecordInstance(typeClass.getName()));      
            if (classInstance == null) {
                throw new TypeException("Type clash: the record type " + recordType + " is not member of the type class " + typeClass.getName() + ".");                                                    
            } 
            
            //For example, this could be (Eq r) => {r}, in the case of the Eq {r} instance
            //or it could be (ChartItem r) => {r} in the case of the Chartable {r} instance
            RecordType classInstanceType = (RecordType)classInstance.getType();
            
            recordTypeClassConstraintSet.addAll(classInstanceType.getPrunedRecordVar().getTypeClassConstraintSet());
        }
        
        //remove redundant constraints and make unmodifiable. This lets us bypass checks on recordTypeClassConstraint set below
        //(the false argument in making the record var and extension fields vars). This is an efficiency optimization.
        TypeClass.removeSuperclassConstraints(recordTypeClassConstraintSet);
        recordTypeClassConstraintSet = Collections.unmodifiableSortedSet(recordTypeClassConstraintSet);
                                           
        //create an intermediate record type whose recordVar and extension fields map all have
        //the type class constraint set equal to recordTypeClassConstraintSet
        
        Map<FieldName, TypeExpr> constrainedExtensionFieldsMap = new HashMap<FieldName, TypeExpr>();     
        Set<FieldName> recordTypeHasFieldsSet = recordType.getHasFieldsMap().keySet(); 
        for (final FieldName fieldName : recordTypeHasFieldsSet) {
            
            constrainedExtensionFieldsMap.put(fieldName, TypeVar.makeTypeVar(null, recordTypeClassConstraintSet, false));                  
        }
        
        RecordVar constrainedRecordVar;
        RecordVar recordTypeRecordVar = recordType.getPrunedRecordVar();
        if (recordTypeRecordVar.isNoFields()) {
            constrainedRecordVar = RecordVar.NO_FIELDS;
        } else {
            constrainedRecordVar = RecordVar.makeRecordVar(null, recordTypeHasFieldsSet, recordTypeClassConstraintSet, false);
        }
        
        RecordType constrainedRecordType = new RecordType(constrainedRecordVar, constrainedExtensionFieldsMap);
        
        setInstance(constrainedRecordType);
                    
        int typeCloseness = TypeExpr.unifyType(constrainedRecordType, recordType, contextModuleTypeInfo); 
        
        // Fields added to the intermediate type get points for matching types, but not for matching names,
        // because the field names weren't present in the original type.
        return typeCloseness - constrainedRecordType.getNHasFields();
    }    
          
    /** {@inheritDoc} */
    @Override
    void patternMatch(TypeExpr anotherTypeExpr, PatternMatchContext patternMatchContext) throws TypeException {

        //this TypeVar must be uninstantiated.
        if (instance != null) {
            throw new IllegalStateException("TypeVar.patternMatch: programming error.");
        }

        if (anotherTypeExpr instanceof TypeVar) {

            //a TypeVar matches with itself
            if (this == anotherTypeExpr) {
                return;
            }

            TypeVar anotherTypeVar = (TypeVar) anotherTypeExpr;

            //anotherTypeVar must be uninstantiated.
            if (anotherTypeVar.instance != null) {
                throw new IllegalArgumentException("TypeVar.patternMatch: programming error.");
            }
                       
            //Can't instantiate a type variable from the declared type expression.
            //For example, this error occurs with the following declaration.
            //funnyId :: a -> b;
            //public funnyId = Prelude.id;
            if (patternMatchContext.isUninstantiableTypeVar(anotherTypeVar)) {
                //todoBI can give a better error message indicating the name of the type variable\
                //with respect to the declared type name. This can be done for all cases (TypeConsApp
                //RecordType etc, but the todo is here only).                           
                throw new TypeException("Attempt to instantiate a type variable from the declared type.");
            }            
            
            //can't pattern match a nongeneric type variable to a generic type variable.
            //For example, for the function:
            //g y = let f x = x + y; in f y;
            //we can't add a local type declaration f :: Num a => a -> a;
            //because that would imply that a is a generic polymorphic type variable,
            //and f could be applied polymorphically in the "in" part. In fact,
            //f's actual type is constrained by each application in the "in" part.
            NonGenericVars nonGenericVars = patternMatchContext.getNonGenericVars();
            if (nonGenericVars != null && !nonGenericVars.isGenericTypeVar(anotherTypeVar)) {
                throw new TypeException("Attempt to match a non-generic type variable to a generic type variable.");
            }

            // check for infinite types e.g. an attempt to pattern match a to (a->Int).
            if (containsUninstantiatedTypeVar(anotherTypeVar)) {
                // Note that we already know that anotherTypeExpr != this.
                throw new TypeException("Type clash: attempt to create an infinite type.");
            }

            //if anotherTypeVar is less specialized than this TypeVar i.e. this TypeVar has all the
            //class constraints of anotherTypeVar then specialize anotherTypeVar to this TypeVar.                          

            if (isClassConstraintSpecialization(anotherTypeVar)) {
                anotherTypeVar.setInstance(this);
                return;
            }
        }
        
        throw new TypeException("Type clash: The type declaration " + toString() + " does not match " + anotherTypeExpr.toString() + ".");                       
    }
       
    /**   
     * Creates a new TypeVar whose typeClassConstraintSet contains all the constraints specified by typeVar1 and typeVar2.   
     * @param typeVar1
     * @param typeVar2
     * @return TypeVar the new TypeVar
     */
    private static TypeVar spanningTypeVar(TypeVar typeVar1, TypeVar typeVar2) {
                   
        if (typeVar1.instance != null || typeVar2.instance != null) {           
            throw new IllegalArgumentException("typeVar1 and typeVar2 must be uninstantiated");
        }
        
        final String newPreferredName = TypeVar.selectPreferredName(typeVar1.preferredName, typeVar2.preferredName);
                 
        if (typeVar1.noClassConstraints() && typeVar2.noClassConstraints()) {
            return new TypeVar(newPreferredName);
        }
         
        SortedSet<TypeClass> spanningConstraints = TypeClass.makeNewClassConstraintSet();        
        spanningConstraints.addAll(typeVar1.typeClassConstraintSet);
        spanningConstraints.addAll(typeVar2.typeClassConstraintSet);
                            
        return TypeVar.makeTypeVar(
            newPreferredName,
            spanningConstraints, true);
    }

    /**
     * Returns true if the class type constraints of this TypeVar are at least as stringent as those of anotherTypeVar.
     * Creation date: (3/22/01 4:02:47 PM)
     * @param anotherTypeVar
     * @return boolean 
     */
    private boolean isClassConstraintSpecialization(TypeVar anotherTypeVar) {

        if (instance != null || anotherTypeVar.instance != null) {
            throw new IllegalStateException("TypeVar.isClassConstraintSpecialization: Programming error. Must compare uninstantiated type variables.");
        }

        if (anotherTypeVar.noClassConstraints()) {
            return true;
        }

        if (noClassConstraints()) {
            return false;
        }

        //todoBI this can be cached for efficiency.
        Set<TypeClass> flattenedTypeClassConstraintSet = flattenTypeClassConstraintSet();

        return flattenedTypeClassConstraintSet.containsAll(anotherTypeVar.typeClassConstraintSet);
    }

    /**
     * Creation date: (3/28/01 3:25:22 PM)
     * @return Set the set of all type class constraints on this TypeVar i.e. including implicit
     * constraints from non-parent ancestor classes.         
     */
    Set<TypeClass> flattenTypeClassConstraintSet() {
      
        Set<TypeClass> flattenedConstraints = new HashSet<TypeClass>(typeClassConstraintSet);
        
        for (final TypeClass typeClass : typeClassConstraintSet) {
            
            flattenedConstraints.addAll(typeClass.calculateAncestorClassList());
        }

        return flattenedConstraints;
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsTypeExpr(TypeExpr searchTypeExpr) {

        if (this == searchTypeExpr) {
            return true;
        }

        if (instance != null) {
            return instance.containsTypeExpr(searchTypeExpr);
        }

        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    boolean containsRecordVar(RecordVar searchRecordVar) {
        if (instance != null) {
            return instance.containsRecordVar(searchRecordVar);    
        }
        
        return false;
    }
        
    /** {@inheritDoc} */
    @Override
    public boolean isPolymorphic() {
        return instance == null || instance.isPolymorphic();
    }
    
    /** {@inheritDoc} */    
    @Override
    public boolean usesForeignType() {
        if (instance == null) {
            return false;
        }
            
        return instance.usesForeignType();
    }
    
    /** {@inheritDoc} */
    @Override
    final void writeActual (RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException {
        
        if (instance == null) {
            s.startRecord(ModuleSerializationTags.TYPE_VAR, serializationSchema);
        } else {
            s.startRecord(ModuleSerializationTags.TYPE_VAR_WITH_INSTANCE, serializationSchema);
        }

        if (instance != null) {
            instance.write(s, visitedTypeExpr, visitedRecordVar);
        }
        
        s.writeShortCompressed(typeClassConstraintSet.size());
        
        for (final TypeClass typeClass : typeClassConstraintSet) {           
            s.writeQualifiedName(typeClass.getName());
        }
        
        s.writeUTF(preferredName);
        
        s.endRecord();
    }
    
    /**
     * Read an instance of TypeVar from the RecordInputStream.
     * Read position will be after the record header.
     * @param s
     * @param schema
     * @param mti
     * @param visitedTypeExpr
     * @param visitedRecordVar
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of TypeVar.
     * @throws IOException
     */
    final static TypeExpr load (RecordInputStream s, short tag, int schema, ModuleTypeInfo mti, Map<Short, TypeExpr> visitedTypeExpr, Map<Short, RecordVar> visitedRecordVar, CompilerMessageLogger msgLogger) throws IOException {
        DeserializationHelper.checkSerializationSchema(schema, serializationSchema, mti.getModuleName(), "TypeVar", msgLogger);
        
        TypeVar tv = new TypeVar();
        visitedTypeExpr.put(new Short((short)visitedTypeExpr.size()), tv);
        
        TypeExpr instance = null;
        if (tag == ModuleSerializationTags.TYPE_VAR_WITH_INSTANCE) {
            instance = TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
        }
        
        SortedSet<TypeClass> typeClassConstraintSet = null;
        int nTypeClasses = s.readShortCompressed();
        if (nTypeClasses > 0) {
            typeClassConstraintSet = TypeClass.makeNewClassConstraintSet();
            for (int i = 0; i < nTypeClasses; ++i) {
                QualifiedName qn = s.readQualifiedName();
                // This could be a type from a non-direct dependee module.
                TypeClass tc = mti.getReachableTypeClass(qn);
                if (tc == null) {
                    throw new IOException ("Unable to resolve TypeClass " + qn + " while loading TypeVar.");
                }
                typeClassConstraintSet.add(tc);
            }
        } else {
            typeClassConstraintSet = TypeClass.NO_CLASS_CONSTRAINTS;
        }
                                     
        tv.typeClassConstraintSet = typeClassConstraintSet;
        
        if (instance != null) {
            tv.setInstance(instance);
        }
        
        tv.preferredName = s.readUTF();       
        
        s.skipRestOfRecord(); 
        
        return tv;
    }
    
}
