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
 * PickListValueEditor.java
 * Created: Feb 23, 2004
 * By: David Mosimann
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JComboBox;

import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.ValueNode;



/**
 * Provides a value editor suitable for picking a single value from a pick list.  If the value node is a list
 * type then the PickListValueEntryPanel should be used instead.  This editor only deals with a single value
 * by using a standard combo box component to display the default values to the user.
 */
public class PickListValueEditor extends ValueEditor {

    private static final long serialVersionUID = 6807747148406965455L;

    /** The combo box used to display the list of possible values */
    private final JComboBox comboBox;
    
    /**
     * KeyListener used to listen for user's commit(Enter) and cancel (Esc)
     * Register the key listener on the textfield of this ValueEntryPanel.
     */
    private class CommitOrCancelKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {

            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                handleCommitGesture();
                evt.consume(); // Don't want the control with the focus to perform its action.

            } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                evt.consume();

            } else if ((evt.getKeyCode() == KeyEvent.VK_X) && evt.isControlDown()) {
                // Only allow if editable.
                if (isEditable()) {
                    cutToClipboard();
                }
                evt.consume();

            } else if ((evt.getKeyCode() == KeyEvent.VK_C) && evt.isControlDown()) {
                copyToClipboard();
                evt.consume();

            } else if ((evt.getKeyCode() == KeyEvent.VK_V) && evt.isControlDown()) {
                // Only allow if editable.
                if (isEditable()) {
                    pasteFromClipboard();
                }
                evt.consume();
            }
        }
    }

    /**
     * Constructor for the pick list value editor
     * @param valueEditorHierarchyManager
     * @param dataVN
     * @param defaultValues
     * @param defaultValuesOnly
     */
    public PickListValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager,
                               ValueNode dataVN,
                               ListValueNode defaultValues,
                               boolean defaultValuesOnly) {
        super(valueEditorHierarchyManager);
        
        // Create a new combo box with the desired default value list
        List<ValueNode> values = defaultValues.getValue();
        comboBox = createComboBox(values.toArray());
        comboBox.addKeyListener(new CommitOrCancelKeyListener());
        comboBox.setSelectedItem(dataVN);

        // Disallow editing in the text field if we are only using default values
        comboBox.setEditable(!defaultValuesOnly);
        
        // Initialize our border according to what the manager wants
        setBorder(valueEditorManager.getValueEditorBorder(this));

        // Add the combo box to this component
        setLayout(new BorderLayout());
        add(comboBox, "Center");
    }

    /**
     * Get the component which by default has focus.
     * This will be called, for instance, when the editor is activated
     * @return Component the default component to receive focus, or null if none.
     */
    @Override
    public Component getDefaultFocusComponent() {
        return comboBox;
    }

    /**
     * Commit the value node currently under edit in this editor.
     */
    @Override
    protected void commitValue() {
        // Get the current value and if it's changed the replace our current value and commit
        Object selection = comboBox.getSelectedItem();
        if (selection instanceof ValueNode) {
            ValueNode newVN = (ValueNode)selection;
            if (newVN != getValueNode()) {
                replaceValueNode(newVN, false);
                super.commitValue();
            }
        } else {
            throw new IllegalStateException("The current combo box selection should be a value node");
        }
    }

    /**
     * Commit the value node currently under edit in this editor.
     */
    @Override
    protected void cancelValue() {
        // Do nothing since selected items in the combo box doesn't actually do anything
        super.cancelValue();
    }

    /**
     * Overridden to satisfy the ValueEditor abstract base class, but nothing needs to be done
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {
    }

    /**
     * Sets the ownerValueNode for this ValueEditor.
     * Also initializes the background/border colour of this ValueEditor.
     * @param newValueNode 
     */
    @Override
    public void setOwnerValueNode(ValueNode newValueNode) {
        super.setOwnerValueNode(newValueNode);
        
        // Setting the owner value node may have changed our value node so set the current pick list
        // selection again.  Note that this method is called before our constructor is finished
        comboBox.setSelectedItem(getValueNode());        
    }

    /**
     * Creates a new combo box and returns it.  Subclasses can override this to change the combo box that is
     * returned.
     * @param values The initial values for the combo box.
     * @return a new JComboBox
     */
    protected JComboBox createComboBox(Object[] values) {
        return new JComboBox(values);
    }
}
