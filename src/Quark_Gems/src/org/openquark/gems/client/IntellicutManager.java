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
 * IntellicutManager.java
 * Creation date: Oct 23, 2002.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.GemEntity;
import org.openquark.gems.client.AutoburnLogic.AutoburnInfo;
import org.openquark.gems.client.AutoburnLogic.AutoburnUnifyStatus;
import org.openquark.gems.client.DisplayedGem.DisplayedPart;
import org.openquark.gems.client.DisplayedGem.DisplayedPartConnectable;
import org.openquark.gems.client.DisplayedGem.DisplayedPartInput;
import org.openquark.gems.client.DisplayedGem.DisplayedPartOutput;
import org.openquark.gems.client.Gem.PartConnectable;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.Gem.PartOutput;
import org.openquark.gems.client.IntellicutListModel.FilterLevel;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;


/**
 * The IntellicutManager manages intellicut for the GemCutter and TableTop. 
 * @author Edward Lam
 */
public class IntellicutManager {

    /** The key for the intellicut popup enabled preference. */
    public static final String INTELLICUT_POPUP_ENABLED_PREF_KEY = "intellicutPopupEnabled";
    
    /** The key for the intellicut popup delay preference. */
    public static final String INTELLICUT_POPUP_DELAY_PREF_KEY = "intellicutPopupDelay";
    
    /** The key for the gem filtering level intellicut preference. */
    public static final String INTELLICUT_GEM_FILTER_LEVEL_PREF_KEY = "intellicutShowGemsFilterLevel";
    
    /** Default value for the intellicut popup enabled preference. */
    public static final boolean INTELLICUT_POPUP_ENABLED_DEFAULT = true;

    /** Default value for the intellicut popup delay preference. */
    public static final int INTELLICUT_POPUP_DELAY_DEFAULT = 3;
    
    /** Default value for the gem filter level intellicut preference. */
    public static final FilterLevel INTELLICUT_GEM_FILTER_LEVEL_DEFAULT = FilterLevel.SHOW_ALL;
    
    /** The X distance that a newly added gem will be away from the intellicutPart's location. */
    private static final int DROP_DISTANCE_X = 30;

    /** The GemCutter instance for which Intellicut is being managed. */
    private final GemCutter gemCutter;
    
    /** Indicates which Intellicut mode we're currently in. */
    private IntellicutMode intellicutMode;

    /** The Part on which intellicut is activated. */
    private DisplayedPartConnectable intellicutPart;

    /** The intellicut panel in use. If this is null then there is no panel. */
    private IntellicutPanel intellicutPanel;
    
    /** 
     * The timer that starts when we are within range of a potential intellicut part input.
     * If the timer fires, then we show the IntellicutPanel.
     */
    private Timer intellicutPanelShowTimer;

    /**
     * Intellicut mode enum pattern.
     * These denote what intellicut mode we're currently in.
     * @author Edward Lam
     */
    public static final class IntellicutMode {
        
        private final String typeString;

        private IntellicutMode(String s) {
            typeString = s;
        }
        
        @Override
        public String toString() {
            return typeString;
        }

        /** Not in intellicut mode. */
        public static final IntellicutMode NOTHING = new IntellicutMode("NOTHING");
        
        /** The intellicut part is an input. */
        public static final IntellicutMode PART_INPUT = new IntellicutMode("PART_INPUT");
        
        /** The intellicut part is an output. */
        public static final IntellicutMode PART_OUTPUT = new IntellicutMode("PART_OUTPUT");
    }
    
    /**
     * A class to encapsulate the information about an intellicut operation.
     * @author Frank Worsley
     */
    public static final class IntellicutInfo {
    
        /** Default intellicut list into for normal type closeness without autoburning. */
        public static final IntellicutInfo DEFAULT_INFO = new IntellicutInfo(AutoburnUnifyStatus.NOT_NECESSARY, -1, 0, false, 0);
        
        /** The type closeness if directly connecting to the intellicut part. */        
        private final int noBurnTypeCloseness;
        
        /** The type closeness if connecting via burning. */
        private final int burnTypeCloseness;
        
        /** The autoburn status. */
        private final AutoburnUnifyStatus autoburnStatus;
        
        /** The number of other gems that reference this gem in their body */
        private final int referenceFrequency;
        
        /** True if this candidate is the same type as the target AND neither the target
         * nor the candidate are polymorphic types.  
         * 
         * We want to treat gems that have the same type as being especially close, but we 
         * only want to do that for nonpolymorphic types, because we don't want to wind up 
         * in a situation where, for example, we rate unsafeCoerce (type: a->b) as a better 
         * match for the function input of map (type: a->b) than fst (type: (a,b) -> a), 
         * because in fact a function with a more concrete type is likely to be a better match 
         * than another function of polymorphic type.  
         */
        private final boolean sameNonpolymorphicType;
        
