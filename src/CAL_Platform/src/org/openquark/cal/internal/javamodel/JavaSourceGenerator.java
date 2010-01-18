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
 * JavaSourceGenerator.java
 * Creation date: Sep 11, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openquark.cal.compiler.StringEncoder;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayAccess;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayLength;
import org.openquark.cal.internal.javamodel.JavaExpression.Assignment;
import org.openquark.cal.internal.javamodel.JavaExpression.CastExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassInstanceCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassLiteral;
import org.openquark.cal.internal.javamodel.JavaExpression.InstanceOf;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalName;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.PlaceHolder;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField.Instance;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField.Static;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField.This;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression.Binary;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression.Ternary;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression.Unary;
import org.openquark.cal.internal.javamodel.JavaStatement.AssertStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.Block;
import org.openquark.cal.internal.javamodel.JavaStatement.ExpressionStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.IfThenElseStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.JavaDocComment;
import org.openquark.cal.internal.javamodel.JavaStatement.LabelledContinue;
import org.openquark.cal.internal.javamodel.JavaStatement.LineComment;
import org.openquark.cal.internal.javamodel.JavaStatement.LocalVariableDeclaration;
import org.openquark.cal.internal.javamodel.JavaStatement.MultiLineComment;
import org.openquark.cal.internal.javamodel.JavaStatement.ReturnStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SynchronizedMethodInvocation;
import org.openquark.cal.internal.javamodel.JavaStatement.ThrowStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.UnconditionalLoop;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement.IntCaseGroup;
import org.openquark.cal.util.ArrayStack;


/**
 * A class to generate Java source code given an object representation.
 * @author Edward Lam
 */
public class JavaSourceGenerator {

    /** The length of an indent. */
    private static final int INDENT_LENGTH = 4;

    /** Line separator. */
    private static final String EOL = System.getProperty("line.separator");

    /**
     * Constructor for a JavaSourceGenerator
     */
    JavaSourceGenerator() {
    }


    /**
     * Holds class names used, etc.
     * TODOEL: manage namespace collisions.
     * @author Edward Lam
     */
    static class GenerationContext {

        private final JavaTypeName classTypeName;

        private Map<String, JavaTypeName> unqualifiedNameToTypeNameMap = new TreeMap<String, JavaTypeName>();       // TODOEL: resolve conflicts.

        /**
         * Constructor for a GenerationContext
         * @param classTypeName
         */
        GenerationContext(JavaTypeName classTypeName) {
            this.classTypeName = classTypeName;
        }

        /**
         * Get the name that can be used in java source for a given type (class).
         * @param typeName
         * @return String
         */
        String getSourceName(JavaTypeName typeName) {
            // We can declare an import and return the short class name if:
            // 1) There isn't another class with the same name imported from another module.
            // 2) The imported class doesn't have the same name as the class we are creating.


            switch (typeName.getTag()) {

                case JavaTypeName.VOID_TAG:
                case JavaTypeName.BOOLEAN_TAG:
                case JavaTypeName.BYTE_TAG:
                case JavaTypeName.SHORT_TAG:
                case JavaTypeName.CHAR_TAG:
                case JavaTypeName.INT_TAG:
                case JavaTypeName.LONG_TAG:
                case JavaTypeName.DOUBLE_TAG:
                case JavaTypeName.FLOAT_TAG:
                    return typeName.getName();

                case JavaTypeName.OBJECT_TAG:
                {
                    JavaTypeName.Reference.Object referenceType = (JavaTypeName.Reference.Object)typeName;

                    // Same type name as this class.
                    if (typeName.equals(classTypeName)) {
                        return referenceType.getBaseName();
                    }

                    // if this type is in java.lang we will need to fully qualify it.
                    // This prevents conflicts with other classes with the same name
                    // ex. mypackage.String
                    if (typeName.getPackageName().equals("java.lang")) {
                        return referenceType.getFullJavaSourceName();
                    }

                    JavaTypeName.Reference.Object classType = (JavaTypeName.Reference.Object)classTypeName;
                    String importName = referenceType.getImportName();

                    // Same enclosing class (ie. for nested classes..).
                    if (importName.equals(classType.getImportName())) {
                        return referenceType.getBaseName();
                    }

                    String objectPackageName = referenceType.getPackageName();
                    String unqualifiedName = referenceType.getUnqualifiedJavaSourceName();

                    // Same unqualified name as this class, different module.
                    if (unqualifiedName.equals(classType.getUnqualifiedJavaSourceName())) {
                        return referenceType.getFullJavaSourceName();
                    }

                    // Inner class, where outer class has same name as this class.
                    String tempName = unqualifiedName;
                    int index = tempName.indexOf (".");
                    while (index >= 0) {
                        String tempUnqualifiedName = tempName.substring(0, index);
                        // Same unqualified name as this class, different module.
                        if (tempUnqualifiedName.equals(classType.getUnqualifiedJavaSourceName())) {
                            return referenceType.getFullJavaSourceName();
                        }
                        tempName = tempName.substring(index + 1);
                        index = tempName.indexOf (".");
                    }

                    // We want to key the unqualified name to typename map off of the containing class name
                    // in cases where we are dealing with an inner class.
                    int dotIndex = unqualifiedName.indexOf (".");
                    String lookupName = (dotIndex != -1) ? unqualifiedName.substring(0, dotIndex): unqualifiedName;
                    JavaTypeName existingTypeName = unqualifiedNameToTypeNameMap.get(lookupName);
                    if (existingTypeName == null) {
                        unqualifiedNameToTypeNameMap.put(lookupName, typeName);
                        return referenceType.getUnqualifiedJavaSourceName();
                    }

                    if (objectPackageName.equals(existingTypeName.getPackageName())) {
                        // There is already a match for this type, or the containing type if this is
                        // an inner class, simply return the unqualifiedName
                        return unqualifiedName;

                    }

                    // There is a match on the unqualifiedName but it is not the same type.
                    return referenceType.getFullJavaSourceName();
                }

                case JavaTypeName.ARRAY_TAG:
                {

                    JavaTypeName.Reference.Array arrayType = (JavaTypeName.Reference.Array)typeName;
                    int nDimensions = arrayType.getNDimensions();
                    JavaTypeName elementType = arrayType.getElementType();

                    // Get the source for the element type, and append "[]" for each dimension.
                    StringBuilder sb = new StringBuilder(getSourceName(elementType));
                    for (int i = 0; i < nDimensions; i++) {
                        sb.append("[]");
                    }

                    return sb.toString();

                }

                default:
                {
                    throw new NullPointerException("Unrecognized class: " + typeName.getName());
                }
            }

        }

        /**
         * Get the type names for which unqualified names were output in source.
         * @return (List of JavaTypeName)
         */
        List<JavaTypeName> getTypeNames() {
            return new ArrayList<JavaTypeName>(unqualifiedNameToTypeNameMap.values());
        }
    }

    /**
     * Emit import statements.
     * @param leccPackageName
     * @param topLevelClassName
     * @param context
     * @return String
     */
    private static String genS_Imports(String leccPackageName, String topLevelClassName, GenerationContext context) {

        // Get the sorted set of import names.
        Set<String> importNameSet = new TreeSet<String>();

        for (final JavaTypeName typeName : context.getTypeNames()) {
            String packageName = typeName.getPackageName();
            if (packageName.equals("") || packageName.equals("java.lang") || packageName.equals(leccPackageName)) {
                continue;
            }

            // Check to see if the class is an inner class of the current class.
            if (packageName.equals(topLevelClassName)) {
                continue;
            }
            importNameSet.add(((JavaTypeName.Reference.Object)typeName).getImportName());
        }

        // Emit the imports.
        StringBuilder sb = new StringBuilder();
        for (final String importName : importNameSet) {
            emitLine(sb, 0, "import " + importName + ";");
        }

        return sb.toString();
    }

