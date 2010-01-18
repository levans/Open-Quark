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
 * LazyLoadingTable.java
 * Created: Nov 12, 2003
 * By: Kevin Sit
 */
package org.openquark.gems.client.valueentry;

import java.awt.Container;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableModel;

/**
 * This class extends from the standard table to provide the ability to load rows 
 * on demand.  When the user clicks on the scroll bar, the table determines if
 * it needs to fetch data from the provider to fulfill the request.  This table
 * is useful when displaying large sequential data set.
 * 
 * @author ksit
 */
public class LazyLoadingTable extends JTable {
    
    private static final long serialVersionUID = -1117367700079761737L;

    /**
     * This event listener listens for value adjustment events posted by the vertical
     * scroll bar, computes a list of visible rows and invokes methods in the table
     * to load the rows that are not cached. 
     */
    private class VerticalScrollBarAdjustmentListener implements AdjustmentListener {
        /* (non-Javadoc)
         * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
         */
        public void adjustmentValueChanged(AdjustmentEvent e) {
            int value = e.getValue();
            int rowHeight = getRowHeight();
            if (rowHeight > 0) {
                int lowerBound = value / rowHeight;
                int upperBound = lowerBound + getPageSize();
                loadRowsFromProvider(lowerBound, upperBound);
            }
        }
    }

    /**
     * The row provider of this table.
     */
    protected LazyLoadingTableRowProvider provider;
    /**
     * A flag used to check if the initialize() method is called.
     */
    private boolean initializeOnce;
    /**
     * When this flag is set, it implies that the data set has been exhausted.
     */
    private boolean exhaustedDataSet;
    /**
     * The number of row(s) that is/are already loaded from the provider.
     */    
    private int loadedRowCount;
    
