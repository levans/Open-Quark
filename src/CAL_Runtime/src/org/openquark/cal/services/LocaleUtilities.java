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
 * LocaleHelper.java
 * Creation date: Sep 16, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.Locale;

/**
 * Encapsulates helper methods for working with Locale values.
 * 
 * Note: This class is not intended to be instantiated or subclassed.
 *
 * @author Joseph Wong
 */
public final class LocaleUtilities {
    
    /**
     * The Locale constant for the <em>invariant locale</em>, which is treated by the CAL platform
     * as the final fallback locale for the lookup of localized resources.
     * <p>
     * The invariant locale has the canonical string representation of the empty string (""), and
     * is not associated with any language or country.
     * <p>
     * While Java does not have a built-in notion of the invariant locale, other systems do, including:
     * <ul>
     *  <li>.NET's CultureInfo.InvariantCulture
     *  <li>WinXP's LOCALE_INVARIANT
     *  <li>the C locale/POSIX locale (e.g. ICU: http://icu.sourceforge.net/userguide/locale.html#canonicalization)
     * </ul>
     */
    public static final Locale INVARIANT_LOCALE = new Locale("", "", "");
    
    /** Private constructor for preventing instantiation. */
    private LocaleUtilities() {}

    /**
     * Converts a Locale value into its canonical string representation.
     * @param locale the Locale value.
     * @return the corresponding canonical string representation.
     */
    public static String localeToCanonicalString(Locale locale) {
        
        String language = locale.getLanguage();
        String country = locale.getCountry();
        boolean hasCountry = country.length() > 0;
        String variant = locale.getVariant();
        boolean hasVariant = variant.length() > 0;
        
        // the language is to be in lowercase
        StringBuilder builder = new StringBuilder(language.toLowerCase(Locale.ENGLISH));
        
        if (hasCountry || hasVariant) {
            // the country is to be in uppercase
            builder.append("_").append(country.toUpperCase(Locale.ENGLISH));
        }
        
        if (hasVariant) {
            // the variant is to be in uppercase
            builder.append("_").append(variant.toUpperCase(Locale.ENGLISH));
        }
        
        return builder.toString();
    }

    /**
     * Converts the canonical string representation of a locale into a Locale value.
     * @param s the canonical string representation of a locale
     * @return the corresponding Locale value.
     */
    public static Locale localeFromCanonicalString(String s) {
        
        if (s.length() == 0) {
            return LocaleUtilities.INVARIANT_LOCALE;
        }
        
        int firstUnderscorePos = s.indexOf('_');
        if (firstUnderscorePos == -1) {
            return new Locale(s);
            
        } else {
            String language = s.substring(0, firstUnderscorePos).toLowerCase(Locale.ENGLISH);
            
            int secondUnderscorePos = s.indexOf('_', firstUnderscorePos + 1);
            if (secondUnderscorePos == -1) {
                String country = s.substring(firstUnderscorePos + 1).toUpperCase(Locale.ENGLISH);
                return new Locale(language, country);
                
            } else {
                String country = s.substring(firstUnderscorePos + 1, secondUnderscorePos).toUpperCase(Locale.ENGLISH);
                String variant = s.substring(secondUnderscorePos + 1).toUpperCase(Locale.ENGLISH);
                return new Locale(language, country, variant);
            }
        }
    }

    /**
     * Returns whether the given Locale value references the invariant locale, i.e.
     * a Locale object whose language, country and variant fields are all empty strings.
     * @param locale the locale value to check.
     * @return true if the given Locale value references the invariant locale; false otherwise.
     */
    public static boolean isInvariantLocale(Locale locale) {
        return locale.equals(INVARIANT_LOCALE);
    }
}
