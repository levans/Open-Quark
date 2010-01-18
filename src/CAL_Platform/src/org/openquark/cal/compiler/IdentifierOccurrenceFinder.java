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
 * IdentifierOccurrenceFinder.java
 * Created: Sep 11, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.compiler.IdentifierOccurrence.Binding;
import org.openquark.cal.compiler.IdentifierOccurrence.Reference;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable;
import org.openquark.cal.compiler.IdentifierResolver.TypeVariableScope;
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.LocalScope;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.TopLevelScope;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.SourceModel.FieldPattern;
import org.openquark.cal.compiler.SourceModel.Friend;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.Import.UsingItem;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;

/**
 * A visitor class that traverses a source model, gathers up the bindings declared
 * therein, visits each source element with the appropriate symbol table passed in as a
 * visitation argument, and gathers up the references (non-binding occurrences) as well.
 * <p>
 * This is the base class that should be extended if one wants to analyze a
 * source model by processing different kinds of identifier occurrences.
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
public class IdentifierOccurrenceFinder<R> extends IdentifierResolver.Visitor<IdentifierOccurrenceFinder.FinderState, R> {
    
    /**
     * Encapsulates the traversal state of the {@link IdentifierOccurrenceFinder}, to be passed
     * around as the user-specified state in the visitor argument.
     * <p>
     * This class is designed as an immutable class, with update methods which produce new copies
     * with the appropriate fields updated. This means that, for example, one does not need to keep
     * an explicitly stack of state as one traverses up and down the source model.
     * 
     * @author Joseph Wong
     */
    public static final class FinderState {
        
        /**
         * If non-null, this list represents the data constructor names that are associated with
         * any field names encountered during the visitation.
         * 
         * Can be null.
         */
        private final List<Name.DataCons> associatedDataConsNamesForFieldName;
        
        /**
         * If non-null, this represents the name of the type class that is associated with
         * any instance methods encountered during the visitation.
         * 
         * Can be null.
         */
        private final Name.TypeClass associatedTypeClassNameForInstanceMethod;

        /**
         * Constructs an instance of this class.
         * 
         * @param associatedDataConsNamesForFieldName
         *            if non-null, this list represents the data constructor names that are
         *            associated with any field names encountered during the visitation. Can be null.
         * @param associatedTypeClassNameForInstanceMethod
         *            if non-null, this represents the name of the type class that is associated
         *            with any instance methods encountered during the visitation. Can be null.
         */
        private FinderState(final List<Name.DataCons> associatedDataConsNamesForFieldName, final Name.TypeClass associatedTypeClassNameForInstanceMethod) {
            if (associatedDataConsNamesForFieldName == null) {
                this.associatedDataConsNamesForFieldName = null;
            } else {
                this.associatedDataConsNamesForFieldName = Collections.unmodifiableList(new ArrayList<Name.DataCons>(associatedDataConsNamesForFieldName));
            }
            this.associatedTypeClassNameForInstanceMethod = associatedTypeClassNameForInstanceMethod;
        }
        
        /**
         * Factory method for constructing an instance of this class.
         * @return a new instance.
         */
        static FinderState make() {
            return new FinderState(null, null);
        }

        /**
         * @return if non-null, this list represents the data constructor names that are associated
         *         with any field names encountered during the visitation. Can be null.
         */
        List<Name.DataCons> getAssociatedDataConsNamesForFieldName() {
            return associatedDataConsNamesForFieldName;
        }

        /**
         * @return if non-null, this represents the name of the type class that is associated with
         *         any instance methods encountered during the visitation. Can be null.
         */
        Name.TypeClass getAssociatedTypeClassNameForInstanceMethod() {
            return associatedTypeClassNameForInstanceMethod;
        }
        
        /**
         * Constructs a new state object based on this one, but with an updated name representing
         * the data constructor that is associated with any field names encountered during the
         * visitation.
         * 
         * @param associatedDataConsNameForFieldName
         *            data constructor name that is associated with any field names encountered
         *            during the visitation. Can *not* be null.
         * @return a new state object.
         */
        FinderState updateAssociatedDataConsNameForFieldName(final Name.DataCons associatedDataConsNameForFieldName) {
            return new FinderState(Collections.singletonList(associatedDataConsNameForFieldName), this.associatedTypeClassNameForInstanceMethod);
        }
        
        /**
         * Constructs a new state object based on this one, but with an updated list representing
         * the data constructor names that are associated with any field names encountered during
         * the visitation.
         * 
         * @param associatedDataConsNamesForFieldName
         *            this array represents the data constructor names that are associated with any
         *            field names encountered during the visitation. Can *not* be null.
         * @return a new state object.
         */
        FinderState updateAssociatedDataConsNamesForFieldName(final Name.DataCons[] associatedDataConsNamesForFieldName) {
            return new FinderState(Arrays.asList(associatedDataConsNamesForFieldName), this.associatedTypeClassNameForInstanceMethod);
        }
        
        /**
         * Constructs a new state object based on this one, but with a null list representing
         * the data constructor names that are associated with any field names encountered during
         * the visitation - the visitation is moving out of data constructor field territory.
         * 
         * @return a new state object.
         */
        FinderState clearAssociatedDataConsNamesForFieldName() {
            return new FinderState(null, this.associatedTypeClassNameForInstanceMethod);
        }
        
        /**
         * Constructs a new state object based on this one, but with an updated name representing
         * the type class that is associated with any instance methods encountered during the
         * visitation.
         * 
         * @param associatedTypeClassNameForInstanceMethod
         *            if non-null, this represents the name of the type class that is associated
         *            with any instance methods encountered during the visitation. Can be null.
         * @return a new state object.
         */
        FinderState updateAssociatedTypeClassNameForInstanceMethod(final Name.TypeClass associatedTypeClassNameForInstanceMethod) {
            return new FinderState(this.associatedDataConsNamesForFieldName, associatedTypeClassNameForInstanceMethod);
        }
    }
    
    ////
    /// Constructor
    //

    /**
     * Constructs an instance of this class.
     * @param currentModuleName the name of the module associated with the source being visited.
     */
    public IdentifierOccurrenceFinder(final ModuleName currentModuleName) {
        super(currentModuleName);
    }

    ////
    /// Handler methods
    //

    /**
     * Processes a module name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleModuleNameBinding(
        final Binding<IdentifierInfo.Module> binding,
        final TopLevelScope scope) {}
    
    /**
     * Processes a module name reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleModuleNameReference(
        final Reference<IdentifierInfo.Module> reference,
        final Binding<IdentifierInfo.Module> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a top-level function or class method name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleTopLevelFunctionOrClassMethodBinding(
        final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> binding,
        final TopLevelScope scope) {}
    
    /**
     * Processes a local variable name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleLocalVariableBinding(
        final Binding<IdentifierInfo.Local> binding,
        final LocalScope scope) {}
    
    /**
     * Processes a variable name reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleVarNameReference(
        final Reference<? extends IdentifierInfo> reference,
        final Binding<? extends IdentifierInfo> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a top-level function or class method operator reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleTopLevelFunctionOrClassMethodOperatorReference(
        final Reference.Operator<IdentifierInfo.TopLevel.FunctionOrClassMethod> reference,
        final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a data constructor name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleDataConsBinding(
        final Binding<IdentifierInfo.TopLevel.DataCons> binding,
        final TopLevelScope scope) {}
    
    /**
     * Processes a data constructor name reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleDataConsNameReference(
        final Reference<IdentifierInfo.TopLevel.DataCons> reference,
        final Binding<IdentifierInfo.TopLevel.DataCons> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a data constructor operator reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleDataConsOperatorReference(
        final Reference.Operator<IdentifierInfo.TopLevel.DataCons> reference,
        final Binding<IdentifierInfo.TopLevel.DataCons> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a type constructor name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleTypeConsBinding(
        final Binding<IdentifierInfo.TopLevel.TypeCons> binding,
        final TopLevelScope scope) {}
    
    /**
     * Processes a type constructor name reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleTypeConsNameReference(
        final Reference<IdentifierInfo.TopLevel.TypeCons> reference,
        final Binding<IdentifierInfo.TopLevel.TypeCons> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a type constructor operator reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleTypeConsOperatorReference(
        final Reference.Operator<IdentifierInfo.TopLevel.TypeCons> reference,
        final Binding<IdentifierInfo.TopLevel.TypeCons> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a type class name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleTypeClassBinding(
        final Binding<IdentifierInfo.TopLevel.TypeClass> binding,
        final TopLevelScope scope) {}
    
    /**
     * Processes a type class name reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleTypeClassNameReference(
        final Reference<IdentifierInfo.TopLevel.TypeClass> reference,
        final Binding<IdentifierInfo.TopLevel.TypeClass> binding,
        final SymbolTable scope) {}
    
    /**
     * Processes a data constructor field name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleDataConsFieldNameBinding(
        final Binding<IdentifierInfo.DataConsFieldName> binding,
        final TopLevelScope scope) {}
    
    /**
     * Processes a data constructor field name reference occurrence.
     * @param reference the reference occurrence.
     * @param bindings the list of bindings to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleDataConsFieldNameReference(
        final Reference.DataConsFieldName reference,
        final List<Binding<IdentifierInfo.DataConsFieldName>> bindings,
        final SymbolTable scope) {}
    
    /**
     * Processes a record field name reference occurrence.
     * @param reference the reference occurrence.
     */
    protected void handleRecordFieldNameReference(final Reference.RecordFieldName reference) {}
    
    /**
     * Processes a type variable name binding occurrence.
     * @param binding the binding occurrence.
     * @param scope the scope declaring the binding.
     */
    protected void handleTypeVariableBinding(
        final Binding<IdentifierInfo.TypeVariable> binding,
        final TypeVariableScope scope) {}
    
    /**
     * Processes a type variable name reference occurrence.
     * @param reference the reference occurrence.
     * @param binding the binding to which the reference is resolved.
     * @param scope the innermost scope from which the resolution is initiated.
     */
    protected void handleTypeVariableReference(
        final Reference<IdentifierInfo.TypeVariable> reference,
        final Binding<IdentifierInfo.TypeVariable> binding,
        final TypeVariableScope scope) {}

    ////
    /// Occurrence recording methods
    //

    /**
     * Resolves a module name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param moduleName the module name.
     * @param mustBeFullyQualified whether the module name must be fully qualified.
     * @param allowForwardReference whether a forward reference is allowed in the context.
     */
    protected void recordModuleName(final SymbolTable scope, final Name.Module moduleName, final boolean mustBeFullyQualified, final boolean allowForwardReference) {

        final Binding<IdentifierInfo.Module> binding = scope.resolveMaybeModuleName(moduleName);
        if (binding != null) {
            if (binding.getSourceElement() != moduleName) {
                handleModuleNameReference(
                    Reference.Module.make(mustBeFullyQualified, binding.getIdentifierInfo(), moduleName),
                    binding,
                    scope);
            }
        } else if (allowForwardReference) {
            final IdentifierInfo.Module identifierInfo = new IdentifierInfo.Module(SourceModel.Name.Module.toModuleName(moduleName));
            
            handleModuleNameReference(
                Reference.Module.make(mustBeFullyQualified, identifierInfo, moduleName),
                Binding.External.make(identifierInfo),
                scope);
        }
    }
    
    /**
     * Resolves a module name appearing in a qualified name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param moduleNameReference the module name reference in the qualified name.
     * @param allowForwardReference whether a forward reference is allowed in the context.
     */
    protected void recordModuleNameInQualifiedName(final SymbolTable scope, final Reference.Module moduleNameReference, final boolean allowForwardReference) {
        
        final Binding<IdentifierInfo.Module> binding = scope.resolveMaybeModuleName(moduleNameReference.getIdentifierInfo().getResolvedName());
        if (binding != null) {
            handleModuleNameReference(moduleNameReference, binding, scope);
            
        } else if (allowForwardReference) {
            final IdentifierInfo.Module identifierInfo = new IdentifierInfo.Module(moduleNameReference.getIdentifierInfo().getResolvedName());
            handleModuleNameReference(moduleNameReference, Binding.External.make(identifierInfo), scope);
        }
    }
    
    /**
     * Resolves a type variable name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current type variable scope.
     * @param typeVarName the type variable name.
     */
    protected void recordTypeVarName(final TypeVariableScope scope, final Name.TypeVar typeVarName) {
        
        final Binding<IdentifierInfo.TypeVariable> binding = scope.findTypeVar(typeVarName.getName());
        if (binding != null && binding.getSourceElement() != typeVarName) {

            handleTypeVariableReference(
                Reference.TypeVariable.make(
                    new IdentifierInfo.TypeVariable(binding.getIdentifierInfo().getTypeVarName(), binding.getIdentifierInfo().getTypeVarUniqueIdentifier()),
                    typeVarName),
                binding,
                scope);
        }
    }
    
    /**
     * Resolves a function name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param functionName the function name.
     * @param isExpression whether the occurrence appears in an expression context.
     */
    protected void recordFunctionName(final SymbolTable scope, final Name.Function functionName, final boolean isExpression) {
        
        final Binding<? extends IdentifierInfo> binding = scope.findFunction(functionName);
        if (binding != null && binding.getSourceElement() != functionName) {

            if (binding.getIdentifierInfo() instanceof IdentifierInfo.TopLevel.FunctionOrClassMethod) {
                final IdentifierInfo.TopLevel.FunctionOrClassMethod topLevelFunctionOrClassMethodIdentifierInfo =
                    (IdentifierInfo.TopLevel.FunctionOrClassMethod)binding.getIdentifierInfo();
                
                final Name.Module moduleName = functionName.getModuleName();

                final Reference.Module moduleNameOccurrence;
                if (moduleName == null) {
                    moduleNameOccurrence = null;

                } else {
                    moduleNameOccurrence =
                        Reference.Module.make(
                            false,
                            new IdentifierInfo.Module(topLevelFunctionOrClassMethodIdentifierInfo.getResolvedName().getModuleName()),
                            moduleName);
                    
                    recordModuleNameInQualifiedName(scope, moduleNameOccurrence, false);
                }

                handleVarNameReference(
                    Reference.Qualifiable.make(topLevelFunctionOrClassMethodIdentifierInfo, isExpression, moduleNameOccurrence, functionName),
                    binding,
                    scope);
                
            } else if (binding.getIdentifierInfo() instanceof IdentifierInfo.Local) {
                final IdentifierInfo.Local localIdentifierInfo =
                    (IdentifierInfo.Local)binding.getIdentifierInfo();
                
                handleVarNameReference(
                    Reference.Local.make(localIdentifierInfo, functionName), binding, scope);
                
            } else {
                throw new IllegalStateException("Unexpected binding:" + binding);
            }
        }
    }
    
    /**
     * Resolves an unqualified variable name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param varName the variable name.
     * @param sourceElement the associated source element.
     * @param nameSourceRange the source range of the name.
     */
    protected void recordUnqualifiedVarName(final SymbolTable scope, final String varName, final SourceElement sourceElement, final SourceRange nameSourceRange) {
        
        final Binding<? extends IdentifierInfo> binding = scope.findVariableDefinition(varName);
        if (binding != null && binding.getSourceElement() != sourceElement) {
            
            handleVarNameReference(
                Reference.NonQualifiable.make(binding.getIdentifierInfo(), sourceElement, nameSourceRange),
                binding,
                scope);
        }
    }
    
    /**
     * Resolves an argument name in a CALDoc arg block, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param argBlock the arg block.
     * @param associatedDataConsNames if non-null, this list represents the data constructor names that are associated with
     *          any field names encountered during the visitation.
     */
    protected void recordCALDocArgBlock(final SymbolTable scope, final CALDoc.TaggedBlock.Arg argBlock, final List<Name.DataCons> associatedDataConsNames) {
        if (associatedDataConsNames == null) {
            // a function argument reference
            final String argName = argBlock.getArgName().getName().getCalSourceForm();
            final Binding<? extends IdentifierInfo> binding = scope.findVariableDefinition(argName);

            if (binding != null) {
                if (binding.getSourceElement() != argBlock.getArgName()) {

                    if (binding.getIdentifierInfo() instanceof IdentifierInfo.Local.Parameter) {
                        final IdentifierInfo.Local.Parameter paramIdentifierInfo =
                            (IdentifierInfo.Local.Parameter)binding.getIdentifierInfo();

                        handleVarNameReference(
                            Reference.Local.make(paramIdentifierInfo, argBlock.getArgName()), binding, scope);

                    } else {
                        // the name resolved to something other than a parameter, so the source code is broken
                        // but this is not an error case to be reported as an exception.
                    }
                }
            } else {
                // a binding could not be found - so this is in fact a definition of a non-lexical argument
                // (e.g. Prelude.apply's second argument "argument" is declared only in CALDoc)
                System.err.println("Bad @arg: " + argName + " at " + argBlock.getArgName().getSourceRange());
            }
        } else {
            // a data cons argument reference
            assert associatedDataConsNames.size() == 1;
            recordNonBindingDataConsFieldName(scope, argBlock.getArgName(), associatedDataConsNames, false);
        }
    }
    
    /**
     * Resolves a top-level function name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param functionName the function name.
     * @param isExpression whether the occurrence appears in an expression context.
     * @param isUncheckedCALDocReference whether the occurrence is an unchecked CALDoc reference.
     */
    protected void recordTopLevelFunctionName(final SymbolTable scope, final Name.Function functionName, final boolean isExpression, final boolean isUncheckedCALDocReference) {

        if (isUncheckedCALDocReference) {

            // It is an unchecked CALDoc reference, so we create an occurrence with an external binding
            // but only if it has a module name that is not the current module name
            // (otherwise it would have been local to the module, and we leave it to the code below to handle it)
            final Name.Module moduleName = functionName.getModuleName();

            if (moduleName != null) {
                final ModuleName moduleNameInterpretedAsFullyQualified = SourceModel.Name.Module.toModuleName(moduleName);

                if (!moduleNameInterpretedAsFullyQualified.equals(getCurrentModuleName())) {

                    final Reference.Module moduleNameOccurrence =
                        Reference.Module.make(
                            true,
                            new IdentifierInfo.Module(moduleNameInterpretedAsFullyQualified),
                            moduleName);

                    recordModuleNameInQualifiedName(scope, moduleNameOccurrence, true);

                    final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> externalBinding =
                        Binding.External.make(
                            new IdentifierInfo.TopLevel.FunctionOrClassMethod(
                                QualifiedName.make(moduleNameInterpretedAsFullyQualified, functionName.getUnqualifiedName())));

                    handleVarNameReference(
                        Reference.Qualifiable.make(externalBinding.getIdentifierInfo(), false, moduleNameOccurrence, functionName),
                        externalBinding,
                        scope);
                    
                    // we're done, so return here to skip the code below
                    return;
                }
            }
        }

        final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> binding = scope.findTopLevelFunctionOrClassMethodDefinition(functionName);
        if (binding != null) {
            if (binding.getSourceElement() != functionName) {

                final Name.Module moduleName = functionName.getModuleName();

                final Reference.Module moduleNameOccurrence;
                if (moduleName == null) {
                    moduleNameOccurrence = null;

                } else {
                    moduleNameOccurrence =
                        Reference.Module.make(
                            isUncheckedCALDocReference,
                            new IdentifierInfo.Module(binding.getIdentifierInfo().getResolvedName().getModuleName()),
                            moduleName);
                    
                    recordModuleNameInQualifiedName(scope, moduleNameOccurrence, false);
                }

                handleVarNameReference(
                    Reference.Qualifiable.make(binding.getIdentifierInfo(), isExpression, moduleNameOccurrence, functionName),
                    binding,
                    scope);
            }
        }
    }
    
    /**
     * Resolves an instance method name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param instanceMethod the instance method.
     * @param typeClassName the associated type class name.
     */
    protected void recordInstanceMethodName(final SymbolTable scope, final InstanceDefn.InstanceMethod instanceMethod, final Name.TypeClass typeClassName) {
        
        final Binding<IdentifierInfo.TopLevel.TypeClass> typeClassBinding = scope.findTypeClass(typeClassName);
        
        if (typeClassBinding != null) {
            final Name.Function classMethodName = Name.Function.make(
                typeClassBinding.getIdentifierInfo().getResolvedName().getModuleName(), instanceMethod.getClassMethodName());
            
            final Binding<? extends IdentifierInfo> classMethodBinding = scope.findFunction(classMethodName);
            if (classMethodBinding != null) {
                if (classMethodBinding.getIdentifierInfo() instanceof IdentifierInfo.TopLevel.FunctionOrClassMethod) {
                    final IdentifierInfo.TopLevel.FunctionOrClassMethod topLevelFunctionOrClassMethodIdentifierInfo =
                        (IdentifierInfo.TopLevel.FunctionOrClassMethod)classMethodBinding.getIdentifierInfo();
                    
                    handleVarNameReference(
                        Reference.NonQualifiable.make(topLevelFunctionOrClassMethodIdentifierInfo, instanceMethod, instanceMethod.getClassMethodNameSourceRange()),
                        classMethodBinding,
                        scope);
                    
                } else {
                    throw new IllegalStateException("Unexpected binding:" + classMethodBinding);
                }
            }
        }
    }
    
    /**
     * Resolves a data constructor name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param dataConsName the data constructor name.
     * @param isExpression whether the occurrence appears in an expression context.
     * @param isUncheckedCALDocReference whether the occurrence is an unchecked CALDoc reference.
     */
    protected void recordDataConsName(final SymbolTable scope, final Name.DataCons dataConsName, final boolean isExpression, final boolean isUncheckedCALDocReference) {
        
        final Pair<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>, Binding<IdentifierInfo.TopLevel.DataCons>>
            dataConsReferenceBindingPair = resolveDataConsOccurrence(scope, dataConsName, isExpression, isUncheckedCALDocReference, true);
        
        if (dataConsReferenceBindingPair != null) {
            handleDataConsNameReference(dataConsReferenceBindingPair.fst(), dataConsReferenceBindingPair.snd(), scope);
        }
    }

    /**
     * Resolves a data constructor name.
     * @param scope the current scope.
     * @param dataConsName the data constructor name.
     * @param isExpression whether the occurrence appears in an expression context.
     * @param isUncheckedCALDocReference whether the occurrence is an unchecked CALDoc reference.
     * @param shouldRecordModuleNameOccurrence whether to record the module name occurrence in the data constructor name.
     * @return a pair: the data constructor reference, and the associated binding.
     */
    protected Pair<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>, Binding<IdentifierInfo.TopLevel.DataCons>>
    resolveDataConsOccurrence(
        final SymbolTable scope, final Name.DataCons dataConsName, final boolean isExpression, final boolean isUncheckedCALDocReference, final boolean shouldRecordModuleNameOccurrence) {
        
        if (isUncheckedCALDocReference) {

            // It is an unchecked CALDoc reference, so we create an occurrence with an external binding
            // but only if it has a module name that is not the current module name
            // (otherwise it would have been local to the module, and we leave it to the code below to handle it)
            final Name.Module moduleName = dataConsName.getModuleName();

            if (moduleName != null) {
                final ModuleName moduleNameInterpretedAsFullyQualified = SourceModel.Name.Module.toModuleName(moduleName);

                if (!moduleNameInterpretedAsFullyQualified.equals(getCurrentModuleName())) {

                    final Reference.Module moduleNameOccurrence =
                        Reference.Module.make(
                            true,
                            new IdentifierInfo.Module(moduleNameInterpretedAsFullyQualified),
                            moduleName);

                    if (shouldRecordModuleNameOccurrence) {
                        recordModuleNameInQualifiedName(scope, moduleNameOccurrence, true);
                    }

                    final Binding<IdentifierInfo.TopLevel.DataCons> externalBinding =
                        Binding.External.make(
                            new IdentifierInfo.TopLevel.DataCons(
                                QualifiedName.make(moduleNameInterpretedAsFullyQualified, dataConsName.getUnqualifiedName())));

                    return Pair.make(
                        Reference.Qualifiable.make(externalBinding.getIdentifierInfo(), false, moduleNameOccurrence, dataConsName),
                        externalBinding);
                }
            }
        }
        
        final Binding<IdentifierInfo.TopLevel.DataCons> binding = scope.findDataCons(dataConsName);
        if (binding != null) {
            if (binding.getSourceElement() != dataConsName) {

                final Name.Module moduleName = dataConsName.getModuleName();

                final Reference.Module moduleNameOccurrence;
                if (moduleName == null) {
                    moduleNameOccurrence = null;

                } else {
                    moduleNameOccurrence =
                        Reference.Module.make(
                            isUncheckedCALDocReference,
                            new IdentifierInfo.Module(binding.getIdentifierInfo().getResolvedName().getModuleName()),
                            moduleName);
                    
                    if (shouldRecordModuleNameOccurrence) {
                        recordModuleNameInQualifiedName(scope, moduleNameOccurrence, false);
                    }
                }

                return Pair.make(
                    Reference.Qualifiable.make(binding.getIdentifierInfo(), isExpression, moduleNameOccurrence, dataConsName),
                    binding);
            }
        }
        
        return null;
    }
    
    /**
     * Resolves a type constructor name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param typeConsName the type constructor name.
     * @param isUncheckedCALDocReference whether the occurrence is an unchecked CALDoc reference.
     */
    protected void recordTypeConsName(final SymbolTable scope, final Name.TypeCons typeConsName, final boolean isUncheckedCALDocReference) {

        if (isUncheckedCALDocReference) {

            // It is an unchecked CALDoc reference, so we create an occurrence with an external binding
            // but only if it has a module name that is not the current module name
            // (otherwise it would have been local to the module, and we leave it to the code below to handle it)
            final Name.Module moduleName = typeConsName.getModuleName();

            if (moduleName != null) {
                final ModuleName moduleNameInterpretedAsFullyQualified = SourceModel.Name.Module.toModuleName(moduleName);

                if (!moduleNameInterpretedAsFullyQualified.equals(getCurrentModuleName())) {

                    final Reference.Module moduleNameOccurrence =
                        Reference.Module.make(
                            true,
                            new IdentifierInfo.Module(moduleNameInterpretedAsFullyQualified),
                            moduleName);

                    recordModuleNameInQualifiedName(scope, moduleNameOccurrence, true);

                    final Binding<IdentifierInfo.TopLevel.TypeCons> externalBinding =
                        Binding.External.make(
                            new IdentifierInfo.TopLevel.TypeCons(
                                QualifiedName.make(moduleNameInterpretedAsFullyQualified, typeConsName.getUnqualifiedName())));

                    handleVarNameReference(
                        Reference.Qualifiable.make(externalBinding.getIdentifierInfo(), false, moduleNameOccurrence, typeConsName),
                        externalBinding,
                        scope);
                    
                    // we're done, so return here to skip the code below
                    return;
                }
            }
        }
        
        final Binding<IdentifierInfo.TopLevel.TypeCons> binding = scope.findTypeCons(typeConsName);
        if (binding != null) {
            if (binding.getSourceElement() != typeConsName) {

                final Name.Module moduleName = typeConsName.getModuleName();

                final Reference.Module moduleNameOccurrence;
                if (moduleName == null) {
                    moduleNameOccurrence = null;

                } else {
                    moduleNameOccurrence =
                        Reference.Module.make(
                            isUncheckedCALDocReference,
                            new IdentifierInfo.Module(binding.getIdentifierInfo().getResolvedName().getModuleName()),
                            moduleName);
                    
                    recordModuleNameInQualifiedName(scope, moduleNameOccurrence, false);
                }

                handleTypeConsNameReference(
                    Reference.Qualifiable.make(binding.getIdentifierInfo(), false, moduleNameOccurrence, typeConsName),
                    binding,
                    scope);
            }
        }
    }
    
    /**
     * Resolves a type class name, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param typeClassName the type class name.
     * @param isUncheckedCALDocReference whether the occurrence is an unchecked CALDoc reference.
     */
    protected void recordTypeClassName(final SymbolTable scope, final Name.TypeClass typeClassName, final boolean isUncheckedCALDocReference) {

        if (isUncheckedCALDocReference) {

            // It is an unchecked CALDoc reference, so we create an occurrence with an external binding
            // but only if it has a module name that is not the current module name
            // (otherwise it would have been local to the module, and we leave it to the code below to handle it)
            final Name.Module moduleName = typeClassName.getModuleName();

            if (moduleName != null) {
                final ModuleName moduleNameInterpretedAsFullyQualified = SourceModel.Name.Module.toModuleName(moduleName);

                if (!moduleNameInterpretedAsFullyQualified.equals(getCurrentModuleName())) {

                    final Reference.Module moduleNameOccurrence =
                        Reference.Module.make(
                            true,
                            new IdentifierInfo.Module(moduleNameInterpretedAsFullyQualified),
                            moduleName);

                    recordModuleNameInQualifiedName(scope, moduleNameOccurrence, true);

                    final Binding<IdentifierInfo.TopLevel.TypeClass> externalBinding =
                        Binding.External.make(
                            new IdentifierInfo.TopLevel.TypeClass(
                                QualifiedName.make(moduleNameInterpretedAsFullyQualified, typeClassName.getUnqualifiedName())));

                    handleVarNameReference(
                        Reference.Qualifiable.make(externalBinding.getIdentifierInfo(), false, moduleNameOccurrence, typeClassName),
                        externalBinding,
                        scope);
                    
                    // we're done, so return here to skip the code below
                    return;
                }
            }
        }
        
        final Binding<IdentifierInfo.TopLevel.TypeClass> binding = scope.findTypeClass(typeClassName);
        if (binding != null) {
            if (binding.getSourceElement() != typeClassName) {

                final Name.Module moduleName = typeClassName.getModuleName();

                final Reference.Module moduleNameOccurrence;
                if (moduleName == null) {
                    moduleNameOccurrence = null;

                } else {
                    moduleNameOccurrence =
                        Reference.Module.make(
                            isUncheckedCALDocReference,
                            new IdentifierInfo.Module(binding.getIdentifierInfo().getResolvedName().getModuleName()),
                            moduleName);
                    
                    recordModuleNameInQualifiedName(scope, moduleNameOccurrence, false);
                }

                handleTypeClassNameReference(
                    Reference.Qualifiable.make(binding.getIdentifierInfo(), false, moduleNameOccurrence, typeClassName),
                    binding,
                    scope);
            }
        }
    }
    
    /**
     * Resolves a constructor name without context (as appearing in a CALDoc reference), and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param name the constructor name.
     * @param isUncheckedCALDocReference whether the occurrence is an unchecked CALDoc reference.
     */
    protected void recordConsNameWithoutContext(final SymbolTable scope, final Name.WithoutContextCons name, final boolean isUncheckedCALDocReference) {
        if (isUncheckedCALDocReference) {
            // unchecked CALDoc reference with constructor name *must* have context provided
            return;
        }
        
        final Binding<? extends IdentifierInfo> binding = scope.findConsNameWithoutContext(name);
        if (binding != null) {
            if (binding.getSourceElement() != name) {

                if (binding.getIdentifierInfo() instanceof IdentifierInfo.Module) {
                    final IdentifierInfo.Module moduleIdentifierInfo = (IdentifierInfo.Module)binding.getIdentifierInfo();
                    
                    handleModuleNameReference(
                        Reference.Module.make(isUncheckedCALDocReference, moduleIdentifierInfo, name),
                        UnsafeCast.<Binding<IdentifierInfo.Module>>unsafeCast(binding),
                        scope);
                    
                } else if (binding.getIdentifierInfo() instanceof IdentifierInfo.TopLevel){
                    final IdentifierInfo.TopLevel topLevelIdentifierInfo = (IdentifierInfo.TopLevel)binding.getIdentifierInfo();
                    
                    final Name.Module moduleName = name.getModuleName();

                    final Reference.Module moduleNameOccurrence;
                    if (moduleName == null) {
                        moduleNameOccurrence = null;

                    } else {
                        moduleNameOccurrence =
                            Reference.Module.make(
                                isUncheckedCALDocReference,
                                new IdentifierInfo.Module(topLevelIdentifierInfo.getResolvedName().getModuleName()),
                                moduleName);
                        
                        recordModuleNameInQualifiedName(scope, moduleNameOccurrence, false);
                    }

                    if (topLevelIdentifierInfo instanceof IdentifierInfo.TopLevel.FunctionOrClassMethod) {
                        handleVarNameReference(
                            Reference.Qualifiable.make(topLevelIdentifierInfo, false, moduleNameOccurrence, name),
                            UnsafeCast.<Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod>>unsafeCast(binding),
                            scope);
                        
                    } else if (topLevelIdentifierInfo instanceof IdentifierInfo.TopLevel.DataCons) {
                        handleDataConsNameReference(
                            Reference.Qualifiable.make((IdentifierInfo.TopLevel.DataCons)topLevelIdentifierInfo, false, moduleNameOccurrence, name),
                            UnsafeCast.<Binding<IdentifierInfo.TopLevel.DataCons>>unsafeCast(binding),
                            scope);
                        
                    } else if (topLevelIdentifierInfo instanceof IdentifierInfo.TopLevel.TypeCons) {
                        handleTypeConsNameReference(
                            Reference.Qualifiable.make((IdentifierInfo.TopLevel.TypeCons)topLevelIdentifierInfo, false, moduleNameOccurrence, name),
                            UnsafeCast.<Binding<IdentifierInfo.TopLevel.TypeCons>>unsafeCast(binding),
                            scope);
                        
                    } else if (topLevelIdentifierInfo instanceof IdentifierInfo.TopLevel.TypeClass) {
                        handleTypeClassNameReference(
                            Reference.Qualifiable.make((IdentifierInfo.TopLevel.TypeClass)topLevelIdentifierInfo, false, moduleNameOccurrence, name),
                            UnsafeCast.<Binding<IdentifierInfo.TopLevel.TypeClass>>unsafeCast(binding),
                            scope);
                        
                    } else {
                        throw new IllegalStateException("Unexpected binding: " + binding);
                    }
                    
                } else {
                    throw new IllegalStateException("Unexpected binding: " + binding);
                }
            }
        }
        
        // CALDoc constructor name references without context must be resolvable, otherwise they must be declared with context
        // so if we fall out here, there's nothing we can do.
    }
    
    /**
     * Resolves a data constructor field name appearing in a non-binding context, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param fieldName the field name.
     * @param dataConsNames the names of the associated data constructors - there can be more than one.
     * @param isPunnedOrdinal whether the field name is an ordinal field name in a punned context.
     */
    protected void recordNonBindingDataConsFieldName(final SymbolTable scope, final Name.Field fieldName, final List<Name.DataCons> dataConsNames, final boolean isPunnedOrdinal) {
        final List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> dataConsOccurrences =
            new ArrayList<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>>();
        
        final List<IdentifierInfo.TopLevel.DataCons> dataConsIdentifierInfos =
            new ArrayList<IdentifierInfo.TopLevel.DataCons>();
        
        final List<Binding<IdentifierInfo.DataConsFieldName>> bindings =
            new ArrayList<Binding<IdentifierInfo.DataConsFieldName>>();
        
        for (final Name.DataCons dataConsName : dataConsNames) {
            final Pair<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>, ?> dataConsReferenceBindingPair =
                resolveDataConsOccurrence(scope, dataConsName, false, false, false);
            
            if (dataConsReferenceBindingPair == null) {
                continue;
            }
            
            final Binding<IdentifierInfo.DataConsFieldName> binding = scope.findDataConsFieldName(dataConsName, fieldName);
            if (binding == null) {
                continue;
            }
            
            dataConsOccurrences.add(dataConsReferenceBindingPair.fst());
            dataConsIdentifierInfos.add(dataConsReferenceBindingPair.fst().getIdentifierInfo());
            
            bindings.add(binding);
        }
        
        if (isPunnedOrdinal) {
            handleDataConsFieldNameReference(
                Reference.DataConsFieldName.PunnedOrdinal.make(
                    new IdentifierInfo.DataConsFieldName(fieldName.getName(), dataConsIdentifierInfos),
                    dataConsOccurrences,
                    fieldName),
                bindings,
                scope);
        } else {
            handleDataConsFieldNameReference(
                Reference.DataConsFieldName.NonPunned.make(
                    new IdentifierInfo.DataConsFieldName(fieldName.getName(), dataConsIdentifierInfos),
                    dataConsOccurrences,
                    fieldName),
                bindings,
                scope);
        }
    }
    
    /**
     * Resolves a record field name appearing in a non-binding context, and if the occurrence is a reference occurrence, records it.
     * @param scope the current scope.
     * @param fieldName the field name.
     * @param isPunnedOrdinal whether the field name is an ordinal field name in a punned context.
     */
    protected void recordNonBindingRecordFieldName(final SymbolTable scope, final Name.Field fieldName, final boolean isPunnedOrdinal) {
        if (isPunnedOrdinal) {
            handleRecordFieldNameReference(
                Reference.RecordFieldName.PunnedOrdinal.make(new IdentifierInfo.RecordFieldName(fieldName.getName()), fieldName));
        } else {
            handleRecordFieldNameReference(
                Reference.RecordFieldName.NonPunned.make(new IdentifierInfo.RecordFieldName(fieldName.getName()), fieldName));
        }
    }
    
    /**
     * Resolves a function/method operator, and records the occurrence.
     * @param scope the current scope.
     * @param operatorFunctionName the name corresponding to the operator.
     * @param sourceElement the operator's associated source element.
     * @param operatorSourceRange the source range associated with the actual operator occurring in source. Can be null.
     * @param isDelimiterPair whether the operator consists of a pair of delimiters, e.g. (), [].
     */
    protected void recordTopLevelFunctionOrClassMethodOperator(
        final SymbolTable scope, final QualifiedName operatorFunctionName, final SourceElement sourceElement, final SourceRange operatorSourceRange, final boolean isDelimiterPair) {
        
        final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> binding =
            scope.findQualifiedFunctionOrClassMethod(Name.Function.make(operatorFunctionName));
        
        if (binding == null) {
            throw new IllegalStateException("Operators should always be resolvable");
        }
        
        handleTopLevelFunctionOrClassMethodOperatorReference(
            Reference.Operator.make(binding.getIdentifierInfo(), true, sourceElement, isDelimiterPair, operatorSourceRange),
            binding,
            scope);
    }
    
    /**
     * Resolves a data constructor operator, and records the occurrence.
     * @param scope the current scope.
     * @param operatorDataConsName the name corresponding to the operator.
     * @param sourceElement the operator's associated source element.
     * @param operatorSourceRange the source range associated with the actual operator occurring in source. Can be null.
     * @param isExpressionContext whether the occurrence appears in an expression context.
     * @param isDelimiterPair whether the operator consists of a pair of delimiters, e.g. (), [].
     */
    protected void recordDataConsOperator(
        final SymbolTable scope, final QualifiedName operatorDataConsName, final SourceElement sourceElement, final SourceRange operatorSourceRange, final boolean isExpressionContext, final boolean isDelimiterPair) {
        
        final Binding<IdentifierInfo.TopLevel.DataCons> binding = scope.findDataCons(Name.DataCons.make(operatorDataConsName));
        
        if (binding == null) {
            throw new IllegalStateException("Operators should always be resolvable");
        }
        
        handleDataConsOperatorReference(
            Reference.Operator.make(binding.getIdentifierInfo(), isExpressionContext, sourceElement, isDelimiterPair, operatorSourceRange),
            binding,
            scope);
    }
    
    /**
     * Resolves a type constructor operator, and records the occurrence.
     * @param scope the current scope.
     * @param operatorTypeConsName the name corresponding to the operator.
     * @param sourceElement the operator's associated source element.
     * @param operatorSourceRange the source range associated with the actual operator occurring in source. Can be null.
     * @param isDelimiterPair whether the operator consists of a pair of delimiters, e.g. (), [].
     */
    protected void recordTypeConsOperator(
        final SymbolTable scope, final QualifiedName operatorTypeConsName, final SourceElement sourceElement, final SourceRange operatorSourceRange, final boolean isDelimiterPair) {
        
        final Binding<IdentifierInfo.TopLevel.TypeCons> binding;
        
        if (operatorTypeConsName.equals(CAL_Prelude.TypeConstructors.Function)) {
            // Prelude.Function is a very special type that does not have a type declaration
            // so we just use an external binding
            binding = Binding.External.make(new IdentifierInfo.TopLevel.TypeCons(CAL_Prelude.TypeConstructors.Function));
            
        } else {
            binding = scope.findTypeCons(Name.TypeCons.make(operatorTypeConsName));

            if (binding == null) {
                throw new IllegalStateException("Operators should always be resolvable");
            }
        }
        
        handleTypeConsOperatorReference(
            Reference.Operator.make(binding.getIdentifierInfo(), false, sourceElement, isDelimiterPair, operatorSourceRange),
            binding,
            scope);
    }
    
    ////
    /// Overridden handler methods for new scopes - translating them into binding occurrences
    //

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleNewLocalScope(final LocalScope scope) {
        for (final Binding<IdentifierInfo.Local> binding : scope.getBindings()) {
            handleLocalVariableBinding(binding, scope);
        }
        
        super.handleNewLocalScope(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleNewTopLevelScope(final TopLevelScope scope) {
        
        handleModuleNameBinding(scope.getCurrentModuleNameBinding(), scope);
        
        for (final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> binding : scope.getFunctionAndClassMethodBindings()) {
            handleTopLevelFunctionOrClassMethodBinding(binding, scope);
        }
        
        for (final Binding<IdentifierInfo.TopLevel.TypeCons> binding : scope.getTypeConsBindings()) {
            handleTypeConsBinding(binding, scope);
        }
        
        for (final Binding<IdentifierInfo.TopLevel.DataCons> binding : scope.getDataConsBindings()) {
            handleDataConsBinding(binding, scope);
        }
        
        for (final Binding<IdentifierInfo.DataConsFieldName> binding : scope.getDataConsFieldNameBindings()) {
            handleDataConsFieldNameBinding(binding, scope);
        }
        
        for (final Binding<IdentifierInfo.TopLevel.TypeClass> binding : scope.getTypeClassBindings()) {
            handleTypeClassBinding(binding, scope);
        }
        
        super.handleNewTopLevelScope(scope);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleNewTypeVariableScope(final TypeVariableScope scope) {
        for (final Binding<IdentifierInfo.TypeVariable> binding : scope.getBindings()) {
            handleTypeVariableBinding(binding, scope);
        }
        
        super.handleNewTypeVariableScope(scope);
    }
    
    ////
    /// Visitor methods - elements which introduce new references (bindings are taken care of by the superclass)
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Name_Module(final Name.Module moduleName, final VisitorArgument<FinderState> arg) {
        recordModuleName(arg.getScope(), moduleName, false, false);
        return super.visit_Name_Module(moduleName, arg);
    }
    
    @Override
    public R visit_Friend(final Friend friendDeclaration, final VisitorArgument<FinderState> arg) {
        recordModuleName(arg.getScope(), friendDeclaration.getFriendModuleName(), true, true);
        // we do not invoke the superclass implementation, which would delegate to visit_Name_Module
        return null;
    }

    @Override
    public R visit_Import(final Import importStmt, final VisitorArgument<FinderState> arg) {
        // we do not invoke the superclass implementation, which would delegate to visit_Name_Module
        // instead we do the traversal ourselves
        recordModuleName(arg.getScope(), importStmt.getImportedModuleName(), true, false);
        for (final UsingItem usingItem : importStmt.getUsingItems()) {
            usingItem.accept(this, arg);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_CALDoc_CrossReference_Module(final CALDoc.CrossReference.Module reference, final VisitorArgument<FinderState> arg) {
        recordModuleName(arg.getScope(), reference.getName(), !reference.isChecked(), !reference.isChecked());
        // we do not invoke the superclass implementation, which would delegate to visit_Name_Module
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Name_Function(final Name.Function function, final VisitorArgument<FinderState> arg) {
        recordFunctionName(arg.getScope(), function, false);
        // instead of invoking the superclass implementation, which would delegate to visit_Name_Module
        // the recording method has already done the recording of the module name
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_FunctionTypeDeclaraction(final FunctionTypeDeclaration declaration, final VisitorArgument<FinderState> arg) {
        recordUnqualifiedVarName(arg.getScope(), declaration.getFunctionName(), declaration, declaration.getSourceRangeOfName());
        return super.visit_FunctionTypeDeclaraction(declaration, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_LocalDefn_Function_TypeDeclaration(final LocalDefn.Function.TypeDeclaration declaration, final VisitorArgument<FinderState> arg) {
        recordUnqualifiedVarName(arg.getScope(), declaration.getName(), declaration, declaration.getNameSourceRange());
        return super.visit_LocalDefn_Function_TypeDeclaration(declaration, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_Var(final Expr.Var var, final VisitorArgument<FinderState> arg) {
        // instead of invoking the superclass implementation, which would delegate to visit_Name_Function
        // we record the function name directly, and specify that it is in an expression context
        recordFunctionName(arg.getScope(), var.getVarName(), true);
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_CALDoc_CrossReference_Function(final CALDoc.CrossReference.Function reference, final VisitorArgument<FinderState> arg) {
        // instead of invoking the superclass implementation, which would delegate to visit_Name_Function
        // we record the function name directly, and specify that it is in a CALDoc context
        recordTopLevelFunctionName(arg.getScope(), reference.getName(), false, !reference.isChecked());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Name_DataCons(final Name.DataCons cons, final VisitorArgument<FinderState> arg) {
        recordDataConsName(arg.getScope(), cons, false, false);
        // instead of invoking the superclass implementation, which would delegate to visit_Name_Module
        // the recording method has already done the recording of the module name
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_DataCons(final Expr.DataCons cons, final VisitorArgument<FinderState> arg) {
        // instead of invoking the superclass implementation, which would delegate to visit_Name_DataCons
        // we record the data cons name directly, and specify that it is in an expression context
        recordDataConsName(arg.getScope(), cons.getDataConsName(), true, false);
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_CALDoc_CrossReference_DataCons(final CALDoc.CrossReference.DataCons reference, final VisitorArgument<FinderState> arg) {
        // instead of invoking the superclass implementation, which would delegate to visit_Name_DataCons
        // we record the data cons name directly, and specify that it is in a CALDoc context
        recordDataConsName(arg.getScope(), reference.getName(), false, !reference.isChecked());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Name_TypeCons(final Name.TypeCons cons, final VisitorArgument<FinderState> arg) {
        recordTypeConsName(arg.getScope(), cons, false);
        // instead of invoking the superclass implementation, which would delegate to visit_Name_Module
        // the recording method has already done the recording of the module name
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_CALDoc_CrossReference_TypeCons(final CALDoc.CrossReference.TypeCons reference, final VisitorArgument<FinderState> arg) {
        // instead of invoking the superclass implementation, which would delegate to visit_Name_TypeCons
        // we record the data cons name directly, and specify that it is in a CALDoc context
        recordTypeConsName(arg.getScope(), reference.getName(), !reference.isChecked());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Name_TypeClass(final Name.TypeClass typeClass, final VisitorArgument<FinderState> arg) {
        recordTypeClassName(arg.getScope(), typeClass, false);
        // instead of invoking the superclass implementation, which would delegate to visit_Name_Module
        // the recording method has already done the recording of the module name
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_CALDoc_CrossReference_TypeClass(final CALDoc.CrossReference.TypeClass reference, final VisitorArgument<FinderState> arg) {
        // instead of invoking the superclass implementation, which would delegate to visit_Name_TypeClass
        // we record the data cons name directly, and specify that it is in a CALDoc context
        recordTypeClassName(arg.getScope(), reference.getName(), !reference.isChecked());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_CALDoc_CrossReference_WithoutContextCons(final CALDoc.CrossReference.WithoutContextCons reference, final VisitorArgument<FinderState> arg) {
        recordConsNameWithoutContext(arg.getScope(), reference.getName(), !reference.isChecked());
        // instead of invoking the superclass implementation, which would ultimately delegate to visit_Name_Module
        // the recording method has already done the recording of the module name (and figured out whether the whole thing
        // is a module name, or whether the reference is to a qualified name)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_InstanceDefn(final InstanceDefn defn, final VisitorArgument<FinderState> arg) {
        final FinderState newState = arg.getUserState().updateAssociatedTypeClassNameForInstanceMethod(defn.getTypeClassName());
        return super.visit_InstanceDefn(defn, arg.updateUserState(newState));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_InstanceDefn_InstanceMethod(final InstanceDefn.InstanceMethod method, final VisitorArgument<FinderState> arg) {
        recordInstanceMethodName(arg.getScope(), method, arg.getUserState().getAssociatedTypeClassNameForInstanceMethod());
        // invoke the superclass implementation to traverse the other bits of the declaration, especially
        // the resolving function name
        return super.visit_InstanceDefn_InstanceMethod(method, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(final TypeConstructorDefn.AlgebraicType.DataConsDefn defn, final VisitorArgument<FinderState> arg) {
        final Name.DataCons dataConsName = Name.DataCons.make(getCurrentModuleName(), defn.getDataConsName());
        final FinderState newState = arg.getUserState().updateAssociatedDataConsNameForFieldName(dataConsName);
        return super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg.updateUserState(newState));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_TypeConstructorDefn_AlgebraicType_DataConsDefn_TypeArgument(final TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument argument, final VisitorArgument<FinderState> arg) {
        // we explicitly do not traverse the field name, since it has already been captured as a binding by the superclass
        
        // also, we moving into the type expression, where field names are *not* associated with the data constructor...
        // any Name.Field encountered would be a record field name!
        final FinderState newState = arg.getUserState().clearAssociatedDataConsNamesForFieldName();
        argument.getTypeExprDefn().accept(this, arg.updateUserState(newState));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Name_Field(final Name.Field name, final VisitorArgument<FinderState> arg) {
        final List<Name.DataCons> associatedDataConsNames = arg.getUserState().getAssociatedDataConsNamesForFieldName();
        if (associatedDataConsNames == null) {
            // a record field name
            recordNonBindingRecordFieldName(arg.getScope(), name, false);
        } else {
            // a data cons field name
            recordNonBindingDataConsFieldName(arg.getScope(), name, associatedDataConsNames, false);
        }
        return super.visit_Name_Field(name, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_CALDoc_TaggedBlock_Arg(final CALDoc.TaggedBlock.Arg argBlock, final VisitorArgument<FinderState> arg) {
        // record the argument name first
        // (we do not want it traversed by visit_Name_Field, which cannot handle the case for when the arg is really a function parameter)
        recordCALDocArgBlock(arg.getScope(), argBlock, arg.getUserState().getAssociatedDataConsNamesForFieldName());
        // then traverse the rest of the block
        return visit_CALDoc_TaggedBlock_TaggedBlockWithText_Helper(argBlock, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_Case_Alt_UnpackDataCons(final Expr.Case.Alt.UnpackDataCons cons, final VisitorArgument<FinderState> arg) {
        final VisitorArgument<FinderState> newArg = updateScopeFor(cons, arg);
        
        for (final Name.DataCons dataConsName : cons.getDataConsNames()) {
            dataConsName.accept(this, newArg);
        }
        cons.getArgBindings().accept(
            this, newArg.updateUserState(newArg.getUserState().updateAssociatedDataConsNamesForFieldName(cons.getDataConsNames())));
        cons.getAltExpr().accept(this, newArg);
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_FieldPattern(final FieldPattern fieldPattern, final VisitorArgument<FinderState> arg) {
        // a punned textual pattern has already been recorded by the superclass as a binding, so we only deal
        // if a non-punned textual pattern, or an ordinal field pattern
        
        if (fieldPattern.getPattern() != null) {
            // not a punned textual pattern, so record the field name
            fieldPattern.getFieldName().accept(this, arg);
        } else if (fieldPattern.getFieldName().getName() instanceof FieldName.Ordinal) {
            // a punned ordinal pattern... we do not defer to visit_Name_Field
            final List<Name.DataCons> associatedDataConsNames = arg.getUserState().getAssociatedDataConsNamesForFieldName();
            if (associatedDataConsNames == null) {
                // a record field name
                recordNonBindingRecordFieldName(arg.getScope(), fieldPattern.getFieldName(), true);
            } else {
                // a data cons field name
                recordNonBindingDataConsFieldName(arg.getScope(), fieldPattern.getFieldName(), associatedDataConsNames, true);
            }
        }
        
        if (fieldPattern.getPattern() != null) {
            fieldPattern.getPattern().accept(this, arg);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_LocalDefn_PatternMatch_UnpackTuple(final LocalDefn.PatternMatch.UnpackTuple unpackTuple, final VisitorArgument<FinderState> arg) {
        // we will skip the patterns as their bindings have been recorded by the superclass
        unpackTuple.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_LocalDefn_PatternMatch_UnpackDataCons(final LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, final VisitorArgument<FinderState> arg) {
        unpackDataCons.getDataConsName().accept(this, arg);
        unpackDataCons.getArgBindings().accept(
            this, arg.updateUserState(arg.getUserState().updateAssociatedDataConsNameForFieldName(unpackDataCons.getDataConsName())));
        unpackDataCons.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_SelectDataConsField(final Expr.SelectDataConsField field, final VisitorArgument<FinderState> arg) {
        field.getDataConsValuedExpr().accept(this, arg);
        field.getDataConsName().accept(this, arg);
        field.getFieldName().accept(
            this, arg.updateUserState(arg.getUserState().updateAssociatedDataConsNameForFieldName(field.getDataConsName())));
        return null;
    }
    
    ////
    /// Visitor methods - elements which contain type variables
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Name_TypeVar(final Name.TypeVar name, final VisitorArgument<FinderState> arg) {
        recordTypeVarName(arg.getTypeVariableScope(), name);
        return super.visit_Name_TypeVar(name, arg);
    }
    
    ////
    /// Visitor methods - elements which contain operators
    //

    /**
     * {@inheritDoc}
     */
    @Override
    protected R visit_Expr_BinaryOp_Helper(final Expr.BinaryOp binop, final VisitorArgument<FinderState> arg) {
        final QualifiedName operatorEntityName = OperatorInfo.getTextualName(binop.getOpText());
        
        if (LanguageInfo.isValidDataConstructorName(operatorEntityName.getUnqualifiedName())) {
            // a data cons, e.g. : (Prelude.Cons)
            recordDataConsOperator(
                arg.getScope(), operatorEntityName, binop, binop.getOperatorSourceRange(), true, false);
        } else {
            // must be a function name
            recordTopLevelFunctionOrClassMethodOperator(
                arg.getScope(), operatorEntityName, binop, binop.getOperatorSourceRange(), false);
        }
        return super.visit_Expr_BinaryOp_Helper(binop, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_Case_Alt_UnpackListCons(final Expr.Case.Alt.UnpackListCons cons, final VisitorArgument<FinderState> arg) {
        recordDataConsOperator(arg.getScope(), CAL_Prelude.DataConstructors.Cons, cons, cons.getOperatorSourceRange(), false, false);
        return super.visit_Expr_Case_Alt_UnpackListCons(cons, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_Case_Alt_UnpackListNil(final Expr.Case.Alt.UnpackListNil nil, final VisitorArgument<FinderState> arg) {
        recordDataConsOperator(arg.getScope(), CAL_Prelude.DataConstructors.Nil, nil, nil.getSourceRange(), false, true);
        return super.visit_Expr_Case_Alt_UnpackListNil(nil, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_Case_Alt_UnpackUnit(final Expr.Case.Alt.UnpackUnit unit, final VisitorArgument<FinderState> arg) {
        recordDataConsOperator(arg.getScope(), CAL_Prelude.DataConstructors.Unit, unit, unit.getSourceRange(), false, true);
        return super.visit_Expr_Case_Alt_UnpackUnit(unit, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_List(final Expr.List list, final VisitorArgument<FinderState> arg) {
        // if the list is empty, then it is [], or Prelude.Nil
        if (list.getNElements() == 0) {
            recordDataConsOperator(arg.getScope(), CAL_Prelude.DataConstructors.Nil, list, list.getSourceRange(), true, true);
        }
        return super.visit_Expr_List(list, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_UnaryOp_Negate(final Expr.UnaryOp.Negate negate, final VisitorArgument<FinderState> arg) {
        recordTopLevelFunctionOrClassMethodOperator(
            arg.getScope(), CAL_Prelude.Functions.negate, negate, negate.getOperatorSourceRange(), false);
        return super.visit_Expr_UnaryOp_Negate(negate, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_Expr_Unit(final Expr.Unit unit, final VisitorArgument<FinderState> arg) {
        recordDataConsOperator(arg.getScope(), CAL_Prelude.DataConstructors.Unit, unit, unit.getSourceRange(), true, true);
        return super.visit_Expr_Unit(unit, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_InstanceDefn_InstanceTypeCons_Function(final InstanceDefn.InstanceTypeCons.Function function, final VisitorArgument<FinderState> arg) {
        recordTypeConsOperator(arg.getScope(), CAL_Prelude.TypeConstructors.Function, function, function.getOperatorSourceRange(), false);
        return super.visit_InstanceDefn_InstanceTypeCons_Function(function, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_InstanceDefn_InstanceTypeCons_List(final InstanceDefn.InstanceTypeCons.List list, final VisitorArgument<FinderState> arg) {
        recordTypeConsOperator(arg.getScope(), CAL_Prelude.TypeConstructors.List, list, list.getSourceRange(), true);
        return super.visit_InstanceDefn_InstanceTypeCons_List(list, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_InstanceDefn_InstanceTypeCons_Unit(final InstanceDefn.InstanceTypeCons.Unit unit, final VisitorArgument<FinderState> arg) {
        recordTypeConsOperator(arg.getScope(), CAL_Prelude.TypeConstructors.Unit, unit, unit.getSourceRange(), true);
        return super.visit_InstanceDefn_InstanceTypeCons_Unit(unit, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_LocalDefn_PatternMatch_UnpackListCons(final LocalDefn.PatternMatch.UnpackListCons unpackListCons, final VisitorArgument<FinderState> arg) {
        recordDataConsOperator(arg.getScope(), CAL_Prelude.DataConstructors.Cons, unpackListCons, unpackListCons.getOperatorSourceRange(), false, false);
        // we will skip the head and tail patterns because their bindings have been recorded by the superclass
        unpackListCons.getDefiningExpr().accept(this, arg);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_TypeExprDefn_Function(final TypeExprDefn.Function function, final VisitorArgument<FinderState> arg) {
        recordTypeConsOperator(arg.getScope(), CAL_Prelude.TypeConstructors.Function, function, function.getOperatorSourceRange(), false);
        return super.visit_TypeExprDefn_Function(function, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_TypeExprDefn_List(final TypeExprDefn.List list, final VisitorArgument<FinderState> arg) {
        recordTypeConsOperator(arg.getScope(), CAL_Prelude.TypeConstructors.List, list, list.getSourceRange(), true);
        return super.visit_TypeExprDefn_List(list, arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visit_TypeExprDefn_Unit(final TypeExprDefn.Unit unit, final VisitorArgument<FinderState> arg) {
        recordTypeConsOperator(arg.getScope(), CAL_Prelude.TypeConstructors.Unit, unit, unit.getSourceRange(), true);
        return super.visit_TypeExprDefn_Unit(unit, arg);
    }
}
