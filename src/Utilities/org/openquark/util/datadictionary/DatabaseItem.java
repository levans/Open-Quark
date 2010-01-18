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
 * DatabaseItem.java
 * Creation date (Oct 5, 2004).
 * By: Richard Webster
 */
package org.openquark.util.datadictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openquark.util.SearchableObject;


/**
 * A base class for database fields and folders.
 * @author Richard Webster
 */
public abstract class DatabaseItem implements SearchableObject {
    /** The unique name of the database item */
    private final String uniqueName;

    /** The name of the database item. */
    private final String name;

    /** The children of this database item. */
    private final List<DatabaseItem> children;

    /**
     * DatabaseItem constructor.
     * @param uniqueName the unique name of the database item
     * @param name  the name of the database item
     */
    public DatabaseItem(String uniqueName, String name) {
        this(uniqueName, name, Collections.<DatabaseItem>emptyList());
    }

    /**
     * DatabaseItem constructor.
     * @param uniqueName the unique name of the database item
     * @param name       the name of the database item
     * @param children   the children of the database item
     */
    public DatabaseItem(String uniqueName, String name, Collection<DatabaseItem> children) {
        super();
        this.uniqueName = uniqueName;
        this.name = name;
        if (children != null && !children.isEmpty()) {
            this.children = new ArrayList<DatabaseItem>(children);
        }
        else {
            this.children = Collections.emptyList();
        }
    }
    
    /**
     * Returns the unique name of the database item.
     * @return String
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Returns the name of the database item.
     * @return the name of the database item
     */
    public String getName() {
        return name;
    }
    
    /**
     * @see org.openquark.util.SearchableObject#getSearchString()
     */
    public String getSearchString() {
        return name;
    }
    
    /**
     * Returns the items in this folder.
     * @return the items in this folder
     */
    public List<DatabaseItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        return ((DatabaseItem) obj).uniqueName.equals(uniqueName);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return uniqueName.hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a version of the item with the specified filtering applied.
     * The original item will not be modified.
     * @param fieldFilter  the field filter
     * @return a version of the item with the filtering applied
     */
    public abstract DatabaseItem filterItem(DatabaseFieldFilter fieldFilter);
}