    /**
     * Get the Java source code for the given top-level class.
     * @param classRep the representation for the top-level class.
     * @return the java source
     * @throws JavaGenerationException
     */
    public static String generateSourceCode(JavaClassRep classRep) throws JavaGenerationException {

        StringBuilder sb = new StringBuilder();

        JavaTypeName classTypeName = classRep.getClassName();
        String packageName = classTypeName.getPackageName();

        // Create a generation context..
        GenerationContext context = new GenerationContext(classTypeName);

        // Emit the class comemnt if any.
        if (classRep.getComment() != null) {
            sb.append(getSource(classRep.getComment(), context, 0));
            emitLine(sb);
        }

        // Emit SC package and banner
        emitLine(sb, 0, "package " + packageName + ";");
        emitLine(sb);

        // Next we need to walk the java model building up the import information.
        ImportGenerator ig = new ImportGenerator(context);
        classRep.accept(ig, null);

        // Emit imports and class comment.
        String imports = genS_Imports(packageName, classTypeName.getFullJavaSourceName(), context);
        sb.append(imports);
        emitLine(sb);

        sb.append(getSource(classRep, context, 0));

        return sb.toString();
    }

    /**
     * Get a String consisting of the opening line for a Java declaration for the given Java class rep.
     * eg. "public static class Foo extends Bar implements Baz {"
     *
     * @param classRep the Java class representation
     * @param context the generation context
     * @return the opening line of the Java class, ending in an open paren.
     */
    private static String getClassDeclaration(JavaClassRep classRep, GenerationContext context) {

        String unqualifiedClassName = ((JavaTypeName.Reference.Object)classRep.getClassName()).getBaseName();

        StringBuilder classDeclaration = new StringBuilder(Modifier.toString(classRep.getModifiers()));
        classDeclaration.append(" class " + unqualifiedClassName + " ");

        JavaTypeName superclassName = classRep.getSuperclassName();
        if (!superclassName.equals(JavaTypeName.OBJECT)) {
            classDeclaration.append("extends " + context.getSourceName(superclassName) + " ");
        }

        final int nInterfaces = classRep.getNInterfaces();
        if (nInterfaces > 0) {
            classDeclaration.append("implements ");
            for (int i = 0; i < nInterfaces; i++) {
                if (i > 0) {
                    classDeclaration.append(", ");
                }
                classDeclaration.append(context.getSourceName(classRep.getInterface(i)));
            }
            classDeclaration.append(" ");
        }

        classDeclaration.append("{");

        return classDeclaration.toString();
    }

    /**
     * Get a String consisting of the opening line for a Java declaration for the given Java constructor.
     * eg. "public static Bar() {"
     *
     * @param javaConstructor
     * @param context the generation context
     * @return the opening line of the Java constructor, ending in an open paren.
     */
    private static String getConstructorDeclaration(JavaConstructor javaConstructor, GenerationContext context) {

        // Initialize with access flags.
        StringBuilder constructorDeclaration = new StringBuilder(Modifier.toString(javaConstructor.getModifiers())).append(' ');

        // Method name
        constructorDeclaration.append(javaConstructor.getConstructorName() + "(");

        // Parameters
        final int nParams = javaConstructor.getNParams();
        for (int i = 0; i < nParams; i++) {
            if (i > 0) {
                constructorDeclaration.append(", ");
            }
            constructorDeclaration.append(context.getSourceName(javaConstructor.getParamType(i)) + " " + javaConstructor.getParamName(i));
        }

        // Close paren, open brace
        constructorDeclaration.append(") {");

        return constructorDeclaration.toString();
    }

    /**
     * Get a String consisting of the opening line for a Java declaration for the given Java method.
     * eg. "public static void foo throws Bar {"
     *
     * @param javaMethod the Java method representation
     * @param context the generation context
     * @return the opening line of the Java method, ending in an open paren.
     */
    private static String getMethodDeclaration(JavaMethod javaMethod, GenerationContext context) {

        // Initialize with access flags.
        StringBuilder methodDeclaration = new StringBuilder(Modifier.toString(javaMethod.getModifiers())).append(' ');

        // Return type
        methodDeclaration.append(context.getSourceName(javaMethod.getReturnType()) + " ");

        // Method name
        methodDeclaration.append(javaMethod.getMethodName() + "(");

        // Parameters
        for (int i = 0, nParams = javaMethod.getNParams(); i < nParams; i++) {
            if (i > 0) {
                methodDeclaration.append(", ");
            }

            if (javaMethod.isParamFinal(i)) {
                methodDeclaration.append("final ");
            }

            methodDeclaration.append(context.getSourceName(javaMethod.getParamType(i))).append(" ").append(javaMethod.getParamName(i));
        }

        // Close paren
        methodDeclaration.append(") ");

        // throws
        final int nThrownExceptions = javaMethod.getNThrownExceptions();
        if (nThrownExceptions > 0) {
            methodDeclaration.append("throws ");

            for (int i = 0; i < nThrownExceptions; ++i) {
                JavaTypeName thrownExceptionName = javaMethod.getThrownException(i);
                methodDeclaration.append(context.getSourceName(thrownExceptionName));

                if (i < nThrownExceptions - 1) {
                    methodDeclaration.append(",");
                }
                methodDeclaration.append(" ");
            }
        }

        // Open brace
        methodDeclaration.append("{");

        return methodDeclaration.toString();
    }

    /**
     * Get the Java source for a given java class representation.
     * @param classRep the java class representation
     * @param context the generation context
     * @param indent the indent level of the source
     * @return the java source
     * @throws JavaGenerationException
     */
    private static String getSource(JavaClassRep classRep, GenerationContext context, int indent) throws JavaGenerationException{
        StringBuilder sb = new StringBuilder();

        // Emit class JavaDoc
        if (classRep.getJavaDoc() != null) {
            sb.append(getSource(classRep.getJavaDoc(), context, indent).toString());
        }

        // Emit class declaration
        {
            emitLine(sb, indent, getClassDeclaration(classRep, context));
        }

        Iterator<Object> elements = classRep.getClassContent().iterator();
        while (elements.hasNext()) {
            Object element = elements.next();

            if (element instanceof JavaFieldDeclaration) {
                // Emit a field declaration.
                JavaFieldDeclaration fieldDeclaration = (JavaFieldDeclaration)element;
                sb.append(getSource(fieldDeclaration, context, indent+1));
            } else
            if (element instanceof JavaConstructor) {
                // Emit constructor
                JavaConstructor javaConstructor = (JavaConstructor)element;
                sb.append(getSource(javaConstructor, context, indent + 1));
                // extra EOL
                emitLine(sb);
            } else
            if (element instanceof JavaMethod) {
                // Emit method
                JavaMethod javaMethod = (JavaMethod)element;
                sb.append(getSource(javaMethod, context, indent + 1));

                // extra EOL
                emitLine(sb);
            } else
            if (element instanceof JavaClassRep) {
                // Emit inner classe
                JavaClassRep innerClassRep = (JavaClassRep)element;
                sb.append(getSource(innerClassRep, context, indent + 1));

                // extra EOL
            } else
            if (element instanceof MultiLineComment) {
                sb.append(getSource((MultiLineComment)element, context, indent + 1));
                emitLine(sb);
            } else {
                throw new JavaGenerationException ("Unhandled top level element in JavaClassRep: " + element.getClass().getName());
            }
        }

        // Close paren.
        {
            emitLine(sb, indent, "}");
        }

        return sb.toString();
    }

    /**
     * Get the Java source for a given java field declaration.
     * @param fieldDeclaration the java field declaration
     * @param context the generation context
     * @param indent the indent level of the source
     * @return String
     * @throws JavaGenerationException
     */
    private static String getSource(JavaFieldDeclaration fieldDeclaration, GenerationContext context, int indent) throws JavaGenerationException {
        StringBuilder block = new StringBuilder();

        JavaDocComment jdc = fieldDeclaration.getJavaDoc();
        if (jdc != null) {
            block.append(getSource(jdc, context, indent));
        }

        String fieldName = fieldDeclaration.getFieldName();
        JavaTypeName fieldType = fieldDeclaration.getFieldType();
        JavaExpression initializer = fieldDeclaration.getInitializer();

        // flags, type, name

        final StringBuilder declaration;
        {
            final String modifiers = Modifier.toString(fieldDeclaration.getModifiers());
            if (modifiers.length() > 0) {
                declaration = new StringBuilder(modifiers).append(' ');
            } else {
                declaration = new StringBuilder();
            }
        }

        declaration.append(context.getSourceName(fieldType)).append(' ').append(fieldName);

        // " = " , initializer.
        if (initializer != null) {
            declaration.append(" = ");
            declaration.append(getSource(initializer, indent, declaration.length(), context));
        }

        declaration.append(";");

        emitLine(block, indent, declaration.toString());
        emitLine(block);

        return block.toString();
    }

