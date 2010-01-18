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
 * ModulePackager.java
 * Creation date: Sep 13, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.ResourcePath.Folder;


/**
 * This class provides utility methods to aid in persistence and navigation of module resources in .packaged form.
 * @author Edward Lam
 */
public class ModulePackager {
    
    /*
      TODOEL: add more properties to the manifest.  Examples:

      BCEL manifest:

        Manifest-Version: 1.0
        Created-By: Apache Jakarta Maven
        Built-By: dahm
        Package: org.apache.bcel
        Build-Jdk: 1.4.1_01
        
        Name: org.apache.bcel
        Specification-Title: bcel
        Specification-Version: 5.1
        Specification-Vendor: Apache Software Foundation
        Implementation-Title: org.apache.bcel
        Implementation-Version: 5.1
        Implementation-Vendor: Apache Software Foundation
        
      cecore.mf:

        Manifest-Version: 1.0
        Product-Name: Crystal Enterprise Java SDK (C)
        Implementation-Title: Crystal Enterprise SDK core
        Implementation-Version: 11.0.0.700
        Implementation-Vendor: Business Objects, Inc.(C)
     
     */
    
    /** The name of the manifest attribute for a Car's canonical name. */
    static final String CAR_NAME_MANIFEST_ATTRIBUTE = "Car-Name";

    /**
     * Test target.
     * Creates a test .jar.
     */
    public static void main(String[] args) throws IOException {
        
//        JarFile jarFile = new JarFile("C:\\Prelude.jar");
//        for (Enumeration enum = jarFile.entries(); enum.hasMoreElements(); ) {
//            JarEntry jarEntry = (JarEntry)enum.nextElement();
//            System.out.println(jarEntry.getName());
//        }

        // Create the manifest
        Manifest manifest = new Manifest();
        
        // Add some attributes.
        Attributes mainAttributes = manifest.getMainAttributes();
        
        String manifestVersionString = Attributes.Name.MANIFEST_VERSION.toString();
        mainAttributes.putValue(manifestVersionString, "1.0");
        
        String mainClassString = Attributes.Name.MAIN_CLASS.toString();
        mainAttributes.putValue(mainClassString, "foo.bar.Baz");
        
//        JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\temp\\foo.jar")), manifest);
        JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\foo.jar")), manifest);
        
        // Set comment and compression level.
        jos.setComment("This is the jar file comment.");
        jos.setLevel(Deflater.DEFAULT_COMPRESSION);
        
        // Add an entry.
        JarEntry jarEntry = new JarEntry("entryName");
//        jarEntry.getAttributes().putValue("someAttributeName", "someValue");
        
        jos.putNextEntry(jarEntry);
        jos.write(new byte[]{1, 2, 3});
        
        
        // Add a second entry.
        jarEntry = new JarEntry("foo/bar.baz");
        jos.putNextEntry(jarEntry);
        jos.write(new byte[]{49, 105, 96, 97, 98});

        // Add a third entry.
        jarEntry = new JarEntry("/foo/bar/Baz.class");
        jos.putNextEntry(jarEntry);
        jos.write(new byte[]{49, 105, 96, 97, 98});

        jos.flush();
        jos.close();
    }

    /**
     * Write a module from the workspace to a file.
     * @param workspace the workspace containing the module to write.
     * @param moduleName the name of the module to write.
     * @param packagedModuleFile the file which will contain the packaged module.  This will be created if it doesn't already exist.
     * @throws IOException
     */
    public static void writePackagedModule(CALWorkspace workspace, ModuleName moduleName, File packagedModuleFile) throws IOException {
        writeModuleToJar(workspace, moduleName, new FileOutputStream(packagedModuleFile), true);
    }
    
    /**
     * Write the workspace resources for a metamodule to a jar.
     * @param workspace the workspace with the module to persist.
     * @param moduleName the name of a module to persist.
     * @param outputStream the output stream to which the jar will be written.
     * @throws IOException
     */
    public static void writeModuleToJar(CALWorkspace workspace, ModuleName moduleName, OutputStream outputStream) throws IOException {
        writeModuleToJar(workspace, moduleName, outputStream, false);
    }
    
