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
 * DatabaseFolder.java
 * Creation date (Oct 5, 2004).
 * By: Richard Webster
 */
package org.openquark.util.datadictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * A container of other database items.
 * @author Richard Webster
 */
public class DatabaseFolder extends DatabaseItem {

    /**
     * DatabaseFolder constructor.
     * @param name      the name of the folder
     * @param children  the children of this item
     */
    public DatabaseFolder(String uniqueName, String name, List<DatabaseItem> children) {
        super(uniqueName, name, children);
    }

    /**
     * DatabaseFolder constructor.
     * @param name      the name of the folder
     * @param children  the children of this item
     */
    public DatabaseFolder(String name, List<DatabaseItem> children) {
        super(name, name, children);
    }

    @Override
    public DatabaseItem filterItem(DatabaseFieldFilter fieldFilter) {
        // Handle the case where there is no filter.
        if (fieldFilter == null) {
            return this;
        }

        // Filter any child items.
        List<DatabaseItem> filteredChildren = new ArrayList<DatabaseItem>();
        boolean childrenFiltered = false;

        for (final DatabaseItem childItem : getChildren()) {
            DatabaseItem filteredItem = childItem.filterItem(fieldFilter);
            // Keep track of whether any child item was modified.
            // Note that the objects should be compared by identity, not equivalence.
            if (filteredItem != childItem) {
                childrenFiltered = true;
            }

            if (filteredItem != null) {
                filteredChildren.add(filteredItem);
            }
        }

        // Don't return empty folders.
        if (filteredChildren.isEmpty()) {
            return null;
        }
        return childrenFiltered ? new DatabaseFolder(getUniqueName(), getName(), filteredChildren) : this;
    }
}
