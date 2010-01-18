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
 * AbstractDocumentationGenerator.java
 * Creation date: Sep 27, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassInstanceIdentifier;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelTraverser;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.ProgramModelManager;
import org.openquark.util.Pair;


/**
 * This is a base class that encapsulates some of the high level data structure
 * traversal for generating documentation from CALDoc/metadata. It abstracts out
 * this traversal detail so that subclasses can focus upon
 * output-format-specific tasks such as generating appropriate formatting
 * markups.
 * <p>
 * 
 * Note that it is completely possible to write a documentation generator from
 * scratch without subclassing this class. All features accessed by this class
 * are themselves exposed through the CAL API.
 * <p>
 * 
 * The class is organized in a way that is similar to a visitor in the visitor
 * pattern, in that the implementation of the abstract methods are called to
 * "visit" a particular CAL entity to generate its documentation.
 * <p>
 * 
 * The current visitation pattern is:
 * <pre>
 * generateDoc
 *  - for each module: prepareGenerationForModule
 *  
 *  beginDoc
 *  
 *  generateModuleDoc
 *      beginModuleDoc
 *      
 *      generateModuleHeaderSection
 *          generateModuleDescription
 *          beginImportedModulesList
 *          generateImportedModule
 *          endImportedModuleList
 *          beginFriendModulesList
 *          generateFriendModule
 *          endFriendModuleList
 *          beginDirectlyDependentModulesList
 *          generateDirectlyDependentModule
 *          endDirectlyDependentModulesList
 *          beginIndirectlyDependentModulesList
 *          generateIndirectlyDependentModule
 *          endIndirectlyDependentModulesList
 *          
 *      generateModuleOverviewSection
 *          beginModuleOverviewSection
 *          
 *          beginTypeConsOverviewSection
 *          generateTypeConsOverview
 *              generateTypeConsOverviewHeader
 *              beginDataConsOverviewList
 *              generateDataConsOverview
 *              endDataConsOverviewList
 *              generateTypeConsOverviewFooter
 *          endTypeConsOverviewSection
 *          
 *          beginFunctionsOverviewSection
 *          generateFunctionOverview
 *          endFunctionsOverviewSection
 *          
 *          beginTypeClassOverviewSection
 *          generateTypeClassOverview
 *              generateTypeClassOverviewHeader
 *              beginClassMethodOverviewList
 *              generateClassMethodOverview
 *              endClassMethodOverviewList
 *              generateTypeClassOverviewFooter
 *          endTypeClassOverviewSection
 *          
 *          beginClassInstancesOverviewSection
 *          generateClassInstanceOverview
 *              generateClassInstanceOverviewHeader
 *              beginInstanceMethodOverviewList
 *              generateInstanceMethodOverview
 *              endInstanceMethodOverviewList
 *              generateClassInstanceOverviewFooter
 *          endClassInstancesOverviewSection
 *          
 *          endModuleOverviewSection
 *          
 *      generateModuleDetailsSection
 *          generateTypeConsDocSection
 *              beginTypeConsDocSection
 *              
 *              generateTypeConsDoc
 *                  generateTypeConsDocHeader
 *                  
 *                  beginDataConsDocList
 *                  generateDataConsDoc
 *                  endDataConsDocList
 *                  
 *                  beginKnownInstancesList
 *                  generateKnownInstance
 *                  endKnownInstancesList
 *                  
 *                  generateTypeConsDocFooter
 *              
 *              endTypeConsDocSection
 *          
 *          generateFunctionsDocSection
 *              beginFunctionsDocSection
 *              
 *              generateFunctionDoc
 *              
 *              endFunctionsDocSection
 *          
 *          generateTypeClassesDocSection
 *              beginTypeClassesDocSection
 *              
 *              generateTypeClassDoc
 *                  generateTypeClassDocHeader
 *                  
 *                  beginClassMethodDocList
 *                  generateClassMethodDoc
 *                  endClassMethodDocList
 *                  
 *                  beginKnownInstancesList
 *                  generateKnownInstance
 *                  endKnownInstancesList
 *                  
 *                  generateTypeClassDocFooter
 *              
 *              endTypeClassesDocSection
 *          
 *          generateClassInstancesDocSection
 *              beginClassInstancesDocSection
 *              
 *              generateClassInstanceDoc
 *                  generateClassInstanceDocHeader
 *                  
 *                  beginInstanceMethodDocList
 *                  generateInstanceMethodDoc
 *                  endInstanceMethodDocList
 *                  
 *                  generateClassInstanceDocFooter
 *              
 *              endClassInstancesDocSection
 *      
 *      endModuleDoc
 *  
 *  genereateUsageIndices
 *      beginTypeConsUsageDoc
 *      
 *      generateUsageIndiciesForTypeConsOrTypeClass
 *          generateUsageIndiciesForDependentModule
 *              beginUsageDocGroupForDependentModule
 *              
 *              beginUsageDocArgTypeIndex
 *              generateUsageDocArgTypeIndexEntry
 *              endUsageDocArgTypeIndex
 *              
 *              beginUsageDocReturnTypeIndex
 *              generateUsageDocArgTypeIndexEntry
 *              endUsageDocReturnTypeIndex
 *              
 *              beginUsageDocInstanceIndex
 *              generateUsageDocInstanceIndexEntry
 *              endUsageDocInstanceIndex
 *              
 *              endUsageDocGroupForDependentModule
 *          
 *      endTypeConsUsageDoc
 *      
 *      beginTypeClassUsageDoc
 *      
 *      generateUsageIndiciesForTypeConsOrTypeClass
 *          generateUsageIndiciesForDependentModule
 *              beginUsageDocGroupForDependentModule
 *              
 *              beginUsageDocArgTypeIndex
 *              generateUsageDocArgTypeIndexEntry
 *              endUsageDocArgTypeIndex
 *              
 *              beginUsageDocReturnTypeIndex
 *              generateUsageDocArgTypeIndexEntry
 *              endUsageDocReturnTypeIndex
 *              
 *              beginUsageDocInstanceIndex
 *              generateUsageDocInstanceIndexEntry
 *              endUsageDocInstanceIndex
 *              
 *              endUsageDocGroupForDependentModule
 *      
 *      endTypeClassUsageDoc
 *  
 *  endDoc
 * </pre>
 * 
 * @author Joseph Wong
 */
abstract class AbstractDocumentationGenerator {
    
    /** The program model manager associated with this instance. */
    final ProgramModelManager programModelManager;
    
    /** The label maker for making labels (aka anchors in HTML). */
    final DocLabelMaker labelMaker;
    
    /** The filter to use for excluding modules and entries. */
    final DocumentationGenerationFilter filter;
    
    /** The logger for logging status and error messages. */
    final Logger logger;
    
    /** The map containing an index entry for each module. */
    final TreeMap<ModuleName, PerModuleIndices> moduleIndices;
    
    /**
     * The map mapping a qualified type name to a map from module names to pairs
     * of sets of index entries which respectively refer to the type in their
     * argument types and refer to the type in their return types.
     * <p>
     * In other words, it represents the nested mapping of:
     * <p>
     * type name -&gt; dependent module name -&gt; ([arg type index entries], [return type index entries])
     */
    final Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> typeConsUsageIndices = new HashMap<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>>();
    
    /**
     * The map mapping a qualified type class name to a map from module names to pairs
     * of sets of index entries which respectively refer to the type class in their
     * argument types and refer to the type in their return types.
     * <p>
     * In other words, it represents the nested mapping of:
     * <p>
     * type class name -&gt; dependent module name -&gt; ([arg type index entries], [return type index entries])
     */
    final Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> classUsageIndices = new HashMap<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>>();
    
    /** The map from a module name to a set of names of modules that import that module. */
    final LinkedHashMap<ModuleName, Set<ModuleName>> moduleNameToDirectlyDependentModuleNames;

    /** Whether to build usage indices. */
    final boolean shouldGenerateUsageIndices;
    
    /** A map from a ClassInstance to the minimum scope of its instance class and instance type, as previously cached. */
    final IdentityHashMap<ClassInstance, Scope> classInstanceMinScopeCache = new IdentityHashMap<ClassInstance, Scope>();

    /** The module name resolver initialized with the names of modules to be documented. */
    private final ModuleNameResolver moduleNameResolverForDocumentedModules;
    
    /**
     * This is a factory class for creating labels for hyperlink-able entities. Such
     * labels can be used for creating hyperlinks or cross-references.
     *
     * @author Joseph Wong
     */
    static final class DocLabelMaker {
        
        /**
         * Creates a label for a module.
         * @param moduleTypeInfo the ModuleTypeInfo of the module.
         * @return the label for the module.
         */
        String getLabel(ModuleTypeInfo moduleTypeInfo) {
            return getModuleLabel(moduleTypeInfo.getModuleName());
        }
        
        /**
         * Creates a label for a module.
         * @param moduleName the name of the module.
         * @return the label for the module.
         */
        String getModuleLabel(ModuleName moduleName) {
            return "module:" + moduleName;
        }
        
        /**
         * Creates a label for a functional agent (FunctionalAgent: function/class method/data constructor).
         * @param entity the entity.
         * @return the label for the entity.
         */
        String getLabel(FunctionalAgent entity) {
            if (entity instanceof Function || entity instanceof ClassMethod) {
                return getFunctionOrClassMethodLabel(entity.getName().getUnqualifiedName());
            } else if (entity instanceof DataConstructor) {
                return getDataConsLabel(entity.getName().getUnqualifiedName());
            } else {
                throw new IllegalArgumentException();
            }
        }
        
        /**
         * Creates a label for a type constructor.
         * @param typeConstructor the type constructor's entity.
         * @return the label for the type constructor.
         */
        String getLabel(TypeConstructor typeConstructor) {
            return getTypeConsLabel(typeConstructor.getName().getUnqualifiedName());
        }
        
        /**
         * Creates a label for a type constructor.
         * @param unqualifiedName the type constructor's name.
         * @return the label for the type constructor.
         */
        String getTypeConsLabel(String unqualifiedName) {
            return "typeCons:" + unqualifiedName;
        }
        
        /**
         * Creates a label for a data constructor.
         * @param dataConstructor the data constructor's entity.
         * @return the label for the data constructor.
         */
        String getLabel(DataConstructor dataConstructor) {
            return getDataConsLabel(dataConstructor.getName().getUnqualifiedName());
        }
        
        /**
         * Creates a label for a data constructor.
         * @param unqualifiedName the data constructor's name.
         * @return the label for the data constructor.
         */
        String getDataConsLabel(String unqualifiedName) {
            return "dataCons:" + unqualifiedName;
        }
        
        /**
         * Creates a label for a function.
         * @param function the function's entity.
         * @return the label for the function.
         */
        String getLabel(Function function) {
            return getFunctionOrClassMethodLabel(function.getName().getUnqualifiedName());
        }
        
        /**
         * Creates a label for a function.
         * @param unqualifiedName the function's name.
         * @return the label for the function.
         */
        String getFunctionOrClassMethodLabel(String unqualifiedName) {
            return "functionOrMethod:" + unqualifiedName;
        }
        
        /**
         * Creates a label for a type class.
         * @param typeClass the type class's entity.
         * @return the label for the type class.
         */
        String getLabel(TypeClass typeClass) {
            return getTypeClassLabel(typeClass.getName().getUnqualifiedName());
        }
        
        /**
         * Creates a label for a type class.
         * @param unqualifiedName the type class's name.
         * @return the label for the type class.
         */
        String getTypeClassLabel(String unqualifiedName) {
            return "typeClass:" + unqualifiedName;
        }
        
        /**
         * Creates a label for a class method.
         * @param classMethod the class method's entity.
         * @return the label for the class method.
         */
        String getLabel(ClassMethod classMethod) {
            return getFunctionOrClassMethodLabel(classMethod.getName().getUnqualifiedName());
        }
        
        /**
         * Creates a label for a class instance.
         * @param classInstance the class instance's entity.
         * @return the label for the class instance.
         */
        String getLabel(ClassInstance classInstance) {
            return getClassInstanceLabel(classInstance.getIdentifier());
        }
        
        /**
         * Creates a label for a class instance.
         * @param identifier the class instance's identifier.
         * @return the label for the class instance.
         */
        String getClassInstanceLabel(ClassInstanceIdentifier identifier) {
            return "instance:" + identifier.getTypeClassName() + ":" + identifier.getTypeIdentifier().replace('$', ':'); // the $ character is not allowed as an HTML element ID
        }
        
        /**
         * Creates a label for an instance method.
         * @param classInstance the entity of the instance method's class instance.
         * @param instanceMethodName the instance method's name.
         * @return the label for the instance method.
         */
        String getLabel(ClassInstance classInstance, String instanceMethodName) {
            return getInstanceMethodLabel(classInstance.getIdentifier(), instanceMethodName);
        }
    
        /**
         * Creates a label for an instance method.
         * @param identifier the identifier of the instance method's class instance.
         * @param instanceMethodName the instance method's name.
         * @return the label for the instance method.
         */
        String getInstanceMethodLabel(ClassInstanceIdentifier identifier, String instanceMethodName) {
            return "instanceMethod:" + identifier.getTypeClassName() + ":" + identifier.getTypeIdentifier().replace('$', ':') + ":" + instanceMethodName; // the $ character is not allowed as an HTML element ID
        }
    }

    /**
     * This class encapsulates the various indices we build on a per-module basis.
     *
     * @author Joseph Wong
     */
    private static final class PerModuleIndices {
        /** Private constructor. */
        private PerModuleIndices() {}
        
