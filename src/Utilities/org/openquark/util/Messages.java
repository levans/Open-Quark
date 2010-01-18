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
 * Messages.java
 * Created: Jul 26, 2004
 * By: Kevin Sit
 */
package org.openquark.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for managing resource strings.
 */
public final class Messages {
    
    /**
     * Use this bundle name to construct the resource bundle.
     */
    private final String bundleName;
    /**
     * The resource bundle, initially set to null.
     */
    private ResourceBundle resourceBundle;
    
    /**
     * The locale
     */
    private final Locale locale;
    
    
    /**
     * The constructor that accepts a fully qualified resource bundle name
     * and locale as input.
     * <p>
     * Subclasses should not override this method, nor changing the visibility of
     * this constructor.
     * @param bundleName
     * @param locale
     */
    public Messages(String bundleName,Locale locale) {
        this.bundleName = bundleName;
        this.locale = locale;
    }
    
    /**
     * This constructor accepts a class and a short bundle name as inputs.  The
     * class is used to compute the package name of the bundle.
     * The locale is set to the default
     * <p>
     * Subclasses should not override this method, nor changing the visibility of
     * this constructor.
     * @param klass
     * @param bundleName
     */
    public Messages(Class<?> klass, String bundleName) {
        this(getFullyQualifiedBundleName(klass, bundleName),Locale.getDefault());
    }
    
    /**
     * This constructor accepts a class and a short bundle name plus a locale as inputs.  The
     * class is used to compute the package name of the bundle.
     * <p>
     * Subclasses should not override this method, nor changing the visibility of
     * this constructor.
     * @param klass
     * @param bundleName
     * @param locale
     */
    public Messages(Class<?> klass, String bundleName,Locale locale) {
        this(getFullyQualifiedBundleName(klass, bundleName),locale);
    }
    
    /**
     * Returns true if the given key exists in the resource bundle embedded in
     * the given message info object.
     * @param key
     * @return boolean
     */
    public boolean hasString(String key) {
        try {
            ensureLoaded();
            return resourceBundle.getString(key) != null;
        } catch (MissingResourceException mre) {
            return false;
        }
    }
    
    /**
     * Returns the string referenced by the given key in the resource bundle.
     * @param key
     * @return String
     */
    public String getString(String key) {
        try {
            ensureLoaded();
            return resourceBundle.getString(key);
        } catch (MissingResourceException mre) {
            return '!' + key + '!';
        }
    }
    
    /**
     * Returns the string referenced by the given, with the argument placeholders
     * replaced with items present in the given argument array.
     * @param key
     * @param argv
     * @return String
     */
    public String getString(String key, Object[] argv) {
        try {
            ensureLoaded();
            return MessageFormat.format(resourceBundle.getString(key), argv);
        } catch (MissingResourceException mre) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the string referenced by the given, with the argument placeholders
     * replaced with item arg0.
     * @param key
     * @param arg0
     * @return String
     */
    public String getString(String key, Object arg0) {
        try {
            ensureLoaded();
            return MessageFormat.format(resourceBundle.getString(key), new Object[] { arg0 });
        } catch (MissingResourceException mre) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the string referenced by the given, with the argument placeholders
     * replaced with items arg0 and arg1.
     * @param key
     * @param arg0
     * @param arg1
     * @return String
     */
    public String getString(String key, Object arg0, Object arg1) {
        try {
            ensureLoaded();
            return MessageFormat.format(resourceBundle.getString(key), new Object[] { arg0, arg1 });
        } catch (MissingResourceException mre) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the string referenced by the given, with the argument placeholders
     * replaced with items arg0, arg1, and arg2.
     * @param key
     * @param arg0
     * @param arg1
     * @param arg2
     * @return String
     */
    public String getString(String key, Object arg0, Object arg1, Object arg2) {
        try {
            ensureLoaded();
            return MessageFormat.format(resourceBundle.getString(key), new Object[] { arg0, arg1, arg2 });
        } catch (MissingResourceException mre) {
            return '!' + key + '!';
        }
    }

    /**
     * A convenient method for figuring out the fully qualified name of
     * a resource bundle.
     * @param klass
     * @param name
     * @return String
     */
    public static String getFullyQualifiedBundleName(Class<?> klass, String name) {
        String className = klass.getName();
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex < 0) {
            // the given class resides in the default package
            return name;
        } else {
            // calculate the package name and append the bundle name to the package name
            String packageName = className.substring(0, lastDotIndex);
            return packageName + "." + name;
        }
    }

    /**
     * Ensures the resource bundle is loaded.
     */
    private void ensureLoaded() throws MissingResourceException {
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle(bundleName,locale);
        }
    }
}