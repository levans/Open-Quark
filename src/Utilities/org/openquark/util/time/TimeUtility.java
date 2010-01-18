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
 * TimeUtility.java
 * Created: 31-Oct-2003
 * By: Rick Cameron
 */

package org.openquark.util.time;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * Class TimeUtility
 * 
 * This class provides some foreign functions used by the RelativeTime and Time CAL modules. 
 * They are also convenient helpers for working with UTC times so may be of more general utility.
 * 
 * @author Rick Cameron
 */
public final class TimeUtility {
    
    // This class is not intended to be instantiated
    private TimeUtility () {
        
    }
    
    
    /**
     * Method makeDate
     * 
     * This method creates a Date that represents the parameters, considered as UTC
     * 
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return the Date that corresponds to the parameters
     */
    @SuppressWarnings(value="deprecation") //this is the only efficient way to do the conversion
    public static Date makeDate (int year, int month, int day, int hour, int minute, int second) {
        return new Date (Date.UTC (year - 1900, month - 1, day, hour, minute, second));
    }
    
    /**
     * Method getUTCYear
     * 
     * @param date
     * @return the year number of the date, in UTC
     */
    public static int getUTCYear (Date date) {
        return dateToUTCCalendar(date).get(com.ibm.icu.util.Calendar.YEAR);
    }
    
    /**
     * Method getUTCMonth
     * 
     * @param date
     * @return the month number of the date, in UTC, January == 1
     */
    public static int getUTCMonth (Date date) {
        return dateToUTCCalendar(date).get(com.ibm.icu.util.Calendar.MONTH) - com.ibm.icu.util.Calendar.JANUARY + 1;
    }
    
    /**
     * Method getUTCDay
     * 
     * @param date
     * @return the day number of the date, in UTC, 1 == 1
     */
    public static int getUTCDay (Date date) {
        return dateToUTCCalendar(date).get(com.ibm.icu.util.Calendar.DATE);
    }
    
    /**
     * Method getUTCHour
     * 
     * @param date
     * @return the hour number of the date, in UTC, 24-hour clock
     */
    public static int getUTCHour (Date date) {
        return dateToUTCCalendar(date).get(com.ibm.icu.util.Calendar.HOUR_OF_DAY);
    }
    
    /**
     * Method getUTCMinute
     * 
     * @param date
     * @return the minute number of the date, in UTC
     */
    public static int getUTCMinute (Date date) {
        return dateToUTCCalendar(date).get(com.ibm.icu.util.Calendar.MINUTE);
    }
    
    /**
     * Method getUTCSecond
     * 
     * @param date
     * @return the second number of the date, in UTC
     */
    public static int getUTCSecond (Date date) {
        return dateToUTCCalendar(date).get(com.ibm.icu.util.Calendar.SECOND);
    }
    
    /**
     * Method formatTime
     * 
     * @param t
     * @param tz
     * @param cal
     * @param pattern
     * @param locale
     * @return a String that results from formatting the given Time using the given TimeZone, Calendar, pattern String and Locale
     */
    public static String formatTime (Time t, TimeZone tz, Calendar cal, String pattern, Locale locale) {
        com.ibm.icu.util.Calendar icuCalendar = cal.toICUCalendar(tz, locale);
        
        DateFormat dateFormat = DateFormat.getDateTimeInstance(icuCalendar, DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
        
        if (pattern != null && pattern.length() != 0 && dateFormat instanceof SimpleDateFormat) {
            ((SimpleDateFormat)dateFormat).applyPattern(pattern);
        }
        
        return dateFormat.format(t.toDate());
    }

    /**
     * Parses a time value from a string using the pattern specified.
     * @throws ParseException 
     */
    public static Time parseTime(String timeString, TimeZone tz, Calendar cal, String pattern, Locale locale) throws ParseException {
        com.ibm.icu.util.Calendar icuCalendar = cal.toICUCalendar(tz, locale);
        
        DateFormat dateFormat = DateFormat.getDateTimeInstance(icuCalendar, DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
        
        if (pattern != null && pattern.length() != 0 && dateFormat instanceof SimpleDateFormat) {
            ((SimpleDateFormat)dateFormat).applyPattern(pattern);
        }

        Date date = dateFormat.parse(timeString);
        return Time.fromDate(date);
    }

    /**
     * Method dateToUTCCalendar
     * 
     * @param date
     * @return a Calendar object that refers to the given date, configured for UTC
     */
    private static com.ibm.icu.util.Calendar dateToUTCCalendar (Date date) {
        com.ibm.icu.util.Calendar result = com.ibm.icu.util.Calendar.getInstance(com.ibm.icu.util.TimeZone.getTimeZone("UTC"));
        
        result.setTime(date);
        
        return result;
    }     
}
