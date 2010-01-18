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
 * DurationLogger.java
 * Created: 2-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.util;

import java.util.Date;


/**
 * Class DurationLogger
 * 
 * This class is used to measure the duration of an operation and to log the duration 
 * to the console.
 * 
 */
public class DurationLogger {

    private Date start;
    
    /**
     * Constructor DurationLogger
     * 
     * The start time is noted when the logger is created.
     */
    public DurationLogger () {
        start = new Date ();
    }
    
    /**
     * Method report
     * 
     * Report the accumulated duration, and optionally reset the start time. 
     * 
     * @param message
     * @param doReset
     */
    public void report (String message, boolean doReset) {
        Date now = new Date ();
        
        long duration = now.getTime() - start.getTime();
        
        System.out.println (message + duration + " ms");
        
        if (doReset) {
            reset ();
        }
    }

    /**
     * Method reset
     * 
     * Reset the start time.
     */
    public void reset () {
        start = new Date ();
    }
}