    /**
     * Get the Java source for a given java constructor.
     * @param javaConstructor the Java constructor representation
     * @param context the generation context
     * @param indent the indent level of the source
     * @return String
     * @throws JavaGenerationException
     */
    private static String getSource(JavaConstructor javaConstructor, GenerationContext context, int indent) throws JavaGenerationException {

        StringBuilder sb = new StringBuilder();

        emitLine(sb, indent, getConstructorDeclaration(javaConstructor, context));

        if (javaConstructor.getSuperConstructorParamValues().length > 0) {
            StringBuilder superCall = new StringBuilder();
            superCall.append("super (");
            for (int i = 0, n = javaConstructor.getSuperConstructorParamValues().length; i < n; ++i) {
                superCall.append(getSource(javaConstructor.getSuperConstructorParamValues()[i], indent + 1, superCall.length(), context));
                if (i < (n-1)) {
                    superCall.append(", ");
                }
            }
            superCall.append(");");
            emitLine(sb, indent+1, superCall.toString());
        }
        sb.append(getSource(javaConstructor.getBodyCode(), context, indent + 1));
        emitLine(sb, indent, "}");

        return sb.toString();
    }

    /**
     * Get the Java source for a given java method.
     * @param javaMethod the Java method representation
     * @param context the generation context
     * @param indent the indent level of the source
     * @return String
     * @throws JavaGenerationException
     */
    private static String getSource(JavaMethod javaMethod, GenerationContext context, int indent) throws JavaGenerationException {

        StringBuilder sb = new StringBuilder();

        if (javaMethod.getJavaDocComment() != null) {
            sb.append(getSource(javaMethod.getJavaDocComment(), context, indent));
        }

        emitLine(sb, indent, getMethodDeclaration(javaMethod, context));
        sb.append(getSource(javaMethod.getBodyCode(), context, indent + 1));
        emitLine(sb, indent, "}");

        return sb.toString();
    }

    /**
     * Get the Java source for a given java statement.
     * @param statement the java statement for which to generate source.
     * @param context the generation context.
     * @param indent the indent level of the source
     * @return StringBuilder
     * @throws JavaGenerationException
     */
    private static StringBuilder getSource(JavaStatement statement, GenerationContext context, int indent) throws JavaGenerationException {
        StringBuilder sb = new StringBuilder();

        if (statement instanceof LocalVariableDeclaration) {
            LocalVariableDeclaration declaration = (LocalVariableDeclaration)statement;
            LocalVariable localVariable = declaration.getLocalVariable();

            emitIndent(sb, indent);

            // final
            if (declaration.isFinal()) {
                sb.append("final ");
            }

            // variable type and name
            sb.append(context.getSourceName(localVariable.getTypeName()) + " " + localVariable.getName());

            // " = " + initializer
            JavaExpression initializer = declaration.getInitializer();
            if (initializer != null) {
                sb.append(" = ");
                sb.append(getSource(initializer, indent, sb.length(), context));
            }

            // ;
            sb.append(";" + EOL);


        } else if (statement instanceof ExpressionStatement) {
            ExpressionStatement expressionStatement = (ExpressionStatement)statement;
            String expressionSource = getSource(expressionStatement.getJavaExpression(), indent, 0, context);
            emitLine(sb, 0, expressionSource + ";");

        } else if (statement instanceof LineComment) {
            LineComment lineComment = (LineComment)statement;
            emitLine(sb, indent, "// " + lineComment.getCommentText());

        } else if (statement instanceof MultiLineComment) {
            boolean isJavaDoc = (statement instanceof JavaDocComment);
            MultiLineComment jdc = (MultiLineComment)statement;

            Iterator<String> it = jdc.getCommentLines().iterator();
            String firstLine = it.next();
            boolean nd = !firstLine.startsWith("/*");
            if (nd) {
                emitLine(sb, indent, isJavaDoc ? "/**" : "/*");
            }
            it = jdc.getCommentLines().iterator();
            while (it.hasNext()) {
                String line = it.next();
                if (nd) {
                    emitLine(sb, indent, " * " + line);
                } else {
                    emitLine(sb, indent, line);
                }
            }
            if (nd) {
                emitLine(sb, indent, " */");
            }

        } else if (statement instanceof ReturnStatement) {
            ReturnStatement returnStatement = (ReturnStatement)statement;
            if (returnStatement.getReturnExpression() == null) {
                emitLine(sb, indent, "return;");
            } else {
                emitLine(sb, indent, "return " + getSource(returnStatement.getReturnExpression(),indent, 7, context) +  ";");
            }
        } else if (statement instanceof IfThenElseStatement) {
            IfThenElseStatement iteStatement = (IfThenElseStatement)statement;

            emitLine(sb, indent, "if (" + getSource(iteStatement.getCondition(), indent, 4, context) + ") {");

            sb.append(getSource(iteStatement.getThenStatement(), context, indent + 1));

            // Emit the else clause only if there is one.
            StringBuilder elseClause = getSource(iteStatement.getElseStatement(), context, indent + 1);

            if (elseClause.length() > 0) {
                emitLine(sb, indent, "} else {");
                sb.append(elseClause);
            }

            emitLine(sb, indent, "}");

        } else if (statement instanceof SwitchStatement) {
            SwitchStatement switchStatement = (SwitchStatement)statement;

            JavaExpression condition = switchStatement.getCondition();
            List<IntCaseGroup> caseGroups = switchStatement.getCaseGroups();
            JavaStatement defaultStatementGroup = switchStatement.getDefaultStatement();

            // Sort cases by the first case label in each group.
            Collections.sort(caseGroups, new Comparator<IntCaseGroup>() {

                public int compare(IntCaseGroup o1, IntCaseGroup o2) {
                    int int1 = o1.getNthCaseLabel(0);
                    int int2 = o2.getNthCaseLabel(0);
                    if (int1 < int2) {
                        return -1;
                    }
                    if (int1 > int2) {
                        return 1;
                    }
                    return 0;
                }
            });

            emitIndent (sb, indent);
            int length = sb.length();

            sb.append("switch (");
            String conditionString = getSource(condition, 0, 0, context, true);
            conditionString = conditionString.replaceAll(EOL," ");
            sb.append(conditionString);
            if (sb.lastIndexOf(EOL, length) >= 0) {
                emitLine(sb);
                emitIndent(sb, indent);
                for (int i = 0; i < 7; ++i) {
                    sb.append(" ");
                }
                sb.append (") {");
                emitLine (sb);
            } else {
                sb.append(") {");
                emitLine(sb);
            }

            emitLine(sb);

            // case labels and their statement groups.
            for (final IntCaseGroup switchCaseGroup : caseGroups) {

                JavaStatement caseStatementGroup = switchCaseGroup.getStatement();

                int nCaseLabels = switchCaseGroup.getNCaseLabels();
                for (int i = 0; i < nCaseLabels - 1; i++) {
                    emitLine(sb, indent + 1, "case " + switchCaseGroup.getNthCaseLabel(i) + ":");
                }
                emitLine(sb, indent + 1, "case " + switchCaseGroup.getNthCaseLabel(nCaseLabels - 1) + ": {");

                sb.append(getSource(caseStatementGroup, context, indent + 2));
                emitLine(sb, indent + 1, "}");
                emitLine(sb);
            }

            // default label and statement group.
            if (defaultStatementGroup != null) {
                emitLine(sb, indent + 1, "default: {");
                sb.append(getSource(defaultStatementGroup, context, indent + 2));
                emitLine(sb, indent + 1, "}");
            }

            emitLine(sb, indent, "}");

        } else if (statement instanceof Block) {
            Block block = (Block)statement;

            int blockIndent = indent;
            List<JavaExceptionHandler> exceptionHandlers = block.getExceptionHandlers();
            if (!exceptionHandlers.isEmpty()) {
                emitLine(sb, indent, "try {");
                blockIndent++;
            }

            boolean doingLocalVarDeclarations = false;
            boolean wasLineComment = false;
            int nStatements = block.getNBlockStatements();
            for (int i = 0; i < nStatements; i++) {
                JavaStatement blockStatement = block.getNthBlockStatement(i);

                // Try to separate local var declarations from other types of block statements.
                boolean isLocalVarDeclaration = blockStatement instanceof LocalVariableDeclaration;
                if (!wasLineComment && isLocalVarDeclaration != doingLocalVarDeclarations && i > 0) {
                    sb.append(EOL);
                }
                doingLocalVarDeclarations = isLocalVarDeclaration;
                wasLineComment = blockStatement instanceof LineComment;

                // Now append the source.
                sb.append(getSource(blockStatement, context, blockIndent));
            }

            if (!exceptionHandlers.isEmpty()) {
                for (final JavaExceptionHandler eh : exceptionHandlers) {
                    emitLine(sb, indent, "} catch (" + fixupClassName(eh.getExceptionClass().getName()) + " " + eh.getExceptionVarName() + ") {");
                    sb.append(getSource(eh.getHandlerCode(), context, blockIndent));
                }
                emitLine(sb, indent, "}");

            }

        } else
        if (statement instanceof ThrowStatement) {
            ThrowStatement throwStatement = (ThrowStatement)statement;
            emitLine(sb, indent, "throw " + getSource(throwStatement.getThrownExpression(), indent, 6, context) + ";");
        } else
        if (statement instanceof UnconditionalLoop) {
            UnconditionalLoop whileStatement = (UnconditionalLoop)statement;
            emitLine (sb, indent, whileStatement.getLabel() + ": while (true) {");
            sb.append(getSource(whileStatement.getBody(), context, indent + 1));
            emitLine (sb, indent, "}");
        } else
        if (statement instanceof LabelledContinue) {
            LabelledContinue lc = (LabelledContinue)statement;
            emitLine (sb, indent, "continue " + lc.getLabel() + ";");
        } else
        if (statement instanceof SynchronizedMethodInvocation){
            // A method invocation wrapped in a synchronization block.
            SynchronizedMethodInvocation sof = (SynchronizedMethodInvocation)statement;

            // Start the synchronized block.
            emitLine (sb, indent, "synchronized (" + getSource(sof.getSynchronizingObject(), indent, 14, context) + ") {");

            // Add the method invocation.
            sb.append (getSource(new ExpressionStatement(sof.getMethodInvocation()), context, indent + 1));

            // Finish the block.
            emitLine (sb, indent, "}");
        } else
        if (statement instanceof AssertStatement) {
            AssertStatement ast = (AssertStatement)statement;
            JavaExpression conditionExpression = ast.getConditionExpr();
            JavaExpression failureExpression = ast.getOnFailureExpr();

            if (failureExpression != null) {
                emitIndent(sb, indent);
                sb.append("assert (");
                sb.append(getSource(conditionExpression, indent, 8, context));
                sb.append(") : (");
                sb.append(getSource(failureExpression, indent+1, 5, context));
                sb.append(");");
                emitLine(sb);
            } else {
                emitLine (sb, indent, "assert (" + getSource(conditionExpression, indent, 8, context) + ");");
            }

        } else {
            throw new JavaGenerationException("Unrecognized statement type: " + statement.getClass());
        }
        return sb;
    }


