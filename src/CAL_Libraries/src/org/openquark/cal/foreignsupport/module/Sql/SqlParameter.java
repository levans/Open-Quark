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
 * SqlParameter.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

import org.openquark.util.ObjectUtils;

/**
 * Description of a parameter used in a SQL statement.
 * 
 * @author Richard Webster
 */
public abstract class SqlParameter {
    /** A singleton instance for unnamed parameters. */
    private static final UnnamedParameter UNNAMED_PARAMETER = new UnnamedParameter();

    /**
     * Constructs a SQL parameter with no name.
     */
    public static SqlParameter makeParameter() {
        return UNNAMED_PARAMETER;
    }

    /**
     * Construct a SQL parameter with a name.
     * If name is Null or empty, then an unnamed parameter will be returned.
     * @param name  the name of the parameter
     */
    public static SqlParameter makeParameter(String name) {
        if (name == null || name.length() == 0) {
            return UNNAMED_PARAMETER;
        }
        else {
            return new NamedParameter(name);
        }
    }

    /**
     * A SQL parameter without a name.
     * The values for unnamed parameters will be bound based on the position of the
     * parameter in the SQL statement.
     */
    public static final class UnnamedParameter extends SqlParameter {
        /**
         * UnnamedParameter constructor.
         */
        private UnnamedParameter() {
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Parameter (unnamed)";
        }
    }

    /**
     * A parameter with an associated name.
     * Note: JDBC 3.0 only supports named parameter for CallableStatement.
     */
    public static final class NamedParameter extends SqlParameter {
        /** The name of the parameter. */
        private final String name;

        /**
         * NamedParameter constructor.
         * @param name  the name of the parameter
         */
        private NamedParameter(String name) {
            this.name = name;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Parameter: " + name;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            NamedParameter otherNamedParameter = (NamedParameter) obj;
            return ObjectUtils.equals(name, otherNamedParameter.name);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = (name == null ? 0 : name.hashCode());
            return hash;
        }
    }
}
