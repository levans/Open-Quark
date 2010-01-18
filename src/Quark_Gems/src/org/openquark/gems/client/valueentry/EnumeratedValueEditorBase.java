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
 * EnumeratedValueEditorBase.java
 * Created: ??
 * By: Richard Webster
 */
package org.openquark.gems.client.valueentry;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;


/**
 * A super class for value editors which allow the user to choose from a list of values.
 * @author Richard Webster
 */
public abstract class EnumeratedValueEditorBase extends ValueEditor {

    /**
     * MouseAdapter used to listen for double clicks (Must be left clicks).
     * (Which would commit the user's choice).
     */
    private class EnumeratedValueEditorMouseAdapter extends MouseClickDragAdapter {
        /**
         * Surrogate method for mouseClicked.  Called only when our definition of click occurs.
         * @param e MouseEvent the relevant event
         * @return boolean true if the click was a double click
         */
        @Override
        public boolean mouseReallyClicked(MouseEvent e) {
            boolean doubleClicked = super.mouseReallyClicked(e);

            // double left-click?
            if (doubleClicked && SwingUtilities.isLeftMouseButton(e)) {
                handleCommitGesture();
            }

            return doubleClicked;
        }
    }
    
    /** The value that should be highlighted in the list. */
    private ValueNode highlightValue;
    
    private JList ivjEnumeratedValueList = null;
    private JScrollPane ivjEnumeratedValueScrollPane = null;

