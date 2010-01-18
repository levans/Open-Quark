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
 * CompositeQualifiedNameFilter.java
 * Creation date: Oct 14, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.filter;

import java.util.List;

import org.openquark.cal.compiler.QualifiedName;


/**
 * A composite qualified name filter which accepts a qualified name if and only if
 * all its constituent filters accept the qualified name.
 * 
 * @author Joseph Wong
 */
public abstract class CompositeQualifiedNameFilter implements QualifiedNameFilter {
    
    /**
     * A specialized subclass for composing two filters.
     *
     * @author Joseph Wong
     */
    private static final class DoubleFilter extends CompositeQualifiedNameFilter {
        /** The first filter. */
        private final QualifiedNameFilter f1;
        /** The second filter. */
        private final QualifiedNameFilter f2;
        
        /**
         * Constructs a composite filter.
         * @param f1 the first filter.
         * @param f2 the second filter.
         */
        private DoubleFilter(final QualifiedNameFilter f1, final QualifiedNameFilter f2) {
            if (f1 == null || f2 == null) {
                throw new NullPointerException();
            }
            
            this.f1 = f1;
            this.f2 = f2;
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptQualifiedName(final QualifiedName qualifiedName) {
            return f1.acceptQualifiedName(qualifiedName) && f2.acceptQualifiedName(qualifiedName);
        }
    }
    
    /**
     * A specialized subclass for composing three filters.
     *
     * @author Joseph Wong
     */
    private static final class TripleFilter extends CompositeQualifiedNameFilter {
        /** The first filter. */
        private final QualifiedNameFilter f1;
        /** The second filter. */
        private final QualifiedNameFilter f2;
        /** The third filter. */
        private final QualifiedNameFilter f3;
        
        /**
         * Constructs a composite filter.
         * @param f1 the first filter.
         * @param f2 the second filter.
         * @param f3 the third filter.
         */
        private TripleFilter(final QualifiedNameFilter f1, final QualifiedNameFilter f2, final QualifiedNameFilter f3) {
            if (f1 == null || f2 == null || f3 == null) {
                throw new NullPointerException();
            }
            
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptQualifiedName(final QualifiedName qualifiedName) {
            return f1.acceptQualifiedName(qualifiedName) && f2.acceptQualifiedName(qualifiedName) && f3.acceptQualifiedName(qualifiedName);
        }
    }
    
    /**
     * A general composite filter composed of 0 or more constituent filters.
     *
     * @author Joseph Wong
     */
    private static final class GeneralCompositeFilter extends CompositeQualifiedNameFilter {
        /** The array of filters. */
        private final QualifiedNameFilter[] filterArray;
        
        /**
         * Constructs a composite filter.
         * @param filterList a List of QualifiedNameFilters specifying the constituent filters.
         */
        private GeneralCompositeFilter(final List<QualifiedNameFilter> filterList) {
            if (filterList == null) {
                throw new NullPointerException();
            }
            
            filterArray = filterList.toArray(new QualifiedNameFilter[0]);
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptQualifiedName(final QualifiedName qualifiedName) {
            for (int i = 0; i < filterArray.length; i++) {
                if (!filterArray[i].acceptQualifiedName(qualifiedName)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Private constructor.
     * Factory methods should be used instead for constructing instances.
     */
    private CompositeQualifiedNameFilter() {}
    
    /**
     * Factory method for constructing a composite qualified name filter composed of two filters.
     * @param f1 the first filter.
     * @param f2 the second filter.
     * @return the composite of f1 and f2.
     */
    public static QualifiedNameFilter make(final QualifiedNameFilter f1, final QualifiedNameFilter f2) {
        return new DoubleFilter(f1, f2);
    }
    
    /**
     * Factory method for constructing a composite qualified name filter composed of three filters.
     * @param f1 the first filter.
     * @param f2 the second filter.
     * @param f3 the third filter.
     * @return the composite of f1, f2, and f3.
     */
    public static QualifiedNameFilter make(final QualifiedNameFilter f1, final QualifiedNameFilter f2, final QualifiedNameFilter f3) {
        return new TripleFilter(f1, f2, f3);
    }
    
    /**
     * Factory method for constructing a composite qualified name filter from a list of filters.
     * @param filterList a List of QualifiedNameFilters specifying the constituents of the composite filter. Cannot be null, but can be empty.
     * @return the appropriate composite filter.
     */
    public static QualifiedNameFilter make(final List<QualifiedNameFilter> filterList) {
        switch (filterList.size()) {
        case 0:
            return new AcceptAllQualifiedNamesFilter();
        case 1:
            return filterList.get(0);
        case 2:
            return make(filterList.get(0), filterList.get(1));
        case 3:
            return make(filterList.get(0), filterList.get(1), filterList.get(2));
        default:
            return new GeneralCompositeFilter(filterList);
        }
    }
}
