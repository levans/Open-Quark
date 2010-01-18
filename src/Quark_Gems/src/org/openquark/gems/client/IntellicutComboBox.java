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
 * IntellicutComboBox.java
 * Creation date: Feb 21-2002
 * By: David Mosimann
 */
package org.openquark.gems.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.gems.client.IntellicutListModelAdapter.InputOutputAdapter;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;


/**
 * Encapsulates the intellicut functionality in a combo box.  The combo box drop down will show the valid
 * gems as selected and rendered by the intellicut functionality.
 * @author dmosimann
 */
public class IntellicutComboBox extends JComboBox {

    private static final long serialVersionUID = -6807031219239353161L;

    /** The timer used to show the combobox popup list. */
    private final Timer popupTimer;
    
    /** The delay before the popup timer fires. */
    private static final int POPUP_TIMER_DELAY = 500;
    
    /** If true the list should be updated when the popup becomes visible. */
    private boolean updateList = true;

    private class IntellicutListener extends KeyAdapter implements PopupMenuListener {

        @Override
        public void keyReleased(KeyEvent e) {
            
            // Ignore action keys that don't affect the text.
            if (e.isActionKey() || ignoreKey(e.getKeyCode())) {
                return;
            }

            
            // Update the list if the popup is actually visible, 
            // otherwise start a timer to show the popup.                
            if (isPopupVisible()) {

                // Set the text to the text of the selected item.
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    
                    IntellicutListEntry selected = (IntellicutListEntry) getSelectedItem();
                    
                    if (selected != null) {
                        JTextField textField = (JTextField) getEditor().getEditorComponent();
                        textField.setText(selected.getDisplayString());
                        hidePopup();
                    }
                    
                    return;
                }
                
                updateList();
                
            } else if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '_' ||
                       e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                
                popupTimer.restart();
            }
        }

        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (updateList) {
                updateList();
            }
        }

        public void popupMenuCanceled(PopupMenuEvent e) {
        }
            
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { 
        }

        /**
         * @param keyCode the keycode of the key to check
         * @return boolean true if the key with the given key code should be ignored.
         */
        private boolean ignoreKey(int keyCode) {

            switch (keyCode) {
                case KeyEvent.VK_SHIFT:
                case KeyEvent.VK_CONTROL:
                case KeyEvent.VK_ALT:
                case KeyEvent.VK_PAUSE:
                case KeyEvent.VK_CAPS_LOCK:
                case KeyEvent.VK_ESCAPE:
                    return true;
            }
                
            return false;
        }
    }
       
    /**
     * Constructor for IntellicutComboBox.
     * @param inputExpr a list of valid input types to the gems
     * @param outputExpr a list of valid output types to the gems
     * @param workspace the workspace to load gems from
     * @param typeCheckInfo the type check info to use
     */
    public IntellicutComboBox(TypeExpr[] inputExpr,
                              TypeExpr[] outputExpr,
                              CALWorkspace workspace,
                              TypeCheckInfo typeCheckInfo) {
        
        setName("MatchingGemsList");
        setBounds(0, 0, 160, 120);
        setEditable(true);
        setRenderer(new IntellicutListRenderer(IntellicutMode.NOTHING));
        
        InputOutputAdapter adapter = new InputOutputAdapter(inputExpr, outputExpr, workspace, typeCheckInfo);
        setModel(new IntellicutListModel (adapter));
        
        // Set a preferred size so that the list is not its default narrow width.
        Dimension prefSize = getPreferredSize();
        setPreferredSize(new Dimension(200, prefSize.height));

        popupTimer = new Timer(POPUP_TIMER_DELAY, this);
        popupTimer.setRepeats(false);

        // This listener updates the popup list if the user types text.
        IntellicutListener listener = new IntellicutListener();
        getEditor().getEditorComponent().addKeyListener(listener);
        addPopupMenuListener(listener);
    }

    /**
     * This method is called when the popup timer fires.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        
        if (evt.getSource() == popupTimer) {
            if (hasFocus() || getEditor().getEditorComponent().hasFocus()) {
                if (!isPopupVisible()) {

                    updateList();
                    
                    // Only show the popup if there are any items to show.
                    if (getModel().getSize() > 0) {
                        updateList = false;
                        showPopup();
                        updateList = true;
                    }
                }
            }
        }
    }
    
    /**
     * Forces the list of valid gems to be rebuilt for the new input and output type expressions.  This
     * will also close the popup window if it is open since its values are no longer valid.
     * @param inputExpr a list of valid input types to the gems
     * @param outputExpr a list of valid output types to the gems
     */
    public void rebuildList(TypeExpr[] inputExpr, TypeExpr[] outputExpr) {
        hidePopup();
        InputOutputAdapter adapter = (InputOutputAdapter) ((IntellicutListModel) getModel()).getAdapter();
        adapter.reset(inputExpr, outputExpr);
    }

    /**
     * Updates the visible list of gems based on what the user has typed so far.
     * It will also ensure that the text display in the text entry field is correct.
     */
    private void updateList() {

        IntellicutListModel listModel = (IntellicutListModel) getModel();
        JTextField textField = (JTextField) getEditor().getEditorComponent();
  
        String userText = textField.getText();
        int oldNumItems = listModel.getSize();

        // Refresh the list.
        listModel.refreshList(userText);
        
        // If the number of items in the list changes then we have to hide and reshow the
        // list so that it resizes itself correctly. If there are no items to show just
        // hide the list.
        if (listModel.getSize() == 0) {
            
            // Check if the popup is already visible. If the user clicked the popup button
            // to show it then this method gets called from the popupWillBecomeVisible method
            // in the listener. If we hide the popup while it is not quite shown yet the list
            // gets screwed up and the user wont be able to hide the popup anymore.
            if (isPopupVisible()) {
                hidePopup();
            }
        
        } else if (listModel.getSize() != oldNumItems && isPopupVisible()) {
            updateList = false;
            hidePopup();
            showPopup();
            updateList = true;
        }
        
        // If the list is visible make sure no item is selected.
        if (isPopupVisible()) {
            setSelectedIndex(-1);
        }

        // Put the original text back into the textfield and request focus for it.
        textField.setText(userText);
        textField.setCaretPosition(userText.length());
        textField.requestFocus();
    }
}