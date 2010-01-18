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
 * RelativeDateValueNode.java
 * Creation date: (04/06/01 10:48:36 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.util.UnsafeCast;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;

/**
 * A specialized AlgebraicValueNode to handle values of the CAL RelativeTime.RelativeDate type.
 *
 * Creation date: (04/06/01 10:48:36 AM)
 * @author Michael Cheng
 */
public class RelativeDateValueNode extends RelativeTemporalValueNode {

    /**
     * A custom ValueNodeProvider for the RelativeDateValueNode.
     * @author Frank Worsley
     */
    public static class RelativeDateValueNodeProvider extends ValueNodeProvider<RelativeDateValueNode> {

        public static final QualifiedName DATE_NAME = CAL_RelativeTime.TypeConstructors.RelativeDate;
        
        public RelativeDateValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<RelativeDateValueNode> getValueNodeClass() {
            return RelativeDateValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public RelativeDateValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isNonParametricType(DATE_NAME)) {
                return null;
            }
            
            return new RelativeDateValueNode((Date) value, typeExpr);
        }        
    }    
    
    /**
     * RelativeDateValueNode constructor.
     * @param dateValue the value of this value node. If null current time will be used.
     * @param typeExprParam the type expression of this value node. Must be of Date type.
     */
    public RelativeDateValueNode(Date dateValue, TypeExpr typeExprParam) {
        super(typeExprParam, dateValue);
        checkTypeConstructorName(typeExprParam, RelativeDateValueNodeProvider.DATE_NAME);
    }
    
    @Override
    public boolean containsParametricValue() {
        return false;
    }

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * Creation date: (06/07/01 8:43:30 AM)
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public RelativeDateValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
        return new RelativeDateValueNode(getDateValue(), newTypeExpr);
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        Calendar calendar = getCalendarValue();

        int dateYear = calendar.get(Calendar.YEAR);
        // Add 1 because Java's Date has month starting at 0.
        int dateMonth = calendar.get(Calendar.MONTH) + 1;
        int dateDay = calendar.get(Calendar.DAY_OF_MONTH);

        return CAL_RelativeTime.Functions.makeRelativeDateValue(dateYear, dateMonth, dateDay);
    }
    
    /**
     * Returns the Date value.
     * Creation date: (13/06/01 2:29:17 PM)
     * @return Date
     */
    public Date getDateValue() {

        return getJavaDate();
    }
    
    @Override
    public Object getValue() {
        return getDateValue();
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {

        return getTextValue(DateFormat.FULL, -1);
    }        
    
    /**
     * Return an output policy which describes how to marshall a value represented
     * by a value node from CAL to Java.
     * @return - the output policy associated with the ValueNode instance.
     */
    @Override
    public OutputPolicy getOutputPolicy() {
        return OutputPolicy.DEFAULT_OUTPUT_POLICY;
    }
    
    /**
     * Set a value which is the result of the marshaller described by
     * 'getOutputPolicy()'.
     * @param value - the java value
     */
    @Override
    public void setOutputJavaValue(Object value) {
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Error in RelativeDateValueNode.setOutputJavaValue: output must be an instance of List, not an instance of: " + value.getClass().getName());
        }
        
        List<Integer> dateTuple = UnsafeCast.asTypeOf(value, Collections.<Integer>emptyList());
        Calendar cal = getCalendarValue();
        cal.set(Calendar.YEAR, (dateTuple.get(0)).intValue());
        cal.set(Calendar.MONTH, (dateTuple.get(1)).intValue() - 1);
        cal.set(Calendar.DAY_OF_MONTH, (dateTuple.get(2)).intValue());
        setJavaDate(cal.getTime());        
    }
        
    /**
     * Return an input policy which describes how to marshall a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    @Override
    public InputPolicy getInputPolicy () {
        return InputPolicy.makeTypedDefaultInputPolicy(getTypeExpr().toSourceModel().getTypeExprDefn());
    }
    
    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues() {
        List<Integer> dateTuple = new ArrayList<Integer> (3); //(year, month, day)
        Calendar calendarDateTime = getCalendarValue ();

        dateTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.YEAR)));
        // Add 1 because Java's Date has month starting at 0.
        dateTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.MONTH) + 1));
        dateTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.DAY_OF_MONTH)));        
        return new Object[]{dateTuple};
    }
    
}
