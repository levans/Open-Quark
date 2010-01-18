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
 * SourceModelTraverser.java
 * Creation date: Feb 16, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import org.openquark.cal.compiler.SourceModel.ArgBindings;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Constraint;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.SourceModel.FieldPattern;
import org.openquark.cal.compiler.SourceModel.Friend;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.Parameter;
import org.openquark.cal.compiler.SourceModel.Pattern;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;
import org.openquark.cal.compiler.SourceModel.Import.UsingItem;

/**
 * SourceModelTraverser is an implementation of the SourceModelVisitor which
 * performs a pre-order traversal of the source model elements it visits. All
 * and only those elements belonging to the SourceModel.SourceElement hierarchy
 * of element types are traversed.
 * <p>
 * 
 * This class is intended to be the base class of other visitors that need to
 * traverse a source model. For example, a visitor that needs to obtain a list
 * of all local function definitions within a source model can be written as:
 * <p>
 * 
 * <pre><code>
 * class FunctionLocalDefnExtractor extends SourceModelTraverser&lt;Void, Void&gt; {
 * 
 *     private java.util.Set&lt;String&gt; definitions = new java.util.HashSet&lt;String&gt;();
 * 
 *     public Void visit_LocalDefn_Function_Definition(
 *         SourceModel.LocalDefn.Function.Definition function, Void arg) {
 * 
 *         definitions.add(function.getName());
 * 
 *         return super.visit_LocalDefn_Function_Definition(function, arg);
 *     }
 * }
 * </code></pre>
 * 
 * Note that the visitFunctionLocalDefn method contains a call to the
 * supertype's implementation (in this case SourceModelTraverser's). The purpose
 * of this call is to let the SourceModelTraverser's implementation to continue
 * on the traversal through the subtree of elements rooted at the source element
 * being visited. When implementing a visitor for traversing source models, one
 * could omit the call to the supertype's implementation <i>only if </i> it
 * properly replaces the traversal logic contained in the supertype with its
 * own.
 * <p>
 * 
 * In SourceModelTraverser, the argument supplied to the visit methods are
 * ignored, and all methods return null as their return values. Subclasses of
 * SourceModelTraverser are free to use the argument and return value for their
 * own purposes, and the traversal logic in SourceModelTraverser will properly
 * propagate the supplied arguments down to child elements.
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
 * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
 * @param <R> the return type. If the return value is not used, specify {@link Void}.
 * 
 * @author Joseph Wong
 */
public class SourceModelTraverser<T, R> implements SourceModelVisitor<T, R> {

