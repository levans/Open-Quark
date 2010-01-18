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
 * Perspective.java
 * Creation date: Oct 3, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeConstructor;


/**
 * A perspective defines a working context within a workspace.
 * With a Perspective, one may specify a working module (to define visibility) and, optionally,
 * a view policy that affects the visibility of entities.
 * Creation date: Oct 3, 2002.
 * @author Edward Lam
 */
public final class Perspective {

    /** The name of the current working module, providing the perspective on the program.  */
    private ModuleName workingModuleName;

    /** The workspace on which the perspective is based. */
    private final CALWorkspace workspace;

//    TypeAnalyzer typeAnalyzer;

    /** A GemViewer to apply to the perspective.
      * If this is null, the perspective is not viewed through a viewer. */
    private GemViewer gemEntityViewer = null;
    
    /**
     * Constructor for a perspective.
     * @param workspace the workspace for which this perspective will apply.
     * @param workingModule the initial working module, or null if there are no modules..
     */
    public Perspective(CALWorkspace workspace, MetaModule workingModule) {

        Assert.isNotNullArgument(workspace, "workspace");
        
        if (workspace.getNMetaModules() != 0) {
            Assert.isNotNullArgument(workingModule, "workingModule");
        }

        this.workspace = workspace;
        this.workingModuleName = workingModule == null ? null : workingModule.getName();
    }
    
    /**
     * Set the working module.
     * @param newModuleName the name of the new working module.
     */
    public void setWorkingModule(ModuleName newModuleName) {

        Assert.isNotNullArgument(newModuleName, "newModuleName");

        // check that the module exists.
        MetaModule newWorkingModule = workspace.getMetaModule(newModuleName);
        if (newWorkingModule == null) {
            throw new IllegalArgumentException("Module " + newModuleName + " is not a member of the workspace.");
        }

        this.workingModuleName = newModuleName;
    }

    /**
     * Get the working module.
     * @return MetaModule the working module, or null if the working module no longer exists in the current perspective.
     */
    public MetaModule getWorkingModule() {
        return workspace.getMetaModule(workingModuleName);
    }

    /**
     * Get the name of the current working module.
     *   Note that the module with this name may not exist in the workspace if the module was removed and the perspective was not updated.
     * @return the name of the current working module.
     */
    public ModuleName getWorkingModuleName() {
        return workingModuleName;
    }
    
    /**
     * @return type info about the current working module.
     */
    public ModuleTypeInfo getWorkingModuleTypeInfo() {
        MetaModule metaModule = getWorkingModule();
        return metaModule == null ? null : metaModule.getTypeInfo();
    }
    
    /**
     * Get the CALWorkspace represented by this Perspective.
     * @return CALWorkspace
     */
    public CALWorkspace getWorkspace() {
        return workspace;
    }
    
    /**
     * @param moduleName the name of the module
     * @return the MetaModule with the given module name or null if there is no module with that name
     */
    public MetaModule getMetaModule(ModuleName moduleName) {
        
        int numModules = workspace.getNMetaModules();
        for (int n = 0; n < numModules; n++) {
            MetaModule module = workspace.getNthMetaModule(n);
            
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }
        
        return null;
    }

    /**
     * Get the number of visible modules.
     * @return int
     */
    public int getNVisibleMetaModules() {
        MetaModule workingModule = getWorkingModule();
        if (workingModule == null) {
            return 0;
        }
        
        // it's the number of imported modules, plus one for the working module itself
        return workingModule.getNImportedModules() + 1;
    }

    /**
     * Get the nth visible module.
     *   The first (n-1) visible modules correspond to the nth imported module in the corresponding ModuleTypeInfo.
     *   The nth module will be the working module itself.
     * @param n 0 based module index
     * @return MetaModule
     */
    public MetaModule getVisibleMetaModule(int n) {

        int numVisibleModules = getNVisibleMetaModules();
        if (n < 0 || n >= numVisibleModules) {
            throw new IndexOutOfBoundsException("Must be between 0 and " + numVisibleModules);
        }

        MetaModule workingModule = getWorkingModule();
        if (n == numVisibleModules - 1) {
            return workingModule;
        }

        if (workingModule == null) {
            return null;
        }

        return workingModule.getNthImportedModule(workspace, n);
    }

    /**
     * Get the modules in the current workspace which are not visible from the current perspective.
     * @return the MetaModules not visible from the current perspective.
     */
    public List<MetaModule> getInvisibleMetaModules() {
        List<MetaModule> moduleList = new ArrayList<MetaModule>();
        int nModules = workspace.getNMetaModules();
        for (int i = 0; i < nModules; i++) {
            MetaModule nthMetaModule = workspace.getNthMetaModule(i);
            if (!isVisibleModule(nthMetaModule.getName())) {
                moduleList.add(nthMetaModule);
            }
        }
        return moduleList;
    }

