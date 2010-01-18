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
 * MonitorMainFrame.java
 * Created: 15-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.openquark.samples.bam.MonitorApp;
import org.openquark.samples.bam.model.MonitorDocument;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.util.ui.ExtensionFileFilter;
import org.openquark.util.ui.SmartCheckBoxMenuItem;


/**
 * 
 *  
 */
public class MonitorMainFrame extends JFrame {

    private static final long serialVersionUID = -1506327581746672170L;

    private final MonitorApp app;

    private boolean logMessages = false;

    public MonitorMainFrame (MonitorApp app) {
        super ("Business Activity Monitor");

        this.app = app;

        setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
        
        ImageIcon frameIcon = loadIcon("BAMIcon.png");
        
        setIconImage(frameIcon.getImage());

        MonitorDocumentEditor editor = new MonitorDocumentEditor (app);

        LogPanel logPanel = new LogPanel ();

        app.addLogHandler (logPanel.getLogHandler ());

        makeMenuBar (editor, logPanel);

        makeContents (editor, logPanel);

        pack ();

        setLocationRelativeTo (null);
    }

    /**
     * @see javax.swing.JFrame#processWindowEvent(java.awt.event.WindowEvent)
     */
    @Override
    protected void processWindowEvent (WindowEvent e) {
        super.processWindowEvent (e);

        if (e.getID () == WindowEvent.WINDOW_CLOSING) {
            onExit ();
        }
    }

