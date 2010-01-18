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
 * ClassInstanceChecker.java
 * Creation date (Aug 28, 2002).
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.openquark.cal.compiler.ClassInstance.InstanceStyle;
import org.openquark.cal.internal.module.Cal.Core.CAL_Debug_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Utilities.CAL_QuickCheck_internal;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_QuickCheck;


/**
 * Checks that class instances are semantically correct and adds ClassInstance objects to the ModuleTypeInfo
 * for the current module.
 * 
 * Also handles creation of internally defined instances for the Typeable type class
 * as well as for derived instances (which are defined implicitly in the deriving clauses
 * of data declarations).
 * 
 * Creation date (Aug 28, 2002).
 * @author Bo Ilic
 */
final class ClassInstanceChecker {
    
    private final CALCompiler compiler;

    /** class instances for the current module are added to the ModuleTypeInfo for the current module being compiler. */
    private final ModuleTypeInfo currentModuleTypeInfo;
             
    private final TypeClassChecker typeClassChecker;
    
    private final DataDeclarationChecker dataDeclarationChecker;
       
    /** used to look up the parse-tree of a class instance definition for the current module. */
    private final Map<ClassInstanceIdentifier, ParseTreeNode> classInstanceMap;
    
    /** A CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN node defining a derived instance function, whose next sibling (if any) is a derived instance function.*/
    private ParseTreeNode derivedInstanceFunctions;
    
    /** The generator for derived instance functions. */
    private final DerivedInstanceFunctionGenerator derivedInstanceFunctionGenerator;
        
    /**
     * Method ClassInstanceChecker.
     * @param compiler
     * @param currentModuleTypeInfo
     * @param typeClassChecker
     * @param dataDeclarationChecker
     */ 
    ClassInstanceChecker (CALCompiler compiler,
        ModuleTypeInfo currentModuleTypeInfo,
        TypeClassChecker typeClassChecker,
        DataDeclarationChecker dataDeclarationChecker) {
            
        if (compiler == null || currentModuleTypeInfo == null || typeClassChecker == null || dataDeclarationChecker == null) {
            throw new NullPointerException();
        }
            
        this.compiler = compiler;
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.typeClassChecker = typeClassChecker;
        this.dataDeclarationChecker = dataDeclarationChecker;  
        
        this.classInstanceMap = new HashMap<ClassInstanceIdentifier, ParseTreeNode>();
        
        this.derivedInstanceFunctionGenerator = DerivedInstanceFunctionGenerator.makeInternalUse();
    }
    
    /**
     * Call this method prior to checking the instances declared within the current module.
     * 
     * The purpose is to verify that there are no overlapping instances brought into
     * scope within the current module through its imports.
     * For example, if modules M2 and M3 import M1, and M4 imports M2 and M3,
     * then a C-T instance defined in both M2 and M3 will result in an error in M4 due to
     * overlapping instances.
     *          
     * @param moduleDefnNode
     */    
    private void checkForOverlappingInstances(ParseTreeNode moduleDefnNode) {
        moduleDefnNode.verifyType(CALTreeParserTokenTypes.MODULE_DEFN);        
        checkForOverlappingInstances(moduleDefnNode, currentModuleTypeInfo, new HashSet<ModuleName>(), new HashMap<ClassInstanceIdentifier, ClassInstance>());                               
    }
    
    /**
     * @param moduleDefnNode
     * @param moduleTypeInfo
     * @param visitedModules Set of String objects giving the names of modules visited so far
     * @param instanceMap (ClassInstanceIdentifier -> ClassInstance)
     */
    private void checkForOverlappingInstances (ParseTreeNode moduleDefnNode, ModuleTypeInfo moduleTypeInfo, Set<ModuleName> visitedModules, Map<ClassInstanceIdentifier, ClassInstance> instanceMap) {
        
        ModuleName moduleName = moduleTypeInfo.getModuleName();
        if (!visitedModules.add(moduleName)) {
            return;
        }             
                        
        for (int i = 0, nInstances = moduleTypeInfo.getNClassInstances(); i < nInstances; ++i) {
            
            ClassInstance instance = moduleTypeInfo.getNthClassInstance(i);
            ClassInstanceIdentifier instanceIdentifier = instance.getIdentifier();
            
            ClassInstance overlappingInstance = instanceMap.put(instanceIdentifier, instance);
            if (overlappingInstance != null) {
                                                     
                String declName = instance.getNameWithContext();
                String otherDeclName = overlappingInstance.getNameWithContext();
                MessageKind message;
                if(declName.equals(otherDeclName)) {                      
                    // ClassInstanceChecker: the instance {declName} has multiple visible definitions. 
                    // One is in module {instance.getModuleName()} and the other is in module {overlappingInstance.getModuleName()}.
                    message = new MessageKind.Error.InstanceHasMultipleVisibleDefinitions(declName, instance.getModuleName(), overlappingInstance.getModuleName());
                } else {
                    // ClassInstanceChecker: the instance {declName} defined in module {instance.getModuleName()} overlaps with 
                    // the instance {otherDeclName} defined in module {overlappingInstance.getModuleName()}.
                    message = new MessageKind.Error.InstancesOverlap(declName, instance.getModuleName(), otherDeclName, overlappingInstance.getModuleName());
                }                
                                       
                compiler.logMessage(new CompilerMessage(moduleDefnNode, message));                                     
            }   
        }
        
        for (int i = 0, nImportedModules = moduleTypeInfo.getNImportedModules(); i < nImportedModules; ++i) {
            checkForOverlappingInstances (moduleDefnNode, moduleTypeInfo.getNthImportedModule(i), visitedModules, instanceMap);
        }
    }
    
    /**
     * For each type constructor T defined in the module M, such that all of its type variables have kind *,
     * add a built-in Prelude.Typeable M.T instance.             
     * 
     * It the type arity of T is > 0, this will be a constrained instance.
     *
     * For example, for Either a b, it is
     * 
     * instance (Typeable a, Typeable b) => Typeable (Either a b) where
     *    typeOf = $typeOfEither;
     *    ;
     * 
     * $typeOfEither is a hidden compiler supplied function. Its definition in CAL source formerly (prior to being built-in
     * and automatically supplied for each type) was:
     * 
     * typeOfEither :: (Typeable a, Typeable b) => Either a b -> TypeRep;
     * private typeOfEither x =
     * let
     *     leftType :: Either a b -> a;
     *     leftType = undefined;
     *     rightType :: Either a b -> b;
     *     rightType = undefined;
     * in
     *     TypeRep "Cal.Core.Prelude.Either" [typeOf (leftType x), typeOf (rightType x)];
     *
     * The tricky part here is that the argument x cannot be touched in any way by the function typeOfEither.
     * 
     * Note that the reason for the restriction on kinds is that, for example, if
     * data Foo a b = MakeFoo f::(a b); 
     * then Foo has kind (* -> *) -> * -> *
     * and an automatic instance definition would be for
     * instance (Typeable a, Typeable b) => Typeable (Foo a b) where ...
     * but the type class Typeable has kind *, and the type variable a, of kind * -> * i.e. this instance 
     * declaration violates static type checking.     
     */
    private void addTypeableInstances() {
        
        if (currentModuleTypeInfo.isExtension()) {
            //don't add instances again for an adjunct module.
            return;
        }
        
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        //this uses the fact that Prelude.Typeable is a public class, and all modules must import the Prelude.
        final TypeClass typeableTypeClass = currentModuleTypeInfo.getVisibleTypeClass(CAL_Prelude.TypeClasses.Typeable);
        
        SortedSet<TypeClass> constraintSet = TypeClass.makeNewClassConstraintSet();
        constraintSet.add(typeableTypeClass);
        constraintSet = Collections.unmodifiableSortedSet(constraintSet);
        
        for (int i = 0, nTypeConstructors = currentModuleTypeInfo.getNTypeConstructors(); i < nTypeConstructors; ++i) {
            
            final TypeConstructor typeCons = currentModuleTypeInfo.getNthTypeConstructor(i);
            
            //we only add a Typeable instance for type constructors all of whose type arguments have arity *.
            if (typeCons.getKindExpr().isSimpleKindChain()) {
                
                final int arity = typeCons.getTypeArity();            
                final TypeExpr[] args = new TypeExpr[arity];
                
                for (int j = 0; j < arity; ++j) {
                    
                    args[j] = TypeVar.makeTypeVar(null, constraintSet, false);
                }
                
                final TypeConsApp typeConsApp = new TypeConsApp(typeCons, args);
                            
                final ClassInstance classInstance = new ClassInstance(currentModuleName, typeConsApp, typeableTypeClass, null, ClassInstance.InstanceStyle.INTERNAL);
                
                currentModuleTypeInfo.addClassInstance(classInstance);
            }
        }        
    }
     
