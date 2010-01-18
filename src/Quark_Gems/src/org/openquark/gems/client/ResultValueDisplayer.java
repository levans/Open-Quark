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
 * TargetResultDisplayer.java
 * Creation date: Dec 17, 2003
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.openquark.gems.client.valueentry.ValueEditor;


/**
 * A class to handle displaying result values.
 * @author Edward Lam
 */
public class ResultValueDisplayer extends InternalFrameAdapter implements ComponentListener {
    /** The frame in which the result will be displayed. */
    private final JFrame frame;

    /** List of JInternalFrames open and not moved or resized */
    private final List<JInternalFrame> unmodifiedFrameList;

    /** Set of all open JInternalFrames */
    private final Set<JInternalFrame> openFrameSet;

    /** The index of the first free spot where we can display the next free frame */
    private int firstFreeIndex;
    
    /** The target bounds the last time we showed a result */
    private Rectangle oldTargetBounds;

    /** The index of the next internal frame to be displayed */
    private int resultIndex = 1;

    /**
     * Constructor for an ResultValueDisplayer.
     * @param frame  the parent frame in which the result value will be displayed
     */
    public ResultValueDisplayer(JFrame frame) {
        this.frame = frame;
        unmodifiedFrameList = new ArrayList<JInternalFrame>();
        openFrameSet = new HashSet<JInternalFrame>();
        firstFreeIndex = 0;
    }

    /**
     * Close all open result frames.
     */
    public void closeAllResultFrames() {
        // Note: we can't close the frame while iterating over openFrameSet, 
        // as closing the frame modifies openFrameSet!
        Set<JInternalFrame> openFrames = new HashSet<JInternalFrame>(openFrameSet);
        for (final JInternalFrame internalFrame : openFrames) {
            try {
                internalFrame.setClosed(true);
            } catch (java.beans.PropertyVetoException pve) {
                // this should never happen..
                throw new Error("Programming error: can't close result windows");
            }
        }
    }
    
