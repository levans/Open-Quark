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
 * Refactorer.java
 * Creation date: (Feb 27, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openquark.cal.compiler.ModuleContainer.ISourceManager2;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.metadata.MetadataRenameUpdater;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.util.General;
import org.openquark.util.Pair;


/**
 * 
 * A Refactorer represents a specific refactoring operation on a specific 
 * module or set of modules.
 * <p>
 * The usage pattern is:
 * <ol>
 * <li> Construct one of Refactorer's subclasses
 * <li> Call the calculateModifications method to determine the modifications 
 *      the refactoring requires
 * <li> Call the apply method to apply the refactoring to the module(s)
 * <li> (optional) Call undo
 * <li> (optional) Call apply
 * </ol>
 * We do some consistency checking (ie, calling apply before calculateModifications
 * or undo before apply will cause an exception), but we don't check every case.
 * eg, calling apply multiple times can cause data corruption. 
 * <p>
 * This class contains a lot of code that was factored out of the RenameRefactoring class 
 * (which no longer exists).  Some of RenameRefactoring's functionality was factored out 
 * into the base-class, and the renaming-specific portions were moved to the Refactorer.Rename
 * inner class.
 * 
 * @author James Wright
 * @author Iulian Radu
 */
public abstract class Refactorer {

    /** Workspace to apply changes to */
    private final ModuleContainer moduleContainer;
        
    /** Map from module name to a SourceModifier for that module */
    private final Map<ModuleName, SourceModifier> moduleSourceModificationsModifiers = new HashMap<ModuleName, SourceModifier>();
    
    /** Set to true when the changes required to perform this refactoring have been calculated */
    private boolean changesCalculated;
    
    /** Listener for updates */
    private StatusListener statusListener;
    
    /**
     * Interface for listeners wishing to receive status updates during refactoring
     * @author Iulian Radu
     */
    public static interface StatusListener {
        /**
         * Refactorer is about to use the specified resource.
         * @param resource module to be accessed
         */
        public void willUseResource(ModuleSourceDefinition resource);
        
        /**
         * Refactorer has finished using the specified resource.
         * @param resource module which has been accessed
         */
        public void doneUsingResource(ModuleSourceDefinition resource);
    }

    /**
     * Exception which will be logged within a compiler message when the
     * refactorer reports an error/warning.
     * 
     * This object identifies the problematic resource, along with further
     * information detailing the cause.
     * 
     * @author Iulian Radu
     */
    public static final class CALRefactoringException extends Exception {
                
        private static final long serialVersionUID = 3908428565998271064L;

        /** Resource scanned when logging exception */
        private final ModuleSourceDefinition resource;
                                    
        /**
         * Constructor
         * @param resource module causing the problem
         * @param description description of error/warning
         */
        CALRefactoringException (ModuleSourceDefinition resource, String description) {
            super(description, (Throwable)null);
            this.resource = resource;                    
        }
        
        /**
         * Constructor
         * @param resource module causing the problem
         * @param description description of error/warning
         * @param exception exception detailing the problem
         */
        CALRefactoringException (ModuleSourceDefinition resource, String description, Exception exception) {
            super(description, exception);
            this.resource = resource;                     
        }
        
        @Override
        public String toString() {
            return getDescription();
        }
        public String getDescription() {
            return getMessage();
        }
        public ModuleSourceDefinition getResource() {
            return resource;
        }      
    }

    /**
     * Represents the Clean Imports refactoring. 
     * 
     * @author James Wright
     */
    public static final class CleanImports extends Refactorer {
        
        /** Name of the module to perform the Clean Imports refactoring on */
        private final ModuleName moduleName;
        
        /** When this is true, items will not be grouped together or reordered */
        private final boolean preserveItems;
        
        /**
         * @param moduleContainer Workspace of module to perform the refactoring on 
         * @param moduleName Name of the module to perform the refactoring on
         * @param preserveItems When this is true, items will not be grouped together or reordered
         *                       (although the names within the items will still be reordered).
         */
        public CleanImports(ModuleContainer moduleContainer, ModuleName moduleName, boolean preserveItems) {
            super(moduleContainer);
            if(moduleName == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;
            this.preserveItems = preserveItems;
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if(!moduleName.equals(this.moduleName)) {
                return null;
            }
            
            int nOldErrors = messageLogger.getNErrors();
            SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(super.moduleContainer, moduleName, super.readModuleText(moduleName, messageLogger), preserveItems, messageLogger);
            if (messageLogger.getNErrors() > nOldErrors) {
                return null;
            }
            return sourceModifier;
        }
        
        /** {@inheritDoc} */
        @Override
        public List<ModuleSourceDefinition> calculateModifications(CompilerMessageLogger messageLogger) {
            if (messageLogger == null) {
                throw new NullPointerException("Argument messageLogger cannot be null.");
            }
            if(!super.moduleContainer.containsModule(moduleName)) {
                MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleName);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return Collections.emptyList();
            }
            
            return super.calculateModifications(messageLogger);
        }
    }

    
    /**
     * Represents the Pretty Print refactoring. 
     * 
     * @author Magnus Byne
     */
    public static final class PrettyPrint extends Refactorer {
        /** Name of the module to perform the refactoring on */
        private final ModuleName moduleName;

        /** The range of code to update the type info in. This may be null */
        private final SourceRange rangeToUpdate;
        
        /** a set of functions that should be refactored - may be the mpty set*/
        private final Set<String> functionsToUpdate;

        /**
         * Create a pretty refactorer for a complete module. 
         */
        public PrettyPrint(ModuleContainer moduleContainer, ModuleName moduleName)  {
            this(moduleContainer, moduleName, -1, -1,-1,-1, Collections.<String>emptySet());
        }
        
        /**
         * Create the pretty print refactorer for part of a module, defined by
         * a line range or a set of functions. 
         * If the startLine is <= 0,
         * and the function names set is empty, the whole module is refactored.
         * Otherwise only lines between startLine and endLine, or functions
         * that are in the functionNames set are refactored.
         * 
         * @param moduleContainer
         *            Workspace of module to perform the refactoring on
         * @param moduleName
         *            Name of the module to perform the refactoring on
         * @param startLine
         *            the first line of range for pretty printing, if this is <
         *            0 and the the whole file is included
         * @param startColumn
         * @param endLine
         * @param endColumn
         * @param functionNames - set of function names to reformat
         */
        public PrettyPrint(ModuleContainer moduleContainer, ModuleName moduleName, final int startLine, final int startColumn, final int endLine, final int endColumn, final Set<String> functionNames) {
            super(moduleContainer);
            if(moduleName == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;
            functionsToUpdate = new HashSet<String>(functionNames);

            if (startLine > 0){
                final SourcePosition startPosition = new SourcePosition(startLine, startColumn);
                final SourcePosition endPosition = new SourcePosition(endLine, endColumn);
                rangeToUpdate = new SourceRange(startPosition, endPosition);
            }
            else{
                rangeToUpdate = null;
            }
   
        }

        /**
         * @param moduleContainer Workspace of module to perform the refactoring on 
         * @param moduleName Name of the module to perform the refactoring on
         */
        public PrettyPrint(ModuleContainer moduleContainer, ModuleName moduleName, final String function) {
            super(moduleContainer);
            if(moduleName == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;
            functionsToUpdate = Collections.singleton(function);
            rangeToUpdate = null;
        }

        /**
         * Checks to make sure a module is formatted correctly
         * @return true iff the formatted string represents the moduleDefn
         */
        private boolean isFormattingValid(ModuleDefn moduleDefn, String formatted) {
            CompilerMessageLogger mlogger= new MessageLogger();
            ArrayList<SourceEmbellishment> embellishments = new ArrayList<SourceEmbellishment>();
            
            //verify the formatted source can parsed
            ModuleDefn moduleDefnTest = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(formatted, false, mlogger, embellishments);
            if (moduleDefnTest == null) {
                return false;
            }

            if (!General.toPlatformLineSeparators(moduleDefn.toString()).equals(General.toPlatformLineSeparators(moduleDefnTest.toString()))) {
                //the original and formatted code are not equal
                return false;
            }

            return true;
        }
        
        /**
         * {@inheritDoc} 
         */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if(!moduleName.equals(this.moduleName)) {
                return null;
            }
                       
            String oldSource = super.readModuleText(moduleName, messageLogger); //super.moduleContainer.getModuleSource(moduleName);
            
            CompilerMessageLogger mlogger= new MessageLogger();
            ArrayList<SourceEmbellishment> embellishments = new ArrayList<SourceEmbellishment>();
            ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(oldSource, false, mlogger, embellishments);
                       
            if (moduleDefn == null) {
                MessageKind messageKind = new MessageKind.Fatal.DebugMessage("PrettyPrint failed: Unable to parse source module.");
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return null;
            }
                
            final PrettyPrintRefactorer ref= new PrettyPrintRefactorer(oldSource, rangeToUpdate, functionsToUpdate);
            final SourceModifier modifier = ref.getModifier();
            
            if (modifier == null) {
                MessageKind messageKind = new MessageKind.Fatal.DebugMessage("PrettyPrint failed: Internal error.");
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return null;
            }

            //verify the formating has not affect the module
            if (!isFormattingValid(moduleDefn, modifier.apply(oldSource))) {
                MessageKind messageKind = new MessageKind.Fatal.DebugMessage("PrettyPrint failed: Unable to format module. Internal error.");
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return null;                
            }
            
            return modifier;
        }


    }
    
    /**
     * Represents the Insert Import refactoring. 
     * 
     * @author Greg McClement
     */
    public static abstract class InsertImport extends Refactorer {
        
        /** Name of the module to perform the Clean Imports refactoring on */
        protected final ModuleName moduleName;
        
        protected final SourcePosition startPosition;
        protected SourceModification sourceModification;

        /**
         * @param moduleContainer Workspace of module to perform the refactoring on 
         * @param moduleName Name of the module to perform the refactoring on
         */
        public InsertImport(ModuleContainer moduleContainer, ModuleName moduleName, int startLine, int startColumn) {
            super(moduleContainer);
            if(moduleName == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;
            this.startPosition = new SourcePosition(startLine, startColumn);
        }
        
        /**
         * @return the source position that corresponds to the input source position after the file has been modified. 
         * This is typically used to update the cursor position.
         */
        public SourcePosition getNewSourcePosition(){
            return sourceModification.getNewSourcePosition();
        }
        
        /** {@inheritDoc} */
        @Override
        public List<ModuleSourceDefinition> calculateModifications(CompilerMessageLogger messageLogger) {
            if (messageLogger == null) {
                throw new NullPointerException("Argument messageLogger cannot be null.");
            }
            if(!super.moduleContainer.containsModule(moduleName)) {
                MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleName);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return Collections.emptyList();
            }
            
            return super.calculateModifications(messageLogger);
        }
        
        protected ModuleContainer getModuleContainer(){
            return super.moduleContainer;
        }
        
        protected String readModuleText(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            return super.readModuleText(moduleName, messageLogger);
        }
    }
    
    /**
     * Represents the Insert Import refactoring that inserts an import statement for a given name. 
     * 
     * @author Greg McClement
     */
    public static final class InsertImport_Symbol extends InsertImport {
        
        /** The name of the symbol to be imported. */
        private final QualifiedName importSymbol;
        
        /** The category of the import symbol. This may be null */
        private final SourceIdentifier.Category category;

        /**
         * @param moduleContainer Workspace of module to perform the refactoring on 
         * @param moduleName Name of the module to perform the refactoring on
         */
        public InsertImport_Symbol(ModuleContainer moduleContainer, ModuleName moduleName, QualifiedName importSymbol, final SourceIdentifier.Category category, int startLine, int startColumn) {
            super(moduleContainer, moduleName, startLine, startColumn);
            this.importSymbol = importSymbol;
            this.category = category;
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if(!moduleName.equals(this.moduleName)) {
                return null;
            }
            
            int nOldErrors = messageLogger.getNErrors();
            SourceModifier sourceModifier = ImportCleaner.getSourceModifier_insertImport(
                    getModuleContainer(), 
                    moduleName, 
                    readModuleText(moduleName, messageLogger), 
                    importSymbol, 
                    category,
                    false, 
                    messageLogger);
            sourceModification = new SourceModification.ReplaceText("", "", startPosition);
            sourceModifier.addSourceModification(sourceModification);
            if (messageLogger.getNErrors() > nOldErrors) {
                return null;
            }
            return sourceModifier;
        }
    }

    /**
     * Represents the Insert Import refactoring that inserts only an import statement with no names.  
     * 
     * @author Greg McClement
     */
    public static final class InsertImport_Only extends InsertImport {
        
        /** The name of the symbol to be imported. */
        private final ModuleName importedModuleName;
        
        /**
         * @param moduleContainer Workspace of module to perform the refactoring on 
         * @param moduleName Name of the module to perform the refactoring on
         */
        public InsertImport_Only(ModuleContainer moduleContainer, ModuleName moduleName, ModuleName importedModuleName, int startLine, int startColumn) {
            super(moduleContainer, moduleName, startLine, startColumn);
            this.importedModuleName = importedModuleName;
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if(!moduleName.equals(this.moduleName)) {
                return null;
            }
            
            int nOldErrors = messageLogger.getNErrors();
            SourceModifier sourceModifier = ImportCleaner.getSourceModifier_insertImportOnly(
                    getModuleContainer(), 
                    moduleName, 
                    importedModuleName, 
                    readModuleText(moduleName, messageLogger), 
                    false, 
                    messageLogger);
            sourceModification = new SourceModification.ReplaceText("", "", startPosition);
            sourceModifier.addSourceModification(sourceModification);
            if (messageLogger.getNErrors() > nOldErrors) {
                return null;
            }
            return sourceModifier;
        }
    }
    

    /**
     * Represents the Auto-complete refactoring with an insert import for the auto-completed symbol. 
     * 
     * @author Greg McClement
     */
    public static final class AutoCompleteWithInsertImport extends Refactorer {
        
        /** Name of the module to perform the Clean Imports refactoring on */
        private final ModuleName moduleName;
        
        /** When this is true, items will not be grouped together or reordered */
        private final QualifiedName importSymbol;
        
        /** The category of the import symbol. This may be null */
        private final SourceIdentifier.Category category;

        private final SourcePosition startPosition;
        private SourceModification sourceModification;
        
        /**
         * @param moduleContainer Workspace of module to perform the refactoring on 
         * @param moduleName Name of the module to perform the refactoring on
         */
        public AutoCompleteWithInsertImport(ModuleContainer moduleContainer, ModuleName moduleName, QualifiedName importSymbol, final SourceIdentifier.Category category, String oldText, String newText, int startLine, int startColumn) {
            super(moduleContainer);
            if(moduleName == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;
            this.importSymbol = importSymbol;
            
            this.startPosition = new SourcePosition(startLine, startColumn);
            this.category = category;
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if(!moduleName.equals(this.moduleName)) {
                return null;
            }
            
            boolean ignoreErrors = true;
            int nOldErrors = messageLogger.getNErrors();
            SourceModifier sourceModifier = ImportCleaner.getSourceModifier_insertImport(
                    super.moduleContainer, 
                    moduleName, 
                    super.readModuleText(moduleName, messageLogger), 
                    importSymbol,
                    category,
                    ignoreErrors, 
                    messageLogger);
            sourceModification = new SourceModification.ReplaceText("", "", startPosition);
            sourceModifier.addSourceModification(sourceModification);
            if (!ignoreErrors && messageLogger.getNErrors() > nOldErrors) {
                return null;
            }
            return sourceModifier;
        }
        
        /**
         * @return the source position that corresponds to the input source position after the file has been modified. 
         * This is typically used to update the cursor position.
         */
        public SourcePosition getNewSourcePosition(){
            return sourceModification.getNewSourcePosition();
        }
        
        /** {@inheritDoc} */
        @Override
        public List<ModuleSourceDefinition> calculateModifications(CompilerMessageLogger messageLogger) {
            if (messageLogger == null) {
                throw new NullPointerException("Argument messageLogger cannot be null.");
            }
            if(!super.moduleContainer.containsModule(moduleName)) {
                MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleName);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return Collections.emptyList();
            }
            
            return super.calculateModifications(messageLogger);
        }
    }

    /**
     * Replaces old text with new text 
     * 
     * @author Greg McClement
     */
    public static final class ReplaceText extends Refactorer {
        
        /** Name of the module to perform the ReplaceText refactoring on */
        private final ModuleName moduleName;
        
        private final SourcePosition startPosition;
        private final String oldText;
        private final String newText;
        
        private SourceModification sourceModification;
        /**
         * This is used to keep track of the new cursor position after an update. 
         */
        private SourceModification.CursorPosition cursorPosition = null;
        
        /**
         * @param moduleContainer Workspace of module to perform the refactoring on 
         * @param moduleName Name of the module to perform the refactoring on
         */
        public ReplaceText(ModuleContainer moduleContainer, ModuleName moduleName, String oldText, String newText, int startLine, int startColumn) {
            super(moduleContainer);
            if(moduleName == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;
            
            this.oldText = oldText;
            this.newText = newText;
            this.startPosition = new SourcePosition(startLine, startColumn);
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if(!moduleName.equals(this.moduleName)) {
                return null;
            }
            
            boolean ignoreErrors = true;
            int nOldErrors = messageLogger.getNErrors();
            SourceModifier sourceModifier = new SourceModifier();
            sourceModification = new SourceModification.ReplaceText(oldText, newText, startPosition);
            sourceModifier.addSourceModification(sourceModification);
            if (cursorPosition != null){
                sourceModifier.addSourceModification(cursorPosition);
            }
            if (!ignoreErrors && messageLogger.getNErrors() > nOldErrors) {
                return null;
            }
            return sourceModifier;
        }
        
        /**
         * If this is set then getNewCursorPosition can be called to get the updated 
         * position of the cursor after the refactoring. 
         */
        public void setCursorPosition(int line, int column){
            cursorPosition = new SourceModification.CursorPosition(new SourcePosition(line, column));
        }
        
        /**
         * @return the update position of the cursor as provided by a call to setCursorPosition.
         * Calling this function without calling setCursorPosition first is invalid.
         */
        public SourcePosition getNewCursorPosition(){
            if (cursorPosition == null){
                throw new IllegalStateException();
            }
            return cursorPosition.getNewSourcePosition();
        }
        
        /**
         * @return the source position that corresponds to the input source position after the file has been modified. 
         * This is typically used to update the cursor position.
         */
        public SourcePosition getNewSourcePosition(){
            return sourceModification.getNewSourcePosition();
        }
        
        /** {@inheritDoc} */
        @Override
        public List<ModuleSourceDefinition> calculateModifications(CompilerMessageLogger messageLogger) {
            if (messageLogger == null) {
                throw new NullPointerException("Argument messageLogger cannot be null.");
            }
            if(!super.moduleContainer.containsModule(moduleName)) {
                MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleName);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return Collections.emptyList();
            }
            
            return super.calculateModifications(messageLogger);
        }
    }

    /**
     * Represents the Insert Type Declarations refactoring.  Type declarations will be inserted
     * in front of every top-level function and local definition that doesn't already have one
     * (where possible; issues with non-generic type variables can make it impossible to add 
     * explicit type declarations for certain local definitions).
     * 
     * @author James Wright
     */
    public static final class InsertTypeDeclarations extends Refactorer {
        
        /** Name of the module to insert type declarations into */
        private final ModuleName moduleName;
        
        /** The range of code to update the type info in. This may be null */
        private final SourceRange rangeToUpdate;
        
        /** Statistics about the latest refactoring operation */
        private TypeDeclarationInserter.RefactoringStatistics statistics;
        
        /**
         * @param moduleContainer Workspace containing module to perform the refactoring on 
         * @param moduleName String name of the module to perform the refactoring on
         * @param startLine The start line of the range to perform the type declaration on. If this is -1 then the entire module will be considered
         * @param startColumn The start column of the range to perform the type declaration on. Only used when startLine != -1.
         * @param endLine The end line of the range to perform the type declaration on. Only used when startLine != -1.
         * @param endColumn The end column of the range to perform the type declaration on. Only used when startLine != -1.
         */
        public InsertTypeDeclarations(ModuleContainer moduleContainer, ModuleName moduleName, final int startLine, final int startColumn, final int endLine, final int endColumn) {
            super(moduleContainer);
            if(moduleName == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;

            if (startLine > 0){
                final SourcePosition startPosition = new SourcePosition(startLine, startColumn);
                final SourcePosition endPosition = new SourcePosition(endLine, endColumn);
                rangeToUpdate = new SourceRange(startPosition, endPosition);
            }
            else{
                rangeToUpdate = null;
            }
            
            statistics = new TypeDeclarationInserter.RefactoringStatistics();
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if(!moduleName.equals(this.moduleName)) {
                return null;
            }
            
            statistics = new TypeDeclarationInserter.RefactoringStatistics();
            
            int nOldErrors = messageLogger.getNErrors();
            SourceModifier sourceModifier = TypeDeclarationInserter.getSourceModifier(super.moduleContainer, moduleName, rangeToUpdate, super.readModuleText(moduleName, messageLogger), messageLogger, statistics);
            if (messageLogger.getNErrors() > nOldErrors) {
                return null;
            }
            return sourceModifier;
        }

        /** {@inheritDoc} */
        @Override
        public List<ModuleSourceDefinition> calculateModifications(CompilerMessageLogger messageLogger) {
            if (messageLogger == null) {
                throw new NullPointerException("Argument messageLogger cannot be null.");
            }
            if(!super.moduleContainer.containsModule(moduleName)) {
                MessageKind messageKind = new MessageKind.Fatal.ModuleNotInWorkspace(moduleName);
                messageLogger.logMessage(new CompilerMessage(messageKind));
                return Collections.emptyList();
            }
            
            return super.calculateModifications(messageLogger);
        }
    
        /** 
         * @return Number of type declarations that included at least one type class constraint 
         * that were added by the refactoring to top-level functions.
         */
        public int getTopLevelTypeDeclarationsAddedWithClassConstraints() {
            return statistics.getTopLevelTypeDeclarationsAddedWithClassConstraints();
        }

        /** 
         * @return Number of type declarations that included no type class constraints 
         * that were added by the refactoring to top-level functions.
         */
        public int getTopLevelTypeDeclarationsAddedWithoutClassConstraints() {
            return statistics.getTopLevelTypeDeclarationsAddedWithoutClassConstraints();
        }
        
        /** 
         * @return Number of type declarations that included at least one type class constraint 
         * that were added by the refactoring to local functions.
         */
        public int getLocalTypeDeclarationsAddedWithClassConstraints() {
            return statistics.getLocalTypeDeclarationsAddedWithClassConstraints();
        }
        
        /** 
         * @return Number of type declarations that included no type class constraints 
         * that were added by the refactoring to local functions.
         */
        public int getLocalTypeDeclarationsAddedWithoutClassConstraints() {
            return statistics.getLocalTypeDeclarationsAddedWithoutClassConstraints();
        }
        
        /** 
         * @return Number of local functions that did not have type declarations where
         * type declarations were not added because their types had no syntactic representation
         * (eg, types that include non-generic type variables).
         */
        public int getLocalTypeDeclarationsNotAdded() {
            return statistics.getLocalTypeDeclarationsNotAdded();
        }
        
        /**
         * @return Number of import declarations that were added by the refactoring.
         */
        public int getImportsAdded() {
            return statistics.getImportsAdded();
        }
        
        /**
         * @return Number of type declarations that are not added due to the fact that the module names appearing
         * therein would require new import statements that may potential introduce module name resolution conflicts.
         */
        public int getTypeDeclarationsNotAddedDueToPotentialImportConflict() {
            return statistics.getTypeDeclarationsNotAddedDueToPotentialImportConflict();
        }
        
        /**
         * @return The set of module names that are not added as imports due to the fact that they may introduce module name
         * resolution conflicts.
         */
        public SortedSet<ModuleName> getImportsNotAddedDueToPotentialConfict() {
            return statistics.getImportsNotAddedDueToPotentialConfict();
        }
    }
    
    /**
     * Represents the Rename refactoring.  This functionality used to reside in RenameRefactorer.
     * <p>
     * Any references found in the refactored resource will be modified to reflect the new name.
     * <p>
     * References searched include:
     * <ul>
     * <li>"import using" statements
     * <li>function definition name within the container module
     * <li>references within class instance declarations
     * <li>references within function definitions
     * </ul>
     * This operation attempts to rename the function entity as specified, and automatically
     * resolves conflicts between the entity new name and local variable bindings.
     * <p>
     * Example:
     * <pre>
     * Renaming scTest to r in "public scTest x = let r = 1.0; in scTest x"
     *                produces "public r x = let r2 = 1.0; in r x"
     * </pre>               
     *  
     * @author James Wright
     * @author Iulian Radu
     */
    public static final class Rename extends Refactorer {
        
        /**
         * The placeholder unqualified name to use for constructing a qualified name when the module renaming
         * operation requires a qualified name to stand for the entity (the module) being renamed.
         */
        public static final String UNQUALIFIED_NAME_FOR_MODULE_RENAMING = "__moduleRenaming";
        
        /** Typechecker to use for the renaming operations */
        private final TypeChecker typeChecker;
        
        /** Name to rename */
        private final QualifiedName oldName;
        
        /** Name to replace oldName with */
        private final QualifiedName newName;
        
        /** Category of the identifier being renamed */
        private final SourceIdentifier.Category category;

        /** 
         * Map (QualifiedName -> CALFeatureName).  The main point of caching this information
         * is the ability to roll back operations that have caused compilation errors; in 
         * such a case, we may no longer have access to ModuleTypeInfo that we need. 
         */
        private final HashMap<QualifiedName, CALFeatureName> featureNameCache = new HashMap<QualifiedName, CALFeatureName>();
        
        /**
         * @param moduleContainer CALWorkspace to perform renaming in
         * @param typeChecker TypeChecker to use during the renaming
         * @param oldName QualifiedName of the identifier to rename
         * @param newName QualifiedName of the new identifier name
         * @param category Category of oldName and newName
         */
        public Rename(ModuleContainer moduleContainer, TypeChecker typeChecker, QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category) {
            super(moduleContainer);
            
            if(typeChecker == null || oldName == null || newName == null || category == null) {
                throw new NullPointerException();
            }
            
            this.typeChecker = typeChecker;
            this.oldName = oldName;
            this.newName = newName;
            this.category = category;
        }
        
        /** {@inheritDoc} */
        @Override
        public void apply(CompilerMessageLogger logger) {
            if (logger == null) {
                throw new NullPointerException("Argument logger cannot be null.");
            }
            
            int nOldErrors = logger.getNErrors();
            
            updateSources(logger);
            if(logger.getNErrors() > nOldErrors) {
                return;
            }
            
            updateMetadata(logger, oldName, newName);
            if(logger.getNErrors() > nOldErrors) {
                undoSources(logger);
                return;
            }
            
            updateResourceNames(logger, oldName, newName);
            if(logger.getNErrors() > nOldErrors) {
                updateMetadata(logger, newName, oldName);
                undoSources(logger);
                return;
            }
        }
        
        /** {@inheritDoc} */
        @Override
        public void undo(CompilerMessageLogger logger) {
            if (logger == null) {
                throw new NullPointerException("Argument logger cannot be null.");
            }
            updateResourceNames(logger, newName, oldName);
            updateMetadata(logger, newName, oldName);
            undoSources(logger);
        }
        
        /**
         * Perform the part of the refactoring that involves modifying source files.
         * The Refactorer parent class is already set up for source modifications, so
         * delegate to its apply method.
         * @param logger
         */
        private void updateSources(CompilerMessageLogger logger) {
            super.apply(logger);
        }
        
        /**
         * Undo the part of the refactoring that involves modifying source files.
         * The Refactorer parent class is already set up for source modifications, so
         * delegate to its undo method.
         * @param logger
         */
        private void undoSources(CompilerMessageLogger logger) {
            super.undo(logger);
        }
        
        /**
         * Perform the part of the refactoring that involves modifying metadata files.
         * There is no corresponding undo method; to undo the effects of this call, call it
         * again with qualifiedFromName and qualifiedToName reversed.  fromName and toName are both
         * assumed to be in the current category.
         * @param logger
         * @param fromName QualifiedName to change from
         * @param toName QualifiedName to change to
         */
        private void updateMetadata(CompilerMessageLogger logger, QualifiedName fromName, QualifiedName toName) {
            Status updateMetadataStatus = new Status("Update metadata status");                
            MetadataRenameUpdater metadataRenamer = new MetadataRenameUpdater(updateMetadataStatus, typeChecker, toName, fromName, category);
            
            MetadataRenameUpdater.StatusListener refactoringStatusListener = new MetadataRenameUpdater.StatusListener() {
                public void willUseResource(WorkspaceResource resource) {}
                public void doneUsingResource(WorkspaceResource resource) {
                    fireDoneUsingResource(((CALFeatureName)resource.getIdentifier().getFeatureName()).toModuleName());
                }
            };
            
            metadataRenamer.setStatusListener(refactoringStatusListener);
            metadataRenamer.updateMetadata(super.moduleContainer);
            metadataRenamer.setStatusListener(null);
            
            addStatusMessagesToCompilerMessageLogger(updateMetadataStatus, Status.Severity.ERROR, logger);
        }
        
        /**
         * Perform the part of the refactoring that involves renaming resources in the workspace.
         * There is no corresponding undo method; to undo the effects of this call, call it
         * again with qualifiedFromName and qualifiedToName reversed.  fromName and toName are both
         * assumed to be in the current category.
         * 
         * @param logger
         * @param fromName QualifiedName to change from
         * @param toName QualifiedName to change to
         */
        private void updateResourceNames(CompilerMessageLogger logger, QualifiedName fromName, QualifiedName toName) {
            CALFeatureName fromFeatureName = toCALFeatureName(fromName, logger);
            CALFeatureName toFeatureName = toCALFeatureName(toName, logger);
            
            if(fromFeatureName == null || toFeatureName == null) {
                // toCALFeatureName will have already logged an error for us
                return;
            }
            
            
            Status renameResourceStatus = new Status("Rename resource status");
            super.moduleContainer.renameFeature(fromFeatureName, toFeatureName, renameResourceStatus);
            addStatusMessagesToCompilerMessageLogger(renameResourceStatus, Status.Severity.ERROR, logger);
        }
        
        /**
         * Adds each message in status of severity at least minimumSeverity to logger. 
         * @param status Status object to pull messages from
         * @param minimumSeverity Minimum severity of messages to copy
         * @param logger CompilerMessageLogger to copy messages to
         */
        private void addStatusMessagesToCompilerMessageLogger(Status status, Status.Severity minimumSeverity, CompilerMessageLogger logger) {
            Status[] msgs = status.getChildren();
            for (int i = 0, nMsgs = msgs.length; i < nMsgs; ++i) {
                Status.Severity msgSeverity = msgs[i].getSeverity();

                if(msgSeverity.compareTo(minimumSeverity) < 0) {
                    continue;
                }
                
                if(msgSeverity == Status.Severity.ERROR) {
                    logger.logMessage(new CompilerMessage(new MessageKind.Error.DebugMessage(msgs[i].getMessage())));
                
                } else if (msgSeverity == Status.Severity.WARNING) {
                    logger.logMessage(new CompilerMessage(new MessageKind.Warning.DebugMessage(msgs[i].getMessage())));
                
                } else if (msgSeverity == Status.Severity.INFO) {
                    logger.logMessage(new CompilerMessage(new MessageKind.Info.DebugMessage(msgs[i].getMessage())));
                }
            }
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger) {
            
            fireWillUseResource(moduleName);
            ModuleTypeInfo moduleTypeInfo = super.moduleContainer.getModuleTypeInfo(moduleName);

            // No modifications required in modules that can't see the old name (since they
            // won't be able to see the new name either)
            if(!isOldNameVisible(moduleTypeInfo)) {
                return new SourceModifier();
            }
            
            String sourceText = super.readModuleText(moduleName, messageLogger);
            SourceModifier sourceModifier = IdentifierRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, category, messageLogger); 
            fireDoneUsingResource(moduleName);

            if(sourceModifier == null) {
                // Module \"{resource.getModuleName()}\" could not be parsed.
                ModuleSourceDefinition moduleSourceDefinition = super.moduleContainer.getSourceDefinition(moduleName);
                MessageKind message = new MessageKind.Error.ModuleCouldNotBeParsed(moduleName);
                messageLogger.logMessage(new CompilerMessage(message, new CALRefactoringException(moduleSourceDefinition, message.getMessage())));
                return null;
            }

            return sourceModifier;
        }

        /**
         * @param moduleTypeInfo
         * @return True if the entity identified by oldName and category is visible
         *          from moduleTypeInfo's module, or false otherwise.
         */
        private boolean isOldNameVisible(ModuleTypeInfo moduleTypeInfo) {
            if(category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
                return moduleTypeInfo.getVisibleFunction(oldName) != null ||
                       moduleTypeInfo.getVisibleClassMethod(oldName) != null;
                
            } else if(category == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
                return moduleTypeInfo.getVisibleDataConstructor(oldName) != null;
            
            } else if(category == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
                return moduleTypeInfo.getVisibleTypeConstructor(oldName) != null;

            } else if(category == SourceIdentifier.Category.TYPE_CLASS) {
                return moduleTypeInfo.getVisibleTypeClass(oldName) != null;
                
            } else if(category == SourceIdentifier.Category.MODULE_NAME) {
                return isOldModuleVisible(moduleTypeInfo);

            } else if(category == SourceIdentifier.Category.LOCAL_VARIABLE) {
                throw new UnsupportedOperationException();
                
            } else if(category == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION) {
                throw new UnsupportedOperationException();
                
            } else {
                throw new IllegalArgumentException("Unrecognized SourceIdentifier category");
            }
        }
        
        /**
         * @param moduleTypeInfo
         * @return True if oldName's module is visible from moduleTypeInfo's module, or
         *          false otherwise. 
         */
        private boolean isOldModuleVisible(ModuleTypeInfo moduleTypeInfo) {
            
            ModuleName oldModuleName = oldName.getModuleName();
            
            if(moduleTypeInfo.getModuleName().equals(oldModuleName)) {
                return true;
            }
            
            for(int i = 0, nImports = moduleTypeInfo.getNImportedModules(); i < nImports; i++) {
                if(moduleTypeInfo.getNthImportedModule(i).getModuleName().equals(oldModuleName)) {
                    return true;
                }
            }
            
            return false;
        }

        /**
         * Converts the given qualified name to a CALFeatureName using the current category.
         * @param qualifiedName name to convert
         * @param logger CompilerMessageLogger for logging any failures
         * @return CALFeatureName of the qualifiedName (in the current category)
         */
        private CALFeatureName toCALFeatureName(QualifiedName qualifiedName, CompilerMessageLogger logger) {
            
            CALFeatureName cachedFeatureName = featureNameCache.get(qualifiedName);
            if(cachedFeatureName != null) {
                return cachedFeatureName;
            }
            
            if (category == SourceIdentifier.Category.MODULE_NAME) {
                cachedFeatureName = CALFeatureName.getModuleFeatureName(qualifiedName.getModuleName());
            
            } else if (category == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
                cachedFeatureName = CALFeatureName.getDataConstructorFeatureName(qualifiedName);
                
            } else if (category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
                // We need to determine exactly what kind of gem this is..
                
                // Note that we are looking up the oldName specifically and not the parameter that
                // was passed in.  That's because the oldName will definitely exist the first time
                // we call this function, whereas the newName will not.  We're relying here on the
                // fact that both the old and new names should have the same feature category.
                ModuleName oldModuleName = oldName.getModuleName();
                String oldUnqualifiedName = oldName.getUnqualifiedName();
                
                ModuleTypeInfo typeInfo = super.moduleContainer.getModuleTypeInfo(oldModuleName);
                if(typeInfo == null) {
                    logger.logMessage(new CompilerMessage(new MessageKind.Error.ModuleCouldNotBeRead(oldModuleName)));
                    return null;
                }
                
                if (typeInfo.getClassMethod(oldUnqualifiedName) != null) {
                    // This is a class method
                    cachedFeatureName = CALFeatureName.getClassMethodFeatureName(qualifiedName);
                } else {
                    // This is a function
                    cachedFeatureName = CALFeatureName.getFunctionFeatureName(qualifiedName);
                }
                
            } else if (category == SourceIdentifier.Category.TYPE_CLASS) {
                cachedFeatureName = CALFeatureName.getTypeClassFeatureName(qualifiedName);
            } else if (category == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
                cachedFeatureName = CALFeatureName.getTypeConstructorFeatureName(qualifiedName);
            } else {
                throw new IllegalStateException("Invalid category");
            }
            
            featureNameCache.put(qualifiedName, cachedFeatureName);
            return cachedFeatureName;
        }
    }
    
    /**
     * Represents the refactoring of renaming a locally-bound name (let-bound functions and local
     * pattern match variables, case-bound patterns, lambda-bound parameters, and parameters of
     * top-level and local functions).
     * 
     * @see LocalNameRenamer
     * 
     * @author Joseph Wong
     */
    public static final class RenameLocalName extends Refactorer {
    
        /**
         * The module of the target to be renamed.
         */
        private final ModuleName targetModule;
        /**
         * The target to be renamed.
         */
        private final IdentifierInfo.Local oldName;
        /**
         * The new name for the target.
         */
        private final String newName;
    
        /**
         * Constructs an instance of this refactorer.
         * @param moduleContainer the module container.
         * @param targetModule the module of the target to be renamed.
         * @param oldName the target to be renamed.
         * @param newName the new name for the target.
         */
        public RenameLocalName(final ModuleContainer moduleContainer, final ModuleName targetModule, final IdentifierInfo.Local oldName, final String newName) {
            super(moduleContainer);
            if (targetModule == null || oldName == null || newName == null) {
                throw new NullPointerException();
            }
            this.targetModule = targetModule;
            this.oldName = oldName;
            this.newName = newName;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        SourceModifier sourceModificationsForModule(final ModuleName moduleName, final CompilerMessageLogger messageLogger) {
            if (moduleName.equals(targetModule)) {
                final ModuleTypeInfo moduleTypeInfo = super.moduleContainer.getModuleTypeInfo(moduleName);
                final String sourceText = super.readModuleText(moduleName, messageLogger);
                
                if (moduleTypeInfo != null && sourceText != null) {
                    return LocalNameRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, messageLogger);
                }
            }
            return null;
        }
    }
    
    /**
     * Represents the refactoring of renaming a type variable.
     *
     * @see TypeVariableRenamer
     *
     * @author Joseph Wong
     */
    public static final class RenameTypeVariable extends Refactorer {
    
        /**
         * The module of the target to be renamed.
         */
        private final ModuleName targetModule;
        /**
         * The target to be renamed.
         */
        private final IdentifierInfo.TypeVariable oldName;
        /**
         * The new name for the target.
         */
        private final String newName;
    
        /**
         * Constructs an instance of this refactorer.
         * @param moduleContainer the module container.
         * @param targetModule the module of the target to be renamed.
         * @param oldName the target to be renamed.
         * @param newName the new name for the target.
         */
        public RenameTypeVariable(final ModuleContainer moduleContainer, final ModuleName targetModule, final IdentifierInfo.TypeVariable oldName, final String newName) {
            super(moduleContainer);
            if (targetModule == null || oldName == null || newName == null) {
                throw new NullPointerException();
            }
            this.targetModule = targetModule;
            this.oldName = oldName;
            this.newName = newName;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        SourceModifier sourceModificationsForModule(final ModuleName moduleName, final CompilerMessageLogger messageLogger) {
            if (moduleName.equals(targetModule)) {
                final ModuleTypeInfo moduleTypeInfo = super.moduleContainer.getModuleTypeInfo(moduleName);
                final String sourceText = super.readModuleText(moduleName, messageLogger);
                
                if (moduleTypeInfo != null && sourceText != null) {
                    return TypeVariableRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, messageLogger);
                }
            }
            return null;
        }
    }
    
    /**
     * Represents the refactoring of renaming a data constructor field.
     * 
     * @see DataConsFieldNameRenamer
     *
     * @author Joseph Wong
     */
    public static final class RenameDataConsFieldName extends Refactorer {
        
        /**
         * The target to be renamed.
         */
        private final IdentifierInfo.DataConsFieldName oldName;
        
        /**
         * The new name for the target.
         */
        private final FieldName newName;
        
        /**
         * A set of all affected data constructors related to the target via multi-data-constructor
         * case alternatives. The value will be set in {@link #calculateModifications}.
         */
        private Set<IdentifierInfo.TopLevel.DataCons> affectedDataConsSet;

        /**
         * A set of the modules in which the data constructor field is visible.
         * The value will be set in {@link #calculateModifications}.
         */
        private Set<ModuleName> relevantModules;
        
        /**
         * The callback to confirm the user's choice.
         */
        private final UserChoiceConfirmer confirmer;
        
        /**
         * A callback interface for confirming the user's choice during the renaming process.
         *
         * @author Joseph Wong
         */
        public static interface UserChoiceConfirmer {
            /**
             * Determines whether to proceed with renaming the named field in multiple data constructors
             * related to the target via multi-data-constructor case alternatives.
             * @param oldName the old field name.
             * @param dataConsSet the affected data constructors.
             * @return the user's choice: true to proceed; false to cancel.
             */
            public boolean shouldRenameMultipleDataConsField(FieldName oldName, SortedSet<QualifiedName> dataConsSet);
            
            /**
             * Notifies the user that there is a name collision (the new name already occurs in one of
             * the affected data constructors).
             * @param newName the colliding name.
             */
            public void notifyCollisionOfNewName(FieldName newName);
        }
        
        /**
         * Constructs an instance of this refactorer.
         * @param moduleContainer the module container.
         * @param oldName the target to be renamed.
         * @param newName the new name for the target.
         * @param confirmer the callback to confirm the user's choice.
         */
        public RenameDataConsFieldName(final ModuleContainer moduleContainer, final IdentifierInfo.DataConsFieldName oldName, final FieldName newName, final UserChoiceConfirmer confirmer) {
            super(moduleContainer);
            if (oldName == null || newName == null || confirmer == null) {
                throw new NullPointerException();
            }
            this.oldName = oldName;
            this.newName = newName;
            this.confirmer = confirmer;
        }

        /**
         * Determines the relevant modules and affected data constructors.
         * @param messageLogger the message logger.
         * @return the pair of results.
         */
        private Pair<Set<ModuleName>, Set<IdentifierInfo.TopLevel.DataCons>> getRelevantModulesAndAffectedDataConsSet(final CompilerMessageLogger messageLogger) {
            final Set<ModuleName> relevantModules = new HashSet<ModuleName>();
            final Set<IdentifierInfo.TopLevel.DataCons> affectedDataConsSet = new HashSet<IdentifierInfo.TopLevel.DataCons>();
            
            final ModuleName definingModule = oldName.getFirstAssociatedDataConstructor().getResolvedName().getModuleName();
            
            ////
            /// Optimization: if the type cons defining the associated data constructor(s) has only private data constructors
            /// then we only need to process the defining module (because there could not be any references to any of the
            /// associated data constructor(s) outside the module).
            //
            
            boolean definingTypeConsHasNonPrivateDataCons = false;
            
            final ModuleTypeInfo definingModuleTypeInfo = super.moduleContainer.getModuleTypeInfo(definingModule);
            if (definingModuleTypeInfo != null) {
                final DataConstructor firstDataCons =
                    definingModuleTypeInfo.getDataConstructor(oldName.getFirstAssociatedDataConstructor().getResolvedName().getUnqualifiedName());

                if (firstDataCons != null) {
                    final TypeConstructor typeCons = firstDataCons.getTypeConstructor();

                    final int nDataCons = typeCons.getNDataConstructors();
                    for (int i = 0; i < nDataCons; i++) {
                        if (!typeCons.getNthDataConstructor(i).getScope().isPrivate()) {
                            definingTypeConsHasNonPrivateDataCons = true;
                            break;
                        }
                    }
                }
            }
            
            final ModuleName[] modulesToCheck;
            if (definingTypeConsHasNonPrivateDataCons) {
                modulesToCheck = super.moduleContainer.getModuleNames();
            } else {
                modulesToCheck = new ModuleName[] {definingModule};
            }
            
            for (final ModuleName moduleName : modulesToCheck) {
                final ModuleTypeInfo moduleTypeInfo = super.moduleContainer.getModuleTypeInfo(moduleName);
                final String sourceText = super.readModuleText(moduleName, messageLogger);
                
                if (moduleTypeInfo != null && sourceText != null) {
                    if (moduleName.equals(definingModule) || moduleTypeInfo.getImportedModule(definingModule) != null) {
                        // this is either the defining module, or imports it so we check it
                        // if the module is a sourceless module, then there is not much we can do with it.
                        if (sourceText.length() == 0) {
                            continue;
                        }

                        final SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(sourceText, messageLogger);
                        if (moduleDefn == null) {
                            continue;
                        }
                        
                        relevantModules.add(moduleName);
                        affectedDataConsSet.addAll(DataConsFieldNameRenamer.getAffectedDataConsSetFromModule(oldName, moduleTypeInfo, moduleDefn));
                    }
                }
            }
            
            return Pair.make(relevantModules, affectedDataConsSet);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public List<ModuleSourceDefinition> calculateModifications(final CompilerMessageLogger messageLogger) {
            Pair<Set<ModuleName>, Set<IdentifierInfo.TopLevel.DataCons>> relevantModulesAndAffectedDataConsSet =
                getRelevantModulesAndAffectedDataConsSet(messageLogger);
            
            relevantModules = relevantModulesAndAffectedDataConsSet.fst();
            affectedDataConsSet = relevantModulesAndAffectedDataConsSet.snd();
            
            final TreeSet<QualifiedName> dataConsNames = new TreeSet<QualifiedName>();
            final Set<FieldName> allSiblingFields = new HashSet<FieldName>();
            
            for (final IdentifierInfo.TopLevel.DataCons dataCons : affectedDataConsSet) {
                
                dataConsNames.add(dataCons.getResolvedName());
                
                final ModuleTypeInfo moduleTypeInfo =
                    super.moduleContainer.getModuleTypeInfo(dataCons.getResolvedName().getModuleName());
                
                if (moduleTypeInfo != null) {
                    final DataConstructor dataConsEntity =
                        moduleTypeInfo.getDataConstructor(dataCons.getResolvedName().getUnqualifiedName());
                    
                    for (int i = 0, n = dataConsEntity.getArity(); i < n; i++) {
                        allSiblingFields.add(dataConsEntity.getNthFieldName(i));
                    }
                }
            }
            
            final boolean confirmedAffectedDataConsSet;
            
            if (affectedDataConsSet.size() > 1) {
                confirmedAffectedDataConsSet = confirmer.shouldRenameMultipleDataConsField(oldName.getFieldName(), dataConsNames);
            } else {
                confirmedAffectedDataConsSet = true;
            }
            
            final boolean proceed;
            if (confirmedAffectedDataConsSet) {
                if (allSiblingFields.contains(newName)) {
                    confirmer.notifyCollisionOfNewName(newName);
                    proceed = false;
                } else {
                    proceed = true;
                }
            } else {
                proceed = false;
            }
            
            if (!proceed) {
                relevantModules = Collections.emptySet();
            }
            
            return super.calculateModifications(messageLogger);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        SourceModifier sourceModificationsForModule(final ModuleName moduleName, CompilerMessageLogger messageLogger) {
            if (relevantModules.contains(moduleName)) {
                final ModuleTypeInfo moduleTypeInfo = super.moduleContainer.getModuleTypeInfo(moduleName);
                final String sourceText = super.readModuleText(moduleName, messageLogger);

                if (moduleTypeInfo != null && sourceText != null) {
                    return DataConsFieldNameRenamer.getSourceModifier(moduleTypeInfo, sourceText, affectedDataConsSet, oldName.getFieldName(), newName, messageLogger);
                }
            }

            return null;
        }
    }

    private Refactorer(ModuleContainer moduleContainer) {
        if(moduleContainer == null) {
            throw new NullPointerException();
        }
        this.moduleContainer = moduleContainer;
        changesCalculated = false;
    }

    /**
     * Calculates the modifications that will be required to perform this refactoring.
     * Problems encountered during calculation will be logged.
     * @param messageLogger CompilerMessageLogger to log failures to.  Cannot be null.
     * @return List of WorkspaceResources that will be modified by this refactoring
     */
    public List<ModuleSourceDefinition> calculateModifications(CompilerMessageLogger messageLogger) {
        if (messageLogger == null) {
            throw new NullPointerException("Argument messageLogger cannot be null.");
        }
        changesCalculated = true;
        
        List<ModuleSourceDefinition> affectedResources = new ArrayList<ModuleSourceDefinition>();
        
        for(int i = 0, nMetaModules = moduleContainer.getNModules(); i < nMetaModules; i++) {
            ModuleName moduleName = moduleContainer.getNthModuleTypeInfo(i).getModuleName();
            SourceModifier sourceModifier = sourceModificationsForModule(moduleName, messageLogger);
            
            // If this module requires source modifications, then it is affected
            if(sourceModifier != null && sourceModifier.getNSourceModifications() > 0) {
                moduleSourceModificationsModifiers.put(moduleName, sourceModifier);
                affectedResources.add(moduleContainer.getSourceDefinition(moduleName));
                
                // Check that the source resource is writeable
                if(!moduleContainer.getSourceManager(moduleName).isWriteable(moduleName)) {
                    MessageKind message = new MessageKind.Error.ModuleNotWriteable(moduleName);
                    messageLogger.logMessage(new CompilerMessage(message));
                }
            }
        }
        
        return affectedResources;
    }
    
    /**
     * @param moduleName Name of module to check for modifications
     * @param messageLogger CompilerMessageLogger to log failures to
     * @return A SourceModifier containing modifications for the module, or null if there are no modifications for this module.
     */
    abstract SourceModifier sourceModificationsForModule(ModuleName moduleName, CompilerMessageLogger messageLogger);

    /**
     * Apply the refactoring.
     * calculateModifications must be called before this method.  An IllegalStateException will be signalled
     * if this method is called before calculateModifications.
     * @param messageLogger CompilerMessageLogger to log failures to.  Cannot be null.
     */
    public void apply(CompilerMessageLogger messageLogger) {
        if (!changesCalculated) {
            throw new IllegalStateException("calculateModifications must be called before apply");
        }
        if (messageLogger == null) {
            throw new NullPointerException("Argument messageLogger cannot be null.");
        }
        
        for (final Map.Entry<ModuleName, SourceModifier> entry : moduleSourceModificationsModifiers.entrySet()) {
            ModuleName moduleName = entry.getKey();
            SourceModifier sourceModifier = entry.getValue();
            
            fireWillUseResource(moduleName);

            /**
             * If the module can be updated incrementally then do the incremental update.
             */
            final ModuleContainer.ISourceManager sourceManager = moduleContainer.getSourceManager(moduleName);
            if (sourceManager instanceof ModuleContainer.ISourceManager2
                    && 
                    ((ISourceManager2) sourceManager).canUpdateIncrementally(moduleName)
                    ){
                ModuleContainer.ISourceManager2 sm2 = (ISourceManager2) sourceManager;
                sm2.startUpdate(moduleName);
                try{
                    sourceModifier.apply(moduleName, sm2);
                }
                finally{
                    sm2.endUpdate(moduleName);
                }
            }
            else{
                final String oldText = readModuleText(moduleName, messageLogger);
                if(oldText == null) {
                    continue;
                }

                String newText = sourceModifier.apply(oldText);
                saveModuleText(moduleName, newText, messageLogger);
            }
            
            fireDoneUsingResource(moduleName);
        }
    }
    
    /**
     * Undo the previously-applied refactoring.
     * calculateModifications must be called before this method.  An IllegalStateException will be signalled
     * if this method is called before calculateModifications.
     * @param messageLogger CompilerMessageLogger to log failures to.  Cannot be null.
     */
    public void undo(CompilerMessageLogger messageLogger) {
        if (!changesCalculated) {
            throw new IllegalStateException("calculateModifications (and then apply) must be called before undo");
        }
        if (messageLogger == null) {
            throw new NullPointerException("Argument messageLogger cannot be null.");
        }
        
        for (final Map.Entry<ModuleName, SourceModifier> entry : moduleSourceModificationsModifiers.entrySet()) {
            ModuleName moduleName = entry.getKey();
            SourceModifier sourceModifier = entry.getValue();
            
            fireWillUseResource(moduleName);
            
            String oldText = readModuleText(moduleName, messageLogger);
            if(oldText == null) {
                continue;
            }
            
            String newText = sourceModifier.undo(oldText);
            saveModuleText(moduleName, newText, messageLogger);
            
            fireDoneUsingResource(moduleName);
        }
    }
    
    /**
     * Set status listener to use
     * @param statusListener
     */
    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    /** 
     * Call willUseResource on any status listeners with the ModuleSourceDefinition for the
     * module named moduleName.
     */
    void fireWillUseResource(ModuleName moduleName) {
        if(statusListener == null) {
            return;
        }
        
        ModuleSourceDefinition moduleSourceDefinition = moduleContainer.getSourceDefinition(moduleName);
        statusListener.willUseResource(moduleSourceDefinition);
    }
    
    /** 
     * Call doneUsingResource on any status listeners with the ModuleSourceDefinition for the
     * module named moduleName.
     */
    void fireDoneUsingResource(ModuleName moduleName) {
        if(statusListener == null) {
            return;
        }
        
        ModuleSourceDefinition moduleSourceDefinition = moduleContainer.getSourceDefinition(moduleName);
        statusListener.doneUsingResource(moduleSourceDefinition);
    }
    
    /**
     * Read the text of the specified module.
     * @param moduleName Name of the module to read from the workspace
     * @param messageLogger CompilerMessageLogger to log failures to
     * @return containing the source text of the module, or null on error
     */
    private String readModuleText(ModuleName moduleName, CompilerMessageLogger messageLogger) {
        final ModuleContainer.ISourceManager sourceManager = moduleContainer.getSourceManager(moduleName);
        final String sourceText = sourceManager.getSource(moduleName, messageLogger);
        if(sourceText == null) {            
            return moduleContainer.getModuleSource(moduleName);           
        }
        return sourceText;
    }

    /**
     * Save moduleText to the workspace to be the new source text for the specified module
     * @param moduleName Name of the module to save
     * @param moduleText String containing the new source text for the module
     * @param messageLogger CompilerMessageLogger to log failures to
     */
    private void saveModuleText(ModuleName moduleName, String moduleText, CompilerMessageLogger messageLogger) {
        ModuleContainer.ISourceManager sourceManager = moduleContainer.getSourceManager(moduleName);

        Status saveStatus = new Status("Saving refactored module text");
        sourceManager.saveSource(moduleName, moduleText, saveStatus);
        if(!saveStatus.isOK()) {
            MessageKind messageKind = new MessageKind.Error.ModuleNotWriteable(moduleName);
            messageLogger.logMessage(new CompilerMessage(messageKind));
        }
    }
}
