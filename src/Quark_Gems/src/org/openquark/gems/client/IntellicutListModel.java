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
 * IntellicutListModel.java
 * Creation date: Feb 21-2002
 * By: David Mosimann, Frank Worsley
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;


/**
 * Provides the list model and combobox model that are used by the intellicut list and combobox.
 * Not thread-safe.
 * @author Frank Worsley
 */
public class IntellicutListModel extends AbstractListModel implements ComboBoxModel {

    private static final long serialVersionUID = -1244914095475717587L;

    /** The adapter for this list model. */
    private final IntellicutListModelAdapter adapter;

    /** The string of user input that has been typed in. */
    private String userInput = null;
    
    /** The currently selected item in the list (needed for the ComboBoxModel interface). */
    private Object selectedItem = null;
    
    /** the elements currently comprising the model */
    private final List<IntellicutListEntry> elementsOfModel = new ArrayList<IntellicutListEntry>();
    
    /**
     * Typesafe enum pattern representing the level of filtering to perform on Gems
     * in the Intellicut list panel.  Values of FilterLevel can be compared using ==. 
     * 
     * Creation date: (Apr 27, 2005)
     * @author Jawright
     */
    public static final class FilterLevel {
        
        /** Filter level to show all gems whose type fits */
        public static final FilterLevel SHOW_ALL = new FilterLevel("ShowAll");

        /** Filter level to show all gems whose typeCloseness is >0 or whose reference frequency is in the top 20% */
        public static final FilterLevel SHOW_LIKELY = new FilterLevel("ShowLikely");

        /** Filter level to show all gems whose typeCloseness is >0 and whose reference frequency is in the top 20% */
        public static final FilterLevel SHOW_BEST = new FilterLevel("ShowBest");
        
        /** @return The unique name of this FilterLevel */
        @Override
        public String toString() {
            return uniqueID;
        }
        
        /**
         * Returns the FilterLevel that has the specified uniqueID
         * @param uniqueID String specifying the FilterLevel to return
         * @return FilterLevel with the given uniqueID
         */
        public static FilterLevel fromString(String uniqueID) {
            if (uniqueID.equals("ShowAll")) {
                return SHOW_ALL;
            } else if (uniqueID.equals("ShowLikely")) {
                return SHOW_LIKELY;
            } else if (uniqueID.equals("ShowBest")) {
                return SHOW_BEST;
            } else {
                throw new IllegalArgumentException(uniqueID + " does not specify a valid FilterLevel");
            }
        }
        
        /** The unique string name of this FilterLevel */
        private final String uniqueID;
        
        /**
         * Constructor for FilterLevels.  This constructor is private to prevent other
         * values of FilterLevel from being defined outside this class.
         * @param uniqueID ID that uniquely identifies this instance of FilterLevel
         */
        private FilterLevel(String uniqueID) {
            this.uniqueID = uniqueID;
        }
    }
    
    /** Level of filtering to apply to what is shown in the list */
    private FilterLevel filterLevel = FilterLevel.SHOW_ALL;
    
    /**
     * Constructor for a new IntellicutListModel.
     * @param adapter the adapter for the list model
     */
    public IntellicutListModel(IntellicutListModelAdapter adapter) {
        
        if (adapter == null) {
            throw new NullPointerException();
        }
        
        this.adapter = adapter;
    }
    
    /**
     * @return the adapter used by this model
     */
    public IntellicutListModelAdapter getAdapter() {
        return adapter;
    }
    
    /**
     * Initially loads the list of available list entries.
     * Does nothing if the list is already loaded.
     */
    public void load() {
        if (adapter.getListEntrySet().isEmpty()) {
            adapter.load();
        } else {
            refreshList(userInput);
        }
    }
    
    /**
     * Refreshes the displayed list of items so that they match the specified user input.
     * @param userInput the prefix they have to match
     */
    public void refreshList(String userInput) {
        refreshList(userInput, filterLevel);
    }
    
    /**
     * Refreshes the displayed list of items so that they match the specified input
     * and whether or not all items should be shown.
     * @param userInput the prefix they have to match
     * @param filterLevel whether to show all items or only items with type closeness > 0
     */
    private void refreshList (String userInput, FilterLevel filterLevel) {
        
        this.userInput = userInput;
        this.filterLevel = filterLevel;
       
        // Load the list of all gems if it has not been loaded yet.
        if (adapter.getListEntrySet().isEmpty()) {
            load();
        }
        
        // Clear our current subset of gems.
        elementsOfModel.clear();
        
        // Add back all gems that match the new criteria.
        for (final IntellicutListEntry listEntry : adapter.getListEntrySet()) {
            
            String sortString = listEntry.getSortString();
            String namePrefix = null;
        
            if (userInput == null) {
                userInput = "";
            }
            
            if (userInput.length() > 0 && userInput.length() <= sortString.length()) {
                namePrefix = sortString.substring(0, userInput.length());
            }
            
            // Only include the entry in the current model subset if the input length is zero and
            // it is a best entry or if the prefix matches the user input.
            if ((adapter.passesFilter(listEntry, filterLevel) && userInput.length() == 0) || userInput.equalsIgnoreCase(namePrefix)) {
                elementsOfModel.add(listEntry);
            }
        }
        fireContentsChanged(this, 0, elementsOfModel.size());
    }

