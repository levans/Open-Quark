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
 * ProgramResourceRepository.java
 * Creation date: Nov 24, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.machine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.runtime.MachineType;



/**
 * Warning- this class should only be used by the CAL runtime implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * This class represents a repository which uses program resource locators to locate files.
 * Its purpose is to present a client-specific abstraction of the file system to the machine, for the purposes of building and running the program.
 * 
 * For instance, the way in which the standalone CAL workspace locates files is substantially different from that of the Eclipse workspace.
 * 
 * @author Edward Lam
 */
public interface ProgramResourceRepository {
    
    /**
     * Provider for a ProgramResourceRepository.
     * This interface allows a client to provide an appropriate repository without knowing the machine type of the program
     * which will be created.
     * When the program manager is instantiated, the provider is required, however the machine type may or may not be known.
     * 
     * @author Edward Lam
     */
    public interface Provider {
        /**
         * @param machineType the machine type for the program associated with the repository.
         * @return a ProgramResourceRepository for programs with the given machine type.
         */
        public ProgramResourceRepository getRepository(MachineType machineType);
    }
    
    /**
     * @return the names of modules for which module folders exist.
     * Note that these module folders are not guaranteed to contain any resources.
     */
    public ModuleName[] getModules();

    /**
     * @param folder a resource folder.
     * @return the members of that folder.
     * Null if the folder does not exist, or the corresponding repository location is a file.
     */
    public ProgramResourceLocator[] getMembers(ProgramResourceLocator.Folder folder);

    /**
     * @param fileLocator a resource file.
     * @return an open input stream on the contents of the file.
     * The client is responsible for closing the stream when finished.
     * @throws IOException if opening the input stream fails.
     * This could happen if the resource does not exist, or for other reasons.
     */
    public InputStream getContents(ProgramResourceLocator.File fileLocator) throws IOException;

    /**
     * Sets the contents of a new or existing file to the bytes in the given input stream.
     * The stream will get closed whether this method succeeds or fails.
     * If the stream is null then the content is set to be the empty sequence of bytes.
     * 
     * @param fileLocator the resource file.
     * @param source an input stream containing the new contents of the file.
     * 
     * @throws IOException if there was a problem writing the file.
     * For instance, if:
     * - the file does not already exist
     * - the corresponding location in the file system is a directory.
     */
    public void setContents(ProgramResourceLocator.File fileLocator, InputStream source) throws IOException;

    /**
     * Ensure that a new folder exists in the repository.
     * If the folder exists, this call will have no effect.
     * If the folder does not exist, an attempt will be made to create it.
     * 
     * @param resourceFolder the folder to create.
     * @throws IOException if there was a problem creating the resource.
     * For instance, if:
     * - a resource already exists at the same path as that provided.
     * - the folder couldn't be created.
     * 
     * Note that if this operation fails, it may have succeeded in creating some of the parent directories.
     */
    public void ensureFolderExists(ProgramResourceLocator.Folder resourceFolder) throws IOException;
    
    /**
     * @param resourceLocator a resource locator.
     * @return whether that resource exists.
     */
    public boolean exists(ProgramResourceLocator resourceLocator);

    /**
     * Deletes this resource from the repository.
     * Deletion applies recursively to all members of this resource in a "best-effort" fashion.  
     * That is, all resources which can be deleted are deleted. Resources which could not be deleted are noted in a thrown exception. 
     * The method does not fail if resources do not exist; it fails only if resources could not be deleted.
     * 
     * @param resourceLocator the locator for the resource to delete.
     * @throws IOException if the resource exists and could not be deleted for some reason.
     */
    public void delete(ProgramResourceLocator resourceLocator) throws IOException;

    /**
     * Deletes a number of resources from the repository.
     * Deletion applies recursively to all members of this resource in a "best-effort" fashion.  
     * That is, all resources which can be deleted are deleted. Resources which could not be deleted are noted in a thrown exception. 
     * The method does not fail if resources do not exist; it fails only if resources could not be deleted.
     * 
     * Note that this method may be more efficient for some repository implementations than calling delete() on the individual resources.
     * 
     * @param resourceLocators the locators for the resources to delete.
     * @throws IOException if the resource exists and could not be deleted for some reason.
     */
    public void delete(ProgramResourceLocator[] resourceLocators) throws IOException;
    
    /**
     * @param resourceLocator a resource locator.
     * @return the last modified time associated with the resource.
     * This will be a long value representing the time the file was last modified, as defined by java.io.File.lastModified().
     */
    public long lastModified(ProgramResourceLocator resourceLocator);

    /** 
     * @param fileLocator a resource file.
     * @return the size of the file, in bytes, or 0L if the file does not exist.
     * If the corresponding repository location names a directory (not a file), the return value is unspecified.
     */
    public long getSize(ProgramResourceLocator.File fileLocator);
    
    /**
     * @param resourceLocator a resource locator for a file or folder.
     * @return the java.io.File corresponding to the resource locator.
     * 
     * NOTE: The returned File can be null, if the resource is not backed by the file system,
     *       e.g. the resource comes from a Car file.
     * 
     * FOR INTERNAL USE ONLY: should only be used by the source generator.
     *   Under no circumstances should one delete any file obtained by this method using file.delete().  
     *   Use the repository.delete() method instead.
     */
    public File getFile(ProgramResourceLocator resourceLocator);

    /**
     * @return a simple test of whether the repository is empty.
     * For efficiency reasons, this method may return the result of a simple test for emptiness, such as existence of module subfolders.
     */
    public boolean isEmpty();
    
    /**
     * @return a human-readable string identifying the location of the repository.
     */
    public String getLocationString();
    
    /**
     * @param fileLocator a resource file.
     * @return debugging information about the resource, e.g. the actual location of the resource. Can be null if the resource does not exist.
     */
    public String getDebugInfo(ProgramResourceLocator.File fileLocator);

    /**
     * @return an asynchronous file writer which is able to write files to the repository.
     */
    public AsynchronousFileWriter getAsynchronousFileWriter();

}