        /** Smallest reference frequency that is still in the top 20% */
        private int referenceFrequencyTopFifthThreshold;
        
        /** Gems that have a higher reference frequency than this and are the same
         * nonpolymorphic type as the target gem will be included in Best Gems even
         * if they don't match the usual criteria.
         */  
        private int sameNonPolymorphicTypeReferenceFrequencyThreshold;
        
        /**
         * Constructs a new IntellicutInfo object.
         * @param autoburnStatus the autoburn status
         * @param maxBurnTypeCloseness the maximum type closeness for burning
         * @param noBurnTypeCloseness the type closeness for directly connecting
         * @param sameNonpolymorphicType whether the candidate represented by this IntellicutInfo
         *                                object has the same type as the target and is not polymorphic
         * @param referenceFrequency Reference frequency of the candidate represented by this IntellicutInfo object
         */
        public IntellicutInfo(AutoburnUnifyStatus autoburnStatus, int maxBurnTypeCloseness, int noBurnTypeCloseness, boolean sameNonpolymorphicType, int referenceFrequency) {
            
            if (autoburnStatus == null) {
                throw new NullPointerException();
            }
            
            this.noBurnTypeCloseness = noBurnTypeCloseness;
            this.burnTypeCloseness = maxBurnTypeCloseness;
            this.autoburnStatus = autoburnStatus;
            this.sameNonpolymorphicType = sameNonpolymorphicType;
            this.referenceFrequency = referenceFrequency;
            referenceFrequencyTopFifthThreshold = 0;
            sameNonPolymorphicTypeReferenceFrequencyThreshold = 0;
        }
        
        public IntellicutInfo(AutoburnUnifyStatus autoburnStatus, int maxBurnTypeCloseness, int noBurnTypeCloseness) {
            this(autoburnStatus, maxBurnTypeCloseness, noBurnTypeCloseness, false, 0);
        }
    
        /**
         * @return the number of times other gems reference this gem in their body
         */
        public int getReferenceFrequency() {
            return referenceFrequency;
        }
        
        /**
         * @return the type closeness achieved by directly connecting to the intellicut part
         */
        public int getNoBurnTypeCloseness() {
            return noBurnTypeCloseness;
        }
        
        /**
         * @return the type closeness achieved by connecting to the intellicut part via burning
         */
        public int getBurnTypeCloseness() {
            return burnTypeCloseness;
        }
        
        /**
         * @return the bigger of no burn and burn type closeness
         */
        public int getMaxTypeCloseness() {
            return Math.max(noBurnTypeCloseness, burnTypeCloseness);
        }
        
        /**
         * @return true if the reference frequency is in the top fifth of reference frequencies
         */
        public boolean isReferenceFrequencyInTopFifth() {
            return referenceFrequency >= referenceFrequencyTopFifthThreshold;
        }
        
        /** 
         * @return True if this candidate is the same type as the target AND neither the target
         * nor the candidate are polymorphic types.  
         * 
         * We want to treat gems that have the same type as being especially close, even if their
         * reference frequency is not high enough that they would normally be in the Best Gems
         * list.  If we are looking for a gem that accepts a RelativeTime, and there are only 2 gems
         * with an argument of type RelativeTime, then it seems wrong to exclude those gems from 
         * the Best Gems list, even if they aren't all that common.  So, we have a special case
         * where the top 10 gems (by reference frequency) with the same type are included in
         * Best Gems even if their reference frequency is not in the top 10% globally.  
         * 
         * However, we  only want to do that for nonpolymorphic types, because we don't want to wind up 
         * in a situation where, for example, we rate unsafeCoerce (type: a->b) as a better 
         * match for the function input of map (type: a->b) than fst (type: (a,b) -> a), 
         * because in fact a function with a more concrete type is likely to be a better match 
         * than another function of polymorphic type.  
         */
        public boolean isSameNonpolymorphicType() {
            return sameNonpolymorphicType;
        }
        
        /**
         * @return true if this gem's reference frequency is above the threshold set for
         *          forcing gems with the same non-polymorphic type as the target into the
         *          Best Gems list. 
         */
        public boolean isSameNonpolymorphicTypeThreshold() {
            return referenceFrequency >= sameNonPolymorphicTypeReferenceFrequencyThreshold;
        }
        
        /**
         * @return the autoburn status for connecting to the intellicut part
         */
        public AutoburnUnifyStatus getAutoburnUnifyStatus() {
            return autoburnStatus;
        }
        
        /**
         * Set the thresholds used to determine whether a given metric is in the top 20%.
         * @param referenceFrequencyTopFifthThreshold The smallest reference frequency that is in the top fifth
         */
        public void setTopFifthThresholds(int referenceFrequencyTopFifthThreshold) {
            this.referenceFrequencyTopFifthThreshold = referenceFrequencyTopFifthThreshold;
        }
        
