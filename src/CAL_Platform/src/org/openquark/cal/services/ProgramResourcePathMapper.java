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
 * ProgramResourcePathMapper.java
 * Creation date: Nov 16, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.cal.services.ResourcePath.Folder;



/**
 * A path mapper for program resources.
 * @author Edward Lam
 */
public class ProgramResourcePathMapper implements ResourcePathMapper.Module {

    /** The base resource folder for module resources for programs with the current machine type. */
    private final Folder baseResourceFolder;
    
    /**
     * Constructor for a ProgramResourcePathMapper.
     * @param machineType the program machine type.
     */
    public ProgramResourcePathMapper(MachineType machineType) {
        baseResourceFolder = new Folder(new String[] { machineType.toString().toLowerCase() + "_runtime" });
    }

    /**
     * {@inheritDoc}
     */
    public Folder getBaseResourceFolder() {
        return baseResourceFolder;
    }

    /**
     * {@inheritDoc}
     */
    public ResourcePath getModuleResourcePath(ModuleName moduleName) {
        return getBaseResourceFolder().extendFolder(createPackageNameSegmentFromModuleName(moduleName));
    }

    /**
     * Take a CAL module name and do any necessary transformations
     * to create a valid java package name segment.
     * @param moduleName the name of a cal module.
     * @return the package name segment corresponding to the cal module.
     */
    private String createPackageNameSegmentFromModuleName(ModuleName moduleName) {
        // '.' in the module name gets mapped to '_'
        // '_' in the module name gets mapped to "__"
        
        if (LECCMachineConfiguration.generateBytecode()) {
            return ProgramResourceLocator.MODULE_PREFIX + moduleName.toSourceText().replaceAll("_", "__").replace('.', '_');
        } else {
            String rootPackage = LECCMachineConfiguration.ROOT_PACKAGE;
            rootPackage = rootPackage.replace('.', File.separatorChar);
            return rootPackage + File.separator + ProgramResourceLocator.MODULE_PREFIX + moduleName.toSourceText().replaceAll("_", "__").replace('.', '_');
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ModuleName getModuleNameFromResourcePath(ResourcePath moduleResourcePath) {
        return ProgramResourceLocator.createModuleNameFromPackageNameSegment(moduleResourcePath.getName());
    }

    /**
     * {@inheritDoc}
     */
    public FilePath getResourcePath(ResourceName resourceName) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromPath(FilePath path) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromFolderAndFileName(Folder folder, String fileName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFileExtension() {
        return "class";             // TODOEL - more than one file extension associated with runtime resources (class, cmi)
    }

    /**
     * Returns the resource path for the given program resource locator and using the given path mapper.
     * @param resourceLocator the locator which identifies a program resource.
     * @param pathMapper the associated path mapper for the program resource.
     * @return the resource path for the given locator.
     */
    static ResourcePath.Folder getResourcePath(ProgramResourceLocator resourceLocator, ProgramResourcePathMapper pathMapper) {
        // Get the path to the module base folder, relative to the workspace root.
        ResourcePath.Folder workspaceRelativeModuleResourcePath = (ResourcePath.Folder)pathMapper.getModuleResourcePath(resourceLocator.getModuleName());
        
        // Extend to get the path to the resource, relative to the workspace root.
        ResourcePath.Folder workspaceRelativeResourceLocation = workspaceRelativeModuleResourcePath.extendFolders(resourceLocator.getResourcePath().getPathElements());
        return workspaceRelativeResourceLocation;
    }
}

