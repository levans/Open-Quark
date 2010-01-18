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
 * TableDescription.java
 * Creation date (Feb 24, 2006).
 * By: Richard Webster
 */
package org.openquark.util.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The metadata for a database table, including field and constraint info.
 */
public class TableDescription {

    /** An identifier of the table in the database. */
    private final TableReference tableRef;

    /** Information about the fields in the table. */
    private final List<FieldDescription> fields;

    /** Information about constraints on the data. */
    private final List<TableConstraint> constraints;

    /**
     * TableDescription constructor.
     */
    public TableDescription(TableReference tableRef, List<FieldDescription> fields, List<TableConstraint> constraints) {
        if (tableRef == null) {
            throw new NullPointerException("The table reference must not be null.");
        }
        if (fields == null) {
            throw new NullPointerException("The field list must not be null.");
        }
        if (constraints == null) {
            throw new NullPointerException("The constraints list must not be null.");
        }

        this.tableRef = tableRef;
        this.fields = new ArrayList<FieldDescription>(fields);
        this.constraints = new ArrayList<TableConstraint>(constraints);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TableDescription: " + tableRef.getTableName()
            + "\nFields:\n  " + fields
            + "\nConstraints:\n  " + constraints;
    }

    /**
     * Returns the table reference.
     */
    public TableReference getTable() {
        return tableRef;
    }

    /**
     * Returns the name of the table.
     */
    public String getTableName() {
        return tableRef.getTableName();
    }

    /**
     * Returns information about the fields in the table.
     */
    public List<FieldDescription> getFields() {
        return Collections.unmodifiableList(fields);
    }

    /**
     * Returns information about the constraints on a table.
     */
    public List<TableConstraint> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }
}
