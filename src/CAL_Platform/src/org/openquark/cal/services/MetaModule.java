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
 * MetaModule.java
 * Creation date: Oct 2, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.machine.Module;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.cal.metadata.ModuleMetadata;


/**
 * A metamodule encapsulates a module and all related metadata, including source (if any).
 * @author Edward Lam
 */
public class MetaModule {

    /** the compiled machine-specific module code */
    private final Module module;
    
    /** Map from entity name to the entities held by this module */
    private final Map<String, GemEntity> nameToEntityMap;
    
    /** List of EnvEntities in this module, in the order that they were added. */
    private final List<GemEntity> gemEntityList;

    /** The manager to manage resources for this module. */
    private final VirtualResourceManager virtualResourceManager;

    /**
     * Constructor for a MetaModule.
     * @param module the compiled machine-specific module code.
     * @param virtualResourceManager the resource manager for the workspace.
     */
    MetaModule(Module module, VirtualResourceManager virtualResourceManager) {
        this.module = module;
        this.virtualResourceManager = virtualResourceManager;

        nameToEntityMap = new HashMap<String, GemEntity>();
        gemEntityList = new ArrayList<GemEntity>();

        // Populate entity map and list from the module.
        ModuleTypeInfo typeInfo = module.getModuleTypeInfo();

        if (typeInfo != null) {
    
            FunctionalAgent[] entities = typeInfo.getFunctionalAgents();
    
            for (int i = 0; i < entities.length; ++i) {
                FunctionalAgent envEntity = entities [i];
                addGemEntity(new GemEntity(envEntity, virtualResourceManager));
            }
        }
    }
    
    /**
     * Add an entity to this module.
     * @param gemEntity the entity to add.
     */
    public void addGemEntity(GemEntity gemEntity) {

        QualifiedName entityName = gemEntity.getName();

        if (!entityName.getModuleName().equals(getName())) {
            throw new IllegalArgumentException("Entity must be a member of module: " + getName());
        }

        GemEntity oldValue = nameToEntityMap.put(gemEntity.getName().getUnqualifiedName(), gemEntity);
        if (oldValue == null) {
            gemEntityList.add(gemEntity);

        } else {
            // Replacing an old value
            int oldValueIndex = gemEntityList.indexOf(oldValue);
            gemEntityList.set(oldValueIndex, gemEntity);
        }
    }
    
    /**
     * Get the name of the module.
     * @return the name of the module
     */
    public ModuleName getName() {
        return module.getName();
    }

    /**
     * @return the type info for this module.
     */
    public ModuleTypeInfo getTypeInfo() {
        return module.getModuleTypeInfo();
    }

    /**
     * @return the module associated with this MetaModule.
     */
    public Module getModule(){
        return module;
    }
    
    /**
     * Get an entity
     * @param entityName the unqualified name of the entity
     * @return the corresponding entity, or null if the entity does not exist in this module.
     */
    public GemEntity getGemEntity(String entityName) {
        return nameToEntityMap.get(entityName);
    }
    
    /**
     * Get the number of entities in this module
     * @return int the number of entities in this module.
     */
    public int getNGemEntities() {
        return gemEntityList.size();
    }

    /**
     * Get the nth entity in this module.
     * @param n 0-based index of the entity to get
     * @return the nth entity in this module.
     */
    public GemEntity getNthGemEntity(int n) {
        return gemEntityList.get(n);
    }

    /**
     * Method getImportedModule.
     * @param workspace
     * @param importedModuleName
     * @return ModuleTypeInfo the imported module or null if the module does not import a module with the given name.
     */
    public MetaModule getImportedModule(CALWorkspace workspace, ModuleName importedModuleName) {
        ModuleTypeInfo importedTypeInfo = module.getModuleTypeInfo().getImportedModule(importedModuleName);
        return importedTypeInfo == null ? null : workspace.getMetaModule(importedModuleName);
    }
    
    /**
     * Method getNImportedModules.
     * @return int the number of modules directly imported into this module.
     */
    public int getNImportedModules() {
        return module.getModuleTypeInfo().getNImportedModules();
    }
           
    /**
     * Method getNthImportedModule.
     * @param workspace
     * @param n zero based index
     * @return ModuleTypeInfo the nth module imported by this module
     */
    public MetaModule getNthImportedModule(CALWorkspace workspace, int n) {
        return workspace.getMetaModule(module.getModuleTypeInfo().getNthImportedModule(n).getModuleName());
    }

