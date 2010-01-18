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
 * SourceModelVisitor.java
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

/**
 * A visitor interface for visiting source model elements.
 * <p>
 * 
 * The visitor mechanism supported by this interface is more general than the
 * regular visitor pattern. In particular, each visit method boasts an argument
 * of the generic type <code>T</code>, and also a generic return type of
 * <code>R</code>. This allows additional arguments, suitably encapsulated in
 * an object, to be passed in to any visit method. The visit methods are also
 * able to return values, which is useful for cases where the visitor needs to
 * aggregate results in a hierarchical fashion from visiting the tree of source
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
 * While it is certainly possible to directly implement the SourceModelVisitor
 * interface, it may be easier to subclass from one of the predefined visitor
 * classes. To build a visitor which traverses a source model, the
 * {@link SourceModelTraverser} class would be an appropriate base class
 * to subclass, since it provides a default pre-order traversal logic for all
 * source model elements. To construct a source model to source model
 * transformation, the {@link SourceModelCopier} is a convenient base
 * class to extend since it provides the default behaviour of performing a deep
 * copy of the source model, while allowing subclasses to hook in and return
 * transformations of source model elements where required.
 * <p>
 * 
 * If the source model structure is changed (e.g. when new source model element
 * classes are added, or when existing element classes are moved around in the
 * inheritance and/or containment hierarchy), then this visitor interface and
 * all implementors must be updated. This may include renaming existing
 * interface methods if element classes have been moved.
 * <p>
 * 
 * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
 * @param <R> the return type. If the return value is not used, specify {@link Void}.
 * 
 * @see org.openquark.cal.compiler.SourceModelTraverser
 * @see org.openquark.cal.compiler.SourceModelCopier
 * 
 * @author Joseph Wong
 */
public interface SourceModelVisitor<T, R> {

