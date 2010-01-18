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
 * ArgumentValueContext.java
 * Creation date: May 1, 2003.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.HashMap;
import java.util.Map;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;
import org.openquark.cal.valuenode.ValueNodeCommitHelper;
import org.openquark.cal.valuenode.ValueNodeTransformer;
import org.openquark.gems.client.Gem.PartInput;


/**
 * This is a helper class for switching value nodes associated with free inputs.
 *   To use, create an instance of this class with the appropriate mappings from input to valuenode, for the inputs under consideration.
 *   Ensure that the inputs are disconnected, then call getInputSwitchValues() to obtain the value nodes appropriate to a value commit.
 * @author Edward Lam
 */
public class InputValueTypeSwitchHelper {
    
    /** The value node commit helper for this class. */
    private final ValueNodeCommitHelper valueNodeCommitHelper;
    
    /**
     * Constructor for an InputValueTypeSwitchHelper
     * @param valueNodeCommitHelper the ValueNodeCommitHelper that will be used by this class.
     */
    public InputValueTypeSwitchHelper(ValueNodeCommitHelper valueNodeCommitHelper) {
        this.valueNodeCommitHelper = valueNodeCommitHelper;
    }

    /**
     * Constructor for an InputValueTypeSwitchHelper
     * @param valueNodeBuilderHelper
     * @param valueNodeTransformer
     */
    public InputValueTypeSwitchHelper(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer) {
        this.valueNodeCommitHelper = new ValueNodeCommitHelper(valueNodeBuilderHelper, valueNodeTransformer);
    }

    /**
     * Get the new values that gem inputs would hold on a value switch.
     * 
     * @param oldValueNode the top-level value node (held by an input) whose type is being switched
     * @param newValueNode the value node to which oldValueNode is being switched.
     * @param inputToValueNodeMap For inputs with which value nodes are associated, the mapping from input to 
     *   its value node.
     * @return Map A map from affected inputs to the new valuenodes they would have on switch.
     */
    public Map<PartInput, ValueNode> getInputSwitchValues(ValueNode oldValueNode, ValueNode newValueNode, Map<PartInput, ValueNode> inputToValueNodeMap) {
        return getInputSwitchValues(oldValueNode, newValueNode, inputToValueNodeMap, null);
    }
    /**
     * Get the new values that gem inputs would hold on a value switch.
     * 
     * @param oldValueNode the top-level value node (held by an input) whose type is being switched
     * @param newValueNode the value node to which oldValueNode is being switched.
     * @param inputToValueNodeMap For inputs with which value nodes are associated, the mapping from input to 
     *   its value node.
     * @param inputToUnconstrainedTypeMap Mapping from input to its associated unconstrained type.  
     *   If null, the input's type will be used directly.
     * @return Map A map from affected inputs to the new valuenodes they would have on switch.
     */
    public Map<PartInput, ValueNode> getInputSwitchValues(ValueNode oldValueNode, ValueNode newValueNode, Map<PartInput, ValueNode> inputToValueNodeMap, Map<PartInput, TypeExpr> inputToUnconstrainedTypeMap) {
        
        // Mapping between inputs and their corresponding value nodes.
        Map<PartInput, ValueNode> inputValueNodeMap = new HashMap<PartInput, ValueNode>(inputToValueNodeMap);

        // Construct the reverse map.
        // Mapping between value nodes and their corresponding inputs.
        Map<ValueNode, Gem.PartInput> valueNodeInputMap = new HashMap<ValueNode, Gem.PartInput>();
        for (final Map.Entry<PartInput, ValueNode> mapEntry : inputValueNodeMap.entrySet()) {
            PartInput nextInput = mapEntry.getKey();
            valueNodeInputMap.put(mapEntry.getValue(), nextInput);
        }

        // Construct a map from value node to unconstrained type
        Map<ValueNode, TypeExpr> valueNodeToUnconstrainedTypeMap = new HashMap<ValueNode, TypeExpr>();
        if (inputToUnconstrainedTypeMap == null) {
            for (final Map.Entry<PartInput, ValueNode> mapEntry : inputValueNodeMap.entrySet()) {
                Gem.PartInput nextInput = mapEntry.getKey();
                valueNodeToUnconstrainedTypeMap.put(mapEntry.getValue(), nextInput.getType());
            }
        } else {
            for (final Map.Entry<PartInput, ValueNode> mapEntry : inputValueNodeMap.entrySet()) {
                Gem.PartInput nextInput = mapEntry.getKey();
                valueNodeToUnconstrainedTypeMap.put(mapEntry.getValue(), inputToUnconstrainedTypeMap.get(nextInput));
            }
        }
        
        // Call a commit helper to do the grunt work.
        Map<ValueNode, ValueNode> commitValueMap = valueNodeCommitHelper.getCommitValues(oldValueNode, newValueNode, valueNodeToUnconstrainedTypeMap);

        // Construct the return map giving new values to the affected inputs.
        Map<PartInput, ValueNode> inputToNewValueMap = new HashMap<PartInput, ValueNode>();
        for (final Map.Entry<ValueNode, ValueNode> mapEntry : commitValueMap.entrySet()){
            ValueNode affectedValueNode = mapEntry.getKey();
            Gem.PartInput affectedInput = valueNodeInputMap.get(affectedValueNode);
            ValueNode newInputValue = mapEntry.getValue();
            inputToNewValueMap.put(affectedInput, newInputValue);
        }

        return inputToNewValueMap;
    }
}