    public LazyLoadingTable(LazyLoadingTableRowProvider listener) {
        this.provider = listener;
        //initialize();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JTable#setModel(javax.swing.table.TableModel)
     */
    @Override
    public void setModel(TableModel dataModel) {
        if (!(dataModel instanceof LazyLoadingTableModel)) {
            throw new IllegalArgumentException();
        }
        super.setModel(dataModel);
    }
    
    /* (non-Javadoc)
     * @see java.awt.Component#doLayout()
     */
    @Override
    public void doLayout() {
        // TODO this might be a bad hack: we cannot get the page size until we
        // know the actual height of the view port.  However, we are not
        // going to get it until the component tree is revalidated.  So, I put
        // this block in here to initialize the table (and populate the table
        // with the first page of data) when the page size is known (and
        // initialize() should return true.
        if (!initializeOnce) {
            initializeOnce = initialize();            
        }
        super.doLayout();
    }
    
    /**
     * Get the table model of this table.
     * @return LazyLoadingTableModel
     */
    public LazyLoadingTableModel getLazyLoadingTableModel() {
        return (LazyLoadingTableModel) getModel();
    }

    /**
     * Returns the number of row(s) that the table should look ahead (and cache).
     * By default, this method returns twice the page size.
     * <p>
     * Note: the sum of the look ahead size and the page size has to be greater
     * than the visible row count.  Otherwise, the scroll bar will not appear.
     * @return int
     */    
    public int getLookAheadSize() {
        // the default behaviour is to look ahead for 2 pages
        return 2 * getPageSize();
    }
    
    /**
     * Returns the number of row(s) that the viewport can display. 
     * <p>
     * Note: the sum of the look ahead size and the page size has to be greater
     * than the visible row count.  Otherwise, the scroll bar will not appear.
     * 
     * If the view port is not yet visible, the number of rows that can be displayed
     * will not be known. In this case we use the maximum viewport size to ensure 
     * enough data is loaded.
     * 
     * @return int
     */
    public int getPageSize() {
        int visibleRowCount = getVisibleRowCount();
        
        if (visibleRowCount > 0) {
            return visibleRowCount;
        }
        
        //No rows are visible yet - use the maximum viewport size instead
        JScrollPane scrollPane = getScrollPane();
        if (scrollPane != null) {
           return scrollPane.getViewport().getMaximumSize().height / getRowHeight();
        } 
           
        return 512;
    }

    /**
     * Returns the maximum number of row(s) that is/are visible to the user.
     * This method uses the parent viewport to determine the height of the
     * visible area.  If there is no viewport defined, then this method
     * will use the table's height to determine the visible area.  This method
     * should be called after the component tree is validated, otherwise,
     * the visible area will always be evaluated to zero.
     * @return int
     */
    public int getVisibleRowCount() {
        int visibleHeight = -1;

        // if the table is embedded within a scroll pane, then get the visible area
        // from the viewport
        JScrollPane scrollPane = getScrollPane();
        if (scrollPane != null) {
            visibleHeight = scrollPane.getViewport().getHeight();
        }
        
        // otherwise, use the table's height instead
        if (visibleHeight <= 0) {
            visibleHeight = getHeight();        
        }
        
        // get the visible row count by dividing the visible drawing area height by the
        // row height
        return visibleHeight / getRowHeight();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JTable#createDefaultDataModel()
     */
    @Override
    protected TableModel createDefaultDataModel() {
        return new LazyLoadingTableModel(); 
    }
    
    /**
     * Initializes the table and populates the table with one page of data
     * (plus any look ahead data).  This method should be called once only,
     * after the component tree is validated.
     * @return boolean
     */
    protected boolean initialize() {
        JScrollPane sp = getScrollPane();
        if (sp != null) {
            // force the scroll pane to calculate the scroll bar width/height based on
            // the actual size of the table; we have use set the preferred viewport size
            // to null because JTable uses some dummy value by default
            setPreferredScrollableViewportSize(null);
            // insert a listener to listen for adjustment changes originated by the
            // vertical scroll bar of the scroll pane
            JScrollBar sb = sp.getVerticalScrollBar();
            if (sb != null) {
                sb.addAdjustmentListener(new VerticalScrollBarAdjustmentListener());
            }
            // reset the exhaustedDataSet flag because we are starting from the beginning
            exhaustedDataSet = false;
            // populate the table with one page of data, plus any look ahead data
            loadRowsFromProvider(0, getPageSize());
            return true;
        }
        return false;
    }

    /**
     * Given a set/range of rows to be loaded, this method attempts to load the rows
     * that are not loaded already.  If the given set does not intersect with
     * the current set of rows that are already loaded, then this method will
     * also attempt to load the set of rows between the two sets.  This method is
     * also aware of the look ahead value returned by the <code>getLookAheadSize()</code>
     * method.  If the given range causes the number of cached row to drop
     * below the look ahead size, then this method will also attempt to load
     * extra rows from the provider in order to bring the cache size back up.
     * @param lowerBound inclusive
     * @param upperBound exclusive
     */
    protected void loadRowsFromProvider(int lowerBound, int upperBound) {
        
        // if we have exhausted the data set already, then no loaded is required
        if (exhaustedDataSet) {
            return;
        }
        
        // swap the boundary values so that upper bound is always larger than the
        // lower bound
        if (lowerBound > upperBound) {
            int n = lowerBound;
            lowerBound = upperBound;
            upperBound = n;
        }
        
        // compute the number of row(s) that are already available (i.e. no
        // fetching required) and the number of row(s) that are missing from 
        // the local data model
        int pageSize = getPageSize();
        int lookAheadSize = getLookAheadSize();
        int availableRowCount = Math.min(loadedRowCount - lowerBound, pageSize);
        int lookAheadRowCount = Math.min(lookAheadSize - (loadedRowCount - upperBound), lookAheadSize); 
        int fetchRowCount = (upperBound - lowerBound) - availableRowCount + lookAheadRowCount;
        if (fetchRowCount > 0) {
            ArrayList<Object[]> rowList = new ArrayList<Object[]>(fetchRowCount);
            for (int i = 0; i < fetchRowCount; i++, loadedRowCount++) {
                if (provider.hasRow(loadedRowCount)) {
                    rowList.add(provider.loadRow(loadedRowCount));
                } else {
                    // we have exhausted the data set, assuming that the data set is
                    // sequential and it does not have gaps in between: i.e. it is not possible
                    // to have hasRow(i) returns true, hasRow(i+x) returns false, and
                    // hasRow(i+x+1) returns true where x > 0.
                    exhaustedDataSet = true;
                    loadedRowCount++;
                    break;
                }
            }
            Object[][] rowArray = new Object[rowList.size()][];
            rowList.toArray(rowArray);
            getLazyLoadingTableModel().addAllRows(rowArray);
        }
    }
    
    /**
     * Returns the scroll pane, if there is any, that contains this table.
     * @return JScrollPane
     */    
    protected JScrollPane getScrollPane() {
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport != null && viewport.getView() == this) {
                    return scrollPane;
                }
            }
        }
        return null;
    }
}