    /**
     * Method makeMenuBar
     * 
     *  
     */
    private void makeMenuBar (MonitorDocumentEditor editor, LogPanel logPanel) {
        JMenuBar menuBar = new JMenuBar ();

        JMenu fileMenu = new JMenu ("File");

        fileMenu.add (new AbstractAction ("New") {

            private static final long serialVersionUID = -7231496657495666085L;

            public void actionPerformed (ActionEvent e) {
                onFileNew ();
            }
        });

        fileMenu.add (new AbstractAction ("Open...") {

            private static final long serialVersionUID = 8513791034284527200L;

            public void actionPerformed (ActionEvent e) {
                onFileOpen ();
            }
        });

        fileMenu.add (new AbstractAction ("Save") {

            private static final long serialVersionUID = -8298609793391808996L;

            public void actionPerformed (ActionEvent e) {
                onFileSave ();
            }
        });

        fileMenu.add (new AbstractAction ("Save As...") {

            private static final long serialVersionUID = -8349400151145739033L;

            public void actionPerformed (ActionEvent e) {
                onFileSaveAs ();
            }
        });

        fileMenu.addSeparator ();

        fileMenu.add (new AbstractAction ("Exit") {

            private static final long serialVersionUID = -2934786890178912553L;

            public void actionPerformed (ActionEvent e) {
                onExit ();
            }
        });

        menuBar.add (fileMenu);

        JMenu editMenu = new JMenu ("Edit");

        editMenu.add (editor.getAddMessageSourceAction ());
        editMenu.add (editor.getRemoveMessageSourceAction ());
               
        editMenu.addSeparator ();

        editMenu.add (editor.getAddTriggerAction ());
        editMenu.add (editor.getEditTriggerAction ());
        editMenu.add (editor.getRemoveTriggerAction ());

        editMenu.addSeparator ();

        editMenu.add (editor.getAddActionAction ());
        editMenu.add (editor.getEditActionAction ());
        editMenu.add (editor.getRemoveActionAction ());

        menuBar.add (editMenu);

        JMenu toolsMenu = new JMenu ("Tools");

        toolsMenu.add (new SmartCheckBoxMenuItem (app.getEnableMessageLoggingAction ()));

        toolsMenu.add (logPanel.getClearLogAction ());

        toolsMenu.addSeparator ();

        toolsMenu.add (new AbstractAction ("Manage Gems") {

            private static final long serialVersionUID = 2688897007995190167L;

            public void actionPerformed (ActionEvent e) {
                onBlessGems ();
            }
        });

        menuBar.add (toolsMenu);

        JMenu testMenu = new JMenu ("Test");

        testMenu.add (new AbstractAction ("Make test document") {

            private static final long serialVersionUID = 6948959299447701086L;

            public void actionPerformed (ActionEvent e) {
                makeTestDocument ();
            }
        });

        menuBar.add (testMenu);

        JMenu helpMenu = new JMenu ("Help");

        helpMenu.add (new AbstractAction ("About") {

            private static final long serialVersionUID = -4668449109712768769L;

            public void actionPerformed (ActionEvent e) {
                JOptionPane.showMessageDialog (MonitorMainFrame.this,
                        "Business Activity Monitor\nversion 0.1\nFrom the Research Group", "About Monitor",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        menuBar.add (helpMenu);

        setJMenuBar (menuBar);
    }

    /**
     * Method onBlessGems
     * 
     * 
     */
    protected void onBlessGems () {
        GemBlessingDialog dialog = new GemBlessingDialog (this, app);
        
        dialog.doModal();
        
        app.getTriggerGemManager().resetAvailableGems();
        app.getActionGemManager().resetAvailableGems();
    }

    /**
     * Method onFileNew
     * 
     *  
     */
    protected void onFileNew () {
        if (app.isRunning ()) {
            JOptionPane.showMessageDialog (this, "Please stop the monitor before creating a new document.", "BAM Sample",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        app.setDocument (new MonitorDocument ());
    }

    /**
     * Method onFileOpen
     * 
     *  
     */
    protected void onFileOpen () {
        if (app.isRunning ()) {
            JOptionPane.showMessageDialog (this, "Please stop the monitor before opening a document.", "BAM Sample",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        JFileChooser fileChooser = new JFileChooser ();

        fileChooser.addChoosableFileFilter (new ExtensionFileFilter ("bam", "Monitor document"));

        if (fileChooser.showOpenDialog (this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile ();

            MonitorDocument document = MonitorDocument.Load (file);

            if (document != null) {
                app.setDocument (document);
            }
        }
    }

    /**
     * Method onFileSave
     * 
     *  
     */
    protected void onFileSave () {
        MonitorDocument document = app.getDocument ();

        String pathname = document.getPathname ();

        if (pathname == null || pathname.length () == 0) {
            onFileSaveAs ();
        } else {
            File file = new File (pathname);

            document.save (file);
        }
    }

    /**
     * Method onFileSaveAs
     * 
     *  
     */
    protected void onFileSaveAs () {
        MonitorDocument document = app.getDocument ();

        String pathname = document.getPathname ();

        JFileChooser fileChooser = new JFileChooser (pathname);

        fileChooser.addChoosableFileFilter (new ExtensionFileFilter ("bam", "Monitor document"));

        if (fileChooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile ();

            document.save (file);
        }
    }

    /**
     * Method onLogMessages
     * 
     *  
     */
    protected void onLogMessages () {
        logMessages = !logMessages;
    }

    /**
     * Method makeTestDocument
     * 
     *  
     */
    protected void makeTestDocument () {
        MonitorDocument newDocument = new MonitorDocument ();

        newDocument.addJobDescription (MonitorJobDescription.makeTestInstance ());
        newDocument.addJobDescription (MonitorJobDescription.makeAnotherTestInstance ());

        app.setDocument (newDocument);
    }

    /**
     * Method makeContents
     * 
     *  
     */
    private void makeContents (MonitorDocumentEditor editor, LogPanel logPanel) {
        getContentPane ().setLayout (new BorderLayout ());

        JToolBar toolBar = new JToolBar ("Toolbar");

        addToolbarButton (toolBar, app.getRunAction (), "play.gif");
        addToolbarButton (toolBar, app.getStopAction (), "stop.gif");

        JPanel mainPanel = new JPanel (new BorderLayout ());
        
        JTabbedPane tabbedPane = new JTabbedPane (JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.addTab ("Editor", null, editor, "Use this tab to edit the BAM jobs");
        tabbedPane.addTab ("Log", null, logPanel, "Use this tab to view the log of actions");
        
        getContentPane().add (new MonitorSidePanel (), BorderLayout.WEST);
        
        mainPanel.add (toolBar, BorderLayout.NORTH);
        mainPanel.add (tabbedPane, BorderLayout.CENTER);
        
        getContentPane ().add (mainPanel, BorderLayout.CENTER);
    }

    private void addToolbarButton (JToolBar toolBar, Action action, String iconFileName) {
        JButton button = toolBar.add (action);
        
        Icon icon = loadIcon(iconFileName);
        
        if (icon != null) {
            button.setText ("");
            button.setIcon (icon);
        }
    }
    
    private ImageIcon loadIcon (String iconFileName) {
        String iconFilePath = "/Resources/" + iconFileName; //$NON-NLS-1$ 

        URL url = getClass().getResource (iconFilePath);

        if (url != null) {
            return new ImageIcon (url);
        } else {
            return null;
        }
    }
    
    /**
     * Method onExit
     * 
     *  
     */
    private void onExit () {
        if (app.isRunning ()) {
            JOptionPane.showMessageDialog (this, "You must stop the application before exiting",
                    "Business Activity Monitor", JOptionPane.WARNING_MESSAGE);

            return;
        }

        System.exit (0);
    }

}