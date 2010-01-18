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
 * SourceModelCopier.java
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
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;


/**
 * SourceModelCopier is an implementation of the SourceModelVisitor which
 * performs a deep copy of the source model elements. Each visit method
 * returns as its return value a deep copy of the element it visits.
 * <p>
 * 
 * This class is intended to be the base class of other visitors that need to
 * perform source model transformations, i.e. taking a source model as input,
 * generating a new source model as output. For exasmple, a visitor that needs
 * to change all references of "List.map" to "MyModule.foo" can be written
 * as:
 * <p>
 * 
 * <pre><code>
 * class ReferenceRenamer extends SourceModelCopier&lt;Void&gt; {
 * 
 *     public Expr visit_Expr_Var(
 *         SourceModel.Expr.Var var, Void arg) {
 * 
 *         SourceModel.Name.Function varName = var.getVarName();
 * 
 *         if (varName.getModuleName() != null &&
 *             varName.getModuleName().equals("List") &&
 *             varName.getUnqualifiedName().equals("map")) {
 * 
 *             return SourceModel.Expr.Var.make(
 *                 SourceModel.Name.Function.make(new QualifiedName("MyModule", "foo")));
 * 
 *         } else {
 *             return super.visit_Expr_Var(var, arg);
 *         }
 *     }
 * }
 * </code></pre>
 *
 * To use this transformation, one can simply apply it to a source model's
 * root element:
 * <pre><code>
 * SourceModel.SourceElement transformedElement =
 *     (SourceModel.SourceElement)originalElement.accept(new ReferenceRenamer(), null);
 * </code></pre>
 *
 * Each visit method in every subclass of SourceModelCopier is
 * generally obligated to return an object of the same type as the element being
 * visited. An exception to this rule is that it is usually acceptable that
 * visiting an element in the SourceModel.Expr hierarchy returns an element
 * representing a different kind of expression.
 * <p>
 * 
 * Note that the visitVarExpr method above defaults to a call to the supertype's
 * implementation. This ensures that in the case when no transformations are
 * required, a simple deep copy of the source model element is returned. In
 * general, in any situations where the supertype's implementation is not
 * invoked, it is the responsibility of the subclass' implementation to 1)
 * traverse through any child elements if necessary, and 2) provide the new
 * source model element resulting from the transformation of the element being
 * visited.
 * <p>
 * 
 * In SourceModelCopier, the argument supplied to the visit methods are ignored.
 * Subclasses of SourceModelCopier are free to use the argument for their own
 * purposes, and the traversal logic in SourceModelCopier will properly
 * propagate the supplied arguments down to child elements.
 * <p>
 * 
 * Note: the design of this class mandates that all fields whose type descend
 * from SourceModel.SourceElement must be visited by the visitor. In particular,
 * for array-valued fields, each array element must be individually visited and
 * consequently copied.
 * For example:
 * 
 * <pre><code>
 *   public ArgBindings visit_ArgBindings_Positional(
 *       ArgBindings.Positional argBindings, T arg) {
 *
 *       SourceModel.verifyArg(argBindings, "argBindings");
 *
 *       Pattern[] newPatterns = new Pattern[argBindings.getNPatterns()];
 *       for (int i = 0; i < argBindings.getNPatterns(); i++) {
 *           newPatterns[i] = (Pattern)argBindings.getNthPattern(i).accept(this, arg);
 *       }
 *
 *       return ArgBindings.Positional.make(newPatterns);
 *   }
 * </code></pre>
 * 
 * This usage guideline applies to any modifications to this class, as well
 * as any subclass that aims to preserve the deep-copying semantics.
 * <p>
 * 
 * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
 * 
 * @author Joseph Wong
 */
public class SourceModelCopier<T> implements SourceModelVisitor<T, SourceModel.SourceElement> {

    /**
     * @param application the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Application(
        Expr.Application application, T arg) {

        SourceModel.verifyArg(application, "application");

        Expr[] newExpressions = new Expr[application.getNExpressions()];
        for (int i = 0; i < application.getNExpressions(); i++) {
            newExpressions[i] = (Expr)application.getNthExpression(i).accept(this, arg);
        }

        return Expr.Application.makeAnnotated(newExpressions, application.getSourceRange());
    }

    /**
     * @param parenthesized the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Parenthesized(
        Expr.Parenthesized parenthesized, T arg) {

        SourceModel.verifyArg(parenthesized, "parenthesized");

        return Expr.Parenthesized.makeAnnotated((Expr)parenthesized.getExpression().accept(this, arg), parenthesized.getSourceRange());
    } 
    
    /** {@inheritDoc}*/
    public TypeExprDefn visit_TypeExprDefn_Parenthesized(
        TypeExprDefn.Parenthesized parenthesized, T arg) {
        
        return TypeExprDefn.Parenthesized.makeAnnotated((TypeExprDefn)parenthesized.getTypeExprDefn().accept(this, arg), parenthesized.getSourceRange());
    }
    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_DataCons(
        Expr.DataCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        return Expr.DataCons.makeAnnotated(
            (Name.DataCons)cons.getDataConsName().accept(this, arg), cons.getSourceRange());
    }

