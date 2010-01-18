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
 * ScrollableImageDialog.java
 * Creation date: Sept 2, 2005.
 * By: Richard Webster
 */

package org.openquark.util.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * A dialog to display an image in a scrollable pane.
 * @author Richard Webster
 */
public class ScrollableImageDialog extends DialogBase {

    private static final long serialVersionUID = -2706970160522698071L;

    /**
     * Create a new ScrollableImageDialog.
     * 
     * @param owner    the parent frame, if any
     * @param caption  the dialog caption (i.e. title)
     * @param image    the image to be displayed
     */
    public ScrollableImageDialog(Frame owner, String caption, final Image image) {
        super(owner, caption);

        JPanel topPanel = getTopPanel();

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        JLabel imageComponent = new JLabel(new ImageIcon(image));

        JScrollPane scrollPane = new JScrollPane(imageComponent);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        topPanel.add(scrollPane, constraints);

        getContentPane().add(topPanel);

        pack();

        addComponentListener(new SizeConstrainer(getSize()));

        Action cancelAction = new AbstractAction(caption) {
            private static final long serialVersionUID = -6019862369918302449L;

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };

        setCancelAction(cancelAction);
    }

    /**
     * A helper method to display the image in a dialog with no parent frame.
     */
    public static void displayImage(String caption, Image image) {
        ScrollableImageDialog dlg = new ScrollableImageDialog(null, caption, image);
        dlg.doModal();
    }

    /**
     * A test function for the dialog.  Displays an image specified by the user.
     * 
     * @param args command-line argumentss.  args[0] should be the full pathname
     *   of the image to be displayed.
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Usage: java ScrollableImageDialog {image path}" ); //$NON-NLS-1$
            System.exit(1);
        }
        
        ImageIcon imageIcon = new ImageIcon(args[0]);
        if(imageIcon == null) {
            System.err.println("Unable to retrieve image " + args[0]); //$NON-NLS-1$
            System.exit(2);
        }

        ScrollableImageDialog dlg = new ScrollableImageDialog(null, "Testing", imageIcon.getImage()); //$NON-NLS-1$
        dlg.doModal();
        System.exit(0);
    }
}
