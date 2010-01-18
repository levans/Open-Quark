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
 * StringValueEditor.java
 * Creation date: (02/03/01 10:20:15 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openquark.cal.valuenode.ListOfCharValueNode;
import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * The ValueEditor used to edit the String type and the [Char] type.
 * Creation date: (02/03/01 10:20:15 AM)
 * @author Michael Cheng
 */
class StringValueEditor extends ValueEditor {

    private static final long serialVersionUID = 6253045166403650749L;

    /**
     * A custom value editor provider for the StringValueEditor.
     */
    public static class StringValueEditorProvider extends ValueEditorProvider<StringValueEditor> {

        public StringValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof ListOfCharValueNode || 
                  (valueNode instanceof LiteralValueNode &&
                    valueNode.getTypeExpr().sameType(getValueEditorManager().getValueNodeBuilderHelper().getPreludeTypeConstants().getStringType()));
        }
        
        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public StringValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            StringValueEditor editor = new StringValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean usableForOutput() {
            return true;
        }
    }

    /**
     * Very similar to ValueEditorKeyListener, with the exception that
     * Alt + Enter is required for a commit, instead of just Enter.
     */
    public class StringValueEditorKeyListener extends ValueEditorKeyListener {
        @Override
        public void keyPressed(KeyEvent evt) {

            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) && evt.isAltDown()) {
                handleCommitGesture();
                evt.consume(); // Don't want the control with the focus to perform its action.

            } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                evt.consume();
            }
        }
    }

    /**
     * This listener grows the text field up to a maximum size
     * if the user enters additional text.
     */
    private class StringValueEditorDocumentListener implements DocumentListener {
        
        public void insertUpdate (DocumentEvent e) {
            
            // We can't resize the text are while the document insert hasn't completed.
            // Doing the resize at this time can cause problems with text layout for the area.
            // Therefore we invoke the resize once AWT events have finished processing.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    resetSize();
                }
            });
        }

        public void changedUpdate (DocumentEvent e) { 
        }
        
        public void removeUpdate (DocumentEvent e) {   
        }
    }
    
    /** 
     * This constant is the maximum number of lines the editor will
     * display by default when first being shown and is also the maximum
     * number of lines it will grow to when the user enters text.
     */
    public static final int DEFAULT_MAXIMUM_LINES = 40;

    /** The default maximum width in pixels the editor will display or grow to. */
    public static final int DEFAULT_MAXIMUM_WIDTH = 600;

    /** True if the user manually resizes this editor. */
    private boolean userHasResized = false;

    /** The scroll pane that contains the text area. */
    private JScrollPane ivjJScrollPane1 = null;
    
    /** The text area for editing the text. */
    private JTextArea ivjTextArea = null;    
    
    /**
     * StringValueEditor constructor comment.
     * @param valueEditorHierarchyManager
     */
    protected StringValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {

        String stringValue = getTextArea().getText();
        ValueNode valueNode = getValueNode();

        if (valueNode instanceof ListOfCharValueNode || 
            valueNode instanceof ListValueNode) {       // TEMP (?): Lists of Chars are collapsed to ListOfChars

            ValueNode returnVN = new ListOfCharValueNode(stringValue, getValueNode().getTypeExpr());
            replaceValueNode(returnVN, true);

        } else if (valueNode instanceof LiteralValueNode){
            ValueNode returnVN = new LiteralValueNode(stringValue, getValueNode().getTypeExpr());
            replaceValueNode(returnVN, true);

        } else {
            throw new IllegalStateException("This value node cannot be handled by the string editor: " + valueNode.getClass());
        }
        
        valueEditorManager.associateInfo(getOwnerValueNode(), new ValueEditor.Info(getSize()));

        notifyValueCommitted();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return getTextArea();
    }

    /**
     * Return the JScrollPane1 property value.
     * @return JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (ivjJScrollPane1 == null) {
            try {
                ivjJScrollPane1 = new JScrollPane();
                ivjJScrollPane1.setName("ScrollPane");
                ivjJScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                ivjJScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                getScrollPane().setViewportView(getTextArea());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane1;
    }
    
    /**
     * Return the TextArea property value.
     * @return JTextArea
     */
    private JTextArea getTextArea() {
        if (ivjTextArea == null) {
            try {
                ivjTextArea = new JTextArea();
                ivjTextArea.setName("TextArea");
                ivjTextArea.setLineWrap(false);
                ivjTextArea.setWrapStyleWord(true);
                ivjTextArea.setBounds(0, 0, 160, 120);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTextArea;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
    
    /**
     * Initialize the class.
     * Note: Extra Set-up Code has been added.
     */
    private void initialize() {
        try {
            setName("StringValueEditor");
            setLayout(new BorderLayout());
            add(getScrollPane(), "Center");
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Make sure that this StringValueEditor handles user's commit or cancel input.
        getTextArea().addKeyListener(new StringValueEditorKeyListener());

        // Set default cursor for components that should have only ever have default cursors
        // (Mouse pointer changes near the edge of the border.
        getScrollPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {

        String stringVal = null;
        if (getValueNode() instanceof ListOfCharValueNode) {
            stringVal = ((ListOfCharValueNode)getValueNode()).getStringValue();
        } else if (getValueNode() instanceof LiteralValueNode) {
            stringVal = ((LiteralValueNode)getValueNode()).getStringValue();            
        } else {
            stringVal = getValueNode().getTextValue();
        }
        
        getTextArea().setText(stringVal);
        getTextArea().setCaretPosition(0);
        
        resetSize();
        setResizable(true);
        
        getTextArea().getDocument().addDocumentListener(new StringValueEditorDocumentListener ());
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#userHasResized()
     */
    @Override
    protected void userHasResized() {
        // If the user does resize the editor we want to enable line wrapping.
        getTextArea().setLineWrap(true);
        userHasResized = true;
    }
  
    /**
     * Sets the size of the editor depending on the preferred size of the text area.
     */
    private void resetSize () {

        boolean isEditable = isEditable();
        int lineHeight = getTextArea().getFontMetrics(getTextArea().getFont()).getHeight();
        Dimension borders = getBorderSize();
        Dimension bestSize = getTextArea().getPreferredSize();
        
        // Always set a reasonable minimum size.
        setMinResizeDimension(new Dimension (100, lineHeight + borders.height));        
        
        // Don't resize the editor if the user manually picked a size they like.
        if (userHasResized) {
            return;
        }
        
        // Restore the saved size if there is any.
        ValueEditor.Info info = valueEditorManager.getInfo(getOwnerValueNode());
        if (info != null) {
            userHasResized = true;
            getTextArea().setLineWrap(true);
            setSize(info.getEditorSize());
            return;
        }
        
        bestSize.height += borders.height;
        bestSize.width += borders.width;

        // Give the text area a dummy default size. Do this so that the BasicTextUI
        // will return a sensible value for the preferred size of the text area.
        getTextArea().setSize(DEFAULT_MAXIMUM_WIDTH, DEFAULT_MAXIMUM_LINES * lineHeight);

        if (isEditable) {
            setMaxResizeDimension(null);
        } else {
            setMaxResizeDimension(bestSize);
        }

        // Compare the new sizes with the current size
        // If they are bigger then we want to grow the editor
        Dimension currentSize = getSize();
        int newHeight = getSize().height;
        int newWidth = getSize().width;

        if (!isEditable || bestSize.height > currentSize.height) {
            newHeight = Math.min (bestSize.height, DEFAULT_MAXIMUM_LINES * lineHeight);
        }
            
        if (!isEditable || bestSize.width > currentSize.width) {
            newWidth = Math.min (bestSize.width, DEFAULT_MAXIMUM_WIDTH);
        }
        
        if (isEditable) {
            // Make sure the editor has a reasonable size if it is editable and the text is small
            newHeight = Math.max(newHeight, lineHeight * 10);
            newWidth = Math.max(newWidth, 300);
        }
        
        // If there are lines longer than the maximum size we grow to, then enable line
        // wrapping. This causes the preferred size of the text area to change, so we
        // have to reset the size again after we do this.
        if (bestSize.width > DEFAULT_MAXIMUM_WIDTH && !getTextArea().getLineWrap()) {
            getTextArea().setLineWrap(true);
            resetSize();
            return;
        }

        // Pick the new size so that we don't accidentally go to a smaller
        // size than we are now. Sometimes the new size ca be smaller if the
        // user erases characters and enters new characters that are narrower.
        setSize (new Dimension (Math.max (newWidth, currentSize.width),
                                Math.max (newHeight, currentSize.height)));        
        
        revalidate();
    }
    
    /**
     * Calculates the total dimension of all borders around the text area of
     * the value editor. The size for the value editor should include the size
     * intended for the text area plus the size of the total border.
     * 
     * @return a dimension with the total border height and width
     */
    private Dimension getBorderSize() {
        
        // The border of the scrollpane.
        Insets insets = getScrollPane().getInsets();
        int borderHeight = insets.top + insets.bottom;
        int borderWidth = insets.left + insets.right;        

        // The border of the value editor itself.
        insets = getInsets();                
        borderHeight += insets.top + insets.bottom;
        borderWidth += insets.left + insets.right; 
        
        // The size of the scrollbars.
        JScrollBar scrollbar = new JScrollBar(Adjustable.HORIZONTAL);
        borderHeight += scrollbar.getPreferredSize().height;
        scrollbar = new JScrollBar(Adjustable.VERTICAL);
        borderWidth += scrollbar.getPreferredSize().width;        

        // Add 5 pixels to the width for good looks
        borderWidth += 5;
        
        return new Dimension (borderWidth, borderHeight);
    }
    
    /**
     * Set the editable status of this StringValueEditor, and make any
     * necessary internal adjustments.
     */
    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        getTextArea().setEditable(editable);
        getTextArea().setForeground(editable ? Color.black : Color.gray);
    }
}
