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
 * ValueNodeCommitHelper.java
 * Creation date: Jul 22, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.valuenode;

import java.util.HashMap;
import java.util.Map;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeExpr;


/**
 * This is a helper class that can be used to calculate the changes that would take place when a value node is committed,
 * and that value node is type-linked to other value nodes.
 * <p>
 * 
 * For instance, in the case of an add gem, the two arguments to the gem are type-linked; the types of the two arguments are the same.
 * <ul>
 *   <li>If one of the arguments is of type Double, then the other argument is also of type Double.
 *   <li>If both arguments are Doubles, and one of the arguments is switched to the Int type, the other argument must also
 *       be switched to the Int type, otherwise there will be an inconsistency.
 * </ul>
 * 
 * To use this class:
 * <ol>
 * <li>create a map from value node to unconstrained type, for every value node for which type-switching is possible.  
 *     Care should be taken to maintain the desired referential equality among the given unconstrained type expr.
 *     <p>
 *     eg.  In the case of an add gem, two value nodes would both be mapped to the same <code>(Num a => a -> a)</code> TypeExpr object.
 *     
 * <li>when a value node is committed, call getCommitValues().  A map will be returned, where every value in the above map 
 *     is mapped to its new value after the commit.
 *     <p>
 *     eg.  In the case of an add gem, if the two value nodes are associated as in (1), a commit to one value will cause
 *          both associated value nodes to appear as keys in the returned map (since they both would change).  These will 
 *          be mapped to the new values that would appear on commit/type-switch.
 * </li>
 * 
 * @author Edward Lam
 */
public class ValueNodeCommitHelper {

    private final ValueNodeBuilderHelper valueNodeBuilderHelper;
    private final ValueNodeTransformer valueNodeTransformer;
    
    /**
     * Constructor for a ValueNodeCommitHelper.
     * @param valueNodeBuilderHelper
     * @param valueNodeTransformer
     */
    public ValueNodeCommitHelper(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer) {
        this.valueNodeBuilderHelper = valueNodeBuilderHelper;
        this.valueNodeTransformer = valueNodeTransformer;
    }

    /**
     * Get the new values that would come out of a value commit.
     * @param oldValueNode the value node whose type is being switched
     * @param newValueNode the value node to which oldValueNode is being switched.
     * @param valueNodeToUnconstrainedTypeMap The mapping from value node to its unconstrained type.
     *   Care should be taken to maintain the desired referential equality among type expr in the map.
     *   eg. ValueNodes with the same TypeExpr reference will be switched together.
     * @return Map A map from old value node to the new values they would have on switch.
     *   An entry will be present for each value node in the valueNodeToUnconstrainedTypeMap.
     */
    public Map<ValueNode, ValueNode> getCommitValues(ValueNode oldValueNode, ValueNode newValueNode, Map<ValueNode, TypeExpr> valueNodeToUnconstrainedTypeMap) {
                                   
        int nTypes = valueNodeToUnconstrainedTypeMap.size();
       
        ValueNode[] oldValueNodes = new ValueNode[nTypes];
        TypeExpr[] oldSpecializedTypes = new TypeExpr[nTypes];
        TypeExpr[] unconstrainedTypes = new TypeExpr[nTypes]; 
        int switchIndex = -1;              
        int index = 0;      
        for (final Map.Entry<ValueNode, TypeExpr> entry : valueNodeToUnconstrainedTypeMap.entrySet()) {
                                                                    
            ValueNode valueNode = entry.getKey(); 
            oldValueNodes[index] = valueNode;
            oldSpecializedTypes[index] = valueNode.getTypeExpr();
            unconstrainedTypes[index] = entry.getValue();
            
            if (valueNode == oldValueNode) {
                switchIndex = index;
            }
            
            index++;
        }        
        
        TypeExpr switchedType = newValueNode.getTypeExpr();        
                    
        ModuleTypeInfo currentModuleTypeInfo = valueNodeBuilderHelper.getPerspective().getWorkingModuleTypeInfo();
                    
        TypeExpr[] updatedSpecializedTypes = TypeExpr.getUpdatedSpecializedTypes(switchedType, switchIndex, oldSpecializedTypes, unconstrainedTypes, currentModuleTypeInfo);
        if (updatedSpecializedTypes == null) {
            //this is not a valid type switch. Perhaps there should be better validation here.
            throw new IllegalStateException();
        }
                                           
        // Construct a map giving the new values.
        Map<ValueNode, ValueNode> oldToNewValueMap = new HashMap<ValueNode, ValueNode>();
        
        // Iterate through the affected values.
        for (int i = 0; i < nTypes; ++i) {            
        
            ValueNode valueNode = oldValueNodes[i];
            TypeExpr updatedSpecializedType = updatedSpecializedTypes[i];

            ValueNode newValue;
            if (i == switchIndex) {
                // Make a copy of the value node which changed, ensuring that it has the updated type.
                newValue = newValueNode.copyValueNode();  //updatedSpecializedType);
            } else {
                // Transmute to convert the old value to the new type.
                newValue = valueNode.copyValueNode().transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, updatedSpecializedType);
            }

            oldToNewValueMap.put(valueNode, newValue);                    
        }

        return oldToNewValueMap;
    }                              
}
