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
 * JavaModelVisitor.java
 * Creation date: Mar. 29, 2006
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.javamodel;


/**
 * A visitor interface for visiting java model elements.
 * <p>
 * 
 * The visitor mechanism supported by this interface is more general than the
 * regular visitor pattern. In particular, each visit method boasts an argument
 * of the generic type java.lang.Object, and also a return type of
 * java.lang.Object. This allows additional arguments, suitably encapsulated in
 * an object, to be passed in to any visit method. The visit methods are also
 * able to return values, which is useful for cases where the visitor needs to
 * aggregate results in a hierarchical fashion from visiting the tree of java
 * model elements.
 * <p>
 * 
 * Nonetheless, for a significant portion of the common cases, the state of the
 * visitation can simply be kept as member variables within the visitor itself,
 * thereby eliminating the need to use the argument and return value of the
 * visit methods. In these scenarios, the recommended approach is to use
 * {@link Void} as the type argument for both <code>T</code> and <code>R</code>, and
 * pass in null as the argument, and return null as the return value.
 * <p>
 * 
 * While it is certainly possible to directly implement the JavaModelVisitor
 * interface, it may be easier to subclass from one of the predefined visitor
 * classes. 
 * To construct a java model to java model
 * transformation, the <code>JavaModelCopier</code> is a convenient base
 * class to extend since it provides the default behaviour of performing a deep
 * copy of the java model, while allowing subclasses to hook in and return
 * transformations of java model elements where required.
 * <p>
 * 
 * If the java model structure is changed (e.g. when new java model element
 * classes are added, or when existing element classes are moved around in the
 * inheritance and/or containment hierarchy), then this visitor interface and
 * all implementors must be updated. This may include renaming existing
 * interface methods if element classes have been moved.
 * <p>
 * 
 * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
 * @param <R> the return type. If the return value is not used, specify {@link Void}.
 * 
 * @see org.openquark.cal.internal.javamodel.JavaModelTraverser
 * @see org.openquark.cal.internal.javamodel.JavaModelCopier
 * 
 * @author Raymond Cypher
 */
public interface JavaModelVisitor<T, R> {