        /** The list containing index entries for types. */
        private final List<IndexEntry> typeIndex = new ArrayList<IndexEntry>();
        
        /** The list containing index entries for functions, data constructors, and class methods. */
        private final List<IndexEntry> functionalAgentIndex = new ArrayList<IndexEntry>();
        
        /** The list containing index entries for classes. */
        private final List<IndexEntry> typeClassIndex = new ArrayList<IndexEntry>();
        
        /** The list containing index entries for instances. */
        private final List<IndexEntry> instanceIndex = new ArrayList<IndexEntry>();
    }

    /**
     * This class encapsulates an index entry - a display name, and the label the entry refers to.
     *
     * @author Joseph Wong
     */
    static class IndexEntry {
        /** The display name to be displayed on this index entry. */
        private final String displayName;
        /** The label this entry refers to. */
        private final String label;
        /** The CAL scope of this entry. Could be null. */
        private final Scope scope;
        /** The kind of entry this is. */
        private final Kind kind;
        
        /** A typesafe enumeration of the possible kinds of index entries. */
        static class Kind {
            static final Kind TYPE = new Kind("type");
            static final Kind FUNCTIONAL_AGENT = new Kind("functionalAgent");
            static final Kind TYPE_CLASS = new Kind("typeClass");
            static final Kind INSTANCE = new Kind("instance");
            
            /** The name of the kind. */
            private final String name;
            /** Private constructor. */
            private Kind(String name) {
                this.name = name;
            }
            /** {@inheritDoc} **/
            @Override
            public String toString() {
                return name;
            }
        }
        
        /**
         * Constructs an index entry.
         * @param displayName the display name.
         * @param label the label the entry refers to.
         * @param scope the CAL scope of the entry. Could be null.
         * @param kind the kind of entry this is. 
         */
        IndexEntry(String displayName, String label, Scope scope, Kind kind) {
            if (displayName == null || label == null || kind == null) {
                throw new NullPointerException();
            }
            
            this.displayName = displayName;
            this.label = label;
            this.scope = scope;
            this.kind = kind;
        }
    
        /**
         * @return the display name of this entry.
         */
        String getDisplayName() {
            return displayName;
        }
    
        /**
         * @return the label of this entry.
         */
        String getLabel() {
            return label;
        }
        
        /**
         * @return the CAL scope of this entry. Could be null.
         */
        Scope getScope() {
            return scope;
        }
        
        /**
         * @return the kind of entry this is.
         */
        Kind getKind() {
            return kind;
        }
    }

    /**
     * A class which traverses a type signature and associates the given entity with its
     * various argument types and return type.
     *
     * @author Joseph Wong
     */
    private final class EnvEntityProcessorForArgAndReturnTypeIndices extends SourceModelTraverser<Void, Void> {
        
        /** The entity to be associated. */
        private final FunctionalAgent entity;
        
        /** Whether we are currently traversing the return type. */
        private boolean inReturnType;
        
        /** A map from type variables to their type class constraints. */
        private Map<String, List<SourceModel.Name.TypeClass>> typeVarToConstrainingClassesMap = new HashMap<String, List<SourceModel.Name.TypeClass>>();
    
        /**
         * Constructs an EnvEntityProcessorForArgAndReturnTypeIndices instance.
         * @param entity the entity to be associated.
         */
        private EnvEntityProcessorForArgAndReturnTypeIndices(FunctionalAgent entity) {
            this.entity = entity;
        }
        
        /**
         * Returns the list of the names of the constraining classes for a type variable.
         * @param typeVarName the type variable.
         * @return the corresponding list of the names of the constraining classes.
         */
        private List<SourceModel.Name.TypeClass> getConstrainingClassNames(String typeVarName) {
            List<SourceModel.Name.TypeClass> theList = typeVarToConstrainingClassesMap.get(typeVarName);
            
            if (theList == null) {
                theList = new ArrayList<SourceModel.Name.TypeClass>();
                typeVarToConstrainingClassesMap.put(typeVarName, theList);
            }
            
            return theList;
        }
        
        /**
         * Returns the appropriate index based on whether we are currently traversing the return type.
         * @param usageIndices the usage indices map.
         * @param qualifiedName the name of the type constructor whose index is to be returned.
         * @return the requested index.
         */
        private TreeSet<FunctionalAgent> getAppropriateIndex(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, QualifiedName qualifiedName) {
            ModuleName entityModuleName = entity.getName().getModuleName();
            if (inReturnType) {
                return getReturnTypeIndex(usageIndices, qualifiedName, entityModuleName);
            } else {
                return getArgTypeIndex(usageIndices, qualifiedName, entityModuleName);
            }
        }
        
