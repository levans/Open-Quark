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
 * ExpressionVisitor.java
 * Creation date: Dec. 10, 2006
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler;


/**
 * A visitor interface for visiting Expression elements.
 * <p>
 * 
 * The visitor mechanism supported by this interface is more general than the
 * regular visitor pattern. In particular, each visit method boasts an argument
 * of the generic type java.lang.Object, and also a return type of
 * java.lang.Object. This allows additional arguments, suitably encapsulated in
 * an object, to be passed in to any visit method. The visit methods are also
 * able to return values, which is useful for cases where the visitor needs to
 * aggregate results in a hierarchical fashion from visiting the tree of expression
 * elements.
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
 * While it is certainly possible to directly implement the ExpressionVisitor
 * interface, it may be easier to subclass from one of the predefined visitor
 * classes. 
 * To construct an Expression to Expression
 * transformation, the <code>ExpressionCopier</code> is a convenient base
 * class to extend since it provides the default behaviour of performing a deep
 * copy of the Expression, while allowing subclasses to hook in and return
 * transformations of Expression elements where required.
 * <p>
 * 
 * If the Expression structure is changed (e.g. when new Expression element
 * classes are added, or when existing element classes are moved around in the
 * inheritence and/or containment hierarchy), then this visitor interface and
 * all implementors must be updated. This may include renaming existing
 * interface methods if element classes have been moved.
 * <p>
 * 
 * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
 * @param <R> the return type. If the return value is not used, specify {@link Void}.
 * 
 * @see org.openquark.cal.compiler.ExpressionCopier
 * 
 * @author Raymond Cypher
 */
public interface ExpressionVisitor<T, R> {

    /**
     * @param appl
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitApplication(
            Expression.Appl appl, T arg);

    /**
     * @param cast
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitCast(
            Expression.Cast cast, T arg);

    /**
     * @param dcSelection
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitDataConsSelection(
            Expression.DataConsSelection dcSelection, T arg);

    /**
     * @param errorInfo
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitErrorInfo(
            Expression.ErrorInfo errorInfo, T arg);

    /**
     * @param letDefn
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitLetDefn(
            Expression.Let.LetDefn letDefn, T arg);
    
    /**
     * @param letNonRec
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitLetNonRec(
            Expression.LetNonRec letNonRec, T arg);

    /**
     * @param letRec
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitLetRec(
            Expression.LetRec letRec, T arg);

    /**
     * @param literal
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitLiteral(
            Expression.Literal literal, T arg);

    /**
     * @param packCons
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitPackCons(
            Expression.PackCons packCons, T arg);

    /**
     * @param recordCase
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitRecordCase(
            Expression.RecordCase recordCase, T arg);

    /**
     * @param recordExtension
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitRecordExtension(
            Expression.RecordExtension recordExtension, T arg);

    /**
     * @param recordSelection
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitRecordSelection(
            Expression.RecordSelection recordSelection, T arg);

    /**
     * @param recordUpdate
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitRecordUpdate(
            Expression.RecordUpdate recordUpdate, T arg);

    /**
     * @param switchAlt
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitSwitchAlt_Matching(
            Expression.Switch.SwitchAlt.Matching switchAlt, T arg);

    /**
     * @param switchAlt
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitSwitchAlt_Positional(
            Expression.Switch.SwitchAlt.Positional switchAlt, T arg);

    /**
     * @param switchExpr
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitSwitch(
            Expression.Switch switchExpr, T arg);

    /**
     * @param tailRecursiveCall
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitTailRecursiveCall(
            Expression.TailRecursiveCall tailRecursiveCall, T arg);

    /**
     * @param var
     *            the Expression element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the Expression element
     */
    public R visitVar(
            Expression.Var var, T arg);


}
