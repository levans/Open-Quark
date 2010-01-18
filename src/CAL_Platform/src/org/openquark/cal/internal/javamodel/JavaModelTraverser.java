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
 * JavaModelTraverser.java
 * Creation date: April 24, 2006
 * By: Raymond Cypher
 */


package org.openquark.cal.internal.javamodel;

import java.util.List;

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
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
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
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement.DefaultCase;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement.IntCaseGroup;


/**
 * JavaModelTraverser is an implementation of the JavaModelVisitor which
 * performs a pre-order traversal of the java model elements it visits. 
 * <p>
 * 
 * This class is intended to be the base class of other visitors that need to
 * traverse a java model. For example, a visitor that needs to obtain a list
 * of all local variable declarations within a java model can be written as:
 * <p>
 * 
 * <pre><code>
 * class LocalVarDeclarationExtractor extends JavaModelTraverser&lt;Void, Void&gt; {
 * 
 *     private java.util.Set localVarDeclarations = new java.util.HashSet();
 * 
 *     public Void visitLocalVariableDeclarationStatement(
 *           JavaStatement.LocalVariableDeclaration localVariableDeclaration,
 *           Void arg) {
 *           
 *           localVarDeclarations.add(localVariableDeclaration);
 *           return super.visitLocalVariableDeclarationStatement(localVariableDeclaration, arg);
 *     }
 * }
 * </code></pre>
 * 
 * Note that the visitLocalVariableDeclarationStatement method contains a call to the
 * supertype's implementation (in this case JavaModelTraverser's). The purpose
 * of this call is to let the JavaModelTraverser's implementation continue
 * on the traversal through the subtree of elements rooted at the java element
 * being visited. When implementing a visitor for traversing java models, one
 * could omit the call to the supertype's implementation <i>only if </i> it
 * properly replaces the traversal logic contained in the supertype with its
 * own.
 * <p>
 * 
 * In JavaModelTraverser, the argument supplied to the visit methods are
 * ignored, and all methods return null as their return values. Subclasses of
 * JavaModelTraverser are free to use the argument and return value for their
 * own purposes, and the traversal logic in JavaModelTraverser will properly
 * propagate the supplied arguments down to child elements.
 * <p>
 * 
 * Nonetheless, for a significant portion of the common cases, the state of the
 * visitation can simply be kept as member variables within the visitor itself,
 * thereby eliminating the need to use the argument and return value of the
 * visit methods. In these scenarios, the recommended approach is to pass in
 * null as the argument, and return null as the return value.
 * <p>
 *
 * @author Raymond Cypher
 */
public class JavaModelTraverser<T, R> implements JavaModelVisitor<T, R> {
    
