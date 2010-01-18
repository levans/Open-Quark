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
 * IdentifierOccurrenceCollector.java
 * Created: Sep 18, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.compiler.IdentifierInfo.DataConsFieldName;
import org.openquark.cal.compiler.IdentifierInfo.Local;
import org.openquark.cal.compiler.IdentifierInfo.Module;
import org.openquark.cal.compiler.IdentifierInfo.TopLevel;
import org.openquark.cal.compiler.IdentifierOccurrence.Binding;
import org.openquark.cal.compiler.IdentifierOccurrence.ForeignDescriptor;
import org.openquark.cal.compiler.IdentifierOccurrence.Reference;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable;
import org.openquark.cal.compiler.IdentifierResolver.TypeVariableScope;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.LocalScope;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.TopLevelScope;

/**
 * A visitor class that traverses a source model, gathers up the bindings declared
 * therein, visits each source element with the appropriate symbol table passed in as a
 * visitation argument, and gathers up the references (non-binding occurrences) as well.
 * The gathered occurrences are filtered based on a number of configurable criteria, and
 * are <i>collected in a list</i>.
 * <p>
 * This is the base class that should be extended if one wants to analyze a
 * source model and collect all identifier occurrences, or only some specific ones.
 *
 * @author Joseph Wong
 * @author James Wright
 */
/*
 * @history
 * 
 * Many of this class's source model traversal functionalities have been supported by the
 * class SourceMetricFinder.SearchWalker, by James Wright.
 */
public class IdentifierOccurrenceCollector<R> extends IdentifierOccurrenceFinder<R> {

    /**
     * Whether declared bindings should be collected.
     */
    private final boolean shouldCollectDeclaredBindings;
    /**
     * Whether external bindings (that do not appear in the source that is analyzed) should be collected.
     */
    private final boolean shouldCollectExternalBindings;
    /**
     * Whether references in an expression context should be collected.
     */
    private final boolean shouldCollectExpressionContextReferences;
    /**
     * Whether references not in an expression context should be collected.
     */
    private final boolean shouldCollectNonExpressionContextReferences;
    /**
     * Whether occurrences of foreign descriptors should be collected.
     */
    private final boolean shouldCollectForeignDescriptors;
    
    /**
     * The list of collected occurrences. These occurrences are *not* in source-position order.
     */
    private final List<IdentifierOccurrence<?>> collectedOccurrences = new ArrayList<IdentifierOccurrence<?>>();

    /**
     * Constructs an instance of this class.
     * @param currentModuleName the name of the module associated with the source being visited.
     * @param shouldCollectDeclaredBindings whether declared bindings should be collected.
     * @param shouldCollectExternalBindings whether external bindings should be collected.
     * @param shouldCollectExpressionContextReferences whether references in an expression context should be collected.
     * @param shouldCollectNonExpressionContextReferences whether references not in an expression context should be collected.
     * @param shouldCollectForeignDescriptors whether occurrences of foreign descriptors should be collected.
     */
    public IdentifierOccurrenceCollector(
        final ModuleName currentModuleName,
        final boolean shouldCollectDeclaredBindings,
        final boolean shouldCollectExternalBindings,
        final boolean shouldCollectExpressionContextReferences,
        final boolean shouldCollectNonExpressionContextReferences,
        final boolean shouldCollectForeignDescriptors) {
        
        super(currentModuleName);
        this.shouldCollectDeclaredBindings = shouldCollectDeclaredBindings;
        this.shouldCollectExternalBindings = shouldCollectExternalBindings;
        this.shouldCollectExpressionContextReferences = shouldCollectExpressionContextReferences;
        this.shouldCollectNonExpressionContextReferences = shouldCollectNonExpressionContextReferences;
        this.shouldCollectForeignDescriptors = shouldCollectForeignDescriptors;
    }
    
    /**
     * @return whether any occurrences has been collected.
     */
    public boolean hasCollectedOccurrences() {
        return !collectedOccurrences.isEmpty();
    }
    
