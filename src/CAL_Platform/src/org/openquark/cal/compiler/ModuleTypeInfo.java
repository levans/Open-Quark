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
 * ModuleTypeInfo.java
 * Creation date: (June 6, 2001)
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;
import org.openquark.cal.machine.Module;
import org.openquark.cal.util.ArrayMap;
import org.openquark.cal.util.ArraySet;


/**
 * Provides information about the top-level entities defined by the module,
 * for use by external clients or dependent modules.
 * 
 * Note that ModuleTypeInfo is immutable with respect to external clients.
 *
 * Creation date: (June 6, 2001)
 * @author Bo Ilic
 */
public final class ModuleTypeInfo {
   
    private static final int serializationSchema = 0; 

    /** the name of this module. */
    private final ModuleName moduleName;

    /**
     * (ModuleName) the modules that are friends of this Module. Friend modules are able to access protected elements of
     * this module. 
     */
    private final ArraySet<ModuleName> friendModulesSet;

    /** 
     * (ModuleName->ModuleTypeInfo) a map from the names of the modules imported by this module to their ModuleTypeInfo objects.
     *  Ordered by import order within the CAL source of the module.
     */
    private final ArrayMap<ModuleName, ModuleTypeInfo> importedModuleMap;

    /**
     * (String -> ModuleName) map from an external function or class method's name to the name of its defining module.
     * The function or class method must be visible within this module (i.e. it must be public, and the
     * module in which it is defined must be imported in the current module).
     * These are the external function and class methods that can be used in an unqualified way within this module.
     */
    private final Map<String, ModuleName> usingFunctionOrClassMethodMap;

    /**
     * (String -> ModuleName) map from an external data constructor name to the name of its defining module.
     * The data constructor must be visible within this module (i.e. it must be public, and the
     * module in which it is defined must be imported in the current module).
     * These are the external data constructors that can be used in an unqualified way within this module.
     */    
    private final Map<String, ModuleName> usingDataConstructorMap;

    /**
     * (String -> ModuleName) map from an external type constructor name to the name of its defining module.
     * The type constructor must be visible within this module (i.e. it must be public, and the
     * module in which it is defined must be imported in the current module).
     * These are the external type constructors that can be used in an unqualified way within this module.
     */    
    private final Map<String, ModuleName> usingTypeConstructorMap;

    /**
     * (String -> ModuleName) map from an external type class's name to the name of its defining module.
     * The type class must be visible within this module (i.e. it must be public, and the
     * module in which it is defined must be imported in the current module).
     * These are the external type classes that can be used in an unqualified way within this module.
     */    
    private final Map<String, ModuleName> usingTypeClassMap;


    /**
     * (String->TypeConstructor) a map from the names of the type constructors defined in the module to their entity objects.
     * Ordered by definition order within the CAL source of the module.
     */
    private final ArrayMap<String, ScopedEntity> typeConstructorMap;

    /**
     * (String->TypeClass) a map from the names of the type classes defined in the module to their TypeClass objects.
     * Ordered by definition order within the CAL source of the module.
     */
    private final ArrayMap<String, ScopedEntity> typeClassMap; 

    /**
     * (ClassInstanceIdentifier->ClassInstance) a map from the names of the class instances defined in this module to their ClassInstance objects.
     * Ordered by definition order within the CAL source of the module.
     */
    private final ArrayMap<ClassInstanceIdentifier, ClassInstance> classInstanceMap;   

    /**
     * (String->Function) a map from the names of the functions defined in this module to their entity objects.
     * Ordered by definition order within the CAL source of the module.
     */
    private final ArrayMap<String, ScopedEntity> functionMap;

    /**
     * (String->ClassMethod) A cache used to quickly look up class methods. 
     * Type classes hold onto their class methods.
     */  
    private final transient Map<String, ClassMethod> cachedClassMethodsMap;

    /**
     * (String->DataConstructor) A cache used to quickly look up data constructors. 
     * Type constructors hold onto their data constructors.
     */
    private final transient Map<String, DataConstructor> cachedDataConstructorsMap;      

    /** (ModuleName->ModuleTypeInfo) Map from module name to module type info for direct and indirect dependees of this module,
     *  not including this module. */
    private final transient Map<ModuleName, ModuleTypeInfo> cachedDependeeModuleTypeInfoMap;

    /** A base instance of ModuleTypeInfo to extend. This is used for extension modules. */
    private final ModuleTypeInfo baseTypeInfo;

    /** A reference to the containing Module object. */
    private final Module module;

    /** Source metrics associated with this module */
    private ModuleSourceMetrics moduleSourceMetrics; 

    /** Set of QualifiedNames from imported modules that occur in this module */
    private Set<QualifiedName> importedNameOccurrences;

    /** The CALDoc comment for this module, or null if there is none. */
    private CALDocComment calDocComment;
    
    /** The module name resolver for resolving module names in the context of this module. */    
    private ModuleNameResolver moduleNameResolver;    

    /**
     * This constructor is for internal CAL compiler use only.
     * 
     * Constructor to use for non-extension modules.
     * @param moduleName the name of the module. Cannot be null.
     * @param module back pointer to the module. Cannot be null.
     */
    public ModuleTypeInfo(ModuleName moduleName, Module module) {

        if (moduleName == null || module == null) {
            throw new NullPointerException();
        }

        this.moduleName = moduleName;
        this.module = module;

        friendModulesSet = new ArraySet<ModuleName>();
        importedModuleMap = new ArrayMap<ModuleName, ModuleTypeInfo>();
        usingFunctionOrClassMethodMap = new HashMap<String, ModuleName>();
        usingDataConstructorMap = new HashMap<String, ModuleName>();
        usingTypeConstructorMap = new HashMap<String, ModuleName>();
        usingTypeClassMap = new HashMap<String, ModuleName>();
        moduleNameResolver = ModuleNameResolver.make(moduleName, importedModuleMap.keySet());

        typeConstructorMap = new ArrayMap<String, ScopedEntity>();               
        typeClassMap = new ArrayMap<String, ScopedEntity>();              
        classInstanceMap = new ArrayMap<ClassInstanceIdentifier, ClassInstance>();              
        functionMap = new ArrayMap<String, ScopedEntity>();

        cachedClassMethodsMap = new HashMap<String, ClassMethod>();
        cachedDataConstructorsMap = new HashMap<String, DataConstructor>();        
        cachedDependeeModuleTypeInfoMap = new HashMap<ModuleName, ModuleTypeInfo>();

        baseTypeInfo = null;                  

        moduleSourceMetrics = null;
        importedNameOccurrences = null;
        calDocComment = null;
    }

    /**
     * Creates a ModuleTypeInfo instance which extends the specified base type info.
     * This is used for extension modules.
     * @param baseTypeInfo
     */
    ModuleTypeInfo (ModuleTypeInfo baseTypeInfo) {

        this.moduleName = null;  
        this.module = baseTypeInfo.module;    

        friendModulesSet = null;
        importedModuleMap = null;   
        usingFunctionOrClassMethodMap = null;
        usingDataConstructorMap = null;
        usingTypeConstructorMap = null;
        usingTypeClassMap = null;
        moduleNameResolver = null;

        typeConstructorMap = new ArrayMap<String, ScopedEntity>();               
        typeClassMap = new ArrayMap<String, ScopedEntity>();              
        classInstanceMap = new ArrayMap<ClassInstanceIdentifier, ClassInstance>();              
        functionMap = new ArrayMap<String, ScopedEntity>();

        cachedClassMethodsMap = new HashMap<String, ClassMethod>();
        cachedDataConstructorsMap = new HashMap<String, DataConstructor>();        
        cachedDependeeModuleTypeInfoMap = new HashMap<ModuleName, ModuleTypeInfo>();

        if (baseTypeInfo.baseTypeInfo != null) {
            throw new IllegalArgumentException("can't extend an extension module.");
        }
        this.baseTypeInfo = baseTypeInfo;        

        moduleSourceMetrics = null;
        calDocComment = null;
    }

    /**    
     * @return boolean True if this ModuleTypeInfo is an extension module (used for adjuncts, and
     *   other temporary modifications of regular CAL modules).
     */
    boolean isExtension() {
        return baseTypeInfo != null;
    }

