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
 * DataConsFieldNameRenamer.java
 * Created: Sep 24, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.IdentifierInfo.DataConsFieldName;
import org.openquark.cal.compiler.IdentifierOccurrence.Binding;
import org.openquark.cal.compiler.IdentifierOccurrence.Reference;
import org.openquark.cal.compiler.IdentifierOccurrenceFinder.FinderState;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable;
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.LocalScope;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.TopLevelScope;

/**
 * This class encapsulates the renaming logic for handling data constructor field names.
 * <p>
 * <h4>Some background</h4>
 * A data constructor field name can be associated with multiple data constructors when it appears
 * in a case alternative containing more than one data constructor, e.g.
 * <pre>
 * case foo of
 * (DC1|DC2|DC3) {field1=bar, field2} -> ...
 * </pre>
 * Both field1 and field2 are associated with DC1, DC2, and DC3.
 * <p>
 * When renaming such a field, care must be taken to ensure that all data constructors linked
 * by such case alternatives must have the named field renamed <i>together</i> in order to preserve
 * semantics.
 * <p>
 * The renaming logic works in two passes:
 * <ol>
 * <li>
 *  The first pass goes through all modules in which the data constructor is visible, and attempts
 *  to find case expressions containing alternatives involving the data constructor field. All other
 *  data constructors appearing in those particular alternatives are recorded.
 * <li>
 *  The second pass determines where are all the occurrences of the field name to be renamed,
 *  including any identically-named fields in sibling data constructors identified in the first pass.
 * </ol>
 * The renaming algorithm takes the information gathered by the two passes to produce a set of
 * required source modifications.
 *
 * @author Joseph Wong
 */
final class DataConsFieldNameRenamer {

    /**
     * This first pass goes through all modules in which the data constructor is visible, and
     * attempts to find case expressions containing alternatives involving the data constructor
     * field. All other data constructors appearing in those particular alternatives are recorded.
     * 
     * @author Joseph Wong
     */
    private static final class AllAffectedDataConsCollector extends IdentifierOccurrenceFinder<Void> {
        
        /**
         * The target data constructor field to be renamed.
         */
        private final IdentifierInfo.DataConsFieldName target;
        
        /**
         * A set for collecting all affected data constructors related to the target via
         * multi-data-constructor case alternatives.
         */
        private final Set<IdentifierInfo.TopLevel.DataCons> affectedDataConsSet = new HashSet<IdentifierInfo.TopLevel.DataCons>();

        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param target the target data constructor field to be renamed.
         */
        AllAffectedDataConsCollector(final ModuleName currentModuleName, final IdentifierInfo.DataConsFieldName target) {
            super(currentModuleName);
            if (target == null) {
                throw new NullPointerException();
            }
            this.target = target;
        }