    /**
     * Returns the user input with which the model was last refreshed.
     * @return the user input string
     */
    public String getUserInput() {
        return userInput;
    }
    
    /**
     * Returns the current filtering level
     * @return FilterLevel Current level of filtering
     */
    public FilterLevel getFilterLevel() {
        
        return filterLevel;
    }
    
    /**
     * Sets the level at which the list of gems should be filtered
     * @param newLevel int level to filter at (SHOW_ALL, SHOW_LIKELY, or SHOW_BEST)
     */
    public void setFilterLevel(FilterLevel newLevel) {
        
        if (filterLevel == null) {
            throw new NullPointerException("filterLevel must not be null in IntellicutListModel.setFilterLevel");
        }
        
        if (filterLevel != newLevel) {
            filterLevel = newLevel;
            refreshList(userInput, filterLevel);
        }
    }
        
    /**
     * Finds the first list entry in the model that matches the given prefix.
     * This searches the currently visible entries, not the entire set of entries.
     * @param prefix the prefix to check for
     * @return the first list entry that matches, null if none matches
     */
    IntellicutListEntry getFirstMatchFor(String prefix) {
        
        if (getSize() == 0) {
            return null;
        }

        if (prefix.length() == 0) {
            return (IntellicutListEntry) getElementAt(0);
        }

        for (final IntellicutListEntry listEntry : elementsOfModel) {
            
            // get the name of the gem we are currently traversing.
            String entryName = listEntry.getSortString();
            
            // Make sure the input length is shorter than the name.
            if (prefix.length() <= entryName.length()) { 
                
                // Return the first match.
                if (entryName.substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
                    return listEntry;
                }
            }
        }

        return null;
    }

    /**
     * Returns whether or not the there exists a list entry that has the given name prefix.
     * This searches the entire list of list entries, not just the currently visible subset.
     * @param prefix the required name prefix (case-insensitive)
     * @param filterLevel The level of filter that the gems must pass to count
     * @return true if a match is found, false otherwise
     */
    private boolean hasMatchesFor(String prefix, FilterLevel filterLevel) {

        if (adapter.getListEntrySet().isEmpty()) {
            return false;
        }
        
        if (prefix.length() == 0 && filterLevel == FilterLevel.SHOW_ALL) {
            return true;
        }
        
        for (final IntellicutListEntry listEntry : adapter.getListEntrySet()) {
            
            String entryName = listEntry.getSortString();
            
            // Make sure the input length is shorter than the name.
            if (prefix.length() <= entryName.length()) { 
                
                // Even if there is a single match, we return true.
                if (adapter.passesFilter(listEntry, filterLevel) && 
                    entryName.substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
                        
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns whether or not there is any list entry that has the given prefix.
     * @param prefix the required name prefix (case-insensitive)
     * @return true if a match is found, false otherwise
     */
    boolean hasAnyGemsFor(String prefix) {
        return hasMatchesFor(prefix, FilterLevel.SHOW_ALL);
    }
    
    /**
     * Returns whether or not there exists a list entry that has the given name prefix and
     * a type closeness of at least 1.
     * @param prefix the desired name prefix (case-insensitive)
     * @return true if a match is found, false otherwise
     */
    boolean hasFilteredGemsFor(String prefix, FilterLevel filterLevel) {
        return hasMatchesFor(prefix, filterLevel);
    }
    
    /**
     * Return the number list entries that pass the specified filter level
     * @param filterLevel filter level to check for (SHOW_ALL, SHOW_LIKELY, or SHOW_BEST)
     * @return number of list entries that pass the specified filter
     */
    int numFilteredGems(FilterLevel filterLevel) {

        // Scan through the list of entries counting up the number of passes
        int passCount = 0;
        
        for (final IntellicutListEntry listEntry : adapter.getListEntrySet()) {
            
            if (adapter.passesFilter(listEntry, filterLevel)) {
                passCount++;
            }
        }
        
        return passCount;
    }
    
    /**
     * Implemented for the ComboBoxModel interface.
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * Implemented for the ComboBoxModel interface.
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return selectedItem;
    }

    /**
     * {@inheritDoc}
     */
    public Object getElementAt(int index) {
        return elementsOfModel.get(index);
    }

    /**
     * @param index the index of the entry to get.
     * @return the entry at the given index.
     */
    public IntellicutListEntry get(int index) {
        return (IntellicutListEntry)getElementAt(index);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSize() {
        return elementsOfModel.size();
    }

    /**
     * @return whether the list model is empty.
     */
    public boolean isEmpty() {
        return elementsOfModel.isEmpty();
    }

    /**
     * @param entry the entry for which the index should be found.
     * @return the index of the entry in the current list model, or -1 if the entry is not a member of the model.
     */
    public int indexOf(IntellicutListEntry entry) {
        return elementsOfModel.indexOf(entry);
    }
}
