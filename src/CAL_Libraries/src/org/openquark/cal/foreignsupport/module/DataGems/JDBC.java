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
 * JDBC.java
 * Creation: Aug 15, 2002 at 10:52:13 AM
 * By: LEvans 
 */
package org.openquark.cal.foreignsupport.module.DataGems;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.apache.log4j.Logger;
import org.openquark.util.ByteArrays;
import org.openquark.util.database.SqlType;
import org.openquark.util.datadictionary.ValueType;
import org.openquark.util.time.Time;


/** 
 * The JDBC class provides primitive foreign functions for SQL database access
 *
 * <p>
 * Creation: Aug 15, 2002 at 10:52:13 AM
 */
public class JDBC {
    
    private static final Logger logger = Logger.getLogger(JDBC.class);

//    static public QueryResult makeQueryResult (ResultSet sqlResultSet) {
//        return new Connection.JDBCQueryResult (sqlResultSet);
//    }
    
    /**
     * Wrapper around a JDBC Connection to hold specfic connection-oriented 
     * properties for JDBC use in Quark.
     * @author LEvans
     *
    */
    public static class Connection {
        
        /**
         * A standard implementation of the <code>JDBCPreparedStatement</code>
         * interface.
         */
        public static class JDBCPreparedStatementImpl implements JDBCPreparedStatement {
            
            /** The underlying SQL prepared statement */
            private final PreparedStatement statement;
            
            /** The original SQL statement */
            private String originalSql;
            
            /** Calculate the time used for executing batches and updates */
            private long executionTime = 0;
            
