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
 * Category.java
 * Creation date: Oct 29, 2002.
 * By: Edward Lam
 */
package org.openquark.gems.client.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.services.GemEntity;

/**
 * A GemCategory is a simple encapsulation of a list of entities, plus a key to represent the category.
 * A simple category key might be its name (a string).
 * @author Edward Lam
 */
public class GemCategory {
    
    /**
     * A container that represents the object key used by the GemCategorizer
     * @author Ken Wong
     * Creation Date: December 16th 2002
     */
    public static final class CategoryKey<T>{
        
        /** the actual key used */
        private final T value;

        /**
         * Default constructor for the CategoryKey class
         * @param value
         */
        public CategoryKey(T value) {
            this.value = value;
        }
        
        /**
         * Returns the string representation of this key
         * @return String
         */
        public String getName() {
            return value.toString();
        }
        
        /**
         * Returns the value that this object is holding on to
         */
        public T getValue() {
            return value;
        }
       
        /**
         * Allows the equality evaluation of two keys.
         * Really just calls the equals method of the objects
         * @return true if they keys are the same, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CategoryKey) {
                return getValue().equals(((CategoryKey<?>)obj).getValue());
            }
            return false;
        }
        
        /**
         * Allows the hashing of the key.
         * Really just calls the hashCode method of the objects
         * @return int
         */
        @Override
        public int hashCode() {
            return value.hashCode();
        }
        
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "CategoryKey (" + value.toString() + ")";
        }
    }

    /** The key representing the category. */
    private final CategoryKey<?> categoryKey;

    /** The list of items in the category. */
    private final List<GemEntity> categoryItems;
    
    /**
     * Constructor for an empty GemCategory.
     * @param categoryKey the key representing the category.
     */
    public GemCategory(CategoryKey<?> categoryKey) {
        this.categoryKey = categoryKey;
        categoryItems = Collections.emptyList();
    }

    /**
     * Constructor for a GemCategory.
     * @param categoryKey the key representing the category.
     * @param categoryItems the items in the category.
     */    
    public GemCategory(CategoryKey<?> categoryKey, Collection<GemEntity> categoryItems) {
        this.categoryKey = categoryKey;
        this.categoryItems = new ArrayList<GemEntity>(categoryItems);
    }
    
    /**
     * Get the category key.
     * @return CategoryKey the key.
     */
    public CategoryKey<?> getCategoryKey() {
        return categoryKey;
    }
    
    /**
     * Get the items in the category.
     * @return List the items in the category.
     */
    public List<GemEntity> getCategoryItems() {
        return new ArrayList<GemEntity>(categoryItems);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GemCategory " + categoryKey.getName() + " (" + categoryItems.size() + " items)";
    }
}
