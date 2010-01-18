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
 * OptimizerHelper.java
 * Created: ??  
 * By: Greg McClement
 */
package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openquark.cal.compiler.Expression.ErrorInfo;
import org.openquark.cal.compiler.ExpressionAnalyzer.Visitor;
import org.openquark.cal.foreignsupport.module.Prelude.AlgebraicValue;
import org.openquark.cal.internal.machine.MachineFunctionImpl;
import org.openquark.cal.internal.module.Cal.Internal.CAL_Optimizer_Expression_internal;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.Program.ProgramException;
import org.openquark.cal.module.Cal.Collections.CAL_Array;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Dynamic;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Core.CAL_String;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.util.FixedSizeList;
import org.openquark.util.UnsafeCast;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * Helper functions that are called from the CAL global optimizer (written in CAL itself).
 * 
 * @author GMCCLEMENT
 */
public final class OptimizerHelper {
    
    /**
     * Used by getLiteralType to tell the CAL code the type of a literal.
     */
    
    public final static int Literal_String = 1;
    public final static int Literal_Int = 2;
    public final static int Literal_Boolean = 3;
    public final static int Literal_Double = 4;
    public final static int Literal_Char = 5;
    public final static int Literal_Byte = 6;
    public final static int Literal_Long = 7;
    public final static int Literal_Opaque = 8;
    public final static int Literal_Integer = 9;
    public final static int Literal_Short = 10;
    public final static int Literal_Float = 11;

    /**
     * Used by getFieldNameType to tell the compiler the type of the field name.
     */
    public final static int FieldName_Ordinal = 1;
    public final static int FieldName_Textual = 2;
    
    /**
     * Used by getExpressionType to tell the CAL code the type of the expression.
     */
    public final static int Expression_Appl = 1;
    public final static int Expression_Cast = 2;
    public final static int Expression_DataConsSelection = 3;
    public final static int Expression_ErrorInfo = 4;
    public final static int Expression_LetRec = 5;
    public final static int Expression_Literal = 6;
    public final static int Expression_PackCons = 7;
    public final static int Expression_RecordCase = 8;    
    public final static int Expression_RecordExtension = 9;
    public final static int Expression_RecordSelection = 10;
    public final static int Expression_Switch = 11;
    public final static int Expression_TailRecursiveCall = 12;
    public final static int Expression_Var = 13;
    public final static int Expression_LetNonRec = 14;
    public final static int Expression_RecordUpdate = 15;
    
    /**
     * Used by getAltType to tell the CAL optimizer the type of a switch alt.
     */
    public final static int Alt_Matching = 1;
    public final static int Alt_Positional = 2;

    /**
     * Used by var_getType to tell the CAL optimizer the type of the var.
     */
    public final static int Var_Type_DataConstructor = 1;
    public final static int Var_Type_FunctionEntity = 2;
    public final static int Var_Type_Name = 3;
    
    /**
     * Used by getTypeType to tell the CAL optimizer the type of the type expression.
     */
    public static final int TypeExpr_TypeConstructor = 1;
    public static final int TypeExpr_TypeVar = 2;
    public static final int TypeExpr_Other = 3;

    /** 
     * Used by getTypeConstructorType to tell the CAL optimizer the type of the type constructor.
     */
    public final static int TypeConstructor_Function = 1;
    public final static int TypeConstructor_List = 2;
    public final static int TypeConstructor_Other = 3;

    
    /**
     * This will only be initialized if the optimizer is enabled.
     */
    private static PreludeTypeConstants preludeTypeConstants = null;
    
    /** This class is not intended to be instantiated */
    private OptimizerHelper() {}
    
    public static void setPreludeTypeConstants(PreludeTypeConstants preludeTypeConstants){
        OptimizerHelper.preludeTypeConstants = preludeTypeConstants;
    }
    
    /**
     * Make a list type expression with elements of the given type.
     *  
     * @param elementType The type of the list elements
     * @return A type expression for lists with the given element type.
     */
    public static TypeExpr makeListType(TypeExpr elementType){
        return preludeTypeConstants.makeListType(elementType);
    }
    
    /**
     * The optimizer currently does not support certain types of expressions. This exception
     * is thrown when an expression with components of unacceptable type are passed to the optimizer.
     * This is temporary.
     * 
     * @author GMCCLEMENT
     *
     */
    public static class UnsupportedExpressionTypeException extends RuntimeException {
        /**
         * Fix a warning
         */
        private static final long serialVersionUID = 4888880848260406510L;
        private final String reason;
        
        UnsupportedExpressionTypeException(String reason){
            this.reason = reason;
        }
        
        public String getReason(){
            return reason;
        }
    }
    