    /**
     * Create a scroll pane to be used for displaying error messages.
     */
    private JScrollPane getJScrollPane1() {
        JScrollPane ivjJScrollPane1 = null;
        try {
            ivjJScrollPane1 = new JScrollPane();
            ivjJScrollPane1.setName("JScrollPane1");
            //getJScrollPane1().setViewportView(getTextArea());
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        return ivjJScrollPane1;
    }       
    
    /**
     * Create a text area to be used for displaying error messages.
     */
    private JTextArea getTextArea() {
        JTextArea ivjTextArea = null;
        try {
            ivjTextArea = new JTextArea();
            ivjTextArea.setName("TextArea");
            ivjTextArea.setLineWrap(true);
            ivjTextArea.setWrapStyleWord(true);
            ivjTextArea.setBounds(0, 0, 100, 20);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjTextArea;
    }               

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private static void handleException(Throwable exception) {
    
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Generate and display a Frame containing the result.
     * Prepares the internal frame for display, and calls the other method to actually display it.
     * @param resultEditor the editor with the result to display. null if we only want to display an error message
     * @param messageTitle the title of the message to display
     * @param errorMessage null if there is no error message.
     * @return JInternalFrame the JInternalFrame that was displayed
     */
    public JInternalFrame showResultFrame(ValueEditor resultEditor, String messageTitle, String errorMessage) {
        // Create the internal frame to hold resultVEP.
        JInternalFrame internalFrame = new JInternalFrame(messageTitle, true, true, false, false);
        internalFrame.getContentPane().setLayout(new BorderLayout ());
        
        JScrollPane sp = null;
        JTextArea ta = null;
        
        if (errorMessage != null) {
            StringBuilder errorBuffer = new StringBuilder ("Error: ");                   
            errorBuffer.append (errorMessage);
            sp = getJScrollPane1 ();
            if (sp != null) {
                ta = getTextArea ();
                if (ta != null) {
                    ta.setRows (4);
                    ta.setColumns (40);
                    sp.setViewportView (ta);
                    ta.setText (errorBuffer.toString ());
                }
                internalFrame.getContentPane().add(sp, BorderLayout.CENTER);
            }
        }

        // Make this frame wide enough to at least display its title
        // in the title bar.
        if (resultEditor != null) {                        
            internalFrame.getContentPane().add(resultEditor, BorderLayout.SOUTH);
        }
                    
        resultIndex++;
        internalFrame.pack();   

        // Make sure the frame is wide enough to display its title.
        // If you add additional titlebar icons to the frame make sure to change this.
        FontMetrics metrics = frame.getFontMetrics(UIManager.getDefaults().getFont("InternalFrame.titleFont"));
        int minWidth = metrics.stringWidth(messageTitle);
        minWidth += frame.getIconImage().getWidth(null);
        minWidth += UIManager.getDefaults().getIcon("InternalFrame.closeIcon").getIconWidth();
        minWidth += internalFrame.getInsets().left + internalFrame.getInsets().right;
        minWidth += 20; // a totally exact size looks weird
        
        Dimension size = internalFrame.getPreferredSize();
        if (size.width < minWidth) {
            size.width = minWidth;
            internalFrame.setPreferredSize(size);
            internalFrame.setSize(size);
        }
        
        // Fix the height of the frame but make the width resizable up to the
        // maximum width of the editor this frame contains.
        Dimension prefSize = internalFrame.getPreferredSize();
        internalFrame.setMinimumSize(prefSize);
        if (resultEditor != null) {
            int maxWidth = resultEditor.getMaximumSize().width + internalFrame.getInsets().left + internalFrame.getInsets().right;
            internalFrame.setMaximumSize(new Dimension(maxWidth, prefSize.height));
        } else {
            internalFrame.setMaximumSize(new Dimension(65536, prefSize.height));
        }
        
        // Give the result internal frame the gemcutter icon.
        internalFrame.setFrameIcon(new ImageIcon(ResultValueDisplayer.class.getResource("/Resources/gemcutter_16.gif")));

        // defer frame display to another method
        showResultFrame(internalFrame);

        return internalFrame;
    }

    /**
     * Display the Frame containing the result.
     * We currently display these in the layered pane.
     * @param internalFrame JInternalFrame the JInternalFrame to display
     */
    private void showResultFrame(JInternalFrame internalFrame) {
        // get the internal frame dimensions
        Dimension internalFrameSize = internalFrame.getSize();

        // get the target info
        Rectangle targetBounds = getTargetBounds();

        int targetRightEdge = targetBounds.x + targetBounds.width;
        int targetBottomEdge = targetBounds.y + targetBounds.height;

        // check against the old target bounds, update the oldTargetBounds
        if (oldTargetBounds != null && !oldTargetBounds.equals(targetBounds)) {
            // the target moved
            targetMoved();
        }
        oldTargetBounds = targetBounds;

        // the frame will be shown within the parent frame, thus we need to know its bounds
        int parentWidth  = frame.getLayeredPane().getWidth();
        int parentHeight = frame.getLayeredPane().getHeight();
        
        // the right edge of the target should line up with the right edge of the internal frame, unless
        //    that would put it or the left edge outside the gemcutter window.
        int newX = Math.min( Math.max(targetRightEdge - internalFrameSize.width, 0), parentWidth - internalFrameSize.width );
        
        // the top of the first panel is aligned with the bottom of the target, 
        //    others with the bottom of the previous panel.
        int newY = Math.min( Math.max(targetBottomEdge, 0), parentHeight - internalFrameSize.height );
        if (firstFreeIndex != 0) {
            // calculate to the bottom of the previous frame
            JInternalFrame previousFrame = unmodifiedFrameList.get(firstFreeIndex - 1);
            Rectangle previousBounds = previousFrame.getBounds();
            newY = previousBounds.y + previousBounds.height;

            // we have to check to see if it would go off the window
            int newBottom = newY + internalFrame.getBounds().height;
            //System.out.println ("internalFrame height = " + internalFrame.getBounds().height);
            
            Point windowPoint = SwingUtilities.convertPoint(frame.getLayeredPane(), newX, newBottom, frame);

            if (windowPoint.y > frame.getHeight()) {
                // goes off the window
                
                // place in the first position under the target (will overlap an already showing window)
                newY = targetBottomEdge;

                // pretend that we set the frame at the first index
                firstFreeIndex = 0;
            }
        }

        // place the frame and display it
        internalFrame.setLocation(newX, newY);
        frame.getLayeredPane().add(internalFrame, JLayeredPane.PALETTE_LAYER, 0);
        internalFrame.setVisible(true);

        //System.out.println ("final iframe size = " + internalFrame.getSize ());
        // add it to the set of open frames
        openFrameSet.add(internalFrame);

        // add it to the (unmodified frame) list
        int frameListSize = unmodifiedFrameList.size();
        if (firstFreeIndex < frameListSize) {
            unmodifiedFrameList.set(firstFreeIndex, internalFrame);
        } else {
            unmodifiedFrameList.add(internalFrame);
            frameListSize++;
        }

        // update the firstFreeIndex to point to the next free spot, or if overlapping just the next spot
        firstFreeIndex++;

        // listen for when they are moved, resized, or closed
        internalFrame.addInternalFrameListener(this);
        internalFrame.addComponentListener(this);

    }

    /**
     * Invoked when the component has been made invisible.
     * @param e ComponentEvent the related event
     */
    public void componentHidden(ComponentEvent e) {
        // do nothing
    }

    /**
     * Invoked when the component's position changes.
     * @param e ComponentEvent the related event
     */
    public void componentMoved(ComponentEvent e) {
//                System.out.println ("e.getsource.getsize = " + ((JInternalFrame)e.getSource()).getSize());
        // end management of this frame
        removeFromManagedFrameList((JInternalFrame)e.getSource());
    }

    /**
     * Invoked when the component's size changes.
     * @param e ComponentEvent the related event
     */
    public void componentResized(ComponentEvent e) {
        // end management of this frame
        removeFromManagedFrameList((JInternalFrame)e.getSource());
    }

    /**
     * Invoked when the component has been made visible.
     * @param e ComponentEvent the related event
     */
    public void componentShown(ComponentEvent e) {
        // do nothing
    }

    /**
     * Invoked when an internal frame has been closed.
     * @param e InternalFrameEvent the related event
     */
    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        JInternalFrame frameClosed = (JInternalFrame)e.getSource();

        // end management of this frame
        removeFromManagedFrameList(frameClosed);

        // remove from the set of open frames
        openFrameSet.remove(frameClosed);
    }

    /**
     * Remove the internalFrame from management by this manager
     * @param frameToRemove JInternalFrame the JInternalFrame to remove.
     */
    private void removeFromManagedFrameList(JInternalFrame frameToRemove) {

        // iterate through the frameList, finding the one to remove
        int indexRemoved = -1;
        int frameListSize = unmodifiedFrameList.size();
        for (int i = 0; i < frameListSize; i++) {
            JInternalFrame listFrame = unmodifiedFrameList.get(i);
            if (listFrame == frameToRemove) {
                // Found it.  Set to null and exit the loop
                unmodifiedFrameList.set(i, null);
                indexRemoved = i;
                break;
            }
        }

        // note that the frame may already have been removed from the list before we enter this method
        // (for instance if first resize, then close)
        if (indexRemoved > -1) {
            // We removed something.  A spot opens up there where we put the next frame.
            // update firstFreeIndex if we removed at a lower index
            firstFreeIndex = Math.min(firstFreeIndex, indexRemoved);
        }

        // we should trim down the list for null's at the end
        while (frameListSize > 0 && unmodifiedFrameList.get(frameListSize - 1) == null){
            unmodifiedFrameList.remove(frameListSize - 1);
            frameListSize--;
        }

    }

    /**
     * Notify this manager that the associated target has moved
     */
    private void targetMoved() {
        // for now we just reset the state of the manager
        unmodifiedFrameList.clear();
        firstFreeIndex = 0;
    }

    /**
     * Returns the set of open frames.
     * @return the set of open frames
     */
    protected Set<JInternalFrame> getOpenFrameSet() {
        return Collections.unmodifiableSet(openFrameSet);
    }

    /**
     * Returns the rectangle of the target near which the result value will be displayed.
     * The position of this rectangle will be relative to the frame.
     * @return the rectangle of the target near which the result value will be displayed
     */
    protected Rectangle getTargetBounds() {
        // By default, just return a rectangle of zero size at (0, 0).
        return new Rectangle();
    }
}
