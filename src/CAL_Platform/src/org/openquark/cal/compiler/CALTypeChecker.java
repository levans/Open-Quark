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
 * CALTypeChecker.java (originally TypeChecker.java)
 * Created: July 17, 2000
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;

import org.openquark.cal.compiler.CALSourceGenerator.CheckGraphSource;
import org.openquark.cal.compiler.CompilerMessage.AbortCompilation;
import org.openquark.cal.compiler.Expression.ErrorInfo;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.util.Graph;
import org.openquark.util.Pair;


/**
 * The main class for type checking modules in a CAL program. 
 * This class checks top-level function definitions as well as top-level function type declarations.
 * 
 * <p>
 * Various subtasks in type checking are delegated to other classes. For example: 
 * data declarations, for the creation of new types, are delegated to the DataDeclarationChecker
 * lambda, case and local function lifting is delegated to LambdaLifter
 * adding extra dictionary arguments to resolve overloading is done in the OverloadingResolver
 * ...
 * 
 * <p>
 * Creation date: (July 17, 2000)
 * @author Bo Ilic
 */
final class CALTypeChecker {

    /** Set to true to have debug info printed while running the type checker. */
    private static final boolean DEBUG_INFO = false;  

    /** The environment built-up during type checking for function (both top-level and local) definitions. */
    private Env functionEnv;
    
    /** 
     * (String -> TypeExpr) map of the (unique-transformed) names of all lower-case starting args within the definition of
     * a top-level (non-foreign, non-primitive) function to their type. 
     */
    private final Map<String, TypeExpr> functionBoundNamesToTypeMap;
    
    /**
     * (String -> NonGenericVars) map from unique-transformed local function name to the NonGenericVars 
     * for the scope of the local function's declaration.  It is entirely possible that all
     * of these type variables will have been instantiated by the time type checking is complete.
     * 
     * Note that not every unique-transformed bound name has an entry in this map; only unique names that
     * correspond to local function definitions.
     */
    private final Map<String, NonGenericVars> localFunctionBoundNamesToNonGenericVarsMap;
    
    /**
     * (String -> LocalFunctionIdentifier) map from unique-transformed local function name to the 
     * corresponding LocalFunctionIdentifier.  This map is populated quite late in the process of checking
     * a module. 
     */
    private final Map<String, LocalFunctionIdentifier> localFunctionBoundNamesToLocalFunctionIdentifiersMap;
    
    /**
     * (String -> Function) map from unique-transformed local function name to the Function for
     * that local function.
     * 
     * Note that not every unique-transformed bound name has an entry in this map; only unique names that
     * correspond to local function definitions.
     */
    private final Map<String, Function> localFunctionBoundNamesToFunctionEntityMap;
    
    /**
     * true if a warning should be given when a let variable is created with an overloaded polymorphic type.
     * An example is "let x = 1; in ..." then x has type Num a => a, and in particular, will be lambda lifted and
     * treated as a function by the runtime, even though it has the appearance of a local constant.
     */
    private static final boolean WARNING_FOR_OVERLOADED_POLYMORPHIC_LET_VARS = false;
    /**
     * (String -> ParseTreeNode) map from let variables (i.e. 0-arguments supplied in the CAL source, so not a local function)
     * to their defining ParseTreeNode. Provided that the type of the let-variable is not an overloaded polymorphic type, then
     * these variables will not be lambda lifted.
     */
    private final Map<String, ParseTreeNode> letVarNameToParseTreeMap;
    
    /**
     * (String Set) names of the local variables that the compiler knows to be evaluated to weak-head normal form.
     * These are unique-transformed so they will have a $ in them. 
     * 
     * <p>
     * These variables are:
     * <ol>
     *   <li> strict function bound argument names (i.e. corresponding to plinged function arguments).
     *   <li> variables introduced by a case-pattern where the variable is guaranteed to be evaluated because of the
     *        strictness annotations on the corresponding data constructor argument.    
     *   <li> let variables of the form 
     *        x = expression known by the compiler to be evaluated to weak head normal form.
     *        Some important cases of this are where the expression is:
     *        <ul>
     *          <li> a top level applications of Prelude.eager
     *          <li> literals (string, integers, double, lists, tuples, non-extension records)
     *          <li> aliases for top-level functions and data constructors
     *          <li> aliases for evaluated local variables
     *          <li> certain special data constructor field selections of the form (evaluated-expression).MyDataConstructor.myStrictField)
     *       </ul>
     * </ol>     
     */
    private final Set<String> evaluatedLocalVariables;

    /** 
     * The environment for data constructors. This is made up of the built-in data constructors, as
     * well as those introduced using data declarations. It does not include the data constructors
     * that are created using special notations (Char, Int, Double, List, Tuples and the trivial type).
     */
    private Env dataConstructorEnv;

    /** The ModuleTypeInfo of the module currently being type checked. */
    private ModuleTypeInfo currentModuleTypeInfo;

    /** Determines the types of all data constructors, both built-in and those introduced via data declarations. */
    private DataDeclarationChecker dataDeclarationChecker;
    
    /** Used for type checking type class declarations within a module. */
    private TypeClassChecker typeClassChecker;

    /** 
     * (String->ParseTreeNode). A map from a function name to a Node in the program parse tree where the function
     * is defined. This is needed since type checking does not proceed in textual order of the source but
     * rather, according to the order given by dependency analysis.
     * The domain of this map contain the names of all non built-in top-level functions defined within
     * the current module (and thus a qualified name is not required since the module name is 
     * implicitly given).
     */
    private final Map<String, ParseTreeNode> functionNameToDefinitionNodeMap;

    private final CALCompiler compiler;
    
    /** (String). The names of all foreign functions declared in the current module. */
    private Set<String> foreignFunctionNamesSet;
    
    /** 
     * (String Set). The names of all built-in primitive functions declared in the current module.
     */
    private Set<String> primitiveFunctionNamesSet;
    
    /** 
     * (String->TypeExpr). A map from a top-level function's name to its type expression as expressed in a type declaration.
     * The domain of this map contain the names of all (non primitive) functions defined within
     * the current module that have been given explicit type declarations.
     */
    private final Map<String, TypeExpr> functionNameToDeclaredTypeMap;
  
    /** 
     * (OverloadingInfo). Holds onto all the OverloadingInfo objects created while type checking a top level
     * component. Overload resolution can only occur after this is completed. For
     * example, if f is a top level function and g and h are defined at the same nesting
     * level within f, then this list will include OverloadingInfo for g and h.
     */
    private final List<OverloadingInfo> overloadingInfoList;
    
    /** 
     * (ParseTreeNode). Holds onto all the parse tree nodes that are calls to assert and
     * undefined. This list is used to updated the parse tree by inlining assert and
     * undefined calls and add the source position info to the error call node.
     */
    private final List<ParseTreeNode> errorCallList;

    /** the level in the CAL source of a definition. This is increased by a let definition.  */
    private int nestingLevel;
    
    /** 
     * holds onto the OverloadingInfo object corresponding to the function that is currently being type
     * checked. This is changed within the body of a local function to be the OverloadingInfo object
     * of the local function.
     */
    private OverloadingInfo currentOverloadingInfo;
    
    /**
     * (String Set) The names of all non built-in top-level functions defined within the current module.
     */
    private final Set<String> topLevelFunctionNameSet;
    
    private final TypeConstants typeConstants;
     
    /**
     * A class used to return info about an adjunct 
     * after it has been type checked.
     * @author RCypher
     */
    static final class AdjunctInfo {
       
       /** Name of the target function in the adjunct. */
        private final String targetName;
        
        /** The type of the target function. */
        private final TypeExpr targetType;
        
        /** True if there is a type declaration for the target function. */
        private final boolean targetExplicitlyTyped;
        
        public AdjunctInfo (final String targetName, final TypeExpr targetType, final boolean targetExplicitlyTyped) {
            this.targetName = targetName;
            this.targetType = targetType;
            this.targetExplicitlyTyped = targetExplicitlyTyped;
        }
        
        public String getTargetName() {
            return targetName;
        }
        public TypeExpr getTargetType () {
            return targetType;
        }
        public boolean isTargetExplicitlyTyped() {
            return targetExplicitlyTyped;
        }
    }
    
    /**
     * A simple container class to hold the results of data constructor pattern analysis returned
     *   by the various analyzeDataConstructorPatternXXXHelper() methods.
     * @author Edward Lam
     */
    private static final class DataConstructorPatternAnalysisResults {
        private final Env extendedEnv;
        private final NonGenericVars extendedNonGenericVars;

        DataConstructorPatternAnalysisResults(final Env extendedEnv, final NonGenericVars extendedNonGenericVars) {
            this.extendedEnv = extendedEnv;
            this.extendedNonGenericVars = extendedNonGenericVars;
        }

        
        public Env getExtendedEnv() {
            return this.extendedEnv;
        }

        public NonGenericVars getExtendedNonGenericVars() {
            return this.extendedNonGenericVars;
        }
    }
    
    /**
     * Useful TypeExpr constants for use during compilation.
     * These are used for typing literal constructs in the language such as Char and String literals.
     * Note: the literals are lazily initialized since the types themselves are not-built in, so we need
     * to have processed type declarations first or calling the methods below will fail in an exception.
     * @author Bo Ilic
     */
    final class TypeConstants {
        
        /** constant for the Prelude.Boolean type. */
        private TypeExpr BOOLEAN_TYPE;
        
        /** constant for the Prelude.Char type. */
        private TypeExpr CHAR_TYPE;
        
        /** constant for the Prelude.Int type. */
        private TypeExpr INT_TYPE;
        
        /** constant for the Prelude.Double type. */
        private TypeExpr DOUBLE_TYPE;
        
        /** constant for the Prelude.String type. */
        private TypeExpr STRING_TYPE;
        
        /** constant for the Prelude.Unit type, also known as (). */
        private TypeExpr UNIT_TYPE;   
        
        /** constant for the type Prelude.Boolean -> Prelude.Boolean -> Prelude.Boolean, i.e. the type of the && and || operators. */
        private TypeExpr AND_OR_TYPE;
        
        /** for the Prelude.List type */
        private TypeConstructor LIST_TYPE_CONS;        
        
        private TypeConstants() {}
        
        /**         
         * @return the type Prelude.Boolean.
         */             
        TypeExpr getBooleanType() {
            
            if (BOOLEAN_TYPE == null) {              
                BOOLEAN_TYPE = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Boolean));
                if (BOOLEAN_TYPE == null) {
                    throw new IllegalStateException("The " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + " type is not defined.");
                }
            }
            
