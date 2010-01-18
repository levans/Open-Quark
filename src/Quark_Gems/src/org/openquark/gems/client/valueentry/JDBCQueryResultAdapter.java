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
 * JDBCQueryResultAdapter.java
 * Created: Oct 10, 2003
 * By: Kevin Sit
 */
package org.openquark.gems.client.valueentry;

import org.openquark.cal.foreignsupport.module.DataGems.DatabaseException;
import org.openquark.cal.foreignsupport.module.DataGems.QueryResult;
import org.openquark.cal.foreignsupport.module.DataGems.RecordPlaceholder;

/**
 * @author ksit
 */
public interface JDBCQueryResultAdapter {

    /**
     * Returns the entire result set as a single object.
     * @return QueryResult
     */
    QueryResult getResultSet();

    /**
     * Returns true if there exists a row with the given index.  Please note that
     * the row index is zero-based.
     * @return boolean
     */
    boolean hasRow(int index);

    /**
     * Returns the row pointed by the given index in the result set.  Please note
     * that the row index is zero-based.
     * @param index
     * @return JDBC.Connection.QueryResult.RecordPlaceholder 
     */
    RecordPlaceholder getRow(int index) throws DatabaseException;
}
