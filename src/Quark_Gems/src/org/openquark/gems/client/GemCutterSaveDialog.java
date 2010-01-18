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
 * GemCutterSaveDialog.java
 * Creation date: (4/12/01 7:47:07 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.util.ui.UIUtilities;


/**
 * The GemCutterSaveDialog prompts the user for info when saving a gem.
 * @author Luke Evans
 */
class GemCutterSaveDialog extends JDialog {

    private static final long serialVersionUID = 1290075904896405018L;

    /** The icon to use for warning messages. */
    private static final Icon WARNING_ICON = UIUtilities.getJOptionPaneIcon(JOptionPane.WARNING_MESSAGE);
    
    /** The icon to use if everything is ok. */
    private static final Icon OK_ICON;
    static {
        Image unscaledImage = (new ImageIcon(GemCutter.class.getResource("/Resources/checkmark_large.gif"))).getImage();
        Dimension iconDimensions = new Dimension(WARNING_ICON.getIconWidth(), WARNING_ICON.getIconHeight());
        
        // Workaround for GTK.
        if (iconDimensions.width == 0 || iconDimensions.height == 0) {
            iconDimensions = new Dimension(32, 32);
        }
        OK_ICON = new ImageIcon(GemCutterPaintHelper.getScaledImage(unscaledImage, iconDimensions));
    }
    
    /** The content pane of the dialog. */
    private JPanel dialogContentPane = null;
    
    /** The main panel that contains the gems list and visibility buttons. */
    private JPanel mainPanel = null;
    
    /** The button panel for the save/cancel buttons. */
    private JPanel buttonPanel = null;
    
    /** The panel that contains the visibility buttons. */
    private JPanel visibilityPanel = null;
    
    /** Whether the user pressed the save button when the dialog was dismissed. */
    private boolean dialogAccepted = false;
    
    /** The save button. */
    private final JButton saveButton = new JButton(GemCutter.getResourceString("SaveDialog_Save"));
    
    /** The cancel button. */
    private final JButton cancelButton = new JButton(GemCutter.getResourceString("SaveDialog_Cancel"));
    
    /** The public visibility button. */
    private final JRadioButton publicButton = new JRadioButton(GemCutter.getResourceString("PublicLabel"));
    
    /** The private visibility button. */
    private final JRadioButton privateButton = new JRadioButton(GemCutter.getResourceString("PrivateLabel"));
    
    /** The protected visibility button. */
    private final JRadioButton protectedButton = new JRadioButton(GemCutter.getResourceString("ProtectedLabel"));
    
    /** The button group for the visibility buttons. */
    private final ButtonGroup buttonGroup =  new ButtonGroup();
    
    /** A label for displaying messages to the user. */
    private final JLabel statusLabel = new JLabel();
    
    /** The key event dispatcher used to catch key presses for Close, Save or Cancel. */
    private final KeyEventDispatcher mnemonicsKeyEventDispatcher;

    /**
     * GemCutterSaveDialog constructor comment.
     * @param gemCutter the GemCutter this save dialog is for
     * @param gemName the name of the gem to save
     */
    public GemCutterSaveDialog(GemCutter gemCutter, QualifiedName gemName) {

        super(gemCutter);

        if (gemCutter == null) {
            throw new NullPointerException();
        }
        
        setName("GemCutterSaveDialog");
        setModal(true);
        setResizable(false);
        setContentPane(getJDialogContentPane());

        // Add a key event dispatcher that will close this dialog when ESC is pressed.
        mnemonicsKeyEventDispatcher = new KeyEventDispatcher() {

            public boolean dispatchKeyEvent(KeyEvent evt) {
                // Only process the KEY_PRESSED events and ignore KEY_RELEASED and KEY_TYPED events for the same keystroke. 
                // If the type check is not implemented, the GemcutterSaveDialog's dispatchKeyEvent will process another event 
                // generated from a key stroke that has been dealt with already.
                if (evt.getID() == KeyEvent.KEY_PRESSED){
                    int keyCode = evt.getKeyCode();
                    if (keyCode == KeyEvent.VK_ESCAPE) {
                        evt.consume();
                        closeDialog(false);
                        return true;
                    
                    } else if (keyCode == GemCutterActionKeys.MNEMONIC_DIALOG_OPTION_SAVE) {
                        evt.consume();
                        closeDialog(true);
                        return true;
                    
                    } else if (keyCode == GemCutterActionKeys.MNEMONIC_DIALOG_OPTION_CANCEL) {
                        evt.consume();
                        closeDialog(false);
                        return true;
                    }
                }

                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(mnemonicsKeyEventDispatcher);

        // Add a window listener that calls the clean up code when the little X button is pressed.
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                closeDialog(false);
            }
        });        
        
        // setup the buttons
        buttonGroup.add(publicButton);
        buttonGroup.add(privateButton);
        buttonGroup.add(protectedButton);
        buttonGroup.setSelected(publicButton.getModel(), true);
        
        saveButton.setDefaultCapable(true);
        saveButton.setMnemonic(KeyEvent.VK_S);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog(true);
            }
        });
        
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog(false);
            }
        });
        
        getRootPane().setDefaultButton(saveButton);
        
        // setup the status label
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        statusLabel.setFont(getFont().deriveFont(Font.BOLD));

        if (gemCutter.getWorkspace().getGemEntity(gemName) != null) {
            statusLabel.setIcon(WARNING_ICON);            
            statusLabel.setText(GemCutter.getResourceString("SaveDialog_GemExists"));
            setTitle(GemCutter.getResourceString("SaveDialog_ReplaceGemButton"));            
            
        } else {
            statusLabel.setIcon(OK_ICON);
            statusLabel.setText(GemCutter.getResourceString("SaveDialog_ClickToSave"));
            setTitle(GemCutter.getResourceString("SaveDialog_SaveGemButton"));
        }
        
        pack();
    }
    
    /**
     * @return panel with the save/cancel buttons
     */
    private JPanel getButtonPanel() {
        
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(saveButton);
            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(cancelButton);
        }
        
        return buttonPanel;
    }

    /**
     * @return the main content pane of the dialog
     */
    private JPanel getJDialogContentPane() {
        
        if (dialogContentPane == null) {
            dialogContentPane = new JPanel();
            dialogContentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            dialogContentPane.setLayout(new BorderLayout(5, 5));
            dialogContentPane.add(getMainPanel(), BorderLayout.CENTER);
            dialogContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
        }
        
        return dialogContentPane;
    }
    
    /**
     * @return the panel that contains the public/private visibility buttons
     */
    private JPanel getVisibilityPanel() {
        
        if (visibilityPanel == null) {
            visibilityPanel = new JPanel();
            visibilityPanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 0));
            visibilityPanel.setLayout(new BoxLayout(visibilityPanel, BoxLayout.X_AXIS));
            visibilityPanel.add(new JLabel(GemCutter.getResourceString("SaveDialog_VisibilityLabel")));
            visibilityPanel.add(Box.createHorizontalStrut(10));
            visibilityPanel.add(publicButton);
            visibilityPanel.add(Box.createHorizontalStrut(10));
            visibilityPanel.add(protectedButton);
            visibilityPanel.add(Box.createHorizontalStrut(10));
            visibilityPanel.add(privateButton);
            visibilityPanel.add(Box.createHorizontalGlue());
        }
        
        return visibilityPanel;
    }

    /**
     * @return the main panel with the visibility buttons
     */
    private JPanel getMainPanel() {
        
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
            mainPanel.setLayout(new BorderLayout(5, 5));
            mainPanel.add(statusLabel, BorderLayout.CENTER);
            mainPanel.add(getVisibilityPanel(), BorderLayout.SOUTH);
        }
        
        return mainPanel;
    }
    
    /**
     * @return the scope the user has selected for the gem to be saved
     */
    Scope getScope() {
        return publicButton.isSelected() ? Scope.PUBLIC : 
            (privateButton.isSelected() ? Scope.PRIVATE : 
                Scope.PROTECTED);
    }
    
    /**
     * @return true if the user accepted the dialog by pressing the save button
     */
    boolean isDialogAccepted() {
        return dialogAccepted;
    }
    
    /**
     * Close this dialog.
     * @param isAccepted true if the dialog has been accepted (Save button pressed), false otherwise.
     */
    private void closeDialog(boolean isAccepted) {
        
        dialogAccepted = isAccepted;

        // Remove the key event dispatcher
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(mnemonicsKeyEventDispatcher);
        
        setVisible(false);
    }
}
