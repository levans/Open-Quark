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
 * JavaDefinitionBuilder.java
 * Creation date: Oct 6, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.machine.lecc;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.CoreFunction;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.ExpressionAnalyzer;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.internal.javamodel.JavaClassRep;
import org.openquark.cal.internal.javamodel.JavaConstructor;
import org.openquark.cal.internal.javamodel.JavaExpression;
import org.openquark.cal.internal.javamodel.JavaFieldDeclaration;
import org.openquark.cal.internal.javamodel.JavaMethod;
import org.openquark.cal.internal.javamodel.JavaOperator;
import org.openquark.cal.internal.javamodel.JavaStatement;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.javamodel.JavaExpression.Assignment;
import org.openquark.cal.internal.javamodel.JavaExpression.CastExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassInstanceCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.InstanceOf;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalName;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression;
import org.openquark.cal.internal.javamodel.JavaStatement.AssertStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.Block;
import org.openquark.cal.internal.javamodel.JavaStatement.ExpressionStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.IfThenElseStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.JavaDocComment;
import org.openquark.cal.internal.javamodel.JavaStatement.LineComment;
import org.openquark.cal.internal.javamodel.JavaStatement.LocalVariableDeclaration;
import org.openquark.cal.internal.javamodel.JavaStatement.ReturnStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.ThrowStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.UnconditionalLoop;
import org.openquark.cal.internal.machine.CodeGenerationException;
import org.openquark.cal.internal.machine.lecc.SCJavaDefn.KernelLiteral;
import org.openquark.cal.internal.machine.lecc.SCJavaDefn.ReferencedDCInfo;
import org.openquark.cal.internal.machine.lecc.SCJavaDefn.Scheme;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTFullApp;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * A class to build a Java representation of a CAL functional agent.
 * @author Edward Lam
 */
final class JavaDefinitionBuilder {

    static final JavaTypeName[] EMPTY_TYPE_NAME_ARRAY = new JavaTypeName[0];

    private static final String ROOT_NODE = "$rootNode";
    private static final MethodVariable METHODVAR_ROOT_NODE = new MethodVariable(ROOT_NODE);
    private static final String CURRENT_ROOT_NODE = "$currentRootNode";
    private static final LocalVariable LOCALVAR_CURRENT_ROOT_NODE = new LocalVariable(CURRENT_ROOT_NODE, JavaTypeNames.RTVALUE);
    private static final MethodVariable METHODVAR_TAGVAL = new MethodVariable("tagVal");
    private static final MethodVariable METHODVAR_ORDINAL = new MethodVariable("ordinal");
    private static final MethodVariable METHODVAR_DEEPSEQ = new MethodVariable("deepSeq");
    private static final MethodVariable METHODVAR_RHS = new MethodVariable("rhs");
    private static final MethodVariable METHODVAR_FIELDINDEX = new MethodVariable("fieldIndex");

    /**
     * Constructor for a JavaDefinitionBuilder
     */
    private JavaDefinitionBuilder () {
        // Since the JavaDefinitionBuilder is stateless we do nothing here.
        // The constructor is made private to control creation.
    }

    /**
     * Get the object representation for a Java class representing the given supercombinator.
     * @param machineFunctions
     * @param module
     * @param codeGenerationStats - the object for collection code generation information, can be null
     * @return JavaClassRep an object representation for the given code label.
     * @throws CodeGenerationException
     */
    static final JavaClassRep getSCDefinition(LECCModule.FunctionGroupInfo machineFunctions,
                                              LECCModule module,
                                              CodeGenerationStats codeGenerationStats) throws CodeGenerationException {
        return SCDefinitionBuilder.getSCDefinition(machineFunctions, module, codeGenerationStats);
    }

    /**
     * Get the object representation for a Java class representing the data type.
     * @param typeCons the type constructor.
     * @param module the LECCModule instance corresponding to the module defining the entity.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @param codeGenerationStats - the object for collection code generation information, can be null
     * @return JavaClassRep an object representation for the given code label.
     * @throws CodeGenerationException
     */
    static final JavaClassRep getDataTypeDefinition(TypeConstructor typeCons, LECCModule module, CodeGenerationStats codeGenerationStats) throws CodeGenerationException {
        return DataTypeDefinitionBuilder.getDataTypeDefinition(typeCons, module, codeGenerationStats);
    }

    /**
     * Get the object representation for a Java class by name.
     * The returned class will not have any inner class info set.
     *
     * @param module the module in which the represented entity exists.
     * @param unqualifiedClassName the unqualified class name (ie. without the package name)
     * @return JavaClassRep an object representation for the given class.
     * @throws CodeGenerationException
     */
    static final JavaClassRep getClassRep(LECCModule module, String unqualifiedClassName) throws CodeGenerationException {

        // Get the outer class name if this is an inner class.
        int lastDollarIndex = unqualifiedClassName.indexOf('$');
        String outerClassName = lastDollarIndex < 0 ? unqualifiedClassName : unqualifiedClassName.substring(0, lastDollarIndex);

        ModuleName moduleName = module.getName();

        // If the unqualified class name is the name of a class representing a type, get the type's name.
        String typeName = CALToJavaNames.getUnqualifiedTypeNameFromClassName(moduleName, outerClassName, module);

        // Note that below we pass in null for code generation stats
        //   The stats object is used to track stats for generation of the whole module or program (which we aren't doing here).
        //   We can pass a non-null object if in future we want to track stats for a single program.
        if (typeName != null) {
            // Get the class from the type definition builder.
            TypeConstructor typeConstructor = module.getModuleTypeInfo().getTypeConstructor(typeName);
            return DataTypeDefinitionBuilder.getClassRep(typeConstructor, unqualifiedClassName, module, null);
        } else {
            // Get the class from the function definition builder.
            String functionName = CALToJavaNames.getUnqualifiedFunctionNameFromClassName(moduleName, outerClassName, module);
            MachineFunction mf = module.getFunction(functionName);
            LECCModule.FunctionGroupInfo mfs = module.getFunctionGroupInfo(mf);
            return SCDefinitionBuilder.getClassRep(mfs, module, unqualifiedClassName, null);
        }
    }

    /**
     * Get the object representation for a Java class by name.
     * The returned class *will* inner class info set.
     *
     * @param module the module in which the represented entity exists.
     * @param unqualifiedClassName the unqualified class name (ie. without the package name)
     * @return JavaClassRep an object representation for the given class.
     * @throws CodeGenerationException
     */
    static final JavaClassRep getClassRepWithInnerClasses(LECCModule module, String unqualifiedClassName) throws CodeGenerationException {

        // Get the outer class name if this is an inner class.
        int lastDollarIndex = unqualifiedClassName.indexOf('$');
        String outerClassName = lastDollarIndex < 0 ? unqualifiedClassName : unqualifiedClassName.substring(0, lastDollarIndex);

        ModuleName moduleName = module.getName();

        // If the unqualified class name is the name of a class representing a type, get the type's name.
        String typeName = CALToJavaNames.getUnqualifiedTypeNameFromClassName(moduleName, outerClassName, module);

        // Note that below we pass in null for code generation stats
        //   The stats object is used to track stats for generation of the whole module or program (which we aren't doing here).
        //   We can pass a non-null object if in future we want to track stats for a single program.
        if (typeName != null) {
            // Get the class from the type definition builder.
            TypeConstructor typeConstructor = module.getModuleTypeInfo().getTypeConstructor(typeName);
            return DataTypeDefinitionBuilder.getDataTypeDefinition(typeConstructor, module, null);
        } else {
            // Get the class from the function definition builder.
            String functionName = CALToJavaNames.getUnqualifiedFunctionNameFromClassName(moduleName, outerClassName, module);
            MachineFunction mf = module.getFunction(functionName);
            LECCModule.FunctionGroupInfo mfs = module.getFunctionGroupInfo(mf);
            return SCDefinitionBuilder.getSCDefinition(mfs, module, null);
        }
    }

    /**
     * Returns a LiteralWrapper instance which is the default value for the given type.
     * @param forType
     * @return a LiteralWrapper instance which is the default value for the given type.
     */
    static final LiteralWrapper getDefaultValueForType (JavaTypeName forType) {
        if (forType == null) {
            return LiteralWrapper.NULL;
        } else if (forType.equals(JavaTypeName.BOOLEAN)) {
            return LiteralWrapper.FALSE;
        } else if (forType.equals(JavaTypeName.BYTE)) {
            return LiteralWrapper.make(Byte.valueOf(((byte) 0)));
        } else if (forType.equals(JavaTypeName.CHAR)) {
            return LiteralWrapper.make(Character.valueOf(' '));
        } else if (forType.equals(JavaTypeName.DOUBLE)) {
            return LiteralWrapper.make(new Double(-1));
        } else if (forType.equals(JavaTypeName.FLOAT)) {
            return LiteralWrapper.make(new Float(-1));
        } else if (forType.equals(JavaTypeName.INT)) {
            return LiteralWrapper.make(Integer.valueOf(-1));
        } else if (forType.equals(JavaTypeName.LONG)) {
            return LiteralWrapper.make(Long.valueOf(-1));
        } else if (forType.equals(JavaTypeName.SHORT)) {
            return LiteralWrapper.make(Short.valueOf(((short) -1)));
        } else {
            return LiteralWrapper.NULL;
        }
    }


    /**
     * An SCDefinitionBuilder builds an internal object representation of a Java
     * class file for a supercombinator.
     *
     * @author Edward Lam
     */
    static final class SCDefinitionBuilder {

        private static final JavaTypeName _0TypeName = JavaTypeName.make(RTFullApp.General._0.class);
        private static final String functionTagFieldName = "scTag";

        /** The name of the class for the sc being built. */
        private final JavaTypeName className;

        /** The functions that will be in represented by the
         * generated class.
         */
        private final LECCModule.FunctionGroupInfo functions;

        /** The Java class representation for the supercombinator. */
        private JavaClassRep javaClassRep;

        /**
         * The LECCModule instance corresponding to either the module defining
         * the entity. This is used for obtaining the appropriate
         * {@link LECCModule.ClassNameMapper} for use in mapping names.
         */
        private final LECCModule module;

        private static boolean CONSOLIDATE_FUNCTIONS = LECCMachineConfiguration.CONSOLIDATE_FUNCTION_BODIES;

        private final CodeGenerationStats codeGenerationStats;

        /**
         * An array of JavaTypeName of size two.  Both members are initialized to JavaTypeNames.RTVALUE.
         * This field is used when specifying argument types when creating a method call instance.
         */
        private static final JavaTypeName[] TWO_RTVALUES = new JavaTypeName[]{JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE};


        /**
         * Constructor for an SCDefinitionBuilder.
         * @param machineFunctions
         * @param module
         * @param codeGenerationStats - object to collect code generation info.  May be null.
         */
        private SCDefinitionBuilder(LECCModule.FunctionGroupInfo machineFunctions,
                                    LECCModule module,
                                    CodeGenerationStats codeGenerationStats) {

            this.className = CALToJavaNames.createTypeNameFromSC(QualifiedName.make(module.getName(), machineFunctions.getFunctionGroupName()), module);
            this.module = module;
            this.functions = machineFunctions;
            this.codeGenerationStats = codeGenerationStats;
        }

        /**
         * Get the object representation for a Java class representing a function by name.
         * The returned class will not have any inner class info set.
         *
         * @param mfs
         * @param module
         * @param unqualifiedClassName
         * @param codeGenerationStats
         * @return the corresponding class representation, or null if the name is not a class built by this builder.
         * @throws CodeGenerationException
         */
        static JavaClassRep getClassRep(LECCModule.FunctionGroupInfo mfs, LECCModule module, String unqualifiedClassName, CodeGenerationStats codeGenerationStats) throws CodeGenerationException {
            SCDefinitionBuilder builder = new SCDefinitionBuilder (mfs, module, codeGenerationStats);

            // className.getUnqualifiedJavaSourceName() returns a string where inner classes are delimited with '.'.
            String unqualifiedBuilderClassName = builder.className.getUnqualifiedJavaSourceName().replace('.', '$');

            if (unqualifiedClassName.equals(unqualifiedBuilderClassName)) {
                return builder.getOuterClassRep();
            }

            for (final MachineFunction mf : mfs.getTopLevelCALFunctions()) {
                QualifiedName calFunctionName = mf.getQualifiedName();
                if (unqualifiedClassName.equals(CALToJavaNames.createStrictInnerClassNameFromSC(calFunctionName, module))) {
                    return builder.getStrictAppClass(mf);
                }
                if (unqualifiedClassName.equals(CALToJavaNames.createLazyInnerClassNameFromSC(calFunctionName, module))) {
                    return builder.getLazyAppClass(mf);
                }

            }

            return null;
        }


        /**
         * Get the Java representation for the functional form of a given supercombinator.
         * This will include inner class info.
         *
         * @param machineFunctions
         * @param module
         * @param codeGenerationStats - object for collecting code generation information
         * @return JavaClassRep
         * @throws CodeGenerationException
         */
        static final JavaClassRep getSCDefinition(LECCModule.FunctionGroupInfo machineFunctions,
                                                  LECCModule module,
                                                  CodeGenerationStats codeGenerationStats) throws CodeGenerationException {
            SCDefinitionBuilder instance = new SCDefinitionBuilder (machineFunctions, module, codeGenerationStats);
            return instance.generateSCDefinition ();
        }

        /**
         * Create the getModuleName() method.
         *     public final String getModuleName() {
         *         return "ModuleName";
         *     }
         *
         */
        private final void createMethod_getModuleName () {
            JavaMethod jm = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "getModuleName");
            javaClassRep.addMethod(jm);
            jm.addStatement(new ReturnStatement(LiteralWrapper.make(module.getName().toSourceText())));
        }

