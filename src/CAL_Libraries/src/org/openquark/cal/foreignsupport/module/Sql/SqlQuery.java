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
 * SqlQuery.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import java.util.ArrayList;
import java.util.List;

import org.openquark.util.ObjectUtils;

/**
 * A model describing a SQL queries.
 * 
 * @author Richard Webster
 */
public abstract class SqlQuery {
    /**
     * A model for a SQL SELECT statement.
     */
    public static final class SelectQuery extends SqlQuery {
        /** A list of the options to be applied to the query. */
        private final List <QueryOption> options;

        /**
         * A list of the columns to be projected by the query, with optional aliases.
         * This info corresponds to the SELECT clause of a typical SQL query. 
         */
        private final List <QueryColumn> columns;

        /**
         * An optional Boolean restriction expression to filter the result rows.
         * This info corresponds to the WHERE clause of a typical SQL query.
         * This can be Null if no restriction is to be applied to the query.
         * A constant 'True' expression also indicates that no restriction should be applied.
         */
        private final SqlExpression restriction;

        /**
         * A list of ordering expressions with Ascending/Descending flags.
         * This info corresponds to the ORDER BY clause of a typical SQL query.
         */
        private final List <SqlOrdering> orderings;

        /**
         * A list of the join nodes for the query.
         * This info corresponds to the FROM clause of a typical SQL query.
         */
        private final List <JoinNode> joins;

        /**
         * A list of grouping expressions for the query.
         * This info corresponds to the GROUP BY clause of a typical SQL query.
         */
        private final List <SqlExpression> groups;

        /**
         * An optional Boolean group restriction expression to filter the result groups.
         * This info corresponds to the HAVING clause of a typical SQL query.
         * This can be Null if no group restriction is to be applied to the query.
         * A constant 'True' expression also indicates that no group restriction should be applied.
         */
        private final SqlExpression groupRestriction;

        /**
         * SelectQuery constructor.
         * @param options           a list of the options to be applied to the query
         * @param columns           a list of the columns to be projected by the query, with optional aliases
         * @param restriction       an optional Boolean restriction expression to filter the result rows
         * @param orderings         a list of ordering expressions with Ascending/Descending flags
         * @param joins             a list of the join nodes for the query
         * @param groups            a list of grouping expressions for the query
         * @param groupRestriction  an optional Boolean group restriction expression to filter the result groups
         */
        public SelectQuery(List <QueryOption> options,
                           List <QueryColumn> columns,
                           SqlExpression restriction,
                           List <SqlOrdering> orderings,
                           List <JoinNode> joins,
                           List <SqlExpression> groups,
                           SqlExpression groupRestriction) {
            this.options = new ArrayList<QueryOption>(options);
            this.columns = new ArrayList<QueryColumn>(columns);
            this.restriction = restriction;
            this.orderings = new ArrayList<SqlOrdering>(orderings);
            this.joins = new ArrayList<JoinNode>(joins);
            this.groups = new ArrayList<SqlExpression>(groups);
            this.groupRestriction = groupRestriction;
        }

        /**
         * Returns a list of the options to be applied to the query.
         */
        public List <QueryOption> getOptions() {
            return new ArrayList<QueryOption>(options);
        }

        /**
         * Returns a list of the columns to be projected by the query, with optional aliases.
         */
        public List <QueryColumn> getColumns() {
            return new ArrayList<QueryColumn>(columns);
        }

        /**
         * Returns an optional Boolean restriction expression to filter the result rows.
         * This can be Null if no restriction is to be applied.
         * A constant 'True' expression also indicates that no restriction should be applied.
         */
        public SqlExpression getRestriction() {
            return restriction;
        }

        /**
         * Returns a list of ordering expressions with Ascending/Descending flags.
         */
        public List <SqlOrdering> getOrderings() {
            return new ArrayList<SqlOrdering>(orderings);
        }

        /**
         * Returns a list of the join nodes for the query.
         */
        public List <JoinNode> getJoins() {
            return new ArrayList<JoinNode>(joins);
        }