    /**
     * Used to get a type code number for the literal by code that inputs the Java object
     * into CAL.
     * 
     * @param literal The literal to get the type code for.
     * @return An integer indicating the type of the literal.
     */
    public static int getLiteralType(Expression.Literal literal){
        Object value = literal.getLiteral();
        
        if (value instanceof String){
            return Literal_String;
        }
        
        if (value instanceof Integer){
            return Literal_Int;
        }
        
        if (value instanceof Boolean){
            return Literal_Boolean;
        }
        
        if (value instanceof Double){
            return Literal_Double;
        }
        
        if (value instanceof Character){
            return Literal_Char;
        }
        
        if (value instanceof Byte){
            return Literal_Byte;
        }

        if (value instanceof Long){
            return Literal_Long;
        }
        
        if (value instanceof BigInteger){
            return Literal_Integer;
        }
        
        if (value instanceof Short){
            return Literal_Short;
        }
        
        if (value instanceof Float){
            return Literal_Float;
        }
        
        return Literal_Opaque;
    }

    /**
     * Used to get a type code number for the field name by code that inputs the Java object
     * into CAL.
     * 
     * @param fieldName The field name to get the type code for.
     * @return An integer indicating the type of the field name.
     */
    public static int getFieldNameType(FieldName fieldName){
        if (fieldName instanceof FieldName.Ordinal){
            return FieldName_Ordinal;
        }
        if (fieldName instanceof FieldName.Textual){
            return FieldName_Textual;
        }
        
        throw new IllegalStateException();
    }
    
    /**
     * Get the ordinal of the given field name. If the field name is not 
     * of type FieldName.Ordinal then an error will be throw.
     * 
     * @param fn The FieldName.Ordinal to get the ordinal of.
     * @return The ordinal of the field name.
     */
    
    public static int fieldName_getOrdinal(FieldName fn){
        FieldName.Ordinal ordinal = (FieldName.Ordinal) fn;
        return ordinal.getOrdinal();
    }

    /**
     * Used to get a type code number for the expression by code that inputs the Java object
     * into CAL.
     * 
     * @param expression The field name to get the type code for.
     * @return An integer indicating the type of the field name.
     */
    
    public static int getExpressionType(Expression expression){
        if (expression instanceof Expression.Appl){
            return Expression_Appl;
        }
        else if (expression instanceof Expression.Cast){            
            return Expression_Cast;
        }
        else if (expression instanceof Expression.DataConsSelection){            
            return Expression_DataConsSelection;
        }
        else if (expression instanceof Expression.ErrorInfo){            
            return Expression_ErrorInfo;
        }
        else if (expression instanceof Expression.LetNonRec){
            return Expression_LetNonRec;
        }
        else if (expression instanceof Expression.LetRec){
            if ( ((Expression.Let) expression).getDefns().length > 1){
                // TODO fix this
                throw new UnsupportedExpressionTypeException("LetRec with more than once definition.");
            }

            return Expression_LetRec;
        }
        else if (expression instanceof Expression.Literal){            
            return Expression_Literal;
        }
        else if (expression instanceof Expression.PackCons){            
            return Expression_PackCons;
        }
        else if (expression instanceof Expression.RecordCase){            
            return Expression_RecordCase;
        }
        else if (expression instanceof Expression.RecordUpdate) {
            return Expression_RecordUpdate;
        }
        else if (expression instanceof Expression.RecordExtension){            
            return Expression_RecordExtension;
        }
        else if (expression instanceof Expression.RecordSelection){            
            return Expression_RecordSelection;
        }
        else if (expression instanceof Expression.Switch){            
            return Expression_Switch;
        }
        else if (expression instanceof Expression.TailRecursiveCall){            
            return Expression_TailRecursiveCall;
        }
        else if (expression instanceof Expression.Var){            
            return Expression_Var;
       }

        throw new IllegalStateException();
    }
    
    /**
     * Construct a new Expression.Var from a qualified name;
     * @param name The name of the variable.
     * @return The Var for the given name.
     */
    public static Expression expression_new_var_name(QualifiedName name){
        return new Expression.Var(name);
    }

    /**
     * Construct a new Expression.Var from an FunctionalAgent
     * @param entity The entity to use to construct the Var.
     * @return The Var constructed.
     */            
    public static Expression expression_new_var_entity(FunctionalAgent entity){
        return new Expression.Var(entity);
    }

    /**
     * Construct an Expression.Literal from the given java object.
     * @param literal The java object to put in the literal.
     * @return The literal for the given java object.
     */
    public static Expression expression_new_literal(Object literal){
        return new Expression.Literal(literal);
    }

    /**
     * Construct an Expression.Appl from the given expressions.
     * @param expr1 The first expression to use.
     * @param expr2 The second expression to use.
     * @return The application for the given expressions.
     */
    public static Expression expression_new_appl(Expression expr1, Expression expr2){
        return new Expression.Appl(expr1, expr2);
    }


    /**
     * Construct an Expression.Let object.
     * 
     * @param unqualifiedName The name of the let variable.
     * @param defExpr The expression that defined the let variable.
     * @param bodyExpr The body of the let expression.
     * @param isRecursive True if the expression being defined is recursive.
     * @return The new Let expression object.
     */    
    public static Expression expression_new_let(String unqualifiedName, Expression defExpr, Expression bodyExpr, boolean isRecursive, TypeExpr varType){
        Expression.Let.LetDefn defns[] = { new Expression.Let.LetDefn(unqualifiedName, defExpr, varType) };
        if (isRecursive){                  
            return new Expression.LetRec(defns, bodyExpr);
        }
        else{
            return new Expression.LetNonRec(defns[0], bodyExpr);
        }
    }

