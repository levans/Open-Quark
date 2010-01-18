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
 * WorkspaceDeclarationNullaryStore.java
 * Creation date: Dec 13, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.openquark.cal.services.ResourceName.Filter;
import org.openquark.cal.services.ResourcePath.Folder;
import org.openquark.util.TextEncodingUtilities;


/**
 * A workspace declaration store for the nullary workspace, using files and folders in the file system.
 * @author Edward Lam
 * @author Joseph Wong
 */
/*
 * @implementation
 * 
 * This store (also the CarNullaryStore) differs from other nullary stores in that it
 * encompasses more than just the resources within the regular nullary environment's organization and
 * directory structure.
 * 
 * In this case, workspace declaration files appearing inside Car-jars on the classpath
 * are treated as workspace declarations in the StandardVault.
 * 
 * This extension in semantics is achieved by overriding all of the interface methods defined in ResourceNullaryStore,
 * so that the methods can all be rendered aware of the existence of these workspace declaration files residing in Jars.
 * 
 * In terms of precedence, the workspace declaration files located in the nullary envrionment's regular directory structure
 * take precedence over the ones appearing inside Car-jars.
 */
class WorkspaceDeclarationNullaryStore extends ResourceNullaryStore implements WorkspaceDeclarationStore {
    
    /** A Map mapping cws names to the managers for the jar files on the classpath that contain them. */
    private Map<String, JarFileManager> cwsToJarFileManagerMap;
    
    /**
     * The base folder for workspace declarations appearing in jars. It is currently "Workspace Declarations", which is
     * the same as the base folder for general workspace declarations, but it could theoretically be something different.
     */
    static final ResourcePath.Folder WORKSPACE_DECLARATION_IN_JAR_BASE_FOLDER = WorkspaceDeclarationPathMapper.baseDeclarationFolder;