    /**
     * @param typeConstructorName the name of the type constructor
     * @return An array with all visible data constructors for the type constructor.
     * This will be null if the type constructor is not visible or zero length if it has
     * no visible data constructors.
     */
    public DataConstructor[] getDataConstructorsForType(QualifiedName typeConstructorName) {

        ModuleTypeInfo currentModuleTypeInfo = module.getModuleTypeInfo();
        TypeConstructor typeCons = currentModuleTypeInfo.getVisibleTypeConstructor(typeConstructorName);

        if (typeCons == null) {
            return null;
        }
        
        List<DataConstructor> dataConsList = new ArrayList<DataConstructor>();
                       
        for (int n = 0, numDataCons = typeCons.getNDataConstructors(); n < numDataCons; n++) {
            
            DataConstructor dataCons = typeCons.getNthDataConstructor(n);
            
            if (currentModuleTypeInfo.isEntityVisible(dataCons)) {
                dataConsList.add(dataCons);
            }
        }
        
        return dataConsList.toArray(new DataConstructor[0]);
    }

    /**
     * Determines all type constructors visible from this module and returns their
     * type constructor entities. The returned array will be zero-length if there are no
     * visible types.
     * @return an array of all visible type constructors
     */
    public TypeConstructor[] getTypeConstructors() {

        List<TypeConstructor> types = new ArrayList<TypeConstructor>();
        
        ModuleTypeInfo moduleTypeInfo = getTypeInfo();
        
        for (int n = 0, numTypes = moduleTypeInfo.getNTypeConstructors(); n < numTypes; n++) {
            types.add(moduleTypeInfo.getNthTypeConstructor(n));
        }

        // check for types in the imported modules
        
        for (int n = 0, numImports = moduleTypeInfo.getNImportedModules(); n < numImports; n++) {
            
            ModuleTypeInfo importedInfo = moduleTypeInfo.getNthImportedModule(n);

            for (int c = 0, numTypes = importedInfo.getNTypeConstructors(); c < numTypes; c++) {
                
                TypeConstructor typeCons = importedInfo.getNthTypeConstructor(c);
                
                if (moduleTypeInfo.isEntityVisible(typeCons)) {
                    types.add(typeCons);
                }
            }
        }
        
        return types.toArray(new TypeConstructor[0]);
    }
    
    /**
     * Find out whether a given type constructor is an Enum type with respect to this module.
     * What this means is that the type is an algebraic data type (introduced by a non-foreign data declaration),
     * all data constructors have arity 0, and there is at least one data constructor.
     * @param typeConstructorName the name of the type constructor
     * @return boolean True if the type constructor exists and is an "enum" (ie. all data constructors take 0 arguments).
     * False otherwise.
     */
    public boolean isEnumDataType(QualifiedName typeConstructorName) {
               
        ModuleTypeInfo currentModuleTypeInfo = module.getModuleTypeInfo();
        TypeConstructor typeCons = currentModuleTypeInfo.getVisibleTypeConstructor(typeConstructorName);
        if (typeCons == null) {
            return false;
        }
            
        int nDataCons = typeCons.getNDataConstructors();
        if (nDataCons == 0) {
            return false;
        }

        boolean hasVisibleConstructor = false;

        for (int i = 0; i < nDataCons; ++i) {
            
            DataConstructor dataCons = typeCons.getNthDataConstructor(i);

            if (currentModuleTypeInfo.isEntityVisible(dataCons)) {
  
                int arity = dataCons.getArity();
                if (arity > 0) {
                    return false;
                }
                            
                hasVisibleConstructor = true;
            }
         }
        
        return hasVisibleConstructor;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        // todo: add entities.
        return "Metamodule " + getName() + "\nModule: " + module;
    }

    /**
     * @param locale the locale associated with the metadata.
     * @return the metadata for the module. If the module has no metadata, then default metadata is returned.
     */
    public ModuleMetadata getMetadata(Locale locale) {
        MetadataManager metadataManager = virtualResourceManager.getMetadataManager(getName());
        if (metadataManager == null) {
            return MetadataManager.getEmptyMetadata(this, locale);
        }
        return metadataManager.getMetadata(this, locale);
    }
    
