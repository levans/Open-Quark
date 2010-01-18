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
 * TypeDeclarationInserter.java
 * Creation date: (Feb 20, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openquark.cal.compiler.SourceModel.FunctionDefn;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.compiler.SourceModel.TypeSignature;
import org.openquark.cal.compiler.SourceModel.Expr.Let;
import org.openquark.cal.util.ArrayStack;


/**
 * The insert-type-declarations refactoring adds explicit type declarations for top-level 
 * algebraic function declarations and local function declarations that lack them.
 * 
 * @author James Wright
 */
final class TypeDeclarationInserter {

    private TypeDeclarationInserter() {
    }

    /**
     * Helper class for collecting information about a module from its SourceModel.
     * We run the summarizer over a ModuleDefn to collect information on which local
     * functions need type declarations, and at which SourcePositions.
     * 
     * @author James Wright
     */
    private static final class Summarizer extends BindingTrackingSourceModelTraverser<Void> {
        
        /** Set (String) of toplevel algebraic functions that are annotated */ 
        private final Set<String> annotatedAlgebraicFunctions = new HashSet<String>();
        
        /** Map (String -> FunctionDefn.Algebraic) from toplevel function name to the corresponding SourceModel element */
        private final Map<String, FunctionDefn.Algebraic> algebraicFunctions = new HashMap<String, FunctionDefn.Algebraic>();
        
        /**
         * LinkedHashMap (LocalFunctionIdentifier -> LocalDefn) from local function identifier to the corresponding SourceModel element
         * for either a function definition or a pattern match declaration. May be a many-to-one mapping for pattern match declarations
         * with more than one pattern-bound variable.
         * 
         * This is ordered by the original source order of the definitions.
         */
        private final LinkedHashMap<LocalFunctionIdentifier, LocalDefn> localDefns = new LinkedHashMap<LocalFunctionIdentifier, LocalDefn>();
        
        /** 
         * Set (LocalFunctionIdentifier) of the names of local functions/pattern-bound variables whose type declaratiosn
         * would not need a leading newline.  
         * The first local function defined in a let expression is such a definition. 
         */
        private final Set<LocalFunctionIdentifier> typeDeclsNotNeedingLeadingNewline = new HashSet<LocalFunctionIdentifier>();

        /** Set (LocalFunctionIdentifier) of identifiers of local functions that are annotated */
        private final Set<LocalFunctionIdentifier> annotatedLocalFunctions = new HashSet<LocalFunctionIdentifier>();

        /**
         * Handles the adding of bindings for local definitions (both local functions and local pattern match declarations).
         * This is done by walking the local definitions and adding a binding for each local function / pattern-bound variable
         * encountered.
         * 
         * @author Joseph Wong
         */
        private final class LocallyDefinedNamesCollector extends IdentifierResolver.LocalBindingsProcessor<LocalDefn, Void> {
            
            /**
             * Keeps track of whether the inserted type declaration will look good without a leading newline.
             */
            private boolean typeDeclDoesNotNeedLeadingNewline;
            
            /**
             * The LocalFunctionIdentifierGenerator to use for generating identifers for function names/pattern-bound variables that are encountered.
             */
            // @implementation the correctness of this code depends on the fact that the synthetic definition
            // for a pattern match declaration comes in *last*, after all the pattern-bound variables have been desugared into
            // function definitions (because of the nature of the LocalFunctionIdentifierGenerator where each generated identifier
            // contains the value of an internal counter - a pair of identifiers match only if that counter value also match) 
            private final LocalFunctionIdentifierGenerator localFunctionIdentifierGenerator;
            
            /**
             * Constructs an instance of this class.
             * @param typeDeclDoesNotNeedLeadingNewline whether the first type declaration inserted will look good without a leading newline. 
             */
            LocallyDefinedNamesCollector(final boolean typeDeclDoesNotNeedLeadingNewline) {
                this.typeDeclDoesNotNeedLeadingNewline = typeDeclDoesNotNeedLeadingNewline;
                this.localFunctionIdentifierGenerator = getLocalFunctionNameGenerator();
            }
            
            /**
             * Checks whether the type declaration for the given local function needs a leading newline.
             * @param localFunctionIdentifier the identfier for a local function.
             */
            private void checkLeadingPosition(final LocalFunctionIdentifier localFunctionIdentifier) {
                if (typeDeclDoesNotNeedLeadingNewline) {
                    typeDeclsNotNeedingLeadingNewline.add(localFunctionIdentifier);
                }
                // subsequent type decls (for multi-variable patterns) do not need leading newlines...
                typeDeclDoesNotNeedLeadingNewline = true;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            void processLocalDefinitionBinding(final String name, final SourceModel.SourceElement localDefinition, final LocalDefn arg) {
                final LocalFunctionIdentifier localFunctionIdentifier = localFunctionIdentifierGenerator.generateLocalFunctionIdentifier(getModuleName(), name);
                final LocalDefn localDefn = arg;
                localDefns.put(localFunctionIdentifier, localDefn);
                checkLeadingPosition(localFunctionIdentifier);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public Void visit_LocalDefn_Function_Definition(final LocalDefn.Function.Definition function, final LocalDefn arg) {
                return super.visit_LocalDefn_Function_Definition(function, function);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public Void visit_LocalDefn_PatternMatch_UnpackDataCons(final LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, final LocalDefn arg) {
                return super.visit_LocalDefn_PatternMatch_UnpackDataCons(unpackDataCons, unpackDataCons);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Void visit_LocalDefn_PatternMatch_UnpackListCons(final LocalDefn.PatternMatch.UnpackListCons unpackListCons, final LocalDefn arg) {
                return super.visit_LocalDefn_PatternMatch_UnpackListCons(unpackListCons, unpackListCons);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Void visit_LocalDefn_PatternMatch_UnpackRecord(final LocalDefn.PatternMatch.UnpackRecord unpackRecord, final LocalDefn arg) {
                return super.visit_LocalDefn_PatternMatch_UnpackRecord(unpackRecord, unpackRecord);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Void visit_LocalDefn_PatternMatch_UnpackTuple(final LocalDefn.PatternMatch.UnpackTuple unpackTuple, final LocalDefn arg) {
                return super.visit_LocalDefn_PatternMatch_UnpackTuple(unpackTuple, unpackTuple);
            }
        }

        
        /** @return List (FunctionDefn.Algebraic) of toplevel functions that don't have type declarations. */ 
        private List<FunctionDefn.Algebraic> getUnannotatedFunctions() {
            Set<String> unannotatedFunctionNames = new HashSet<String>(algebraicFunctions.keySet());
            unannotatedFunctionNames.removeAll(annotatedAlgebraicFunctions);
            List<FunctionDefn.Algebraic> unannotatedFunctions = new ArrayList<FunctionDefn.Algebraic>();
            
            for (final String functionName : unannotatedFunctionNames) {
                FunctionDefn.Algebraic unannotatedFunctionObj = algebraicFunctions.get(functionName);
                unannotatedFunctions.add(unannotatedFunctionObj);
            }
            
            return unannotatedFunctions;
        }

        /** 
         * @return LinkedHashSet (LocalFunctionIdentifier) of LocalFunctionIdentifiers of local functions that don't have type declarations.
         */
        private LinkedHashSet<LocalFunctionIdentifier> getUnannotatedLocalFunctions() {
            LinkedHashSet<LocalFunctionIdentifier> unannotatedLocalFunctionIdentifiers = new LinkedHashSet<LocalFunctionIdentifier>(localDefns.keySet());
            unannotatedLocalFunctionIdentifiers.removeAll(annotatedLocalFunctions);
            return unannotatedLocalFunctionIdentifiers;
        }
        
        /** 
         * @return LocalDefn for the local function or pattern match declaration named by localFunctionIdentifier.
         * @param localFunctionIdentifier
         */
        private LocalDefn getLocalDefn(final LocalFunctionIdentifier localFunctionIdentifier) {
            return localDefns.get(localFunctionIdentifier);
        }

        /** 
         * @param localFunctionIdentifier
         * @return true if the local function named by localFunctionIdentifier does not need a leading newline
         *          before its type declaration. For example, the first definition in
         *          a let expression (including type declarations) is such a function.  
         *          
         *          For example, in
         *          
         *              let
         *                  foo :: Int;
         *                  foo = 10;
         *              in ...
         *              
         *          foo is /not/ a leading local function (because it is preceded by its type declaration), whereas
         *          in
         *              
         *              let
         *                  foo = 20;
         *                  foo :: Int;
         *              in ...
         *              
         *          foo /is/ a leading local function, because its definition occurs first in the let expression.
         */
        private boolean doesTypeDeclNeedLeadingNewline(LocalFunctionIdentifier localFunctionIdentifier) {
            return typeDeclsNotNeedingLeadingNewline.contains(localFunctionIdentifier);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionDefn_Algebraic(FunctionDefn.Algebraic algebraic, Object arg) {
            algebraicFunctions.put(algebraic.getName(), algebraic);
            return super.visit_FunctionDefn_Algebraic(algebraic, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionTypeDeclaraction(FunctionTypeDeclaration declaration, Object arg) {
            annotatedAlgebraicFunctions.add(declaration.getFunctionName());
            return super.visit_FunctionTypeDeclaraction(declaration, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Let(Let let, Object arg) {
            
            // We record the name of the leading local function definition if and
            // only if it is a function definition (since we don't add anything before
            // function type declarations).
            for (int i = 0, n = let.getNLocalDefinitions(); i < n; i++) {
                final LocallyDefinedNamesCollector collector = new LocallyDefinedNamesCollector(i == 0);
                let.getNthLocalDefinition(i).accept(collector, null);
            }

            return super.visit_Expr_Let(let, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_LocalDefn_Function_Definition(LocalDefn.Function.Definition function, Object arg) {
            Void ret = super.visit_LocalDefn_Function_Definition(function, arg); 
            LocalFunctionIdentifier identifier = getBoundLocalFunctionIdentifier(function.getName());
            localDefns.put(identifier, function);
            return ret;
        }

        @Override
        public Void visit_LocalDefn_Function_TypeDeclaration(LocalDefn.Function.TypeDeclaration declaration, Object arg) {
            LocalFunctionIdentifier identifier = getBoundLocalFunctionIdentifier(declaration.getName());
            annotatedLocalFunctions.add(identifier);
            return super.visit_LocalDefn_Function_TypeDeclaration(declaration, arg);
        }
    }
    
    /**
     * Class that contains statistics about a TypeDeclarationInserter refactoring
     * of a CAL module.
     * 
     * @author James Wright
     */
    static final class RefactoringStatistics {
        
        
        /** 
         * Number of type declarations that included at least one type class constraint 
         * that were added by the refactoring to top-level functions.
         */
        private int topLevelTypeDeclarationsAddedWithClassConstraints;
        
        /** 
         * Number of type declarations that included no type class constraints 
         * that were added by the refactoring to top-level functions.
         */
        private int topLevelTypeDeclarationsAddedWithoutClassConstraints;
        
        /** 
         * Number of type declarations that included at least one type class constraint 
         * that were added by the refactoring to local functions.
         */
        private int localTypeDeclarationsAddedWithClassConstraints;
        
        /** 
         * Number of type declarations that included no type class constraints 
         * that were added by the refactoring to local functions.
         */
        private int localTypeDeclarationsAddedWithoutClassConstraints;
        
        /** 
         * Number of local functions that did not have type declarations where
         * type declarations were not added because their types had no syntactic representation
         * (eg, types that include non-generic type variables).
         */
        private int localTypeDeclarationsNotAdded;
        
        /** Number of import declarations that were added by the refactoring. */
        private int importsAdded;
        
        /**
         * Number of type declarations that are not added due to the fact that the module names appearing
         * therein would require new import statements that may potential introduce module name resolution conflicts.
         */
        private int typeDeclarationsNotAddedDueToPotentialImportConflict;
        
        /**
         * The set of module names that are not added as imports due to the fact that they may introduce module name
         * resolution conflicts.
         */
        private final SortedSet<ModuleName> importsNotAddedDueToPotentialConfict = new TreeSet<ModuleName>();
        
        /**
         * Increment the appropriate counter for top-level function type declarations added
         * depending upon the value of withClassConstraint.
         * @param withClassConstraint True if the type expr being added includes at least one type class constraint
         */
        private void recordAddedToplevelTypeDeclaration(boolean withClassConstraint) {
            if(withClassConstraint) {
                topLevelTypeDeclarationsAddedWithClassConstraints++;
            } else {
                topLevelTypeDeclarationsAddedWithoutClassConstraints++;
            }
        }
        
        /**
         * Increment the appropriate counter for local function type declarations added
         * depending upon the value of withClassConstraint.
         * @param withClassConstraint True if the type expr being added includes at least one type class constraint
         */
        private void recordAddedLocalTypeDeclaration(boolean withClassConstraint) {
            if(withClassConstraint) {
                localTypeDeclarationsAddedWithClassConstraints++;
            } else {
                localTypeDeclarationsAddedWithoutClassConstraints++;
            }
        }
        
        /**
         * Increment the counter for import declarations added
         * @param nModules Number of modules added
         */
        private void recordImportInsertions(int nModules) {
            importsAdded += nModules;
        }
        
        /**
         * Increment the counter for local functions that could not have a type declaration added.
         */
        private void recordLocalTypeDeclarationNotAdded() {
            localTypeDeclarationsNotAdded++;
        }
        
        /**
         * Increment the counter for the number of type declarations that are not added due to the fact that the module names
         * appearing therein would require new import statements that may potential introduce module name resolution conflicts.
         * The module names are also recorded.
         * 
         * @param importsNotAdded
         *            the set of module names that are not added as imports due to the fact that they may introduce
         *            module name resolution conflicts.
         */
        private void recordTypeDeclarationNotAddedDueToPotentialImportConflict(Set<ModuleName> importsNotAdded) {
            typeDeclarationsNotAddedDueToPotentialImportConflict++;
            importsNotAddedDueToPotentialConfict.addAll(importsNotAdded);
        }
        
        /** 
         * @return Number of type declarations that included at least one type class constraint 
         * that were added by the refactoring to top-level functions.
         */
        int getTopLevelTypeDeclarationsAddedWithClassConstraints() {
            return topLevelTypeDeclarationsAddedWithClassConstraints;
        }
        
        /** 
         * @return Number of type declarations that included no type class constraints 
         * that were added by the refactoring to top-level functions.
         */
        int getTopLevelTypeDeclarationsAddedWithoutClassConstraints() {
            return topLevelTypeDeclarationsAddedWithoutClassConstraints;
        }
        
        /** 
         * @return Number of type declarations that included at least one type class constraint 
         * that were added by the refactoring to local functions.
         */
        int getLocalTypeDeclarationsAddedWithClassConstraints() {
            return localTypeDeclarationsAddedWithClassConstraints;
        }
        
        /** 
         * @return Number of type declarations that included no type class constraints 
         * that were added by the refactoring to local functions.
         */
        int getLocalTypeDeclarationsAddedWithoutClassConstraints() {
            return localTypeDeclarationsAddedWithoutClassConstraints;
        }
        
        /** 
         * @return Number of local functions that did not have type declarations where
         * type declarations were not added because their types had no syntactic representation
         * (eg, types that include non-generic type variables).
         */
        int getLocalTypeDeclarationsNotAdded() {
            return localTypeDeclarationsNotAdded;
        }
        
        /**
         * @return Number of import declarations that were added by the refactoring.
         */
        int getImportsAdded() {
            return importsAdded;
        }
        
        /**
         * @return Number of type declarations that are not added due to the fact that the module names appearing
         * therein would require new import statements that may potential introduce module name resolution conflicts.
         */
        int getTypeDeclarationsNotAddedDueToPotentialImportConflict() {
            return typeDeclarationsNotAddedDueToPotentialImportConflict;
        }
        
        /**
         * @return The set of module names that are not added as imports due to the fact that they may introduce module name
         * resolution conflicts.
         */
        SortedSet<ModuleName> getImportsNotAddedDueToPotentialConfict() {
            return Collections.unmodifiableSortedSet(importsNotAddedDueToPotentialConfict);
        }
    }
    
    /**
     * 
     * @param sourceText
     * @param messageLogger
     * @param sourceRange The source range to perform the action over. This maybe null to apply to the whole file.
     * @return A SourceModifier containing the necessary SourceModifications to perform the Insert Type Declarations refactoring
     *          on sourceText.
     */
    static SourceModifier getSourceModifier(ModuleContainer moduleContainer, ModuleName moduleName, SourceRange sourceRange, String sourceText, CompilerMessageLogger messageLogger, RefactoringStatistics refactoringStatistics) {
        SourceModifier sourceModifier = new SourceModifier();
        
        // if the module is a sourceless module, then there is not much we can do with it.
        if (sourceText.length() == 0) {
            return sourceModifier;
        }

        int nErrorsBefore = messageLogger.getNErrors();
        SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(sourceText, messageLogger);
        if(moduleDefn == null || messageLogger.getNErrors() > nErrorsBefore) {
            // We can't proceed if the module is unparseable
            return sourceModifier;
        }
        
        if(!moduleName.equals(SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName()))) {
            throw new IllegalArgumentException("moduleName must correspond to the module name in sourceText");
        }
        
        ModuleTypeInfo moduleTypeInfo = moduleContainer.getModuleTypeInfo(moduleName);
        if(moduleTypeInfo == null) {
            messageLogger.logMessage(new CompilerMessage(new MessageKind.Fatal.ModuleNotInWorkspace(moduleName)));
            return sourceModifier;
        }
         
        Set<ModuleName> unimportedModules = new HashSet<ModuleName>();
        
        Summarizer visitor = new Summarizer();
        visitor.visit_ModuleDefn(moduleDefn, ArrayStack.make());
        List<FunctionDefn.Algebraic> unannotatedFunctions = visitor.getUnannotatedFunctions();
        
        ScopedEntityNamingPolicy namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedIfUsingOrSameModule(moduleTypeInfo); 
        
        // Add SourceModifications for unannotated toplevel functions
        for (final FunctionDefn.Algebraic function : unannotatedFunctions) {
            if (sourceRange != null){
                final SourceRange functionSourceRange = function.getSourceRange();
                if (functionSourceRange != null){
                    if (!sourceRange.overlaps(functionSourceRange)){
                        continue;
                    }
                }
            }
                
            TypeExpr typeExpr = computeTypeExpr(moduleTypeInfo, function, messageLogger); 
            if(typeExpr == null) {
                continue;
            }

            Set<ModuleName> importsThatProduceConflicts = new HashSet<ModuleName>();
            boolean noConflicts = updateWithUnimportedModules(unimportedModules, importsThatProduceConflicts, moduleTypeInfo, typeExpr);

            if (noConflicts) {
                TypeSignature typeSignature = typeExpr.toSourceModel(null, namingPolicy);
                FunctionTypeDeclaration typeDecl = FunctionTypeDeclaration.make(function.getName(), typeSignature);

                SourcePosition insertionPosition = function.getSourceRangeExcludingCaldoc().getStartSourcePosition();

                String insertionText = makeIndentedSourceElementText(typeDecl, insertionPosition, sourceText, false);
                sourceModifier.addSourceModification(new SourceModification.InsertText(insertionText, insertionPosition));

                if(refactoringStatistics != null) {
                    refactoringStatistics.recordAddedToplevelTypeDeclaration(containsClassConstraints(typeExpr));
                }
                
            } else {
                if(refactoringStatistics != null) {
                    refactoringStatistics.recordTypeDeclarationNotAddedDueToPotentialImportConflict(importsThatProduceConflicts);
                }
            }
        }
                
        // Add SourceModifications for unannotated local functions
        LinkedHashSet<LocalFunctionIdentifier> unannotatedLocalFunctions = visitor.getUnannotatedLocalFunctions();
        for (final LocalFunctionIdentifier identifier : unannotatedLocalFunctions) {
            TypeExpr typeExpr = computeLocalTypeExpr(moduleTypeInfo, identifier); 
            if(typeExpr == null) {
                if(refactoringStatistics != null) {
                    refactoringStatistics.recordLocalTypeDeclarationNotAdded();
                }
                continue;
            }
     
            LocalDefn localDefn = visitor.getLocalDefn(identifier);


            if (sourceRange != null){
                final SourceRange functionSourceRange = localDefn.getSourceRange();
                if (functionSourceRange != null){
                    if (!sourceRange.overlaps(functionSourceRange)){
                        continue;
                    }
                }
            }
            
            Set<ModuleName> importsThatProduceConflicts = new HashSet<ModuleName>();
            boolean noConflicts = updateWithUnimportedModules(unimportedModules, importsThatProduceConflicts, moduleTypeInfo, typeExpr);

            if (noConflicts) {
                TypeSignature typeSignature = typeExpr.toSourceModel(null, namingPolicy);
                LocalDefn.Function.TypeDeclaration typeDecl = LocalDefn.Function.TypeDeclaration.make(identifier.getLocalFunctionName(), typeSignature);

                boolean noLeadingNewline = visitor.doesTypeDeclNeedLeadingNewline(identifier);
                final SourcePosition insertionPosition;
                if (localDefn instanceof LocalDefn.Function.Definition) {
                    insertionPosition = ((LocalDefn.Function.Definition)localDefn).getSourceRangeExcludingCaldoc().getStartSourcePosition();
                } else {
                    // in this case the localDefn should be a LocalDefn.PatternMatch
                    insertionPosition = localDefn.getSourceRange().getStartSourcePosition();
                }
                String insertionText = makeIndentedSourceElementText(typeDecl, insertionPosition, sourceText, noLeadingNewline);
                sourceModifier.addSourceModification(new SourceModification.InsertText(insertionText, insertionPosition));

                if(refactoringStatistics != null) {
                    refactoringStatistics.recordAddedLocalTypeDeclaration(containsClassConstraints(typeExpr));
                }
                
            } else {
                if(refactoringStatistics != null) {
                    refactoringStatistics.recordTypeDeclarationNotAddedDueToPotentialImportConflict(importsThatProduceConflicts);
                }
            }
        }

        // Add a SourceModification for importing any unimported modules whose types we want to
        // reference.
        SourceModification importInsertion = computeImportInsertion(unimportedModules, moduleDefn, sourceText);
        if(importInsertion != null) {
            sourceModifier.addSourceModification(importInsertion);
            if(refactoringStatistics != null) {
                refactoringStatistics.recordImportInsertions(unimportedModules.size());
            }
        }
        
        return sourceModifier;
    }

    /**
     * 
     * @param sourceElement
     * @param sourcePosition
     * @param sourceText
     * @param noLeadingNewline
     * @return representation of sourceElement followed by a line indented to the column of sourcePosition
     */
    private static String makeIndentedSourceElementText(SourceElement sourceElement, SourcePosition sourcePosition, String sourceText, boolean noLeadingNewline) {

        int column = sourcePosition.getColumn();
        StringBuilder buffer = new StringBuilder();
        
        if(!noLeadingNewline && !isPriorLineBlank(sourcePosition, sourceText)) {
            buffer.append('\n');
            appendNBlanks(buffer, column - 1);
        }
        
        sourceElement.toSourceText(buffer);
        
        buffer.append('\n');
        appendNBlanks(buffer, column - 1);
        
        return buffer.toString();
    }
    
    /**
     * Add to the unimportedModules set the names of modules that are not currently imported, but which are the 
     * home modules of elements of typeExpr.  eg, if typeExpr is "Prelude.Int -> Color.JColor" but we don't
     * currently import Color, then "Color" will be added to unimportedModules.
     * <p>
     * If a module that needs to be added as an import will cause new ambiguities in resolving partially qualified module names,
     * the module is not added, and false is returned.
     * 
     * @param unimportedModules Set (ModuleName) of module names to add to
     * @param importsThatProduceConflicts Set (ModuleName) of module names that will (potentially) produce conflicts - to add to 
     * @param moduleTypeInfo ModuleTypeInfo of the module to perform the check in the context of.  ie, when we
     *         say above "not currently imported", we mean "not currently imported into the module represented by
     *         moduleTypeInfo".
     * @param typeExpr TypeExpr to check
     * @return false if one or more modules that need to be imported cannot be safely imported due to potential ambiguities; true otherwise.
     */
    private static boolean updateWithUnimportedModules(Set<ModuleName> unimportedModules, Set<ModuleName> importsThatProduceConflicts, ModuleTypeInfo moduleTypeInfo, TypeExpr typeExpr) {

        ArrayStack<TypeExpr> typeExprStack = ArrayStack.make();
        typeExprStack.add(typeExpr);
        
        boolean hasConflictingImports = false;
        
        ModuleName moduleName = moduleTypeInfo.getModuleName();
        while(typeExprStack.size() > 0) {
            TypeExpr currentExpr = typeExprStack.pop();
            
            TypeConsApp rootTypeConsApp = currentExpr.rootTypeConsApp(); 
            if(rootTypeConsApp != null) {
                ModuleName typeConsModuleName = rootTypeConsApp.getName().getModuleName();
                if(moduleName.equals(typeConsModuleName) == false &&
                   moduleTypeInfo.getImportedModule(typeConsModuleName) == null) {
                    
                    if (moduleTypeInfo.getModuleNameResolver().willAdditionalModuleImportProduceConflict(typeConsModuleName)) {
                        
                        importsThatProduceConflicts.add(typeConsModuleName);
                        hasConflictingImports = true;
                    } else {
                        unimportedModules.add(typeConsModuleName);
                    }
                }
            }
            
            addChildTypeExprsToStack(currentExpr, typeExprStack);
        }
        
        return !hasConflictingImports;
    }
    
    /**
     * Check that every type and class referenced in a type expression is visible in the module
     * represented by ModuleTypeInfo. 
     * @param moduleTypeInfo ModuleTypeInfo of module to check in the context of
     * @param typeExpr TypeExpr to check
     * @return true if every type and class referenced in typeExpr is visible in moduleTypeInfo's module,
     *          or false if the typeExpr contains some non-public (and non-protected for friend modules) 
     *          type or class.
     */
    private static boolean isEntireTypeExprVisibleInModule(ModuleTypeInfo moduleTypeInfo, TypeExpr typeExpr) {
        ArrayStack<TypeExpr> typeExprStack = ArrayStack.make();
        typeExprStack.add(typeExpr);
        
        // Check type constructors
        while(typeExprStack.size() > 0) {
            TypeExpr currentExpr = typeExprStack.pop();
            
            TypeConsApp rootTypeConsApp = currentExpr.rootTypeConsApp(); 
            if(rootTypeConsApp != null) {
                if(moduleTypeInfo.getVisibleTypeConstructor(rootTypeConsApp.getName()) == null) {
                    return false;
                }
            }
            
            addChildTypeExprsToStack(currentExpr, typeExprStack);
        }
        
        // Check variable constraints
        for (final PolymorphicVar polymorphicVar : typeExpr.getConstrainedPolymorphicVars()) {
            if(polymorphicVar instanceof TypeVar) {
                TypeVar typeVar = (TypeVar)polymorphicVar;
                for (final TypeClass typeClass : typeVar.getTypeClassConstraintSet()) {
                    if(moduleTypeInfo.getVisibleTypeClass(typeClass.getName()) == null) {
                        return false;
                    }
                }
            
            } else if(polymorphicVar instanceof RecordVar) {
                RecordVar recordVar = (RecordVar)polymorphicVar;
                for (final TypeClass typeClass : recordVar.getTypeClassConstraintSet()) {
                    if(moduleTypeInfo.getVisibleTypeClass(typeClass.getName()) == null) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Compute a single SourceModification to insert import declarations for all the modules of unimportedModules.
     * @param unimportedModules Set (ModuleName) of module names to include in the inserted imports
     * @param moduleDefn ModuleDefn of the module to insert the imports into
     * @param sourceText String source of the module to  insert the imports into
     * @return SourceModification that adds imports for all of the modules in unimportedModules.
     */
    private static SourceModification computeImportInsertion(Set<ModuleName> unimportedModules, SourceModel.ModuleDefn moduleDefn, String sourceText) {

        // If there are no modules that need to be imported, then we don't have anything to calculate
        if(unimportedModules.size() == 0) {
            return null;
        }
        
        // Compute the block of text that we want to insert
        StringBuilder insertionTextBuffer = new StringBuilder();
        for (final ModuleName moduleName : unimportedModules) {
            SourceModel.Import importDecl = SourceModel.Import.make(moduleName);
            importDecl.toSourceText(insertionTextBuffer);
            insertionTextBuffer.append('\n');
        }
        
        // Compute the insertion point
        SourcePosition lastImportEndPosition = new SourcePosition(1, 1);
        for(int i = 0; i < moduleDefn.getNImportedModules(); i++) {
            SourceModel.Import importDecl = moduleDefn.getNthImportedModule(i);
            SourceRange sourceRange = importDecl.getSourceRange(); 
            if(sourceRange == null) {
                continue;
            }

            SourcePosition endPosition = sourceRange.getEndSourcePosition();
            if(SourcePosition.compareByPosition.compare(endPosition, lastImportEndPosition) > 0) {
                lastImportEndPosition = endPosition;
            }
        }
        
        // We want to insert at the beginning of the line following the final existing import.
        int lastImportEndLine = lastImportEndPosition.getLine();
        SourcePosition insertionPosition = new SourcePosition(lastImportEndLine + 1, 1);
        String insertionText = insertionTextBuffer.toString();
        
        // There is always the "pathological case with the whole module on one line" to consider, so
        // verify that insertionPosition actually exists.  If it doesn't we'll fail over to starting
        // from the very end of the final import, starting with a newline.  This is less ideal, because
        // end-of-line comments associated with the final import will get moved, which is why we don't
        // do it for non-pathological cases.
        int newlineCount = 0;
        int newlineIndex = sourceText.indexOf('\n');
        while(newlineIndex != -1 && newlineCount < lastImportEndLine) {
            newlineCount++;
            newlineIndex = sourceText.indexOf('\n', newlineIndex + 1);
        }
        
        if(newlineCount < lastImportEndLine) {
            insertionPosition = lastImportEndPosition;
            insertionText = '\n' + insertionText;
        }
        
        return new SourceModification.InsertText(insertionText, insertionPosition);
    }
    
    /**
     * Adds the components of typeExpr to stack.
     * @param typeExpr
     * @param stack
     */
    private static void addChildTypeExprsToStack(TypeExpr typeExpr, ArrayStack<TypeExpr> stack) {
        if (typeExpr instanceof RecordType) {
            RecordType recordType = (RecordType)typeExpr;
            for (final TypeExpr fieldTypeExpr : recordType.getHasFieldsMap().values()) {
                stack.add(fieldTypeExpr);
            }
        
        } else if (typeExpr instanceof TypeConsApp) {
            TypeConsApp typeConsApp = (TypeConsApp)typeExpr;
            for(int i = 0; i < typeConsApp.getNArgs(); i++) {
                stack.add(typeConsApp.getArg(i));
            }
       
        } else if (typeExpr instanceof TypeApp) {
            TypeApp typeApp = (TypeApp)typeExpr;
            stack.add(typeApp.getOperatorType());
            stack.add(typeApp.getOperandType());            
        } else if (typeExpr instanceof TypeVar) {
            return;
                   
        } else {
            throw new IllegalStateException("unhandled TypeExpr subtype");
        }
    }
    
    /**
     * @param sourcePosition position of the line just after the line to check
     * @param sourceText String that sourcePosition points into
     * @return true if the previous line is "effectively" blank (ie, it might contain a comment)
     */
    private static boolean isPriorLineBlank(SourcePosition sourcePosition, String sourceText) {
        int line = sourcePosition.getLine();
        
        // If there's no previous line, it can't be blank
        if(line <= 1) {
            return false;
        }
        
        SourcePosition currentLineStartPosition = new SourcePosition(line, 1);
        SourcePosition priorLineStartPosition = new SourcePosition(line - 1, 1);
        int priorLineStartIndex = priorLineStartPosition.getPosition(sourceText);
        int currentLineStartIndex = currentLineStartPosition.getPosition(sourceText, priorLineStartPosition, priorLineStartIndex);
        String priorLine = sourceText.substring(priorLineStartIndex, currentLineStartIndex);
        
        // The heuristic: If the line starts with a single-line comment or ends with a multi-line close-comment
        // delimiter, then it is effectively blank.
        return priorLine.matches("(\\s*//.*\\s*)|(.*\\*/\\s*)|(\\s*)");
    }
    
    /**
     * Append numBlanks blanks to buffer
     * @param buffer StringBuilder
     * @param numBlanks int
     */
    private static void appendNBlanks(StringBuilder buffer, int numBlanks) {
        for(int i = 0; i < numBlanks; i++) {
            buffer.append(' ');
        }
    }
    
    /**
     * Returns the type of the local function specified by localFunctionIdentifier if it is possible to insert
     * an explicit declaration of that type, or null if it isn't (due to eg. non-generic variable issues). 
     * @param moduleTypeInfo
     * @param localFunctionIdentifier
     * @return TypeExpr for use in an explicit type declaration if possible, or null otherwise
     */
    private static TypeExpr computeLocalTypeExpr(ModuleTypeInfo moduleTypeInfo, LocalFunctionIdentifier localFunctionIdentifier) {
        
        Function toplevelFunction = moduleTypeInfo.getFunction(localFunctionIdentifier.getToplevelFunctionName().getUnqualifiedName());
        if (toplevelFunction == null){
            return null;
        }
        Function localFunction = toplevelFunction.getLocalFunction(localFunctionIdentifier);
        if(localFunction == null) {
            return null;
        }
        
        // If the local function's type contains uninstantiated nongeneric variables,
        // then it's of no use to us.
        if(localFunction.typeContainsUninstantiatedNonGenerics()) {
            return null;
        }
        
        TypeExpr typeExpr = localFunction.getTypeExpr();
        if(typeExpr == null) {
            return null;
        }
        
        // A type declaration won't compile unless every part of the type expression is visible in the current module. 
        if(!isEntireTypeExprVisibleInModule(moduleTypeInfo, typeExpr)) {
            return null;
        }
        
        return typeExpr;
    }
    
    /**
     * Returns the type of the toplevel function specified by function if it is possible to insert
     * an explicit declaration of that type, or null if it isn't (due to eg. type scoping issues). 
     * @param moduleTypeInfo ModuleTypeInfo for the function's module
     * @param function SourceModel of the function
     * @param messageLogger Logger to log failures to
     * @return TypeExpr for the specified function (may be null)
     */
    private static TypeExpr computeTypeExpr(ModuleTypeInfo moduleTypeInfo, FunctionDefn.Algebraic function, CompilerMessageLogger messageLogger) {
        String functionName = function.getName();
        Function functionEntity = moduleTypeInfo.getFunction(functionName);
        if(functionEntity == null) {
            return null;
        }

        TypeExpr functionTypeExpr = functionEntity.getTypeExpr();
        
        // A type declaration won't compile unless every part of the type expression is visible in the current module. 
        if(!isEntireTypeExprVisibleInModule(moduleTypeInfo, functionTypeExpr)) {
            return null;
        }
        
        return functionTypeExpr;
    }
    
    /** 
     * @param typeExpr A type expression to check
     * @return true if typeExpr contains any type or record variables with class
     *          constraints, or false otherwise.
     */
    private static boolean containsClassConstraints(TypeExpr typeExpr) {
        Set<PolymorphicVar> polymorphicVars = typeExpr.getConstrainedPolymorphicVars();
        for (final PolymorphicVar var : polymorphicVars) {
            if(var instanceof RecordVar) {
                RecordVar recordVar = (RecordVar)var;
                if(recordVar.noClassConstraints() == false) {
                    return true;
                }
            
            } else if(var instanceof TypeVar) {
                TypeVar typeVar = (TypeVar)var;
                if(typeVar.noClassConstraints() == false) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