    /**    
     * Creation date: (6/6/01 10:05:52 AM)
     * @return the name of the Module that this ModuleTypeInfo has type info for
     */
    public ModuleName getModuleName() {
        if (baseTypeInfo != null) {
            return baseTypeInfo.getModuleName();
        }

        return moduleName;
    }

    /**         
     * @param moduleName
     * @return true if the module with name 'moduleName' is a friend of this module.
     */
    public boolean hasFriendModule(ModuleName moduleName) {
        if (baseTypeInfo != null) {
            return baseTypeInfo.hasFriendModule(moduleName);
        }

        return friendModulesSet.contains(moduleName);
    }

    void addFriendModule(ModuleName moduleName) {
        if (baseTypeInfo != null) {
            throw new UnsupportedOperationException("Adding friend modules to an adjunct is unsupported");
        }

        friendModulesSet.add(moduleName);       
    }

    /**     
     * @return number of friend modules that this module declares.
     */
    public int getNFriendModules() {
        return friendModulesSet.size();
    }

    /**     
     * @param n zero-based index.
     * @return the name of a friend module declared by this module. 
     */
    public ModuleName getNthFriendModule(int n) { 
        return friendModulesSet.get(n);
    }

    /**
     * True if the entity can be used lexically in this CAL module.
     * In particular, the entity must be 
     * a) defined in this module
     * or
     * b) defined in a module imported by this module, be public in that module, or this module is a friend 
     *    of that module.
     *    
     * @param scopedEntity   
     * @return true if the given scoped entity can be used lexically in this module.
     */
    public boolean isEntityVisible(ScopedEntity scopedEntity) {
        if (baseTypeInfo != null) {
            return baseTypeInfo.isEntityVisible(scopedEntity);
        }

        ModuleName entityModuleName = scopedEntity.getName().getModuleName();
        if (entityModuleName.equals(moduleName)) {   
            //an entity can always be used within its own module
            return true;
        }

        ModuleTypeInfo importedModuleTypeInfo = getImportedModule(entityModuleName);
        if (importedModuleTypeInfo == null) {
            //this module does not import the module in which the entity is defined, and so the entity is not visible.
            return false;
        }

        Scope scope = scopedEntity.getScope();        

        return scope.isPublic() || (scope.isProtected() && importedModuleTypeInfo.hasFriendModule(moduleName));        
    }

    /**
     * A functional agent is either a function, a data constructor or a class method.
     * Clients that don't care what specific kind of functional agent they are using, only that
     * it can be used to transform, can call this method.
     * Creation date: (6/7/01 9:20:30 AM)
     * @param functionalAgentName
     * @return FunctionalAgent the entity or null if it doesn't exist    
     */
    public FunctionalAgent getFunctionalAgent(String functionalAgentName) {

        int nameLength = functionalAgentName.length();

        if (nameLength == 0) {
            return null;
        }

        char firstChar = functionalAgentName.charAt(0);                    
        if (Character.isUpperCase(firstChar)) {
            return getDataConstructor(functionalAgentName);        
        }

        return getFunctionOrClassMethod(functionalAgentName);                          
    }

    public FunctionalAgent getFunctionOrClassMethod(String functionOrClassMethodName) {
        if (baseTypeInfo != null) {
            FunctionalAgent entity = baseTypeInfo.getFunctionOrClassMethod(functionOrClassMethodName);
            if (entity != null) {
                return entity;
            }
        }

        FunctionalAgent entity = getFunction(functionOrClassMethodName);
        if (entity != null) {
            return entity;
        }

        return getClassMethod(functionOrClassMethodName);
    } 

    /**
     * An internal helper to get a function or class method that is in a module
     * imported into this module, either directly or indirectly and regardless
     * of the scope of the given entity.
     * This is mostly for internal compiler usage where class method functions
     * can resolve to be functions that are not directly visible within a module.
     * 
     * @param functionOrClassMethodName
     * @return FunctionalAgent
     */
    FunctionalAgent getReachableFunctionOrClassMethod(QualifiedName functionOrClassMethodName) {
        if (baseTypeInfo != null) {
            FunctionalAgent functionOrClassMethod = baseTypeInfo.getReachableFunctionOrClassMethod(functionOrClassMethodName);
            if (functionOrClassMethod != null) {
                return functionOrClassMethod;    
            }
        }   

        ModuleName functionOrClassMethodModuleName = functionOrClassMethodName.getModuleName();
        if(functionOrClassMethodModuleName.equals(moduleName)) {
            return getFunctionOrClassMethod(functionOrClassMethodName.getUnqualifiedName());
        }

        ModuleTypeInfo moduleTypeInfo = getDependeeModuleTypeInfo(functionOrClassMethodModuleName);        
        if (moduleTypeInfo == null) {
            return null;
        }

        return moduleTypeInfo.getFunctionOrClassMethod(functionOrClassMethodName.getUnqualifiedName());                          
    }

    /**
     * Method getFunctionalAgents.
     * A functional agent is either a function, a data constructor or a class method.
     * i.e. anything that to an external client "looks like" a top-level named function or "Gem".   
     * @return FunctionalAgent[] the array of functional agents in this module. Includes entities
     *    like Cons which can be used in CAL text in operator form, but not hidden functions
     *    introduced in the implementation (e.g. lifted lambda expressions).
     */
    public FunctionalAgent[] getFunctionalAgents() {

        List<FunctionalAgent> functionalAgentList = new ArrayList<FunctionalAgent>();

        if (baseTypeInfo != null) {        
            FunctionalAgent baseEntities[] = baseTypeInfo.getFunctionalAgents();

            for (int i = 0; i < baseEntities.length; ++i) {
                functionalAgentList.add(baseEntities[i]);
            }
        }

        //add the functions
        for (int i = 0, nFunctions = getNFunctions(); i < nFunctions; ++i) {
            functionalAgentList.add(getNthFunction(i));
        }

        //add the data constructors
        for (int i = 0, nTypeCons = getNTypeConstructors(); i < nTypeCons; ++i) {

            TypeConstructor typeCons = getNthTypeConstructor(i);

            for (int j = 0, nDataCons = typeCons.getNDataConstructors(); j < nDataCons; ++j) {

                functionalAgentList.add(typeCons.getNthDataConstructor(j));
            }
        }

        //add the class methods
        for (int i = 0, nTypeClasses = getNTypeClasses(); i < nTypeClasses; ++i) {

            TypeClass typeClass = getNthTypeClass(i);

            for (int j = 0, nClassMethods = typeClass.getNClassMethods(); j < nClassMethods; ++j) {

                functionalAgentList.add(typeClass.getNthClassMethod(j));
            }
        }

        return functionalAgentList.toArray(new FunctionalAgent[0]);
    }     

    /**
     * Method getImportedModule.
     * @param importedModuleName
     * @return ModuleTypeInfo the imported module or null if the module does not import a module with the given name.
     */   
    public ModuleTypeInfo getImportedModule(ModuleName importedModuleName) {
        if (baseTypeInfo != null) {
            return baseTypeInfo.getImportedModule(importedModuleName);
        }
        return importedModuleMap.get(importedModuleName);
    }

    /**
     * Get the module type info for the named dependee.
     * The dependency can be direct or indirect. 
     * @param dependeeModuleName
     * @return the ModuleTypeInfo for the named module if it is a dependee of this module, otherwise null
     */
    public ModuleTypeInfo getDependeeModuleTypeInfo (ModuleName dependeeModuleName) {
        // Synchronize cache access to ensure thread-safety.
        synchronized (cachedDependeeModuleTypeInfoMap) {
            
            if (cachedDependeeModuleTypeInfoMap.isEmpty() && getNImportedModules() != 0) {
                populateDependeeModuleTypeInfoMapHelper(this);

                // An entry will have been entered for the current module, so we need to remove it.
                cachedDependeeModuleTypeInfoMap.remove(getModuleName());
            }

            return cachedDependeeModuleTypeInfoMap.get(dependeeModuleName);
        }
    }

