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
 * DatabaseField.java
 * Creation date (Oct 5, 2004).
 * By: Richard Webster
 */
package org.openquark.util.datadictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openquark.util.ObjectUtils;


/**
 * A database field.
 * It is possible for a database field to have child fields (details).
 * @author Richard Webster
 */
public class DatabaseField extends DatabaseItem implements Field {

    /** The data type of the field. */
    private final ValueType dataType;
    
    /** The field type */
    private final FieldType fieldType;

    /** The default aggregation type for the field (sum, min, max, etc...). */
    private final AggregationType defaultAggregationType;

    /** Indicates whether the underlying definition for the field includes an aggregation function. */
    private final boolean isPreaggregated;

    /**
     * DatabaseField constructor.
     * @param uniqueName              the unique name of the field
     * @param name                    the name of the database field
     * @param dataType                the data type of the database field
     * @param fieldType               the type of the field
     * @param defaultAggregationType  the default way in which values of this field should be aggregated
     * @param isPreaggregated         whether the underlying definition for the field includes an aggregation function
     * @param children                children of this database field
     */
    public DatabaseField(String uniqueName, String name, ValueType dataType, FieldType fieldType, 
                         AggregationType defaultAggregationType, boolean isPreaggregated, List <DatabaseItem> children) {
        super(uniqueName, name, children);
        this.dataType = dataType;
        this.fieldType = fieldType;
        this.defaultAggregationType = defaultAggregationType;
        this.isPreaggregated = isPreaggregated;
    }

    /**
     * DatabaseField constructor.
     * @param uniqueName  the unique name of the field
     * @param name        the name of the database field
     * @param dataType    the data type of the database field
     * @param fieldType   the type of the field
     * @param defaultAggregationType  the default way in which values of this field should be aggregated
     * @param isPreaggregated         whether the underlying definition for the field includes an aggregation function 
     */
    public DatabaseField(String uniqueName, String name, ValueType dataType, FieldType fieldType, AggregationType defaultAggregationType, boolean isPreaggregated) {
        this(uniqueName, name, dataType, fieldType, defaultAggregationType, isPreaggregated, Collections.<DatabaseItem>emptyList());
    }

    /**
     * DatabaseField constructor.
     * @param uniqueName  the unique name of the field
     * @param name        the name of the database field
     * @param dataType    the data type of the database field
     * @param fieldType   the type of the field
     * @param defaultAggregationType  the default way in which values of this field should be aggregated 
     */
    public DatabaseField(String uniqueName, String name, ValueType dataType, FieldType fieldType, AggregationType defaultAggregationType) {
        this(uniqueName, name, dataType, fieldType, defaultAggregationType, false, Collections.<DatabaseItem>emptyList());
    }

    /**
     * DatabaseField constructor.
     * @param name      the name of the database field
     * @param dataType  the data type of the database field
     * @param fieldType   the type of the field
     */
    public DatabaseField(String name, ValueType dataType, FieldType fieldType) {
        this(name, name, dataType, fieldType, bestAggregationType(dataType));
    }