        /**
         * Set the reference frequency threshold for forcing gems with the same non-polymorphic type 
         * as the target into the Best Gems list.
         * @param sameNonPolymorphicTypeReferenceFrequencyThreshold
         */
        public void setSameNonpolymorphicTypeReferenceFrequencyThreshold(int sameNonPolymorphicTypeReferenceFrequencyThreshold) {
            this.sameNonPolymorphicTypeReferenceFrequencyThreshold = sameNonPolymorphicTypeReferenceFrequencyThreshold; 
        }
    }

    /**
     * This mouse motion listener for the table top will start intellicut if the mouse hovers over the
     * same unconnected part long enough and the GemCutter is in the GUI_STATE_EDIT. 
     * If the intellicutDismissTimer is running it is restarted if there is mouse motion.
     */
    private class TableTopMouseMotionListener extends MouseMotionAdapter {
    
        /**
         * For the purpose of determining whether or not to show the IntellicutPanel.
         * This variable keeps track of the last PartInput/PartOuput that the mouse was on.
         * Note: Sometimes cleared to null when we move out of range of the part.
         */
        private PartConnectable lastPartConnectable;
        
        @Override
        public void mouseMoved(MouseEvent evt) {        
    
            // Determine if we are over an unconnected (and unburnt) source/sink part.
            DisplayedPart partOver = gemCutter.getTableTop().getGemPartUnder(evt.getPoint());
                    
            if (partOver instanceof DisplayedPartOutput ||
                (partOver instanceof DisplayedPartInput && !((DisplayedPartInput)partOver).getPartInput().isBurnt())) {
    
                DisplayedPartConnectable dPartConnectable = (DisplayedPartConnectable)partOver;
                PartConnectable partConnectable = dPartConnectable.getPartConnectable();
    
                if (!partConnectable.isConnected()) {
        
                    if (lastPartConnectable == null || lastPartConnectable == partConnectable) {

                        // We're hovering over the same part or a new part. Therefore we start a new show timer
                        // if there isn't a timer already running.
                                            
                        lastPartConnectable = null;
    
                        if (intellicutPanelShowTimer == null && gemCutter.getGUIState() == GemCutter.GUIState.EDIT) {
                            startIntellicutPanelShowTimer(dPartConnectable);
                        }
                        
                    } else {
                        
                        // We're hovering over a new part connectable. Start a new timer for
                        // this connectable and remember it in case we hover over it again.
                        
                        stopIntellicutPanelShowTimer();                 
    
                        if (gemCutter.getGUIState() == GemCutter.GUIState.EDIT) {
                            startIntellicutPanelShowTimer(dPartConnectable);
                            lastPartConnectable = partConnectable;
                        }
                    }       
                        
                } else {
                    // We've moved over a part that's connected, so stop the timer.
                    stopIntellicutPanelShowTimer();
                }
                
            } else {
                // We're not over a part at all, so stop the timer.
                stopIntellicutPanelShowTimer();
            }
        }
    
        /**
         * Stops the currently running intellicutPanelShowTimer and set the
         * intellicutPanelShowTimer and lastPartConnectable to null.
         */
        private void stopIntellicutPanelShowTimer() {

            if (intellicutPanelShowTimer != null) {
                intellicutPanelShowTimer.stop();
                intellicutPanelShowTimer = null;
                lastPartConnectable = null;             
            }
        }
    
        /**
         * Start the intellicutPanelShowTimer if automatic intellicut is enabled.
         */
        private void startIntellicutPanelShowTimer(DisplayedPartConnectable dPartConnectable) {     
                    
            boolean enabled = GemCutter.getPreferences().getBoolean(INTELLICUT_POPUP_ENABLED_PREF_KEY, INTELLICUT_POPUP_ENABLED_DEFAULT);
            
            if (enabled) {
                
                int delay = GemCutter.getPreferences().getInt(INTELLICUT_POPUP_DELAY_PREF_KEY, INTELLICUT_POPUP_DELAY_DEFAULT) * 1000;
                
                intellicutPanelShowTimer = new Timer(delay, new IntellicutShowTimerActionListener(dPartConnectable));
                                                     
                intellicutPanelShowTimer.setRepeats(false);
                intellicutPanelShowTimer.start();
            }
        }
    }
    
    /**
     * ActionListener for the intellicut show timer. It starts intellicut if the mouse hovers over
     * a part input on the table top.
     */
    private class IntellicutShowTimerActionListener implements ActionListener {
    
        /** The part that intellicut will be started with if the timer fires. */
        private final DisplayedPartConnectable dPartConnectable;    
    
