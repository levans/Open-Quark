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
 * RecordFieldSelectionGemFieldNameEditor.java
 * Creation date: Dec 13, 2006.
 * By: Neil Corkum
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.utilities.ExtendedUndoManager;

/**
 * This class is responsible for determining the correct field name editor to
 * display for a given Record Field Selection Gem. The editor provided will depend on the
 * types connected to the Record Field Selection Gem, if any.
 * @author Neil Corkum
 */
public class RecordFieldSelectionGemFieldNameEditor {

    /**
     * The RecordFieldSelection Gem this editor will be acting on. 
     */
    private final RecordFieldSelectionGem recordFieldSelectionGem;

    /** this is used to record the fixed state before any modifications are made
     * so that the gem can be restored to it's original state if the user aborts the
     * edit.
     */
    private final boolean oldFixedState;
    
    /** this is used to record the select field name before any modifications are made
     * so that the gem can be restored to it's original state if the user aborts the
     * edit.
     */
    private final String oldFieldName;
    
    /**
     * TableTop reference. 
     */
    private final TableTop tableTop;
    
 
    /**
     * This interface is used so the combo box editor can listen for special text events -
     * when the text is updated so that the combo box can be resized, and when
     * the editor is finished so that the combo box can close. 
     */
    private interface TextListener {

        /**
         * This is called when the text is edited and the editor has resized
         * @param dimension
         */
        void resize(Dimension dimension);
        
        /**
         * This is called when the enter or escape is pressed and the editor is closing.
         */
        void closing();
    }
    
    /**
     * An editable text field that accepts valid CAL identifiers for field names
     * @author Neil Corkum
     */
    private class RecordFieldSelectionTextEditor extends EditableIdentifierNameField {
        
        //a set containing field names that are disallowed - i.e. in the lacks constraints
        final private Set<FieldName> disallowedFields;
        
        //listener to receive test editor messages - resize and select
        public TextListener textListener = null;

 
        private static final long serialVersionUID = -3629150370610668038L;

        /** Keeps track of whether this component has been removed from the tableTop */ // is there a better way??
        private boolean removed = false;

        /** The undo manager for this text field. */
        private final ExtendedUndoManager undoManager;
        
        /**
         * Constructor for a new EditableGemNameField.
         */
        public RecordFieldSelectionTextEditor(Set<FieldName> disallowedFields) {
            super();
           
            this.disallowedFields = disallowedFields;
            
            String initialText = oldFieldName;
            
            // set the initial text
            setInitialText(initialText);
            setText(initialText);
            
            
            // set the font of the text
            setFont(GemCutterPaintHelper.getTitleFont());
            
            // update the size of the text area to reflect the size of the text
            updateSize();
            
            // starts out with all text selected
            selectAll();
            
            // set up the undo manager
            undoManager = new ExtendedUndoManager();
            getDocument().addUndoableEditListener(undoManager);
            
            // moving focus away commits the text entered and closes this component
            addFocusListener(new FocusAdapter(){
                @Override
                public void focusLost(FocusEvent e) {
                    commitText();
                }
            });
            
            // intercept some key events            
            addKeyListener(new KeyAdapter(){
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    
                    // pressing "ESC" cancels text entry and closes this component
                    if (keyCode == KeyEvent.VK_ESCAPE) {
                        cancelEntry();
                    }
                    
                    KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
                    
                    // handle undo and redo
                    if (keyStroke.equals(GemCutterActionKeys.ACCELERATOR_UNDO)) {
                        if (undoManager.canUndo()) {
                            undoManager.undo();
                            textChanged();
                        }
                        e.consume();
                        
                    } else if (keyStroke.equals(GemCutterActionKeys.ACCELERATOR_REDO)) {
                        if (undoManager.canRedo()) {
                            undoManager.redo();
                            textChanged();
                        }
                        e.consume();
                        
                    } else if (keyStroke.equals(GemCutterActionKeys.ACCELERATOR_ARRANGE_GRAPH) ||
                            keyStroke.equals(GemCutterActionKeys.ACCELERATOR_FIT_TABLETOP) ||
                            keyStroke.equals(GemCutterActionKeys.ACCELERATOR_NEW)) {
                        
                        // We have to intercept accelerators for these so that the GemCutter 
                        // doesn't get screwed up when the text field doesn't match the action result.
                        e.consume();
                    }
                }
            });
            
