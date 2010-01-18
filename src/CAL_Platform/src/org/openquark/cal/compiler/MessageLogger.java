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
 * MessageLogger.java
 * Created: Apr 16, 2004
 * By: Ray Cypher (extracted from original logging code in CALCompiler by Luke)
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used by the compilation process (i.e. parsing, static analysis, type-checking,
 * code generation), to log messages of interest.
 * @author Luke Evans
 */
public class MessageLogger implements CompilerMessageLogger {

    /** True if this is an internal logger used to log compiler messages for a single module.  False otherwise. */
    private final boolean compileModuleLogger;

    /** The max number of errors.  Only used if compileModuleLogger is true. */
    private final int maxErrors;
    
    private int errorCount = 0;
    private CompilerMessage.Severity maxErrSeverity = CompilerMessage.Severity.INFO;

    /** The accumulated errors/warnings/info etc. */
    private final List<CompilerMessage> compilerMessages = new ArrayList<CompilerMessage>();
    
    /**
     * Constructor for a MessageLogger.
     */
    public MessageLogger() {
        this(false);
    }
    
    /**
     * Package-scope constructor for a messageLogger.
     * @param compileModuleLogger true if this is an internal logger used to log compiler messages for a single module.
     * False otherwise.
     */
    MessageLogger(boolean compileModuleLogger) {
        this.compileModuleLogger = compileModuleLogger;
        
        if (compileModuleLogger) {
            maxErrors = 10;
        } else {
            maxErrors = -1;
        }
    }
    
    /**
     * @return if this is an internal compiler logger.
     */
    boolean isCompileModuleLogger() {
        return compileModuleLogger;
    }
    
    /**
     * {@inheritDoc}
     */
    public void logMessage(CompilerMessage compilerMessage) {
        logMessages(Collections.singletonList(compilerMessage));
    }

    /**
     * {@inheritDoc}
     */
    public void logMessages(CompilerMessageLogger otherLogger) {
        logMessages(otherLogger.getCompilerMessages());
    }
    
    /**
     * Log messages to this logger.
     * @param messagesToAdd the compiler messages to add.
     * @throws CompilerMessage.AbortCompilation
     * if 1) compileModuleLogger is true, and
     *    2) a fatal error is logged, or there are too many errors.
     */
    private void logMessages(List<CompilerMessage> messagesToAdd) {
        
        for (final CompilerMessage compilerMessage : messagesToAdd) {
            CompilerMessage.Severity thisSeverity = compilerMessage.getSeverity();

            // Add the error
            compilerMessages.add(compilerMessage);

            // Increase max severity if required
            if (maxErrSeverity.compareTo(thisSeverity) < 0) {
                maxErrSeverity = thisSeverity;
            }

            // Check for maximum errors
            if (thisSeverity.compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                
                errorCount++;
                
                // If this is the logger used when compiling a module, check for an abort-compilation condition.
                if (compileModuleLogger) {
                    // Check for too many errors.
                    boolean tooManyErrors = (errorCount == maxErrors);
                    if (tooManyErrors) {
                        compilerMessages.add(new CompilerMessage (compilerMessage.getSourceRange(), new MessageKind.Info.TooManyErrors(maxErrors)));
                    }

                    // Check for abort condition.
                    if (tooManyErrors || maxErrSeverity.compareTo(CompilerMessage.Severity.FATAL) >= 0) {
                        throw new CompilerMessage.AbortCompilation();
                    }
                }
            }
            
        }
    }
    
    /**
     * Get all the compiler messages generated by a compile cycle.
     *
     * @return the error/warning/info messages
     */
    public List<CompilerMessage> getCompilerMessages() {
        return Collections.unmodifiableList(compilerMessages);
    }

    /**
     * Get all the compiler messages of the given severity and above, generated by a compile cycle.
     *
     * @param minSeverity
     * @return the error/warning/info messages
     */
    public List<CompilerMessage> getCompilerMessages(CompilerMessage.Severity minSeverity) {
        List<CompilerMessage> filtered;
        // Did they ask for all?
        if (minSeverity.compareTo(CompilerMessage.Severity.INFO) <= 0) {
            filtered = compilerMessages;
        } else {
            // Filter messages below the given severity
            filtered = new ArrayList<CompilerMessage>();
            
            for (int i = 0, size = compilerMessages.size(); i < size; i++) {
                CompilerMessage message = compilerMessages.get(i);
                if (message.getSeverity().compareTo(minSeverity) >= 0) {
                    filtered.add(message);
                }
            }
        }
        return Collections.unmodifiableList(filtered);
    }
    
    /**
     * @return the number of messages held onto by the logger.
     */
    public int getNMessages() {
        return compilerMessages.size();
    }
    
    /**
     * @return int the number of messages that are of severity error or more.
     */
    public int getNErrors() {
        return errorCount;
    }

    /**
     * Get the first error generated by the compile cycle, or null if there is none.
     * Note: this is an actual first error and doesn't include warnings or info messages.
     * @return CompilerMessage the first actual error
     */
    public CompilerMessage getFirstError() {
        
        for (int i = 0, size = compilerMessages.size(); i < size; i++) {
            CompilerMessage message = compilerMessages.get(i);
            if (message.getSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                return message;
            }
        }
        
        return null;
    }

    /**
     * Method getMaxSeverity.
     * @return CompilerMessage.Severity the maximum severity of a message encountered since the last
     *      time compiler messages were cleared.
     */
    public CompilerMessage.Severity getMaxSeverity() {
        return maxErrSeverity;
    }

    /**
     * A textual representation of all messages held by the compiler message logger, suitable for
     * debugging purposes. Do not write code that relies on the precise format of this String.
     * @return textual representation of each compiler message held.
     */
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0, nMessages = compilerMessages.size(); i < nMessages; ++i) {
            sb.append(compilerMessages.get(i));
            sb.append('\n');
        }
        
        return sb.toString();
    }

}