    /**
     * Create a new Switch expression for the given parameters.
     * @param expression The case expression for the switch.
     * @param altsObject A list of the SwitchAlts for the switch.
     * @return The Switch expression.
     */    
    public static Expression expression_new_switch(Expression expression, Object altsObject){
        List<Expression.Switch.SwitchAlt> altsList = UnsafeCast.unsafeCast(altsObject);
        Expression.Switch.SwitchAlt alts[] = new Expression.Switch.SwitchAlt[altsList.size()];
        int iAlts = 0;
        for(Iterator<Expression.Switch.SwitchAlt> i = altsList.iterator(); i.hasNext();){
            alts[iAlts++] = i.next();
        }
      
        /* TODO Add the error info back in */
        return new Expression.Switch(expression, alts, new ErrorInfo(QualifiedName.make(ModuleName.make("OptimizerHelper"), "todo"), 1, 1));

    }

    public static Expression expression_new_opaque(Object expression){
        return (Expression) expression;
    }

    /**
     * Construct a new Expression.Var for the given data constructor.
     * @param dataConstructor The data constructor that the Var will hold.
     * @return A new Var for the given data constructor.
     */
    public static Expression expression_new_dataConstructor(Object dataConstructor){
        return new Expression.Var( (DataConstructor) dataConstructor);
    }

    /**
     * Construct a SwitchAlt object for the given CAL object values.
     * @param switchTag The tag of the switch alt.
     * @param isPositional A flag indicating if the alt is positional.
     * @param varsObject A list of the vars. 
     * @param altExpr The expression body.
     * @return A SwitchAlt for the given CAL parameters.
     */
    
    public static Expression.Switch.SwitchAlt alt_new(Object switchTag, boolean isPositional, Object varsObject, Expression altExpr){   
        SortedMap<FieldName, String> fieldNameToVarNameMap = new TreeMap<FieldName, String>();
        SortedMap<Integer, String> ordinalToVarNameMap = new TreeMap<Integer, String>();
        {
            List<List<Object>> varsList = UnsafeCast.unsafeCast(varsObject);
            for(Iterator<List<Object>> iVarsList = (varsList).iterator(); iVarsList.hasNext();){
                List<Object> fieldNameQualifiedName = iVarsList.next();
                Iterator<Object> fieldNameQualifiedNameIterator = fieldNameQualifiedName.iterator();
                FieldName fn = (FieldName) fieldNameQualifiedNameIterator.next();
                QualifiedName qn = (QualifiedName) fieldNameQualifiedNameIterator.next();
                String name = qn.getUnqualifiedName();
                if (fn instanceof FieldName.Ordinal){
                    FieldName.Ordinal ordinal = (FieldName.Ordinal) fn;
                    // Ordinals start at zero but field name start at one so we have to 
                    // take the number back one.
                    ordinalToVarNameMap.put(Integer.valueOf(ordinal.getOrdinal() - 1), name);
                    isPositional = true;
                }
                else{
                    // TODO GregM Rethink if this is the right way to handle positional versus matching.
                    fieldNameToVarNameMap.put(fn, name);
                    isPositional = false;
                }
            }
        }
        
        if (isPositional){
            return new Expression.Switch.SwitchAlt.Positional(switchTag, ordinalToVarNameMap, altExpr);
        }
        else{
            return new Expression.Switch.SwitchAlt.Matching(switchTag, fieldNameToVarNameMap, altExpr);
        }
    }

    /**
     * Used to get a type code number for the alt by code that inputs the Java object
     * into CAL.
     * 
     * @param alt The alt to get the type code for.
     * @return An integer indicating the type of the alt.
     */
    public static int getAltType(Expression.Switch.SwitchAlt alt){
        if (alt instanceof Expression.Switch.SwitchAlt.Matching){
            return Alt_Matching;
        }
        else if (alt instanceof Expression.Switch.SwitchAlt.Positional){
            return Alt_Positional;
        }
        throw new IllegalStateException();
    }

