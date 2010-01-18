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
 * ParametricValueEditor.java
 * Created: March 20, 2001
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.valuenode.ParametricValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.IntellicutListModelAdapter;
import org.openquark.gems.client.IntellicutListRenderer;
import org.openquark.gems.client.IntellicutPanel;
import org.openquark.gems.client.IntellicutPanelOwner;
import org.openquark.gems.client.ToolTipHelpers;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;
import org.openquark.gems.client.IntellicutManager.IntellicutInfo;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.util.Messages;


/**
 * The ValueEditor for choosing the data type for the uninstantiated TypeVar.
 * @author Michael Cheng
 */
class ParametricValueEditor extends ValueEditor implements IntellicutPanelOwner {
    
    private static final long serialVersionUID = -1170600242670766483L;
    
    private static Messages messages = new Messages(ParametricValueEditor.class, "ParametricValueEditor_ui");

    /**
     * A custom value editor provider for the ParametricValueEditor.
     */
    public static class ParametricValueEditorProvider extends ValueEditorProvider<ParametricValueEditor> {
        
        public ParametricValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof ParametricValueNode;
        }

        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public ParametricValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            ParametricValueEditor editor = new ParametricValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
        
    }

    /**
     * Customized cell renderer that will use the type icon as the icon for a list entry.
     */
    private class ParametricValueListRenderer extends IntellicutListRenderer {

        private static final long serialVersionUID = 345425620311502011L;

        public ParametricValueListRenderer(IntellicutMode mode) {
            super(mode);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            TypeExpr typeExpr = (TypeExpr) ((IntellicutListEntry) value).getData();
            label.setIcon(new ImageIcon(ParametricValueEditor.class.getResource(valueEditorManager.getTypeIconName(typeExpr))));
            label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));

            return label;
        }
    }

    /**
     * The adapter for the IntellicutListModel used by this value editor.
     * @author Frank Worsley
     */
    private class IntellicutAdapter extends IntellicutListModelAdapter {

        @Override
        protected Set<TypeExpr> getDataObjects() {
            return getMatchingInputTypes();   
        }
        
        @Override
        protected IntellicutInfo getIntellicutInfo(IntellicutListEntry data) {
            return IntellicutInfo.DEFAULT_INFO;
        }
        
        public boolean isBestEntry(IntellicutListEntry listEntry) {
            return true;
        }
        
        @Override
        public String getToolTipTextForEntry(IntellicutListEntry listEntry, JComponent invoker) {
            
            Object listEntryData = listEntry.getData();
            if (listEntryData instanceof RecordType) {
                
                return "<html><body><b>Prelude Record</b></body></html>";
                
            } else if (listEntryData instanceof TypeConsApp) {
            
                TypeConsApp typeConsApp = (TypeConsApp) listEntryData;
                QualifiedName typeConsName = typeConsApp.getName();
                
                TypeConstructor typeCons = valueEditorManager.getPerspective().getTypeConstructor(typeConsName);
                return ToolTipHelpers.getEntityToolTip(typeCons, getNamingPolicy(), valueEditorManager.getWorkspace(), invoker);
                
            } else {
                throw new IllegalStateException("ParametricValueEditor: Unsupported list entry type");
            }
        }
    }
    
    /** The Intellicut panel used by this value editor. */
    private IntellicutPanel intellicutPanel;
    
    /**
     * ParametricValueEditor constructor.
     * @param valueEditorHierarchyManager
     */
    protected ParametricValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
    }
    
    /**
     * @return the JList that is used by the editor to display its values
     */
    protected IntellicutPanel getIntellicutPanel() {
        return intellicutPanel;
    }
    
    /**
     * Returns the list of input data types that could possibly be available in the switch type list.
     * This list will be used by getMatchingInputTypes to filter out types that do not match
     * the value node type expression. Override this if you want to narrow down the list of
     * possible input types, but still want the default behaviour of filtering the types.
     * @return the list of possible input types
     */
    protected Set<TypeExpr> getAvailableInputTypes() {
        return valueEditorManager.getAvailableInputTypes();
    }
    
    /**
     * Filters the list returned by getAvailableInputTypes to only return the types that can actually
     * pattern match with the value node type expression.
     * @return list of input types to be displayed in the switch type list
     */
    protected Set<TypeExpr> getMatchingInputTypes() {
        
        Set<TypeExpr> dataTypes = getAvailableInputTypes();
        Set<TypeExpr> matchingTypes = new HashSet<TypeExpr>();
        ModuleTypeInfo currentModuleTypeInfo = valueEditorManager.getPerspective().getWorkingModuleTypeInfo();
        
        if (currentModuleTypeInfo != null) {
            
            TypeExpr valueNodeTypeExpr = getValueNode().getTypeExpr();
        
            for (final TypeExpr typeExpr : dataTypes) {
    
                if (TypeExpr.canPatternMatch(typeExpr, valueNodeTypeExpr, currentModuleTypeInfo)) {
                    matchingTypes.add(typeExpr);
                }
            }
        }
        
        return matchingTypes;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {

        IntellicutListEntry listEntry = (IntellicutListEntry) intellicutPanel.getIntellicutList().getSelectedValue();
        
        if (listEntry != null) {
        
            // Note: For TypeExpr which contains uninstantiated TypeVars, we must 'duplicate' them somehow.
            TypeExpr instanceTypeExpr = ((TypeExpr) listEntry.getData()).copyTypeExpr();
            ValueNode replacement = getOwnerValueNode().transmuteValueNode(valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer(), instanceTypeExpr);
    
            // Get the TypeVar that we are instantiating.
            replaceValueNode(replacement, true);

            notifyValueCommitted();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return intellicutPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitialValue() {
        
        setLayout(new BorderLayout());
        setResizable(true);

        IntellicutAdapter adapter = new IntellicutAdapter();
        adapter.setNamingPolicy(new UnqualifiedUnlessAmbiguous(valueEditorManager.getPerspective().getWorkingModuleTypeInfo()));
        
        intellicutPanel = new IntellicutPanel(this, adapter, null, IntellicutMode.NOTHING);
        intellicutPanel.loadListModel();
        intellicutPanel.setBorder(null);
        intellicutPanel.setMoveable(false);
        intellicutPanel.getIntellicutList().setCellRenderer(new ParametricValueListRenderer(IntellicutMode.NOTHING));

        // By default there is no item selected.
        intellicutPanel.getIntellicutList().clearSelection();

        add(intellicutPanel, BorderLayout.CENTER);
        
        resetSize();
    }
    
    /**
     * Resets the current size and minimum resize dimension of the editor.
     */
    protected final void resetSize() {
        
        int size = intellicutPanel.getIntellicutListModel().getSize(); 
        
        if (size > 0) {

            // If the list is displayed, then setup some sizes.
            
            // Set the minimum resize dimension to a height of 2 item.
            intellicutPanel.setPreferredVisibleRows(2);
            setMinResizeDimension(getPreferredSize());
            setMaxResizeDimension(new Dimension(getPreferredSize().width, 2048));
            setResizable(true);
            
            // Set the preferred list height to max 10 rows
            intellicutPanel.setPreferredVisibleRows(size >= 10 ? 10 : size + 1);
            
        } else {
            setResizable(false);
        }
        
        setSize(getPreferredSize());
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
     * @see org.openquark.gems.client.IntellicutPanelOwner#getMessages()
     */
    public Messages getMessages() {
        return messages;
    }
}
