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
 * DataDeclarationChecker.java
 * Created: Jan 18, 2001
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.openquark.cal.internal.compiler.ForeignEntityResolver;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_QuickCheck;
import org.openquark.cal.util.Graph;
import org.openquark.cal.util.VertexBuilder;
import org.openquark.cal.util.VertexBuilderList;


/**
 * A class used to introduce the new types and their associated data constructors as
 * specified via data declarations to the type checker. New types are either built-in,
 * introduced by foreign data declarations or introduced by data declarations.
 * This class also ensures that the data declarations are semantically correct (kind checking).
 * A new instance of this class must be instantiated for each module that needs
 * data declaration checking.
 * Creation date: (1/18/01 1:45:07 PM)
 * @author Bo Ilic
 */
final class DataDeclarationChecker {

    /** Set to true to have debug info printed while running the data declaration checker. */
    private static final boolean DEBUG_INFO = false;

    private final CALCompiler compiler;

    /** Type constructors and data constructors for the current module are added here. */
    private final ModuleTypeInfo currentModuleTypeInfo;
    
    /** 
     * used to resolve type class names in the deriving clause of a foreign or algebraic type definition.
     */
    private final TypeClassChecker typeClassChecker;
       
    static private final TypeConstructor.DerivingClauseInfo[] NO_DERIVING_CLAUSE = new TypeConstructor.DerivingClauseInfo[0];
    