            // ensure the cursor is visible when it moves
            addCaretListener(new CaretListener(){
                public void caretUpdate(CaretEvent e){
                    // just ensure the caret is visible
                    scrollCaretToVisible();
                }
            });
        }

        /**
         * Returns whether a name is a valid name for this field
         * @param name String the name to check for validity 
         */
        @Override
        protected boolean isValidName(String name){
            return !disallowedFields.contains(FieldName.make(name)) && LanguageInfo.isValidFieldName(name);
        }
        
        /**
         * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
         * Overridden to return a LetterNumberUnderscorePoundDocument.
         * Creation date: (10/29/01 6:50:22 PM)
         * @return Document the default model implementation. 
         */
        @Override
        protected Document createDefaultModel() {
            return new LetterNumberUnderscorePoundDocument();
        }
        
        /**
         * Cancel text entry (press "ESC" ..)
         */
        @Override
        protected void cancelEntry(){
            // Revert to the last valid name
            setText(getInitialText());
            
            // What to do, what to do..
            textCommittedInvalid();
        }
        
        /**
         * If this has been placed in the tabletop, make sure the caret is visible
         */
        void scrollCaretToVisible(){
            if (tableTop.getTableTopPanel().isAncestorOf(this)) {
                int dotPos = getCaret().getDot();
                try {
                    Rectangle caretRect = modelToView(dotPos);
                    
                    if (caretRect != null) {
                        caretRect.width += 1;
                        Rectangle convertedRect = 
                            SwingUtilities.convertRectangle(this, caretRect, tableTop.getTableTopPanel());
                        tableTop.getTableTopPanel().scrollRectToVisible(convertedRect);
                    }
                } catch (BadLocationException e) {
                    // Nowhere to scroll.  Oh well.
                }
            }
        }

        /**
         * Close this window (if not already gone..)
         */
        synchronized void closeField(){
            // check if we've removed this already
            if (!removed) {
                removed = true;
                tableTop.getTableTopPanel().remove(this);
                tableTop.getTableTopPanel().repaint(RecordFieldSelectionTextEditor.this.getBounds());

                // we have to do this or else you can just keep typing (..!)
                setEnabled(false);
                
                // Update the tabletop for the new gem graph state.
                //  Among other things, this will ensure arg name disambiguation with respect to the new collector name.
                tableTop.updateForGemGraph();
                
                if (textListener != null) {
                    textListener.closing();
                }
            }
            // trigger a focusLost() on this component if it had focus
            tableTop.getTableTopPanel().requestFocus();
        }
        
        /**
         * Notify that the text of this text field has changed.  Called upon insertUpdate() and remove() completion.
         * Eg. If the current result is not valid, maybe do something about it (like warn the user somehow..)
         */
        @Override
        protected void textChanged(){
            super.textChanged();
            
            tableTop.resizeForGems();

            // ensure the caret is visible
            scrollCaretToVisible();
            
        }

        /**
         * Take appropriate action if the result of the text change is invalid.
         */
        @Override
        protected void textChangeInvalid(){
            // Signal the user.
            setForeground(Color.lightGray);

            // update the gem field name to display the new text (despite being invalid)
            updateGemFieldName(getText());

            // set a tooltip saying that the text is invalid
            String text = GemCutter.getResourceString("ToolTip_InvalidFieldName");
            String[] lines = ToolTipHelpers.splitTextIntoLines(text, 300, getFont(), ((Graphics2D)tableTop.getTableTopPanel().getGraphics()).getFontRenderContext());
            text = "<html>" + lines [0];
            for (int i = 1; i < lines.length; i++) {
                text += "<br>" + lines[i];
            }
            setToolTipText(text + "</html>");

            // update the text field to reflect the new size of the text
            updateSize();
        }

