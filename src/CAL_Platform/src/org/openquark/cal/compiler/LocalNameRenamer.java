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
 * LocalNameRenamer.java
 * Created: Sep 24, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.IdentifierOccurrence.Binding;
import org.openquark.cal.compiler.IdentifierOccurrence.Reference;
import org.openquark.cal.compiler.IdentifierOccurrenceFinder.FinderState;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable;
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.LocalScope;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;

/**
 * This class encapsulates the renaming logic for handling locally-bound names
 * (let-bound functions and local pattern match variables, case-bound patterns, lambda-bound parameters,
 * and parameters of top-level and local functions).
 * <p>
 * The renaming logic works in two passes:
 * <ol>
 * <li>
 *  The first pass gathers the following information:
 *  <ul>
 *  <li>which algebraic function contains the local variable
 *  <li>what are the other names defined locally within that algebraic function
 *  <li>among the local definitions that are <i>shadowed</i> by the renamed definition, which ones
 *      must be renamed in order to avoid a change in semantics.
 *      <p>
 *      For example:
 *      <pre>
 *      let
 *          foo = 3.0;
 *      in
 *          let
 *              bar = 4.0;
 *          in
 *              foo + bar;
 *      </pre>
 *      In the above, if bar is renamed to foo, the existing foo reference must be renamed, because
 *      the reference to foo in 'foo + bar' is in the scope of the bar definition to be renamed.
 *      On the other hand, given:
 *      <pre>
 *      let
 *          foo = 3.0;
 *          baz =
 *              let
 *                  bar = 4.0;
 *              in
 *                  bar;
 *      in
 *          foo + baz;
 *      </pre>
 *      If bar is renamed to foo here, the original foo does not need to be renamed, because there
 *      are no references to it within the scope defining bar.
 *  </ul>
 * <li>
 *  The second pass takes the information from the first pass, and calculates:
 *  <ul>
 *  <li>which local definitions <i>shadow</i> the renamed definition. These are to be renamed so as to
 *      not conflict with the renamed definition.
 *      <p>
 *      For example:
 *      <pre>
 *      let
 *          foo = 3.0;
 *      in
 *          let
 *              bar = 4.0;
 *          in
 *              foo + bar;
 *      </pre>
 *      If foo is renamed to bar in the above, then bar needs to be renamed so as to preserve semantics.
 *  <li>where are all the occurrences of the variable to be renamed
 *  <li>which unqualified names refer to top-level functions or class methods and would be shadowed
 *      by the renamed definition - these will be qualified to avoid conflicts
 *  <li>which occurrences correspond to the affected shadowed definitions and the shadowing definitions
 *      - these will need to be renamed to something else to avoid conflicts (these are termed "collateral damage")
 *  </ul>
 * </ol>
 * The renaming algorithm takes the information gathered by the two passes to produce a set of
 * required source modifications.
 *
 * @author Joseph Wong
 */
/*
 * @history
 * 
 * The local name renaming logic is based on the work in CALSourceGenerator.ParameterRenamer,
 * which handles the renaming of lambda parameters.
 */
final class LocalNameRenamer {
    
    /**
     * The first pass gathers the following information:
     * <ul>
     * <li>which algebraic function contains the local variable
     * <li>what are the other names defined locally within that algebraic function
     * <li>among the local definitions that are <i>shadowed</i> by the renamed definition, which
     * ones must be renamed in order to avoid a change in semantics.
     * </ul>
     * 
     * @author Joseph Wong
     */
    private static final class FirstPass extends IdentifierOccurrenceFinder<Void> {
        
        /**
         * The target to be renamed.
         */
        private final IdentifierInfo.Local target;
        
        /**
         * The new name for the target.
         */
        private final String newName;
        
        /**
         * The current algebraic function - it is non-null only if an algebraic function is being visited.
         */
        private FunctionDefn.Algebraic currentAlgebraicFunction;
        
        /**
         * The set of local names defined in the current algebraic function.
         */
        private final Set<String> localNamesInCurrentAlgebraicFunction = new HashSet<String>();
        
        /**
         * The algebraic function containing the target - this is null until the target is found.
         */
        private FunctionDefn.Algebraic algebraicFunctionContainingTarget;
        
        /**
         * The set of local names defined in the algebraic function containing the target - this is empty until
         * the target is found.
         */
        private final Set<String> localNamesInAlgebraicFunctionContainingTarget = new HashSet<String>();
        
        /**
         * A map of all local definitions that are shadowed by the new name for the target.
         */
        private final Map<IdentifierInfo.Local, Binding<IdentifierInfo.Local>> shadowedLocalDefinitions =
            new HashMap<IdentifierInfo.Local, Binding<IdentifierInfo.Local>>();
        
