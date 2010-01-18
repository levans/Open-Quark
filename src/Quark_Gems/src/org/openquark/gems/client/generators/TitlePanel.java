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
 * TitlePanel.java
 * Creation date: Oct 27, 2005.
 * By: Edward Lam
 */
package org.openquark.gems.client.generators;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * The help panel appearing at the top of the generator dialog.
 * This consists of a bold title, and a subtitle.
 * The subtitle is a text area, and so can display newlines.
 * 
 * @author Edward Lam
 */
class TitlePanel extends JPanel {

    // Note: tried using a JTextPane for the subtitle area, but this didn't interact well with the 
    // DialogBase.SizeConstrainer when used in a GridBagLayout.
    
    private static final long serialVersionUID = 3250981712987937770L;

    /** The title label in the help panel at the top. */
    private final JLabel titleLabel;
    
    /** The subtitle area in the help panel at the top. 
     *  Use a JTextArea in order to facilitate display of multiple lines. */
    private final JTextArea subtitleArea;

    /**
     * Constructor for a TitlePanel.
     */
    public TitlePanel() {
        this(false);
    }
    /**
     * Constructor for a TitlePanel.
     * @param emphasis if true, the title font will be a little bit larger, and extra padding will be 
     *   added between the title label and the subtitle area.
     */
    public TitlePanel(boolean emphasis) {
        
        setBackground(Color.WHITE);
        
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        setLayout(new GridBagLayout());
        
        // Keep track of the number of rows.
        int numRows = 0;
        
        // Add the title label.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.NONE;
            
            constraints.gridx = 0;
            constraints.gridy = numRows;
            
            constraints.insets = new Insets(0, 0, emphasis ? 10 : 0, 0);
            
            this.titleLabel = new JLabel();
            Font font = getFont();
            if (font == null) {
                font = UIManager.getFont("Panel.font");
            }
            if (font != null) {
                titleLabel.setFont(font.deriveFont(Font.BOLD, font.getSize() + (emphasis ? 3 : 2)));
            }
            
            this.add(titleLabel, constraints);
            numRows++;
        }
        
        // Add the subtitle component.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.NONE;

            constraints.gridx = 0;
            constraints.gridy = numRows;

            constraints.insets = new Insets(0, 0, 0, 0);
            
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            
            this.subtitleArea = new JTextArea();
            subtitleArea.setEditable(false);
            
            this.add(subtitleArea, constraints);
            numRows++;
        }
    }

    /**
     * Set the text to display in the title label.
     * @param titleText
     */
    public void setTitleText(String titleText) {
        titleLabel.setText(titleText);
    }
    
    /**
     * Set the text to display in the subtitle component.
     * @param subtitleText
     */
    public void setSubtitleText(String subtitleText) {
        subtitleArea.setText(subtitleText);
    }
}