        /**
         * Take appropriate action if the result of the text change is valid.
         */
        @Override
        protected void textChangeValid(){
            // do validation checking - paint text colors differently depending on the result
            setForeground(Color.black);

            // update the gem name to display the new text
            updateGemFieldName(getText());

            // clear any tooltip saying that the text is invalid
            setToolTipText(null);
            
            // update the text field to reflect the new size of the text
            updateSize();
        }

        /**
         * Take appropriate action if the text committed is valid.
         */
        @Override
        protected void textCommittedInvalid(){
            revertEdit();
            
            // close this component
            closeField();
        }

        /**
         * Take appropriate action if the text committed is valid.
         */
        @Override
        protected void textCommittedValid(){
            commitEdit(getText());

            // close this component
            closeField();
        }
        
        /**
         * Update the size of this field.
         */
        private void updateSize(){
            Insets insets = getInsets();

            // The X dimension is based on the size of the text for the name (plus some margins)
            FontMetrics fm = getFontMetrics(getFont());

            // Calculate width and height
            int newWidth = fm.stringWidth(getText()) + insets.right + insets.left + 1;
            int newHeight = fm.getHeight();

            setSize(new Dimension(newWidth, newHeight));
            
            if (textListener != null) {
                textListener.resize(new Dimension(newWidth, newHeight));
            }
        }
        
