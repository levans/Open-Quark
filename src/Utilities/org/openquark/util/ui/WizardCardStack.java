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
 * WizardCardStack.java
 * Created: 23-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JPanel;



/**
 * 
 * 
 */
public class WizardCardStack extends JPanel {
    
    private static final long serialVersionUID = -9109618473153877966L;

    private final Stack<WizardCard> viewedPageStack = new Stack<WizardCard>();
    
    private final Map<String, WizardCard> cardMap = new HashMap<String, WizardCard> ();
    
    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener () {

        private static final long serialVersionUID = -9109618473153877966L;
        
        public void propertyChange (PropertyChangeEvent evt) {
            firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    };

    /**
     * Constructor WizardCardStack
     * 
     * 
     */
    public WizardCardStack () {
        super (new CardLayout ());
        
        setBorder(BorderFactory.createEtchedBorder());
    }

    /**
     * Method addCard
     * 
     * This method adds a WizardCard to the stack.
     */
    protected void addCard(WizardCard card) {
        // The first card added will be the initial card shown
        if (viewedPageStack.size() == 0) {
            viewedPageStack.push (card);
        }
        
        cardMap.put(card.getCardName(), card);
        
        card.buildUI();
        add (card, card.getCardName ());
        
        card.addPropertyChangeListener(propertyChangeListener);
    }
    
    /**
     * Method finishInit
     * 
     * This method must be called after all the cards have been added to the card stack.
     * It ensures that the first card of the wizard is initialised.
     */
    protected void finishInit () {
        getCurrentCard().initCard();
    }

    /**
     * Method canGoToPrevCard
     * 
     * @return true iff it's possible to move to the previous card.
     */
    public boolean canGoToPrevCard () {
        return viewedPageStack.size() > 1;
    }

    /**
     * Method goToPrevCard
     * 
     * Move to the previous card. This can fail if the current card cannot commit its values,
     * or the previous card cannot be initialised.
     */
    public void goToPrevCard () {
        // validate current card
        WizardCard currentCard = getCurrentCard();
        
        if (currentCard == null || !currentCard.commitChanges()) {
            return;
        }
        
        viewedPageStack.pop();

        // Initialize the UI for the new current card.
        currentCard = getCurrentCard();
        
        if (currentCard == null || !currentCard.initCard()) {
            return;
        }
        
        getCardLayout().show(this, currentCard.getCardName());
    }

    /**
     * Method canGoToNextCard
     * 
     * @return true iff it's posible to go to the next card
     */
    public boolean canGoToNextCard () {
        WizardCard currentCard = getCurrentCard();
        
        return currentCard != null && currentCard.canGoToNextCard();
    }

    /**
     * Method goToNextCard
     * 
     * Move to the next card. This can fail if the current card cannot commit its values,
     * or if the next card cannot be initialised.
     */
    public void goToNextCard () {
        // validate current card
        WizardCard currentCard = getCurrentCard();
        
        if (currentCard == null || !currentCard.commitChanges()) {
            return;
        }

        String nextCardName = currentCard.getNextCardName();
        
        WizardCard nextCard = cardMap.get(nextCardName);
        
        if (nextCard == null || !nextCard.initCard()) {
            return;
        }
        
        viewedPageStack.push (nextCard);
        
        getCardLayout().show(this, nextCardName);
    }

    /**
     * Method canFinish
     * 
     * @return true if it's possible to finish on the current card
     */
    public boolean canFinish () {
        WizardCard currentCard = getCurrentCard();

        return currentCard != null && currentCard.canFinish();
    }


    /**
     * Method finish
     * 
     * @return true iff the attempt to finish on the current card succeeded 
     */
    public boolean finish () {
        // validate current card
        WizardCard currentCard = getCurrentCard();

        return currentCard != null && currentCard.onFinish();
    }

    /**
     * Returns the first page of the wizard.
     */
    WizardCard getFirstCard() {
        // The current page will be at the bottom of the stack.
        if (viewedPageStack.isEmpty()) {
            return null;
        }
            
        return viewedPageStack.get(0);
    }

    
    /**
     * Method getCurrentCard
     * 
     * @return the current WizardCard
     */
    WizardCard getCurrentCard () {
        // The current page will be at the top of the stack.
        if (viewedPageStack.isEmpty()) {
            return null;
        }
            
        return viewedPageStack.peek();
    }
    
    /**
     * Method getCardLayout
     * 
     * @return the layout manager for the card stack, as a CardLayout
     */
    private CardLayout getCardLayout () {
        return (CardLayout) getLayout ();
    }

}
