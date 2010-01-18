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
 * DefaultValueEditorDirector.java
 * Creation date: (12/07/01 9:33:36 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.util.List;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Graphics.CAL_Color;
import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;


/**
 * The default ValueEditorDirector used to determine which ValueEditor to use next.
 * Creation date: (12/07/01 9:33:36 AM)
 * @author Michael Cheng
 */
public class DefaultValueEditorDirector implements ValueEditorDirector {
    
    private final ValueNodeBuilderHelper valueNodeBuilderHelper;

    /**
     * DefaultValueEditorDirector constructor.
     * @param valueNodeBuilderHelper
     */
    public DefaultValueEditorDirector(ValueNodeBuilderHelper valueNodeBuilderHelper) {
        this.valueNodeBuilderHelper = valueNodeBuilderHelper;
    }

    /**
     * {@inheritDoc}
     */
    public ValueEditor getRootValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                          ValueNode valueNode,
                                          QualifiedName entityName,
                                          int argumentNumber,
                                          MetadataRunner metadataRunner) {

        // if there is a specialized VEP, return it.
        ValueEditor specializedVE;
        if ((specializedVE = getSpecializedRootValueEntryPanel(valueEditorHierarchyManager,
                                                               valueNode,
                                                               entityName,
                                                               argumentNumber,
                                                               metadataRunner)) != null) {
            return specializedVE;
        }

        // just get the appropriate unspecialized VEP for this type.
        return new ValueEntryPanel( valueEditorHierarchyManager, valueNode);
    }

    /**
     * Returns the ValueEditor (intended for the root level) most suited to handle the indicated situation.
     * Note: If there is no corresponding Entity (Eg: Value Gem or it's just Output), then entityName is null, and
     * the argumentNumber is to be disregarded.
     * Note: This method must return a ValueEntryPanel (no null).
     * @param valueEditorHierarchyManager
     * @param typeExpr 
     * @param entityName 
     * @param argumentNumber 
     * @param metadataRunner Needed to evaluate CAL expressions such as default value list from the metadata
     * @return ValueEditor
     */
    public ValueEditor getRootValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                          TypeExpr typeExpr,
                                          QualifiedName entityName,
                                          int argumentNumber,
                                          MetadataRunner metadataRunner) {
        // defer to the more general method
        ValueNode vn = valueNodeBuilderHelper.getValueNodeForTypeExpr(typeExpr);
        return getRootValueEditor(valueEditorHierarchyManager, vn, entityName, argumentNumber, metadataRunner);
    }

    /**
     * Returns a ValueEntryPanel specialized for the given parameters, if any.  Returns null if there is no special VEP.
     * @param valueEditorHierarchyManager the value editor hierarchy manager
     * @param valueNode the initial value for the VEP
     * @param entityName the name of the supercombinator
     * @param argumentNumber the index of the argument
     * @return ValueEntryPanel the specialized VEP, or null if not applicable.
     */
    private ValueEditor getSpecializedRootValueEntryPanel(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                                          ValueNode valueNode,
                                                          QualifiedName entityName,
                                                          int argumentNumber,
                                                          MetadataRunner metadataRunner) {

        if (CAL_Color.Functions.makeColor.equals(entityName) ||
            CAL_Color.Functions.makeTranslucentColor.equals(entityName)) {

            ColourRangeValueEntryPanel.PrimaryColour primaryColour = null;
            switch (argumentNumber) {
                case 0 :
                    primaryColour = ColourRangeValueEntryPanel.PrimaryColour.RED;
                    break;

                case 1 :
                    primaryColour = ColourRangeValueEntryPanel.PrimaryColour.GREEN;
                    break;

                case 2 :
                    primaryColour = ColourRangeValueEntryPanel.PrimaryColour.BLUE;
                    break;
                    
                case 3 :
                    primaryColour = ColourRangeValueEntryPanel.PrimaryColour.ALPHA;
                    break;

                default :
                    System.out.println("Error in getRootValueEntryPanel method of DefaultValueEditorDirector.");
            }

            ColourRangeValueEntryPanel crVEP = new ColourRangeValueEntryPanel(primaryColour, valueEditorHierarchyManager, valueNode);

            return crVEP;
        }

        // Check if there's default value metadata on the input.  If there is we can use a pick list
        // value editor.
        if (metadataRunner != null) {
            // Evaluate the expression
            ValueNode defaultValues = metadataRunner.evaluateDefaultValueMetadata(argumentNumber);

            if (defaultValues != null) {
                // If the value node is a list then use a pick list value entry panel which will popup a
                // customized list editor.  Otherwise the value node is a single value and we can use the
                // pick list value editor which is just a combo box.
                ModuleTypeInfo typeInfo = valueEditorHierarchyManager.getValueEditorManager().getPerspective().getWorkingModuleTypeInfo();
                TypeExpr dataType = valueNode.getTypeExpr(); 
                if (dataType.isListType() && TypeExpr.canUnifyType(dataType, defaultValues.getTypeExpr(), typeInfo)) {
                    return new PickListValueEntryPanel(valueEditorHierarchyManager,
                                                       valueNode,
                                                       (ListValueNode)defaultValues,
                                                       metadataRunner.useDefaultValuesOnly(argumentNumber));
                } else {
                    return new PickListValueEditor(valueEditorHierarchyManager,
                                                   valueNode,
                                                   (ListValueNode)defaultValues,
                                                   metadataRunner.useDefaultValuesOnly(argumentNumber));
                }
            }
        }
        
        return null;
    }

    /**
     * Uses the most recommended ValueEditor.
     * If there are no recommended ValueEditors, then returns null.
     * @param valueNode 
     * @param recommendedValueEditorList 
     * @param valueEditorHierarchyManager
     * @return ValueEditor
     */
    public ValueEditor getValueEditor(ValueNode valueNode, List<ValueEditorProvider<?>> recommendedValueEditorList, ValueEditorHierarchyManager valueEditorHierarchyManager) {

        if (recommendedValueEditorList.isEmpty()) {
            // Couldn't find a suitable ValueEditor handler.
            return null;
        }

        // The most recommended ValueEditor is represented by the first provider one in the list.
        ValueEditorProvider<?> provider = recommendedValueEditorList.get(0);
        
        return provider.getEditorInstance(valueEditorHierarchyManager, valueNode);
    }
}