    /**
     * @return a <i>sorted</i> copy of the list of collected occurrences.
     */
    public List<IdentifierOccurrence<?>> getCollectedOccurrences() {
        final List<IdentifierOccurrence<?>> copy = new ArrayList<IdentifierOccurrence<?>>(collectedOccurrences);
        Collections.sort(copy, IdentifierOccurrence.SOURCE_POSITION_BASED_COMPARATOR);
        return copy;
    }

    /**
     * @return whether declared bindings should be collected.
     */
    protected boolean shouldCollectDeclaredBindings() {
        return shouldCollectDeclaredBindings;
    }
    
    /**
     * @return whether external bindings (that do not appear in the source that is analyzed) should be collected.
     */
    protected boolean shouldCollectExternalBindings() {
        return shouldCollectExternalBindings;
    }
    
    /**
     * Checks whether a binding should be filtered out and not be collected.
     * @param binding the binding to be checked.
     * @return true if it should be kept, false if it should be filtered out.
     */
    protected final boolean shouldCollectBinding(final Binding<?> binding) {
        if (binding instanceof Binding.External<?>) {
            return shouldCollectExternalBindings();
        } else {
            return shouldCollectDeclaredBindings();
        }
    }
    
    /**
     * @return whether references in an expression context should be collected.
     */
    protected boolean shouldCollectExpressionContextReferences() {
        return shouldCollectExpressionContextReferences;
    }
    
    /**
     * @return whether references not in an expression context should be collected.
     */
    protected boolean shouldCollectNonExpressionContextReferences() {
        return shouldCollectNonExpressionContextReferences;
    }
    
    /**
     * Checks whether a reference should be filtered out and not be collected.
     * @param reference the reference to be checked.
     * @return true if it should be kept, false if it should be filtered out.
     */
    protected final boolean shouldCollectReference(final Reference<?> reference) {
        final boolean isExpressionContext;
        if (reference instanceof Reference.Qualifiable<?>) {
            isExpressionContext = ((Reference.Qualifiable<?>)reference).isExpressionContext();
        } else if (reference instanceof Reference.Operator<?>) {
            isExpressionContext = ((Reference.Operator<?>)reference).isExpressionContext();
        } else {
            isExpressionContext = false;
        }
        
        if (isExpressionContext) {
            return shouldCollectExpressionContextReferences();
        } else {
            return shouldCollectNonExpressionContextReferences();
        }
    }
    
    /**
     * Checks whether the occurrence should be filtered out because it does not match certain criteria.
     * The default implementation returns true (no filtering).
     * @param occurrence the occurrence to be checked.
     * @return true if it should be kept, false if it should be filtered out.
     */
    protected boolean matchesDesiredIdentifiers(final IdentifierOccurrence<?> occurrence) {
        return true;
    }
    
    /**
     * Processes a binding and collects it if it satisfies all criteria.
     * @param binding the binding to process.
     */
    protected void processBinding(final Binding<?> binding) {
        if (shouldCollectBinding(binding) && matchesDesiredIdentifiers(binding)) {
            collectedOccurrences.add(binding);
        }
    }
    
    /**
     * Processes a reference and collects it if it satisfies all criteria.
     * @param reference the reference to process.
     */
    protected void processReference(final Reference<?> reference) {
        if (shouldCollectReference(reference) && matchesDesiredIdentifiers(reference)) {
            collectedOccurrences.add(reference);
        }
    }
    
    /**
     * Processes a foreign descriptor and collects it if it satisfies all criteria.
     * @param descriptorOccurrence the occurrence to process.
     */
    protected void processForeignDescriptor(final ForeignDescriptor<?> descriptorOccurrence) {
        if (shouldCollectForeignDescriptors && matchesDesiredIdentifiers(descriptorOccurrence)) {
            collectedOccurrences.add(descriptorOccurrence);
        }
    }
    
