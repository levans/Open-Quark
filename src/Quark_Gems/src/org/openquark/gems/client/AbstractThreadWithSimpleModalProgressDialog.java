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
 * AbstractThreadWithSimpleModalProgressDialog.java
 * Creation date: (Apr 19, 2006)
 * By: James Wright
 */
package org.openquark.gems.client;

import java.awt.Component;

import javax.swing.SwingUtilities;

/**
 * This is a convenience class for driving a modal progress dialog from a non-AWT thread.
 * It includes helper methods like showMonitor(), setStatus(), incrementProgress(), that will call
 * the corresponding methods on the dialogue using invokeLater (to ensure that the UI calls happen
 * on the AWT thread).
 * 
 * Usage:
 * 
 * Thread myThread = new AbstractThreadWithSimpleModalProgressDialog("thread description", parent, "title", 0, 2) {
 *     public void run() {
 *         showMonitor();
 *         setStatus("step 1");
 *         ... do some work ...
 *         incrementProgress();
 *         setStatus("step 2");
 *         ... do some more ...
 *         incrementProgress();
 *         setStatus("step 3");
 *         ... do some more ...
 *         closeMonitor();
 *     }
 * };
 * 
 * @author James Wright
 */
abstract class AbstractThreadWithSimpleModalProgressDialog extends Thread {
    
    /** Modal dialog that shows a progress bar */
    private final ModalProgressMonitor monitor;
    
    /**
     * @param threadName Description of the thread
     * @param parent Parent component for the progress dialog
     * @param title Title of the progress dialog (should be already localized)
     * @param min Minimum value for the progress bar
     * @param max Maximum value for the progress bar
     */
    AbstractThreadWithSimpleModalProgressDialog(String threadName, Component parent, String title, int min, int max) {
        super(threadName);
        monitor = new ModalProgressMonitor(parent, title, 1, min, max);
    }
    
    // Convenience methods for managing the monitor

    /**
     * Call monitor.incrementProgress on the AWT thread
     */
    void incrementProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                monitor.incrementProgress();
            }
        });
    }
    
    /**
     * Call monitor.showDialog on the AWT thread
     */
    void showMonitor() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                monitor.showDialog();
            }
        });
    }

    /**
     * Call monitor.setStatus on the AWT thread
     */
    void setStatus(final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                monitor.setMessage(0, status);
            }
        });
    }
    
    /**
     * Call monitor.done on the AWT thread
     */
    void closeMonitor() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                monitor.done();
            }
        });
    }
}

