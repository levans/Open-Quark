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
 * CustomDrawDialog.java
 * Creation date: Oct 17, 2007.
 * By: Richard Webster
 */

package org.openquark.cal.foreignsupport.module.Drawing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openquark.cal.runtime.CalFunction;
import org.openquark.util.ui.DialogBase;


/**
 * A dialog to display the results of a specified CAL drawing function.
 * @author Richard Webster
 */
public class CustomDrawDialog extends DialogBase {

	private static final long serialVersionUID = 3451254813673521428L;

	/**
     * Create a new CustomDrawDialog.
     * 
     * @param owner    the parent frame, if any
     * @param caption  the dialog caption (i.e. title)
     * @param drawFn   the CAL function (Graphics -> Graphics) to draw the dialog contents
     */
    public CustomDrawDialog(Frame owner, String caption, final CalFunction /*Graphics -> Graphics*/ drawFn) {
        super(owner, caption);

        JPanel topPanel = getTopPanel();

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        JComponent drawComponent = new JComponent() {
			private static final long serialVersionUID = 8082752999146629222L;

			/**
			 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
			 */
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				// Invoke the CAL function to draw the component.
				drawFn.evaluate(g);
			}
        };

        JScrollPane scrollPane = new JScrollPane(drawComponent);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        topPanel.add(scrollPane, constraints);

        getContentPane().add(topPanel);

        pack();

        addComponentListener(new SizeConstrainer(getSize()));

        Action cancelAction = new AbstractAction(caption) {
			private static final long serialVersionUID = -3971325738204923022L;

			public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };

        setCancelAction(cancelAction);
    }

    /**
     * A helper method to display the custom draw dialog with no parent frame.
     */
    public static void displayCustomDrawDialog(String caption, CalFunction /*Graphics -> Graphics*/ drawFn) {
        CustomDrawDialog dlg = new CustomDrawDialog(null, caption, drawFn);
        dlg.doModal();
    }
}
