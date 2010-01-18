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
 * EnumeratedValueEditor.java
 * Creation date: (05/03/01 10:49:14 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ValueNode;


/**
 * The value editor for editing enumerated values. 
 * Creation date: (05/03/01 10:49:14 AM)
 * @author Michael Cheng
 */
class EnumeratedValueEditor extends EnumeratedValueEditorBase {

    private static final long serialVersionUID = 8992686985444519741L;

    /**
     * A custom value editor provider for the EnumeratedValueEditor.
     */
    public static class EnumeratedValueEditorProvider extends ValueEditorProvider<EnumeratedValueEditor> {

        public EnumeratedValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            TypeConsApp typeConsApp = valueNode.getTypeExpr().rootTypeConsApp();
            return typeConsApp != null && getPerspective().isEnumDataType(typeConsApp.getName());
        }

        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public EnumeratedValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            EnumeratedValueEditor editor = new EnumeratedValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
        
    }
    
    /**
     * EnumeratedValueEditor constructor.
     * @param valueEditorHierarchyManager
     */
    protected EnumeratedValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.EnumeratedValueEditorBase#getValueList()
     */
    @Override
    protected List<ValueNode> getValueList() {

        // Add values to the list.
        TypeExpr typeExpr = getValueNode().getTypeExpr();
        QualifiedName typeConstructorName = ((TypeConsApp)typeExpr).getName();
        DataConstructor[] dcList = valueEditorManager.getPerspective().getDataConstructorsForType(typeConstructorName);

        List<ValueNode> valueList = new ArrayList<ValueNode>();
        for (final DataConstructor dataConstructor : dcList) {
            ValueNode value = valueEditorManager.getValueNodeBuilderHelper().buildValueNode(null, dataConstructor, typeExpr);
            valueList.add(value);
        }

        return valueList;
    }
}