    /**
     * @param application the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Application(
        Expr.Application application, T arg) {

        SourceModel.verifyArg(application, "application");

        final int nExpressions = application.getNExpressions();
        for (int i = 0; i < nExpressions; i++) {
            application.getNthExpression(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_DataCons(
        Expr.DataCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        cons.getDataConsName().accept(this, arg);
        return null;
    }

    /**
     * @param var the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Var(
        Expr.Var var, T arg) {

        SourceModel.verifyArg(var, "var");

        var.getVarName().accept(this, arg);
        return null;
    }

    /**
     * @param let the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Let(
        Expr.Let let, T arg) {

        SourceModel.verifyArg(let, "let");

        final int nLocalFunctions = let.getNLocalDefinitions();
        for (int i = 0; i < nLocalFunctions; i++) {
            let.getNthLocalDefinition(i).accept(this, arg);
        }

        let.getInExpr().accept(this, arg);
        return null;
    }

    /**
     * @param caseExpr the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case(
        Expr.Case caseExpr, T arg) {

        SourceModel.verifyArg(caseExpr, "caseExpr");

        caseExpr.getConditionExpr().accept(this, arg);

        final int nCaseAlts = caseExpr.getNCaseAlts();
        for (int i = 0; i < nCaseAlts; i++) {
            caseExpr.getNthCaseAlt(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param defaultAlt the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_Default(
        Expr.Case.Alt.Default defaultAlt, T arg) {

        SourceModel.verifyArg(defaultAlt, "defaultAlt");

        defaultAlt.getAltExpr().accept(this, arg);
        return null;
    }

    /**
     * @param tuple the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackTuple(
        Expr.Case.Alt.UnpackTuple tuple, T arg) {

        SourceModel.verifyArg(tuple, "tuple");

        final int nPatterns = tuple.getNPatterns();
        for (int i = 0; i < nPatterns; i++) {
            tuple.getNthPattern(i).accept(this, arg);
        }

        tuple.getAltExpr().accept(this, arg);
        return null;
    }

    /**
     * @param unit the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackUnit(
        Expr.Case.Alt.UnpackUnit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        unit.getAltExpr().accept(this, arg);
        return null;
    }

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackDataCons(
        Expr.Case.Alt.UnpackDataCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        for (int i = 0, nDataConsNames = cons.getNDataConsNames(); i < nDataConsNames; i++) {
            cons.getNthDataConsName(i).accept(this, arg);
        }
        cons.getArgBindings().accept(this, arg);
        cons.getAltExpr().accept(this, arg);
        return null;
    }
    
    /**
     * @param argBindings the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_ArgBindings_Matching(
        ArgBindings.Matching argBindings, T arg) {
        
        final int nPatterns = argBindings.getNFieldPatterns();
        for (int i = 0; i < nPatterns; i++) {
            argBindings.getNthFieldPattern(i).accept(this, arg);
        }
        return null;
    }
    
    /**
     * @param argBindings the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_ArgBindings_Positional(
        ArgBindings.Positional argBindings, T arg) {
        
        final int nPatterns = argBindings.getNPatterns();
        for (int i = 0; i < nPatterns; i++) {
            argBindings.getNthPattern(i).accept(this, arg);
        }
        return null;
    }

    /**
     * @param intAlt the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackInt(
        Expr.Case.Alt.UnpackInt intAlt, T arg) {

        SourceModel.verifyArg(intAlt, "intAlt");

        intAlt.getAltExpr().accept(this, arg);
        return null;
    }
    
    /**
     * @param charAlt the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackChar(
        Expr.Case.Alt.UnpackChar charAlt, T arg) {

        SourceModel.verifyArg(charAlt, "charAlt");

        charAlt.getAltExpr().accept(this, arg);
        return null;
    }
    
    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackListCons(
        Expr.Case.Alt.UnpackListCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        cons.getHeadPattern().accept(this, arg);
        cons.getTailPattern().accept(this, arg);
        cons.getAltExpr().accept(this, arg);
        return null;
    }

    /**
     * @param nil the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackListNil(
        Expr.Case.Alt.UnpackListNil nil, T arg) {

        SourceModel.verifyArg(nil, "nil");

        nil.getAltExpr().accept(this, arg);
        return null;
    }

    /**
     * @param record the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Case_Alt_UnpackRecord(
        Expr.Case.Alt.UnpackRecord record, T arg) {

        SourceModel.verifyArg(record, "record");

        if (record.getBaseRecordPattern() != null) {
            record.getBaseRecordPattern().accept(this, arg);
        }

        final int nFieldPatterns = record.getNFieldPatterns();
        for (int i = 0; i < nFieldPatterns; i++) {
            record.getNthFieldPattern(i).accept(this, arg);
        }

        record.getAltExpr().accept(this, arg);
        return null;
    }

    /**
     * @param pattern the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_FieldPattern(
        FieldPattern pattern, T arg) {

        SourceModel.verifyArg(pattern, "pattern");

        pattern.getFieldName().accept(this, arg);
        if (pattern.getPattern() != null) {
            pattern.getPattern().accept(this, arg);
        }
        return null;
    }

    /**
     * @param lambda the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Lambda(
        Expr.Lambda lambda, T arg) {

        SourceModel.verifyArg(lambda, "lambda");

        final int nParameters = lambda.getNParameters();
        for (int i = 0; i < nParameters; i++) {
            lambda.getNthParameter(i).accept(this, arg);
        }

        lambda.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * @param ifExpr the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_If(
        Expr.If ifExpr, T arg) {

        SourceModel.verifyArg(ifExpr, "ifExpr");

        ifExpr.getConditionExpr().accept(this, arg);
        ifExpr.getThenExpr().accept(this, arg);
        ifExpr.getElseExpr().accept(this, arg);
        return null;
    }

    /**
     * @param num the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Literal_Num(
        Expr.Literal.Num num, T arg) {

        SourceModel.verifyArg(num, "num");

        return null;
    }

    /**
     * @param doubleLiteral the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Literal_Double(
        Expr.Literal.Double doubleLiteral, T arg) {

        SourceModel.verifyArg(doubleLiteral, "doubleLiteral");

        return null;
    }
    
    /**
     * @param floatLiteral the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Literal_Float(Expr.Literal.Float floatLiteral, T arg) {
        
        SourceModel.verifyArg(floatLiteral, "floatLiteral");
        
        return null;
    }

    /**
     * @param charLiteral the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Literal_Char(
        Expr.Literal.Char charLiteral, T arg) {

        SourceModel.verifyArg(charLiteral, "charLiteral");

        return null;
    }

    /**
     * @param string the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Literal_StringLit(
        Expr.Literal.StringLit string, T arg) {

        SourceModel.verifyArg(string, "string");

        return null;
    }

    /**
     * @param negate the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_UnaryOp_Negate(
        Expr.UnaryOp.Negate negate, T arg) {

        SourceModel.verifyArg(negate, "negate");

        negate.getExpr().accept(this, arg);
        return null;
    }

    /**
     * @param backquotedOperator the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_BackquotedOperator_Var(
        Expr.BinaryOp.BackquotedOperator.Var backquotedOperator, T arg) {

        SourceModel.verifyArg(backquotedOperator, "backquotedOperator");
        
        backquotedOperator.getOperatorVarExpr().accept(this, arg);
        backquotedOperator.getLeftExpr().accept(this, arg);
        backquotedOperator.getRightExpr().accept(this, arg);
        return null;
    }

    /**
     * @param backquotedOperator the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_BackquotedOperator_DataCons(
        Expr.BinaryOp.BackquotedOperator.DataCons backquotedOperator, T arg) {

        SourceModel.verifyArg(backquotedOperator, "backquotedOperator");
        
        backquotedOperator.getOperatorDataConsExpr().accept(this, arg);
        backquotedOperator.getLeftExpr().accept(this, arg);
        backquotedOperator.getRightExpr().accept(this, arg);
        return null;
    }

    /**
     * @param binop the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    protected R visit_Expr_BinaryOp_Helper(
        Expr.BinaryOp binop, T arg) {

        SourceModel.verifyArg(binop, "binop");

        binop.getLeftExpr().accept(this, arg);
        binop.getRightExpr().accept(this, arg);
        return null;
    }

    /**
     * @param and the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_And(
        Expr.BinaryOp.And and, T arg) {

        SourceModel.verifyArg(and, "and");

        return visit_Expr_BinaryOp_Helper(and, arg);
    }

    /**
     * @param or the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Or(
        Expr.BinaryOp.Or or, T arg) {

        SourceModel.verifyArg(or, "or");

        return visit_Expr_BinaryOp_Helper(or, arg);
    }

    /**
     * @param equals the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Equals(
        Expr.BinaryOp.Equals equals, T arg) {

        SourceModel.verifyArg(equals, "equals");

        return visit_Expr_BinaryOp_Helper(equals, arg);
    }

    /**
     * @param notEquals the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_NotEquals(
        Expr.BinaryOp.NotEquals notEquals, T arg) {

        SourceModel.verifyArg(notEquals, "notEquals");

        return visit_Expr_BinaryOp_Helper(notEquals, arg);
    }

    /**
     * @param lt the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_LessThan(
        Expr.BinaryOp.LessThan lt, T arg) {

        SourceModel.verifyArg(lt, "lt");

        return visit_Expr_BinaryOp_Helper(lt, arg);
    }

    /**
     * @param lteq the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_LessThanEquals(
        Expr.BinaryOp.LessThanEquals lteq, T arg) {

        SourceModel.verifyArg(lteq, "lteq");

        return visit_Expr_BinaryOp_Helper(lteq, arg);
    }

    /**
     * @param gteq the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_GreaterThanEquals(
        Expr.BinaryOp.GreaterThanEquals gteq, T arg) {

        SourceModel.verifyArg(gteq, "gteq");

        return visit_Expr_BinaryOp_Helper(gteq, arg);
    }

    /**
     * @param gt the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_GreaterThan(
        Expr.BinaryOp.GreaterThan gt, T arg) {

        SourceModel.verifyArg(gt, "gt");

        return visit_Expr_BinaryOp_Helper(gt, arg);
    }

    /**
     * @param add the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Add(
        Expr.BinaryOp.Add add, T arg) {

        SourceModel.verifyArg(add, "add");

        return visit_Expr_BinaryOp_Helper(add, arg);
    }

    /**
     * @param subtract the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Subtract(
        Expr.BinaryOp.Subtract subtract, T arg) {

        SourceModel.verifyArg(subtract, "subtract");

        return visit_Expr_BinaryOp_Helper(subtract, arg);
    }

    /**
     * @param multiply the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Multiply(
        Expr.BinaryOp.Multiply multiply, T arg) {

        SourceModel.verifyArg(multiply, "multiply");

        return visit_Expr_BinaryOp_Helper(multiply, arg);
    }

    /**
     * @param divide the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Divide(
        Expr.BinaryOp.Divide divide, T arg) {

        SourceModel.verifyArg(divide, "divide");

        return visit_Expr_BinaryOp_Helper(divide, arg);
    }
    
    /**
     * @param remainder the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Remainder(
        Expr.BinaryOp.Remainder remainder, T arg) {

        SourceModel.verifyArg(remainder, "remainder");

        return visit_Expr_BinaryOp_Helper(remainder, arg);
    }    

    /**
     * @param compose the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Compose(
        Expr.BinaryOp.Compose compose, T arg) {

        SourceModel.verifyArg(compose, "compose");

        return visit_Expr_BinaryOp_Helper(compose, arg);
    }

    /**
     * @param apply the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Apply(
        Expr.BinaryOp.Apply apply, T arg) {

        SourceModel.verifyArg(apply, "apply");

        return visit_Expr_BinaryOp_Helper(apply, arg);
    }

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Cons(
        Expr.BinaryOp.Cons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        return visit_Expr_BinaryOp_Helper(cons, arg);
    }

    /**
     * @param append the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_BinaryOp_Append(
        Expr.BinaryOp.Append append, T arg) {

        SourceModel.verifyArg(append, "append");

        return visit_Expr_BinaryOp_Helper(append, arg);
    }

    /**
     * @param unit the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Unit(
        Expr.Unit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        return null;
    }

    /**
     * @param tuple the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Tuple(
        Expr.Tuple tuple, T arg) {

        SourceModel.verifyArg(tuple, "tuple");

        final int nComponents = tuple.getNComponents();
        for (int i = 0; i < nComponents; i++) {
            tuple.getNthComponent(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param list the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_List(
        Expr.List list, T arg) {

        SourceModel.verifyArg(list, "list");

        final int nElements = list.getNElements();
        for (int i = 0; i < nElements; i++) {
            list.getNthElement(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param record the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Record(
        Expr.Record record, T arg) {

        SourceModel.verifyArg(record, "record");

        if (record.getBaseRecordExpr() != null) {
            record.getBaseRecordExpr().accept(this, arg);
        }

        final int nExtensionFields = record.getNExtensionFields();
        for (int i = 0; i < nExtensionFields; i++) {
            record.getNthExtensionField(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param fieldExtension the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Record_FieldModification_Extension(
        Expr.Record.FieldModification.Extension fieldExtension, T arg) {

        SourceModel.verifyArg(fieldExtension, "fieldExtension");

        fieldExtension.getFieldName().accept(this, arg);
        fieldExtension.getValueExpr().accept(this, arg);
        return null;
    }
    
    /**
     * @param fieldValueUpdate the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Record_FieldModification_Update(
        Expr.Record.FieldModification.Update fieldValueUpdate, T arg) {

        SourceModel.verifyArg(fieldValueUpdate, "fieldValueUpdate");

        fieldValueUpdate.getFieldName().accept(this, arg);
        fieldValueUpdate.getValueExpr().accept(this, arg);
        return null;
    }    

    /**
     * @param field the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_SelectDataConsField(
        Expr.SelectDataConsField field, T arg) {

        SourceModel.verifyArg(field, "field");

        field.getDataConsValuedExpr().accept(this, arg);
        field.getDataConsName().accept(this, arg);
        field.getFieldName().accept(this, arg);
        return null;
    }

    /**
     * @param field the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_SelectRecordField(
        Expr.SelectRecordField field, T arg) {

        SourceModel.verifyArg(field, "field");

        field.getRecordValuedExpr().accept(this, arg);
        field.getFieldName().accept(this, arg);
        return null;
    }

    /**
     * @param signature the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_ExprTypeSignature(
        Expr.ExprTypeSignature signature, T arg) {

        SourceModel.verifyArg(signature, "signature");

        signature.getExpr().accept(this, arg);
        signature.getTypeSignature().accept(this, arg);
        return null;
    }

    /**
     * @param lacks the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Constraint_Lacks(
        Constraint.Lacks lacks, T arg) {

        SourceModel.verifyArg(lacks, "lacks");

        lacks.getTypeVarName().accept(this, arg);
        lacks.getLacksField().accept(this, arg);
        return null;
    }

    /**
     * @param typeClass the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Constraint_TypeClass(
        Constraint.TypeClass typeClass, T arg) {

        SourceModel.verifyArg(typeClass, "typeClass");

        typeClass.getTypeClassName().accept(this, arg);
        typeClass.getTypeVarName().accept(this, arg);
        return null;
    }

    /**
     * @param importStmt the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Import(
        Import importStmt, T arg) {

        SourceModel.verifyArg(importStmt, "importStmt");
        
        importStmt.getImportedModuleName().accept(this, arg);

        Import.UsingItem[] usingItems = importStmt.getUsingItems();
        for (final UsingItem usingItem : usingItems) {
            usingItem.accept(this, arg);
        }
        
        return null;
    }
    
    /**
     * @param usingItemFunction the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Import_UsingItem_Function(
        Import.UsingItem.Function usingItemFunction, T arg) {
        
        SourceModel.verifyArg(usingItemFunction, "usingItemFunction");
        
        return null;
    }
    
    /**
     * @param usingItemDataConstructor the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Import_UsingItem_DataConstructor(
        Import.UsingItem.DataConstructor usingItemDataConstructor, T arg) {
        
        SourceModel.verifyArg(usingItemDataConstructor, "usingItemDataConstructor");
        
        return null;
    }
    
    /**
     * @param usingItemTypeConstructor the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Import_UsingItem_TypeConstructor(
        Import.UsingItem.TypeConstructor usingItemTypeConstructor, T arg) {
        
        SourceModel.verifyArg(usingItemTypeConstructor, "usingItemTypeConstructor");
        
        return null;
    }
    
    /**
     * @param usingItemTypeClass the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Import_UsingItem_TypeClass(
        Import.UsingItem.TypeClass usingItemTypeClass, T arg) {
        
        SourceModel.verifyArg(usingItemTypeClass, "usingItemTypeClass");
        
        return null;
    }
    
    /**
     * @param friendDeclaration the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Friend(
        Friend friendDeclaration, T arg) {

        SourceModel.verifyArg(friendDeclaration, "friendDeclaration");

        friendDeclaration.getFriendModuleName().accept(this, arg);
        
        return null;
    }    

    /**
     * @param function the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_LocalDefn_Function_Definition(
        LocalDefn.Function.Definition function, T arg) {

        SourceModel.verifyArg(function, "function");
        
        if (function.getCALDocComment() != null) {
            function.getCALDocComment().accept(this, arg);
        }

        final int nParameters = function.getNParameters();
        for (int i = 0; i < nParameters; i++) {
            function.getNthParameter(i).accept(this, arg);
        }

        function.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * @param declaration the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_LocalDefn_Function_TypeDeclaration(
        LocalDefn.Function.TypeDeclaration declaration, T arg) {

        SourceModel.verifyArg(declaration, "declaration");
        
        if (declaration.getCALDocComment() != null) {
            declaration.getCALDocComment().accept(this, arg);
        }

        declaration.getDeclaredType().accept(this, arg);
        return null;
    }

    /**
     * @param unpackDataCons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_LocalDefn_PatternMatch_UnpackDataCons(
        LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, T arg) {
        
        SourceModel.verifyArg(unpackDataCons, "unpackDataCons");
        
        unpackDataCons.getDataConsName().accept(this, arg);
        unpackDataCons.getArgBindings().accept(this, arg);
        unpackDataCons.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * @param unpackListCons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_LocalDefn_PatternMatch_UnpackListCons(
        LocalDefn.PatternMatch.UnpackListCons unpackListCons, T arg) {

        SourceModel.verifyArg(unpackListCons, "unpackListCons");

        unpackListCons.getHeadPattern().accept(this, arg);
        unpackListCons.getTailPattern().accept(this, arg);
        unpackListCons.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * @param unpackRecord the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_LocalDefn_PatternMatch_UnpackRecord(
        LocalDefn.PatternMatch.UnpackRecord unpackRecord, T arg) {

        SourceModel.verifyArg(unpackRecord, "unpackRecord");

        if (unpackRecord.getBaseRecordPattern() != null) {
            unpackRecord.getBaseRecordPattern().accept(this, arg);
        }

        final int nFieldPatterns = unpackRecord.getNFieldPatterns();
        for (int i = 0; i < nFieldPatterns; i++) {
            unpackRecord.getNthFieldPattern(i).accept(this, arg);
        }

        unpackRecord.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * @param unpackTuple the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_LocalDefn_PatternMatch_UnpackTuple(
        LocalDefn.PatternMatch.UnpackTuple unpackTuple, T arg) {

        SourceModel.verifyArg(unpackTuple, "unpackTuple");

        final int nPatterns = unpackTuple.getNPatterns();
        for (int i = 0; i < nPatterns; i++) {
            unpackTuple.getNthPattern(i).accept(this, arg);
        }

        unpackTuple.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * @param defn the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_ModuleDefn(
        ModuleDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");
        
        if (defn.getCALDocComment() != null) {
            defn.getCALDocComment().accept(this, arg);
        }
        
        defn.getModuleName().accept(this, arg);

        final int nImportedModules = defn.getNImportedModules();
        for (int i = 0; i < nImportedModules; i++) {
            defn.getNthImportedModule(i).accept(this, arg);
        }

        final int nFriendModules = defn.getNFriendModules();
        for (int i = 0; i < nFriendModules; i++) {
            defn.getNthFriendModule(i).accept(this, arg);
        }

        final int nTopLevelDefns = defn.getNTopLevelDefns();
        for (int i = 0; i < nTopLevelDefns; i++) {
            defn.getNthTopLevelDefn(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param moduleName the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_Module(
        Name.Module moduleName, T arg) {

        SourceModel.verifyArg(moduleName, "moduleName");
        
        moduleName.getQualifier().accept(this, arg);

        return null;
    }

    /**
     * @param qualifier the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_Module_Qualifier(
        Name.Module.Qualifier qualifier, T arg) {

        SourceModel.verifyArg(qualifier, "qualifier");
        
        return null;
    }

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_DataCons(
        Name.DataCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");
        
        if (cons.getModuleName() != null) {
            cons.getModuleName().accept(this, arg);
        }

        return null;
    }

    /**
     * @param function the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_Function(
        Name.Function function, T arg) {

        SourceModel.verifyArg(function, "function");
        
        if (function.getModuleName() != null) {
            function.getModuleName().accept(this, arg);
        }

        return null;
    }

    /**
     * @param typeClass the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_TypeClass(
        Name.TypeClass typeClass, T arg) {

        SourceModel.verifyArg(typeClass, "typeClass");
        
        if (typeClass.getModuleName() != null) {
            typeClass.getModuleName().accept(this, arg);
        }

        return null;
    }

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_TypeCons(
        Name.TypeCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");
        
        if (cons.getModuleName() != null) {
            cons.getModuleName().accept(this, arg);
        }

        return null;
    }

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_WithoutContextCons(
        Name.WithoutContextCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");
        
        if (cons.getModuleName() != null) {
            cons.getModuleName().accept(this, arg);
        }

        return null;
    }

    /**
     * @param name the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_Field(
        Name.Field name, T arg) {

        SourceModel.verifyArg(name, "name");
        
        return null;
    }

    /**
     * @param name the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Name_TypeVar(
        Name.TypeVar name, T arg) {

        SourceModel.verifyArg(name, "name");
        
        return null;
    }

    /**
     * @param parameter the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Parameter(
        Parameter parameter, T arg) {

        SourceModel.verifyArg(parameter, "parameter");

        return null;
    }

    /**
     * @param var the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Pattern_Var(
        Pattern.Var var, T arg) {

        SourceModel.verifyArg(var, "var");

        return null;
    }

    /**
     * @param wildcard the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Pattern_Wildcard(
        Pattern.Wildcard wildcard, T arg) {

        SourceModel.verifyArg(wildcard, "wildcard");

        return null;
    }

    /**
     * @param algebraic the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_FunctionDefn_Algebraic(
        FunctionDefn.Algebraic algebraic, T arg) {

        SourceModel.verifyArg(algebraic, "algebraic");
        
        if (algebraic.getCALDocComment() != null) {
            algebraic.getCALDocComment().accept(this, arg);
        }

        final int nParameters = algebraic.getNParameters();
        for (int i = 0; i < nParameters; i++) {
            algebraic.getNthParameter(i).accept(this, arg);
        }

        algebraic.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * @param foreign the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_FunctionDefn_Foreign(
        FunctionDefn.Foreign foreign, T arg) {

        SourceModel.verifyArg(foreign, "foreign");

        if (foreign.getCALDocComment() != null) {
            foreign.getCALDocComment().accept(this, arg);
        }
        foreign.getDeclaredType().accept(this, arg);
        return null;
    }

    /**
     * @param primitive the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_FunctionDefn_Primitive(
        FunctionDefn.Primitive primitive, T arg) {

        SourceModel.verifyArg(primitive, "primitive");

        if (primitive.getCALDocComment() != null) {
            primitive.getCALDocComment().accept(this, arg);
        }
        primitive.getDeclaredType().accept(this, arg);
        return null;
    }

    /**
     * @param declaration the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_FunctionTypeDeclaraction(
        FunctionTypeDeclaration declaration, T arg) {

        SourceModel.verifyArg(declaration, "declaration");

        if (declaration.getCALDocComment() != null) {
            declaration.getCALDocComment().accept(this, arg);
        }
        declaration.getTypeSignature().accept(this, arg);
        return null;
    }

    /**
     * @param defn the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_InstanceDefn(
        InstanceDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        if (defn.getCALDocComment() != null) {
            defn.getCALDocComment().accept(this, arg);
        }
        defn.getTypeClassName().accept(this, arg);
        defn.getInstanceTypeCons().accept(this, arg);

        final int nConstraints = defn.getNConstraints();
        for (int i = 0; i < nConstraints; i++) {
            defn.getNthConstraint(i).accept(this, arg);
        }

        final int nInstanceMethods = defn.getNInstanceMethods();
        for (int i = 0; i < nInstanceMethods; i++) {
            defn.getNthInstanceMethod(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param function the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_InstanceDefn_InstanceTypeCons_Function(
        InstanceDefn.InstanceTypeCons.Function function, T arg) {

        SourceModel.verifyArg(function, "function");

        function.getDomainTypeVar().accept(this, arg);
        function.getCodomainTypeVar().accept(this, arg);
        return null;
    }

    /**
     * @param list the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_InstanceDefn_InstanceTypeCons_List(
        InstanceDefn.InstanceTypeCons.List list, T arg) {

        SourceModel.verifyArg(list, "list");

        list.getElemTypeVar().accept(this, arg);
        return null;
    }
    
    /**
     * @param record the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_InstanceDefn_InstanceTypeCons_Record(
        InstanceDefn.InstanceTypeCons.Record record, T arg) {

        SourceModel.verifyArg(record, "record");

        record.getElemTypeVar().accept(this, arg);
        return null;
    }      

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_InstanceDefn_InstanceTypeCons_TypeCons(
        InstanceDefn.InstanceTypeCons.TypeCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        cons.getTypeConsName().accept(this, arg);
        for (final Name.TypeVar typeVar : cons.getTypeVars()) {
            typeVar.accept(this, arg);
        }
        return null;
    } 
    /**
     * @param unit the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_InstanceDefn_InstanceTypeCons_Unit(
        InstanceDefn.InstanceTypeCons.Unit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        return null;
    }

    /**
     * @param method the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_InstanceDefn_InstanceMethod(
        InstanceDefn.InstanceMethod method, T arg) {

        SourceModel.verifyArg(method, "method");

        if (method.getCALDocComment() != null) {
            method.getCALDocComment().accept(this, arg);
        }
        
        method.getResolvingFunctionName().accept(this, arg);
        return null;
    }

    /**
     * @param defn the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeClassDefn(
        TypeClassDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        if (defn.getCALDocComment() != null) {
            defn.getCALDocComment().accept(this, arg);
        }
        
        final int nParentClassConstraints = defn.getNParentClassConstraints();
        for (int i = 0; i < nParentClassConstraints; i++) {
            defn.getNthParentClassConstraint(i).accept(this, arg);
        }
        
        defn.getTypeVar().accept(this, arg);

        final int nClassMethodDefns = defn.getNClassMethodDefns();
        for (int i = 0; i < nClassMethodDefns; i++) {
            defn.getNthClassMethodDefn(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param defn the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeClassDefn_ClassMethodDefn(
        TypeClassDefn.ClassMethodDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        if (defn.getCALDocComment() != null) {
            defn.getCALDocComment().accept(this, arg);
        }

        defn.getTypeSignature().accept(this, arg);
        
        if (defn.getDefaultClassMethodName() != null) {
            defn.getDefaultClassMethodName().accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param type the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeConstructorDefn_AlgebraicType(
        TypeConstructorDefn.AlgebraicType type, T arg) {

        SourceModel.verifyArg(type, "type");

        if (type.getCALDocComment() != null) {
            type.getCALDocComment().accept(this, arg);
        }
        
        final int nTypeParameters = type.getNTypeParameters();
        for (int i = 0; i < nTypeParameters; i++) {
            type.getNthTypeParameter(i).accept(this, arg);
        }
        
        final int nDataConstructors = type.getNDataConstructors();
        for (int i = 0; i < nDataConstructors; i++) {
            type.getNthDataConstructor(i).accept(this, arg);
        }
        
        final int nDerivingClauseTypeClassNames = type.getNDerivingClauseTypeClassNames();
        for (int i = 0; i < nDerivingClauseTypeClassNames; i++) {
            type.getDerivingClauseTypeClassName(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param defn the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(
        TypeConstructorDefn.AlgebraicType.DataConsDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");
        
        if (defn.getCALDocComment() != null) {
            defn.getCALDocComment().accept(this, arg);
        }
        
        final int nTypeArgs = defn.getNTypeArgs();
        for (int i = 0; i < nTypeArgs; i++) {
            defn.getNthTypeArg(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param argument the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeConstructorDefn_AlgebraicType_DataConsDefn_TypeArgument(
        TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument argument, T arg) {

        SourceModel.verifyArg(argument, "argument");

        argument.getFieldName().accept(this, arg);
        argument.getTypeExprDefn().accept(this, arg);
        return null;
    }

    /**
     * @param type the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeConstructorDefn_ForeignType(
        TypeConstructorDefn.ForeignType type, T arg) {

        SourceModel.verifyArg(type, "type");
        
        if (type.getCALDocComment() != null) {
            type.getCALDocComment().accept(this, arg);
        }
        
        final int nDerivingClauseTypeClassNames = type.getNDerivingClauseTypeClassNames();
        for (int i = 0; i < nDerivingClauseTypeClassNames; i++) {
            type.getDerivingClauseTypeClassName(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param parenthesized the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_Expr_Parenthesized(Expr.Parenthesized parenthesized, T arg) {
        parenthesized.getExpression().accept(this, arg);
        
        return null;
    }
    
    /** {@inheritDoc} */
    public R visit_TypeExprDefn_Parenthesized(
        TypeExprDefn.Parenthesized parenthesized, T arg) {

        parenthesized.getTypeExprDefn().accept(this, arg);
        return null;
    }

