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
 * JavaModelCopier.java
 * Creation date: Mar 29, 2006
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.javamodel;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.internal.javamodel.JavaExpression.ArrayAccess;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayLength;
import org.openquark.cal.internal.javamodel.JavaExpression.Assignment;
import org.openquark.cal.internal.javamodel.JavaExpression.CastExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassInstanceCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassLiteral;
import org.openquark.cal.internal.javamodel.JavaExpression.InstanceOf;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalName;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.Nameable;
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
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement.SwitchCase;


/**
 * JavaModelCopier is an implementation of the JavaModelVisitor which
 * performs a deep copy of the java model elements. Each visit method
 * returns as its return value a deep copy of the element it visits.
 * <p>
 * 
 * This class is intended to be the base class of other visitors that need to
 * perform java model transformations, i.e. taking a java model as input,
 * generating a new java model as output. For example, a visitor that needs
 * to change all invocations of "Math.sin" to "Math.cos" can be written
 * as:
 * <p>
 * 
 * <pre><code>
 * class ReferenceRenamer extends JavaModelCopier&lt;Void&gt; {
 * 
 *     public JavaExpression visitStaticMethodInvocationExpression(
 *         JavaExpression.MethodInvocation.Static staticInvocation, Void arg) {
 * 
 *         if (staticInvocation.getMethodName().equals("Math.sin")) {
 * 
 *             // Create a new MethodInvocation instance with appropriate changes.
 * 
 *         } else {
 *             return super.visitStaticMethodInvocationExpression(staticInvocation, arg);
 *         }
 *     }
 * }
 * </code></pre>
 *
 * To use this transformation, one can simply apply it to a java model's
 * root element:
 * <pre><code>
 * JavaStatement transformedElement =
 *     (JavaStatement)originalElement.accept(new ReferenceRenamer(), null);
 * </code></pre>
 *
 * Each visit method in every subclass of JavaModelCopier is
 * generally obligated to return an object of the same type as the element being
 * visited. An exception to this rule is that it is usually acceptable that
 * visiting an element in the JavaExpression hierarchy returns an element
 * representing a different kind of expression.
 * <p>
 * 
 * Note that the visitStaticMethodInvocationExpression method above 
 * defaults to a call to the supertype's implementation. This 
 * ensures that in the case when no transformations are
 * required, a simple deep copy of the java model element is returned. In
 * general, in any situations where the supertype's implementation is not
 * invoked, it is the responsibility of the subclass' implementation to 1)
 * traverse through any child elements if necessary, and 2) provide the new
 * java model element resulting from the transformation of the element being
 * visited.
 * <p>
 * 
 * In JavaModelCopier, the argument supplied to the visit methods are ignored.
 * Subclasses of JavaModelCopier are free to use the argument for their own
 * purposes, and the traversal logic in JavaModelCopier will properly
 * propagate the supplied arguments down to child elements.
 * <p>
 * 
 * Note: the design of this class mandates that all fields whose type descend
 * from JavaStatement or JavaExpression must be visited by the visitor. In particular,
 * for array-valued fields, each array element must be individually visited and
 * consequently copied.
 * 
 * This usage guideline applies to any modifications to this class, as well
 * as any subclass that aims to preserve the deep-copying semantics.
 * <p>
 * 
 * @author Raymond Cypher
 */

