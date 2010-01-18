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
 * TypeExpr.java
 * Created: July 17, 2000
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * The internal representation of type expressions. Some examples are (schematically)
 * a -> b, Int, (Int, a -> Boolean).
 * <p>
 * From the point of view of client packages outside the compiler package, TypeExpr is an immutable class.
 * <p>
 * Replacing an instantiated type variable by its instantiation results in an equivalent type.
 * <p>
 * TypeExpr is not an immutable class, but its state transitions are limited.
 * The only state transitions allowed are:
 * <ol>
 *   <li> pruning (replacing instantiated type variables by their instantiations. This preserves
 *    the type equivalence class).
 *   <li> instantiating an uninstantiated type variable to a type constructor or to a type variable
 *        with more stringent type class constraints. This makes the type expression a specialization
 *        of the original type expression.
 * </ol>
 * <p>
 * As a consequence, type expressions whose types do not depend on type variables will always
 * represent the same type.
 * <p>
 * Creation date: (July 17, 2000)
 * @author Bo Ilic
 */
abstract public class TypeExpr {
   
    /** 
     * Serialization schema for a record indicating an already visited type expression.  This
     * goes with the record tag: EXISTING_TYPE_EXPR
     */
    private static final int alreadyVisitedTypeExprSerializationSchema = 0;
    
    /**     
     * Serialization schema for a record indicating constant type expression.  i.e. a type expression 
     * defined as a static final value.
     */
    private static final int staticConstantTypeExprSerializationSchema = 0;
    
    /** the main effect is to print type variables with addresses rather than using letters */
    static final boolean DEBUG_INFO = false;

    //Here are some useful static constants for some of the most common types. Also, note the
    //static helper functions in this class which help with creating types. 
    //Warning: do not add parametric types as public constants, since they can be mutated via type variable instantiation.  
                        
    /** the type of the empty record {} */
    static final TypeExpr EMPTY_RECORD =
        new RecordType(RecordVar.NO_FIELDS, Collections.<FieldName, TypeExpr>emptyMap());
    
    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #load(RecordInputStream, ModuleTypeInfo, Map, Map, CompilerMessageLogger)} method.
     */
    private static final short[] TYPE_EXPR_RECORD_TAGS = new short[]{
        ModuleSerializationTags.ALREADY_VISITED_TYPE_EXPR,
        ModuleSerializationTags.STATIC_CONSTANT_TYPE_EXPR,
        ModuleSerializationTags.TYPE_CONSTRUCTOR,
        ModuleSerializationTags.TYPE_VAR,
        ModuleSerializationTags.TYPE_VAR_WITH_INSTANCE,
        ModuleSerializationTags.RECORD_TYPE,
        ModuleSerializationTags.TYPE_APP
    };
    
    /**
     * Whether to parenthesize a type expression sometimes depends on the parent node in the type expression
     * tree. For example,
     * Int -> Char must be parenthesized in (Int -> Char) -> Boolean because it is the domain of a function type
     * and it itself is a function.
     * Either Int Char must be parenthesized in Maybe (Either Int Char) because its parent is a textual type
     * constructor (Maybe) and it itself is a positive arity textual type constructor.
     * 
     * This class is a type-safe enum indicating the information passed from the parent node in the type expression
     * graph that enables this node to decide if it needs parentheses.
     * 
     * @author Bo Ilic
     */
    final static class ParenthesizationInfo {
        
        private final String name;
        
        /** indicates that no parentheses are necessary for the root of the type expression. */
        static final ParenthesizationInfo NONE = new ParenthesizationInfo("no parentheses required");
        
        /** 
         * indicates that the typeExpr is an argument of a textual form type constructor or type var. 
         * 
         * An example for the (rare) case of a textual type var is the  type string for
         * "Functor.map (Prelude.undefined :: Maybe Int -> Either Char String)" 
         * which is "Cal.Core.Functor.Functor a => a (Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int) -> a (Cal.Core.Prelude.Either Cal.Core.Prelude.Char Cal.Core.Prelude.String)"
         */
        static final ParenthesizationInfo ARG_OF_TEXTUAL_TYPE_CONS_OR_VAR = new ParenthesizationInfo("arg of textual type constructor or type var");
        
        /** indicates that the typeExpr is the domain of a function type constructor (->). */
        static final ParenthesizationInfo DOMAIN_OF_FUNCTION = new ParenthesizationInfo("domain of function");
        
