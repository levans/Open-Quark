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
 * RecordFieldRenameEditor.java
 * Creation date: Oct 4, 2007
 * By: Jennifer Chen
 */

package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.utilities.ExtendedUndoManager;

/**
 * This class is responsible for displaying the correct field name editor for a given Record Creation Gem. 
 * The editor is specific to the field which it is activated on
 * 
 * @author Jennifer Chen
 */

public class RecordFieldRenameEditor extends EditableIdentifierNameField {

    private static final long serialVersionUID = -5205246630972127102L;

    /** The RecordCreationGem Gem for this editor  */
    private RecordCreationGem rcGem;

    /** The original field name before edit */
    private final String oldFieldName;

    /** The list of existing names which the new name cannot overlap with*/
    private final List<String> restrictedFieldNames;

    /** Saves the previous state of field name and inputs before the edit */
    private final Map<String, PartInput> fieldToInputMap;

    /** TableTop reference */
    private final TableTop tableTop;

    /** The undo manager for this text field */
    private ExtendedUndoManager undoManager;

    /** Keeps track of whether this component has been removed from the tableTop */
    private boolean removed = false;

    /** Index of the field which we are modifying */
    private final int fieldIndex;

    public RecordFieldRenameEditor(RecordCreationGem rcGem, FieldName fieldToRename, TableTop tabletop) {
        super();

        this.rcGem = rcGem;
        this.tableTop = tabletop;
        this.fieldToInputMap = rcGem.getFieldNameToInputMap();
        this.oldFieldName = fieldToRename.getCalSourceForm();
        this.restrictedFieldNames = rcGem.getCopyOfFieldsList();
        this.fieldIndex = restrictedFieldNames.indexOf(oldFieldName);

        // set the initial text
        setInitialText(oldFieldName);
        setText(oldFieldName);

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
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Note: when user presses Enter, an actionPerformed() in EditableIdentifierNameField will cause a 
                // commitText(), then the focusLost event will also commitText(). The extra commit is ok given that 
                // the extra UndoEdit posted is removed. Therefore in the UndoableModifyRecordFieldEdit, we need to 
                // implement the replaceEdit() method so there is only 1 edit for the rename action. 

                commitText();
            }
        });

        // intercept some key events            
        addKeyListener(new KeyAdapter() {
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

                } else if (keyStroke.equals(GemCutterActionKeys.ACCELERATOR_ARRANGE_GRAPH) || keyStroke.equals(GemCutterActionKeys.ACCELERATOR_FIT_TABLETOP)
                        || keyStroke.equals(GemCutterActionKeys.ACCELERATOR_NEW)) {

                    // We have to intercept accelerators for these so that the GemCutter 
                    // doesn't get screwed up when the text field doesn't match the action result.
                    e.consume();
                }
            }
        });

        // ensure the cursor is visible when it moves
        addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                // just ensure the caret is visible
                scrollCaretToVisible();
            }
        });
    }

    @Override
    protected boolean isValidName(String name) {

        // String is not a valid field name 
        if (!LanguageInfo.isValidFieldName(name)) {
            return false;
        }

        // Determine if the current input is duplicate to an existing name
        for (String tabooName : restrictedFieldNames) {
            if (tabooName.equals(name) && !name.equals(oldFieldName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Notify that the text of this text field has changed.  Called upon insertUpdate() and remove() completion.
     */
    @Override
    protected void textChanged() {
        super.textChanged();

        tableTop.resizeForGems();

        // ensure the caret is visible
        scrollCaretToVisible();

    }

    @Override
    protected void textChangeInvalid() {
        // Signal the user.
        setForeground(Color.lightGray);

        // Update the gem field name to display the new text (despite being invalid)
        updateFieldName(getText());

        // set a tooltip saying that the text is invalid
        String text = GemCutter.getResourceString("ToolTip_InvalidFieldName");
        String[] lines = ToolTipHelpers.splitTextIntoLines(text, 300, getFont(), ((Graphics2D)tableTop.getTableTopPanel().getGraphics()).getFontRenderContext());
        text = "<html>" + lines[0];
        for (int i = 1; i < lines.length; i++) {
            text += "<br>" + lines[i];
        }
        setToolTipText(text + "</html>");

        // update the text field to reflect the new size of the text
        updateSize();
    }

    @Override
    protected void textChangeValid() {
        setForeground(Color.black);

        // update the gem name to display the new text
        updateFieldName(getText());

        // clear any tooltip saying that the text is invalid
        setToolTipText(null);

        // update the text field to reflect the new size of the text
        updateSize();
    }

    @Override
    protected void textCommittedInvalid() {
        // revert changes
        rcGem.renameRecordField(fieldIndex, oldFieldName);

        // close this component
        closeField();
    }

    @Override
    protected void textCommittedValid() {
        // Update the gem name.
        String committedText = getText();
        updateFieldName(committedText);

        // close this component
        closeField();

        // Notify the undo manager of the name change, if any
        tableTop.updateForGemGraph();
        tableTop.getUndoableEditSupport().postEdit(new UndoableModifyRecordFieldEdit(rcGem, fieldToInputMap));

    }

    /**
     * @param newFieldName the new name for the field 
     */
    private void updateFieldName(String newFieldName) {
        rcGem.renameRecordField(fieldIndex, newFieldName);
    }

    /**
     * Update the size of this field.
     */
    private void updateSize() {
        Insets insets = getInsets();

        // The X dimension is based on the size of the text for the name (plus some margins)
        FontMetrics fm = getFontMetrics(getFont());

        // Calculate width and height
        int newWidth = fm.stringWidth(getText()) + insets.right + insets.left + 1;
        int newHeight = fm.getHeight();

        setSize(new Dimension(newWidth, newHeight));

    }

    /**
     * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
     * Overridden to return a LetterNumberUnderscorePoundDocument.
     * @return Document the default model implementation. 
     */
    @Override
    protected Document createDefaultModel() {
        //Underscores are allowed for field names
        return new LetterNumberUnderscorePoundDocument();
    }

    /**
     * Cancel text entry (press "ESC" ..)
     */
    @Override
    protected void cancelEntry() {
        // Revert to the last valid name
        setText(getInitialText());

        textCommittedInvalid();
    }

    /**
     * If this has been placed in the tabletop, make sure the caret is visible
     */
    void scrollCaretToVisible() {
        if (tableTop.getTableTopPanel().isAncestorOf(this)) {
            int dotPos = getCaret().getDot();
            try {
                Rectangle caretRect = modelToView(dotPos);

                if (caretRect != null) {
                    caretRect.width += 1;
                    Rectangle convertedRect = SwingUtilities.convertRectangle(this, caretRect, tableTop.getTableTopPanel());
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
    synchronized void closeField() {
        if (!removed) {
            removed = true;
            tableTop.getTableTopPanel().remove(this);
            tableTop.getTableTopPanel().repaint(RecordFieldRenameEditor.this.getBounds());

            // we have to do this or else you can just keep typing (..!)
            setEnabled(false);

            // Update the tabletop for the new gem graph state.
            //  Among other things, this will ensure arg name disambiguation with respect to the new collector name.
            tableTop.updateForGemGraph();
        }
        // trigger a focusLost() on this component if it had focus
        tableTop.getTableTopPanel().requestFocus();
    }
}
