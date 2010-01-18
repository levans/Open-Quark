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
 * RepositoryResourceCompiledModuleSourceDefinition.java
 * Creation date: Sep 2, 2005
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine;

import java.io.IOException;
import java.io.InputStream;

import org.openquark.cal.compiler.CompiledModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.services.Status;


/**
 * A compiled module source definition which is backed by a resource in a repository.
 * @author rcypher
 */
public class RepositoryResourceCompiledModuleSourceDefinition extends CompiledModuleSourceDefinition {

    /** The locator for the compiled source definition */
    private ProgramResourceLocator.File fileLocator;
    
    /** The repository in which the source definition lives. */
    private ProgramResourceRepository repository;

    /**
     * Constructor for a FileModuleSourceDefinition from a File
     * @param moduleName the name of the module which is defined in the given file.
     * @param fileLocator The locator for the file from which the ModuleSourceDefinition will be read.
     * @param repository
     */
    public RepositoryResourceCompiledModuleSourceDefinition(ModuleName moduleName, ProgramResourceLocator.File fileLocator, ProgramResourceRepository repository) {
        super(moduleName);
        
        if (fileLocator == null) {
            throw new NullPointerException("File must not be null.");
        }
        if (repository == null) {
            throw new NullPointerException("Repository must not be null.");
        }

        this.fileLocator = fileLocator;
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(Status status) {
        // the file may be null if we have discarded the associated compiled module info.
        if (fileLocator != null) {
            try {
                return repository.getContents(fileLocator);
            } catch (IOException e) {
                String errorString = "Error loading workspace.  File: \"" + fileLocator + "\" could not be read.";
                status.add(new Status(Status.Severity.ERROR, errorString, e));
            }
        }        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeStamp() {
        return repository.lastModified(fileLocator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDebugInfo() {
        // the file may be null if we have discarded the associated compiled module info.
        if (fileLocator != null) {
            return repository.getDebugInfo(fileLocator);
        }        
        return "compiled module info discarded";
    }
}