    /**
     * Check the derived instances defined in this module i.e. the instances implied by the deriving clauses
     * of data declarations.
     *
     * This function
     * a) verifies that the derived instance is valid for the particular type for type-theoretic reasons
     *    (earlier static checks were done when the data declaration was initially checked)
     * b) creates the internally defined derived instance definitions
     * c) creates the internal helper functions which define the derived instance functions 
     *
     */
    private void checkDerivedInstances() throws UnableToResolveForeignEntityException {
        
        if (currentModuleTypeInfo.isExtension()) {
            //don't add derived instances again for an adjunct module.
            return;
        }
        
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();  
        
        
       
        for (int i = 0, nTypeConstructors = currentModuleTypeInfo.getNTypeConstructors(); i < nTypeConstructors; ++i) {
            
            TypeConstructor typeCons = currentModuleTypeInfo.getNthTypeConstructor(i);
            
            //use private functions equalsInt, lessThanInt etc as the instance resolving functions for
            //the Eq, Ord and Enum instances of an enum type. This saves class file space and which could be quite bulky for
            //long enum types, since the Ord methods do case-of-cases and so have length proportional to the square of
            //the number of data constructors. It also saves on compilation time. Benchmarks show that there is no
            //runtime performance impact.
            final boolean useCompactEnumDerivedInstances = 
                TypeExpr.isEnumType(typeCons) && !typeCons.getName().equals(CAL_Prelude.TypeConstructors.Boolean);
            
            //this flag is used to ensure that the fromInt toInt Helper functions
            //are added at most once for each instance type
            boolean addedIntHelpers = false;
            
            for (int j = 0, nDerivedInstances = typeCons.getNDerivedInstances(); j < nDerivedInstances; ++j) {
                
                QualifiedName typeClassName = typeCons.getDerivingClauseTypeClassName(j);
                TypeClass typeClass = currentModuleTypeInfo.getVisibleTypeClass(typeClassName);
                final SourceRange declarationPosition = typeCons.getDerivingClauseTypeClassPosition(j);
                
                //If the type is T a b c, then the instance type for type class C will be 
                //(C a, C b, C c) => T a b c
                TypeConsApp instanceType = makeInstanceType(typeCons, typeClass);                    
                
                ClassInstance classInstance;
                                
                //make sure the toInt fromInt helpers are added if the 
                //class needs them
                if (!addedIntHelpers && 
                   (typeClassName.equals(CAL_Prelude.TypeClasses.Enum) ||
                    typeClassName.equals(CAL_Prelude.TypeClasses.IntEnum) ||
                    typeClassName.equals(CAL_QuickCheck.TypeClasses.Arbitrary))) {

                    TypeExpr intType = compiler.getTypeChecker().getTypeConstants().getIntType();
                    TypeExpr fromIntHelperTypeExpr = TypeExpr.makeFunType(intType, instanceType);
                    TypeExpr toIntHelperTypeExpr = TypeExpr.makeFunType(instanceType, intType);
                    
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeFromIntHelper(typeCons), fromIntHelperTypeExpr, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeToIntHelper(typeCons), toIntHelperTypeExpr, declarationPosition);

                    addedIntHelpers = true;
                }
                
                
                //add the definitions of the instance functions                
                if (typeClassName.equals(CAL_Prelude.TypeClasses.Eq)) {
                    
                    classInstance = checkDerivedEqInstance(typeCons, instanceType, typeClass, useCompactEnumDerivedInstances, declarationPosition);                                       
                    
                } else if (typeClassName.equals(CAL_Prelude.TypeClasses.Ord)) {
                    
                    classInstance = checkDerivedOrdInstance(typeCons, instanceType, typeClass, useCompactEnumDerivedInstances, declarationPosition);  

                } else if (typeClassName.equals(CAL_Prelude.TypeClasses.Bounded)) {
                    
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeMinBoundInstanceFunction(typeCons), instanceType, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeMaxBoundInstanceFunction(typeCons), instanceType, declarationPosition);
                    
                    classInstance = new ClassInstance(currentModuleName, instanceType, typeClass, null, ClassInstance.InstanceStyle.DERIVING);
                    
                } else if (typeClassName.equals(CAL_Prelude.TypeClasses.Enum)) {                                                        
                
                    TypeExpr listTypeExpr = compiler.getTypeChecker().getTypeConstants().makeListType(instanceType);
                    TypeExpr intType = compiler.getTypeChecker().getTypeConstants().getIntType();
                    TypeExpr upFromTypeExpr = TypeExpr.makeFunType(instanceType, listTypeExpr);
                    TypeExpr upFromThenTypeExpr = TypeExpr.makeFunType(instanceType, upFromTypeExpr);
                    TypeExpr upFromThenToTypeExpr = TypeExpr.makeFunType(instanceType, upFromThenTypeExpr);
                       
                    TypeExpr upFromToTypeExpr = TypeExpr.makeFunType(instanceType, upFromTypeExpr);           
                    TypeExpr upFromToHelperTypeExpr = 
                        TypeExpr.makeFunType(intType, TypeExpr.makeFunType(intType, listTypeExpr));  
                    
                    TypeExpr upFromThenToHelperExpr =
                        TypeExpr.makeFunType(intType, TypeExpr.makeFunType(intType, TypeExpr.makeFunType(intType, listTypeExpr)));
                    

                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeUpFromToHelper(typeCons), 
                        upFromToHelperTypeExpr, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeUpFromThenToHelperUp(typeCons), 
                        upFromThenToHelperExpr, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeUpFromThenToHelperDown(typeCons), 
                        upFromThenToHelperExpr, declarationPosition);
                    
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeEnumUpFromThenInstanceFunction(typeCons), 
                        upFromThenTypeExpr, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeEnumUpFromThenToInstanceFunction(typeCons), 
                        upFromThenToTypeExpr, declarationPosition);
                                       
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeEnumUpFromToInstanceFunction(typeCons), 
                        upFromToTypeExpr, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeEnumUpFromInstanceFunction(typeCons), 
                        upFromTypeExpr, declarationPosition);
                           
                        classInstance = new ClassInstance(currentModuleName, instanceType, typeClass, 
                            new QualifiedName[] {
                                ClassInstance.makeInternalInstanceMethodName(CAL_Prelude.Functions.upFrom.getUnqualifiedName(), typeCons.getName()),
                                ClassInstance.makeInternalInstanceMethodName(CAL_Prelude.Functions.upFromThen.getUnqualifiedName(), typeCons.getName()),
                                ClassInstance.makeInternalInstanceMethodName(CAL_Prelude.Functions.upFromTo.getUnqualifiedName(), typeCons.getName()),
                                ClassInstance.makeInternalInstanceMethodName(CAL_Prelude.Functions.upFromThenTo.getUnqualifiedName(), typeCons.getName())
                            }, 
                            ClassInstance.InstanceStyle.DERIVING);
                    
                } else if (typeClassName.equals(CAL_Prelude.TypeClasses.Outputable)) {
                    
                    classInstance = checkDerivedOutputableInstance(typeCons, instanceType, typeClass, declarationPosition);
                                                            
                }  else if (typeClassName.equals(CAL_Prelude.TypeClasses.Inputable)) {
                    
                    if(typeCons.getNDataConstructors() > 0) {
                        TypeExpr jObjectTypeExpr = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.JObject));
                        TypeExpr inputTypeExpr = TypeExpr.makeFunType(jObjectTypeExpr, instanceType);
                        addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeAlgebraicInputInstanceMethod(typeCons), inputTypeExpr, declarationPosition);

                        classInstance = new ClassInstance(currentModuleName, instanceType, typeClass, null, ClassInstance.InstanceStyle.DERIVING);
                        
                    } else {
                        //will be a foreign type (and not a built-in type or type defined by an algebraic data declaration).
                        //Unlike any of the other derived instances, the class methods for the derived Inputable class are
                        //added late in the ExpressionGenerator. This is because 
                        //a) there is a distinct method for each foreign object type (it must do a cast, and that cast is type specific.
                        //b) there is no direct way to represent the cast in CAL (although derived Inputable and Outputable instances can
                        //   be used to do the job, once this feature is in place!)
                        
                        classInstance = new ClassInstance(currentModuleName, instanceType, typeClass, null, ClassInstance.InstanceStyle.DERIVING);
                    }
                    
                } else if (typeClassName.equals(CAL_Debug.TypeClasses.Show)) {
                    
                    classInstance = checkDerivedShowInstance(typeCons, instanceType, typeClass, declarationPosition);     
                    
                } else if (typeClassName.equals(CAL_QuickCheck.TypeClasses.Arbitrary)) {
                    
                    if (typeCons.getForeignTypeInfo() != null) {
                        throw new IllegalStateException("Arbitrary instances cannot be derived for foreign types"); 
                    }
                  
                    //this type represents a Gen for the current instance type.
                    TypeExpr genInstanceType =
                        new TypeConsApp(
                            currentModuleTypeInfo.getVisibleTypeConstructor(CAL_QuickCheck.TypeConstructors.Gen),
                            new TypeExpr [] {instanceType});  
                    
                    //this type represents the type (Gen a)
                    TypeExpr genAType =
                        new TypeConsApp(
                            currentModuleTypeInfo.getVisibleTypeConstructor(CAL_QuickCheck.TypeConstructors.Gen),
                            new TypeExpr [] {TypeExpr.makeParametricType()} );  

                    TypeExpr coarbitraryFunctionType = TypeExpr.makeFunType(instanceType, TypeExpr.makeFunType(genAType, genAType));                    
 
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeArbitraryFunction(typeCons), genInstanceType, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeCoArbitraryFunction(typeCons), coarbitraryFunctionType, declarationPosition);
  
                    classInstance = new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {
                              ClassInstance.makeInternalInstanceMethodName("arbitrary", typeCons.getName()),
                              ClassInstance.makeInternalInstanceMethodName("coarbitrary", typeCons.getName()),
                              CAL_QuickCheck_internal.Functions.generateInstanceDefault
                        }, 
                        InstanceStyle.DERIVING);
                    
                      
                } else if (typeClassName.equals(CAL_Prelude.TypeClasses.IntEnum)) {
                    
                    // algebraic types (only enumeration types are permitted; the DerivedInstanceFunctionGenerator methods will check that)
                    if(typeCons.getForeignTypeInfo() != null) {
                        throw new IllegalStateException("IntEnum instances cannot be derived for foreign types"); 
                    }
                    
                    TypeExpr maybeTypeExpr = 
                        new TypeConsApp(
                            currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Maybe),
                            new TypeExpr[] {instanceType});

                    TypeExpr intType = compiler.getTypeChecker().getTypeConstants().getIntType();
                    TypeExpr intToEnumTypeExpr = TypeExpr.makeFunType(intType, instanceType);
                    TypeExpr intToEnumCheckedTypeExpr = TypeExpr.makeFunType(intType, maybeTypeExpr);

                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeEnumIntToEnumFunction(typeCons), intToEnumTypeExpr, declarationPosition);
                    addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeEnumIntToEnumCheckedFunction(typeCons), intToEnumCheckedTypeExpr, declarationPosition);
                        
                    classInstance = new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {
                            ClassInstance.makeInternalInstanceMethodName("intToEnum", typeCons.getName()),
                            ClassInstance.makeInternalInstanceMethodName("intToEnumChecked", typeCons.getName()),
                            ClassInstance.makeInternalInstanceMethodName("toIntHelper", typeCons.getName())}, 
                        InstanceStyle.DERIVING);
                    
                } else {
                    // static checks in DataDeclarationChecker will have logged an error message for classes that we cannot
                    // derive an instance for, so this exception won't be shown to the user as an internal coding error (although it
                    // will cause further compilation to halt with "unable to recover from previous compilation errors").
                    throw new IllegalStateException("Invalid deriving type class.");
                }
                
                currentModuleTypeInfo.addClassInstance(classInstance);                
            }            
        }        
    }

    /**
     * A helper function to create the instance type for a derived instance.
     * If the type is T a b c, then the instance type for type class C will be 
     * (C a, C b, C c) => T a b c
     * 
     * @param typeCons
     * @param typeClass
     * @return the TypeConsApp object representing the instance type.
     */
    static TypeConsApp makeInstanceType(TypeConstructor typeCons, TypeClass typeClass) {
        
        SortedSet<TypeClass> constraintSet = TypeClass.makeNewClassConstraintSet();
        constraintSet.add(typeClass);
        constraintSet = Collections.unmodifiableSortedSet(constraintSet);                                                       
        final int arity = typeCons.getTypeArity(); 
        final TypeExpr[] args = new TypeExpr[arity];            
        for (int k = 0; k < arity; ++k) {                
            args[k] = TypeVar.makeTypeVar(null, constraintSet, false);
        }
        
        return new TypeConsApp(typeCons, args);
    }
    
    
    /**
     * A helper function to create the ClassInstance object for a derived Eq instance.
     * @param typeCons
     * @param instanceType
     * @param typeClass
     * @param useCompactEnumDerivedInstances
     * @param sourcePosition TODO
     * @return the ClassInstance object for the derived Eq instance
     */
    private ClassInstance checkDerivedEqInstance(TypeConstructor typeCons,
            TypeConsApp instanceType,
            TypeClass typeClass,
            boolean useCompactEnumDerivedInstances, SourceRange sourcePosition) throws UnableToResolveForeignEntityException { 
        
        if (!typeClass.getName().equals(CAL_Prelude.TypeClasses.Eq)) {
            throw new IllegalStateException();
        } 
        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
               
        ForeignTypeInfo foreignTypeInfo = typeCons.getForeignTypeInfo();
        if (foreignTypeInfo != null) {
            
            // Foreign types
            
            // CAL has primitive ops for the Eq methods of each primitive Java type
            Class<?> foreignType = foreignTypeInfo.getForeignType(); 
            if(foreignType.isPrimitive()) {
                
                if(foreignType == boolean.class) {
                    // The Boolean Eq instance is derived, so its instance functions will have 
                    // standard internal names rather than standard primitive names
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] { 
                            ClassInstance.makeInternalInstanceMethodName("equals", CAL_Prelude.TypeConstructors.Boolean), 
                            ClassInstance.makeInternalInstanceMethodName("notEquals", CAL_Prelude.TypeConstructors.Boolean)},
                        InstanceStyle.DERIVING);
                    
                } else {
                    // The instance functions for the non-boolean primitive types are named in a standard way,
                    // so we can generate the name of each method based on the name of the type.
                    String foreignTypeName = foreignType.getName();
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] { 
                            makePrimitiveInstanceMethodName("equals", foreignTypeName), 
                            makePrimitiveInstanceMethodName("notEquals", foreignTypeName)},
                        InstanceStyle.DERIVING);
                }
                
                // For foreign types that represent non-primitive Java types (reference types),
                // we delegate to the methods of defined for Objects.
            } else {
                
                return new ClassInstance(currentModuleName, instanceType, typeClass, 
                    new QualifiedName[] { 
                        CAL_Prelude_internal.Functions.equalsObject, 
                        CAL_Prelude_internal.Functions.notEqualsObject},
                    InstanceStyle.DERIVING);
            }
            
            
        } else {
            
            // Non-foreign types
            
            if (useCompactEnumDerivedInstances){
                return
                    new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[]{
                            CAL_Prelude_internal.Functions.equalsInt,
                            CAL_Prelude_internal.Functions.notEqualsInt},
                        InstanceStyle.DERIVING);
                
            } else {
                
                TypeExpr booleanType = compiler.getTypeChecker().getTypeConstants().getBooleanType();
                TypeExpr equalsTypeExpr = TypeExpr.makeFunType(instanceType, TypeExpr.makeFunType(instanceType, booleanType));
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeEqualsInstanceFunction(typeCons), equalsTypeExpr, sourcePosition);
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeNotEqualsInstanceFunction(typeCons), equalsTypeExpr, sourcePosition);
                
                return new ClassInstance(currentModuleName, instanceType, typeClass, null, InstanceStyle.DERIVING);
            }
        }               
    }
    
   /**
     * A helper function to create the ClassInstance object for a derived Ord instance.
     * @param typeCons
 * @param instanceType
 * @param typeClass
 * @param useCompactEnumDerivedInstances
 * @param position TODO
     * @return the ClassInstance object for the derived Ord instance
     */
    private ClassInstance checkDerivedOrdInstance(TypeConstructor typeCons,
            TypeConsApp instanceType,
            TypeClass typeClass,
            boolean useCompactEnumDerivedInstances, SourceRange position) throws UnableToResolveForeignEntityException { 
        
        if (!typeClass.getName().equals(CAL_Prelude.TypeClasses.Ord)) {
            throw new IllegalStateException();
        } 
        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
                
        ForeignTypeInfo foreignTypeInfo = typeCons.getForeignTypeInfo();
        if (foreignTypeInfo != null) {
        
            // Foreign types
            
            // CAL has primitive ops for the Ord methods for each primitive Java type
            Class<?> foreignType = foreignTypeInfo.getForeignType(); 
            if(foreignType.isPrimitive()) {
                
                if(foreignType == boolean.class) {
                    
                    // The Boolean Ord instance is derived, so its instance functions will have 
                    // standard internal names rather than standard primitive names
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] { 
                            ClassInstance.makeInternalInstanceMethodName("lessThan", CAL_Prelude.TypeConstructors.Boolean),
                            ClassInstance.makeInternalInstanceMethodName("lessThanEquals", CAL_Prelude.TypeConstructors.Boolean),
                            ClassInstance.makeInternalInstanceMethodName("greaterThanEquals", CAL_Prelude.TypeConstructors.Boolean),
                            ClassInstance.makeInternalInstanceMethodName("greaterThan", CAL_Prelude.TypeConstructors.Boolean),
                            ClassInstance.makeInternalInstanceMethodName("compare", CAL_Prelude.TypeConstructors.Boolean),
                            ClassInstance.makeInternalInstanceMethodName("max", CAL_Prelude.TypeConstructors.Boolean),
                            ClassInstance.makeInternalInstanceMethodName("min", CAL_Prelude.TypeConstructors.Boolean)
                        },
                        InstanceStyle.DERIVING);
                    
                } else {
                    
                    // The Ord instance functions for the non-boolean primitive types are named in a standard way,
                    // so we can generate the name of each method based on the name of the type.
                    String foreignTypeName = foreignType.getName();
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] { 
                            makePrimitiveInstanceMethodName("lessThan", foreignTypeName),
                            makePrimitiveInstanceMethodName("lessThanEquals", foreignTypeName),
                            makePrimitiveInstanceMethodName("greaterThanEquals", foreignTypeName),
                            makePrimitiveInstanceMethodName("greaterThan", foreignTypeName),
                            makePrimitiveInstanceMethodName("compare", foreignTypeName),
                            makePrimitiveInstanceMethodName("max", foreignTypeName),
                            makePrimitiveInstanceMethodName("min", foreignTypeName)
                        },
                        InstanceStyle.DERIVING);
                }
                
                // For foreign types that represent non-primitive Java types (reference types),
                // we delegate to the methods of the Comparable interface.
            } else {
                
                // Foreign reference types must implement Comparable to have derived Ord instances
                if(!Comparable.class.isAssignableFrom(foreignTypeInfo.getForeignType())) {
                    throw new IllegalStateException("Foreign reference types can only derive an Ord instance if they implement Comparable");
                }
                
                return new ClassInstance(currentModuleName, instanceType, typeClass, 
                    new QualifiedName[] { 
                        CAL_Prelude_internal.Functions.lessThanComparable,
                        CAL_Prelude_internal.Functions.lessThanEqualsComparable,
                        CAL_Prelude_internal.Functions.greaterThanEqualsComparable, 
                        CAL_Prelude_internal.Functions.greaterThanComparable,
                        CAL_Prelude_internal.Functions.compareComparable,
                        CAL_Prelude_internal.Functions.maxComparable,
                        CAL_Prelude_internal.Functions.minComparable
                    },
                    InstanceStyle.DERIVING);
            }
            
           
        } else {
            
             // Non-foreign types
            
            if (useCompactEnumDerivedInstances){
                return
                    new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[]{
                            CAL_Prelude_internal.Functions.lessThanInt, 
                            CAL_Prelude_internal.Functions.lessThanEqualsInt,
                            CAL_Prelude_internal.Functions.greaterThanEqualsInt,
                            CAL_Prelude_internal.Functions.greaterThanInt,
                            CAL_Prelude_internal.Functions.compareInt,
                            CAL_Prelude_internal.Functions.maxInt,
                            CAL_Prelude_internal.Functions.minInt},
                        InstanceStyle.DERIVING);                          
                
            } else {                    
                
                TypeExpr booleanType = compiler.getTypeChecker().getTypeConstants().getBooleanType();
                TypeExpr lessThanTypeExpr = TypeExpr.makeFunType(instanceType, TypeExpr.makeFunType(instanceType, booleanType));
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeLessThanInstanceFunction(typeCons), lessThanTypeExpr, position);
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeLessThanEqualsInstanceFunction(typeCons), lessThanTypeExpr, position);
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeGreaterThanEqualsInstanceFunction(typeCons), lessThanTypeExpr, position);
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeGreaterThanInstanceFunction(typeCons), lessThanTypeExpr, position);
                
                //this uses the fact that Prelude.Ordering is a public class, and all modules must import the Prelude.
                final TypeConstructor orderingTypeCons =
                    currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Ordering);
        
                TypeExpr compareTypeExpr = TypeExpr.makeFunType(instanceType, TypeExpr.makeFunType(instanceType, TypeExpr.makeNonParametricType(orderingTypeCons)));
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeCompareInstanceFunction(typeCons), compareTypeExpr, position);
                
                TypeExpr maxTypeExpr = TypeExpr.makeFunType(instanceType, TypeExpr.makeFunType(instanceType, instanceType));
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeMaxInstanceFunction(typeCons), maxTypeExpr, position);
                addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeMinInstanceFunction(typeCons), maxTypeExpr, position);
                
                return new ClassInstance(currentModuleName, instanceType, typeClass, null, InstanceStyle.DERIVING);
            }
        }        
        
    }
    
    /**
     * A helper function to create the ClassInstance object for a derived Outputable instance.
     * @param typeCons
     * @param instanceType
     * @param typeClass
     * @param position TODO
     * @return the ClassInstance object for the derived Outputable instance
     */
    private ClassInstance checkDerivedOutputableInstance(TypeConstructor typeCons,
            TypeConsApp instanceType,
            TypeClass typeClass, SourceRange position) throws UnableToResolveForeignEntityException {
        
        if (!typeClass.getName().equals(CAL_Prelude.TypeClasses.Outputable)) {
            throw new IllegalStateException();
        }
        
        if (TypeConstructor.getBuiltInType(typeCons.getName()) != null) {
            throw new IllegalStateException("The built-in type " + typeCons.getName() + " cannot derive the Outputable type class.");                        
        }
        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        
        ForeignTypeInfo foreignTypeInfo = typeCons.getForeignTypeInfo();                
        
        if (foreignTypeInfo != null) {
            
            // foreign types
            
            Class<?> foreignType = foreignTypeInfo.getForeignType();
            
            if(foreignType.isPrimitive()) {
                
                if(foreignType == boolean.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputBoolean},
                        InstanceStyle.DERIVING); 
                    
                } else if (foreignType == byte.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputByte},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == char.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputChar},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == short.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputShort},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == int.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputInt},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == long.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputLong},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == float.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputFloat},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == double.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Prelude_internal.Functions.outputDouble},
                        InstanceStyle.DERIVING);
                    
                } else {
                    throw new IllegalStateException("A type that claims to be primitive is not one of the known primitive types");
                }
                
            } else {
                // Defer to Prelude.outputJObject for non-primitive foreign values
                return new ClassInstance(currentModuleName, instanceType, typeClass, 
                    new QualifiedName[] {CAL_Prelude_internal.Functions.outputJObject}, 
                    InstanceStyle.DERIVING);
            }                        
            
        } else {
            
            // Types declared using an algebraic data definition
            
            // The instance function will use an AlgebraicValue container object
            TypeExpr jObjectTypeExpr = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.JObject));
            TypeExpr outputTypeExpr = TypeExpr.makeFunType(instanceType, jObjectTypeExpr);
            addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeAlgebraicOutputInstanceMethod(typeCons), outputTypeExpr, position);
            
            return new ClassInstance(currentModuleName, instanceType, typeClass, null, InstanceStyle.DERIVING);
        }
    }
    
   /**
     * A helper function to create the ClassInstance object for a derived Show instance.
     * @param typeCons
 * @param instanceType
 * @param typeClass
 * @param position TODO
     * @return the ClassInstance object for the derived Show instance
     */
    private ClassInstance checkDerivedShowInstance(TypeConstructor typeCons,
            TypeConsApp instanceType,
            TypeClass typeClass, SourceRange position) throws UnableToResolveForeignEntityException {
        
        if (!typeClass.getName().equals(CAL_Debug.TypeClasses.Show)) {
            throw new IllegalStateException();
        }
        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        
        
        ForeignTypeInfo foreignTypeInfo = typeCons.getForeignTypeInfo(); 
        
        if (foreignTypeInfo != null) {
        
            // foreign types
            
            Class<?> foreignType = foreignTypeInfo.getForeignType();
            
            if (foreignType.isPrimitive()) {
                
                if (foreignType == boolean.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showForeignBoolean},
                        InstanceStyle.DERIVING); 
                    
                } else if (foreignType == byte.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showByte},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == char.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showForeignChar},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == short.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showShort},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == int.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showInt},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == long.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showLong},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == float.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showFloat},
                        InstanceStyle.DERIVING);
                    
                } else if (foreignType == double.class) {
                    return new ClassInstance(currentModuleName, instanceType, typeClass, 
                        new QualifiedName[] {CAL_Debug_internal.Functions.showDouble},
                        InstanceStyle.DERIVING);
                    
                } else {
                    throw new IllegalStateException("A type that claims to be primitive is not one of the known primitive types");
                }
                
            } else {
                // Defer to String.valueOf(Object) for non-primitive foreign values
                return new ClassInstance(currentModuleName, instanceType, typeClass, 
                    new QualifiedName[] {CAL_Debug_internal.Functions.showJObject}, 
                    InstanceStyle.DERIVING);
            }
            
           
        } else {
            
            // non-foreign types
            
            TypeExpr showTypeExpr = TypeExpr.makeFunType(instanceType, compiler.getTypeChecker().getTypeConstants().getStringType());
            addDerivedInstanceFunction(derivedInstanceFunctionGenerator.makeShowInstanceFunction(typeCons), showTypeExpr, position);                    
            
            return new ClassInstance(currentModuleName, instanceType, typeClass, null, InstanceStyle.DERIVING);
        }
    }    
    
    /**
     * A helper method to add an instance function for a derived instance, 
     * to the environment so that that instance function can go through
     * further type-checking (most importantly overload resolution).    
     * @param functionDefn
     * @param declaredFunctionTypeExpr may be null if no type is declared
     * @param sourceRange TODO
     */
    private void addDerivedInstanceFunction(SourceModel.FunctionDefn.Algebraic functionDefn, TypeExpr declaredFunctionTypeExpr, SourceRange sourceRange) {
        
        CALTypeChecker typeChecker = compiler.getTypeChecker();
        String functionName = functionDefn.getName();
        ParseTreeNode functionParseTree = functionDefn.toParseTreeNode();
        
        functionParseTree.setInternallyGenerated(sourceRange);
        
        if (derivedInstanceFunctions == null) {
            derivedInstanceFunctions = functionParseTree;
        } else {
            functionParseTree.setNextSibling(derivedInstanceFunctions);
            derivedInstanceFunctions = functionParseTree;
        }
        
        typeChecker.addDerivedInstanceFunction(functionName, functionParseTree, declaredFunctionTypeExpr);
    }
    
    /**
     * Adds the derived instance functions as children of outerDefnListNode so that they can be encoded 
     * by the ExpressionGenerator into core functions
     * @param outerDefnListNode
     */
    void addDerivedInstanceFunctions(ParseTreeNode outerDefnListNode) {
        if (derivedInstanceFunctions == null) {
            return;
        }
        
        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
                      
        ParseTreeNode attachingNode = outerDefnListNode.lastChild();
        if (attachingNode == null) {
            outerDefnListNode.setFirstChild(derivedInstanceFunctions);
        } else {
            attachingNode.setNextSibling(derivedInstanceFunctions);
        }
    }    
                                             
    /**
     * Static analysis of the class instance declarations defined within the current module.
     * Verification that the types of the instance methods are what they should be must be
     * delayed until after type checking the functions defined in this module.
     * 
     * Also checks that there are no overalapping instances brought into scope in the current module. 
     * 
     * @param moduleLevelParseTrees
     */
    void checkClassInstanceDefinitions(ModuleLevelParseTrees moduleLevelParseTrees) throws UnableToResolveForeignEntityException {  
        
        checkForOverlappingInstances(moduleLevelParseTrees.getModuleDefnNode());
        
        List<ParseTreeNode> instanceDefnNodes = moduleLevelParseTrees.getInstanceDefnNodes();             
              
        //add the built-in class instance declarations "instance Typeable T where ..." for each type T
        //where this is possible (all of the type variables must have kind *).
        addTypeableInstances();

        checkDerivedInstances();
                     
        for (final ParseTreeNode instanceNode : instanceDefnNodes) {          
                  
            instanceNode.verifyType(CALTreeParserTokenTypes.INSTANCE_DEFN);
            
            ClassInstance classInstance = checkClassInstanceDefinition(instanceNode);
            ClassInstanceIdentifier instanceIdentifier = classInstance.getIdentifier();                     
            classInstanceMap.put(instanceIdentifier, instanceNode);           
        }
        
        //Now do the static analysis on the instance declarations that must wait for a 
        //second pass over the instance declarations.
        
        //If there is a C-T instance, and C' is a superclass of C, then there is a C'-T instance in scope.
        //So for example, if there is an instance Ord-Foo, there must be an instance Eq-Foo in the current module 
        //or in some module imported directly or indirectly into the current module.
        //Note: we only need to check immediate superclasses (i.e. parents) since the parents will check for their parents.
               
        //The constraints on the type variables in C-T must imply the constraints
        //on the type variables in C'-T.  What this means, is that if (Cij' a) is a constraint for C'-T, then (Dij' a)
        //is a constraint for D-T where Dij' is Cij' or a subclass.
        //The reason for this is so that we can derive a dictionary for C'-T given a dictionary for C-T.     
     
        for (int i = 0, nInstances = currentModuleTypeInfo.getNClassInstances(); i < nInstances; ++i) {
            
            ClassInstance classInstance = currentModuleTypeInfo.getNthClassInstance(i);           
            if (classInstance.isUniversalRecordInstance()) {
                //todoBI it is not valid to skip this check. Need to do more work here in this case.
                //this path happens during adjunct checking.
                continue;
            }
            
            TypeClass typeClass = classInstance.getTypeClass();            
            TypeExpr instanceType = classInstance.getType();
            
            List<Set<TypeClass>> childVarConstraints = classInstance.getSuperclassPolymorphicVarConstraints(); 
                                            
            for (int j = 0, nParents = typeClass.getNParentClasses(); j < nParents; ++j) {
                
                TypeClass parentClass = typeClass.getNthParentClass(j);
                
                ClassInstanceIdentifier parentInstanceId = ClassInstanceIdentifier.make(parentClass.getName(), instanceType);
                
                ClassInstance parentInstance = currentModuleTypeInfo.getVisibleClassInstance(parentInstanceId);
                
                if (parentInstance == null) {   
                    //superclass instance declaration is missing                 
                    ParseTreeNode instanceNode = classInstanceMap.get(classInstance.getIdentifier());
                    String requiredParentInstanceName = ClassInstance.getNameWithContext(parentClass, instanceType, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
                    compiler.logMessage(new CompilerMessage(instanceNode, new MessageKind.Error.SuperclassInstanceDeclarationMissing(requiredParentInstanceName, classInstance.getNameWithContext())));
                } 
                   
                List<SortedSet<TypeClass>> parentVarConstraints = parentInstance.getDeclaredPolymorphicVarConstraints();   
                                               
                if (parentVarConstraints.size() != childVarConstraints.size()) {
                    //this situation should be handled by earlier checking
                    throw new IllegalArgumentException();
                } 
                
                for (int varN = 0; varN < parentVarConstraints.size(); ++varN) {
                    //the constraints on the varNth type variable that are not implied by the constraints on the child instance
                    Set<TypeClass> unsatisfiedConstraints = new HashSet<TypeClass>(parentVarConstraints.get(varN));
                    unsatisfiedConstraints.removeAll(childVarConstraints.get(varN));
                    
                    if (!unsatisfiedConstraints.isEmpty()) {
                                                                        
                        ParseTreeNode instanceNode = classInstanceMap.get(classInstance.getIdentifier());
                        // ClassInstanceChecker: the constraints on the instance declaration {classInstance.getNameWithContext()} must 
                        // imply the constraints on the parent instance declaration {parentInstance.getNameWithContext()}.\n In particular, 
                        // the class constraint {((TypeClass)unsatisfiedConstraints.iterator().next()).getName()} on type variable number 
                        // {varN} in the parent instance is not implied.
                        compiler.logMessage(new CompilerMessage(instanceNode, new MessageKind.Error.ConstraintsOnInstanceDeclarationMustImplyConstraintsOnParentInstance( 
                                classInstance.getNameWithContext(), parentInstance.getNameWithContext(), unsatisfiedConstraints.iterator().next().getName().getQualifiedName(), varN)));
                        break;
                    }                                      
                }                                                                                                     
            }                        
        }                                                               
    }    
    
    /**
     * Do various checks on an individual class instance definition. This includes:
     * <ol>
     *   <li> check that the instance name corresponds to a type class and a type constructor e.g. (Eq Int) that
     *        exist and are visible.
     *   <li> check that each instance method is defined only once in the instance
     *   <li> check that there is an instance method corresponding to every class method without a default,
     *        and no additional instance methods.
     * </ol>
     * @param instanceNode
     * @return ClassInstance
     */
    private ClassInstance checkClassInstanceDefinition(ParseTreeNode instanceNode) {
        
        instanceNode.verifyType(CALTreeParserTokenTypes.INSTANCE_DEFN);
        
        ParseTreeNode optionalCALDocNode = instanceNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        ParseTreeNode instanceNameNode = optionalCALDocNode.nextSibling();
        //do most of the checking for the part of the instance declaration that occurs between the "instance" and "where" keywords.         
        ClassInstance classInstance = resolveInstanceName (instanceNameNode);
        
        ParseTreeNode instanceMethodListNode = instanceNameNode.nextSibling();
        instanceMethodListNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD_LIST);
                       
        TypeClass typeClass = classInstance.getTypeClass();
                       
        Set<String> instanceMethodNamesSet = new HashSet<String>();
        
        for (final ParseTreeNode instanceMethodNode : instanceMethodListNode) {
                
            instanceMethodNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD);
           
            ParseTreeNode optionalInstanceMethodCALDocNode = instanceMethodNode.firstChild();
            optionalInstanceMethodCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            ParseTreeNode instanceMethodNameNode = optionalInstanceMethodCALDocNode.nextSibling();
            instanceMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID); 
            String instanceMethodName = instanceMethodNameNode.getText(); 
            
            if (!instanceMethodNamesSet.add(instanceMethodName)) {
                //each instance method can be defined only once
                compiler.logMessage(new CompilerMessage(instanceMethodNameNode, new MessageKind.Error.MethodDefinedMoreThanOnce(instanceMethodName, classInstance.getNameWithContext())));
                continue; 
            }
            
            ClassMethod classMethod = typeClass.getClassMethod(instanceMethodName);
            if (classMethod == null) {
                //instance method must first be declared by the type class that the instance is an instance of
                compiler.logMessage(new CompilerMessage(instanceMethodNameNode, new MessageKind.Error.MethodNotDeclaredByClass(instanceMethodName, typeClass.getName().getQualifiedName())));
                continue;
            }
                        
            ParseTreeNode resolvingFunctionNameNode = instanceMethodNameNode.nextSibling();
            QualifiedName resolvingFunctionName = resolveResolvingFunction(resolvingFunctionNameNode);
            classInstance.addInstanceMethod(classMethod, resolvingFunctionName);                                         
        }
        
        //check that the instance has an instance method defined for each class method in the type class that does not have
        //a default class method.
        if (typeClass.getNClassMethods() != instanceMethodNamesSet.size()) {
            
            //(String set) the class methods that are required to be implemented (because they have no defaults) but were not in this instance.
            Set<String> unimplementedMethodsNamesSet = new LinkedHashSet<String>();
            {
                for (int i = 0, nClassMethods = typeClass.getNClassMethods(); i < nClassMethods; ++i) {
                    ClassMethod classMethod = typeClass.getNthClassMethod(i);
                    if (!classMethod.hasDefaultClassMethod()) {
                        unimplementedMethodsNamesSet.add(classMethod.getName().getUnqualifiedName());
                    }
                }
                
                unimplementedMethodsNamesSet.removeAll(instanceMethodNamesSet);
            }
            
            for (final String methodName : unimplementedMethodsNamesSet) {
                
                // ClassInstanceChecker: the method {methodName} is not defined by the instance {classInstance.getNameWithContext()}.
                compiler.logMessage(new CompilerMessage(instanceNode, new MessageKind.Error.MethodNotDefinedByInstance(methodName, classInstance.getNameWithContext())));
            }
        }
        
        return classInstance;
    }
    
    /**
     * Resolve an instance name such as "Num JBigInteger" to "Prelude.Num BigInteger.JBigInteger".
     * Other examples of instance names: "(Eq a, Eq b) => Eq (Tuple2 a b)" and (Eq a) => Eq (List a)" 
     * In particular, the type class and type constructor defining the instance must be resolvable.
     * 
     * The method returns a partially constructed ClassInstance object and adds it to the current module.
     * 
     * The method also does a number of static checks on the validity of the instance name (i.e. everything
     * that appears between "instance" and "where". See the comments below for details.
     * 
     * @param instanceNameNode
     * @return ClassInstance the partially constructed ClassInstance object.
     */
    private ClassInstance resolveInstanceName(ParseTreeNode instanceNameNode) {
        
        //For the comments in this method we use the following prototype instance declaration:
        //"instance (C1 a1, C2 a2, ..., Cn an) => C (T b1 ... bm) where ..."
        //where Ci = (Ci1, Ci2, .. Cij) where j is a function of i, and the ai are distinct.
        //This is called a C-T instance declaration because it declares that the type T is an instance of the class C.         
        
        instanceNameNode.verifyType(CALTreeParserTokenTypes.INSTANCE_NAME);
        
        ParseTreeNode contextListNode = instanceNameNode.firstChild();
        contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
                
        ParseTreeNode classNameNode = contextListNode.nextSibling();         
        //resolve the class C i.e. make sure it is visible within the current module, and give it a fully qualified name if
        //it was referenced without the module qualifier.      
        TypeClass typeClass = typeClassChecker.resolveClassName(classNameNode);                      
        
        ParseTreeNode instanceTypeConsNameNode = classNameNode.nextSibling();
        
        TypeConstructor typeCons = null;
        //the number of type variables appearing in the CAL source (may be incorrect for the type constructor, in which case we
        //produce a compilation error.
        int nTypeVariables = -1;
        ParseTreeNode firstTypeVarNode = null;       
        
        switch (instanceTypeConsNameNode.getType()) {
        
            case CALTreeParserTokenTypes.GENERAL_TYPE_CONSTRUCTOR:
            case CALTreeParserTokenTypes.UNPARENTHESIZED_TYPE_CONSTRUCTOR:
            {
                ParseTreeNode typeConsNameNode = instanceTypeConsNameNode.firstChild();
                //resolve the type (constructor) T.
                typeCons = dataDeclarationChecker.resolveTypeConsName(typeConsNameNode);
                nTypeVariables = instanceTypeConsNameNode.getNumberOfChildren() - 1;
                firstTypeVarNode = typeConsNameNode.nextSibling();
                break;
            }
            
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR:
            {
                typeCons = TypeConstructor.FUNCTION;
                nTypeVariables = 2; //guaranteed by the parser
                firstTypeVarNode = instanceTypeConsNameNode.firstChild();
                break;
            }
                
            case CALTreeParserTokenTypes.UNIT_TYPE_CONSTRUCTOR:
            {    
                nTypeVariables = 0;
                typeCons = compiler.getTypeChecker().getTypeConstants().getUnitType().rootTypeConsApp().getRoot();               
                firstTypeVarNode = instanceTypeConsNameNode.firstChild();
                break;
            }
            
            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR:
            {
                typeCons = compiler.getTypeChecker().getTypeConstants().getListTypeCons();
                nTypeVariables = 1; //guaranteed by the parser
                firstTypeVarNode = instanceTypeConsNameNode.firstChild();
                break;
            }
            
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR:
            {
                //for "instance (constraints) => C {r} where ..." check that kind (C) == *. 
                try {
                    //after kind inference the kinds of type classes have no kind variables (they are grounded)
                    //so they will not be mutated by the unification below.  
                    KindExpr.unifyKind(typeClass.getKindExpr(), KindExpr.STAR);
                } catch (TypeException typeException) {
                    
                    //"Invalid record instance declaration; the kind of the type class {0} (i.e. {1}) is not equal to the kind *."
                    compiler.logMessage(
                        new CompilerMessage(
                            instanceTypeConsNameNode,
                            new MessageKind.Error.RecordInstanceDeclarationKindClash(
                                typeClass)));                     
                }
                
                String recordVarName = instanceTypeConsNameNode.firstChild().getText();
                RecordType instanceRecordType = checkConstrainedRecordVar(contextListNode, recordVarName);
                return addCurrentClassInstance(instanceNameNode, instanceRecordType, typeClass);                              
            }
            
            default:
            {
                instanceTypeConsNameNode.unexpectedParseTreeNode();
                break;
            }
               
        }  
        
        //Type constructor instance declarations for the Typeable type class are automatically generated by the compiler
        //whenever possible. However, for types whose type variable arguments are not of kind *, such
        //an instance declaration is invalid. Thus a user could theoretically create an instance declaration
        //for the type, causing a security violation since the Typeable instances are used for Dynamic support.
        //
        //In addition, even for the instances where an explicit declaration would create an overlapping instance
        //error, this message is easier to understand.
        if (typeClass.getName().equals(CAL_Prelude.TypeClasses.Typeable) &&
            instanceTypeConsNameNode.getType() != CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR) {
            //"Explicit instance declarations for the Cal.Core.Prelude.Typeable type class are not allowed."
            compiler.logMessage(
                new CompilerMessage(
                    classNameNode,
                    new MessageKind.Error.ExplicitTypeableInstance()));             
            
        }
        
        //check that the type (T b1 ... bm) and the typeClass C have the same kind.       
                                  
        //First we do a simple check that m is not too big. In essence this is a kind-checking error, but we   
        //we can give a friendlier error message than a kind-checking error in the case of over-saturation.
        //note that we cannot assume that the type constructor is fully saturated
        //i.e. this may correctly be an undersaturated application.         
        final int maxNTypeVariables = typeCons.getTypeArity();
        if (nTypeVariables > maxNTypeVariables) {
            // "The type constructor {0} expects at most {1} type argument(s). {2} supplied."
            compiler.logMessage(
                new CompilerMessage(
                    instanceTypeConsNameNode,
                    new MessageKind.Error.TypeConstructorAppliedToOverlyManyArgs(typeCons.getName(), maxNTypeVariables, nTypeVariables)));                
        } 
        
        // the kind of T b1 ... bm where m <= typeCons.getTypeArity is the last element of the kindPieces array
        KindExpr[] kindPieces = typeCons.getKindExpr().getKindPieces(nTypeVariables);        
        KindExpr appliedTypeConsKind = kindPieces[nTypeVariables];
        
        //now do the general check that kind (T b1 ... bm) == kind (type class C).
        
        try {
            //after kind inference the kinds of type classes and type constructors have no kind variables (they are grounded)
            //so they will not be mutated by the unification below.          
            
            KindExpr.unifyKind(appliedTypeConsKind, typeClass.getKindExpr());
            
        } catch (TypeException typeException) {
            
            //"Invalid instance declaration; the kind of the type class {0} (i.e. {1}) is not the same as the kind of the application of the type constructor {2} to {3} type arguments (i.e. {4})." 
             compiler.logMessage(
                new CompilerMessage(
                    instanceTypeConsNameNode,
                    new MessageKind.Error.InstanceDeclarationKindClash(
                        typeClass, typeCons, nTypeVariables, appliedTypeConsKind)));            
        }
                
        
        //Now check that there are no repeated type variables bi and set up some data structures for later checks.
        
        //(String -> Integer) For example, for an instance for Tuple3 d a b then the map is {(d, 0), (a, 1), (b, 2)}.
        Map<String, Integer> varNameToArgNumberMap = new HashMap<String, Integer>();
               
        int argNumber = 0;
        for (ParseTreeNode varNameNode = firstTypeVarNode; varNameNode != null; varNameNode = varNameNode.nextSibling()) {
    
            varNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            String varName = varNameNode.getText();
            if (varNameToArgNumberMap.put(varName, Integer.valueOf(argNumber)) != null) {    
                //Can't have repeated type variables such as instance Eq a => Foo a a
                compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.RepeatedTypeVariableInInstanceDeclaration(varName)));
            }
                                                         
            ++argNumber;            
        }
        
        TypeExpr [] argTypes = checkConstrainedTypeVars(contextListNode, varNameToArgNumberMap, kindPieces);
        TypeConsApp instanceType = new TypeConsApp(typeCons, argTypes);
        
        return addCurrentClassInstance(instanceNameNode, instanceType, typeClass);      
    }  
    
    /**
     * Checks the left hand side of the "=>" in a class instance declaration for validity, and 
     * returns and array of constrained type variables as specified by the constraints.
     * 
     * For example, for constraints such as:
     * (Eq a, Show a, XMLSerializable b, Enum b, Read c) => ...
     * we must verify that all the type classes exist and are resolvable,
     * that the type variables (a, b, c) occur on the right hand side of the =>
     * and then create the appropriate array of constrained type variables. 
     * 
     * @param contextListNode
     * @param varNameToArgNumberMap (String -> Integer) For example, for an instance for Tuple3 d a b then the map is {(d, 0), (a, 1), (b, 2)}
     * @param kindPieces for the instance declaration "instance (constraints) => T (C a0 ... am)" then the elements of this array are the kinds
     *        of a1, a2, ..., an and (T a1 ... an). 
     * @return TypeExpr[] will have the same size as varNameToArgNumberMap
     */
    private TypeExpr[] checkConstrainedTypeVars(ParseTreeNode contextListNode, Map<String, Integer> varNameToArgNumberMap, KindExpr[] kindPieces) {
        
        final int nTypeVariables = varNameToArgNumberMap.size();
        
        //Cij is the jth type class constraint on the ith type variable.
        //Check that each Cij resolves to a type class
        //Check that for each i, the Cij are distinct
        //Check that the type variables a1,..., an are a subset of b1, ..., bm. Note a proper subset is allowed
        //e.g. "instance Music (Maybe a) where..." is allowed.
               
        //constraintSetList.get(i) is the set of qualified names of all the type class constraints on type argument i.
        List<Set<QualifiedName>> constraintSetList = new ArrayList<Set<QualifiedName>>(nTypeVariables);
        for (int i = 0; i < nTypeVariables; ++i) {
            constraintSetList.add(new HashSet<QualifiedName>());
        }     
                                 
        for (final ParseTreeNode contextNode : contextListNode) {
    
            contextNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT);                    

            ParseTreeNode constraintTypeClassNameNode = contextNode.firstChild();
            constraintTypeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);            
            //verify that the type class referred to actually exists, and supply its inferred module name
            //if it was omitted in the user's CAL code.           
            TypeClass constraintTypeClass = typeClassChecker.resolveClassName(constraintTypeClassNameNode);  
                                  
            ParseTreeNode varNameNode = constraintTypeClassNameNode.nextSibling();
            varNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            String varName = varNameNode.getText();
            
            if (!varNameToArgNumberMap.containsKey(varName)) {
                //the type variables a1,..., an must be a subset of b1, ..., bm. Note a proper subset is allowed
                compiler.logMessage(new CompilerMessage(varNameNode,
                    new MessageKind.Error.TypeVariableMustOccurWithingTypeVariableArgumentsOfInstanceDeclaration(varName)));
            }
            
            //this context is a constraint on the type variable argument at index argN
            int argN = varNameToArgNumberMap.get(varName).intValue();
            
            if (!constraintSetList.get(argN).add(constraintTypeClass.getName())) {
                //can't have a duplicate constraint such as (Eq a, Eq a) => Eq (List a)
                compiler.logMessage(new CompilerMessage(varNameNode,
                    new MessageKind.Error.DuplicateConstraint(constraintTypeClass.getName().getQualifiedName(), varName)));
            } 
            
            //if the constraint is Ci aj, then kind Ci == kind aj must hold.
            KindExpr constraintTypeClassKind = constraintTypeClass.getKindExpr();
            KindExpr typeVarKind = kindPieces[argN];
            try {                
                //check that these kinds are the same. They are both grounded (do not involve type variables) so they will not be
                //mutated by the unification.
                KindExpr.unifyKind(constraintTypeClassKind, typeVarKind);
            } catch (TypeException typeException) {
                //"Invalid instance declaration; the kind of the type variable {0} (i.e. {1}) is not the same as the kind of the constraining type class {2} (i.e. {3})."
                compiler.logMessage(new CompilerMessage(varNameNode,
                    new MessageKind.Error.InstanceDeclarationKindClashInConstraint(varName, typeVarKind, constraintTypeClass)));
            }
            
        }
        
        //now create the type expression (C1 a1, C2 a2, ..., Cn an) => (T b1 ... bm).
        
        TypeExpr[] argTypes = new TypeExpr[nTypeVariables];
        for (int i = 0; i < nTypeVariables; ++i) {
                        
            //set of TypeClass constraints on the type variable at argument i.
            SortedSet<TypeClass> constraintSet = TypeClass.makeNewClassConstraintSet();
            Set<QualifiedName> constraintSetNames = constraintSetList.get(i);
            for (final QualifiedName constraintTypeClassName : constraintSetNames) {               
                TypeClass constraintTypeClass = currentModuleTypeInfo.getVisibleTypeClass(constraintTypeClassName);
                if (constraintTypeClass == null) {
                    //can't happen because of the above checks.
                    throw new NullPointerException();
                }
                constraintSet.add(constraintTypeClass);
            }
            
            argTypes[i] = TypeVar.makeTypeVar(null, constraintSet, true);
        }  
                                      
        return argTypes;
    }
    
    /**
     * Checks the left hand side of the "=>" in a class instance declaration for a universal record instance for
     * validity, and returns and SortedSet of type class constraints on the record variable.
     * 
     * For example, for constraints such as:
     * (Eq a, Show a, XMLSerializable b, Enum b, Read c) => ...
     * we must verify that all the type classes exist and are resolvable,
     * that the type variables (a, b, c) occur on the right hand side of the =>
     * and then create the appropriate array of constrained type variables. 
     * 
     * @param contextListNode
     * @param recordVarName for example, for instance Eq r => Eq {r} this is "r".
     * @return RecordType the record type determined by the instance declaration. For example, for instance
     *         Eq r => Eq {r}, this will be Eq r => {r}.
     */
    private RecordType checkConstrainedRecordVar(ParseTreeNode contextListNode, String recordVarName) {
    
        //Cj is the jth type class constraint on the record variable.
        //Check that each Cj resolves to a type class
        //Check that the Cj are distinct
        //Check that the record variables on the lhs of context are all 'recordVarName'.
        //Check that the kind of Cj == * (which is the kind of the record var).
                    
        //constraintSet is the set of qualified names of all the type class constraints on recordVarName.
        Set<QualifiedName> constraintSet = new HashSet<QualifiedName>();          
                                 
        for (final ParseTreeNode contextNode : contextListNode) {
    
            contextNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT);                    

            ParseTreeNode constraintTypeClassNameNode = contextNode.firstChild();
            constraintTypeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);            
            //verify that the type class referred to actually exists, and supply its inferred module name
            //if it was omitted in the user's CAL code.           
            TypeClass constraintTypeClass = typeClassChecker.resolveClassName(constraintTypeClassNameNode);  
                                  
            ParseTreeNode varNameNode = constraintTypeClassNameNode.nextSibling();
            varNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            String varName = varNameNode.getText();
            
            if (!varName.equals(recordVarName)) {    
                //todoBI could add a more specific error message here.
                compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.TypeVariableMustOccurWithingTypeVariableArgumentsOfInstanceDeclaration(varName)));
            }
                                   
            if (!constraintSet.add(constraintTypeClass.getName())) {
                //can't have a duplicate constraint such as (Eq a, Eq a) => Eq {a}
                compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.DuplicateConstraint(constraintTypeClass.getName().getQualifiedName(), varName)));
            } 
            
            //if the constraint is Ci r, then kind Ci == kind r (== *) must hold.
            KindExpr constraintTypeClassKind = constraintTypeClass.getKindExpr();           
            try {                
                //check that these kinds are the same. They are both grounded (do not involve type variables) so they will not be
                //mutated by the unification.
                KindExpr.unifyKind(constraintTypeClassKind, KindExpr.STAR);
            } catch (TypeException typeException) {
                //"Invalid instance declaration; the kind of the type variable {0} (i.e. {1}) is not the same as the kind of the constraining type class {2} (i.e. {3})."
                compiler.logMessage(new CompilerMessage(varNameNode,
                    new MessageKind.Error.InstanceDeclarationKindClashInConstraint(varName, KindExpr.STAR, constraintTypeClass)));
            }            
        }
        
        //now create the type expression (C1 r, C2 r, ..., Cn r) => {r}.
             
        //set of TypeClass constraints on the type variable at argument i.
        SortedSet<TypeClass> typeClassConstraintSet = TypeClass.makeNewClassConstraintSet();        
        for (final QualifiedName constraintTypeClassName : constraintSet) {
            TypeClass constraintTypeClass = currentModuleTypeInfo.getVisibleTypeClass(constraintTypeClassName);
            if (constraintTypeClass == null) {
                //can't happen because of the above checks.
                throw new NullPointerException();
            }
            typeClassConstraintSet.add(constraintTypeClass);
        }
        
        return new RecordType(
            RecordVar.makeRecordVar(recordVarName, Collections.<FieldName>emptySet(), typeClassConstraintSet, true),
            Collections.<FieldName, TypeExpr>emptyMap());        
    }    
                      
    /**
     * Adds the current class instance being parsed to the ModuleTypeInfo.
     * Does a check that there are no overlapping instances prior to making the addition.
     * @param instanceNameNode
     * @param instanceType
     * @param typeClass
     * @return ClassInstance
     */
    private ClassInstance addCurrentClassInstance(ParseTreeNode instanceNameNode, TypeExpr instanceType, TypeClass typeClass) {

        ClassInstance classInstance = new ClassInstance (currentModuleTypeInfo.getModuleName(), instanceType, typeClass, null, InstanceStyle.EXPLICIT); 
                              
        //There cannot be duplicate C-T instances in any class imported either directly or indirectly into the module
        //where this C-T instance is defined. an instance is identified by the pair (C, T). There cannot be 2 instances for the
        //same pair (C, T) in scope in the program. For example, instance Eq a => Ord (Tree a) and instance Ord a => Ord (Tree a)
        //are not both allowed.    
        //       
        //To elaborate, an instance, such as Ord Date defined in a module cannot be defined within any module imported, either
        //directly or indirectly into the module. For example, if module C imports module B and module B imports module A,
        //and within module A, the instance Ord Date is defined, then Ord Date, cannot be defined in module C, even though
        //module C doesn't import module A directly. One way to think of this is that instances have universal scope.  
                             
        ClassInstance otherInstance = currentModuleTypeInfo.getVisibleClassInstance(classInstance.getIdentifier());
        if (otherInstance != null) {                              
           
            //an instance, such as Ord Date, can be defined only once within a module.
            //instances such as "(Eq a) => Ord Maybe a", "Ord Maybe a" and "Ord a => Ord Maybe a"
            //all have the same instance identifiers Ord Maybe, and are not allowed. These
            //are called overlapping instances. We distinguish the error messages for clarity.  
                        
            String declName = classInstance.getNameWithContext();
            String otherDeclName = otherInstance.getNameWithContext();
            MessageKind message; 
            if (instanceType instanceof RecordType) {
                //universal record instance
                
                // The instance {classInstance.getNameWithContext()} overlaps with the record instance {otherInstance.getNameWithContext()} defined in module {otherInstance.getModuleName()}.
                message = new MessageKind.Error.RecordInstancesOverlap(classInstance.getNameWithContext(), otherInstance.getNameWithContext(), otherInstance.getModuleName());
                
            } else {
                //type constructor instances
                
                if (declName.equals(otherDeclName)) {                                                                                    
                    message = new MessageKind.Error.InstanceAlreadyDefined(declName, otherInstance.getModuleName());
                } else {
                    message = new MessageKind.Error.InstanceOverlapsWithInstanceInModule(declName, otherDeclName, otherInstance.getModuleName());
                }
            }
                                       
            compiler.logMessage(new CompilerMessage(instanceNameNode, message));                                                                   
        }
                           
        currentModuleTypeInfo.addClassInstance(classInstance);        
                      
        return classInstance;          
    }         
    
    /**
     * Given an instance declaration "instance C-T where m1 = f1; m2 = f2; ...;" this method
     * checks that the supplied instance method resolving function fi indeed exists and is visible.
     * Note that the resolving function cannot be a class method but must be a true function! 
     * As a side effect, the module name is supplied if it was left out in the source.
     * 
     * @param resolvingFunctionNameNode
     * @return QualifiedName qualified name of the instance method.
     */
    private QualifiedName resolveResolvingFunction(ParseTreeNode resolvingFunctionNameNode) {        
        return resolveMethodName(resolvingFunctionNameNode, compiler, currentModuleTypeInfo);
    }
    
    /**
     * A helper function used to resolve instance method resolving function names and default class method names.
     * This function handles the fact that at this point in type-checking the module, the functions
     * themselves have not been added to the ModuleTypeInfo, and so the usual methods of resolution
     * don't work.         
     * 
     * @param methodNameNode
     * @param compiler
     * @param currentModuleTypeInfo
     * @return QualifiedName
     */
    static QualifiedName resolveMethodName(ParseTreeNode methodNameNode, CALCompiler compiler, ModuleTypeInfo currentModuleTypeInfo) {
                
        methodNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
               
        ParseTreeNode moduleNameNode = methodNameNode.firstChild();
        String maybeModuleName = ModuleNameUtilities.resolveMaybeModuleNameInParseTree(moduleNameNode, currentModuleTypeInfo, compiler, false);
        ParseTreeNode varNameNode = moduleNameNode.nextSibling();
        String varName = varNameNode.getText();

        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();           
             
        if (maybeModuleName.length() > 0) {
            
            ModuleName moduleName = ModuleName.make(maybeModuleName);

            //an explicitly qualified variable
            QualifiedName functionName = QualifiedName.make(moduleName, varName);

            //todoBI this can be improved eventually to use the currentModuleTypeInfo
            //we can't use currentModuleTypeInfo since functions have not been added to it yet. So we do a
            //direct check for the case of the current module.
            if (moduleName.equals(currentModuleTypeInfo.getModuleName())) {
                
                if (!compiler.getTypeChecker().isTopLevelFunctionName(varName)) {
                                        
                    // the function {functionName} is not defined in the current module.
                    compiler.logMessage (new CompilerMessage(methodNameNode, new MessageKind.Error.FunctionNotDefinedInCurrentModule(functionName.getQualifiedName())));
                }
                
                checkResolvedClassMethodReference(compiler, methodNameNode, functionName);
                return functionName;
            }
              
            //todoBI more detailed error messages here for the various failure cases
            //i.e. class method, not visible, etc            
            Function function = currentModuleTypeInfo.getVisibleFunction(functionName);
            
            if (function == null) {
                compiler.logMessage (new CompilerMessage(methodNameNode, new MessageKind.Error.FunctionDoesNotExist(functionName.getQualifiedName())));
                    
            }
            
            checkResolvedClassMethodReference(compiler, methodNameNode, functionName);
            return functionName;            
        }
                            
        if (compiler.getTypeChecker().isTopLevelFunctionName(varName)) {
            
            //the var is defined in the current module.
            
            QualifiedName functionName = QualifiedName.make(currentModuleName, varName);
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, currentModuleName);   

            checkResolvedClassMethodReference(compiler, methodNameNode, functionName);
            return functionName;         
        }
        
        //We now know that the variable can't be defined within the current module.
        //check if it is a "using function" and then patch up the module name.
        
        ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingFunctionOrClassMethod(varName);
        if (usingModuleName != null) {
            QualifiedName usingFunctionName = QualifiedName.make(usingModuleName, varName); 
            Function function = currentModuleTypeInfo.getVisibleFunction(usingFunctionName);
            if (function != null) {
                ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, usingModuleName);

                checkResolvedClassMethodReference(compiler, methodNameNode, usingFunctionName);
                return usingFunctionName;
            }
        
            //we know that usingFunctionName must exist because of earlier static checks. 
            //Since it is not a function, it must be a class method, which is an error here.
            compiler.logMessage (new CompilerMessage(methodNameNode, new MessageKind.Error.FunctionIsClassMethodNotResolvingFunction(usingFunctionName.getQualifiedName())));
                
        }
        
        //We now know that the variable can't be defined within the current module and is not a "using function".
        //Check if it is defined in another module.     
        //This will be an error since the user must supply a module qualification, but we
        //can attempt to give a good error message.     

        List<QualifiedName> candidateEntityNames = new ArrayList<QualifiedName>();
        int nImportedModules = currentModuleTypeInfo.getNImportedModules();
        for (int i = 0; i < nImportedModules; ++i) {

            FunctionalAgent candidate = currentModuleTypeInfo.getNthImportedModule(i).getFunctionOrClassMethod(varName);
            if (candidate != null && currentModuleTypeInfo.isEntityVisible(candidate)) {
                candidateEntityNames.add(candidate.getName());
            }
        }

        int numCandidates = candidateEntityNames.size();
        if(numCandidates == 0) {
            // Attempt to use undefined identifier {varName}.
            compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.AttemptToUseUndefinedFunction(varName)));
            
        } else if(numCandidates == 1) {
            
            QualifiedName candidateName = candidateEntityNames.get(0);
            
            // Attempt to use undefined function {varName}. Was {candidateName} intended?
            compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.AttemptToUseUndefinedFunctionSuggestion(varName, candidateName)));                
            
        } else {
            // The reference to the function {varName} is ambiguous. It could mean any of {candidateEntityNames}. 
            compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.AmbiguousFunctionReference(varName, candidateEntityNames)));
        }
        
        return null;        
    }
    
    /**
     * Performs late static checks on a class method reference that has already been resolved.
     * 
     * Currently, this performs a deprecation check on a class method reference, logging a warning message if the method is deprecated.
     * @param compiler the compiler instance.
     * @param nameNode the parse tree node representing the reference.
     * @param qualifiedName the qualified name of the reference.
     */
    private static void checkResolvedClassMethodReference(final CALCompiler compiler, final ParseTreeNode nameNode, final QualifiedName qualifiedName) {
        if (compiler.getDeprecationScanner().isFunctionOrClassMethodDeprecated(qualifiedName)) {
            compiler.logMessage(new CompilerMessage(nameNode, new MessageKind.Warning.DeprecatedClassMethod(qualifiedName)));
        }
    }

    /**  
     * Given an instance declaration "instance C-T where m1 = f1; m2 = f2; ...;" this method
     * checks that all the instance method resolving functions f1, f2,... have 
     * a type compatible with what is required by the C-T-m triple. For example,
     * the function resolving the Prelude.add class method in the Num Int instance must have type Int -> Int -> Int;
     * <p>
     * This method performs the above check for all instances within the current module. Call this method after all
     * the functions in the module have been type checked.     
     */
    void checkTypesOfInstanceMethodResolvingFunctions() {
       
        for (int i = 0, nClassInstances = currentModuleTypeInfo.getNClassInstances(); i < nClassInstances; ++i) {                       
            
            ClassInstance classInstance = currentModuleTypeInfo.getNthClassInstance(i);
                        
            ParseTreeNode instanceNode = classInstanceMap.get(classInstance.getIdentifier());
            if (instanceNode == null) {
                // This can occur if we are compiling an adjunct to a module that contains instance declarations.
                continue;
            }
            instanceNode.verifyType(CALTreeParserTokenTypes.INSTANCE_DEFN);
                                             
            ParseTreeNode instanceMethodListNode = instanceNode.getChild(2);
            instanceMethodListNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD_LIST);
            
            TypeClass typeClass = classInstance.getTypeClass();          
                                       
            for (final ParseTreeNode instanceMethodNode : instanceMethodListNode) {
                    
                instanceMethodNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD);
               
                ParseTreeNode optionalInstanceMethodCALDocNode = instanceMethodNode.firstChild();
                optionalInstanceMethodCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode classMethodNameNode = optionalInstanceMethodCALDocNode.nextSibling();
                classMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID); 
                String classMethodName = classMethodNameNode.getText(); 
              
                ClassMethod classMethod = typeClass.getClassMethod(classMethodName);                               
                QualifiedName resolvingFunctionName = classMethodNameNode.nextSibling().toQualifiedName();
                
                TypeExpr instanceMethodType;
                {
                    TypeExpr classMethodType = classMethod.getTypeExpr();
                    TypeVar classTypeVar = classMethodType.getTypeClassTypeVar(typeClass.getTypeClassTypeVarName());
                    classTypeVar.setInstance(classInstance.getType());
                    instanceMethodType = classMethodType;
                }
                                               
                TypeExpr resolvingFunctionType = compiler.getTypeChecker().getEntity(resolvingFunctionName).getTypeExpr();               
                
                if (instanceMethodType.getGenericClassConstrainedPolymorphicVars(null).size() != 0 ||
                    resolvingFunctionType.getGenericClassConstrainedPolymorphicVars(null).size() != 0) {
                    //if either the instance method type or the resolving function type involve constrained type vars, then they
                    //must have identically the same type. For example, for equals in the Eq-List instance, the types of the instance
                    //method and the resolving instance must both be: Eq a => [a] -> [a] -> Boolean.
                    if (!instanceMethodType.sameType(resolvingFunctionType)) {
                         compiler.logMessage(new CompilerMessage(classMethodNameNode, new MessageKind.Error.ResolvingFunctionForInstanceMethodHasWrongTypeSpecific(resolvingFunctionName.getQualifiedName(), classMethodName, instanceMethodType.toString(), resolvingFunctionType.toString())));
                             
                    }          
                } else {
                    //we allow extra flexibility here, so that e.g. withing the Eq-Integer instance, we can define fromInteger as id (rather than idInteger).
                    if (!TypeExpr.canPatternMatch(instanceMethodType, resolvingFunctionType, currentModuleTypeInfo)) {
                          compiler.logMessage(new CompilerMessage(classMethodNameNode, new MessageKind.Error.ResolvingFunctionForInstanceMethodHasWrongTypeGeneral(resolvingFunctionName.getQualifiedName(), classMethodName, instanceMethodType.toString(), resolvingFunctionType.toString())));
                    }         
                }                                  
            }            
        }                    
    }                  

    /**
     * Generate the conventional name of a method for a given primitive type.
     * For example, makePrimitiveMethodName("equals", "int") returns Prelude.equalsInt.
     * This relies on the primitive methods' being in the Prelude and having been named 
     * in this conventional fashion.   
     * 
     * @param methodName Name of the method to generate an instance method name for
     * @param primitiveTypeName Primitive Java type to generate an instance method name for
     * @return QualifiedName instance method name for the given method and primitive type 
     */
    private static QualifiedName makePrimitiveInstanceMethodName(String methodName, String primitiveTypeName) {
        
        if(methodName == null || primitiveTypeName == null) {
            throw new IllegalArgumentException("makePrimitiveInstanceMethodName must not be passed null values");
        }
        
        // Trivial case: If we are given the name of a primitive type in CAL form (eg, "Int")
        // then we don't need to do any particular work to get things into the correct form.
        if(Character.isUpperCase(primitiveTypeName.charAt(0))) {
            return QualifiedName.make(CAL_Prelude.MODULE_NAME, methodName + primitiveTypeName);
        }
        
        // Non-trivial case: we need to convert the first character of the primitive type name
        // to upper-case.
        StringBuilder buffer = new StringBuilder(methodName.length() + primitiveTypeName.length());
        buffer.append(methodName);
        buffer.append(Character.toUpperCase(primitiveTypeName.charAt(0)));
        buffer.append(primitiveTypeName.substring(1));
        
        return QualifiedName.make(CAL_Prelude.MODULE_NAME, buffer.toString());
    }
}
