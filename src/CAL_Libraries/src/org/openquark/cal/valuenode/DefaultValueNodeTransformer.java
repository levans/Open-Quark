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
 * DefaultValueNodeTransformer.java
 * Creation date: (19/07/01 1:14:39 PM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import java.text.ParseException;

import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.TypeExpr;

import com.ibm.icu.text.DateFormat;

/**
 * Default ValueNodeTransformer used to transform (transfer data) between ValueNodes.
 * Creation date: (19/07/01 1:14:39 PM)
 * @author Michael Cheng
 */
public class DefaultValueNodeTransformer implements ValueNodeTransformer {
    
    /**
     * DefaultValueNodeTransformer constructor.
     */
    public DefaultValueNodeTransformer() {
        super();
    }
    
    /**
     * Will return a new ValueNode ideally suited for handling targetTypeExpr.
     * If the sourceVN and the returned ValueNode are of a recognized 'data transferable'
     * combination, then the sourceVN's data value will be moved to the returned ValueNode 
     * (perhaps with some minor mutations like rounding numbers).
     * Note: It is assumed that Int and Double types will always be handled by LiteralValueNode.
     * Currently, we recognize the following:
     * LiteralValueNode (double) -> LiteralValueNode (int)
     * LiteralValueNode (int) -> LiteralValueNode (double)
     * RelativeDateTimeValueNode -> RelativeDateValueNode
     * RelativeDateTimeValueNode -> RelativeTimeValueNode
     * Also, we do some conversions involving Strings.
     * @param valueNodeBuilderHelper
     * @param sourceVN 
     * @param destTypeExpr 
     * @return ValueNode
     */
    public ValueNode transform(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNode sourceVN, TypeExpr destTypeExpr) {

        TypeExpr sourceTypeExpr = sourceVN.getTypeExpr();
        
        // check if we're "transforming" to the same type
        if (sourceTypeExpr.sameType(destTypeExpr)) {
            return sourceVN.copyValueNode(destTypeExpr);
        }

        Object newValue = null;

        Class<? extends ValueNode> handlerClass = valueNodeBuilderHelper.getValueNodeClass(destTypeExpr);
        
        PreludeTypeConstants typeConstants = valueNodeBuilderHelper.getPreludeTypeConstants();

        if (handlerClass.equals(ListOfCharValueNode.class) || destTypeExpr.sameType(typeConstants.getStringType())) {

            if (sourceVN instanceof ListValueNode && sourceVN.getTypeExpr().sameType(typeConstants.getCharListType())) {
                // Converting a List to a List of Char: we convert each of the list values

                ListValueNode listValueNode = (ListValueNode)sourceVN;

                StringBuilder sb = new StringBuilder();
                int nElements = listValueNode.getNElements();
                for (int i = 0; i < nElements; i++) {
                    sb.append(listValueNode.getValueAt(i).getCALValue());
                }

                newValue = sb.toString();

            } else {
                newValue = sourceVN.getTextValue();
            }

        } else if (destTypeExpr.sameType(typeConstants.getCharType())) {
            
            // Just take the first character of the text value (if any)
            String textValue = sourceVN.getTextValue();
            if (textValue.length() != 0) {
                newValue = Character.valueOf(textValue.charAt(0));
            }
        }

        if (sourceVN instanceof LiteralValueNode && !sourceVN.getTypeExpr().sameType(typeConstants.getStringType())) {

            LiteralValueNode sourceLiteralVN = (LiteralValueNode) sourceVN;

            if (sourceTypeExpr.sameType(typeConstants.getDoubleType()) &&
                destTypeExpr.sameType(typeConstants.getIntType())) {

                newValue = Integer.valueOf((int) Math.round(sourceLiteralVN.getDoubleValue().doubleValue()));

            } else if (sourceTypeExpr.sameType(typeConstants.getIntType()) &&
                       destTypeExpr.sameType(typeConstants.getDoubleType())) {

                newValue = Double.valueOf(sourceLiteralVN.getIntegerValue().intValue());
            }

        } else if (sourceVN instanceof RelativeDateTimeValueNode) {

            RelativeDateTimeValueNode sourceDateTimeVN = (RelativeDateTimeValueNode) sourceVN;

            if (handlerClass.equals(RelativeDateValueNode.class) || handlerClass.equals(RelativeTimeValueNode.class)) {

                // TEMP: May need to copy.
                newValue = sourceDateTimeVN.getDateTimeValue();
            }

        } else if (sourceVN instanceof ListOfCharValueNode || sourceVN.getTypeExpr().sameType(typeConstants.getStringType())) {

            String stringVal = sourceVN instanceof ListOfCharValueNode ? 
                                ((ListOfCharValueNode)sourceVN).getStringValue() : 
                                ((LiteralValueNode)sourceVN).getStringValue();

            if (handlerClass.equals(RelativeDateValueNode.class)) {

                DateFormat dateFormat = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, -1);

                try {

                    newValue = dateFormat.parse(stringVal);

                } catch (ParseException pe) {

                    // Okay, can't parse.  Don't transform.
                }

            } else if (handlerClass.equals(RelativeTimeValueNode.class)) {

                DateFormat timeFormat = RelativeTemporalValueNode.getDateFormat(-1, DateFormat.MEDIUM);

                try {

                    newValue = timeFormat.parse(stringVal);

                } catch (ParseException pe) {

                    // Okay, can't parse.  Don't transform.
                }

            } else if (handlerClass.equals(RelativeDateTimeValueNode.class)) {

                DateFormat dateTimeFormat = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, DateFormat.MEDIUM);

                try {

                    newValue = dateTimeFormat.parse(stringVal);

                } catch (ParseException pe) {

                    // Okay, can't parse.  Don't transform.
                }

            } else if (handlerClass.equals(LiteralValueNode.class)) {
              
                if (destTypeExpr.sameType(typeConstants.getDoubleType())) {

                    try {

                        newValue = Double.valueOf(stringVal);

                    } catch (NumberFormatException e) {

                        // Okay, can't parse.  Don't transform.
                    }

                } else if (destTypeExpr.sameType(typeConstants.getIntType())) {

                    try {

                        Double unRoundedVal = Double.valueOf(stringVal);
                        newValue = Integer.valueOf(unRoundedVal.intValue());

                    } catch (NumberFormatException e) {

                        // Okay, can't parse.  Don't transform.
                    }

                } else if (destTypeExpr.sameType(typeConstants.getCharType())) {

                    // Only transform when there's only 1 character in stringVal.
                    if (stringVal.length() == 1) {

                        newValue = Character.valueOf(stringVal.charAt(0));
                    }

                } else if (destTypeExpr.sameType(typeConstants.getBooleanType())) {

                    // Let's be forgiving, and allow case insensitivity.
                    if (stringVal.equalsIgnoreCase("True")) {

                        newValue = Boolean.TRUE;

                    } else if (stringVal.equalsIgnoreCase("False")) {

                        newValue = Boolean.FALSE;
                    }
                }
            }
        }

        return valueNodeBuilderHelper.buildValueNode(newValue, null, destTypeExpr);
    }
}