        public IntellicutShowTimerActionListener(DisplayedPartConnectable dConnectablePart) {
            this.dPartConnectable = dConnectablePart;
        }
        
        public void actionPerformed(ActionEvent evt) {
    
            if (dPartConnectable == null) {
                return;
            }
    
            // Sometimes (ie: broken code gem) the TypeExpr is null and should not have intellicut used on it.
            if (dPartConnectable.getPartConnectable().getType() == null) {
                return;
            }    
    
            // Don't start intellicut if the connection point has scrolled outside of
            // the visible rectangle of the table top.
            TableTopPanel tableTop = gemCutter.getTableTopPanel();
            if (!tableTop.getVisibleRect().contains(dPartConnectable.getConnectionPoint())) {
                return;
            }
    
            // Start intellicut if it is not already running for this part.
            if (intellicutMode == IntellicutMode.NOTHING || intellicutPart != dPartConnectable) {
                startIntellicut(dPartConnectable);
                displayIntellicutPanelOnTableTop(dPartConnectable);
            }
        }
    }
    
    /**
     * Constructor for an IntellicutManager
     * @param gemCutter the gemcutter for which intellicut is being managed.
     */
    IntellicutManager(GemCutter gemCutter) {

        this.gemCutter = gemCutter;

        intellicutPart = null;
        intellicutMode = IntellicutMode.NOTHING;

        // Add the listener that displays the Intellicut popup if if the user hovers over a part.
        gemCutter.getTableTopPanel().addMouseMotionListener(new TableTopMouseMotionListener());
    }

    /**
     * Get the part for which to perform intellicut. This may be null if intellicut is being
     * performed on the table top.
     * @return the intellicut part
     */
    DisplayedPartConnectable getIntellicutPart() {
        return intellicutPart;
    }
    
    /**
     * @return the current intellicut mode
     */
    IntellicutMode getIntellicutMode() {
        return intellicutMode;
    }
    
    /**
     * @return the intellicut panel in use or null if the panel is not open
     */
    IntellicutPanel getIntellicutPanel() {
        return intellicutPanel;
    }    

    /**
     * @see #displayIntellicutPanel(DisplayedGem.DisplayedPartConnectable, Rectangle, boolean, Point, JComponent)
     * @param displayRect the point where the panel should be displayed and also the gem drop point
     */
    private void displayIntellicutPanelOnTableTop(Rectangle displayRect) {
        displayIntellicutPanelOnTableTop(displayRect, null);
    }
    /**
     * @see #displayIntellicutPanel(DisplayedGem.DisplayedPartConnectable, Rectangle, boolean, Point, JComponent)
     * @param displayRect the point where the panel should be displayed and also the gem drop point
     * @param preferredDropPoint the point where the new gem should appear
     */    
    private void displayIntellicutPanelOnTableTop(Rectangle displayRect, Point preferredDropPoint) {
        displayIntellicutPanel(null, displayRect, false, preferredDropPoint, gemCutter.getTableTopPanel());
    }
    
    /**
     * @see #displayIntellicutPanel(DisplayedGem.DisplayedPartConnectable, Rectangle, boolean, Point, JComponent)
     * @param dPartConnectable the PartConnectable which intellicut should match gems to
     */
    private void displayIntellicutPanelOnTableTop(DisplayedPartConnectable dPartConnectable) {
        boolean alignLeft = dPartConnectable instanceof DisplayedPartInput;
        Rectangle displayRect = new Rectangle(dPartConnectable.getConnectionPoint());
        displayIntellicutPanel(dPartConnectable, displayRect, alignLeft, displayRect.getLocation(), gemCutter.getTableTopPanel());
    }

    /**
     * Displays the Intellicut panel at the specified location with matches for the specified PartConnectable.
     * If the part connectable is null it will display all possible gems that can be added to the table top.
     * The preferred drop point can be null in which case an appropriate drop-point, close to the top-left of
     * the table top area will be chosen automatically. The display rectangle must be specified in the coordinate
     * space of the source component. The panel will be displayed so that its corner lies on one of the rectangle's
     * corners and does not obscure any part of the rectangle.
     * 
     * @param displayedPart the part which intellicut should match gems to (can be null to just add a gem)
     * @param displayRect the rectangle along whose edges the panel will be displayed
     * @param alignLeft whether or not we want to align to the left of the display rectangle
     * @param preferredDropPoint the point on the table top where the gem should appear
     * @param source the Component that wants to display Intellicut and in whose coordinate space the display rectangle lies
     */    
    private void displayIntellicutPanel(DisplayedPartConnectable displayedPart, Rectangle displayRect, boolean alignLeft, Point preferredDropPoint, JComponent source) {

        // There may or may not be a part depending on how Intellicut was started
        Gem.PartConnectable part = null;
        if (displayedPart != null) {
            part = displayedPart.getPartConnectable();
        }

        IntellicutPanelAdapter intellicutAdapter = new IntellicutPanelAdapter(part, gemCutter, preferredDropPoint, source);

        intellicutPanel = intellicutAdapter.getIntellicutPanel();

        if (part == null || part instanceof Gem.PartInput) {
            // If we are dealing with an input or are just clicking on the table top,
            // then add the collectors so that the user can easily add emitters for them.
            Set<CollectorGem> collectors = gemCutter.getTableTop().getGemGraph().getCollectors();
            intellicutPanel.getIntellicutListModel().getAdapter().addAdditionalDataObjects(collectors);
        }

        intellicutPanel.loadListModel();
        intellicutPanel.makeTransparent();

        positionIntellicutPanel(displayRect, alignLeft, source);

        intellicutPanel.getIntellicutList().requestFocus();
        
        // Update the gem browser to display icons matching the intellicut list.
        gemCutter.getBrowserTree().repaint();        
    }
    
