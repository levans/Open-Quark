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
 * QueryResult.java
 * Created: Oct 13, 2004
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.DataGems;

import java.math.BigDecimal;
import java.util.Date;

import org.openquark.util.database.SqlType;
import org.openquark.util.time.Time;


/**
 * A common interface for accessing resultset information.
 * @author Richard Webster
 */
public interface QueryResult {

    /**
     * Closes the resultset.
     * @throws DatabaseException
     */
    void close() throws DatabaseException;

    /**
     * Get the number of columns in this record set
     * @return  the number of columns in the resultset
     * @throws DatabaseException
     */
    int getColumnCount() throws DatabaseException;

    /**
     * Fetch the column label for the desired column.  The label is intended to be a visual
     * string and may have no relation to the actual database table.
     * @param column  the column to fetch the label of
     * @return the column label
     * @throws DatabaseException
     */
    String getColumnLabel(int column) throws DatabaseException;

    /**
     * Fetch the column name for the desired column.  The name can be used to retrieve
     * values from this record using the extract*(String) methods. 
     * @param column  the column to fetch the name of
     * @return the column name
     * @throws DatabaseException
     */
    String getColumnName(int column) throws DatabaseException;

    /**
     * Fetch the SQL data type for the desired column.
     * @param column  the column to fetch the type of
     * @return        the SQL data type for the column
     * @throws DatabaseException
     */
    SqlType getColumnType(int column) throws DatabaseException;

    /**
     * Fetch the designated column's normal maximum width of characters.
     * @param column  the column to fetch the size of
     * @return the display size for the column
     * @throws DatabaseException
     */
    int getColumnDisplaySize(int column) throws DatabaseException;

    /**
     * Map the column name in the result set to its index number (1-based) within
     * the result set.  
     * @param columnName  the name of the column
     * @return            the (1-based) column index of the given column name, 
     *                    or -1 if the column cannot be found
     * @throws DatabaseException
     */
    int getColumnIndex(String columnName) throws DatabaseException;

    /**
     * Moves the resultset to the first record.
     * @return  True if the resultset was moved to the first record, False if there are no records.
     * @throws DatabaseException
     */
    boolean moveFirst() throws DatabaseException;

    /**
     * Moves the resultset to the next records.
     * @return  True if the resultset was moved to the next record, False if there is no next record.
     * @throws DatabaseException
     */
    boolean moveNext() throws DatabaseException;

    /**
     * Returns whether the last fetched result was null.
     * @return  True if the last fetched result was null.
     * @throws DatabaseException
     */
    boolean wasLastFetchNull() throws DatabaseException;

    /**
     * Moves to the specified row.
     * @param row  the row index (1-based)
     * @return     True if the move was successful; False if the row doesn't exist
     */
    boolean moveToRow(int row) throws DatabaseException;

    /**
     * Determine if the result set has a record at the given row
     * @param row the row
     * @return boolean true if the cursor has been moved forward to the given row (it exists)
     */
    boolean recordAt(int row);

    /**
     * Return the record at the given row in the ResultSet
     * @param row the row
     * @return RecordPlaceholder a placeholder for the record
     */
    RecordPlaceholder resultGetRecord(int row) throws DatabaseException;

    /**
     * Returns the string value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the string value of the column for the current row
     * @throws DatabaseException
     */
    String getCurrentRowString(int colIndex) throws DatabaseException;

    /**
     * Returns the string value of the specified column for the current row.
     * @param colName  the column name
     * @return          the string value of the column for the current row
     * @throws DatabaseException
     */
    String getCurrentRowString(String colName) throws DatabaseException;

    /**
     * Returns the int value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the int value of the column for the current row
     * @throws DatabaseException
     */
    int getCurrentRowInt(int colIndex) throws DatabaseException;

    /**
     * Returns the long value of the specified column for the current row.
     * @param colName  the column name
     * @return         the long value of the column for the current row
     * @throws DatabaseException
     */
    long getCurrentRowLong(String colName) throws DatabaseException;

    /**
     * Returns the long value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the long value of the column for the current row
     * @throws DatabaseException
     */
    long getCurrentRowLong(int colIndex) throws DatabaseException;

    /**
     * Returns the int value of the specified column for the current row.
     * @param colName  the column name
     * @return          the int value of the column for the current row
     * @throws DatabaseException
     */
    int getCurrentRowInt(String colName) throws DatabaseException;

    /**
     * Returns the double value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the double value of the column for the current row
     * @throws DatabaseException
     */
    double getCurrentRowDouble(int colIndex) throws DatabaseException;

    /**
     * Returns the double value of the specified column for the current row.
     * @param colName  the column name
     * @return         the double value of the column for the current row
     * @throws DatabaseException
     */
    double getCurrentRowDouble(String colName) throws DatabaseException;

    /**
     * Returns the Decimal value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the Decimal value of the column for the current row
     * @throws DatabaseException
     */
    BigDecimal getCurrentRowDecimal(int colIndex) throws DatabaseException;

    /**
     * Returns the Decimal value of the specified column for the current row.
     * @param colName  the column name
     * @return         the Decimal value of the column for the current row
     * @throws DatabaseException
     */
    BigDecimal getCurrentRowDecimal(String colName) throws DatabaseException;

    /**
     * Returns the date value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the date value of the column for the current row
     * @throws DatabaseException
     */
    Date getCurrentRowDate(int colIndex) throws DatabaseException;

    /**
     * Returns the date value of the specified column for the current row.
     * @param colName  the column name
     * @return          the date value of the column for the current row
     * @throws DatabaseException
     */
    Date getCurrentRowDate(String colName) throws DatabaseException;

    /**
     * Returns the time value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the time value of the column for the current row
     * @throws DatabaseException
     */
    Time getCurrentRowTime(int colIndex) throws DatabaseException;

    /**
     * Returns the time value of the specified column for the current row.
     * @param colName  the column name
     * @return          the time value of the column for the current row
     * @throws DatabaseException
     */
    Time getCurrentRowTime(String colName) throws DatabaseException;

    /**
     * Returns the boolean value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the boolean value of the column for the current row
     * @throws DatabaseException
     */
    boolean getCurrentRowBoolean(int colIndex) throws DatabaseException;

    /**
     * Returns the boolean value of the specified column for the current row.
     * @param colName  the column name
     * @return          the boolean value of the column for the current row
     * @throws DatabaseException
     */
    boolean getCurrentRowBoolean(String colName) throws DatabaseException;

    /**
     * Returns the binary data value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the binary data value of the column for the current row
     * @throws DatabaseException
     */
    byte[] getCurrentRowBytes(int colIndex) throws DatabaseException;

    /**
     * Returns the binary data value of the specified column for the current row.
     * @param colName  the column name
     * @return          the binary data value of the column for the current row
     * @throws DatabaseException
     */
    byte[] getCurrentRowBytes(String colName) throws DatabaseException;

    /**
     * Returns the object value of the specified column for the current row.
     * @param colIndex  the column index (1-based)
     * @return          the object value of the column for the current row
     * @throws DatabaseException
     */
    Object getCurrentRowObject(int colIndex) throws DatabaseException;

    /**
     * Returns the object value of the specified column for the current row.
     * @param colName  the column name
     * @return          the object value of the column for the current row
     * @throws DatabaseException
     */
    Object getCurrentRowObject(String colName) throws DatabaseException;
}
