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
 * SearchManager.java
 * Created: Sep 20, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.IdentifierOccurrenceFinder.FinderState;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable;
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;

/**
 * This immutable class encapsulates logic for performing searches on source code and performing structured
 * source code navigation. The functionality is based on source model traversal, implemented by
 * subclasses of {@link IdentifierOccurrenceFinder}.
 * 
 * @see IdentifierOccurrenceFinder
 * @see IdentifierOccurrenceCollector
 * 
 * @author Joseph Wong
 * @author James Wright
 * @author Greg McClement
 */
/*
 * @history
 * 
 * This class contains search-related functionality formerly implemented in the SourceMetricsManager by James Wright
 * and Greg McClement.
 */
public final class SearchManager {

    /**
     * A visitor class that traverses a source model, and collects only those occurrences that refer
     * to a particular named target.
     *
     * @author Joseph Wong
     */
    private static final class IdentifierMatchesCollector extends IdentifierOccurrenceCollector<Void> {
        
        /**
         * The target to look for.
         */
        private final IdentifierOccurrence<?> target;

        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param target the target to look for.
         * @param shouldCollectDeclaredBindings whether to collect declared bindings of the target.
         * @param shouldCollectReferences whether to collect references to the target.
         */
        private IdentifierMatchesCollector(final ModuleName currentModuleName, final IdentifierOccurrence<?> target, final boolean shouldCollectDeclaredBindings, final boolean shouldCollectReferences) {
            super(currentModuleName, shouldCollectDeclaredBindings, false, shouldCollectReferences, shouldCollectReferences, false);
            this.target = target;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean matchesDesiredIdentifiers(final IdentifierOccurrence<?> occurrence) {
            final IdentifierInfo targetIdentifierInfo = target.getIdentifierInfo();
            final IdentifierInfo occurrenceIdentifierInfo = occurrence.getIdentifierInfo();
            
            if (targetIdentifierInfo instanceof IdentifierInfo.DataConsFieldName) {
                // the target may be a data cons field name associated with multiple data cons... a non-empty overlap
                // with the occurrence in terms of the associated data constructors is enough to cause a match
                if (occurrenceIdentifierInfo instanceof IdentifierInfo.DataConsFieldName) {
                    final IdentifierInfo.DataConsFieldName targetFieldInfo = (IdentifierInfo.DataConsFieldName)targetIdentifierInfo;
                    final IdentifierInfo.DataConsFieldName occurrenceFieldInfo = (IdentifierInfo.DataConsFieldName)occurrenceIdentifierInfo;
                    
                    // first we need to check that the field names are actually equal
                    if (targetFieldInfo.getFieldName().getCalSourceForm().equals(occurrenceFieldInfo.getFieldName().getCalSourceForm())) {
                        
                        final Set<IdentifierInfo.TopLevel.DataCons> targetDataConsList = targetFieldInfo.getAssociatedDataConstructors();
                        for (final IdentifierInfo.TopLevel.DataCons occurrenceDataCons : occurrenceFieldInfo.getAssociatedDataConstructors()) {
                            if (targetDataConsList.contains(occurrenceDataCons)) {
                                // the occurrence is associated with a data cons that is also associated with the target, so we declare a match
                                return true;
                            }
                        }
                    }
                }
            }
            
            return targetIdentifierInfo.equals(occurrenceIdentifierInfo);
        }
    }

    /**
     * A visitor class that traverses a source model, and collects only those occurrences that contain
     * a particular source position.
     *
     * @author Joseph Wong
     */
    private static class PositionDirectedOccurrenceCollector extends IdentifierOccurrenceCollector<Void> {