    /**
     * @param arrayAccess
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitArrayAccessExpression(
            JavaExpression.ArrayAccess arrayAccess, T arg);
    
    
   /**
     * @param arrayLength
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitArrayLengthExpression(
            JavaExpression.ArrayLength arrayLength, T arg);    

    /**
     * @param arrayCreation
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitArrayCreationExpression(
            JavaExpression.ArrayCreationExpression arrayCreation, T arg);

    /**
     * @param assignment
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitAssignmentExpression(
            JavaExpression.Assignment assignment, T arg);

    /**
     * @param cast
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitCastExpression(JavaExpression.CastExpression cast,
            T arg);

    /**
     * @param instanceCreation
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitClassInstanceCreationExpression(
            JavaExpression.ClassInstanceCreationExpression instanceCreation,
            T arg);

    /**
     * @param instanceOf
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitInstanceOfExpression(
            JavaExpression.InstanceOf instanceOf, T arg);

    /**
     * @param classLiteral
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitClassLiteralExpression(
        JavaExpression.ClassLiteral classLiteral, T arg);

    /**
     * @param instanceField
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitInstanceFieldExpression(
            JavaExpression.JavaField.Instance instanceField, T arg);

    /**
     * @param staticField
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitStaticFieldExpression(
            JavaExpression.JavaField.Static staticField, T arg);

    /**
     * @param thisField
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitThisFieldExpression(
            JavaExpression.JavaField.This thisField, T arg);

    /**
     * @param literalWrapper
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitLiteralWrapperExpression(
            JavaExpression.LiteralWrapper literalWrapper, T arg);

    /**
     * @param localName
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitLocalNameExpression(JavaExpression.LocalName localName,
            T arg);

    /**
     * @param localVariable
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitLocalVariableExpression(
            JavaExpression.LocalVariable localVariable, T arg);

    /**
     * @param instanceInvocation
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitInstanceMethodInvocationExpression(
            JavaExpression.MethodInvocation.Instance instanceInvocation,
            T arg);

    /**
     * @param staticInvocation
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitStaticMethodInvocationExpression(
            JavaExpression.MethodInvocation.Static staticInvocation, T arg);

    /**
     * @param methodVariable
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitMethodVariableExpression(
            JavaExpression.MethodVariable methodVariable, T arg);

    /**
     * @param binaryOperator
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitBinaryOperatorExpression(
            JavaExpression.OperatorExpression.Binary binaryOperator, T arg);

    /**
     * @param ternaryOperator
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitTernaryOperatorExpression(
            JavaExpression.OperatorExpression.Ternary ternaryOperator,
            T arg);

    /**
     * @param unaryOperator
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitUnaryOperatorExpression(
            JavaExpression.OperatorExpression.Unary unaryOperator, T arg);

    /**
     * @param placeHolder
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitPlaceHolderExpression(
            JavaExpression.PlaceHolder placeHolder, T arg);

    /**
     * @param assertStatement
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitAssertStatement(
            JavaStatement.AssertStatement assertStatement, T arg);

    /**
     * @param block
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitBlockStatement(JavaStatement.Block block, T arg);

    /**
     * @param expressionStatement
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitExpressionStatement(
            JavaStatement.ExpressionStatement expressionStatement, T arg);

    /**
     * @param ifThenElse
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitIfThenElseStatement(
            JavaStatement.IfThenElseStatement ifThenElse, T arg);

    /**
     * @param labelledContinue
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitLabelledContinueStatement(
            JavaStatement.LabelledContinue labelledContinue, T arg);

    /**
     * @param lineComment
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitLineCommentStatement(
            JavaStatement.LineComment lineComment, T arg);

    /**
     * @param multiLineComment
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitMultiLineCommentStatement(
            JavaStatement.MultiLineComment multiLineComment, T arg);

    /**
     * @param comment
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitJavaDocCommentStatement(
            JavaStatement.JavaDocComment comment, T arg);
    
    /**
     * @param localVariableDeclaration
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitLocalVariableDeclarationStatement(
            JavaStatement.LocalVariableDeclaration localVariableDeclaration,
            T arg);

    /**
     * @param returnStatement
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitReturnStatement(
            JavaStatement.ReturnStatement returnStatement, T arg);

    /**
     * @param switchStatement
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitSwitchStatement(
            JavaStatement.SwitchStatement switchStatement, T arg);

    /**
     * @param defaultCase
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitDefaultCase(
            JavaStatement.SwitchStatement.DefaultCase defaultCase, T arg);

    /**
     * @param intCaseGroup
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitIntCaseGroup(
            JavaStatement.SwitchStatement.IntCaseGroup intCaseGroup, T arg);

    /**
     * @param synchronizedInvocation
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitSynchronizedMethodInvocationStatement(
            JavaStatement.SynchronizedMethodInvocation synchronizedInvocation,
            T arg);

    /**
     * @param throwStatement
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitThrowStatement(
            JavaStatement.ThrowStatement throwStatement, T arg);

    /**
     * @param unconditionalLoop
     *            the java model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitUnconditonalLoop(
            JavaStatement.UnconditionalLoop unconditionalLoop, T arg);

    /**
     * @param exceptionHandler 
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitJavaExceptionHandler(
            JavaExceptionHandler exceptionHandler, T arg);

    /**
     * @param classRep
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitJavaClassRep (
            JavaClassRep classRep, T arg);

    /**
     * @param fieldDeclaration
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitJavaFieldDeclaration (
            JavaFieldDeclaration fieldDeclaration, T arg);
    
    /**
     * @param javaConstructor
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitJavaConstructor (
            JavaConstructor javaConstructor, T arg);
    
    /**
     * @param javaMethod
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public R visitJavaMethod (
            JavaMethod javaMethod, T arg);
}
