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
 * DetailsDialog.java
 * Creation date: Sep 30, 2002.
 * By: Edward Lam
 */
package org.openquark.util.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.openquark.util.Messages;

/**
 * A message dialog very similar to the type displayed by JOptionPane, 
 * but with two buttons: OK and Details.
 * The Details button shows or hides an additional panel with details in it.
 * @author Edward Lam
 */
public class DetailsDialog extends DialogBase {
    
    private static final long serialVersionUID = -5170897673341834973L;

    /** The font to use for the details. */
    private static final Font DETAILS_FONT = new Font("sansserif", Font.PLAIN, 11); //$NON-NLS-1$

    /** Use this message bundle to get string resources. */
    private static Messages messages = UIMessages.instance;

    private static final String EXPAND_DETAILS_STRING = messages.getString("DD_ExpandDetails"); //$NON-NLS-1$
    private static final String CONTRACT_DETAILS_STRING = messages.getString("DD_ContractDetails"); //$NON-NLS-1$

    private static final int DETAILS_BUTTON_MNEMONIC = KeyStroke.getKeyStroke(messages.getString("DD_DetailsMnemonic")).getKeyCode(); //$NON-NLS-1$

    /** The ok button. */
    private JButton okButton;

    /** The details button. */
    private JButton detailsButton;
    
    /** The details pane. */
    private JScrollPane detailsPane;
    

    /**
     * Message Type enum pattern.
     * @author Edward Lam
     */
    public enum MessageType {
        PLAIN,
        ERROR,
        INFORMATION,
        WARNING,
        QUESTION
    }

    /**
     * Constructor for a DetailsDialog constructor.
     * @param owner the Dialog from which the dialog is displayed 
     * @param title the String to display in the dialog's title bar
     * @param message a message to be placed in the dialog box
     * @param details the details displayed in the details pane
     * @param icon the (optional) icon to use.  Null for no icon.
     */
    public DetailsDialog(Dialog owner, String title, String message, String details, Icon icon) {
        super(owner, title);
        initialize(message, details, icon);
    }

    /**
     * Constructor for a DetailsDialog constructor.
     * @param owner the Dialog from which the dialog is displayed
     * @param title the String to display in the dialog's title bar
     * @param message a message to be placed in the dialog box
     * @param details the details displayed in the details pane
     * @param type the type of message.  This is used to set the icon.
     */
    public DetailsDialog(Dialog owner, String title, String message, String details, MessageType type) {
        super(owner, title);
        initialize(message, details, getIconForType(type));
    }

    /**
     * Constructor for a DetailsDialog constructor.
     * @param owner the Frame from which the dialog is displayed 
     * @param title the String to display in the dialog's title bar
     * @param message a message to be placed in the dialog box
     * @param details the details displayed in the details pane
     * @param icon the (optional) icon to use.  Null for no icon.
     */
    public DetailsDialog(Frame owner, String title, String message, String details, Icon icon) {
        super(owner, title);
        initialize(message, details, icon);
    }

    /**
     * Constructor for a DetailsDialog constructor.
     * @param owner the Frame from which the dialog is displayed
     * @param title the String to display in the dialog's title bar
     * @param message a message to be placed in the dialog box
     * @param details the details displayed in the details pane
     * @param type the type of message.  This is used to set the icon.
     */
    public DetailsDialog(Frame owner, String title, String message, String details, MessageType type) {
        super(owner, title);
        initialize(message, details, getIconForType(type));
    }

    /**
     * Initialize the dialog.
     * @param message the dialog message
     * @param details the details to display in the details pane
     * @param icon the (optional) icon to display.  Null to display no icon
     */
    private void initialize(String message, String details, Icon icon) {

        setName("DetailsDialog"); //$NON-NLS-1$
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // main panel
        JPanel topPanel = getTopPanel();
        setContentPane(topPanel);

        // Keep track of the number of rows.
        int numRows = 0;
        
        // Add the message text area
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.insets = new Insets(5, 10, 5, 5);
            
            JTextArea messageArea = new JTextArea();
            messageArea.setText(message);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setEditable(false);
            messageArea.setBackground(getBackground());
            messageArea.setForeground(getForeground());
            messageArea.setSelectionColor(getBackground());
            messageArea.setSelectedTextColor(getForeground());
            messageArea.setCaretColor(getBackground());
            
            // icon label
            JLabel iconLabel = null;
            if (icon != null) {
                iconLabel = new JLabel();
                iconLabel.setIcon(icon);
                iconLabel.setText(""); //$NON-NLS-1$
            }
            
            // icon/message box
            JPanel iconMessageBox = new JPanel();
            iconMessageBox.setLayout(new BoxLayout(iconMessageBox, BoxLayout.X_AXIS));
            if (iconLabel != null) {
                iconMessageBox.add(iconLabel);
                iconMessageBox.add(Box.createHorizontalStrut(15));
            }
            iconMessageBox.add(messageArea);
            iconMessageBox.add(Box.createHorizontalGlue());

            getTopPanel().add(iconMessageBox, constraints);
            
            numRows++;
        }
        