        /**
         * The source position to look for.
         */
        private final SourcePosition targetPosition;
        
        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         * @param targetPosition the source position to look for.
         */
        PositionDirectedOccurrenceCollector(final ModuleName currentModuleName, final SourcePosition targetPosition) {
            super(currentModuleName, true, true, true, true, true);
            if (targetPosition == null) {
                throw new NullPointerException();
            }
            this.targetPosition = targetPosition;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_FunctionDefn_Algebraic(final FunctionDefn.Algebraic algebraic, final VisitorArgument<FinderState> arg) {
            // optimization: only visit an algebraic function if it contains the target position
            if (algebraic.getSourceRange().containsPosition(targetPosition)) {
                return super.visit_FunctionDefn_Algebraic(algebraic, arg);
            } else {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean matchesDesiredIdentifiers(final IdentifierOccurrence<? extends IdentifierInfo> occurrence) {
            return occurrence.getSourceRange() != null && occurrence.getSourceRange().containsPosition(targetPosition);
        }
    }
    
    /**
     * A visitor class that traverses a source model, and finds the occurences that are
     * after the given position. 
     *
     * @author Greg McClement
     */
    
    public class FindOccurrencesFollowingPositionCollector extends IdentifierOccurrenceCollector<Void> {

        /**
         * The source position to look after.
         */
        private final SourcePosition targetPosition;
        
        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         */
        public FindOccurrencesFollowingPositionCollector(
            final ModuleName currentModuleName, final SourcePosition targetPosition) {
            super(currentModuleName, true, true, true, true, true);
            if (targetPosition == null) {
                throw new NullPointerException();
            }
            this.targetPosition = targetPosition;
        }
        
        /**
         * Checks whether the occurrence should be filtered out because it does not match certain criteria.
         * The default implementation returns true (no filtering).
         * @param occurrence the occurrence to be checked.
         * @return true if it should be kept, false if it should be filtered out.
         */
        protected boolean matchesDesiredIdentifiers(final IdentifierOccurrence<?> occurrence) {
            return 
                occurrence.getSourceRange() != null &&
                occurrence.getSourceRange().getStartSourcePosition().isAfter(targetPosition);                
        }        
    }
    
    /**
     * The module container.
     */
    private final ModuleContainer moduleContainer;
    
    /**
     * Constructs an instance of this class.
     * @param moduleContainer the module container.
     */
    public SearchManager(final ModuleContainer moduleContainer) {
        if (moduleContainer == null) {
            throw new NullPointerException();
        }
        this.moduleContainer = moduleContainer;
    }

    /**
     * Creates a source position for use with the API in this class.
     * @param line the line number.
     * @param column the column number.
     * @param moduleName the module name. Can be null.
     * @return a source position.
     */
    public static SourcePosition makeSourcePosition(final int line, final int column, final ModuleName moduleName) {
        return new SourcePosition(line, column, moduleName);
    }    
    
    /**
     * Finds the identifier occurrence at the specified source position.
     * @param moduleName the name of the module to process.
     * @param sourcePosition the source position to look for.
     * @param sourcePositionToTheLeft the source position immediately to the left. Can be null if there is none
     *          (e.g. start of line, or is in a bit of modification not currently represented in the source model)
     * @param logger a message logger.
     * @return the most specific occurrence containing the source position, or null if none could be found.
     */
    public IdentifierOccurrence<?> findSymbolAt(final ModuleName moduleName, final SourcePosition sourcePosition, final SourcePosition sourcePositionToTheLeft, final CompilerMessageLogger logger) {

        final PositionDirectedOccurrenceCollector collector = new PositionDirectedOccurrenceCollector(moduleName, sourcePosition);
        
        final boolean visitOK = visitModule(moduleName, collector, logger);
        if (!visitOK) {
            return null;
        }
        
        if (collector.hasCollectedOccurrences()) {
            return getMostSpecificOccurrence(collector);
            
        } else {
            // the collector could not find anything, so perhaps the source position is on a whitespace or a punctuation
            // we should try the position immediately to the left, if we're not at the first column already
            
            if (sourcePositionToTheLeft != null) {
                final PositionDirectedOccurrenceCollector collectorForPosToTheLeft = new PositionDirectedOccurrenceCollector(moduleName, sourcePositionToTheLeft);
                
                final boolean secondVisitOK = visitModule(moduleName, collectorForPosToTheLeft, logger);
                if (!secondVisitOK) {
                    return null;
                }
                
                return getMostSpecificOccurrence(collectorForPosToTheLeft);
            } else {
                return null;
            }
        }
    }

    /**
     * Finds the symbols after the specified source position.
     * @param moduleName the name of the module to process.
     * @param line the line of the position.
     * @param column the column of the position.
     * @param logger a message logger.
     * @return list of symbols after the given position, or null if none could be found.
     */
    public List<IdentifierOccurrence<?>> findSymbolsAfter(final ModuleName moduleName, final int line, final int column, final CompilerMessageLogger logger) {
        return findSymbolsAfter(moduleName, new SourcePosition(line, column, moduleName), logger);
    }
    
    /**
     * Finds the symbols after the specified source position.
     * @param moduleName the name of the module to process.
     * @param sourcePosition the source position to look for.
     * @param logger a message logger.
     * @return list of symbols after the given position, or null if none could be found.
     */
    public List<IdentifierOccurrence<?>> findSymbolsAfter(final ModuleName moduleName, final SourcePosition sourcePosition, final CompilerMessageLogger logger) {

        final FindOccurrencesFollowingPositionCollector collector = new FindOccurrencesFollowingPositionCollector(moduleName, sourcePosition);
        
        final boolean visitOK = visitModule(moduleName, collector, logger);
        if (!visitOK) {
            return null;
        }
        
        return collector.getCollectedOccurrences();
    }
    
    /**
     * Finds the definition of the identifier appearing in the given occurrence.
     * @param targetModuleName the module defining the target.
     * @param target an occurrence of the target.
     * @param logger a message logger.
     * @return the definition of the identifier.
     */
    public IdentifierOccurrence<?> findDefinition(final ModuleName targetModuleName, final IdentifierOccurrence<?> target, final CompilerMessageLogger logger) {
        if (target.getIdentifierInfo() instanceof IdentifierInfo.Local.Parameter.InstanceMethodCALDoc) {
            // an instance method's CALDoc-declared argument's definition is itself... we do this check specially since
            // the IdentifierInfo.Local.Parameter.InstanceMethodCALDoc is currently non unique
            // todo-jowong when the identifier info is fixed to be unique in the module, we probably don't need this special case
            return target;
            
        } else {
            final boolean shouldCollectDeclaredBindings = true;
            final boolean shouldCollectExpressions = false;
            
            final IdentifierOccurrenceCollector<Void> collector = new IdentifierMatchesCollector(targetModuleName, target, shouldCollectDeclaredBindings, shouldCollectExpressions);
            
            final boolean visitOK = visitModule(targetModuleName, collector, logger);
            if (!visitOK) {
                return null;
            }
            
            return getMostSpecificOccurrence(collector);
        }
    }

    /**
     * Finds all occurrences in the given module of the identifier appearing in the given occurrence.
     * @param currentModuleName the module to process.
     * @param target an occurrence of the target.
     * @param logger a message logger.
     * @return all occurrences in the same module.
     */
    public List<IdentifierOccurrence<?>> findOccurrencesInCurrentModule(final ModuleName currentModuleName, final IdentifierOccurrence<?> target, final CompilerMessageLogger logger) {
        if (target.getIdentifierInfo() instanceof IdentifierInfo.Local.Parameter.InstanceMethodCALDoc) {
            // an instance method's CALDoc-declared argument's definition is itself... we do this check specially since
            // the IdentifierInfo.Local.Parameter.InstanceMethodCALDoc is currently non unique
            // todo-jowong when the identifier info is fixed to be unique in the module, we probably don't need this special case
            return Collections.<IdentifierOccurrence<?>>singletonList(target);
            
        } else {
            final boolean shouldCollectDeclaredBindings = true;
            final boolean shouldCollectExpressions = true;
            
            final IdentifierOccurrenceCollector<Void> collector = new IdentifierMatchesCollector(currentModuleName, target, shouldCollectDeclaredBindings, shouldCollectExpressions);
            
            final boolean visitOK = visitModule(currentModuleName, collector, logger);
            if (!visitOK) {
                return null;
            }
            
            return collector.getCollectedOccurrences();
        }
    }

    /**
     * Internal helper method to visit a module with a {@link IdentifierOccurrenceFinder}.
     * @param moduleName the name of the module to visit.
     * @param visitor the visitor.
     * @param logger a message logger for logging parser errors.
     * @return true if the module was visited successfully; false if the module could not be visited
     *         (e.g. the module does not exist, or contains syntax errors).
     */
    private boolean visitModule(final ModuleName moduleName, final IdentifierOccurrenceFinder<?> visitor, final CompilerMessageLogger logger) {
        final MessageLogger loggerForParsingErrors = new MessageLogger();

        final SourceModel.ModuleDefn moduleDefn = moduleContainer.getSourceModel(moduleName, false, loggerForParsingErrors);

        if (moduleDefn == null) {
            augmentAndCopyLoggerMessages(moduleName, loggerForParsingErrors, logger);
            return false;
        }

        // todo-jowong get rid of the reliance on module type info when possible (when visibility check for empty context is fixed)
        final ModuleTypeInfo homeModuleTypeInfo = moduleContainer.getModuleTypeInfo(moduleName);
        if (homeModuleTypeInfo == null){
            return false;
        }

        moduleDefn.accept(visitor, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(homeModuleTypeInfo)), FinderState.make()));

        return true;
    }

