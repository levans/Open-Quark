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
 * SourceTypeCard.java
 * Created: 23-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openquark.util.ui.DialogBase;
import org.openquark.util.ui.WizardCard;



/**
 * 
 * 
 */
class SourceTypeCard extends WizardCard {

    private static final long serialVersionUID = 1496322755800086273L;

    /**
     * Constructor SourceTypeCard
     * 
     */
    SourceTypeCard () {
    }

    private static final String TEXTFILE_TYPE = "TextFile";
    private static final String JMSQUEUE_TYPE = "JMSQueue";
    private static final String RANDOM_TYPE   = "Random";
    
    private ButtonGroup buttonGroup;
    
    private String sourceType;

    /**
     * @see org.openquark.util.ui.WizardCard#getTitle()
     */
    @Override
    protected String getTitle () {
        return "Message Source";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getSubtitle()
     */
    @Override
    protected String getSubtitle () {
        return "Select one of the kinds of message source shown below";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getCardName()
     */
    @Override
    public String getCardName () {
        return "MessageSourceType";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getMainPanel()
     */
    @Override
    protected JComponent getMainPanel () {
        JPanel mainPanel = new JPanel (new GridBagLayout ());

        GridBagConstraints constraints = new GridBagConstraints ();
        
        constraints.anchor = GridBagConstraints.NORTHWEST;
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add (makeSourceTypeRadioButtons (), constraints);
        
        return mainPanel;
    }
    
    @Override
    protected TipInfo getTipInfo () {
        transferValuesFromControls();
        
        if (sourceType.equals(TEXTFILE_TYPE)) {
            return new TipInfo (INFO_TIP, "Press Next to select the text file to use as a message source.");
        } else if (sourceType.equals(JMSQUEUE_TYPE)) {
            return new TipInfo (ERROR_TIP, "Using a JMS queue as a message source is not yet implemented.");
        } else if (sourceType.equals(RANDOM_TYPE)) {
            return new TipInfo (INFO_TIP, "Press Next to configure the random message source.");
        }
        
        return null;
    }

    /**
     * Method makeSourceTypeRadioButtons
     * 
     * @return Returns a Component that contains the radio buttons in the given group
     */
    private Component makeSourceTypeRadioButtons () {
        return DialogBase.makeRadioButtonBox(getButtonGroup ());
    }

    /**
     * Method makeButtonGroup
     * 
     * @return Returns a ButtonGroup that holds the radio buttons for source type
     */
    private ButtonGroup getButtonGroup () {
        if (buttonGroup == null) {
            JRadioButton textFileButton = DialogBase.makeRadioButton("Text file", TEXTFILE_TYPE, true);
            
            textFileButton.addActionListener(new ActionListener () {

                public void actionPerformed (ActionEvent e) {
                    cardStateChanged ();
                }
            });
            
            JRadioButton jmsQueueButton = DialogBase.makeRadioButton("JMS queue", JMSQUEUE_TYPE, false);
            
            jmsQueueButton.addActionListener(new ActionListener () {

                public void actionPerformed (ActionEvent e) {
                    cardStateChanged ();
                }
            });
            
            JRadioButton randomButton = DialogBase.makeRadioButton("Random", RANDOM_TYPE, false);
            
            randomButton.addActionListener(new ActionListener () {

                public void actionPerformed (ActionEvent e) {
                    cardStateChanged ();
                }
            });

            
            buttonGroup = new ButtonGroup ();
            buttonGroup.add(textFileButton);
            buttonGroup.add(jmsQueueButton);
            buttonGroup.add(randomButton);
        }
        
        return buttonGroup;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#initControls()
     */
    @Override
    protected boolean initControls () {
        // nothing to do...
        return true;
    }
    
    
    /**
     * @see org.openquark.util.ui.WizardCard#canGoToNextCard()
     */
    @Override
    public boolean canGoToNextCard () {
        if (transferValuesFromControls()) {
            return sourceType == TEXTFILE_TYPE || sourceType == RANDOM_TYPE;
        }
        
        return false;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#commitChanges()
     */
    @Override
    protected boolean commitChanges () {
        return transferValuesFromControls();
    }

    /**
     * @see org.openquark.util.ui.WizardCard#canFinish()
     */
    @Override
    protected boolean canFinish () {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#onFinish()
     */
    @Override
    protected boolean onFinish () {
        // finish does not apply to this card
        return false;
    }

    private boolean transferValuesFromControls () {
        ButtonGroup buttonGroup = getButtonGroup();
        
        ButtonModel buttonModel = buttonGroup.getSelection();
        
        if (buttonModel != null) {
            sourceType = buttonModel.getActionCommand();
        
            return true;
        } else {
            sourceType = null;
            
            return false;
        }
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getNextCardName()
     */
    @Override
    protected String getNextCardName () {
        if (transferValuesFromControls()) {
            if (sourceType == TEXTFILE_TYPE) {
                return TextFileSourceCard.CARD_NAME;
            } else if (sourceType == JMSQUEUE_TYPE) {
                return JMSSourceCard.CARD_NAME;
            } else if (sourceType == RANDOM_TYPE) {
                return RandomSourceCard.CARD_NAME;
            }
        }
        
        return null;
    }

}
