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
 * WizardCard.java
 * Created: 23-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.io.Serializable;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;


/**
 * 
 * 
 */
public abstract class WizardCard extends JPanel {
    
    public static final String CARD_STATE_PROPERTY_NAME = "wizardCardState"; //$NON-NLS-1$
    
    public static final int INFO_TIP = 0;
    public static final int WARNING_TIP = 1;
    public static final int ERROR_TIP = 2;
    public static final int ALLOK_TIP = 3;
    
    public static final class TipInfo implements Serializable {
        
        private static final long serialVersionUID = 4248465953049210294L;

        public final int tipType;
        
        public final String message;
        
        /**
         * Constructor TipInfo
         * 
         * @param tipType
         * @param message
         */
        public TipInfo (final int tipType, final String message) {
            this.tipType = tipType;
            this.message = message;
        }
        
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals (Object obj) {
            if (obj instanceof TipInfo) {
                TipInfo other = (TipInfo)obj;
                
                return tipType == other.tipType && message.equals(other.message);
            }
            
            return false;
        }
        
        
        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode () {
            return message.hashCode() ^ tipType;
        }
    }
    
    private static class TipLabel extends JLabel {
        private static final long serialVersionUID = -527521810142948389L;

        private static final String [] iconNames = {
            "smallInfo.gif", //$NON-NLS-1$
            "smallWarning.gif", //$NON-NLS-1$
            "smallError.gif", //$NON-NLS-1$
            "smallAllOK.gif", //$NON-NLS-1$
        };
        
        private static Icon [] icons = new Icon [4]; 
        
        private TipInfo info;
        
        TipLabel () {
            setHorizontalAlignment(SwingConstants.LEFT);
            
            Font tipFont = getFont();
            
            tipFont = tipFont.deriveFont(Font.BOLD);
            
            setFont(tipFont);
            
            setBorder (BorderFactory.createEmptyBorder(2, 5, 2, 5));
        }
        
        /**
         * Method setInfo
         * 
         * @param newInfo
         */
        void setInfo (TipInfo newInfo) {
            if (info == null) {
                if (newInfo == null) {
                    // do nothing
                    return;
                }
            } else if (info.equals(newInfo)) {
                // do nothing
                return;
            }
            
            info = newInfo;
            
            if (info == null) {
                setIcon (null);
                setText (""); //$NON-NLS-1$
            } else {
                setIcon (findIcon (info.tipType));
                setText (info.message);
            }
        }

        /**
         * Method findIcon
         * 
         * @param tipType
         * @return an Icon that represents the given tipType
         */
        private Icon findIcon (int tipType) {
            if (tipType < 0 || tipType >= icons.length) {
                throw new IllegalArgumentException ("Invalid tip type: " + tipType); //$NON-NLS-1$
            }
            
            if (icons [tipType] == null) {
                icons [tipType] = loadIcon (tipType);
            }
            
            return icons [tipType];
        }

        /**
         * Method loadIcon
         * 
         * @param tipType
         * @return an Icon that represents the given tipType
         */
        private Icon loadIcon (int tipType) {
            return loadImageIcon (iconNames [tipType]);
        }

        /**
         * Method loadImageIcon
         * 
         * @param filename
         * @return an ImageIcon created from the given file 
         */
        private ImageIcon loadImageIcon(String filename) {
            String iconFileName = "/Resources/" + filename; //$NON-NLS-1$ 
        
            URL url = this.getClass().getResource(iconFileName);
        
            if (url != null) {
                return new ImageIcon(url);
            } else {
                return null;
            }
        }
    }

    private TipLabel tipLabel;
    
    public WizardCard() {
    }

    /**
     * Method getTitle
     * 
     * @return String
     */
    protected abstract String getTitle();

    /**
     * Method getSubtitle
     * 
     * @return String
     */
    protected abstract String getSubtitle();
    
    /**
     * Method getCardName
     * 
     * @return String
     */
    public abstract String getCardName();

    /**
     * Returns the name of the following card, or null if this is the last card.
     */
    protected abstract String getNextCardName();

    /**
     * Method getMainPanel
     * 
     * @return Component
     */
    protected abstract JComponent getMainPanel();
    
    /**
     * Method initControls
     * 
     * @return true iff initialising the controls on the card succeeded
     */
    protected abstract boolean initControls (); 
    
    /**
     * Called before moving to the next card in the wizard. If the changes were
     * committed successfully, returns <code>true</code> and the wizard moves
     * to the next page. Otherwise, the wizard remains on this page.
     * 
     * @return true if the changes made on the card were committed successfully
     *         and the wizard can proceed to the next card
     */
    protected abstract boolean commitChanges ();
    