        /**
         * Returns the appropriate index based on whether we are currently traversing the return type.
         * @param usageIndices the usage indices map.
         * @param moduleName the module name of the type constructor whose index is to be returned.
         * @param unqualifiedName the unqualified name of the type constructor whose index is to be returned.
         * @return the requested index.
         */
        private TreeSet<FunctionalAgent> getAppropriateIndex(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, ModuleName moduleName, String unqualifiedName) {
            ModuleName entityModuleName = entity.getName().getModuleName();
            if (inReturnType) {
                return getReturnTypeIndex(usageIndices, moduleName, unqualifiedName, entityModuleName);
            } else {
                return getArgTypeIndex(usageIndices, moduleName, unqualifiedName, entityModuleName);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeExprDefn_List(SourceModel.TypeExprDefn.List list, Void arg) {
            verifyArg(list, "list");
            getAppropriateIndex(typeConsUsageIndices, CAL_Prelude.TypeConstructors.List).add(entity);
            return super.visit_TypeExprDefn_List(list, arg);
        }
    
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeExprDefn_TypeCons(SourceModel.TypeExprDefn.TypeCons cons, Void arg) {
            verifyArg(cons, "cons");
            // NOTE: We require that the type cons name be fully qualified with a non-null module name.
            getAppropriateIndex(typeConsUsageIndices, SourceModel.Name.Module.toModuleName(cons.getTypeConsName().getModuleName()), cons.getTypeConsName().getUnqualifiedName()).add(entity);
            return super.visit_TypeExprDefn_TypeCons(cons, arg);
        }
    
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeExprDefn_Unit(SourceModel.TypeExprDefn.Unit unit, Void arg) {
            verifyArg(unit, "unit");
            getAppropriateIndex(typeConsUsageIndices, CAL_Prelude.TypeConstructors.Unit).add(entity);
            return super.visit_TypeExprDefn_Unit(unit, arg);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_Constraint_TypeClass(SourceModel.Constraint.TypeClass typeClassConstraint, Void arg) {
            verifyArg(typeClassConstraint, "typeClass");
            
            SourceModel.Name.TypeClass typeClassName = typeClassConstraint.getTypeClassName();
            String typeVarName = typeClassConstraint.getTypeVarName().getName();
            
            // we make a note that the type variable has this particular class constraint
            getConstrainingClassNames(typeVarName).add(typeClassName);
            return null;
        }
    
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeExprDefn_TypeVar(SourceModel.TypeExprDefn.TypeVar var, Void arg) {
            verifyArg(var, "var");
            
            String typeVarName = var.getTypeVarName().getName();
            
            // we obtain the list of the class constraints on this type variable, and for each one
            // associate the entity with it in the appropriate index
            List<SourceModel.Name.TypeClass> constrainingClassNames = getConstrainingClassNames(typeVarName);
            
            for (int i = 0, n = constrainingClassNames.size(); i < n; i++) {
                SourceModel.Name.TypeClass constrainingClassName = constrainingClassNames.get(i);
                // NOTE: We require that the type class name be fully qualified with a non-null module name.
                QualifiedName qualifiedClassName = QualifiedName.make(SourceModel.Name.Module.toModuleName(constrainingClassName.getModuleName()), constrainingClassName.getUnqualifiedName());
                
                getAppropriateIndex(classUsageIndices, qualifiedClassName).add(entity);
            }
    
            return null;
        }
    
        /**
         * Processes the type signature being visited and associate the entity encapsulated by this
         * visitor with the type classes and type constructors contained within this signature.
         */
        @Override
        public Void visit_TypeSignature(SourceModel.TypeSignature signature, Void arg) {
            verifyArg(signature, "signature");
            
            // process the type constraints 
            int nConstraints = signature.getNConstraints();
            for (int i = 0; i < nConstraints; i++) {
                signature.getNthConstraint(i).accept(this, arg);
            }
            
            // having processed the type constraints, proceed to the type expression
            SourceModel.TypeExprDefn typeExpr = signature.getTypeExprDefn();
            
            // first we traverse each argument type of the function type 
            inReturnType = false;
            while (typeExpr instanceof SourceModel.TypeExprDefn.Function) {
                SourceModel.TypeExprDefn.Function functionTypeExpr = (SourceModel.TypeExprDefn.Function)typeExpr;
    
                // process the domain type
                functionTypeExpr.getDomain().accept(this, arg);
                // then loop on the codomain type
                typeExpr = functionTypeExpr.getCodomain();
            }
            
            // then we traverse the return type
            inReturnType = true;
            typeExpr.accept(this, arg);
            
            return null;
        }
    }

    /**
     * A comparator to sort scoped entities so that 1) different modules are sorted according to
     * a module name ordering, and 2) the entities are sorted in a case-insensitive manner.
     *
     * @author Joseph Wong
     */
    private static final class ScopedEntityComparator implements Comparator<ScopedEntity> {

        /** Singleton instance. */
        private static final ScopedEntityComparator INSTANCE = new ScopedEntityComparator();
        
        /** Private constructor. */
        private ScopedEntityComparator() {}
        
        /** {@inheritDoc} */
        public int compare(ScopedEntity a, ScopedEntity b) {
            int moduleComparison = a.getName().getModuleName().compareTo(b.getName().getModuleName());
            if (moduleComparison != 0) {
                return moduleComparison;
            }
            
            int ignoreCaseComparison = a.getName().getUnqualifiedName().compareToIgnoreCase(b.getName().getUnqualifiedName());
            if (ignoreCaseComparison != 0) {
                return ignoreCaseComparison;
            }
            
            return a.getName().getUnqualifiedName().compareTo(b.getName().getUnqualifiedName());
        }
    }

    /**
     * A comparator to sort class instances firstly by the type class of the instance, and then by
     * the instance type's string representation.
     *
     * @author Joseph Wong
     */
    private static final class ClassInstanceComparator implements Comparator<ClassInstance> {
        /** {@inheritDoc} */
        public int compare(ClassInstance a, ClassInstance b) {
            
            QualifiedName aTypeClassName = a.getTypeClass().getName();
            QualifiedName bTypeClassName = b.getTypeClass().getName();
            
            /// first compare the type classes' names in a case-insensitive manner
            //
            int typeClassUnqualifiedNameComparison = aTypeClassName.getUnqualifiedName().compareTo(bTypeClassName.getUnqualifiedName());
            
            if (typeClassUnqualifiedNameComparison != 0) {
                return typeClassUnqualifiedNameComparison;
            } else {
                /// then comparse the type classes' names in a case-sensitive manner
                //
                int typeClassModuleNameComparison = aTypeClassName.getModuleName().compareTo(bTypeClassName.getModuleName());
                
                if (typeClassModuleNameComparison != 0) {
                    return typeClassModuleNameComparison;
                } else {
                    /// the type classes' are in fact the same, so compare the instance type:
                    //
                    String aInstanceTypeString = a.getType().toString(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
                    String bInstanceTypeString = b.getType().toString(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
                    
                    /// first compare the instance types without their constraints
                    //
                    String aInstanceTypeWithoutConstraint = aInstanceTypeString.replaceAll(".*=> ", "");
                    String bInstanceTypeWithoutConstraint = bInstanceTypeString.replaceAll(".*=> ", "");
    
                    int instanceTypeWithoutContraintComparison = aInstanceTypeWithoutConstraint.compareTo(bInstanceTypeWithoutConstraint);
                    
                    if (instanceTypeWithoutContraintComparison != 0) {
                        return instanceTypeWithoutContraintComparison;
                    } else {
                        // the instance types without their constraints do not differ, so compare
                        // the instance types in their entirety
                        //
                        int instanceTypeComparison = aInstanceTypeString.compareTo(bInstanceTypeString);
                        return instanceTypeComparison;
                    }
                }
            }
        }
    }

    /**
     * A class which traverses a type signature and calculates whether the type signature contains at least one type
     * constructor whose documentation is not generated.
     */
    final class TypeSigCheckerForTypeConsWhereDocNotGenerated extends SourceModelTraverser<Void, Void> {
        
        /**
         * Keeps track of whether the type signature contains at least one type
         * constructor whose documentation is not generated.
         */
        private boolean hasTypeConsWhereDocNotGenerated = false;
        
        /**
         * @return whether the type signature contains at least one type
         * constructor whose documentation is not generated.
         */
        boolean hasTypeConsWhereDocNotGenerated() {
            return hasTypeConsWhereDocNotGenerated;
        }
    
        /**
         * Checks the type constructor being visited, and if documentation is not generated for
         * it, set the member flag to indicate that there is at least one type constructor in the
         * type signature visited whose documentation is not generated.
         */
        @Override
        public Void visit_TypeExprDefn_TypeCons(SourceModel.TypeExprDefn.TypeCons cons, Void arg) {
            SourceModel.Name.TypeCons name = cons.getTypeConsName();
            
            // NOTE: We require that the type cons name be fully qualified with a non-null module name.
            if (!isDocForTypeConsGenerated(SourceModel.Name.Module.toModuleName(name.getModuleName()), name.getUnqualifiedName())) {
                hasTypeConsWhereDocNotGenerated = true;
            }
            
            return super.visit_TypeExprDefn_TypeCons(cons, arg);
        }
    }
    
    /**
     * A class which traverses a type signature and calculates whether the type signature contains at least one type
     * constructor that is not accepted (based on scope only).
     */
    final class TypeSigCheckerForTypeConsNotAcceptedBasedOnScopeOnly extends SourceModelTraverser<Void, Void> {
        
        /**
         * Keeps track of whether the type signature contains at least one type
         * constructor that is not accepted (based on scope only).
         */
        private boolean hasTypeConsNotAcceptedBasedOnScopeOnly = false;
        
        /**
         * @return whether the type signature contains at least one type
         * constructor that is not accepted (based on scope only).
         */
        boolean hasTypeConsNotAcceptedBasedOnScopeOnly() {
            return hasTypeConsNotAcceptedBasedOnScopeOnly;
        }
    
        /**
         * Checks the type constructor being visited, and if it is not accepted,
         * set the member flag to indicate that there is at least one type constructor in the
         * type signature visited that is not accepted (based on scope only).
         */
        @Override
        public Void visit_TypeExprDefn_TypeCons(SourceModel.TypeExprDefn.TypeCons cons, Void arg) {
            SourceModel.Name.TypeCons name = cons.getTypeConsName();
            
            // NOTE: We require that the type cons name be fully qualified with a non-null module name.
            if (!shouldAcceptTypeConsBasedOnScopeOnly(SourceModel.Name.Module.toModuleName(name.getModuleName()), name.getUnqualifiedName())) {
                hasTypeConsNotAcceptedBasedOnScopeOnly = true;
            }
            
            return super.visit_TypeExprDefn_TypeCons(cons, arg);
        }
    }
    
    /**
     * A class which traverses a type signature and calculates the minimum scope of all the type
     * constructors contained therein..
     */
    final class MinScopeCalculatorForTypeConsInTypeSig extends SourceModelTraverser<Void, Void> {
        
        /**
         * Keeps track of the minimum scope encountered.
         */
        private Scope minimumScope = Scope.PUBLIC;
        
        /**
         * @return the minimum scope encountered.
         */
        Scope getMinScope() {
            return minimumScope;
        }
        
        /**
         * Checks the type constructor being visited, comparing its scope against the existing
         * minimumScope, and setting the new minimum to be the minScope of the two.
         */
        @Override
        public Void visit_TypeExprDefn_TypeCons(SourceModel.TypeExprDefn.TypeCons cons, Void arg) {
            SourceModel.Name.TypeCons name = cons.getTypeConsName();
            
            if (minimumScope != Scope.PRIVATE) {
                // NOTE: We require that the type cons name be fully qualified with a non-null module name.
                minimumScope = minScope(minimumScope, getTypeConstructorScope(SourceModel.Name.Module.toModuleName(name.getModuleName()), name.getUnqualifiedName()));
            }
            
            return super.visit_TypeExprDefn_TypeCons(cons, arg);
        }
    }

    /**
     * Constructor for this abstract base class.
     * @param programModelManager the program model manager associated with this generator.
     * @param filter the filter to use for excluding modules and entries.
     * @param buildUsageIndices whether to build usage indices.
     * @param logger the logger for use in logging status message. Can be null.
     */
    AbstractDocumentationGenerator(ProgramModelManager programModelManager, DocumentationGenerationFilter filter, boolean buildUsageIndices, Logger logger) {
        verifyArg(programModelManager, "programModelManager");
        this.programModelManager = programModelManager;
        
        verifyArg(filter, "filter");
        this.filter = filter;
        
        this.shouldGenerateUsageIndices = buildUsageIndices;
        
        if (logger == null) {
            logger = Logger.getLogger(getClass().getName());
        }
        this.logger = logger;
        
        this.labelMaker = new DocLabelMaker();
        this.moduleIndices = new TreeMap<ModuleName, PerModuleIndices>();
        this.moduleNameToDirectlyDependentModuleNames = new LinkedHashMap<ModuleName, Set<ModuleName>>();
        this.moduleNameResolverForDocumentedModules = ModuleNameResolver.make(new HashSet<ModuleName>(filterModuleNames(Arrays.asList(programModelManager.getModuleNamesInProgram()))));
    }
    
    /**
     * Adds a direct module dependency to our mapping of (imported module, dependent modules).
     * @param importedModuleName the module that is imported.
     * @param dependentModuleName the dependent module with the import statement.
     */
    private void addDirectModuleDependency(ModuleName importedModuleName, ModuleName dependentModuleName) {
        getDirectlyDependentModuleNames(importedModuleName).add(dependentModuleName);
    }
    
    /**
     * Fetches a set of the names of the modules that import the given module. 
     * @param moduleName the name of the module whose direct dependents are to be returned.
     * @return a Set of Strings containing the names of the directly dependent modules. 
     */
    Set<ModuleName> getDirectlyDependentModuleNames(ModuleName moduleName) {
        Set<ModuleName> list = moduleNameToDirectlyDependentModuleNames.get(moduleName);
        if (list == null) {
            list = new LinkedHashSet<ModuleName>();
            moduleNameToDirectlyDependentModuleNames.put(moduleName, list);
        }
        
        return list;
    }
    
    /**
     * Fetches the per-module indices of the given module.
     * @param moduleName the name of the module whose indices are to be returned.
     * @return a PerModuleIndices instance containing the indices of the module.
     */
    private PerModuleIndices getPerModuleIndices(ModuleName moduleName) {
        PerModuleIndices indices = moduleIndices.get(moduleName);
        if (indices == null) {
            indices = new PerModuleIndices();
            moduleIndices.put(moduleName, indices);
        }
        return indices;
    }
    
    /**
     * Fetches the Type Index of the given module.
     * @param moduleName the name of the module.
     * @return a List of IndexEntry objects representing the index.
     */
    List<IndexEntry> getPerModuleTypeIndex(ModuleName moduleName) {
        return getPerModuleIndices(moduleName).typeIndex;
    }
    
    /**
     * Fetches the Functional Agent Index of the given module.
     * @param moduleName the name of the module.
     * @return a List of IndexEntry objects representing the index.
     */
    List<IndexEntry> getPerModuleFunctionalAgentIndex(ModuleName moduleName) {
        return getPerModuleIndices(moduleName).functionalAgentIndex;
    }
    
    /**
     * Fetches the Type Class Index of the given module.
     * @param moduleName the name of the module.
     * @return a List of IndexEntry objects representing the index.
     */
    List<IndexEntry> getPerModuleTypeClassIndex(ModuleName moduleName) {
        return getPerModuleIndices(moduleName).typeClassIndex;
    }
    
    /**
     * Fetches the Instance Index of the given module.
     * @param moduleName the name of the module.
     * @return a List of IndexEntry objects representing the index.
     */
    List<IndexEntry> getPerModuleInstanceIndex(ModuleName moduleName) {
        return getPerModuleIndices(moduleName).instanceIndex;
    }
    
    /**
     * Fetches the Argument Type Index of the given type constructor.
     * @param usageIndices the usage indices map.
     * @param qualifiedName the type constructor's name.
     * @param dependentModuleName the name of the dependent's module.
     * @return a TreeSet of FunctionalAgent objects whose argument types refer to the given type constructor.
     */
    TreeSet<FunctionalAgent> getArgTypeIndex(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, QualifiedName qualifiedName, ModuleName dependentModuleName) {
        return getArgAndReturnTypeIndices(usageIndices, qualifiedName, dependentModuleName).fst();
    }

    /**
     * Fetches the Argument Type Index of the given type constructor.
     * @param usageIndices the usage indices map.
     * @param moduleName the type constructor's module name.
     * @param unqualifiedName the type constructor's unqualified name.
     * @param dependentModuleName the name of the dependent's module.
     * @return a TreeSet of FunctionalAgent objects whose argument types refer to the given type constructor.
     */
    TreeSet<FunctionalAgent> getArgTypeIndex(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, ModuleName moduleName, String unqualifiedName, ModuleName dependentModuleName) {
        return getArgAndReturnTypeIndices(usageIndices, moduleName, unqualifiedName, dependentModuleName).fst();
    }

    /**
     * Fetches the pair of Argument and Return Type Indices of the given type constructor.
     * @param usageIndices the usage indices map.
     * @param qualifiedName the type constructor's name.
     * @param dependentModuleName the name of the dependent's module.
     * @return a Pair of TreeSets of FunctionalAgent objects.
     */
    private Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>> getArgAndReturnTypeIndices(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, QualifiedName qualifiedName, ModuleName dependentModuleName) {
        
        /// First, get the right map for the given type constructor
        //
        Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>> theMap = usageIndices.get(qualifiedName);
        if (theMap == null) {
            // if there is no existing map for the type constructor, create one and add it to the usageIndices
            
            theMap = new TreeMap<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>();
            usageIndices.put(qualifiedName, theMap);
        }
        
        /// Then, get the pair of argument/return type indices for the given dependent module name
        //
        Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>> thePair = theMap.get(dependentModuleName);
        if (thePair == null) {
            // if there is no existing pair for the dependent module name, create one and add it to the map for
            // the given constructor
            
            TreeSet<FunctionalAgent> argTypeIndex = new TreeSet<FunctionalAgent>(ScopedEntityComparator.INSTANCE);
            TreeSet<FunctionalAgent> returnTypeIndex = new TreeSet<FunctionalAgent>(ScopedEntityComparator.INSTANCE);
            thePair = new Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>(argTypeIndex, returnTypeIndex);
            theMap.put(dependentModuleName, thePair);
        }
        return thePair;
    }

    /**
     * Fetches the pair of Argument and Return Type Indices of the given type constructor.
     * @param usageIndices the usage indices map.
     * @param moduleName the type constructor's module name.
     * @param unqualifiedName the type constructor's unqualified name.
     * @param dependentModuleName the name of the dependent's module.
     * @return a Pair of TreeSets of FunctionalAgent objects.
     */
    private Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>> getArgAndReturnTypeIndices(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, ModuleName moduleName, String unqualifiedName, ModuleName dependentModuleName) {
        return getArgAndReturnTypeIndices(usageIndices, QualifiedName.make(moduleName, unqualifiedName), dependentModuleName);
    }
    
    /**
     * Fetches the Return Type Index of the given type constructor.
     * @param usageIndices the usage indices map.
     * @param qualifiedName the type constructor's name.
     * @param dependentModuleName the name of the dependent's module.
     * @return a TreeSet of FunctionalAgent objects whose return types refer to the given type constructor.
     */
    TreeSet<FunctionalAgent> getReturnTypeIndex(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, QualifiedName qualifiedName, ModuleName dependentModuleName) {
        return getArgAndReturnTypeIndices(usageIndices, qualifiedName, dependentModuleName).snd();
    }

    /**
     * Fetches the Return Type Index of the given type constructor.
     * @param usageIndices the usage indices map.
     * @param moduleName the type constructor's module name.
     * @param unqualifiedName the type constructor's unqualified name.
     * @param dependentModuleName the name of the dependent's module.
     * @return a TreeSet of FunctionalAgent objects whose return types refer to the given type constructor.
     */
    TreeSet<FunctionalAgent> getReturnTypeIndex(Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices, ModuleName moduleName, String unqualifiedName, ModuleName dependentModuleName) {
        return getArgAndReturnTypeIndices(usageIndices, moduleName, unqualifiedName, dependentModuleName).snd();
    }
    
    /**
     * Verifies that the argument is non-null.
     * @param arg the argument to check.
     * @param argName the name of the argument.
     * @throws NullPointerException if the argument is null.
     */
    static void verifyArg(Object arg, String argName) {
        if (arg == null) {
            throw new NullPointerException("The argument '" + argName + "' cannot be null.");
        }
    }
    
    /**
     * Takes the given entity and based on its type signature add it to the appropriate
     * argument and return type indices.
     * @param entity the entity to process.
     */
    private void processEnvEntityForArgAndReturnTypeIndices(FunctionalAgent entity) {
        if (shouldGenerateUsageIndices) {
            SourceModel.TypeSignature typeSignature = entity.getTypeExpr().toSourceModel();
            typeSignature.accept(new EnvEntityProcessorForArgAndReturnTypeIndices(entity), null);
        }
    }
    
    /**
     * This is the top-level method for generating the documentation.
     */
    void generateDoc() {
        // Obtain a list of all the module names
        List<ModuleName> allModuleNames = Arrays.asList(programModelManager.getModuleNamesInProgram());
        
        // Filter the list according to the user-specified configuration
        List<ModuleName> moduleNames = filterModuleNames(allModuleNames);
        
        // Sort the module names
        Collections.sort(moduleNames);
        
        ////
        /// Run the preparation pass through all modules, and build up the direct dependency information
        //
        for (int i = 0, n = moduleNames.size(); i < n; i++) {
            ModuleName moduleName = moduleNames.get(i);
            ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
            
            for (int j = 0, nImportedModules = moduleTypeInfo.getNImportedModules(); j < nImportedModules; j++) {
                ModuleName importedModuleName = moduleTypeInfo.getNthImportedModule(j).getModuleName();
                addDirectModuleDependency(importedModuleName, moduleName);
            }
            
            prepareGenerationForModule(moduleTypeInfo);
        }
        
        ////
        /// Run the actual documentation generation pass
        //
        beginDoc();
        
        // first portion: main documentation
        for (int i = 0, n = moduleNames.size(); i < n; i++) {
            ModuleName moduleName = moduleNames.get(i);
            ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
            logger.fine(CALDocMessages.getString("STATUS.generatingDocumentationForModule", moduleName));
            generateModuleDoc(moduleTypeInfo);
        }
        
        // optional: usage documentation
        if (shouldGenerateUsageIndices) {
            for (int i = 0, n = moduleNames.size(); i < n; i++) {
                ModuleName moduleName = moduleNames.get(i);
                ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
                logger.fine(CALDocMessages.getString("STATUS.generatingUsageDocumentationForModule", moduleName));
                generateUsageIndices(moduleTypeInfo);
            }
        }
        endDoc();
    }

    /**
     * Generates the usage indices for the given module.
     * @param moduleTypeInfo the module whose type constructors are to have their usage indices generated.
     */
    void generateUsageIndices(ModuleTypeInfo moduleTypeInfo) {
        int nTypeConstructors = moduleTypeInfo.getNTypeConstructors();
        for (int i = 0; i < nTypeConstructors; i++) {
            TypeConstructor typeCons = moduleTypeInfo.getNthTypeConstructor(i);
            
            // only generate the usage indices if the type constructor is actually to be documented
            if (filter.shouldGenerateScopedEntity(typeCons)) {
                beginTypeConsUsageDoc(typeCons);
                generateUsageIndicesForTypeConsOrTypeClass(typeCons, typeConsUsageIndices);
                endTypeConsUsageDoc(typeCons);
            }
        }
        
        int nTypeClasses = moduleTypeInfo.getNTypeClasses();
        for (int i = 0; i < nTypeClasses; i++) {
            TypeClass typeClass = moduleTypeInfo.getNthTypeClass(i);
            
            // only generate the usage indices if the type constructor is actually to be documented
            if (filter.shouldGenerateScopedEntity(typeClass)) {
                beginTypeClassUsageDoc(typeClass);
                generateUsageIndicesForTypeConsOrTypeClass(typeClass, classUsageIndices);
                endTypeClassUsageDoc(typeClass);
            }
        }
    }

    /**
     * Generates the usage indices for a type constructor or a type class.
     * @param documentedEntity the type constructor or type class whose usage is being generated.
     * @param usageIndices the usage indices map.
     */
    void generateUsageIndicesForTypeConsOrTypeClass(ScopedEntity documentedEntity, Map<QualifiedName, Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>>> usageIndices) {
        if (!(documentedEntity instanceof TypeConstructor || documentedEntity instanceof TypeClass)) {
            throw new IllegalArgumentException();
        }
        
        ModuleName moduleName = documentedEntity.getName().getModuleName();
        
        // obtain the argument and return type indices from the usageIndices map
        Map<ModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>>> theMap = usageIndices.get(documentedEntity.getName());
        
        ////
        /// generate for the defining module first
        //
        Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>> usageIndicesForTypeConsForDefiningModule = (theMap != null) ? theMap.get(moduleName) : null;
        generateUsageIndicesForDependentModule(documentedEntity, moduleName, usageIndicesForTypeConsForDefiningModule);
        
        ////
        /// then generate for the other dependent modules:
        //
        
        // get a new list for the names of the directly dependent modules, so that it can be filtered and sorted later
        Set<ModuleName> directlyDependentModuleNamesSet = getDirectlyDependentModuleNames(moduleName);
        List<ModuleName> directlyDependentModuleNames = new ArrayList<ModuleName>(directlyDependentModuleNamesSet);
        
        // filter the list of dependent modules
        directlyDependentModuleNames = filterModuleNames(directlyDependentModuleNames);
        
        // sort the list of dependent modules
        Collections.sort(directlyDependentModuleNames);
        
        // loop through each directly dependent module (excluding the defining module) and document the usages in each.
        int nDependentModules = directlyDependentModuleNames.size();
        for (int j = 0; j < nDependentModules; j++) {
            
            ModuleName dependentModuleName = directlyDependentModuleNames.get(j);
            Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>> usageIndicesForTypeCons = (theMap != null) ? theMap.get(dependentModuleName) : null;
            
            if (!dependentModuleName.equals(moduleName)) {
                generateUsageIndicesForDependentModule(documentedEntity, dependentModuleName, usageIndicesForTypeCons);
            }
        }
    }

    /**
     * Generates the usage indices section for a particular dependent module.
     * 
     * @param documentedEntity
     *            the ScopedEntity whose usage documentation is being
     *            generated.
     * @param dependentModuleName
     *            the name of the dependent module.
     * @param usageIndices
     *            the Pair of TreeSets of EnvEntities constituting the argument
     *            and return type indices affiliated with the type constructor/class
     *            and the dependent module. Can be null if the type constructor/class
     *            has neither the argument type index nor the return type index.
     */
    void generateUsageIndicesForDependentModule(ScopedEntity documentedEntity, ModuleName dependentModuleName, Pair<TreeSet<FunctionalAgent>, TreeSet<FunctionalAgent>> usageIndices) {
        if (!(documentedEntity instanceof TypeConstructor || documentedEntity instanceof TypeClass)) {
            throw new IllegalArgumentException();
        }
        
        // obtain the list of instances
        List<ClassInstance> instances = new ArrayList<ClassInstance>();
        addInstancesToList(documentedEntity, dependentModuleName, instances);
        int nInstances = instances.size();
        
        // only generate the section if there is at least something in the arg/return indices or the instance index
        if (usageIndices != null || nInstances > 0) {
            beginUsageDocGroupForDependentModule(documentedEntity, dependentModuleName);
            
            if (usageIndices != null) {
                ////
                /// generate the argument type index
                //
                TreeSet<FunctionalAgent> argTypeIndex = usageIndices.fst();
                int nArgTypeIndexEntries = argTypeIndex.size();
                
                beginUsageDocArgTypeIndex(documentedEntity, nArgTypeIndexEntries, calcMaxScopeOfScopedEntities(argTypeIndex));
                for (final FunctionalAgent entity : argTypeIndex) {
                    generateUsageDocArgTypeIndexEntry(documentedEntity, entity);
                }
                endUsageDocArgTypeIndex(documentedEntity, nArgTypeIndexEntries);
                
                ////
                /// generate the return type index
                //
                TreeSet<FunctionalAgent> returnTypeIndex = usageIndices.snd();
                int nReturnTypeIndexEntries = returnTypeIndex.size();
                
                beginUsageDocReturnTypeIndex(documentedEntity, nReturnTypeIndexEntries, calcMaxScopeOfScopedEntities(returnTypeIndex));
                for (final FunctionalAgent entity : returnTypeIndex) {
                    generateUsageDocReturnTypeIndexEntry(documentedEntity, entity);
                }
                endUsageDocReturnTypeIndex(documentedEntity, nReturnTypeIndexEntries);
            }
            
            ////
            /// generate the instance index
            //
            beginUsageDocInstanceIndex(documentedEntity, nInstances, calcMaxScopeOfClassInstances(instances));
            for (int i = 0; i < nInstances; i++) {
                ClassInstance instance = instances.get(i);
                generateUsageDocInstanceIndexEntry(documentedEntity, instance);
            }
            endUsageDocInstanceIndex(documentedEntity, nInstances);
            
            endUsageDocGroupForDependentModule(documentedEntity, dependentModuleName);
        }
    }

    /**
     * Generates the start of the type constructor usage documentation.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     */
    abstract void beginTypeConsUsageDoc(TypeConstructor documentedEntity);
    
    /**
     * Generates the start of the type class usage documentation.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     */
    abstract void beginTypeClassUsageDoc(TypeClass documentedEntity);
    
    /**
     * Generates the start of the usage documentation section for a particular dependent module.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param dependentModuleName the name of the dependent module.
     */
    abstract void beginUsageDocGroupForDependentModule(ScopedEntity documentedEntity, ModuleName dependentModuleName);

    /**
     * Generates the start of an argument type index.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param nArgTypeIndexEntries the number of index entries.
     * @param maxScopeOfArgTypeIndexEntries the maximum scope of the index entries.
     */
    abstract void beginUsageDocArgTypeIndex(ScopedEntity documentedEntity, int nArgTypeIndexEntries, Scope maxScopeOfArgTypeIndexEntries);

    /**
     * Generates an argument type index entry.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param entity the functional agent entity that is the index entry.
     */
    abstract void generateUsageDocArgTypeIndexEntry(ScopedEntity documentedEntity, FunctionalAgent entity);

    /**
     * Generates the end of an argument type index.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param nArgTypeIndexEntries the number of index entries.
     */
    abstract void endUsageDocArgTypeIndex(ScopedEntity documentedEntity, int nArgTypeIndexEntries);

    /**
     * Generates the start of a return type index.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param nReturnTypeIndexEntries the number of index entries.
     * @param maxScopeOfReturnTypeIndexEntries the maximum scope of the index entries.
     */
    abstract void beginUsageDocReturnTypeIndex(ScopedEntity documentedEntity, int nReturnTypeIndexEntries, Scope maxScopeOfReturnTypeIndexEntries);

    /**
     * Generates a return type index entry.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param entity the functional agent entity that is the index entry.
     */
    abstract void generateUsageDocReturnTypeIndexEntry(ScopedEntity documentedEntity, FunctionalAgent entity);

    /**
     * Generates the end of a return type index.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param nReturnTypeIndexEntries the number of index entries.
     */
    abstract void endUsageDocReturnTypeIndex(ScopedEntity documentedEntity, int nReturnTypeIndexEntries);

    /**
     * Generates the start of an instance index as part of the usage documentation.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param nInstances the number of instances.
     * @param maxScopeOfInstanceIndexEntries the maximum scope of the index entries.
     */
    abstract void beginUsageDocInstanceIndex(ScopedEntity documentedEntity, int nInstances, Scope maxScopeOfInstanceIndexEntries);

    /**
     * Generates an instance index entry as part of the usage documentation.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param classInstance the class instance that is the index entry.
     */
    abstract void generateUsageDocInstanceIndexEntry(ScopedEntity documentedEntity, ClassInstance classInstance);

    /**
     * Generates the end of an instance index as part of the usage documentation.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param nInstances the number of instances.
     */
    abstract void endUsageDocInstanceIndex(ScopedEntity documentedEntity, int nInstances);
    
    /**
     * Generates the end of the usage documentation section for a particular dependent module.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     * @param dependentModuleName the name of the dependent module.
     */
    abstract void endUsageDocGroupForDependentModule(ScopedEntity documentedEntity, ModuleName dependentModuleName);

    /**
     * Generates the end of the type constructor usage documentation.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     */
    abstract void endTypeConsUsageDoc(TypeConstructor documentedEntity);

    /**
     * Generates the end of the type class usage documentation.
     * @param documentedEntity the type constructor or type class whose usage is documented.
     */
    abstract void endTypeClassUsageDoc(TypeClass documentedEntity);
    
    /**
     * Filters the given list of module names and returns a new list containing
     * only the names of those modules whose documentation are generated.
     * 
     * @param moduleNames the list of module names to filter. This list is not modified by the filtering.
     * @return the filtered list.
     */
    private List<ModuleName> filterModuleNames(List<ModuleName> moduleNames) {
        List<ModuleName> filteredModuleNames = new ArrayList<ModuleName>();
        for (int i = 0, n = moduleNames.size(); i < n; i++) {
            ModuleName moduleName = moduleNames.get(i);
            
            if (filter.shouldGenerateModule(moduleName)) {
                filteredModuleNames.add(moduleName);
            }
        }
        return filteredModuleNames;
    }
    
    /**
     * Makes the necessary preparation for later generating the documentation for the given module.
     * @param moduleTypeInfo the ModuleTypeInfo of the module to prepare for.
     */
    abstract void prepareGenerationForModule(ModuleTypeInfo moduleTypeInfo);
    
    /**
     * Performs the necessary task to begin the generation of documentation.
     */
    abstract void beginDoc();
    
    /**
     * Performs the necessary task to end the generation of documentation.
     */
    abstract void endDoc();
    
    /**
     * Generates the documentation for a module.
     * @param moduleTypeInfo the ModuleTypeInfo of the module whose documentation is to be generated.
     */
    void generateModuleDoc(ModuleTypeInfo moduleTypeInfo) {
        ModuleName moduleName = moduleTypeInfo.getModuleName();
        
        ////
        /// First, collect the various entities that need to be documented, applying the user-specified filters along the way.
        //
        int nTypeConstructors = moduleTypeInfo.getNTypeConstructors();
        List<TypeConstructor> typeConstructorsList = new ArrayList<TypeConstructor>(nTypeConstructors);
        for (int i = 0; i < nTypeConstructors; i++) {
            TypeConstructor typeCons = moduleTypeInfo.getNthTypeConstructor(i);
            if (filter.shouldGenerateScopedEntity(typeCons)) {
                typeConstructorsList.add(typeCons);
            }
        }
        
        int nFunctions = moduleTypeInfo.getNFunctions();
        List<Function> functionsList = new ArrayList<Function>(nFunctions);
        for (int i = 0; i < nFunctions; i++) {
            Function function = moduleTypeInfo.getNthFunction(i);
            if (filter.shouldGenerateScopedEntity(function)) {
                functionsList.add(function);
            }
        }
        
        int nTypeClasses = moduleTypeInfo.getNTypeClasses();
        List<TypeClass> typeClassesList = new ArrayList<TypeClass>(nTypeClasses);
        for (int i = 0; i < nTypeClasses; i++) {
            TypeClass entity = moduleTypeInfo.getNthTypeClass(i);
            if (filter.shouldGenerateScopedEntity(entity)) {
                typeClassesList.add(entity);
            }
        }
        
        int nClassInstances = moduleTypeInfo.getNClassInstances();
        List<ClassInstance> classInstancesList = new ArrayList<ClassInstance>(nClassInstances);
        for (int i = 0; i < nClassInstances; i++) {
            ClassInstance classInstance = moduleTypeInfo.getNthClassInstance(i);
            if (isDocForClassInstanceGenerated(classInstance)) {
                classInstancesList.add(classInstance);
            }
        }
        
        ////
        /// Sort the entities with the appropriate comparator, so that the documentation can generated in an easy-to-understand order.
        //
        Collections.sort(typeConstructorsList, ScopedEntityComparator.INSTANCE);
        Collections.sort(functionsList, ScopedEntityComparator.INSTANCE);
        Collections.sort(typeClassesList, ScopedEntityComparator.INSTANCE);
        Collections.sort(classInstancesList, new ClassInstanceComparator());

        TypeConstructor[] typeConstructors = typeConstructorsList.toArray(new TypeConstructor[0]);
        Function[] functions = functionsList.toArray(new Function[0]);
        TypeClass[] typeClasses = typeClassesList.toArray(new TypeClass[0]);
        ClassInstance[] classInstances = classInstancesList.toArray(new ClassInstance[0]);
        
        ////
        /// Actually generate the documentation.
        //
        beginModuleDoc(moduleTypeInfo, typeConstructors.length, functions.length, typeClasses.length, classInstances.length);
        generateModuleHeaderSection(moduleTypeInfo);
        generateModuleOverviewSection(typeConstructors, functions, typeClasses, classInstances);
        generateModuleDetailsSection(moduleName, typeConstructors, functions, typeClasses, classInstances);
        endModuleDoc(typeConstructors.length, functions.length, typeClasses.length, classInstances.length);
    }
    
    /**
     * Generates the start of the documentation for a module.
     * @param moduleTypeInfo the ModuleTypeInfo of the module.
     * @param nTypeConstructors the number of type constructors to be documented (not necessarily equal to the number of all type constructors in the module).
     * @param nFunctions the number of functions to be documented (not necessarily equal to the number of all functions in the module).
     * @param nTypeClasses the number of type classes to be documented (not necessarily equal to the number of all type classes in the module).
     * @param nClassInstances the number of class instances to be documented (not necessarily equal to the number of all class instances in the module).
     */
    abstract void beginModuleDoc(ModuleTypeInfo moduleTypeInfo, int nTypeConstructors, int nFunctions, int nTypeClasses, int nClassInstances);
    
    /**
     * Generates the end of the documentation for a module.
     * @param nTypeConstructors the number of type constructors to be documented (not necessarily equal to the number of all type constructors in the module).
     * @param nFunctions the number of functions to be documented (not necessarily equal to the number of all functions in the module).
     * @param nTypeClasses the number of type classes to be documented (not necessarily equal to the number of all type classes in the module).
     * @param nClassInstances the number of class instances to be documented (not necessarily equal to the number of all class instances in the module).
     */
    abstract void endModuleDoc(int nTypeConstructors, int nFunctions, int nTypeClasses, int nClassInstances);
    
    /**
     * Generates the header section of the documentation for a module.
     * @param moduleTypeInfo the ModuleTypeInfo of the module.
     */
    void generateModuleHeaderSection(ModuleTypeInfo moduleTypeInfo) {
        
        ModuleName moduleName = moduleTypeInfo.getModuleName();
        
        generateModuleDescription(moduleTypeInfo);
        
        ////
        /// Generate the list of imported modules
        //
        
        // note: we don't filter out the modules for which documentation is not generated,
        // since it would be misleading to say that a module A imports no modules (not
        // even Prelude) if it is the only module whose documentation is generated,
        // when in fact it imports Prelude and also some other modules B, C, D.
        
        int nImportedModules = moduleTypeInfo.getNImportedModules();
        beginImportedModulesList(nImportedModules);
        for (int i = 0; i < nImportedModules; i++) {
            generateImportedModule(moduleTypeInfo.getNthImportedModule(i), i, nImportedModules);
        }
        endImportedModulesList(nImportedModules);
        
        ////
        /// Generate the list of friend modules
        //
        
        // note: we don't filter out the modules for which documentation is not generated,
        // since friend modules are granted special privileges (to access protected entities)
        // and it would be good to know the entire set of modules which are granted these privileges
        // whether they are included in the documentation or not.
        
        int nFriendModules = moduleTypeInfo.getNFriendModules();
        beginFriendModulesList(nFriendModules);
        for (int i = 0; i < nFriendModules; i++) {
            generateFriendModule(moduleTypeInfo.getNthFriendModule(i), i, nFriendModules);
        }
        endFriendModulesList(nFriendModules);
        
        ////
        /// Generate the list of directly dependent modules
        //
        
        Set<ModuleName> directlyDependentModuleNamesSet = getDirectlyDependentModuleNames(moduleName);
        List<ModuleName> directlyDependentModuleNames = new ArrayList<ModuleName>(directlyDependentModuleNamesSet);
        
        // filter the list of dependent modules
        directlyDependentModuleNames = filterModuleNames(directlyDependentModuleNames);
        
        // sort the list of dependent modules in lexicographical order
        Collections.sort(directlyDependentModuleNames);
        
        int nDirectlyDependentModules = directlyDependentModuleNames.size();
        
        beginDirectlyDependentModulesList(nDirectlyDependentModules);
        for (int i = 0; i < nDirectlyDependentModules; i++) {
            generateDirectlyDependentModule(directlyDependentModuleNames.get(i), i, nDirectlyDependentModules);
        }
        endDirectlyDependentModulesList(nDirectlyDependentModules);

        ////
        /// Generate the list of indirectly dependent modules
        //
        
        List<ModuleName> indirectlyDependentModulesList = new ArrayList<ModuleName>();
        for (final ModuleName dependentModuleName : programModelManager.getDependentModuleNames(moduleName)) {
            if (!directlyDependentModuleNamesSet.contains(dependentModuleName)) {
                indirectlyDependentModulesList.add(dependentModuleName);
            }
        }
        
        // filter the list of dependent modules
        indirectlyDependentModulesList = filterModuleNames(indirectlyDependentModulesList);
        
        // sort the list of dependent modules in lexicographical order
        Collections.sort(indirectlyDependentModulesList);

        int nIndirectlyDependentModules = indirectlyDependentModulesList.size();
        
        beginIndirectlyDependentModulesList(nIndirectlyDependentModules);
        for (int i = 0; i < nIndirectlyDependentModules; i++) {
            generateIndirectlyDependentModule(indirectlyDependentModulesList.get(i), i, nIndirectlyDependentModules);
        }
        endIndirectlyDependentModulesList(nIndirectlyDependentModules);
    }
     
    /**
     * Generates the start of the imported modules list.
     * @param nImportedModules the number of imported modules.
     */
    abstract void beginImportedModulesList(int nImportedModules);

    /**
     * Generates an imported modules list entry.
     * @param moduleTypeInfo the ModuleTypeInfo of the imported module.
     * @param index the position of this entry in its enclosing list.
     * @param nImportedModules the number of entries in the list.
     */
    abstract void generateImportedModule(ModuleTypeInfo moduleTypeInfo, int index, int nImportedModules);

    /**
     * Generates the end of the imported modules list.
     * @param nImportedModules the number of imported modules.
     */
    abstract void endImportedModulesList(int nImportedModules);
    
    /**
     * Generates the start of the friend modules list.
     * @param nFriendModules the number of friend modules.
     */
    abstract void beginFriendModulesList(int nFriendModules);

    /**
     * Generates an friend modules list entry.
     * @param moduleName the name of the friend module.
     * @param index the position of this entry in its enclosing list.
     * @param nFriendModules the number of entries in the list.
     */
    abstract void generateFriendModule(ModuleName moduleName, int index, int nFriendModules);

    /**
     * Generates the end of the friend modules list.
     * @param nFriendModules the number of friend modules.
     */
    abstract void endFriendModulesList(int nFriendModules);
    
    /**
     * Generates the start of the directly dependent modules list.
     * @param nDependentModules the number of dependent modules.
     */
    abstract void beginDirectlyDependentModulesList(int nDependentModules);

    /**
     * Generates a directly dependent modules list entry.
     * @param moduleName the name of the dependent module.
     * @param index the position of this entry in its enclosing list.
     * @param nDependentModules the number of entries in the list.
     */
    abstract void generateDirectlyDependentModule(ModuleName moduleName, int index, int nDependentModules);

    /**
     * Generates the end of the directly dependent modules list.
     * @param nDependentModules the number of dependent modules.
     */
    abstract void endDirectlyDependentModulesList(int nDependentModules);
    
    /**
     * Generates the start of the indirectly dependent modules list.
     * @param nDependentModules the number of dependent modules.
     */
    abstract void beginIndirectlyDependentModulesList(int nDependentModules);

    /**
     * Generates an indirectly dependent modules list entry.
     * @param moduleName the name of the dependent module.
     * @param index the position of this entry in its enclosing list.
     * @param nDependentModules the number of entries in the list.
     */
    abstract void generateIndirectlyDependentModule(ModuleName moduleName, int index, int nDependentModules);

    /**
     * Generates the end of the indirectly dependent modules list.
     * @param nDependentModules the number of dependent modules.
     */
    abstract void endIndirectlyDependentModulesList(int nDependentModules);
    
    /**
     * Generates the description of the given module.
     * @param moduleTypeInfo the ModuleTypeInfo of the module.
     */
    abstract void generateModuleDescription(ModuleTypeInfo moduleTypeInfo);
    
    /**
     * Generates the overview section of the documentation for the given module.
     * @param typeConstructors the array of type constructors to be documented.
     * @param functions the array of functions to be documented.
     * @param typeClasses the array of type classes to be documented.
     * @param classInstances the array of class instances to be documented.
     */
    void generateModuleOverviewSection(TypeConstructor[] typeConstructors, Function[] functions, TypeClass[] typeClasses, ClassInstance[] classInstances) {
        
        int nTypeConstructors = typeConstructors.length;
        int nFunctions = functions.length;
        int nTypeClasses = typeClasses.length;
        int nClassInstances = classInstances.length;
        
        // determine whether the entire overview section is empty
        boolean isOverviewSectionEmpty = (nTypeConstructors == 0) && (nFunctions == 0) && (nTypeClasses == 0) && (nClassInstances == 0);
        
        beginModuleOverviewSection(isOverviewSectionEmpty);

        ////
        /// generate the type constructor section of the overview
        //
        beginTypeConsOverviewSection(nTypeConstructors, calcMaxScopeOfScopedEntities(typeConstructors));
        for (int i = 0; i < nTypeConstructors; i++) {
            generateTypeConsOverview(typeConstructors[i]);
        }
        endTypeConsOverviewSection(nTypeConstructors);
        
        ////
        /// generate the functions section of the overview
        //
        beginFunctionsOverviewSection(nFunctions, calcMaxScopeOfScopedEntities(functions));
        for (int i = 0; i < nFunctions; i++) {
            generateFunctionOverview(functions[i]);
        }
        endFunctionsOverviewSection(nFunctions);
        
        ////
        /// generate the type class section of the overview
        //
        beginTypeClassOverviewSection(nTypeClasses, calcMaxScopeOfScopedEntities(typeClasses));
        for (int i = 0; i < nTypeClasses; i++) {
            generateTypeClassOverview(typeClasses[i]);
        }
        endTypeClassOverviewSection(nTypeClasses);
        
        ////
        /// generate the class instances section of the overview
        //
        beginClassInstancesOverviewSection(nClassInstances, calcMaxScopeOfClassInstances(classInstances));
        for (int i = 0; i < nClassInstances; i++) {
            generateClassInstanceOverview(classInstances[i]);
        }
        endClassInstancesOverviewSection(nClassInstances);
        
        endModuleOverviewSection(isOverviewSectionEmpty);
    }
    
    /**
     * Generates the start of the module overview portion of the documentation.
     * @param isOverviewSectionEmpty whether the section is actually empty.
     */
    abstract void beginModuleOverviewSection(boolean isOverviewSectionEmpty);
    
    /**
     * Generates the end of the module overview portion of the documentation.
     * @param isOverviewSectionEmpty whether the section is actually empty.
     */
    abstract void endModuleOverviewSection(boolean isOverviewSectionEmpty);

    /**
     * Generates the start of the type constructors section of the module overview.
     * @param nTypeConstructors the number of type constructors to be documented.
     * @param maxScopeOfTypeConstructors the maximum scope of the type constructors.
     */
    abstract void beginTypeConsOverviewSection(int nTypeConstructors, Scope maxScopeOfTypeConstructors);

    /**
     * Generates the end of the type constructors section of the module overview.
     * @param nTypeConstructors the number of type constructors to be documented.
     */
    abstract void endTypeConsOverviewSection(int nTypeConstructors);
    
    /**
     * Generates the start of the functions section of the module overview.
     * @param nFunctions the number of functions to be documented.
     * @param maxScopeOfFunctions the maximum scope of the functions.
     */
    abstract void beginFunctionsOverviewSection(int nFunctions, Scope maxScopeOfFunctions);

    /**
     * Generates the end of the functions section of the module overview.
     * @param nFunctions the number of functions to be documented.
     */
    abstract void endFunctionsOverviewSection(int nFunctions);

    /**
     * Generates the start of the type classes section of the module overview.
     * @param nTypeClasses the number of type classes to be documented.
     * @param maxScopeOfTypeClasses the maximum scope of the type classes.
     */
    abstract void beginTypeClassOverviewSection(int nTypeClasses, Scope maxScopeOfTypeClasses);

    /**
     * Generates the end of the type classes section of the module overview.
     * @param nTypeClasses the number of type classes to be documented.
     */
    abstract void endTypeClassOverviewSection(int nTypeClasses);
    
    /**
     * Generates the start of the class instances section of the module overview.
     * @param nClassInstances the number of class instances to be documented.
     * @param maxScopeOfClassInstances the maximum scope of the class instances.
     */
    abstract void beginClassInstancesOverviewSection(int nClassInstances, Scope maxScopeOfClassInstances);

    /**
     * Generates the end of the class instances section of the module overview.
     * @param nClassInstances the number of class instances to be documented.
     */
    abstract void endClassInstancesOverviewSection(int nClassInstances);
    
    /**
     * Generates an overview entry for a type constructor.
     * @param typeConstructor the type constructor to be documented.
     */
    void generateTypeConsOverview(TypeConstructor typeConstructor) {
        
        ////
        /// Generate information about the type constructor itself
        //
        generateTypeConsOverviewHeader(typeConstructor);
        
        ////
        /// Create a list of the data constructors to be documented, applying the user-specified filters along the way.
        //
        int nDataConstructors = typeConstructor.getNDataConstructors();
        List<DataConstructor> dataConstructorsList = new ArrayList<DataConstructor>(nDataConstructors);
        for (int i = 0; i < nDataConstructors; i++) {
            DataConstructor entity = typeConstructor.getNthDataConstructor(i);
            if (filter.shouldGenerateScopedEntity(entity)) {
                dataConstructorsList.add(entity);
            }
        }
        
        ////
        /// Generate the list of data constructors
        //
        int nDataConstructorsToGenerate = dataConstructorsList.size();
        beginDataConsOverviewList(nDataConstructorsToGenerate, calcMaxScopeOfScopedEntities(dataConstructorsList));
        for (int i = 0; i < nDataConstructorsToGenerate; i++) {
            generateDataConsOverview(dataConstructorsList.get(i));
        }
        endDataConsOverviewList(nDataConstructorsToGenerate);
        
        generateTypeConsOverviewFooter();
    }

    /**
     * Generates the header portion of the overview entry for a type constructor.
     * @param typeConstructor the type constructor to be documented.
     */
    abstract void generateTypeConsOverviewHeader(TypeConstructor typeConstructor);
    
    /**
     * Generates the start of the list of data constructors for the overview entry for a type constructor.
     * @param nDataConstructors the number of data constructors to be documented.
     * @param maxScopeOfDataConstructors the maximum scope of the data constructors.
     */
    abstract void beginDataConsOverviewList(int nDataConstructors, Scope maxScopeOfDataConstructors);
    
    /**
     * Generates an overview entry for a data constructor.
     * @param dataConstructor the data constructor to be documented.
     */
    abstract void generateDataConsOverview(DataConstructor dataConstructor);

    /**
     * Generates the end of the list of data constructors for the overview entry for a type constructor.
     * @param nDataConstructors the number of data constructors to be documented.
     */
    abstract void endDataConsOverviewList(int nDataConstructors);
    
    /**
     * Generates the footer portion of the overview entry for a type constructor.
     */
    abstract void generateTypeConsOverviewFooter();
    
    /**
     * Generates an overview entry for a function.
     * @param function the function to be documented.
     */
    abstract void generateFunctionOverview(Function function);

    /**
     * Generates an overview entry for a type class.
     * @param typeClass the type class to be documented.
     */
    void generateTypeClassOverview(TypeClass typeClass) {
        
        ////
        /// Generate information about the type class itself
        //
        generateTypeClassOverviewHeader(typeClass);
        
        ////
        /// Create a list of the class methods to be documented, applying the user-specified filters along the way.
        //
        int nClassMethods = typeClass.getNClassMethods();
        List<ClassMethod> classMethodsList = new ArrayList<ClassMethod>(nClassMethods);
        for (int i = 0; i < nClassMethods; i++) {
            ClassMethod entity = typeClass.getNthClassMethod(i);
            if (filter.shouldGenerateScopedEntity(entity)) {
                classMethodsList.add(entity);
            }
        }
        
        ////
        /// Generate the list of class methods
        //
        int nClassMethodsToGenerate = classMethodsList.size();
        beginClassMethodOverviewList(nClassMethodsToGenerate);
        for (int i = 0; i < nClassMethodsToGenerate; i++) {
            generateClassMethodOverview(classMethodsList.get(i));
        }
        endClassMethodOverviewList(nClassMethodsToGenerate);
        
        generateTypeClassOverviewFooter();
    }

    /**
     * Generates the header portion of the overview entry for a type class.
     * @param typeClass the type class to be documented.
     */
    abstract void generateTypeClassOverviewHeader(TypeClass typeClass);
    
    /**
     * Generates the start of the list of class methods for the overview entry for a type class.
     * @param nClassMethods the number of class methods to be documented.
     */
    abstract void beginClassMethodOverviewList(int nClassMethods);

    /**
     * Generates an overview entry for a class method.
     * @param classMethod the class method to be documented.
     */
    abstract void generateClassMethodOverview(ClassMethod classMethod);
    
    /**
     * Generates the end of the list of class methods for the overview entry for a type class.
     * @param nClassMethods the number of class methods to be documented.
     */
    abstract void endClassMethodOverviewList(int nClassMethods);
    
    /**
     * Generates the footer portion of the overview entry for a type class.
     */
    abstract void generateTypeClassOverviewFooter();

    /**
     * Generates an overview entry for a class instance.
     * @param classInstance the class instances to be documented.
     */
    void generateClassInstanceOverview(ClassInstance classInstance) {
        
        ////
        /// Generate information about the class instance itself
        //
        generateClassInstanceOverviewHeader(classInstance);
        
        ////
        /// Generate the list of instance methods
        /// (no filtering necessary: if the class instance is to be generated, then so should all its methods)
        //
        int nInstanceMethods = classInstance.getNInstanceMethods();
        beginInstanceMethodOverviewList(nInstanceMethods);
        TypeClass typeClass = classInstance.getTypeClass();
        for (int i = 0; i < nInstanceMethods; i++) {
            generateInstanceMethodOverview(classInstance, typeClass.getNthClassMethod(i).getName().getUnqualifiedName());
        }
        endInstanceMethodOverviewList(nInstanceMethods);
        
        generateClassInstanceOverviewFooter();
    }

    /**
     * Generates the header portion of the overview entry for a class instance.
     * @param classInstance the class instance to be documented.
     */
    abstract void generateClassInstanceOverviewHeader(ClassInstance classInstance);
        
    /**
     * Generates the start of the list of instance methods for the overview entry for a class instance.
     * @param nInstanceMethods the number of instance methods to be documented.
     */
    abstract void beginInstanceMethodOverviewList(int nInstanceMethods);
    
    /**
     * Generates an overview entry for an instance method.
     * @param classInstance the class instance of the instance method.
     * @param methodName the name of the method.
     */
    abstract void generateInstanceMethodOverview(ClassInstance classInstance, String methodName);

    /**
     * Generates the end of the list of instance methods for the overview entry for a class instance.
     * @param nInstanceMethods the number of instance methods to be documented.
     */
    abstract void endInstanceMethodOverviewList(int nInstanceMethods);
    
    /**
     * Generates the footer portion of the overview entry for a class instance.
     */
    abstract void generateClassInstanceOverviewFooter();

    /**
     * Generates the details section of the documentation for a module.
     * @param moduleName the name of the module.
     * @param typeConstructors the array of type constructors to be documented.
     * @param functions the array of functions to be documented.
     * @param typeClasses the array of type classes to be documented.
     * @param classInstances the array of class instances to be documented.
     */
    void generateModuleDetailsSection(ModuleName moduleName, TypeConstructor[] typeConstructors, Function[] functions, TypeClass[] typeClasses, ClassInstance[] classInstances) {
        generateTypeConsDocSection(moduleName, typeConstructors);
        generateFunctionsDocSection(moduleName, functions);
        generateTypeClassesDocSection(moduleName, typeClasses);
        generateClassInstancesDocSection(moduleName, classInstances);
    }

    /**
     * Generates the type constructors portion of the detailed documentation.
     * @param moduleName the name of the module.
     * @param typeConstructors the array of type cnostructors to be documented.
     */
    void generateTypeConsDocSection(ModuleName moduleName, TypeConstructor[] typeConstructors) {
        List<IndexEntry> perModuleTypeIndex = getPerModuleTypeIndex(moduleName);
        
        ////
        /// Generate the documentation for each type constructor, and add each to the main index.
        //
        int nTypeConstructors = typeConstructors.length;
        beginTypeConsDocSection(nTypeConstructors, calcMaxScopeOfScopedEntities(typeConstructors));
        for (int i = 0; i < nTypeConstructors; i++) {
            
            TypeConstructor typeConstructor = typeConstructors[i];
            QualifiedName name = typeConstructor.getName();
            perModuleTypeIndex.add(new IndexEntry(name.getUnqualifiedName(), labelMaker.getLabel(typeConstructor), typeConstructor.getScope(), IndexEntry.Kind.TYPE));
            
            generateTypeConsDoc(typeConstructor, i);
        }
        endTypeConsDocSection(nTypeConstructors);
    }

    /**
     * Generates the functions portion of the detailed documentation.
     * @param moduleName the name of the module.
     * @param functions the array of functions to be documented.
     */
    void generateFunctionsDocSection(ModuleName moduleName, Function[] functions) {
        List<IndexEntry> perModuleFunctionalAgentIndex = getPerModuleFunctionalAgentIndex(moduleName);
        
        ////
        /// Generate the documentation for each type constructor, and add each to the main index
        /// and the argument type and return type indices.
        //
        int nFunctions = functions.length;
        beginFunctionsDocSection(nFunctions, calcMaxScopeOfScopedEntities(functions));
        for (int i = 0; i < nFunctions; i++) {
            
            Function function = functions[i];
            QualifiedName name = function.getName();
            perModuleFunctionalAgentIndex.add(new IndexEntry(name.getUnqualifiedName(), labelMaker.getLabel(function), function.getScope(), IndexEntry.Kind.FUNCTIONAL_AGENT));
            
            processEnvEntityForArgAndReturnTypeIndices(function);
            
            generateFunctionDoc(function, i);
        }
        endFunctionsDocSection(nFunctions);
    }

    /**
     * Generates the type classes portion of the detailed documentation.
     * @param moduleName the name of the module.
     * @param typeClasses the array of type classes to be documented.
     */
    void generateTypeClassesDocSection(ModuleName moduleName, TypeClass[] typeClasses) {
        List<IndexEntry> perModuleClassIndex = getPerModuleTypeClassIndex(moduleName);
        
        ////
        /// Generate the documentation for each type class, and add each to the main index.
        //
        int nTypeClasses = typeClasses.length;
        beginTypeClassesDocSection(nTypeClasses, calcMaxScopeOfScopedEntities(typeClasses));
        for (int i = 0; i < nTypeClasses; i++) {
            
            TypeClass typeClass = typeClasses[i];
            QualifiedName name = typeClass.getName();
            perModuleClassIndex.add(new IndexEntry(name.getUnqualifiedName(), labelMaker.getLabel(typeClass), typeClass.getScope(), IndexEntry.Kind.TYPE_CLASS));
            
            generateTypeClassDoc(typeClass, i);
        }
        endTypeClassesDocSection(nTypeClasses);
    }

    /**
     * Generates the class instances portion of the detailed documentation.
     * @param moduleName the name of the module.
     * @param classInstances the array of class instances to be documented.
     */
    void generateClassInstancesDocSection(ModuleName moduleName, ClassInstance[] classInstances) {
        List<IndexEntry> perModuleInstanceIndex = getPerModuleInstanceIndex(moduleName);
        
        ////
        /// Generate the documentation for each class instance, and add each to the main index.
        //
        int nClassInstances = classInstances.length;
        beginClassInstancesDocSection(nClassInstances, classInstances, calcMaxScopeOfClassInstances(classInstances));
        for (int i = 0; i < nClassInstances; i++) {

            ClassInstance classInstance = classInstances[i];
            perModuleInstanceIndex.add(new IndexEntry(getClassInstanceDisplayName(classInstance), labelMaker.getLabel(classInstance), minScopeForInstanceClassAndInstanceType(classInstance), IndexEntry.Kind.INSTANCE));
            
            generateClassInstanceDoc(classInstance, i);
        }
        endClassInstancesDocSection(nClassInstances);
    }
    
    /**
     * Returns the display name for a class instance.
     * @param classInstance the class instance.
     * @return the display name for the class instance.
     */
    String getClassInstanceDisplayName(ClassInstance classInstance) {
        return classInstance.getNameWithContext(ScopedEntityNamingPolicy.UNQUALIFIED);
    }
    
    /**
     * Generates the start of the type constructor section of the detailed documentation.
     * @param nTypeConstructors the number of type constructors to be documented.
     * @param maxScopeOfTypeConstructors the maximum scope of the type constructors.
     */
    abstract void beginTypeConsDocSection(int nTypeConstructors, Scope maxScopeOfTypeConstructors);

    /**
     * Generates the detailed documentation for a type constructor.
     * @param typeConstructor the type constructor to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    void generateTypeConsDoc(TypeConstructor typeConstructor, int index) {
        ////
        /// Generate documentation about the type constructor itself.
        //
        generateTypeConsDocHeader(typeConstructor, index);
        
        ////
        /// Create a list of the data constructors to be documented, applying the user-specified filters along the way.
        //
        int nDataConstructors = typeConstructor.getNDataConstructors();
        List<DataConstructor> dataConstructorsList = new ArrayList<DataConstructor>(nDataConstructors);
        for (int i = 0; i < nDataConstructors; i++) {
            DataConstructor entity = typeConstructor.getNthDataConstructor(i);
            if (filter.shouldGenerateScopedEntity(entity)) {
                dataConstructorsList.add(entity);
            }
        }

        ModuleName moduleName = typeConstructor.getName().getModuleName();

        ////
        /// Generate documentation for each data constructor of this type constructor, and add each to the main index
        /// and the argument type and return type indices.
        //
        List<IndexEntry> perModuleFunctionalAgentIndex = getPerModuleFunctionalAgentIndex(moduleName);
        
        int nDataConstructorsToGenerate = dataConstructorsList.size();
        beginDataConsDocList(nDataConstructorsToGenerate, calcMaxScopeOfScopedEntities(dataConstructorsList));
        for (int i = 0; i < nDataConstructorsToGenerate; i++) {
            
            DataConstructor dataConstructor = dataConstructorsList.get(i);
            QualifiedName name = dataConstructor.getName();
            perModuleFunctionalAgentIndex.add(new IndexEntry(name.getUnqualifiedName(), labelMaker.getLabel(dataConstructor), dataConstructor.getScope(), IndexEntry.Kind.FUNCTIONAL_AGENT));
            
            processEnvEntityForArgAndReturnTypeIndices(dataConstructor);
            
            generateDataConsDoc(dataConstructor, i);
        }
        endDataConsDocList(nDataConstructorsToGenerate);
        
        ////
        /// Identify the known instances of this type constructor.
        //
        Set<ModuleName> namesOfModulesToSearchForInstances = getDirectlyDependentModuleNames(moduleName);
        namesOfModulesToSearchForInstances.add(moduleName);
        
        List<ClassInstance> knownInstances = new ArrayList<ClassInstance>();
        
        for (final ModuleName nameOfModuleToSearchForInstances : namesOfModulesToSearchForInstances) {
            addInstancesToList(typeConstructor, nameOfModuleToSearchForInstances, knownInstances);
        }
        
        // sort the known instances according to the standard order
        Collections.sort(knownInstances, new ClassInstanceComparator());
        
        ////
        /// Generate references to known instances of this type constructor.
        //
        int nKnownInstances = knownInstances.size();
        beginKnownInstancesList(nKnownInstances, calcMaxScopeOfClassInstances(knownInstances));
        for (int i = 0; i < nKnownInstances; i++) {
            generateKnownInstance(knownInstances.get(i), i);
        }
        endKnownInstancesList(nKnownInstances);
        
        generateTypeConsDocFooter(typeConstructor, index);
    }
    
    /**
     * Add the instances for the specified type constructor or type class found in the specified module into the given list.
     * @param documentedEntity the type constructor or type class whose instances are to be returned.
     * @param nameOfModuleToSearchForInstances the module in which to search for instances.
     * @param instances the List to which instances are to be added.
     */
    private void addInstancesToList(ScopedEntity documentedEntity, ModuleName nameOfModuleToSearchForInstances, List<ClassInstance> instances) {
        if (documentedEntity instanceof TypeConstructor) {
            addInstancesOfTypeConsToList((TypeConstructor)documentedEntity, nameOfModuleToSearchForInstances, instances);
        } else if (documentedEntity instanceof TypeClass) {
            addIntancesOfTypeClassToList((TypeClass)documentedEntity, nameOfModuleToSearchForInstances, instances);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Add the instances for the specified type constructor found in the specified module into the given list.
     * @param typeConstructor the type constructor whose instances are to be returned.
     * @param nameOfModuleToSearchForInstances the module in which to search for instances.
     * @param instances the List to which instances are to be added.
     */
    private void addInstancesOfTypeConsToList(TypeConstructor typeConstructor, ModuleName nameOfModuleToSearchForInstances, List<ClassInstance> instances) {
        ModuleTypeInfo moduleToSearchForInstances = programModelManager.getModuleTypeInfo(nameOfModuleToSearchForInstances);
        
        int nClassInstances = moduleToSearchForInstances.getNClassInstances();
        for (int i = 0; i < nClassInstances; i++) {
            ClassInstance classInstance = moduleToSearchForInstances.getNthClassInstance(i);
            
            if (classInstance.isTypeConstructorInstance()) {
                ClassInstanceIdentifier.TypeConstructorInstance identifier =
                    (ClassInstanceIdentifier.TypeConstructorInstance)classInstance.getIdentifier();
                
                ////
                /// Only add the instance to the list of know instances if both the type class
                /// and the type constructors in the instance type have their documentation generated.
                //
                if (identifier.getTypeConsName().equals(typeConstructor.getName()) &&
                    isDocForClassInstanceGenerated(classInstance)) {
                    
                    instances.add(classInstance);
                }
            }
        }
    }
    
    /**
     * Add the instances for the specified type class found in the specified module into the given list.
     * @param typeClass the type class whose instances are to be returned.
     * @param nameOfModuleToSearchForInstances the module in which to search for instances.
     * @param instances the List to which instances are to be added.
     */
    private void addIntancesOfTypeClassToList(TypeClass typeClass, ModuleName nameOfModuleToSearchForInstances, List<ClassInstance> instances) {
        ModuleTypeInfo moduleToSearchForInstances = programModelManager.getModuleTypeInfo(nameOfModuleToSearchForInstances);
        
        int nClassInstances = moduleToSearchForInstances.getNClassInstances();
        for (int i = 0; i < nClassInstances; i++) {
            ClassInstance classInstance = moduleToSearchForInstances.getNthClassInstance(i);
            
            ////
            /// Only add the instance to the list of know instances if both the type class
            /// and the type constructors in the instance type have their documentation generated.
            //
            if (classInstance.getTypeClass().getName().equals(typeClass.getName()) &&
                    isDocForClassInstanceGenerated(classInstance)) {
                
                instances.add(classInstance);
            }
        }
    }

    /**
     * Generate the header portion of the detailed documentation for a type constructor.
     * @param typeConstructor the type constructor to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateTypeConsDocHeader(TypeConstructor typeConstructor, int index);
    
    /**
     * Generates the start of the list of data constructors for the detailed documentation for a type constructor.
     * @param nDataConstructors the number of data constructors to be documented.
     * @param maxScopeOfDataConstructors the maximum scope of the data constructors.
     */
    abstract void beginDataConsDocList(int nDataConstructors, Scope maxScopeOfDataConstructors);
    
    /**
     * Generates the detailed documentation for a data constructor.
     * @param dataConstructor the data constructor to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateDataConsDoc(DataConstructor dataConstructor, int index);

    /**
     * Generates the end of the list of data constructors for the detailed documentation for a type constructor.
     * @param nDataConstructors the number of data constructors to be documented.
     */
    abstract void endDataConsDocList(int nDataConstructors);

    /**
     * Generate the footer portion of the detailed documentation for a type constructor.
     * @param typeConstructor the type constructor to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateTypeConsDocFooter(TypeConstructor typeConstructor, int index);
    
    /**
     * Generates the end of the type constructor section of the detailed documentation.
     * @param nTypeConstructors the number of type constructors to be documented.
     */
    abstract void endTypeConsDocSection(int nTypeConstructors);
    
    /**
     * Generates the start of the functions section of the detailed documentation.
     * @param nFunctions the number of functions to be documented.
     * @param maxScopeOfFunctions the maximum scope of the functions.
     */
    abstract void beginFunctionsDocSection(int nFunctions, Scope maxScopeOfFunctions);
    
    /**
     * Generates the detailed documentation for a function.
     * @param function the function to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateFunctionDoc(Function function, int index);
    
    /**
     * Generates the end of the functions section of the detailed documentation.
     * @param nFunctions the number of functions to be documented.
     */
    abstract void endFunctionsDocSection(int nFunctions);
    
    /**
     * Generates the start of the type class section of the detailed documentation.
     * @param nTypeClasses the number of type classes to be documented.
     * @param maxScopeOfTypeClasses the maximum scope of the type classes.
     */
    abstract void beginTypeClassesDocSection(int nTypeClasses, Scope maxScopeOfTypeClasses);

    /**
     * Generates the detailed documentation for a type class.
     * @param typeClass the type class to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    void generateTypeClassDoc(TypeClass typeClass, int index) {
        ////
        /// Generate documentation about the type class itself.
        //
        generateTypeClassDocHeader(typeClass, index);
        
        ////
        /// Create a list of the type classes to be documented, applying the user-specified filters along the way.
        //
        int nClassMethods = typeClass.getNClassMethods();
        List<ClassMethod> classMethodsList = new ArrayList<ClassMethod>(nClassMethods);
        for (int i = 0; i < nClassMethods; i++) {
            ClassMethod entity = typeClass.getNthClassMethod(i);
            if (filter.shouldGenerateScopedEntity(entity)) {
                classMethodsList.add(entity);
            }
        }
        
        ModuleName moduleName = typeClass.getName().getModuleName();
        
        ////
        /// Generate documentation for each method of this class, and add each to the main index
        /// and the argument type and return type indices.
        //
        List<IndexEntry> perModuleFunctionalAgentIndex = getPerModuleFunctionalAgentIndex(moduleName);
        
        int nClassMethodsToGenerate = classMethodsList.size();
        beginClassMethodDocList(nClassMethodsToGenerate);
        for (int i = 0; i < nClassMethodsToGenerate; i++) {
            
            ClassMethod classMethod = classMethodsList.get(i);
            QualifiedName classMethodName = classMethod.getName();
            perModuleFunctionalAgentIndex.add(new IndexEntry(classMethodName.getUnqualifiedName(), labelMaker.getLabel(classMethod), classMethod.getScope(), IndexEntry.Kind.FUNCTIONAL_AGENT));
            
            processEnvEntityForArgAndReturnTypeIndices(classMethod);
            
            generateClassMethodDoc(classMethod, i);
        }
        endClassMethodDocList(nClassMethodsToGenerate);
        
        ////
        /// Identify the known instances of this class.
        //
        Set<ModuleName> namesOfModulesToSearchForInstances = getDirectlyDependentModuleNames(moduleName);
        namesOfModulesToSearchForInstances.add(moduleName);
        
        List<ClassInstance> knownInstances = new ArrayList<ClassInstance>();
        
        for (final ModuleName nameOfModuleToSearchForInstances : namesOfModulesToSearchForInstances) {
            addIntancesOfTypeClassToList(typeClass, nameOfModuleToSearchForInstances, knownInstances);
        }
        
        // sort the known instances according to the standard order
        Collections.sort(knownInstances, new ClassInstanceComparator());
        
        ////
        /// Generate references to known instances of this class.
        //
        int nKnownInstances = knownInstances.size();
        beginKnownInstancesList(nKnownInstances, calcMaxScopeOfClassInstances(knownInstances));
        for (int i = 0; i < nKnownInstances; i++) {
            generateKnownInstance(knownInstances.get(i), i);
        }
        endKnownInstancesList(nKnownInstances);
        
        generateTypeClassDocFooter(typeClass, index);
    }

    /**
     * Generates the header portion of the detailed documentation for a type class.
     * @param typeClass the type class to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateTypeClassDocHeader(TypeClass typeClass, int index);
    
    /**
     * Generates the start of the list of class methods for the detailed documentation for a type class.
     * @param nClassMethods the number of class methods to be documented.
     */
    abstract void beginClassMethodDocList(int nClassMethods);

    /**
     * Generates the detailed documentation for a class method.
     * @param classMethod the class method to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateClassMethodDoc(ClassMethod classMethod, int index);
    
    /**
     * Generates the end of the list of class methods for the detailed documentation for a type class.
     * @param nClassMethods the number of class methods to be documented.
     */
    abstract void endClassMethodDocList(int nClassMethods);

    /**
     * Generates the start of the list of known instances for the detailed documentation for a type constructor or type class.
     * @param nKnownInstances the number of known instances to be documented.
     * @param maxScopeOfKnownInstances the maximum scope of the known instances.
     */
    abstract void beginKnownInstancesList(int nKnownInstances, Scope maxScopeOfKnownInstances);

    /**
     * Generates a known instance list item.
     * @param classInstance the class instance.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateKnownInstance(ClassInstance classInstance, int index);

    /**
     * Generates the end of the list of known instances for the detailed documentation for a type constructor or type class.
     * @param nKnownInstances the number of known instances to be documented.
     */
    abstract void endKnownInstancesList(int nKnownInstances);

    /**
     * Generates the footer portion of the detailed documentation for a type class.
     * @param typeClass the type class to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateTypeClassDocFooter(TypeClass typeClass, int index);
    
    /**
     * Generates the end of the type class section of the detailed documentation.
     * @param nTypeClasses the number of type classes to be documented.
     */
    abstract void endTypeClassesDocSection(int nTypeClasses);
    
    /**
     * Generates the start of the class instances section of the detailed documentation.
     * @param nClassInstances the number of class instances to be documented.
     * @param classInstances the array of class instances to be documented.
     * @param maxScopeOfClassInstances the maximum scope of the class instances.
     */
    abstract void beginClassInstancesDocSection(int nClassInstances, ClassInstance[] classInstances, Scope maxScopeOfClassInstances);

    /**
     * Generates the detailed documentation for a class instance.
     * @param classInstance the class instance to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    void generateClassInstanceDoc(ClassInstance classInstance, int index) {
        ////
        /// Generate documentation about the class instance itself.
        //
        generateClassInstanceDocHeader(classInstance, index);
        
        ////
        /// Generate documentation for each method of this class instance.
        //
        int nInstanceMethods = classInstance.getNInstanceMethods();
        beginInstanceMethodDocList(nInstanceMethods);
        TypeClass typeClass = classInstance.getTypeClass();
        for (int i = 0; i < nInstanceMethods; i++) {
            // no index entries for instance method definitions
            ClassMethod classMethod = typeClass.getNthClassMethod(i);
            generateInstanceMethodDoc(classInstance, classMethod.getName().getUnqualifiedName(), classMethod, i);
        }
        endInstanceMethodDocList(nInstanceMethods);
        
        generateClassInstanceDocFooter(classInstance, index);
    }

    /**
     * Generates the header portion of the detailed documentation for a class instance.
     * @param classInstance the class instance to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateClassInstanceDocHeader(ClassInstance classInstance, int index);
        
    /**
     * Generates the start of the list of instance methods for the detailed documentation for a class instance.
     * @param nInstanceMethods the number of instance methods to be documented.
     */
    abstract void beginInstanceMethodDocList(int nInstanceMethods);
    
    /**
     * Generates the detailed documentation for an instance method.
     * @param classInstance the class instance of the instance method.
     * @param methodName the name of the instance method.
     * @param classMethod the corresponding class method.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateInstanceMethodDoc(ClassInstance classInstance, String methodName, ClassMethod classMethod, int index);

    /**
     * Generates the end of the list of instance methods for the detailed documentation for a class instance.
     * @param nInstanceMethods the number of instance methods to be documented.
     */
    abstract void endInstanceMethodDocList(int nInstanceMethods);

    /**
     * Generates the footer portion of the detailed documentation for a class instance.
     * @param classInstance the class instance to be documented.
     * @param index the position of this entry in its enclosing list.
     */
    abstract void generateClassInstanceDocFooter(ClassInstance classInstance, int index);
    
    /**
     * Generates the start of the class instances section of the detailed documentation.
     * @param nClassInstances the number of class instances to be documented.
     */
    abstract void endClassInstancesDocSection(int nClassInstances);
    
    /**
     * Returns whether documentation is generated for the specified module.
     * @param moduleName the name of the module to check.
     * @return true if documentation is generated for the module; false otherwise.
     */
    boolean isDocForModuleGenerated(ModuleName moduleName) {
        // The module must exist and it must not be filtered out by the user-specified filter
        // for the documentation to be generated.
        return programModelManager.hasModuleInProgram(moduleName) && filter.shouldGenerateModule(moduleName);
    }
    
    /**
     * Returns whether documentation is generated for the specified type constructor.
     * @param moduleName the name of the type constructor's module.
     * @param unqualifiedName the unqualified name of the type constructor.
     * @return true if documentation is generated for the type constructor; false otherwise.
     */
    boolean isDocForTypeConsGenerated(ModuleName moduleName, String unqualifiedName) {
        ////
        /// First the entity must exist for its documentation to be generated.
        //
        ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        if (moduleTypeInfo == null) {
            return false;
        } else {
            ScopedEntity entity = moduleTypeInfo.getTypeConstructor(unqualifiedName);
            if (entity == null) {
                return false;
            } else {
                ////
                /// Having found the entity, now defer to the user-specified filter for the final answer.
                //
                return filter.shouldGenerateScopedEntity(entity);
            }
        }
    }
    
    /**
     * Returns whether the specified type constructor should be accepted (based on scope only).
     * @param moduleName the name of the type constructor's module.
     * @param unqualifiedName the unqualified name of the type constructor.
     * @return true if the type constructor is accepted; false otherwise.
     */
    boolean shouldAcceptTypeConsBasedOnScopeOnly(ModuleName moduleName, String unqualifiedName) {
        ////
        /// First the entity must exist for its documentation to be generated.
        //
        ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        if (moduleTypeInfo == null) {
            return false;
        } else {
            ScopedEntity entity = moduleTypeInfo.getTypeConstructor(unqualifiedName);
            if (entity == null) {
                return false;
            } else {
                ////
                /// Having found the entity, now defer to the user-specified filter for the final answer.
                //
                return filter.shouldAcceptScopedEntityBasedOnScopeOnly(entity);
            }
        }
    }
    
    /**
     * Returns whether documentation is generated for the specified data constructor.
     * @param moduleName the name of the data constructor's module.
     * @param unqualifiedName the unqualified name of the data constructor.
     * @return true if documentation is generated for the data constructor; false otherwise.
     */
    boolean isDocForDataConsGenerated(ModuleName moduleName, String unqualifiedName) {
        ////
        /// First the entity must exist for its documentation to be generated.
        //
        ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        if (moduleTypeInfo == null) {
            return false;
        } else {
            ScopedEntity entity = moduleTypeInfo.getDataConstructor(unqualifiedName);
            if (entity == null) {
                return false;
            } else {
                ////
                /// Having found the entity, now defer to the user-specified filter for the final answer.
                //
                return filter.shouldGenerateScopedEntity(entity);
            }
        }
    }
   
    /**
     * Returns whether documentation is generated for the specified function or class method.
     * @param moduleName the name of the function or class method's module.
     * @param unqualifiedName the unqualified name of the function or class method.
     * @return true if documentation is generated for the function or class method; false otherwise.
     */
    boolean isDocForFunctionOrClassMethodGenerated(ModuleName moduleName, String unqualifiedName) {
        ////
        /// First the entity must exist for its documentation to be generated.
        //
        ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        if (moduleTypeInfo == null) {
            return false;
        } else {
            ScopedEntity entity = moduleTypeInfo.getFunctionOrClassMethod(unqualifiedName);
            if (entity == null) {
                return false;
            } else {
                ////
                /// Having found the entity, now defer to the user-specified filter for the final answer.
                //
                return filter.shouldGenerateScopedEntity(entity);
            }
        }
    }
    
    /**
     * Returns whether documentation is generated for the specified type class.
     * @param moduleName the name of the type class's module.
     * @param unqualifiedName the unqualified name of the type class.
     * @return true if documentation is generated for the type class; false otherwise.
     */
    boolean isDocForTypeClassGenerated(ModuleName moduleName, String unqualifiedName) {
        ////
        /// First the entity must exist for its documentation to be generated.
        //
        ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        if (moduleTypeInfo == null) {
            return false;
        } else {
            ScopedEntity entity = moduleTypeInfo.getTypeClass(unqualifiedName);
            if (entity == null) {
                return false;
            } else {
                ////
                /// Having found the entity, now defer to the user-specified filter for the final answer.
                //
                return filter.shouldGenerateScopedEntity(entity);
            }
        }
    }
    
    /**
     * Returns whether documentation is generated for the class instance.
     * @param classInstance the class instance to check.
     * @return true if documentation is generated for the class instance; false otherwise.
     */
    boolean isDocForClassInstanceGenerated(ClassInstance classInstance) {
        ////
        /// First check the type class of the instance.
        //
        if (!filter.shouldAcceptScopedEntityBasedOnScopeOnly(classInstance.getTypeClass())) {
            return false;
        }
        
        ////
        /// The type class is accepted, so check the instance type.
        //
        SourceModel.TypeSignature instanceType = classInstance.getType().toSourceModel();
        TypeSigCheckerForTypeConsNotAcceptedBasedOnScopeOnly checker = new TypeSigCheckerForTypeConsNotAcceptedBasedOnScopeOnly();
        instanceType.accept(checker, null);
        
        return !checker.hasTypeConsNotAcceptedBasedOnScopeOnly();
    }
    
    /**
     * Returns whether documentation is generated for both the class instance's type class and instance type.
     * @param classInstance the class instance to check.
     * @return true if documentation is generated for both the class instance's type class and instance type; false otherwise.
     */
    boolean areDocForInstanceClassAndInstanceTypeGenerated(ClassInstance classInstance) {
        ////
        /// First check the type class of the instance.
        //
        QualifiedName typeClassName = classInstance.getTypeClass().getName();
        if (!isDocForTypeClassGenerated(typeClassName.getModuleName(), typeClassName.getUnqualifiedName())) {
            return false;
        }
        
        ////
        /// Documentation is generated for the type class, so check the instance type.
        //
        SourceModel.TypeSignature instanceType = classInstance.getType().toSourceModel();
        TypeSigCheckerForTypeConsWhereDocNotGenerated checker = new TypeSigCheckerForTypeConsWhereDocNotGenerated();
        instanceType.accept(checker, null);
        
        return !checker.hasTypeConsWhereDocNotGenerated();
    }
    
    /**
     * Returns the scope of the specified type constructor.
     * @param moduleName the name of the type constructor's module.
     * @param unqualifiedName the unqualified name of the type constructor.
     * @return the scope of the type constructor.
     */
    Scope getTypeConstructorScope(ModuleName moduleName, String unqualifiedName) {
        ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        if (moduleTypeInfo == null) {
            return Scope.PRIVATE;
        } else {
            ScopedEntity entity = moduleTypeInfo.getTypeConstructor(unqualifiedName);
            if (entity == null) {
                return Scope.PRIVATE;
            } else {
                return entity.getScope();
            }
        }
    }
    
    /**
     * Returns the minimum scope of the class instance's type class and instance type.
     * @param classInstance the class instance to check.
     * @return the minimum scope of the class instance's type class and instance type.
     */
    Scope minScopeForInstanceClassAndInstanceType(ClassInstance classInstance) {
        
        Scope cachedScope = classInstanceMinScopeCache.get(classInstance);
        if (cachedScope != null) {
            return cachedScope;
        }
        
        Scope instanceClassScope = classInstance.getTypeClass().getScope();
        
        SourceModel.TypeSignature instanceType = classInstance.getType().toSourceModel();
        MinScopeCalculatorForTypeConsInTypeSig calculator = new MinScopeCalculatorForTypeConsInTypeSig();
        instanceType.accept(calculator, null);
        
        Scope instanceTypeMinScope = calculator.getMinScope();
        
        Scope instanceMinScope = minScope(instanceClassScope, instanceTypeMinScope);
        
        classInstanceMinScopeCache.put(classInstance, instanceMinScope);
        
        return instanceMinScope;
    }
    
    /**
     * Returns the minimum of two scopes (i.e. the less visible of the two scopes).
     * @param a the first scope.
     * @param b the second scope.
     * @return the minimum of the two scopes.
     */
    static Scope minScope(Scope a, Scope b) {
        if (a.compareTo(b) <= 0) {
            return a;
        } else {
            return b;
        }
    }
    
    /**
     * Returns the maximum of two scopes (i.e. the more visible of the two scopes).
     * @param a the first scope.
     * @param b the second scope.
     * @return the maximum of the two scopes.
     */
    static Scope maxScope(Scope a, Scope b) {
        if (a.compareTo(b) > 0) {
            return a;
        } else {
            return b;
        }
    }
    
    /**
     * Calculates the maximum scope of all the specified entities.
     * @param entities the scoped entities.
     * @return the maximum scope of all the specified entities.
     */
    static Scope calcMaxScopeOfScopedEntities(ScopedEntity[] entities) {
        Scope max = Scope.PRIVATE;
        
        for (final ScopedEntity scopedEntity : entities) {
            max = maxScope(max, scopedEntity.getScope());
            
            // we're done if the maximum is the public scope, since there's no scope more visible than public
            if (max == Scope.PUBLIC) {
                break;
            }
        }
        
        return max;
    }
    
    /**
     * Calculates the maximum scope of all the specified entities.
     * @param entities the scoped entities.
     * @return the maximum scope of all the specified entities.
     */
    static Scope calcMaxScopeOfScopedEntities(Collection<? extends ScopedEntity> entities) {
        Scope max = Scope.PRIVATE;
        
        for (final ScopedEntity scopedEntity : entities) {
            max = maxScope(max, scopedEntity.getScope());
            
            // we're done if the maximum is the public scope, since there's no scope more visible than public
            if (max == Scope.PUBLIC) {
                break;
            }
        }
        
        return max;
    }
    
    /**
     * Calculates the maximum scope of all the specified instances.
     * @param classInstances the class instances.
     * @return the maximum scope of all the specified instances.
     */
    Scope calcMaxScopeOfClassInstances(ClassInstance[] classInstances) {
        Scope max = Scope.PRIVATE;
        
        for (final ClassInstance classInstance : classInstances) {
            max = maxScope(max, minScopeForInstanceClassAndInstanceType(classInstance));
            
            // we're done if the maximum is the public scope, since there's no scope more visible than public
            if (max == Scope.PUBLIC) {
                break;
            }
        }
        
        return max;
    }
    
    /**
     * Calculates the maximum scope of all the specified instances.
     * @param classInstances the class instances.
     * @return the maximum scope of all the specified instances.
     */
    Scope calcMaxScopeOfClassInstances(Collection<ClassInstance> classInstances) {
        Scope max = Scope.PRIVATE;
        
        for (final ClassInstance classInstance : classInstances) {
            max = maxScope(max, minScopeForInstanceClassAndInstanceType(classInstance));
            
            // we're done if the maximum is the public scope, since there's no scope more visible than public
            if (max == Scope.PUBLIC) {
                break;
            }
        }
        
        return max;
    }

    /**
     * @return the moduleNameResolverForDocumentedModules
     */
    ModuleNameResolver getModuleNameResolverForDocumentedModules() {
        return moduleNameResolverForDocumentedModules;
    }
}
