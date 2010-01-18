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
 * FieldDescription.java
 * Creation date (Feb 24, 2006).
 * By: Richard Webster
 */
package org.openquark.util.database;


/**
 * A description of a column in a database table.
 */
public class FieldDescription {

    /** The name of the database field. */
    private final String fieldName;

    /** The SQL data type of the database field. */
    private final SqlType dataType;

    /** Indicates whether the column can hold Null values. */
    private final boolean isNullable;

    /** A comment describing this field. */
    private final String comment;

    /**
     * FieldDescription constructor.
     */
    public FieldDescription(String fieldName, SqlType dataType, boolean isNullable, String comment) {
        if (fieldName == null) {
            throw new NullPointerException("The field name must not be null.");
        }
        if (dataType == null) {
            throw new NullPointerException("The data type must not be null.");
        }
        if (comment == null) {
            throw new NullPointerException("The field comment must not be null.");
        }

        this.fieldName = fieldName;
        this.dataType = dataType;
        this.isNullable = isNullable;
        this.comment = comment;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Field: " + fieldName + " (" + dataType + ")";
    }

    /**
     * Returns the name of the field.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the data type of the field.
     */
    public SqlType getDataType() {
        return dataType;
    }

    /**
     * Returns whether values of the field can be null.
     */
    public boolean isNullable() {
        return isNullable;
    }

    /**
     * Returns the field comment.
     */
    public String getComment() {
        return comment;
    }
}