        /**
         * @return a set of all affected data constructors related to the target via
         *         multi-data-constructor case alternatives.
         */
        Set<IdentifierInfo.TopLevel.DataCons> getAffectedDataConsSet() {
            return affectedDataConsSet;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleLocalVariableBinding(final Binding<IdentifierInfo.Local> binding, final LocalScope scope) {
            if (binding instanceof Binding.PunnedTextualDataConsFieldName<?>) {
                final Binding.PunnedTextualDataConsFieldName<?> punnedBinding = (Binding.PunnedTextualDataConsFieldName<?>)binding;
                
                if (target.getFieldName().getCalSourceForm().equals(punnedBinding.getIdentifierInfo().getVarName())) {
                    boolean overlapsWithTarget = false;
                    for (final Binding<IdentifierInfo.TopLevel.DataCons> dataConsNameBinding : punnedBinding.getDataConsNameBindings()) {
                        if (target.getAssociatedDataConstructors().contains(dataConsNameBinding.getIdentifierInfo())) {
                            overlapsWithTarget = true;
                        }
                    }

                    if (overlapsWithTarget) {
                        for (final Binding<IdentifierInfo.TopLevel.DataCons> dataConsNameBinding : punnedBinding.getDataConsNameBindings()) {
                            affectedDataConsSet.add(dataConsNameBinding.getIdentifierInfo());
                        }
                    }
                }
            }
            super.handleLocalVariableBinding(binding, scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleDataConsFieldNameReference(final Reference.DataConsFieldName reference, final List<Binding<DataConsFieldName>> bindings, final SymbolTable scope) {

            if (target.getFieldName().equals(reference.getIdentifierInfo().getFieldName())) {
                boolean overlapsWithTarget = false;
                for (final Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons> dataConsNameOccurrence : reference.getDataConsOccurrences()) {
                    if (target.getAssociatedDataConstructors().contains(dataConsNameOccurrence.getIdentifierInfo())) {
                        overlapsWithTarget = true;
                    }
                }

                if (overlapsWithTarget) {
                    for (final Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons> dataConsNameOccurrence : reference.getDataConsOccurrences()) {
                        affectedDataConsSet.add(dataConsNameOccurrence.getIdentifierInfo());
                    }
                }
            }
            super.handleDataConsFieldNameReference(reference, bindings, scope);
        }
    }
    
    /**
     * This second pass determines where are all the occurrences of the field name to be renamed,
     * including any identically-named fields in sibling data constructors identified in the first
     * pass.
     * 
     * @author Joseph Wong
     */
    private static final class RenameableOccurrenceCollector extends IdentifierOccurrenceFinder<Void> {
        
        /**
         * A set of all affected data constructors related to the target via multi-data-constructor
         * case alternatives. Can *not* be null.
         */
        private final Set<IdentifierInfo.TopLevel.DataCons> affectedDataConsSet;
        
        /**
         * The original name of the target field to be renamed.
         */
        private final FieldName oldName;
        
        /**
         * A list of the occurrences of the target to be renamed.
         */
        private final List<IdentifierOccurrence<?>> occurrencesToRename = new ArrayList<IdentifierOccurrence<?>>();

        /**
         * @return a list of the occurrences of the target to be renamed.
         */
        List<IdentifierOccurrence<?>> getOccurrencesToRename() {
            return occurrencesToRename;
        }

        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param affectedDataConsSet
         *            a set of all affected data constructors related to the target via
         *            multi-data-constructor case alternatives. Can *not* be null.
         * @param oldName the original name of the target field to be renamed.
         */
        RenameableOccurrenceCollector(final ModuleName currentModuleName, final Set<IdentifierInfo.TopLevel.DataCons> affectedDataConsSet, final FieldName oldName) {
            super(currentModuleName);
            if (affectedDataConsSet == null || oldName == null) {
                throw new NullPointerException();
            }
            this.affectedDataConsSet = affectedDataConsSet;
            this.oldName = oldName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleDataConsFieldNameBinding(final Binding<IdentifierInfo.DataConsFieldName> binding, final TopLevelScope scope) {
            for (final IdentifierInfo.TopLevel.DataCons associatedDataConsInfo : binding.getIdentifierInfo().getAssociatedDataConstructors()) {
                if (affectedDataConsSet.contains(associatedDataConsInfo)) {
                    if (binding.getIdentifierInfo().getFieldName().equals(oldName)) {
                        occurrencesToRename.add(binding);
                        break; // if we allow the loop to continue, the occurrence may be added more than once!
                    }
                }
            }
            super.handleDataConsFieldNameBinding(binding, scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleLocalVariableBinding(final Binding<IdentifierInfo.Local> binding, final LocalScope scope) {
            if (binding instanceof Binding.PunnedTextualDataConsFieldName<?>) {
                final Binding.PunnedTextualDataConsFieldName<?> punnedBinding = (Binding.PunnedTextualDataConsFieldName<?>)binding;
                for (final Binding<IdentifierInfo.TopLevel.DataCons> associatedDataCons : punnedBinding.getDataConsNameBindings()) {
                    if (affectedDataConsSet.contains(associatedDataCons.getIdentifierInfo())) {
                        if (punnedBinding.getIdentifierInfo().getVarName().equals(oldName.getCalSourceForm())) {
                            occurrencesToRename.add(binding);
                            break; // if we allow the loop to continue, the occurrence may be added more than once!
                        }
                    }
                }
            }
            super.handleLocalVariableBinding(binding, scope);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleDataConsFieldNameReference(final Reference.DataConsFieldName reference, final List<Binding<DataConsFieldName>> bindings, final SymbolTable scope) {
            for (final Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons> associatedDataCons : reference.getDataConsOccurrences()) {
                if (affectedDataConsSet.contains(associatedDataCons.getIdentifierInfo())) {
                    if (reference.getIdentifierInfo().getFieldName().equals(oldName)) {
                        occurrencesToRename.add(reference);
                        break; // if we allow the loop to continue, the occurrence may be added more than once!
                    }
                }
            }
            super.handleDataConsFieldNameReference(reference, bindings, scope);
        }
    }
    
    /** Private constructor. */
    private DataConsFieldNameRenamer() {}

    /**
     * Calculates the set of all affected data constructors related to the target via
     * multi-data-constructor case alternatives in the specified module.
     * @param oldName the original name of the target field.
     * @param moduleTypeInfo the module type info.
     * @param moduleDefn the corresponding module source model.
     * @return the set of all affected data constructors.
     */
    static Set<IdentifierInfo.TopLevel.DataCons> getAffectedDataConsSetFromModule(final IdentifierInfo.DataConsFieldName oldName, final ModuleTypeInfo moduleTypeInfo, final SourceModel.ModuleDefn moduleDefn) {
        final ModuleName moduleName = moduleTypeInfo.getModuleName();
        final AllAffectedDataConsCollector collector = new AllAffectedDataConsCollector(moduleName, oldName);
        moduleDefn.accept(collector, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(moduleTypeInfo)), FinderState.make()));
        return collector.getAffectedDataConsSet();
    }
    
    /**
     * Runs the renaming logic for a data constructor field name and returns the necessary modifications in
     * a {@link SourceModifier}.
     * 
     * @param moduleTypeInfo ModuleTypeInfo for the module to process 
     * @param affectedDataConsSet
     * @param oldName the identifier being renamed
     * @param newName the name name
     * @param messageLogger CompilerMessageLogger for logging failures
     * @return a SourceModifier that will apply the renaming to the source. 
     */
    static SourceModifier getSourceModifier(final ModuleTypeInfo moduleTypeInfo, final String sourceText, final Set<IdentifierInfo.TopLevel.DataCons> affectedDataConsSet, final FieldName oldName, final FieldName newName, final CompilerMessageLogger messageLogger) {

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
        
        final RenameableOccurrenceCollector collector = new RenameableOccurrenceCollector(moduleName, affectedDataConsSet, oldName);
        moduleDefn.accept(collector, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(moduleTypeInfo)), FinderState.make()));

        for (final IdentifierOccurrence<?> occurrenceToRename : collector.getOccurrencesToRename()) {
            final String newNameString = newName.getCalSourceForm();
            
            if (occurrenceToRename instanceof Binding.PunnedTextualDataConsFieldName<?>) {
                final Binding.PunnedTextualDataConsFieldName<?> punnedBinding = (Binding.PunnedTextualDataConsFieldName<?>)occurrenceToRename;
                // need to unpun the pattern
                sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToRename.getSourceRange(), newNameString + " = " + punnedBinding.getIdentifierInfo().getVarName()));
                
            } else if (occurrenceToRename instanceof Reference.DataConsFieldName.PunnedOrdinal && oldName instanceof FieldName.Ordinal && !(newName instanceof FieldName.Ordinal)) {
                // if this is an ordinal->textual field name renaming, and the original ordinal name appears in a punned context
                // it needs to be unpunned and bound to the wildcard pattern, e.g. {#3} is renamed to {foo = _}
                sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToRename.getSourceRange(), newNameString + " = _"));
                
            } else {
                sourceModifier.addSourceModification(IdentifierRenamer.makeReplaceText(sourceText, occurrenceToRename.getSourceRange(), newNameString));
            }
        }

        return sourceModifier;
    }
}
