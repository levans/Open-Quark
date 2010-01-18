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
 * BindingTrackingSourceModelTraverser.java
 * Creation date: (Feb 14, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.openquark.cal.compiler.SourceModel.ArgBindings;
import org.openquark.cal.compiler.SourceModel.FieldPattern;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.Pattern;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.compiler.SourceModel.Expr.Lambda;
import org.openquark.cal.compiler.SourceModel.Expr.Let;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackDataCons;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackListCons;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackRecord;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackTuple;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Algebraic;
import org.openquark.cal.compiler.SourceModel.Import.UsingItem;
import org.openquark.cal.compiler.SourceModel.LocalDefn.Function.Definition;
import org.openquark.cal.util.ArrayStack;


/**
 * An implementation of SourceModelTraverser that tracks the current local definitions that
 * are in scope.  The current top-level function is considered to be in scope.
 * 
 * @param <R> the return type. If the return value is not used, specify {@link Void}.
 * 
 * @author James Wright
 */
class BindingTrackingSourceModelTraverser<R> extends SourceModelTraverser<Object, R> { 

    /**
     * Name of the module currently being processed.
     * This will be set when a ModuleDefn is visited.
     */
    private ModuleName moduleName;
    
    /** 
     * ArrayStack of Maps (String -> SourceElement) that map from bindings
     * currently in scope to the corresponding source element.
     */
    private final ArrayStack<Map<String, SourceElement>> currentBindings;

    /**
     * ArrayStacks of Maps (String -> LocalFunctionIdentifier) that map from
     * bindings of local function names currently in scope to the corresponding
     * LocalFunctionIdentifier.
     */
    private final ArrayStack<Map<String, LocalFunctionIdentifier>> currentLocalFunctionIdentifierBindings;
    
    /** Used for generating LocalFunctionIdentifiers when we encounter local functions. */
    private final LocalFunctionIdentifierGenerator localFunctionIdentifierGenerator;
    
    /** Map (String -> QualifiedName) from function names declared in using clauses to the QualifiedName of the entity that they refer to */
    private final Map<String, QualifiedName> usingFunctionNames = new HashMap<String, QualifiedName>();
    
    /** Map (String -> QualifiedName) from datacons names declared in using clauses to the QualifiedName of the entity that they refer to */
    private final Map<String, QualifiedName> usingDataconsNames = new HashMap<String, QualifiedName>();
    
    /** Map (String -> QualifiedName) from typecons names declared in using clauses to the QualifiedName of the entity that they refer to */
    private final Map<String, QualifiedName> usingTypeconsNames = new HashMap<String, QualifiedName>();
    
    /** Map (String -> QualifiedName) from type class names declared in using clauses to the QualifiedName of the entity that they refer to */
    private final Map<String, QualifiedName> usingTypeClassNames = new HashMap<String, QualifiedName>();
    
    BindingTrackingSourceModelTraverser() {
        moduleName = null;
        localFunctionIdentifierGenerator = new LocalFunctionIdentifierGenerator();
        currentBindings = ArrayStack.make();
        currentLocalFunctionIdentifierBindings = ArrayStack.make();
    }

    /** {@inheritDoc} */
    @Override
    public R visit_ModuleDefn(ModuleDefn defn, Object arg) {
        // Save modulename for clients
        moduleName = SourceModel.Name.Module.toModuleName(defn.getModuleName());
        return super.visit_ModuleDefn(defn, arg);
    }
    
    /** 
     * {@inheritDoc}
     * This implementation of visitImport passes the name of the imported module
     * to each of its child UsingItems.
     * 
     */
    @Override
    public R visit_Import(Import importStmt, Object arg) {
        
        ModuleName importedModuleName = SourceModel.Name.Module.toModuleName(importStmt.getImportedModuleName());
        UsingItem[] usingItems = importStmt.getUsingItems();

        for (final UsingItem usingItem : usingItems) {
            // We pass the name of the imported module to using clause visitors so that
            // they can fill the using maps; we're overriding the default traversal method
            // here, so we don't call the superclass method.
            usingItem.accept(this, importedModuleName);
        }
    
        return null;
    }
    
    /** {@inheritDoc} */
    @Override
    public R visit_Import_UsingItem_Function(UsingItem.Function usingItemFunction, Object arg) {

        if(arg != null && arg instanceof ModuleName) {
            // Fill up the using maps for name resolution
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemFunction.getUsingNames();

            for (final String usingName : usingNames) {
                usingFunctionNames.put(usingName, QualifiedName.make(importedModuleName, usingName));
            }
        }

        return super.visit_Import_UsingItem_Function(usingItemFunction, arg);
    }

    /** {@inheritDoc} */
    @Override
    public R visit_Import_UsingItem_DataConstructor(UsingItem.DataConstructor usingItemDataConstructor, Object arg) {

        if(arg != null && arg instanceof ModuleName) {
            // Fill up the using maps for name resolution
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemDataConstructor.getUsingNames();

            for (final String usingName : usingNames) {
                usingDataconsNames.put(usingName, QualifiedName.make(importedModuleName, usingName));
            }
        }

        return super.visit_Import_UsingItem_DataConstructor(usingItemDataConstructor, arg);
    }

    /** {@inheritDoc} */
    @Override
    public R visit_Import_UsingItem_TypeConstructor(UsingItem.TypeConstructor usingItemTypeConstructor, Object arg) {

        if(arg != null && arg instanceof ModuleName) {
            // Fill up the using maps for name resolution
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemTypeConstructor.getUsingNames();

            for (final String usingName : usingNames) {
                usingTypeconsNames.put(usingName, QualifiedName.make(importedModuleName, usingName));
            }
        }

        return super.visit_Import_UsingItem_TypeConstructor(usingItemTypeConstructor, arg);
    }

    /** {@inheritDoc} */
    @Override
    public R visit_Import_UsingItem_TypeClass(UsingItem.TypeClass usingItemTypeClass, Object arg) {

        if(arg != null && arg instanceof ModuleName) {
            // Fill up the using maps for name resolution
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemTypeClass.getUsingNames();

            for (final String usingName : usingNames) {
                usingTypeClassNames.put(usingName, QualifiedName.make(importedModuleName, usingName));
            }
        }

        return super.visit_Import_UsingItem_TypeClass(usingItemTypeClass, arg);
    }
            
    /** {@inheritDoc} */
    @Override
    public R visit_FunctionDefn_Algebraic(Algebraic algebraic, Object arg) {

        enterScope();

        localFunctionIdentifierGenerator.reset(algebraic.getName());

        for(int i = 0; i < algebraic.getNParameters(); i++) {
            SourceModel.Parameter param = algebraic.getNthParameter(i);
            addRegularBinding(param.getName(), param);
        }
        
        R ret = super.visit_FunctionDefn_Algebraic(algebraic, arg);
        
        localFunctionIdentifierGenerator.reset(null);
        
        leaveScope();
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public R visit_Expr_Lambda(Lambda lambda, Object arg) {

        enterScope();
        
        for(int i = 0; i < lambda.getNParameters(); i++) {
            SourceModel.Parameter param = lambda.getNthParameter(i);
            addRegularBinding(param.getName(), param);
        }

        R ret = super.visit_Expr_Lambda(lambda, arg);
        leaveScope();
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public R visit_Expr_Let(Let let, Object arg) {
        
        enterScope();
        
        // Let expressions are mutually recursive, so we want to bind the
        // function names before we enter the new scope associated with each local function.
        
        /**
         * Handles the adding of bindings for local definitions (both local functions and local pattern match declarations).
         * This is done by walking the local definitions and adding a binding for each local function / pattern-bound variable
         * encountered. The synthetic local function generated by the compiler for desugaring a local pattern match declaration
         * is also taken into account.
         * 
         * @author Joseph Wong
         */
        class LocallyDefinedNamesCollector extends IdentifierResolver.LocalBindingsProcessor<LinkedHashSet<String>, R> {

            /**
             * {@inheritDoc}
             */
            @Override
            void processLocalDefinitionBinding(final String name, final SourceModel.SourceElement localDefinition, final LinkedHashSet<String> arg) {
                addLocalDefinitionBinding(name, localDefinition);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            void additionallyProcessPatternVar(final Pattern.Var var, final LinkedHashSet<String> patternVarNames) {
                patternVarNames.add(var.getName());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            void additionallyProcessPunnedTextualRecordFieldPattern(final FieldName.Textual fieldName, final Name.Field fieldNameElement, final LinkedHashSet<String> patternVarNames) {
                patternVarNames.add(fieldName.getCalSourceForm());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            void additionallyProcessPunnedTextualDataConsFieldPattern(final FieldName.Textual fieldName, final Name.Field fieldNameElement, final Name.DataCons dataConsName, final LinkedHashSet<String> patternVarNames) {
                patternVarNames.add(fieldName.getCalSourceForm());
            }
            
            /**
             * Adds an additional binding for the synthetic local function which is generated by the compiler to host the defining
             * expression of a local pattern match declaration. This is done to keep the local function identifier generator in
             * sync with what the compiler would do.
             * 
             * @param patternMatchDecl the pattern match declaration.
             * @param patternVarNames the LinkedHashSet of the pattern variable names, in source order.
             */
            private void addBindingForSyntheticLocalDefinition(final LocalDefn.PatternMatch patternMatchDecl, final LinkedHashSet<String> patternVarNames) {
                addLocalDefinitionBinding(FreeVariableFinder.makeTempVarNameForDesugaredLocalPatternMatchDecl(patternVarNames), patternMatchDecl);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public R visit_LocalDefn_PatternMatch_UnpackDataCons(final LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, final LinkedHashSet<String> arg) {
                // visit only the patterns
                final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                super.visit_LocalDefn_PatternMatch_UnpackDataCons(unpackDataCons, patternVarNames);
                // add the synthetic definition last
                addBindingForSyntheticLocalDefinition(unpackDataCons, patternVarNames);
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public R visit_LocalDefn_PatternMatch_UnpackListCons(final LocalDefn.PatternMatch.UnpackListCons unpackListCons, final LinkedHashSet<String> arg) {
                // visit only the patterns
                final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                super.visit_LocalDefn_PatternMatch_UnpackListCons(unpackListCons, patternVarNames);
                // add the synthetic definition last
                addBindingForSyntheticLocalDefinition(unpackListCons, patternVarNames);
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public R visit_LocalDefn_PatternMatch_UnpackRecord(final LocalDefn.PatternMatch.UnpackRecord unpackRecord, final LinkedHashSet<String> arg) {
                // visit only the field patterns (and not the base record pattern - since we do not support them in local pattern match decl)
                final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                super.visit_LocalDefn_PatternMatch_UnpackRecord(unpackRecord, patternVarNames);
                // add the synthetic definition last
                addBindingForSyntheticLocalDefinition(unpackRecord, patternVarNames);
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public R visit_LocalDefn_PatternMatch_UnpackTuple(final LocalDefn.PatternMatch.UnpackTuple unpackTuple, final LinkedHashSet<String> arg) {
                // visit only the patterns
                final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                super.visit_LocalDefn_PatternMatch_UnpackTuple(unpackTuple, patternVarNames);
                // add the synthetic definition last
                addBindingForSyntheticLocalDefinition(unpackTuple, patternVarNames);
                return null;
            }
        }
        
        // Use the LocallyDefinedNamesCollector to visit the let definitions
        final LocallyDefinedNamesCollector locallyDefinedNamesCollector = new LocallyDefinedNamesCollector();
        final int nLocalFunctions = let.getNLocalDefinitions();
        for (int i = 0; i < nLocalFunctions; i++) {
            let.getNthLocalDefinition(i).accept(locallyDefinedNamesCollector, null);
        }

        // Now call the superclass implementation to walk through the let expression with the right name bindings
        R ret = super.visit_Expr_Let(let, arg);
        leaveScope();
        return ret;
    }
         
    /** {@inheritDoc} */
    @Override
    public R visit_LocalDefn_Function_Definition(Definition function, Object arg) {

        enterScope();
        
        for(int i = 0; i < function.getNParameters(); i++) {
            SourceModel.Parameter param = function.getNthParameter(i); 
            addRegularBinding(param.getName(), param);
        }
        
        R ret = super.visit_LocalDefn_Function_Definition(function, arg);
        
        leaveScope();            
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public R visit_Expr_Case_Alt_UnpackDataCons(UnpackDataCons cons, Object arg) {
        enterScope();
        handleArgBindings(cons.getArgBindings());
        
        R ret = super.visit_Expr_Case_Alt_UnpackDataCons(cons, arg);
        leaveScope();
        return ret;
    }
    
    /**
     * Adds all of the bindings in argBindings to the current scope.
     *    
     * visitCaseExprUnpackDataConsAlt and visitCaseExprUnpackDataConsGroupAlt both
     * do exactly the same (rather involved) processing on their arg bindings.  This
     * common processing is factored into handleArgBindings.
     * 
     * @param argBindings ArgBindings the bindings to process 
     */
    private void handleArgBindings(ArgBindings argBindings) {
        
        if(argBindings instanceof ArgBindings.Matching) {
            
            ArgBindings.Matching matchingArgBindings = (ArgBindings.Matching)argBindings;
            
            for (int i = 0; i < matchingArgBindings.getNFieldPatterns(); i++) {
                FieldPattern fieldPattern = matchingArgBindings.getNthFieldPattern(i);
                Pattern pattern = fieldPattern.getPattern();
                
                if (pattern == null) {
                    // punning.
                    
                    // Textual field names become Vars of the same name.
                    // Ordinal field names become wildcards ("_").
                    FieldName fieldName = fieldPattern.getFieldName().getName();
                    if (fieldName instanceof FieldName.Textual) {
                        pattern = Pattern.Var.make(fieldName.getCalSourceForm());
                    }
                }
                
                if (pattern instanceof Pattern.Var) {
                    Pattern.Var patternVar = (Pattern.Var)pattern;
                    addRegularBinding(patternVar.getName(), patternVar);
                }
            }

        } else if (argBindings instanceof ArgBindings.Positional) {
            
            ArgBindings.Positional positionalArgBindings = (ArgBindings.Positional)argBindings;
            
            for (int i = 0; i < positionalArgBindings.getNPatterns(); i++) {
                Pattern pattern = positionalArgBindings.getNthPattern(i);
                if (pattern instanceof Pattern.Var) {
                    Pattern.Var patternVar = (Pattern.Var)pattern;
                    addRegularBinding(patternVar.getName(), patternVar);
                }
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public R visit_Expr_Case_Alt_UnpackListCons(UnpackListCons cons, Object arg) {

        enterScope();
        
        if(cons.getHeadPattern() instanceof Pattern.Var) {
            Pattern.Var patternVar = (Pattern.Var)cons.getHeadPattern();
            addRegularBinding(patternVar.getName(), patternVar);
        }
        
        if(cons.getTailPattern() instanceof Pattern.Var) {
            Pattern.Var patternVar = (Pattern.Var)cons.getTailPattern();
            addRegularBinding(patternVar.getName(), patternVar);
        }
        
        R ret = super.visit_Expr_Case_Alt_UnpackListCons(cons, arg);
        leaveScope();
        return ret;
    }
    
    /** {@inheritDoc} */
    @Override
    public R visit_Expr_Case_Alt_UnpackRecord(UnpackRecord record, Object arg) {

        enterScope();
        
        for(int i = 0; i < record.getNFieldPatterns(); i++) {
            FieldPattern pattern = record.getNthFieldPattern(i);
            
            if(pattern.getPattern() != null && 
               pattern.getPattern() instanceof Pattern.Var) {
                Pattern.Var patternVar = (Pattern.Var)pattern.getPattern();
                addRegularBinding(patternVar.getName(), patternVar);

            } else if (pattern.getPattern() == null) {
                addRegularBinding(pattern.getFieldName().getName().getCalSourceForm(), pattern);
            }
        }
        
        if(record.getBaseRecordPattern() != null) {
            if(record.getBaseRecordPattern() instanceof Pattern.Var) {
                Pattern.Var patternVar = (Pattern.Var)record.getBaseRecordPattern();
                addRegularBinding(patternVar.getName(), patternVar);
            }
        }
        
        R ret = super.visit_Expr_Case_Alt_UnpackRecord(record, arg);
        leaveScope();
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public R visit_Expr_Case_Alt_UnpackTuple(UnpackTuple tuple, Object arg) {

        enterScope();
        
        for(int i = 0; i < tuple.getNPatterns(); i++) {
            Pattern pattern = tuple.getNthPattern(i);
            if(pattern instanceof Pattern.Var) {
                Pattern.Var patternVar = (Pattern.Var)pattern;
                addRegularBinding(patternVar.getName(), patternVar);
            }
        }
        
        R ret = super.visit_Expr_Case_Alt_UnpackTuple(tuple, arg);
        leaveScope();
        return ret;
    }

    /**
     * Adds a binding to the current innermost scope
     * @param name Name of the binding to add
     * @param sourceElement SourceElement that name should be bound to
     */
    private void addRegularBinding(String name, SourceModel.SourceElement sourceElement) {
        Map<String, SourceElement> currentScope = currentBindings.peek();
        currentScope.put(name, sourceElement);
    }
    
    /**
     * Adds a binding to the current innermost scope and records the unique identifier for
     * a local definition (a function or a pattern variable in a pattern match declaration).
     * @param name String name of the local definition to add a binding for
     * @param localDefinition SourceModel for the local function
     */
    private void addLocalDefinitionBinding(String name, SourceModel.SourceElement localDefinition) {
        addRegularBinding(name, localDefinition);

        Map<String, LocalFunctionIdentifier> currentIdentifierScope = currentLocalFunctionIdentifierBindings.peek();

        // Don't try to track local function identifiers without a module name and toplevel function
        if(moduleName != null && getCurrentFunction() != null) {
            LocalFunctionIdentifier localFunctionIdentifier = localFunctionIdentifierGenerator.generateLocalFunctionIdentifier(moduleName, name);
            currentIdentifierScope.put(name, localFunctionIdentifier);
        }
    }
    
    /**
     * Adds a new scope to the current bindings.
     */
    private void enterScope() {
        currentBindings.push(new HashMap<String, SourceElement>());
        currentLocalFunctionIdentifierBindings.push(new HashMap<String, LocalFunctionIdentifier>());
    }
    
    /**
     * Removes the current scope from the bindings.
     */
    private void leaveScope() {
        currentBindings.pop();
        currentLocalFunctionIdentifierBindings.pop();
    }
    
    /** @return The name of the currently in-scope function, if any */
    String getCurrentFunction() {
        return localFunctionIdentifierGenerator.getCurrentFunction();
    }
    
    /** @return The name of the module being processed */
    ModuleName getModuleName() {
        return moduleName;
    }
    
    /**
     * @param name Name to fetch the qualifiedName for 
     * @param moduleNameResolver the module name resolver to use for resolving module names.
     * @return QualifiedName of the top-level entity that name refers to, or null if name does not refer to
     *          a top-level entity.
     */
    QualifiedName getQualifiedName(Name.Qualifiable name, ModuleNameResolver moduleNameResolver) {

        String unqualifiedName = name.getUnqualifiedName();
        if(name.getModuleName() != null) {
            ModuleNameResolver.ResolutionResult resolution = moduleNameResolver.resolve(SourceModel.Name.Module.toModuleName(name.getModuleName()));
            ModuleName resolvedModuleName = resolution.getResolvedModuleName();
            return QualifiedName.make(resolvedModuleName, name.getUnqualifiedName());
        }
        
        if(isBound(unqualifiedName)) {
            return null;
        }
        
        if(name instanceof Name.Function && usingFunctionNames.containsKey(unqualifiedName)) {
            return usingFunctionNames.get(unqualifiedName);
        }
        
        if(name instanceof Name.DataCons && usingDataconsNames.containsKey(unqualifiedName)) {
            return usingDataconsNames.get(unqualifiedName);
        }
        
        if(name instanceof Name.TypeCons && usingTypeconsNames.containsKey(unqualifiedName)) {
            return usingTypeconsNames.get(unqualifiedName);
        }
        
        if(name instanceof Name.TypeClass && usingTypeClassNames.containsKey(unqualifiedName)) {
            return usingTypeClassNames.get(unqualifiedName);
        }
        
        if(name instanceof Name.WithoutContextCons) {
            if(usingDataconsNames.containsKey(unqualifiedName)) {
                return usingDataconsNames.get(unqualifiedName);
            }
            
            if(usingTypeconsNames.containsKey(unqualifiedName)) {
                return usingTypeconsNames.get(unqualifiedName);
            }
            
            if(usingTypeClassNames.containsKey(unqualifiedName)) {
                return usingTypeClassNames.get(unqualifiedName);
            }
        }
        
        return QualifiedName.make(getModuleName(), unqualifiedName);
    }
    
    /** @return A clone of the LocalFunctionIdentifierGenerator */
    LocalFunctionIdentifierGenerator getLocalFunctionNameGenerator() {
        return localFunctionIdentifierGenerator.clone();
    }
    
    /** 
     * @return True if name is currently bound, or false otherwise.
     * @param name String name to check
     */
    boolean isBound(String name) {
        
        for(int i = 0, nBindings = currentBindings.size(); i < nBindings; i++) {
            Map<String, SourceElement> currentScope = currentBindings.peek(i);
            if(currentScope.containsKey(name)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param name Name of binding to retrieve the defining SourceElement for
     * @return The SourceElement that defines the current binding specified by
     *          name, or null if name is not currently bound.
     */
    SourceModel.SourceElement getBoundElement(String name) {
        
        for(int i = 0, nBindings = currentBindings.size(); i < nBindings; i++) {
            Map<String, SourceElement> currentScope = currentBindings.peek(i);
            Object sourceElement = currentScope.get(name);
            if(sourceElement != null) {
                return (SourceModel.SourceElement)sourceElement;
            }
        }
        
        return null;
    }

    /**
     * @param name Name of a local function definition that is current in scope
     * @return The LocalFunctionIdentifier that corresponds to the current binding of name,
     *          or null if name is not currently bound to a local function.
     */
    LocalFunctionIdentifier getBoundLocalFunctionIdentifier(String name) {
        for(int i = 0, nBindings = currentBindings.size(); i < nBindings; i++) {
            Map<String, SourceElement> currentScope = currentBindings.peek(i);
            SourceElement sourceElement = currentScope.get(name);
            
            if(sourceElement == null) {
                continue;
            }
            
            if(!(sourceElement instanceof LocalDefn.Function || sourceElement instanceof Pattern.Var || sourceElement instanceof FieldPattern)) {
                return null;
            }
            
            Map<String, LocalFunctionIdentifier> currentIdentifierScope = currentLocalFunctionIdentifierBindings.peek(i);
            return currentIdentifierScope.get(name);
        }
        
        return null;
    }
}
