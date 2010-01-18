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
 * ModalProgressMonitor.java
 * Creation date: Jan 20, 2006.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * This class implements a modal dialog for monitoring the progress of some operation. This class contrasts
 * with {@link javax.swing.ProgressMonitor} in a number of ways:
 * 
 * <ul>
 * <li>This class implements a modal dialog which blocks the UI, unless it is either closed (when the operation
 * finishes) or is dismissed. The standard Swing ProgressMonitor implements a non-modal dialog which pops up only
 * when the operation takes longer than a certain threshold amount of time to finish. Having a modal dialog
 * is handy if the length operation cannot occur in parallel with other user operations.
 * 
 * <li>The dialog is not closed automatically when the progress reaches the maximum value.
 * </ul>
 *
 * @author Joseph Wong
 */
class ModalProgressMonitor {
    
    /**
     * The JLabels for the messages above the progress bar.
     */
    private final JLabel[] messageLabels;
    /**
     * The progress bar.
     */
    private final JProgressBar progressBar;
    /**
     * The label for displaying the progress as a percentage value.
     */
    private final JLabel percentageDone;
    /**
     * The option to be used in the JOptionPane for the cancel button.
     */
    private final String cancelOption;
    /**
     * The JOptionPane which encapsulates the message labels and the progress bar.
     */
    private final JOptionPane optionPane;
    /**
     * The actual dialog that hosts the option pane.
     */
    private final JDialog dialog;
    /**
     * The flag which, when set to true, indicates that the operation is done, that the dialog should be closed, and
     * that if showDialog() is called afterwards, that the dialog not be shown.
     */
    private boolean isDone;
    
    /**
     * Constructs a progress monitor.
     * @param parent the parent component for the dialog box.
     * @param title the title of the dialog box, or null for an empty title.
     * @param nMessages the number of messages that will be shown above the progress bar.
     * @param min the lower bound of the possible range of progress values.
     * @param max the upper bound of the possible range of progress values.
     */
    public ModalProgressMonitor(Component parent, String title, int nMessages, int min, int max) {
        
        messageLabels = new JLabel[nMessages];
        for (int i = 0; i < nMessages; i++) {
            messageLabels[i] = new JLabel("XXX"); // put a string in the labels so that the dialog packing is done properly
        }
        
        progressBar = new JProgressBar(min, max);
        progressBar.setValue(min);
        
        percentageDone = new JLabel();
        percentageDone.setMinimumSize(new Dimension(50, 0));
        percentageDone.setPreferredSize(new Dimension(50, 0));
        percentageDone.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(percentageDone, BorderLayout.EAST);
        
        cancelOption = GemCutterMessages.getString("CancelButton");
        
        Object[] paneMessages = new Object[nMessages + 1];
        System.arraycopy(messageLabels, 0, paneMessages, 0, nMessages);
        paneMessages[nMessages] = progressPanel;
        
        optionPane = new JOptionPane(paneMessages,
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[] {cancelOption},
            null);
        
        dialog = optionPane.createDialog(parent, title);
        dialog.setModal(true);
        
        // reset the message strings after the dialog is done packing
        for (int i = 0; i < nMessages; i++) {
            messageLabels[i].setText(" ");
        }
        
        isDone = false;
    }
    
    /**
     * Sets the preferred width of the dialog.
     * @param width the preferred width.
     */
    public void setPreferredWidth(int width) {
        Dimension dimension = new Dimension(width, optionPane.getHeight());
        optionPane.setMinimumSize(dimension);
        optionPane.setPreferredSize(dimension);
        dialog.pack();
    }
    
    /**
     * Specifies the minimum value - the lower bound of the possible range of progress values.
     * @param min the minimum value. 
     */
    public void setMinimum(int min) {
        progressBar.setMinimum(min);
    }
    
    /**
     * Returns the minimum value - the lower bound of the possible range of progress values.
     * @return the minimum value.
     */
    public int getMinimum() {
        return progressBar.getMinimum();
    }
    
    /**
     * Specifies the maximum value - the upper bound of the possible range of progress values.
     * @param max the maximum value.
     */
    public void setMaximum(int max) {
        progressBar.setMaximum(max);
    }
    
    /**
     * Returns the maximum value - the upper bound of the possible range of progress values.
     * @return the maximum value.
     */
    public int getMaximum() {
        return progressBar.getMaximum();
    }
    
    /**
     * Sets the title of the progress monitor dialog.
     * @param title the title string, or null for an empty title.
     */
    public void setTitle(String title) {
        dialog.setTitle(title);
    }
    
    /**
     * Specifies the message to be displayed on a particular line above the progress bar.
     * @param index the position of the message label. 
     * @param message the message to be displayed.
     */
    public void setMessage(int index, String message) {
        messageLabels[index].setText(message);
    }
    
    /**
     * Updates the progress of the operation being monitored.
     * @param progress the current progress value, between the minimum and maximum specified for this monitor.
     */
    public void setProgress(int progress) {
        progressBar.setValue(progress);
        percentageDone.setText(GemCutterMessages.getString("ProgressPercentage", new Double(progressBar.getPercentComplete())));
    }
    
    /**
     * Increments the progress of the operation by 1.
     * @see #setProgress
     */
    public void incrementProgress() {
        setProgress(progressBar.getValue() + 1);
    }
    
    /**
     * Returns the progress of the operation as registered with the monitor.
     * @return the current progress value.
     */
    public int getProgress() {
        return progressBar.getValue();
    }
    
    /**
     * Shows the progress monitor dialog in a modal fashion. If done() has already been called on this instance,
     * then the dialog with not be shown.
     */
    public synchronized void showDialog() {
        if (!isDone) {
            dialog.setVisible(true);
        }
    }
    
    /**
     * Signals the monitor that the operation is done, and that the dialog, if displayed, should be closed.
     */
    public synchronized void done() {
        isDone = true;
        dialog.dispose();
    }
    
    /**
     * @return true if the user has canceled the operation via the cancel button in the dialog.
     */
    public boolean isCanceled() {
        Object userChoice = optionPane.getValue();
        return userChoice == cancelOption;
    }
}
