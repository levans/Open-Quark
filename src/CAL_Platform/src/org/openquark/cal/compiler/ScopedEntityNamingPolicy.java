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
 * TypeNamingPolicy.java
 * Created: Aug 21, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import org.openquark.cal.module.Cal.Core.CAL_Prelude;

/**
 * A helper class used to determine how a scoped name (such as Maybe) should be displayed.
 * For example, it could be displayed always fully qualified, or always unqualified, or
 * fully qualified only when it is ambiguous within the context of the current module.
 * 
 * @author Bo Ilic
 */
abstract public class ScopedEntityNamingPolicy {

    static public final ScopedEntityNamingPolicy UNQUALIFIED = new Unqualified();
    static public final ScopedEntityNamingPolicy FULLY_QUALIFIED = new FullyQualified();
    
    private ScopedEntityNamingPolicy() {
    }

    public String getName(ScopedEntity entity) {
        
        ModuleName moduleName = getModuleNameForScopedEntity(entity);
        
        if(moduleName != null) {
            return QualifiedName.make(moduleName, entity.getName().getUnqualifiedName()).getQualifiedName();
        } else {
            return entity.getName().getUnqualifiedName();
        }
    }
    
    /**
     * @return the module name to be used for the entity. Null is returned if the entity's name is to be unqualified.
     */
    abstract ModuleName getModuleNameForScopedEntity(ScopedEntity entity);

    /**
     * Use the unqualified name e.g. Maybe rather than Prelude.Maybe.
     * @author Bo Ilic
     */
    static final class Unqualified extends ScopedEntityNamingPolicy {
        
        private Unqualified() {
        }
        
        /** {@inheritDoc} */
        @Override
        ModuleName getModuleNameForScopedEntity(ScopedEntity entity) {
            return null;
        }
    }
    
    /**
     * Use the fully-qualified name (including the full name of the module) e.g. Prelude.Maybe and not Maybe. 
     * @author Bo Ilic
     */
    static final class FullyQualified extends ScopedEntityNamingPolicy {
        
        private FullyQualified() {
        }
        
        /** {@inheritDoc} */
        @Override
        ModuleName getModuleNameForScopedEntity(ScopedEntity entity) {
            return entity.getName().getModuleName();
        }
    } 
    
    /**
     * Use the unqualified name unless there is ambiguity.
     * <p>     
     * More precisely, if the context module is M:
     * <ul>
     * <li> 
     * If the entity is not visible in M, then use the qualified name.
     * <li> 
     * If the entity is visible in M and there
     * is another module in which an entity in the same namespace exists with the same name,
     * and this other entity is defined and visible in M, then use a qualified name with
     * a minimally qualified module name (e.g. Prelude.id, rather than Cal.Core.Prelude.id).
     * </ul>
     * Note: a type class name and a type constructor name live in different namespaces so they
     * will never be ambiguous by the criterion of this class even if they have the same names.
     * <p>
     * Note also that this is not quite the same rule that is used when resolving names in CAL source.
     * For example, if you are working in module Foo and define a sin function there, then
     * you can refer to Foo.sin as sin, since Foo's sin will mask Prelude.sin. This is
     * because dependent modules should not break when new public functions are introduced.
     * However, from the point of view of this naming policy, Foo.sin would be displayed
     * fully qualified, since this appears to be less confusing from the point of view
     * of UI clients. 
     * 
     * @author Bo Ilic
     */
    public static final class UnqualifiedUnlessAmbiguous extends ScopedEntityNamingPolicy {
        
        private ModuleTypeInfo contextModuleTypeInfo;
        
        public UnqualifiedUnlessAmbiguous(ModuleTypeInfo contextModuleTypeInfo) {
            if (contextModuleTypeInfo == null) {
                throw new NullPointerException();
            }
            
            this.contextModuleTypeInfo = contextModuleTypeInfo;
        }
               