    /**
     * Method canFinish
     * 
     * @return true if it's possible to finish on this card
     */
    protected abstract boolean canFinish ();
    
    /**
     * Called when the wizard is about to finish.
     * 
     * @return true if the finish operation succeeds
     */
    protected abstract boolean onFinish ();
    
    /**
     * Method buildUI
     * 
     * Constructs the UI.
     * 
     * This needs to be done after the constructor since it will call methods implemented
     * in subclasses.
     */
    public void buildUI() {
        setLayout(new BorderLayout());
        add(getHeaderPanel(), BorderLayout.NORTH);
        JComponent mainPanel = getMainPanel();
        
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JComponent getHeaderPanel () {
//        JPanel headerPanel = new JPanel (new BorderLayout (5, 5));
        
        Box headerPanel = new Box (BoxLayout.Y_AXIS);
        
        JPanel titlePanel = getTitlePanel();
        
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
//        Dimension prefSize = titlePanel.getPreferredSize();
//System.out.println("Title panel preferred size: " + prefSize);        
        
//        titlePanel.setPreferredSize(prefSize);
//        titlePanel.setMinimumSize(prefSize);
        
        headerPanel.add(titlePanel);

        JLabel tipLabel = getTipLabel ();
        
        tipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(tipLabel);
        
        return headerPanel;
    }
    
    private JLabel getTipLabel () {
        tipLabel = new TipLabel ();
        
//        tipLabel.setAlignmentX(0);
//        tipLabel.setAlignmentY(1);
        
//        tipLabel.setInfo(new TipInfo (INFO_TIP, "For my info"));
//        
        Dimension prefSize = tipLabel.getPreferredSize();
        
        prefSize.height *= 4;
//        prefSize.width = 1000;
//System.out.println("Tip label preferred size: " + prefSize);        
        
//        tipLabel.setPreferredSize(prefSize);
        tipLabel.setMinimumSize(prefSize);
        
        prefSize.width = Integer.MAX_VALUE;
        tipLabel.setMaximumSize(prefSize);

//        Font tipFont = tipLabel.getFont();
//        
//        FontMetrics fontMetrics = tipLabel.getGraphics().getFontMetrics(tipFont);
//        
//        int fontHeight = fontMetrics.getHeight();
//        
//        tipLabel.setPreferredSize(new Dimension (10, 2 * fontHeight));
        
        return tipLabel;
    }
    
    /**
     * @return the white title panel that appears at the top of the dialog
     */
    private JPanel getTitlePanel() {
        
        JPanel titlePanel = new JPanel();
        
        titlePanel.setBackground(Color.WHITE);
        
        Border compoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titlePanel.setBorder(compoundBorder);
        
        titlePanel.setLayout(new BorderLayout(5, 5));
        
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 2));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel subTitleLabel = new JLabel(getSubtitle());
        titlePanel.add(subTitleLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    boolean initCard () {
        if (initControls ()) {
            updateTip ();
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Method canGoToPrevCard
     * 
     * Normally the card stack allows movement to the previous card iff any other cards 
     * have been visited. This method allows a card to veto returning to a previous card.
     * 
     * @return true if this card allows going to the previous card
     */
    public boolean canGoToPrevCard () {
        return true;
    }
    
    /**
     * Method canGoToNextCard
     * 
     * Override this method if you want to implement more fine-grained control over
     * movement to the next card. For example, if you want to allow or disallow 
     * movement to the next card depending on the state of the current card.
     * 
     * @return true if it's possible to go to the next card
     */
    public boolean canGoToNextCard () {
        return getNextCardName() != null;
    }

    protected abstract TipInfo getTipInfo ();
    
    protected void cardStateChanged () {
        firePropertyChange(CARD_STATE_PROPERTY_NAME, null, null);
        
        updateTip ();
    }
    
    private void updateTip () {
        showTipInfo (getTipInfo());
    }
    
    protected void showTipInfo (TipInfo info) {
        tipLabel.setInfo(info);
    }
    
    protected Cursor setWaitCursor() {
        return setTopLevelCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void restoreCursor() {
        setTopLevelCursor(null);
    }

    /**
     * Method setTopLevelCursor
     * 
     */
    private Cursor setTopLevelCursor(Cursor cursor) {
        Component root = SwingUtilities.getRoot(this);

//System.out.println("Set cursor for " + root.getClass().getName() + " to " + cursor + " thread = " + Thread.currentThread().getName());        
        Cursor oldCursor = root.getCursor();

        root.setCursor(cursor);

        return oldCursor;
    }

}
