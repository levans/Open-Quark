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
 * ExpressionCopier.java
 * Creation date: Dec. 10, 2006
 * By: Raymond Cypher
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * ExpressionCopier is an implementation of the ExpressionVisitor which
 * performs a deep copy of the Expression elements. Each visit method
 * returns as its return value a deep copy of the element it visits.
 * <p>
 * 
 * This class is intended to be the base class of other visitors that need to
 * perform Expression transformations, i.e. taking an Expression as input,
 * generating a new Expression as output. For example, a visitor that needs
 * to change all invocations of "Math.sin" to "Math.cos" can be written
 * as:
 * <p>
 * 
 * <pre><code>
 * class ReferenceRenamer extends ExpressionCopier&lt;Void&gt; {
 * 
 *     public Expression visitVar(
 *         Expression.Var var, Void arg) {
 * 
 *         if (var.getName().getQualifiedName().equals("Math.sin")) {
 * 
 *             // Create a new Var instance with appropriate changes.
 * 
 *         } else {
 *             return super.visitVar(var, arg);
 *         }
 *     }
 * }
 * </code></pre>
 *
 * To use this transformation, one can simply apply it to an Expression's
 * root element:
 * <pre><code>
 * Expression transformedElement =
 *     (Expression)originalElement.accept(new ReferenceRenamer(), null);
 * </code></pre>
 *
 * The cast of the return value is required because the ExpressionVisitor
 * interface declares all visit methods to have the generic return type of
 * java.lang.Object. Each visit method in every subclass of ExpressionCopier is
 * generally obligated to return an object of the same type as the element being
 * visited. An exception to this rule is that it is usually acceptable that
 * visiting an element in the Expression hierarchy returns an element
 * representing a different kind of expression.
 * <p>
 * 
 * Note that the visitVar method above 
 * defaults to a call to the supertype's implementation. This 
 * ensures that in the case when no transformations are
 * required, a simple deep copy of the Expression element is returned. In
 * general, in any situations where the supertype's implementation is not
 * invoked, it is the responsibility of the subclass' implementation to 1)
 * traverse through any child elements if necessary, and 2) provide the new
 * Expression element resulting from the transformation of the element being
 * visited.
 * <p>
 * 
 * In ExpressionCopier, the argument supplied to the visit methods are ignored.
 * Subclasses of ExpressionCopier are free to use the argument for their own
 * purposes, and the traversal logic in ExpressionCopier will properly
 * propagate the supplied arguments down to child elements.
 * <p>
 * 
 * Note: the design of this class mandates that all fields whose type descend
 * from Expression must be visited by the visitor. In particular,
 * for array-valued fields, each array element must be individually visited and
 * consequently copied.
 * 
 * This usage guideline applies to any modifications to this class, as well
 * as any subclass that aims to preserve the deep-copying semantics.
 * <p>
 * 
 * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
 * 
 * @author Raymond Cypher
 */

