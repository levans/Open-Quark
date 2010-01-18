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
 * OperatorInfo.java
 * Creation date (Aug 20, 2002).
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.util.HashMap;
import java.util.Map;

import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * Some helpful methods for working with functions and class methods that
 * have an operator form. For example, 'Prelude.equals' has operator form '=' and 'Prelude.and' has
 * operator form '&&'.
 * Note: all entities with operator forms must belong to the Prelude module.
 * <P>
 * The overloaded '-' operator is always interpreted as binary subtraction rather than unary negate.
 * The static NEGATE_CLASS_METHOD field contains the textual name for unary negation. 
 * <P>
 * Creation date (Aug 20, 2002).
 * @author Bo Ilic
 */
public final class OperatorInfo {
            
    static private final Map<String, String> textualToOperatorMap = new HashMap<String, String>();
    static private final Map<String, String> operatorToTextualMap = new HashMap<String, String>();

    /**static initializer */
    static {
        
        //operators corresponding to functions
        
        addOperator(CAL_Prelude.Functions.and, "&&");
        addOperator(CAL_Prelude.Functions.or, "||");
        addOperator(CAL_Prelude.Functions.append, "++");
        
        addOperator(CAL_Prelude.Functions.compose, "#");
        addOperator(CAL_Prelude.Functions.apply, "$");
        
        //operators corresponding to class methods
        
        addOperator(CAL_Prelude.Functions.equals, "==");
        addOperator(CAL_Prelude.Functions.notEquals, "!=");
        
        addOperator(CAL_Prelude.Functions.greaterThan, ">");
        addOperator(CAL_Prelude.Functions.greaterThanEquals, ">=");
        addOperator(CAL_Prelude.Functions.lessThan, "<");
        addOperator(CAL_Prelude.Functions.lessThanEquals, "<=");
              
        addOperator(CAL_Prelude.Functions.add, "+");
        addOperator(CAL_Prelude.Functions.subtract, "-");
        addOperator(CAL_Prelude.Functions.multiply, "*");
        addOperator(CAL_Prelude.Functions.divide, "/");
        addOperator(CAL_Prelude.Functions.remainder, "%");
        
        //addOperator("negate", "-"); 
        
        //operators corresponding to data constructors
        
        addOperator(CAL_Prelude.DataConstructors.Cons, ":"); 
        addOperator(CAL_Prelude.DataConstructors.Nil, "[]");
        addOperator(CAL_Prelude.DataConstructors.Unit, "()");                                          
    } 
    
    private OperatorInfo() {}
      
    /**
     * Method addOperator.
     * @param textualName e.g. the textual name of '==' is 'equals', which is assumed to be a top-level Prelude entity.
     * @param operatorName e.g. the operator name of 'and' is '&&'.
     */
    private static void addOperator (QualifiedName textualName, String operatorName) {
        
        if (!textualName.getModuleName().equals(CAL_Prelude.MODULE_NAME)) {
            throw new IllegalArgumentException("OperatorInfo implementation needs updating to handle non-Prelude operators.");
        }
        
        String unqualifiedTextualName = textualName.getUnqualifiedName();
        textualToOperatorMap.put(unqualifiedTextualName, operatorName);
        operatorToTextualMap.put(operatorName, unqualifiedTextualName);
    }
    
    /**
     * Method getOperatorName.
     * @param textualName e.g. the textual name of '==' is 'Prelude.equals'.
     * @return the operator name (or null if not an operator).
     */
    public static String getOperatorName(QualifiedName textualName) {
        
        if (!textualName.getModuleName().equals(CAL_Prelude.MODULE_NAME)) {
            return null;
        }
    
        String unqualifiedTextualName = textualName.getUnqualifiedName();
    
        return textualToOperatorMap.get(unqualifiedTextualName);
    }
    
    /**
     * Method getTextualName.
     * @param operatorName  e.g. the operator name of 'Prelude.and' is '&&'.
     *                       '-' is always treated by this method as binary subtraction, not unary negation. 
     * @return QualifiedName the textual name of the operator.
     */
    public static QualifiedName getTextualName(String operatorName) {
        return QualifiedName.make(CAL_Prelude.MODULE_NAME, operatorToTextualMap.get(operatorName));
    }    
}
