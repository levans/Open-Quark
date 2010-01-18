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
 * ScrollableListDialog.java
 * Creation date: Oct 28, 2004.
 * By: Richard Webster
 */

package org.openquark.util.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * A dialog to display a list of values in a scrollable pane.
 * @author Richard Webster
 */
public class ScrollableListDialog extends DialogBase {

    private static final long serialVersionUID = -5124381557649259961L;

    /**
     * Create a new ScrollableListDialog.
     * 
     * @param owner    the parent frame, if any
     * @param caption  the dialog caption (i.e. title)
     * @param values   the values to be displayed in the dialog
     */
    public ScrollableListDialog(Frame owner, String caption, Collection<?> values) {
        super(owner, caption);

        JPanel topPanel = getTopPanel();

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        JList listBox = new JList(values.toArray());
        
        JScrollPane scrollPane = new JScrollPane(listBox);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        topPanel.add(scrollPane, constraints);

        getContentPane().add(topPanel);

        pack();

        addComponentListener(new SizeConstrainer(getSize()));

        Action cancelAction = new AbstractAction(caption) {
            private static final long serialVersionUID = 7582698157902959494L;

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };

        setCancelAction(cancelAction);
    }

    /**
     * A test function for the dialog.  Creates a dialog that displays a list of
     * colors.
     * 
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        List<String> values = new ArrayList<String>();
        values.add("red"); //$NON-NLS-1$
        values.add("green");//$NON-NLS-1$
        values.add("blue");//$NON-NLS-1$
        values.add("yellow");//$NON-NLS-1$
        values.add("orange");//$NON-NLS-1$
        values.add("purple");//$NON-NLS-1$
        values.add("brown");//$NON-NLS-1$
        values.add("cyan");//$NON-NLS-1$
        values.add("magenta");//$NON-NLS-1$
        values.add("silver");//$NON-NLS-1$
        values.add("grey");//$NON-NLS-1$
        values.add("white");//$NON-NLS-1$
        values.add("black");//$NON-NLS-1$
        values.add("lime");//$NON-NLS-1$
        values.add("violet");//$NON-NLS-1$

        ScrollableListDialog dlg = new ScrollableListDialog(null, "Testing", values); //$NON-NLS-1$
        dlg.doModal();
        System.exit(0);
    }


}