    /**
     * Helper method to populate dependeeModuleTypeInfoMap.
     * @param currentModuleTypeInfo the ModuleTypeInfo being traversed.
     */
    private void populateDependeeModuleTypeInfoMapHelper(ModuleTypeInfo currentModuleTypeInfo) {
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        if (cachedDependeeModuleTypeInfoMap.containsKey(currentModuleName)) {
            return;
        }
        cachedDependeeModuleTypeInfoMap.put(currentModuleName, currentModuleTypeInfo);

        for (int i = 0; i < currentModuleTypeInfo.getNImportedModules(); ++i) {
            ModuleTypeInfo mti = currentModuleTypeInfo.getNthImportedModule(i);
            populateDependeeModuleTypeInfoMapHelper(mti);
        }
    }

    /**
     * @return The ModuleSourceMetrics object associated with this module
     */
    ModuleSourceMetrics getModuleSourceMetrics() {
        return moduleSourceMetrics;
    }

    /**
     * @param qualifiedName
     * @return true if qualifiedName occurs in this module and is from an imported module.
     */
    boolean doesImportedNameOccur(QualifiedName qualifiedName) {
        // I did not initialize this with an empty data structure
        // since the presence or absence of this value is used to 
        // detect an error when the variable is initialized.
        if (importedNameOccurrences == null){
            return false;
        }
            
        return importedNameOccurrences.contains(qualifiedName);
    }

    /**
     * Set the ModuleSourceMetrics object associated with this module
     * @param moduleSourceMetrics The ModuleSourceMetrics to associate with this module
     */
    void setModuleSourceMetrics(ModuleSourceMetrics moduleSourceMetrics) {
        if (this.moduleSourceMetrics != null) {
            throw new IllegalArgumentException("The module " + moduleName + " already has an associated ModuleSourceMetrics object");
        }

        this.moduleSourceMetrics = moduleSourceMetrics;
    }

    /**
     * Set the importedNameOccurrences associated with this module.
     * @param importedNameOccurrences the importedNameOccurrences to associate with this module 
     */
    void setImportedNameOccurrences(Set<QualifiedName> importedNameOccurrences) {
        if(this.importedNameOccurrences != null) {
            throw new IllegalArgumentException("The module " + moduleName + " already has an associated importedNameOccurrences object.");
        }

        this.importedNameOccurrences = importedNameOccurrences;
    }

    /**
     * @return the CALDoc comment for this module, or null if there is none.
     */
    public CALDocComment getCALDocComment() {
        return calDocComment;
    }

    /**
     * Set the CALDoc comment associated with this module.
     * @param comment the CALDoc comment for this module, or null if there is none.
     */
    void setCALDocComment(CALDocComment comment) {
        calDocComment = comment;
    }
    
    /**
     * @return whether this module is deprecated (via a CALDoc deprecated block).
     */
    public final boolean isDeprecated() {
        if (calDocComment == null) {
            return false;
        } else {
            return calDocComment.getDeprecatedBlock() != null;
        }
    }
    
    /**
     * Method getNImportedModules.
     * @return int the number of modules directly imported into this module.
     */
    public int getNImportedModules() {

        if (baseTypeInfo != null) {
            return baseTypeInfo.getNImportedModules();
        }

        return importedModuleMap.size();      
    }

    /**
     * Method getNthImportedModule.
     * @param n zero based index
     * @return ModuleTypeInfo the nth module imported by this module
     */
    public ModuleTypeInfo getNthImportedModule (int n) {
        if (baseTypeInfo != null) {            
            return baseTypeInfo.getNthImportedModule(n);            
        }

        return importedModuleMap.getNthValue(n);        
    }

    /**
     * Method addImportedModule.
     * @param importedModuleTypeInfo
     * @throws IllegalArgumentException if the imported module is already imported by this module
     */
    void addImportedModule(ModuleTypeInfo importedModuleTypeInfo) {
        if (baseTypeInfo != null) {
            throw new IllegalStateException("can't add an imported module to an extension module.");
        }        

        ModuleName importedModuleName = importedModuleTypeInfo.getModuleName();
        if (importedModuleMap.containsKey(importedModuleName)) {
            throw new IllegalArgumentException("The module " + moduleName + " already imports the module " + importedModuleName + ".");
        }

        importedModuleMap.put(importedModuleName, importedModuleTypeInfo);
    } 

    /**
     * This method needs to be called when all the imports are processed by {@link #addImportedModule}. This
     * builds up the module name resolver needed to resolve module names.
     */
    void finishAddingImportedModules() {
        if (baseTypeInfo != null) {
            throw new IllegalStateException("imported modules are not allowed in an extension module.");
        }        

        moduleNameResolver = ModuleNameResolver.make(moduleName, importedModuleMap.keySet());
    }
    
    /**
     * @return the module name resolver for resolving module names in the context of this module.
     */
    public ModuleNameResolver getModuleNameResolver() {
        if (baseTypeInfo != null) {
            return baseTypeInfo.getModuleNameResolver();
        }
        
        return moduleNameResolver;
    }
    
    /**
     * Method getTypeConstructor.
     * @param typeConstructorName the unqualified type constructor name e.g. "Maybe".
     * @return TypeConstructor the type constructor or null if the module does not define a type constructor with the given name.  
     */
    public TypeConstructor getTypeConstructor(String typeConstructorName) {
        if (baseTypeInfo != null) {
            TypeConstructor typeCons = baseTypeInfo.getTypeConstructor(typeConstructorName);
            if (typeCons != null) {
                return typeCons;
            }
        }

        return (TypeConstructor) typeConstructorMap.get(typeConstructorName);
    }

    /**        
     * @param typeConstructorName name of a type constructor visible from this module
     * @return TypeConstructor the type constructor or null if it does not exist or is not visible
     */
    public TypeConstructor getVisibleTypeConstructor(QualifiedName typeConstructorName) {
        if (baseTypeInfo != null) {
            TypeConstructor typeCons = baseTypeInfo.getVisibleTypeConstructor(typeConstructorName);
            if (typeCons != null) {
                return typeCons;
            }
        }

        ModuleName typeConstructorModuleName = typeConstructorName.getModuleName();
        if(typeConstructorModuleName.equals(moduleName)) {
            return getTypeConstructor(typeConstructorName.getUnqualifiedName());
        }

        ModuleTypeInfo importedModuleTypeInfo = getImportedModule(typeConstructorModuleName);
        if (importedModuleTypeInfo == null) {
            return null;
        }

        TypeConstructor typeConstructor = importedModuleTypeInfo.getTypeConstructor(typeConstructorName.getUnqualifiedName());
        if (typeConstructor != null) {
            Scope scope = typeConstructor.getScope();

            if (scope.isPublic() || (scope.isProtected() && importedModuleTypeInfo.hasFriendModule(this.moduleName))) {
                return typeConstructor;
            }
        }

        return null;
    }          

    /**
     * An internal helper to get a type constructor that is in a module
     * imported into this module, either directly or indirectly and regardless
     * of the scope of the given entity.
     * This is for internal use by the compile when loading serialized module files.
     * 
     * @param typeConstructorName
     * @return TypeConstructor
     */
    public TypeConstructor getReachableTypeConstructor(QualifiedName typeConstructorName) {
        if (baseTypeInfo != null) {
            TypeConstructor typeCons = baseTypeInfo.getReachableTypeConstructor(typeConstructorName);
            if (typeCons != null) {
                return typeCons;    
            }
        }   

        ModuleName typeConstructorModuleName = typeConstructorName.getModuleName();
        if(typeConstructorModuleName.equals(moduleName)) {
            return getTypeConstructor(typeConstructorName.getUnqualifiedName());
        }

        ModuleTypeInfo moduleTypeInfo = getDependeeModuleTypeInfo(typeConstructorModuleName);        
        if (moduleTypeInfo == null) {
            return null;
        }

        return moduleTypeInfo.getTypeConstructor(typeConstructorName.getUnqualifiedName());                          
    }

    /**
     * Method getNTypeConstructors.
     * @return int the number of type constructors defined within the module
     */
    public int getNTypeConstructors() {
        int nTC = 0;
        if (baseTypeInfo != null) {
            nTC += baseTypeInfo.getNTypeConstructors();
        }
        return nTC + typeConstructorMap.size();
    }