    /**
     * EnumeratedValueEditorBase constructor.
     * @param valueEditorHierarchyManager
     */
    protected EnumeratedValueEditorBase(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {

        valueEditorManager.associateInfo(getOwnerValueNode(), new ValueEditor.Info(getSize()));

        ValueNode returnVN = (ValueNode) getEnumeratedValueList().getSelectedValue();
        if (returnVN != null) {
            replaceValueNode(returnVN, true);
            notifyValueCommitted();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return getEnumeratedValueList();
    }

    /**
     * Return the EnumeratedValueEditorBorderLayout property value.
     * @return BorderLayout
     */
    private BorderLayout getEnumeratedValueEditorBorderLayout() {
        BorderLayout ivjEnumeratedValueEditorBorderLayout = null;
        try {
            /* Create part */
            ivjEnumeratedValueEditorBorderLayout = new BorderLayout();
            ivjEnumeratedValueEditorBorderLayout.setVgap(5);
            ivjEnumeratedValueEditorBorderLayout.setHgap(5);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjEnumeratedValueEditorBorderLayout;
    }
    
    /**
     * Return the EnumeratedValueList property value.
     * @return JList
     */
    private JList getEnumeratedValueList() {
        if (ivjEnumeratedValueList == null) {
            try {
                ivjEnumeratedValueList = new JList();
                ivjEnumeratedValueList.setName("EnumeratedValueList");
                ivjEnumeratedValueList.setBounds(0, 0, 160, 120);
                ivjEnumeratedValueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
               
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjEnumeratedValueList;
    }
    
    /**
     * Return the EnumeratedValueScrollPane property value.
     * @return JScrollPane
     */
    private JScrollPane getEnumeratedValueScrollPane() {
        if (ivjEnumeratedValueScrollPane == null) {
            try {
                ivjEnumeratedValueScrollPane = new JScrollPane();
                ivjEnumeratedValueScrollPane.setName("EnumeratedValueScrollPane");
                ivjEnumeratedValueScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                getEnumeratedValueScrollPane().setViewportView(getEnumeratedValueList());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjEnumeratedValueScrollPane;
    }
    
    /**
     * Returns the JList used to display the various value options.
     * Creation date: (05/03/01 11:08:12 AM)
     * @return JList
     */
    protected JList getListDisplay() {
        return getEnumeratedValueList();
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
     * Sets the value that should be highlighted with a different colour in the value list.
     * @param valueNode the value that should be highlighted
     */
    void setHighlightedValue(ValueNode valueNode) {
        this.highlightValue = valueNode;
    }
      
    /**
     * Initialize the class.
     * Note: Extra set-up code has been added.
     */
    private void initialize() {
        
        setName("EnumeratedValueEditor");
        setLayout(getEnumeratedValueEditorBorderLayout());
        setResizable(true);
        
        add(getEnumeratedValueScrollPane(), "Center");

        // Make sure that this EnumeratedValueEditor handles user's commit or cancel input.
        getEnumeratedValueList().addKeyListener(new ValueEditorKeyListener());

        getEnumeratedValueList().setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = -6142474728739005212L;

                @Override
                public Component getListCellRendererComponent(JList list,
                                                              Object value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus)
                {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    ValueNode vn = (ValueNode) value;
                    
                    label.setText(vn.getTextValue());
                    label.setToolTipText(vn.getTextValue());
                    
                    if (highlightValue != null && vn.sameValue(highlightValue)) {
                        label.setForeground(isSelected ? Color.WHITE : Color.BLUE);
                    }
                    
                    return label;
                }
        });

        // Set default cursor for components that should have only ever have default cursors
        // (Mouse pointer changes near the edge of the border.
        getEnumeratedValueScrollPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        // Allow double-click on the list to commit the user input.
        getEnumeratedValueList().addMouseListener(new EnumeratedValueEditorMouseAdapter());
    }

    /**
     * Sets the initial value.
     * Check Base class ValueEditor for more description.
     * Creation date: (28/02/01 10:18:37 AM)
     */
    @Override
    public void setInitialValue() {

        // Select the DataConstructor which matches with the current ValueNode's DataConstructor.
        DefaultListModel listModel = (DefaultListModel) getEnumeratedValueList().getModel();
        ValueNode searchValueNode = getValueNode();

        for (int i = 0, listSize = listModel.getSize(); i < listSize; i++) {
            ValueNode vn = (ValueNode) listModel.get(i);

            if (vn.sameValue(searchValueNode)) {
                getEnumeratedValueList().setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * Sets the ValueNode for this EnumeratedValueEditor and initializes some of the UI set-up.
     * @param newValueNode 
     */
    @Override
    public void setOwnerValueNode(ValueNode newValueNode) {

        super.setOwnerValueNode(newValueNode);

        // Add values to the list.
        DefaultListModel defaultListModel = new DefaultListModel();
        List<ValueNode> valueList = getValueList();
        
        for (final ValueNode valueListElem : valueList) {
            defaultListModel.addElement(valueListElem);
        }
        
        JList enumValueList = getEnumeratedValueList();
        enumValueList.setModel(defaultListModel);

        // Ideally we want to be the size of the value list.
        Dimension borders = getBorderSize();
        Dimension listSize = enumValueList.getPreferredSize();
        Dimension bestSize = new Dimension(listSize.width + borders.width, listSize.height + borders.height);

        // Compute intelligent sizes for the editor.
        // We pick 'False' for the minimum width so that the boolean data type fits perfectly.
        FontMetrics metrics = enumValueList.getFontMetrics(enumValueList.getFont());
        int smallestHeight = enumValueList.getCellBounds(0, 0).height;
        int smallestWidth = metrics.stringWidth("False") + borders.width;
        
        // Setup the sizes. Maximum is what is needed to display all items.
        setMaxResizeDimension(bestSize);
        setMinResizeDimension(new Dimension (smallestWidth, smallestHeight + borders.height));

        // Preferred size is 200 pixels wide and high enough to display 10 items.        
        Dimension prefSize = new Dimension (200, 10 * smallestHeight + borders.height);

        // If there is a saved size use it.
        ValueEditor.Info info = valueEditorManager.getInfo(getOwnerValueNode());
        if (info != null) {
            setSize(info.getEditorSize());
        } else {
            // Set the list size. Be sure not to exceed the preferred size.
            setSize(new Dimension(Math.min(bestSize.width, prefSize.width),
                                  Math.min(bestSize.height, prefSize.height)));
        }
    }

    /**
     * Calculates the total dimension of all borders around the value list of
     * the value editor. The size for the value editor should include the size
     * intended for the value list plus the size of the total border.
     * 
     * @return a dimension with the total border height and width
     */
    private Dimension getBorderSize() {

        // The margins of the text area.
        Insets insets = getEnumeratedValueScrollPane().getInsets();
        int borderHeight = insets.top + insets.bottom;
        int borderWidth = insets.left + insets.right;

        // The border of the value editor itself.
        //insets = getInsets();
        insets = getInsets();                
        borderHeight += insets.top + insets.bottom;
        borderWidth += insets.left + insets.right; 
        
        // The borders of the scrollbar for the list.
        JScrollBar scrollbar = new JScrollBar(Adjustable.VERTICAL);
        borderWidth += scrollbar.getPreferredSize().width;        

        // Add 5 pixels to the width for good looks
        borderWidth += 5;
        
        return new Dimension (borderWidth, borderHeight);
    }

    /**
     * Returns a list of ValueNode objects to be displayed in the list.
     */
    protected abstract List<ValueNode> getValueList();
}