    /**
     * Positions the intellicut panel inside the layered pane. It is positioned so that it lies below the
     * specified display rectangle. It appears to the left or right of the display rectangle
     * depending on the alignLeft parameter.
     * @param displayRect the preferred point at which the panel should be displayed
     * @param alignLeft whether the panel should be to the left of the point
     * @param source the source component in whose coordinate space the point/area lie
     */
    private void positionIntellicutPanel(Rectangle displayRect, boolean alignLeft, JComponent source) {
        
        JLayeredPane jlp = gemCutter.getLayeredPane();

        // Transform the coordinates between the source component and the layered pane.
        displayRect = SwingUtilities.convertRectangle(source, displayRect, jlp);

        // Calculate the ideal location for the panel.
        Dimension preferredSize = intellicutPanel.getPreferredSize();
        Dimension frameSize = jlp.getSize();

        Point displayPoint = new Point(displayRect.x + displayRect.width, displayRect.y + displayRect.height);

        if (alignLeft) {
            displayPoint.x = displayRect.x - preferredSize.width;
        }

        // If the panel extends below the bottom of the window, move it up
        if (preferredSize.height > (frameSize.height - displayPoint.y)) {
            displayPoint.y = displayRect.y - preferredSize.height;

            if (displayPoint.y < 0) {
                displayPoint.y = 0;
            }
        }

        // If the panel extends past the sides of the window, move it left or right
        if (preferredSize.width > (frameSize.width - displayPoint.x)) {
            displayPoint.x = displayRect.x - preferredSize.width;
        } else if (displayPoint.x < 0) {
            displayPoint.x = displayRect.x + displayRect.width;
        }
        
        // Finally add the panel to the layered pane.    
        intellicutPanel.setLocation(displayPoint);
        intellicutPanel.setSize(preferredSize);
        jlp.add(intellicutPanel, JLayeredPane.PALETTE_LAYER, 0);        
    }

    /**
     * Starts intellicut mode for a component. The display rectangle for the panel
     * must be specified in the source component's coordinate space. The source 
     * component will regain focus when the intellicut panel is closed.
     * 
     * @param partClicked the part for which intellicut is activated.
     * @param displayRect the rectangle along whose corners the panel should appear
     * @param alignLeft if the list should be displayed to the left of the gem
     * @param dropPoint where the gem will be dropped if it is unattached
     * @param source the component in whose coordinate space the displayRect lies
     */
    void startIntellicutMode(DisplayedPartConnectable partClicked, Rectangle displayRect, boolean alignLeft, Point dropPoint, JComponent source){
        startIntellicut(partClicked);
        displayIntellicutPanel(intellicutPart, displayRect, alignLeft, dropPoint, source);
    }

    /**
     * Starts intellicut mode on the table top.
     * @param location the point where intellicut should be displayed
     */
    void startIntellicutModeForTableTop(Rectangle location) {
        startIntellicut(null);
        displayIntellicutPanelOnTableTop(location);
    }
    
    /**
     * Starts the intellicut mode on the table top.
     * @param location the point where intellicut should be displayed
     * @param preferredDropPoint the preferred drop point for the new gem
     */
    void startIntellicutModeForTableTop(Rectangle location, Point preferredDropPoint) {
        startIntellicut(null);
        displayIntellicutPanelOnTableTop(location, preferredDropPoint);
    }
    
    /**
     * Starts intellicut mode on the table top.
     * @param partClicked DisplayedPart the part for which intellicut is activated
     */
    void startIntellicutModeForTableTop(DisplayedPartConnectable partClicked) {
        startIntellicut(partClicked);
        displayIntellicutPanelOnTableTop(intellicutPart);
    }

