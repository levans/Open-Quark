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
 * DialogBase.java
 * Created: 17-Feb-2004
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * 
 * 
 */
public abstract class DialogBase extends JDialog {

    private static final String ESCAPE_PRESSED = "EscapePressed"; //$NON-NLS-1$
    
    protected class SizeConstrainer extends ComponentAdapter {
        private Dimension minimumSize;
        
        /**
         * Constructor SizeConstrainer
         */
        public SizeConstrainer () {
            this(null);
        }
        
        /**
         * Constructor SizeConstrainer
         * @param minimumSize
         */
        public SizeConstrainer (Dimension minimumSize) {
            this.minimumSize = minimumSize;
        }
        
        /**
         * Set the minimum size. 
         * Components with this size constrainer will be resized to fit the minimum size constraints on a
         *  componentResized() event.
         * @param minimumSize the minimum size, or null for no minimum size.
         */
        public void setMinimumSize(Dimension minimumSize) {
            this.minimumSize = minimumSize;
        }
        
        /**
         * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
         */
        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            
            if (minimumSize != null) {
                Dimension size = getSize ();
                
                size.width = Math.max(minimumSize.width, size.width);
                size.height = Math.max(minimumSize.height, size.height);
                
                setSize (size);
                
                // Re-layout all the components.
                invalidate();
                validate();
            }
        }

    }
    
    private boolean cancelled = true;
    
    private JPanel topPanel;


    /**
     * Method makeRadioButton
     * 
     * @param text
     * @param actionCommand
     * @param selected
     * @return JRadioButton
     */
    static public JRadioButton makeRadioButton (String text, String actionCommand, boolean selected) {
        JRadioButton result = new JRadioButton (text, selected);
        
        result.setActionCommand(actionCommand);
        
        return result;
    }
    
    /**
     * Method makeRadioButtonBox
     * 
     * @param buttonGroup
     * @return Box
     */
    static public Box makeRadioButtonBox (ButtonGroup buttonGroup) {
        Box box = new Box (BoxLayout.Y_AXIS);

        for (Enumeration<AbstractButton> buttonEnum = buttonGroup.getElements(); buttonEnum.hasMoreElements(); ) {
            box.add (buttonEnum.nextElement());
        }
        
        return box;
    }
    
    /**
     * Add a horizontal separator component to a container with a grid bag layout.
     * 
     * @param container the container to which the separator will be added.
     * @param label the label to display in the separator, if any.  Null if none.
     * @param gridy the gridy value to associate with the separator component.
     * @param insets the insets to associate with the separator component, or null to use default insets.
     */
    public static void addSeparator(Container container, String label, int gridy, Insets insets) {
        LayoutManager layoutManager = container.getLayout();
        if (!(layoutManager instanceof GridBagLayout)) {
            throw new IllegalArgumentException("The container's layout manager must be a GridBagLayout. not: " +  //$NON-NLS-1$
                                               layoutManager.getClass().getName());
        }
        
        // The box containing the separator.
        Box separatorBox = new Box(BoxLayout.X_AXIS);

        // The left side of the box - contains a separator.
        Box leftSeparatorBox = new Box(BoxLayout.Y_AXIS);
        leftSeparatorBox.add(Box.createGlue());
        leftSeparatorBox.add(new JSeparator(SwingConstants.HORIZONTAL));
        leftSeparatorBox.add(Box.createGlue());
        
        separatorBox.add(leftSeparatorBox);
        
        // The label in the middle of the separator.
        if (label != null) {
            JLabel l = new JLabel(" " + label + " ");  //$NON-NLS-1$//$NON-NLS-2$
            l.setMaximumSize(l.getPreferredSize());
            separatorBox.add(l);
        }
        
        // The right side of the box - contains a separator.
        Box rightSeparatorBox = new Box(BoxLayout.Y_AXIS);
        rightSeparatorBox.add(Box.createGlue());
        rightSeparatorBox.add(new JSeparator(SwingConstants.HORIZONTAL));
        rightSeparatorBox.add(Box.createGlue());
        separatorBox.add(rightSeparatorBox);
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = gridy;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        if (insets != null) {
            constraints.insets = insets;
        }

        container.add(separatorBox, constraints);
    }



    /**
     * Constructor DialogBase.
     * @param owner
     * @param caption
     */
    public DialogBase(Dialog owner, String caption) {
        super (owner, caption);
    }
    
    /**
     * Constructor DialogBase.
     * @param owner
     * @param caption
     */
    public DialogBase(Frame owner, String caption) {
        super (owner, caption);
    }

    /**
     * Method doModal
     * 
     * @return true iff the dialog was not cancelled
     */
    public boolean doModal () {
        cancelled = true;
        setModal(true);
        
        setLocationRelativeTo (getOwner ());
        
        setVisible(true);
        
        return !cancelled;
    }

    /**
     * Method getTopPanel
     * 
     * @return a JPanel using a GridBagLayout, in which the dialog's controls should be placed
     */
    protected JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel(new GridBagLayout());
            
            // The border size is based on Sun's UI recommendations
            topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        }
        
        return topPanel;
    }
    
    protected Box addOKCancelButtons (int rowN, int colCount) {
        Box buttonBox = new Box (BoxLayout.X_AXIS);
        
        buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JButton okButton = makeOKButton();
        
        getRootPane().setDefaultButton(okButton);
        
        JButton cancelButton = makeCancelButton();

        buttonBox.add (Box.createHorizontalGlue());
        buttonBox.add (okButton);
        buttonBox.add (Box.createHorizontalStrut(5));
        buttonBox.add (cancelButton);

        GridBagConstraints constraints = new GridBagConstraints ();
        
        constraints.gridx = 0;
        constraints.gridy = rowN;
        constraints.gridwidth = colCount;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        getTopPanel ().add (buttonBox, constraints);
        
        return buttonBox;
    }

    
    /**
     * Method makeOKButton
     * 
     * @return JButton
     */
    protected JButton makeOKButton () {
        JButton okButton = new JButton ();
        
        okButton.setAction(new AbstractAction ("OK") {//$NON-NLS-1$
            private static final long serialVersionUID = 1047472612446765030L;

            public void actionPerformed(ActionEvent e) {
                closeDialog(false);
            }
        });
        
        return okButton;
    }
    
    /**
     * Method to allow subclasses to close the dialog programatically.  Normally dialogs are closed when
     * the OK or Cancel buttons are triggered, but they may be closed for other reasons by a subclass.
     * @param cancelDialog If true then the dialog is cancelled and doModal() will return false.  If
     * false the the dialog is 'OKed' and doModal() may return true.
     */
    protected void closeDialog(boolean cancelDialog) {
        if (cancelDialog) {
            // If the dialog is cancelled then simply hide it
            setVisible(false);
        } else if (onOK()) {
            // If the dialog is not cancelled and the onOK() method returns true then set the cancelled
            // flag to true and close the dialog
            cancelled = cancelDialog;
            setVisible(false);
        }
        
        // We can get here if the dialog was not cancelled, but the onOK() check failed.  Leave the
        // dialog visible on the screen.
    }
    
    /**
     * Method onOK
     * 
     * Override this method to be notified when the user presses OK.
     * 
     * @return true iff the operation succeeded and the dialog should close
     */
    protected boolean onOK () {
        return true;
    }

    /**
     * Returns a cancel button with a default caption.
     * This should be called after dialogInit() so that the Esc key binding will work.
     * 
     * @return JButton
     */
    protected JButton makeCancelButton () {
        return makeCancelButton("Cancel");    //$NON-NLS-1$
    }

    /**
     * Returns a cancel button with the specified caption.
     * This should be called after dialogInit() so that the Esc key binding will work.
     * 
     * @param caption
     * @return JButton
     */
    protected JButton makeCancelButton (String caption) {
        Action cancelAction = setupCancelAction(caption);
        return new JButton (cancelAction);
    }

    /**
     * Set up the cancel action.
     * The action (which closes the dialog) is created, and the action and input maps are updated so that the action is activated
     *   when esc is pressed.
     * @param caption the caption associated with the action.
     * @return the cancel action.
     */
    protected Action setupCancelAction(String caption) {
        Action cancelAction = new AbstractAction (caption) {
            private static final long serialVersionUID = 1407987945136684389L;

            public void actionPerformed(ActionEvent e) {
                closeDialog(true);
            }
        };

        setCancelAction(cancelAction);
        
        return cancelAction;
    }

    protected final void setCancelAction(Action cancelAction) { 
        JRootPane theRootPane = getRootPane ();
        InputMap inputMap = theRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        inputMap.put(keyStroke, ESCAPE_PRESSED);
        
        ActionMap actionMap = theRootPane.getActionMap();
        actionMap.put(ESCAPE_PRESSED, cancelAction);
    }
}