public class ExpressionCopier<T> implements ExpressionVisitor<T, Object> {

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitApplication(org.openquark.cal.compiler.Expression.Application, java.lang.Object)
     */
    public Expression visitApplication(
            Expression.Appl appl, T arg) {
        return new Expression.Appl(
                (Expression)appl.getE1().accept(this, arg), 
                (Expression)appl.getE2().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitCast(org.openquark.cal.compiler.Expression.Cast, java.lang.Object)
     */
    public Expression visitCast(
            Expression.Cast cast, T arg) {
        return Expression.Cast.makeWithCastTypeProvider(cast.getCastTypeProvider(), (Expression.Var)cast.getVarToCast().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitDataConsSelection(org.openquark.cal.compiler.Expression.DataConsSelection, java.lang.Object)
     */
    public Expression visitDataConsSelection(
            Expression.DataConsSelection dcSelection, T arg) {
        return new Expression.DataConsSelection(
                (Expression)dcSelection.getDCValueExpr().accept(this, arg),
                dcSelection.getDataConstructor(),
                dcSelection.getFieldIndex(),
                (Expression.ErrorInfo)dcSelection.getErrorInfo().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitErrorInfo(org.openquark.cal.compiler.Expression.ErrorInfo, java.lang.Object)
     */
    public Expression visitErrorInfo(
            Expression.ErrorInfo errorInfo, T arg){
        return new Expression.ErrorInfo(errorInfo.getTopLevelFunctionName(), errorInfo.getLine(), errorInfo.getColumn());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitLetDefn(org.openquark.cal.compiler.Expression.LetDefn, java.lang.Object)
     */
    public Expression.Let.LetDefn visitLetDefn(
            Expression.Let.LetDefn letDefn, T arg) {
        
        return new Expression.Let.LetDefn(
                letDefn.getVar(),
                (Expression)letDefn.getExpr().accept(this, arg),
                letDefn.getVarType());
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitLetNonRec(org.openquark.cal.compiler.Expression.LetNonRec, java.lang.Object)
     */
    public Expression visitLetNonRec(
            Expression.LetNonRec letNonRec, T arg) {
        return new Expression.LetNonRec(
                (Expression.Let.LetDefn)letNonRec.getDefn().accept(this, arg),
                (Expression)letNonRec.getBody().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitLetRec(org.openquark.cal.compiler.Expression.LetRec, java.lang.Object)
     */
    public Expression visitLetRec(
            Expression.LetRec letRec, T arg) {
        Expression.Let.LetDefn defns[] = letRec.getDefns();
        Expression.Let.LetDefn newDefns[] = new Expression.Let.LetDefn[defns.length];
        for (int i = 0; i < defns.length; ++i) {
            newDefns[i] = (Expression.LetNonRec.LetDefn)defns[i].accept(this, arg);
        }
        
        return new Expression.LetRec(
                newDefns,
                (Expression)letRec.getBody().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitLiteral(org.openquark.cal.compiler.Expression.Literal, java.lang.Object)
     */
    public Expression visitLiteral(
            Expression.Literal literal, T arg) {
        return new Expression.Literal(literal.getLiteral());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitPackCons(org.openquark.cal.compiler.Expression.PackCons, java.lang.Object)
     */
    public Expression visitPackCons(
            Expression.PackCons packCons, T arg){
        return new Expression.PackCons(packCons.getDataConstructor());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitRecordCase(org.openquark.cal.compiler.Expression.RecordCase, java.lang.Object)
     */
    public Expression visitRecordCase(
            Expression.RecordCase recordCase, T arg){
        
        SortedMap<FieldName, String> newFieldBindingVarMap = new TreeMap<FieldName, String>(recordCase.getFieldBindingVarMap());
        return new Expression.RecordCase(
                (Expression)recordCase.getConditionExpr().accept(this,arg),
                recordCase.getBaseRecordPatternVarName(),
                newFieldBindingVarMap,
                (Expression)recordCase.getResultExpr().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitRecordExtension(org.openquark.cal.compiler.Expression.RecordExtension, java.lang.Object)
     */
    public Expression visitRecordExtension(
            Expression.RecordExtension recordExtension, T arg){
        
        SortedMap<FieldName, Expression> newExtensionFieldsMap = new TreeMap<FieldName, Expression>();
        SortedMap<FieldName, Expression> extensionFieldsMap = recordExtension.getExtensionFieldsMap();
        for (final Map.Entry<FieldName, Expression> entry : extensionFieldsMap.entrySet()) {
            final FieldName key = entry.getKey();
            final Expression fieldExpression = entry.getValue();
            Expression newFieldExpression = (Expression)fieldExpression.accept(this, arg);
            newExtensionFieldsMap.put(key, newFieldExpression);
        }
        
        Expression baseRecordExpression = recordExtension.getBaseRecordExpr();
        Expression newBaseRecordExpression = (baseRecordExpression == null) ? null : (Expression)baseRecordExpression.accept(this, arg); 
        return new Expression.RecordExtension(
                newBaseRecordExpression,
                newExtensionFieldsMap);
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitRecordSelection(org.openquark.cal.compiler.Expression.RecordSelection, java.lang.Object)
     */
    public Expression visitRecordSelection(
            Expression.RecordSelection recordSelection, T arg){
        return new Expression.RecordSelection(
                (Expression)recordSelection.getRecordExpr().accept(this, arg),
                recordSelection.getFieldName());
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitRecordUpdate(org.openquark.cal.compiler.Expression.RecordUpdate, java.lang.Object)
     */
    public Expression visitRecordUpdate(
            Expression.RecordUpdate recordUpdate, T arg) {
        SortedMap<FieldName, Expression> newUpdateFieldValuesMap = new TreeMap<FieldName, Expression>();
        SortedMap<FieldName, Expression> updateFieldValuesMap = recordUpdate.getUpdateFieldValuesMap();
        for (final Map.Entry<FieldName, Expression> entry : updateFieldValuesMap.entrySet()) {
            final FieldName key = entry.getKey();
            final Expression valueExpression = entry.getValue();
            Expression newValueExpression = (Expression)valueExpression.accept(this, arg);
            newUpdateFieldValuesMap.put(key, newValueExpression);
        }
        return new Expression.RecordUpdate(
                (Expression)recordUpdate.getBaseRecordExpr().accept(this, arg),
                newUpdateFieldValuesMap);
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitSwitch(org.openquark.cal.compiler.Expression.Switch, java.lang.Object)
     */
    public Expression visitSwitch(
            Expression.Switch switchExpr, T arg) {
        Expression.Switch.SwitchAlt alts[] = switchExpr.getAlts();
        Expression.Switch.SwitchAlt newAlts[] = new Expression.Switch.SwitchAlt[alts.length];
        for (int i = 0; i < alts.length; ++i) {
            newAlts[i] = (Expression.Switch.SwitchAlt)alts[i].accept(this, arg);
        }
        Expression.ErrorInfo errorInfo = switchExpr.getErrorInfo();
        Expression.ErrorInfo newErrorInfo = (errorInfo == null) ? null : (Expression.ErrorInfo)errorInfo.accept(this, arg);
        return new Expression.Switch(
                (Expression)switchExpr.getSwitchExpr().accept(this, arg),
                newAlts,
                newErrorInfo);
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitSwitchAlt_Matching(org.openquark.cal.compiler.Expression.Switch.SwitchAlt.Matching, java.lang.Object)
     */
    public Expression.Switch.SwitchAlt visitSwitchAlt_Matching(
            Expression.Switch.SwitchAlt.Matching switchAlt, T arg) {
        
        List<Object> newAltTags = new ArrayList<Object>(switchAlt.getAltTags());
        Map<FieldName, String> newFieldNameToVarNameMap = new HashMap<FieldName, String>(switchAlt.getFieldNameToVarNameMap());
        return new Expression.Switch.SwitchAlt.Matching(
                newAltTags,
                newFieldNameToVarNameMap,
                (Expression)switchAlt.getAltExpr().accept(this, arg));
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitSwitchAlt_Positional(org.openquark.cal.compiler.Expression.Switch.SwitchAlt.Positional, java.lang.Object)
     */
    public Expression.Switch.SwitchAlt visitSwitchAlt_Positional(
            Expression.Switch.SwitchAlt.Positional switchAlt, T arg) {
        List<Object> newAltTags = new ArrayList<Object>(switchAlt.getAltTags());
        SortedMap<Integer, String> newPositionToVarNameMap = new TreeMap<Integer, String>(switchAlt.getPositionToVarNameMap());
        return new Expression.Switch.SwitchAlt.Positional(
                newAltTags,
                newPositionToVarNameMap,
                (Expression)switchAlt.getAltExpr().accept(this, arg));
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitTailRecursiveCall(org.openquark.cal.compiler.Expression.TailRecursiveCall, java.lang.Object)
     */
    public Expression visitTailRecursiveCall(
            Expression.TailRecursiveCall tailRecursiveCall, T arg) {
        Expression args[] = tailRecursiveCall.getArguments();
        Expression newArgs[] = new Expression[args.length];
        for (int i = 0; i < args.length; ++i) {
            newArgs[i] = (Expression)args[i].accept(this, arg);
        }
        return new Expression.TailRecursiveCall(
                (Expression.Var)tailRecursiveCall.getVar().accept(this, arg),
                newArgs);
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.compiler.ExpressionVisitor#visitVar(org.openquark.cal.compiler.Expression.Var, java.lang.Object)
     */
    public Expression visitVar(
            Expression.Var var, T arg) {
        FunctionalAgent fa = var.getFunctionalAgent();
        QualifiedName qn = var.getName();
        Expression.ErrorInfo ei = var.getErrorInfo();
        if (fa != null) {
            return new Expression.Var(fa);
        } else
        if (ei != null) {
            return new Expression.Var(ei);
        } else {
            return new Expression.Var(qn);
        }
    }


    
}
