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
 * SqlExpression.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import java.util.ArrayList;
import java.util.List;

import org.openquark.util.ObjectUtils;
import org.openquark.util.time.Time;

/**
 * A model describing a SQL expression.
 * 
 * @author Richard Webster
 */
public abstract class SqlExpression {

    /**
     * A reference to a field in a table (or subquery table).
     */
    public static final class QueryField extends SqlExpression {
        /** The name of the field within the table. */
        private final String fieldName;

        /** The query table containing the field. */
        private final QueryTable queryTable;

        /**
         * QueryField constructor.
         * @param fieldName   the name of the field within the table
         * @param queryTable  the query table containing the field
         */
        public QueryField(String fieldName, QueryTable queryTable) {
            this.fieldName = fieldName;
            this.queryTable = queryTable;
        }

        /**
         * Return the name of the field.
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * Return the query table to which the field belongs.
         */
        public QueryTable getQueryTable() {
            return queryTable;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "QueryField: " + queryTable.getTableAlias() + "." + fieldName;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            QueryField otherQueryField = (QueryField) obj;
            return ObjectUtils.equals(fieldName, otherQueryField.fieldName)
                && ObjectUtils.equals(queryTable, otherQueryField.queryTable);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (fieldName == null ? 0 : fieldName.hashCode());
            hash = hash * 17 + (queryTable == null ? 0 : queryTable.hashCode());
            return hash;
        }
    }

    /**
     * A literal value expression.
     */
    public abstract static class ConstantExpression extends SqlExpression {
        /**
         * A NULL value constant (of no specific type).
         */
        public static final class NullConstant extends ConstantExpression {
            /** Singleton instance of the NULL constant expression. */
            private static final NullConstant NULL_CONSTANT = new NullConstant();

            /**
             * Return the Null constant expression.
             */
            public static NullConstant nullConstant() {
                return NULL_CONSTANT;
            }

            /**
             * NullConstant constructor.
             */
            private NullConstant() {
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "Constant: NULL";
            }
        }

        /**
         * A string constant value.
         */
        public static final class StringConstant extends ConstantExpression {
            /** The constant String value. */
            private final String stringValue;

            /**
             * StringConstant constructor.
             * @param stringValue  the constant String value
             */
            public StringConstant(String stringValue) {
                this.stringValue = stringValue;
            }

            /**
             * Return the constant String value.
             */
            public String getStringValue() {
                return stringValue;
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "String Constant: " + stringValue;
            }

            /** {@inheritDoc} */
            @Override
            public boolean equals(Object obj) {
                if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }

                StringConstant otherStringConstant = (StringConstant) obj;
                return ObjectUtils.equals(stringValue, otherStringConstant.stringValue);
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                int hash = (stringValue == null ? 0 : stringValue.hashCode());
                return hash;
            }
        }

        /**
         * A numeric constant value.
         */
        public static final class NumericConstant extends ConstantExpression {
            /** The constant numeric value. */
            private final double numericValue;

            /**
             * NumericConstant constructor.
             * @param numericValue  the constant numeric value
             */
            public NumericConstant(double numericValue) {
                this.numericValue = numericValue;
            }

            /**
             * Return the constant numeric value.
             */
            public double getStringValue() {
                return numericValue;
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "Numeric Constant: " + numericValue;
            }

            /** {@inheritDoc} */
            @Override
            public boolean equals(Object obj) {
                if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }

