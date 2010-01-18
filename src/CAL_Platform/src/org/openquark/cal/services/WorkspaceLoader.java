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
 * WorkspaceLoader.java
 * Creation date: Feb 27, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.util.ClassInfo;
import org.openquark.util.General;
import org.openquark.util.Pair;
import org.openquark.util.TextEncodingUtilities;


/**
 * A class responsible for loading a program that comprises an initial workspace.
 * @author Edward Lam
 */
public class WorkspaceLoader {

    private static final String LINE_COMMENT_CHARS = "//";
    private static final String BEGIN_BLOCK_COMMENT_CHARS = "/*";
    private static final String END_BLOCK_COMMENT_CHARS = "*/";
    
    /**
     * Encapsulates the various pieces of information built up by {@link WorkspaceLoader#getWorkspaceDeclarationInfoHelper}
     * and returned by {@link WorkspaceLoader#getWorkspaceDeclarationInfo}.
     *
     * @author Joseph Wong
     */
    private static final class WorkspaceDeclarationInfo {
        
        /**
         * names of workspace declarations already traversed.
         */
        private final Set<String> traversedDeclarationNamesSet;

        /**
         * Map from module name to the stored module for that module
         * for each module which has been traversed.
         */
        private final LinkedHashMap<ModuleName, StoredVaultElement.Module> nameToStoredModuleMap;

        /**
         * A map containing the names of the modules included by the workspace declaration and
         * their associated entries in all the workspace declarations in the tree.
         */
        private final Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToWorkspaceDeclInfoMap;

        /**
         * A Map mapping a workspace declaration name to a LinkedHashSet of other declarations it imports.
         */
        private final Map<String, LinkedHashSet<String>> importsMap;
        
        /**
         * Constructs a WorkspaceDeclarationInfo.
         */
        WorkspaceDeclarationInfo(Set<String> traversedDeclarationNamesSet, LinkedHashMap<ModuleName, StoredVaultElement.Module> nameToStoredModuleMap, Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToWorkspaceDeclInfoMap, Map<String, LinkedHashSet<String>> importsMap) {
            this.traversedDeclarationNamesSet = traversedDeclarationNamesSet;
            this.nameToStoredModuleMap = nameToStoredModuleMap;
            this.moduleNameToWorkspaceDeclInfoMap = moduleNameToWorkspaceDeclInfoMap;
            this.importsMap = importsMap;
        }

        /**
         * @return the {@link WorkspaceLoader.WorkspaceDeclarationInfo#importsMap}.
         */
        Map<String, LinkedHashSet<String>> getImportsMap() {
            return importsMap;
        }

        /**
         * @return the {@link WorkspaceLoader.WorkspaceDeclarationInfo#moduleNameToWorkspaceDeclInfoMap}.
         */
        Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> getModuleNameToWorkspaceDeclInfoMap() {
            return moduleNameToWorkspaceDeclInfoMap;
        }

        /**
         * @return the {@link WorkspaceLoader.WorkspaceDeclarationInfo#nameToStoredModuleMap}.
         */
        Map<ModuleName, StoredVaultElement.Module> getNameToStoredModuleMap() {
            return nameToStoredModuleMap;
        }

        /**
         * @return the {@link WorkspaceLoader.WorkspaceDeclarationInfo#traversedDeclarationNamesSet}.
         */
        Set<String> getTraversedDeclarationNamesSet() {
            return traversedDeclarationNamesSet;
        }
    }
    
    /**
     * Get a file object from a path
     * @param path the string representation of the path.  This can be a path relative to the classloader, or a URI.
     * @return File the corresponding file, or null if the path is invalid or the file does not exists.
     */
    public static File fileFromPath(String path) {

        // Check for a workspace file defined by a system property.
        try {
            URI uri = new URI(path);
            return new File(uri);
        } catch (URISyntaxException mue) {          // bad uri
        } catch (IllegalArgumentException iae) {    // invalid param
        }

        // The argument for url and uri constructors differ slightly - url accepts spaces, but for the uri they must be encoded.
        try {
            URL url = new URL(path);
            return new File(urlToUri(url));
        } catch (MalformedURLException e) {         // bad url
        } catch (IllegalArgumentException iae) {    // invalid param
        }

        // Attempt to parse the pathname relative to the current directory (relative to the class loader).
        URL resource = ClassInfo.getResource(path);
        
        if (resource == null && path.startsWith("/")) {
            // Try again if we have a leading slash..
            resource = ClassInfo.getResource(path.substring(1));
        }
        if (resource == null) {
            return null;
        }

        String fileName = General.decodeUrlUtf8(resource.getFile());
        File fileFromProperty = new File(fileName);
        if (fileFromProperty.exists()) {
            return fileFromProperty;
        }
        
        return null;
    }
    
