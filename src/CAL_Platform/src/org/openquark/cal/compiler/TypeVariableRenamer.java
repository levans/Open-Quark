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
 * TypeVariableRenamer.java
 * Created: Sep 26, 2007
 * By: Joseph
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
import org.openquark.cal.compiler.IdentifierResolver.TypeVariableScope;
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;

/**
 * This class encapsulates the renaming logic for handling type variables.
 * <p>
 * The renaming logic works in two passes:
 * <ol>
 * <li>
 *  The first pass gathers the following information:
 *  <ul>
 *  <li>which "top level" type variable scope contains the type variable (we will call this the
 *      "type container")
 *      <p>
 *      To be precise, a type container can be:
 *      <ul>
 *      <li>an algebraic type definition
 *      <li>a type class definition
 *      <li>an instance definition
 *      <li>a type signature not in the scope of the above
 *      <li>(a data constructor definition, but only if that is the only fragment being visited)
 *      </ul>
 *  <li>what are the other names defined locally within that type container
 *  <li>what are the type variables that are <i>shadowed</i> by the renamed definition.
 *      <p>
 *      For example:
 *      <pre>
 *      class C x where
 *          m1 :: y -> x -> y;
 *          ;
 *      </pre>
 *      In the above, if y is renamed to x, the existing x reference must be renamed to avoid a conflict.
 *  </ul>
 * <li>
 *  The second pass takes the information from the first pass, and calculates:
 *  <ul>
 *  <li>which type variables <i>shadow</i> the renamed definition. These are to be renamed so as to
 *      not conflict with the renamed definition.
 *      <p>
 *      For example:
 *      <pre>
 *      class C x where
 *          m1 :: y -> x -> y;
 *          m2 :: x -> y;
 *          ;
 *      </pre>
 *      If x is renamed to y in the above, then the y in both m1 and m2's definitions need to be
 *      renamed so as to preserve semantics.
 *  <li>where are all the occurrences of the variable to be renamed
 *  <li>which occurrences correspond to the shadowed definitions and the shadowing definitions
 *      - these will need to be renamed to something else to avoid conflicts (these are termed "collateral damage")
 *  </ul>
 * </ol>
 * The renaming algorithm takes the information gathered by the two passes to produce a set of
 * required source modifications.
 *
 * @author Joseph Wong
 */
final class TypeVariableRenamer {
    
    /**
     * The first pass gathers the following information:
     * <ul>
     * <li>which "top level" type variable scope contains the type variable (we will call this the
     * "type container")
     * <li>what are the other names defined locally within that type container
     * <li>what are the type variables that are <i>shadowed</i> by the renamed definition.
     * </ul>
     * 
     * @author Joseph Wong
     */
    private static final class FirstPass extends IdentifierOccurrenceFinder<Void> {
        
        /**
         * The target to be renamed.
         */
        private final IdentifierInfo.TypeVariable target;
        
        /**
         * The new name for the target.
         */
        private final String newName;
        
        /**
         * The current "type container" - it is non-null only if one is being visited.
         */
        private SourceElement currentTypeContainer;
        
        /**
         * The current algebraic function - it is non-null only if an algebraic function is being visited.
         */
        private FunctionDefn.Algebraic currentAlgebraicFunction;
        
        /**
         * The set of type variable names defined in the current type container.
         */
        private final Set<String> typeVarsInCurrentTypeContainer = new HashSet<String>();
        
        /**
         * The type container containing the target - this is null until the target is found.
         */
        private SourceElement typeContainerContainingTarget;
        
        /**
         * The algebraic function containing the target.
         * 
         * Can be null if the target is not in an algebraic function.
         */
        private FunctionDefn.Algebraic algebraicFunctionContainingTarget;
        
        /**
         * The set of type variable names defined in the type container containing the target - this is empty until
         * the target is found.
         */
        private final Set<String> typeVarsInTypeContainerContainingTarget = new HashSet<String>();
        