        /** 
         * @return true if entity's name should be fully-qualified, or false if it should be unqualified.
         * Note: It's possible for a naming policy to return a name that is neither qualified nor unqualified,
         *       but rather in some custom string format (eg, localized type names, strings 
         *       like "List of Int" for [Int], etc.).  For such cases, override getName as well as useQualifiedName.
         */
        private boolean useQualifiedName(ScopedEntity entity) {
                    
            //if the entity is not visible in the context module, then return the qualified name
            if (!isVisible(entity)) {
                return true;
            } 
           
            //we are now looking to see if there are 2 visible entities having the same unqualified names. 
            
            int sameNameCount = 0;             
            if (getOtherEntity(contextModuleTypeInfo, entity) != null) {
                ++sameNameCount;                
            }                    
                                            
            for (int i = 0, nImportedModules = contextModuleTypeInfo.getNImportedModules(); i < nImportedModules; ++i) {
                
                ModuleTypeInfo moduleTypeInfo = contextModuleTypeInfo.getNthImportedModule(i);
                
                ScopedEntity otherEntity = getOtherEntity(moduleTypeInfo, entity);
                if (otherEntity != null && contextModuleTypeInfo.isEntityVisible(otherEntity)) {
                               
                    ++sameNameCount;
                    
                    if (sameNameCount > 1) {                        
                        return true;
                    }
                }                                                                           
            }
            
            return false;                    
        }
        
        /** {@inheritDoc} */
        @Override
        ModuleName getModuleNameForScopedEntity(ScopedEntity entity) {
            if (useQualifiedName(entity)) {
                return contextModuleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(entity.getName().getModuleName());
            } else {
                return null;
            }
        }
        
        /**
         * boolean true if the entity is visible within the context module
         */
        private boolean isVisible(ScopedEntity entity) {
            
            //an entity is visible within its own context module
            ModuleName entityModuleName = entity.getName().getModuleName();
            if (entityModuleName.equals(contextModuleTypeInfo.getModuleName())) {
                return true;
            }
            
            //a private entity is not visible outside its context module
            if (entity.getScope() == Scope.PRIVATE) {
                return false;
            }
            
            //check if the context module imports the entity's module.
            return contextModuleTypeInfo.getImportedModule(entityModuleName) != null;
        }
        
        /**
         * A helper function that attempts to look up an entity living in the same namespace
         * and with the same unqualified name as the given entity.
         * @param moduleTypeInfo module in which to look up the other entity in
         * @param entity
         * @return ScopedEntity
         */        
        private ScopedEntity getOtherEntity(ModuleTypeInfo moduleTypeInfo, ScopedEntity entity) {

            String entityName = entity.getName().getUnqualifiedName();
            ScopedEntity otherEntity;
            
            if (entity instanceof TypeConstructor) {
                
                otherEntity = moduleTypeInfo.getTypeConstructor(entityName);
                
            } else if (entity instanceof Function || entity instanceof ClassMethod) {
                              
                otherEntity = moduleTypeInfo.getFunctionOrClassMethod(entityName);       
                         
            } else if (entity instanceof TypeClass) {
                
                otherEntity = moduleTypeInfo.getTypeClass(entityName);
                
            } else if (entity instanceof DataConstructor) {
                
                otherEntity = moduleTypeInfo.getDataConstructor(entityName);
                
            } else {
                
                throw new UnsupportedOperationException();
            }
            
            return otherEntity;
        }
    }
    
    /**
     * Qualifies names based on the module that the policy is constructed with.
     * For a policy constructed with module M:
     * <ul>
     * <li>entities in M will not be qualified
     * <li>entities imported in a using clause in M will not be qualified
     * <li>all other entities will be qualified
     * <li>all module names will have minimal qualifiers (Prelude rather than Cal.Prelude if Prelude is unambiguous)
     * </ul>
     * 
     * @author James Wright
     */
    public static final class UnqualifiedIfUsingOrSameModule extends ScopedEntityNamingPolicy {
        
