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
 * StringPropertiesSupport.java
 * Creation date: Jun 6, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.internal.foreignsupport.module.StringProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Contains helper methods for the CAL module to work with the {@link java.util.Properties} class.
 *
 * @author Joseph Wong
 */
public final class StringPropertiesSupport {

    /**
     * Private constructor.
     */
    private StringPropertiesSupport() {
    }
    

    /**
     * Constructs a new {@link Properties} instance based on the text file given in the specified InputStream,
     * and the specified defaults. If the loading from the stream fails, then the defaults is returned.
     * 
     * @param inputStream the InputStream containing a text file with key-value pairs.
     * @return an instance of Properties - the newly constructed instance if the loading succeeds, or the specified defaults
     *         if the loading failed.
     */
    public static Properties makeProperties(InputStream inputStream, Properties defaults) {
        try {
            Properties properties = new Properties(defaults);
            properties.load(inputStream);
            inputStream.close();
            return properties;
        } catch (IOException e) {
            // something wrong happened loading from the stream, so at least use the defaults
            return defaults;
        }
    }      
    
    /**
     * Returns a list of the keys defined in the specified Properties instance (and
     * its chain of defaults instances). This is an encapsulation of the {@link Properties#propertyNames()} method.
     * 
     * @param properties the Properties instance.
     * @return a List of all the keys in the Properties instance, including the keys in the default property list.
     */
    public static List<?> getPropertyNames(Properties properties) {
        List<Object> result = new ArrayList<Object>();
        
        for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
            result.add(e.nextElement());
        }
        
        return result;
    }
}