    /**
     * Method getNthTypeConstructor.
     * @param n zero based index
     * @return TypeConstructor the nth type constructor defined within the module (defined via a data or foreign data declaration).
     */
    public TypeConstructor getNthTypeConstructor(int n) {
        if (baseTypeInfo != null) {
            if (n < baseTypeInfo.getNTypeConstructors()) {
                return baseTypeInfo.getNthTypeConstructor(n);
            } else {
                n -= baseTypeInfo.getNTypeConstructors();
            }
        }
        return (TypeConstructor)typeConstructorMap.getNthValue(n);                   
    }  

    /**
     * Method addTypeConstructor.
     * @param typeCons the new type constructor to be added to the module   
     */
    void addTypeConstructor(TypeConstructor typeCons) {

        QualifiedName typeConstructorName = typeCons.getName();

        if (!moduleName.equals(typeConstructorName.getModuleName())) {

            throw new IllegalArgumentException("the type constructor added must belong to the module " + moduleName + ".");
        }

        if (containsTypeConstructor(typeConstructorName.getUnqualifiedName())) {
            throw new IllegalArgumentException("The module " + moduleName + " already defines the type " + typeConstructorName + ".");
        }

        typeConstructorMap.put(typeConstructorName.getUnqualifiedName(), typeCons);
    }

    /**
     * Determine if this type info or the base type info contain the named type.
     * @param name
     * @return - true if if this type info or the base type info contain the named type.
     */    
    private boolean containsTypeConstructor (String name) {
        return ((baseTypeInfo != null && baseTypeInfo.containsTypeConstructor(name)) || typeConstructorMap.containsKey(name));
    }

    /**
     * Method getTypeClass.
     * @param typeClassName the unqualified type class name e.g. "Ord".
     * @return TypeClass the type class or null if the module does not define a type class with the given name.  
     */
    public TypeClass getTypeClass(String typeClassName) {
        if (baseTypeInfo != null) {
            TypeClass typeClass = baseTypeInfo.getTypeClass(typeClassName);
            if (typeClass != null) {
                return typeClass;
            }
        }
        return (TypeClass) typeClassMap.get(typeClassName);
    }  

    /**        
     * @param typeClassName name of a type class visible from this module
     * @return TypeClass the type class or null if it does not exist or is not visible
     */
    public TypeClass getVisibleTypeClass(QualifiedName typeClassName) {
        if (baseTypeInfo != null) {
            TypeClass typeClass = baseTypeInfo.getVisibleTypeClass(typeClassName);
            if (typeClass != null) {
                return typeClass;
            }
        }

        ModuleName typeClassModuleName = typeClassName.getModuleName();
        if(typeClassModuleName.equals(moduleName)) {
            return getTypeClass(typeClassName.getUnqualifiedName());
        }

        ModuleTypeInfo importedModuleTypeInfo = getImportedModule(typeClassModuleName);
        if (importedModuleTypeInfo == null) {
            return null;
        }

        TypeClass typeClass = importedModuleTypeInfo.getTypeClass(typeClassName.getUnqualifiedName());            
        if (typeClass != null) {
            Scope scope = typeClass.getScope();

            if (scope.isPublic() || (scope.isProtected() && importedModuleTypeInfo.hasFriendModule(this.moduleName))) {        
                return typeClass;
            }
        }

        return null;
    }      

    /**
     * An internal helper to get a type class that is in a module
     * imported into this module, either directly or indirectly and regardless
     * of the scope of the given entity.
     * This is for internal use by the compile when loading serialized module files.
     * 
     * @param typeClassName
     * @return TypeClassEntity
     */
    TypeClass getReachableTypeClass(QualifiedName typeClassName) {
        if (baseTypeInfo != null) {
            TypeClass typeClass = baseTypeInfo.getReachableTypeClass(typeClassName);
            if (typeClass != null) {
                return typeClass;    
            }
        }   

        ModuleName typeClassModuleName = typeClassName.getModuleName();
        if(typeClassModuleName.equals(moduleName)) {
            return getTypeClass(typeClassName.getUnqualifiedName());
        }

        ModuleTypeInfo moduleTypeInfo = getDependeeModuleTypeInfo(typeClassModuleName);        
        if (moduleTypeInfo == null) {
            return null;
        }

        return moduleTypeInfo.getTypeClass(typeClassName.getUnqualifiedName());                          
    }

    /**
     * Method getNTypeClasses.
     * @return int the number of type classes defined within the module
     */
    public int getNTypeClasses() {
        int nTypeClasses = 0;
        if (baseTypeInfo != null) {
            nTypeClasses += baseTypeInfo.getNTypeClasses();
        }
        return nTypeClasses + typeClassMap.size();
    }

    /**
     * Method getNTopLevelTypeClasses
     * @return int the number of type classes defined at the top level of the type info. 
     */
    int getNTopLevelTypeClasses () {
        return typeClassMap.size();
    }

    /**
     * Method getNthTypeClass.
     * @param n zero based index
     * @return TypeClassEntity the nth type class defined within the module.
     */
    public TypeClass getNthTypeClass(int n) {
        if (baseTypeInfo != null) {
            if (n < baseTypeInfo.getNTypeClasses()) {
                return baseTypeInfo.getNthTypeClass(n);
            } else {
                n -= baseTypeInfo.getNTypeClasses();
            }
        }
        return (TypeClass)typeClassMap.getNthValue(n);                   
    }  

    /**
     * Method getNthTopLevelTypeClass.
     * @param n zero based index
     * @return TypeClassEntity the nth type class defined within the top level module.
     */
    TypeClass getNthTopLevelTypeClass (int n) {
        return (TypeClass)typeClassMap.getNthValue(n);
    }

    /**
     * Method addTypeClass.
     * @param typeClass the new type class to be added to the module  
     */
    void addTypeClass(TypeClass typeClass) {

        QualifiedName typeClassName = typeClass.getName();

        if (!moduleName.equals(typeClassName.getModuleName())) {

            throw new IllegalArgumentException("the type class added must belong to the module " + moduleName + ".");
        }

        if (containsTypeClass (typeClassName.getUnqualifiedName())) {
            throw new IllegalArgumentException("The module " + moduleName + " already defines the class " + typeClassName.getUnqualifiedName() + ".");
        }

        typeClassMap.put(typeClassName.getUnqualifiedName(), typeClass);
    }

    private boolean containsTypeClass (String name) {
        return ((baseTypeInfo != null && baseTypeInfo.containsTypeClass(name)) || typeClassMap.containsKey(name)); 
    }


    /**
     * Method getFunction.    
     * @param functionName unqualified name of the function
     * @return Function the function or null if the module does not define a function with the given name.    
     */
    public Function getFunction(String functionName) {
        if (baseTypeInfo != null) {
            Function e = baseTypeInfo.getFunction(functionName);
            if (e != null) {
                return e;
            }
        }
        return (Function) functionMap.get(functionName);
    }

    /**
     * Method getNFunctionss.
     * @return int number of functions defined within the module
     */
    public int getNFunctions() {
        int nFunctions = 0;
        if (baseTypeInfo != null) {
            nFunctions += baseTypeInfo.getNFunctions();
        }
        return nFunctions + functionMap.size();      
    }   

    /**
     * Method getNthFunction.
     * @param n zero based index
     * @return FunctionalAgent the nth function defined within the module
     */
    public Function getNthFunction(int n) {
        if (baseTypeInfo != null) {
            if (n < baseTypeInfo.getNFunctions()) {
                return baseTypeInfo.getNthFunction(n);
            } else {
                n -= baseTypeInfo.getNFunctions();
            }
        }
        return (Function)functionMap.getNthValue(n);        
    }            

    /**
     * @param functionEntity the function to add to the module.
     */
    void addFunction(Function functionEntity) {

        if (!moduleName.equals(functionEntity.getName().getModuleName())) {
            throw new IllegalArgumentException("function added must belong to the module " + moduleName + ".");
        }

        String functionName = functionEntity.getName().getUnqualifiedName();
        if (containsFunction (functionName)) {
            throw new IllegalArgumentException("The module " + moduleName + " already defines the function " + functionName + ".");
        }

        functionMap.put(functionName, functionEntity);
    }

    private boolean containsFunction (String functionName) {
        return ((baseTypeInfo != null && baseTypeInfo.containsFunction(functionName)) || functionMap.containsKey(functionName));
    }

