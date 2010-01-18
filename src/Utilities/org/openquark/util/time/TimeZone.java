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
 * TimeZone.java
 * Created: 17-Oct-2003
 * By: Rick Cameron
 */

package org.openquark.util.time;

import java.util.Locale;

import org.openquark.util.Messages;


/**
 * Instances of this class represent a time zone.
 * <p> 
 * The class is implemented using an instance of <code>com.ibm.icu.util.TimeZone</code>. 
 * The main reasons for not using the ICU class directly are:
 * <ul>
 * <li>it is not immutable, and we want an immutable representation of time zones
 * <li>we want the public interface of this class to be simple so that it can be ported to other environments, such as C#/.NET
 * </ul> 
 * @author Rick Cameron
 */
public final class TimeZone {
    private final com.ibm.icu.util.TimeZone timeZone;
    
    /** Use this message bundle to dig up localized messages */
    private static final Messages messages = PackageMessages.instance;
    
    /**
     * Method utc
     * 
     * @return a TimeZone representing UTC
     */
    public static TimeZone utc () {
        return getInstance("UTC");
    }
    
    /**
     * Method local
     * 
     * @return a TimeZone representing the local time zone on this computer
     */
    public static TimeZone local () {
        return new TimeZone (com.ibm.icu.util.TimeZone.getDefault());
    }
    
    /**
     * Method getInstance
     * 
     * @param id - the IDs recognised are defined by ICU
     * @return a TimeZone for the given ID
     */
    public static TimeZone getInstance (String id) {
        com.ibm.icu.util.TimeZone tz = com.ibm.icu.util.TimeZone.getTimeZone(id);
        
        // If the ID is unknown, and not a custom one, throw an exception
        if (!tz.getID().equals(id) && !isCustomID (id)) {
            throw new IllegalArgumentException (messages.getString("TheIdDoesNotMatchTZ", id));
        }
        
        return new TimeZone (tz);
    }
    
    /**
     * Method isCustomID
     * 
     * @param id
     * 
     * @return Returns true if the id is in one of the forms "GMT[+-]hh:mm", "GMT[+-]hhmm" or "GMT[+-]hh"
     */
    private static boolean isCustomID (String id) {
        return id.matches("GMT[+-]\\d{1,2}(:?\\d{1,2})?");
    }

    /**
     * Constructor TimeZone
     * 
     * @param timeZone
     */
    private TimeZone (com.ibm.icu.util.TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString () {
        return "TimeZone <" + timeZone.getDisplayName() +">";
    }
    
    /**
     * Method toICUTimeZone
     * 
     * This method is intended to be used only by related implementation classes.
     * It is public because such classes may be in a different package.
     * 
     * @return an instance of com.ibm.icu.util.TimeZone corresponding to this TimeZone
     */
    public com.ibm.icu.util.TimeZone toICUTimeZone () {
        return com.ibm.icu.util.TimeZone.getTimeZone(timeZone.getID());
    }
    
    /**
     * @return the ID of this time zone.
     */
    public String getID() {
        return timeZone.getID();
    }
    
    /**
     * @param locale the locale in which the display name is localized.
     * @return the localized short display name.
     */
    public String getShortDisplayName(Locale locale) {
        return timeZone.getDisplayName(false, com.ibm.icu.util.TimeZone.SHORT, locale);
    }
    
    /**
     * @param locale the locale in which the display name is localized.
     * @return the localized long display name.
     */
    public String getLongDisplayName(Locale locale) {
        return timeZone.getDisplayName(false, com.ibm.icu.util.TimeZone.LONG, locale);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof TimeZone && (getID().equals(((TimeZone)other).getID()));
    }
    
    @Override
    public int hashCode() {
        return getID().hashCode();
    }
}