    /**
     * Get the Java source for a given java expression.
     * @param expression the java expression
     * @param context the generation context
     * @param indentLevel
     * @param startOffset
     * @return String
     */
    private static String getSource(JavaExpression expression, int indentLevel, int startOffset, GenerationContext context) {
        return getSource (expression, indentLevel, startOffset, context, false);
    }

    private static String getSource(JavaExpression expression, int indentLevel, int startOffset, GenerationContext context, boolean singleLine) {

        ExpressionTextGenerator etg = new ExpressionTextGenerator(context);
        expression.accept(etg, null);
        ExpressionTextNode rootNode = etg.getCurrentNode().getNthChild(0);
        StringBuilder sb = new StringBuilder();
        if (!singleLine) {
            rootNode.formatExpressionText(sb, indentLevel, startOffset);
        } else {
            rootNode.appendExpressionText(sb);
        }

        return sb.toString();

    }

    /**
     * Emit an indent.
     * @param sb the StringBuilder to which to add an indent.
     * @param indent the number of indents to add.
     * @return StringBuilder sb, returned for convenience.
     */
    private static StringBuilder emitIndent(StringBuilder sb, int indent) {
        // NOTE: we use the tab character '\t' instead of adding spaces
        // because of memory issues.
        // When using spaces instead of '\t' some of our generated java source
        // files were large enough to cause out-of-memory errors.
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
        return sb;
    }

    /**
     * Emit an empty line.
     * @param sb the StringBuilder to which to add the empty line.
     */
    private static void emitLine(StringBuilder sb) {
        sb.append(EOL);
    }

    /**
     * Emit an indent, some text, and an EOL.
     * @param sb the StringBuilder to which to add an indent.
     * @param text the text to add.
     * @param indent the number of indents to add.
     */
    private static void emitLine(StringBuilder sb, int indent,String text) {
        emitIndent(sb, indent);
        sb.append(text + EOL);
    }

    /**
     * A textual representation of a JavaClassRep to be used for debugging purposes only.
     * @param classRep
     * @return String
     */
    static String toDebugString(JavaClassRep classRep) {

        try {
            return generateSourceCode(classRep);
        } catch (JavaGenerationException e) {
            JavaTypeName classTypeName = classRep.getClassName();
            return "JavaGenerationException generating source for JavaClassRep: " + classTypeName + ".";
        }
    }

    /**
     * A textual representation of a JavaMethod to be used for debugging purposes only.
     * With respect to use of qualified symbols, the method is treated as being defined in the java.lang.Object class.
     * @param javaMethod
     * @return String
     */
    static String toDebugString(JavaMethod javaMethod) {

        try {
            return getSource(javaMethod, new GenerationContext(JavaTypeName.OBJECT), 0);
        } catch (JavaGenerationException cge) {
            return "JavaGenerationException generating source for JavaMethod: " + javaMethod.getMethodName() + ".";
        }
    }

    /**
     * A textual representation of a JavaConstructor to be used for debugging purposes only.
     * With respect to use of qualified symbols, the constructor is treated as being defined in the java.lang.Object class.
     * @param javaConstructor
     * @return String
     */
    static String toDebugString(JavaConstructor javaConstructor) {

        try {
            return getSource(javaConstructor, new GenerationContext(JavaTypeName.OBJECT), 0);
        } catch (JavaGenerationException cge) {
            return "JavaGenerationException generating source for JavaConstructor: " + javaConstructor.getConstructorName() + ".";
        }
    }

    /**
     * A textual representation of a JavaStatement to be used for debugging purposes only.
     * With respect to use of qualified symbols, the statement is treated as being defined in the java.lang.Object class.
     * @param javaStatement
     * @return String
     */
    static String toDebugString(JavaStatement javaStatement) {

        try {
            return getSource(javaStatement, new GenerationContext(JavaTypeName.OBJECT), 0).toString();
        } catch (JavaGenerationException cge) {
            return "JavaGenerationException generating source for JavaStatement: " + javaStatement.getClass() + ".";
        }
    }

    /**
     * A textual representation of a JavaExpression to be used for debugging purposes only.
     * With respect to use of qualified symbols, the expression is treated as being defined in the java.lang.Object class.
     * @param javaExpression
     * @return String
     */
    static String toDebugString(JavaExpression javaExpression) {

        return getSource(javaExpression, 0, 0, new GenerationContext(JavaTypeName.OBJECT));
    }

    /**
     * A textual representation of a JavaFieldDeclaration to be used for debugging purposes only.
     * With respect to use of qualified symbols, the field declaration is treated as being defined in the java.lang.Object class.
     * @param javaFieldDeclaration
     * @return String
     */
    static String toDebugString(JavaFieldDeclaration javaFieldDeclaration) {

        try {
            return getSource(javaFieldDeclaration, new GenerationContext(JavaTypeName.OBJECT), 0);
        } catch (JavaGenerationException cge) {
            return "JavaGenerationException generating source for JavaFieldDeclaration: " + javaFieldDeclaration.getClass() + ".";
        }
    }