    /**
     * Starts basic Intellicut for the given intellicut part. This does not
     * display the IntellicutPanel for the part.
     * @param intellicutPart the part to start intellicut for
     */
    private void startIntellicut(DisplayedPartConnectable intellicutPart) {

        stopIntellicut();

        this.intellicutPart = intellicutPart;
    
        if (intellicutPart instanceof DisplayedPartInput) {
            intellicutMode = IntellicutMode.PART_INPUT;
        } else if (intellicutPart instanceof DisplayedPartOutput) {
            intellicutMode = IntellicutMode.PART_OUTPUT;
        } else {
            intellicutMode = IntellicutMode.NOTHING;
        }

        // Draw the intellicut lines on the table top.
        gemCutter.getTableTopPanel().repaint();
    }

    /**
     * This fully stops intellicut by closing the intellicut panel and stopping the pulsing.
     */
    void stopIntellicut() {

        // Note that we don't want to set the intellicut part to null.
        // Some parts of the code need to know what the last intellicut part was.
        // To stop intellicut it is sufficient to set the mode to be NOTHING.
        intellicutMode = IntellicutMode.NOTHING;
        
        gemCutter.getStatusMessageDisplayer().clearMessage(gemCutter.getBrowserTree());
    
        if (intellicutPanelShowTimer != null) {
            intellicutPanelShowTimer.stop();
            intellicutPanelShowTimer = null;
        }   

        if (intellicutPanel != null) {
            intellicutPanel.close();
            intellicutPanel = null;
        }            

        gemCutter.getBrowserTree().repaint();
        gemCutter.getTableTopPanel().repaint();
    }

    /**
     * This makes a connection between the given part and the current intellicut part. This is called by
     * the table top if the user clicks on another part input while the intellicut pulsing is active.
     * @param part the part to which intellicut should attempt to connect the current intellicut part
     * @return whether a connection was made
     */
    boolean attemptIntellicutAutoConnect(DisplayedPartConnectable part) {

        PartInput inPart = null;
        PartOutput outPart = null;
        
        // Copy the reference to avoid threading issues.
        DisplayedPartConnectable intellicutPartConnectable = intellicutPart;

        // Assign the correct parts to inPart and outPart.
        if (intellicutPartConnectable instanceof DisplayedPartInput) {
            inPart = (PartInput) intellicutPartConnectable.getPartConnectable();
        } else if (intellicutPartConnectable instanceof DisplayedPartOutput) {
            outPart = (PartOutput) intellicutPartConnectable.getPartConnectable();
        }
        if (part instanceof DisplayedPartInput) {
            inPart = (PartInput) part.getPartConnectable();
        } else if (part instanceof DisplayedPartOutput) {
            outPart = (PartOutput) part.getPartConnectable();
        }
        
        // Check if parts are valid. They maye be burnt, connected or have a null type.
        // A null type occurs if parts belong to a broken gem graph.
        if (inPart == null || inPart.isBurnt() || inPart.isConnected() || inPart.getType() == null || 
            outPart == null || outPart.isConnected() || outPart.getType() == null) {
            
            return false;
        }

        Gem inGem = inPart.getGem();
        Gem outGem = outPart.getGem();
        boolean makeIntellicutConnection = false;
    
        // Connection not possible if both parts are from the same gem.
        if (outGem != inGem) {    
            
            TableTop tableTop = gemCutter.getTableTop();
            
            // Check if it is possible to make the connection by burning or connecting directly.
            if (!GemGraph.isAncestorOfBrokenGemForest(outGem)) {

                TypeExpr destTypeExpr = inPart.getType();
                AutoburnLogic.AutoburnAction burnAction = tableTop.getBurnManager().handleAutoburnGemGesture(outGem, destTypeExpr, true);

                if (burnAction != AutoburnLogic.AutoburnAction.IMPOSSIBLE && burnAction != AutoburnLogic.AutoburnAction.MULTIPLE) {
                    makeIntellicutConnection = true;

                    // Complete the connection.
                    Connection connection = tableTop.doConnectIfValidUserAction(outPart, inPart);

                    if (connection == null) {
                        gemCutter.getTableTop().showCannotConnectDialog();
                        makeIntellicutConnection = false;
                    }
                }
            }
        }
        
        return makeIntellicutConnection;
    }
    
