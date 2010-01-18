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
 * QueryOption.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

/**
 * This class represents an option which can be applied to a SQL SELECT query.
 * 
 * @author Richard Webster
 */          
public abstract class QueryOption {

    /** The singleton instance of the Distinct option. */
    private static Distinct DISTINCT_OPTION = new Distinct();

    /**
     * Return the Distinct query option.
     */
    public static Distinct makeDistinctOption() {
        return DISTINCT_OPTION;
    }

    /**
     * Return the specified TopN query option.
     */
    public static TopN makeTopNOption(int n, boolean percent, boolean withTies) {
        return new TopN(n, percent, withTies);
    }

    /**
     * This option represents the DISTINCT keyword which can be applied to a SQL SELECT query.
     */
    public static final class Distinct extends QueryOption {
        /**
         * Constructor for the Distinct query option.
         */
        private Distinct() {
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Distinct";
        }
    }

    /**
     * This option applies a TopN restriction to a SQL SELECT query.
     */
    public static final class TopN extends QueryOption {
        private final int n;
        private final boolean percent;
        private final boolean withTies;

        /**
         * TopN option constructor.
         */
        private TopN(int n, boolean percent, boolean withTies) {
            this.n = n;
            this.percent = percent;
            this.withTies = withTies;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Top " + n + (percent ? "%" : "") + (withTies ? " With Ties" : "");
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            TopN otherTopN = (TopN) obj;
            return n == otherTopN.n
                && percent == otherTopN.percent
                && withTies == otherTopN.withTies;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = n;
            hash = hash * 17 + (percent ? 1 : 0);
            hash = hash * 11 + (withTies ? 1 : 0);
            return hash;
        }

        /**
         * Returns the N value for the TopN option.
         */
        public int getN() {
            return n;
        }

        /**
         * Returns whether the TopN option is based on N percent of the values (if True)
         * or just N values (if False).
         */
        public boolean isPercent() {
            return percent;
        }

        /**
         * Returns whether the TopN option includes tied values or not.
         */
        public boolean isWithTies() {
            return withTies;
        }
    }
}