        /**
         * A map of all type variable definitions that are shadowed by the new name for the target.
         */
        private final Map<IdentifierInfo.TypeVariable, Binding<IdentifierInfo.TypeVariable>> shadowedTypeVars =
            new HashMap<IdentifierInfo.TypeVariable, Binding<IdentifierInfo.TypeVariable>>();

        /**
         * Construct an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param target the target to be renamed.
         * @param newName the new name for the target.
         */
        FirstPass(final ModuleName currentModuleName, final IdentifierInfo.TypeVariable target, final String newName) {
            super(currentModuleName);
            if (target == null || newName == null) {
                throw new NullPointerException();
            }
            this.target = target;
            this.newName = newName;
        }
        
        /**
         * @return the algebraic function containing the target.
         * 
         * Can be null if the target is not in an algebraic function.
         */
        FunctionDefn.Algebraic getAlgebraicFunctionContainingTarget() {
            return algebraicFunctionContainingTarget;
        }

        /**
         * @return the set of type variable names defined in the type container containing the
         *         target - this is empty until the target is found.
         */
        Set<String> getTypeVarsInTypeContainerContainingTarget() {
            return typeVarsInTypeContainerContainingTarget;
        }

        /**
         * @return a map of all type variable definitions that are shadowed by the new name for the target.
         */
        Map<IdentifierInfo.TypeVariable, Binding<IdentifierInfo.TypeVariable>> getShadowedTypeVars() {
            return shadowedTypeVars;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_FunctionDefn_Algebraic(final FunctionDefn.Algebraic algebraic, final VisitorArgument<FinderState> arg) {
            // optimization: if already found the source element containing the target, just return
            // note that we are not checking against algebraicFunctionContainingTarget, which may be null
            // even *after* the target has been found, because the target may have been found outside of an algebraic function
            if (typeContainerContainingTarget != null && typeContainerContainingTarget != currentTypeContainer) {
                return null;
            }
            
            currentAlgebraicFunction = algebraic;
            super.visit_FunctionDefn_Algebraic(algebraic, arg);
            currentAlgebraicFunction = null;
            
            return null;
        }

        ////
        /// Visitor methods - elements which introduce new type variable scopes
        //
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_InstanceDefn(final InstanceDefn defn, final VisitorArgument<FinderState> arg) {
            // optimization: if already found the source element containing the target, just return
            if (typeContainerContainingTarget != null && typeContainerContainingTarget != currentTypeContainer) {
                return null;
            }

            if (currentTypeContainer == null) {
                preProcessTypeContainer(defn);
                super.visit_InstanceDefn(defn, arg);
                postProcessTypeContainer(defn);
            } else {
                super.visit_InstanceDefn(defn, arg);
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeClassDefn(final TypeClassDefn defn, final VisitorArgument<FinderState> arg) {
            // optimization: if already found the source element containing the target, just return
            if (typeContainerContainingTarget != null && typeContainerContainingTarget != currentTypeContainer) {
                return null;
            }

            if (currentTypeContainer == null) {
                preProcessTypeContainer(defn);
                super.visit_TypeClassDefn(defn, arg);
                postProcessTypeContainer(defn);
            } else {
                super.visit_TypeClassDefn(defn, arg);
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(final TypeConstructorDefn.AlgebraicType.DataConsDefn defn, final VisitorArgument<FinderState> arg) {
            // optimization: if already found the source element containing the target, just return
            if (typeContainerContainingTarget != null && typeContainerContainingTarget != currentTypeContainer) {
                return null;
            }

            if (currentTypeContainer == null) {
                preProcessTypeContainer(defn);
                super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg);
                postProcessTypeContainer(defn);
            } else {
                super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg);
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType(final TypeConstructorDefn.AlgebraicType type, final VisitorArgument<FinderState> arg) {
            // optimization: if already found the source element containing the target, just return
            if (typeContainerContainingTarget != null && typeContainerContainingTarget != currentTypeContainer) {
                return null;
            }

            if (currentTypeContainer == null) {
                preProcessTypeContainer(type);
                super.visit_TypeConstructorDefn_AlgebraicType(type, arg);
                postProcessTypeContainer(type);
            } else {
                super.visit_TypeConstructorDefn_AlgebraicType(type, arg);
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeSignature(final TypeSignature signature, final VisitorArgument<FinderState> arg) {
            // optimization: if already found the source element containing the target, just return
            if (typeContainerContainingTarget != null && typeContainerContainingTarget != currentTypeContainer) {
                return null;
            }

            if (currentTypeContainer == null) {
                preProcessTypeContainer(signature);
                super.visit_TypeSignature(signature, arg);
                postProcessTypeContainer(signature);
            } else {
                super.visit_TypeSignature(signature, arg);
            }
            
            return null;
        }

        /**
         * Sets up the visitor on entering a "type container".
         * @param typeContainer the associated source element.
         */
        private void preProcessTypeContainer(final SourceElement typeContainer) {
            currentTypeContainer = typeContainer;
            typeVarsInCurrentTypeContainer.clear();
        }
        
        /**
         * Performs wrap-up on exiting a "type container".
         * @param typeContainer the associated source element.
         */
        private void postProcessTypeContainer(final SourceElement typeContainer) {
            if (typeContainerContainingTarget == typeContainer) {
                // we have found the right container source element, so copy so the type vars
                typeVarsInTypeContainerContainingTarget.addAll(typeVarsInCurrentTypeContainer);
            }
            
            // reset the state
            currentTypeContainer = null;
            typeVarsInCurrentTypeContainer.clear();
        }
        
        ////
        /// Overridden handler methods
        //

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleTypeVariableBinding(final Binding<IdentifierInfo.TypeVariable> binding, final TypeVariableScope scope) {

            if (target.equals(binding.getIdentifierInfo())) {
                // set the fields to remember the match
                typeContainerContainingTarget = currentTypeContainer;
                algebraicFunctionContainingTarget = currentAlgebraicFunction;
                
                // find all the local definitions that are shadowed
                TypeVariableScope scopeToCheck = scope;
                while (scopeToCheck != null) {
                    for (final Binding<IdentifierInfo.TypeVariable> bindingInScope : scopeToCheck.getBindings()) {
                        if (newName.equals(bindingInScope.getIdentifierInfo().getTypeVarName())) {
                            shadowedTypeVars.put(bindingInScope.getIdentifierInfo(), bindingInScope);
                        }
                    }
                    
                    scopeToCheck = scopeToCheck.getParent();
                }
            }
            
            typeVarsInCurrentTypeContainer.add(binding.getIdentifierInfo().getTypeVarName());
            
            super.handleTypeVariableBinding(binding, scope);
        }
    }
    
    /**
     * The second pass takes the information from the first pass, and calculates:
     * <ul>
     * <li>which type variables <i>shadow</i> the renamed definition. These are to be renamed so
     * as to not conflict with the renamed definition.
     * <li>where are all the occurrences of the variable to be renamed
     * <li>which occurrences correspond to the shadowed definitions and the shadowing definitions -
     * these will need to be renamed to something else to avoid conflicts (these are termed
     * "collateral damage")
     * </ul>
     * 
     * @author Joseph Wong
     */
    private static final class SecondPass extends IdentifierOccurrenceFinder<Void> {
        
        /**
         * The target to be renamed.
         */
        private final IdentifierInfo.TypeVariable target;
        
        /**
         * The new name for the target.
         */
        private final String newName;
        
        /**
         * A map of all type variable definitions that are shadowed by the new name for the target. Can *not* be null.
         */
        private final Map<IdentifierInfo.TypeVariable, Binding<IdentifierInfo.TypeVariable>> shadowedTypeVars;
        
        /**
         * The scope that defines the target. Can *not* be null.
         */
        private TypeVariableScope definingScopeOfTarget;
        
        /**
         * A set for collecting the type variables that would shadow the renamed target.
         */
        private final Set<IdentifierInfo.TypeVariable> shadowingTypeVars = new HashSet<IdentifierInfo.TypeVariable>();

        /**
         * The algebraic function containing the target.
         * 
         * Can be null if the target is not in an algebraic function.
         */
        private final FunctionDefn.Algebraic algebraicFunctionContainingTarget;
        
        /**
         * A list of the occurrences of the target to be renamed.
         */
        private final List<IdentifierOccurrence<?>> occurrencesToRename = new ArrayList<IdentifierOccurrence<?>>();
        
        /**
         * A list of the occurrences corresponding to the shadowed definitions and the shadowing
         * definitions - these will need to be renamed to something else to avoid conflicts (these
         * are termed "collateral damage").
         */
        private final List<IdentifierOccurrence<?>> collaterallyDamagedTypeVarOccurrences = new ArrayList<IdentifierOccurrence<?>>();

        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param target the target to be renamed.
         * @param newName the new name for the target.
         * @param algebraicFunctionContainingTarget the algebraic function containing the target.
         * Can be null if the target is not in an algebraic function.
         * @param shadowedTypeVars a map of all type variable definitions that are shadowed by the new name for the target. Can *not* be null.
         */
        SecondPass(final ModuleName currentModuleName, final IdentifierInfo.TypeVariable target, final String newName, final FunctionDefn.Algebraic algebraicFunctionContainingTarget, final Map<IdentifierInfo.TypeVariable, Binding<IdentifierInfo.TypeVariable>> shadowedTypeVars) {
            super(currentModuleName);
            if (target == null || newName == null || shadowedTypeVars == null) {
                throw new NullPointerException();
            }
            this.target = target;
            this.newName = newName;
            this.algebraicFunctionContainingTarget = algebraicFunctionContainingTarget;
            this.shadowedTypeVars = shadowedTypeVars;
        }
        
        /**
         * @return alist of the occurrences of the target to be renamed.
         */
        List<IdentifierOccurrence<?>> getOccurrencesToRename() {
            return occurrencesToRename;
        }

        /**
         * @return a list of the occurrences corresponding to the shadowed definitions and the
         *         shadowing definitions - these will need to be renamed to something else to avoid
         *         conflicts (these are termed "collateral damage").
         */
        List<IdentifierOccurrence<?>> getCollaterallyDamagedTypeVarOccurrences() {
            return collaterallyDamagedTypeVarOccurrences;
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
        protected void handleNewTypeVariableScope(final TypeVariableScope scope) {
            
            // first loop: set the definingScopeOfTarget if the target is indeed defined in this scope
            for (final Binding<IdentifierInfo.TypeVariable> binding : scope.getBindings()) {
                if (target.equals(binding.getIdentifierInfo())) {
                    definingScopeOfTarget = scope;
                }
            }

            // second loop - done by superclass implementation:
            // determining if the binding corresponds to the one needing to be renamed
            // or to one of the "collaterally damaged" names that need to be renamed to something else
            super.handleNewTypeVariableScope(scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleTypeVariableBinding(final Binding<IdentifierInfo.TypeVariable> binding, final TypeVariableScope scope) {
            if (target.equals(binding.getIdentifierInfo())) {
                occurrencesToRename.add(binding);

            } else if (shadowedTypeVars.containsKey(binding.getIdentifierInfo())) {
                collaterallyDamagedTypeVarOccurrences.add(binding);
                
            } else if (isSameOrDescendantScope(scope, definingScopeOfTarget) && newName.equals(binding.getIdentifierInfo().getTypeVarName())) {
                // check to see if this definition shadows the target
                shadowingTypeVars.add(binding.getIdentifierInfo());
                collaterallyDamagedTypeVarOccurrences.add(binding);
            }
            
            super.handleTypeVariableBinding(binding, scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleTypeVariableReference(final Reference<IdentifierInfo.TypeVariable> reference, final Binding<IdentifierInfo.TypeVariable> binding, final TypeVariableScope scope) {

            if (target.equals(reference.getIdentifierInfo())) {
                occurrencesToRename.add(reference);
                
            } else if (shadowingTypeVars.contains(reference.getIdentifierInfo())) {
                collaterallyDamagedTypeVarOccurrences.add(reference);
                
            } else if (shadowedTypeVars.containsKey(reference.getIdentifierInfo())) {
                collaterallyDamagedTypeVarOccurrences.add(reference);
            }
            
            super.handleTypeVariableReference(reference, binding, scope);
        }
    }

    /** Private constructor. */
    private TypeVariableRenamer() {}
    
    /**
     * Helper method for determining whether a scope is the same, or is a descendant of, another scope.
     * @param potentialDescendantScope the potential descendant scope.
     * @param potentialAncestorScope the potential ancestor scope.
     * @return true if the first scope is the same, or a descendant of, the second scope.
     */
    private static boolean isSameOrDescendantScope(final TypeVariableScope potentialDescendantScope, final TypeVariableScope potentialAncestorScope) {
        if (potentialAncestorScope == null) {
            return false;
        }
        
        if (potentialDescendantScope == potentialAncestorScope) {
            return true;
        }
        
        TypeVariableScope parent = potentialDescendantScope.getParent();
        while (parent != null) {
            if (parent == potentialAncestorScope) {
                return true;
            }
            parent = parent.getParent();
        }
        
        return false;
    }
    
    /**
     * Runs the renaming logic for a type variable and returns the necessary modifications in
     * a {@link SourceModifier}.
     * 
     * @param moduleTypeInfo ModuleTypeInfo for the module to process 
     * @param oldName the identifier being renamed
     * @param newName the name name
     * @param messageLogger CompilerMessageLogger for logging failures
     * @return a SourceModifier that will apply the renaming to the source. 
     */
    static SourceModifier getSourceModifier(final ModuleTypeInfo moduleTypeInfo, final String sourceText, final IdentifierInfo.TypeVariable oldName, final String newName, final CompilerMessageLogger messageLogger) {

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
        
        final Set<String> typeVarsInTypeContainerContainingTarget = firstPass.getTypeVarsInTypeContainerContainingTarget();
        
        final SecondPass secondPass = new SecondPass(moduleName, oldName, newName, firstPass.getAlgebraicFunctionContainingTarget(), firstPass.getShadowedTypeVars());
        moduleDefn.accept(secondPass, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(moduleTypeInfo)), FinderState.make()));

        for (final IdentifierOccurrence<?> occurrenceToRename : secondPass.getOccurrencesToRename()) {
            addTypeVarRenameModification(sourceModifier, sourceText, occurrenceToRename, newName);
        }
        
        if (!secondPass.getCollaterallyDamagedTypeVarOccurrences().isEmpty()) {
            final String newNameForCollateralDamage = getNewNameForCollateralDamage(newName, typeVarsInTypeContainerContainingTarget);
            for (final IdentifierOccurrence<?> collateralDamage : secondPass.getCollaterallyDamagedTypeVarOccurrences()) {
                addTypeVarRenameModification(sourceModifier, sourceText, collateralDamage, newNameForCollateralDamage);
            }
        }

        return sourceModifier;
    }

    /**
     * Produces a non-conflicting name for definitions deemed "collateral damage".
     * @param newName the new name of the target.
     * @param typeVarsInTypeContainerContainingTarget the other type variable names in the type container containing the target <i>to be avoided</i>.
     * @return a non-conflicting name.
     */
    private static String getNewNameForCollateralDamage(final String newName, final Set<String> typeVarsInTypeContainerContainingTarget) {
        String newNameForCollateralDamage;
        int disambiguator = 1; 
        do {
            newNameForCollateralDamage = newName + "_" + disambiguator;
            disambiguator++;
        } while (typeVarsInTypeContainerContainingTarget.contains(newNameForCollateralDamage));
        return newNameForCollateralDamage;
    }

    /**
     * Adds a source modification corresponding to a type variable name renaming.
     * @param sourceModifier the source modifier to add to.
     * @param sourceText the source text.
     * @param occurrenceToRename the occurrence to rename.
     * @param newName the new name.
     */
    private static void addTypeVarRenameModification(final SourceModifier sourceModifier, final String sourceText, final IdentifierOccurrence<?> occurrenceToRename, final String newName) {
        sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToRename.getSourceRange(), newName));
    }
}
