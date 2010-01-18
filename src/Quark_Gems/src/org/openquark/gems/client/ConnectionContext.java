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
 * ConnectionContext.java
 * Creation date: Sep 29, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.gems.client.valueentry.ValueEditorManager;


/**
 * A class that encapsulates the information related to the context a connection is made in.
 * This is used by the GemManager to determine if a connection can be made.
 * @author Frank Worsley
 */
public class ConnectionContext {

    /** The type info of the module in whose context the connection is made. */
    private final ModuleTypeInfo contextModuleTypeInfo;
    
    /**
     * The value editor manager that is used to check if value gems can supply the
     * values for arguments if a connection is made. 
     */
    private final ValueEditorManager valueEditorManager;
    
    /**
     * Constructor for a new ConnectionContext object.
     * @param contextModuleTypeInfo the type info of the module in whose context the connection is made
     * @param valueEditorManager the manager used to determine if value gems can supply argument values
     */
    public ConnectionContext(ModuleTypeInfo contextModuleTypeInfo, ValueEditorManager valueEditorManager) {

        if (contextModuleTypeInfo == null || valueEditorManager == null) {
            throw new NullPointerException();
        }
        
        this.contextModuleTypeInfo = contextModuleTypeInfo;
        this.valueEditorManager = valueEditorManager;
    }
    
    /**
     * @return the type info of the module in whose context the connection is made
     */
    public ModuleTypeInfo getContextModuleTypeInfo() {
        return contextModuleTypeInfo;
    }
    
    /**
     * @return the manager used to determine if value gems can supply argument values
     */
    public ValueEditorManager getValueEditorManager() {
        return valueEditorManager;
    }
}
