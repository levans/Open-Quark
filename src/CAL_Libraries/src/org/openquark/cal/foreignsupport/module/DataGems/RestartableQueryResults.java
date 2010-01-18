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
 * RestartableQueryResults.java
 * Created: ??
 * By: ??
 */
package org.openquark.cal.foreignsupport.module.DataGems;

import java.math.BigDecimal;
import java.util.Date;

import org.openquark.util.database.SqlType;
import org.openquark.util.time.Time;


/**
 * This class adds random access support to a forward-only resultset.
 * If the rows are accessed out of order, then the resultset will be recreated
 * and then will seek to the requested row.
 */
public abstract class RestartableQueryResults implements QueryResult {
    /** The current query results. */
    private QueryResult currentResults;

    /** The row at which the result set is currently positioned. */
    private int currentRow = 0;

    /**
     * Constructor for RestartableQueryResults.
     */
    public RestartableQueryResults() {
        this(null);
    }

    /**
     * Constructor for RestartableQueryResults.
     */
    public RestartableQueryResults(QueryResult currentResults) {
        this.currentResults = currentResults;
    }

    /**
     * Create a new resultset, positioned at the start.
     */
     protected abstract QueryResult createResultSet() throws DatabaseException;

     /**
      * Returns the current query results, creating a new one if none exists.
      * @return  the current query results
      */
     private QueryResult getQueryResults() throws DatabaseException {
         // Create the initial resultset, if necessary.
         if (currentResults == null) {
//             System.out.println(">>>Creating resultset.");
             currentResults = createResultSet();
             currentRow = 0;
         }
         return currentResults;
     }

     /**
      * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#close()
      */
    public void close() throws DatabaseException {
        if (currentResults != null) {
            currentResults.close();
        }
        currentResults = null;
        currentRow = 0;
    }

    /**
     * Moves to the specified row.
     */
    public boolean moveToRow(int row) throws DatabaseException {
         // Create the initial resultset, if necessary.
         getQueryResults();

         // If the requested row is the same as the current one, then no action
         // is required.
         if (row == currentRow) {
//             System.out.println("Returning current row: " + row);
             return true;
         }

         // If the requested row precedes the current position, then recreate the
         // resultset and start from the beginning again.
         if (row < currentRow) {
//             System.out.println(">>>Creating resultset.");
             if (currentResults != null) {
                 currentResults.close();
             }
             currentResults = createResultSet();
             currentRow = 0;
         }

         // If the requested row is the new one, then advance the resultset
         // position.
//       System.out.println("On row " + currentRow + " moving to row " + row);

         while (row > currentRow) {
             boolean moveResult = currentResults.moveNext();
             if (!moveResult) {
                 currentResults.close();
                 currentResults = null;
                 currentRow = 0;
                 return false;
             }
             ++currentRow;
         }
         return true;
     }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getColumnCount()
     */
    public int getColumnCount() throws DatabaseException {
        return getQueryResults().getColumnCount();
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getColumnLabel(int)
     */
    public String getColumnLabel(int column) throws DatabaseException {
        return getQueryResults().getColumnLabel(column);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getColumnName(int)
     */
    public String getColumnName(int column) throws DatabaseException {
        return getQueryResults().getColumnName(column);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getColumnIndex(java.lang.String)
     */
    public int getColumnIndex(String columnName) throws DatabaseException {
        return getQueryResults().getColumnIndex(columnName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getColumnType(int)
     */
    public SqlType getColumnType(int column) throws DatabaseException {
        return getQueryResults().getColumnType(column);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getColumnDisplaySize(int)
     */
    public int getColumnDisplaySize(int column) throws DatabaseException {
        return getQueryResults().getColumnDisplaySize(column);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#moveFirst()
     */
    public boolean moveFirst() throws DatabaseException {
        return moveToRow(1);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#moveNext()
     */
    public boolean moveNext() throws DatabaseException {
        return moveToRow(currentRow + 1);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#recordAt(int)
     */
    public boolean recordAt(int row) {
        // Return whether the resultset has been exhausted (the cursor is after the last record)
        // If an error occurs, we report the end of the result set (return false)
        try {
            return moveToRow(row);
        } catch (Exception e) {
            // An error occured, report no more records
            return false;
        }
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#wasLastFetchNull()
     */
    public boolean wasLastFetchNull() throws DatabaseException {
        return getQueryResults().wasLastFetchNull();
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#resultGetRecord(int)
     */
    public RecordPlaceholder resultGetRecord(int row) throws DatabaseException {
        return new RecordPlaceholder(this, row);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowString(int)
     */
    public String getCurrentRowString(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowString(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowString(java.lang.String)
     */
    public String getCurrentRowString(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowString(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowInt(int)
     */
    public int getCurrentRowInt(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowInt(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowInt(java.lang.String)
     */
    public int getCurrentRowInt(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowInt(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowLong(int)
     */
    public long getCurrentRowLong(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowLong(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowLong(java.lang.String)
     */
    public long getCurrentRowLong(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowLong(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowDouble(int)
     */
    public double getCurrentRowDouble(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowDouble(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowDouble(java.lang.String)
     */
    public double getCurrentRowDouble(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowDouble(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowDecimal(int)
     */
    public BigDecimal getCurrentRowDecimal(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowDecimal(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowDecimal(java.lang.String)
     */
    public BigDecimal getCurrentRowDecimal(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowDecimal(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowDate(int)
     */
    public Date getCurrentRowDate(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowDate(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowDate(java.lang.String)
     */
    public Date getCurrentRowDate(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowDate(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowTime(int)
     */
    public Time getCurrentRowTime(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowTime(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowTime(java.lang.String)
     */
    public Time getCurrentRowTime(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowTime(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowBoolean(int)
     */
    public boolean getCurrentRowBoolean(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowBoolean(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowBoolean(java.lang.String)
     */
    public boolean getCurrentRowBoolean(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowBoolean(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowBytes(int)
     */
    public byte[] getCurrentRowBytes(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowBytes(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowBytes(java.lang.String)
     */
    public byte[] getCurrentRowBytes(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowBytes(colName);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowObject(int)
     */
    public Object getCurrentRowObject(int colIndex) throws DatabaseException {
        return getQueryResults().getCurrentRowObject(colIndex);
    }

    /**
     * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#getCurrentRowObject(java.lang.String)
     */
    public Object getCurrentRowObject(String colName) throws DatabaseException {
        return getQueryResults().getCurrentRowObject(colName);
    }
}
