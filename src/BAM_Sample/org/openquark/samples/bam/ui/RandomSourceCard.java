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
 * RandomSourceCard.java
 * Created: 7-May-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openquark.samples.bam.model.MonitorDocument;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.RandomMessageSourceDescription;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.ui.WizardCard;



/**
 * 
 * 
 */
class RandomSourceCard extends WizardCard {

    private static final long serialVersionUID = 2278668492305551418L;

    static final String CARD_NAME = "RandomSource";
    
    private final MonitorDocument document;
    
    private String name = "";

    private JTextField nameField;

    /**
     * Constructor RandomSourceCard
     * 
     * @param document
     */
    RandomSourceCard (final MonitorDocument document) {
        this.document = document;
    }
    
    /**
     * @see org.openquark.util.ui.WizardCard#getTitle()
     */
    @Override
    protected String getTitle () {
        return "Random Data";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getSubtitle()
     */
    @Override
    protected String getSubtitle () {
        return "Choose a name for the random message source.";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getCardName()
     */
    @Override
    public String getCardName () {
        return CARD_NAME;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getNextCardName()
     */
    @Override
    protected String getNextCardName () {
        // This is the last card
        return null;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getMainPanel()
     */
    @Override
    protected JComponent getMainPanel () {
        JPanel mainPanel = new JPanel (new GridBagLayout ());

        GridBagConstraints constraints = new GridBagConstraints ();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill   = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;

        mainPanel.add (new JLabel ("Name:"), constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        mainPanel.add (getNameField (), constraints);
        
        constraints.weightx = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 1.0;
        
        mainPanel.add (new JPanel (), constraints);

        return mainPanel;
    }

    /**
     * Method getNameField
     * 
     * @return a JTextField for the name of the message source
     */
    private JTextField getNameField () {
        if (nameField == null) {
            nameField = new JTextField ();
            
            Dimension preferredSize = nameField.getPreferredSize();
            
            preferredSize.width = 100;
            
            nameField.setPreferredSize(preferredSize);
            
            addDocumentListener(nameField);
        }

        return nameField;
    }

    /**
     * Method addDocumentListener
     * 
     * @param textField
     */
    private void addDocumentListener (JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentListener () {

            public void changedUpdate (DocumentEvent e) {
                cardStateChanged();
            }

            public void insertUpdate (DocumentEvent e) {
                cardStateChanged();
            }

            public void removeUpdate (DocumentEvent e) {
                cardStateChanged();
            }
        });
    }
    
    /**
     * @see org.openquark.util.ui.WizardCard#initControls()
     */
    @Override
    protected boolean initControls () {
        // nothing to do
        return true;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#commitChanges()
     */
    @Override
    protected boolean commitChanges () {
        // does not apply to this card
        return true;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#canFinish()
     */
    @Override
    protected boolean canFinish () {
        if (!transferValuesFromControls ()) {
            return false;
        }

        return name.length() != 0;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#onFinish()
     */
    @Override
    protected boolean onFinish () {
        if (!transferValuesFromControls ()) {
            return false;
        }
        
        if (name.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please enter a name for the message source.", "BAM Sample", JOptionPane.WARNING_MESSAGE);
            
            return false;
        }
        
        RandomMessageSourceDescription messageSourceDescription = new RandomMessageSourceDescription (name);
        
        Collection<MessagePropertyDescription> messagePropertyInfos = messageSourceDescription.getMessagePropertyDescriptions();
        
        if (messagePropertyInfos != null) {
            MonitorJobDescription jobDescription = new MonitorJobDescription (messageSourceDescription);
            
            document.addJobDescription(jobDescription);
        } else {
            JOptionPane.showMessageDialog(this, "Cannot retrieve message properties for random message source.", "BAM Sample", JOptionPane.ERROR_MESSAGE);
            
            return false;
        }
        
        return true;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getTipInfo()
     */
    @Override
    protected TipInfo getTipInfo () {
        transferValuesFromControls();
        
        if (name == null || name.length() == 0) {
            return new TipInfo (WARNING_TIP, "Please enter a name for the message source.");
        }
        
        return new TipInfo (ALLOK_TIP, "Press the Finish button to complete the message source.");
    }

    /**
     * Method transferValuesFromControls
     * 
     * @return true iff all values are successfully transferred from the controls
     */
    private boolean transferValuesFromControls () {
        name = nameField.getText();
        
        return name != null;
    }

}