    ////
    /// Overridden handler methods
    //

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleModuleNameBinding(final Binding<Module> binding, final TopLevelScope scope) {
        processBinding(binding);
        super.handleModuleNameBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleModuleNameReference(final Reference<Module> reference, final Binding<Module> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleModuleNameReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTopLevelFunctionOrClassMethodBinding(final Binding<TopLevel.FunctionOrClassMethod> binding, final TopLevelScope scope) {
        processBinding(binding);
        super.handleTopLevelFunctionOrClassMethodBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleLocalVariableBinding(final Binding<Local> binding, final LocalScope scope) {
        processBinding(binding);
        super.handleLocalVariableBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleVarNameReference(final Reference<? extends IdentifierInfo> reference, final Binding<? extends IdentifierInfo> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleVarNameReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTopLevelFunctionOrClassMethodOperatorReference(final Reference.Operator<TopLevel.FunctionOrClassMethod> reference, final Binding<TopLevel.FunctionOrClassMethod> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleTopLevelFunctionOrClassMethodOperatorReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleDataConsBinding(final Binding<TopLevel.DataCons> binding, final TopLevelScope scope) {
        processBinding(binding);
        super.handleDataConsBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleDataConsNameReference(final Reference<TopLevel.DataCons> reference, final Binding<TopLevel.DataCons> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleDataConsNameReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleDataConsOperatorReference(final Reference.Operator<TopLevel.DataCons> reference, final Binding<TopLevel.DataCons> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleDataConsOperatorReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTypeConsBinding(final Binding<TopLevel.TypeCons> binding, final TopLevelScope scope) {
        processBinding(binding);
        super.handleTypeConsBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTypeConsNameReference(final Reference<TopLevel.TypeCons> reference, final Binding<TopLevel.TypeCons> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleTypeConsNameReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTypeConsOperatorReference(final Reference.Operator<TopLevel.TypeCons> reference, final Binding<TopLevel.TypeCons> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleTypeConsOperatorReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTypeClassBinding(final Binding<TopLevel.TypeClass> binding, final TopLevelScope scope) {
        processBinding(binding);
        super.handleTypeClassBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTypeClassNameReference(final Reference<TopLevel.TypeClass> reference, final Binding<TopLevel.TypeClass> binding, final SymbolTable scope) {
        processReference(reference);
        super.handleTypeClassNameReference(reference, binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleDataConsFieldNameBinding(final Binding<DataConsFieldName> binding, final TopLevelScope scope) {
        processBinding(binding);
        super.handleDataConsFieldNameBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleDataConsFieldNameReference(final Reference.DataConsFieldName reference, final List<Binding<DataConsFieldName>> bindings, final SymbolTable scope) {
        processReference(reference);
        super.handleDataConsFieldNameReference(reference, bindings, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleRecordFieldNameReference(final Reference.RecordFieldName reference) {
        processReference(reference);
        super.handleRecordFieldNameReference(reference);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTypeVariableBinding(final Binding<IdentifierInfo.TypeVariable> binding, final TypeVariableScope scope) {
        processBinding(binding);
        super.handleTypeVariableBinding(binding, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTypeVariableReference(final Reference<IdentifierInfo.TypeVariable> reference, final Binding<IdentifierInfo.TypeVariable> binding, final TypeVariableScope scope) {
        processReference(reference);
        super.handleTypeVariableReference(reference, binding, scope);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleForeignFunctionDescriptor(final ForeignDescriptor<IdentifierInfo.TopLevel.FunctionOrClassMethod> descriptorOccurrence) {
        processForeignDescriptor(descriptorOccurrence);
        super.handleForeignFunctionDescriptor(descriptorOccurrence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleForeignTypeDescriptor(final ForeignDescriptor<IdentifierInfo.TopLevel.TypeCons> descriptorOccurrence) {
        processForeignDescriptor(descriptorOccurrence);
        super.handleForeignTypeDescriptor(descriptorOccurrence);
    }
}
