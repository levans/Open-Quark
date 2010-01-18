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
 * MonitorApp.java
 * Created: 15-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.samples.bam.MessageSource.StatusListener;
import org.openquark.samples.bam.model.MonitorDocument;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.ui.MonitorMainFrame;
import org.openquark.samples.bam.ui.MonitorSplashScreen;
import org.openquark.util.ui.AbstractSelectableAction;


/**
 * This class contains the top level logic for the BAM sample.
 *  
 */
public class MonitorApp {
    
    /**
     * This class is used to log messages that are received
     */
    private class MonitorMessageLoger implements MessageListener {

        /**
         * {@inheritDoc}
         */
        public void messageReceived (Message message) {
            logMessage(message);
        }
        
    }

    public static final String DOCUMENT_PROPERTY_NAME = "Document";

    public static final String STATE_PROPERTY_NAME = "State";

    public static final int IDLE = 0;
    public static final int RUNNING = 1;
    
    private int appState = IDLE;

    public static final ModuleName TARGET_MODULE = ModuleName.make("Cal.Samples.BusinessActivityMonitor.RunModule");

    private static final String WORKSPACE_FILE_PROPERTY = "org.openquark.samples.bam.monitor.workspace";

    private static final String DEFAULT_WORKSPACE_FILE = "bam.default.cws";
    
    /** Whether or not to use a nullary workspace. */
    private static final boolean USE_NULLARY_WORKSPACE = true;
    
    /** The default workspace client id. */
    public static final String DEFAULT_WORKSPACE_CLIENT_ID = USE_NULLARY_WORKSPACE ? null : "monitor";

    private MonitorDocument document;

    private MonitorMainFrame mainFrame;

 

    private BasicCALServices calServices = null;

    private TriggerGemManager triggerGemManager = null;

    private ActionGemManager actionGemManager = null;

    private AbstractAction runAction = null;

    private AbstractAction stopAction = null;
    
    private AbstractSelectableAction enableMessageLoggingAction = null;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport (this);

    private final List<MonitorJob> jobs = new ArrayList<MonitorJob> (); // of MessageJobs
    
    private final MonitorMessageLoger messageLogger = new MonitorMessageLoger ();

    private final Logger logger = Logger.getLogger("BAM");

    private boolean loggingMessages = true;
    
    static private MonitorApp app= null;

    static public MonitorApp getInstance() {
        if (app==null) {
            app = new MonitorApp ();
        }
        return app;
    }
    
    
    public static void main (String[] args) {
        MonitorApp app = getInstance();

        app.startMonitor ();
    }

    private MonitorApp () {
        document = new MonitorDocument ();
    }

    public void addPropertyChangeListener (PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener (listener);
    }

    void removePropertyChangeListener (PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener (listener);
    }

    private void startMonitor () {
        try {
            UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
        } catch (ClassNotFoundException e) {
            e.printStackTrace ();
        } catch (InstantiationException e) {
            e.printStackTrace ();
        } catch (IllegalAccessException e) {
            e.printStackTrace ();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace ();
        }

        mainFrame = new MonitorMainFrame (this);

        MonitorSplashScreen splashScreen = new MonitorSplashScreen (mainFrame);

        mainFrame.setEnabled (false);

        splashScreen.setVisible (true);

        mainFrame.setVisible (true);

        getCalServices ();

        mainFrame.setEnabled (true);

        splashScreen.dispose ();
    }

    /**
     * Method getDocument
     * 
     * @return Returns the MonitorDocument
     */
    public MonitorDocument getDocument () {
        return document;
    }

    /**
     * Method isRunning
     * 
     * @return Returns true iff any jobs are running
     */
    public boolean isRunning () {
        return appState == RUNNING;
    }

    /**
     * @return Returns true iff logging messages is enabled
     */
    boolean isLoggingMessages () {
        return loggingMessages;
    }
    /**
     * @param loggingMessages Sets whether messages should be logged
     */
    void setLoggingMessages (boolean loggingMessages) {
        this.loggingMessages = loggingMessages;
        
        getEnableMessageLoggingAction().setSelected(loggingMessages);
    }
    