    /**        
     * @param functionName name of a function visible from this module
     * @return Function the function or null if it does not exist or is not visible
     */
    public Function getVisibleFunction(QualifiedName functionName) {
        if (baseTypeInfo != null) {
            Function e = baseTypeInfo.getVisibleFunction(functionName);
            if (e != null) {
                return e;    
            }
        }

        ModuleName functionModuleName = functionName.getModuleName();
        if(functionModuleName.equals(moduleName)) {
            return getFunction(functionName.getUnqualifiedName());
        }

        ModuleTypeInfo importedModuleTypeInfo = getImportedModule(functionModuleName);
        if (importedModuleTypeInfo == null) {
            return null;
        }

        Function function = importedModuleTypeInfo.getFunction(functionName.getUnqualifiedName());            
        if (function != null) {
            Scope scope = function.getScope();

            if (scope.isPublic() || (scope.isProtected() && importedModuleTypeInfo.hasFriendModule(this.moduleName))) {        
                return function;
            }
        }

        return null;
    }      

    /**
     * An internal helper to get a function that is in a module
     * imported into this module, either directly or indirectly and regardless
     * of the scope of the given entity.
     * This is for internal use by the compile when loading serialized module files.
     * 
     * @param functionName
     * @return TypeClassEntity
     */
    Function getReachableFunction(QualifiedName functionName) {
        if (baseTypeInfo != null) {
            Function f = baseTypeInfo.getReachableFunction(functionName);
            if (f != null) {
                return f;    
            }
        }   

        ModuleName functionModuleName = functionName.getModuleName();
        if(functionModuleName.equals(moduleName)) {
            return getFunction(functionName.getUnqualifiedName());
        }

        ModuleTypeInfo moduleTypeInfo = getDependeeModuleTypeInfo(functionModuleName);        
        if (moduleTypeInfo == null) {
            return null;
        }

        return moduleTypeInfo.getFunction(functionName.getUnqualifiedName());                          
    }

    /**    
     * @param dataConsName name of the data constructor, assumed to belong to this module
     * @return DataConstructor the data constructor or null if it is not in this module
     */
    public DataConstructor getDataConstructor(String dataConsName) {

        if (baseTypeInfo != null) {
            DataConstructor dataCons = baseTypeInfo.getDataConstructor(dataConsName);
            if (dataCons != null) {
                return dataCons;
            }
        }

        // Synchronize cache access to ensure thread-safety.
        synchronized (cachedDataConstructorsMap) {
            
            //first check for the data constructor in the cache
            DataConstructor dataCons = cachedDataConstructorsMap.get(dataConsName);
            if (dataCons != null) {
                return dataCons;
            }

            //next, check within each type constructor, and add to the cache.    

            for (int i = 0, nTypeCons = getNTypeConstructors(); i < nTypeCons; ++i) {

                TypeConstructor typeCons = getNthTypeConstructor(i);

                dataCons = typeCons.getDataConstructor(dataConsName);
                if (dataCons != null) {
                    cachedDataConstructorsMap.put(dataConsName, dataCons);
                    return dataCons;
                }                       
            }
        }

        return null;                       
    }       

    /**        
     * @param dataConsName name of a data constructor visible from this module
     * @return DataConstructor the data constructor or null if it does not exist or is not visible
     */
    public DataConstructor getVisibleDataConstructor(QualifiedName dataConsName) {
        if (baseTypeInfo != null) {
            DataConstructor dataCons = baseTypeInfo.getVisibleDataConstructor(dataConsName);
            if (dataCons != null) {
                return dataCons;
            }
        }

        ModuleName dataConsModuleName = dataConsName.getModuleName();
        if(dataConsModuleName.equals(moduleName)) {
            return getDataConstructor(dataConsName.getUnqualifiedName());
        }

        ModuleTypeInfo importedModuleTypeInfo = getImportedModule(dataConsModuleName);
        if (importedModuleTypeInfo == null) {
            return null;
        }

        DataConstructor dataCons = importedModuleTypeInfo.getDataConstructor(dataConsName.getUnqualifiedName());            
        if (dataCons != null) {
            Scope scope = dataCons.getScope();

            if (scope.isPublic() || (scope.isProtected() && importedModuleTypeInfo.hasFriendModule(this.moduleName))) {        
                return dataCons;
            }
        }

        return null;
    }      

    /**
     * An internal helper to get a data constructor that is in a module
     * imported into this module, either directly or indirectly and regardless
     * of the scope of the given entity.
     * This is for internal use by the compile when loading serialized module files.
     * 
     * @param dataConstructorName
     * @return DataConstructor
     */
    DataConstructor getReachableDataConstructor(QualifiedName dataConstructorName) {
        if (baseTypeInfo != null) {
            DataConstructor dataCons = baseTypeInfo.getReachableDataConstructor(dataConstructorName);
            if (dataCons != null) {
                return dataCons;    
            }
        }   

        ModuleName dataConstructorModuleName = dataConstructorName.getModuleName();
        if(dataConstructorModuleName.equals(moduleName)) {
            return getDataConstructor(dataConstructorName.getUnqualifiedName());
        }

        ModuleTypeInfo moduleTypeInfo = getDependeeModuleTypeInfo(dataConstructorModuleName);        
        if (moduleTypeInfo == null) {
            return null;
        }

        return moduleTypeInfo.getDataConstructor(dataConstructorName.getUnqualifiedName());                          
    }

    /**    
     * Creation date: (6/7/01 9:20:30 AM)    
     * @param classMethodName class method name belonging to the current module
     * @return ClassMethod the class method, or null if it does not exist.
     */
    public ClassMethod getClassMethod(String classMethodName) {
        if (baseTypeInfo != null) {
            ClassMethod classMethod = baseTypeInfo.getClassMethod(classMethodName);
            if (classMethod != null) {
                return classMethod;
            }
        }

        // Synchronize cache access to ensure thread-safety.
        synchronized (cachedClassMethodsMap) {
            
            //first check for the class method in the cache.
            ClassMethod classMethod = cachedClassMethodsMap.get(classMethodName);
            if (classMethod != null) {
                return classMethod;
            }   

            //next, check within each type class, and add to the cache.    

            for (int i = 0, nTypeClasses = getNTypeClasses(); i < nTypeClasses; ++i) {

                TypeClass typeClass = getNthTypeClass(i);

                classMethod = typeClass.getClassMethod(classMethodName);
                if (classMethod != null) {
                    cachedClassMethodsMap.put(classMethodName, classMethod);
                    return classMethod;
                }                       
            }
        }

        return null;
    }

    /**
     * Method getVisibleClassMethod.
     * @param classMethodName name of a class method visible from this module
     * @return ClassMethod the class method or null if it does not exist or is not visible
     */
    public ClassMethod getVisibleClassMethod(QualifiedName classMethodName) {
        if (baseTypeInfo != null) {
            ClassMethod classMethod = baseTypeInfo.getVisibleClassMethod(classMethodName);
            if (classMethod != null) {
                return classMethod;    
            }
        }

        ModuleName classMethodModuleName = classMethodName.getModuleName();
        if(classMethodModuleName.equals(moduleName)) {
            return getClassMethod(classMethodName.getUnqualifiedName());
        }

        ModuleTypeInfo importedModuleTypeInfo = getImportedModule(classMethodModuleName);
        if (importedModuleTypeInfo == null) {
            return null;
        }

        ClassMethod classMethod = importedModuleTypeInfo.getClassMethod(classMethodName.getUnqualifiedName());            
        if (classMethod != null) {
            Scope scope = classMethod.getScope();

            if (scope.isPublic() || (scope.isProtected() && importedModuleTypeInfo.hasFriendModule(this.moduleName))) {       
                return classMethod;
            }
        }

        return null;
    }  

    /**
     * An internal helper to get a class method that is in a module
     * imported into this module, either directly or indirectly and regardless
     * of the scope of the given entity.
     * This is for internal use by the compile when loading serialized module files.
     * 
     * @param classMethodName
     * @return ClassMethod
     */
    ClassMethod getReachableClassMethod(QualifiedName classMethodName) {
        if (baseTypeInfo != null) {
            ClassMethod classMethod = baseTypeInfo.getReachableClassMethod(classMethodName);
            if (classMethod != null) {
                return classMethod;    
            }
        }   

        ModuleName classMethodModuleName = classMethodName.getModuleName();
        if(classMethodModuleName.equals(moduleName)) {
            return getClassMethod(classMethodName.getUnqualifiedName());
        }

        ModuleTypeInfo moduleTypeInfo = getDependeeModuleTypeInfo(classMethodModuleName);        
        if (moduleTypeInfo == null) {
            return null;
        }

        return moduleTypeInfo.getClassMethod(classMethodName.getUnqualifiedName());                          
    }

