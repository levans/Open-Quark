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
 * BasicProgramResourcePathRepository.java
 * Creation date: December 8, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.machine.AsynchronousFileWriter;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.runtime.MachineType;
import org.openquark.util.FileChangeOutputStream;
import org.openquark.util.FileSystemHelper;


/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A repository of program resources in the stand-alone cal workspace.
 * @author Edward Lam
 */
public class BasicProgramResourcePathRepository implements ProgramResourceRepository {
    
    /** A path mapper for program resources. */
    private final ProgramResourcePathMapper pathMapper;
    
    /** The root directory for generated program resources. */
    private final File sourceGenerationRoot;
    
    /**
     * @param sourceGenerationRoot the root directory with respect to program resource generation.
     * @return a provider for a program resource finder associated with the workspace.
     */
    public static ProgramResourceRepository.Provider getResourceRepositoryProvider(final File sourceGenerationRoot) {
        return new ProgramResourceRepository.Provider() {

            public ProgramResourceRepository getRepository(MachineType machineType) {
                return new BasicProgramResourcePathRepository(new ProgramResourcePathMapper(machineType), sourceGenerationRoot);
            }
        };
    }
    
    /**
     * Constructor for a BasicProgramResourcePathRepository.
     * 
     * @param pathMapper
     * @param sourceGenerationRoot the directory which represents the root of the folder structure which containing
     * the program resources.  This will be the parent of the module folders.
     */
    protected BasicProgramResourcePathRepository(ProgramResourcePathMapper pathMapper, File sourceGenerationRoot) {
        if (sourceGenerationRoot == null || pathMapper == null) {
            throw new NullPointerException();
        }
        this.pathMapper = pathMapper;
        this.sourceGenerationRoot = sourceGenerationRoot;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContents(ProgramResourceLocator.File fileLocator) throws IOException {
        File inputFile = getFile(fileLocator);
        if (inputFile == null) {
            return null;
        }
        return new FileInputStream(inputFile);
    }

    /**
     * {@inheritDoc}
     */
    public void setContents(ProgramResourceLocator.File fileLocator, InputStream source) throws IOException {
        
        // Handle null source.
        if (source == null) {
            source = new ByteArrayInputStream(new byte[0]);
        }
        
        try {
            File outputFile = getFile(fileLocator);
            
            FileChangeOutputStream outputStream = null;
            try {
                // throws IOException:
                outputStream = new FileChangeOutputStream(outputFile);
                FileSystemResourceHelper.transferData(source, outputStream);
                
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException ioe) {
                    // Nothing can really be done at this point, and we don't bother reporting the error.
                }
            }
            
            // If here, an exception was not thrown.
            
            // If the file wasn't written, update the timestamp to provide a visible indication that the file data was processed.
            // Note: don't do this in the try block since this call would have no effect while the output stream is open.
            if (outputStream != null && !outputStream.isDifferent()) {
                getFile(fileLocator).setLastModified(System.currentTimeMillis());
            }
        } finally {
            source.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void ensureFolderExists(ProgramResourceLocator.Folder folder) throws IOException {
        File folderFile = getFile(folder);

        // Attempt to create the folder.
        if (!FileSystemHelper.ensureDirectoryExists(folderFile)) {
            
            // Check if a file already exists at the folder location.
            if (FileSystemHelper.fileExists(folderFile)) {
                throw new IOException("A file resource already exists at folder location: " + folder + ".");
            }
            
            throw new IOException("Folder " + folder + " could not be created. (Corresponding file system directory: '" + folderFile + "')");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ProgramResourceLocator resourceLocator) throws IOException {
        Status status = new Status("Delete status.");
        File resourceFile = getFile(resourceLocator);
        
        if (resourceFile.exists()) {
            FileSystemResourceHelper.delTree(resourceFile, status);
            
            if (status.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
                throw new IOException(status.getDebugMessage());
            }
        
        } else {
            // Nothing happens if the resource doesn't exist.
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ProgramResourceLocator[] resourceLocators) throws IOException {
        Status status = new Status("Delete status.");
        
        for (final ProgramResourceLocator resourceLocator : resourceLocators) {
            File resourceFile = getFile(resourceLocator);
            
            if (resourceFile.exists()) {
                FileSystemResourceHelper.delTree(resourceFile, status);
                
            } else {
                // Nothing happens if the resource doesn't exist.
            }
        }
        
        if (status.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            throw new IOException(status.getDebugMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(ProgramResourceLocator resourceLocator) {
        if (resourceLocator instanceof ProgramResourceLocator.File) {
            // file
            return FileSystemHelper.fileExists(getFile(resourceLocator));
        
        } else {
            // directory
            return getFile(resourceLocator).exists();
        }
    }

    /**
     * {@inheritDoc}
     */
    public long lastModified(ProgramResourceLocator resourceLocator) {
        return getFile(resourceLocator).lastModified();
    }

    /**
     * {@inheritDoc}
     */
    public ProgramResourceLocator[] getMembers(ProgramResourceLocator.Folder folder) {
        File folderFile = getFile(folder);
        File[] files = folderFile.listFiles();
        if (files == null) {
            return null;
        }
        
        ProgramResourceLocator[] folderFileLocators = new ProgramResourceLocator[files.length];
        for (int i = 0; i < files.length; i++) {
            File nthFile = files[i];
            String nthFileName = nthFile.getName();
            if (nthFile.isDirectory()) {
                folderFileLocators[i] = folder.extendFolder(nthFileName);
            } else {
                folderFileLocators[i] = folder.extendFile(nthFileName);
            }
        }
        return folderFileLocators;
    }

    /**
     * {@inheritDoc}
     */
    public File getFile(ProgramResourceLocator resourceLocator) {
        ResourcePath.Folder workspaceRelativeResourceLocation = ProgramResourcePathMapper.getResourcePath(resourceLocator, pathMapper);
        return FileSystemResourceHelper.getResourceFile(workspaceRelativeResourceLocation, sourceGenerationRoot, false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        File resourceRoot = FileSystemResourceHelper.getResourceFile(pathMapper.getBaseResourceFolder(), sourceGenerationRoot, false);
        if (!resourceRoot.exists()) {
            return true;
        }
        
        File[] files = resourceRoot.listFiles();
        
        return (files == null || files.length == 0);
    }

    /**
     * {@inheritDoc}
     */
    public long getSize(ProgramResourceLocator.File fileLocator) {
        File resourceRoot = getFile(fileLocator);
        return resourceRoot.length();
    }
    
    /**
     * {@inheritDoc}
     */
    public ModuleName[] getModules() {
        File resourceRoot = FileSystemResourceHelper.getResourceFile(pathMapper.getBaseResourceFolder(), sourceGenerationRoot, false);
        if (!resourceRoot.exists()) {
            return new ModuleName[0];
        }
        
        File[] files = resourceRoot.listFiles();

        ModuleName[] modules = filesToModuleNames (files);
        
        if (modules.length == 0) {
            // If the runtime is directly generating Java bytecode (either in static or dynamic mode)
            // the resource root will contain a directory for each module.  However, if the runtime
            // is generating Java source there will be a directory "org\openquark" underneath the
            // resource root and this will contain a directory for each module.
            File sourceModuleRoot = new File(resourceRoot, "org" + File.separator + "openquark");
            if (sourceModuleRoot.exists()) {
                files = sourceModuleRoot.listFiles();
                modules = filesToModuleNames(files);
            }
        }
        
        return modules;
    }
    
    /**
     * Attemp to generate an array of ModuleName from an array of File.
     * @param files - array of File
     * @return array of ModuleName
     */
    private ModuleName[] filesToModuleNames (File files[]) {
        if (files == null) {
            return new ModuleName[0];
        }
        
        List<ModuleName> moduleNameList = new ArrayList<ModuleName>();
        for (final File file : files) {
            String fileName = file.getName();
            ModuleName moduleName = ProgramResourceLocator.createModuleNameFromPackageNameSegment(fileName);
            if (moduleName != null) {
                moduleNameList.add(moduleName);
            }
        }
        
        return moduleNameList.toArray(new ModuleName[moduleNameList.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public String getLocationString() {
        return FileSystemResourceHelper.getResourceFile(pathMapper.getBaseResourceFolder(), sourceGenerationRoot, false).getAbsolutePath();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDebugInfo(ProgramResourceLocator.File fileLocator) {
        File inputFile = getFile(fileLocator);
        if (inputFile == null) {
            return null;
        } else {
            return "from file: " + inputFile.getAbsolutePath();
        }
    }

    /**
     * {@inheritDoc}
     */
    public AsynchronousFileWriter getAsynchronousFileWriter() {
        return new BasicAsynchronousFileWriter(this);
    }
}