        /**
         * Update the name of the gem represented by this text field.
         * @param newName String the new name for the let gem
         */
        private void updateGemFieldName(String newName){
            recordFieldSelectionGem.setFieldName(newName);
        }
    }
    
    /**
     * A combo box for editing the field name to select from records of which
     * we have some knowledge of the fields contained in the record. It
     * provides an option to enter a field of the user's choice, if the record 
     * is polymorphic.
     * @author Neil Corkum
     */
    private class RecordFieldSelectionComboEditor extends JComboBox {

        private static final long serialVersionUID = -1520958490445008579L;
        
        /** this is set when the combo box is closed - it is used to prevent the
         * combo box for duplicating actions when it is closing and committing
         * a value. This can happen when multiple events calling for the control
         * to close are fired. The combo box cannot be reused once it has been closed.*/
        private boolean closed = false;
        
        /**
         * This class implements a custom editor for the combo box that is based on a supplied 
         * RecordFieldSelectionTextEditor. This ensures the combo text editing is 
         * identical to plain record field text editor.
         */
        private class ComboEditor implements  ComboBoxEditor, DocumentListener {
            
            /** the actual text editor used by this component*/
            private final RecordFieldSelectionTextEditor text;

            /**
             * Constructs the combo box editor with a specific editor
             * @param textEditor the editor to use
             */
            public ComboEditor(RecordFieldSelectionTextEditor textEditor) {
                text = textEditor;
            }

            /**
             * {@inheritDoc}
             */
            public Component getEditorComponent() {
                return text;
            }

            /**
             * {@inheritDoc}
             */
            public void setItem(Object item) {
                text.setText(item.toString());
            }

            /**
             * {@inheritDoc}
             */
            public Object getItem() {
                return text.getText();
            }

            /**
             * {@inheritDoc}
             */
            public void selectAll() { text.selectAll(); }

            /**
             * {@inheritDoc}
             */
            public void addActionListener(ActionListener l) {
                text.addActionListener(l);
            }

            /**
             * {@inheritDoc}
             */
            public void removeActionListener(ActionListener l) {
                text.removeActionListener(l);
            }

            /**
             * {@inheritDoc}
             */
            public void insertUpdate(DocumentEvent e) {  }
            
            /**
             * {@inheritDoc}
             */
            public void removeUpdate(DocumentEvent e) {  }
            
            /**
             * {@inheritDoc}
             */
            public void changedUpdate(DocumentEvent e) { }

        }
        
        /**
         * Constructor for a RecordFieldSelectionComboEditor. 
         * @param obj objects to put in combo box list. Can include RecordFieldName 
         * and String (String only used for the "other field" item).
         */
        private RecordFieldSelectionComboEditor(Object obj[], RecordFieldSelectionTextEditor textEditor) {
            super(obj);
            
            setFont(GemCutterPaintHelper.getTitleFont());
            
            setSelectedItem(oldFieldName);
            
            //if a text editor is supplied, the field is editable
            if (textEditor != null) { 
                setEditable(true);
                setEditor(new ComboEditor(textEditor));
            }
            
            // popup menu listener to determine when to close editor and commit value
            addPopupMenuListener(new PopupMenuListener() {
                
                ///** marks whether popup has been canceled. 
                boolean cancelled = false; // is there a better way to do this?
                
                public void popupMenuCanceled(PopupMenuEvent e) {
                    // selection canceled; revert to previous value
                    cancelFieldSelection();
                    cancelled = true;
                }
                
                synchronized public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    // popup menu closing; get rid of editor and commit value if editor not canceled
                    if (!isClosed()){
                        setClosed();
                        tableTop.getTableTopPanel().remove(RecordFieldSelectionComboEditor.this);
                        if (!cancelled) {
                            commitFieldSelection();
                        }
                    }
                }
                
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {          
                }
            });

            //listen for when the value is set
            addActionListener(new ActionListener() {
                synchronized public void actionPerformed(ActionEvent a) {
                    if (isClosed()) {
                        setClosed();
                        tableTop.getTableTopPanel().remove(RecordFieldSelectionComboEditor.this);
                        commitFieldSelection();
                    }
                }           
            });
            
              
        }
        
        /**
         * @return true if the combo box has been marked as closed
         */
        public boolean isClosed() {
            return closed;
        }
        
        /**
         * mark the combo box as closed
         */
        public void setClosed() {
            closed = true;
        }
        
        /**
         * Saves change to the selected field to select. 
         */
        void commitFieldSelection() {
            if (getSelectedIndex() >= 0) {
                commitEdit(getSelectedItem().toString());
            } 

        }
        
        /**
         * Cancels any changes made to the selected field, and reverts to the
         * field name present before creating the editor.
         */
        void cancelFieldSelection() {
            revertEdit();
        }
        

        
        /**
         * Override to ensure that popup to control the visibility of the drop down options for non-polymorphic records
         */
        @Override
        public void requestFocus() {
            if (!isEditable()) {
              // always show the popup while the combo box is showing for non polymorphic records.
              setPopupVisible(true);
            }
            super.requestFocus();            
        }

    }

    /**
     * Constructor for an RecordFieldSelectionGemFieldNameEditor. To make an editor, use the static
     * method makeEditor().
     * @param recordFieldSelectionGem The RecordFieldSelection Gem this editor is associated with 
     * @param tableTop A reference to the table top on which the RecordFieldSelection Gem is present
     */
    private RecordFieldSelectionGemFieldNameEditor(RecordFieldSelectionGem recordFieldSelectionGem, TableTop tableTop) {
        this.recordFieldSelectionGem = recordFieldSelectionGem;
        this.tableTop = tableTop;
        this.oldFieldName = recordFieldSelectionGem.getFieldNameString();
        this.oldFixedState = recordFieldSelectionGem.isFieldFixed();

        //update the gem to fixed so the rendering is in fixed mode
        this.recordFieldSelectionGem.setFieldFixed(true);

        //force the gem to be redrawn
        this.recordFieldSelectionGem.setFieldName("");
        this.recordFieldSelectionGem.setFieldName(oldFieldName);
        
    }
    
    /**
     * Creates an editor for the field name to be selected. This method
     * selects the appropriate type of editor - edit box or combo box,
     * or nothing if the field may not be edited due to type constraints
     * @param recordFieldSelectionGem The RecordFieldSelection Gem this editor is associated with 
     * @param tableTop A reference to the table top on which the RecordFieldSelection Gem is present
     * @return the component for editing the field to be selected, or null if the field cannot be edited.
     */
    public static JComponent makeEditor(RecordFieldSelectionGem recordFieldSelectionGem, TableTop tableTop) {
        // field editor to return
        final JComponent fieldEditor;
 
        final PartInput inputPart = recordFieldSelectionGem.getInputPart();
        
        final TypeExpr inputTypeExpr;
        TypeExpr outputTypeExpr = TypeExpr.makeParametricType();
        
        //determine the input type 
        if (inputPart.isConnected() ) {
            inputTypeExpr = inputPart.inferType(tableTop.getTypeCheckInfo()) ;
            if (recordFieldSelectionGem.getOutputPart().getConnection() != null) {
                outputTypeExpr = recordFieldSelectionGem.getOutputPart().inferType(tableTop.getTypeCheckInfo());
            }
        } else if (inputPart.isBurnt() && recordFieldSelectionGem.getOutputPart().getConnection() != null) {   
            TypeExpr outputType = recordFieldSelectionGem.getOutputPart().inferType(tableTop.getTypeCheckInfo());
            
            if (outputType.getArity() > 0) {
                TypeExpr[] typePieces = outputType.getTypePieces(1);
                inputTypeExpr = typePieces[0];
                outputTypeExpr = typePieces[1];
            } else {
                inputTypeExpr = null;
            }
        } else {
            inputTypeExpr = null;
        }

        RecordFieldSelectionGemFieldNameEditor editor = new RecordFieldSelectionGemFieldNameEditor(recordFieldSelectionGem, tableTop);
        
        //create the appropriate editor type
        if (inputTypeExpr instanceof RecordType) {
            RecordType inputType = (RecordType)inputTypeExpr;
            
            Set<FieldName> disallowedFields = new HashSet<FieldName>(inputType.getLacksFieldsSet());
            disallowedFields.removeAll(inputType.getHasFieldNames()); 
            
            List<FieldName> possibleFields = RecordFieldSelectionGem.possibleFieldNames(inputType, outputTypeExpr, tableTop.getCurrentModuleTypeInfo());
                  
            if (inputType.isRecordPolymorphic()) {
                if (possibleFields.isEmpty()) {
                    fieldEditor = editor.createTextFieldEditor(disallowedFields);
                } else {
                    fieldEditor = editor.createComboFieldEditor(possibleFields, editor.createTextFieldEditor(disallowedFields)); 
                }
            } else {
                if (possibleFields.size() == 1) {
                    //there is only one option - no reason to edit
                    fieldEditor = null;
                } else {
                    fieldEditor = editor.createComboFieldEditor(possibleFields, null);  
                }
            }
        } else {
            // input generic/not connected; use text editor
            fieldEditor = editor.createTextFieldEditor(Collections.<FieldName>emptySet());
        }
        
        return fieldEditor;
    }
    
    /**
     * Commits an edit and creates an undo action.
     */
    private void commitEdit(String newFieldName) {
        recordFieldSelectionGem.setFieldFixed(true);
        recordFieldSelectionGem.setFieldName(newFieldName);
        tableTop.getUndoableEditSupport().postEdit(new UndoableChangeFieldSelectionEdit(tableTop, recordFieldSelectionGem, oldFieldName, oldFixedState));
        tableTop.updateForGemGraph();
    }
    
    /**
     * Reverts any changes to the record field selection gem
     */
    private void revertEdit() {
        recordFieldSelectionGem.setFieldFixed(oldFixedState);
        recordFieldSelectionGem.setFieldName(oldFieldName);
    }
    
    /**
     * Creates a text field-style editor. This is used when it is appropriate
     * to allow the user to input any valid field name for extraction.
     */
    private RecordFieldSelectionTextEditor createTextFieldEditor(Set<FieldName> disallowedFields) {
        // not connected to record input; display text field for editing field name
        RecordFieldSelectionTextEditor fieldEditor = new RecordFieldSelectionTextEditor(disallowedFields);
        
        fieldEditor.setFont(GemCutterPaintHelper.getTitleFont());

        // ensure the cursor is visible
        fieldEditor.scrollCaretToVisible();
        
        return fieldEditor;
    }
    
    /**
     * Creates a combo box-style editor. This is used when some or all of the 
     * fields available in a record to be extracted from are known.
     * @param validFieldNames a list of possible field names to display in the combo box
     * @param textEditor if the record type is polymorphic this is the editor used to enter new field names, if the record is non polymorphic this should be null.
     */
    private JComponent createComboFieldEditor(final List<FieldName> validFieldNames, final RecordFieldSelectionTextEditor textEditor) {
        
        final RecordFieldSelectionComboEditor fieldComboEditor = new RecordFieldSelectionComboEditor(validFieldNames.toArray(), textEditor);

        //set the selection to the existing value of it is in the list of values 
        fieldComboEditor.setSelectedItem(oldFieldName);        
        final int currentIndex = validFieldNames.indexOf(FieldName.make(oldFieldName));
        if (currentIndex >=0 ) {
            fieldComboEditor.setSelectedIndex(currentIndex);
        }
            
        fieldComboEditor.setSize(fieldComboEditor.getPreferredSize());
        
        //if the text editor is supplied we must catch size changes and closes
        if (textEditor != null) { 
            textEditor.selectAll();
            
            final int sizeDiff = fieldComboEditor.getPreferredSize().width - textEditor.getPreferredSize().width;
            
            //listen for text update and closing events
            textEditor.textListener = 
                new TextListener() {
                    public void resize(Dimension d) {
                        fieldComboEditor.setSize(d.width + sizeDiff,fieldComboEditor.getHeight() );
                    }
                    public void closing() {
                        if (!fieldComboEditor.isClosed()){
                            fieldComboEditor.setClosed();
                            tableTop.getTableTopPanel().remove(fieldComboEditor);
                        }
                    }
                }; 

            //listen for when the text value is set value is set
            textEditor.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent a) {
                    if (!fieldComboEditor.isClosed()){
                        fieldComboEditor.setClosed();
                        tableTop.getTableTopPanel().remove(fieldComboEditor);
                        
                        if (!textEditor.isValidName(textEditor.getText())) {
                            textEditor.cancelEntry();
                        }

                        fieldComboEditor.commitFieldSelection();
                    }
                }           
            });
        } else {
            //if there is no text field we must watch for focus lost events
            //which would would otherwise be taken care of by the text field
            fieldComboEditor.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {}
                public void focusLost(FocusEvent e) {
                    if (!fieldComboEditor.isClosed()){
                        fieldComboEditor.setClosed();
                        tableTop.getTableTopPanel().remove(fieldComboEditor);
                        fieldComboEditor.cancelFieldSelection();                    
                    }
                }
                
            });
            //catch the escape key
            fieldComboEditor.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e)  {}
                public void keyTyped(KeyEvent e)  {
                    int keyCode = e.getKeyCode();
                    
                    // pressing "ESC" cancels entry and closes this component
                    if (keyCode == KeyEvent.VK_ESCAPE) {
                        if (!fieldComboEditor.isClosed()){
                            fieldComboEditor.setClosed();
                            tableTop.getTableTopPanel().remove(fieldComboEditor);
                            fieldComboEditor.cancelFieldSelection();                            
                        }
                    }
                    
                }
            });           
        }
        return fieldComboEditor;
    }
    
}
