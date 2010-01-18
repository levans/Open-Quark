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
 * ExpressionGenerator.java
 * Created: Sep 12 2000
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openquark.cal.compiler.Expression.ErrorInfo;
import org.openquark.cal.compiler.Expression.Let.LetDefn;
import org.openquark.cal.compiler.Expression.Switch.SwitchAlt;
import org.openquark.cal.internal.module.Cal.Collections.CAL_List_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Bits_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Debug_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Dynamic_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Exception_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.internal.module.Cal.Utilities.CAL_QuickCheck_internal;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * Takes a possibly simplified AST produced by the parser and type checker, and converts
 * to our own internal Expression format.
 *
 * Creation date: (9/12/00 11:24:51 AM)
 * @author Bo Ilic
 */
final class ExpressionGenerator {
    
    private static final SortedMap<FieldName, String> NO_MATCHING_FIELDS = Collections.unmodifiableSortedMap(new TreeMap<FieldName, String>());
    private static final SortedMap<Integer, String> NO_POSITIONAL_FIELDS = Collections.unmodifiableSortedMap(new TreeMap<Integer, String>());
    
    private final CALCompiler compiler;

    /** The name of the module in which to generate the expression. */
    private final ModuleName currentModuleName;
    
    private final ModuleTypeInfo currentModuleTypeInfo;
    
    /** whether expression generation is for an adjunct or not */
    private final boolean isAdjunct;
       
    /** 
     * Counts the number of letNonRecs,letRecs (with a single binding) and 
     * letRecs (with a multiple binding) in the current module, as well as
     * running totals for the workspace.     
     */
    private static final boolean GENERATE_LET_STATISTICS = false;
    private int nLetNonRecs;
    private int nLetRecsMultiple;
    private int nLetRecsSingle;    
    private static int totalNLetNonRecs;    
    private static int totalNLetRecsMultiple;
    private static int totalNLetRecsSingle;

    /**
     * Constructs an ExpressionGenerator.
     * @param compiler 
     * @param currentModuleTypeInfo
     * @param isAdjunct
     */
    ExpressionGenerator(CALCompiler compiler, ModuleTypeInfo currentModuleTypeInfo, boolean isAdjunct) {
        if (compiler == null || currentModuleTypeInfo == null) {
            throw new NullPointerException();            
        }
        
        this.compiler = compiler;
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.currentModuleName = currentModuleTypeInfo.getModuleName();
        this.isAdjunct = isAdjunct;
    }
    
    private void generateBuiltInFunction(final QualifiedName functionName) {
        generateBuiltInFunction(functionName, null);
    }

    /**
     * Creates a CoreFunction for a built-in function. This is necessary
     * when built-in functions are applied to less than their full number of arguments. For example:
     * "main = sin 2;" works without having these extra supercombinator activation records, but
     * "main = mySin 2; mySin = sin;" fails with a missing "sin" label error. The solution is to create
     * core functions representing definitions of the form:
     * sin x = sin x;
     * This is not a recursive definition! The right hand side is fully applied, so it will be treated
     * as a primitive op by the code generator.
     *
     * Creation date: (2/22/01 11:02:56 AM)
     *
     * @param functionName name of the built-in function (assumed to belong to the current module)
     * @param maybeArgStrictness  strictness annotations for the built-in function. If null, all args are assumed to be strict
     *    and the function is assumed *not* to have a class context i.e. no dictionary args.
     */
    private void generateBuiltInFunction(final QualifiedName functionName, final boolean[] maybeArgStrictness) {
              
        final Function function = currentModuleTypeInfo.getFunction(functionName.getUnqualifiedName());       
      
        final int arity = function.getTypeExpr().getArity();       
        final int nTotalArgs;
        
        final boolean[] argStrictness;
        if (maybeArgStrictness == null) {            
            nTotalArgs = arity;
            argStrictness = new boolean[nTotalArgs];            
            //all parameters assumed to be strict
            Arrays.fill(argStrictness, true);
        } else {
            argStrictness = maybeArgStrictness;
            if (argStrictness.length >= arity) {
                // If >, Assume that there will be dictionary arguments
                nTotalArgs = argStrictness.length;
            } else {
                throw new IllegalArgumentException();
            }
        }
        String[] argNames = new String[nTotalArgs];
        TypeExpr[] argTypes = new TypeExpr[nTotalArgs];
        TypeExpr[] typePieces = function.getTypeExpr().getTypePieces();
        
        for (int argN = 0; argN < (nTotalArgs-arity); ++argN) {
            argNames[argN] = "$x" + argN;
        }
        
        for (int argN = (nTotalArgs-arity), start = argN; argN < nTotalArgs; ++argN) {            
            argNames[argN] = "$x" + argN;            
            argTypes[argN] = typePieces[argN - start];            
        }
        
        TypeExpr resultType = typePieces[typePieces.length-1];

        CoreFunction coreFunction = CoreFunction.makePrimitiveCoreFunction(function.getName(), argNames, argStrictness, argTypes, resultType, compiler.getCurrentModuleTimeStamp());
               
        //Record the number of actual arguments of the first expression.       
        Expression expr = new Expression.Var(function);

        for (int argN = 0; argN < arity; ++argN) {

            expr = new Expression.Appl(expr, new Expression.Var(QualifiedName.make(currentModuleName, "$x" + argN)));
        }

        coreFunction.setExpression(expr);
        
        storeCoreFunction(coreFunction);        
    }
    
    /**
     * Generate the expression for unsafeCoerce. Operationally it is just the identity function,
     * but it will not be accepted by the type checker so it can't be written directly in CAL.     
     */
    private void generateUnsafeCoerce() {
      
         QualifiedName qualifiedSCName = CAL_Prelude.Functions.unsafeCoerce;

         String[] argNames = new String[]{"$x"};
         //unsafeCoerce is strict.
         boolean[] argStrictness = new boolean[]{true};
         TypeExpr[] argTypes = new TypeExpr[]{TypeExpr.makeParametricType()};
         TypeExpr resultType = TypeExpr.makeParametricType();
         
         CoreFunction coreFunction = CoreFunction.makeCALCoreFunction(qualifiedSCName, argNames, argStrictness, argTypes, resultType, compiler.getCurrentModuleTimeStamp());
               
         coreFunction.setExpression(new Expression.Var(QualifiedName.make(currentModuleName, "$x")));
        
         storeCoreFunction(coreFunction);
     }   
    
    /**
     * Generate the expression for Prelude.if.  We need a functional form for this
     * so that it can be used in a lazy context.  The definition is if x y z = if x y z.
     * This is not a recursive definition! The right hand side is fully applied, so it will be treated
     * as a primitive op by the code generator.
     */
    private void generateFunctionalIf() {
        QualifiedName qualifiedSCName = QualifiedName.make(currentModuleName, "if");

        String[] argNames = new String[]{"$cond", "$then", "$else"};
        //unsafeCoerce is strict.
        boolean[] argStrictness = new boolean[]{true, false, false};
        TypeExpr typeVar = TypeExpr.makeParametricType();
        TypeExpr booleanType = compiler.getTypeChecker().getTypeConstants().getBooleanType();
        TypeExpr[] argTypes = new TypeExpr[] {booleanType, typeVar, typeVar};
        
        CoreFunction coreFunction = CoreFunction.makeCALCoreFunction (qualifiedSCName, argNames, argStrictness, argTypes, typeVar, compiler.getCurrentModuleTimeStamp());
              
        Expression left = new Expression.Var (qualifiedSCName);
        for (int i = 0; i < argNames.length; ++i) {
            Expression.Var var = new Expression.Var (QualifiedName.make(currentModuleName, argNames[i]));
            left = new Expression.Appl (left, var);
        }
        coreFunction.setExpression(left);
       
        storeCoreFunction(coreFunction);
    }

    /**
     * Creates a CoreFunction for a foreign function. This is necessary
     * when foreign functions are applied to less than their full number of arguments. 
     * @param function entity of the foreign function
     * @throws IllegalArgumentException if functionEntity is not a foreign function
     * @see #generateBuiltInFunction(QualifiedName)
     */
    private void generateForeignFunction (Function function) {
                            
        int arity = function.getTypeExpr().getArity();       
        
        String functionName = function.getName().getUnqualifiedName();

        QualifiedName qualifiedFunctionName = QualifiedName.make(currentModuleName, functionName);
        
        String[] argNames = new String[arity];
        boolean[] argStrictness = new boolean[arity];
        TypeExpr[] typePieces = function.getTypeExpr().getTypePieces();
        TypeExpr resultType = typePieces[typePieces.length-1];
        TypeExpr[] argTypes = new TypeExpr[arity];
        for (int argN = 0; argN < arity; ++argN) {
            //all parameters of foreign functions are strict
            argNames[argN] = "$x" + argN;
            argStrictness[argN] = true;
            argTypes[argN] = typePieces[argN];            
        }

        CoreFunction coreFunction = CoreFunction.makeForeignCoreFunction(qualifiedFunctionName, argNames, argStrictness, argTypes, resultType, compiler.getCurrentModuleTimeStamp());
               
        //Record the number of actual arguments of the first expression.       
        Expression expr = new Expression.Var(function);

        for (int argN = 0; argN < arity; ++argN) {

            expr = new Expression.Appl(expr, new Expression.Var(QualifiedName.make(currentModuleName, "$x" + argN)));
        }

        coreFunction.setExpression(expr);
        
        storeCoreFunction(coreFunction);
    }
    
