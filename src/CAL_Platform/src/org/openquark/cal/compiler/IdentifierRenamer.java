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
 * IdentifierRenamer.java
 * Creation date: (Apr 5, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Friend;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;
import org.openquark.cal.compiler.SourceModel.CALDoc.CrossReference;
import org.openquark.cal.compiler.SourceModel.CALDoc.CrossReference.CanAppearWithoutContext;
import org.openquark.cal.compiler.SourceModel.CALDoc.CrossReference.Module;
import org.openquark.cal.compiler.SourceModel.CALDoc.TaggedBlock.See;
import org.openquark.cal.compiler.SourceModel.CALDoc.TextSegment.InlineTag.Link;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Algebraic;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Foreign;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Primitive;
import org.openquark.cal.compiler.SourceModel.Import.UsingItem;
import org.openquark.cal.compiler.SourceModel.InstanceDefn.InstanceMethod;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn.ClassMethodDefn;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.AlgebraicType;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.ForeignType;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn;
import org.openquark.util.Pair;


/**
 * IdentifierRenamer is a helper class for performing the Rename refactoring.
 * It calculates a set of SourceModifications that must be applied in order to 
 * perform a given renaming without conflicts.
 * 
 * <p> 
 * This class supersedes the RenamedIdentifierFinder class (by Iulian Radu).
 * 
 * <p>
 * <b>Binding conflicts</b>
 * <p>
 * One type of potential conflict occurs when an unqualified reference to the identifier
 * being renamed occurs in a context where the new name is already bound.  Eg, when 
 * renaming r to s in the following code:
 * <pre>
 *   let
 *       s = 10;
 *   in
 *       r s;
 * </pre>
 * 
 * <p>      
 * In these situations, the reference to r is renamed to a fully-qualified reference
 * to s (which refers to top-level identifiers only):
 * <pre>
 *   let
 *       s = 10;
 *   in
 *       M.s s;
 * </pre>
 * 
 * <p>
 * <b>Import-using conflict</b>
 * <p>
 * Another type of potential conflict occurs when an existing import-using clause
 * is changed to something that conflicts with either another import-using clause,
 * or with a toplevel definition.  For example, in the following code:
 * <pre>
 *     module Module;
 *     import Prelude using
 *        function = head, tail;
 *        ;
 *     import List using
 *        function = first;
 *        ;
 *    
 *     last x = ...
 * </pre>
 * <p>
 * Renaming Prelude.tail to Prelude.last could cause a conflict with the toplevel 
 * definition for last.  IdentifierRenamer deals with this by removing the using
 * clause for Prelude.tail/Prelude.last and fully qualifying all references to 
 * Prelude.tail when it renames them.
 * 
 * <p>
 * Similarly, renaming Prelude.head to Prelude.first in the above code could cause
 * a conflict (since the "first" symbol will appear in two using clauses).  IdentifierRenamer
 * deals with this situation in the same way: by removing the using-clause for the 
 * renamed identifier and fully qualifying all references to the renamed identifier.
 * 
 * <p>
 * In the case of the home module where the renamed identifier is defined, we
 * deal with this style of conflict by removing the conflicting using clause
 * and fully qualifying references to its identifier.
 * 
 * @author James Wright
 */
final class IdentifierRenamer {

    /** Not instantiable */
    private IdentifierRenamer() {}
    
    /**
     * Helper class for finding all references to a specific top-level identifier
     * in the context of a renaming operation.
     * 
     * <p>
     * References are tracked in several disjoint collections:
     * <ul>
     *    <li> Fully-qualified references
     *    <li> "Unobstructed" unqualified references
     *    <li> "Obstructed" unqualified references.
     *    <li> definition references
     *    <li> CALDoc elements that will become ambiguous
     *  </ul>
     *  
     *  <p>
     *  An obstructed unqualified reference is an unqualified reference to targetName
     *  that occurs in a context where newName is bound (so that a simple change
     *  from unqualified-targetName to unqualified-newName would change which value 
     *  was being referenced).
     *  
     *  <p>
     *  An unobstructed unqualified reference is an unqualified reference to targetName
     *  that occurs in a context where newName is not bound.
     *  
     *  <p>
     *  A definition reference is a reference that occurs in a declaration, or in a 
     *  similar context where an unqualified name is always unambiguous and a qualified 
     *  name is forbidden (eg, in using clauses, class method specifiers in instance
     *  definitions, etc).
     *  
     *  <p>
     *  We also track CALDoc elements that contain without-context cons references that 
     *  will become ambiguous after the renaming.
     *  
     *  <p>
     *  There are a couple of suppression flags to support special cases.  In particular,
     *  you can specify that import-using clauses should not be traversed, and you can
     *  provide a collection of See.WithoutContext elements to skip.
     * 
     * @author James Wright
     */
    private static class RenamableReferenceFinder extends BindingTrackingSourceModelTraverser<Object> {
        
        /** ModuleTypeInfo for the module being walked */
        private final ModuleTypeInfo moduleTypeInfo;
        
        /** Top-level identifier to find references to */
        private final QualifiedName targetName;
        
        /** Top-level identifier that targetName will be renamed to */
        private final QualifiedName newName;
        
        /** Category of targetName */
        private final Category category;
        
        /** When true, we will report references that occur in using clauses; when false, we won't. */
        private final boolean searchUsingClauses;
        
        /** 
         * Contains SeeBlock objects that should not be processed.  This allows us to avoid reporting
         * SourceRanges inside of seeBlocks that will be entirely re-generated.
         */
        private final Set<CALDoc> ignorableSeeBlocks = new HashSet<CALDoc>();
        
        /** List (SourceRange) of matching unobstructed unqualified Name elements */
        private final List<SourceRange> unobstructedUnqualifiedReferences = new ArrayList<SourceRange>();
        
        /** List (SourceRange) of matching obstructed unqualified Name elements */
        private final List<SourceRange> obstructedUnqualifiedReferences = new ArrayList<SourceRange>();
        
        /** List (SourceRange) of matching fully-qualified Name elements */
        private final List<SourceRange> fullyQualifiedReferences = new ArrayList<SourceRange>();
        
        /** List (SourceRange) of SourceRanges of definition references to targetName */
        private final List<SourceRange> definitionReferences = new ArrayList<SourceRange>();
        
        /** List (SourceRange) of SourceRanges of references to targetName (which is a module) appearing in module, friend and import statements. */
        private final List<SourceRange> moduleFriendImportStatementReferences = new ArrayList<SourceRange>();
        
        /** 
         * List (See.WithoutContext or Link.ConsNameWithoutContext) of contextless CALDoc elements
         * that will become ambiguous as a result of renaming targetName to newName.
         */
        private final List<CALDoc> ambiguousCALDocElements = new ArrayList<CALDoc>();
        
        /**
         * A mapping which maps module names that are affected by a module renaming to
         * their corresponding disambiguated names.
         */
        private final ModuleNameResolver.RenameMapping collateralDamageModuleRenameMapping;
        
        /**
         * A list of pairs (module name source range, module name) representing the module names that are affected
         * by a module renaming (and thus would need disambiguation).
         */
        private final List<Pair<SourceRange, ModuleName>> collateralDamageModuleNames = new ArrayList<Pair<SourceRange, ModuleName>>();
        
        /**
         * 
         * @param targetName QualifiedName that we are looking for references to
         * @param newName QualifiedName that targetName will be renamed to.  We use this to determine when an identifier
         *                 is obstructed.
         * @param category Category of targetName
         * @param searchUsingClauses When true, we will report references that occur in using clauses; when false, we won't.
         * @param ignorableSeeBlocks Collection (See.WithoutContext) of see blocks that should not be processed.
         *                            Uses object identity for equality, so these blocks must be from the same SourceModel
         *                            that this object will be traversing.
         * @param moduleTypeInfo ModuleTypeInfo for the module being traversed
         * @param collateralDamageModuleRenameMapping a mapping which maps module names that are affected by a module renaming to
         *                                             their corresponding disambiguated names.
         */
        RenamableReferenceFinder(QualifiedName targetName, QualifiedName newName, Category category, boolean searchUsingClauses, Collection<CALDoc> ignorableSeeBlocks, ModuleTypeInfo moduleTypeInfo, ModuleNameResolver.RenameMapping collateralDamageModuleRenameMapping) {
            if(targetName == null || category == null || moduleTypeInfo == null || collateralDamageModuleRenameMapping == null) {
                throw new NullPointerException();
            }
            
            if(category == Category.LOCAL_VARIABLE || category == Category.LOCAL_VARIABLE_DEFINITION) {
                throw new UnsupportedOperationException();
            }
            
            this.moduleTypeInfo = moduleTypeInfo;
            this.targetName = targetName;
            this.newName = newName;
            this.category = category;
            this.searchUsingClauses = searchUsingClauses;
            
            if(ignorableSeeBlocks != null) {
                this.ignorableSeeBlocks.addAll(ignorableSeeBlocks);
            }
            
            this.collateralDamageModuleRenameMapping = collateralDamageModuleRenameMapping;
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_ModuleDefn(ModuleDefn defn, Object arg) {
            if(category == Category.MODULE_NAME && org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(defn.getModuleName()).equals(targetName.getModuleName())) {
                moduleFriendImportStatementReferences.add(defn.getSourceRangeOfModuleName());
            }
            return super.visit_ModuleDefn(defn, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_Friend(Friend friend, Object arg) {
            if(category == Category.MODULE_NAME && org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(friend.getFriendModuleName()).equals(targetName.getModuleName())) {
                moduleFriendImportStatementReferences.add(friend.getFriendModuleName().getSourceRange());
            }
            return super.visit_Friend(friend, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_Import(Import importStatement, Object arg) {
            if(category == Category.MODULE_NAME && org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(importStatement.getImportedModuleName()).equals(targetName.getModuleName())) {
                moduleFriendImportStatementReferences.add(importStatement.getImportedModuleName().getSourceRange());
            }
            return super.visit_Import(importStatement, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Object visit_Import_UsingItem_Function(UsingItem.Function usingItemFunction, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("RenameableReferenceFinder.visitImportUsingItemFunction expects to be passed a module name as its arg");
            }
            
            // Ignore using items of the wrong category
            if(category != Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
                return super.visit_Import_UsingItem_Function(usingItemFunction, arg);
            }
            
            // Skip any import that isn't for the target module
            ModuleName importedModuleName = (ModuleName)arg;
            if(!searchUsingClauses || !importedModuleName.equals(targetName.getModuleName())) {
                return super.visit_Import_UsingItem_Function(usingItemFunction, arg);
            }
            
            String[] usingNames = usingItemFunction.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemFunction.getUsingNameSourceRanges();
            
            for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                if(usingNames[i].equals(targetName.getUnqualifiedName())) {
                    definitionReferences.add(usingNameSourceRanges[i]);
                }
            }
            
            return super.visit_Import_UsingItem_Function(usingItemFunction, arg);
        }
            
        /** {@inheritDoc} */
        @Override
        public Object visit_Import_UsingItem_DataConstructor(UsingItem.DataConstructor usingItemDataConstructor, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("RenameableReferenceFinder.visitImportUsingItemDataConstructor expects to be passed a module name as its arg");
            }
            
            // Ignore using items of the wrong category
            if(category != Category.DATA_CONSTRUCTOR) {
                return super.visit_Import_UsingItem_DataConstructor(usingItemDataConstructor, arg);
            }
            
            // Skip any import that isn't for the target module
            ModuleName importedModuleName = (ModuleName)arg;
            if(!searchUsingClauses || !importedModuleName.equals(targetName.getModuleName())) {
                return super.visit_Import_UsingItem_DataConstructor(usingItemDataConstructor, arg);
            }
            
            String[] usingNames = usingItemDataConstructor.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemDataConstructor.getUsingNameSourceRanges();
            
            for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                if(usingNames[i].equals(targetName.getUnqualifiedName())) {
                    definitionReferences.add(usingNameSourceRanges[i]);
                }
            }
            
            return super.visit_Import_UsingItem_DataConstructor(usingItemDataConstructor, arg);
        }
            
        /** {@inheritDoc} */
        @Override
        public Object visit_Import_UsingItem_TypeConstructor(UsingItem.TypeConstructor usingItemTypeConstructor, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("RenameableReferenceFinder.visitImportUsingItemTypeConstructor expects to be passed a module name as its arg");
            }
            
            // Ignore using items of the wrong category
            if(category != Category.TYPE_CONSTRUCTOR) {
                return super.visit_Import_UsingItem_TypeConstructor(usingItemTypeConstructor, arg);
            }
            
            // Skip any import that isn't for the target module
            ModuleName importedModuleName = (ModuleName)arg;
            if(!searchUsingClauses || !importedModuleName.equals(targetName.getModuleName())) {
                return super.visit_Import_UsingItem_TypeConstructor(usingItemTypeConstructor, arg);
            }
            
            String[] usingNames = usingItemTypeConstructor.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemTypeConstructor.getUsingNameSourceRanges();
            
            for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                if(usingNames[i].equals(targetName.getUnqualifiedName())) {
                    definitionReferences.add(usingNameSourceRanges[i]);
                }
            }
            
            return super.visit_Import_UsingItem_TypeConstructor(usingItemTypeConstructor, arg);
        }
            
        /** {@inheritDoc} */
        @Override
        public Object visit_Import_UsingItem_TypeClass(UsingItem.TypeClass usingItemTypeClass, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("RenameableReferenceFinder.visitImportUsingItemTypeClass expects to be passed a module name as its arg");
            }
            
            // Ignore using items of the wrong category
            if(category != Category.TYPE_CLASS) {
                return super.visit_Import_UsingItem_TypeClass(usingItemTypeClass, arg);
            }
            
            // Skip any import that isn't for the target module
            ModuleName importedModuleName = (ModuleName)arg;
            if(!searchUsingClauses || !importedModuleName.equals(targetName.getModuleName())) {
                return super.visit_Import_UsingItem_TypeClass(usingItemTypeClass, arg);
            }
            
            String[] usingNames = usingItemTypeClass.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemTypeClass.getUsingNameSourceRanges();
            
            for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                if(usingNames[i].equals(targetName.getUnqualifiedName())) {
                    definitionReferences.add(usingNameSourceRanges[i]);
                }
            }
            
            return super.visit_Import_UsingItem_TypeClass(usingItemTypeClass, arg);
        }
            
        /** {@inheritDoc} */
        @Override
        public Object visit_CALDoc_TextSegment_InlineTag_Link_ConsNameWithoutContext(Link.ConsNameWithoutContext segment, Object arg) {
            Object needsContext = segment.getReference().accept(this, arg);
            if(needsContext == Boolean.TRUE) {
                ambiguousCALDocElements.add(segment);
            }

            // No need to call super implementation since all it does is call: segment.getReference().accept(this, arg);
            // which is already done above.
            // Repeating the visitation to the reference would mean that visit_Name_WithoutContextCons()
            // would be called twice on the same element, which can potentially corrupt the various data structures.
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_CALDoc_TaggedBlock_See_WithoutContext(See.WithoutContext seeBlock, Object arg) {
            
            if(ignorableSeeBlocks.contains(seeBlock)) {
                // Don't process this block at all (including recursive processing) if it is ignorable
                return null;
            }
            
            // First check to see if we need to add context to any of our references
            for (int i = 0, nReferencedNames = seeBlock.getNReferencedNames(); i < nReferencedNames; i++) {
                CanAppearWithoutContext reference = seeBlock.getNthReferencedName(i);
                if(reference instanceof CrossReference.WithoutContextCons) {
                    CrossReference.WithoutContextCons consReference = (CrossReference.WithoutContextCons)reference;
                    if(willConflictWith(moduleTypeInfo, consReference.getName(), newName, category)) {

                        // We do /not/ continue recursive processing if we discover that we need to add context to a reference.
                        // That's because adding context requires us to replace the entire @see block.  Recursive processing 
                        // might discover some individual references (different from the conflicting ones) that need to be 
                        // renamed, which could cause us to attempt overlapping SourceModifications, which is an error.
                        // So we will process the see block as a whole and handle renamings and conflict resolution in one pass.
                        ambiguousCALDocElements.add(seeBlock);
                        return null;
                    }
                    
                }
            }

            // If there's no conflicts, then we don't need to do any see-block-wide replacements, so it's safe to 
            // consider each sub-reference individually.
            return super.visit_CALDoc_TaggedBlock_See_WithoutContext(seeBlock, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_CALDoc_CrossReference_WithoutContextCons(org.openquark.cal.compiler.SourceModel.CALDoc.CrossReference.WithoutContextCons reference, Object arg) {
            return reference.getName().accept(this, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_Name_WithoutContextCons(Name.WithoutContextCons cons, Object arg) {
            
            // Special case: When we're renaming an entity to have the name as an existing context-free cons reference,
            // then we will need to add context to the reference (since it is no longer unambiguous).
            if(willConflictWith(moduleTypeInfo, cons, newName, category)) {
                
                super.visit_Name_WithoutContextCons(cons, arg);
                
                // Indicate to our CrossReference parent that this name needs context
                return Boolean.TRUE;
            }
            
            // Not interested in wrong category or wrong unqualified name
            Category consCategory = getCategory(moduleTypeInfo, cons);
            
            if (consCategory == category) {
                
                if (category == Category.MODULE_NAME) {

                    ModuleName targetModuleName = targetName.getModuleName();

                    // the entire WithoutContextCons could be a module name
                    ModuleName rawModuleName = ModuleName.make(cons.toSourceText());
                    ModuleName resolvedModuleName = moduleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();

                    if (resolvedModuleName.equals(targetModuleName)) {
                        definitionReferences.add(cons.getSourceRange());
                    } else {
                        if (collateralDamageModuleRenameMapping.hasNewName(rawModuleName)) {
                            ModuleName newModuleName = collateralDamageModuleRenameMapping.getNewNameForModule(rawModuleName);
                            collateralDamageModuleNames.add(new Pair<SourceRange, ModuleName>(cons.getSourceRange(), newModuleName));
                        }
                    }
                }

                if (cons.getUnqualifiedName().equals(targetName.getUnqualifiedName())) {

                    ModuleName consModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(cons.getModuleName()); // may be null

                    if(consModuleName == null) {
                        unobstructedUnqualifiedReferences.add(cons.getSourceRange());

                    } else {
                        ModuleName resolvedConsModuleName = moduleTypeInfo.getModuleNameResolver().resolve(consModuleName).getResolvedModuleName();

                        if (resolvedConsModuleName.equals(targetName.getModuleName())) {             
                            fullyQualifiedReferences.add(cons.getSourceRange());
                        }
                    }
                }
            }
            
            return super.visit_Name_WithoutContextCons(cons, arg);
        }
        
        /**
         * Process a constructor name (type constructor, data constructor or type class name).
         * @param name the constructor name.
         * @param nameCategory the category of the name.
         */
        private void processConsName(Name.Qualifiable name, Category nameCategory) {
            
            if (category == Category.MODULE_NAME) {

                if (name.getModuleName() != null) {
                    ModuleName rawModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(name.getModuleName());
                    ModuleName resolvedModuleName = moduleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();

                    if (targetName.getModuleName().equals(resolvedModuleName)) {
                        definitionReferences.add(name.getModuleName().getSourceRange());
                    } else {
                        if (collateralDamageModuleRenameMapping.hasNewName(rawModuleName)) {
                            ModuleName newModuleName = collateralDamageModuleRenameMapping.getNewNameForModule(rawModuleName);
                            collateralDamageModuleNames.add(new Pair<SourceRange, ModuleName>(name.getModuleName().getSourceRange(), newModuleName));
                        }
                    }
                }
            }
            
            if (category == nameCategory && name.getUnqualifiedName().equals(targetName.getUnqualifiedName())) {

                if(name.getModuleName() == null && targetName.equals(getQualifiedName(name, moduleTypeInfo.getModuleNameResolver()))) {

                    unobstructedUnqualifiedReferences.add(name.getSourceRange());

                } else if (name.getModuleName() != null) {
                    ModuleName consModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(name.getModuleName());
                    ModuleName resolvedConsModuleName = moduleTypeInfo.getModuleNameResolver().resolve(consModuleName).getResolvedModuleName();
                    
                    if (resolvedConsModuleName.equals(targetName.getModuleName())) {             
                        fullyQualifiedReferences.add(name.getSourceRange());
                    }
                }
            }
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_Name_DataCons(Name.DataCons cons, Object arg) {
            
            processConsName(cons, Category.DATA_CONSTRUCTOR);
            
            return super.visit_Name_DataCons(cons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_Name_TypeClass(Name.TypeClass typeClass, Object arg) {
            
            processConsName(typeClass, Category.TYPE_CLASS);
            
            return super.visit_Name_TypeClass(typeClass, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_Name_TypeCons(SourceModel.Name.TypeCons cons, Object arg) {
            
            processConsName(cons, Category.TYPE_CONSTRUCTOR);
            
            return super.visit_Name_TypeCons(cons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_Name_Function(Name.Function function, Object arg) {

            if (category == Category.MODULE_NAME) {

                if (function.getModuleName() != null) {
                    ModuleName rawModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(function.getModuleName());
                    ModuleName resolvedModuleName = moduleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();

                    if (targetName.getModuleName().equals(resolvedModuleName)) {
                        definitionReferences.add(function.getModuleName().getSourceRange());
                    } else {
                        if (collateralDamageModuleRenameMapping.hasNewName(rawModuleName)) {
                            ModuleName newModuleName = collateralDamageModuleRenameMapping.getNewNameForModule(rawModuleName);
                            collateralDamageModuleNames.add(new Pair<SourceRange, ModuleName>(function.getModuleName().getSourceRange(), newModuleName));
                        }
                    }
                }
            }

            if (category == Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD && function.getUnqualifiedName().equals(targetName.getUnqualifiedName())) {

                if(function.getModuleName() == null && targetName.equals(getQualifiedName(function, moduleTypeInfo.getModuleNameResolver()))) {

                    if(isObstructed()) {
                        obstructedUnqualifiedReferences.add(function.getSourceRange());
                    } else {
                        unobstructedUnqualifiedReferences.add(function.getSourceRange());
                    }

                } else if (function.getModuleName() != null) {
                    ModuleName consModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(function.getModuleName());
                    ModuleName resolvedConsModuleName = moduleTypeInfo.getModuleNameResolver().resolve(consModuleName).getResolvedModuleName();

                    if (resolvedConsModuleName.equals(targetName.getModuleName())) {             
                        fullyQualifiedReferences.add(function.getSourceRange());
                    }
                }
            }

            return super.visit_Name_Function(function, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Object visit_FunctionDefn_Algebraic(Algebraic algebraic, Object arg) {

            if(category != Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_FunctionDefn_Algebraic(algebraic, arg);
            }
            
            if(algebraic.getName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(algebraic.getNameSourceRange());
            }
            
            return super.visit_FunctionDefn_Algebraic(algebraic, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_FunctionDefn_Foreign(Foreign foreign, Object arg) {
            if(category != Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_FunctionDefn_Foreign(foreign, arg);
            }
            
            if(foreign.getName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(foreign.getNameSourceRange());
            }
            
            return super.visit_FunctionDefn_Foreign(foreign, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_FunctionDefn_Primitive(Primitive primitive, Object arg) {
            if(category != Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_FunctionDefn_Primitive(primitive, arg);
            }
            
            if(primitive.getName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(primitive.getNameSourceRange());
            }
            
            return super.visit_FunctionDefn_Primitive(primitive, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_TypeConstructorDefn_AlgebraicType(AlgebraicType type, Object arg) {

            if(category != Category.TYPE_CONSTRUCTOR ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_TypeConstructorDefn_AlgebraicType(type, arg);
            }
            
            if(type.getTypeConsName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(type.getSourceRangeOfName());
            }
            
            return super.visit_TypeConstructorDefn_AlgebraicType(type, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_TypeConstructorDefn_ForeignType(ForeignType type, Object arg) {
            if(category != Category.TYPE_CONSTRUCTOR ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_TypeConstructorDefn_ForeignType(type, arg);
            }

            if(type.getTypeConsName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(type.getSourceRangeOfName());
            }
            
            return super.visit_TypeConstructorDefn_ForeignType(type, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Object visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(DataConsDefn defn, Object arg) {
            if(category != Category.DATA_CONSTRUCTOR ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg);
            }

            if(defn.getDataConsName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(defn.getSourceRangeOfName());
            }
            
            return super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_TypeClassDefn(TypeClassDefn defn, Object arg) {
            if(category != Category.TYPE_CLASS ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_TypeClassDefn(defn, arg);
            }
            
            if(defn.getTypeClassName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(defn.getSourceRangeOfName());
            }
            
            return super.visit_TypeClassDefn(defn, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_TypeClassDefn_ClassMethodDefn(ClassMethodDefn defn, Object arg) {
            if(category != Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_TypeClassDefn_ClassMethodDefn(defn, arg);
            }
            
            if(defn.getMethodName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(defn.getSourceRangeOfName());
            }
            
            return super.visit_TypeClassDefn_ClassMethodDefn(defn, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_FunctionTypeDeclaraction(FunctionTypeDeclaration declaration, Object arg) {
            if(category != Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
               !targetName.getModuleName().equals(getModuleName())) {
                return super.visit_FunctionTypeDeclaraction(declaration, arg);
            }
            
            if(declaration.getFunctionName().equals(targetName.getUnqualifiedName())) {
                definitionReferences.add(declaration.getSourceRangeOfName());
            }
            
            return super.visit_FunctionTypeDeclaraction(declaration, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Object visit_InstanceDefn(InstanceDefn defn, Object arg) {

            // We pass the type class name so that the method visitor methods can infer
            // the module name of the class methods.
            return super.visit_InstanceDefn(defn, defn.getTypeClassName());
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_InstanceDefn_InstanceMethod(InstanceMethod method, Object arg) {
            SourceModel.Name.Qualifiable typeClassName = (SourceModel.Name.Qualifiable)arg;
            QualifiedName qualifiedTypeClass = getQualifiedName(typeClassName, moduleTypeInfo.getModuleNameResolver());
            
            if(category == Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD &&
               targetName.getModuleName().equals(qualifiedTypeClass.getModuleName()) &&
               targetName.getUnqualifiedName().equals(method.getClassMethodName())) {
                
                definitionReferences.add(method.getClassMethodNameSourceRange());
            }
            
            return super.visit_InstanceDefn_InstanceMethod(method, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Object visit_CALDoc_CrossReference_Module(Module reference, Object arg) {
            if (category == Category.MODULE_NAME) {
                
                ModuleName rawModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(reference.getName());
                ModuleName resolvedModuleName = moduleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
                
                if (targetName.getModuleName().equals(resolvedModuleName)) {
                    definitionReferences.add(reference.getName().getSourceRange());
                } else {
                    if (collateralDamageModuleRenameMapping.hasNewName(rawModuleName)) {
                        ModuleName newModuleName = collateralDamageModuleRenameMapping.getNewNameForModule(rawModuleName);
                        collateralDamageModuleNames.add(new Pair<SourceRange, ModuleName>(reference.getName().getSourceRange(), newModuleName));
                    }
                }
            }
            
            return super.visit_CALDoc_CrossReference_Module(reference, arg);
        }
    
        /**
         * @return True if the currently-visited source element is obstructed, or false otherwise.
         *          When there is no newName, this always returns false.
         */
        private boolean isObstructed() {
            if(newName == null) {
                return false;
            }
            
            return isBound(newName.getUnqualifiedName());
        }
        
        /** 
         * @return List (SourceRange) of all unobstructed unqualified Name elements that matched the
         * search criteria.
         */
        List<SourceRange> getUnobstructedUnqualifiedReferences() {
            return Collections.unmodifiableList(unobstructedUnqualifiedReferences);
        }
        
        /** 
         * @return List (SourceRange) of all obstructed unqualified Name elements that matched the
         * search criteria.
         */
        List<SourceRange> getObstructedUnqualifiedReferences() {
            return Collections.unmodifiableList(obstructedUnqualifiedReferences);
        }
        
        /** 
         * @return List (SourceRange) of all qualified Name elements that matched the search criteria.
         */
        List<SourceRange> getFullyQualifiedReferences() {
            return Collections.unmodifiableList(fullyQualifiedReferences);
        }
        
        /**
         * @return List (SourceRange) of all definition references that matched the search criteria
         */
        List<SourceRange> getDefinitionReferences() {
            return Collections.unmodifiableList(definitionReferences);
        }
        
        /**
         * @return List (SourceRange) of SourceRanges of references to targetName (which is a module) appearing in module, friend and import statements.
         */
        List<SourceRange> getModuleFriendImportStatementReferences() {
            return Collections.unmodifiableList(moduleFriendImportStatementReferences);
        }
        
        /**
         * @return List (CrossReference.WithoutContextCons) of CrossReference.WithoutContextConses that refer either 
         * to newName's entity or to an entity with the same unqualified name as newName, and which will become ambiguous 
         * after the renaming. 
         */
        List<CALDoc> getAmbiguousCALDocElements() {
            return Collections.unmodifiableList(ambiguousCALDocElements);
        }
        
        /**
         * @return a list of pairs (module name source range, module name) representing the module names that are affected
         * by a module renaming (and thus would need disambiguation).
         */
        List<Pair<SourceRange, ModuleName>> getCollateralDamageModuleNames() {
            return Collections.unmodifiableList(collateralDamageModuleNames);
        }
    }
    
    /**
     * @param moduleTypeInfo ModuleTypeInfo for the module to process 
     * @param oldName QualifiedName of the identifier being renamed
     * @param newName QualifiedName to rename oldName to
     * @param category Category of the identifier being renamed
     * @param messageLogger CompilerMessageLogger for logging failures
     * @return a SourceModifier that will apply the renaming to the module specified by moduleName. 
     */
    static SourceModifier getSourceModifier(ModuleTypeInfo moduleTypeInfo, String sourceText, QualifiedName oldName, QualifiedName newName, Category category, CompilerMessageLogger messageLogger) {

        SourceModifier sourceModifier = new SourceModifier();
        
        // if the module is a sourceless module, then there is not much we can do with it.
        if (sourceText.length() == 0) {
            return sourceModifier;
        }

        ModuleName moduleName = moduleTypeInfo.getModuleName();        
        String qualifiedNewName = newName.getQualifiedName();
        String unqualifiedNewName = newName.getUnqualifiedName();

        SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(sourceText, messageLogger);
        
        if(moduleDefn == null) {
            return sourceModifier;
        }
        
        ModuleNameResolver.RenameMapping collateralDamageModuleRenameMapping;
        if (category == Category.MODULE_NAME) {
            ModuleName oldModuleName = oldName.getModuleName();
            ModuleName newModuleName = newName.getModuleName();
            
            collateralDamageModuleRenameMapping = moduleTypeInfo.getModuleNameResolver().makeRenameMapping(oldModuleName, newModuleName);
        } else {
            collateralDamageModuleRenameMapping = moduleTypeInfo.getModuleNameResolver().makeEmptyRenameMapping();
        }
        
        ModuleName newUsingModule = getModuleOfUsingIdentifier(moduleTypeInfo, unqualifiedNewName, category);
        ModuleName oldUsingModule = getModuleOfUsingIdentifier(moduleTypeInfo, oldName.getUnqualifiedName(), category);

        // Special case: if this module has a toplevel definition called unqualifiedNewName,
        // and has an import-using clause for oldName, then we need to remove the import-using
        // clause for oldName and make all new references to newName fully-qualified.
        boolean qualifyAllRenamedReferences = false;
        boolean renameInUsingClauses = true;
        if(oldUsingModule != null && getEntityFromModule(moduleTypeInfo, unqualifiedNewName, category) != null) {
            // Remove oldName's import-using clause
            sourceModifier.addSourceModification(makeUsingClauseRemovalModification(sourceText, moduleDefn, oldName, category));

            // Don't rename oldName's import-using clause, because it's being removed
            renameInUsingClauses = false;
            
            // All references to newName need to be fully-qualified
            qualifyAllRenamedReferences = true;
        }
        
        RenamableReferenceFinder oldNameFinder = new RenamableReferenceFinder(oldName, newName, category, renameInUsingClauses, null, moduleTypeInfo, collateralDamageModuleRenameMapping);
        oldNameFinder.visit_ModuleDefn(moduleDefn, null);

        // Special case: If this is the module where oldName is defined and an identifier whose unqualified
        // name is the same as newName's is already imported in an import-using clause, then we need to:
        //  1) remove N.g's using clause
        //  2) fully-qualify every reference to N.g
        //
        // Similar special case: If this module has import-using clauses for both oldName and also for 
        // a different identifier with the same unqualified name as newName, then we need to resolve that
        // conflict as well.  We resolve it in the same way: Remove the occurrence of newName from the
        // using clause and explicitly qualify all of its references.
        if((oldName.getModuleName().equals(moduleName) && newUsingModule != null) ||
           (newUsingModule != null && oldUsingModule != null)) {

            QualifiedName fullQualificationTarget = QualifiedName.make(newUsingModule, unqualifiedNewName);
            String fullQualificationTargetStr = fullQualificationTarget.getQualifiedName();
            
            // Remove newUsingModule.newUnqualifiedName's import-using clause
            sourceModifier.addSourceModification(makeUsingClauseRemovalModification(sourceText, moduleDefn, fullQualificationTarget, category));
            
            // Unqualified references to newName become fully-qualified references to newName
            RenamableReferenceFinder fullQualificationTargetFinder = new RenamableReferenceFinder(fullQualificationTarget, null, category, false, oldNameFinder.getAmbiguousCALDocElements(), moduleTypeInfo, collateralDamageModuleRenameMapping);
            fullQualificationTargetFinder.visit_ModuleDefn(moduleDefn, null);
            
            for (final SourceRange sourceRange : fullQualificationTargetFinder.getUnobstructedUnqualifiedReferences()) {
                sourceModifier.addSourceModification(makeReplaceText(sourceText, sourceRange, fullQualificationTargetStr));
            }
        }
        
        // Fully-qualified references to oldName become fully-qualified references to newName
        for (final SourceRange sourceRange : oldNameFinder.getFullyQualifiedReferences()) {
            sourceModifier.addSourceModification(makeReplaceText(sourceText, sourceRange, qualifiedNewName));
        }
        
        // Obstructed unqualified references to oldName become fully-qualified references to newName
        for (final SourceRange sourceRange : oldNameFinder.getObstructedUnqualifiedReferences()) {
            sourceModifier.addSourceModification(makeReplaceText(sourceText, sourceRange, qualifiedNewName));
        }
        
        // Unobstructed unqualified references to oldName become unqualified references to newName
        // (unless we are in a special case flagged by qualifyAllRenamedReferences)
        for (final SourceRange sourceRange : oldNameFinder.getUnobstructedUnqualifiedReferences()) {
            if(qualifyAllRenamedReferences) {
                sourceModifier.addSourceModification(makeReplaceText(sourceText, sourceRange, qualifiedNewName));
            } else {
                sourceModifier.addSourceModification(makeReplaceText(sourceText, sourceRange, unqualifiedNewName));
            }
        }
        
        // Definition references to oldName become definition references to newName
        final String definitionReferenceReplacementName;
        if (category == Category.MODULE_NAME) {
            // if it is a module renaming, we try to be smart and use the minimally qualified form of the new module name.
            
            ModuleNameResolver newResolver = moduleTypeInfo.getModuleNameResolver().getResolverAfterRenaming(oldName.getModuleName(), newName.getModuleName());
            ModuleName minimallyQualifiedNewModuleName = newResolver.getMinimallyQualifiedModuleName(newName.getModuleName());
            
            definitionReferenceReplacementName = minimallyQualifiedNewModuleName.toSourceText();
        } else {
            definitionReferenceReplacementName = unqualifiedNewName;
        }
        
        for (final SourceRange sourceRange : oldNameFinder.getDefinitionReferences()) {
            sourceModifier.addSourceModification(makeReplaceText(sourceText, sourceRange, definitionReferenceReplacementName));
        }

        // Module, import and friend statement references to oldName become references to newName, in its fully qualified form. 
        for (final SourceRange sourceRange : oldNameFinder.getModuleFriendImportStatementReferences()) {
            sourceModifier.addSourceModification(makeReplaceText(sourceText, sourceRange, newName.getModuleName().toSourceText()));
        }

        // Ambiguous CALDoc elements that contain references to WithoutContextConses
        // need to be "contextified".
        for (final CALDoc caldocElement : oldNameFinder.getAmbiguousCALDocElements()) {
            if(caldocElement instanceof Link.ConsNameWithoutContext) {
                sourceModifier.addSourceModification(makeContextifyLinkModification(moduleTypeInfo, sourceText, (Link.ConsNameWithoutContext)caldocElement, collateralDamageModuleRenameMapping));
            } else if(caldocElement instanceof See.WithoutContext) {
                sourceModifier.addSourceModification(makeContextifySeeBlockModification(moduleTypeInfo, sourceText, (See.WithoutContext)caldocElement, oldName, newName, category, qualifyAllRenamedReferences, collateralDamageModuleRenameMapping));
            }
        }
        
        // (Short-form) module names that are affected by a module renaming become the fully-qualified module names they resolve to
        for (final Pair<SourceRange, ModuleName> pair : oldNameFinder.getCollateralDamageModuleNames()) {
            SourceRange collateralDamageModuleNameSourceRange = pair.fst();
            ModuleName fixedModuleName = pair.snd();
            
            sourceModifier.addSourceModification(makeReplaceText(sourceText, collateralDamageModuleNameSourceRange, fixedModuleName.toSourceText()));
        }

        return sourceModifier;
    }
    
    /**
     * Generates a SourceModification that will replace segment with a new link that includes an explicit
     * context specifier.
     * @param moduleTypeInfo ModuleTypeInfo object for the module being processed
     * @param sourceText String sourceText being processed
     * @param segment Link.ConsNameWithoutContext A context-free CALDoc link element for a cons name
     * @param collateralDamageModuleRenameMapping a mapping which maps module names that are affected by a module renaming to
     *                                             their corresponding disambiguated names. 
     * @return A SourceModifier that will replace segment with an equivalent CALDoc element that explicitly
     *          specifies the context of its reference.
     */
    private static SourceModification makeContextifyLinkModification(ModuleTypeInfo moduleTypeInfo, String sourceText, Link.ConsNameWithoutContext segment, ModuleNameResolver.RenameMapping collateralDamageModuleRenameMapping) {
        
        CrossReference.WithoutContextCons oldReference = segment.getReference();
        Name.WithoutContextCons cons = oldReference.getName();
        Category consCategory = getCategory(moduleTypeInfo, cons);
        
        CALDoc.TextSegment.InlineTag.Link newSegment;
        if(consCategory == Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
           consCategory == Category.LOCAL_VARIABLE ||
           consCategory == Category.LOCAL_VARIABLE_DEFINITION) {
            throw new IllegalStateException("WithoutContextCons shouldn't have a function/method category!");
        
        } else if(consCategory == Category.MODULE_NAME) {
            ModuleName consNameAsModuleName = ModuleName.make(cons.toSourceText());
            
            Name.Module moduleName = Name.Module.make(collateralDamageModuleRenameMapping.getNewNameForModule(consNameAsModuleName));
            newSegment = CALDoc.TextSegment.InlineTag.Link.Module.make(CrossReference.Module.make(moduleName, oldReference.isChecked()));
        
        } else {
            ModuleName moduleName = org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(cons.getModuleName()); // may be null
            
            if(consCategory == Category.DATA_CONSTRUCTOR) {
                Name.DataCons dataCons = Name.DataCons.make(collateralDamageModuleRenameMapping.getNewNameForModule(moduleName), cons.getUnqualifiedName());
                newSegment = CALDoc.TextSegment.InlineTag.Link.DataCons.make(CrossReference.DataCons.make(dataCons, oldReference.isChecked()));

            } else if(consCategory == Category.TYPE_CONSTRUCTOR) {
                Name.TypeCons typeCons = SourceModel.Name.TypeCons.make(collateralDamageModuleRenameMapping.getNewNameForModule(moduleName), cons.getUnqualifiedName());
                newSegment = CALDoc.TextSegment.InlineTag.Link.TypeCons.make(CrossReference.TypeCons.make(typeCons, oldReference.isChecked()));

            } else if(consCategory == Category.TYPE_CLASS) {
                Name.TypeClass typeClass = Name.TypeClass.make(collateralDamageModuleRenameMapping.getNewNameForModule(moduleName), cons.getUnqualifiedName());
                newSegment = CALDoc.TextSegment.InlineTag.Link.TypeClass.make(CrossReference.TypeClass.make(typeClass, oldReference.isChecked()));

            } else {
                throw new IllegalStateException("Unexpected category");
            }
        }
     
        return makeReplaceText(sourceText, segment.getSourceRange(), newSegment.toString());
    }

    /**
     * Checks whether a context-free cons name will conflict with the entity specified by entityName and entityCategory.
     * This is a weaker claim than the claim that it refers to the entity.
     * 
     * We assume that cons isn't /currently/ in conflict (ie, that it is an unambiguous reference).
     * 
     * @param moduleTypeInfo ModuleTypeInfo for the module being processed
     * @param cons Name to check for conflicts
     * @param entityName Name of the entity to check for conflicts with cons
     * @param entityCategory Category of the entity to check for conflicts with cons
     * @return true if cons conflicts with the entity specified by entityName and entityCategory
     */
    private static boolean willConflictWith(ModuleTypeInfo moduleTypeInfo, Name.WithoutContextCons cons, QualifiedName entityName, Category entityCategory) {
        if(entityName == null) {
            return false;
        }
        
        if(entityCategory == Category.MODULE_NAME) {
            ModuleName moduleName = entityName.getModuleName();
            
            // the entire WithoutContextCons could be a module name
            // e.g. renaming module M -> L.M makes {@link L.M@} ambiguous if module L (or X.L, X.Y.L etc.) has visible entity M
            
            // no module name resolution required here, since we're doing a purely lexical comparison
            return ModuleName.make(cons.toSourceText()).equals(moduleName);
        }
        
        String consUnqualifiedName = cons.getUnqualifiedName();
        ModuleName consModule = org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(cons.getModuleName()); // may be null
        
        return entityName.getUnqualifiedName().equals(consUnqualifiedName) &&
               (consModule == null || entityName.getModuleName().equals(consModule));
    }
    
    /**
     * Checks whether name refers to the entity specified by qualifiedName and category.  WithoutContextCons names are
     * assumed to be unambiguous.
     * 
     * @param moduleTypeInfo ModuleTypeInfo for the module being processed
     * @param name Name to check
     * @param qualifiedName Name of the entity that name perhaps refers to
     * @param category Category of the entity that name perhaps refers to
     * @return True if name refers to the entity specified by qualifiedName and category
     */
    private static boolean refersTo(ModuleTypeInfo moduleTypeInfo, Name.Qualifiable name, QualifiedName qualifiedName, Category category) {
        ModuleName qualifiedModuleName = qualifiedName.getModuleName();
        
        if (category == Category.MODULE_NAME) {
            
            if (name instanceof Name.WithoutContextCons) {
                ModuleName consNameAsModuleName = ModuleName.make(name.toSourceText());
                ModuleName resolvedModuleName = moduleTypeInfo.getModuleNameResolver().resolve(consNameAsModuleName).getResolvedModuleName();
                
                // the module name to be checked against will be in qualifiedName's module name field.
                return resolvedModuleName.equals(qualifiedModuleName);
                
            } else {
                return false;
            }
        }
        
        String unqualifiedName = name.getUnqualifiedName();
        
        if(!unqualifiedName.equals(qualifiedName.getUnqualifiedName())) {
            return false;
        }
        
        Category nameCategory;
        if(name instanceof Name.Function) {
            nameCategory = Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD;
        
        } else if(name instanceof Name.DataCons) {
            nameCategory = Category.DATA_CONSTRUCTOR;
        
        } else if(name instanceof Name.TypeCons) {
            nameCategory = Category.TYPE_CONSTRUCTOR;
        
        } else if(name instanceof Name.TypeClass) {
            nameCategory = Category.TYPE_CLASS;
        
        } else if(name instanceof Name.WithoutContextCons) {
            nameCategory = getCategory(moduleTypeInfo, (Name.WithoutContextCons)name);
        
        } else {
            throw new IllegalStateException("unexpected Name subtype");
        }
        
        if(category != nameCategory) {
            return false;
        }
                
        ModuleName moduleName = org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(name.getModuleName()); // may be null
        if(moduleName != null) {
            ModuleName resolvedModuleName = moduleTypeInfo.getModuleNameResolver().resolve(moduleName).getResolvedModuleName();
            
            return resolvedModuleName.equals(qualifiedModuleName);
        }
        
        // At this point, the categories match, the unqualified names match, and name's moduleName is null
        
        // If this module contains an entity of this name, then that's what unqualified references refer to
        if(getEntityFromModule(moduleTypeInfo, unqualifiedName, nameCategory) != null) {
            return qualifiedModuleName.equals(moduleTypeInfo.getModuleName());
        }
        
        // Otherwise, unqualified references refer to identifiers imported specified in using-clauses
        return qualifiedModuleName.equals(getModuleOfUsingIdentifier(moduleTypeInfo, unqualifiedName, nameCategory));
    }
    
    /**
     * Generates a SourceModification that will replace seeBlock with one or more new see blocks that include
     * explicit context specifiers for any identifiers that conflict with newName.  References to oldName will
     * also be converted to references to newName.
     * 
     * @param moduleTypeInfo ModuleTypeInfo for the module being processed
     * @param sourceText String containing the source code for the module being processed
     * @param seeBlock See block to process
     * @param oldName QualifiedName of the identifier being renamed
     * @param newName QualifiedName that oldName is being renamed to
     * @param category Category of oldName/newName's entity
     * @param qualifyAllRenamedReferences boolean When true, renamed references to oldName will be fully qualified
     *         (eg, "@see oldUnqualified" will become "@see NewModule.newUnqualified").
     * @param collateralDamageModuleRenameMapping a mapping which maps module names that are affected by a module renaming to
     *                                             their corresponding disambiguated names. 
     * @return A SourceModification that replaces seeBlock with one or more new see blocks with the changes described above.
     */
    private static SourceModification makeContextifySeeBlockModification(ModuleTypeInfo moduleTypeInfo, String sourceText, See.WithoutContext seeBlock, QualifiedName oldName, QualifiedName newName, Category category, boolean qualifyAllRenamedReferences, ModuleNameResolver.RenameMapping collateralDamageModuleRenameMapping) {

        StringBuilder sb = new StringBuilder(); 
        
        for(int i = 0, nReferences = seeBlock.getNReferencedNames(); i < nReferences; i++) {
            CanAppearWithoutContext reference = seeBlock.getNthReferencedName(i);
                
            if(reference instanceof CanAppearWithoutContext.Function) {
                CanAppearWithoutContext.Function functionReference = (CanAppearWithoutContext.Function)reference;
                
                Name.Function functionName = functionReference.getName();
                CanAppearWithoutContext newReference = reference; 
                
                if(refersTo(moduleTypeInfo, functionName, oldName, category)) {
                    if(functionName.getModuleName() == null) {
                        newReference = CanAppearWithoutContext.Function.make(Name.Function.makeUnqualified(newName.getUnqualifiedName()), reference.isChecked());
                    } else {
                        newReference = CanAppearWithoutContext.Function.make(Name.Function.make(newName.getModuleName(), newName.getUnqualifiedName()), reference.isChecked());
                    }
                } else {
                    ModuleName oldModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(functionName.getModuleName()); // may be null
                    if (oldModuleName != null && collateralDamageModuleRenameMapping.hasNewName(oldModuleName)) {
                        ModuleName newModuleName = collateralDamageModuleRenameMapping.getNewNameForModule(oldModuleName);
                        newReference = CanAppearWithoutContext.Function.make(Name.Function.make(newModuleName, functionName.getUnqualifiedName()), reference.isChecked());
                    }
                }
                
                See.WithoutContext newSee = See.WithoutContext.make(new CanAppearWithoutContext[] { newReference });
                newSee.toSourceText(sb);
                
            } else if(reference instanceof CanAppearWithoutContext.WithoutContextCons) {
                
                CanAppearWithoutContext.WithoutContextCons consReference = (CanAppearWithoutContext.WithoutContextCons)reference;
                Name.WithoutContextCons name = consReference.getName();
                
                if(willConflictWith(moduleTypeInfo, name, newName, category)) {
                    Category consCategory = getCategory(moduleTypeInfo, name);
                    See newSee;
                    
                    if(consCategory == Category.MODULE_NAME) {
                        newSee = See.Module.make(new CrossReference.Module[] { 
                                CrossReference.Module.make(Name.Module.make(collateralDamageModuleRenameMapping.getNewNameForModule(ModuleName.make(name.toSourceText()))), consReference.isChecked()) 
                        });
                    
                    } else if(consCategory == Category.DATA_CONSTRUCTOR) {
                        newSee = See.DataCons.make(new CrossReference.DataCons[] {
                                CrossReference.DataCons.make(Name.DataCons.make(collateralDamageModuleRenameMapping.getNewNameForModule(org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(name.getModuleName())), name.getUnqualifiedName()), consReference.isChecked())
                        });

                    } else if(consCategory == Category.TYPE_CONSTRUCTOR) {
                        newSee = See.TypeCons.make(new CrossReference.TypeCons[] {
                                CrossReference.TypeCons.make(Name.TypeCons.make(collateralDamageModuleRenameMapping.getNewNameForModule(org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(name.getModuleName())), name.getUnqualifiedName()), consReference.isChecked())
                        });

                    } else if(consCategory == Category.TYPE_CLASS) {
                        newSee = See.TypeClass.make(new CrossReference.TypeClass[] {
                                CrossReference.TypeClass.make(Name.TypeClass.make(collateralDamageModuleRenameMapping.getNewNameForModule(org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(name.getModuleName())), name.getUnqualifiedName()), consReference.isChecked())
                        });
                    
                    } else {
                        throw new IllegalStateException("unexpected category");
                    }
                    
                    newSee.toSourceText(sb);
                    
                } else if(refersTo(moduleTypeInfo, name, oldName, category)) {
                    See newSee;
                    
                    if(category == Category.MODULE_NAME) {
                        newSee = See.Module.make(new CrossReference.Module[] { 
                                CrossReference.Module.make(Name.Module.make(newName.getModuleName()), consReference.isChecked()) 
                        });
                    
                    } else if(category == Category.DATA_CONSTRUCTOR) {
                        CrossReference.DataCons newReference;
                        
                        if(name.getModuleName() != null || qualifyAllRenamedReferences) {
                            newReference = CrossReference.DataCons.make(Name.DataCons.make(newName.getModuleName(), newName.getUnqualifiedName()), consReference.isChecked());
                        } else {
                            newReference = CrossReference.DataCons.make(Name.DataCons.makeUnqualified(newName.getUnqualifiedName()), consReference.isChecked());
                        }
                        
                        newSee = See.DataCons.make(new CrossReference.DataCons[] { newReference });

                    } else if(category == Category.TYPE_CONSTRUCTOR) {
                        CrossReference.TypeCons newReference;
                        
                        if(name.getModuleName() != null || qualifyAllRenamedReferences) {
                            newReference = CrossReference.TypeCons.make(Name.TypeCons.make(newName.getModuleName(), newName.getUnqualifiedName()), consReference.isChecked());
                        } else {
                            newReference = CrossReference.TypeCons.make(Name.TypeCons.makeUnqualified(newName.getUnqualifiedName()), consReference.isChecked());
                        }

                        newSee = See.TypeCons.make(new CrossReference.TypeCons[] { newReference });

                    } else if(category == Category.TYPE_CLASS) {
                        CrossReference.TypeClass newReference;
                        
                        if(name.getModuleName() != null || qualifyAllRenamedReferences) {
                            newReference = CrossReference.TypeClass.make(Name.TypeClass.make(newName.getModuleName(), newName.getUnqualifiedName()), consReference.isChecked());
                        } else {
                            newReference = CrossReference.TypeClass.make(Name.TypeClass.makeUnqualified(newName.getUnqualifiedName()), consReference.isChecked());
                        }

                        newSee = See.TypeClass.make(new CrossReference.TypeClass[] { newReference });
                    
                    } else {
                        throw new IllegalStateException("unexpected category");
                    }
                    
                    newSee.toSourceText(sb);
                    
                } else {
                    
                    // If the constructor name without context can be a module name or a qualified cons name where the
                    // module name is affected by a module renaming and needs to be disambiguated.
                    
                    CanAppearWithoutContext.WithoutContextCons newReference = consReference;
                    
                    if (getCategory(moduleTypeInfo, name) == Category.MODULE_NAME) {
                        // if the constructor name without context is a module name, disambiguate it if required.
                        
                        ModuleName rawModuleName = ModuleName.make(name.toSourceText());
                        
                        if (collateralDamageModuleRenameMapping.hasNewName(rawModuleName)) {
                            ModuleName fixedModuleName = collateralDamageModuleRenameMapping.getNewNameForModule(rawModuleName);
                            String fixedModuleNameAsString = fixedModuleName.toSourceText();
                            
                            newReference = CrossReference.WithoutContextCons.make(Name.WithoutContextCons.make(fixedModuleNameAsString), reference.isChecked());
                        }
                        
                    } else {
                        // the constructor name without context is not a module name,
                        // so if it is a qualified cons name, disambiguate the module name component if required.
                        
                        ModuleName moduleName = org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(name.getModuleName()); // may be null
                        
                        if (moduleName != null) {
                            if (collateralDamageModuleRenameMapping.hasNewName(moduleName)) {
                                ModuleName fixedModuleName = collateralDamageModuleRenameMapping.getNewNameForModule(moduleName);
                                
                                newReference = CrossReference.WithoutContextCons.make(Name.WithoutContextCons.make(fixedModuleName, name.getUnqualifiedName()), reference.isChecked());
                            }
                        }
                    }
                    
                    See.WithoutContext newSee = See.WithoutContext.make(new CanAppearWithoutContext[] { newReference });
                    newSee.toSourceText(sb);
                }
            }
        }

        return makeReplaceText(sourceText, seeBlock.getSourceRange(), sb.toString());
    }
    
    /**
     * @return The name of the module that unqualified name is imported from in a using clause, if any,
     *          or null otherwise.
     * @param moduleTypeInfo ModuleTypeInfo of the module to check for imports
     * @param unqualifiedName
     * @param category
     */
    private static ModuleName getModuleOfUsingIdentifier(ModuleTypeInfo moduleTypeInfo, String unqualifiedName, Category category) {
        
        if(category == Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            return moduleTypeInfo.getModuleOfUsingFunctionOrClassMethod(unqualifiedName);
        
        } else if(category == Category.DATA_CONSTRUCTOR) {
            return moduleTypeInfo.getModuleOfUsingDataConstructor(unqualifiedName);
        
        } else if(category == Category.TYPE_CONSTRUCTOR) {
            return moduleTypeInfo.getModuleOfUsingTypeConstructor(unqualifiedName);
        
        } else if(category == Category.TYPE_CLASS) {
            return moduleTypeInfo.getModuleOfUsingTypeClass(unqualifiedName);
        
        } else if(category == Category.MODULE_NAME) {
            return null;
            
        } else if(category == Category.LOCAL_VARIABLE) {
            return null;
            
        } else if(category == Category.LOCAL_VARIABLE_DEFINITION) {
            return null;
        
        } else {
            throw new IllegalArgumentException("Unrecognized category");
        }
    }
    
    /**
     * @return the entity in moduleTypeInfo's module named unqualifiedName and having
     *          category category, if any exists, or null otherwise.
     */
    private static ScopedEntity getEntityFromModule(ModuleTypeInfo moduleTypeInfo, String unqualifiedName, Category category) {
        
        if(category == Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            return moduleTypeInfo.getFunctionOrClassMethod(unqualifiedName);
        
        } else if(category == Category.DATA_CONSTRUCTOR) {
            return moduleTypeInfo.getDataConstructor(unqualifiedName);
        
        } else if(category == Category.TYPE_CONSTRUCTOR) {
            return moduleTypeInfo.getTypeConstructor(unqualifiedName);
        
        } else if(category == Category.TYPE_CLASS) {
            return moduleTypeInfo.getTypeClass(unqualifiedName);
        
        } else if(category == Category.MODULE_NAME) {
            throw new UnsupportedOperationException();
        
        } else if(category == Category.LOCAL_VARIABLE) {
            throw new UnsupportedOperationException();
        
        } else if(category == Category.LOCAL_VARIABLE_DEFINITION) {
            throw new UnsupportedOperationException();
        
        } else {
            throw new IllegalArgumentException("Unrecognized category");
        }
    }
    
    /**
     * Constructs and returns a SoruceModification that replaces the text at sourceRange of sourceText with newText. 
     * @param sourceText
     * @param sourceRange
     * @param newText
     * @return A SourceModification.ReplaceText
     */
    static SourceModification makeReplaceText(String sourceText, SourceRange sourceRange, String newText) {
        SourcePosition startPosition = sourceRange.getStartSourcePosition();
        SourcePosition endPosition = sourceRange.getEndSourcePosition();
        int startIndex = startPosition.getPosition(sourceText);
        int endIndex = endPosition.getPosition(sourceText, startPosition, startIndex);
        String oldText = sourceText.substring(startIndex, endIndex);
        
        return new SourceModification.ReplaceText(oldText, newText, startPosition);
    }
    
    /**
     * Constructs and returns a SourceModification that will replace the import statement for targetName's module
     * with a new one that does not include targetName in its using clauses.  An exception will be raised if 
     * targetName is not in the using clause of the appropriate import definition. 
     * @param sourceText
     * @param moduleDefn
     * @param targetName
     * @param category
     * @return SourceModification that removes targetName from the appropriate import-using clause
     */
    private static SourceModification makeUsingClauseRemovalModification(String sourceText, SourceModel.ModuleDefn moduleDefn, QualifiedName targetName, Category category) {

        ModuleName targetModule = targetName.getModuleName();
        
        // Remove targetName from the appropriate import-using clause 
        for(int i = 0, nImports = moduleDefn.getNImportedModules(); i < nImports; i++) {
            SourceModel.Import moduleImport = moduleDefn.getNthImportedModule(i);
            if(!org.openquark.cal.compiler.SourceModel.Name.Module.toModuleName(moduleImport.getImportedModuleName()).equals(targetModule)) {
                continue;
            }
            
            SourcePosition startPosition = moduleImport.getSourceRange().getStartSourcePosition();
            SourcePosition endPosition = moduleImport.getSourceRange().getEndSourcePosition();
            int startIndex = startPosition.getPosition(sourceText);
            int endIndex = endPosition.getPosition(sourceText, startPosition, startIndex);
            String oldImportText = sourceText.substring(startIndex, endIndex);
            
            SourceModel.Import newImport = removeUsingClauseEntry(moduleImport, targetName.getUnqualifiedName(), category);
            
            return new SourceModification.ReplaceText(oldImportText, newImport.toSourceText(), startPosition);

        }

        throw new IllegalArgumentException("targetName has no import-using entry in the provided moduleDefn");
    }
    
    /**
     * Constructs and returns a new import statement which does not contain unqualifiedName in the using-clause
     * specified by category.  An exception will be raised if unqualifiedName is not in the expected using-clause.
     * @param importStatement
     * @param unqualifiedName
     * @param category
     * @return SourceModel.Import
     */
    private static SourceModel.Import removeUsingClauseEntry(SourceModel.Import importStatement, String unqualifiedName, Category category) {

        List<UsingItem> newUsingItems = new ArrayList<UsingItem>();
        UsingItem[] oldItems = importStatement.getUsingItems();
        for (final UsingItem oldItem : oldItems) {
            UsingItem newItem = removeUsingItemEntry(oldItem, unqualifiedName, category);
            if(newItem != null) {
                newUsingItems.add(newItem);
            }
        }
        
        return SourceModel.Import.make(importStatement.getImportedModuleName(), newUsingItems.toArray(new UsingItem[0]));
    }

    /**
     * Constructs and returns a new UsingItem based on usingItem which does not contain unqualifiedName if usingItem
     * is of the specified category; if it isn't, usingItem is returned unchanged.
     * @param usingItem UsingItem to mostly duplicate
     * @param unqualifiedName Name to be possibly removed from usingItem
     * @param category
     * @return A new UsingItem that is of the same category as usingItem and which has all the same names
     *          (except unqualifiedName if category is a match).
     */
    private static UsingItem removeUsingItemEntry(UsingItem usingItem, String unqualifiedName, Category category) {
        
        // If this using item doesn't contain the same category of names as the target, then return it unchanged
        if((usingItem instanceof UsingItem.Function && category != Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) ||
           (usingItem instanceof UsingItem.DataConstructor && category != Category.DATA_CONSTRUCTOR) ||
           (usingItem instanceof UsingItem.TypeConstructor && category != Category.TYPE_CONSTRUCTOR) ||
           (usingItem instanceof UsingItem.TypeClass && category != Category.TYPE_CLASS)) {
            
            return usingItem;
        }
        
        List<String> newNames = new ArrayList<String>();
        String[] oldNames = usingItem.getUsingNames();
        for (final String oldName : oldNames) {
            if(oldName.equals(unqualifiedName)) {
                continue;
            }
            newNames.add(oldName);
        }
        
        if(newNames.size() == 0) {
            return null;
        }
        
        if(usingItem instanceof UsingItem.Function) {
            return UsingItem.Function.make(newNames.toArray(new String[0]));
        
        } else if(usingItem instanceof UsingItem.DataConstructor) {
            return UsingItem.DataConstructor.make(newNames.toArray(new String[0]));
        
        } else if(usingItem instanceof UsingItem.TypeConstructor) {
            return UsingItem.TypeConstructor.make(newNames.toArray(new String[0]));
        
        } else if(usingItem instanceof UsingItem.TypeClass) {
            return UsingItem.TypeClass.make(newNames.toArray(new String[0]));

        } else {
            throw new IllegalStateException("unexpected UsingItem subclass");
        }
    }
    
    /**
     * Calculates the category of a Name.WithoutContextCons.  It is assumed to be a valid (ie, unambiguous)
     * reference.
     * @param moduleTypeInfo ModuleTypeInfo of the module that cons appears in
     * @param cons Name.WithoutContextCons to find the category of
     * @return Category of cons
     */
    static private Category getCategory(ModuleTypeInfo moduleTypeInfo, Name.WithoutContextCons cons) {
        
        ModuleName consModuleName = org.openquark.cal.compiler.SourceModel.Name.Module.maybeToModuleName(cons.getModuleName()); // may be null
        String consUnqualifiedName = cons.getUnqualifiedName();
        if(consModuleName != null) {
            QualifiedName qualifiedName = QualifiedName.make(moduleTypeInfo.getModuleNameResolver().resolve(consModuleName).getResolvedModuleName(), consUnqualifiedName);
            if(moduleTypeInfo.getVisibleFunction(qualifiedName) != null || moduleTypeInfo.getVisibleClassMethod(qualifiedName) != null) {
                return Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD;
            
            } else if(moduleTypeInfo.getVisibleDataConstructor(qualifiedName) != null) {
                return Category.DATA_CONSTRUCTOR;
            
            } else if(moduleTypeInfo.getVisibleTypeConstructor(qualifiedName) != null) {
                return Category.TYPE_CONSTRUCTOR;
            
            } else if(moduleTypeInfo.getVisibleTypeClass(qualifiedName) != null) {
                return Category.TYPE_CLASS;
            
            } else {
                return Category.MODULE_NAME;
            }
        }
        
        if(moduleTypeInfo.getDataConstructor(consUnqualifiedName) != null ||
           moduleTypeInfo.getModuleOfUsingDataConstructor(consUnqualifiedName) != null) {
            return Category.DATA_CONSTRUCTOR;
        
        } else if(moduleTypeInfo.getFunctionOrClassMethod(consUnqualifiedName) != null ||
                  moduleTypeInfo.getModuleOfUsingFunctionOrClassMethod(consUnqualifiedName) != null) {
            return Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD;
        
        } else if(moduleTypeInfo.getTypeConstructor(consUnqualifiedName) != null ||
                  moduleTypeInfo.getModuleOfUsingTypeConstructor(consUnqualifiedName) != null) {
            return Category.TYPE_CONSTRUCTOR;

        } else if(moduleTypeInfo.getTypeClass(consUnqualifiedName) != null ||
                  moduleTypeInfo.getModuleOfUsingTypeClass(consUnqualifiedName) != null) {
            return Category.TYPE_CLASS;
        
        } else {
            return Category.MODULE_NAME;
        }
    }   
}