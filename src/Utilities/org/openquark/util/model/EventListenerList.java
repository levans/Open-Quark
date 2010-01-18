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
 * EventListenerList.java
 * Created: Mar 8, 2005
 * By: ksit
 */
package org.openquark.util.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;


/**
 * A data structure for managing a list of event listeners.  At this moment,
 * this class does not perform the same as <code>EventListenerList</code>
 * in the Swing pacakge - this class assumes that only one type of listener
 * can be stored in the list. 
 */
public class EventListenerList {
    
    /** A list of listeners */
    private final List<EventListener> listeners = new ArrayList<EventListener>();
    
    /** The listener class */
    private final Class<? extends EventListener> listenerClass;
    
    /**
     * Constructor for EventListenerList
     * @param listenerClass
     */
    public EventListenerList(Class<? extends EventListener> listenerClass) {
        if (listenerClass == null) {
            throw new IllegalArgumentException("Listener class cannot be null");
        }
        this.listenerClass = listenerClass;
    }
    
    /**
     * Returns an <code>Iterator</code> to the list of listeners contained
     * in this listener list.
     * @return Iterator
     */
    public Iterator<EventListener> getListeners() {
        // Make a copy so that listeners can register / deregister in response to the
        // event without leading to a concurrent modification exception
        return Collections.unmodifiableList(new ArrayList<EventListener>(listeners)).iterator();
    }
    
    /**
     * Adds a listener to the event listener list.  Any attempt to add an existing
     * listener to the list will cause an exception to be thrown.
     * @param l
     */
    public void add(EventListener l) {
        if (l instanceof EventListenerBridge) {
            add((EventListenerBridge) l);
        } else if (!listeners.contains(l)) {
            checkListenerClass(l);
            listeners.add(l);
        } else {
            throw new IllegalArgumentException("Listener " + l + " already exist");
        }
    }
    
    /**
     * Adds a event listener bridge to the event listener list.
     * @param b
     */
    public void add(EventListenerBridge b) {
        // Only one bridge can exist for each listener because no duplicate
        // listener can exist in the listener list
        checkListenerClass(b);
        EventListener bridged = b.getBridgedListener();
        if (!listeners.contains(b)) {
            checkBridgedListenerUniqueness(bridged);
            listeners.add(b);
        }
    }
    
    /**
     * A helper method for checking if the given listener matches the listener class.
     * @param l
     */
    private void checkListenerClass(EventListener l) {
        if (!listenerClass.isInstance(l)) {
            throw new IllegalArgumentException("Listener " + l + " is not of type " + listenerClass.getName());
        }
    }
    
    /**
     * A helper method for checking if the given listener is unique.
     * @param l
     */
    private void checkBridgedListenerUniqueness(EventListener l) {
        for (int i = 0; i < listeners.size(); i++) {
            Object listener = listeners.get(i);
            if (listener instanceof EventListenerBridge) {
                EventListener b = ((EventListenerBridge) listener).getBridgedListener();
                if (l == b) {
                    throw new IllegalArgumentException("The bridged listener " + b + " already exists in the list.");
                }
            }
        }
    }
    
    /**
     * Removes a listener from the event listener list.  If the entry cannot
     * be found, then nothing will be done.
     * @param l
     */
    public void remove(EventListener l) {
        listeners.remove(l);
    }
    
    /**
     * Removes the given event listener bridge from the list.  If the entry
     * cannot be found, then nothing will be done.
     * @param b
     */
    public void remove(EventListenerBridge b) {
        // This is the same as the other method, and we leave this method here
        // to match the add(EventListenerBridge) method in this class.
        listeners.remove(b);
    }
    
    /**
     * Removes the wrapped listener that serves as the listener bridge for the
     * given listener.  If the bridged listener cannot be found, then nothing
     * will be done.
     * @param l
     */
    public void removeBridged(EventListener l) {
        for (int i = 0; i < listeners.size(); i++) {
            Object listener = listeners.get(i);
            if (listener instanceof EventListenerBridge) {
                EventListener b = ((EventListenerBridge) listener).getBridgedListener();
                if (l == b) {
                    remove((EventListenerBridge) listener);
                }
            }
        }
    }
    
}
