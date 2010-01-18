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
 * EntryPointImpl.java
 * Created: Jan 20, 2004 at 12:15:01 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * This is the class which the compiler actually generates and the runtime expects.  It
 * is presented to the outside world as an instance of EntryPoint to hide the internal details. 
 * 
 * @author RCypher
 */
public final class EntryPointImpl implements EntryPoint {
    private final QualifiedName functionName;
    
    public EntryPointImpl (QualifiedName functionName) {       
        if (functionName == null) {
            throw new NullPointerException("argument 'functionName' cannot be null");
        }
        this.functionName = functionName;
    }
    
    /**     
     * @return cannot be null.
     */
    public QualifiedName getFunctionName() {
        return functionName;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return functionName.getQualifiedName();
    }

    /**
     * A helper function that makes an entry point sc name from a string name, assuming
     * reasonable defaults.
     * If the module name is omitted, it defaults to the Prelude.
     * If the unqualified sc name is omitted, it defaults to "main"
     *     
     * @param entryPointString String a name of the form <em>moduleName.unqualifiedName</em>
     *        <em>moduleName</em> or <em>unqualifiedName</em>
     * @return QualifiedName
     */    
    static public QualifiedName makeEntryPointName(String entryPointString) {
    
        if (entryPointString == null || entryPointString.length() == 0) {
            return QualifiedName.make(CAL_Prelude.MODULE_NAME, "main");
        }
    
        // look for the last dot because the other dots are part of the hierarchical module name
        int periodPos = entryPointString.lastIndexOf('.');
        if (periodPos == -1) {
    
            if (Character.isLowerCase(entryPointString.charAt(0))) {
                //the module name was not supplied
                return QualifiedName.make(CAL_Prelude.MODULE_NAME, entryPointString);
            }
    
            final ModuleName maybeModuleName = ModuleName.maybeMake(entryPointString);
            //the function name was not supplied
            if (maybeModuleName != null) {
                return QualifiedName.make(maybeModuleName, "main");
            } else {
                return QualifiedName.make(CAL_Prelude.MODULE_NAME, "main");
            }
        }
    
        ModuleName maybeModuleName = ModuleName.maybeMake(entryPointString.substring(0, periodPos));
        String unqualifiedName = entryPointString.substring(periodPos + 1);
    
        if (maybeModuleName != null) {
            return QualifiedName.make(maybeModuleName, unqualifiedName);
        } else {
            return QualifiedName.make(CAL_Prelude.MODULE_NAME, unqualifiedName);
        }
    }
}
