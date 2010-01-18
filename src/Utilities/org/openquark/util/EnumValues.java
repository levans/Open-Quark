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
 * EnumValues.java
 * Created: 9-Jan-2003
 * By: Richard Webster
 */
package org.openquark.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A set of values with associated display strings.
 * @author Richard Webster
 */
public final class EnumValues {
    
    private final Map<String, Object> displayToValueMap = new LinkedHashMap<String, Object>();
    private final Map<Object, String> valueToDisplayMap = new HashMap<Object, String>();

    /**
     * Constructor for EnumValues.
     */
    public EnumValues (List<?> values) {
        this (null, values);
    }

    /**
     * Constructor for EnumValues.
     */
    public EnumValues (String[] displayStrings, int[] intValues) {
        /*
         *  *** AUTOBOXING ** (intValues)
         */
        this(Arrays.asList(displayStrings.clone(), Arrays.asList(intValues.clone())));
    }

    /**
     * Constructor for EnumValues.
     */
    public EnumValues (List<String> displayStrings, List<?> values) {
        // Either the display strings or the values (or both) must be specified.
        if (displayStrings == null && values == null) {
            throw new NullPointerException ();
        }

        // If no display strings are specified, then create them based on the values.
        if (displayStrings == null) {
            displayStrings = new ArrayList<String>(values.size());

            for (final Object value : values) {
                displayStrings.add(value.toString());
            }
        }

        // If no values are specified, then use the display strings for the values as well.
        if (values == null) {
            values = displayStrings;
        }

        // Construct a map of display strings to values, and vice versa.
        Iterator<?> valueIt = values.iterator();
        for (final String s : displayStrings) {
            if (!valueIt.hasNext()) {
                break;
            }
            Object value = valueIt.next();
            
            displayToValueMap.put (s, value);
            valueToDisplayMap.put (value, s);
        }
    }

    /**
     * Returns a collection containing the display strings of the enum values.
     */
    public Collection<String> getDisplayValues () {
        return displayToValueMap.keySet ();
    }

    /**
     * Returns the value associated with the display string, or null if none is found.
     */
    public Object getValueForDisplayString (String displayString) {
        return displayToValueMap.get (displayString);
    }

    /**
     * Returns the display string associated with the value, or null if none is found.
     */
    public String getDisplayStringForValue (Object value) {
        return valueToDisplayMap.get (value);
    }

}

