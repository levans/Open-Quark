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
 * GemEventMulticaster.java
 * Creation date: (02/25/02 12:10:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.EventListener;

/**
 * A multicaster class, modeled after AWTEventMulticaster, that provides a way to manage listeners on 
 * various gem events.  It is quite efficient for relatively short lists of listeners.
 * 
 * Creation date: (02/25/02 12:10:00 PM)
 * @author Edward Lam
 * @see java.awt.AWTEventMulticaster
 */
public class GemEventMulticaster 
        implements DisplayedConnectionStateListener, DisplayedGemLocationListener, DisplayedGemSizeListener, DisplayedGemStateListener, 
        BurnListener, GemConnectionListener, CodeGemDefinitionChangeListener, GemStateListener, NameChangeListener {

    protected final EventListener a, b;

    /**
     * Default constructor for a gem event multicaster
     * Creation date: (02/25/02 12:10:00 PM)
     */
    protected GemEventMulticaster(EventListener a, EventListener b) {
        this.a = a;
        this.b = b;
    }

    /** 
     * Returns the resulting multicast listener from adding listener-a and listener-b together.  
     * If listener-a is null, it returns listener-b;  
     * If listener-b is null, it returns listener-a
     * If neither are null, then it creates and returns a new AWTEventMulticaster instance which chains a with b.
     * @param a event listener-a
     * @param b event listener-b
     */
    protected static EventListener addInternal(EventListener a, EventListener b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new GemEventMulticaster(a, b);
    }

    /** 
     * Returns the resulting multicast listener after removing the old listener from listener-l.
     * If listener-l equals the old listener OR listener-l is null, returns null.
     * Else if listener-l is an instance of AWTEventMulticaster, then it removes the old listener from it.
     * Else, returns listener l.
     * 
     * Creation date: (02/25/02 12:15:00 PM)
     * @param l the listener being removed from
     * @param oldl the listener being removed
     */
    protected static EventListener removeInternal(EventListener l, EventListener oldl) {
        if (l == oldl || l == null) {
            return null;

        } else if (l instanceof GemEventMulticaster) {
            return ((GemEventMulticaster)l).remove(oldl);

        } else {
            return l;       // it's not here
        }
    }

    /**
     * Removes a listener from this multicaster and returns the resulting multicast listener.
     * @param oldl the listener to be removed
     */
    protected EventListener remove(EventListener oldl) {
        if (oldl == a) {
            return b;
        }
        if (oldl == b) {
            return a;
        }

        EventListener a2 = removeInternal(a, oldl);
        EventListener b2 = removeInternal(b, oldl);

        if (a2 == a && b2 == b) {
            return this;    // it's not here
        }

        return addInternal(a2, b2);
    }

    /**
     * Adds burn-listener-a with burn-listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/25/02 4:52:00 PM)
     * @param a burn-listener-a
     * @param b burn-listener-b
     */
    public static BurnListener add(BurnListener a, BurnListener b) {
        return (BurnListener)addInternal(a, b);
    }
    
    /**
     * Removes the old burn-listener from burn-listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/25/02 4:52:00 PM)
     * @param l burn-listener-l
     * @param oldl the burn-listener being removed
     */
    public static BurnListener remove(BurnListener l, BurnListener oldl) {
        return (BurnListener) removeInternal(l, oldl);
    }

    /**
     * Adds name change listener-a with name change listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/28/02 5:26:00 PM)
     * @param a name change listener-a
     * @param b name change listener-b
     */
    public static NameChangeListener add(NameChangeListener a, NameChangeListener b) {
        return (NameChangeListener)addInternal(a, b);
    }
    
    /**
     * Removes the old name change listener from name change listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/28/02 5:28:00 PM)
     * @param l name change listener-l
     * @param oldl the name change listener being removed
     */
    public static NameChangeListener remove(NameChangeListener l, NameChangeListener oldl) {
        return (NameChangeListener) removeInternal(l, oldl);
    }

    /**
     * Adds state change listener-a with state change listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 12:22:00 PM)
     * @param a state change listener-a
     * @param b state change listener-b
     */
    public static GemStateListener add(GemStateListener a, GemStateListener b) {
        return (GemStateListener)addInternal(a, b);
    }
    
    /**
     * Removes the old state change listener from state change listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 12:23:00 PM)
     * @param l state change listener-l
     * @param oldl the state change listener being removed
     */
    public static GemStateListener remove(GemStateListener l, GemStateListener oldl) {
        return (GemStateListener) removeInternal(l, oldl);
    }

    /**
     * Adds code change listener-a with code change listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 1:31:00 PM)
     * @param a code change listener-a
     * @param b code change listener-b
     */
    public static CodeGemDefinitionChangeListener add(CodeGemDefinitionChangeListener a, CodeGemDefinitionChangeListener b) {
        return (CodeGemDefinitionChangeListener)addInternal(a, b);
    }
    
    /**
     * Removes the old code change listener from code change listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 1:31:00 PM)
     * @param l code change listener-l
     * @param oldl the code change listener being removed
     */
    public static CodeGemDefinitionChangeListener remove(CodeGemDefinitionChangeListener l, CodeGemDefinitionChangeListener oldl) {
        return (CodeGemDefinitionChangeListener) removeInternal(l, oldl);
    }

    /**
     * Adds gem connection change listener-a with gem connection change listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 4:09:00 PM)
     * @param a gem connection change listener-a
     * @param b gem connection change listener-b
     */
    public static GemConnectionListener add(GemConnectionListener a, GemConnectionListener b) {
        return (GemConnectionListener)addInternal(a, b);
    }
    
    /**
     * Removes the old gem connection change listener from gem connection change listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 4:11:00 PM)
     * @param l gem connection change listener-l
     * @param oldl the gem connection change listener being removed
     */
    public static GemConnectionListener remove(GemConnectionListener l, GemConnectionListener oldl) {
        return (GemConnectionListener) removeInternal(l, oldl);
    }

    /**
     * Adds gem state listener-a with gem state listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (03/04/02 4:20:00 PM)
     * @param a gem state listener-a
     * @param b gem state listener-b
     */
    public static DisplayedConnectionStateListener add(DisplayedConnectionStateListener a, DisplayedConnectionStateListener b) {
        return (DisplayedConnectionStateListener)addInternal(a, b);
    }
    
    /**
     * Removes the old gem state listener from gem state listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (03/04/02 4:21:00 PM)
     * @param l gem state listener-l
     * @param oldl the gem state listener being removed
     */
    public static DisplayedConnectionStateListener remove(DisplayedConnectionStateListener l, DisplayedConnectionStateListener oldl) {
        return (DisplayedConnectionStateListener) removeInternal(l, oldl);
    }

    /**
     * Adds location change listener-a with location change listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/27/02 5:25:00 PM)
     * @param a location change listener-a
     * @param b location change listener-b
     */
    public static DisplayedGemLocationListener add(DisplayedGemLocationListener a, DisplayedGemLocationListener b) {
        return (DisplayedGemLocationListener)addInternal(a, b);
    }
    
    /**
     * Removes the old location change listener from location change listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/27/02 5:28:00 AM)
     * @param l location change listener-l
     * @param oldl the location change listener being removed
     */
    public static DisplayedGemLocationListener remove(DisplayedGemLocationListener l, DisplayedGemLocationListener oldl) {
        return (DisplayedGemLocationListener) removeInternal(l, oldl);
    }

    /**
     * Adds size change listener-a with size change listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/28/02 5:11:00 PM)
     * @param a size change listener-a
     * @param b size change listener-b
     */
    public static DisplayedGemSizeListener add(DisplayedGemSizeListener a, DisplayedGemSizeListener b) {
        return (DisplayedGemSizeListener)addInternal(a, b);
    }
    
    /**
     * Removes the old size change listener from size change listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/28/02 5:11:00 AM)
     * @param l size change listener-l
     * @param oldl the size change listener being removed
     */
    public static DisplayedGemSizeListener remove(DisplayedGemSizeListener l, DisplayedGemSizeListener oldl) {
        return (DisplayedGemSizeListener) removeInternal(l, oldl);
    }

    /**
     * Adds state change listener-a with state change listener-b and returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 11:18:00 AM)
     * @param a state change listener-a
     * @param b state change listener-b
     */
    public static DisplayedGemStateListener add(DisplayedGemStateListener a, DisplayedGemStateListener b) {
        return (DisplayedGemStateListener)addInternal(a, b);
    }
    
    /**
     * Removes the old state change listener from state change listener-l and
     * returns the resulting multicast listener.
     * 
     * Creation date: (02/26/02 11:18:00 AM)
     * @param l state change listener-l
     * @param oldl the state change listener being removed
     */
    public static DisplayedGemStateListener remove(DisplayedGemStateListener l, DisplayedGemStateListener oldl) {
        return (DisplayedGemStateListener) removeInternal(l, oldl);
    }

    /**
     * Notify listeners that the run state changed.
     * Creation date: (03/04/02 4:08:00 PM)
     * @param e ConnectionStateEvent the related event.
     */
    public void badStateChanged(DisplayedConnectionStateEvent e){
        ((DisplayedConnectionStateListener)a).badStateChanged(e);
        ((DisplayedConnectionStateListener)b).badStateChanged(e);
    }

    /**
     * Notify listeners that the gem location changed.
     * Creation date: (03/04/02 3:35:00 PM)
     * @param e DisplayedGemLocationEvent the related event.
     */
    public void gemLocationChanged(DisplayedGemLocationEvent e){
        ((DisplayedGemLocationListener)a).gemLocationChanged(e);
        ((DisplayedGemLocationListener)b).gemLocationChanged(e);
    }

    /**
     * Notify listeners that the gem size changed.
     * Creation date: (03/04/02 3:35:00 PM)
     * @param e DisplayedGemSizeEvent the related event.
     */
    public void gemSizeChanged(DisplayedGemSizeEvent e) {
        ((DisplayedGemSizeListener)a).gemSizeChanged(e);
        ((DisplayedGemSizeListener)b).gemSizeChanged(e);
    }

    /**
     * Handles the inputBurntStateChanged event by invoking the
     * inputBurntStateChanged methods on listener-a and listener-b.
     * 
     * Creation date: (02/25/02 4:57:00 PM)
     * @param e the burn event
     */
    public void burntStateChanged(BurnEvent e){
        ((BurnListener)a).burntStateChanged(e);
        ((BurnListener)b).burntStateChanged(e);
    }

    /**
     * Notify listeners that the run state changed.
     * Creation date: (02/26/02 10:50:00 AM)
     * @param e GemStateEvent the related event.
     */
    public void runStateChanged(DisplayedGemStateEvent e) {
        ((DisplayedGemStateListener)a).runStateChanged(e);
        ((DisplayedGemStateListener)b).runStateChanged(e);
    }

    /**
     * Notify listeners that the selection state changed.
     * Creation date: (02/26/02 10:50:00 AM)
     * @param e GemStateEvent the related event.
     */
    public void selectionStateChanged(DisplayedGemStateEvent e) {
        ((DisplayedGemStateListener)a).selectionStateChanged(e);
        ((DisplayedGemStateListener)b).selectionStateChanged(e);
    }

    /**
     * Notify listeners that the broken state changed.
     * Creation date: (02/26/02 12:19:00 PM)
     * @param e GemStateEvent the related event.
     */
    public void brokenStateChanged(GemStateEvent e){
        ((GemStateListener)a).brokenStateChanged(e);
        ((GemStateListener)b).brokenStateChanged(e);
    }

    /**
     * Notify the listener that the gem definition has been changed.
     * Creation date: (02/26/02 1:34:00 PM)
     * @param e CodeGemDefinitionEditEvent the related event.
     */
    public void codeGemDefinitionChanged(CodeGemDefinitionChangeEvent e) {
        ((CodeGemDefinitionChangeListener)a).codeGemDefinitionChanged(e);
        ((CodeGemDefinitionChangeListener)b).codeGemDefinitionChanged(e);
    }

    /**
     * Notify listeners that a connection event occurred
     * Creation date: (02/26/02 4:00:00 PM)
     * @param e GemConnectionEvent the related event.
     */
    public void connectionOccurred(GemConnectionEvent e){
        ((GemConnectionListener)a).connectionOccurred(e);
        ((GemConnectionListener)b).connectionOccurred(e);
    }

    /**
     * Notify listeners that a connection event occurred
     * Creation date: (02/26/02 4:00:00 PM)
     * @param e GemConnectionEvent the related event.
     */
    public void disconnectionOccurred(GemConnectionEvent e){
        ((GemConnectionListener)a).disconnectionOccurred(e);
        ((GemConnectionListener)b).disconnectionOccurred(e);
    }

    /**
     * Notify listeners that the gem name changed.
     * Creation date: (03/04/02 3:38:00 PM)
     * @param e NameChangeEvent the related event.
     */
    public void nameChanged(NameChangeEvent e) {
        ((NameChangeListener)a).nameChanged(e);
        ((NameChangeListener)b).nameChanged(e);
    }


}
