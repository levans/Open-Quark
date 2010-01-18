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
 * StringModuleSourceDefinition.java
 * Created: Nov 24, 2005
 * By: dmosimann
 */
package org.openquark.cal.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.util.TextEncodingUtilities;


/**
 * A ModuleSourceDefinition that provides source from a string.
 * @author David Mosimann
 */
public class StringModuleSourceDefinition extends ModuleSourceDefinition {
    /** The text defining the module (includes imports, functions, data declarations, ...) */
    private final String moduleDefinition;
    
    /**
     * Constructor for StringModuleSourceDefinition
     * @param moduleName the name of the module which is defined in the given file.
     * @param moduleDefinition The string from which the ModuleSourceDefinition will be read.
     */
    public StringModuleSourceDefinition(ModuleName moduleName, String moduleDefinition) {
        super(moduleName);
        this.moduleDefinition = moduleDefinition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(Status status) {
        return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(moduleDefinition));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeStamp() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDebugInfo() {
        return "constructed prorgammatically via a source string";
    }
}