public class JavaModelCopier<T> implements JavaModelVisitor<T, Object> {

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitArrayAccessExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ArrayAccess, java.lang.Object)
     */
    public JavaExpression visitArrayAccessExpression(ArrayAccess arrayAccess, T arg) {
        return new JavaExpression.ArrayAccess(
                (JavaExpression)arrayAccess.getArrayReference().accept(this, arg),
                (JavaExpression)arrayAccess.getArrayIndex().accept(this, arg));
    }
    
    /** {@inheritDoc} */
    public JavaExpression visitArrayLengthExpression(ArrayLength arrayLength, T arg) {
        return new JavaExpression.ArrayLength(
                (JavaExpression)arrayLength.getArrayReference().accept(this, arg));
    }    

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitArrayCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ArrayCreationExpression, java.lang.Object)
     */
    public JavaExpression visitArrayCreationExpression(
            ArrayCreationExpression arrayCreation, T arg) {
        JavaExpression[] elementValues = new JavaExpression[arrayCreation.getNElementValues()];
        for (int i = 0; i < elementValues.length; ++i) {
            elementValues[i] = (JavaExpression)arrayCreation.getElementValue(i).accept(this, arg);
        }
        
        return new ArrayCreationExpression(arrayCreation.getArrayElementTypeName(), elementValues);
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitAssignmentExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.Assignment, java.lang.Object)
     */
    public JavaExpression visitAssignmentExpression(Assignment assignment, T arg) {
        return new Assignment (
                (Nameable)assignment.getLeftHandSide().accept(this, arg),
                (JavaExpression)assignment.getValue().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitCastExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.CastExpression, java.lang.Object)
     */
    public JavaExpression visitCastExpression(CastExpression cast, T arg) {
        return new CastExpression (
                cast.getCastType(),
                (JavaExpression)cast.getExpressionToCast().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitClassInstanceCreationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.ClassInstanceCreationExpression, java.lang.Object)
     */
    public JavaExpression visitClassInstanceCreationExpression(
            ClassInstanceCreationExpression instanceCreation, T arg) {
        JavaTypeName[] argTypes = new JavaTypeName[instanceCreation.getNArgs()];
        JavaExpression[] argValues = new JavaExpression[instanceCreation.getNArgs()];
        for (int i = 0, n = argTypes.length; i < n; ++i) {
            argTypes[i] = instanceCreation.getParamType(i);
            argValues[i] = (JavaExpression)instanceCreation.getArg(i).accept(this, arg);
        }
        
        return new ClassInstanceCreationExpression(instanceCreation.getClassName(), argValues, argTypes);
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceOfExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.InstanceOf, java.lang.Object)
     */
    public JavaExpression visitInstanceOfExpression(InstanceOf instanceOf, T arg) {
        return new InstanceOf (
                (JavaExpression)instanceOf.getJavaExpression().accept(this, arg), 
                instanceOf.getReferenceType());
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.Instance, java.lang.Object)
     */
    public JavaExpression visitInstanceFieldExpression(Instance instanceField,
            T arg) {
        if (instanceField.getInstance() != null) {
            return new Instance (
                    (JavaExpression)instanceField.getInstance().accept(this, arg),
                    instanceField.getFieldName(),
                    instanceField.getFieldType());
        } else {
            return new Instance (
                    null,
                    instanceField.getFieldName(),
                    instanceField.getFieldType());
            
        }
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.Static, java.lang.Object)
     */
    public JavaExpression visitStaticFieldExpression(Static staticField, T arg) {
        return new Static (
                staticField.getInvocationClass(),
                staticField.getFieldName(),
                staticField.getFieldType());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitThisFieldExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.JavaField.This, java.lang.Object)
     */
    public JavaExpression visitThisFieldExpression(This thisField, T arg) {
        return new This(thisField.getFieldType());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLiteralWrapperExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LiteralWrapper, java.lang.Object)
     */
    public JavaExpression visitLiteralWrapperExpression(LiteralWrapper literalWrapper,
            T arg) {
        return LiteralWrapper.make(literalWrapper.getLiteralObject());
    }

    /**
     * {@inheritDoc}
     */
    public JavaExpression visitClassLiteralExpression(ClassLiteral classLiteral, T arg) {
        return new ClassLiteral(classLiteral.getReferentType());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalNameExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LocalName, java.lang.Object)
     */
    public JavaExpression visitLocalNameExpression(LocalName localName, T arg) {
        return new LocalName(localName.getName(), localName.getTypeName());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalVariableExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.LocalVariable, java.lang.Object)
     */
    public JavaExpression visitLocalVariableExpression(LocalVariable localVariable,
            T arg) {
        return new LocalVariable(localVariable.getName(), localVariable.getTypeName());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitInstanceMethodInvocationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodInvocation.Instance, java.lang.Object)
     */
    public JavaExpression visitInstanceMethodInvocationExpression(
            MethodInvocation.Instance instanceInvocation, T arg) {
        JavaTypeName[] argTypes = new JavaTypeName[instanceInvocation.getNArgs()];
        JavaExpression[] argValues = new JavaExpression[instanceInvocation.getNArgs()];
        for (int i = 0, n = argTypes.length; i < n; ++i) {
            argTypes[i] = instanceInvocation.getParamType(i);
            argValues[i] = (JavaExpression)instanceInvocation.getArg(i).accept(this, arg);
        }
        JavaExpression target = null;
        if (instanceInvocation.getInvocationTarget() != null) {
            target = (JavaExpression)instanceInvocation.getInvocationTarget().accept(this, arg);
        }
        return new MethodInvocation.Instance(
                target,
                instanceInvocation.getMethodName(),
                instanceInvocation.getDeclaringClass(),
                argValues,
                argTypes,
                instanceInvocation.getReturnType(),
                instanceInvocation.getInvocationType());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitStaticMethodInvocationExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodInvocation.Static, java.lang.Object)
     */
    public JavaExpression visitStaticMethodInvocationExpression(
            MethodInvocation.Static staticInvocation, T arg) {

        JavaTypeName[] argTypes = new JavaTypeName[staticInvocation.getNArgs()];
        JavaExpression[] argValues = new JavaExpression[staticInvocation.getNArgs()];
        for (int i = 0, n = argTypes.length; i < n; ++i) {
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
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitMethodVariableExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.MethodVariable, java.lang.Object)
     */
    public JavaExpression visitMethodVariableExpression(MethodVariable methodVariable,
            T arg) {
        return new MethodVariable(methodVariable.getName());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitBinaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Binary, java.lang.Object)
     */
    public JavaExpression visitBinaryOperatorExpression(Binary binaryOperator,
            T arg) {
        
        return new Binary (
                binaryOperator.getJavaOperator(),
                (JavaExpression)binaryOperator.getArgument(0).accept(this, arg),
                (JavaExpression)binaryOperator.getArgument(1).accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitTernaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Ternary, java.lang.Object)
     */
    public JavaExpression visitTernaryOperatorExpression(Ternary ternaryOperator,
            T arg) {
        return new Ternary (
                (JavaExpression)ternaryOperator.getArgument(0).accept(this, arg),
                (JavaExpression)ternaryOperator.getArgument(1).accept(this, arg),
                (JavaExpression)ternaryOperator.getArgument(2).accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitUnaryOperatorExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.Unary, java.lang.Object)
     */
    public JavaExpression visitUnaryOperatorExpression(Unary unaryOperator, T arg) {
        return new Unary (
                unaryOperator.getJavaOperator(),
                (JavaExpression)unaryOperator.getArgument(0).accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitPlaceHolderExpression(org.openquark.cal.internal.runtime.lecc.JavaExpression.PlaceHolder, java.lang.Object)
     */
    public JavaExpression visitPlaceHolderExpression(PlaceHolder placeHolder, T arg) {
        PlaceHolder phl = new PlaceHolder(placeHolder.name);
        if (placeHolder.getActualExpression() != null) {
            phl.setActualExpression((JavaExpression)placeHolder.getActualExpression().accept(this, arg));
        }
        return phl;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitAssertStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.AssertStatement, java.lang.Object)
     */
    public JavaStatement visitAssertStatement(AssertStatement assertStatement,
            T arg) {
        JavaExpression condition = 
            (JavaExpression)assertStatement.getConditionExpr().accept(this, arg);
        if (assertStatement.getOnFailureExpr() != null) {
            JavaExpression failureExpr = (JavaExpression)assertStatement.getOnFailureExpr().accept(this, arg);
            return new AssertStatement (
                    condition,
                    failureExpr,
                    assertStatement.getOnFailureExprType());
        }
        return new AssertStatement (condition);
        
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitBlockStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.Block, java.lang.Object)
     */
    public JavaStatement visitBlockStatement(Block block, T arg) {
        List<JavaExceptionHandler> newExceptionHandlers = new ArrayList<JavaExceptionHandler>();
        List<JavaExceptionHandler> oldExceptionHandlers = block.getExceptionHandlers();
        for (int i = 0, n = oldExceptionHandlers.size(); i < n; ++i) {
            newExceptionHandlers.add((JavaExceptionHandler)(oldExceptionHandlers.get(i)).accept(this, arg));
        }
        
        Block newBlock = new Block(newExceptionHandlers);
        
        for (int i = 0, n = block.getNBlockStatements(); i < n; ++i) {
            newBlock.addStatement(
                    (JavaStatement)block.getNthBlockStatement(i).accept(this, arg));
        }
        
        return newBlock;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitExpressionStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.ExpressionStatement, java.lang.Object)
     */
    public JavaStatement visitExpressionStatement(
            ExpressionStatement expressionStatement, T arg) {
        return new ExpressionStatement (
                (JavaExpression)expressionStatement.getJavaExpression().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitIfThenElseStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.IfThenElseStatement, java.lang.Object)
     */
    public JavaStatement visitIfThenElseStatement(IfThenElseStatement ifThenElse,
            T arg) {
        JavaExpression condition = 
            (JavaExpression)ifThenElse.getCondition().accept (this, arg);
        JavaStatement thenPart =
            (JavaStatement)ifThenElse.getThenStatement().accept(this, arg);
        
        if (ifThenElse.getElseStatement() != null) {
            JavaStatement elsePart = 
                (JavaStatement)ifThenElse.getElseStatement().accept(this, arg);
            
            return new IfThenElseStatement(condition, thenPart, elsePart);
        }
        
        return new IfThenElseStatement(condition, thenPart);
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLabelledContinueStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.LabelledContinue, java.lang.Object)
     */
    public JavaStatement visitLabelledContinueStatement(
            LabelledContinue labelledContinue, T arg) {
        return new LabelledContinue (labelledContinue.getLabel());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLineCommentStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.LineComment, java.lang.Object)
     */
    public JavaStatement visitLineCommentStatement(LineComment lineComment, T arg) {
        return new LineComment (lineComment.getCommentText());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLineCommentStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.MultiLineComment, java.lang.Object)
     */
    public JavaStatement visitMultiLineCommentStatement(MultiLineComment lineComment, T arg) {
        return new MultiLineComment (lineComment);
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaDocCommentStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.JavaDocComment, java.lang.Object)
     */
    public JavaStatement visitJavaDocCommentStatement(
            JavaStatement.JavaDocComment comment, T arg) {
        return new JavaStatement.JavaDocComment(comment);
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitLocalVariableDeclarationStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.LocalVariableDeclaration, java.lang.Object)
     */
    public JavaStatement visitLocalVariableDeclarationStatement(
            LocalVariableDeclaration localVariableDeclaration, T arg) {
        
        if (localVariableDeclaration.getInitializer() != null) {
            LocalVariableDeclaration newDeclaration =
                new LocalVariableDeclaration (
                    (LocalVariable)localVariableDeclaration.getLocalVariable().accept(this, arg),
                    (JavaExpression)localVariableDeclaration.getInitializer().accept(this, arg),
                    localVariableDeclaration.isFinal());
           
            return newDeclaration;
            
        } else {
            LocalVariableDeclaration newDeclaration =
                new LocalVariableDeclaration (
                    (LocalVariable)localVariableDeclaration.getLocalVariable().accept(this, arg),
                    null,
                    localVariableDeclaration.isFinal());           
            
            return newDeclaration;
        }
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitReturnStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.ReturnStatement, java.lang.Object)
     */
    public JavaStatement visitReturnStatement(ReturnStatement returnStatement,
            T arg) {
        
        if (returnStatement.getReturnExpression() != null) {
            return new ReturnStatement (
                    (JavaExpression)returnStatement.getReturnExpression().accept(this, arg));
        } else {
            return new ReturnStatement();
        }
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitSwitchStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.SwitchStatement, java.lang.Object)
     */
    public JavaStatement visitSwitchStatement(SwitchStatement switchStatement,
            T arg) {
        SwitchStatement newSwitch = new SwitchStatement (
                (JavaExpression)switchStatement.getCondition().accept(this, arg));
        
        if (switchStatement.getDefaultStatement() != null) {
            newSwitch.addCase (new SwitchStatement.DefaultCase (
                    (JavaStatement)switchStatement.getDefaultStatement().accept(this, arg)));
        }
        
        List<IntCaseGroup> cases = switchStatement.getCaseGroups();
        for (int i = 0, n = cases.size(); i < n; ++i) {
            newSwitch.addCase(
                    (SwitchStatement.SwitchCase)cases.get(i).accept(this, arg));
        }
        
        return newSwitch;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitDefaultCase(org.openquark.cal.internal.runtime.lecc.JavaStatement.SwitchStatement.DefaultCase, java.lang.Object)
     */
    public SwitchCase visitDefaultCase(DefaultCase defaultCase, T arg) {
        return new DefaultCase (
                (JavaStatement)defaultCase.getStatement().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitIntCaseGroup(org.openquark.cal.internal.runtime.lecc.JavaStatement.SwitchStatement.IntCaseGroup, java.lang.Object)
     */
    public SwitchCase visitIntCaseGroup(IntCaseGroup intCaseGroup, T arg) {
        return new IntCaseGroup (
                intCaseGroup.getCaseLabels(),
                (JavaStatement)intCaseGroup.getStatement().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitSynchronizedMethodInvocationStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.SynchronizedMethodInvocation, java.lang.Object)
     */
    public JavaStatement visitSynchronizedMethodInvocationStatement(
            SynchronizedMethodInvocation synchronizedInvocation, T arg) {
        return new SynchronizedMethodInvocation (
                (MethodInvocation)synchronizedInvocation.getMethodInvocation().accept(this, arg),
                (JavaField)synchronizedInvocation.getSynchronizingObject().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitThrowStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.ThrowStatement, java.lang.Object)
     */
    public JavaStatement visitThrowStatement(ThrowStatement throwStatement, T arg) {
        return new ThrowStatement (
                (JavaExpression)throwStatement.getThrownExpression().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitUnconditonalLoop(org.openquark.cal.internal.runtime.lecc.JavaStatement.UnconditionalLoop, java.lang.Object)
     */
    public JavaStatement visitUnconditonalLoop(UnconditionalLoop unconditionalLoop,
            T arg) {
        return new UnconditionalLoop (
                unconditionalLoop.getLabel(),
                (JavaStatement)unconditionalLoop.getBody().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaExceptionHandler(org.openquark.cal.internal.runtime.lecc.JavaStatement.UnconditionalLoop, java.lang.Object)
     */
    public JavaExceptionHandler visitJavaExceptionHandler (JavaExceptionHandler exceptionHandler, T arg) {
        return new JavaExceptionHandler (
                exceptionHandler.getExceptionClass(),
                exceptionHandler.getExceptionVarName(),
                (JavaStatement)exceptionHandler.getHandlerCode().accept(this, arg));
    }

    /**
     * @param classRep
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public JavaClassRep visitJavaClassRep (
            JavaClassRep classRep, T arg) {
        
        JavaTypeName[] interfaces = new JavaTypeName [classRep.getNInterfaces()];
        for (int i = 0, n = interfaces.length; i < n; ++i) {
            interfaces[i] = classRep.getInterface(i);
        }
        JavaClassRep newClassRep = 
            new JavaClassRep (classRep.getClassName(),
                              classRep.getSuperclassName(),
                              classRep.getModifiers(),
                              interfaces);
        
        for (int i = 0, n = classRep.getNFieldDeclarations(); i < n; ++i) {
            JavaFieldDeclaration fieldDeclaration = classRep.getFieldDeclaration(i);
            JavaFieldDeclaration newFieldDeclaration = (JavaFieldDeclaration)fieldDeclaration.accept(this, arg);
            newClassRep.addFieldDeclaration(newFieldDeclaration);
        }
        
        for (int i = 0, n = classRep.getNConstructors(); i < n; ++i) {
            JavaConstructor javaConstructor = classRep.getConstructor(i);
            JavaConstructor newConstructor = (JavaConstructor)javaConstructor.accept(this, arg);
            newClassRep.addConstructor(newConstructor);
        }
        
        for (int i = 0, n = classRep.getNMethods(); i < n; ++i) {
            JavaMethod method = classRep.getMethod(i);
            JavaMethod newMethod = (JavaMethod)method.accept(this, arg);
            newClassRep.addMethod(newMethod);
        }
        
        for (int i = 0, n = classRep.getNInnerClasses(); i < n; ++i) {
            JavaClassRep innerClass = classRep.getInnerClass(i);
            JavaClassRep newInnerClass = (JavaClassRep)innerClass.accept(this, arg);
            newClassRep.addInnerClass(newInnerClass);
        }
        
        JavaDocComment jdc = classRep.getJavaDoc();
        if (jdc != null) {
            JavaDocComment newJDC = (JavaDocComment)jdc.accept(this, arg);
            newClassRep.setJavaDoc(newJDC);
        }
        
        MultiLineComment mlc = classRep.getComment();
        if (mlc != null) {
            MultiLineComment newMlc = (MultiLineComment)mlc.accept(this, arg);
            newClassRep.setComment(newMlc);
        }
        
        return newClassRep;
    }

    
    /**
     * @param fieldDeclaration
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public JavaFieldDeclaration visitJavaFieldDeclaration (
            JavaFieldDeclaration fieldDeclaration, T arg) {
        
        JavaExpression newInitializer = null;
        if (fieldDeclaration.getInitializer() != null) {
            newInitializer = (JavaExpression)fieldDeclaration.getInitializer().accept(this, arg);
        }
        JavaFieldDeclaration newFieldDeclaration = 
            new JavaFieldDeclaration(fieldDeclaration.getModifiers(),
                                     fieldDeclaration.getFieldType(),
                                     fieldDeclaration.getFieldName(),
                                     newInitializer);
        
        newFieldDeclaration.setJavaDoc((JavaDocComment)fieldDeclaration.getJavaDoc().accept(this, arg));

        return newFieldDeclaration;
    }
    
    /**
     * @param javaConstructor
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public JavaConstructor visitJavaConstructor (
            JavaConstructor javaConstructor, T arg) {
        
        // Build up the parameter names and types.
        int nParams = javaConstructor.getNParams();
        String[] paramNames = new String[nParams];
        JavaTypeName[] paramTypes = new JavaTypeName[nParams];
        for (int i = 0; i < nParams; ++i) {
            paramNames[i] = javaConstructor.getParamName(i);
            paramTypes[i] = javaConstructor.getParamType(i);
        }
        
        JavaConstructor newConstructor = 
            new JavaConstructor(javaConstructor.getModifiers(),
                                paramNames,
                                paramTypes,
                                javaConstructor.getConstructorName());
        
        // Now add in the exceptions.
        for (int i = 0, n = javaConstructor.getNThrownExceptions(); i < n; ++i) {
            newConstructor.addThrows(javaConstructor.getThrownException(i));
        }
        
        // Now add the body of the constructor.
        newConstructor.addStatement((JavaStatement)javaConstructor.getBodyCode().accept(this, arg));
        
        return newConstructor;
    }
    
    /**
     * @param javaMethod
     *          the java model element to be visited
     * @param arg
     *          additional argument for the visitation
     * @return the result from visiting the java model element
     */
    public JavaMethod visitJavaMethod (
            JavaMethod javaMethod, T arg) {
        
        // Build up the argument names and types.
        final int nParams = javaMethod.getNParams();
        final String[] paramNames = new String[nParams];
        final JavaTypeName[] paramTypes = new JavaTypeName[nParams];
        final boolean[] paramFinal = new boolean[nParams];
        for (int i = 0; i < nParams; ++i) {
            paramNames[i] = javaMethod.getParamName(i);
            paramTypes[i] = javaMethod.getParamType(i);
            paramFinal[i] = javaMethod.isParamFinal(i);
        }
        
        JavaMethod newMethod = 
            new JavaMethod (
                javaMethod.getModifiers(),
                javaMethod.getReturnType(),
                paramNames,
                paramTypes,
                paramFinal,
                javaMethod.getMethodName());
        
        // Add the thrown exceptions.
        for (int i = 0, n = javaMethod.getNThrownExceptions(); i < n; ++i) {
            newMethod.addThrows(javaMethod.getThrownException(i));
        }
        
        // Add the JavaDoc
        if (javaMethod.getJavaDocComment() != null) {
            newMethod.setJavaDocComment((JavaDocComment)javaMethod.getJavaDocComment().accept(this, arg));
        }
        
        // Add the method body.
        newMethod.addStatement((JavaStatement)javaMethod.getBodyCode().accept(this, arg));
        
        return newMethod;
    }
    
}