    /**
     * Constructor for a WorkspaceDeclarationNullaryStore.
     */
    public WorkspaceDeclarationNullaryStore() {
        super(WorkspaceResource.WORKSPACE_DECLARATION_RESOURCE_TYPE, WorkspaceDeclarationPathMapper.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    public Set<ResourceName> getWorkspaceNames() {
        return new HashSet<ResourceName>(getFolderResourceNames(getPathMapper().getBaseResourceFolder()));
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<WorkspaceResource> getResourceIterator() {
        return WorkspaceDeclarationPathStoreHelper.getResourceIterator(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceName> getFilteredFolderResourceNames(Folder folder, Filter filter) {
        // we combine the resources from the nullary environment and those from the classpath
        
        List<ResourceName> baseNullaryEnvResult = super.getFilteredFolderResourceNames(folder, filter);
        
        List<ResourceName> finalList = new ArrayList<ResourceName>();
        finalList.addAll(baseNullaryEnvResult);
        
        if (folder.equals(getPathMapper().getBaseResourceFolder())) {
            for (final String cwsName : getCWSToJarFileManagerMap().keySet()) {
                ResourceName workspaceDeclarationResourceName = new ResourceName(WorkspaceDeclarationFeatureName.getWorkspaceDeclarationFeatureName(cwsName));
                
                if (filter.accept(workspaceDeclarationResourceName)) {
                    finalList.add(workspaceDeclarationResourceName);
                }
            }
        }
        
        return finalList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceName> getFolderResourceNames(Folder folder) {
        // we combine the resources from the nullary environment and those from the classpath
        
        List<ResourceName> baseNullaryEnvResult = super.getFolderResourceNames(folder);
        
        List<ResourceName> finalList = new ArrayList<ResourceName>();
        finalList.addAll(baseNullaryEnvResult);
        
        if (folder.equals(getPathMapper().getBaseResourceFolder())) {
            for (final String cwsName : getCWSToJarFileManagerMap().keySet()) {
                finalList.add(new ResourceName(WorkspaceDeclarationFeatureName.getWorkspaceDeclarationFeatureName(cwsName)));
            }
        }
        
        return finalList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(ResourceName resourceName) {
        
        InputStream baseNullaryEnvResult = super.getInputStream(resourceName);
        
        if (baseNullaryEnvResult != null) {
            return baseNullaryEnvResult;
            
        } else {
            // if the cws does not exist in the nullary environment, then check the jars on the classpath
            JarFileManager jfm = getJarFileManagerForCWS(resourceName);
            if (jfm != null) {
                String relativePath = WORKSPACE_DECLARATION_IN_JAR_BASE_FOLDER.extendFile(resourceName.getFeatureName().getName()).getPathStringMinusSlash();
                JarFile jarFile = jfm.getJarFile();
                ZipEntry entry = jarFile.getEntry(relativePath);
                if (entry == null) {
                    return null;
                }
                try {
                    return jarFile.getInputStream(entry);
                } catch (IOException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream(ResourceName resourceName, Status saveStatus) {
        
        // we do not support writing to cws files contained in jars on the classpath
        return super.getOutputStream(resourceName, saveStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDebugInfo(ResourceName resourceName) {
        
        if (super.hasFeature(resourceName)) {
            return super.getDebugInfo(resourceName);
            
        } else {
            // if the cws does not exist in the nullary environment, then check the jars on the classpath
            JarFileManager jfm = getJarFileManagerForCWS(resourceName);
            if (jfm != null) {
                String relativePath = WORKSPACE_DECLARATION_IN_JAR_BASE_FOLDER.extendFile(resourceName.getFeatureName().getName()).getPathStringMinusSlash();
                JarFile jarFile = jfm.getJarFile();
                ZipEntry entry = jarFile.getEntry(relativePath);
                if (entry == null) {
                    return null;
                }
                return "from Car-jar: " + jarFile.getName() + ", entry: " + entry;
            } else {
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeStamp(ResourceName resourceName) {
        
        long baseNullaryEnvResult = super.getTimeStamp(resourceName);
        
        if (baseNullaryEnvResult != 0) {
            return baseNullaryEnvResult;
            
        } else {
            // if the cws does not exist in the nullary environment, then check the jars on the classpath
            JarFileManager jfm = getJarFileManagerForCWS(resourceName);
            if (jfm != null) {
                String relativePath = WORKSPACE_DECLARATION_IN_JAR_BASE_FOLDER.extendFile(resourceName.getFeatureName().getName()).getPathStringMinusSlash();
                ZipEntry entry = jfm.getJarFile().getEntry(relativePath);
                if (entry == null) {
                    return 0;
                }
                return entry.getTime();
            } else {
                return 0;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasFeature(ResourceName resourceName) {
        
        // if the cws does not exist in the nullary environment, then check the jars on the classpath
        return super.hasFeature(resourceName) || getJarFileManagerForCWS(resourceName) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRemovable(ResourceName resourceName) {
        
        // we do not support removing cws files contained in jars on the classpath
        return super.isRemovable(resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable() {
        
        // this store itself is writeable
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(ResourceName resourceName) {
        
        // we do not support writing to cws files contained in jars on the classpath
        return super.isWriteable(resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllResources(Status removeStatus) {
        
        // we do not support removing cws files contained in jars on the classpath
        super.removeAllResources(removeStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeResource(ResourceName resourceName, Status removeStatus) {
        
        // we do not support removing cws files contained in jars on the classpath
        super.removeResource(resourceName, removeStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus) {
        
        // we do not support renaming cws files contained in jars on the classpath
        return super.renameResource(oldResourceName, newResourceName, newResourceStore, renameStatus);
    }

    /**
     * @param resourceName the resource name for a cws file.
     * @return the manager for the jar file containing the cws file.
     */
    private JarFileManager getJarFileManagerForCWS(ResourceName resourceName) {
        return getCWSToJarFileManagerMap().get(resourceName.getFeatureName().getName());
    }

    /**
     * @return a Map mapping cws names to the managers for the jar files on the classpath that contain them.
     */
    private synchronized Map<String, JarFileManager> getCWSToJarFileManagerMap() {
        if (cwsToJarFileManagerMap != null) {
            return cwsToJarFileManagerMap;
        }
        
        cwsToJarFileManagerMap = new HashMap<String, JarFileManager>();
        
        Set<File> carJarFilesInEnvironment = NullaryEnvironment.getNullaryEnvironment().getCarJarFilesInEnvironment();
        
        for (final File jarFile : carJarFilesInEnvironment) {
            if (jarFile.exists()) {
                try {
                    JarFile jf = new JarFile(jarFile);
                    
                    ZipEntry entry = jf.getEntry(CarBuilder.CAR_ADDITIONAL_FILES_NAME);
                    if (entry == null) {
                        // the jar file does not contain a Car.additionalFiles file, so just skip it
                        continue;
                    }
                    InputStream is = jf.getInputStream(entry);
                    
                    BufferedReader reader = new BufferedReader(TextEncodingUtilities.makeUTF8Reader(is));
                    
                    String folderPath = WORKSPACE_DECLARATION_IN_JAR_BASE_FOLDER.getName() + "/";
                    int folderPathLen = folderPath.length();
                    
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith(folderPath)) {
                            String cwsName = line.substring(folderPathLen);

                            // if the cws file has already been added to the map, then it must come from an earlier
                            // classpath entry, and so we honour the first entry rather than this one
                            if (!cwsToJarFileManagerMap.containsKey(cwsName)) {
                                JarFileManager jfm = new JarFileManager(jf);
                                cwsToJarFileManagerMap.put(cwsName, jfm);
                            }
                        }
                    }

                } catch (IOException e) {
                    // skip this bad entry and continue
                }
            }
        }
        
        return cwsToJarFileManagerMap;
    }
}