    public Action getRunAction () {
        if (runAction == null) {
            runAction = new AbstractAction ("Run") {

                private static final long serialVersionUID = 2750702965541172917L;

                public void actionPerformed (ActionEvent e) {
                    runApp ();
                }
            };

            runAction.setEnabled (!isRunning ());
        }

        return runAction;
    }

    public Action getStopAction () {
        if (stopAction == null) {
            stopAction = new AbstractAction ("Stop") {

                private static final long serialVersionUID = 4182911199644551389L;

                /**
                 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                 */
                public void actionPerformed (ActionEvent e) {
                    stopApp ();
                }
            };

            stopAction.setEnabled (isRunning ());
        }

        return stopAction;
    }
    
    /**
     * Method getEnableMessageLoggingAction
     * 
     * @return Returns an AbstractSelectableAction that controls logging of messages
     */
    public AbstractSelectableAction getEnableMessageLoggingAction () {
        if (enableMessageLoggingAction == null) {
            enableMessageLoggingAction = new AbstractSelectableAction ("Log Messages") {
                
                private static final long serialVersionUID = 8174005437618776063L;

                /**
                 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                 */
                public void actionPerformed (ActionEvent e) {
                    setLoggingMessages(!isLoggingMessages());
                }
            };
            
            enableMessageLoggingAction.setSelected(isLoggingMessages());
        }
        
        return enableMessageLoggingAction;
    }
    