    /**
     * Method addClassInstance.
     * @param classInstance
     */
    void addClassInstance (ClassInstance classInstance) {
        if (containsClassInstance(classInstance)) {
            throw new IllegalArgumentException("The module " + moduleName + " already defines an instance that overlaps with " + classInstance.getNameWithContext() + ".");
        }

        classInstanceMap.put(classInstance.getIdentifier(), classInstance);
    }

    private boolean containsClassInstance (ClassInstance ci) {
        return ((baseTypeInfo != null && baseTypeInfo.containsClassInstance(ci)) || classInstanceMap.containsKey(ci.getIdentifier()));
    }

    /**
     * Method getClassInstance.
     * @param classInstanceName
     * @return ClassInstance
     */
    public ClassInstance getClassInstance (ClassInstanceIdentifier classInstanceName) {
        if (baseTypeInfo != null) {
            ClassInstance ci = baseTypeInfo.getClassInstance(classInstanceName);
            if (ci != null) {
                return ci;
            }
        }

        return classInstanceMap.get(classInstanceName);         
    }

    private ClassInstance getVisibleClassInstance (Set<ModuleName> visitedModules, ClassInstanceIdentifier classInstanceIdentifier) {

        if (!visitedModules.add(moduleName)) {
            //If a module has been visited already, return immediately.
            //This is needed to avoid inefficiencies when there is a deep module hierarchy with many diamond shaped
            //import hierarchies. For example, Intellicut on the Everything project on the Ord a => a input of lessThan
            //went from taking 16 seconds to 0.7 seconds!
            return null;
        } 

        ClassInstance classInstance = getClassInstance(classInstanceIdentifier);
        if (classInstance != null) { 
            return classInstance;
        }

        for (int i = 0, nImports = getNImportedModules(); i < nImports; ++i) {

            ModuleTypeInfo importedModule = getNthImportedModule(i);

            classInstance = importedModule.getVisibleClassInstance(visitedModules, classInstanceIdentifier);
            if (classInstance != null) {              
                return classInstance;
            }
        }

        return null;      
    }    

    /**
     * Method getVisibleClassInstance.
     * @param classInstanceIdentifier
     * @return ClassInstance
     */
    public ClassInstance getVisibleClassInstance (ClassInstanceIdentifier classInstanceIdentifier) {
        if (baseTypeInfo != null) {
            ClassInstance ci = baseTypeInfo.getVisibleClassInstance(classInstanceIdentifier);
            if (ci != null) {
                return ci;
            }
        }

        return getVisibleClassInstance(new HashSet<ModuleName>(), classInstanceIdentifier);        
    }

    /**
     * Returns the unique visible typeClass-typeCons instance for the given type class and type constructor if there is
     * one, or null if none such exists.
     * @param typeClass
     * @param typeCons
     * @return ClassInstance
     */
    public ClassInstance getVisibleClassInstance (TypeClass typeClass, TypeConstructor typeCons) {
        return getVisibleClassInstance(new ClassInstanceIdentifier.TypeConstructorInstance(typeClass.getName(), typeCons.getName()));
    }

    /**
     * Method getNClassInstances.
     * @return int the number of class instances defined within the module
     */
    public int getNClassInstances() {
        int nCI = 0;
        if (baseTypeInfo != null) {
            nCI += baseTypeInfo.getNClassInstances();
        }
        return nCI + classInstanceMap.size();
    }

    /**
     * Method getNTopLevelClassInstances.
     * @return int the number of class instances defined within the top level module
     */
    int getNTopLevelClassInstances() {
        return classInstanceMap.size();
    }

    /**
     * Method getNthClassInstance.
     * @param n zero based index
     * @return ClassInstance the nth class instance defined within the module (defined via an instance declaration).
     */
    public ClassInstance getNthClassInstance(int n) {
        if (baseTypeInfo != null) {
            if (n < baseTypeInfo.getNClassInstances()) {
                return baseTypeInfo.getNthClassInstance(n);
            } else {
                n -= baseTypeInfo.getNClassInstances();
            }
        }
        return classInstanceMap.getNthValue(n);                   
    } 

    /**
     * Method getNthTopLevelClassInstance.
     * @param n zero based index
     * @return ClassInstance the nth class instance defined within the top level module (defined via an instance declaration).
     */
    ClassInstance getNthTopLevelClassInstance(int n) {
        return classInstanceMap.getNthValue(n);                   
    } 

    /**
     * Insert the method's description here.
     * Creation date: (6/18/01 10:33:31 AM)
     * @return String
     */
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        if (isExtension()) {
            result.append("extension module of ");
        }
        result.append("module ").append(getModuleName()).append('\n');

        result.append("import ");
        for (int i = 0, n = getNImportedModules(); i < n; ++i) {                 
            result.append(getNthImportedModule(i).getModuleName()).append(' ');
        }
        result.append('\n');

        result.append("friend ");
        for (int i = 0, n = getNFriendModules(); i < n; ++i) {                 
            result.append(getNthFriendModule(i)).append(' ');
        }
        result.append('\n');        

        result.append("--------------type constructors\n");
        for (int i = 0, n = getNTypeConstructors(); i < n; ++i) {         
            result.append(getNthTypeConstructor(i).getName().getUnqualifiedName()).append('\n');
        }

        result.append("--------------type classes\n");
        for (int i = 0, n = getNTypeClasses(); i < n; ++i) {         
            result.append(getNthTypeClass(i).getName().getUnqualifiedName()).append('\n');
        }

        result.append("--------------class instances\n");
        for (int i = 0, n = getNClassInstances(); i < n; ++i) {
            result.append(getNthClassInstance(i).getNameWithContext()).append('\n');
        }

        result.append("--------------functions\n");
        for (int i = 0, n = getNFunctions(); i < n; ++i) {         
            result.append(getNthFunction(i)).append('\n');
        }

        result.append("--------------cached class methods\n");        
        synchronized (cachedClassMethodsMap) {
            for (final String cachedClassMethod : cachedClassMethodsMap.keySet()) {
                result.append(cachedClassMethodsMap.get(cachedClassMethod)).append('\n');
            }
        }

        result.append("--------------cached data constructors\n");        
        synchronized (cachedDataConstructorsMap) {
            for (final String cachedDataConstructor : cachedDataConstructorsMap.keySet()) {
                result.append(cachedDataConstructorsMap.get(cachedDataConstructor)).append('\n');
            }
        }
        
        result.append("--------------cached dependee module type info\n");        
        synchronized (cachedDependeeModuleTypeInfoMap) {
            for (final ModuleName cachedDependeeModuleTypeInfo : cachedDependeeModuleTypeInfoMap.keySet()) {
                result.append(cachedDependeeModuleTypeInfo).append('\n');
            }
        }

