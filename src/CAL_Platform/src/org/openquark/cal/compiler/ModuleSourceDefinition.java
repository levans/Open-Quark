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
 * ModuleSourceDefinition.java
 * Creation date: Mar 7, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.ResourceIdentifier;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.util.TextEncodingUtilities;


/**
 * A ModuleSourceDefinition is a module resource which wraps the text of a module definition and its name.
 * @author Edward Lam
 */
public abstract class ModuleSourceDefinition extends WorkspaceResource {

    public static final ModuleSourceDefinition[] EMPTY_ARRAY = new ModuleSourceDefinition[0];
    public static final String RESOURCE_TYPE = "SourceDefinition";

    /** The name of the module. */
    private final ModuleName moduleName;
    
    /**
     * Constructor for a ModuleSourceDefinition.
     * @param moduleName the name of the module.
     */
    public ModuleSourceDefinition(ModuleName moduleName) {
        super(ResourceIdentifier.make(RESOURCE_TYPE, CALFeatureName.getModuleFeatureName(moduleName)));
        if (moduleName == null) {
            throw new NullPointerException("Module name must not be null.");
        }

        this.moduleName = moduleName;
    }

    /**
     * Get the name of the module
     * @return the name of the module, or null if unknown by the source
     */
    public final ModuleName getModuleName() {
        return moduleName;
    }
    
    /**
     * Get the source of the module in the form of a newly-instantiated Reader.
     * The client should take care to call close() on the Reader when done.
     * @param status the tracking status object.
     * @return Reader the reader, or null if the reader could not be instantiated (eg. for a file, if the
     *   file no longer exists).  Errors will be logged to the logger.
     */
    public final Reader getSourceReader(Status status) {
        InputStream inputStream = getInputStream(status);
        if (inputStream == null) {
            return null;
        }
        return TextEncodingUtilities.makeUTF8Reader(inputStream);
    }

    /**
     * Get the source of the module in String form.
     * 
     * @param status the tracking status object.
     * @return A string containing the source of the current module. May return null if the module could not be read.
     */
    
    public final String getSourceText(Status status, CompilerMessageLogger messageLogger){
        Reader reader = getSourceReader(status);
        if (reader == null) {
            return null;
        }

        BufferedReader sourceReader = new BufferedReader(reader);

        try {
            StringBuilder stringBuilder = new StringBuilder();
            char[] inputBuffer = new char[4096];

            try{
                int charsRead = sourceReader.read(inputBuffer);
                while(charsRead != -1) {
                    stringBuilder.append(inputBuffer, 0, charsRead);
                    charsRead = sourceReader.read(inputBuffer);
                }
            } catch (IOException e) {
                MessageKind messageKind = new MessageKind.Error.ModuleCouldNotBeRead(moduleName);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return null;
            }

            return stringBuilder.toString(); 
        } finally {
            try {
                sourceReader.close();
            } catch (IOException e) {
            }
        }
    }
}
