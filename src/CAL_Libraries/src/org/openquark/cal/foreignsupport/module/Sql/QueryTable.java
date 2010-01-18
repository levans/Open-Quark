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
 * QueryTable.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import org.openquark.util.ObjectUtils;

/**
 * A query table associates an actual database table or a subquery with a table alias.
 * @author Richard Webster
 */
public abstract class QueryTable {

    /**
     * The alias for the table in the query.
     */
    private final String tableAlias;

    /**
     * QueryTable constructor.
     * @param tableAlias
     */
    private QueryTable(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    /**
     * Returns the alias of the query table.
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        QueryTable otherQueryTable = (QueryTable) obj;
        return ObjectUtils.equals(tableAlias, otherQueryTable.tableAlias);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = (tableAlias == null) ? 0 : tableAlias.hashCode();
        return hash;
    }

    /**
     * A query table for an actual database table.
     */
    public static final class BaseTable extends QueryTable {
        /**
         * The (qualified) name of the database table.
         */
        private final String tableName;

        /**
         * BaseTable constructor.
         */
        public BaseTable(String tableAlias, String tableName) {
            super(tableAlias);
            this.tableName = tableName;
        }

        /**
         * Returns the (qualified) name of the database table.
         */
        public String getTableName() {
            return tableName;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "BaseTable: " + tableName + " AS " + getTableAlias();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            BaseTable otherBaseTable = (BaseTable) obj;
            return super.equals(otherBaseTable)
                && ObjectUtils.equals(tableName, otherBaseTable.tableName);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 17 + (tableName == null ? 0 : tableName.hashCode());
            return hash;
        }
    }

    /**
     * A query table for an sub-query.
     */
    public static final class SubQueryTable extends QueryTable {
        /**
         * The sub-query.
         */
        private final SqlQuery subquery;

        /**
         * SubQueryTable constructor.
         */
        public SubQueryTable(String tableAlias, SqlQuery subquery) {
            super(tableAlias);
            this.subquery = subquery;
        }

        /**
         * Returns the sub-query.
         */
        public SqlQuery getSubquery() {
            return subquery;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "SubQueryTable: " + getTableAlias();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            SubQueryTable otherSubQueryTable = (SubQueryTable) obj;
            return super.equals(otherSubQueryTable)
                && ObjectUtils.equals(subquery, otherSubQueryTable.subquery);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 17 + (subquery == null ? 0 : subquery.hashCode());
            return hash;
        }
    }
}