        private final ModuleTypeInfo moduleTypeInfo;
        
        public UnqualifiedIfUsingOrSameModule(ModuleTypeInfo moduleTypeInfo) {
            if(moduleTypeInfo == null) {
                throw new NullPointerException();
            }
            this.moduleTypeInfo = moduleTypeInfo;
        }
        
        /** 
         * @return true if entity's name should be fully-qualified, or false if it should be unqualified.
         * Note: It's possible for a naming policy to return a name that is neither qualified nor unqualified,
         *       but rather in some custom string format (eg, localized type names, strings 
         *       like "List of Int" for [Int], etc.).  For such cases, override getName as well as useQualifiedName.
         */
        private boolean useQualifiedName(ScopedEntity entity) {
            
            QualifiedName qualifiedName = entity.getName(); 
            ModuleName moduleName = qualifiedName.getModuleName();
            String unqualifiedName = qualifiedName.getUnqualifiedName();
            
            // In the same module, don't fully qualify
            if (moduleName.equals(moduleTypeInfo.getModuleName())) {
                return false;
            }
            
            // For names that are in a using clause, don't fully qualify
            if ((entity instanceof Function || entity instanceof ClassMethod) && 
                    moduleName.equals(moduleTypeInfo.getModuleOfUsingFunctionOrClassMethod(unqualifiedName))) { 
                return false;
            }

            if (entity instanceof TypeClass &&
                    moduleName.equals(moduleTypeInfo.getModuleOfUsingTypeClass(unqualifiedName))) {
                return false;
            }

            if (entity instanceof TypeConstructor &&
                    moduleName.equals(moduleTypeInfo.getModuleOfUsingTypeConstructor(unqualifiedName))) {
                return false;
            }
            
            if (entity instanceof DataConstructor &&
                    moduleName.equals(moduleTypeInfo.getModuleOfUsingDataConstructor(unqualifiedName))) {
                return false;
            }
            
            // Otherwise, qualify it
            return true;
        }
        
        /** {@inheritDoc} */
        @Override
        ModuleName getModuleNameForScopedEntity(ScopedEntity entity) {
            if (useQualifiedName(entity)) {
                ModuleName fullModuleName = entity.getName().getModuleName();
                return moduleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(fullModuleName);
            } else {
                return null;
            }
        }
    }
    
    /**
     * This policy produces unqualified names for entities defined in the current module, and Prelude entities
     * (if their names are not also defined in the current module). For qualified names, it produces fully qualified
     * module names.
     * <p>
     * This policy is mainly intended to produce user documentation where the only contextual information is the
     * current module, and the produced names are not further hyperlinked or described by tooltips with their
     * fully qualified names. 
     * 
     * @author Joseph Wong
     */
    public static final class UnqualifiedInCurrentModuleOrInPreludeIfUnambiguous extends ScopedEntityNamingPolicy {
        
        private final ModuleTypeInfo moduleTypeInfo;
        
        public UnqualifiedInCurrentModuleOrInPreludeIfUnambiguous(ModuleTypeInfo moduleTypeInfo) {
            if(moduleTypeInfo == null) {
                throw new NullPointerException();
            }
            this.moduleTypeInfo = moduleTypeInfo;
        }
        