    /**
     * Call when the core function has been fully initialized. It is now ready to be used for code generation
     * and packaging within a module.
     * @param coreFunction
     */
    private void storeCoreFunction(CoreFunction coreFunction) {
              
        Packager packager = compiler.getPackager();
        if (packager != null) {
            try {
                packager.store(coreFunction);
            } catch (Packager.PackagerException e) {
                // Couldn't package this supercombinator
                compiler.logMessage(new CompilerMessage(new MessageKind.Error.UnableToPackageSuperCombinator(this.toString())));
            }
        } else {
            // Otherwise emit an INFO error saying what we compiled
            compiler.logMessage(new CompilerMessage(coreFunction.dump()));
        }
    }

    /**
     * Converts a parse tree generated by antlr to our internal representations.
     * Creation date: (6/20/00 10:32:10 AM)
     * @param outerDefnListNode a ParseTreeNode for an outer defn list.     
     */
    void generateCode(ParseTreeNode outerDefnListNode) throws UnableToResolveForeignEntityException {

        try {
            Packager packager = compiler.getPackager();
            packager.switchModule(currentModuleName);
        } catch (Packager.PackagerException e) {
            // Error while packaging module {currentModuleName}.
            compiler.logMessage(new CompilerMessage(new MessageKind.Fatal.ErrorWhilePackagingModule(currentModuleName)));
        }
       
        if (!isAdjunct) {
            generatePreambleCode();
        }

        generateOuterDefnList(outerDefnListNode);
        
        if (ExpressionGenerator.GENERATE_LET_STATISTICS) { 
            ExpressionGenerator.totalNLetNonRecs += nLetNonRecs;
            ExpressionGenerator.totalNLetRecsMultiple += nLetRecsMultiple;
            ExpressionGenerator.totalNLetRecsSingle += nLetRecsSingle;
                   
            System.out.println("module " + currentModuleName);
            System.out.println("number of letNonRecs = " + nLetNonRecs);
            System.out.println("number of letRecsMultiple = " + nLetRecsMultiple);
            System.out.println("number of letRecsSingle = " + nLetRecsSingle);
            System.out.println("total of letNonRecs = " + ExpressionGenerator.totalNLetNonRecs);
            System.out.println("total of letRecsMultiple = " + ExpressionGenerator.totalNLetRecsMultiple);
            System.out.println("total of letRecsSingle = " + ExpressionGenerator.totalNLetRecsSingle + '\n');
        }
    }

    /**
     * Converts the various expression parse trees to our internal Expression representation.
     * This is a public method as it is used to generate expression form for the purposes of
     * syntax checking     
     * Creation date: (6/20/00 11:42:12 AM)
     * @param parseTree 
     * @return Expression     
     */
    private Expression generateExpr(ParseTreeNode parseTree) {

        int nodeType = parseTree.getType();

        switch (nodeType) {

            case CALTreeParserTokenTypes.VIRTUAL_LET_NONREC:
            {
                ParseTreeNode defnsNode = parseTree.firstChild();
                ParseTreeNode exprNode = defnsNode.nextSibling();
                //note we still need a list in defnsNode because the single definition may have a type
                //declaration so there are 2 items in the list.
                Expression.Let.LetDefn[] letDefnList = generateLetDefnList(defnsNode);
                if (letDefnList.length != 1) {
                    compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Fatal.MoreThanOneDefinitionInANonRecursiveLet()));    
                }
                
                if (ExpressionGenerator.GENERATE_LET_STATISTICS) {                
                    ++nLetNonRecs;                   
                }
                
                return new Expression.LetNonRec(letDefnList[0], generateExpr(exprNode));
            }
            
            case CALTreeParserTokenTypes.VIRTUAL_LET_REC:
            {
                ParseTreeNode defnsNode = parseTree.firstChild();
                ParseTreeNode exprNode = defnsNode.nextSibling();
                Expression.Let.LetDefn[] letDefnList = generateLetDefnList(defnsNode);
                
                if (ExpressionGenerator.GENERATE_LET_STATISTICS) {                
                    if(letDefnList.length == 1) {
                        ++nLetRecsSingle;                      
                    } else {
                        ++nLetRecsMultiple;                        
                    }
                }
                return new Expression.LetRec(letDefnList, generateExpr(exprNode));
            }

            case CALTreeParserTokenTypes.LAMBDA_DEFN :
            {
                // ExpressionGenerator: Internal Coding Error- unlifted lambda expression.
                compiler.logMessage(new CompilerMessage(parseTree, new MessageKind.Fatal.UnliftedLambdaExpression()));
                break;

                //todoBI should we delete the Expression.Lambda class?
            }

            case CALTreeParserTokenTypes.LITERAL_if :
            {
                ParseTreeNode conditionNode = parseTree.firstChild();
                ParseTreeNode ifTrueNode = conditionNode.nextSibling();
                ParseTreeNode ifFalseNode = ifTrueNode.nextSibling();

                //e = (@ if condition)
                Expression e = new Expression.Appl(new Expression.Var(QualifiedName.make(CAL_Prelude.MODULE_NAME, "if")), generateExpr(conditionNode));
                //returns (@ (@ (@ "if" condExpr) trueExpr) falseExpr)
                return new Expression.Appl(new Expression.Appl(e, generateExpr(ifTrueNode)), generateExpr(ifFalseNode));
            }

            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE:
            {
                return generateDataConstructorCaseExpr(parseTree);              
            }
            
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE:
            {
                return generateTupleCaseExpr(parseTree);
            }

            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE :
            {              
                return generateRecordCaseExpr(parseTree);                
            }

            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD :
            {
                return generateDataConstructorFieldSelection(parseTree);
            }
            
            case CALTreeParserTokenTypes.APPLICATION :
            {
                // Convert the list of subexpressions into a single subexpression by chaining application
                // subexpressions. If there's only one subexpression in the list then this is the result
                // otherwise, build nested applications
              
                ParseTreeNode firstArgNode = parseTree.firstChild();
                Expression firstArgExpr = generateExpr(firstArgNode);
                if (parseTree.hasExactlyOneChild()){
                    return firstArgExpr;
                }
                
                Expression applicationExpr = firstArgExpr;

                for (final ParseTreeNode exprNode : firstArgNode.nextSiblings()) {

                    applicationExpr = new Expression.Appl(applicationExpr, generateExpr(exprNode));
                }

                return applicationExpr;
            }

            // variables
            case CALTreeParserTokenTypes.QUALIFIED_VAR :
            {
                QualifiedName name = parseTree.toQualifiedName();
                
                // If this is an error call set the error information on the Var expression.
                if (name.equals(CAL_Prelude.Functions.error)) {
                    
                    ErrorInfo errorInfo = parseTree.getErrorInfoForErrorCall();
                    Expression.Var errorVar = new Expression.Var(errorInfo);
                    return new Expression.Appl(errorVar, errorInfo);
                    
                }
                
                if (name.getUnqualifiedName().indexOf('$') == -1) {
                    
                    //not an internal name
                    
                    FunctionalAgent entity = currentModuleTypeInfo.getReachableFunctionOrClassMethod(name);
                    if (entity != null) {
                        return new Expression.Var(entity);
                    }
                    
                    if (!isAdjunct) {
                        throw new IllegalStateException("non-adjunct top-level entities should have EnvEntities...");
                    }
                }
                
                //local function or argument name (does not correspond to a top-level construct.
                return new Expression.Var(name);                
            }

            // data constructors
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {       
                DataConstructor dc = getDataConstructor(parseTree.toQualifiedName());
                if (TypeExpr.isEnumType(dc.getTypeConstructor())) {
                    // We optimize enumeration data types by treating them as int.
                    return new Expression.Literal (Integer.valueOf(dc.getOrdinal()));
                } else {
                    return new Expression.Var(dc);
                }
            }