    /**
     * Return the name of the class, in a form that can be used in source code.
     * eg. [[B ==> byte[][].
     *     CALExecutor$ForeignFunctionException ==> CALExecutor.ForiegnFunctionException.
     * @param name
     * @return String
     */
    static String fixupClassName (String name) {

        // Count the number of array dimensions (if any).
        int i = 0;
        while (name.startsWith("[")) {
            i++;
            name = name.substring(1);
        }

        if (name.startsWith ("L") && name.endsWith(";")) {
            // This is a fully qualified class name.
            name = name.substring (1, name.length() - 1);
        } else
        if (name.equals ("Z")) {
            // boolean
            name = "boolean";
        } else
        if (name.equals ("B")) {
            name = "byte";
        } else
        if (name.equals ("C")) {
            name = "char";
        } else
        if (name.equals("S")) {
            name = "short";

        } else
        if (name.equals ("I")) {
            name = "integer";
        } else
        if (name.equals ("J")) {
            name = "long";
        } else
        if (name.equals ("F")) {
            name = "float";
        } else
        if (name.equals("D")) {
            name = "double";
        }

        for (int j = 0; j < i; ++j) {
            name = name + "[]";
        }

        // Substitute . for $
        name = name.replace ('$', '.');

        return name;
    }

    /**
     * A class used for generating Java source code from a
     * JavaExpression.
     *
     * @author rcypher
     *
     */
    private static class ExpressionTextNode {
        /** Max length of a generated line of source. */
        private static final int MAX_LINE_LENGTH = 80;

        /** The text associated with this node. */
        private String myText = "";

        /** Child nodes. */
        private List<ExpressionTextNode> children = new ArrayList<ExpressionTextNode>();

        /** The type of formatting for this node. */
        private final FormatType formatType;

        /**
         * Create an ExpressionTextNode
         * @param formatType
         */
        ExpressionTextNode (FormatType formatType) {
            if (formatType == null) {
                throw new NullPointerException("Invalid null value for format type.");
            }

            this.formatType = formatType;
        }

        /**
         * Create an ExpressionTextNode
         * @param myText
         * @param formatType
         */
        ExpressionTextNode (String myText, FormatType formatType) {
            if (formatType == null) {
                throw new NullPointerException("Invalid null value for format type.");
            }

            this.myText = myText;
            this.formatType = formatType;
        }

        @Override
        public String toString () {
            StringBuilder sb = new StringBuilder();
            appendExpressionText(sb);
            return sb.toString();
        }

        String getThisText() {
            return myText;
        }

        void setThisText (String newText) {
            myText = newText;
        }

        int getNChildren () {
            return children.size();
        }

        ExpressionTextNode getNthChild (int index) {
            return children.get(index);
        }

        int getTotalTextLength () {
            int totalLength = getThisText().length();
            for (final ExpressionTextNode expressionTextNode : children) {
                totalLength += expressionTextNode.getTotalTextLength();
            }
            return totalLength;
        }

        void addChild (ExpressionTextNode node) {
            children.add(node);
        }

        /**
         * Prefix the supplied text to the text of
         * this node, if there is any.  Otherwise prefix
         * it to the leftmost child node.
         * @param prefixText
         */
        void prefixText (String prefixText) {
            if(myText != null && myText.length() > 0) {
                myText = prefixText + myText;
            } else
            if (getNChildren() > 0) {
                getNthChild(0).prefixText(prefixText);
            }
        }

        /**
         * Suffix the given text to this nodes text if
         * there are no children.  Otherwise suffix to the
         * rightmost child.
         * @param suffixText
         */
        void suffixText (String suffixText) {
            if (getNChildren() == 0) {
                myText = myText + suffixText;
            } else {
                children.get(children.size()-1).suffixText(suffixText);
            }
        }

