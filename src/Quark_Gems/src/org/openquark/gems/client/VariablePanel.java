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
 * VariablePanel.java
 * Creation date: (1/24/01 7:10:05 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import org.openquark.gems.client.caleditor.AdvancedCALEditor;

/**
 * A panel representing a potential or actual argument in the VariablesDisplay area of the code editor dialog.
 * @author Luke Evans
 */
 
public class VariablePanel extends JPanel {

    private static final long serialVersionUID = 538060152983985793L;
    public static final Icon FUNCTION_ICON;
    public static final Icon ARGUMENT_ICON;

    public static final Icon BURN_ICON;
    public static final Icon TYPE_CLASH_ICON;
    public static final Icon UNDEFINED_TYPE_ICON;
    public static final Icon DULL_CONNECTION_ICON;
    public static final Icon SHARP_CONNECTION_ICON;

    public static final Color defaultBackgroundColor = new Color(239,239,239);
    public static final Color defaultTextColor = Color.BLACK;
    public static final Color warningBackgroundColor = new Color(255,100,100);

    static {
        FUNCTION_ICON = new ImageIcon(VariablePanel.class.getResource("/Resources/call.gif"));
        ARGUMENT_ICON = new ImageIcon(VariablePanel.class.getResource("/Resources/argument.gif"));
        
        int panelIconWidth = 16;
        int panelIconHeight = 16;
        ImageIcon unscaledBurnIcon = new ImageIcon(VariablePanel.class.getResource("/Resources/burn.gif"));

        BURN_ICON             = new ImageIcon(unscaledBurnIcon.getImage().getScaledInstance(panelIconWidth, panelIconHeight, Image.SCALE_SMOOTH));
        TYPE_CLASH_ICON       = new ImageIcon(VariablePanel.class.getResource("/Resources/typeClash.gif"));
        UNDEFINED_TYPE_ICON   = new ImageIcon(VariablePanel.class.getResource("/Resources/undefinedType.gif"));
        DULL_CONNECTION_ICON  = new ImageIcon(VariablePanel.class.getResource("/Resources/connection_dull.gif"));
        SHARP_CONNECTION_ICON = new ImageIcon(VariablePanel.class.getResource("/Resources/connection_sharp.gif"));
    }


    /** Whether the panel is selected. */    
    private boolean isSelected = false;
    
    /** The status of the variable represented by this panel. */
    private final Argument.Status status;
    
    /** The identifier that this panel represents */
    private final AdvancedCALEditor.PositionlessIdentifier identifier;
    
    private JLabel varSourceIconLabel = null;
    private JLabel varNameLabel = null;
    private JLabel varTypeLabel = null;


    /**
     * Constructor for a VariablePanel
     * @param identifier the identifier this panel represents
     * @param typeText the text displayed for the type
     * @param toolTipText the text displayed for the tooltip
     * @param status the status of the variable represented by this panel. 
     */
    VariablePanel(
            AdvancedCALEditor.PositionlessIdentifier identifier, 
            String typeText, 
            String toolTipText, 
            Argument.Status status) {

        setName("VariablePanel");
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(1,2,0,2));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setMaximumSize(new Dimension(99999, 16));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setSize(75, 22);
        setMinimumSize(new Dimension(0, 16));
        add(getVarSourceIconLabel(), getVarSourceIconLabel().getName());
        add(getVarNameLabel(), getVarNameLabel().getName());
        add(getVarTypeLabel(), getVarTypeLabel().getName());
        
        // Set some members
        this.identifier = identifier;
        this.status = status;

        // Set the argument icon
        getVarSourceIconLabel().setIcon(ARGUMENT_ICON); 

        // Set the type icon if any.                
        if (status == Argument.Status.TYPE_UNDEFINED) {
            varTypeLabel.setIcon(UNDEFINED_TYPE_ICON);

        } else if (status == Argument.Status.BURNT) {
            varTypeLabel.setIcon(BURN_ICON);

        } else if (status == Argument.Status.CONNECTED) {
            varTypeLabel.setIcon(SHARP_CONNECTION_ICON);

        } else if (status == Argument.Status.CONNECTED_UNUSED) {
            varTypeLabel.setIcon(DULL_CONNECTION_ICON);

        } else if (status == Argument.Status.TYPE_CLASH) {
            varTypeLabel.setIcon(TYPE_CLASH_ICON);
        }
        
        if (identifier.getQualificationType().isCodeQualified()) {
            this.varNameLabel.setEnabled(false);
            this.varTypeLabel.setEnabled(false);
        }
        
        getVarNameLabel().setText(identifier.getName());    // Set the name in the JLabel
        getVarTypeLabel().setText(typeText);
        setToolTipText(toolTipText);
    }
    
    /**
     * Return the VarName property value.
     * @return JLabel
     */
    private JLabel getVarNameLabel() {
        if (varNameLabel == null) {
            try {
                varNameLabel = new JLabel();
                varNameLabel.setName("VarName");
                varNameLabel.setOpaque(true);
                varNameLabel.setText("a");
                varNameLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
                setBackground(defaultBackgroundColor);
                updateVarNameBorder();
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return varNameLabel;
    }

    /**
     * Update the border for the variable name based on the variable status and selection state.
     */
    private void updateVarNameBorder() {

        Color highlight, shadow;
        if (status == Argument.Status.TYPE_CLASH) {
            // return a red border
            highlight = isSelected ? Color.black : defaultBackgroundColor;
            shadow = warningBackgroundColor;

        } else if (isSelected) {
            highlight = Color.black;
            shadow = defaultBackgroundColor;

        } else {
            // default case: ok type, not selected
            highlight = defaultBackgroundColor;
            shadow = defaultBackgroundColor;
        }

        getVarNameLabel().setBorder(
                new CompoundBorder(BorderFactory.createEtchedBorder(highlight, shadow),
                                   BorderFactory.createEmptyBorder(0,2,0,3)));    
    }

    /**
     * Return the VarSourceIcon property value.
     * @return JLabel
     */
    private JLabel getVarSourceIconLabel() {
        if (varSourceIconLabel == null) {
            try {
                varSourceIconLabel = new JLabel();
                varSourceIconLabel.setName("VarSourceIcon");
                varSourceIconLabel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
                varSourceIconLabel.setBackground(new Color(255,255,255));
                varSourceIconLabel.setIcon(new ImageIcon(getClass().getResource("/Resources/argument.gif")));
                varSourceIconLabel.setVerticalAlignment(SwingConstants.CENTER);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return varSourceIconLabel;
    }
    
    /**
     * Check whether a given point falls on the source icon.
     * @return whether a given point (in the variable panel's coordinate system) falls on the source icon
     */
    boolean checkIconHit(Point p) {
        return getVarSourceIconLabel().contains(p);
    }

    /**
     * Return the VarType property value.
     * @return JLabel
     */
    private JLabel getVarTypeLabel() {
        if (varTypeLabel == null) {
            try {
                varTypeLabel = new JLabel();
                varTypeLabel.setName("VarType");
                varTypeLabel.setOpaque(true);
                varTypeLabel.setText("Double");
                varTypeLabel.setForeground(defaultTextColor);
                varTypeLabel.setPreferredSize(new Dimension(0, 14));
                varTypeLabel.setFont(new Font("sansserif", Font.BOLD | Font.ITALIC, 12));
                varTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                varTypeLabel.setIconTextGap(1);
                updateVarTypeBorder();
                updateVarTypeBackground();
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return varTypeLabel;
    }

    /**
     * Update the background color for the variable type.
     */
    private void updateVarTypeBackground() {

        if (status == Argument.Status.NATURAL) {
            // some color similar to tooltip color
            getVarTypeLabel().setBackground(new Color(255,255,200));

        } else {
            getVarTypeLabel().setBackground(defaultBackgroundColor);
        }
    }

    /**
     * Update the border for the variable type.
     */
    private void updateVarTypeBorder() {

        Color highlight, shadow;

        if (status == Argument.Status.TYPE_CLASH) {
            highlight = isSelected ? Color.black : defaultBackgroundColor;
            shadow = warningBackgroundColor;

        } else if (isSelected) {
            highlight = Color.black;
            shadow = defaultBackgroundColor;

        } else if (status == Argument.Status.NATURAL) {
            EtchedBorder etchedBorder = (EtchedBorder)BorderFactory.createEtchedBorder();
            highlight = etchedBorder.getHighlightColor(this);
            shadow = etchedBorder.getShadowColor(this);

        } else {
            highlight = defaultBackgroundColor;
            shadow = defaultBackgroundColor;
        }

        getVarTypeLabel().setBorder(new CompoundBorder(BorderFactory.createEtchedBorder(highlight, shadow),
                                                       BorderFactory.createEmptyBorder(0,2,0,3)));
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
     * Set the appearance of this variable panel as grayed out (or not).
     * @param grayed whether this variable panel should be grayed
     */
    void setGrayed(boolean grayed) {
        JLabel typeLabel = getVarTypeLabel();
        JLabel nameLabel = getVarNameLabel();
            
        if (grayed) {
            typeLabel.setForeground(Color.lightGray);
            nameLabel.setForeground(Color.lightGray);
        } else {
            typeLabel.setForeground(defaultTextColor);
            nameLabel.setForeground(defaultTextColor);
        }
    }

    /**
     * Set the appearance of this variable panel according to whether or not it is selected
     * @param selected boolean whether this variable panel is selected
     */
    void setSelected(boolean selected) {
        JLabel nameLabel = getVarNameLabel();
        JLabel typeLabel = getVarTypeLabel();
        JList someList = new JList();
        Color selectedColor = someList.getSelectionForeground();
    
        isSelected = selected;
            
        if (selected) {
            setBackground(someList.getSelectionBackground());
            typeLabel.setForeground(selectedColor);
            nameLabel.setForeground(selectedColor);
        } else {
            setBackground(defaultBackgroundColor);
            typeLabel.setForeground(defaultTextColor);
            nameLabel.setForeground(defaultTextColor);
        }
        nameLabel.setBackground(defaultBackgroundColor);

        updateVarNameBorder();
        updateVarTypeBackground();
        updateVarTypeBorder();
    }

    /** @return the identifier this panel represents */ 
    public AdvancedCALEditor.PositionlessIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * @param otherVariablePanel another variable panel.
     * @return whether the other variable panel holds the same info as this one.
     */
    public boolean sameInfo(VariablePanel otherVariablePanel) {
        return this.identifier.equals(otherVariablePanel.identifier) &&
               this.getVarTypeLabel().getText().equals(otherVariablePanel.getVarTypeLabel().getText()) &&
               this.getToolTipText().equals(otherVariablePanel.getToolTipText()) &&
               this.status == otherVariablePanel.status;
    }
}