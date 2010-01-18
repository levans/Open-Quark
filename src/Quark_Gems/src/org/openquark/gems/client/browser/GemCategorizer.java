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
 * Categorizer.java
 * Creation date: Oct 28, 2002.
 * By: Edward Lam
 */
package org.openquark.gems.client.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.services.GemEntity;

/**
 * A GemCategorizer breaks up a collection of entities into named (and optionally sorted) categories.
 * 
 * @author Edward Lam
 */
public abstract class GemCategorizer<T> {

    /**
     * List of keys representing categories. The keys for the categories to include, 
     * in the order in which they should be returned.
     */
    private final List<T> categoriesToInclude;

    /**
     * Whether new categories should be formed if not in the list of provided categories.
     * If false, any entity whose category does not fall into the list of categories will not be included.
     */
    private final boolean formNewCategories;

    /**
     * Constructor for a Categorizer.
     * This categorizer will form categories based on the categorized entities, and sort the categories.
     */
    public GemCategorizer() {
        this(Collections.<T>emptyList(), true);
    }
    
    /**
     * Constructor for a GemCategorizer
     * @param categoriesToInclude List of keys representing categories.  
     *   The keys for the categories to include, in the order in which they should be returned.
     *   Passing null is equivalent to passing an empty list.
     * @param formNewCategories Whether new categories should be formed if not in the list of provided categories.
     *   If false, any entity whose category does not fall into the list of categories will not be included.
     *   If true, in the absence of a specified ordering, any new categories will be included after the 
     *   provided categories.
     */
    public GemCategorizer(List<T> categoriesToInclude, boolean formNewCategories) {

        if (categoriesToInclude == null) {
            categoriesToInclude = Collections.emptyList();
        }

        this.categoriesToInclude = categoriesToInclude;
        this.formNewCategories = formNewCategories;
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer, zero, or a positive integer 
     * as the first argument is less than, equal to, or greater than the second.<p>
     * The default implementation considers all keys to be equal, meaning that categories will be returned
     * in the order in which they are encountered in the entity list.
     * Subclasses in which categories should exhibit a given ordering should override this method.
     * @param key1 the first object to be compared.
     * @param key2 the second object to be compared.
     * @return int a negative integer, zero, or a positive integer as the first argument 
     *         is less than, equal to, or greater than the second. 
     */
    public abstract int compareKey(GemCategory.CategoryKey<T> key1, GemCategory.CategoryKey<T> key2);

    /**
     * Return a list of keys representing all of the categories that an entity belongs to.
     * Most implementations should return a single-element list.
     *   
     * @param gemEntity The entity to generate a list of categories for
     * @return List of category keys
     */
    public abstract List<GemCategory.CategoryKey<T>> getCategoryKeyList(GemEntity gemEntity);
    
    /**
     * Form the given gemEntities into categories.
     * @param gemEntities the entities to categorize.
     * @return List the categories
     */
    public List<GemCategory> formCategories(Collection<GemEntity> gemEntities) {

        // Create the categories provided (if any)
        Map<GemCategory.CategoryKey<T>, List<GemEntity>> keyToCategoryItemsListMap = new HashMap<GemCategory.CategoryKey<T>, List<GemEntity>>();

        for (final T key : categoriesToInclude) {
            GemCategory.CategoryKey<T> categoryKey = new GemCategory.CategoryKey<T>(key);
            keyToCategoryItemsListMap.put(categoryKey, new ArrayList<GemEntity>());
        }

        // Now place the items into category lists.
        for (final GemEntity gemEntity : gemEntities) {

            List<GemCategory.CategoryKey<T>> gemCategories = getCategoryKeyList(gemEntity);

            // Add the gem to each of the categories that apply to it
            for (final GemCategory.CategoryKey<T> categoryKey : gemCategories) {
                List<GemEntity> category = keyToCategoryItemsListMap.get(categoryKey);

                // Create a new category if appropriate.
                if (category == null) {
        
                    // skip if we don't form new categories.
                    if (!formNewCategories) {
                        continue;
                    }
        
                    category = new ArrayList<GemEntity>();
                    keyToCategoryItemsListMap.put(categoryKey, category);
                }
                
                category.add(gemEntity);
            }
        }

        // Grab the category keys and sort them.
        List<GemCategory.CategoryKey<T>> categoryKeys = new ArrayList<GemCategory.CategoryKey<T>>(keyToCategoryItemsListMap.keySet());
        Comparator<GemCategory.CategoryKey<T>> keyComparator = new Comparator<GemCategory.CategoryKey<T>>() {
            public int compare(GemCategory.CategoryKey<T> o1, GemCategory.CategoryKey<T> o2) {
                return compareKey(o1, o2);
            }
        };
        Collections.sort(categoryKeys, keyComparator);

        // Form the categories
        List<GemCategory> categoryList = new ArrayList<GemCategory>(categoryKeys.size());
        for (final GemCategory.CategoryKey<T> categoryKey : categoryKeys) {

            List<GemEntity> category = keyToCategoryItemsListMap.get(categoryKey);
            categoryList.add(new GemCategory(categoryKey, category));
        }

        return categoryList;
    }
}