    /**
     * DatabaseField constructor.
     * @param uniqueName
     * @param name      the name of the database field
     * @param dataType  the data type of the database field
     * @param fieldType   the type of the field
     */
    public DatabaseField(String uniqueName, String name, ValueType dataType, FieldType fieldType) {
        this(uniqueName, name, dataType, fieldType, bestAggregationType(dataType));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc *= dataType.hashCode();
        hc *= fieldType.hashCode();
        hc *= defaultAggregationType.hashCode();
        hc *= isPreaggregated ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
        return hc;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj.getClass().equals(DatabaseField.class))) {
            return false;
        }
        DatabaseField other = (DatabaseField) obj;
        return super.equals(obj)
                && ObjectUtils.equals(dataType, other.dataType)
                && ObjectUtils.equals(fieldType, other.fieldType)
                && ObjectUtils.equals(defaultAggregationType, other.defaultAggregationType)
                && isPreaggregated == other.isPreaggregated;
    }
    
    /**
     * @param dataType
     * @return Returns the best aggregation type for this data type.
     */
    private static AggregationType bestAggregationType(ValueType dataType) {
        if (dataType.equals(ValueType.intType) || dataType.equals(ValueType.doubleType)) {
            return AggregationType.SUM;
        }
        else {
            return AggregationType.COUNT;
        }
    }

    /**
     * Returns the data type of this database field.
     * @return the data type of this field
     */
    public ValueType getDataType() {
        return dataType;
    }

    /**
     * Returns the field type of this database field.
     * @return FieldType
     */
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * @return Returns the default aggregation type for the field (sum, min, max, etc...).
     */
    public AggregationType getDefaultAggregationType() {
        return defaultAggregationType;
    }

    /**
     * @return Returns whether the underlying definition for the field includes an aggregation function.
     */
    public boolean isPreaggregated() {
        return isPreaggregated;
    }

    /**
     * @see org.openquark.util.datadictionary.DatabaseItem#filterItem(org.openquark.util.datadictionary.DatabaseFieldFilter)
     */
    @Override
    public DatabaseItem filterItem(DatabaseFieldFilter fieldFilter) {
        // Handle the case where there is no filter.
        if (fieldFilter == null) {
            return this;
        }

        // Check whether this field should be rejected.
        if (!fieldFilter.acceptField(this)) {
            return null;
        }

        // Filter any child fields as well.
        List<DatabaseItem> filteredChildren = new ArrayList<DatabaseItem>();
        boolean childrenFiltered = false;

        for (final DatabaseItem childItem : getChildren()) {
            DatabaseItem filteredItem = childItem.filterItem(fieldFilter);
            // Keep track of whether any child item was modified.
            // Note that the objects should be compared by identity, not equivalence.
            if (filteredItem != childItem) {
                childrenFiltered = true;
            }

            if (filteredItem != null) {
                filteredChildren.add(filteredItem);
            }
        }

        return childrenFiltered 
            ? new DatabaseField(getUniqueName(), getName(), dataType, fieldType, defaultAggregationType, isPreaggregated, filteredChildren) 
            : this;
    }

    /**
     * A helper function to combine 2 field filters (either of which can be null).
     * @param f1  a field filter
     * @param f2  another field filter
     * @return a combined filter
     */
    public static DatabaseFieldFilter combineFilters(final DatabaseFieldFilter f1, final DatabaseFieldFilter f2) {
        if (f1 == null) {
            return f2;
        }
        if (f2 == null) {
            return f1;
        }
        return new DatabaseFieldFilter() {
            public boolean acceptField(DatabaseField field) {
                return f1.acceptField(field) && f2.acceptField(field);
            }
        };
    }

    /**
     * Fields returned from the CAL data dictionary can not expose a SQL expression.  The SQL
     * expression is internal to the data dictionary and intentionally not exposed to the client
     * code which shouldn't care about such details.
     * @see org.openquark.util.datadictionary.Field#getExpression()
     */
    public String getExpression() {
        return getName();
    }

    /**
     * @see org.openquark.util.datadictionary.Field#getQualifierName()
     */
    public String getQualifierName() {
        return "";
    }

    /**
     * @see org.openquark.util.datadictionary.Field#getOwnerName()
     */
    public String getOwnerName() {
        return "";
    }

    /**
     * @see org.openquark.util.datadictionary.Field#getTableName()
     */
    public String getTableName() {
        return "";
    }

    /**
     * @return The field name.  This name will be a valid field name coming from the table
     * specified in getTableName().
     */
    public String getFieldName() {
        return getName();
    }
    
    /**
     * Fields returned from the CAL data dictionary do not know they're table name.  This makes sense
     * since the field may not be a field in the sense of a data foundation table/field, but could
     * be a more abstract object like a calculation or Universe object.
     * @see org.openquark.util.datadictionary.Field#getQualifiedTableName()
     */
    public String getQualifiedTableName() {
        return null;
    }
}
