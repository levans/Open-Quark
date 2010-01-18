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
 * ScrollableMessageDialog.java
 * Creation date: June 1, 2004.
 * By: Richard Webster
 */

package org.openquark.util.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * A dialog to display a message in a scrollable text pane.
 * @author Richard Webster
 */
public class ScrollableMessageDialog extends DialogBase {
    private static final long serialVersionUID = -7659805178818805947L;

    /**
     * Create a new ScrollableMessageDialog.
     * 
     * @param owner    the parent frame, if any
     * @param caption  the dialog caption (i.e. title)
     * @param message  the text to be displayed in the dialog
     */
    public ScrollableMessageDialog(Frame owner, String caption, String message) {
        super(owner, caption);

        JPanel topPanel = getTopPanel();

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        topPanel.add(scrollPane, constraints);

        getContentPane().add(topPanel);

        pack();

        addComponentListener(new SizeConstrainer(getSize()));

        Action cancelAction = new AbstractAction(caption) {
            private static final long serialVersionUID = 3487738699501470273L;

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };

        setCancelAction(cancelAction);
    }

    /**
     * A test function for the dialog.  Displays a dialog containing some 
     * simple multi-line text.
     * 
     * @param args  command-line arguments (not used).
     */
    public static void main(String[] args) {
        ScrollableMessageDialog dlg = new ScrollableMessageDialog(null, "Testing", "Here is some text\non multiple lines.\nHere is some more text.");  //$NON-NLS-1$//$NON-NLS-2$
        dlg.doModal();
        System.exit(0);
    }
}
