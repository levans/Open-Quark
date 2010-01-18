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
 * ValueEditorTableCellRenderer.java
 * Creation date: (11/04/01 11:45:48 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ColourValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * A default cell renderer for a table representing ValueEditors.
 * Uses a simple JPanel with a label for display. (Border colour set to the correct type). 
 */
class ValueEditorTableCellRenderer extends JPanel implements TableCellRenderer {

    private static final long serialVersionUID = 6512005668479800281L;

    /** The label used to display the value of the cell. */
    private final JLabel displayLabel;

    /** Whether or not the cell being rendered is 'editable'. */
    private boolean editable;

    /**
     * Element Number corresponds to the position of this element in the list.
     * This also just happens to be (row number in the table + 1).  [We count from 1.]
     */
    private int elementNumber;

    /**
     * Flag to denote whether or not the tooltip should be prefixed with text indicating the element number.
     * Typically, this will be true for List types, and false otherwise.
     */
    private final boolean displayElementNumber;

    /** Sorta like a 'dirty' flag.  Set to true when the background color is set to selected color. */
    private boolean isSelectedColor;

    /** The value editor manager for the value editor this renderer is for. */
    private final ValueEditorManager valueEditorManager;

    /** The TypeExpr represented by this CellRenderer. */
    private final TypeExpr typeExpr;
    
    /**
     * Constructor for a new ValueEditorTableCellRenderer.
     * @param displayElementNumber whether to display the element number in the tooltip
     * @param typeExpr the type expression of the type to render
     * @param valueEditorManager the value editor manager
     */
    public ValueEditorTableCellRenderer(boolean displayElementNumber,
                                        TypeExpr typeExpr,
                                        ValueEditorManager valueEditorManager) {
        super();

        this.editable = true;
        this.typeExpr = typeExpr;
        this.valueEditorManager = valueEditorManager;
        this.displayElementNumber = displayElementNumber;

        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        if (valueEditorManager.useTypeColour()) {
            Color typeColour = valueEditorManager.getTypeColour(typeExpr);
            setBackground(typeColour);
            isSelectedColor = false;
        }

        // UI Set-up.
        displayLabel = new JLabel();
        displayLabel.setBackground(Color.white);
        displayLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        displayLabel.setOpaque(true);
        
        setLayout(new BorderLayout());
        add(displayLabel, BorderLayout.CENTER);
    }
    
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (valueEditorManager.useTypeColour()) {
            
            if (!isSelected && isSelectedColor) {
                Color typeColour = valueEditorManager.getTypeColour(typeExpr);
                setBackground(typeColour);
                isSelectedColor = false;
                
            } else if (isSelected && !isSelectedColor) {
                setBackground(Color.black);
                isSelectedColor = true;
            }
        }
        
        // Set the foreground colour.
        if (editable) {
            
            if (valueEditorManager.isFieldEditable(typeExpr)) {
                displayLabel.setForeground(Color.black);
            } else {
                displayLabel.setForeground(Color.gray);
            }
            
        } else {
            displayLabel.setForeground(Color.gray);
        }

        ValueNode valueVN = (ValueNode) value;

        // Special case when handling Colour data types.
        if (valueVN instanceof ColourValueNode) {       

            ColourValueNode colourValueNode = (ColourValueNode) valueVN;
            Color colour = colourValueNode.getColourValue();
            
            displayLabel.setBackground(colour);
            displayLabel.setForeground(colour);
            
        } else {
            displayLabel.setBackground(Color.white);
        }

        displayLabel.setText(valueVN.getTextValue());

        elementNumber = row + 1;

        return this;
    }
    
    /**
     * @return the tool tip text for the renderer (same as text shown in label)
     */
    @Override
    public String getToolTipText(java.awt.event.MouseEvent mouseEvent) {

        if (displayElementNumber) {
            return ValueEditorMessages.getString("VE_ElementNumber", new Integer(elementNumber-1), displayLabel.getText());
        } else {
            return displayLabel.getText();
        }
    }
    
    /**
     * @param isEditable whether or not the field displayed by this renderer should be editable.
     */
    public void setEditable(boolean isEditable) {
        this.editable = isEditable;
    }
}
