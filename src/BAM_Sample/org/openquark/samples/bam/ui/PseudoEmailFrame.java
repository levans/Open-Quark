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
 * PseudoEmailFrame.java
 * Created: Jan 8, 2005
 * By: Rick Cameron
 */
package org.openquark.samples.bam.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * @author RCameron
 */
public class PseudoEmailFrame extends JFrame {

    private static final long serialVersionUID = -4797499825137366937L;

    private static PseudoEmailFrame theFrame = null;

    private DefaultTableModel tableModel;

    private JTable table;

    private List<Message> messages = new ArrayList<Message>();

    private JTextArea textArea;

    private static class Message {

        public final String toList;

        public final String subject;

        public final String message;

        /**
         * @param toList
         * @param subject
         * @param message
         */
        Message(final String toList, final String subject, final String message) {
            super();
            this.toList = toList;
            this.subject = subject;
            this.message = message;
        }

    }

    /**
     * Method addMessage
     * 
     * @param toList
     * @param subject
     * @param message
     */
    public static void addMessage(final String toList, final String subject, final String message) {
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                PseudoEmailFrame frame = getInstance();
                
                frame.addMessage(new Message(toList, subject, message));
            }
        });
    }

    /**
     * Method addMessage
     * 
     * @param message
     */
    private void addMessage(Message message) {
        messages.add(message);

        tableModel.addRow(new String[] { message.toList, message.subject });

        int lastRowN = tableModel.getRowCount() - 1;

        table.getSelectionModel().setSelectionInterval(lastRowN, lastRowN);
    }

    /**
     * Method getInstance
     * 
     * @return Returns the singleton PseudoEmailFrame
     */
    private synchronized static PseudoEmailFrame getInstance() {
        if (theFrame == null) {
            theFrame = new PseudoEmailFrame();
        }

        return theFrame;
    }

    /**
     * Constructor PseudoEmailFrame
     * 
     * 
     */
    private PseudoEmailFrame() {
        super("Messages");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            /**
             * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosed (WindowEvent e) {
                theFrame = null;
            }
        });
        
        JMenuBar menuBar = new JMenuBar ();
        
        JMenu toolsMenu = new JMenu ("Tools");
        
        toolsMenu.add (new AbstractAction ("Clear all") {

            private static final long serialVersionUID = 579196649577493146L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed (ActionEvent e) {
                onClearAll ();
            }
        });
        
        toolsMenu.add (new AbstractAction ("Close") {
        
            private static final long serialVersionUID = 2184805220667737159L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed (ActionEvent e) {
                onClose ();
            }
        
        });
        
        menuBar.add (toolsMenu);
        
        setJMenuBar (menuBar );

        tableModel = createTableModel ();
        table = new JTable(tableModel);
        table.setRowSelectionAllowed(true);
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        onTableSelectionChanged(e);
                    }
                });

        JScrollPane tableScroller = new JScrollPane(table);

        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(400, 400));

        JScrollPane textAreaScroller = new JScrollPane(textArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true, tableScroller, textAreaScroller);

        getContentPane().add(splitPane);

        pack();

        setLocationRelativeTo(null);

        setVisible(true);
    }

    /**
     * Method createTableModel
     *
     * @return Returns a new {@link DefaultTableModel}
     */
    private DefaultTableModel createTableModel () {
        return new DefaultTableModel(new String[] { "To", "Subject" }, 0);
    }

    /**
     * Method onClearAll
     *
     */
    protected void onClearAll () {
        tableModel = createTableModel ();
        table.setModel (tableModel);
        
        messages.clear ();
    }

    /**
     * Method onTableSelectionChanged
     * 
     * @param e
     */
    protected void onTableSelectionChanged(ListSelectionEvent e) {
        int rowN = table.getSelectedRow();

        if (rowN >= 0) {
            Message message = messages.get(rowN);

            textArea.setText(message.message);
        } else {
            textArea.setText ("");
        }
    }

    /**
     * Method onClose
     *
     */
    private void onClose () {
        dispose ();
    }

    /**
     * Method main
     * 
     * @param args
     */
    public static void main(String[] args) {
        PseudoEmailFrame.addMessage("VP Sales", "Check this out!",
                "Joe bought 1,000,000");
    }
}

