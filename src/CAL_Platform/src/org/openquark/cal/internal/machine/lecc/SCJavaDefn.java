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
 * SCJavaDefn.java
 * Created: Feb 2, 2003
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.lecc;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.ExpressionAnalyzer;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ForeignFunctionInfo;
import org.openquark.cal.compiler.ForeignTypeInfo;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.StringEncoder;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeVar;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.Expression.DataConsSelection;
import org.openquark.cal.compiler.Expression.FieldValueData;
import org.openquark.cal.compiler.Expression.Switch.SwitchAlt;
import org.openquark.cal.internal.javamodel.JavaClassRep;
import org.openquark.cal.internal.javamodel.JavaExceptionHandler;
import org.openquark.cal.internal.javamodel.JavaExpression;
import org.openquark.cal.internal.javamodel.JavaFieldDeclaration;
import org.openquark.cal.internal.javamodel.JavaModelCopier;
import org.openquark.cal.internal.javamodel.JavaModelTraverser;
import org.openquark.cal.internal.javamodel.JavaOperator;
import org.openquark.cal.internal.javamodel.JavaStatement;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.Assignment;
import org.openquark.cal.internal.javamodel.JavaExpression.CastExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassInstanceCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalName;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.Nameable;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation.InvocationType;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression.Binary;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression.Ternary;
import org.openquark.cal.internal.javamodel.JavaStatement.AssertStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.Block;
import org.openquark.cal.internal.javamodel.JavaStatement.ExpressionStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.IfThenElseStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.LineComment;
import org.openquark.cal.internal.javamodel.JavaStatement.LocalVariableDeclaration;
import org.openquark.cal.internal.javamodel.JavaStatement.ReturnStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.ThrowStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement.IntCaseGroup;
import org.openquark.cal.internal.machine.BasicOpTuple;
import org.openquark.cal.internal.machine.CodeGenerationException;
import org.openquark.cal.internal.machine.CondTuple;
import org.openquark.cal.internal.machine.ConstructorOpTuple;
import org.openquark.cal.internal.machine.lecc.JavaDefinitionBuilder.SCDefinitionBuilder.LECCLiftedLetVarMachineFunction;
import org.openquark.cal.internal.machine.primitiveops.PrimOps;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Boolean;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Byte;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Char;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Double;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Float;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Int;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Integer;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Long;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Short;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_String;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.services.Assert;
import org.openquark.cal.util.ArrayStack;
import org.openquark.util.General;



/**
 * An SCJavaDefn represents a supercombinator definition which can have either
 * Java source or bytecode emitted.
 * @author RCypher
 */
final class SCJavaDefn {

    private static final String LITERAL_PREFIX = "$L";
    static final String TAIL_RECURSION_LOOP_LABEL = "TRLoop";
    static final QualifiedName PRELUDE_SEQ = CAL_Prelude.Functions.seq;

    //
    // zero-argument methods
    //
    // RTValue:     public RTValue prevArg();
    static final MethodInfo PREVARG = new MethodInfo("prevArg", JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);

    // RTValue:     public RTValue getArgValue();
    static final MethodInfo GETARGVALUE = new MethodInfo("getArgValue", JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);

    // RTValue:     public RTValue evaluate() throws CALExecutorException;
    static final MethodInfo EVALUATE = new MethodInfo("evaluate", JavaTypeNames.RTEXECUTION_CONTEXT, JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);

    // RTVAlue:  public int getOrdinal();
    private static final MethodInfo GETORDINALVALUE = new MethodInfo("getOrdinalValue", JavaTypeName.INT, MethodInvocation.InvocationType.VIRTUAL);

    //
    // single argument methods
    //
    // RTFunction:  public RTValue apply(RTValue argument, RTValue arg2, ...);
    private static final MethodInfo APPLY = new MethodInfo("apply", JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);

    // RTValue:     public abstract RTValue setResult (RTValue result) throws RTValue.Exception;
    private static final MethodInfo SETRESULT = new MethodInfo("setResult", JavaTypeNames.RTVALUE, JavaTypeName.VOID, InvocationType.VIRTUAL);

    /** (Class->Class) CAL Literal class to RTKernel handler class.
     * Since this map is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initialization block it will need to be synchronized.*/
    private static final Map<Class<?>, Class<?>> literalMap = new HashMap<Class<?>, Class<?>>();
    static {
        literalMap.put(Integer.class, CAL_Int.class);
        literalMap.put(Double.class, CAL_Double.class);
        literalMap.put(Boolean.class, CAL_Boolean.class);
        literalMap.put(Byte.class, CAL_Byte.class);
        literalMap.put(Short.class, CAL_Short.class);
        literalMap.put(Float.class, CAL_Float.class);
        literalMap.put(Long.class, CAL_Long.class);
    }


    /** The expression for the SC body */
    private final Expression body;

    /** The generated code for the body */
    private Block bodyCode = null;
    private Block unboxedBodyCode = null;

    /** List of ExceptionBlock */
    private List<JavaExceptionHandler> exceptionInfo = new ArrayList<JavaExceptionHandler>();

    /** The DataConstructor iff this is a constructor, not an SC. */
    private final DataConstructor dataConstructor;

    /** The module containing the entity this SCJavaDefn object is associated with. */
    private final LECCModule module;

    private String instanceName;

    /** Name of the RTExecutionContext variable in the generated code.  NOTE: if this name is changed
     * CodeGenerator.CODEGEN_VERSION must be incremented as well.
     */
    static final String EXECUTION_CONTEXT_NAME = "$ec";

    static final MethodVariable EXECUTION_CONTEXT_VAR = new MethodVariable (SCJavaDefn.EXECUTION_CONTEXT_NAME);

    /** The level of nested case statements. */
    private int nestedCaseLevel = 0;

    /** Flag indicating that the SC associated with this SCJavaDefn has strict arguments */
    private boolean hasStrictArguments;

    /** boolean flags indicating the strictness of the SC arguments. */
    private final boolean argumentStrictness[];

    /** The types of the function arguments.  Some or all members of this array may be null. */
    private final TypeExpr argumentTypes[];

    /** The result type of the function. */
    private final TypeExpr resultType;

    /** The number of arguments for this function */
    private final int nArguments;

    /** The names of the arguments for this function.
     * These are the CAL names produced by the compiler. */
    private final String argumentNames[];

    /** The Java names for the arguments to this function.
     * These are the names that will be used in the generated
     * Java code.
     */
    private final String javaArgumentNames[];

    /** The name of the containing module. */
    private final ModuleName currentModuleName;

    /** The qualified name of the SC. */
    private final String functionName;
    private final QualifiedName qualifiedName;

    /** True if this is a tail recursive function. */
    private boolean isTailRecursive = false;

    /** Set of String.  Names of strongly connected components. */
    private final Set<String> connectedComponents;

    /** Flag indicating that this function is foreign. */
    private final boolean isForeign;

    /** Flag indicating that this function is primitive. */
    private final boolean isPrimitive;

    private final JavaTypeName thisTypeName;

    /** Object to collect code generation info.  May be null.*/
    private final CodeGenerationStats codeGenerationStats;

    /**
     * Values shared across multiple SC definitions.  Referenced SCs, literal values, etc.
     */
    private final SharedValues sharedValues;

    private final Map<ReturnStatement, Set<VarInfo>> returnStatementToLocalVars =
        new HashMap<ReturnStatement, Set<VarInfo>>();

    /**
     * Create an SCJavaDefn from a MachineFunction
     * @param label
     * @param body
     * @param module
     * @param codeGenerationStats - object to collect code generation info.  May be null.
     * @param sharedValues
     * @throws CodeGenerationException
     */
    SCJavaDefn(MachineFunction label,
               Expression body,
               LECCModule module,
               CodeGenerationStats codeGenerationStats,
               SharedValues sharedValues) throws CodeGenerationException {
        if (label == null || module == null || body == null) {
            throw new IllegalArgumentException ("Unable to create SCJavaDefn due to null argument.");
        }

        if (LECCMachineConfiguration.isLeccRuntimeStatic() && label.isCodeGenerated()) {
            throw new CodeGenerationException ("Code already generated for: " + label.getQualifiedName());
        }

        //this.scar = scar;
        this.body = body;
        this.module = module;

        this.functionName = label.getName();

        // R is a pack constructor?
        Expression.PackCons packCons = body.asPackCons();
        dataConstructor = (packCons != null) ? packCons.getDataConstructor() : null;

        this.nArguments = label.getArity();
        this.argumentNames = new String[this.nArguments];
        System.arraycopy(label.getParameterNames(), 0, this.argumentNames , 0, this.argumentNames.length);
        this.javaArgumentNames = new String[this.nArguments];

        if (LECCMachineConfiguration.IGNORE_STRICTNESS_ANNOTATIONS) {

            this.argumentStrictness = new boolean [nArguments];
            Arrays.fill (this.argumentStrictness, false);
            this.hasStrictArguments = false;

        } else {

            this.argumentStrictness = label.getParameterStrictness();
            for (int i = 0; i < argumentStrictness.length; ++i) {
                if (argumentStrictness[i]) {
                    this.hasStrictArguments = true;
                    break;
                }
            }
        }

        this.argumentTypes = label.getParameterTypes();
        this.resultType = label.getResultType();
        this.currentModuleName = module.getName();
        this.isTailRecursive = label.isTailRecursive();
        this.connectedComponents = label.getStronglyConnectedComponents();
        this.isPrimitive = label.isPrimitiveFunction();
        this.isForeign = label.isForeignFunction();
        this.thisTypeName = CALToJavaNames.createTypeNameFromSC(QualifiedName.make(getModuleName(), getFunctionName()), module);
        this.qualifiedName = QualifiedName.make(getModuleName(), getFunctionName());
        this.codeGenerationStats = codeGenerationStats;
        this.sharedValues = sharedValues;
    }


    /**
     * @return - true if this SC has strict arguments.
     */
    boolean hasStrictArguments() {
        return hasStrictArguments;
    }

    /**
     * @return - true if this SC has strict arguments of an unboxable type.
     */
    boolean hasStrictUnboxableArguments () throws CodeGenerationException {
        if (!hasStrictArguments()) {
            return false;
        }

        for (int i = 0; i < argumentTypes.length; ++i) {
            if (SCJavaDefn.canTypeBeUnboxed(argumentTypes[i]) && argumentStrictness[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param mf
     * @return - true if this SC has strict arguments of an unboxable type.
     */
    private boolean hasStrictUnboxableArguments (MachineFunction mf) throws CodeGenerationException {
        TypeExpr paramTypes[] = mf.getParameterTypes();
        boolean paramStrictness[] = mf.getParameterStrictness();

        for (int i = 0, n = paramTypes.length; i < n; ++i) {
            if (SCJavaDefn.canTypeBeUnboxed(paramTypes[i]) && paramStrictness[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param n
     * @return - true if the nth argument is strict.
     */
    boolean isArgStrict(int n) {
        return argumentStrictness[n];
    }

    /**
     * @param name
     * @return true if the named argument is strict.
     */
    private boolean isArgStrict (String name) {
        for (int i = 0; i < argumentNames.length; ++i) {
            if (argumentNames[i].equals(name)) {
                return isArgStrict(i);
            }
        }

        return false;
    }

    /**
     * @param i
     * @return return the Java name for the ith argument to this function.
     */
    String getJavaArgumentName(int i) {
        return javaArgumentNames[i];
    }

    /**
     * @return boolean array indicating the strictness of the function arguments.
     */
    boolean[] getArgumentStrictness () {
        return argumentStrictness;
    }

    /**
     * @param n
     * @return boolean indicating the strictness of the nth argument.
     */
    boolean getArgumentStrictness (int n) {
        return argumentStrictness[n];
    }

    /**
     * Build up a list of KernelLiteral object corresponding to all
     * referenced literals in the function body.
     * @throws CodeGenerationException
     */
    private void buildLiteralsList () throws CodeGenerationException {

        List<Expression.Literal> allLiterals = body.getLiterals();

        for (final Expression.Literal literal : allLiterals) {

            Object literalObject = literal.getLiteral();
            // Force creation of the literal value.
            sharedValues.addKernelLiteral(literalObject, thisTypeName);
        }
    }

    /**
     * The SC scheme deals with entire supercombinators.
     * This version generates a function which returns boxed values.
     * @return StatementBlock the resulting code contribution (SC body)
     * @throws CodeGenerationException
     */
    Block genS_SC_Boxed() throws CodeGenerationException {
        genS_SC();
        return bodyCode;
    }

    /**
     * The SC scheme deals with entire supercombinators.
     * This version generates a function which returns unboxed values.
     * @return StatementBlock the resulting code contribution (SC body), may be null.
     * @throws CodeGenerationException
     */
    Block genS_SC_Unboxed() throws CodeGenerationException {
        if (!SCJavaDefn.canTypeBeUnboxed(resultType)) {
            return null;
        }

        if (unboxedBodyCode == null) {

            // First we need to generate the body code for the boxed case.
            genS_SC();

            // Now try to do a copy/transformation of the boxed body code.
            // This may generate a null pointer exception.
            UnboxedReturnCopier copier = new UnboxedReturnCopier(SCJavaDefn.typeExprToTypeName(resultType));
            try {
                unboxedBodyCode = (JavaStatement.Block)bodyCode.accept(copier, null);
            } catch (UnboxingTransformationError e) {
                // Unable to transform function body to directly return an unboxed value.
                // Fall back on calling fNS.

                Block body = new Block();

                JavaExpression argValues[] = new JavaExpression[getArity() + 1];
                JavaTypeName argTypes[] = new JavaTypeName[argValues.length];

                argValues[argValues.length-1] = SCJavaDefn.EXECUTION_CONTEXT_VAR;

                Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
                argTypes[argTypes.length-1] = JavaTypeNames.RTEXECUTION_CONTEXT;

                for (int i = 0, n = getArity(); i < n; ++i) {
                    argValues[i] = new JavaExpression.MethodVariable(getJavaArgumentName(i));
                    if (isArgStrict(i) && SCJavaDefn.canTypeBeUnboxed(getArgumentType(i))) {
                        argTypes[i] = typeExprToTypeName(getArgumentType(i));
                    }
                }

                LECCModule.FunctionGroupInfo fgi = module.getFunctionGroupInfo(getQualifiedName());
                String functionName = fgi.getFnNamePrefix(getFunctionName()) + "f";
                if (getArity() > 0) {
                    functionName = functionName + getArity() + "S";
                }

                MethodInvocation fNS =
                    new MethodInvocation.Instance(
                            null,
                            functionName,
                            argValues,
                            argTypes,
                            JavaTypeNames.RTVALUE,
                            MethodInvocation.InvocationType.VIRTUAL);

                LocalVariable result = new LocalVariable("$result", JavaTypeNames.RTVALUE);
                LocalVariableDeclaration resultDecl = new LocalVariableDeclaration(result, fNS);

                body.addStatement(resultDecl);

                for (int i = 0, n = getArity(); i < n; ++i) {
                    if (!isArgStrict(i) || !SCJavaDefn.canTypeBeUnboxed(getArgumentType(i))) {
                        // Null out the argument value.
                        Assignment nullOut =
                            new Assignment((JavaExpression.Nameable)argValues[i], LiteralWrapper.NULL);
                        body.addStatement(new ExpressionStatement(nullOut));
                    }
                }

                MethodInvocation eval = createInvocation(result, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                JavaExpression unbox = SCJavaDefn.unboxValue(SCJavaDefn.typeExprToTypeName(resultType), eval);

                body.addStatement(new ReturnStatement(unbox));

                unboxedBodyCode = body;
            }

        }

        return unboxedBodyCode;
    }

    /**
     * @return the JavaTypeName corresponding to the result type of this function.
     */
    JavaTypeName getResultType () throws CodeGenerationException {
        return SCJavaDefn.typeExprToTypeName(resultType);
    }

    TypeExpr getResultTypeExpr () {
        return resultType;
    }

    /**
     * The SC scheme deals with entire supercombinators.
     * @throws CodeGenerationException
     */
    private void genS_SC() throws CodeGenerationException {
        if (bodyCode == null) {
            // Set up the literal values.
            buildLiteralsList ();

            if (codeGenerationStats != null) {
                codeGenerationStats.incrementSCArity(nArguments);
            }

            bodyCode = new Block(exceptionInfo);

            // Generate the root (SC) variable scope
            VariableContext variableContext = new VariableContext();

            // Add the arguments to the variable scope and deal with strictness, boxing, etc.
            for (int i = 0; i < nArguments; ++i) {
                // Add each of the arguments to the variable scope.
                VarInfo vi = variableContext.addArgument(QualifiedName.make(currentModuleName, argumentNames[i]), isArgStrict(i), getArgumentTypeName(i), getArgumentType(i));
                this.javaArgumentNames[i] = vi.getJavaName();
            }

            // Generate the body contribution
            JavaStatement bodyStatement = genS_R(body, variableContext);
            // Emit let variables
            bodyCode.addStatement(variableContext.popJavaScope());

            // Emit the body
            bodyCode.addStatement(new LineComment("Top level supercombinator logic"));
            bodyCode.addStatement(bodyStatement);

            if (LECCMachineConfiguration.sourcecodeSpaceOptimization()) {
                bodyCode = (JavaStatement.Block)releaseVars(bodyCode, null);
            }
        }
    }

    Block genS_LetVarDef(Scheme scheme, LECCLiftedLetVarMachineFunction mf) throws CodeGenerationException {
        if (bodyCode == null) {
            // Set up the literal values.
            buildLiteralsList ();

            if (codeGenerationStats != null) {
                codeGenerationStats.incrementSCArity(nArguments);
            }

            Expression methodBody = body;

            // If the body of this 'function' is an application of Prelude.eager' we want
            // to strip it off and treat this as strict.
            Expression eagerStripped = stripEager(methodBody);
            if (eagerStripped != methodBody) {
                methodBody = eagerStripped;
                if (scheme == Scheme.C_SCHEME) {
                    scheme = Scheme.E_SCHEME;
                }
            }

            bodyCode = new Block(exceptionInfo);

            // Generate the root (SC) variable scope
            VariableContext variableContext = new VariableContext();

            // Add the arguments to the variable scope and deal with strictness, boxing, etc.
            for (int i = 0; i < nArguments; ++i) {
                // Add each of the arguments to the variable scope.
                VarInfo vi = variableContext.addArgument(QualifiedName.make(currentModuleName, argumentNames[i]), isArgStrict(i), getArgumentTypeName(i), getArgumentType(i));
                this.javaArgumentNames[i] = vi.getJavaName();
            }

            if (canIgnoreLaziness(body, variableContext)) {
                mf.setIgnoreLaziness(true);
            }


            Expression expressions[] = flattenTopLevelSeq(methodBody);
            if (expressions != null && (scheme == Scheme.E_SCHEME || scheme == Scheme.UNBOX_INTERNAL_SCHEME)) {
                for (int i = 0, n = expressions.length - 1; i < n; ++i) {
                    JavaStatement js = makeStrictStatementFromSeqArg(expressions[i], variableContext);
                    bodyCode.addStatement(js);
                }

                methodBody = expressions[expressions.length - 1];
            }


            ExpressionContextPair ecp;
            if (scheme == Scheme.E_SCHEME) {
                ecp = genS_E(methodBody, variableContext);
            } else
            if (scheme == Scheme.C_SCHEME) {
                ecp = genS_C(methodBody, variableContext);
            } else
            if (scheme == Scheme.UNBOX_INTERNAL_SCHEME) {
                ecp = generateUnboxedArgument(typeExprToTypeName(resultType), methodBody, variableContext);
            } else {
                throw new CodeGenerationException("Invalid compilation scheme for let variable definition.");
            }

            // Emit let variables
            bodyCode.addStatement(variableContext.popJavaScope());

            bodyCode.addStatement(ecp.getContextBlock());
            if (ecp.getJavaExpression() != null) {
                bodyCode.addStatement(new JavaStatement.ReturnStatement(ecp.getJavaExpression()));
            }

            if (LECCMachineConfiguration.sourcecodeSpaceOptimization()) {
                bodyCode = (JavaStatement.Block)releaseVars(bodyCode, null);
            }
        }

        return bodyCode;
    }

    /**
     * This method attempts to generate code to produce an unboxed value
     * by calling the fUnboxednS method of a supercombinator.
     * @param e
     * @param unboxedType
     * @param scheme
     * @param variableContext
     * @return The generated code if successful, null otherwise.
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildUnboxedDirectCall (
            Expression e,
            JavaTypeName unboxedType,
            Scheme scheme,
            VariableContext variableContext) throws CodeGenerationException {

        if (!LECCMachineConfiguration.DIRECT_UNBOXED_RETURNS) {
            return null;
        }

        // Is e a call to a let variable definition function?
        ExpressionContextPair letVarECP = buildLetVarDefCall(e, Scheme.UNBOX_INTERNAL_SCHEME, variableContext);
        if (letVarECP != null) {
            return letVarECP;
        }

        // We have to be dealing with an application.
        if (e.asAppl() == null) {
            return null;
        }

        // Break the application chain down into its components.
        Expression[] chain = appChain (e.asAppl());
        if (chain[0].asVar() == null) {
            return null;
        }

        // If the function is a supercombinator we can potentially optimize the
        // representation.
        Expression.Var var = (Expression.Var)chain[0];
        if (var.getDataConstructor() != null) {
            return null;
        }
        if (variableContext.isLocalVariable(var.getName())) {
            return null;
        }

        // Try to collect information about the called function.
        // This is used to decide how to encode the function call.
        MachineFunction mf = module.getFunction(var.getName());
        if (mf == null) {
            return null;
        }

        // Only functions with an unboxable return type implement an
        // fUnboxed method.
        if (!SCJavaDefn.canTypeBeUnboxed(mf.getResultType())) {
            return null;
        }

        // We only generate fUnboxed methods under the same conditions we generate the fNL and fNS methods.
        boolean generateFnMethods = mf.getArity() > 0
        && (mf.getArity() <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH
                || mf.isTailRecursive() || hasStrictUnboxableArguments(mf));

        if (!generateFnMethods) {
            return null;
        }

        // It must be a fully saturated application.
        if (mf.getArity() != chain.length -1 || mf.getArity() == 0) {
            return null;
        }

        // We don't 'unbox' our internal type.
        JavaTypeName calledReturnType = SCJavaDefn.typeExprToTypeName(mf.getResultType());
        if (calledReturnType.equals(JavaTypeNames.RTVALUE) ||
            calledReturnType.equals(JavaTypeName.CAL_VALUE)) {
            return null;
        }

        // If the primitiveness of the called function result type is not the same
        // as the primitiveness of the desired unboxed type we can't do a direct call.
        verifyUnboxType(unboxedType, calledReturnType);

        //  Get information about the arguments of the called function.
        boolean[] calledArgStrictness = mf.getParameterStrictness();
        TypeExpr[] calledArgTypes = mf.getParameterTypes();
        int calledArity = mf.getArity();

        if (!canCallDirectlyOnJavaStack (chain[0].asVar(), calledArgTypes, scheme)) {
            return null;
        }

        // First generate the java expressions for the argument values.
        ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length];
        for (int i = 0; i < chain.length; ++i) {
            if (i > 0 && calledArgStrictness[i-1]) {
                if (SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i-1])) {
                    ecp[i] = generateUnboxedArgument(typeExprToTypeName(calledArgTypes[i-1]), chain[i], variableContext);
                } else {
                    ecp[i] = genS_E(chain[i], variableContext);
                }
            } else {
                ecp[i] = genS_C(chain[i], variableContext);
            }
        }

        // Consolidate the context of the arguments.
        Block newContext = ecp[0].getContextBlock();
        for (int i = 1; i < chain.length; ++i) {
            newContext.addStatement(ecp[i].getContextBlock());
        }

        // Get the supercombinator we're calling.
        JavaExpression root = ecp[0].getJavaExpression();

        if (this.codeGenerationStats != null) {
            if (var.getForeignFunctionInfo() != null) {
                this.codeGenerationStats.incrementDirectForeignCalls();
            } else {
                this.codeGenerationStats.incrementDirectSCCalls();
            }
        }

        // Optimize Prelude.not to use the '!' java operator.
        if (var.getName().equals(CAL_Prelude.Functions.not)) {
            JavaExpression not = new JavaExpression.OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, ecp[1].getJavaExpression());
            return new ExpressionContextPair (not, newContext);
        }


        JavaExpression[] args = new JavaExpression[calledArity + 1];
        for (int i = 0; i < calledArity; ++i) {
            args[i] = ecp[i + 1].getJavaExpression();
        }
        args[calledArity] = EXECUTION_CONTEXT_VAR;

        // Get the supercombinator class and do a direct application of the fn function.
        // arg types are all RTValue except when strict
        JavaTypeName[] argTypes = new JavaTypeName[args.length];
        for (int i = 0; i < calledArgTypes.length; ++i) {
            if (calledArgStrictness[i] && SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i])) {
                argTypes[i] = SCJavaDefn.typeExprToTypeName(calledArgTypes[i]);
            } else {
                argTypes[i] = JavaTypeNames.RTVALUE;
            }
        }
        argTypes[calledArity] = JavaTypeNames.RTEXECUTION_CONTEXT;

        root = expressionVarToJavaDef(var, Scheme.E_SCHEME, variableContext);

        // Get the method name to call.
        LECCModule.FunctionGroupInfo fgi = module.getFunctionGroupInfo(var.getName());
        String functionName = fgi.getFnNamePrefix(var.getName().getUnqualifiedName()) + "fUnboxed";
        if (calledArity > 0) {
            functionName = functionName + calledArity + "S";
        }

        root = new MethodInvocation.Instance(root, functionName, args, argTypes, calledReturnType, InvocationType.VIRTUAL);

        return new ExpressionContextPair(root, newContext);

    }

    /**
     * This method determines if an expression can be compiled strictly
     * even though it is in a lazy context.
     * @param e
     * @param variableContext
     * @return whether the expression can be compiled strictly.
     * @throws CodeGenerationException
     */
    private boolean canIgnoreLaziness (Expression e, VariableContext variableContext) throws CodeGenerationException {
        if (!LECCMachineConfiguration.OPTIMIZE_LAZY_SUPERCOMBINATORS) {
            return false;
        }

        // Basically we return true for anthing that is already at WHNF.

        if (e.asLiteral() != null) {
            return true;
        }

        // If a var is a non-zero arity SC, a DC, or is already evaluated we can shortcut it.
        if (e.asVar() != null) {
            Expression.Var var = e.asVar();

            if (var.getDataConstructor() != null) {
                return true;
            }
            if (variableContext.isVarPreEvaluated(var.getName())) {
                return true;
            }
            if (!variableContext.isLocalVariable(var.getName())) {
                // This is an SC
                MachineFunction mf = module.getFunction(var.getName());
                if (mf != null && mf.getArity() > 0) {
                    return true;
                }

            }
        }

        // We can shortcut a basic op if it is marked as eager
        // arguments can all be shortcut.
        // Also if the op is Prelude.eager we can shortcut.
        BasicOpTuple basicOpExpressions = BasicOpTuple.isBasicOp(e);
        if (basicOpExpressions != null) {
            if (basicOpExpressions.getPrimitiveOp() == PrimOps.PRIMOP_EAGER) {
                return true;
            }

            QualifiedName opName = basicOpExpressions.getName();
            MachineFunction mf = module.getFunction(opName);
            if (mf != null && mf.canFunctionBeEagerlyEvaluated()) {
                int nArgs = basicOpExpressions.getNArguments();
                int nWHNFArgs = 0;
                for (int i = 0; i < nArgs; ++i) {
                    Expression eArg = basicOpExpressions.getArgument(i);
                    if (canIgnoreLaziness(eArg, variableContext)) {
                        nWHNFArgs++;
                    }
                }
                if (nArgs == nWHNFArgs) {

                    String unqualifiedOpName = opName.getUnqualifiedName();
                    if (opName.getModuleName().equals(CAL_Prelude.MODULE_NAME) &&
                        (unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideLong.getUnqualifiedName()) ||
                         unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderLong.getUnqualifiedName()) ||
                         unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideInt.getUnqualifiedName()) ||
                         unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderInt.getUnqualifiedName()) ||
                         unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideShort.getUnqualifiedName()) ||
                         unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderShort.getUnqualifiedName()) ||
                         unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideByte.getUnqualifiedName()) ||
                         unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderByte.getUnqualifiedName()))) {

                        // Check that the second argument is a non zero literal.

                        Expression arg = basicOpExpressions.getArgument(1);
                        if (arg.asLiteral() != null) {

                            if (unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideLong.getUnqualifiedName()) ||
                                unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderLong.getUnqualifiedName())) {

                                Long l = (Long)arg.asLiteral().getLiteral();
                                return l.longValue() != 0;

                            } else if (unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideInt.getUnqualifiedName()) ||
                                unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderInt.getUnqualifiedName())) {

                                Integer i = (Integer)arg.asLiteral().getLiteral();
                                return i.intValue() != 0;

                            } else if (unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideShort.getUnqualifiedName()) ||
                                unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderShort.getUnqualifiedName())) {

                                Short shortValue = (Short)arg.asLiteral().getLiteral();
                                return shortValue.shortValue() != 0;

                            } else if (unqualifiedOpName.equals(CAL_Prelude_internal.Functions.divideByte.getUnqualifiedName()) ||
                                unqualifiedOpName.equals(CAL_Prelude_internal.Functions.remainderByte.getUnqualifiedName())) {

                                Byte byteValue = (Byte)arg.asLiteral().getLiteral();
                                return byteValue.byteValue() != 0;

                            } else {
                                throw new IllegalStateException();
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }

        basicOpExpressions = BasicOpTuple.isAndOr (e);
        if (basicOpExpressions != null) {

            // Code a basic operation
            int nArgs = basicOpExpressions.getNArguments ();
            int nWHNFArgs = 0;
            for (int i = 0; i < nArgs; ++i) {
                Expression eArg = basicOpExpressions.getArgument(i);
                if (canIgnoreLaziness(eArg, variableContext)) {
                    nWHNFArgs++;
                }
            }
            if (nArgs == nWHNFArgs) {
                return true;
            }
        }

        // If e is a fully saturated application of a function tagged for optimization and
        // all the arguments can be shortcutted we can shortcut the application.
        if (e.asAppl() != null) {
            Expression[] chain = appChain(e.asAppl());
            if (chain[0].asVar() != null) {
                // Get the supercombinator on the left end of the chain.
                Expression.Var scVar = chain[0].asVar();
                if (scVar != null) {
                    // Check if this supercombinator is one we should try to optimize.
                    MachineFunction mf = module.getFunction(scVar.getName());
                    if (mf != null && mf.canFunctionBeEagerlyEvaluated()) {

                        // Now determine the arity of the SC.
                        int calledArity = mf.getArity();

                        // Check to see if we can shortcut all the arguments.
                        if (chain.length - 1 == calledArity) {
                            int nWHNFArgs = 0;
                            for (int i = 0; i < calledArity; ++i) {
                                if (canIgnoreLaziness(chain[i+1], variableContext)) {
                                    nWHNFArgs++;
                                }
                            }
                            if (nWHNFArgs == calledArity) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        // Is e an application of a zero arity constructor.
        if (ConstructorOpTuple.isConstructorOp(e, true) != null) {
            ConstructorOpTuple  constructorOpExpressions = ConstructorOpTuple.isConstructorOp(e, false);

            DataConstructor dc = constructorOpExpressions.getDataConstructor ();

            if (dc.getArity() == 0){
                return true;
            }
        }

        // Is e a DataConsFieldSelection where the data constructor expression can be shortcutted and
        // the field is strict.
        if (e.asDataConsSelection() != null) {
            Expression.DataConsSelection dcs = (Expression.DataConsSelection)e;
            if (dcs.getDataConstructor().isArgStrict(dcs.getFieldIndex()) && canIgnoreLaziness(dcs.getDCValueExpr(), variableContext)) {
                return true;
            }
        }

        // 'if a then b else c' where a, b, and c can all be shortcut.
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            Expression condExpr = conditionExpressions.getConditionExpression();
            Expression thenExpr = conditionExpressions.getThenExpression();
            Expression elseExpr = conditionExpressions.getElseExpression();
            if (canIgnoreLaziness(condExpr, variableContext) &&
                canIgnoreLaziness(thenExpr, variableContext) &&
                canIgnoreLaziness(elseExpr, variableContext)) {
                return true;
            }
        }

        // We can compile a record extension strictly if the base record is null or
        // laziness can be ignored for the base record.  This is safe because while
        // strict compilation generates code that creates a new record object none
        // of the fields will be compiled differently, thus preserving laziness.
        if (e.asRecordExtension() != null) {
            Expression.RecordExtension re = (Expression.RecordExtension)e;
            return re.getBaseRecordExpr() == null || canIgnoreLaziness(re.getBaseRecordExpr(), variableContext);
        }

        // We can compile a record update strictly if we can ignore laziness for the base
        // record.   This is safe because while
        // strict compilation generates code that creates a new record object none
        // of the fields will be compiled differently, thus preserving laziness.
        if (e.asRecordUpdate() != null) {
            Expression.RecordUpdate ru = (Expression.RecordUpdate)e;
            return canIgnoreLaziness(ru.getBaseRecordExpr(), variableContext);
        }

        ///////////////////
        // Note: we can't ignore laziness for a record selection/case even if
        // laziness can be ignored for the base record expression since strict
        // compilation would force the evaluation of the field value and change
        // laziness.
        // However there is a less general optimization that can be applied in this
        // situations.  See generateLazyRecordSelection().


        return false;
    }

    /**
     * The C scheme generates code to construct an instance of the given expression.
     * @param e expression of regular kind
     * @param variableContext
     * @return the resulting code contribution
     * @throws CodeGenerationException
     */
    private ExpressionContextPair genS_C(Expression e, VariableContext variableContext) throws CodeGenerationException  {

        if (canIgnoreLaziness(e, variableContext)) {
            return genS_E(e, variableContext);
        }

        // e is a literal?
        Expression.Literal lit = e.asLiteral();
        if (lit != null) {
            return new ExpressionContextPair(getKernelLiteral(lit.getLiteral()).getBoxedReference());
        }

        // Is e a call to a let variable definition function?
        ExpressionContextPair letVarECP = buildLetVarDefCall(e, Scheme.C_SCHEME, variableContext);
        if (letVarECP != null) {
            return letVarECP;
        }

        // There is one primitive operation that gets special treatment in the lazy compilation context.
        // This is Prelude.eager.  Even in a lazy context eager is evaluated immediately.
        BasicOpTuple bop = BasicOpTuple.isBasicOp(e);
        if (bop != null) {
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementLazyPrimOps (bop.getName());
            }
            if (bop.getPrimitiveOp() == PrimOps.PRIMOP_EAGER) {
                if (LECCMachineConfiguration.generateDirectPrimOpCalls()) {
                    return generatePrimitiveOp(e, true, Scheme.C_SCHEME, variableContext);
                } else {
                     //we don't directly call primitive operators when doing function tracing.
                    //This will have the effect of ensuring that they get traced when called.
                    return genS_E(e, variableContext);
                }
            }
        }

        // Is e an application of a saturated constructor?
        ConstructorOpTuple constructorOpTuple = ConstructorOpTuple.isConstructorOp(e, false);
        if (constructorOpTuple != null) {
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementFullySaturatedDCInLazyContext(constructorOpTuple.getDataConstructor().getName());
            }

            return generateDataConstructor(constructorOpTuple, Scheme.C_SCHEME, variableContext);
        }

        // e is a variable?
        if (e.asVar() != null) {
            Expression.Var var = e.asVar();
            JavaExpression varExpression = expressionVarToJavaDef(var, Scheme.C_SCHEME, variableContext);
            return new ExpressionContextPair (varExpression);
        }

        // With changes to the compiler to lift inner case statements to their own function we
        // should never encounter a case at this level.
        // e is a case/switch?
        if (e.asSwitch() != null) {
            throw new CodeGenerationException  ("Encountered a case statement at an inner level.  schemeC.");
        }

        // Similarly, we should never encounter a data constructor field selection.
        if (e.asDataConsSelection() != null) {
            return generateDCFieldSelection(e.asDataConsSelection(), Scheme.C_SCHEME, variableContext);
            //throw new CodeGenerationException  ("Encountered a data constructor field selection at an inner level.  schemeC.");
        }

        // e is a let var.
        if (e.asLetNonRec() != null) {
            return generateLetNonRec(e.asLetNonRec(), Scheme.C_SCHEME, null, variableContext);
        }

        if (e.asLetRec() != null) {
            return generateLetRec(e.asLetRec(), Scheme.C_SCHEME, variableContext);
        }

        // e is an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            ExpressionContextPair ecp = buildApplicationChain (appl, Scheme.C_SCHEME, variableContext);
            if (ecp == null) {
                // This is a general application
                ExpressionContextPair target = genS_C(appl.getE1(), variableContext);
                ExpressionContextPair arg = genS_C(appl.getE2(), variableContext);

                // Add the contexts for the target and the arg.
                Block newContextBlock = target.getContextBlock();
                newContextBlock.addStatement(arg.getContextBlock());

                ecp = new ExpressionContextPair(createInvocation(target.getJavaExpression(), APPLY, arg.getJavaExpression()), newContextBlock);
            }

            return ecp;
        }

        //e is a record update
        Expression.RecordUpdate recordUpdate = e.asRecordUpdate();
        if (recordUpdate != null) {
            return generateLazyRecordUpdate(recordUpdate, variableContext);
        }

        // e is a record extension
        Expression.RecordExtension recordExtension = e.asRecordExtension();
        if (recordExtension != null) {
            return generateLazyRecordExtension(recordExtension, variableContext);
        }

        // e is a record selection
        Expression.RecordSelection recordSelection = e.asRecordSelection();
        if (recordSelection != null) {
            return generateLazyRecordSelection(recordSelection, variableContext);
        }

        // e is an error info
        Expression.ErrorInfo errorInfo = e.asErrorInfo();
        if (errorInfo != null){
            return new ExpressionContextPair(createMakeKernelOpaqueInvocation(getErrorInfo(errorInfo)));
        }

        // e is a record case
        Expression.RecordCase recordCase = e.asRecordCase();
        if (recordCase != null) {
            throw new CodeGenerationException ("Encountered a record-case statement at an inner level.  schemeC.");
        }

        throw new CodeGenerationException ("Unhandled expression of type " + e.getClass().getName() + " encountered in C scheme.");
    }

    /**
     * The E scheme generates code to evaluate the given expression.
     * @param e expression of regular kind
     * @param variableContext
     * @return the resulting code contribution
     * @throws CodeGenerationException
     */
    private ExpressionContextPair genS_E(Expression e, VariableContext variableContext) throws CodeGenerationException  {

        // Strip off an application of Prelude.eager and compile the argument strictly.
        e = stripEager(e);

        // R is a literal?
        Expression.Literal lit = e.asLiteral();
        if (lit != null) {
            return new ExpressionContextPair(getKernelLiteral(lit.getLiteral()).getBoxedReference());
        }

        // Is e a call to a let variable definition function?
        ExpressionContextPair letVarECP = buildLetVarDefCall(e, Scheme.E_SCHEME, variableContext);
        if (letVarECP != null) {
            return letVarECP;
        }

        // Is e a primitive operation (arithmetic, comparative etc.)?
        {
            BasicOpTuple bop = BasicOpTuple.isBasicOp(e);
            if (!LECCMachineConfiguration.generateDirectPrimOpCalls()) {
                // When we are generating call chains we want to force all primitive operations
                // to be done as a function call to the generated function.

                //When we have function tracing enabled, we want to force all primitive operations to be
                //done as function calls. This will have the effect of ensuring that they get traced when called.

                if (bop != null && !bop.getName().equals(getQualifiedName())) {
                    bop = null;
                }
            }

            if (bop != null) {
                return generatePrimitiveOp(e, true, Scheme.E_SCHEME, variableContext);
            }
        }

        if (isNot(e)) {
            return generateNot (e.asAppl(), true, variableContext);
        }

        // Is e an application of 'and' or 'or'?  Test only - don't get tuple
        if (BasicOpTuple.isAndOr(e) != null) {
            return generateAndOr(e, true, variableContext);
        }

        // Is e an application of a saturated constructor?
        ConstructorOpTuple constructorOpTuple = ConstructorOpTuple.isConstructorOp(e, false);
        if (constructorOpTuple != null) {
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementFullySaturatedDCInStrictContext(constructorOpTuple.getDataConstructor().getName());
            }

            return generateDataConstructor(constructorOpTuple, Scheme.E_SCHEME, variableContext);
        }

        // e is a variable?
        if (e.asVar() != null) {
            Expression.Var var = e.asVar();
            JavaExpression varExpression = expressionVarToJavaDef(var, Scheme.E_SCHEME, variableContext);

            if (variableContext.isLocalVariable(var.getName())) {
                return new ExpressionContextPair (varExpression );
            } else {
                // this is either a data constructor or an SC.
                if (var.getDataConstructor() != null || (var.getForeignFunctionInfo() != null && SCJavaDefn.getNArguments(var.getForeignFunctionInfo()) > 0)) {
                    return new ExpressionContextPair(varExpression);
                }

                MachineFunction varFunction = module.getFunction(var.getName());
                if (varFunction != null && varFunction.getArity() > 0) {
                    // This is an non-CAF SC and can't be resolved any further.
                    return new ExpressionContextPair(varExpression);
                } else {
                    // We are in a strict context so we want to evaluate the variable
                    return new ExpressionContextPair(createInvocation(varExpression, EVALUATE, EXECUTION_CONTEXT_VAR));
                }
            }
        }

        // Is e a conditional op (if <cond expr> <then expr> <else expr>)?
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementOptimizedIfThenElse();
            }

            // This is a conditional op.  The conditionExpressions tuple holds (kCond, kThen, kElse) expressions
            // Generate the code for kThen and kElse, as arguments to a new I_Cond instruction

            Expression condExpression = conditionExpressions.getConditionExpression();
            ExpressionContextPair pair;
            pair = generateUnboxedArgument(JavaTypeName.BOOLEAN, condExpression, variableContext);

            Block contextBlock = pair.getContextBlock();
            JavaExpression condJavaExpression = pair.getJavaExpression();
            ExpressionContextPair thenPart = genS_E (conditionExpressions.getThenExpression(), variableContext);
            ExpressionContextPair elsePart = genS_E (conditionExpressions.getElseExpression(), variableContext);

            // Note: both contexts are evaluated..
            contextBlock.addStatement(thenPart.getContextBlock());
            contextBlock.addStatement(elsePart.getContextBlock());

            OperatorExpression ternaryExpression = new OperatorExpression.Ternary(condJavaExpression, new JavaExpression.CastExpression(JavaTypeNames.RTVALUE, thenPart.getJavaExpression()), elsePart.getJavaExpression());
            return new ExpressionContextPair(ternaryExpression, contextBlock);
        }


        // With the changes to the compiler to lift inner cases into their own function
        // we should never encounte a case at this level.
        // Expression is a case/switch?
        if (e.asSwitch() != null) {
            throw new CodeGenerationException ("Encountered a case statement that wasn't at top level in schemeE.");
        }

        // Similarly, we should never encounter a data constructor field selection.
        if (e.asDataConsSelection() != null) {
            return generateDCFieldSelection(e.asDataConsSelection(), Scheme.E_SCHEME, variableContext);
            //throw new CodeGenerationException  ("Encountered a data constructor field selection at an inner level.  schemeE.");
        }

        // R is a let var.
        if (e.asLetNonRec() != null) {
            return generateLetNonRec (e.asLetNonRec(), Scheme.E_SCHEME, null, variableContext);
        }
        if (e.asLetRec() != null) {
            return generateLetRec (e.asLetRec(), Scheme.E_SCHEME, variableContext);
        }

        // R is an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            ExpressionContextPair ecp = buildApplicationChain (appl, Scheme.E_SCHEME, variableContext);
            if (ecp == null) {

                // This is a general application
                ExpressionContextPair target = genS_C(appl.getE1(), variableContext);
                ExpressionContextPair arg = genS_C(appl.getE2(), variableContext);

                Block newContextBlock = target.getContextBlock();
                newContextBlock.addStatement(arg.getContextBlock());

                MethodInvocation applyMI = createInvocation(target.getJavaExpression(), APPLY, arg.getJavaExpression());
                MethodInvocation evaluateMI = createInvocation(applyMI, EVALUATE, EXECUTION_CONTEXT_VAR);//, ExecutionContextClassName));

                ecp = new ExpressionContextPair(evaluateMI, newContextBlock);
            }

            return ecp;
        }

        //e is a record update
        Expression.RecordUpdate recordUpdate = e.asRecordUpdate();
        if (recordUpdate != null) {
            return generateStrictRecordUpdate(recordUpdate, variableContext);
        }

        // e is a record extension
        Expression.RecordExtension recordExtension = e.asRecordExtension();
        if (recordExtension != null) {
            return generateStrictRecordExtension(recordExtension, variableContext);
        }

        // e is a record selection
        Expression.RecordSelection recordSelection = e.asRecordSelection();
        if (recordSelection != null) {
            return generateStrictRecordSelection(recordSelection, true, variableContext);
        }

        // e is an error info
        Expression.ErrorInfo errorInfo = e.asErrorInfo();
        if (errorInfo != null){
            return new ExpressionContextPair(createMakeKernelOpaqueInvocation(getErrorInfo(errorInfo)));
        }

        // e is a record case
        Expression.RecordCase recordCase = e.asRecordCase();
        if (recordCase != null) {
            throw new CodeGenerationException ("Encountered a record-case statement at an inner level.  schemeE.");
        }

        return new ExpressionContextPair(LiteralWrapper.make("Can't handle this expression."));
    }

    /**
     * Helper method for generating return statements.
     * This is the method that should be used to build any return
     * statements for the main function body.
     * Generate a return statement from an ExpressionContextPair.
     * This will simply be the context block from the pair, with an additional
     * statement at the end "return"ing the pair's expression.
     * Note: This method correctly deals with setting the result value
     * before returning when dealing with a CAF.
     *
     * @param pair the ExpressionContextPair
     * @param context - current variable context.  May be null.
     * @return the resulting java statement
     */
    private JavaStatement generateReturn(ExpressionContextPair pair, VariableContext context) {
        Block contextBlock = pair.getContextBlock();
        JavaExpression javaExpression = pair.getJavaExpression();

        if (javaExpression != null) {
            ReturnStatement ret = new ReturnStatement(javaExpression);

            if (context != null) {
                Set<VarInfo> vars = new HashSet<VarInfo>();
                Map<QualifiedName, VarInfo> scope = context.getCurrentScope();
                for (final Map.Entry<QualifiedName, VarInfo> entry : scope.entrySet()) {
                    vars.add(entry.getValue());
                }
                returnStatementToLocalVars.put(ret, vars);
            }

            contextBlock.addStatement(ret);
        }
        return contextBlock;
    }

    /**
     * Helper method for generating return statements.
     * This method should be used for generating any return statements in
     * the main function body.
     * Generate a return statement from a java expression.  This method deals
     * with setting the result before returning when dealing with a CAF.
     * @param javaExpression
     * @param context - current variable context.  May be null.
     * @return the return statement
     */
    private JavaStatement generateReturn(JavaExpression javaExpression, VariableContext context) {
        ReturnStatement ret = new ReturnStatement(javaExpression);

        if (context != null) {
            Set<VarInfo> vars = new HashSet<VarInfo>();
            Map<QualifiedName, VarInfo> scope = context.getCurrentScope();
            for (final Map.Entry<QualifiedName, VarInfo> entry : scope.entrySet()) {
                vars.add(entry.getValue());
            }
            returnStatementToLocalVars.put(ret, vars);
        }

        return ret;
    }

    /**
     * If e is an application of Prelude.eager strip
     * of eager and return the argument.
     * @param e
     * @return the argument to Prelude.eager
     */
    private Expression stripEager (Expression e) throws CodeGenerationException {
        BasicOpTuple bot = BasicOpTuple.isBasicOp(e);
        while (bot != null && bot.getPrimitiveOp() == PrimOps.PRIMOP_EAGER) {
            e = bot.getArgument(0);
            bot = BasicOpTuple.isBasicOp(e);
        }

        return e;
    }

    private ExpressionContextPair buildLetVarDefCall(Expression e, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {

        // e has to be a fully saturated call to a letvar def function.
        if (e.asVar() == null && e.asAppl() == null) {
            return null;
        }

        Expression.Var eVar = e.asVar();
        Expression[] chain = new Expression[]{eVar};

        int nArgs = 0;
        if (eVar == null) {
            chain = appChain(e.asAppl());
            eVar = chain[0].asVar();
            nArgs = chain.length - 1;
        }

        if (eVar == null) {
            return null;
        }

        MachineFunction mf = module.getFunction(eVar.getName());
        if (mf == null ||
            !(mf instanceof LECCLiftedLetVarMachineFunction) ||
            mf.getArity() != nArgs) {
            return null;
        }

        if (scheme == Scheme.UNBOX_INTERNAL_SCHEME && !canTypeBeUnboxed(mf.getResultType())) {
            return null;
        }

        boolean calledArgStrictness[] = mf.getParameterStrictness();
        TypeExpr calledArgTypes[] = mf.getParameterTypes();

        // First generate the java expressions for the argument CAL expressions.
        ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length-1];
        for (int i = 1; i < chain.length; ++i) {
            if (calledArgStrictness[i-1]) {
                if (SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i-1])) {
                    ecp[i-1] = generateUnboxedArgument(typeExprToTypeName(calledArgTypes[i-1]), chain[i], variableContext);
                } else {
                    ecp[i-1] = genS_E(chain[i], variableContext);
                }
            } else {
                ecp[i-1] = genS_C(chain[i], variableContext);
            }
        }

        Block newContext;
        if (ecp.length > 0) {
            newContext = ecp[0].getContextBlock();
            for (int i = 1; i < ecp.length; ++i) {
                newContext.addStatement(ecp[i].getContextBlock());
            }
        } else {
            newContext = new Block();
        }

        // This is a fully saturated supercombinator application
        // for which we can generate a direct call instead of building a suspension.
        int calledArity = mf.getArity();
        JavaExpression args[] = new JavaExpression[ecp.length + 1];
        for (int i = 0; i < calledArity; ++i) {
            args[i] = ecp[i].getJavaExpression();
        }
        args[calledArity] = EXECUTION_CONTEXT_VAR;

        JavaTypeName[] argTypes = new JavaTypeName[args.length];
        for (int i = 0; i < calledArgTypes.length; ++i) {
            if (calledArgStrictness[i] && SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i])) {
                argTypes[i] = SCJavaDefn.typeExprToTypeName(calledArgTypes[i]);
            } else {
                argTypes[i] = JavaTypeNames.RTVALUE;
            }
        }
        argTypes[calledArity] = JavaTypeNames.RTEXECUTION_CONTEXT;

        JavaTypeName returnType = JavaTypeNames.RTVALUE;

        // Get the method name to call.
        String functionName = CALToJavaNames.makeLetVarDefFunctionJavaName(mf.getQualifiedName(), module);
        if (scheme == Scheme.E_SCHEME) {
            functionName = functionName + "_Strict";
        } else
        if (scheme == Scheme.C_SCHEME) {
            functionName = functionName + "_Lazy";
        } else
        if (scheme == Scheme.UNBOX_INTERNAL_SCHEME) {
            functionName = functionName + "_Unboxed";
            returnType = SCJavaDefn.typeExprToTypeName(mf.getResultType());
        }

        JavaExpression root = new MethodInvocation.Static(thisTypeName, functionName, args, argTypes, returnType);

        return new ExpressionContextPair(root, newContext);

    }

    /**
     * The R scheme generates code to apply a supercombinator to its arguments.
     * @param e expression of regular kind
     * @param variableContext
     * @return Statement the resulting code contribution.  This will either be a return statement, or a statement for which
     *   all possible code paths lead to a return statement.
     *   @throws CodeGenerationException
     */
    private JavaStatement genS_R(Expression e, VariableContext variableContext) throws CodeGenerationException {

        // e is a pack constructor?
        if (e.asPackCons() != null) {
            //This is an error!  We should never be at this point with a data constructor expression.
            throw new CodeGenerationException ("PackCons expression encountered in R scheme: " + e.asPackCons().getDataConstructor().getName());
        }

        // R is a literal?
        Expression.Literal lit = e.asLiteral();
        if (lit != null) {
            return generateReturn (getKernelLiteral(lit.getLiteral()).getBoxedReference(), variableContext);
        }

        // Is e a call to a let variable definition function?
        ExpressionContextPair letVarECP = buildLetVarDefCall(e, Scheme.C_SCHEME, variableContext);
        if (letVarECP != null) {
            return generateReturn(letVarECP, variableContext);
        }

        // Is e a primitive operation (arithmetic, comparative etc.)?
        {
            BasicOpTuple bop = BasicOpTuple.isBasicOp(e);
            if (!LECCMachineConfiguration.generateDirectPrimOpCalls()) {

                // When we are generating call chains we want to force all primitive operations
                // to be done as a function call to the generated function.

                //When we have function tracing enabled, we want to force all primitive operations to be
                //done as function calls. This will have the effect of ensuring that they get traced when called.

                if (bop != null && !bop.getName().equals(getQualifiedName())) {
                    bop = null;
                }
            }

            if (bop != null) {
                ExpressionContextPair ecPair = generatePrimitiveOp(e, true, Scheme.R_SCHEME, variableContext);
                return generateReturn (ecPair, variableContext);
            }
        }

        // We can potentially optimize an application of not to use the Java ! operator.
        // Because we are directly evaluating and circumventing laziness it is only safe
        // to do this if there is no chance of a directly/indirectly recursive call.
        if (isNot(e) && !ExpressionAnalyzer.dependsOnStronglyConnectedComponent(e, connectedComponents, getModuleName())) {
            ExpressionContextPair ecPair = generateNot (e.asAppl(), true, variableContext);
            return generateReturn(ecPair, variableContext);
        }

        // Is e an application of 'and' or 'or'?  Test only - don't get tuple
        if (BasicOpTuple.isAndOr(e) != null) {
            ExpressionContextPair ecPair = generateAndOr(e, true, variableContext);
            return generateReturn(ecPair, variableContext);
        }

        // Is e an application of a saturated constructor?
        ConstructorOpTuple constructorOpTuple = ConstructorOpTuple.isConstructorOp(e, false);
        if (constructorOpTuple != null) {
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementFullySaturatedDCInTopLevelContext(constructorOpTuple.getDataConstructor().getName());
            }
            ExpressionContextPair ecPair = generateDataConstructor(constructorOpTuple, Scheme.R_SCHEME, variableContext);
            return generateReturn(ecPair, variableContext);
        }

        // Is e a tail recursive call?
        if (e.asTailRecursiveCall() != null) {
            // We don't need to call generateReturn() at this point since a this
            // expression will generate a loop continuation, not a 'return'.
            return buildTailRecursiveLoopCall(e.asTailRecursiveCall(), variableContext);
        }

        // R is a variable?
        if (e.asVar() != null) {
            Expression.Var var = e.asVar();
            JavaExpression varExpression = expressionVarToJavaDef(var, Scheme.R_SCHEME, variableContext);

            // If the variable is already evaluated we want to call getValue(), which will follow any
            // indirection chains and return the final value.  However, if we are simply returning
            // a strict unboxed function argument this isn't necessary.
            if (variableContext.isLocalVariable(var.getName()) &&
                variableContext.isVarPreEvaluated(var.getName()) &&
                (!variableContext.isFunctionArgument(var.getName()) || !isArgUnboxable(var.getName()))) {
                // Variable is already evaluated so simply do a 'getValue()' on it.
                varExpression = new MethodInvocation.Instance(varExpression, "getValue", JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
            }

            return generateReturn(varExpression, variableContext);
        }

        // Is e a conditional op (if <cond expr> <then expr> <else expr>)?
        // Since this is the top level compilation scheme we can generate an actual 'if' statement.
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementOptimizedIfThenElse();
            }

            // This is a conditional op.  The conditionExpressions tuple holds (kCond, kThen, kElse) expressions
            // Generate the code for kThen and kElse, as arguments to a new I_Cond instruction

            Expression condExpression = conditionExpressions.getConditionExpression();
            ExpressionContextPair pair = generateUnboxedArgument(JavaTypeName.BOOLEAN, condExpression, variableContext);

            // Then
            variableContext.pushJavaScope();
            JavaStatement thenPart = genS_R (conditionExpressions.getThenExpression(), variableContext);

            Block thenBlock = variableContext.popJavaScope();
            thenBlock.addStatement(thenPart);

            // Else
            variableContext.pushJavaScope();
            JavaStatement elsePart = genS_R (conditionExpressions.getElseExpression(), variableContext);

            Block elseBlock = variableContext.popJavaScope();
            elseBlock.addStatement(elsePart);

            JavaExpression conditionExpression = pair.getJavaExpression();

            JavaStatement ite = new IfThenElseStatement(conditionExpression, thenBlock, elseBlock);

            Block contextBlock = pair.getContextBlock();
            contextBlock.addStatement(ite);

            // We don't need to call generateReturn() at this point.  Any return statements
            // will have been generated in the sub-parts of the if-then-else.
            return contextBlock;
        }


        // Expression is a case/switch?
        Expression.Switch eswitch = e.asSwitch();
        if (eswitch != null) {
            // We don't need to call generateReturn() at this point.  Any return statements
            // will have been generated in the sub-parts of the switch.
            return generateSwitch (eswitch, variableContext);
        }

        // Expression is a data constructor field selection?
        Expression.DataConsSelection eDCSelection = e.asDataConsSelection();
        if (eDCSelection != null) {
             return generateReturn(generateDCFieldSelection(eDCSelection, Scheme.R_SCHEME, variableContext), variableContext);
        }

        // e is a let var.
        if (e.asLetNonRec() != null) {
            return generateReturn (generateLetNonRec(e.asLetNonRec(), Scheme.R_SCHEME, null, variableContext), variableContext);
        }

        if (e.asLetRec() != null) {
            return generateReturn(generateLetRec(e.asLetRec(), Scheme.R_SCHEME, variableContext), variableContext);
        }

        // e is an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            JavaStatement topLevelSeq = buildTopLevelSeq (appl, false, variableContext);
            if (topLevelSeq != null) {
                return topLevelSeq;
            }
            ExpressionContextPair ecp = buildApplicationChain (appl, Scheme.R_SCHEME, variableContext);
            if (ecp != null) {
                return generateReturn(ecp, variableContext);
            } else {

                // This is a general application
                ExpressionContextPair target;
                if (appl.getE1().asDataConsSelection() != null) {
                    target = generateDCFieldSelection(appl.getE1().asDataConsSelection(), Scheme.E_SCHEME, variableContext);
                } else {
                    target = genS_C(appl.getE1(), variableContext);
                }
                ExpressionContextPair arg = genS_C(appl.getE2(), variableContext);

                JavaStatement.Block returnBlock = target.getContextBlock();
                returnBlock.addStatement(arg.getContextBlock());

                MethodInvocation mi = createInvocation(target.getJavaExpression(), APPLY, arg.getJavaExpression());
                return generateReturn (new ExpressionContextPair(mi, returnBlock), variableContext);
            }
        }

        //e is a record update
        Expression.RecordUpdate recordUpdate = e.asRecordUpdate();
        if (recordUpdate != null) {
            ExpressionContextPair ecPair = generateStrictRecordUpdate(recordUpdate, variableContext);
            return generateReturn(ecPair, variableContext);
        }

        // e is a record extension
        Expression.RecordExtension recordExtension = e.asRecordExtension();
        if (recordExtension != null) {

            ExpressionContextPair ecPair = generateStrictRecordExtension(recordExtension, variableContext);
            return generateReturn(ecPair, variableContext);
        }

        // e is a record selection
        Expression.RecordSelection recordSelection = e.asRecordSelection();
        if (recordSelection != null) {
            return generateReturn(generateStrictRecordSelection(recordSelection, true, variableContext), variableContext);
        }

        // e is a record case
        Expression.RecordCase recordCase = e.asRecordCase();
        if (recordCase != null) {
            return generateRecordCase (recordCase, variableContext);
        }

        // e is a cast
        Expression.Cast cast = e.asCast();
        if (cast != null) {
            Expression.Var varToCast = cast.getVarToCast();
            if (!variableContext.isFunctionArgument(varToCast.getName())) {
                throw new CodeGenerationException("Expression.Cast is applied to a non-argument variable.");
            }
            if (!isArgStrict(varToCast.getName()) || !isArgUnboxable(varToCast.getName())) {
                throw new CodeGenerationException("Expression.Cast is applied to a non-strict or non-primitive argument.");
            }

            JavaTypeName castType = JavaTypeName.make (getCastType(cast));

            JavaExpression returnVal = boxExpression(castType, new JavaExpression.CastExpression(castType, variableContext.getUnboxedReference(varToCast.getName())));
            return generateReturn(returnVal, variableContext);

        }

        throw new CodeGenerationException("Unrecognized expression: " + e);
    }

    /**
     * Generate java code corresponding to a data constructor field selection.
     * @param dcSelection the expression representing the data constructor field selection.
     * @param scheme
     * @param variableContext
     * @return The expression/context
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateDCFieldSelection(DataConsSelection dcSelection, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {
        if (codeGenerationStats != null) {
            if (scheme == Scheme.E_SCHEME) {
                codeGenerationStats.incrementStrictDCFieldSelectionCount();
            } else if (scheme == Scheme.R_SCHEME) {
                codeGenerationStats.incrementTopLevelDCFieldSelectionCount();
            } else if (scheme == Scheme.C_SCHEME) {
                codeGenerationStats.incrementLazyDCFieldSelectionCount();
            }
        }

        if (scheme == Scheme.E_SCHEME) {
            ExpressionContextPair ecp = genS_E (dcSelection.getDCValueExpr(), variableContext);
            JavaExpression target = ecp.getJavaExpression();
            boolean doCast = true;
            if (target instanceof JavaExpression.ClassInstanceCreationExpression) {
                doCast = false;
            }

            if (doCast) {
                target = new JavaExpression.CastExpression(JavaTypeNames.RTCONS, target);
            }
            MethodInvocation mi =
                new MethodInvocation.Instance (target,
                        "getFieldByIndex",
                        new JavaExpression[]{LiteralWrapper.make(new Integer(dcSelection.getDataConstructor().getOrdinal())),
                                             LiteralWrapper.make(new Integer(dcSelection.getFieldIndex())),
                                             getErrorInfo (dcSelection.getErrorInfo())},
                        new JavaTypeName[]{JavaTypeName.INT,
                                           JavaTypeName.INT,
                                           JavaTypeName.ERRORINFO},
                        JavaTypeNames.RTVALUE,
                        MethodInvocation.InvocationType.VIRTUAL);
            return new ExpressionContextPair (createInvocation(mi, EVALUATE, EXECUTION_CONTEXT_VAR), ecp.getContextBlock());
        }

        if (scheme == Scheme.R_SCHEME) {
            ExpressionContextPair ecp = genS_E (dcSelection.getDCValueExpr(), variableContext);
            JavaExpression target = ecp.getJavaExpression();
            if (!(target instanceof JavaExpression.ClassInstanceCreationExpression)) {
                target = new JavaExpression.CastExpression(JavaTypeNames.RTCONS, target);
            }
            MethodInvocation mi =
                new MethodInvocation.Instance (target,
                        "getFieldByIndex",
                        new JavaExpression[]{LiteralWrapper.make(new Integer(dcSelection.getDataConstructor().getOrdinal())),
                                             LiteralWrapper.make(new Integer(dcSelection.getFieldIndex())),
                                             getErrorInfo (dcSelection.getErrorInfo())},
                        new JavaTypeName[]{JavaTypeName.INT,
                                           JavaTypeName.INT,
                                           JavaTypeName.ERRORINFO},
                       JavaTypeNames.RTVALUE,
                       MethodInvocation.InvocationType.VIRTUAL);
            return new ExpressionContextPair (mi, ecp.getContextBlock());
        }

        // This is a DC field selection expression in a lazy scheme.  Do a lazy compilation of the
        // DC expression and then pass the resulting graph as an argument to an RTDataConsFieldSelection
        // sub class.
        ExpressionContextPair ecp = genS_C (dcSelection.getDCValueExpr(), variableContext);

        JavaTypeName fieldSelectionClassTypeName =
            CALToJavaNames.createFieldSelectionClassTypeNameFromDC(dcSelection.getDataConstructor(), module);

        JavaExpression cice =
            new ClassInstanceCreationExpression (fieldSelectionClassTypeName,
                                                 new JavaExpression[]{ecp.getJavaExpression(),
                                                                      LiteralWrapper.make(new Integer(dcSelection.getDataConstructor().getOrdinal())),
                                                                      LiteralWrapper.make(new Integer(dcSelection.getFieldIndex())),
                                                                      getErrorInfo (dcSelection.getErrorInfo())},
                                                 new JavaTypeName[]{JavaTypeNames.RTVALUE,
                                                                    JavaTypeName.INT,
                                                                    JavaTypeName.INT,
                                                                    JavaTypeName.ERRORINFO});

        return new ExpressionContextPair (cice, ecp.getContextBlock());
    }

    /**
     * Generates code for the record update operator, such as
     * {r | #1 := "abc", #2 = 10.0}
     * in a lazy context. What this means is that r is not actually evaluated, but a special RTRecordUpdate node is created
     * that can be evaluated later if needed to an actual updated record value.
     *
     * @param recordUpdateExpr
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateLazyRecordUpdate(Expression.RecordUpdate recordUpdateExpr, VariableContext variableContext) throws CodeGenerationException {

        Expression baseRecordExpr = recordUpdateExpr.getBaseRecordExpr();
        ExpressionContextPair baseRecordExprContextPair = genS_C(baseRecordExpr, variableContext);

        JavaExpression javaBaseRecordExpr = baseRecordExprContextPair.getJavaExpression();
        //holds the variable declarations introduced in subexpressions of the record update.
        //for example, in the expression {e | field1 := e1, field2 := e2}, these could come from e, e1 or e2.
        Block recordUpdateBlock = new Block();
        recordUpdateBlock.addStatement(baseRecordExprContextPair.getContextBlock());

        Expression.FieldValueData updateFieldsData = recordUpdateExpr.getUpdateFieldsData();
        final int nOrdinalFields = updateFieldsData.getNOrdinalFields();
        final int nTextualFields = updateFieldsData.getNTextualFields();

        if (nOrdinalFields > 0 ) {

            final boolean hasTupleOrdinalPart = updateFieldsData.hasTupleOrdinalPart();

            if (nTextualFields > 0) {

                if (hasTupleOrdinalPart) {
                    return new ExpressionContextPair (new MethodInvocation.Static(
                        JavaTypeNames.RTRECORD_UPDATE,
                        "makeTupleMixedRecordUpdate",
                        new JavaExpression[] {
                            javaBaseRecordExpr,
                            createOrdinalValuesArray(updateFieldsData, recordUpdateBlock, variableContext),
                            createTextualNamesArray(updateFieldsData.getTextualNames()),
                            createTextualValuesArray(updateFieldsData, recordUpdateBlock, variableContext)},
                        new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                        JavaTypeNames.RTRECORD_UPDATE),
                        recordUpdateBlock);
                }

                return new ExpressionContextPair (new MethodInvocation.Static(
                    JavaTypeNames.RTRECORD_UPDATE,
                    "makeMixedRecordUpdate",
                    new JavaExpression[] {
                        javaBaseRecordExpr,
                        createOrdinalNamesArray(updateFieldsData.getOrdinalNames()),
                        createOrdinalValuesArray(updateFieldsData, recordUpdateBlock, variableContext),
                        createTextualNamesArray(updateFieldsData.getTextualNames()),
                        createTextualValuesArray(updateFieldsData, recordUpdateBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_UPDATE),
                    recordUpdateBlock);
            }

            if (hasTupleOrdinalPart) {

                return new ExpressionContextPair(new MethodInvocation.Static(
                    JavaTypeNames.RTRECORD_UPDATE,
                    "makeTupleRecordUpdate",
                    new JavaExpression[] {javaBaseRecordExpr, createOrdinalValuesArray(updateFieldsData, recordUpdateBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_UPDATE),
                    recordUpdateBlock);
            }

            return new ExpressionContextPair (new MethodInvocation.Static(
                JavaTypeNames.RTRECORD_UPDATE,
                "makeOrdinalRecordUpdate",
                new JavaExpression[] {
                    javaBaseRecordExpr,
                    createOrdinalNamesArray(updateFieldsData.getOrdinalNames()),
                    createOrdinalValuesArray(updateFieldsData, recordUpdateBlock, variableContext)},
                new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                JavaTypeNames.RTRECORD_UPDATE),
                recordUpdateBlock);
        }

        if (nTextualFields > 0) {

           return new ExpressionContextPair(new MethodInvocation.Static(
                JavaTypeNames.RTRECORD_UPDATE,
                "makeTextualRecordUpdate",
                new JavaExpression[] {
                    javaBaseRecordExpr,
                    createTextualNamesArray(updateFieldsData.getTextualNames()),
                    createTextualValuesArray(updateFieldsData, recordUpdateBlock, variableContext)},
                new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                JavaTypeNames.RTRECORD_UPDATE),
                recordUpdateBlock);
        }


        //the empty record update is not allowed (should be caught by static checks).
        throw new CodeGenerationException("The empty record update is not allowed.");
    }

    /**
     * Generates code for record update in a strict context. For example,
     * {r | field1 := 20.0, field2 := "abc"}.
     * The main difference between the strict and lazy case is that in the strict case r must be
     * reduced to know what the shape of the record is.
     *
     * @param recordUpdateExpr
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateStrictRecordUpdate(Expression.RecordUpdate recordUpdateExpr, VariableContext variableContext) throws CodeGenerationException {

        Expression baseRecordExpr = recordUpdateExpr.getBaseRecordExpr();

        //holds the variable declarations introduced in subexpressions of the record update expression.
        //for example, in the expression {e | field1 := e1, field2 := e2}, these could come from e, e1 or e2.
        Block recordUpdateBlock = new Block();

        JavaExpression invocationTarget;
        {

            ExpressionContextPair baseRecordExprContextPair = genS_E(baseRecordExpr, variableContext);

            JavaExpression javaBaseRecordExpr = baseRecordExprContextPair.getJavaExpression();
            recordUpdateBlock.addStatement(baseRecordExprContextPair.getContextBlock());

            //the compiler ensures that evaluating baseRecordExpr will result in a RTRecordValue.
            invocationTarget = new CastExpression(JavaTypeNames.RTRECORD_VALUE, javaBaseRecordExpr);
        }

        //the expression {e | field1 := e1, field2 := e2, field3 := e3} is encoded as
        //{{{e | field1 := e1} | field2 := e2} | field3 := e3}

        // We want to copy the base record for the first update.  Subsequent updates can just
        // mutate the copy.
        // The copy is created by a call to:
        //    1) updateOrdinalField - if only ordinal fields are being updated
        //    2) updateTextualField - if only textual fields are being updated
        //    3) updateMixedOrdinalField - if both ordinal and textual fields are being updated
        FieldValueData fieldValueData = recordUpdateExpr.getUpdateFieldsData();

        SortedMap<FieldName, Expression> updateFieldValuesMap = recordUpdateExpr.getUpdateFieldValuesMap();
        int fieldN = 0;
        for (final Map.Entry<FieldName, Expression> entry : updateFieldValuesMap.entrySet()) {

            FieldName fieldName = entry.getKey();
            Expression updateExpr = entry.getValue();

            //the actual updated values are not strictly evaluated, so we use the C scheme.
            ExpressionContextPair updateExprContextPair = genS_C(updateExpr, variableContext);
            JavaExpression javaUpdateExpr = updateExprContextPair.getJavaExpression();
            recordUpdateBlock.addStatement(updateExprContextPair.getContextBlock());

            if (fieldName instanceof FieldName.Ordinal) {

                //we need to copy the base record only for the first update. Subsequent updates can just mutate the base.
                String updateMethodName;
                if (fieldN == 0) {
                    // If there are only ordinal field updates we call updateOrdinalField to generate
                    // the record copy.  Otherwise we call updateMixedOrdinalField, so that it is
                    // safe to modify the textual fields of the copy.
                    if (fieldValueData.getNTextualFields() == 0) {
                        updateMethodName = "updateOrdinalField";
                    } else {
                        updateMethodName = "updateMixedOrdinalField";
                    }
                } else {
                    updateMethodName = "mutateOrdinalField";
                }

                int ordinal = ((FieldName.Ordinal)fieldName).getOrdinal();
                invocationTarget =
                   new MethodInvocation.Instance(
                       invocationTarget,
                       updateMethodName,
                       new JavaExpression[] {
                           LiteralWrapper.make(Integer.valueOf(ordinal)),
                           javaUpdateExpr},
                       new JavaTypeName[] {JavaTypeName.INT, JavaTypeNames.RTVALUE},
                       JavaTypeNames.RTRECORD_VALUE,
                       InvocationType.VIRTUAL);

            } else if (fieldName instanceof FieldName.Textual) {

                //we need to copy the base record only for the first update. Subsequent updates can just mutate the base.
                String updateMethodName;
                if (fieldN == 0) {
                    updateMethodName = "updateTextualField";
                } else {
                    updateMethodName = "mutateTextualField";
                }

                String textualFieldName = fieldName.getCalSourceForm();
                invocationTarget =
                   new MethodInvocation.Instance(
                       invocationTarget,
                       updateMethodName,
                       new JavaExpression[] {
                           LiteralWrapper.make(textualFieldName),
                           javaUpdateExpr},
                       new JavaTypeName[] {JavaTypeName.STRING, JavaTypeNames.RTVALUE},
                       JavaTypeNames.RTRECORD_VALUE,
                       InvocationType.VIRTUAL);

            } else {
                throw new IllegalStateException();
            }

            ++fieldN;
        }

        return new ExpressionContextPair (invocationTarget, recordUpdateBlock);
    }

    /**
     * Generates code for record extensions in a strict context.
     * These include non record-polymorphic records such as
     * {field1 = 10.0, field2 = "abc"}
     * and record-polymorphic expressions such as
     * {r | field1 = 20.0}.
     * The main difference between the strict and lazy case is that in the strict case r must be
     * reduced to know what the shape of the record is.
     *
     * @param recordExtensionExpr
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateStrictRecordExtension(Expression.RecordExtension recordExtensionExpr, VariableContext variableContext) throws CodeGenerationException {

        Expression baseRecordExpr = recordExtensionExpr.getBaseRecordExpr();

        //holds the variable declarations introduced in subexpressions of the record extension.
        //for example, in the expression {e | field1 = e1, field2 = e2}, these could come from e, e1 or e2.
        Block recordExtensionBlock = new Block();

        JavaExpression invocationTarget;
        if (baseRecordExpr != null) {

            ExpressionContextPair baseRecordExprContextPair = genS_E(baseRecordExpr, variableContext);

            JavaExpression javaBaseRecordExpr = baseRecordExprContextPair.getJavaExpression();
            recordExtensionBlock.addStatement(baseRecordExprContextPair.getContextBlock());

            //the compiler ensures that evaluating baseRecordExpr will result in a RTRecordValue.
            invocationTarget = new CastExpression(JavaTypeNames.RTRECORD_VALUE, javaBaseRecordExpr);
            //invocationType = InvocationType.VIRTUAL;

        } else {
            invocationTarget = null;
        }

        Expression.FieldValueData extensionFieldsData = recordExtensionExpr.getExtensionFieldsData();
        final int nOrdinalFields = extensionFieldsData.getNOrdinalFields();
        final int nTextualFields = extensionFieldsData.getNTextualFields();

        if (nOrdinalFields > 0 ) {

            final boolean hasTupleOrdinalPart = extensionFieldsData.hasTupleOrdinalPart();

            if (nTextualFields > 0) {

                if (hasTupleOrdinalPart) {
                    //in the non-extension case, create code for:
                    //RTRecordValue.makeTupleMixedRecord(new RTValue[] {ordinalValues1, ..., ordinalValuesN},
                    //    new String[] {textualFieldName1, ..., textualFieldNameM},
                    //    new RTValue[] {textualFieldValue1, ..., textualFieldValueM}
                    MethodInvocation mi;
                    if (baseRecordExpr != null) {
                        mi = new MethodInvocation.Instance(
                            invocationTarget,
                            makeRecordCreationName("makeTupleMixedRecord", baseRecordExpr),
                            new JavaExpression[] {
                                createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext),
                                createTextualNamesArray(extensionFieldsData.getTextualNames()),
                                createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                            new JavaTypeName[] {JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                            JavaTypeNames.RTRECORD_VALUE,
                            InvocationType.VIRTUAL);
                    } else {
                        //static invocation
                        mi = new MethodInvocation.Static(
                            JavaTypeNames.RTRECORD_VALUE,
                            makeRecordCreationName("makeTupleMixedRecord", null),
                            new JavaExpression[] {
                                createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext),
                                createTextualNamesArray(extensionFieldsData.getTextualNames()),
                                createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                            new JavaTypeName[] {JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                            JavaTypeNames.RTRECORD_VALUE);
                    }
                    return new ExpressionContextPair (mi, recordExtensionBlock);
                }

                MethodInvocation mi;
                if (baseRecordExpr != null) {
                    mi = new MethodInvocation.Instance(
                        invocationTarget,
                        makeRecordCreationName("makeMixedRecord", baseRecordExpr),
                        new JavaExpression[] {
                            createOrdinalNamesArray(extensionFieldsData.getOrdinalNames()),
                            createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext),
                            createTextualNamesArray(extensionFieldsData.getTextualNames()),
                            createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                        new JavaTypeName[] {JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                        JavaTypeNames.RTRECORD_VALUE,
                        InvocationType.VIRTUAL);
                } else {
                    mi = new MethodInvocation.Static(
                        JavaTypeNames.RTRECORD_VALUE,
                        makeRecordCreationName("makeMixedRecord", null),
                        new JavaExpression[] {
                            createOrdinalNamesArray(extensionFieldsData.getOrdinalNames()),
                            createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext),
                            createTextualNamesArray(extensionFieldsData.getTextualNames()),
                            createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                        new JavaTypeName[] {JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                        JavaTypeNames.RTRECORD_VALUE);
                }

                return new ExpressionContextPair (mi, recordExtensionBlock);
            }

            if (hasTupleOrdinalPart) {

                MethodInvocation mi;
                if (baseRecordExpr != null) {
                    mi = new MethodInvocation.Instance(
                        invocationTarget,
                        makeRecordCreationName("makeTupleRecord", baseRecordExpr),
                        new JavaExpression[] {createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                        new JavaTypeName[] {JavaTypeNames.RTVALUE_ARRAY},
                        JavaTypeNames.RTRECORD_VALUE,
                        InvocationType.VIRTUAL);
                } else {
                    mi = new MethodInvocation.Static(
                        JavaTypeNames.RTRECORD_VALUE,
                        makeRecordCreationName("makeTupleRecord", null),
                        new JavaExpression[] {createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                        new JavaTypeName[] {JavaTypeNames.RTVALUE_ARRAY},
                        JavaTypeNames.RTRECORD_VALUE);
                }

                return new ExpressionContextPair(mi, recordExtensionBlock);
            }

            MethodInvocation mi;
            if (baseRecordExpr != null) {
                mi = new MethodInvocation.Instance(
                    invocationTarget,
                    makeRecordCreationName("makeOrdinalRecord", baseRecordExpr),
                    new JavaExpression[] {
                        createOrdinalNamesArray(extensionFieldsData.getOrdinalNames()),
                        createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_VALUE,
                    InvocationType.VIRTUAL);
            } else {
                mi = new MethodInvocation.Static(
                    JavaTypeNames.RTRECORD_VALUE,
                    makeRecordCreationName("makeOrdinalRecord", null),
                    new JavaExpression[] {
                        createOrdinalNamesArray(extensionFieldsData.getOrdinalNames()),
                        createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_VALUE);
            }

            return new ExpressionContextPair (mi, recordExtensionBlock);
        }

        if (nTextualFields > 0) {

            //create code for:
            //RTRecordValue.makeTextualRecord(new String[] {textualFieldName1, ..., textualFieldNameN},
            //    new RTValue[] {valueExpr1, ...., valueExprN})
            //or
            //baseRecordExpr.makeTextualRecordExtension(new String[] {textualFieldName1, ..., textualFieldNameN},
            //    new RTValue[] {valueExpr1, ...., valueExprN})

            MethodInvocation mi;
            if (baseRecordExpr != null) {
                mi = new MethodInvocation.Instance(
                    invocationTarget,
                    makeRecordCreationName("makeTextualRecord", baseRecordExpr),
                    new JavaExpression[] {createTextualNamesArray(extensionFieldsData.getTextualNames()),
                        createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_VALUE,
                    InvocationType.VIRTUAL);
            } else {
                mi = new MethodInvocation.Static(
                    JavaTypeNames.RTRECORD_VALUE,
                    makeRecordCreationName("makeTextualRecord", null),
                    new JavaExpression[] {createTextualNamesArray(extensionFieldsData.getTextualNames()),
                        createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_VALUE);
            }

            return new ExpressionContextPair(mi, recordExtensionBlock);
        }

        if (baseRecordExpr != null) {
            //the empty record extension {e |}
            return new ExpressionContextPair(invocationTarget, recordExtensionBlock);
        }

        //empty record: RTRecordValue.EMPTY_RECORD
        return new ExpressionContextPair(new JavaField.Static(JavaTypeNames.RTRECORD_VALUE, "EMPTY_RECORD", JavaTypeNames.RTRECORD_VALUE),
                recordExtensionBlock);
    }

    private static String makeRecordCreationName(String baseName, Expression baseRecordExpr) {
        if (baseRecordExpr == null) {
            return baseName;
        }

        return baseName + "Extension";
    }

    /**
     * @param ordinalNames
     * @return creates the expression "new int[] {ordinal1, ..., ordinalN}".
     */
    private static ArrayCreationExpression createOrdinalNamesArray(int[] ordinalNames) {

        int nOrdinalFields = ordinalNames.length;
        JavaExpression[] javaTextualNames = new JavaExpression[nOrdinalFields];

        for (int i = 0; i < nOrdinalFields; ++i) {
            javaTextualNames[i] = LiteralWrapper.make(Integer.valueOf(ordinalNames[i]));
        }
        return new ArrayCreationExpression(JavaTypeName.INT, javaTextualNames);
    }

    /**
     * @param textualNames
     * @return creates the expression "new String[] {textualFieldName1, ..., textualFieldNameN}".
     */
    private static ArrayCreationExpression createTextualNamesArray(String[] textualNames) {

        int nTextualFields = textualNames.length;
        JavaExpression[] javaTextualNames = new JavaExpression[nTextualFields];

        for (int i = 0; i < nTextualFields; ++i) {
            javaTextualNames[i] = LiteralWrapper.make(textualNames[i]);
        }
        return new ArrayCreationExpression(JavaTypeName.STRING, javaTextualNames);
    }

    private ArrayCreationExpression createOrdinalValuesArray(Expression.FieldValueData fieldValueData, Block recordModificationBlock, VariableContext variableContext)  throws CodeGenerationException {
        //create the expression:
        //new RTValue[] {valueExpr1, ...., valueExprN}

        int nOrdinalFields = fieldValueData.getNOrdinalFields();
        Expression[] ordinalValues = fieldValueData.getOrdinalValues();
        JavaExpression[] javaOrdinalValues = new JavaExpression[nOrdinalFields];

        for (int i = 0; i < nOrdinalFields; ++i) {

            ExpressionContextPair valueExprContextPair = genS_C(ordinalValues[i], variableContext);
            javaOrdinalValues[i] = valueExprContextPair.getJavaExpression();
            recordModificationBlock.addStatement(valueExprContextPair.getContextBlock());
        }

        return new ArrayCreationExpression(JavaTypeNames.RTVALUE, javaOrdinalValues);
    }

    private ArrayCreationExpression createTextualValuesArray(Expression.FieldValueData fieldValueData, Block recordModificationBlock, VariableContext variableContext)  throws CodeGenerationException {
        //create the expression:
        //new RTValue[] {valueExpr1, ...., valueExprN}

        int nTextualFields = fieldValueData.getNTextualFields();
        Expression[] textualValues = fieldValueData.getTextualValues();
        JavaExpression[] javaTextualValues = new JavaExpression[nTextualFields];

        for (int i = 0; i < nTextualFields; ++i) {

            ExpressionContextPair valueExprContextPair = genS_C(textualValues[i], variableContext);
            javaTextualValues[i] = valueExprContextPair.getJavaExpression();
            recordModificationBlock.addStatement(valueExprContextPair.getContextBlock());
        }

        return new ArrayCreationExpression(JavaTypeNames.RTVALUE, javaTextualValues);
    }

    /**
     * Generates code for record extensions in a lazy context. These include
     * non record-polymorphic records such as {field1 = 10.0, field2 = "abc"}
     * and record-polymorphic expressions such as {r | field1 = 20.0}. The main
     * difference between the strict and lazy case is that in the strict case r
     * must be reduced to know what the shape of the record is.
     *
     * @param recordExtensionExpr
     * @param variableContext
     * @return Expression.RecordExtension
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateLazyRecordExtension(Expression.RecordExtension recordExtensionExpr, VariableContext variableContext) throws CodeGenerationException {

        Expression baseRecordExpr = recordExtensionExpr.getBaseRecordExpr();
        if (baseRecordExpr == null) {
            //this is a non record-polymorphic record extension so it is already in reduced form.
            return generateStrictRecordExtension(recordExtensionExpr, variableContext);
        }

        ExpressionContextPair baseRecordExprContextPair = genS_C(baseRecordExpr, variableContext);

        JavaExpression javaBaseRecordExpr = baseRecordExprContextPair.getJavaExpression();
        //holds the variable declarations introduced in subexpressions of the record extension.
        //for example, in the expression {e | field1 = e1, field2 = e2}, these could come from e, e1 or e2.
        Block recordExtensionBlock = new Block();
        recordExtensionBlock.addStatement(baseRecordExprContextPair.getContextBlock());

        Expression.FieldValueData extensionFieldsData = recordExtensionExpr.getExtensionFieldsData();
        final int nOrdinalFields = extensionFieldsData.getNOrdinalFields();
        final int nTextualFields = extensionFieldsData.getNTextualFields();

        if (nOrdinalFields > 0 ) {

            final boolean hasTupleOrdinalPart = extensionFieldsData.hasTupleOrdinalPart();

            if (nTextualFields > 0) {

                if (hasTupleOrdinalPart) {
                    return new ExpressionContextPair (new MethodInvocation.Static(
                        JavaTypeNames.RTRECORD_EXTENSION,
                        "makeTupleMixedRecordExtension",
                        new JavaExpression[] {
                            javaBaseRecordExpr,
                            createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext),
                            createTextualNamesArray(extensionFieldsData.getTextualNames()),
                            createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                        new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                        JavaTypeNames.RTRECORD_EXTENSION),
                        recordExtensionBlock);
                }

                return new ExpressionContextPair (new MethodInvocation.Static(
                    JavaTypeNames.RTRECORD_EXTENSION,
                    "makeMixedRecordExtension",
                    new JavaExpression[] {
                        javaBaseRecordExpr,
                        createOrdinalNamesArray(extensionFieldsData.getOrdinalNames()),
                        createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext),
                        createTextualNamesArray(extensionFieldsData.getTextualNames()),
                        createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_EXTENSION),
                    recordExtensionBlock);
            }

            if (hasTupleOrdinalPart) {

                return new ExpressionContextPair(new MethodInvocation.Static(
                    JavaTypeNames.RTRECORD_EXTENSION,
                    "makeTupleRecordExtension",
                    new JavaExpression[] {javaBaseRecordExpr, createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                    new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE_ARRAY},
                    JavaTypeNames.RTRECORD_EXTENSION),
                    recordExtensionBlock);
            }

            return new ExpressionContextPair (new MethodInvocation.Static(
                JavaTypeNames.RTRECORD_EXTENSION,
                "makeOrdinalRecordExtension",
                new JavaExpression[] {
                    javaBaseRecordExpr,
                    createOrdinalNamesArray(extensionFieldsData.getOrdinalNames()),
                    createOrdinalValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.INT_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                JavaTypeNames.RTRECORD_EXTENSION),
                recordExtensionBlock);
        }

        if (nTextualFields > 0) {

           return new ExpressionContextPair(new MethodInvocation.Static(
                JavaTypeNames.RTRECORD_EXTENSION,
                "makeTextualRecordExtension",
                new JavaExpression[] {
                    javaBaseRecordExpr,
                    createTextualNamesArray(extensionFieldsData.getTextualNames()),
                    createTextualValuesArray(extensionFieldsData, recordExtensionBlock, variableContext)},
                new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.STRING_ARRAY, JavaTypeNames.RTVALUE_ARRAY},
                JavaTypeNames.RTRECORD_EXTENSION),
                recordExtensionBlock);
        }


        //the empty record extension {e |}
        return new ExpressionContextPair(javaBaseRecordExpr, recordExtensionBlock);
    }

    /**
     * Generates code for record selection i.e. recordExpr.fieldName in a strict context.
     *
     * @param recordSelectionExpr - the record selection construct
     * @param evaluateField - indicates that the selected field value should be evaluated to WHNF
     * @param variableContext
     * @return Expression.RecordSelection
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateStrictRecordSelection(
            Expression.RecordSelection recordSelectionExpr,
            boolean evaluateField,
            VariableContext variableContext) throws CodeGenerationException {

        //for the CAL code recordExpr.fieldName we generate (roughly)
        //for ordinal fields
        //((RTRecordValue) (codeForRecordExpr.evaluate($ec))).getOrdinalFieldValue(ordinal)
        //for textual fields
        //((RTRecordValue) (codeForRecordExpr.evaluate($ec))).getTextualFieldValue(textualFieldName)

        Expression recordExpr = recordSelectionExpr.getRecordExpr();
        FieldName fieldName = recordSelectionExpr.getFieldName();


        ExpressionContextPair recordExprContextPair = genS_E(recordExpr, variableContext);

        JavaExpression javaRecordExpr = recordExprContextPair.getJavaExpression();
        Block recordSelectionBlock = new Block();
        recordSelectionBlock.addStatement(recordExprContextPair.getContextBlock());

        //the compiler ensures that evaluating recordExpr will result in a RTRecordValue.
        javaRecordExpr = new CastExpression(JavaTypeNames.RTRECORD_VALUE, javaRecordExpr);

        MethodInvocation getValueInvocation;
        if (fieldName instanceof FieldName.Textual) {
            getValueInvocation = new MethodInvocation.Instance(javaRecordExpr, "getTextualFieldValue",
                LiteralWrapper.make(fieldName.getCalSourceForm()), JavaTypeName.STRING,
                JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);
        } else {
            int ordinal = ((FieldName.Ordinal)fieldName).getOrdinal();
            getValueInvocation = new MethodInvocation.Instance(javaRecordExpr, "getOrdinalFieldValue",
                LiteralWrapper.make(Integer.valueOf(ordinal)), JavaTypeName.INT,
                JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);
        }

        if (evaluateField) {
            getValueInvocation = createInvocation (getValueInvocation, EVALUATE, EXECUTION_CONTEXT_VAR);
        }

        return new ExpressionContextPair(getValueInvocation, recordSelectionBlock);
    }


    /**
     * Generates code for record selection i.e. recordExpr.fieldName in a lazy context.
     * What this essentially means is that we don't want to force the evaluation of recordExpr
     * to get a record.
     *
     * @param recordSelectionExpr
     * @param variableContext
     * @return Expression.RecordSelection
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateLazyRecordSelection(Expression.RecordSelection recordSelectionExpr, VariableContext variableContext) throws CodeGenerationException {

        //for the CAL code recordExpr.fieldName we generate (roughly)
        //for textual field names
        //new RTRecordSelection.Textual(codeForRecordExpr, textualFieldName);
        //for ordinal field names
        //new RTRecordSelection.Ordinal(codeForRecordExpr, ordinal);

        Expression recordExpr = recordSelectionExpr.getRecordExpr();

        // If we can ignore laziness for the record expression then we can generate code
        // that directly extracts the field value, but doesn't force the field value to
        // WHNF.
        if (canIgnoreLaziness(recordExpr, variableContext)) {
            return generateStrictRecordSelection(recordSelectionExpr, false, variableContext);
        }

        FieldName fieldName = recordSelectionExpr.getFieldName();

        ExpressionContextPair recordExprContextPair = genS_C(recordExpr, variableContext);

        JavaExpression javaRecordExpr = recordExprContextPair.getJavaExpression();
        Block recordSelectionBlock = new Block();
        recordSelectionBlock.addStatement(recordExprContextPair.getContextBlock());

        JavaExpression createLazyRecordSelection;
        if (fieldName instanceof FieldName.Textual) {

            createLazyRecordSelection = new ClassInstanceCreationExpression(JavaTypeNames.RTRECORD_SELECTION_TEXTUAL_FIELD,
                new JavaExpression[] {javaRecordExpr, LiteralWrapper.make(fieldName.getCalSourceForm())},
                new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.STRING});

        } else {

            int ordinal = ((FieldName.Ordinal)fieldName).getOrdinal();
            createLazyRecordSelection = new ClassInstanceCreationExpression(JavaTypeNames.RTRECORD_SELECTION_ORDINAL_FIELD,
                new JavaExpression[] {javaRecordExpr, LiteralWrapper.make(Integer.valueOf(ordinal))},
                new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeName.INT});
        }

        return new ExpressionContextPair(createLazyRecordSelection, recordSelectionBlock);
    }

    /**
     * Generates code for record case.
     * The prototypical record case is:
     * case conditionExpr of {r | field1 = x1, field2 = x2, ...} -> resultExpr;
     * r may be null in the case of a non record-polymorphic pattern match
     *
     * @param recordCaseExpr
     * @param variableContext
     * @return JavaStatement
     * @throws CodeGenerationException
     */
    private JavaStatement generateRecordCase(Expression.RecordCase recordCaseExpr, VariableContext variableContext) throws CodeGenerationException {

        //generate code for the record case condition expression
        //this will look something like:
        //$recordCase = (RTRecordValue)javaConditionExpr.evaluate()

        // Increment the nested case level.  This is used to disambiguate the name of the variable created
        // to hold the record value of the case expression.
        nestedCaseLevel++;

        Expression conditionExpr = recordCaseExpr.getConditionExpr();

        ExpressionContextPair conditionExprContextPair = genS_E(conditionExpr, variableContext);

        Block recordCaseBlock = new Block();

        JavaExpression javaConditionExpr = conditionExprContextPair.getJavaExpression();
        recordCaseBlock.addStatement(conditionExprContextPair.getContextBlock());

        //the compiler ensures that evaluating conditionExpr will result in a RTRecordValue.
        javaConditionExpr = new CastExpression(JavaTypeNames.RTRECORD_VALUE, javaConditionExpr);

        LocalVariable conditionVar = new LocalVariable("$recordCase" + nestedCaseLevel, JavaTypeNames.RTRECORD_VALUE);

        LocalVariableDeclaration conditionVarDeclaration = new LocalVariableDeclaration(conditionVar, javaConditionExpr);
        recordCaseBlock.addStatement(conditionVarDeclaration);

        //now encode the extraction of the pattern bound variables from the condition record expr.

        // Also need to push a let variable block.  This is separate from the variable scope because the two
        // do not always coincide.  The let variable block is popped by calling i_VariableScope.genS_Vars().
        variableContext.pushJavaScope();

        //FieldName -> String
        SortedMap<FieldName, String> fieldBindingVarMap = recordCaseExpr.getFieldBindingVarMap();

        String baseRecordPatternVarName = recordCaseExpr.getBaseRecordPatternVarName();
        if (baseRecordPatternVarName != null &&
            !baseRecordPatternVarName.equals(Expression.RecordCase.WILDCARD_VAR)) {

            QualifiedName qn = QualifiedName.make(currentModuleName, baseRecordPatternVarName);
            VarInfo.RecordField varInfo = variableContext.addRecordField(qn, null);
            String javaBaseRecordPatternVarName = varInfo.getJavaName();
            LocalName lazyRef = new LocalName(varInfo.getJavaName(), JavaTypeNames.RTVALUE);
            varInfo.updateLazyReference(lazyRef);
            varInfo.updateStrictReference(SCJavaDefn.createInvocation(lazyRef, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR));

            //generate the Java code:
            //(in the case of both ordinal and textual field names
            //javaBaseRecordPatternVarName = conditionVar.makeMixedRecordRetraction(new int[] {ordinalFieldName1, ..., ordinalFieldNameN},
            //  new String[] {textualFieldName1, ..., textualFieldNameN}

            LocalVariable baseRecordPatternVar = new LocalVariable(javaBaseRecordPatternVarName, JavaTypeNames.RTRECORD_VALUE);

            JavaExpression javaExtractBaseRecordExpr;

            Expression.RecordCase.FieldData fieldData = recordCaseExpr.getBindingFieldsData();
            //todoBI there could be some more optimizations here to handle the cases
            //a. where the ordinal names are in tuple form, then only the tuple size needs to be passed.
            //b. where only a single ordinal or single textual field is being retracted, then we don't
            //   need to form the array.
            //Note however, that if the record pattern var is not used in the expression on the right hand side
            //of the ->, then it will not be created (earlier analysis replaces it by a _), so in fact this
            //code is not called that often anyways.
            int[] retractedOrdinalFields = fieldData.getOrdinalNames();
            String[] retractedTextualFields = fieldData.getTextualNames();

            if (retractedOrdinalFields.length > 0) {

                if (retractedTextualFields.length > 0) {

                    if (fieldData.hasTupleOrdinalPart()) {

                        javaExtractBaseRecordExpr = new MethodInvocation.Instance(
                                conditionVar,
                            "makeTupleMixedRecordRetraction",
                            new JavaExpression[] {
                                LiteralWrapper.make(Integer.valueOf(retractedOrdinalFields.length)),
                                createTextualNamesArray(retractedTextualFields)},
                            new JavaTypeName[] {JavaTypeName.INT, JavaTypeName.STRING_ARRAY},
                            JavaTypeNames.RTRECORD_VALUE,
                            InvocationType.VIRTUAL);
                    } else {

                        javaExtractBaseRecordExpr = new MethodInvocation.Instance(
                                conditionVar,
                            "makeMixedRecordRetraction",
                            new JavaExpression[] {
                                createOrdinalNamesArray(retractedOrdinalFields),
                                createTextualNamesArray(retractedTextualFields)},
                            new JavaTypeName[] {JavaTypeName.INT_ARRAY, JavaTypeName.STRING_ARRAY},
                            JavaTypeNames.RTRECORD_VALUE,
                            InvocationType.VIRTUAL);
                    }
                } else {

                    if (fieldData.hasTupleOrdinalPart()) {

                        javaExtractBaseRecordExpr = new MethodInvocation.Instance(
                                conditionVar,
                            "makeTupleRecordRetraction",
                            new JavaExpression[] {
                                LiteralWrapper.make(Integer.valueOf(retractedOrdinalFields.length))},
                            new JavaTypeName[] {JavaTypeName.INT},
                            JavaTypeNames.RTRECORD_VALUE,
                            InvocationType.VIRTUAL);
                    } else {

                        javaExtractBaseRecordExpr = new MethodInvocation.Instance(
                                conditionVar,
                            "makeOrdinalRecordRetraction",
                            new JavaExpression[] {
                                createOrdinalNamesArray(retractedOrdinalFields)},
                            new JavaTypeName[] {JavaTypeName.INT_ARRAY},
                            JavaTypeNames.RTRECORD_VALUE,
                            InvocationType.VIRTUAL);
                    }
                }

            } else if (retractedTextualFields.length > 0) {

                    javaExtractBaseRecordExpr = new MethodInvocation.Instance(
                            conditionVar,
                        "makeTextualRecordRetraction",
                        new JavaExpression[] {
                            createTextualNamesArray(retractedTextualFields)},
                        new JavaTypeName[] {JavaTypeName.STRING_ARRAY},
                        JavaTypeNames.RTRECORD_VALUE,
                        InvocationType.VIRTUAL);

            } else {
                javaExtractBaseRecordExpr = conditionVar;
            }

            LocalVariableDeclaration extractBaseRecordDeclaration =
                new LocalVariableDeclaration(baseRecordPatternVar, javaExtractBaseRecordExpr);
            recordCaseBlock.addStatement(extractBaseRecordDeclaration);
        }

        for (final Map.Entry<FieldName, String> entry : fieldBindingVarMap.entrySet()) {

            FieldName fieldName = entry.getKey();
            String bindingVarName = entry.getValue();

            //ignore anonymous pattern variables. These are guaranteed not to be used
            //by the result expression, and so don't need to be extracted from the condition record.
            if (!bindingVarName.equals(Expression.RecordCase.WILDCARD_VAR)) {

                QualifiedName qn = QualifiedName.make(currentModuleName, bindingVarName);
                VarInfo.RecordField varInfo = variableContext.addRecordField(qn, null);
                String javaBindingVarName = varInfo.getJavaName();
                LocalName lazyRef = new LocalName(varInfo.getJavaName(), JavaTypeNames.RTVALUE);
                varInfo.updateLazyReference(lazyRef);
                varInfo.updateStrictReference(SCJavaDefn.createInvocation(lazyRef, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR));

                LocalVariable bindingVar = new LocalVariable(javaBindingVarName, JavaTypeNames.RTVALUE);

                JavaExpression javaExtractValueExpr;
                if (fieldName instanceof FieldName.Textual) {
                    //javaBindingVarName = $recordCase.getTextualFieldValue(fieldName);
                    javaExtractValueExpr = new MethodInvocation.Instance(conditionVar, "getTextualFieldValue",
                        LiteralWrapper.make(fieldName.getCalSourceForm()), JavaTypeName.STRING,
                        JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);
                } else {
                    int ordinal = ((FieldName.Ordinal)fieldName).getOrdinal();
                    javaExtractValueExpr = new MethodInvocation.Instance(conditionVar, "getOrdinalFieldValue",
                        LiteralWrapper.make(Integer.valueOf(ordinal)), JavaTypeName.INT,
                        JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);
                }

                LocalVariableDeclaration extractValueDeclaration = new LocalVariableDeclaration(bindingVar, javaExtractValueExpr);
                recordCaseBlock.addStatement(extractValueDeclaration);
            }
        }


        //encode the result expression in the context of the extended variable scope.
        Expression resultExpr = recordCaseExpr.getResultExpr();
        JavaStatement resultJavaStatement = genS_R(resultExpr, variableContext);

        // Generate any let variables in this block and add them to the recordCaseBlock.
        recordCaseBlock.addStatement(variableContext.popJavaScope());

        // Add the body of the record case.
        recordCaseBlock.addStatement(resultJavaStatement);

        return recordCaseBlock;
    }

    private JavaField getReferencedDC (JavaTypeName type, DataConstructor dc) {
        if ((dc == null) || (type == null)) {
            throw new NullPointerException ("Invalid argument to getReferencedDC()");
        }

        for (final ReferencedDCInfo rfi : sharedValues.getReferencedDCs()) {

            if (rfi.getGeneratedClassTypeName().equals(type)) {
                DataConstructor odc = rfi.getDC();
                if (dc == odc) {
                    return rfi.getJField();
                    //don't need to copy, JavaField is immutable!
                    //return new JavaField (field);
                }
            }
        }

        String baseFieldName = "i_" + dc.getName().getUnqualifiedName();
        String fieldName = baseFieldName;

        boolean conflict = true;
        int appi = 2;
        while (conflict) {
            conflict = false;
            if (this.instanceName != null && fieldName.equals(this.instanceName)) {
                conflict = true;
                fieldName = fieldName + appi;
                appi++;
            } else {
                for (final ReferencedDCInfo rfi : sharedValues.getReferencedDCs()) {
                    JavaField field = rfi.getJField();
                    if (field.getFieldName().equals(fieldName)) {
                        conflict = true;
                        fieldName = baseFieldName + appi;
                        appi++;
                        break;
                    }
                }
            }
        }

        JavaTypeName fieldType = type;

        JavaField field;
        field =new JavaField.Static(thisTypeName, fieldName, fieldType);
        sharedValues.add (new ReferencedDCInfo(field, type, dc));

        return field;

    }

    /**
     * Get a literal variable to use in place of a CAL literal.
     * @param object the literal object from the compiler
     * @return JavaField a field that will represent the literal.
     *   These will be emitted on call to genS_LiteralDefs.
     * @throws CodeGenerationException
     */
    private KernelLiteral getKernelLiteral(Object object) throws CodeGenerationException {
        KernelLiteral kl = sharedValues.getKernelLiteral(object);
        if (kl == null) {
            throw new CodeGenerationException ("Reference to unknown literal value: " + object.toString());
        }

        return kl;

    }

    /**
     * Determine if this SCJavaDefn is actually a data constructor
     * @return boolean true if this is a constructor (otherwise this is a supercombinator)
     */
    boolean isDataConstructor() {
        return (dataConstructor != null);
    }


    /**
     * Get the arity of this SC
     * @return int the arity
     */
    int getArity() {
        return nArguments;
    }

    /**
     * Obtain the compiler DataConstructor object if this is a data constructor
     * @return DataConstructor the data constructor object.  Will be null if this is a supercombinator
     */
    DataConstructor getDataConstructor() {
        return dataConstructor;
    }


    /**
     * Attemp to create a java expression that 'unboxes' the given value into one of the
     * built in types or an opaque type.
     * @param unboxedType - the desired type of the unboxed value.  Null if the type doesn't matter.
     * @param value
     * @return the unboxing expression or null if the unboxed class doesn't correspond to a CAL primitive
     */
    static JavaExpression unboxValue (JavaTypeName unboxedType, JavaExpression value) {
        assert (value != null) : ("Attempt to unbox null expression.");

        if (unboxedType == null) {
            // We don't care about the actual type.
            return value;
        }

        if (unboxedType.equals(JavaTypeNames.RTVALUE)) {
            return value;
        } else if (unboxedType.equals(JavaTypeName.CHAR)) {
            return createVirtualInvocation(value, "getCharValue", JavaTypeName.CHAR);

        } else if (unboxedType.equals(JavaTypeName.BOOLEAN)) {
            return createVirtualInvocation(value, "getBooleanValue", JavaTypeName.BOOLEAN);

        } else if (unboxedType.equals(JavaTypeName.INT)) {
            return SCJavaDefn.createInvocation(value, SCJavaDefn.GETORDINALVALUE);

        } else if (unboxedType.equals(JavaTypeName.DOUBLE)) {
            return createVirtualInvocation(value, "getDoubleValue", JavaTypeName.DOUBLE);

        } else if (unboxedType.equals(JavaTypeName.BYTE)) {
            return createVirtualInvocation(value, "getByteValue", JavaTypeName.BYTE);

        } else if (unboxedType.equals(JavaTypeName.SHORT)) {
            return createVirtualInvocation(value, "getShortValue", JavaTypeName.SHORT);

        } else if (unboxedType.equals(JavaTypeName.FLOAT)) {
            return createVirtualInvocation(value, "getFloatValue", JavaTypeName.FLOAT);

        } else if (unboxedType.equals(JavaTypeName.LONG)) {
            return createVirtualInvocation(value, "getLongValue", JavaTypeName.LONG);

        } else if (unboxedType.equals(JavaTypeName.STRING)) {
            return createVirtualInvocation(value, "getStringValue", JavaTypeName.STRING);

        } else if (unboxedType.equals(JavaTypeName.BIG_INTEGER)) {
            return createVirtualInvocation(value, "getIntegerValue", JavaTypeName.BIG_INTEGER);

        } else if (unboxedType.equals(JavaTypeName.CAL_VALUE)) {
            // Casting to CalValue here is unnecessary as RTValue is derived from CalValue
            return value;

        } else {
            // ((typeName)(argDef.getOpaqueValue()))
            JavaExpression arg = createVirtualInvocation(value, "getOpaqueValue", JavaTypeName.OBJECT);
            if (unboxedType.equals(JavaTypeName.OBJECT)) {
                // getOpaqueValue() returns an Object no need to cast.
                return arg;
            } else {
                return new CastExpression(unboxedType, arg);       // cast to argClass
            }
        }
    }

    /**
     * Unmarshal an argument from java to CAL. This is used to convert the return value from a foreign function
     * call to one of the RTValue derived classes.
     *
     * @param argClass The return type of the Java call.
     * @param definition Def the definition/expression of the Java type.
     * @return JavaExpression the expression to get the CAL type.
     */
    private ExpressionContextPair returnTypeToCal(Class<?> argClass, JavaExpression definition) {

        // Variables from which to construct the ExpressionContextPair
        JavaExpression javaExpression = null;
        Block context = new Block();

        if (argClass == void.class) {
            // hopefully definition will be a method invocation, rather than object creation or a field..
            context.addStatement(new ExpressionStatement(definition));

            if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                javaExpression = createMakeKernelIntInvocation(LiteralWrapper.make (Integer.valueOf(0)));
            } else {
                JavaTypeName calUnitClassName = JavaTypeName.make(CALToJavaNames.createFullPackageName(CALToJavaNames.createPackageNameFromModule(CAL_Prelude.MODULE_NAME)) + ".TYPE_Unit$CAL_Unit", false);
                javaExpression = new MethodInvocation.Static(calUnitClassName, "make", EXECUTION_CONTEXT_VAR, JavaTypeNames.RTEXECUTION_CONTEXT, JavaTypeNames.RTFUNCTION);
            }
        } else
        if (argClass == byte.class) {
            javaExpression = createMakeKernelByteInvocation(definition);
        } else
        if (argClass == short.class) {
            javaExpression = createMakeKernelShortInvocation(definition);
        } else
        if (argClass == long.class) {
            javaExpression = createMakeKernelLongInvocation(definition);
        } else
        if (argClass == float.class) {
            javaExpression = createMakeKernelFloatInvocation(definition);
        } else
        if (argClass == char.class) {
            javaExpression = createMakeKernelCharInvocation(definition);
        } else
        if (argClass == boolean.class) {
            javaExpression = createMakeKernelBooleanInvocation(definition);
        } else
        if (argClass == int.class) {
            javaExpression = createMakeKernelIntInvocation(definition);
        } else
        if (argClass == double.class) {
            javaExpression = createMakeKernelDoubleInvocation(definition);
        } else
        if (argClass == java.lang.String.class) {
            javaExpression = createMakeKernelStringInvocation(definition);
        } else
        if (argClass == java.math.BigInteger.class) {
            javaExpression = createMakeKernelIntegerInvocation(definition);
        } else
        if (isCalValueClass(argClass)) {
            javaExpression = new CastExpression (JavaTypeNames.RTVALUE, definition);
        } else
        {
            javaExpression = createMakeKernelOpaqueInvocation(definition);
        }

        return new ExpressionContextPair(javaExpression, context);
    }

    /**
     * Generate the java model for a literal list in CAL code. (i.e. [a, b, c, d]).
     * @param constructorOpExpressions
     * @param scheme
     * @param variableContext
     * @return the ExpressionContextPair describing the java equivalent of the literal list.
     * @throws CodeGenerationException
     */
    private ExpressionContextPair compileLiteralList (ConstructorOpTuple constructorOpExpressions, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {
        // Because of the recursive nature of the List data structur we handle literal lists as a special case.
        DataConstructor dc = constructorOpExpressions.getDataConstructor ();
        QualifiedName qn = dc.getName();
        if (!qn.equals(CAL_Prelude.DataConstructors.Cons)) {
            return null;
        }

        JavaTypeName dcTypeName = CALToJavaNames.createTypeNameFromDC(dc, module);

        int nArgs = 2;
        boolean addEC = LECCMachineConfiguration.passExecContextToDataConstructors();
        if (addEC) {
            nArgs++;
        }

        JavaExpression constructorArgs[] = null;
        JavaExpression lastConstructorArgs[] = null;
        JavaTypeName constructorArgTypes[] = new JavaTypeName[nArgs];
        Arrays.fill(constructorArgTypes, JavaTypeNames.RTVALUE);
        if (addEC) {
            constructorArgTypes[dc.getArity()] = JavaTypeNames.RTEXECUTION_CONTEXT;
        }

        JavaExpression listCreation = null;
        Block contextBlock = null;
        boolean moreListElements = true;
        do {
            moreListElements = false;
            ExpressionContextPair leftArg = genS_C(constructorOpExpressions.getArgument(0), variableContext);
            if (contextBlock == null) {
                contextBlock = leftArg.getContextBlock();
            } else {
                contextBlock.addStatement(leftArg.getContextBlock());
            }

            constructorArgs = new JavaExpression [nArgs];
            if (addEC) {
                constructorArgs[dc.getArity()] = EXECUTION_CONTEXT_VAR;
            }

            constructorArgs[0] = leftArg.getJavaExpression();

            ClassInstanceCreationExpression cc =
                new ClassInstanceCreationExpression (dcTypeName, constructorArgs, constructorArgTypes);
            if (listCreation == null) {
                listCreation = cc;
            }

            if (lastConstructorArgs != null) {
                lastConstructorArgs[1] = cc;
            }

            lastConstructorArgs = constructorArgs;

            // The second argument will always be a data constructor, either cons or Nil.
            ConstructorOpTuple constructorOpExpressions2 = ConstructorOpTuple.isConstructorOp(constructorOpExpressions.getArgument(1), false);
            if (constructorOpExpressions2 != null && constructorOpExpressions2.getDataConstructor().getName().equals(CAL_Prelude.DataConstructors.Cons)) {
                constructorOpExpressions = constructorOpExpressions2;
                moreListElements = true;
            }
        } while (moreListElements);

        ExpressionContextPair terminator = genS_C(constructorOpExpressions.getArgument(1), variableContext);
        contextBlock.addStatement(terminator.getContextBlock());
        lastConstructorArgs[1] = terminator.getJavaExpression();

        return new ExpressionContextPair (listCreation, contextBlock);
    }

    /**
     * Generate java code corresponding to a data constructor.
     * @param constructorOpExpressions
     * @param scheme
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateDataConstructor(ConstructorOpTuple constructorOpExpressions, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {

        // If this is a literal list (i.e. a saturated application of Prelude.Cons) then we can
        // handle it in a non-recursive fashion.
        ExpressionContextPair list = compileLiteralList(constructorOpExpressions, scheme, variableContext);
        if (list != null) {
            return list;
        }

        DataConstructor dc = constructorOpExpressions.getDataConstructor ();

        // If we are dealing with a data constructor for the CAL type boolean we want to optimize
        // by substituting and instance of the literal RTKernel.CAL_Boolean.
        if (isTrueOrFalseDataCons(dc)) {
            LiteralWrapper boolWrapper = LiteralWrapper.make(Boolean.valueOf(isTrueDataCons(dc)));

            return new ExpressionContextPair(createMakeKernelBooleanInvocation(boolWrapper));
        }

        if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
            if (isEnumDataType(dc)) {
                // We should never see a DataConstructor instance for an enumeration type.
                throw new CodeGenerationException("Encountered enumeration data constructor: " + dc.getName().getQualifiedName());
            }
        }

        int arity = dc.getArity();

        JavaTypeName dcTypeName = CALToJavaNames.createTypeNameFromDC(dc, module);
        Block argContext = new Block();

        boolean[] fieldStrictness = new boolean [dc.getArity()];
        boolean dcHasStrictFields = false;
        if (LECCMachineConfiguration.IGNORE_STRICTNESS_ANNOTATIONS) {
            Arrays.fill(fieldStrictness, false);
        } else {
            for (int i = 0; i < dc.getArity(); ++i) {
                fieldStrictness[i] = dc.isArgStrict(i);
                if (fieldStrictness[i]) {
                    dcHasStrictFields = true;
                }
            }
        }

        // If there are no strict arguments we can simply create an instance of the DC class.
        // The simplest way to do this is to treat this DC application as if it were in a strict context.
        if (!dcHasStrictFields) {
            scheme = Scheme.E_SCHEME;
        } else {
            // If all strict arguments are already evaluated, or we consider them safe to evaluate (i.e. cheap and
            // with no side effects) we can treat this as strict.
            boolean allOK = true;
            for (int i = 0; i < dc.getArity(); ++i) {
                if (dc.getArgStrictness()[i] && !canIgnoreLaziness(constructorOpExpressions.getArgument(i), variableContext)) {
                    allOK = false;
                    break;
                }
            }

            if (allOK) {
                scheme = Scheme.E_SCHEME;
            }
        }

        if (arity < 0) {
            throw new CodeGenerationException("Invalid constructor operator arity: " + arity);
        } else
        if (arity == 0) {
            JavaExpression dcInstance;
            JavaTypeName typeName = CALToJavaNames.createTypeNameFromDC(dc, module);
            if (LECCMachineConfiguration.generateStatistics()) {
                // If this is a TagDC we want to pass the ordinal as an argument
                if (isTagDC(dc, getModule())) {
                    dcInstance = new ClassInstanceCreationExpression(typeName, JavaExpression.LiteralWrapper.make(Integer.valueOf(dc.getOrdinal())), JavaTypeName.INT);
                } else {
                    dcInstance = new ClassInstanceCreationExpression(typeName, SCJavaDefn.EXECUTION_CONTEXT_VAR, JavaTypeNames.RTEXECUTION_CONTEXT);
                }
            } else {
                dcInstance = getReferencedDC(typeName, dc);
            }
            return new ExpressionContextPair(dcInstance, argContext);
        } else{
            if (scheme == Scheme.C_SCHEME) {
                // This is a fully saturated DC application in a lazy context.
                // If there are no strict fields in the data constructor we can
                // simply create an instance of it.  Otherwise create an
                // appropriate application node.

                // First generate the java expressions for the CAL expressions.
                ExpressionContextPair[] ecp = new ExpressionContextPair[dc.getArity()];

                for (int i = 0; i < dc.getArity(); ++i) {
                    ecp[i] = genS_C(constructorOpExpressions.getArgument(i), variableContext);
                }

                Block newContext = ecp[0].getContextBlock();
                for (int i = 1; i < dc.getArity(); ++i) {
                    newContext.addStatement(ecp[i].getContextBlock());
                }

                JavaExpression dcExpr = expressionVarToJavaDef(constructorOpExpressions.getDataConstructorExpression(), scheme, variableContext);

                if (dc.getArity() <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH) {
                    JavaExpression nodeArgs[] = new JavaExpression[dc.getArity() + 1];
                    JavaTypeName nodeArgTypes[] = new JavaTypeName[dc.getArity() + 1];
                    nodeArgs[0] = dcExpr;
                    nodeArgTypes[0] = JavaTypeNames.RTSUPERCOMBINATOR;
                    for (int i = 0; i < dc.getArity(); ++i) {
                        nodeArgs[i+1] = ecp[i].getJavaExpression();
                        nodeArgTypes[i+1] = JavaTypeNames.RTVALUE;
                    }

                    JavaTypeName appNodeType = getTypeNameForApplicationNode(dc.getArity(), false);

                    return new ExpressionContextPair(new JavaExpression.ClassInstanceCreationExpression(appNodeType, nodeArgs, nodeArgTypes), newContext);
                } else {
                    JavaExpression target = dcExpr;
                    for (int i = 0; i < dc.getArity(); ++i) {
                        target = createInvocation(target, APPLY, ecp[i].getJavaExpression());
                    }
                    return new ExpressionContextPair (target, newContext);
                }
            } else {
                // This is a fully saturated DC application in a strict context.  Create a
                // new DC class instance.

                // First generate the java expressions for the members.
                ExpressionContextPair[] ecp = new ExpressionContextPair[dc.getArity()];
                TypeExpr[] fieldTypes = SCJavaDefn.getFieldTypesForDC(dc);

                for (int i = 0; i < dc.getArity(); ++i) {
                    if (fieldStrictness[i]) {
                        if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                            ecp[i] = generateUnboxedArgument(typeExprToTypeName(fieldTypes[i]), constructorOpExpressions.getArgument(i), variableContext);
                        } else {
                            ecp[i] = genS_E(constructorOpExpressions.getArgument(i), variableContext);
                        }
                    } else {
                        ecp[i] = genS_C(constructorOpExpressions.getArgument(i), variableContext);
                    }
                }

                Block newContext = ecp[0].getContextBlock();
                for (int i = 1; i < dc.getArity(); ++i) {
                    newContext.addStatement(ecp[i].getContextBlock());
                }

                int nArgs = dc.getArity();
                boolean addEC = LECCMachineConfiguration.passExecContextToDataConstructors();
                if (addEC) {
                    nArgs++;
                }

                JavaExpression[] constructorArgs = new JavaExpression [nArgs];
                JavaTypeName[] constructorArgTypes = new JavaTypeName [nArgs];
                for (int i = 0; i < dc.getArity(); ++i) {
                    constructorArgs[i] = ecp[i].getJavaExpression();
                    constructorArgTypes[i] = JavaTypeNames.RTVALUE;
                    if (fieldStrictness[i] && SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                        constructorArgTypes[i] = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                    }
                }

                if (addEC) {
                    constructorArgs[dc.getArity()] = EXECUTION_CONTEXT_VAR;
                    constructorArgTypes[dc.getArity()] = JavaTypeNames.RTEXECUTION_CONTEXT;
                }

                ClassInstanceCreationExpression cc = new ClassInstanceCreationExpression (dcTypeName, constructorArgs, constructorArgTypes);
                return new ExpressionContextPair (cc, newContext);
            }
        }
    }



    /**
     * Generate the source code for a call to a foreign function in a strict context.
     * @param e the expression which is an application of a foreign function.
     * @param boxResult
     * @param scheme
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateForeignCall(Expression e, boolean boxResult, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {

        if (codeGenerationStats != null) {
            codeGenerationStats.incrementDirectForeignCalls();
        }

        // Unpack the basic op into subexpressions
        final BasicOpTuple basicOpExpressions = BasicOpTuple.isBasicOp(e);

        final ForeignFunctionInfo foreignFunctionInfo = basicOpExpressions.getForeignFunctionInfo();
        final ForeignFunctionInfo.JavaKind kind = foreignFunctionInfo.getJavaKind();

        JavaExpression returnExpression = null;
        Block returnContext = new Block();

        if (kind.isMethod()) {

            final ForeignFunctionInfo.Invocation invocationInfo = (ForeignFunctionInfo.Invocation)foreignFunctionInfo;

            final Method method = (Method)SCJavaDefn.getJavaProxy(invocationInfo);
            if (method.getReturnType() == void.class && (scheme != Scheme.R_SCHEME)) {
                ExpressionContextPair ecp = genS_C (e, variableContext);
                JavaExpression expr = ecp.getJavaExpression();
                JavaStatement block = ecp.getContextBlock();
                JavaExpression.MethodInvocation eval = createInvocation(expr, EVALUATE, EXECUTION_CONTEXT_VAR);
                return new ExpressionContextPair(eval, block);
            }

            int nJavaArgs = SCJavaDefn.getNArguments(foreignFunctionInfo);
            final Class<?> invocationClass = SCJavaDefn.getInvocationClass(invocationInfo);
            final JavaTypeName invocationClassName = JavaTypeName.make(invocationClass);
            String methodName = method.getName();
            int startArg = 0;

            Class<?>[] exceptions = method.getExceptionTypes();
            for (int i = 0; i < exceptions.length; ++i) {
                if (!exceptionHandled(exceptions [i])) {
                    addExceptionHandler(exceptions[i]);
                }
            }

            if (!LECCMachineConfiguration.generateDirectPrimOpCalls()) {
                if (!exceptionHandled(Throwable.class)) {
                    addExceptionHandler(Throwable.class);
                }
            }

            JavaExpression target;
            InvocationType invocationType;      // static, virtual, or interface.  Can't be special (ie. private method).
            if (kind.isStatic()){
                target = null;
                invocationType = InvocationType.STATIC;
            } else {
                ExpressionContextPair pair = generateUnboxedForeignFunctionArgument(invocationClassName, basicOpExpressions.getArgument(0), variableContext);
                target = pair.getJavaExpression();
                invocationType = invocationClass.isInterface() ? InvocationType.INTERFACE : InvocationType.VIRTUAL;
                returnContext.addStatement(pair.getContextBlock());
                startArg = 1;
            }

            Class<?>[] argTypes = method.getParameterTypes();
            JavaExpression[] args = new JavaExpression[argTypes.length];
            JavaTypeName[] argTypeNames = new JavaTypeName[argTypes.length];

            for (int i = startArg, j = 0; i < nJavaArgs; ++i, ++j) {
                int index = i - startArg;
                final JavaTypeName argTypeName = JavaTypeName.make(argTypes[index]);
                ExpressionContextPair pair = generateUnboxedForeignFunctionArgument(argTypeName, basicOpExpressions.getArgument(i), variableContext);
                args[index] = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());

                argTypeNames[index] = argTypeName;
            }

            JavaTypeName returnType = JavaTypeName.make(method.getReturnType());

            if (kind.isStatic()) {
                returnExpression = new MethodInvocation.Static(invocationClassName, methodName, args, argTypeNames, returnType);
            } else {
                returnExpression = new MethodInvocation.Instance(target, methodName, invocationClassName, args, argTypeNames, returnType, invocationType);
            }

            if (boxResult) {
                ExpressionContextPair pair = returnTypeToCal(method.getReturnType(), returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind.isField()) {

            final ForeignFunctionInfo.Invocation invocationInfo = (ForeignFunctionInfo.Invocation)foreignFunctionInfo;

            final Field field = (Field)SCJavaDefn.getJavaProxy(invocationInfo);

            JavaTypeName fieldType = JavaTypeName.make(field.getType());
            String fieldName = field.getName();

            final JavaTypeName invocationClassName = JavaTypeName.make(SCJavaDefn.getInvocationClass(invocationInfo));
            if (kind.isStatic()) {
                returnExpression = new JavaField.Static(invocationClassName, fieldName, fieldType);
            } else {
                ExpressionContextPair pair = generateUnboxedForeignFunctionArgument(invocationClassName, basicOpExpressions.getArgument(0), variableContext);
                JavaExpression instance = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
                returnExpression = new JavaField.Instance(instance, fieldName, fieldType);
            }

            if (boxResult) {
                ExpressionContextPair pair = returnTypeToCal (field.getType(), returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind.isConstructor()) {

            final ForeignFunctionInfo.Invocation invocationInfo = (ForeignFunctionInfo.Invocation)foreignFunctionInfo;

            Constructor<?> constructor = (Constructor<?>)SCJavaDefn.getJavaProxy(invocationInfo);
            int nJavaArgs = SCJavaDefn.getNArguments(foreignFunctionInfo);
            Class<?> clazz = constructor.getDeclaringClass();
            Class<?>[] argTypes = constructor.getParameterTypes();

            Class<?>[] exceptions = constructor.getExceptionTypes();
            for (int i = 0; i < exceptions.length; ++i) {
                if (!exceptionHandled(exceptions [i])) {
                    addExceptionHandler(exceptions[i]);
                }
            }

            JavaExpression[] args = new JavaExpression[argTypes.length];
            JavaTypeName[] argTypeNames = new JavaTypeName[argTypes.length];
            for (int i = 0; i < nJavaArgs; i++) {
                final JavaTypeName argTypeName = JavaTypeName.make(argTypes[i]);
                ExpressionContextPair pair = generateUnboxedForeignFunctionArgument(argTypeName, basicOpExpressions.getArgument(i), variableContext);
                args[i] = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
                argTypeNames[i] = argTypeName;
            }

            returnExpression = new ClassInstanceCreationExpression(JavaTypeName.make(clazz), args, argTypeNames);

            if (boxResult) {
                ExpressionContextPair pair = returnTypeToCal(constructor.getDeclaringClass(), returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind.isCast()) {

            final Class<?> argType = SCJavaDefn.getJavaArgumentType(foreignFunctionInfo, 0);
            final Class<?> resultType = SCJavaDefn.getJavaReturnType(foreignFunctionInfo);

            final ExpressionContextPair argExprPair = generateUnboxedForeignFunctionArgument(JavaTypeName.make(argType), basicOpExpressions.getArgument(0), variableContext);
            final JavaExpression argExpr = argExprPair.getJavaExpression();
            returnContext.addStatement(argExprPair.getContextBlock());

            if (kind == ForeignFunctionInfo.JavaKind.IDENTITY_CAST ||
                kind == ForeignFunctionInfo.JavaKind.WIDENING_REFERENCE_CAST) {

                //it is important to do nothing for a widening reference cast (except for evaluating)
                //this is because at the JavaTypeName level, where the inheritance hierarchy is not available, it is not possible for
                //the bytecode generator to determine if this is truly a widening reference cast i.e. a no-op. Hence we must make
                //this optimization at this point.
                returnExpression = argExpr;

            } else if (kind == ForeignFunctionInfo.JavaKind.NARROWING_PRIMITIVE_CAST ||
                       kind == ForeignFunctionInfo.JavaKind.WIDENING_PRIMITIVE_CAST ||
                       kind == ForeignFunctionInfo.JavaKind.NARROWING_REFERENCE_CAST) {

                returnExpression = new JavaExpression.CastExpression(JavaTypeName.make(resultType), argExpr);

            } else {
                throw new CodeGenerationException("Unrecognized foreign function cast kind: " + kind);
            }

            if (boxResult) {
                ExpressionContextPair pair = returnTypeToCal(resultType, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind.isInstanceOf()) {

            final Class<?> argType = SCJavaDefn.getJavaArgumentType(foreignFunctionInfo, 0);
            final ForeignFunctionInfo.InstanceOf instanceOfInfo = (ForeignFunctionInfo.InstanceOf)foreignFunctionInfo;
            final Class<?> instanceOfType = SCJavaDefn.getInstanceOfType(instanceOfInfo);

            final ExpressionContextPair argExprPair = generateUnboxedForeignFunctionArgument(JavaTypeName.make(argType), basicOpExpressions.getArgument(0), variableContext);
            final JavaExpression argExpr = argExprPair.getJavaExpression();
            returnContext.addStatement(argExprPair.getContextBlock());

            returnExpression = new JavaExpression.InstanceOf(argExpr, JavaTypeName.make(instanceOfType));

            if (boxResult) {
                ExpressionContextPair pair = returnTypeToCal(boolean.class, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind.isClassLiteral()) {

            final ForeignFunctionInfo.ClassLiteral classLiteralInfo = (ForeignFunctionInfo.ClassLiteral)foreignFunctionInfo;
            final Class<?> referentType = SCJavaDefn.getReferentType(classLiteralInfo);

            returnExpression = new JavaExpression.ClassLiteral(JavaTypeName.make(referentType));

            if (boxResult) {
                ExpressionContextPair pair = returnTypeToCal(Class.class, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind.isNullLiteral()) {

            returnExpression = JavaExpression.LiteralWrapper.NULL;
            if (boxResult) {
                final ExpressionContextPair pair = returnTypeToCal(SCJavaDefn.getJavaReturnType(foreignFunctionInfo), returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind.isNullCheck()) {

            final ForeignFunctionInfo.NullCheck nullCheckInfo = (ForeignFunctionInfo.NullCheck)foreignFunctionInfo;

            final Class<?> argType = SCJavaDefn.getJavaArgumentType(foreignFunctionInfo, 0);

            final ExpressionContextPair argExprPair = generateUnboxedForeignFunctionArgument(JavaTypeName.make(argType), basicOpExpressions.getArgument(0), variableContext);
            final JavaExpression argExpr = argExprPair.getJavaExpression();
            returnContext.addStatement(argExprPair.getContextBlock());

            final JavaOperator javaOp = nullCheckInfo.checkIsNull() ? JavaOperator.EQUALS_OBJECT : JavaOperator.NOT_EQUALS_OBJECT;

            returnExpression = new JavaExpression.OperatorExpression.Binary(javaOp, argExpr, LiteralWrapper.NULL);

            if (boxResult) {
                final ExpressionContextPair pair = returnTypeToCal(boolean.class, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind == ForeignFunctionInfo.JavaKind.NEW_ARRAY) {

            //e.g. newString3Array :: Int -> Int -> JString3Array; (for new String[d1][d2][])

            //note, this may be less than the dimension of the array e.g. for new String[10][7][] this is 2.
            final int nJavaArgs = SCJavaDefn.getNArguments(foreignFunctionInfo);
            final Class<?> newArrayType = SCJavaDefn.getJavaReturnType(foreignFunctionInfo);

            final JavaExpression[] args = new JavaExpression[nJavaArgs];
            final JavaTypeName[] argTypeNames = new JavaTypeName[nJavaArgs];
            for (int i = 0; i < nJavaArgs; i++) {
                final ExpressionContextPair pair = generateUnboxedForeignFunctionArgument(JavaTypeName.INT, basicOpExpressions.getArgument(i), variableContext);
                args[i] = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
                argTypeNames[i] = JavaTypeName.INT;
            }

            returnExpression = new ClassInstanceCreationExpression(JavaTypeName.make(newArrayType), args, argTypeNames);

            if (boxResult) {
                final ExpressionContextPair pair = returnTypeToCal(newArrayType, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind == ForeignFunctionInfo.JavaKind.LENGTH_ARRAY) {

            final Class<?> argType = SCJavaDefn.getJavaArgumentType(foreignFunctionInfo, 0);

            final ExpressionContextPair argExprPair = generateUnboxedForeignFunctionArgument(JavaTypeName.make(argType), basicOpExpressions.getArgument(0), variableContext);
            final JavaExpression argExpr = argExprPair.getJavaExpression();
            returnContext.addStatement(argExprPair.getContextBlock());

            returnExpression = new JavaExpression.ArrayLength(argExpr);

            if (boxResult) {
                final ExpressionContextPair pair = returnTypeToCal(int.class, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);


        } else if (kind == ForeignFunctionInfo.JavaKind.SUBSCRIPT_ARRAY) {

            //e.g. subscriptString3Array :: String3Array -> Int -> Int -> StringArray;
            //for subscripting using 2 indices a 3-dimensional String array to get a 1-dimensional String array

            //note, this may be less than the dimension of the array e.g. for new String[10][7][] this is 2.
            final int nJavaArgs = SCJavaDefn.getNArguments(foreignFunctionInfo);
            final Class<?> subscriptedArrayType = SCJavaDefn.getJavaReturnType(foreignFunctionInfo);

            for (int i = 0; i < nJavaArgs; i++) {

                final JavaTypeName argTypeName = JavaTypeName.make(SCJavaDefn.getJavaArgumentType(foreignFunctionInfo, i));
                final ExpressionContextPair pair =
                    generateUnboxedForeignFunctionArgument(argTypeName, basicOpExpressions.getArgument(i), variableContext);
                returnContext.addStatement(pair.getContextBlock());

                if (i == 0) {
                    //the initial array expression
                    returnExpression = pair.getJavaExpression();
                } else {
                    //subscript by the next index
                    returnExpression = new JavaExpression.ArrayAccess(returnExpression, pair.getJavaExpression());
                }
            }

            if (boxResult) {
                final ExpressionContextPair pair = returnTypeToCal(subscriptedArrayType, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);

        } else if (kind == ForeignFunctionInfo.JavaKind.UPDATE_ARRAY) {

            //e.g. updateString3Array :: String3Array -> Int -> Int -> StringArray -> StringArray;
            //for updating using 2 indices a 3-dimensional String array with a 1-dimensional String array

            //note, this may be less than the dimension of the array to update
            final int nJavaArgs = SCJavaDefn.getNArguments(foreignFunctionInfo);
            final Class<?> updatedElementType = SCJavaDefn.getJavaReturnType(foreignFunctionInfo);

            for (int i = 0; i < nJavaArgs; i++) {

                final JavaTypeName argTypeName = JavaTypeName.make(SCJavaDefn.getJavaArgumentType(foreignFunctionInfo, i));
                final ExpressionContextPair pair =
                    generateUnboxedForeignFunctionArgument(argTypeName, basicOpExpressions.getArgument(i), variableContext);
                returnContext.addStatement(pair.getContextBlock());

                if (i == 0) {

                    //the initial array expression
                    returnExpression = pair.getJavaExpression();

                } else if (i < nJavaArgs - 1) {

                    //subscript by the next index
                    returnExpression = new JavaExpression.ArrayAccess(returnExpression, pair.getJavaExpression());

                } else if (i == nJavaArgs - 1) {

                    returnExpression = new JavaExpression.Assignment((JavaExpression.Nameable)returnExpression, pair.getJavaExpression());

                }
            }


            if (boxResult) {
                final ExpressionContextPair pair = returnTypeToCal(updatedElementType, returnExpression);
                returnExpression = pair.getJavaExpression();
                returnContext.addStatement(pair.getContextBlock());
            }

            return new ExpressionContextPair(returnExpression, returnContext);
        }

        throw new CodeGenerationException("Unrecognized foreign function kind: " + kind);
    }

    private boolean isArgStrict (QualifiedName qn) {
        return isArgStrict (qn.getUnqualifiedName());
    }

    /**
     * Generate code for non-recursive let.
     * @param let
     * @param scheme
     * @param unboxBodyType
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     *   Note: in the case of Scheme R (type 0), the javaExpression member will be null, and the let will be fully-expressed
     *         by the statements in the context.
     */
    private ExpressionContextPair generateLetNonRec(Expression.LetNonRec let,
                                                    Scheme scheme,
                                                    JavaTypeName unboxBodyType,
                                                    VariableContext variableContext) throws CodeGenerationException {

        // Add the let variable to the variable scope.
        VarInfo.LetNonRec varInfo = variableContext.addLetNonRecVar(QualifiedName.make(currentModuleName, let.getDefn().getVar()), let.getDefn().getVarType());


        // Determine if for the letvar definition can ignore laziness.
        boolean canIgnoreLaziness = canIgnoreLaziness(let.getDefn().getExpr(), variableContext);
        if (!canIgnoreLaziness) {
            // Check to see if the def is a call to a lifted var definition
            Expression defExpr = let.getDefn().getExpr();
            Expression.Var defVar = defExpr.asVar();
            if (defVar == null && defExpr.asAppl() != null) {
                Expression[] chain = appChain(defExpr.asAppl());
                defVar = chain[0].asVar();
            }
            if (defVar != null) {
                MachineFunction mf = module.getFunction(defVar.getName());
                if (mf != null && mf instanceof LECCLiftedLetVarMachineFunction) {
                    canIgnoreLaziness = ((LECCLiftedLetVarMachineFunction)mf).canIgnoreLaziness();
                }
            }
        }

        varInfo.setEvaluated(canIgnoreLaziness);

        // Set up the references to use when referring to this variable.
        // These are set up as PlaceHolder instances, since the nature of
        // the reference will be determined by how the variable is used.
        // i.e. single vs. multiple use, boxed vs. unboxed, strict vs. lazy
        if (canIgnoreLaziness) {
            if (varInfo.getUnboxedType() != null) {
                JavaExpression localVar = new LocalVariable(varInfo.getJavaName()+"$U", varInfo.getUnboxedType());
                varInfo.updateUnboxedReference(localVar);
                JavaExpression boxedDef = SCJavaDefn.boxExpression(varInfo.getUnboxedType(), localVar);
                varInfo.updateStrictReference(boxedDef);
                varInfo.updateLazyReference(boxedDef);
            } else {
                JavaExpression localVar = new LocalVariable(varInfo.getJavaName(), JavaTypeNames.RTVALUE);
                varInfo.updateStrictReference(localVar);
                varInfo.updateLazyReference(localVar);
            }
        } else {
            JavaExpression localVar = new JavaExpression.LocalVariable(varInfo.getJavaName(), JavaTypeNames.RTVALUE);
            varInfo.updateLazyReference(localVar);
            JavaExpression evaluatedVar = SCJavaDefn.createInvocation(localVar, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
            varInfo.updateStrictReference(evaluatedVar);
            if (varInfo.getUnboxedType() != null) {
                varInfo.updateUnboxedReference(SCJavaDefn.unboxValue(varInfo.getUnboxedType(), evaluatedVar));
            }
        }

        // Compile the body (i.e. the 'in' part).
        ExpressionContextPair compiledBody;
        if (scheme == Scheme.UNBOX_INTERNAL_SCHEME) {
            compiledBody = generateUnboxedArgument(unboxBodyType, let.getBody(), variableContext);
        } else if (scheme == Scheme.UNBOX_FOREIGN_SCHEME) {
            compiledBody = generateUnboxedForeignFunctionArgument(unboxBodyType, let.getBody(), variableContext);
        } else if (scheme == Scheme.R_SCHEME) {
            // Special case for scheme R (see Javadoc for this method).
            JavaStatement rStatement = genS_R(let.getBody(), variableContext);
            compiledBody = new ExpressionContextPair(null, rStatement);

        } else if (scheme == Scheme.E_SCHEME) {
            compiledBody = genS_E(let.getBody(), variableContext);

        } else if (scheme == Scheme.C_SCHEME) {
            compiledBody = genS_C(let.getBody(), variableContext);

        } else {
            throw new CodeGenerationException("Unrecognized let type: " + scheme);
        }


        if (canIgnoreLaziness) {
            if(varInfo.getUnboxedType() != null) {
                // Compile an unboxed definition function.
                // Compile the let variable definition to produce an unboxed value
                JavaTypeName unboxedType = varInfo.getUnboxedType();
                ExpressionContextPair compiledVarDef =
                    generateUnboxedArgument(unboxedType,
                                            let.getDefn().getExpr(),
                                            variableContext);


                // Now update the unboxed var definition.
                varInfo.updateUnboxedVarDef(compiledVarDef.getJavaExpression());
            } else {
                // Compile a strict definition function.
                // Compile the let variable definition to produce a fully evaluated boxed value.
                ExpressionContextPair compiledVarDef = genS_E(let.getDefn().getExpr(), variableContext);

                // Now update the strict var def.
                varInfo.updateStrictVarDef(compiledVarDef.getJavaExpression());
            }
        } else {
            // Compile the let variable definition to produce a lazy program graph.
            ExpressionContextPair compiledVarDef = genS_C(let.getDefn().getExpr(), variableContext);

            // Update the lazy var def.
            varInfo.updateLazyVarDef(compiledVarDef.getJavaExpression());

        }

        return compiledBody;
    }

    private JavaTypeName getTypeNameForForeignFunctionArg (ForeignFunctionInfo foreignFunctionInfo, int argNum) throws CodeGenerationException{

        return JavaTypeName.make(SCJavaDefn.getJavaArgumentType(foreignFunctionInfo, argNum));
    }

    private JavaTypeName getTypeNameForPrimitiveOpArg (BasicOpTuple bot, int argNum) throws CodeGenerationException {
        JavaTypeName tn = PrimOp.getTypeNameForPrimOpArgument(bot.getPrimitiveOp(), argNum);
        if (tn == null) {
            if (bot.getPrimitiveOp() == PrimOps.PRIMOP_FOREIGN_FUNCTION) {
                tn = getTypeNameForForeignFunctionArg (bot.getForeignFunctionInfo(), argNum);
            } else {
                throw new CodeGenerationException ("Attempt to retrieve argument type for " + bot.getName() + ", argNum = " + argNum + ".");
            }
        }
        return tn;
    }



    /**
     * Generate code for recursive let variable(s).
     * @param let
     * @param scheme
     * @param variableContext
     * @return ExpressionContextPair
     *   Note: in the case of Scheme R , the javaExpression member will be null, and the let will be fully-expressed
     *         by the statements in the context.
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateLetRec(Expression.LetRec let, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {
        Expression.Let.LetDefn[] defns = let.getDefns();

        VarInfo.LetRec letVars[] = new VarInfo.LetRec[defns.length];

        // First enhance the variable scope with the let variable names.
        for (int i = 0; i < defns.length; ++i) {
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementLetRecCount(defns.length);
            }

            Expression.Let.LetDefn def = defns [i];
            QualifiedName qn = QualifiedName.make(currentModuleName, def.getVar());
            letVars[i] = variableContext.addLetRecVar(qn, def.getVarType());
            LocalName lazyRef = new LocalName(letVars[i].getJavaName(), JavaTypeNames.RTVALUE);
            letVars[i].updateLazyReference(lazyRef);
            letVars[i].updateStrictReference(SCJavaDefn.createInvocation(lazyRef, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR));
        }


        // Now generate the definitions for the let vars.
        for (int i = 0; i < defns.length; ++i) {
            Expression.Let.LetDefn def = defns [i];
            ExpressionContextPair varDef = genS_C (def.getExpr(), variableContext);
            letVars[i].updateLazyVarDef(varDef.getJavaExpression());
        }

        ExpressionContextPair returnValue = null;
        if (scheme == Scheme.R_SCHEME) {
            // Special case for scheme R (see Javadoc for this method).
            JavaStatement rStatement = genS_R (let.getBody(), variableContext);
            returnValue = new ExpressionContextPair(null, rStatement);

        } else if (scheme == Scheme.E_SCHEME) {
            returnValue = genS_E (let.getBody(), variableContext);

        } else if (scheme == Scheme.C_SCHEME) {
            returnValue = genS_C (let.getBody(), variableContext);

        } else {
            throw new CodeGenerationException("Unrecognized let type: " + scheme);
        }

        return returnValue;
    }

    private boolean isNot (Expression e) {
        if (e.asAppl() != null &&
            e.asAppl().getE1().asVar() != null &&
            e.asAppl().getE1().asVar().getName().equals(CAL_Prelude.Functions.not)) {
            return true;
        }

        return false;
    }

    private ExpressionContextPair generateNot (Expression e, boolean boxResult, VariableContext variableContext) throws CodeGenerationException {
        // Get the argument.
        Expression arg = e.asAppl().getE2();
        ExpressionContextPair ecp = generateUnboxedArgument(JavaTypeName.BOOLEAN, arg, variableContext);
        JavaExpression not = new JavaExpression.OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, ecp.getJavaExpression());

        if (boxResult) {
            not = createMakeKernelBooleanInvocation(not);
        }

        return new ExpressionContextPair (not, ecp.getContextBlock());
    }

    private ExpressionContextPair generateAndOr (Expression e, boolean boxResult, VariableContext variableContext) throws CodeGenerationException {
        if (codeGenerationStats != null) {
            codeGenerationStats.incrementOptimizedAndOr();
        }

        // Unpack the basic op into subexpressions
        BasicOpTuple basicOpExpressions = BasicOpTuple.isAndOr (e);

        // Code a basic operation
        int op = basicOpExpressions.getPrimitiveOp ();
        int nArgs = basicOpExpressions.getNArguments ();

        if (nArgs < 0 || nArgs > 2) {
            throw new CodeGenerationException("Invalid basic operator arity: " + nArgs);
        }

        // The arguments for primitive ops are handled as a special case.
        // We want to avoid boxing/unboxing of primitive values wherever possible.
        Block generatedContext = new Block();
        JavaExpression args[] = new JavaExpression[nArgs];
        for (int i = 0; i < nArgs; ++i) {
            int argIndex = nArgs - i - 1;
            ExpressionContextPair pair = generateUnboxedPrimOpArgument(basicOpExpressions, basicOpExpressions.getArgument(argIndex), argIndex, variableContext);
            args[argIndex] = pair.getJavaExpression();
            generatedContext.addStatement(pair.getContextBlock());
        }

        JavaExpression javaExpression = PrimOp.getPrimOpDefinition(op, args);

        if (boxResult) {
            javaExpression = boxPrimitiveOpResult(op, javaExpression);
        }

        return new ExpressionContextPair(javaExpression, generatedContext);
    }

    /**
     * Generate the source code for a primitive op.
     * Note: args will be strictly evaluated.  Use generateAndOr() for lazy evaluation in the second arg.
     * @param e
     * @param boxResult
     * @param scheme
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generatePrimitiveOp (Expression e, boolean boxResult, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {

        // Unpack the basic op into subexpressions
        BasicOpTuple basicOpExpressions = BasicOpTuple.isBasicOp(e);

        if (!LECCMachineConfiguration.generateDirectPrimOpCalls()) {
            //when we are not generating direct primitive op calls we should not call this function,
            //other than the special case of generating code for the function that calls the primitive op
            //e.g. org.openquark.cal.internal.runtime.lecc.cal_Math.Sin will directly call java.lang.Math.sin but
            //we will not directly call java.lang.Math.sin anywhere else.

            if (!basicOpExpressions.getName().equals(getQualifiedName())) {
                throw new CodeGenerationException("Should not directly call a primitive operator when GEN_DIRECT_PRIMOP_CALLS is false.");
            }
        }

        // Code a basic operation
        int op = basicOpExpressions.getPrimitiveOp ();
        int nArgs = basicOpExpressions.getNArguments ();

        if (nArgs < 0) {
            throw new CodeGenerationException("Invalid basic operator arity: " + nArgs);
        }

        if (op == PrimOps.PRIMOP_FOREIGN_FUNCTION) {
            // This is a foreign function
            return generateForeignCall (e, boxResult, scheme, variableContext);
        }

        if (codeGenerationStats != null) {
            codeGenerationStats.incrementOptimizedPrimOp (basicOpExpressions.getName());
        }

        // The primitive op Prelude.eager is a special case.  We want to simply
        // compile the argument in a strict scheme.
        if (op == PrimOps.PRIMOP_EAGER) {
            return genS_E(basicOpExpressions.getArgument(0), variableContext);
        }

        // The arguments for primitive ops are handled as a special case.
        // We want to avoid boxing/unboxing of primitive values wherever possible.
        Block generatedContext = new Block();
        JavaExpression args[] = new JavaExpression[nArgs];
        for (int i = 0; i < nArgs; ++i) {
            int argIndex = nArgs - i - 1;
            ExpressionContextPair pair;
            if (op == PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT) {
                //Prelude.calValueToObject is an unusual primitive op in that it is non-strict in its argument
                pair = genS_C(basicOpExpressions.getArgument(argIndex), variableContext);
            } else
            {
                pair = generateUnboxedPrimOpArgument(basicOpExpressions, basicOpExpressions.getArgument(argIndex), argIndex, variableContext);
            }

            args[argIndex] = pair.getJavaExpression();
            generatedContext.addStatement(pair.getContextBlock());
        }

        JavaExpression javaExpression = PrimOp.getPrimOpDefinition(op, args);

        if (boxResult) {
            javaExpression = boxPrimitiveOpResult(op, javaExpression);
        }

        return new ExpressionContextPair(javaExpression, generatedContext);
    }

    /**
     * Construct a call that makes a new ErrorInfo object with the given parameters.
     *
     * @param errorInfo The information about where the error has occurred.
     * @return A call that generates the new error info object.
     */

    private JavaExpression getErrorInfo( Expression.ErrorInfo errorInfo ){

        // Sometimes the provided Expression.ErrorInfo may be null.
        // This can happen when generating code from a source model construct
        // that was dynamically generated, rather than being generated from
        // a CAL source file.
        if (errorInfo == null) {
            return LiteralWrapper.NULL;
        }

        QualifiedName topLevelFunctionName = errorInfo.getTopLevelFunctionName();
        int line = errorInfo.getLine();
        int column = errorInfo.getColumn();

        // '.' in the module name gets mapped to '_'
        // '_' in the module name gets mapped to "__"
        String errorVarName = topLevelFunctionName.getModuleName().toSourceText().replaceAll("_", "__").replace('.', '_') + "_" + topLevelFunctionName.getUnqualifiedName() + "_" + line + "_" + column;

        JavaExpression newErrorInfo = sharedValues.getStaticError(errorVarName);

        if (newErrorInfo == null) {
            // Initialize newErrorInfo
            JavaExpression args[] = new JavaExpression[4];
            args[0] = LiteralWrapper.make (topLevelFunctionName.getModuleName().toSourceText());
            args[1] = LiteralWrapper.make (topLevelFunctionName.getUnqualifiedName());
            args[2] = LiteralWrapper.make( Integer.valueOf(line) );
            args[3] = LiteralWrapper.make( Integer.valueOf(column) );

            JavaTypeName paramTypes[] = new JavaTypeName[4];
            paramTypes[0] = JavaTypeName.STRING;
            paramTypes[1] = JavaTypeName.STRING;
            paramTypes[2] = JavaTypeName.INT;
            paramTypes[3] = JavaTypeName.INT;

            newErrorInfo = new ClassInstanceCreationExpression(JavaTypeName.ERRORINFO, args, paramTypes);

            // Add the expression to the map of static error info objects to be created.
            sharedValues.addStaticError(errorVarName, newErrorInfo);
        }

        return new JavaField.Static(thisTypeName, errorVarName, JavaTypeName.ERRORINFO);
    }

    /**
     * Generate a call to RTSupercombinator.badSwitchIndex.
     *
     * @param errorInfo
     * @return the call
     */
    private JavaExpression getBadSwitchIndexCall (Expression.ErrorInfo errorInfo) {
        final JavaExpression jErrorInfo;
        if (errorInfo != null) {
            jErrorInfo = getErrorInfo (errorInfo);
        } else {
            jErrorInfo = LiteralWrapper.NULL;
        }

        MethodInvocation mi =
            new MethodInvocation.Instance (null,
                                           "badSwitchIndex",
                                           jErrorInfo,
                                           JavaTypeName.ERRORINFO,
                                           JavaTypeNames.RTVALUE,
                                           MethodInvocation.InvocationType.VIRTUAL);

        return mi;
    }

    /**
     * Generate a call to RTSupercombinator.unhandledSwitchIndexForIntPattern.
     *
     * @param errorInfo
     * @return the call
     */
    private JavaExpression getUnhandledSwitchIndexForIntPatternCall(Expression.ErrorInfo errorInfo) {

        final JavaExpression jErrorInfo;
        if (errorInfo != null) {
            jErrorInfo = getErrorInfo (errorInfo);
        } else {
            jErrorInfo = LiteralWrapper.NULL;
        }

        MethodInvocation mi =
            new MethodInvocation.Instance(null,
                "unhandledSwitchIndexForIntPattern",
                jErrorInfo,
                JavaTypeName.ERRORINFO,
                JavaTypeNames.RTVALUE,
                MethodInvocation.InvocationType.VIRTUAL);

        return mi;
    }

    /**
     * Generate a call to RTSupercombinator.unhandledSwitchIndexForCharPattern.
     *
     * @param errorInfo
     * @return the call
     */
    private JavaExpression getUnhandledSwitchIndexForCharPatternCall(Expression.ErrorInfo errorInfo) {

        final JavaExpression jErrorInfo;
        if (errorInfo != null) {
            jErrorInfo = getErrorInfo (errorInfo);
        } else {
            jErrorInfo = LiteralWrapper.NULL;
        }

        MethodInvocation mi =
            new MethodInvocation.Instance(null,
                "unhandledSwitchIndexForCharPattern",
                jErrorInfo,
                JavaTypeName.ERRORINFO,
                JavaTypeNames.RTVALUE,
                MethodInvocation.InvocationType.VIRTUAL);

        return mi;
    }

    /**
     * @param errorInfo The error information to pass into the badValue call.
     * @param message The message associated with the bad value call.
     * @return An expression that invokes the badValue call on the given target.
     */

    private JavaExpression getBadValueCall(Expression.ErrorInfo errorInfo, JavaExpression message){
        if (errorInfo == null){
            return new MethodInvocation.Static (JavaTypeNames.RTVALUE, "badValue", message, JavaTypeName.STRING, JavaTypeNames.RTVALUE);
        }
        else{
            JavaExpression[] args = {
                    getErrorInfo(errorInfo),
                    message
            };

            JavaTypeName[] paramTypes = {
                    JavaTypeName.ERRORINFO,
                    JavaTypeName.STRING
            };

            return new MethodInvocation.Static(JavaTypeNames.RTVALUE, "badValue", args, paramTypes, JavaTypeNames.RTVALUE);
        }
    }

    /**
     * This takes the expression for a primitive operation argument and generates the
     * appropriate source to produce an unboxed (i.e. java primitive) value.
     * @param op
     * @param e
     * @param argNum
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateUnboxedPrimOpArgument (BasicOpTuple op,
                                                                 Expression e,
                                                                 int argNum,
                                                                 VariableContext variableContext) throws CodeGenerationException {
        JavaTypeName argType = getTypeNameForPrimitiveOpArg(op, argNum);
        return generateUnboxedArgument(argType, e, variableContext);
    }

    /**
     * Verify that the desired unboxed type and the unboxed type of the expression are compatible.
     * @param desiredType
     * @param expressionType
     * @throws CodeGenerationException
     */
    private void verifyUnboxType (JavaTypeName desiredType, JavaTypeName expressionType) throws CodeGenerationException {
        if (desiredType == null) {
            // We don't care about the actual type we get so it doesn't matter what the
            // expression type is.
            return;
        }

        if (desiredType.equals(expressionType)) {
            // If the types are the same then everything is OK.
            return;
        }

        if (desiredType.equals(JavaTypeName.INT) && expressionType.equals(JavaTypeName.VOID)) {
            // It is valid to unbox a foreign function of type void to an int since the CAL
            // representation of void is the unit type ().  This is handled as an int internally.
            return;
        }

        if (desiredType instanceof JavaTypeName.Primitive != expressionType instanceof JavaTypeName.Primitive) {
            // Trying to treat an object as a primitive or vice versa.  This is not allowed.
            throw new CodeGenerationException ("Unboxed type mismatch in " + getModuleName() + "." + getFunctionName() + " : expression type = " + expressionType + ", desiredType = " + desiredType);
        }

        if (desiredType instanceof JavaTypeName.Primitive && !desiredType.equals(expressionType)) {
            // The desired and expression types are both primitives but not the same primitive.  This is not allowed.
            throw new CodeGenerationException ("Unboxed type mismatch in " + getModuleName() + "." + getFunctionName() + " : expression type = " + expressionType + ", desiredType = " + desiredType);
        }

        if (desiredType instanceof JavaTypeName.Reference.Object != expressionType instanceof JavaTypeName.Reference.Object) {
            // This means that we are trying to treat to equate an array and a non-array.
            // This is valid if we are treating an array as an object.
            if (!desiredType.equals(JavaTypeName.OBJECT)) {
                throw new CodeGenerationException ("Unboxed type mismatch in " + getModuleName() + "." + getFunctionName() + " : expression type = " + expressionType + ", desiredType = " + desiredType);
            }
        }

        if (desiredType instanceof JavaTypeName.Reference.Object && expressionType instanceof JavaTypeName.Reference.Object) {
            // Check to see that objects types are compatible.
            // i.e. it is valid to treat a String as an Object, but not vice versa.
            String dtn = ((JavaTypeName.Reference.Object)desiredType).getName();
            String etn = ((JavaTypeName.Reference.Object)expressionType).getName();
            try {
                ClassLoader foreignClassLoader = module.getForeignClassLoader();
                Class<?> dtc = Class.forName(dtn, true, foreignClassLoader);
                Class<?> etc = Class.forName(etn, true, foreignClassLoader);
                if (!dtc.isAssignableFrom(etc)) {
                    throw new CodeGenerationException ("Unboxed type mismatch in " + getModuleName() + "." + getFunctionName() + " : expression type = " + expressionType + ", desiredType = " + desiredType);
                }
            } catch (ClassNotFoundException e) {
                throw new CodeGenerationException ("Unable to determine unboxed type compatability in  " + getModuleName() + "." + getFunctionName() + " : expression type = " + expressionType + ", desiredType = " + desiredType);
            }
        }

        // If both types are arrays verify that the element types are compatible.
        if (desiredType instanceof JavaTypeName.Reference.Array && expressionType instanceof JavaTypeName.Reference.Array) {
            verifyUnboxType(((JavaTypeName.Reference.Array)desiredType).getElementType(), ((JavaTypeName.Reference.Array)expressionType).getElementType());
        }

    }

    /**
     * Extract a value of the 'unboxType' from the given expression.
     * These arguments are for passing between CAL functions.
     * Unboxed arguments for foreign functions are handled differently.
     *
     * @param unboxType - the desired type of the unboxed value.  Null if we don't care about the type.
     * @param e
     * @param variableContext
     * @return The java structure to unbox the value.
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateUnboxedArgument (JavaTypeName unboxType, Expression e, VariableContext variableContext) throws CodeGenerationException {

        // Because this is a top-level entry point for a code generation of previously unseen expression
        // in a strict context we should strip eager.
        e = stripEager (e);
        boolean primitiveUnboxType = unboxType == null || unboxType instanceof JavaTypeName.Primitive;

        // If the argType is a CalValue the various shortcuts involving directly retrieving an unboxed value don't apply.
        if (unboxType == null || (!unboxType.equals(JavaTypeName.CAL_VALUE) && !unboxType.equals(JavaTypeNames.RTVALUE))) {
            // First check if this is a literal.
            if (e.asLiteral() != null) {
                if (primitiveUnboxType ||
                 e.asLiteral().getLiteral() instanceof String ||
                 e.asLiteral().getLiteral() instanceof BigInteger) {
                    KernelLiteral kernelLit = getKernelLiteral (e.asLiteral().getLiteral());
                    return new ExpressionContextPair(kernelLit.getUnboxedReference());
                } else {
                    throw new CodeGenerationException ("Unboxed type mismatch in " + getModuleName() + "." + getFunctionName() + " : expression type = " + e.asLiteral().getLiteral().getClass().getName() + ", desiredType = " + unboxType);
                }
            }

            // If the expression is a primitive op compile it as an unboxed op.
            //We would not want to directly call primitive operators when doing function tracing.
            //This will have the effect of ensuring that they get traced when called.
            BasicOpTuple bot;
            if (LECCMachineConfiguration.generateDirectPrimOpCalls() &&
                (bot = BasicOpTuple.isBasicOp(e)) != null) {

                // We need to be sure that the primitiveness of the operation/foreign function and the
                // desired unbox type match.  For example an operation that produces an int can either unbox to
                // an int or an Object.

                if (bot.getPrimitiveOp() == PrimOps.PRIMOP_FOREIGN_FUNCTION) {
                    // We know that this is an unboxed argument to a CAL function.
                    // The unboxed value cannot be void.  If we are calling a foreign
                    // function with return type of void we can't do a direct call.  We
                    // need to call the CAL wrapper function which will return the CAL
                    // equivalent to void. i.e. Unit
                    ForeignFunctionInfo ffi = bot.getForeignFunctionInfo();
                    Class<?> foreignReturnType = SCJavaDefn.getJavaReturnType(ffi);

                    verifyUnboxType(unboxType, JavaTypeName.make(foreignReturnType));


                    if(foreignReturnType.equals(void.class)) {
                        // This is the special case of a void foreign function.  The return type needs to
                        // be converted to the CAL Unit type.
                        // Compile the expression strictly, this will result in an RTValue which can be unboxed to the int equivalent
                        // of the unit type.
                        ExpressionContextPair argResult = genS_E (e, variableContext);
                        return new ExpressionContextPair(SCJavaDefn.unboxValue (unboxType, argResult.getJavaExpression()), argResult.getContextBlock());

                    } else if (bot.getPrimitiveOp() == PrimOps.PRIMOP_EAGER) {

                        throw new CodeGenerationException ("PRIMOP_EAGER encountered in generateUnboxedArgument.");

                    } else if (foreignReturnType.isPrimitive() == primitiveUnboxType){
                        // Using unsafeCoerce and input/output we can get CAL code that corresponds to
                        // treating primitives as objects and vice versa.  For example the Java primitive int and
                        // the Java class Integer get equated.
                        // We need to check that the foreign type of the expression being compiled has the same
                        // primitiveness as the unboxed type we are trying to generate.

                        return generatePrimitiveOp(e, false, Scheme.E_SCHEME, variableContext);
                    }  else {
                        throw new CodeGenerationException ("Primitivenes mismatch in " + getModuleName() + "." + getFunctionName() + " in generateUnboxedArgument.  Expression is " + foreignReturnType.toString() + " expected type is " + unboxType);
                    }
                } else {
                    if (bot.getPrimitiveOp() != PrimOps.PRIMOP_OBJECT_TO_CAL_VALUE &&
                        bot.getPrimitiveOp() != PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT) {
                        verifyUnboxType (unboxType, PrimOp.getTypeNameForPrimOp(bot.getPrimitiveOp()));
                        return generatePrimitiveOp(e, false, Scheme.E_SCHEME, variableContext);
                    }
                }
            }

            if (isNot(e)) {
                verifyUnboxType(unboxType, JavaTypeName.BOOLEAN);
                return generateNot (e, false, variableContext);
            }

            if (BasicOpTuple.isAndOr(e) != null) {
                verifyUnboxType(unboxType, JavaTypeName.BOOLEAN);
                return generateAndOr(e, false, variableContext);
            }

            if (e.asVar() != null) {
                Expression.Var var = e.asVar();
                if (variableContext.isLocalVariable(var.getName())) {
                    if (unboxType != null && variableContext.getUnboxedType(var.getName()) != null) {
                        verifyUnboxType(unboxType, variableContext.getUnboxedType(var.getName()));
                    }

                    // At this point we know that the desired unboxType is null (i.e. to be ignored) or
                    // is compatible with the unboxed type of the variable (since we called verifyUnboxtype).
                    // If the desired type is null and the variable can be unboxed we simply use the unboxed
                    // reference.
                    // If the desired unbox type is not null and is equal to the variables unboxed type
                    // we can simply use the unboxed reference.
                    // If the desired unbox type is Object we can simply use the variables unboxed reference
                    // since we know the variable can't be primitive (because we called verifyUnboxType) and
                    // all non-primitives in Java are an instance of Object.
                    if ((unboxType == null && variableContext.getUnboxedType(var.getName()) != null) ||
                        (unboxType != null &&
                            (unboxType.equals(variableContext.getUnboxedType(var.getName())) || unboxType.equals(JavaTypeName.OBJECT)))) {
                        JavaExpression unboxedRef = variableContext.getUnboxedReference(var.getName());
                        if (unboxedRef != null) {
                            return new ExpressionContextPair(unboxedRef);
                        }
                    }
                }
            }

            // Is e an application of a saturated constructor?
            if (ConstructorOpTuple.isConstructorOp(e, true) != null) {
                ConstructorOpTuple  constructorOpExpressions = ConstructorOpTuple.isConstructorOp(e, false);

                DataConstructor dc = constructorOpExpressions.getDataConstructor ();

                // If we are dealing with a data constructor for the CAL type boolean we want to optimize
                // by substituting the literal boolean values.
                if (isTrueOrFalseDataCons(dc)) {
                    verifyUnboxType(unboxType, JavaTypeName.BOOLEAN);
                    LiteralWrapper boolWrapper = LiteralWrapper.make(Boolean.valueOf(isTrueDataCons(dc)));
                    return new ExpressionContextPair(boolWrapper);
                }

                if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                    if (SCJavaDefn.isEnumDataType(dc)) {
                        verifyUnboxType(unboxType, JavaTypeName.INT);
                           return new ExpressionContextPair (LiteralWrapper.make (Integer.valueOf(dc.getOrdinal())));
                    }
                }
            }

            // Is e a dc field selection?
            if (e.asDataConsSelection() != null) {
                return generateUnboxedDCFieldSelection(e.asDataConsSelection(), unboxType, variableContext);
            }

            // Is e a non-recursive let variable.
            if (e.asLetNonRec() != null) {
                return generateLetNonRec(e.asLetNonRec(), Scheme.UNBOX_INTERNAL_SCHEME, unboxType, variableContext);
            }

            // Try generating a direct call to a supercombinators fUnboxed method.
            ExpressionContextPair argResult = buildUnboxedDirectCall (e, unboxType, Scheme.UNBOX_INTERNAL_SCHEME, variableContext);
            if (argResult != null) {
                return  new ExpressionContextPair(argResult.getJavaExpression(), argResult.getContextBlock());
            }
        }
        // Compile the expression Strictly.
        ExpressionContextPair argResult = genS_E (e, variableContext);
        return new ExpressionContextPair(SCJavaDefn.unboxValue (unboxType, argResult.getJavaExpression()), argResult.getContextBlock());
    }


    /**
     * Generate the source code for an unboxed foreign function arg.
     * @param unboxType
     * @param e
     * @param variableContext
     * @return ExpressionContextPair
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateUnboxedForeignFunctionArgument (JavaTypeName unboxType,
                                                                          Expression e,
                                                                          VariableContext variableContext) throws CodeGenerationException {

        // Because this is a top-level entry point for a code generation of previously unseen expression
        // in a strict context we should strip eager.
        e = stripEager(e);

        // If the argType is a CalValue the various shortcuts involving directly retrieving an unboxed value don't apply.
        if (!unboxType.equals(JavaTypeName.CAL_VALUE) && !unboxType.equals(JavaTypeNames.RTVALUE)) {
            // First check if this is a literal.
            if (e.asLiteral() != null) {
                if (unboxType instanceof JavaTypeName.Primitive ||
                 e.asLiteral().getLiteral() instanceof String ||
                 e.asLiteral().getLiteral() instanceof BigInteger) {
                    KernelLiteral kernelLit = getKernelLiteral (e.asLiteral().getLiteral());
                    return new ExpressionContextPair(kernelLit.getUnboxedReference());
                } else {
                    throw new CodeGenerationException ("Unboxed type mismatch in " + getModuleName() + "." + getFunctionName() + " : expression type = " + e.asLiteral().getLiteral().getClass().getName() + ", desiredType = " + unboxType);
                }
            }

            // If the expression is a primitive op compile it as an unboxed op.
            //we don't directly call primitive operators if doing function tracing.
            //This will have the effect of ensuring that they get traced when called.
            BasicOpTuple bot;
            if (LECCMachineConfiguration.generateDirectPrimOpCalls() &&
                (bot = BasicOpTuple.isBasicOp(e)) != null) {

                // Generally speaking if an argument to a foreign function is expressed as a primitive op/foreign function we can
                // assume that the unboxed return value of the argument expression is of the correct type to pass.
                // One exception is when dealing with an argument expression of type Prelude.CalValue.  In this case we need to
                // get the Prelude.CalValue, evaluate, and then unbox the actual type needed by the foreign function.
                // Another is when dealing with Prelude.eager
                // Finally we need to be sure that the primitiveness of the operation/foreign function and the
                // desired unbox type match.  For example an operation that produces an int can either unbox to
                // an int or an Object.
                if (bot.getPrimitiveOp() == PrimOps.PRIMOP_FOREIGN_FUNCTION) {
                    // If this foreign SC is of type internal value we can't just get the unboxed value and pass
                    final ForeignFunctionInfo ffi = bot.getForeignFunctionInfo();
                    final Class<?> foreignReturnType = SCJavaDefn.getJavaReturnType(ffi);

                    // If this foreign SC is of type Prelude.CalValue we can't just get the unboxed value and pass
                    final boolean hasCalValueReturnType = isCalValueClass(foreignReturnType);
                    if (hasCalValueReturnType){
                        ExpressionContextPair argResult = genS_E (e, variableContext);
                        return new ExpressionContextPair(unboxValue(unboxType, SCJavaDefn.createInvocation (argResult.getJavaExpression(), SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR)), argResult.getContextBlock());
                    }
                    verifyUnboxType(unboxType, JavaTypeName.make(foreignReturnType));
                    return generatePrimitiveOp (e, false, Scheme.E_SCHEME, variableContext);
                } else
                if (bot.getPrimitiveOp() == PrimOps.PRIMOP_EAGER) {
                    throw new CodeGenerationException ("PRIMOP_EAGER encountered in generateUnboxedForeignFunctionArgument.");
                } else
                if (bot.getPrimitiveOp() != PrimOps.PRIMOP_OBJECT_TO_CAL_VALUE &&
                    bot.getPrimitiveOp() != PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT) {
                    verifyUnboxType(unboxType, PrimOp.getTypeNameForPrimOp(bot.getPrimitiveOp()));
                    return generatePrimitiveOp(e, false, Scheme.E_SCHEME, variableContext);
                }
            }

            if (isNot(e)) {
                verifyUnboxType(unboxType, JavaTypeName.BOOLEAN);
                   return generateNot (e, false, variableContext);
            }

            if (BasicOpTuple.isAndOr(e) != null) {
                verifyUnboxType(unboxType, JavaTypeName.BOOLEAN);
                return generateAndOr(e, false, variableContext);
            }

            if (e.asVar() != null) {
                Expression.Var var = e.asVar();
                if (variableContext.isLocalVariable(var.getName())) {
                    final JavaTypeName unboxedTypeOfVar = variableContext.getUnboxedType(var.getName());
                    if (unboxedTypeOfVar != null) {
                        verifyUnboxType(unboxType, unboxedTypeOfVar);
                    }

                    // At this point we know that the desired unboxType is compatible with the
                    // unboxed type of the variable (since we called verifyUnboxtype).
                    // If the desired unbox type is not null and is equal to the variables unboxed type
                    // we can simply use the unboxed reference.
                    // If the desired unbox type is Object we can simply use the variables unboxed reference
                    // since we know the variable can't be primitive (because we called verifyUnboxType) and
                    // all non-primitives in Java are an instance of Object.
                    if(unboxType.equals(unboxedTypeOfVar) ||
                       unboxType.equals(JavaTypeName.OBJECT)) {
                        JavaExpression unboxedRef = variableContext.getUnboxedReference(var.getName());
                        if (unboxedRef != null) {
                            return new ExpressionContextPair (unboxedRef);
                        }
                    } else {
                        if (unboxedTypeOfVar != null) {
                            // If the variable has an unboxed type, then in fact it should be the same as the desired unboxed type
                            // or should be java.lang.Object
                            throw new CodeGenerationException("The code generator should not be generating code where the desired unboxed type is not java.lang.Object nor the unboxed type of the variable.");
                        }
                    }
                }
            }

            // Is e an application of a saturated constructor?
            if (ConstructorOpTuple.isConstructorOp(e, true) != null) {
                ConstructorOpTuple  constructorOpExpressions = ConstructorOpTuple.isConstructorOp(e, false);

                DataConstructor dc = constructorOpExpressions.getDataConstructor ();

                // If we are dealing with a data constructor for the CAL type boolean we want to optimize
                // by substituting and instance of the literal RTKernel.CAL_Boolean.
                if (isTrueOrFalseDataCons(dc)) {
                    verifyUnboxType(unboxType, JavaTypeName.BOOLEAN);
                    LiteralWrapper boolWrapper = LiteralWrapper.make(Boolean.valueOf(isTrueDataCons(dc)));
                    return new ExpressionContextPair(boolWrapper);
                }

                if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                    if (SCJavaDefn.isEnumDataType(dc)) {
                        verifyUnboxType(unboxType, JavaTypeName.INT);
                        return new ExpressionContextPair (LiteralWrapper.make (Integer.valueOf(dc.getOrdinal())));
                    }
                }

            }

            // Is e a dc field selection?
            if (e.asDataConsSelection() != null) {
                return generateUnboxedDCFieldSelection(e.asDataConsSelection(), unboxType, variableContext);
            }

            // Is e a non-recursive let variable.
            if (e.asLetNonRec() != null) {
                return generateLetNonRec(e.asLetNonRec(), Scheme.UNBOX_FOREIGN_SCHEME, unboxType, variableContext);
            }

            // Try generating a direct call to the supercombinator fUnboxed method.
            ExpressionContextPair argResult = buildUnboxedDirectCall(e, unboxType, Scheme.UNBOX_FOREIGN_SCHEME, variableContext);
            if (argResult != null) {
                return new ExpressionContextPair(argResult.getJavaExpression(), argResult.getContextBlock());
            }
        }

        // Compile the expression Strictly.
        ExpressionContextPair argResult = genS_E (e, variableContext);
        return new ExpressionContextPair(unboxValue(unboxType, argResult.getJavaExpression()), argResult.getContextBlock());
    }

    /**
     * Generate code for a DataConsSelection expression which returns an
     * unboxed form of the field
     * @param dcs
     * @param unboxedType - the desired type of the unboxed value.  Null if the type doesn't matter.
     * @param variableContext
     * @return java code to generate an unboxed field value
     * @throws CodeGenerationException
     */
    private ExpressionContextPair generateUnboxedDCFieldSelection (DataConsSelection dcs, JavaTypeName unboxedType, VariableContext variableContext) throws CodeGenerationException {
        // If the field is a strict primitive we can use
        // the version of getFieldByIndex() which returns an unboxed
        // value.
        // Assuming of course that the expected unboxed type is compatible with
        // the type of the field.
        DataConstructor dc = dcs.getDataConstructor();
        TypeExpr[] fieldTypes = SCJavaDefn.getFieldTypesForDC(dc);
        TypeExpr fieldType = fieldTypes[dcs.getFieldIndex()];

        if (unboxedType == null) {
            // The type of the unboxed value is irrelevent so set it to be the same type as the field.
            if (dc.isArgStrict(dcs.getFieldIndex())) {
                unboxedType = SCJavaDefn.typeExprToTypeName(fieldType);
            } else {
                unboxedType = JavaTypeNames.RTVALUE;
            }
        }

        // Check that the field is strict and can be unboxed.
        boolean directlyRetrieveUnboxed = dc.isArgStrict(dcs.getFieldIndex()) &&
                                          SCJavaDefn.canTypeBeUnboxed(fieldType);

        // Now check compatability of the expected type and the field type.
        if (directlyRetrieveUnboxed) {
            verifyUnboxType(unboxedType, SCJavaDefn.typeExprToTypeName(fieldType));
        }

        if (directlyRetrieveUnboxed) {

            ExpressionContextPair ecp = genS_E(dcs.getDCValueExpr(), variableContext);
            JavaExpression target = new CastExpression (JavaTypeNames.RTCONS, ecp.getJavaExpression());

            // Call the unboxed version of getFieldByIndex.
            // In RTCons we declare versions of getFieldByIndex_As_...() for each of the Java primitive types,
            // Object, and java.lang.String.
            // At this point we need to decide which one to call.

            boolean retrieveAsObject = !(unboxedType instanceof JavaTypeName.Primitive) && !unboxedType.equals(JavaTypeName.STRING);

            JavaExpression mi;
            if (retrieveAsObject) {
                mi = new MethodInvocation.Instance(target,
                        "getFieldByIndex_As_Object",
                        new JavaExpression[] {
                                LiteralWrapper.make(Integer.valueOf(dcs.getDataConstructor().getOrdinal())),
                                LiteralWrapper.make(Integer.valueOf(dcs.getFieldIndex())),
                                getErrorInfo(dcs.getErrorInfo()) },
                        new JavaTypeName[] {
                                JavaTypeName.INT,
                                JavaTypeName.INT,
                                JavaTypeName.ERRORINFO },
                        JavaTypeName.OBJECT,
                        MethodInvocation.InvocationType.VIRTUAL);
                if (!unboxedType.equals(JavaTypeName.OBJECT)) {
                    mi = new JavaExpression.CastExpression(unboxedType, mi);
                }
            } else {
                mi = new MethodInvocation.Instance (target,
                                                   "getFieldByIndex_As_" + SCJavaDefn.getNameForPrimitive(fieldTypes[dcs.getFieldIndex()]),
                                                   new JavaExpression[] {
                                                        LiteralWrapper.make(Integer.valueOf(dcs.getDataConstructor().getOrdinal())),
                                                        LiteralWrapper.make(Integer.valueOf(dcs.getFieldIndex())),
                                                        getErrorInfo(dcs.getErrorInfo())},
                                                   new JavaTypeName[] {
                                                        JavaTypeName.INT,
                                                        JavaTypeName.INT,
                                                        JavaTypeName.ERRORINFO},
                                                   SCJavaDefn.typeExprToTypeName(fieldTypes[dcs.getFieldIndex()]),
                                                   MethodInvocation.InvocationType.VIRTUAL);
            }
            return new ExpressionContextPair (mi, ecp.getContextBlock());
        }

        // Compile the expression Strictly and then unbox the return.
        ExpressionContextPair argResult = genS_E (dcs, variableContext);
        return new ExpressionContextPair(unboxValue(unboxedType, argResult.getJavaExpression()), argResult.getContextBlock());

    }

    /**
     * Determine if the ith argument is of an unboxable type.
     * @param i
     * @return true if argument i is unboxable.
     */
    boolean isArgUnboxable(int i) throws CodeGenerationException {
        if (argumentTypes[i] == null) {
            return false;
        }

        return SCJavaDefn.canTypeBeUnboxed(argumentTypes[i]);
    }

    /**
     * Determine if the named argument is of an unboxable type.
     * @param name
     * @return true if the arg is unboxable.
     */
    private boolean isArgUnboxable (String name) throws CodeGenerationException {
        for (int i = 0; i < argumentNames.length; ++i) {
            if (argumentNames[i].equals(name)) {
                return isArgUnboxable(i);
            }
        }

        return false;
    }

    /**
     * Determine if the named argument is of an unboxable type.
     * @param name
     * @return true if the arg is unboxable.
     */
    private boolean isArgUnboxable (QualifiedName name) throws CodeGenerationException {
        return isArgUnboxable(name.getUnqualifiedName());
    }

    /**
     * Return the JavaTypeName for the ith argument.
     * @param i
     * @return JavaTypeName of the ith argument.
     */
    JavaTypeName getArgumentTypeName (int i) throws CodeGenerationException {
        if (i < argumentTypes.length && argumentTypes[i] != null) {
            return SCJavaDefn.typeExprToTypeName(argumentTypes[i]);
        }

        return JavaTypeNames.RTVALUE;
    }

    JavaTypeName getArgumentTypeName (String name) throws CodeGenerationException {
        for (int i = 0; i < argumentNames.length; ++i) {
            if (argumentNames[i].equals(name)) {
                return getArgumentTypeName(i);
            }
        }

        return JavaTypeNames.RTVALUE;
    }


    /**
     * Return the type of the ith argument.
     * @param i
     * @return the argument type.
     */
    TypeExpr getArgumentType(int i) {
        return argumentTypes[i];
    }

    /**
     * Get a name corresponding to a primitive type.
     * @param typeExpr
     * @return the name
     * @throws CodeGenerationException
     */
    static String getNameForPrimitive (TypeExpr typeExpr) throws CodeGenerationException {
        JavaTypeName typeName = typeExprToTypeName(typeExpr);
        return getNameForPrimitive(typeName);
    }

    /**
     * Get a name corresponding to a primitive type.
     * @param typeName
     * @return the name
     * @throws CodeGenerationException
     */
    static String getNameForPrimitive (JavaTypeName typeName) throws CodeGenerationException {
        switch (typeName.getTag()) {

        case JavaTypeName.BOOLEAN_TAG: return "Boolean";
        case JavaTypeName.BYTE_TAG: return "Byte";
        case JavaTypeName.SHORT_TAG: return "Short";
        case JavaTypeName.CHAR_TAG: return "Character";
        case JavaTypeName.INT_TAG: return "Int";
        case JavaTypeName.LONG_TAG: return "Long";
        case JavaTypeName.DOUBLE_TAG: return "Double";
        case JavaTypeName.FLOAT_TAG: return "Float";

        case JavaTypeName.OBJECT_TAG:
        {
            String name = typeName.getFullJavaSourceName();
            return CALToJavaNames.fixupVarName(name);
        }

        case JavaTypeName.ARRAY_TAG:
        {
            JavaTypeName.Reference.Array arrayType = (JavaTypeName.Reference.Array)typeName;
            int nDimensions = arrayType.getNDimensions();
            JavaTypeName elementType = arrayType.getElementType();

            // Get the source for the element type, and append "[]" for each dimension.
            StringBuilder sb = new StringBuilder(getNameForPrimitive(elementType));
            for (int i = 0; i < nDimensions; i++) {
                sb.append("_Array");
            }

            return sb.toString();

        }

        default:
        {
            throw new CodeGenerationException("Unable to return name for primitive type: " + typeName.getName());
        }

        }

    }

    /**
     * Returns true if the given TypeExpr corresponds to a foreign type that is
     * not a primitive unboxed java type.
     * @param typeExpr
     * @return true/false
     */
    static boolean doesTypeUnboxToObjectDerivative (TypeExpr typeExpr) throws CodeGenerationException {
        if (typeExpr == null) {
            return false;
        }

        TypeConsApp typeCons = typeExpr.rootTypeConsApp();

        if (typeCons != null && typeCons.getNArgs() == 0) {
            if (typeCons.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
                return false;
            }

            // Zero arity data constructors are treated as int.
            if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                if (isEnumDataType (typeCons)) {
                    return false;
                }
            }

            if(typeCons.getForeignTypeInfo() != null) {
                Class<?> foreignClass = SCJavaDefn.getForeignType(typeCons.getForeignTypeInfo());

                // If the class is CalValue or one of the Java primitives we don't treat
                // this as corresponding to an unboxed Object type.
                if (isCalValueClass(foreignClass)|| foreignClass.isPrimitive()) {
                    return false;
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given TypeExpr corresponds to a type which can be
     * represented internally with an unboxed value.
     * Unboxed values are either a Java primitive or a Java Object (i.e. anything
     * derived from java.lang.Object).
     * @param typeExpr
     * @return true/false
     */
    static boolean canTypeBeUnboxed (TypeExpr typeExpr) throws CodeGenerationException {
        if (typeExpr == null) {
            return false;
        }

        TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
        if (typeConsApp != null && typeConsApp.getNArgs() == 0) {

            if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
                return true;
            }

            if (typeConsApp.getForeignTypeInfo() != null && !isCalValueClass(SCJavaDefn.getForeignType(typeConsApp.getForeignTypeInfo()))) {
                return true;
            }

            if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                if (isEnumDataType (typeConsApp)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * An enumeration type is a:
     * -non parametric type (i.e. the type has 0 arity)
     * -not a foreign type
     * -there is at least one data constructor
     * -all data constructors have 0 arity
     * -it is not Prelude.Boolean
     * For example, Prelude.Ordering is an enumeration type.
     *
     * @param typeExpr
     * @return true if the given type is an enumeration, according to the above definition.
     */
    static boolean isEnumDataType (TypeExpr typeExpr) {
        TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
        if (typeConsApp == null) {
            return false;
        }

        return TypeExpr.isEnumType(typeConsApp.getRoot());
    }

    /**
     * @param typeExpr
     * @return true if the data type is self referential
     */
    static private boolean isSelfReferentialDataType (TypeExpr typeExpr) {
        TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
        if (typeConsApp == null) {
            return false;
        }

        TypeConstructor typeCons = typeConsApp.getRoot();

        for (int i = 0, n = typeCons.getNDataConstructors(); i < n; ++i) {
            DataConstructor dc = typeCons.getNthDataConstructor(i);
            TypeExpr[] fieldTypes = SCJavaDefn.getFieldTypesForDC(dc);
            for (int j = 0, k = fieldTypes.length; j < k; ++j) {
                TypeExpr fieldType = fieldTypes[j];
                TypeConsApp fieldTc = fieldType.rootTypeConsApp();
                if (fieldTc != null &&
                    (typeConsApp.sameType(fieldTc) || fieldTc.getRoot().equals(typeCons))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
    /**
     * An enumeration type is a:
     * -non parametric type (i.e. the type has 0 arity)
     * -not a foreign type
     * -there is at least one data constructor
     * -all data constructors have 0 arity
     * -it is not Prelude.Boolean
     * For example, Prelude.Ordering is an enumeration type.
     *
     * @param dc
     * @return true if the given DataConstructor belongs to an 'enumeration' data type.
     */
    static boolean isEnumDataType (DataConstructor dc) {
        return TypeExpr.isEnumType(dc.getTypeConstructor());
    }



    /**
     * Go from a TypeExpr to the corresponding JavaTypeName.
     * @param typeExpr
     * @return a JavaTypeName
     */
    static JavaTypeName typeExprToTypeName (TypeExpr typeExpr) throws CodeGenerationException {
        if (typeExpr != null) {
            TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
            if (typeConsApp != null) {

                if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
                    return JavaTypeName.BOOLEAN;
                }

                if(typeConsApp.getForeignTypeInfo() != null) {
                    ForeignTypeInfo fti = typeConsApp.getForeignTypeInfo();
                    return JavaTypeName.make (SCJavaDefn.getForeignType(fti));
                }

                if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                    if (isEnumDataType (typeConsApp)) {
                        return JavaTypeName.INT;
                    }
                }
            }
        }

        return JavaTypeNames.RTVALUE;

    }

    /**
     * Given a JavaTypeName and a JavaExpression generate java code to
     * box the expression in the CAL kernel type corresponding to the JavaTypeName.
     * @param boxType
     * @param e
     * @return a JavaExpression which boxes the value of e.
     */
    static JavaExpression boxExpression (JavaTypeName boxType, JavaExpression e) {
        if (boxType.equals(JavaTypeName.BOOLEAN)) {
            return createMakeKernelBooleanInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.BYTE)) {
            return createMakeKernelByteInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.CHAR)) {
            return createMakeKernelCharInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.DOUBLE)) {
            return createMakeKernelDoubleInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.FLOAT)) {
            return createMakeKernelFloatInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.INT)) {
            return createMakeKernelIntInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.LONG)) {
            return createMakeKernelLongInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.SHORT)) {
            return createMakeKernelShortInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.STRING)) {
            return createMakeKernelStringInvocation(e);
        } else
        if (boxType.equals(JavaTypeName.BIG_INTEGER)){
            return createMakeKernelIntegerInvocation(e);
        } else {
            return createMakeKernelOpaqueInvocation(e);
        }
    }

    /**
     * Generate a java expression which wraps the given java expression in a runtime
     * boxing class of the appropriate type.
     * @param boxType
     * @param e
     * @return boxed expression
     * @throws CodeGenerationException
     */
    static JavaExpression boxExpression (TypeExpr boxType, JavaExpression e) throws CodeGenerationException {
        if (boxType == null || e == null) {
            throw new CodeGenerationException ("Attempt to box null type. ");
        }

        TypeConsApp typeConsApp = boxType.rootTypeConsApp();

        if (typeConsApp != null) {

            if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
                return boxExpression (JavaTypeName.BOOLEAN, e);
            }

            if (typeConsApp.getForeignTypeInfo() != null) {
                return boxExpression (JavaTypeName.make(SCJavaDefn.getForeignType(typeConsApp.getForeignTypeInfo())), e);
            }

            if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                if (SCJavaDefn.isEnumDataType (typeConsApp)) {
                    return boxExpression (JavaTypeName.INT, e);
                }
            }
        }

        throw new CodeGenerationException ("Attempt to box unhandled type: " + boxType.toString());
    }

    /**
     * This function boxes (i.e. puts the result into a CAL type class) the
     * result of a primitive op.
     * @param op
     * @param arg
     * @return JavaExpression
     * @throws CodeGenerationException
     */
    private JavaExpression boxPrimitiveOpResult (int op, JavaExpression arg) throws CodeGenerationException {

        switch (op) {
            case PrimOps.PRIMOP_NOP:
                throw new CodeGenerationException ("Attemp to box argument for PRIMOP_NOP.");

            // Booleans
            case PrimOps.PRIMOP_OR:
            case PrimOps.PRIMOP_AND:
            case PrimOps.PRIMOP_EQUALS_INT:
            case PrimOps.PRIMOP_NOT_EQUALS_INT:
            case PrimOps.PRIMOP_GREATER_THAN_INT:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_INT:
            case PrimOps.PRIMOP_LESS_THAN_INT:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_INT:
            case PrimOps.PRIMOP_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_NOT_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_GREATER_THAN_DOUBLE:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_LESS_THAN_DOUBLE:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_EQUALS_FLOAT:
            case PrimOps.PRIMOP_NOT_EQUALS_FLOAT:
            case PrimOps.PRIMOP_GREATER_THAN_FLOAT:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_FLOAT:
            case PrimOps.PRIMOP_LESS_THAN_FLOAT:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_FLOAT:
            case PrimOps.PRIMOP_EQUALS_LONG:
            case PrimOps.PRIMOP_NOT_EQUALS_LONG:
            case PrimOps.PRIMOP_GREATER_THAN_LONG:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_LONG:
            case PrimOps.PRIMOP_LESS_THAN_LONG:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_LONG:
            case PrimOps.PRIMOP_EQUALS_SHORT:
            case PrimOps.PRIMOP_NOT_EQUALS_SHORT:
            case PrimOps.PRIMOP_GREATER_THAN_SHORT:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_SHORT:
            case PrimOps.PRIMOP_LESS_THAN_SHORT:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_SHORT:
            case PrimOps.PRIMOP_EQUALS_BYTE:
            case PrimOps.PRIMOP_NOT_EQUALS_BYTE:
            case PrimOps.PRIMOP_GREATER_THAN_BYTE:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_BYTE:
            case PrimOps.PRIMOP_LESS_THAN_BYTE:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_BYTE:
            case PrimOps.PRIMOP_EQUALS_CHAR:
            case PrimOps.PRIMOP_NOT_EQUALS_CHAR:
            case PrimOps.PRIMOP_GREATER_THAN_CHAR:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_CHAR:
            case PrimOps.PRIMOP_LESS_THAN_CHAR:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_CHAR:
                return createMakeKernelBooleanInvocation(arg);

            // Integers
            case PrimOps.PRIMOP_ADD_INT:
            case PrimOps.PRIMOP_SUBTRACT_INT:
            case PrimOps.PRIMOP_MULTIPLY_INT:
            case PrimOps.PRIMOP_NEGATE_INT:
            case PrimOps.PRIMOP_DIVIDE_INT:
            case PrimOps.PRIMOP_REMAINDER_INT:
            case PrimOps.PRIMOP_BITWISE_AND_INT:
            case PrimOps.PRIMOP_BITWISE_OR_INT:
            case PrimOps.PRIMOP_BITWISE_XOR_INT:
            case PrimOps.PRIMOP_COMPLEMENT_INT:
            case PrimOps.PRIMOP_SHIFTL_INT:
            case PrimOps.PRIMOP_SHIFTR_INT:
            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_INT:
                return createMakeKernelIntInvocation(arg);

            // Doubles
            case PrimOps.PRIMOP_ADD_DOUBLE:
            case PrimOps.PRIMOP_SUBTRACT_DOUBLE:
            case PrimOps.PRIMOP_MULTIPLY_DOUBLE:
            case PrimOps.PRIMOP_DIVIDE_DOUBLE:
            case PrimOps.PRIMOP_NEGATE_DOUBLE:
            case PrimOps.PRIMOP_REMAINDER_DOUBLE:
                return createMakeKernelDoubleInvocation(arg);

            // Longs
            case PrimOps.PRIMOP_ADD_LONG:
            case PrimOps.PRIMOP_SUBTRACT_LONG:
            case PrimOps.PRIMOP_MULTIPLY_LONG:
            case PrimOps.PRIMOP_DIVIDE_LONG:
            case PrimOps.PRIMOP_NEGATE_LONG:
            case PrimOps.PRIMOP_REMAINDER_LONG:
            case PrimOps.PRIMOP_BITWISE_AND_LONG:
            case PrimOps.PRIMOP_BITWISE_OR_LONG:
            case PrimOps.PRIMOP_BITWISE_XOR_LONG:
            case PrimOps.PRIMOP_COMPLEMENT_LONG:
            case PrimOps.PRIMOP_SHIFTL_LONG:
            case PrimOps.PRIMOP_SHIFTR_LONG:
            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_LONG:
                return createMakeKernelLongInvocation(arg);

            // Floats
            case PrimOps.PRIMOP_ADD_FLOAT:
            case PrimOps.PRIMOP_SUBTRACT_FLOAT:
            case PrimOps.PRIMOP_MULTIPLY_FLOAT:
            case PrimOps.PRIMOP_DIVIDE_FLOAT:
            case PrimOps.PRIMOP_NEGATE_FLOAT:
            case PrimOps.PRIMOP_REMAINDER_FLOAT:
                return createMakeKernelFloatInvocation(arg);


            case PrimOps.PRIMOP_FIELD_NAMES:
            case PrimOps.PRIMOP_FIELD_VALUES:
            case PrimOps.PRIMOP_MAKE_ITERATOR:
            case PrimOps.PRIMOP_MAKE_COMPARATOR:
            case PrimOps.PRIMOP_MAKE_EQUIVALENCE_RELATION:
            case PrimOps.PRIMOP_MAKE_CAL_FUNCTION:
            case PrimOps.PRIMOP_EXECUTION_CONTEXT:
                return createMakeKernelOpaqueInvocation(arg);

            case PrimOps.PRIMOP_HAS_FIELD:
                return createMakeKernelBooleanInvocation(arg);

            case PrimOps.PRIMOP_RECORD_FIELD_INDEX:
                return createMakeKernelIntInvocation(arg);

            case PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT:
            case PrimOps.PRIMOP_OBJECT_TO_CAL_VALUE:
                return arg;

            default:
            {
                throw new CodeGenerationException("Unrecognized primop " + op + ".");
            }
        }
    }

    /**
     * Generate the java switch corresponding to a CAL case statement.
     * @param eswitch
     * @param variableContext
     * @return JavaStatement the switch statement.  All possible code paths within this statement will result in a return statement.
     * @throws CodeGenerationException
     */
    private JavaStatement generateSwitch(Expression.Switch eswitch, VariableContext variableContext) throws CodeGenerationException {
        if (codeGenerationStats != null) {
            codeGenerationStats.incrementNCases(eswitch);
        }

        nestedCaseLevel++;

        // Extract the alternatives
        Expression.Switch.SwitchAlt[] alts = eswitch.getAlts();

        // Preflight: must have >0 alts.
        if (alts.length <= 0) {
            // This can happen if a user creates a CAL Class that
            // has no instance methods.
            // We simply generate an error call.
            MethodInvocation errorCall =
                new MethodInvocation.Instance(null,
                                              "unhandledSwitchIndex",
                                              new JavaExpression[]{getErrorInfo(eswitch.getErrorInfo()), LiteralWrapper.make("any value")},
                                              new JavaTypeName[]{JavaTypeName.ERRORINFO, JavaTypeName.STRING},
                                              JavaTypeNames.RTVALUE,
                                              MethodInvocation.InvocationType.VIRTUAL);

            return generateReturn(errorCall, variableContext);
        }

        // If there is only one alternate and it is the default we can optimize.
        if (alts.length == 1 && alts[0].isDefaultAlt()) {
            return generateSingleAltSwitch (eswitch, variableContext);
        }

        for (int i = 0; i < alts.length; ++i) {
            if (!alts[i].isDefaultAlt ()) {
                Object firstAltTag = alts[i].getFirstAltTag ();

                if (firstAltTag instanceof DataConstructor) {
                    DataConstructor dc = (DataConstructor)firstAltTag;
                    if (dc.getTypeConstructor().getName().equals(CAL_Prelude.TypeConstructors.Boolean)) {
                        return generateIfThenElseFromSwitch (eswitch, variableContext);
                    } else {
                        return generateSwitchOnDataConstructor (eswitch, variableContext);
                    }
                } else
                if (firstAltTag instanceof Boolean) {
                    return generateIfThenElseFromSwitch (eswitch, variableContext);
                } else
                if (firstAltTag instanceof Integer) {
                    return generateSwitchOnInteger (eswitch, variableContext);
                } else
                if (firstAltTag instanceof Character) {
                    return generateSwitchOnCharacter (eswitch, variableContext);
                } else {
                    throw new CodeGenerationException ("Unexpected tag type encountered in switch: " + firstAltTag.getClass ().getName ());
                }
            }
        }

        throw new CodeGenerationException ("Invalid switch expression encountered in: " + getFunctionName());
    }

    /**
     * Generate a java switch for a CAL case statement on a data type.
     * @param eswitch
     * @param variableContext
     * @return the java switch statement.
     * @throws CodeGenerationException
     */
    private JavaStatement generateSwitchOnDataConstructor (Expression.Switch eswitch, VariableContext variableContext) throws CodeGenerationException {
        Block switchBlock = new Block();

        // Extract the alternatives
        Expression.Switch.SwitchAlt[] alts = eswitch.getAlts();

        TypeConstructor typeCons = null;
        boolean isEnumDataType = false;
        for (int i = 0; i < alts.length; ++i) {
            if (!alts[i].isDefaultAlt()) {
                DataConstructor dc = (DataConstructor)alts[i].getFirstAltTag();
                typeCons = dc.getTypeConstructor();
                isEnumDataType = SCJavaDefn.isEnumDataType(dc);
                break;
            }
        }

        if (typeCons == null) {
            throw new CodeGenerationException ("Unable to retrieve TypeConstructor for switch in " + getFunctionName() + ".");
        }



        int nDataConstructorsForType = typeCons.getNDataConstructors();
        if (nDataConstructorsForType == 0) {
            throw new CodeGenerationException ("Encountered a data type with zero data constructors in a switch in " + getFunctionName() + ".");
        }

        DataConstructor[] allDCs = new DataConstructor [nDataConstructorsForType];
        for (int i = 0; i < nDataConstructorsForType; ++i ) {
            DataConstructor dc = typeCons.getNthDataConstructor(i);
            allDCs[dc.getOrdinal()] = dc;
        }

        // If all the case alternatives return a boolean literal we may
        // be able to optimize this.
        boolean isa = true;
        for (int i = 0; i < alts.length; ++i) {
            SwitchAlt switchAlt = alts[i];
            if (!switchAlt.isDefaultAlt() &&
                !(switchAlt.getFirstAltTag() instanceof DataConstructor)) {
                isa = false;
                break;
            }
            Expression altExpr = switchAlt.getAltExpr();
            if (altExpr.asLiteral() != null) {
                if (!(altExpr.asLiteral().getLiteral() instanceof Boolean)) {
                    isa = false;
                    break;
                }
            } else if (altExpr.asVar() != null) {
                DataConstructor dcv = altExpr.asVar().getDataConstructor();
                if (dcv == null || !isTrueOrFalseDataCons(dcv)) {
                    isa = false;
                    break;
                }
            } else {
                isa = false;
                break;
            }
        }

        // We either need to have a default alt or an alt for every data
        // constructor for the type.
        if (isa && (eswitch.hasDefaultAlt() || nDataConstructorsForType == alts.length)) {
            return generateIsAFunctionFromSwitch (eswitch, variableContext);
        }

        // Determining if any of the alternates have alt vars that need to be extracted from the
        // switch value.
        boolean noAltVars = true;
        for (int i = 0; i < alts.length; ++i) {
            if (alts[i].hasVars()) {
                noAltVars = false;
                break;
            }
        }

        if (LECCMachineConfiguration.OPTIMIZE_SINGLE_DC_CASES && nDataConstructorsForType == 1) {
            // If there is only one DataConstructor we can eliminate the switch.
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementSingleDCCases();
            }
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementSingleDCCases();
            }

            return generateSingleAltSwitch(eswitch, variableContext);
        }

        // Create a boolean array to determine which cases we have.
        boolean[] caseExistsArray = new boolean[nDataConstructorsForType];  // false by default.
        for (int i = 0; i < alts.length; ++i) {
            if (!alts[i].isDefaultAlt()) {
                List<Object> tags = alts[i].getAltTags();
                for (final Object tag : tags) {
                    DataConstructor dc = (DataConstructor)tag;
                    caseExistsArray[dc.getOrdinal()] = true;
                }
            }
        }

        // Generate the switch conditional.
        LocalVariable caseVar = null;
        SwitchStatement switchStatement;
        if (noAltVars /*&& (defaultAltProvided || !missingCases)*/) {
            // If there are no alt vars and we don't have to fill in any missing cases we don't need a local
            // variable holding the switchexpression.  This means we can generate something like:
            // switch (expression.evaluate().getOrdinal())
            ExpressionContextPair ecp = generateUnboxedArgument(JavaTypeName.INT, eswitch.getSwitchExpr(), variableContext);
            switchBlock.addStatement(ecp.getContextBlock());
            JavaExpression conditionExpression = ecp.getJavaExpression();

            switchStatement = new SwitchStatement(conditionExpression);
            switchBlock.addStatement(switchStatement);

        } else {
            // If there are alternates that have alt vars we generate something like:
            // RTValue caseVar;
            // switch ((caseVar = expression.evaluate()).getIntValue())
            // We do the assignment of the local in the actual switch statement
            // because analysis of the generated bytecode has shown this to be
            // slightly more efficient than initializing the local as part of the
            // declaration.
            JavaStatement caseVarDeclaration = null;
            Expression switchExpression = eswitch.getSwitchExpr();

            // Generate a local variable and assign the evaluated value of the expression
            // we are switching on.
            JavaTypeName typeClassName = isEnumDataType ? JavaTypeNames.RTVALUE : CALToJavaNames.createTypeNameFromType(typeCons, module);
            caseVar = new LocalVariable("$case" + nestedCaseLevel, typeClassName);

            // Add the local variable declaration.
            caseVarDeclaration = new LocalVariableDeclaration(caseVar);
            switchBlock.addStatement(caseVarDeclaration);

            // Compile the expression we are switching on strictly.
            ExpressionContextPair pair = genS_E(switchExpression, variableContext);
            switchBlock.addStatement(pair.getContextBlock());

            JavaExpression caseExpression = pair.getJavaExpression();
            //caseExpression = releaseVarsInSwitchCondition(eswitch, caseExpression, variableContext);

            // We may need to cast the result of the case expression to the type of the local variable.
            caseExpression = (isEnumDataType || caseExpression instanceof ClassInstanceCreationExpression) ? caseExpression : new CastExpression(typeClassName, caseExpression);

            // Assign the result of the switch expression to the local an then get the ordinal value.
            JavaExpression assignLocal = new JavaExpression.Assignment(caseVar, caseExpression);
            JavaExpression getOrdinal = SCJavaDefn.createInvocation(assignLocal, SCJavaDefn.GETORDINALVALUE);

            switchStatement = new SwitchStatement(getOrdinal);
            switchBlock.addStatement(switchStatement);
        }

        // Populate the switch statement with case statement groups.
        for (final SwitchAlt alt : alts) {
            List<Object> altTags = alt.getAltTags();

            // If no variables are used, we can share the code among all data constructors for this alt.
            if (!alt.hasVars()) {

                Block caseBlock = new Block();

                // Add a comment for the data constructors in the group if any (ie. if not the default alt).
                if (alt.getFirstAltTag() instanceof DataConstructor) {
                    StringBuilder commentSB = new StringBuilder();

                    boolean firstDC = true;
                    for (final Object tag : altTags) {
                        DataConstructor tagDC = (DataConstructor)tag;
                        if (firstDC) {
                            firstDC = false;
                        } else {
                            commentSB.append(", ");
                        }
                        commentSB.append(tagDC.getName().getQualifiedName());

                    }
                    caseBlock.addStatement(new LineComment(commentSB.toString()));
                }

                // Create a new child variable scope to handle the alternate and any let variables it contains.
                variableContext.pushJavaScope();

                // Compile the body of the alternate.
                JavaStatement altStatement = genS_R(alt.getAltExpr(), variableContext);
                caseBlock.addStatement(variableContext.popJavaScope());
                caseBlock.addStatement(altStatement);

                if (alt.isDefaultAlt()) {
                    switchStatement.addCase(new SwitchStatement.DefaultCase(caseBlock));

                } else {
                    int[] caseLabels = new int[altTags.size()];
                    int index = 0;
                    for (final Object tag : altTags) {
                        if (!(tag instanceof DataConstructor)) {
                            throw new CodeGenerationException ("Unknown tag type in DC case statement in " + getFunctionName() + ": " + tag.getClass().getName());
                        }

                        caseLabels[index] = ((DataConstructor)tag).getOrdinal();
                        index++;
                    }

                    switchStatement.addCase(new SwitchStatement.IntCaseGroup(caseLabels, caseBlock));
                }

            } else {
                // The alts use variables.

                if (alt instanceof SwitchAlt.Positional) {
                    // Positional notation for extracted variables.
                    // For now, a separate code block must be generated for each data constructor in the case.

                    Collection<List<DataConstructor>> tagGroups = consolidatePositionalSwitchAlt((SwitchAlt.Positional)alt);

                    for (final List<DataConstructor> group : tagGroups) {
                        // Must be a data constructor tag, since there are field names (see Expression.Switch.SwitchAlt).

                        Block caseBlock = new Block();

                        int[] caseLabels = new int[group.size()];
                        int index = 0;
                        DataConstructor firstDC = null;
                        // Must be a data constructor tag, since there are field names (see Expression.Switch.SwitchAlt).
                        for (final DataConstructor tagDC : group) {
                            if (firstDC == null) {
                                firstDC = tagDC;
                            } else
                            if (tagDC.getOrdinal() < firstDC.getOrdinal()) {
                                firstDC = tagDC;
                            }
                            caseBlock.addStatement(new LineComment(tagDC.getName().getQualifiedName()));
                            caseLabels[index] = tagDC.getOrdinal();
                            index++;
                        }

                        caseBlock.addStatement(new LineComment("Decompose data type to access members."));

                        // Create a new child variable scope to handle the alternate and any let variables it contains.
                        variableContext.pushJavaScope();

                        // Get this alternative's variables.  These have to be added to the active list of scope variables
                        TypeExpr fieldTypes[] = SCJavaDefn.getFieldTypesForDC(firstDC);
                        for (final AltVarIndexPair altVarIndexPair : getAltVarIndexList(alt, firstDC)) {

                            String altVar = altVarIndexPair.getAltVar();
                            int fieldIndex = altVarIndexPair.getIndex();

                            QualifiedName qn = QualifiedName.make(currentModuleName, altVar);
                            VarInfo.DCMember vi = variableContext.addDCField(qn, fieldTypes[fieldIndex]);

                            boolean fieldIsStrict =
                                !LECCMachineConfiguration.IGNORE_STRICTNESS_ANNOTATIONS;

                            for (final DataConstructor tagDC : group) {
                                fieldIsStrict = fieldIsStrict && tagDC.isArgStrict(fieldIndex);
                            }

                            if (fieldIsStrict) {
                                vi.setEvaluated(true);
                            }

                            String fieldName = SCJavaDefn.getJavaFieldNameFromDC(firstDC, fieldIndex);
                            String fieldGetterName = "get" + fieldName;

                            // Generate the code defining the variable.
                            if (fieldIsStrict) {
                                if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[fieldIndex])) {
                                    // This is a strict field of a primitive type so has both a boxed and unboxed form.
                                    JavaExpression unboxedInitializer =
                                        new JavaExpression.MethodInvocation.Instance(caseVar,
                                                                                     fieldGetterName + "_As_" + SCJavaDefn.getNameForPrimitive(fieldTypes[fieldIndex]),
                                                                                     SCJavaDefn.typeExprToTypeName(fieldTypes[fieldIndex]),
                                                                                     JavaExpression.MethodInvocation.InvocationType.VIRTUAL);

                                    vi.updateUnboxedVarDef(unboxedInitializer);
                                    JavaExpression localVar = new LocalVariable(vi.getJavaName()+"$U", vi.getUnboxedType());
                                    vi.updateUnboxedReference(localVar);
                                    JavaExpression boxedDef = SCJavaDefn.boxExpression(vi.getUnboxedType(), localVar);
                                    vi.updateStrictReference(boxedDef);
                                    vi.updateLazyReference(boxedDef);
                                } else {
                                    // RTValue altVarName = ((DCClass)caseVar).getFieldn();
                                    JavaExpression initializer = new JavaExpression.MethodInvocation.Instance(caseVar, fieldGetterName, JavaTypeNames.RTVALUE, JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
                                    vi.updateStrictVarDef (initializer);
                                    JavaExpression localVar = new LocalVariable(vi.getJavaName(), JavaTypeNames.RTVALUE);
                                    vi.updateStrictReference(localVar);
                                    vi.updateLazyReference(localVar);
                                }
                            } else {
                                // RTValue altVarName = ((DCClass)caseVar).getFieldn();
                                JavaExpression initializer = new JavaExpression.MethodInvocation.Instance(caseVar, fieldGetterName, JavaTypeNames.RTVALUE, JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
                                vi.updateLazyVarDef (initializer);
                                JavaExpression localVar = new LocalVariable(vi.getJavaName(), JavaTypeNames.RTVALUE);
                                vi.updateLazyReference(localVar);

                                JavaExpression evaluatedVar = SCJavaDefn.createInvocation(localVar, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                                vi.updateStrictReference(evaluatedVar);
                                if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[fieldIndex])) {
                                    vi.updateUnboxedReference(SCJavaDefn.unboxValue(vi.getUnboxedType(), evaluatedVar));
                                }
                            }
                        }

                        // Compile the actual body of the alternate.
                        JavaStatement altStatement = genS_R(alt.getAltExpr(), variableContext);
                        caseBlock.addStatement(variableContext.popJavaScope());
                        caseBlock.addStatement(altStatement);

                        switchStatement.addCase(new SwitchStatement.IntCaseGroup(caseLabels, caseBlock));
                    }
                } else {
                    // Matching notation for switch alternate.
                    Map<FieldName, String> fieldNameToVarNameMap = ((SwitchAlt.Matching)alt).getFieldNameToVarNameMap();

                    Block caseBlock = new Block();

                    int[] caseLabels = new int[altTags.size()];
                    int index = 0;
                    DataConstructor firstDC = null;
                    // Must be a data constructor tag, since there are field names (see Expression.Switch.SwitchAlt).
                    for (final Object altTag : altTags) {
                        DataConstructor tagDC = (DataConstructor)altTag;
                        if (firstDC == null) {
                            firstDC = tagDC;
                        } else if (tagDC.getOrdinal() < firstDC.getOrdinal()) {
                            firstDC = tagDC;
                        }
                        caseBlock.addStatement(new LineComment(tagDC.getName().getQualifiedName()));
                        caseLabels[index] = tagDC.getOrdinal();
                        index++;
                    }

                    caseBlock.addStatement(new LineComment("Decompose data type to access members."));

                    // Create a new child variable scope to handle the alternate and any let variables it contains.
                    variableContext.pushJavaScope();

                    for (int iField = 0; iField < firstDC.getArity(); ++iField) {
                        FieldName fn = firstDC.getNthFieldName(iField);
                        String altVar = fieldNameToVarNameMap.get(fn);
                        if (altVar == null) {
                            continue;
                        }

                        QualifiedName qn = QualifiedName.make(currentModuleName, altVar);
                        TypeExpr fieldType = SCJavaDefn.getFieldTypeForDC(firstDC, fn);

                        VarInfo.DCMember vi = variableContext.addDCField(qn, fieldType);

                        boolean fieldIsStrict = !LECCMachineConfiguration.IGNORE_STRICTNESS_ANNOTATIONS;
                        for (final Object altTag : altTags) {
                            DataConstructor tagDC = (DataConstructor)altTag;
                            fieldIsStrict = fieldIsStrict & tagDC.isArgStrict(tagDC.getFieldIndex(fn));
                        }

                        if (fieldIsStrict) {
                            vi.setEvaluated(true);
                        }

                        String fieldName = SCJavaDefn.getJavaFieldNameFromFieldName(fn);
                        String fieldGetterName = "get" + fieldName;

                        // Generate the code defining the variable.
                        if (fieldIsStrict) {
                            if (SCJavaDefn.canTypeBeUnboxed(fieldType)) {
                                // This is a strict field of a primitive type so has both a boxed and unboxed form.
                                JavaExpression unboxedInitializer =
                                    new JavaExpression.MethodInvocation.Instance(caseVar,
                                                                                 fieldGetterName + "_As_" + SCJavaDefn.getNameForPrimitive(fieldType),
                                                                                 SCJavaDefn.typeExprToTypeName(fieldType),
                                                                                 JavaExpression.MethodInvocation.InvocationType.VIRTUAL);

                                vi.updateUnboxedVarDef(unboxedInitializer);
                                JavaExpression localVar = new LocalVariable(vi.getJavaName()+"$U", vi.getUnboxedType());
                                vi.updateUnboxedReference(localVar);
                                JavaExpression boxedDef = SCJavaDefn.boxExpression(vi.getUnboxedType(), localVar);
                                vi.updateStrictReference(boxedDef);
                                vi.updateLazyReference(boxedDef);
                            } else {
                                // RTValue altVarName = ((DCClass)caseVar).getFieldn();
                                JavaExpression initializer = new JavaExpression.MethodInvocation.Instance(caseVar, fieldGetterName, JavaTypeNames.RTVALUE, JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
                                vi.updateStrictVarDef (initializer);
                                JavaExpression localVar = new LocalVariable(vi.getJavaName(), JavaTypeNames.RTVALUE);
                                vi.updateStrictReference(localVar);
                                vi.updateLazyReference(localVar);
                            }
                        } else {
                            // RTValue altVarName = ((DCClass)caseVar).getFieldn();
                            JavaExpression initializer = new JavaExpression.MethodInvocation.Instance(caseVar, fieldGetterName, JavaTypeNames.RTVALUE, JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
                            vi.updateLazyVarDef (initializer);
                            JavaExpression localVar = new LocalVariable(vi.getJavaName(), JavaTypeNames.RTVALUE);
                            vi.updateLazyReference(localVar);

                            JavaExpression evaluatedVar = SCJavaDefn.createInvocation(localVar, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                            vi.updateStrictReference(evaluatedVar);
                            if (SCJavaDefn.canTypeBeUnboxed(fieldType)) {
                                vi.updateUnboxedReference(SCJavaDefn.unboxValue(vi.getUnboxedType(), evaluatedVar));
                            }
                        }
                    }

                    // Compile the actual body of the alternate.
                    JavaStatement altStatement = genS_R(alt.getAltExpr(), variableContext);
                    caseBlock.addStatement(variableContext.popJavaScope());
                    caseBlock.addStatement(altStatement);

                    switchStatement.addCase(new SwitchStatement.IntCaseGroup(caseLabels, caseBlock));
                }
            }
        }

        // If no default case is provided, add case alternates for any missing data constructors.
        // Switches in java are marginally more efficient if the case tags cover a contiguous block.
        JavaStatement defaultCase = switchStatement.getDefaultStatement();
        if (defaultCase == null) {

            // Iterate over the array.  For each case not provided, treat as an error.
            List<Integer> intTagList = new ArrayList<Integer>();
            for (int i = 0; i < caseExistsArray.length; ++i) {
                if (!caseExistsArray[i]) {
                    // Add the ordinal to the list.
                    intTagList.add(Integer.valueOf(i));
                }
            }
            addMissingCases(intTagList, typeCons, switchStatement, eswitch);


            // Create a default default case.
            defaultCase = generateReturn(getBadSwitchIndexCall(eswitch.getErrorInfo()), variableContext);
            switchStatement.addCase (new SwitchStatement.DefaultCase(defaultCase));
        }

        return switchBlock;
    }

    /**
     * Breaks the data constructors for the switch alt into groups
     * where the extracted dc fields have the same names.
     * @param alt
     * @return  Collection of (List of DataConstructor)
     */
    Collection<List<DataConstructor>> consolidatePositionalSwitchAlt (SwitchAlt.Positional alt) {
        List<List<DataConstructor>> groups = new ArrayList<List<DataConstructor>> ();
        Map<Integer, String> f = alt.getPositionToVarNameMap();
        int[] indexes = new int[f.size()];
        int i = 0;
        for (final Integer key : f.keySet()) {
            indexes[i++] = key.intValue();
        }

        for (final Object altTag : alt.getAltTags()) {
            DataConstructor dc = (DataConstructor)altTag;
            List<DataConstructor> group = null;
            for (final List<DataConstructor> pGroup : groups) {
                DataConstructor dcMatch = pGroup.get(0);
                boolean match = true;
                for (int j = 0, n = indexes.length; j < n; ++j) {
                    if (!dc.getArgumentName(j).equals(dcMatch.getArgumentName(j))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    group = pGroup;
                    break;
                }
            }
            if (group == null) {
                group = new ArrayList<DataConstructor>();
                groups.add (group);
            }
            group.add(dc);
        }

        return groups;
    }

    /**
     * For each data constructor that is not explicitly handled, add a case to the switch that makes an
     * unhandledSwitchIndex error call.
     * @param intTagList the list of tags corresponding to data cons that are not handled.
     * @param typeCons the associated type constructor.
     * @param switchStatement the java switch statement.
     * @param eSwitch the generated switch expression.
     */
    private void addMissingCases(final List/*Integer*/<Integer> intTagList,
                                 final TypeConstructor typeCons,
                                 final SwitchStatement switchStatement,
                                 final Expression.Switch eSwitch) {

        final int nTags = intTagList.size();
        for (int i = 0; i < nTags; i++) {
            // Generate a case for each tag -
            // Since there is no default case provided we want to treat this as an error..

            final int tag = intTagList.get(i).intValue();
            final Block altBody = new Block();
            final String dataConsName = typeCons.getNthDataConstructor(tag).getName().getQualifiedName();

            altBody.addStatement (new JavaStatement.LineComment(dataConsName));

            // We generate a call to unhandledSwitchIndex with the data cons name appearing as a string literal.
            final MethodInvocation errorCall =
                new MethodInvocation.Instance(null,
                                              "unhandledSwitchIndex",
                                              new JavaExpression[]{getErrorInfo(eSwitch.getErrorInfo()), LiteralWrapper.make(dataConsName)},
                                              new JavaTypeName[]{JavaTypeName.ERRORINFO, JavaTypeName.STRING},
                                              JavaTypeNames.RTVALUE,
                                              MethodInvocation.InvocationType.VIRTUAL);

            altBody.addStatement(generateReturn (errorCall, null));
            switchStatement.addCase(new SwitchStatement.IntCaseGroup (new int[] {tag}, altBody));
        }
    }

    /**
     * Get the indices of the alt variables with respect to a data constructor.
     * @param alt the alt for which to retrieve the vars.
     * @param fromDC the data constructor with respect to which the indices will be returned.
     * @return (List of AltVarIndexPair) the indices of the alt's variables.
     *   These will be in the same order as that returned by an iterator over the alt's relevant mapping to vars.
     */
    private List<AltVarIndexPair> getAltVarIndexList(SwitchAlt alt, DataConstructor fromDC) {
        final List<AltVarIndexPair> altVarIndexList = new ArrayList<AltVarIndexPair>(); // List (of AltVarIndexPair)

        if (alt instanceof SwitchAlt.Positional) {
            SortedMap<Integer, String> positionToVarNameMap = ((SwitchAlt.Positional)alt).getPositionToVarNameMap();
            for (final Map.Entry<Integer, String> entry : positionToVarNameMap.entrySet()) {
                Integer indexInteger = entry.getKey();
                String altVar = entry.getValue();
                altVarIndexList.add(new AltVarIndexPair(altVar, indexInteger.intValue()));
            }
        } else {
            // Must be matching.
            Map<FieldName, String> fieldNameToVarNameMap = ((SwitchAlt.Matching)alt).getFieldNameToVarNameMap();
            for (final Map.Entry<FieldName, String> entry : fieldNameToVarNameMap.entrySet()) {

                FieldName fieldName = entry.getKey();
                String altVar = entry.getValue();
                int fieldIndex = fromDC.getFieldIndex(fieldName);
                altVarIndexList.add(new AltVarIndexPair(altVar, fieldIndex));
            }
        }

        Collections.<AltVarIndexPair>sort (altVarIndexList);

        return altVarIndexList;
    }

    /**
     * Transforms the statement so that the last reference to any local variables
     * or function arguments is replaced by a call to RTValue.lastRef().
     * @param statement
     * @param releasedVars
     * @return the transformed statement
     * @throws CodeGenerationException
     */
    private JavaStatement releaseVars (JavaStatement statement, Set<String> releasedVars) throws CodeGenerationException {

        // Names of variables to be released mapped to the Java type.
        Map<String, JavaTypeName> variablesOfInterest = new HashMap<String, JavaTypeName>();

        if (!isTailRecursive()) {
            // Add the names the function arguments.
            for (int i = 0; i < getArity(); ++i) {
                if (!isArgStrict(i) || !isArgUnboxable(i)) {
                    variablesOfInterest.put(getJavaArgumentName(i), JavaTypeNames.RTVALUE);
                }
            }
        }

        // Add the names of any locals declared in the statement.
        DeclaredLocalsFinder dlf = new DeclaredLocalsFinder();
        statement.accept(dlf, variablesOfInterest);

        // Transform the statement.
        VarReleaser vr = new VarReleaser(variablesOfInterest);

        return (JavaStatement)statement.accept(vr, null);

    }


    /**
     * Generate a java switch for a CAL case statement on an integer.
     * @param eswitch
     * @param variableContext
     * @return the java switch statement.
     * @throws CodeGenerationException
     */
    private JavaStatement generateSwitchOnInteger (Expression.Switch eswitch, VariableContext variableContext) throws CodeGenerationException {

        Block switchBlock = new Block();

        // Extract the alternatives
        Expression.Switch.SwitchAlt[] alts = eswitch.getAlts();

        // Generate the switch conditional.
        SwitchStatement switchStatement;

        // Generate code to get the int value that we are switching on.
        ExpressionContextPair ecp = generateUnboxedArgument(JavaTypeName.INT, eswitch.getSwitchExpr(), variableContext);
        switchBlock.addStatement(ecp.getContextBlock());
        JavaExpression conditionExpression = ecp.getJavaExpression();

        switchStatement = new SwitchStatement(conditionExpression);
        switchBlock.addStatement(switchStatement);

        // Populate the switch statement with case statement groups.
        for (final SwitchAlt alt : alts) {
            // Create a new child variable scope to handle the alternate and any let variables it contains.
            variableContext.pushJavaScope();

            Block caseBlock = new Block();

            // If we are switching on an integer we should never have alt variables.
            if (alt.hasVars()) {
                throw new CodeGenerationException ("Alt vars encountered in integer switch in " + getFunctionName() + ".");
            }

            JavaStatement altStatement = genS_R(alt.getAltExpr(), variableContext);
            caseBlock.addStatement(variableContext.popJavaScope());
            caseBlock.addStatement(altStatement);

            if (alt.isDefaultAlt()) {
                switchStatement.addCase(new SwitchStatement.DefaultCase(caseBlock));

            } else {
                List<Object> altTags = alt.getAltTags();
                int[] caseLabels = new int[altTags.size()];

                int index = 0;
                for (final Object tag : altTags) {
                    if (!(tag instanceof Integer)) {
                        throw new CodeGenerationException ("Unknown tag type in case statement in " + getFunctionName() + ":" + tag.getClass().getName() + ".");
                    }

                    caseLabels[index] = ((Integer)tag).intValue();
                    index++;
                }

                switchStatement.addCase(new SwitchStatement.IntCaseGroup(caseLabels, caseBlock));
            }
        }

        // Add case alternate for default case if missing.
        if (switchStatement.getDefaultStatement() == null) {
            //this will mostly be a user error e.g.
            //(\x -> case x of 1 -> "one";) (2 :: Int)
            //However, because cases on ints are also used by internal dictionary functions, and a few other situations
            //it could happen that this occurs because of an internal error.
            //todoBI encode enough information into Expression.Switch so that we know which case we're in.
            //todoBI pass the unhandled int value that occurred at runtime i.e. 2 in the above example, to the error message
            JavaStatement defaultCase = generateReturn(getUnhandledSwitchIndexForIntPatternCall(eswitch.getErrorInfo()), variableContext);
            switchStatement.addCase (new SwitchStatement.DefaultCase(defaultCase));
        }

        return switchBlock;
    }

    /**
     * Generate a java switch for a CAL case statement on an character.
     * @param eswitch
     * @param variableContext
     * @return the java switch statement.
     * @throws CodeGenerationException
     */
    private JavaStatement generateSwitchOnCharacter (Expression.Switch eswitch, VariableContext variableContext) throws CodeGenerationException {

        Block switchBlock = new Block();

        // Extract the alternatives
        Expression.Switch.SwitchAlt[] alts = eswitch.getAlts();

        // Generate the switch conditional.
        SwitchStatement switchStatement;

        // Generate code to get the char value that we are switching on.
        ExpressionContextPair ecp = generateUnboxedArgument(JavaTypeName.CHAR, eswitch.getSwitchExpr(), variableContext);
        switchBlock.addStatement(ecp.getContextBlock());
        JavaExpression conditionExpression = ecp.getJavaExpression();

        switchStatement = new SwitchStatement(conditionExpression);
        switchBlock.addStatement(switchStatement);

        // Populate the switch statement with case statement groups.
        for (final SwitchAlt alt : alts) {
            // Create a new child variable scope to handle the alternate and any let variables it contains.
            variableContext.pushJavaScope();

            Block caseBlock = new Block();

            // If we are switching on an character we should never have alt variables.
            if (alt.hasVars()) {
                throw new CodeGenerationException ("Alt vars encountered in character switch in " + getFunctionName() + ".");
            }

            JavaStatement altStatement = genS_R(alt.getAltExpr(), variableContext);
            caseBlock.addStatement(variableContext.popJavaScope());
            caseBlock.addStatement(altStatement);

            if (alt.isDefaultAlt()) {
                switchStatement.addCase(new SwitchStatement.DefaultCase(caseBlock));

            } else {
                List<Object> altTags = alt.getAltTags();
                int[] caseLabels = new int[altTags.size()];

                int index = 0;
                for (final Object tag : altTags) {
                    if (!(tag instanceof Character)) {
                        throw new CodeGenerationException ("Unknown tag type in case statement in " + getFunctionName() + ":" + tag.getClass().getName() + ".");
                    }

                    caseLabels[index] = ((Character)tag).charValue();
                    index++;
                }

                switchStatement.addCase(new SwitchStatement.IntCaseGroup(caseLabels, caseBlock));
            }
        }

        // Add case alternate for default case if missing.
        if (switchStatement.getDefaultStatement() == null) {
            //this will mostly be a user error e.g.
            //(\x -> case x of 'a' -> "char a";) 'b'
            //todoBI pass the unhandled char value that occurred at runtime i.e. 'b' in the above example, to the error message

            JavaStatement defaultCase = generateReturn(getUnhandledSwitchIndexForCharPatternCall(eswitch.getErrorInfo()), variableContext);

            switchStatement.addCase (new SwitchStatement.DefaultCase(defaultCase));
        }

        return switchBlock;
    }

    /**
     * If all the branches in a CAL switch return a literal boolean the generated function
     * can be optimized into boolean java expression.
     * @param eswitch
     * @param variableContext
     * @return JavaStatement the switch statement.  All possible code paths within this statement will result in a return statement.
     * @throws CodeGenerationException
     */
    private JavaStatement generateIsAFunctionFromSwitch(Expression.Switch eswitch, VariableContext variableContext) throws CodeGenerationException {

        // Since this switch returns either true or false we can generate code in the form.
        // int $intVal = switchExpression;
        // ($intVal == a || $intVal == b)
        // Where a, b, etc. are the tags for the alternates that return true.
        // One wrinkle is if a default is included.
        // If the default returns false code can be generated as described above.
        // If the default returns true the logic needs to be reversed.
        // i.e. whatever the default case returns needs to be returned by the else part of the expression.
        // So:
        // int $intVal = switchExpression;
        // !($intVal == c || $intVal == d)
        // Where c and d are the cases that return false.


        Block isABlock = new Block();

        // Extract the alternatives
        Expression.Switch.SwitchAlt[] alts = eswitch.getAlts();

        boolean lookingForTrue = true;
        int defaultIndex = -1;
        int trueCount = 0;
        // Need to determine if there is a default case and how many true and false cases.
        for (int i = 0; i < alts.length; ++i) {
            Expression altExpr = alts[i].getAltExpr();
            Expression.Literal lit = altExpr.asLiteral();
            Expression.Var var = altExpr.asVar();
            boolean isTrue = false;
            if (lit != null && lit.getLiteral() instanceof Boolean && ((Boolean)lit.getLiteral()).booleanValue()) {
                isTrue = true;
                trueCount++;
            } else
            if (var != null && var.getDataConstructor() != null && isTrueDataCons(var.getDataConstructor())) {
                isTrue = true;
                trueCount++;
            }

            if(alts[i].isDefaultAlt()) {
                defaultIndex = i;
                // If the default case returns true we want to build up our expression based
                // on the cases that return false.
                if (isTrue) {
                    lookingForTrue = false;
                }
            }
        }

        if (trueCount == alts.length) {
            isABlock.addStatement(
                    generateReturn(
                            boxPrimitiveOpResult(
                                    PrimOps.PRIMOP_OR,
                                    JavaExpression.LiteralWrapper.make(Boolean.TRUE)), variableContext));
            return isABlock;
        }

        if (trueCount == 0) {
            isABlock.addStatement(generateReturn(boxPrimitiveOpResult(PrimOps.PRIMOP_OR, JavaExpression.LiteralWrapper.make(Boolean.FALSE)), variableContext));
            return isABlock;
        }

        int falseCount = alts.length - trueCount;

        // Generate the switch conditional.
        ExpressionContextPair pair = genS_E (eswitch.getSwitchExpr(), variableContext);

        isABlock.addStatement(pair.getContextBlock());

        JavaExpression isAExpr = null;
        JavaExpression tagValue = SCJavaDefn.createInvocation (pair.getJavaExpression(), SCJavaDefn.GETORDINALVALUE);
        if ((lookingForTrue && trueCount > 1) || (!lookingForTrue && falseCount > 1)) {
            LocalVariable intVal = new LocalVariable ("$intVal", JavaTypeName.INT);
            LocalVariableDeclaration intValDeclaration = new LocalVariableDeclaration(intVal, tagValue);
            isABlock.addStatement (intValDeclaration);
            tagValue = intVal;
        }

        for (int i = 0; i < alts.length; ++i) {
            // We arrange things so that we don't have to explicitly handle the default case.
            if (i == defaultIndex) {
                continue;
            }

            Expression altExpr = alts[i].getAltExpr();
            Expression.Literal lit = altExpr.asLiteral();
            Expression.Var var = altExpr.asVar();

            boolean isTrue = false;
            if (lit != null && lit.getLiteral() instanceof Boolean && ((Boolean)lit.getLiteral()).booleanValue()) {
                isTrue = true;
            } else
            if (var != null && var.getDataConstructor() != null && isTrueDataCons(var.getDataConstructor())) {
                isTrue = true;
            }

            if (isTrue == lookingForTrue) {
                for (final Object altTag : alts[i].getAltTags()) {
                    int ord = ((DataConstructor)altTag).getOrdinal();
                    JavaExpression litOrd = JavaExpression.LiteralWrapper.make(Integer.valueOf(ord));
                    JavaExpression eqExpression = new JavaExpression.OperatorExpression.Binary(JavaOperator.EQUALS_INT, tagValue, litOrd);
                    if (isAExpr == null) {
                        isAExpr = eqExpression;
                    } else {
                        isAExpr = new JavaExpression.OperatorExpression.Binary(JavaOperator.CONDITIONAL_OR, isAExpr, eqExpression);
                    }
                }
            }
        }


        if (isAExpr == null) {
            isAExpr = JavaExpression.LiteralWrapper.make(Boolean.valueOf(!lookingForTrue));
        } else {
            if (!lookingForTrue) {
                isAExpr = new JavaExpression.OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, isAExpr);
            }
        }

        isABlock.addStatement(generateReturn(boxPrimitiveOpResult(PrimOps.PRIMOP_OR, isAExpr), variableContext));

        return isABlock;
    }

    /**
     * In cases where the tag in a switch is a boolean it can be optimised as an if-then-else.
     * @param eSwitch
     * @param variableContext
     * @return JavaStatement
     * @throws CodeGenerationException
     */
    private JavaStatement generateIfThenElseFromSwitch (Expression.Switch eSwitch, VariableContext variableContext) throws CodeGenerationException {

        // Extract the alternatives
        Expression.Switch.SwitchAlt[] alts = eSwitch.getAlts();

        Expression trueAlt = null;
        Expression falseAlt = null;
        Expression defaultAlt = null;

        for (int i = 0; i < alts.length; ++i) {
            SwitchAlt switchAlt = alts[i];
            if (switchAlt.isDefaultAlt()) {
                defaultAlt = switchAlt.getAltExpr();

            } else {
                if (switchAlt.getAltTags().size() > 1) {
                    // (True | False) - ie. always.
                    return genS_R(switchAlt.getAltExpr(), variableContext);
                }

                Object altTag = switchAlt.getFirstAltTag();

                if (altTag instanceof DataConstructor) {
                    // This is either Prelude.True or Prelude.False
                    DataConstructor dc = (DataConstructor)altTag;
                    if (dc.getName().equals(CAL_Prelude.DataConstructors.True)) {
                        altTag = Boolean.TRUE;
                    } else
                    if (dc.getName().equals(CAL_Prelude.DataConstructors.False)) {
                        altTag = Boolean.FALSE;
                    } else {
                        // We should never get here.
                        throw new CodeGenerationException ("Trying to generate if-then-else from data constructor: " + dc.getName().getQualifiedName());
                    }
                }

                // Tag is a boolean.
                if (((Boolean)altTag).booleanValue()) {
                    trueAlt = switchAlt.getAltExpr();
                } else {
                    falseAlt = switchAlt.getAltExpr();
                }
            }
        }

        if (trueAlt == null) {
            trueAlt = defaultAlt;
        }
        if (falseAlt == null) {
            falseAlt = defaultAlt;
        }

        Expression condExpression = eSwitch.getSwitchExpr();
        ExpressionContextPair pair = generateUnboxedArgument(JavaTypeName.BOOLEAN, condExpression, variableContext);

        // Then
        Block thenBlock;
        if (trueAlt == null) {
            LiteralWrapper badValueMessageWrapper = LiteralWrapper.make("Illegal fallthrough to default case.");
            JavaExpression returnValueExpression = getBadValueCall(eSwitch.getErrorInfo(), badValueMessageWrapper);
            JavaStatement badValueReturnStatement = generateReturn(returnValueExpression, variableContext);
            thenBlock = new Block();
            thenBlock.addStatement (badValueReturnStatement);
        } else {
            variableContext.pushJavaScope();
            JavaStatement thenPart = genS_R (trueAlt, variableContext);

            thenBlock = variableContext.popJavaScope();
            thenBlock.addStatement(thenPart);
        }

        // Else
        Block elseBlock;
        if (falseAlt == null) {
            LiteralWrapper badValueMessageWrapper = LiteralWrapper.make("Illegal fallthrough to default case in function.");
            JavaStatement badValueReturnStatement = generateReturn(getBadValueCall(eSwitch.getErrorInfo(), badValueMessageWrapper), variableContext);
            elseBlock = new Block();
            elseBlock.addStatement (badValueReturnStatement);
        } else {
            variableContext.pushJavaScope();
            JavaStatement elsePart = genS_R (falseAlt, variableContext);

            elseBlock = variableContext.popJavaScope();
            elseBlock.addStatement(elsePart);
        }

        JavaStatement ite = new IfThenElseStatement(pair.getJavaExpression(), thenBlock, elseBlock);

        Block contextBlock = pair.getContextBlock();
        contextBlock.addStatement(ite);
        return contextBlock;
    }

    /**
     * This method generates the java code for a switch statement when we know that the switch can be ignored.
     * Examples would be cases where the data type being switched on has a single data constructor or there
     * is only a default alternate.
     * @param eSwitch
     * @param variableContext
     * @return The java code corresponding to the switch.
     * @throws CodeGenerationException
     */
    private JavaStatement generateSingleAltSwitch (Expression.Switch eSwitch, VariableContext variableContext) throws CodeGenerationException {
        // If this is the method being called to generate a switch we know a few things:
        // If there is more than one alternate we can ignore the default alternate.
        // We are only going to generate code for one alternate.
        // We don't need a switch statement.

        // First let's figure out which alternate we're going to be compiling.
        Expression.Switch.SwitchAlt[] alts = eSwitch.getAlts();

        Expression.Switch.SwitchAlt alt = alts[0];
        if (alts.length > 1) {
            // Use the first non-default alt.
            for (int i = 0; i < alts.length; ++i) {
                if (!alts[i].isDefaultAlt()) {
                    alt = alts[i];
                    break;
                }
            }
        }

        // Now we want to continue with almost the same process as when dealing with a regular switch.

        Block switchBlock = new Block();

        Object altTag = alt.getFirstAltTag();

        // Preserve the behaviour of a switch, i.e. the switch expression will be
        // strictly evaluated before falling through to the single default alternate.
        // This is really only needed if the switch expression can have a side effect.
        boolean isSwitchingOnDC = !alt.isDefaultAlt() && (altTag instanceof DataConstructor);
        boolean switchExprEvaluated = false;
        Expression.Var switchVar = eSwitch.getSwitchExpr().asVar();
        if (switchVar != null) {
            switchExprEvaluated = variableContext.isVarPreEvaluated(switchVar.getName());
        }

        // Only do a local variable if the switch expression is not evaluated or we will
        // be extracting member fields from the data constructor resulting from evaluating the switch expression.
        LocalVariable caseVar = null;
        JavaTypeName caseVarType = (isSwitchingOnDC && alt.hasVars()) ? JavaTypeNames.RTCONS : JavaTypeNames.RTVALUE;
        if (!switchExprEvaluated || alt.hasVars()) {
            caseVar = new LocalVariable("$case" + nestedCaseLevel, caseVarType);

            ExpressionContextPair pair = genS_E(eSwitch.getSwitchExpr(), variableContext);
            switchBlock.addStatement(pair.getContextBlock());
            JavaExpression caseExpression = pair.getJavaExpression();

            if (caseVarType.equals(JavaTypeNames.RTCONS)) {
                caseExpression = new CastExpression(JavaTypeNames.RTCONS, caseExpression);
            }

            LocalVariableDeclaration caseVarDeclaration =
                new LocalVariableDeclaration(caseVar, caseExpression);
            switchBlock.addStatement(caseVarDeclaration);
        }

        // Populate the switch statement with case statement groups.

        // Create a new let variable scope to handle the alternate and any let variables it contains.
        variableContext.pushJavaScope();

        Block caseBlock = new Block();

        if (isSwitchingOnDC) {
            caseBlock.addStatement(new LineComment(((DataConstructor)altTag).getName().getQualifiedName()));
        }

        // Get this alternative's variables.  These have to be added to the active list of scope variables
        if (alt.hasVars()) {
            caseBlock.addStatement(new LineComment("Decompose data type to access members."));

            if (caseVar == null) {
                throw new CodeGenerationException ("Null case variable encountered in single alternate switch.");
            }

            // Must be a data constructor tag, since there are field names (see Expression.Switch.SwitchAlt).
            DataConstructor tagDC = (DataConstructor)altTag;

            TypeExpr fieldTypes[] = SCJavaDefn.getFieldTypesForDC(tagDC);

            // Cast the case var to the appropriate type to call getField0, getField1, etc.
            JavaTypeName dcTypeName = CALToJavaNames.createTypeNameFromDC(tagDC, module);
            JavaExpression castExpression = new CastExpression(dcTypeName, caseVar);

            // There is at least one field to be extracted.
            // Declare a local variable that is the casted case var so that we only do the cast once.
            JavaExpression castCaseVar = new LocalVariable("$dcCaseVar" + nestedCaseLevel, dcTypeName);
            LocalVariableDeclaration localVarStmnt = new LocalVariableDeclaration((LocalVariable)castCaseVar, castExpression);
            caseBlock.addStatement (localVarStmnt);

            for (final AltVarIndexPair altVarIndexPair : getAltVarIndexList(alt, tagDC)) {

                String altVar = altVarIndexPair.getAltVar();
                int fieldIndex = altVarIndexPair.getIndex();

                String fieldGetterName = "get" + SCJavaDefn.getJavaFieldNameFromDC(tagDC, fieldIndex);

                // Build in place representation of this variable mapped to its name
                QualifiedName qn = QualifiedName.make(currentModuleName, altVar);
                VarInfo.DCMember vi = variableContext.addDCField(qn, fieldTypes[fieldIndex]);

                boolean fieldIsStrict =
                    !LECCMachineConfiguration.IGNORE_STRICTNESS_ANNOTATIONS && tagDC.isArgStrict(fieldIndex);

                if (fieldIsStrict) {
                    vi.setEvaluated(true);
                }

                if (fieldIsStrict) {
                    if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[fieldIndex])) {
                        JavaTypeName strictType = SCJavaDefn.typeExprToTypeName(fieldTypes[fieldIndex]);
                        JavaExpression unboxedInitializer =
                            new JavaExpression.MethodInvocation.Instance(castCaseVar,
                                                                         fieldGetterName + "_As_" + SCJavaDefn.getNameForPrimitive(strictType),
                                                                         strictType,
                                                                         JavaExpression.MethodInvocation.InvocationType.VIRTUAL);

                        vi.updateUnboxedVarDef(unboxedInitializer);
                        JavaExpression localVar = new LocalVariable(vi.getJavaName()+"$U", vi.getUnboxedType());
                        vi.updateUnboxedReference(localVar);
                        JavaExpression boxedDef = SCJavaDefn.boxExpression(vi.getUnboxedType(), localVar);
                        vi.updateStrictReference(boxedDef);
                        vi.updateLazyReference(boxedDef);
                    } else {
                        JavaExpression initializer = new JavaExpression.MethodInvocation.Instance(castCaseVar, fieldGetterName, JavaTypeNames.RTVALUE, JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
                        vi.updateStrictVarDef (initializer);
                        JavaExpression localVar = new LocalVariable(vi.getJavaName(), JavaTypeNames.RTVALUE);
                        vi.updateStrictReference(localVar);
                        vi.updateLazyReference(localVar);
                    }
                } else {
                    JavaExpression initializer = new JavaExpression.MethodInvocation.Instance(castCaseVar, fieldGetterName, JavaTypeNames.RTVALUE, JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
                    vi.updateLazyVarDef (initializer);
                    JavaExpression localVar = new LocalVariable(vi.getJavaName(), JavaTypeNames.RTVALUE);
                    vi.updateLazyReference(localVar);

                    JavaExpression evaluatedVar = SCJavaDefn.createInvocation(localVar, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                    vi.updateStrictReference(evaluatedVar);
                    if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[fieldIndex])) {
                        vi.updateUnboxedReference(SCJavaDefn.unboxValue(vi.getUnboxedType(), evaluatedVar));
                    }
                }
            }
        }

        JavaStatement altStatement = genS_R(alt.getAltExpr(), variableContext);
        caseBlock.addStatement(variableContext.popJavaScope());
        caseBlock.addStatement(altStatement);
        switchBlock.addStatement(caseBlock);

        return switchBlock;
    }

    /**
     * Place an application chain into a more easily manageable format.
     * @param root
     * @return Expression[]
     */
    private Expression[] appChain (Expression.Appl root) {

        // Walk down the left branch.
        Expression c = root;
        int nArgs = 0;
        while (c instanceof Expression.Appl) {
            nArgs++;
            c = ((Expression.Appl)c).getE1();
        }

        Expression[] chain = new Expression [nArgs + 1];
        chain[0] = c;
        c = root;
        for (int i = nArgs; i >= 1; i--) {
            chain[i] = ((Expression.Appl)c).getE2();
            c = ((Expression.Appl)c).getE1();
        }

        return chain;
    }

    /**
     * Generate the expression context pair for a fully saturated application at the top level.
     * @param chain
     * @param calledArity
     * @param calledArgStrictness
     * @param calledArgTypes
     * @param variableContext
     * @return the code that creates the graph node
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildTopLevelApplicationChain(Expression[] chain,
                                                                int calledArity,
                                                                boolean[] calledArgStrictness,
                                                                TypeExpr[] calledArgTypes,
                                                                VariableContext variableContext) throws CodeGenerationException {

        boolean recursiveCallInStrictArg = false;
        for (int i = 1, n = chain.length; !recursiveCallInStrictArg && i < n; ++i) {
            if (calledArgStrictness[i-1]) {
                recursiveCallInStrictArg = ExpressionAnalyzer.dependsOnStronglyConnectedComponent(chain[i], connectedComponents, getModuleName());
            }
        }

        // If none of the strict arguments contain a recursive call we can build
        // a strict application node or, potentially, do the call directly on the stack.
        if (!recursiveCallInStrictArg) {
            // Try to build a direct call.
            ExpressionContextPair directCall = buildDirectCall(chain, calledArity, calledArgStrictness, calledArgTypes, Scheme.R_SCHEME, variableContext);
            if (directCall != null) {
                return directCall;
            }

            // We can't do a direct call on the java stack, but we know the strict arguments don't contain
            // recursive calls, so we can build a strict application node.
            return buildStrictApplicationNode(chain, calledArity, calledArgStrictness, calledArgTypes, variableContext);
        }

        return buildLazyApplicationNode(chain, calledArity, calledArgStrictness, calledArgTypes, variableContext);
    }

    /**
     * Determine if it is safe to generate a call on the java stack instead of
     * building an application graph.  Generally this is concerned with making
     * sure we don't break correct space behaviour.
     * @param calledFunction
     * @param calledArgTypes
     * @param scheme
     * @return - true if the specified function can be called directly on the call stack.
     */
    private boolean canCallDirectlyOnJavaStack (
            Expression.Var calledFunction,
            TypeExpr[] calledArgTypes,
            Scheme scheme) throws CodeGenerationException {

        // If we are in the E scheme or one of the unboxed schemes
        // it won't make any difference space-wise whether
        // we do the call directly or build the graph, since we'll immediately
        // reduce the graph anyway.
        if (scheme == Scheme.E_SCHEME || scheme == Scheme.UNBOX_FOREIGN_SCHEME || scheme == Scheme.UNBOX_INTERNAL_SCHEME) {
            return true;
        }

        // At this point we want to see if the call can be made directly on the java
        // stack.
        // We can do the call on the stack if:
        // - the called function is not a strongly connected component of this function.  If it is
        //   a strongly connected component it is too easy to fill the java call stack with mutually
        //   recursive calls.
        // - the called function is not tail recursive or we can determine that it is a tail recursive
        //   function that can be called directly.  The problem with directly calling a tail recursive
        //   function is that the roots of the function arguments get held on the java call stack.
        //   This can cause memory usage issues because of the optimization which converts tail
        //   recursion to a loop.  If the loop expands/consumes an argument which is a data structure
        //   such as a list the consumed part will not be garbage collected because the root is still
        //   held on the call stack.  In normal reduction this wouldn't be a problem but the loop in
        //   the tail recursive function changes things.
        //   So, if we can determine that none of the arguments to a tail recursive function have the
        //   potential to grow in this fashion then we can safely do a direct call to the tail recursive
        //   function.

        // Try to collect information about the called function.
        // This is used to decide how to encode the function call.
        MachineFunction mf = module.getFunction(calledFunction.getName());
        if (mf == null) {
            return false;
        }

        // Start of by checking if the called function is a closely connected component of this function.
        boolean safeToCallOnJavaStack =
            (!mf.getQualifiedName().getModuleName().equals(this.getModuleName()) ||!connectedComponents.contains(calledFunction.getName().getUnqualifiedName()));

        if (safeToCallOnJavaStack && mf.isTailRecursive()) {
            // Since the called function is tail recursive we need to
            // check the types of its arguments.
            // At the moment we do the simple check of allowing the call if
            // all arguments are primitive/foreign or an enum data type.
            // This way we know that holding the roots of the arguments can safely be held on the
            // java call stack since none of them can grow and be consumed in an unbounded fashion
            // by the loop in the tail recursive function.
            for (int i = 0, n = calledArgTypes.length; i < n && safeToCallOnJavaStack; ++i) {
                TypeExpr argType = calledArgTypes[i];
                safeToCallOnJavaStack = argType != null &&
                                            (SCJavaDefn.canTypeBeUnboxed(argType) ||
                                             SCJavaDefn.isEnumDataType(argType) ||
                                             !SCJavaDefn.isSelfReferentialDataType(argType));
            }

        }


        return safeToCallOnJavaStack;
    }

    /**
     * Build a strict application node from a fully saturated application.
     * @param chain
     * @param calledArity
     * @param calledArgStrictness
     * @param calledArgTypes
     * @param variableContext
     * @return the code that creates the graph node
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildStrictApplicationNode(Expression[] chain,
                                                                int calledArity,
                                                                boolean[] calledArgStrictness,
                                                                TypeExpr[] calledArgTypes,
                                                                VariableContext variableContext) throws CodeGenerationException {

        Expression.Var var = (Expression.Var)chain[0];

        // We want to use a type specific app node if the called function has unboxed arguments or
        // the called function is a tail recursive loop.
        boolean useTypeSpecificAppNode = false;
        for (int i = 0; i < calledArgTypes.length; ++i) {
            if (calledArgStrictness[i] && SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i])) {
                useTypeSpecificAppNode = true;
                break;
            }
        }

        MachineFunction mf = module.getFunction(var.getName());
        if (mf != null && mf.isTailRecursive()) {
            useTypeSpecificAppNode = true;
        }


        // We want to use the SC specific application node that takes unboxed
        // First generate the java expressions for the CAL expressions.
        ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length];
        for (int i = 0; i < chain.length; ++i) {
            if (i > 0 && calledArgStrictness[i-1]) {
                if (SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i-1])) {
                    ecp[i] = generateUnboxedArgument(typeExprToTypeName(calledArgTypes[i-1]), chain[i], variableContext);
                } else {
                    ecp[i] = genS_E(chain[i], variableContext);
                }
            } else {
                ecp[i] = genS_C(chain[i], variableContext);
            }
        }

        Block newContext = ecp[0].getContextBlock();
        for (int i = 1; i < chain.length; ++i) {
            newContext.addStatement(ecp[i].getContextBlock());
        }

        JavaExpression root = ecp[0].getJavaExpression();

        if (codeGenerationStats != null) {
            if (var.getForeignFunctionInfo() == null) {
                codeGenerationStats.incrementStrictSCNodeCount();
            } else {
                codeGenerationStats.incrementStrictForeignNodeCount();
            }
        }

        // Optimize Prelude.not to use the '!' java operator.
        if (var.getName().equals(CAL_Prelude.Functions.not)) {
            JavaExpression not = new JavaExpression.OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, ecp[1].getJavaExpression());
            return new ExpressionContextPair (not, newContext);
        }

        // This is a fully saturated supercombinator application that can be represented
        // using an special purpose graph node.
        JavaExpression args[] = new JavaExpression[calledArity + 1];
        args[0] = expressionVarToJavaDef(var, Scheme.E_SCHEME, variableContext);
        JavaTypeName argTypes[] = new JavaTypeName[calledArity + 1];
        if (useTypeSpecificAppNode) {
            argTypes[0] = CALToJavaNames.createTypeNameFromSC(var.getName(), module);
        } else {
            argTypes[0] = JavaTypeNames.RTSUPERCOMBINATOR;
        }
        for (int i = 0; i < calledArity; ++i) {
            args[i+1] = ecp[i+1].getJavaExpression();
            if (calledArgStrictness[i] && SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i])) {
                argTypes[i+1] = SCJavaDefn.typeExprToTypeName(calledArgTypes[i]);
            } else {
                argTypes[i+1] = JavaTypeNames.RTVALUE;
            }
        }

        JavaTypeName appNodeType;
        if (useTypeSpecificAppNode) {
            appNodeType = CALToJavaNames.createStrictInnerTypeNameFromSC(var.getName(), module);
        } else {
            appNodeType = getTypeNameForApplicationNode(chain.length-1, true);
        }

        root = new JavaExpression.ClassInstanceCreationExpression(appNodeType, args, argTypes);

        return new ExpressionContextPair(root, newContext);
    }

    /**
     * This function generates java code to create a partial application node.
     * A partial application node holds a supercombinator and the arguments it is
     * applied to.  The node then acts like a supercombinator of arity n where n
     * is the arity of the original supercombinator less the number of applied arguments.
     * We cannot generate one of these nodes if:
     * 1) We cannot determine the specific supercombinator being applied
     * 2) The supercombinator arity is greater than the number of args for which a special node exists
     * 3) The supercombinator is tail recursive.
     * @param chain
     * @param mf
     * @param variableContext
     * @return java code to generate a partial application node if possible, null otherwise
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildUndersaturatedAppNode(
            Expression[] chain,
            MachineFunction mf,
            VariableContext variableContext) throws CodeGenerationException {

        /*
         * This method is currently disabled due to some apparent memory usage
         * issues that come up with using partial application nodes.
         */

        int calledArity = mf.getArity();

        /* We only have specialized application nodes for SCs of up to arity 15. */
        if (calledArity > LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH) {
            return null;
        }

        /*
         * We can't use one of these nodes if the supercombinator is tail recursive.
         * This is because the f/fnL/fnS method of a partial application node makes
         * a direct call to the supercombinators f/fnS/fnL method.  This means that
         * the roots of the function arguments have a reference in the java call
         * stack while the supercombinators f/fnS/fnL method is executing.  In the
         * case of a tail recursive function, whose body is transformed to a loop to
         * optimize the recursion, this can cause memory usage issues when the
         * arguments are evaluated since the root of the argument can't be released
         * and garbage collected.
         */
        if (mf.isTailRecursive()) {
            return null;
        }


        // Get the JavaTypeName for the partial application node.
        JavaTypeName nodeClassName = JavaTypeName.make("org.openquark.cal.internal.runtime.lecc.RTPartialApp$_"+calledArity+"$_"+(chain.length-1), false);
        int length = chain.length;

        // Compile each of the arguments in the partial application lazily.
        ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length];
        for (int i = 1; i < length; ++i) {
                ecp[i] = genS_C(chain[i], variableContext);
        }

        // Build up the context from the compiled arguments.
        Block newContext = ecp[1].getContextBlock();
        for (int i = 2; i < length; ++i) {
            newContext.addStatement(ecp[i].getContextBlock());
        }

        // Build the statement to create a new partial application node.
        JavaExpression args[] = new JavaExpression[length];
        JavaTypeName argTypes[] = new JavaTypeName[length];
        Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
        args[0] = expressionVarToJavaDef(chain[0].asVar(), Scheme.E_SCHEME, variableContext);
        argTypes[0] = JavaTypeNames.RTSUPERCOMBINATOR;

        for (int i = 1; i < length; ++i) {
            args[i] = ecp[i].getJavaExpression();
        }

        JavaExpression cci =
            new ClassInstanceCreationExpression(nodeClassName, args, argTypes);

        return new ExpressionContextPair(cci, newContext);
    }

    /**
     * Do the necessary re-assignemnts to modify a tail recursive call into
     * a loop continuation.
     * @param rc
     * @param variableContext
     * @return a block of java statements.
     * @throws CodeGenerationException
     */
    private JavaStatement buildTailRecursiveLoopCall (Expression.TailRecursiveCall rc, VariableContext variableContext) throws CodeGenerationException {

        if (codeGenerationStats != null) {
            codeGenerationStats.incrementTailRecursiveFunctions(getArity());
        }

        Block newContext = new Block ();

        Expression[] args = rc.getArguments();

        // We want to use the SC specific application node.
        // First generate the java expressions for the CAL expressions.
        ExpressionContextPair[] ecps = new ExpressionContextPair[args.length];
        for (int i = 0; i < args.length; ++i) {
            if (argumentStrictness[i]) {
                if (SCJavaDefn.canTypeBeUnboxed(argumentTypes[i])) {
                    ecps[i] = generateUnboxedArgument(typeExprToTypeName(argumentTypes[i]), args[i], variableContext);
                } else {
                    ecps[i] = genS_E(args[i], variableContext);
                }
            } else {
                ecps[i] = genS_C(args[i], variableContext);
            }
        }

        for (int i = 0; i < args.length; ++i) {
            newContext.addStatement(ecps[i].getContextBlock());
        }

        Block tempAssignment = new Block ();
        Block reAssignment = new Block ();

        // Do function argument re-assignment using temporaries.
        // We use temporaries to avoid problems with function interdependencies.
        // i.e. simply transforming: new RTAppS ($instance, n - 1, acc + n);  to:
        // n = n - 1; acc = acc + n; continue;  will give incorrect results because
        // the value of n has been re-assigned before calculating acc + n.

        // Try to avoid the temporary local variable where possible.
        int[] needTemp = new int[getArity()];  // 0 -> temp, 1 -> assign, 2 -> skip
        Arrays.fill (needTemp, 0);

        // If we are assigning an atomic entity i.e. literal or var we don't need the temporary.
        for (int i = 0; i < getArity(); ++i) {
            if (args[i].asLiteral() != null) {
                needTemp[i] = 1;
            } else
            if (args[i].asVar() != null) {
                QualifiedName newVarName = args[i].asVar().getName();
                // If the argument is being passed unchanged to the tail recursive call we can skip the assignment.
                if (newVarName.equals(QualifiedName.make(currentModuleName, argumentNames[i])) &&
                        variableContext.isFunctionArgument(newVarName)) {
                        // The argument passed to the function is being passed unchanged in the tail recursive call.
                        needTemp[i] = 2;
                } else
                // If the var is a local or an sc do a simple assignment
                if (!variableContext.isLocalVariable(newVarName) ||
                    variableContext.isDCField(newVarName) ||
                    variableContext.isRecordField(newVarName)) {
                    needTemp[i] = 1;
                } else
                // If the assignment is to a following argument it is safe to do a simple assignment.
                if (variableContext.isFunctionArgument(newVarName)) {
                    for (int j = i + 1; j < getArity(); ++j) {
                        if (argumentNames[j].equals(newVarName.getUnqualifiedName())) {
                            needTemp[i] = 1;
                        }
                    }
                }

            } else {
                boolean dependent = false;
                for (int j = 0; j < i; j++) {
                    QualifiedName argName = QualifiedName.make(currentModuleName, argumentNames[j]);
                    if (needTemp[j] != 2 && args[i].isDependentOn(argName)) {
                        dependent = true;
                        break;
                    }
                }
                if (!dependent) {
                    needTemp[i] = 1;
                }
            }
        }

        for (int i = 0; i < getArity(); ++i) {
            JavaTypeName argType = JavaTypeNames.RTVALUE;
            if (isArgUnboxable(i) && isArgStrict (i)) {
                argType = typeExprToTypeName(argumentTypes[i]);
            }

            if (needTemp[i] == 0) {
                // Assign the new argument value to a local variable.
                // RTValue arg1$ = ...;
                JavaStatement.LocalVariableDeclaration decl = new LocalVariableDeclaration (new LocalVariable (getJavaArgumentName(i)+"$", argType),
                                                                             ecps[i].getJavaExpression());
                tempAssignment.addStatement (decl);

                // Assign back to the argument.
                // arg1 = arg1$;
                Assignment a = new Assignment(new JavaExpression.LocalName(getJavaArgumentName(i), argType),
                                              new JavaExpression.LocalName(getJavaArgumentName(i) + "$", argType));
                reAssignment.addStatement(new ExpressionStatement(a));
            } else
            if (needTemp[i] == 1){
                // Simply assign the new definition to the argument.
                // arg1 = ...;
                Assignment a = new Assignment(new JavaExpression.LocalName(getJavaArgumentName(i), argType),
                                                                           ecps[i].getJavaExpression());
                reAssignment.addStatement(new ExpressionStatement(a));
            }
        }

        newContext.addStatement (tempAssignment);
        newContext.addStatement (reAssignment);

        newContext.addStatement(new JavaStatement.LabelledContinue(TAIL_RECURSION_LOOP_LABEL));

        return newContext;
    }

    /**
     * Generate the code corresponding to a fully saturated application encountered in a strict context.
     * @param chain
     * @param calledArity
     * @param calledArgStrictness
     * @param calledArgTypes
     * @param variableContext
     * @return the code that creates the graph node, null if a strict application chain can't be built
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildStrictApplicationChain(Expression[] chain,
                                                              int calledArity,
                                                              boolean[] calledArgStrictness,
                                                              TypeExpr[] calledArgTypes,
                                                              VariableContext variableContext) throws CodeGenerationException {
        return buildDirectCall (chain, calledArity, calledArgStrictness, calledArgTypes, Scheme.E_SCHEME, variableContext);
    }

    /**
     * Generated a direct call to the function body for a fully saturated application.
     * @param chain
     * @param calledArity
     * @param calledArgStrictness
     * @param calledArgTypes
     * @param scheme
     * @param variableContext
     * @return the code that creates the graph node, null if the function cannot be called directly.
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildDirectCall(Expression[] chain,
                                                              int calledArity,
                                                              boolean[] calledArgStrictness,
                                                              TypeExpr[] calledArgTypes,
                                                              Scheme scheme,
                                                              VariableContext variableContext) throws CodeGenerationException {

        Expression.Var var = (Expression.Var)chain[0];

        if (!canCallDirectlyOnJavaStack (chain[0].asVar(), calledArgTypes, scheme)) {
            return null;
        }

        // First generate the java expressions for the CAL expressions.
        ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length];
        for (int i = 0; i < chain.length; ++i) {
            if (i > 0 && calledArgStrictness[i-1]) {
                if (SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i-1])) {
                    ecp[i] = generateUnboxedArgument(typeExprToTypeName(calledArgTypes[i-1]), chain[i], variableContext);
                } else {
                    ecp[i] = genS_E(chain[i], variableContext);
                }
            } else {
                ecp[i] = genS_C(chain[i], variableContext);
            }
        }

        Block newContext = ecp[0].getContextBlock();
        for (int i = 1; i < chain.length; ++i) {
            newContext.addStatement(ecp[i].getContextBlock());
        }

        JavaExpression root = ecp[0].getJavaExpression();

        if (codeGenerationStats != null) {
            if (var.getForeignFunctionInfo() != null) {
                codeGenerationStats.incrementDirectForeignCalls();
            } else {
                codeGenerationStats.incrementDirectSCCalls();
            }
        }

        // Optimize Prelude.not to use the '!' java operator.
        if (var.getName().equals(CAL_Prelude.Functions.not)) {
            JavaExpression not = new JavaExpression.OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, ecp[1].getJavaExpression());
            return new ExpressionContextPair (not, newContext);
        }

        // This is a fully saturated supercombinator application
        // for which we can generate a direct call instead of building a suspension.
        JavaExpression args[] = new JavaExpression[calledArity + 1];
        for (int i = 0; i < calledArity; ++i) {
            args[i] = ecp[i + 1].getJavaExpression();
        }
        args[calledArity] = EXECUTION_CONTEXT_VAR;

        // Get the supercombinator class and do a direct application of the fn function.
        // arg types are all RTValue except when strict
        JavaTypeName[] argTypes = new JavaTypeName[args.length];
        for (int i = 0; i < calledArgTypes.length; ++i) {
            if (calledArgStrictness[i] && SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i])) {
                argTypes[i] = SCJavaDefn.typeExprToTypeName(calledArgTypes[i]);
            } else {
                argTypes[i] = JavaTypeNames.RTVALUE;
            }
        }
        argTypes[calledArity] = JavaTypeNames.RTEXECUTION_CONTEXT;

        root = expressionVarToJavaDef(var, Scheme.E_SCHEME, variableContext);

        // Get the method name to call.
        LECCModule.FunctionGroupInfo fgi = module.getFunctionGroupInfo(var.getName());
        String functionName = fgi.getFnNamePrefix(var.getName().getUnqualifiedName()) + "f";
        if (calledArity > 0) {
            functionName = functionName + calledArity + "S";
        } else {
            args = new JavaExpression[]{JavaExpression.LiteralWrapper.NULL, EXECUTION_CONTEXT_VAR};
            argTypes = new JavaTypeName[]{JavaTypeNames.RTVALUE, JavaTypeNames.RTEXECUTION_CONTEXT};
        }

        root = new MethodInvocation.Instance(root, functionName, args, argTypes, JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);
        if (scheme == Scheme.E_SCHEME) {
            root = createInvocation (root, EVALUATE, EXECUTION_CONTEXT_VAR);
        }


        return new ExpressionContextPair(root, newContext);
    }


    /**
     * Generated the expression/context pair corresponding to a fully saturated application encountered in a
     * lazy context.
     * @param chain
     * @param calledArity
     * @param calledArgStrictness
     * @param calledArgTypes
     * @param variableContext
     * @return the code that creates the graph node
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildLazyApplicationChain(Expression[] chain,
                                                                int calledArity,
                                                                boolean[] calledArgStrictness,
                                                                TypeExpr[] calledArgTypes,
                                                                VariableContext variableContext) throws CodeGenerationException {

        if (LECCMachineConfiguration.OPTIMIZE_LAZY_APP_NODES) {
            boolean useStrictAppNode = true;
            // If all the strict arguments are already evaluated we should use a strict application node.
            for (int i = 0; i < calledArity; ++i) {
                if (calledArgStrictness[i] && !canIgnoreLaziness(chain[i+1], variableContext)) {
                    useStrictAppNode = false;
                    break;
                }
            }

            if (useStrictAppNode) {
                return buildStrictApplicationNode(chain, calledArity, calledArgStrictness, calledArgTypes, variableContext);
            }

        }

        return buildLazyApplicationNode (chain, calledArity, calledArgStrictness, calledArgTypes, variableContext);
    }

    /**
     * Build a lazy application node from an application chain.
     * @param chain
     * @param calledArity
     * @param variableContext
     * @param calledArgStrictness
     * @param calledArgTypes
     * @return the generated expression/context pair.
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildLazyApplicationNode (Expression[] chain,
                                                            int calledArity,
                                                            boolean[] calledArgStrictness,
                                                            TypeExpr[] calledArgTypes,
                                                            VariableContext variableContext) throws CodeGenerationException {


        Expression.Var var = (Expression.Var)chain[0];
        MachineFunction mf = module.getFunction(var.getName());
        if (mf == null) {
            // Simply return null and let a different compilation path handle
            // the application.
            return null;
        }

        // At this point we need to do a check to make sure that we can
        // build a lazy application using a special purpose node.
        // The function must have an arity small enough to use a predefined
        // lazy app node or be tail recursive.  Special purpose lazy app nodes
        // are generated for tail recursive functions with arity greather than
        // that accomodated by the predefined lazy app nodes.
        if (calledArity > LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH &&
            !mf.isTailRecursive()) {
            // Simply return null and let a different compilation path handle
            // the application.
            return null;
        }

        // First generate the java expressions for the CAL expressions.
        ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length];
        for (int i = 0; i < chain.length; ++i) {
            ecp[i] = genS_C(chain[i], variableContext);
        }

        Block newContext = ecp[0].getContextBlock();
        for (int i = 1; i < chain.length; ++i) {
            newContext.addStatement(ecp[i].getContextBlock());
        }

        if (codeGenerationStats != null) {
            if (var.getForeignFunctionInfo() != null) {
                codeGenerationStats.incrementLazyForeignNodeCount();
            } else {
                codeGenerationStats.incrementLazySCNodeCount();
            }
        }

        // This is a fully saturated supercombinator application that can be represented
        // using an special purpose graph node.
        JavaExpression args[] = new JavaExpression[calledArity];
        for (int i = 0; i < calledArity; ++i) {
            args[i] = ecp[i + 1].getJavaExpression();
        }

        JavaExpression scInstance = expressionVarToJavaDef(var, Scheme.C_SCHEME, variableContext);

        JavaExpression constructorArgs[] = new JavaExpression [calledArity + 1];
        constructorArgs[0] = scInstance;
        for (int i = 0; i < args.length; ++i) {
            constructorArgs[i+1] = args[i];
        }
        JavaTypeName constructorArgTypes[] = new JavaTypeName[calledArity + 1];
        Arrays.fill(constructorArgTypes, JavaTypeNames.RTVALUE);
        constructorArgTypes[0] = JavaTypeNames.RTSUPERCOMBINATOR;

        JavaTypeName appNodeType;
        // We want to use a type specific app node if the called function
        // is a tail recursive loop with arity greater than that supported
        // by the predefined lazy app nodes.
        if (mf.isTailRecursive() && calledArity > LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH) {
            appNodeType = CALToJavaNames.createLazyInnerTypeNameFromSC(var.getName(), module);
            constructorArgTypes[0] = CALToJavaNames.createTypeNameFromSC(var.getName(), module);
        } else {
            appNodeType = getTypeNameForApplicationNode(chain.length-1, false);
        }

        return new ExpressionContextPair(new JavaExpression.ClassInstanceCreationExpression(appNodeType, constructorArgs, constructorArgTypes), newContext);
    }

    /**
     * Get the JavaTypeName for one of the classes used to represent
     * fully saturated function applications.
     * @param nApplicationArguments
     * @param isStrict
     * @return the JavaTypeName for the fully saturated application class.
     */
    private JavaTypeName getTypeNameForApplicationNode (int nApplicationArguments, boolean isStrict) {
        switch (nApplicationArguments) {
        case 1:
            return isStrict ? JavaTypeNames.RTAPP1S : JavaTypeNames.RTAPP1L;
        case 2:
            return isStrict ? JavaTypeNames.RTAPP2S : JavaTypeNames.RTAPP2L;
        case 3:
            return isStrict ? JavaTypeNames.RTAPP3S : JavaTypeNames.RTAPP3L;
        case 4:
            return isStrict ? JavaTypeNames.RTAPP4S : JavaTypeNames.RTAPP4L;
        case 5:
            return isStrict ? JavaTypeNames.RTAPP5S : JavaTypeNames.RTAPP5L;
        case 6:
            return isStrict ? JavaTypeNames.RTAPP6S : JavaTypeNames.RTAPP6L;
        case 7:
            return isStrict ? JavaTypeNames.RTAPP7S : JavaTypeNames.RTAPP7L;
        case 8:
            return isStrict ? JavaTypeNames.RTAPP8S : JavaTypeNames.RTAPP8L;
        case 9:
            return isStrict ? JavaTypeNames.RTAPP9S : JavaTypeNames.RTAPP9L;
        case 10:
            return isStrict ? JavaTypeNames.RTAPP10S : JavaTypeNames.RTAPP10L;
        case 11:
            return isStrict ? JavaTypeNames.RTAPP11S : JavaTypeNames.RTAPP11L;
        case 12:
            return isStrict ? JavaTypeNames.RTAPP12S : JavaTypeNames.RTAPP12L;
        case 13:
            return isStrict ? JavaTypeNames.RTAPP13S : JavaTypeNames.RTAPP13L;
        case 14:
            return isStrict ? JavaTypeNames.RTAPP14S : JavaTypeNames.RTAPP14L;
        case 15:
            return isStrict ? JavaTypeNames.RTAPP15S : JavaTypeNames.RTAPP15L;
        }

        throw new UnsupportedOperationException("Attempt to get application node class name for " + nApplicationArguments + ".");
    }

    /**
     * Generate the java representation that will build the given application graph.
     * @param chain
     * @param scheme
     * @param variableContext
     * @return expression context pair which builds the given application graph.
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildGeneralApplicationChain (Expression[] chain, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {
        // First generate the java expressions for the CAL expressions.
        ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length];

        if (chain[0].asDataConsSelection() != null && (scheme == Scheme.E_SCHEME || scheme == Scheme.R_SCHEME)) {
            ecp[0] = genS_E(chain[0], variableContext);
        } else {
            ecp[0] = genS_C(chain[0], variableContext);
        }

        for (int i = 1; i < chain.length; ++i) {
            ecp[i] = genS_C(chain[i], variableContext);
        }

        Block newContext = ecp[0].getContextBlock();
        JavaExpression[] je = new JavaExpression [chain.length];
        je[0] = ecp[0].getJavaExpression();
        for (int i = 1; i < chain.length; ++i) {
            newContext.addStatement(ecp[i].getContextBlock());
            je[i] = ecp[i].getJavaExpression();
        }

        // If we are in a strict context we want to generate something like:
        // arg.f3L(a, b, c).evaluate($ec);
        if (scheme == Scheme.E_SCHEME && chain.length - 1 <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH) {
            JavaExpression[] args = new JavaExpression [chain.length];
            System.arraycopy(je, 1, args, 0, args.length -1);
            args[args.length-1] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
            JavaTypeName[] argTypes = new JavaTypeName[chain.length];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
            argTypes[argTypes.length-1] = JavaTypeNames.RTEXECUTION_CONTEXT;
            JavaExpression root = je[0];
            MethodInvocation fL = new MethodInvocation.Instance(root, "f" + (chain.length-1) + "L", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
            MethodInvocation eval = SCJavaDefn.createInvocation(fL, SCJavaDefn.EVALUATE, EXECUTION_CONTEXT_VAR);
            return new ExpressionContextPair(eval, newContext);
        }

        // Now we want to build the application chain. (As efficiently as possible).
        int nApplicationArguments = chain.length - 1;
        int argIndex = 1;
        JavaExpression root = je[0];
        while (nApplicationArguments > 0) {
            if (nApplicationArguments >= 4) {
                JavaExpression[] args = new JavaExpression[4];
                JavaTypeName[] argTypes = new JavaTypeName[4];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);
                System.arraycopy(je, argIndex, args, 0, 4);
                root = new MethodInvocation.Instance(root, "apply", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex += 4;
                nApplicationArguments -= 4;
            } else
            if (nApplicationArguments == 3) {
                JavaExpression[] args = new JavaExpression[3];
                JavaTypeName[] argTypes = new JavaTypeName[3];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);
                System.arraycopy(je, argIndex, args, 0, 3);
                root = new MethodInvocation.Instance(root, "apply", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex += 3;
                nApplicationArguments -= 3;
            } else
            if (nApplicationArguments == 2) {
                JavaExpression[] args = new JavaExpression[2];
                JavaTypeName[] argTypes = new JavaTypeName[2];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);
                System.arraycopy(je, argIndex, args, 0, 2);
                root = new MethodInvocation.Instance(root, "apply", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex += 2;
                nApplicationArguments -= 2;
            } else
            if (nApplicationArguments == 1) {
                root = new MethodInvocation.Instance(root, "apply", je[argIndex], JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex++;
                nApplicationArguments--;
            }
        }

        if (scheme == Scheme.E_SCHEME) {
            // Strict application scheme.  Call 'evalaute' on the RTValue.
            root = createInvocation(root, EVALUATE, EXECUTION_CONTEXT_VAR);
        }

        return new ExpressionContextPair (root, newContext);
    }


    private Expression[] flattenTopLevelSeq (Expression e) throws CodeGenerationException {
        Collection<Expression> c = new ArrayList<Expression>();
        flattenTopLevelSeqHelper(e, c);
        if (c.size() <= 1) {
            return null;
        }

        return c.toArray(new Expression[c.size()]);
    }

    private void flattenTopLevelSeqHelper (Expression e, Collection<Expression> c) throws CodeGenerationException {
        if (e.asAppl() == null) {
            c.add(e);
            return;
        }

        Expression[] chain = appChain (e.asAppl());
        if (chain.length != 3 ||
            chain[0].asVar() == null ||
            !chain[0].asVar().getName().equals(SCJavaDefn.PRELUDE_SEQ)) {
            c.add(e);
            return;
        }

        // At this point we know that we have a fully saturated application of Prelude.seq.
        //
        // We also know that we are at a 'top' level in the generated java function.
        // We want to break this into two java statements instead of calling the
        // primitive function Prelude.seq.
        // The first statement, corresponding to the first argument to seq, will be evaluated
        // and the second statement will return the graph of the second argument to seq.
        // We need to flatten the 'seq' expresion out into a collection of expressions, each
        // of which will generate a statement in the Java code.

        flattenTopLevelSeqHelper (chain[1], c);
        flattenTopLevelSeqHelper (chain[2], c);

    }

    /**
     * If the application chain is a fully saturated application
     * of Prelude.seq occurring at the top level of the function
     * we want to do a special transformation allowing the generated
     * Java to have a separate Java statement for each argument to 'seq'.
     * The first statement will fully evaluate the first argument to 'seq'
     * and the second statement will build/return the graph of the second
     * argument.
     * This is applied recursively to nested applications of seq.
     *
     * @param applChain - the application to try to optimize.
     * @param isNested - true if buildTopLevelSeq is being called recursively.
     * @param variableContext - the current variable context.
     * @return null if the applChain is not an application of seq, otherwise a JavaStatement
     * @throws CodeGenerationException
     */
    private JavaStatement buildTopLevelSeq (Expression.Appl applChain,
                                                      boolean isNested,
                                                      VariableContext variableContext) throws CodeGenerationException {

        Expression expressions[] = flattenTopLevelSeq(applChain);
        if (expressions == null) {
            return null;
        }

        Block block = new Block();

        // For all but the last expression we want to generate a Java statement
        // which will evaluate to WHNF.
        for (int i = 0, n = expressions.length - 1; i < n; ++i) {
            Expression e = expressions[i];
            JavaStatement js = makeStrictStatementFromSeqArg(e, variableContext);
            block.addStatement(js);
        }

        // For the last expression we simply generate a return expression using the
        // usual top level compilation scheme.
        block.addStatement (genS_R (expressions[expressions.length - 1], variableContext));

        return block;
    }

    /**
     * A helper function for buildTopLevelSeq
     * Takes an Expression and generates a JavaStatement that encapsulates the
     * evaluation of the expression.
     * @param e
     * @param variableContext
     * @return a JavaStatement which encapsulates evaluation of the expression.
     * @throws CodeGenerationException
     */
    private JavaStatement makeStrictStatementFromSeqArg (Expression e, VariableContext variableContext) throws CodeGenerationException {


        // If e is a local variable which is known to have been evaluated we don't have to do
        // anything.
        if (e.asVar() != null) {
            VarInfo vi = variableContext.getVariableInfo(e.asVar().getName());
            if (vi != null && vi.isEvaluated()) {
                return new Block();
            }
        }

        ExpressionContextPair pair;

        // Since we're going to ignore the result we can save ourselves a boxing step on
        // the result.  First check to see if the expression is a primitive operation.  If
        // it is we want to call generatePrimitiveOp directly with the R scheme.  This will
        // correctly handle primitive operations and foreign functions, including foreign
        // functions that return void.
        // If the expression is not a primitive op call generateUnboxedArgument() with an
        // unbox type of null (indicating there is no desired type).  This will handle
        // things like unboxed arguments, let variables, etc.

        //we don't directly call primitive operators when doing function operator tracing.
        //This will have the effect of ensuring that they get traced when called.

        if (LECCMachineConfiguration.generateDirectPrimOpCalls() &&
            BasicOpTuple.isBasicOp(e) != null) {
            //there is an assumption here that the primitive op will return a value in weak-head normal form
            //The purpose of this optimization is to avoid an unnecessary boxing and unboxing
            pair = generatePrimitiveOp (e, false, Scheme.R_SCHEME, variableContext);
        } else {
            pair = generateUnboxedArgument(null, e, variableContext);
        }

        Block block = pair.getContextBlock();
        JavaExpression firstArg = pair.getJavaExpression();
        block.addStatement(new ExpressionStatement(forceSafeForStatement(firstArg)));

        return block;
    }

    /**
     * Force a JavaExpression into a form which is safe to make into a statement.
     * @param e
     * @return the transformed expression
     */
    private JavaExpression forceSafeForStatement(JavaExpression e) {
        // If we are generating bytecode we may need to massage this expression
        // so that it is also an statement.
        // For example: the java expression 1 + 2 cannot be turned into a java
        // statement by simply putting a semicolon at the end of it.  For these cases
        // we pass the expression as an argument to the overloaded function RTValue.doNothing()
        // since a method invocation can be made into a statement this way.
        if (!LECCMachineConfiguration.generateBytecode()) {
            if (!(e instanceof JavaExpression.Assignment) &&
                !(e instanceof JavaExpression.ClassInstanceCreationExpression) &&
                ! (e instanceof JavaExpression.MethodInvocation)) {
                 e = new MethodInvocation.Static(JavaTypeNames.RTVALUE, "doNothing", e, JavaTypeNames.RTVALUE, JavaTypeName.VOID);
            }
        }

        return e;
    }

    /**
     * If the application chain is a fully saturated application
     * express it as a specialized graph node.
     * @param applChain
     * @param scheme
     * @param variableContext
     * @return the code that creates the graph node
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildApplicationChain (Expression.Appl applChain,
                                                         Scheme scheme,
                                                         VariableContext variableContext) throws CodeGenerationException {
        Expression[] chain = appChain (applChain);
        if (chain[0].asVar() == null) {
            if (LECCMachineConfiguration.OPTIMIZE_GENERAL_APP_CHAINS) {
                return buildGeneralApplicationChain (chain, scheme, variableContext);
            } else {
                return null;
            }
        }

        // If the function is a supercombinator we can potentially optimize the
        // representation.
        Expression.Var var = (Expression.Var)chain[0];

        if (var.getDataConstructor() != null) {
            return null;
        }

        if (LECCMachineConfiguration.OPTIMIZE_TYPECLASS_DICTS) {
            if (variableContext.isLocalVariable(var.getName())) {
                // The left hand side of the application chain is a local variable.
                // See if it is an optimizable dictionary application.
                ExpressionContextPair ecp =  buildLocalVarApplicationChain (applChain, scheme, variableContext);
                if (ecp != null) {
                    return ecp;
                }
            }
        }

        if (variableContext.isLocalVariable(var.getName())) {
            // This is an application of a local variable to some arguments
            // and the local variable is not one we recognize/optimize.
            if (LECCMachineConfiguration.OPTIMIZE_GENERAL_APP_CHAINS) {
                return buildGeneralApplicationChain (chain, scheme, variableContext);
            } else {
                return null;
            }
        }

        // Try to collect information about the called function.
        // This is used to decide how to encode the function call.
        MachineFunction mf = module.getFunction(var.getName());
        if (mf == null) {
            return null;
        }

        // arity of the called function
        int calledArity = mf.getArity();

        if (calledArity >= chain.length) {
            // This is an undersaturated SC application.
            if (codeGenerationStats != null) {
                codeGenerationStats.incrementUnderSaturation(calledArity, chain.length-1);
            }
            ExpressionContextPair ecp = buildUndersaturatedAppNode(chain, mf, variableContext);
            if (ecp != null) {
                return ecp;
            } else {
                return buildGeneralApplicationChain (chain, scheme, variableContext);
            }
        }

        // argument strictness of the called function
        boolean[] calledArgStrictness;
        if (LECCMachineConfiguration.IGNORE_STRICTNESS_ANNOTATIONS) {
            Arrays.fill (calledArgStrictness, false);
        } else {
            calledArgStrictness = mf.getParameterStrictness();
        }

        // argument types of the called function
        TypeExpr[] calledArgTypes = mf.getParameterTypes();

        // does the called function have strict primitive arguments
        boolean calledHasStrictPrimitiveArgs = false;
        for (int i = 0; i < calledArity; ++i) {
            if (calledArgStrictness[i] && SCJavaDefn.canTypeBeUnboxed(calledArgTypes[i])) {
                calledHasStrictPrimitiveArgs = true;
                break;
            }
        }

        // is the called function tail recursive
        boolean calledIsTailRecursive = mf.isTailRecursive();


        // If this is a fully saturated function call we can optimize it if:
        // 1) The number of arguments is > zero and <= OPTIMIZED_APP_CHAIN_LENGTH, because the runtime has special purpose application
        //    nodes for these size of functions.
        // 2) The called function has strict primitive arguments, since this causes the generation of a function
        //    specific application node class.
        // 3) The called function is tail recursive, since this causes the generation of a function specific application
        //    node class.
        if (chain.length -1 == calledArity &&
            (chain.length - 1 <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH || calledHasStrictPrimitiveArgs || calledIsTailRecursive) ) {
            if (scheme == Scheme.R_SCHEME) {
                return buildTopLevelApplicationChain(chain, calledArity, calledArgStrictness, calledArgTypes, variableContext);
            } else
            if (scheme == Scheme.E_SCHEME) {
                return buildStrictApplicationChain(chain, calledArity, calledArgStrictness, calledArgTypes, variableContext);
            } else
            if (scheme == Scheme.C_SCHEME) {
                return buildLazyApplicationChain(chain, calledArity, calledArgStrictness, calledArgTypes, variableContext);
            } else {
                throw new CodeGenerationException ("Attempt to build application chain from unknown scheme: " + scheme);
            }
        }

        if (chain.length - 1 > calledArity) {
            return buildOversaturatedApplicationChain(applChain, chain, calledArity, scheme, variableContext);
        }

        if (LECCMachineConfiguration.OPTIMIZE_GENERAL_APP_CHAINS) {
            return buildGeneralApplicationChain(chain, scheme, variableContext);
        } else {
            return null;
        }
    }

    private ExpressionContextPair buildOversaturatedApplicationChain (Expression.Appl e,
                                                                      Expression[] appChain,
                                                                      int calledArity,
                                                                      Scheme scheme,
                                                                      VariableContext variableContext) throws CodeGenerationException {
        Expression fsRoot = e;
        int diff = appChain.length - 1 - calledArity;
        for (int i = 0; i < diff; ++i) {
            fsRoot = fsRoot.asAppl().getE1();
        }

        ExpressionContextPair fsRootECP = null;
        if (scheme == Scheme.E_SCHEME || scheme == Scheme.R_SCHEME) {
            fsRootECP = genS_E(fsRoot, variableContext);
        } else {
            fsRootECP = genS_C(fsRoot, variableContext);
        }

        Block b = fsRootECP.getContextBlock();
        JavaExpression rootExpression = fsRootECP.getJavaExpression();

        JavaExpression extraArgs[] = new JavaExpression[diff];
        for (int i = calledArity + 1; i < appChain.length; ++i) {
            ExpressionContextPair argECP = genS_C(appChain[i], variableContext);
            b.addStatement(argECP.getContextBlock());
            extraArgs[i-calledArity-1] = argECP.getJavaExpression();
        }

        // If we are in a strict context we want to generate something like:
        // root.f3L(a, b, c).evaluate($ec);
        if (scheme == Scheme.E_SCHEME && diff <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH) {
            JavaExpression[] args = new JavaExpression [diff + 1];
            System.arraycopy(extraArgs, 0, args, 0, extraArgs.length);
            args[args.length-1] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
            JavaTypeName[] argTypes = new JavaTypeName[args.length];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
            argTypes[argTypes.length-1] = JavaTypeNames.RTEXECUTION_CONTEXT;
            MethodInvocation fL = new MethodInvocation.Instance(rootExpression, "f" + (diff) + "L", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
            MethodInvocation eval = SCJavaDefn.createInvocation(fL, SCJavaDefn.EVALUATE, EXECUTION_CONTEXT_VAR);
            return new ExpressionContextPair(eval, b);
        }

        // Now we want to build the application chain. (As efficiently as possible).
        int nApplicationArguments = extraArgs.length;
        int argIndex = 0;
        while (nApplicationArguments > 0) {
            if (nApplicationArguments >= 4) {
                JavaExpression[] args = new JavaExpression[4];
                JavaTypeName[] argTypes = new JavaTypeName[4];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);
                System.arraycopy(extraArgs, argIndex, args, 0, 4);
                rootExpression = new MethodInvocation.Instance(rootExpression, "apply", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex += 4;
                nApplicationArguments -= 4;
            } else
            if (nApplicationArguments == 3) {
                JavaExpression[] args = new JavaExpression[3];
                JavaTypeName[] argTypes = new JavaTypeName[3];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);
                System.arraycopy(extraArgs, argIndex, args, 0, 3);
                rootExpression = new MethodInvocation.Instance(rootExpression, "apply", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex += 3;
                nApplicationArguments -= 3;
            } else
            if (nApplicationArguments == 2) {
                JavaExpression[] args = new JavaExpression[2];
                JavaTypeName[] argTypes = new JavaTypeName[2];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);
                System.arraycopy(extraArgs, argIndex, args, 0, 2);
                rootExpression = new MethodInvocation.Instance(rootExpression, "apply", args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex += 2;
                nApplicationArguments -= 2;
            } else
            if (nApplicationArguments == 1) {
                rootExpression = new MethodInvocation.Instance(rootExpression, "apply", extraArgs[argIndex], JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                argIndex++;
                nApplicationArguments--;
            }
        }

        if (scheme == Scheme.E_SCHEME) {
            // Strict application scheme.  Call 'evalaute' on the RTValue.
            rootExpression = createInvocation(rootExpression, EVALUATE, EXECUTION_CONTEXT_VAR);
        }

        return new ExpressionContextPair(rootExpression, b);
    }


    /**
     * If the application chain is a fully saturated application of a dictionary function
     * express it as a specialized graph node.
     * @param applChain
     * @param scheme 0 - R-scheme, 1 - E-scheme, 2 C-Scheme.
     * @param variableContext
     * @return the code that creates the graph node
     * @throws CodeGenerationException
     */
    private ExpressionContextPair buildLocalVarApplicationChain (Expression.Appl applChain,
                                                                 Scheme scheme,
                                                                 VariableContext variableContext) throws CodeGenerationException {

        int nExpectedSecondaryArgs =
            ExpressionAnalyzer.getNOversaturatedDictionaryArgs(applChain, getModule().getModuleTypeInfo());
        if (nExpectedSecondaryArgs < 0) {
            return null;
        }
        //System.out.println (dai.typeClass.getName() + " -> " + dai.classMethod.getName() + " -> " + dai.nExpectedSecondaryArgs);

        Expression[] chain = appChain (applChain);

        // Is this a dictionary function that is itself the function to be used.
        if (chain.length - 1 == nExpectedSecondaryArgs) {
            return null;
        } else {
            final int nArgs = chain.length - 1;
            if (nArgs == 2 || nArgs == 3) {
                if (codeGenerationStats != null) {
                    // Parse out the type class name.
                    // Variable name will be in the form $dictvarModule.Class#N where N is an integer
                    // number and where dictvarModule may contain zero or more dots.
                    String name = chain[0].asVar().getName().getUnqualifiedName();
                    name = name.substring(8);

                    QualifiedName qualifiedClassName = QualifiedName.makeFromCompoundName(name.substring(0, name.indexOf("#")));

                    codeGenerationStats.incrementOptimizedDictionaryApplication(qualifiedClassName);
                }

                // First generate the java expressions for the CAL expressions.
                ExpressionContextPair[] ecp = new ExpressionContextPair[chain.length];
                for (int i = 0; i < chain.length; ++i) {
                    ecp[i] = genS_C(chain[i], variableContext);
                }

                Block newContext = ecp[0].getContextBlock();
                for (int i = 1; i < chain.length; ++i) {
                    newContext.addStatement(ecp[i].getContextBlock());
                }

                JavaExpression root = ecp[0].getJavaExpression();

                JavaExpression args[] = new JavaExpression[chain.length];
                for (int i = 0; i < chain.length; ++i) {
                    args[i] = ecp[i].getJavaExpression();
                }

                JavaTypeName type;
                switch (nArgs) {
                    case 2:
                        type = JavaTypeNames.RTOAPP2;
                        break;
                    case 3:
                        type = JavaTypeNames.RTOAPP3;
                         break;
                    default:
                        throw new IllegalStateException();
                }

                JavaTypeName constructorArgTypes[] = new JavaTypeName[chain.length];
                Arrays.fill(constructorArgTypes, JavaTypeNames.RTVALUE);
                root = new JavaExpression.ClassInstanceCreationExpression(type, args, constructorArgTypes);
                if (scheme == Scheme.E_SCHEME) {
                    // This is being built in a strict scheme so call evaluate on the RTValue.
                    root = createInvocation (root, EVALUATE, EXECUTION_CONTEXT_VAR);
                }
                return new ExpressionContextPair (root, newContext);

            }
        }

        return null;
    }

    /**
     * Generate the java expression corresponding to a variable reference.
     * @param var
     * @param scheme
     * @param variableContext
     * @return JavaExpression
     * @throws CodeGenerationException
     */
    private JavaExpression expressionVarToJavaDef (Expression.Var var, Scheme scheme, VariableContext variableContext) throws CodeGenerationException {
        return generateVar (var.getName(), var.getDataConstructor(), var.getForeignFunctionInfo(), scheme, variableContext);
    }

    /**
     * Generate the java expression corresponding to a variable.
     * @param qualName
     * @param dc - may be null
     * @param foreignFunctionInfo - may be null
     * @param scheme
     * @param variableContext
     * @return the JavaExpression
     * @throws CodeGenerationException
     */
    private JavaExpression generateVar (QualifiedName qualName,
                                        DataConstructor dc,
                                        ForeignFunctionInfo foreignFunctionInfo,
                                        Scheme scheme,
                                        VariableContext variableContext) throws CodeGenerationException {
        if (variableContext.isLocalVariable(qualName)) {
            if (scheme == Scheme.E_SCHEME) {
                JavaExpression strictRef = variableContext.getStrictReference(qualName);
                if (strictRef != null) {
                    return strictRef;
                }
            } else {
                JavaExpression lazyRef = variableContext.getLazyReference(qualName);
                if (lazyRef != null) {
                    return lazyRef;
                }
            }

            throw new CodeGenerationException ("Unable to obtain reference to local variable " + qualName);
        } else {
            // This is not a local symbol (i.e. it is a supercombinator, foreign function, or constructor)
            // Kernel may claim the symbol if its defined in the Prelude module
            // First determine what kind of symbol this is
            if (dc != null) {
                // Constructor
                // Emit symbol with factory invocation

                JavaTypeName typeName = CALToJavaNames.createTypeNameFromDC(dc, module);
                JavaExpression dcInstance;
                dcInstance = getReferencedDC(typeName, dc);

                return dcInstance;
            } else {
                MachineFunction mf = module.getFunction(qualName);
                if (mf == null) {
                    throw new CodeGenerationException("Unable to retrieve MachineFunction for " + qualName.getQualifiedName());
                }

                LECCModule.FunctionGroupInfo fgi = module.getFunctionGroupInfo(qualName);
                if (fgi == null) {
                    throw new CodeGenerationException("Unable to retrieve FunctionGroupInfo for " + qualName.getQualifiedName());
                }

                // Function
                JavaTypeName fullClass = CALToJavaNames.createTypeNameFromSC(qualName, module);

                if (mf.getArity() == 0) {
                    // This is a CAF or a zero arity function.
                    // Either way we need to invoke the static factory method
                    // 'make'.
                    if (fgi.getNCAFs() + fgi.getNZeroArityFunctions() <= 1) {
                        return new MethodInvocation.Static (
                                fullClass,
                                "make",
                                new JavaExpression[]{SCJavaDefn.EXECUTION_CONTEXT_VAR},
                                new JavaTypeName[]{JavaTypeNames.RTEXECUTION_CONTEXT},
                                JavaTypeNames.RTFUNCTION);
                    } else {
                        int functionIndex = fgi.getFunctionIndex(qualName.getUnqualifiedName());
                        return new MethodInvocation.Static (
                                fullClass,
                                "make",
                                new JavaExpression[]{LiteralWrapper.make(Integer.valueOf(functionIndex)), SCJavaDefn.EXECUTION_CONTEXT_VAR},
                                new JavaTypeName[]{JavaTypeName.INT, JavaTypeNames.RTEXECUTION_CONTEXT},
                                JavaTypeNames.RTFUNCTION);
                    }
                }


                // If this is a self reference or a reference to a function
                // in the same function group use the '$instance....' member.
                if (fullClass.equals (thisTypeName)) {
                    return new JavaField.Static(fullClass, CALToJavaNames.getInstanceFieldName(qualName, module), thisTypeName);
                }

                // Use the static 'instance' field in the class for the referenced function.
                return new JavaField.Static(fullClass, CALToJavaNames.getInstanceFieldName(qualName, module), fullClass);
            }
        }
    }

    /**
     * Return true if an exception handling block for the given
     * exception already exists.
     * @param exceptionClass
     * @return boolean
     */
    private boolean exceptionHandled(Class<?> exceptionClass) {
        for (final JavaExceptionHandler eh : exceptionInfo) {
            if (eh.getExceptionClass() == exceptionClass) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a JavaExceptionHandler for the specified exception class.
     * @param exceptionClass
     */
    private void addExceptionHandler(Class<?> exceptionClass) {
        // Generate a Block corresponding to:
        // throw new RTForeignFunctionException(RTValue.generateForeignFunctionErrorMessage(exception, generatedClassName, supercombinatorName), null, exception);

        JavaTypeName exceptionType = JavaTypeName.make(exceptionClass);
        JavaExpression args[] = new JavaExpression [4];
        JavaTypeName argTypes[] = new JavaTypeName [4];
        args[0] = new LocalVariable("caught_exception" , exceptionType);
        argTypes[0] = JavaTypeName.THROWABLE;
        args[1] = LiteralWrapper.make(CALToJavaNames.createFullClassNameFromSC(getQualifiedName(), module));
        argTypes[1] = JavaTypeName.STRING;
        args[2] = LiteralWrapper.make(currentModuleName.toSourceText());
        argTypes[2] = JavaTypeName.STRING;
        args[3] = LiteralWrapper.make(getFunctionName());
        argTypes[3] = JavaTypeName.STRING;
        MethodInvocation genMessage = new MethodInvocation.Static (JavaTypeNames.RTVALUE,
                                                            "generateForeignFunctionErrorMessage",
                                                            args,
                                                            argTypes,
                                                            argTypes[1]);

        JavaExpression constructorArgs[] = new JavaExpression [2];
        JavaTypeName constructorArgTypes[] = new JavaTypeName [2];
        constructorArgs[0] = genMessage;
        constructorArgTypes[0] = JavaTypeName.STRING;
        constructorArgs[1] = new LocalVariable("caught_exception", exceptionType);
        constructorArgTypes[1] = JavaTypeName.THROWABLE;
        ClassInstanceCreationExpression nc =
            new ClassInstanceCreationExpression (JavaTypeNames.RTFOREIGN_FUNCTION_EXCEPTION, constructorArgs, constructorArgTypes);

        ThrowStatement throwSt = new ThrowStatement (nc);
        JavaExceptionHandler eh = new JavaExceptionHandler(exceptionClass, "caught_exception", throwSt);
        exceptionInfo.add(eh);
    }

    void setInstanceName (String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * @return Returns the module.
     */
    LECCModule getModule() {
        return module;
    }

    ModuleName getModuleName() {
        return module.getName();
    }

    String getFunctionName () {
        return functionName;
    }

    QualifiedName getQualifiedName () {
        return qualifiedName;
    }


    /**
     * Create a zero-argument method invocation from a MethodInfo.
     * @param target the target of the invocation.
     * @param mi
     * @return MethodInvocation the resulting method invocation
     */
    static MethodInvocation createInvocation(JavaExpression target, MethodInfo mi) {
        return new MethodInvocation.Instance(target, mi.getMethodName(), mi.getReturnType(), mi.getInvocationType());
    }

    /**
     * Create a single -argument method invocation from a MethodInfo.
     * @param target the target of the invocation.
     * @param mi
     * @param arg the argument to the invocation.
     * @return MethodInvocation the resulting method invocation
     */
    static MethodInvocation createInvocation(JavaExpression target, MethodInfo mi, JavaExpression arg) {
        return new MethodInvocation.Instance(target, mi.getMethodName(), arg, mi.getParamTypes()[0], mi.getReturnType(), mi.getInvocationType());
    }

    /**
     * Helper method to create a zero-argument virtual method invocation.
     * @param target the target of the invocation.
     * @param methodName the name of the method
     * @param returnType the return type of the method
     * @return MethodInvocation the resulting method invocation
     */
    static MethodInvocation createVirtualInvocation(JavaExpression target, String methodName, JavaTypeName returnType) {
        return new MethodInvocation.Instance(target, methodName, returnType, InvocationType.VIRTUAL);
    }

    /**
     * Create a method invocation for one of the "apply" calls (taking an array of 1 - 4 args) in RTFunction.
     *      RTFunction:  public RTValue apply(RTValue argument, RTValue arg2, ...);
     * @param target the target of the invocation.
     * @param args the args to the apply call.
     * @return MethodInvocation the resulting method invocation
     */
    static MethodInvocation createApplyInvocation(JavaExpression target, JavaExpression[] args) {
        JavaTypeName[] argTypes = new JavaTypeName[args.length];
        Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

        // arg types and return type of apply is RTValue
        return new MethodInvocation.Instance(target, "apply", args, argTypes, JavaTypeNames.RTVALUE, InvocationType.VIRTUAL);
    }

    /**
     * Create a method invocation for the "make" call in an SC implementation.
     * eg. CAL_Short:   public static final CAL_Short make(short value);
     * @param calPrimitiveClass eg. RTKernel.CAL_Byte.class
     * @param arg the argument to the invocation
     * @return MethodInvocation the resulting method invocation
     */
    static MethodInvocation createPrimitiveMakeInvocation(Class<?> calPrimitiveClass, JavaExpression arg) {

        if (calPrimitiveClass == RTData.CAL_Boolean.class) {
            return createMakeKernelBooleanInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Byte.class) {
            return createMakeKernelByteInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Char.class) {
            return createMakeKernelCharInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Double.class) {
            return createMakeKernelDoubleInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Float.class) {
            return createMakeKernelFloatInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Int.class) {
            return createMakeKernelIntInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Integer.class) {
            return createMakeKernelIntegerInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Long.class) {
            return createMakeKernelLongInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Opaque.class) {
            return createMakeKernelOpaqueInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_Short.class) {
            return createMakeKernelShortInvocation(arg);
        }

        if (calPrimitiveClass == RTData.CAL_String.class) {
            return createMakeKernelStringInvocation(arg);
        }

        throw new IllegalArgumentException("unexpected primitive type " + calPrimitiveClass + ".");
    }

    /**
     * Creates an invocation of the static constructor function: RTData.CAL_Boolean.make(boolean b) in the Java expression model.
     * It is efficient in that static constants are used for the various JavaTypeName objects. The earlier lack of use of static
     * constants proved to be a significant compile-time performance penalty after benchmarking.
     * @param arg
     * @return MethodInvocation
     */
    static MethodInvocation createMakeKernelBooleanInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_BOOLEAN, "make", arg, JavaTypeName.BOOLEAN, JavaTypeNames.RTDATA_BOOLEAN);
    }
    static MethodInvocation createMakeKernelByteInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_BYTE, "make", arg, JavaTypeName.BYTE, JavaTypeNames.RTDATA_BYTE);
    }
    static MethodInvocation createMakeKernelCharInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_CHAR, "make", arg, JavaTypeName.CHAR, JavaTypeNames.RTDATA_CHAR);
    }
    static MethodInvocation createMakeKernelDoubleInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_DOUBLE, "make", arg, JavaTypeName.DOUBLE, JavaTypeNames.RTDATA_DOUBLE);
    }
    static MethodInvocation createMakeKernelFloatInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_FLOAT, "make", arg, JavaTypeName.FLOAT, JavaTypeNames.RTDATA_FLOAT);
    }
    static MethodInvocation createMakeKernelIntInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_INT, "make", arg, JavaTypeName.INT, JavaTypeNames.RTDATA_INT);
    }
    static MethodInvocation createMakeKernelIntegerInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_INTEGER, "make", arg, JavaTypeName.BIG_INTEGER, JavaTypeNames.RTDATA_INTEGER);
    }
    static MethodInvocation createMakeKernelLongInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_LONG, "make", arg, JavaTypeName.LONG, JavaTypeNames.RTDATA_LONG);
    }
    static MethodInvocation createMakeKernelOpaqueInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_OPAQUE, "make", arg, JavaTypeName.OBJECT, JavaTypeNames.RTDATA_OPAQUE);
    }
    static MethodInvocation createMakeKernelShortInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_SHORT, "make", arg, JavaTypeName.SHORT, JavaTypeNames.RTDATA_SHORT);
    }
    static MethodInvocation createMakeKernelStringInvocation(JavaExpression arg) {
        return new MethodInvocation.Static(JavaTypeNames.RTDATA_STRING, "make", arg, JavaTypeName.STRING, JavaTypeNames.RTDATA_STRING);
    }

    /**
     * Generate a java name for the given field in the data constructor.
     * @param dc
     * @param index
     * @return the java name.
     */
    static String getJavaFieldNameFromDC (DataConstructor dc, int index) {
        FieldName fn = dc.getNthFieldName(index);
        String name = getJavaFieldNameFromFieldName(fn);
        if (name == null) {
            name = "field" + index;
        }
        return name;
    }

    /**
     * Generate a java name for the given FieldName.
     * @param fn
     * @return the java name.
     */
    static String getJavaFieldNameFromFieldName (FieldName fn) {
        if (fn == null) {
            return null;
        }
        String fieldName = CALToJavaNames.fixupVarName(fn.toString());
        if (!fieldName.startsWith("_")) {
            fieldName = "_" + fieldName;
        }
        return fieldName;
    }

    /**
     * Use this function to get the field types for a data constructor.
     * Appropriate conversions are done based on whether or not unboxed DC fields are being used
     * and whether enums are treated as ints.
     * @param dc
     * @return an array of TypeExpr
     */
    static TypeExpr[] getFieldTypesForDC (DataConstructor dc) {
        TypeExpr[] fieldTypes = new TypeExpr[dc.getArity()];
        TypeExpr dcType = dc.getTypeExpr();
        TypeExpr[] tft = dcType.getTypePieces(dc.getArity());
        System.arraycopy(tft, 0, fieldTypes, 0, fieldTypes.length);

        return fieldTypes;
    }

    /**
     * Get the TypExpr for the type of the given FieldName in the given DataConstructor
     * @param dc
     * @param fieldName
     * @return the type of the field
     */
    static private TypeExpr getFieldTypeForDC (DataConstructor dc, FieldName fieldName) {
        TypeExpr[] fieldTypes = new TypeExpr[dc.getArity()];
        TypeExpr dcType = dc.getTypeExpr();
        TypeExpr[] tft = dcType.getTypePieces(dc.getArity());
        System.arraycopy(tft, 0, fieldTypes, 0, fieldTypes.length);

        return fieldTypes[dc.getFieldIndex(fieldName)];
    }

    /**
     * This class maps from a CAL name to the current Java equivalent.
     * @author RCypher
     *
     */
    private final class VariableContext {
        /**
         * A stack of Map of QualifiedName -> VarInfo.  The list on the top of the stack represents the
         * a java scope for the generation of let variables.
         */
        private final ArrayStack<Map<QualifiedName, VarInfo>> javaScopeStack = ArrayStack.make();

        /**
         * CAL allows duplicate variable names in nested scopes.  Since java doesn't
         * we need to make sure that we never assign a duplicate java name.
         */
        private final Set<String> allJavaNames = new HashSet<String>();

        VariableContext () {
            javaScopeStack.push (new LinkedHashMap<QualifiedName, VarInfo>());
        }

        VarInfo getVariableInfo (QualifiedName qn) {

            for (int i = javaScopeStack.size()-1, j = 0; i >= j; i--) {
                Map<QualifiedName, VarInfo> scope = javaScopeStack.get(i);
                VarInfo vi = scope.get(qn);
                if (vi != null) {
                    return vi;
                }
            }

            return null;
        }

        JavaTypeName getUnboxedType (QualifiedName qn) {
            VarInfo info = getVariableInfo(qn);
            if (info != null) {
                return info.getUnboxedType();
            }
            return null;
        }

        boolean isLocalVariable (QualifiedName qn) {
            return getVariableInfo(qn) != null;
        }

        boolean isFunctionArgument (QualifiedName qn) {
            VarInfo info = getVariableInfo(qn);
            if (info != null) {
                return info instanceof VarInfo.Argument;
            }
            return false;
        }

        boolean isDCField (QualifiedName qn) {
            VarInfo info = getVariableInfo(qn);
            if (info != null) {
                return info instanceof VarInfo.DCMember;
            }
            return false;
        }

        boolean isRecordField (QualifiedName qn) {
            VarInfo info = getVariableInfo(qn);
            if (info != null) {
                return info instanceof VarInfo.RecordField;
            }
            return false;
        }

        boolean isVarPreEvaluated (QualifiedName qn) {
            VarInfo info = getVariableInfo(qn);
            if (info != null) {
                return info.isEvaluated();
            }
            return false;
        }
        List<VarInfo> getAllVariableInfo () {
            List<VarInfo> list = new ArrayList<VarInfo>();
            for (int i = javaScopeStack.size()-1, j = 0; i >= j; i--) {
                Map<QualifiedName, VarInfo> scope = javaScopeStack.get(i);
                list.addAll(scope.values());
            }
            return list;
        }

        /**
         * Push a java scope onto the stack.
         */
        public void pushJavaScope() {
            javaScopeStack.push (new LinkedHashMap<QualifiedName, VarInfo>());
        }

        /**
         * Add a mapping for a let variable to the current scope.
         * @param name
         * @param varTypeExpr
         * @return varInfo
         */
        public VarInfo.LetNonRec addLetNonRecVar (QualifiedName name, TypeExpr varTypeExpr) throws CodeGenerationException {
            String javaName = "letVar_" + makeJavaName(name.getUnqualifiedName());
            javaName = makeNameUnique (javaName);
            VarInfo.LetNonRec info = new VarInfo.LetNonRec (name, javaName, varTypeExpr);
            addVar (name, info);
            if (SCJavaDefn.canTypeBeUnboxed(varTypeExpr)) {
                info.setUnboxedType(SCJavaDefn.typeExprToTypeName(varTypeExpr));
            }
            return info;
        }

        /**
         * Add a mapping for a letrec variable to the current scope.
         * @param name
         * @param varType
         * @return varInfo
         */
        public VarInfo.LetRec addLetRecVar (QualifiedName name, TypeExpr varType) {
            String javaName = "letVar_" + makeJavaName(name.getUnqualifiedName());
            javaName = makeNameUnique (javaName);
            VarInfo.LetRec info = new VarInfo.LetRec (name, javaName, varType);
            addVar (name, info);
            return info;
        }

        /**
         *
         * @return Map of (QualifiedName -> VarInfo)
         */
        Map<QualifiedName, VarInfo> getCurrentScope () {
            return javaScopeStack.peek();
        }

        /**
         * Generate the java source for any variables in the current java scope.
         * @return the resulting code contribution
         * @throws CodeGenerationException
         */
        public Block popJavaScope () throws CodeGenerationException {
            Map<QualifiedName, VarInfo> varMap = javaScopeStack.pop();

            Block declarationBlock = new Block();
            Block definitionBlock = new Block();

            for (final Map.Entry<QualifiedName, VarInfo> entry : varMap.entrySet()) {
                VarInfo info = entry.getValue();

                String javaName = info.getJavaName();

                if (info instanceof VarInfo.LetRec) {
                    JavaExpression boxedVarDef = ((VarInfo.LetRec)info).getLazyVarDef();
                    if (boxedVarDef == null) {
                        throw new CodeGenerationException ("Missing boxed definition for let variable: " + info.getCALName());
                    }

                    LocalVariable letNameVariable = new LocalVariable(javaName, JavaTypeNames.RTINDIRECTION);

                    // RTValue javaName = new RTIndirection();
                    JavaExpression newIndirection = new ClassInstanceCreationExpression(JavaTypeNames.RTINDIRECTION);
                    LocalVariableDeclaration letDecl = new LocalVariableDeclaration(letNameVariable, newIndirection);
                    declarationBlock.addStatement(letDecl);

                    // Emit let variable definition.
                    // letName.setResult(letDef);
                    MethodInvocation letInvocation = createInvocation(letNameVariable, SETRESULT, boxedVarDef);
                    definitionBlock.addStatement(new ExpressionStatement(letInvocation));
                } else
                if (info instanceof VarInfo.LetNonRec) {
                    // This variable is referenced multiple times.
                    // We need to declare/initialize a local java variable.

                    // If the variable is already evaluated we behave differently.
                    if (info.isEvaluated()) {
                        if (info.getUnboxedType() != null) {
                            // The definition function will return an unboxed value.
                            // Declare an unboxed local variable.
                            LocalVariable unboxedLocal = new LocalVariable(info.getJavaName()+"$U", info.getUnboxedType());
                            LocalVariableDeclaration unboxedVarDecl = new LocalVariableDeclaration(unboxedLocal, ((VarInfo.LetNonRec)info).getUnboxedVarDef());
                            declarationBlock.addStatement(unboxedVarDecl);

                        } else {
                            // All references will be boxed.
                            // Declare a boxed local variable an update the strict/lazy refs.
                            // We use the strict variable def since we know this variable is already evaluated.
                            LocalVariable boxedLocal = new LocalVariable(info.getJavaName(), JavaTypeNames.RTVALUE);
                            LocalVariableDeclaration boxedVarDecl = new LocalVariableDeclaration(boxedLocal, ((VarInfo.LetNonRec)info).getStrictVarDef());
                            declarationBlock.addStatement(boxedVarDecl);
                        }
                    } else {
                        // The variable is not pre-evaluated.
                        // The variable is referenced more than once.
                        // We want to use the lazy initializer.
                        LocalVariable boxedLocal = new LocalVariable(info.getJavaName(), JavaTypeNames.RTVALUE);
                        LocalVariableDeclaration boxedVarDecl = new LocalVariableDeclaration(boxedLocal, ((VarInfo.LetNonRec)info).getLazyVarDef());
                        declarationBlock.addStatement(boxedVarDecl);
                    }

                } else
                if (info instanceof VarInfo.DCMember){
                    // The DCMember instance of VarInfo will only have one
                    // varDef defined.  i.e. unboxed or strict or lazy.
                    JavaExpression unboxedVarDef = ((VarInfo.DCMember)info).getUnboxedVarDef();
                    JavaExpression lazyVarDef = ((VarInfo.DCMember)info).getLazyVarDef();
                    JavaExpression strictVarDef = ((VarInfo.DCMember)info).getStrictVarDef();

                    if (unboxedVarDef != null) {
                        // Base declarations on unboxed definition.
                        // Declare a local unboxed variable and update the unboxed reference.
                        LocalVariable unboxedLocal = new LocalVariable(javaName+"$U", info.getUnboxedType());
                        LocalVariableDeclaration letDecl = new LocalVariableDeclaration(unboxedLocal, unboxedVarDef);
                        declarationBlock.addStatement(letDecl);
                    } else
                    if (strictVarDef != null) {
                        // If there is a strict variable definition we should never have unboxed use.
                        // Since a strict variable of an unboxable type would have an unboxed variable definition.
                        // Declare a boxed local and update the strict and lazy references.
                        LocalVariable boxedLocal = new LocalVariable (javaName, JavaTypeNames.RTVALUE);
                        LocalVariableDeclaration boxedDecl = new LocalVariableDeclaration (boxedLocal, strictVarDef);
                        declarationBlock.addStatement(boxedDecl);
                        info.updateStrictReference(boxedLocal);
                        info.updateLazyReference(boxedLocal);
                    } else {
                        // Lazy variable.
                        // Declare a lazy local variable.
                        LocalVariable lazyLocal = new LocalVariable(info.getJavaName(), JavaTypeNames.RTVALUE);
                        LocalVariableDeclaration lazyDecl = new LocalVariableDeclaration(lazyLocal, lazyVarDef);
                        declarationBlock.addStatement(lazyDecl);
                    }
                }
            }

            declarationBlock.addStatement(definitionBlock);
            return declarationBlock;
        }

        /**
         * Add a mapping for a SC argument to the current scope.
         * @param name
         * @param isEvaluated
         * @param argType
         * @param argTypeExpr
         * @return VarInfo
         */
        public VarInfo addArgument (QualifiedName name, boolean isEvaluated, JavaTypeName argType, TypeExpr argTypeExpr) {
            addVar (name, new VarInfo.Argument (name, makeJavaName(name.getUnqualifiedName()), argTypeExpr));
            VarInfo varInfo = getVariableInfo(name);

            // Mark evaluation state.
            varInfo.setEvaluated(isEvaluated);

            JavaExpression straightReference;
            if (isEvaluated && argType != null && !argType.equals(JavaTypeNames.RTVALUE) && !argType.equals(JavaTypeName.CAL_VALUE)) {
                straightReference= new LocalName(varInfo.getJavaName(), argType);
            } else {
                straightReference= new LocalName(varInfo.getJavaName(), JavaTypeNames.RTVALUE);
            }

            // Set up boxed/unboxed references
            if (isEvaluated) {
                if (argType != null && !argType.equals(JavaTypeNames.RTVALUE) && !argType.equals(JavaTypeName.CAL_VALUE)) {
                    varInfo.setUnboxedType(argType);
                    // The unboxed reference is simply the argument.
                    varInfo.updateUnboxedReference(straightReference);

                    // The boxed reference wraps the arg in one of the RTData classes.
                    JavaExpression boxedReference = boxExpression(argType, straightReference);

                    // The boxed reference can be used for both lazy and strict, since we know that it is
                    // already evaluated.
                    varInfo.updateLazyReference(boxedReference);
                    varInfo.updateStrictReference(boxedReference);
                } else {
                    // Since we know that the argument is strict we can call getValue on the lazy reference
                    // for the strict reference.
                    varInfo.updateLazyReference(straightReference);
                    MethodInvocation getValue = new MethodInvocation.Instance(straightReference, "getValue", JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                    varInfo.updateStrictReference(getValue);
                }
            } else {
                // Without an arg type the lazy reference is just the arg and there is no unboxed ref or strict ref.
                varInfo.updateLazyReference(straightReference);
                varInfo.updateStrictReference(SCJavaDefn.createInvocation(straightReference, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR));
            }

            return varInfo;
        }

        /**
         * Add a mapping for an extracted data constructor field to the current scope.
         * @param name
         * @param fieldTypeExpr
         * @return VarInfo
         */
        public VarInfo.DCMember addDCField (QualifiedName name, TypeExpr fieldTypeExpr) throws CodeGenerationException {
            VarInfo.DCMember newInfo = new VarInfo.DCMember (name, makeJavaName(name.getUnqualifiedName()), fieldTypeExpr);
            addVar (name, newInfo);
            if (SCJavaDefn.canTypeBeUnboxed(fieldTypeExpr)) {
                newInfo.setUnboxedType(SCJavaDefn.typeExprToTypeName(fieldTypeExpr));
            }
            return newInfo;
        }

        /**
         * Add a mapping for an extracted record field to the current scope.
         * @param name
         * @param varType
         * @return VarInfo
         */
        public VarInfo.RecordField addRecordField (QualifiedName name, TypeExpr varType) {
            VarInfo.RecordField newInfo = new VarInfo.RecordField (name, makeJavaName(name.getUnqualifiedName()), varType);
            addVar (name, newInfo);
            return newInfo;
        }

        private void addVar (QualifiedName name, VarInfo info) {
            if (name == null || info == null) {
                throw new NullPointerException("Invalid argument in VariableContext.addVar()");
            }

            Map<QualifiedName, VarInfo> scope = javaScopeStack.peek();
            scope.put(name, info);
        }

        /**
         * Make a Java name for a variable based on the CAL name
         * (i.e. the variable name supplied by the compiler).
         * @param calName
         * @return a valid Java name that is unique in this context.
         */
        private String makeJavaName (String calName) {

            // First try to simplify the CAL name.
            String javaName = simplifyCALName(calName);

            // Now we need to clean the java name to make sure it is
            // valid java. i.e. doesn't conflict with reserved words, etc.
            javaName = CALToJavaNames.fixupVarName(javaName);

            // We want to make sure that the java name is unique
            // within this context.
            return makeNameUnique(javaName);
        }

        /**
         * This function tries to take a CAL variable name as
         * supplied by the compiler and simplify it so that it
         * is closer to the original variable name that appeared
         * in the CAL code.
         * @param calName
         * @return a simpler version of the CAL name or the unchanged name.
         */
        private String simplifyCALName (String calName) {
            // First we want to try to simplify the calName.
            // What starts out as a fairly simple name in CAL
            // code can become quite complex by the time that
            // the compiler is through with it.  For example
            // the argument x in the function NonCAFEntryPoint
            // will be renamed to nonCAFEntryPoint$x$1.

            String javaName = calName;

            // Look for the last and second-to-last incidents of $
            // and use what is between them.
            int lastIndex = calName.lastIndexOf('$');
            if (lastIndex >= 1) {
                int secondLastIndex = calName.lastIndexOf('$', lastIndex - 1);
                if (secondLastIndex > -1  && lastIndex - secondLastIndex > 1) {
                    javaName = calName.substring(secondLastIndex + 1, lastIndex);
                }
            }

            return javaName;
        }

        /**
         * Make sure that a proposed java name hasn't already been used for a
         * java entity at this or a higher scope.
         * @param baseName
         * @return a unique java name.
         */
        private String makeNameUnique (String baseName) {
            String name = baseName;
            int modifier = 1;
            while (allJavaNames.contains(name)) {
                name = baseName + "_" + modifier++;
            }

            allJavaNames.add (name);
            return name;
        }

        JavaExpression getStrictReference (QualifiedName name) {
            VarInfo vi = getVariableInfo(name);
            if (vi == null) {
                return null;
            }


            return vi.strictReference;
        }

        JavaExpression getLazyReference (QualifiedName name) {
            VarInfo vi = getVariableInfo(name);
            if (vi == null) {
                return null;
            }

            return vi.lazyReference;
        }

        JavaExpression getUnboxedReference (QualifiedName name) {
            VarInfo vi = getVariableInfo(name);
            if (vi == null) {
                return null;
            }

            return vi.unboxedReference;
        }


    }

    /**
     * @return Returns the isTailRecursive flag.
     */
    boolean isTailRecursive() {
        return isTailRecursive;
    }

    /**
     * @return true if this function is primitive.
     */
    public boolean isPrimitive() {
        return isPrimitive;
    }

    /**
     * @return true if this function is a constant applicative form.
     */
    boolean isCAF () {
        return getArity() == 0 && !isForeign();
    }

    /**
     * @return true if this function is foreign.
     */
    public boolean isForeign() {
        return isForeign;
    }

    /** Return the set of names of the strongly connected component set which contains this
     * entity.
     * @return Set
     */
    Set<String> getStronglyConnectedComponents() {
        return connectedComponents;
    }


    /**
     * Represents a value literal (kernel class and constructor expression)
     * @author Raymond Cypher
     */
    static final class KernelLiteral {
        /** The Class of the CAL literal. */
        private final Class<?> kernelTypeClass;

        /** An expression to construct the literal. */
        private final JavaExpression constructorExpression;

        /** A suggested String identifier. */
        private final String suggestedIdent;

        /** The literal object this KernelLiteral represents. */
        private final Object literal;

        /** A string for the symbol used to refer to this literal. */
        private final String symbol;

        private int boxedUseCount = 0;
        private int unboxedUseCount = 0;

        private JavaExpression boxedReference;
        private JavaExpression unboxedReference;

        /**
         * Construct a KernelLiteral that represents the given object.
         * @param literal
         * @param sharedValues
         * @param containingClass
         * @throws CodeGenerationException
         */
        private KernelLiteral (Object literal, SharedValues sharedValues, JavaTypeName containingClass) throws CodeGenerationException {
            this.literal = literal;

            // RTData value types are special in that they represent the only values (primitives) that can be
            // 'met' as values directly in CAL code (i.e. as literals).  When the backend is asked to represent
            // a literal the value has to be 'converted' to a declaration of a value in terms of RTData
            // value types
            // This routine performs this translation for 'known' types

            // This could be done by having each primitive type class register its Java primitive
            // object wrapper class with an RTData object, which could then map to the class name.
            // For now, we'll just use the static map...
            String proposedIdent;
            Class<?> calObjectClass = literalMap.get(literal.getClass());
            if (calObjectClass != null) {
                kernelTypeClass = calObjectClass;
                constructorExpression = LiteralWrapper.make(literal);
                proposedIdent = literal.toString();
            } else
            if (literal instanceof String) {
                kernelTypeClass = CAL_String.class;
                constructorExpression = LiteralWrapper.make(literal);
                proposedIdent = StringEncoder.encodeString(literal.toString());
            } else
            if (literal instanceof Character) {
                kernelTypeClass = CAL_Char.class;
                constructorExpression = LiteralWrapper.make(literal);
                proposedIdent = StringEncoder.encodeChar(((Character)literal).charValue());
            } else
            if (literal instanceof BigInteger) {
                kernelTypeClass = CAL_Integer.class;
                constructorExpression =  new ClassInstanceCreationExpression(JavaTypeName.BIG_INTEGER,
                                                                             LiteralWrapper.make(literal.toString()),
                                                                             JavaTypeName.STRING);
                proposedIdent = literal.toString();
            } else {
                throw new CodeGenerationException ("Unknown literal type: " + literal.toString());
            }

            suggestedIdent = General.validJavaIdentifier(proposedIdent, false, 32);

            String kernelTypeString = CALToJavaNames.fixupClassName(kernelTypeClass.getName());
            int endPrefix = kernelTypeString.lastIndexOf('_');
            String typeWord;
            if (endPrefix != -1) {
                typeWord = kernelTypeString.substring(endPrefix + 1);
            } else {
                typeWord = kernelTypeString;
            }
            symbol = LITERAL_PREFIX + (sharedValues.getNLiteralValues() + 1) + "_" + typeWord + "_" + getSuggestedIdent();

            unboxedReference = constructorExpression;
            boxedReference = new JavaField.Static(containingClass, getSymbol(), JavaTypeName.make(getKernelTypeClass()));
            if (literal instanceof BigInteger) {
                boxedReference = new JavaField.Static(containingClass, getSymbol(), JavaTypeName.make(getKernelTypeClass()));
                unboxedReference = new JavaField.Static(containingClass, getSymbol()+"$U", JavaTypeName.make(literal.getClass()));
            }
        }

        /**
         * Get the RTData class to handle this literal.
         * @return Class
         */
        private Class<?> getKernelTypeClass() {
            return kernelTypeClass;
        }

        /**
         * Get the suggested String identifier for this literal.
         * @return String
         */
        private String getSuggestedIdent() {
            return suggestedIdent;
        }

        /**
         * Return true if the given literal is equivalent to this literal.
         * @param otherLiteral
         * @return boolean
         */
        public boolean match (Object otherLiteral) {
            return literal.equals(otherLiteral);
        }

        final Object getLiteralObject () {
            return literal;
        }

        /**
         * Return the symbol used to refer to this literal.
         * @return String
         */
        String getSymbol () {
            return symbol;
        }

        /**
         * @return Returns the boxedReference.
         */
        JavaExpression getBoxedReference() {
            boxedUseCount++;
            return boxedReference;
        }
        /**
         * @return Returns the unboxedReference.
         */
        JavaExpression getUnboxedReference() {
            unboxedUseCount++;
            return unboxedReference;
        }

        public void addFieldDeclarations (JavaClassRep classRep) {
            String literalName = getSymbol();
            if (literal instanceof BigInteger) {
                if (unboxedUseCount > 0) {
                    JavaFieldDeclaration unboxedDeclaration = new JavaFieldDeclaration(Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL, JavaTypeName.make(literal.getClass()), literalName + "$U", constructorExpression);
                    classRep.addFieldDeclaration(unboxedDeclaration);
                    if (boxedUseCount > 0) {
                        JavaExpression initializer = SCJavaDefn.createPrimitiveMakeInvocation(getKernelTypeClass(), new JavaField.Static(classRep.getClassName(), literalName + "$U", JavaTypeName.make(literal.getClass())));
                        JavaFieldDeclaration boxedDeclaration = new JavaFieldDeclaration(Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL, JavaTypeName.make(getKernelTypeClass()), literalName, initializer);
                        classRep.addFieldDeclaration(boxedDeclaration);
                    }
                } else
                if (boxedUseCount > 0) {
                    JavaExpression initializer = SCJavaDefn.createPrimitiveMakeInvocation(getKernelTypeClass(), constructorExpression);
                    JavaFieldDeclaration boxedDeclaration = new JavaFieldDeclaration(Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL, JavaTypeName.make(getKernelTypeClass()), literalName, initializer);
                    classRep.addFieldDeclaration(boxedDeclaration);
                }
            } else if (boxedUseCount > 0) {
                // kernel type class: something like: "org.openquark.cal.internal.runtime.lecc.RTData$CAL_Integer";
                JavaExpression initializer = SCJavaDefn.createPrimitiveMakeInvocation(getKernelTypeClass(), constructorExpression);

                JavaFieldDeclaration instanceDeclaration = new JavaFieldDeclaration(Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL, JavaTypeName.make(getKernelTypeClass()), literalName, initializer);
                classRep.addFieldDeclaration(instanceDeclaration);
            }
        }
    }

    /**
     * This class is used to represent a Java expression plus the context statements (eg. variable declarations) needed to define it.
     * For instance, a let defines a java expression after a series of java statements assigning values to the let variables.
     * @author Edward Lam
     */
    private static final class ExpressionContextPair {
        /** The returned expression. */
        private final JavaExpression javaExpression;

        /** The context associated with the expression. */
        private final Block context = new Block();

        /**
         * Constructor for an ExpressionContextPair with no associated context.
         * @param javaExpression the expression
         */
        ExpressionContextPair(JavaExpression javaExpression) {
            this.javaExpression = javaExpression;
        }

        /**
         * Constructor for an ExpressionContextPair.
         * @param javaExpression the expression
         * @param context the associated context.
         */
        ExpressionContextPair(JavaExpression javaExpression, JavaStatement context) {
            this(javaExpression);
            this.context.addStatement(context);
        }

        /**
         * Get the expression
         * @return JavaExpression
         */
        JavaExpression getJavaExpression() {
            return javaExpression;
        }

        /**
         * Get the context
         * @return Block
         */
        Block getContextBlock() {
            return context.getCopy();
        }
    }



    /**
     * Simple wrapper class to encapsulate common info about a method.
     * @author Edward Lam
     */
    private static final class MethodInfo {

        private final String methodName;
        private final JavaTypeName[] paramTypes;
        private final JavaTypeName returnType;
        private final InvocationType invocationType;

        MethodInfo(String methodName, JavaTypeName returnType, InvocationType invocationType) {
            this(methodName, JavaExpression.EMPTY_TYPE_NAME_ARRAY, returnType, invocationType);
        }

        MethodInfo(String methodName, JavaTypeName paramType, JavaTypeName returnType, InvocationType invocationType) {
            this(methodName, new JavaTypeName[]{paramType}, returnType, invocationType);
        }

        MethodInfo(String methodName, JavaTypeName[] paramTypes, JavaTypeName returnType, InvocationType invocationType) {
            Assert.isNotNull(methodName);
            Assert.isNotNull(paramTypes);
            Assert.isNotNull(returnType);
            Assert.isNotNull(invocationType);

            this.methodName = methodName;
            this.paramTypes = paramTypes;
            this.returnType = returnType;
            this.invocationType = invocationType;
        }

        /**
         * @return the method name
         */
        public String getMethodName() {
            return methodName;
        }

        /**
         * @return JavaTypeName[] the parameter types
         */
        public JavaTypeName[] getParamTypes() {
            return paramTypes;
        }

        /**
         * @return JavaTypeName the return type
         */
        public JavaTypeName getReturnType() {
            return returnType;
        }

        /**
         * @return InvocationType the invocation type
         */
        public InvocationType getInvocationType() {
            return invocationType;
        }
    }

    /**
     * ReferencedDCInfo is used to hold information about referenced
     * zero arity data constructors.  It is used to avoid creating duplicate
     * DC instances and to generate/initialize class level fields for the
     * referenced entities.
     * @author rcypher
     */
    static final class ReferencedDCInfo implements Comparable<ReferencedDCInfo> {
        /** The java field which is the reference to the SC/DC */
        private final JavaField javaField;

        /** The DataConstructor if this is a zero arity DC */
        private final DataConstructor dc;

        /** The type of the supercombinator. */
        private final JavaTypeName generatedClassType;

        public  ReferencedDCInfo (JavaField javaField, JavaTypeName generatedClassType, DataConstructor dc) {
            this.javaField = javaField;
            this.generatedClassType = generatedClassType;
            this.dc = dc;
        }
        public JavaField getJField () {
            return javaField;
        }
        public DataConstructor getDC () {
            return dc;
        }

        /** {@inheritDoc} */
        public int compareTo (ReferencedDCInfo o) {
            return javaField.getFieldName().compareTo((o.javaField.getFieldName()));
        }
        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof ReferencedDCInfo)) {
                return false;
            }
            return javaField.getFieldName().equals(((ReferencedDCInfo)o).javaField.getFieldName());
        }
        @Override
        public int hashCode () {
            return javaField.getFieldName().hashCode();
        }

        public JavaTypeName getGeneratedClassTypeName () {
            return generatedClassType;
        }
    }

    /**
     * ReferencedSCInfo is used to hold information about referenced
     * SCs.  It is used to avoid creating duplicate
     * SC instances and to generate/initialize class level fields for the
     * referenced entities.
     * @author rcypher
     */
    static final class ReferencedSCInfo implements Comparable<ReferencedSCInfo> {
        /** The java field which is the reference to the SC/DC */
        private final JavaField javaField;

        /** The type of the supercombinator. */
        private final JavaTypeName generatedClassType;

        /** The name of the supercombinator. */
        private final QualifiedName scName;

        public  ReferencedSCInfo (JavaField javaField,
                                  JavaTypeName generatedClassType,
                                  QualifiedName scName) {
            this.javaField = javaField;
            this.generatedClassType = generatedClassType;
            this.scName = scName;
        }
        public JavaField getJField () {
            return javaField;
        }
        /** {@inheritDoc} */
        public int compareTo (ReferencedSCInfo o) {
            return javaField.getFieldName().compareTo(o.javaField.getFieldName());
        }
        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof ReferencedSCInfo)) {
                return false;
            }

            return javaField.getFieldName().equals(((ReferencedSCInfo)o).javaField.getFieldName());
        }
        @Override
        public int hashCode() {
            return javaField.getFieldName().hashCode();
        }

        public JavaTypeName getGeneratedClassTypeName () {
            return generatedClassType;
        }
        public String getSCName () { return scName.getUnqualifiedName();}
        public QualifiedName getQualifiedSCName () {return scName;}

    }




    /**
     * Information about a variable in the current CAL state.
     * @author RCypher
     */
    private static abstract class VarInfo implements Cloneable {
        /** The java name which currently corresponds to the CAL variable. */
        private final String javaName;

        /** The CAL name. */
        private final QualifiedName calName;

        /** flag indicating that this variable has been evaluated. */
        private boolean evaluated;

        JavaExpression lazyReference;
        JavaExpression strictReference;
        JavaExpression unboxedReference;
        private JavaTypeName unboxedType;
        private final TypeExpr varType;

        VarInfo (QualifiedName calName, String javaName, TypeExpr varType) {
            this.calName = calName;
            this.javaName = javaName;
            this.varType = varType;
        }

        VarInfo (VarInfo otherInfo) {
            this.calName = otherInfo.calName;
            this.evaluated = otherInfo.evaluated;
            this.javaName = otherInfo.javaName;
            this.unboxedType = otherInfo.unboxedType;
            this.varType = otherInfo.varType;

        }

        public String getJavaName () {
            return javaName;
        }
        public QualifiedName getCALName ()  {
            return calName;
        }
        TypeExpr getVarType () {
            return varType;
        }
        /**
         * @return Returns the evaluated.
         */
        public boolean isEvaluated() {
            return evaluated;
        }
        /**
         * @param evaluated The evaluated to set.
         */
        public void setEvaluated(boolean evaluated) {
            this.evaluated = evaluated;
        }


        /**
         * @return Returns the unboxedType.
         */
        JavaTypeName getUnboxedType() {
            return unboxedType;
        }
        /**
         * @param unboxedType The unboxedType to set.
         */
        void setUnboxedType(JavaTypeName unboxedType) {
            this.unboxedType = unboxedType;
        }
        /**
         * @param newUnboxedReference The unboxedReference to set.
         */
        void updateUnboxedReference(JavaExpression newUnboxedReference) {
            this.unboxedReference = newUnboxedReference;
        }

        public static final class Argument extends VarInfo {
            Argument (QualifiedName calName, String javaName, TypeExpr varType) {
                super (calName, javaName, varType);
            }
            private Argument (Argument otherArg) {
                super (otherArg);
                updateLazyReference(otherArg.lazyReference);
                updateStrictReference(otherArg.strictReference);
                updateUnboxedReference(otherArg.unboxedReference);
            }
            @Override
            public Object clone () {
                return new Argument(this);
            }
        }

        private static abstract class LocallyDefinedVar extends VarInfo {

            /** The strict definition of the variable. */
            private JavaExpression strictVarDef;

            /** The lazy definition of the variable. */
            private JavaExpression lazyVarDef;

            /** The java expression which corresponds to the unboxed form of the variable. */
            private JavaExpression unboxedVarDef;


            LocallyDefinedVar (QualifiedName calName, String javaName, TypeExpr varType) {
                super (calName, javaName, varType);
            }


            /**
             * @return Returns the unboxedDef.
             */
            JavaExpression getUnboxedVarDef() {
                return unboxedVarDef;
            }
            /**
             * @param unboxedDef The unboxedDef to set.
             */
            void updateUnboxedVarDef(JavaExpression unboxedDef) {
                this.unboxedVarDef =unboxedDef;
            }
            /**
             * @return Returns the lazyvarDef.
             */
            JavaExpression getLazyVarDef() {
                return lazyVarDef;
            }
            /**
             * @param varDef The varDef to set.
             */
            void updateLazyVarDef(JavaExpression varDef) {
                this.lazyVarDef = varDef;
            }
            /**
             * @return Returns the strictVarDef.
             */
            JavaExpression getStrictVarDef() {
                return strictVarDef;
            }
            /**
             * @param varDef The varDef to set.
             */
            void updateStrictVarDef(JavaExpression varDef) {
                this.strictVarDef = varDef;
            }

        }

        public static final class DCMember extends LocallyDefinedVar {
            DCMember (QualifiedName calName, String javaName, TypeExpr varType) {
                super (calName, javaName, varType);
            }
        }

        public static final class LetNonRec extends LocallyDefinedVar {
            LetNonRec (QualifiedName calName, String javaName, TypeExpr varType) {
                super (calName, javaName, varType);
            }

        }

        public static final class LetRec extends LocallyDefinedVar {
            LetRec (QualifiedName calName, String javaName, TypeExpr varType) {
                super (calName, javaName, varType);
            }
        }

        public static final class RecordField extends VarInfo {
            RecordField (QualifiedName calName, String javaName, TypeExpr varType) {
                super (calName, javaName, varType);
            }
            private RecordField (RecordField other) {
                super (other);
                updateLazyReference(other.lazyReference);
                updateStrictReference(other.strictReference);
                updateUnboxedReference(other.unboxedReference);
            }
            @Override
            public final Object clone () {
                return new RecordField (this);
            }
        }

        /**
         * @param newStrictReference The strict reference to set.
         */
        void updateStrictReference(JavaExpression newStrictReference) {
            this.strictReference = newStrictReference;
        }
        /**
         * @param newLazyReference The lazy reference to set.
         */
        void updateLazyReference(JavaExpression newLazyReference) {
            this.lazyReference = newLazyReference;
        }
   }

    /**
     * A simple container class which holds the name of an alt variable and its index with respect to
     *   a data constructor's arguments.
     * @author Edward Lam
     */
    private static final class AltVarIndexPair implements Comparable<AltVarIndexPair> {
        private final int index;
        private final String altVar;

        AltVarIndexPair(String altVar, int index) {
            this.index = index;
            this.altVar = altVar;
        }

        public String getAltVar() {
            return this.altVar;
        }


        public int getIndex() {
            return this.index;
        }

        /** {@inheritDoc} */
        public int compareTo (AltVarIndexPair o) {
            return index - o.index;
        }
        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof AltVarIndexPair)) {
                return false;
            }

            return index == ((AltVarIndexPair)o).index;
        }
        @Override
        public int hashCode () {
            return index;
        }
    }

    /**
     * Internal class to represent the three levels of compilation schemes.
     * These schemes are used to determine the strictness of evaluation.
     * @author rcypher
     */
    static class Scheme extends Object {
        // Top level.
        static final Scheme R_SCHEME = new Scheme("R_Scheme");

        // Strict.
        static final Scheme E_SCHEME = new Scheme("E_Scheme");

        // Lazy.
        static final Scheme C_SCHEME = new Scheme("C_Scheme");

        // Unbox for internal use. This is a kind of E scheme.
        static final Scheme UNBOX_INTERNAL_SCHEME = new Scheme("Unbox_Internal_Scheme");

        // Unbox for foreign use. This is a kind of E scheme.
        static final Scheme UNBOX_FOREIGN_SCHEME = new Scheme("Unbox_Foreign_Scheme");


        private final String description;
        private Scheme (String description) {this.description = description;}

        @Override
        public String toString () {
            return description;
        }
    }

    static final class LiftedExpression {
        private final String[] argNames;
        private final JavaTypeName typeName;
        private final Expression e;
        private final String name;
        private final String containingFunction;

        LiftedExpression (String name, String[] argNames, Expression e, JavaTypeName typeName, String containingFunction) {
            if (name == null || argNames == null || e == null || typeName == null) {
                throw new NullPointerException ("Bad value creating: " + getClass().getName());
            }
            this.name = name;
            this.argNames = argNames;
            this.typeName = typeName;
            this.e = e;
            this.containingFunction = containingFunction;
        }

        Expression getExpression () {return e;}
        JavaTypeName getTypeName () {return typeName;}
        String getName () {return name;}
        String[] getArgNames() {return argNames;}
        int getArity () {return argNames.length;}
        String getContainingFunction () {return containingFunction;}
    }

    /**
     * @param dc
     * @return true for the data constructors Prelude.True or Prelude.False.
     */
    private static boolean isTrueOrFalseDataCons(DataConstructor dc) {
        QualifiedName qn = dc.getName();
        return qn.equals(CAL_Prelude.DataConstructors.True) || qn.equals(CAL_Prelude.DataConstructors.False);
    }

    /**
     * @param dc
     * @return true for the data constructor Prelude.True.
     */
    private static boolean isTrueDataCons(DataConstructor dc) {
        return dc.getName().equals(CAL_Prelude.DataConstructors.True);
    }

    /**
     * Determine if the given data constructor will be generated as a TagDC class.
     * i.e. is the data constructor zero arity and from a data type with more
     * than one zero arity data constructor.
     * @param dc
     * @param module
     * @return true if the data constructor is a TagDC
     */
    static boolean isTagDC (DataConstructor dc, LECCModule module) {
        TypeConstructor typeCons = dc.getTypeConstructor();

        int nTagDCs = 0;
        for (int i = 0; i < typeCons.getNDataConstructors(); ++i) {
            DataConstructor dci = typeCons.getNthDataConstructor(i);
            if (dci.getArity() == 0) {
                nTagDCs++;
            }
        }
        return nTagDCs > 1;
    }

    /**
     * Returns the Class object corresponding to the specified foreign type info. If the Class object could not be resolved,
     * a CodeGenerationException is thrown.
     * @param foreignTypeInfo the foreign type info.
     * @return the Class object corresponding to the foreign type info.
     * @throws CodeGenerationException if the Class object could not be resolved.
     */
    private static Class<?> getForeignType(final ForeignTypeInfo foreignTypeInfo) throws CodeGenerationException {
        try {
            return foreignTypeInfo.getForeignType();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign type.", e);
        }
    }

    /** The fully-qualified name of the CalValue class.*/
    private static final String CalValueClassName = CalValue.class.getName();
    /**
     * Determine whether a given class is the CalValue class.
     * We cannot simply check for object equality since the CalValue class we check against may not
     * necessarily be the CalValue class loaded by this classloader.
     * For instance, in Eclipse, the Module's foreign classloader (for this project in the Eclipse workspace)
     * will be different from the classloader used to load the platform plugin.
     *
     * @param clazz a class object
     * @return whether the given class is CalValue.class.
     */
    private static boolean isCalValueClass(Class<?> clazz) {
        return CalValueClassName.equals(clazz.getName());
    }

    /**
     * Returns the Java type T in the expression "expr instanceof T", as represented by the specified foreign function info.
     * If the Class object could not be resolved, a CodeGenerationException is thrown.
     * @param foreignFunctionInfo the foreign function info.
     * @return the Java type T in the expression "expr instanceof T", as represented by the foreign function info. Will not be null.
     * @throws CodeGenerationException if the Class object could not be resolved.
     */
    private static Class<?> getInstanceOfType(final ForeignFunctionInfo.InstanceOf foreignFunctionInfo) throws CodeGenerationException {
        try {
            return foreignFunctionInfo.getInstanceOfType();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign type for foreign instanceof function.", e);
        }
    }

    /**
     * Returns the class from which to invoke a method or field (both static and non-static) which cannot be null,
     * unless this is a constructor invocation.
     *
     * It is sometimes necessary to invoke a method/field from a class other than which it was defined.
     * For example, if package scope class A defines a static public field f, and public class B extends A,
     * then B.f in a different package will not result in a compilation error but A.f will.
     *
     * Or for example, if package scope class A defines a non-static public method m, and public class B extends A,
     * then in a different package we cannot invoke m on an object of type B if:
     * - the invocation is done via reflection, or
     * - the reference is first cast to the method's declared type, in this case A, i.e. ((A)b).m()
     *
     * @param foreignFunctionInfo the foreign function info.
     * @return the class from which to invoke a method or field (both static and non-static) which cannot be null,
     *         unless this is a constructor invocation.
     * @throws CodeGenerationException if the AccessibleObject could not be resolved.
     */
    private static Class<?> getInvocationClass(final ForeignFunctionInfo.Invocation foreignFunctionInfo) throws CodeGenerationException {
        try {
            return foreignFunctionInfo.getInvocationClass();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign type containing foreign method, field, or constructor.", e);
        }
    }

    /**
     * Returns the Java class corresponding to the argN argument in the specified foreign function info. If the Class object could not be resolved,
     * a CodeGenerationException is thrown.
     * @param foreignFunctionInfo the foreign function info.
     * @param argN a zero-based argument index.
     * @return the Java class corresponding to the argN argument in the foreign function info.
     * @throws CodeGenerationException if the Class object could not be resolved.
     */
    private static Class<?> getJavaArgumentType(final ForeignFunctionInfo foreignFunctionInfo, int argN) throws CodeGenerationException {
        try {
            return foreignFunctionInfo.getJavaArgumentType(argN);
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign argument type for foreign function.", e);
        }
    }

    /**
     * Returns the field, method or constructor corresponding to the specified foreign function info. If the AccessibleObject could not be resolved,
     * a CodeGenerationException is thrown.
     * @param foreignFunctionInfo the foreign function info.
     * @return the field, method or constructor corresponding to the foreign function info.
     * @throws CodeGenerationException if the AccessibleObject could not be resolved.
     */
    private static AccessibleObject getJavaProxy(final ForeignFunctionInfo.Invocation foreignFunctionInfo) throws CodeGenerationException {
        try {
            return foreignFunctionInfo.getJavaProxy();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign method, field, or constructor.", e);
        }
    }

    /**
     * Returns the return type of the foreign entity corresponding to the specified foreign function info as a Java class. If the Class object could not be resolved,
     * a CodeGenerationException is thrown.
     * @param foreignFunctionInfo the foreign function info.
     * @return the return type of the foreign entity corresponding to the foreign function info as a Java class.
     * @throws CodeGenerationException if the Class object could not be resolved.
     */
    private static Class<?> getJavaReturnType(final ForeignFunctionInfo foreignFunctionInfo) throws CodeGenerationException {
        try {
            return foreignFunctionInfo.getJavaReturnType();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign return type for foreign function.", e);
        }
    }

    /**
     * Returns the number of arguments of the CAL foreign function corresponding to the specified foreign function info.
     * If the field/method/constructor could not be resolved, a CodeGenerationException is thrown.
     * @param foreignFunctionInfo the foreign function info.
     * @return the number of arguments of the CAL foreign function corresponding to the foreign function info.
     * @throws CodeGenerationException if the field/method/constructor could not be resolved, a CodeGenerationException is thrown.
     */
    private static int getNArguments(final ForeignFunctionInfo foreignFunctionInfo) throws CodeGenerationException {
        try {
            return foreignFunctionInfo.getNArguments();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign method, field, or constructor.", e);
        }
    }

    /**
     * Returns the Class object corresponding to the cast type in a cast expression. If the Class object could not be resolved,
     * a CodeGenerationException is thrown.
     * @param castExpression the cast expression.
     * @return the Class object corresponding to the cast type in a cast expression.
     * @throws CodeGenerationException if the Class object could not be resolved.
     */
    private static Class<?> getCastType(final Expression.Cast castExpression) throws CodeGenerationException {
        try {
            return castExpression.getCastType();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign type for Expression.Cast.", e);
        }
    }

    /**
     * Returns the Class object corresponding to the referent type in a class literal expression. If the Class object could not be resolved,
     * a CodeGenerationException is thrown.
     * @param foreignFunctionInfo the foreign function info.
     * @return the referent type, i.e. the Java type R where this literal corresponds to R.class.
     * @throws CodeGenerationException if the Class object could not be resolved.
     */
    private static Class<?> getReferentType(final ForeignFunctionInfo.ClassLiteral foreignFunctionInfo) throws CodeGenerationException {
        try {
            return foreignFunctionInfo.getReferentType();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign class literal.", e);
        }
    }

    /**
     * Traverse the JavaModel instance and build up a map associating the names of
     * declared locals to their Java type.
     * @author Raymond Cypher
     *
     */
    private static final class DeclaredLocalsFinder extends JavaModelTraverser<Map<String, JavaTypeName>, Void>{
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitLocalVariableDeclarationStatement(org.openquark.cal.internal.machine.lecc.JavaStatement.LocalVariableDeclaration, java.lang.Object)
         */
        public Void visitLocalVariableDeclarationStatement(
                final LocalVariableDeclaration localVariableDeclaration, final Map<String, JavaTypeName> arg) {

            JavaTypeName variableType = localVariableDeclaration.getLocalVariable().getTypeName();
            if (variableType.isObjectReference() &&
                (variableType.equals(JavaTypeNames.RTVALUE) ||
                 variableType.equals(JavaTypeNames.RTRECORD_VALUE) ||
                 variableType.equals(JavaTypeNames.RTCONS) ||
                 variableType.equals(JavaTypeNames.RTINDIRECTION) ||
                 localVariableDeclaration.getLocalVariable().getName().startsWith("$case"))) {
                arg.put(localVariableDeclaration.getLocalVariable().getName(), variableType);
            }

            return null;
        }

    }

    /**
     * This class transforms an instance of the JavaModel.
     * The transformation is applied to return statements.
     * Return statements are modified to return an unboxed value.
     * @author rcypher
     */
    final class UnboxedReturnCopier extends JavaModelCopier<Void> {
        private JavaTypeName resultTypeName;

        UnboxedReturnCopier (JavaTypeName resultType) {
            this.resultTypeName = resultType;
        }

        private JavaExpression handleDataInstance(JavaExpression returnValue) {

            if (returnValue instanceof MethodInvocation.Instance) {
                MethodInvocation.Instance mii = (MethodInvocation.Instance)returnValue;
                if (mii.getMethodName().equals("getValue") && mii.getNArgs() == 0) {
                    returnValue = mii.getInvocationTarget();
                }
            }

            if (returnValue instanceof MethodInvocation.Static) {
                MethodInvocation.Static mis = (MethodInvocation.Static)returnValue;

                // If the return value is a call to create a boxed value we're OK
                // since this means there is an unboxed value to return.
                if (mis.getMethodName().equals("make")) {
                    // Check to see if the static type is one of the RTData sub-classes.
                    JavaTypeName staticClass = mis.getInvocationClass();
                    if (
                        (staticClass.equals(JavaTypeNames.RTDATA_BOOLEAN) &&
                         resultTypeName.equals(JavaTypeName.BOOLEAN)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_BYTE) &&
                         resultTypeName.equals(JavaTypeName.BYTE)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_CHAR) &&
                         resultTypeName.equals(JavaTypeName.CHAR)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_DOUBLE) &&
                         resultTypeName.equals(JavaTypeName.DOUBLE)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_FLOAT) &&
                         resultTypeName.equals(JavaTypeName.FLOAT)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_INT) &&
                         resultTypeName.equals(JavaTypeName.INT)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_INTEGER) &&
                         resultTypeName.equals(JavaTypeName.BIG_INTEGER)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_LONG) &&
                         resultTypeName.equals(JavaTypeName.LONG)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_SHORT) &&
                         resultTypeName.equals(JavaTypeName.SHORT)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_STRING) &&
                         resultTypeName.equals(JavaTypeName.STRING)) ||
                        (staticClass.equals(JavaTypeNames.RTDATA_OPAQUE) &&
                         resultTypeName.equals(JavaTypeName.OBJECT))) {

                        // The first argument to make() will be the unboxed value we
                        // want to return.
                        return (JavaExpression)mis.getArg(0).accept(this, null);
                    }

                    // If we're creating an instance of CAL_Opaque we're dealing with an
                    // Object.  We just do a cast and return.
                    if (staticClass.equals(JavaTypeNames.RTDATA_OPAQUE) &&
                        !(resultTypeName instanceof JavaTypeName.Primitive)) {
                        return new CastExpression(resultTypeName, (JavaExpression)mis.getArg(0).accept(this, null));
                    }

                }
            }

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitReturnStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.ReturnStatement, java.lang.Object)
         */
        @Override
        public JavaStatement visitReturnStatement(ReturnStatement returnStatement,
                Void arg) {

            JavaExpression returnValue = returnStatement.getReturnExpression();

            // First we want to get rid of any indirections.
            while (returnValue instanceof JavaExpression.PlaceHolder) {
                returnValue = ((JavaExpression.PlaceHolder)returnValue).getActualExpression();
            }

            // Check for a static invocation.
            if (returnValue instanceof MethodInvocation) {

                MethodInvocation mis = (MethodInvocation)returnValue;
                boolean isStatic = mis instanceof MethodInvocation.Static;
                final JavaTypeName invocationClass;
                if (isStatic) {
                    invocationClass = ((MethodInvocation.Static)mis).getInvocationClass();
                } else {
                    invocationClass = JavaTypeNames.RTVALUE;
                }


                // If this return value is a call to one of the error functions
                // we can treat it as returning an unboxed value since we know it
                // will actually throw an exception.  So we check if the returnValue
                // is an invocation of RTValue.badValue or RTValue.errorCall
                if (invocationClass.equals(JavaTypeNames.RTVALUE) &&
                    (mis.getMethodName().equals("errorCall") ||
                     mis.getMethodName().equals("badValue") ||
                     mis.getMethodName().equals("badSwitchIndex"))) {
                    // We need to create new MethodInvocation instances which will
                    // invoke the type specific version of 'errorCall'.
                    JavaTypeName[] argTypes;
                    JavaExpression[] argValues;

                    if (mis.getMethodName().equals("badSwitchIndex")) {
                        argTypes = new JavaTypeName[]{JavaTypeName.ERRORINFO};
                        argValues = new JavaExpression[]{mis.getArg(0)};
                    } else {
                        argTypes = new JavaTypeName[]{JavaTypeName.ERRORINFO, JavaTypeName.STRING};
                        argValues = new JavaExpression[2];
                        if (mis.getNArgs() == 1) {
                            argValues[0] = LiteralWrapper.NULL;
                            argValues[1] = mis.getArg(0);
                        } else {
                            argValues[0] = mis.getArg(0);
                            argValues[1] = mis.getArg(1);
                        }
                    }

                    boolean cast = false;
                    JavaTypeName callReturnType = resultTypeName;
                    String staticFunctionName = mis.getMethodName();
                    if (resultTypeName.equals(JavaTypeName.BOOLEAN)) {
                        staticFunctionName = staticFunctionName + "_boolean";
                    } else if (resultTypeName.equals(JavaTypeName.BYTE)) {
                        staticFunctionName = staticFunctionName + "_byte";
                    } else if (resultTypeName.equals(JavaTypeName.CHAR)) {
                        staticFunctionName = staticFunctionName + "_char";
                    } else if (resultTypeName.equals(JavaTypeName.DOUBLE)) {
                        staticFunctionName = staticFunctionName + "_double";
                    } else if (resultTypeName.equals(JavaTypeName.FLOAT)) {
                        staticFunctionName = staticFunctionName + "_float";
                    } else if (resultTypeName.equals(JavaTypeName.INT)) {
                        staticFunctionName = staticFunctionName + "_int";
                    } else if (resultTypeName.equals(JavaTypeName.LONG)) {
                        staticFunctionName = staticFunctionName + "_long";
                    } else if (resultTypeName.equals(JavaTypeName.SHORT)) {
                        staticFunctionName = staticFunctionName + "_short";
                    } else {
                        callReturnType = JavaTypeName.OBJECT;
                        staticFunctionName = staticFunctionName + "_Object";
                        if (!resultTypeName.equals(JavaTypeName.OBJECT)) {
                            cast = true;
                        }
                    }

                    JavaExpression newReturnValue;
                    if (isStatic) {
                        newReturnValue =
                            new MethodInvocation.Static(JavaTypeNames.RTVALUE, staticFunctionName, argValues, argTypes, callReturnType);
                    } else {
                        newReturnValue =
                            new MethodInvocation.Instance(null, staticFunctionName, argValues, argTypes, callReturnType, MethodInvocation.InvocationType.VIRTUAL);
                    }

                    if (cast) {
                        newReturnValue = new JavaExpression.CastExpression(resultTypeName, newReturnValue);
                    }

                    return new ReturnStatement (newReturnValue);
                }

            }

            JavaExpression dataInstanceValue = handleDataInstance(returnValue);
            if (dataInstanceValue != null) {
                return new ReturnStatement(dataInstanceValue);
            }

            // See if we're returning a cached literal value.
            //For example, this applies in the case of Prelude.signumInt and signumLong. We want the unboxed versions
            //to return the literal constant values instead of evaluating and unboxing the RTValues.
            if (returnValue instanceof JavaExpression.JavaField.Static) {

                final JavaField.Static field = (JavaField.Static)returnValue;
                final String fieldName = field.getFieldName();

                if (fieldName.startsWith(SCJavaDefn.LITERAL_PREFIX)) {

                    for (final KernelLiteral kl : sharedValues.getLiteralValues()) {

                        //Prelude.Integer (i.e. java.lang.BigInteger) values are not handled here, since they do not
                        //have a literal unboxed representation in Java.
                        if (kl.getSymbol().equals(fieldName) && !kl.kernelTypeClass.equals(CAL_Integer.class)) {
                            return new ReturnStatement(LiteralWrapper.make(kl.getLiteralObject()));
                        }
                    }
                }
            }


            // We want to invoke evaluate on the return value and then unbox.
            JavaExpression newReturnValue = (JavaExpression)returnValue.accept(this, arg);

            if (!(newReturnValue instanceof JavaExpression.MethodInvocation.Instance) ||
                !((JavaExpression.MethodInvocation)newReturnValue).getMethodName().equals("evaluate")) {

                // If all arguments and locals are not self referential types we can continue.
                boolean problematicType = false;
                for (int i = 0, n = getArity(); i < n; ++i) {
                    TypeExpr argType = getArgumentType(i);
                    if (argType != null) {
                        if (argType instanceof TypeVar) {
                            problematicType = true;
                            break;
                        } else if (argType instanceof TypeConsApp) {
                            if (SCJavaDefn.isSelfReferentialDataType(argType)) {
                                problematicType = true;
                                break;
                            }
                        }
                    }
                }

                if (!problematicType) {
                    // Check local variables.
                    Set<VarInfo> localVars = returnStatementToLocalVars.get(returnStatement);
                    if (localVars == null) {
                        problematicType = true;
                    } else {
                        for (final VarInfo vi : localVars) {
                            TypeExpr argType = vi.getVarType();
                            if (argType != null) {
                                if (argType instanceof TypeVar) {
                                    problematicType = true;
                                    break;
                                } else if (argType instanceof TypeConsApp) {
                                    if (SCJavaDefn.isSelfReferentialDataType(argType)) {
                                        problematicType = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (problematicType) {
                    throw new UnboxingTransformationError("Unable to transform function body.");
                }

                newReturnValue = createInvocation(newReturnValue, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
            }

            newReturnValue = SCJavaDefn.unboxValue(resultTypeName, newReturnValue);

            return new ReturnStatement(newReturnValue);
        }
    }

    static class SharedValues {
        private final Map<Object, KernelLiteral> literalObjectToKernelLiteralMap = new LinkedHashMap<Object, KernelLiteral>();
        Set<ReferencedDCInfo> referencedDCs = new TreeSet<ReferencedDCInfo>();
        Map<String, JavaExpression> staticErrorInfo = new TreeMap<String, JavaExpression>();

        KernelLiteral addKernelLiteral (Object literalValue, JavaTypeName containingClass) throws CodeGenerationException {
            KernelLiteral kl = literalObjectToKernelLiteralMap.get(literalValue);
            if (kl == null) {
                kl = new KernelLiteral (literalValue, this, containingClass);
                literalObjectToKernelLiteralMap.put (literalValue, kl);
            }
            return kl;
        }

        KernelLiteral getKernelLiteral (Object literalValue) {
            return literalObjectToKernelLiteralMap.get(literalValue);
        }

        Collection<KernelLiteral> getLiteralValues () {
            return literalObjectToKernelLiteralMap.values();
        }

        int getNLiteralValues () {
            return literalObjectToKernelLiteralMap.size();
        }

        Set<ReferencedDCInfo> getReferencedDCs () {
            return referencedDCs;
        }
        int getNReferencedDCs () {
            return referencedDCs.size();
        }

        void add (ReferencedDCInfo referencedDC) {
            referencedDCs.add(referencedDC);
        }

        JavaExpression getStaticError (String errorVarName) {
            return staticErrorInfo.get(errorVarName);
        }

        void addStaticError (String errorVarname, JavaExpression errorInfo) {
            staticErrorInfo.put (errorVarname, errorInfo);
        }

        Set<String> getStaticErrorInfoNames () {
            return staticErrorInfo.keySet();
        }

        int getNStaticErrorInfo () {
            return staticErrorInfo.size();
        }


    }


    /**
     * UnboxingTransformationException
     * Class for raising exceptions during the process of transforming
     * a function body to return an unboxed value.  When thrown this exception
     * indicates that a valid transformation does not exist.
     * @author rcypher
     * Created: March 19, 2007
     */
    static class UnboxingTransformationError extends Error {

        private static final long serialVersionUID = -6310507883559829154L;

        /**
         * @param message
         */
        public UnboxingTransformationError(String message) {
            super(message);
        }

        /**
         * @param message
         * @param cause
         */
        public UnboxingTransformationError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * This class performs a transformation on a Java Model instance which replaces the
     * last reference to local variables or arguments with a call to RTValue.lastRef().
     * This serves to release the local reference, allowing the garbage collector to
     * potentially recover memory earlier than it would have otherwise.
     * This is primarily intended to improve the space usage behavior in cases where the
     * generated Java would otherwise hold a reference to the head of data structure.
     * For example:
     * foo :: [Maybe Double] -> String;
     * foo x =
     *     case (List.last x) of
     *         Just {value} -> "True";
     *         Nothing -> "False";
     *         ;
     * Would generate Java code like:
     * public final RTValue f1S(RTValue x, RTExecutionContext $ec) throws CALExecutorException {
     *     // Top level supercombinator logic
     *     TYPE_Maybe $case1 =
     *         (((TYPE_Maybe)(java.lang.Object)Last.$instance.f1S(x.evaluate($ec), $ec).evaluate($ec)));
     *
     *     switch ($case1.getOrdinalValue()) {
     *       case 0: return "False";
     *       case 1: return "True";
     *     }
     *  }
     *
     * In this scenario evaluation of 'List.last x' would normally cause the list to be fully expanded in
     * memory, because the 'x' argument of f1S is holding a reference to the head of the list.  Since the list
     * isn't referenced anywhere else in f1S and the behavior of List.last doesn't hold onto the already
     * traversed portion of the list it would be nice to avoid holding the whole list in memory.
     * If the line containing the last reference to 'x' is changed to:
     *     (((TYPE_Maybe)(java.lang.Object)Last.$instance.f1S(RTValue.lastRef(x.evaluate($ec), x = null), $ec).evaluate($ec)));
     * The effect is to release the local reference to 'x' before the evaluation of List.last, which allows the garbage
     * collector to recover the traversed portion of the list.  This, of course, assumes that there is not
     * some other reference top the head of the list still held elsewhere.
     *
     * NOTE:  This transformation takes advantage of the fact that in the generated code for a function body
     * branches of execution never converge.  i.e. the path of execution may branch due to switch or
     * if-then-else but these branches will always ultimately terminate in a 'return' rather than
     * popping up a level in scope.  If the code generation changes so that this is no longer true
     * this transformation will need to be updated.
     * @author Raymond Cypher
     *
     */
    private static final class VarReleaser extends JavaModelCopier<Void> {

        /**
         * An array of JavaTypeName of size two.  Both members are initialized to JavaTypeNames.RTVALUE.
         * This field is used when specifying argument types when creating a method call instance.
         */
        private static final JavaTypeName[] TWO_RTVALUES = new JavaTypeName[]{JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE};

        /**
         * The names of the variables to be released, mapped to the corresponding Java type.
         */
        private final Map<String, JavaTypeName> variablesOfInterest;

        VarReleaser(Map<String, JavaTypeName> variablesOfInterest) {
            this.variablesOfInterest = variablesOfInterest;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitBinaryOperatorExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.OperatorExpression.Binary, java.lang.Object)
         */
        public JavaExpression visitBinaryOperatorExpression(Binary binaryOperator,
                Void arg) {

            // Visit the second argument first, as we want to release the last reference to each
            // variable.
            JavaExpression arg1 = (JavaExpression)binaryOperator.getArgument(1).accept(this, arg);
            JavaExpression arg0 = (JavaExpression)binaryOperator.getArgument(0).accept(this, arg);
            return new Binary (
                    binaryOperator.getJavaOperator(),
                    arg0,
                    arg1);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitAssignmentExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.Assignment, java.lang.Object)
         */
        public JavaExpression visitAssignmentExpression(final Assignment assignment, final Void arg) {

            // The 'lastRef' transformation should not be applied to the left hand side of the
            // assignment.
            return new Assignment (
                    (Nameable)assignment.getLeftHandSide().accept(new JavaModelCopier<Void>(), arg),
                    (JavaExpression)assignment.getValue().accept(this, arg));
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitLocalVariableDeclarationStatement(org.openquark.cal.internal.machine.lecc.JavaStatement.LocalVariableDeclaration, java.lang.Object)
         */
        public JavaStatement visitLocalVariableDeclarationStatement(
                LocalVariableDeclaration localVariableDeclaration, Void arg) {

            // The 'lastRef' transformation should not be applied to the left hand side of
            // the declaration.
            LocalVariable assignTo = (LocalVariable)localVariableDeclaration.getLocalVariable().accept(new JavaModelCopier<Void>(), null);

            if (localVariableDeclaration.getInitializer() != null) {
                LocalVariableDeclaration newDeclaration =
                    new LocalVariableDeclaration (
                        assignTo,
                        (JavaExpression)localVariableDeclaration.getInitializer().accept(this, arg),
                        localVariableDeclaration.isFinal());

                return newDeclaration;

            } else {
                LocalVariableDeclaration newDeclaration =
                    new LocalVariableDeclaration (
                        assignTo,
                        null,
                        localVariableDeclaration.isFinal());

                return newDeclaration;
            }
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitArrayCreationExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.ArrayCreationExpression, java.lang.Object)
         */
        public JavaExpression visitArrayCreationExpression(
                ArrayCreationExpression arrayCreation, Void arg) {

            // Visit the element values in reverse order so that the last reference to
            // the variable is released.
            JavaExpression[] elementValues = new JavaExpression[arrayCreation.getNElementValues()];
            for (int i = elementValues.length - 1; i >= 0; --i) {
                elementValues[i] = (JavaExpression)arrayCreation.getElementValue(i).accept(this, arg);
            }

            return new ArrayCreationExpression(arrayCreation.getArrayElementTypeName(), elementValues);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitClassInstanceCreationExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.ClassInstanceCreationExpression, java.lang.Object)
         */
        public JavaExpression visitClassInstanceCreationExpression(
                ClassInstanceCreationExpression instanceCreation, Void arg) {

            // Visit the arguments to this constructor call in reverse order so that
            // the last reference to any variables is released.
            JavaTypeName[] argTypes = new JavaTypeName[instanceCreation.getNArgs()];
            JavaExpression[] argValues = new JavaExpression[instanceCreation.getNArgs()];
            for (int i = argTypes.length - 1; i >= 0; --i) {
                argTypes[i] = instanceCreation.getParamType(i);
                argValues[i] = (JavaExpression)instanceCreation.getArg(i).accept(this, arg);
            }

            return new ClassInstanceCreationExpression(instanceCreation.getClassName(), argValues, argTypes);
        }


        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitAssertStatement(org.openquark.cal.internal.machine.lecc.JavaStatement.AssertStatement, java.lang.Object)
         */
        public JavaStatement visitAssertStatement(final AssertStatement assertStatement,
                final Void arg) {

            // Visit the 'on failure' expression before the condition expr so
            // the last reference to a variable is released.
            if (assertStatement.getOnFailureExpr() != null) {
                assertStatement.getOnFailureExpr().accept(this, arg);
            }

            assertStatement.getConditionExpr().accept(this, arg);

            return null;
        }
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitTernaryOperatorExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.OperatorExpression.Ternary, java.lang.Object)
         */
        public JavaExpression visitTernaryOperatorExpression(Ternary ternaryOperator,
                Void arg) {

            // The branches of the conditional need to be handled separately, since they are mutually
            // exclusive.
            Map<String, JavaTypeName> variablesOfInterestBranch1 = new HashMap<String, JavaTypeName>(variablesOfInterest);
            VarReleaser vr = new VarReleaser(variablesOfInterestBranch1);
            JavaExpression arg1 = (JavaExpression)ternaryOperator.getArgument(1).accept(vr, arg);

            Map<String, JavaTypeName> variablesOfInterestBranch2 = new HashMap<String, JavaTypeName>(variablesOfInterest);
            vr = new VarReleaser(variablesOfInterestBranch2);
            JavaExpression arg2 = (JavaExpression)ternaryOperator.getArgument(2).accept(vr, arg);

            // Update the variables of interest.
            // If a variable was released in either branch it cannot be relesed previous
            // to the branching.  i.e. the variables of interest are the union of the
            // variables still in play from each branch.
            variablesOfInterest.clear();
            for(String varName : variablesOfInterestBranch1.keySet()) {
                if (variablesOfInterestBranch2.containsKey(varName)) {
                    variablesOfInterest.put(varName, variablesOfInterestBranch1.get(varName));
                }
            }

            JavaExpression arg0 = (JavaExpression)ternaryOperator.getArgument(0).accept(this, arg);
            return new Ternary (
                    arg0,
                    arg1,
                    arg2);
        }

        /**
         *
         * @param mi
         * @return true if the method invocation is a call to 'evaluate' on a local.
         */
        private final JavaExpression.Nameable isEvaluateInvocation (MethodInvocation.Instance mi) {
            if (mi.getMethodName().equals("evaluate")) {
                JavaExpression target = mi.getInvocationTarget();

                if (target != null) {
                    if (target instanceof LocalName) {
                        return (LocalName)target;
                    } else if (target instanceof LocalVariable) {
                        return (LocalVariable)target;
                    } else if (target instanceof MethodVariable) {
                        return (MethodVariable)target;
                    }
                }
            }
            return null;
        }

        /**
         *
         * @param varName
         * @return true if the named variable is of interest and has not been released.
         */
        private boolean shouldRelease(String varName) {
            return variablesOfInterest.containsKey(varName);
        }

        /**
         * Generates a call to RTValue.lastRef where the
         * second argument is nulled out.
         * @param keep - the first argument to lastRef
         * @param nullOut - the reference to be assigned null and passed as the second argument to lastRef
         * @param varType
         * @return a call to RTValue.lastRef
         */
        private JavaExpression callLastRef(JavaExpression keep, JavaExpression.Nameable nullOut, JavaTypeName varType) {

            JavaExpression release =
                new MethodInvocation.Static(
                        JavaTypeNames.RTVALUE,
                        "lastRef",
                        new JavaExpression[]{keep, new Assignment(nullOut, LiteralWrapper.NULL)},
                        TWO_RTVALUES,
                        JavaTypeNames.RTVALUE);

            // If the var type is not RTValue cast the result of the call to lastRef.
            if (!varType.equals(JavaTypeNames.RTVALUE)) {
                release = new JavaExpression.CastExpression(varType, release);
            }

            return release;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitLocalNameExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.LocalName, java.lang.Object)
         */
        public JavaExpression visitLocalNameExpression(LocalName localName, Void arg) {
            // If we've gotten to this point the LocalName may be a reference that needs to be nulled out.
            if (shouldRelease(localName.getName())) {
                JavaTypeName varType = variablesOfInterest.get(localName.getName());
                variablesOfInterest.remove(localName.getName());
                return callLastRef (localName, localName, varType);
            } else {
                return super.visitLocalNameExpression(localName, arg);
            }
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitLocalVariableExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.LocalVariable, java.lang.Object)
         */
        public JavaExpression visitLocalVariableExpression(LocalVariable localVariable,
                Void arg) {
            // If we've gotten to this point the LocalVariable may be a reference that needs to be nulled out.
            if (shouldRelease(localVariable.getName())) {
                JavaTypeName varType = variablesOfInterest.get(localVariable.getName());
                variablesOfInterest.remove(localVariable.getName());
                return callLastRef (localVariable, localVariable, varType);
            } else {
                return super.visitLocalVariableExpression(localVariable, arg);
            }
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitMethodVariableExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.MethodVariable, java.lang.Object)
         */
        public JavaExpression visitMethodVariableExpression(MethodVariable methodVariable,
                Void arg) {
            // If we've gotten to this point the LocalVariable may be a reference that needs to be nulled out.
            if (shouldRelease(methodVariable.getName())) {
                JavaTypeName varType = variablesOfInterest.get(methodVariable.getName());
                variablesOfInterest.remove(methodVariable.getName());
                return callLastRef (methodVariable, methodVariable, varType);
            } else {
                return super.visitMethodVariableExpression(methodVariable, arg);
            }
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitInstanceMethodInvocationExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.MethodInvocation.Instance, java.lang.Object)
         */
        public JavaExpression visitInstanceMethodInvocationExpression(
                MethodInvocation.Instance instanceInvocation, Void arg) {

            JavaTypeName[] argTypes = new JavaTypeName[instanceInvocation.getNArgs()];
            JavaExpression[] argValues = new JavaExpression[instanceInvocation.getNArgs()];

            // We want to access the method arguments in reverse order
            // as we want to apply the transformation to the rightmost
            // reference to a variable.
            for (int i = argTypes.length - 1; i >= 0; --i) {
                argTypes[i] = instanceInvocation.getParamType(i);
                argValues[i] = (JavaExpression)instanceInvocation.getArg(i).accept(this, arg);
            }


            // If the invocation is of the form 'x.evaluate(...)', x is a local variable,
            // and this is the last reference to x we want to produce:
            // RTValue.lastRef(x.evaluate(...), x = null)
            // instead of:
            // RTValue.lastRef(x, x = null).evaluate(...);
            JavaExpression.Nameable evaluateTarget = isEvaluateInvocation(instanceInvocation);
            if (evaluateTarget != null ) {
                JavaExpression target = null;
                if (instanceInvocation.getInvocationTarget() != null) {
                    target = (JavaExpression)instanceInvocation.getInvocationTarget().accept(new JavaModelCopier<Void>(), arg);
                }

                String varName = null;
                if (evaluateTarget instanceof LocalName) {
                    varName =  ( ((LocalName)evaluateTarget).getName());
                } else if (evaluateTarget instanceof LocalVariable) {
                    varName = ( ((LocalVariable)evaluateTarget).getName());
                } else if (evaluateTarget instanceof MethodVariable) {
                    varName = ( ((MethodVariable)evaluateTarget).getName());
                } else {
                    throw new NullPointerException ("Unhandled sub type of Nameable in VarReleaser.visitInstanceMethodInvocation().");
                }

                JavaExpression newInvocation =
                    new MethodInvocation.Instance(
                        target,
                        instanceInvocation.getMethodName(),
                        instanceInvocation.getDeclaringClass(),
                        argValues,
                        argTypes,
                        instanceInvocation.getReturnType(),
                        instanceInvocation.getInvocationType());

                if (shouldRelease(varName)) {
                    JavaTypeName varType = variablesOfInterest.get(varName);
                    variablesOfInterest.remove(varName);
                    return callLastRef(newInvocation, evaluateTarget, varType);
                }

                return newInvocation;

            } else {
                JavaExpression target = null;
                if (instanceInvocation.getInvocationTarget() != null) {
                    target = (JavaExpression)instanceInvocation.getInvocationTarget().accept(this, arg);
                }

                JavaExpression newInvocation =
                    new MethodInvocation.Instance(
                        target,
                        instanceInvocation.getMethodName(),
                        instanceInvocation.getDeclaringClass(),
                        argValues,
                        argTypes,
                        instanceInvocation.getReturnType(),
                        instanceInvocation.getInvocationType());

                return newInvocation;
            }

        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitStaticMethodInvocationExpression(org.openquark.cal.internal.machine.lecc.JavaExpression.MethodInvocation.Static, java.lang.Object)
         */
        public JavaExpression visitStaticMethodInvocationExpression(
                MethodInvocation.Static staticInvocation, Void arg) {

            // We want to access the method arguments in reverse order
            // as we want to apply the transformation to the rightmost
            // reference to a variable.
            JavaTypeName[] argTypes = new JavaTypeName[staticInvocation.getNArgs()];
            JavaExpression[] argValues = new JavaExpression[staticInvocation.getNArgs()];
            for (int i = argTypes.length - 1; i >= 0; --i) {
                argTypes[i] = staticInvocation.getParamType(i);
                argValues[i] = (JavaExpression)staticInvocation.getArg(i).accept(this, arg);
            }

            return new MethodInvocation.Static(
                    staticInvocation.getInvocationClass(),
                    staticInvocation.getMethodName(),
                    argValues,
                    argTypes,
                    staticInvocation.getReturnType());
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitSwitchStatement(org.openquark.cal.internal.machine.lecc.JavaStatement.SwitchStatement, java.lang.Object)
         */
        public JavaStatement visitSwitchStatement(SwitchStatement switchStatement,
                Void arg) {

            List<Map<String, JavaTypeName>>remainingVars = new ArrayList<Map<String, JavaTypeName>>();
            List<IntCaseGroup> cases = switchStatement.getCaseGroups();
            List<IntCaseGroup> newCases = new ArrayList<IntCaseGroup>();

            // We want to treat each case group independently with regards to the
            // variables that need to be released.
            for (int i = 0, n = cases.size(); i < n; ++i) {
                IntCaseGroup group = cases.get(i);
                Map<String, JavaTypeName> variablesToFindInGroup = new HashMap<String, JavaTypeName>(variablesOfInterest);
                remainingVars.add(variablesToFindInGroup);

                VarReleaser vr = new VarReleaser(variablesToFindInGroup);
                newCases.add((IntCaseGroup)group.accept(vr, null));
            }

            SwitchStatement.DefaultCase newDefault = null;
            Map<String, JavaTypeName> variablesOfInterestForDefault = new HashMap<String, JavaTypeName>(variablesOfInterest);
            if (switchStatement.getDefaultStatement() != null) {
                VarReleaser vr = new VarReleaser(variablesOfInterestForDefault);
                newDefault = new SwitchStatement.DefaultCase (
                        (JavaStatement)switchStatement.getDefaultStatement().accept(vr, arg));
            }

            // Update the variables of interest.
            // This will be the union of the variables remaining from each branch.
            variablesOfInterest.clear();
            for(String varName : variablesOfInterestForDefault.keySet()) {
                boolean missing = false;
                for (int i = 0, n = remainingVars.size(); i < n; ++i) {
                    if (!remainingVars.get(i).containsKey(varName)) {
                        missing = true;
                    }
                }
                if (!missing) {
                    variablesOfInterest.put(varName, variablesOfInterestForDefault.get(varName));
                }
            }

            SwitchStatement newSwitch = new SwitchStatement (
                    (JavaExpression)switchStatement.getCondition().accept(this, arg));


            for (int i = 0, n = newCases.size(); i < n; ++i) {
                newSwitch.addCase(newCases.get(i));
            }

            if (newDefault != null) {
                newSwitch.addCase(newDefault);
            }

            return newSwitch;
        }
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitIfThenElseStatement(org.openquark.cal.internal.machine.lecc.JavaStatement.IfThenElseStatement, java.lang.Object)
         */
        public JavaStatement visitIfThenElseStatement(IfThenElseStatement ifThenElse,
                Void arg) {

            // Treat each branch independently with regards to the variables to be realeased.
            Map<String, JavaTypeName> variablesOfInterestForThen = new HashMap<String, JavaTypeName>(variablesOfInterest);
            VarReleaser vr = new VarReleaser(variablesOfInterestForThen);
            JavaStatement newThen = (JavaStatement)ifThenElse.getThenStatement().accept(vr, null);

            JavaStatement newElse = null;
            Map<String, JavaTypeName> variablesOfInterestForElse = new HashMap<String, JavaTypeName>(variablesOfInterest);
            if (ifThenElse.getElseStatement() != null) {
                vr = new VarReleaser(variablesOfInterestForElse);
                newElse = (JavaStatement)ifThenElse.getElseStatement().accept(vr, null);
            }

            // Update variables of interest.
            variablesOfInterest.clear();
            for (String varName : variablesOfInterestForThen.keySet()) {
                if (variablesOfInterestForElse.containsKey(varName)) {
                    variablesOfInterest.put(varName, variablesOfInterestForElse.get(varName));
                }
            }

            JavaExpression condition =
                (JavaExpression)ifThenElse.getCondition().accept (this, arg);

            if (newElse != null) {
                return new IfThenElseStatement(condition, newThen, newElse);
            }

            return new IfThenElseStatement(condition, newThen);
        }




        /* (non-Javadoc)
         * @see org.openquark.cal.internal.machine.lecc.JavaModelVisitor#visitBlockStatement(org.openquark.cal.internal.machine.lecc.JavaStatement.Block, java.lang.Object)
         */
        public JavaStatement visitBlockStatement(Block block, Void arg) {

            // Treat each exception handler independently with regards to the variables to
            // be released, since they are mutually exclusive.
            List<JavaExceptionHandler> newExceptionHandlers = new ArrayList<JavaExceptionHandler>();
            List<JavaExceptionHandler> oldExceptionHandlers = block.getExceptionHandlers();
            List<Map<String, JavaTypeName>> remainingVars = new ArrayList<Map<String, JavaTypeName>>();
            for (int i = 0, n = oldExceptionHandlers.size(); i < n; ++i) {
                Map<String, JavaTypeName> variablesOfInterestForHandler = new HashMap<String, JavaTypeName>(variablesOfInterest);
                remainingVars.add(variablesOfInterestForHandler);
                VarReleaser vr = new VarReleaser(variablesOfInterestForHandler);
                newExceptionHandlers.add((JavaExceptionHandler)(oldExceptionHandlers.get(i)).accept(vr, arg));
            }

            Block newBlock = new Block(newExceptionHandlers);

            // Update variables of interest.
            if (remainingVars.size() > 0) {
                variablesOfInterest.clear();
                for (String varName : remainingVars.get(0).keySet()) {
                    boolean missing = false;
                    for (int i = 1, n = remainingVars.size(); i < n; ++i) {
                        if (!remainingVars.get(i).containsKey(varName)) {
                            missing = true;
                            break;
                        }
                    }
                    if (!missing) {
                        variablesOfInterest.put(varName, remainingVars.get(0).get(varName));
                    }
                }
            }

            // Traverse the contained statements in reverse order, since we want to release the
            // last reference to each variable.
            List<JavaStatement> newStatements = new ArrayList<JavaStatement>(block.getNBlockStatements());
            for (int i = block.getNBlockStatements() - 1; i >= 0; --i) {
                newStatements.add(0, (JavaStatement)block.getNthBlockStatement(i).accept(this, arg));
            }

            for (int i = 0, n = newStatements.size(); i < n; ++i) {
                newBlock.addStatement(newStatements.get(i));
            }

            return newBlock;
        }

    }
}
