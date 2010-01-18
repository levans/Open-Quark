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
 * JoinNode.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import org.openquark.util.ObjectUtils;

/**
 * A model for a query table or a join between query table or other join nodes.
 * 
 * @author Richard Webster
 */
public abstract class JoinNode {
    /**
     * A join node representing a single query table.
     */
    public static final class JoinTable extends JoinNode {
        /** A query table. */
        private final QueryTable table;

        /**
         * JoinTable constructor.
         * @param table  a query table
         */
        public JoinTable(QueryTable table) {
            this.table = table;
        }

        /**
         * Returns the query table for the join node.
         */
        public QueryTable getTable() {
            return table;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "JoinTable: " + table;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            JoinTable otherJoinTable = (JoinTable) obj;
            return ObjectUtils.equals(table, otherJoinTable.table);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (table == null ? 0 : table.hashCode());
            return hash;
        }
    }

    /**
     * A join node specifying a join between tables in two join trees.
     */
    public static final class JoinSubtree extends JoinNode {
     
        /** One of the join trees to be joined. */
        private final JoinNode leftNode;

        /** The other join tree to be joined. */
        private final JoinNode rightNode;

        /** A Boolean expression joining tables in the join trees. */
        private final SqlExpression linkingExpression;

        /** The type of join to be performed. */
        private final JoinType joinType;

        /**
         * JoinSubtree constructor.
         * @param leftNode           one of the join trees to be joined
         * @param rightNode          the other join tree to be joined
         * @param linkingExpression  a Boolean expression joining tables in the join trees
         * @param joinType           the type of join to be performed
         */
        public JoinSubtree(JoinNode leftNode, JoinNode rightNode, SqlExpression linkingExpression, JoinType joinType) {
            this.leftNode = leftNode;
            this.rightNode = rightNode;
            this.linkingExpression = linkingExpression;
            this.joinType = joinType;
        }

        /**
         * Returns the left node being joined.
         */
        public JoinNode getLeftNode() {
            return leftNode;
        }

        /**
         * Returns the right node being joined.
         */
        public JoinNode getRightNode() {
            return rightNode;
        }

        /**
         * Returns the Boolean expression joining tables in the join trees.
         */
        public SqlExpression getLinkingExpression() {
            return linkingExpression;
        }

        /**
         * Returns the type of join to be performed.
         */
        public JoinType getJoinType() {
            return joinType;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "JoinSubtree: " + "(" + leftNode + " " + joinType + " " + rightNode + " ON " + linkingExpression + ")";
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            JoinSubtree otherJoinSubtree = (JoinSubtree) obj;
            return ObjectUtils.equals(leftNode, otherJoinSubtree.leftNode)
                && ObjectUtils.equals(rightNode, otherJoinSubtree.rightNode)
                && ObjectUtils.equals(linkingExpression, otherJoinSubtree.linkingExpression)
                && ObjectUtils.equals(joinType, otherJoinSubtree.joinType);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (leftNode == null ? 0 : leftNode.hashCode());
            hash = hash * 11 + (rightNode == null ? 0 : rightNode.hashCode());
            hash = hash * 37 + (linkingExpression == null ? 0 : linkingExpression.hashCode());
            hash = hash * 41 + (joinType == null ? 0 : joinType.hashCode());
            return hash;
        }
    }
}
