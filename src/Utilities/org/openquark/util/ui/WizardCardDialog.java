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
 * WizardCardDialog.java
 * Created: 8-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;


/**
 * 
 * 
 */
public abstract class WizardCardDialog extends DialogBase {
    
    private WizardCard card;
    
    private JButton okButton;

    /**
     * Constructor WizardCardDialog
     * 
     * @param owner
     * @param caption
     */
    public WizardCardDialog (Frame owner, String caption) {
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
     * 
     */
    private void createContents () {
        card = makeCard();
        
        card.buildUI();
        
        card.initCard();
        
        card.addPropertyChangeListener(WizardCard.CARD_STATE_PROPERTY_NAME, new PropertyChangeListener () {

            public void propertyChange (PropertyChangeEvent evt) {
                updateButtons ();
            }
        });
        
        getContentPane().setLayout(new BorderLayout ());

        getContentPane ().add (card, BorderLayout.CENTER);
        getContentPane ().add (makeButtonBox (), BorderLayout.SOUTH);

        pack ();
        
        addComponentListener(new SizeConstrainer (getSize ()));
        
        updateButtons ();
    }


    /**
     * Method makeButtonBox
     * 
     * @return Returns a Box containing the OK and Cancel buttons
     */
    private Component makeButtonBox () {
        Box buttonBox = new Box (BoxLayout.X_AXIS);
        
        okButton = makeOKButton();
        
        getRootPane().setDefaultButton(okButton);
        
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add (makeCancelButton());
        buttonBox.add(Box.createHorizontalStrut(5));
        buttonBox.add (okButton);
        
        return buttonBox;

    }


    /**
     * Method makeCard
     * 
     * @return Returns the WizardCard that holds the content of the dialog
     */
    protected abstract WizardCard makeCard ();


    /**
     * Method updateButtons
     * 
     * 
     */
    private void updateButtons () {
        okButton.setEnabled(card.canFinish());
    }

}