        return result.toString();
    }

    /**
     * Add the information in all the "import M using function = ..." clauses to this ModuleTypeInfo.
     * Note: this should not be done for extension modules since extension modules cannot import
     * other modules.
     * 
     * @param usingFunctionOrClassMethodMap function or class method name to the imported module name in which it is defined.
     */
    void addUsingFunctionOrClassMethodMap(Map<String, ModuleName> usingFunctionOrClassMethodMap) { 
        this.usingFunctionOrClassMethodMap.putAll(usingFunctionOrClassMethodMap);
    }
    /**     
     * @param usingFunctionOrClassMethodName name of a function or class method as declared in one of
     *    the "import module using function = functionOrClassMethodName" clauses.
     * @return the corresponding import module name, or null if this is not a "using function"
     */
    ModuleName getModuleOfUsingFunctionOrClassMethod(String usingFunctionOrClassMethodName) {
        if (isExtension()) {
            return baseTypeInfo.getModuleOfUsingFunctionOrClassMethod(usingFunctionOrClassMethodName);
        }

        return usingFunctionOrClassMethodMap.get(usingFunctionOrClassMethodName);
    }

    void addUsingDataConstructorMap(Map<String, ModuleName> usingDataConstructorMap) {   
        this.usingDataConstructorMap.putAll(usingDataConstructorMap);        
    }
    ModuleName getModuleOfUsingDataConstructor(String usingDataConstructorName) {
        if (isExtension()) {
            return baseTypeInfo.getModuleOfUsingDataConstructor(usingDataConstructorName);
        }        
        return usingDataConstructorMap.get(usingDataConstructorName);
    }

    void addUsingTypeConstructorMap(Map<String, ModuleName> usingTypeConstructorMap) {
        this.usingTypeConstructorMap.putAll(usingTypeConstructorMap);        
    }
    ModuleName getModuleOfUsingTypeConstructor(String usingTypeConstructorName) {
        if (isExtension()) {
            return baseTypeInfo.getModuleOfUsingTypeConstructor(usingTypeConstructorName);
        }         
        return usingTypeConstructorMap.get(usingTypeConstructorName);
    }    

    void addUsingTypeClassMap(Map<String, ModuleName> usingTypeClassMap) {
        this.usingTypeClassMap.putAll(usingTypeClassMap);        
    }
    ModuleName getModuleOfUsingTypeClass(String usingTypeClassName) {
        if (isExtension()) {
            return baseTypeInfo.getModuleOfUsingTypeClass(usingTypeClassName);
        }         
        return usingTypeClassMap.get(usingTypeClassName);
    }    

    /**
     * @return the containing Module.
     */
    public Module getModule() {
        return module;
    }

    /**
     * Write this ModuleTypeInfo instance to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    public final void write (RecordOutputStream s) throws IOException {
        if (baseTypeInfo != null) {
            throw new IOException ("Saving adjunct module " + getModuleName() + ".");
        }

        s.startRecord(ModuleSerializationTags.MODULE_TYPE_INFO, serializationSchema);

        // Module name.
        s.writeModuleName(getModuleName());

        // friend modules
        s.writeInt(friendModulesSet.size());
        for (final ModuleName friendModuleName : friendModulesSet) {
            s.writeModuleName(friendModuleName);
        }

        // Imported modules.
        s.writeInt(getNImportedModules());
        for (int i = 0; i < getNImportedModules(); ++i) {
            ModuleTypeInfo imti = getNthImportedModule(i);
            s.writeModuleName(imti.getModuleName());
        }

        // All the using... map members are maps of String -> ModuleName.
        // usingFunctionOrClassMethodMap
        s.writeInt(usingFunctionOrClassMethodMap.size());        
        for (final Map.Entry<String, ModuleName> entry : usingFunctionOrClassMethodMap.entrySet()) {
            String key = entry.getKey();
            ModuleName value = entry.getValue();
            s.writeUTF(key);
            s.writeModuleName(value);
        }

        // usingDataConstructorMap
        s.writeInt(usingDataConstructorMap.size());        
        for (final Map.Entry<String, ModuleName> entry : usingDataConstructorMap.entrySet()) {
            String key = entry.getKey();
            ModuleName value = entry.getValue();
            s.writeUTF(key);
            s.writeModuleName(value);
        }

        // usingTypeConstructorMap
        s.writeInt(usingTypeConstructorMap.size());        
        for (final Map.Entry<String, ModuleName> entry : usingTypeConstructorMap.entrySet()) {
            String key = entry.getKey();
            ModuleName value = entry.getValue();
            s.writeUTF(key);
            s.writeModuleName(value);
        }

        // usingTypeClassMap        
        s.writeInt(usingTypeClassMap.size());        
        for (final Map.Entry<String, ModuleName> entry : usingTypeClassMap.entrySet()) {
            String key = entry.getKey();
            ModuleName value = entry.getValue();
            s.writeUTF(key);
            s.writeModuleName(value);
        }

        // typeConstructorMap
        // String->TypeConstructor
        // The String key is the unqualified name of the TypeConstructor so
        // we just save the TypeConstructor.
        s.writeInt(typeConstructorMap.size());
        for (int i = 0; i < typeConstructorMap.size(); ++i) {
            TypeConstructor typeCons = (TypeConstructor)typeConstructorMap.getNthValue(i);
            typeCons.write(s);
        }

        // typeClassMap
        // String->TypeClass
        // The String key is the unqualified name of the TypeClass so we just save the TypeClass.
        s.writeInt(typeClassMap.size());
        for (int i = 0; i < typeClassMap.size(); ++i) {
            TypeClass typeClass = (TypeClass)typeClassMap.getNthValue(i);
            typeClass.write(s);
        }

        // classInstanceMap
        // ClassInstanceIdentifier->ClassInstance
        // The ClassInstanceIdentifier key is contained in the ClassInstance so we just save the ClassInstance.
        s.writeInt(classInstanceMap.size());
        for (int i = 0; i < classInstanceMap.size(); ++i) {
            ClassInstance ci = classInstanceMap.getNthValue(i);
            ci.write(s);
        }

        // functionMap
        // String->Function
        // The String key is the unqualified name of the Function so we just save the Function.
        s.writeInt(functionMap.size());
        for (int i = 0; i < functionMap.size(); ++i) {
            Function function = (Function)functionMap.getNthValue(i);
            function.write(s);
        }

        //ModuleSourceMetrics
        moduleSourceMetrics.write(s);

        // CALDoc comment
        boolean hasCALDocComment = (calDocComment != null);
        s.writeBoolean(hasCALDocComment);
        if (hasCALDocComment) {
            calDocComment.write(s);
        }

        // importedNameOccurrences
        s.writeInt(importedNameOccurrences.size());
        for (final QualifiedName qualifiedName : importedNameOccurrences) {
            s.writeQualifiedName(qualifiedName);
        }

        s.endRecord();
    }

    /**
     * Adds all the type constructors, type classes, functions, data types and class members that are 
     * public in imported modules or protected in imported modules that this module is a friend of. Not
     * including adjuncts. This is used currently by auto complete.
     * 
     * @return A list of the entities accessible in the current module.
     */
    public List<ScopedEntity> getAccessibleEntitiesInScope(){
        List<ScopedEntity> entities = new ArrayList<ScopedEntity>();
        getAccessibleEntitiesForModule(entities, moduleName);
        for(int i = 0; i < importedModuleMap.size(); ++i){
            importedModuleMap.getNthValue(i).getAccessibleEntitiesForModule(entities, moduleName);
        }
        return entities;
    }

    /**
     * Returns a list of entities from this module that are visible in the target module.
     * @param targetModuleName the name of the module that the symbols will be used in
     * @return a list of entities from the module that would be visible in the target module
     */
    public List<ScopedEntity> getAccessibleEntitiesForModule(ModuleName targetModuleName){
        List<ScopedEntity> entities = new ArrayList<ScopedEntity>();
        getAccessibleEntitiesForModule(entities, targetModuleName);
        return entities;
    }

    private void getAccessibleEntitiesForModule(List<ScopedEntity> entities, ModuleName currentModuleName){
        addFromMap(typeConstructorMap, entities, currentModuleName);
        addFromMap(typeClassMap, entities, currentModuleName);
        addFromMap(functionMap, entities, currentModuleName);
    }
    
    private void addFromMap(ArrayMap<String, ScopedEntity> map, List<ScopedEntity> entities, ModuleName currentModuleName) {
        final int size = map.size();
        for (int i = 0; i < size; ++i) {
            final ScopedEntity scopedEntity = map.getNthValue(i);

            // I do not call isEntityVisible because that will return true for
            // too many symbols. For example,
            // it will return true for upFromToBoolean.

            if (isAccessibleEntity(scopedEntity, currentModuleName)) {
                entities.add(scopedEntity);
            }

            // Add class methods for typeClasses

            if (scopedEntity instanceof TypeClass) {
                final TypeClass typeClass = (TypeClass) scopedEntity;
                for (int j = 0; j < typeClass.getNClassMethods(); ++j) {
                    ClassMethod classMethod = typeClass.getNthClassMethod(j);
                    if (isAccessibleEntity(classMethod, currentModuleName)) {
                        entities.add(classMethod);
                    }
                }
            }

            // Add data constructors for typeConstructor

            else if (scopedEntity instanceof TypeConstructor) {
                TypeConstructor typeConstructor = (TypeConstructor) scopedEntity;
                for (int j = 0; j < typeConstructor.getNDataConstructors(); ++j) {
                    final DataConstructor nthDataConstructor = typeConstructor.getNthDataConstructor(j);
                    if (isAccessibleEntity(nthDataConstructor, currentModuleName)) {
                        entities.add(nthDataConstructor);
                    }
                }
            }
        }
    }

    private boolean isAccessibleEntity(final ScopedEntity scopedEntity, ModuleName currentModuleName) {
        final Scope scope = scopedEntity.getScope();

        final boolean isVisible = scope.isPublic() ||
            (scope.isProtected() && hasFriendModule(currentModuleName));

        return isVisible || scopedEntity.getName().getModuleName().equals(currentModuleName);
    }

    /**
     * Check if a given scopedEntity is in the using clause for this module.
     * @param scopedEntity The scopedEntity to check.
     * @return True if the given scoped entity is in a using clause in this module.
     */
    
    public boolean isUsingEntity(ScopedEntity scopedEntity){
        final QualifiedName name = scopedEntity.getName();
        final String unqualifiedName = name.getUnqualifiedName();
        final ModuleName moduleName = name.getModuleName();
        
        if (scopedEntity instanceof Function || scopedEntity instanceof ClassMethod){
            return isInUsingMap(usingFunctionOrClassMethodMap, moduleName, unqualifiedName);
        }
        else if (scopedEntity instanceof DataConstructor){
            return isInUsingMap(usingDataConstructorMap, moduleName, unqualifiedName);
        }
        else if (scopedEntity instanceof TypeConstructor){
            return isInUsingMap(usingTypeConstructorMap, moduleName, unqualifiedName);
        }
        else if (scopedEntity instanceof TypeClass){
            return isInUsingMap(usingTypeClassMap, moduleName, unqualifiedName);
        }
        else{
            assert false; // new type of scoped entity so update this expression
            return false;
        }
    }

    private boolean isInUsingMap(Map<String, ModuleName> map, ModuleName expectedModuleName, String unqualifiedName){
        final ModuleName moduleName = map.get(unqualifiedName);
        if (moduleName != null){
            if (moduleName.equals(expectedModuleName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Get a collection of the modules imported by the current module.
     * 
     * @return Collection of modules imported by the current module. The set is unmodifiable.
     */
    public Collection<ModuleName> getImportedModules(){
        return importedModuleMap.keySet();
    }

    /**
     * Read the content of this ModuleTypeInfo instance from the input stream.
     * The read position of the stream is before the record header.
     * @param s - the RecordInputStream containing the content.
     * @param otherModules - Map of existing modules keyed by ModuleName, used to resolve imports, etc.
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    public void readContent (RecordInputStream s, Map<ModuleName, Module> otherModules, CompilerMessageLogger msgLogger) throws IOException {
        // The first thing should be the record header for the type info.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.MODULE_TYPE_INFO);
        if (rhi == null) {
            throw new IOException ("Unable to find record for ModuleTypeInfo for Module " + getModuleName() + ".");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, getModuleName(), "ModuleTypeInfo", msgLogger);

        try {
            // Module name.
            // No need to set, that was done when the ModuleTypeInfo instance was created.
            s.readUTF();

            // friend modules
            int nFriendModules = s.readInt();
            for (int i = 0; i < nFriendModules; ++i) {
                ModuleName friendModuleName = s.readModuleName();
                addFriendModule(friendModuleName);
            }            

            // Imported modules.
            int nImports = s.readInt();
            for (int i = 0; i < nImports; ++i) {
                ModuleName importName = s.readModuleName();
                Module m = otherModules.get(importName);
                if (m == null) {
                    throw new IOException ("Unable to resolve imported module " + importName + " in " + getModuleName());
                }
                addImportedModule(m.getModuleTypeInfo());
            }
            finishAddingImportedModules();

            // All the using... map members are maps of String -> String.
            // usingFunctionOrClassMethodMap
            int n = s.readInt();
            for (int i = 0; i < n; ++i) {
                String key = s.readUTF();
                ModuleName value = s.readModuleName();
                usingFunctionOrClassMethodMap.put(key, value);
            }

            // usingDataConstructorMap
            n = s.readInt();
            for (int i = 0; i < n; ++i) {
                String key = s.readUTF();
                ModuleName value = s.readModuleName();
                usingDataConstructorMap.put(key, value);
            }

            // usingTypeConstructorMap
            n = s.readInt();
            for (int i = 0; i < n; ++i) {
                String key = s.readUTF();
                ModuleName value = s.readModuleName();
                usingTypeConstructorMap.put(key, value);
            }

            // usingTypeClassMap        
            n = s.readInt();
            for (int i = 0; i < n; ++i) {
                String key = s.readUTF();
                ModuleName value = s.readModuleName();
                usingTypeClassMap.put(key, value);
            }

            // typeConstructorMap
            // String->TypeConstructor
            // The String key is the unqualified name of the TypeConstructor so
            // we only saved the TypeConstructor.
            int nTypeConstructors = s.readInt();
            RecordInputStream.Bookmark bookmark = s.bookmark();

            // Because types can be mutually recursive we need to do an initial pass in which
            // we create all the TypeConstructor objects and then do a second pass where we
            // load the content of each.
            for (int i = 0; i < nTypeConstructors; ++i) {
                TypeConstructor typeCons = TypeConstructor.loadInit(s, this, msgLogger);
                String uName = typeCons.getName().getUnqualifiedName();
                // Built in TypeConstructor may already be in map if we are loading
                // the Prelude module.
                if (typeConstructorMap.get(uName) == null) {
                    addTypeConstructor(typeCons);
                }
            }
            s.reposition(bookmark);
            for (int i = 0; i < nTypeConstructors; ++i) {
                TypeConstructor typeCons = (TypeConstructor)typeConstructorMap.getNthValue(i);
                typeCons.loadFinal(s, this, msgLogger);
            }


            // typeClassMap
            // String->TypeClass
            // Because type classes can be mutually recursive we need to do an initial pass in which
            // we create all the TypeClass objects and then do a second pass where we
            // load the content of each.
            int nTypeClasses = s.readInt();
            bookmark = s.bookmark();
            for (int i = 0; i < nTypeClasses; ++i) {
                /*TypeClass typeClass =*/ TypeClass.loadInit(s, this, msgLogger);
            }
            s.reposition(bookmark);
            for (int i = 0; i < nTypeClasses; ++i) {
                TypeClass typeClass = (TypeClass)typeClassMap.getNthValue(i);
                typeClass.loadFinal(s, this, msgLogger);
            }

            // classInstanceMap
            // ClassInstanceIdentifier->ClassInstance
            // The ClassInstanceIdentifier key is contained in the ClassInstance so we just save the ClassInstance.
            int nClassInstances = s.readInt();
            for (int i = 0; i < nClassInstances; ++i) {
                ClassInstance classInstance = ClassInstance.load(s, this, msgLogger);
                addClassInstance(classInstance);
            }

            // functionMap
            // String->Function
            // The String key is the unqualified name of the Function so we just save the Function.
            int nFunctions = s.readInt();
            for (int i = 0; i < nFunctions; ++i) {
                Function function = Function.load(s, this, msgLogger);
                if (function != null) {
                    functionMap.put(function.getName().getUnqualifiedName(), function);
                }
            }

            // moduleSourceMetrics
            moduleSourceMetrics = ModuleSourceMetrics.load(s, this, msgLogger);

            // CALDoc comment
            boolean hasCALDocComment = s.readBoolean();
            if (hasCALDocComment) {
                calDocComment = CALDocComment.load(s, getModuleName(), msgLogger);
            }

            // importedNameOccurrences
            importedNameOccurrences = new HashSet<QualifiedName>();
            int nOccurrences = s.readInt();
            for (int i = 0; i < nOccurrences; i++) {
                QualifiedName qualifiedName = s.readQualifiedName();
                if(qualifiedName != null) {
                    importedNameOccurrences.add(qualifiedName);
                }
            }

            s.skipRestOfRecord();

        } catch (IOException e) {
            throw new IOException ("Unable to load ModuleTypeInfo for Module " + getModuleName() + ": " + e.getLocalizedMessage());
        }
    }
}
