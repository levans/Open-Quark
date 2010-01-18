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
 * QualificationPanel.java
 * Creation date: (Feb 20, 2004 2:10:05 PM)
 * By: Iulian Radu
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.gems.client.caleditor.AdvancedCALEditor;


/**
 * Panel representing the qualification of an unqualified identifier found in code.
 * 
 * @author Iulian Radu
 */
 
public final class QualificationPanel extends JPanel {

    private static final long serialVersionUID = -5901174270631451303L;
    
    public static final Icon FUNCTION_ICON;
    public static final Icon CLASS_ICON;
    public static final Icon CONSTRUCTOR_ICON;
    public static final Icon TYPE_ICON;

    public static final Color defaultBackgroundColor = new Color(239,239,239);
    public static final Color ambiguousBackgroundColor = new Color(205,205,255);
    public static final Color defaultTextColor = Color.BLACK;
    public static final Color warningBackgroundColor = new Color(255,100,100);

    static {
        FUNCTION_ICON = new ImageIcon(VariablePanel.class.getResource("/Resources/nav_function.gif"));
        CLASS_ICON = new ImageIcon(VariablePanel.class.getResource("/Resources/nav_typeclass.gif"));
        TYPE_ICON = new ImageIcon(VariablePanel.class.getResource("/Resources/nav_typeconstructor.gif"));
        CONSTRUCTOR_ICON =  new ImageIcon(VariablePanel.class.getResource("/Resources/Gem_Yellow.gif"));
    }
    
    /** The identifier that this panel represents */
    private final AdvancedCALEditor.PositionlessIdentifier identifier;
    
    /** Whether the panel is selected. */    
    private boolean isSelected = false;
    
    /** 
     * Whether the qualification is ambiguous 
     * (ie: name can be qualified to more than 1 module)
     */
    private boolean ambiguous;

    private JLabel sourceIconLabel = null;
    private JLabel nameLabel = null;
    private JLabel moduleLabel = null;
 

    /**
     * Constructor for a QualificationPanel
     * 
     * @param identifier the identifier represented by this panel
     * @param toolTipText the text displayed for the tooltip
     * @param ambiguous whether the qualification is ambiguous
     */
    QualificationPanel(AdvancedCALEditor.PositionlessIdentifier identifier, String toolTipText, boolean ambiguous) {

        // Set some members
        this.identifier = identifier;
        this.ambiguous = ambiguous;
        
        // Build control
        setName("QualificationPanel");
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(1,2,0,2));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setMaximumSize(new Dimension(99999, 16));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setSize(75, 22);
        setMinimumSize(new Dimension(0, 16));
        add(getVarSourceIconLabel(), getVarSourceIconLabel().getName());
        add(getNameLabel(), getNameLabel().getName());
        add(getModuleLabel(), getModuleLabel().getName());
        
        if (identifier.getQualificationType().isCodeQualified()) {
            this.nameLabel.setEnabled(false);
            this.moduleLabel.setEnabled(false);
        }
        
        // Set the scope icon
        SourceIdentifier.Category form = identifier.getCategory();
        if (form == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            getVarSourceIconLabel().setIcon(FUNCTION_ICON);
            
        } else if (form == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            getVarSourceIconLabel().setIcon(CONSTRUCTOR_ICON);
            
        } else  if (form == SourceIdentifier.Category.TYPE_CLASS) {
            getVarSourceIconLabel().setIcon(CLASS_ICON);
            
        } else if (form == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            getVarSourceIconLabel().setIcon(TYPE_ICON); 
        }
        