        // Add an invisible area which will expand if there is no details area.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.weightx = 0.00001;
            constraints.weighty = 0.00001;

            JPanel panel = new JPanel();
            
            getTopPanel().add(panel, constraints);
            
            numRows++;
        }
        
        // Add the button area.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.SOUTHEAST;

            // ok button
            okButton = makeOKButton();
            
            // details button
            detailsButton = new JButton();
            detailsButton.setText(EXPAND_DETAILS_STRING);
            detailsButton.setMnemonic(DETAILS_BUTTON_MNEMONIC);
            
            // button box
            Box buttonBox = Box.createHorizontalBox();
            buttonBox.add(Box.createHorizontalGlue());
            buttonBox.add(Box.createHorizontalStrut(150));  // to make the dialog pack to a reasonable width
            buttonBox.add(okButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(detailsButton);
            
            getTopPanel().add(buttonBox, constraints);
            
            numRows++;
        }
        
        // Add the details text area
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 5, 5);
            
            final JTextArea displayComponent = new JTextArea();
            displayComponent.setFont(DETAILS_FONT);
            displayComponent.setText(details);
            displayComponent.setEditable(false);
            displayComponent.setLineWrap(false);
            displayComponent.setCaretPosition(0);
            
            detailsPane = new JScrollPane(displayComponent);
            detailsPane.setVisible(false);
            
            // Clamp preferred size to between 100 and 500.
            Dimension preferredDim = detailsPane.getPreferredSize();
            preferredDim.width += 10;
            if (preferredDim.width < 100) {
                preferredDim.width = 100;
            } else if (preferredDim.width > 500) {
                preferredDim.width = 500;
            }
            
            preferredDim.height += 10;
            if (preferredDim.height < 100) {
                preferredDim.height = 100;
            } else if (preferredDim.height > 500) {
                preferredDim.height = 500;
            }
            detailsPane.setPreferredSize(preferredDim);

            getTopPanel().add(detailsPane, constraints);
            
            numRows++;
        }
        
        // final setup
        pack();
        addComponentListener(new SizeConstrainer(getSize()));
        getRootPane().setDefaultButton(okButton);
        setupCancelAction(""); //$NON-NLS-1$

        // handle details button event
        detailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                boolean visible = !detailsPane.isVisible();
                setDetailsVisible(visible);
            }
        });

    }

    /**
     * Set whether the details area is visible.
     * @param visible the new visibility of the details area.
     */
    public void setDetailsVisible(boolean visible) {
        detailsButton.setText(visible ? CONTRACT_DETAILS_STRING : EXPAND_DETAILS_STRING);
        detailsPane.setVisible(visible);
        pack();
    }
    
    /**
     * Returns the icon to use for the passed in type.
     * @param messageType the message type.  Null for no icon.
     * @return Icon the corresponding icon
     */
    private Icon getIconForType(MessageType messageType) {

        // Just take the corresponding icon for the JOptionPane
        if (messageType == MessageType.ERROR) {
            return UIUtilities.getJOptionPaneIcon(JOptionPane.ERROR_MESSAGE);

        } else if (messageType == MessageType.INFORMATION) {
            return UIUtilities.getJOptionPaneIcon(JOptionPane.INFORMATION_MESSAGE);

        } else if (messageType == MessageType.WARNING) {
            return UIUtilities.getJOptionPaneIcon(JOptionPane.WARNING_MESSAGE);

        } else if (messageType == MessageType.QUESTION) {
            return UIUtilities.getJOptionPaneIcon(JOptionPane.QUESTION_MESSAGE);
        }

        return null;
    }
}