    /**
     * Write the workspace resources for a metamodule to a jar.
     * @param workspace the workspace with the module to persist.
     * @param moduleName the name of a module to persist.
     * @param outputStream the output stream to which the jar will be written.
     * @param encrypt if true, the resource files will be encrypted in a simple way.
     * @throws IOException
     */
    private static void writeModuleToJar(CALWorkspace workspace, ModuleName moduleName,
                                         OutputStream outputStream, boolean encrypt) throws IOException {
        
//      JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(outputStream), manifest);
        JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(outputStream));
        
        // Write out a minimal manifest.
        writeMinimalManifestToJar(jos);
        
        // Set compression level.
        jos.setLevel(Deflater.DEFAULT_COMPRESSION);
        
        writeModuleToJar(workspace, moduleName, jos, encrypt, false);
        
        // For some reason, closing the enclosing output stream isn't sufficient.
        jos.close();
    }

    /**
     * Writes a minimal manifest file to the jar.
     * @param jos the jar output stream.
     * @throws IOException
     */
    static void writeMinimalManifestToJar(JarOutputStream jos) throws IOException {
        // Create the manifest
        Manifest manifest = new Manifest();
        
        // Add some attributes.
        Attributes mainAttributes = manifest.getMainAttributes();
        
        String manifestVersionString = Attributes.Name.MANIFEST_VERSION.toString();
        mainAttributes.putValue(manifestVersionString, "1.0");
        
        writeManifestToJar(jos, manifest);
    }

    /**
     * Writes a minimal manifest file to the Car or Car-jar.
     * @param jos the jar output stream.
     * @param carName the canonical name of the car.
     * @throws IOException
     */
    static void writeMinimalManifestToCar(JarOutputStream jos, String carName) throws IOException {
        // Create the manifest
        Manifest manifest = new Manifest();
        
        // Add some attributes.
        Attributes mainAttributes = manifest.getMainAttributes();
        
        String manifestVersionString = Attributes.Name.MANIFEST_VERSION.toString();
        mainAttributes.putValue(manifestVersionString, "1.0");
        
        mainAttributes.putValue(CAR_NAME_MANIFEST_ATTRIBUTE, carName);
        
        writeManifestToJar(jos, manifest);
    }

    /**
     * Writes a manifest file to the jar.
     * @param jos the jar output stream.
     * @param manifest the manifest to be written out.
     * @throws IOException
     */
    private static void writeManifestToJar(JarOutputStream jos, Manifest manifest) throws IOException {
        // Manually add the manifest, so that we can set the time for its entry.
        ZipEntry e = new ZipEntry(JarFile.MANIFEST_NAME);

        // TODOEL: Find a sensible time to put here.
        // If this time changes for every output, the .jar file which is created will have a different timestamp for this entry.
        // This will cause the digest on the jar "packaged module" in CE to be different.
        //
        // Possibilities:
        //   The latest time for any of the added resources.
        //   The time of the first resource added after Manifest.mf.
        //   Use a pre-written Manifest.mf.
        //
        e.setTime(0);

        jos.putNextEntry(e);
        manifest.write(new BufferedOutputStream(jos));
        jos.closeEntry();
    }

    /**
     * Write the workspace resources for a module to a jar.
     * @param workspace the workspace with the module to persist.
     * @param moduleName the name of a module to persist.
     * @param jos the JarOutputStream to be written to.
     * @param buildSourcelessModules whether to build the jar with sourceless modules (i.e. the CAL files will be empty stubs).
     * @throws IOException
     */
    static void writeModuleToJar(CALWorkspace workspace, ModuleName moduleName, JarOutputStream jos, boolean buildSourcelessModules) throws IOException {
        writeModuleToJar(workspace, moduleName, jos, false, buildSourcelessModules);
    }
    
    /**
     * Write the workspace resources for a module to a jar.
     * @param workspace the workspace with the module to persist.
     * @param moduleName the name of a module to persist.
     * @param jos the JarOutputStream to be written to.
     * @param encrypt if true, the resource files will be encrypted in a simple way.
     * @param buildSourcelessModules whether to build the jar with sourceless modules (i.e. the CAL files will be empty stubs).
     * @throws IOException
     */
    private static void writeModuleToJar(CALWorkspace workspace, ModuleName moduleName, JarOutputStream jos, boolean encrypt, boolean buildSourcelessModules) throws IOException {

        // Iterate over the resource managers.
        for (final ResourceManager resourceManager : workspace.getResourceManagersForModule(moduleName)) {
            if (resourceManager instanceof ModuleResourceManager) {
                writeResourcesToJar(workspace, (ModuleResourceManager)resourceManager, moduleName, jos, encrypt, buildSourcelessModules);
            }
        }
    }
    
    /**
     * Writes the user resources associated with a module into a jar.
     * @param workspace the workspace with the module.
     * @param moduleName the name of the module whose user resources are to be written out.
     * @param jos the JarOutputStream to be written to.
     * @throws IOException
     */
    public static void writeUserResourcesToJar(final CALWorkspace workspace, final ModuleName moduleName, final JarOutputStream jos) throws IOException {
        final UserResourceManager userResourceManager = workspace.getUserResourceManager(moduleName);
        writeResourcesToJar(workspace, userResourceManager, moduleName, jos, false, false);
    }

    /**
     * Writes the resources from a resource manager into a jar.
     * @param workspace the workspace with the module to persist.
     * @param resourceManager the resource manager.
     * @param moduleName the name of a module to persist.
     * @param jos the JarOutputStream to be written to.
     * @param encrypt if true, the resource files will be encrypted in a simple way.
     * @param buildSourcelessModules whether to build the jar with sourceless modules (i.e. the CAL files will be empty stubs).
     * @throws IOException
     */
    private static void writeResourcesToJar(final CALWorkspace workspace, final ModuleResourceManager resourceManager, final ModuleName moduleName, final JarOutputStream jos, boolean encrypt, final boolean buildSourcelessModules) throws IOException {
        
        // The byte buffer and array used for copying resources.
        byte[] bufArray = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(bufArray);
        
        boolean isSourceManager = resourceManager instanceof CALSourceManager;
         
        // Iterate over the resources in the manager.
        for (Iterator<WorkspaceResource> it = resourceManager.getResourceIterator(moduleName); it.hasNext(); ) {
            WorkspaceResource moduleResource = it.next();
            
            // Get the name of the resource, and the path to that resource.
            ResourceIdentifier resourceIdentifier = moduleResource.getIdentifier();
            ResourcePath.FilePath filePath = workspace.getResourcePath(resourceIdentifier);
            
            // Add a jar entry.
            JarEntry jarEntry = new JarEntry(filePath.getPathStringMinusSlash());
            jarEntry.setTime(moduleResource.getTimeStamp());
            jos.putNextEntry(jarEntry);
            
            if (!isSourceManager || !buildSourcelessModules) {
                // We omit the content of the entry if the resource is a module source and buildSourcelessModules is true.
                
                // Write the bytes for that entry.
                InputStream moduleResourceStream = null;
                try {
                    moduleResourceStream = moduleResource.getInputStream(new Status("Input Status"));       // shouldn't normally be null..
                    
                    ReadableByteChannel byteChannel = Channels.newChannel(moduleResourceStream);
                    while (true) {
                        int bytesRead = byteChannel.read(buffer);
                        
                        if (bytesRead < 0) {
                            // end of file.
                            break;
                        }
                        
                        // Write the data.
                        jos.write(bufArray, 0, bytesRead);
                        buffer.rewind(); // rewind the buffer so that the next read starts at position 0 again
                    }
                } finally {
                    // Don't forget to close the input stream..
                    if (moduleResourceStream != null) {
                        try {
                            moduleResourceStream.close();
                        } catch (IOException ioe) {
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Get the names representing the resources in a folder.
     * 
     * @param jarFileManager the jar file to search
     * @param folder the folder to search.
     * @param pathMapper the path mapper to use to map paths back to resource names
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getFolderResourceNames(JarFileManager jarFileManager, ResourcePath.Folder folder, ResourcePathMapper pathMapper) {
        return getFilteredFolderResourceNames(jarFileManager, folder, pathMapper, null);
    }
    
    /**
     * Get a filtered list of the names representing the resources in a folder.
     * 
     * @param jarFileManager the jar file to search
     * @param folder the folder to search.
     * @param pathMapper the path mapper to use to map paths back to resource names
     * @param filter the filter to use for determining which resource names to keep in the returned list.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getFilteredFolderResourceNames(JarFileManager jarFileManager, ResourcePath.Folder folder, ResourcePathMapper pathMapper, ResourceName.Filter filter) {
        
        String relativePathOfFolder = folder.getPathStringMinusSlash();
        
        Set<ResourceName> filteredSet = new TreeSet<ResourceName>();        // sort alphabetically..
        
        // Iterate over the entries in the jar file in the folder.
        Set<String> fileNamesInFolder = jarFileManager.getFileNamesInFolder(relativePathOfFolder);
        
        if (fileNamesInFolder != null) {
            for (final String fileName : fileNamesInFolder) {
                // It passes if the path mapper returns a non-null resource name (e.g. it has the right file extension).
                ResourceName resourceName = pathMapper.getResourceNameFromFolderAndFileName(folder, fileName);
                if (resourceName != null) {
                    // If the filter is not null, check the path to make sure it's acceptable first
                    if (filter == null || filter.accept(resourceName)) {
                        filteredSet.add(resourceName);
                    }
                }
            }
        }
        
        return new ArrayList<ResourceName>(filteredSet);
    }
    
    /**
     * Get the names representing the resources in the subtree rooted at the folder.
     * 
     * @param jarFileManager the jar file to search
     * @param folder the folder to search.
     * @param pathMapper the path mapper to use to map paths back to resource names
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getResourceNamesInSubtreeRootedAtFolder(JarFileManager jarFileManager, ResourcePath.Folder folder, ResourcePathMapper pathMapper) {
        
        String relativePathOfFolder = folder.getPathStringMinusSlash();
        
        Set<ResourceName> filteredSet = new TreeSet<ResourceName>();        // sort alphabetically..
        
        // Iterate over the entries in the jar file in all the subfolders and the given folder.
        Set<String> foldersToCheck = new HashSet<String>();
        foldersToCheck.add(relativePathOfFolder);
        foldersToCheck.addAll(jarFileManager.getAllDescendentFolderPathsInFolder(relativePathOfFolder));
        
        for (final String relativePathOfFolderToCheck : foldersToCheck) {
            
            ResourcePath.Folder resourceFolderToCheck = ResourcePath.Folder.makeFromPathStringMinusSlash(relativePathOfFolderToCheck);
            
            Set<String> fileNamesInFolder = jarFileManager.getFileNamesInFolder(relativePathOfFolderToCheck);

            if (fileNamesInFolder != null) {
                for (final String fileName : fileNamesInFolder) {
                    // It passes if the path mapper returns a non-null resource name (e.g. it has the right file extension).
                    ResourceName resourceName = pathMapper.getResourceNameFromFolderAndFileName(resourceFolderToCheck, fileName);
                    if (resourceName != null) {
                        filteredSet.add(resourceName);
                    }
                }
            }
        }
        
        return new ArrayList<ResourceName>(filteredSet);
    }
    
    /**
     * Get the subfolders of a given folder which contain any files with a given extension.
     * @param parentFolder the folder whose subfolders will be searched.  The names of these subfolders
     *   are eligible to be returned.
     * @param fileExtension the file extension to search for.  If null, all files will be returned.
     * @param convertFromFileSystemName whether the folder names in the jar file are encoded with getFileSystemName(), and should be decoded.
     * @return the names of folders for which files with the given extension exist.
     */
    public static Set<String> getFolderNamesWithFileExtension(JarFileManager jarFileManager, ResourcePath.Folder parentFolder,
                                                      String fileExtension, boolean convertFromFileSystemName) {

        String relativePathOfParentFolder = parentFolder.getPathStringMinusSlash();
        
        Set<String> matchingFolderNameSet = new HashSet<String>();
        
        // Get the components of the parent folder.
        String[] parentFolderElements = parentFolder.getPathElements();
        int nParentFolderElements = parentFolderElements.length;
        
        // Iterate over the entries in the jar file in the folder.
        Set<String> subfolderPaths = jarFileManager.getAllDescendentFolderPathsInFolder(relativePathOfParentFolder);
        
        if (subfolderPaths != null) {
            for (final String subfolderPath : subfolderPaths) {
                
                Set<String> folderContents = jarFileManager.getFileNamesInFolder(subfolderPath);
                
                if (folderContents != null) {
                    
                    for (final String fileName : folderContents) {
                        
                        // Get the name of the entry, minus the leading slash.
                        String entryName = subfolderPath + "/" + fileName;
                        
                        // Split into its components.
                        String[] entryNameComponents = (entryName.charAt(0) == '/') ? entryName.substring(1).split("/") : entryName.split("/");
                        int nEntryNameComponents = entryNameComponents.length;
            
//            // Convert from file system name if necessary.
//            if (convertFromFileSystemName) {
//                for (int i = 0; i < nEntryNameComponents; i++) {
//                    entryNameComponents[i] = FileSystemResourceHelper.fromFileSystemName(entryNameComponents[i]);
//                }
//            }

                        // Check that there are enough components in the entry.
                        if (nEntryNameComponents <= nParentFolderElements) {
                            continue;
                        }
                        // Check that the components of the entry name match with the parent folder.
                        for (int i = 0; i < nParentFolderElements; i++) {
                            if (!parentFolderElements[i].equals(entryNameComponents[i])) {
                                continue;
                            }
                        }
                        
                        // Check that the file extension matches.
                        if (fileExtension != null && !entryNameComponents[nEntryNameComponents - 1].endsWith("." + fileExtension)) {
                            continue;
                        }
                        
                        // If we're here, the folder matches.
                        String matchingFolderName = entryNameComponents[nParentFolderElements];
                        if (convertFromFileSystemName) {
                            matchingFolderName = FileSystemResourceHelper.fromFileSystemName(matchingFolderName);
                        }
                        matchingFolderNameSet.add(matchingFolderName);
                    }
                }
            }
        }
        
        return matchingFolderNameSet;
    }
    
    /**
     * Returns a set of folder resource paths of all non-empty subfolders in the given folder.
     * @param jarFileManager the jar file manager containing the jar to be processed.
     * @param parentFolder the folder resource path of the root of the subtree to process.
     * @return a set of folder resource paths of all non-empty subfolders in the given folder.
     */
    public static Set<ResourcePath.Folder> getResourcePathsOfAllNonEmptySubfolders(JarFileManager jarFileManager, ResourcePath.Folder parentFolder) {
        
        String relativePathOfParentFolder = parentFolder.getPathStringMinusSlash();
        
        Set<ResourcePath.Folder> result = new HashSet<Folder>();
        
        // Iterate over the entries in the jar file in the folder.
        Set<String> subfolderPaths = jarFileManager.getAllDescendentFolderPathsInFolder(relativePathOfParentFolder);
        
        for (final String subfolderPath : subfolderPaths) {            
            if (!jarFileManager.getFileNamesInFolder(subfolderPath).isEmpty()) {
                result.add(ResourcePath.Folder.makeFromPathStringMinusSlash(subfolderPath));
            }
        }
        return result;
    }
}