    /**
     * @param application the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_Application(
        TypeExprDefn.Application application, T arg) {

        SourceModel.verifyArg(application, "application");

        final int nTypeExpressions = application.getNTypeExpressions();
        for (int i = 0; i < nTypeExpressions; i++) {
            application.getNthTypeExpression(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param function the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_Function(
        TypeExprDefn.Function function, T arg) {

        SourceModel.verifyArg(function, "function");

        function.getDomain().accept(this, arg);
        function.getCodomain().accept(this, arg);
        return null;
    }

    /**
     * @param list the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_List(
        TypeExprDefn.List list, T arg) {

        SourceModel.verifyArg(list, "list");

        list.getElement().accept(this, arg);
        return null;
    }

    /**
     * @param record the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_Record(
        TypeExprDefn.Record record, T arg) {

        SourceModel.verifyArg(record, "record");

        if (record.getBaseRecordVar() != null) {
            record.getBaseRecordVar().accept(this, arg);
        }

        final int nExtensionFields = record.getNExtensionFields();
        for (int i = 0; i < nExtensionFields; i++) {
            record.getNthExtensionField(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param pair the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_Record_FieldTypePair(
        TypeExprDefn.Record.FieldTypePair pair, T arg) {

        SourceModel.verifyArg(pair, "pair");

        pair.getFieldName().accept(this, arg);
        pair.getFieldType().accept(this, arg);
        return null;
    }

    /**
     * @param tuple the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_Tuple(
        TypeExprDefn.Tuple tuple, T arg) {

        SourceModel.verifyArg(tuple, "tuple");

        final int nComponents = tuple.getNComponents();
        for (int i = 0; i < nComponents; i++) {
            tuple.getNthComponent(i).accept(this, arg);
        }

        return null;
    }

    /**
     * @param cons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_TypeCons(
        TypeExprDefn.TypeCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        cons.getTypeConsName().accept(this, arg);
        return null;
    }

    /**
     * @param var the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_TypeVar(
        TypeExprDefn.TypeVar var, T arg) {

        SourceModel.verifyArg(var, "var");

        var.getTypeVarName().accept(this, arg);
        return null;
    }

    /**
     * @param unit the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeExprDefn_Unit(
        TypeExprDefn.Unit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        return null;
    }

    /**
     * @param signature the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_TypeSignature(
        TypeSignature signature, T arg) {

        SourceModel.verifyArg(signature, "signature");

        final int nConstraints = signature.getNConstraints();
        for (int i = 0; i < nConstraints; i++) {
            signature.getNthConstraint(i).accept(this, arg);
        }

        signature.getTypeExprDefn().accept(this, arg);
        return null;
    }
    
    /**
     * @param comment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    protected R visit_CALDoc_Comment_Helper(
        CALDoc.Comment comment, T arg) {
        
        SourceModel.verifyArg(comment, "comment");
        
        comment.getDescription().accept(this, arg);
        
        final int nTaggedBlocks = comment.getNTaggedBlocks();
        for (int i = 0; i < nTaggedBlocks; i++) {
            comment.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return null;
    }
    
    /**
     * @param module the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_Module(
        CALDoc.Comment.Module module, T arg) {
        
        SourceModel.verifyArg(module, "module");
        
        return visit_CALDoc_Comment_Helper(module, arg);
    }

    /**
     * @param function the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_Function(
        CALDoc.Comment.Function function, T arg) {
        
        SourceModel.verifyArg(function, "function");
        
        return visit_CALDoc_Comment_Helper(function, arg);
    }

    /**
     * @param typeCons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_TypeCons(
        CALDoc.Comment.TypeCons typeCons, T arg) {
        
        SourceModel.verifyArg(typeCons, "typeCons");
        
        return visit_CALDoc_Comment_Helper(typeCons, arg);
    }

    /**
     * @param dataCons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_DataCons(
        CALDoc.Comment.DataCons dataCons, T arg) {
        
        SourceModel.verifyArg(dataCons, "dataCons");
        
        return visit_CALDoc_Comment_Helper(dataCons, arg);
    }

    /**
     * @param typeClass the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_TypeClass(
        CALDoc.Comment.TypeClass typeClass, T arg) {
        
        SourceModel.verifyArg(typeClass, "typeClass");
        
        return visit_CALDoc_Comment_Helper(typeClass, arg);
    }

    /**
     * @param method the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_ClassMethod(
        CALDoc.Comment.ClassMethod method, T arg) {
        
        SourceModel.verifyArg(method, "method");
        
        return visit_CALDoc_Comment_Helper(method, arg);
    }

    /**
     * @param instance the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_Instance(
        CALDoc.Comment.Instance instance, T arg) {
        
        SourceModel.verifyArg(instance, "instance");
        
        return visit_CALDoc_Comment_Helper(instance, arg);
    }

    /**
     * @param method the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_Comment_InstanceMethod(
        CALDoc.Comment.InstanceMethod method, T arg) {
        
        SourceModel.verifyArg(method, "method");
        
        return visit_CALDoc_Comment_Helper(method, arg);
    }

    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_Plain(
        CALDoc.TextSegment.Plain segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        // no children to visit, since a plain text segment's content is just a string.
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_Preformatted(
        CALDoc.TextSegment.Preformatted segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        final int nSegments = segment.getNSegments();
        for (int i = 0; i < nSegments; i++) {
            segment.getNthSegment(i).accept(this, arg);
        }
        
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_URL(
        CALDoc.TextSegment.InlineTag.URL segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_Function(
        CALDoc.TextSegment.InlineTag.Link.Function segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getReference().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_Module(
        CALDoc.TextSegment.InlineTag.Link.Module segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getReference().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_TypeCons(
        CALDoc.TextSegment.InlineTag.Link.TypeCons segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getReference().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_DataCons(
        CALDoc.TextSegment.InlineTag.Link.DataCons segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getReference().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_TypeClass(
        CALDoc.TextSegment.InlineTag.Link.TypeClass segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getReference().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_ConsNameWithoutContext(
        CALDoc.TextSegment.InlineTag.Link.ConsNameWithoutContext segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getReference().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_FunctionWithoutContext(
        CALDoc.TextSegment.InlineTag.Link.FunctionWithoutContext segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getReference().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_Emphasized(
        CALDoc.TextSegment.InlineTag.TextFormatting.Emphasized segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_StronglyEmphasized(
        CALDoc.TextSegment.InlineTag.TextFormatting.StronglyEmphasized segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_Superscript(
        CALDoc.TextSegment.InlineTag.TextFormatting.Superscript segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_Subscript(
        CALDoc.TextSegment.InlineTag.TextFormatting.Subscript segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Summary(
        CALDoc.TextSegment.InlineTag.Summary segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param segment the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_Code(
        CALDoc.TextSegment.InlineTag.Code segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param list the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_List_Unordered(
        CALDoc.TextSegment.InlineTag.List.Unordered list, T arg) {
        
        SourceModel.verifyArg(list, "list");
        
        final int nItems = list.getNItems();
        for (int i = 0; i < nItems; i++) {
            list.getNthItem(i).accept(this, arg);
        }
        
        return null;
    }
    
    /**
     * @param list the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_List_Ordered(
        CALDoc.TextSegment.InlineTag.List.Ordered list, T arg) {
        
        SourceModel.verifyArg(list, "list");
        
        final int nItems = list.getNItems();
        for (int i = 0; i < nItems; i++) {
            list.getNthItem(i).accept(this, arg);
        }
        
        return null;
    }
    
    /**
     * @param item the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextSegment_InlineTag_List_Item(
        CALDoc.TextSegment.InlineTag.List.Item item, T arg) {
        
        SourceModel.verifyArg(item, "item");
        
        item.getContent().accept(this, arg);
        return null;
    }
    
    /**
     * @param block the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TextBlock(
        CALDoc.TextBlock block, T arg) {
        
        SourceModel.verifyArg(block, "block");
        
        final int nSegments = block.getNSegments();
        for (int i = 0; i < nSegments; i++) {
            block.getNthSegment(i).accept(this, arg);
        }
        
        return null;
    }
    
    /**
     * @param block the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    protected R visit_CALDoc_TaggedBlock_TaggedBlockWithText_Helper(
        CALDoc.TaggedBlock.TaggedBlockWithText block, T arg) {
        
        SourceModel.verifyArg(block, "block");
        
        block.getTextBlock().accept(this, arg);
        return null;
    }

    /**
     * @param authorBlock the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_Author(
        CALDoc.TaggedBlock.Author authorBlock, T arg) {
        
        SourceModel.verifyArg(authorBlock, "authorBlock");
        
        return visit_CALDoc_TaggedBlock_TaggedBlockWithText_Helper(authorBlock, arg);
    }

    /**
     * @param deprecatedBlock the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_Deprecated(
        CALDoc.TaggedBlock.Deprecated deprecatedBlock, T arg) {
        
        SourceModel.verifyArg(deprecatedBlock, "deprecatedBlock");
        
        return visit_CALDoc_TaggedBlock_TaggedBlockWithText_Helper(deprecatedBlock, arg);
    }

    /**
     * @param returnBlock the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_Return(
        CALDoc.TaggedBlock.Return returnBlock, T arg) {
        
        SourceModel.verifyArg(returnBlock, "returnBlock");
        
        return visit_CALDoc_TaggedBlock_TaggedBlockWithText_Helper(returnBlock, arg);
    }

    /**
     * @param versionBlock the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_Version(
        CALDoc.TaggedBlock.Version versionBlock, T arg) {
        
        SourceModel.verifyArg(versionBlock, "versionBlock");
        
        return visit_CALDoc_TaggedBlock_TaggedBlockWithText_Helper(versionBlock, arg);
    }

    /**
     * @param argBlock the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_Arg(
        CALDoc.TaggedBlock.Arg argBlock, T arg) {
        
        SourceModel.verifyArg(argBlock, "argBlock");
        
        // visit the arg name first
        argBlock.getArgName().accept(this, arg);
        // then the rest of the block
        return visit_CALDoc_TaggedBlock_TaggedBlockWithText_Helper(argBlock, arg);
    }
    
    /**
     * @param function the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_See_Function(
        CALDoc.TaggedBlock.See.Function function, T arg) {
        
        SourceModel.verifyArg(function, "function");
        
        final int nFunctionNames = function.getNFunctionNames();
        for (int i = 0; i < nFunctionNames; i++) {
            function.getNthFunctionName(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param module the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_See_Module(
        CALDoc.TaggedBlock.See.Module module, T arg) {
        
        SourceModel.verifyArg(module, "module");
        
        final int nTypeConsNames = module.getNModuleNames();
        for (int i = 0; i < nTypeConsNames; i++) {
            module.getNthModuleName(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param typeCons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_See_TypeCons(
        CALDoc.TaggedBlock.See.TypeCons typeCons, T arg) {
        
        SourceModel.verifyArg(typeCons, "typeCons");
        
        final int nTypeConsNames = typeCons.getNTypeConsNames();
        for (int i = 0; i < nTypeConsNames; i++) {
            typeCons.getNthTypeConsName(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param dataCons the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_See_DataCons(
        CALDoc.TaggedBlock.See.DataCons dataCons, T arg) {
        
        SourceModel.verifyArg(dataCons, "dataCons");
        
        final int nDataConsNames = dataCons.getNDataConsNames();
        for (int i = 0; i < nDataConsNames; i++) {
            dataCons.getNthDataConsName(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param typeClass the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_See_TypeClass(
        CALDoc.TaggedBlock.See.TypeClass typeClass, T arg) {
        
        SourceModel.verifyArg(typeClass, "typeClass");
        
        final int nTypeClassNames = typeClass.getNTypeClassNames();
        for (int i = 0; i < nTypeClassNames; i++) {
            typeClass.getNthTypeClassName(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param reference the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_CrossReference_Function(
        CALDoc.CrossReference.Function reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        reference.getName().accept(this, arg);
        return null;
    }

    /**
     * @param reference the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_CrossReference_Module(
        CALDoc.CrossReference.Module reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");

        reference.getName().accept(this, arg);
        return null;
    }

    /**
     * @param reference the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_CrossReference_TypeCons(
        CALDoc.CrossReference.TypeCons reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        reference.getName().accept(this, arg);
        return null;
    }

    /**
     * @param reference the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_CrossReference_DataCons(
        CALDoc.CrossReference.DataCons reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        reference.getName().accept(this, arg);
        return null;
    }

    /**
     * @param reference the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_CrossReference_TypeClass(
        CALDoc.CrossReference.TypeClass reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        reference.getName().accept(this, arg);
        return null;
    }

    /**
     * @param reference the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_CrossReference_WithoutContextCons(
        CALDoc.CrossReference.WithoutContextCons reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        reference.getName().accept(this, arg);
        return null;
    }

    /**
     * @param seeBlock the source model element to be traversed
     * @param arg unused argument
     * @return null
     */
    public R visit_CALDoc_TaggedBlock_See_WithoutContext(
        CALDoc.TaggedBlock.See.WithoutContext seeBlock, T arg) {
        
        SourceModel.verifyArg(seeBlock, "seeBlock");
        
        final int nReferencedNames = seeBlock.getNReferencedNames();
        for (int i = 0; i < nReferencedNames; i++) {
            seeBlock.getNthReferencedName(i).accept(this, arg);
        }
        
        return null;
    }
}