        private ParenthesizationInfo(String name) {
            this.name = name;
        }
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return name;
        }
    }        
 
    /**
     * @return TypeConsApp if this TypeExpr has a TypeConsApp at its root then the TypeConsApp
     *      is returned, otherwise null.
     */
    public final TypeConsApp rootTypeConsApp() {
        
        TypeExpr typeExpr = this.prune();
        return (typeExpr instanceof TypeConsApp) ? (TypeConsApp) typeExpr : null;
    }
    
    /**
     * @return RecordType if this TypeExpr has a TypeApp at its root then the TypeApp
     *      is returned, otherwise null.    
     */    
    public final TypeApp rootTypeApp() {
        
        TypeExpr typeExpr = this.prune();
        return (typeExpr instanceof TypeApp) ? (TypeApp) typeExpr : null;
    }    
    
    /**
     * @return TypeVar if this TypeExpr has a TypeVar at its root then the TypeVar
     *      is returned, otherwise null.    
     */
    public final TypeVar rootTypeVar() {
        
        TypeExpr typeExpr = this.prune();
        return (typeExpr instanceof TypeVar) ? (TypeVar) typeExpr : null;
    }
    
    /**
     * @return RecordType if this TypeExpr has a RecordType at its root then the RecordType
     *      is returned, otherwise null.    
     */    
    public final RecordType rootRecordType() {
        
        TypeExpr typeExpr = this.prune();
        return (typeExpr instanceof RecordType) ? (RecordType) typeExpr : null;
    }
          
    /**
     * Checks if typeExpr2 can be specialized to typeExpr1. There are no side effects.
     *
     * Creation date: (5/16/01 3:24:26 PM)
     * @param typeExpr1
     * @param typeExpr2
     * @param contextModuleTypeInfo context in which this pattern matching takes place. This is needed to know what class instances are in scope.
     * @return boolean returns True if typeExpr2 can be specialized to typeExpr1.
     */
    public static boolean canPatternMatch(TypeExpr typeExpr1, TypeExpr typeExpr2, ModuleTypeInfo contextModuleTypeInfo) {        

        //Note: we copy both type expressions simultaneously since there may be referentially
        //identical type variables in common.
        //So for example, (a, Int) and (Char, a) can't unify whereas (a, Int) and (Char, a') can.
        TypeExpr[] copiedTypes = TypeExpr.copyTypeExprs(new TypeExpr[] {typeExpr1, typeExpr2});
        TypeExpr typeExprCopy1 = copiedTypes[0];
        TypeExpr typeExprCopy2 = copiedTypes[1];
                      
        try {
            TypeExpr.patternMatch(typeExprCopy1, typeExprCopy2, contextModuleTypeInfo);

        } catch (TypeException te) {
            return false;
        }
        
        return true;             
    }      
    
    /**
     * Please see the comment for patternMatchTypePieces. Returns true if patternMatchTypePieces would return without an
     * exception.
     * 
     * Note: there are no side effects on the arguments.
     * 
     * @param types1
     * @param types2
     * @param contextModuleTypeInfo
     * @return boolean
     */
    public static boolean canPatternMatchPieces(TypeExpr[] types1, TypeExpr[] types2, ModuleTypeInfo contextModuleTypeInfo) {

        try {
            TypeExpr.patternMatchPieces (types1, types2, contextModuleTypeInfo);
        } catch (TypeException te) {
            return false;
        }
        
        return true;    
    }    
    
    /**
     * Checks if unification of 2 type expressions would succeed. There are no side effects.
     * This function properly handles shared type variables between typeExpr1 and typeExpr2.    
     * So for example, (a, Int) and (Char, a) can't unify whereas (a, Int) and (Char, a') can. 
     *
     * @param typeExpr1
     * @param typeExpr2
     * @param contextModuleTypeInfo context in which this unification takes place. This is needed to know what class instances are in scope.
     * @return boolean returns True if unification of the 2 type expressions would succeed.
     */
    public static boolean canUnifyType(TypeExpr typeExpr1, TypeExpr typeExpr2, ModuleTypeInfo contextModuleTypeInfo) {      
        
        // Copy the type expressions and try to unify them.
        //Note: we copy both type expressions simultaneously since there may be referentially
        //identical type variables in common.
        
        TypeExpr[] copiedTypes = TypeExpr.copyTypeExprs(new TypeExpr[] {typeExpr1, typeExpr2});
        TypeExpr typeExprCopy1 = copiedTypes[0];
        TypeExpr typeExprCopy2 = copiedTypes[1];
        
        try {
            TypeExpr.unifyType(typeExprCopy1, typeExprCopy2, contextModuleTypeInfo);

        } catch (TypeException te) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Please see the comment for unifyTypePieces. Returns true if unifyTypePieces would return without an
     * exception.
     * 
     * Note: there are no side effects on the arguments.
     * 
     * @param types1
     * @param types2
     * @param contextModuleTypeInfo
     * @return whether unifyTypePieces would return without an exception.
     */
    public static boolean canUnifyTypePieces(TypeExpr[] types1, TypeExpr[] types2, ModuleTypeInfo contextModuleTypeInfo) {

        try {
            TypeExpr.unifyTypePieces (types1, types2, contextModuleTypeInfo);
        } catch (TypeException te) {
            return false;
        }
        
        return true;    
    }    
    
    /**
     * Calculates the degree of closeness between the 2 types. There are no side effects.
     * 
     * <P>
     * The type closeness is a measure of the closeness between the 2 argument types. A value of -1 means that the
     * types do not unify. A value >= 0 means that the type do unify. The larger the number, the closer
     * the 2 types are. This value should not be persisted and may change in the future. It is intended for use
     * by UI clients such as Intellicut in filtering the list of possible candidates to display in the pick list
     * of gems to those that are a closer match. This number is not scaled. What this means is that you can't
     * interpret a closeness of 3 as a having a fixed meaning. In other words, if you fix the first argument,
     * unifyType can be considered as a function from TypeExpr -> Int and the maximum and minimum values attained
     * by this function of one variable on its domain gives the interpretation of the closeness magnitude for 
     * that particular first argument.
     * 
     * <P>
     * The intuition behind the heuristic is that it represents how many "coincidences"
     * there are between two types.  So every time we encounter two things that are the
     * same without being totally uninstantiated type variables, we add a point.
     * 
     * <P>
     * The current heuristic gives 1 point for each time it matches:
     * <ol>
     *   <li> a type constructor
     *   <li> a record type
     *   <li> a record field name that was present in both "original" types
     * </ol>
     * 
     * <P>
     * to something other than an uninstantiated variable.
     * 
     * <P>
     * There are a couple of subtleties here:
     * <ol>
     *    <li> Matching a constrained type variable to a type that meets the contraint
     *         is just as good as matching the type itself.  So for example the type
     *         closeness of "Int" to "Num a => a" is 1, just like the closeness of 
     *         "Int" to "Int".
     *    <li> Variables are instantiated as the type closeness check proceeds.  For
     *         example, the type closeness of "a -> a" to "Int -> Int" is 2:
     *          <ul>
     *             <li> 1 point for matching "->" type constructor
     *             <li> 0 points for matching first a to first Int, since a is uninstantiated
     *             <li> 1 point for matching second a to second Int, because by that point a 
     *                  has been instantiated to Int.
     *           </ul>
     *    <li> Field types (in records) are sometimes matched against intermediate types,
     *         but field names are only matched against original types.  For example,
     *         when calculating the closeness of "Eq a => a" to "{x :: Int, y :: Char}",
     *         the type closeness is 3:        
     *         First we replace "Eq a => a" with the intermediate type "(Eq b, Eq c) => {x :: b, y :: c}"
     *         <ul>
     *             <li> 1 point for matching record types
     *             <li> 1 point for matching type "Eq b => b" to Int
     *             <li> 1 point for matching type "Eq c => c" to Char
     *             <li> 0 points for matching field names (x and y), because the field names
     *                  were not present in both original types  
     *         </ul>
     * <ol>
     * <P>   
     * @param typeExpr1
     * @param typeExpr2
     * @param contextModuleTypeInfo context in which this closeness measure takes place. This is needed to know what class instances are in scope.
     * @return int 
     */
    public static int getTypeCloseness(TypeExpr typeExpr1, TypeExpr typeExpr2, ModuleTypeInfo contextModuleTypeInfo) {
      
        //Copy the type expressions and try to unify them.
        
        //Note: we copy both arguments simultaneously since there may be referentially
        //identical type variables in typeExpr1 and typeExpr2.
        //So for example, (a, Int) and (Char, a) can't unify whereas (a, Int) and (Char, a') can. 
        TypeExpr[] copiedTypes = TypeExpr.copyTypeExprs(new TypeExpr[] {typeExpr1, typeExpr2});
        TypeExpr typeExprCopy1 = copiedTypes[0];
        TypeExpr typeExprCopy2 = copiedTypes[1];
       
        try {
            return typeExprCopy1.unifyType(typeExprCopy2, contextModuleTypeInfo);

        } catch (TypeException te) {
            return -1;
        }   
    }
    
    /**
     * Returns true if searchTypeExpr appears somewhere in this TypeExpr tree.
     * Note: We are doing object/pointer equality here.
     * Creation date: (08/08/01 9:28:13 AM)
     * @return boolean
     * @param searchTypeExpr
     */
    public abstract boolean containsTypeExpr(TypeExpr searchTypeExpr);
    
    /**
     * Returns true if searchTypeVar appears somewhere in this TypeExpr tree.
     * Note: We are doing object/pointer equality here.  
     *     
     * For example, "a -> b" contains the uninstantiated type variable a.
     * We do not allow unifications where a type variable is specialized to a type expression
     * in which the variable itself occurs since this leads to infinite types.
     * There are many difficulties in dealing with infinite types and the Hindley-Milner typing
     * algorithm just disallows them. 
     *
     * @return boolean
     * @param searchTypeVar
     */
    final boolean containsUninstantiatedTypeVar(TypeVar searchTypeVar) {
        if (searchTypeVar.getInstance() != null) {
            throw new IllegalArgumentException();
        }
                    
        return containsTypeExpr(searchTypeVar);
    }
    
    /**
     * Returns true if searchRecordVar appears somewhere in this TypeExpr tree.
     * Note: We are doing object/pointer equality here.
     *
     * @return boolean
     * @param searchRecordVar
     */
    abstract boolean containsRecordVar(RecordVar searchRecordVar);
              
    /**
     * A polymorphic type expression is one that includes an uninstantiated type variable
     * or record variable.
     * Examples of polymorphic types are:
     * a
     * {r}
     * r\field1 => {r | field1 :: Double}
     * Int -> Either a b
     * Examples of non-polymorphic types are:
     * Int
     * {field1 :: Double -> Maybe Char, field2 :: Int}
     * [(Int, Boolean)]
     * @return true if this type expression is polymorphic
     */
    public abstract boolean isPolymorphic();
    
    /**
     * Makes a copy of this type expression. Instantiating the copy won't affect the original
     * type expression.
     * Creation date: (7/30/01 10:10:48 AM)
     * @return TypeExpr
     */
    public final TypeExpr copyTypeExpr() {
        return CopyEnv.freshType(this, null);
    }
    
    /**
     * Make copies of the given type expressions, maintaining referential equality in the duplicated expressions.
     *   eg. if the types to copy are [a -> b, c -> a], this will return [a' -> b', c' -> a'].
     * 
     * @param typesToCopy the type expressions to be copied
     * @return TypeExpr[] the copies of the type expressions.  Referential equality among the types to copy
     *   will be retained in the duplicated types.
     */
    public static TypeExpr[] copyTypeExprs(TypeExpr[] typesToCopy) {        
        return CopyEnv.freshTypePieces(typesToCopy, null);      
    }  
    
    private static CopyEnv.TypesPair copyTypesPair(TypeExpr[] types1, TypeExpr[] types2) {
        return CopyEnv.freshTypesPair(types1, types2, null);                      
    }
    
    /**
     * Get a reference to this type expression with a number of parameters hidden at the beginning.
     * For instance, if the type expression is a -> (b -> bool) -> (c -> d) -> e, calling this method with n == 2 
     * drops a and (b -> bool), returning (c -> d) -> e.
     * Note that if n == 1, this is equivalent to getResultType().
     *
     * Creation date: (30/07/2001 12:00:01 PM)
     * @param n the number of parameters to suppress from the beginning of this type expression
     * @return TypeExpr the resulting type expression
     */
    public final TypeExpr dropFirstNArgs(int n) {
        TypeExpr typeExpr = this.prune();
        for (int i = 0; i < n; i++) {
            if (!(typeExpr instanceof TypeConsApp && ((TypeConsApp) typeExpr).getName().equals(CAL_Prelude.TypeConstructors.Function))) {
                throw new IllegalStateException("Programming Error");
            }
                       
            typeExpr = ((TypeConsApp) typeExpr).getArg(1).prune();
        }
        return typeExpr;
    }
    
    /**
     * Returns the TypeExpr of a subcomponent of the param typeExpr indicated by params componentNumber and dataConstructor.
     * 
     * Eg: If we called 'getComponentTypeExpr( (Either Double Bool), 0, Left), we get back 'Double'.
     *     This is because the type of the Either type is Either a b, with data constructors Left a and Right b.
     * 
     * @param typeExpr the type for which the component should be returned.
     * @param componentNumber the zero-based index of the component to return.
     * @param dataConstructor the data constructor whose component should be returned.
     * @return TypeExpr the component type expression.
     */
    public static TypeExpr getComponentTypeExpr(TypeExpr typeExpr, int componentNumber, DataConstructor dataConstructor) {

        // eg. for Left a, the type is (a -> Either a b), the type pieces are [a, Either a b]
        TypeExpr[] dataConstructorTypePieces = dataConstructor.getTypeExpr().getTypePieces();

        if (dataConstructorTypePieces.length <= componentNumber) {
            throw new IllegalArgumentException("TypeExpr.getComponentTypeExpr() - invalid component number: " + 
                                               componentNumber + " for data constructor " + dataConstructor);
        }

        // This is the TypeExpr that we want (although it may have parts not instantiated properly yet).
        TypeExpr targetTypeExpr = dataConstructorTypePieces[componentNumber];

        // So, let's do a search, and instantiate where appropriate.
        TypeExpr[] actualTypeList = typeExpr.rootTypeConsApp().getArgs();
        TypeConsApp dataConstructorResultType = dataConstructorTypePieces[dataConstructorTypePieces.length - 1].rootTypeConsApp();
        TypeExpr[] searchList = dataConstructorResultType.getArgs();
        
        return instantiateWithCorrespondingTypeExpr(targetTypeExpr, searchList, actualTypeList);
    }
    
    /**
     * Helper method for getComponentTypeExpr().
     * Returns a TypeExpr modeled after targetTypeExpr with the TypeVars instantiated according to searchList and actualTypeList.
     * (If targetTypeExpr is an uninstantiated TypeVar, it will find it's match in the searchList, then, it will be set to the corresponding
     * TypeExpr in the actualTypeList)
     * 
     * Eg: If we called instantiateWithCorrespondingTypeExpr( 'a', ['b', 'a', 'c'], [Double, Int, Bool] ), then "Int" will be returned.
     * Eg: If we called instantiateWithCorrespondingTypeExpr( Maybe 'a', ['b', 'a'], [Double, Int] ), then "Maybe Int" will be returned.
     * 
     * Warning: this is a mutator.       
     * 
     * @param targetTypeExpr
     * @param searchList 
     * @param actualTypeList  
     * @return targetTypeExpr, potentially pruned
     */   
    private static TypeExpr instantiateWithCorrespondingTypeExpr(TypeExpr targetTypeExpr, TypeExpr[] searchList, TypeExpr[] actualTypeList) {

        // After pruning, the targetTypeExpr must either be an uninstantiated TypeVar (which means we have to search thru the list),
        // or it's a TypeConsApp (which means we'll have to check its arg list [if any]).
        targetTypeExpr = targetTypeExpr.prune();

        if (targetTypeExpr instanceof TypeVar) {
            
            TypeVar typeVar = (TypeVar)targetTypeExpr;
                       
            for (int i = 0, n = searchList.length; i < n; ++i) {
                
                if (typeVar.equals(searchList[i])) {
                    return actualTypeList[i];                                       
                }
            }                        

            throw new IllegalArgumentException("Error in instantiateWithCorrespondingTypeExpr method: Unable to find matching TypeExpr.");
            
        } else if (targetTypeExpr instanceof RecordType) {
            
            RecordType recordType = targetTypeExpr.rootRecordType();            
            Map<FieldName, TypeExpr> hasFieldsMap = recordType.getHasFieldsMap();                            
            for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                                    
                TypeExpr fieldType = entry.getValue();            
                instantiateWithCorrespondingTypeExpr(fieldType, searchList, actualTypeList);
            }            
            
            return recordType;
           
        } else if (targetTypeExpr instanceof TypeConsApp) {

            TypeConsApp typeConsApp = (TypeConsApp) targetTypeExpr;
                                   
            for (int i = 0, nArgs = typeConsApp.getNArgs(); i < nArgs; ++i) {
                typeConsApp.setArg(i, instantiateWithCorrespondingTypeExpr(typeConsApp.getArg(i), searchList, actualTypeList));
            }
            
            return typeConsApp;
            
        } else {
            throw new IllegalArgumentException("Unhandled TypeExpr class: " + targetTypeExpr.getClass());
        }
    }
    
    /**
     * Returns the set of generic, type-class constrained and uninstantiated polymorphic variables (i.e. either type variables or
     * record variables) that occur in this TypeExpr. This set has a well-defined ordering determined by the traversal
     * of the TypeExpr tree.
     * (As a technical point, record types are traversed first via they polymorphic record variable, then
     * by field name order of the "has fields". In other words, the ordering returned below is independent of the representation
     * of the record type as a sequence of extensions).
     * Note that record variables can have both type-class constraints and lacks constraints. This refers only to the type-class
     * constraints.
     * 
     * @param nonGenericVars nonGenericVars list of type variables that are not generic. Can be null to indicate an empty list.
     * @return Set (PolymorphicVar Set, where each PolymorphicVar is either a TypeVar or RecordVar)
     */
    final Set<PolymorphicVar> getGenericClassConstrainedPolymorphicVars(NonGenericVars nonGenericVars) {
        //use a LinkedHashSet to ensure iteration order.        
        Set<PolymorphicVar> varSet = new LinkedHashSet<PolymorphicVar>();
        getGenericClassConstrainedPolymorphicVars(varSet, nonGenericVars);
        return varSet;
    }
    
    
    /**
     * A helper function that returns the TypeVar with the specified typeClassTypeVarName
     * @param typeClassTypeVarName
     * @return TypeVar will not be null (the method fails in an exception if the type variable cannot be found).
     */
    final TypeVar getTypeClassTypeVar(String typeClassTypeVarName) {
        
        if (typeClassTypeVarName == null) {
            throw new NullPointerException("typeClassTypeVarName cannot be null.");
        }
                     
        //todoBI can give a more efficient implementation.               
        for (final PolymorphicVar typeVar : getGenericClassConstrainedPolymorphicVars(null)) {
           
            if (typeClassTypeVarName.equals(typeVar.getPreferredName())) {
                return (TypeVar)typeVar;
            }
        }
        
        throw new IllegalArgumentException("type class type variable '" + typeClassTypeVarName + "' not found");
    }

    /**
     * Helper function for finding the set of generic, constrained and uninstantiated polymorphic variables (i.e. either type variables or
     * record variables) that occur in this TypeExpr. This set has a well-defined ordering determined by the traversal of
     * the TypeExpr tree.
     * (As a technical point, record types are traversed first via they polymorphic record variable, then
     * by field name order of the "has fields". In other words, the ordering returned below is independent of the representation
     * of the record type as a sequence of extensions).     
     * Note that record variables can have both type-class constraints and lacks constraints. This refers only to the type-class
     * constraints.
     *
     * @param varSet Set (PolymorphicVar Set, where each PolymorphicVar is either a TypeVar or RecordVar)
     * @param nonGenericVars nonGenericVars list of type variables that are not generic. Can be null to indicate an empty list.
     */
    abstract void getGenericClassConstrainedPolymorphicVars(Set<PolymorphicVar> varSet, NonGenericVars nonGenericVars);
    
    /**
     * Returns the set of uninstantiated TypeVars that occur in this TypeExpr. This set has a
     * well-defined ordering determined by the traversal of the TypeExpr tree. 
     * However, the ordering is *not* invariant with respect to the representation of record types as extensions,
     * and so in effect the ordering should not be used for any critical purpose. (This is in contrast with
     * getGenenericClassConstrainedPolymorphicVars. An invariant order could be defined, but it is more expensive,
     * and we don't need it currently).
     * @return Set
     */
    final Set<TypeVar> getUninstantiatedTypeVars() {
        //use a LinkedHashSet to ensure iteration order.        
        Set<TypeVar> varSet = new LinkedHashSet<TypeVar>();
        getUninstantiatedTypeVars(varSet);
        return varSet;
    }

    /**
     * Helper function for finding the set of uninstantiated TypeVars that occur in this
     * TypeExpr. This set has a well-defined ordering determined by the traversal of the TypeExpr tree.
     * However, the ordering is *not* invariant with respect to the representation of record types as extensions,
     * and so in effect the ordering should not be used for any critical purpose. (This is in contrast with
     * getGenenericClassConstrainedPolymorphicVars. An invariant order could be defined, but it is more expensive,
     * and we don't need it currently).
     *
     * @param varSet Set set of the uninstantiated TypeVars in this TypeExpr.    
     */
    abstract void getUninstantiatedTypeVars(Set<TypeVar> varSet);
   
    /**
     * Returns the set of uninstantiated RecordVars that occur in this TypeExpr.  Don't depend on this
     * set being in any particular order.
     * @return Set
     */
    final Set<RecordVar> getUninstantiatedRecordVars() {
        Set<RecordVar> varSet = new HashSet<RecordVar>();
        getUninstantiatedRecordVars(varSet);
        return varSet;
    }
    
    /**
     * Helper function for finding the set of uninstantiated RecordVars that occur in this TypeExpr.
     * Don't rely on the order of this set.
     */
    abstract void getUninstantiatedRecordVars(Set<RecordVar> varSet);
    
    /**
     * Get the TypeExpr corresponding to a reference to a subtype in another typeExpr
     * eg. if this typeExpr == (a -> (Either a b) -> c), correspondingSuperType == (d -> (Either d e) -> f), typeToFind == a,
     * this will return d.
     * Creation date: (04/04/2002 5:03:00 PM)
     */
    public abstract TypeExpr getCorrespondingTypeExpr(TypeExpr correspondingSuperType, TypeExpr typeToFind);
    
    /**
     * Returns the number of top level fully saturated applications of the Prelude.Function 
     * type constructor in this TypeExpr. Intuitively, this can be considered the arity of a 
     * function having this as its type.
     * 
     * For example, if this TypeExpr corresponds to (a -> b) -> Boolean -> (c -> d) then it returns 3.
     * This counts the arrows as shown: (a -> b)-#1#->Bool-#2#->(c-#3#->d). Note that the
     * -> operator is right associative so that the parentheses around c->d are not
     * necessary. 
     *
     * Creation date: (9/13/00 1:55:49 PM)
     * @return int
     */
    abstract public int getArity();
    
    /**
     * Similar to getTypePieces, except that it just returns the result type. It is more
     * efficient to use this method if that is all you need.
     * Creation date: (4/23/01 4:09:38 PM)
     * @return TypeExpr
     */
    public final TypeExpr getResultType() {

        TypeExpr typeExpr = this.prune();

        if (typeExpr instanceof TypeConsApp) {

            TypeConsApp typeConsApp = (TypeConsApp) typeExpr;

            if (typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) && typeConsApp.getNArgs() == 2) {
                
                TypeExpr rhs = typeConsApp.getArg(1);
                return rhs.getResultType();
            }
            
        } else if (typeExpr instanceof TypeApp) {
            
            TypeApp typeApp = (TypeApp)typeExpr;
            
            //recognize the special forms:
            
            //(TypeApp (TypeConsApp Function te1) te2)
            //and
            //(TypeApp (TypeApp (TypeConsApp Function) te1) te2)     
            
            TypeExpr operatorType = typeApp.getOperatorType().prune();
            
            if (operatorType instanceof TypeConsApp) {
                
                //recognize the form (TypeApp (TypeConsApp Function e1) (e2))
                
                TypeConsApp typeConsApp = (TypeConsApp)operatorType;
                
                if (typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) && typeConsApp.getNArgs() == 1) {    
                    return typeApp.getOperandType().getResultType();              
                }     
                
            } else if (operatorType instanceof TypeApp) {
                
                //recognize the form (TypeApp (TypeApp (TypeConsApp Function) te1) te2)
                
                TypeApp nextTypeApp = (TypeApp)operatorType;
                
                TypeExpr nextOperatorType = nextTypeApp.getOperatorType().prune();
                
                if (nextOperatorType instanceof TypeConsApp) {
                    
                    TypeConsApp typeConsApp = (TypeConsApp)nextOperatorType;
                    
                    if (typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) && typeConsApp.getNArgs() == 0) {    
                        return typeApp.getOperandType().getResultType();            
                    }                   
                }
            }          
        }

        return typeExpr;
    }
    
    /**
     * Returns a TypeExpr that represents the input types of this gem.
     * If the gem has no inputs then null is returned.
     * @return TypeExpr the expression that represents the input types
     */
    public final TypeExpr getArgumentType() {
        
        TypeExpr pieces[] = getTypePieces();
        
        if (pieces.length == 1) {
            return null;
        
        } else if (pieces.length == 2) {
            return pieces[0];
        
        } else {
            
            TypeExpr arguments = pieces[pieces.length - 2];
            
            for (int i = pieces.length - 3; i >= 0; i--) {
                arguments = makeFunType(pieces[i], arguments);
            }
            
            return arguments;
        }
    }
                
    /** 
     * Note that the list type must be fully saturated for this method to return true. i.e. "isListType (Prelude.List)" returns false.  
     * Creation date: (July 25, 2002)
     * @return boolean true if this type is of the form [a] for any type a.     
     */
    public final boolean isListType() {  
        
        TypeExpr typeExpr = this.prune();
        
        if (typeExpr instanceof TypeConsApp) {
            
            //recognize the form (TypeConsApp List te)
            
            TypeConsApp typeConsApp = (TypeConsApp)typeExpr;
            return typeConsApp.getNArgs() == 1 && typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.List);
            
        } else if (typeExpr instanceof TypeApp) {
            
            //recognize the form (TypeApp (TypeConsApp List) te)
            
            TypeExpr operatorType = ((TypeApp)typeExpr).getOperatorType();
            if (operatorType instanceof TypeConsApp) {
                
                TypeConsApp typeConsApp = (TypeConsApp)operatorType;
                return typeConsApp.getNArgs() == 0 && typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.List);
                
            }            
        }
        
        return false;      
    }
    
    /**   
     * Creation date: (July 25, 2002)
     * @return boolean true if this type is a tuple of dimension 2 or more. e.g. (Int, a), ([Int], [(a,b)], c)     
     */
    public final boolean isTupleType() {             
        return getTupleDimension() >= 2;       
    }

    /**
     * Note that a non fully saturated application of Prelude.Function is not considered to be a function type e.g. 
     * calling this method on "Prelude.Function Int" returns false. 
     * @return true if this type is of the form a -> b, for some types a and b. For example, a -> b, Int -> [String] -> Boolean, etc.)
     */
    public final boolean isFunctionType() {  
        //we use getArity() in order to correctly handle TypeApp forms
        
        return getArity() > 0;       
    }
    
    /**
     * @param typeConsName the name of the type constructor
     * @return true if this type expression, after pruning, is a list type and its elements,
     * after pruning, are type constructors with the given name.
     */
    public final boolean isListTypeOf(QualifiedName typeConsName) {
        
        //todoBI recognize types using the TypeApp form 
         
        TypeConsApp typeConsApp = this.rootTypeConsApp();
        if (typeConsApp != null &&
            typeConsApp.hasRootTypeConstructor(CAL_Prelude.TypeConstructors.List) &&
            typeConsApp.getNArgs() == 1) {
                
            return typeConsApp.getArg(0).hasRootTypeConstructor(typeConsName);             
        }            
        
        return false;
    }
    
    /**
     * Returns true if this TypeExpr is a non-parametric type (e.g. such as Int, String or Ordering) and
     * its name is the same as typeConsName.
     * 
     * It is faster to use TypeExpr.sameType(TypeExpr) if you have access to the other TypeExpr to compare with.
     *      
     * @param typeConsName
     * @return true if this TypeExpr is a non-parametric type with type constructor name as specified.
     */
    public final boolean isNonParametricType(QualifiedName typeConsName) {
        TypeConsApp typeConsApp = this.rootTypeConsApp();
        return typeConsApp != null &&
            typeConsApp.getNArgs() == 0 &&
            typeConsApp.getName().equals(typeConsName);       
    }
    
    /**
     * An enumeration type is a:
     * -non parameteric type (i.e. the type has 0 arity)
     * -not a foreign type
     * -there is at least one data constructor
     * -all data constructors have 0 arity
     * -it is not Prelude.Boolean
     * For example, Prelude.Ordering is an enumeration type.
     *      
     * @return true if the given type is an enumeration, according to the above definition.
     */
    public static boolean isEnumType(TypeConstructor typeCons) {
        if (typeCons.getTypeArity() > 0 || 
            typeCons.getForeignTypeInfo() != null || 
            typeCons.getNDataConstructors() == 0 ||
            typeCons.getName().equals(CAL_Prelude.TypeConstructors.Boolean)) {
            return false;
        }
               
        for (int i = 0, nDataCons = typeCons.getNDataConstructors(); i < nDataCons; ++i) {
            DataConstructor dc = typeCons.getNthDataConstructor(i);
            if (dc.getArity() > 0) {
                return false; 
            }
        }
        
        return true;

    }
    
    /**
     * For example, it this TypeExpr is "Prelude.Maybe Prelude.Int", and typeConsName is "Prelude.Maybe",
     * then hasRootTypeConstructor would return true.
     * 
     * @param typeConsName the name of the type constructor
     * @return true if this type expression, after pruning, has as its root a type constructor with the given name
     */
    public final boolean hasRootTypeConstructor(QualifiedName typeConsName) {
        TypeExpr typeExpr = this.prune();
        return typeExpr instanceof TypeConsApp &&
               ((TypeConsApp) typeExpr).getName().equals(typeConsName);
    }

    /**
     * Returns the tuple dimension of the current type, or -1 if the type is not a tuple.
     * A tuple is by definition a record type of the form {#1 :: t1, ..., #n :: tn} where n>=2,
     * there are no gaps in the ordinal fields and there are no textual fields.
     * 
     * For example, for
     * {#1 :: Int, #2 :: {name = "Fred", age = 40}}
     * we return 2.
     * 
     * For
     * Just {#1 :: Int, #2 :: {name = "Fred", age = 40}}
     * we return -1.
     * 
     * @return int the number of components of the tuple type (n in the above notation)
     */
    public final int getTupleDimension() {
        TypeExpr typeExpr = this.prune();
        
        if (typeExpr instanceof RecordType) {
            
            RecordType recordType = (RecordType)typeExpr;
            RecordVar prunedRecordVar = recordType.getPrunedRecordVar();
            if (!prunedRecordVar.isNoFields()) {
                return -1;
            }
            
            //todoBI this can be computed more efficiently. Don't need to create the set to find its
            //maximal element.
            SortedSet<FieldName> hasFieldsSet = recordType.getHasFields();
            int nHasFields = hasFieldsSet.size();
            
            if (nHasFields <= 1) {
                return -1;
            }
            
            FieldName lastFieldName = hasFieldsSet.last();                   
            if (lastFieldName instanceof FieldName.Ordinal) {
                if (((FieldName.Ordinal)lastFieldName).getOrdinal() == nHasFields) {
                    return nHasFields;
                }
            }
        }
        
        return -1;
    }   
            
    /**
     * Breaks up a type into an array of types holding the type of each argument followed by the return type where we
     * are interpreting this TypeExpr to be the TypeExpr of a function (or functional agent). Of course, this interpretation can
     * always be made! This method is intended for use primarily by a UI client for colouring arguments and return types based
     * on type equality,
     *
     * For example, the type (a->b) -> Double -> b will return [a->b, Double, b].
     * Creation date: (11/23/00 12:47:26 PM)
     * @return TypeExpr[] an array holding the type of each argument followed by the return type
     */
    public final TypeExpr[] getTypePieces() {
        return getTypePiecesHelper(getArity());       
    }
    
    /**
     * In certain cases there are multiple options as to how to break up a type into argument types and
     * return type. Primarily, this is the case if a functional return type is needed.
     * For example, if the type is Int -> Int -> Boolean. Then getTypePieces() will return:
     * [Int, Int, Boolean]. However, we may want to consider this as a type expression for a function of 1 
     * argument returning a function. Then getTypePieces(1) returns: [Int, Int->Boolean]. 
     *         
     * @param arity must be less than or equal to the type arity of this type expression
     * @return TypeExpr[] type pieces array of length arity + 1.
     */
    public final TypeExpr[] getTypePieces(int arity) {
               
        if (arity < 0 || arity > getArity()) {
            throw new IllegalArgumentException("arity arguments must be non-negative and less than or equal to TypeExpr.getArity()");
        }
        
        return getTypePiecesHelper(arity);
    }
    
    private final TypeExpr[] getTypePiecesHelper(int arity) {
    
        TypeExpr[] typePieces = new TypeExpr[arity + 1];

        TypeExpr typeExpr = this.prune();

        for (int i = 0; i < arity; ++i) {
            
            //will match one of the 3 functional forms for te1 -> te2, or will fail in an IllegalStateException.
            
            if (typeExpr instanceof TypeConsApp) {
                
                TypeConsApp typeConsApp = (TypeConsApp)typeExpr;
                
                if (!typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) || typeConsApp.getNArgs() != 2) {
                    throw new IllegalStateException();
                }
                
                typePieces[i] = typeConsApp.getArg(0).prune();
                typeExpr = typeConsApp.getArg(1).prune();
                
            } else if (typeExpr instanceof TypeApp) {
                
                TypeApp typeApp = (TypeApp)typeExpr;
                
                TypeExpr operatorType = typeApp.getOperatorType().prune();
                if (operatorType instanceof TypeConsApp) {
                    
                    //recognize the form (TypeApp (TypeConsApp Function e1) (e2))
                    
                    TypeConsApp typeConsApp = (TypeConsApp)operatorType;
                    
                    if (!typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) || typeConsApp.getNArgs() != 1) {
                        throw new IllegalStateException();
                    }
                    
                    typePieces[i] = typeConsApp.getArg(0).prune();
                    typeExpr = typeApp.getOperandType().prune();
                    
                } else if (operatorType instanceof TypeApp) {
                                        
                    
                    TypeApp nextTypeApp = (TypeApp)operatorType;
                    
                    TypeExpr nextOperatorType = nextTypeApp.getOperatorType().prune();
                    
                    if (nextOperatorType instanceof TypeConsApp) {
                        
                        // recognize the form (TypeApp (TypeApp (TypeConsApp Function) te1) te2)
                        
                        TypeConsApp typeConsApp = (TypeConsApp)nextOperatorType;
                        
                        if (!typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) || typeConsApp.getNArgs() != 0) {    
                            throw new IllegalStateException();            
                        } 
                        
                        typePieces[i] = nextTypeApp.getOperandType().prune();
                        typeExpr = typeApp.getOperandType().prune();
                    }   
                    
                } else {                    
                    throw new IllegalStateException();
                }                
            } else {
                throw new IllegalStateException();
            }
         
        }

        typePieces[arity] = typeExpr;

        return typePieces;
    }    
    
    /**
     * Add the uninstantiated typevars and record variables in this type expression to the
     * PolymorphicVarContext. This is useful when toString-ing several related TypeExpr objects,
     * in such a way that the variables within them do no overlap unless they are indeed the same.     
     * 
     * Creation date: (10/01/01 11:58:50 AM)
     * 
     * @param polymorphicVarContext object threaded through nested toString invocations
     *        to track the type and record variables within this TypeExpr. 
     * @return PolymorphicVarContext the polymorphicVarContext argument, if it was not null,
     *      or a new PolymorphicVarContext, suitably populated, otherwise.
     */
    final PolymorphicVarContext indexPolymorphicVars(PolymorphicVarContext polymorphicVarContext) {
        
        if (polymorphicVarContext == null) {
            polymorphicVarContext = PolymorphicVarContext.make();
        }
        
        // indexes uninstantiated typevars as a side effect.       
        toSourceText(new StringBuilder(), polymorphicVarContext, ParenthesizationInfo.NONE, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
               
        return polymorphicVarContext;      
    }    
    
    /**
     * @return the TypeExpr of the compose operator (#), namely (b->c) -> (a->b) -> (a->c).
     */
    static TypeExpr makeComposeType() {
        
        TypeExpr a = TypeExpr.makeParametricType();
        TypeExpr b = TypeExpr.makeParametricType();
        TypeExpr c = TypeExpr.makeParametricType();
        TypeExpr b2c = TypeExpr.makeFunType(b, c);
        TypeExpr a2b = TypeExpr.makeFunType(a, b);
        TypeExpr a2c = TypeExpr.makeFunType(a, c);
        return TypeExpr.makeFunType(b2c, TypeExpr.makeFunType(a2b, a2c));
    }
    
    /**
     * Create a type expression for the type of function from domain to codomain.
     *
     * Creation date: (1/22/01 1:08:03 PM)
     * @return TypeExpr the type expression for the function
     * @param domain the type of the domain. Cannot be null.
     * @param codomain the type of the codomain. Cannot be null.
     */
    public static TypeExpr makeFunType(TypeExpr domain, TypeExpr codomain) {
        if (domain == null) {
            throw new NullPointerException("domain type cannot be null");
        }
        if (codomain == null) {
            throw new NullPointerException("codomain type cannot be null");
        }
        
        TypeExpr[] args = new TypeExpr[] {domain, codomain};
        return new TypeConsApp(TypeConstructor.FUNCTION, args);
    }
    
    /**
     * Create a type expression for a list.
     *
     * @param elementTypeExpr the type of the element. Cannot be null.
     * @param contextModuleTypeInfo
     * @return TypeExpr the type expression for the list with given element type    
     */
    public static TypeExpr makeListType(TypeExpr elementTypeExpr, ModuleTypeInfo contextModuleTypeInfo) { 
        if (elementTypeExpr == null) {
            throw new NullPointerException("elementTypeExpr cannot be null");
        }
        
        TypeConstructor listTypeCons = contextModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.List);
        return new TypeConsApp(listTypeCons, new TypeExpr [] {elementTypeExpr});
    }
             
    /**
     * Create a type expression for a data type that is not dependent on any type variables.
     * For example, "Maybe a" is not such a type because of the "a" while "Boolean" is such a type.
     *
     * Creation date: (3/1/01 5:27:09 PM)     
     * @param rootTypeCons must have type arity == 0.
     * @return TypeExpr the type name of the non parametric type.
     */
    public static TypeExpr makeNonParametricType(TypeConstructor rootTypeCons) {
         
        if (rootTypeCons.getTypeArity() != 0) {
            throw new IllegalArgumentException("TypeExpr.makeNonParametricType can only be used for types with type arity 0. " +
                rootTypeCons + " has type arity " + rootTypeCons.getTypeArity() + "."); 
        }

        return new TypeConsApp(rootTypeCons, null);
    }

    /**    
     * Create a type expression for the type "a".
     *  
     * @return TypeExpr 
     */
    public static TypeExpr makeParametricType() {
        return new TypeVar();
    }   
    
    /**
     * Create a type expression for a tuple data constructor. For example, if n = 3, then the data
     * constructor Tuple3 or (,,,) has type a -> b -> c -> (a, b, c).
     *
     * @return TypeExpr the type expression for the tuple data cons
     * @param n size of the Tuple type
     */
    public static TypeExpr makeTupleDataConsType(int n) {

        if (n < 2) {
            throw new IllegalArgumentException("TypeExpr.makeTupleDataConsType: a tuple type must have at least 2 components.");
        }

        List<TypeExpr> componentTypeVarList = new ArrayList<TypeExpr>(n);
        for (int i = 0; i < n; ++i) {
            componentTypeVarList.add(new TypeVar());
        }

        TypeExpr typeExpr = makeTupleType(componentTypeVarList);

        for (int i = n - 1; i >= 0; --i) {
            typeExpr = makeFunType(componentTypeVarList.get(i), typeExpr);
        }

        return typeExpr;
    }
    
    /**
     * Create a type expression for a tuple each of whose entries is a different type var.
     *   
     * @param n size of the Tuple type
     * @return TypeExpr the type expression for the tuple
     * @throws IllegalArgumentException if n is not greater than 2
     */
    public static TypeExpr makeTupleType(int n) {

        if (n < 2) {
            throw new IllegalArgumentException("the tuple dimension must be greater than or equal to 2.");
        }

        List<TypeExpr> componentTypeList = new ArrayList<TypeExpr>(n);
        for (int i = 0; i < n; ++i) {
            componentTypeList.add(new TypeVar());
        }

        return makeTupleType(componentTypeList);
    }
    
    /**
     * Create a type expression for a tuple. The type of the tuple depends on the number of elements
     * of componentTypeExprList.
     *         
     * @param componentTypeExprList (TypeExpr) a list of the component type expressions.
     * @return TypeExpr the type expression for the tuple
     * @throws IllegalArgumentException if the number of elements in the list is not greater than 2
     */
    public static TypeExpr makeTupleType(List<TypeExpr> componentTypeExprList) {
        
        int nChildren = componentTypeExprList != null ? componentTypeExprList.size() : 0;
        if (nChildren < 2) {
            throw new IllegalArgumentException("the tuple dimension must be greater than or equal to 2.");
        }
        
                               
        Map<FieldName, TypeExpr> fieldNameToTypeMap = new HashMap<FieldName, TypeExpr>(); //FieldName -> TypeExpr       
        for (int i = 0; i < nChildren; ++i) {
            fieldNameToTypeMap.put(FieldName.makeOrdinalField(i + 1), componentTypeExprList.get(i));
        }
        
        return new RecordType(RecordVar.NO_FIELDS, fieldNameToTypeMap);
    }
    
    /**
     * Create a type expression for a free record. The result will be a record type containing 
     * fields named as specified and associated to parametric values. 
     * 
     * Ex: makeRecordType([age,height]) creates record (r\age, r\height) == {r | age :: a, height :: b}
     * Ex: makeRecordType([]) == {r}
     *  
     * @param fieldNamesSet Set of field names. 
     * @return RecordType the record type
     */
    public static RecordType makeFreeRecordType(Set<FieldName> fieldNamesSet) {
        
        // Create field name to field type map
        Map<FieldName, TypeExpr> extensionFieldsMap = new HashMap<FieldName, TypeExpr>();
        for (final FieldName fieldName : fieldNamesSet) {                   
            
            TypeExpr fieldType = TypeExpr.makeParametricType();
            extensionFieldsMap.put(fieldName, fieldType);
        }
        
        // Create a new free record with this type
        return new RecordType(RecordVar.makeRecordVar(null, new HashSet<FieldName>(extensionFieldsMap.keySet()), TypeClass.NO_CLASS_CONSTRAINTS, false), extensionFieldsMap);
    }
    
    /**
     * Create a new type expression for a record-polymorphic type containing the 
     * specified fields and associated types.
     * 
     * Ex: makePolymorphicRecordType( [("age"->Double), ("name"->a)] ) 
     *     creates the record (r\age,r\name) => {r | age :: Double, name :: a}
     * Ex: makePolymorphicRecordType( [] ) creates the free record {r}
     * 
     * Note: The record fields will contain copies of the specified types, but referential equality 
     * will be preserved.
     * 
     * @param fieldNameToTypeMap Map (FieldName->TypeExpr) of field name to field type expression
     * @return RecordType the resulting record type 
     */
    public static RecordType makePolymorphicRecordType(Map<FieldName, TypeExpr> fieldNameToTypeMap) {
        
        // Copy the field type expressions, preserving referential equality 
        
        TypeExpr[] oldFieldTypes = fieldNameToTypeMap.values().toArray(new TypeExpr[fieldNameToTypeMap.values().size()]);
        TypeExpr[] newFieldTypes = copyTypeExprs(oldFieldTypes);
        List<TypeExpr> oldFieldTypesList = Arrays.asList(oldFieldTypes);
        List<TypeExpr> newFieldTypesList = Arrays.asList(newFieldTypes);
        
        // Check the extension field names, and make a copy of the map
        
        Map<FieldName, TypeExpr> newExtensionFieldsMap = new HashMap<FieldName, TypeExpr>(); //FieldName -> TypeExpr
        for (final Map.Entry<FieldName, TypeExpr> entry : fieldNameToTypeMap.entrySet()) {
                       
            FieldName fieldName = entry.getKey();            
            
            TypeExpr fieldType = entry.getValue();
            if (fieldType == null) {
                throw new IllegalArgumentException("The record field \"" + fieldName.getCalSourceForm() + "\" refers to a null type expression");
            }
            
            newExtensionFieldsMap.put(fieldName, newFieldTypesList.get(oldFieldTypesList.indexOf(fieldType)));
        }
        
        // Create a new free record with these fields
        return new RecordType(RecordVar.makeRecordVar(null, new HashSet<FieldName>(newExtensionFieldsMap.keySet()), TypeClass.NO_CLASS_CONSTRAINTS, false), newExtensionFieldsMap);
    }
    
    /**
     * Create a new type expression for a non-record-polymorphic type containing the 
     * specified fields and associated types.
     * 
     * For example,
     * makeNonPolymorphicRecordType([("age", Double), ("name", a)] ) == {age :: Double, name :: a}
     * makeNonPolymorphicRecordType([("age", Double), ("inner", (r\name) => {r | name :: String})])
     *     == (r\name)=>{age :: Double, inner :: {r | name :: String}}
     * makeNonPolymorphicRecordType([]) == {}
     * 
     * Note: The record fields will contain copies of the specified types, but referential equality 
     * will be preserved. 
     * 
     * @param fieldNameToTypeMap Map (FieldName->TypeExpr) of field name to field type expression
     * @return RecordType the resulting record type 
     */
    public static RecordType makeNonPolymorphicRecordType(Map<FieldName, TypeExpr> fieldNameToTypeMap) {
        
        // Copy the field type expressions, preserving referential equality 
        
        TypeExpr[] oldFieldTypes = fieldNameToTypeMap.values().toArray(new TypeExpr[fieldNameToTypeMap.values().size()]);
        TypeExpr[] newFieldTypes = copyTypeExprs(oldFieldTypes);
        List<TypeExpr> oldFieldTypesList = Arrays.asList(oldFieldTypes);
        List<TypeExpr> newFieldTypesList = Arrays.asList(newFieldTypes);
        
        // Check the extension field names, and make a copy of the map
        
        Map<FieldName, TypeExpr> newExtensionFieldsMap = new HashMap<FieldName, TypeExpr>();
        for (final Map.Entry<FieldName, TypeExpr> entry : fieldNameToTypeMap.entrySet()) {
                        
            FieldName fieldName = entry.getKey();           
            
            TypeExpr fieldType = entry.getValue();
            if (fieldType == null) {
                throw new IllegalArgumentException("The record field \"" + fieldName.getCalSourceForm() + "\" refers to a null type expression");
            }
            
            newExtensionFieldsMap.put(fieldName, newFieldTypesList.get(oldFieldTypesList.indexOf(fieldType)));
        }
        
        // Create a new free record with these fields
        return new RecordType(RecordVar.NO_FIELDS, newExtensionFieldsMap);
    }
    
    /**
     * Succeeds without throwing a TypeException if anotherTypeExpr can be specialized to this TypeExpr.
     * If it can, then anotherTypeExpr is thus specialized.
     * Creation date: (3/22/01 4:02:47 PM)
     * @param anotherTypeExpr
     * @param patternMatchContext  
     */
    abstract void patternMatch(TypeExpr anotherTypeExpr, PatternMatchContext patternMatchContext) throws TypeException;
    
    /**
     * Attempt to match inferredTypeExpr (binding the type variables occurring within it) to
     * declaredTypeExpr. During pattern matching, declaredTypeExpr is not modified at all.
     * Thus, unification really corresponds to two-way pattern matching.
     * Note that unlike the unifyType and canUnifyType methods, the order of arguments is significant.
     *
     * Creation date: (9/12/00 5:56:28 PM)
     * @param declaredTypeExpr type expression arising from a declaration e.g. head :: [Int] -> Int
     * @param inferredTypeExpr type expression arising from type inference e.g. for head it is [a] -> a
     * @param contextModuleTypeInfo context in which this pattern matching takes place. This is needed to know what class instances are in scope.
     * @throws TypeException
     */
    static void patternMatch(TypeExpr declaredTypeExpr, TypeExpr inferredTypeExpr, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {

        patternMatch(declaredTypeExpr, inferredTypeExpr, (NonGenericVars)null, contextModuleTypeInfo);
    }
    
    /**
     * Attempt to match inferredTypeExpr (binding the type variables occurring within it) to
     * declaredTypeExpr. During pattern matching, declaredTypeExpr is not modified at all.
     * Thus, unification really corresponds to two-way pattern matching.
     * Note that unlike the unifyType and canUnifyType methods, the order of arguments is significant.
     * <P>
     * This method is intended for pattern matching for local type declarations, where the list of 
     * non-generic type variables can be non-empty. A non-generic type variable in inferredTypeExpr
     * cannot be instantiated to a generic type variable in declaredTypeExpr. Note however, that it
     * is OK to instantiate a non-generic type variable to a type constructor.    
     * <P>
     * Creation date: (9/12/00 5:56:28 PM)
     * @param declaredTypeExpr type expression arising from a declaration e.g. head :: [Int] -> Int
     * @param inferredTypeExpr type expression arising from type inference e.g. for head it is [a] -> a
     * @param nonGenericVars the list of non-generic type variables when inferredTypeExpr is being defined.
     *          Can be null if there are no non-generic type variables.
     * @param contextModuleTypeInfo
     * @throws TypeException
     */
    static void patternMatch(TypeExpr declaredTypeExpr, TypeExpr inferredTypeExpr, NonGenericVars nonGenericVars, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {

        if (contextModuleTypeInfo == null) {
            throw new NullPointerException();
        }
               
        declaredTypeExpr = declaredTypeExpr.prune();
        inferredTypeExpr = inferredTypeExpr.prune();        
                    
        PatternMatchContext patternMatchContext = new PatternMatchContext(declaredTypeExpr, nonGenericVars, contextModuleTypeInfo);
        declaredTypeExpr.patternMatch(inferredTypeExpr, patternMatchContext);
    }
    
    /**
     * If types1 = [t1, ..., tn], and types2 = [u1, ..., un], then this function 
     * checks if types2 can be specialized to types1 i.e. u1 can be specialized to t1,
     * u2 can be specialized to t2, ...
     * 
     * What this really means: we can assign types to the type variables occurring in types2,
     * (other than those also occurring in types1) so that u1(with assignments) == t1, 
     * u2(with assignments) == t2, ...
     *                    
     * There are no side-effects on the arguments.
     * 
     * The TypeExpr elements of the returned array have no instantiated type variables within them.
     * 
     * This function correctly handles shared type variables i.e. the same type variable appearing
     * in types1 and in types2. 
     * 
     * The types2 array may be longer than the types1 array. For example: 
     * patternMatchTypePieces ([Int], [a, a -> Char, Maybe a]) = [Int, Int -> Char, Maybe Int]. 
     * Another example:
     * patternMatchTypePieces ([c -> c], [a -> b, [a -> b]]) = [d -> d, [d -> d]].   
     * The types1 array may have null values. For example:
     * patternMatchTypePieces ([null, Int], [a -> Char, a])  = [Int -> Char, Int].
     * One can think of types1 as specifying constraints on types2, and there might not be a constraint for each type
     * expression in the types2 array. The returned value array, which has the same length
     * as types2, is types2 where the pattern matching constraints have been taken into account. 
     *      
     * @param types1 an array whose length is <= the length of types1 and may contain null values
     * @param types2 an array whose length is >= the length of types1, and does not contain null values
     * @param contextModuleTypeInfo
     * @return TypeExpr[] an array having the same length as types2.
     * @throws TypeException
     */
    public static TypeExpr[] patternMatchPieces(TypeExpr[] types1, TypeExpr[] types2, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
        if (contextModuleTypeInfo == null) {
            throw new NullPointerException();
        }
        
        int nTypes1 = types1.length;
        int nTypes2 = types2.length;
        if (nTypes1 > nTypes2) {
            throw new IllegalArgumentException();        
        }
        
        //types2 may not contain null values
        if (TypeExpr.hasNullType(types2)) {
            throw new IllegalArgumentException();
        }       
                
        //so at this point nTypes2 >= nTypes1.
        
        //copy as a pair since there may be referentially identical type variables in types1 and types2.
        CopyEnv.TypesPair copyTypesPair = TypeExpr.copyTypesPair(types1, types2);
        TypeExpr[] copyTypes1 = copyTypesPair.getTypes1();
        TypeExpr[] copyTypes2 = copyTypesPair.getTypes2();
        
        for (int i = 0; i < nTypes1; ++i) {
            
            if (copyTypes1[i] != null) {            
                TypeExpr.patternMatch(copyTypes1[i], copyTypes2[i], contextModuleTypeInfo);
            }
        }
        
        for (int i = 0; i < nTypes2; ++i) {
            copyTypes2[i] = copyTypes2[i].deepPrune();            
        }
        
        return copyTypes2;                              
    }
    
    /**
     * Returns a TypeExpr without redundant instantiated type variables at the top of this TypeExpr.
     * The result of prune is always a non-instantiated type variable, a type constructor or a 
     * record type.
     * 
     * This function has no side-effects.
     *
     * Creation date: (7/17/00 3:59:38 PM)
     * @return TypeExpr the pruned type expression
     */
    abstract TypeExpr prune();
    
    /**
     * Similar to prune, except that the arguments (if any) are also pruned. 
     * Also record variables are pruned.
     * 
     * Another important difference is that this function has side effects- this TypeExpr is
     * modified to not have instantiation chains (except for possibly at the root of the TypeExpr
     * tree if it happens to be an instantiated type variable).
     * 
     * Creation date: (18/07/01 1:15:10 PM)
     * @return TypeExpr a TypeExpr representing the same Type as this TypeExpr, but without any
     *    instantiated type or record variables in its tree representation.
     */
    abstract TypeExpr deepPrune();
    
    /**
     * Returns the most compact and efficient representation of a TypeExpr
     * within the equivalence class of TypeExpr objects that represent the same type.
     * 
     * Technically, this function removes redundant instantiated type and record variables
     * as well as simplifies TypeApp nodes that could be written as TypeConsApp.
     * For example, (TypeApp (TypeApp (TypeConsApp Function []) Int) Char) is replaced 
     * by its equivalent form (TypeConsApp Function [Int, Char]). 
     * 
     * This function has no side-effects. However, the type are record variables
     * of this TypeExpr and the returned TypeExpr are shared, so modification of one e.g.
     * via instantiations will also modify the other.
     * 
     * Creation date: (18/07/01 1:15:10 PM)
     * @return TypeExpr a TypeExpr representing the same type as this TypeExpr, but using the
     *    simplest representative in the equivalence class.
     */
    abstract TypeExpr normalize();
    
    /**
     * Checks if 2 types are equal, up to renamings of type variables. For example, the types:
     * "a -> b -> [a] -> [b]", "b -> a -> [b] -> [a]" and "c -> d -> [c] -> [d]" would all be
     * considered to be the same type under this method. 
     * 
     * By definition, 2 types t1 and t2 are the same via this method if t1.toString().equals(t2.toString()).
     * However, this method will be significantly faster in many cases than comparing string representations,
     * especially in the case when the 2 types are different.        
     */
    public abstract boolean sameType(TypeExpr anotherTypeExpr);    
      
    /**
     * Returns the address of this object. Primarily intended for debugging purposes.
     * Creation date: (6/6/01 4:29:53 PM)
     * @return String
     */
    final String toAddressString() {
        return "" + super.hashCode();       
    }
    
    /**
     * Returns a string representation of this TypeExpr that also indicates the address of each
     * type variable appearing in the TypeExpr. 
     * @return String
     */  
    final String toDebugString() {
                                    
        StringBuilder debugTypeString = new StringBuilder();
        
        Set<TypeVar> typeVarsSet = getUninstantiatedTypeVars();
        int nVars = typeVarsSet.size();
        if (nVars > 0) {  
            debugTypeString.append('<');      
            int varN = 0;
            for (final TypeVar typeVar : typeVarsSet) {
                
                debugTypeString.append(PolymorphicVarContext.indexToVarName(varN + 1)).append("=").append(typeVar.toAddressString());
                if (varN < nVars - 1) {
                    debugTypeString.append(", ");
                }
                ++varN;
            }
            debugTypeString.append("> ");
        }
        
        debugTypeString.append(toString());
        
        return debugTypeString.toString();        
    }
                         
    /**
     * Returns a string representation of this TypeExpr. Type and record variables are represented
     * by lower case letters: a, b, c, ..., z, a1, a2, a3,... according to their first occurrence in the
     * non-context part of this TypeExpr. Type constructor and type class names appearing in the type 
     * are fully qualified.
     * <p>
     * The String representation will be the same for equivalent type expressions i.e. ones that represent
     * the "same" type (where type variables are all considered to be quantified over the type itself).
     * Some defining characteristics:
     * <ul>
     *     <li>the resulting String is parseable as a CAL type.</li>
     *     <li>redundant parentheses are not displayed; all parentheses displayed are required for a correct parse</li>
     *     <li>If Prelude.Function or Prelude.List are fully saturated, then they will appear in their symbolic forms e.g.
     *         (Prelude.Int -> Prelude.Char rather than the equivalent Prelude.Function Prelude.Int Prelude.Char
     *         and [Prelude.String] rather than the equivalent Prelude.List Prelude.String).</li>
     *     <li>records will use tuple notation when possible, and otherwise use field-name notation where the fields are
     *         displayed in field-name order.</li>
     *  </ul>
     *  <p>
     * Creation date: (3/26/01 2:26:03 PM)
     * @return a canonical String representation of this TypeExpr
     */
    @Override
    public final String toString() {        
        return toString((PolymorphicVarContext)null, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
    }
    
    /**
     * @see TypeExpr#toString(boolean, ScopedEntityNamingPolicy)
     */
    public final String toString(ScopedEntityNamingPolicy namingPolicy) {
        return toString((PolymorphicVarContext)null, namingPolicy);
    }
    
    /**
     * A version of TypeExpr.toString that provides more options for displaying the String in a more human-readable format.
     * 
     * @param favorPreferredPolymorphicVarNames if true, then the preferred names of type are record variables will be used
     *      if possible. The definition of 'if possible' is subject to change so this is more of a preference to display prettier 
     *      type strings in client code. if false, then type and record variables are represented by lower case letters:
     *       a, b, c, ..., z, a1, a2, a3,... according to their first occurrence in the non-context part of this TypeExpr
     * @param namingPolicy naming policy influencing how type constructor and type class names occurring within this TypeExpr
     *           are displayed in the returned value.
     * @return a String representation of this TypeExpr
     */
    public final String toString(boolean favorPreferredPolymorphicVarNames, ScopedEntityNamingPolicy namingPolicy) {
        
        PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make(favorPreferredPolymorphicVarNames);
        
        if (favorPreferredPolymorphicVarNames) {
            indexPolymorphicVars(polymorphicVarContext);
        }
        
        return toString(polymorphicVarContext, namingPolicy);
    }
    
    /**
     * Similar to TypeExpr#toSourceModel(boolean, ScopedEntityNamingPolicy). Type are record variables are scoped over the
     * whole TypeExpr array argument, and so sharing between the different types in the array is properly shown.
     * 
     * @see TypeExpr#toString(boolean, ScopedEntityNamingPolicy)
     * @param types
     * @param favorPreferredPolymorphicVarNames
     * @param namingPolicy
     * @return String[]
     */
    public static final String[] toStringArray(
        TypeExpr[] types,
        boolean favorPreferredPolymorphicVarNames,
        ScopedEntityNamingPolicy namingPolicy) {
        
        PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make(favorPreferredPolymorphicVarNames);
       
        final int nTypes = types.length;
        
        if (favorPreferredPolymorphicVarNames) {
            for (int i = 0; i < nTypes; ++i) {
                types[i].indexPolymorphicVars(polymorphicVarContext);
            }
        }
               
        String[] typeStrings = new String[nTypes];
        for (int i = 0; i < nTypes; ++i) {
            typeStrings[i] = types[i].toString(polymorphicVarContext, namingPolicy);
        }
        
        return typeStrings;
    }
           
    /**
     * Returns a string representation of this TypeExpr. Type and record variables are represented
     * by lower case letters: a, b, c, ..., z, a1, a2, a3,... according to their first occurrence in the
     * polymorphicVarContext argument. 
     * <p>
     * Creation date: (3/26/01 2:26:03 PM)
     * @param polymorphicVarContext object threaded through nested toString invocations
     *        to track the type and record variables within this TypeExpr. Can be null. If not null, then any
     *        uninstantiated type and record variables occurring in this TypeExpr that are not already
     *        in the context are added.
     * @param namingPolicy
     * @return the resultant string representation
     */
    public final String toString(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {

        // create a new PolymorphicVarContext if necessary
        if (polymorphicVarContext == null) {
            polymorphicVarContext = PolymorphicVarContext.make();
        }
        
        if (namingPolicy == null) {
            namingPolicy = ScopedEntityNamingPolicy.FULLY_QUALIFIED;           
        }             
              
        // generate the type string, without including the context (the stuff to the left of =>).
        StringBuilder sb = new StringBuilder();
        toSourceText(sb, polymorphicVarContext, ParenthesizationInfo.NONE, namingPolicy);
        String typeString = sb.toString();
     
        // now generate the context string  
        String contextString = makeContextString(polymorphicVarContext, namingPolicy);
        
        return contextString + typeString;
    }         

    /**
     * Returns a source model representation of this TypeExpr.
     * 
     * @return SourceModel.TypeSignature the resultant source model representation
     */
    public final SourceModel.TypeSignature toSourceModel() {
        return toSourceModel((PolymorphicVarContext)null, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
    }
    
    /**
     * Returns a SourceModel representation of this TypeExpr, but with more control about the formatting use
     * for type and record variables as well as type constructors and type classes occurring within the TypeExpr.
     * @param favorPreferredPolymorphicVarNames if true, then the preferred names of type are record variables will be used
     *      if possible. The definition of 'if possible' is subject to change so this is more of a preference to display prettier 
     *      type strings in client code. if false, then type and record variables are represented by lower case letters:
     *       a, b, c, ..., z, a1, a2, a3,... according to their first occurrence in the non-context part of this TypeExpr
     * @param namingPolicy naming policy influencing how type constructor and type class names occurring within this TypeExpr
     *           are displayed in the returned value.
     * @return SourceModel.TypeSignature the resultant source model representation
     */
    public final SourceModel.TypeSignature toSourceModel(boolean favorPreferredPolymorphicVarNames, ScopedEntityNamingPolicy namingPolicy) {
        PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make(favorPreferredPolymorphicVarNames);
        if (favorPreferredPolymorphicVarNames) {
            indexPolymorphicVars(polymorphicVarContext);
        }
                
        return toSourceModel(polymorphicVarContext, namingPolicy);         
    }
    
    /**
     * Similar to TypeExpr#toSourceModel(boolean, ScopedEntityNamingPolicy). Type are record variables are scoped over the
     * whole TypeExpr array argument, and so sharing between the different types in the array is properly shown.
     * 
     * @see TypeExpr#toSourceModel(boolean, ScopedEntityNamingPolicy)
     * @param types
     * @param favorPreferredPolymorphicVarNames
     * @param namingPolicy
     * @return SourceModel.TypeSignature[]
     */
    public static final SourceModel.TypeSignature[] toSourceModelArray(
        TypeExpr[] types,
        boolean favorPreferredPolymorphicVarNames,
        ScopedEntityNamingPolicy namingPolicy) {
        
        PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make(favorPreferredPolymorphicVarNames);
       
        final int nTypes = types.length;
        
        if (favorPreferredPolymorphicVarNames) {
            for (int i = 0; i < nTypes; ++i) {
                types[i].indexPolymorphicVars(polymorphicVarContext);
            }
        }
               
        SourceModel.TypeSignature[] typeSourceModels = new SourceModel.TypeSignature[nTypes];
        for (int i = 0; i < nTypes; ++i) {
            typeSourceModels[i] = types[i].toSourceModel(polymorphicVarContext, namingPolicy);
        }
        
        return typeSourceModels;
    }    
    
    
    /**
     * Returns a source model representation of this TypeExpr.
     * @param polymorphicVarContext object threaded through nested toString invocations
     *        to track the type and record variables within this TypeExpr. Can be null. If not null, then any
     *        uninstantiated type and record variables occurring in this TypeExpr that are not already
     *        in the context are added.
     * @param namingPolicy NamingPolicy to use when generating names 
     * @return SourceModel.TypeSignature the resultant source model representation
     */
    final SourceModel.TypeSignature toSourceModel(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {
        // create a new PolymorphicVarContext if necessary
        if (polymorphicVarContext == null) {
            polymorphicVarContext = PolymorphicVarContext.make();
        }
        
        if (namingPolicy == null) {            
            namingPolicy = ScopedEntityNamingPolicy.FULLY_QUALIFIED;           
        } 
                             
        // generate the source model for the type, without including the context (the stuff to the left of =>).
        SourceModel.TypeExprDefn typeSourceModel = makeDefinitionSourceModel(polymorphicVarContext, namingPolicy);
        
        // now generate the context portion
        SourceModel.Constraint[] contextSourceModel = makeContextSourceModel(polymorphicVarContext, namingPolicy);
        
        return SourceModel.TypeSignature.make(contextSourceModel, typeSourceModel);
    }

    /**
     * A helper function that returns all the constrained type and record variables occurring in
     * this TypeExpr, ordered by first occurrence.
     * 
     * TypeVars and RecordVars can technically live in a separate namespace, but for reasons of
     * simplicity we share the namespace i.e. we could technically allow:
     * (a\field1, Num a) => {a | } -> {a | field1 :: a}
     * but actually require
     * (a\field1, Num b) => {a | } -> {a | field1 :: b}
     * for reasons of end-user simplicity in reading String representations of type expressions.
     * 
     * @return (Set (Either TypeVar RecordVar))
     */
    final Set<PolymorphicVar> getConstrainedPolymorphicVars(){
        PolymorphicVarContext localizedVarContext = PolymorphicVarContext.make();
        //called for the side effect of populating localizedVarContext
        toSourceText(new StringBuilder(), localizedVarContext, ParenthesizationInfo.NONE, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        return localizedVarContext.getConstrainedPolymorphicVars();     
    }
    
    /**
     * Build a string representation of the context of a type expression with a given PolymorphicVarContext
     * that determines how the class constraints (on type variables) and the lacks constraints (on record variables)
     * will be displayed.
     * @param polymorphicVarContext object threaded through nested toString invocations
     *        to track the type and record variables within this TypeExpr. 
     * @param namingPolicy
     * @return the string representation      
     */
    private final String makeContextString(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {
         
        Set<PolymorphicVar> contextPolymorphicVars = this.getConstrainedPolymorphicVars();

        List<String> contextList = new ArrayList<String>();
            
        for (final PolymorphicVar polymorphicVar : contextPolymorphicVars) {
                  
            if (polymorphicVar instanceof TypeVar) { 
                         
                TypeVar typeVar = (TypeVar)polymorphicVar;
               
                String typeVarName = polymorphicVarContext.getPolymorphicVarName(typeVar);                              
                if (DEBUG_INFO) {
                    typeVarName += typeVar.toAddressString();
                }
                
                if (typeVar.noClassConstraints()) {
                    //there is a bug in getGenericConstrainedTypeVars...
                    throw new IllegalStateException();
                }
                           
                for (final TypeClass typeClass : typeVar.getTypeClassConstraintSet()) {
                                    
                    String typeClassName = namingPolicy.getName(typeClass);
                    contextList.add(typeClassName + " " + typeVarName);
                } 
                
            } else if (polymorphicVar instanceof RecordVar) {
                
                RecordVar recordVar = (RecordVar)polymorphicVar;
                                                                            
                String recordVarName = polymorphicVarContext.getPolymorphicVarName(recordVar);                 
                
                //add the type class constraints
                
                for (final TypeClass typeClass : recordVar.getTypeClassConstraintSet()) {
                                
                     String typeClassName = namingPolicy.getName(typeClass);
                     contextList.add(typeClassName + " " + recordVarName);
                } 
                
                //add the lacks fields constraints
                
                Set<FieldName> lacksFieldsSet = recordVar.getLacksFieldsSet();
                List<String> lacksConstraints = new ArrayList<String>(); 
                for (final FieldName lacksFieldName : lacksFieldsSet) {
                    
                    lacksConstraints.add(recordVarName + "\\" + lacksFieldName.getCalSourceForm());
                }
                
                //need a canonical order for the lacks fields. This is alphabetical order.
                Collections.sort(lacksConstraints);
                
                contextList.addAll(lacksConstraints);
                                                  
            } else {
                throw new IllegalStateException();
            }
        }

        int nContexts = contextList.size();

        if (nContexts == 0) {        
            return "";
        }      

        StringBuilder result = new StringBuilder();

        if (nContexts > 1) {       
            result.append("(");
        }     

        for (int i = 0; i < nContexts; ++i) {
            
            if (i > 0) {            
                result.append(", ");
            }            
            result.append(contextList.get(i));
        }

        if (nContexts > 1) {        
            result.append(")");
        }

        result.append(" => ");

        return result.toString();
    }
    
    /**
     * Build a source model representation of the context of a type expression
     * with a given PolymorphicVarContext that determines how the class
     * constraints (on type variables) and the lacks constraints (on record
     * variables) will be displayed.
     * 
     * @param polymorphicVarContext
     *            object threaded through nested makeDefinitionSourceModel
     *            invocations to track the type and record variables within this
     *            TypeExpr.
     * @param namingPolicy NamingPolicy to use when constructing names           
     * @return the source model representation of the context
     */
    private final SourceModel.Constraint[] makeContextSourceModel(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {
        
        Set<PolymorphicVar> contextPolymorphicVars = this.getConstrainedPolymorphicVars();

        List<SourceModel.Constraint> contextList = new ArrayList<SourceModel.Constraint>();
            
        for (final PolymorphicVar polymorphicVar : contextPolymorphicVars) {
           
            if (polymorphicVar instanceof TypeVar) { 
                         
                TypeVar typeVar = (TypeVar)polymorphicVar;
                                      
                String typeVarName = polymorphicVarContext.getPolymorphicVarName(typeVar);   
                if (DEBUG_INFO) {
                    typeVarName += typeVar.toAddressString();
                }
                
                if (typeVar.noClassConstraints()) {
                    //there is a bug in getGenericConstrainedTypeVars...
                    throw new IllegalStateException();
                }
                           
                for (final TypeClass typeClass : typeVar.getTypeClassConstraintSet()) {
                    
                    final ModuleName typeClassModuleName = namingPolicy.getModuleNameForScopedEntity(typeClass);
                    
                    SourceModel.Name.TypeClass typeClassName =
                        SourceModel.Name.TypeClass.make(typeClassModuleName, typeClass.getName().getUnqualifiedName());
                    
                    contextList.add(SourceModel.Constraint.TypeClass.make(typeClassName, SourceModel.Name.TypeVar.make(typeVarName)));
                } 
                
            } else if (polymorphicVar instanceof RecordVar) {
                
                RecordVar recordVar = (RecordVar)polymorphicVar;
                                                                            
                String recordVarName = polymorphicVarContext.getPolymorphicVarName(recordVar);              
               
                //add the type class constraints
                
                for (final TypeClass typeClass : recordVar.getTypeClassConstraintSet()) {
                                                            
                    final ModuleName typeClassModuleName = namingPolicy.getModuleNameForScopedEntity(typeClass);
                    
                    SourceModel.Name.TypeClass typeClassName =
                        SourceModel.Name.TypeClass.make(typeClassModuleName, typeClass.getName().getUnqualifiedName());
                    
                    contextList.add(SourceModel.Constraint.TypeClass.make(typeClassName, SourceModel.Name.TypeVar.make(recordVarName)));
                } 
                
                //add the lacks fields constraints
                
                Set<FieldName> lacksFieldsSet = recordVar.getLacksFieldsSet();
                List<SourceModel.Constraint.Lacks> lacksConstraints = new ArrayList<SourceModel.Constraint.Lacks>();
                for (final FieldName lacksFieldName : lacksFieldsSet) {                   
                    lacksConstraints.add(SourceModel.Constraint.Lacks.make(SourceModel.Name.TypeVar.make(recordVarName), SourceModel.Name.Field.make(lacksFieldName)));
                }
                
                //need a canonical order for the lacks fields. This is alphabetical order.
                Collections.sort(lacksConstraints, new Comparator<SourceModel.Constraint.Lacks>() {
                    /** {@inheritDoc} */
                    public int compare(SourceModel.Constraint.Lacks o1, SourceModel.Constraint.Lacks o2) {
                        return o1.toSourceText().compareTo(o2.toSourceText());
                    }
                });
                
                contextList.addAll(lacksConstraints);
                                                  
            } else {
                throw new IllegalStateException();
            }
        }

        return contextList.isEmpty() ? null : contextList.toArray(new SourceModel.Constraint[0]);
    }

    /**
     * Build a textual representation of this type expression without including the context (the stuff to the left of =>).
     * Creation date: (10/11/00 5:52:57 PM)
     * @param sb StringBuilder to use to accumulate the resulting source text.
     * @param polymorphicVarContext object threaded through nested toString invocations
     *        to track the type and record variables within this TypeExpr. 
     * @param parenthesizationInfo info on whether to parenthesize the root node of this type expression from the point of view
     *        of the parent of this node in the TypeExpr tree.
     * @param namingPolicy how to display type constructors e.g. whether unqualified, fully qualified etc.
   
     */
    abstract void toSourceText(StringBuilder sb,
        PolymorphicVarContext polymorphicVarContext,
        ParenthesizationInfo parenthesizationInfo,     
        ScopedEntityNamingPolicy namingPolicy);
    
    /**
     * Builds a source model representation of this type expression without
     * including the context (the stuff to the left of =>).
     * 
     * @param polymorphicVarContext
     *            object threaded through nested makeDefinitionSourceModel
     *            invocations to track the type and record variables within this
     *            TypeExpr.
     * @param namingPolicy ScopedEntityNamingPolicy to use when creating names
     * @return the source model representation of the type expression definition
     *         (without the context)
     */
    abstract SourceModel.TypeExprDefn makeDefinitionSourceModel(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy);

    /**
     * A helper function for unifying this type expression with another type expression.
     * @param anotherTypeExpr
     * @param contextModuleTypeInfo
     * @return int type closeness. See the javadoc for TypeExpr.getTypeCloseness for a detailed description of the type closeness heuristic.
     */
    abstract int unifyType(TypeExpr anotherTypeExpr, ModuleTypeInfo contextModuleTypeInfo) throws TypeException;
    
    /**
     * Unify two type expressions. What this means is to find the most general type to which both
     * typeExpr1 and typeExpr2 can be specialized to. Unification fails when trying to match 2 different
     * type constructors (such as Boolean and Int) or when trying to instantiate a type variable to a term
     * containing that type variable (like a and a->b, where a circular structure would be built.)
     * As a general point of interest, this process is essentially the same as unification in Prolog.
     * <p>
     * Note that this function has side effects and modifies the TypeExpr arguments. If it fails in a TypeException,
     * the modification is undefined. Otherwise, the modification is the unification.
     * <p>
     * Creation date: (7/21/00 1:27:22 PM)
     * @param typeExpr1
     * @param typeExpr2
     * @param contextModuleTypeInfo context in which this unification takes place. This is needed to know what class instances are in scope.
     * @return int a measure of the closeness between the 2 argument types. The larger the number, the closer
     *  the 2 types are. This value should not be persisted and may change in the future. It is intended for use
     *  by UI clients such as Intellicut in filtering the list of possible candidates to display in the pick list
     *  of gems to those that are a closer match. This number is not scaled. What this means is that you can't
     *  interpret a closeness of 3 as a having a fixed meaning. In other words, if you fix the first argument,
     *  unifyType can be considered as a function from TypeExpr -> Int and the maximum and minimum values attained
     *  by this function of one variable on its domain gives the interpretation of the closeness magnitude for 
     *  that particular first argument.  See the javadoc for TypeExpr.getTypeCloseness for a detailed description of the type closeness heuristic.
     */
    static int unifyType(TypeExpr typeExpr1, TypeExpr typeExpr2, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
    
        if (contextModuleTypeInfo == null) {
            throw new NullPointerException();
        }

        typeExpr1 = typeExpr1.prune();
        typeExpr2 = typeExpr2.prune();

        return typeExpr1.unifyType(typeExpr2, contextModuleTypeInfo);
    }
    
    /**
     * Similar to TypeExpr.unifyType except there are no side effects on the argument type expressions.
     * The returned TypeExpr has no instantiated type variables within it.
     * <p>
     * Note: this function correctly handles the case where typeExpr1 and typeExpr2 have shared type variables.
     * For example, we can't unify (a, Int) and (Char, a), but we can unify (a, Int) and (Char, a').
     * @param typeExpr1
     * @param typeExpr2
     * @param contextModuleTypeInfo
     * @return TypeExpr
     * @throws TypeException
     */
    public static TypeExpr unify(TypeExpr typeExpr1, TypeExpr typeExpr2, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
        //Note: we copy both type expressions simultaneously since there may be referentially
        //identical type variables in common.
        //So for example, (a, Int) and (Char, a) can't unify whereas (a, Int) and (Char, a') can. 
        TypeExpr[] copiedTypes = TypeExpr.copyTypeExprs(new TypeExpr[] {typeExpr1, typeExpr2});
        TypeExpr typeExprCopy1 = copiedTypes[0];
        TypeExpr typeExprCopy2 = copiedTypes[1];
        
        TypeExpr.unifyType(typeExprCopy1, typeExprCopy2, contextModuleTypeInfo);
        
        return typeExprCopy1.deepPrune();               
    }
    
    /**
     * If types1 = [t1, ..., tn], and types2 = [u1, ..., un], then this function unifies t1, u1, then t2, u2,
     * ... and then tn, un, returning a copy of the result.
     * <p>
     * There are no side-effects on the arguments.
     * <p>
     * The TypeExpr elements of the returned array have no instantiated type variables within them.
     * <p>
     * This function correctly handles shared type variables. For example:
     * [a -> b, a, (a, b, Boolean)] and [Int -> Double, a, (a, c, d)] unifies to [Int -> Double, Int, (Int, Double, Boolean)].
     * 
     * The types2 array may be longer than the types1 array. For example: 
     * unifyTypePieces ([a], [Int, a -> Char]) = [Int, Int -> Char].    
     * The types1 array may have null values. For example:
     * unifyTypePieces ([null, a], [a -> Char, Int])  = [Int -> Char, Int].
     * One can think of types1 as specifying constraints on types2, and there might not be a constraint for each type
     * expression in the types2 array. The returned value array, which has the same length
     * as types2, is types2 where the unification constraints from types1 have been taken into account. 
     *      
     * @param types1 an array whose length is <= the length of types2, and may contain null values
     * @param types2 an array whose length is >= the length of types and does not contain null values      
     * @param contextModuleTypeInfo
     * @return TypeExpr[] an array having the same length as types2.
     * @throws TypeException
     */
    public static TypeExpr[] unifyTypePieces(TypeExpr[] types1, TypeExpr[] types2, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
        if (contextModuleTypeInfo == null) {
            throw new NullPointerException();
        }
        
        int nTypes1 = types1.length;
        int nTypes2 = types2.length;
        if (nTypes1 > nTypes2) {        
            throw new IllegalArgumentException();        
        }
        
        //types2 may not contain null values
        if (TypeExpr.hasNullType(types2)) {
            throw new IllegalArgumentException();
        }      
                
        //so at this point nTypes2 >= nTypes1.
        
        //copy as a pair since there may be referentially identical type variables in types1 and types2.
        CopyEnv.TypesPair copyTypesPair = TypeExpr.copyTypesPair(types1, types2);
        TypeExpr[] copyTypes1 = copyTypesPair.getTypes1();
        TypeExpr[] copyTypes2 = copyTypesPair.getTypes2();
        
        for (int i = 0; i < nTypes1; ++i) {
            
            if (copyTypes1[i] != null) {            
                TypeExpr.unifyType(copyTypes1[i], copyTypes2[i], contextModuleTypeInfo);
            }
        }
        
        for (int i = 0; i < nTypes2; ++i) {
            copyTypes2[i] = copyTypes2[i].deepPrune();            
        }
        
        return copyTypes2;                              
    }
    
    /**     
     * @param types
     * @return boolean true if any of the elements in the types array are null.
     */
    static boolean hasNullType(TypeExpr[] types) {
        for (int i = 0, nTypes = types.length; i < nTypes; ++i) {
            if (types[i] == null) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Method usesForeignType.
     * @return boolean return true if any of the type constructors in the type was created with a foreign data declaration     
     */
    abstract public boolean usesForeignType ();    
    
    /**
     * The idea here is that unconstrainedTypes can be specialized to oldSpecializedTypes (such as when the inputs of a gem are specialized
     * by value panels), and someone has changed one of the oldSpecializedTypes, at index switchIndex to switchedType. Then what is the best
     * updatedSpecializedTypes that can be computed? In other words, unconstrainedTypes can be specialized to updatedSpecializedTypes, 
     * updatedSpecializedTypes[switchIndex] is equal to switchType, and unconstrainedTypes has all the specializations of oldSpecializedTypes
     * that are not invalidated by switchType. For example:
     * 
     * getUpdatedSpecializedTypes((Char, Long), 1, [(Int, Boolean), (Int, Double)], [(a, b), (a, c)]) == [(Char, Boolean), (Char, Long)].
     * 
     * @param switchedType
     * @param switchIndex
     * @param oldSpecializedTypes
     * @param unconstrainedTypes
     * @param currentModuleTypeInfo
     * @return TypeExpr[] null if the specialization relationships described above between the arguments of this function do not hold
     */
    public static TypeExpr[] getUpdatedSpecializedTypes(TypeExpr switchedType, int switchIndex, TypeExpr[] oldSpecializedTypes, TypeExpr[] unconstrainedTypes, ModuleTypeInfo currentModuleTypeInfo) {
       
        TypeExpr unconstrainedSwitchType = unconstrainedTypes[switchIndex];
           
        if (!TypeExpr.canPatternMatchPieces(oldSpecializedTypes, unconstrainedTypes, currentModuleTypeInfo) ||
            !TypeExpr.canPatternMatch(switchedType, unconstrainedSwitchType, currentModuleTypeInfo)) {
            return null;  
        }
                    
        // Returns a map going from the typeVars in the unconstrained type that the value was connected with, 
        //   to their associated TypeExprs in the newValueNode type.
        Map<TypeExpr, TypeExpr> affectedTypeVarToNewTypeMap = getTypeVarCorrespondenceMap(unconstrainedSwitchType, switchedType);
                    
        int nTypes = unconstrainedTypes.length;                    

        // Now construct the map from type var to its specialization.
        // First populate with its former specialization..
        Map<TypeExpr, TypeExpr> typeVarToSpecializationMap = new HashMap<TypeExpr, TypeExpr>();
        for (int i = 0; i < nTypes; ++i) {
            // Map type vars in the unconstrained type to copied corresponding type in the old value.
            Map<TypeExpr, TypeExpr> typeVarToOldSpecializedTypeMap = getTypeVarCorrespondenceMap(unconstrainedTypes[i], oldSpecializedTypes[i]);
            typeVarToSpecializationMap.putAll(typeVarToOldSpecializedTypeMap);

        }
        // Now add new specializations to ensure these take precedence.
        typeVarToSpecializationMap.putAll(affectedTypeVarToNewTypeMap);
        
        
        // Copy the type specializations, so we can use them in the return values.
        
        // First create of the type var specializations, in the same order that they appear in an iterator over the map keys.
        List<TypeExpr> typeVarSpecializationList = new ArrayList<TypeExpr>();
        for (final Map.Entry<TypeExpr, TypeExpr> entry : typeVarToSpecializationMap.entrySet()) {
            final TypeExpr specialization = entry.getValue();
            typeVarSpecializationList.add(specialization);
        }
        
        // Convert to an array, make a copy.
        TypeExpr[] typeVarSpecializationArray = typeVarSpecializationList.toArray(new TypeExpr[typeVarSpecializationList.size()]);
        TypeExpr[] typeVarSpecializationArrayCopy = copyTypeExprs(typeVarSpecializationArray);
        
        // Create a map from the type var to the copied specialized type.
        Map<TypeExpr, TypeExpr> typeVarToSpecializationCopyMap = new HashMap<TypeExpr, TypeExpr>();
        int index = 0;
        for (final TypeExpr typeVar : typeVarToSpecializationMap.keySet()) {
            typeVarToSpecializationCopyMap.put(typeVar, typeVarSpecializationArrayCopy[index]);
            index++;
        }
        
        
        // The array of updated specializations - starts out as a copy of the unconstrained types, has its type vars specialized.
        TypeExpr[] updatedSpecializedTypes = copyTypeExprs(unconstrainedTypes);
        
        // Create a map from type vars in the original to type vars in the copy.
        Map<TypeExpr, TypeExpr> typeVarToTypeVarCopyMap = new HashMap<TypeExpr, TypeExpr>();
        for (int i = 0; i < nTypes; i++) {
            typeVarToTypeVarCopyMap.putAll(getTypeVarCorrespondenceMap(unconstrainedTypes[i], updatedSpecializedTypes[i]));
        }

        // Specialize the type variables in the copy to the new types.
        for (final Map.Entry<TypeExpr, TypeExpr> entry : typeVarToSpecializationCopyMap.entrySet()) {
            TypeExpr affectedTypeVar = entry.getKey();
            TypeExpr affectedTypeVarCopy = typeVarToTypeVarCopyMap.get(affectedTypeVar);
            TypeExpr specializeTypeCopy = entry.getValue();

            try {
                TypeExpr.patternMatch(specializeTypeCopy, affectedTypeVarCopy, currentModuleTypeInfo);
            } catch (TypeException e) {                   
                return null;
            }
        }

        // Fully-prune..
        for (int i = 0; i < nTypes; ++i) {
            updatedSpecializedTypes[i] = updatedSpecializedTypes[i].deepPrune();       
        }
        
        return updatedSpecializedTypes;
    }
    
    /**
     * For use in finding associations between type vars in a given type and their corresponding types in a 
     *   specialization of that type.
     * 
     * Note: it is assumed that canPatternMatch(correspondingType, typeToMap).
     * 
     * Example:
     *           typeToMap: (a, (Double, b))
     *   correspondingType: ((Foo, c), (Double, [d]))
     *  resulting mappings: a -> (Foo, c)
     *                      b -> [d]
     * 
     * How do you find that 'a' corresponds to 'x'? You use this method.
     * Once you know that 'a' corresponds to 'x', then you can find all other values that corresponds to 'a' as well, and
     * manipulate them as necessary. 
     *  
     * @param typeToMap the type expr from which to map type vars.
     * @param correspondingType the corresponding type expr for which type vars from typeToMap will be mapped.  
     *   This will be at least as specialized as the type to map.
     * @return Map (TypeExpr->TypeExpr) from type var in typeToMap to the corresponding type expr in corresponding type.
     */
    private static Map<TypeExpr, TypeExpr> getTypeVarCorrespondenceMap(TypeExpr typeToMap, TypeExpr correspondingType) {
        Map<TypeExpr, TypeExpr> returnMap = new HashMap<TypeExpr, TypeExpr>();
        TypeExpr.getTypeVarCorrespondenceMapHelper(typeToMap, correspondingType, returnMap);
        return returnMap;
    }

    /**
     * Helper for getTypeVarCorrespondenceMap
     * @param typeToMap
     * @param correspondingType
     * @param map
     */
    private static void getTypeVarCorrespondenceMapHelper(TypeExpr typeToMap, TypeExpr correspondingType, Map<TypeExpr, TypeExpr> map) {
  
        // If it's a type var, then we've completed mapping
        if (typeToMap instanceof TypeVar) {
            map.put(typeToMap, correspondingType);

        } else if (typeToMap instanceof TypeConsApp) {
            // If it's a data constructor, map its arguments.
            for (int i = 0, size = typeToMap.rootTypeConsApp().getNArgs(); i < size; i++) {
                getTypeVarCorrespondenceMapHelper(typeToMap.rootTypeConsApp().getArg(i), 
                                                  correspondingType.rootTypeConsApp().getArg(i).prune(), map);
            }
        } else if (typeToMap instanceof RecordType) {
            
            Map<FieldName, TypeExpr> hasFieldsMap = ((RecordType)typeToMap).getHasFieldsMap();
            RecordType correspondingRecordType = correspondingType.rootRecordType();
            Map<FieldName, TypeExpr> correspondingHasFieldsMap = correspondingRecordType.getHasFieldsMap();           
            
            for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                               
                FieldName fieldName = entry.getKey();
                TypeExpr fieldType = entry.getValue();            
                TypeExpr correspondingFieldType = correspondingHasFieldsMap.get(fieldName); 
                
                getTypeVarCorrespondenceMapHelper(fieldType, correspondingFieldType, map);                                        
            }            
                       
        } else {                
            throw new IllegalStateException("Programming error.");
        }
    }
    
    /**
     * Write this TypeExpr to the RecordOutputStream. Writing a single type object out is suspicious are you
     * sure you don't want the write(RecordOutputStream, TypeExpr[]) function?
     * @param s
     * @throws IOException
     */
    final void write (RecordOutputStream s) throws IOException {
        Map<TypeExpr, Short> visitedTypeExpr = new HashMap<TypeExpr, Short>();
        Map<RecordVar, Short> visitedRecordVar = new HashMap<RecordVar, Short>();
        write (s, visitedTypeExpr, visitedRecordVar);
    }
    
    /**
     * Write the array of types to the output stream. This will make sure all vars are mapped to the same objects.
     * @param s
     * @param types
     * @throws IOException
     */
    static final void write(RecordOutputStream s, ArrayList<TypeExpr> types) throws IOException{
        Map<TypeExpr, Short> visitedTypeExpr = new HashMap<TypeExpr, Short>();
        Map<RecordVar, Short> visitedRecordVar = new HashMap<RecordVar, Short>();
        for(int i = 0, nTypes = types.size(); i < nTypes; ++i){
            types.get(i).write (s, visitedTypeExpr, visitedRecordVar);
        }
    }
    
    /**
     * Write this TypeExpr to the RecordOutputStream.
     * @param s
     * @param visitedTypeExpr - map of TypeExpr instances that have been serialized.
     * @param visitedRecordVar - map of RecordVar instances that have been serialized.
     * @throws IOException
     */
    final void write (RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException {
                      
        if (this == EMPTY_RECORD) {
            s.startRecord(ModuleSerializationTags.STATIC_CONSTANT_TYPE_EXPR, staticConstantTypeExprSerializationSchema);
            s.writeUTF("EMPTY_RECORD");
            s.endRecord(); 
        } else {
            
            Short key = visitedTypeExpr.get(this);
            if (key != null) {
                // This instance has already been serialized so we simply reference it by key.
                s.startRecord(ModuleSerializationTags.ALREADY_VISITED_TYPE_EXPR, alreadyVisitedTypeExprSerializationSchema);
                s.writeShortCompressed(key.shortValue());
                s.endRecord();
            } else {
                // This instance has not been serialized.  Add it to the map of serialized TypeExpr
                // and write it out.
                key = new Short((short)visitedTypeExpr.size());
                visitedTypeExpr.put(this, key);
                
                writeActual(s, visitedTypeExpr, visitedRecordVar);
            }
        }
    }
    
    /**
     * Method to actually write the instance of TypeExpr to the RecordOutputStream.
     * @param s
     * @param visitedTypeExpr
     * @param visitedRecordVar
     * @throws IOException
     */
    abstract void writeActual (RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException; 
    
    /**
     * Load a TypeExpr from the RecordInputStream.
     * @param s
     * @param mti - the containing ModuleTypeInfo
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of TypeExpr
     * @throws IOException
     */
    static final TypeExpr load (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        Map<Short, TypeExpr> visitedTypeExpr = new HashMap<Short, TypeExpr>();
        Map<Short, RecordVar> visitedRecordVar = new HashMap<Short, RecordVar>();
        return TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
    }
   
    /**
     * Load an array of TypeExpr from the RecordInputStream. This will ensure that the same variables will
     * map the the same TypeVar objects.
     * @param s
     * @param mti
     * @param nTypes
     * @param msgLogger the logger to which to log deserialization messages.
     * @return An array of TypeExpr as read from the input stream.
     * @throws IOException
     */
    static final TypeExpr[] load (RecordInputStream s, ModuleTypeInfo mti, int nTypes, CompilerMessageLogger msgLogger) throws IOException {
        Map<Short, TypeExpr> visitedTypeExpr = new HashMap<Short, TypeExpr>();
        Map<Short, RecordVar> visitedRecordVar = new HashMap<Short, RecordVar>();
        TypeExpr[] types = new TypeExpr[nTypes];
        for(int i = 0; i < nTypes; ++i){
            types[i] = TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
        }
        return types;
    }

    /**
     * Load a TypeExpr from the RecordInputStream.
     * @param s
     * @param mti
     * @param visitedTypeExpr - a map of loaded TypeExpr instances.
     * @param visitedRecordVar - a map of loaded RecordVar instances.
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of TypeExpr
     * @throws IOException
     */
    static final TypeExpr load (RecordInputStream s, ModuleTypeInfo mti, Map<Short, TypeExpr> visitedTypeExpr, Map<Short, RecordVar> visitedRecordVar, CompilerMessageLogger msgLogger) throws IOException {
        
        // Determine which specific class this serialized instance is.
        RecordHeaderInfo rhi = s.findRecord(TYPE_EXPR_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException ("Unable to find TypeExpr record header.");
        }
        
        // Based on the record tag load the actual instance.
        switch (rhi.getRecordTag()) {
        
            case ModuleSerializationTags.STATIC_CONSTANT_TYPE_EXPR:
            {
                if (rhi.getSchema() > staticConstantTypeExprSerializationSchema) {
                    throw new IOException("Stored schema " + rhi.getSchema() +  " is greater than current schema " + staticConstantTypeExprSerializationSchema + " when loading a static constant TypeExpr.");
                }
                String constName = s.readUTF();
                s.skipRestOfRecord();
                                                          
                if (constName.equals ("EMPTY_RECORD")) {
                    return EMPTY_RECORD;
                } else {
                    
                }
            }
            
            case ModuleSerializationTags.ALREADY_VISITED_TYPE_EXPR: 
            {
                // This is a reference to an existing instance.  The instance should be
                // in the visitedTypeExpr map.
                if (rhi.getSchema() > alreadyVisitedTypeExprSerializationSchema) {
                    throw new IOException("Stored schema " + rhi.getSchema() +  " is greater than current schema " + alreadyVisitedTypeExprSerializationSchema + " loading an already visited TypeExpr.");
                }
                
                // Read the key and do the lookup.
                short id = s.readShortCompressed();
                TypeExpr te = visitedTypeExpr.get(new Short(id));
                if (te == null) {
                    throw new IOException ("Unable to resolve previously encountered TypeExpr instance in TypeExpr.");
                }
                s.skipRestOfRecord();
                return te;
            }
            
            case ModuleSerializationTags.TYPE_CONSTRUCTOR:
            {
                return TypeConsApp.load(s, rhi.getSchema(), mti, visitedTypeExpr, visitedRecordVar, msgLogger);             
            }
            
            case ModuleSerializationTags.TYPE_APP:
            {
                return TypeApp.load(s, rhi.getSchema(), mti, visitedTypeExpr, visitedRecordVar, msgLogger);
            }
            
            case ModuleSerializationTags.TYPE_VAR:
            case ModuleSerializationTags.TYPE_VAR_WITH_INSTANCE:
            {
                return TypeVar.load(s, rhi.getRecordTag(), rhi.getSchema(), mti, visitedTypeExpr, visitedRecordVar, msgLogger);                
            }
            
            case ModuleSerializationTags.RECORD_TYPE:
            {
                return RecordType.load(s, rhi.getSchema(), mti, visitedTypeExpr, visitedRecordVar, msgLogger);               
            }
            
            default: 
            {
                throw new IOException("Unexpected record tag " + rhi.getRecordTag() + " found when looking for TypeExpr.");
            }
        }
    }
}