        /**
         * Format the Java source expression represented by this ExpressionTextNode
         * @param sb - the StringBuilder to put the formatted source in.
         * @param indentLevel
         * @param offset - offset from the current indent
         */
        private void formatExpressionText (
                StringBuilder sb,
                int indentLevel,
                int offset) {

            // Update the offset if necessary.
            if (offset == 0) {
                offset = sb.length() - sb.lastIndexOf(EOL) - EOL.length();
                if (offset < 0) {
                    offset = 0;
                }
            }

            // Find starting position on current line.
            int startPos = offset + (indentLevel * INDENT_LENGTH);

            // If the expression fits on the remainder of the current line
            // we can simply append the text.
            if (getTotalTextLength() + startPos <= MAX_LINE_LENGTH) {

                // If we're at the beginning of a line we need to indent.
                if (offset == 0) {
                    emitIndent(sb, indentLevel);
                }

                appendExpressionText(sb);
                return;
            }

            // If switching to the next line will give more space
            // emit a line break and increase the indent level.
            if (offset > 0 && offset > INDENT_LENGTH) {
                emitLine(sb);
                formatExpressionText(sb, ++indentLevel, 0);
                return;
            }

            // We need to break up the expression.

            // The expression is broken up based on the format type
            if (getFormatType().equals(FormatType.ATOMIC)) {
                if (offset == 0) {
                    emitIndent(sb, indentLevel);
                }

                appendExpressionText(sb);
            } else
            if (getFormatType().equals(FormatType.CHILDREN_AT_SAME_LEVEL)) {
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndent(sb, indentLevel);
                    }
                    sb.append(getThisText());
                    emitLine(sb);
                    offset = 0;
                }
                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    getNthChild(i).formatExpressionText(sb, indentLevel, i == 0 ? offset : 0);
                    if (i < n-1) {
                        emitLine(sb);
                    }
                }
            } else
            if (getFormatType().equals(FormatType.CHILDREN_IN_ONE_LEVEL)) {
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitLine(sb, indentLevel, getThisText());
                    } else {
                        sb.append(getThisText());
                        emitLine(sb);
                        offset = 0;
                    }
                }

                // Format each child on a new line.
                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    getNthChild(i).formatExpressionText(sb, indentLevel+1, 0);
                    if (i < n-1) {
                        emitLine(sb);
                    }
                }
            } else
            if (getFormatType().equals(FormatType.FIRST_CHILD_AT_LEVEL)) {
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitLine(sb, indentLevel, getThisText());
                    } else {
                        sb.append(getThisText());
                        emitLine(sb);
                        offset = 0;
                    }
                }

                // Format first child at the same indent level.
                getNthChild(0).formatExpressionText(sb, indentLevel, offset);
                if (getNChildren() > 1) {
                    emitLine(sb);
                }

                // Format other children on new lines.
                for (int i = 1, n = getNChildren(); i < n; ++i) {
                    getNthChild(i).formatExpressionText(sb, indentLevel+1, 0);
                    if (i < n-1) {
                        emitLine(sb);
                    }
                }
            } else {
                // Default behaviour is to place this nodes text on the current line and then
                // format each child starting on a new line with the same indent.
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndent(sb, indentLevel);
                    }
                    sb.append(getThisText());
                    emitLine(sb);
                    offset = 0;
                }

                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    ExpressionTextNode child = getNthChild(i);
                    child.formatExpressionText(sb, indentLevel, offset);
                    if (!sb.toString().endsWith(EOL)) {
                        sb.append(EOL);
                    }
                }
            }

        }

        /**
         * Build up the concatenation of the text contained in
         * this node and all its children.
         * @param sb
         */
        private void appendExpressionText (StringBuilder sb) {
            sb.append(getThisText());
            for (int i = 0, n = getNChildren(); i < n; ++i) {
                getNthChild(i).appendExpressionText(sb);
            }

        }

        private FormatType getFormatType () {
            return formatType;
        }

        /**
         * A class used to indicate the type of formatting associated
         * with an ExpressionTextNode.
         * @author rcypher
         */
        static class FormatType {
            /** The text of the sub-tree cannot be broken apart. */
            static final FormatType ATOMIC = new FormatType(1, "ATOMIC");

            /** The text of the sub-tree can be broken between nodes.
             *  The children should have the same indent level as the root. */
            static final FormatType CHILDREN_AT_SAME_LEVEL = new FormatType(2, "CHILDREN_AT_SAME_LEVEL");

            /** The text of the sub-tree can be broken between nodes.
             *  The children should indent one more level than the root. */
            static final FormatType CHILDREN_IN_ONE_LEVEL = new FormatType(3, "CHILDREN_IN_ONE_LEVEL");

            /** The text of the sub-tree can be broken between nodes.
             *  The first child should have the same indent level as the root.
             *  Other children should be indented an additional level.*/
            static final FormatType FIRST_CHILD_AT_LEVEL = new FormatType(4, "FIRST_CHILD_AT_LEVEL");

            private final int type;
            private final String name;

            private FormatType (int type, String name) {
                this.type = type;
                this.name = name;
            }

            @Override
            public boolean equals(Object other) {
                return (other != null &&
                        other instanceof FormatType &&
                        ((FormatType)other).type == this.type);
            }

            @Override
            public String toString () {
                return name;
            }
        }
    }

    /**
     * This class is an extension of the JavaModelTraverser which
     * is used in generating Java source for from a JavaExpression.
     *
     * The JavaExpression is traversed and a tree of ExpressionTextNode
     * instances is created.  The text in each ExpressionTextNode is
     * considered to be an atomic part of the generated source (i.e.
     * it cannot be broken apart for formatting purposes)
     *
     * @author rcypher
     *
     */
    private static class ExpressionTextGenerator extends JavaModelTraverser<Void, Void> {
        ArrayStack<ExpressionTextNode> stack = ArrayStack.make();
        GenerationContext context;

        ExpressionTextGenerator (GenerationContext context) {
            this.context = context;
            stack.push(new ExpressionTextNode(ExpressionTextNode.FormatType.ATOMIC));
        }

        ExpressionTextNode getCurrentNode () {
            return stack.peek();
        }

        boolean doOptionalParenthesis (JavaExpression expression) {
            if (expression instanceof PlaceHolder) {
                expression = ((PlaceHolder)expression).getActualExpression();
            }

            boolean parenthesize =
                    (expression instanceof Assignment) ||
                    (expression instanceof CastExpression) ||
                    (expression instanceof ClassInstanceCreationExpression) ||
                    (expression instanceof ArrayCreationExpression) ||
                    (expression instanceof InstanceOf) ||
                    (expression instanceof OperatorExpression);        // sometimes not parenthesizing these are ok too..

            // parenthesize if the literal is a -ve number.
            if (!parenthesize && expression instanceof LiteralWrapper) {
                Object literalObject = ((LiteralWrapper)expression).getLiteralObject();
                if (literalObject instanceof Number && ((Number)literalObject).doubleValue() < 0) {
                    parenthesize = true;
                }
            }

            // don't parenthesize:
            //   (expression instanceof UnknownJavaExpression)
            //   (expression instanceof MethodInvocation)
            //   (expression instanceof JavaTypeName)
            //   (expression instanceof JavaField)
            //   (expression instanceof LocalVariable)
            //   (expression instanceof MethodVariable)
            //   (expression instanceof ArrayAccess)

            return parenthesize;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitArrayAccessExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ArrayAccess, java.lang.Object)
         */
        @Override
        public Void visitArrayAccessExpression(ArrayAccess arrayAccess, Void arg) {
            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_IN_ONE_LEVEL);
            stack.peek().addChild(node);
            stack.push(node);

            super.visitArrayAccessExpression(arrayAccess, arg);

            assert node.getNChildren() == 2;

            if (doOptionalParenthesis(arrayAccess.getArrayReference())) {
                node.getNthChild(0).prefixText("(");
                node.getNthChild(0).suffixText(")");
            }
            node.getNthChild(0).suffixText("[");
            node.getNthChild(1).suffixText("]");

            stack.pop();
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Void visitArrayLengthExpression(
                ArrayLength arrayLength,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_AT_SAME_LEVEL);
            stack.peek().addChild(node);
            stack.push(node);

            super.visitArrayLengthExpression(arrayLength, arg);

            if (doOptionalParenthesis(arrayLength.getArrayReference())) {
                node.getNthChild(0).prefixText("(");
                node.getNthChild(0).suffixText(")");
            }

            node.getNthChild(0).suffixText(".length");

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitArrayCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ArrayCreationExpression, java.lang.Object)
         */
        @Override
        public Void visitArrayCreationExpression(
                ArrayCreationExpression arrayCreation,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.ATOMIC);
            stack.peek().addChild(node);
            stack.push(node);

            try {
                String elementClassNameString = context.getSourceName(arrayCreation.getArrayElementTypeName());

                node.setThisText("new " + elementClassNameString + "[] {");
            } catch (Exception e) {

            }

            super.visitArrayCreationExpression(arrayCreation, arg);

            for (int i = 0, n = node.getNChildren()-1; i < n; ++i) {
                node.getNthChild(i).suffixText(", ");
            }
            node.suffixText("}");

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitAssignmentExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.Assignment, java.lang.Object)
         */
        @Override
        public Void visitAssignmentExpression(
                Assignment assignment,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_IN_ONE_LEVEL);
            stack.peek().addChild(node);
            stack.push(node);

            super.visitAssignmentExpression(assignment, arg);
            node.getNthChild(0).suffixText(" = ");

            if (doOptionalParenthesis(assignment.getValue())) {
                node.getNthChild(1).prefixText("(");
                node.getNthChild(1).suffixText(")");
            }
            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitCastExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.CastExpression, java.lang.Object)
         */
        @Override
        public Void visitCastExpression(
                CastExpression cast,
                Void arg) {

            // Check for redundant casts. i.e. (RTRecordValue)(RTRecordValue)expressionToCast

            if (cast.getExpressionToCast() instanceof CastExpression) {
                CastExpression innerCast = (CastExpression)cast.getExpressionToCast();
                if (cast.getCastType().equals(innerCast.getCastType())) {
                    return super.visitCastExpression(cast, arg);
                }
            }

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_IN_ONE_LEVEL);
            stack.peek().addChild(node);
            stack.push(node);

            String classNameString = context.getSourceName(cast.getCastType());
            if (cast.getCastType() instanceof JavaTypeName.Primitive) {
                node.setThisText("((" + classNameString + ")");
            } else {
                // This is a reference/array type cast, and we need to first upcast to java.lang.Object
                // so that the Java compiler does not complain about incompatible types in situations like this:
                // Suppose there are classes A, B, and C, where B and C are subclasses of A.
                // The cast is from A to B. The actual expression being cast has a static Java type of C.
                // While the JVM allows such a checkcast operation, the Java language does not, because
                // C is not compatible with B for a cast operation.
                node.setThisText("((" + classNameString + ")(" + context.getSourceName(JavaTypeName.OBJECT) + ")");
            }
            super.visitCastExpression(cast, arg);

            if (doOptionalParenthesis(cast.getExpressionToCast())) {
                node.getNthChild(0).prefixText("(");
                node.getNthChild(0).suffixText(")");
            }
            node.suffixText(")");

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitClassInstanceCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ClassInstanceCreationExpression, java.lang.Object)
         */
        @Override
        public Void visitClassInstanceCreationExpression(
                ClassInstanceCreationExpression instanceCreation,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_IN_ONE_LEVEL);
            stack.peek().addChild(node);
            stack.push(node);

            node.setThisText("new ");

            JavaTypeName className = instanceCreation.getClassName();

            if (instanceCreation.isArrayCreationExpression()) {
                // Creating an array.

                // Get the source for the array element type
                final JavaTypeName.Reference.Array arrayType = ((JavaTypeName.Reference.Array)className);
                final int nDims = arrayType.getNDimensions();
                final JavaTypeName arrayElementType = arrayType.getElementType();

                final String classNameString = context.getSourceName(arrayElementType);
                node.suffixText(classNameString);

                super.visitClassInstanceCreationExpression(instanceCreation, arg);

                final int nArgs = instanceCreation.getNArgs();

                // Get the source for the args whose dimensions are specified.
                for (int i = 0; i < nArgs; i++) {
                    node.getNthChild(i).prefixText("[");
                    node.getNthChild(i).suffixText("]");
                }

                //the renaming dimensions (needed to specify the array type correctly)
                for (int i = 0, nRemaining = nDims - nArgs; i < nRemaining; ++i) {
                    node.addChild(new ExpressionTextNode("[]",
                                       ExpressionTextNode.FormatType.ATOMIC));
                }


            } else {
                // Creating a non-array object.

                // Get the source for the object type.
                String classNameString = context.getSourceName(className);
                node.suffixText(classNameString + "(");

                // Get the source for the args.
                super.visitClassInstanceCreationExpression(instanceCreation, arg);

                for (int i = 0, nArgs = instanceCreation.getNArgs()-1; i < nArgs; i++) {
                    node.getNthChild(i).suffixText(", ");
                }
                node.suffixText(")");
            }

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceOfExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.InstanceOf, java.lang.Object)
         */
        @Override
        public Void visitInstanceOfExpression(
                InstanceOf instanceOf,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_IN_ONE_LEVEL);
            stack.peek().addChild(node);
            stack.push(node);

            super.visitInstanceOfExpression(instanceOf, arg);

            JavaTypeName referenceType = instanceOf.getReferenceType();

            // We need to first upcast to java.lang.Object
            // so that the Java compiler does not complain about incompatible types in situations like this:
            // Suppose there are classes A, B, and C, where B and C are subclasses of A.
            // The instanceof foreign function takes an argument of CAL type JA, whose Java implementation type is A.
            // The instanceof is a check for type B. The actual expression being checked has a static Java type of C.
            // While the JVM allows such an instanceof check, the Java language does not, because
            // C is not compatible with B as B is neither a subclass nor a superclass of C.
            node.setThisText("((" + context.getSourceName(JavaTypeName.OBJECT) + ")");
            if (doOptionalParenthesis(instanceOf.getJavaExpression())) {
                node.getNthChild(0).prefixText("(");
                node.getNthChild(0).suffixText(")");
            }
            node.suffixText(")");
            node.addChild(
                    new ExpressionTextNode(
                            " instanceof " + context.getSourceName(referenceType),
                            ExpressionTextNode.FormatType.ATOMIC));

            stack.pop ();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.Instance, java.lang.Object)
         */
        @Override
        public Void visitInstanceFieldExpression(
                Instance instanceField,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.ATOMIC);
            stack.peek().addChild(node);
            stack.push(node);

            super.visitInstanceFieldExpression(instanceField, arg);

            String fieldName = instanceField.getFieldName();
            if (node.getNChildren() > 0) {
                if (doOptionalParenthesis(instanceField.getInstance())) {
                    node.getNthChild(0).prefixText("(");
                    node.getNthChild(0).suffixText(")");
                }
                node.suffixText("." + fieldName);
            } else {
                node.setThisText(fieldName);
            }

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.Static, java.lang.Object)
         */
        @Override
        public Void visitStaticFieldExpression(
                Static staticField,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        context.getSourceName(staticField.getInvocationClass()) + "." + staticField.getFieldName(),
                        ExpressionTextNode.FormatType.ATOMIC);

            stack.peek().addChild(node);

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitThisFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.This, java.lang.Object)
         */
        @Override
        public Void visitThisFieldExpression(
                This thisField,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        "this",
                        ExpressionTextNode.FormatType.ATOMIC);
            stack.peek().addChild(node);

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLiteralWrapperExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LiteralWrapper, java.lang.Object)
         */
        @Override
        public Void visitLiteralWrapperExpression(
                LiteralWrapper literalWrapper,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        ExpressionTextNode.FormatType.ATOMIC);

            stack.peek().addChild(node);
            stack.push(node);

            Object literalObject = literalWrapper.getLiteralObject();
            if (literalObject instanceof Integer || literalObject instanceof Double || literalObject instanceof Boolean) {
                node.setThisText(literalObject.toString());

            } else if (literalObject instanceof Byte) {
                node.setThisText("(byte)" + literalObject.toString());

            } else if (literalObject instanceof Short) {
                node.setThisText("(short)" + literalObject.toString());

            } else if (literalObject instanceof Long) {
                node.setThisText(literalObject.toString() + "L");

            } else if (literalObject instanceof Float) {
                node.setThisText(literalObject.toString() + "F");

            } else if (literalObject instanceof String) {
                node.setThisText(StringEncoder.encodeString(literalObject.toString()));

            } else if (literalObject instanceof Character) {
                node.setThisText(StringEncoder.encodeChar(((Character)literalObject).charValue()));

            } else if (literalObject == null) {
                node.setThisText("null");
            }

            stack.pop();

            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visitClassLiteralExpression(ClassLiteral classLiteral, Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        context.getSourceName(classLiteral.getReferentType()) + ".class",
                        ExpressionTextNode.FormatType.ATOMIC);

            stack.peek().addChild(node);

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalNameExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LocalName, java.lang.Object)
         */
        @Override
        public Void visitLocalNameExpression(
                LocalName localName,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        localName.getName(),
                        ExpressionTextNode.FormatType.ATOMIC);
            stack.peek().addChild(node);

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalVariableExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LocalVariable, java.lang.Object)
         */
        @Override
        public Void visitLocalVariableExpression(
                LocalVariable localVariable,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        localVariable.getName(),
                        ExpressionTextNode.FormatType.ATOMIC);

            stack.peek().addChild(node);

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceMethodInvocationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodInvocation.Instance, java.lang.Object)
         */
        @Override
        public Void visitInstanceMethodInvocationExpression(
                MethodInvocation.Instance instanceInvocation,
                Void arg) {

            final ExpressionTextNode node;
            if (instanceInvocation.getInvocationTarget() == null) {
                node =
                    new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_IN_ONE_LEVEL);
            } else {
                node =
                    new ExpressionTextNode(ExpressionTextNode.FormatType.FIRST_CHILD_AT_LEVEL);
            }

            stack.peek().addChild(node);
            stack.push(node);


            super.visitInstanceMethodInvocationExpression(instanceInvocation, arg);

            int firstArg = 0;

            // The invocation target can be null, which means use "this" or "super" (depending on invocationType).
            // since "this" is optional in Java source in this situation, we just omit it.
            if (instanceInvocation.getInvocationTarget() == null) {
                if (instanceInvocation.getInvocationType() == MethodInvocation.InvocationType.SPECIAL) {
                    node.setThisText("super." + instanceInvocation.getMethodName() + "(");
                } else {
                    node.setThisText(instanceInvocation.getMethodName() + "(");
                }
            } else {
                if (doOptionalParenthesis(instanceInvocation.getInvocationTarget())) {
                    node.getNthChild(0).prefixText("(");
                    node.getNthChild(0).suffixText(")");
                }
                node.getNthChild(0).suffixText("." + instanceInvocation.getMethodName() + "(");
                firstArg++;
            }

            for(int i = firstArg, n = node.getNChildren() - 1; i < n; ++i) {
                node.getNthChild(i).suffixText(", ");
            }

            node.suffixText(")");

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticMethodInvocationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodInvocation.Static, java.lang.Object)
         */
        @Override
        public Void visitStaticMethodInvocationExpression(
                JavaExpression.MethodInvocation.Static staticInvocation,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(ExpressionTextNode.FormatType.CHILDREN_IN_ONE_LEVEL);
            stack.peek().addChild(node);
            stack.push(node);

            node.setThisText(context.getSourceName(staticInvocation.getInvocationClass()) + "." + staticInvocation.getMethodName() + "(");

            super.visitStaticMethodInvocationExpression(staticInvocation, arg);
            for (int i = 0, nArgs = staticInvocation.getNArgs() - 1; i < nArgs; i++) {
                node.getNthChild(i).suffixText(", ");
            }

            node.suffixText(")");

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitMethodVariableExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodVariable, java.lang.Object)
         */
        @Override
        public Void visitMethodVariableExpression(
                MethodVariable methodVariable,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        methodVariable.getName(),
                        ExpressionTextNode.FormatType.ATOMIC);

            stack.peek().addChild(node);

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitBinaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Binary, java.lang.Object)
         */
        @Override
        public Void visitBinaryOperatorExpression(
                Binary binaryOperator,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        ExpressionTextNode.FormatType.CHILDREN_AT_SAME_LEVEL);

            stack.peek().addChild(node);
            stack.push(node);

            super.visitBinaryOperatorExpression(binaryOperator, arg);

            if (doOptionalParenthesis(binaryOperator.getArgument(0))) {
                node.getNthChild(0).prefixText("(");
                node.getNthChild(0).suffixText(")");
            }
            if (doOptionalParenthesis(binaryOperator.getArgument(1))) {
                node.getNthChild(1).prefixText("(");
                node.getNthChild(1).suffixText(")");
            }
            node.getNthChild(0).suffixText(" " + binaryOperator.getJavaOperator().getSymbol() + " ");

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitTernaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Ternary, java.lang.Object)
         */
        @Override
        public Void visitTernaryOperatorExpression(
                Ternary ternaryOperator,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        ExpressionTextNode.FormatType.CHILDREN_AT_SAME_LEVEL);

            stack.peek().addChild(node);
            stack.push(node);

            super.visitTernaryOperatorExpression(ternaryOperator, arg);
            if (doOptionalParenthesis(ternaryOperator.getArgument(0))) {
                node.getNthChild(0).prefixText("(");
                node.getNthChild(0).suffixText(")");
            }
            if (doOptionalParenthesis(ternaryOperator.getArgument(1))) {
                node.getNthChild(1).prefixText("(");
                node.getNthChild(1).suffixText(")");
            }
            if (doOptionalParenthesis(ternaryOperator.getArgument(2))) {
                node.getNthChild(2).prefixText("(");
                node.getNthChild(2).suffixText(")");
            }

            node.getNthChild(0).suffixText(" ? " );
            node.getNthChild(1).suffixText(" : ");

            stack.pop();

            return null;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitUnaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Unary, java.lang.Object)
         */
        @Override
        public Void visitUnaryOperatorExpression(
                Unary unaryOperator,
                Void arg) {

            ExpressionTextNode node =
                new ExpressionTextNode(
                        ExpressionTextNode.FormatType.CHILDREN_AT_SAME_LEVEL);

            stack.peek().addChild(node);
            stack.push(node);

            super.visitUnaryOperatorExpression(unaryOperator, arg);
            if (doOptionalParenthesis(unaryOperator.getArgument(0))) {
                node.getNthChild(0).prefixText("(");
                node.getNthChild(0).suffixText(")");
            }

            node.prefixText(unaryOperator.getJavaOperator().getSymbol());

            stack.pop();

            return null;
        }

    }

    /**
     * An extension of JavaModelTraverser which traverses a JavaModel
     * construct and builds up the context of imported classes.
     *
     * @author rcypher
     *
     */
    private static class ImportGenerator extends JavaModelTraverser<Void, Void> {

        /** The context of imported classes. */
        GenerationContext context;

        /**
         * Create an ImportGenerator.
         * @param context - the context of imported classes.
         */
        ImportGenerator (GenerationContext context) {
            this.context = context;
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitArrayCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ArrayCreationExpression, java.lang.Object)
         */
        @Override
        public Void visitArrayCreationExpression(
                ArrayCreationExpression arrayCreation, Void arg) {
            context.getSourceName(arrayCreation.getArrayElementTypeName());
            return super.visitArrayCreationExpression(arrayCreation, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitCastExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.CastExpression, java.lang.Object)
         */
        @Override
        public Void visitCastExpression(
                CastExpression cast,
                Void arg) {
            context.getSourceName(cast.getCastType());
            return super.visitCastExpression(cast, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitClassInstanceCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ClassInstanceCreationExpression, java.lang.Object)
         */
        @Override
        public Void visitClassInstanceCreationExpression(
                ClassInstanceCreationExpression instanceCreation,
                Void arg) {

            JavaTypeName className = instanceCreation.getClassName();

            if (instanceCreation.isArrayCreationExpression()) {
                // Creating an array.

                // Get the source for the array element type
                final JavaTypeName.Reference.Array arrayType = ((JavaTypeName.Reference.Array)className);
                final JavaTypeName arrayElementType = arrayType.getElementType();
                context.getSourceName(arrayElementType);
            } else {
                // Creating a non-array object.

                // Get the source for the object type.
                context.getSourceName(className);
            }

            return super.visitClassInstanceCreationExpression(instanceCreation, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceOfExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.InstanceOf, java.lang.Object)
         */
        @Override
        public Void visitInstanceOfExpression(
                InstanceOf instanceOf,
                Void arg) {

            JavaTypeName referenceType = instanceOf.getReferenceType();
            context.getSourceName(referenceType);

            return super.visitInstanceOfExpression(instanceOf, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.Static, java.lang.Object)
         */
        @Override
        public Void visitStaticFieldExpression(
                Static staticField,
                Void arg) {

            context.getSourceName(staticField.getInvocationClass());
            return super.visitStaticFieldExpression(staticField, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticMethodInvocationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodInvocation.Static, java.lang.Object)
         */
        @Override
        public Void visitStaticMethodInvocationExpression(
                JavaExpression.MethodInvocation.Static staticInvocation,
                Void arg) {

            context.getSourceName(staticInvocation.getInvocationClass());

            return super.visitStaticMethodInvocationExpression(staticInvocation, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visitClassLiteralExpression(ClassLiteral classLiteral, Void arg) {

            context.getSourceName(classLiteral.getReferentType());
            return super.visitClassLiteralExpression(classLiteral, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaClassRep(org.openquark.cal.internal.runtime.lecc.JavaClassRep, java.lang.Object)
         */
        @Override
        public Void visitJavaClassRep(JavaClassRep classRep, Void arg) {
            JavaTypeName superclassName = classRep.getSuperclassName();
            if (!superclassName.equals(JavaTypeName.OBJECT)) {
                context.getSourceName(superclassName);
            }

            for (int i = 0, n = classRep.getNInterfaces(); i < n; i++) {
                context.getSourceName(classRep.getInterface(i));
            }

            return super.visitJavaClassRep(classRep, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalVariableDeclarationStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.LocalVariableDeclaration, java.lang.Object)
         */
        @Override
        public Void visitLocalVariableDeclarationStatement(
                LocalVariableDeclaration localVariableDeclaration, Void arg) {

            LocalVariable localVariable = localVariableDeclaration.getLocalVariable();
            context.getSourceName(localVariable.getTypeName());

            return super.visitLocalVariableDeclarationStatement(localVariableDeclaration, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaFieldDeclaration(org.openquark.cal.internal.runtime.lecc.JavaFieldDeclaration, java.lang.Object)
         */
        @Override
        public Void visitJavaFieldDeclaration(
                JavaFieldDeclaration fieldDeclaration, Void arg) {

            JavaTypeName fieldType = fieldDeclaration.getFieldType();
            context.getSourceName(fieldType);

            return super.visitJavaFieldDeclaration(fieldDeclaration, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaMethod(org.openquark.cal.internal.runtime.lecc.JavaMethod, java.lang.Object)
         */
        @Override
        public Void visitJavaMethod(JavaMethod javaMethod, Void arg) {
            // Return type
            context.getSourceName(javaMethod.getReturnType());

            // Parameters
            for (int i = 0, nParams = javaMethod.getNParams(); i < nParams; i++) {
                context.getSourceName(javaMethod.getParamType(i));
            }

            // throws
            for (int i = 0, n = javaMethod.getNThrownExceptions(); i < n; ++i) {
                JavaTypeName thrownExceptionName = javaMethod.getThrownException(i);
                context.getSourceName(thrownExceptionName);
            }

            return super.visitJavaMethod(javaMethod, arg);
        }

        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaConstructor(org.openquark.cal.internal.runtime.lecc.JavaConstructor, java.lang.Object)
         */
        @Override
        public Void visitJavaConstructor(JavaConstructor javaConstructor,
                Void arg) {
            for (int i = 0, n = javaConstructor.getNParams(); i < n; ++i) {
                context.getSourceName(javaConstructor.getParamType(i));
            }
            return super.visitJavaConstructor(javaConstructor, arg);
        }
    }
}