    /**
     * @param application
     *            the source model element to be visited the source model
     *            element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Application(
        Expr.Application application, T arg);

    /**
     * @param parenthesized
     *            the source model element to be visited the source model
     *            element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Parenthesized(
        Expr.Parenthesized parenthesized, T arg);
    
    /**
     * @param cons
     *            the source model element to be visited the source model
     *            element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_DataCons(
        Expr.DataCons cons, T arg);

    /**
     * @param var
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Var(
        Expr.Var var, T arg);

    /**
     * @param let
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Let(
        Expr.Let let, T arg);

    /**
     * @param caseExpr
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case(
        Expr.Case caseExpr, T arg);

    /**
     * @param defaultAlt
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_Default(
        Expr.Case.Alt.Default defaultAlt, T arg);

    /**
     * @param tuple
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackTuple(
        Expr.Case.Alt.UnpackTuple tuple, T arg);

    /**
     * @param unit
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackUnit(
        Expr.Case.Alt.UnpackUnit unit, T arg);

    /**
     * @param intAlt
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackInt(
        Expr.Case.Alt.UnpackInt intAlt, T arg);
    
    /**
     * @param charAlt
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackChar(
        Expr.Case.Alt.UnpackChar charAlt, T arg);
    
    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackDataCons(
        Expr.Case.Alt.UnpackDataCons cons, T arg);

    /**
     * @param argBindings
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_ArgBindings_Matching(
        ArgBindings.Matching argBindings, T arg);

    /**
     * @param argBindings
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_ArgBindings_Positional(
        ArgBindings.Positional argBindings, T arg);

    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackListCons(
        Expr.Case.Alt.UnpackListCons cons, T arg);

    /**
     * @param nil
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackListNil(
        Expr.Case.Alt.UnpackListNil nil, T arg);

    /**
     * @param record
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Case_Alt_UnpackRecord(
        Expr.Case.Alt.UnpackRecord record, T arg);

    /**
     * @param pattern
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_FieldPattern(
        FieldPattern pattern, T arg);

    /**
     * @param lambda
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Lambda(
        Expr.Lambda lambda, T arg);

    /**
     * @param ifExpr
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_If(
        Expr.If ifExpr, T arg);

    /**
     * @param num
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Literal_Num(
        Expr.Literal.Num num, T arg);

    /**
     * @param doubleLiteral
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Literal_Double(
        Expr.Literal.Double doubleLiteral, T arg);

    /**
     * @param floatLiteral
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Literal_Float(
            Expr.Literal.Float floatLiteral, T arg);
    
    /**
     * @param charLiteral
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Literal_Char(
        Expr.Literal.Char charLiteral, T arg);

    /**
     * @param string
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Literal_StringLit(
        Expr.Literal.StringLit string, T arg);

    /**
     * @param negate
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_UnaryOp_Negate(
        Expr.UnaryOp.Negate negate, T arg);

    /**
     * @param backquotedOperator
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_BackquotedOperator_Var(
        Expr.BinaryOp.BackquotedOperator.Var backquotedOperator, T arg);

    /**
     * @param backquotedOperator
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_BackquotedOperator_DataCons(
        Expr.BinaryOp.BackquotedOperator.DataCons backquotedOperator, T arg);

    /**
     * @param and
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_And(
        Expr.BinaryOp.And and, T arg);

    /**
     * @param or
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Or(
        Expr.BinaryOp.Or or, T arg);

    /**
     * @param equals
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Equals(
        Expr.BinaryOp.Equals equals, T arg);

    /**
     * @param notEquals
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_NotEquals(
        Expr.BinaryOp.NotEquals notEquals, T arg);

    /**
     * @param lt
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_LessThan(
        Expr.BinaryOp.LessThan lt, T arg);

    /**
     * @param lteq
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_LessThanEquals(
        Expr.BinaryOp.LessThanEquals lteq, T arg);

    /**
     * @param gteq
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_GreaterThanEquals(
        Expr.BinaryOp.GreaterThanEquals gteq, T arg);

    /**
     * @param gt
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_GreaterThan(
        Expr.BinaryOp.GreaterThan gt, T arg);

    /**
     * @param add
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Add(
        Expr.BinaryOp.Add add, T arg);

    /**
     * @param subtract
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Subtract(
        Expr.BinaryOp.Subtract subtract, T arg);

    /**
     * @param multiply
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Multiply(
        Expr.BinaryOp.Multiply multiply, T arg);

    /**
     * @param divide
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Divide(
        Expr.BinaryOp.Divide divide, T arg);
    
    /**
     * @param remainder
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Remainder(
        Expr.BinaryOp.Remainder remainder, T arg);    

    /**
     * @param compose
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Compose(
        Expr.BinaryOp.Compose compose, T arg);
    
    /**
     * @param apply
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Apply(
        Expr.BinaryOp.Apply apply, T arg);
    
    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Cons(
        Expr.BinaryOp.Cons cons, T arg);

    /**
     * @param append
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_BinaryOp_Append(
        Expr.BinaryOp.Append append, T arg);

    /**
     * @param unit
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Unit(
        Expr.Unit unit, T arg);

    /**
     * @param tuple
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Tuple(
        Expr.Tuple tuple, T arg);

    /**
     * @param list
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_List(
        Expr.List list, T arg);

    /**
     * @param record
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Record(
        Expr.Record record, T arg);

    /**
     * @param fieldExtension
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Record_FieldModification_Extension(
        Expr.Record.FieldModification.Extension fieldExtension, T arg);
    
    /**
     * @param fieldValueUpdate
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_Record_FieldModification_Update(
        Expr.Record.FieldModification.Update fieldValueUpdate, T arg);    

    /**
     * @param field
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_SelectDataConsField(
        Expr.SelectDataConsField field, T arg);

    /**
     * @param field
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_SelectRecordField(
        Expr.SelectRecordField field, T arg);

    /**
     * @param signature
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Expr_ExprTypeSignature(
        Expr.ExprTypeSignature signature, T arg);

    /**
     * @param lacks
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Constraint_Lacks(
        Constraint.Lacks lacks, T arg);

    /**
     * @param typeClass
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Constraint_TypeClass(
        Constraint.TypeClass typeClass, T arg);

    /**
     * @param importStmt
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Import(
        Import importStmt, T arg);
    
    /**
     * @param usingItem 
     *            the using item to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Import_UsingItem_Function(
        Import.UsingItem.Function usingItem, T arg);
    
    /**
     * @param usingItem 
     *            the using item to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Import_UsingItem_DataConstructor(
        Import.UsingItem.DataConstructor usingItem, T arg);
    
    /**
     * @param usingItem 
     *            the using item to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Import_UsingItem_TypeConstructor(
        Import.UsingItem.TypeConstructor usingItem, T arg);
    
    /**
     * @param usingItem 
     *            the using item to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Import_UsingItem_TypeClass(
        Import.UsingItem.TypeClass usingItem, T arg);
    
    /**
     * @param friendDeclaration
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Friend(
        Friend friendDeclaration, T arg);    

    /**
     * @param function
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_LocalDefn_Function_Definition(
        LocalDefn.Function.Definition function, T arg);

    /**
     * @param declaration
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_LocalDefn_Function_TypeDeclaration(
        LocalDefn.Function.TypeDeclaration declaration, T arg);

    /**
     * @param unpackDataCons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_LocalDefn_PatternMatch_UnpackDataCons(
        LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, T arg);

    /**
     * @param unpackTuple
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_LocalDefn_PatternMatch_UnpackTuple(
        LocalDefn.PatternMatch.UnpackTuple unpackTuple, T arg);

    /**
     * @param unpackListCons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_LocalDefn_PatternMatch_UnpackListCons(
        LocalDefn.PatternMatch.UnpackListCons unpackListCons, T arg);

    /**
     * @param unpackRecord
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_LocalDefn_PatternMatch_UnpackRecord(
        LocalDefn.PatternMatch.UnpackRecord unpackRecord, T arg);

    /**
     * @param defn
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_ModuleDefn(
        ModuleDefn defn, T arg);

    /**
     * @param moduleName
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_Module(
        Name.Module moduleName, T arg);
    
    /**
     * @param qualifier
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_Module_Qualifier(
        Name.Module.Qualifier qualifier, T arg);
    
    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_DataCons(
        Name.DataCons cons, T arg);

    /**
     * @param function
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_Function(
        Name.Function function, T arg);

    /**
     * @param typeClass
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_TypeClass(
        Name.TypeClass typeClass, T arg);

    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_TypeCons(
        Name.TypeCons cons, T arg);

    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_WithoutContextCons(
        Name.WithoutContextCons cons, T arg);

    /**
     * @param name
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_Field(
        Name.Field name, T arg);
    
    /**
     * @param name
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Name_TypeVar(
        Name.TypeVar name, T arg);

    /**
     * @param parameter
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Parameter(
        Parameter parameter, T arg);

    /**
     * @param var
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Pattern_Var(
        Pattern.Var var, T arg);

    /**
     * @param wildcard
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_Pattern_Wildcard(
        Pattern.Wildcard wildcard, T arg);

    /**
     * @param algebraic
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_FunctionDefn_Algebraic(
        FunctionDefn.Algebraic algebraic, T arg);

    /**
     * @param foreign
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_FunctionDefn_Foreign(
        FunctionDefn.Foreign foreign, T arg);

    /**
     * @param primitive
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_FunctionDefn_Primitive(
        FunctionDefn.Primitive primitive, T arg);

    /**
     * @param declaration
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_FunctionTypeDeclaraction(
        FunctionTypeDeclaration declaration, T arg);

    /**
     * @param defn
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_InstanceDefn(
        InstanceDefn defn, T arg);

    /**
     * @param function
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_InstanceDefn_InstanceTypeCons_Function(
        InstanceDefn.InstanceTypeCons.Function function, T arg);

    /**
     * @param list
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_InstanceDefn_InstanceTypeCons_List(
        InstanceDefn.InstanceTypeCons.List list, T arg);
    
    /**
     * @param record
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_InstanceDefn_InstanceTypeCons_Record(
        InstanceDefn.InstanceTypeCons.Record record, T arg);      

    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_InstanceDefn_InstanceTypeCons_TypeCons(
        InstanceDefn.InstanceTypeCons.TypeCons cons, T arg);  
    /**
     * @param unit
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_InstanceDefn_InstanceTypeCons_Unit(
        InstanceDefn.InstanceTypeCons.Unit unit, T arg); 

    /**
     * @param method
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_InstanceDefn_InstanceMethod(
        InstanceDefn.InstanceMethod method, T arg);

    /**
     * @param defn
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeClassDefn(
        TypeClassDefn defn, T arg);

    /**
     * @param defn
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeClassDefn_ClassMethodDefn(
        TypeClassDefn.ClassMethodDefn defn, T arg);

    /**
     * @param type
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeConstructorDefn_AlgebraicType(
        TypeConstructorDefn.AlgebraicType type, T arg);

    /**
     * @param defn
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(
        TypeConstructorDefn.AlgebraicType.DataConsDefn defn, T arg);

    /**
     * @param argument
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeConstructorDefn_AlgebraicType_DataConsDefn_TypeArgument(
        TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument argument, T arg);

    /**
     * @param type
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeConstructorDefn_ForeignType(
        TypeConstructorDefn.ForeignType type, T arg);

    /**
     * @param application
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_Application(
        TypeExprDefn.Application application, T arg);

    /**
     * @param parenthesized
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_Parenthesized(
        TypeExprDefn.Parenthesized parenthesized, T arg);

    
    /**
     * @param function
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_Function(
        TypeExprDefn.Function function, T arg);

    /**
     * @param list
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_List(
        TypeExprDefn.List list, T arg);

    /**
     * @param record
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_Record(
        TypeExprDefn.Record record, T arg);

    /**
     * @param pair
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_Record_FieldTypePair(
        TypeExprDefn.Record.FieldTypePair pair, T arg);

    /**
     * @param tuple
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_Tuple(
        TypeExprDefn.Tuple tuple, T arg);

    /**
     * @param cons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_TypeCons(
        TypeExprDefn.TypeCons cons, T arg);

    /**
     * @param var
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_TypeVar(
        TypeExprDefn.TypeVar var, T arg);

    /**
     * @param unit
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeExprDefn_Unit(
        TypeExprDefn.Unit unit, T arg);

    /**
     * @param signature
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_TypeSignature(
        TypeSignature signature, T arg);

    /**
     * @param module
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_Module(
        CALDoc.Comment.Module module, T arg);

    /**
     * @param function
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_Function(
        CALDoc.Comment.Function function, T arg);

    /**
     * @param typeCons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_TypeCons(
        CALDoc.Comment.TypeCons typeCons, T arg);

    /**
     * @param dataCons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_DataCons(
        CALDoc.Comment.DataCons dataCons, T arg);

    /**
     * @param typeClass
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_TypeClass(
        CALDoc.Comment.TypeClass typeClass, T arg);

    /**
     * @param method
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_ClassMethod(
        CALDoc.Comment.ClassMethod method, T arg);

    /**
     * @param instance
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_Instance(
        CALDoc.Comment.Instance instance, T arg);

    /**
     * @param method
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_Comment_InstanceMethod(
        CALDoc.Comment.InstanceMethod method, T arg);

    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_Plain(
        CALDoc.TextSegment.Plain segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_Preformatted(
        CALDoc.TextSegment.Preformatted segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_URL(
        CALDoc.TextSegment.InlineTag.URL segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_Function(
        CALDoc.TextSegment.InlineTag.Link.Function segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_Module(
        CALDoc.TextSegment.InlineTag.Link.Module segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_TypeCons(
        CALDoc.TextSegment.InlineTag.Link.TypeCons segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_DataCons(
        CALDoc.TextSegment.InlineTag.Link.DataCons segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_TypeClass(
        CALDoc.TextSegment.InlineTag.Link.TypeClass segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_ConsNameWithoutContext(
        CALDoc.TextSegment.InlineTag.Link.ConsNameWithoutContext segment, T arg);

    /**
     * @param reference
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_CrossReference_WithoutContextCons(
        CALDoc.CrossReference.WithoutContextCons reference, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Link_FunctionWithoutContext(
        CALDoc.TextSegment.InlineTag.Link.FunctionWithoutContext segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_Emphasized(
        CALDoc.TextSegment.InlineTag.TextFormatting.Emphasized segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_StronglyEmphasized(
        CALDoc.TextSegment.InlineTag.TextFormatting.StronglyEmphasized segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_Superscript(
        CALDoc.TextSegment.InlineTag.TextFormatting.Superscript segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_TextFormatting_Subscript(
        CALDoc.TextSegment.InlineTag.TextFormatting.Subscript segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Summary(
        CALDoc.TextSegment.InlineTag.Summary segment, T arg);
    
    /**
     * @param segment
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_Code(
        CALDoc.TextSegment.InlineTag.Code segment, T arg);
    
    /**
     * @param list
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_List_Unordered(
        CALDoc.TextSegment.InlineTag.List.Unordered list, T arg);
    
    /**
     * @param list
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_List_Ordered(
        CALDoc.TextSegment.InlineTag.List.Ordered list, T arg);
    
    /**
     * @param item
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextSegment_InlineTag_List_Item(
        CALDoc.TextSegment.InlineTag.List.Item item, T arg);
    
    /**
     * @param block
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TextBlock(
        CALDoc.TextBlock block, T arg);

    /**
     * @param authorBlock
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_Author(
        CALDoc.TaggedBlock.Author authorBlock, T arg);

    /**
     * @param deprecatedBlock
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_Deprecated(
        CALDoc.TaggedBlock.Deprecated deprecatedBlock, T arg);

    /**
     * @param returnBlock
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_Return(
        CALDoc.TaggedBlock.Return returnBlock, T arg);

    /**
     * @param versionBlock
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_Version(
        CALDoc.TaggedBlock.Version versionBlock, T arg);

    /**
     * @param argBlock
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_Arg(
        CALDoc.TaggedBlock.Arg argBlock, T arg);

    /**
     * @param function
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_See_Function(
        CALDoc.TaggedBlock.See.Function function, T arg);

    /**
     * @param reference
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_CrossReference_Function(
        CALDoc.CrossReference.Function reference, T arg);

    /**
     * @param module
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_See_Module(
        CALDoc.TaggedBlock.See.Module module, T arg);

    /**
     * @param reference
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_CrossReference_Module(
        CALDoc.CrossReference.Module reference, T arg);

    /**
     * @param typeCons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_See_TypeCons(
        CALDoc.TaggedBlock.See.TypeCons typeCons, T arg);

    /**
     * @param reference
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_CrossReference_TypeCons(
        CALDoc.CrossReference.TypeCons reference, T arg);

    /**
     * @param dataCons
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_See_DataCons(
        CALDoc.TaggedBlock.See.DataCons dataCons, T arg);

    /**
     * @param reference
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_CrossReference_DataCons(
        CALDoc.CrossReference.DataCons reference, T arg);

    /**
     * @param typeClass
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_See_TypeClass(
        CALDoc.TaggedBlock.See.TypeClass typeClass, T arg);

    /**
     * @param reference
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_CrossReference_TypeClass(
        CALDoc.CrossReference.TypeClass reference, T arg);

    /**
     * @param seeBlock
     *            the source model element to be visited
     * @param arg
     *            additional argument for the visitation
     * @return the result from visiting the source model element
     */
    public R visit_CALDoc_TaggedBlock_See_WithoutContext(
        CALDoc.TaggedBlock.See.WithoutContext seeBlock, T arg);
}
