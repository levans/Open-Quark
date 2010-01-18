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
 * RecordPlaceholder.java
 * Created: Oct 13, 2004
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.DataGems;

import java.util.Date;

import org.openquark.util.time.Time;


/**
 * RecordPlaceholder represents a single resultset record within a
 * QueryResult.
 */
public class RecordPlaceholder {

    /** The query results to which this record belongs. */
    private final QueryResult queryResult;
    
    /** The number of the row within the results (1-based). */
    private final int row;

    /**
     * Construct a RecordPlaceholder from the row number it represents
     * @param row the row number
     */
    public RecordPlaceholder(QueryResult queryResult, int row) {
        this.queryResult = queryResult;
        this.row = row;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Record for row " + row;
    }
    
    /**
     * Returns the record set that contains this particular record.
     * @return  the record set that contains this particular record
     */
    public QueryResult getRecordSet() {
        return queryResult;
    }

    /**
     * Extract a string from this record
     * @param column the column to extract
     * @return  the result
     */
    public String extractString(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowString(column);
    }

    /**
     * Extract a string from this record
     * @param column name of the column to extract
     * @return String the result
     */
    public String extractString(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowString(column);
    }

    /**
     * Extract an integer from this given record
     * @param column the column to extract
     * @return int the result
     */
    public int extractInt(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowInt(column);
    }

    /**
     * Extract an integer from this given record
     * @param column the name of the column to extract
     * @return int the result
     */
    public int extractInt(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowInt(column);                  
    }

    /**
     * Extract a double precision floating point value from this given record
     * @param column the column to extract
     * @return int the result
     */
    public double extractDouble(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowDouble(column);
    }

    /**
     * Extract a double precision floating point value from this given record
     * @param column the name of the column to extract
     * @return int the result
     */
    public double extractDouble(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowDouble(column);
    }

    /**
     * Extract a date from this given record
     * @param column the column to extract
     * @return Date the result
     */
    public Date extractDate(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowDate(column);
    }

    /**
     * Extract a date from this given record
     * @param column the name of the column to extract
     * @return Date the result
     */
    public Date extractDate(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowDate(column);
    }

    /**
     * Extract a time from this given record
     * @param column the column to extract
     * @return Time the result
     */
    public Time extractTime(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowTime(column);
    }

    /**
     * Extract a time from this given record
     * @param column the name of the column to extract
     * @return Time the result
     */
    public Time extractTime(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowTime(column);
    }

    /**
     * Extract an object from this given record
     * @param column the column to extract
     * @return Object the result
     */
    public Object extractObject(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowObject(column);
    }

    /**
     * Extract an object from this given record
     * @param column the name of the column to extract
     * @return Object the result
     */
    public Object extractObject(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Return value
        return queryResult.getCurrentRowObject(column);
    }

    /**
     * Extract a boolean from this given record
     * @param column the column to extract
     * @return boolean the result
     */
    public boolean extractBoolean(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowBoolean(column);
    }

    /**
     * Extract a boolean from this given record
     * @param column the name of the column to extract
     * @return boolean the result
     */
    public boolean extractBoolean(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowBoolean(column);
    }

    /**
     * Extract an array of bytes from this given record
     * @param column the column to extract
     * @return byte[] the result
     */
    public byte[] extractBytes(int column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowBytes(column);
    }

    /**
     * Extract an array of bytes from this given record
     * @param column the name of the column to extract
     * @return byte[] the result
     */
    public byte[] extractBytes(String column) throws DatabaseException {
        // Move the sql result set to this record
        queryResult.moveToRow(row);

        // Fetch the value.
        return queryResult.getCurrentRowBytes(column);
    }
}
