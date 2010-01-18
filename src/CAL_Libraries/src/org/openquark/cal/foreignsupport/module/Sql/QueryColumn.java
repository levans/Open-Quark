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
 * QueryColumn.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import org.openquark.util.ObjectUtils;

/**
 * A description of a projected SQL query column.
 * 
 * @author Richard Webster
 */
public final class QueryColumn {
    /** The column projected expression. */
    private final SqlExpression expression;

    /**
     * An optional alias name to be assigned to the projected column.
     * This can be empty string if no alias should be assigned.
     * Null values are not valid here.
     */ 
    private final String alias;

    /**
     * SqlOrdering constructor.
     * @param expression  the projected column expression
     * @param alias       an optional alias name to be assigned to the projected column
     */
    public QueryColumn(SqlExpression expression, String alias) {
        if (expression == null) {
            throw new NullPointerException("A null value was specified for the projected expression.");
        }

        if (alias == null) {
            throw new NullPointerException("A null value was specified for the column alias.");
        }

        this.expression = expression;
        this.alias = alias;
    }

    /**
     * Returns the projected column expression.
     */
    public SqlExpression getExpression() {
        return expression;
    }

    /**
     * Returns the optional alias name to be assigned to the projected column.
     * An empty string indicates that no alias will be assigned.
     */
    public String getAlias() {
        return alias;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (alias.length() == 0) {
            return expression.toString();
        }
        else {
            return expression + " AS " + alias;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        QueryColumn otherQueryColumn = (QueryColumn) obj;
        return ObjectUtils.equals(expression, otherQueryColumn.expression)
            && ObjectUtils.equals(alias, otherQueryColumn.alias);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = expression.hashCode();
        hash = hash * 17 + alias.hashCode();
        return hash;
    }
}