    /**
     * @param var the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Var(
        Expr.Var var, T arg) {

        SourceModel.verifyArg(var, "var");

        return Expr.Var.makeAnnotated(
            (Name.Function)var.getVarName().accept(this, arg), var.getSourceRange());
    }

    /**
     * @param let the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Let(
        Expr.Let let, T arg) {

        SourceModel.verifyArg(let, "let");

        LocalDefn[] newLocalFunctions = new LocalDefn[let.getNLocalDefinitions()];
        for (int i = 0; i < let.getNLocalDefinitions(); i++) {
            newLocalFunctions[i] = (LocalDefn)let.getNthLocalDefinition(i).accept(this, arg);
        }

        return Expr.Let.make(
            newLocalFunctions,
            (Expr)let.getInExpr().accept(this, arg));
    }

    /**
     * @param caseExpr the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Case(
        Expr.Case caseExpr, T arg) {

        SourceModel.verifyArg(caseExpr, "caseExpr");

        Expr.Case.Alt[] newCaseAlts = new Expr.Case.Alt[caseExpr.getNCaseAlts()];
        for (int i = 0; i < caseExpr.getNCaseAlts(); i++) {
            newCaseAlts[i] = (Expr.Case.Alt)caseExpr.getNthCaseAlt(i).accept(this, arg);
        }

        return Expr.Case.makeAnnotated(
            (Expr)caseExpr.getConditionExpr().accept(this, arg),
            newCaseAlts, 
            caseExpr.getSourceRange());
    }

    /**
     * @param defaultAlt the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_Default(
        Expr.Case.Alt.Default defaultAlt, T arg) {

        SourceModel.verifyArg(defaultAlt, "defaultAlt");

        return Expr.Case.Alt.Default.makeAnnotated(
            (Expr)defaultAlt.getAltExpr().accept(this, arg), defaultAlt.getSourceRange());
    }

    /**
     * @param tuple the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackTuple(
        Expr.Case.Alt.UnpackTuple tuple, T arg) {

        SourceModel.verifyArg(tuple, "tuple");

        Pattern[] newPatterns = new Pattern[tuple.getNPatterns()];
        for (int i = 0; i < tuple.getNPatterns(); i++) {
            newPatterns[i] = (Pattern)tuple.getNthPattern(i).accept(this, arg);
        }

        return Expr.Case.Alt.UnpackTuple.make(
            newPatterns,
            (Expr)tuple.getAltExpr().accept(this, arg));
    }

    /**
     * @param unit the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackUnit(
        Expr.Case.Alt.UnpackUnit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        return Expr.Case.Alt.UnpackUnit.makeAnnotated(
            (Expr)unit.getAltExpr().accept(this, arg), unit.getSourceRange());
    }

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackDataCons(
            Expr.Case.Alt.UnpackDataCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        int nDataConsNames = cons.getNDataConsNames();
        Name.DataCons[] newDataConsNameArray = new Name.DataCons[nDataConsNames];
        for (int i = 0; i < nDataConsNames; i++) {
            newDataConsNameArray[i] = (Name.DataCons)cons.getNthDataConsName(i).accept(this, arg);
        }
        
        return Expr.Case.Alt.UnpackDataCons.makeAnnotated(
            newDataConsNameArray,
            (ArgBindings)cons.getArgBindings().accept(this,arg),
            (Expr)cons.getAltExpr().accept(this, arg), cons.getSourceRange(), cons.getParenthesized());
    }
    
    /**
     * @param argBindings the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public ArgBindings visit_ArgBindings_Matching(
        ArgBindings.Matching argBindings, T arg) {

        SourceModel.verifyArg(argBindings, "argBindings");

        FieldPattern[] newFieldPatterns =
            new FieldPattern[argBindings.getNFieldPatterns()];

        for (int i = 0; i < argBindings.getNFieldPatterns(); i++) {
            newFieldPatterns[i] =
                (FieldPattern)argBindings.getNthFieldPattern(i).accept(this, arg);
        }

        return ArgBindings.Matching.make(newFieldPatterns);
    }
    
    /**
     * @param argBindings the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public ArgBindings visit_ArgBindings_Positional(
        ArgBindings.Positional argBindings, T arg) {

        SourceModel.verifyArg(argBindings, "argBindings");

        Pattern[] newPatterns = new Pattern[argBindings.getNPatterns()];
        for (int i = 0; i < argBindings.getNPatterns(); i++) {
            newPatterns[i] = (Pattern)argBindings.getNthPattern(i).accept(this, arg);
        }

        return ArgBindings.Positional.make(newPatterns);
    }

    /**
     * @param intAlt the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackInt(
        Expr.Case.Alt.UnpackInt intAlt, T arg) {

        SourceModel.verifyArg(intAlt, "intAlt");

        return Expr.Case.Alt.UnpackInt.make(
            intAlt.getIntValues(),
            (Expr)intAlt.getAltExpr().accept(this, arg));
    }
    
    /**
     * @param charAlt the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackChar(
        Expr.Case.Alt.UnpackChar charAlt, T arg) {

        SourceModel.verifyArg(charAlt, "charAlt");

        return Expr.Case.Alt.UnpackChar.make(
            charAlt.getCharValues(),
            (Expr)charAlt.getAltExpr().accept(this, arg));
    }
    
    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackListCons(
        Expr.Case.Alt.UnpackListCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        return Expr.Case.Alt.UnpackListCons.makeAnnotated(
            (Pattern)cons.getHeadPattern().accept(this, arg),
            (Pattern)cons.getTailPattern().accept(this, arg),
            (Expr)cons.getAltExpr().accept(this, arg),
            cons.getSourceRange(),
            cons.getOperatorSourceRange());
    }

    /**
     * @param nil the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackListNil(
        Expr.Case.Alt.UnpackListNil nil, T arg) {

        SourceModel.verifyArg(nil, "nil");

        return Expr.Case.Alt.UnpackListNil.makeAnnotated(
            (Expr)nil.getAltExpr().accept(this, arg),
            nil.getSourceRange());
    }

    /**
     * @param record the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackRecord(
        Expr.Case.Alt.UnpackRecord record, T arg) {

        SourceModel.verifyArg(record, "record");

        FieldPattern[] newFieldPatterns =
            new FieldPattern[record.getNFieldPatterns()];

        for (int i = 0; i < record.getNFieldPatterns(); i++) {
            newFieldPatterns[i] =
                (FieldPattern)record.getNthFieldPattern(i).accept(this, arg);
        }

        return Expr.Case.Alt.UnpackRecord.make(
            (record.getBaseRecordPattern() == null)
                ? null
                : (Pattern)record.getBaseRecordPattern().accept(this, arg),
            newFieldPatterns,
            (Expr)record.getAltExpr().accept(this, arg));
    }

    /**
     * @param pattern the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public FieldPattern visit_FieldPattern(
        FieldPattern pattern, T arg) {

        SourceModel.verifyArg(pattern, "pattern");

        return FieldPattern.makeAnnotated(
            (Name.Field)pattern.getFieldName().accept(this, arg),
            (pattern.getPattern() == null)
                ? null
                : (Pattern)pattern.getPattern().accept(this, arg));
    }

    /**
     * @param lambda the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Lambda(
        Expr.Lambda lambda, T arg) {

        SourceModel.verifyArg(lambda, "lambda");

        Parameter[] newParameters = new Parameter[lambda.getNParameters()];
        for (int i = 0; i < lambda.getNParameters(); i++) {
            newParameters[i] = (Parameter)lambda.getNthParameter(i).accept(this, arg);
        }

        return Expr.Lambda.makeAnnotated(
            newParameters,
            (Expr)lambda.getDefiningExpr().accept(this, arg),
            lambda.getSourceRange());
    }

    /**
     * @param ifExpr the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_If(
        Expr.If ifExpr, T arg) {

        SourceModel.verifyArg(ifExpr, "ifExpr");

        return Expr.If.makeAnnotated(
            (Expr)ifExpr.getConditionExpr().accept(this, arg),
            (Expr)ifExpr.getThenExpr().accept(this, arg),
            (Expr)ifExpr.getElseExpr().accept(this, arg),
            ifExpr.getSourceRange());
    }

    /**
     * @param num the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Literal_Num(
        Expr.Literal.Num num, T arg) {

        SourceModel.verifyArg(num, "num");

        return Expr.Literal.Num.makeAnnotated(num.getNumValue(), num.getSourceRange());
    }

    /**
     * @param doubleLiteral the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Literal_Double(
        Expr.Literal.Double doubleLiteral, T arg) {

        SourceModel.verifyArg(doubleLiteral, "doubleLiteral");

        return Expr.Literal.Double.makeAnnotated(doubleLiteral.getDoubleValue(), doubleLiteral.getSourceRange());
    }
    
    /**
     * @param floatLiteral the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Literal_Float(
            Expr.Literal.Float floatLiteral, T arg) {
        SourceModel.verifyArg(floatLiteral, "floatLiteral");
        
        return Expr.Literal.Float.makeAnnotated(floatLiteral.getFloatValue(), floatLiteral.getSourceRange());
    }
    
    /**
     * @param charLiteral the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Literal_Char(
        Expr.Literal.Char charLiteral, T arg) {

        SourceModel.verifyArg(charLiteral, "charLiteral");

        return Expr.Literal.Char.makeAnnotated(charLiteral.getCharValue(), charLiteral.getSourceRange());
    }

    /**
     * @param string the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Literal_StringLit(
        Expr.Literal.StringLit string, T arg) {

        SourceModel.verifyArg(string, "string");

        return Expr.Literal.StringLit.makeAnnotated(string.getStringValue(), string.getSourceRange());
    }

    /**
     * @param negate the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_UnaryOp_Negate(
        Expr.UnaryOp.Negate negate, T arg) {

        SourceModel.verifyArg(negate, "negate");

        return Expr.UnaryOp.Negate.makeAnnotated(
            (Expr)negate.getExpr().accept(this, arg),
            negate.getSourceRange(),
            negate.getOperatorSourceRange());
    }

    /**
     * @param backquotedOperator the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_BackquotedOperator_Var(
        Expr.BinaryOp.BackquotedOperator.Var backquotedOperator, T arg) {

        SourceModel.verifyArg(backquotedOperator, "backquotedOperator");

        return Expr.BinaryOp.BackquotedOperator.Var.makeAnnotated(
                (Expr.Var)backquotedOperator.getOperatorVarExpr().accept(this, arg),
                (Expr)backquotedOperator.getLeftExpr().accept(this, arg),
                (Expr)backquotedOperator.getRightExpr().accept(this, arg),
                backquotedOperator.getSourceRange(),
                backquotedOperator.getOperatorSourceRange());
    }

    /**
     * @param backquotedOperator the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_BackquotedOperator_DataCons(
        Expr.BinaryOp.BackquotedOperator.DataCons backquotedOperator, T arg) {

        SourceModel.verifyArg(backquotedOperator, "backquotedOperator");

        return Expr.BinaryOp.BackquotedOperator.DataCons.makeAnnotated(
                (Expr.DataCons)backquotedOperator.getOperatorDataConsExpr().accept(this,arg),
                (Expr)backquotedOperator.getLeftExpr().accept(this, arg),
                (Expr)backquotedOperator.getRightExpr().accept(this, arg),
                backquotedOperator.getSourceRange(),
                backquotedOperator.getOperatorSourceRange());
    }

    /**
     * @param and the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_And(
        Expr.BinaryOp.And and, T arg) {

        SourceModel.verifyArg(and, "and");

        return Expr.BinaryOp.And.makeAnnotated(
            (Expr)and.getLeftExpr().accept(this, arg),
            (Expr)and.getRightExpr().accept(this, arg),
            and.getSourceRange(),
            and.getOperatorSourceRange());

    }

    /**
     * @param or the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Or(
        Expr.BinaryOp.Or or, T arg) {

        SourceModel.verifyArg(or, "or");

        return Expr.BinaryOp.Or.makeAnnotated(
            (Expr)or.getLeftExpr().accept(this, arg),
            (Expr)or.getRightExpr().accept(this, arg),
            or.getSourceRange(),
            or.getOperatorSourceRange());
    }

    /**
     * @param equals the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Equals(
        Expr.BinaryOp.Equals equals, T arg) {

        SourceModel.verifyArg(equals, "equals");

        return Expr.BinaryOp.Equals.makeAnnotated(
                (Expr)equals.getLeftExpr().accept(this, arg),
                (Expr)equals.getRightExpr().accept(this, arg),
                equals.getSourceRange(),
                equals.getOperatorSourceRange());
    }

    /**
     * @param notEquals the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_NotEquals(
        Expr.BinaryOp.NotEquals notEquals, T arg) {

        SourceModel.verifyArg(notEquals, "notEquals");

        return Expr.BinaryOp.NotEquals.makeAnnotated(
                (Expr)notEquals.getLeftExpr().accept(this, arg),
                (Expr)notEquals.getRightExpr().accept(this, arg),
                notEquals.getSourceRange(),
                notEquals.getOperatorSourceRange());
    }

    /**
     * @param lt the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_LessThan(
        Expr.BinaryOp.LessThan lt, T arg) {

        SourceModel.verifyArg(lt, "lt");

        return Expr.BinaryOp.LessThan.makeAnnotated(
                (Expr)lt.getLeftExpr().accept(this, arg),
                (Expr)lt.getRightExpr().accept(this, arg),
                lt.getSourceRange(),
                lt.getOperatorSourceRange());
    }

    /**
     * @param lteq the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_LessThanEquals(
        Expr.BinaryOp.LessThanEquals lteq, T arg) {

        SourceModel.verifyArg(lteq, "lteq");

        return Expr.BinaryOp.LessThanEquals.makeAnnotated(
                (Expr)lteq.getLeftExpr().accept(this, arg),
                (Expr)lteq.getRightExpr().accept(this, arg),
                lteq.getSourceRange(),
                lteq.getOperatorSourceRange());
    }

    /**
     * @param gteq the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_GreaterThanEquals(
        Expr.BinaryOp.GreaterThanEquals gteq, T arg) {

        SourceModel.verifyArg(gteq, "gteq");

        return Expr.BinaryOp.GreaterThanEquals.makeAnnotated(
                (Expr)gteq.getLeftExpr().accept(this, arg),
                (Expr)gteq.getRightExpr().accept(this, arg),
                gteq.getSourceRange(),
                gteq.getOperatorSourceRange());
    }

    /**
     * @param gt the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_GreaterThan(
        Expr.BinaryOp.GreaterThan gt, T arg) {

        SourceModel.verifyArg(gt, "gt");

        return Expr.BinaryOp.GreaterThan.makeAnnotated(
                (Expr)gt.getLeftExpr().accept(this, arg),
                (Expr)gt.getRightExpr().accept(this, arg),
                gt.getSourceRange(),
                gt.getOperatorSourceRange());
    }

    /**
     * @param add the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Add(
        Expr.BinaryOp.Add add, T arg) {

        SourceModel.verifyArg(add, "add");

        return Expr.BinaryOp.Add.makeAnnotated(
                (Expr)add.getLeftExpr().accept(this, arg),
                (Expr)add.getRightExpr().accept(this, arg),
                add.getSourceRange(),
                add.getOperatorSourceRange());
    }

    /**
     * @param subtract the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Subtract(
        Expr.BinaryOp.Subtract subtract, T arg) {

        SourceModel.verifyArg(subtract, "subtract");

        return Expr.BinaryOp.Subtract.makeAnnotated(
                (Expr)subtract.getLeftExpr().accept(this, arg),
                (Expr)subtract.getRightExpr().accept(this, arg),
                subtract.getSourceRange(),
                subtract.getOperatorSourceRange());
    }

    /**
     * @param multiply the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Multiply(
        Expr.BinaryOp.Multiply multiply, T arg) {

        SourceModel.verifyArg(multiply, "multiply");

        return Expr.BinaryOp.Multiply.makeAnnotated(
                (Expr)multiply.getLeftExpr().accept(this, arg),
                (Expr)multiply.getRightExpr().accept(this, arg),
                multiply.getSourceRange(),
                multiply.getOperatorSourceRange());
    }

    /**
     * @param divide the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Divide(
        Expr.BinaryOp.Divide divide, T arg) {

        SourceModel.verifyArg(divide, "divide");

        return Expr.BinaryOp.Divide.makeAnnotated(
                (Expr)divide.getLeftExpr().accept(this, arg),
                (Expr)divide.getRightExpr().accept(this, arg),
                divide.getSourceRange(),
                divide.getOperatorSourceRange());
    }
    
    /**
     * @param remainder the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Remainder(
        Expr.BinaryOp.Remainder remainder, T arg) {

        SourceModel.verifyArg(remainder, "remainder");

        return Expr.BinaryOp.Remainder.makeAnnotated(
                (Expr)remainder.getLeftExpr().accept(this, arg),
                (Expr)remainder.getRightExpr().accept(this, arg),
                remainder.getSourceRange(),
                remainder.getOperatorSourceRange());
    }    

    /**
     * @param compose the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Compose(
        Expr.BinaryOp.Compose compose, T arg) {

        SourceModel.verifyArg(compose, "compose");

        return Expr.BinaryOp.Compose.makeAnnotated(
                (Expr)compose.getLeftExpr().accept(this, arg),
                (Expr)compose.getRightExpr().accept(this, arg),
                compose.getSourceRange(),
                compose.getOperatorSourceRange());
    }

    /**
     * @param apply the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Apply(
        Expr.BinaryOp.Apply apply, T arg) {

        SourceModel.verifyArg(apply, "apply");

        return Expr.BinaryOp.Apply.makeAnnotated(
                (Expr)apply.getLeftExpr().accept(this, arg),
                (Expr)apply.getRightExpr().accept(this, arg),
                apply.getSourceRange(),
                apply.getOperatorSourceRange());
    }

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Cons(
        Expr.BinaryOp.Cons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        return Expr.BinaryOp.Cons.makeAnnotated(
                (Expr)cons.getLeftExpr().accept(this, arg),
                (Expr)cons.getRightExpr().accept(this, arg),
                cons.getSourceRange(),
                cons.getOperatorSourceRange());
    }

    /**
     * @param append the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_BinaryOp_Append(
        Expr.BinaryOp.Append append, T arg) {

        SourceModel.verifyArg(append, "append");

        return Expr.BinaryOp.Append.makeAnnotated(
                (Expr)append.getLeftExpr().accept(this, arg),
                (Expr)append.getRightExpr().accept(this, arg),
                append.getSourceRange(),
                append.getOperatorSourceRange());
    }

    /**
     * @param unit the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Unit(
        Expr.Unit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        return Expr.Unit.makeAnnotated(unit.getSourceRange());
    }

    /**
     * @param tuple the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Tuple(
        Expr.Tuple tuple, T arg) {

        SourceModel.verifyArg(tuple, "tuple");

        Expr[] newComponents = new Expr[tuple.getNComponents()];
        for (int i = 0; i < tuple.getNComponents(); i++) {
            newComponents[i] = (Expr)tuple.getNthComponent(i).accept(this, arg);
        }

        return Expr.Tuple.makeAnnotated(newComponents, tuple.getSourceRange());
    }

    /**
     * @param list the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_List(
        Expr.List list, T arg) {

        SourceModel.verifyArg(list, "list");

        Expr[] newElements = new Expr[list.getNElements()];
        for (int i = 0; i < list.getNElements(); i++) {
            newElements[i] = (Expr)list.getNthElement(i).accept(this, arg);
        }

        return Expr.List.makeAnnotated(newElements, list.getSourceRange());
    }

    /**
     * @param record the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_Record(
        Expr.Record record, T arg) {

        SourceModel.verifyArg(record, "record");

        Expr.Record.FieldModification[] newExtensionFields =
            new Expr.Record.FieldModification[record.getNExtensionFields()];

        for (int i = 0; i < record.getNExtensionFields(); i++) {
            newExtensionFields[i] =
                (Expr.Record.FieldModification)record.getNthExtensionField(i).accept(this, arg);
        }

        return Expr.Record.make(
            (record.getBaseRecordExpr() == null)
                ? null
                : (Expr)record.getBaseRecordExpr().accept(this, arg),
            newExtensionFields);
    }

    /**
     * @param fieldExtension the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Record.FieldModification visit_Expr_Record_FieldModification_Extension(
        Expr.Record.FieldModification.Extension fieldExtension, T arg) {

        SourceModel.verifyArg(fieldExtension, "fieldExtension");

        return Expr.Record.FieldModification.Extension.make(
            (Name.Field)fieldExtension.getFieldName().accept(this, arg),
            (Expr)fieldExtension.getValueExpr().accept(this, arg));
    }
    
    /**
     * @param fieldValueUpdate the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr.Record.FieldModification visit_Expr_Record_FieldModification_Update(
        Expr.Record.FieldModification.Update fieldValueUpdate, T arg) {

        SourceModel.verifyArg(fieldValueUpdate, "fieldValueUpdate");

        return Expr.Record.FieldModification.Update.make(
            (Name.Field)fieldValueUpdate.getFieldName().accept(this, arg),
            (Expr)fieldValueUpdate.getValueExpr().accept(this, arg));
    }    

    /**
     * @param field the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_SelectDataConsField(
        Expr.SelectDataConsField field, T arg) {

        SourceModel.verifyArg(field, "field");

        return Expr.SelectDataConsField.makeAnnotated(
            (Expr)field.getDataConsValuedExpr().accept(this, arg),
            (Name.DataCons)field.getDataConsName().accept(this, arg),
            (Name.Field)field.getFieldName().accept(this, arg),
            field.getSourceRange());
    }

    /**
     * @param field the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_SelectRecordField(
        Expr.SelectRecordField field, T arg) {

        SourceModel.verifyArg(field, "field");

        return Expr.SelectRecordField.make(
            (Expr)field.getRecordValuedExpr().accept(this, arg),
            (Name.Field)field.getFieldName().accept(this, arg));
    }

    /**
     * @param signature the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Expr visit_Expr_ExprTypeSignature(
        Expr.ExprTypeSignature signature, T arg) {

        SourceModel.verifyArg(signature, "signature");

        return Expr.ExprTypeSignature.make(
            (Expr)signature.getExpr().accept(this, arg),
            (TypeSignature)signature.getTypeSignature().accept(this, arg));
    }

    /**
     * @param lacks the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Constraint visit_Constraint_Lacks(
        Constraint.Lacks lacks, T arg) {

        SourceModel.verifyArg(lacks, "lacks");

        return Constraint.Lacks.makeAnnotated(
            (Name.TypeVar)lacks.getTypeVarName().accept(this, arg),
            (Name.Field)lacks.getLacksField().accept(this, arg),
            lacks.getSourceRange());
    }

    /**
     * @param typeClass the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Constraint visit_Constraint_TypeClass(
        Constraint.TypeClass typeClass, T arg) {

        SourceModel.verifyArg(typeClass, "typeClass");

        return Constraint.TypeClass.makeAnnotated(
            (Name.TypeClass)typeClass.getTypeClassName().accept(this, arg),
            (Name.TypeVar)typeClass.getTypeVarName().accept(this, arg),
            typeClass.getSourceRange());
    }

    /**
     * @param importStmt the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Import visit_Import(
        Import importStmt, T arg) {

        SourceModel.verifyArg(importStmt, "importStmt");

        Import.UsingItem[] usingItems = importStmt.getUsingItems();
        Import.UsingItem[] newUsingItems = new Import.UsingItem[usingItems.length];
        
        for(int i = 0, nItems = usingItems.length; i < nItems; i++) {
            newUsingItems[i] = (Import.UsingItem)usingItems[i].accept(this, arg);
        }
        
        return Import.makeAnnotated(
            (Name.Module)importStmt.getImportedModuleName().accept(this, arg),
            newUsingItems,
            importStmt.getSourceRange());
    }
    
    /**
     * @param usingItemFunction the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Import.UsingItem visit_Import_UsingItem_Function(
        Import.UsingItem.Function usingItemFunction, T arg) {
        
        SourceModel.verifyArg(usingItemFunction, "usingItemFunction");
        
        return Import.UsingItem.Function.makeAnnotated(
            usingItemFunction.getUsingNames(),
            usingItemFunction.getSourceRange(),
            usingItemFunction.getUsingNameSourceRanges());
    }
    
    /**
     * @param usingItemDataConstructor the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Import.UsingItem visit_Import_UsingItem_DataConstructor(
        Import.UsingItem.DataConstructor usingItemDataConstructor, T arg) {
        
        SourceModel.verifyArg(usingItemDataConstructor, "usingItemDataConstructor");
        
        return Import.UsingItem.DataConstructor.makeAnnotated(
            usingItemDataConstructor.getUsingNames(),
            usingItemDataConstructor.getSourceRange(),
            usingItemDataConstructor.getUsingNameSourceRanges());
    }
    
    /**
     * @param usingItemTypeConstructor the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Import.UsingItem visit_Import_UsingItem_TypeConstructor(
        Import.UsingItem.TypeConstructor usingItemTypeConstructor, T arg) {
        
        SourceModel.verifyArg(usingItemTypeConstructor, "usingItemTypeConstructor");
        
        return Import.UsingItem.TypeConstructor.makeAnnotated(
            usingItemTypeConstructor.getUsingNames(),
            usingItemTypeConstructor.getSourceRange(),
            usingItemTypeConstructor.getUsingNameSourceRanges());
    }
    
    /**
     * @param usingItemTypeClass the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Import.UsingItem visit_Import_UsingItem_TypeClass(
        Import.UsingItem.TypeClass usingItemTypeClass, T arg) {
        
        SourceModel.verifyArg(usingItemTypeClass, "usingItemTypeClass");
        
        return Import.UsingItem.TypeClass.makeAnnotated(
            usingItemTypeClass.getUsingNames(),
            usingItemTypeClass.getSourceRange(),
            usingItemTypeClass.getUsingNameSourceRanges());
    }
    
    /**
     * @param friendDeclaration the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Friend visit_Friend(
        Friend friendDeclaration, T arg) {

        SourceModel.verifyArg(friendDeclaration, "friendDeclaration");

        return Friend.makeAnnotated(
            (Name.Module)friendDeclaration.getFriendModuleName().accept(this, arg), friendDeclaration.getSourceRange());
    }    

    /**
     * @param function the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public LocalDefn visit_LocalDefn_Function_Definition(
        LocalDefn.Function.Definition function, T arg) {

        SourceModel.verifyArg(function, "function");
        
        CALDoc.Comment.Function newCALDocComment = null;
        if (function.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Function)function.getCALDocComment().accept(this, arg);
        }

        Parameter[] newParameters = new Parameter[function.getNParameters()];
        for (int i = 0; i < function.getNParameters(); i++) {
            newParameters[i] = (Parameter)function.getNthParameter(i).accept(this, arg);
        }

        return LocalDefn.Function.Definition.makeAnnotated(
            newCALDocComment,
            function.getName(),
            function.getNameSourceRange(),
            newParameters,
            (Expr)function.getDefiningExpr().accept(this, arg),
            function.getSourceRange(),
            function.getSourceRangeExcludingCaldoc());
    }

    /**
     * @param declaration the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public LocalDefn visit_LocalDefn_Function_TypeDeclaration(
        LocalDefn.Function.TypeDeclaration declaration, T arg) {

        SourceModel.verifyArg(declaration, "declaration");
        
        CALDoc.Comment.Function newCALDocComment = null;
        if (declaration.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Function)declaration.getCALDocComment().accept(this, arg);
        }

        return LocalDefn.Function.TypeDeclaration.makeAnnotated(
            newCALDocComment,
            declaration.getName(),
            declaration.getNameSourceRange(),
            (TypeSignature)declaration.getDeclaredType().accept(this, arg),
            declaration.getSourceRange());
    }

    /**
     * @param unpackDataCons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public LocalDefn visit_LocalDefn_PatternMatch_UnpackDataCons(
        LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, T arg) {

        SourceModel.verifyArg(unpackDataCons, "unpackDataCons");

        return LocalDefn.PatternMatch.UnpackDataCons.makeAnnotated(
            (Name.DataCons)unpackDataCons.getDataConsName().accept(this, arg),
            (ArgBindings)unpackDataCons.getArgBindings().accept(this, arg),
            (Expr)unpackDataCons.getDefiningExpr().accept(this, arg),
            unpackDataCons.getSourceRange());
    }

    /**
     * @param unpackListCons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public LocalDefn visit_LocalDefn_PatternMatch_UnpackListCons(
        LocalDefn.PatternMatch.UnpackListCons unpackListCons, T arg) {

        SourceModel.verifyArg(unpackListCons, "unpackListCons");

        return LocalDefn.PatternMatch.UnpackListCons.makeAnnotated(
            (Pattern)unpackListCons.getHeadPattern().accept(this, arg),
            (Pattern)unpackListCons.getTailPattern().accept(this, arg),
            (Expr)unpackListCons.getDefiningExpr().accept(this, arg),
            unpackListCons.getSourceRange(),
            unpackListCons.getOperatorSourceRange());
    }

    /**
     * @param unpackRecord the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public LocalDefn visit_LocalDefn_PatternMatch_UnpackRecord(
        LocalDefn.PatternMatch.UnpackRecord unpackRecord, T arg) {

        SourceModel.verifyArg(unpackRecord, "unpackRecord");

        FieldPattern[] newFieldPatterns =
            new FieldPattern[unpackRecord.getNFieldPatterns()];

        for (int i = 0; i < unpackRecord.getNFieldPatterns(); i++) {
            newFieldPatterns[i] =
                (FieldPattern)unpackRecord.getNthFieldPattern(i).accept(this, arg);
        }

        return LocalDefn.PatternMatch.UnpackRecord.makeAnnotated(
            (unpackRecord.getBaseRecordPattern() == null)
                ? null
                : (Pattern)unpackRecord.getBaseRecordPattern().accept(this, arg),
            newFieldPatterns,
            (Expr)unpackRecord.getDefiningExpr().accept(this, arg),
            unpackRecord.getSourceRange());
    }

    /**
     * @param unpackTuple the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public LocalDefn visit_LocalDefn_PatternMatch_UnpackTuple(
        LocalDefn.PatternMatch.UnpackTuple unpackTuple, T arg) {

        SourceModel.verifyArg(unpackTuple, "unpackTuple");

        Pattern[] newPatterns = new Pattern[unpackTuple.getNPatterns()];
        for (int i = 0; i < unpackTuple.getNPatterns(); i++) {
            newPatterns[i] = (Pattern)unpackTuple.getNthPattern(i).accept(this, arg);
        }

        return LocalDefn.PatternMatch.UnpackTuple.makeAnnotated(
            newPatterns,
            (Expr)unpackTuple.getDefiningExpr().accept(this, arg),
            unpackTuple.getSourceRange());
    }

    /**
     * @param defn the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public ModuleDefn visit_ModuleDefn(
        ModuleDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        CALDoc.Comment.Module newCALDocComment = null;
        if (defn.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Module)defn.getCALDocComment().accept(this, arg);
        }

        final int nImportedModules = defn.getNImportedModules();
        Import[] newImportedModules = new Import[nImportedModules];
        for (int i = 0; i < nImportedModules; i++) {
            newImportedModules[i] = (Import)defn.getNthImportedModule(i).accept(this, arg);
        }

        final int nFriendModules = defn.getNFriendModules();
        Friend[] newFriendModules = new Friend[nFriendModules];
        for (int i = 0; i < nFriendModules; i++) {
            newFriendModules[i] = (Friend)defn.getNthFriendModule(i).accept(this, arg);
        }

        final int nTopLevelDefns = defn.getNTopLevelDefns();
        TopLevelSourceElement[] newTopLevelDefns = new TopLevelSourceElement[nTopLevelDefns];
        for (int i = 0; i < nTopLevelDefns; i++) {
            newTopLevelDefns[i] = (TopLevelSourceElement)defn.getNthTopLevelDefn(i).accept(this, arg);
        }

        return ModuleDefn.makeAnnotated(
                newCALDocComment, 
                (Name.Module)defn.getModuleName().accept(this, arg), 
                newImportedModules, 
                newFriendModules, 
                newTopLevelDefns, 
                defn.getSourceRange());
    }

    /**
     * @param moduleName the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.Module visit_Name_Module(
        Name.Module moduleName, T arg) {

        SourceModel.verifyArg(moduleName, "moduleName");

        return Name.Module.makeAnnotated(
            (Name.Module.Qualifier)moduleName.getQualifier().accept(this, arg),
            moduleName.getUnqualifiedModuleName(),
            moduleName.getSourceRange());
    }

    /**
     * @param qualifier the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.Module.Qualifier visit_Name_Module_Qualifier(
        Name.Module.Qualifier qualifier, T arg) {

        SourceModel.verifyArg(qualifier, "qualifier");

        return Name.Module.Qualifier.makeAnnotated(qualifier.getComponents(), qualifier.getSourceRange());
    }

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.DataCons visit_Name_DataCons(
        Name.DataCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        Name.Module newModuleName = null;
        if (cons.getModuleName() != null) {
            newModuleName = (Name.Module)cons.getModuleName().accept(this, arg);
        }
        
        return Name.DataCons.makeAnnotated(
            newModuleName,
            cons.getUnqualifiedName(),
            cons.getSourceRange());
    }

    /**
     * @param function the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.Function visit_Name_Function(
        Name.Function function, T arg) {

        SourceModel.verifyArg(function, "function");

        Name.Module newModuleName = null;
        if (function.getModuleName() != null) {
            newModuleName = (Name.Module)function.getModuleName().accept(this, arg);
        }
        
        return Name.Function.makeAnnotated(
            newModuleName,
            function.getUnqualifiedName(),
            function.getSourceRange(),
            function.getUnqualifiedNameSourceRange());
    }

    /**
     * @param typeClass the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.TypeClass visit_Name_TypeClass(
        Name.TypeClass typeClass, T arg) {

        SourceModel.verifyArg(typeClass, "typeClass");

        Name.Module newModuleName = null;
        if (typeClass.getModuleName() != null) {
            newModuleName = (Name.Module)typeClass.getModuleName().accept(this, arg);
        }
        
        return Name.TypeClass.makeAnnotated(
            newModuleName,
            typeClass.getUnqualifiedName(),
            typeClass.getSourceRange());
    }

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.TypeCons visit_Name_TypeCons(
        Name.TypeCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        Name.Module newModuleName = null;
        if (cons.getModuleName() != null) {
            newModuleName = (Name.Module)cons.getModuleName().accept(this, arg);
        }
        
        return Name.TypeCons.makeAnnotated(
            newModuleName,
            cons.getUnqualifiedName(),
            cons.getSourceRange());
    }

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.WithoutContextCons visit_Name_WithoutContextCons(
        Name.WithoutContextCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        Name.Module newModuleName = null;
        if (cons.getModuleName() != null) {
            newModuleName = (Name.Module)cons.getModuleName().accept(this, arg);
        }
        
        return Name.WithoutContextCons.makeAnnotated(
            newModuleName,
            cons.getUnqualifiedName(),
            cons.getSourceRange());
    }

    /**
     * @param name the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.Field visit_Name_Field(
        Name.Field name, T arg) {

        SourceModel.verifyArg(name, "name");

        return Name.Field.makeAnnotated(
            name.getName(),
            name.getSourceRange());
    }

    /**
     * @param name the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Name.TypeVar visit_Name_TypeVar(
        Name.TypeVar name, T arg) {

        SourceModel.verifyArg(name, "name");

        return Name.TypeVar.makeAnnotated(
            name.getName(),
            name.getSourceRange());
    }

    /**
     * @param parameter the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Parameter visit_Parameter(
        Parameter parameter, T arg) {

        SourceModel.verifyArg(parameter, "parameter");

        return Parameter.makeAnnotated(parameter.getName(), parameter.isStrict(), parameter.getSourceRange());
    }

    /**
     * @param var the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Pattern visit_Pattern_Var(
        Pattern.Var var, T arg) {

        SourceModel.verifyArg(var, "var");

        return Pattern.Var.makeAnnotated(var.getName(), var.getSourceRange());
    }

    /**
     * @param wildcard the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public Pattern visit_Pattern_Wildcard(
        Pattern.Wildcard wildcard, T arg) {

        SourceModel.verifyArg(wildcard, "wildcard");

        return Pattern.Wildcard.make();
    }

    /**
     * @param algebraic the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public FunctionDefn visit_FunctionDefn_Algebraic(
        FunctionDefn.Algebraic algebraic, T arg) {

        SourceModel.verifyArg(algebraic, "algebraic");
        
        CALDoc.Comment.Function newCALDocComment = null;
        if (algebraic.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Function)algebraic.getCALDocComment().accept(this, arg);
        }

        Parameter[] newParameters = new Parameter[algebraic.getNParameters()];
        for (int i = 0; i < algebraic.getNParameters(); i++) {
            newParameters[i] = (Parameter)algebraic.getNthParameter(i).accept(this, arg);
        }

        return FunctionDefn.Algebraic.makeAnnotated(
            newCALDocComment,
            algebraic.getName(),
            algebraic.getScope(),
            algebraic.isScopeExplicitlySpecified(),
            newParameters,
            (Expr)algebraic.getDefiningExpr().accept(this, arg),
            algebraic.getSourceRange(),
            algebraic.getSourceRangeExcludingCaldoc(),
            algebraic.getNameSourceRange());
    }

    /**
     * @param foreign the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public FunctionDefn visit_FunctionDefn_Foreign(
        FunctionDefn.Foreign foreign, T arg) {

        SourceModel.verifyArg(foreign, "foreign");

        CALDoc.Comment.Function newCALDocComment = null;
        if (foreign.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Function)foreign.getCALDocComment().accept(this, arg);
        }

        return FunctionDefn.Foreign.makeAnnotated(
            newCALDocComment,
            foreign.getName(),
            foreign.getScope(),
            foreign.isScopeExplicitlySpecified(),
            foreign.getExternalName(),
            foreign.getExternalNameSourceRange(),
            (TypeSignature)foreign.getDeclaredType().accept(this, arg),
            foreign.getSourceRange(),
            foreign.getSourceRangeExcludingCaldoc(),
            foreign.getNameSourceRange());
    }

    /**
     * @param primitive the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public FunctionDefn visit_FunctionDefn_Primitive(
        FunctionDefn.Primitive primitive, T arg) {

        SourceModel.verifyArg(primitive, "primitive");

        CALDoc.Comment.Function newCALDocComment = null;
        if (primitive.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Function)primitive.getCALDocComment().accept(this, arg);
        }

        return FunctionDefn.Primitive.makeAnnotated(
            newCALDocComment,
            primitive.getName(),
            primitive.getScope(),
            primitive.isScopeExplicitlySpecified(),
            (TypeSignature)primitive.getDeclaredType().accept(this, arg),
            primitive.getSourceRange(),
            primitive.getSourceRangeExcludingCaldoc(),
            primitive.getNameSourceRange());
    }

    /**
     * @param declaration the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public FunctionTypeDeclaration visit_FunctionTypeDeclaraction(
        FunctionTypeDeclaration declaration, T arg) {

        SourceModel.verifyArg(declaration, "declaration");

        CALDoc.Comment.Function newCALDocComment = null;
        if (declaration.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Function)declaration.getCALDocComment().accept(this, arg);
        }

        return FunctionTypeDeclaration.makeAnnotated(
            newCALDocComment,
            declaration.getFunctionName(),
            (TypeSignature)declaration.getTypeSignature().accept(this, arg),
            declaration.getSourceRange(),
            declaration.getSourceRangeOfDefn());
    }

    /**
     * @param defn the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public InstanceDefn visit_InstanceDefn(
        InstanceDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        CALDoc.Comment.Instance newCALDocComment = null;
        if (defn.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.Instance)defn.getCALDocComment().accept(this, arg);
        }

        Constraint.TypeClass[] newConstraints =
            new Constraint.TypeClass[defn.getNConstraints()];

        for (int i = 0; i < defn.getNConstraints(); i++) {
            newConstraints[i] =
                (Constraint.TypeClass)defn.getNthConstraint(i).accept(this, arg);
        }

        InstanceDefn.InstanceMethod[] newInstanceMethods =
            new InstanceDefn.InstanceMethod[defn.getNInstanceMethods()];

        for (int i = 0; i < defn.getNInstanceMethods(); i++) {
            newInstanceMethods[i] =
                (InstanceDefn.InstanceMethod)defn.getNthInstanceMethod(i).accept(this, arg);
        }

        return InstanceDefn.makeAnnotated(
            newCALDocComment,
            (Name.TypeClass)defn.getTypeClassName().accept(this, arg),
            (InstanceDefn.InstanceTypeCons)defn.getInstanceTypeCons().accept(this, arg),
            newConstraints,
            newInstanceMethods,
            defn.getSourceRange(),
            defn.getParenthesizeConstraints());
    }

    /**
     * @param function the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public InstanceDefn.InstanceTypeCons visit_InstanceDefn_InstanceTypeCons_Function(
        InstanceDefn.InstanceTypeCons.Function function, T arg) {

        SourceModel.verifyArg(function, "function");

        return InstanceDefn.InstanceTypeCons.Function.makeAnnotated(
            (Name.TypeVar)function.getDomainTypeVar().accept(this, arg),
            (Name.TypeVar)function.getCodomainTypeVar().accept(this, arg),
            function.getSourceRange(),
            function.getOperatorSourceRange());
    }

    /**
     * @param list the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public InstanceDefn.InstanceTypeCons visit_InstanceDefn_InstanceTypeCons_List(
        InstanceDefn.InstanceTypeCons.List list, T arg) {

        SourceModel.verifyArg(list, "list");

        return InstanceDefn.InstanceTypeCons.List.makeAnnotated(
            (Name.TypeVar)list.getElemTypeVar().accept(this, arg),
            list.getSourceRange());
    }
    
    /**
     * @param record  the source model element to be copied
     * @param arg unused argument
     * @return the result from visiting the source model element
     */
    public InstanceDefn.InstanceTypeCons visit_InstanceDefn_InstanceTypeCons_Record(
        InstanceDefn.InstanceTypeCons.Record record, T arg) {
       
        SourceModel.verifyArg(record, "record");

        return InstanceDefn.InstanceTypeCons.Record.makeAnnotated(
            (Name.TypeVar)record.getElemTypeVar().accept(this, arg),
            record.getSourceRange(), record.getSourceRangeOfDefn());        
    } 

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public InstanceDefn.InstanceTypeCons visit_InstanceDefn_InstanceTypeCons_TypeCons(
        InstanceDefn.InstanceTypeCons.TypeCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");
        
        final Name.TypeVar[] typeVars = cons.getTypeVars();
        final int nTypeVars = typeVars.length;
        final Name.TypeVar[] newTypeVars = new Name.TypeVar[nTypeVars];
        for (int i = 0; i < nTypeVars; i++) {
            newTypeVars[i] = (Name.TypeVar)typeVars[i].accept(this, arg);
        }

        return InstanceDefn.InstanceTypeCons.TypeCons.makeAnnotated(
            (Name.TypeCons)cons.getTypeConsName().accept(this, arg),
            newTypeVars,
            cons.getSourceRange(),
            cons.getSourceRangeOfDefn(),
            cons.getParenthesized());
    }
    
