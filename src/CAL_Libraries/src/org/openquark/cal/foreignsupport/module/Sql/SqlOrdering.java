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
 * SqlOrdering.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import org.openquark.util.ObjectUtils;

/**
 * Sorting information for a SQLquery.
 * 
 * @author Richard Webster
 */
public final class SqlOrdering {
    /** The ordering expression. */
    private final SqlExpression expression;

    /** Whether the ordering should be in ascending order (True) or descending order (False). */
    private final boolean ascending;

    /**
     * SqlOrdering constructor.
     * @param expression  the ordering expression
     * @param ascending   whether the ordering should be in ascending order (True) or descending order (False)
     */
    public SqlOrdering(SqlExpression expression, boolean ascending) {
        if (expression == null) {
            throw new NullPointerException("A null value was specified for the ordering expression.");
        }

        this.expression = expression;
        this.ascending = ascending;
    }

    /**
     * Returns the ordering expression.
     */
    public SqlExpression getExpression() {
        return expression;
    }

    /**
     * Returns whether the ordering should be in ascending order (True) or descending order (False).
     */
    public boolean isAscending() {
        return ascending;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Ordering: " + expression + (ascending ? " ASC" : " DESC");
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        SqlOrdering otherSqlOrdering = (SqlOrdering) obj;
        return ObjectUtils.equals(expression, otherSqlOrdering.expression)
            && ascending == otherSqlOrdering.ascending;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = expression.hashCode();
        hash = hash * 17 + (ascending ? 1 : 0);
        return hash;
    }
}