        /**
         * Create the getUnqualifiedName() method.
         *     public final String getModuleName() {
         *         return "unqualifiedName";
         *     }
         *
         */
        private final void createMethod_getUnqualifiedName () {
            JavaMethod jm =
                new JavaMethod (Modifier.PUBLIC | Modifier.FINAL,
                                JavaTypeName.STRING,
                                "getUnqualifiedName");
            javaClassRep.addMethod(jm);

            if (functions.getNFunctions() > 1) {
                SwitchStatement switchStatement =
                    new SwitchStatement(new JavaField.Instance(null, SCDefinitionBuilder.functionTagFieldName, JavaTypeName.INT));

                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                    switchStatement.addCase(
                            new SwitchStatement.IntCaseGroup(
                                    functions.getFunctionIndex(mf.getName()),
                                    new ReturnStatement(LiteralWrapper.make(mf.getName()))));
                }
                jm.addStatement(switchStatement);

                MethodInvocation mi =
                    new MethodInvocation.Static (JavaTypeNames.RTVALUE,
                                                   "badValue_Object",
                                                   new JavaExpression[]{LiteralWrapper.NULL, LiteralWrapper.make("Bad index in getUnQualifiedName()")},
                                                   new JavaTypeName[]{JavaTypeName.ERRORINFO, JavaTypeName.STRING},
                                                   JavaTypeName.OBJECT);
                jm.addStatement(new ReturnStatement(new CastExpression(JavaTypeName.STRING, mi)));
            } else {
                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                    jm.addStatement(new ReturnStatement(LiteralWrapper.make(mf.getName())));
                }

            }
        }

        /**
         * Create the getQualifiedName() method.
         *    public final String getQualifiedName () {
         *        return "Module.unqualifiedName";
         *    }
         */
        private final void createMethod_getQualifiedName () {
            JavaMethod jm = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "getQualifiedName");
            javaClassRep.addMethod(jm);

            if (functions.getNFunctions() > 1) {
                SwitchStatement switchStatement =
                    new SwitchStatement(new JavaField.Instance(null, SCDefinitionBuilder.functionTagFieldName, JavaTypeName.INT));

                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                    switchStatement.addCase(
                            new SwitchStatement.IntCaseGroup(
                                    functions.getFunctionIndex(mf.getName()),
                                    new ReturnStatement(LiteralWrapper.make(mf.getQualifiedName().getQualifiedName()))));
                }

                jm.addStatement(switchStatement);

                MethodInvocation mi =
                    new MethodInvocation.Static (JavaTypeNames.RTVALUE,
                                                   "badValue_Object",
                                                   new JavaExpression[]{LiteralWrapper.NULL, LiteralWrapper.make("Bad index in getQualifiedName()")},
                                                   new JavaTypeName[]{JavaTypeName.ERRORINFO, JavaTypeName.STRING},
                                                   JavaTypeName.OBJECT);
                jm.addStatement(new ReturnStatement(new CastExpression(JavaTypeName.STRING, mi)));
            } else {
                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                    jm.addStatement( new ReturnStatement(LiteralWrapper.make(mf.getQualifiedName().getQualifiedName())));
                }
            }
        }

        /**
         * Generate the java class representation of the function for which this builder is responsible.
         * This representation will contain the relevant inner classes.
         *
         * @return the generated class representation.
         * @throws CodeGenerationException
         */
        private final JavaClassRep generateSCDefinition () throws CodeGenerationException {
            if (this.javaClassRep == null) {
                generateOuterSCDefinition();

                // Now go over the difference CAL functions represented by this class and
                // generate any special purpose application nodes.
                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {

                    boolean hasStrictUnboxableArguments = false;
                    for (int i = 0; i < mf.getArity(); ++i) {
                        if (mf.getParameterStrictness()[i] &&
                                SCJavaDefn.canTypeBeUnboxed(mf.getParameterTypes()[i])) {
                                hasStrictUnboxableArguments = true;
                        }

                    }
                    boolean generateFnMethods = mf.getArity() > 0
                    && (mf.getArity() <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH
                            || mf.isTailRecursive() || hasStrictUnboxableArguments);

                    boolean generateApplicationClasses = generateFnMethods
                        && (hasStrictUnboxableArguments || mf.isTailRecursive());

                    if (generateApplicationClasses) {
                        createStrictAppClass(mf);

                        // We only generate a lazy application node class for tail recursive functions with
                        // an arity too large to be handled by one of the built-in lazy application node
                        // classes.  We want the lazy app node so that we can release the roots of the
                        // arguments to the tail recursive function.
                        if (mf.isTailRecursive() && mf.getArity() > LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH) {
                            createLazyAppClass(mf);
                        }
                    }
                }
            }

            return javaClassRep;
        }

        /**
         * Build the let variable definition functions for this function.
         * This involves lifting the let variable definitions into their own
         * functions and then generating the corresponding Java methods.
         * @param sharedValues
         * @return a Collection of JavaMethod
         * @throws CodeGenerationException
         */
        private Collection<JavaMethod> buildLetVarDefFunctions(SCJavaDefn.SharedValues sharedValues) throws CodeGenerationException {

            List<JavaMethod> methods = new ArrayList<JavaMethod>();

            for (final MachineFunction mf : functions.getLiftedLetVarDefFunctions()) {

                // Generate three versions of letvar def function:
                // lazy, strict, unboxed.
                methods.add(buildLetVarDefFunction(mf, sharedValues, Scheme.C_SCHEME));
                methods.add(buildLetVarDefFunction(mf, sharedValues, Scheme.E_SCHEME));

                if (SCJavaDefn.canTypeBeUnboxed(mf.getResultType())) {
                    methods.add(buildLetVarDefFunction(mf, sharedValues, Scheme.UNBOX_INTERNAL_SCHEME));
                }
            }

            return methods;
        }

        /**
         * Generate the Java method for a lifted let variable definition function.
         * @param mf
         * @param sharedValues
         * @param scheme
         * @return the JavaMethod representing the let variable definition function.
         * @throws CodeGenerationException
         */
        private JavaMethod buildLetVarDefFunction (MachineFunction mf, SCJavaDefn.SharedValues sharedValues, Scheme scheme) throws CodeGenerationException {
            int modifiers = Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL;

            SCJavaDefn javaDefn = new SCJavaDefn(mf, mf.getExpressionForm(), module, codeGenerationStats, sharedValues);

            // Figure out the methodName
            final int arity = javaDefn.getArity();
            String methodName = CALToJavaNames.makeLetVarDefFunctionJavaName(javaDefn.getQualifiedName(), module) + "_";
            if (scheme == Scheme.E_SCHEME) {
                methodName = methodName + "Strict";
            } else
            if (scheme == Scheme.C_SCHEME) {
                methodName = methodName + "Lazy";
            } else
            if (scheme == Scheme.UNBOX_INTERNAL_SCHEME) {
                methodName = methodName + "Unboxed";
            }

            Block bodyBlock = javaDefn.genS_LetVarDef(scheme, (LECCLiftedLetVarMachineFunction)mf);


            // Figure out the arg names and types.
            String[] argNames = new String[arity + 1];
            JavaTypeName[] argTypes = new JavaTypeName[arity + 1];

            // Default type of argument is RTValue
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

            // Fill in the argument names from the SC definition.
            for (int i =  0; i < arity; ++i) {
                argNames[i] = javaDefn.getJavaArgumentName(i);
            }

            // The last argument is the execution context.
            argNames[arity] = SCJavaDefn.EXECUTION_CONTEXT_NAME;
            argTypes[arity] = JavaTypeNames.RTEXECUTION_CONTEXT;

            // Try to get type info for this SC.
            for (int i = 0; i < arity; ++i) {
                if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                    argTypes[i] = javaDefn.getArgumentTypeName(i);
                }
            }

            // Add the method to the class.
            JavaTypeName returnType = JavaTypeNames.RTVALUE;
            if (scheme == Scheme.UNBOX_INTERNAL_SCHEME) {
                returnType = SCJavaDefn.typeExprToTypeName(mf.getResultType());
            }
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, argNames, argTypes, null, methodName);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            // If we are doing a sanity check on let variable behaviour we
            // want to check/set the flag associated with this method.
            if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                JavaField flag = new JavaField.Instance(null, CALToJavaNames.cleanSCName(javaDefn.getFunctionName()) + "_flag_", JavaTypeName.BOOLEAN);
                JavaExpression exception =
                    new JavaExpression.ClassInstanceCreationExpression(
                            JavaTypeName.NULL_POINTER_EXCEPTION,
                            LiteralWrapper.make("Double evaluation of " + javaDefn.getModuleName() + "." + CALToJavaNames.cleanSCName(javaDefn.getFunctionName()) + " in " + functions.getFunctionGroupName()),
                            JavaTypeName.STRING);

                JavaStatement.IfThenElseStatement check =
                    new JavaStatement.IfThenElseStatement(flag, new JavaStatement.ThrowStatement(exception));

                javaMethod.addStatement(check);

                JavaExpression assign = new JavaExpression.Assignment(flag, LiteralWrapper.make(Boolean.TRUE));
                javaMethod.addStatement(new ExpressionStatement(assign));
            }

            // Add the body
            javaMethod.addStatement(bodyBlock);

            return javaMethod;
        }

        /**
         * Lift the let variables in the given function into their own
         * functions.
         * @param mf
         * @return the modified body of the given function.
         */
        private Expression liftLetVars (MachineFunction mf) {
            // Lift the let variable definitions.
            ExpressionAnalyzer.LiftedLetVarResults liftedLetVarResults =
                ExpressionAnalyzer.liftLetVariables(mf, module);

            // For each lifted let variable create an LECCLiftedLetVarMachineFunction
            // and update the FunctionGroupInfo and Module to include the newly created
            // function.
            for (final ExpressionAnalyzer.LiftedLetVarInfo liftedLetVarInfo : liftedLetVarResults.getLiftedVarInfo()) {
                String[] paramNames = liftedLetVarInfo.getParameterNames();
                TypeExpr[] paramTypes = liftedLetVarInfo.getParameterTypes();
                int arity = liftedLetVarInfo.getArity();
                String functionName = liftedLetVarInfo.getFunctionName();
                boolean paramStrictness[] = liftedLetVarInfo.getParameterStrictness();

                // We can fill in the strictness and type for parameters of the lifted
                // function which are simply the parameters of the containing function.
                for (int i = 0; i < arity; ++i) {
                    for (int j = 0, k = mf.getArity(); j < k; ++j) {
                        String containingArgName = mf.getParameterNames()[j];
                        if (containingArgName.equals(paramNames[i])) {
                            paramTypes[i] = mf.getParameterTypes()[j];
                            paramStrictness[i] = mf.getParameterStrictness()[j];
                            break;
                        }
                    }
                }

                LECCLiftedLetVarMachineFunction lmf =
                    new LECCLiftedLetVarMachineFunction(
                            QualifiedName.make(module.getName(), functionName),
                            arity,
                            paramNames,
                            paramTypes,
                            paramStrictness,
                            liftedLetVarInfo.getResultType(),
                            liftedLetVarInfo.getExpression(),
                            mf.isForAdjunct());

                functions.addLiftedLetVarFunction(lmf, mf.getName());
                module.addLiftedLetVarFunction(lmf, functions);

            }

            return liftedLetVarResults.getExpression();
        }

        /**
         * Generate the java class representation of the function for which this builder is responsible.
         * The generated representation will be the outermost class definition only -- no inner classes will have been generated.
         * @throws CodeGenerationException
         */
        private final void generateOuterSCDefinition() throws CodeGenerationException {
            if (this.javaClassRep == null) {

                // Get the fully-qualified superclass and class names;
                JavaTypeName superClassTypeName = JavaTypeNames.RTSUPERCOMBINATOR;
                if (functions.includesCAFs()) {
                    superClassTypeName = JavaTypeNames.RTCAF;
                }


                // Construct the class access flags.
                //int classModifiers = Modifier.FINAL | Modifier.SUPER;
                int classModifiers = Modifier.PUBLIC | Modifier.FINAL;

                // No interfaces are implemented
                JavaTypeName[] interfaces = JavaDefinitionBuilder.EMPTY_TYPE_NAME_ARRAY;

                // Now instantiate the java class representation.
                this.javaClassRep = new JavaClassRep(className, superClassTypeName, classModifiers, interfaces);

                SCJavaDefn.SharedValues sharedValues = new SCJavaDefn.SharedValues();

                // Build up a list of SCJavaDefn instances, once for each CAL function.
                // We call genS_SC_Boxed() to force the population of the SharedValues
                // instance.
                List<SCJavaDefn> scJavaDefns = new ArrayList<SCJavaDefn> ();
                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {

                    // We want to actually work with a modified version of the function body.  i.e. one
                    // which has had let variable definitions lifted into their own functions.  liftLetVars()
                    // builds up a list of lifted functions which can be referenced later.
                    Expression modifiedExpression = liftLetVars(mf);
                    SCJavaDefn javaDefn = new SCJavaDefn(mf, modifiedExpression, module, codeGenerationStats, sharedValues);
                    scJavaDefns.add(javaDefn);
                }

                // Now that the shared values have been initialized and all the let variable definitions have been
                // lifted we can generate the Java methods for each lifted let variable definition.
                // NOTE:  We need to do this before we generate fields so that we build the correct set of
                // literal values.
                Collection<JavaMethod> letVarDefFunctions = buildLetVarDefFunctions(sharedValues);

                // Now that the let variable definition functions are in place we can compile the main function
                // bodies (which may refer to the lifted variable functions).
                for (final SCJavaDefn javaDefn : scJavaDefns) {
                    javaDefn.genS_SC_Boxed();
                }

                // Now that we have the information about referenced values
                // across the set of CAL functions we can create the class fields.
                createFields(sharedValues);

                // Create a constructor.
                createConstructor();

                // Create a static factory method for accessing the instances for the
                // different supercombinators.
                createMethod_make();

                // If this contains a CAF it needs to implement the method to release its cached result.
                createMethod_resetCachedResults ();

                // Retrieve the arity of the represented supercombinator.
                createMethod_getArity();

                // Methods for determining information about the class instance.
                createMethod_getModuleName();
                createMethod_getUnqualifiedName();
                createMethod_getQualifiedName();

                // Add in the let var definition methods so that they will appear immediately before the
                // 'f' methods.
                for (final JavaMethod jm : letVarDefFunctions) {
                    javaClassRep.addMethod(jm);
                }

                Set<Integer> aritiesForfNMethods = new HashSet<Integer>();

                for (final SCJavaDefn javaDefn : scJavaDefns) {

                  /*
                     * There are, potentially three methods which implement the
                     * function logic: f, fnS, and fnL (where n is the function
                     * arity). The f method is used in the general case of graph
                     * reduction. It is passed the root of the application graph
                     * and must unwind the graph to extract the values for the
                     * function arguments. The fnL and fnS methods are called
                     * when all the arguments are available and can be passed in
                     * directly. The fnL method is used when all arguments are
                     * available but it is not guaranteed that any strict
                     * arguments are in WHNF. The fnS method is used when it can
                     * be guaranteed that strict arguments are in WHNF.
                     *
                     * The fnL/S methods will potentially be called for arity of
                     * up to SCJavaDefn.OPTIMIZED_APP_CHAIN_LENGTH, because that
                     * is the maximum arity for which the runtime has special
                     * purpose graph nodes for fully saturated applications.
                     *
                     * The other situation where the fnL/S methods will be
                     * called is when a function is tail recursive or has
                     * arguments which are strict and unboxable. In these cases
                     * a graph node for fully saturated applications will be
                     * generated, so we are not limited in the arity.
                     */
                    boolean generateFnMethods = javaDefn.getArity() > 0
                            && (javaDefn.getArity() <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH
                                    || javaDefn.isTailRecursive() || javaDefn
                                    .hasStrictUnboxableArguments());


                    /*
                     * Generally speaking the logic in each of the f methods
                     * (i.e. f, fnL, fnS) is the same except for the inital
                     * unpacking or evaluation of the arguments. It is therefore
                     * possible to consolidate the functions so that the f and
                     * fnL methods simply unpack/evaluate arguments and then
                     * call the fnS method. The exception to this is with tail
                     * recursive functions. Because a tail recursive function is
                     * generated as a function containing a loop a call to the
                     * function body can perform many reductions. This can cause
                     * a problem since arguments, such as a list, that are
                     * expanded still have their root held in the original
                     * application node. As a result the root node will be held
                     * in the execution context and the beginning of the
                     * function body will clear the fields of the root node.
                     * Similarly if the function bodies are consolidated the
                     * call from f/fnL to fnS will hold the argument roots on
                     * the stack, causing the same kind of space usage issue. As
                     * a result we don't consolidate function bodies for tail
                     * recursive functions.
                     */
                    final boolean consolidateFunctions = SCDefinitionBuilder.CONSOLIDATE_FUNCTIONS
                            && generateFnMethods;

                    // Function body used when dealing with a general application.
                    createMethod_fUnsaturated(javaDefn, consolidateFunctions);

                    if (generateFnMethods) {
                        aritiesForfNMethods.add(new Integer(javaDefn.getArity()));

                        // Function body used when dealing with a fully saturated application in a lazy context.
                        createMethod_fSaturatedLazy(javaDefn, consolidateFunctions);

                        // Function body used when dealing with a fully saturated application in a strict context.
                        createMethod_fSaturatedStrict(javaDefn);

                        // If the return type of the CAL function is a type which can be unboxed
                        // we need to generate the unboxed version of the fnS method.
                        if (javaDefn.genS_SC_Unboxed() != null &&
                            javaDefn.getArity() > 0) {
                            // Function body used when dealing with a fully saturated application in a strict context.
                            createMethod_fUnboxedSaturatedStrict(javaDefn);
                        }
                    }



                }

                if (functions.getNFunctions() > 1) {
                    createMethod_fSwitching();
                }

                Map<Integer, Integer> arityToCount = functions.getArityToCountMap();
                for (final Integer arity : arityToCount.keySet()) {

                    if (functions.getNFunctions() > 1 &&
                        arity.intValue() > 0 &&
                        aritiesForfNMethods.contains(arity) &&
                        arity.intValue() <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH) {

                        createMethod_fNSwitching(arity.intValue(), true, scJavaDefns);
                        createMethod_fNSwitching(arity.intValue(), false, scJavaDefns);
                    }
                }

            }
        }

        /**
         * If this generated class represents multiple supercombinators we may
         * need to generate a series of fnS functions, where n is the arity.
         * These functions are necessary in the case where a represented SC
         * has strict boxed arguments.  In this case the runtime classes,
         * especially application nodes, will acess the fnS method.
         * The f method for an SC with strict unboxed arguments will always
         * be called either directly or from a special purpose application node,
         * so they aren't included here.
         * @param arity
         * @param strict
         * @param javaDefns
         * @throws CodeGenerationException
         */
        private void createMethod_fNSwitching(int arity, boolean strict, List<SCJavaDefn> javaDefns) throws CodeGenerationException {

            int modifiers = Modifier.PUBLIC | Modifier.FINAL;

            // Add the method to the class.
            // Figure out the arg names and types.
            String[] argNames = new String[arity + 1];
            JavaTypeName[] argTypes = new JavaTypeName[arity + 1];
            JavaExpression[] argValues = new JavaExpression[arity+1];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
            for (int i =  0; i < arity; ++i) {
                // If the argument is strict and primitive add $L to the name so that we
                // can use the declared name for the primitive value.
                argNames[i] = "$arg" + i;
                argValues[i] = new MethodVariable(argNames[i]);
            }

            argNames[arity] = SCJavaDefn.EXECUTION_CONTEXT_NAME;
            argTypes[arity] = JavaTypeNames.RTEXECUTION_CONTEXT;
            argValues[arity] = SCJavaDefn.EXECUTION_CONTEXT_VAR;

            String methodName = "f" + arity + (strict ? "S" : "L");
            JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeNames.RTVALUE, argNames, argTypes, null, methodName);
            javaClassRep.addMethod(javaMethod);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            // At this point we want to switch based on the scTag and dispatch to the appropriate
            // sc specific f method.
            SwitchStatement switchStatement =
                new SwitchStatement(new JavaField.Instance(null, SCDefinitionBuilder.functionTagFieldName, JavaTypeName.INT));

            for (int i = 0, n = javaDefns.size(); i < n; ++i) {
                SCJavaDefn javaDefn = javaDefns.get(i);
                if (javaDefn.getArity() != arity) {
                    continue;
                }

                if (functions.getMachineFunction(javaDefn.getFunctionName()) instanceof LECCLiftedLetVarMachineFunction) {
                    continue;
                }

                if (strict &&
                    javaDefn.hasStrictUnboxableArguments()) {
                    continue;
                }

                methodName = functions.getFNamePrefix(javaDefn.getFunctionName()) + "f" + arity + (strict ? "S" : "L");
                MethodInvocation mi =
                    new MethodInvocation.Instance(
                            null,
                            methodName,
                            argValues,
                            argTypes,
                            JavaTypeNames.RTVALUE,
                            MethodInvocation.InvocationType.VIRTUAL);

                switchStatement.addCase(
                    new SwitchStatement.IntCaseGroup(functions.getFunctionIndex(javaDefn.getFunctionName()), new ReturnStatement(mi)));
            }

            javaMethod.addStatement(switchStatement);

            // Handle the fallthrough.
            // If this is the lazy version of the 'f' method we want to defer to the
            // version in the superclass (i.e. RTValue).  This can occur when there
            // is a lazy application of a functional argument, or an oversaturation.
            if (strict) {
                MethodInvocation mi =
                    new MethodInvocation.Static (JavaTypeNames.RTVALUE,
                                                   "badValue",
                                                   LiteralWrapper.make("Bad scTag in 'f'."),
                                                   JavaTypeName.STRING,
                                                   JavaTypeNames.RTVALUE);
                javaMethod.addStatement(new ReturnStatement(mi));
            } else {
                MethodInvocation mi =
                    new MethodInvocation.Instance(
                            null,
                            "f" + arity + "L",
                            this.javaClassRep.getSuperclassName(),
                            argValues,
                            argTypes,
                            JavaTypeNames.RTVALUE,
                            MethodInvocation.InvocationType.SPECIAL);

                javaMethod.addStatement(new JavaStatement.LineComment("This is an oversaturated lazy application."));
                javaMethod.addStatement(new JavaStatement.LineComment("Usually this occurs when dealing with a lazy application of a function type argument."));
                javaMethod.addStatement(new JavaStatement.LineComment("Defer to the base implementation in the super class."));

                javaMethod.addStatement(new ReturnStatement(mi));
            }
        }

        private void createMethod_fSwitching () {

            final int modifiers = Modifier.PUBLIC | Modifier.FINAL;

            // Add the method to the class.
            final String argNames[] = new String []{ROOT_NODE, SCJavaDefn.EXECUTION_CONTEXT_NAME};
            final JavaTypeName argTypes[] = new JavaTypeName []{JavaTypeNames.RTRESULT_FUNCTION, JavaTypeNames.RTEXECUTION_CONTEXT};
            final boolean[] argFinal = new boolean[] {true, true};

            String methodName = "f";

            final JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeNames.RTVALUE, argNames, argTypes, argFinal, methodName);
            javaClassRep.addMethod(javaMethod);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            // At this point we want to switch based on the scTag and dispatch to the appropriate
            // sc specific f method.
            SwitchStatement switchStatement =
                new SwitchStatement(new JavaField.Instance(null, SCDefinitionBuilder.functionTagFieldName, JavaTypeName.INT));

            for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                methodName = functions.getFNamePrefix(mf.getName()) + "f";
                MethodInvocation mi =
                    new MethodInvocation.Instance(
                            null,
                            methodName,
                            new JavaExpression[]{new MethodVariable(ROOT_NODE), SCJavaDefn.EXECUTION_CONTEXT_VAR},
                            argTypes,
                            JavaTypeNames.RTVALUE,
                            MethodInvocation.InvocationType.VIRTUAL);

                switchStatement.addCase(
                    new SwitchStatement.IntCaseGroup(
                            functions.getFunctionIndex(mf.getName()),
                            new ReturnStatement(mi)));
            }

            javaMethod.addStatement(switchStatement);

            // Handle the fallthrough.
            final MethodInvocation mi =
                new MethodInvocation.Static (JavaTypeNames.RTVALUE,
                                               "badValue",
                                               LiteralWrapper.make("Bad scTag in 'f'."),
                                               JavaTypeName.STRING,
                                               JavaTypeNames.RTVALUE);
            javaMethod.addStatement(new ReturnStatement(mi));
        }

        /**
         * Get the object representation for a Java class representing the function for this class.
         * This will not have any inner class info set.
         *
         * @return the class representation of the function for this class.
         * @throws CodeGenerationException
         */
        private JavaClassRep getOuterClassRep() throws CodeGenerationException {
            if (this.javaClassRep == null) {
                generateOuterSCDefinition();
            }

            for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                boolean hasStrictUnboxableArguments = false;
                for (int i = 0; i < mf.getArity(); ++i) {
                    if (mf.getParameterStrictness()[i] &&
                            SCJavaDefn.canTypeBeUnboxed(mf.getParameterTypes()[i])) {
                            hasStrictUnboxableArguments = true;
                    }

                }
                boolean generateFnMethods = mf.getArity() > 0
                && (mf.getArity() <= LECCMachineConfiguration.OPTIMIZED_APP_CHAIN_LENGTH
                        || mf.isTailRecursive() || hasStrictUnboxableArguments);

                boolean generateApplicationClasses = generateFnMethods
                    && (hasStrictUnboxableArguments || mf.isTailRecursive());


                if (generateApplicationClasses) {
                    // We need to mark this class as having
                    // inner classes containing assertions.
                    // This is done manually because we don't always
                    // generate the inner classes at the same time
                    // as the containing class.
                    javaClassRep.setInnerClassContainsAssertions();
                    break;
                }
            }

            return javaClassRep;
        }


        /**
         * Create the fields.
         *     private static final RTFunction instance = new ThisClass();
         *     literalDef1;
         *     literalDef2;
         *     ...
         *     @param sharedValues
         */
        private void createFields(final SCJavaDefn.SharedValues sharedValues) {

            // Create fields for the literal def symbols...
            // private final RTValue litSymbol = (litDefn.getKernelTypeClass()).make(litDefn.getConstructorExpression());
            if (sharedValues.getNLiteralValues() > 0) {
                javaClassRep.addComment(new JavaStatement.MultiLineComment("CAL data instances for literal values."));
            }
            for (final KernelLiteral kernelLiteral : sharedValues.getLiteralValues()) {
                kernelLiteral.addFieldDeclarations(javaClassRep);
            }

            // Instance of this class for each supercombinator it
            // represents.
            for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {

                final int functionIndex = functions.getFunctionIndex(mf.getName());
                final JavaExpression initializer;

                if (functions.getNFunctions() <= 1) {
                    initializer = new ClassInstanceCreationExpression(
                            className);
                } else {
                    initializer = new ClassInstanceCreationExpression(
                            className,
                            new JavaExpression[]{LiteralWrapper.make(Integer.valueOf(functionIndex)),
                                                 LiteralWrapper.make(Integer.valueOf(mf.getArity()))},
                            new JavaTypeName[]{JavaTypeName.INT,
                                               JavaTypeName.INT});
                }

                final String fieldName = CALToJavaNames.getInstanceFieldName(mf.getQualifiedName(), module);
                final int instanceModifiers;
                if (mf.getArity() == 0) {
                    //for a CAF or a zero-arity function, the instance field will never be referred to directly outside the class
                    //so declare them private for safety sake.
                    instanceModifiers = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
                } else {
                    instanceModifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
                }
                final JavaFieldDeclaration instanceDeclaration =
                    new JavaFieldDeclaration(instanceModifiers,
                            className,
                            fieldName,
                            initializer);

                if (functions.getNFunctions() <= 1) {
                    instanceDeclaration.setJavaDoc(new JavaDocComment("Singleton instance of this class."));
                } else {
                    instanceDeclaration.setJavaDoc(new JavaDocComment("Instance of this class representing CAL function " + mf.getName() + "."));
                }
                javaClassRep.addFieldDeclaration(instanceDeclaration);
            }


            if (sharedValues.getNStaticErrorInfo() > 0) {
                javaClassRep.addComment(new JavaStatement.MultiLineComment("ErrorInfo instances."));
            }
            for (final String name : sharedValues.getStaticErrorInfoNames()) {

                final JavaExpression initializer = sharedValues.getStaticError(name);
                final int instanceModifiers = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
                final JavaFieldDeclaration errorDeclaration = new JavaFieldDeclaration(instanceModifiers, JavaTypeName.ERRORINFO, name, initializer);
                javaClassRep.addFieldDeclaration(errorDeclaration);

            }

            // If this function references other supercombinators we need to set
            // up a field for each referenced SC, a flag to indicate the
            // initialization state of the referenced SC fields, and potentially an
            // object to be used as a synchronization mutex for the initialization method.
            final int referencedDCModifiers = Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL;

            // Create an instance field for each referenced data constructor.  This will give us
            // a local reference that can be used in the body function.
            //  e.g. RTFunction i_Foo;
            if (sharedValues.getNReferencedDCs() > 0) {
                javaClassRep.addComment(new JavaStatement.MultiLineComment("Data constructor class instances for all referenced data constructors."));
            }
            for (final ReferencedDCInfo rfi : sharedValues.getReferencedDCs()) {
                DataConstructor dc = rfi.getDC();
                JavaField jf = rfi.getJField();
                JavaExpression fieldInitializer;
                if (dc.getArity() > 0) {
                    // Just invoke the regular make invocation to get the singleton SC/DC instance.
                    fieldInitializer =
                        new MethodInvocation.Static(jf.getFieldType(), "make", JavaExpression.EMPTY_JAVA_EXPRESSION_ARRAY, JavaExpression.EMPTY_TYPE_NAME_ARRAY, jf.getFieldType());
                } else {
                    // This is a zero arity data constructor.  We get the singleton instance by accessing
                    // the DataType class factory method if the data type has more than one zero arity DC.
                    TypeConstructor typeCons = dc.getTypeConstructor();
                    if (SCJavaDefn.isTagDC(dc, module)) {
                        JavaTypeName typeClass = CALToJavaNames.createTypeNameFromType(typeCons, module);
                        JavaTypeName tagDCTypeName = CALToJavaNames.createTypeNameForTagDCFromType(typeCons, module);
                        Integer ordinal = Integer.valueOf(dc.getOrdinal());
                        fieldInitializer = new MethodInvocation.Static(typeClass, "getTagDC", LiteralWrapper.make(ordinal), JavaTypeName.INT, tagDCTypeName);
                    } else {
                        // Just invoke the regular make invocation to get the singleton SC/DC instance.
                        fieldInitializer =
                            new MethodInvocation.Static(jf.getFieldType(), "make", JavaExpression.EMPTY_JAVA_EXPRESSION_ARRAY, JavaExpression.EMPTY_TYPE_NAME_ARRAY, jf.getFieldType());
                    }
                }

                JavaFieldDeclaration dcDeclaration =
                    new JavaFieldDeclaration(
                            referencedDCModifiers,
                            jf.getFieldType(),
                            jf.getFieldName(),
                            fieldInitializer);
                javaClassRep.addFieldDeclaration(dcDeclaration);
            }

            if (functions.includesCAFs()) {

                javaClassRep.addComment(new JavaStatement.MultiLineComment("Mappings of execution context to CAF instances."));
                // Add an instance field to hold a Map of
                // ExecutionContext -> RTFullApp.General._0
                // A map for associating instances of this SC with execution contexts.
                // This is only created for CAF functions.  There are two reasons why CAFs
                // have an instance for each execution context.  One is thread safety.  The
                // other is so that different threads of execution can release cached CAF
                // results at will.
                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                    if (mf.isCAF()) {

                        //we do not synchronize the instance map, but rather the methods in the CAF class that mutate it.
                        //this is because the make method has check-then-modify semantics, and so needs to be synchronized at the method
                        //level anyways.
                        JavaExpression instancesMapInit = new ClassInstanceCreationExpression(JavaTypeName.WEAK_HASH_MAP);

                        String mapPrefix = functions.getFNamePrefix(mf.getName());
                        JavaFieldDeclaration instancesMap =
                            new JavaFieldDeclaration (
                                Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL,
                                JavaTypeName.MAP,
                                mapPrefix + "$instancesMap",
                                instancesMapInit);

                        instancesMap.setJavaDoc(new JavaDocComment("Execution context -> instance map for " + mf.getName()));
                        javaClassRep.addFieldDeclaration(instancesMap);
                    }
                }
            }

            // We have two int instance fields to hold the tag, indicating which function
            // is represented by the class instance, and the arity of that function.
            // These fields are only needed if the class represents more than one function.
            if (functions.getNFunctions() > 1) {
                JavaFieldDeclaration scTagDeclaration =
                    new JavaFieldDeclaration(Modifier.PRIVATE | Modifier.FINAL,
                                             JavaTypeName.INT,
                                             SCDefinitionBuilder.functionTagFieldName,
                                             null);

                List<String> commentLines = new ArrayList<String> ();
                commentLines.add ("Tag field indicating which CAL function this class instance represents.");
                for (int i = 0, n = functions.getTopLevelCALFunctions().size(); i < n; ++i) {
                    String fName = functions.getFunctionNameFromIndex(i);
                    commentLines.add("    " + i + " -> " + fName);
                }

                scTagDeclaration.setJavaDoc(new JavaStatement.JavaDocComment(commentLines));
                javaClassRep.addFieldDeclaration(scTagDeclaration);

                JavaFieldDeclaration arityDeclaration =
                    new JavaFieldDeclaration(Modifier.PRIVATE | Modifier.FINAL,
                            JavaTypeName.INT,
                            "arity",
                            null);
                arityDeclaration.setJavaDoc(new JavaStatement.JavaDocComment("Field holding arity of represented function."));
                javaClassRep.addFieldDeclaration(arityDeclaration);
            }


            if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                // Create a boolean flag for each lifted let variable definition.
                for (final MachineFunction mf : functions.getLiftedLetVarDefFunctions()) {
                    String flagName = CALToJavaNames.cleanSCName(mf.getName()) + "_flag_";

                    javaClassRep.addFieldDeclaration(
                            new JavaFieldDeclaration(Modifier.PRIVATE, JavaTypeName.BOOLEAN, flagName, LiteralWrapper.make(Boolean.FALSE)));
                }
            }
        }

        /**
         * Add a static method to clear any cached CAF results.
         */
        private void createMethod_resetCachedResults () {

            if (!functions.includesCAFs()) {
                return;
            }

            //todoBI it may be better to guard each instance map by synchronizing on it directly. However, currently the ASM bytecode
            //generator does not support synchronization blocks, and so this is adequate.
            final int modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL | Modifier.SYNCHRONIZED;
            JavaTypeName returnType = JavaTypeName.VOID;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, SCJavaDefn.EXECUTION_CONTEXT_NAME, JavaTypeNames.RTEXECUTION_CONTEXT, false, "resetCachedResults");
            javaClassRep.addMethod(javaMethod);

            for (final MachineFunction mf : functions.getTopLevelCALFunctions() ) {
                if (!mf.isCAF()) {
                    continue;
                }
                String mapPrefix = functions.getFNamePrefix(mf.getName());

                JavaField instanceField = new JavaField.Static(className, mapPrefix+"$instancesMap", JavaTypeName.MAP);
                MethodInvocation remove = new MethodInvocation.Instance (instanceField,
                                                             "remove",
                                                             new JavaExpression[]{SCJavaDefn.EXECUTION_CONTEXT_VAR},
                                                             new JavaTypeName[]{JavaTypeName.OBJECT},
                                                             JavaTypeName.OBJECT,
                                                             MethodInvocation.InvocationType.INTERFACE);
                javaMethod.addStatement (new ExpressionStatement(remove));
            }
        }

        /**
         * Create the make() method.
         *     public static final RTFunction make(int scTag) {
         *         switch (scTag) {
         *         }
         *     }
         *
         *     This method is used for creating instances of zero arity
         *     functions and CAFs.
         */
        private void createMethod_make() throws CodeGenerationException {
            if (!functions.includesCAFs() && !functions.includesZeroArityFunctions()) {
                return;
            }

            if (functions.includesZeroArityFunctions() && functions.getNFunctions() > 1) {
                throw new CodeGenerationException("zero-arity functions can only be in a component of size 1.");
            }

            final int modifiers;
            if (functions.includesCAFs()) {
                //todoBI it may be better to guard each instance map by synchronizing on it directly. However, currently the ASM bytecode
                //generator does not support synchronization blocks, and so this is adequate.
                modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL | Modifier.SYNCHRONIZED;
            } else {
                modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
            }

            JavaTypeName returnType = JavaTypeNames.RTFUNCTION;

            // Add the method to the class.
            JavaMethod javaMethod;
            if (functions.getNCAFs() + functions.getNZeroArityFunctions() <= 1) {
                javaMethod = new JavaMethod(modifiers,
                        returnType,
                        new String[]{SCJavaDefn.EXECUTION_CONTEXT_NAME},
                        new JavaTypeName[]{JavaTypeNames.RTEXECUTION_CONTEXT},
                        null, "make");
            } else {
                javaMethod = new JavaMethod(modifiers,
                               returnType,
                               new String[]{"scIndex", SCJavaDefn.EXECUTION_CONTEXT_NAME},
                               new JavaTypeName[]{JavaTypeName.INT, JavaTypeNames.RTEXECUTION_CONTEXT},
                               null, "make");
            }
            javaClassRep.addMethod(javaMethod);

            // Add the body..

// Turn on this code to include a diagnostic print statement.
//            JavaExpression field = new JavaField.Static(JavaTypeName.make("java.lang.System"), "out", JavaTypeName.make("java.io.PrintStream"));
//            JavaExpression args[] = new JavaExpression[1];
//            String s = javaDefn.getFunctionName() + ".make()";
//            args[0] = LiteralWrapper.make(s);
//            JavaTypeName argTypes[] = new JavaTypeName[1];
//            argTypes[0] = JavaTypeName.STRING;
//            JavaExpression me = new MethodInvocation.Instance (field, "println", args, argTypes, JavaTypeName.VOID, MethodInvocation.InvocationType.VIRTUAL);
//            JavaStatement se = new JavaStatement.ExpressionStatement(me);
//            javaMethod.addStatement(se);


            // For a CAF we cache 'instances' associated with execution contexts.  In this case an
            // 'instance' is represented by an application of the supercombinator to zero arguments.
            // A zero arity function is different from a CAF in that we don't cache the 'instance'.
            // Regular functions we just return the singleton supercombinator instance.

            if (functions.getNCAFs() + functions.getNZeroArityFunctions() <= 1) {
                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                    JavaField instanceField = new JavaField.Static(className, CALToJavaNames.getInstanceFieldName(mf.getQualifiedName(), module), className);

                    if (mf.isCAF()) {
                        Block b = new Block();
                        // This is a CAF (constant applicative form) we want to use the cached instance associated
                        // with the execution context, if it exists.
                        // If it doesn't exist we will create an instance and add it to the cache.

                        String instanceMapFieldName = functions.getFNamePrefix(mf.getName()) + "$instancesMap";
                        JavaField instanceMapField =
                            new JavaField.Static(className, instanceMapFieldName, JavaTypeName.MAP);

                        // RTFunction newInstance = (RTFunction)$instancesMap.get($ec);
                        JavaExpression.LocalVariable newInstanceVar =
                            new JavaExpression.LocalVariable("newInstance", returnType);
                        JavaExpression initializer = new JavaExpression.CastExpression(
                                returnType,
                                new MethodInvocation.Instance(
                                        instanceMapField,
                                        "get",
                                        SCJavaDefn.EXECUTION_CONTEXT_VAR,
                                        JavaTypeName.OBJECT,
                                        JavaTypeName.OBJECT,
                                        MethodInvocation.InvocationType.INTERFACE));
                        JavaStatement decl = new JavaStatement.LocalVariableDeclaration(newInstanceVar, initializer);
                        b.addStatement(decl);

                        // If no instance exists for the execution context create one and cache it.

                        // newInstance == null
                        JavaExpression comparison = new JavaExpression.OperatorExpression.Binary(JavaOperator.EQUALS_OBJECT, newInstanceVar, JavaExpression.LiteralWrapper.NULL);

                        // newInstance = new RTFullApp.General_0($instance);
                        // $instancesMap.put($ec, newInstance);
                        JavaStatement.Block then = new JavaStatement.Block();
                        then.addStatement(new ExpressionStatement(new JavaExpression.Assignment(newInstanceVar, new JavaExpression.ClassInstanceCreationExpression(_0TypeName, instanceField, JavaTypeNames.RTSUPERCOMBINATOR))));
                        JavaExpression cacheValue = new MethodInvocation.Instance(
                                instanceMapField,
                                "put",
                                new JavaExpression[] {
                                        SCJavaDefn.EXECUTION_CONTEXT_VAR,
                                        newInstanceVar },
                                new JavaTypeName[] {
                                        JavaTypeName.OBJECT,
                                        JavaTypeName.OBJECT },
                                JavaTypeName.OBJECT,
                                MethodInvocation.InvocationType.INTERFACE);
                        then.addStatement(new ExpressionStatement(cacheValue));

                        // Put the whole if expression together.
                        JavaStatement ifthen = new JavaStatement.IfThenElseStatement(comparison, then);
                        b.addStatement(ifthen);
                        b.addStatement(new ReturnStatement(newInstanceVar));

                        javaMethod.addStatement(b);
                        break;
                    } else if (mf.getArity() == 0) {

                        Block b = new Block();

                        // This is an unsafe method.  i.e. a non-CAF function of arity zero.  Since the
                        // function can have side effects we need to create a new instance of the class
                        // each time so that a previously evaluated value doesn't get re-used in place
                        // of actually executing the function.
                        JavaExpression.LocalVariable newInstanceVar = new JavaExpression.LocalVariable("newInstance", returnType);
                        JavaExpression initializer = new ClassInstanceCreationExpression(_0TypeName, instanceField, JavaTypeNames.RTSUPERCOMBINATOR);
                        JavaStatement decl = new JavaStatement.LocalVariableDeclaration(newInstanceVar, initializer);
                        b.addStatement(decl);
                        b.addStatement(new ReturnStatement(newInstanceVar));

                        javaMethod.addStatement(b);
                        break;
                    }
                }

            } else {
                SwitchStatement switchStatement =
                    new SwitchStatement(new MethodVariable("scIndex"));

                for (final MachineFunction mf : functions.getTopLevelCALFunctions()) {
                    JavaField instanceField = new JavaField.Static(className, CALToJavaNames.getInstanceFieldName(mf.getQualifiedName(), module), className);
                    int functionIndex = functions.getFunctionIndex(mf.getName());
                    if (mf.isCAF()) {

                        Block b = new Block();
                        // This is a CAF (constant applicative form) we want to use the cached instance associated
                        // with the execution context, if it exists.
                        // If it doesn't exist we will create an instance and add it to the cache.

                        String instanceMapFieldName = functions.getFNamePrefix(mf.getName()) + "$instancesMap";
                        JavaField instanceMapField =
                            new JavaField.Static(className, instanceMapFieldName, JavaTypeName.MAP);


                        // RTFunction newInstance = (RTFunction)$instancesMap.get($ec);
                        JavaExpression.LocalVariable newInstanceVar = new JavaExpression.LocalVariable("newInstance", returnType);
                        JavaExpression initializer =
                                new JavaExpression.CastExpression(returnType, new MethodInvocation.Instance (instanceMapField,
                                                    "get",
                                                    SCJavaDefn.EXECUTION_CONTEXT_VAR,
                                                    JavaTypeName.OBJECT,
                                                    JavaTypeName.OBJECT,
                                                    MethodInvocation.InvocationType.INTERFACE));
                        JavaStatement decl = new JavaStatement.LocalVariableDeclaration(newInstanceVar, initializer);
                        b.addStatement(decl);

                        // If no instance exists for the execution context create one and cache it.

                        // newInstance == null
                        JavaExpression comparison = new JavaExpression.OperatorExpression.Binary(JavaOperator.EQUALS_OBJECT, newInstanceVar, JavaExpression.LiteralWrapper.NULL);

                        // newInstance = new RTFullApp.General_0($instance);
                        // $instancesMap.put($ec, newInstance);
                        JavaStatement.Block then = new JavaStatement.Block();
                        then.addStatement(new ExpressionStatement(new JavaExpression.Assignment(newInstanceVar, new JavaExpression.ClassInstanceCreationExpression(_0TypeName, instanceField, JavaTypeNames.RTSUPERCOMBINATOR))));
                        JavaExpression cacheValue =
                            new MethodInvocation.Instance(instanceMapField,
                                                 "put",
                                                 new JavaExpression[]{SCJavaDefn.EXECUTION_CONTEXT_VAR, newInstanceVar},
                                                 new JavaTypeName[]{JavaTypeName.OBJECT, JavaTypeName.OBJECT},
                                                 JavaTypeName.OBJECT,
                                                 MethodInvocation.InvocationType.INTERFACE);
                        then.addStatement(new ExpressionStatement(cacheValue));

                        // Put the whole if expression together.
                        JavaStatement ifthen = new JavaStatement.IfThenElseStatement(comparison, then);
                        b.addStatement(ifthen);
                        b.addStatement(new ReturnStatement(newInstanceVar));

                        switchStatement.addCase(
                                new SwitchStatement.IntCaseGroup(
                                        functionIndex,
                                        b));

                    }

                }

                MethodInvocation mi =
                    new MethodInvocation.Static (JavaTypeNames.RTVALUE,
                                                   "badValue",
                                                   new JavaExpression[]{LiteralWrapper.NULL, LiteralWrapper.make("Illegal fall through to default case in " + functions.getFunctionGroupQualifiedName() + ".make().")},
                                                   new JavaTypeName[]{JavaTypeName.ERRORINFO, JavaTypeName.STRING},
                                                   JavaTypeNames.RTVALUE);
                switchStatement.addCase(
                        new SwitchStatement.DefaultCase(new ReturnStatement(new CastExpression(JavaTypeNames.RTFUNCTION, mi))));



                javaMethod.addStatement(switchStatement);
            }
        }


        /**
         * Create the no-argument constructor.
         *      private ThisClass(int scIndex) {
         *      }
         */
        private void createConstructor() {
            int modifiers = Modifier.PRIVATE;

            // Add the method to the class.
            if (functions.getNFunctions() <= 1) {
                JavaConstructor javaConstructor =
                    new JavaConstructor(modifiers,
                                        new String[]{},
                                        new JavaTypeName[]{},
                                        ((JavaTypeName.Reference.Object)className).getBaseName());
                    javaClassRep.addConstructor(javaConstructor);
            } else {
                JavaConstructor javaConstructor =
                new JavaConstructor(modifiers,
                                    new String[]{"scIndex", "arityValue"},
                                    new JavaTypeName[]{JavaTypeName.INT, JavaTypeName.INT},
                                    ((JavaTypeName.Reference.Object)className).getBaseName());
                javaClassRep.addConstructor(javaConstructor);

                // Now fill in the constructor body.

                // assign the tag field.
                JavaField tagField = new JavaField.Instance(null, SCDefinitionBuilder.functionTagFieldName, JavaTypeName.INT);
                MethodVariable tagArgument = new MethodVariable("scIndex");
                JavaExpression.Assignment tagAssign = new JavaExpression.Assignment(tagField, tagArgument);
                javaConstructor.addStatement(new ExpressionStatement(tagAssign));

                // asign the arity.
                JavaExpression.Assignment assignArity =
                    new JavaExpression.Assignment(
                            new JavaField.Instance(null, "arity", JavaTypeName.INT),
                            new MethodVariable("arityValue"));

                javaConstructor.addStatement (new ExpressionStatement (assignArity));
            }
        }


        /**
         * Create the getArity() method.
         *     public final int getArity() {return (javaDefn.getArity());}
         */
        private void createMethod_getArity() {
            int modifiers = Modifier.PUBLIC | Modifier.FINAL;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeName.INT, "getArity");
            javaClassRep.addMethod(javaMethod);

            // Add the body
            if (functions.getNFunctions() > 1) {
                JavaField arityField = new JavaField.Instance(null, "arity", JavaTypeName.INT);
                javaMethod.addStatement(new ReturnStatement(arityField));
            } else {
                int arity = functions.getTopLevelCALFunctions().iterator().next().getArity();
                javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make(Integer.valueOf(arity))));
            }
        }


        /**
         * Create the f() method for the non-fully-saturated case.
         *      public final RTValue f(RTResultFunction $rootNode) throws org.openquark.cal.runtime.CALExecutor.CALExecutorException
         * @param javaDefn
         * @param consolidateFunctions
         * @throws CodeGenerationException
         */
        private void createMethod_fUnsaturated(final SCJavaDefn javaDefn,
                                               final boolean consolidateFunctions) throws CodeGenerationException {

            final int arity = javaDefn.getArity();

            final JavaMethod javaMethod;
            {
                final int modifiers = Modifier.PUBLIC | Modifier.FINAL;

                // Add the method to the class.
                final String[] argNames = new String []{ROOT_NODE, SCJavaDefn.EXECUTION_CONTEXT_NAME};
                final JavaTypeName[] argTypes = new JavaTypeName []{JavaTypeNames.RTRESULT_FUNCTION, JavaTypeNames.RTEXECUTION_CONTEXT};
                final boolean[] argFinal = new boolean[] {true, true};

                final String methodNamePrefix = functions.getFNamePrefix(javaDefn.getFunctionName());
                final String methodName = methodNamePrefix + "f";

                javaMethod = new JavaMethod(modifiers, JavaTypeNames.RTVALUE, argNames, argTypes, argFinal, methodName);

                // Add the throws declaration
                javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

                final JavaDocComment comment = new JavaDocComment(methodName);
                comment.addLine("This method implements the function logic of the CAL function " + javaDefn.getModuleName() + "." + javaDefn.getFunctionName());
                javaMethod.setJavaDocComment(comment);
            }

            javaClassRep.addMethod(javaMethod);

            if (consolidateFunctions) {

                // Extract the arguments from the application chain.
                JavaStatement argumentExtraction = generateArgumentExtractors(javaDefn);
                javaMethod.addStatement(argumentExtraction);

                final JavaExpression[] args = new JavaExpression[arity + 1];
                final JavaTypeName[] argTypes = new JavaTypeName[arity + 1];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);
                args[arity] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
                argTypes[arity] = JavaTypeNames.RTEXECUTION_CONTEXT;

                // Call fnS with the extracted arguments.
                for (int i = 0; i < arity; ++i) {
                    String javaName = javaDefn.getJavaArgumentName(i);
                    if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                        // We append the names of the arguments with $L to allow the declared name to
                        // be used with the unboxed primitive value.
                        javaName += "$L";
                    }

                    JavaTypeName argType = JavaTypeNames.RTVALUE;
                    if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                        argType = javaDefn.getArgumentTypeName(i);
                    }
                    LocalVariable lv = new LocalVariable (javaName, JavaTypeNames.RTVALUE);
                    args[i] = lv;
                    if (javaDefn.isArgStrict(i)) {
                        args[i] = SCJavaDefn.createInvocation(args[i], SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                        args[i] =
                            callLastRef(args[i], lv);
                        if (javaDefn.isArgUnboxable(i)) {
                            args[i] = SCJavaDefn.unboxValue(javaDefn.getArgumentTypeName(i), args[i]);
                            argTypes[i] = argType;
                        }
                    } else {
                        args[i] =
                            callLastRef(args[i], lv);
                    }
                }

                String methodNamePrefix2 = functions.getFnNamePrefix(javaDefn.getFunctionName());
                JavaExpression mi =
                    new MethodInvocation.Instance (
                            null,
                            methodNamePrefix2 + "f" + arity + "S",
                            args,
                            argTypes,
                            JavaTypeNames.RTVALUE,
                            MethodInvocation.InvocationType.VIRTUAL);

                javaMethod.addStatement(new JavaStatement.ReturnStatement(mi));

            } else {
                // Add statistics generation.
                addStatsBlock (javaMethod, javaDefn);


                // Extract function arguments from the application chain.
                JavaStatement argumentExtraction = generateArgumentExtractors(javaDefn);
                javaMethod.addStatement(argumentExtraction);

                javaMethod.addStatement (generateStrictArgEvaluationBlock(javaDefn));


                String[] unboxedLocalVarNames = null;
                JavaTypeName[] unboxedLocalVarTypes = null;
                if (LECCMachineConfiguration.generateDebugCode() || LECCMachineConfiguration.generateDebugCode()) {
                    unboxedLocalVarNames = new String[arity];
                    unboxedLocalVarTypes = new JavaTypeName[arity];
                    for (int i = 0; i < arity; ++i) {
                        unboxedLocalVarNames[i] = javaDefn.getJavaArgumentName(i);
                        if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                            unboxedLocalVarTypes[i] = javaDefn.getArgumentTypeName(i);
                        } else {
                            unboxedLocalVarTypes[i] = JavaTypeNames.RTVALUE;
                        }
                    }
                }

                //Add the body
                Block bodyBlock = javaDefn.genS_SC_Boxed();

                if (javaDefn.isTailRecursive()) {

                    Block loopBodyBlock = new Block();

                    if (!LECCMachineConfiguration.nonInterruptibleRuntime()) {
                        // Add a check of the quit flag at the top of the loop body.
                        loopBodyBlock.addStatement(checkForQuit());
                    }

                    if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                        loopBodyBlock.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));

                    }

                    if (LECCMachineConfiguration.generateDebugCode()) {
                        loopBodyBlock.addStatement(generateDebugCode(javaDefn, unboxedLocalVarNames, unboxedLocalVarTypes));
                    }

                    loopBodyBlock.addStatement(bodyBlock);
                    UnconditionalLoop whileStatement = new UnconditionalLoop (SCJavaDefn.TAIL_RECURSION_LOOP_LABEL, loopBodyBlock);
                    javaMethod.addStatement (whileStatement);

                } else {
                    if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                        javaMethod.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));
                    }

                    if (LECCMachineConfiguration.generateDebugCode()) {
                        javaMethod.addStatement(generateDebugCode(javaDefn, unboxedLocalVarNames, unboxedLocalVarTypes));
                    }

                    javaMethod.addStatement(bodyBlock);
                }
            }
        }

        /**
         * Generate code to reset the flags used to track let variable definition function
         * usage.
         * @param originatingFunction
         * @return the code block that resets the flags.
         */
        private Block resetLetVarFlags(String originatingFunction) {
            Block b = new Block();
            if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                Set<String> liftedFunctions = functions.getLiftedFunctionsFor(originatingFunction);
                if (liftedFunctions != null) {
                    for (final String liftedFunctionName : liftedFunctions) {
                        String flagName = CALToJavaNames.cleanSCName(liftedFunctionName) + "_flag_";

                        b.addStatement(new ExpressionStatement(new JavaExpression.Assignment(new JavaField.Instance(null, flagName, JavaTypeName.BOOLEAN), LiteralWrapper.make(Boolean.FALSE))));
                    }
                }
            }

            return b;
        }

        /**
         * @param javaDefn
         * @return A block which extracts the function arguments from an application chain.
         * @throws CodeGenerationException
         */
        private JavaStatement generateArgumentExtractors (SCJavaDefn javaDefn) throws CodeGenerationException {
            final Block argExtractorBlock = new Block();

            // Extract the arguments from the application chain.
            final int arity = javaDefn.getArity();

            if (arity > 0) {
                argExtractorBlock.addStatement(new LineComment("Arguments"));
            }

            // Add argument extractors
            for (int i = arity - 1; i >= 0; i--) {

                // The name used for the extracted variable depends on whether the argument is of primitive type
                // and strict.  If it is we append $L so that the actual name can be used for the unboxed primitive value.

                final String fixedUpVarName;
                {
                    String basicVarName = javaDefn.getJavaArgumentName(i);
                    if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                        fixedUpVarName = basicVarName + "$L";
                    } else {
                        fixedUpVarName = basicVarName;
                    }
                }

                final JavaExpression initializer;

                if (i == arity - 1) {
                    //$rootNode.getArgValue();
                    initializer = SCJavaDefn.createInvocation(METHODVAR_ROOT_NODE, SCJavaDefn.GETARGVALUE);

                } else if (i == arity - 2) {

                    if (arity > 2) {
                        //RTValue $currentRootNode;
                        argExtractorBlock.addStatement(new LocalVariableDeclaration(LOCALVAR_CURRENT_ROOT_NODE));
                        //($currentRootNode = $rootNode.prevArg()).getArgValue();
                        initializer =
                            SCJavaDefn.createInvocation(
                                new Assignment(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.createInvocation(METHODVAR_ROOT_NODE, SCJavaDefn.PREVARG)),
                                SCJavaDefn.GETARGVALUE);
                    } else {
                        //$rootNode.prevArg().getArgValue();
                        initializer =
                            SCJavaDefn.createInvocation(
                                SCJavaDefn.createInvocation(METHODVAR_ROOT_NODE, SCJavaDefn.PREVARG),
                                SCJavaDefn.GETARGVALUE);
                    }

                } else if (i == 0) {

                    //$currentRootNode.prevArg().getArgValue();
                    initializer =
                        SCJavaDefn.createInvocation(
                            SCJavaDefn.createInvocation(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.PREVARG),
                            SCJavaDefn.GETARGVALUE);

                } else {

                    //($currentRootNode = $currentRootNode.prevArg()).getArgValue();
                    initializer =
                        SCJavaDefn.createInvocation(
                            new Assignment(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.createInvocation(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.PREVARG)),
                            SCJavaDefn.GETARGVALUE);
                }

                LocalVariableDeclaration varDecl = new LocalVariableDeclaration(new LocalVariable(fixedUpVarName, JavaTypeNames.RTVALUE), initializer);

                argExtractorBlock.addStatement(varDecl);
            }

            // Make a call on the root node to free its member fields.
            // This frees them up for potential garbage collection.

            JavaStatement comment =
                new JavaStatement.LineComment("Release the fields in the root node to open them to garbage collection");
            argExtractorBlock.addStatement(comment);

            JavaExpression clearMembers =
                new MethodInvocation.Instance(
                        new MethodVariable(ROOT_NODE),
                        "clearMembers",
                        JavaTypeName.VOID,
                        MethodInvocation.InvocationType.VIRTUAL);
            argExtractorBlock.addStatement(new ExpressionStatement(clearMembers));

            return argExtractorBlock;
        }

        /**
         * Create a block of code that evaluates any arguments marked as strict.
         * @param javaDefn
         * @return the java statement (i.e. a block) that evaluates the strict arguments.
         * @throws CodeGenerationException
         */
        private JavaStatement generateStrictArgEvaluationBlock(SCJavaDefn javaDefn) throws CodeGenerationException {
            JavaStatement.Block block = new JavaStatement.Block();

            // Do evaluation of any arguments marked as strict.
            if (javaDefn.hasStrictArguments()) {
                // Do evaluation of any arguments marked as strict.
                block.addStatement(new LineComment("Evaluate any arguments marked as strict."));
                for (int argIndex = 0; argIndex < javaDefn.getArity(); argIndex++) {
                    if (javaDefn.isArgStrict(argIndex)) {
                        String fixedUpVarName = javaDefn.getJavaArgumentName(argIndex);
                        if (javaDefn.isArgUnboxable(argIndex)) {
                            // In this case the argument was named with an appended $L so that we can declare a
                            // local variable with the CAL argument name that is the unboxed primitive value.
                            JavaExpression evaluate = SCJavaDefn.createInvocation(new LocalName(fixedUpVarName + "$L", JavaTypeNames.RTVALUE), SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                            evaluate = SCJavaDefn.unboxValue(javaDefn.getArgumentTypeName(argIndex), evaluate);
                            LocalVariableDeclaration lvd = new LocalVariableDeclaration(new LocalVariable(fixedUpVarName, javaDefn.getArgumentTypeName(argIndex)), evaluate);
                            block.addStatement(lvd);
                        } else {
                            JavaExpression.Nameable argVar = new LocalName(fixedUpVarName, JavaTypeNames.RTVALUE);
                            JavaExpression evaluate = SCJavaDefn.createInvocation(argVar, SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                            Assignment assign = new Assignment(argVar, evaluate);
                            block.addStatement(new ExpressionStatement (assign));
                        }
                    }
                }
            }

            return block;
        }

        /**
         * Generate code to check the state of the quit request flag and throw an interrupt exception if it is set.
         * @return a java statement that checks if quit is requested and takes appropriate action.
         */
        private static JavaStatement checkForQuit() {
            // Call RTExecutionContext.isQuitRequest() and if true throw RTValue.InterruptException
            MethodInvocation isQuitRequested = new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR, "isQuitRequested", JavaTypeName.BOOLEAN, MethodInvocation.InvocationType.VIRTUAL);
            ThrowStatement throwStatement = new ThrowStatement(new JavaExpression.JavaField.Static(JavaTypeNames.RTVALUE, "INTERRUPT_EXCEPTION", JavaTypeName.CAL_EXECUTOR_EXCEPTION));
            IfThenElseStatement conditional = new IfThenElseStatement(isQuitRequested, throwStatement);
            return conditional;
        }

        /**
         * Create the the version of the 'f' method used when we encounter a fully saturated
         * application and the strict arguments are already evaluated.
         * @param javaDefn
         * @throws CodeGenerationException
         */
        private void createMethod_fSaturatedStrict(SCJavaDefn javaDefn) throws CodeGenerationException {
            int modifiers = Modifier.PUBLIC | Modifier.FINAL;

            // Figure out the methodName
            final int arity = javaDefn.getArity();
            String methodName = functions.getFnNamePrefix(javaDefn.getFunctionName()) + "f" + arity + "S";

            Block bodyBlock = javaDefn.genS_SC_Boxed();

            // Figure out the arg names and types.
            String[] argNames = new String[arity + 1];
            JavaTypeName[] argTypes = new JavaTypeName[arity + 1];

            // Default type of argument is RTValue
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

            // Fill in the argument names from the SC definition.
            for (int i =  0; i < arity; ++i) {
                argNames[i] = javaDefn.getJavaArgumentName(i);
            }

            // The last argument is the execution context.
            argNames[arity] = SCJavaDefn.EXECUTION_CONTEXT_NAME;
            argTypes[arity] = JavaTypeNames.RTEXECUTION_CONTEXT;

            // Try to get type info for this SC.
            for (int i = 0; i < arity; ++i) {
                if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                    argTypes[i] = javaDefn.getArgumentTypeName(i);
                }
            }

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeNames.RTVALUE, argNames, argTypes, null, methodName);
            javaClassRep.addMethod(javaMethod);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            JavaDocComment comment = new JavaDocComment(methodName);
            comment.addLine("This method implements the function logic of the CAL function " + javaDefn.getModuleName() + "." + javaDefn.getFunctionName());
            javaMethod.setJavaDocComment(comment);

            // Add statistics generation.
            addStatsBlock (javaMethod, javaDefn);

            // Add the body
            if (javaDefn.isTailRecursive()) {

                Block loopBodyBlock = new Block();

                if (!LECCMachineConfiguration.nonInterruptibleRuntime()) {
                    // Add a check of the quit flag at the top of the loop body.
                    loopBodyBlock.addStatement(checkForQuit());
                }

                if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                    loopBodyBlock.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));
                }

                if (LECCMachineConfiguration.generateDebugCode()) {
                    loopBodyBlock.addStatement(generateDebugCode(javaDefn, argNames, argTypes));
                }

                loopBodyBlock.addStatement(bodyBlock);
                UnconditionalLoop whileStatement = new UnconditionalLoop (SCJavaDefn.TAIL_RECURSION_LOOP_LABEL, loopBodyBlock);
                javaMethod.addStatement (whileStatement);
            } else {

                if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                    javaMethod.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));
                }

                if (LECCMachineConfiguration.generateDebugCode()) {
                    javaMethod.addStatement(generateDebugCode(javaDefn, argNames, argTypes));
                }

                javaMethod.addStatement(bodyBlock);
            }
        }

        /**
         * Create the the version of the 'f' method used when we encounter a fully saturated
         * application and the strict arguments are already evaluated.
         * @param javaDefn
         * @throws CodeGenerationException
         */
        private void createMethod_fUnboxedSaturatedStrict(SCJavaDefn javaDefn) throws CodeGenerationException {
            int modifiers = Modifier.PUBLIC | Modifier.FINAL;
            JavaTypeName returnType = javaDefn.getResultType();

            // Figure out the methodName
            final int arity = javaDefn.getArity();
            String methodName = functions.getFnNamePrefix(javaDefn.getFunctionName()) + "fUnboxed";
            if (arity > 0) {
                methodName = methodName + arity + "S";
            }

            Block bodyBlock = javaDefn.genS_SC_Unboxed();

            // Figure out the arg names and types.
            String[] argNames = new String[arity + 1];
            JavaTypeName[] argTypes = new JavaTypeName[arity + 1];

            // Default type of argument is RTValue
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

            // Fill in the argument names from the SC definition.
            for (int i =  0; i < arity; ++i) {
                argNames[i] = javaDefn.getJavaArgumentName(i);
            }

            // The last argument is the execution context.
            argNames[arity] = SCJavaDefn.EXECUTION_CONTEXT_NAME;
            argTypes[arity] = JavaTypeNames.RTEXECUTION_CONTEXT;

            // Try to get type info for this SC.
            for (int i = 0; i < arity; ++i) {
                if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                    argTypes[i] = javaDefn.getArgumentTypeName(i);
                }
            }

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, argNames, argTypes, null, methodName);
            javaClassRep.addMethod(javaMethod);

            // Add a comment indicating which CAL function this
            // corresponds to.
            JavaDocComment comment = new JavaDocComment(methodName);
            comment.addLine("This method implements the logic of the CAL function " + javaDefn.getModuleName() + "." + javaDefn.getFunctionName());
            comment.addLine("This version of the logic returns an unboxed value.");
            javaMethod.setJavaDocComment(comment);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            // Add statistics generation.
            addStatsBlock (javaMethod, javaDefn);

            // Add the body
            if (javaDefn.isTailRecursive()) {

                Block loopBodyBlock = new Block();

                if (!LECCMachineConfiguration.nonInterruptibleRuntime()) {
                    // Add a check of the quit flag at the top of the loop body.
                    loopBodyBlock.addStatement(checkForQuit());
                }

                if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                    loopBodyBlock.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));
                }

                if (LECCMachineConfiguration.generateDebugCode()) {
                    loopBodyBlock.addStatement(generateDebugCode(javaDefn, argNames, argTypes));
                }

                loopBodyBlock.addStatement(bodyBlock);
                UnconditionalLoop whileStatement = new UnconditionalLoop (SCJavaDefn.TAIL_RECURSION_LOOP_LABEL, loopBodyBlock);
                javaMethod.addStatement (whileStatement);
            } else {

                if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                    javaMethod.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));
                }

                if (LECCMachineConfiguration.generateDebugCode()) {
                    javaMethod.addStatement(generateDebugCode(javaDefn, argNames, argTypes));
                }

                javaMethod.addStatement(bodyBlock);
            }
        }

        /**
         * Create the the version of the 'f' method used when we encounter a fully saturated
         * application and the strict arguments aren't already evaluated.
         * @param javaDefn
         * @param consolidateFunctions
         * @throws CodeGenerationException
         */
        private void createMethod_fSaturatedLazy(SCJavaDefn javaDefn,
                                                 boolean consolidateFunctions) throws CodeGenerationException {
            int modifiers = Modifier.PUBLIC | Modifier.FINAL;

            // Figure out the methodName
            final int arity = javaDefn.getArity();
            String methodNamePrefix = functions.getFnNamePrefix(javaDefn.getFunctionName());
            String methodName = methodNamePrefix + "f" + arity + "L";

            // Figure out the arg names and types.
            String[] argNames = new String[arity + 1];
            JavaTypeName[] argTypes = new JavaTypeName[arity + 1];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

            for (int i =  0; i < arity; ++i) {
                // If the argument is strict and primitive add $L to the name so that we
                // can use the declared name for the primitive value.
                argNames[i] = javaDefn.getJavaArgumentName(i);
                if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                    argNames[i] = argNames[i] + "$L";
                }
            }

            argNames[arity] = SCJavaDefn.EXECUTION_CONTEXT_NAME;
            argTypes[arity] = JavaTypeNames.RTEXECUTION_CONTEXT;


            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeNames.RTVALUE, argNames, argTypes, null, methodName);
            javaClassRep.addMethod(javaMethod);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            JavaDocComment comment = new JavaDocComment(methodName);
            comment.addLine("This method implements the function logic of the CAL function " + javaDefn.getModuleName() + "." + javaDefn.getFunctionName());
            javaMethod.setJavaDocComment(comment);

            if (consolidateFunctions) {
                JavaExpression[] args = new JavaExpression[arity + 1];
                argTypes = new JavaTypeName[arity + 1];
                Arrays.fill (argTypes, JavaTypeNames.RTVALUE);

                for (int i = 0; i < arity; ++i) {
                    String varName = javaDefn.getJavaArgumentName(i);
                    if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                        varName += "$L";
                    }
                    MethodVariable mv = new MethodVariable(varName);
                    args[i] = mv;
                    if (javaDefn.hasStrictArguments() && javaDefn.isArgStrict(i)) {
                        args[i] = SCJavaDefn.createInvocation(args[i], SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
                        args[i] =
                            callLastRef(args[i], mv);
                        if (javaDefn.isArgUnboxable(i)) {
                            args[i] = SCJavaDefn.unboxValue(javaDefn.getArgumentTypeName(i), args[i]);
                            argTypes[i] = javaDefn.getArgumentTypeName(i);
                        }
                    } else {
                        args[i] =
                            callLastRef(args[i], mv);
                    }
                }

                args[arity] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
                argTypes[arity] = JavaTypeNames.RTEXECUTION_CONTEXT;

                JavaExpression mi =
                    new MethodInvocation.Instance (
                            null,
                            methodNamePrefix + "f" + arity + "S",
                            args,
                            argTypes,
                            JavaTypeNames.RTVALUE,
                            MethodInvocation.InvocationType.VIRTUAL);

                javaMethod.addStatement(new JavaStatement.ReturnStatement(mi));

            } else {

                Block bodyBlock = javaDefn.genS_SC_Boxed();

                // Add statistics generation.
                addStatsBlock (javaMethod, javaDefn);

                javaMethod.addStatement(generateStrictArgEvaluationBlock(javaDefn));

                String[] unboxedLocalVarNames = null;
                JavaTypeName[] unboxedLocalVarTypes = null;
                if (LECCMachineConfiguration.generateDebugCode() || LECCMachineConfiguration.generateDebugCode()) {
                    unboxedLocalVarNames = new String[arity];
                    unboxedLocalVarTypes = new JavaTypeName[arity];
                    for (int i = 0; i < arity; ++i) {
                        unboxedLocalVarNames[i] = javaDefn.getJavaArgumentName(i);
                        if (javaDefn.isArgStrict(i) && javaDefn.isArgUnboxable(i)) {
                            unboxedLocalVarTypes[i] = javaDefn.getArgumentTypeName(i);
                        } else {
                            unboxedLocalVarTypes[i] = JavaTypeNames.RTVALUE;
                        }
                    }
                }

                // Add the body
                if (javaDefn.isTailRecursive()) {
                    Block loopBodyBlock = new Block();

                    if (!LECCMachineConfiguration.nonInterruptibleRuntime()) {
                        // Add a check of the quit flag at the top of the loop body.
                        loopBodyBlock.addStatement(checkForQuit());
                    }

                    if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                        loopBodyBlock.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));
                    }

                    if (LECCMachineConfiguration.generateDebugCode()) {
                        loopBodyBlock.addStatement(generateDebugCode(javaDefn, unboxedLocalVarNames, unboxedLocalVarTypes));
                    }

                    loopBodyBlock.addStatement(bodyBlock);
                    UnconditionalLoop whileStatement = new UnconditionalLoop (SCJavaDefn.TAIL_RECURSION_LOOP_LABEL, loopBodyBlock);
                    javaMethod.addStatement (whileStatement);
                } else {

                    if (LECCMachineConfiguration.SANITY_CHECK_LET_VARS) {
                        javaMethod.addStatement(resetLetVarFlags(javaDefn.getFunctionName()));
                    }

                    if (LECCMachineConfiguration.generateDebugCode()) {
                        javaMethod.addStatement(generateDebugCode(javaDefn, unboxedLocalVarNames, unboxedLocalVarTypes));
                    }

                    javaMethod.addStatement(bodyBlock);
                }
            }
        }

        /**
         * If generation of statistics is turned on this method
         * adds the appropriate code to augment the statistics.
         * @param javaMethod
         * @param javaDefn
         */
        private void addStatsBlock (JavaMethod javaMethod, SCJavaDefn javaDefn) {
            if (LECCMachineConfiguration.generateStatistics()) {
                MethodInvocation mi = new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR, "incrementNMethodCalls", JavaTypeName.VOID, MethodInvocation.InvocationType.VIRTUAL);
                javaMethod.addStatement(new ExpressionStatement(mi));
            }
            if (LECCMachineConfiguration.generateCallCounts()) {
                JavaExpression args[] = new JavaExpression[2];
                JavaTypeName argTypes[] = new JavaTypeName[2];
                args[0] = LiteralWrapper.make(javaDefn.getModuleName().toSourceText());
                args[1] = LiteralWrapper.make(javaDefn.getFunctionName());
                argTypes[0] = argTypes[1] = JavaTypeName.STRING;
                MethodInvocation mi = new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR, "scCalled", args, argTypes, JavaTypeName.VOID, MethodInvocation.InvocationType.VIRTUAL);
                javaMethod.addStatement(new ExpressionStatement(mi));
            }
        }

        /**
         * Adds debug processing:
         *   Tracing that prints (when tracing, and all tracing options are enabled):
         *     -the name of the executing thread
         *     -the name of the function and the argument values, in the applicative style of CAL textual syntax.
         *   Halting on breakpoints.
         *   etc.
         *
         * Note that the processing takes place after the arguments that are plinged are evaluated to WHNF. Thus the tracing
         * does not occur at the immediate entry of the generated f function, but a bit later. This is conceptually closer
         * to what the meaning of plinged arguments in CAL source is, in that they are evaluated to WHNF prior to evaluating
         * the body of the function to WHNF. It also makes it much easier to do proper tracing in tail recursive functions for
         * each recursive call.
         *
         * @param javaDefn
         * @param argNames String[] names of the arguments to be traced, in argument order. Only the first method.getArity() names
         *    are used. Also, argNames may actually be Java local variable or method variable names: we generate code in the
         *    implementation below using LocalName to handle both cases.
         * @param argTypes JavaTypeName[] types of the traced arguments.
         * @return JavaStatement a java statement that will perform the debug processing
         */
        private JavaStatement generateDebugCode (
                final SCJavaDefn javaDefn,
                final String[] argNames,
                final JavaTypeName[] argTypes) {

            if (!LECCMachineConfiguration.generateDebugCode()) {
                throw new IllegalStateException();
            }

            // Add debug processing.  This includes things such as function tracing
            // and halting on breakpoints.

            //if ($ec.isDebugProcessingNeeded("Prelude.take")) {
            //
            //    $ec.debugProcessing("Prelude.take",
            //                new RTValue[]{CAL_Int.make(take$nElements$1), take$list$2});
            //
            //}

            //notice that for non-RTValue fields, we need to box

            //$ec.isBreakpointEnabled($functionNameField)
            JavaExpression isDebuggingNeededCheck =
                new MethodInvocation.Instance(
                        SCJavaDefn.EXECUTION_CONTEXT_VAR,
                        "isDebugProcessingNeeded",
                        LiteralWrapper.make(javaDefn.getQualifiedName().getQualifiedName()),
                        JavaTypeName.STRING,
                        JavaTypeName.BOOLEAN,
                        MethodInvocation.InvocationType.VIRTUAL);

            JavaStatement.Block debuggingNeededThenBlock = new Block();
            JavaStatement isDebuggingNeededIfStatement =
                new JavaStatement.IfThenElseStatement(isDebuggingNeededCheck, debuggingNeededThenBlock);

            final int arity = javaDefn.getArity();

            //new RTValue[]{CAL_Int.make(take$nElements$1), take$list$2}
            JavaExpression[] argValues = new JavaExpression[arity];
            for (int i = 0; i < arity; ++i) {

                String javaArgName = argNames[i];
                JavaTypeName javaArgType = argTypes[i];

                JavaExpression javaArgValue = new LocalName(javaArgName, javaArgType);
                if (!javaArgType.equals(JavaTypeNames.RTVALUE)) {
                    javaArgValue = SCJavaDefn.boxExpression(javaArgType, javaArgValue);
                }

                argValues[i] = javaArgValue;
            }

            JavaExpression argValueArrayCreation = new JavaExpression.ArrayCreationExpression(JavaTypeNames.RTVALUE, argValues);


            //$ec.debugProcessing("Prelude.take",
            //            new RTValue[]{CAL_Int.make(take$nElements$1), take$list$2}));
            JavaExpression suspend =
                new MethodInvocation.Instance(
                    SCJavaDefn.EXECUTION_CONTEXT_VAR, "debugProcessing",
                    new JavaExpression[] {
                            LiteralWrapper.make(javaDefn.getQualifiedName().getQualifiedName()),
                            argValueArrayCreation },
                    new JavaTypeName[] { JavaTypeName.STRING,
                            JavaTypeName.CAL_VALUE_ARRAY },
                    JavaTypeName.VOID,
                    MethodInvocation.InvocationType.VIRTUAL);
            debuggingNeededThenBlock.addStatement(new ExpressionStatement(suspend));

            return isDebuggingNeededIfStatement;
        }



        /**
         * Create an inner class which represents a fully saturated application of the SC to
         * evaluated/primitive values.  Add this to the existing class rep.
         * @param mf
         * @throws CodeGenerationException
         */
        private void createStrictAppClass (MachineFunction mf) throws CodeGenerationException {
            javaClassRep.addInnerClass(getStrictAppClass(mf));
        }

        /**
         * Get a new inner class representation of a fully saturated application of the SC to evaluated/primitive values.
         * @param mf
         * @return the JavaClassRep for the strict application node class
         * @throws CodeGenerationException
         */
        private JavaClassRep getStrictAppClass(MachineFunction mf) throws CodeGenerationException{

            // Get the fully-qualified superclass and class names;
            JavaTypeName superClassTypeName = JavaTypeNames.RTFULLAPP;

            // Determine whether the sc is public or package protected.
            // Construct the class access flags.
            int classModifiers = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;

            // No interfaces are implemented
            JavaTypeName[] interfaces = JavaDefinitionBuilder.EMPTY_TYPE_NAME_ARRAY;

            JavaTypeName strictAppTypeName = CALToJavaNames.createStrictInnerTypeNameFromSC(mf.getQualifiedName(), module);

            // Now instantiate the java class representation.
            JavaClassRep strictAppClassRep = new JavaClassRep(strictAppTypeName, superClassTypeName, classModifiers, interfaces);

            //add the function field, which is a reference to the function singleton.
            JavaFieldDeclaration functionFieldDec = new JavaFieldDeclaration (Modifier.PRIVATE | Modifier.FINAL, className, "function", null);
            strictAppClassRep.addFieldDeclaration(functionFieldDec);
            JavaField functionField = new JavaField.Instance (null, "function", className);

            // This class has a member field for each argument to the SC.
            JavaField[] functionArgumentMemberFields = new JavaField [mf.getArity()];
            for (int i = 0; i < mf.getArity(); ++i) {
                JavaFieldDeclaration fieldDec;
                JavaField field;
                String fieldName = CALToJavaNames.fixupVarName(mf.getParameterNames()[i]);
                if (mf.getParameterStrictness()[i] && SCJavaDefn.canTypeBeUnboxed(mf.getParameterTypes()[i])) {
                    fieldDec = new JavaFieldDeclaration (Modifier.PRIVATE, SCJavaDefn.typeExprToTypeName(mf.getParameterTypes()[i]), fieldName, null);
                    field = new JavaField.Instance (null, fieldName, SCJavaDefn.typeExprToTypeName(mf.getParameterTypes()[i]));
                } else {
                    fieldDec = new JavaFieldDeclaration (Modifier.PRIVATE, JavaTypeNames.RTVALUE, fieldName, null);
                    field = new JavaField.Instance (null, fieldName, JavaTypeNames.RTVALUE);
                }
                strictAppClassRep.addFieldDeclaration(fieldDec);
                functionArgumentMemberFields[i] = field;
            }

            // Add the constructor
            strictAppClassRep.addConstructor(createStrictAppClass_constructor(mf, functionField, functionArgumentMemberFields));

            // Add the reduce method.
            strictAppClassRep.addMethod(createStrictAppClass_method_reduce(mf, functionField, strictAppTypeName, functionArgumentMemberFields));

            // Add the clearMembers() method.
            JavaMethod clearMembers = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.VOID, "clearMembers");
            //the line "function = null;" should not be added to clearMembers. function is a reference to a singleton
            //so that clearing it doesn't actually free any memory. However, not clearing it means that the the method
            //debug_getNodeStartText works as desired even in the case when result == null and clearMembers() has been called.
            //clearMembers.addStatement(new ExpressionStatement(new Assignment(functionField, LiteralWrapper.NULL)));
            for (int i = 0; i < functionArgumentMemberFields.length; ++i) {
                if (!mf.getParameterStrictness()[i] || !SCJavaDefn.canTypeBeUnboxed(mf.getParameterTypes()[i])) {
                    Assignment a = new Assignment(functionArgumentMemberFields[i], LiteralWrapper.NULL);
                    clearMembers.addStatement(new ExpressionStatement(a));
                }
            }
            strictAppClassRep.addMethod (clearMembers);

            createAppClass_debugMethods(strictAppClassRep, functionArgumentMemberFields);

            // We need to mark this class as containing assertions.
            // This is done manually because we don't always
            // generate the inner classes at the same time
            // as the containing class.
            strictAppClassRep.setContainsAssertions();

            return strictAppClassRep;
        }

        /**
         * Generates a call to RTValue.lastRef where the
         * second argument is nulled out.
         * @param keep - the first argument to lastRef
         * @param nullOut - the reference to be assigned null and passed as the second argument to lastRef
         * @return a call to RTValue.lastRef
         */
        private JavaExpression callLastRef(JavaExpression keep, JavaExpression.Nameable nullOut) {
            return new MethodInvocation.Static(
                    JavaTypeNames.RTVALUE,
                    "lastRef",
                    new JavaExpression[]{keep, new Assignment(nullOut, LiteralWrapper.NULL)},
                    TWO_RTVALUES,
                    JavaTypeNames.RTVALUE);
        }

        /**
         * Generate the reduce method for the strict application class
         * @param mf
         * @param functionField
         * @param strictAppTypeName
         * @param functionArgumentMemberFields
         * @return the generated reduce method
         * @throws CodeGenerationException
         */
        private JavaMethod createStrictAppClass_method_reduce (MachineFunction mf,
                                                               JavaField functionField,
                                                               JavaTypeName strictAppTypeName,
                                                               JavaField[] functionArgumentMemberFields) throws CodeGenerationException {
            // Add the reduce method.
            JavaMethod reduce = new JavaMethod (Modifier.PROTECTED | Modifier.FINAL,
                                                JavaTypeNames.RTVALUE,
                                                SCJavaDefn.EXECUTION_CONTEXT_NAME,
                                                JavaTypeNames.RTEXECUTION_CONTEXT,
                                                false, "reduce");
            // Add the throws declaration
            reduce.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            // Add the body.
            // if (result == null) {
            //     setResult (
            //         function.f4S(
            //             RTValue.lastRef(arg1, arg1 = null), ...));
            // }
            // return result;

            final JavaField resultField = new JavaField.Instance(null, "result", JavaTypeNames.RTVALUE);

            OperatorExpression condition = new OperatorExpression.Binary (JavaOperator.EQUALS_OBJECT, resultField, LiteralWrapper.NULL);
            Block then = new Block();

            JavaExpression args[] = new JavaExpression[mf.getArity() + 1];
            JavaTypeName[] argTypes = new JavaTypeName[mf.getArity()+1];
            for (int i = 0; i < mf.getArity(); ++i) {
                args[i] = functionArgumentMemberFields[i];
                argTypes[i] = JavaTypeNames.RTVALUE;
                if (mf.getParameterStrictness()[i] && SCJavaDefn.canTypeBeUnboxed(mf.getParameterTypes()[i])) {
                    argTypes[i] = SCJavaDefn.typeExprToTypeName(mf.getParameterTypes()[i]);
                }
                if (argTypes[i].equals(JavaTypeNames.RTVALUE)) {
                    args[i] =
                        callLastRef(args[i], (JavaField)args[i]);
                }
            }
            args[args.length-1] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
            argTypes[argTypes.length-1] = JavaTypeNames.RTEXECUTION_CONTEXT;

            String fMethodName = functions.getFnNamePrefix(mf.getName()) + "f" + mf.getArity() + "S";
            MethodInvocation fn = new MethodInvocation.Instance (functionField, fMethodName, args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
            MethodInvocation setResult = new MethodInvocation.Instance (null, "setResult", fn, JavaTypeNames.RTVALUE, JavaTypeName.VOID,  MethodInvocation.InvocationType.VIRTUAL);
            then.addStatement(new ExpressionStatement(setResult));

            //call clearMembers(). We don't need to do this for tail recursive functions since clearMembers() will be called as
            //a result of setting the root node above.
            if (!mf.isTailRecursive()) {
                then.addStatement(new ExpressionStatement(new MethodInvocation.Instance(null, "clearMembers", JavaTypeName.VOID, MethodInvocation.InvocationType.VIRTUAL)));
            }

            reduce.addStatement(new IfThenElseStatement(condition, then));
            reduce.addStatement(new ReturnStatement(resultField));

            return reduce;
        }
         /**
         * Defines the CalValue.debug_* methods within the generated RTFullApp(S/L) inner classes.
         * @param applicationClassRep the class rep for the RTFullApp(S/L) static inner class of the RTSupercombinator derived class.
         * @param memberFields the member fields of the RTFullApp(S/L) class (does not include the function field).
         */
        private void createAppClass_debugMethods(JavaClassRep applicationClassRep, JavaField[] memberFields) {
            applicationClassRep.addMethod(createAppClass_method_debug_getNChildren(memberFields));
            applicationClassRep.addMethod(createAppClass_method_debug_getChild(memberFields));
            applicationClassRep.addMethod(createAppClass_method_debug_getNodeStartText());
            applicationClassRep.addMethod(createAppClass_method_debug_getNodeEndText());
            applicationClassRep.addMethod(createAppClass_method_debug_getChildPrefixText(memberFields));
        }

        /**
         * Create the constructor for the strict application node class.
         * @param mf
         * @param functionField
         * @param functionArgumentMemberFields
         * @return the java constructor
         * @throws CodeGenerationException
         */
        private JavaConstructor createStrictAppClass_constructor(
                MachineFunction mf,
                JavaField functionField,
                JavaField[] functionArgumentMemberFields) throws CodeGenerationException {

            String innerClassName = CALToJavaNames.createStrictInnerClassNameFromSC(mf.getQualifiedName(), module);
            String constructorName = innerClassName.substring(innerClassName.lastIndexOf("$") + 1);

            // Get arg types
            JavaTypeName[] argTypes = new JavaTypeName[mf.getArity() + 1];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
            argTypes[0] = className;

            // Figure out arg names.
            String[] argNames = new String[mf.getArity() + 1];
            MethodVariable[] argVars = new MethodVariable[argNames.length];
            argNames[0] = "$function";
            argVars[0] = new MethodVariable(argNames[0]);
            for (int i = 0; i < mf.getArity(); i++) {
                argNames[i+1] = "$" + CALToJavaNames.fixupVarName(mf.getParameterNames()[i]);
                argVars[i+1] = new MethodVariable(argNames[i+1]);
                if (mf.getParameterStrictness()[i] && SCJavaDefn.canTypeBeUnboxed(mf.getParameterTypes()[i])) {
                    argTypes[i+1] = SCJavaDefn.typeExprToTypeName(mf.getParameterTypes()[i]);
                }
            }

            // Add the constructor to the class.
            JavaConstructor javaConstructor =
                new JavaConstructor(Modifier.PUBLIC, argNames, argTypes, constructorName);

            // Add the body of the constructor

            // Check for null argument values via assert.
            JavaExpression argCheck = new OperatorExpression.Binary (JavaOperator.NOT_EQUALS_OBJECT, argVars[0], LiteralWrapper.NULL);
            for (int i = 1; i < argNames.length; ++i) {
                // We only do the null pointer check on arguments of type RTValue.
                // It is valid to have an external object (ex. String) which is null.
                if (argTypes[i].equals(JavaTypeNames.RTVALUE)) {
                    JavaExpression compareArg = new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_OBJECT, new MethodVariable(argNames[i]), LiteralWrapper.NULL);
                    argCheck = new JavaExpression.OperatorExpression.Binary(JavaOperator.CONDITIONAL_AND, argCheck, compareArg);
                }
            }
            javaConstructor.addStatement(
                    new AssertStatement(
                            argCheck,
                            new MethodInvocation.Instance(null, "badConsArgMsg", JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL),
                            JavaTypeName.STRING));

            // Assign the argument values to the class fields.
            javaConstructor.addStatement(new ExpressionStatement(new Assignment(functionField, new MethodVariable(argNames[0]))));
            for (int i = 1; i < argNames.length; i++) {
                JavaField field = functionArgumentMemberFields[i-1];
                Assignment memberAssignment = new Assignment(field, new MethodVariable(argNames[i]));
                javaConstructor.addStatement(new ExpressionStatement(memberAssignment));

            }

            return javaConstructor;
        }

        /**
         * Create an inner class which represents a fully saturated application of the SC.
         * Add this to the existing class rep.
         * @param mf
         */
        private void createLazyAppClass (MachineFunction mf) {
            javaClassRep.addInnerClass(getLazyAppClass(mf));
        }

        /**
         * Get a new inner class representation of a fully saturated application of the SC to evaluated/primitive values.
         * @param mf
         * @return the JavaClassRep for the lazy application node class
         */
        private JavaClassRep getLazyAppClass (MachineFunction mf) {

            // Get the fully-qualified superclass and class names;
            JavaTypeName superClassTypeName = JavaTypeNames.RTFULLAPP;

            // Determine whether the sc is public or package protected.
            // Construct the class access flags.
            int classModifiers = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;

            // No interfaces are implemented
            JavaTypeName[] interfaces = JavaDefinitionBuilder.EMPTY_TYPE_NAME_ARRAY;

            JavaTypeName lazyAppTypeName = CALToJavaNames.createLazyInnerTypeNameFromSC(mf.getQualifiedName(), module);

            // Now instantiate the java class representation.
            JavaClassRep lazyAppClassRep = new JavaClassRep(lazyAppTypeName, superClassTypeName, classModifiers, interfaces);

            //add the function field, which is a reference to the function singleton.
            JavaFieldDeclaration functionFieldDec = new JavaFieldDeclaration (Modifier.PRIVATE | Modifier.FINAL, className, "function", null);
            lazyAppClassRep.addFieldDeclaration(functionFieldDec);
            JavaField functionField = new JavaField.Instance (null, "function", className);

            // This class has a member field for each argument to the SC.
            JavaField[] functionArgumentMemberFields = new JavaField [mf.getArity()];
            for (int i = 0; i < mf.getArity(); ++i) {
                JavaFieldDeclaration fieldDec;
                JavaField field;
                String fieldName = CALToJavaNames.fixupVarName(mf.getParameterNames()[i]);
                fieldDec = new JavaFieldDeclaration (Modifier.PRIVATE, JavaTypeNames.RTVALUE, fieldName, null);
                field = new JavaField.Instance (null, fieldName, JavaTypeNames.RTVALUE);
                lazyAppClassRep.addFieldDeclaration(fieldDec);
                functionArgumentMemberFields[i] = field;
            }

            // Add the constructor.
            lazyAppClassRep.addConstructor(createLazyAppClass_constructor(mf, functionField, functionArgumentMemberFields));

            // Add the reduce method.
            lazyAppClassRep.addMethod(createLazyAppClass_method_reduce(mf, functionField, lazyAppTypeName, functionArgumentMemberFields));

            // Add the clearMembers() method.
            JavaMethod clearMembers = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.VOID, "clearMembers");
            //the line "function = null;" should not be added to clearMembers. function is a reference to a singleton
            //so that clearing it doesn't actually free any memory. However, not clearing it means that the the method
            //debug_getNodeStartText works as desired even in the case when result == null and clearMembers() has been called.
            //clearMembers.addStatement(new ExpressionStatement(new Assignment(functionField, LiteralWrapper.NULL)));
            for (int i = 0; i < functionArgumentMemberFields.length; ++i) {
                Assignment a = new Assignment(functionArgumentMemberFields[i], LiteralWrapper.NULL);
                clearMembers.addStatement(new ExpressionStatement(a));
            }
            lazyAppClassRep.addMethod (clearMembers);

            createAppClass_debugMethods(lazyAppClassRep, functionArgumentMemberFields);

            // We need to mark this class as containing assertions.
            // This is done manually because we don't always
            // generate the inner classes at the same time
            // as the containing class.
            lazyAppClassRep.setContainsAssertions();

            return lazyAppClassRep;
        }

        /**
         * Create the reduce method for the lazy application class.
         * @param mf
         * @param functionField
         * @param lazyAppTypeName
         * @param functionArgumentMemberFields
         * @return the java method
         */
        private JavaMethod createLazyAppClass_method_reduce(MachineFunction mf,
                                                            JavaField functionField,
                                                            JavaTypeName lazyAppTypeName,
                                                            JavaField[] functionArgumentMemberFields) {
            // Add the reduce method.
            JavaMethod reduce = new JavaMethod (Modifier.PROTECTED | Modifier.FINAL,
                                                JavaTypeNames.RTVALUE,
                                                SCJavaDefn.EXECUTION_CONTEXT_NAME,
                                                JavaTypeNames.RTEXECUTION_CONTEXT,
                                                false, "reduce");
            // Add the throws declaration
            reduce.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

           // Add the body.
            // if (result == null) {
            //     setResult (
            //         function.f4L(RTValue.lastRef(arg1, arg1 = null), ...));
            // }
            // return result;

            final JavaField resultField = new JavaField.Instance(null, "result", JavaTypeNames.RTVALUE);

            OperatorExpression condition = new OperatorExpression.Binary (JavaOperator.EQUALS_OBJECT, resultField, LiteralWrapper.NULL);
            Block then = new Block();

            JavaExpression args[] = new JavaExpression[mf.getArity() + 1];
            JavaTypeName[] argTypes = new JavaTypeName[mf.getArity()+1];
            for (int i = 0; i < mf.getArity(); ++i) {
                args[i] =
                    callLastRef(functionArgumentMemberFields[i], (JavaField)functionArgumentMemberFields[i]);
                argTypes[i] = JavaTypeNames.RTVALUE;
            }
            args[args.length-1] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
            argTypes[argTypes.length-1] = JavaTypeNames.RTEXECUTION_CONTEXT;

            String fMethodName = functions.getFnNamePrefix(mf.getName()) + "f" + mf.getArity() + "L";
            MethodInvocation fn = new MethodInvocation.Instance (functionField, fMethodName, args, argTypes, JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
            MethodInvocation setResult = new MethodInvocation.Instance (null, "setResult", fn, JavaTypeNames.RTVALUE, JavaTypeName.VOID,  MethodInvocation.InvocationType.VIRTUAL);
            then.addStatement(new ExpressionStatement(setResult));

            reduce.addStatement(new IfThenElseStatement(condition, then));
            reduce.addStatement(new ReturnStatement(resultField));

            return reduce;
        }

        /**
         * Create the constructor for the lazy application node class.
         * @param mf
         * @param functionField
         * @param functionArgumentMemberFields
         * @return the constructor
         */
        private JavaConstructor createLazyAppClass_constructor (MachineFunction mf,
                                                     JavaField functionField,
                                                     JavaField[] functionArgumentMemberFields) {
            // Add the constructor

            String innerClassName = CALToJavaNames.createLazyInnerClassNameFromSC(mf.getQualifiedName(), module);
            String constructorName = innerClassName.substring(innerClassName.lastIndexOf("$") + 1);

            // Get arg types - all RTValues.
            JavaTypeName[] argTypes = new JavaTypeName[mf.getArity() + 1];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
            argTypes[0] = className;

            // Figure out arg names and method variables.
            String[] argNames = new String[mf.getArity() + 1];
            MethodVariable argVars[] = new MethodVariable[mf.getArity() + 1];
            argNames[0] = "$function";
            argVars[0] = new MethodVariable(argNames[0]);
            for (int i = 0; i < mf.getArity(); i++) {
                argNames[i+1] = "$" + CALToJavaNames.fixupVarName(mf.getParameterNames()[i]);
                argVars[i+1] = new MethodVariable(argNames[i+1]);
            }

            // Create the constructor.
            JavaConstructor javaConstructor =
                new JavaConstructor(Modifier.PUBLIC, argNames, argTypes, constructorName);

            // Add the body of the constructor


            // We check for null argument values via assert.
            JavaExpression argCheck = new OperatorExpression.Binary (JavaOperator.NOT_EQUALS_OBJECT, argVars[0], LiteralWrapper.NULL);
            for (int i = 1; i < argNames.length; ++i) {
                JavaExpression compareArg = new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_OBJECT, argVars[i], LiteralWrapper.NULL);
                argCheck = new JavaExpression.OperatorExpression.Binary(JavaOperator.CONDITIONAL_AND, argCheck, compareArg);
            }
            javaConstructor.addStatement(
                    new AssertStatement(argCheck,
                                        new MethodInvocation.Instance(null, "badConsArgMsg", JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL),
                                        JavaTypeName.STRING));

            // Assign the constructor arguments to the class fields.
            javaConstructor.addStatement(new ExpressionStatement(new Assignment(functionField, argVars[0])));
            for (int i = 1; i < argNames.length; i++) {
                JavaField field = functionArgumentMemberFields[i-1];
                Assignment memberAssignment = new Assignment(field, argVars[i]);
                javaConstructor.addStatement(new ExpressionStatement(memberAssignment));
            }

            return javaConstructor;
        }

        /**
         * Defines the abstract method CalValue.debug_getNChildren within RTAppL and RTAppS (the method is the
         * same in the strict and lazy generated app classes).
         * @param memberFields
         * @return the java method
         */
        private JavaMethod createAppClass_method_debug_getNChildren(JavaField[] memberFields) {

            //todoBI the method is the same for Lazy and Strict App classes. This is a possible refactoring opportunity.

            //for example, for Prelude.upFromByUpToInt:

            //public final int debug_getNChildren() {
            //    if (result != null) {
            //        return super.debug_getNChildren();
            //    } else {
            //        return 3;
            //    }
            //}

            JavaMethod method = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.INT, "debug_getNChildren");

            JavaField resultField = new JavaField.Instance(null, "result", JavaTypeNames.RTVALUE);
            JavaExpression conditionExpr =
                new JavaExpression.OperatorExpression.Binary(
                    JavaOperator.NOT_EQUALS_OBJECT,
                    resultField,
                    LiteralWrapper.NULL)
                    ;

            JavaStatement thenStatement =
                new ReturnStatement(
                    new MethodInvocation.Instance(
                        null,
                        "debug_getNChildren",
                        JavaTypeNames.RTFULLAPP,
                        JavaExpression.EMPTY_JAVA_EXPRESSION_ARRAY,
                        JavaExpression.EMPTY_TYPE_NAME_ARRAY,
                        JavaTypeName.INT,
                        MethodInvocation.InvocationType.SPECIAL));

            JavaStatement elseStatement =
                new ReturnStatement(LiteralWrapper.make(Integer.valueOf(memberFields.length)));

            JavaStatement.IfThenElseStatement ifThenElseStatement =
                new JavaStatement.IfThenElseStatement (conditionExpr, thenStatement, elseStatement);

            method.addStatement(ifThenElseStatement);

            return method;
        }

        private JavaMethod createAppClass_method_debug_getChild(JavaField[] memberFields) {

            //for example, for Prelude.upFromByDownToInt this is:

            //public final CalValue debug_getChild(int childN) {
            //    if (result != null) {
            //        return super.debug_getChild(childN);
            //    }
            //
            //    switch (childN) {
            //    case 0:
            //        return CAL_Int.make(upFromByDownToInt$start$1);
            //    case 1:
            //        return CAL_Int.make(upFromByDownToInt$step$2);
            //    case 2:
            //        return CAL_Int.make(upFromByDownToInt$end$3);
            //    default:
            //        throw new IndexOutOfBoundsException();
            //    }
            //}

            JavaMethod method = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.CAL_VALUE, "childN", JavaTypeName.INT, false, "debug_getChild");

            MethodVariable childNVar = new JavaExpression.MethodVariable("childN");

            {
                //    if (result != null) {
                //        return super.debug_getChild(childN);
                //    }

                JavaField resultField = new JavaField.Instance(null, "result", JavaTypeNames.RTVALUE);
                JavaExpression conditionExpr =
                    new OperatorExpression.Binary(
                        JavaOperator.NOT_EQUALS_OBJECT,
                        resultField,
                        LiteralWrapper.NULL)
                        ;

                JavaStatement thenStatement =
                    new ReturnStatement(
                        new MethodInvocation.Instance(
                            null,
                            "debug_getChild",
                            JavaTypeNames.RTFULLAPP,
                            new JavaExpression[]{childNVar},
                            new JavaTypeName[] {JavaTypeName.INT},
                            JavaTypeName.CAL_VALUE,
                            MethodInvocation.InvocationType.SPECIAL));

                JavaStatement.IfThenElseStatement ifThenStatement =
                    new JavaStatement.IfThenElseStatement (conditionExpr, thenStatement);

                method.addStatement(ifThenStatement);
            }

            SwitchStatement switchStatement =
                new SwitchStatement(childNVar);

            for (int i = 0, nFields = memberFields.length; i < nFields; ++i) {

                JavaField javaField = memberFields[i];
                String javaFieldName = javaField.getFieldName();
                JavaTypeName javaFieldType = javaField.getFieldType();
                JavaExpression javaFieldExpr = new JavaExpression.JavaField.Instance(null, javaFieldName, javaFieldType);
                if (!javaFieldType.equals(JavaTypeNames.RTVALUE)) {
                    javaFieldExpr = SCJavaDefn.boxExpression(javaFieldType, javaFieldExpr);
                }
                switchStatement.addCase(
                    new SwitchStatement.IntCaseGroup(i, new ReturnStatement(javaFieldExpr)));
            }

            switchStatement.addCase(
                new SwitchStatement.DefaultCase(
                    new JavaStatement.ThrowStatement(
                        new JavaExpression.ClassInstanceCreationExpression(JavaTypeName.INDEX_OUT_OF_BOUNDS_EXCEPTION))));

            method.addStatement(switchStatement);

            return method;
        }

        private JavaMethod createAppClass_method_debug_getNodeStartText() {

            //public final String debug_getNodeStartText() {
            //    if (result != null) {
            //        return super.debug_getNodeStartText();
            //    } else {
            //        return "(" + function.getQualifiedName();
            //    }
            //}

            JavaMethod method = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "debug_getNodeStartText");

            JavaField resultField = new JavaField.Instance(null, "result", JavaTypeNames.RTVALUE);
            JavaExpression conditionExpr =
                new JavaExpression.OperatorExpression.Binary(
                    JavaOperator.NOT_EQUALS_OBJECT,
                    resultField,
                    LiteralWrapper.NULL)
                    ;

            JavaStatement thenStatement =
                new ReturnStatement(
                     new MethodInvocation.Instance(
                         null,
                         "debug_getNodeStartText",
                         JavaTypeNames.RTFULLAPP,
                         JavaExpression.EMPTY_JAVA_EXPRESSION_ARRAY,
                         JavaExpression.EMPTY_TYPE_NAME_ARRAY,
                         JavaTypeName.STRING,
                         MethodInvocation.InvocationType.SPECIAL));

            JavaStatement elseStatement =
                new ReturnStatement(
                    new JavaExpression.OperatorExpression.Binary(JavaOperator.STRING_CONCATENATION,
                        LiteralWrapper.make("("),
                        new MethodInvocation.Instance(
                            new JavaExpression.JavaField.Instance(null, "function", className),
                            "getQualifiedName", JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL)));

            JavaStatement.IfThenElseStatement ifThenElseStatement =
                new JavaStatement.IfThenElseStatement (conditionExpr, thenStatement, elseStatement);

            method.addStatement(ifThenElseStatement);

            return method;
        }

        private JavaMethod createAppClass_method_debug_getNodeEndText() {

            //public final String debug_getNodeEndText() {
            //    if (result != null) {
            //        return super.debug_getNodeEndText();
            //    } else {
            //       return ")";
            //    }
            //}

            JavaMethod method = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "debug_getNodeEndText");

            JavaField resultField = new JavaField.Instance(null, "result", JavaTypeNames.RTVALUE);
            JavaExpression conditionExpr =
                new JavaExpression.OperatorExpression.Binary(
                    JavaOperator.NOT_EQUALS_OBJECT,
                    resultField,
                    LiteralWrapper.NULL)
                    ;

            JavaStatement thenStatement =
                new ReturnStatement(
                    new MethodInvocation.Instance(
                        null,
                        "debug_getNodeEndText",
                        JavaTypeNames.RTFULLAPP,
                        JavaExpression.EMPTY_JAVA_EXPRESSION_ARRAY,
                        JavaExpression.EMPTY_TYPE_NAME_ARRAY,
                        JavaTypeName.STRING,
                        MethodInvocation.InvocationType.SPECIAL));

            JavaStatement elseStatement =
                new ReturnStatement(LiteralWrapper.make(")"));

            JavaStatement.IfThenElseStatement ifThenElseStatement =
                new JavaStatement.IfThenElseStatement (conditionExpr, thenStatement, elseStatement);

            method.addStatement(ifThenElseStatement);

            return method;
        }

        private JavaMethod createAppClass_method_debug_getChildPrefixText(JavaField[] memberFields) {

            //for example, if there are 2 fields:

            //public final String debug_getChildPrefixText(int childN) {
            //    if (result != null) {
            //        return super.debug_getChildPrefixText(childN);
            //    }
            //
            //    if (childN >= 0 && childN < 2) {
            //        return " ";
            //    }
            //
            //    throw new IndexOutOfBoundsException();
            //}

            JavaMethod method = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "childN", JavaTypeName.INT, false, "debug_getChildPrefixText");

            MethodVariable childNVar = new JavaExpression.MethodVariable("childN");

            {
                //    if (result != null) {
                //        return super.debug_getChildPrefixText(childN);
                //    }

                JavaField resultField = new JavaField.Instance(null, "result", JavaTypeNames.RTVALUE);
                JavaExpression conditionExpr =
                    new OperatorExpression.Binary(
                        JavaOperator.NOT_EQUALS_OBJECT,
                        resultField,
                        LiteralWrapper.NULL)
                        ;

                JavaStatement thenStatement =
                    new ReturnStatement(
                        new MethodInvocation.Instance(
                            null,
                            "debug_getChildPrefixText",
                            JavaTypeNames.RTFULLAPP,
                            new JavaExpression[]{childNVar},
                            new JavaTypeName[]{JavaTypeName.INT},
                            JavaTypeName.STRING,
                            MethodInvocation.InvocationType.SPECIAL));

                JavaStatement.IfThenElseStatement ifThenStatement =
                    new JavaStatement.IfThenElseStatement (conditionExpr, thenStatement);

                method.addStatement(ifThenStatement);
            }

            {
                //    if (childN >= 0 && childN < 2) {
                //        return " ";
                //    }

                JavaExpression conditionExpr =
                    new OperatorExpression.Binary(
                        JavaOperator.CONDITIONAL_AND,
                        new OperatorExpression.Binary(
                            JavaOperator.GREATER_THAN_EQUALS_INT,
                            childNVar,
                            LiteralWrapper.make(Integer.valueOf(0))),
                        new OperatorExpression.Binary(
                            JavaOperator.LESS_THAN_INT,
                            childNVar,
                            LiteralWrapper.make(Integer.valueOf(memberFields.length))));

                JavaStatement thenStatement =
                    new ReturnStatement(LiteralWrapper.make(" "));

                JavaStatement.IfThenElseStatement ifThenStatement =
                    new JavaStatement.IfThenElseStatement (conditionExpr, thenStatement);

                method.addStatement(ifThenStatement);
            }

            method.addStatement(
                new JavaStatement.ThrowStatement(
                    new JavaExpression.ClassInstanceCreationExpression(JavaTypeName.INDEX_OUT_OF_BOUNDS_EXCEPTION)));

            return method;
        }

        /**
         * An extension of MachineFunction used to describe lifted let variable
         * definition functions in the LECC machine.
         * @author rcypher
         */
        static final class LECCLiftedLetVarMachineFunction implements MachineFunction {

            /** Qualified name of the function. */
            private final QualifiedName functionName;

            /** arity of the function. */
            private final int arity;

            /** Parameter names of the function.*/
            private final String[] parameterNames;

            /** Parameter types of the function. */
            private final TypeExpr[] parameterTypes;

            /** Parameter strictness for the function. */
            private final boolean[] parameterStrictness;

            /** The body of the function. */
            private Expression expression;

            /** Is the function part of an Adjunct. */
            private final boolean isForAdjunct;

            /** The result type of the funciton. */
            private final TypeExpr resultType;

            /** Set of names (i.e. String) comprising the names of functions that are strongly connected to this function.*/
            private Set<String> connectedComponents = new HashSet<String>();

            /** Flag indicating that machine specific code has been generated. */
            private boolean codeGenerated = false;

            /** Can laziness be ignored when generating code for this function. */
            private boolean ignoreLaziness = false;

            /**
             * If this function can be considered an alias of another function then aliasOf
             * will hold the name of another function.
             *
             * One function is an alias of another if all references to the alias can be
             * safely replaced by references to the aliased function.  This is true when:
             *
             *   1. the body of the alias function consists solely of a call to the aliased
             *      function.
             *   2. All the arguments to the alias are passed to the aliased function in the
             *      same order as the alias's argument list
             *   3. the aliased function and the alias have the same arity
             *   4. the aliased function and the alias have compatible strictness (see below)
             *   5. the aliased function and the alias aren't defined in terms of each other
             *      (ie, there is no cycle between the two)
             *   6. the aliased function is not a 0-arity foreign function
             *
             * Ex, in the following:
             *
             *   foo x y = bar x y;
             *
             *   bar a b = baz a b;
             *
             *   baz i j k = quux i j k;
             *
             *   quux m n o = quux2 m n o;
             *
             *   quux2 p q r = baz p q r + baz r q p;
             *
             * foo is an alias of bar.
             * bar is _not_ an alias of baz, because bar and baz have different arities.
             * baz is _not_ an alias of quux, because quux is defined in terms of quux2,
             * which is defined in terms of baz.
             *
             * The strictness of an alias is compatible with that of an aliased function
             * when the same arguments are plinged, up to the last plinged argument of the
             * alias.  In other words, it's okay for an aliased function to have extra
             * plinged arguments to the right of the last pling on the alias, but otherwise
             * they must match exactly.  Ex:
             *
             *   alpha x y z = delta x y z;
             *
             *   beta x !y z = delta x y z;
             *
             *   gamma !x y z = delta x y z;
             *
             *   delta x !y !z = ...;
             *
             *
             *   epsilon x y !z = zeta x y z;
             *
             *   zeta !x y !z = ...;
             *
             * In the above code, alpha has compatible strictness with delta, because
             * alpha doesn't have any plinged arguments.
             *
             * beta has compatible strictness with delta, because the first and second
             * arguments have the same plings; the third argument doesn't need to have
             * the same strictness because the second argument is beta's last plinged
             * argument.
             *
             * gamma and delta do _not_ have compatible strictness, because gamma's
             * first argument is plinged and delta's is not.
             *
             * epsilon and zeta also don't have compatible strictness, because the
             * first argument's strictness does not match (which is important because
             * the third argument is epsilon's rightmost strict argument).
             *
             */
            private QualifiedName aliasOf;

            /**
             * Flag indicating transformation optimizations have been applied.
             * Lifted let variable machine functions are created as part of
             * final code generation. As such they can be considered as
             * already optimized.
             */
            private boolean optimized = true;

            /**
             * Flag indication that the expression used to have a call to unsafeCoerce
             */
            private boolean hadUnsafeCoerce = false;

            LECCLiftedLetVarMachineFunction (
                    QualifiedName functionName,
                    int arity,
                    String[] parameterNames,
                    TypeExpr[] parameterTypes,
                    boolean[] parameterStrictness,
                    TypeExpr resultType,
                    Expression expression,
                    boolean isForAdjunct) {

                this.functionName = functionName;
                this.arity = arity;
                this.parameterNames = parameterNames;
                this.parameterTypes = parameterTypes;
                this.parameterStrictness = parameterStrictness;
                this.expression = expression;
                this.isForAdjunct = isForAdjunct;
                this.resultType = resultType;
            }

            /**
             * @return Returns the optimized.
             */
            public boolean isOptimized() {
                return optimized;
            }

            /**
             * Mark this function as being optimized.
             */
            public void setOptimized() {
                this.optimized = true;
            }

            /**
             * @return True if the function contained a call to unsafeCoerce.
             */
            public boolean getHadUnsafeCoerce(){
                return hadUnsafeCoerce;
            }

            /**
             * Mark the funtion as having contained a call to unsafeCoerce.
             */
            public void setHadUnsafeCoerce(){
                this.hadUnsafeCoerce = true;
            }

            /**
             * Get the number of formal arguments for this supercombinator.
             * Creation date: (3/9/00 3:30:22 PM)
             * @return int
             */
            public int getNFormalParameters() {
                return parameterNames.length;
            }

            /**
             * Get the type signature for the function. This is arguments and return type.
             * @return The function type signature.
             */
            public TypeExpr[] getType(){
                TypeExpr[] argAndResultTypes = new TypeExpr[parameterTypes.length + 1];
                for(int i = 0; i < parameterTypes.length; ++i){
                    argAndResultTypes[i] = parameterTypes[i];
                }
                argAndResultTypes[parameterTypes.length] = resultType;

                argAndResultTypes = TypeExpr.copyTypeExprs(argAndResultTypes);

                return argAndResultTypes;
            }

            /**
             * @return Returns the codeGenerated.
             */
            public boolean isCodeGenerated() {
                return codeGenerated;
            }
            /**
             * @param codeGenerated The codeGenerated to set.
             */
            public void setCodeGenerated(boolean codeGenerated) {
                this.codeGenerated = codeGenerated;
            }

            /**
             * @param isTailRecursive The isTailRecursive to set.
             */
            public void setIsTailRecursive(boolean isTailRecursive) {
                // Lifted let variable definitions can never be
                // tail recursive.
                if (isTailRecursive) {
                    throw new NullPointerException("Attempt to mark lifted let variable definition functions as tail recursive.");
                }
            }

            /** {@inheritDoc} */
            public int compareTo (MachineFunction o) {
                return getName().compareTo(o.getName());
            }

            @Override
            public boolean equals (Object o) {
                if (o == null || !(o instanceof MachineFunction)) {
                    return false;
                } else {
                    return getName().equals(((MachineFunction)o).getName());
                }
            }

            @Override
            public int hashCode () {
                return getName().hashCode();
            }

            /**
             * @return Returns the isTailRecursive flag.
             */
            public boolean isTailRecursive() {
                return false;
            }

            /** Return the qualified name for this label.
             * @return QualifiedName
             */
            public QualifiedName getQualifiedName () {
                return functionName;
            }

            /**
             * Return the name for this label
             * @return String
             */
            public String getName() {
                return getQualifiedName().getUnqualifiedName();
            }

            /**
             * @return true if this CAL function is marked as being valid for
             * eager evaluation.
             */
            public boolean canFunctionBeEagerlyEvaluated () {
                return false;
            }

            /**
             * Returns the arity of the code associated with this label.
             * @return in The arity of the code associated with this label.
             */
            public int getArity () {
                return arity;
            }

            /**
             * @return Returns the connectedComponent.
             */
            public Set<String> getStronglyConnectedComponents() {
                return this.connectedComponents;
            }

            /**
             * @param connectedComponents (String set) The connectedComponents to set. Cannot be null.
             */
            public void setStronglyConnectedComponents(Set<String> connectedComponents) {
                this.connectedComponents = connectedComponents;
            }

            /**
             * @return Returns the aliasOf.
             */
            public QualifiedName getAliasOf() {
                return aliasOf;
            }

            public void setAliasOf(QualifiedName name) {
                this.aliasOf = name;
            }

            /**
             * @return true if this is a primitive function, false otherwise.
             */
            public boolean isPrimitiveFunction() {
                return false;
            }

            /**
             * @return true if this is a foreign function, false otherwise.
             */
            public boolean isForeignFunction () {
                return false;
            }

            /**
             * @return true if this is a CAL function (i.e. not primitive and not foreign), false otherwise.
             */
            public boolean isCALFunction () {
                return true;
            }

            /**
             * @return true if this is a CAL Data Constructor.
             */
            public boolean isDataConstructor () {
                return false;
            }

            /**
             * A CAF is a constant applicative form.
             * Currently zero arity CAL functions are CAFs but foreign functions are not.
             * This is because a zero arity foreign function can return a different value
             * each time it is evaluated (ex. a construtor).
             * @return true if this is a constant applicative form.
             */
            public boolean isCAF () {
                return false;
            }

            /**
             * Return the timestamp associated with this entity.
             * @return long
             */
            public long getTimeStamp() {
                throw new UnsupportedOperationException("LECCLiftedLetVarMachineFunction does not support getTimeStamp()");
            }

            /**
             * @return strictness info for the arguments.
             */
            public boolean[] getParameterStrictness() {
                return parameterStrictness;
            }

            /**
             * @return types of the arguments.
             */
            public TypeExpr[] getParameterTypes() {
                return parameterTypes;
            }

            /**
             * @return Returns the expressionForm.
             */
            public Expression getExpressionForm() {
                return expression;
            }

            /**
             * Set the expression form of this function.
             * @param e
             */
            public void setExpression(Expression e) {
                this.expression = e;
            }

            /**
             * @return Returns the parameterNames.
             */
            public String[] getParameterNames() {
                return parameterNames;
            }

            @Override
            public String toString () {
                StringBuilder sb = new StringBuilder();
                sb.append (getQualifiedName());
                sb.append (": arity = ");
                sb.append (getArity());
                return sb.toString();
            }

            /**
             * @return The result type of this function.
             */
            public TypeExpr getResultType () {
                return resultType;
            }

            /**
             * @param function
             * @return true if this is strongly connected to function.
             */
            public boolean isStronglyConnectedTo(String function) {
                return false;
            }

            /**
             * Derived classes can override this to generate appopriate disassembly.
             * @return the disassembled form of the function.
             */
            public String getDisassembly () {
                return "Unable to disassemble function " + getQualifiedName() + ".";
            }

            /**
             * @return Returns the coreFunction.
             */
            public CoreFunction getCoreFunction() {
                throw new UnsupportedOperationException("LECCLiftedLetVarMachineFunction does not support getCoreFunction().");
            }

            /**
             * Write this MachineFunction instance out to the RecordOutputStream.
             * @param s
             */
            public void write (RecordOutputStream s) {
                throw new UnsupportedOperationException("LECCLiftedLetVarMachineFunction does not support write(RecordOutputStream).");
            }

            /**
             * Reads this MachineFunction instance.
             * Read position in the stream is before record header.
             * @param s
             * @param mti
             * @param msgLogger the logger to which to log deserialization messages.
             */
            protected void read (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger)  {
                throw new UnsupportedOperationException("LECCLiftedLetVarMachineFunction does not support read(RecordInputStream, ModuleTypeInfo, CompilerMessageLogger).");
            }

            /**
             * Reads content of this MachineFunction instance.
             * Read position in the stream is after record header.
             * @param s
             * @param schema
             * @param mti
             * @param msgLogger the logger to which to log deserialization messages.
             */
            protected void readContent (RecordInputStream s, int schema, ModuleTypeInfo mti, CompilerMessageLogger msgLogger)  {
                throw new UnsupportedOperationException("LECCLiftedLetVarMachineFunction does not support read(RecordInputStream, int, ModuleTypeInfo, CompilerMessageLogger).");
            }

            /**
             * @return literal value if this supercombinator is defined as a literal value, null otherwise
             */
            public Object getLiteralValue() {
                return null;
            }

            /**
             * @return true if this MachineFunction corresponds to an adjunct function.
             */
            public boolean isForAdjunct ()  {
                return isForAdjunct;
            }

            boolean canIgnoreLaziness() {
                return ignoreLaziness;
            }

            void setIgnoreLaziness(boolean ignoreLaziness) {
                this.ignoreLaziness = ignoreLaziness;
            }


        }


    }



    /**
     * A DataTypeDefinitionBuilder builds an internal object representation of a Java class file for a CAL data type.
     * @author Edward Lam
     */
    static class DataTypeDefinitionBuilder {

        /** The type name of the Java class for the data type. */
        private final JavaTypeName className;

        /** The Java class representation for the data type. */
        private JavaClassRep javaClassRep;

        /**
         * The LECCModule instance corresponding to either the module defining
         * the entity. This is used for obtaining the appropriate
         * {@link LECCModule.ClassNameMapper} for use in mapping names.
         */
        private final LECCModule module;

        /** (List of DataConstructor) the list of data constructors for this type. */
        private final List<DataConstructor> dataConsList;

        /** (Set of FieldName) The field names calculated to be common to all data constructors.
         * i.e. same name, same type, same strictness */
        private Set<FieldName> commonFieldNames = null;

        /** The type for which this builder is responsible. */
        private final TypeConstructor typeConstructor;

        /** Object used to collect code generation info.  May be null. */
        private final CodeGenerationStats codeGenerationStats;

        /**
         * Constructor for a DataTypeDefinitionBuilder.
         * @param typeCons the type constructor.
         * @param module the LECCModule instance corresponding to the module defining the entity.
         *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
         * @param codeGenerationStats - object used to collect code generation information.  May be null.
         */
        private DataTypeDefinitionBuilder(TypeConstructor typeCons, LECCModule module, CodeGenerationStats codeGenerationStats) {
            if (typeCons == null) {
                throw new IllegalArgumentException ("Unable to create DataTypeDefinitionBuilder: null argument.");
            }
            // eg. "org.openquark.cal.internal.runtime.lecc.cal_Prelude.TYPE_Maybe"
            this.className = CALToJavaNames.createTypeNameFromType(typeCons, module);
            this.module = module;

            this.dataConsList = new ArrayList<DataConstructor>(typeCons.getNDataConstructors());
            for (int i = 0; i < typeCons.getNDataConstructors(); ++i) {
                this.dataConsList.add(typeCons.getNthDataConstructor(i));
            }

            this.typeConstructor = typeCons;
            this.codeGenerationStats = codeGenerationStats;
        }

        /**
         * Get the object representation for a Java class representing a data or type constructor by name.
         * The returned class will not have any inner class info set.
         *
         * @param typeConstructor the relevant type constructor
         * @param unqualifiedClassName the unqualified class name (ie. without the package name)
         * @param module
         * @param codeGenerationStats
         * @return the corresponding class representation, or null if the name is not a class built by this builder.
         * @throws CodeGenerationException
         */
        static JavaClassRep getClassRep(TypeConstructor typeConstructor, String unqualifiedClassName, LECCModule module, CodeGenerationStats codeGenerationStats) throws CodeGenerationException {
            DataTypeDefinitionBuilder builder = new DataTypeDefinitionBuilder(typeConstructor, module, codeGenerationStats);

            if (unqualifiedClassName.equals(CALToJavaNames.createClassNameFromType(typeConstructor, module))) {
                return builder.getOuterClassRep();
            }
            if (unqualifiedClassName.equals(CALToJavaNames.createUnqualifiedClassNameForTagDCFromType(typeConstructor, module))) {
                return builder.getTagDCClass();
            }
            for (int i = 0, nDataConstructors = typeConstructor.getNDataConstructors(); i < nDataConstructors; i++) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                if (unqualifiedClassName.equals(CALToJavaNames.createClassName(dc, module))) {
                    return builder.getDCClass(dc);
                } else if (unqualifiedClassName.equals(CALToJavaNames.createFieldSelectionClassNameFromDC(dc, module))) {
                    return builder.getDCFieldSelectionClass(dc);
                }
            }

            return null;
        }


        /**
         * Get the Java representation for the given data type.
         * This will include inner class info.
         *
         * @param typeCons the type constructor.
         * @param module the LECCModule instance corresponding to the module defining the entity.
         *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
         * @param codeGenerationStats - object used to collect code generation information.  May be null.
         * @return JavaClassRep
         * @throws CodeGenerationException
         */
        static JavaClassRep getDataTypeDefinition(TypeConstructor typeCons, LECCModule module, CodeGenerationStats codeGenerationStats) throws CodeGenerationException {
            DataTypeDefinitionBuilder instance = new DataTypeDefinitionBuilder (typeCons, module, codeGenerationStats);
            return instance.generateDataTypeDefinition();
        }

        /**
         * Generate the java class representation of the data type for which this builder is responsible.
         * This representation will contain the relevant inner classes.
         *
         * @return the generated class representation.
         * @throws CodeGenerationException
         */
        private JavaClassRep generateDataTypeDefinition() throws CodeGenerationException {
            if (javaClassRep == null) {
                generateOuterTypeDefinition();

                // If there is more than one zero arity data constructor in this data type
                // we create a derived class called TagDC which holds an int tag field to
                // indicate which data constructor it corresponds to.  We than create a single instance
                // of this class for each of the zero arity data constructors.  This avoids
                // proliferation of classes.
                boolean createdTagDC = false;
                int nZeroArityDCs = 0;
                for (final DataConstructor dc : dataConsList) {

                    if (dc.getArity() == 0) {
                        nZeroArityDCs++;

                        if (nZeroArityDCs > 1) {
                            createClass_tagDC();
                            createdTagDC = true;
                            break;
                        }
                    }
                }

                // Add inner classes for the data constructors.
                for (final DataConstructor dc : dataConsList) {
                    if (dc.getArity() > 0 || !createdTagDC) {
                        JavaClassRep dataConsClassRep = (new DataConsDefinitionBuilder(dc, className, commonFieldNames, module)).generateDataConsDefinition();
                        javaClassRep.addInnerClass(dataConsClassRep);
                    }
                }
            }

            return javaClassRep;
        }

        /**
         * Get the object representation for a Java class representing a data constructor by name.
         * @param dc the data constructor whose definition to return.
         * @return the class representation for the data constructor.
         * @throws CodeGenerationException
         */
        private JavaClassRep getDCClass(DataConstructor dc) throws CodeGenerationException {
            if (commonFieldNames == null) {
                setCommonFieldNames(new HashMap<FieldName, JavaTypeName>(), new HashMap<FieldName, Boolean>());
            }

            return (new DataConsDefinitionBuilder(dc, className, commonFieldNames, module)).getDataConsDefinition();
        }

        /**
         * Retrieve the inner FieldSelection class for the provided DC.
         * @param dc
         * @return The FieldSelection inner class.
         * @throws CodeGenerationException
         */
        private JavaClassRep getDCFieldSelectionClass (DataConstructor dc) throws CodeGenerationException {
            return (new DataConsDefinitionBuilder(dc, className, commonFieldNames, module)).getDCFieldSelectionClass();
        }

        /**
         * Get the object representation for a Java class representing the type constructor for this class.
         * This will not have any inner class info set.
         *
         * @return the class representation of the type constructor for this class.
         * @throws CodeGenerationException
         */
        private JavaClassRep getOuterClassRep() throws CodeGenerationException {
            if (this.javaClassRep == null) {
                generateOuterTypeDefinition();
            }
            return javaClassRep;
        }

        /**
         * Generate the java class representation of the data type for which this builder is responsible.
         * The generated representation will be the outermost class definition only -- no inner classes will have been generated.
         * @throws CodeGenerationException
         */
        private void generateOuterTypeDefinition() throws CodeGenerationException {

            if (codeGenerationStats != null) {
                codeGenerationStats.incrementDataType(dataConsList.size());
                for (final DataConstructor dc : dataConsList) {
                    codeGenerationStats.incrementDCArity(dc.getArity());
                }
            }

            int classModifiers = Modifier.ABSTRACT | Modifier.PUBLIC;

            JavaTypeName superClassTypeName = JavaTypeNames.RTCONS;

            // No interfaces are implemented
            JavaTypeName[] interfaces = JavaDefinitionBuilder.EMPTY_TYPE_NAME_ARRAY;

            this.javaClassRep = new JavaClassRep(className, superClassTypeName, classModifiers, interfaces);

            // If there is more than one zero arity data constructor in this data type
            // we create a derived class called TagDC which holds an int tag field to
            // indicate which data constructor it corresponds to.  We than create a single instance
            // of this class for each of the zero arity data constructors.  This avoids
            // proliferation of classes.
            int nTagDCs = 0;
            for (final DataConstructor dci : dataConsList) {
                if (dci.getArity() == 0) {
                    nTagDCs++;

                    if (nTagDCs > 1) {
                        createFields_tagDCs();
                        createMethod_getTagDC();
                        break;
                    }
                }
            }

            if (nTagDCs < dataConsList.size()) {
                // We need to mark this class as having
                // inner classes containing assertions.
                // This is done manually because we don't always
                // generate the inner classes at the same time
                // as the containing class.
                javaClassRep.setInnerClassContainsAssertions();
            }

            // These fields will be lifted into the class generated for the type.
            Map<FieldName, JavaTypeName> fieldNameToTypeMap = new HashMap<FieldName, JavaTypeName> ();
            Map<FieldName, Boolean> fieldNameToStrictnessMap = new HashMap<FieldName, Boolean> ();
            setCommonFieldNames(fieldNameToTypeMap, fieldNameToStrictnessMap);

            // Generate fields in this class for any fields which are common to all data constructors.
            create_Fields(commonFieldNames, fieldNameToTypeMap, fieldNameToStrictnessMap);

            javaClassRep.addConstructor(createDefaultConstructor(commonFieldNames, fieldNameToTypeMap, fieldNameToStrictnessMap));
            if (commonFieldNames.size() > 0) {
                javaClassRep.addConstructor(createConstructor_allArgs(commonFieldNames, fieldNameToTypeMap, fieldNameToStrictnessMap));
            }

            Map<String, Map<String, FieldTypeAndStrictness>> fieldNameToInfo = new LinkedHashMap<String, Map<String, FieldTypeAndStrictness>>();
            for (final DataConstructor dc : dataConsList) {
                TypeExpr[] fieldTypes = SCJavaDefn.getFieldTypesForDC (dc);

                for (int i = 0; i < dc.getArity(); ++i) {
                    TypeExpr fieldTypeExpr = fieldTypes[i];
                    if (SCJavaDefn.canTypeBeUnboxed(fieldTypeExpr)) {
                        String fieldName = SCJavaDefn.getJavaFieldNameFromDC(dc, i);
                        Map<String, FieldTypeAndStrictness> infoMap = fieldNameToInfo.get(fieldName);
                        if (infoMap == null) {
                            infoMap = new LinkedHashMap<String, FieldTypeAndStrictness>();
                            fieldNameToInfo.put (fieldName, infoMap);
                        }

                        // 0 - means all fields with the name/type are lazy
                        // 1 - means all fields with the name/type are strict
                        // -1 - means some are strict some are lazy
                        String fieldTypeName = SCJavaDefn.getNameForPrimitive(fieldTypeExpr);
                        FieldTypeAndStrictness tas = infoMap.get(fieldTypeName);
                        if (tas == null) {
                            tas = new FieldTypeAndStrictness (dc.getNthFieldName(i), fieldName, fieldTypeExpr, dc.isArgStrict(i) ? 1 : 0);
                            infoMap.put (fieldTypeName, tas);
                        } else {
                            if ((dc.isArgStrict(i) && tas.strictness != 1) ||
                                (!dc.isArgStrict(i) && tas.strictness != 0)) {
                                tas.strictness = -1;
                            }
                        }
                    }
                }
            }

            // Create a get_FieldName method for each unique field name in the set of
            // data constructors.  This is the accessor that returns an RTValue.
            // If the field is common to all DCs it will be implemented at this
            // level.  Otherwise the implementation at this level throws an error
            // and the DC classes are expected to override it.
            Set<String> doneFieldNames = new HashSet<String>();
            for (final DataConstructor dc : dataConsList) {

                TypeExpr[] fieldTypes = SCJavaDefn.getFieldTypesForDC(dc);

                for (int i = 0; i < dc.getArity(); ++i) {
                    String fieldName = SCJavaDefn.getJavaFieldNameFromDC(dc, i);
                    if (!doneFieldNames.contains(fieldName)) {
                        createMethod_getBoxedField (fieldName,
                                dc.isArgStrict(i),
                                dc.isArgStrict(i) ? fieldTypes[i] : null,
                                commonFieldNames.contains(dc.getNthFieldName(i)));
                        doneFieldNames.add(fieldName);
                    }
                }
            }

            // Create a get_FieldName method for each unique field name/primitive type
            // combination in the data constructors. This returns an unboxed value.
            // These will be overridden by the data constructor classes for fields
            // which are not common.
            for (final Map.Entry<String, Map<String, FieldTypeAndStrictness>> entry : fieldNameToInfo.entrySet()) {

                String fieldName = entry.getKey();
                Map<String, FieldTypeAndStrictness> infoMap = entry.getValue();

                for (final Map.Entry<String, FieldTypeAndStrictness> typeEntry : infoMap.entrySet()) {

                    FieldTypeAndStrictness tas = typeEntry.getValue();
                        // If all instances of this field/type are strict we create an unboxed
                        // accessor.
                    createMethod_getUnBoxedField(fieldName,
                            tas.type,
                            tas.strictness == 1,
                            commonFieldNames.contains(tas.fieldName));

                }
            }

            createMethod_getDCNameByOrdinal();
        }

        /**
         * Calculate which fields are common to all data constructors - i.e. same name, same type, same strictness
         * Set this into commonFieldNames.
         *
         * @param fieldNameToType (FieldName->JavaTypeName) this map will be populated with mappings from field name to type
         * @param fieldNameToStrictness (FieldName->Boolean) this map will be populated with mappings from field name to strictness
         * @throws CodeGenerationException
         */
        private void setCommonFieldNames(Map<FieldName, JavaTypeName> fieldNameToType, Map<FieldName, Boolean> fieldNameToStrictness) throws CodeGenerationException {
            this.commonFieldNames = new LinkedHashSet<FieldName>();
            DataConstructor firstDC = dataConsList.get(0);
            boolean[] fieldStrictness = firstDC.getArgStrictness();
            for (int i = 0; i < firstDC.getArity(); ++i) {
                TypeExpr[] fieldTypes = SCJavaDefn.getFieldTypesForDC(firstDC);
                FieldName fn = firstDC.getNthFieldName(i);
                commonFieldNames.add(fn);
                int index = firstDC.getFieldIndex(fn);
                fieldNameToType.put (fn, SCJavaDefn.typeExprToTypeName(fieldTypes[index]));
                fieldNameToStrictness.put (fn, Boolean.valueOf (fieldStrictness[i]));
            }

            for (int i = 1; i < dataConsList.size(); ++i) {
                DataConstructor dc = dataConsList.get(i);
                TypeExpr[] fieldTypes = SCJavaDefn.getFieldTypesForDC(dc);
                fieldStrictness = dc.getArgStrictness();
                Set<FieldName> commonFieldNamesClone = new HashSet<FieldName>(commonFieldNames);

                for (int j = 0, dcArity = dc.getArity(); j < dcArity; ++j) {
                    commonFieldNamesClone.remove(dc.getNthFieldName(j));
                }

                for (final FieldName fieldName : commonFieldNamesClone) {
                    commonFieldNames.remove(fieldName);
                }

                HashSet<FieldName> set = new LinkedHashSet<FieldName>();
                for (final FieldName fn : commonFieldNames) {
                    int index = dc.getFieldIndex(fn);
                    JavaTypeName jt = fieldNameToType.get(fn);
                    boolean fs = fieldNameToStrictness.get(fn).booleanValue();

                    if (fs == fieldStrictness[index] &&
                            SCJavaDefn.typeExprToTypeName(fieldTypes[index]).equals(jt)) {
                        set.add(fn);
                    }
                }
                this.commonFieldNames = set;
            }
        }

        /**
         * Create a static final field for each zero arity DC.
         */
        private void createFields_tagDCs () {
            JavaTypeName tagDCTypeName = CALToJavaNames.createTypeNameForTagDCFromType(typeConstructor, module);
            int fieldModifiers = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
            for (final DataConstructor dc : dataConsList) {
                if (dc.getArity() == 0) {
                    JavaExpression initializer = new ClassInstanceCreationExpression(tagDCTypeName, LiteralWrapper.make(Integer.valueOf(dc.getOrdinal())), JavaTypeName.INT);
                    String fieldName = CALToJavaNames.fixupVarName(dc.getName().getUnqualifiedName());
                    JavaFieldDeclaration fieldDec = new JavaFieldDeclaration(fieldModifiers, tagDCTypeName, fieldName, initializer);
                    javaClassRep.addFieldDeclaration(fieldDec);
                }
            }
        }

        /**
         * Create the default constructor for the data type class.
         * @param fieldNames
         * @param fieldNameToType
         * @param fieldNameToStrictness
         * @return the constructor.
         */
        private JavaConstructor createDefaultConstructor (Set<FieldName> fieldNames, Map<FieldName, JavaTypeName> fieldNameToType, Map<FieldName, Boolean> fieldNameToStrictness) {
            JavaConstructor constructor = new JavaConstructor (Modifier.PROTECTED, className.getUnqualifiedJavaSourceName());

            // The final fields need to be initialized to a default value.
            for (final FieldName fn : fieldNames) {
                boolean strict = fieldNameToStrictness.get(fn).booleanValue();
                if (!strict) {
                    continue;
                }
                JavaTypeName type = fieldNameToType.get(fn);
                String fieldName = SCJavaDefn.getJavaFieldNameFromFieldName(fn);

                JavaExpression assign = new JavaExpression.Assignment(new JavaField.Instance(null, fieldName, type), getDefaultValueForType(type));
                constructor.addStatement(new ExpressionStatement(assign));
            }

            return constructor;
        }

        /**
         * Create a constructor for the data type class.
         * @param fieldNames - set of FieldName representing fields lifted into the data type class
         * @param fieldNameToType - map of FieldName -> JavaTypeName
         * @param fieldNameToStrictness - map of FieldName -> boolean
         * @return the constructor for the data type class.
         */
        private JavaConstructor createConstructor_allArgs (Set<FieldName> fieldNames, Map<FieldName, JavaTypeName> fieldNameToType, Map<FieldName, Boolean> fieldNameToStrictness) {
            String[] argNames = new String [fieldNames.size()];
            JavaTypeName[] argTypes = new JavaTypeName [fieldNames.size()];
            Block constructorBody = new Block();

            int i = 0;
            for (final FieldName fn : fieldNames) {
                boolean strict = fieldNameToStrictness.get(fn).booleanValue();
                JavaTypeName type = JavaTypeNames.RTVALUE;
                if (strict) {
                    type = fieldNameToType.get(fn);
                }
                String fieldName = SCJavaDefn.getJavaFieldNameFromFieldName(fn);
                String argName = fieldName+"$";

                argNames[i] = argName;
                argTypes[i] = type;
                MethodVariable mv = new MethodVariable (argName);
                JavaExpression.JavaField.Instance field =
                    new JavaExpression.JavaField.Instance(null, fieldName, type);
                JavaExpression assign = new Assignment (field, mv);
                constructorBody.addStatement(new ExpressionStatement(assign));
                i++;
            }

            JavaConstructor constructor = new JavaConstructor (Modifier.PROTECTED, argNames, argTypes, className.getUnqualifiedJavaSourceName());
            constructor.addStatement(constructorBody);

            return constructor;
        }

        /**
         * Create fields for this class.
         * @param fieldNames - Set of FieldName
         * @param fieldNameToType - Map of FieldName -> TypeExpr
         * @param fieldNameToStrictness - Map of FieldName -> Boolean
         */
        private void create_Fields (Set<FieldName> fieldNames, Map<FieldName, JavaTypeName> fieldNameToType, Map<FieldName, Boolean> fieldNameToStrictness) {
            for (final FieldName fieldName : fieldNames) {
                boolean strict = fieldNameToStrictness.get(fieldName).booleanValue();
                JavaTypeName type = JavaTypeNames.RTVALUE;
                int modifiers = 0;
                if (strict) {
                    modifiers = Modifier.FINAL;
                    type = fieldNameToType.get(fieldName);
                }
                String javaFieldName = SCJavaDefn.getJavaFieldNameFromFieldName(fieldName);

                JavaFieldDeclaration fieldDec = new JavaFieldDeclaration (modifiers, type, javaFieldName, null);
                javaClassRep.addFieldDeclaration(fieldDec);
            }
        }

        /**
         * Create the method:
         *     protected final String getDCNameByOrdinal(int dcOrdinal)
         * This overrides the implementation in the RTCons base class.
         */
        private void createMethod_getDCNameByOrdinal() {
            JavaMethod javaMethod =
                new JavaMethod(Modifier.PROTECTED | Modifier.FINAL,
                               JavaTypeName.STRING,
                               "dcOrdinal",
                               JavaTypeName.INT,
                               false, "getDCNameByOrdinal");
            javaClassRep.addMethod(javaMethod);

            SwitchStatement sw =
                new SwitchStatement(new MethodVariable("dcOrdinal"));

            for (int i = 0, n = dataConsList.size(); i < n; ++i) {
                DataConstructor dc = dataConsList.get(i);
                SwitchStatement.IntCaseGroup icg =
                    new SwitchStatement.IntCaseGroup(
                            dc.getOrdinal(),
                            new ReturnStatement(LiteralWrapper.make(dc.getName().getUnqualifiedName())));
                sw.addCase(icg);
            }

            javaMethod.addStatement(sw);

            // If the argument doesn't match the ordinal for any DC we
            // throw an error.
            MethodInvocation badValue =
                new MethodInvocation.Static(
                        JavaTypeNames.RTVALUE,
                        "badValue_Object",
                        new JavaExpression[]{LiteralWrapper.NULL, LiteralWrapper.make("Invalid DC ordinal in getDCNameByOrdinal() for " + className.toString())},
                        new JavaTypeName[]{JavaTypeName.ERRORINFO, JavaTypeName.STRING},
                        JavaTypeName.OBJECT);

            javaMethod.addStatement (new ReturnStatement(new CastExpression(JavaTypeName.STRING, badValue)));
        }

        /**
         * Create the getTagDC(int tag) method.  This method
         * returns the TagDC instance corresponding to the given ordinal.
         */
        private void createMethod_getTagDC() {
            int modifiers = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;

            // Add the method to the class.
            JavaTypeName tagDCTypeName = CALToJavaNames.createTypeNameForTagDCFromType(typeConstructor, module);
            JavaMethod javaMethod = new JavaMethod(modifiers, tagDCTypeName, "ordinal", JavaTypeName.INT, false, "getTagDC");
            javaClassRep.addMethod(javaMethod);

            // Add the body..
            // switch (ordinal) {
            //     case 1: ...;
            // }
            SwitchStatement ordSwitch = new SwitchStatement(METHODVAR_ORDINAL);
            for (final DataConstructor dc : dataConsList) {
                if (dc.getArity() == 0) {
                    // Return the static TagDC instance for this ordinal.
                    String fieldName = CALToJavaNames.fixupVarName(dc.getName().getUnqualifiedName());
                    JavaField field = new JavaField.Static(className, fieldName, tagDCTypeName);
                    SwitchStatement.SwitchCase sc = new SwitchStatement.IntCaseGroup(dc.getOrdinal(), new ReturnStatement(field));
                    ordSwitch.addCase(sc);
                } else {
                    // This is a valid ordinal for the data type but does not correspond to a zero arity DC.
                    LiteralWrapper badValueMessageWrapper = LiteralWrapper.make ("Attempt to treat " + dc.getName() + " as a zero arity data constructor.");
                    Block block = new Block();
                    block.addStatement(new JavaStatement.LineComment(dc.getName().getQualifiedName()));
                    JavaExpression castExpression = new CastExpression(tagDCTypeName, new MethodInvocation.Static(JavaTypeNames.RTVALUE, "badValue", badValueMessageWrapper, JavaTypeName.STRING, JavaTypeNames.RTVALUE));
                    block.addStatement(new ReturnStatement(castExpression));
                    ordSwitch.addCase(new SwitchStatement.IntCaseGroup (dc.getOrdinal(), block));
                }
            }

            // Add a default case in the switch to throw an error if an invalid ordinal value is used.
            Block defaultBlock = new Block();
            LocalVariable bf = new LocalVariable("bf", JavaTypeName.STRING_BUILDER);
            defaultBlock.addStatement(new LocalVariableDeclaration (bf, new ClassInstanceCreationExpression(JavaTypeName.STRING_BUILDER)));
            LiteralWrapper badValueMessageWrapper1 = LiteralWrapper.make("Invalid ordinal value of ");
            JavaExpression message = new MethodInvocation.Instance(bf, "append", badValueMessageWrapper1, JavaTypeName.STRING, JavaTypeName.STRING_BUILDER, MethodInvocation.InvocationType.VIRTUAL);
            message = new  MethodInvocation.Instance(message, "append", METHODVAR_ORDINAL, JavaTypeName.INT, JavaTypeName.STRING_BUILDER, MethodInvocation.InvocationType.VIRTUAL);
            LiteralWrapper badValueMessageWrapper2 = LiteralWrapper.make(" in " + className.toString() + ".getTagDC().");
            message = new MethodInvocation.Instance(message, "append", badValueMessageWrapper2, JavaTypeName.STRING, JavaTypeName.STRING_BUILDER, MethodInvocation.InvocationType.VIRTUAL);
            defaultBlock.addStatement (new ExpressionStatement(message));
            message = new MethodInvocation.Instance(bf, "toString", JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL);
            defaultBlock.addStatement (new ReturnStatement(new CastExpression(tagDCTypeName, new MethodInvocation.Static(JavaTypeNames.RTVALUE, "badValue", message, JavaTypeName.STRING, JavaTypeNames.RTVALUE))));
            ordSwitch.addCase(new SwitchStatement.DefaultCase (defaultBlock));

            // Add the switch statement to the method.
            javaMethod.addStatement(ordSwitch);
        }

        /**
         * Generate an accessor function of the form RTValue get_FieldName() {}
         * @param fieldName
         * @param fieldIsStrict
         * @param fieldTypeExpr
         * @param implementAtThisLevel
         * @throws CodeGenerationException
         */
        private void createMethod_getBoxedField (String fieldName,
                                                 boolean fieldIsStrict,
                                                 TypeExpr fieldTypeExpr,
                                                 boolean implementAtThisLevel) throws CodeGenerationException {
            int modifiers = Modifier.PUBLIC;
            JavaTypeName fieldType = SCJavaDefn.typeExprToTypeName(fieldTypeExpr);
            boolean primitiveType = !fieldType.equals(JavaTypeNames.RTVALUE);

            // Add the method to the class.
            String methodName = "get" + fieldName;
            JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeNames.RTVALUE, methodName);
            javaClassRep.addMethod(javaMethod);

            if (implementAtThisLevel) {
                JavaField field = new JavaField.Instance (null, fieldName, fieldType);
                if (primitiveType) {
                    javaMethod.addStatement(new ReturnStatement(SCJavaDefn.boxExpression(fieldTypeExpr, field)));
                } else {
                    if (!fieldIsStrict) {
                        // We have a non-strict field of type RTValue.  In order to reduce space usage
                        // we want to update the field value to be the result of the original suspension.
                        // If the field value is an RTResultFunction we want to re-assign the field with
                        // a call to 'getValue()'.
                        // For example:
                        //public final RTValue get_head() {
                        //    RTValue field;
                        //        if ((field = _head) instanceof RTResultFunction) {
                        //            return (_head = field.getValue());
                        //        }
                        //        return field;
                        //}
                        String localName = fieldName+"$";
                        LocalVariable localVariable = new LocalVariable(localName, fieldType);
                        LocalVariableDeclaration localDeclaration = new LocalVariableDeclaration(localVariable);
                        javaMethod.addStatement(localDeclaration);
                        Assignment localAssignment = new Assignment(localVariable, field);
                        InstanceOf checkType = new InstanceOf(localAssignment, JavaTypeNames.RTRESULT_FUNCTION);
                        MethodInvocation getValue = new MethodInvocation.Instance(localVariable, "getValue", JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                        Assignment fieldAssignment = new Assignment(field, getValue);
                        ReturnStatement returnNewVal = new ReturnStatement(fieldAssignment);
                        IfThenElseStatement ifThen = new IfThenElseStatement(checkType, returnNewVal);
                        javaMethod.addStatement(ifThen);
                        javaMethod.addStatement(new ReturnStatement (localVariable));
                    } else {
                        javaMethod.addStatement(new ReturnStatement (field));
                    }
                }
            } else {
                // This class should throw an error for any access.  The methods will
                // be overridden by derived classes for each data constructor.
                // We can simply call the method badFieldAccessor() implemented in
                // RTCons.
                MethodInvocation mi =
                    new MethodInvocation.Instance(
                            null,
                            "badFieldAccessor",
                            LiteralWrapper.make(fieldName),
                            JavaTypeName.STRING,
                            JavaTypeNames.RTVALUE,
                            MethodInvocation.InvocationType.VIRTUAL);

                javaMethod.addStatement (new ReturnStatement (mi));
            }
        }


        /**
         * Generate an accessor function of the form Type get_FieldName_As_Type(RTExecutionContext $ec) {}
         * @param fieldName
         * @param fieldTypeExpr
         * @param isStrict - indicates that all instances of this fieldname/type are strict
         * @param implementAtThisLevel
         * @throws CodeGenerationException
         */
        private void createMethod_getUnBoxedField (String fieldName,
                                                   TypeExpr fieldTypeExpr,
                                                   boolean isStrict,
                                                   boolean implementAtThisLevel) throws CodeGenerationException {
            int modifiers = Modifier.PUBLIC;
            JavaTypeName fieldType = SCJavaDefn.typeExprToTypeName(fieldTypeExpr);

            // Add the method to the class.
            String methodName = "get" + fieldName + "_As_" + SCJavaDefn.getNameForPrimitive(fieldType);


            JavaMethod javaMethod;
            // Don't need to pass in an execution context as this will always be evaluted.
            javaMethod = new JavaMethod(modifiers, fieldType, methodName);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            javaClassRep.addMethod(javaMethod);

            if (implementAtThisLevel && isStrict) {
                JavaField field = new JavaField.Instance (null, fieldName, fieldType);
                javaMethod.addStatement(new ReturnStatement (field));
            } else {
                // This class should throw an error for any access.  The methods will
                // be overridden by derived classes for each data constructor.
                // We want to call the RTCons method badFieldAccessor_...
                // that matches the type of the field.

                String fieldTypeString = SCJavaDefn.getNameForPrimitive(fieldType);
                String castTypeString = null;
                if (!(fieldType instanceof JavaTypeName.Primitive)) {
                    castTypeString = fieldTypeString;
                    fieldTypeString = "Object";
                }
                MethodInvocation mi =
                    new MethodInvocation.Instance(
                        null,
                        "badFieldAccessor_" + fieldTypeString,
                        LiteralWrapper.make(fieldName),
                        JavaTypeName.STRING,
                        fieldType,
                        MethodInvocation.InvocationType.VIRTUAL);

                if (castTypeString != null) {
                    javaMethod.addStatement(new ReturnStatement(new JavaExpression.CastExpression(fieldType, mi)));
                } else {
                    javaMethod.addStatement (new ReturnStatement (mi));
                }
            }
        }

        /**
         * Create an inner class 'TagDC' which is used to represent the various zero arity data constructors.
         * Add this to the existing class rep.
         */
        private void createClass_tagDC() {
            javaClassRep.addInnerClass(getTagDCClass());
        }

        /**
         * Get a new inner class 'TagDC' representation used to represent the various zero arity data constructors.
         * @return the JavaClassRep for the tag DC class
         */
        private JavaClassRep getTagDCClass() {

            // Determine access..
            // Determine access..
            int classModifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

            // No interfaces are implemented
            JavaTypeName[] interfaces = JavaDefinitionBuilder.EMPTY_TYPE_NAME_ARRAY;

            // Now instantiate the java class representation.
            JavaTypeName tagDCTypeName = CALToJavaNames.createTypeNameForTagDCFromType(typeConstructor, module);
            JavaClassRep tagDCClassRep = new JavaClassRep(tagDCTypeName, className, classModifiers, interfaces);

            // Add the body of the class.

            // private final int tag;
            JavaFieldDeclaration tagField = new JavaFieldDeclaration (Modifier.PRIVATE | Modifier.FINAL, JavaTypeName.INT, "tag", null);
            tagDCClassRep.addFieldDeclaration(tagField);

            // public TagDC(int tagVal) {this.tag = tagVal;}
            JavaConstructor javaConstructor = new JavaConstructor(Modifier.PUBLIC, new String[]{"tagVal"}, new JavaTypeName[]{JavaTypeName.INT}, "TagDC");
            tagDCClassRep.addConstructor(javaConstructor);
            Assignment tagAssign = new Assignment (new JavaField.Instance(null, "tag", JavaTypeName.INT), METHODVAR_TAGVAL);
            javaConstructor.addStatement(new ExpressionStatement(tagAssign));



            // public final int getArity() {return 0;}
            int modifiers = Modifier.PUBLIC | Modifier.FINAL;
            JavaTypeName returnType = JavaTypeName.INT;
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, "getArity");
            tagDCClassRep.addMethod(javaMethod);
            javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make(Integer.valueOf(0))));

            // public final int getOrdinalValue(){return tag;}
            modifiers = Modifier.PUBLIC | Modifier.FINAL;
            returnType = JavaTypeName.INT;
            javaMethod = new JavaMethod(modifiers, returnType, "getOrdinalValue");
            tagDCClassRep.addMethod(javaMethod);
            javaMethod.addStatement(new ReturnStatement(new JavaField.Instance(null, "tag", JavaTypeName.INT)));

            // public final String getModuleName() ...
            modifiers = Modifier.PUBLIC | Modifier.FINAL;
            returnType = JavaTypeName.STRING;
            javaMethod = new JavaMethod(modifiers, returnType, "getModuleName");
            tagDCClassRep.addMethod(javaMethod);
            javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make (typeConstructor.getName().getModuleName().toSourceText())));

            // public final String getUnqualifiedName() ...
            modifiers = Modifier.PUBLIC | Modifier.FINAL;
            returnType = JavaTypeName.STRING;
            javaMethod = new JavaMethod(modifiers, returnType, "getUnqualifiedName");
            tagDCClassRep.addMethod(javaMethod);
            SwitchStatement sw = new SwitchStatement (new JavaField.Instance(null, "tag", JavaTypeName.INT));
            for (int i = 0, nDCs = dataConsList.size(); i < nDCs; ++i) {
                DataConstructor dc = dataConsList.get (i);
                sw.addCase(new SwitchStatement.IntCaseGroup(dc.getOrdinal(), new ReturnStatement (LiteralWrapper.make(dc.getName().getUnqualifiedName()))));
            }
            javaMethod.addStatement (sw);
            javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make ("Unknown data constructor")));

            // public final String getQualfiedName() ...
            modifiers = Modifier.PUBLIC | Modifier.FINAL;
            returnType = JavaTypeName.STRING;
            javaMethod = new JavaMethod(modifiers, returnType, "getQualifiedName");
            tagDCClassRep.addMethod(javaMethod);
            sw = new SwitchStatement (new JavaField.Instance(null, "tag", JavaTypeName.INT));
            for (int i = 0, nDCs = dataConsList.size(); i < nDCs; ++i) {
                DataConstructor dc = dataConsList.get (i);
                sw.addCase(new SwitchStatement.IntCaseGroup(dc.getOrdinal(), new ReturnStatement (LiteralWrapper.make(dc.getName().getQualifiedName()))));
            }
            javaMethod.addStatement (sw);
            javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make ("Unknown data constructor")));

            return tagDCClassRep;
        }

    }

    /**
     * A DataConsDefinitionBuilder builds an internal object representation of a Java class file for a CAL data constructor.
     * @author Edward Lam
     */
    static class DataConsDefinitionBuilder {

        /** The type name of the Java class for the data constructor. */
        private final JavaTypeName className;

        /** The type name of the Java class for the type to which the data constructor belongs.
         *  This is also the data constructor's superclass. */
        private final JavaTypeName dataTypeClassName;

        /** The data constructor associated with this definition. */
        private final DataConstructor dc;

        /** The Java class representation for the data constructor. */
        private JavaClassRep javaClassRep;

        /**
         * The LECCModule instance corresponding to either the module defining
         * the entity. This is used for obtaining the appropriate
         * {@link LECCModule.ClassNameMapper} for use in mapping names.
         */
        private final LECCModule module;

        /** The name to use for the singleton instance field. */
        private final String instanceName;

        /** Flag indicating whether this data constructor has any strict fields. */
        private final boolean[] fieldStrictness;

        /** true if at least one of the fields has an unboxed form (as a Java primitive type or object type rather than an RTValue).*/
        private boolean dcHasPrimitives;

        private final TypeExpr[] fieldTypes;

        /** same cardinality as fieldTypes. The names of the fields of the data constructor as appearing in its Java class representation. */
        private final String[] javaFieldNames;

        /** (FieldName) fields that are common to all data constructors for this type. They must be indentically plinged. */
        private final Set<FieldName> commonFields;

        /**
         * Constructor for a DataConsDefinitionBuilder.
         * @param dc data constructor for which to build a definition.
         * @param dataTypeClassName the type name of the data type.
         * @param commonFields - FieldName -> fields that are common to all data constructors for this type
         * @param module the LECCModule instance corresponding to the module defining the entity.
         *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
         * @throws CodeGenerationException
         */
        DataConsDefinitionBuilder(DataConstructor dc,
                                  JavaTypeName dataTypeClassName,
                                  Set<FieldName> commonFields, LECCModule module) throws CodeGenerationException {

            if (dc == null || dataTypeClassName == null) {
                throw new IllegalArgumentException ("Unable to create DataConsDefinitionBuilder: null argument.");
            }
            this.dc = dc;
            this.dataTypeClassName = dataTypeClassName;
            this.className = CALToJavaNames.createTypeNameFromDC(dc, module);
            this.module = module;
            this.instanceName = "$instance";
            this.fieldStrictness = new boolean [dc.getArity()];
            if (LECCMachineConfiguration.IGNORE_STRICTNESS_ANNOTATIONS) {
                Arrays.fill (this.fieldStrictness, false);
            } else {
                for (int i = 0; i < dc.getArity(); ++i) {
                    this.fieldStrictness[i] = dc.isArgStrict(i);
                }
            }

            this.fieldTypes = SCJavaDefn.getFieldTypesForDC(dc);
            for (int i = 0; i < this.fieldTypes.length; ++i) {
                if (SCJavaDefn.canTypeBeUnboxed(this.fieldTypes[i])) {
                    this.dcHasPrimitives = true;
                }
            }

            this.javaFieldNames = new String[dc.getArity()];
            for (int i = 0; i < javaFieldNames.length; ++i) {
                javaFieldNames[i] = SCJavaDefn.getJavaFieldNameFromDC(dc, i);
            }

            this.commonFields = commonFields;
        }

        /**
         * Adds debug processing:
         *   Tracing that prints (when tracing, and all tracing options are enabled):
         *     -the name of the executing thread
         *     -the name of the function and the argument values, in the applicative style of CAL textual syntax.
         *   Halting on breakpoints.
         *   etc.
         *
         * Note that the processing takes place after the arguments that are plinged are evaluated to WHNF. Thus the tracing
         * does not occur at the immediate entry of the generated f function, but a bit later. This is conceptually closer
         * to what the meaning of plinged arguments in CAL source is, in that they are evaluated to WHNF prior to evaluating
         * the body of the function to WHNF. It also makes it much easier to do proper tracing in tail recursive functions for
         * each recursive call.
         *
         * @param argNames String[] names of the arguments to be traced, in argument order. Only the first method.getArity() names
         *    are used. Also, argNames may actually be Java local variable or method variable names: we generate code in the
         *    implementation below using LocalName to handle both cases.
         * @param argTypes JavaTypeName[] types of the traced arguments.
         * @return JavaStatement a java statement that will perform the debug processing
         */
        private JavaStatement generateDebugCode (String[] argNames, JavaTypeName[] argTypes) {
            if (!LECCMachineConfiguration.generateDebugCode()) {
                throw new IllegalStateException();
            }

            if (!LECCMachineConfiguration.generateDebugCode()) {
                throw new IllegalStateException();
            }

            // Add debug processing.  This includes things such as function tracing
            // and halting on breakpoints.

            //if ($ec.isDebugProcessingNeeded("Prelude.take")) {
            //
            //    $ec.debugProcessing("Prelude.take",
            //                new RTValue[]{CAL_Int.make(take$nElements$1), take$list$2});
            //
            //}

            //notice that for non-RTValue fields, we need to box

            //$ec.isBreakpointEnabled($functionNameField)
            JavaExpression isDebuggingNeededCheck = new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR, "isDebugProcessingNeeded", LiteralWrapper.make(dc.getName().getQualifiedName()), JavaTypeName.STRING, JavaTypeName.BOOLEAN, MethodInvocation.InvocationType.VIRTUAL);
            JavaStatement.Block debuggingNeededThenBlock = new Block();
            JavaStatement isDebuggingNeededIfStatement =
                new JavaStatement.IfThenElseStatement(isDebuggingNeededCheck, debuggingNeededThenBlock);

            final int arity = dc.getArity();

            //new RTValue[]{CAL_Int.make(take$nElements$1), take$list$2}
            JavaExpression[] argValues = new JavaExpression[arity];
            for (int i = 0; i < arity; ++i) {

                String javaArgName = argNames[i];
                JavaTypeName javaArgType = argTypes[i];

                JavaExpression javaArgValue = new LocalName(javaArgName, javaArgType);
                if (!javaArgType.equals(JavaTypeNames.RTVALUE)) {
                    javaArgValue = SCJavaDefn.boxExpression(javaArgType, javaArgValue);
                }

                argValues[i] = javaArgValue;
            }
            JavaExpression argValueArrayCreation = new JavaExpression.ArrayCreationExpression(JavaTypeNames.RTVALUE, argValues);

            //$ec.debugProcessing("Prelude.take",
            //            new RTValue[]{CAL_Int.make(take$nElements$1), take$list$2}));
            JavaExpression suspend =
                new MethodInvocation.Instance(
                    SCJavaDefn.EXECUTION_CONTEXT_VAR, "debugProcessing",
                    new JavaExpression[] {
                            LiteralWrapper.make(dc.getName().getQualifiedName()),
                            argValueArrayCreation },
                    new JavaTypeName[] { JavaTypeName.STRING,
                            JavaTypeName.CAL_VALUE_ARRAY },
                    JavaTypeName.VOID,
                    MethodInvocation.InvocationType.VIRTUAL);
            debuggingNeededThenBlock.addStatement(new ExpressionStatement(suspend));

            return isDebuggingNeededIfStatement;

        }

        /**
         * Get the Java representation for the data constructor.
         * The returned class will include any inner classes.
         * @return JavaClassRep
         * @throws CodeGenerationException
         */
        JavaClassRep generateDataConsDefinition() throws CodeGenerationException {
            JavaClassRep dcClass = getDataConsDefinition();
            dcClass.addInnerClass(getDCFieldSelectionClass());

            return dcClass;
        }

        /**
         * Get the Java representation for the data constructor.
         * The returned class will not include any inner classes.
         * @return JavaClassRep
         * @throws CodeGenerationException
         */
        JavaClassRep getDataConsDefinition() throws CodeGenerationException {

            if (javaClassRep == null) {

                // Determine access..
                int classModifiers = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;

                // No interfaces are implemented
                JavaTypeName[] interfaces = JavaDefinitionBuilder.EMPTY_TYPE_NAME_ARRAY;

                // Now instantiate the java class representation.
                this.javaClassRep = new JavaClassRep(className, dataTypeClassName, classModifiers, interfaces);

                createFields();

                // There are two situations where we construct a 'data type' object: 1) fully saturated
                // 2) where it is the beginning of an application chain.  For case 1 we need a constructor
                // that takes all arguments.  For case 2 we need a zero argument constructor.
                int arity = dc.getArity();
                createConstructor_noArgs();
                if (arity > 0) {
                    // We need to mark this class as containing assertions.
                    // This is done manually because we don't always
                    // generate the inner classes at the same time
                    // as the containing class.
                    javaClassRep.setContainsAssertions();

                    if (dcHasPrimitives) {
                        createConstructor_allArgsUnboxed();
                    } else {
                        createConstructor_allArgs();
                    }
                    createMethod_f();
                    createMethod_fSaturatedLazy();
                }

                createMethod_getArity();
                createMethod_getOrdinalValue();
                createMethod_make();

                // Special case for boolean false
                if (dc.getName().equals(CAL_Prelude.DataConstructors.False)) {
                    createMethod_isLogicalTrueOverrideFalse();
                }

                for (int i = 0; i < arity; ++i) {
                    createMethod_fieldGetter(i);
                }

                createMethod_buildDeepSeq();

                // We want to create a version of get field by index which returns
                // a boxed version (i.e. RTValue) of each field.
                createMethod_getFieldByIndex(null, "");

                // Now for each primitive type, for which there is a strict field,
                // generate a version getFieldByIndex_As_... that returns an unboxed
                // value.
                createMethod_getFieldByIndex(JavaTypeName.BOOLEAN, SCJavaDefn.getNameForPrimitive(JavaTypeName.BOOLEAN));
                createMethod_getFieldByIndex(JavaTypeName.BYTE, SCJavaDefn.getNameForPrimitive(JavaTypeName.BYTE));
                createMethod_getFieldByIndex(JavaTypeName.CHAR, SCJavaDefn.getNameForPrimitive(JavaTypeName.CHAR));
                createMethod_getFieldByIndex(JavaTypeName.DOUBLE, SCJavaDefn.getNameForPrimitive(JavaTypeName.DOUBLE));
                createMethod_getFieldByIndex(JavaTypeName.FLOAT, SCJavaDefn.getNameForPrimitive(JavaTypeName.FLOAT));
                createMethod_getFieldByIndex(JavaTypeName.INT, SCJavaDefn.getNameForPrimitive(JavaTypeName.INT));
                createMethod_getFieldByIndex(JavaTypeName.LONG, SCJavaDefn.getNameForPrimitive(JavaTypeName.LONG));
                createMethod_getFieldByIndex(JavaTypeName.SHORT, SCJavaDefn.getNameForPrimitive(JavaTypeName.SHORT));
                createMethod_getFieldByIndex(JavaTypeName.STRING, SCJavaDefn.getNameForPrimitive(JavaTypeName.STRING));
                createMethod_getFieldByIndex(JavaTypeName.OBJECT, "Object");

                createMethod_getModuleName();
                createMethod_getUnqualifiedName();
                createMethod_getQualifiedName();

                createMethod_isFunctionSingleton();
                createMethod_debug_getChild();

            }

            return javaClassRep;

        }

        /**
         * Generate the inner class FieldSelection.  This is used to represent a
         * field selection construct on an instance of the DC.
         * @return the FieldSelection class.
         */
        private JavaClassRep getDCFieldSelectionClass () {
            int classModifiers = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;

            // No interfaces are implemented
            JavaTypeName[] interfaces = JavaDefinitionBuilder.EMPTY_TYPE_NAME_ARRAY;
            JavaTypeName fieldSelectionTypeName = CALToJavaNames.createFieldSelectionClassTypeNameFromDC(dc, module);
            // Now instantiate the java class representation.
            JavaClassRep fieldSelectionClass = new JavaClassRep(fieldSelectionTypeName, JavaTypeNames.RTDATACONS_FIELD_SELECTION, classModifiers, interfaces);

            // Add the constructor: FieldSelection (RTValue $dataConsExpr, int $fieldOrdinal, ErrorInfo $errorInfo);
            fieldSelectionClass.addConstructor(createDCFieldSelectionClass_constructor ());

            // Add the method: private final String getFieldNameByOrdinal (int ordinal);
            fieldSelectionClass.addMethod(createDCFieldSelectionClass_method_getFieldNameByOrdinal());

            fieldSelectionClass.addMethod (createDCFieldSelectionClass_method_getDCName());

            return fieldSelectionClass;
        }

        /**
         * Create a constructor for the static inner FieldSelection class.
         * public FieldSelection (RTValue $dataConsExpr, int $fieldOrdinal, ErrorInfo $errorInfo)
         * @return the constructor for the FieldSelection class.
         */
        private JavaConstructor createDCFieldSelectionClass_constructor () {
            JavaTypeName argTypes[] =
                new JavaTypeName[]{
                    JavaTypeNames.RTVALUE,
                    JavaTypeName.INT,
                    JavaTypeName.INT,
                    JavaTypeName.ERRORINFO};
            String argNames[] =
                new String[]{
                    "$dataConsExpr",
                    "$dcOrdinal",
                    "$fieldOrdinal",
                    "$errorInfo"};

            MethodVariable $dataConsExprVariable = new MethodVariable(argNames[0]);
            MethodVariable $dcOrdinalVariable = new MethodVariable(argNames[1]);
            MethodVariable $fieldOrdinalVariable = new MethodVariable(argNames[2]);
            MethodVariable $errorInfoVariable = new MethodVariable(argNames[3]);

            // Create the constructor.
            // In this case we simply pass the parameters to the superclass constructor.
            JavaConstructor constructor =
                new JavaConstructor (Modifier.PUBLIC,
                                     argNames,
                                     argTypes,
                                     "FieldSelection",
                                     new JavaExpression[]{
                                         $dataConsExprVariable,
                                         $dcOrdinalVariable,
                                         $fieldOrdinalVariable,
                                         $errorInfoVariable},
                                     argTypes);

            return constructor;
        }

        /**
         * Create the method String getFieldNameByOrdinal (int ordinal).
         * This is a method in the static inner class FieldSelection.
         * @return the getFieldNameByOrdinal() method.
         */
        private JavaMethod createDCFieldSelectionClass_method_getFieldNameByOrdinal () {

            int modifiers = Modifier.PROTECTED | Modifier.FINAL;
            JavaMethod javaMethod = new JavaMethod (modifiers, JavaTypeName.STRING, "ordinal", JavaTypeName.INT, false, "getFieldNameByOrdinal");
            SwitchStatement sw = new SwitchStatement (METHODVAR_ORDINAL);
            for (int i = 0, nFields = dc.getArity(); i < nFields; ++i) {
                String fieldName = dc.getNthFieldName(i).toString();
                sw.addCase(new SwitchStatement.IntCaseGroup(i, new ReturnStatement(LiteralWrapper.make(fieldName))));
            }

            javaMethod.addStatement(sw);

            JavaExpression exception =
                new JavaExpression.ClassInstanceCreationExpression(JavaTypeName.INDEX_OUT_OF_BOUNDS_EXCEPTION);
            JavaStatement s =
                new JavaStatement.ThrowStatement (exception);
            javaMethod.addStatement(s);
            return javaMethod;
        }

        private JavaMethod createDCFieldSelectionClass_method_getDCName () {
            int modifiers = Modifier.PROTECTED | Modifier.FINAL;
            JavaMethod javaMethod = new JavaMethod (modifiers, JavaTypeName.STRING, "getDCName");

            javaMethod.addStatement(new ReturnStatement (JavaExpression.LiteralWrapper.make (dc.getName().getQualifiedName())));
            return javaMethod;
        }

        /**
         * Create the fields.
         *   private static DataConstructor dc;
         *   private static QualifiedName qn = new QualifiedName(dcName);
         *   private RTValue[] members = new RTValue[arity];                    // arity > 0
         *   private static final (typeConsType) self = new ThisClass();        // arity == 0
         * @throws CodeGenerationException
         */
        private void createFields() throws CodeGenerationException {

            int arity = dc.getArity();
            if (arity > 0) {
                // private RTValue field1, field2, etc.
                for (int i = 0; i < arity; ++i) {
                    FieldName fn = dc.getNthFieldName(i);
                    if (commonFields.contains(fn)) {
                        // Common fields are implemented in base class.
                        continue;
                    }

                    JavaTypeName fieldType = JavaTypeNames.RTVALUE;
                    int modifiers = Modifier.PRIVATE;
                    if (fieldStrictness[i]) {
                        modifiers = modifiers | Modifier.FINAL;
                        if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                            fieldType = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                        }
                    }

                    JavaFieldDeclaration fieldDec = new JavaFieldDeclaration (modifiers, fieldType, javaFieldNames[i], null);

                    javaClassRep.addFieldDeclaration(fieldDec);
                }
            }

            // We want a singleton instance of the data constructor class to use as a
            // supercombinator.
            // private static final (typeConsType) self = new ThisClass();
            int modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
            JavaExpression selfInitializer;
            if (LECCMachineConfiguration.passExecContextToDataConstructors()) {
                selfInitializer = new ClassInstanceCreationExpression(className, JavaExpression.LiteralWrapper.NULL, JavaTypeNames.RTEXECUTION_CONTEXT);
            } else {
                selfInitializer = new ClassInstanceCreationExpression(className);
            }
            JavaFieldDeclaration selfFieldDeclaration = new JavaFieldDeclaration(modifiers, className, instanceName, selfInitializer);
            javaClassRep.addFieldDeclaration(selfFieldDeclaration);

        }

        /**
         * Create the no-argument constructor.
         *      private ThisClass() {
         *      }
         * @throws CodeGenerationException
         */
        private void createConstructor_noArgs() throws CodeGenerationException {
            int modifiers = 0;

            boolean addEC = LECCMachineConfiguration.passExecContextToDataConstructors();
            JavaConstructor javaConstructor;
            if (addEC) {
                modifiers |= Modifier.PUBLIC;
                javaConstructor = new JavaConstructor(modifiers,  new String[]{SCJavaDefn.EXECUTION_CONTEXT_NAME}, new JavaTypeName[]{JavaTypeNames.RTEXECUTION_CONTEXT}, CALToJavaNames.createInnerClassNameFromDC(dc, module));
            } else {
                modifiers |= Modifier.PRIVATE;
                javaConstructor = new JavaConstructor(modifiers, CALToJavaNames.createInnerClassNameFromDC(dc, module));
            }
            // Add the method to the class.
            javaClassRep.addConstructor(javaConstructor);

            // Initialize any final fields.
            for (int i = 0; i < dc.getArity(); ++i) {
                if (!commonFields.contains(dc.getNthFieldName(i)) && fieldStrictness[i]) {
                    JavaTypeName fieldType = JavaTypeNames.RTVALUE;
                    if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                        fieldType = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                    }

                    LiteralWrapper fieldVal = JavaDefinitionBuilder.getDefaultValueForType(fieldType);
                    JavaField jf = new JavaField.Instance(null, javaFieldNames[i], fieldType);
                    JavaExpression assign = new JavaExpression.Assignment(jf, fieldVal);
                    javaConstructor.addStatement(new ExpressionStatement(assign));
                }
            }

        }

        /**
         * Create the all-argument constructor.
         *      private ThisClass(RTValue member0, RTValue member1, ...) {
         *          this.members[0] = member0;
         *          this.members[1] = member1;
         *          ...
         *      };
         */
        private void createConstructor_allArgs() {
            int modifiers = Modifier.PUBLIC;
            int nArgs = dc.getArity();
            boolean addEC = LECCMachineConfiguration.passExecContextToDataConstructors();
            if (addEC) {
                nArgs++;
            }

            // Get arg types - all RTValues.
            JavaTypeName[] argTypes = new JavaTypeName[nArgs];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

            // Figure out arg names and create method variables.
            String[] argNames = new String[nArgs];
            MethodVariable[] argVars = new MethodVariable[nArgs];
            for (int i = 0, n = argNames.length; i < n; i++) {
                argNames[i] = "member" + i;
                argVars[i] = new MethodVariable(argNames[i]);
            }

            if (addEC) {
                argTypes[argTypes.length - 1] = JavaTypeNames.RTEXECUTION_CONTEXT;
                argNames[argNames.length - 1] = SCJavaDefn.EXECUTION_CONTEXT_NAME;
            }

            // Build up the arguments to the superclass constructor.
            JavaExpression superClassConstructorArgValues[] = new JavaExpression [commonFields.size()];
            JavaTypeName   superClassConstructorArgTypes[]  = new JavaTypeName [commonFields.size()];
            int j = 0;
            for (final FieldName fn : commonFields) {
                int fieldIndex = dc.getFieldIndex(fn);
                superClassConstructorArgValues[j] = argVars[fieldIndex];
                superClassConstructorArgTypes[j] = argTypes[fieldIndex];
                j++;
            }

            // Add the method to the class.
            JavaConstructor javaConstructor = new JavaConstructor(modifiers, argNames, argTypes, CALToJavaNames.createInnerClassNameFromDC(dc, module), superClassConstructorArgValues, superClassConstructorArgTypes);
            javaClassRep.addConstructor(javaConstructor);

            if (LECCMachineConfiguration.generateDebugCode()) {
                javaConstructor.addStatement(generateDebugCode(argNames, argTypes));
            }

            // Assert the arguments are not null.
            JavaExpression check = new JavaExpression.OperatorExpression.Binary(JavaOperator.NOT_EQUALS_OBJECT, argVars[0], LiteralWrapper.NULL);
            for (int i = 1, n = dc.getArity(); i < n; ++i) {
                JavaExpression nextCheck = new JavaExpression.OperatorExpression.Binary(JavaOperator.NOT_EQUALS_OBJECT, argVars[i], LiteralWrapper.NULL);
                check = new OperatorExpression.Binary(JavaOperator.CONDITIONAL_AND, check, nextCheck);
            }
            javaConstructor.addStatement(new AssertStatement(check, LiteralWrapper.make("Invalid constructor argument for " + dc.getName().getQualifiedName()), JavaTypeName.STRING));

            // Add the body..
            // We want to assign any fields in this class.
            for (int i = 0; i < dc.getArity(); i++) {
                if (commonFields.contains(dc.getNthFieldName(i))) {
                    // This field is in the containing class so we don't
                    // assign here.
                    continue;
                }

                JavaField field = new JavaField.Instance(null, javaFieldNames[i], JavaTypeNames.RTVALUE);

                Assignment memberAssignment = new Assignment(field, argVars[i]);
                javaConstructor.addStatement(new ExpressionStatement(memberAssignment));
            }

            // Add the statistics block.
            addStatsBlock(javaConstructor);
        }

        private void createConstructor_allArgsUnboxed() throws CodeGenerationException {
            int modifiers = Modifier.PUBLIC;
            int nArgs = dc.getArity();
            boolean addEC = LECCMachineConfiguration.passExecContextToDataConstructors();
            if (addEC) {
                nArgs++;
            }

            // Get arg types
            JavaTypeName[] argTypes = new JavaTypeName[nArgs];
            for (int i = 0; i < dc.getArity(); ++i) {
                if (fieldStrictness[i] && SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                    argTypes[i] = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                } else {
                    argTypes[i] = JavaTypeNames.RTVALUE;
                }
            }

            // Figure out arg names and create method vars.
            String[] argNames = new String[nArgs];
            MethodVariable[] argVars = new MethodVariable[nArgs];
            for (int i = 0; i < argNames.length; i++) {
                argNames[i] = "member" + i;
                argVars[i] = new MethodVariable(argNames[i]);
            }

            if (addEC) {
                argTypes[argTypes.length - 1] = JavaTypeNames.RTEXECUTION_CONTEXT;
                argNames[argNames.length - 1] = SCJavaDefn.EXECUTION_CONTEXT_NAME;
            }

            // Build up the arguments to the superclass constructor.
            JavaExpression superClassConstructorArgValues[] = new JavaExpression [commonFields.size()];
            JavaTypeName   superClassConstructorArgTypes[]  = new JavaTypeName [commonFields.size()];
            int j = 0;
            for (final FieldName fn : commonFields) {
                int fieldIndex = dc.getFieldIndex(fn);
                superClassConstructorArgValues[j] = argVars[fieldIndex];
                superClassConstructorArgTypes[j] = argTypes[fieldIndex];
                j++;
            }

            // Add the method to the class.
            JavaConstructor javaConstructor = new JavaConstructor(modifiers, argNames, argTypes, CALToJavaNames.createInnerClassNameFromDC(dc, module), superClassConstructorArgValues, superClassConstructorArgTypes);
            javaClassRep.addConstructor(javaConstructor);

            if (LECCMachineConfiguration.generateDebugCode()) {
                javaConstructor.addStatement(generateDebugCode(argNames, argTypes));
            }

            // Assert that object arguments are non-null.
            JavaExpression check = null;
            for (int i = 0, n = dc.getArity(); i < n; ++i) {
                // We only check fields of type RTValue.  It is valid to have an unboxed object value of null.
                if (argTypes[i].equals(JavaTypeNames.RTVALUE)) {
                    JavaExpression newCheck = new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_OBJECT, argVars[i], LiteralWrapper.NULL);
                    if (check == null) {
                        check = newCheck;
                    } else {
                        check = new OperatorExpression.Binary(JavaOperator.CONDITIONAL_AND, check, newCheck);
                    }
                }
            }
            if (check != null) {
                javaConstructor.addStatement(new AssertStatement(check, LiteralWrapper.make("Invalid constructor argument for " + dc.getName().getQualifiedName()), JavaTypeName.STRING));
            }

            // Add the body..
            // We want to assign any of the fields in this class.
            for (int i = 0, n = dc.getArity(); i < n; i++) {
                if (commonFields.contains(dc.getNthFieldName(i))) {
                    // This field is in the containing class so we don't
                    // assign here.
                    continue;
                }
                JavaTypeName fieldType = JavaTypeNames.RTVALUE;
                if (fieldStrictness[i] && SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                    fieldType = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                }

                JavaField field = new JavaField.Instance(null, javaFieldNames[i], fieldType);

                Assignment memberAssignment = new Assignment(field, argVars[i]);
                javaConstructor.addStatement(new ExpressionStatement(memberAssignment));
            }

            // Add the statistics block.
            addStatsBlock(javaConstructor);
        }

        /**
         * Create the getArity() method.
         *      public final int getArity();
         */
        private void createMethod_getArity() {
            int modifiers = Modifier.PUBLIC | Modifier.FINAL;
            JavaTypeName returnType = JavaTypeName.INT;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, "getArity");
            javaClassRep.addMethod(javaMethod);

            // Add the body..
            javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make(Integer.valueOf(dc.getArity()))));

            //LiteralWrapper zeroWrapper = LiteralWrapper.make(JavaPrimitives.makeInteger(0));
