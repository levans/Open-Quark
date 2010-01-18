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
 * CompositeModuleFilter.java
 * Creation date: Oct 14, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.filter;

import java.util.List;

import org.openquark.cal.compiler.ModuleName;


/**
 * A composite module filter which accepts a module if and only if all its
 * constituent filters accept the module.
 * 
 * @author Joseph Wong
 */
public abstract class CompositeModuleFilter implements ModuleFilter {
    
    /**
     * A specialized subclass for composing two filters.
     *
     * @author Joseph Wong
     */
    private static final class DoubleFilter extends CompositeModuleFilter {
        /** The first filter. */
        private final ModuleFilter f1;
        /** The second filter. */
        private final ModuleFilter f2;
        
        /**
         * Constructs a composite filter.
         * @param f1 the first filter.
         * @param f2 the second filter.
         */
        private DoubleFilter(final ModuleFilter f1, final ModuleFilter f2) {
            if (f1 == null || f2 == null) {
                throw new NullPointerException();
            }
            
            this.f1 = f1;
            this.f2 = f2;
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptModule(final ModuleName moduleName) {
            return f1.acceptModule(moduleName) && f2.acceptModule(moduleName);
        }
    }
    
    /**
     * A specialized subclass for composing three filters.
     *
     * @author Joseph Wong
     */
    private static final class TripleFilter extends CompositeModuleFilter {
        /** The first filter. */
        private final ModuleFilter f1;
        /** The second filter. */
        private final ModuleFilter f2;
        /** The third filter. */
        private final ModuleFilter f3;
        
        /**
         * Constructs a composite filter.
         * @param f1 the first filter.
         * @param f2 the second filter.
         * @param f3 the third filter.
         */
        private TripleFilter(final ModuleFilter f1, final ModuleFilter f2, final ModuleFilter f3) {
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
        public boolean acceptModule(final ModuleName moduleName) {
            return f1.acceptModule(moduleName) && f2.acceptModule(moduleName) && f3.acceptModule(moduleName);
        }
    }
    
    /**
     * A general composite filter composed of 0 or more constituent filters.
     *
     * @author Joseph Wong
     */
    private static final class GeneralCompositeFilter extends CompositeModuleFilter {
        /** The array of filters. */
        private final ModuleFilter[] filterArray;
        
        /**
         * Constructs a composite filter.
         * @param filterList a List of ModuleFilters specifying the constituent filters.
         */
        private GeneralCompositeFilter(final List<ModuleFilter> filterList) {
            if (filterList == null) {
                throw new NullPointerException();
            }
            
            filterArray = filterList.toArray(new ModuleFilter[0]);
        }

        /**
         * {@inheritDoc}
         */
        public boolean acceptModule(final ModuleName moduleName) {
            for (int i = 0; i < filterArray.length; i++) {
                if (!filterArray[i].acceptModule(moduleName)) {
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
    private CompositeModuleFilter() {}
    
    /**
     * Factory method for constructing a composite module filter composed of two filters.
     * @param f1 the first filter.
     * @param f2 the second filter.
     * @return the composite of f1 and f2.
     */
    public static ModuleFilter make(final ModuleFilter f1, final ModuleFilter f2) {
        return new DoubleFilter(f1, f2);
    }
    
    /**
     * Factory method for constructing a composite module filter composed of three filters.
     * @param f1 the first filter.
     * @param f2 the second filter.
     * @param f3 the third filter.
     * @return the composite of f1, f2, and f3.
     */
    public static ModuleFilter make(final ModuleFilter f1, final ModuleFilter f2, final ModuleFilter f3) {
        return new TripleFilter(f1, f2, f3);
    }
    
    /**
     * Factory method for constructing a composite module filter from a list of filters.
     * @param filterList a List of ModuleFilters specifying the constituents of the composite filter. Cannot be null, but can be empty.
     * @return the appropriate composite filter.
     */
    public static ModuleFilter make(final List<ModuleFilter> filterList) {
        switch (filterList.size()) {
        case 0:
            return new AcceptAllModulesFilter();
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