        /** 
         * @return true if entity's name should be fully-qualified, or false if it should be unqualified.
         * Note: It's possible for a naming policy to return a name that is neither qualified nor unqualified,
         *       but rather in some custom string format (eg, localized type names, strings 
         *       like "List of Int" for [Int], etc.).  For such cases, override getName as well as useQualifiedName.
         */
        private boolean useQualifiedName(IdentifierInfo.TopLevel topLevelIdentifier) {
            
            QualifiedName qualifiedName = topLevelIdentifier.getResolvedName(); 
            ModuleName moduleName = qualifiedName.getModuleName();
            String unqualifiedName = qualifiedName.getUnqualifiedName();
            
            // In the current module, don't fully qualify
            if (moduleName.equals(moduleTypeInfo.getModuleName())) {
                return false;
            }

            // In Prelude, so check to see if the current module also defines an entity with that name
            if (moduleName.equals(CAL_Prelude.MODULE_NAME)) {
                
                if (topLevelIdentifier instanceof IdentifierInfo.TopLevel.FunctionOrClassMethod && 
                        moduleTypeInfo.getFunctionOrClassMethod(unqualifiedName) != null) { 
                    return true;
                }
                
                if (topLevelIdentifier instanceof IdentifierInfo.TopLevel.TypeClass &&
                        moduleTypeInfo.getTypeClass(unqualifiedName) != null) {
                    return true;
                }
                
                if (topLevelIdentifier instanceof IdentifierInfo.TopLevel.TypeCons &&
                        moduleTypeInfo.getTypeConstructor(unqualifiedName) != null) {
                    return true;
                }
                
                if (topLevelIdentifier instanceof IdentifierInfo.TopLevel.DataCons &&
                        moduleTypeInfo.getDataConstructor(unqualifiedName) != null) {
                    return true;
                }
                
                // does not conflict with anything in the current module, so use unqualified name
                return false;
            }
            
            // Otherwise, qualify it
            return true;
        }
        
        /** {@inheritDoc} */
        @Override
        ModuleName getModuleNameForScopedEntity(ScopedEntity entity) {
            if (entity instanceof Function || entity instanceof ClassMethod) {
                return getModuleNameForTopLevelIdentifier(new IdentifierInfo.TopLevel.FunctionOrClassMethod(entity.getName()));
            }
            
            if (entity instanceof TypeClass) {
                return getModuleNameForTopLevelIdentifier(new IdentifierInfo.TopLevel.TypeClass(entity.getName()));
            }
            
            if (entity instanceof TypeConstructor) {
                return getModuleNameForTopLevelIdentifier(new IdentifierInfo.TopLevel.TypeCons(entity.getName()));
            }
            
            if (entity instanceof DataConstructor) {
                return getModuleNameForTopLevelIdentifier(new IdentifierInfo.TopLevel.DataCons(entity.getName()));
            }
            
            return null;
        }

        /**
         * @param topLevelIdentifier
         * @return the module name to be used for the entity named by the identifier. Null is returned if the entity's name is to be unqualified.
         */
        ModuleName getModuleNameForTopLevelIdentifier(IdentifierInfo.TopLevel topLevelIdentifier) {
            if (useQualifiedName(topLevelIdentifier)) {
                return topLevelIdentifier.getResolvedName().getModuleName();
            } else {
                return null;
            }
        }

        /** {@inheritDoc} */
        @Override
        public String getName(ScopedEntity entity) {
            ModuleName moduleName = getModuleNameForScopedEntity(entity);
            
            if (moduleName != null) {
                return QualifiedName.make(moduleName, entity.getName().getUnqualifiedName()).getQualifiedName();
            } else {
                return entity.getName().getUnqualifiedName();
            }
        }
        
        /**
         * @param topLevelIdentifier
         * @return the display name to be used for the entity named by the identifier.
         */
        public String getName(IdentifierInfo.TopLevel topLevelIdentifier) {
            ModuleName moduleName = getModuleNameForTopLevelIdentifier(topLevelIdentifier);
            
            if (moduleName != null) {
                return QualifiedName.make(moduleName, topLevelIdentifier.getResolvedName().getUnqualifiedName()).getQualifiedName();
            } else {
                return topLevelIdentifier.getResolvedName().getUnqualifiedName();
            }
        }
        
        /**
         * @return the current module name.
         */
        public ModuleName getCurrentModuleName() {
            return moduleTypeInfo.getModuleName();
        }
    }
}