    /** 
     * Flag used to indicate whether traversal should continue.
     * derived classes can use this to halt traversal when a specific 
     * condition is met.  For example a derived class which was trying to 
     * determine if a java model contained an if-then-else expression
     * could halt traversal as soon as one was encountered.
     */
    private boolean continueTraversal = true;
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitArrayAccessExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ArrayAccess, java.lang.Object)
     */
    public R visitArrayAccessExpression(final ArrayAccess arrayAccess, final T arg) {
        if (continueTraversal) {
            arrayAccess.getArrayReference().accept(this, arg);
        }
        
        if (continueTraversal) {
            arrayAccess.getArrayIndex().accept(this, arg);
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public R visitArrayLengthExpression(final ArrayLength arrayLength, final T arg) {
        if (continueTraversal) {
            arrayLength.getArrayReference().accept(this, arg);
        }
               
        return null;
    }    

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitArrayCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ArrayCreationExpression, java.lang.Object)
     */
    public R visitArrayCreationExpression(
            final ArrayCreationExpression arrayCreation, final T arg) {
        
        for (int i = 0, n = arrayCreation.getNElementValues(); i < n && continueTraversal; ++i) {
            arrayCreation.getElementValue(i).accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitAssignmentExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.Assignment, java.lang.Object)
     */
    public R visitAssignmentExpression(final Assignment assignment, final T arg) {
        if (continueTraversal) {
            assignment.getLeftHandSide().accept(this, arg);
        }
        if (continueTraversal) {
            assignment.getValue().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitCastExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.CastExpression, java.lang.Object)
     */
    public R visitCastExpression(final CastExpression cast, final T arg) {
        if (continueTraversal) {
            cast.getExpressionToCast().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitClassInstanceCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ClassInstanceCreationExpression, java.lang.Object)
     */
    public R visitClassInstanceCreationExpression(
            final ClassInstanceCreationExpression instanceCreation, final T arg) {

        for (int i = 0, n = instanceCreation.getNArgs(); i < n && continueTraversal; ++i) {
            instanceCreation.getArg(i).accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceOfExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.InstanceOf, java.lang.Object)
     */
    public R visitInstanceOfExpression(final InstanceOf instanceOf, final T arg) {
        if (continueTraversal) {
            instanceOf.getJavaExpression().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.Instance, java.lang.Object)
     */
    public R visitInstanceFieldExpression(final Instance instanceField,
            final T arg) {

        if (instanceField.getInstance() != null && continueTraversal) {
            instanceField.getInstance().accept(this, arg);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.Static, java.lang.Object)
     */
    public R visitStaticFieldExpression(final Static staticField, final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitThisFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.This, java.lang.Object)
     */
    public R visitThisFieldExpression(final This thisField, final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLiteralWrapperExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LiteralWrapper, java.lang.Object)
     */
    public R visitLiteralWrapperExpression(final LiteralWrapper literalWrapper,
            final T arg) {
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public R visitClassLiteralExpression(final ClassLiteral classLiteral, final T arg) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalNameExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LocalName, java.lang.Object)
     */
    public R visitLocalNameExpression(final LocalName localName, final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalVariableExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LocalVariable, java.lang.Object)
     */
    public R visitLocalVariableExpression(final LocalVariable localVariable,
            final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceMethodInvocationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodInvocation.Instance, java.lang.Object)
     */
    public R visitInstanceMethodInvocationExpression(
            final org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation.Instance instanceInvocation,
            final T arg) {

        if (instanceInvocation.getInvocationTarget() != null && continueTraversal) {
            instanceInvocation.getInvocationTarget().accept(this, arg);
        }

        for (int i = 0, n = instanceInvocation.getNArgs(); i < n && continueTraversal; ++i) {
            instanceInvocation.getArg(i).accept(this, arg);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticMethodInvocationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodInvocation.Static, java.lang.Object)
     */
    public R visitStaticMethodInvocationExpression(
            final org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation.Static staticInvocation,
            final T arg) {

        for (int i = 0, n = staticInvocation.getNArgs(); i < n && continueTraversal; ++i) {
            staticInvocation.getArg(i).accept(this, arg);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitMethodVariableExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodVariable, java.lang.Object)
     */
    public R visitMethodVariableExpression(final MethodVariable methodVariable,
            final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitBinaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Binary, java.lang.Object)
     */
    public R visitBinaryOperatorExpression(final Binary binaryOperator,
            final T arg) {
        if (continueTraversal) {
            binaryOperator.getArgument(0).accept(this, arg);
        }
        if (continueTraversal) {
            binaryOperator.getArgument(1).accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitTernaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Ternary, java.lang.Object)
     */
    public R visitTernaryOperatorExpression(final Ternary ternaryOperator,
            final T arg) {

        if (continueTraversal) {
            ternaryOperator.getArgument(0).accept(this, arg);
        }
        if (continueTraversal) {
            ternaryOperator.getArgument(1).accept(this, arg);
        }
        if (continueTraversal) {
            ternaryOperator.getArgument(2).accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitUnaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Unary, java.lang.Object)
     */
    public R visitUnaryOperatorExpression(final Unary unaryOperator, final T arg) {
        if (continueTraversal) {
            unaryOperator.getArgument(0).accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitPlaceHolderExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.PlaceHolder, java.lang.Object)
     */
    public R visitPlaceHolderExpression(final PlaceHolder placeHolder, final T arg) {

        if (placeHolder.getActualExpression() != null && continueTraversal) {
            placeHolder.getActualExpression().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitAssertStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.AssertStatement, java.lang.Object)
     */
    public R visitAssertStatement(final AssertStatement assertStatement,
            final T arg) {

        if (continueTraversal) {
            assertStatement.getConditionExpr().accept(this, arg);
        }
        if (assertStatement.getOnFailureExpr() != null && continueTraversal) {
            assertStatement.getOnFailureExpr().accept(this, arg);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitBlockStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.Block, java.lang.Object)
     */
    public R visitBlockStatement(final Block block, final T arg) {
        
        final List<JavaExceptionHandler> oldExceptionHandlers = block.getExceptionHandlers();
        for (int i = 0, n = oldExceptionHandlers.size(); i < n && continueTraversal; ++i) {
            oldExceptionHandlers.get(i).accept(this, arg);
        }
        
        for (int i = 0, n = block.getNBlockStatements(); i < n && continueTraversal; ++i) {
            block.getNthBlockStatement(i).accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitExpressionStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.ExpressionStatement, java.lang.Object)
     */
    public R visitExpressionStatement(
            final ExpressionStatement expressionStatement, final T arg) {
        if (continueTraversal) {
            expressionStatement.getJavaExpression().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitIfThenElseStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.IfThenElseStatement, java.lang.Object)
     */
    public R visitIfThenElseStatement(final IfThenElseStatement ifThenElse,
            final T arg) {

        if (continueTraversal) {
            ifThenElse.getCondition().accept (this, arg);
        }
        if (continueTraversal) {
            ifThenElse.getThenStatement().accept(this, arg);
        }
        
        if (ifThenElse.getElseStatement() != null && continueTraversal) {
            ifThenElse.getElseStatement().accept(this, arg);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLabelledContinueStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.LabelledContinue, java.lang.Object)
     */
    public R visitLabelledContinueStatement(
            final LabelledContinue labelledContinue, final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLineCommentStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.LineComment, java.lang.Object)
     */
    public R visitLineCommentStatement(final LineComment lineComment, final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitMultiLineCommentStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.MultiLineComment, java.lang.Object)
     */
    public R visitMultiLineCommentStatement(final MultiLineComment lineComment, final T arg) {
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaDocCommentStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.JavaDocComment, java.lang.Object)
     */
    public R visitJavaDocCommentStatement(
            final JavaStatement.JavaDocComment comment, final T arg) {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalVariableDeclarationStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.LocalVariableDeclaration, java.lang.Object)
     */
    public R visitLocalVariableDeclarationStatement(
            final LocalVariableDeclaration localVariableDeclaration, final T arg) {

        if (continueTraversal) {
            localVariableDeclaration.getLocalVariable().accept(this, arg);
        }
        
        if (localVariableDeclaration.getInitializer() != null && continueTraversal) {
            localVariableDeclaration.getInitializer().accept(this, arg);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitReturnStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.ReturnStatement, java.lang.Object)
     */
    public R visitReturnStatement(final ReturnStatement returnStatement,
            final T arg) {
        if (returnStatement.getReturnExpression() != null && continueTraversal) {
            returnStatement.getReturnExpression().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitSwitchStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.SwitchStatement, java.lang.Object)
     */
    public R visitSwitchStatement(final SwitchStatement switchStatement,
            final T arg) {

        if (continueTraversal) {
            switchStatement.getCondition().accept(this, arg);
        }
        
        if (switchStatement.getDefaultStatement() != null && continueTraversal) {
            switchStatement.getDefaultStatement().accept(this, arg);
        }
        
        final List<IntCaseGroup> cases = switchStatement.getCaseGroups();
        for (int i = 0, n = cases.size(); i < n && continueTraversal; ++i) {
            cases.get(i).accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitDefaultCase(org.openquark.cal.internal.runtime.lecc.JavaStatement.SwitchStatement.DefaultCase, java.lang.Object)
     */
    public R visitDefaultCase(final DefaultCase defaultCase, final T arg) {
        if (continueTraversal) {
            defaultCase.getStatement().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitIntCaseGroup(org.openquark.cal.internal.runtime.lecc.JavaStatement.SwitchStatement.IntCaseGroup, java.lang.Object)
     */
    public R visitIntCaseGroup(final IntCaseGroup intCaseGroup, final T arg) {
        if (continueTraversal) {
            intCaseGroup.getStatement().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitSynchronizedMethodInvocationStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.SynchronizedMethodInvocation, java.lang.Object)
     */
    public R visitSynchronizedMethodInvocationStatement(
            final SynchronizedMethodInvocation synchronizedInvocation, final T arg) {

        if (continueTraversal) {
            synchronizedInvocation.getMethodInvocation().accept(this, arg);
        }
        if (continueTraversal) {
            synchronizedInvocation.getSynchronizingObject().accept(this, arg);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitThrowStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.ThrowStatement, java.lang.Object)
     */
    public R visitThrowStatement(final ThrowStatement throwStatement, final T arg) {
        if (continueTraversal) {
            throwStatement.getThrownExpression().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitUnconditonalLoop(org.openquark.cal.internal.runtime.lecc.JavaStatement.UnconditionalLoop, java.lang.Object)
     */
    public R visitUnconditonalLoop(final UnconditionalLoop unconditionalLoop,
            final T arg) {
        if (continueTraversal) {
            unconditionalLoop.getBody().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaExceptionHandler(org.openquark.cal.internal.runtime.lecc.JavaExceptionHandler, java.lang.Object)
     */
    public R visitJavaExceptionHandler(
            final JavaExceptionHandler exceptionHandler, final T arg) {
        if (continueTraversal) {
            exceptionHandler.getHandlerCode().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaClassRep(org.openquark.cal.internal.runtime.lecc.JavaClassRep, java.lang.Object)
     */
    public R visitJavaClassRep(final JavaClassRep classRep, final T arg) {

        for (int i = 0, n = classRep.getNFieldDeclarations(); i < n && continueTraversal; ++i) {
            final JavaFieldDeclaration fieldDeclaration = classRep.getFieldDeclaration(i);
            fieldDeclaration.accept(this, arg);
        }
        
        for (int i = 0, n = classRep.getNConstructors(); i < n && continueTraversal; ++i) {
            final JavaConstructor javaConstructor = classRep.getConstructor(i);
            javaConstructor.accept(this, arg);
        }
        
        for (int i = 0, n = classRep.getNMethods(); i < n && continueTraversal; ++i) {
            final JavaMethod method = classRep.getMethod(i);
            method.accept(this, arg);
        }
        
        for (int i = 0, n = classRep.getNInnerClasses(); i < n && continueTraversal; ++i) {
            final JavaClassRep innerClass = classRep.getInnerClass(i);
            innerClass.accept(this, arg);
        }
        
        final JavaDocComment jdc = classRep.getJavaDoc();
        if (jdc != null && continueTraversal) {
            jdc.accept(this, arg);
        }
        
        final MultiLineComment mlc = classRep.getComment();
        if (mlc != null && continueTraversal) {
            mlc.accept(this, arg);
        }
        
        return null;
    }

    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaFieldDeclaration(org.openquark.cal.internal.runtime.lecc.JavaFieldDeclaration, java.lang.Object)
     */
    public R visitJavaFieldDeclaration(
            final JavaFieldDeclaration fieldDeclaration, final T arg) {
        
        if (fieldDeclaration.getInitializer() != null && continueTraversal) {
            fieldDeclaration.getInitializer().accept(this, arg);
        }
        
        if (fieldDeclaration.getJavaDoc() != null && continueTraversal) {
            fieldDeclaration.getJavaDoc().accept(this, arg);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaConstructor(org.openquark.cal.internal.runtime.lecc.JavaConstructor, java.lang.Object)
     */
    public R visitJavaConstructor(final JavaConstructor javaConstructor,
            final T arg) {
        if (continueTraversal) {
            javaConstructor.getBodyCode().accept(this, arg);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaMethod(org.openquark.cal.internal.runtime.lecc.JavaMethod, java.lang.Object)
     */
    public R visitJavaMethod(final JavaMethod javaMethod, final T arg) {
        if (continueTraversal) {
            if (javaMethod.getJavaDocComment() != null) {
                javaMethod.getJavaDocComment().accept(this, arg);
            }
            javaMethod.getBodyCode().accept(this, arg);
        }
        return null;
    }

    /**
     * @return the state of the continueTraversal flag.
     */
    protected final boolean getContinueTraversal () {
        return continueTraversal;
    }
    
    /**
     * @param b the new value for the continueTraversal flag.
     */
    protected final void setContinueTraversal (final boolean b) {
        continueTraversal = b;
    }
}
