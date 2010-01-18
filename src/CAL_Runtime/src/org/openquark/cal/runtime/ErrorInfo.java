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
 * ErrorInfo.java
 * Creation date: (July 12/05 2:30:03 PM)
 * By: Greg McClement
 */
package org.openquark.cal.runtime;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;

/**
 * Class used to provide the information needed to show source position information in error messages.
 * 
 * It is used for the Prelude.error function, but also for pattern matching failures, and even internal
 * CAL implementation errors where an "origin" in terms of CAL source can be given.
 *
 * ErrorInfo contains information that indicates where in the source code that call
 * that generated the error exists.
 *
 * @author Greg McClement
 */
public final class ErrorInfo {
    
    /**
     * <code>topLevelFunctionName</code> contains the name of the top level function that contains the error call.
     */
    private final QualifiedName topLevelFunctionName;
    
    /**
     * <code>line</code> is the line number that the error call appears on. This will be 0 if not set.
     */
    private int line = 0;
    
    /**
     * <code>column</code> is the column number in the line that the error call appears on. This will be 
     * 0 if not set.
     */
    
    private int column = 0;

    public ErrorInfo(QualifiedName topLevelFunctionName, int line, int column) { 
        if (topLevelFunctionName == null) {
            throw new NullPointerException();
        }
        this.topLevelFunctionName = topLevelFunctionName;
        this.line = line;
        this.column = column;
    }
    
    public ErrorInfo(String moduleName, String topLevelFunctionName, int line, int column) {
        this (QualifiedName.make(ModuleName.make(moduleName), topLevelFunctionName), line, column);        
    } 
    
    @Override
    public String toString() {
        String asString = topLevelFunctionName.getQualifiedName();

        if (line > 0) {
            asString = asString + ": line " + line + ", column " + column;
        }

        return asString;
    }
    
    /**
     * @return The top level function that contains the error call. This is never null. 
     */
    
    public QualifiedName getTopLevelFunctionName(){
        return topLevelFunctionName;
    }
    
    /**
     * @return The line number that the error appeared on. Will be 0 if not applicable.
     */
    
    public int getLine(){
        return line;
    }
    
    /**
     * @return The column number of the line that the error call appeared on. Will be 0 if not applicable.
     */
    public int getColumn(){
        return column;
    }
}