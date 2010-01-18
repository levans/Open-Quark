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
 * TableConstraint.java
 * Creation date (Feb 24, 2006).
 * By: Richard Webster
 */
package org.openquark.util.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A description of a constraint on a table (such as uniqueness or a foreign key).
 */
public abstract class TableConstraint {

    /**
     * A primary key on a table consisting of a number of database columns.
     * This also behaves like a uniqueness constraint.
     */
    public static class PrimaryKeyConstraint {
        /** The names of the columns that make up the primary key. */
        private final List<String> keyFieldNames;

        /**
         * PrimaryKeyConstraint constructor.
         */
        public PrimaryKeyConstraint(List<String> keyFieldNames) {
            if (keyFieldNames == null || keyFieldNames.isEmpty()) {
                throw new IllegalArgumentException("At least one field must be specified for the primary key.");
            }
            this.keyFieldNames = new ArrayList<String>(keyFieldNames);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
           return "PrimaryKeyConstraint: " + keyFieldNames;
        }

        /**
         * Returns the names of the columns that make up the primary key.
         */
        public List<String> getKeyFieldNames() {
            return Collections.unmodifiableList(keyFieldNames);
        }
    }

    /**
     * A constraint on a table which ensures that no 2 rows have the same values for the specified set of fields.
     */
    public static class UniquenessConstraint {
        /** The names of the columns that must form unique sets of values. */
        private final List<String> uniqueFieldNames;

        /**
         * UniquenessConstraint constructor.
         */
        public UniquenessConstraint(List<String> uniqueFieldNames) {
            if (uniqueFieldNames == null || uniqueFieldNames.isEmpty()) {
                throw new IllegalArgumentException("At least one field must be specified for the uniqueness constraint.");
            }
            this.uniqueFieldNames = new ArrayList<String>(uniqueFieldNames);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
           return "UniquenessConstraint: " + uniqueFieldNames;
        }

        /**
         * Returns the names of the columns that must form unique sets of values.
         */
        public List<String> getUniqueFieldNames() {
            return Collections.unmodifiableList(uniqueFieldNames);
        }
    }

    /**
     * A foreign key constraint on a table.
     * For a table which a foreign key constraint, the values of the foreign key columns in that table much match the 
     * values of the primary key fields in the associcated primary key table. 
     */
    public static class ForeignKeyConstraint {
        /** The table with the referenced primary key constraint. */
        private final TableReference primaryKeyTable;

        /** The names of the constrained columns in the foreign key table. */
        private final List<String> foreignKeyTableFieldNames;

        /** The names of the columns in the primary key table to which the foreign key fields are constrained. */
        private final List<String> primaryKeyTableFieldNames;

        /**
         * ForeignKeyConstraint constructor.
         */
        public ForeignKeyConstraint(TableReference primaryKeyTable, List<String> foreignKeyTableFieldNames, List<String> primaryKeyTableFieldNames) {
            if (primaryKeyTable == null) {
                throw new NullPointerException("The primary key table must not be null.");
            }
            if (foreignKeyTableFieldNames == null || primaryKeyTableFieldNames == null
                || foreignKeyTableFieldNames.size() != primaryKeyTableFieldNames.size()
                || foreignKeyTableFieldNames.isEmpty()) {
                throw new IllegalArgumentException("The same number of column names (>0) must be specified for the foreign and primary key tables.");
            }

            this.primaryKeyTable = primaryKeyTable;
            this.foreignKeyTableFieldNames = new ArrayList<String>(foreignKeyTableFieldNames);
            this.primaryKeyTableFieldNames = new ArrayList<String>(primaryKeyTableFieldNames);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "ForeignKeyConstraint: " + primaryKeyTable.getTableName() 
                + "\nForeign Table Fields = " + foreignKeyTableFieldNames 
                + "\nPrimary Table Fields = " + primaryKeyTableFieldNames;
        }

        /**
         * Returns the table with the referenced primary key constraint.
         */
        public TableReference getPrimaryKeyTable() {
            return primaryKeyTable;
        }

        /** 
         * Returns the names of the constrained columns in the foreign key table.
         */
        public List<String> getForeignKeyTableFieldNames() {
            return Collections.unmodifiableList(foreignKeyTableFieldNames);
        }

        /** 
         * Returns the names of the columns in the primary key table to which the foreign key fields are constrained.
         */
        public List<String> getPrimaryKeyTableFieldNames() {
            return Collections.unmodifiableList(primaryKeyTableFieldNames);
        }
    }
}
