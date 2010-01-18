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
 * SortedListModel.java
 * 
 * Based on the 'Using TreeSet to provide a sorted JList' tutorial at developers.sun.com.
 * 
 * The example was modified to pass in a comparator to allow different sorting to be specified.  As well
 * as cleaning up the JavaDoc and making the method names consistent with the DefaultListModel class. 
 * 
 * 
 * On creation of this class, this tutorial made no reference to any license.
 * The page referencing the code had the following statement:
 * 
 *   Unless otherwise licensed, code in all technical manuals herein (including articles, FAQs, samples)
 *   is provided under this License. 
 *   
 *   
 * The word "Licence" above was hyperlinked to http://developers.sun.com/license/berkeley_license.html.
 * This read as follows:
 * 
 *   Code sample 
 *   License
 *   
 *   Copyright 1994-2006 Sun Microsystems, Inc. All Rights Reserved.
 *   
 *   Redistribution and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *   
 *     * Redistribution of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer.
 *        
 *     * Redistribution in binary form must reproduce the above copyright notice, 
 *       this list of conditions and the following disclaimer in the documentation 
 *       and/or other materials provided with the distribution.
 *            
 *   Neither the name of Sun Microsystems, Inc. or the names of contributors may be 
 *   used to endorse or promote products derived from this software without specific 
 *   prior written permission.
 *              
 *   This software is provided "AS IS," without a warranty of any kind. 
 *   ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY 
 *   IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR 
 *   NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS 
 *   LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 *   USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL 
 *   SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, 
 *   INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND 
 *   REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE 
 *   THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *               
 *   You acknowledge that this software is not designed, licensed or intended for use in 
 *   the design, construction, operation or maintenance of any nuclear facility. 
 */


/*
 * SortedListModel.java
 * Created: Sep 4, 2004
 * By: David Mosimann
 */
package org.openquark.util.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

/**
 * A list model which maintains its contents in sorted order.
 * The ordering is defined by a provided comparator.
 * 
 * @param <T> The type of the elements in the list model.
 */
public class SortedListModel<T> extends AbstractListModel {
    
    private static final long serialVersionUID = 8655910160300479643L;

    /** The sorted model that will store all of the JList elements in sorted order. */
    private SortedSet<T> model;

    /** The current comparator being used to sort the items.  Never null. */
    private Comparator<T> comparator;
    
    /**
     * Constructor for SortedListModel
     * @param comparator Used to compare elements that are added to the model and ensure that they
     * are always in sorted order.  Must not be null.
     */
    public SortedListModel(Comparator<T> comparator) {
        if (comparator == null) {
            throw new NullPointerException("Argument 'comparator' must not be null."); //$NON-NLS-1$
        }
        // Create a tree set using the comparator
        this.model = new TreeSet<T>(comparator);
        this.comparator = comparator;
    }

    /**
     * If an item is changed in a way that would affect its sorted order then the client has two choices.
     * The item can be removed an added back so that it is reinserted in the correct sorted position or
     * the client can resort then entire model by calling this method.  In general it is preferable for
     * the client to reinsert the item as it will be much more efficient and this method is only
     * provided for situations in which that may not be possible.
     * When finished a contents changed event will be fired.
     */
    public void resort() {
        // Reuse the setComparator method by simply setting the comparator to itself (resort with
        // the same comparator)
        setComparator(comparator);
    }
    
    /**
     * Changes the comparator that is used to sort the model.  The entire model will be rebuilt using
     * the new comparator.  When finished a contents changed event will be fired.
     * @param newComparator
     */
    public void setComparator(Comparator<T> newComparator) {
        if (comparator == null) {
            throw new NullPointerException("Argument 'comparator' must not be null."); //$NON-NLS-1$
        }

        // Create the new model with the new comparator
        this.comparator = newComparator;
        SortedSet<T> newModel = new TreeSet<T>(comparator);
        
        // Add all of the old items to the model.  Note that TreeSet.addAll() has an 'optimal' path
        // when adding from another TreeSet that doesn't resort the elements so we need to add one
        // at a time to force the resort.
        for (final T o : model) {
            newModel.add(o);
        }
        this.model = newModel;
        
        // Fire the contents changed event indicating that every item could have changed.
        fireContentsChanged(this, 0, getSize());
    }
    
    /**
     * @return the number of elements in the model
     */
    public int getSize() {
        return model.size();
    }

    /**
     * Returns the item at the specified index.
     * @param index
     * @return The object at the specified index.
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Object getElementAt(int index) {
        // Return the appropriate element
        return model.toArray()[index];
    }

    /**
     * Adds a new element to the model.  The model will be inserted in sorted order and
     * a contents changed event will be fired.
     * @param element
     */
    public void addElement(T element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * All of the objects in the array will be added.  The array passed in must be non-null, but
     * no sorting is assumed.  All objects from the array will be added in sorted order.  A single
     * contents changed event will be first after all the objects are added.
     * @param elements
     */
    public void addAllElements(T[] elements) {
        Collection<T> c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Removes all of the elements in the model and fires a contents changed event.
     */
    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * @param element
     * @return True if the element is in the model and false otherwise
     */
    public boolean contains(Object element) {
        return model.contains(element);
    }

    /**
     * @return Returns the first element in the model. 
     * @throws NoSuchElementException sorted set is empty.
     */
    public Object firstElement() {
        // Return the appropriate element
        return model.first();
    }

    /**
     * @return An iterator for the model.  The iterator can not be used to modify the model.
     */
    public Iterator<T> iterator() {
        return Collections.unmodifiableSet(model).iterator();
    }

    /**
     * @return Returns the last element in the model.
     * @throws NoSuchElementException sorted set is empty.
     */
    public Object lastElement() {
        // Return the appropriate element
        return model.last();
    }

    /**
     * Attempts to remove the specified element from the model.  If the element is not
     * in the model then nothing is changed.  If the element does exist then it is
     * removed and a contents changed event is fired.
     * @param element
     * @return True if the element was removed and false if the element was not found
     * in the model.
     */
    public boolean removeElement(Object element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;   
    }
}