    /**
     * Get the names of the workspace declarations required by the given workspace declaration.
     * ie. Get the declarations on which a given declaration depends.
     * @param workspaceDeclarationProvider the workspace declaration to analyse.
     * @param workspace the cal workspace.
     * @param status the tracking status object.
     * @return the names of the workspace declarations on which the given workspace depends.
     * This set will include the name of the provided workspace itself.
     */
    public static Set<String> getRequiredDeclarationNames(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider,
                                                  CALWorkspace workspace, Status status) {

        WorkspaceDeclarationInfo info = getWorkspaceDeclarationInfo(workspaceDeclarationProvider, workspace.getVaultRegistry(), status);

        return info.getTraversedDeclarationNamesSet();
    }
    
    /**
     * Get the names of the modules included directly or indirectly in the given workspace declaration.
     * @param workspaceDeclarationProvider the workspace declaration to analyse.
     * @param workspace the CAL workspace.
     * @param status the tracking status object.
     * @return a set containing the names of the modules included by the workspace declaration.
     */
    public static Set<ModuleName> getStoredModuleNames(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider,
                                                     CALWorkspace workspace, Status status) {

        WorkspaceDeclarationInfo info = getWorkspaceDeclarationInfo(workspaceDeclarationProvider, workspace.getVaultRegistry(), status);

        // the key set of nameToStoredModuleMap is the set of module names
        return info.getNameToStoredModuleMap().keySet();
    }
    
    /**
     * Get the names of the modules included directly or indirectly in the given workspace declaration, in the form
     * of a map (module name -> LinkedHashSet of workspace declaration names) that provides the names of the workspace declarations
     * in which a particular module is declared (with the first name in the list being the one that counts, while the
     * remaining workspace declarations contained entries that are shadowed).
     * 
     * @param workspaceDeclarationProvider the workspace declaration to analyse.
     * @param workspace the CAL workspace.
     * @param status the tracking status object.
     * @return a map containing the names of the modules included by the workspace declaration and
     *         their associated entries in all the workspace declarations in the tree.
     */
    public static Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>>
    getStoredModuleNameToWorkspaceDeclNamesMap(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider, CALWorkspace workspace, Status status) {

        WorkspaceDeclarationInfo info = getWorkspaceDeclarationInfo(workspaceDeclarationProvider, workspace.getVaultRegistry(), status);

        return info.getModuleNameToWorkspaceDeclInfoMap();
    }
    
    /**
     * Get a map containing information about which workspace declaration imports which other declarations.
     * 
     * @param workspaceDeclarationProvider the workspace declaration to analyse.
     * @param workspace the CAL workspace.
     * @param status the tracking status object.
     * @return a map mapping a workspace declaration name to a LinkedHashSet of other declarations it imports.
     */
    public static Map<String, LinkedHashSet<String>>
    getWorkspaceDeclarationImportsMap(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider, CALWorkspace workspace, Status status) {

        WorkspaceDeclarationInfo info = getWorkspaceDeclarationInfo(workspaceDeclarationProvider, workspace.getVaultRegistry(), status);

        return info.getImportsMap();
    }
    
    /**
     * Get the stored module from a reader on a workspace specification.
     * @param workspaceDeclarationProvider the provider for the workspace spec.  Should not be null.
     * @param vaultRegistry the registry to use to look up the paths referenced by the workspace definition.
     * @param status the tracking status object.
     * @return the stored modules corresponding to the specification, or null if the workspace declaration provider is null.
     * WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider
     */
    static StoredVaultElement.Module[] getStoredModules(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider,
                                                        VaultRegistry vaultRegistry, Status status) {

        // Defer to the helper method.
        WorkspaceDeclarationInfo info = getWorkspaceDeclarationInfo(workspaceDeclarationProvider, vaultRegistry, status);
        
        // Convert to an array and return.
        Collection<StoredVaultElement.Module> storedModuleSet = info.getNameToStoredModuleMap().values();
        return storedModuleSet.toArray(new StoredVaultElement.Module[storedModuleSet.size()]);
    }
    
