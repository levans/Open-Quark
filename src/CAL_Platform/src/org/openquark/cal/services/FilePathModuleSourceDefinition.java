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
 * FileModuleSourceDefinition.java
 * Creation date: Mar 12, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;


/**
 * A ModuleSourceDefinition that provides source from a file path.
 * @author Edward Lam
 */
public final class FilePathModuleSourceDefinition extends ModuleSourceDefinition {

    /** The file path from which the ModuleSourceDefinition will be read. */
    private final String filePathString;

    /**
     * Constructor for a FileModuleSourceDefinition from a File
     * @param path The file path from which the ModuleSourceDefinition will be read.
     */
    public FilePathModuleSourceDefinition(String path, ModuleName moduleName) {
        super(moduleName);
        this.filePathString = path;
    }

    /**
     * Get a File object corresponding to the filePathString.
     * @param status the tracking status object.
     * @return File the File object for the represented source definition.
     */
    private File getFile(Status status) {
        // Get a file reader for the module.
        File fileFromString = WorkspaceLoader.fileFromPath(filePathString);
        if (fileFromString == null) {
            String errorString = "Error loading workspace.  \"" + filePathString + "\" is not a valid file path.\n" +
                                 "File paths must be specified either as a resource path or as a URI.";
            status.add(new Status(Status.Severity.ERROR, errorString));
        }
            
        return fileFromString;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(Status status) {
        File f = getFile(status);
        if (f == null) {
            return null;
        }
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException fnfe) {
            String errorString = "Error loading workspace.  File: \"" + filePathString + "\" could not be found.";
            status.add(new Status(Status.Severity.ERROR, errorString));
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeStamp() {
        File f = getFile (null);
        if (f == null) {
            return 0L;
        }
        
        return f.lastModified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDebugInfo() {
        File f = getFile (null);
        if (f == null) {
            return "no file";
        }
        
        return "from file: " + f.getAbsolutePath();
    }
    
}