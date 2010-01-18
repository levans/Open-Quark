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
 * SimpleConsoleHandler.java
 * Creation date: Dec 19, 2003
 * By: Frank Worsley
 */
package org.openquark.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A Java log handler that outputs messages to System.out. It uses a custom formatter
 * that prints only the log message if the level is INFO or less. If the level is above INFO
 * it will prefix the message with the log level. Default level for this handler is FINE.
 * @author Frank Worsley
 */
public final class SimpleConsoleHandler extends StreamHandler{

    /**
     * A log message formatter that simply outputs the message of the log record.
     * Used by the GemCutter to print message to the console without any additional info.
     * @author Frank Worsley
     */
    private static class ConsoleFormatter extends Formatter {

        /**
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            // Append the log level if it is greater than warning            
            if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                sb.append(record.getLevel().getLocalizedName() + ": ");
            }
            
            // Append the log message
            sb.append(record.getMessage() + "\n");

            // Append the throwable if there is one
            if (record.getThrown() != null) {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ex) {
                    sb.append("Failed to generate a stack trace for the throwable");
                }
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Default constructor for a new SimpleConsoleHandler.
     */
    public SimpleConsoleHandler() {
        super(System.out, new ConsoleFormatter());
        
        setLevel(Level.FINE);
    }
        
    /** Override this to always flush the stream. */
    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }
    
    /** Override to just flush the stream, we don't want to close System.out. */
    @Override
    public void close() {
        flush();
    }
}
