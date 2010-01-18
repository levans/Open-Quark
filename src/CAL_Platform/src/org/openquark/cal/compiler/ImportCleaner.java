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
 * ImportCleaner.java
 * Creation date: (Feb 14, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.Import.UsingItem;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.util.ArrayStack;



/**
 * The clean-imports refactoring cleans up import-using clauses by: 
 * <ol>
 *     <li> removing names that are not referenced from using clauses
 *     <li> Sorting and nicely formatting using clauses
 *     <li> removing extraneous import declarations
 * </ol> 
 *  
 * <P> There are ordering issues with the removal of extraneous import declarations.
 *  We never remove an import of a module M if it brings instances into scope that
 *  are not brought into scope by the other imports.  But the order in which we do
 *  the removals can affect the outcome.
 *  
 *  <P>Consider the case where we import modules M, N, O, and P.  M and N both have 
 *  no instances of their own, but they both import Q (and only Q), which does have 
 *  some instances.  Assume that both M and N are extraneous in the sense that we 
 *  don't reference any of their symbols.
 *  
 *  <P>If we check M first, then it will be removed, because it doesn't add any new
 *  instances that aren't provided by the other imports (ie, {N, O, P}).  But then
 *  when we check N we will /not/ remove it, because it provides instances that are
 *  not provided by the other imports (ie, {O, P}).  So our final import set will be
 *  {N, O, P}.
 *  
 *  <P>On the other hand, if we check N first, then it will be removed, because it
 *  doesn't add any new instances over {M, O, P}.  And M will not be removed, 
 *  because {O, P} doesn't provide Q's instances.  So the final import set will be
 *  {M, O, P}.
 *  
 *  <P>We check imports in source order; ie, if import M occurs before import Q in
 *  the source text, then import M will be checked for redundancy first.
 *
 * @author James Wright
 */
final class ImportCleaner {

    private ImportCleaner() {
    }

    /**
     * This class gathers the information from a module's SourceModel that we need in
     * order to perform the refactoring and provides methods to query the resulting
     * summary data.
     * 
     * @author James Wright
     */
    private static final class Summarizer extends BindingTrackingSourceModelTraverser<Void> {
        
        /** 
         * Map (UsingItem. Category -> Set (String)) from category of name to
         * a set of names that are not bound (eg by let or case expressions)
         * that have been encountered
         */
        private final Map<String, Set<String>> unqualifiedUnboundNames = new HashMap<String, Set<String>>();
        
        /**
         * Set (ModuleName) of names of modules that have been (potentially) referenced
         * in the module being summarized.  The uncertainty arise from CALDoc links,
         * which can contain cons names which may or may not be module names.  We
         * conservatively treat such links as module references.
         */
        private final Set<ModuleName> potentiallyReferencedModules = new HashSet<ModuleName>();

        /**
         * List of import statements in the order that they were encountered
         * during a source traversal.
         */
        private final List<SourceModel.Import> importStatements = new ArrayList<SourceModel.Import>();
        
        /**
         * The module name resolver for the module to be processed.
         */
        private final ModuleNameResolver moduleNameResolver;

        /**
         * @param moduleNameResolver the module name resolver for the module to be processed.
         */
        Summarizer(ModuleNameResolver moduleNameResolver) {
            
            if (moduleNameResolver == null) {
                throw new NullPointerException();
            }
            
            this.moduleNameResolver = moduleNameResolver;
            
            unqualifiedUnboundNames.put(UsingItem.Function.CATEGORY_NAME, new HashSet<String>());
            unqualifiedUnboundNames.put(UsingItem.DataConstructor.CATEGORY_NAME, new HashSet<String>());
            unqualifiedUnboundNames.put(UsingItem.TypeConstructor.CATEGORY_NAME, new HashSet<String>());
            unqualifiedUnboundNames.put(UsingItem.TypeClass.CATEGORY_NAME, new HashSet<String>());
        }
        
        /**
         * Checks whether a name should be added to the list of unbound
         * unqualified names. 
         * @param name Name to check
         */
        private void checkName(Name.Qualifiable name) {
            
            String unqualifiedName = name.getUnqualifiedName();
            ModuleName moduleName = SourceModel.Name.Module.maybeToModuleName(name.getModuleName()); // may be null
            
            if(moduleName == null && !isBound(unqualifiedName)) {
                Set<String> nameSet;
                if(name instanceof Name.Function) {
                    nameSet = unqualifiedUnboundNames.get(UsingItem.Function.CATEGORY_NAME);

                } else if(name instanceof Name.DataCons) {
                    nameSet = unqualifiedUnboundNames.get(UsingItem.DataConstructor.CATEGORY_NAME);
                
                } else if(name instanceof Name.TypeCons) {
                    nameSet = unqualifiedUnboundNames.get(UsingItem.TypeConstructor.CATEGORY_NAME);
                
                } else if(name instanceof Name.TypeClass) {
                    nameSet = unqualifiedUnboundNames.get(UsingItem.TypeClass.CATEGORY_NAME);
                
                } else if(name instanceof Name.WithoutContextCons) {
                    // Special case: This might be a type class, type cons, or data cons
                    // It will only be one of them (else it would be ambiguous and therefore uncompilable),
                    // so we can just add the name to all 3 consname sets without having to worry that it
                    // will cause us to retain superfluous names in a using item.

                    nameSet = unqualifiedUnboundNames.get(UsingItem.DataConstructor.CATEGORY_NAME);
                    nameSet.add(unqualifiedName);
                    
                    nameSet = unqualifiedUnboundNames.get(UsingItem.TypeConstructor.CATEGORY_NAME);
                    nameSet.add(unqualifiedName);

                    nameSet = unqualifiedUnboundNames.get(UsingItem.TypeClass.CATEGORY_NAME);
                    
                } else {
                    throw new IllegalStateException("unrecognized Name subclass");
                }
                
                nameSet.add(unqualifiedName);
            }

            if(name instanceof Name.WithoutContextCons) {
                ModuleName resolvedModuleName = moduleNameResolver.resolve(ModuleName.make(name.toSourceText())).getResolvedModuleName();
                potentiallyReferencedModules.add(resolvedModuleName);
            }

            if(moduleName != null) {
                ModuleName resolvedModuleName = moduleNameResolver.resolve(moduleName).getResolvedModuleName();
                potentiallyReferencedModules.add(resolvedModuleName);
            }
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Name_DataCons(Name.DataCons cons, Object arg) {
            checkName(cons);
            return super.visit_Name_DataCons(cons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Name_Function(Name.Function function, Object arg) {
            checkName(function);
            return super.visit_Name_Function(function, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Name_TypeClass(Name.TypeClass typeClass, Object arg) {
            checkName(typeClass);
            return super.visit_Name_TypeClass(typeClass, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Name_TypeCons(Name.TypeCons cons, Object arg) {
            checkName(cons);
            return super.visit_Name_TypeCons(cons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Name_WithoutContextCons(Name.WithoutContextCons cons, Object arg) {
            checkName(cons);
            return super.visit_Name_WithoutContextCons(cons, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import(SourceModel.Import importStmt, Object arg) {

            importStatements.add(importStmt);
            return super.visit_Import(importStmt, arg);
        }

        /**
         * @return A List of Imports that were encountered while walking the model.
         */
        private List<SourceModel.Import> getImportStatements() {
            return Collections.unmodifiableList(importStatements);
        }
        
        /**
         * @param categoryName Name of the category of identifier that we are checking.
         *         This should be the CATEGORY_NAME field of one of the UsingItem subclasses.
         * @param name Name to check for unqualified unbound references
         * @return True if the specified name occurs unqualified and unbound in the module
         *          in the category with the name specified by categoryName.
         */
        private boolean isUnqualifiedUnboundName(String categoryName, String name) {
            Set<String> nameSet = unqualifiedUnboundNames.get(categoryName); 
            return nameSet.contains(name);
        }

        /** 
         * @return True if the specified module is not directly referenced in this module.
         *          A "direct" reference occurs when a module occurs in a fully-qualified name.
         *          Unqualified references to names imported in a using clause are explicitly
         *          not included.
         *           
         *          False negatives may occur (ie, we may return false for a module that is
         *          in fact unreferenced).  This is because CALDoc links can contain cons
         *          names that may or may not be modules; we conservatively treat all such 
         *          names as module names.
         * @param moduleName Name of the module to check
         */
        private boolean isModuleUnreferenced(ModuleName moduleName) {
            return !potentiallyReferencedModules.contains(moduleName);
        }
    }

    /**
     * Helper class for finding instances that are brought into scope by a given set of imports.
     * This class caches results to allow us to check many combinations of imports without having
     * to fully walk the import graph each time.
     * 
     * An instance is "brought into scope" by a module if
     *  (a) it is declared in the module, or
     *  (b) it is brought into scope by a module imported by the module
     * 
     * @author James Wright
     */
    private static final class InstanceFinder {
        
        /** 
         * Map (ModuleName -> Set (String)) from module name to Set of instances brought
         * into scope by importing the module.
         */ 
        private final Map<ModuleName, Set<String>> moduleInstances = new HashMap<ModuleName, Set<String>>();
        
        private final ModuleContainer moduleContainer;
        
        private InstanceFinder(ModuleContainer workspace) {
            if(workspace == null) {
                throw new NullPointerException();
            }
            this.moduleContainer = workspace;
        }
        
        /**
         * Find all the instances brought into scope by importing rootModule and
         * return a Set of their names.
         * @param moduleName Name of module
         * @return Set (String) containing the names of all the instances that will
         *          be brought into scope by importing rootModule
         */
        private Set<String> findInstancesInScope(ModuleName moduleName) {
            
            // Check the cache before walking the import graph 
            if(moduleInstances.containsKey(moduleName)) {
                return moduleInstances.get(moduleName);
            }

            /** (String) */
            Set<String> instanceNames = new HashSet<String>();
            
            ModuleTypeInfo moduleTypeInfo = moduleContainer.getModuleTypeInfo(moduleName);
            
            // Record the instances that this module brings into scope
            // We store Strings instead of the ClassIdentifiers directly so that we can
            // distinguish between instances of the same type and type class that were
            // declared in different modules.  A properly typechecked program will not
            // have two visible instances for the same type and type class, but it never
            // hurts to be certain.
            for(int i = 0, nClassInstances = moduleTypeInfo.getNClassInstances(); i < nClassInstances; i++) {
                ClassInstance classInstance = moduleTypeInfo.getNthClassInstance(i);
                instanceNames.add(moduleName + "|" + classInstance.getNameWithContext());
            }
            
            // Record the instances that this module's imports bring into scope
            for(int i = 0, nImportedModules = moduleTypeInfo.getNImportedModules(); i < nImportedModules; i++) {
                ModuleTypeInfo importedModule = moduleTypeInfo.getNthImportedModule(i);
                ModuleName importedModuleName = importedModule.getModuleName();
                instanceNames.addAll(findInstancesInScope(importedModuleName));
            }

            Set<String> retVal = Collections.unmodifiableSet(instanceNames);
            moduleInstances.put(moduleName, retVal);
            return retVal;
        }
        
        /**
         * Find all the instances that will be brought into scope by importing 
         * all the modules in moduleNames. 
         * @param moduleNames Set (ModuleName) of module names
         * @return Set (String) of all the instances that will be brought into scope by importing 
         *          all the modules in moduleNames.
         */
        private Set<String> findInstancesInScope(Set<ModuleName> moduleNames) {
            Set<String> instanceNames = new HashSet<String>();
            for(final ModuleName moduleName : moduleNames) {              
                instanceNames.addAll(findInstancesInScope(moduleName));
            }
            
            return instanceNames;
        }
    }
    
    /**
     * Comparator that orders data constructor names by typecons name and 
     * datacons ordinal.
     * 
     * @author James Wright
     */
    private static final class DataConsNameComparator implements Comparator<String> {
        private final ModuleTypeInfo moduleTypeInfo;
        private final CompilerMessageLogger messageLogger;
        
        private DataConsNameComparator(ModuleTypeInfo moduleTypeInfo, CompilerMessageLogger messageLogger) {
            if(moduleTypeInfo == null || messageLogger == null) {
                throw new NullPointerException();
            }
            this.moduleTypeInfo = moduleTypeInfo;
            this.messageLogger = messageLogger;
        }
        
        /** {@inheritDoc} */
        public int compare(String leftName, String rightName) {
                               
            DataConstructor leftDataCons = moduleTypeInfo.getDataConstructor(leftName);
            if(leftDataCons == null) {
                MessageKind messageKind = new MessageKind.Error.DataConstructorDoesNotExist(QualifiedName.make(moduleTypeInfo.getModuleName(), leftName));
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return leftName.compareTo(rightName);
            }

            DataConstructor rightDataCons = moduleTypeInfo.getDataConstructor(rightName);
            if(rightDataCons == null) {
                MessageKind messageKind = new MessageKind.Error.DataConstructorDoesNotExist(QualifiedName.make(moduleTypeInfo.getModuleName(), rightName));
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return leftName.compareTo(rightName);
            }
            
            TypeConsApp leftTypeConsApp = leftDataCons.getTypeConsApp();
            TypeConsApp rightTypeConsApp = rightDataCons.getTypeConsApp();
            
            if(leftTypeConsApp.getName().equals(rightTypeConsApp.getName())) {
                return leftDataCons.getOrdinal() - rightDataCons.getOrdinal();
            } else {
                return leftTypeConsApp.getName().compareTo(rightTypeConsApp.getName());
            }
        }
    }

    /**
     * Used to generalize the getSourceModifier code so that is can be mostly shared for two different purposes.
     * 
     * @author GMCCLEMENT
     */
    private interface ListUpdater{
        /**
         * @param moduleName Module name of the current import statement.
         * @return True if the given module imports should be skipped
         */
        public boolean skipModule(ModuleName moduleName);
        
        /**
         * @return the name of the module that the import statement is going to be updated on.
         */
        public ModuleName usingImportStatementFor();
        
        /**
         * Update the newUsingItemList as appropriate.
         */
        public void update(UsingItem[] oldUsingItems, List<UsingItem> newUsingItemsList, Summarizer summarizer, DataConsNameComparator dataconsComparator, ModuleTypeInfo importedModuleTypeInfo);
    }
    
    /**
     * Creates a SourceModifier containing SourceModifications that will perform the
     * clean-imports refactoring on a module.
     * 
     * Messages will be logged to messageLogger on error.
     * 
     * @param moduleContainer CALWorkspace containing the module to process
     * @param moduleName name of the module to process
     * @param sourceText source text of the module to process
     * @param preserveItems If true, items will not be combined or reordered.
     * @param messageLogger CompilerMessageLogger to log error messages to
     * @return A SourceModifier containing SourceModifications that will perform the
     *          ImportClean refactoring on a module.
     */
    static SourceModifier getSourceModifier_cleanImports(ModuleContainer moduleContainer, ModuleName moduleName, String sourceText, final boolean preserveItems, CompilerMessageLogger messageLogger) {
        ListUpdater updater = new ListUpdater(){
            public boolean skipModule(ModuleName moduleName){
                return false;
            }
            
            public void update(UsingItem[] oldUsingItems, List<UsingItem> newUsingItemsList, Summarizer summarizer, DataConsNameComparator dataconsComparator, ModuleTypeInfo importedModuleTypeInfo){
                if(preserveItems) {
                    for (final UsingItem oldUsingItem : oldUsingItems) {
                        Comparator<String> comparator = (oldUsingItem instanceof UsingItem.DataConstructor) ? dataconsComparator : null;
                        UsingItem newItem = calculateFactoredUsingItem(summarizer, oldUsingItem.getUsingItemCategoryName(), oldUsingItem.getUsingNames(), comparator);
                        if(newItem != null) {
                            newUsingItemsList.add(newItem);
                        }
                    }

                } else {
                    String[] emptyStingArray = new String[0];

                    List<String> functionNames = new ArrayList<String>();
                    List<String> dataConsNames = new ArrayList<String>();
                    List<String> typeConsNames = new ArrayList<String>();
                    List<String> typeClassNames = new ArrayList<String>();

                    Map<String, List<String>> groups = new HashMap<String, List<String>>();
                    groups.put(UsingItem.Function.CATEGORY_NAME, functionNames);
                    groups.put(UsingItem.DataConstructor.CATEGORY_NAME, dataConsNames);
                    groups.put(UsingItem.TypeConstructor.CATEGORY_NAME, typeConsNames);
                    groups.put(UsingItem.TypeClass.CATEGORY_NAME, typeClassNames);

                    for (final UsingItem oldUsingItem : oldUsingItems) {
                        List<String> groupNames = groups.get(oldUsingItem.getUsingItemCategoryName());
                        groupNames.addAll(Arrays.asList(oldUsingItem.getUsingNames()));
                    }

                    UsingItem typeClassUsingItem = calculateFactoredUsingItem(summarizer, UsingItem.TypeClass.CATEGORY_NAME, typeClassNames.toArray(emptyStingArray), null);
                    if(typeClassUsingItem != null) {
                        newUsingItemsList.add(typeClassUsingItem);
                    }

                    UsingItem typeConsUsingItem = calculateFactoredUsingItem(summarizer, UsingItem.TypeConstructor.CATEGORY_NAME, typeConsNames.toArray(emptyStingArray), null);
                    if(typeConsUsingItem != null) {
                        newUsingItemsList.add(typeConsUsingItem);
                    }

                    UsingItem dataConsUsingItem = calculateFactoredUsingItem(summarizer, UsingItem.DataConstructor.CATEGORY_NAME, dataConsNames.toArray(emptyStingArray), dataconsComparator);
                    if(dataConsUsingItem != null) {
                        newUsingItemsList.add(dataConsUsingItem);
                    }

                    UsingItem functionUsingItem = calculateFactoredUsingItem(summarizer, UsingItem.Function.CATEGORY_NAME, functionNames.toArray(emptyStingArray), null);
                    if(functionUsingItem != null) {
                        newUsingItemsList.add(functionUsingItem);
                    }
                }
            }

            public ModuleName usingImportStatementFor() {
                // not used
                return null;
            }
        };

        return getSourceModifier_common(updater, moduleContainer, moduleName, sourceText, false, messageLogger);
    }
    
    /**
     * Returns true if importing moduleName brings additional instances into scope compared with the 
     * other imports of startingImports.  In other words, this function returns true if importing
     * startingImports - moduleName will bring a smaller number of instances into scope than importing
     * all of startingImports.
     * @param moduleName name of module to check
     * @param startingImports Set (ModuleName) of module names; it is assumed that this set contains moduleName.
     * @param instanceFinder An InstanceFinder instance to use for computation
     * @return boolean
     */    
    private static boolean bringsNewInstancesIntoScope(ModuleName moduleName, Set<ModuleName> startingImports, InstanceFinder instanceFinder) {
        Set<ModuleName> visitModules = new HashSet<ModuleName>();
        
        visitModules.addAll(startingImports);
        visitModules.remove(moduleName);
        Set<String> importsWithoutTarget = instanceFinder.findInstancesInScope(visitModules);
        Set<String> importsViaTarget = instanceFinder.findInstancesInScope(moduleName);
        
        for (final String instanceName : importsViaTarget) {
            
            if(!importsWithoutTarget.contains(instanceName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calculates a new UsingItem that contains all of the referenced names in oldNames.
     * Returns null if none of the names in oldNames have unqualified references.
     * @param visitor A Summarizer object containing information about references in the current module.
     * @param oldUsingCategoryName Category of the names in oldNames; Should be the CATEGORY_NAME field of
     *         one of the UsingItem subclasses.
     * @param oldNames String array of names to consider including in the new UsingItem
     * @param comparator If non-null, the names added to the new UsingItem will be ordered according
     *                    to this comparator.  If null, the names added to the new UsingItem will be 
     *                    ordered alphabetically.
     * @return a UsingItem with category oldUsingCategory that contains all of the names in
     *          oldNames that have at least one unqualified reference in the current module.
     *          Returns null if none of the names in oldNames have an unqualified reference. 
     */
    private static UsingItem calculateFactoredUsingItem(Summarizer visitor, String oldUsingCategoryName, String[] oldNames, Comparator<String> comparator) {
        List<String> newNamesList = new ArrayList<String>();
        
        for (final String oldName : oldNames) {
            if(visitor.isUnqualifiedUnboundName(oldUsingCategoryName, oldName)) {
                newNamesList.add(oldName);
            }
        }
        
        if(newNamesList.size() == 0) {
            return null;
        }
        
        String[] newNames = newNamesList.toArray(new String[0]);
        if(comparator != null) {
            Arrays.sort(newNames, comparator);
        } else {
            Arrays.sort(newNames);
        }
        
        if(oldUsingCategoryName.equals(UsingItem.Function.CATEGORY_NAME)) {
            return UsingItem.Function.make(newNames);

        } else if(oldUsingCategoryName.equals(UsingItem.DataConstructor.CATEGORY_NAME)) {
            return UsingItem.DataConstructor.make(newNames);
    
        } else if(oldUsingCategoryName.equals(UsingItem.TypeConstructor.CATEGORY_NAME)) {
            return UsingItem.TypeConstructor.make(newNames);
    
        } else if(oldUsingCategoryName.equals(UsingItem.TypeClass.CATEGORY_NAME)) {
            return UsingItem.TypeClass.make(newNames);
        
        } else {
            throw new IllegalArgumentException("invalid using category name");
        }
    }
    

    /**
     * @param ui The using item to add the name to. This maybe null. 
     * @param newName
     */
    private static String[] updateNames(UsingItem ui, String newName){
        if (ui == null){
            String[] newNames = new String[1];
            newNames[0] = newName;
            return newNames;
        }
        else{
            String[] names = ui.getUsingNames();
            // if the name is already in the list then don't add it. This is not  
            // done in the following loop combined because the list might
            // be unalphabetical and that loop bails early.
            for(int i = 0; i < names.length; ++i){
                if (names[i].equals(newName)){
                    // already has the name so why bother
                    return names;
                }
            }
            String[] newNames = new String[names.length+1];
            // insert the name as alphabetically as possible.
            int iNextName = 0;
            // copy all the names that are alphabetically before the new one over to the new array.
            while(iNextName < names.length){
                if (names[iNextName].compareTo(newName) <= 0){
                    newNames[iNextName] = names[iNextName]; 
                    iNextName++;
                }
                else{
                    break;
                }
            }
            // copy over the new name
            newNames[iNextName] = newName;
            // copy all the names that are alphabetically after the new one over to the new array
            System.arraycopy(names, iNextName, newNames, iNextName+1, names.length - iNextName);
            return newNames;
        }
    }

    /**
     * @param usingItem The using item to add the name to. This maybe be null
     * @param unqualifiedName
     * @param importedModuleTypeInfo
     */
    private static UsingItem maybeGetNewUsingItem(UsingItem usingItem, String unqualifiedName, final SourceIdentifier.Category category, ModuleTypeInfo importedModuleTypeInfo){
        if (
                (category == null || category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) &&
                importedModuleTypeInfo.getFunction(unqualifiedName) != null ||
                importedModuleTypeInfo.getClassMethod(unqualifiedName) != null
           ){
            if (usingItem == null || usingItem instanceof UsingItem.Function){
                return UsingItem.Function.make(updateNames(usingItem, unqualifiedName));
            }
        }
        else if (
                (category == null || category == SourceIdentifier.Category.DATA_CONSTRUCTOR) &&
                importedModuleTypeInfo.getDataConstructor(unqualifiedName) != null
                ){                
            if (usingItem == null || usingItem instanceof UsingItem.DataConstructor){
                return UsingItem.DataConstructor.make(updateNames(usingItem, unqualifiedName));
            }
        }
        else if (
                (category == null || category == SourceIdentifier.Category.TYPE_CONSTRUCTOR) &&
                importedModuleTypeInfo.getTypeConstructor(unqualifiedName) != null){                
            if (usingItem == null || usingItem instanceof UsingItem.TypeConstructor){
                return UsingItem.TypeConstructor.make(updateNames(usingItem, unqualifiedName));
            }
        }
        else if (
                (category == null || category == SourceIdentifier.Category.TYPE_CLASS) &&
                importedModuleTypeInfo.getTypeClass(unqualifiedName) != null){                
            if (usingItem == null || usingItem instanceof UsingItem.TypeClass){
                return UsingItem.TypeClass.make(updateNames(usingItem, unqualifiedName));
            }
        }
        
        return null; // not applicable
    }            

    /**
     * Insert the given symbol as an import in the current file. 
     */
    static SourceModifier getSourceModifier_insertImport(ModuleContainer moduleContainer, ModuleName moduleName, String sourceText, final QualifiedName insertImport, final SourceIdentifier.Category category, final boolean ignoreErrors, CompilerMessageLogger messageLogger) {
        ListUpdater updater = new ListUpdater(){
            public boolean skipModule(ModuleName importedModuleName){
                return !importedModuleName.equals(insertImport.getModuleName());
            }

            public ModuleName usingImportStatementFor() {
                return insertImport.getModuleName();
            }
            
            public void update(UsingItem[] oldUsingItems, List<UsingItem> newUsingItemsList, Summarizer summarizer, DataConsNameComparator dataconsComparator, ModuleTypeInfo importedModuleTypeInfo){
                // Update the newUsingItemsList
                {
                    boolean wasAdded = false;
                    for (final UsingItem oldItem : oldUsingItems) {
                        UsingItem newItem = maybeGetNewUsingItem(oldItem, insertImport.getUnqualifiedName(), category, importedModuleTypeInfo);
                        // if wasAdded then the symbol has already been added to an import statement.
                        // The user has used multiple import statements for the same type so use the old definition.
                        if (wasAdded || newItem == null){
                            newUsingItemsList.add(oldItem);
                        }
                        else{
                            newUsingItemsList.add(newItem);
                            wasAdded = true;
                            // don't add a break here because the remaining unchanged 
                            // oldItems must be added to the list
                        }
                    }
                    if (!wasAdded){
                        newUsingItemsList.add(maybeGetNewUsingItem(null, insertImport.getUnqualifiedName(), category, importedModuleTypeInfo));
                    }
                }
            }
        };            
    
        return getSourceModifier_common(updater, moduleContainer, moduleName, sourceText, ignoreErrors, messageLogger);
    }

    private static SourceModifier getSourceModifier_common(ListUpdater updater, ModuleContainer moduleContainer, ModuleName moduleName, String sourceText, final boolean ignoreErrors, CompilerMessageLogger messageLogger) {
        SourceModifier sourceModifier = new SourceModifier();
        
        // if the module is a sourceless module, then there is not much we can do with it.
        if (sourceText.length() == 0) {
            return sourceModifier;
        }
        
        int nErrorsBefore = messageLogger.getNErrors();
        SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(sourceText, ignoreErrors, messageLogger);
        if (!ignoreErrors && messageLogger.getNErrors() > nErrorsBefore || moduleDefn == null) {
            // We can't proceed if the module is unparseable
            return sourceModifier;
        }
        
        if(!moduleName.equals(SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName()))) {
            throw new IllegalArgumentException("moduleName must correspond to the module name in sourceText");
        }
        
        ModuleTypeInfo moduleTypeInfo = moduleContainer.getModuleTypeInfo(moduleName);
        if(moduleTypeInfo == null) {
            // We can't attempt to process a module that isn't in the workspace
            MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleName);
            messageLogger.logMessage(new CompilerMessage(messageKind));
            return sourceModifier;
        }
        
        Summarizer summarizer = new Summarizer(moduleTypeInfo.getModuleNameResolver());
        summarizer.visit_ModuleDefn(moduleDefn, ArrayStack.make());
        List<SourceModel.Import> importList = new ArrayList<SourceModel.Import>(summarizer.getImportStatements());
        
        if(importList.size() == 0) {
            return sourceModifier;
        }
        
        // Set (ModuleName) of imported modules post-refactoring
        Set/*ModuleName*/<ModuleName> unremovedImports = new HashSet<ModuleName>(); 
        for(int i = 0; i < moduleTypeInfo.getNImportedModules(); i++) {
            unremovedImports.add(moduleTypeInfo.getNthImportedModule(i).getModuleName());
        }
        
        InstanceFinder instanceFinder = new InstanceFinder(moduleContainer);
        SourcePosition previousPosition = new SourcePosition(1, 1);
        int previousIndex = 0;

        // The import statement for the module that the 
        // symbols are imported from might not be present
        // so we have to add it. This code figure out if one
        // is missing and adds it.
        SourceModel.Import newImportStatement = null;
        {
            boolean foundImportStatement = false;
            SourcePosition positionOfLastImportStatement = null;
            // there has to be at least one import statement since the module must import Prelude.
            for (final SourceModel.Import importStmt : importList) {
                
                final SourcePosition importStmtSourcePosition = importStmt.getSourceRange().getEndSourcePosition().offsetPositionByText("\n\n");
                if (positionOfLastImportStatement == null){
                    positionOfLastImportStatement = importStmtSourcePosition;
                }
                if (SourcePosition.compareByPosition.compare(importStmtSourcePosition, positionOfLastImportStatement) > 0){
                    positionOfLastImportStatement = importStmtSourcePosition;
                }
                ModuleName importedModuleName = SourceModel.Name.Module.toModuleName(importStmt.getImportedModuleName());

                if (updater.skipModule(importedModuleName)){
                    continue;
                }
                foundImportStatement = true;
                break;
            }

            if (!foundImportStatement){
                newImportStatement = SourceModel.Import.makeAnnotated(Name.Module.make(updater.usingImportStatementFor()), new UsingItem[0], new SourceRange(positionOfLastImportStatement, positionOfLastImportStatement));
                importList.add(newImportStatement);
            }
        }

        for (final SourceModel.Import importStmt : importList) {
            
            ModuleName importedModuleName = SourceModel.Name.Module.toModuleName(importStmt.getImportedModuleName());

            if (updater.skipModule(importedModuleName)){
                continue;
            }

            ModuleTypeInfo importedModuleTypeInfo = moduleContainer.getModuleTypeInfo(importedModuleName);
            if(importedModuleTypeInfo == null) {
                // We can't attempt to process a module that isn't in the workspace
                MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(importedModuleName);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                sourceModifier.clearAllModifications();
                return sourceModifier;
            }
            DataConsNameComparator dataconsComparator = new DataConsNameComparator(importedModuleTypeInfo, messageLogger);
            
            // Calculate a new import statement
            UsingItem[] oldUsingItems = importStmt.getUsingItems();
            List<UsingItem> newUsingItemsList = new ArrayList<UsingItem>();

            updater.update(oldUsingItems, newUsingItemsList, summarizer, dataconsComparator, importedModuleTypeInfo);
            
            // If the calculated import statement doesn't have any using clauses, and
            // we don't reference any symbols from it, and it doesn't bring any new
            // instance declarations into scope, then remove the import entirely.
            if(newUsingItemsList.size() == 0 &&
               summarizer.isModuleUnreferenced(importedModuleName) &&
               !importedModuleName.equals(CAL_Prelude.MODULE_NAME) &&
               !bringsNewInstancesIntoScope(importedModuleName, unremovedImports, instanceFinder)) {
                
                unremovedImports.remove(importedModuleName);
                
                SourcePosition startPosition = importStmt.getSourceRange().getStartSourcePosition();
                int startIndex = startPosition.getPosition(sourceText, previousPosition, previousIndex);
                SourcePosition endPosition = importStmt.getSourceRange().getEndSourcePosition();
                int endIndex = endPosition.getPosition(sourceText, startPosition, startIndex);
                
                sourceModifier.addSourceModification(new SourceModification.RemoveText(sourceText.substring(startIndex, endIndex), startPosition));
                previousPosition = endPosition;
                previousIndex = endIndex;

            // If the original import statement had no using clauses, then don't bother
            // regenerating it (since there are no using clauses to tidy up).
            } else if(oldUsingItems.length == 0 && newUsingItemsList.size() == 0) {
                continue;
            
            // Otherwise record a change that emits the new import statement
            } else {            
                UsingItem[] newUsingItems = newUsingItemsList.toArray(new UsingItem[0]);

                if (importStmt == newImportStatement){
                    final SourceModel.Import newImportStmt = SourceModel.Import.make(importedModuleName, newUsingItems);
                    SourcePosition startPosition = importStmt.getSourcePosition();
                    sourceModifier.addSourceModification(
                            new SourceModification.ReplaceText("", newImportStmt.toSourceText() + "\n\n", startPosition));
                }
                else{
                    SourcePosition startPosition = importStmt.getSourceRange().getStartSourcePosition();
                    int startIndex = startPosition.getPosition(sourceText, previousPosition, previousIndex);
                    SourcePosition endPosition = importStmt.getSourceRange().getEndSourcePosition();
                    int endIndex = endPosition.getPosition(sourceText, startPosition, startIndex);

                    SourceModel.Import newImportStmt = SourceModel.Import.make(importedModuleName, newUsingItems); 
                    sourceModifier.addSourceModification(new SourceModification.ReplaceText(sourceText.substring(startIndex, endIndex), newImportStmt.toSourceText(), startPosition));

                    previousPosition = endPosition;
                    previousIndex = endIndex;
                }
            }
        }
    
        return sourceModifier;
    }

    /**
     * This function creates a source modifier that will insert an import of the given module name.
     */
    public static SourceModifier getSourceModifier_insertImportOnly(ModuleContainer moduleContainer, ModuleName moduleName, ModuleName moduleToImport, String sourceText, final boolean ignoreErrors, CompilerMessageLogger messageLogger) {
        SourceModifier sourceModifier = new SourceModifier();
        
        // if the module is a sourceless module, then there is not much we can do with it.
        if (sourceText.length() == 0) {
            return sourceModifier;
        }
        
        int nErrorsBefore = messageLogger.getNErrors();
        SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(sourceText, ignoreErrors, messageLogger);
        if (!ignoreErrors && messageLogger.getNErrors() > nErrorsBefore || moduleDefn == null) {
            // We can't proceed if the module is unparseable
            return sourceModifier;
        }
        
        if(!moduleName.equals(SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName()))) {
            throw new IllegalArgumentException("moduleName must correspond to the module name in sourceText");
        }
        
        ModuleTypeInfo moduleTypeInfo = moduleContainer.getModuleTypeInfo(moduleName);
        if(moduleTypeInfo == null) {
            // We can't attempt to process a module that isn't in the workspace
            MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleName);
            messageLogger.logMessage(new CompilerMessage(messageKind));
            return sourceModifier;
        }
        
        Summarizer summarizer = new Summarizer(moduleTypeInfo.getModuleNameResolver());
        summarizer.visit_ModuleDefn(moduleDefn, ArrayStack.make());
        List<SourceModel.Import> importList = new ArrayList<SourceModel.Import>(summarizer.getImportStatements());
        
        if(importList.size() == 0) {
            return sourceModifier;
        }
        
        // Set (ModuleName) of imported modules post-refactoring
        Set/*ModuleName*/<ModuleName> unremovedImports = new HashSet<ModuleName>(); 
        for(int i = 0; i < moduleTypeInfo.getNImportedModules(); i++) {
            unremovedImports.add(moduleTypeInfo.getNthImportedModule(i).getModuleName());
        }
        
        // The import statement for the module that the 
        // symbols are imported from might not be present
        // so we have to add it. This code figure out if one
        // is missing and adds it.
        SourceModel.Import newImportStatement = null;
        {
            SourcePosition positionOfLastImportStatement = null;
            // If there already is an import statement then 
            for (final SourceModel.Import importStmt : importList) {                
                final SourcePosition importStmtSourcePosition = importStmt.getSourceRange().getEndSourcePosition().offsetPositionByText("\n\n");
                if (positionOfLastImportStatement == null){
                    positionOfLastImportStatement = importStmtSourcePosition;
                }
                if (SourcePosition.compareByPosition.compare(importStmtSourcePosition, positionOfLastImportStatement) > 0){
                    positionOfLastImportStatement = importStmtSourcePosition;
                }
                ModuleName currentModuleName = SourceModel.Name.Module.toModuleName(importStmt.getImportedModuleName());
                if (moduleToImport.equals(currentModuleName)){
                    return sourceModifier;
                }
            }

            newImportStatement = SourceModel.Import.makeAnnotated(Name.Module.make(moduleToImport), new UsingItem[0], new SourceRange(positionOfLastImportStatement, positionOfLastImportStatement));
            importList.add(newImportStatement);
        }

        {
            ModuleTypeInfo importedModuleTypeInfo = moduleContainer.getModuleTypeInfo(moduleToImport);
            if(importedModuleTypeInfo == null) {
                // We can't attempt to process a module that isn't in the workspace
                MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleToImport);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                sourceModifier.clearAllModifications();
                return sourceModifier;
            }
            // Calculate a new import statement
            List<UsingItem> newUsingItemsList = new ArrayList<UsingItem>();

            // Otherwise record a change that emits the new import statement
            {            
                UsingItem[] newUsingItems = newUsingItemsList.toArray(new UsingItem[0]);

                final SourceModel.Import newImportStmt = SourceModel.Import.make(moduleToImport, newUsingItems);
                SourcePosition startPosition = newImportStatement.getSourcePosition();
                sourceModifier.addSourceModification(
                        new SourceModification.ReplaceText("", newImportStmt.toSourceText() + "\n\n", startPosition));
            }
        }
    
        return sourceModifier;
    }
    
}
