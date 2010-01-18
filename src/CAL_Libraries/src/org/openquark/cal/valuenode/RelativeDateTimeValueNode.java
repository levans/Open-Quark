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
 * RelativeDateTimeValueNode.java
 * Creation date: (05/06/01 10:28:25 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.util.UnsafeCast;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;

/**
 * A specialized AlgebraicValueNode used to handle values of the RelativeTime.RelativeDateTime data type.
 *
 * Creation date: (05/06/01 10:28:25 AM)
 * @author Michael Cheng
 */
public class RelativeDateTimeValueNode extends RelativeTemporalValueNode {

    /**
     * A custom ValueNodeProvider for the RelativeDateTimeValueNode.
     * @author Frank Worsley
     */
    public static class RelativeDateTimeValueNodeProvider extends ValueNodeProvider<RelativeDateTimeValueNode> {

        public RelativeDateTimeValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<RelativeDateTimeValueNode> getValueNodeClass() {
            return RelativeDateTimeValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public RelativeDateTimeValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime)) {
                return null;
            }
            
            return new RelativeDateTimeValueNode((Date) value, typeExpr);
        }
    }
    
    /**
     * RelativeDateTimeValueNode constructor.
     * @param dateTimeValue the value of this value node. If null current time will be used. 
     * @param typeExprParam  the type expression of the value node. Must be of DateTime type.
     */
    public RelativeDateTimeValueNode(Date dateTimeValue, TypeExpr typeExprParam) {

        super(typeExprParam, dateTimeValue);
        checkTypeConstructorName(typeExprParam, CAL_RelativeTime.TypeConstructors.RelativeDateTime);
    }
    
    @Override
    public boolean containsParametricValue() {
        return false;
    }    
        
    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * Creation date: (06/07/01 8:42:43 AM)
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public RelativeDateTimeValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
        return new RelativeDateTimeValueNode(getDateTimeValue(), newTypeExpr);
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        Calendar calendarDateTime = getCalendarValue();

        int dateTimeYear = calendarDateTime.get(Calendar.YEAR);
        // Add 1 because Java's Date has month starting at 0.
        int dateTimeMonth = calendarDateTime.get(Calendar.MONTH) + 1;
        int dateTimeDay = calendarDateTime.get(Calendar.DAY_OF_MONTH);
        int dateTimeHour = calendarDateTime.get(Calendar.HOUR_OF_DAY);
        int dateTimeMinute = calendarDateTime.get(Calendar.MINUTE);
        int dateTimeSecond = calendarDateTime.get(Calendar.SECOND);

        return CAL_RelativeTime.Functions.makeRelativeDateTimeValue(
                dateTimeYear,
                dateTimeMonth,
                dateTimeDay,
                dateTimeHour,
                dateTimeMinute,
                dateTimeSecond);
    }
    
    /**
     * Returns the DateTime value.
     * Creation date: (13/06/01 2:27:43 PM)
     * @return Date
     */
    public Date getDateTimeValue() {

        return getJavaDate();
    }
    
    @Override
    public Object getValue() {
        return getDateTimeValue();
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {
        return getTextValue(DateFormat.FULL, DateFormat.MEDIUM);
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
            throw new IllegalArgumentException("Error in RelativeDateTimeValueNode.setOutputJavaValue: output must be an instance of List, not an instance of: " + value.getClass().getName());
        }
        
        List<List<Integer>> dateTimeTuple = UnsafeCast.asTypeOf(value, Collections.<List<Integer>>emptyList());
        List<Integer> dateTuple = dateTimeTuple.get(0);
        List<Integer> timeTuple = dateTimeTuple.get(1);
        Calendar cal = getCalendarValue();
        cal.set(Calendar.YEAR, (dateTuple.get(0)).intValue());
        cal.set(Calendar.MONTH, (dateTuple.get(1)).intValue() - 1);
        cal.set(Calendar.DAY_OF_MONTH, (dateTuple.get(2)).intValue());
        cal.set(Calendar.HOUR_OF_DAY, (timeTuple.get(0)).intValue());
        cal.set(Calendar.MINUTE, (timeTuple.get(1)).intValue());
        cal.set(Calendar.SECOND, (timeTuple.get(2)).intValue());
        setJavaDate(cal.getTime());        
    }
    
    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues () {
        List<Integer> dateTuple = new ArrayList<Integer>(3);
        Calendar calendarDateTime = getCalendarValue ();

        dateTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.YEAR)));
        // Add 1 because Java's Date has month starting at 0.
        dateTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.MONTH) + 1));
        dateTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.DAY_OF_MONTH)));      

        List<Integer> timeTuple = new ArrayList<Integer>(3);
        timeTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.HOUR_OF_DAY)));
        timeTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.MINUTE)));
        timeTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.SECOND)));       
        
        List<List<Integer>> dateTimeTuple = new ArrayList<List<Integer>>(2);
        dateTimeTuple.add(dateTuple);
        dateTimeTuple.add(timeTuple);         
        return new Object[]{dateTimeTuple};
    }
}
