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
 * ModuleContainer.
 * Created: Feb 21, 2007
 * By: Greg McClement
 */

package org.openquark.cal.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.ResourceManager;
import org.openquark.cal.services.Status;
import org.openquark.util.Pair;

/**
 * Objects that implement this class are needed to initialize the SourceMetricsManager. This is 
 * the only external dependency of the source metrics manager.
 * 
 * @author GMcClement
 */
public abstract class ModuleContainer {
    /**
     * @return the number of modules in the container.
     */
    public abstract int getNModules();

    /**
     * Returns the module type info at the position specified by the given index.
     * @param i the index of the module type info to return.
     * @return the specified module type info.
     */
    public abstract ModuleTypeInfo getNthModuleTypeInfo(int i);

    /**
     * Returns the module type info of the module with the given name.
     * @param moduleName the name of a module.
     * @return the corresponding module type info.
     */
    public abstract ModuleTypeInfo getModuleTypeInfo(ModuleName moduleName);

    /**
     * Returns the source definition of the module with the given name.
     * @param moduleName the name of a module.
     * @return the corresponding module source definition.
     */
    public abstract ModuleSourceDefinition getSourceDefinition(ModuleName moduleName);

    public boolean containsModule(ModuleName name){
        return getModuleTypeInfo(name) != null;
    }

    public abstract ResourceManager getResourceManager(ModuleName moduleName, String resourceType);
    
    public abstract boolean saveMetadata(CALFeatureMetadata metadata, Status saveStatus);
    
    public abstract boolean renameFeature(CALFeatureName oldFeatureName, CALFeatureName newFeatureName, Status renameStatus);

    /**
     * Use to manage updates of the contents of a module.
     * @author GMCCLEMENT
     */
    public interface ISourceManager{
        public boolean isWriteable(ModuleName moduleName);
        // may return null for the string if not available
        public String getSource(ModuleName moduleName, CompilerMessageLogger messageLogger);
        public boolean saveSource(final ModuleName moduleName, final String moduleDefinition, Status saveStatus);
    }

    // These are optional methods
    /**
     * Used for more precise changes to the source. 
     * 
     * @author Greg McClement
     */
    public interface ISourceManager2{
        /**
         * Check if the saveSource and getOffset members can be used with the given module.
         * @return true if the given module can be updated incrementally.
         */
        public boolean canUpdateIncrementally(ModuleName moduleName);
        
        /**
         * Update the module contents with the given string.
         * @return true if the module was successfully updated.
         */
        public boolean saveSource(final ModuleName moduleName, final int startIndex, final int endIndex, final String newText);
        
        /**
         * @return The offset of the given position in the given module.
         */
        public int getOffset(final ModuleName name, final SourcePosition position);
        
        /**
         * @param name name of the module
         * @param offset the offset into the module
         * @return the line and column in the document corresponding to the given offset. 
         * These are one based. This will return null if the offset is not in range.
         */
        public Pair<Integer, Integer> getLineAndColumn(final ModuleName name, final int offset);
        
        /**
         * This is used to support undo. All of the operations 
         * after start up will treated
         * as a semantic group.
         */        
        public void startUpdate(ModuleName moduleName);
        
        /**
         * This is used to support undo. All of the operations before this is called
         * will be considered to be part of a semantic group.
         */
        public void endUpdate(ModuleName moduleName);
    }
    
    public abstract ISourceManager getSourceManager(ModuleName moduleName);
    
    /**
     * Return the source model for the given module name
     * @param moduleName the name of the module.
     * @param ignoreErrors return the source model even if there are parse errors.
     * @return the source model for the given module. Maybe be null if the file is unparsable.
     */
    public SourceModel.ModuleDefn getSourceModel(ModuleName moduleName, boolean ignoreErrors, CompilerMessageLogger logger){
        String moduleSource = getModuleSource(moduleName);
        if (moduleSource.length() == 0){
            return null;
        }
        return SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(moduleSource, ignoreErrors, logger);
    }
    
    public String getModuleSource(ModuleName moduleName){
        return ModuleContainer.readModuleSource(getSourceDefinition(moduleName));
    }
    
    public ModuleName[] getModuleNames(){
        final int nModules = getNModules(); 
        ModuleName[] moduleNames = new ModuleName[nModules];
        for(int i = 0; i < nModules; ++i){
            moduleNames[i] = getNthModuleTypeInfo(i).getModuleName();
        }
        return moduleNames;
    }
    
    /**
     * @param moduleSourceDefn The source definition to read the source text for
     * @return The source text of a module as a single String
     */
    public static String readModuleSource(ModuleSourceDefinition moduleSourceDefn) {

        Reader reader = moduleSourceDefn.getSourceReader(new org.openquark.cal.services.Status("reading module source"));
        if (reader == null) {
            System.err.println("Failed to read module " + moduleSourceDefn.getModuleName());
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(reader);

        try {
            StringBuilder stringBuf = new StringBuilder();
            String line = null;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuf.append(line).append('\n');
                }
            } catch (IOException e) {
                System.err.println("Failed to read module " + moduleSourceDefn.getModuleName());
                return null;
            }
            
            return stringBuf.toString(); 
            
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
            }
        }
    }

}