    /**
     * Is the tag of the alt a data constructor.
     * @param alt The alt to check the tag of.
     * @return True if the first tag is a data constructor.
     */
    public static boolean alt_tagIsDataConstructor(Expression.Switch.SwitchAlt alt){
        Object tag = alt.getFirstAltTag();
        if (tag instanceof DataConstructor){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Is the given object a data constructor.
     * @param object The object to perform the check on.
     * @return True if the object is a data constructor.
     */
    public static boolean object_isDataConstructor(Object object){
        if (object instanceof DataConstructor){
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * The first tag of the alt.
     * @param alt The alt to get the tag of.
     * @return The first tag.
     */    
    public static FunctionalAgent alt_getFirstTag_asJFunctionalAgent(Expression.Switch.SwitchAlt alt){
        return (FunctionalAgent) alt.getFirstAltTag();
    }

    
    /**
     * The first tag of the alt.
     * @param alt The alt to get the tag of.
     * @return The first tag.
     */    
    public static Expression.Literal alt_getFirstTag_asJLiteral(Expression.Switch.SwitchAlt alt){
        return new Expression.Literal(alt.getFirstAltTag());
    }
    
    /**
     * Used for converting from a Java Var expression to a CAL expression.
     * @param var The var object to get the type of
     * @return A code indicating the type of the Var.
     */
    public static int var_getType(Expression.Var var){
        Object entity = var.getFunctionalAgent();
        if (entity != null){
            if (entity instanceof DataConstructor){
                return Var_Type_DataConstructor;
            }
            else if (entity instanceof Function){
                return Var_Type_FunctionEntity;
            }
            else{
                throw new IllegalStateException();
            }
        }
        else{
            return Var_Type_Name;
        }
    }
    
    /**
     * Construct a new unconstrained type var.
     * @return The new type var.
     */
    public static TypeVar newTypeVar(){
        return new TypeVar();
    }
    
    /**
     * Get the number of arguments for the alt pattern.
     * 
     * @param alt The SwitchAlt object
     * @return The number of arguments for this alt pattern.
     */
    public static int alt_getNArguments(Expression.Switch.SwitchAlt alt){
        Object tag = alt.getFirstAltTag();
        if (tag instanceof DataConstructor){
            DataConstructor dctag = (DataConstructor) tag;
            return dctag.getArity();
        }
        else {
            return 0;
        }
    }

    /**
     * Get the Nth argument from the position to var map for a Positional switch alt.
     * @param altObject
     * @param index The index of the argument to get.
     * @return The nth argument.
     */
    
    public static String alt_getPositionArgument(Expression.Switch.SwitchAlt altObject, int index){
        Expression.Switch.SwitchAlt.Positional alt = (Expression.Switch.SwitchAlt.Positional) altObject;
        SortedMap<Integer, String> map = alt.getPositionToVarNameMap();
        String varName = map.get(Integer.valueOf(index));
        if (varName == null){
            return "";
        }
        else{
            return varName;
        }
    }

    public static Expression let_getDef(Expression.Let letObject, int index){
        return letObject.getDefns()[index].getExpr();
    }
    
    public static String let_getVar(Expression.Let letObject, int index){
        return letObject.getDefns()[index].getVar();
    }
    
    public static TypeExpr let_getDefType(Expression.Let letObject, int index){
        return letObject.getDefns()[index].getVarType();
    }
    
    public static TypeExpr objectAsTypeExpr(TypeVar object){
        return object;
    }
    
    /**
     * Used to get a type code number for the TypeExpr by code that inputs the Java object
     * into CAL.
     * 
     * @param type The type expression to get the type code for.
     * @return An integer indicating the type of the type expression.
     */
    public static int getTypeType(TypeExpr type){
        if (type instanceof TypeConsApp) {
            return TypeExpr_TypeConstructor;
        } else if (type instanceof TypeVar) {
            return TypeExpr_TypeVar;
        } else {
            return TypeExpr_Other;
        }
    }

    public static TypeVar typeExprAsJTypeVar(TypeExpr type){
        return (TypeVar) type;
    }
    
    public static Object typeExprAsJObject(TypeExpr type){
        return type;
    }
    
    public static String typeExpr_getName(TypeExpr type){
        if (type == null){
            return "null";
        }
        else{
            return type.toString();
        }
    }
    
    /**
     * Used to get a type code number for the TypeConsApp by code that inputs the Java object
     * into CAL.
     * 
     * @param type The type constructor expression to get the type code for.
     * @return An integer indicating the type of the type constructor expression.
     */
    public static int getTypeConstructorType(TypeConsApp type){
        QualifiedName typeName = type.getRoot().getName();
        if (typeName.equals(CAL_Prelude.TypeConstructors.Function)) {
            if (TypeConstructor.FUNCTION != type.getRoot()){
                System.out.println("Doesn't Match Root");
            }
            return TypeConstructor_Function;
        } else if (typeName.equals(CAL_Prelude.TypeConstructors.List)) {            
            return TypeConstructor_List;
        }
        else{
            return TypeConstructor_Other;
        }
    }

    public static TypeConsApp typeExpr_asTypeConstructor(TypeExpr typeExpr){
        return (TypeConsApp) typeExpr;
    }
    
    public static boolean typeVar_hasClassConstraints(TypeVar typeVar){
        return !typeVar.noClassConstraints();
    }
    
    public static TypeExpr functionalAgent_getTypeExprExact(FunctionalAgent functionalAgent){
        return (functionalAgent).getTypeExprExact();
    }
    
    /**
     * Construct a new Expression.RecordSelection.
     * @param expression The record expression.
     * @param fieldName The field name.
     * @return A new record selection expression for the given record expression and field name.
     */
    public static Expression expression_new_recordSelection(Expression expression, FieldName fieldName){
        return new Expression.RecordSelection(expression, fieldName);
    }

    /**
     * Construct a new Expression.RecordSelection.
     * @param conditionExpr The record case expression.
     * @param baseRecordPatternVarName The pattern var name for the record case.
     * @param fieldBindingVars The map from FieldName to Var name.
     * @param resultExpr The result expression for the record case.
     * @return A new record case expression for the given record expression and field name.
     */
    public static Expression expression_new_recordCase(Expression conditionExpr, String baseRecordPatternVarName, Object fieldBindingVars, Expression resultExpr){
        List<List<Object>> fieldsList = UnsafeCast.unsafeCast(fieldBindingVars);
        return new Expression.RecordCase(conditionExpr, baseRecordPatternVarName, getFieldBindingVarMap(fieldsList), resultExpr);
    }
    
    private static SortedMap<FieldName, String> getFieldBindingVarMap(List<List<Object>> extensionFieldsList){
        SortedMap<FieldName, String> extensionFieldsMap = new TreeMap<FieldName, String>();
        for (final List<Object> ef : extensionFieldsList) {
            Iterator<Object> efIterator = ef.iterator();
            FieldName fieldName = (FieldName) efIterator.next();
            String varName = (String) efIterator.next();
            extensionFieldsMap.put(fieldName, varName);
        }
        
        return extensionFieldsMap;
    }
    

    public static RecordType type_asRecordType(TypeExpr type){
        return (RecordType) type;
    }    

    public static FunctionalAgent dataConstructor_asFunctionalAgent(DataConstructor functionalAgent){
        return functionalAgent;
    }
    
    public static DataConstructor functionalAgent_asDataConstructor(FunctionalAgent functionalAgent){
        return (DataConstructor) functionalAgent;
    }
    
    /**
     * Construct a new Expression.DataConsSelection.
     * @param dcValueExpr The expression.
     * @param dataConstructor
     * @param fieldIndex
     * @param errorInfo
     * @return The new DataConsSelection.
     */
    public static Expression expression_new_dataConsSelection(Expression dcValueExpr, FunctionalAgent dataConstructor, int fieldIndex, ErrorInfo errorInfo){
        return new Expression.DataConsSelection(dcValueExpr, (DataConstructor) dataConstructor, fieldIndex, errorInfo);
    }

    private static SortedMap<FieldName, Expression> getExtensionFieldsMap(List<List<Object>> extensionFieldsList){
        SortedMap<FieldName, Expression> extensionFieldsMap = new TreeMap<FieldName, Expression>();
        for (final List<Object> ef : extensionFieldsList) {
            Iterator<Object> efIterator = ef.iterator();
            FieldName fieldName = (FieldName) efIterator.next();
            Expression expression = (Expression) efIterator.next();
            extensionFieldsMap.put(fieldName, expression);
        }
        
        return extensionFieldsMap;
    }
    
    public static Expression expression_new_recordExtensionLiteral(Object extensionFields){
        List<List<Object>> efList = UnsafeCast.unsafeCast(extensionFields);
        return new Expression.RecordExtension(null, getExtensionFieldsMap( efList));
    }
    
    public static Expression expression_new_recordExtensionPolymorphic(Expression baseRecordExpr, Object extensionFields){
        List<List<Object>> efList = UnsafeCast.unsafeCast(extensionFields);
        return new Expression.RecordExtension(baseRecordExpr, getExtensionFieldsMap(efList));
    }    
    
    public static FieldName fieldName_ordinal_new(int ordinal){
        return FieldName.makeOrdinalField(ordinal);
    }
    
    public static FieldName fieldName_textual_new(String text){
        return FieldName.makeTextualField(text);
    }
    
    /**
     * Checks if the expression contains Expression types no supported by the CAL optimizer.
     * @param expr The expression to check
     * @return True if the expression contains unsupported types.
     */
    
    static boolean hasExcludedTypes(Expression expr) {
        class HasExcludedTypes extends Visitor {
            public boolean found = false;

            HasExcludedTypes(){
            }

            @Override
            void enterPackCons(Expression.PackCons packCons) { 
                found = true; 
            }
            @Override
            void enterRecordCase(Expression.RecordCase recordCase) { 
                found = true; 
            }
            @Override
            void enterCast(Expression.Cast cast) { 
                found = true; 
            }
        }

        HasExcludedTypes collector = new HasExcludedTypes();
        expr.walk(collector);
        return collector.found;
    }
    
    /**
     * Converts the given core function into an algebraic value usable within the CAL optimizer.
     * 
     * @param cf The core function to get the algebraic value for.
     * @return An algebraic value corresponing to the given core function.
     */
    static AlgebraicValue getAlgebraicValue(CoreFunction cf){
        if (hasExcludedTypes(cf.getExpression())){
            return null;
        }
        
        // Initialize the type list
        TypeExpr calTypesArray[] = cf.getType();
        
        // If the arguments have null for a type then skip this body.
        for(int i = 0; i < calTypesArray.length; ++i){
            if (calTypesArray[i] == null){
                return null;
            }
        }
        
        List<TypeExpr> calType = new ArrayList<TypeExpr>(calTypesArray.length);
        for(int i = 0; i < calTypesArray.length; ++i){
            calType.add(calTypesArray[i]);
        }
        
        boolean[] strictness = cf.getParameterStrictness();
        List<Boolean> calStrictness = new ArrayList<Boolean>(strictness.length);
        {
            for(int i = 0; i < strictness.length; ++i){
                calStrictness.add(Boolean.valueOf(strictness[i]));
            }
        }
        
        boolean[] argIsWHNF = cf.getParameterStrictness();
        List<Boolean> calArgIsWHNF = new ArrayList<Boolean>(argIsWHNF.length);
        {
            for(int i = 0; i < strictness.length; ++i){
                calArgIsWHNF.add(Boolean.valueOf(false));
            }
        }

        ArrayList<QualifiedName> calParameters = new ArrayList<QualifiedName>(cf.getFormalParameters().length);
        for(int i = 0; i < cf.getFormalParameters().length; ++i){
            calParameters.add(QualifiedName.make(cf.getName().getModuleName(), cf.getFormalParameters()[i]));
        }
        
        // create the helper functions using the name to
        // type map
        QualifiedName bodyVar = cf.getName();
        Expression calExpr = cf.getExpression();
        
        final int Optimizer_calType_CF = 0;

        return AlgebraicValue.makeGeneralAlgebraicValue(
                CAL_Optimizer_Expression_internal.DataConstructors.CoreFunction,
                Optimizer_calType_CF,
                FixedSizeList.make(
                        bodyVar,
                        calParameters,
                        calExpr,
                        calType,
                        calStrictness,
                        calArgIsWHNF
                )
        );
    }

    /**
     * Lookup the given function name and return an algebraic value based of the function definition.
     * @param workspaceManager The workspace to look for the function definition in.
     * @param var The name of the function to look for.
     * @return An algebraic value for the core function that is usable by the optimizer.
     */
    
    public static AlgebraicValue getCoreFunction(WorkspaceManager workspaceManager, QualifiedName var){
        MachineFunction mf = null;
        try {
            mf = workspaceManager.getMachineFunction(var);
        } catch (ProgramException e) {
            // TODO FIX THIS
            e.printStackTrace();
        }
        if (mf == null){
            // Fix your test app so it does not have unhandled expression types.
            throw new IllegalStateException();
        }
    
        return getAlgebraicValue(((MachineFunctionImpl)mf).getCoreFunction());        
    }
    
    /**
     * Lookup the given function name and return an algebraic value based of the function definition.
     * @param workspaceManager The workspace to look for the function definition in.
     * @param var The name of the function to look for.
     * @return An algebraic value for the core function that is usable by the optimizer.
     */
    
    public static AlgebraicValue getTestInput(WorkspaceManager workspaceManager, QualifiedName var){
        MachineFunction mf = null;
        try {
            mf = workspaceManager.getMachineFunction(var);
        } catch (ProgramException e) {
            // TODO FIX THIS
            e.printStackTrace();
        }
        if (mf == null){
            // Fix your test app so it does not have unhandled expression types.
            throw new IllegalStateException();
        }
    
        CoreFunction cf = ((MachineFunctionImpl)mf).getCoreFunction();
        
        AlgebraicValue coreFunction = getAlgebraicValue(cf);
        if (coreFunction == null){
            // Fix your test app so it does not have unhandled expression types.
            throw new IllegalStateException();
        }
        
        List<List<Object>> nameToTypeExpr = new LinkedList<List<Object>>();
        LinkedList<AlgebraicValue> helperFunctions = new LinkedList<AlgebraicValue>();
        List<QualifiedName> nonCalFunctions = new ArrayList<QualifiedName>();
        try {
            OptimizerHelper.getHelperFunctions(null, workspaceManager, new HashMap<QualifiedName, CoreFunction>(), cf, nameToTypeExpr, helperFunctions, nonCalFunctions);
        } catch (ProgramException e) {
            // TODO FIX THIS
            e.printStackTrace();
        }


        /*
         * Make a list of the parameter strictness flags.
         */
        
        boolean strictness[] = cf.getParameterStrictness();
        List<Boolean> strictnessList = new ArrayList<Boolean>(strictness.length);
        {
            for( int i = 0; i < strictness.length; ++i){
                strictnessList.add(Boolean.valueOf(strictness[i]));
            }
        }
        
        /*
         * Make a list of the formal parameters of the expression.
         */
        
        String formalParameters[] = cf.getFormalParameters();
        List<QualifiedName> formalParametersList = new ArrayList<QualifiedName>(formalParameters.length);
        ModuleName optimizerTestModule = ModuleName.make("Cal.Test.Internal.Optimizer_Test");
        for(int i = 0; i < formalParameters.length; ++i){
            formalParametersList.add(QualifiedName.make(optimizerTestModule, formalParameters[i]));
        }
        
//        return new TestInput(nameToTypeExpr, helperFunctions, cf.getExpression());
        return AlgebraicValue.makeGeneralAlgebraicValue(
                QualifiedName.make(optimizerTestModule, "TestInput"),
                0,
                FixedSizeList.make(
                        nameToTypeExpr, 
                        helperFunctions,
                        nonCalFunctions,
                        formalParametersList,
                        strictnessList,
                        cf.getExpression()
                ));
    }

    private static final QualifiedName PRELUDE_SEQ = CAL_Prelude.Functions.seq;

    /**
     * @param program
     * @param workspace
     * @param inlinableFunctions A list of extra functions to look at when searching for a given function. Map of QualifiedName to CoreFunction
     * @param cf
     * @param nameToTypeExpr
     * @param helperFunctions
     * @param nonCalFunctions
     * @throws ProgramException
     */
    
    static void getHelperFunctions(Program program, WorkspaceManager workspace, Map<QualifiedName, CoreFunction> inlinableFunctions, CoreFunction cf, List<List<Object>> nameToTypeExpr, LinkedList<AlgebraicValue> helperFunctions, List<QualifiedName> nonCalFunctions) throws ProgramException{
        
        QualifiedName name = cf.getName();
        Expression body = cf.getExpression();
        Set<Object> varsDone = variables(body);
        LinkedList<Object> varsUsed = new LinkedList<Object>(varsDone);
        varsUsed.add(PRELUDE_SEQ); // The optimizer can add calls to this 
        // Keep track of the types already in the nameToTypeList
        Set<QualifiedName> alreadyGotTypes = new HashSet<QualifiedName>();
        int numberOfFunctionsNeeded = 0;
        // initialize name to type map
        while (varsUsed.size() > 0) {
            QualifiedName var = (QualifiedName) varsUsed.removeFirst();
            CoreFunction iBody = null;
            // Look for the function the the previously compiled modules.
            MachineFunction iMF = program == null ? workspace.getMachineFunction(var) : program.getCodeLabel(var);
            if (iMF != null){
                iBody = ((MachineFunctionImpl)iMF).getCoreFunction();
            }
            
            if (iBody == null){
                // Look for the function in the module currently being compiled.
                iBody = inlinableFunctions.get(var);                            
            }

            // if iBody is still null then the symbol found was perhaps an argument and not a function name.
            if (iBody != null) {
                if (
                        iBody.isCALFunction() &&
                        !iBody.getHadUnsafeCoerce() &&
//                        iBody.getFormalParameters().length > 0 &&
                        // if the function calls itself don't include itself in the helper functions
                        // since we are optimizing that function already.
                        iBody.getName() != name &&  
                        iBody.getExpression() != null){
                    getCALTypes(iBody, nameToTypeExpr, alreadyGotTypes);

                    AlgebraicValue coreFunction = OptimizerHelper.getAlgebraicValue(iBody);
                    if (coreFunction == null){
                        continue;
                    }
                    
                    if (iBody.getName().getQualifiedName().startsWith("$dict")){
                        continue;
                    }
                    
                    /*
                     * What I really want to do is not use functions that call unsafe coerce in the body
                     * this is just temporary so I can get some timings
                     */
                     
                    if (iBody.getName().getQualifiedName().equals(CAL_Array.Functions.subscript.getQualifiedName())){
                        continue;
                    }
                    
                    helperFunctions.addFirst(coreFunction);                        

                    // add the functions used by this body to the
                    // inlined list iff the function is defined
                    // in this module since the functions in this 
                    // module are not optimized yet. The hard coded 
                    // names are here because the optimizer currently
                    // does not optimize these functions.

                    if (iBody.getName().getModuleName().equals(name.getModuleName()) ||
                            iBody.getName().getModuleName().equals(CAL_Prelude.MODULE_NAME) ||
                            iBody.getName().getModuleName().equals(CAL_List.MODULE_NAME) ||
                            iBody.getName().getModuleName().equals(CAL_String.MODULE_NAME) ||
                            iBody.getName().getModuleName().equals(CAL_Dynamic.MODULE_NAME) ||
                            iBody.getName().getModuleName().equals(CAL_Debug.MODULE_NAME)
                        ){
                        Set<Object> moreVars = variables(iBody.getExpression());
                        for (final Object mv : moreVars) {
                            if (varsDone.contains(mv)) {
                                continue; // already done
                            } else {
                                varsDone.add(mv);
                                varsUsed.add(mv);
                            }
                        }
                    }
                    else{
                        {
                            // Add type information for any variables used by this function. 
                            Set<Object> moreVars = variables(iBody.getExpression());
                            for (final Object object : moreVars) {
                                QualifiedName mv = (QualifiedName) object;
                                if (varsDone.contains(mv) || varsUsed.contains(mv)) {
                                    continue; // already done or is going to be done
                                } else {
                                    CoreFunction mvBody = null;
                                    // Look for the function the the previously compiled modules.
                                    MachineFunction mvMF = program == null ? workspace.getMachineFunction(mv) : program.getCodeLabel(mv);
                                    if (mvMF != null){
                                        mvBody = ((MachineFunctionImpl)mvMF).getCoreFunction();
                                    }
                                    
                                    if (mvBody == null){
                                        // Look for the function in the module currently being compiled.
                                        mvBody = inlinableFunctions.get(mv);                            
                                    }
                                    
                                    // if mvBody is still null then the symbol found was perhaps an argument and not a function name.
                                    if (mvBody != null) {
                                        getCALType(mvBody, nameToTypeExpr, alreadyGotTypes);
                                    }
                                }
                            }
                        }
                    }
                    
                    numberOfFunctionsNeeded++;
                }
                else{
                    // Add an entry for the type of this function for any recursive calls.
                    getCALType(iBody, nameToTypeExpr, alreadyGotTypes);
                    if (!iBody.isCALFunction()){
                        nonCalFunctions.add(iBody.getName());
                    }
                }
            }
            else{
            }
        }
        // Add an entry for the type of this function for any recursive calls.
        getCALTypes(cf, nameToTypeExpr, alreadyGotTypes);
    }


    /**
     * Build a list of all the Expression.Var objects in the given expression.
     * 
     * @param expr
     *            The expression to get all the variable in.
     * @return A list of Expression.Var objects that appear in the given
     *         expression.
     */

    private static Set<Object> variables(Expression expr) {
        class VariableCollector extends Visitor {
            private Set<Object> variables = new HashSet<Object>();

            @Override
            void enterVar(Expression.Var var) {
                QualifiedName name = var.getName();
                if (name != null) {
                    variables.add(name);
                }
            }

            Set<Object> getVariables() {
                return variables;
            }
        }

        VariableCollector collector = new VariableCollector();
        expr.walk(collector);

        return collector.getVariables();
    }

    /**
     * Returns the function type of the given core function in a form that can be passed to the CAL Optimizer.
     *  
     * @param cf The core function to get the type for.
     * @param nameToTypeList The list of name to type objects.
     * @param alreadySeenTypes The list of objects names already in the nameToTypeList
     * return The function type in a form that can be passed to the CAL optimizer.
     */
    private static void getCALType(CoreFunction cf, List<List<Object>> nameToTypeList, Set<QualifiedName> alreadySeenTypes){
        if (alreadySeenTypes.contains(cf.getName())){
            return;
        }
        else{
            alreadySeenTypes.add(cf.getName());
        }
        List<Object> nameToType = new ArrayList<Object>(3);
        
        // Name
        nameToType.add(cf.getName());
        
        // Type
        TypeExpr calTypesArray[] = cf.getType();
        List<Object> calType = new ArrayList<Object>(calTypesArray.length);
        for(int i = 0; i < calTypesArray.length; ++i){
            calType.add(calTypesArray[i]);
        }
        nameToType.add(calType);
        
        // Strictness
        boolean[] strictness = cf.getParameterStrictness();
        List<Object> calStrictness = new ArrayList<Object>(strictness.length);
        {
            for(int i = 0; i < strictness.length; ++i){
                calStrictness.add(Boolean.valueOf(strictness[i]));
            }
        }
        nameToType.add(calStrictness);
        
        nameToTypeList.add(nameToType);
    }

    /**
     * Add mappings of symbols to type for the core function and all of its arguments.
     *  
     * @param cf The core function to get the types from.
     * @param nameToTypeList List to add the name to type tuples to.
     * @param alreadySeenTypes A list of the names already in the nameToTypeList.
     */
    private static void getCALTypes(CoreFunction cf, List<List<Object>> nameToTypeList, Set<QualifiedName> alreadySeenTypes){
        getCALType(cf, nameToTypeList, alreadySeenTypes);

        final ModuleName moduleName = cf.getName().getModuleName();
        
        // Get name to type tuples for the parameters.
        String[] parameterNames = cf.getFormalParameters();
        TypeExpr calTypesArray[] = cf.getType();
        boolean[] strictness = cf.getParameterStrictness();
        for(int i = 0; i < parameterNames.length; ++i){
            final QualifiedName name = QualifiedName.make(moduleName, parameterNames[i]);

            if (alreadySeenTypes.contains(name)){
                continue;
            }
            else{
                alreadySeenTypes.add(name);
            }
                    
            List<Object> nameToType = new ArrayList<Object>(3);
            
            // Name
            nameToType.add(name);
            
            // Type
            {
                List<Object> calType = new ArrayList<Object>(calTypesArray.length);
                calType.add(calTypesArray[i]);
                nameToType.add(calType);
            }
            
            // Strictness
            {
                List<Object> calStrictness = new ArrayList<Object>(strictness.length);
                calStrictness.add(Boolean.valueOf(strictness[i]));
                nameToType.add(calStrictness);
            }
            
            nameToTypeList.add(nameToType);
        }
    }

    /**
     * Get the number of data constructors defined in the type of the given data constructor.
     * @param dc The data constructor to get the number for. 
     * @return The number of data constructors defined in the type of the given data constructor.
     */
    public static int dataConstructor_getNumberOfDataTypes(DataConstructor dc){
        if (dc.getNArgumentNames() > 0){
            return dc.getTypeConstructor().getNDataConstructors();    
        }
        return dc.getTypeConstructor().getNDataConstructors();
    }
    
    /**
     * True iff the given FunctionalAgent is Prelude.True.
     * @param functionalAgent - Check whether or not this value is Prelude.True.
     * @return True iff the given EntEntity is Prelude.True.
     */
    public static boolean dataCons_isTrue(FunctionalAgent functionalAgent){
        return functionalAgent.getName().equals(CAL_Prelude.DataConstructors.True);
    }
    
    /**
     * True iff the given FunctionalAgent is Prelude.False.
     * @param functionalAgent - Check whether or not this value is Prelude.False.
     * @return True iff the given EntEntity is Prelude.False.
     */
    public static boolean dataCons_isFalse(FunctionalAgent functionalAgent){
        return functionalAgent.getName().equals(CAL_Prelude.DataConstructors.False);
    }
}