//            if (arity == 0) {
                // return 0;
//                javaMethod.addStatement(new ReturnStatement(zeroWrapper));

//            } else {
//                // return (members[0] == null) ? arity : 0;
//                JavaField field = new JavaField("field0", JavaTypeNames.RTVALUE);
//                JavaExpression equalityTest = new OperatorExpression.Binary(JavaOperator.EQUALS_OBJECT, field, LiteralWrapper.NULL);
//                JavaExpression ternaryExpression = new OperatorExpression.Ternary(equalityTest, LiteralWrapper.make(JavaPrimitives.makeInteger(arity)), zeroWrapper);
//                javaMethod.addStatement(new ReturnStatement(ternaryExpression));
//            }
        }

        /**
         * Create the getOrdinalValue() method.
         * This method returns the ordinal identifier of this data constructor.
         *      public int getOrdinalValue()  {return (ordinal);}
         */
        private void createMethod_getOrdinalValue() {
            int modifiers = Modifier.PUBLIC;
            JavaTypeName returnType = JavaTypeName.INT;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, "getOrdinalValue");
            javaClassRep.addMethod(javaMethod);

            // Add the body..
            // return (ordinal);
            javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make(Integer.valueOf(dc.getOrdinal()))));
        }

        /**
         * Create the no-argument make() method
         *      public static final RTFunction make();
         */
        private void createMethod_make() {
            int modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
            JavaTypeName returnType = className;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, "make");
            javaClassRep.addMethod(javaMethod);

            // Add the body..
            // The no argument version of make should only be called for an instance to
            // be used as a supercombinator so we can return the singleton instance.
            JavaField selfField = new JavaField.Static(className, instanceName, className);
            javaMethod.addStatement(new ReturnStatement(selfField));

        }


        /**
         * Create the f() method.
         *      public final RTValue f(RTResultFunction $rootNode) throws org.openquark.cal.runtime.CALExecutor.CALExecutorException;
         * @throws CodeGenerationException
         */
        private void createMethod_f() throws CodeGenerationException {

            final int modifiers = Modifier.PUBLIC | Modifier.FINAL;
            final JavaTypeName returnType = JavaTypeNames.RTVALUE;

            // Add the method to the class.
            final JavaMethod javaMethod = new JavaMethod(
                modifiers,
                returnType,
                new String [] {ROOT_NODE, SCJavaDefn.EXECUTION_CONTEXT_NAME},
                new JavaTypeName[]{JavaTypeNames.RTRESULT_FUNCTION, JavaTypeNames.RTEXECUTION_CONTEXT},
                new boolean[] {true, true},
                "f");
            javaClassRep.addMethod(javaMethod);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            if (LECCMachineConfiguration.generateCallCounts()) {
                JavaExpression args[] = new JavaExpression[2];
                JavaTypeName argTypes[] = new JavaTypeName[2];
                args[0] = LiteralWrapper.make(dc.getName().getModuleName().toSourceText());
                args[1] = LiteralWrapper.make(dc.getName().getUnqualifiedName());
                argTypes[0] = argTypes[1] = JavaTypeName.STRING;
                MethodInvocation mi =
                    new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR,
                                         "dcFunctionCalled",
                                         args,
                                         argTypes,
                                         JavaTypeName.VOID,
                                         MethodInvocation.InvocationType.VIRTUAL);
                javaMethod.addStatement(new ExpressionStatement(mi));
            }

            // Add the body..
            final int arity = dc.getArity();
            if (arity > 0) {
                // // Arguments
                javaMethod.addStatement(new LineComment("Arguments"));

                final boolean addEC = LECCMachineConfiguration.passExecContextToDataConstructors();

                final int nConstructorArgs = addEC ? (arity + 1) : arity;

                // Get arg names
                final JavaExpression[] arguments = new JavaExpression[nConstructorArgs];
                for (int i = 0; i < arity; i++) {
                    final String localVarName = "$arg" + i;
                    arguments[i] = new LocalVariable(localVarName, JavaTypeNames.RTVALUE);
                }


                // final RTValue $arg_i = initializer;
                for (int i = arity - 1; i >= 0; i--) {

                    final JavaExpression initializer;

                    if (i == arity - 1) {
                        //$rootNode.getArgValue();
                        initializer = SCJavaDefn.createInvocation(METHODVAR_ROOT_NODE, SCJavaDefn.GETARGVALUE);

                    } else if (i == arity - 2) {

                        if (arity > 2) {
                            //RTValue $currentRootNode;
                            javaMethod.addStatement(new LocalVariableDeclaration(LOCALVAR_CURRENT_ROOT_NODE));
                            //($currentRootNode = $rootNode.prevArg()).getArgValue();
                            initializer =
                                SCJavaDefn.createInvocation(
                                    new Assignment(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.createInvocation(METHODVAR_ROOT_NODE, SCJavaDefn.PREVARG)),
                                    SCJavaDefn.GETARGVALUE);
                        } else {
                            //$rootNode.prevArg().getArgValue();
                            initializer =
                                SCJavaDefn.createInvocation(
                                    SCJavaDefn.createInvocation(METHODVAR_ROOT_NODE, SCJavaDefn.PREVARG),
                                    SCJavaDefn.GETARGVALUE);
                        }

                    } else if (i == 0) {

                        //$currentRootNode.prevArg().getArgValue();
                        initializer =
                            SCJavaDefn.createInvocation(
                                SCJavaDefn.createInvocation(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.PREVARG),
                                SCJavaDefn.GETARGVALUE);

                    } else {

                        //($currentRootNode = $currentRootNode.prevArg()).getArgValue();
                        initializer =
                            SCJavaDefn.createInvocation(
                                new Assignment(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.createInvocation(LOCALVAR_CURRENT_ROOT_NODE, SCJavaDefn.PREVARG)),
                                SCJavaDefn.GETARGVALUE);
                    }

                    final LocalVariableDeclaration argDeclaration =
                        new LocalVariableDeclaration((LocalVariable)arguments[i], initializer, !fieldStrictness[i]);

                    javaMethod.addStatement(argDeclaration);
                }

                // Get arg types - all RTValues.
                final JavaTypeName[] argTypes = new JavaTypeName[nConstructorArgs];
                Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

                if (addEC) {
                    arguments[arguments.length - 1] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
                    argTypes[argTypes.length - 1] = JavaTypeNames.RTEXECUTION_CONTEXT;
                }

                for (int i = 0; i < dc.getArity(); ++i) {
                    if (fieldStrictness[i]) {
                        arguments[i]= new MethodInvocation.Instance (arguments[i],
                                                            "evaluate",
                                                            SCJavaDefn.EXECUTION_CONTEXT_VAR,
                                                            JavaTypeNames.RTEXECUTION_CONTEXT,
                                                            JavaTypeNames.RTVALUE,
                                                            MethodInvocation.InvocationType.VIRTUAL);

                        if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                            argTypes[i] = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                            arguments[i] = SCJavaDefn.unboxValue(SCJavaDefn.typeExprToTypeName(fieldTypes[i]), arguments[i]);
                        }
                    }
                }

                // return new ThisClass($arg0, $arg1, ...);
                final JavaExpression cice = new ClassInstanceCreationExpression(className, arguments, argTypes);

                javaMethod.addStatement(new ReturnStatement(cice));
            } else {
                // return new ThisClass();
                final JavaExpression cice = new ClassInstanceCreationExpression(className, JavaExpression.LiteralWrapper.NULL, JavaTypeNames.RTEXECUTION_CONTEXT);
                javaMethod.addStatement(new ReturnStatement(cice));
            }
        }

        /**
         * Create the fnL() method.
         * @throws CodeGenerationException
         */
        private void createMethod_fSaturatedLazy() throws CodeGenerationException {
            final int modifiers = Modifier.PUBLIC | Modifier.FINAL;
            final JavaTypeName returnType = JavaTypeNames.RTVALUE;

            final int nArgs = dc.getArity() + 1;

            // Get arg types - all RTValues.
            final JavaTypeName[] argTypes = new JavaTypeName[nArgs];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);

            // Figure out arg names.
            final String[] argNames = new String[nArgs];
            for (int i = 0; i < argNames.length; i++) {
                argNames[i] = "member" + i;
            }

             argTypes[argTypes.length - 1] = JavaTypeNames.RTEXECUTION_CONTEXT;
              argNames[argNames.length - 1] = SCJavaDefn.EXECUTION_CONTEXT_NAME;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, argNames, argTypes, null, "f" + dc.getArity() + "L");
            javaClassRep.addMethod(javaMethod);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            if (LECCMachineConfiguration.generateCallCounts()) {
                JavaExpression args[] = new JavaExpression[2];
                JavaTypeName ccArgTypes[] = new JavaTypeName[2];
                args[0] = LiteralWrapper.make(dc.getName().getModuleName().toSourceText());
                args[1] = LiteralWrapper.make(dc.getName().getUnqualifiedName());
                ccArgTypes[0] = ccArgTypes[1] = JavaTypeName.STRING;
                MethodInvocation mi =
                    new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR,
                                         "dcFunctionCalled",
                                         args,
                                         ccArgTypes,
                                         JavaTypeName.VOID,
                                         MethodInvocation.InvocationType.VIRTUAL);

                javaMethod.addStatement(new ExpressionStatement(mi));
            }

            boolean addEC = LECCMachineConfiguration.passExecContextToDataConstructors();
            int nConstructorArgs = dc.getArity();
            if (addEC) {
                nConstructorArgs++;
            }

            JavaExpression constructorArgs[] = new JavaExpression [nConstructorArgs];
            JavaTypeName constructorArgTypes[] = new JavaTypeName [nConstructorArgs];
            System.arraycopy(argTypes, 0, constructorArgTypes, 0, constructorArgTypes.length);

            if (addEC) {
                constructorArgs[constructorArgs.length - 1] = SCJavaDefn.EXECUTION_CONTEXT_VAR;
            }

            for (int i = 0; i < dc.getArity(); ++i) {
                constructorArgs[i] = new MethodVariable("member" + i);
                if (fieldStrictness[i]) {
                    constructorArgs[i] = new MethodInvocation.Instance (constructorArgs[i],
                                                    "evaluate",
                                                    SCJavaDefn.EXECUTION_CONTEXT_VAR,
                                                    JavaTypeNames.RTEXECUTION_CONTEXT,
                                                    JavaTypeNames.RTVALUE,
                                                    MethodInvocation.InvocationType.VIRTUAL);

                    if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                        constructorArgs[i] = SCJavaDefn.unboxValue(SCJavaDefn.typeExprToTypeName(fieldTypes[i]), constructorArgs[i]);
                        constructorArgTypes[i] = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                    }
                }
            }

            JavaExpression cc = new ClassInstanceCreationExpression (className, constructorArgs, constructorArgTypes);

            javaMethod.addStatement(new ReturnStatement(cc));
        }

        /**
         * Create the isLogicalTrue() override method for the Prelude.False data constructor.
         *      public boolean isLogicalTrue() {return false;}
         */
        private void createMethod_isLogicalTrueOverrideFalse() {
            int modifiers = Modifier.PUBLIC;
            JavaTypeName returnType = JavaTypeName.BOOLEAN;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, "isLogicalTrue");
            javaClassRep.addMethod(javaMethod);

            // Add the body..
            // return false;
            javaMethod.addStatement(new ReturnStatement(LiteralWrapper.FALSE));
        }

        /**
         * Create the getFieldn() method.
         *      public final RTValue getFieldn()
         * @param i
         * @throws CodeGenerationException
         */
        private void createMethod_fieldGetter(int i) throws CodeGenerationException {
            if (commonFields.contains(dc.getNthFieldName(i))) {
                return;
            }

            int modifiers = Modifier.PUBLIC | Modifier.FINAL;
            JavaTypeName returnType = JavaTypeNames.RTVALUE;
            // Add the method to the class.
            String methodName = "get" + javaFieldNames[i];
            JavaMethod javaMethod = new JavaMethod(modifiers, returnType, methodName);
            javaClassRep.addMethod(javaMethod);

            // return a boxed version of the field.
            JavaExpression jf;
            if (fieldStrictness[i] && SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                jf = new JavaField.Instance (null, javaFieldNames[i], SCJavaDefn.typeExprToTypeName(fieldTypes[i]));
                jf = SCJavaDefn.boxExpression(fieldTypes[i], jf);
                javaMethod.addStatement(new ReturnStatement(jf));
            } else {
                JavaField field = new JavaField.Instance (null, javaFieldNames[i], JavaTypeNames.RTVALUE);
                jf = field;
               if (!fieldStrictness[i]) {
                   // We have a non-strict field of type RTValue.  In order to reduce space usage
                   // we want to update the field value to be the result of the original suspension.
                   // If the field value is an RTResultFunction we want to re-assign the field with
                   // a call to 'getValue()'.
                   // For example:
                   //public final RTValue get_head() {
                   //    RTValue field;
                   //        if ((field = _head) instanceof RTResultFunction) {
                   //            return (_head = field.getValue());
                   //        }
                   //        return field;
                   //}
                   String localName = javaFieldNames[i]+"$";
                   LocalVariable localVariable = new LocalVariable(localName, JavaTypeNames.RTVALUE);
                   LocalVariableDeclaration localDeclaration = new LocalVariableDeclaration(localVariable);
                   javaMethod.addStatement(localDeclaration);
                   Assignment localAssignment = new Assignment(localVariable, field);
                   InstanceOf checkType = new InstanceOf(localAssignment, JavaTypeNames.RTRESULT_FUNCTION);
                   MethodInvocation getValue = new MethodInvocation.Instance(localVariable, "getValue", JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                   Assignment fieldAssignment = new Assignment(field, getValue);
                   ReturnStatement returnNewVal = new ReturnStatement(fieldAssignment);
                   IfThenElseStatement ifThen = new IfThenElseStatement(checkType, returnNewVal);
                   javaMethod.addStatement(ifThen);
                   javaMethod.addStatement(new ReturnStatement (localVariable));

               } else {
                   // The field is strict and therefore already in WHNF.
                   javaMethod.addStatement(new ReturnStatement(jf));
               }

            }

            // If the field type is primitive and strict we want to generate
            // a method that retrieves the field as an unboxed value.
            if (SCJavaDefn.canTypeBeUnboxed(fieldTypes[i]) && dc.isArgStrict((i))) {

                returnType = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);
                methodName = methodName + "_As_" + SCJavaDefn.getNameForPrimitive(returnType);

                // If all the fields with this name/type combination across all DCs are strict we
                // don't need an RTExecutionContext passed to the getter.
                javaMethod = new JavaMethod (modifiers, returnType, methodName);
                javaClassRep.addMethod(javaMethod);

                // Add the throws declaration
                javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

                if (fieldStrictness[i]) {
                    jf = new JavaField.Instance (null, javaFieldNames[i], returnType);
                } else {
                    // This is an error.  We should only get to this point if all
                    // instances of this field/type are strict across all DCs.
                    throw new CodeGenerationException ("Attempt to generate unboxed accessor on lazy field " + javaFieldNames[i] + " for DC " + dc.getName());
                }
                javaMethod.addStatement(new ReturnStatement(jf));

            }
        }

        /**
         * Generate a method: RTValue getFieldByIndex(int fieldIndex, ErrorInfo errorInfo).
         * This method retrieves a field by index with a check that the object has the expected ordinal.
         * It is primarily used by data constructor field selection outside of a case expression.
         * @param forType - unboxed type to return.  May be null.
         * @param nameForType - the string name of the type to retrieve
         * @throws CodeGenerationException
         */
        private void createMethod_getFieldByIndex (JavaTypeName forType, String nameForType) throws CodeGenerationException {
            if (javaFieldNames.length == 0) {
                return;
            }

            Block methodBodyBlock = new Block();
            int nReturnedFields = 0;

            // Check that this is an instance of the expected data constructor.
            MethodInvocation checkDC =
                new MethodInvocation.Instance(
                        null,
                        "checkDCOrdinalForFieldSelection",
                        new JavaExpression[]{new MethodVariable("dcOrdinal"), new MethodVariable("errorInfo")},
                        new JavaTypeName[]{JavaTypeName.INT, JavaTypeName.ERRORINFO},
                        JavaTypeName.VOID,
                        MethodInvocation.InvocationType.VIRTUAL);
            methodBodyBlock.addStatement(new ExpressionStatement(checkDC));

            SwitchStatement sw = new SwitchStatement(METHODVAR_FIELDINDEX);
            for (int i = 0; i < javaFieldNames.length; ++i) {
                if (forType != null) {
                    // If field is not strict or not primitive can't return unboxed form.
                    if (!fieldStrictness[i] || !SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                        continue;
                    }

                    JavaTypeName ftn = SCJavaDefn.typeExprToTypeName(fieldTypes[i]);

                    if (!forType.equals(ftn)) {
                        // Check to see if we're doing return type 'Object' on a foreign type.
                        if (!forType.equals(JavaTypeName.OBJECT) || ftn instanceof JavaTypeName.Primitive) {
                            continue;
                        }
                    }
                }

                JavaExpression jf;
                if (fieldStrictness[i] && SCJavaDefn.canTypeBeUnboxed(fieldTypes[i])) {
                    jf = new JavaField.Instance (null, javaFieldNames[i], SCJavaDefn.typeExprToTypeName(fieldTypes[i]));
                    if (forType == null) {
                        jf = SCJavaDefn.boxExpression(fieldTypes[i], jf);
                    }
                } else {
                    // We have a non-strict field of type RTValue.  In order to reduce space usage
                    // we want to update the field value to be the result of the original suspension.
                    // We do this by using the field accessor function get_fieldName().
                    jf = new MethodInvocation.Instance(null, "get" + javaFieldNames[i], JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                    if (forType != null) {
                        jf = SCJavaDefn.unboxValue(forType, jf);
                    }
                }

                SwitchStatement.IntCaseGroup iCase = new SwitchStatement.IntCaseGroup(i, new ReturnStatement (jf));
                sw.addCase(iCase);
                nReturnedFields++;

            }

            methodBodyBlock.addStatement (sw);

            MethodInvocation error =
                new MethodInvocation.Instance(
                        null,
                        "badFieldIndexInGetFieldByIndex",
                        METHODVAR_FIELDINDEX,
                        JavaTypeName.INT,
                        JavaTypeName.VOID,
                        MethodInvocation.InvocationType.VIRTUAL);

            methodBodyBlock.addStatement(new ExpressionStatement(error));
            methodBodyBlock.addStatement (makeDefaultValReturnStatement(forType));

            if (nReturnedFields > 0) {
                int modifiers = Modifier.PUBLIC | Modifier.FINAL;

                String methodName = "getFieldByIndex";
                if (forType != null) {
                    methodName = methodName + "_As_" + nameForType;
                }

                JavaMethod javaMethod =
                    new JavaMethod (modifiers,
                            (forType == null) ? JavaTypeNames.RTVALUE : forType,
                            new String[]{"dcOrdinal", "fieldIndex", "errorInfo"},
                            new JavaTypeName[]{JavaTypeName.INT, JavaTypeName.INT, JavaTypeName.ERRORINFO},
                            null, methodName);

                javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

                javaClassRep.addMethod (javaMethod);
                javaMethod.addStatement(methodBodyBlock);
            }

        }

        private final ReturnStatement makeDefaultValReturnStatement (JavaTypeName forType) {
            return new ReturnStatement(JavaDefinitionBuilder.getDefaultValueForType(forType));
        }


        /**
         * Create the getModuleName() method.
         *     public final String getModuleName() {
         *         return "ModuleName";
         *     }
         *
         */
        private void createMethod_getModuleName () {
            JavaMethod javaMethod = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "getModuleName");
            javaClassRep.addMethod (javaMethod);

            javaMethod.addStatement (new ReturnStatement (LiteralWrapper.make (dc.getName().getModuleName().toSourceText())));
        }

        /**
         * Create the getUnqualifiedName() method.
         *     public final String getModuleName() {
         *         return "unqualifiedName";
         *     }
         *
         */
        private void createMethod_getUnqualifiedName () {
            JavaMethod javaMethod = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "getUnqualifiedName");
            javaClassRep.addMethod (javaMethod);

            javaMethod.addStatement (new ReturnStatement (LiteralWrapper.make (dc.getName().getUnqualifiedName())));
        }

        /**
         * Create the getQualifiedName() method.
         *    public final String getQualifiedName () {
         *        return "Module.unqualifiedName";
         *    }
         */
        private final void createMethod_getQualifiedName () {
            JavaMethod jm = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.STRING, "getQualifiedName");
            javaClassRep.addMethod(jm);
            jm.addStatement(new ReturnStatement(LiteralWrapper.make(dc.getName().getQualifiedName())));
        }

        /**
         * generate the code implementing the method
         * boolean RTCons.isFunctionSingleton()
         * for non zero-arity data constructors.
         */
        private void createMethod_isFunctionSingleton() {
            final int dcArity = dc.getArity();
            if (dcArity == 0) {
                //0-arity data constructors are handled by the base case implementation of
                //RTCons.isFunctionSingleton() which returns true.
                return;
            }

            //the generated code is:
            //public final boolean isFunctionSingleton() {
            //    return this == $instance;
            //}

            JavaMethod javaMethod = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.BOOLEAN, "isFunctionSingleton");
            javaClassRep.addMethod (javaMethod);

            JavaExpression conditionExpr =
                new OperatorExpression.Binary(
                    JavaOperator.EQUALS_OBJECT,
                    new JavaExpression.JavaField.This(className),
                    new JavaExpression.JavaField.Static(className, "$instance", className));

            JavaStatement returnStatement =
                new JavaStatement.ReturnStatement(conditionExpr);

            javaMethod.addStatement(returnStatement);
        }

        /**
         * Generates a method similar to getFieldByIndex, except no side effects on the CalValue are permitted.
         * @throws CodeGenerationException
         */
        private void createMethod_debug_getChild() throws CodeGenerationException {
            final int dcArity = dc.getArity();
            if (dcArity == 0) {
                //0-arity data constructors are handled by RTCons.debug_getChild()
                return;
            }

            //For example, for Prelude.Cons this generates:

            //public final CalValue getChild(int childN) {
            //    if (isFunctionSingleton()) {
            //        throw new IndexOutOfBoundsException();
            //    }
            //    switch (childN) {
            //    case 0:
            //        return _head;
            //    case 1:
            //        return _tail:
            //    default:
            //        throw new IndexOfOfBoundsException();
            //    }
            //}

            //for strict primitive fields, we generate stuff like "return CAL_Int.make(field)" instead.

            JavaMethod method = new JavaMethod (Modifier.PUBLIC | Modifier.FINAL, JavaTypeName.CAL_VALUE, "childN", JavaTypeName.INT, false, "debug_getChild");
            javaClassRep.addMethod (method);

            MethodVariable childNVar = new JavaExpression.MethodVariable("childN");

            {
                //    if (isFunctionSingleton()) {
                //        throw new IndexOutOfBoundsException();
                //    }

                JavaExpression conditionExpr =
                    new MethodInvocation.Instance(null, "isFunctionSingleton", JavaTypeName.BOOLEAN, MethodInvocation.InvocationType.VIRTUAL);

                JavaStatement thenStatement =
                    new JavaStatement.ThrowStatement(
                        new JavaExpression.ClassInstanceCreationExpression(JavaTypeName.INDEX_OUT_OF_BOUNDS_EXCEPTION));

                JavaStatement.IfThenElseStatement ifThenStatement =
                    new JavaStatement.IfThenElseStatement (conditionExpr, thenStatement);

                method.addStatement(ifThenStatement);
            }

            SwitchStatement switchStatement =
                new SwitchStatement(childNVar);

            for (int i = 0; i < dcArity; ++i) {

                TypeExpr calFieldType = fieldTypes[i];
                String javaFieldName = javaFieldNames[i];

                JavaExpression javaFieldExpr;
                if (fieldStrictness[i] && SCJavaDefn.canTypeBeUnboxed(calFieldType)) {
                    JavaTypeName javaFieldType = SCJavaDefn.typeExprToTypeName(calFieldType);
                    javaFieldExpr = new JavaExpression.JavaField.Instance(null, javaFieldName, javaFieldType);
                    javaFieldExpr = SCJavaDefn.boxExpression(javaFieldType, javaFieldExpr);
                } else {
                    javaFieldExpr = new JavaExpression.JavaField.Instance(null, javaFieldName, JavaTypeNames.RTVALUE);
                }

                switchStatement.addCase(
                    new SwitchStatement.IntCaseGroup(i, new ReturnStatement(javaFieldExpr)));
            }

            switchStatement.addCase(
                new SwitchStatement.DefaultCase(
                    new JavaStatement.ThrowStatement(
                        new JavaExpression.ClassInstanceCreationExpression(JavaTypeName.INDEX_OUT_OF_BOUNDS_EXCEPTION))));

            method.addStatement(switchStatement);
        }

        /**
         * Create an override of the method RTValue.buildDeepSeq().
         * This method will apply deepSeq to each member field.
         * @throws CodeGenerationException
         */
        private void createMethod_buildDeepSeq() throws CodeGenerationException {
            if (dc.getArity() == 0) {
                return;
            }

            // public RTValue buildDeepSeq(RTSupercombinator deepSeq, RTValue rhs) throws CALExecutorException {


            int modifiers = Modifier.PUBLIC | Modifier.FINAL;

            // Add the method to the class.
            JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeNames.RTVALUE, new String[]{"deepSeq", "rhs"}, new JavaTypeName[]{JavaTypeNames.RTSUPERCOMBINATOR, JavaTypeNames.RTVALUE}, null, "buildDeepSeq");
            javaClassRep.addMethod(javaMethod);

            // Add the throws declaration
            javaMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);

            // Add the method body.
            // Evaluate each child.
            JavaExpression rhs = METHODVAR_RHS;

            for (int i = dc.getArity() - 1; i >= 0; --i) {
                if (fieldStrictness[i] && !SCJavaDefn.typeExprToTypeName(fieldTypes[i]).equals(JavaTypeNames.RTVALUE)) {
                    // Strict primitive/foreign types don't need to have anything done.
                    continue;
                }

                // We want to build up an expression like:
                // deepSeq(field1 (deepSeq field2 (deepSeq field3 rhs)))
                // However, for non-strict fields we want to access them via the get_field accessor
                // to ensure the compacting of any indirection chains.
                JavaExpression deepSeqArgs[] = new JavaExpression [2];
                if (fieldStrictness[i]) {
                    deepSeqArgs[0] = new JavaField.Instance(null, javaFieldNames[i], JavaTypeNames.RTVALUE);
                } else {
                    deepSeqArgs[0] = new MethodInvocation.Instance(null, "get"+javaFieldNames[i], JavaTypeNames.RTVALUE, MethodInvocation.InvocationType.VIRTUAL);
                }
                deepSeqArgs[1] = rhs;
                rhs = SCJavaDefn.createApplyInvocation(METHODVAR_DEEPSEQ, deepSeqArgs);
            }

            javaMethod.addStatement(new ReturnStatement(rhs));

        }

        /**
         * If generation of statistics is turned on this method
         * adds the appropriate code to augment the statistics.
         * @param javaCons
         */
        private void addStatsBlock(JavaConstructor javaCons) {
            if (LECCMachineConfiguration.generateStatistics()) {
                MethodInvocation mi = new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR, "incrementNDataTypeInstances", JavaTypeName.VOID, MethodInvocation.InvocationType.VIRTUAL);
                javaCons.addStatement(new ExpressionStatement(mi));
            }
            if (LECCMachineConfiguration.generateCallCounts()) {
                JavaExpression args[] = new JavaExpression[2];
                JavaTypeName argTypes[] = new JavaTypeName[2];
                args[0] = LiteralWrapper.make(dc.getName().getModuleName().toSourceText());
                args[1] = LiteralWrapper.make(dc.getName().getUnqualifiedName());
                argTypes[0] = argTypes[1] = JavaTypeName.STRING;
                MethodInvocation mi = new MethodInvocation.Instance(SCJavaDefn.EXECUTION_CONTEXT_VAR, "dcConstructorCalled", args, argTypes, JavaTypeName.VOID, MethodInvocation.InvocationType.VIRTUAL);
                javaCons.addStatement(new ExpressionStatement(mi));
            }

        }
    }


    /**
     * A helper class used to bundle a field name, TypeExpr and the strictness of all fields with
     * that name/type.
     */
    private static final class FieldTypeAndStrictness {
        final FieldName fieldName;
        final String javaFieldName;
        final TypeExpr type;
        int strictness; // 1 - all strict, 0 - all lazy, -1 - mixed
        FieldTypeAndStrictness (FieldName fieldName, String javaFieldName, TypeExpr type, int strictness) {
            this.fieldName = fieldName;
            this.javaFieldName = javaFieldName;
            this.type = type;
            this.strictness = strictness;
        }
    }

}

