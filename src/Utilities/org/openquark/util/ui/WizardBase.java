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
 * WizardBase.java
 * Created: 24-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;


/**
 * 
 * 
 */
public abstract class WizardBase extends DialogBase {

    private JButton prevButton;

    private JButton nextButton;

    private JButton finishButton;
    
    private JButton cancelButton;
    
    private WizardCardStack cardStack;

    /**
     * Constructor WizardBase
     * 
     * @param owner
     * @param caption
     */
    public WizardBase (Frame owner, String caption) {
        super (owner, caption);
    }
    
    
    /**
     * @see org.openquark.util.ui.DialogBase#doModal()
     */
    @Override
    public boolean doModal () {
        createContents();
        
        return super.doModal ();
    }

    /**
     * Method createContents
     * 
     * Creates the child components of the wizard. 
     */
    private void createContents () {
        WizardButtonBox buttonBox = new WizardButtonBox ();
        
        setUpActions(buttonBox);
        
        cardStack = makeCardStack();
        
        cardStack.addPropertyChangeListener(WizardCard.CARD_STATE_PROPERTY_NAME, new PropertyChangeListener () {

            public void propertyChange (PropertyChangeEvent evt) {
                updateButtons ();
            }
        });

        getContentPane ().add (cardStack, BorderLayout.CENTER);
        getContentPane ().add (buttonBox, BorderLayout.SOUTH);

        pack ();
        
        // Make the initial size be 20% larger than the default size
        Dimension curSize = getSize();
        
        curSize.width = (int)(curSize.width * 1.2);
        curSize.height = (int)(curSize.height * 1.2);
        
        setSize (curSize);
        
        validate ();
        
        addComponentListener(new SizeConstrainer (getSize ()));
        
        updateButtons ();
    }


    /**
     * Method setUpActions
     * 
     * @param buttonBox
     */
    private void setUpActions (WizardButtonBox buttonBox) {
        prevButton = buttonBox.getPrevButton ();
        nextButton = buttonBox.getNextButton ();
        finishButton = buttonBox.getFinishButton ();
        cancelButton = buttonBox.getCancelButton();
        
        prevButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                onPrevButton ();
            }
        });

        nextButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                onNextButton ();
            }
        });

        finishButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                onFinishButton ();
            }
        });
        
        cancelButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                onCancelButton ();
            }
        });
    }

    /**
     * Method onPrevButton
     * 
     *  
     */
    protected void onPrevButton () {
        cardStack.goToPrevCard ();
        
        updateButtons ();
    }

    /**
     * Method onNextButton
     * 
     *  
     */
    protected void onNextButton () {
        cardStack.goToNextCard ();
        
        updateButtons();
    }

    /**
     * Method onFinishButton
     * 
     *  
     */
    protected void onFinishButton () {
        if (cardStack.finish ()) {
            closeDialog(false);
        }
    }
    
    protected void onCancelButton () {
        closeDialog(true);
    }
    
    /**
     * Method updateButtons
     * 
     * 
     */
    protected void updateButtons() {
        prevButton.setEnabled(cardStack.canGoToPrevCard ());
        nextButton.setEnabled(cardStack.canGoToNextCard ());
        finishButton.setEnabled(cardStack.canFinish());
    }


    /**
     * Method makeCardStack
     * 
     * A subclass of WizardBase must override this method, and return a WizardCardStack that holds
     * the WizardCards for the wizard.  
     */
    protected abstract WizardCardStack makeCardStack ();

}