        getNameLabel().setText(identifier.getName());
        ModuleName minimallyQualifiedModuleNameOrNull = identifier.getMinimallyQualifiedModuleName();
        if (minimallyQualifiedModuleNameOrNull == null) {
            getModuleLabel().setText("");
        } else {
            getModuleLabel().setText(minimallyQualifiedModuleNameOrNull.toSourceText());
        }
        setToolTipText(toolTipText);
    }
    
    /** 
     * @return the identifier represented by this panel 
     */ 
    public AdvancedCALEditor.PositionlessIdentifier getIdentifier() {
        return identifier;
    }
    
    /**
     * Return the NameLabel property value.
     * @return JLabel
     */
    private JLabel getNameLabel() {
        if (nameLabel == null) {
            try {
                nameLabel = new JLabel();
                nameLabel.setName("Name");
                nameLabel.setOpaque(true);
                nameLabel.setText("a");
                nameLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
                setBackground(defaultBackgroundColor);
                updateNameBorder();
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return nameLabel;
    }

    /**
     * Update the border for the name based on the variable status and selection state.
     */
    private void updateNameBorder() {

        Color highlight, shadow;
        if (isSelected) {
            highlight = Color.black;
            shadow = defaultBackgroundColor;

        } else {
            // default case: ok type, not selected
            highlight = defaultBackgroundColor;
            shadow = defaultBackgroundColor;
        }

        getNameLabel().setBorder(
                new CompoundBorder(BorderFactory.createEtchedBorder(highlight, shadow),
                                   BorderFactory.createEmptyBorder(0,2,0,3)));    
    }

    /**
     * Return the SourceIconLabel property value.
     * @return JLabel
     */
    private JLabel getVarSourceIconLabel() {
        if (sourceIconLabel == null) {
            try {
                sourceIconLabel = new JLabel();
                sourceIconLabel.setName("VarSourceIcon");
                sourceIconLabel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
                sourceIconLabel.setBackground(new Color(255,255,255));
                sourceIconLabel.setIcon(new ImageIcon(getClass().getResource("/Resources/argument.gif")));
                sourceIconLabel.setVerticalAlignment(SwingConstants.CENTER);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return sourceIconLabel;
    }
    
    /**
     * Check whether a given point falls on the source icon.
     * 
     * @param p a given point (in the panel coordinate system) 
     * @return whether the point falls on the source icon
     */
    boolean checkIconHit(Point p) {
        Point labelPoint = new Point(p.x - getVarSourceIconLabel().getX(), p.y - getVarSourceIconLabel().getY());
        return getVarSourceIconLabel().contains(labelPoint);
    }
    
    /**
     * Checks whether a given point falls on the module label
     * @param p point (in the panel coordinate system)
     * @return whether the point falls on the module label
     */
    boolean checkModuleLabelHit(Point p) {
        Point labelPoint = new Point(p.x - getModuleLabel().getX(), p.y - getModuleLabel().getY());  
        return getModuleLabel().contains(labelPoint);
    }

    /**
     * Return the ModuleLabel property value.
     * @return JLabel
     */
    private JLabel getModuleLabel() {
        if (moduleLabel == null) {
            try {
                moduleLabel = new JLabel();
                moduleLabel.setName("VarType");
                moduleLabel.setOpaque(true);
                moduleLabel.setText("Double");
                moduleLabel.setForeground(defaultTextColor);
                moduleLabel.setPreferredSize(new Dimension(0, 14));
                moduleLabel.setFont(new Font("sansserif", Font.BOLD | Font.ITALIC, 12));
                moduleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                moduleLabel.setIconTextGap(1);
                if (!ambiguous) {
                    moduleLabel.setBackground(defaultBackgroundColor);
                } else {
                    moduleLabel.setBackground(ambiguousBackgroundColor);
                }
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return moduleLabel;
    }


    /**
     * Update the border for the type.
     */
    private void updateTypeBorder() {

        Color highlight, shadow;

        if (isSelected) {
            highlight = Color.black;
            shadow = defaultBackgroundColor;

        } else {
            EtchedBorder etchedBorder = (EtchedBorder)BorderFactory.createEtchedBorder();
            highlight = etchedBorder.getHighlightColor(this);
            shadow = etchedBorder.getShadowColor(this);

        } 

        getModuleLabel().setBorder(new CompoundBorder(BorderFactory.createEtchedBorder(highlight, shadow),
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
     * Set the appearance of this panel as grayed out (or not).
     * @param grayed whether this panel should be grayed
     */
    void setGrayed(boolean grayed) {
        JLabel moduleLabel = getModuleLabel();
        JLabel nameLabel = getNameLabel();
            
        if (grayed) {
            moduleLabel.setForeground(Color.lightGray);
            nameLabel.setForeground(Color.lightGray);
        } else {
            moduleLabel.setForeground(defaultTextColor);
            nameLabel.setForeground(defaultTextColor);
        }
    }

    /**
     * Set the appearance of this panel according to whether or not it is selected
     * 
     * @param selected boolean whether this variable panel is selected
     */
    void setSelected(boolean selected) {
        JLabel nameLabel = getNameLabel();
        JLabel moduleLabel = getModuleLabel();
        JList someList = new JList();
        Color selectedColor = someList.getSelectionForeground();
    
        isSelected = selected;
            
        if (selected) {
            setBackground(someList.getSelectionBackground());
            moduleLabel.setForeground(selectedColor);
            nameLabel.setForeground(selectedColor);
        } else {
            setBackground(defaultBackgroundColor);
            moduleLabel.setForeground(defaultTextColor);
            nameLabel.setForeground(defaultTextColor);
        }
        nameLabel.setBackground(defaultBackgroundColor);

        updateNameBorder();
        updateTypeBorder();
    }
}
