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
 * IntellicutValueEditor.java
 * Creation date: Nov 4, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.util.Set;

import javax.swing.JPopupMenu;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.valuenode.GemEntityValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.GemCutterMessages;
import org.openquark.gems.client.IntellicutListModelAdapter;
import org.openquark.gems.client.IntellicutPanel;
import org.openquark.gems.client.IntellicutPanelOwner;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;
import org.openquark.gems.client.IntellicutManager.IntellicutInfo;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.util.Messages;


/**
 * @author Frank Worsley
 */
public class IntellicutValueEditor extends ValueEditor implements IntellicutPanelOwner {

    private static final long serialVersionUID = -2692793681484116317L;

    /**
     * A custom value editor provider for the IntellicutValueEditor.
     */
    public static class IntellicutValueEditorProvider extends ValueEditorProvider<IntellicutValueEditor> {
        
        public IntellicutValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof GemEntityValueNode;
        }

        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public IntellicutValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager, ValueNode valueNode) {
            IntellicutValueEditor editor = new IntellicutValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }

    }
    
    /**
     * The adapter for the IntellicutListModel used by this value editor.
     * @author Frank Worsley
     */
    private class IntellicutAdapter extends IntellicutListModelAdapter {

        @Override
        protected Set<GemEntity> getDataObjects() {
            return getVisibleGemsFromPerspective(valueEditorManager.getPerspective());   
        }
        
        @Override
        protected IntellicutInfo getIntellicutInfo(IntellicutListEntry data) {
            
            if (data.getTypeExpr().getArity() != getValueNode().getTypeExpr().getArity()) {
                return null;
            }
            
            return getIntellicutInfo(data, getValueNode().getTypeExpr(), valueEditorManager.getTypeCheckInfo(), false, null);
        }
    }
    
    /** The IntellicutPanel embedded in this editor. */
    private IntellicutPanel intellicutPanel = null;
    
    public IntellicutValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        
        setLayout(new BorderLayout());
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#getDefaultFocusComponent()
     */
    @Override
    public Component getDefaultFocusComponent() {
        return intellicutPanel.getIntellicutList();
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {
        
        IntellicutAdapter adapter = new IntellicutAdapter();
        
        adapter.setNamingPolicy(new UnqualifiedUnlessAmbiguous(valueEditorManager.getPerspective().getWorkingModuleTypeInfo()));
        
        intellicutPanel = new IntellicutPanel(this, adapter, null, IntellicutMode.NOTHING);
        
        intellicutPanel.loadListModel();
        intellicutPanel.setBorder(null);
        intellicutPanel.setMoveable(false);
        
        // Select the correct item in the list.
        GemEntity gemEntity = (GemEntity) getValueNode().getValue();
        IntellicutListEntry listEntry = adapter.getListEntryForData(gemEntity);
        
        if (listEntry != null) {
            intellicutPanel.getIntellicutList().setSelectedValue(listEntry, true);
        } else {
            intellicutPanel.getIntellicutList().clearSelection();
        }
        
        add(intellicutPanel, BorderLayout.CENTER);
        setSize(getPreferredSize());
        
        // Add a dummy MouseListener so that Swing doesn't forward mouse events intended for
        // us to the component below us. Swing does that if a component has no mouse listeners at all.
        addMouseListener(new MouseAdapter(){});
    }
    
    @Override
    public void commitValue() {
        
        IntellicutListEntry listEntry = (IntellicutListEntry) intellicutPanel.getIntellicutList().getSelectedValue();

        if (listEntry != null) {
            
            GemEntity gemEntity = (GemEntity) listEntry.getData();
            ModuleTypeInfo currentModuleInfo = valueEditorManager.getPerspective().getWorkingModuleTypeInfo();
            TypeExpr valueNodeType = getValueNode().getTypeExpr();
            TypeExpr functionType = gemEntity.getTypeExpr();
            TypeExpr unifiedType;
            
            try {
                unifiedType = TypeExpr.unify(valueNodeType, functionType, currentModuleInfo);
            } catch (TypeException e) {
                throw new IllegalStateException(e.getMessage());
            }
            
            GemEntityValueNode newValueNode = new GemEntityValueNode(gemEntity, unifiedType);
            replaceValueNode(newValueNode, true);
        }
        
        super.commitValue();
    }
        
    /**
     * @see org.openquark.gems.client.IntellicutPanelOwner#connectSelectedGem()
     */
    public void connectSelectedGem() {
        handleCommitGesture();
    }

    /**
     * @see org.openquark.gems.client.IntellicutPanelOwner#stopIntellicutPanel()
     */
    public void stopIntellicutPanel() {
        handleCancelGesture();
    }
    
    /**
     * @see org.openquark.gems.client.IntellicutPanelOwner#getIntellicutPopupMenu()
     */
    public JPopupMenu getIntellicutPopupMenu() {
        return null;
    }        
    
    /**
     * @return the message bundle that contains the IntellicutPanel resource strings
     */
    public Messages getMessages() {
        return GemCutterMessages.instance;
    }
}
