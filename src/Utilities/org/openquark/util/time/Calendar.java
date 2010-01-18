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
 * Calendar.java
 * Created: 17-Oct-2003
 * By: Rick Cameron
 */

package org.openquark.util.time;

import java.util.Locale;

import com.ibm.icu.util.BuddhistCalendar;
import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.HebrewCalendar;
import com.ibm.icu.util.IslamicCalendar;
import com.ibm.icu.util.JapaneseCalendar;

/**
 * Class Calendar
 * 
 * This class represents a kind of calendar. Currently the following kinds are implemented:
 * - Gregorian
 * - Japanese (Emperor Era)
 * - Buddhist
 * - Chinese Lunar
 * - Hebrew
 * - Islamic (Hijri)
 * 
 * Despite the similarity of name, this class bears little relationship to java.util.Calendar
 * or com.ibm.icu.util.Calendar since those classes are really much more than just a calendar.
 * 
 * @author Rick Cameron
 */
public final class Calendar {
    
    public static final int GREGORIAN = 1;
    public static final int JAPANESE  = 2;
    public static final int BUDDHIST  = 3;
    public static final int CHINESE   = 4;
    public static final int HEBREW    = 5;
    public static final int ISLAMIC   = 6;
    
    /** a Calendar representing the Gregorian calendar */
    public static final Calendar GREGORIAN_CALENDAR = new Calendar (GREGORIAN);
    
    /** a Calendar representing the Japanese Emperor era calendar */
    public static final Calendar JAPANESE_CALENDAR = new Calendar (JAPANESE);
    
    /** a Calendar representing the Chinese lunar calendar */
    public static final Calendar CHINESE_CALENDAR = new Calendar (CHINESE);
    
    /** a Calendar representing the Thai Buddhist calendar */
    public static final Calendar BUDDHIST_CALENDAR = new Calendar (BUDDHIST);
    
    /** a Calendar representing the Hebrew calendar */
    public static final Calendar HEBREW_CALENDAR = new Calendar (HEBREW);
   
    /** a Calendar representing the Islamic or Hijri calendar */
    public static final Calendar ISLAMIC_CALENDAR = new Calendar(ISLAMIC);
       
    private final int id; 
    
    public static boolean isValidID (int id) {
        return id == GREGORIAN
            || id == JAPANESE
            || id == BUDDHIST
            || id == CHINESE
            || id == HEBREW
            || id == ISLAMIC;
    }     
    
    /**
     * Method getInstance
     * 
     * @param id
     * @return Returns a Calendar for this ID value
     */
    public static Calendar getInstance (int id) {
        switch (id) {
            case GREGORIAN:        
                return GREGORIAN_CALENDAR;
    
            case JAPANESE:
                return JAPANESE_CALENDAR;
    
            case CHINESE:
                return CHINESE_CALENDAR;
    
            case BUDDHIST:
                return BUDDHIST_CALENDAR;
    
            case HEBREW:
                return HEBREW_CALENDAR;
    
            case ISLAMIC:
                return ISLAMIC_CALENDAR;
                
            default:
                return GREGORIAN_CALENDAR; 
        }              
    }
    
    /**
     * Constructor Calendar
     * 
     * @param id
     */
    private Calendar (int id) {
        this.id = id;
    }
    
    /**
     * Method toICUCalendar
     * 
     * This method is intended to be used only by related implementation classes.
     * It is public because such classes may be in a different package.
     * The locale must be specified here, since ICU does not allow the locale 
     * of an existing instance of Calendar to be changed. 
     * 
     * @param timeZone
     * @param locale
     * @return Returns an instance of com.ibm.icu.util.Calendar representing this calendar
     */
    public com.ibm.icu.util.Calendar toICUCalendar (TimeZone timeZone, Locale locale) {
        com.ibm.icu.util.TimeZone icuTimeZone = timeZone.toICUTimeZone();
        
        switch (id) {
            case GREGORIAN:
            default:
                return new GregorianCalendar (icuTimeZone, locale);
                
            case JAPANESE:
                return new JapaneseCalendar (icuTimeZone, locale);
               
            case CHINESE:
                return new ChineseCalendar (icuTimeZone, locale);
               
            case BUDDHIST:
                return new BuddhistCalendar (icuTimeZone, locale);
                
            case HEBREW:
                return new HebrewCalendar (icuTimeZone, locale);
                
            case ISLAMIC:
                return new IslamicCalendar (icuTimeZone, locale);
        }
    }
    
    @Override
    public String toString() {           
        switch (id) {
            case GREGORIAN:        
                return "Gregorian Calendar";
    
            case JAPANESE:
                return "Japanese (Emperor Era) Calendar";
    
            case CHINESE:
                return "Chinese Lunar Calendar";
    
            case BUDDHIST:
                return "Buddhist Calendar";
    
            case HEBREW:
                return "Hebrew Calendar";
    
            case ISLAMIC:
                return "Islamic (Hijri) Calendar";
                
            default:
                throw new IllegalStateException("invalid Calendar id field");
        }                              
    }

}