    /**
     * Get the modules in the current workspace which are visible from the current perspective.
     * @return the MetaModules visible from the current perspective
     */
    public List<MetaModule> getVisibleMetaModules() {
        List<MetaModule> moduleList = new ArrayList<MetaModule>();
        int nModules = getNVisibleMetaModules();
        for (int i = 0; i < nModules; i++) {
            moduleList.add(getVisibleMetaModule(i));
        }
        return moduleList;
    }

    /**
     * Get all visible entities in a workspace.
     * Visibility is determined by the current working module.
     * @return the set of visible entities.
     */
    public Set<GemEntity> getVisibleGemEntities() {

        Set<GemEntity> visibleEntitySet = new LinkedHashSet<GemEntity>();

        // add all visible entities from visible modules
        int nVisibleModules = getNVisibleMetaModules();
        for (int i = 0; i < nVisibleModules; i++) {
            MetaModule nthModule = getVisibleMetaModule(i);
            visibleEntitySet.addAll(getVisibleGemEntities(nthModule));
        }

        // Apply the view policy if any.
        if (gemEntityViewer != null) {
            visibleEntitySet = new LinkedHashSet<GemEntity>(gemEntityViewer.view(visibleEntitySet));
        }
        return visibleEntitySet;
    }

    /**
     * Get the visible entities in a module.
     * Visibility is determined by the current working module.
     * @param module the module in which to look.
     * @return the set of visible entities.
     */
    public Set<GemEntity> getVisibleGemEntities(MetaModule module) {
        
        // Check that module != null
        Assert.isNotNullArgument(module, "module");

        int nEntities = module.getNGemEntities();
        ModuleTypeInfo workingModuleTypeInfo = getWorkingModuleTypeInfo();

        // Add all entities from the working module, or only public entities from non-working modules.
        Set<GemEntity> visibleEntitySet = new LinkedHashSet<GemEntity>();
        for (int i = 0; i < nEntities; i++) {

            GemEntity gemEntity = module.getNthGemEntity(i);
            
            if (workingModuleTypeInfo.isEntityVisible(gemEntity.getFunctionalAgent())) {
                visibleEntitySet.add(gemEntity);
            }
        }

        // Apply the view policy if any.
        if (gemEntityViewer != null) {
            visibleEntitySet = new LinkedHashSet<GemEntity>(gemEntityViewer.view(visibleEntitySet));
        }

        return visibleEntitySet;
    }

    /**
     * Get a given visible GemEntity.
     * Visibility is determined by the current working module.
     * @param entityName the entity's name
     * @return the entity, or null if not visible or it doesn't exist.
     */
    public GemEntity getVisibleGemEntity(QualifiedName entityName) {
        
        GemEntity gemEntity = workspace.getGemEntity(entityName);
        if (gemEntity == null) {
            return null;
        }
        
        // check for visibility.
        if (isVisibleModule(entityName.getModuleName())) {
            return gemEntity;
        }
        
        // not visible
        return null;
    }
    
    /**
     * Gets the type constructor with the given name if it is visible from the current module.
     * @param typeConstructorName the name of the type constructor.
     * @return the type constructor, or null if the given type constructor is not visible.
     */
    public TypeConstructor getTypeConstructor(QualifiedName typeConstructorName) {
        MetaModule workingModule = getWorkingModule();
        return workingModule == null ? null : workingModule.getTypeInfo().getVisibleTypeConstructor(typeConstructorName);
    }
    
    /**
     * Returns whether a given name is the name of a visible module.
     * @param moduleName
     * @return boolean
     */
    public boolean isVisibleModule(ModuleName moduleName) {

        MetaModule workingModule = getWorkingModule();
        if (workingModule == null) {
            return false;
        }
        if (workingModule.getName().equals(moduleName)) {
            return true;
        }
        
        return workingModule.getTypeInfo().getImportedModule(moduleName) != null;
    }

    /**
     * Set the new entity viewer for this perspective.
     * @param newViewer the new viewer, or null to reset to the default (do-not-alter) policy.
     */
    public void setGemEntityViewer(GemViewer newViewer) {
        this.gemEntityViewer = newViewer;
    }