        /**
         * Returns a list of grouping expressions for the query.
         */
        public List <SqlExpression> getGroups() {
            return new ArrayList<SqlExpression>(groups);
        }

        /**
         * Returns an optional Boolean group restriction expression to filter the result groups.
         * This can be Null if no restriction is to be applied.
         * A constant 'True' expression also indicates that no restriction should be applied.
         */
        public SqlExpression getGroupRestriction() {
            return groupRestriction;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Query:\n  Options: " + options
                 + "\n  Columns: " + columns
                 + "\n  Restriction: " + (restriction == null ? "<none>" : restriction.toString())
                 + "\n  Orderings: " + orderings
                 + "\n  Joins: " + joins
                 + "\n  Groups: " + groups
                 + "\n  GroupRestriction: " + (groupRestriction == null ? "<none>" : groupRestriction.toString());
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            SelectQuery otherSelectQuery = (SelectQuery) obj;
            return ObjectUtils.equalsLists(options, otherSelectQuery.options)
                && ObjectUtils.equalsLists(columns, otherSelectQuery.columns)
                && ObjectUtils.equals(restriction, otherSelectQuery.restriction)
                && ObjectUtils.equalsLists(orderings, otherSelectQuery.orderings)
                && ObjectUtils.equalsLists(joins, otherSelectQuery.joins)
                && ObjectUtils.equalsLists(groups, otherSelectQuery.groups)
                && ObjectUtils.equals(groupRestriction, otherSelectQuery.groupRestriction);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (options == null ? 0 : options.hashCode());
            hash = hash * 11 + (columns == null ? 0 : columns.hashCode());
            hash = hash * 17 + (restriction == null ? 0 : restriction.hashCode());
            hash = hash * 23 + (orderings == null ? 0 : orderings.hashCode());
            hash = hash * 29 + (joins == null ? 0 : joins.hashCode());
            hash = hash * 37 + (groups == null ? 0 : groups.hashCode());
            hash = hash * 47 + (groupRestriction == null ? 0 : groupRestriction.hashCode());
            return hash;
        }
    }

    /**
     * A model for a Union between 2 other SQL queries.
     */
    public static final class UnionQuery extends SqlQuery {
        /** The first query to be unioned. */
        private final SqlQuery query1;

        /** The second query to be unioned. */
        private final SqlQuery query2;

        /** Indicates whether all rows (if True) or only distinct rows (if False) should be returned by the query. */
        private final boolean unionAll;

        /**
         * UnionQuery constructor.
         * @param query1    the first query to be unioned
         * @param query2    the second query to be unioned
         * @param unionAll  indicates whether all rows (if True) or only distinct rows (if False) should be returned by the query
         */
        public UnionQuery(SqlQuery query1, SqlQuery query2, boolean unionAll) {
            this.query1 = query1;
            this.query2 = query2;
            this.unionAll = unionAll;
        }

        /**
         * Returns the first of the queries being unioned.
         */
        public SqlQuery getQuery1() {
            return query1;
        }

        /**
         * Returns the second of the queries being unioned.
         */
        public SqlQuery getQuery2() {
            return query2;
        }

        /**
         * Returns whether all rows (if True) or only distinct rows (if False) should be returned by the query.
         */
        public boolean isUnionAll() {
            return unionAll;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return query1 + (unionAll ? "\nUNION ALL\n" : "\nUNION\n") + query2;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            UnionQuery otherUnionQuery = (UnionQuery) obj;
            return ObjectUtils.equals(query1, otherUnionQuery.query1)
                && ObjectUtils.equals(query2, otherUnionQuery.query2)
                && unionAll == otherUnionQuery.unionAll;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (query1 == null ? 0 : query1.hashCode());
            hash = hash * 17 + (query1 == null ? 0 : query1.hashCode());
            hash = hash * 11 + (unionAll ? 1 : 0);
            return hash;
        }
    }
}
