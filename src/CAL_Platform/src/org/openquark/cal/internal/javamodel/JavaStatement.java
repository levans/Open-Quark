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
 * JavaBlockStatement.java
 * Creation date: Sep 11, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.openquark.cal.internal.javamodel.JavaExpression.JavaField;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.services.Assert;


/**
 * Java object representation for BlockStatements.
 *   In the Java grammar, this represents a BlockStatement, which represents LocalVariableDeclarations, and so-called Statements.
 * @author Edward Lam
 */
public abstract class JavaStatement { 
    /**
     * A private constructor to prevent subclasses other than the inner classes above.    
     */
    private JavaStatement() {
        // Constructor made private to prevent instantiation.
    }
    
    /**
     * @return representation of this JavaStatement for debug purposes only.
     */      
    @Override
    public String toString() {
        return JavaSourceGenerator.toDebugString(this);
    }
    
    /**
     * @return true if the statement is the empty block {}.
     */
    boolean emptyStatement() {
        return false;
    }

    /**
     * Accepts the visitation of a visitor, which implements the
     * JavaModelVisitor interface. This abstract method is to be overriden
     * by each concrete subclass so that the corrent visit method on the
     * visitor may be called based upon the type of the element being
     * visited. Each concrete subclass of JavaStatement should correspond
     * one-to-one with a visit method declaration in the SourceModelVisitor
     * interface.
     * <p>
     * 
     * As the JavaModelVisitor follows a more general visitor pattern
     * where arguments can be passed into the visit methods and return
     * values obtained from them, this method passes through the argument
     * into the visit method, and returns as its return value the return
     * value of the visit method.
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
     * @see JavaModelVisitor
     * 
     * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
     * @param <R> the return type. If the return value is not used, specify {@link Void}.
     * 
     * @param visitor
     *            the visitor
     * @param arg
     *            the argument to be passed to the visitor's visitXXX method
     * @return the return value of the visitor's visitXXX method
     */
    public abstract <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg);
        
    /**
     * Encapsulates a number of block statements, optionally encapsulated by a try/catch block.
     * @author Edward Lam
     */
    public static final class Block extends JavaStatement {

        /** the list of statements in this block. */
        private final List<JavaStatement> blockStatements = new ArrayList<JavaStatement>();
        
        /** The exception handlers for an enclosing try/catch block. */
        private final List<JavaExceptionHandler> exceptionHandlers;
        
        /** The number of local variable declarations in this block. */
        private int nLocalVariableDeclarations = 0;
        
        /**
         * Constructor for a Block.
         */
        public Block() {
            this(Collections.<JavaExceptionHandler>emptyList());
        }

        /**
         * Constructor for a Block
         * @param exceptionHandlers any exception handlers for an enclosing try/catch block.
         */
        public Block(List<JavaExceptionHandler> exceptionHandlers) {
            Assert.isNotNull(exceptionHandlers);
            this.exceptionHandlers = exceptionHandlers;
        }
    
        /**
         * Add a statement to this block.
         * @param blockStatement the statement to add.  
         *        If the statement is a block, all component statements will be added separately.
         */
        public void addStatement(JavaStatement blockStatement) {
            assert (blockStatement != null) : "Attempt to add a null block statement.";

            blockStatements.add(blockStatement);
            if (blockStatement instanceof LocalVariableDeclaration) {
                nLocalVariableDeclarations++;
            }
        }
        
        /**
         * Get the number of statements in this block.
         * @return the number of statements.
         */
        public int getNBlockStatements() {
            return blockStatements.size();
        }
        
        /**
         * Get the nth statement in this block.
         * @param n the index of the statement to return.
         * @return the nth statement in this block.
         */
        public JavaStatement getNthBlockStatement(int n) {
            return blockStatements.get(n);
        }
        
        /**
         * Get the exception handlers for this block.
         * @return the exception handlers for this block.  Never null.
         */
        public List<JavaExceptionHandler> getExceptionHandlers() {
            if (exceptionHandlers.isEmpty()) {
                return Collections.<JavaExceptionHandler>emptyList();
            }
            return new ArrayList<JavaExceptionHandler>(exceptionHandlers);
        }
        
        /**
         * Get a copy of this block.
         * @return Block a copy of this block.
         */
        public Block getCopy() {
            Block copy = new Block(getExceptionHandlers());
            copy.blockStatements.addAll(blockStatements);
            copy.nLocalVariableDeclarations = nLocalVariableDeclarations;
            return copy;
        }
        
        /**
         * @return true if the statement is the empty block {}.
         */
        @Override
        public boolean emptyStatement() {
            return getNBlockStatements() == 0;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitBlockStatement(this, arg);
        }        
        
    }

    /**
     * A Java expression that's been turned into a statement.
     * @author Edward Lam
     */
    public static final class ExpressionStatement extends JavaStatement {

        /** The component expression. */
        private final JavaExpression javaExpression;
        
        /**
         * Constructor for an expression statement.
         * @param javaExpression
         */
        public ExpressionStatement(JavaExpression javaExpression) {
            Assert.isNotNull(javaExpression);
            this.javaExpression = javaExpression;
        }

        /**
         * Get the component expression.
         * @return JavaExpression
         */
        public JavaExpression getJavaExpression() {
            if (javaExpression instanceof JavaExpression.PlaceHolder) {
                return ((JavaExpression.PlaceHolder)javaExpression).getActualExpression();
            }
            return javaExpression;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitExpressionStatement(this, arg);
        }        
        
    }

    /**
     * A local variable declaration.
     *   Note: array variables not supported here.
     * @author Edward Lam
     */
    public static final class LocalVariableDeclaration extends JavaStatement {
        private final boolean isFinal;
        
        /** The local variable. */
        private final LocalVariable localVariable;
        
        /** The initializer for the local variable declaration, or null if there isn't any.  */
        private final JavaExpression initializer;
    
        /**
         * Constructor for a LocalVariableDeclaration.
         * @param localVariable the local variable
         */
        public LocalVariableDeclaration(LocalVariable localVariable) {
            this(localVariable, null);
        }
        
        /**
         * Constructor for a LocalVariableDeclaration.
         * @param localVariable the local variable
         * @param initializer the initializer for the local variable
         */
        public LocalVariableDeclaration(LocalVariable localVariable, JavaExpression initializer) {
            this(localVariable, initializer, false);            
        }
        
        /**
         * Constructor for a LocalVariableDeclaration.
         * @param localVariable the local variable
         * @param initializer the initializer for the local variable
         * @param isFinal whether the local variable is final or not
         */
        public LocalVariableDeclaration(LocalVariable localVariable, JavaExpression initializer, boolean isFinal) {
            Assert.isNotNull(localVariable);

            this.localVariable = localVariable;
            this.initializer = initializer;
            this.isFinal = isFinal;
        }        

        /**
         * Get the local variable.
         * @return LocalVariable
         */
        public LocalVariable getLocalVariable() {
            return localVariable;
        }

        /**
         * Get the initializer for the local variable.
         * @return JavaExpression the initializer, or null if there is no initializer.
         */
        public JavaExpression getInitializer() {
            return initializer;
        }
        /**
         * @return boolean
         */
        public boolean isFinal() {
            return isFinal;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitLocalVariableDeclarationStatement(this, arg);
        }        
        
    }
    
    /**
     * Models the java assert statement in both its 1 argument and 2 argument forms:
     * 
     * assert conditionExpr;
     * assert conditionExpr : onFailureExpr;
     * 
     * @author Bo Ilic
     */
    public static final class AssertStatement extends JavaStatement {
        
        /** 
         * if conditionExpr, which must have type boolean, evaluates to false then the assertion throws 
         * a java.lang.AssertionError. Cannot be null.
         */
        private final JavaExpression conditionExpr;
        
        /** 
         * onFailureExpr is an expression having type != void. It is evaluated and then passed as an argument to
         * AssertionError if the assertion fails. May be null, which indicates to call the no argument AssertionError
         * on failure.
         */
        private final JavaExpression onFailureExpr;

        /**
         * The type of the onFailureExpr.  Cannot be void.  This is used to invoke the correct version
         * of the AssertionError constructor.
         */
        private final JavaTypeName onFailureExprType;
        
        /**
         * @param booleanExpr the expression to assert is true.
         */
        public AssertStatement(JavaExpression booleanExpr) {
            this (booleanExpr, null, null);           
        }
        
        /**
         * @param conditionExpr - the expression to assert is true
         * @param onFailureExpr - the expression used as an argument to the AssertionError constructor
         * @param onFailureExprType - the type of onFailureExpr
         */
        public AssertStatement(JavaExpression conditionExpr, JavaExpression onFailureExpr, JavaTypeName onFailureExprType) {
            if (conditionExpr == null) {
                throw new NullPointerException("conditionExpr cannot be null");
            }
            if ((onFailureExpr == null) != (onFailureExprType == null)) {
                throw new NullPointerException("onFailureExpr and onFailureExprType must both be null or both be non-null");
            }
            if (onFailureExprType != null && onFailureExprType.equals(JavaTypeName.VOID)) {
                throw new NullPointerException("onFailureExprType cannot be void");
            }
            this.conditionExpr = conditionExpr;
            this.onFailureExpr = onFailureExpr;
            this.onFailureExprType = onFailureExprType;
        }
        
        /** 
         * @return if conditionExpr, which must have type boolean, evaluates to false then the assertion throws 
         * a java.lang.AssertionError. Cannot be null.
         */        
        public JavaExpression getConditionExpr() {
            return conditionExpr;
        }
        
        /** 
         * @return onFailureExpr is an expression having type != void. It is evaluated and then passed as an argument to
         * AssertionError if the assertion fails. May be null, which indicates to call the no argument AssertionError
         * on failure.
         */        
        public JavaExpression getOnFailureExpr() {
            return onFailureExpr;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitAssertStatement(this, arg);
        }

        public JavaTypeName getOnFailureExprType() {
            return onFailureExprType;
        }        
    }
    
    /**
     * An If-Then-Else statement
     * @author Edward Lam
     */
    public static final class IfThenElseStatement extends JavaStatement {
            
        /** The "if" condition. */
        private final JavaExpression condition;
            
        /** The "then" clause. */
        private final JavaStatement thenStatement;

        /** The "else" clause. */
        private final JavaStatement elseStatement;
        
        /**
         * Constructor for an IfThenElseStatement, where there is only a "then" statement.
         * @param condition the condition tested by the "if".
         * @param thenStatement the statement for the "then" clause.
         */
        public IfThenElseStatement(JavaExpression condition, JavaStatement thenStatement) {
            this(condition, thenStatement, new Block());
        }
        
        /**
         * Constructor for an IfThenElseStatement.
         * @param condition the condition tested by the "if".
         * @param thenStatement the statement for the "then" clause.
         * @param elseStatement the statement for the "else" clause.
         */
        public IfThenElseStatement(JavaExpression condition, JavaStatement thenStatement, JavaStatement elseStatement) {
            Assert.isNotNull(condition);
            Assert.isNotNull(thenStatement);
            Assert.isNotNull(elseStatement);

            this.condition = condition;
            this.thenStatement = thenStatement;
            this.elseStatement = elseStatement;
        }
        
        /**
         * Get the "if" condition.
         * @return JavaExpression
         */
        public JavaExpression getCondition() {
            return condition;
        }
        
        /**
         * Get the "then" clause.
         * @return JavaStatement
         */
        public JavaStatement getThenStatement() {
            return thenStatement;
        }
        
        /**
         * Get the "else" clause.
         * @return JavaStatement
         */
        public JavaStatement getElseStatement() {
            return elseStatement;
        }
        
        /**         
         * @return boolean true if the if-then-else has an empty else statement i.e. it is just "else {}", or equivalently, it is
         *    really an "if-then" statement without an else clause. 
         */
        public boolean hasEmptyElseStatement() {
            return elseStatement.emptyStatement();            
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitIfThenElseStatement(this, arg);
        }        
        
    }

    /**
     * A switch statement.
     *  Note: Java supports multiple case labels for a given switch block statement group.
     * @author Edward Lam
     */
    public static final class SwitchStatement extends JavaStatement {
        
        /** The condition tested by the switch. */
        private final JavaExpression condition;
        
        /** (IntCaseGroup) All cases except the default case, in added order. */
        private List<IntCaseGroup> caseList = new ArrayList<IntCaseGroup>();
        
        /** The default case. */
        private DefaultCase defaultCase = null;
        
        /**
         * Constructor for a SwitchStatement.
         * @param condition the condition tested by the switch.
         */
        public SwitchStatement(JavaExpression condition) {
            Assert.isNotNull(condition);
            this.condition = condition;
        }
        
        /**
         * Add a case to this switch statement.
         * @param caseToAdd the case to add.
         */
        public void addCase(SwitchCase caseToAdd) {

            if (caseToAdd instanceof IntCaseGroup) {
                caseList.add((IntCaseGroup)caseToAdd);
                Collections.sort(caseList, new IntCaseGroupComparator());
            
            } else if (caseToAdd instanceof DefaultCase) {
                this.defaultCase = (DefaultCase)caseToAdd;

            } else {
                assert false : ("Unrecognized case type: " + caseToAdd.getClass().getName());
            }
        }
        
        /**
         * Get the condition tested by the switch.
         * @return JavaExpression
         */
        public JavaExpression getCondition() {
            return condition;
        }

        /**
         * Get the list of switch case groups (not including the default).
         * @return List (IntCaseGroup)
         */
        public List<IntCaseGroup> getCaseGroups () {
            return new ArrayList<IntCaseGroup>(caseList);
        }
        
        /**
         * Get the statement for the default case.
         * @return JavaStatement
         */
        public JavaStatement getDefaultStatement() {
            if (defaultCase == null) {
                return null;
            }
            return defaultCase.getStatement();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitSwitchStatement(this, arg);
        }        
        
        /**
         * A switch case.
         * @author Edward Lam
         */
        public abstract static class SwitchCase {
            /** The statements for this case. */
            private final JavaStatement caseStatement;
            
            /**
             * Constructor for a SwitchCase.
             * @param caseStatement the statements for this case.
             */
            private SwitchCase(JavaStatement caseStatement) {
                Assert.isNotNull(caseStatement);
                this.caseStatement = caseStatement;
            }
            
            /**
             * Get the statements for this case.
             * @return JavaStatement
             */
            public JavaStatement getStatement() {
                return caseStatement;
            }
            
            /**
             * Accepts the visitation of a visitor, which implements the
             * JavaModelVisitor interface. This abstract method is to be overriden
             * by each concrete subclass so that the corrent visit method on the
             * visitor may be called based upon the type of the element being
             * visited. Each concrete subclass of JavaStatement should correspond
             * one-to-one with a visit method declaration in the SourceModelVisitor
             * interface.
             * <p>
             * 
             * As the JavaModelVisitor follows a more general visitor pattern
             * where arguments can be passed into the visit methods and return
             * values obtained from them, this method passes through the argument
             * into the visit method, and returns as its return value the return
             * value of the visit method.
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
             * @see JavaModelVisitor
             * 
             * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
             * @param <R> the return type. If the return value is not used, specify {@link Void}.
             * 
             * @param visitor
             *            the visitor
             * @param arg
             *            the argument to be passed to the visitor's visitXXX method
             * @return the return value of the visitor's visitXXX method
             */
            public abstract <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg);
        }
        
        private static class IntCaseGroupComparator implements Comparator<IntCaseGroup> {
            public int compare (IntCaseGroup o1, IntCaseGroup o2) {
                int i1 = o1.getNthCaseLabel(0);
                int i2 = o2.getNthCaseLabel(0);
                return i1 - i2;
            }
        }

        /**
         * A group of int cases with a common case statement.
         * @author Edward Lam
         */
        public static final class IntCaseGroup extends SwitchCase {
            /** The case labels. */
            private final int[] caseLabels;

            /**
             * Constructor for an IntCaseGroup with only one case label.
             * @param caseLabel the case label
             * @param caseStatement
             */
            public IntCaseGroup(int caseLabel, JavaStatement caseStatement) {
                this(new int[]{caseLabel}, caseStatement);
            }
            
            /**
             * Constructor for an IntCaseGroup.
             * @param caseLabels the case labels
             * @param caseStatement
             */
            public IntCaseGroup(int[] caseLabels, JavaStatement caseStatement) {
                super(caseStatement);
                this.caseLabels = caseLabels.clone();
            }
            
            /**
             * Get the number of case labels in this group.
             * @return the number of case labels.
             */
            public int getNCaseLabels() {
                return caseLabels.length;
            }
            
            /**
             * Get the nth case label in this group.
             * @param n the index of the case label to return.
             * @return the nth case label in this group.
             */
            public int getNthCaseLabel(int n) {
                return caseLabels[n];
            }
            
            /**
             * @return a copy of the array of case labels.
             */
            public int[] getCaseLabels () {
                int[] rv = new int[caseLabels.length];
                System.arraycopy(caseLabels, 0, rv, 0, rv.length);
                return rv;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitIntCaseGroup(this, arg);
            }        
            
        }
        
        /**
         * A default case.
         * @author Edward Lam
         */
        public static final class DefaultCase extends SwitchCase {
            /**
             * Constructor for a DefaultCase.
             * @param caseStatement
             */
            public DefaultCase(JavaStatement caseStatement) {
                super(caseStatement);
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitDefaultCase(this, arg);
            }        
            
        }
    }
    
    /**
     * A return statement.
     * @author Edward Lam
     */
    public static final class ReturnStatement extends JavaStatement {

        /** The JavaExpression to return, or null if no value is returned. */
        private final JavaExpression returnExpression;
        
        /**
         * Constructor for a ReturnStatement.
         */
        public ReturnStatement() {
            this.returnExpression = null;
        }
        
        /**
         * Constructor for a ReturnStatement.
         * @param returnExpression the JavaExpression to return, or null if no value is returned.
         */
        public ReturnStatement(JavaExpression returnExpression) {
            this.returnExpression = returnExpression;
        }
        
        /**
         * Get the JavaExpression to return, or null if no value is returned.
         * @return JavaExpression
         */
        public JavaExpression getReturnExpression() {
            return returnExpression;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitReturnStatement(this, arg);
        }        

    }
    
    public static final class ThrowStatement extends JavaStatement {
    
        /** The JavaExpression to throw. */
        private final JavaExpression thrownExpression;
        
        /**
         * Constructor for a ThrowStatement
         * @param thrownExpression the JavaExpression to throw.
         */
        public ThrowStatement (JavaExpression thrownExpression) {
            this.thrownExpression = thrownExpression;
            Assert.isNotNull(thrownExpression);            
        }
        
        /**
         * Get the java expression to throw.
         * @return JavaExpression
         */
        public JavaExpression getThrownExpression () {
            return thrownExpression;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitThrowStatement(this, arg);
        }        
    }
    
    /**
     * A JavaDoc comment.
     * Note: this isn't really a Statement (as defined by Java)..
     * @author Raymond Cypher
     */
    public static final class JavaDocComment extends MultiLineComment {
        /**
         * Constructor for a multi line comment.
         * @param lines
         */
        public JavaDocComment (List<String> lines) {
            super (lines);
        }
        
        /**
         * Constructor for multi line comment.
         * @param content
         */
        public JavaDocComment (String content) {
            super (content);
        }
        
        /**
         * Constructor for multi line comment.
         * @param other
         */
        public JavaDocComment (MultiLineComment other) {
            super (other);
        }
        
        /** 
         * Constructor for an empty JavaDocComment
         */
        public JavaDocComment () {
            
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitJavaDocCommentStatement(this, arg);
        } 
    }
    
    /**
     * A multi line comment.
     * Note: this isn't really a Statement (as defined by Java)..
     * @author Raymond Cypher
     */
    public static class MultiLineComment extends Comment {
        /** List of String.
         * The lines of text in the comment. */
        private final List<String> commentLines = new ArrayList<String>();
        
        /**
         * Constructor for a multi line comment.
         * @param lines
         */
        public MultiLineComment (List<String> lines) {
            for (int i = 0, n = lines.size(); i < n; ++i) {
                commentLines.add(lines.get(i));
            }
        }
        
        /**
         * Constructor for multi line comment.
         * @param content
         */
        public MultiLineComment (String content) {
            // Break content in to lines.
            StringTokenizer stk = new StringTokenizer (content, "\r\n");
            while (stk.hasMoreTokens()) {
                commentLines.add(stk.nextToken());
            }
        }
        
        /**
         * Constructor for multi line comment.
         * @param other
         */
        public MultiLineComment (MultiLineComment other) {
            for (final String commentLine : other.getCommentLines()) {
                this.addLine(commentLine);
            }
        }
        
        /**
         * Constructor for an empty multi-line comment.
         */
        public MultiLineComment () {
            
        }
        
        /**
         * @return an unmodifiable collection containing the individual lines
         * of the comment as strings.
         */
        public Collection<String> getCommentLines () {
            return Collections.unmodifiableCollection(commentLines);
        }
        
        /**
         * Add a line to the multi line comment.
         * @param line
         */
        public void addLine (String line) {
            // Break content in to lines.
            StringTokenizer stk = new StringTokenizer (line, "\r\n");
            while (stk.hasMoreTokens()) {
                commentLines.add(stk.nextToken());
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitMultiLineCommentStatement(this, arg);
        }   
    }
    
    /**
     * A line comment.
     * Note: this isn't really a Statement (as defined by Java)..
     * @author Edward Lam
     */
    public static final class LineComment extends Comment {
        
        /** The text of the comment. */
        private final String commentText;
        
        /**
         * Constructor for a line comment.
         * @param commentText the text of the comment.
         */
        public LineComment(String commentText) {
            Assert.isNotNull(commentText);
            this.commentText = commentText;
        }
        
        /**
         * Get the text of the comment.
         * @return String
         */
        public String getCommentText() {
            return commentText;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitLineCommentStatement(this, arg);
        }        
        
    }

    /**
     * Base class for comments in the Java model.
     * @author RCypher
     *
     */
    public static abstract class Comment extends JavaStatement {
        
    }
    
    /**
     * Represents an unconditional loop. i.e. while (true) {...}
     * @author RCypher
     *
     */
    public static final class UnconditionalLoop extends JavaStatement {
        /**
         * Label for the loop.
         */
        private final String label;
        
        /** Body of the loop. */
        private final JavaStatement body;
        
        public UnconditionalLoop (String label, JavaStatement body) {
            this.label = label;
            this.body = body;
        }
        /**
         * @return Returns the body.
         */
        public JavaStatement getBody() {
            return body;
        }
        /**
         * @return Returns the label.
         */
        public String getLabel() {
            return label;
        }
    
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitUnconditonalLoop(this, arg);
        }        
    }

    /**
     * Represents a method invocation that is wrapped in a synchronized block. 
     * @author rcypher
     */
    public static class SynchronizedMethodInvocation extends JavaStatement {
        
        /** The method invocation. */
        private final MethodInvocation methodInvocation;
        
        /** The object that is being synchronized on. */
        private final JavaExpression synchronizingObject;
        
        /** Create a SynchronizedMethodInvocation that is being synchronized on 
         * a java field.
         * @param methodInvocation
         * @param synchronizingObject
         */
        public SynchronizedMethodInvocation (MethodInvocation methodInvocation, JavaField synchronizingObject) {
            // Check that the field in question is an object (i.e. we can't synchronize on a primitive type.)
            if (methodInvocation == null || synchronizingObject == null || !synchronizingObject.getFieldType().isObjectReference()) {
                throw new IllegalArgumentException("Illegal argument to SynchronizedMethodInvocation constructor.");
            }
            
            this.methodInvocation = methodInvocation;
            this.synchronizingObject = synchronizingObject;
        }

        public MethodInvocation getMethodInvocation() {
            return methodInvocation;
        }
        
        public JavaExpression getSynchronizingObject() {
            return synchronizingObject;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitSynchronizedMethodInvocationStatement(this, arg);
        }        
        
    }
    
    /** 
     * Represents a labeled continue statement.
     * @author RCypher
     */
    public static final class LabelledContinue extends JavaStatement {
        private final String label;
        
        public LabelledContinue (String label) {
            this.label = label;
        }
        
        public String getLabel () {
            return label;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitLabelledContinueStatement(this, arg);
        }        
        
    }
    
}