    /**
     * Helper method for the various API methods of this class that obtains information contained in a workspace declaration file.
     * Collects declared modules, and also processes imported workspace declarations.
     * 
     * @param workspaceDeclarationProvider the provider for the workspace declaration which should be looked at.
     * @param vaultRegistry the vault registry to use to instantiate vaults.
     * @param status the tracking status object.
     */
    private static WorkspaceDeclarationInfo getWorkspaceDeclarationInfo(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider, VaultRegistry vaultRegistry, Status status) {
        
        // names of workspace declarations already traversed.
        Set<String> traversedDeclarationNamesSet = new HashSet<String>();

        // Map from module name to the stored module for that module
        // for each module which has been traversed.
        LinkedHashMap<ModuleName, StoredVaultElement.Module> nameToStoredModuleMap = new LinkedHashMap<ModuleName, StoredVaultElement.Module>();

        // A map containing the names of the modules included by the workspace declaration and
        // their associated entries in all the workspace declarations in the tree.
        Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToWorkspaceDeclInfoMap = new HashMap<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>>();

        // A Map mapping a workspace declaration name to a LinkedHashSet of other declarations it imports.
        Map<String, LinkedHashSet<String>> importsMap = new HashMap<String, LinkedHashSet<String>>();
        
        getWorkspaceDeclarationInfoHelper(
            workspaceDeclarationProvider,
            vaultRegistry,
            traversedDeclarationNamesSet,
            nameToStoredModuleMap,
            moduleNameToWorkspaceDeclInfoMap,
            importsMap,
            status);
        
        return new WorkspaceDeclarationInfo(traversedDeclarationNamesSet, nameToStoredModuleMap, moduleNameToWorkspaceDeclInfoMap, importsMap);
    }
    
