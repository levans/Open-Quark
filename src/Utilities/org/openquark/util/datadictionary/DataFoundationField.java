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
 * DataFoundationField.java
 * Created: Mar 10, 2006
 * By: dmosimann
 */
package org.openquark.util.datadictionary;

import org.openquark.util.ObjectUtils;

public class DataFoundationField implements Field {
    private final String qualifierName;
    private final String ownerName;
    private final String tableName;
    private final String fieldName;
    private final ValueType dataType;

    /**
     * Constructor for a data foundation field.
     * @param qualifierName
     * @param ownerName
     * @param tableName
     * @param fieldName
     * @param dataType
     */
    public DataFoundationField(String qualifierName,
                               String ownerName,
                               String tableName,
                               String fieldName,
                               ValueType dataType) {
        this.qualifierName = qualifierName;
        this.ownerName = ownerName;
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.dataType = dataType;
    }
    
    /**
     * Simplified constructor for a data foundation field for cases where the qualified name and
     * owner name are not required.
     * @param tableName
     * @param fieldName
     * @param dataType
     */
    public DataFoundationField(String tableName,
                               String fieldName,
                               ValueType dataType) {
        this("", "", tableName, fieldName, dataType);
    }
    
    public String getExpression() {
        StringBuilder buffer = new StringBuilder();
        if (qualifierName != null && qualifierName.length() > 0) {
            buffer.append(qualifierName).append(".");
        }
        if (ownerName != null && ownerName.length() > 0) {
            buffer.append(ownerName).append(".");
        }
        buffer.append(tableName).append(".");
        buffer.append(fieldName);
        
        return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hc = 13;
        hc *= (qualifierName != null) ? qualifierName.hashCode() : 1;
        hc *= (ownerName != null) ? ownerName.hashCode() : 1;
        hc *= (tableName != null) ? tableName.hashCode() : 1;
        hc *= (fieldName != null) ? fieldName.hashCode() : 1;
        hc *= dataType.hashCode();
        return hc;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj.getClass().equals(DataFoundationField.class))) {
            return false;
        }
        DataFoundationField other = (DataFoundationField) obj;
        return ObjectUtils.equals(qualifierName, other.qualifierName)
                && ObjectUtils.equals(ownerName, other.ownerName)
                && ObjectUtils.equals(tableName, other.tableName)
                && ObjectUtils.equals(fieldName, other.fieldName)
                && ObjectUtils.equals(dataType, other.dataType);
    }
    
    /**
     * @return The qualifier name for this field.
     */
    public String getQualifierName() {
        return qualifierName;
    }
    
    /**
     * @return The owner name for this field.
     */
    public String getOwnerName() {
        return ownerName;
    }
    
    /**
     * @return The unqualified table name.
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * @return The qualified table name for this table that this field comes from.  This will include
     * prefixes for the qualifier and owner if they exist.
     */
    public String getQualifiedTableName() {
        StringBuilder buffer = new StringBuilder();
        if (qualifierName != null && qualifierName.length() > 0) {
            buffer.append(qualifierName).append(".");
        }
        if (ownerName != null && ownerName.length() > 0) {
            buffer.append(ownerName).append(".");
        }
        buffer.append(tableName);
        
        return buffer.toString();
    }
    
    /**
     * @return The field name.  This name will be a valid field name coming from the table
     * specified in getTableName().
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return The data type for the values represented by this field.
     * @see org.openquark.util.datadictionary.Field#getDataType()
     */
    public ValueType getDataType() {
        return dataType;
    }
}