        /**
         * A submap of {@link #shadowedLocalDefinitions} containing only those shadowed local definitions
         * that <i>must</i> be renamed to avoid conflicts.
         */
        private final Map<IdentifierInfo.Local, Binding<IdentifierInfo.Local>> affectedShadowedLocalDefinitions =
            new HashMap<IdentifierInfo.Local, Binding<IdentifierInfo.Local>>();
        
        /**
         * The scope that defines the target - this is null until the target is found.
         */
        private LocalScope definingScopeOfTarget;

        /**
         * Construct an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param target the target to be renamed.
         * @param newName the new name for the target.
         */
        FirstPass(final ModuleName currentModuleName, final IdentifierInfo.Local target, final String newName) {
            super(currentModuleName);
            if (target == null || newName == null) {
                throw new NullPointerException();
            }
            this.target = target;
            this.newName = newName;
        }

        /**
         * @return the algebraic function containing the target - this is null until the target is found.
         */
        FunctionDefn.Algebraic getAlgebraicFunctionContainingTarget() {
            return algebraicFunctionContainingTarget;
        }

        /**
         * @return the set of local names defined in the algebraic function containing the target - this is null until the target is found.
         */
        Set<String> getLocalNamesInAlgebraicFunctionContainingTarget() {
            return localNamesInAlgebraicFunctionContainingTarget;
        }

