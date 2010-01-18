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
 * GemGenerator.java
 * Created: 11-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.gems.client.services;

import java.awt.Color;

import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Graphics.CAL_Color;
import org.openquark.cal.module.Cal.Utilities.CAL_Time;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.valuenode.ColourValueNode;
import org.openquark.cal.valuenode.JTimeValueNode;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.ValueGem;
import org.openquark.util.time.Time;



/**
 * This is a factory class used to build value and function gems.
 * @author Rick Cameron
 * @author mbyne
 */
public final class GemFactory {
          
    /**
     * the CAL services used to create gems
     */
    private final BasicCALServices calServices;
    
    
    /**
     * construct the gem services.
     * @param calServices
     */
    public GemFactory(BasicCALServices calServices) {      
        this.calServices = calServices;
    }

    /**
     * Creates a FunctionalAgentGem for the specified function, or null if the function is not found.
     * @param functionalAgentName
     * @return FunctionalAgentGem
     */
    public FunctionalAgentGem makeFunctionalAgentGem(QualifiedName functionalAgentName) {
        GemEntity gemEntity = calServices.getGemEntity(functionalAgentName);
        if (gemEntity == null)
            return null;
    
        return new FunctionalAgentGem (gemEntity);
    }

    /**
     * Creates a ValueGem for the provided value, or null if a value node could not be constructed for the given value.
     * @param value
     * @return a ValueGem wrapping the provided value.
     */
    public ValueGem makeValueGem(Object value) {
        return new ValueGem (getValueNode (value));
    }

    /**
     * Returns a simple value node for the specified value, or null
     * if it cannot be represented as a simple value node.
     * This supports the following types: 
     *      Color, Character, Boolean, Byte, Short, Integer, Long, Float, Double, String, and Time.
     * @param value  a value
     * @return a value node for this value, or null if it cannot be expressed as a simple value node
     */
    private ValueNode getValueNode(Object value) {
        
        
        // TODO: is there a more flexible way of doing this?
        //       Probably should use the ValueNodeBuilderHelper.
        
        PreludeTypeConstants preludeTypeConstants = calServices.getPreludeTypeConstants();
        
        if (value instanceof Character) {
            return new LiteralValueNode(value, preludeTypeConstants.getCharType());
        }
        else if (value instanceof Boolean) {
            return new LiteralValueNode(value, preludeTypeConstants.getBooleanType());
        }
        else if (value instanceof Byte) {
            return new LiteralValueNode(value, preludeTypeConstants.getByteType());
        }
        else if (value instanceof Short) {
            return new LiteralValueNode(value, preludeTypeConstants.getShortType());
        }
        else if (value instanceof Integer) {
            return new LiteralValueNode(value, preludeTypeConstants.getIntType());
        }
        else if (value instanceof Long) {
            return new LiteralValueNode(value, preludeTypeConstants.getLongType());
        }
        else if (value instanceof Float) {
            return new LiteralValueNode(value, preludeTypeConstants.getFloatType());
        }
        else if (value instanceof Double) {
            return new LiteralValueNode(value, preludeTypeConstants.getDoubleType());
        }
        else if (value instanceof String) {
            return new LiteralValueNode(value, preludeTypeConstants.getStringType());
        }
        else if (value instanceof Color) {        
            TypeExpr colourType = calServices.getTypeFromQualifiedName(CAL_Color.TypeConstructors.Color);
            if (colourType == null) {
                return null;
            }
            return new ColourValueNode((Color) value, colourType);
        }
        else if (value instanceof Time) {
            TypeExpr timeType = calServices.getTypeFromQualifiedName(CAL_Time.TypeConstructors.Time);
            if (timeType == null) {
                return null;                
            }
            return new JTimeValueNode((Time) value, timeType);
        }
    
        return null;
    }

}