    /**
     * (QualifiedName Set) names of the type classes that are statically allowed to be in the deriving clause
     * of foreign data declarations.
     */
    static private final Set<QualifiedName> possibleForeignTypeDerivingClauseNames = new HashSet<QualifiedName>();
    static {
        possibleForeignTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Outputable);
        possibleForeignTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Inputable);        
        possibleForeignTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Eq);
        possibleForeignTypeDerivingClauseNames.add(CAL_Debug.TypeClasses.Show);
        possibleForeignTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Ord);
    }
    
    /**
     * (QualifiedName Set) names of the type classes that are statically allowed to be in the deriving clause
     * of algebraic data declarations. Further checks are required to establish that a particular data declaration
     * satisfies the requirements for being a derived instance of a particular type class.
     * 
     * For example, the type:
     *    data Foo = Foo (Int -> Int);
     * while algebraic, cannot be made a derived Eq instance (because (Int->Int) is not an Eq instance).
     */    
    static private final Set<QualifiedName> possibleAlgebraicTypeDerivingClauseNames =
        new HashSet<QualifiedName>();
    static {
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Eq);
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Debug.TypeClasses.Show);
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Ord);
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Bounded);
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Enum);
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Outputable);
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Inputable);        
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.IntEnum);        
        possibleAlgebraicTypeDerivingClauseNames.add(CAL_QuickCheck.TypeClasses.Arbitrary);
        
    }

    /**
     * (QualifiedName Set) names of the type classes that are statically allowed to be in the deriving clause
     * of algebraic data declarations for enumeration types.
     */    
    static private final Set<QualifiedName> possibleAlgebraicEnumerationTypeDerivingClauseNames =
        new HashSet<QualifiedName>();
    static {
        possibleAlgebraicEnumerationTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Bounded);
        possibleAlgebraicEnumerationTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.Enum);
        possibleAlgebraicEnumerationTypeDerivingClauseNames.add(CAL_Prelude.TypeClasses.IntEnum);
        possibleAlgebraicEnumerationTypeDerivingClauseNames.add(CAL_QuickCheck.TypeClasses.Arbitrary);
    }

    /**
     * Map (QualifiedName -> Class) 
     * Type classes that can only be derived for foreign types
     * if the foreign type is either a primitive or implements a given interface.  The key is
     * the QualifiedName of the type class, and the value is the Class object for the required 
     * interface.  
     */
    static private final Map<QualifiedName, Class<?>> foreignTypesRequiringPrimitiveOrImplementedInterface = new HashMap<QualifiedName, Class<?>>();
    static {
        foreignTypesRequiringPrimitiveOrImplementedInterface.put(CAL_Prelude.TypeClasses.Ord, Comparable.class);
    }
    
    /**
     * DataDeclarationChecker constructor comment.
     */
    DataDeclarationChecker(final ModuleTypeInfo currentModuleTypeInfo, final CALCompiler compiler, final TypeClassChecker typeClassChecker) {
        if (currentModuleTypeInfo == null || compiler == null || typeClassChecker == null) {
            throw new NullPointerException();
        }
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.compiler = compiler;
        this.typeClassChecker = typeClassChecker;
    }
    
    /**
     * Checks a top-level type expression, which must have kind *.
     * @param typeVarToKindExprMap
     * @param typeExpr
     * @throws TypeException
     */
    void kindCheckTypeExpr (final Map<TypeVar, KindExpr> typeVarToKindExprMap, final TypeExpr typeExpr) throws TypeException {
        final KindExpr kindExpr = analyzeTypeExpr(typeVarToKindExprMap, typeExpr);
        
        try {
            KindExpr.unifyKind(KindExpr.STAR, kindExpr);
        } catch (TypeException typeException) {
            throw new TypeException("The type " + typeExpr + " must have kind * but actually has kind " + kindExpr + ".", typeException);
        }
        
    }

    /**
     * Computes the kind of a type expression and fails in a TypeException if there is a kind error i.e.
     * the type expression is inconsistent with respect to kind unification constraints.
     * 
     * Creation date: (1/24/01 6:55:12 PM)
    
     * @param typeVarToKindExprMap
     * @param typeExpr
     * @return KindExpr
     */
    private KindExpr analyzeTypeExpr(final Map<TypeVar, KindExpr> typeVarToKindExprMap, TypeExpr typeExpr) throws TypeException {

        typeExpr = typeExpr.prune();

        if (typeExpr instanceof TypeVar) {
            //the type variable will be uninstantiated because of the pruning
            if(((TypeVar)typeExpr).getInstance() != null) {
                throw new IllegalStateException("excepting an uninstantiated type variable");
            }
            return typeVarToKindExprMap.get(typeExpr);
            
        } else if (typeExpr instanceof TypeConsApp) {
                
            TypeConsApp typeConsApp = (TypeConsApp) typeExpr;
           
            QualifiedName typeConsName = typeConsApp.getName();               
            KindExpr kindOfAppl = (currentModuleTypeInfo.getVisibleTypeConstructor(typeConsName)).getKindExpr();
            
            for (int i = 0, nArgs = typeConsApp.getNArgs(); i < nArgs; ++i) {        
    
                TypeExpr argTypeExpr = typeConsApp.getArg(i);
    
                //the kind of (typeCons a1 a2 ... ai) 
                KindExpr kindOfArgsSoFar = kindOfAppl;
                KindExpr kindOfNextArg = analyzeTypeExpr(typeVarToKindExprMap, argTypeExpr);
                kindOfAppl = new KindExpr.KindVar();
    
                KindExpr.unifyKind(kindOfArgsSoFar, new KindExpr.KindFunction(kindOfNextArg, kindOfAppl));
            }
    
            return kindOfAppl;
            
        } else if (typeExpr instanceof TypeApp) {
            
            TypeApp typeApp = (TypeApp)typeExpr;
            KindExpr operatorKind = analyzeTypeExpr(typeVarToKindExprMap, typeApp.getOperatorType());
            KindExpr operandKind = analyzeTypeExpr(typeVarToKindExprMap, typeApp.getOperandType());
            KindExpr kindOfAppl = new KindExpr.KindVar();            
            KindExpr.unifyKind(operatorKind, new KindExpr.KindFunction(operandKind, kindOfAppl));
            
            return kindOfAppl;
            
        } else if (typeExpr instanceof RecordType) {

            //each of the types of each of the fields in a record must have kind *.
            
            RecordType recordType = (RecordType)typeExpr;
            SortedMap<FieldName, TypeExpr> hasFieldsMap = recordType.getHasFieldsMap();
            for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                
                FieldName fieldName = entry.getKey();
                TypeExpr fieldTypeExpr = entry.getValue();
                KindExpr fieldKind = analyzeTypeExpr(typeVarToKindExprMap, fieldTypeExpr);
                try {
                    KindExpr.unifyKind(KindExpr.STAR, fieldKind);
                } catch (TypeException typeException) {
                    throw new TypeException("The record field " + fieldName + " of type " + fieldTypeExpr + " must have kind * but actually has kind " + fieldKind + ".", typeException); 
                }
            }
                                    
            //records types, since they always appear in fully saturated form because of their notation, must have kind *.
            return KindExpr.STAR;
        }
        
        throw new IllegalStateException();
    }

    /**
     * Check that the data declarations in the module are correct, and extract the typing
     * information needed to type check the function definitions.
     *
     * Creation date: (1/18/01 1:46:43 PM)
     * @return Env the environment giving the types for all the data constructors defined in the module
     * @param moduleLevelParseTrees     
     */
    Env checkDataDeclarations(final ModuleLevelParseTrees moduleLevelParseTrees) {
      
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        final boolean isPreludeModule = currentModuleName.equals(CAL_Prelude.MODULE_NAME);
        
        //Add the built-in type constructors to the currentModuleTypeInfo.
        
        if (isPreludeModule) {
            
            TypeConstructor.addBuiltInTypes(currentModuleTypeInfo);                                 
        }
        
        final List<ParseTreeNode> dataDeclarationNodes = moduleLevelParseTrees.getDataDeclarationNodes();
        final List<ParseTreeNode> foreignDataDeclarationNodes = moduleLevelParseTrees.getForeignDataDeclarationNodes();
        
        //Check that the names of all types introduced in the module are distinct.
        //Add the names of the foreign types to the currentModuleTypeInfo.
        
        Map<String, ParseTreeNode> typeNameToDataDeclarationNodeMap = checkNamesUsed(dataDeclarationNodes, foreignDataDeclarationNodes);

        Env dataConstructorEnv = null;        

        //Each data declaration determines a set of data constructors, and the type signature of
        //each of these data constructors. This loop extracts this information from the data
        //declarations, without checking for correctness due to kind errors, which is done latter.

        for (final ParseTreeNode dataDeclarationNode : dataDeclarationNodes) {
                   
            //Construct the type expression for the new data type. For example, in
            //data Tree a = Leaf a | Branch (Tree a) (Tree a)
            //this part constructs the type expression "Tree a".
                                                               
            ParseTreeNode optionalCALDocNode = dataDeclarationNode.firstChild();
            optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
            Scope typeScope = CALTypeChecker.getScopeModifier(accessModifierNode);
            
            ParseTreeNode typeNameNode = accessModifierNode.nextSibling();
            String typeName = typeNameNode.getText();

            ParseTreeNode typeConsParamListNode = typeNameNode.nextSibling();
            typeConsParamListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONS_PARAM_LIST);

            Map<String, TypeVar> typeVarNameToTypeVarMap = new HashMap<String, TypeVar>();
            List<TypeVar> typeVarList = new ArrayList<TypeVar>();

            for (final ParseTreeNode typeVarNode : typeConsParamListNode) {

                typeVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String typeVarName = typeVarNode.getText();
                TypeVar typeVarType = new TypeVar(typeVarName);

                //Check that type variable names are not repeated within a single data declaration. 
                //Namely, "data T a a = ..." is not allowed, and later, if we have "data T a b = ..." 
                //then only the type variables a and b can appear on the right hand side.
                if (typeVarNameToTypeVarMap.containsKey(typeVarName)) {
                    compiler.logMessage(new CompilerMessage(typeVarNode, new MessageKind.Error.RepeatedTypeVariable(typeVarName)));
                }

                typeVarNameToTypeVarMap.put(typeVarName, typeVarType);
                typeVarList.add(typeVarType);
            }
            
            TypeConstructor typeCons = currentModuleTypeInfo.getTypeConstructor(typeName);
            TypeExpr[] args = new TypeExpr [typeVarList.size()];
            typeVarList.toArray(args);
            TypeExpr typeConsTypeExpr = new TypeConsApp(typeCons, args);

            //Now iterate over the data constructor, and obtain their types. In other words,
            //we now calculate the types for "Leaf" and "Branch".

            ParseTreeNode dataConsDefnListNode = typeConsParamListNode.nextSibling();
            dataConsDefnListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);
            
            int ordinal = 0;

            for (final ParseTreeNode dataConsDefnNode : dataConsDefnListNode) {

                //Determine the type expression of each data constructor. For example,
                //Leaf :: a -> Tree a

                dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);

                ParseTreeNode dataConsOptionalCALDocNode = dataConsDefnNode.firstChild();
                ParseTreeNode dataConsAccessModifierNode = dataConsOptionalCALDocNode.nextSibling();

                //the declared scope of a data constructor may not be its actual scope. A data constructor
                //cannot be more visible than its type constructor.
                //e.g. data private Foo = public MakeFoo; //MakeFoo has private scope.
                Scope declaredDataConsScope = CALTypeChecker.getScopeModifier(dataConsAccessModifierNode);                               
                Scope dataConsScope = declaredDataConsScope.min(typeScope);                            

                ParseTreeNode dataConsNameNode = dataConsAccessModifierNode.nextSibling();
                String dataConsName = dataConsNameNode.getText();

                ParseTreeNode dataConsArgListNode = dataConsNameNode.nextSibling();
                dataConsArgListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_ARG_LIST);

                final int nDataConsArgs = dataConsArgListNode.getNumberOfChildren();
                TypeExpr[] argTypesArray = new TypeExpr[nDataConsArgs];
                boolean[] argStrictnessArray = new boolean[nDataConsArgs]; 

                FieldName[] fieldNamesArray = new FieldName[nDataConsArgs]; 
                Set<FieldName> fieldNamesSet = new HashSet<FieldName>();
                int argN = 0;
                
                for (final ParseTreeNode dataConsArgNode : dataConsArgListNode) {
                    
                    dataConsArgNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAMED_ARG);
                    
                    // Get the name.
                    ParseTreeNode dataConsArgNameNode = dataConsArgNode.firstChild();
                    FieldName fieldName = FieldName.make(dataConsArgNameNode.getText());
                    
                    // Get the type node.
                    ParseTreeNode maybePlingTypeExprNode = dataConsArgNameNode.nextSibling();
                    
                    if (!fieldNamesSet.add(fieldName)) {
                        // Repeated argument name {fieldName.getCalSourceForm()} in data constructor. 
                        compiler.logMessage(new CompilerMessage(dataConsArgNameNode, new MessageKind.Error.RepeatedFieldNameInDataConstructorDeclaration(fieldName)));
                    }                              
                    
                    fieldNamesArray[argN] = fieldName;
                        
                    ParseTreeNode typeExprNode;
                    if (maybePlingTypeExprNode.getType() == CALTreeParserTokenTypes.STRICT_ARG) {
                        typeExprNode = maybePlingTypeExprNode.firstChild();
                        argStrictnessArray[argN] = true;
                    } else {
                        typeExprNode = maybePlingTypeExprNode;                        
                    }
                    
                    
                    argTypesArray[argN] = determineTypeExpr(typeExprNode, typeVarNameToTypeVarMap);
                    
                    ++argN;
                }

                TypeExpr dataConsTypeExpr = typeConsTypeExpr;

                for (int j = nDataConsArgs - 1; j >= 0; --j) {
                    dataConsTypeExpr = TypeExpr.makeFunType(argTypesArray[j], dataConsTypeExpr);
                }
                     
                DataConstructor dataCons = 
                    new DataConstructor(QualifiedName.make(currentModuleName, dataConsName), dataConsScope, 
                                        fieldNamesArray, dataConsTypeExpr, argStrictnessArray, ordinal);
                dataConstructorEnv = Env.extend(dataConstructorEnv, dataCons);
                typeCons.addDataConstructor(dataCons);
                
                ordinal++;
            }
        }

        checkKinds(dataConstructorEnv, dataDeclarationNodes, typeNameToDataDeclarationNodeMap);    

        return dataConstructorEnv;
    }

    /**
     * The main method for performing kind checking for all the type definitions introduced using data
     * declarations.
     * Creation date: (1/25/01 1:50:16 PM)
     * @param dataConstructorEnv Env environment for all the data constructors defined in the module
     * @param typeNameToDataDeclarationNodeMap (String->ParseTreeNode). 
     *   It is necessary to traverse the data declarations more than once for typing them.  We use this map to provide quick access.
     *   The domain of this map contain the names of all non built-in type names defined within the current module 
     *   A qualified name is not required since the module name is implicitly given.
     */
    private void checkKinds(final Env dataConstructorEnv, final List<ParseTreeNode> dataDeclarationNodes, final Map<String, ParseTreeNode> typeNameToDataDeclarationNodeMap) {

        //Reorder the data declarations so that dependees are processed before dependents
        final Set<String> declaredTypeNamesSet = typeNameToDataDeclarationNodeMap.keySet();
        final Graph<String> g = performTypeConstructorDependencyAnalysis(dataDeclarationNodes, declaredTypeNamesSet);

        if (DEBUG_INFO) {

            System.out.println("" + dataConstructorEnv);
        }
        
        //Kind check the type definitions. Proceed one strongly connected component at a time.

        for (int i = 0, nComponents = g.getNStronglyConnectedComponents(); i < nComponents; ++i) {

            final Graph<String>.Component component = g.getStronglyConnectedComponent(i);
           
            //Infer the values of the kind variables by inference from the type expressions used as arguments
            //in the definition of the type constructors.
            //e.g. "data T a1 ... an = MakeT1 te_1 ... te_m | MakeT2 ..."
            //Then we determine the kind of each of the type expressions te_1, ..., te_m involved in the
            //definition of the data constructor MakeT1. We then do the same for the rest of the data types
            //in the component.

            //e.g. data T a b c =  Cons1 te1 te2 | Cons2 te

            final int componentSize = component.size();

            for (int j = 0; j < componentSize; ++j) {

                final String typeName = component.getVertex(j).getName();
                final ParseTreeNode dataDeclarationNode = typeNameToDataDeclarationNodeMap.get(typeName);
                dataDeclarationNode.verifyType(CALTreeParserTokenTypes.DATA_DECLARATION);

                final ParseTreeNode dataConsDefnListNode = dataDeclarationNode.getChild(4);
                dataConsDefnListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);

                final Map<TypeVar, KindExpr> typeVarToKindExprMap = makeTypeVarToKindVarMap(dataConstructorEnv, dataConsDefnListNode);

                for (final ParseTreeNode dataConsDefnNode : dataConsDefnListNode) {

                    dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);

                    final String dataConsName = dataConsDefnNode.getChild(2).getText();

                    final FunctionalAgent dataConsEntity = dataConstructorEnv.retrieveEntity(dataConsName);                            
                    final TypeExpr dataConsTypeExpr = dataConsEntity.getTypeExprExact();

                    final TypeExpr[] typePieces = dataConsTypeExpr.getTypePieces();

                    for (int k = 0, nDataConsArgs = typePieces.length - 1; k < nDataConsArgs; ++k) {

                        try {                            
                            kindCheckTypeExpr(typeVarToKindExprMap, typePieces[k]);
                        } catch (TypeException typeException) {
                            // DataDeclarationChecker: Kind error in the data constructor {dataConsName} for type {typeName}.
                            compiler.logMessage(
                                new CompilerMessage(
                                    dataConsDefnNode,
                                    new MessageKind.Error.KindErrorInDataConstructorForType(dataConsName, typeName),
                                    typeException));
                        }
                    }
                }
            }

            //Now bind the remaining kind variables to *. This must be done before kind checking the next component.

            for (int j = 0; j < componentSize; ++j) {

                final String typeName = component.getVertex(j).getName(); 
                final TypeConstructor typeCons = currentModuleTypeInfo.getTypeConstructor(typeName);                
                typeCons.finishedKindChecking();                                              
              
                if (DEBUG_INFO) {
                    System.out.println("Type: " + typeName + " Comp #" + i);
                    System.out.println("Kind:     " + typeCons.getKindExpr().toString());                   
                    System.out.println("");
                }
            }
        }
    }
    
    /**
     * Checks that there is only one type declaration for each data type.
     * Note: a type may be built-in, introduced through a foreign data declaration, or introduced
     * through a non-foreign data declaration.
     * Also, checks that data constructor names are unique throughout the module.
     * These can be thought of as "global" symbol checks. There are other symbol checks, such that the type variables
     * in a given data declaration all have distinct symbols, but these are performed later.
     * 
     * Also checks that the type-classes referred to in the derived clauses actually exist and satisfy some simple
     * static checks.
     *
     * Creation date: (1/18/01 1:48:03 PM)
     * @param dataDeclarationNodes
     * @param foreignDataDeclarationNodes
     * @return Map (String->ParseTreeNode) from unqualified type name to DataDeclarationNode.
     *   It is necessary to traverse the data declarations more than once for typing them.  We provide this map to enable quick access.
     *   The domain of this map contain the names of all non built-in type names defined within the current module 
     *   A qualified name is not required since the module name is implicitly given.
     */
    private Map<String, ParseTreeNode> checkNamesUsed(final List<ParseTreeNode> dataDeclarationNodes, final List<ParseTreeNode> foreignDataDeclarationNodes) {
               
        //the names of all the non built-in data constructors used in the module
        final Set<String> dataConstructorNamesSet = new HashSet<String>();
        
        final Set<String> foreignTypeConstructorNamesSet = new HashSet<String>();

        //Check to see that there is only 1 data declaration for a given data type and cache
        //the access to the data declaration Nodes for later use.
                
        for (final ParseTreeNode foreignDataDeclarationNode : foreignDataDeclarationNodes) {
                       
            foreignDataDeclarationNode.verifyType(CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION);
            
            final ParseTreeNode optionalCALDocNode = foreignDataDeclarationNode.firstChild();
            optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            final ParseTreeNode implementationScopeNode = optionalCALDocNode.nextSibling();
            Scope implementationScope = CALTypeChecker.getScopeModifier(implementationScopeNode);  
      
            final ParseTreeNode externalNameNode = implementationScopeNode.nextSibling();
            externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);                                           
            final String externalName = StringEncoder.unencodeString(externalNameNode.getText ());
                          
            final ParseTreeNode accessModifierNode = externalNameNode.nextSibling();
            final Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);                 
            
            final ParseTreeNode typeNameNode = accessModifierNode.nextSibling();
            typeNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);                                   
            final String typeName = typeNameNode.getText();
                                   
            final boolean isBuiltInTypeName = TypeConstructor.getBuiltInType(QualifiedName.make(currentModuleTypeInfo.getModuleName(), typeName)) != null;                  

            if (isBuiltInTypeName) {
                //foreign type name is the same as a built-in type name
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.ForeignTypeHasSameNameAsBuiltInType(typeName)));                        
            }
                                   
            if (foreignTypeConstructorNamesSet.contains(typeName)) {
                //foreign type name is defined more than once
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.RepeatedDefinitionOfForeignType(typeName)));
            }
            
            final ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingTypeConstructor(typeName); 
            if (usingModuleName != null) {
                //there is already an 
                //import 'usingModuleName' using typeConstructor = 'typeName';
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.ForeignFunctionNameAlreadyUsedInImportUsingDeclaration(typeName, usingModuleName)));
            }                
            
            //Now create the type constructor entity object and add it to currentModuleTypeInfo.                   
            //todoBI we check that the Java type can be initialized at compile time. This may be
            //too early in certain cases, but it is handy during compiler development. May want to
            //later have a pragma to turn off the check until run-time.
            
            foreignTypeConstructorNamesSet.add(typeName);
            
            Class<?> foreignType = ForeignEntityResolver.getPrimitiveType(externalName);
            if (foreignType == null) {
                
                final ForeignEntityResolver.ResolutionResult<Class<?>> classResolution = ForeignEntityResolver.resolveClass(ForeignEntityResolver.javaSourceReferenceNameToJvmInternalName(externalName), currentModuleTypeInfo.getModule().getForeignClassLoader());
                final ForeignEntityResolver.ResolutionStatus resolutionStatus = classResolution.getStatus();
                
                foreignType = classResolution.getResolvedEntity();
                
                if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SUCCESS) {
                    // the resolution was successful, so no need to report errors 
                    
                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NO_SUCH_ENTITY) {
                    compiler.logMessage(
                        new CompilerMessage(
                            typeNameNode,
                            new MessageKind.Error.ExternalClassNotFound(externalName, typeName)));
                    
                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND) {
                    // The Java class {notFoundClass} was not found.  This class is required by {externalName}.
                    compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.DependeeJavaClassNotFound(classResolution.getAssociatedMessage(), externalName)));
                    
                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_LOAD_CLASS) {
                    // The definition of Java class {externalName} could not be loaded.
                    compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.JavaClassDefinitionCouldNotBeLoaded(externalName)));
                    
                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_INITIALIZE_CLASS) {
                    compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.ExternalClassCouldNotBeInitialized(externalName, typeName)));
                    
                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.LINKAGE_ERROR) {
                    // The java class {externalName} was found, but there were problems with using it.
                    // Class:   {LinkageError.class}
                    // Message: {e.getMessage()}
                    compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.ProblemsUsingJavaClass(externalName, (LinkageError)classResolution.getThrowable())));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NOT_ACCESSIBLE) {
                    //"The Java type ''{0}'' is not accessible. It does not have public scope or is in an unnamed package."
                    //the parameter {0} will be replaced by "class java.lang.Foo" or "interface java.lang.Foo" as appropriate.
                    compiler.logMessage(
                        new CompilerMessage(
                            externalNameNode,
                            new MessageKind.Error.ExternalClassNotAccessible(foreignType)));
                    
                } else {
                    // Some other unexpected status
                    throw new IllegalStateException("Unexpected status: " + resolutionStatus);
                }
            }
                         
            final QualifiedName calTypeName = QualifiedName.make(currentModuleTypeInfo.getModuleName(), typeName);
            final ForeignTypeInfo foreignTypeInfo = new ForeignTypeInfo(calTypeName, ForeignEntityProvider.makeStrict(foreignType), implementationScope);
          
            
            final ParseTreeNode derivingClauseNode = typeNameNode.nextSibling();
            final TypeConstructor.DerivingClauseInfo[] derivingClauseTypeClassNames = checkDerivingClauseNames(derivingClauseNode, true, foreignType);            
          
            final TypeConstructor typeCons =
                TypeConstructor.makeTypeConstructor(calTypeName, scope, KindExpr.STAR, foreignTypeInfo, derivingClauseTypeClassNames, null);
            
            currentModuleTypeInfo.addTypeConstructor(typeCons);                       
        }
        
        
        final Map<String, ParseTreeNode> typeNameToDataDeclarationNodeMap = new HashMap<String, ParseTreeNode>();   
    
        for (final ParseTreeNode dataDeclarationNode : dataDeclarationNodes) {
                      
            dataDeclarationNode.verifyType(CALTreeParserTokenTypes.DATA_DECLARATION);
                                      
            //Collect the names of the types used in the program. 
            
            final ParseTreeNode optionalCALDocNode = dataDeclarationNode.firstChild();
            optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            final ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
            final Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);  

            final ParseTreeNode typeNameNode = accessModifierNode.nextSibling();
            typeNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);                    
            final String typeName = typeNameNode.getText();

            final boolean isBuiltInTypeName =
                TypeConstructor.getBuiltInType(QualifiedName.make(currentModuleTypeInfo.getModuleName(), typeName)) != null;                                        
   
            if (isBuiltInTypeName) {
                //type name is the same as a built-in type name
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.TypeHasSameNameAsBuiltInType(typeName)));
                    
            }
            
            if (foreignTypeConstructorNamesSet.contains(typeName)) {
                //type name is the same as a type introduced by a foreign data declaration  
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.TypeHasSameNameAsForeignType(typeName)));
            }
            
            if (typeNameToDataDeclarationNodeMap.containsKey(typeName)) {
                //type name is defined more than once
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.RepeatedDefinitionOfType(typeName)));
            }
            
            final ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingTypeConstructor(typeName); 
            if (usingModuleName != null) {
                //there is already an 
                //import 'usingModuleName' using typeConstructor = 'typeName';
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.TypeConstructorAlreadyUsedInImportUsingDeclaration(typeName, usingModuleName)));
            }              

            //add a reference to the parseTree to the map so that it can be looked up later
            //when typing a component
            typeNameToDataDeclarationNodeMap.put(typeName, dataDeclarationNode);

            //Data constructor names must be unique within the entire module. 

            final ParseTreeNode dataConsDefnListNode = typeNameNode.nextSibling().nextSibling();
            dataConsDefnListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);

            final ParseTreeNode derivingClauseNode = dataConsDefnListNode.nextSibling();
            final TypeConstructor.DerivingClauseInfo[] derivingClauseTypeClassNames = checkDerivingClauseNames(derivingClauseNode, false, null); 

            // determine whether we need to perform additional checks to see whether
            // the data type being declared is an enumeration type or a non-polymorphic type.
            QualifiedName enumerationOnlyDerivedTypeClassName = null;
            
            for (final TypeConstructor.DerivingClauseInfo typeClass : derivingClauseTypeClassNames) {
                if (possibleAlgebraicEnumerationTypeDerivingClauseNames.contains(typeClass.getName())) {
                    enumerationOnlyDerivedTypeClassName = typeClass.getName();
                }
            }
            
            for (final ParseTreeNode dataConsDefnNode : dataConsDefnListNode) {

                dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);
                final ParseTreeNode dataConsNameNode = dataConsDefnNode.getChild(2);
                
                final String dataConsName = dataConsNameNode.getText();
                               
                if (!dataConstructorNamesSet.add(dataConsName)) {                        
                    // Data constructor names must be unique within a module.
                    compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.RepeatedDefinitionOfDataConstructor(dataConsName)));
                }
                
                final ModuleName usingModuleNameForDataConstructor = currentModuleTypeInfo.getModuleOfUsingDataConstructor(dataConsName); 
                if (usingModuleNameForDataConstructor != null) {
                    //there is already an 
                    //import 'usingModuleNameForDataConstructor' using dataConstructor = 'dataConsName';
                    compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.DataConstructorNameAlreadyUsedInImportUsingDeclaration(dataConsName, usingModuleNameForDataConstructor)));
                        
                }                                               
                
                if (enumerationOnlyDerivedTypeClassName != null) {
                    final ParseTreeNode dataConsArgListNode = dataConsDefnNode.getChild(3);
                    dataConsArgListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_ARG_LIST);
                    
                    if (!dataConsArgListNode.hasNoChildren()) {
                        // the data type being declared is not an enumeration type, and therefore cannot be a derived instance of Bounded or Enum
                        compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.TypeClassInDerivingClauseRequiresEnumerationType(enumerationOnlyDerivedTypeClassName)));
                    }
                }
            }
            
            // A phantom type cannot be made a derived instance of the Prelude.Enum and Prelude.Bounded type classes.
            // Since derived Prelude.Enum and Prelude.Bounded instances are also required to be pure enumeration types,
            // (a requirement that is already checked by the code immediately above)
            // we can simply check to see whether the type constructor has any parameters to determine
            // whether the type is a phantom type.
            if (enumerationOnlyDerivedTypeClassName != null) {
                final ParseTreeNode typeConsParamListNode = typeNameNode.nextSibling();
                typeConsParamListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONS_PARAM_LIST);
                
                if (typeConsParamListNode.getFirstChild() != null) {
                    // the data type being declared is a polymorphic type, and therefore cannot be a derived instance of Bounded or Enum
                    compiler.logMessage(new CompilerMessage(typeConsParamListNode, new MessageKind.Error.TypeClassInDerivingClauseRequiresNonPolymorphicType(enumerationOnlyDerivedTypeClassName)));
                }
            }
            
            //add the type constructor to the moduleTypeInfo. Note that the kinds will need to be updated after kind checking.                    
            //Add the initial guess of the kind of each type constructor to the typeConsNameToKindMap.
            //e.g. for "data T a b c = ..." then T has kind k1->(k2->(k3->*)) where k1, k2, and k3 are kind
            //variables. The actual kinds of the kind variables are determined by kind inference
 
            final ParseTreeNode typeConsParamListNode = typeNameNode.nextSibling();
            typeConsParamListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONS_PARAM_LIST);

            KindExpr kindExpr = KindExpr.STAR;
            for (int j = typeConsParamListNode.getNumberOfChildren(); j > 0; --j) {
                kindExpr = new KindExpr.KindFunction(new KindExpr.KindVar(), kindExpr);
            }
            
            final TypeConstructor typeCons =
                TypeConstructor.makeTypeConstructor(QualifiedName.make(currentModuleTypeInfo.getModuleName(), typeName), scope, kindExpr, null, derivingClauseTypeClassNames, null);
                                        
            currentModuleTypeInfo.addTypeConstructor(typeCons);                                                           
        }
        
        return typeNameToDataDeclarationNodeMap;
    }
    
    /**
     * Checks that the deriving clause of a foreign or algebraic data declaration
     * a) contains only resolvable type class names
     * b) has no duplicate type class names
     * c) only makes use of supported type classes
     * 
     * There are additional checks that need to be made later- this is only simple static checking.
     * For example:
     * a) certain types cannot be derived instances of Eq because their constituent types can't be e.g. data Foo a = For (a -> a);
     * b) cannot have overlapping instances
     *      
     * @param derivingClauseNode ParseTreeNode for the deriving clause
     * @param isForeignType True if the deriving clause is attached to a foreign data declaration
     * @param foreignType Class of the foreign type (for foreign data declarations).  Ignored for algebraic data declarations.
     * @return QualifiedName[] names of the type classes in the deriving clause in declaration order.   
     */
    private TypeConstructor.DerivingClauseInfo[] checkDerivingClauseNames(final ParseTreeNode derivingClauseNode, final boolean isForeignType, final Class<?> foreignType) {
                       
        if (derivingClauseNode != null) {
            
            derivingClauseNode.verifyType(CALTreeParserTokenTypes.LITERAL_deriving);
            
            if(isForeignType && foreignType == null) {
                throw new IllegalArgumentException("checkDerivingClauseNames must be passed a non-null foreignType for foreign data declarations");
            }
            
            //QualifiedName Set. Ordered by declaration order of the type class names in the deriving clause.
            final Set<TypeConstructor.DerivingClauseInfo> typeClassNamesSet = new LinkedHashSet<TypeConstructor.DerivingClauseInfo>(); 
            
            for (final ParseTreeNode typeClassNameNode : derivingClauseNode) {
                
                final TypeClass typeClass = typeClassChecker.resolveClassName(typeClassNameNode);
                //note we check that typeClass != null in order to be able to continue compilation even if the type class name
                //was not successfully resolved.
                if (typeClass != null) {
                    
                    final QualifiedName typeClassName = typeClass.getName();
                    if (!typeClassNamesSet.add(new TypeConstructor.DerivingClauseInfo(typeClassName, typeClassNameNode.getAssemblySourceRange()))) {
                        //Repeated type class name <name> in deriving clause.
                        compiler.logMessage(new CompilerMessage(typeClassNameNode, new MessageKind.Error.RepeatedTypeClassNameInDerivingClause(typeClassName)));
                        continue;
                    }
                    
                    if (isForeignType && !possibleForeignTypeDerivingClauseNames.contains(typeClassName) ||
                        !isForeignType && !possibleAlgebraicTypeDerivingClauseNames.contains(typeClassName)) {
                        
                        // Note: for the type classes in possibleAlgebraicEnumerationTypeDerivingClauseNames,
                        //       we need to do an additional check later on to make sure that the type being
                        //       declared is indeed an enumeration type.
                        
                        //Unsupported type class name <name> in deriving clause.
                        compiler.logMessage(new CompilerMessage(typeClassNameNode, new MessageKind.Error.UnsupportedTypeClassNameInDerivingClause(typeClassName)));
                    }
                    
                    if(isForeignType) {
                        // Check that foreign types inherit from prerequisite interfaces
                        Class<?> requiredInterface = foreignTypesRequiringPrimitiveOrImplementedInterface.get(typeClassName);
                        if(requiredInterface != null && !foreignType.isPrimitive() && !requiredInterface.isAssignableFrom(foreignType)) {
                            // "The type class {0} in the deriving clause requires that the foreign type being imported either implement {1} or be a Java primitive"
                            compiler.logMessage(new CompilerMessage(typeClassNameNode, new MessageKind.Error.TypeClassInDerivingClauseRequiresPrimitiveOrImplementedInterface(typeClassName, requiredInterface)));
                        }
                    }
                }                                       
            }
            
            return typeClassNamesSet.toArray(NO_DERIVING_CLAUSE);
            
        } else {
            return NO_DERIVING_CLAUSE;
        }
    }
    
    /**
     * Given a type expression parse tree, and a map with all the type variables that can appear in it,
     * this method returns the resulting type expression.
     * 
     * Creation date: (1/22/01 1:06:30 PM)
     * @return TypeExpr
     * @param parseTree
     * @param typeVarNameToTypeMap (String -> TypeExpr) 
     */
    private TypeExpr determineTypeExpr(final ParseTreeNode parseTree, final Map<String, TypeVar> typeVarNameToTypeMap) {

        //todoBI
        //This method is rather similar (but not identical to) TypeChecker.calculateDeclaredType
        //investigate how to factor better.
        //The main differences are that type expressions appearing within data declarations cannot
        //have unbound typeVars e.g. data FooBar a = MakeFooBar b gives an error since b is unbound.
        //Also they cannot involve record variables.        
        
        switch (parseTree.getType()) {
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR :
            {
                ParseTreeNode domainNode = parseTree.firstChild();
                TypeExpr domain = determineTypeExpr(domainNode, typeVarNameToTypeMap);
                TypeExpr codomain = determineTypeExpr(domainNode.nextSibling(), typeVarNameToTypeMap);
                return TypeExpr.makeFunType(domain, codomain);
            }

            case CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR :
            {               
                if (parseTree.hasNoChildren()) {
                    return compiler.getTypeChecker().getTypeConstants().getUnitType();
                }

                if (parseTree.hasExactlyOneChild()) {                
                    // the type (t) is equivalent to the type t.
                    return determineTypeExpr(parseTree.firstChild(), typeVarNameToTypeMap);
                }
                                                   
                final Map<FieldName, TypeExpr> fieldToTypeMap = new HashMap<FieldName, TypeExpr>(); //FieldName -> TypeExpr
                int componentN = 1;
                for (final ParseTreeNode componentNode : parseTree) {

                    final TypeExpr componentTypeExpr = determineTypeExpr(componentNode, typeVarNameToTypeMap);
                    fieldToTypeMap.put(FieldName.makeOrdinalField(componentN), componentTypeExpr);
                    ++componentN;
                }

                return new RecordType(RecordVar.NO_FIELDS, fieldToTypeMap);                
            }

            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR :
            {
                final TypeExpr elementTypeExpr = determineTypeExpr(parseTree.firstChild(), typeVarNameToTypeMap);
                return compiler.getTypeChecker().getTypeConstants().makeListType(elementTypeExpr);
            }

            case CALTreeParserTokenTypes.TYPE_APPLICATION :
            {               
                if (parseTree.hasExactlyOneChild()) {                
                    //not really an application node, but an artifact of parsing
                    return determineTypeExpr(parseTree.firstChild(), typeVarNameToTypeMap);
                }
               
                //we preferentially construct a TypeConsApp over a TypeApp where both are technically
                //valid representations of the type. This is mainly because type inference is a little
                //simpler (as well as more firmly tested) in the earlier case.
                
                final ParseTreeNode firstChildNode = parseTree.firstChild();

                if (firstChildNode.getType() == CALTreeParserTokenTypes.QUALIFIED_CONS) {
                    
                    final TypeConstructor typeCons = resolveTypeConsName(firstChildNode);
                                   
                    //note that we cannot assume that the type constructor is fully saturated
                    //i.e. this may correctly be an undersaturated application.
                    //We can give a friendlier error message than a kind-checking error in the case of oversaturation. 
                    final int nArgs = parseTree.getNumberOfChildren() - 1;                
                    final int maxNArgs = typeCons.getTypeArity();
                    if (nArgs > maxNArgs) {
                        // The type constructor {typeConsEntity.getName()} expects at most {maxNArgs} type argument(s). {nArgs} supplied.
                        compiler.logMessage(
                            new CompilerMessage(
                                firstChildNode,
                                new MessageKind.Error.TypeConstructorAppliedToOverlyManyArgs(typeCons.getName(), maxNArgs, nArgs)));
                    }
                                                    
                    final TypeExpr[] args = new TypeExpr[nArgs];                             
                    int argN = 0;
                    for (final ParseTreeNode argNode : firstChildNode.nextSiblings()) {
                             
                        args[argN] = determineTypeExpr(argNode, typeVarNameToTypeMap);
                        
                        ++argN;
                    }
                                  
                    return new TypeConsApp(typeCons, args);
                }
                
                TypeExpr partialApp = determineTypeExpr(firstChildNode, typeVarNameToTypeMap);
                for (final ParseTreeNode argNode : firstChildNode.nextSiblings()) {
                    
                    partialApp = new TypeApp(partialApp, determineTypeExpr(argNode, typeVarNameToTypeMap));                        
                }
                
                return partialApp;                        
            }

            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            { 
                final TypeConstructor typeCons = resolveTypeConsName(parseTree);
                //note that we cannot assume that the type constructor is non-parametric 
                //i.e. this may be an undersaturated application.
                //For example, for data T f = MakeT field :: (f List); 
                //this case will be called for List. (The kind of T is (* -> *) -> *)                
                return new TypeConsApp(typeCons, null);                
            }

            case CALTreeParserTokenTypes.VAR_ID :
            {
                final String typeVarName = parseTree.getText();
                final TypeVar typeVar = typeVarNameToTypeMap.get(typeVarName);
                if (typeVar == null) {
                    compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Error.TypeVariableMustAppearOnLHSOfDataDeclaration(typeVarName)));
                }
                
                return typeVar;
            }
                       
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR :
            {
                final ParseTreeNode recordVarNode = parseTree.firstChild();
                recordVarNode.verifyType(CALTreeParserTokenTypes.RECORD_VAR);
                
                final RecordVar recordVar;
                final ParseTreeNode recordVarNameNode = recordVarNode.firstChild();
                if (recordVarNameNode != null) {
                    
                    //a record-polymorphic record
                    // Record variables, such as {recordVarNameNode.getText()} cannot appear in data declarations.
                    compiler.logMessage(new CompilerMessage(recordVarNameNode, new MessageKind.Error.RecordVarsCannotAppearInDataDeclarations(recordVarNameNode.getText())));
                    return null;                        
                                       
                } else {
                    //a non record-polymorphic record
                    recordVar = RecordVar.NO_FIELDS;
                }
                
                final ParseTreeNode fieldTypeAssignmentListNode = recordVarNode.nextSibling();
                fieldTypeAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT_LIST);
                
                //FieldName -> TypeExpr
                final Map<FieldName, TypeExpr> extensionFieldsMap = new HashMap<FieldName, TypeExpr>(); 
                
                for (final ParseTreeNode fieldTypeAssignmentNode : fieldTypeAssignmentListNode) {
                         
                    fieldTypeAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT);
                    
                    final ParseTreeNode fieldNameNode = fieldTypeAssignmentNode.firstChild();
                    final FieldName fieldName = compiler.getTypeChecker().getFieldName(fieldNameNode);
                    
                    final ParseTreeNode typeNode = fieldNameNode.nextSibling();
                    final TypeExpr type = determineTypeExpr(typeNode, typeVarNameToTypeMap);
                    extensionFieldsMap.put(fieldName, type);                   
                }
                                
                return new RecordType(recordVar, extensionFieldsMap);                 
            }           
            
            default :
            {            
                parseTree.unexpectedParseTreeNode();               
                break;
            }
        }

        return null;
    }
    
    /**
     * Find the types that are used within the definition of a given type in its data declaration.
     *
     * Creation date: (1/19/01 12:25:29 PM)
     * @param parseTree
     * @param typeVarNamesSet (set of Strings) the names of the type variables used in a particular data declaration
     *        e.g. Either a b = Left a | Right b -- the type variables are a and b.
     * @param freeTypesSet (set of Strings) The free types encountered while traversing the parse tree of a type definition. These
     *        are the names of the non-built-in non-foreign types defined within the current module upon which the type being examined depends.
     * @param declaredTypeNamesSet the set of all non-built-in type names defined within the current module.
     */
    private void findFreeTypes(ParseTreeNode parseTree, Set<String> typeVarNamesSet, Set<String> freeTypesSet, Set<String> declaredTypeNamesSet) {

        switch (parseTree.getType()) {

            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR :
            case CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR :
            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR :
            case CALTreeParserTokenTypes.TYPE_APPLICATION :
            {                                               
                //We are not adding the built-in types as dependee types. This includes both the built-in types that
                //have identifier names, as well as those introduced using special syntax, as in this case.

                for (final ParseTreeNode typeExprNode : parseTree) {
                         
                    findFreeTypes(typeExprNode, typeVarNamesSet, freeTypesSet, declaredTypeNamesSet);
                }
                break;
            }

            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {            
                QualifiedName typeName = parseTree.toQualifiedName();
                String unqualifiedTypeName = typeName.getUnqualifiedName();
                ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();

                if (typeName.getModuleName().equals(currentModuleName) &&
                    declaredTypeNamesSet.contains(unqualifiedTypeName)) {

                    freeTypesSet.add(unqualifiedTypeName);
                }
                break;
            }

            case CALTreeParserTokenTypes.VAR_ID :
            {
                final String typeVarName = parseTree.getText();
                if (!typeVarNamesSet.contains(parseTree.getText())) {
                    // Unbound type variable {typeVarName}.
                    compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Error.UnboundTypeVariable(typeVarName)));
                }
                break;
            }
            
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR :
            {
                final ParseTreeNode recordVarNode = parseTree.firstChild();
                recordVarNode.verifyType(CALTreeParserTokenTypes.RECORD_VAR);
                                
                final ParseTreeNode recordVarNameNode = recordVarNode.firstChild();
                if (recordVarNameNode != null) {
                    
                    //a record-polymorphic record
                    
                    // Record variables, such as {recordVarNameNode.getText()} cannot appear in data declarations.
                    compiler.logMessage(new CompilerMessage(recordVarNameNode, new MessageKind.Error.RecordVarsCannotAppearInDataDeclarations(recordVarNameNode.getText())));
                    return;                                                              
                } 
                
                final ParseTreeNode fieldTypeAssignmentListNode = recordVarNode.nextSibling();
                fieldTypeAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT_LIST);
                                              
                for (final ParseTreeNode fieldTypeAssignmentNode : fieldTypeAssignmentListNode) {
                         
                    fieldTypeAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT);                    
                    final ParseTreeNode fieldNameNode = fieldTypeAssignmentNode.firstChild();                                        
                    final ParseTreeNode typeExprNode = fieldNameNode.nextSibling();
                    
                    findFreeTypes(typeExprNode, typeVarNamesSet, freeTypesSet, declaredTypeNamesSet);                   
                }
                                
                break;                 
            }                       

            default :
            {            
                parseTree.unexpectedParseTreeNode();               
                break;
            }
        }
    }
    
    /**
     * Makes the dependency graph of the type constructors defined within the module.
     *
     * Creation date: (1/19/01 11:52:00 AM)
     * @param dataDeclarationNodes
     * @param declaredTypeNamesSet the set of all non-built-in type names defined within the current module.
     * @return VertexBuilderList
     */
    private VertexBuilderList<String> makeTypeConstructorDependencyGraph(final List<ParseTreeNode> dataDeclarationNodes, final Set<String> declaredTypeNamesSet) {

        final VertexBuilderList<String> vertexBuilderList = new VertexBuilderList<String>();
        
        for (final ParseTreeNode dataDeclarationNode : dataDeclarationNodes) {
                                   
            final String typeName = dataDeclarationNode.getChild(2).getText();

            final Set<String> typeVarNamesSet = new HashSet<String>();

            final ParseTreeNode typeConsParamListNode = dataDeclarationNode.getChild(3);
            typeConsParamListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONS_PARAM_LIST);

            for (final ParseTreeNode typeVarNameNode : typeConsParamListNode) {

                //have already checked for repeated vars
                typeVarNamesSet.add(typeVarNameNode.getText());
            }

            //Go through the type expression sub trees and find the free types. These are the types
            //upon which this type definition depends. 

            final ParseTreeNode dataConsDefnListNode = typeConsParamListNode.nextSibling();
            dataConsDefnListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);

            final Set<String> freeTypesSet = new HashSet<String>();

            for (final ParseTreeNode dataConsDefnNode : dataConsDefnListNode) {

                dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);

                final ParseTreeNode dataConsArgListNode = dataConsDefnNode.getChild(3);
                dataConsArgListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_ARG_LIST);

                for (final ParseTreeNode dataConsArgNode : dataConsArgListNode) {

                    dataConsArgNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAMED_ARG);
                    
                    // Get the type node.
                    final ParseTreeNode dataConsArgNameNode = dataConsArgNode.firstChild();
                    final ParseTreeNode maybePlingTypeExprNode = dataConsArgNameNode.nextSibling();
                    
                    final ParseTreeNode typeExprNode;
                    if (maybePlingTypeExprNode.getType() == CALTreeParserTokenTypes.STRICT_ARG) {
                        typeExprNode = maybePlingTypeExprNode.firstChild();
                    } else {
                        typeExprNode = maybePlingTypeExprNode;                        
                    }

                    findFreeTypes(typeExprNode, typeVarNamesSet, freeTypesSet, declaredTypeNamesSet);
                }
            }

            vertexBuilderList.add(new VertexBuilder<String>(typeName, freeTypesSet));
        }

        return vertexBuilderList;
    }
    
    /**
     * A helper function that makes a map from the type variable to its initial kind variable kind.
     * Creation date: (1/26/01 10:30:08 AM)
     * @return Map
     * @param dataConstructorEnv
     * @param dataConsDefnListNode 
     */
    private Map<TypeVar, KindExpr> makeTypeVarToKindVarMap(final Env dataConstructorEnv, final ParseTreeNode dataConsDefnListNode) {

        final Map<TypeVar, KindExpr> typeVarToKindVarMap = new HashMap<TypeVar, KindExpr>();

        final ParseTreeNode dataConsDefnNode = dataConsDefnListNode.firstChild();
        dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);

        final String dataConsName = dataConsDefnNode.getChild(2).getText();

        final FunctionalAgent dataConsEntity = dataConstructorEnv.retrieveEntity(dataConsName);
        if (dataConsEntity == null) {
            //we've added dataConsName to the environment, so this should never happen.
            throw new NullPointerException("Programming error");
        }
        final TypeExpr dataConsTypeExpr = dataConsEntity.getTypeExprExact();

        //if "data T a1 ... an = MakeT te1 te2 ... tek | ..." then the type of MakeT is of
        //the form "... -> T a1 ... an". In particular, the last type piece is "T a1 ... an".
        //The kind of T is its initial guess: k1->k2->,,,->kn->*. We create the map:
        //typeVarToKindExprMap = (a1,k1), (a2,k2), ... (an,kn).

        final TypeConsApp typeCons = (TypeConsApp) dataConsTypeExpr.getResultType();

        final QualifiedName typeName = typeCons.getName();
        
        KindExpr kindExpr = currentModuleTypeInfo.getTypeConstructor(typeName.getUnqualifiedName()).getKindExpr();
        
        for (int i = 0, nArgs = typeCons.getNArgs(); i < nArgs; ++i) {
      
            final TypeVar typeVar = (TypeVar) typeCons.getArg(i);
            typeVarToKindVarMap.put(typeVar, ((KindExpr.KindFunction) kindExpr).getDomain());
            kindExpr = ((KindExpr.KindFunction) kindExpr).getCodomain();
        }

        return typeVarToKindVarMap;
    }
    
    /**
     * Perform dependency analysis to divide up the types defined in the CAL program into
     * a topologically ordered set of strongly connected components. This is needed because
     * the data declarations in a program must be processed in a particular order, and in dependency
     * groups, whereas they can appear in any order in the source file, and are not grouped together
     * in any way.
     *
     * Creation date: (1/19/01 11:47:40 AM)
     * @param dataDeclarationNodes
     * @param declaredTypeNamesSet the set of all non-built-in type names defined within the current module.
     * @return Graph 
     */
    private Graph<String> performTypeConstructorDependencyAnalysis(final List<ParseTreeNode> dataDeclarationNodes, final Set<String> declaredTypeNamesSet) {

        final VertexBuilderList<String> vertexBuilderList = makeTypeConstructorDependencyGraph(dataDeclarationNodes, declaredTypeNamesSet);

        // should never fail. It is a redundant check since makeTypeConstructorDependencyGraph should throw an exception otherwise.
        if (!vertexBuilderList.makesValidGraph()) {
            throw new IllegalStateException("Internal coding error during dependency analysis."); 
        }

        final Graph<String> g = new Graph<String>(vertexBuilderList);
        vertexBuilderList.clear();

        return g.calculateStronglyConnectedComponents();
    }
    
    /**
     * A helper function that verifies that a type constructor name refers to a type constructor
     * that actually exists. As a side effect, the qualifiedTypeConsNode is modified to explicitly
     * include the module name, so that fully qualified type names can be assumed in further
     * analysis.
     * 
     * Note: this function assumes that checkNames has already been called.
     *
     * Creation date: (7/24/01 10:47:52 AM)     
     * @param qualifiedTypeConsNode
     * @return TypeConstructor the resolved type constructor, or null if a type constructor can't be resolved.
     */
    TypeConstructor resolveTypeConsName(final ParseTreeNode qualifiedTypeConsNode) {
        return resolveTypeConsName(qualifiedTypeConsNode, currentModuleTypeInfo, compiler);
    }
    
    /**
     * A helper function that verifies that a type constructor name refers to a type constructor
     * that actually exists. As a side effect, the qualifiedTypeConsNode is modified to explicitly
     * include the module name, so that fully qualified type names can be assumed in further
     * analysis.
     * 
     * Note: this function assumes that checkNames has already been called.
     *
     * @param qualifiedTypeConsNode the node containing the qualified type constructor name to be resolved.
     * @param currentModuleTypeInfo the type info for the current module.
     * @param compiler the CALCompiler instance for error reporting.
     * @return TypeConstructor the resolved type constructor, or null if a type constructor can't be resolved.
     */
    static TypeConstructor resolveTypeConsName(final ParseTreeNode qualifiedTypeConsNode, final ModuleTypeInfo currentModuleTypeInfo, final CALCompiler compiler) {
        return resolveTypeConsName(qualifiedTypeConsNode, currentModuleTypeInfo, compiler, false);
    }
    
    /**
     * A helper function that verifies that a type constructor name refers to a type constructor
     * that actually exists. As a side effect, the qualifiedTypeConsNode is modified to explicitly
     * include the module name, so that fully qualified type names can be assumed in further
     * analysis.
     * 
     * Note: this function assumes that checkNames has already been called.
     *
     * @param qualifiedTypeConsNode the node containing the qualified type constructor name to be resolved.
     * @param currentModuleTypeInfo the type info for the current module.
     * @param compiler the CALCompiler instance for error reporting.
     * @param suppressErrorMessageLogging whether to suppress the logging of error messages to the CALCompiler instance
     * @return TypeConstructor the resolved type constructor, or null if a type constructor can't be resolved.
     */
    static TypeConstructor resolveTypeConsName(final ParseTreeNode qualifiedTypeConsNode, final ModuleTypeInfo currentModuleTypeInfo, final CALCompiler compiler, boolean suppressErrorMessageLogging) {

        qualifiedTypeConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);

        final ParseTreeNode moduleNameNode = qualifiedTypeConsNode.firstChild();
        final String maybeModuleName = ModuleNameUtilities.resolveMaybeModuleNameInParseTree(moduleNameNode, currentModuleTypeInfo, compiler, suppressErrorMessageLogging);
        final ParseTreeNode typeNameNode = moduleNameNode.nextSibling();
        final String typeName = typeNameNode.getText();

        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();

        if (maybeModuleName.length() > 0) {
            
            //an explicitly qualified type name

            final ModuleName moduleName = ModuleName.make(maybeModuleName);
            final QualifiedName qualifiedTypeName = QualifiedName.make(moduleName, typeName);

            if (moduleName.equals(currentModuleName)) {

                //type belongs to the current module
                
                final TypeConstructor typeCons = currentModuleTypeInfo.getTypeConstructor(typeName);
                if (typeCons != null) {
                
                    checkResolvedTypeConsReference(compiler, typeNameNode, typeCons.getName(), currentModuleName, suppressErrorMessageLogging);
                    
                    //typeName is a non built-in type defined in the current module via a non-foreign data declaration
                    //or a built-in type name or a type-name defined with a foreign data declaration                  

                    return typeCons;
                }
                
                if (!suppressErrorMessageLogging) {
                    // Attempt to use undefined type {qualifiedTypeName}.
                    compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.AttemptToUseUndefinedType(qualifiedTypeName.getQualifiedName())));
                }
                return null;
            }

            //typeName belongs to a different module

            final ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
            if (importedModuleTypeInfo == null) {
                // The module {moduleName} has not been imported into {currentModuleName}.
                if (!suppressErrorMessageLogging) {
                    compiler.logMessage(new CompilerMessage(moduleNameNode, new MessageKind.Error.ModuleHasNotBeenImported(moduleName, currentModuleName)));
                }
                return null;
            }

            final TypeConstructor typeCons = importedModuleTypeInfo.getTypeConstructor(typeName);
            if (typeCons == null) {

                // The type {qualifiedTypeName} does not exist.
                if (!suppressErrorMessageLogging) {
                    compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.TypeDoesNotExist(qualifiedTypeName.getQualifiedName())));
                }
                return null;
                    
            } else if (!currentModuleTypeInfo.isEntityVisible(typeCons)) {
                
                if (!suppressErrorMessageLogging) {
                    // The type {qualifiedTypeName} is not visible in {currentModuleName}.
                    //"The type {0} is not visible in module {1}."
                    compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.TypeConstructorNotVisible(typeCons, currentModuleName)));
                }
                return null;               
            }

            checkResolvedTypeConsReference(compiler, typeNameNode, typeCons.getName(), currentModuleName, suppressErrorMessageLogging);
            
            //Success- the module is indeed imported and has a visible type name of the specified name.    
            return typeCons;
        }

        //An unqualified type name

        //At this point we need to decide if the type is
        //a. a type defined in the current module
        //b. a visible type defined in another module
        //We patch up the parse tree to supply the missing module name.


        final TypeConstructor typeCons = currentModuleTypeInfo.getTypeConstructor(typeName);
        if (typeCons != null) {
                  
            checkResolvedTypeConsReference(compiler, typeNameNode, typeCons.getName(), currentModuleName, suppressErrorMessageLogging);
            
             //typeName is a non built-in type defined in the current module via a non-foreign data declaration
             //or a built-in type name or a type-name defined with a foreign data declaration   

            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, currentModuleName);
            return typeCons;
        }
        
        
        //We now know that the type name can't be defined within the current module.
        //check if it is a "using typeConstructor" and then patch up the module name.
        
        final ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingTypeConstructor(typeName);
        if (usingModuleName != null) {
            final QualifiedName qualifiedTypeName = QualifiedName.make(usingModuleName, typeName);
            
            checkResolvedTypeConsReference(compiler, typeNameNode, qualifiedTypeName, currentModuleName, suppressErrorMessageLogging);
            
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, usingModuleName);
            //this call is guaranteed to return a type constructor due to earlier static checks on the using clause
            return currentModuleTypeInfo.getVisibleTypeConstructor(qualifiedTypeName);
        }
        
        if (!suppressErrorMessageLogging) {
            //We now know that the type name can't be defined within the current module and is not a "using typeConstructor".
            //Check if it is defined in another module.
            //This will be an error since the user must supply a module qualification, but we
            //can attempt to give a good error message.          
            
            final List<QualifiedName> candidateTypeConsNames = new ArrayList<QualifiedName>();
            final int nImportedModules = currentModuleTypeInfo.getNImportedModules();
            
            for (int i = 0; i < nImportedModules; ++i) {
                
                final TypeConstructor candidate = currentModuleTypeInfo.getNthImportedModule(i).getTypeConstructor(typeName);
                if (candidate != null && currentModuleTypeInfo.isEntityVisible(candidate)) {
                    candidateTypeConsNames.add(candidate.getName());
                }
            }
            
            final int numCandidates = candidateTypeConsNames.size();
            if(numCandidates == 0) {
                // Attempt to use undefined type {typeName}.
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.AttemptToUseUndefinedType(typeName)));
                
            } else if(numCandidates == 1) {
                
                final QualifiedName typeConsName = candidateTypeConsNames.get(0);
                
                // Attempt to use undefined type {typeName}. Was {typeCons.getName()} intended?
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.AttemptToUseUndefinedTypeSuggestion(typeName, typeConsName)));
                
            } else {
                // The reference to the type {typeName} is ambiguous. It could mean any of {candidateTypeConsNames.toString()}.
                compiler.logMessage(new CompilerMessage(typeNameNode, new MessageKind.Error.AmbiguousTypeReference(typeName, candidateTypeConsNames)));
            }
        }

        return null;
    }        
    
    /**
     * Performs late static checks on a type constructor reference that has already been resolved.
     * 
     * Currently, this performs 1) a deprecation check on a type constructor reference, logging a warning message if the type is deprecated, and
     * 2) a check that if the reference is to a foreign type, then the corresponding Java class should be resolvable and loadable, logging
     * an error message if the class cannot be loaded.
     * @param compiler the compiler instance.
     * @param nameNode the parse tree node representing the reference.
     * @param qualifiedName the qualified name of the reference.
     * @param currentModuleName the name of the current module.
     * @param suppressCompilerMessageLogging whether to suppress message logging.
     */
    static void checkResolvedTypeConsReference(final CALCompiler compiler, final ParseTreeNode nameNode, final QualifiedName qualifiedName, ModuleName currentModuleName, final boolean suppressCompilerMessageLogging) {
        if (!suppressCompilerMessageLogging) {
            
            // 1) deprecation check on the type constructor reference, logging a warning message if the type is deprecated.
            if (compiler.getDeprecationScanner().isTypeDeprecated(qualifiedName)) {
                compiler.logMessage(new CompilerMessage(nameNode, new MessageKind.Warning.DeprecatedType(qualifiedName)));
            }
            
            // 2) check that if the reference is to a foreign type, then the corresponding Java class should be resolvable and loadable.
            
            // only perform the check on references to entities outside the current module
            if (!qualifiedName.getModuleName().equals(currentModuleName)) {

                // Check if the type is a foreign type
                final ModuleTypeInfo moduleTypeInfo = compiler.getPackager().getModuleTypeInfo(qualifiedName.getModuleName());
                final TypeConstructor typeConstructor = moduleTypeInfo.getTypeConstructor(qualifiedName.getUnqualifiedName());
                final ForeignTypeInfo foreignTypeInfo = typeConstructor.getForeignTypeInfo();
                
                if (foreignTypeInfo != null) {

                    // Force resolution to occur.
                    try {
                        foreignTypeInfo.getForeignType(); // return value ignored

                    } catch (UnableToResolveForeignEntityException e) {
                        compiler.logMessage(new CompilerMessage(
                            nameNode,
                            new MessageKind.Error.AttemptToUseForeignTypeThatFailedToLoad(qualifiedName.toSourceText()),
                            e));
                    }
                }
            }
        }
    }
}