            public JDBCPreparedStatementImpl(String stmt, java.sql.Connection conn) throws DatabaseException {
                try {
                    this.originalSql = stmt;
                    this.statement = conn.prepareStatement(stmt);
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#getSQLStatement()
             */
            public String getSQLStatement() {
                return originalSql;
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#setBoolean(int, boolean)
             */
            public JDBCPreparedStatement setBoolean(int parameterIndex, boolean x) throws DatabaseException {
                try {
                    statement.setBoolean(parameterIndex, x);
                    return this;
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#setInt(int, int)
             */
            public JDBCPreparedStatement setInt(int parameterIndex, int x) throws DatabaseException {
                try {
                    statement.setInt(parameterIndex, x);
                    return this;
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#setDouble(int, double)
             */
            public JDBCPreparedStatement setDouble(int parameterIndex, double x) throws DatabaseException {
                try {
                    statement.setDouble(parameterIndex, x);
                    return this;
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#setString(int, java.lang.String)
             */
            public JDBCPreparedStatement setString(int parameterIndex, String x) throws DatabaseException {
                try {
                    statement.setString(parameterIndex, x);
                    return this;
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#setTime(int, org.openquark.util.time.Time)
             */
            public JDBCPreparedStatement setTime(int parameterIndex, Time x) throws DatabaseException {
                try {
                    Timestamp tstamp = new Timestamp(x.toDate().getTime());
                    statement.setTimestamp(parameterIndex, tstamp);
                    return this;
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#setNull(int, org.openquark.util.datadictionary.ValueType)
             */
            public JDBCPreparedStatement setNull(int parameterIndex, ValueType valueType) throws DatabaseException {
                // TODO instead of having ValueType, should we create a SQLValueType?
                try {
                    switch (valueType.value()) {
                    case ValueType._binaryType:
                        statement.setNull(parameterIndex, Types.BOOLEAN); break;
                    case ValueType._intType:
                        statement.setNull(parameterIndex, Types.INTEGER); break;
                    case ValueType._doubleType:
                        statement.setNull(parameterIndex, Types.DOUBLE); break;
                    case ValueType._stringType:
                        statement.setNull(parameterIndex, Types.VARCHAR); break; // TODO is this database specific?
                    case ValueType._timeType:
                        statement.setNull(parameterIndex, Types.TIMESTAMP); break; // TODO is this database specific?
                    default:
                        statement.setNull(parameterIndex, Types.NULL); break;
                    }
                    return this;
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#clearParameters()
             */
            public void clearParameters() throws DatabaseException {
                long start = System.currentTimeMillis();
                try {
                    statement.clearParameters();
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                } finally {
                    executionTime += (System.currentTimeMillis() - start);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#addBatch()
             */
            public boolean addBatch() throws DatabaseException {
                long start = System.currentTimeMillis();
                try {
                    statement.addBatch();
                    return true;
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                } finally {
                    executionTime += (System.currentTimeMillis() - start);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#executeBatch()
             */
            public int[] executeBatch() throws DatabaseException {
                long startTime = System.currentTimeMillis();
                try {
                    //long freeMem = Runtime.getRuntime().freeMemory();
                    //logger.info("Executing batch... " + freeMem + " bytes free");
                    logger.info("Executing batch");
                    return statement.executeBatch();
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                } finally {
                    long endTime = System.currentTimeMillis();
                    executionTime += (endTime - startTime);
                    logger.info("Time to execute batch: " + (endTime - startTime) + " ms");
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#executeUpdate()
             */
            public int executeUpdate() throws DatabaseException {
                long startTime = System.currentTimeMillis();
                try {
                    logger.info("Executing update");
                    return statement.executeUpdate();
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                } finally {
                    long endTime = System.currentTimeMillis();
                    executionTime += (endTime - startTime);
                    logger.info("Time to execute update: " + (endTime - startTime) + " ms");
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#close()
             */
            public void close() throws DatabaseException {
                try {
                    statement.close();
                } catch (SQLException sqle) {
                    throw new DatabaseException(sqle);
                }
            }
            
            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.JDBCPreparedStatement#getTotalExecutionTime()
             */
            public long getTotalExecutionTime() {
                return executionTime;
            }
        }
        
        /**
         * QueryResult is a wrapper around a JDBC result set. This wrapper
         * provides flexibility when referring to some underlying results, plus
         * acts as a parent object for subsequent creation of RecordPlaceholders
         * within this result set.
         *
         * <p> Creation: Aug 22, 2002 at 9:51:08 AM
         */
        public static class JDBCQueryResult implements QueryResult {
            /** The JDBC resultset. */
            private ResultSet sqlResults;
 
            /**
             * Construct a JDBCQueryResult from a ResultSet
             * @param sqlResults the underlying ResultSet
             */
            private JDBCQueryResult(ResultSet sqlResults) {
                this.sqlResults = sqlResults;
            }

            /**
             * @see java.lang.Object#toString()
             */
            @Override
            public String toString() {
                return "JDBC Resultset";
            }

            /**
             * @see org.openquark.cal.foreignsupport.module.DataGems.QueryResult#close()
             */
            public void close() throws DatabaseException {
                try {
                    sqlResults.close();
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Moves to the specified row.
             */
            public boolean moveToRow(int row) throws DatabaseException {
                try {
                    return sqlResults.absolute(row);
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Determine if the result set has a record at the given row
             * @param row the row
             * @return boolean true if the cursor has been moved forward to the given row (it exists)
             */
            public boolean recordAt(int row) {
                // Return whether the resultset has been exhausted (the cursor is after the last record)
                // If a SQL error occurs, we report the end of the result set (return false)
                try {
                    return moveToRow(row);
                } catch (DatabaseException e) {
                    // An error occured, report no more records
                    return false;
                }
            }

            /**
             * Return the record at the given row in the ResultSet
             * @param row the row
             * @return RecordPlaceholder a placeholder for the record
             */
            public RecordPlaceholder resultGetRecord(int row) throws DatabaseException {
                // Return a RecordPlaceholder for the current place in the resultSet
                return new RecordPlaceholder(this, row);
            }

            /**
             * Moves the resultset to the first record.
             * @return  True if the resultset was moved to the first record, False if there are no records.
             * @throws DatabaseException
             */
            public boolean moveFirst() throws DatabaseException {
//                boolean moveResult = getSqlResults().first();
                // The moveToRow() method handles forward-only rowsets better.
                boolean moveResult = moveToRow(1);

                logger.debug("moveFirst " + moveResult);

                return moveResult;
            }

            /**
             * Moves the resultset to the next records.
             * @return  True if the resultset was moved to the next record, False if there is no next record.
             * @throws DatabaseException
             */
            public boolean moveNext() throws DatabaseException {
                try {
                    boolean moveResult = sqlResults.next();

                    logger.debug("moveNext " + moveResult);

                    return moveResult;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns whether the last fetched result was null.
             * @return  True if the last fetched result was null.
             * @throws DatabaseException
             */
            public boolean wasLastFetchNull() throws DatabaseException {
                try {
                    return sqlResults.wasNull();
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
            
            /**
             * Returns the string value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the string value of the column for the current row
             * @throws DatabaseException
             */
            public String getCurrentRowString(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    String strVal = sqlResults.getString(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        strVal = Connection.NULL_STRING;
                    }

                    logger.debug("getCurrentRowString (col = " + colIndex + ") = " + strVal);

                    // Return value
                    return strVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the string value of the specified column for the current row.
             * @param colName  the column name
             * @return          the string value of the column for the current row
             * @throws DatabaseException
             */
            public String getCurrentRowString(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    String strVal = sqlResults.getString(colName);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        strVal = Connection.NULL_STRING;
                    }

                    logger.debug("getCurrentRowString (col = " + colName + ") = " + strVal);

                    // Return value
                    return strVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the int value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the int value of the column for the current row
             * @throws DatabaseException
             */
            public int getCurrentRowInt(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    int intVal = sqlResults.getInt(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        intVal = Connection.NULL_INT;
                    }

                    logger.debug("getCurrentRowInt (col = " + colIndex + ") = " + intVal);

                    // Return value
                    return intVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the int value of the specified column for the current row.
             * @param colName  the column name
             * @return          the int value of the column for the current row
             * @throws DatabaseException
             */
            public int getCurrentRowInt(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    int intVal = sqlResults.getInt(colName);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        intVal = Connection.NULL_INT;
                    }

                    logger.debug("getCurrentRowInt (col = " + colName + ") = " + intVal);

                    // Return value
                    return intVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the long value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the long value of the column for the current row
             * @throws DatabaseException
             */
            public long getCurrentRowLong(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                	long longVal = sqlResults.getLong(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                    	longVal = Connection.NULL_LONG;
                    }

                    logger.debug("getCurrentRowLong (col = " + colIndex + ") = " + longVal);

                    // Return value
                    return longVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the long value of the specified column for the current row.
             * @param colName  the column name
             * @return         the long value of the column for the current row
             * @throws DatabaseException
             */
            public long getCurrentRowLong(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    long longVal = sqlResults.getLong(colName);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                    	longVal = Connection.NULL_LONG;
                    }

                    logger.debug("getCurrentRowLong (col = " + colName + ") = " + longVal);

                    // Return value
                    return longVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the double value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the double value of the column for the current row
             * @throws DatabaseException
             */
            public double getCurrentRowDouble(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    double doubleVal = sqlResults.getDouble(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        doubleVal = Connection.NULL_DOUBLE;
                    }

                    logger.debug("getCurrentRowDouble (col = " + colIndex + ") = " + doubleVal);

                    // Return value
                    return doubleVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the double value of the specified column for the current row.
             * @param colName  the column name
             * @return          the double value of the column for the current row
             * @throws DatabaseException
             */
            public double getCurrentRowDouble(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    double doubleVal = sqlResults.getDouble(colName);
                    
                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        doubleVal = Connection.NULL_DOUBLE;
                    }

                    logger.debug("getCurrentRowDouble (col = " + colName + ") = " + doubleVal);

                    // Return value
                    return doubleVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the Decimal value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the Decimal value of the column for the current row
             * @throws DatabaseException
             */
            public BigDecimal getCurrentRowDecimal(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                	BigDecimal decimalVal = sqlResults.getBigDecimal(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                    	decimalVal = Connection.NULL_DECIMAL;
                    }

                    logger.debug("getCurrentRowDecimal (col = " + colIndex + ") = " + decimalVal);

                    // Return value
                    return decimalVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the Decimal value of the specified column for the current row.
             * @param colName  the column name
             * @return          the Decimal value of the column for the current row
             * @throws DatabaseException
             */
            public BigDecimal getCurrentRowDecimal(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    BigDecimal decimalVal = sqlResults.getBigDecimal(colName);
                    
                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                    	decimalVal = Connection.NULL_DECIMAL;
                    }

                    logger.debug("getCurrentRowDecimal (col = " + colName + ") = " + decimalVal);

                    // Return value
                    return decimalVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the date value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the date value of the column for the current row
             * @throws DatabaseException
             */
            public Date getCurrentRowDate(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    Date dateVal = sqlResults.getDate(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        dateVal = Connection.NULL_DATE;
                    }

                    logger.debug("getCurrentRowDate (col = " + colIndex + ") = " + dateVal);

                    // Return value
                    return dateVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the date value of the specified column for the current row.
             * @param colName  the column name
             * @return          the date value of the column for the current row
             * @throws DatabaseException
             */
            public Date getCurrentRowDate(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    Date dateVal = sqlResults.getDate(colName);
                    
                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        dateVal = Connection.NULL_DATE;
                    }

                    logger.debug("getCurrentRowDate (col = " + colName + ") = " + dateVal);

                    // Return value
                    return dateVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the time value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the time value of the column for the current row
             * @throws DatabaseException
             */
            public Time getCurrentRowTime(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    Timestamp timestampVal = sqlResults.getTimestamp(colIndex);
                    Time timeVal = (timestampVal == null) ? null : Time.fromTimeStamp(timestampVal);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        timeVal = Connection.NULL_TIME;
                    }

                    logger.debug("getCurrentRowTime (col = " + colIndex + ") = " + timeVal);

                    // Return value
                    return timeVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the time value of the specified column for the current row.
             * @param colName  the column name
             * @return          the time value of the column for the current row
             * @throws DatabaseException
             */
            public Time getCurrentRowTime(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    Timestamp timestampVal = sqlResults.getTimestamp(colName);
                    Time timeVal = (timestampVal == null) ? null : Time.fromTimeStamp(timestampVal);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        timeVal = Connection.NULL_TIME;
                    }

                    logger.debug("getCurrentRowTime (col = " + colName + ") = " + timeVal);
                        
                    // Return value
                    return timeVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the boolean value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the boolean value of the column for the current row
             * @throws DatabaseException
             */
            public boolean getCurrentRowBoolean(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    boolean boolVal = sqlResults.getBoolean(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        boolVal = Connection.NULL_BOOLEAN;
                    }

                    logger.debug("getCurrentRowBoolean (col = " + colIndex + ") = " + boolVal);

                    // Return value
                    return boolVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the boolean value of the specified column for the current row.
             * @param colName  the column name
             * @return          the boolean value of the column for the current row
             * @throws DatabaseException
             */
            public boolean getCurrentRowBoolean(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    boolean boolVal = sqlResults.getBoolean(colName);
                    
                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        boolVal = Connection.NULL_BOOLEAN;
                    }

                    logger.debug("getCurrentRowBoolean (col = " + colName + ") = " + boolVal);

                    // Return value
                    return boolVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the binary data value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the binary data value of the column for the current row
             * @throws DatabaseException
             */
            public byte[] getCurrentRowBytes(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    byte[] bytesVal = sqlResults.getBytes(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        bytesVal = Connection.NULL_BYTES;
                    }

                    logger.debug("getCurrentRowBytes (col = " + colIndex + ") = " + ByteArrays.byteArrayToHexString(bytesVal));

                    // Return value
                    return bytesVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the binary data value of the specified column for the current row.
             * @param colName  the column name
             * @return          the binary data value of the column for the current row
             * @throws DatabaseException
             */
            public byte[] getCurrentRowBytes(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    byte[] bytesVal = sqlResults.getBytes(colName);
                    
                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        bytesVal = Connection.NULL_BYTES;
                    }

                    logger.debug("getCurrentRowBytes (col = " + colName + ") = " + ByteArrays.byteArrayToHexString(bytesVal));

                    // Return value
                    return bytesVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the object value of the specified column for the current row.
             * @param colIndex  the column index (1-based)
             * @return          the object value of the column for the current row
             * @throws DatabaseException
             */
            public Object getCurrentRowObject(int colIndex) throws DatabaseException {
                try {
                    // Get the extracted value
                    Object objVal = sqlResults.getObject(colIndex);

                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        objVal = Connection.NULL_OBJECT;
                    }

                    logger.debug("getCurrentRowObject (col = " + colIndex + ") = " + objVal);

                    // Return value
                    return objVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Returns the object value of the specified column for the current row.
             * @param colName  the column name
             * @return          the object value of the column for the current row
             * @throws DatabaseException
             */
            public Object getCurrentRowObject(String colName) throws DatabaseException {
                try {
                    // Get the extracted value
                    Object objVal = sqlResults.getObject(colName);
                    
                    // Check whether the value was null.
                    if (sqlResults.wasNull()) {
                        objVal = Connection.NULL_OBJECT;
                    }

                    logger.debug("getCurrentRowObject (col = " + colName + ") = " + objVal);

                    // Return value
                    return objVal;
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Get the number of columns in this record set
             * @return String the result
             */
            public int getColumnCount() throws DatabaseException {
                try {
                    // Get the list of values
                    return sqlResults.getMetaData().getColumnCount();
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Fetch the column label for the desired column.  The label is intended to be a visual
             * string and may have no relation to the actual database table.
             * @param column the column to fetch the label of
             * @return String the result
             */
            public String getColumnLabel(int column) throws DatabaseException {
                try {
                    // Get the list of values
                    return sqlResults.getMetaData().getColumnLabel(column);
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Fetch the column name for the desired column.  The name can be used to retrieve
             * values from this record using the extract*(String) methods. 
             * @param column the column to fetch the name of
             * @return String the result
             */
            public String getColumnName(int column) throws DatabaseException {
                try {
                    // Get the list of values
                    return sqlResults.getMetaData().getColumnName(column);
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
            
            /**
             * Fetch the SQL data type for the desired column.
             * @param column  the column to fetch the type of
             * @return        the SQL data type for the column
             * @throws DatabaseException
             */
            public SqlType getColumnType(int column) throws DatabaseException {
                try {
                    ResultSetMetaData rsMetadata = sqlResults.getMetaData();
                    int jdbcType = rsMetadata.getColumnType(column);

                    switch (jdbcType) {
                    case Types.BIT :            return new SqlType.SqlType_Bit();
                    case Types.TINYINT :        return new SqlType.SqlType_TinyInt();
                    case Types.SMALLINT :       return new SqlType.SqlType_SmallInt();
                    case Types.INTEGER :        return new SqlType.SqlType_Integer();
                    case Types.BIGINT :         return new SqlType.SqlType_BigInt();
                    case Types.FLOAT :          return new SqlType.SqlType_Float();
                    case Types.REAL :           return new SqlType.SqlType_Real();
                    case Types.DOUBLE :         return new SqlType.SqlType_Double();
                    case Types.NUMERIC :        return new SqlType.SqlType_Numeric(rsMetadata.getPrecision(column), rsMetadata.getScale(column));
                    case Types.DECIMAL :        return new SqlType.SqlType_Decimal(rsMetadata.getPrecision(column), rsMetadata.getScale(column));
                    case Types.CHAR :           return new SqlType.SqlType_Char(rsMetadata.getColumnDisplaySize(column));
                    case Types.VARCHAR :        return new SqlType.SqlType_VarChar(rsMetadata.getColumnDisplaySize(column));
                    case Types.LONGVARCHAR :    return new SqlType.SqlType_LongVarChar();
                    case Types.DATE :           return new SqlType.SqlType_Date();
                    case Types.TIME :           return new SqlType.SqlType_Time();
                    case Types.TIMESTAMP :      return new SqlType.SqlType_TimeStamp();
                    case Types.BINARY :         return new SqlType.SqlType_Binary(rsMetadata.getColumnDisplaySize(column));     // TODO: is this the correct length?
                    case Types.VARBINARY :      return new SqlType.SqlType_VarBinary(rsMetadata.getColumnDisplaySize(column));  // TODO: is this the correct length?
                    case Types.LONGVARBINARY :  return new SqlType.SqlType_LongVarBinary();
                    case Types.NULL :           return new SqlType.SqlType_Null();
                    case Types.BLOB :           return new SqlType.SqlType_Blob();
                    case Types.CLOB :           return new SqlType.SqlType_Clob();
                    case Types.BOOLEAN :        return new SqlType.SqlType_Boolean();
                    case Types.OTHER :          return new SqlType.SqlType_Other();
                    case Types.JAVA_OBJECT :    return new SqlType.SqlType_JavaObject();
                    case Types.DISTINCT :       return new SqlType.SqlType_Distinct();
                    case Types.STRUCT :         return new SqlType.SqlType_Struct();
                    case Types.ARRAY :          return new SqlType.SqlType_Array();
                    case Types.REF :            return new SqlType.SqlType_Ref();
                    case Types.DATALINK :       return new SqlType.SqlType_Datalink();
                    default :                   return new SqlType.SqlType_Other();
                    }
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Fetch the designated column's normal maximum width of characters.
             * @param column the column to fetch the size of
             * @return int the result
             * @see java.sql.Types
             */
            public int getColumnDisplaySize(int column) throws DatabaseException {
                try {
                    return sqlResults.getMetaData().getColumnDisplaySize(column);
                }
                catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }

            /**
             * Map the column name in the result set to its index number (1-based) within
             * the result set.  
             * @param columnName  the name of the column
             * @return            the (1-based) column index of the given column name, or -1 if the column cannot be found
             */
            public int getColumnIndex(String columnName) throws DatabaseException {
                try {
                    int columnIndex = sqlResults.findColumn(columnName);

                    logger.debug("getColumnIndex: '" + columnName + "' = " + columnIndex);
                    
                    return columnIndex;
                }
                catch (SQLException e) {
                    return -1;
                }
            }
        }

        // The JDBC (underlying) connection
        private java.sql.Connection jdbcConnection;
        private Statement jdbcStatement;

        // Static singletons
        private static Date baseDate = new Date(0);
        private static Time baseTime = new Time(0);

        // Default null substitutions
        private static final String NULL_STRING = "";
        private static final int NULL_INT = Integer.MIN_VALUE;
        private static final long NULL_LONG = Long.MIN_VALUE;
        private static final double NULL_DOUBLE = Double.NaN;
        private static final BigDecimal NULL_DECIMAL = BigDecimal.ZERO;
        private static final Date NULL_DATE = baseDate;
        private static final Time NULL_TIME = baseTime;
        private static final Object NULL_OBJECT = null;
        private static final boolean NULL_BOOLEAN = false;
        private static final byte[] NULL_BYTES = new byte[0];

        /**
         * Construct an a Connection from and underlying JDBC Connection
         * @param jdbcConnection the JDBC Connection object
             */
        public Connection(java.sql.Connection jdbcConnection) {
            this.jdbcConnection = jdbcConnection;
            this.jdbcStatement = null;
        }

        /**
         * Returns the jdbcConnection.
         * @return java.sql.Connection the underlying JDBC connection
         */
        private java.sql.Connection getJdbcConnection() {
            return jdbcConnection;
        }
        
        /**
         * Returns a (cached) jdbcStatement.
         * @return java.sql.Statement a statement that can be used to execute
         * SQL.
         */
        private java.sql.Statement getJdbcStatement() throws SQLException {
            if(jdbcStatement == null) {
                jdbcStatement = getJdbcConnection().createStatement();
            }
            
            return jdbcStatement;
        }

        /**
         * Sets the auto-commit flag.
         * @param flag
         * @throws DatabaseException
         */
        public Connection setAutoCommit(boolean flag) throws DatabaseException {
            try {
                getJdbcConnection().setAutoCommit(flag);
                return this;
            } catch (SQLException sqle) {
                throw new DatabaseException(sqle);
            }
        }
        
        /**
         * Gets the auto-commit flag.
         * @return boolean true if the auto-commit flag is set for this connection. 
         * @throws DatabaseException
         */
        public boolean getAutoCommit() throws DatabaseException {
            try {
                return getJdbcConnection().getAutoCommit();
            } catch (SQLException sqle) {
                throw new DatabaseException(sqle);
            }
        }
        
        /**
         * Closes an existing database connection.
         * @throws DatabaseException
         */
        public void close() throws DatabaseException {
            try {
                java.sql.Connection conn = getJdbcConnection();
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                throw new DatabaseException(sqle);
            }
        }

        /**
         * Perform a SQL query on a given connection, from a textual SQL
         * statement
         * @param sqluery the query
         * @return the result set
         * @throws DatabaseException
         */
        public QueryResult queryFromSQLString(final String sqluery) throws DatabaseException {
            try {
                final Statement stmt = getJdbcConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

                return new RestartableQueryResults() {
                    @Override
                    protected QueryResult createResultSet() throws DatabaseException {
                        long startTime = System.currentTimeMillis();
                        try {
                            logger.info("Executing SQL:\n" + sqluery);
                            return new JDBCQueryResult(stmt.executeQuery(sqluery));
                        }
                        catch (SQLException e) {
                            throw new DatabaseException(e);
                        }
                        finally {
                            long endTime = System.currentTimeMillis();
                            logger.info("Time to execute query: " + (endTime - startTime) + " ms");
                        }
                    }};
            }
            catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
        
        /**
         * Creates a prepared Statement from the given SQL string.
         * @param sql
         * @return JDBCPreparedStatement
         * @throws DatabaseException
         */
        public JDBCPreparedStatement createPreparedStatement(String sql) throws DatabaseException {
            return new JDBCPreparedStatementImpl(sql, jdbcConnection);
        }

        /**
         * Run the specified update query on the connection.
         * @param updateSQL  the update query
         * @return           the number of rows affected
         * @throws DatabaseException
         */
        public int executeUpdate(String updateSQL) throws DatabaseException {
            long start = System.currentTimeMillis();
            try {
                logger.info("Executing update SQL: " + updateSQL);
                Statement stmt = getJdbcStatement();
                return stmt.executeUpdate(updateSQL);
            } catch (SQLException e) {
                throw new DatabaseException(e);
            } finally {
                long end = System.currentTimeMillis();
                logger.info("Time to execute update: " + (end - start) + " ms");
            }
        }
        
        /**
         * Adds the given SQL query to the connection.
         * @param sql
         * @return boolean
         */
        public boolean addBatch(String sql) throws DatabaseException {
            try {
                logger.debug("Batching SQL: " + sql);
                Statement stmt = getJdbcStatement();
                stmt.addBatch(sql);
                return true;
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
        
        /**
         * Executes all queries that are batched but not yet executed.
         * @return int[]
         */
        public int[] executeBatch() throws DatabaseException {
            long startTime = System.currentTimeMillis();
            try {
                //long freeMem = Runtime.getRuntime().freeMemory();
                //logger.info("Executing batch... " + freeMem + " bytes free");
                logger.info("Executing batch");
                Statement stmt = getJdbcStatement();
                return stmt.executeBatch();
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
            finally {
                long endTime = System.currentTimeMillis();
                logger.info("Time to execute batch: " + (endTime - startTime) + " ms");
            }
        }

        /**
         * Commits the changes made through the JDBC connection.
         * @return boolean
         * @throws DatabaseException
         */
        public boolean commit() throws DatabaseException {
            long start = System.currentTimeMillis();
            try {
                jdbcConnection.commit();
                return true;
            } catch (SQLException e) {
                throw new DatabaseException(e);
            } finally {
                long end = System.currentTimeMillis();
                logger.debug("Time to commit: " + (end - start) + " ms");
            }
        }
        
        /**
         * Rolls back the changes made through the JDBC connection.
         * @return boolean
         * @throws DatabaseException
         */
        public boolean rollback() throws DatabaseException {
            long start = System.currentTimeMillis();
            try {
                // Only rollback connections that are not set to auto-commit. 
                if (!getAutoCommit()) {
                    jdbcConnection.rollback();
                }
                return true;
            } catch (SQLException e) {
                throw new DatabaseException(e);
            } finally {
                long end = System.currentTimeMillis();
                logger.debug("Time to rollback: " + (end - start) + " ms");
            }
        }

        /**
         * Returns the name of the database product to which this connection is connected. 
         */
        public String getDatabaseProductName() throws DatabaseException {
            try {
                return jdbcConnection.getMetaData().getDatabaseProductName();
            }
            catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }

        /**
         * Returns a resultset containing information about tables in the connection.
         */
        public QueryResult getTablesInfo() {
            // TODO: add params to filter based on catalog, schema, table type, etc...
            return new RestartableQueryResults() {
                @Override
                protected QueryResult createResultSet() throws DatabaseException {
                    try {
                        String[] tableTypes = new String[] { "TABLE", "VIEW", "ALIAS", "SYNONYM" };
                        ResultSet rs = getJdbcConnection().getMetaData().getTables(null, null, null, tableTypes);
                        return new JDBCQueryResult(rs);
                    }
                    catch (SQLException e) {
                        throw new DatabaseException(e);
                    }
                }
            };
        }
        
        /**
         * Method getTablesInfo
         * 
         * @param catalogName - ignored if ""
         * @param schemaPattern - ignored if ""
         * @param tableNamePattern - ignored if ""
         * 
         * @return Returns a resultset containing information about tables in the connection.
         */
        public QueryResult getTablesInfo(final String catalogName, final String schemaPattern, final String tableNamePattern) {
            return new RestartableQueryResults() {
                @Override
                protected QueryResult createResultSet() throws DatabaseException {
                    try {
                        String[] tableTypes = new String[] { "TABLE", "VIEW", "ALIAS", "SYNONYM" };
                        ResultSet rs = getJdbcConnection().getMetaData().getTables(convert (catalogName), convert (schemaPattern), convert (tableNamePattern), tableTypes);
                        return new JDBCQueryResult(rs);
                    }
                    catch (SQLException e) {
                        throw new DatabaseException(e);
                    }
                }

                private String convert (String string) {
                    return (string == null || string.length () == 0) ? null : string; 
                }
            };
        }
        /**
         * Method getConnection
         * 
         * @return Returns the underlying JDBC connection
         */
        public java.sql.Connection getConnection () {
            return jdbcConnection;
        }

        /**
         * Returns a resultset containing information about columns in a table.
         */
        public QueryResult getColumnsInfo(final String tableName) {
            // TODO: add params to filter based on catalog and schema...
            return new RestartableQueryResults() {
                @Override
                protected QueryResult createResultSet() throws DatabaseException {
                    try {
                        ResultSet rs = getJdbcConnection().getMetaData().getColumns(null, null, tableName, null);
                        return new JDBCQueryResult(rs);
                    }
                    catch (SQLException e) {
                        throw new DatabaseException(e);
                    }
                }
            };
        }

        /**
         * Returns a resultset containing information the primary key columns in a table.
         */
        public QueryResult getTablePrimaryKeyInfo(final String tableName) {
            // TODO: add params to filter based on catalog and schema...
            return new RestartableQueryResults() {
                @Override
                protected QueryResult createResultSet() throws DatabaseException {
                    try {
                        ResultSet rs = getJdbcConnection().getMetaData().getPrimaryKeys(null, null, tableName);
                        return new JDBCQueryResult(rs);
                    }
                    catch (SQLException e) {
                        throw new DatabaseException(e);
                    }
                }
            };
        }

        /**
         * Returns a resultset containing information the indexes on a table.
         */
        public QueryResult getTableIndexInfo(final String tableName, final boolean uniqueOnly) {
            // TODO: add params to filter based on catalog and schema...
            return new RestartableQueryResults() {
                @Override
                protected QueryResult createResultSet() throws DatabaseException {
                    try {
                        ResultSet rs = getJdbcConnection().getMetaData().getIndexInfo(null, null, tableName, uniqueOnly, false);
                        return new JDBCQueryResult(rs);
                    }
                    catch (SQLException e) {
                        throw new DatabaseException(e);
                    }
                }
            };
        }

        /**
         * Returns a resultset containing information the foreign key constraints on a table.
         */
        public QueryResult getTableForiegnKeyConstraintInfo(final String tableName) {
            // TODO: add params to filter based on catalog and schema...
            return new RestartableQueryResults() {
                @Override
                protected QueryResult createResultSet() throws DatabaseException {
                    try {
                        ResultSet rs = getJdbcConnection().getMetaData().getImportedKeys(null, null, tableName);
                        return new JDBCQueryResult(rs);
                    }
                    catch (SQLException e) {
                        throw new DatabaseException(e);
                    }
                }
            };
        }

//        /**
//         * Returns a list of the fields in the specified table for the connection.
//         */
//        public List getTableFieldNames(String tableName) throws SQLException {
//            // TODO: add params to filter based on catalog, schema, table type, etc...
//            // TODO: return the resultset to CAL instead...
//            ResultSet rs = getJdbcConnection().getMetaData().getColumns(null, null, tableName, null);
//
//            List fieldNames = new ArrayList();
//            while (rs.next()) {
//                String fieldName = rs.getString(4);
//                fieldNames.add(fieldName);
//            }
//
//            return fieldNames;
//        }
    }
    
    /**
     * Load a given JDBC driver
     * @param driver the driver to load e.g. "sun.jdbc.odbc.JdbcOdbcDriver"
     * @return true if the driver was loaded, false otherwise
     */
    public static boolean loadDriver(String driver) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            return false;
        } catch (ExceptionInInitializerError e) {
            return false;
        } catch (LinkageError e) {
            return false;
        } 
        return true;
    }

    /**
     * Establish a connection to a given database, given by the URL, authenticated
     * by the login and password.
     * @param url the URL identifying the database (e.g. "jdbc:odbc:MyDatabase")
     * @param login the user account valid for accessing the given database
     * @param password the password on the given account
     * @return Connection the connection object
     */
    public static Connection connect(String url, String login, String password) throws DatabaseException {
        try {
            logger.info("Connecting: url=" + url + ", userID=" + login);
            return new Connection(DriverManager.getConnection(url, login, password));
        }
        catch (SQLException e) {
            throw new DatabaseException("Failed to connect: url=" + url + ", userID=" + login, e);
        }
    }

    /**
     * Construct a JDBCQueryResult to wrap the specified JDBC resultSet.
     */
    public static QueryResult makeJDBCResultSet(java.sql.ResultSet resultSet) {
        return new Connection.JDBCQueryResult(resultSet);
    }
}
