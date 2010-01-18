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
 * WizardButtonBox.java
 * Created: 23-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.border.Border;


/**
 * 
 * 
 */
public class WizardButtonBox extends Box {
    
    private static final long serialVersionUID = 7185793213158316355L;

    private final JButton prevButton = new JButton (UIMessages.instance.getString("WizardBack")); //$NON-NLS-1$
    private final JButton nextButton = new JButton (UIMessages.instance.getString("WizardNext")); //$NON-NLS-1$
    private final JButton finishButton = new JButton (UIMessages.instance.getString("WizardFinish")); //$NON-NLS-1$
    private final JButton cancelButton = new JButton (UIMessages.instance.getString("WizardCancel")); //$NON-NLS-1$

    public WizardButtonBox () {
        super (BoxLayout.X_AXIS);
        
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        
        Border outerBorder = BorderFactory.createEtchedBorder();
        
        setBorder(BorderFactory.createCompoundBorder(outerBorder, emptyBorder));
        
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        makeButtons ();
    }

    /**
     * Method makeButtons
     * 
     * 
     */
    private void makeButtons() {
        add(Box.createHorizontalGlue());
        add (prevButton);
        add(Box.createHorizontalStrut(5));
        add (nextButton);
        add(Box.createHorizontalStrut(5));
        add (finishButton);
        add(Box.createHorizontalStrut(5));
        add (cancelButton);
    }

    /**
     * Method getPrevButton
     * 
     * @return Returns the Previous button
     */
    public JButton getPrevButton() {
        return prevButton;
    }

    /**
     * Method getNextButton
     * 
     * @return Returns the Next button
     */
    public JButton getNextButton() {
        return nextButton;
    }
    
    /**
     * Method getFinishButton
     * 
     * @return Returns the Finish button
     */
    public JButton getFinishButton () {
        return finishButton;
    }
    
    /**
     * Method getCancelButton
     * 
     * @return Returns the Finish button
     */
    public JButton getCancelButton () {
        return cancelButton;
    }
    
}