    /**
     * Attempts to automatically connect the given displayed gem to the intellicut part.
     * Undoable edits will be posted for any connections and burnings.
     * @param dGem the gem to connect
     * @return true if a connection was made, false otherwise. A connection is not made if there are
     * multiple actions that result in the same type closeness (ie: burning/not burning is ambiguous).
     */
    boolean attemptIntellicutAutoConnect(DisplayedGem dGem) {
    
        if (intellicutPart == null) {
            return true;
        }

        Connection connection = null;
        PartConnectable part = intellicutPart.getPartConnectable();

        if (intellicutMode == IntellicutMode.PART_INPUT) {
                
            // Figure out if we should connect the gem by burning it or by just connecting it.
            // We want to perform whatever action results in the highest type closeness.

            AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(part.getType(), dGem.getGem(), gemCutter.getTypeCheckInfo());
            AutoburnUnifyStatus burnStatus = autoburnInfo.getAutoburnUnifyStatus();
            
            boolean attemptToConnect = burnStatus.isAutoConnectable();
        
            if (burnStatus == AutoburnUnifyStatus.UNAMBIGUOUS) {

                // Perform the burn if it is unambiguous.
                attemptToConnect = autoburnGem(dGem, autoburnInfo);
                
            } else if (burnStatus == AutoburnUnifyStatus.UNAMBIGUOUS_NOT_NECESSARY) {

                // Only burn it if that is better than not burning it.
                int noBurnTypeCloseness = TypeExpr.getTypeCloseness(part.getType(), dGem.getGem().getOutputPart().getType(), gemCutter.getPerspective().getWorkingModuleTypeInfo());
                if (autoburnInfo.getMaxTypeCloseness() > noBurnTypeCloseness) {
                    attemptToConnect = autoburnGem(dGem, autoburnInfo);
                }
            }
           
            if (attemptToConnect) {
                connection = gemCutter.getTableTop().doConnectIfValidUserAction(dGem.getGem().getOutputPart(), (PartInput) part);
            }
    
        } else if (intellicutMode == IntellicutMode.PART_OUTPUT){
            
            PartInput inputToConnect = GemGraph.isAutoConnectable(dGem.getGem(), (PartOutput) part, gemCutter.getConnectionContext());

            if (inputToConnect != null) {
                connection = gemCutter.getTableTop().doConnectIfValidUserAction((PartOutput) part, inputToConnect);
            }
        }
        
        // Position the gem next to the part.
        DisplayedGem intellicutGem = intellicutPart.getDisplayedGem();
        int y = intellicutGem.getLocation().y + intellicutGem.getBounds().height/2 - dGem.getBounds().height/2;
        int x = (intellicutPart instanceof DisplayedPartOutput) ? 
            intellicutGem.getLocation().x + intellicutGem.getBounds().width + DROP_DISTANCE_X :
            intellicutGem.getLocation().x - dGem.getBounds().width - DROP_DISTANCE_X;
                
        gemCutter.getTableTop().changeGemLocation(dGem, new Point(x, y));

        // If the gem was connected, use the layout arranger to tidy up.
        if (intellicutPart.isConnected()) {
            DisplayedGem[] displayedGems = {dGem, intellicutGem};
            Graph.LayoutArranger layoutArranger = new Graph.LayoutArranger(displayedGems);
            gemCutter.getTableTop().doTidyUserAction(layoutArranger, intellicutGem);
        }
        
        // Update the TableTop in case anything happened.
        gemCutter.getTableTop().updateForGemGraph();
        
        return connection != null;
    }

    /**
     * Attempt to autoburn a gem's input parts so its output can connect to the given part.
     * Undoable edits will be posted for any burnings.
     * @param dGem the displayed gem that has to get burned
     * @param autoburnInfo the autoburn info to use for burning
     * @return true if the burn was performed, false otherwise
     */
    private boolean autoburnGem(DisplayedGem dGem, AutoburnInfo autoburnInfo) {               

        if (autoburnInfo.getAutoburnUnifyStatus().isUnambiguous()) {

            AutoburnLogic.BurnCombination burnCombination = autoburnInfo.getBurnCombinations().get(0);
            int[] argsToBurn = burnCombination.getInputsToBurn();
            int numBurns = argsToBurn.length;

            for (int i = 0; i < numBurns; i++) {
                int burnIndex = argsToBurn[i];
                Gem.PartInput input = dGem.getGem().getInputPart(burnIndex);
                gemCutter.getTableTop().getBurnManager().doSetInputBurnStatusUserAction(input, AutoburnLogic.BurnStatus.AUTOMATICALLY_BURNT);
            }

            return true;
        }
        
        return false;
    }

    /**
     * @param gemEntity the entity to get intellicut info for
     * @return the intellicut info for the given entity
     */
    IntellicutInfo getIntellicutInfo(GemEntity gemEntity) {
        
        if (intellicutMode != IntellicutMode.NOTHING) {
            IntellicutListModel listModel = intellicutPanel.getIntellicutListModel();
            IntellicutListEntry listEntry = listModel.getAdapter().getListEntryForData(gemEntity);
            
            if (listEntry != null) {
                return listEntry.getIntellicutInfo();
            } else {
                return new IntellicutInfo(AutoburnUnifyStatus.NOT_POSSIBLE, -1, -1);
            }
        }

        throw new IllegalStateException("intellicut is not active");
    }

