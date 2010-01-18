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
 * BusinessObjectsException.java
 * Created: Jul 13, 2004
 * By: David Mosimann
 */
package org.openquark.util;

/**
 * Generic 'marker' exception to indicate that the exception is being thrown from Business Objects code.
 * Clients should subclass this to provide a more specific exception.
 */
public class BusinessObjectsException extends Exception {
   
    private static final long serialVersionUID = 7983903523717974749L;
    
    /** Use this message bundle to dig up localized messages */
    private static final Messages messages = PackageMessages.instance;
    
    /**
     * Constructor for BusinessObjectsException
     * @param message
     */
    public BusinessObjectsException(String message) {
        super(message);
    }

    /**
     * Constructor for BusinessObjectsException
     * @param message
     * @param other
     */
    public BusinessObjectsException(String message, Throwable other) {
        super(message, other);
    }

    /**
     * Constructor for BusinessObjectsException
     * @param other
     */
    public BusinessObjectsException(Throwable other) {
        super(other);
    }

    /**
     * Retrieve the message associated with this exception.
     * The error message for the underlying 'cause' exeception is included.
     * @return String
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder ();
        String sm = super.getMessage();
        if (sm!= null && !sm.equals("")) {
            sb.append (sm);
            sb.append ("\n");
        }

        // Add the info from the originating exception             
        if (getCause() != null) {
            Throwable cause = getCause();
            String oem = cause.getMessage();
            sb.append (messages.getString("CausedBy", cause.toString(), oem));
        }

        return sb.toString ();
    }  
}