            return BOOLEAN_TYPE;
        }
        
        /**         
         * @return the type Prelude.Char.
         */             
        TypeExpr getCharType() {
            
            if (CHAR_TYPE == null) {              
                CHAR_TYPE = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Char));
                if (CHAR_TYPE == null) {
                    throw new IllegalStateException("The " + CAL_Prelude.TypeConstructors.Char.getQualifiedName() + " type is not defined.");
                }
            }
            
            return CHAR_TYPE;
        }
        
        /**         
         * @return the type Prelude.Int.
         */            
        TypeExpr getIntType() {
            
            if (INT_TYPE == null) {              
                INT_TYPE = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Int));
                if (INT_TYPE == null) {
                    throw new IllegalStateException("The " + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + " type is not defined.");
                }
            }
            
            return INT_TYPE;
        }
        
        /**         
         * @return the type Prelude.Double.
         */          
        TypeExpr getDoubleType() {
            
            if (DOUBLE_TYPE == null) {              
                DOUBLE_TYPE = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Double));
                if (DOUBLE_TYPE == null) {
                    throw new IllegalStateException("The " + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + " type is not defined.");
                }
            }
            
            return DOUBLE_TYPE;
        }        
        
        /**         
         * @return the type Prelude.String.
         */        
        TypeExpr getStringType() {
            
            if (STRING_TYPE == null) {              
                STRING_TYPE = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.String));
                if (STRING_TYPE == null) {
                    throw new IllegalStateException("The " + CAL_Prelude.TypeConstructors.String + " type is not defined.");
                }
            }
            
            return STRING_TYPE;
        } 
        
        /**         
         * @return the type Prelude.Unit, also known as ().
         */
        TypeExpr getUnitType() {
            
            if (UNIT_TYPE == null) {              
                UNIT_TYPE = TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Unit));
                if (UNIT_TYPE == null) {
                    throw new IllegalStateException("The " + CAL_Prelude.TypeConstructors.Unit + " type is not defined.");
                }
            }
            
            return UNIT_TYPE;
        } 
        
        /**         
         * @return constant for the type Prelude.Boolean -> Prelude.Boolean -> Prelude.Boolean, i.e. the type of the && and || operators.
         */
        TypeExpr getAndOrType() {
            
            if (AND_OR_TYPE == null) { 
                TypeExpr booleanType = getBooleanType();
                AND_OR_TYPE = TypeExpr.makeFunType (booleanType, TypeExpr.makeFunType(booleanType, booleanType));              
            }
            
            return AND_OR_TYPE;
        }   
        
        TypeConstructor getListTypeCons() {
            if (LIST_TYPE_CONS == null) {
                LIST_TYPE_CONS = currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.List);
                if (LIST_TYPE_CONS == null) {
                    throw new IllegalStateException(CAL_Prelude.TypeConstructors.List.getQualifiedName() + " type not available.");
                }
            }
            
            return LIST_TYPE_CONS;
        }
        
        /**
         * Create a type expression for a list.
         *
         * @param elementTypeExpr the type of the element    
         * @return TypeExpr the type expression for the list with given element type    
         */    
        TypeExpr makeListType(final TypeExpr elementTypeExpr) {               
            return new TypeConsApp(getListTypeCons(), new TypeExpr [] {elementTypeExpr});        
        }
        
        /**
         * Returns the TypeExpr for list append operator (:). Namely, a -> [a] -> [a].
         * Creation date: (3/9/01 3:27:27 PM)
         * @return TypeExpr
         */
        TypeExpr makeConsType() {
    
            TypeExpr alpha = new TypeVar();
            TypeExpr listType = makeListType(alpha);
            return TypeExpr.makeFunType(alpha, TypeExpr.makeFunType(listType, listType));
        }        
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (8/31/00 11:47:59 AM)
     * @param compiler
     */
    CALTypeChecker(final CALCompiler compiler) {
        
        if (compiler == null) {
            throw new NullPointerException();
        }

        this.compiler = compiler;
        
        functionBoundNamesToTypeMap = new HashMap<String, TypeExpr>();
        localFunctionBoundNamesToNonGenericVarsMap = new HashMap<String, NonGenericVars>();
        localFunctionBoundNamesToLocalFunctionIdentifiersMap = new HashMap<String, LocalFunctionIdentifier>();
        localFunctionBoundNamesToFunctionEntityMap = new HashMap<String, Function>();
        evaluatedLocalVariables = new HashSet<String>();
        letVarNameToParseTreeMap = new HashMap<String, ParseTreeNode>();

        functionNameToDefinitionNodeMap = new HashMap<String, ParseTreeNode>();
        functionNameToDeclaredTypeMap = new HashMap<String, TypeExpr>();        
        overloadingInfoList = new ArrayList<OverloadingInfo>();
        errorCallList = new ArrayList<ParseTreeNode>();
        nestingLevel = 0;      
        topLevelFunctionNameSet = new HashSet<String>();
        typeConstants = new TypeConstants();
    } 
    
    /**     
     * DO NOT MAKE THIS PUBLIC.
     * 
     * @return Useful TypeExpr constants for use during compilation for typing literal constructs in the language
     * such as Char and String literals. Note: the literals are lazily initialized since the types themselves
     * are not-built in, so we need to have processed type declarations before these will not fail in exceptions.
     */
    TypeConstants getTypeConstants() {
        return typeConstants;
    }
    
    /**
     * There are 2 types of case expressions, those unpacking data constructors, and those unpacking record values.
     * This distinguishes between them to faciliate compilation analysis.
     * @param caseNode
     * @return boolean
     */
    static boolean isRecordCase(final ParseTreeNode caseNode) {
        
        caseNode.verifyType(CALTreeParserTokenTypes.LITERAL_case);
        
        final ParseTreeNode altListNode = caseNode.getChild(1);
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
        
        final ParseTreeNode altNode = altListNode.firstChild();
        altNode.verifyType(CALTreeParserTokenTypes.ALT);
        
        return altNode.firstChild().getType() == CALTreeParserTokenTypes.RECORD_PATTERN;              
    }
      
    /**
     * Determines the type of a record-case expression. This is a case-expression whose single pattern matches
     * for a value of a record type. 
     * @param functionEnv
     * @param nonGenericVars
     * @param recordCaseNode
     * @return TypeExpr
     */
    private TypeExpr analyzeRecordCase(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode recordCaseNode) {
        
        recordCaseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE);

        final ParseTreeNode conditionNode = recordCaseNode.firstChild();
        final TypeExpr conditionType = analyzeExpr(functionEnv, nonGenericVars, conditionNode);


        final ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
              
        if (!altListNode.hasExactlyOneChild()) {
            //record-case patterns have only 1 alternative. This should be caught earlier in static analysis.
            throw new IllegalArgumentException();
        }
        
        final ParseTreeNode altNode = altListNode.firstChild();       
        altNode.verifyType(CALTreeParserTokenTypes.ALT);

        final ParseTreeNode patternNode = altNode.firstChild();
        patternNode.verifyType(CALTreeParserTokenTypes.RECORD_PATTERN);
               
        //Add the pattern variables to the environment for when we type the expression to evaluate
        //as a consequence of pattern matching i.e. the expression to the right of the "->". 
        //For example, if the  pattern is {r | field1 = x, field2 = y, field3 = z},
        //The environment is extended by r, x, y and z, and the non-generic vars is extended by s, a, b, c.  
        //where s is then specialized to (u\field1, u\field2, u\field3) => {u}
        Env extendedEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;
        
        final ParseTreeNode baseRecordPatternNode = patternNode.firstChild();
        baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
                    
        final ParseTreeNode baseRecordPatternVarNode = baseRecordPatternNode.firstChild();
        final String baseRecordVarName;
        
        final TypeExpr baseRecordType;
                      
        if (baseRecordPatternVarNode != null) {
            
            //a record-polymorphic pattern
            
            baseRecordType = new TypeVar();
                                
            switch (baseRecordPatternVarNode.getType())
            {
                case CALTreeParserTokenTypes.VAR_ID:
                {                                
                    baseRecordVarName = baseRecordPatternVarNode.getText();
                    extendedEnv = extendedEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), baseRecordVarName),
                        Scope.PRIVATE, null, baseRecordType, FunctionalAgent.Form.PATTERNVAR, nestingLevel));
                    functionBoundNamesToTypeMap.put(baseRecordVarName, baseRecordType);
                    break;
                }
                                
                case CALTreeParserTokenTypes.UNDERSCORE:                                            
                    break;
                                
                default:
                {                            
                    baseRecordPatternVarNode.unexpectedParseTreeNode();
                    return null;
                }                            
            }
                        
            extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, (TypeVar)baseRecordType);            
            
        } else {
            
            //a non-record-polymorphic pattern
            
            baseRecordType = TypeExpr.EMPTY_RECORD;            
        }
                
                                                                                 
        final ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();
        fieldBindingVarAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
        
        final Map<FieldName, TypeExpr> extensionFieldsMap = new HashMap<FieldName, TypeExpr>();
                                                                                                                   
        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {
    
            fieldBindingVarAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT);
    
            final ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            final FieldName fieldName = getFieldName(fieldNameNode);            
            final TypeVar patternVarType = new TypeVar();
                          
            final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
          
            switch (patternVarNode.getType()) {
                case CALTreeParserTokenTypes.VAR_ID :
                {
                    final String patternVarName = patternVarNode.getText();
                    extendedEnv = extendedEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), patternVarName),
                        Scope.PRIVATE, null, patternVarType, FunctionalAgent.Form.PATTERNVAR, nestingLevel));   
                    functionBoundNamesToTypeMap.put(patternVarName, patternVarType);
                    break;
                }
    
                case CALTreeParserTokenTypes.UNDERSCORE :
                    break;
    
                default :
                {
                    patternVarNode.unexpectedParseTreeNode();                    
                    return null;
                }
            }
            
            extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, patternVarType);
            
            extensionFieldsMap.put(fieldName, patternVarType);
        }
        
        //this is the type expression determined by the record pattern. For example, if the pattern is:
        //{r | field1 = x, field2 = y, field3 = z}, this is just: (r\field1, r\field2, r\field3) => {r | field1 :: a, field2 :: b, field3 :: c}               
        final TypeExpr recordPatternTypeExpr;
        try {
            recordPatternTypeExpr = RecordType.recordExtension(baseRecordType, extensionFieldsMap);
        } catch (TypeException te) {
            //this should never fail since static analysis should have caught failure cases earlier
            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Fatal.UnexpectedUnificationFailure()));
            return null;
        }    
               
        try {            
            TypeExpr.unifyType(conditionType, recordPatternTypeExpr, currentModuleTypeInfo);
        } catch (TypeException te) { 
            //the case pattern and the case condition must unify to the same type               
            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.CasePatternAndCaseConditionMustHaveSameType(), 
                te));                
        }

        //now type the expression after the ->, which determines the type returned by the case. Use the extended environments.
               
        final ParseTreeNode exprNode = patternNode.nextSibling();
        final TypeExpr caseTypeExpr = analyzeExpr(extendedEnv, extendedNonGenericVars, exprNode);
            
        return caseTypeExpr;
    } 
    
    /**
     * Determines the type of a record constructor. This can either be a record literal, or a record modification (i.e. 
     * includes a list of field extensions and field value updates).
     * 
     * e.g.
     * {name = "Anton", age = 2.5}
     * {r | name := "Anton", #1 = True, #2 = "abc", age := 3.0}
     * 
     * @param functionEnv
     * @param nonGenericVars
     * @param recordConstructorNode
     * @return TypeExpr
     */
    private TypeExpr analyzeRecordConstructor(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode recordConstructorNode) {
        
        recordConstructorNode.verifyType(CALTreeParserTokenTypes.RECORD_CONSTRUCTOR);
        
        final ParseTreeNode baseRecordNode = recordConstructorNode.firstChild();
        baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);           
        
        final ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();        
        final TypeExpr baseRecordType;
        if (baseRecordExprNode != null) {
            baseRecordType = analyzeExpr(functionEnv, nonGenericVars, baseRecordExprNode);
        } else {
            baseRecordType = TypeExpr.EMPTY_RECORD;
        }
        
        //baseRecordType, but updated for the fields whose types are updated via the := operator.
        TypeExpr updatedBaseRecordType = baseRecordType;
        
        final ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
        fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);
        
        //FieldName -> TypeExpr
        final Map<FieldName, TypeExpr> extensionFieldsMap = new HashMap<FieldName, TypeExpr>(); 
        
        for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
            
            fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION,
                CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
            
            final ParseTreeNode fieldNameNode = fieldModificationNode.firstChild();
            
            final FieldName fieldName = getFieldName(fieldNameNode);
            
            final ParseTreeNode fieldValueExprNode = fieldNameNode.nextSibling();
            
            final TypeExpr fieldTypeExpr = analyzeExpr(functionEnv, nonGenericVars, fieldValueExprNode);
            
            switch (fieldModificationNode.getType()) {
                case CALTreeParserTokenTypes.FIELD_EXTENSION:
                {
                    extensionFieldsMap.put(fieldName, fieldTypeExpr);
                    break;
                }
                case CALTreeParserTokenTypes.FIELD_VALUE_UPDATE:
                {   
                    //updatedBaseRecordType must unify with r\fieldName => {r | fieldName :: a}
                    //i.e. it must be or specialize to a record type having a field named 'fieldName'.
                    
                    //the type of fieldName will then be fieldTypeExpr i.e. the type of the updated field comes from
                    //the updating expression, and does not need to be compatible with the type of the value being
                    //replaced.
                    
                    final TypeExpr lacksUpdateFieldType = new TypeVar();    
                    //this is initially the type  {r | fieldName :: a}.
                    final TypeExpr hasUpdateFieldType;
                    {
                        final Map<FieldName, TypeExpr> singleFieldMap = new HashMap<FieldName, TypeExpr>();
                        singleFieldMap.put(fieldName, new TypeVar());
                        try {                        
                            hasUpdateFieldType = RecordType.recordExtension(lacksUpdateFieldType, singleFieldMap);
                        } catch (TypeException te) {
                            //this should never fail
                            //todoBI remove this error message since it is not a true user error.
                            compiler.logMessage(new CompilerMessage(fieldModificationNode, new MessageKind.Fatal.UnexpectedUnificationFailure()));
                            return null;
                        }                                               
                    }
                                        
                    try {   
                        //the only way this can fail is if updatedBaseRecordType cannot unify with a free record type that has a field
                        //named 'fieldName'.
                        TypeExpr.unifyType(updatedBaseRecordType, hasUpdateFieldType, currentModuleTypeInfo);
                    } catch (TypeException te) {
                        //"Type error. Invalid field value update for field {0}." 
                        compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Error.InvalidRecordFieldValueUpdate(fieldName), te));
                    }
                    
                    try {
                        final Map<FieldName, TypeExpr> singleFieldMap = new HashMap<FieldName, TypeExpr>();
                        singleFieldMap.put(fieldName, fieldTypeExpr);
                        updatedBaseRecordType = RecordType.recordExtension(lacksUpdateFieldType, singleFieldMap);
                    } catch (TypeException te) {
                        //this should never fail
                        //todoBI remove this error message since it is not a true user error.
                        compiler.logMessage(new CompilerMessage(fieldModificationNode, new MessageKind.Fatal.UnexpectedUnificationFailure()));
                        return null;                        
                    }
                    
                    break;                            
                }
                default:
                {
                    fieldModificationNode.unexpectedParseTreeNode();
                    break;
                }
            }
            
        }
        
        final TypeExpr recordType;
        try {                
            recordType = RecordType.recordExtension(updatedBaseRecordType, extensionFieldsMap);
        } catch (TypeException te) {
            // Type error. Invalid record extension.
            compiler.logMessage(new CompilerMessage(baseRecordNode, new MessageKind.Error.InvalidRecordExtension(), te));
            return null;
        }
        
        return recordType;                 
    }    
    
    /**
     * Determines the type of a case expression (that switches on data constructors).
     * In particular, case expressions on record values and tuples are not handled here.
     * Also note that data constructor field selection syntax is not handled here e.g. expr.Just.value.
     *
     * Creation date: (9/13/00 10:44:54 AM)
     * @return TypeExpr
     * @param functionEnv
     * @param nonGenericVars
     * @param caseNode
     */
    private TypeExpr analyzeDataConstructorCase(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode caseNode) {
        
        caseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE);

        final ParseTreeNode conditionNode = caseNode.firstChild();
        final TypeExpr conditionType = analyzeExpr(functionEnv, nonGenericVars, conditionNode);

        final ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);

        final TypeExpr unifiedCaseTypeExpr = new TypeVar();

        for (final ParseTreeNode altNode : altListNode) {

            altNode.verifyType(CALTreeParserTokenTypes.ALT);

            final ParseTreeNode patternNode = altNode.firstChild();
            
            final DataConstructorPatternAnalysisResults patternAnalysisResults;
            
            switch (patternNode.getType()) {

                case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR :
                {
                    final ParseTreeNode dcNameListNode = patternNode.firstChild();
                    dcNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
                    
                    final ParseTreeNode dcArgBindingsNode = dcNameListNode.nextSibling();
                                                            
                    switch (dcArgBindingsNode.getType()) {
                        case CALTreeParserTokenTypes.PATTERN_VAR_LIST:
                        {
                            //positional based extraction, possibly with multiple data constructors
                            //e.g. (MyDataCons1 | MyDataCons2) field1 field2 field3 -> expr
                            patternAnalysisResults = analyzeDataConstructorPatternWithVarListHelper(
                                    functionEnv, nonGenericVars, conditionType, dcNameListNode, patternNode);
                            break;
                        }
                        case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST:
                        {                                      
                            //field-name based extraction, possibly with multiple data constructors
                            //e.g. (MyDataCons1 | MyDataCons2) {field1, field2, field3} -> expr
                            dcArgBindingsNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
                            patternAnalysisResults = analyzeDataConstructorPatternWithFieldBindingsHelper(
                                    functionEnv, nonGenericVars, conditionType, dcNameListNode, patternNode);
                            break;
                        }
                        default: 
                        {
                            dcArgBindingsNode.unexpectedParseTreeNode();
                            return null;
                        }
                    }
                    
                    break;
                }
                
                case CALTreeParserTokenTypes.INT_PATTERN :
                {
                    //matches to Int or (Int | Int | Int | ...)
                    final TypeExpr consTypeExpr = typeConstants.getIntType();
                    
                    patternAnalysisResults = analyzeDataConstructorNonPatternConstructorHelper(
                            functionEnv, nonGenericVars, consTypeExpr, conditionType, null, patternNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.CHAR_PATTERN :
                {
                    //matches to Char or (Char | Char | Char | ...)
                    final TypeExpr consTypeExpr = typeConstants.getCharType();
                    
                    patternAnalysisResults = analyzeDataConstructorNonPatternConstructorHelper(
                            functionEnv, nonGenericVars, consTypeExpr, conditionType, null, patternNode);
                    break;
                }
                                               
                case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
                {
                    //matches to the null list data constructor []
                    final TypeExpr consTypeExpr = typeConstants.makeListType(new TypeVar());

                    patternAnalysisResults = analyzeDataConstructorNonPatternConstructorHelper(
                            functionEnv, nonGenericVars, consTypeExpr, conditionType, null, patternNode);
                    break;
                }

                case CALTreeParserTokenTypes.COLON :
                {
                    //matches to a : as
                    final TypeExpr consTypeExpr = typeConstants.makeConsType();

                    patternAnalysisResults = analyzeDataConstructorNonPatternConstructorHelper(
                            functionEnv, nonGenericVars, consTypeExpr, conditionType, patternNode, patternNode);
                    break;
                }
                    
                case CALTreeParserTokenTypes.UNDERSCORE:
                {
                    //matches to any constructor
                    final TypeExpr consTypeExpr = new TypeVar();
                    
                    patternAnalysisResults = analyzeDataConstructorNonPatternConstructorHelper(
                            functionEnv, nonGenericVars, consTypeExpr, conditionType, null, patternNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.VIRTUAL_UNIT_DATA_CONSTRUCTOR:
                {
                    //matches to the Unit data constructor ()
                    final TypeExpr consTypeExpr = typeConstants.getUnitType();
                    
                    patternAnalysisResults = analyzeDataConstructorNonPatternConstructorHelper(
                            functionEnv, nonGenericVars, consTypeExpr, conditionType, null, patternNode);
                    break;
                }

                case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
                default :
                {
                    patternNode.unexpectedParseTreeNode();                   
                    return null;
                }
            }

            final Env extendedEnv = patternAnalysisResults.getExtendedEnv();
            final NonGenericVars extendedNonGenericVars = patternAnalysisResults.getExtendedNonGenericVars();

            // Finally, type the expression to evaluate as a consequence of pattern matching.
            // The unification ensures that the collection of all these expressions will have
            // a compatible type specialization.
            // c1 ps1 -> e1
            // c2 ps2 -> e2
            // c3 ps3 -> e3
            // Then the type of the case is Unify (Unify (e1, e2), e3).

            final ParseTreeNode exprNode = patternNode.nextSibling();
            final TypeExpr caseTypeExpr = analyzeExpr(extendedEnv, extendedNonGenericVars, exprNode);
            
            try {            
                TypeExpr.unifyType(unifiedCaseTypeExpr, caseTypeExpr, currentModuleTypeInfo);
            } catch (TypeException te) {
                //the types of all the case branches must be compatible
                compiler.logMessage(new CompilerMessage(exprNode, new MessageKind.Error.TypesOfAllCaseBranchesMustBeCompatible(), te));
            }
        }

        return unifiedCaseTypeExpr.prune();
    }
    
    /**
     * Get the extended env and non generic vars resulting from analysis of the given case alt pattern.
     *   The alt pattern should not be for a pattern constructor or group pattern constructor.
     * @param functionEnv
     * @param nonGenericVars
     * @param consTypeExpr the most general type of the data constructor
     * @param conditionType the type of the expression on which the case is predicated.
     * @param patternVarListNode if non-null, the parent node of the pattern vars (in a generalized var list) for the case alt.
     * @param patternNode the node on which to report errors for a data constructor.
     *   This should have pattern var nodes as its children.
     * @return the extended env and non-generic vars resulting from analysis of the pattern.
     */
    private DataConstructorPatternAnalysisResults analyzeDataConstructorNonPatternConstructorHelper (
            final Env functionEnv, final NonGenericVars nonGenericVars, final TypeExpr consTypeExpr, final TypeExpr conditionType, 
            final ParseTreeNode patternVarListNode, final ParseTreeNode patternNode) {
        
        // Add the pattern variables to the environment for when we type the expression to evaluate
        // as a consequence of pattern matching. Also, we must unify the type of the application
        // (constructor p1 p2 ... pn) with the type of the condition expression. For example, if
        // x : xs then we can type x :: a and xs :: b, then determine further restrictions
        // via unification with the type of the condition expression.

        Env extendedEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;

        TypeExpr typeOfAppl = consTypeExpr;  // start with the type of the case pattern
        
        if (patternVarListNode != null) {
            
            for (final ParseTreeNode patternVarNode : patternVarListNode) {
                
                // The type of the current pattern var.
                TypeVar typeOfNextArg = new TypeVar();
                
                switch (patternVarNode.getType()) {
                    
                    case CALTreeParserTokenTypes.VAR_ID:
                    {                    
                        final String varName = patternVarNode.getText();
                        extendedEnv = extendedEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), varName), Scope.PRIVATE, null, typeOfNextArg, FunctionalAgent.Form.PATTERNVAR, nestingLevel));
                        extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeOfNextArg);
                        functionBoundNamesToTypeMap.put(varName, typeOfNextArg);
                        break;
                    }
                    
                    case CALTreeParserTokenTypes.UNDERSCORE:
                    {
                        //todoBI is it necessary to add the typeVar to the non-generics for wildcards?
                        extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeOfNextArg);                        
                        break;
                    }
                    
                    default:
                    {
                        patternVarNode.unexpectedParseTreeNode();                      
                        break;
                    }
                }
                
                // the type of (constructor p1 p2 ... pi)
                TypeExpr typeOfArgsSoFar = typeOfAppl;
                typeOfAppl = new TypeVar();
                
                try {
                    // typeOfNextArg is now an unspecialized typeVar in the extendedNonGenericVars (and possibly the env).
                    // Perform unification to specialize it and typeOfAppl to the correct type.
                    TypeExpr.unifyType(typeOfArgsSoFar, TypeExpr.makeFunType(typeOfNextArg, typeOfAppl), currentModuleTypeInfo);
                    
                } catch (TypeException te) {  
                    //this should never happen since we should have earlier checked that the number of args
                    //supplied to the data constructor is correct.            
                    compiler.logMessage(new CompilerMessage(patternNode, 
                            new MessageKind.Error.DataConstructorArgumentsDoNotMatchDataConstructor(), te));
                }
            }
        }
        
        try {            
            TypeExpr.unifyType(conditionType, typeOfAppl, currentModuleTypeInfo);
        } catch (TypeException te) { 
            //the case pattern and the case condition must have the same type               
            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.CasePatternAndCaseConditionMustHaveSameType(), te));
        }

        return new DataConstructorPatternAnalysisResults(extendedEnv, extendedNonGenericVars);
    }
    
    /**
     * Helper for positional based extraction, possibly with multiple data constructors
     * e.g. (MyDataCons1 | MyDataCons2) field1 field2 field3 -> expr
     * 
     * Get the extended env and non generic vars resulting from analysis of the 
     *   given data constructor pattern group alt with var list.
     * @param functionEnv
     * @param nonGenericVars
     * @param conditionType the type of the expression on which the case is predicated.
     * @param dcNameListNode the parse tree node for the data constructor name(s) in the pattern.  
     * @param patternNode the node on which to report errors for a data constructor.
     * @return the extended env and non-generic vars resulting from analysis of the pattern, which should
     *   not be a group pattern.
     */
    private DataConstructorPatternAnalysisResults analyzeDataConstructorPatternWithVarListHelper(
            final Env functionEnv, final NonGenericVars nonGenericVars, final TypeExpr conditionType, final ParseTreeNode dcNameListNode, 
            final ParseTreeNode patternNode) {
                
        dcNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
        final ParseTreeNode dcArgBindingsNode = dcNameListNode.nextSibling();
        dcArgBindingsNode.verifyType(CALTreeParserTokenTypes.PATTERN_VAR_LIST);
        
        // (Set of DataConstructor objects). Data constructors referenced in this pattern.
        final Set<DataConstructor> dataConstructors = getPatternDataConstructors(dcNameListNode);        
        
        Env extendedEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;

        // (List of TypeVar) the type var at index i will be the type of the ith arg, if not an underscore (in the cal code).
        // If the ith pattern var is an underscore, the ith item will be null.
        final List<TypeVar> argTypesList = new ArrayList<TypeVar>();

        // (TypeVar->String) map from type var for an arg to the name of the arg.
        final Map<TypeVar, String> argTypeToNameMap = new HashMap<TypeVar, String>();
        
        int fieldIndex = 0;
        
        for (final ParseTreeNode patternVarNode : dcArgBindingsNode) {
            
            switch (patternVarNode.getType()) {
                
                case CALTreeParserTokenTypes.VAR_ID:
                {                    
                    // The type of the nth pattern var.
                    TypeVar typeOfNextArg = new TypeVar();
                    argTypesList.add(typeOfNextArg);
                    
                    final String varName = patternVarNode.getText();
                    argTypeToNameMap.put(typeOfNextArg, varName);
                    
                    //if the fields at index fieldIndex are strict for all data constructors then add the pattern variable to
                   //the set of evaluatedLocalVariables.                    
                    if (isStrictForAllDataConstructors(fieldIndex, dataConstructors)) {
                        evaluatedLocalVariables.add(varName);
                    }
                    
                    extendedEnv = extendedEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), varName), Scope.PRIVATE, null, typeOfNextArg, FunctionalAgent.Form.PATTERNVAR, nestingLevel));
                    extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeOfNextArg);
                    functionBoundNamesToTypeMap.put(varName, typeOfNextArg);
                    break;
                }
                
                case CALTreeParserTokenTypes.UNDERSCORE:
                {
                    // Check whether the underscore was converted from an unused var_id.
                    final String unusedVarName = patternVarNode.getUnusedVarNameForWildcardPatternVar();

                    if (unusedVarName != null) {
                        // Add to the list and map, but not to the extended env and non-generic vars.
                        TypeVar typeOfNextArg = new TypeVar();
                        argTypesList.add(typeOfNextArg);
                        argTypeToNameMap.put(typeOfNextArg, unusedVarName);
                        
                    } else {
                        argTypesList.add(null);
                    }
                    
                    break;
                }
                
                default:
                {
                    patternVarNode.unexpectedParseTreeNode();                      
                    break;
                }
            }
            
            ++fieldIndex;
        }

        final int nPatternArgs = dcArgBindingsNode.getNumberOfChildren();
        
        // Iterate over the data constructors named in the pattern.      
        for (final DataConstructor dataConsEntity : dataConstructors) {
                                              
            // Get the entity's type.
            // For example, if consName = "Just" then the type is "a -> Maybe a" and this is a constructor taking 1 argument.
            final TypeExpr consTypeExpr = dataConsEntity.getTypeExpr();

            // Check that the number of variables expected by the data constructor corresponds to the number actually supplied in the pattern.
            final int nConsArgs = consTypeExpr.getArity();
            if (nPatternArgs != nConsArgs) {
                //this is logged as a compiler message earlier during static analysis.
                throw new IllegalStateException("unexpected number of pattern arguments");               
            }

            // start with the type of the case pattern
            TypeExpr typeOfAppl = consTypeExpr;  

            // Iterate over the type vars in the list of arg types.
            for (TypeVar typeOfNextArg : argTypesList) {                
                
                // Check for an underscore.
                if (typeOfNextArg == null) {
                    typeOfNextArg = new TypeVar();
                    
                    //todoBI is it necessary to add the typeVar to the non-generics for wildcards?
                    extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeOfNextArg);
                }

                // the type of (constructor p1 p2 ... pi)
                TypeExpr typeOfArgsSoFar = typeOfAppl;
                typeOfAppl = new TypeVar();

                try {
                    // typeOfNextArg is now a typeVar in the extendedNonGenericVars (and possibly the env).
                    // Perform unification to (possibly further) specialize it and typeOfAppl to the correct type.
                    TypeExpr.unifyType(typeOfArgsSoFar, TypeExpr.makeFunType(typeOfNextArg, typeOfAppl), currentModuleTypeInfo);
                
                } catch (TypeException te) {
                    // We must be in a pattern group where the type of an argument in one dc 
                    // doesn't unify with the corresponding arg in another dc.
                    
                    // This should only happen for args with names, since args with underscores unify with a fresh type var,
                    //   which should always succeed.
                    final String argName = argTypeToNameMap.get(typeOfNextArg);
                    final String displayName = FreeVariableFinder.getDisplayName(argName);

                    compiler.logMessage(new CompilerMessage(patternNode, 
                                                            new MessageKind.Error.DataConstructorPatternGroupArgumentNotTypeable(displayName), te));
                }
            }
            
            try {            
                TypeExpr.unifyType(conditionType, typeOfAppl, currentModuleTypeInfo);
            } catch (TypeException te) { 
                //the case pattern and the case condition must have the same type               
                compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.CasePatternAndCaseConditionMustHaveSameType(), te));
            }
        }
        
        return new DataConstructorPatternAnalysisResults(extendedEnv, extendedNonGenericVars);
    }
    
    /**
     * Collect the data constructors declared in a pattern (either using positional or field-name based extraction)
     * into a set. For example, for either of: 
     * 
     * (MyDataCons1 | MyDataCons2) {field1, field2 field3} -> expr
     * (MyDataCons1 | MyDataCons2) field1 field2 field3 -> expr
     * The set would be {MyDataCons1, MyDataCons2}.
     * 
     * @param dcNameListNode
     * @return Set of DataConstructor objects. Ordered by declaration order in the pattern.
     */
    private Set<DataConstructor> getPatternDataConstructors(final ParseTreeNode dcNameListNode) {
        
        dcNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
        
        // (Set of DataConstructor objects). Data constructors referenced in this pattern.
        final Set<DataConstructor> dataConstructors = new LinkedHashSet<DataConstructor>();
        for (final ParseTreeNode dcNameNode : dcNameListNode) {  
            
            dcNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            
            // Get the dc entity.
            final DataConstructor dataConstructor = retrieveQualifiedDataConstructor(dcNameNode);
            if (dataConstructor == null) {
                //a message is logged in retrieveQualifiedDataConstructor,
                //continue to the next data constructor to try to catch additional errors. (since otherwise
                //a null pointer exception will shortly occur
                continue;
            } 
            
            dataConstructors.add(dataConstructor);
        }  
        
        return dataConstructors;
    }
    
    /**  
     * Returns true if 'fieldName' is a strict data constructor field for all the data constructors in the 'dataConstructors' set.
     *    
     * @param fieldName each data constructor is assumed to have fieldName as a field.
     * @param dataConstructors Set of DataConstructor objects     
     * @return true if fieldName is a strict data constructor field for all the data constructors in the dataConstructors set.
     */
    private boolean isStrictForAllDataConstructors(final FieldName fieldName, final Set<DataConstructor> dataConstructors) {
        for (final DataConstructor dataConstructor : dataConstructors) {
          
            int index = dataConstructor.getFieldIndex(fieldName);
            if (index == -1) {
                throw new IllegalArgumentException();                               
            }

            if (!dataConstructor.isArgStrict(index)) {
                return false;
            }
        }

        return true;
    }
    
    /**  
     * Returns true if 'fieldIndex' is the index of a strict data constructor field for all the data constructors in the 'dataConstructors' set.
     *    
     * @param fieldIndex each data constructor is assumed to have a field at the given fieldIndex.
     * @param dataConstructors Set of DataConstructor objects
     * @return true if fieldName is a strict data constructor field for all the data constructors in the dataConstructors set.
     */
    private static boolean isStrictForAllDataConstructors(final int fieldIndex, final Set<DataConstructor> dataConstructors) {
        for (final DataConstructor dataConstructor : dataConstructors) {
            
            if (!dataConstructor.isArgStrict(fieldIndex)) {
                return false;
            }
        }
        
        return true;
    }    

    /**
     * Helper for field-name based extraction, possibly with multiple data constructors
     * e.g. (MyDataCons1 | MyDataCons2) {field1, field2, field3} -> expr
     * 
     * et the extended env and non generic vars resulting from analysis of the 
     *   given data constructor pattern group alt with var list.
     * @param functionEnv
     * @param nonGenericVars
     * @param conditionType the type of the expression on which the case is predicated.
     * @param dcNameListNode (List of ParseTreeNode) the parse tree nodes for the data constructor name(s) in the pattern.     
     * @param patternNode the node on which to report errors for a data constructor.
     * @return the extended env and non-generic vars resulting from analysis of the pattern, which should
     *   not be a group pattern.
     */
    private DataConstructorPatternAnalysisResults analyzeDataConstructorPatternWithFieldBindingsHelper(
            final Env functionEnv, final NonGenericVars nonGenericVars, final TypeExpr conditionType, final ParseTreeNode dcNameListNode, 
            final ParseTreeNode patternNode) {
        
        dcNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
        final ParseTreeNode dcArgBindingsNode = dcNameListNode.nextSibling();        
        dcArgBindingsNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
                
        // (Set of DataConstructor objects). Data constructors referenced in this pattern.
        final Set<DataConstructor> dataConstructors = getPatternDataConstructors(dcNameListNode);
                   
        Env extendedEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;               

        // (FieldName->TypeVar) map from field name to the type var which will hold the type for that field.
        final Map<FieldName, TypeVar> fieldNameStringToArgTypeMap = new HashMap<FieldName, TypeVar>();              
        
        // Populate the map and extend the env and the non-generic vars.
        for (final ParseTreeNode fieldBindingNode : dcArgBindingsNode) {
            
            fieldBindingNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT);
            
            final ParseTreeNode fieldNameNode = fieldBindingNode.firstChild();
            final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
            
            // Add to field names.
            final FieldName fieldName = FieldName.make(fieldNameNode.getText());  
            
            //note that by earlier static analysis we know that fieldName *is* a field for all the data constructors                        
                          
            // The type of the arg for this field.
            TypeVar typeOfFieldArg = new TypeVar();
            fieldNameStringToArgTypeMap.put(fieldName, typeOfFieldArg);
            
            // Add to the extended env and non-generic vars if not unused.
            if (patternVarNode.getType() != CALTreeParserTokenTypes.UNDERSCORE) {
                
                patternVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String varName = patternVarNode.getText();
                
                //if fieldName is strict for each data constructor in the pattern, then add the pattern variable to
                //the set of evaluatedLocalVariables.
                if (isStrictForAllDataConstructors(fieldName, dataConstructors)) {
                    evaluatedLocalVariables.add(varName);
                }        
                               
                extendedEnv = extendedEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), varName), Scope.PRIVATE, null, typeOfFieldArg, FunctionalAgent.Form.PATTERNVAR, nestingLevel));
                extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeOfFieldArg);
                functionBoundNamesToTypeMap.put(varName, typeOfFieldArg);
            }
        }

        // Iterate over the data constructors named in the list.      
        for (final DataConstructor dataConstructor : dataConstructors) {
                                    
            // Get the entity's type.
            // For example, if consName = "Just" then the type is "a -> Maybe a" and this is a constructor taking 1 argument.
            final TypeExpr consTypeExpr = dataConstructor.getTypeExpr();

            // start with the type of the case pattern
            TypeExpr typeOfAppl = consTypeExpr;  

            // Iterate over the fields in the data constructor.
            for (int i = 0, arity = dataConstructor.getArity(); i < arity; i++) {
                final FieldName fieldName = dataConstructor.getNthFieldName(i);
                
                // get the binding for the field name if any.               
                TypeVar typeOfNextArg = fieldNameStringToArgTypeMap.get(fieldName);

                if (typeOfNextArg == null) {
                    // No binding for this field.
                    typeOfNextArg = new TypeVar();
                    
                    //todoBI is it necessary to add the typeVar to the non-generics for wildcards?
                    extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeOfNextArg);                        
                }

                // the type of (constructor p1 p2 ... pi)
                TypeExpr typeOfArgsSoFar = typeOfAppl;
                typeOfAppl = new TypeVar();

                try {
                    // typeOfNextArg is now a typeVar in the extendedNonGenericVars (and possibly the env).
                    // Perform unification to (possibly further) specialize it and typeOfAppl to the correct type.
                    TypeExpr.unifyType(typeOfArgsSoFar, TypeExpr.makeFunType(typeOfNextArg, typeOfAppl), currentModuleTypeInfo);
                
                } catch (TypeException te) {
                    // We must be in a pattern group where the type of an argument in one dc 
                    // doesn't unify with the corresponding arg in another dc.
                    compiler.logMessage(new CompilerMessage(patternNode, 
                        new MessageKind.Error.DataConstructorPatternGroupArgumentNotTypeable(fieldName.getCalSourceForm()), te));
                }
            }
            
            try {            
                TypeExpr.unifyType(conditionType, typeOfAppl, currentModuleTypeInfo);
            } catch (TypeException te) { 
                //the case pattern and the case condition must have the same type               
                compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.CasePatternAndCaseConditionMustHaveSameType(), te));
            }
        }
        
        return new DataConstructorPatternAnalysisResults(extendedEnv, extendedNonGenericVars);
    }

    /**
     * Determines the type of a case expression whose pattern is a tuple-style record (e.g. (), (x, y), (x, y, z) ...).
     * @param functionEnv
     * @param nonGenericVars
     * @param caseNode
     * @return TypeExpr
     */
    private TypeExpr analyzeTupleCase(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode caseNode) {
        
        caseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);

        final ParseTreeNode conditionNode = caseNode.firstChild();
        final TypeExpr conditionType = analyzeExpr(functionEnv, nonGenericVars, conditionNode);

        final ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
        
        if (!altListNode.hasExactlyOneChild()) {
            //tuple-case patterns have only 1 alternative. This should be caught earlier in static analysis.
            throw new IllegalArgumentException();
        }      

        final ParseTreeNode altNode = altListNode.firstChild();            
        altNode.verifyType(CALTreeParserTokenTypes.ALT);

        final ParseTreeNode patternNode = altNode.firstChild();
        patternNode.verifyType(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);
        
        // Add the pattern variables to the environment for when we type the expression to evaluate
        // as a consequence of pattern matching. Also, we must unify the type of the application
        // (constructor p1 p2 ... pn) with the type of the condition expression. For example, if
        // x : xs then we can type x :: a and xs :: b, then determine further restrictions
        // via unification with the type of the condition expression.

        Env extendedEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;
        
        int componentN = 1;
        final Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>(); 
        
        for (final ParseTreeNode patternVarNode : patternNode) {
                
            final TypeVar typeVar;
                
            switch (patternVarNode.getType()) {

                case CALTreeParserTokenTypes.VAR_ID:
                {                    
                    final String varName = patternVarNode.getText();
                    typeVar = new TypeVar();
                    extendedEnv = extendedEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), varName), Scope.PRIVATE, null, typeVar, FunctionalAgent.Form.PATTERNVAR, nestingLevel));
                    extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeVar);
                    functionBoundNamesToTypeMap.put(varName, typeVar);
                    break;
                }
                
                case CALTreeParserTokenTypes.UNDERSCORE:
                {
                    typeVar = new TypeVar();
                    //todoBI is it necessary to add the typeVar to the non-generics for wildcards?
                    extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeVar);                        
                    break;
                }
                
                default:
                {
                    patternVarNode.unexpectedParseTreeNode(); 
                    typeVar = null;
                    break;
                }
            }
            
            fieldNamesToTypeMap.put(FieldName.makeOrdinalField(componentN), typeVar);
            
            ++componentN;
        }

        final TypeExpr tuplePatternTypeExpr = new RecordType(RecordVar.NO_FIELDS, fieldNamesToTypeMap);

        try {            
            TypeExpr.unifyType(conditionType, tuplePatternTypeExpr, currentModuleTypeInfo);
        } catch (TypeException te) { 
            //the case pattern and the case condition must have the same type               
            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.CasePatternAndCaseConditionMustHaveSameType(), 
                te));
        }

        //now type the expression after the ->, which determines the type returned by the case. Use the extended environments.                     
        final ParseTreeNode exprNode = patternNode.nextSibling();
        final TypeExpr caseTypeExpr = analyzeExpr(extendedEnv, extendedNonGenericVars, exprNode);
        return caseTypeExpr;      
    }
    
    /**
     * Determines the type of a data constructor field selection such as expr.Just.value.
     *
     * @param functionEnv
     * @param nonGenericVars
     * @param selectNode
     * @return TypeExpr may be null if the type could not be determined (although in that case an error will be logged).
     */
    private TypeExpr analyzeDataConstructorFieldSelection(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode selectNode) {
        
        selectNode.verifyType(CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD);
        
        final ParseTreeNode conditionNode = selectNode.firstChild();
        final TypeExpr conditionType = analyzeExpr(functionEnv, nonGenericVars, conditionNode);
        
        final ParseTreeNode dcNameNode = conditionNode.nextSibling();
        dcNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        final DataConstructor dataConstructor = retrieveQualifiedDataConstructor(dcNameNode);
        if (dataConstructor == null) {
            //a message was logged in retrieveQualifiedDataConstructor
            return null;            
        }
        
        final ParseTreeNode fieldNameNode = dcNameNode.nextSibling();
        fieldNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
        
        final FieldName fieldName = getFieldName(fieldNameNode);
        
        // During the first pass of free variable finding, the select node should have had its children augmented with the 
        //  qualified var for the alt expression.        
        final ParseTreeNode qualifiedVarNode = fieldNameNode.nextSibling();
        qualifiedVarNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
        
        // Add corresponding pattern variables to the environment for when we type the expression to 
        // evaluate as a consequence of pattern matching. Also, we must unify the type of the application
        // (constructor p1 p2 ... pn) with the type of the condition expression. For example, if
        // x : xs then we can type x :: a and xs :: b, then determine further restrictions
        // via unification with the type of the condition expression.

        Env extendedEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;

        // Start out with the cons type expr.
        //As an example, if consName = "Just" then the type is "a -> Maybe a" and this is a constructor taking 1 argument.
        TypeExpr typeOfAppl = dataConstructor.getTypeExpr();
        
        for (int i = 0, arity = dataConstructor.getArity(); i < arity; i++) {
            final FieldName nthFieldName = dataConstructor.getNthFieldName(i);
            TypeVar typeVar = new TypeVar();

            if (nthFieldName.equals(fieldName)) {
                // a var.
                // Get the name of the pattern var.
                // This will not be the same as the field name for ordinal-based fields, since ordinals are not valid identifiers.
                final String varName = qualifiedVarNode.getChild(1).getText();
                
                extendedEnv = extendedEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), varName), Scope.PRIVATE, null, typeVar, FunctionalAgent.Form.PATTERNVAR, nestingLevel));
                extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeVar);
                functionBoundNamesToTypeMap.put(varName, typeVar);
                
            } else {
                // an underscore.
                //todoBI is it necessary to add the typeVar to the non-generics for wildcards?
                extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, typeVar);                        
            }

            // the type of (constructor p1 p2 ... pi)
            TypeExpr typeOfArgsSoFar = typeOfAppl;
            TypeExpr typeOfNextArg = typeVar;
            typeOfAppl = new TypeVar();

            try {                
                TypeExpr.unifyType(typeOfArgsSoFar, TypeExpr.makeFunType(typeOfNextArg, typeOfAppl), currentModuleTypeInfo);
            } catch (TypeException te) {  
                //this should never happen since we should have earlier checked that the number of args
                //supplied to the data constructor is correct.            
                compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Fatal.UnexpectedUnificationFailure(), te));
            }
        }                                    

        try {            
            TypeExpr.unifyType(conditionType, typeOfAppl, currentModuleTypeInfo);
        } catch (TypeException te) { 
            //the expression and the data constructor must have the same type               
            compiler.logMessage(new CompilerMessage(dcNameNode, new MessageKind.Error.ExpressionDoesNotMatchDataConstructorType(), te));
        }

        // Finally, return the type of the expression being evaluated.
        // This is the implicit qualified var which would appear on the rhs of the corresponding case.
        // eg. foo.DCName.argName   converts to   case foo of DCName {argName} -> argName
        //     then the "argName" on the rhs of the arrow is typed.
        return analyzeExpr(extendedEnv, extendedNonGenericVars, qualifiedVarNode);
    }
    
    /**
     * Determining the type of expressions.
     *
     * Creation date: (9/1/00 9:05:15 AM)
     * @return TypeExpr the type of the expression. May be null if the type could not be determined, although in that case
     *    an error message will be logged.
     * @param functionEnv environment to use for typing the expression
     * @param nonGenericVars the list of variables to treat as non-generic during the typing
     * @param parseTree an expression parse tree
     */
    private TypeExpr analyzeExpr(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode parseTree) {

        final int nodeType = parseTree.getType();

        switch (nodeType) {

            case CALTreeParserTokenTypes.VIRTUAL_LET_NONREC:
            case CALTreeParserTokenTypes.VIRTUAL_LET_REC: 
                return analyzeLet(functionEnv, nonGenericVars, parseTree);

            case CALTreeParserTokenTypes.LAMBDA_DEFN :
            {

                final ParseTreeNode paramListNode = parseTree.firstChild();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

                return analyzeLambda(functionEnv, nonGenericVars, paramListNode);
            }

            case CALTreeParserTokenTypes.LITERAL_if :
            {
                final ParseTreeNode conditionNode = parseTree.firstChild();
                final TypeExpr conditionTypeExpr = analyzeExpr(functionEnv, nonGenericVars, conditionNode);
                try {                
                    TypeExpr.unifyType(typeConstants.getBooleanType(), conditionTypeExpr, currentModuleTypeInfo);
                } catch (TypeException te) {
                    //the condition part of an if-then-else must be a Boolean
                    compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Error.ConditionPartOfIfThenElseMustBeBoolean(), te));
                        
                }               

                final ParseTreeNode ifTrueNode = conditionNode.nextSibling();
                final TypeExpr ifTrueTypeExpr = analyzeExpr(functionEnv, nonGenericVars, ifTrueNode);

                final ParseTreeNode ifFalseNode = ifTrueNode.nextSibling();
                final TypeExpr ifFalseTypeExpr = analyzeExpr(functionEnv, nonGenericVars, ifFalseNode);

                try {                
                    TypeExpr.unifyType(ifTrueTypeExpr, ifFalseTypeExpr, currentModuleTypeInfo);
                } catch (TypeException te) {
                    //the type of the 'then' and 'else' parts of an if-then-else must match
                    compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Error.TypeOfThenAndElsePartsMustMatch(), te));
                }

                return ifTrueTypeExpr;
            }

            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE :
            {
                errorCallList.add(parseTree);                
                
                final TypeExpr typeExpr = analyzeDataConstructorCase(functionEnv, nonGenericVars, parseTree);
                
                // save the type so later on I can know the type of the case expression.
                parseTree.setTypeExprForCaseExpr(typeExpr);
                
                return typeExpr;                
            }
            
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE: 
            {
                errorCallList.add(parseTree);
                
                final TypeExpr typeExpr = analyzeTupleCase(functionEnv, nonGenericVars, parseTree);
                
                // save the type so later on I can know the type of the case expression.
                parseTree.setTypeExprForCaseExpr(typeExpr);
                
                return typeExpr;                
            }
            
            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE :
            {                
                errorCallList.add(parseTree);
                 
                final TypeExpr typeExpr = analyzeRecordCase(functionEnv, nonGenericVars, parseTree);
                
                // save the type so later on I can know the type of the case expression.
                parseTree.setTypeExprForCaseExpr(typeExpr);
                
                return typeExpr;                
            }

            case CALTreeParserTokenTypes.BARBAR:              
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND: 
            case CALTreeParserTokenTypes.PLUSPLUS:               
            case CALTreeParserTokenTypes.EQUALSEQUALS:                              
            case CALTreeParserTokenTypes.NOT_EQUALS:              
            case CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS:             
            case CALTreeParserTokenTypes.GREATER_THAN:               
            case CALTreeParserTokenTypes.LESS_THAN:                
            case CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS:              
            case CALTreeParserTokenTypes.PLUS:               
            case CALTreeParserTokenTypes.MINUS:              
            case CALTreeParserTokenTypes.ASTERISK:              
            case CALTreeParserTokenTypes.SOLIDUS:
            case CALTreeParserTokenTypes.PERCENT:
            case CALTreeParserTokenTypes.COLON:
            case CALTreeParserTokenTypes.UNARY_MINUS:
            case CALTreeParserTokenTypes.POUND:
                return analyzeOperator(functionEnv, nonGenericVars, parseTree);               
                
            case CALTreeParserTokenTypes.DOLLAR:
            {
                // Turn the node representing the $ operator into an application node 
                
                parseTree.setType(CALTreeParserTokenTypes.APPLICATION);
                parseTree.setText("@operator");
                return analyzeApplication(functionEnv, nonGenericVars, parseTree);
            }

            case CALTreeParserTokenTypes.BACKQUOTE :
            {
                // Skip the back-quoted node 
                
                parseTree.setType(CALTreeParserTokenTypes.APPLICATION);
                parseTree.setText("@operator");
                parseTree.setFirstChild(parseTree.firstChild().firstChild());
                return analyzeApplication(functionEnv, nonGenericVars, parseTree);
            }
            
            case CALTreeParserTokenTypes.APPLICATION :
                return analyzeApplication(functionEnv, nonGenericVars, parseTree);

            //variables and functions
            case CALTreeParserTokenTypes.QUALIFIED_VAR :
            {
                // Keep track of which nodes have calls to error in order
                // to add source position information.
                
                {
                    final ModuleName moduleName = ModuleNameUtilities.getModuleNameFromParseTree(parseTree.getChild(0));
                    final ParseTreeNode name = parseTree.getChild(1);
                    
                    if (moduleName.equals(CAL_Prelude.MODULE_NAME)){
                        if (name.getText().equals(CAL_Prelude.Functions.error.getUnqualifiedName())){
                            errorCallList.add(parseTree);
                        }
                    }
                }
                
                final FunctionalAgent entity = retrieveQualifiedVar(functionEnv, parseTree, true);
                if (entity == null) {
                    //logging already done in retreiveQualifiedVar
                    return null;                    
                }
                
                final TypeExpr typeExpr = entity.getTypeExpr(nonGenericVars);
                final FunctionalAgent.Form form = entity.getForm();

                //exclude pattern bound variables from overload resolution
                if (form != FunctionalAgent.Form.PATTERNVAR) {

                    //functions that are in the process of being type checked are treated differently from functions 
                    //which are already type checked. Note: what we mean by "type checked" is that the body of all functions
                    //in the same declaration group have been type checked. In other words, the function is free to be
                    //generalized when used in the "in" part (if a local function) or in another component (if a top-level function).                                
                    final boolean isPolymorphic = entity.isTypeCheckingDone();                            
                    final ApplicationInfo apInfo = new ApplicationInfo(entity, parseTree, typeExpr, nonGenericVars, isPolymorphic);
                    currentOverloadingInfo.addApplication(apInfo);                                           
                }

                return typeExpr;
            }

            //data constructors 
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {
                final DataConstructor dataCons = retrieveQualifiedDataConstructor(parseTree);
                if (dataCons == null) {
                    //error logged in retrieveQualifiedDataConstructor
                    return null;
                }

                return dataCons.getTypeExpr();
            }

            // literals
            case CALTreeParserTokenTypes.CHAR_LITERAL :
                return typeConstants.getCharType();

            case CALTreeParserTokenTypes.INTEGER_LITERAL :
            {
                //the integer literal n is replaced by the application:
                //Prelude.fromInt n
                //if n is representable as an Int
                //Otherwise it is replaced by
                //Prelude.fromLong n
                //if n is representable as a Long
                //Otherwise it is replaced by
                //Prelude.fromInteger n
                //
                //In all cases, the application is marked for overload resolution
                
                final String integerAsString = parseTree.getText(); 
                BigInteger integerLiteral = null;
                try {
                    integerLiteral = new BigInteger(parseTree.getText());
                } catch (NumberFormatException e) {
                    // We failed to parse the INTEGER_LITERAL as an integer!
                    compiler.logMessage(new CompilerMessage(parseTree,
                        new MessageKind.Error.UnableToParseToIntegerLiteral(integerAsString)));                      
                }
                
                final SourcePosition sourcePosition = parseTree.getSourcePosition();
                final QualifiedName fromLiteralFunction;                
                final ParseTreeNode integerLiteralNode = new ParseTreeNode(CALTreeParserTokenTypes.INTEGER_LITERAL, integerAsString, sourcePosition);
                
                final int intLiteral;
                final long longLiteral;
                if (integerLiteral.compareTo(BigInteger.valueOf(intLiteral = integerLiteral.intValue())) == 0) {
                    
                    //the literal can be represented as an int
                    
                    fromLiteralFunction = CAL_Prelude.Functions.fromInt;
                    integerLiteralNode.setIntegerValueForMaybeMinusIntLiteral(Integer.valueOf(intLiteral));
                    
                } else if (integerLiteral.compareTo(BigInteger.valueOf(longLiteral = integerLiteral.longValue())) == 0) {
                    
                    //the literal cannot be represented as an int, but can be represented as a long
                    
                    fromLiteralFunction = CAL_Prelude.Functions.fromLong;
                    integerLiteralNode.setLongValueForMaybeMinusIntLiteral(Long.valueOf(longLiteral));
                    
                } else {
                    
                    //the literal can't be represented as an int or long- fall through to BigInteger which can represent them all.
                    
                    fromLiteralFunction = CAL_Prelude.Functions.fromInteger;
                    integerLiteralNode.setBigIntegerValueForMaybeMinusIntLiteral(integerLiteral);
                }                                                          

                final ParseTreeNode fromLiteralNode = ParseTreeNode.makeQualifiedVarNode(fromLiteralFunction, sourcePosition);
                fromLiteralNode.setNextSibling(integerLiteralNode);

                final ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@", sourcePosition);
                applicationNode.setFirstChild(fromLiteralNode);
                applicationNode.setNextSibling(parseTree.nextSibling());

                parseTree.copyContentsFrom(applicationNode);
                
                final ClassMethod classMethod = currentModuleTypeInfo.getVisibleClassMethod(fromLiteralFunction);
                final TypeExpr fromLiteralType = classMethod.getTypeExpr();
                final TypeExpr typeOfResult = fromLiteralType.getResultType();               
               
                final boolean isPolymorphic = true;                            
                final ApplicationInfo apInfo = new ApplicationInfo(classMethod, fromLiteralNode, fromLiteralType, null, isPolymorphic);
                currentOverloadingInfo.addApplication(apInfo);

                return typeOfResult;
            }

            case CALTreeParserTokenTypes.FLOAT_LITERAL :
                return typeConstants.getDoubleType();

            case CALTreeParserTokenTypes.STRING_LITERAL : 
                return typeConstants.getStringType();

            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
            {
                //Lists could be simply reduced to applications e.g. treat [1,2,3] as Cons 1 (Cons 2 (Cons 3 Nil))
                //However, it is clearer and more efficient to treat them as a special case.

                final TypeExpr unifiedElementType = new TypeVar();

                for (final ParseTreeNode elementNode : parseTree) {

                    final TypeExpr elementType = analyzeExpr(functionEnv, nonGenericVars, elementNode);
                    try {                    
                        TypeExpr.unifyType(unifiedElementType, elementType, currentModuleTypeInfo);
                    } catch (TypeException te) {                        
                        //all elements of a list must have compatible types.
                        compiler.logMessage(new CompilerMessage(elementNode, new MessageKind.Error.AllListElementsMustHaveCompatibleTypes(), te));                           
                    }
                }
              
                return typeConstants.makeListType(unifiedElementType.prune());
            }
            
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            {               
                if (parseTree.hasNoChildren()) {
                    //the Unit type, also known as ().
                    return typeConstants.getUnitType();
                }

                if (parseTree.hasExactlyOneChild()) {
                    //a parenthesized expression
                    return analyzeExpr(functionEnv, nonGenericVars, parseTree.firstChild());
                }

                //Tuples
                //The 3-tuple (a, b, c) is treated like {#1 = a, #2 = b, #3 = c}
              
                //FieldName -> TypeExpr
                final Map<FieldName, TypeExpr> extensionFieldsMap = new HashMap<FieldName, TypeExpr>(); 
                
                int componentN = 1;
                
                for (final ParseTreeNode componentNode : parseTree) {
                                                     
                    final TypeExpr valueExpr = analyzeExpr(functionEnv, nonGenericVars, componentNode);
                    
                    extensionFieldsMap.put(FieldName.makeOrdinalField(componentN), valueExpr);   
                    
                    ++componentN;
                }
                
                return new RecordType(RecordVar.NO_FIELDS, extensionFieldsMap);
            }
           
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR :             
            {
                return analyzeRecordConstructor(functionEnv, nonGenericVars, parseTree);                
            }
            
            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD :
            {
                errorCallList.add(parseTree);
                return analyzeDataConstructorFieldSelection(functionEnv, nonGenericVars, parseTree);
            }
                
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:
            {
                final ParseTreeNode exprNode = parseTree.firstChild();
                final TypeExpr exprTypeExpr = analyzeExpr(functionEnv, nonGenericVars, exprNode);
                
                final ParseTreeNode fieldNameNode = exprNode.nextSibling();
                final FieldName fieldName = getFieldName(fieldNameNode);                
                
                //make the record type r\fieldName => {r | fieldName :: a}.
                //exprTypeExpr must unify with this. 
                final Set<FieldName> lacksFieldsSet = new HashSet<FieldName>();
                lacksFieldsSet.add(fieldName); 
                final RecordVar baseRecordVar = RecordVar.makeRecordVar(null, lacksFieldsSet, TypeClass.NO_CLASS_CONSTRAINTS, false); 
                final Map<FieldName, TypeExpr> hasFieldsMap = new HashMap<FieldName, TypeExpr>();
                hasFieldsMap.put(fieldName, TypeExpr.makeParametricType());                                   
                final RecordType recordType = new RecordType(baseRecordVar, hasFieldsMap);
                
                try {                              
                    TypeExpr.unifyType(exprTypeExpr, recordType, currentModuleTypeInfo);
                } catch (TypeException te) {
                    if (parseTree.getIsSyntheticVarOrRecordFieldSelection()) {
                        // Type error. Invalid record field {fieldName} in local pattern match decl.
                        compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Error.InvalidRecordFieldInLocalPatternMatchDecl(fieldName, exprTypeExpr)));
                    } else {
                        // Type error. Invalid record selection for field {fieldName}.
                        compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Error.InvalidRecordSelectionForField(fieldName), 
                            te));
                    }
                }
                
                return recordType.getHasFieldType(fieldName).prune();                   
            }
            
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:
            {
                final ParseTreeNode exprNode = parseTree.firstChild();
                final TypeExpr inferredTypeExpr = analyzeExpr(functionEnv, nonGenericVars, exprNode);
                //copy for error reporting purposes in case pattern matching fails.
                final TypeExpr copyOfInferredTypeExpr = inferredTypeExpr.copyTypeExpr();
                final ParseTreeNode signatureNode = exprNode.nextSibling();
                final TypeExpr declaredTypeExpr = calculateTypeFromSignature(signatureNode, null);
                
                try {
                    TypeExpr.patternMatch(declaredTypeExpr, inferredTypeExpr, nonGenericVars, currentModuleTypeInfo);
                    
                } catch (TypeException te) {
                    //the declared type of the expression is not compatible with its inferred type.
                    //for example, this would happen for something like 
                    // 'a' :: Double
                    compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Error.DeclaredTypeOfExpressionNotCompatibleWithInferredType(copyOfInferredTypeExpr.toString()),
                        te));
                }
                
                return inferredTypeExpr;
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
     * Determining the type of application expressions.
     *
     * @param functionEnv environment to use for typing the expression
     * @param nonGenericVars the list of variables to treat as non-generic during the typing
     * @param parseTree an expression parse tree
     * @return TypeExpr the type of the expression
     */

    private TypeExpr analyzeApplication(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode parseTree) {
        
        // Look for calls to error/assert and undefined and flag them. The source position only
        // applies to fully saturated calls.
        
        // we know from the grammar that there is at least one child        
        final ParseTreeNode firstChildNode = parseTree.firstChild();
                
        if (firstChildNode.getType() == CALTokenTypes.QUALIFIED_VAR){                        
            // look for error/trace/assert/undefined call
            final ParseTreeNode module = firstChildNode.firstChild();

            final ModuleName moduleName = ModuleNameUtilities.getModuleNameFromParseTree(module);
            
            if (moduleName.equals(CAL_Prelude.MODULE_NAME)){
                final ParseTreeNode name = module.nextSibling();
                
                final String varName = name.getText();
                
                if (varName.equals(CAL_Prelude.Functions.assert_.getUnqualifiedName()) && parseTree.getNumberOfChildren() == 2){
                    errorCallList.add( parseTree );
                } else if (varName.equals(CAL_Prelude.Functions.undefined.getUnqualifiedName())){
                    errorCallList.add( parseTree );
                }
            }
        }
                       
        TypeExpr typeOfAppl = analyzeExpr(functionEnv, nonGenericVars, firstChildNode);

        for (final ParseTreeNode nextArgNode : firstChildNode.nextSiblings()) {

            // the type of (a1 a2 ... ai)
            final TypeExpr typeOfArgsSoFar = typeOfAppl;
            final TypeExpr typeOfNextArg = analyzeExpr(functionEnv, nonGenericVars, nextArgNode);
            typeOfAppl = new TypeVar();

            try {                    
                TypeExpr.unifyType(typeOfArgsSoFar, TypeExpr.makeFunType(typeOfNextArg, typeOfAppl), currentModuleTypeInfo);
            } catch (TypeException te) {
                //the type of the application so far is not compatible with its next argument
                compiler.logMessage(new CompilerMessage(nextArgNode, new MessageKind.Error.TypeErrorDuringApplication(), te));
            }
        }

        return typeOfAppl;
    }
    
    /**
     * A helper function to construct FieldName values from their ParseTreeNode form.
     * @param fieldNameNode
     * @return FieldName either a FieldName.Ordinal or a FieldName.Textual for the 2 different
     *    kinds of field names
     */
    FieldName getFieldName(final ParseTreeNode fieldNameNode) {
                   
        switch (fieldNameNode.getType()) {
            case CALTreeParserTokenTypes.VAR_ID:
            {
                return FieldName.makeTextualField(fieldNameNode.getText());               
            }
            
            case CALTreeParserTokenTypes.ORDINAL_FIELD_NAME:
            {
                final int ordinal;
                try {
                    ordinal = Integer.parseInt(fieldNameNode.getText().substring(1));
                } catch (NumberFormatException nfe) {
                    // Ordinal field name {fieldNameNode.getText()} is out of range. 
                    compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Error.OrdinalFieldNameOutOfRange(fieldNameNode.getText()), 
                        nfe));
                    return null;
                }
                return FieldName.makeOrdinalField(ordinal);
            }
            
            default:
            {
                fieldNameNode.unexpectedParseTreeNode();
                return null;
            }               
        }       
    }
    
    /**
     * Method analyzeOperator.  
     * @param functionEnv
     * @param nonGenericVars
     * @param parseTree
     * @return TypeExpr
     */
    private TypeExpr analyzeOperator(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode parseTree) {
      
        //Note operators can't be hidden by scoping. So that && always refers to Prelude.and and not
        //some local function named "and". Thus, we can't look up the type directly in the environment...
            
        final FunctionalAgent envEntity;
        final String operatorName = parseTree.getText();
          
        final int nodeKind = parseTree.getType(); 
        boolean unaryMinus = false;
        switch (nodeKind) {
            
            case CALTreeParserTokenTypes.BARBAR :              
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND :            
            { 
                envEntity = Function.makeTopLevelFunction(OperatorInfo.getTextualName(operatorName), typeConstants.getAndOrType(), Scope.PUBLIC);             
                break;
            }
            
            case CALTreeParserTokenTypes.POUND:
            { 
                envEntity = Function.makeTopLevelFunction(OperatorInfo.getTextualName(operatorName), TypeExpr.makeComposeType(), Scope.PUBLIC);             
                break;
            }
                
            case CALTreeParserTokenTypes.PLUSPLUS:      
            case CALTreeParserTokenTypes.EQUALSEQUALS:                              
            case CALTreeParserTokenTypes.NOT_EQUALS:              
            case CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS:             
            case CALTreeParserTokenTypes.GREATER_THAN:               
            case CALTreeParserTokenTypes.LESS_THAN:                
            case CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS:              
            case CALTreeParserTokenTypes.PLUS:               
            case CALTreeParserTokenTypes.MINUS:              
            case CALTreeParserTokenTypes.ASTERISK:              
            case CALTreeParserTokenTypes.SOLIDUS:
            case CALTreeParserTokenTypes.PERCENT:
            {             
                //These tokens are a short-hand for class methods               
                envEntity = currentModuleTypeInfo.getVisibleClassMethod(OperatorInfo.getTextualName(operatorName));              
                break;
            }
            
            case CALTreeParserTokenTypes.UNARY_MINUS:
            {
                envEntity = currentModuleTypeInfo.getVisibleClassMethod(CAL_Prelude.Functions.negate);
                unaryMinus = true;
                break;
            }
                                    
            case CALTreeParserTokenTypes.COLON : 
            {
                envEntity = currentModuleTypeInfo.getVisibleDataConstructor(OperatorInfo.getTextualName(operatorName));
                break;                              
            }
                
            default :
            {
            	parseTree.unexpectedParseTreeNode();
            	return null;                          
            }                                           
        }
        
        final TypeExpr typeOfOp = envEntity.getTypeExpr();
        
        //convert the parse tree so that it doesn't use operators (for example, + is converted to Prelude.add)
        //we do not do this in earlier passes (such as the FreeVariableFinder) in order to give better error messages.  
        final ParseTreeNode arg1Node = parseTree.firstChild();
        final QualifiedName textualName = unaryMinus? CAL_Prelude.Functions.negate : OperatorInfo.getTextualName(operatorName);
        final ParseTreeNode functionalAgentNode;
        if (nodeKind == CALTreeParserTokenTypes.COLON) {
            functionalAgentNode = ParseTreeNode.makeQualifiedConsNode(textualName, parseTree.getSourcePosition());
        } else {
            functionalAgentNode = ParseTreeNode.makeQualifiedVarNode(textualName, parseTree.getSourcePosition());
        }        
        parseTree.setType(CALTreeParserTokenTypes.APPLICATION);
        parseTree.setText("@operator");
        parseTree.setFirstChild(functionalAgentNode);
        functionalAgentNode.setNextSibling(arg1Node);        
        
              
        //(&&), (||), (:), (#) do not need overload resolution, since their type signatures do not
        //depend on constrained type variables. 
        switch (nodeKind) {
            case CALTreeParserTokenTypes.BARBAR :              
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND : 
            case CALTreeParserTokenTypes.COLON :   
            case CALTreeParserTokenTypes.POUND:
                break;
                
            default:
            {
                final boolean isPolymorphic = true;       
                final ApplicationInfo apInfo = new ApplicationInfo(envEntity, functionalAgentNode, typeOfOp, null, isPolymorphic);
                currentOverloadingInfo.addApplication(apInfo);   
                break;              
            }
        }
        
        final TypeExpr typeOfArg1 = analyzeExpr(functionEnv, nonGenericVars, arg1Node);
        final TypeExpr typeOfOpAppliedToArg1 = new TypeVar();

        try {       
            TypeExpr.unifyType(typeOfOp, TypeExpr.makeFunType(typeOfArg1, typeOfOpAppliedToArg1), currentModuleTypeInfo);
        } catch (TypeException te) {
            //type clash between the operator and its first argument
            compiler.logMessage(new CompilerMessage(functionalAgentNode, new MessageKind.Error.TypeErrorApplyingOperatorToFirstArgument(operatorName), 
                te));
        }
        
        if (unaryMinus) {
            return typeOfOpAppliedToArg1;
        }

        final ParseTreeNode arg2Node = arg1Node.nextSibling();        
        final TypeExpr typeOfArg2 = analyzeExpr(functionEnv, nonGenericVars, arg2Node);
        final TypeExpr typeOfResult = new TypeVar();

        try {        
            TypeExpr.unifyType(typeOfOpAppliedToArg1, TypeExpr.makeFunType(typeOfArg2, typeOfResult), currentModuleTypeInfo);
        } catch (TypeException te) {
            //type clash between the operator and its second argument
            compiler.logMessage(new CompilerMessage(functionalAgentNode, new MessageKind.Error.TypeErrorApplyingOperatorToSecondArgument(operatorName), 
                te));
        }       

        return typeOfResult;        
    }
    
    /**
     * Determines the type of a lambda expression.
     *
     * Creation date: (9/1/00 9:05:15 AM)
     * @return TypeExpr the type of the lambda expression
     * @param functionEnv environment to use for typing the function
     * @param nonGenericVars the list of variables to treat as non-generic during the typing
     * @param paramListNode the parse tree for the lambda expression
     */
    private TypeExpr analyzeLambda(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode paramListNode) {

        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

        final List<TypeVar> typesOfArgs = new ArrayList<TypeVar>();

        Env extendedFunctionEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;
        for (final ParseTreeNode varNode : paramListNode) {
            
            varNode.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);

            final String varName = varNode.getText();
            
            if (varNode.getType() == CALTreeParserTokenTypes.STRICT_PARAM) {
                evaluatedLocalVariables.add(varName);
            }
                       
            final TypeVar newTypeVar = new TypeVar();
            typesOfArgs.add(newTypeVar);

            extendedFunctionEnv = extendedFunctionEnv.extend(new Function(QualifiedName.make(currentModuleTypeInfo.getModuleName(), varName), Scope.PRIVATE, null, newTypeVar, FunctionalAgent.Form.PATTERNVAR, nestingLevel));
            extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, newTypeVar);
            functionBoundNamesToTypeMap.put(varName, newTypeVar);
        }

        final ParseTreeNode exprNode = paramListNode.nextSibling();

        final TypeExpr typeOfBody = analyzeExpr(extendedFunctionEnv, extendedNonGenericVars, exprNode);

        TypeExpr typeOfLambda = typeOfBody;

        for (int i = typesOfArgs.size() - 1; i >= 0; --i) {
            typeOfLambda = TypeExpr.makeFunType(typesOfArgs.get(i), typeOfLambda);
        }

        // save the type so later on I can know the type of the lambda expression.
        paramListNode.setTypeExprForFunctionParamList(typeOfLambda);

        return typeOfLambda;
    }
    
    /**
     * A helper function to determine the declared types in a let block.
     * @param defnListNode
     * @return Map (String -> TypeExpr) local function name to its declared type.
     */    
    private Map<String, TypeExpr> calculateDeclaredTypesMap(final ParseTreeNode defnListNode) {
        
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);
        
        final Map<String, TypeExpr> declaredTypesMap = new HashMap<String, TypeExpr>();
        
        for (final ParseTreeNode defnNode : defnListNode) {
                       
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION) {
                
                final ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                final ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                
                final String functionName = typeDeclNode.firstChild().getText();
                final TypeExpr declaredType = calculateDeclaredType(typeDeclNode); 
                declaredTypesMap.put(functionName, declaredType); 
            }        
        } 
        
        return declaredTypesMap;       
    }
    
    /**
     * Attempts to match the inferred type for a local function with the type determined by the local type declaration.
     *
     * @param defnNode parse tree of the local function definition
     * @param declaredTypesMap
     * @param inferredTypeExpr (in/out) the inferred type for the local function, which when this method returns successfully, 
     *      will be pattern matched against the declared type. 
     * @param nonGenericVars
     */
    private void localPatternMatch(final ParseTreeNode defnNode, final Map<String, TypeExpr> declaredTypesMap,
            final TypeExpr inferredTypeExpr, final NonGenericVars nonGenericVars) {

        defnNode.verifyType(CALTreeParserTokenTypes.LET_DEFN);
        
        final ParseTreeNode optionalCALDocNode = defnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        final ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
        final String functionName = localFunctionNameNode.getText();

        final TypeExpr originalDeclaredTypeExpr = declaredTypesMap.get(functionName);

        if (originalDeclaredTypeExpr != null) {
            
            final TypeExpr declaredTypeExpr = originalDeclaredTypeExpr.copyTypeExpr(); // we use a copy of the declared type, so as to keep the original one pristine for later checking

            try {
                TypeExpr.patternMatch(declaredTypeExpr, inferredTypeExpr, nonGenericVars, currentModuleTypeInfo);

            } catch (TypeException te) { 
                //the declared type of the local function is not compatible with its inferred type.           
                compiler.logMessage(new CompilerMessage(defnNode, new MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType(FreeVariableFinder.getDisplayName(functionName), inferredTypeExpr.toString()),
                    te));
            }
        }
    }    
      
    /**
     * Determines the type of a let expression. Note this is the same as "letrec" in the Core functional
     * language in that the functions defined within the let can be mutually recursive.
     *
     * Creation date: (9/5/00 1:26:53 PM)
     * @return TypeExpr
     * @param functionEnv
     * @param nonGenericVars
     * @param letNode
     */
    private TypeExpr analyzeLet(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode letNode) {

        letNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_LET_NONREC, CALTreeParserTokenTypes.VIRTUAL_LET_REC);

        ++nestingLevel;

        final ParseTreeNode defnListNode = letNode.firstChild();
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);

        Env extendedEnv = functionEnv;
        NonGenericVars extendedNonGenericVars = nonGenericVars;
      
        final int baseSize = overloadingInfoList.size();

        int componentSizeCounter = 0;   

        //if the let expression is (let f1 x1 = e1; f2 x2 = e2; ... fn xn = en in e), then add f1, f2, ..., fn to the
        //extended environment prior to type checking e1, e2, ..., en.
        for (final ParseTreeNode defnNode : defnListNode) {
           
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                
                componentSizeCounter++;

                final ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                final ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                localFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                final String functionName = localFunctionNameNode.getText();
                
                final ParseTreeNode paramListNode = localFunctionNameNode.nextSibling();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
                
                //special check for lexical 0-argument let declarations.
                if (paramListNode.hasNoChildren()) {
               
                    //we check for the special let variables of the form
                    //x = expression known to be evaluated to weak-head normal form
                    //some important cases are:
                    //x = Prelude.eager expr;
                    //x = Prelude.eager $ expr;
                    //x = "abc"; //literal String                                               
                    //and mark the variable x as being "evaluated".
                    //This is so that the lambda lifter can add these as strict arguments to lifted functions
                    //when actually doing the lifting.
                    
                    //note that we will also be marking as evaluated let variables that are not truly variables
                    //because they depend on an overloaded type signature e.g.
                    //x :: Num a => a;
                    //x = 1;
                    //However, this doesn't really matter since the lambda lifter will not add these as extra arguments
                    //to a lifted function, but rather lift them in their own right.
                    
                    //note that we exclude 0-arity functions (not data constructors) such as
                    //x = undefined
                    //since evaluating these may intentionally terminate in a run-time error.
                                        
                    final ParseTreeNode exprNode = paramListNode.nextSibling();
                    if (isEvaluatedExpr(functionEnv, exprNode)) {
                        evaluatedLocalVariables.add(functionName);
                    }
                }
                
                final List<String> namedArgumentsList = new ArrayList<String>();
                for (final ParseTreeNode varNode : paramListNode) {

                    varNode.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);
                    final String varName = FreeVariableFinder.getDisplayName(varNode.getText());
                    namedArgumentsList.add(varName);
                }
                final String[] namedArguments = namedArgumentsList.toArray(new String[0]);
                                   
                final TypeVar newTypeVar = new TypeVar();
    
                final Function function = 
                    new Function(
                            QualifiedName.make(currentModuleTypeInfo.getModuleName(), functionName),  
                            Scope.PRIVATE, 
                            namedArguments, 
                            newTypeVar, 
                            FunctionalAgent.Form.FUNCTION, 
                            nestingLevel);
                
                extendedEnv = extendedEnv.extend(function);
                localFunctionBoundNamesToFunctionEntityMap.put(functionName, function);
                extendedNonGenericVars = NonGenericVars.extend(extendedNonGenericVars, newTypeVar); 
                functionBoundNamesToTypeMap.put(functionName, newTypeVar);
                localFunctionBoundNamesToNonGenericVarsMap.put(functionName, nonGenericVars);
                overloadingInfoList.add(new OverloadingInfo(function, defnNode, newTypeVar, currentOverloadingInfo));
            }
        }

        final int componentSize = componentSizeCounter;
                        
        final Map<String, TypeExpr> declaredTypesMap = calculateDeclaredTypesMap(defnListNode);

        int componentN = 0;

        //now type check the e1, e2, ..., en.
        
        // We store a map of the LET_DEFN to their type expressions, to be checked against the corresponding type declaration
        // in the second phase.
        //
        // We need to do the checking in two phases because when the inferred type and its corresponding declared type
        // are unified in the first phase, the inferred type may not be the ultimate specific type of the function (as it
        // may be further restricted by the types of subsequent functions in the component) and thus not all type errors are
        // detectable then. The second phase, with its second loop, is able to handle the situation as the inferred types should all
        // have been restricted to their final form. 
        //
        // An example of such a component is:
        //
        // let
        //     gt :: (Prelude.Num a) => a -> a -> Boolean;
        //     gt x y =
        //         let
        //             foo = gt3 0 1;
        //         in
        //             x > y;
        //
        //     gt2 :: Int -> Int -> Boolean;
        //     gt2 = gt;
        //
        //     gt3 = gt2;
        // in
        //     gt
        //
        // Here, the local definition of foo in gt constrains gt to have a type of Int -> Int -> Boolean (since it uses gt3
        // which is aliased to gt2, which in turn is aliased to gt, but constraining both arguments to type Int). Thus the
        // type declaration for gt is too general, but cannot be detected in the first phase as gt2 has not been type checked
        // (and more importantly has had its inferred type unified with its type declaration).
        //
        final IdentityHashMap<ParseTreeNode, TypeExpr> localFunctionTypes = new IdentityHashMap<ParseTreeNode, TypeExpr>();
        
        for (final ParseTreeNode defnNode : defnListNode) {
                 
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {                 

                final ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                final ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                localFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                final String functionName = localFunctionNameNode.getText();
    
                final ParseTreeNode paramListNode = localFunctionNameNode.nextSibling();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
                                             
                currentOverloadingInfo = overloadingInfoList.get(baseSize + componentN);
               
    
                final TypeExpr localFunctionType = extendedEnv.retrieveEntity(functionName).getTypeExpr(extendedNonGenericVars);
                final TypeExpr exprType = analyzeLambda(extendedEnv, extendedNonGenericVars, paramListNode);
                try {                
                    TypeExpr.unifyType(localFunctionType, exprType, currentModuleTypeInfo);
                } catch (TypeException te) {
                    if (defnNode.getIsDesugaredPatternMatchForLetDefn()) {
                        if (FreeVariableFinder.isInternalSyntheticVariable(functionName)) {
                            //type of the desugared definition of a pattern match decl is not compatible with the type of some of its pattern-bound variables
                            //for example this happens when typing:
                            //foo19 = let {ax, b2} = (b2, ax); in (ax, b2);
                            compiler.logMessage(new CompilerMessage(defnNode, new MessageKind.Error.TypeOfDesugaredDefnOfLocalPatternMatchDeclNotCompatibleWithPatternBoundVars(),
                                te));
                            
                        } else {
                            //type of the local pattern-bound variable is not compatible with the defining expression of the local pattern match decl
                            //for example, this happens when typing g in:
                            //test = let f = 1.0 + g; (g,h) = ((f, 2.0), "foo"); in True;
                            compiler.logMessage(new CompilerMessage(defnNode, new MessageKind.Error.TypeOfPatternBoundVariableNotCompatibleWithLocalPatternMatchDecl(FreeVariableFinder.getDisplayName(functionName)),
                                te));
                        }
                        
                    } else {
                        //type of the local function is not compatible with its defining expression
                        //for example, this happens when typing g in:
                        //test = let f = 1.0 + g; g = (f, 2.0); in True;
                        compiler.logMessage(new CompilerMessage(defnNode, new MessageKind.Error.TypeOfLocalFunctionNotCompatibleWithDefiningExpression(FreeVariableFinder.getDisplayName(functionName)),
                            te));
                    }
                }
                
                // Even though we are not doing the checking of the type declarations in this phase, we still need
                // to unified the inferred type with the type declaration, since it may constrain the function to a
                // more specific type than the one inferred.
                //   
                //Another interesting point is that we use nonGenericVars instead of extendedNonGenericVars. The problem
                //type variables are those that come from the enclosing scope.                
                //can't pattern match a nongeneric type variable to a generic type variable.
                //For example, for the function:
                //g y = let f x = x + y; in f y;
                //we can't add a local type declaration f :: Num a => a -> a;
                //because that would imply that a is a generic polymorphic type variable,
                //and f could be applied polymorphically in the "in" part. In fact,
                //f's actual type is constrained by each application in the "in" part.             
                localPatternMatch(defnNode, declaredTypesMap, localFunctionType, nonGenericVars);
                
                // Add the type expression as one to be checked in the second phase.                localFunctionTypes.put(defnNode, localFunctionType);
    
                
                // If the local function is generated by desugaring a local record/tuple pattern match declaration,
                // then we will need to perform additional checks on the type of the defining expression because implicit
                // in the declaration of the record/tuple pattern is a constraint on the exact fields that must be in the record type.
                
                // Retrieve the SortedSet of FieldNames corresponding to non-polymorphic record patterns appearing in the local pattern match declaration
                final SortedSet<FieldName> declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn =
                    defnNode.getDeclaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn();
                
                if (declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn != null) {
                    
                    // We unify the type of the expression with the type implied by the non-polymorphic record pattern 
                    
                    final Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                    
                    for (final FieldName fieldName : declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn) {
                                                
                        fieldNamesToTypeMap.put(fieldName, new TypeVar());
                    }
                    
                    final TypeExpr nonPolymorphicRecordPatternTypeExpr = new RecordType(RecordVar.NO_FIELDS, fieldNamesToTypeMap);

                    try {            
                        TypeExpr.unifyType(exprType, nonPolymorphicRecordPatternTypeExpr, currentModuleTypeInfo);
                        
                    } catch (TypeException te) { 
                        //the local pattern match pattern and the defining expression must have the same type
                        reportErrorOnTypeMismatchForNonPolymorphicLocalRecordPatternMatchDecl(defnNode, exprType, declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn);
                    }
                    
                } else {
                    // Retrieve the SortedSet of FieldNames corresponding to *polymorphic* record patterns appearing in the local pattern match declaration
                    final SortedSet<FieldName> declaredFieldsInPolymorphicRecordPatternMatchForLetDefn = 
                        defnNode.getDeclaredFieldsInPolymorphicRecordPatternMatchForLetDefn();
                    
                    if (declaredFieldsInPolymorphicRecordPatternMatchForLetDefn != null) {
                        
                        // We unify the type of the expression with the type implied by the polymorphic record pattern 
                        
                        final Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                        
                        for (final FieldName fieldName : declaredFieldsInPolymorphicRecordPatternMatchForLetDefn) {
                           
                            fieldNamesToTypeMap.put(fieldName, new TypeVar());
                        }
                        
                        try {            
                            final TypeExpr polymorphicRecordPatternTypeExpr = RecordType.recordExtension(new TypeVar(), fieldNamesToTypeMap);
                            
                            TypeExpr.unifyType(exprType, polymorphicRecordPatternTypeExpr, currentModuleTypeInfo);
                            
                        } catch (TypeException te) { 
                            //the local pattern match pattern and the defining expression must have the same type
                            reportErrorOnTypeMismatchForPolymorphicLocalRecordPatternMatchDecl(defnNode, exprType, declaredFieldsInPolymorphicRecordPatternMatchForLetDefn);
                        }
                    }                    
                }
                
                // Store the localFunctionType in the OPTIONAL_CALDOC_COMMENT node
                // for use by the CALDocChecker in verifying the associated CALDoc comment's @arg blocks.
                // Note that we count on localFunctionType itself to be modified in the remainder of the
                // type checking process. In particular, the value localFunctionType.getNApplications()
                // at this point may not reflect the actual number of arguments accepted by the local function.
                // For example:
                //
                // fact x =
                //    let
                //        /**
                //         * @arg y the arg.
                //         */
                //        f = fact;
                //    in
                //        if (x :: Prelude.Int) == 0 then 1 else x * f (x - 1);
                //
                // The value of localFunctionType for the local function 'f' at this point is simply 'a',
                // and not the final Int -> Int.
                optionalCALDocNode.setFunctionTypeForLocalFunctionCALDocComment(localFunctionType);
                
               
                if (WARNING_FOR_OVERLOADED_POLYMORPHIC_LET_VARS &&
                    paramListNode.hasNoChildren() &&
                    !localFunctionType.getGenericClassConstrainedPolymorphicVars(nonGenericVars).isEmpty() &&
                    !localFunctionNameNode.getIsSyntheticVarOrRecordFieldSelection()) {
                    
                    //the 2nd condition says that we have a local variable declaration rather than a local function declaration.
                    
                    //the 3rd condition says that the type of the local variable is a polymorphic overloaded type.
                    //in particular, this local variable actually is a local function in the runtime because overloading must
                    //be resolved. For example,
                    //let x = 1; in ...
                    //then x has polymorphic type Num a => a
                    
                    //the 4th condition says that the local variable is not synthetically added by the compiler
                    //which is the case when a local pattern match decl is desugared, e.g.
                    //   let (x, y)=(1, 2); in "foo"
                    //is desugared to:
                    //   let x=$pattern_x_y.#1; y=$pattern_x_y.#2; $pattern_x_y=(1, 2); in "foo"
                    //then the last desugared definition is a synthetic local variable declaration
                    
                    letVarNameToParseTreeMap.put(functionName, localFunctionNameNode);
                }
                
                ++componentN;
            }
        }
        
        // Now we perform the second phase of the type checking of the local functions: checking that the type declarations
        // do unify with the inferred types of the local functions.
        if (componentSize > 1) {
            // optimization: we do not need to do the second pattern match when the componentSize is 1
            // because the type unification in the first phase is sufficient for checking the declared type
            // against the (final) inferred type.
            for (final ParseTreeNode defnNode : defnListNode) {

                if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {                 

                    final TypeExpr localFunctionType = localFunctionTypes.get(defnNode);

                    //verify that the inferred type of the local function in fact meets the assertion of the declared type (if any)
                    //the inferred type may be specialized here as well.
                    //   
                    //Another interesting point is that we use nonGenericVars instead of extendedNonGenericVars. The problem
                    //type variables are those that come from the enclosing scope.                
                    //can't pattern match a nongeneric type variable to a generic type variable.
                    //For example, for the function:
                    //g y = let f x = x + y; in f y;
                    //we can't add a local type declaration f :: Num a => a -> a;
                    //because that would imply that a is a generic polymorphic type variable,
                    //and f could be applied polymorphically in the "in" part. In fact,
                    //f's actual type is constrained by each application in the "in" part.             
                    localPatternMatch(defnNode, declaredTypesMap, localFunctionType, nonGenericVars);
                }
            }
        }

        //the types of the functions in this component (declaration group) are now free to be generalized.
        extendedEnv.finishedTypeChecking(componentSize);
        --nestingLevel;
        currentOverloadingInfo = currentOverloadingInfo.getParent();
        
        //we can now determine what dictionary variables need to be added to each local function
        //these correspond to the type variables in the type of the signature that are *generic* constrained type variables.
        //non-generic constrained type variables will be resolved in an enclosing scope.
        for (int i = 0; i < componentSize; ++i) {
            OverloadingInfo oi = overloadingInfoList.get(baseSize + i);
            oi.finishedTypeCheckingFunction(nonGenericVars);
        }

        final ParseTreeNode exprNode = defnListNode.nextSibling();

        // The let local function or CAFs are nonGeneric only during their declaration,
        // and not for the "in" part. This is why we use nonGenericVars here and not
        // extendedNonGenericVars below.
        return analyzeExpr(extendedEnv, nonGenericVars, exprNode);
    }

    /**
     * Reports an error for a type mismatch of a local non-polymorphic record (or tuple) pattern match declaration
     * and its defining expression.
     * @param defnNode the node to report the error on.
     * @param exprType the type of the defining expression.
     * @param declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn the declared fields of the non-polymorphic record (or tuple) pattern.
     */
    private void reportErrorOnTypeMismatchForNonPolymorphicLocalRecordPatternMatchDecl(
            ParseTreeNode defnNode, 
            final TypeExpr exprType,
            final SortedSet<FieldName> declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn) {
        // we figure out whether the declared fields form a tuple type or not, and produce
        // the appropriate error
        
        int tupleDimension = 0;
        for (final FieldName fieldName : declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn) {
            
            if (fieldName instanceof FieldName.Ordinal) {
                if (((FieldName.Ordinal)fieldName).getOrdinal() == tupleDimension + 1) {
                    tupleDimension++;
                } else {
                    // includes non-contiguous ordinal fields - not a tuple
                    tupleDimension = -1;
                    break;
                }
            } else {
                // includes non-ordinal fields - not a tuple
                tupleDimension = -1;
                break;
            }
        }
        
        if (tupleDimension < 2) {
            // just the field #1 - not a tuple
            tupleDimension = -1;
        }
        
        // report the appropriate error
        if (tupleDimension == -1) {
            compiler.logMessage(new CompilerMessage(defnNode,
                new MessageKind.Error.LocalPatternMatchDeclMustHaveFields(exprType, declaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn)));
        } else {
            compiler.logMessage(new CompilerMessage(defnNode,
                new MessageKind.Error.LocalPatternMatchDeclMustHaveTupleDimension(exprType, tupleDimension)));
        }
    }
    
    /**
     * Reports an error for a type mismatch of a local polymorphic record (or tuple) pattern match declaration
     * and its defining expression.
     * @param defnNode the node to report the error on.
     * @param exprType the type of the defining expression.
     * @param declaredFieldsInPolymorphicRecordPatternMatchForLetDefn the declared fields of the non-polymorphic record (or tuple) pattern.
     */
    private void reportErrorOnTypeMismatchForPolymorphicLocalRecordPatternMatchDecl(
        ParseTreeNode defnNode,
        final TypeExpr exprType,
        final SortedSet<FieldName> declaredFieldsInPolymorphicRecordPatternMatchForLetDefn) {
        
        compiler.logMessage(new CompilerMessage(defnNode,
            new MessageKind.Error.LocalPatternMatchDeclMustAtLeastHaveFields(exprType, declaredFieldsInPolymorphicRecordPatternMatchForLetDefn)));
    }
    
    /**
     * Identifies calls to Prelude.eager. Also handles some degenerate situations which are
     * parser and analysis artifacts that semantically are equivalent to a reference to Prelude.eager.   
     * a) parenthesized calls to Prelude.eager such as (((Prelude.eager)))
     * b) zero-argument applications of Prelude.eager such as (APPLICATION_NODE^ Prelude.eager) 
     * 
     * @param exprNode
     * @return true if the node is a call to Prelude.eager.
     */
    private static boolean isEagerFunctionNode (final ParseTreeNode exprNode) {
        switch (exprNode.getType()) {
            case CALTreeParserTokenTypes.QUALIFIED_VAR :
            {
                final ParseTreeNode moduleNode = exprNode.firstChild();
                final ParseTreeNode varNode = moduleNode.nextSibling();
                return varNode.getText().equals(CAL_Prelude.Functions.eager.getUnqualifiedName()) &&
                    ModuleNameUtilities.getModuleNameFromParseTree(moduleNode).equals(CAL_Prelude.MODULE_NAME);
            }
            
            case CALTreeParserTokenTypes.APPLICATION : 
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR:
            {
                return exprNode.hasExactlyOneChild() && isEagerFunctionNode(exprNode.firstChild());               
            }
            
            default:
            {
                return false;
            }
        }
    }
    
    /**
     * A helper function which returns true if the compiler can guarantee that the expression is already
     * evaluated to weak-head normal form. Note that it is possible to do more aggressive check here but
     * we limit ourselves to simple cases.
     * 
     * The main application of this function is when analyzing let variables of the form
     * x = expr;
     * then if expr is know to be evaluated, we can mark x as an evaluated variable. Then if x is used
     * in a local function, it can be added as a plinged variable to that local function. This is a run-time
     * optimization.
     * 
     * Some of the main cases that are handled:
     * -top level applications of Prelude.eager
     * -literals (string, integers, double, lists, tuples, non-extension records)
     * -aliases for positive arity top-level functions and (zero of positive arity) data constructors
     * -aliases for evaluated local variables
     * -certain special data constructor field selections of the form (evaluated-expression).MyDataConstructor.myStrictField)
     * 
     * @param functionEnv the environment to look up local symbols from
     * @param exprNode
     * @return true if the expression is guaranteed to be evaluated to weak-head normal form.
     */
    private boolean isEvaluatedExpr(final Env functionEnv, final ParseTreeNode exprNode) {
        
        final int nodeType = exprNode.getType();
        
        switch (nodeType) {

            case CALTreeParserTokenTypes.VIRTUAL_LET_NONREC:
            case CALTreeParserTokenTypes.VIRTUAL_LET_REC: 
            {
                //a let or let rec is evaluated to weak-head normal form if its in part is.
                
                final ParseTreeNode defnListNode = exprNode.firstChild();
                defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);
                
                final ParseTreeNode inExprNode = defnListNode.nextSibling();
                return isEvaluatedExpr(functionEnv, inExprNode);
            }

            case CALTreeParserTokenTypes.LAMBDA_DEFN :                
            case CALTreeParserTokenTypes.LITERAL_if :            
            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE :           
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE:            
            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE :            
            case CALTreeParserTokenTypes.BARBAR :              
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND : 
            case CALTreeParserTokenTypes.PLUSPLUS :               
            case CALTreeParserTokenTypes.EQUALSEQUALS :                              
            case CALTreeParserTokenTypes.NOT_EQUALS :              
            case CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS :             
            case CALTreeParserTokenTypes.GREATER_THAN :               
            case CALTreeParserTokenTypes.LESS_THAN :                
            case CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS :              
            case CALTreeParserTokenTypes.PLUS :               
            case CALTreeParserTokenTypes.MINUS :              
            case CALTreeParserTokenTypes.ASTERISK :              
            case CALTreeParserTokenTypes.SOLIDUS :    
            case CALTreeParserTokenTypes.PERCENT:
            case CALTreeParserTokenTypes.UNARY_MINUS:
            case CALTreeParserTokenTypes.POUND:
                return false;
                
            case CALTreeParserTokenTypes.COLON : 
            {
                //Prelude.Cons fully saturated is in weak-head normal form since both arguments are non-strict.
                return true;
            }
                
            case CALTreeParserTokenTypes.DOLLAR:
            {                
                return isEvaluatedExpr(functionEnv, exprNode.firstChild());
            }

            case CALTreeParserTokenTypes.BACKQUOTE :
                return false;
            
            case CALTreeParserTokenTypes.APPLICATION :
            {
                //true if we have an application of Prelude.eager to an expr.
                final ParseTreeNode argNode = exprNode.firstChild();
                if (argNode.nextSibling() == null) {
                    return isEvaluatedExpr(functionEnv, argNode);
                }
                
                return isEagerFunctionNode(argNode);                
            }

            //variables and functions
            case CALTreeParserTokenTypes.QUALIFIED_VAR :
            {                
                final FunctionalAgent entity = retrieveQualifiedVar(functionEnv, exprNode, false);
                if (entity == null) {
                    //we don't log errors for entites not found. This is because we don't
                    //build a deep environment so that code such as
                    //let 
                    //   x = let y = 2.0; in y;
                    //in expr
                    //will not be able to resolve the local symbol y.
                    
                    return false;                    
                }
                
                if (entity.getNestingLevel() == 0
                    && entity.getForm() != FunctionalAgent.Form.PATTERNVAR) {
                    //references to top level functions are in weak head normal form.
                    //note that arguments of top level functions have nesting level 0, so we must exclude pattern variables as well.
                    
                    //we want to exclude 0-arity functions since these may intentionally evaluate to a run-time error.
                    //x = Prelude.undefined;                    
                    return entity.getTypeExpr().getArity() > 0;                  
                }
                
                //aliases of evaluated variables are evaluted.
                return isEvaluatedLocalVariable(entity.getName().getUnqualifiedName());                                          
            }

            //data constructors 
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {
                final DataConstructor dataCons = retrieveQualifiedDataConstructor(exprNode);
                if (dataCons == null) {
                    //error logged in retrieveQualifiedDataConstructor
                    return false;
                }
                
                //data constructors applied to 0 arguments are already in WHNF.
                return true;              
            }

            // literals
            case CALTreeParserTokenTypes.CHAR_LITERAL :             
            case CALTreeParserTokenTypes.INTEGER_LITERAL : 
            case CALTreeParserTokenTypes.FLOAT_LITERAL :              
            case CALTreeParserTokenTypes.STRING_LITERAL :             
            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
            {
                //literals are already in weak-head normal form.
                //the integer literal case is a bit different since it may in fact be an overloaded value of type Num a.
                //but we won't be calling this function in that case...
                return true;
            }
            
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            {                                    
                if (exprNode.hasExactlyOneChild()) {
                    //a parenthesized expression
                    return isEvaluatedExpr(functionEnv, exprNode.firstChild());
                }
                
                //literal tuples are in weak-head normal form
                return true;                
            }
           
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR :     
            {
                final ParseTreeNode baseRecordNode = exprNode.firstChild();
                baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);                           
                final ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
                
                //a record which is not a record-extension i.e. a record literal is evaluated to weak-head normal form.
                return baseRecordExprNode == null;                
            }
            
            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD : 
            {
                //handle the special pattern
                //(evalauted expr).MyDataConstructor.myStrictField
                    
                final ParseTreeNode conditionNode = exprNode.firstChild();
              
                final ParseTreeNode dcNameNode = conditionNode.nextSibling();
                dcNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                
                final DataConstructor dataConstructor = retrieveQualifiedDataConstructor(dcNameNode);
                if (dataConstructor == null) {
                    //a message was logged in retrieveQualifiedDataConstructor
                    return false;            
                }
                
                final ParseTreeNode fieldNameNode = dcNameNode.nextSibling();
                fieldNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
                
                final FieldName fieldName = getFieldName(fieldNameNode); 
                
                if (!dataConstructor.isArgStrict(dataConstructor.getFieldIndex(fieldName))) {
                    //if the field is not a strict field of the data constructor, then it is not evaluated.
                    return false;
                }
                
                return isEvaluatedExpr(functionEnv, conditionNode);                                    
            }
                
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD: 
                return false;
                
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:
            {    
                //an expression type-signature is evaluated to weak-head normal form if its expression is.
                return isEvaluatedExpr(functionEnv, exprNode.firstChild());                
            }
            
            default :
            {            
                exprNode.unexpectedParseTreeNode();               
                return false;
            }
        }
        
    }
    
    /**
     * Determines the type of a top-level function.
     *
     * Creation date: (9/1/00 9:05:15 AM)
     * @return TypeExpr the type of the function
     * @param functionEnv environment to use for typing the function
     * @param nonGenericVars the list of variables to treat as non-generic during the typing
     * @param parseTree the parse tree for the function
     */
    private TypeExpr analyzeTopLevelFunction(final Env functionEnv, final NonGenericVars nonGenericVars, final ParseTreeNode parseTree) {

        parseTree.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);

        final ParseTreeNode optionalCALDocNode = parseTree.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        final ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);

        final ParseTreeNode functionNameNode = accessModifierNode.nextSibling();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
		final String functionName = functionNameNode.getText(); 

        final ParseTreeNode paramListNode = functionNameNode.nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

        // f x1 x2 .. xn = e
        // is typed as if it were a lambda of the form
        // f = \x1 -> (\x2 -> ... (e)...)
        // in other words, x1,...xn are added to the environment and the list of nonGenericVars while
        // typing e.

        final TypeExpr lambdaTypeExpr = analyzeLambda(functionEnv, nonGenericVars, paramListNode);

        final TypeExpr functionTypeExpr = functionEnv.retrieveEntity(functionName).getTypeExpr(nonGenericVars);

        //The simplest example where this final unification is necessary is:
        //g f x = g f (f x);
        //Then the type of g while typing the body of the lambda (i.e. functionType) is (a->b)->b->c
        //whilst lambdaType is (a->b)->a->c. So unification gives the right answer of (a->a)->a->c.

        try {        
            TypeExpr.unifyType(functionTypeExpr, lambdaTypeExpr, currentModuleTypeInfo);
        } catch (TypeException te) {
            //type of the function is not compatible with its defining expression
            //for example, this happens when typing
            //cat = isEmpty [cat + 1.0];
            compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Error.TypeOfFunctionNotCompatibleWithDefiningExpression(functionName), 
                te));
        }

        return lambdaTypeExpr;
    }
    
    /**
     * Adds the built-in primitive functions to the environment.
     * @param primitiveFunctionDeclarationNodes
     */
    private void calculatePrimitiveFunctionTypes(final List<ParseTreeNode> primitiveFunctionDeclarationNodes) {
        
        for (final ParseTreeNode primitiveFunctionNode : primitiveFunctionDeclarationNodes) {
                        
            primitiveFunctionNode.verifyType(CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION);
            
            final ParseTreeNode optionalCALDocNode = primitiveFunctionNode.firstChild();
            optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            final ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
            final Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);                 
        
            final ParseTreeNode typeDeclarationNode = accessModifierNode.nextSibling();                
            final TypeExpr typeExpr = calculateDeclaredType(typeDeclarationNode); 
            
            final ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
            functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
            final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();    
            final String functionName = functionNameNode.getText();                  
                                                                  
            final Function function = Function.makeTopLevelFunction(QualifiedName.make(currentModuleName, functionName), typeExpr, scope);
            function.setAsPrimitive();
                    
            //entity.setTypeCheckingDone();   
            
            functionEnv = Env.extend(functionEnv, function);                         
        }        
    }    
    
    /**
     * Adds the foreign function declarations to the environment.
     * Creation date: (April 29, 2002)
     * @param foreignFunctionDefnNodes
     */
    private void calculateForeignFunctionTypes(final List<ParseTreeNode> foreignFunctionDefnNodes) throws UnableToResolveForeignEntityException {
                
        final ForeignFunctionChecker foreignFunctionChecker = new ForeignFunctionChecker (compiler, currentModuleTypeInfo);
        functionEnv = foreignFunctionChecker.calculateForeignFunctionTypes(functionEnv, foreignFunctionDefnNodes);
    }
               
    /**
     * Determines the typeExpression defined by a parseTree describing a declaration.
     * Creation date: (12/8/00 10:46:25 AM)
     * @return TypeExpr
     * @param parseTree 
     * @param typeVarNameToTypeMap (String -> TypeExpr) this function populates this map, which should be empty in the initial call
     *       (unless some types are supplied by an enclosing context, as in the case of a class method and the type class type variable).
     * @param recordVarNameToRecordVarMap (String -> RecordVar) this function populates this map, which should be empty in the initial call.
     */
    private TypeExpr calculateDeclaredTypeExpr(
            final ParseTreeNode parseTree,
            final Map<String, TypeVar> typeVarNameToTypeMap,
            final Map<String, RecordVar> recordVarNameToRecordVarMap) {

        switch (parseTree.getType()) {
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR :
            {
                final ParseTreeNode domainNode = parseTree.firstChild();
                final TypeExpr domain = calculateDeclaredTypeExpr(domainNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                final TypeExpr codomain = calculateDeclaredTypeExpr(domainNode.nextSibling(), typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                return TypeExpr.makeFunType(domain, codomain);
            }

            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR :
            {
                final TypeExpr elementTypeExpr = calculateDeclaredTypeExpr(parseTree.firstChild(), typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                return typeConstants.makeListType(elementTypeExpr);
            }

            case CALTreeParserTokenTypes.TYPE_APPLICATION :
            {              
                if (parseTree.hasExactlyOneChild()) {
                    //not really an application node, but an artifact of parsing
                    return calculateDeclaredTypeExpr(parseTree.firstChild(), typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                }
                
                //we preferentially construct a TypeConsApp over a TypeApp where both are technically
                //valid representations of the type. This is mainly because type inference is a little
                //simpler (as well as more firmly tested) in the earlier case.
                
                final ParseTreeNode firstChildNode = parseTree.firstChild();
                if (firstChildNode.getType() == CALTreeParserTokenTypes.QUALIFIED_CONS) {
                  
                    TypeConstructor typeCons = dataDeclarationChecker.resolveTypeConsName(firstChildNode);                
                             
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
                             
                        args[argN] = calculateDeclaredTypeExpr(argNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                        
                        ++argN;
                    }
                                  
                    return new TypeConsApp(typeCons, args);                    
                } 
                                       
                TypeExpr partialApp = calculateDeclaredTypeExpr(firstChildNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                for (final ParseTreeNode argNode : firstChildNode.nextSiblings()) {
                    
                    partialApp = new TypeApp(partialApp, calculateDeclaredTypeExpr(argNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap));                        
                }
                
                return partialApp;                                   
            }

            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {               
                final TypeConstructor typeCons = dataDeclarationChecker.resolveTypeConsName(parseTree); 
                //note that we cannot assume that the type constructor is non-parametric 
                //i.e. this may be an understaturated application.
                return new TypeConsApp(typeCons, null);                
            }

            case CALTreeParserTokenTypes.VAR_ID :
            {
                final String typeVarName = parseTree.getText();
                if (typeVarNameToTypeMap.containsKey(typeVarName)) {                
                    return typeVarNameToTypeMap.get(typeVarName);
                }

                final TypeVar typeVar = new TypeVar(typeVarName);
                typeVarNameToTypeMap.put(typeVarName, typeVar);

                return typeVar;
            }
            
            case CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR :
            {                                   
                if (parseTree.hasNoChildren()) {
                    return typeConstants.getUnitType();
                }

                if (parseTree.hasExactlyOneChild()) {
                    // the type (t) is equivalent to the type t.
                    return calculateDeclaredTypeExpr(parseTree.firstChild(), typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                }

                final Map<FieldName, TypeExpr> fieldToTypeMap = new HashMap<FieldName, TypeExpr>();
                int componentN = 1;
                for (final ParseTreeNode componentNode : parseTree) {

                    final TypeExpr componentTypeExpr = calculateDeclaredTypeExpr(componentNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                    fieldToTypeMap.put(FieldName.makeOrdinalField(componentN), componentTypeExpr);
                    ++componentN;
                }

                return new RecordType(RecordVar.NO_FIELDS, fieldToTypeMap);
            }
                        
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR :
            {
                final ParseTreeNode recordVarNode = parseTree.firstChild();
                recordVarNode.verifyType(CALTreeParserTokenTypes.RECORD_VAR);
                
                final RecordVar recordVar;
                final ParseTreeNode recordVarNameNode = recordVarNode.firstChild();
                if (recordVarNameNode != null) {
                    
                    //a record-polymorphic record
                    
                    String recordVarName = recordVarNameNode.getText();                    
                    if (recordVarNameToRecordVarMap.containsKey(recordVarName)) {
                        recordVar = recordVarNameToRecordVarMap.get(recordVarName);
                    } else {
                        //create an unconstrained recordVar i.e. {r}
                        recordVar = RecordVar.makePolymorphicRecordVar(recordVarName); 
                        recordVarNameToRecordVarMap.put(recordVarName, recordVar);
                    }
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
                    final FieldName fieldName = getFieldName(fieldNameNode);                   
                    
                    final ParseTreeNode typeNode = fieldNameNode.nextSibling();
                    final TypeExpr type = calculateDeclaredTypeExpr(typeNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap);
                    extensionFieldsMap.put(fieldName, type);                   
                }
                               
                final RecordVar prunedRecordVar = recordVar.prune();
                if (!prunedRecordVar.isNoFields() &&
                    !prunedRecordVar.getLacksFieldsSet().containsAll(extensionFieldsMap.keySet())) {
                    
                    //Can't have extensions such as {r | field1 :: Int}. r must have a field1 lacks constraint.               
                    final Set<FieldName> missingLacksConstraints = new HashSet<FieldName>(extensionFieldsMap.keySet());
                    missingLacksConstraints.removeAll(prunedRecordVar.getLacksFieldsSet());
                    // TypeChecker: the extension fields {missingLacksConstraints} must be lacks constraints on the record variable.
                    compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Error.ExtensionFieldsLacksConstraintsOnRecordVariable(missingLacksConstraints.toString())));
                    return null;
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
     * Calculates the context determined by a given type declaration. 
     * The context specifies
     * <ol>
     *   <li> the type variables that are qualified by type class constraints.
     *   <li> the record variables that have lacks fields constraints.
     * </ol>
     * 
     * <p>
     * In a context, we must ensure that the type class constraints imposed by each type class on a particular type
     * variable are compatible. For example, (Functor a, Eq a) => ... should give a compilation error since the kind of
     * Functor is * -> * and the kind of Eq is *.
     *
     * @param contextListNode
     * @param typeVarNameToTypeMap (String -> TypeVar) map of the names of the type variables that appear in the type context to their TypeVar values.
     *    This map is empty on entry, and is populated by this method.        
     * @param typeVarNamesSet (String Set) the names of the type variables that occur in the type expression (not including the context)
     * @param recordVarNameToRecordVarMap (String -> RecordVar) map of the names of the record variables that appear in the type context to their RecordVar values.
     *    This map is empty on entry, and is populated by this method. 
     * @param recordVarNamesSet (String Set) the names of the record variables that occur in the type expression (not including the context)
     * @param classTypeVarNamesSet (Set of Strings) Empty except in the case of class methods. These are the additional 
     *     type variables scoped over this type arising from the class declaration. Note that the set has one element for 
     *     a single parameter type class.                            
     */
    private void calculateDeclaredTypeContext(
            final ParseTreeNode contextListNode,
            final Map<String, TypeVar> typeVarNameToTypeMap, final Set<String> typeVarNamesSet,
            final Map<String, RecordVar> recordVarNameToRecordVarMap, final Set<String> recordVarNamesSet,
            final Set<String> classTypeVarNamesSet) {
    
        contextListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON );
        
        //String -> (TypeClass SortedSet)
        final Map<String, SortedSet<TypeClass>> typeVarNameToConstraintSet = new HashMap<String, SortedSet<TypeClass>>();
        
        //String -> (TypeClass SortedSet)
        final Map<String, SortedSet<TypeClass>> recordVarNameToConstraintSet = new HashMap<String, SortedSet<TypeClass>>();
        
        //String -> (FieldName Set)
        final Map<String, Set<FieldName>> recordVarNameToLacksSet = new HashMap<String, Set<FieldName>>();
        
        //String -> TypeClass
        //map from a type var name to the lexically first class in the context that uses that type var. Used for error messages.
        //for example, for (Fuctor f, Eq a, Ord b, Foo a, Foo b, Monad f) this is [(a, Eq), (b, Ord), (f, Functor)]
        final Map<String, TypeClass> typeVarNameToFirstClassConstraint = new HashMap<String, TypeClass>();
                
        for (final ParseTreeNode contextNode : contextListNode) {
               
            switch (contextNode.getType())
            { 
                         
                case CALTreeParserTokenTypes.CLASS_CONTEXT :
                {
    
                    final ParseTreeNode typeClassNameNode = contextNode.firstChild();
                    typeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                    
                    //verify that the type class referred to actually exists, and supply its inferred module name
                    //if it was omitted in the user's CAL code.           
                    final TypeClass typeClass = typeClassChecker.resolveClassName(typeClassNameNode);                    
        
                    final ParseTreeNode varNameNode = typeClassNameNode.nextSibling();
                    varNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                    //could be a record var or a type var
                    final String varName = varNameNode.getText(); 
                    
                    if (classTypeVarNamesSet.contains(varName)) {
                        //"The class type variable {0} cannot be used in a class method context." 
                        compiler.logMessage (new CompilerMessage(varNameNode, new MessageKind.Error.ClassTypeVarInMethodContext(varName)));
                    }
                    
                    if (typeVarNamesSet.contains(varName)) {
                        SortedSet<TypeClass> constraintSet = typeVarNameToConstraintSet.get(varName);
                    
                        if (constraintSet == null) {
                            constraintSet = TypeClass.makeNewClassConstraintSet();
                            constraintSet.add(typeClass);
                            typeVarNameToConstraintSet.put(varName, constraintSet);
                            //the kind of the type var must be the same as the kind of any of its constraining type classes,
                            //in particular, that of the first. Log the first type class to report a potential kinding error 
                            //with later constraints
                            typeVarNameToFirstClassConstraint.put(varName, typeClass);                            
                        } else {
    
                            if (!constraintSet.add(typeClass)) {
                                //a repeated constraint such as 
                                //(Eq a, Eq a) => a -> a
                                //is allowed in Hugs, but is not allowed in CAL to be consistent with other restrictions
                                //on repeated declarations and definitions.
                                compiler.logMessage(new CompilerMessage(typeClassNameNode, new MessageKind.Error.RepeatedClassConstraintOnTypeVariable(typeClass.getName(), varName)));
                                    
                            }
                            
                            //check that the type class kinds for a given type variable all unify                            
                            
                            final TypeClass firstTypeClass = typeVarNameToFirstClassConstraint.get(varName);                               
                            try {                                                               
                                KindExpr.unifyKind(firstTypeClass.getKindExpr(), typeClass.getKindExpr());
                            } catch (TypeException typeException) {
                                //"The kinds of all classes constraining the type variable '{0}' must be the same. Class {1} has kind {2} while class {3} has kind {4}."
                                compiler.logMessage(new CompilerMessage(contextNode, 
                                    new MessageKind.Error.KindErrorInClassConstraints(
                                        varName,
                                        firstTypeClass,
                                        typeClass)));          
                            }
                        }                        
    
                    } else if (recordVarNamesSet.contains(varName)) {
                        SortedSet<TypeClass> constraintSet = recordVarNameToConstraintSet.get(varName);
                    
                        if (constraintSet == null) {
                            constraintSet = TypeClass.makeNewClassConstraintSet();
                            constraintSet.add(typeClass);
                            recordVarNameToConstraintSet.put(varName, constraintSet);
                        } else {
    
                            if (!constraintSet.add(typeClass)) {
                                //a repeated constraint such as 
                                //(Eq a, Eq a) => {a}
                                //is allowed in Hugs, but is not allowed in CAL to be consistent with other restrictions
                                //on repeated declarations and definitions.
                                compiler.logMessage(
                                    new CompilerMessage(
                                        typeClassNameNode,
                                        new MessageKind.Error.RepeatedClassConstraintOnRecordVariable(typeClass.getName().getQualifiedName(), varName)));
                            }
                        }                        
    
                    } else {
                                               
                        //The purpose is to signal declarations like
                        //(Eq a) => Int
                        //in which a constraint appears on an unused variable. This is a compilation error because it is not possible
                        //to resolve the overloading when this method is then used in an expression, so it would result in an error.
                        //Thus, we might as well give the error as soon as possible to avoid having people mess up.
                        
                        //varName could have been intended to be a record variable or a row variable. We can't tell.
                        //TypeChecker: variable {varName} is not used in the type signature.
                        compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.VariableNotUsedInTypeSignature(varName)));
                    }                                                                                               
    
                    break;                                      
                }
                
                case CALTreeParserTokenTypes.LACKS_FIELD_CONTEXT:
                {
                    final ParseTreeNode recordVarNameNode = contextNode.firstChild();
                    recordVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                    final String recordVarName = recordVarNameNode.getText();  
                    
                    //this error message is strictly speaking not needed since the following error will be triggered. However, it is
                    //added for clarity sake.
                    if (classTypeVarNamesSet.contains(recordVarName)) {
                        //"The class type variable {0} cannot be used in a class method context." 
                        compiler.logMessage (new CompilerMessage(recordVarNameNode, new MessageKind.Error.ClassTypeVarInMethodContext(recordVarName)));
                    }                    
        
                    if (!recordVarNamesSet.contains(recordVarName)) {
        
                        //cannot have constrained record vars that do not appear in the non-context type of the signature
                        //such as r\field1 => Maybe a      
                        compiler.logMessage(new CompilerMessage(recordVarNameNode, new MessageKind.Error.RecordVariableNotUsedInTypeSignature(recordVarName)));
                            
                    }
                    
                    final ParseTreeNode fieldNameNode = recordVarNameNode.nextSibling();
                    final FieldName fieldName = getFieldName(fieldNameNode);                    
                    
                    Set<FieldName> lacksSet = recordVarNameToLacksSet.get(recordVarName);
                    
                    if (lacksSet == null) {
                        lacksSet = new HashSet<FieldName>();
                        lacksSet.add(fieldName);
                        recordVarNameToLacksSet.put(recordVarName, lacksSet);
                    } else {
                        
                        if (!lacksSet.add(fieldName)) {
                            //can't duplicate a lacks constraint on the same record variable e.g. r\field1, r\field1
                            //on repeated declarations and definitions.
                            compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Error.RepeatedLacksFieldConstraintOnRecordVariable(fieldName.toString(), recordVarName)));
                        }                                              
                    }                    
                    
                    break;
                }
                
                default:
                {                    
                    contextNode.unexpectedParseTreeNode();                    
                    return;                    
                }
            }
        }
        
        //populate typeVarNameToTypeMap.
        for (final Map.Entry<String, SortedSet<TypeClass>> entry : typeVarNameToConstraintSet.entrySet()) {
                
            final String typeVarName = entry.getKey();
            final SortedSet<TypeClass> constraintSet = entry.getValue();
            
            //Declared contexts with redundant constraints because of class inheritance are allowed.
            //For example, in (Eq a, Ord a, Num a) => ... Eq and Ord are redundant. They are
            //eliminated from the internal representation of the type class constraint set.
            
            typeVarNameToTypeMap.put(typeVarName, TypeVar.makeTypeVar(typeVarName, constraintSet, true));
        }        
        
        //populate recordVarNameToRecordVarMap.
        for (final String recordVarName : recordVarNamesSet) {
                        
            Set<FieldName> lacksSet = recordVarNameToLacksSet.get(recordVarName);
            if (lacksSet == null) {
                lacksSet = RecordVar.NO_LACKS_FIELDS;
            }
            SortedSet<TypeClass> typeClassConstraintSet = recordVarNameToConstraintSet.get(recordVarName);
            if (typeClassConstraintSet == null) {
                typeClassConstraintSet = TypeClass.NO_CLASS_CONSTRAINTS;
            }
            final RecordVar recordVar = RecordVar.makeRecordVar(recordVarName, lacksSet, typeClassConstraintSet, true);
            recordVarNameToRecordVarMap.put(recordVarName, recordVar);
        }
    }
    
    /**
     * Generates a type expression for each top-level type declaration in the CAL program.
     * Creation date: (12/8/00 10:21:34 AM)
     * @param functionTypeDeclarationNodes 
     */
    private void calculateDeclaredTypes(final List<ParseTreeNode> functionTypeDeclarationNodes) {

       for (final ParseTreeNode topLevelTypeDeclarationNode : functionTypeDeclarationNodes) {
            
            topLevelTypeDeclarationNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION);
            
            final ParseTreeNode optionalCALDocNode = topLevelTypeDeclarationNode.firstChild();
            optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            final ParseTreeNode typeDeclarationNode = optionalCALDocNode.nextSibling();
            
            typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                      
            final ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
            functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
          
            final TypeExpr typeExpr = calculateDeclaredType(typeDeclarationNode);            
            functionNameToDeclaredTypeMap.put(functionNameNode.getText(), typeExpr);            
        }
    }
    
    /**
     * Generates a type expression for an individual type declaration.
     * Creation date: (April 29, 2002)
     * @param typeDeclarationNode 
     * @return TypeExpr
     */
    TypeExpr calculateDeclaredType(final ParseTreeNode typeDeclarationNode) {

        typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
              
        final ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        final ParseTreeNode typeSignatureNode = functionNameNode.nextSibling();
        
        return calculateTypeFromSignature(typeSignatureNode, functionNameNode.getText());            
    }
    
    /**
     * A helper function for computing the type from a textual representation of a type signature.
     * @param typeSignatureNode
     * @param functionName name of the function that this type signature is a signature of, or null
     *          if it is not a signature of a function. Used for error reporting only.
     * @return TypeExpr built up out of the typeSignatureNode
     */
    private TypeExpr calculateTypeFromSignature(final ParseTreeNode typeSignatureNode, final String functionName) {
        
        typeSignatureNode.verifyType(CALTreeParserTokenTypes.TYPE_SIGNATURE);

        final ParseTreeNode contextListNode = typeSignatureNode.firstChild();
        contextListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON);
        final ParseTreeNode typeNode = contextListNode.nextSibling();

        final Set<String> typeVarNamesSet = new HashSet<String>(); 
        final Set<String> recordVarNamesSet = new HashSet<String>();        
        calculateFreeVariablesInDeclaredType(typeNode, typeVarNamesSet, recordVarNamesSet, Collections.<String>emptySet());        

        final Map<String, TypeVar> typeVarNameToTypeMap = new HashMap<String, TypeVar>();       
        final Map<String, RecordVar> recordVarNameToRecordVarMap = new HashMap<String, RecordVar>();

        calculateDeclaredTypeContext(contextListNode,
            typeVarNameToTypeMap, typeVarNamesSet,
            recordVarNameToRecordVarMap, recordVarNamesSet,
            Collections.<String>emptySet());

        final TypeExpr typeExpr = calculateDeclaredTypeExpr(typeNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap);
        if (typeExpr == null) {
            return null;
        }

        //kind check the type declaration.
        
        final Map<TypeVar, KindExpr> typeVarToKindExprMap = getInitialTypeVarToKindMap(typeVarNameToTypeMap);
                    
        try {
            
            dataDeclarationChecker.kindCheckTypeExpr(typeVarToKindExprMap, typeExpr);
            
        } catch (TypeException typeException) {
            
            final MessageKind errorMessage;
            if (functionName != null) {
                // TypeChecker: Kind error in the type declaration for the function {functionName}.
                errorMessage = new MessageKind.Error.KindErrorInTypeDeclarationForFunction(functionName);
            } else {
                // TypeChecker: Kind error in the type signature.
                errorMessage = new MessageKind.Error.KindErrorInTypeSignature();
            }
                     
            compiler.logMessage(new CompilerMessage(typeNode, errorMessage, typeException));    
        }

        return typeExpr;        
    }  
    
    /**
     * In a context, we must ensure that the type class constraints imposed by each type class on a particular type
     * variable are compatible. For example, (Functor a, Eq a) => ... should give a compilation error since the kind of
     * Functor is * -> * and the kind of Eq is *. This compilation error should be reported in an earlier call to
     * calculateDeclaredTypeContext. This function gathers up the implied (TypeVar -> KindExpr) map from the context.
     *  
     * @param typeVarNameToTypeMap (String -> TypeVar)
     * @return (TypeVar -> KindExpr) map from type variables, to its corresponding kind as determined by the context.
     */
    private Map<TypeVar, KindExpr> getInitialTypeVarToKindMap(final Map<String, TypeVar> typeVarNameToTypeMap) {
                           
        final Map<TypeVar, KindExpr> typeVarToKindExprMap = new HashMap<TypeVar, KindExpr>();   
        for (final Map.Entry<String, TypeVar> entry : typeVarNameToTypeMap.entrySet()) {
                                  
            final TypeVar typeVar = entry.getValue();
            
            final KindExpr kindExpr;
            if (typeVar.noClassConstraints()) {
                kindExpr = new KindExpr.KindVar();
            } else {
                
                //if the constraints on the type variable a are (C1 a, ..., Cn a), then
                //we already know that they must all unify, thus we can just pick the first.
                kindExpr = (typeVar.getTypeClassConstraintSet().first()).getKindExpr();                                        
            }
                                  
            typeVarToKindExprMap.put(typeVar, kindExpr);
        }
        
        return typeVarToKindExprMap;
    }
    
    /**
     * Calculates the type of a class method, in the meantime doing some static checks which are logged as
     * compiler messages.  
     * <ol>  
     *   <li> the classTypeVarName must actually appear in the type signature of the class method.
     *   <li> the context of any class method must not involve classTypeVarName
     *   <li> classTypeVarName must not be used as a record variable.
     * </ol>
     * 
     * @param classMethodNode
     * @param classTypeVarName For example, if this is a method in the declaration of the Ord a class, this is the variable "a".
     * @param classTypeVarType The classTypeVarName, properly constrained i.e. Ord a => a.
     * @param classTypeVarKindExpr the kind of the class type variable. It will be modified via kind inference as a 
     *              side effect of this method.
     * @return TypeExpr
     */
    TypeExpr calculateDeclaredClassMethodType(final ParseTreeNode classMethodNode,
            final String classTypeVarName,
            final TypeVar classTypeVarType,
            final KindExpr classTypeVarKindExpr) {

        classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);
        
        final ParseTreeNode classMethodNameNode = classMethodNode.getChild(2);
        classMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        final String classMethodName = classMethodNameNode.getText();
        
        final ParseTreeNode typeSignatureNode = classMethodNameNode.nextSibling();
        typeSignatureNode.verifyType(CALTreeParserTokenTypes.TYPE_SIGNATURE);
                                                                               
        final ParseTreeNode contextListNode = typeSignatureNode.firstChild();
        contextListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON);                                    
        final ParseTreeNode typeNode = contextListNode.nextSibling();
        
        final Set<String> typeVarNamesSet = new HashSet<String>(); 
        final Set<String> recordVarNamesSet = new HashSet<String>();
        final Set<String> classTypeVarNamesSet = new HashSet<String>(); //names of the type class type variables.
        classTypeVarNamesSet.add(classTypeVarName);
        calculateFreeVariablesInDeclaredType(typeNode, typeVarNamesSet, recordVarNamesSet, classTypeVarNamesSet);   
        
        //the classTypeVarNamesSet must contain classTypeVarName.
        //This is because otherwise the class method could never be used without an ambiguous overloading
        //type error.
        if (!typeVarNamesSet.contains(classTypeVarName)) {
            compiler.logMessage(new CompilerMessage(classMethodNode,
                new MessageKind.Error.ClassMethodMustUseClassTypeVariable(classMethodName, classTypeVarName)));
        }              
       
        final Map<String, TypeVar> typeVarNameToTypeMap = new HashMap<String, TypeVar>();
        typeVarNameToTypeMap.put(classTypeVarName, classTypeVarType);
        
        final Map<String, RecordVar> recordVarNameToRecordVarMap = new HashMap<String, RecordVar>();
        
        calculateDeclaredTypeContext(contextListNode,
            typeVarNameToTypeMap, typeVarNamesSet,
            recordVarNameToRecordVarMap, recordVarNamesSet, classTypeVarNamesSet);        
    
        final TypeExpr typeExpr = calculateDeclaredTypeExpr(typeNode, typeVarNameToTypeMap, recordVarNameToRecordVarMap);        

        //kind check the type.
        
        final Map<TypeVar, KindExpr> typeVarToKindExprMap = getInitialTypeVarToKindMap(typeVarNameToTypeMap);           
        typeVarToKindExprMap.put(classTypeVarType, classTypeVarKindExpr);

        try {
            dataDeclarationChecker.kindCheckTypeExpr(typeVarToKindExprMap, typeExpr);
        } catch (TypeException typeException) {            
            // TypeChecker: Kind error in the type declaration for the class method {0}.
            compiler.logMessage(
                new CompilerMessage(
                    classMethodNode,
                    new MessageKind.Error.KindErrorInTypeDeclarationForClassMethod(classMethodName),
                    typeException));
        }

        return typeExpr;                    
    }       
    
    /**
     * Finds the names of all the type and record variables occurring in a type declaration (without the context).
     * 
     * <p>
     * For example, in [a] -> (b, a), it would return typeVariables = {a, b}, recordVariables = {}.
     * In ({r | field1 :: [a] -> [a]}, {s | }) it would return typeVariables = {a}, recordVariables = {r, s}.
     * 
     * <p>
     * This method also does some first pass static analysis of type declarations:
     * <ol>    
     *   <li> verify that field names in a record are all distinct
     *   <li> verify that no record variable is used as a type variable and vice-versa
     *   <li> verify that none of the record variables occurs in typeClassTypeVariables
     * </ol>
     * 
     * <p>
     * The check that each of the typeClassTypeVariables actually does occur in typeVarNamesSet is done by the caller
     * of this method to provide a better context.
     *
     * <p>
     * Creation date: (3/27/01 2:07:32 PM)
     * @param typeNode 
     * @param typeVarNamesSet (Set of Strings) empty when called, and populated by this method
     * @param recordVarNamesSet (Set of Strings) empty when called, and populated by this method
     * @param classTypeVarNamesSet (Set of Strings) Empty except in the case of class methods. These are the additional 
     *     type variables scoped over this type arising from the class declaration. Note that the set has one element for 
     *     a single parameter type class.                       
     */
    private void calculateFreeVariablesInDeclaredType(final ParseTreeNode typeNode,
        final Set<String> typeVarNamesSet,
        final Set<String> recordVarNamesSet,
        final Set<String> classTypeVarNamesSet) {
              
        switch (typeNode.getType()) {
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR :
            case CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR :
            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR :
            case CALTreeParserTokenTypes.TYPE_APPLICATION :
            {                               
                for (final ParseTreeNode childNode : typeNode) {

                    calculateFreeVariablesInDeclaredType(childNode, typeVarNamesSet, recordVarNamesSet, classTypeVarNamesSet);
                }

                return;
            }

            case CALTreeParserTokenTypes.QUALIFIED_CONS :
                return;

            case CALTreeParserTokenTypes.VAR_ID :
            {
                final String typeVarName = typeNode.getText();
                typeVarNamesSet.add(typeVarName);
                
                if (recordVarNamesSet.contains(typeVarName)) {
                    // The type variable {typeVarName} is already used as a record variable.
                    compiler.logMessage(new CompilerMessage(typeNode, new MessageKind.Error.TypeVariableAlreadyUsedAsRecordVariable(typeVarName)));
                }

                return;
            }
            
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR:
            {
                final ParseTreeNode recordVarNode = typeNode.firstChild();
                recordVarNode.verifyType(CALTreeParserTokenTypes.RECORD_VAR);
                
                final ParseTreeNode recordVarNameNode = recordVarNode.firstChild();
                if (recordVarNameNode != null) {
                    recordVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                    final String recordVarName = recordVarNameNode.getText();
                    recordVarNamesSet.add(recordVarName);
                    if (typeVarNamesSet.contains(recordVarName) ||
                        classTypeVarNamesSet.contains(recordVarName)) {
                        // The record variable {recordVarName} is already used as a type variable.
                        compiler.logMessage(new CompilerMessage(recordVarNameNode, new MessageKind.Error.RecordVariableAlreadyUsedAsTypeVariable(recordVarName)));
                    }
                }                              
                
                final ParseTreeNode fieldTypeAssignmentListNode = typeNode.getChild(1);
                fieldTypeAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT_LIST);
                
                final Set<FieldName> fieldNamesSet = new HashSet<FieldName>();
                
                for (final ParseTreeNode fieldTypeAssignmentNode : fieldTypeAssignmentListNode) {
                         
                    fieldTypeAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT);
                    
                    final ParseTreeNode fieldNameNode = fieldTypeAssignmentNode.firstChild();
                    final FieldName fieldName = getFieldName(fieldNameNode);
                    if (!fieldNamesSet.add(fieldName)) {
                        // repeated occurrence of field name {fieldName} in record type.
                        compiler.logMessage(new CompilerMessage(fieldTypeAssignmentNode, new MessageKind.Error.RepeatedOccurrenceOfFieldNameInRecordType(fieldName.toString())));
                    }
                    
                    final ParseTreeNode assignedTypeNode = fieldNameNode.nextSibling();
                    
                    calculateFreeVariablesInDeclaredType(assignedTypeNode, typeVarNamesSet, recordVarNamesSet, classTypeVarNamesSet);                                      
                }
                return;                
            }

            default : 
            { 
            	typeNode.unexpectedParseTreeNode();                          
                return;
            }
        }         
    }
    
    /**
     * Given a type signature, return the type expression that it represents.
     * @param typeSignatureNode the first ParseTreeNode in a parsed type signature.  
     *   Its type should be a context list (and its sibling a declaration node).
     * @param workingModule the module in which the type exists.
     * @return TypeExpr the type expression it represents, or null if invalid.
     */
    private TypeExpr getTypeFromSignature(final ParseTreeNode typeSignatureNode, final ModuleName workingModule) {
        
        final CompilerMessageLogger oldLogger = compiler.getMessageLogger();
        compiler.setCompilerMessageLogger(null);
        
        changeModule(workingModule);
        
        compiler.setCompilerMessageLogger(oldLogger);
        
        return calculateTypeFromSignature(typeSignatureNode, null);                     
    }       

    /**
     * Get a TypeExpr from its string representation.
     * Note: any compiler messages held by the compiler will be lost.
     * 
     * @param typeString the string representation.
     * @param workingModule the module in which the type exists.
     * @return TypeExpr a TypeExpr representation of the string, or null if the string does not represent a valid type.
     */
    TypeExpr getTypeFromString(final String typeString, final ModuleName workingModule) {

        // Temporarily replace the logger.
        final CompilerMessageLogger oldLogger = compiler.getMessageLogger();
        final CompilerMessageLogger checkLogger = new MessageLogger(true);
        compiler.setCompilerMessageLogger(checkLogger);
         
        final java.io.Reader stringReader = new java.io.StringReader(typeString);

        final CALParser parser = freshParser(compiler, stringReader);
        final CALTreeParser treeParser = new CALTreeParser(compiler);

        // Parse and type check and catch any fatal errors
        try {
            try {

                // Call the parser to parse a type declaration
                parser.startTypeSignature();
    
                // Walk the parse tree as a sanity check on the generated AST and of the tree parser
                final ParseTreeNode typeSignatureNode = (ParseTreeNode)parser.getAST();
                treeParser.startTypeSignature(typeSignatureNode);
                
                final TypeExpr typeExpr = getTypeFromSignature(typeSignatureNode, workingModule);
                
            
                //deep prune so that instantiated type variables are not part of the returned TypeExpr.
                //this has the effect of chosing a deterministic element in the equivalence class of
                //representations of this TypeExpr.
                return typeExpr.deepPrune();                                       
               
            } catch (antlr.RecognitionException e) {
                // syntax error
                final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));
                
            } catch (antlr.TokenStreamException e) {
                // Bad token stream
                compiler.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));

            } catch (Exception e) {
                
                final int errorCount = compiler.getMessageLogger().getNErrors();
                if (errorCount > 0 || e instanceof UnableToResolveForeignEntityException) {

                    // If the exception is an UnableToResolveForeignEntityException, there is
                    // a CompilerMessage inside that we should be logging.
                    if (e instanceof UnableToResolveForeignEntityException) {
                        try {
                            compiler.logMessage(((UnableToResolveForeignEntityException)e).getCompilerMessage());
                        } catch (AbortCompilation ace) {
                            //logMessage can throw a AbortCompilation if a FATAL message was sent.
                        }
                    }
                    
                    //if an error occurred previously, we continue to compile the program to try to report additional
                    //meaningful compilation errors. However, this can produce spurious exceptions related to the fact
                    //that the program state does not satisfy preconditions because of the initial error(s). We don't
                    //report the spurious exception as an internal coding error.
                    compiler.logMessage(new CompilerMessage(new MessageKind.Info.UnableToRecover()));
                } else {                               
                    compiler.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                }                                                           
            }

        } catch (AbortCompilation e) {
            // Compilation aborted.
    
        } finally {
            // replace the old logger.
            compiler.setCompilerMessageLogger(oldLogger);
            
            oldLogger.logMessages(checkLogger);
        }

        //the failure case- be sure to return null
        return null;
    }

    /**
     * Type checks an adjunct. 
     *
     * @param outerDefnListNode an outer defn list node containining the root parse tree node of the function adjunct
     * @param moduleName the name of the module in which the adjunct should be considered to be defined.
     */
    void checkAdjunct(final ParseTreeNode outerDefnListNode, final ModuleName moduleName) throws UnableToResolveForeignEntityException {
        checkAdjunct(outerDefnListNode, moduleName, null);
    }
    
    /**
     * Type checks an adjunct and returns information about the specified function. 
     *
     * @param outerDefnListNode an outer defn list node containining the root parse tree node of the function adjunct
     * @param moduleName the name of the module in which the adjunct should be considered to be defined.
     * @param targetName
     * @return information about the named target.
     */
    private AdjunctInfo checkAdjunct(final ParseTreeNode outerDefnListNode, final ModuleName moduleName, final String targetName) throws UnableToResolveForeignEntityException {
        // Switch the environment to the target module.
        changeModule(moduleName);
        
        TypeExpr targetType = null;
        boolean explicitTargetType = false;
        
        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
        
        final ParseTreeNode moduleDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.MODULE_DEFN, "MODULE_DEFN");
        final ParseTreeNode optionalCALDocNode = new ParseTreeNode(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT, "OPTIONAL_CALDOC_COMMENT");
        final ParseTreeNode moduleNameNode = ModuleNameUtilities.makeParseTreeForModuleName(moduleName);
        final ParseTreeNode importListNode = new ParseTreeNode(CALTreeParserTokenTypes.IMPORT_DECLARATION_LIST, "IMPORT_DECLARATION_LIST");
        final ParseTreeNode friendDeclarationListNode = new ParseTreeNode(CALTreeParserTokenTypes.FRIEND_DECLARATION_LIST, "FRIEND_DECLARATION_LIST");      
        
        moduleDefnNode.addChildren(new ParseTreeNode[]{optionalCALDocNode, moduleNameNode, importListNode, friendDeclarationListNode, outerDefnListNode});
        
        //Check the top level declarations in the module
                
        ModuleLevelParseTrees moduleLevelParseTrees = new ModuleLevelParseTrees(moduleDefnNode, outerDefnListNode);

        //Checks that the top-level functions used in the program are defined only once, and that
        //each top-level function has at most one type declaration. 
        checkNamesUsedForTopLevelFunctions(moduleLevelParseTrees);

        //calculate the type expressions determined by each top-level function type declaration
        calculateDeclaredTypes(moduleLevelParseTrees.getFunctionTypeDeclarationNodes());
        
        // Check to see if the target SC has an explicitly declared type.
        if (targetName != null) {
            targetType = functionNameToDeclaredTypeMap.get(targetName); 
            if (targetType != null) {
                explicitTargetType = true;
            }
        }
                          
        //add the instances that are defined in this module to the currentModuleTypeInfo
        //the instance methods themselves need further checking latter.      
        ClassInstanceChecker instanceChecker = new ClassInstanceChecker(compiler, currentModuleTypeInfo, typeClassChecker, dataDeclarationChecker);
        instanceChecker.checkClassInstanceDefinitions(moduleLevelParseTrees);
                            
        //add the foreign functions in the current module to the environment
        calculateForeignFunctionTypes(moduleLevelParseTrees.getForeignFunctionDefnNodes());

        //Reorder the top-level functions defined in the program so that dependees are checked before dependents.
        final Graph<String> g = performFunctionDependencyAnalysis();

        //Used to augment the parse tree of a module with hidden overloading arguments,
        //and to add hidden dictionary functions.
        final OverloadingResolver overloadingResolver = new OverloadingResolver(compiler, currentModuleTypeInfo);

        //Type check the top-level functions. Proceed one strongly connected component at a time.
        for (int i = 0, nComponents = g.getNStronglyConnectedComponents(); i < nComponents; ++i) {

            final Graph<String>.Component component = g.getStronglyConnectedComponent(i);

            if (targetType == null) {
                targetType = checkComponent(component, targetName);
            } else {
                checkComponent(component);
            }       

            //now add dictionary arguments to the function defined in this declaration group, and fix
            //up applications within the function definitions.
            overloadingResolver.resolveOverloading(overloadingInfoList);

            //Free up memory associated with this potentially bulky object
            overloadingInfoList.clear();
        } 
        
        instanceChecker.checkTypesOfInstanceMethodResolvingFunctions();        
        
        //Do the lambda lifting       
        final FreeVariableFinder finder = new FreeVariableFinder(compiler, topLevelFunctionNameSet, currentModuleTypeInfo);

        final LambdaLifter lifter = new LambdaLifter(compiler, finder, moduleName);
        lifter.lift(outerDefnListNode);

        //Add the hidden class method, dictionary and dictionary switching functions
        //necessary to resolve overloading.
        overloadingResolver.addOverloadingHelperFunctions(outerDefnListNode);

        return new AdjunctInfo(targetName, targetType == null ? null : targetType.deepPrune(), explicitTargetType);
    }
    
    AdjunctInfo checkAdjunct(final AdjunctSource adjunctSource, final ModuleName moduleName, final String targetName) {
         
        // Temporarily replace the logger.
        final CompilerMessageLogger oldLogger = compiler.getMessageLogger();
        final CompilerMessageLogger checkLogger = new MessageLogger(true);  // Internal compiler logger.
        compiler.setCompilerMessageLogger(checkLogger);
         
        // Parse and type check and catch any fatal errors
        try {
            try {

                final ParseTreeNode outerDefnNode;
                final CALTreeParser treeParser = new CALTreeParser(compiler);

                if (adjunctSource instanceof AdjunctSource.FromText) {

                    final CALParser parser = freshParser(compiler, ((AdjunctSource.FromText)adjunctSource).getReader());

                    // Call the parser to parse an adjunct.
                    parser.adjunct();

                    outerDefnNode = (ParseTreeNode)parser.getAST();

                } else if (adjunctSource instanceof AdjunctSource.FromSourceModel) {

                    outerDefnNode = ((AdjunctSource.FromSourceModel)adjunctSource).toParseTreeNode();

                } else {
                    throw new IllegalArgumentException(
                            "CALTypeChecker.checkAdjunct - cannot handle adjunct source of type " + adjunctSource.getClass());
                }

                // Walk the parse tree as a sanity check on the generated AST and of the tree parser
                treeParser.adjunct(outerDefnNode);

                outerDefnNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);

                final AdjunctInfo ai = checkAdjunct (outerDefnNode, moduleName, targetName);
                return ai;           
            
            } catch (antlr.RecognitionException e) {
                // syntax error
                final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));

            } catch (antlr.TokenStreamException e) {
                // Bad token stream
                compiler.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));
            }
        
        } catch (AbortCompilation e) {
            // Compilation abort, we catch and continue
        
        } catch (Exception e) {
            // Major failure - internal coding error
            try {
                final int errorCount = compiler.getMessageLogger().getNErrors();
                if (errorCount > 0 || e instanceof UnableToResolveForeignEntityException) {

                    // If the exception is an UnableToResolveForeignEntityException, there is
                    // a CompilerMessage inside that we should be logging.
                    if (e instanceof UnableToResolveForeignEntityException) {
                        try {
                            compiler.logMessage(((UnableToResolveForeignEntityException)e).getCompilerMessage());
                        } catch (AbortCompilation ace) {
                            //logMessage can throw a AbortCompilation if a FATAL message was sent.
                        }
                    }
                    
                    //if an error occurred previously, we continue to compile the program to try to report additional
                    //meaningful compilation errors. However, this can produce spurious exceptions related to the fact
                    //that the program state does not satisfy preconditions because of the initial error(s). We don't
                    //report the spurious exception as an internal coding error.
                    compiler.logMessage(new CompilerMessage(new MessageKind.Info.UnableToRecover()));
                } else {                               
                    compiler.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                }                              
            } catch (AbortCompilation ace) {
                // Yeah, yeah, we know
            }
        } finally {
            // replace the old logger.
            compiler.setCompilerMessageLogger(oldLogger);
            
            try {
                oldLogger.logMessages(checkLogger);
            } catch (AbortCompilation e) {
                // Too many errors for the ol' logger.
            }
        }

        //the failure case- be sure to return null
        return null;
    }
    
    /**
     * Type check a strongly connected component representing a group of functions.
     * Creation date: (4/17/01 10:05:49 AM)
     * @param component
     */
    private void checkComponent(final Graph<String>.Component component) {
        checkComponent (component, null);
    }
    
    /**
     * Type check a strongly connected component representing a group of functions and
     * return the type of the specified function.
     * Creation date: (4/17/01 10:05:49 AM)
     * @param component
     * @param functionOfInterest
     * @return the type of the specified function
     */
    private TypeExpr checkComponent(final Graph<String>.Component component, final String functionOfInterest) {

        //Previously components of size 1 that did depend upon themselves were treated
        //analogously to let declarations in the Core language, and all other cases were
        //treated analogously to letrecs. It is no longer necessary to do this, and we can
        //just treat the letrec case.   

        TypeExpr returnVal = null;
        NonGenericVars nonGenericVars = null;

        // Add all the function names in this component to the environment and
        // nonGenericVars while typing the component. They are removed from the nonGenericVars
        // when typing the next component.  

        final int componentSize = component.size();

        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();

        for (int i = 0; i < componentSize; ++i) {

            final String functionName = component.getVertex(i).getName();
            
            final TypeVar newTypeVar = new TypeVar();

            final ParseTreeNode functionNode = functionNameToDefinitionNodeMap.get(functionName);
            final ParseTreeNode optionalCALDocNode = functionNode.firstChild();
            optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            final ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
            final Scope scope = getScopeModifier(accessModifierNode);

            final List<String> namedArgumentsList = new ArrayList<String>();
            final ParseTreeNode paramListNode = accessModifierNode.nextSibling().nextSibling();
            paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
            for (final ParseTreeNode varNode : paramListNode) {

                varNode.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);
                final String varName = FreeVariableFinder.getDisplayName(varNode.getText());
                namedArgumentsList.add(varName);
            }
            final String[] namedArguments = namedArgumentsList.toArray(new String[0]);

            final Function function = new Function(QualifiedName.make(currentModuleName, functionName), scope, namedArguments, newTypeVar, FunctionalAgent.Form.FUNCTION, nestingLevel);
            functionEnv = Env.extend(functionEnv, function);
            nonGenericVars = NonGenericVars.extend(nonGenericVars, newTypeVar);  
            functionBoundNamesToTypeMap.put(functionName, newTypeVar);

            overloadingInfoList.add(new OverloadingInfo(function, functionNode, newTypeVar, null));
        }

        // Type check the component.
        
        // We store a list of the type expressions that are obtained for each top level function
        // in the first phase, to be checked against the corresponding type declaration in the second phase.
        //
        // We need to do the checking in two phases because when the inferred type and its corresponding declared type
        // are unified in the first phase, the inferred type may not be the ultimate specific type of the function (as it
        // may be further restricted by the types of subsequent functions in the component) and thus not all type errors are
        // detectable then. The second phase, with its second loop, is able to handle the situation as the inferred types should all
        // have been restricted to their final form.
        //
        // An example of such a component is:
        //
        // gt :: (Prelude.Num a) => a -> a -> Boolean;
        // gt x y =
        //     let
        //         foo = gt3 0 1;
        //     in
        //         x > y;
        //
        // gt2 :: Int -> Int -> Boolean;
        // gt2 = gt;
        //
        // gt3 = gt2;
        //
        // Here, the local definition of foo in gt constrains gt to have a type of Int -> Int -> Boolean (since it uses gt3
        // which is aliased to gt2, which in turn is aliased to gt, but constraining both arguments to type Int). Thus the
        // type declaration for gt is too general, but cannot be detected in the first phase as gt2 has not been type checked
        // (and more importantly has had its inferred type unified with its type declaration).
        //
        final List<TypeExpr> typeExprs = new ArrayList<TypeExpr>();
        
        if (DEBUG_INFO) {
            System.out.println("Checking component -- Phase 1 - type inference:");
        }
        
        for (int i = 0; i < componentSize; ++i) {

            // lookup parsetree corresponding to the function with name functionName
            final String functionName = component.getVertex(i).getName();
            final ParseTreeNode functionNode = functionNameToDefinitionNodeMap.get(functionName);
           
            currentOverloadingInfo = overloadingInfoList.get(i);            
           
            final TypeExpr typeExpr = analyzeTopLevelFunction(functionEnv, nonGenericVars, functionNode);

            // Even though we are not doing the checking of the type declarations in this phase, we still need
            // to unified the inferred type with the type declaration, since it may constrain the function to a
            // more specific type than the one inferred.
            patternMatch(functionName, typeExpr);

            if (DEBUG_INFO) {
                //this is a copy of the inferred type expr for debugging purposes only
                final TypeExpr inferredTypeExpr = DEBUG_INFO ? CopyEnv.freshType(typeExpr, null) : null;
                dumpTypingInfo(functionName, typeExpr, inferredTypeExpr);
            }
            
            // Add the type expression as one to be checked in the second phase.
            typeExprs.add(typeExpr);
        }
        
        if (DEBUG_INFO) {
            System.out.println("Checking component -- Phase 2 - checking type declarations:");
        }
        
        for (int i = 0; i < componentSize; ++i) {

            // lookup parsetree corresponding to the function with name functionName
            final String functionName = component.getVertex(i).getName();
            
            final TypeExpr typeExpr = typeExprs.get(i);

            //this is a copy of the inferred type expr for debugging purposes only
            final TypeExpr inferredTypeExpr = DEBUG_INFO ? CopyEnv.freshType(typeExpr, null) : null;

            // See that the inferred type is compatible with the declared type, if a type declaration is provided.
            if (componentSize > 1) {
                // optimization: we do not need to do the second pattern match when the componentSize is 1
                // because the type unification in the first phase is sufficient for checking the declared type
                // against the (final) inferred type.
                
                patternMatch(functionName, typeExpr);
            }

            if (DEBUG_INFO) {
                dumpTypingInfo(functionName, typeExpr, inferredTypeExpr);
            }
            
                
            if (functionOfInterest != null && functionName.equals(functionOfInterest)) {
                returnVal = typeExpr;
            }
            
            inlineErrorCallsWithSourcePosition(currentModuleName, functionName);
        }

        //the types of the functions in this component (declaration group) are now free to be generalized.
        functionEnv.finishedTypeChecking(componentSize);    
        
        for (int i = 0; i < componentSize; ++i) {         
            final OverloadingInfo oi = overloadingInfoList.get(i);
            oi.finishedTypeCheckingFunction(null);                     
        }
        
        return returnVal;
    }
    
    /**
     * For each application in the errorCallList 
     * 
     * 1. If the call is an error call, set the ErrorInfo on the error call node.
     * 2. If the call is an assert call, inline the assert body and set the ErrorInfo on the error call node.
     * 3. If the call is an undefined call, inline the undefined body and set the ErrorInfo on the error call node.
     * 
     * @param moduleName The name of the current module.
     * @param topLevelFunctionName The name of the function being processed.
     */
    
    void inlineErrorCallsWithSourcePosition(final ModuleName moduleName, final String topLevelFunctionName){
        for(final ParseTreeNode errorCallNode : errorCallList){
                                  
            switch(errorCallNode.getType()){
            case CALTreeParserTokenTypes.QUALIFIED_VAR:
            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD: 
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE: 
            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE:
            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE:
            {               
                final SourcePosition sourcePosition;
                
                if (errorCallNode.getType() == CALTreeParserTokenTypes.QUALIFIED_VAR){
                    final ParseTreeNode nameNode = errorCallNode.getChild(1);
                    sourcePosition = nameNode.getSourcePosition();
                }
                else{
                    sourcePosition = errorCallNode.getSourcePosition();
                }
                
                final ErrorInfo errorInfo;
                if (sourcePosition == null){
                    errorInfo = new ErrorInfo(QualifiedName.make(moduleName, topLevelFunctionName), 0, 0);
                }
                else{
                    errorInfo = new ErrorInfo(QualifiedName.make(moduleName, topLevelFunctionName), sourcePosition.getLine(), sourcePosition.getColumn());
                }
                
                errorCallNode.setErrorInfoForErrorCall(errorInfo);
            }
            break;
            
            default:
            {            
                final ParseTreeNode firstChildNode = errorCallNode.getChild(0);
                final ParseTreeNode moduleNode = firstChildNode.getChild(0);
                final ParseTreeNode nameNode = firstChildNode.getChild(1);
                
                final SourcePosition sourcePosition = nameNode.getSourcePosition();
                final ErrorInfo errorInfo;
                if (sourcePosition == null){
                    errorInfo = new ErrorInfo(QualifiedName.make(moduleName, topLevelFunctionName), 0, 0);
                }
                else{
                    errorInfo = new ErrorInfo(QualifiedName.make(moduleName, topLevelFunctionName), sourcePosition.getLine(), sourcePosition.getColumn());
                }
                
                // source position can be null for internally generated functions such
                // as those arrising from the use of the deriving clause
                if (ModuleNameUtilities.getModuleNameFromParseTree(moduleNode).equals(CAL_Prelude.MODULE_NAME)){
                    if (nameNode.getText().equals(CAL_Prelude.Functions.assert_.getUnqualifiedName()) && errorCallNode.getNumberOfChildren() == 2){
                        addSourcePositionTo_assert(errorCallNode, errorInfo);
                    } else if (nameNode.getText().equals(CAL_Prelude.Functions.undefined.getUnqualifiedName())){
                        addSourcePositionTo_undefined(errorCallNode, errorInfo);
                    }
                }    
            }
            break;
            }
        }
        
        errorCallList.clear();
    }
    
    /**
     * Change "assert &lt;arg&gt;" calls to "&lt;arg&gt; || error "Assert failed."" calls and set the error info on the call node.
     *  
     * @param next The tree containing the assert call at the root.
     * @param errorInfo The error info to embed in the call node.
     * 
     */
    private static void addSourcePositionTo_assert(final ParseTreeNode next, final ErrorInfo errorInfo){ 
        // assert b ==> f = b || error "Assert failed."
        
        // assert b ==> b || ...
        next.setText("@operator");
        next.setType(CALTokenTypes.APPLICATION);
        
        final ParseTreeNode orNode = ParseTreeNode.makeQualifiedVarNode(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.or.getUnqualifiedName(), null);
        
        // Create the function call node, Prelude.error2
        
        final ParseTreeNode qvNode = ParseTreeNode.makeQualifiedVarNode(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.error.getUnqualifiedName(), null);
        qvNode.setErrorInfoForErrorCall(errorInfo);
        
        // Create the literal with the error message
        
        final String encodedErrorMessage = StringEncoder.encodeString( "Assert failed." );
        final ParseTreeNode errorMessageNode = new ParseTreeNode(CALTokenTypes.STRING_LITERAL, encodedErrorMessage, null);
        
        // Create the application of error2, @ Prelude.error2 <sourcePosition> <assertArg>
        final ParseTreeNode errorNode = new ParseTreeNode(CALTokenTypes.APPLICATION, "@", null);
        errorNode.setFirstChild(qvNode);
        qvNode.setNextSibling(errorMessageNode);
        
        // now add the second argument error <sourcePosition> <message> to the || 
        
        final ParseTreeNode booleanExpression = next.getChild(1);
        booleanExpression.setNextSibling(errorNode);
        orNode.setNextSibling(booleanExpression);
        next.setFirstChild(orNode);
    }
    
    /**
     * Change "undefined" calls to "error "Prelude.undefined called."" and set the error information
     * on the call node.
     *  
     * @param errorCallNode The tree containing the undefined call at the root.
     * @param errorInfo The error information to embed in the call node.
     * 
     */
    
    private static void addSourcePositionTo_undefined(final ParseTreeNode errorCallNode, final ErrorInfo errorInfo){
        
        final ParseTreeNode firstChild = errorCallNode.firstChild();
        firstChild.setErrorInfoForErrorCall(errorInfo);
        
        // Change the undefined call to an error2 call
        
        firstChild.getChild(1).setText(CAL_Prelude.Functions.error.getUnqualifiedName());
        
        // Create the literal with the error message
        
        final String encodedErrorMessage = StringEncoder.encodeString( CAL_Prelude.Functions.undefined.getQualifiedName() + " called." );
        final ParseTreeNode errorMessageNode = new ParseTreeNode(CALTokenTypes.STRING_LITERAL, encodedErrorMessage, null);
        
        // preserve the following arguments
        
        final ParseTreeNode secondChild = firstChild.nextSibling();
        if (secondChild != null){
            errorMessageNode.setNextSibling(secondChild);
        }
        
        // add the source position as the first arguement
        
        firstChild.setNextSibling(errorMessageNode);
    }
        
    /**
     * Get the types of all free gem parts in a graph - free inputs, free outputs, and trees.
     * 
     * <p>This generates a let expression which generates all free input and output types simultaneously when evaluated.  
     * Evaluation produces a type expression with arguments to the expression function yielding the types of the roots
     *  and free arguments in the graph.
     * 
     * @param rootNodes the set of rootNodes in the graph to evaluate
     * @param moduleName the name of the module in which the graph exists
     * @return A pair of two maps:
     *   (CompositionNode.CompositionArg -> TypeExpr) Map from argument to its type.
     *   (CompositionNode -> List (of TypeExpr)) map from rootNode to the derived types for its definition..
     *     The types in the List are: argument types first (in order), then output.
     * @throws TypeException if the graph fails type checking (eg. the connections are invalid).
     */
    Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>> checkGraph(final Set<? extends CompositionNode> rootNodes, final ModuleName moduleName) throws TypeException {
        
        // argument -> it type
        final Map<CompositionNode.CompositionArgument, TypeExpr> argMap = new HashMap<CompositionNode.CompositionArgument, TypeExpr>();
        // rootNode -> its type
        final Map<CompositionNode, List<TypeExpr>> rootNodeMap = new HashMap<CompositionNode, List<TypeExpr>>();

        // check for presence of any nodes
        if (rootNodes == null || rootNodes.isEmpty()) {
            return new Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>>(argMap, rootNodeMap);
        }
       
        final CheckGraphSource checkGraphSource = CALSourceGenerator.getCheckGraphSource(rootNodes);
        final SourceModel.FunctionDefn functionSource = checkGraphSource.getGraphFunction();
        final Map<CompositionNode, List<CompositionNode.CompositionArgument>> rootToArgumentsMap = checkGraphSource.getRootToArgumentsMap();
        final List<CompositionNode.CompositionArgument> unusedArgumentList = checkGraphSource.getUnusedArgumentList();
        final Map<CompositionNode.CompositionArgument, CompositionNode.CompositionArgument> recursiveEmitterArgumentToReflectedInputMap = checkGraphSource.getRecursiveEmitterArgumentToReflectedInputMap();

        // get the type for the graph, break it up
        // Where there are n+1 roots (typed xi) and m+1 unused vars (typed yi) this returns the type 
        //   x0 -> x1 -> ... -> xn -> y0 -> y1 -> ... -> ym -> Double
        final TypeExpr functionType = checkFunction(new AdjunctSource.FromSourceModel(functionSource), moduleName);
        if (functionType == null) {
            String messageString = "Attempt to check the type of an invalid (broken) tree.  Generated text:\n" + functionSource.toSourceText();
            CALCompiler.COMPILER_LOGGER.log(Level.FINE, messageString);
            throw new TypeException(messageString);
        }
        final TypeExpr[] graphTypePieces = functionType.getTypePieces();
        
        // Iterate over the roots..
        int graphTypeIndex = 0;
        for (final Map.Entry<CompositionNode, List<CompositionNode.CompositionArgument>> entry : rootToArgumentsMap.entrySet()) {
            final CompositionNode rootNode = entry.getKey();
            final List<CompositionNode.CompositionArgument> rootNodeArguments = entry.getValue();
            
            // Get the root type.
            final TypeExpr rootTypes = graphTypePieces[graphTypeIndex];
            
            // Break it up into argument and output types.
            final TypeExpr[] rootTypePieces = rootTypes.getTypePieces(rootNodeArguments.size());
            
            // Populate the map with the argument type mappings.
            int rootTypeIndex = 0;
            for (final CompositionNode.CompositionArgument rootNodeArgument : rootNodeArguments) {
                argMap.put(rootNodeArgument, rootTypePieces[rootTypeIndex]);
                rootTypeIndex++;
            }
            
            // Add the mapping for root node types.
            rootNodeMap.put(rootNode, Arrays.asList(rootTypePieces));
            
            graphTypeIndex++;
        }
        
        // Iterate over the unused arguments.
        for (final CompositionNode.CompositionArgument unusedArgument : unusedArgumentList) {
            // Get the argument type.
            final TypeExpr argumentType = graphTypePieces[graphTypeIndex];
            
            // Add the mapping
            argMap.put(unusedArgument, argumentType);
            
            graphTypeIndex++;
        }
        
        // Iterate over the recursive emitter arguments.
        for (final Map.Entry<CompositionNode.CompositionArgument, CompositionNode.CompositionArgument> entry : recursiveEmitterArgumentToReflectedInputMap.entrySet()) {
            final CompositionNode.CompositionArgument recursiveEmitterArgument = entry.getKey();
            final CompositionNode.CompositionArgument reflectedArgument = entry.getValue();
            argMap.put(recursiveEmitterArgument, argMap.get(reflectedArgument));
        }
        
        return new Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>>(argMap, rootNodeMap);
    }
    
    /**
     * Type check a single module according to the Hindley-Milner typing system with extensions for
     * dependency analysis transformations.
     *
     * @param moduleDefnNode root ParseTreeNode for the module
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     */
    void checkModule(final ParseTreeNode moduleDefnNode, final ClassLoader foreignClassLoader) throws UnableToResolveForeignEntityException {

        moduleDefnNode.verifyType(CALTreeParserTokenTypes.MODULE_DEFN);

        clearState();
        
        final ParseTreeNode optionalCALDocNode = moduleDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        final ParseTreeNode moduleNameNode = optionalCALDocNode.nextSibling();
        final ModuleName currentModuleName = ModuleNameUtilities.getModuleNameFromParseTree(moduleNameNode);

        if (DEBUG_INFO) {
            System.out.println("-----------------------------------------------------");
            System.out.println("Compiling module " + currentModuleName);
        }

        //Inform the packager that we will be adding entities for a new module

        final Packager packager = compiler.getPackager();
        try {
            packager.newModule(currentModuleName, foreignClassLoader);
        } catch (Packager.PackagerException e) {
            // TypeChecker.checkModule: could not create a module {currentModuleName} in the package.
            compiler.logMessage(new CompilerMessage(new SourceRange(currentModuleName), new MessageKind.Fatal.CouldNotCreateModuleInPackage(currentModuleName)));
        }

        //Add references to the imported modules' type info to the current module's type info

        final ParseTreeNode importDeclarationListNode = moduleNameNode.nextSibling();
        checkModuleImports (currentModuleName, importDeclarationListNode);  
        
        final ParseTreeNode friendDeclarationListNode = importDeclarationListNode.nextSibling();
        checkFriendDeclarations (friendDeclarationListNode);        
             
        //Check the top level declarations in the module
                
        final ParseTreeNode outerDefnListNode = friendDeclarationListNode.nextSibling();
        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);

        ModuleLevelParseTrees moduleLevelParseTrees = new ModuleLevelParseTrees(moduleDefnNode, outerDefnListNode);

        //Checks that the functions used in the program are defined only once, and that
        //each function has at most one type declaration. 
        checkNamesUsedForTopLevelFunctions(moduleLevelParseTrees);
        
        //do the initial check of the type classes defined in the module. This does not check class methods yet.
        typeClassChecker = new TypeClassChecker(currentModuleTypeInfo, compiler);  
        final List<ParseTreeNode> typeClassDefnNodes = moduleLevelParseTrees.getTypeClassDefnNodes();
        typeClassChecker.checkTypeClassDefinitions(typeClassDefnNodes);        

        //Determines the types of all data constructors, both built-in and those introduced
        //via data declarations.
        //todoBI should we really hold this state?
        dataDeclarationChecker = new DataDeclarationChecker(currentModuleTypeInfo, compiler, typeClassChecker);
        dataConstructorEnv = dataDeclarationChecker.checkDataDeclarations(moduleLevelParseTrees);

        //Store the type info for the data constructors defined in the current module
        if (dataConstructorEnv != null) {
            dataConstructorEnv.wrap(currentModuleTypeInfo);
        }
        
        //check class methods. We need to have checked data declarations prior to this to be able to verify
        //that the types of the class methods are valid.
        typeClassChecker.checkClassMethods(typeClassDefnNodes);
        //add the class methods to the environment
        //todoBI it is an abuse of terminology to have class methods added to the functionEnv.
        for (int i = 0, nTypeClasses = currentModuleTypeInfo.getNTypeClasses(); i < nTypeClasses; ++i) {
            
            final TypeClass typeClass = currentModuleTypeInfo.getNthTypeClass(i);
            
            for (int j = 0, nClassMethods = typeClass.getNClassMethods(); j < nClassMethods; ++j) {
                ClassMethod classMethod = typeClass.getNthClassMethod(j);
                functionEnv = Env.extend (functionEnv, classMethod);
            }
        }       
        
        
        calculatePrimitiveFunctionTypes(moduleLevelParseTrees.getPrimitiveFunctionDeclarationNodes()); 
        
        //the built-in primitive functions and class methods have been type checked fully at this point
        if (functionEnv != null) {
            functionEnv.finishedTypeChecking();
        }
                       
        //add the instances that are defined in this module to the currentModuleTypeInfo
        //the instance methods themselves need further checking latter.      
        final ClassInstanceChecker instanceChecker = new ClassInstanceChecker(compiler, currentModuleTypeInfo, typeClassChecker, dataDeclarationChecker);
        instanceChecker.checkClassInstanceDefinitions(moduleLevelParseTrees);          

        //calculate the type expressions determined by each top-level function type declaration
        calculateDeclaredTypes(moduleLevelParseTrees.getFunctionTypeDeclarationNodes());
                            
        //add the foreign functions in the current module to the environment
        calculateForeignFunctionTypes(moduleLevelParseTrees.getForeignFunctionDefnNodes());

        //Reorder the top-level functions defined in the program so that dependees are checked before dependents.
        final Graph<String> g = performFunctionDependencyAnalysis();

        //Used to augment the parse tree of a module with hidden overloading arguments,
        //and to add hidden dictionary functions.
        final OverloadingResolver overloadingResolver = new OverloadingResolver(compiler, currentModuleTypeInfo);

        //Type check the functions. Proceed one strongly connected component at a time.

        for (int i = 0, nComponents = g.getNStronglyConnectedComponents(); i < nComponents; ++i) {

            final Graph<String>.Component component = g.getStronglyConnectedComponent(i);

            checkComponent(component);       

            //now add dictionary arguments to the function defined in this declaration group, and fix
            //up applications within the function definitions.
            overloadingResolver.resolveOverloading(overloadingInfoList);

            //Free up memory associated with this potentially bulky object
            overloadingInfoList.clear();
        }

        //warn for overloaded polymorphic let variables. These are actually *functions* in the runtime
        //even though they look like constants, and may have efficiency implications.
        
        if (WARNING_FOR_OVERLOADED_POLYMORPHIC_LET_VARS) {
        
            for (final String functionName : letVarNameToParseTreeMap.keySet()) {
             
                final ParseTreeNode functionNode = letVarNameToParseTreeMap.get(functionName);
                final TypeExpr functionType = functionBoundNamesToTypeMap.get(functionName);
                                                   
                final int firstDollar = functionName.indexOf('$');
                final String enclosingTopLevelFunctionName = functionName.substring(0, firstDollar);              
                                                            
                compiler.logMessage(new CompilerMessage(functionNode, new MessageKind.Info.LocalOverloadedPolymorphicConstant(FreeVariableFinder.getDisplayName(functionName), enclosingTopLevelFunctionName, functionType.toString())));
            }
        }
        
        //Store the type info for the functions defined in the current module.
        if (functionEnv != null) {
            functionEnv.wrap(currentModuleTypeInfo);
        }        
        
        // Store the type info for all local functions in the current module
        storeLocalFunctionTypes(currentModuleTypeInfo);
        
        typeClassChecker.checkDefaultClassMethodTypes(typeClassDefnNodes);
        
        instanceChecker.checkTypesOfInstanceMethodResolvingFunctions();        
        
        //Check the CALDoc comments
        final FreeVariableFinder finderForCALDocChecker = new FreeVariableFinder(compiler, topLevelFunctionNameSet, currentModuleTypeInfo);
        final CALDocChecker calDocChecker = new CALDocChecker(compiler, currentModuleTypeInfo, finderForCALDocChecker, this);
        calDocChecker.checkCALDocCommentsInModuleDefn(moduleDefnNode);

        //Do the lambda lifting       
        final FreeVariableFinder finder = new FreeVariableFinder(compiler, topLevelFunctionNameSet, currentModuleTypeInfo);

        final LambdaLifter lifter = new LambdaLifter(compiler, finder, currentModuleName);
        lifter.lift(outerDefnListNode);
        
        //Add the hidden class method, dictionary and dictionary switching functions
        //necessary to resolve overloading.
        overloadingResolver.addOverloadingHelperFunctions(outerDefnListNode);
        
        instanceChecker.addDerivedInstanceFunctions(outerDefnListNode);

    }    
    
    /**
     * Add type information for all the local functions in this module to their respective
     * toplevel functions' FunctionEntities in moduleTypeInfo. 
     * @param moduleTypeInfo
     */
    private void storeLocalFunctionTypes(final ModuleTypeInfo moduleTypeInfo) {
        
        final ModuleName currentModule = currentModuleTypeInfo.getModuleName();
        final LocalFunctionIdentifierGenerator identifierGenerator = new LocalFunctionIdentifierGenerator();

        // We want to process local functions in pre-order.  ie, we want to process all the local functions
        // in a given let expression, then recursively process the first local function, the second, etc.
        // The disambiguation numbers generated by the unique-name transformation in FreeVariableFinder are
        // in pre-order as well, so sorting by top-level function name, local function name, disambiguation
        // index will give us the order we want.
        final SortedSet<String> localFunctionNames = new TreeSet<String>(new Comparator<String>() {
            /** {@inheritDoc} */
            public int compare(String left, String right) {
                // Order first by name and second by disambiguation index.                
                
                final int lastLeftDollar = left.lastIndexOf('$');
                final int lastRightDollar = right.lastIndexOf('$');
                
                if(lastLeftDollar == -1 || lastRightDollar == -1) {
                    throw new IllegalStateException("unique-transformed names must contain at least 1 dollar sign");
                }
                
                final int nameOrder = left.substring(0, lastLeftDollar).compareTo(right.substring(0, lastRightDollar));
                if(nameOrder != 0) {
                    return nameOrder;
                }
                
                final int leftId = Integer.parseInt(left.substring(lastLeftDollar + 1));
                final int rightId = Integer.parseInt(right.substring(lastRightDollar + 1));
                
                if(leftId > rightId) {
                    return 1;
                } else if(rightId > leftId) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        localFunctionNames.addAll(localFunctionBoundNamesToNonGenericVarsMap.keySet());
        
        for(final String uniqueName : localFunctionNames) {
            
            final NonGenericVars nonGenericVars = localFunctionBoundNamesToNonGenericVarsMap.get(uniqueName);
            final TypeExpr typeExpr = functionBoundNamesToTypeMap.get(uniqueName);

            // Don't record local functions in compiler-generated functions
            if(uniqueName.charAt(0) == '$') {
                continue;
            }
            
            boolean hasUninstantiatedNonGenerics = false;
            for(final TypeVar typeVar : typeExpr.getUninstantiatedTypeVars()) {
                
                if(!nonGenericVars.isGenericTypeVar(typeVar)) {
                    hasUninstantiatedNonGenerics = true;
                    break;
                }
            }
            
            for(final RecordVar recordVar : typeExpr.getUninstantiatedRecordVars()) {
                
                if(!nonGenericVars.isGenericRecordVar(recordVar)) {
                    hasUninstantiatedNonGenerics = true;
                    break;
                }
            }
            
            final String[] components = uniqueName.split("\\$");
            final String toplevelFunctionName = components[0];
            final String localFunctionName = components[1];
            
            if(!toplevelFunctionName.equals(identifierGenerator.getCurrentFunction())) {
                identifierGenerator.reset(toplevelFunctionName);
            }

            final LocalFunctionIdentifier identifier = identifierGenerator.generateLocalFunctionIdentifier(currentModule, localFunctionName);
            localFunctionBoundNamesToLocalFunctionIdentifiersMap.put(uniqueName, identifier);
            
            final Function localFunction = localFunctionBoundNamesToFunctionEntityMap.get(uniqueName); 
            localFunction.setLocalFunctionIdentifier(identifier);
            localFunction.setTypeContainsUninstantiatedNonGenerics(hasUninstantiatedNonGenerics);
            
            final Function toplevelFunction = moduleTypeInfo.getFunction(toplevelFunctionName);
            toplevelFunction.addLocalFunction(localFunction);
        }
    }
    
    /**
     * The primary purpose of this function is to process the import using declarations
     * and add this information to the currentModuleTypeInfo in preparation for further
     * static analysis.
     * @param currentModuleName
     * @param importDeclarationListNode
     */
    private void checkModuleImports(final ModuleName currentModuleName, final ParseTreeNode importDeclarationListNode) {
        
        //Add references to the imported modules' type info to the current module's type info
       
        importDeclarationListNode.verifyType(CALTreeParserTokenTypes.IMPORT_DECLARATION_LIST);

        final Packager packager = compiler.getPackager();
        currentModuleTypeInfo = packager.getModuleTypeInfo(currentModuleName);        
        
        //(String -> ModuleName) map from an external function or class method's name to the name of its defining module.
        //The function or class method must be visible within this module (i.e. it must be public, and the
        //module in which it is defined must be imported in the current module).
        //These are the external sc and class methods that can be used in an unqualified way within this module.
        final Map<String, ModuleName> usingFunctionOrClassMethodMap = new HashMap<String, ModuleName>();     
        final Map<String, ModuleName> usingDataConstructorMap = new HashMap<String, ModuleName>();    
        final Map<String, ModuleName> usingTypeConstructorMap = new HashMap<String, ModuleName>();      
        final Map<String, ModuleName> usingTypeClassMap = new HashMap<String, ModuleName>();        

        for (final ParseTreeNode importDeclarationNode : importDeclarationListNode) {

            importDeclarationNode.verifyType(CALTreeParserTokenTypes.LITERAL_import);

            final ParseTreeNode importedModuleNameNode = importDeclarationNode.firstChild();
            final ModuleName importedModuleName = ModuleNameUtilities.getModuleNameFromParseTree(importedModuleNameNode);

            final ModuleTypeInfo importedModuleTypeInfo = packager.getModuleTypeInfo(importedModuleName);
            if (importedModuleTypeInfo == null) {
                MessageKind message = new MessageKind.Error.UnresolvedExternalModuleImportWithNoSuggestions(importedModuleName);
                compiler.logMessage(new CompilerMessage(importDeclarationNode, message));                
            } else {
                currentModuleTypeInfo.addImportedModule(importedModuleTypeInfo);
            }

            if (DEBUG_INFO) {
                System.out.println("Imports " + importedModuleName);
            }
            
            //do the static analysis on the import using declarations to determine what external
            //symbols can be used in an unqualified way within this module.
            
            final ParseTreeNode usingClauseNode = importedModuleNameNode.nextSibling();
            
            if (usingClauseNode != null) {
                
                usingClauseNode.verifyType(CALTreeParserTokenTypes.LITERAL_using);
                
                for (final ParseTreeNode usingItemNode : usingClauseNode) {
                    
                    switch (usingItemNode.getType()) {
                    
                        case CALTreeParserTokenTypes.LITERAL_function:
                        {
                            for (final ParseTreeNode functionNameNode : usingItemNode) {
                                
                                functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                                final String functionName = functionNameNode.getText();
                                final ModuleName otherImportedModuleName = usingFunctionOrClassMethodMap.put(functionName, importedModuleName);
                                if (otherImportedModuleName != null) {
                                    //cannot have
                                    //import A using function = foo;;
                                    //import B using function = foo;;
                                    //since the use of foo would be ambiguous: A.foo or B.foo.                                    
                                 
                                    compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.FunctionAlreadyUsedInImportUsingDeclaration(functionName, otherImportedModuleName)));
                                    
                                    //restore state to give error messages with respect to the first import using function
                                    usingFunctionOrClassMethodMap.put(functionName, otherImportedModuleName);
                                }
                                
                                final FunctionalAgent importedFunction = importedModuleTypeInfo.getFunctionOrClassMethod(functionName);
                                if (importedFunction == null) {                                    
                                    //function doesn't exist in importedModule
                                    compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.FunctionNotDefinedInModule(functionName, importedModuleName)));
                                    
                                } else if (!currentModuleTypeInfo.isEntityVisible(importedFunction)) {                                    
                                    //function exists in importedModule, but it is not visible in this module
                                    final MessageKind message;
                                    if (importedFunction instanceof Function) {
                                        //"The function {0} is not visible in module {1}."
                                        message = new MessageKind.Error.FunctionNotVisible((Function)importedFunction, currentModuleName);
                                    } else {
                                        //"The class method {0} is not visible in module {1}."
                                        message = new MessageKind.Error.ClassMethodNotVisible((ClassMethod)importedFunction, currentModuleName);
                                    }                                    
                                    compiler.logMessage(new CompilerMessage(functionNameNode, message));
                                }
                            }
                                                        
                            break;
                        }
                        
                        case CALTreeParserTokenTypes.LITERAL_dataConstructor:
                        {
                            for (final ParseTreeNode dataConstructorNameNode : usingItemNode) {

                                dataConstructorNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                                final String dataConstructorName = dataConstructorNameNode.getText();
                                final ModuleName otherImportedModuleName = usingDataConstructorMap.put(dataConstructorName, importedModuleName);
                                if (otherImportedModuleName != null) {
                                    //cannot have
                                    //import A using dataConstructor = Foo;;
                                    //import B using dataConstructor = Foo;;
                                    //since the use of Foo would be ambiguous: A.Foo or B.Foo.                                    

                                    compiler.logMessage(new CompilerMessage(dataConstructorNameNode, new MessageKind.Error.DataConstructorAlreadyUsedInImportUsingDeclaration(dataConstructorName, otherImportedModuleName)));
                                        

                                    //restore state to give error messages with respect to the first import using dataConstructor
                                    usingDataConstructorMap.put(dataConstructorName, otherImportedModuleName);
                                }

                                final DataConstructor importedDataConstructor = importedModuleTypeInfo.getDataConstructor(dataConstructorName);
                                if (importedDataConstructor == null) {
                                    //dataConstructor doesn't exist in importedModule
                                    compiler.logMessage(new CompilerMessage(dataConstructorNameNode, new MessageKind.Error.DataConstructorNotDefinedInModule(dataConstructorName, importedModuleName)));
                                            

                                } else if (!currentModuleTypeInfo.isEntityVisible(importedDataConstructor)) {
                                    //dataConstructor exists in importedModule, but it is not visible in this module
                                    //"The data constructor {0} is not visible in module {1}." 
                                    compiler.logMessage(new CompilerMessage(dataConstructorNameNode, new MessageKind.Error.DataConstructorNotVisible(importedDataConstructor, currentModuleName)));
                                }
                            }                            
                            
                            break;
                        }
                        
                        case CALTreeParserTokenTypes.LITERAL_typeConstructor:
                        {
                            for (final ParseTreeNode typeConstructorNameNode : usingItemNode) {

                                typeConstructorNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                                final String typeConstructorName = typeConstructorNameNode.getText();
                                final ModuleName otherImportedModuleName = usingTypeConstructorMap.put(typeConstructorName, importedModuleName);
                                if (otherImportedModuleName != null) {
                                    //cannot have
                                    //import A using typeConstructor = Foo;;
                                    //import B using typeConstructor = Foo;;
                                    //since the use of Foo would be ambiguous: A.Foo or B.Foo.

                                    compiler.logMessage(new CompilerMessage(typeConstructorNameNode, new MessageKind.Error.TypeConstructorAlreadyUsedInImportUsingDeclaration(typeConstructorName, otherImportedModuleName)));
                                        

                                    //restore state to give error messages with respect to the first import using typeConstructor
                                    usingTypeConstructorMap.put(typeConstructorName, otherImportedModuleName);
                                }

                                final TypeConstructor importedTypeConstructor = importedModuleTypeInfo.getTypeConstructor(typeConstructorName);
                                if (importedTypeConstructor == null) {
                                    //typeConstructor doesn't exist in importedModule
                                    compiler.logMessage(new CompilerMessage(typeConstructorNameNode, new MessageKind.Error.TypeConstructorNotDefinedInModule(typeConstructorName, importedModuleName)));

                                } else if (!currentModuleTypeInfo.isEntityVisible(importedTypeConstructor)) {
                                    //typeConstructor exists in importedModule, but it is not visible in this module
                                    //"The type {0} is not visible in module {1}."
                                    compiler.logMessage(new CompilerMessage(typeConstructorNameNode, new MessageKind.Error.TypeConstructorNotVisible(importedTypeConstructor, currentModuleName)));
                                }
                            }                            
                            
                            break;
                        }
                        
                        case CALTreeParserTokenTypes.LITERAL_typeClass:
                        {
                            for (final ParseTreeNode typeClassNameNode : usingItemNode) {

                                typeClassNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                                final String typeClassName = typeClassNameNode.getText();
                                final ModuleName otherImportedModuleName = usingTypeClassMap.put(typeClassName, importedModuleName);
                                if (otherImportedModuleName != null) {
                                    //cannot have
                                    //import A using typeClass = Foo;;
                                    //import B using typeClass = Foo;;
                                    //since the use of Foo would be ambiguous: A.Foo or B.Foo.

                                    compiler.logMessage(new CompilerMessage(typeClassNameNode, new MessageKind.Error.TypeClassAlreadyUsedInImportUsingDeclaration(typeClassName, otherImportedModuleName)));

                                    //restore state to give error messages with respect to the first import using typeClass
                                    usingTypeClassMap.put(typeClassName, otherImportedModuleName);
                                }

                                final TypeClass importedTypeClass = importedModuleTypeInfo.getTypeClass(typeClassName);
                                if (importedTypeClass == null) {
                                    //typeClass doesn't exist in importedModule
                                    compiler.logMessage(new CompilerMessage(typeClassNameNode, new MessageKind.Error.TypeClassNotDefinedInModule(typeClassName, importedModuleName)));

                                } else if (!currentModuleTypeInfo.isEntityVisible(importedTypeClass)) {
                                    //typeClass exists in importedModule, but it is not visible in this module
                                    //"The class {0} is not visible in module {1}."
                                    compiler.logMessage(new CompilerMessage(typeClassNameNode, new MessageKind.Error.TypeClassNotVisible(importedTypeClass, currentModuleName)));
                                }
                            }                            
                            
                            break;
                        }
                        
                        default :
                        {                            
                            usingItemNode.unexpectedParseTreeNode();                       
                            break;
                        }                    
                    }
                }                               
            }                     
            
        }
        
        currentModuleTypeInfo.finishAddingImportedModules();
        
        currentModuleTypeInfo.addUsingFunctionOrClassMethodMap(usingFunctionOrClassMethodMap);
        currentModuleTypeInfo.addUsingDataConstructorMap(usingDataConstructorMap);
        currentModuleTypeInfo.addUsingTypeConstructorMap(usingTypeConstructorMap);
        currentModuleTypeInfo.addUsingTypeClassMap(usingTypeClassMap);

        if (DEBUG_INFO) {
            System.out.println();
        }
    }
    
    /**
     * Add friend modules to the currentModuleTypeInfo.
     * Do static analysis on the friend declarations to check basic correctness.
     * For example, cannot repeat a friend 
     * @param friendDeclarationListNode
     */
    private void checkFriendDeclarations(final ParseTreeNode friendDeclarationListNode) {
        friendDeclarationListNode.verifyType(CALTreeParserTokenTypes.FRIEND_DECLARATION_LIST);
        
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        
        for (final ParseTreeNode friendDeclarationNode : friendDeclarationListNode) {
            
            friendDeclarationNode.verifyType(CALTreeParserTokenTypes.LITERAL_friend);
            
            final ParseTreeNode friendNameNode = friendDeclarationNode.firstChild();
            final ModuleName friendModuleName = ModuleNameUtilities.getModuleNameFromParseTree(friendNameNode);
            
            if (currentModuleName.equals(friendModuleName)) {
                //a module cannot be a friend to itself
                compiler.logMessage(new CompilerMessage(friendNameNode, new MessageKind.Error.ModuleCannotBeFriendOfItself(friendModuleName)));                          
                
            } else if (currentModuleTypeInfo.hasFriendModule(friendModuleName)) {  
                //repeated friend declarations for the same friend module are not allowed.
                compiler.logMessage(new CompilerMessage(friendNameNode, new MessageKind.Error.RepeatedFriendModuleDeclaration(friendModuleName)));
                
            } else if (currentModuleTypeInfo.getDependeeModuleTypeInfo(friendModuleName) != null) {
                //a module cannot have a friend module that is a direct or indirect import.
                //For example, if module A imports module B, then module B above cannot refer to any symbols
                //of A since to import A would result in a cyclic import. Thus it can't refer to the protected symbols
                //so there is no point in making a friend declaration for B. 
                //If we ever allow recursive module imports, then this condition can be changed.
                //For now, this is a helper so people put their friend declarations in the right spot.
                compiler.logMessage(new CompilerMessage(friendNameNode, new MessageKind.Error.ModuleCannotBeFriendOfImport(currentModuleName, friendModuleName)));
                
            } else {                              
                currentModuleTypeInfo.addFriendModule(friendModuleName);
            }
        }
    }
    
    /**
     * Checks that each function defined in the module is defined only once, and that
     * each function has at most one type declaration. 
     * 
     * @param moduleLevelParseTrees
     */
    private void checkNamesUsedForTopLevelFunctions(final ModuleLevelParseTrees moduleLevelParseTrees) {
                
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
         
        primitiveFunctionNamesSet = new HashSet<String>();
                      
        final List<ParseTreeNode> primitiveFunctionDeclarationNodes = moduleLevelParseTrees.getPrimitiveFunctionDeclarationNodes();
        for (final ParseTreeNode primitiveFunctionNode : primitiveFunctionDeclarationNodes) {            
           
            primitiveFunctionNode.verifyType(CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION);
           
            final ParseTreeNode typeDeclarationNode = primitiveFunctionNode.getChild(2);
            typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);

            final ParseTreeNode primitiveFunctionNameNode = typeDeclarationNode.firstChild();
            primitiveFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            final String primitiveFunctionName = primitiveFunctionNameNode.getText();
                        
            if (!PrimitiveInfo.isPrimitiveFunction(QualifiedName.make(currentModuleName, primitiveFunctionName))) {               
                compiler.logMessage(new CompilerMessage(primitiveFunctionNode, new MessageKind.Error.InvalidPrimitiveFunction(primitiveFunctionName)));
            }            
            
            if (!primitiveFunctionNamesSet.add(primitiveFunctionName)) {
               // the primitive function has already been declared. This is an attempted re-declaration.
                compiler.logMessage(new CompilerMessage(primitiveFunctionNameNode, new MessageKind.Error.AttemptToRedeclarePrimitiveFunction(primitiveFunctionName)));
            }

        }
        
        foreignFunctionNamesSet = new HashSet<String>();
        
        final List<ParseTreeNode> foreignSCDefnNodes = moduleLevelParseTrees.getForeignFunctionDefnNodes();
        for (final ParseTreeNode foreignSCDefnNode : foreignSCDefnNodes) {
                                                
            final ParseTreeNode typeDeclarationNode = foreignSCDefnNode.getChild(3);
            typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
            
            final ParseTreeNode foreignSCNameNode = typeDeclarationNode.firstChild();
            foreignSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            final String foreignSCName = foreignSCNameNode.getText();
            
            if (primitiveFunctionNamesSet.contains(foreignSCName)) {
                // the foreign function is built-in. Cannot declare it as foreign.
                compiler.logMessage(new CompilerMessage(foreignSCNameNode, new MessageKind.Error.ForeignDeclarationForBuiltinPrimitiveFunction(foreignSCName)));
                    
            }         
                         
            if (!foreignFunctionNamesSet.add(foreignSCName)) {
               // the foreign function has already been defined. This is an attempted redefinition
                compiler.logMessage(new CompilerMessage(foreignSCNameNode, new MessageKind.Error.AttemptToRedefineFunction(foreignSCName)));
            }
            
            final ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingFunctionOrClassMethod(foreignSCName); 
            if (usingModuleName != null) {
                //there is already an 
                //import 'usingModuleName' using function = 'foreignSCName';
                compiler.logMessage(new CompilerMessage(foreignSCNameNode, new MessageKind.Error.ForeignFunctionNameAlreadyUsedInImportUsingDeclaration(foreignSCName, usingModuleName)));
            }            
        }
            
        final List<ParseTreeNode> functionDefnNodes = moduleLevelParseTrees.getFunctionDefnNodes();
        for (final ParseTreeNode functionDefnNode : functionDefnNodes) {          
   
            // Collect the names of the functions used in the program. This is needed
            // before finding the free variables in order to trap the use of undefined functions
            // in another function's definition.            
                
            final ParseTreeNode accessModifierNode = functionDefnNode.getChild(1);
            accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);
    
            final ParseTreeNode functionNameNode = accessModifierNode.nextSibling();
            functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            final String functionName = functionNameNode.getText();
    
            if (primitiveFunctionNamesSet.contains(functionName)) {
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.AttemptToRedefinePrimitiveFunction(functionName)));
            }
    
            if (foreignFunctionNamesSet.contains(functionName) || functionNameToDefinitionNodeMap.containsKey(functionName)) {
                // the function has already been defined. This is an attempted redefinition
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.AttemptToRedefineFunction(functionName)));
            }
            
            final ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingFunctionOrClassMethod(functionName); 
            if (usingModuleName != null) {
                //there is already an 
                //import 'usingModuleName' using function = 'foreignSCName';
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.FunctionNameAlreadyUsedInImportUsingDeclaration(functionName, usingModuleName)));
                    
            }            
    
            // add a reference to the parseTree to the map so that it can be looked up later
            // when typing a component
            functionNameToDefinitionNodeMap.put(functionName, functionDefnNode);
            
            // Update the set of top level functions defined in the current module.  
            topLevelFunctionNameSet.add(functionName);          
        }

        //the names of the functions that have explicit type declarations
        final Set<String> declaredNamesSet = new HashSet<String>();

        final List<ParseTreeNode> functionTypeDeclarationNodes = moduleLevelParseTrees.getFunctionTypeDeclarationNodes();
        
        for (final ParseTreeNode topLevelTypeDeclarationNode : functionTypeDeclarationNodes) {
                             
            final ParseTreeNode optionalCALDocNode = topLevelTypeDeclarationNode.firstChild();
            optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            final ParseTreeNode functionTypeDeclarationNode = optionalCALDocNode.nextSibling();
            
            final ParseTreeNode functionNameNode = functionTypeDeclarationNode.firstChild();
            functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            final String declaredFunctionName = functionNameNode.getText();

            if (primitiveFunctionNamesSet.contains(declaredFunctionName)) {
                // the function is built-in. Cannot declare a type for it.
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.TypeDeclarationForBuiltInPrimitiveFuncton(declaredFunctionName)));
            }

            if (foreignFunctionNamesSet.contains(declaredFunctionName)) {
                // the function is foreign. Cannot redeclare a type for it (it is already given in the foreign declaration).
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.TypeDeclarationForForeignFunction(declaredFunctionName)));
            }

            if (!functionNameToDefinitionNodeMap.containsKey(declaredFunctionName)) {
                // the function is declared but not defined. This is illegal in Haskell and CAL.
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.DefinitionMissing(declaredFunctionName)));
            }               

            if (!declaredNamesSet.add(declaredFunctionName)) {
                // the function has already been declared. This is an attempted redeclaration.
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.RepeatedTypeDeclaration(declaredFunctionName)));
            }            
        }
    }
    
    /**    
     * Only call this after CALTypeChecker.checkNamesUsed and TypeClassChecker.checkNamesUsed have been called.
     * @param functionName name of the function (including built-in or foreign) assumed to belong to the current module being type checked.
     * @return boolean true if varName is the name of a foreign function, top level function or built-in function defined within the current module.
     */
    boolean isTopLevelFunctionName(final String functionName) {

        //todoBI a lot of the need for carrying around foreignSCNamesSet, etc. could be removed if we added functions to
        //the currentModuleTypeInfo immediately. This is a future refactoring.
                                
        return functionNameToDefinitionNodeMap.containsKey(functionName) ||
            foreignFunctionNamesSet.contains(functionName) ||
            primitiveFunctionNamesSet.contains(functionName);                                  
    }
    
    boolean isPrimitiveOrForeignFunctionName(final String functionName) {
        return foreignFunctionNamesSet.contains(functionName) ||
            primitiveFunctionNamesSet.contains(functionName); 
    }
    
    /**
     * Ensures that the sets of foreign function names and primitive function names are
     * initialized to contain the proper content given the current module.
     */
    private void ensureFieldsInitialized() {
        // if foreignFunctionNamesSet is null, create a new set and populate it
        // with information from the module type info
        if (foreignFunctionNamesSet == null) {
            foreignFunctionNamesSet = new HashSet<String>();
            
            final int nFunctions = currentModuleTypeInfo.getNFunctions();
            for (int i = 0; i < nFunctions; i++) {
                Function function = currentModuleTypeInfo.getNthFunction(i);
                if (function.getForeignFunctionInfo() != null) {
                    foreignFunctionNamesSet.add(function.getName().getUnqualifiedName());
                }
            }
        }
        
        // if primitiveFunctionNamesSet is null, create a new set and populate it
        // with information from the module type info
        if (primitiveFunctionNamesSet == null) {
            primitiveFunctionNamesSet = new HashSet<String>();
            
            final int nFunctions = currentModuleTypeInfo.getNFunctions();
            for (int i = 0; i < nFunctions; i++) {
                Function function = currentModuleTypeInfo.getNthFunction(i);
                if (function.isPrimitive()) {
                    primitiveFunctionNamesSet.add(function.getName().getUnqualifiedName());
                }
            }
        }
    }

    /**
     * This methods checks if a new function definition is well typed. It uses
     * the enviroment already built up while checking the main module. This
     * method is intended to be called by tools, and so exceptions are
     * suppressed.
     *
     * Creation date: (10/12/00 12:26:22 PM)
     * @return TypeExpr type of the defined function, or null if it is not well-typed
     * @param functionSource the adjunct source defining the function e.g. "add2 x = x + 2;"
     * @param functionModule the module in which the function should be considered to be defined
     */
    TypeExpr checkFunction(final AdjunctSource functionSource, final ModuleName functionModule) {
         
        if (currentModuleTypeInfo == null) {
            changeModule(functionModule);
        }
        ensureFieldsInitialized();
        
        final CompilerMessageLogger oldLogger = compiler.getMessageLogger();
        final CompilerMessageLogger checkLogger = new MessageLogger(true);  // Internal compiler logger.
        compiler.setCompilerMessageLogger(checkLogger);
         
        //todoBI there is a lot of overlap between checkFunction and checkAdjunct, Factor this better.       

        // Parse and type check and catch any fatal errors
        try {
            try {

                final ParseTreeNode outerDefnListNode;
                final CALTreeParser treeParser = new CALTreeParser(compiler);

                if (functionSource instanceof AdjunctSource.FromText) {

                    final CALParser parser = freshParser(compiler, ((AdjunctSource.FromText)functionSource).getReader());

                    // Call the parser to parse an adjunct.
                    parser.adjunct();

                    outerDefnListNode = (ParseTreeNode)parser.getAST();

                } else if (functionSource instanceof AdjunctSource.FromSourceModel) {

                    outerDefnListNode = ((AdjunctSource.FromSourceModel)functionSource).toParseTreeNode();

                } else {
                    throw new IllegalArgumentException(
                            "CALTypeChecker.checkFunction - cannot handle adjunct source of type " + functionSource.getClass());
                }

                // Walk the parse tree as a sanity check on the generated AST and of the tree parser
                treeParser.adjunct(outerDefnListNode);

                if (outerDefnListNode.getNumberOfChildren() > 2) {
                    throw new IllegalArgumentException("Programming Error: TypeChecker.checkAdjunct. Can only add a single adjunct.");
                }

                outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
                final ParseTreeNode outerDefnNode = outerDefnListNode.firstChild();
                outerDefnNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN, CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION);
                
                final ParseTreeNode functionNode;                 
                if (outerDefnNode.getType() == CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION) {
                    functionNode = outerDefnNode.nextSibling();                    
                } else {
                    functionNode = outerDefnNode;                    
                }
                functionNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);

                changeModule (functionModule);

                final Set<String> functionNamesSet = new HashSet<String>();

                FreeVariableFinder finder = new FreeVariableFinder(compiler, functionNamesSet, currentModuleTypeInfo);
                finder.resolveUnqualifiedIdentifiers(functionNode);

                final ParseTreeNode optionalCALDocNode = functionNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                final ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
                accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);
                final Scope scope = getScopeModifier(accessModifierNode);

                final ParseTreeNode functionNameNode = accessModifierNode.nextSibling();
                functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                final String functionName = functionNameNode.getText();

                if(currentModuleTypeInfo.getFunction(functionName) != null) {
                    compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.AttemptToRedefineFunction(functionName)));
                    return null;
                }

                final TypeVar newTypeVar = new TypeVar();

                final Function function = Function.makeTopLevelFunction(QualifiedName.make(functionModule, functionName), newTypeVar, scope);

                // extend with adjunct, but don't modify the original environment.  This is a non-destructive check.
                final Env extendedFunctionEnv = Env.extend(functionEnv, function);

                currentOverloadingInfo = new OverloadingInfo(function, functionNode, newTypeVar, null);
                overloadingInfoList.add(currentOverloadingInfo);            

                final NonGenericVars nonGenericVars = NonGenericVars.extend(null, newTypeVar);       
                final TypeExpr typeExpr = analyzeTopLevelFunction(extendedFunctionEnv, nonGenericVars, functionNode);

                extendedFunctionEnv.finishedTypeChecking(1);

                currentOverloadingInfo.finishedTypeCheckingFunction(null);         

                //Used to augment the parse tree of a module with hidden overloading arguments,
                //and to add hidden dictionary functions.
                final OverloadingResolver overloadingResolver = new OverloadingResolver(compiler, currentModuleTypeInfo);

                // now add dictionary arguments to the function, and fix up applications within the function definition.
                overloadingResolver.resolveOverloading(overloadingInfoList);

                if (DEBUG_INFO) {
                    System.out.println("");
                    System.out.println("overloading for: " + functionName);
                    System.out.println(functionNode.toStringTree());
                }

                // Free up memory associated with this potentially bulky object
                overloadingInfoList.clear();

                //Check the CALDoc comments
                final ParseTreeNode paramListNode = functionNameNode.nextSibling();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

                final ParseTreeNode definingExprNode = paramListNode.nextSibling();

                final FreeVariableFinder finderForCALDocChecker = new FreeVariableFinder(compiler, topLevelFunctionNameSet, currentModuleTypeInfo);
                final CALDocChecker calDocChecker = new CALDocChecker(compiler, currentModuleTypeInfo, finderForCALDocChecker, this);
                calDocChecker.checkCALDocCommentsInExpr(definingExprNode);

                //now do the lambda lifting

                final LambdaLifter lifter = new LambdaLifter(compiler, finder, currentModuleTypeInfo.getModuleName());
                lifter.lift(outerDefnListNode);

                if (compiler.getMessageLogger().getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {             
                    return null;
                }

                //deep prune so that instantiated type variables are not part of the returned TypeExpr.
                //this has the effect of chosing a deterministic element in the equivalence class of
                //representations of this TypeExpr.
                return typeExpr.deepPrune();                      

            } catch (antlr.RecognitionException e) {
                // syntax error
                final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));

            } catch (antlr.TokenStreamException e) {
                // Bad token stream
                compiler.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));
            }
        
        } catch (AbortCompilation e) {
            // Compilation abort, we catch and continue
        
        } catch (Exception e) {
            // Major failure - internal coding error
            try {
                final int errorCount = compiler.getMessageLogger().getNErrors();
                if (errorCount > 0 || e instanceof UnableToResolveForeignEntityException) {

                    // If the exception is an UnableToResolveForeignEntityException, there is
                    // a CompilerMessage inside that we should be logging.
                    if (e instanceof UnableToResolveForeignEntityException) {
                        try {
                            compiler.logMessage(((UnableToResolveForeignEntityException)e).getCompilerMessage());
                        } catch (AbortCompilation ace) {
                            //logMessage can throw a AbortCompilation if a FATAL message was sent.
                        }
                    }
                    
                    //if an error occurred previously, we continue to compile the program to try to report additional
                    //meaningful compilation errors. However, this can produce spurious exceptions related to the fact
                    //that the program state does not satisfy preconditions because of the initial error(s). We don't
                    //report the spurious exception as an internal coding error.
                    compiler.logMessage(new CompilerMessage(new MessageKind.Info.UnableToRecover()));
                } else {                               
                    compiler.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                }                              
            } catch (AbortCompilation ace) {
                // Yeah, yeah, we know
            }
        } finally {
            // replace the old logger.
            compiler.setCompilerMessageLogger(oldLogger);
            
            try {
                oldLogger.logMessages(checkLogger);
            } catch (AbortCompilation e) {
                // Too many errors for the ol' logger.
            }
        }

        //the failure case- be sure to return null
        return null;
    }    
    
    /**
     * Clears the state of the type checker to prepare to process a new module.
     * Creation date: (10/13/00 10:16:32 AM)
     */
    private void clearState() {

        functionEnv = null;
        functionBoundNamesToTypeMap.clear();
        localFunctionBoundNamesToNonGenericVarsMap.clear();
        localFunctionBoundNamesToLocalFunctionIdentifiersMap.clear();
        localFunctionBoundNamesToFunctionEntityMap.clear();
        evaluatedLocalVariables.clear();
        letVarNameToParseTreeMap.clear();
        dataConstructorEnv = null;
        dataDeclarationChecker = null;
        currentModuleTypeInfo = null;

        functionNameToDefinitionNodeMap.clear();
        functionNameToDeclaredTypeMap.clear();
        
        topLevelFunctionNameSet.clear();
        overloadingInfoList.clear();
        
        nestingLevel = 0;
        currentOverloadingInfo = null;
    }
    
    /**
     * Changes the current module in the type checker.
     *   Certain state will also be cleared such that the type checker will be able to process an adjunct if necessary.
     * This method should only be used when modules have been fully typechecked.  It would be used, for instance, 
     *   if an adjunct needed to be type checked in a different module, or to make available certain type services 
     *   (eg. getting the type from a string).
     * @param moduleName the name of the module to which to change.
     */
    private void changeModule(final ModuleName moduleName) {

        // reconstruct or clear the various environments and maps as appropriate.

        // switch modules..
        final Packager packager = compiler.getPackager();
        final ModuleTypeInfo mti = packager.getModuleTypeInfo(moduleName);
        if (mti == null) {
            compiler.logMessage(new CompilerMessage(new MessageKind.Fatal.ModuleNotInWorkspace(moduleName)));
        }

        currentModuleTypeInfo = new ModuleTypeInfo(mti);
                
        // functionEnv, dataConstructorEnv
        buildEnvFromTypeInfo();
        
        functionBoundNamesToTypeMap.clear();
        localFunctionBoundNamesToNonGenericVarsMap.clear();
        localFunctionBoundNamesToLocalFunctionIdentifiersMap.clear();
        localFunctionBoundNamesToFunctionEntityMap.clear();
        evaluatedLocalVariables.clear(); 
        letVarNameToParseTreeMap.clear();

        // typeClassChecker
        typeClassChecker = new TypeClassChecker(currentModuleTypeInfo, compiler);

        
        // dataDeclarationChecker
        dataDeclarationChecker = new DataDeclarationChecker(currentModuleTypeInfo, compiler, typeClassChecker);
      
        // foreignSCNamesSet
        if (foreignFunctionNamesSet == null) {
        	foreignFunctionNamesSet = new HashSet<String>();
        } else {
        	foreignFunctionNamesSet.clear();
        }
        
        functionNameToDefinitionNodeMap.clear();
        functionNameToDeclaredTypeMap.clear();   
        overloadingInfoList.clear();
        nestingLevel = 0;
        currentOverloadingInfo = null;
        topLevelFunctionNameSet.clear();

        // Update the topLevelSCNameSet with the names of functions in the current module.        
        final int nFunctions = currentModuleTypeInfo.getNFunctions();
        for (int i = 0; i < nFunctions; i++) {
            FunctionalAgent entity = currentModuleTypeInfo.getNthFunction(i);
            topLevelFunctionNameSet.add(entity.getName().getUnqualifiedName());
        }
    }
    
    /**
     * Rebuild the current functions and data constructor environments from the current ModuleTypeInfo.
     */
    private void buildEnvFromTypeInfo() {
        functionEnv = null;
       
        // add functions
        final int nFunctions = currentModuleTypeInfo.getNFunctions();
        for (int i = 0; i < nFunctions; i++) {
            Function function = currentModuleTypeInfo.getNthFunction(i);
            functionEnv = Env.extend(functionEnv, function);
        }

        //add the class methods
        //todoBI it is an abuse of terminology to have class methods added to the functionEnv.
        final int nTypeClasses = currentModuleTypeInfo.getNTypeClasses();
        for (int i = 0; i < nTypeClasses; ++i) {
            final TypeClass typeClass = currentModuleTypeInfo.getNthTypeClass(i);

            for (int j = 0, nClassMethods = typeClass.getNClassMethods(); j < nClassMethods; ++j) {
                final ClassMethod classMethod = typeClass.getNthClassMethod(j);
                functionEnv = Env.extend (functionEnv, classMethod);
            }
        }

        // data constructors
        dataConstructorEnv = null;
        final int nTypeConstructors = currentModuleTypeInfo.getNTypeConstructors();
        for (int i = 0; i < nTypeConstructors; i++) {
            final TypeConstructor typeCons = currentModuleTypeInfo.getNthTypeConstructor(i);
            
            final int nDataConstructors = typeCons.getNDataConstructors();
            for (int j = 0; j < nDataConstructors; j++) {
                DataConstructor dataConstructor = typeCons.getNthDataConstructor(j);
                dataConstructorEnv = Env.extend(dataConstructorEnv, dataConstructor);
            }
        }
    }          
              
    /**
     * Construct a new parser state from the given reader.
     *   The returned parser will have its lexer and stream selector set, and will be configured for ASTNodes of type ParseTreeNode.
     *   Note that tree nodes created by this parser will not have any source name (filename) info.
     * 
     * @param compiler The compiler.  This will be used for message logging and for parser access to its stream selector.
     * @param reader the reader from which to parse.
     * @return CALParser a new parser configured for the given args.
     */
    private static CALParser freshParser(final CALCompiler compiler, final java.io.Reader reader) {

        // Make a multiplexed lexer
        final CALMultiplexedLexer lexer = new CALMultiplexedLexer(compiler, reader, null);
        
        // Create a parser, it gets its tokens from the multiplexed lexer
        final CALParser parser = new CALParser(compiler, lexer);
                
        final String treeNodeClassName = ParseTreeNode.class.getName();
        parser.setASTNodeClass(treeNodeClassName);
        
        return parser;
    }

    /**
     * Dump typing info for debugging purposes.
     * Creation date: (1/8/01 5:23:02 PM)
     * @param functionName
     * @param typeExpr
     * @param inferredTypeExpr
     */
    private void dumpTypingInfo(final String functionName, final TypeExpr typeExpr, final TypeExpr inferredTypeExpr) {

        System.out.println("function " + functionName);
        System.out.println("inferred type :: " + inferredTypeExpr.toString());

        final TypeExpr declaredTypeExpr = functionNameToDeclaredTypeMap.get(functionName);
        if (declaredTypeExpr != null) {
            System.out.println("declared type :: " + declaredTypeExpr.toString());
            System.out.println("final type    :: " + typeExpr.toString() + '\n');
        } else {
            System.out.println("no declared type\n");
        }

        /*
        System.out.println ("# args = " + typeExpr.getNApplications());
        TypeExpr [] typePieces = typeExpr.getTypePieces ();
        for (int k = 0; k < typePieces.length; ++k) {
            System.out.println ("piece" + k + " : " + typePieces [k].toString ());
        }
        */
    }
           
    /**
     * Obtain the entity for this name. Returns null if the entity is not found.
     * Creation date: (1/29/01 4:23:46 PM)
     * @return FunctionalAgent
     * @param name QualifiedName the name of the identifier, as encoded in the Expression class.
     */
    FunctionalAgent getEntity(final QualifiedName name) {
        
        //todoBI the only reason this can't look up from the currentModuleTypeInfo is because
        //of checking of instance resolution functions for adjuncts. When adjuncts are implemented
        //properly (i.e. with sufficient static checking) then this method can be simplified.

        final ModuleName moduleName = name.getModuleName();
        final String unqualifiedName = name.getUnqualifiedName();

        

        if (moduleName.equals(currentModuleTypeInfo.getModuleName())) {

            // We use the underlying environment method, but we have to check multiple environments
            // (one for SC's and one for data constructors)

            if (functionEnv != null) {
                final FunctionalAgent entity = functionEnv.retrieveEntity(unqualifiedName);
                if (entity != null) {
                    return entity;
                }
            }

            if (dataConstructorEnv != null) {
                final FunctionalAgent entity = dataConstructorEnv.retrieveEntity(unqualifiedName);
                if (entity != null) {
                    return entity;
                }
            }
        }

        ModuleTypeInfo moduleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
        if (moduleTypeInfo == null) {
            return null;
        }

        return moduleTypeInfo.getFunctionalAgent(unqualifiedName);
    }
    
    /**
     * Returns the name of the qualified entity to be used for display in error messages.
     * The FreeVariableFinder makes local names unique by changing a local name such as x occurring in the definition 
     * of f to something like f$x$9. We want to get back to x for the purpose of error messages. 
     *
     * Creation date: (7/10/01 12:06:08 PM)
     * @return String
     * @param qualifiedNameNode
     */
    static private String getQualifiedNameDisplayString(final ParseTreeNode qualifiedNameNode) {

        if (qualifiedNameNode.getType() != CALTreeParserTokenTypes.QUALIFIED_CONS &&
            qualifiedNameNode.getType() != CALTreeParserTokenTypes.QUALIFIED_VAR) {
            throw new IllegalArgumentException("TypeChecker: unexpected parse tree node " + qualifiedNameNode.toDebugString());
        }

        final ParseTreeNode moduleNameNode = qualifiedNameNode.firstChild();
        final String moduleName = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(moduleNameNode);
        final ParseTreeNode nameNode = moduleNameNode.nextSibling();
        
        final String unqualifiedDisplayName = FreeVariableFinder.getDisplayName(nameNode.getText());
        final String displayName;
        
        if (moduleName.length() > 0) {
            displayName = moduleName + '.' + unqualifiedDisplayName;
        } else {
            displayName = unqualifiedDisplayName;
        }

        return displayName;
    }
    
    /**
     * A helper function that returns the scope (i.e. public or private) modifier given the
     * access modifier ParseTreeNode. If the scope is omitted, it defaults to private.
     * Creation date: (6/18/01 11:27:30 AM)    
     * @param accessModifierNode
     * @return the corresponding scope modifier.
     */
    static Scope getScopeModifier(final ParseTreeNode accessModifierNode) {

        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);

        final ParseTreeNode scopeNode = accessModifierNode.firstChild();

        //the default scope is private
        if (scopeNode == null) {
            return Scope.PRIVATE;
        }
        
        switch (scopeNode.getType()) {
            case CALTreeParserTokenTypes.LITERAL_private:
                return Scope.PRIVATE;
                
            case CALTreeParserTokenTypes.LITERAL_protected:
                return Scope.PROTECTED;
                
            case CALTreeParserTokenTypes.LITERAL_public:
                return Scope.PUBLIC;
                
            default:
                throw new IllegalStateException("Unexpected parse tree node " + scopeNode.toDebugString());
        }       
    }
    
    /**
     * Attempts to match the inferred type for the top-level function with the type determined by the type declaration.
     * Note that on output, typeExpr is the (possibly) restricted type which is the actual type of the function.
     * Creation date: (1/9/01 1:20:49 PM)
     * @param functionName name of the function
     * @param inferredTypeExpr the inferred type on input, the restricted type on output
     */
    private void patternMatch(final String functionName, final TypeExpr inferredTypeExpr /*in-out*/) {

        final TypeExpr originalDeclaredTypeExpr = functionNameToDeclaredTypeMap.get(functionName);
               
        if (originalDeclaredTypeExpr != null) {
            
            final TypeExpr declaredTypeExpr = originalDeclaredTypeExpr.copyTypeExpr(); // we use a copy of the declared type, so as to keep the original one pristine for later checking
            
            final TypeExpr copyOfTypeExpr = inferredTypeExpr.copyTypeExpr();

            try {
                TypeExpr.patternMatch(declaredTypeExpr, inferredTypeExpr, currentModuleTypeInfo);

            } catch (TypeException te) {

                final ParseTreeNode functionNode = functionNameToDefinitionNodeMap.get(functionName);

                //the declared type of the function is not compatible with its inferred type.           
                compiler.logMessage(new CompilerMessage(functionNode, new MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType(functionName, copyOfTypeExpr.toString()),
                    te));
            }
        }
    }
    
    /**
     * Perform dependency analysis to divide up the functions defined in a module into
     * a topologically ordered set of strongly connected components.
     * Creation date: (1/18/01 6:11:23 PM)
     * @return Graph 
     */
    private Graph<String> performFunctionDependencyAnalysis() {
        
        final FreeVariableFinder freeVariableFinder = new FreeVariableFinder(compiler, topLevelFunctionNameSet, currentModuleTypeInfo);
        return freeVariableFinder.performFunctionDependencyAnalysis(functionNameToDefinitionNodeMap);      
    }
    
    /**
     * Retrieves a qualified data constructor from the module that is currently being typechecked, or from
     * the ModuleTypeInfo objects of previously type checked modules, as appropriate.
     *
     * If the data constructor cannot be found, an appropriate message is logged. Note: this message is typically
     * redundant since earlier checks will have determined if the data constructor can be resolved, but it
     * improves multiple error messages to handle things carefully here.
     *     
     * Creation date: (6/29/01 6:22:32 PM)     
     * @param qualifiedDataConsNode ParseTreeNode
     * @return DataConstructor null if the data constructor could not be retrieved.
     */
    private DataConstructor retrieveQualifiedDataConstructor(final ParseTreeNode qualifiedDataConsNode) {

        qualifiedDataConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        final QualifiedName dataConsName = qualifiedDataConsNode.toQualifiedName();

        final ModuleName moduleName = dataConsName.getModuleName();

        final ParseTreeNode consNode = qualifiedDataConsNode.getChild(1);
        consNode.verifyType(CALTreeParserTokenTypes.CONS_ID);        

        final String unqualifiedDataConsName = dataConsName.getUnqualifiedName();
      
        if (moduleName.equals(currentModuleTypeInfo.getModuleName())) {
            
            final DataConstructor dataCons = (DataConstructor)dataConstructorEnv.retrieveEntity(unqualifiedDataConsName);
           
            if (dataCons == null) {

                final String displayName = getQualifiedNameDisplayString(qualifiedDataConsNode);
                // TypeChecker: unknown data constructor {displayName}.
                compiler.logMessage(new CompilerMessage(consNode, new MessageKind.Error.UnknownDataConstructor(displayName)));               
            }            
            
            return dataCons;
        }

        final ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
        final DataConstructor dataCons = importedModuleTypeInfo.getDataConstructor(unqualifiedDataConsName);
        
        if (dataCons == null) {
            String displayName = getQualifiedNameDisplayString(qualifiedDataConsNode);
            // TypeChecker: unknown data constructor {displayName}.
            compiler.logMessage(new CompilerMessage(consNode, new MessageKind.Error.UnknownDataConstructor(displayName)));            
            return null;
        }
        
        if (currentModuleTypeInfo.isEntityVisible(dataCons)) {
            return dataCons;
        }
                        
        //"The data constructor {0} is not visible in module {1}."
        compiler.logMessage(new CompilerMessage(consNode, new MessageKind.Error.DataConstructorNotVisible(dataCons, currentModuleTypeInfo.getModuleName())));           
            
        return null;              
    }
    
    /**
     * Retrieves a qualified variable from the module that is currently being typechecked, or from
     * the ModuleTypeInfo objects of previously type checked modules, as appropriate.
     * If the entity cannot be found, an appropriate message is logged. Note: this message is typically
     * redundant since earlier checks will have determined if the entity can be resolved, but it
     * improves multiple error messages to handle things carefully here.
     *
     * Creation date: (6/28/01 10:45:38 AM)    
     * @param functionEnv
     * @param qualifiedVarNode
     * @param logErrors true if the inability to retreive a variable from the current module should be logged as an error.
     *     This parameter is added to simplify the requirement for building a full environment for the let variable optimization.
     * @return FunctionalAgent null if the entity cannot be retreived.  
     */
    private FunctionalAgent retrieveQualifiedVar(final Env functionEnv, final ParseTreeNode qualifiedVarNode, final boolean logErrors) {

        qualifiedVarNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
        final QualifiedName varName = qualifiedVarNode.toQualifiedName();

        final ModuleName moduleName = varName.getModuleName();

        final ParseTreeNode varNode = qualifiedVarNode.getChild(1);
        varNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        final String unqualifiedName = varName.getUnqualifiedName();
        
        if (moduleName.equals(currentModuleTypeInfo.getModuleName())) {
            
            final FunctionalAgent entity = functionEnv.retrieveEntity(unqualifiedName);
            
            if (entity == null && logErrors) {

                String displayName = getQualifiedNameDisplayString(qualifiedVarNode);
                // TypeChecker: unknown function or variable {displayName}.
                compiler.logMessage(new CompilerMessage(varNode, new MessageKind.Error.UnknownFunctionOrVariable(displayName)));                 
            }
            
            return entity;
        }

        final ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
        final FunctionalAgent entity = importedModuleTypeInfo.getFunctionOrClassMethod(unqualifiedName);
        
        if (entity == null) {
            final String displayName = getQualifiedNameDisplayString(qualifiedVarNode);
            // TypeChecker: unknown function or variable {displayName}.
            compiler.logMessage(new CompilerMessage(varNode, new MessageKind.Error.UnknownFunctionOrVariable(displayName)));             
            
            return null;
        }
        
        if (currentModuleTypeInfo.isEntityVisible(entity) || qualifiedVarNode.isInternallyGenerated()) {
            return entity;
        }
        
        // The identifier {qualifiedVarName} is not visible in {currentModuleName}.
        
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        final MessageKind message;
        if (entity instanceof Function) {
            //"The function {0} is not visible in module {1}."
            message = new MessageKind.Error.FunctionNotVisible((Function)entity, currentModuleName);
        } else {
            //"The class method {0} is not visible in module {1}."
            message = new MessageKind.Error.ClassMethodNotVisible((ClassMethod)entity, currentModuleName);
        }
        compiler.logMessage(new CompilerMessage(varNode, message));        
            
        return null;        
    }
    
    /**
     * A helper method to get the field bindings from a parse tree node for a list of such bindings.
     * 
     * This method assumes that there are no duplicate field bindings.
     * 
     * @param fieldBindingVarAssignmentListNode the parse tree node for the list of field bindings.
     * @param fieldNameToParseTreeNodeMap (FieldName->ParseTreeNode) if non-null, this map will be populated with mappings
     *   from field name to the parse tree node for that field name
     * @param recordBindings true for record bindings, false for data constructor case unpack alt bindings.     
     * @return (FieldName -> String) Map from field name to the name bound for that field, in the order that the fields were given.
     */
    static SortedMap<FieldName, String> getFieldBindingsMap(final ParseTreeNode fieldBindingVarAssignmentListNode, final Map<FieldName, ParseTreeNode> fieldNameToParseTreeNodeMap, 
                                         final boolean recordBindings) {
        
        fieldBindingVarAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);

        final SortedMap<FieldName, String> fieldBindingsMap = new TreeMap<FieldName, String>(); //FieldName -> String

        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {

            fieldBindingVarAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT);

            final ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            fieldNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
            final FieldName fieldName = FieldName.make(fieldNameNode.getText());            

            final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
            final String patternVarName;

            switch (patternVarNode.getType()) {
                case CALTreeParserTokenTypes.VAR_ID :
                {
                    patternVarName = patternVarNode.getText();                    
                    break;
                }

                case CALTreeParserTokenTypes.UNDERSCORE :
                {
                    if (recordBindings) {
                        patternVarName = Expression.RecordCase.WILDCARD_VAR;             
                    } else {
                        patternVarName = Expression.Switch.SwitchAlt.WILDCARD_VAR;             
                    }
                    break;
                }

                default :
                {
                    patternVarNode.unexpectedParseTreeNode();                    
                    return null;
                }
            }
          
            fieldBindingsMap.put(fieldName, patternVarName);
            
            if (fieldNameToParseTreeNodeMap != null) {
                fieldNameToParseTreeNodeMap.put(fieldName, fieldNameNode);
            }
        }
        
        return fieldBindingsMap;
    }

    /**
     * An internal helper method.
     * @param functionBoundName name of a top-level (non-foreign, non-primitive function), or the unique name of any
     *   lower-case starting symbol occurring within a function definition.
     * @return TypeExpr 
     */
    TypeExpr getFunctionBoundNameType(final String functionBoundName) {        
        return functionBoundNamesToTypeMap.get(functionBoundName);        
    }
    
    /**     
     * Returns true if the local variables is known by the compiler knows to be evaluated to weak-head normal form.
     *
     * These variables are:
     * a) strict function bound argument names (i.e. corresponding to plinged function arguments).
     * b) variables introduced by a case-pattern where the variable is guaranteed to be evaluated because of the
     *    strictness annotations on the corresponding data constructor argument.    
     * c) let variables of the form 
     *     x = expression known by the compiler to be evaluated to weak head normal form.
     *     Some important cases of this are where the expression is:
     *     -a top level applications of Prelude.eager
     *     -literals (string, integers, double, lists, tuples, non-extension records)
     *     -aliases for top-level positive arity functions and (zero of positive arity) data constructors
     *          -we exclude zero-arity functions e.g. x = Prelude.undefined; should not be marked as being evaluated since
     *           running it intentionally terminates in an error.
     *     -aliases for evaluated local variables
     *     -certain special data constructor field selections of the form (evaluated-expression).MyDataConstructor.myStrictField)        
     *    
     * @param uniqueArgName
     * @return true if uniqueArgName is known to be evaluated to weak head normal form.
     */
    boolean isEvaluatedLocalVariable(final String uniqueArgName) { 
        return evaluatedLocalVariables.contains(uniqueArgName);        
    } 
    
    /**
     * @param functionName the name of the top-level function.
     * @return the definition node of the function, or null if there is none.
     */
    ParseTreeNode getFunctionDefinitionNode(final String functionName) {
        return functionNameToDefinitionNodeMap.get(functionName);
    }
    
    /**
     * @param functionName the name of the top-level function.
     * @return true if the function has a corresponding type declaration; false otherwise.
     */
    boolean hasFunctionTypeDeclaration(final String functionName) {
        return functionNameToDeclaredTypeMap.containsKey(functionName);
    }
    
    /**
     * Looks up the LocalFunctionIdentifier for a local function with the specified unique-transformed name.
     * @param uniqueName
     * @return LocalFunctionIdentifier
     */
    LocalFunctionIdentifier getLocalFunctionIdentifier(final String uniqueName) {
        return localFunctionBoundNamesToLocalFunctionIdentifiersMap.get(uniqueName);
    }
    
    /**
     * A helper method to add an instance function for  a derived instance, 
     * to the environment so that that instance function can go through
     * further type-checking (most importantly overload resolution).    
     * @param functionName
     * @param functionParseTree
     * @param declaredFunctionTypeExpr may be null if no type is declared
     */
    void addDerivedInstanceFunction(final String functionName, final ParseTreeNode functionParseTree, final TypeExpr declaredFunctionTypeExpr) {
        functionParseTree.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);
        topLevelFunctionNameSet.add(functionName);        
        functionNameToDefinitionNodeMap.put(functionName, functionParseTree);
        if (declaredFunctionTypeExpr != null) {
            functionNameToDeclaredTypeMap.put(functionName, declaredFunctionTypeExpr);
        }
    }
    
    /**
     * @return the current module name or null if not available
     */
    ModuleName getCurrentModuleName(){
        if (currentModuleTypeInfo == null){
            return null;
        }
        else{
            return currentModuleTypeInfo.getModuleName();
        }
    }
}
