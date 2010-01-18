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
 * DatabaseFunction.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import org.openquark.util.ObjectUtils;

/**
 * An identifier for a database function, operator, conversion, etc....
 * 
 * @author Richard Webster
 */
public abstract class DatabaseFunction {
    /**
     * An identifier for a database operator.
     */
    public static final class Operator extends DatabaseFunction {

        public static final Operator NOT_OPERATOR                   = new Operator("Not",                   "NOT",      1);
        public static final Operator BITWISE_NOT_OPERATOR           = new Operator("Bitwise Not",           "~",        1);
        public static final Operator NEGATE_OPERATOR                = new Operator("Negate",                "-",        1);
        public static final Operator ISNULL_OPERATOR                = new Operator("Is Null",               "IS NULL",  1);
        public static final Operator ISNOTNULL_OPERATOR             = new Operator("Is Not Null",           "IS NOT NULL", 1);
        public static final Operator EXISTS_OPERATOR                = new Operator("Exists",                "EXISTS",   1);

        public static final Operator EQUAL_OPERATOR                 = new Operator("Equal",                 "=",        2);
        public static final Operator LESS_THAN_OPERATOR             = new Operator("Less Than",             "<",        2);
        public static final Operator LESS_THAN_OR_EQUAL_OPERATOR    = new Operator("Less Than Or Equal",    "<=",       2);
        public static final Operator GREATER_THAN_OPERATOR          = new Operator("Greater Than",          ">",        2);
        public static final Operator GREATER_THAN_OR_EQUAL_OPERATOR = new Operator("Greater Than or Equal", ">=",       2);
        public static final Operator NOT_EQUAL_OPERATOR             = new Operator("Not Equal",             "<>",       2);
        public static final Operator AND_OPERATOR                   = new Operator("And",                   "AND",      2);
        public static final Operator OR_OPERATOR                    = new Operator("Or",                    "OR",       2);
        public static final Operator LIKE_OPERATOR                  = new Operator("Like",                  "LIKE",     2);
        public static final Operator IN_OPERATOR                    = new Operator("In",                    "IN",       2);
        public static final Operator CONCAT_OPERATOR                = new Operator("Concat",                "+",        2);
        public static final Operator PLUS_OPERATOR                  = new Operator("Plus",                  "+",        2);
        public static final Operator MINUS_OPERATOR                 = new Operator("Minus",                 "-",        2);
        public static final Operator MULTIPLY_OPERATOR              = new Operator("Multiply",              "*",        2);
        public static final Operator DIVIDE_OPERATOR                = new Operator("Divide",                "/",        2);
        public static final Operator MODULUS_OPERATOR               = new Operator("Modulus",               "%",        2);
        public static final Operator BITWISE_AND_OPERATOR           = new Operator("Bitwise And",           "&",        2);
        public static final Operator BITWISE_OR_OPERATOR            = new Operator("Bitwise Or",            "|",        2);
        public static final Operator BITWISE_XOR_OPERATOR           = new Operator("Bitwise XOr",           "^",        2);

        public static final Operator BETWEEN_OPERATOR               = new Operator("Between",               "BETWEEN",  3);
        public static final Operator SIMPLE_CASE_OPERATOR           = new Operator("Simple Case",           "CASE",     3);
        public static final Operator SEARCHED_CASE_OPERATOR         = new Operator("Searched Case",         "CASE",     3);

        /** A descriptive name of the operator. */
        private final String descriptiveName;

        /** The default text to be used with the operator. */
        private final String defaultText;

        /** The number of arguments that can be used with the operator. */
        private final int arity;

        /**
         * Operator constructor.
         * @param descriptiveName  a descriptive name of the operator
         * @param defaultText      the default text to be used with the operator
         * @param arity            the number of arguments that can be used with the operator
         */
        private Operator(String descriptiveName, String defaultText, int arity) {
            this.descriptiveName = descriptiveName;
            this.defaultText = defaultText;
            this.arity = arity;
        }

        /**
         * Returns the descriptive name of the operator.
         */
        public String getDescriptiveName() {
            return descriptiveName;
        }

        /**
         * Returns the default text for the operator.
         */
        public String getDefaultText() {
            return defaultText;
        }

        /**
         * Returns the number of arguments for the operator.
         */
        public int getArity() {
            return arity;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Operator: " + descriptiveName;
        }
    }

    /**
     * An identifier for a conversion to some data type.
     */
    public static final class Conversion extends DatabaseFunction {
        public static final Conversion CONVERSION_TO_STRING = new Conversion("String");
        public static final Conversion CONVERSION_TO_INT    = new Conversion("Int");
        public static final Conversion CONVERSION_TO_DOUBLE = new Conversion("Double");
        public static final Conversion CONVERSION_TO_TIME   = new Conversion("Time");

        /** A descriptive name of the data type to which values will be converted. */
        private final String resultDataTypeName;

        /**
         * Conversion constructor.
         * @param resultDataTypeName  a descriptive name of the data type to which values will be converted
         */
        private Conversion(String resultDataTypeName) {
            this.resultDataTypeName = resultDataTypeName;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Conversion to " + resultDataTypeName;
        }
    }

    /**
     * An identifier for a database function.
     */
    public static final class Function extends DatabaseFunction {
        /** The name of the database function. */
        private final String functionName;

        /**
         * Function constructor.
         * @param functionName  the name of the database function
         */
        public Function(String functionName) {
            this.functionName = functionName;
        }

        /**
         * The name of the database function.
         */
        public String getFunctionName() {
            return functionName;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Function: " + functionName;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
    
            Function otherFunction = (Function) obj;
            return ObjectUtils.equals(functionName, otherFunction.functionName);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (functionName == null ? 0 : functionName.hashCode());
            return hash;
        }
    }
}
