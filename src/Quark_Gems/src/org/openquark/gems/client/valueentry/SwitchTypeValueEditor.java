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
 * SwitchTypeValueEditor.java
 * Creation date: (16/07/01 11:19:40 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutFocusTraversalPolicy;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeVar;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.GemCutter;


/**
 * Very similar to a ParametricValueEditor except that this
 * ValueEditor enables a data type to be switched (or cleared)
 * Creation date: (16/07/01 11:19:40 AM)
 * @author Michael Cheng
 */
public class SwitchTypeValueEditor extends ParametricValueEditor {

    private static final long serialVersionUID = -9197270367654193581L;

    /** The message label used to display the type cannot be changed message. */
    private JLabel messageLabel = null;
    
    /** The 'Clear Type' button that appears below the type list. */
    private JButton clearButton = null;

    /** Flag to indicate that when we close this ValueEditor, we want to clear the data type. */
    private boolean clearType = false;

    /**
     * The action listener for the clear type button. Performs the clear type action.
     * @author Frank Worsley
     */
    private class ClearButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            clearType = true;
            handleCommitGesture();
        }
    }

    /**
     * The key listener for the clear type button.
     * Closes the editor if 'ESC' is pressed.
     */
    private class ClearButtonKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                evt.consume();
            }
        }
    }

    /**
     * SwitchTypeValueEditor constructor.
     * @param valueEditorHierarchyManager
     */
    public SwitchTypeValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        clearType = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
    
        if (clearType) {
            // Note: For TypeExpr which contains uninstantiated TypeVars, we must 'duplicate' them somehow.
            TypeExpr instanceTypeExpr = getContext().getLeastConstrainedTypeExpr();
            ValueNode replacement = getOwnerValueNode().transmuteValueNode(valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer(), instanceTypeExpr);

            // Get the TypeVar that we are instantiating.
            replaceValueNode(replacement, true);

            notifyValueCommitted();

        } else {
            super.commitValue();
        }
    }
    
    /**
     * Overrides the default implementation to only return the types that pattern match with
     * the value node type expression and are not the same type.
     * @return set of available input types to be displayed in the switch type list
     */
    @Override
    protected Set<TypeExpr> getMatchingInputTypes() {
        
        Set<TypeExpr> dataTypes = getAvailableInputTypes();
        Set<TypeExpr> matchingTypes = new HashSet<TypeExpr>();
        ModuleTypeInfo currentModuleTypeInfo = valueEditorManager.getPerspective().getWorkingModuleTypeInfo();

        // We can only type switch if the least constrained type is a TypeVar.
        TypeExpr leastConstrainedType = getContext().getLeastConstrainedTypeExpr().copyTypeExpr();
        TypeExpr valueNodeTypeExpr = getValueNode().getTypeExpr();
        
        if (currentModuleTypeInfo != null && leastConstrainedType instanceof TypeVar) {
            
            for (final TypeExpr typeExpr : dataTypes) {
    
                if (!typeExpr.sameType(valueNodeTypeExpr) && TypeExpr.canPatternMatch(typeExpr, leastConstrainedType, currentModuleTypeInfo)) {
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
    public Component getDefaultFocusComponent() {
        return messageLabel != null ? messageLabel : super.getDefaultFocusComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitialValue() {

        super.setInitialValue();

        // Setup the clear type button. Only enable it if we can clear the type.
        clearButton = new JButton(ValueEditorMessages.getString("VE_ClearType"));
        clearButton.setToolTipText(ValueEditorMessages.getString("VE_ClearTypeToolTip"));
        clearButton.addActionListener(new ClearButtonActionListener());
        clearButton.addKeyListener(new ClearButtonKeyListener());
        clearButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        // The clear button is enabled if there are items in the list, or if
        // the value node type is a function that can be cleared to its least constrained type.
        TypeExpr leastConstrainedType = getContext().getLeastConstrainedTypeExpr().copyTypeExpr();
        TypeExpr valueNodeTypeExpr = getValueNode().getTypeExpr();
        boolean canClearFunction = leastConstrainedType instanceof TypeConsApp &&
                                   leastConstrainedType.isFunctionType() &&
                                   !leastConstrainedType.sameType(valueNodeTypeExpr);

        final boolean hasOtherTypes = getIntellicutPanel().getIntellicutListModel().getSize() > 0;
        boolean enableButton = canClearFunction || hasOtherTypes;
        
        clearButton.setEnabled(enableButton);

        if (!enableButton && !hasOtherTypes) {
            
            messageLabel = new JLabel(ValueEditorMessages.getString("VE_CannotChangeType"));
            messageLabel.setIcon(new ImageIcon(GemCutter.class.getResource("/Resources/intellicut.gif")));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            messageLabel.addKeyListener(new ValueEditorKeyListener());
            
            remove(getIntellicutPanel());
            add(messageLabel, BorderLayout.CENTER);
            
            resetSize();
            
            return;
        }
        
        // Now, set up the button panel and add it to the display.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Make the focus alternate between list and button.
        FocusTraversalPolicy focusTraversalPolicy = new LayoutFocusTraversalPolicy() {
            
            private static final long serialVersionUID = -6710851570109105533L;

            @Override
            public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
                return (aComponent == clearButton && hasOtherTypes) ? getIntellicutPanel().getIntellicutList() : (Component) clearButton;
            }

            @Override
            public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
                return (aComponent == clearButton && hasOtherTypes) ? getIntellicutPanel().getIntellicutList() : (Component) clearButton;
            }

            @Override
            public Component getDefaultComponent(Container focusCycleRoot) {
                return getDefaultFocusComponent();
            }

            @Override
            public Component getFirstComponent(Container focusCycleRoot) {
                return null;
            }

            @Override
            public Component getLastComponent(Container focusCycleRoot) {
                return null;
            }
        };            
         
        setFocusTraversalPolicy(focusTraversalPolicy);
        
        resetSize();
    }
}