    /**
     * Retrieves the most specific occurrence from an {@link IdentifierOccurrenceCollector} - the
     * first occurrence that does not container another occurrence.
     * @param collector the collector to process.
     * @return the most specific occurrence.
     */
    private IdentifierOccurrence<?> getMostSpecificOccurrence(final IdentifierOccurrenceCollector<?> collector) {
        final List<IdentifierOccurrence<?>> collectedOccurrences = collector.getCollectedOccurrences();
        
        // return the first occurrence that does not contain any other occurrence
        for (final IdentifierOccurrence<?> occurrence : collectedOccurrences) {
            boolean containsAnother = false;
            for (final IdentifierOccurrence<?> other : collectedOccurrences) {
                if (occurrence != other && occurrence.getSourceRange().contains(other.getSourceRange())) {
                    containsAnother = true;
                    break;
                }
            }
            if (!containsAnother) {
                return occurrence;
            }
        }
        
        return null;
    }

    /**
     * Augment each message in the given logger with the given module name.
     * @param moduleName the module name to use.
     * @param inputLogger the input logger.
     * @param outputLogger the output logger.
     */
    private void augmentAndCopyLoggerMessages(final ModuleName moduleName, final CompilerMessageLogger inputLogger, final CompilerMessageLogger outputLogger) {
        // Copy messages, adding source names to each SourcePosition
        for (final CompilerMessage compilerMessage : inputLogger.getCompilerMessages()) {
            final SourceRange sourceRange = compilerMessage.getSourceRange(); 
            if(sourceRange != null) {
                final SourceRange augmentedRange = getAugmentedRange(sourceRange, moduleName);
                outputLogger.logMessage(new CompilerMessage(augmentedRange, compilerMessage.getMessageKind()));
            } else {
                outputLogger.logMessage(compilerMessage);
            }
        }
    }

    /**
     * Returns a source range the same as the given one except that the module name is updated.
     * @param sourceRange the source range to add the module name to
     * @param moduleName the module name to add to the source range
     * @return a new source range that is the same as the given one except that the module name is updated.
     */
    private static SourceRange getAugmentedRange(final SourceRange sourceRange, final ModuleName moduleName){
        final SourcePosition augmentedStartPosition = new SourcePosition(sourceRange.getStartLine(), sourceRange.getStartColumn(), moduleName);
        final SourcePosition augmentedEndPosition = new SourcePosition(sourceRange.getEndLine(), sourceRange.getEndColumn(), moduleName);
        return new SourceRange(augmentedStartPosition, augmentedEndPosition);
    }
}
