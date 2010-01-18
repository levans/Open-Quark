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
 * RelativeTimeValueNode.java
 * Creation date: (04/06/01 11:28:09 AM)
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
 * A specialized AlgebraicValueNode used to handle values of the RelativeTime.RelativeTime type.
 *
 * Creation date: (04/06/01 11:28:09 AM)
 * @author Michael Cheng
 */
public class RelativeTimeValueNode extends RelativeTemporalValueNode {

    /**
     * A custom ValueNodeProvider for the RelativeTimeValueNode.
     * @author Frank Worsley
     */
    public static class RelativeTimeValueNodeProvider extends ValueNodeProvider<RelativeTimeValueNode> {

        public static final QualifiedName TIME_NAME = CAL_RelativeTime.TypeConstructors.RelativeTime;
        
        public RelativeTimeValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<RelativeTimeValueNode> getValueNodeClass() {
            return RelativeTimeValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public RelativeTimeValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isNonParametricType(TIME_NAME)) {
                return null;
            }
            
            return new RelativeTimeValueNode((Date) value, typeExpr);
        }        
    }  
    
    /**
     * RelativeTimeValueNode constructor.
     * @param timeValue the time value of the value node. If this is null the current time will be used. 
     * @param typeExprParam  the type expression of the value node. Must be of the Time type.
     */
    public RelativeTimeValueNode(Date timeValue, TypeExpr typeExprParam) {
        super(typeExprParam, timeValue);
        checkTypeConstructorName(typeExprParam, RelativeTimeValueNodeProvider.TIME_NAME);
    }
    
    @Override
    public boolean containsParametricValue() {
        return false;
    }    

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * Creation date: (06/07/01 8:56:30 AM)
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public RelativeTimeValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
        return new RelativeTimeValueNode(getTimeValue(), newTypeExpr);
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        Calendar calendarTime = getCalendarValue();

        int timeHour = calendarTime.get(Calendar.HOUR_OF_DAY);
        int timeMinute = calendarTime.get(Calendar.MINUTE);
        int timeSecond = calendarTime.get(Calendar.SECOND);

        return CAL_RelativeTime.Functions.makeRelativeTimeValue(timeHour, timeMinute, timeSecond);
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {
        return getTextValue(-1, DateFormat.MEDIUM);
    }
    
    /**
     * Returns the Time value.
     * @return Date
     */
    public Date getTimeValue() {
        return getJavaDate();
    }  
    
    @Override
    public Object getValue() {
        return getTimeValue();
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
            throw new IllegalArgumentException("Error in RelativeTimeValueNode.setOutputJavaValue: output must be an instance of List, not an instance of: " + value.getClass().getName());
        }
        List<Integer> timeTuple = UnsafeCast.asTypeOf(value, Collections.<Integer>emptyList());
        Calendar cal = getCalendarValue();
        cal.set(Calendar.HOUR_OF_DAY, (timeTuple.get(0)).intValue());
        cal.set(Calendar.MINUTE, (timeTuple.get(1)).intValue());
        cal.set(Calendar.SECOND, (timeTuple.get(2)).intValue());
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
    public Object[] getInputJavaValues () {
        List<Integer> timeTuple = new ArrayList<Integer>(3);
        Calendar calendarDateTime = getCalendarValue ();

        timeTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.HOUR_OF_DAY)));
        timeTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.MINUTE)));
        timeTuple.add(Integer.valueOf(calendarDateTime.get(Calendar.SECOND)));       
        
        return new Object[]{timeTuple};
    }
    
}