    /**
     * Obtain the entity for this function (supercombinator or class method).
     * Returns null if the entity is not found or if the resolution is ambiguous according
     * to the scoping rules for an unqualified function in CAL defined in the current module.
     *
     * @param unqualifiedName the name of the entity
     * @return the corresponding entity, or null.
     */
    public GemEntity resolveGemEntity(String unqualifiedName) {
        Assert.isNotNullArgument(unqualifiedName, "unqualifiedName");

        MetaModule workingModule = getWorkingModule();
        if (workingModule == null) {
            return null;
        }

        // check if the unqualifiedName can be resolved in the current module
        GemEntity gemEntity = workingModule.getGemEntity(unqualifiedName);
        if (gemEntity != null) {
            return gemEntity;
        }

        int foundIndex = -1;
        int nImportedModules = workingModule.getNImportedModules();
        
        ModuleTypeInfo workingModuleTypeInfo = workingModule.getTypeInfo();

        for (int i = 0; i < nImportedModules; ++i) {

            gemEntity = workingModule.getNthImportedModule(workspace, i).getGemEntity(unqualifiedName);
            if (gemEntity != null && workingModuleTypeInfo.isEntityVisible(gemEntity.getFunctionalAgent())) {
                foundIndex = i;
                break;
            }
        }

        //Check for an ambiguous import
        if (foundIndex != -1) {

            for (int i = foundIndex + 1; i < nImportedModules; ++i) {

                GemEntity otherEntity = workingModule.getNthImportedModule(workspace, i).getGemEntity(unqualifiedName);
                if (otherEntity != null && workingModuleTypeInfo.isEntityVisible(otherEntity.getFunctionalAgent())) {
                    //ambigious symbol resolution
                    return null;
                }
            }
        }

        return gemEntity;
    }

    /**
     * Obtain the entity for this function (supercombinator or class method), which
     * may be an ambiguous import (that is, an entity with the specified unqualified
     * name may be contained in more than one imported module).
     * 
     * Returns null if the entity is not found
     *
     * @param unqualifiedName the name of the entity
     * @return GemEntity
     */
    public GemEntity resolveAmbiguousGemEntity(String unqualifiedName) {
        Assert.isNotNullArgument(unqualifiedName, "unqualifiedName");

        MetaModule workingModule = getWorkingModule();
        if (workingModule == null) {
            return null;
        }

        // check if the unqualifiedName can be resolved in the current module
        GemEntity gemEntity = workingModule.getGemEntity(unqualifiedName);
        if (gemEntity != null) {
            return gemEntity;
        }

        // check if the unqualifiedName can be resolved in imported modules
        int nImportedModules = workingModule.getNImportedModules();
        ModuleTypeInfo workingModuleTypeInfo = workingModule.getTypeInfo();
        for (int i = 0; i < nImportedModules; ++i) {

            gemEntity = workingModule.getNthImportedModule(workspace, i).getGemEntity(unqualifiedName);
            if (gemEntity != null && workingModuleTypeInfo.isEntityVisible(gemEntity.getFunctionalAgent())) {
                break;
            }
        }

        return gemEntity;
    }

    /**
     * Find out whether a given type constructor is an Enum type.
     * What this means is that the type is an algebraic data type (introduced by a non-foreign data declaration),
     * all data constructors have arity 0, and there is at least one data constructor.
     * @param typeConstructorName the name of the type constructor.
     * @return true if the type constructor is a visible enumerated type.
     */
    public boolean isEnumDataType(QualifiedName typeConstructorName) {
        MetaModule workingModule = getWorkingModule();
        return workingModule == null ? false : workingModule.isEnumDataType(typeConstructorName);
    }
    
    /**
     * @param typeConstructorName the name of the type constructor
     * @return the array with all visible data constructors for the given type constructor.
     * Will be null if the type constructor is not visible or zero length if there are no
     * visible data constructors.
     */
    public DataConstructor[] getDataConstructorsForType(QualifiedName typeConstructorName) {
        MetaModule workingModule = getWorkingModule();
        return workingModule == null ? null : workingModule.getDataConstructorsForType(typeConstructorName);
    }
    
    /**
     * @return all type constructor entities visible from the current module. Null if there is no current module.
     */
    public TypeConstructor[] getTypeConstructors() {
        MetaModule workingModule = getWorkingModule();
        return workingModule == null ? null : workingModule.getTypeConstructors();
    }

    /**
     * GemCutter: every edit.  Errors only shown for working module
     * Not implemented
     * @param gemEntity
     */
    void saveEntityToWorkingModule(GemEntity gemEntity) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Not implemented
     * @param entity
     */
    void deleteEntityFromWorkingModule(GemEntity entity) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Not implemented
     */
    void refreshWorkingModuleFromWorkspace() {
        throw new UnsupportedOperationException("not implemented");
    }

}