        /**
         * @return a map containing only those shadowed local definitions that must be renamed to avoid conflicts. 
         */
        Map<IdentifierInfo.Local, Binding<IdentifierInfo.Local>> getAffectedShadowedLocalDefinitions() {
            return affectedShadowedLocalDefinitions;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_FunctionDefn_Algebraic(final FunctionDefn.Algebraic algebraic, final VisitorArgument<FinderState> arg) {
            // optimization: if already found the function containing the target, just return
            if (algebraicFunctionContainingTarget != null) {
                return null;
            }
            
            currentAlgebraicFunction = algebraic;
            localNamesInCurrentAlgebraicFunction.clear();
            
            super.visit_FunctionDefn_Algebraic(algebraic, arg);
            
            if (algebraicFunctionContainingTarget == algebraic) {
                // we have found the right function, so copy so the local names
                localNamesInAlgebraicFunctionContainingTarget.addAll(localNamesInCurrentAlgebraicFunction);
            }
            
            // reset the state
            currentAlgebraicFunction = null;
            localNamesInCurrentAlgebraicFunction.clear();
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleNewLocalScope(final LocalScope scope) {
            
            // first loop: set the definingScopeOfTarget if the target is indeed defined in this scope
            for (final Binding<IdentifierInfo.Local> binding : scope.getBindings()) {
                if (target.equals(binding.getIdentifierInfo())) {
                    algebraicFunctionContainingTarget = currentAlgebraicFunction;
                    definingScopeOfTarget = scope;
                }
            }
            
            // second loop - done by superclass implementation:
            super.handleNewLocalScope(scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleLocalVariableBinding(final Binding<IdentifierInfo.Local> binding, final LocalScope scope) {

            if (target.equals(binding.getIdentifierInfo())) {
                // find all the local definitions that are shadowed
                SymbolTable scopeToCheck = scope;
                while (scopeToCheck instanceof LocalScope) {
                    final LocalScope localAncestorScope = (LocalScope)scopeToCheck;
                    for (final Binding<IdentifierInfo.Local> bindingInAncestor : localAncestorScope.getBindings()) {
                        if (newName.equals(bindingInAncestor.getIdentifierInfo().getVarName())) {
                            shadowedLocalDefinitions.put(bindingInAncestor.getIdentifierInfo(), bindingInAncestor);
                        }
                    }
                    
                    if (scopeToCheck instanceof LocalScope) {
                        scopeToCheck = ((LocalScope)scopeToCheck).getParent();
                    } else {
                        break;
                    }
                }
            }
            
            localNamesInCurrentAlgebraicFunction.add(binding.getIdentifierInfo().getVarName());
            
            super.handleLocalVariableBinding(binding, scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleVarNameReference(final Reference<? extends IdentifierInfo> reference, final Binding<? extends IdentifierInfo> binding, final SymbolTable scope) {

            if (scope instanceof LocalScope) {
                if (isSameOrDescendantScope((LocalScope)scope, definingScopeOfTarget)) {
                    if (binding.getIdentifierInfo() instanceof IdentifierInfo.Local) {
                        // If the reference appears in the scope of the target, and the definition is shadowed,
                        // then the definition is affected.
                        
                        final Binding<IdentifierInfo.Local> shadowedLocalDefinition = shadowedLocalDefinitions.get(binding.getIdentifierInfo());
                        if (shadowedLocalDefinition != null) {
                            affectedShadowedLocalDefinitions.put(shadowedLocalDefinition.getIdentifierInfo(), shadowedLocalDefinition);
                        }
                    }
                }
            }
            
            super.handleVarNameReference(reference, binding, scope);
        }
    }
    
    /**
     * The second pass takes the information from the first pass, and calculates:
     * <ul>
     * <li>which local definitions <i>shadows</i> the renamed definition. These are to be renamed
     * so as to not conflict with the renamed definition.
     * <li>where are all the occurrences of the variable to be renamed
     * <li>which unqualified names refer to top-level functions or class methods and would be
     * shadowed by the renamed definition - these will be qualified to avoid conflicts
     * <li>which occurrences correspond to the affected shadowed definitions and the shadowing
     * definitions - these will need to be renamed to something else to avoid conflicts (these are
     * termed "collateral damage")
     * </ul>
     * 
     * @author Joseph Wong
     */
    private static final class SecondPass extends IdentifierOccurrenceFinder<Void> {
        
        /**
         * The target to be renamed.
         */
        private final IdentifierInfo.Local target;
        
        /**
         * The new name for the target.
         */
        private final String newName;
        
        /**
         * A map containing only those shadowed local definitions that <i>must</i> be renamed to avoid conflicts.
         * Can *not* be null.
         */
        private final Map<IdentifierInfo.Local, Binding<IdentifierInfo.Local>> affectedShadowedLocalDefinitions;
        
        /**
         * The scope that defines the target. Can *not* be null.
         */
        private LocalScope definingScopeOfTarget;
        
        /**
         * A set for collecting the local definitions that would shadow the renamed target.
         */
        private final Set<IdentifierInfo.Local> shadowingLocalDefinitions = new HashSet<IdentifierInfo.Local>();

        /**
         * The algebraic function containing the target. Can *not* be null.
         */
        private final FunctionDefn.Algebraic algebraicFunctionContainingTarget;
        
        /**
         * A list of the occurrences of the target to be renamed.
         */
        private final List<IdentifierOccurrence<?>> occurrencesToRename = new ArrayList<IdentifierOccurrence<?>>();
        
        /**
         * A list of the unqualified name occurrences referring to top-level functions or class
         * methods that would be shadowed by the renamed target.
         */
        private final List<Reference.Qualifiable<?>> occurrencesToQualify = new ArrayList<Reference.Qualifiable<?>>();
        
        /**
         * A list of the occurrences corresponding to the affected shadowed definitions and the
         * shadowing definitions - these will need to be renamed to something else to avoid
         * conflicts (these are termed "collateral damage").
         */
        private final List<IdentifierOccurrence<?>> collaterallyDamagedLocalNameOccurrences = new ArrayList<IdentifierOccurrence<?>>();

        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param target the target to be renamed.
         * @param newName the new name for the target.
         * @param algebraicFunctionContainingTarget the algebraic function containing the target. Can *not* be null.
         * @param affectedShadowedLocalDefinitions a map containing only those shadowed local definitions that must be renamed to avoid conflicts. Can *not* be null.
         */
        SecondPass(final ModuleName currentModuleName, final IdentifierInfo.Local target, final String newName, final FunctionDefn.Algebraic algebraicFunctionContainingTarget, final Map<IdentifierInfo.Local, Binding<IdentifierInfo.Local>> affectedShadowedLocalDefinitions) {
            super(currentModuleName);
            if (target == null || newName == null || algebraicFunctionContainingTarget == null || affectedShadowedLocalDefinitions == null) {
                throw new NullPointerException();
            }
            this.target = target;
            this.newName = newName;
            this.algebraicFunctionContainingTarget = algebraicFunctionContainingTarget;
            this.affectedShadowedLocalDefinitions = affectedShadowedLocalDefinitions;
        }
        
        /**
         * @return a list of the occurrences of the target to be renamed.
         */
        List<IdentifierOccurrence<?>> getOccurrencesToRename() {
            return occurrencesToRename;
        }

        /**
         * @return a list of the unqualified name occurrences referring to top-level functions or
         *         class methods that would be shadowed by the renamed target.
         */
        List<Reference.Qualifiable<?>> getOccurrencesToQualify() {
            return occurrencesToQualify;
        }

        /**
         * @return a list of the occurrences corresponding to the affected shadowed definitions and
         *         the shadowing definitions - these will need to be renamed to something else to
         *         avoid conflicts (these are termed "collateral damage").
         */
        List<IdentifierOccurrence<?>> getCollaterallyDamagedLocalNameOccurrences() {
            return collaterallyDamagedLocalNameOccurrences;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_FunctionDefn_Algebraic(final FunctionDefn.Algebraic algebraic, final VisitorArgument<FinderState> arg) {
            // optimization: skip everything except the function containing the target
            if (algebraic != algebraicFunctionContainingTarget) {
                return null;
            }
            return super.visit_FunctionDefn_Algebraic(algebraic, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleNewLocalScope(final LocalScope scope) {
            
            // first loop: set the definingScopeOfTarget if the target is indeed defined in this scope
            for (final Binding<IdentifierInfo.Local> binding : scope.getBindings()) {
                if (target.equals(binding.getIdentifierInfo())) {
                    definingScopeOfTarget = scope;
                }
            }

            // second loop - done by superclass implementation:
            // determining if the binding corresponds to the one needing to be renamed
            // or to one of the "collaterally damaged" names that need to be renamed to something else
            super.handleNewLocalScope(scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleLocalVariableBinding(final Binding<IdentifierInfo.Local> binding, final LocalScope scope) {
            if (target.equals(binding.getIdentifierInfo())) {
                occurrencesToRename.add(binding);

            } else if (affectedShadowedLocalDefinitions.containsKey(binding.getIdentifierInfo())) {
                collaterallyDamagedLocalNameOccurrences.add(binding);
                
            } else if (isSameOrDescendantScope(scope, definingScopeOfTarget) && newName.equals(binding.getIdentifierInfo().getVarName())) {
                // check to see if this definition shadows the target
                shadowingLocalDefinitions.add(binding.getIdentifierInfo());
                collaterallyDamagedLocalNameOccurrences.add(binding);
            }
            
            super.handleLocalVariableBinding(binding, scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleVarNameReference(final Reference<? extends IdentifierInfo> reference, final Binding<? extends IdentifierInfo> binding, final SymbolTable scope) {

            if (target.equals(reference.getIdentifierInfo())) {
                occurrencesToRename.add(reference);
                
            } else if (shadowingLocalDefinitions.contains(reference.getIdentifierInfo())) {
                collaterallyDamagedLocalNameOccurrences.add(reference);
                
            } else if (affectedShadowedLocalDefinitions.containsKey(reference.getIdentifierInfo())) {
                collaterallyDamagedLocalNameOccurrences.add(reference);
                
            } else if (reference instanceof Reference.Qualifiable<?>) {
                final Reference.Qualifiable<?> qualifiableReference = (Reference.Qualifiable<?>)reference;
                if (qualifiableReference.getModuleNameOccurrence() == null) {
                    if (newName.equals(qualifiableReference.getIdentifierInfo().getResolvedName().getUnqualifiedName())) {
                        // unqualified occurrence to a top-level entity with the same name as the new name, so need
                        // to qualify it
                        occurrencesToQualify.add(qualifiableReference);
                    }
                }
            }
            
            super.handleVarNameReference(reference, binding, scope);
        }
    }

    /** Private constructor. */
    private LocalNameRenamer() {}
    
    /**
     * Helper method for determining whether a scope is the same, or is a descendant of, another scope.
     * @param potentialDescendantScope the potential descendant scope.
     * @param potentialAncestorScope the potential ancestor scope.
     * @return true if the first scope is the same, or a descendant of, the second scope.
     */
    private static boolean isSameOrDescendantScope(final LocalScope potentialDescendantScope, final LocalScope potentialAncestorScope) {
        if (potentialAncestorScope == null) {
            return false;
        }
        
        if (potentialDescendantScope == potentialAncestorScope) {
            return true;
        }
        
        SymbolTable parent = potentialDescendantScope.getParent();
        while (parent instanceof LocalScope) {
            if (parent == potentialAncestorScope) {
                return true;
            }
            
            if (parent instanceof LocalScope) {
                parent = ((LocalScope)parent).getParent();
            } else {
                break;
            }
        }
        
        return false;
    }
    
    /**
     * Runs the renaming logic for a local variable and returns the necessary modifications in
     * a {@link SourceModifier}.
     * 
     * @param moduleTypeInfo ModuleTypeInfo for the module to process 
     * @param oldName the identifier being renamed
     * @param newName the name name
     * @param messageLogger CompilerMessageLogger for logging failures
     * @return a SourceModifier that will apply the renaming to the source. 
     */
    static SourceModifier getSourceModifier(final ModuleTypeInfo moduleTypeInfo, final String sourceText, final IdentifierInfo.Local oldName, final String newName, final CompilerMessageLogger messageLogger) {

        final SourceModifier sourceModifier = new SourceModifier();
        
        // if the module is a sourceless module, then there is not much we can do with it.
        if (sourceText.length() == 0) {
            return sourceModifier;
        }

        final ModuleName moduleName = moduleTypeInfo.getModuleName();        

        final SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(sourceText, messageLogger);
        
        if (moduleDefn == null) {
            return sourceModifier;
        }
        
        final FirstPass firstPass = new FirstPass(moduleName, oldName, newName);
        moduleDefn.accept(firstPass, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(moduleTypeInfo)), FinderState.make()));
        
        final Set<String> localNamesInAlgebraicFunctionContainingTarget = firstPass.getLocalNamesInAlgebraicFunctionContainingTarget();
        
        final SecondPass secondPass = new SecondPass(moduleName, oldName, newName, firstPass.getAlgebraicFunctionContainingTarget(), firstPass.getAffectedShadowedLocalDefinitions());
        moduleDefn.accept(secondPass, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(moduleTypeInfo)), FinderState.make()));

        for (final IdentifierOccurrence<?> occurrenceToRename : secondPass.getOccurrencesToRename()) {
            addLocalNameRenameModification(sourceModifier, sourceText, occurrenceToRename, newName);
        }
        
        for (final Reference.Qualifiable<?> occurrenceToQualify : secondPass.getOccurrencesToQualify()) {
            final QualifiedName fullyQualifiedName = occurrenceToQualify.getIdentifierInfo().getResolvedName();
            final ModuleName moduleNameOfReference = fullyQualifiedName.getModuleName();
            final ModuleName minimallyQualifiedModuleNameOfReference = moduleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(moduleNameOfReference);
            final QualifiedName qualifiedName = QualifiedName.make(minimallyQualifiedModuleNameOfReference, fullyQualifiedName.getUnqualifiedName());
            
            sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToQualify.getSourceRange(), qualifiedName.toSourceText()));
        }
        
        if (!secondPass.getCollaterallyDamagedLocalNameOccurrences().isEmpty()) {
            final String newNameForCollateralDamage = getNewNameForCollateralDamage(newName, localNamesInAlgebraicFunctionContainingTarget);
            for (final IdentifierOccurrence<?> collateralDamage : secondPass.getCollaterallyDamagedLocalNameOccurrences()) {
                addLocalNameRenameModification(sourceModifier, sourceText, collateralDamage, newNameForCollateralDamage);
            }
        }

        return sourceModifier;
    }

    /**
     * Produces a non-conflicting name for definitions deemed "collateral damage".
     * @param newName the new name of the target.
     * @param localNamesInAlgebraicFunctionContainingTarget the other names in the algebraic function containing the target <i>to be avoided</i>.
     * @return a non-conflicting name.
     */
    private static String getNewNameForCollateralDamage(final String newName, final Set<String> localNamesInAlgebraicFunctionContainingTarget) {
        String newNameForCollateralDamage;
        int disambiguator = 1; 
        do {
            newNameForCollateralDamage = newName + "_" + disambiguator;
            disambiguator++;
        } while (localNamesInAlgebraicFunctionContainingTarget.contains(newNameForCollateralDamage));
        return newNameForCollateralDamage;
    }

    /**
     * Adds a source modification corresponding to a local name renaming.
     * @param sourceModifier the source modifier to add to.
     * @param sourceText the source text.
     * @param occurrenceToRename the occurrence to rename.
     * @param newName the new name.
     */
    private static void addLocalNameRenameModification(final SourceModifier sourceModifier, final String sourceText, final IdentifierOccurrence<?> occurrenceToRename, final String newName) {
        if (occurrenceToRename instanceof Binding.PunnedTextualDataConsFieldName<?>) {
            final Binding.PunnedTextualDataConsFieldName<?> punnedBinding = (Binding.PunnedTextualDataConsFieldName<?>)occurrenceToRename;
            // need to unpun the pattern
            sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToRename.getSourceRange(), punnedBinding.getIdentifierInfo().getVarName() + " = " + newName));
            
        } else if (occurrenceToRename instanceof Binding.PunnedTextualRecordFieldName<?>) {
            final Binding.PunnedTextualRecordFieldName<?> punnedBinding = (Binding.PunnedTextualRecordFieldName<?>)occurrenceToRename;
            // need to unpun the pattern
            sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToRename.getSourceRange(), punnedBinding.getIdentifierInfo().getVarName() + " = " + newName));
            
        } else {
            sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToRename.getSourceRange(), newName));
        }
    }
}