                NumericConstant otherNumericConstant = (NumericConstant) obj;
                return numericValue == otherNumericConstant.numericValue;
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                int hash = new Double(numericValue).hashCode();
                return hash;
            }
        }

        /**
         * A Boolean constant value.
         */
        public static final class BooleanConstant extends ConstantExpression {
            /** The constant Boolean value. */
            private final boolean booleanValue;

            /**
             * BooleanConstant constructor.
             * @param booleanValue  the constant Boolean value
             */
            public BooleanConstant(boolean booleanValue) {
                this.booleanValue = booleanValue;
            }

            /**
             * Return the constant Boolean value.
             */
            public boolean getBooleanValue() {
                return booleanValue;
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "Boolean Constant: " + booleanValue;
            }

            /** {@inheritDoc} */
            @Override
            public boolean equals(Object obj) {
                if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }

                BooleanConstant otherBooleanConstant = (BooleanConstant) obj;
                return booleanValue == otherBooleanConstant.booleanValue;
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                int hash = (booleanValue ? 1 : 0);
                return hash;
            }
        }

        /**
         * A date/time constant value.
         */
        public static final class TimeConstant extends ConstantExpression {
            /** The constant time value. */
            private final Time timeValue;

            /**
             * TimeConstant constructor.
             * @param timeValue  the constant time value
             */
            public TimeConstant(Time timeValue) {
                this.timeValue = timeValue;
            }

            /**
             * Return the constant time value.
             */
            public Time getTimeValue() {
                return timeValue;
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "Time Constant: " + timeValue;
            }

            /** {@inheritDoc} */
            @Override
            public boolean equals(Object obj) {
                if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }

                TimeConstant otherTimeConstant = (TimeConstant) obj;
                return ObjectUtils.equals(timeValue, otherTimeConstant.timeValue);
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                int hash = (timeValue == null ? 0 : timeValue.hashCode());
                return hash;
            }
        }
    }

    /**
     * A parameter placeholder expression.
     */
    public static final class ParameterExpression extends SqlExpression {
        /** The parameter being referenced by the expression. */
        private final SqlParameter parameter;

        /**
         * ParameterExpression constructor.
         * @param parameter  the parameter being referenced by the expression
         */
        public ParameterExpression(SqlParameter parameter) {
            this.parameter = parameter;
        }

        /**
         * Returns the parameter being referenced by the expression.
         */
        public SqlParameter getParameter() {
            return parameter;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Parameter: " + parameter;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            ParameterExpression otherParameterExpression = (ParameterExpression) obj;
            return ObjectUtils.equals(parameter, otherParameterExpression.parameter);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (parameter == null ? 0 : parameter.hashCode());
            return hash;
        }
    }

    /**
     * An expression representing a list of values.
     * This is typically used with the 'IN' operator.
     */
    public static final class ListExpression extends SqlExpression {
        /** The list of expressions for the list values. */
        private final List <SqlExpression> listValues;

        /**
         * ListExpression constructor.
         * @param listValues  the list of expressions for the list values
         */
        public ListExpression(List <SqlExpression> listValues) {
            this.listValues = new ArrayList<SqlExpression>(listValues);
        }

        /**
         * Return the list of expressions for the list values.
         */
        public List<SqlExpression> getListValues() {
            return new ArrayList<SqlExpression>(listValues);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "ListExpression: " + listValues;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            ListExpression otherListExpression = (ListExpression) obj;
            return ObjectUtils.equalsLists(listValues, otherListExpression.listValues);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (listValues == null ? 0 : listValues.hashCode());
            return hash;
        }
    }

    /**
     * An expression representing a SQL function call.
     */
    public static final class FunctionExpression extends SqlExpression {
        /** An identifier of the database function. */
        private final DatabaseFunction databaseFunction;

        /** The list of expressions for the function arguments. */
        private final List <SqlExpression> arguments;

        /**
         * FunctionExpression constructor.
         * @param databaseFunction  an identifier of the database function
         * @param arguments         the list of expressions for the function arguments
         */
        public FunctionExpression(DatabaseFunction databaseFunction, List <SqlExpression> arguments) {
            this.databaseFunction = databaseFunction;
            this.arguments = new ArrayList<SqlExpression>(arguments);
        }

        /**
         * Returns the database function identfier.
         */
        public DatabaseFunction getDatabaseFunction() {
            return databaseFunction;
        }

        /**
         * Returns the list of expressions for the function arguments.
         */
        public List<SqlExpression> getArguments() {
            return new ArrayList<SqlExpression>(arguments);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "FunctionExpression: " + databaseFunction + "(" + arguments + ")";
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            FunctionExpression otherFunctionExpression = (FunctionExpression) obj;
            return ObjectUtils.equals(databaseFunction, otherFunctionExpression.databaseFunction)
                && ObjectUtils.equalsLists(arguments, otherFunctionExpression.arguments);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (databaseFunction == null ? 0 : databaseFunction.hashCode());
            hash = hash * 37 + (arguments == null ? 0 : arguments.hashCode());
            return hash;
        }
    }

    /**
     * An expression based on a subquery (which projects a single column and returns a single row).
     */
    public static final class SubQueryExpression extends SqlExpression {
        /** The subquery to be treated as a database expression. */
        private final SqlQuery subquery;

        /**
         * SubQueryExpression constructor.
         * @param subquery  the subquery to be treated as a database expression
         */
        public SubQueryExpression(SqlQuery subquery) {
            this.subquery = subquery;
        }

        /**
         * Return the subquery being treated as a database expression.
         */
        public SqlQuery getSubquery() {
            return subquery;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "SubQueryExpression: " + subquery;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            SubQueryExpression otherSubQueryExpression = (SubQueryExpression) obj;
            return ObjectUtils.equals(subquery, otherSubQueryExpression.subquery);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (subquery == null ? 0 : subquery.hashCode());
            return hash;
        }
    }
}