    /**
     * @param unit the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public InstanceDefn.InstanceTypeCons visit_InstanceDefn_InstanceTypeCons_Unit(
        InstanceDefn.InstanceTypeCons.Unit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        return InstanceDefn.InstanceTypeCons.Unit.makeAnnotated(unit.getSourceRange());
    } 

    /**
     * @param method the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public InstanceDefn.InstanceMethod visit_InstanceDefn_InstanceMethod(
        InstanceDefn.InstanceMethod method, T arg) {

        SourceModel.verifyArg(method, "method");

        CALDoc.Comment.InstanceMethod newCALDocComment = null;
        if (method.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.InstanceMethod)method.getCALDocComment().accept(this, arg);
        }

        return InstanceDefn.InstanceMethod.makeAnnotated(
            newCALDocComment,
            method.getClassMethodName(),
            (Name.Function)method.getResolvingFunctionName().accept(this, arg),
            method.getSourceRange(),
            method.getClassMethodNameSourceRange());
    }

    /**
     * @param defn the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeClassDefn visit_TypeClassDefn(
        TypeClassDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        CALDoc.Comment.TypeClass newCALDocComment = null;
        if (defn.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.TypeClass)defn.getCALDocComment().accept(this, arg);
        }

        Constraint.TypeClass[] newParentClassConstraints =
            new Constraint.TypeClass[defn.getNParentClassConstraints()];

        for (int i = 0; i < defn.getNParentClassConstraints(); i++) {
            newParentClassConstraints[i] =
                (Constraint.TypeClass)defn.getNthParentClassConstraint(i).accept(this, arg);
        }

        TypeClassDefn.ClassMethodDefn[] newClassMethodDefns =
            new TypeClassDefn.ClassMethodDefn[defn.getNClassMethodDefns()];

        for (int i = 0; i < defn.getNClassMethodDefns(); i++) {
            newClassMethodDefns[i] =
                (TypeClassDefn.ClassMethodDefn)defn.getNthClassMethodDefn(i).accept(this, arg);
        }

        return TypeClassDefn.makeAnnotated(
            newCALDocComment, defn.getTypeClassName(),
            (Name.TypeVar)defn.getTypeVar().accept(this, arg),
            defn.getScope(), defn.isScopeExplicitlySpecified(),
            newParentClassConstraints, newClassMethodDefns, defn.getSourceRange(), defn.getSourceRangeOfDefn(),defn.getParenthesizeConstraints());
    }

    /**
     * @param defn the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeClassDefn.ClassMethodDefn visit_TypeClassDefn_ClassMethodDefn(
        TypeClassDefn.ClassMethodDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        CALDoc.Comment.ClassMethod newCALDocComment = null;
        if (defn.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.ClassMethod)defn.getCALDocComment().accept(this, arg);
        }
        
        TypeSignature newTypeSignature = (TypeSignature)defn.getTypeSignature().accept(this, arg);
        
        Name.Function newDefaultClassMethodName = null;
        if (defn.getDefaultClassMethodName() != null) {
            newDefaultClassMethodName = (Name.Function)defn.getDefaultClassMethodName().accept(this, arg);
        }

        return TypeClassDefn.ClassMethodDefn.makeAnnotated(
            newCALDocComment,
            defn.getMethodName(),
            defn.getScope(),
            defn.isScopeExplicitlySpecified(),
            newTypeSignature,
            newDefaultClassMethodName,
            defn.getSourceRange(), 
            defn.getSourceRangeOfClassDefn());
    }

    /**
     * @param type the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeConstructorDefn visit_TypeConstructorDefn_AlgebraicType(
        TypeConstructorDefn.AlgebraicType type, T arg) {

        SourceModel.verifyArg(type, "type");

        CALDoc.Comment.TypeCons newCALDocComment = null;
        if (type.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.TypeCons)type.getCALDocComment().accept(this, arg);
        }

        final Name.TypeVar[] typeParameters = type.getTypeParameters();
        final int nTypeParameters = typeParameters.length;
        final Name.TypeVar[] newTypeParameters = new Name.TypeVar[nTypeParameters];
        for (int i = 0; i < nTypeParameters; i++) {
            newTypeParameters[i] = (Name.TypeVar)typeParameters[i].accept(this, arg);
        }
        
        TypeConstructorDefn.AlgebraicType.DataConsDefn[] newDataConstructors =
            new TypeConstructorDefn.AlgebraicType.DataConsDefn[type.getNDataConstructors()];

        for (int i = 0; i < type.getNDataConstructors(); i++) {
            newDataConstructors[i] =
                (TypeConstructorDefn.AlgebraicType.DataConsDefn)type.getNthDataConstructor(i).accept(this, arg);
        }
        
        final int nDerivingClauseTypeClassNames = type.getNDerivingClauseTypeClassNames();
        Name.TypeClass[] newDerivingClauseTypeClassNames;
        if (nDerivingClauseTypeClassNames > 0) {
            newDerivingClauseTypeClassNames = new Name.TypeClass[nDerivingClauseTypeClassNames];
            for (int i = 0; i < nDerivingClauseTypeClassNames; ++i) {
                newDerivingClauseTypeClassNames[i] = (Name.TypeClass)type.getDerivingClauseTypeClassName(i).accept(this, arg);
            }
        } else {
            newDerivingClauseTypeClassNames = null;
        }

        return TypeConstructorDefn.AlgebraicType.makeAnnotated(
            newCALDocComment, type.getTypeConsName(), type.getScope(), type.isScopeExplicitlySpecified(), newTypeParameters, newDataConstructors, newDerivingClauseTypeClassNames, type.getSourceRange(), type.getSourceRangeOfDefn());
    }

    /**
     * @param defn the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeConstructorDefn.AlgebraicType.DataConsDefn visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(
        TypeConstructorDefn.AlgebraicType.DataConsDefn defn, T arg) {

        SourceModel.verifyArg(defn, "defn");

        CALDoc.Comment.DataCons newCALDocComment = null;
        if (defn.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.DataCons)defn.getCALDocComment().accept(this, arg);
        }

        TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument[] newTypeArgs =
            new TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument[defn.getNTypeArgs()];

        for (int i = 0; i < defn.getNTypeArgs(); i++) {
            newTypeArgs[i] =
                (TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument)defn.getNthTypeArg(i).accept(this, arg);
        }

        return TypeConstructorDefn.AlgebraicType.DataConsDefn.makeAnnotated(newCALDocComment, defn.getDataConsName(), defn.getScope(), defn.isScopeExplicitlySpecified(), newTypeArgs, defn.getSourceRange(), defn.getSourceRangeOfDefn());
    }

    /**
     * @param argument the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument visit_TypeConstructorDefn_AlgebraicType_DataConsDefn_TypeArgument(
        TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument argument, T arg) {

        SourceModel.verifyArg(argument, "argument");

        return TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument.makeAnnotated(
            (Name.Field)argument.getFieldName().accept(this, arg),
            (TypeExprDefn)argument.getTypeExprDefn().accept(this, arg),
            argument.isStrict(), argument.getSourceRange());
    }

    /**
     * @param type the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeConstructorDefn visit_TypeConstructorDefn_ForeignType(
        TypeConstructorDefn.ForeignType type, T arg) {

        SourceModel.verifyArg(type, "type");
        
        CALDoc.Comment.TypeCons newCALDocComment = null;
        if (type.getCALDocComment() != null) {
            newCALDocComment = (CALDoc.Comment.TypeCons)type.getCALDocComment().accept(this, arg);
        }

        final int nDerivingClauseTypeClassNames = type.getNDerivingClauseTypeClassNames();
        Name.TypeClass[] newDerivingClauseTypeClassNames;
        if (nDerivingClauseTypeClassNames > 0) {
            newDerivingClauseTypeClassNames = new Name.TypeClass[nDerivingClauseTypeClassNames];
            for (int i = 0; i < nDerivingClauseTypeClassNames; ++i) {
                newDerivingClauseTypeClassNames[i] = (Name.TypeClass)type.getDerivingClauseTypeClassName(i).accept(this, arg);
            }
        } else {
            newDerivingClauseTypeClassNames = null;
        }        

        return TypeConstructorDefn.ForeignType.makeAnnotated(
            newCALDocComment,
            type.getTypeConsName(),
            type.getScope(),
            type.isScopeExplicitlySpecified(),
            type.getExternalName(),
            type.getExternalNameSourceRange(),
            type.getImplementationScope(),
            type.isImplementationScopeExplicitlySpecified(),
            newDerivingClauseTypeClassNames,
            type.getSourceRange(),
            type.getSourceRangeOfDefn());
    }

    /**
     * @param application the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_Application(
        TypeExprDefn.Application application, T arg) {

        SourceModel.verifyArg(application, "application");

        TypeExprDefn[] newTypeExpressions = new TypeExprDefn[application.getNTypeExpressions()];
        for (int i = 0; i < application.getNTypeExpressions(); i++) {
            newTypeExpressions[i] = (TypeExprDefn)application.getNthTypeExpression(i).accept(this, arg);
        }

        return TypeExprDefn.Application.makeAnnotated(newTypeExpressions, application.getSourceRange());
    }

    /**
     * @param function the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_Function(
        TypeExprDefn.Function function, T arg) {

        SourceModel.verifyArg(function, "function");

        return TypeExprDefn.Function.makeAnnotated(
            (TypeExprDefn)function.getDomain().accept(this, arg),
            (TypeExprDefn)function.getCodomain().accept(this, arg),
            function.getSourceRange(),
            function.getOperatorSourceRange());
    }

    /**
     * @param list the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_List(
        TypeExprDefn.List list, T arg) {

        SourceModel.verifyArg(list, "list");

        return TypeExprDefn.List.makeAnnotated(
            (TypeExprDefn)list.getElement().accept(this, arg),
            list.getSourceRange());
    }

    /**
     * @param record the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_Record(
        TypeExprDefn.Record record, T arg) {

        SourceModel.verifyArg(record, "record");

        TypeExprDefn.Record.FieldTypePair[] newExtensionFields =
            new TypeExprDefn.Record.FieldTypePair[record.getNExtensionFields()];

        for (int i = 0; i < record.getNExtensionFields(); i++) {
            newExtensionFields[i] =
                (TypeExprDefn.Record.FieldTypePair)record.getNthExtensionField(i).accept(this, arg);
        }

        return TypeExprDefn.Record.makeAnnotated(
            (record.getBaseRecordVar() == null)
                ? null
                : (TypeExprDefn.TypeVar)record.getBaseRecordVar().accept(this, arg),
            newExtensionFields,
            record.getSourceRange());
    }

    /**
     * @param pair the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn.Record.FieldTypePair visit_TypeExprDefn_Record_FieldTypePair(
        TypeExprDefn.Record.FieldTypePair pair, T arg) {

        SourceModel.verifyArg(pair, "pair");

        return TypeExprDefn.Record.FieldTypePair.make(
            (Name.Field)pair.getFieldName().accept(this, arg),
            (TypeExprDefn)pair.getFieldType().accept(this, arg));
    }

    /**
     * @param tuple the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_Tuple(
        TypeExprDefn.Tuple tuple, T arg) {

        SourceModel.verifyArg(tuple, "tuple");

        TypeExprDefn[] newComponents = new TypeExprDefn[tuple.getNComponents()];
        for (int i = 0; i < tuple.getNComponents(); i++) {
            newComponents[i] = (TypeExprDefn)tuple.getNthComponent(i).accept(this, arg);
        }

        return TypeExprDefn.Tuple.makeAnnotated(newComponents, tuple.getSourceRange());
    }

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_TypeCons(
        TypeExprDefn.TypeCons cons, T arg) {

        SourceModel.verifyArg(cons, "cons");

        return TypeExprDefn.TypeCons.makeAnnotated(
            (Name.TypeCons)cons.getTypeConsName().accept(this, arg),
            cons.getSourceRange());
    }

    /**
     * @param var the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_TypeVar(
        TypeExprDefn.TypeVar var, T arg) {

        SourceModel.verifyArg(var, "var");

        return TypeExprDefn.TypeVar.makeAnnotated(
            (Name.TypeVar)var.getTypeVarName().accept(this, arg),
            var.getSourceRange());
    }

    /**
     * @param unit the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeExprDefn visit_TypeExprDefn_Unit(
        TypeExprDefn.Unit unit, T arg) {

        SourceModel.verifyArg(unit, "unit");

        return TypeExprDefn.Unit.makeAnnotated(unit.getSourceRange());
    }

    /**
     * @param signature the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public TypeSignature visit_TypeSignature(
        TypeSignature signature, T arg) {

        SourceModel.verifyArg(signature, "signature");

        Constraint[] newConstraints = new Constraint[signature.getNConstraints()];
        for (int i = 0; i < signature.getNConstraints(); i++) {
            newConstraints[i] = (Constraint)signature.getNthConstraint(i).accept(this, arg);
        }

        return TypeSignature.make(
            newConstraints,
            (TypeExprDefn)signature.getTypeExprDefn().accept(this, arg),
            signature.getConstraintsHaveParen());
    }

    /**
     * @param module the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_Module(
        CALDoc.Comment.Module module, T arg) {
        
        SourceModel.verifyArg(module, "module");
        
        final int nTaggedBlocks = module.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)module.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.Module.makeAnnotated(
            (CALDoc.TextBlock)module.getDescription().accept(this, arg),
            newTaggedBlocks, module.getSourceRange());
    }

    /**
     * @param function the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_Function(
        CALDoc.Comment.Function function, T arg) {
        
        SourceModel.verifyArg(function, "function");
        
        final int nTaggedBlocks = function.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)function.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.Function.makeAnnotated(
            (CALDoc.TextBlock)function.getDescription().accept(this, arg),
            newTaggedBlocks, function.getSourceRange());
    }

    /**
     * @param typeCons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_TypeCons(
        CALDoc.Comment.TypeCons typeCons, T arg) {
        
        SourceModel.verifyArg(typeCons, "typeCons");
        
        final int nTaggedBlocks = typeCons.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)typeCons.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.TypeCons.makeAnnotated(
            (CALDoc.TextBlock)typeCons.getDescription().accept(this, arg),
            newTaggedBlocks, typeCons.getSourceRange());
    }

    /**
     * @param dataCons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_DataCons(
        CALDoc.Comment.DataCons dataCons, T arg) {
        
        SourceModel.verifyArg(dataCons, "dataCons");
        
        final int nTaggedBlocks = dataCons.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)dataCons.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.DataCons.makeAnnotated(
            (CALDoc.TextBlock)dataCons.getDescription().accept(this, arg),
            newTaggedBlocks, dataCons.getSourceRange());
    }

    /**
     * @param typeClass the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_TypeClass(
        CALDoc.Comment.TypeClass typeClass, T arg) {
        
        SourceModel.verifyArg(typeClass, "typeClass");
        
        final int nTaggedBlocks = typeClass.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)typeClass.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.TypeClass.makeAnnotated(
            (CALDoc.TextBlock)typeClass.getDescription().accept(this, arg),
            newTaggedBlocks, typeClass.getSourceRange());
    }

    /**
     * @param method the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_ClassMethod(
        CALDoc.Comment.ClassMethod method, T arg) {
        
        SourceModel.verifyArg(method, "method");
        
        final int nTaggedBlocks = method.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)method.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.ClassMethod.makeAnnotated(
            (CALDoc.TextBlock)method.getDescription().accept(this, arg),
            newTaggedBlocks, method.getSourceRange());
    }

    /**
     * @param instance the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_Instance(
        CALDoc.Comment.Instance instance, T arg) {
        
        SourceModel.verifyArg(instance, "instance");
        
        final int nTaggedBlocks = instance.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)instance.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.Instance.makeAnnotated(
            (CALDoc.TextBlock)instance.getDescription().accept(this, arg),
            newTaggedBlocks, instance.getSourceRange());
    }

    /**
     * @param method the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.Comment visit_CALDoc_Comment_InstanceMethod(
        CALDoc.Comment.InstanceMethod method, T arg) {
        
        SourceModel.verifyArg(method, "method");
        
        final int nTaggedBlocks = method.getNTaggedBlocks();
        CALDoc.TaggedBlock[] newTaggedBlocks = new CALDoc.TaggedBlock[nTaggedBlocks];
        for (int i = 0; i < nTaggedBlocks; i++) {
            newTaggedBlocks[i] = (CALDoc.TaggedBlock)method.getNthTaggedBlock(i).accept(this, arg);
        }
        
        return CALDoc.Comment.InstanceMethod.makeAnnotated(
            (CALDoc.TextBlock)method.getDescription().accept(this, arg),
            newTaggedBlocks, method.getSourceRange());
    }

    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_Plain(
        CALDoc.TextSegment.Plain segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");

        return CALDoc.TextSegment.Plain.make(segment.getText());
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment visit_CALDoc_TextSegment_Preformatted(
        CALDoc.TextSegment.Preformatted segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        final int nSegments = segment.getNSegments();
        CALDoc.TextSegment.TopLevel[] newSegments = new CALDoc.TextSegment.TopLevel[nSegments];
        for (int i = 0; i < nSegments; i++) {
            newSegments[i] = (CALDoc.TextSegment.TopLevel)segment.getNthSegment(i).accept(this, arg);
        }
        
        return CALDoc.TextSegment.Preformatted.make(newSegments);
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_URL(
        CALDoc.TextSegment.InlineTag.URL segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.URL.make((CALDoc.TextSegment.Plain)segment.getContent().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Link_Function(
        CALDoc.TextSegment.InlineTag.Link.Function segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Link.Function.make((CALDoc.CrossReference.Function)segment.getReference().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Link_Module(
        CALDoc.TextSegment.InlineTag.Link.Module segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Link.Module.make((CALDoc.CrossReference.Module)segment.getReference().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Link_TypeCons(
        CALDoc.TextSegment.InlineTag.Link.TypeCons segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Link.TypeCons.make((CALDoc.CrossReference.TypeCons)segment.getReference().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Link_DataCons(
        CALDoc.TextSegment.InlineTag.Link.DataCons segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Link.DataCons.make((CALDoc.CrossReference.DataCons)segment.getReference().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Link_TypeClass(
        CALDoc.TextSegment.InlineTag.Link.TypeClass segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Link.TypeClass.make((CALDoc.CrossReference.TypeClass)segment.getReference().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Link_ConsNameWithoutContext(
        CALDoc.TextSegment.InlineTag.Link.ConsNameWithoutContext segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Link.ConsNameWithoutContext.makeAnnotated(
                (CALDoc.CrossReference.WithoutContextCons)segment.getReference().accept(this, arg),
                segment.getSourceRange());
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Link_FunctionWithoutContext(
        CALDoc.TextSegment.InlineTag.Link.FunctionWithoutContext segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Link.FunctionWithoutContext.make((CALDoc.CrossReference.Function)segment.getReference().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_TextFormatting_Emphasized(
        CALDoc.TextSegment.InlineTag.TextFormatting.Emphasized segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.TextFormatting.Emphasized.make((CALDoc.TextBlock)segment.getContent().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_TextFormatting_StronglyEmphasized(
        CALDoc.TextSegment.InlineTag.TextFormatting.StronglyEmphasized segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.TextFormatting.StronglyEmphasized.make((CALDoc.TextBlock)segment.getContent().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_TextFormatting_Superscript(
        CALDoc.TextSegment.InlineTag.TextFormatting.Superscript segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.TextFormatting.Superscript.make((CALDoc.TextBlock)segment.getContent().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_TextFormatting_Subscript(
        CALDoc.TextSegment.InlineTag.TextFormatting.Subscript segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.TextFormatting.Subscript.make((CALDoc.TextBlock)segment.getContent().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Summary(
        CALDoc.TextSegment.InlineTag.Summary segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Summary.make((CALDoc.TextBlock)segment.getContent().accept(this, arg));
    }
    
    /**
     * @param segment the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_Code(
        CALDoc.TextSegment.InlineTag.Code segment, T arg) {
        
        SourceModel.verifyArg(segment, "segment");
        
        return CALDoc.TextSegment.InlineTag.Code.make((CALDoc.TextSegment.Preformatted)segment.getContent().accept(this, arg));
    }
    
    /**
     * @param list the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_List_Unordered(
        CALDoc.TextSegment.InlineTag.List.Unordered list, T arg) {
        
        SourceModel.verifyArg(list, "list");
        
        final int nItems = list.getNItems();
        CALDoc.TextSegment.InlineTag.List.Item[] newItems = new CALDoc.TextSegment.InlineTag.List.Item[nItems];
        for (int i = 0; i < nItems; i++) {
            newItems[i] = (CALDoc.TextSegment.InlineTag.List.Item)list.getNthItem(i).accept(this, arg);
        }
        
        return CALDoc.TextSegment.InlineTag.List.Unordered.make(newItems);
    }
    
    /**
     * @param list the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.TopLevel visit_CALDoc_TextSegment_InlineTag_List_Ordered(
        CALDoc.TextSegment.InlineTag.List.Ordered list, T arg) {
        
        SourceModel.verifyArg(list, "list");
        
        final int nItems = list.getNItems();
        CALDoc.TextSegment.InlineTag.List.Item[] newItems = new CALDoc.TextSegment.InlineTag.List.Item[nItems];
        for (int i = 0; i < nItems; i++) {
            newItems[i] = (CALDoc.TextSegment.InlineTag.List.Item)list.getNthItem(i).accept(this, arg);
        }
        
        return CALDoc.TextSegment.InlineTag.List.Ordered.make(newItems);
    }
    
    /**
     * @param item the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextSegment.InlineTag.List.Item visit_CALDoc_TextSegment_InlineTag_List_Item(
        CALDoc.TextSegment.InlineTag.List.Item item, T arg) {
        
        SourceModel.verifyArg(item, "item");
        
        return CALDoc.TextSegment.InlineTag.List.Item.make((CALDoc.TextBlock)item.getContent().accept(this, arg));
    }
    
    /**
     * @param block the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TextBlock visit_CALDoc_TextBlock(
        CALDoc.TextBlock block, T arg) {
        
        SourceModel.verifyArg(block, "block");
        
        final int nSegments = block.getNSegments();
        CALDoc.TextSegment.TopLevel[] newSegments = new CALDoc.TextSegment.TopLevel[nSegments];
        for (int i = 0; i < nSegments; i++) {
            newSegments[i] = (CALDoc.TextSegment.TopLevel)block.getNthSegment(i).accept(this, arg);
        }
        
        return CALDoc.TextBlock.make(newSegments);
    }
    
    /**
     * @param authorBlock the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_Author(
        CALDoc.TaggedBlock.Author authorBlock, T arg) {
        
        SourceModel.verifyArg(authorBlock, "authorBlock");
        
        return CALDoc.TaggedBlock.Author.make(
            (CALDoc.TextBlock)authorBlock.getTextBlock().accept(this, arg));
    }

    /**
     * @param deprecatedBlock the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_Deprecated(
        CALDoc.TaggedBlock.Deprecated deprecatedBlock, T arg) {
        
        SourceModel.verifyArg(deprecatedBlock, "deprecatedBlock");
        
        return CALDoc.TaggedBlock.Deprecated.make(
            (CALDoc.TextBlock)deprecatedBlock.getTextBlock().accept(this, arg));
    }

    /**
     * @param returnBlock the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_Return(
        CALDoc.TaggedBlock.Return returnBlock, T arg) {
        
        SourceModel.verifyArg(returnBlock, "returnBlock");
        
        return CALDoc.TaggedBlock.Return.make(
            (CALDoc.TextBlock)returnBlock.getTextBlock().accept(this, arg));
    }

    /**
     * @param versionBlock the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_Version(
        CALDoc.TaggedBlock.Version versionBlock, T arg) {
        
        SourceModel.verifyArg(versionBlock, "versionBlock");
        
        return CALDoc.TaggedBlock.Version.make(
            (CALDoc.TextBlock)versionBlock.getTextBlock().accept(this, arg));
    }

    /**
     * @param argBlock the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_Arg(
        CALDoc.TaggedBlock.Arg argBlock, T arg) {
        
        SourceModel.verifyArg(argBlock, "argBlock");
        
        return CALDoc.TaggedBlock.Arg.make(
            (Name.Field)argBlock.getArgName().accept(this, arg),
            (CALDoc.TextBlock)argBlock.getTextBlock().accept(this, arg));
    }
    
    /**
     * @param function the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_See_Function(
        CALDoc.TaggedBlock.See.Function function, T arg) {
        
        SourceModel.verifyArg(function, "function");
        
        final int nFunctionNames = function.getNFunctionNames();
        CALDoc.CrossReference.Function[] newFunctionNames = new CALDoc.CrossReference.Function[nFunctionNames];
        for (int i = 0; i < nFunctionNames; i++) {
            newFunctionNames[i] = (CALDoc.CrossReference.Function)function.getNthFunctionName(i).accept(this, arg);
        }
        
        return CALDoc.TaggedBlock.See.Function.make(newFunctionNames);
    }

    /**
     * @param module the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_See_Module(
        CALDoc.TaggedBlock.See.Module module, T arg) {
        
        SourceModel.verifyArg(module, "module");
        
        final int nModuleNames = module.getNModuleNames();
        CALDoc.CrossReference.Module[] newModuleNames = new CALDoc.CrossReference.Module[nModuleNames];
        for (int i = 0; i < nModuleNames; i++) {
            newModuleNames[i] = (CALDoc.CrossReference.Module)module.getNthModuleName(i).accept(this, arg);
        }
        
        return CALDoc.TaggedBlock.See.Module.make(newModuleNames);
    }

    /**
     * @param typeCons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_See_TypeCons(
        CALDoc.TaggedBlock.See.TypeCons typeCons, T arg) {
        
        SourceModel.verifyArg(typeCons, "typeCons");
        
        final int nTypeConsNames = typeCons.getNTypeConsNames();
        CALDoc.CrossReference.TypeCons[] newTypeConsNames = new CALDoc.CrossReference.TypeCons[nTypeConsNames];
        for (int i = 0; i < nTypeConsNames; i++) {
            newTypeConsNames[i] = (CALDoc.CrossReference.TypeCons)typeCons.getNthTypeConsName(i).accept(this, arg);
        }
        
        return CALDoc.TaggedBlock.See.TypeCons.make(newTypeConsNames);
    }

    /**
     * @param dataCons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_See_DataCons(
        CALDoc.TaggedBlock.See.DataCons dataCons, T arg) {
        
        SourceModel.verifyArg(dataCons, "dataCons");
        
        final int nDataConsNames = dataCons.getNDataConsNames();
        CALDoc.CrossReference.DataCons[] newDataConsNames = new CALDoc.CrossReference.DataCons[nDataConsNames];
        for (int i = 0; i < nDataConsNames; i++) {
            newDataConsNames[i] = (CALDoc.CrossReference.DataCons)dataCons.getNthDataConsName(i).accept(this, arg);
        }
        
        return CALDoc.TaggedBlock.See.DataCons.make(newDataConsNames);
    }

    /**
     * @param typeClass the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_See_TypeClass(
        CALDoc.TaggedBlock.See.TypeClass typeClass, T arg) {
        
        SourceModel.verifyArg(typeClass, "typeClass");
        
        final int nTypeClassNames = typeClass.getNTypeClassNames();
        CALDoc.CrossReference.TypeClass[] newTypeClassNames = new CALDoc.CrossReference.TypeClass[nTypeClassNames];
        for (int i = 0; i < nTypeClassNames; i++) {
            newTypeClassNames[i] = (CALDoc.CrossReference.TypeClass)typeClass.getNthTypeClassName(i).accept(this, arg);
        }
        
        return CALDoc.TaggedBlock.See.TypeClass.make(newTypeClassNames);
    }
    
    /**
     * @param reference the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.CrossReference visit_CALDoc_CrossReference_Function(
        CALDoc.CrossReference.Function reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        return CALDoc.CrossReference.Function.make(
            (Name.Function)reference.getName().accept(this, arg),
            reference.isChecked());
    }

    /**
     * @param reference the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.CrossReference visit_CALDoc_CrossReference_Module(
        CALDoc.CrossReference.Module reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        return CALDoc.CrossReference.Module.make(
            (Name.Module)reference.getName().accept(this, arg),
            reference.isChecked());
    }

    /**
     * @param reference the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.CrossReference visit_CALDoc_CrossReference_TypeCons(
        CALDoc.CrossReference.TypeCons reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        return CALDoc.CrossReference.TypeCons.make(
            (Name.TypeCons)reference.getName().accept(this, arg),
            reference.isChecked());
    }

    /**
     * @param reference the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.CrossReference visit_CALDoc_CrossReference_DataCons(
        CALDoc.CrossReference.DataCons reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        return CALDoc.CrossReference.DataCons.make(
            (Name.DataCons)reference.getName().accept(this, arg),
            reference.isChecked());
    }

    /**
     * @param reference the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.CrossReference visit_CALDoc_CrossReference_TypeClass(
        CALDoc.CrossReference.TypeClass reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        return CALDoc.CrossReference.TypeClass.make(
            (Name.TypeClass)reference.getName().accept(this, arg),
            reference.isChecked());
    }

    /**
     * @param reference the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.CrossReference visit_CALDoc_CrossReference_WithoutContextCons(
        CALDoc.CrossReference.WithoutContextCons reference, T arg) {
        
        SourceModel.verifyArg(reference, "reference");
        
        return CALDoc.CrossReference.WithoutContextCons.makeAnnotated(
            (Name.WithoutContextCons)reference.getName().accept(this, arg),
            reference.isChecked(),
            reference.getSourceRange());
    }

    /**
     * @param seeBlock the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element
     */
    public CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_See_WithoutContext(
        CALDoc.TaggedBlock.See.WithoutContext seeBlock, T arg) {
        
        SourceModel.verifyArg(seeBlock, "seeBlock");
        
        final int nReferencedNames = seeBlock.getNReferencedNames();
        CALDoc.CrossReference.CanAppearWithoutContext[] newReferencedNames = new CALDoc.CrossReference.CanAppearWithoutContext[nReferencedNames];
        for (int i = 0; i < nReferencedNames; i++) {
            newReferencedNames[i] = (CALDoc.CrossReference.CanAppearWithoutContext)seeBlock.getNthReferencedName(i).accept(this, arg);
        }
        
        return CALDoc.TaggedBlock.See.WithoutContext.makeAnnotated(newReferencedNames, seeBlock.getSourceRange());
    }
}