    /**
     * Paints the intellicut lines on the table top if pulsing is active.
     * @param g2d Graphics2D the graphics context
     */
    void paintIntellicutLines(Graphics2D g2d) {

        if (intellicutMode == IntellicutMode.NOTHING || intellicutPart == null) {
            return;
        }

        // Grab all the parts that could unify with the intellicut part.
        Set<DisplayedPartConnectable> intellicutCheckParts = new HashSet<DisplayedPartConnectable>();
        
        for (final DisplayedGem displayedGem : gemCutter.getTableTop().getDisplayedGems()) {
            
            if (intellicutMode == IntellicutMode.PART_OUTPUT) {
                
                int nArgs = displayedGem.getNDisplayedArguments();
                for (int i = 0; i < nArgs; i++) {
                    DisplayedPartInput dInput = displayedGem.getDisplayedInputPart(i);
                    if (dInput != null) {
                        intellicutCheckParts.add(dInput);
                    }
                }

            } else if (intellicutMode == IntellicutMode.PART_INPUT) {
                
                DisplayedPartOutput dOutput = displayedGem.getDisplayedOutputPart();
                if (dOutput != null) {
                    intellicutCheckParts.add(dOutput);
                }

            } else {
                throw new IllegalStateException("intellicut mode not supported: " + intellicutMode);
            }
        }
        
        Color prevColour = g2d.getColor();

        // Now draw all the appropriate lines.
        for (final DisplayedPartConnectable nextPart : intellicutCheckParts) {
            
            g2d.setColor(gemCutter.getTableTop().getTypeColour(nextPart));

            if (intellicutMode == IntellicutMode.PART_OUTPUT) {
                maybePaintIntellicutLine((DisplayedPartOutput) intellicutPart, (DisplayedPartInput) nextPart, g2d);

            } else {
                maybePaintIntellicutLine((DisplayedPartOutput) nextPart, (DisplayedPartInput) intellicutPart, g2d);
            }
        }

        g2d.setColor(prevColour);
    }

    /**
     * Paints the appropriate intellicut line between two displayed parts on the table top, if the parts
     * can be connected using intelilcut.
     * @param dSourcePart the source part
     * @param dSinkPart the sink part
     * @param g2d the graphics object to draw with
     */
    private void maybePaintIntellicutLine(DisplayedPartOutput dSourcePart, DisplayedPartInput dSinkPart, Graphics2D g2d) {

        if (dSourcePart == null || dSinkPart == null) {
            return;
        }

        PartConnectable sourcePart = dSourcePart.getPartConnectable();
        PartConnectable sinkPart = dSinkPart.getPartConnectable();

        int lineDashSize = -1;
        int lineSpaceSize = -1;
        float lineDashPhase = -1;
        
        if (GemGraph.arePartsConnectable(sourcePart, sinkPart) && GemGraph.isConnectionValid(sourcePart, sinkPart)) {

            // Get the burn status for the two gems
            Gem sourceGem = sourcePart.getGem();
            TypeExpr destTypeExpr = sinkPart.getType();
            AutoburnUnifyStatus autoburnUnifyStatus = GemGraph.isAncestorOfBrokenGemForest(sourceGem) ?
                                        AutoburnUnifyStatus.NOT_POSSIBLE
                                    :   AutoburnLogic.getAutoburnInfo(destTypeExpr, sourceGem, gemCutter.getTypeCheckInfo()).getAutoburnUnifyStatus();
    
            if (autoburnUnifyStatus.isConnectableWithoutBurning()) {

                // we can link directly without burning
                lineDashSize = 10;
                lineSpaceSize = 10;
                lineDashPhase = 0;
                
            } else if (autoburnUnifyStatus.isUnambiguous()) {
                
                // we can link via unambiguous burning
                lineDashSize = 1;
                lineSpaceSize = 9;
                lineDashPhase = 0;
            }
        }
    
        // Draw the dashed line if the parts can be connected
        if (lineDashSize > 0) {
            
            BasicStroke connectionStroke = 
                new BasicStroke((float) 1.0, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 
                                lineDashSize, new float[] {lineDashSize, lineSpaceSize}, lineDashPhase);

            g2d.setStroke(connectionStroke);
    
            double startX = dSourcePart.getConnectionPoint().getX();
            double startY = dSourcePart.getConnectionPoint().getY();
            double endX = dSinkPart.getConnectionPoint().getX();
            double endY = dSinkPart.getConnectionPoint().getY();            
                
            Line2D.Double connectLine = new Line2D.Double(startX, startY, endX, endY);
            g2d.draw(connectLine);  
                    
            g2d.setStroke(new BasicStroke());
        }
    }
}