    /**
     * Helper method for getWorkspaceDeclarationInfo().
     *   Collects declared modules, and calls itself recursively on declared workspace declarations.
     * 
     * @param workspaceDeclarationProvider the provider for the workspace declaration which should be looked at.
     * @param vaultRegistry the vault registry to use to instantiate vaults.
     * @param traversedDeclarationNamesSet names of workspace declarations already traversed.
     *   This will be populated by this method.
     * @param nameToStoredModuleMap Map from module name to the stored module for that module
     *   for each module which has been traversed.  This map will be populated by this method.
     * @param moduleNameToWorkspaceDeclInfoMap a (Map<String, LinkedHashSet<Pair<String, VaultElementInfo>>>)
     *         containing the names of the modules included by the workspace declaration and
     *         their associated entries in all the workspace declarations in the tree. This will be populated by this method.
     * @param importsMap a Map mapping a workspace declaration name to a LinkedHashSet of other declarations it imports.
     * @param status the tracking status object.
     */
    private static void getWorkspaceDeclarationInfoHelper(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider, VaultRegistry vaultRegistry,
        Set<String> traversedDeclarationNamesSet, Map<ModuleName, StoredVaultElement.Module> nameToStoredModuleMap, Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToWorkspaceDeclInfoMap, Map<String, LinkedHashSet<String>> importsMap, Status status) {

        // Check whether we've already traversed a workspace declaration with this name.
        final String workspaceDeclarationName = workspaceDeclarationProvider.getName();
        if (traversedDeclarationNamesSet.contains(workspaceDeclarationName)) {
            return;
        }
        // Add to the set of workspace declarations we've traversed.
        traversedDeclarationNamesSet.add(workspaceDeclarationName);
        
        // Add an entry of the workspace declarations to the importsMap.
        LinkedHashSet<String> imports = importsMap.get(workspaceDeclarationName);
        if (imports == null) {
            imports = new LinkedHashSet<String>();
            importsMap.put(workspaceDeclarationName, imports);
        }
        
        // Get an input stream on the workspace declaration.
        InputStream providerInputStream = workspaceDeclarationProvider.getInputStream(vaultRegistry, status);
        
        if (providerInputStream == null) {
            String message = "The specified workspace declaration does not exist: " + workspaceDeclarationProvider.getName();
            status.add(new Status(Status.Severity.ERROR, message, null));
            return;
        }
        
        // the names of modules specified in the current workspace declaration only.  For error-checking.
        Set<ModuleName> moduleNamesInCurrentDeclaration = new HashSet<ModuleName>();
        
        // Lex the file into lines of tokens.
        List<Pair<Integer, List<String>>> workspaceDeclarationLinesList;
        try {
            workspaceDeclarationLinesList = lexWorkspaceDeclarationStream(TextEncodingUtilities.makeUTF8Reader(providerInputStream), status);
        
        } finally {
            try {
                providerInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        for (final Pair<Integer, List<String>> lineNumTokensPair : workspaceDeclarationLinesList) {
            
            int lineNum = (lineNumTokensPair.fst()).intValue();
            List<String> lineTokens = lineNumTokensPair.snd();
            
            // Check for an import directive
            if (!lineTokens.isEmpty() && lineTokens.get(0).equals("import")) {
                
                if (lineTokens.size() > 2 && lineTokens.get(1).equals("car")) {
                    // importing a Car file.
                    
                    // the syntax for importing a Car in the workspace declaration is:
                    //
                    // import car <declaration for car file> <car workspace spec name>
                    //
                    // where <declaration for car file> is a declaration of the car file including
                    // the vault in which it is stored, e.g. StandardVault test.car
                    //
                    // So, for example, a complete declaration line may look like this:
                    //
                    // import car StandardVault test.car main.carspec
                    
                    // Get the Car workspace spec name.
                    String specName = lineTokens.get(lineTokens.size() - 1);
                    
                    // Get the info for the vault element.
                    lineTokens = lineTokens.subList(2, lineTokens.size() - 1);
                    
                    VaultElementInfo vaultElementInfo = VaultElementInfo.Basic.makeFromDeclarationString(lineTokens, status);
                    if (vaultElementInfo == null) {
                        continue;
                    }
                    
                    // Get the Car
                    StoredVaultElement.Car car = vaultRegistry.getStoredCar(vaultElementInfo, status);
                    if (car == null) {
                        status.add(new Status(Status.Severity.WARNING, "Error reading vault element from " + workspaceDeclarationName + ", line: " + lineNum, null));
                        continue;
                    }
                    
                    // Read the Car workspace spec.
                    ModuleName[] moduleNames = getModulesInWorkspaceSpec(car, specName, status);
                    
                    // Read the resources from the Car vault
                    CarVault carVault = car.getCarVault();

                    Set<ModuleName> moduleNamesInCar = new HashSet<ModuleName>();
                    for (final ModuleName element : moduleNames) {
                        StoredVaultElement.Module storedModule = carVault.getStoredModule(element, 0, status);
                        addStoredModuleToMapHelper(storedModule, workspaceDeclarationName, moduleNamesInCar, nameToStoredModuleMap, moduleNameToWorkspaceDeclInfoMap, status);
                    }
                    
                } else {
                    // importing another workspace declaration.
                    
                    lineTokens = lineTokens.subList(1, lineTokens.size());
                    
                    // Get the info for the vault element.
                    VaultElementInfo vaultElementInfo = VaultElementInfo.Basic.makeFromDeclarationString(lineTokens, status);
                    if (vaultElementInfo == null) {
                        status.add(new Status(Status.Severity.WARNING, "Error reading vault element from " + workspaceDeclarationName + ", line: " + lineNum, null));
                        continue;
                    }
                    
                    // Get the stored workspace declaration.
                    StoredVaultElement.WorkspaceDeclaration workspaceDeclaration = vaultRegistry.getStoredWorkspaceDeclaration(vaultElementInfo, status);
                    if (workspaceDeclaration == null) {
                        status.add(new Status(Status.Severity.WARNING, "Error reading vault element from " + workspaceDeclarationName + ", line: " + lineNum, null));
                        continue;
                    }
                    
                    // Wrap as a declaration provider.
                    WorkspaceDeclaration.StreamProvider workspaceDeclarationProviderFromLine =
                            DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider(vaultElementInfo);
                    
                    imports.add(workspaceDeclarationProviderFromLine.getName());
                    
                    // Recursive call.
                    getWorkspaceDeclarationInfoHelper(workspaceDeclarationProviderFromLine, vaultRegistry,
                                             traversedDeclarationNamesSet, nameToStoredModuleMap, moduleNameToWorkspaceDeclInfoMap, importsMap, status);
                }
                
            } else {
                // specifying a stored module.
                VaultElementInfo vaultElementInfo = VaultElementInfo.Basic.makeFromDeclarationString(lineTokens, status);
                if (vaultElementInfo == null) {
                    status.add(new Status(Status.Severity.WARNING, "Invalid module declaration in " + workspaceDeclarationName + ", line: " + lineNum, null));
                    continue;
                }
                
                StoredVaultElement.Module storedModule = vaultRegistry.getStoredModule(vaultElementInfo, status);
                if (storedModule == null) {
                    status.add(new Status(Status.Severity.WARNING, "Error reading vault element from " + workspaceDeclarationName + ", line: " + lineNum, null));
                    continue;
                }
                
                addStoredModuleToMapHelper(storedModule, workspaceDeclarationName, moduleNamesInCurrentDeclaration, nameToStoredModuleMap, moduleNameToWorkspaceDeclInfoMap, status);
            }
        }
    }

    /**
     * Retrieves the list of module names contained in the specified Car workspace spec file.
     * 
     * The spec file format is a simple whitespace-delimited list of module names that also allows
     * both slash-slash single line comments and C-style block comments.
     * 
     * @param car the Car from which the spec file is to be fetched.
     * @param specName the name of the spec file.
     * @param status the status tracking object.
     * @return the array of module names contained in the spec file.
     */
    private static ModuleName[] getModulesInWorkspaceSpec(StoredVaultElement.Car car, String specName, Status status) {
        InputStream workspaceSpecInputStream = car.getWorkspaceSpec(specName, status);
        if (workspaceSpecInputStream == null) {
            status.add(new Status(Status.Severity.ERROR, "Cannot read the workspace spec " + specName + " in the Car " + car.getName(), null));
            return new ModuleName[0];
        }
        
        BufferedReader reader = new BufferedReader(TextEncodingUtilities.makeUTF8Reader(workspaceSpecInputStream));
        
        try {
            StringBuilder moduleNamesInWhitespaceAndCommentSeparatedList = new StringBuilder();
            
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                // trim the leading whitespace to make the single-line comment handling easier
                line = line.trim();
                
                if (!line.startsWith("//")) { // handle slash-slash style single-line comments
                    moduleNamesInWhitespaceAndCommentSeparatedList.append(line).append(' ');
                }
            }

            // get rid of the C-style block comments
            String moduleNamesInWhitespaceSeparatedList =
                moduleNamesInWhitespaceAndCommentSeparatedList.toString().replaceAll("/\\*([^*]|\\*[^/])*\\*/", "");
            
            // tokenize the remaining content into a list of module names (separated by whitespace)
            StringTokenizer tokenizer = new StringTokenizer(moduleNamesInWhitespaceSeparatedList);
            
            List<ModuleName> moduleNameList = new ArrayList<ModuleName>();
            
            while (tokenizer.hasMoreTokens()) {
                moduleNameList.add(ModuleName.make(tokenizer.nextToken()));
            }
            
            return moduleNameList.toArray(new ModuleName[moduleNameList.size()]);
            
        } catch (IOException e) {
            status.add(new Status(Status.Severity.ERROR, "Cannot read the workspace spec " + specName + " in the Car " + car.getName(), null));
            return new ModuleName[0];
        }
    }
    
    /**
     * Adds the given stored module to the given stored module map, while updating the set of module names already seen.
     * @param storedModule the stored module to be added to the map.
     * @param workspaceDeclarationName the name of the workspace declaration currently being processed.
     * @param moduleNamesInCurrentDeclaration the set of module names that have already been processed - this set will be updated.
     * @param nameToStoredModuleMap the stored module map to which the given stored module is to be added.
     * @param moduleNameToWorkspaceDeclInfoMap a map containing the names of the modules included by the workspace declaration and
     *         their associated entries in all the workspace declarations in the tree. This will be populated by this method.
     * @param status the tracking status object.
     */
    private static void addStoredModuleToMapHelper(StoredVaultElement.Module storedModule, String workspaceDeclarationName, Set<ModuleName> moduleNamesInCurrentDeclaration, Map<ModuleName, StoredVaultElement.Module> nameToStoredModuleMap, Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToWorkspaceDeclInfoMap, Status status) {
        ModuleName moduleName = ModuleName.maybeMake(storedModule.getName());
        if (moduleName == null) {
            status.add(new Status(Status.Severity.ERROR, "The name " + storedModule.getName() + " is not a valid module name", null));
        }
        
        // Add to the stored module map if a stored module by that name doesn't already exist in the map.
        if (!nameToStoredModuleMap.containsKey(moduleName)) {
            nameToStoredModuleMap.put(moduleName, storedModule);
        }
        
        LinkedHashSet<Pair<String, VaultElementInfo>> workspaceDeclarationNamesForModule = moduleNameToWorkspaceDeclInfoMap.get(moduleName);
        if (workspaceDeclarationNamesForModule == null) {
            workspaceDeclarationNamesForModule = new LinkedHashSet<Pair<String, VaultElementInfo>>();
            moduleNameToWorkspaceDeclInfoMap.put(moduleName, workspaceDeclarationNamesForModule);
        }
        workspaceDeclarationNamesForModule.add(new Pair<String, VaultElementInfo>(workspaceDeclarationName, storedModule.getVaultInfo()));
        
        // Signal a warning if the same module appears twice in the same declaration.
        if (moduleNamesInCurrentDeclaration.contains(moduleName)) {
            status.add(new Status(Status.Severity.WARNING, "Module " + moduleName + " appears twice in declaration " + workspaceDeclarationName, null));
        }
        
        // Add to the set of module names in the current declaration.
        moduleNamesInCurrentDeclaration.add(moduleName);
    }
    
    /**
     * Lex a stream on a workspace declaration into lines of tokens.
     * @param workspaceDeclarationReader the reader on the workspace declaration.
     * @param status the tracking status object.
     * @return For each line which contains tokens, the line number and the tokens on that line.
     */
    private static List<Pair<Integer, List<String>>> lexWorkspaceDeclarationStream(Reader workspaceDeclarationReader, Status status) {

        //
        // Note: the StreamTokenizer has (among its many problems) a bug where setting the '/' character to a
        //  wordChar will cause it to fail to recognize slash-slash and slash-star style comments.
        //  Sun bug id: 4217381 (closed, not fixed..).
        //
        // This method tries to deal with this problem.
        //  The stream is tokenized with '/' as a wordChar, but with slashed comments unrecognized.
        //  Later, the list of tokens is processed again to remove the commented bits.
        //
        
        // Use a StreamTokenizer..
        StreamTokenizer st = new StreamTokenizer(workspaceDeclarationReader);

        // Reset the syntax.. we are creating our own.
        st.resetSyntax();
        
        // '!' is the first non-whitespace char..
        st.wordChars('!', 127);
        st.wordChars(128 + 32, 255);        // From the StreamTokenizer constructor.  What about 128 to 128 + 31?
        
        // ' ' is the last character which doesn't appear on the console.
        st.whitespaceChars(0, ' ');
        
        // Recognize quotes with single- and double-quotes.
        st.quoteChar('"');
        st.quoteChar('\'');

        // Recognize eol's.
        st.eolIsSignificant(true);
        
        // List of line # / token pairs.
        LinkedList<Pair<Integer, String>> lineNoTokenPairList = new LinkedList<Pair<Integer, String>>();
        
        try {
            boolean eof = false;
            while (!eof) {
                int tokType = st.nextToken();
                String token = null;
                
                switch (tokType) {
                    case StreamTokenizer.TT_WORD :
                        token = st.sval;
                        break;
                    case StreamTokenizer.TT_NUMBER :
                        // Shouldn't happen, but whatever.. we can handle this anyways.
                        token = String.valueOf(st.nval);
                        break;
                    case '\'':
                    case '\"':
                        // Add back quotes to mark as quoted, so that we can properly ignore quoted comment characters.
                        // We'll strip them back out again in the next phase.
                        token = '"' + st.sval + '"';
                        break;
                    case StreamTokenizer.TT_EOL :
                        // Ignore..
                        // Note: st.lineno() reports the next line, not the previous.
                        break;
    
                    case StreamTokenizer.TT_EOF :
                        eof = true;
                        break;
                        
                    default :
                        token = String.valueOf((char)st.ttype);
                        break;
                }
                
                // If there was a token, add it to the current list of tokens.
                //  If necessary, also update the current line number, and add the old line to the module location list.
                if (token != null) {
                    int lineNo = st.lineno();
                    lineNoTokenPairList.add(new Pair<Integer, String>(Integer.valueOf(lineNo), token));
                    
                }
            }
            
        } catch (IOException ioe) {
            // thrown by StreamTokenizer.nextToken();
            String errorString = "WorkspaceLoader: IOException reading the workspace file.";
            status.add(new Status(Status.Severity.ERROR, errorString, ioe));

            return null;
        }
        
        //
        // Second pass: strip out comments (slash-slash and slash-star).
        // This works around a bug where if the slash is set as a word character, slash-slash and slash-star
        //   comments are not properly stripped.
        //
        
        int lineCommentLine = -1;       // if >= 0, we're in a line comment for this line.
        boolean inBlockComment = false;
        
        // the same list, but without the commented bits.
        List<Pair<Integer, String>> lineNoRealTokenPairList = new ArrayList<Pair<Integer, String>>();
        
        while (!lineNoTokenPairList.isEmpty()) {
            // Remove the head of the list.
            Pair<Integer,String> lineNoTokenPair = lineNoTokenPairList.removeFirst();
            
            int lineNo = lineNoTokenPair.fst().intValue();
            String token = lineNoTokenPair.snd();

            // Handle the case where we are in a comment.
            if (lineCommentLine >= 0) {

                if (lineNo == lineCommentLine) {
                    // Keep consuming tokens on the same line.
                    continue;
                } else {
                    // A different line: no longer within a line comment.
                    lineCommentLine = -1;
                }

            } else if (inBlockComment) {
                int endBlockCommentIndex = token.indexOf(END_BLOCK_COMMENT_CHARS);
                if (endBlockCommentIndex < 0) {
                    // The comment does not end.
                    continue;
                }
                
                // The comment ends.  Just handle the remnant if any.
                inBlockComment = false;

                String tokenRemnant = token.substring(endBlockCommentIndex + END_BLOCK_COMMENT_CHARS.length());
                if (tokenRemnant.length() == 0) {
                    continue;
                }
                
                // Push the remnant back onto the head of the list.
                // Note that we can't just fall out of the loop, since the remnant may itself have comment characters.
                lineNoTokenPairList.addFirst(new Pair<Integer, String>(Integer.valueOf(lineNo), tokenRemnant));
                continue;
            }
            
            // If we are here, we are not currently in a comment.
            
            // Check for quoted string..
            if (token.startsWith("\"")) {
                // Un-quote the string.
                int tokenLength = token.length();
                if (token.endsWith("\"") && tokenLength > 1) {
                    token = token.substring(1, tokenLength - 1);
                }
                
                // Note that the empty string "" is passed as an empty token.
                
            } else {
                // Determine if the token contains characters to start a comment.
                
                int lineCommentCharsIndex = token.indexOf(LINE_COMMENT_CHARS);
                int blockCommentCharsIndex = token.indexOf(BEGIN_BLOCK_COMMENT_CHARS);
                
                // For the case of a comment, retain the bit that isn't part of a comment.
                if (lineCommentCharsIndex >= 0 && (blockCommentCharsIndex < 0 || lineCommentCharsIndex < blockCommentCharsIndex)) {
                    // We're in a line comment.
                    lineCommentLine = lineNo;
                    token = token.substring(0, lineCommentCharsIndex);
                    
                } else if (blockCommentCharsIndex >= 0) {
                    // We're in a block comment.
                    inBlockComment = true;
                    
                    // Push the remnant back onto the head of the list.
                    String tokenRemnant = token.substring(blockCommentCharsIndex + BEGIN_BLOCK_COMMENT_CHARS.length());
                    
                    if (tokenRemnant.length() != 0) {
                        lineNoTokenPairList.addFirst(new Pair<Integer, String>(Integer.valueOf(lineNo), tokenRemnant));
                    }
                    
                    // The token is the bit before the block comment chars.
                    token = token.substring(0, blockCommentCharsIndex);
                }
                
                if (token.length() == 0) {
                    continue;
                }
                            
            }
            
            lineNoRealTokenPairList.add(new Pair<Integer, String>(Integer.valueOf(lineNo), token));
        }

        //
        // Now that we have the list of tokens (as line # / token pairs), turn these into lines of tokens.
        //
        
        // Line number, Tokens from that line.
        List<Pair<Integer, List<String>>> moduleLocationList = new ArrayList<Pair<Integer, List<String>>>();

        List<String> currentLineTokens = new ArrayList<String>();
        int currentLineNo = -1;         // the current line number
        
        // Iterate over the processed list of tokens, populating the module location list
        for (final Pair<Integer, String> nextTokenPair : lineNoRealTokenPairList) {
            int lineNo = nextTokenPair.fst().intValue();
            String token = nextTokenPair.snd();
            
            // Add the token to the current list of tokens.
            //  If necessary, also update the current line number, and add the old line to the module location list.
            if (lineNo != currentLineNo) {
                if (currentLineNo > -1) {
                    moduleLocationList.add(new Pair<Integer, List<String>>(Integer.valueOf(currentLineNo), currentLineTokens));
                }
                currentLineTokens = new ArrayList<String>();
                currentLineNo = lineNo;
            }
            
            // Add to the list.
            currentLineTokens.add(token);

        }
        
        // Add the last line
        if (!currentLineTokens.isEmpty()) {
            moduleLocationList.add(new Pair<Integer, List<String>>(Integer.valueOf(currentLineNo), currentLineTokens));
        }
        
        return moduleLocationList;
    }
    
    /**
     * Convert a url to a uri.
     * @param url the url to convert.
     * @return the corresponding uri, or null if the conversion failed.
     */
    public static URI urlToUri(URL url) {
        if (url == null) {
            return null;
        }
        
        try {
            return new URI(url.getProtocol(), General.decodeUrlUtf8(url.getPath()), null);

        } catch (URISyntaxException e) {
            CALWorkspace.SERVICES_LOGGER.log(Level.SEVERE, "Error getting uri.", e);
        }

        return null;
    }
    
    /**
     * @return the workspace provider factory class.
     * The default provider factory will be returned, unless the provider is specified by the relevant system property.
     */
    static CALWorkspaceEnvironmentProvider.Factory getWorkspaceProviderFactory() {
        String providerFactoryPropertyString = System.getProperty(WorkspaceConfiguration.PROVIDER_FACTORY_PROPERTY);
        if (providerFactoryPropertyString != null && providerFactoryPropertyString.length() > 0) {
            try {
                // Find the class.
                Class<?> providerFactoryClass;
                try {
                    providerFactoryClass = ClassInfo.loadClass(providerFactoryPropertyString);
                
                } catch (ClassNotFoundException e) {
                    String message = "Class \"" + providerFactoryPropertyString + "\", specified as provider factory by the system property, was not found.";
                    throw new CALWorkspaceEnvironmentProvider.FactoryException(message, e);
                }
                
                // Instantiate the class.
                try {
                    return (CALWorkspaceEnvironmentProvider.Factory)providerFactoryClass.newInstance();
                
                } catch (ClassCastException e) {
                    String message = "Class \"" + providerFactoryPropertyString + "\", specified as provider factory by the system property, is not an instance of a provider factory.";
                    throw new CALWorkspaceEnvironmentProvider.FactoryException(message, e);
                } catch (InstantiationException e) {
                    String message = "Class \"" + providerFactoryPropertyString + "\", specified as provider factory by the system property, is abstract.";
                    throw new CALWorkspaceEnvironmentProvider.FactoryException(message, e);
                } catch (IllegalAccessException e) {
                    String message = "Class \"" + providerFactoryPropertyString + "\", specified as provider factory by the system property, is not accessible.";
                    throw new CALWorkspaceEnvironmentProvider.FactoryException(message, e);
                }
            } catch (CALWorkspaceEnvironmentProvider.FactoryException e) {
                // What to do?
                e.printStackTrace();
            }
        }

        // The default provider factory provides BasicCALWorkspaces.
        return BasicCALWorkspace.PROVIDER_FACTORY;
    }

}
