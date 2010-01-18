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
 * CompositeScopedEntityFilter.java
 * Creation date: Oct 14, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.filter;

import java.util.List;

import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntity;


/**
 * A composite scoped entity filter which accepts a scoped entity if and only if
 * all its constituent filters accept the entity.
 * 
 * @author Joseph Wong
 */
public abstract class CompositeScopedEntityFilter implements ScopedEntityFilter {
    
    /**
     * A specialized subclass for composing two filters.
     *
     * @author Joseph Wong
     */
    private static final class DoubleFilter extends CompositeScopedEntityFilter {
        /** The first filter. */
        private final ScopedEntityFilter f1;
        /** The second filter. */
        private final ScopedEntityFilter f2;
        
        /**
         * Constructs a composite filter.
         * @param f1 the first filter.
         * @param f2 the second filter.
         */
        private DoubleFilter(final ScopedEntityFilter f1, final ScopedEntityFilter f2) {
            if (f1 == null || f2 == null) {
                throw new NullPointerException();
            }
            
            this.f1 = f1;
            this.f2 = f2;
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptScopedEntity(final ScopedEntity entity) {
            return f1.acceptScopedEntity(entity) && f2.acceptScopedEntity(entity);
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptBasedOnScopeOnly(final Scope scope) {
            return f1.acceptBasedOnScopeOnly(scope) && f2.acceptBasedOnScopeOnly(scope);
        }
    }
    
    /**
     * A specialized subclass for composing three filters.
     *
     * @author Joseph Wong
     */
    private static final class TripleFilter extends CompositeScopedEntityFilter {
        /** The first filter. */
        private final ScopedEntityFilter f1;
        /** The second filter. */
        private final ScopedEntityFilter f2;
        /** The third filter. */
        private final ScopedEntityFilter f3;
        
        /**
         * Constructs a composite filter.
         * @param f1 the first filter.
         * @param f2 the second filter.
         * @param f3 the third filter.
         */
        private TripleFilter(final ScopedEntityFilter f1, final ScopedEntityFilter f2, final ScopedEntityFilter f3) {
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
        public boolean acceptScopedEntity(final ScopedEntity entity) {
            return f1.acceptScopedEntity(entity) && f2.acceptScopedEntity(entity) && f3.acceptScopedEntity(entity);
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptBasedOnScopeOnly(final Scope scope) {
            return f1.acceptBasedOnScopeOnly(scope) && f2.acceptBasedOnScopeOnly(scope) && f3.acceptBasedOnScopeOnly(scope);
        }
    }
    
    /**
     * A general composite filter composed of 0 or more constituent filters.
     *
     * @author Joseph Wong
     */
    private static final class GeneralCompositeFilter extends CompositeScopedEntityFilter {
        /** The array of filters. */
        private final ScopedEntityFilter[] filterArray;
        
        /**
         * Constructs a composite filter.
         * @param filterList a List of ScopedEntityFilters specifying the constituent filters.
         */
        private GeneralCompositeFilter(final List<ScopedEntityFilter> filterList) {
            if (filterList == null) {
                throw new NullPointerException();
            }
            
            filterArray = filterList.toArray(new ScopedEntityFilter[0]);
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptScopedEntity(final ScopedEntity entity) {
            for (int i = 0; i < filterArray.length; i++) {
                if (!filterArray[i].acceptScopedEntity(entity)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptBasedOnScopeOnly(final Scope scope) {
            for (int i = 0; i < filterArray.length; i++) {
                if (!filterArray[i].acceptBasedOnScopeOnly(scope)) {
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
    private CompositeScopedEntityFilter() {}
    
    /**
     * Factory method for constructing a composite scoped entity filter composed of two filters.
     * @param f1 the first filter.
     * @param f2 the second filter.
     * @return the composite of f1 and f2.
     */
    public static ScopedEntityFilter make(final ScopedEntityFilter f1, final ScopedEntityFilter f2) {
        return new DoubleFilter(f1, f2);
    }
    
    /**
     * Factory method for constructing a composite scoped entity filter composed of three filters.
     * @param f1 the first filter.
     * @param f2 the second filter.
     * @param f3 the third filter.
     * @return the composite of f1, f2, and f3.
     */
    public static ScopedEntityFilter make(final ScopedEntityFilter f1, final ScopedEntityFilter f2, final ScopedEntityFilter f3) {
        return new TripleFilter(f1, f2, f3);
    }
    
    /**
     * Factory method for constructing a composite scoped entity filter from a list of filters.
     * @param filterList a List of ScopedEntityFilters specifying the constituents of the composite filter. Cannot be null, but can be empty.
     * @return the appropriate composite filter.
     */
    public static ScopedEntityFilter make(final List<ScopedEntityFilter> filterList) {
        switch (filterList.size()) {
        case 0:
            return new AcceptAllScopedEntitiesFilter();
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
