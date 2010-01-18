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
 * JTimeValueNode.java
 * Created: 15-Oct-2003
 * By: Rick Cameron
 */

package org.openquark.cal.valuenode;

import java.util.Date;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Utilities.CAL_Time;
import org.openquark.cal.module.Cal.Utilities.CAL_TimeZone;
import org.openquark.util.time.Time;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.TimeZone;

public class JTimeValueNode extends AlgebraicValueNode {

    /**
     * A custom ValueNodeProvider for the JTimeValueNode.
     * @author Frank Worsley
     */
    public static class JTimeValueNodeProvider extends ValueNodeProvider<JTimeValueNode> {

        public JTimeValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<JTimeValueNode> getValueNodeClass() {
            return JTimeValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public JTimeValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isNonParametricType(CAL_Time.TypeConstructors.Time)) {
                return null;
            }
            
            return new JTimeValueNode((Time) value, typeExpr);
        }        
    }    

    
    private Time timeValue;
    
    /**
     * JTimeValueNode constructor.
     * This JTimeValueNode's value will be set to timeValue.
     * Note: If timeValue is null, then the default value of the current time will be given.
     * Note: typeExprParam really must be a data type "JTime".
     * @param timeValue 
     * @param typeExprParam 
     */
    public JTimeValueNode(Time timeValue, TypeExpr typeExprParam) {

        super(typeExprParam);
        checkTypeConstructorName(typeExprParam, CAL_Time.TypeConstructors.Time);
        
        if (timeValue == null) {
            this.timeValue = Time.now();
        } else {
            this.timeValue = timeValue;
        }
    }
    
    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public JTimeValueNode copyValueNode(TypeExpr newTypeExpr) {
        checkCopyType(newTypeExpr);
        return new JTimeValueNode(timeValue, newTypeExpr);
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {
        int[] utc = timeValue.toUTC();

        return CAL_Time.Functions.makeTimeValue(
                utc[0],
                utc[1],
                utc[2],
                utc[3],
                utc[4],
                utc[5],
                utc[6],
                SourceModel.Expr.makeGemCall(CAL_TimeZone.Functions.utcTimeZone)); // TODO: can something better be done here?
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#containsParametricValue()
     */
    @Override
    public boolean containsParametricValue() {
        return false;
    }

    /**
     * @see org.openquark.cal.valuenode.ValueNode#getValue()
     */
    @Override
    public Object getValue() {
        return timeValue;
    }

    /**
     * @return a new Date object that can be safely modified representing the time held by the value node.
     */
    public Date getJavaDate() {
        return timeValue.toDate();
    }
    
    /**
     * Returns the display text representation of the expression
     * represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {
        DateFormat fmt=DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG);
        fmt.setTimeZone(TimeZone.getDefault());

        return fmt.format(timeValue.toDate());
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
        return new Object[]{timeValue};
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
        if (!(value instanceof Time)) {
            throw new IllegalArgumentException("Error in JTimeValueNode output method: output must be an instance of Time.");
        }
        
        timeValue = (Time)value;
    }

}
