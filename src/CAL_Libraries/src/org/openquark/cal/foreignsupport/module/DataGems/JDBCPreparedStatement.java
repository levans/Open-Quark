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
 * JDBCPreparedStatement.java
 * Created: May 18, 2006
 * By: Kevin Sit
 */
package org.openquark.cal.foreignsupport.module.DataGems;

import org.openquark.util.datadictionary.ValueType;
import org.openquark.util.time.Time;


/**
 * 
 */
public interface JDBCPreparedStatement {
    
    /**
     * Returns the original SQL statement.
     * @return String
     */
    public String getSQLStatement();
    
    /**
     * Binds a boolean value to the prepared statement at the specified index.
     * @param parameterIndex
     * @param x
     * @return JDBCPreparedStatement
     * @throws DatabaseException
     */
    public JDBCPreparedStatement setBoolean(int parameterIndex, boolean x) throws DatabaseException;
    
    /**
     * Binds an integer value to the prepared statement at the specified index.
     * @param parameterIndex
     * @param x
     * @return JDBCPreparedStatement
     * @throws DatabaseException
     */
    public JDBCPreparedStatement setInt(int parameterIndex, int x) throws DatabaseException;
    
    /**
     * Binds a double value to the prepared statement at the specified index.
     * @param parameterIndex
     * @param x
     * @return JDBCPreparedStatement
     * @throws DatabaseException
     */
    public JDBCPreparedStatement setDouble(int parameterIndex, double x) throws DatabaseException;
    
    /**
     * Binds a string value to the prepared statement at the specified index.
     * @param parameterIndex
     * @param x
     * @return JDBCPreparedStatement
     * @throws DatabaseException
     */
    public JDBCPreparedStatement setString(int parameterIndex, String x) throws DatabaseException;
    
    /**
     * Binds a time value to the prepared statement at the specified index.
     * @param parameterIndex
     * @param x
     * @return JDBCPreparedStatement
     * @throws DatabaseException
     */
    public JDBCPreparedStatement setTime(int parameterIndex, Time x) throws DatabaseException;
    
    /**
     * Binds a null value to the prepared statement at the specified index.
     * @param parameterIndex
     * @param type
     * @return JDBCPreparedStatement
     * @throws DatabaseException
     */
    public JDBCPreparedStatement setNull(int parameterIndex, ValueType type) throws DatabaseException;
    
    /**
     * Adds a set of parameters to this <code>PreparedStatement</code>
     * object's batch of commands.
     * @return boolean
     * @throws DatabaseException
     */
    public boolean addBatch() throws DatabaseException;

    /**
     * Executes the statement and return the number of rows affected.
     * @return int
     * @throws DatabaseException
     */
    public int executeUpdate() throws DatabaseException;
    
    /**
     * Executes this statement against all the batched parameters.
     * @return int[]
     * @throws DatabaseException
     */
    public int[] executeBatch() throws DatabaseException;
    
    
    /**
     * Clears the parameters used in this statement.
     * @throws DatabaseException
     */
    public void clearParameters() throws DatabaseException;
    
    /**
     * Close the this prepared statement.
     * @throws DatabaseException
     */
    public void close() throws DatabaseException;
    
    /**
     * Returns the total time spent on executing batches and updates.  The
     * result is expressed as milli-seconds.
     * @return long
     */
    public long getTotalExecutionTime();
    
}