    /**
     * Method runApp
     * 
     * Prepare and run all jobs
     */
    private void runApp () {
        
        //ensure a job has been defined
        if (document.getJobDescriptionCount () == 0) {
            JOptionPane.showMessageDialog (mainFrame,
                    "You must add a message source before running.", "BAM Sample",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        // ensure all jobs are in a ready to run state
        for (int jobN = 0; jobN < document.getJobDescriptionCount (); ++jobN) {
            MonitorJobDescription jobDescription = document.getNthJobDescription (jobN);

            if (!readyToRun (jobDescription)) {
                JOptionPane.showMessageDialog (mainFrame, "The message source <"
                        + jobDescription.getMessageSourceDescription ().getName ()
                        + "> must have triggers and actions set.", "BAM Sample",
                        JOptionPane.WARNING_MESSAGE);

                return;
            }
        }
        
        // compile and start each job
        try {
            BasicCALServices calServices = getCalServices();
            for (int jobN = 0; jobN < document.getJobDescriptionCount (); ++jobN) {
                MonitorJobDescription jobDescription = document.getNthJobDescription (jobN);

                GemGraphGenerator generator = new GemGraphGenerator (calServices, jobDescription);

                //create each new job in separate modules.
                ModuleName jobModuleName = ModuleName.make(jobDescription.getJobId().toUpperCase());

                EntryPointSpec entry = calServices.addNewModuleWithFunction(jobModuleName, generator.getCalSource());

                MessageSource messageSource = MessageSourceFactory.createMessageSource (jobDescription);
                MonitorJob job = new MonitorJob (jobDescription, messageSource, entry);

                messageSource.addStatusListener (makeStatusListener (job));
                messageSource.addMessageListener(messageLogger);
                messageSource.start();

                job.start();
                jobs.add (job);
            }
            setAppState (RUNNING);
        } catch (GemCompilationException ex) {
            stopApp ();
            JOptionPane.showMessageDialog (mainFrame,
                "Error compiling job: " + ex.getMessage(), "BAM Sample",
                JOptionPane.WARNING_MESSAGE);
        }

    }

    
    /**
     * Method readyToRun
     * 
     * @param jobDescription
     * @return Returns true iff the job description has everything needed to run
     */
    private boolean readyToRun (MonitorJobDescription jobDescription) {
        return !isEmptyList (jobDescription.getTriggerDescriptions ())
                && !isEmptyList (jobDescription.getActionDescriptions ());
    }

    /**
     * Method emptyList
     *  
     */
    private boolean isEmptyList (List<?> list) {
        return list == null || list.size () == 0;
    }

    /**
     * Method makeStatusListener
     *  
     */
    private StatusListener makeStatusListener (final MonitorJob job) {
        return new StatusListener () {

            public void statusChanged (int newStatus) {
                String jobName = job.getJobDescription ().getMessageSourceDescription ()
                        .getName ();
                
                if (newStatus == MessageSource.STATUS_RUNNING) {
                    log ("Job started: " + jobName);
                } else if (newStatus == MessageSource.STATUS_IDLE) {
                    removeJob (job);
                    
                    log ("Job stopped: " + jobName);
                }
            }
        };
    }

    /**
     * Method removeJob
     *  
     */
    protected void removeJob (MonitorJob job) {
        jobs.remove (job);

        if (jobs.isEmpty ()) {
            setAppState (IDLE);
        }
    }

    /**
     * Method stopApp
     * 
     *  
     */
    private void stopApp () {
        List<MonitorJob> tempJobList = new ArrayList<MonitorJob> (jobs);

        for (final MonitorJob job : tempJobList) {
            job.getMessageSource().stop(); //stop the message source - the job will continue to process buffered messages and then finish
            jobs.remove (job);
        }
    }

    /**
     * Method setAppState
     *  
     */
    private void setAppState (int newState) {
        if (appState != newState) {
            getRunAction ().setEnabled (newState == IDLE);
            getStopAction ().setEnabled (newState == RUNNING);

            int oldState = appState;

            appState = newState;

            propertyChangeSupport.firePropertyChange (STATE_PROPERTY_NAME, oldState, newState);
        }
    }

    /**
     * Method getcalServices
     * 
     * @return Returns the BasicCALServices for the app
     */
    public BasicCALServices getCalServices () {
        if (calServices == null) {
            calServices = BasicCALServices.make (WORKSPACE_FILE_PROPERTY, DEFAULT_WORKSPACE_FILE, DEFAULT_WORKSPACE_CLIENT_ID);
            
            CompilerMessageLogger logger = new MessageLogger();
            calServices.compileWorkspace(null, logger);
            
            List<CompilerMessage> compilerMessages = logger.getCompilerMessages(CompilerMessage.Severity.WARNING);
            if (!compilerMessages.isEmpty()) {
                int size = compilerMessages.size();
                for (int i = 0; i < size; i++) {
                    CompilerMessage message = compilerMessages.get(i);
                    System.err.println(message.toString());
                }
            }
        }

        return calServices;
    }

    /**
     * Method getTriggerGemManager
     * 
     * @return Returns the TriggerGemManager for the app
     */
    public TriggerGemManager getTriggerGemManager () {
        if (triggerGemManager == null) {
            triggerGemManager = new TriggerGemManager (getCalServices ());
        }

        return triggerGemManager;
    }

    /**
     * Method getActionGemManager
     * 
     * @return Returns the ActionGemManager for the app
     */
    public ActionGemManager getActionGemManager () {
        if (actionGemManager == null) {
            actionGemManager = new ActionGemManager (getCalServices ());
        }

        return actionGemManager;
    }

    /**
     * Method setDocument
     *  
     */
    public void setDocument (MonitorDocument newDocument) {
        assert (appState == IDLE);

        if (appState != IDLE) {
            throw new IllegalStateException (
                    "Attempt to set the document while the app is running");
        }

        MonitorDocument oldDocument = document;

        document = newDocument;

        propertyChangeSupport.firePropertyChange (DOCUMENT_PROPERTY_NAME, oldDocument, newDocument);
    }

    //
    // Logging
    //

    /**
     * Method addLogHandler
     * 
     * @param handler
     */
    public void addLogHandler (Handler handler) {
        logger.addHandler (handler);
    }

    /**
     * Method removeLogHandler
     * 
     * @param handler
     */
    void removeLogHandler (Handler handler) {
        logger.removeHandler (handler);
    }

    /**
     * Method enableLogging
     * 
     * @param enable
     */
    void enableLogging (boolean enable) {
        if (enable) {
            logger.setLevel (Level.WARNING);
        } else {
            logger.setLevel (Level.OFF);
        }
    }

    /**
     * Method isLoggingEnabled
     * 
     * @return Returns true iff logging is enabled
     */
    boolean isLoggingEnabled () {
        return logger.getLevel ().intValue () <= Level.WARNING.intValue ();
    }

    /**
     * Method log
     * 
     * @param message
     */
    void log (String message) {
        logger.info (message);
    }

    /**
     * Method logMessage
     * 
     * @param message
     */
    void logMessage (Message message) {
        if (loggingMessages) {
            log ("Message received: " + message);
        }
    }
    
}
