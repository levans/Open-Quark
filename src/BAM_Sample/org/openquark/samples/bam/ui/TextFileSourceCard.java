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
 * TextFileSourceCard.java
 * Created: 23-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openquark.samples.bam.model.MonitorDocument;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.TextFileMessageSourceDescription;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.ui.ExtensionFileFilter;
import org.openquark.util.ui.WizardCard;


/**
 * 
 *  
 */
class TextFileSourceCard extends WizardCard {
    
    private static final long serialVersionUID = 1364718494422296556L;

    static final String CARD_NAME = "TextFileSource";
    
    private final MonitorDocument document;
    
    private String name = "";

    private String filename = "";
    
    private JTextField nameField;

    private JTextField filenameField;
    
    TextFileSourceCard (MonitorDocument document) {
        this.document = document;
    }
    
    /**
     * @see org.openquark.util.ui.WizardCard#getTitle()
     */
    @Override
    protected String getTitle () {
        return "Text File";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getSubtitle()
     */
    @Override
    protected String getSubtitle () {
        return "Select a text file to use as a message source";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getCardName()
     */
    @Override
    public String getCardName () {
        return CARD_NAME;
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
        
        constraints.gridx = 0;
        constraints.gridy = 2;

        mainPanel.add (new JLabel ("File name:"), constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;

        mainPanel.add (getFilenameField (), constraints);
        
        constraints.weightx = 0.0;

        constraints.gridx = 1;
        constraints.gridy = 3;

        mainPanel.add (getBrowseButton (), constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
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
     * Method getFilenameField
     * 
     * @return a JTextField for the filename of the message source
     */
    private JTextField getFilenameField () {
        if (filenameField == null) {
            filenameField = new JTextField ();
            
            Dimension preferredSize = filenameField.getPreferredSize();
            
            preferredSize.width = 100;
            
            filenameField.setPreferredSize(preferredSize);
            
            addDocumentListener(filenameField);
        }

        return filenameField;
    }
    
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
     * Method getBrowseButton
     * 
     * @return a JButton used to invoke a file dialog, to browse for a text file
     */
    private JButton getBrowseButton () {
        JButton browseButton = new JButton ("Browse...");

        browseButton.addActionListener (new ActionListener () {

            public void actionPerformed (ActionEvent e) {
                onBrowse ();
            }
        });

        return browseButton;
    }

    /**
     * Method onBrowse
     * 
     * Handle a press on the Browse button by opening a file dialog 
     */
    protected void onBrowse () {
        JFileChooser fileChooser = new JFileChooser (getFilenameField().getText());
        
        fileChooser.setFileFilter(new ExtensionFileFilter ("txt", "Text Files"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            
            getFilenameField().setText(filename);
        }
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

        return name.length() != 0 && filename.length() != 0;
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
        
        if (filename.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please choose a file for the message source.", "BAM Sample", JOptionPane.WARNING_MESSAGE);
            
            return false;
        }
        
        TextFileMessageSourceDescription messageSourceDescription = new TextFileMessageSourceDescription (name, filename);
        
        Collection<MessagePropertyDescription> messagePropertyInfos = messageSourceDescription.getMessagePropertyDescriptions(); 
        
        if (messagePropertyInfos != null) {
            MonitorJobDescription jobDescription = new MonitorJobDescription (messageSourceDescription);
            
            document.addJobDescription(jobDescription);
        } else {
            JOptionPane.showMessageDialog(this, "The file <" + filename + "> is not a valid message file.", "BAM Sample", JOptionPane.ERROR_MESSAGE);
            
            return false;
        }
        
        return true;
    }

    /**
     * Method transferValuesFromControls
     * 
     * @return true iff all values are successfully transferred from the controls
     */
    private boolean transferValuesFromControls () {
        name = nameField.getText();
        filename = filenameField.getText();
        
        return name != null && filename != null;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getNextCardName()
     */
    @Override
    protected String getNextCardName () {
        return null;
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
        
        if (filename == null || filename.length() == 0) {
            return new TipInfo (WARNING_TIP, "Please choose a text file that contains messages.");
        }
        
        return new TipInfo (ALLOK_TIP, "Press the Finish button to complete the message source.");
    }

}
