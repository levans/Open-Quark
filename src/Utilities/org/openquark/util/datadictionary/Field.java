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
 * Field.java
 * Created: Mar 10, 2006
 * By: dmosimann
 */
package org.openquark.util.datadictionary;

/**
 * Interface defining the methods necessary to be considered a Field in the system.
 */
public interface Field {
    /**
     * Returns the data type of the values represented by this field. 
     * @return A ValueType for the values of this field.
     */
    public ValueType getDataType();
    
    /**
     * Returns a SQL expression that can be used in a query for this field.
     * @return A String suitable for use in a SQL expression. 
     */
    public String getExpression();
    
    /**
     * @return The qualifier name for this field.
     */
    public String getQualifierName();
    
    /**
     * @return The owner name for this field.
     */
    public String getOwnerName();
    
    /**
     * @return The unqualified table name.
     */
    public String getTableName();

    /**
     * @return The field name.  This name will be a valid field name coming from the table
     * specified in getTableName().
     */
    public String getFieldName();
    
    /**
     * Returns the qualified name of the table that this Field comes from.  The name will
     * be of the form Qualifier.Owner.Table, where both qualifier and owner are optional
     * components that may not exist.
     * This can be null if the table name is not known or not required.
     * @return The name of the table which may be null.
     */
    public String getQualifiedTableName();
}
