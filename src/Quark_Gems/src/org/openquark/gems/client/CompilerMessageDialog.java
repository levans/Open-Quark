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
 * CompilerMessageDialog.java
 * Creation date: (Apr 20, 2006)
 * By: James Wright
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;


/**
 * A dialog that shows the contents of a CompilerMessageLogger.
 * 
 * Usage:
 * 1) Create an instance
 * 2) Set the description
 * 3) Add messages
 * 4) Show the dialog
 * 
 * @author James Wright
 */
class CompilerMessageDialog extends JDialog {
    private static final long serialVersionUID = 8721051697393955887L;

    /** The icon to use for error messages. */
    private static final Icon ERROR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/error.gif"));
    
    /** The icon to use for warning messages. */
    private static final Icon WARNING_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/warning.gif"));
    
    /** The icon to use for warning messages. */
    private static final Icon INFO_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/smallinfo.gif"));
    
    /** 
     * Text area that displays description set by the caller (usually something
     * along the lines of "look out, errors happened!")
     */
    private final JTextArea descriptionText = new JTextArea();
    
    /** Holds the list of messages */
    private final JList messageList = new JList(new DefaultListModel());
    
    /** Provides scroll bars etc. for messagesList */
    private final JScrollPane messagePane = new JScrollPane(messageList);
    
    /** The OK button */
    private final JButton okButton = new JButton("OK");
    
    /**
     * This is a custom cell renderer that we use to add the little severity icons to
     * the messages in the list.
     * 
     * @author James Wright
     */
    private static class MessageRenderer extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 6898987006278555574L;

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
            CompilerMessage message = (CompilerMessage)value;
            
            if(message.getSeverity() == CompilerMessage.Severity.FATAL) {
                setIcon(ERROR_ICON);
            
            } else if(message.getSeverity() == CompilerMessage.Severity.ERROR) {
                setIcon(ERROR_ICON);

            } else if(message.getSeverity() == CompilerMessage.Severity.WARNING) {
                setIcon(WARNING_ICON);
            
            } else if(message.getSeverity() == CompilerMessage.Severity.INFO) {
                setIcon(INFO_ICON);
            }
            
            setText(message.toString());
            return this;
        }
    }
    
    /**
     * @param parent Parent frame for the dialog
     */
    CompilerMessageDialog(final JFrame parent, final boolean warningsOnly) {
        super(parent);
        initialize(warningsOnly);
    }
    
    /**
     * Set up the UI elements and layout.
     */
    private void initialize(final boolean warningsOnly) {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle(GemCutterMessages.getString(warningsOnly ? "CompilerMessageDialogTitle_Warning" : "CompilerMessageDialogTitle_Error"));
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setContentPane(mainPanel);
            
            descriptionText.setEditable(false);
            descriptionText.setBackground(getBackground());
            descriptionText.setWrapStyleWord(true);
            descriptionText.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 2));
            
            getContentPane().add(descriptionText, "North");
            getContentPane().add(messagePane, "Center");

            Box buttonBox = Box.createHorizontalBox();
            buttonBox.add(Box.createHorizontalGlue());
            buttonBox.add(okButton);
            buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 2));
            getContentPane().add(buttonBox, "South");

            okButton.setAction(
                    new AbstractAction(GemCutterMessages.getString("OKButton")) {
                        private static final long serialVersionUID = -2921721933181924292L;

                        public void actionPerformed(ActionEvent e) {
                            CompilerMessageDialog.this.dispose();
                        }
                    });

            setModal(true);
            pack();
            
            setSize(400, 200);
            GemCutter.centerWindow(this);
    }
    
    /** Remove all messages from the dialogue */
    void clearMessages() {
        DefaultListModel messageModel = (DefaultListModel)messageList.getModel();
        messageModel.clear();
    }
    
    /** 
     * Adds all the messages contained in logger to the dialog
     * @param logger CompilerMessageLogger containing messages to add
     */
    void addMessages(CompilerMessageLogger logger) {
        
        messageList.setCellRenderer(new MessageRenderer());
        DefaultListModel messageModel = (DefaultListModel)messageList.getModel();
        
        for (final CompilerMessage compilerMessage : logger.getCompilerMessages()) {
            messageModel.addElement(compilerMessage);
        }
    }
    
    /**
     * Sets the description text area at the top of the dialog to text.
     * The text is assumed to be already localized.
     * @param text String to place in the description area.
     */
    void setDescriptionText(String text) {
        descriptionText.setText(text);
    }
}
