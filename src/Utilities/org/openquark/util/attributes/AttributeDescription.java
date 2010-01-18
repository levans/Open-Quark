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
 * AttributeDescription.java
 * Created: 7-Jan-2003
 * By: Richard Webster
 */
package org.openquark.util.attributes;

import org.openquark.util.EnumValues;
import org.openquark.util.Messages;

/**
 * Information describing an attribute.
 * 
 * @author Richard Webster
 */
public class AttributeDescription {
    public final String name;
    public final Class<?> dataType;
    public final Object defaultValue; // may be null, but if not, it must be an instance of dataType
    public final EnumValues enumValues;
    public final boolean allowMultipleValues;

    /** Use this message bundle to dig up localized messages */
    private static final Messages messages = PackageMessages.instance;
    
    /**
     * Constructor for AttributeDescription.
     * @param name
     * @param dataType
     * @param defaultValue
     * @param enumValues
     * @param allowMultipleValues
     */
    public AttributeDescription (String name,
                                 Class<?> dataType,
                                 Object defaultValue,
                                 EnumValues enumValues,
                                 boolean allowMultipleValues) {
        if (defaultValue != null && defaultValue.getClass() != dataType)
            throw new IllegalArgumentException (messages.getString("AttributeDescriptionDefaultArgError")); //$NON-NLS-1$
            
        this.name = name;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.enumValues = enumValues;
        this.allowMultipleValues = allowMultipleValues;
    }

    /**
     * Constructor AttributeDescription
     * 
     * @param name
     * @param dataType
     * @param defaultValue
     * @param allowMultipleValues
     */
    public AttributeDescription (String name,
                                 Class<?> dataType,
                                 Object defaultValue,
                                 boolean allowMultipleValues) {
        this (name, dataType, defaultValue, null, allowMultipleValues);
    }
    
    /**
     * Constructor for AttributeDescription.
     * @param name
     * @param dataType
     * @param defaultValue
     */
    public AttributeDescription (String name,
                                 Class<?> dataType,
                                 Object defaultValue) {
        this (name, dataType, defaultValue, null, false);
    }

    /**
     * Constructor AttributeDescription
     * 
     * @param name
     * @param defaultValue
     * @param enumValues
     * @param allowMultipleValues
     */
    public AttributeDescription (String name,
                                 int defaultValue,
                                 EnumValues enumValues,
                                 boolean allowMultipleValues) {
        this (name, Integer.class, new Integer (defaultValue), enumValues, allowMultipleValues);                                     
    }

}



