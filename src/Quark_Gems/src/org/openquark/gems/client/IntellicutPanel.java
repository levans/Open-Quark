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
 * IntellicutPanelBase.java
 * Creation date: (16/04/01 2:03:41 PM)
 * By: Michael Cheng
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.openquark.gems.client.IntellicutListModel.FilterLevel;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;
import org.openquark.gems.client.utilities.SmoothHighlightBorder;


/**
 * A Panel listing the gems that are able to be connected to the intellicut part.
 * Confirmation (double-click/enter key press) will drop the selected gem down onto
 * the table top, and connected to the intellicut part.
 * Cancel (Esc) will close this panel.
 * Creation date: (16/04/01 2:03:41 PM)
 * @author Michael Cheng
 */
public class IntellicutPanel extends JPanel {

    private static final long serialVersionUID = -8327460694085077388L;

    /** A completely transparent background color. */
    private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
    
    /** A slightly tinted background color used for the panel background. */
    private static final Color BACKGROUND_TINT_COLOR = new Color(200, 200, 200, 150);

    /** The icon used by the show all gems button. */
    private static final ImageIcon showAllGemsIcon = new ImageIcon(GemCutter.class.getResource("/Resources/showAllGems.gif"));

    /** The icon used by the show likely gems button */
    private static final ImageIcon showLikelyGemsIcon = new ImageIcon(GemCutter.class.getResource("/Resources/showingGoodGems.gif"));
    
    /** The icon used by the show best gems button */
    private static final ImageIcon showBestGemsIcon = new ImageIcon(GemCutter.class.getResource("/Resources/Gem_Red.gif"));
    
    /** The icon to display next to the title label. */
    private static final ImageIcon intellicutIcon = new ImageIcon(GemCutter.class.getResource("/Resources/intellicut.gif"));

    /** The mouse listener for moving the panel around. */
    private final MovePanelMouseListener movePanelMouseListener = new MovePanelMouseListener();
    
    /** The adapter class that owns this intellicut panel. */
    private final IntellicutPanelOwner adapter;
    
    /** The scrollpane that contains the intellicut list. */
    private JScrollPane listScrollPane = null;
    
    /** The intellicut list instance. */
    private final IntellicutList intellicutList;
    
    /** The button used to pick the "all" level of gem filtering */
    private JToggleButton showAllGemsButton = null;
    
    /** The button used to pick the "likely" level of gem filtering */
    private JToggleButton showLikelyGemsButton = null;
    
    /** The button used to pick the "best" level of gem filtering */
    private JToggleButton showBestGemsButton = null;
    
    /** The toolbar that contains the showGemsButton. */
    private JToolBar showGemsButtonToolBar = null;

    /** The button group that manages exclusion among the filter buttons */
    private ButtonGroup filterButtonGroup;
    
    /** The label that shows the status message text. */
    private JLabel statusLabel = null;

    /** The label that displayed the intellicut title. */
    private JLabel titleLabel = null;
    
    /** The top panel that contains the buttons and status label. */
    private JPanel topPanel = null;

    /** The input the user has typed. This is used to refresh the intellicut list. */
    private String userInput = null;

    /** The color that the type color manager recommends for the part we are trying to connect. */
    private Color typeColor = null;

    /** The original background color that was saved when we entered drag mode. */
    private Color savedBackgroundColor = null;
    
    /** Whether the panel is currently being dragged around by the user. */
    private boolean isDragging = false;
    
    /** Whether the current filtering setting should be saved on close */
    private boolean saveFilterPreference = true;
    
    /** Whether the filter buttons are clickable */
    private boolean filtersEnabled = true; 
    
    /** 
     * The listener that updates the IntellicutList as the user types. 
     * It does so by keeping track of the String the user has typed in so far and
     * and it shrinks the visible list of matching gems accordingly.
     * Also closes the list if the user hits ESC or ENTER.
     */
    private class IntellicutKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            
            int keyCode = e.getKeyCode();
            int selectedIndex = intellicutList.getSelectedIndex();
            IntellicutListModel listModel = (IntellicutListModel) intellicutList.getModel();
            