            // literals
            case CALTreeParserTokenTypes.INTEGER_LITERAL :
            case CALTreeParserTokenTypes.FLOAT_LITERAL :
            case CALTreeParserTokenTypes.CHAR_LITERAL :
            case CALTreeParserTokenTypes.STRING_LITERAL :
                return new Expression.Literal(generateLiteral(parseTree));

            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            {               
                if (parseTree.hasNoChildren()) {
                    // By definition the Unit data type is an enumeration type so we substitute
                    // an int.
                    DataConstructor dc = getDataConstructor(CAL_Prelude.DataConstructors.Unit);
                    return new Expression.Literal(Integer.valueOf(dc.getOrdinal()));
                }
                
                if (parseTree.hasExactlyOneChild()) {
                    // a parenthesized expression generates the same code as an expression
                    return generateExpr(parseTree.firstChild());
                }
                
                SortedMap<FieldName, Expression> extensionFieldsMap = new TreeMap<FieldName, Expression>(); 
                int componentN = 1;
                
                for (final ParseTreeNode componentExprNode : parseTree) {

                    extensionFieldsMap.put(FieldName.makeOrdinalField(componentN), generateExpr(componentExprNode));
                    ++componentN;
                }

                return new Expression.RecordExtension(null, extensionFieldsMap);                                              
            }

            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
            {
                List<Expression> elementExprList = new ArrayList<Expression>();

                for (final ParseTreeNode elementExprNode : parseTree) {

                    elementExprList.add(generateExpr(elementExprNode));
                }
                
                return generateListExpr(elementExprList);
            }
            
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR:
            {
                ParseTreeNode baseRecordNode = parseTree.firstChild();
                baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);           
                
                ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
                Expression baseRecordExpr;
                if (baseRecordExprNode != null) {
                    baseRecordExpr = generateExpr(baseRecordExprNode);                    
                } else {
                    baseRecordExpr = null;
                }
                
                ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
                fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);
                
                              
                SortedMap<FieldName, Expression> extensionFieldsMap = new TreeMap<FieldName, Expression>();               
                SortedMap<FieldName, Expression> updateFieldValuesMap = new TreeMap<FieldName, Expression>();
                
                for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
                    
                    fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION, CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
                    
                    ParseTreeNode fieldNameNode = fieldModificationNode.firstChild();
                   
                    FieldName fieldName = compiler.getTypeChecker().getFieldName(fieldNameNode);
                    
                    ParseTreeNode valueExprNode = fieldNameNode.nextSibling();                    
                    Expression valueExpr = generateExpr(valueExprNode);
                    
                    switch (fieldModificationNode.getType()) {
                        case CALTreeParserTokenTypes.FIELD_EXTENSION:
                        {
                            extensionFieldsMap.put(fieldName, valueExpr);
                            break;
                        }
                        case CALTreeParserTokenTypes.FIELD_VALUE_UPDATE:
                        {
                            updateFieldValuesMap.put(fieldName, valueExpr);
                            break;
                        }
                        default:
                        {
                            fieldModificationNode.unexpectedParseTreeNode();
                            break;
                        }
                    }                          
                }
                
                if (updateFieldValuesMap.isEmpty()) { 
                    
                    if (extensionFieldsMap.isEmpty() && baseRecordExpr != null) {
                        return baseRecordExpr;
                    }
                    
                    return new Expression.RecordExtension(baseRecordExpr, extensionFieldsMap);
                }
                
                if (extensionFieldsMap.isEmpty()) {
                    return new Expression.RecordUpdate(baseRecordExpr, updateFieldValuesMap);
                }
                
                return new Expression.RecordExtension(new Expression.RecordUpdate(baseRecordExpr, updateFieldValuesMap), extensionFieldsMap);
            }
            
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:
            {               
                //select a field from a record-valued expression e.g. r.field1.
                
                ParseTreeNode exprNode = parseTree.firstChild();
                Expression expr = generateExpr(exprNode);
                
                ParseTreeNode fieldNameNode = exprNode.nextSibling();
                FieldName fieldName = compiler.getTypeChecker().getFieldName(fieldNameNode);                
                
                return new Expression.RecordSelection(expr, fieldName);              
            } 
            
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:
            {
                return generateExpr(parseTree.firstChild());
            }

            //these operators should be replaced by their functional forms by this point.
            case CALTreeParserTokenTypes.BARBAR :
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND :
            case CALTreeParserTokenTypes.PLUSPLUS :
            case CALTreeParserTokenTypes.EQUALSEQUALS :
            case CALTreeParserTokenTypes.NOT_EQUALS :
            case CALTreeParserTokenTypes.GREATER_THAN :
            case CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS :
            case CALTreeParserTokenTypes.LESS_THAN :
            case CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS :
            case CALTreeParserTokenTypes.PLUS :
            case CALTreeParserTokenTypes.MINUS :
            case CALTreeParserTokenTypes.ASTERISK :
            case CALTreeParserTokenTypes.SOLIDUS :
            case CALTreeParserTokenTypes.PERCENT:
            case CALTreeParserTokenTypes.COLON :
            case CALTreeParserTokenTypes.UNARY_MINUS:
            case CALTreeParserTokenTypes.POUND:
            case CALTreeParserTokenTypes.DOLLAR:
            case CALTreeParserTokenTypes.BACKQUOTE:
            default :
            {
                parseTree.unexpectedParseTreeNode();                
                break;
            }
        }

        return null;
    }
    
    /**     
     * @param elementExprList (list of Expression objects)
     * @return Expression form of a CAL list literal with expression elements in elementExprList.
     */
    private Expression generateListExpr(List<Expression> elementExprList) {
        
        Expression applicationExpr = new Expression.Var(getDataConstructor(CAL_Prelude.DataConstructors.Nil));
        
        DataConstructor consDataConstructor = getDataConstructor(CAL_Prelude.DataConstructors.Cons);
        for (int i = elementExprList.size() - 1; i >= 0; --i) {
            
            Expression leftExpr = new Expression.Appl(new Expression.Var(consDataConstructor), elementExprList.get(i));
            applicationExpr = new Expression.Appl(leftExpr, applicationExpr);
        }
        
        return applicationExpr;
    }
     
    /**
     * Tuple-cases are a special type of record-case expression and are encoded as record-cases in the Expression format.
     * @param tupleCaseNode
     * @return Expression.RecordCase
     */
    private Expression.RecordCase generateTupleCaseExpr(ParseTreeNode tupleCaseNode) {
        
        tupleCaseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);

        ParseTreeNode conditionNode = tupleCaseNode.firstChild();
        Expression conditionExpr = generateExpr(conditionNode);

        ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);

        if (!altListNode.hasExactlyOneChild()) {
            //record-case patterns have only 1 alternative. This should be caught earlier in static analysis.
            throw new IllegalArgumentException();
        }

        ParseTreeNode altNode = altListNode.firstChild();
        altNode.verifyType(CALTreeParserTokenTypes.ALT);      
        
        ParseTreeNode patternNode = altNode.firstChild();
        patternNode.verifyType(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);
                        
        int componentN = 1;
        SortedMap<FieldName, String> extensionFieldsMap = new TreeMap<FieldName, String>();
        
        for (final ParseTreeNode patternVarNode : patternNode) {               
            
            String patternVarName = null;
            
            switch (patternVarNode.getType()) {
                case CALTreeParserTokenTypes.VAR_ID :
                {
                    patternVarName = patternVarNode.getText();                    
                    break;
                }

                case CALTreeParserTokenTypes.UNDERSCORE :
                {
                    patternVarName = Expression.RecordCase.WILDCARD_VAR;             
                    break;
                }

                default :
                {
                    patternVarNode.unexpectedParseTreeNode();                    
                    return null;
                }
            }
          
            extensionFieldsMap.put(FieldName.makeOrdinalField(componentN), patternVarName);           
            
            ++componentN;
        }   
       
        ParseTreeNode resultExprNode = patternNode.nextSibling();
        Expression resultExpr = generateExpr(resultExprNode);
        
        String baseRecordVarName = null;
        return new Expression.RecordCase(conditionExpr, baseRecordVarName, extensionFieldsMap, resultExpr);        
    }
    
    /**
     * Generates the Expression associated with a record-case.
     * This will have the form:           
     * case conditionExpr of recordPattern -> resultExpr;
     * 
     * @param recordCaseNode
     * @return Expression.RecordCase
     */
    private Expression.RecordCase generateRecordCaseExpr(ParseTreeNode recordCaseNode) {
      
        recordCaseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE);

        ParseTreeNode conditionNode = recordCaseNode.firstChild();
        Expression conditionExpr = generateExpr(conditionNode);

        ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);

        if (!altListNode.hasExactlyOneChild()) {
            //record-case patterns have only 1 alternative. This should be caught earlier in static analysis.
            throw new IllegalArgumentException();
        }

        ParseTreeNode altNode = altListNode.firstChild();
        altNode.verifyType(CALTreeParserTokenTypes.ALT);

        ParseTreeNode patternNode = altNode.firstChild();
        patternNode.verifyType(CALTreeParserTokenTypes.RECORD_PATTERN);
       
        ParseTreeNode baseRecordPatternNode = patternNode.firstChild();
        baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);

        ParseTreeNode baseRecordPatternVarNode = baseRecordPatternNode.firstChild();
        String baseRecordVarName = null;      
        if (baseRecordPatternVarNode != null) {

            //a record-polymorphic pattern

            switch (baseRecordPatternVarNode.getType()) {
                case CALTreeParserTokenTypes.VAR_ID :
                {
                    baseRecordVarName = baseRecordPatternVarNode.getText();                        
                    break;
                }

                case CALTreeParserTokenTypes.UNDERSCORE :
                {
                    baseRecordVarName = Expression.RecordCase.WILDCARD_VAR;                
                    break;
                }

                default :
                {
                    baseRecordPatternVarNode.unexpectedParseTreeNode();                    
                    return null;
                }
            }           

        }

        ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();
        SortedMap<FieldName, String> extensionFieldsMap = CALTypeChecker.getFieldBindingsMap(fieldBindingVarAssignmentListNode, null, true);
       
        ParseTreeNode resultExprNode = patternNode.nextSibling();
        Expression resultExpr = generateExpr(resultExprNode);
         
        return new Expression.RecordCase(conditionExpr, baseRecordVarName, extensionFieldsMap, resultExpr);
    }
    
    /**
     * Converts the parse tree for a Let definition to our internal representation.
     * Creation date: (6/20/00 10:32:10 AM)
     * @param parseTree
     * @return Expression.Let.LetDefn 
     */
    private Expression.Let.LetDefn generateLetDefn(ParseTreeNode parseTree) {

        parseTree.verifyType(CALTreeParserTokenTypes.LET_DEFN);

        ParseTreeNode optionalCALDocNode = parseTree.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        TypeExpr varType = optionalCALDocNode.getFunctionTypeForLocalFunctionCALDocComment();
        
        ParseTreeNode varNode = optionalCALDocNode.nextSibling();

        ParseTreeNode varListNode = varNode.nextSibling();
        if (varListNode.firstChild() != null) {
            //local functions should have all been lifted by now.
            throw new IllegalStateException("Internal coding error- cannot generate an Expression for a local function definition.");            
        }

        ParseTreeNode exprNode = varListNode.nextSibling();

        return new Expression.Let.LetDefn(varNode.getText(), generateExpr(exprNode), varType);
    }

    /**
     * Converts the parse tree for a series of Let definitions to our internal representation.
     * Creation date: (6/20/00 10:32:10 AM)
     * @param parseTree
     * @return Expression.Let.LetDefn []     
     */
    private Expression.Let.LetDefn[] generateLetDefnList(ParseTreeNode parseTree) {

        parseTree.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);

        List<LetDefn> defnsList = new ArrayList<LetDefn>();

        for (final ParseTreeNode defnNode : parseTree) {
                 
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {            
                defnsList.add(generateLetDefn(defnNode));
            }
        }

        Expression.Let.LetDefn[] defnsArray = new Expression.Let.LetDefn[defnsList.size()];
        defnsList.toArray(defnsArray);

        return defnsArray;
    }

    /**
     * A helper function for converting a literal in a parse tree node to an object.
     * Creation date: (6/20/00 11:59:46 AM)
     * @return Object
     * @param literalNode
     */
    private Object generateLiteral(ParseTreeNode literalNode) {
                   
        switch (literalNode.getType()) {
            case CALTreeParserTokenTypes.INTEGER_LITERAL :                       
            {  
                //first see if the literal has been stored as a precomputed value.                
                Number literal = literalNode.getLiteralValueForMaybeMinusIntLiteral();
                if (literal == null) {
                    throw new IllegalStateException("integer literals should be resolved at this point");                    
                }
                
                return literal;               
            }

            case CALTreeParserTokenTypes.FLOAT_LITERAL :
            {
                //first see if the literal has been stored as a precomputed value.                
                Object literal = literalNode.getLiteralValueForFloatLiteral();
                if (literal != null) {
                    return literal;
                }                
                
                // Return the value of the literal
                try {
                    // Return the value of a double
                    return new Double(literalNode.getText());
                } catch (NumberFormatException e) {
                    // We failed to parse the FLOAT_LITERAL as a double!
                    // ExpressionGenerator: unable to parse {literalNode.getText()} to a floating point literal.
                    compiler.logMessage(new CompilerMessage(literalNode, new MessageKind.Error.UnableToParseToFloatingPointLiteral(literalNode.getText())));
                }
                break;
            }

            case CALTreeParserTokenTypes.CHAR_LITERAL :
            {
                try {
                    char c = StringEncoder.unencodeChar(literalNode.getText());
                    return Character.valueOf(c);
                } catch (IllegalArgumentException e) {
                    //the encoded character did not have a valid format. This should never happen for CHAR_LITERAL
                    //values returned from the parser.
                    // ExpressionGenerator: unable to parse {literalNode.getText()} to a character literal.
                    compiler.logMessage(new CompilerMessage(literalNode, new MessageKind.Error.UnableToParseToCharacterLiteral(literalNode.getText())));
                }
                break;
            }
            
            case CALTreeParserTokenTypes.STRING_LITERAL:
            {
                String unencodedString = null;
                try {
                    unencodedString = StringEncoder.unencodeString(literalNode.getText()); 
                                        
                } catch (IllegalArgumentException e) {
                    //the encoded string did not have a valid format. This should never happen for stringLiteral
                    //values returned from the parser.
                        // ExpressionGenerator: unable to parse {literalNode.getText()} to a string literal.
                    compiler.logMessage(new CompilerMessage(literalNode, new MessageKind.Error.UnableToParseToStringLiteral(literalNode.getText())));
                }
                
                if (isStringLiteralTooLong(unencodedString)) {
                    //"The string literal {0} is too long." 
                    compiler.logMessage(
                        new CompilerMessage(
                            literalNode, new MessageKind.Error.StringLiteralTooLong(literalNode.getText())));    
                }

                return unencodedString;             
            }

            default :
            {
                literalNode.unexpectedParseTreeNode();                           
                break;
            }
        }

        return null;
    }
    
    /** 
     * Checks to see if the String literal is too long. The restriction here comes from the Java bytecode format
     * where String literals are encoded with a modified UTF-8, and the resulting byte array must have length <= 65535 bytes.
     * (see section 4.4.7 of the JVM spec).
     * <p>
     * This test is strictly speaking not necessary, since we will just get a bytecode verification error later.
     * However, the error message from the compiler is much friendlier.
     *    
     * @param unencodedString
     * @return true if the string literal is too long for encoding into Java bytecodes as a String constant. 
     */
    private static boolean isStringLiteralTooLong(final String unencodedString) {
        
        final int maxNModifiedUtf8Bytes = 65535;
        
        final int unicodeStringLength = unencodedString.length();       
        
        //Using modified UTF-8, each char will be converted to at most three bytes,
        //so if this test is true, we can dispense with counting the number of modified UTF-8 bytes.
        if (unicodeStringLength <= maxNModifiedUtf8Bytes / 3) {
            return false;
        }
        
        //Using modified UTF-8, each char will be converted to at least one byte,
        //so if this test is true, we can dispense with counting the number of modified UTF-8 bytes.      
        if (unicodeStringLength > maxNModifiedUtf8Bytes) {           
            return true;
        }
        
        //we can't just use unencodedString.getBytes("UTF-8").length because the JVM uses a modified
        //UTF-8 encoding. In particular, the JVM encoding maps the null char to 2 bytes and SMP characters
        //(which are mapped to 4 bytes in the UTF-8 standard) are not handled as true multi-char values.
   
        int nModifiedUtfBytes = 0;
        for (int i = 0; i < unicodeStringLength; ++i) {
            final char c = unencodedString.charAt(i);
            if (c >= '\u0001' && c <= '\u07FF') {
                ++nModifiedUtfBytes;
            } else if (c == '\u0000' || (c >= '\u0080' && c <= '\u07FF')) {
                nModifiedUtfBytes += 2;
            } else {
                nModifiedUtfBytes += 3;
            }
        }
        
        return nModifiedUtfBytes > maxNModifiedUtf8Bytes;             
    }

    /**
     * Creation date: (7/12/01 11:07:32 AM)
     * @param outerDefnListNode
     */
    private void generateOuterDefnList(ParseTreeNode outerDefnListNode) {

        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);

        for (final ParseTreeNode parseTree : outerDefnListNode) {

            switch (parseTree.getType()) {

                case CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN :
                    generateFunctionDefn(parseTree);
                    break;

                case CALTreeParserTokenTypes.DATA_DECLARATION :
                {
                    ParseTreeNode dataConsListNode = parseTree.getChild(4);
                    dataConsListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);
                    
                    for (final ParseTreeNode dataConsNode : dataConsListNode) {

                        dataConsNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);

                        ParseTreeNode dataConsNameNode = dataConsNode.getChild(2);
                        dataConsNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                        QualifiedName dataConsName = QualifiedName.make(currentModuleName, dataConsNameNode.getText());
                       
                        generatePack(getDataConstructor(dataConsName));
                    }

                    break;
                }
               
                case CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION :
                //Types created by foreign data declarations do not correspond to Pack instructions.
                //Values of this type can only be created by calls to foreign functions.
                case CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION:
                    break;

                case CALTreeParserTokenTypes.TYPE_CLASS_DEFN:
                case CALTreeParserTokenTypes.INSTANCE_DEFN:
                    //todoBI
                    break;

                case CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION :
                {
                    ParseTreeNode externalNameNode = parseTree.getChild(1);
                    externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);

                    ParseTreeNode functionNameNode = externalNameNode.nextSibling().nextSibling().firstChild();
                    functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);

                    String functionName = functionNameNode.getText();
                    Function function = currentModuleTypeInfo.getFunction(functionName);                         

                    generateForeignFunction (function);
                    break;
                }
                
                case CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION:
                {
                    break;
                }

                default :
                {
                    parseTree.unexpectedParseTreeNode();                                   
                    break;
                }
            }
        }
    }

    /**
     * Generates a Pack instruction.
     * In the Core language these take the form:
     * Cons = Pack {1, 2}; -- tag = 1 arity = 2
     * Nil = Pack {2, 0};
     * However, in the typed language, the tag can be any object.
     * Cons = Pack {"Cons", 2};
     * Nil = Pack {"Nil", 0};
     *
     * Creation date: (9/28/00 2:58:43 PM)
     * @param dataConstructor    
     */
    private void generatePack(DataConstructor dataConstructor) {

        CoreFunction coreFunction = 
            CoreFunction.makeDataConstructorCoreFunction(dataConstructor, compiler.getCurrentModuleTimeStamp());      
               
        Expression expr = new Expression.PackCons(dataConstructor);                            
              
        coreFunction.setExpression(expr);

        // We're done with this data constructor.
        storeCoreFunction(coreFunction);
    }
    
    /**
     * @param dataConsName name of a data constructor.
     * @return DataConstructor 
     */
    private DataConstructor getDataConstructor(QualifiedName dataConsName) { 
                                                               
        return currentModuleTypeInfo.getVisibleDataConstructor(dataConsName);            
    }

    /**
     * Generate preambles needed for the built-in functions.
     * Creation date: (4/3/01 1:03:25 PM)
     */
    private void generatePreambleCode() throws UnableToResolveForeignEntityException {
        
        if (currentModuleName.equals(CAL_Prelude_internal.MODULE_NAME)) {
                                      
            //Add instructions for the built-in functions that are implemented as primitives
            //in the run-time and need to support partial application.
            //Omitted here are "if", which is handled as a special case, the operators, which are replaced
            //by function primitives, and constant applicative forms.                      
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanInt); 
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsInt);
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.addInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.subtractInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.multiplyInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.divideInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.negateInt);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.remainderInt);
    
            //primitive functions for comparison and arithmetic with floats
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsFloat);
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.addFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.subtractFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.multiplyFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.divideFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.negateFloat);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.remainderFloat);
                     
            //primitive functions for comparison and arithmetic with doubles
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsDouble);
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.addDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.subtractDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.multiplyDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.divideDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.negateDouble);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.remainderDouble);
            
            // primitive functions for comparison and arithmetic with longs
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsLong);
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.addLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.subtractLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.multiplyLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.divideLong);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.negateLong);  
            generateBuiltInFunction(CAL_Prelude_internal.Functions.remainderLong);  
    
            // primitive functions for comparison with shorts
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsShort);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsShort);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanShort);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsShort);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanShort);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsShort);
    
            // primitive functions for comparison with bytes
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsByte);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsByte);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanByte);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsByte);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanByte);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsByte);
                  
            //primitive functions for char comparison
    
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsChar);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsChar);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanChar);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsChar);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanChar);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsChar);
    
            //record functions                                       
            generateBuiltInFunction(CAL_Prelude_internal.Functions.compareRecord, new boolean[]{true, true, true});
            generateBuiltInFunction(CAL_Prelude_internal.Functions.equalsRecord, new boolean[]{true, true, true});            
            generateBuiltInFunction(CAL_Prelude_internal.Functions.recordFromJListPrimitive, new boolean[]{true, true});
            generateBuiltInFunction(CAL_Prelude_internal.Functions.recordFromJMapPrimitive, new boolean[]{true, true});
            generateBuiltInFunction(CAL_Prelude_internal.Functions.recordToJListPrimitive, new boolean[]{true, true});             
            generateBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsRecord, new boolean[]{true, true, true});                       
            generateBuiltInFunction(CAL_Prelude_internal.Functions.recordTypeDictionary, new boolean[]{true, false});
            
            
            generateBuiltInFunction(CAL_Prelude_internal.Functions.objectToCalValue);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.calValueToObject, new boolean[] {false});
            generateBuiltInFunction(CAL_Prelude_internal.Functions.makeComparator);
            generateBuiltInFunction(CAL_Prelude_internal.Functions.makeEquivalenceRelation);
            generateBuiltInFunction(CAL_Prelude.Functions.makeCalFunction);
               
            generateBuiltInFunction(CAL_Prelude.Functions.eager);
                        
            generateBuiltInFunction(CAL_Prelude.Functions.seq, new boolean[]{true, false});
            generateBuiltInFunction(CAL_Prelude.Functions.deepSeq, new boolean[]{true, false});
            generateBuiltInFunction(CAL_Prelude_internal.Functions.ordinalValue, new boolean[]{true});
            
            generateBuiltInFunction(CAL_Prelude.Functions.error, new boolean[]{false, true});
            
            generateBuiltInFunction(CAL_Prelude_internal.Functions.executionContext);
            
            //unsafeCoerce is operationally just the identity function. We need to encode it specially because
            //it won't make it past the type checker!
            generateUnsafeCoerce();
            
            // This is the functional form of the conditional primitive.
            generateFunctionalIf();
            
        } else if (currentModuleName.equals(CAL_Dynamic_internal.MODULE_NAME)) {
           
            generateBuiltInFunction(CAL_Dynamic_internal.Functions.fieldValuesPrimitive);    
            generateBuiltInFunction(CAL_Dynamic_internal.Functions.recordFieldIndex);
            generateBuiltInFunction(CAL_Dynamic_internal.Functions.recordFieldTypePrimitive, new boolean[]{true, false, true});
            generateBuiltInFunction(CAL_Dynamic_internal.Functions.recordFieldValuePrimitive, new boolean[]{true, true});            
            generateBuiltInFunction(CAL_Dynamic_internal.Functions.appendRecordPrimitive, new boolean[]{true, true});
            generateBuiltInFunction(CAL_Dynamic_internal.Functions.insertOrdinalRecordFieldPrimitive, new boolean[]{true, true, false});
            generateBuiltInFunction(CAL_Dynamic_internal.Functions.insertTextualRecordFieldPrimitive, new boolean[]{true, true, false});  
            
        } else if (currentModuleName.equals(CAL_Record_internal.MODULE_NAME)) {
            
            generateBuiltInFunction(CAL_Record_internal.Functions.recordToJRecordValuePrimitive, new boolean[]{true, true}); 
            generateBuiltInFunction(CAL_Record_internal.Functions.strictRecordPrimitive);
            generateBuiltInFunction(CAL_Record_internal.Functions.fieldNamesPrimitive);
            generateBuiltInFunction(CAL_Record_internal.Functions.hasFieldPrimitive);   
             
            generateBuiltInFunction(CAL_Record_internal.Functions.buildListPrimitive, new boolean[]{true,true,true});
            generateBuiltInFunction(CAL_Record_internal.Functions.buildRecordPrimitive, new boolean[]{true,true,true});
            
        } else if (currentModuleName.equals(CAL_List_internal.MODULE_NAME)) {
            
            generateBuiltInFunction(CAL_List_internal.Functions.makeIterator);
            
        } else if (currentModuleName.equals(CAL_Exception_internal.MODULE_NAME)) {
            
            //primThrow and primCatch are both declared having all their arguments non-plinged in order to simplify the implementation
            //of their custom lecc functions. In fact, throw and catch are both strict in their first argument.
            generateBuiltInFunction(CAL_Exception_internal.Functions.primThrow, new boolean[] {false});
            generateBuiltInFunction(CAL_Exception_internal.Functions.primCatch, new boolean[] {false, false});
            
        } else if (currentModuleName.equals(CAL_QuickCheck_internal.MODULE_NAME)) { 
            
            generateBuiltInFunction(CAL_QuickCheck_internal.Functions.arbitraryRecordPrimitive, new boolean[]{true,true,true});
            generateBuiltInFunction(CAL_QuickCheck_internal.Functions.coarbitraryRecordPrimitive, new boolean[]{true,true,true});

        } else if (currentModuleName.equals(CAL_Debug_internal.MODULE_NAME)) {

            generateBuiltInFunction(CAL_Debug_internal.Functions.showRecord, new boolean[]{true, true});
            
        } else if (currentModuleName.equals(CAL_Bits_internal.MODULE_NAME))  {
            
            generateBuiltInFunction(CAL_Bits_internal.Functions.bitwiseAndInt);
            generateBuiltInFunction(CAL_Bits_internal.Functions.bitwiseOrInt);
            generateBuiltInFunction(CAL_Bits_internal.Functions.bitwiseXorInt);
            generateBuiltInFunction(CAL_Bits_internal.Functions.complementInt);
            generateBuiltInFunction(CAL_Bits_internal.Functions.shiftLInt);
            generateBuiltInFunction(CAL_Bits_internal.Functions.shiftRInt);
            generateBuiltInFunction(CAL_Bits_internal.Functions.shiftRUnsignedInt);

            generateBuiltInFunction(CAL_Bits_internal.Functions.bitwiseAndLong);
            generateBuiltInFunction(CAL_Bits_internal.Functions.bitwiseOrLong);
            generateBuiltInFunction(CAL_Bits_internal.Functions.bitwiseXorLong);
            generateBuiltInFunction(CAL_Bits_internal.Functions.complementLong);
            generateBuiltInFunction(CAL_Bits_internal.Functions.shiftLLong);
            generateBuiltInFunction(CAL_Bits_internal.Functions.shiftRLong);
            generateBuiltInFunction(CAL_Bits_internal.Functions.shiftRUnsignedLong);
            
        }
        
        generateDerivedForeignInputFunctions();
        
        generateTypeableInstanceFunctions();
    }
    
    /**
     * Every foreign type T that has a derived instance for Inputable defines a hidden function $inputT.
     * This hidden function is defined as follows:
     * 
     * if the foreign type T corresponds to one of the primitive unboxed Java types such as int
     * $inputT :: JObject -> T;
     * $inputT !x = Prelude.inputIntFromJObject x;
     * In particular, this will be recognized as an "alias" and treated simply as Prelude.inputIntFromJObject by the runtime.
     * The code shown above would not pass the type checker, which is part of the reason we're generating it directly in
     * the expression generator.
     * 
     * in the case where T corresponds to a Java object type, then     
     * $inputT !x = cast<T> x;
     * cast<T> is supported in the Expression format but not directly in the CAL language.     
     */
    private void generateDerivedForeignInputFunctions() throws UnableToResolveForeignEntityException {
                      
        final int nTypeConstructors = currentModuleTypeInfo.getNTypeConstructors();   
        
        for (int i = 0; i < nTypeConstructors; ++i) {
            
            //define left hand side of the CoreFunction definition            
            
            final TypeConstructor typeCons = currentModuleTypeInfo.getNthTypeConstructor(i);
            ForeignTypeInfo foreignTypeInfo = typeCons.getForeignTypeInfo();
            if (foreignTypeInfo != null &&
                typeCons.hasDerivingClause(CAL_Prelude.TypeClasses.Inputable)) {                
                
                final QualifiedName typeConsName = typeCons.getName();            
                final QualifiedName inputableInstanceFunctionName = ClassInstance.makeInternalInstanceMethodName("input", typeConsName);                               
                
                final String[] argNames = {"$x"};
                final boolean[] argStrictness = {true};
                                
                final TypeExpr[] argTypes = {TypeExpr.makeNonParametricType(currentModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.JObject))};
                final TypeExpr resultType = TypeExpr.makeNonParametricType(typeCons);
                
                //the "isPrimitive" flag is false because these are not declared via a primitive function declaration in a module.
                final CoreFunction coreFunction = CoreFunction.makeCALCoreFunction(inputableInstanceFunctionName, argNames, argStrictness, argTypes, resultType, compiler.getCurrentModuleTimeStamp());
                                
                //define the Expression part of the CoreFunction definition (i.e. the right hand side)            
                                                
                Expression expr = null;
                
                final Class<?> foreignClass = foreignTypeInfo.getForeignType();
                
                if (foreignClass.isPrimitive()) {
                    
                    FunctionalAgent castFunction;                
                    if (foreignClass == char.class) {
                        castFunction = getPreludeFunction("inputChar");   
                        
                    } else if (foreignClass == boolean.class) {
                        castFunction = getPreludeFunction("inputBoolean");
                        
                    } else if (foreignClass == byte.class) {
                        castFunction = getPreludeFunction("inputByte");
                        
                    } else if (foreignClass == short.class) {
                        castFunction = getPreludeFunction("inputShort");
                        
                    } else if (foreignClass == int.class) {
                        castFunction = getPreludeFunction("inputInt");
                        
                    } else if (foreignClass == long.class) {
                        castFunction = getPreludeFunction("inputLong");
                        
                    } else if (foreignClass == float.class) {
                        castFunction = getPreludeFunction("inputFloat");
                        
                    } else if (foreignClass == double.class) {
                        castFunction = getPreludeFunction("inputDouble");
                        
                    } else {
                        //we don't handle void.class- it is an earlier compilation error for this.
                        throw new IllegalStateException();
                    }
                    
                    expr =
                        new Expression.Appl(
                            new Expression.Var(castFunction),
                            new Expression.Var(QualifiedName.make(currentModuleName, "$x")));
                } else {
                    
                    expr = Expression.Cast.make(foreignClass, new Expression.Var(QualifiedName.make(currentModuleName, "$x")));
                }
                                                                                 
                coreFunction.setExpression(expr);        
                storeCoreFunction(coreFunction);                   
            }           
        }     
    }
    
    /**     
     * A helper function for retrieving private functions from the Prelude.
     * @param preludeFunctionName     
     * @return FunctionalAgent
     */
    private FunctionalAgent getPreludeFunction(String preludeFunctionName) {
        
        if (currentModuleName.equals(CAL_Prelude.MODULE_NAME)) {
            return currentModuleTypeInfo.getFunction(preludeFunctionName);
        } else {
            return currentModuleTypeInfo.getImportedModule(CAL_Prelude.MODULE_NAME).getFunction(preludeFunctionName);
        }        
    }
    
    /**
     * Every type T constructor is automatically an instance of the type class Typeable.
     * for example, there is an automatic instance declaration added by the compiler for:
     * 
     * instance (Typeable a, Typeable b) => Typeable (Either a b) where
     *     typeOf = $typeOfEither;
     *     ;
     * 
     * this function defines the hidden internal function $typeOfEither etc.
     * 
     * Note: unlike the derived instance functions, the instance functions such as $typeOfEither
     * are defined directly as core functions and do not go through type-checking. This is because
     * we implement them more efficiently than would be possible as CAL functions that need to pass
     * the type-checking phase.          
     */
    private void generateTypeableInstanceFunctions() throws UnableToResolveForeignEntityException {
              
        //typeOfEither was previously defined in the Prelude as:
        //
        //typeOfEither :: (Typeable a, Typeable b) => Either a b -> TypeRep; 
        //private typeOfEither x =
        //    let
        //        leftType :: Either a b -> a;
        //        leftType = undefined;
        //        rightType :: Either a b -> b;
        //        rightType = undefined;
        //    in
        //        TypeRep "Cal.Core.Prelude.Either" [typeOf (leftType x), typeOf (rightType x)];
        //
        //we now internally can give a more efficient core language definition (since we
        //don't need the local variables to get past the type checker. Note that however we
        //do the dictionary resoloution ourselves!
        //
        //private $typeOfEither $dict1 $dict2 $x = 
        //   Prelude.TypeRep "Cal.Core.Prelude.Either" [Prelude.typeOf $dict1 Prelude.undefined, Prelude.typeOf $dict2 Prelude.undefined];  
        //
        //because Typeable has a single class method, with no superclasses, Prelude.typeOf $dict1 simple reduces to $dict1, so
        //we actually generate this reduced version.
        //private $typeOfEither $dict1 $dict2 $x = 
        //   Prelude.TypeRep "Cal.Core.Prelude.Either" [$dict1 Prelude.undefined, $dict2 Prelude.undefined];  
        //
        //There are special cases for some of the data constructors of TypeRep.
        //
        //private $typeOfList $dict1 $x  = Prelude.ListTypeRep (Prelude.typeOf $dict1 Prelude.undefined);
        //private $typeOfUnit $x = Prelude.UnitTypeRep;
        //private $typeOfFunction $dict1 $dict2 = Prelude.FunctionTypeRep (Prelude.typeOf $dict1 Prelude.undefined) ((Prelude.typeOf $dict2 Prelude.undefined)
        //private $typeOfBoolean $x = Prelude.BooleanTypeRep;
        //private $typeOfInt $x = Prelude.IntTypeRep;
        //private $typeOfByte $x = Prelude.ByteTypeRep;
        //private $typeOfShort $x = Prelude.ShortTypeRep;
        //private $typeOfLong $x = Prelude.LongTypeRep;
        //private $typeOfFloat $x = Prelude.FloatTypeRep;
        //private $typeOfDouble $x = Prelude.DoubleTypeRep;
        //private $typeOfChar $x = Prelude.CharTypeRep;
        //private $typeOfString $x = Prelude.StringTypeRep;
                        
        final ModuleTypeInfo currentModuleTypeInfo = compiler.getPackager().getModuleTypeInfo(currentModuleName);    
        final int nTypeConstructors = currentModuleTypeInfo.getNTypeConstructors();
        if (nTypeConstructors == 0) {
            return;
        }
        
        //the TypeRep type (and its data constructors) are defined in the Prelude module. Many of these data constructors
        //are private so we need to query for them directly from the Prelude's ModuleTypeInfo
        final boolean isPreludeModule = currentModuleName.equals(CAL_Prelude.MODULE_NAME);
        ModuleTypeInfo preludeModuleTypeInfo;
        if (currentModuleName.equals(CAL_Prelude.MODULE_NAME)) {
            preludeModuleTypeInfo = currentModuleTypeInfo;            
        } else {        
            preludeModuleTypeInfo = currentModuleTypeInfo.getImportedModule(CAL_Prelude.MODULE_NAME);                              
        }        
               
        final Function undefinedFunction = preludeModuleTypeInfo.getFunction("undefined");
        TypeExpr resultType = TypeExpr.makeNonParametricType(preludeModuleTypeInfo.getTypeConstructor("TypeRep"));
        
        for (int i = 0; i < nTypeConstructors; ++i) {
            
            //define left hand side of the CoreFunction definition            
            
            final TypeConstructor typeCons = currentModuleTypeInfo.getNthTypeConstructor(i);  
            final QualifiedName typeConsName = typeCons.getName();            
            final QualifiedName typeOfInstanceFunctionName = ClassInstance.makeInternalInstanceMethodName("typeOf", typeConsName);
            
            final int typeArity = typeCons.getTypeArity();
            
            final int arity = typeArity + 1;
            
            final String[] argNames = new String[arity];
            for (int j = 0; j < typeArity; ++j) {
                argNames[j] = "$dict" + j;
            }
            argNames[typeArity] = "$x";
            
            //all args are non-strict
            final boolean[] argStrictness = new boolean[arity];
            Arrays.fill(argStrictness, false);
            
            //all types are non-determined and so null. Note: potentially we could give a type to the last element (the $x in the example)
            //but there is not advantage to that since it is not a strict argument.
            final TypeExpr[] argTypes = new TypeExpr[arity];
            
            final CoreFunction coreFunction = CoreFunction.makeCALCoreFunction (typeOfInstanceFunctionName, argNames, argStrictness, argTypes, resultType, compiler.getCurrentModuleTimeStamp());
            
            
            //define the Expression part of the CoreFunction definition (i.e. the right hand side)            
            
            //[$dict1 Prelude.undefined, $dict2 Prelude.undefined, ...]            
            final List<Expression> elementExprList = new ArrayList<Expression>(typeArity); //List of Expression
            for (int j = 0; j < typeArity; ++ j) {
                elementExprList.add(
                    new Expression.Appl(                        
                        new Expression.Var(QualifiedName.make(currentModuleName, argNames[j])),
                        new Expression.Var(undefinedFunction)));
            }
            final Expression listExpr = generateListExpr(elementExprList);
            
            Expression expr = null;
            
            //There is special handling for some types in the Prelude for efficiency reasons.
            if (isPreludeModule) {
                               
                if (typeConsName.equals(CAL_Prelude.TypeConstructors.Function)) {
                    
                    //private $typeOfFunction $dict1 $dict2 = Prelude.FunctionTypeRep (Prelude.typeOf $dict1 Prelude.undefined) ((Prelude.typeOf $dict2 Prelude.undefined)
                    expr =
                        new Expression.Appl(
                            new Expression.Appl(
                                new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.FunctionTypeRep.getUnqualifiedName())),
                                elementExprList.get(0)),
                            elementExprList.get(1));
                    
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.List)) {
                    
                    //private $typeOfList $dict1 $x = Prelude.ListTypeRep (Prelude.typeOf $dict1 Prelude.undefined);
                    expr = 
                        new Expression.Appl(
                            new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.ListTypeRep.getUnqualifiedName())),
                            elementExprList.get(0));
                    
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Unit)) {                    
                    //private $typeOfUnit $x = Prelude.UnitTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.UnitTypeRep.getUnqualifiedName()));
                    
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Boolean)) {
                    //private $typeOfBoolean $x = Prelude.BooleanTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.BooleanTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Int)) {
                    //private $typeOfInt $x = Prelude.IntTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.IntTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Byte)) {
                    //private $typeOfByte $x = Prelude.ByteTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.ByteTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Short)) {
                    //private $typeOfShort $x = Prelude.ShortTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.ShortTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Long)) {
                    //private $typeOfLong $x = Prelude.LongTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.LongTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Float)) {
                    //private $typeOfFloat $x = Prelude.FloatTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.FloatTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Double)) {
                    //private $typeOfDouble $x = Prelude.DoubleTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.DoubleTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Char)) {
                    //private $typeOfChar $x = Prelude.CharTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.CharTypeRep.getUnqualifiedName()));
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.String)) {
                    //private $typeOfString $x = Prelude.StringTypeRep;
                    expr = new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.StringTypeRep.getUnqualifiedName()));
                }
            }

            //Handle the Prelude types not treated above, as well as non-Prelude types.
            //There are some special cases here as well.
            if (expr == null) {                
               
                ForeignTypeInfo foreignTypeInfo = typeCons.getForeignTypeInfo();
                if (foreignTypeInfo != null) {
                    
                    final Class<?> foreignType = foreignTypeInfo.getForeignType();
                    
                    //private $typeOf<?> $x = Prelude.ForeignTypeRep "<?>" (Prelude.executionContext_getForeignClass $ec "<?>" "<foreign name as returned by Class.getName()>");
                    expr =
                        new Expression.Appl(
                            new Expression.Appl( 
                                new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.ForeignTypeRep.getUnqualifiedName())),
                                new Expression.Literal(typeConsName.getQualifiedName())),
                                new Expression.Appl(
                                    new Expression.Appl(
                                        new Expression.Appl(
                                            new Expression.Var(preludeModuleTypeInfo.getFunction(CAL_Prelude_internal.Functions.executionContext_getForeignClass.getUnqualifiedName())),
                                            new Expression.Var(preludeModuleTypeInfo.getFunction(CAL_Prelude_internal.Functions.executionContext.getUnqualifiedName()))),
                                        new Expression.Literal(typeConsName.getQualifiedName())),
                                    new Expression.Literal(foreignType.getName())));                        
                                
                } else {                                                           
                    //private $typeOfEither $dict0 $dict1 $x =
                    //    Prelude.AlgebraicTypeRep "Cal.Core.Prelude.Either" (Prelude.listToTypeReps [$dict1 Prelude.undefined, $dict2 Prelude.undefined]);
                    expr =
                        new Expression.Appl(
                            new Expression.Appl(
                                new Expression.Var(preludeModuleTypeInfo.getDataConstructor(CAL_Prelude_internal.DataConstructors.AlgebraicTypeRep.getUnqualifiedName())),
                                new Expression.Literal(typeConsName.getQualifiedName())),
                            new Expression.Appl(
                                new Expression.Var(preludeModuleTypeInfo.getFunction(CAL_Prelude_internal.Functions.listToTypeReps.getUnqualifiedName())),
                                listExpr));                    
                }
            } 
            
            if (expr == null) {
                throw new IllegalStateException();
            }
                                 
            coreFunction.setExpression(expr);        
            storeCoreFunction(coreFunction);   
        }     
    }
   
    /**
     * Converts a function parse tree generated by antlr to our internal representations.
     * Creation date: (6/20/00 10:32:10 AM)
     * @param functionNode
     */
    private void generateFunctionDefn(ParseTreeNode functionNode) {

        functionNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);
        ParseTreeNode functionNameNode = functionNode.getChild(2);
        String functionName = functionNameNode.getText();
        
        /*
        //this is a good place to dump the parse tree of a function immediately
        //prior to expression generation.
                 
        if (functionName.equals("overloadingTest21")) {
            try {
            
            functionNode.xmlDumpToFile("d:\\dev\\" + functionName + ".xml");
            } catch (java.io.IOException e) {
                System.out.println(e);
            }
        }
        */

        // Collect formal arguments and add to activation record
        ParseTreeNode paramListNode = functionNameNode.nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
        final int nArgs = paramListNode.getNumberOfChildren();
        int argN = 0;
        // This is used to figure out what the 'return' type of the expression is. There
        // was a problem where "Sql.toTypedExpr :: Expr -> TypedExpr a" defined as 
        // toTypedExpr = TypedExpr. The old code just called TypeExpr.getReturnType. The
        // correct way is to remove the types corresponding to explicit arguments.
        int explicitListedArgN = 0;
        // The type of the dictionary and lifted arguments are not in the result type of the expression. 
        // For all other cases, the types are args are in the result type. The count is needed to 
        // adjust the code that determines the return type of the function after all the args 
        // are applied.
        int implicitArgN = 0;
        String [] argNames = new String[nArgs];
        boolean [] argStrictness = new boolean[nArgs];
        TypeExpr [] argTypes = new TypeExpr[nArgs];
        
        for (final ParseTreeNode varNode : paramListNode) {

            String argName = varNode.getText();
            if (argName.indexOf('$') == -1) {
                //at this point all arguments must have unique names, or be internal (start with a $).
                //this is a sanity check.
                throw new IllegalStateException();
            }
            argNames[argN] = argName;
            
            argStrictness[argN] = varNode.getType() == CALTreeParserTokenTypes.STRICT_PARAM;
           
            TypeExpr argType = compiler.getTypeChecker().getFunctionBoundNameType(argName);
            if (argType == null && argName.charAt(0) != '$') {
                //the only arguments without types should be the internal arguments
                throw new IllegalStateException();
            }
            explicitListedArgN++;
            if (argType == null || varNode.getIsLiftedArgument()){
                implicitArgN += explicitListedArgN;
                explicitListedArgN = 0;
            }
            argTypes[argN] = argType;
                       
            ++argN;
        }
        
        QualifiedName qualifiedFunctionName = QualifiedName.make(currentModuleName, functionName);

        TypeExpr resultType = null;
        if (nArgs > 0 &&
            functionName.startsWith(ClassInstance.DICTIONARY_FUNCTION_PREFIX) &&
            argNames[nArgs - 1].equals(OverloadingResolver.DICTIONARY_FUNCTION_INDEX)) {
            
            //dictionary functions such as 
            //$dictEq#Maybe dictVar !$i = ...
            //don't go through type checking, but nevertheless we know that the index
            //argument $i is strict and of Int type. We set this so that the runtime can unbox this
            //argument.
            
            argTypes[nArgs - 1] = compiler.getTypeChecker().getTypeConstants().getIntType();
            resultType = TypeExpr.makeParametricType();
        }
        else{
            TypeExpr functionType = compiler.getTypeChecker().getFunctionBoundNameType(functionName);
            if (functionType == null){
                // Lambda expressions are not found in the bound names map. 
                functionType = paramListNode.getTypeExprForFunctionParamList();
            }

            /**
             * The dictionary and lifted arguments do not show up in the type expression. Account for this 
             * when determining the return type.
             */
            if (implicitArgN > 0){
                TypeExpr[] typePieces = functionType.getTypePieces(argTypes.length - implicitArgN); 
                resultType = typePieces[typePieces.length - 1];                
             }
            else{
//                resultType = functionType.getResultType();
                /**
                 * Strip off the types associated with the arguments. This is done because the 
                 * return type effectively returns the last type in the list. This works find except for
                 * functions defined like, 
                 * 
                 *      map = prelude.map;
                 *      
                 * There are no arguments to map and the return type is set to [a] and not the correct type.
                 */

                TypeExpr[] typePieces = functionType.getTypePieces(nArgs);
                resultType = typePieces[typePieces.length - 1];
            }

        }

        // Copy the types making sure that type var remain synchronized.
        {
            TypeExpr[] argAndResultTypes = new TypeExpr[argTypes.length + 1];
            for(int i = 0; i < argTypes.length; ++i){
                argAndResultTypes[i] = argTypes[i];
            }
            argAndResultTypes[argTypes.length] = resultType;
            
            argAndResultTypes = TypeExpr.copyTypeExprs(argAndResultTypes);
            
            for(int i = 0; i < argTypes.length; ++i){
                argTypes[i] = argAndResultTypes[i];
            }
            resultType = argAndResultTypes[argTypes.length];
        }        
        
        CoreFunction coreFunction = CoreFunction.makeCALCoreFunction(qualifiedFunctionName, argNames, argStrictness, argTypes, resultType, compiler.getCurrentModuleTimeStamp());
       
        ParseTreeNode exprNode = paramListNode.nextSibling();

        // Compile code generated by expression processing
        // Invoke the SC compilation scheme with the outermost expression
        // Code is compiled into the current code contribution (supercombinator)
        Expression expr = generateExpr(exprNode);
        
        coreFunction.setExpression(expr);

        // We're done with this supercombinator. 
        storeCoreFunction(coreFunction);
    }

    /**
     * Converts the AST for a list of switches within a Case expression to our internal representation.
     * @return Expression.Switch.SwitchAlt[]
     * @param altListNode
     */
    private Expression.Switch.SwitchAlt[] generateSwitchAlts(ParseTreeNode altListNode) {
        
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
        
        List<SwitchAlt> altsList = new ArrayList<SwitchAlt>();
        
        for (final ParseTreeNode altNode : altListNode) {
            
            altNode.verifyType(CALTreeParserTokenTypes.ALT);
            
            ParseTreeNode patternNode = altNode.firstChild();
            ParseTreeNode exprNode = patternNode.nextSibling();
            
            Expression generatedExpr = generateExpr(exprNode);
            
            switch (patternNode.getType()) {
                
                case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR :
                {
                    ParseTreeNode qualifiedConsNameListNode = patternNode.firstChild();
                    
                    // Gather the tags for this pattern.
                    List<Object> altTags = new ArrayList<Object>();
                    for (final ParseTreeNode qualifiedConsNameNode : qualifiedConsNameListNode) {
                        
                        QualifiedName dataConsName = qualifiedConsNameNode.toQualifiedName();
                        DataConstructor dataCons = getDataConstructor(dataConsName);
                        altTags.add(dataCons);
                    }
                    
                    ParseTreeNode argBindingsNode = qualifiedConsNameListNode.nextSibling();
                    
                    // Add alts for the bound vars.
                    switch (argBindingsNode.getType()) {
                        
                        case CALTreeParserTokenTypes.PATTERN_VAR_LIST:
                        {
                            altsList.add(new Expression.Switch.SwitchAlt.Positional(altTags, getPositionToVarNameMap(argBindingsNode), generatedExpr));
                            break;
                        }
                        case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST:
                        {
                            Map<FieldName, String> fieldBindingsMap = CALTypeChecker.getFieldBindingsMap(argBindingsNode, null, false);
                            
                            if (fieldBindingsMap != null) {
                                altsList.add(new Expression.Switch.SwitchAlt.Matching(altTags, fieldBindingsMap, generatedExpr));
                            }
                            break;
                        }
                        default:
                        {
                            patternNode.unexpectedParseTreeNode();           
                            break;
                        }
                    }
                    
                    break;
                }
                
                case CALTreeParserTokenTypes.VIRTUAL_UNIT_DATA_CONSTRUCTOR :
                {                                   
                    Object altTag = getDataConstructor(CAL_Prelude.DataConstructors.Unit);
                    altsList.add(new Expression.Switch.SwitchAlt.Matching(altTag, NO_MATCHING_FIELDS, generatedExpr));
                    break;
                }
                
                case CALTreeParserTokenTypes.LIST_CONSTRUCTOR : 
                {               
                    Object altTag = getDataConstructor(CAL_Prelude.DataConstructors.Nil);
                    altsList.add(new Expression.Switch.SwitchAlt.Matching(altTag, NO_MATCHING_FIELDS, generatedExpr));
                    break;
                }
                
                case CALTreeParserTokenTypes.COLON : 
                {
                    Object altTag = getDataConstructor(CAL_Prelude.DataConstructors.Cons);
                    ParseTreeNode generalizedVarListNode = patternNode;
                    altsList.add(new Expression.Switch.SwitchAlt.Positional(altTag, getPositionToVarNameMap(generalizedVarListNode), generatedExpr));
                    break;
                }
                
                case CALTreeParserTokenTypes.UNDERSCORE:            
                {
                    String altTag = Expression.Switch.SwitchAlt.WILDCARD_TAG;
                    altsList.add(new Expression.Switch.SwitchAlt.Positional(altTag, NO_POSITIONAL_FIELDS, generatedExpr));
                    break;
                }
                
                case CALTreeParserTokenTypes.INT_PATTERN :
                {
                    ParseTreeNode maybeMinusIntListNode = patternNode.firstChild();
                    maybeMinusIntListNode.verifyType(CALTreeParserTokenTypes.MAYBE_MINUS_INT_LIST);
                    
                    List<Object> altTags = new ArrayList<Object>();
                    for (final ParseTreeNode maybeMinusIntNode : maybeMinusIntListNode) {
                        
                        Integer altTag = (Integer)maybeMinusIntNode.getLiteralValueForMaybeMinusIntLiteral();
                        if (altTag == null) {
                            throw new IllegalStateException();
                        }
                        
                        altTags.add(altTag);
                    }
                    
                    altsList.add(new Expression.Switch.SwitchAlt.Matching(altTags, NO_MATCHING_FIELDS, generatedExpr));
                    break;
                }
                
                case CALTreeParserTokenTypes.CHAR_PATTERN :
                {
                    ParseTreeNode charListNode = patternNode.firstChild();
                    charListNode.verifyType(CALTreeParserTokenTypes.CHAR_LIST);
                    
                    List<Object> altTags = new ArrayList<Object>();
                    for (final ParseTreeNode charLiteralNode : charListNode) {
                        
                        Character altTag = charLiteralNode.getCharacterValueForCharLiteral();
                        if (altTag == null) {
                            throw new IllegalStateException();
                        }
                        
                        altTags.add(altTag);
                    }
                    
                    altsList.add(new Expression.Switch.SwitchAlt.Matching(altTags, NO_MATCHING_FIELDS, generatedExpr));
                    break;
                }
                
                case CALTreeParserTokenTypes.INTEGER_LITERAL :
                {
                    //this node does not currently arise in user-written code, but does occur in the
                    //hidden dictionary functions.
                    Object altTag = generateLiteral(patternNode);
                    if (!(altTag instanceof Integer)) {
                        throw new IllegalStateException();
                    }
                    altsList.add(new Expression.Switch.SwitchAlt.Matching(altTag, NO_MATCHING_FIELDS, generatedExpr));
                    break;
                }
                
                default :
                {
                    patternNode.unexpectedParseTreeNode();           
                    break;
                }
            }
        }
        
        Expression.Switch.SwitchAlt[] altsArray = new Expression.Switch.SwitchAlt[altsList.size()];
        return altsList.toArray(altsArray);
    }
    
    /**
     * Generates the Expression format for a data-constructor based switch expression.
     * @param parseTree
     * @return Expression.Switch
     */
    private Expression.Switch generateDataConstructorCaseExpr (ParseTreeNode parseTree) {        
        parseTree.verifyType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE);        
        
        ParseTreeNode exprNode = parseTree.firstChild();
        ParseTreeNode altListNode = exprNode.nextSibling();
        Expression conditionExpr = generateExpr(exprNode);
        
        Expression.Switch.SwitchAlt[] altsArray = generateSwitchAlts(altListNode);
        
        return new Expression.Switch(conditionExpr, altsArray, parseTree.getErrorInfoForErrorCall());
    }
   
    /**
     * Generates the Expression format for a data-constructor field selection.
     * @param parseTree
     * @return Expression.DataConsSelection
     */
    private Expression generateDataConstructorFieldSelection(ParseTreeNode parseTree) {
        parseTree.verifyType(CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD);
        
        ParseTreeNode exprNode = parseTree.firstChild();
        ParseTreeNode dcNameNode = exprNode.nextSibling();
        ParseTreeNode fieldNameNode = dcNameNode.nextSibling();       
        
        Expression conditionExpr = generateExpr(exprNode);
        QualifiedName dataConsName = dcNameNode.toQualifiedName();

        DataConstructor dataConstructor = getDataConstructor(dataConsName);  
        
        FieldName fieldName = FieldName.make(fieldNameNode.getText());
        int fieldIndex = dataConstructor.getFieldIndex(fieldName);
        
        if (fieldIndex < 0) {
            throw new IllegalStateException("Internal coding error- cannot generate an Expression for a non-existent field name.");
        }              

        return new Expression.DataConsSelection(conditionExpr, dataConstructor, fieldIndex, parseTree.getErrorInfoForErrorCall());
    }

    /**
     * A helper function that converts the AST for a list of vars to mappings for those vars.
     * @return (Integer->String) map from position to var name for all used alt vars.
     * @param generalizedVarListNode the parent of the nodes representing var ids or underscores.
     */
    private SortedMap<Integer, String> getPositionToVarNameMap(ParseTreeNode generalizedVarListNode) {

        SortedMap<Integer, String> positionToVarNameMap = new TreeMap<Integer, String>();
        
        int index = 0;

        for (final ParseTreeNode patternVarNode : generalizedVarListNode) {
                
            switch (patternVarNode.getType()) {
                case CALTreeParserTokenTypes.VAR_ID:
                {
                    positionToVarNameMap.put(Integer.valueOf(index), patternVarNode.getText());
                    break;
                }
                
                case CALTreeParserTokenTypes.UNDERSCORE:                    
                {
                    break;
                }
                
                default:
                {
                    patternVarNode.unexpectedParseTreeNode();                                   
                    break;
                }
            }
            index++;
        }

        return positionToVarNameMap;
    }   
}
