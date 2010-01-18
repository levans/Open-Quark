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
 * NavigatorMessages.java
 * Created: Oct 25, 2004
 * By: Peter Cardwell
 */
package org.openquark.gems.client.navigator;

import org.openquark.util.Messages;

/**
 * This class holds on to an instance of the message bundle which can be used
 * by other classes within the navigator.* package.
 * <p>
 * This class should not be instantiated.
 */
public final class NavigatorMessages {
    
    /**
     * The public singleton instance for accessing the message bundle.
     */
    public static final Messages instance = new Messages(NavigatorMessages.class, "navigator_ui");
    
    /** Private constructor to keep the class from being instantiated */
    private NavigatorMessages(){}
    
    //  Boilerpate methods
    
    /**
     * Returns the string referenced by the given key in the resource bundle.
     * @param key
     * @return String
     */
    public static String getString(String key) {
        return instance.getString(key);
    }
    
    /**
     * Returns the string referenced by the given, with the argument placeholders
     * replaced with item arg0.
     * @param key
     * @param arg0
     * @return String
     */
    public static String getString(String key, Object arg0){
        return instance.getString(key, arg0);
    }
    
    /**
     * Returns the string referenced by the given, with the argument placeholders
     * replaced with items arg0 and arg1.
     * @param key
     * @param arg0
     * @param arg1
     * @return String
     */
    public static String getString(String key, Object arg0, Object arg1){
        return instance.getString(key, arg0, arg1);
    }
    
    /**
     * Returns the string referenced by the given, with the argument placeholders
     * replaced with items present in the given argument array.
     * @param key
     * @param argv
     * @return String
     */
    public static String getString(String key, Object[] argv){
        return instance.getString(key, argv);
    }
    
    /**
     * Returns true if the given key exists in the resource bundle embedded in
     * the given message info object.
     * @param key
     * @return boolean
     */
    public static boolean hasString(String key){
        return instance.hasString(key);
    }
    
}
