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
 * IntellicutList.java
 * Creation date: Dec 10th 2002
 * By: Ken Wong
 */
package org.openquark.gems.client;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;

import org.openquark.gems.client.IntellicutListModel.FilterLevel;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;


/**
 * A list that encapsulates all the details associated with populating
 * a list with the unqualified names of gems. 
 * @author Ken Wong
 * @author Frank Worsley
 */
public class IntellicutList extends JList {

    private static final long serialVersionUID = -8515575227984050475L;

    /** The last list index that was visible when the state was saved. */
    private int savedVisibleIndex = -1;

    /** The last list entry that was visible when the state was saved. */
    private IntellicutListEntry savedVisibleEntry = null;
    
    /** The list entry that was selected when the state was saved. */
    private IntellicutListEntry savedSelectedEntry = null;

    /** Whether or not this list should be drawn transparently. */
    private boolean transparent = false;

    /**
     * Constructor for IntellicutList.
     */
    public IntellicutList(IntellicutMode mode) {
        setName("MatchingGemsList");
        setCellRenderer(new IntellicutListRenderer(mode));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Enables the list to be transparent. If the list is transparent non selected list items
     * will let the components below them show through. Selected list items will draw a blue
     * semi-transparent tint as their background. 
     * @param value whether or not the list should be transparent
     */
    void setTransparent(boolean value) {
        this.transparent = value;
        updateUI();
    }
    
    /**
     * @return whether or not the intellicut list is transparent
     */
    public boolean isTransparent() {
        return transparent;
    }
    
    /**
     * Saves the currently selected list item and the scroll position of the
     * list so that the list state can be restored later.
     */
    private void saveListState() {

        IntellicutListModel listModel = (IntellicutListModel) getModel();
        
        savedVisibleEntry = null;
        savedSelectedEntry = null;
        savedVisibleIndex = getLastVisibleIndex();
        int selectedIndex = getSelectedIndex();
            
        try {
            savedVisibleEntry = listModel.get(savedVisibleIndex);
            savedSelectedEntry = listModel.get(selectedIndex);

            // We only want to make the selected entry visible again after refreshing
            // the list if it was visible before.
            if (selectedIndex > getLastVisibleIndex() || selectedIndex < getFirstVisibleIndex()) {
                savedSelectedEntry = null;
            }
                
        } catch (Exception ex) {
            // This might throw a NullPointerException or ArrayIndexOutOfBoundsException
            // if the model is not initialized. In that case there is no index we can restore. 
        }
        
    }
    
    /**
     * Restores the previously saved list state as closely as possible.
     */
    private void restoreListState() {

        IntellicutListModel listModel = (IntellicutListModel) getModel();

        int newSelectedIndex = listModel.indexOf(savedSelectedEntry);
        int newVisibleIndex = listModel.indexOf(savedVisibleEntry);
        if (newVisibleIndex == -1) {
            newVisibleIndex = savedVisibleIndex;
        }

        // We have to add one cell height, otherwise the cell will be just
        // outside of the visible range of the list. ensureIndexIsVisible doesn't
        // work here since we want the index to appear at the very bottom.
        Point newLocation = indexToLocation(newVisibleIndex);
        if (newLocation != null) {
            newLocation.y += getCellBounds(0, 0).height;
            scrollRectToVisible(new Rectangle(newLocation));
        }
        if (newSelectedIndex != -1) {
            setSelectedIndex(newSelectedIndex);
            ensureIndexIsVisible(newSelectedIndex);
        } else {
            setSelectedIndex(0);
        }
    }
    
    /**
     * Refreshes the list while remembering the selected item and scroll position
     * as closely as possible. Simply refreshing the list model will not remember
     * the list state.
     * @param prefix the new prefix for values in the list
     */
    public void refreshList(String prefix) {

        IntellicutListModel listModel = (IntellicutListModel) getModel();
        
        saveListState();
        listModel.refreshList(prefix);
        restoreListState();
    }
    
    /**
     * Finds the JViewport this list is embedded in.
     * @return the viewport or null if there is no viewport
     */
    private JViewport getViewport() {

        Container parent = getParent();
        while (parent != null && !(parent instanceof JViewport)) {
            parent = parent.getParent ();
        }
        
        return (JViewport) parent;
    }
    
    /**
     * Scrolls the given index as close as possible to the top of the list's viewport.
     * @param index index of the item to scroll to the top
     */
    void scrollToTop(int index) {
        
        Point location = indexToLocation(index);
        
        if (location == null) {
            return;
        }
        
        JViewport viewport = getViewport();
        
        // Scroll this element as close to the top as possible.
        if (viewport.getViewSize().height - location.y < viewport.getSize().height) {
            location.y -= viewport.getSize().height - viewport.getViewSize().height + location.y;
        }
        
        viewport.setViewPosition(location);
    }

    /**
     * Sets the current gem filtering level.
     * 
     * The list is automatically refreshed after doing this. This method also saves 
     * the list's displayed state as opposed to just setting the filter level which 
     * will not remember the list state.
     * @param filterLevel the new level to filter at
     */
    void setFilterLevel(FilterLevel filterLevel) {
        
        if (filterLevel == null) {
            throw new NullPointerException("filterLevel must not be null in IntellicutList.setFilterLevel");
        }
        
        IntellicutListModel listModel = (IntellicutListModel) getModel();

        saveListState();
        listModel.setFilterLevel(filterLevel);
        restoreListState();
    }
        
    /**
     * Returns the user selected IntellicutListEntry
     * @return IntellicutListEntry
     */
    IntellicutListEntry getSelected(){
        return getModel().getSize() > 0 ? (IntellicutListEntry) getSelectedValue() : null;
    }

    /**
     * We want tooltips to be displayed to the right of an item.
     * If there is no item at the coordinates it returns null.
     * @param e the mouse event for which to determine the location
     * @return the tooltip location for the list item at the coordinates of the mouse event
     */
    @Override
    public Point getToolTipLocation(MouseEvent e) {
        
        int index = locationToIndex(e.getPoint());
        
        if (index == -1) {
            return null;
        }
        
        Rectangle cellBounds = getCellBounds(index, index);

        // take off 50 and add 5 for good looks
        return new Point (cellBounds.x + cellBounds.width - 50, cellBounds.y + 5);        
    }

    /**
     * @param e the MouseEvent for which to get the tooltip text
     * @return the tooltip text for the list item at the coordinates or null if there is no
     * item at the coordinates
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        
        int index = locationToIndex(e.getPoint());
        
        if (index == -1) {
            return null;
        }
        
        IntellicutListEntry listEntry = (IntellicutListEntry) getModel().getElementAt(index);
        
        if (listEntry == null) {
            return null;
        }

        IntellicutListModel listModel = (IntellicutListModel) getModel();
        
        return listModel.getAdapter().getToolTipTextForEntry(listEntry, this);
    }
}
