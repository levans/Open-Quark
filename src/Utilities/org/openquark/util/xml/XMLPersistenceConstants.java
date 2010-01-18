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
 * XMLPersistenceConstants.java
 * Creation date: Jul 16, 2004.
 * By: Edward Lam
 */
package org.openquark.util.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * XMLPersistenceConstants.java
 * Strings used in the persistence of XML objects.
 * @author Edward Lam
 */
public final class XMLPersistenceConstants {
    
    // Prevents this class from being instantiated 
    private XMLPersistenceConstants () {}
    
    /** The file extension for saved gem design views. */
    static public final String XML_FILE_EXTENSION =                    "xml"; //$NON-NLS-1$

    /*
     * General namespace info
     */

    /** XML namespace. */
    static public final String XML_NS =                                "http://www.w3.org/2000/xmlns/"; //$NON-NLS-1$
    static public final String XML_NS_PREFIX =                         "xmlns"; //$NON-NLS-1$

    /** XML Schema namespace. */
    static public final String XSI_NS =                                "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
    static public final String XSI_NS_PREFIX =                         "xsi"; //$NON-NLS-1$

    /**
     * A DateFormat for emitting time in the ISO 8601 format.
     * This is the XML standard (without the optional time zone) and also the format we use to store
     * dates in the metadata XML files. NOTE: DateFormat is not synchronized (fine for single threaded use).
     * 
     *  noteFW: UTC / time-zone independent?
     */
    static private final DateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$

    /* 
     * Boolean string values
     */
    static public final String TRUE_STRING =                           "true"; //$NON-NLS-1$
    static public final String FALSE_STRING =                          "false"; //$NON-NLS-1$


    
    /**
     * Static access method to synchronize access to the ISO8601 date format 
     * as DateFormat objects are not thread safe.
     * @param date
     * @return The date formatted as yyyy-MM-dd'T'HH:mm:ss
     */
    public static synchronized String formatDateAsISO8601(Date date) {
        return XMLPersistenceConstants.ISO8601_DATE_FORMAT.format(date);
    }
    
    public static synchronized Date unformatDateFromISO8601 (String date) throws ParseException {
        return XMLPersistenceConstants.ISO8601_DATE_FORMAT.parse(date);
    }
}
