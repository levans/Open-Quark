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
 * SourceModelParenStripper.java
 * Created: Jun 27, 2007
 * By: mbyne
 */

package org.openquark.cal.compiler;

import org.openquark.cal.compiler.SourceModel.ArgBindings;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Constraint;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;

/**
 * This class is used to make a copy of a SourceModel with all the user
 * inserted parentheses removed.
 *
 * @author Magnus Byne
 */
public class SourceModelParenStripper extends SourceModelCopier<Void> {

    /**
     * This does not copy the paren node
     * @param parenthesized the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element contained within the paren
     */
    @Override
    public Expr visit_Expr_Parenthesized(
        Expr.Parenthesized parenthesized, Void arg) {

        SourceModel.verifyArg(parenthesized, "parenthesized");

        return (Expr)parenthesized.getExpression().accept(this, arg);
    } 
    
    /**
     * This does not copy the paren node
     * @param parenthesized the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element contained within the paren
     */
    @Override
    public TypeExprDefn visit_TypeExprDefn_Parenthesized(
        TypeExprDefn.Parenthesized parenthesized, Void arg) {
        
        return (TypeExprDefn)parenthesized.getTypeExprDefn().accept(this, arg);
    }
    
    /**
     * This does not copy the paren field
     * @param signature the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element ignoring paren
     */
    @Override
    public TypeSignature visit_TypeSignature(
        TypeSignature signature, Void arg) {

        SourceModel.verifyArg(signature, "signature");

        Constraint[] newConstraints = new Constraint[signature.getNConstraints()];
        for (int i = 0; i < signature.getNConstraints(); i++) {
            newConstraints[i] = (Constraint)signature.getNthConstraint(i).accept(this, arg);
        }

        return TypeSignature.make(
            newConstraints,
            (TypeExprDefn)signature.getTypeExprDefn().accept(this, arg));
    }
    
    /**
     * @param defn the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element ignoring parens
     */
    @Override
    public InstanceDefn visit_InstanceDefn(
        InstanceDefn defn, Void arg) {

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
            defn.getSourceRange());
    }

    /**
     * @param defn the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element ignoring parens
     */
    @Override
    public TypeClassDefn visit_TypeClassDefn(
        TypeClassDefn defn, Void arg) {

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
            newParentClassConstraints, newClassMethodDefns, defn.getSourceRange(), defn.getSourceRangeOfDefn(), false);
    }

    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element with explicit parens removed
     */
    @Override
    public InstanceDefn.InstanceTypeCons visit_InstanceDefn_InstanceTypeCons_TypeCons(
        InstanceDefn.InstanceTypeCons.TypeCons cons, Void arg) {

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
            false);
    }
    
    /**
     * @param cons the source model element to be copied
     * @param arg unused argument
     * @return a deep copy of the source model element ignoring parens
     */
    @Override
    public Expr.Case.Alt visit_Expr_Case_Alt_UnpackDataCons(
            Expr.Case.Alt.UnpackDataCons cons, Void arg) {

        SourceModel.verifyArg(cons, "cons");

        int nDataConsNames = cons.getNDataConsNames();
        Name.DataCons[] newDataConsNameArray = new Name.DataCons[nDataConsNames];
        for (int i = 0; i < nDataConsNames; i++) {
            newDataConsNameArray[i] = (Name.DataCons)cons.getNthDataConsName(i).accept(this, arg);
        }
        
        return Expr.Case.Alt.UnpackDataCons.makeAnnotated(
            newDataConsNameArray,
            (ArgBindings)cons.getArgBindings().accept(this,arg),
            (Expr)cons.getAltExpr().accept(this, arg), cons.getSourceRange());
    }
    
}
