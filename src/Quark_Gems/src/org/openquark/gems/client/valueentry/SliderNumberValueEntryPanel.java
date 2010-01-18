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
 * SliderNumberValueEntryPanel.java
 * Creation date:  Sept 30, 2004
 * By: Richard Webster
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.util.OrientationType;
import org.openquark.util.ui.NumericTextField;


/**
 * A value entry panel displaying a slider for setting and displaying the value.
 * @author Richard Webster
 */
public class SliderNumberValueEntryPanel extends ValueEntryPanel {

    private static final long serialVersionUID = 6895591427785034118L;

    /**
     * When the state of the slider changes, we have to update the ValueNode and
     * the label's text.
     * It also notifies the ValueEditorListeners of the value change.
     */
    private class SliderChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            if (!ignoreChangeEvents) {
                double newValue = sliderValueToRealValue(slider.getValue());
                changeValue(newValue);

                // Update the label.
                ignoreChangeEvents = true;
                label.setText(getCurrentValueString());
                ignoreChangeEvents = false;

                // Commit the value when the user lets go of the slider.
                if (!slider.getValueIsAdjusting()) {
                    handleCommitGesture();
                }
            }
        }
    }

    /** The number of slider positions to use (when using fractional values). */
    private static final int N_SLIDER_POSITIONS = 100;

    /** The slider control. */
    private final JSlider slider;

    /** A text entry field for showing the current value. */
    private JTextField label;

    /** Whether the values must be integers or whether the values can be fractional. */
    private final boolean integerValues;

    /** The scaling factor between the real values and the slider integer values. */
    private final double scalingFactor;

    /** The current value displayed in the controls. */
    private double currentValue;

    /** This flag will be true when changing the value of the slider or label 
     *  programatically in order to avoid extra events from being fired. */
    private boolean ignoreChangeEvents = false;

    /**
     * SliderNumberValueEntryPanel constructor.
     * @param valueEditorHierarchyManager  the value editor hierarchy manager
     * @param valueNode                    the initial value for the VEP
     * @param realLowerBound               the lower bound value for the slider
     * @param realUpperBound               the upper bound value for the slider
     * @param orientation                  the orientation for the slider
     * @param integerValues                True if working with integral values 
     */
    public SliderNumberValueEntryPanel(ValueEditorHierarchyManager valueEditorHierarchyManager, 
                                       ValueNode valueNode,
                                       double realLowerBound,
                                       double realUpperBound,
                                       OrientationType orientation,
                                       boolean integerValues) {
        super(valueEditorHierarchyManager, valueNode);
        this.integerValues = integerValues;

        // Make sure that the upper bound is higher than the lower bound.
        realUpperBound = (realUpperBound > realLowerBound) ? realUpperBound : (realLowerBound + 1.0);
        
        // Update the displayed values.
        currentValue = 0;
        Object value = getValueNode().getValue();
        if (value instanceof Number) {
            currentValue = ((Number) value).doubleValue();
        }

        // Make sure that the current value is within the slider bounds.
        realLowerBound = Math.min(realLowerBound, currentValue);
        realUpperBound = Math.max(realUpperBound, currentValue);

        // Determine the scaling factor between the real values and the slider integer values.
        this.scalingFactor = integerValues ? 1.0 : (realUpperBound - realLowerBound) / N_SLIDER_POSITIONS;

        this.slider = new JSlider(orientation.isHorizontal() ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL, 
                                  realValueToSliderValue(realLowerBound), 
                                  realValueToSliderValue(realUpperBound), 
                                  realValueToSliderValue(currentValue));

        initializeUI(realLowerBound, realUpperBound, orientation);
    }

    /**
     * Initialize the UI components for this Value Entry Panel
     */
    private void initializeUI(final double lowerBound, final double upperBound, OrientationType orientation) {
        // We don't need the launch editor button, nor the value text field.
        this.remove(getLaunchEditorButton());
        this.remove(this.getValueField());

        // Configure the slider.
        slider.addChangeListener(new SliderChangeListener());

        // Show tick marks and labels in the slider.
        final int nMajorTicks = 5;
        int majorTickSpacing;
        if (integerValues) {
            majorTickSpacing = ((int) upperBound - (int) lowerBound + 2) / nMajorTicks;
        }
        else {
            majorTickSpacing = N_SLIDER_POSITIONS / nMajorTicks;
        }
        int minorTickSpacing = majorTickSpacing / 2;

        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setMinorTickSpacing(minorTickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(Color.white);
        slider.setInverted(orientation.isReversed());

        // Create the label table.
        // Note that a Hashtable is required by slider.setLabelTable().
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        for (int i = 0; i <= nMajorTicks; ++i) {
            double dblVal = lowerBound + sliderValueToRealValue(i * majorTickSpacing);
            String labelVal = formatDouble(dblVal);
            JLabel label = new JLabel(labelVal);

            // TODO: can the formatting of the labels be set to match the rest of the component?
            labelTable.put(new Integer(realValueToSliderValue(lowerBound) + i * majorTickSpacing), label);
        }
        slider.setLabelTable(labelTable);


        // Cancel the sliding action if the Esc key is pressed.
        slider.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    handleCancelGesture();

                    currentValue = 0;
                    Object value = getValueNode().getValue();
                    if (value instanceof Number) {
                        currentValue = ((Number) value).doubleValue();
                    }

                    ignoreChangeEvents = true;
                    slider.setValue(realValueToSliderValue(currentValue));
                    label.setText(getCurrentValueString());
                    ignoreChangeEvents = false;

                    // TODO: find a proper way to reset the UI...
                    slider.updateUI();
                }
            }
        });

        this.add(slider, BorderLayout.CENTER);

        // Create the value label.
        label = integerValues ? NumericTextField.createIntegerTextField() : NumericTextField.createDecimalTextField();
        label.setText(getCurrentValueString());
        label.setBackground(Color.white);
        label.setBorder(null);
        label.setHorizontalAlignment(orientation.isHorizontal() ? SwingConstants.RIGHT : SwingConstants.CENTER);

        // TODO: do this properly for the fractional values case...
        int nColumns = integerValues ? Math.max(nCharsForValue((int) lowerBound), nCharsForValue((int) upperBound)) : 4;
        label.setColumns(nColumns);

        label.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!ignoreChangeEvents) {
                        double newValue = Double.parseDouble(label.getText());
                        if (newValue >= lowerBound && newValue <= upperBound) {
                            changeValue(newValue);
                            
                            // Update the slider.
                            ignoreChangeEvents = true;
                            slider.setValue(realValueToSliderValue(currentValue));
                            ignoreChangeEvents = false;
    
                            // Commit the change.
                            handleCommitGesture();
                        }
                    }
                }
            });

        this.add(label, orientation.isHorizontal() ? 
                            orientation.isReversed() ? BorderLayout.WEST  : BorderLayout.EAST : 
                            orientation.isReversed() ? BorderLayout.NORTH : BorderLayout.SOUTH);
    }

    /**
     * Returns the slider value corresponding to a real value.
     * @param realValue  a real value
     * @return the slider value corresponding to a real value
     */
    private int realValueToSliderValue(double realValue) {
        return (int) Math.round(realValue / scalingFactor);
    }

    /**
     * Returns the real value corresponding to a slider value.
     * @param sliderValue  a slider value
     * @return the real value corresponding to a slider value
     */
    private double sliderValueToRealValue(int sliderValue) {
        return sliderValue * scalingFactor;
    }

    /**
     * Sets the current value to the specified value.
     * @param newValue  the new current value
     */
    private void changeValue(double newValue) {
        currentValue = newValue;

        ValueNode oldValue = getValueNode();

        Object newValueObj = integerValues
                                ? (Object) new Integer((int) newValue)
                                : (Object) new Double(newValue);

        TypeExpr typeExpr = getValueNode().getTypeExpr();
        replaceValueNode(new LiteralValueNode(newValueObj, typeExpr), true);

        notifyValueChanged(oldValue);
    }

    /**
     * Returns a string representation of the current value.
     */
    private String getCurrentValueString() {
        if (integerValues) {
            return String.valueOf((int) currentValue);
        }
        else {
            return formatDouble(currentValue);
        }
    }

    /**
     * Formats a double value as a string.
     * @param dblVal  a double value
     * @return the formatted double value
     */
    private String formatDouble(double dblVal) {
        // TODO: figure this out automatically...
        final int nDecimalPlaces = 2;

        // TODO: perhaps this should use a NumberFormatter...
        NumberFormat numberFormat = NumberFormat.getInstance ();
        numberFormat.setMinimumFractionDigits (0);
        numberFormat.setMaximumFractionDigits (nDecimalPlaces);

        return numberFormat.format(dblVal);
    }

    /**
     * Returns the number of chars needed to represent the specified integer value.
     * @param value  the integer value to be represented.
     * @return the number of chars needed to represent the integer value
     */
    private int nCharsForValue(int value) {
        int nDigits = (value == 0) ? 1 : (int) (Math.log(Math.abs(value)) / Math.log(10.0)) + 1;
        return (value >= 0) ? nDigits : nDigits + 1;
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setEditable(boolean)
     */
    @Override
    public void setEditable(boolean isEditable) {
        super.setEditable(isEditable);
        
        slider.setEnabled(isEditable);
        label.setEditable(isEditable);
    }
}