    /**
     * @param featureName the name of a feature in this module.
     * @param locale the locale associated with the metadata.
     * @return the metadata for the feature. If the feature has no metadata, then default metadata is returned.
     */
    public CALFeatureMetadata getMetadata(CALFeatureName featureName, Locale locale) {
        if (!(featureName.hasModuleName() && featureName.toModuleName().equals(getName()))) {
            throw new IllegalArgumentException("Feature must be a member of module: " + getName());
        }

        MetadataManager metadataManager = virtualResourceManager.getMetadataManager(getName());
        if (metadataManager == null) {
            return MetadataManager.getEmptyMetadata(featureName, locale);
        }
        
        return metadataManager.getMetadata(featureName, locale);
    }
    
    /**
     * Returns a List of ResourceNames for the metadata resources associated with the given feature name, across all locales.
     * @param featureName the name of the feature whose metadata resources across all locales are to be enumerated.
     * @return a List of ResourceNames, one for each localized metadata resource associated with the given feature name.
     */
    public List<ResourceName> getMetadataResourceNamesForAllLocales(CALFeatureName featureName) {
        if (!(featureName.hasModuleName() && featureName.toModuleName().equals(getName()))) {
            throw new IllegalArgumentException("Feature must be a member of module: " + getName());
        }

        MetadataManager metadataManager = virtualResourceManager.getMetadataManager(getName());
        if (metadataManager == null) {
            return Collections.emptyList();
        }
        
        return metadataManager.getMetadataResourceNamesForAllLocales(featureName);
    }
    
    /**
     * Get the features associated with this metamodule.
     * @return the names of features associated with this module.
     */
    public Set<CALFeatureName> getFeatureNames() {
        // CALFeatureNames for all features in the current program.
        Set<CALFeatureName> programFeatureNameSet = new HashSet<CALFeatureName>();
        
        // Grab the module names and feature names.
        ModuleTypeInfo moduleInfo = module.getModuleTypeInfo();
        
        // Module name.
        programFeatureNameSet.add(CALFeatureName.getModuleFeatureName(this));
        
        // Functional agents.
        FunctionalAgent[] entities = moduleInfo.getFunctionalAgents();
        for (final FunctionalAgent entity : entities) {
            programFeatureNameSet.add(CALFeatureName.getScopedEntityFeatureName(entity));
        }
        
        // Type constructors.
        int constructorCount = moduleInfo.getNTypeConstructors();
        for (int i = 0; i < constructorCount; i++) {
            
            TypeConstructor typeConstructor = moduleInfo.getNthTypeConstructor(i);
            programFeatureNameSet.add(CALFeatureName.getScopedEntityFeatureName(typeConstructor));
            
            // Data constructors.
            int dataConstructorCount = typeConstructor.getNDataConstructors();
            for (int n = 0; n < dataConstructorCount; n++) {
                DataConstructor dataConstructor = typeConstructor.getNthDataConstructor(n);
                programFeatureNameSet.add(CALFeatureName.getScopedEntityFeatureName(dataConstructor));
            }
        }
        
        
        // Type classes.
        int classCount = moduleInfo.getNTypeClasses();
        for (int i = 0; i < classCount; i++) {
            
            TypeClass typeClass = moduleInfo.getNthTypeClass(i);
            programFeatureNameSet.add(CALFeatureName.getScopedEntityFeatureName(typeClass));
            
            // Class methods.
            int methodCount = typeClass.getNClassMethods();
            for (int n = 0; n < methodCount; n++) {
                programFeatureNameSet.add(CALFeatureName.getScopedEntityFeatureName(typeClass.getNthClassMethod(n)));
            }
        }
        
        // Class instances.
        int instanceCount = moduleInfo.getNClassInstances();
        for (int n = 0; n < instanceCount; n++) {
            
            ClassInstance instance = moduleInfo.getNthClassInstance(n);
            programFeatureNameSet.add(CALFeatureName.getClassInstanceFeatureName(instance));
            
            // Instance methods.
            TypeClass typeClass = instance.getTypeClass();
            int methodCount = typeClass.getNClassMethods();
            for (int k = 0; k < methodCount; k++) {
                String methodName = typeClass.getNthClassMethod(k).getName().getUnqualifiedName();
                programFeatureNameSet.add(CALFeatureName.getInstanceMethodFeatureName(instance, methodName));
            }
        }
        
        return programFeatureNameSet;
    }

}

