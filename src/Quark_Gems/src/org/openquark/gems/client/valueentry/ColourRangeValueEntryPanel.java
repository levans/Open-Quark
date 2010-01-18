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
 * ColourRangeValueEntryPanel.java
 * Creation date: (13/07/01 10:38:38 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * A quick prototype for handling colour range (0 - 255) input.
 * Creation date: (13/07/01 10:38:38 AM)
 * @author Michael Cheng
 */
class ColourRangeValueEntryPanel extends ValueEntryPanel {

    private static final long serialVersionUID = 3391357553525824929L;
    
    private JSlider slider;
    private JLabel label;

    /** The primary colour being changed by this ColourRangeValueEntryPanel. */
    private final PrimaryColour primaryColour;


    /**
     * Basically an enum for RED, GREEN, BLUE.
     */
    public static class PrimaryColour {

        private final String colourName;

        private PrimaryColour(String colourNameParam) {

            this.colourName = colourNameParam;
        }

        @Override
        public String toString() {
            return colourName;
        }

        @Override
        public final boolean equals(Object that) {
            return super.equals(that);
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        public static final PrimaryColour RED = new PrimaryColour("Red");
        public static final PrimaryColour GREEN = new PrimaryColour("Green");
        public static final PrimaryColour BLUE = new PrimaryColour("Blue");
        public static final PrimaryColour ALPHA = new PrimaryColour("Alpha");
    }

    /**
     * When the state of the slider changes, we have to update the ValueNode,
     * the label's text, the label's tooltip, and the colour intensity.
     * It also notifies the ValueEditorListeners of the value change.
     */
    private class SliderChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent evt) {

            ValueNode oldValue = getValueNode();
            Integer newValue = new Integer(slider.getValue());

            TypeExpr typeExpr = getValueNode().getTypeExpr();
            replaceValueNode(new LiteralValueNode(newValue, typeExpr), true);

            label.setText(String.valueOf(slider.getValue()));
            label.setToolTipText(String.valueOf(slider.getValue()));

            updateDisplayedColour();
            
            notifyValueChanged(oldValue);
        }
    }

    /**
     * ColourRangeValueEntryPanel constructor.
     * @param primaryColour 
     * @param valueEditorHierarchyManager
     * @param valueNode 
     */
    ColourRangeValueEntryPanel(PrimaryColour primaryColour, ValueEditorHierarchyManager valueEditorHierarchyManager, ValueNode valueNode) {

        super(valueEditorHierarchyManager, valueNode);
        this.primaryColour = primaryColour;

        // we didn't set the colour until after the constructor was called (and therefore after setInitialValue())
        // so we must update the displayed colour.
        updateDisplayedColour();
    }

    /**
     * Returns the LiteralValueNode.
     * Creation date: (13/07/01 11:28:51 AM)
     * @return LiteralValueNode
     */
    public LiteralValueNode getLiteralValueNode() {
        return (LiteralValueNode) getValueNode();
    }

    /**
     * Re-initializes the GUI layout (sets up the slider, etc.)
     * Creation date: (13/07/01 11:16:19 AM)
     */
    @Override
    public void setInitialValue() {

        super.setInitialValue();

        // Okay, let's set up the rest of the GUI (slider)
        if (slider == null) {
            initializeUI();
        }
        
        // Update the displayed values and colors
        slider.setValue(getLiteralValueNode().getIntegerValue().intValue());
        updateDisplayedColour();
    }
    
    /**
     * Initialize the UI components for this Value Entry Panel
     */
    private void initializeUI() {
        // We don't need the launch editor button, nor the value text field.
        this.remove(getLaunchEditorButton());
        this.remove(this.getValueField());
        
        // Create the color slider
        slider = new JSlider();
        slider.setMaximum(255);
        slider.setMinimum(0);
        slider.addChangeListener(new SliderChangeListener());
        this.add(slider, "Center");

        // Create the colour value label
        label = new JLabel(String.valueOf(slider.getValue()));
        label.setToolTipText(String.valueOf(slider.getValue()));
        label.setBorder(BorderFactory.createEtchedBorder());
        Rectangle textBounds = label.getFontMetrics(label.getFont()).getStringBounds("555", label.getGraphics()).getBounds();
        label.setSize(textBounds.width + 6, textBounds.height + 6);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(label.getSize());
        label.setForeground(Color.white);
        label.setBackground(Color.white);
        label.setBorder(BorderFactory.createLineBorder(slider.getBackground(), 2));
        this.add(label, "East");
    }

    /**
     * Update the colour displayed by this VEP
     * Creation date: (02/08/02 1:45:51 PM)
     */
    private void updateDisplayedColour() {

        Color b;                            // Background
        Color f = new Color(255, 255, 255); // Foreground
        
        if (primaryColour == PrimaryColour.RED) {
            b = new Color(slider.getValue(), 0, 0);

        } else if (primaryColour == PrimaryColour.GREEN) {
            b = new Color(0, slider.getValue(), 0);
            
        } else if (primaryColour == PrimaryColour.BLUE) {
            b = new Color(0, 0, slider.getValue());
            
        } else {
            b = new Color(slider.getValue(), slider.getValue(), slider.getValue());
            if(slider.getValue() >= 128) {
                f = new Color(0, 0, 0);                
            }             
        }

        label.setBackground(b);
        label.setForeground(f);
    }
}
