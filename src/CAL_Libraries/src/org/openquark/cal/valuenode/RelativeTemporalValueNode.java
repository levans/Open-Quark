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
 * RelativeTemporalValueNode.java
 * Created: 31-Oct-2003
 * By: Rick Cameron
 */

package org.openquark.cal.valuenode;

import java.util.Date;

import org.openquark.cal.compiler.TypeExpr;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

/**
 * 
 * 
 */
public abstract class RelativeTemporalValueNode extends AlgebraicValueNode {

    /** store the date-time value as the internal representation of java.util.Date */
    private long javaDate;

    /**
     * Method getDateFormat
     * 
     * Constructs a DateFormat using a default pattern for the current locale, chosen with the styles provided.
     * The valid style constants are DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT
     * or -1 (in which case that part of the pattern is omitted). 
     * 
     * @param dateStyle
     * @param timeStyle
     * @return a DateFormat with a pattern drawn from the defaults for the current locale, with the given styles, in UTC
     */
    public static DateFormat getDateFormat(int dateStyle, int timeStyle) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(dateStyle, timeStyle);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
    
    /**
     * Method getUTCCalendar
     * 
     * @return a Gregorian Calendar object that uses UTC
     */
    private static Calendar getUTCCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Constructor RelativeTemporalValueNode
     * 
     * @param typeExprParam
     */
    protected RelativeTemporalValueNode(TypeExpr typeExprParam, Date dateValue) {
        super (typeExprParam);
        
        setJavaDate(dateValue);
    }
    
    /**
     * Method setJavaDate
     * 
     * Sets the state of this node to correspond to the given Date
     * 
     * @param dateValue
     */
    protected void setJavaDate (Date dateValue) {
        if (dateValue == null) {
            javaDate = calcDefaultDate ();
        } else {
            javaDate = dateValue.getTime();
        }
    }
    
    /**
     * Method calcDefaultDate
     * 
     * @return the current time in the local time zone, shifted to UTC
     */
    private long calcDefaultDate() {
        // The current time, with the local time zone
        Calendar cal = Calendar.getInstance();
        
        // Retrieve the year, month, day, etc. of the current time in the local time zone, 
        // and construct a UTC time representing those values
        return Date.UTC(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH) - Calendar.JANUARY, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    }

    /**
     * Method getJavaDate
     * 
     * @return a Date representing the state of this node
     */
    protected Date getJavaDate () {
        return new Date (javaDate);
    }
    
    /**
     * Method getCalendarValue
     * 
     * @return a Calendar representing the state of this node, configured for UTC
     */
    public Calendar getCalendarValue () {
        Calendar result = getUTCCalendar();
        result.setTimeInMillis(javaDate);
        
        return result;
    }

    /**
     * Method getTextValue
     * 
     * The valid style constants are DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT
     * or -1 (in which case that part of the pattern is omitted). 
     * 
     * @param dateStyle
     * @param timeStyle
     * @return a formatted string representing the date value stored in this node 
     */
    protected String getTextValue(int dateStyle, int timeStyle) {

        DateFormat dateFormat = getDateFormat(dateStyle, timeStyle);
        return dateFormat.format(getJavaDate());

    }

}