            // Toggles between showing all or best gems
            if (keyCode == KeyEvent.VK_T && e.isAltDown()) {
                
                if (getShowAllGemsButton().isSelected()) {
                    getShowLikelyGemsButton().doClick();
                } else if (getShowLikelyGemsButton().isSelected()) {
                    getShowBestGemsButton().doClick();
                } else {
                    getShowAllGemsButton().doClick();
                }
                
                selectedIndex = intellicutList.getSelectedIndex();
                
            // Moves the selection upwards and downwards
            } else if (keyCode == KeyEvent.VK_UP) {
                selectedIndex--;

            } else if (keyCode == KeyEvent.VK_DOWN) {
                selectedIndex++;

            } else if (keyCode == KeyEvent.VK_PAGE_UP) {
                selectedIndex -= Math.min(getNumVisibleRows() - 1, selectedIndex);

            } else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
                int nextRow = intellicutList.getModel().getSize() - selectedIndex - 1;
                selectedIndex += Math.min(getNumVisibleRows() - 1, nextRow);
               
            } else if (keyCode == KeyEvent.VK_HOME) {
                selectedIndex = 0;
                
            } else if (keyCode == KeyEvent.VK_END) {
                selectedIndex = intellicutList.getModel().getSize() - 1;               

            // Commits or cancels the intellicut selection
            } else if (keyCode == KeyEvent.VK_ENTER) {
                adapter.connectSelectedGem();
                
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                adapter.stopIntellicutPanel();
                
            // Deletes the last character
            } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                
                // First delete previous character
                if (userInput.length() > 0) {
                    userInput = userInput.substring(0, userInput.length() - 1);
                    intellicutList.refreshList(userInput);
                    selectedIndex = intellicutList.getSelectedIndex();
                }
                
                // Now check if we need to re-enable the filter buttons
                if (userInput.length() == 0) {
                    setFiltersEnabled(true);
                }
                
            } else if (keyCode == KeyEvent.VK_CONTROL   || keyCode == KeyEvent.VK_ALT ||
                       keyCode == KeyEvent.VK_SHIFT     || keyCode == KeyEvent.VK_META || 
                       keyCode == KeyEvent.VK_CAPS_LOCK || e.isActionKey() || e.getKeyChar() == ' ') {
                
                // Do nothing.

            } else {

                // Adds the newly typed character to the string if necessary
                
                String newUserInput = userInput + e.getKeyChar();
                setFiltersEnabled(false);
                
                if (listModel.hasAnyGemsFor(newUserInput)) {
                    userInput = newUserInput;
                    intellicutList.refreshList(userInput);
                    selectedIndex = intellicutList.getSelectedIndex();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    if (userInput.length() == 0) {
                        setFiltersEnabled(true);
                    }
                }
                
            }
            
            // Select the correct item and make it visible
            if (selectedIndex >= 0 && selectedIndex < intellicutList.getModel().getSize()) {
                intellicutList.setSelectedIndex(selectedIndex);
                intellicutList.ensureIndexIsVisible(selectedIndex);
                updateStatusLabel();
                
                // Hide the currently displayed tooltip if we move the list selection.
                // Easiest way to do this is to temporarily disable the tooltip manager.
                ToolTipManager.sharedInstance().setEnabled(false);
                ToolTipManager.sharedInstance().setEnabled(true);
            }
            
            e.consume();
        }
    }

    /**
     * Mouse listener that listens for double left clicks to close the list and right
     * clicks to display the list popup menu.
     */
    private class DoubleClickMouseListener extends MouseClickDragAdapter {

        @Override
        public boolean mouseReallyClicked(MouseEvent e){
    
            // double left-click?
            boolean doubleClicked = super.mouseReallyClicked(e);
            
            // If it is the popup trigger, show the popup.
            if (e.isPopupTrigger()) {
                JList matchingGemsList = getIntellicutList();
                int index = matchingGemsList.locationToIndex(e.getPoint());
                if (index >= 0) { 
                    matchingGemsList.setSelectedIndex(index);
                    JPopupMenu popupMenu = adapter.getIntellicutPopupMenu();
                    if (popupMenu != null) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
    
            if (doubleClicked && SwingUtilities.isLeftMouseButton(e)) {
                adapter.connectSelectedGem();       
            }
            
            return doubleClicked;
        }
    }
    
    /**
     * This listener class is responsible for moving the intellicut panel if the user clicks
     * on it and moves the mouse.
     * @author Frank Worsley
     */
    private class MovePanelMouseListener extends MouseAdapter implements MouseMotionListener {
        
        /** The point at which the mouse was first pressed and in whose relation we are dragging. */
        private Point dragPoint = null;
        
        public void mouseDragged(MouseEvent e) {
            
            if (dragPoint == null) {
                // This shouldn't happen.
                return;
            }

            int newX = IntellicutPanel.this.getX() + e.getX() - dragPoint.x;
            int newY = IntellicutPanel.this.getY() + e.getY() - dragPoint.y;

            setDragMode(true);            
            setLocation(newX, newY);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragPoint = e.getPoint();
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (isDragging) {
                setDragMode(false);
                intellicutList.requestFocusInWindow();
            }
        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    /**
     * IntellicutPanel constructor.
     */
    public IntellicutPanel(IntellicutPanelOwner adapter,
                           IntellicutListModelAdapter modelAdapter,
                           Color typeColor, IntellicutMode mode) {

        this.adapter = adapter;
        this.typeColor = typeColor;
        this.userInput = "";
        
        IntellicutListModel listModel = new IntellicutListModel(modelAdapter);

        intellicutList = new IntellicutList(mode);
        intellicutList.setModel(listModel);
        intellicutList.setCursor(Cursor.getDefaultCursor());

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        setMoveable(true);
        
        if (typeColor != null) {
            setBackground(typeColor);
        }

        listScrollPane = new JScrollPane();
        listScrollPane.setViewportView(getIntellicutList());
        listScrollPane.getHorizontalScrollBar().setCursor(Cursor.getDefaultCursor());
        listScrollPane.getVerticalScrollBar().setCursor(Cursor.getDefaultCursor());

        add(listScrollPane, BorderLayout.CENTER);

        // Remove the default list key listeners. These are added by Swing to enable list
        // navigation, but they screw up our own intellicut searching and navigation.
        KeyListener[] keyListeners = getIntellicutList().getKeyListeners();
        for (final KeyListener listenerToRemove : keyListeners) {
            intellicutList.removeKeyListener(listenerToRemove);
        }
        
        // Make sure double clicks are handled by the list.
        intellicutList.addMouseListener(new DoubleClickMouseListener()); 
                
        // The key listener that updates the list as the user types.
        KeyListener keyListener = new IntellicutKeyListener();
        addKeyListener(keyListener);
        intellicutList.addKeyListener(keyListener);
    }
    
    /**
     * Whether or not the user should be able to move the panel around by dragging the title bar area.
     * @param moveable true if panel should be movable, false otherwise
     */
    public void setMoveable(boolean moveable) {

        if (moveable) {
            addMouseListener(movePanelMouseListener);
            addMouseMotionListener(movePanelMouseListener);
        } else {
            removeMouseListener(movePanelMouseListener);
            removeMouseMotionListener(movePanelMouseListener);
        }
    }
        
    /**
     * Removes the panel from its parent's display.
     */
    public void close() {   
        
        if (saveFilterPreference) {
            GemCutter.getPreferences().put(IntellicutManager.INTELLICUT_GEM_FILTER_LEVEL_PREF_KEY, 
                                              getIntellicutListModel().getFilterLevel().toString());
        }
        
        // Remove ourselves from our parent and repaint it.
        Container parent = getParent();
        if (parent != null) {
            
            parent.remove(this);
            
            if (parent instanceof JPopupMenu) {
                JPopupMenu popupMenu = (JPopupMenu) parent;
                popupMenu.setVisible(false);
            }
            
            if (parent instanceof JComponent) {
                ((JComponent) parent).repaint(getBounds());
            } else {
                parent.validate();
            }   
        }
    }
    
    /**
     * @return the IntellicutList for this panel.
     */
    public IntellicutList getIntellicutList() {
        return intellicutList;
    }
    
    /**
     * This method modifies the preferred size of the list's scroll pane to show the desired
     * number of rows. This method only works if the list model has at least one item in it. If 
     * called with when the list model is empty, then nothing is done.
     * @param numRows the number of rows that should be visible
     */
    public void setPreferredVisibleRows(int numRows) {
     
        if (getIntellicutListModel().isEmpty()) {
            return;
        }
        
        Dimension prefSize = listScrollPane.getPreferredSize();
        int cellHeight = getIntellicutList().getCellBounds(0, 0).height;
        int borders = listScrollPane.getInsets().top + listScrollPane.getInsets().bottom;
        listScrollPane.setPreferredSize(new Dimension(prefSize.width, numRows * cellHeight + borders));
    }
    
    /**
     * @return the number of rows currently visible in the Intellicut list. 
     */
    private int getNumVisibleRows() {
        
        if (getIntellicutListModel().isEmpty()) {
            return 0;
        }
        
        int rowHeight = intellicutList.getCellBounds(0, 0).height;
        int listHeight = listScrollPane.getSize().height;
        
        return listHeight / rowHeight;
    }
    
    /**
     * @return the list model used by the intellicut list.
     */
    public IntellicutListModel getIntellicutListModel() {
        return (IntellicutListModel) getIntellicutList().getModel(); 
    }
    
    /**
     * Sets the title of the intellicut panel.
     * @param newTitle the new title for the list
     */
    public void setTitle(String newTitle) {
        getTitleLabel().setText(newTitle);
    }
    
    /**
     * Initially loads the list model. This should be called after users of the panel
     * have added any additional gems they want to be loaded.  This is a separate call
     * to make loading gems and displaying the list more efficient.
     */
    public void loadListModel() {
        
        IntellicutListModel listModel = getIntellicutListModel();
        
        listModel.load();

        FilterLevel filterLevel = FilterLevel.fromString(GemCutter.getPreferences().get(IntellicutManager.INTELLICUT_GEM_FILTER_LEVEL_PREF_KEY, IntellicutManager.INTELLICUT_GEM_FILTER_LEVEL_DEFAULT.toString()));
        boolean hasBestGems = listModel.hasFilteredGemsFor("", filterLevel); 
        if (!hasBestGems) {
            filterLevel = FilterLevel.SHOW_ALL;
        }
        
        // Processing to eliminate redundant buttons (ie, those that add no filtering)
        int numEntries = listModel.numFilteredGems(FilterLevel.SHOW_ALL);
        int numLikelyEntries = listModel.numFilteredGems(FilterLevel.SHOW_LIKELY);
        int numBestEntries = listModel.numFilteredGems(FilterLevel.SHOW_BEST);

        if ((numEntries - numLikelyEntries) < 20 || (numLikelyEntries - numBestEntries) < 20) {
            getShowGemsButtonToolBar().remove(getShowLikelyGemsButton());
            if (filterLevel == FilterLevel.SHOW_LIKELY) {
                filterLevel = FilterLevel.SHOW_BEST;
                saveFilterPreference = false; // Don't save automatically adjusted filter settings
            }
        }
        
        if ((numEntries - numBestEntries) < 20 || numBestEntries == 0) {
            getShowGemsButtonToolBar().remove(getShowBestGemsButton());
            if (filterLevel == FilterLevel.SHOW_BEST) {
                filterLevel = FilterLevel.SHOW_ALL;
                saveFilterPreference = false; // Don't save automatically adjusted filter settings
            }
        }
        
        listModel.setFilterLevel(filterLevel);
        listModel.refreshList("");

        // If there are no matching gems, display an error message and return.
        if (listModel.isEmpty()) {
            JLabel message = new JLabel(adapter.getMessages().getString("ICL_NoGemsFound"));
            message.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            message.setIcon(intellicutIcon);
            
            remove(listScrollPane);
            add(message, BorderLayout.CENTER);
            
            return;
        }

        // Add the top panel only now so that the status message and button state
        // are synced up with the show all gems state of the model.
        add(getTopPanel(), BorderLayout.NORTH);
        
        if (listModel.getFilterLevel() == FilterLevel.SHOW_ALL) {
            getShowAllGemsButton().getModel().setSelected(true);
        } else if(listModel.getFilterLevel() == FilterLevel.SHOW_LIKELY) {
            getShowLikelyGemsButton().getModel().setSelected(true);
        } else if(listModel.getFilterLevel() == FilterLevel.SHOW_BEST) {
            getShowBestGemsButton().getModel().setSelected(true);
        }
        
        getIntellicutList().setSelectedIndex(0);

        setPreferredVisibleRows(20);
    }
    
    /**
     * Enables transparency for the intellicut panel and components within it.
     * If you enable transparency the panel and list will be coloured in a light tint,
     * allowing you to see the components below it. Once transparency is enabled you
     * can't disabled it anymore. You have to recreate the panel if you don't want
     * transparency anymore.
     */
    public void makeTransparent() {

        // Components can not be opaque or transparency will not work. We have to
        // manually draw in the background since Java screws it up for some reason.

        setOpaque(false);
        setBackground(BACKGROUND_TINT_COLOR);

        setBorder(new SmoothHighlightBorder(typeColor != null ? typeColor : getBackground(), false));
        
        getIntellicutList().setBackground(TRANSPARENT_COLOR);
        getIntellicutList().setOpaque(false);
        getIntellicutList().setTransparent(true);
        
        listScrollPane.setBackground(TRANSPARENT_COLOR);
        listScrollPane.setOpaque(false);
        
        listScrollPane.getViewport().setBackground(TRANSPARENT_COLOR);
        listScrollPane.getViewport().setOpaque(false);
        
        getTopPanel().setBackground(TRANSPARENT_COLOR);
        getTopPanel().setOpaque(false);
        
        getStatusLabel().setBackground(TRANSPARENT_COLOR);
        getStatusLabel().setOpaque(false);
        
        getShowAllGemsButton().setBackground(TRANSPARENT_COLOR);
        getShowAllGemsButton().setOpaque(false);

        getShowLikelyGemsButton().setBackground(TRANSPARENT_COLOR);
        getShowLikelyGemsButton().setOpaque(false);

        getShowBestGemsButton().setBackground(TRANSPARENT_COLOR);
        getShowBestGemsButton().setOpaque(false);

        getShowGemsButtonToolBar().setBackground(TRANSPARENT_COLOR);
        getShowGemsButtonToolBar().setOpaque(false);

        // Here we have to again remove the list's default key listeners that are added
        // by Swing. These listeners are re-added when the list updates its UI when the
        // transparency is enabled. The listeners need to be removed since they screw up
        // our own key navigation and intellicut searching.
        KeyListener[] keyListeners = getIntellicutList().getKeyListeners();
        for (final KeyListener listenerToRemove : keyListeners) {
            getIntellicutList().removeKeyListener(listenerToRemove);
        }
        
        // Add back our own listener
        getIntellicutList().addKeyListener(new IntellicutKeyListener());        
    }
    
    /**
     * Makes the entire panel except for its border invisible. This is used to draw the outline
     * of the panel while it is being moved around the screen.
     * @param dragMode true to make everything invisible but the border, false otherwise
     */
    private void setDragMode(boolean dragMode) {
        
        if (dragMode == isDragging) {
            return;
        }
        
        isDragging = dragMode;
        
        if (dragMode) {
            savedBackgroundColor = getBackground();
            setBackground(TRANSPARENT_COLOR);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            listScrollPane.setVisible(false);
            getTopPanel().setVisible(false);
            
        } else {
            
            setBackground(savedBackgroundColor);

            if (intellicutList.isTransparent()) {
                setBorder(new SmoothHighlightBorder(typeColor != null ? typeColor : getBackground(), false));
            } else {
                setBorder(BorderFactory.createEtchedBorder());
            }

            getTopPanel().setVisible(true);
            listScrollPane.setVisible(true);
        }
    }
    
    /**
     * We draw in our background manually. This is necessary since Java screws up the background
     * drawing if the panel is transparent.
     */
    @Override
    public void paintComponent(Graphics g) {
        Insets insets = getInsets();
        g.setColor(getBackground());
        g.fillRect(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);
    }
    
    /**
     * Returns the panel at the top of the intellicut list that contains the switch buttons
     * and title labels.
     * @return JPanel
     */
    private JPanel getTopPanel() {
        
        if (topPanel == null) {

            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
    
            topPanel.add(getTitleLabel());
            topPanel.add(Box.createHorizontalGlue());
            topPanel.add(getStatusLabel());
            topPanel.add(Box.createHorizontalStrut(5));
            topPanel.add(getShowGemsButtonToolBar());
            
            // we don't want a visible border for the top panel, just use this to create margins
            topPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

            // set the size to be large enough to display long status messages
            Dimension prefSize = getTopPanel().getPreferredSize();
            topPanel.setPreferredSize(new Dimension(prefSize.width + 25, prefSize.height));
            
            topPanel.setCursor(Cursor.getDefaultCursor());
        }
        
        return topPanel;        
    }
    
    /**
     * Returns the title label for the title of the intellicut list.
     * @return JLabel
     */
    private JLabel getTitleLabel() {
        
        if (titleLabel == null) {
            titleLabel = new JLabel(adapter.getMessages().getString("ICL_IntellicutTitle"));
            
            titleLabel.setIcon(intellicutIcon);
            
            Font labelFont = getFont().deriveFont(Font.BOLD, getFont().getSize() + 4);
            titleLabel.setFont(labelFont);
            
            titleLabel.setCursor(Cursor.getDefaultCursor());
        }

        return titleLabel;
    }
    
    /**
     * Returns the status label for the status of the intellicut list.
     * @return JLabel
     */
    private JLabel getStatusLabel() {
        
        if (statusLabel == null) {
            statusLabel = new JLabel();
            statusLabel.setCursor(Cursor.getDefaultCursor());
            updateStatusLabel();
        }
        
        return statusLabel;
    }
 
    private static class TransparentToggleButton extends JToggleButton {
        private static final long serialVersionUID = -7591214478387598044L;

        /**
         * This overrides paintComponent to make transparency work correctly.
         * We have to draw in our own background, otherwise Java will screw it up.
         * @param g the Graphics object to draw with
         */
        @Override
        public void paintComponent(Graphics g) {
            
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            super.paintComponent(g);
        }
    }

    /** @return JToggleButton the button for the show all gems level of filtering */
    private JToggleButton getShowAllGemsButton() {
        if (showAllGemsButton == null) {
            showAllGemsButton = new TransparentToggleButton();

            showAllGemsButton.setAction(getGemFilterButtonAction(showAllGemsButton, showAllGemsIcon, "ICL_ShowAllGemsToolTip", FilterLevel.SHOW_ALL));            
            showAllGemsButton.setText(null);
            showAllGemsButton.setMargin(new Insets(0, 0, 0, 0));
            showAllGemsButton.setCursor(Cursor.getDefaultCursor());
        }
        
        return showAllGemsButton;
    }

    /** @return JToggleButton the button for the show likely gems level of filtering */
    private JToggleButton getShowLikelyGemsButton() {
        if (showLikelyGemsButton == null) {
            showLikelyGemsButton = new TransparentToggleButton();

            showLikelyGemsButton.setAction(getGemFilterButtonAction(showLikelyGemsButton, showLikelyGemsIcon, "ICL_ShowLikelyGemsToolTip", FilterLevel.SHOW_LIKELY));            
            showLikelyGemsButton.setText(null);
            showLikelyGemsButton.setMargin(new Insets(0, 0, 0, 0));
            showLikelyGemsButton.setCursor(Cursor.getDefaultCursor());
        }
        
        return showLikelyGemsButton;
    }

    /** @return JToggleButton the button for the show best gems level of filtering */
    private JToggleButton getShowBestGemsButton() {
        if (showBestGemsButton == null) {
            showBestGemsButton = new TransparentToggleButton();

            showBestGemsButton.setAction(getGemFilterButtonAction(showBestGemsButton, showBestGemsIcon, "ICL_ShowBestGemsToolTip", FilterLevel.SHOW_BEST));            
            showBestGemsButton.setText(null);
            showBestGemsButton.setMargin(new Insets(0, 0, 0, 0));
            showBestGemsButton.setCursor(Cursor.getDefaultCursor());
        }
        
        return showBestGemsButton;
    }

    /**
     * We cheat and place the show gems button inside a toolbar.
     * Toolbars can automatically give buttons the nice mouse-rollover effect,
     * without us having to install custom mouse listeners to do it.
     * @return the toolbar the show gems button is placed in.
     */
    private JToolBar getShowGemsButtonToolBar() {
        
        if (showGemsButtonToolBar == null) {
        
            showGemsButtonToolBar = new JToolBar() {
                    
                private static final long serialVersionUID = -4840586584969771381L;

                /**
                 * This overrides paintComponent to make transparency work correctly.
                 * We have to draw in our own background, otherwise Java will screw it up.
                 * @param g the Graphics object to draw with
                 */
                @Override
                public void paintComponent(Graphics g) {
                        
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
    
                    super.paintComponent(g);
                }
            };
                
            showGemsButtonToolBar.setOpaque(true);
            showGemsButtonToolBar.setRollover(true);
            showGemsButtonToolBar.setFloatable(false);
            showGemsButtonToolBar.setCursor(Cursor.getDefaultCursor());
            showGemsButtonToolBar.add(getShowBestGemsButton());
            showGemsButtonToolBar.add(getShowLikelyGemsButton());
            showGemsButtonToolBar.add(getShowAllGemsButton());
        
            getFilterButtonGroup();
        }
        
        return showGemsButtonToolBar;        
    }
    
    private ButtonGroup getFilterButtonGroup() {
        if (filterButtonGroup == null) {
            
            filterButtonGroup = new ButtonGroup();
            
            filterButtonGroup.add(getShowAllGemsButton());
            filterButtonGroup.add(getShowLikelyGemsButton());
            filterButtonGroup.add(getShowBestGemsButton());
        }
        
        return filterButtonGroup;
    }
    
    /**
     * Returns an action for the three filter-level buttons
     * @param button The button that this action is for
     * @param icon Icon to display on the button
     * @param toolTipId String id of the tooltip for this button
     * @param filterLevel Filter level that this button sets
     * @return Action
     */
    private Action getGemFilterButtonAction(JToggleButton button, ImageIcon icon, String toolTipId, final FilterLevel filterLevel) {
        
        Action filterButtonAction = new AbstractAction("filterButtonAction", icon) {
            
            private static final long serialVersionUID = -196510266687548115L;

            public void actionPerformed(ActionEvent evt) {
                
                getIntellicutList().setFilterLevel(filterLevel);
                getIntellicutList().requestFocus();
                updateStatusLabel();
                saveFilterPreference = true;
            }
        };
        
        filterButtonAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_T));
        filterButtonAction.putValue(Action.SHORT_DESCRIPTION, adapter.getMessages().getString(toolTipId));
    
        return filterButtonAction;
    }
    
    /**
     * Updates the status label of the list to show the correct information.
     */
    private void updateStatusLabel() {
        
        FilterLevel filterLevel = getIntellicutListModel().getFilterLevel();
        int numGems = getIntellicutListModel().getSize();
        
        String message = null;
        String messageId = (filtersEnabled == false)                    ? (numGems > 1 ? "ICL_ShowingMatchingGems" : "ICL_ShowingOneMatchingGem") :
                           (filterLevel == FilterLevel.SHOW_ALL)        ? (numGems > 1 ? "ICL_ShowingAllGems" : "ICL_ShowingAllOneGem") :
                           (filterLevel == FilterLevel.SHOW_LIKELY)     ? (numGems > 1 ? "ICL_ShowingLikelyGems" : "ICL_ShowingLikelyOneGem") :
                                        /* FilterLevel.SHOW_BEST */       (numGems > 1 ? "ICL_ShowingBestGems" : "ICL_ShowingBestOneGem");
        
        if (numGems > 1) {
            Object[] arguments = { Integer.valueOf(numGems) };
            message = MessageFormat.format(adapter.getMessages().getString(messageId), arguments);
        } else {
            message = adapter.getMessages().getString(messageId);
        }
        
        getStatusLabel().setText(message);
    }

    private void setFiltersEnabled(boolean enableFilters) {
        
        filtersEnabled = enableFilters;

        if (enableFilters) {
            getShowAllGemsButton().setVisible(true);
            getShowLikelyGemsButton().setVisible(true);
            getShowBestGemsButton().setVisible(true);
        } else {
            getShowAllGemsButton().setVisible(false);
            getShowLikelyGemsButton().setVisible(false);
            getShowBestGemsButton().setVisible(false);
        }

        updateStatusLabel();
    }
}
