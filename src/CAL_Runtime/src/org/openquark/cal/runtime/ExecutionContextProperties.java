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
 * ExecutionContextProperties.java
 * Creation date: May 31, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.openquark.cal.services.LocaleUtilities;


/**
 * This class encapsulates an <em>immutable</em> map of key-value pairs which are exposed in CAL as
 * system properties. Only strings can be used as keys.
 * <p>
 * There are a number of keys whose values are constrained to be of a particular type, e.g.
 * the value for the "cal.locale" property must be of type {@link java.util.Locale}. For these
 * special keys, dedicated methods are available for accessing their values. These properties are
 * known as <em>system properties</em>.
 * <p>
 * For other keys, their corresponding values can be objects of any type. It is up to the client
 * code to coordinate the setting of the property on the Java side with the retrieval on the CAL side.
 * <p>
 * Note that instances of this class are immutable. In fact, the only way to build an instance is through
 * the {@link ExecutionContextProperties.Builder} class, which contains methods for adding key-value pairs,
 * and for finally building an instance of this class.
 *
 * @author Joseph Wong
 */
public final class ExecutionContextProperties {
    
    /**
     * An instance of this class can be used to gather up key-value pairs to turn into a
     * {@link ExecutionContextProperties} instance.
     *
     * @author Joseph Wong
     */
    public static final class Builder {
                       
        /**
         * The map of string-valued keys to their values. Does not include cal.timezone.
         */
        private final Map<String, Object> properties = new HashMap<String, Object>();
        
        /**
         * The value for the cal.timezone property. It will be null if the time zone is the default time zone and then
         * lazily initialized. This is to speed up class loading on startup when time zones are not used.
         */
        private TimeZone timeZone;
        
        /**
         * Constructs a Builder.
         */
        public Builder() {
            properties.put(ExecutionContextProperties.SYS_PROP_KEY_LOCALE, LocaleUtilities.INVARIANT_LOCALE);
            //do not do this. We don't want to load the TimeZone related classes if they are not used.
            //properties.put(ExecutionContextProperties.SYS_PROP_KEY_TIMEZONE, TimeZone.local());
        }
                       
        /**
         * Sets the current locale associated with the execution context.
         * @param locale the locale.
         */
        public void setLocale(Locale locale) {
            if (locale == null) {
                throw new NullPointerException("locale cannot be null.");
            }
            
            properties.put(ExecutionContextProperties.SYS_PROP_KEY_LOCALE, locale);
        }
        
        /**
         * Sets the current time zone associated with the execution context.
         * @param timeZone the time zone.
         */
        public void setTimeZone(TimeZone timeZone) {
            if (timeZone == null) {
                throw new NullPointerException("timeZone cannot be null.");
            }
            this.timeZone = timeZone;           
        }
        
        /**
         * Sets the current time zone associated with the execution context.
         * @param timeZoneID the time zone ID.
         */
        public void setTimeZone(String timeZoneID) {
            if (timeZoneID == null) {
                throw new NullPointerException("timeZone cannot be null.");
            }
            this.timeZone = TimeZone.getTimeZone(timeZoneID);           
        }
        
        /**
         * Sets the value for a particular key. This method cannot be used for system properties - for these properties,
         * use the dedicated set methods instead.
         * 
         * @param key the key of the property.
         * @param value the value of the property.
         */
        public void setProperty(String key, Object value) {
            if (ExecutionContextProperties.SYSTEM_PROPERTY_KEYS.contains(key)) {
                throw new IllegalArgumentException("The property " + key + " is a system property. Set it via a dedicated setter method.");
            }
            
            properties.put(key, value);
        }
        
        /**
         * Builds an {@link ExecutionContextProperties} instance based on the information gathered so far.
         * @return a new instance of {@link ExecutionContextProperties}.
         */
        public ExecutionContextProperties toProperties() {
            return new ExecutionContextProperties(properties, timeZone);
        }
    }   
    
    /**
     * A set of all the keys of the well-known system properties.
     */
    private static final Set<String> SYSTEM_PROPERTY_KEYS = new HashSet<String>();
    
    /**
     * The system property key for the current locale associated with the execution context. 
     */
    public static final String SYS_PROP_KEY_LOCALE = makeSystemPropertyKey("cal.locale");
    
    /**
     * The system property key for the current time zone associated with the execution context.
     */
    public static final String SYS_PROP_KEY_TIMEZONE = makeSystemPropertyKey("cal.timezone");
            
    /**
     * The map of string-valued keys to their values. Does not include the cal.timezone property.
     */
    private final Map<String, Object> properties;
    
    /**
     * Value of the cal.timezone property. This is lazily initialized. Guarded by this.
     */
    private TimeZone timeZone;
    
    /**
     * Helper for creating a system property key. The key is registered with {@link #SYSTEM_PROPERTY_KEYS}.
     * @param key the system property key.
     * @return the key itself.
     */
    private static String makeSystemPropertyKey(String key) {
        SYSTEM_PROPERTY_KEYS.add(key);
        return key;
    }
    
    /**
     * Private constructor meant to be called only by {@link ExecutionContextProperties.Builder#toProperties}.
     * @param properties the map of string-valued keys to their values.
     * @param timeZone timeZone
     */
    private ExecutionContextProperties(Map<String, Object> properties, TimeZone timeZone) {
        if (properties == null) {
            throw new NullPointerException();
        }
        
        // make a copy of the map (since the Builder could still be around and modify its map)
        this.properties = new HashMap<String, Object>(properties);
        this.timeZone = timeZone;
    }
    
    /**
     * Returns whether the specified property is defined.
     * @param key the key for the property.
     * @return true if the property is defined.
     */
    public boolean hasProperty(String key) {
        if (key.equals(SYS_PROP_KEY_TIMEZONE)) {
            return true;
        }
        
        return properties.containsKey(key);
    }
    
    /**
     * Returns the value of the specified property.
     * @param key the key for the property.
     * @return the value of the property, or null if there is no such property.
     */
    public Object getProperty(String key) {
        if (key.equals(SYS_PROP_KEY_TIMEZONE)) {
            return getTimeZone();
        }
        
        return properties.get(key);
    }
    
    /**
     * Returns the keys of all the defined properties.
     * @return a List of the keys of all the defined properties.
     */
    public List<String> getPropertyKeys() {
        List<String> result = new ArrayList<String>(properties.keySet());
        result.add(SYS_PROP_KEY_TIMEZONE);
        Collections.sort(result);
        return result;
    }
    
    /**
     * @return the value of the system property for the current locale associated with the execution context.
     */
    public Locale getLocale() {
        return (Locale)getProperty(SYS_PROP_KEY_LOCALE);
    }
    
    /**
     * @return the value of the system property for the current time zone associated with the execution context.
     */
    public synchronized TimeZone getTimeZone() {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        
        return timeZone;
    }
}