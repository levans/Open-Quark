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
 * IdentifierResolver.java
 * Created: Sep 5, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.openquark.cal.compiler.IdentifierOccurrence.Binding;
import org.openquark.cal.compiler.IdentifierOccurrence.ForeignDescriptor;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.compiler.SourceModel.ArgBindings;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.SourceModel.FieldPattern;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.Parameter;
import org.openquark.cal.compiler.SourceModel.Pattern;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;
import org.openquark.cal.compiler.SourceModel.Name.Module;
import org.openquark.util.Pair;

/**
 * This is a non-instantiatable class containing a collection of inner classes related to the task of resolving identifiers
 * in source code. The public API exposed by this class consists of:
 * 
 * <dl>
 * <dt>The {@link org.openquark.cal.compiler.IdentifierResolver.Context Context} interface
 * 
 * <dd>This public interface encapsulates the notion of an external context for resolving identifiers -
 * when an identifier could not be resolved within the scopes that are lexically declared in the source being analyzed,
 * the context is consulted to see if the name is bound in the context.
 * <p>
 * There are factory methods to construct contexts based on {@link ModuleTypeInfo} and {@link CodeQualificationMap},
 * as well as methods to construct empty contexts, and methods which combine two contexts.
 * 
 * 
 * <dt>The {@link org.openquark.cal.compiler.IdentifierResolver.Visitor Visitor} class
 * 
 * <dd>This is the base visitor that traverses the source model, gathers up the bindings declared therein, and visits each
 * source element with the appropriate symbol table passed in as a visitation argument. This is the base class that should
 * be extended if one wants to analyze a source model with information about which symbols are in scope.
 * <p>
 * The task of gathering up <i>references</i> (non-binding occurrences) is up to the subclass {@link IdentifierOccurrenceFinder}.
 * 
 * 
 * <dt>The {@link org.openquark.cal.compiler.IdentifierResolver.VisitorArgument VisitorArgument} class
 * 
 * <dd>This class encapsulates the visitation argument used with the visitor class. It contains information necessary for
 * resolving identifiers, including the relevant symbol table and {@link LocalFunctionIdentifierGenerator}, as well as any
 * user state which may be passed along.
 * <p>
 * This class is designed as an immutable class, with update methods which produce new copies with the appropriate fields updated.
 * This means that, for example, one does not need to keep an explicitly stack of state (e.g. the symbol table) as one
 * traverses up and down the source model.
 * 
 * 
 * <dt>The {@link org.openquark.cal.compiler.IdentifierResolver.SymbolTable SymbolTable} class
 * 
 * <dd>This family of immutable classes encapsulates a symbol table, representing a particular scope for identifier resolution. Symbol tables
 * can correspond to three kinds of scopes:
 * <ol>
 * <li>top level scope - the scope where top-level (scoped) entities are defined.
 * <li>local scope - a scope where local variables are defined. Local scopes can be nested in other local scopes, or be
 *     directly under the top level scope.
 * <li>root scope - this is the scope above the top level scope, which is associated with an external context.
 * </ol>
 * Symbol tables handle the lookup of module names, qualified names, and unqualified names.
 * 
 * 
 * <dt>The {@link org.openquark.cal.compiler.IdentifierResolver.TypeVariableScope TypeVariableScope} class
 * <dd>This immutable class encapsulates a scope for type variables.
 * </dl>
 *
 * @author Joseph Wong
 */
public final class IdentifierResolver {

    /**
     * This public interface encapsulates the notion of an external context for resolving identifiers -
     * when an identifier could not be resolved within the scopes that are lexically declared in the source being analyzed,
     * the context is consulted to see if the name is bound in the context.
     * <p>
     * There are factory methods in {@link IdentifierResolver} to construct contexts based on {@link ModuleTypeInfo}
     * and {@link CodeQualificationMap}, as well as methods to construct empty contexts, and methods which combine two contexts.
     *
     * @author Joseph Wong
     */
    public static interface Context {
        /**
         * Resolves a module name, which may be partially qualified.
         * @param moduleName the module name. Can *not* be null.
         * @return the resolution result.
         */
        public ModuleNameResolver.ResolutionResult resolveModuleName(Name.Module moduleName);
        /**
         * Resolves an unqualified function or class method name.
         * @param unqualifiedFunctionOrClassMethodName the function or class method name. Can *not* be null.
         * @return the corresponding fully qualified name, or null if the name cannot be resolved.
         */
        public QualifiedName resolveFunctionOrClassMethodName(String unqualifiedFunctionOrClassMethodName);
        /**
         * Resolves an unqualified type constructor name.
         * @param unqualifiedTypeConsName the type constructor name. Can *not* be null.
         * @return the corresponding fully qualified name, or null if the name cannot be resolved.
         */
        public QualifiedName resolveTypeConsName(String unqualifiedTypeConsName);
        /**
         * Resolves an unqualified data constructor name.
         * @param unqualifiedDataConsName the data constructor name. Can *not* be null.
         * @return the corresponding fully qualified name, or null if the name cannot be resolved.
         */
        public QualifiedName resolveDataConsName(String unqualifiedDataConsName);
        /**
         * Resolves an unqualified type class name.
         * @param unqualifiedTypeClassName the type class name. Can *not* be null.
         * @return the corresponding fully qualified name, or null if the name cannot be resolved.
         */
        public QualifiedName resolveTypeClassName(String unqualifiedTypeClassName);
        
        /**
         * @return an external entity visibility checker, or null if there is none associated with this context.
         */
        public ExternalEntityVisibilityChecker getExternalEntityVisibilityChecker();
        
        /**
         * This public interface provides methods for checking the visibility of a given external entity in the context
         * of a particular module.
         *
         * @author Joseph Wong
         */
        public static interface ExternalEntityVisibilityChecker {
            /**
             * Checks whether the named function or class method is visible to the specified module.
             * @param currentModuleName the module from which the reference is made. Can *not* be null.
             * @param qualifiedFunctionOrClassMethodName the fully qualified name of the function or class method to check. Can *not* be null.
             * @return true if the named entity is visible; false otherwise.
             */
            public boolean isFunctionOrClassMethodVisible(ModuleName currentModuleName, QualifiedName qualifiedFunctionOrClassMethodName);
            /**
             * Checks whether the named type constructor is visible to the specified module.
             * @param currentModuleName the module from which the reference is made. Can *not* be null.
             * @param qualifiedTypeConsName the fully qualified name of the type constructor to check. Can *not* be null.
             * @return true if the named entity is visible; false otherwise.
             */
            public boolean isTypeConsVisible(ModuleName currentModuleName, QualifiedName qualifiedTypeConsName);
            /**
             * Checks whether the named data constructor is visible to the specified module.
             * @param currentModuleName the module from which the reference is made. Can *not* be null.
             * @param qualifiedDataConsName the fully qualified name of the data constructor to check. Can *not* be null.
             * @return true if the named entity is visible; false otherwise.
             */
            public boolean isDataConsVisible(ModuleName currentModuleName, QualifiedName qualifiedDataConsName);
            /**
             * Checks whether the named field exists in the named data constructor, and whether the data constructor is visible to the specified module.
             * @param currentModuleName the module from which the reference is made. Can *not* be null.
             * @param qualifiedDataConsName the fully qualified name of the data constructor to check. Can *not* be null.
             * @param fieldName the associated field name of the data constructor. Can *not* be null.
             * @return true if the named entity is visible; false otherwise.
             */
            public boolean isDataConsFieldNameVisible(ModuleName currentModuleName, QualifiedName qualifiedDataConsName, FieldName fieldName);
            /**
             * Checks whether the named type class is visible to the specified module.
             * @param currentModuleName the module from which the reference is made. Can *not* be null.
             * @param qualifiedTypeClassName the fully qualified name of the type class to check. Can *not* be null.
             * @return true if the named entity is visible; false otherwise.
             */
            public boolean isTypeClassVisible(ModuleName currentModuleName, QualifiedName qualifiedTypeClassName);
        }
    }
    
    /**
     * An empty context which binds no names.
     * This class is meant to be instantiated only by {@link IdentifierResolver#makeEmptyContext}.
     *
     * @author Joseph Wong
     */
    private static final class EmptyContext implements Context {
        /**
         * An empty module name resolver for producing module name resolutions.
         */
        private final ModuleNameResolver emptyModuleNameResolver = ModuleNameResolver.make(Collections.<ModuleName>emptySet());
        /**
         * An external entity visibility checker, or null if there is none associated with this context.
         */
        private final ExternalEntityVisibilityChecker visibilityChecker;
        
        /**
         * Constructs an instance of this class.
         * @param visibilityChecker an external entity visibility checker. Can be null.
         */
        EmptyContext(final ExternalEntityVisibilityChecker visibilityChecker) {
            this.visibilityChecker = visibilityChecker;
        }
        
        /** {@inheritDoc} */
        public ModuleNameResolver.ResolutionResult resolveModuleName(final Name.Module moduleName) {
            return emptyModuleNameResolver.resolve(SourceModel.Name.Module.toModuleName(moduleName));
        }
        /** {@inheritDoc} */
        public QualifiedName resolveFunctionOrClassMethodName(final String unqualifiedFunctionOrClassMethodName) {
            return null;
        }
        /** {@inheritDoc} */
        public QualifiedName resolveTypeConsName(final String unqualifiedTypeConsName) {
            return null;
        }
        /** {@inheritDoc} */
        public QualifiedName resolveDataConsName(final String unqualifiedDataConsName) {
            return null;
        }
        /** {@inheritDoc} */
        public QualifiedName resolveTypeClassName(final String unqualifiedTypeClassName) {
            return null;
        }
        /** {@inheritDoc} */
        public ExternalEntityVisibilityChecker getExternalEntityVisibilityChecker() {
            return visibilityChecker;
        }
    }
    
    /**
     * An external context for resolving identifiers based on a {@link ModuleTypeInfo} instance.
     * This is also an external entity visibility checker that works off of the information in the module type info.
     * This class is meant to be instantiated only by {@link IdentifierResolver#makeContext(ModuleTypeInfo)}.
     *
     * @author Joseph Wong
     */
    private static final class ModuleTypeInfoContext implements Context, Context.ExternalEntityVisibilityChecker {
        
        /**
         * The backing module type info, on which name resolutions will be based.
         */
        private final ModuleTypeInfo moduleTypeInfo;
        
        /**
         * Constructs an instance of this class.
         * @param moduleTypeInfo the backing module type info, on which name resolutions will be based.
         */
        ModuleTypeInfoContext(final ModuleTypeInfo moduleTypeInfo) {
            if (moduleTypeInfo == null) {
                throw new NullPointerException();
            }
            this.moduleTypeInfo = moduleTypeInfo;
        }

        /** {@inheritDoc} */
        public ModuleNameResolver.ResolutionResult resolveModuleName(final Name.Module moduleName) {
            return moduleTypeInfo.getModuleNameResolver().resolve(SourceModel.Name.Module.toModuleName(moduleName));
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveFunctionOrClassMethodName(final String unqualifiedFunctionOrClassMethodName) {
            
            final ModuleName importedModule =
                moduleTypeInfo.getModuleOfUsingFunctionOrClassMethod(unqualifiedFunctionOrClassMethodName);
            
            if (importedModule != null) {
                // the name is imported via an import using clause
                return QualifiedName.make(importedModule, unqualifiedFunctionOrClassMethodName);
                
            } else if (moduleTypeInfo.getFunctionOrClassMethod(unqualifiedFunctionOrClassMethodName) != null) {
                // the name is defined in this module
                return QualifiedName.make(moduleTypeInfo.getModuleName(), unqualifiedFunctionOrClassMethodName);
            }
            
            return null;
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveTypeConsName(final String unqualifiedTypeConsName) {
            
            final ModuleName importedModule =
                moduleTypeInfo.getModuleOfUsingTypeConstructor(unqualifiedTypeConsName);
            
            if (importedModule != null) {
                // the name is imported via an import using clause
                return QualifiedName.make(importedModule, unqualifiedTypeConsName);
                
            } else if (moduleTypeInfo.getTypeConstructor(unqualifiedTypeConsName) != null) {
                // the name is defined in this module
                return QualifiedName.make(moduleTypeInfo.getModuleName(), unqualifiedTypeConsName);
            }
            
            return null;
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveDataConsName(final String unqualifiedDataConsName) {
            
            final ModuleName importedModule =
                moduleTypeInfo.getModuleOfUsingDataConstructor(unqualifiedDataConsName);
            
            if (importedModule != null) {
                // the name is imported via an import using clause
                return QualifiedName.make(importedModule, unqualifiedDataConsName);
                
            } else if (moduleTypeInfo.getDataConstructor(unqualifiedDataConsName) != null) {
                // the name is defined in this module
                return QualifiedName.make(moduleTypeInfo.getModuleName(), unqualifiedDataConsName);
            }
            
            return null;
        }

        /** {@inheritDoc} */
        public QualifiedName resolveTypeClassName(final String unqualifiedTypeClassName) {
            
            final ModuleName importedModule =
                moduleTypeInfo.getModuleOfUsingTypeClass(unqualifiedTypeClassName);
            
            if (importedModule != null) {
                // the name is imported via an import using clause
                return QualifiedName.make(importedModule, unqualifiedTypeClassName);
                
            } else if (moduleTypeInfo.getTypeClass(unqualifiedTypeClassName) != null) {
                // the name is defined in this module
                return QualifiedName.make(moduleTypeInfo.getModuleName(), unqualifiedTypeClassName);
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         * @return this instance, which is also an external entity visibility checker.
         */
        public ExternalEntityVisibilityChecker getExternalEntityVisibilityChecker() {
            return this;
        }

        /** {@inheritDoc} */
        public boolean isDataConsVisible(final ModuleName currentModuleName, final QualifiedName qualifiedDataConsName) {
            // we only support checking the visibility of entities referenced in the module associated with the module type info
            if (!moduleTypeInfo.getModuleName().equals(currentModuleName)) {
                throw new IllegalArgumentException();
            }
            return moduleTypeInfo.getVisibleDataConstructor(qualifiedDataConsName) != null;
        }
        
        /** {@inheritDoc} */
        public boolean isDataConsFieldNameVisible(ModuleName currentModuleName, QualifiedName qualifiedDataConsName, FieldName fieldName) {
            // we only support checking the visibility of entities referenced in the module associated with the module type info
            if (!moduleTypeInfo.getModuleName().equals(currentModuleName)) {
                throw new IllegalArgumentException();
            }
            // first make sure the data constructor is visible
            final DataConstructor dataConstructor = moduleTypeInfo.getVisibleDataConstructor(qualifiedDataConsName);
            if (dataConstructor == null) {
                return false;
            } else {
                // then check whether the field actually exists in that data cons
                return dataConstructor.getFieldIndex(fieldName) != -1;
            }
        }

        /** {@inheritDoc} */
        public boolean isFunctionOrClassMethodVisible(final ModuleName currentModuleName, final QualifiedName qualifiedFunctionOrClassMethodName) {
            // we only support checking the visibility of entities referenced in the module associated with the module type info
            if (!moduleTypeInfo.getModuleName().equals(currentModuleName)) {
                throw new IllegalArgumentException();
            }
            return
                moduleTypeInfo.getVisibleClassMethod(qualifiedFunctionOrClassMethodName) != null
                || moduleTypeInfo.getVisibleFunction(qualifiedFunctionOrClassMethodName) != null;
        }

        /** {@inheritDoc} */
        public boolean isTypeClassVisible(final ModuleName currentModuleName, final QualifiedName qualifiedTypeClassName) {
            // we only support checking the visibility of entities referenced in the module associated with the module type info
            if (!moduleTypeInfo.getModuleName().equals(currentModuleName)) {
                throw new IllegalArgumentException();
            }
            return moduleTypeInfo.getVisibleTypeClass(qualifiedTypeClassName) != null;
        }

        /** {@inheritDoc} */
        public boolean isTypeConsVisible(final ModuleName currentModuleName, final QualifiedName qualifiedTypeConsName) {
            // we only support checking the visibility of entities referenced in the module associated with the module type info
            if (!moduleTypeInfo.getModuleName().equals(currentModuleName)) {
                throw new IllegalArgumentException();
            }
            return moduleTypeInfo.getVisibleTypeConstructor(qualifiedTypeConsName) != null;
        }
    }
    
    /**
     * An external context for resolving identifiers based on a {@link CodeQualificationMap} instance.
     * This class is meant to be instantiated only by {@link IdentifierResolver#makeContext(CodeQualificationMap)}.
     *
     * @author Joseph Wong
     */
    private static final class CodeQualificationMapContext implements Context {
        
        /**
         * The module name resolver for resolving module names.
         */
        private final ModuleNameResolver moduleNameResolver;
        
        /**
         * The backing code qualification map, on which name resolutions will be based.
         */
        private final CodeQualificationMap codeQualificationMap;
        
        /**
         * Constructs an instance of this class.
         * @param codeQualificationMap the backing code qualification map, on which name resolutions will be based.
         */
        CodeQualificationMapContext(final CodeQualificationMap codeQualificationMap) {
            if (codeQualificationMap == null) {
                throw new NullPointerException();
            }
            // todo-jowong in the future, we can support custom mappings-based module name resolution as well
            this.moduleNameResolver = ModuleNameResolver.make(Collections.<ModuleName>emptySet());
            this.codeQualificationMap = codeQualificationMap;
        }

        /** {@inheritDoc} */
        public ModuleNameResolver.ResolutionResult resolveModuleName(final Name.Module moduleName) {
            return moduleNameResolver.resolve(SourceModel.Name.Module.toModuleName(moduleName));
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveFunctionOrClassMethodName(final String unqualifiedFunctionOrClassMethodName) {
            return codeQualificationMap.getQualifiedName(
                unqualifiedFunctionOrClassMethodName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveTypeConsName(final String unqualifiedTypeConsName) {
            return codeQualificationMap.getQualifiedName(unqualifiedTypeConsName, Category.TYPE_CONSTRUCTOR);
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveDataConsName(final String unqualifiedDataConsName) {
            return codeQualificationMap.getQualifiedName(unqualifiedDataConsName, Category.DATA_CONSTRUCTOR);
        }

        /** {@inheritDoc} */
        public QualifiedName resolveTypeClassName(final String unqualifiedTypeClassName) {
            return codeQualificationMap.getQualifiedName(unqualifiedTypeClassName, Category.TYPE_CLASS);
        }

        /** {@inheritDoc} */
        public ExternalEntityVisibilityChecker getExternalEntityVisibilityChecker() {
            return null;
        }
    }
    
    /**
     * This is a context combiner which joins two contexts into one, with one acting as the base, and the other acting as
     * the override (the override takes precedence for resolutions).
     *
     * @author Joseph Wong
     */
    private static final class OverrideContext implements Context {
        
        /**
         * The base context.
         */
        private final Context baseContext;
        
        /**
         * The override context - this has precedence over the base context.
         */
        private final Context overrideContext;
        
        /**
         * Constructs an instance of this class.
         * @param baseContext the base context.
         * @param overrideContext the override context - this has precedence over the base context.
         */
        OverrideContext(final Context baseContext, final Context overrideContext) {
            if (baseContext == null || overrideContext == null) {
                throw new NullPointerException();
            }
            this.baseContext = baseContext;
            this.overrideContext = overrideContext;
        }

        /** {@inheritDoc} */
        public ModuleNameResolver.ResolutionResult resolveModuleName(final Name.Module moduleName) {
            final ModuleNameResolver.ResolutionResult override = overrideContext.resolveModuleName(moduleName);
            if (override.isKnownUnambiguous()) {
                return override;
            } else {
                return baseContext.resolveModuleName(moduleName);
            }
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveFunctionOrClassMethodName(final String unqualifiedFunctionOrClassMethodName) {
            final QualifiedName override = overrideContext.resolveFunctionOrClassMethodName(unqualifiedFunctionOrClassMethodName);
            if (override != null) {
                return override;
            } else {
                return baseContext.resolveFunctionOrClassMethodName(unqualifiedFunctionOrClassMethodName);
            }
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveTypeConsName(final String unqualifiedTypeConsName) {
            final QualifiedName override = overrideContext.resolveTypeConsName(unqualifiedTypeConsName);
            if (override != null) {
                return override;
            } else {
                return baseContext.resolveTypeConsName(unqualifiedTypeConsName);
            }
        }
        
        /** {@inheritDoc} */
        public QualifiedName resolveDataConsName(final String unqualifiedDataConsName) {
            final QualifiedName override = overrideContext.resolveDataConsName(unqualifiedDataConsName);
            if (override != null) {
                return override;
            } else {
                return baseContext.resolveDataConsName(unqualifiedDataConsName);
            }
        }

        /** {@inheritDoc} */
        public QualifiedName resolveTypeClassName(final String unqualifiedTypeClassName) {
            final QualifiedName override = overrideContext.resolveTypeClassName(unqualifiedTypeClassName);
            if (override != null) {
                return override;
            } else {
                return baseContext.resolveTypeClassName(unqualifiedTypeClassName);
            }
        }

        /** {@inheritDoc} */
        public ExternalEntityVisibilityChecker getExternalEntityVisibilityChecker() {
            final ExternalEntityVisibilityChecker override = overrideContext.getExternalEntityVisibilityChecker();
            if (override != null) {
                return override;
            } else {
                return baseContext.getExternalEntityVisibilityChecker();
            }
        }
    }
    
    
    /**
     * An exception representing a repeated definition in a particular scope.
     *
     * @author Joseph Wong
     */
    public static final class RepeatedDefinition extends RuntimeException {
        private static final long serialVersionUID = -4578199360030867990L;
        
        /**
         * The name being defined repeatedly.
         */
        private final String name;
        /**
         * The corresponding identifier info.
         */
        private final IdentifierInfo identifierInfo;
        
        /**
         * Constructs an instance of this exception.
         * @param name the name being defined repeatedly.
         * @param identifierInfo the corresponding identifier info.
         */
        RepeatedDefinition(final String name, final IdentifierInfo identifierInfo) {
            super("Repeated definition of the variable " + name);
            this.name = name;
            this.identifierInfo = identifierInfo;
        }

        /**
         * @return the name being defined repeatedly.
         */
        public String getName() {
            return name;
        }

        /**
         * @return the corresponding identifier info.
         */
        public IdentifierInfo getIdentifierInfo() {
            return identifierInfo;
        }
    }
    
    /**
     * A subclass of {@link LinkedHashMap} that maps names to their corresponding bindings, and handling the detection
     * of repeated bindings in the same scope.
     * @param <I> the kind of identifiers handled by this map.
     *
     * @author Joseph Wong
     */
    private static class BindingsMap<I extends IdentifierInfo> extends LinkedHashMap<String, Binding<I>> {
        private static final long serialVersionUID = 1384828040556279035L;
        
        /**
         * Factory method for constructing an instance.
         * @return a new instance of this class.
         */
        static <I extends IdentifierInfo> BindingsMap<I> make() {
            return new BindingsMap<I>();
        }

        /**
         * {@inheritDoc}
         * @throws RepeatedDefinition if the name has already been defined.
         */
        @Override
        public Binding<I> put(final String key, final Binding<I> value) {
            if (containsKey(key)) {
                throw new RepeatedDefinition(key, value.getIdentifierInfo());
            }
            return super.put(key, value);
        }
    }
    
    /**
     * Abstract base class for a family of immutable classes that encapsulates a symbol table,
     * representing a particular scope for identifier resolution. Symbol tables handle the lookup of
     * module names, qualified names, and unqualified names.
     * 
     * @author Joseph Wong
     */
    /*
     * @history
     * 
     * This class is based on the class CALSourceGenerator.LambdaDefiningExprScopeAnalyzer.Scope.
     */
    public static abstract class SymbolTable {
        
        /**
         * Represents a scope above the top level scope that is associated with an external name resolution context.
         *
         * @author Joseph Wong
         */
        public static final class RootScope extends SymbolTable {

            /**
             * The name of the current module. Can *not* be null.
             */
            private final ModuleName currentModuleName;
            
            /**
             * The external context for resolving identifiers. Can *not* be null.
             */
            private final Context context;

            /**
             * Constructs an instance of this class.
             * @param currentModuleName the name of the current module. Can *not* be null.
             * @param context the external context for resolving identifiers. Can *not* be null.
             */
            private RootScope(final ModuleName currentModuleName, final Context context) {
                if (currentModuleName == null || context == null) {
                    throw new NullPointerException();
                }
                this.currentModuleName = currentModuleName;
                this.context = context;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<? extends IdentifierInfo> findVariableDefinition(final String varName) {
                // this is a top-level scope, so just delegate to findTopLevelFunctionOrClassMethodDefinition()
                return findTopLevelFunctionOrClassMethodDefinition(varName);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findTopLevelFunctionOrClassMethodDefinition(final String varName) {
                final QualifiedName resolvedName = context.resolveFunctionOrClassMethodName(varName);
                if (resolvedName != null) {
                    return Binding.External.make(
                        new IdentifierInfo.TopLevel.FunctionOrClassMethod(resolvedName));
                } else {
                    return null;
                }
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findQualifiedFunctionOrClassMethod(final Name.Function functionName, final SymbolTable innermostScope) {
                assert functionName.getModuleName() != null;
                
                final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(functionName.getModuleName());
                if (moduleNameBinding != null) {
                    final QualifiedName qualifiedFunctionName = QualifiedName.make(moduleNameBinding.getIdentifierInfo().getResolvedName(), functionName.getUnqualifiedName());
                    
                    // only return a binding if the named function is visible in this module
                    final boolean isVisible;
                    if (context.getExternalEntityVisibilityChecker() != null) {
                        isVisible = context.getExternalEntityVisibilityChecker().isFunctionOrClassMethodVisible(currentModuleName, qualifiedFunctionName);
                    } else {
                        // no visibility checker, so just assume the name is visible
                        isVisible = true;
                    }
                    
                    if (isVisible) {
                        return Binding.External.make(new IdentifierInfo.TopLevel.FunctionOrClassMethod(qualifiedFunctionName));
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.DataCons> findDataCons(final Name.DataCons dataConsName, final SymbolTable innermostScope) {

                final Name.Module moduleName = dataConsName.getModuleName();
                if (moduleName != null) {
                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(moduleName);

                    if (moduleNameBinding != null) {
                        final QualifiedName qualifiedDataConsName = QualifiedName.make(moduleNameBinding.getIdentifierInfo().getResolvedName(), dataConsName.getUnqualifiedName());
                        
                        // only return a binding if the named data cons is visible in this module
                        final boolean isVisible;
                        if (context.getExternalEntityVisibilityChecker() != null) {
                            isVisible = context.getExternalEntityVisibilityChecker().isDataConsVisible(currentModuleName, qualifiedDataConsName);
                        } else {
                            // no visibility checker, so just assume the name is visible
                            isVisible = true;
                        }

                        if (isVisible) {
                            return Binding.External.make(new IdentifierInfo.TopLevel.DataCons(qualifiedDataConsName));
                        } else {
                            return null;
                        }
                    }

                } else {
                    final QualifiedName resolvedDataConsName = context.resolveDataConsName(dataConsName.getUnqualifiedName());

                    if (resolvedDataConsName != null) {
                        return Binding.External.make(
                            new IdentifierInfo.TopLevel.DataCons(resolvedDataConsName));
                    }
                }
                
                return null;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.DataConsFieldName> findDataConsFieldName(final Name.DataCons dataConsName, final Name.Field fieldName, final SymbolTable innermostScope) {
                final Binding<IdentifierInfo.TopLevel.DataCons> dataConsBinding = innermostScope.findDataCons(dataConsName, innermostScope);
                if (dataConsBinding != null) {
                    final IdentifierInfo.TopLevel.DataCons dataConsIdentifier = dataConsBinding.getIdentifierInfo();
                    
                    // only return a binding if the named data cons field is visible in this module
                    final boolean isVisible;
                    if (context.getExternalEntityVisibilityChecker() != null) {
                        isVisible = context.getExternalEntityVisibilityChecker().isDataConsFieldNameVisible(currentModuleName, dataConsIdentifier.getResolvedName(), fieldName.getName());
                    } else {
                        // no visibility checker, so just assume the name is visible
                        isVisible = true;
                    }

                    if (isVisible) {
                        return Binding.External.make(
                            new IdentifierInfo.DataConsFieldName(
                                fieldName.getName(), Collections.singletonList(dataConsIdentifier)));
                    } else {
                        return null;
                    }
                }
                
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.TypeCons> findTypeCons(final Name.TypeCons typeConsName, final SymbolTable innermostScope) {

                final Name.Module moduleName = typeConsName.getModuleName();
                if (moduleName != null) {
                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(moduleName);

                    if (moduleNameBinding != null) {
                        final QualifiedName qualifiedTypeConsName = QualifiedName.make(moduleNameBinding.getIdentifierInfo().getResolvedName(), typeConsName.getUnqualifiedName());

                        // only return a binding if the named type cons is visible in this module
                        final boolean isVisible;
                        if (context.getExternalEntityVisibilityChecker() != null) {
                            isVisible = context.getExternalEntityVisibilityChecker().isTypeConsVisible(currentModuleName, qualifiedTypeConsName);
                        } else {
                            // no visibility checker, so just assume the name is visible
                            isVisible = true;
                        }

                        if (isVisible) {
                            return Binding.External.make(new IdentifierInfo.TopLevel.TypeCons(qualifiedTypeConsName));
                        } else {
                            return null;
                        }
                    }

                } else {
                    final QualifiedName resolvedTypeConsName = context.resolveTypeConsName(typeConsName.getUnqualifiedName());

                    if (resolvedTypeConsName != null) {
                        return Binding.External.make(
                            new IdentifierInfo.TopLevel.TypeCons(resolvedTypeConsName));
                    }
                }
                
                return null;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.TypeClass> findTypeClass(final Name.TypeClass typeClassName, final SymbolTable innermostScope) {

                final Name.Module moduleName = typeClassName.getModuleName();
                if (moduleName != null) {
                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(moduleName);

                    if (moduleNameBinding != null) {
                        final QualifiedName qualifiedTypeClassName = QualifiedName.make(moduleNameBinding.getIdentifierInfo().getResolvedName(), typeClassName.getUnqualifiedName());

                        // only return a binding if the named type class is visible in this module
                        final boolean isVisible;
                        if (context.getExternalEntityVisibilityChecker() != null) {
                            isVisible = context.getExternalEntityVisibilityChecker().isTypeClassVisible(currentModuleName, qualifiedTypeClassName);
                        } else {
                            // no visibility checker, so just assume the name is visible
                            isVisible = true;
                        }

                        if (isVisible) {
                            return Binding.External.make(new IdentifierInfo.TopLevel.TypeClass(qualifiedTypeClassName));
                        } else {
                            return null;
                        }
                    }

                } else {
                    final QualifiedName resolvedTypeClassName = context.resolveTypeClassName(typeClassName.getUnqualifiedName());

                    if (resolvedTypeClassName != null) {
                        return Binding.External.make(
                            new IdentifierInfo.TopLevel.TypeClass(resolvedTypeClassName));
                    }
                }
                
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.Module> resolveMaybeModuleName(final Name.Module moduleName) {
                if (moduleName == null) {
                    return null;
                }
                
                final ModuleNameResolver.ResolutionResult resolution = context.resolveModuleName(moduleName);
                if (resolution.isKnownUnambiguous()) {
                    return Binding.External.make(
                        new IdentifierInfo.Module(resolution.getResolvedModuleName()));
                } else {
                    return null;
                }
            }
        }

        /**
         * Represents the scope where top-level (scoped) entities are defined.
         *
         * @author Joseph Wong
         */
        public static final class TopLevelScope extends SymbolTable {
            /**
             * The parent scope.
             */
            private final SymbolTable parent;

            /**
             * The binding for the name of the current module.
             */
            private final Binding<IdentifierInfo.Module> currentModuleNameBinding;
            
            /**
             * A map from unqualified names to bindings of functions and class methods.
             * (The source ranges stored may be null.)
             */
            private final BindingsMap<IdentifierInfo.TopLevel.FunctionOrClassMethod> functionAndClassMethodBindings;

            /**
             * A map from unqualified names to bindings of type constructors.
             * (The source ranges stored may be null.)
             */
            private final BindingsMap<IdentifierInfo.TopLevel.TypeCons> typeConsBindings;

            /**
             * A map from unqualified names to bindings of data constructors.
             * (The source ranges stored may be null.)
             */
            private final BindingsMap<IdentifierInfo.TopLevel.DataCons> dataConsBindings;
            
            /**
             * A map from names to bindings of data constructors.
             * The name with which we associate a data cons field name is "<dataConsName>.<fieldName>".
             * (The source ranges stored may be null.)
             */
            private final BindingsMap<IdentifierInfo.DataConsFieldName> dataConsFieldNameBindings;

            /**
             * A map from unqualified names to bindings of type classes.
             * (The source ranges stored may be null.)
             */
            private final BindingsMap<IdentifierInfo.TopLevel.TypeClass> typeClassBindings;
            
            /**
             * An ordered set of the imported module names.
             */
            private final LinkedHashSet<ModuleName> importedModuleNames;
            
            /**
             * The module name resolver built upon the imported modules and the current module name.
             */
            private final ModuleNameResolver moduleNameResolver;

            /**
             * Constructs an instance of this class.
             * @param parent the parent scope.
             * @param currentModuleNameBinding the binding for the name of the current module.
             * @param functionAndClassMethodBindings a map from unqualified names to bindings of functions and class methods.
             * @param typeConsBindings a map from unqualified names to bindings of type constructors.
             * @param dataConsBindings a map from unqualified names to bindings of data constructors.
             * @param dataConsFieldNameBindings a map from names to bindings of data constructors.
             * The name with which we associate a data cons field name is "<dataConsName>.<fieldName>".
             * @param typeClassBindings a map from unqualified names to bindings of type classes.
             * @param importedModuleNames an ordered collection of the imported module names.
             */
            TopLevelScope(
                final SymbolTable parent,
                final Binding<IdentifierInfo.Module> currentModuleNameBinding,
                final BindingsMap<IdentifierInfo.TopLevel.FunctionOrClassMethod> functionAndClassMethodBindings,
                final BindingsMap<IdentifierInfo.TopLevel.TypeCons> typeConsBindings,
                final BindingsMap<IdentifierInfo.TopLevel.DataCons> dataConsBindings,
                final BindingsMap<IdentifierInfo.DataConsFieldName> dataConsFieldNameBindings,
                final BindingsMap<IdentifierInfo.TopLevel.TypeClass> typeClassBindings,
                final Collection<ModuleName> importedModuleNames) {
                
                if (parent == null || currentModuleNameBinding == null || functionAndClassMethodBindings == null || typeConsBindings == null
                        || dataConsBindings == null || dataConsFieldNameBindings == null || typeClassBindings == null || importedModuleNames == null) {
                    throw new NullPointerException();
                }
                this.parent = parent;
                this.currentModuleNameBinding = currentModuleNameBinding;
                this.functionAndClassMethodBindings = functionAndClassMethodBindings;
                this.typeConsBindings = typeConsBindings;
                this.dataConsBindings = dataConsBindings;
                this.dataConsFieldNameBindings = dataConsFieldNameBindings;
                this.typeClassBindings = typeClassBindings;
                this.importedModuleNames = new LinkedHashSet<ModuleName>(importedModuleNames);
                this.moduleNameResolver = ModuleNameResolver.make(currentModuleNameBinding.getIdentifierInfo().getResolvedName(), this.importedModuleNames);
            }
            
            /**
             * @return the parent scope.
             */
            public SymbolTable getParent() {
                return parent;
            }
            
            /**
             * @return the binding for the name of the current module. 
             */
            public Binding<IdentifierInfo.Module> getCurrentModuleNameBinding() {
                return currentModuleNameBinding;
            }

            /**
             * @return an iterable collection of the function and class method bindings.
             */
            public Iterable<Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod>> getFunctionAndClassMethodBindings() {
                return Collections.unmodifiableCollection(functionAndClassMethodBindings.values());
            }

            /**
             * @return an iterable collection of the type constructor bindings.
             */
            public Iterable<Binding<IdentifierInfo.TopLevel.TypeCons>> getTypeConsBindings() {
                return Collections.unmodifiableCollection(typeConsBindings.values());
            }

            /**
             * @return an iterable collection of the data constructor bindings.
             */
            public Iterable<Binding<IdentifierInfo.TopLevel.DataCons>> getDataConsBindings() {
                return Collections.unmodifiableCollection(dataConsBindings.values());
            }

            /**
             * @return an iterable collection of the data constructor field name bindings.
             */
            public Iterable<Binding<IdentifierInfo.DataConsFieldName>> getDataConsFieldNameBindings() {
                return Collections.unmodifiableCollection(dataConsFieldNameBindings.values());
            }

            /**
             * @return an iterable collection of the type class bindings.
             */
            public Iterable<Binding<IdentifierInfo.TopLevel.TypeClass>> getTypeClassBindings() {
                return Collections.unmodifiableCollection(typeClassBindings.values());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<? extends IdentifierInfo> findVariableDefinition(final String varName) {
                // this is a top-level scope, so just delegate to findTopLevelFunctionOrClassMethodDefinition()
                return findTopLevelFunctionOrClassMethodDefinition(varName);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findTopLevelFunctionOrClassMethodDefinition(final String varName) {
                // check this scope first
                final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> binding = functionAndClassMethodBindings.get(varName);
                if (binding != null) {
                    return binding;
                }

                // unqualified name not found in this scope, so delegate to parent
                return parent.findTopLevelFunctionOrClassMethodDefinition(varName);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findQualifiedFunctionOrClassMethod(final Name.Function functionName, final SymbolTable innermostScope) {
                assert functionName.getModuleName() != null;
                
                final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(functionName.getModuleName());
                if (moduleNameBinding != null) {
                    if (moduleNameBinding.getIdentifierInfo().getResolvedName().equals(currentModuleNameBinding.getIdentifierInfo().getResolvedName())) {
                        // check this scope first
                        final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> binding = functionAndClassMethodBindings.get(functionName.getUnqualifiedName());
                        if (binding != null) {
                            return binding;
                        }
                    }
                    
                    if (importedModuleNames.contains(moduleNameBinding.getIdentifierInfo().getResolvedName())) {
                        // defined in an imported module, so delegate to parent
                        return parent.findQualifiedFunctionOrClassMethod(functionName, innermostScope);
                    }
                }
                
                // a qualified name whose module name is not in the list of imported module names, so the entity cannot be visible
                // so just return null
                return null;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.DataCons> findDataCons(final Name.DataCons dataConsName, final SymbolTable innermostScope) {
                
                boolean inCurrentModule = false;
                if (dataConsName.getModuleName() != null) {

                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(dataConsName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (moduleNameBinding.getIdentifierInfo().getResolvedName().equals(currentModuleNameBinding.getIdentifierInfo().getResolvedName())) {
                            inCurrentModule = true;
                        }
                    }
                } else {
                    inCurrentModule = true;
                }

                if (inCurrentModule) {
                    final Binding<IdentifierInfo.TopLevel.DataCons> binding = dataConsBindings.get(dataConsName.getUnqualifiedName());
                    if (binding != null) {
                        return binding;
                    }
                }
                
                if (dataConsName.getModuleName() != null) {
                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(dataConsName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (importedModuleNames.contains(moduleNameBinding.getIdentifierInfo().getResolvedName())) {
                            // defined in an imported module, so delegate to parent
                            return parent.findDataCons(dataConsName, innermostScope);
                        }
                    }
                } else {
                    // unqualified name not found in this scope, so delegate to parent
                    return parent.findDataCons(dataConsName, innermostScope);
                }
                
                // a qualified name whose module name is not in the list of imported module names, so the entity cannot be visible
                // so just return null
                return null;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.DataConsFieldName> findDataConsFieldName(final Name.DataCons dataConsName, final Name.Field fieldName, final SymbolTable innermostScope) {
                
                boolean inCurrentModule = false;
                if (dataConsName.getModuleName() != null) {

                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(dataConsName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (moduleNameBinding.getIdentifierInfo().getResolvedName().equals(currentModuleNameBinding.getIdentifierInfo().getResolvedName())) {
                            inCurrentModule = true;
                        }
                    }
                } else {
                    // the data cons has to be defined in the current module, not just visible as an unqualified name
                    // in the current module (imported via an import using clause)
                    final Binding<IdentifierInfo.TopLevel.DataCons> dataConsBinding = dataConsBindings.get(dataConsName.getUnqualifiedName());
                    if (dataConsBinding instanceof Binding.Definition) {
                        inCurrentModule = true;
                    }
                }
                
                if (inCurrentModule) {
                    final String key = dataConsName.getUnqualifiedName() + "." + fieldName.getName().getCalSourceForm();
                    
                    final Binding<IdentifierInfo.DataConsFieldName> binding = dataConsFieldNameBindings.get(key);
                    if (binding != null) {
                        return binding;
                    }
                }
                
                if (dataConsName.getModuleName() != null) {
                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(dataConsName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (importedModuleNames.contains(moduleNameBinding.getIdentifierInfo().getResolvedName())) {
                            // defined in an imported module, so delegate to parent
                            return parent.findDataConsFieldName(dataConsName, fieldName, innermostScope);
                        }
                    }
                } else {
                    // unqualified name not found in this scope, so delegate to parent
                    return parent.findDataConsFieldName(dataConsName, fieldName, innermostScope);
                }
                
                // a qualified name whose module name is not in the list of imported module names, so the entity cannot be visible
                // so just return null
                return null;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.TypeCons> findTypeCons(final Name.TypeCons typeConsName, final SymbolTable innermostScope) {
                
                boolean inCurrentModule = false;
                if (typeConsName.getModuleName() != null) {

                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(typeConsName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (moduleNameBinding.getIdentifierInfo().getResolvedName().equals(currentModuleNameBinding.getIdentifierInfo().getResolvedName())) {
                            inCurrentModule = true;
                        }
                    }
                } else {
                    inCurrentModule = true;
                }
                
                if (inCurrentModule) {
                    final Binding<IdentifierInfo.TopLevel.TypeCons> binding = typeConsBindings.get(typeConsName.getUnqualifiedName());
                    if (binding != null) {
                        return binding;
                    }
                }
                
                if (typeConsName.getModuleName() != null) {
                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(typeConsName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (importedModuleNames.contains(moduleNameBinding.getIdentifierInfo().getResolvedName())) {
                            // defined in an imported module, so delegate to parent
                            return parent.findTypeCons(typeConsName, innermostScope);
                        }
                    }
                } else {
                    // unqualified name not found in this scope, so delegate to parent
                    return parent.findTypeCons(typeConsName, innermostScope);
                }
                
                // a qualified name whose module name is not in the list of imported module names, so the entity cannot be visible
                // so just return null
                return null;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.TypeClass> findTypeClass(final Name.TypeClass typeClassName, final SymbolTable innermostScope) {
                
                boolean inCurrentModule = false;
                if (typeClassName.getModuleName() != null) {

                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(typeClassName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (moduleNameBinding.getIdentifierInfo().getResolvedName().equals(currentModuleNameBinding.getIdentifierInfo().getResolvedName())) {
                            inCurrentModule = true;
                        }
                    }
                } else {
                    inCurrentModule = true;
                }
                
                if (inCurrentModule) {
                    final Binding<IdentifierInfo.TopLevel.TypeClass> binding = typeClassBindings.get(typeClassName.getUnqualifiedName());
                    if (binding != null) {
                        return binding;
                    }
                }
                
                if (typeClassName.getModuleName() != null) {
                    final IdentifierOccurrence<IdentifierInfo.Module> moduleNameBinding = innermostScope.resolveMaybeModuleName(typeClassName.getModuleName());
                    if (moduleNameBinding != null) {
                        if (importedModuleNames.contains(moduleNameBinding.getIdentifierInfo().getResolvedName())) {
                            // defined in an imported module, so delegate to parent
                            return parent.findTypeClass(typeClassName, innermostScope);
                        }
                    }
                } else {
                    // unqualified name not found in this scope, so delegate to parent
                    return parent.findTypeClass(typeClassName, innermostScope);
                }
                
                // a qualified name whose module name is not in the list of imported module names, so the entity cannot be visible
                // so just return null
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.Module> resolveMaybeModuleName(final Name.Module moduleName) {
                if (moduleName == null) {
                    return null;
                }
                
                final ModuleNameResolver.ResolutionResult resolution = moduleNameResolver.resolve(SourceModel.Name.Module.toModuleName(moduleName));
                if (resolution.isKnownUnambiguous()) {
                    if (resolution.getResolvedModuleName().equals(currentModuleNameBinding.getIdentifierInfo().getResolvedName())) {
                        return currentModuleNameBinding;
                    } else {
                        return Binding.External.make(
                            new IdentifierInfo.Module(resolution.getResolvedModuleName()));
                    }
                } else if (resolution.isUnknown()) {
                    // cannot find the name in this scope, so delegate to parent
                    return parent.resolveMaybeModuleName(moduleName);
                } else {
                    // the name is ambiguous, so we return null in this case
                    return null;
                }
            }
        }
        
        /**
         * Represents a scope where local variables are defined. Local scopes can be nested in other local scopes, or be
         * directly under the top level scope.
         *
         * @author Joseph Wong
         */
        public static final class LocalScope extends SymbolTable {
            /**
             * The parent scope.
             */
            private final SymbolTable parent;
            
            /**
             * A map from unqualified names to bindings of local variables.
             * (The source ranges stored may be null.)
             */
            private final BindingsMap<IdentifierInfo.Local> bindings;

            /**
             * Constructs an instance of this class.
             * @param parent the parent scope.
             * @param bindings a map from unqualified names to bindings of local variables.
             */
            private LocalScope(final SymbolTable parent, final BindingsMap<IdentifierInfo.Local> bindings) {
                if (parent == null) {
                    throw new NullPointerException();
                }
                this.parent = parent;
                this.bindings = bindings;
            }
            
            /**
             * @return the parent scope.
             */
            public SymbolTable getParent() {
                return parent;
            }

            /**
             * @return a map from unqualified names to bindings of local variables.
             */
            public Iterable<Binding<IdentifierInfo.Local>> getBindings() {
                return Collections.unmodifiableCollection(bindings.values());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<? extends IdentifierInfo> findVariableDefinition(final String varName) {
                // check this scope first
                final Binding<IdentifierInfo.Local> binding = bindings.get(varName);
                if (binding != null) {
                    return binding;
                }

                // cannot find the name in this scope, so delegate to parent
                return parent.findVariableDefinition(varName);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findTopLevelFunctionOrClassMethodDefinition(final String varName) {
                // this is not a top-level scope, so delegate to parent
                return parent.findTopLevelFunctionOrClassMethodDefinition(varName);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findQualifiedFunctionOrClassMethod(final Name.Function functionName, final SymbolTable innermostScope) {
                return parent.findQualifiedFunctionOrClassMethod(functionName, innermostScope);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.DataCons> findDataCons(final Name.DataCons dataConsName, final SymbolTable innermostScope) {
                return parent.findDataCons(dataConsName, innermostScope);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.DataConsFieldName> findDataConsFieldName(final Name.DataCons dataConsName, final Name.Field fieldName, final SymbolTable innermostScope) {
                return parent.findDataConsFieldName(dataConsName, fieldName, innermostScope);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.TypeCons> findTypeCons(final Name.TypeCons typeConsName, final SymbolTable innermostScope) {
                return parent.findTypeCons(typeConsName, innermostScope);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.TopLevel.TypeClass> findTypeClass(final Name.TypeClass typeClassName, final SymbolTable innermostScope) {
                return parent.findTypeClass(typeClassName, innermostScope);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Binding<IdentifierInfo.Module> resolveMaybeModuleName(final Name.Module moduleName) {
                return parent.resolveMaybeModuleName(moduleName);
            }
        }
        
        /**
         * Factory method for making a root scope.
         * @param currentModuleName the name of the current module. Can *not* be null.
         * @param context the external context for resolving identifiers. Can *not* be null.
         * @return a root scope instance.
         */
        public static RootScope makeRoot(final ModuleName currentModuleName, final Context context) {
            return new RootScope(currentModuleName, context);
        }
        
        /** Private constructor. */
        private SymbolTable() {}

        /**
         * Looks up the binding for an unqualified variable name.
         * @param varName the variable name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        abstract Binding<? extends IdentifierInfo> findVariableDefinition(String varName);

        /**
         * Looks up the binding for an unqualified name that must refer to a top-level function or class method.
         * An example of such a case is a CALDoc short-form function reference that is unqualified.
         * @param varName the variable name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        abstract Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findTopLevelFunctionOrClassMethodDefinition(String varName);
        
        /**
         * Looks up the binding for a qualified function/class method name.
         * @param functionName the function/class method name.
         * @param innermostScope the innermost scope which is currently being analyzed.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /*
         * @discussion
         * 
         * The use of the innermost scope is necessary, as often the task of resolution is a 2-step process:
         * 1) first resolve the module name
         * 2) then resolve the unqualified name
         * It may be the case that the module name is not resolvable in the current scope (e.g. the root scope),
         * and in fact needs to be resolved by a lower scope (e.g. the top level scope) - thus the need to go from
         * the bottom up via the innermost scope *while* in the context of a particular, higher scope.
         */
        abstract Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findQualifiedFunctionOrClassMethod(Name.Function functionName, SymbolTable innermostScope);
        
        /**
         * Looks up the binding for a qualified function/class method name.
         * @param functionName the function/class method name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /*
         * @discussion
         * 
         * Passes 'this' as the innermost scope, as this is the entry point for clients to use for lookups.
         */
        final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findQualifiedFunctionOrClassMethod(final Name.Function functionName) {
            return findQualifiedFunctionOrClassMethod(functionName, this);
        }
        
        /**
         * Looks up the binding for a potentially qualified function/method/variable name.
         * @param functionName the function/method/variable name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        final Binding<? extends IdentifierInfo> findFunction(final Name.Function functionName) {
            if (functionName.getModuleName() == null) {
                // unqualified, so go through findVariableDefinition()
                return findVariableDefinition(functionName.getUnqualifiedName());
            } else {
                // qualified, so go through findQualifiedFunctionOrClassMethod()
                return findQualifiedFunctionOrClassMethod(functionName, this);
            }
        }
        
        /**
         * Looks up the binding for a qualified function/method name.
         * @param functionName the function/method name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        final Binding<IdentifierInfo.TopLevel.FunctionOrClassMethod> findTopLevelFunctionOrClassMethodDefinition(final Name.Function functionName) {
            if (functionName.getModuleName() == null) {
                // unqualified, so go through findTopLevelFunctionOrClassMethodDefinition()
                return findTopLevelFunctionOrClassMethodDefinition(functionName.getUnqualifiedName());
            } else {
                // qualified, so go through findQualifiedFunctionOrClassMethod()
                return findQualifiedFunctionOrClassMethod(functionName, this);
            }
        }
        
        /**
         * Looks up the binding for a potentially qualified data cons name.
         * @param dataConsName the data cons name.
         * @param innermostScope the innermost scope which is currently being analyzed.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        abstract Binding<IdentifierInfo.TopLevel.DataCons> findDataCons(Name.DataCons dataConsName, SymbolTable innermostScope);
        
        /**
         * Looks up the binding for a potentially qualified data cons name.
         * @param dataConsName the data cons name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        final Binding<IdentifierInfo.TopLevel.DataCons> findDataCons(final Name.DataCons dataConsName) {
            return findDataCons(dataConsName, this);
        }
        
        /**
         * Looks up the binding for a field associated with a potentially qualified data cons name.
         * @param dataConsName the data cons name.
         * @param fieldName the field name.
         * @param innermostScope the innermost scope which is currently being analyzed.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        abstract Binding<IdentifierInfo.DataConsFieldName> findDataConsFieldName(Name.DataCons dataConsName, Name.Field fieldName, SymbolTable innermostScope);
        
        /**
         * Looks up the binding for a field associated with a potentially qualified data cons name.
         * @param dataConsName the data cons name.
         * @param fieldName the field name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        final Binding<IdentifierInfo.DataConsFieldName> findDataConsFieldName(final Name.DataCons dataConsName, final Name.Field fieldName) {
            return findDataConsFieldName(dataConsName, fieldName, this);
        }

        /**
         * Looks up the binding for a potentially qualified type cons name.
         * @param typeConsName the type cons name.
         * @param innermostScope the innermost scope which is currently being analyzed.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        abstract Binding<IdentifierInfo.TopLevel.TypeCons> findTypeCons(Name.TypeCons typeConsName, SymbolTable innermostScope);

        /**
         * Looks up the binding for a potentially qualified type cons name.
         * @param typeConsName the type cons name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        final Binding<IdentifierInfo.TopLevel.TypeCons> findTypeCons(final Name.TypeCons typeConsName) {
            return findTypeCons(typeConsName, this);
        }

        /**
         * Looks up the binding for a potentially qualified type class name.
         * @param typeClassName the type class name.
         * @param innermostScope the innermost scope which is currently being analyzed.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        abstract Binding<IdentifierInfo.TopLevel.TypeClass> findTypeClass(Name.TypeClass typeClassName, SymbolTable innermostScope);

        /**
         * Looks up the binding for a potentially qualified type class name.
         * @param typeClassName the type class name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        /* @discussion see above */
        final Binding<IdentifierInfo.TopLevel.TypeClass> findTypeClass(final Name.TypeClass typeClassName) {
            return findTypeClass(typeClassName, this);
        }
        
        /**
         * Looks up the binding for a potentially partially qualified module name, which *may* be null.
         * @param moduleName the module name. Can be null.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        abstract Binding<IdentifierInfo.Module> resolveMaybeModuleName(Name.Module moduleName);
        
        /**
         * Looks up the binding for a potentially partially qualified module name, which *may* be null.
         * @param moduleName the module name. Can be null.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        final Binding<IdentifierInfo.Module> resolveMaybeModuleName(final ModuleName moduleName) {
            return resolveMaybeModuleName(Name.Module.make(moduleName));
        }
        
        /**
         * Looks up the binding for a potentially qualified constructor name without context (as appearing in CALDoc)
         * @param name the name without context.
         * @return the corresponding binding, or null if the name is irresolvable (according to the special rules of resolving such names).
         */
        final Binding<? extends IdentifierInfo> findConsNameWithoutContext(final Name.WithoutContextCons name) {
            
            // try to resolve the name under all contexts
            final Binding<IdentifierInfo.TopLevel.DataCons> asDataCons =
                findDataCons(Name.DataCons.make(name.getModuleName(), name.getUnqualifiedName()));
            
            final Binding<IdentifierInfo.TopLevel.TypeCons> asTypeCons =
                findTypeCons(Name.TypeCons.make(name.getModuleName(), name.getUnqualifiedName()));
            
            final Binding<IdentifierInfo.TopLevel.TypeClass> asTypeClass =
                findTypeClass(Name.TypeClass.make(name.getModuleName(), name.getUnqualifiedName()));
            
            final Binding<IdentifierInfo.Module> asModuleName =
                resolveMaybeModuleName(Name.Module.make(ModuleName.make(name.toSourceText())));
            
            final List<Binding<? extends IdentifierInfo>> bindingsList =
                new ArrayList<Binding<? extends IdentifierInfo>>();
            
            if (asDataCons != null) {
                bindingsList.add(asDataCons);
            }
            if (asTypeCons != null) {
                bindingsList.add(asTypeCons);
            }
            if (asTypeClass != null) {
                bindingsList.add(asTypeClass);
            }
            if (asModuleName != null) {
                bindingsList.add(asModuleName);
            }
            
            // The name is unambiguously resolved if and only if there is exactly one resolution
            if (bindingsList.size() != 1) {
                return null;
            } else {
                return bindingsList.get(0);
            }
        }
    }
    
    /**
     * An immutable class representing a scope for type variables. A type variable scope can nest within another type variable
     * scope. For example, the type variable associated with the type class name in the class definition is in scope within
     * the entire definition (including the class methods), while other type variables that appear only within a particular
     * class method's definition are local to that method.
     *
     * @author Joseph Wong
     */
    public static final class TypeVariableScope {
        
        /**
         * The parent scope. Can be null if this is the top-most type variable scope.
         */
        private final TypeVariableScope parent;
        
        /**
         * A map from type variables names to their corresponding bindings. Can *not* be null.
         */
        private final LinkedHashMap<String, Binding<IdentifierInfo.TypeVariable>> typeVarBindings;

        /**
         * Constructs an instance of this class.
         * @param parent the parent scope. Can be null if this is the top-most type variable scope.
         * @param typeVarBindings a map from type variables names to their corresponding bindings. Can *not* be null.
         */
        TypeVariableScope(final TypeVariableScope parent, final LinkedHashMap<String, Binding<IdentifierInfo.TypeVariable>> typeVarBindings) {
            super();
            this.parent = parent;
            if (typeVarBindings == null) {
                throw new NullPointerException();
            }
            this.typeVarBindings = typeVarBindings;
        }

        /**
         * @return the parent scope. Can be null if this is the top-most type variable scope.
         */
        public TypeVariableScope getParent() {
            return parent;
        }

        /**
         * @return an iterable collection of the type variable bindings.
         */
        public Iterable<Binding<IdentifierInfo.TypeVariable>> getBindings() {
            return Collections.unmodifiableCollection(typeVarBindings.values());
        }
        
        /**
         * Looks up the binding for a type variable name.
         * @param typeVarName the type variable name.
         * @return the corresponding binding, or null if the name is irresolvable.
         */
        Binding<IdentifierInfo.TypeVariable> findTypeVar(final String typeVarName) {
            final Binding<IdentifierInfo.TypeVariable> binding = typeVarBindings.get(typeVarName);
            if (binding != null) {
                return binding;
            } else if (parent != null) {
                return parent.findTypeVar(typeVarName);
            } else {
                return null;
            }
        }
        
        /**
         * Factory method for constructing a new scope containing bindings for type variables in a type signature,
         * to be used for resolution of type variables within the scope.
         * @param parent the parent scope.
         * @param sourceElement the source element corresponding to the new scope.
         * @param currentModuleName the current module name.
         * @return the new scope.
         */
        static TypeVariableScope newScope(final TypeVariableScope parent, final TypeSignature sourceElement, final ModuleName currentModuleName) {
            return newScopeInternal(parent, new SourceElement[] {sourceElement}, currentModuleName);
        }
        
        /**
         * Factory method for constructing a new scope containing bindings for type variables in a algebraic type definition (not including the data constructor definitions),
         * to be used for resolution of type variables within the scope.
         * @param parent the parent scope.
         * @param sourceElement the source element corresponding to the new scope.
         * @param currentModuleName the current module name.
         * @return the new scope.
         */
        static TypeVariableScope newScope(final TypeVariableScope parent, final TypeConstructorDefn.AlgebraicType sourceElement, final ModuleName currentModuleName) {
            return newScopeInternal(parent, sourceElement.getTypeParameters(), currentModuleName);
        }
        
        /**
         * Factory method for constructing a new scope containing bindings for type variables in a data constructor definition,
         * to be used for resolution of type variables within the scope.
         * @param parent the parent scope.
         * @param sourceElement the source element corresponding to the new scope.
         * @param currentModuleName the current module name.
         * @return the new scope.
         */
        static TypeVariableScope newScope(final TypeVariableScope parent, final TypeConstructorDefn.AlgebraicType.DataConsDefn sourceElement, final ModuleName currentModuleName) {
            return newScopeInternal(parent, new SourceElement[] {sourceElement}, currentModuleName);
        }
        
        /**
         * Factory method for constructing a new scope containing bindings for type variables in a type class definition (not including the method declarations),
         * to be used for resolution of type variables within the scope.
         * @param parent the parent scope.
         * @param sourceElement the source element corresponding to the new scope.
         * @param currentModuleName the current module name.
         * @return the new scope.
         */
        static TypeVariableScope newScope(final TypeVariableScope parent, final TypeClassDefn sourceElement, final ModuleName currentModuleName) {
            return newScopeInternal(parent, new SourceElement[] {sourceElement.getTypeVar()}, currentModuleName);
        }
        
        /**
         * Factory method for constructing a new scope containing bindings for type variables in an instance definition,
         * to be used for resolution of type variables within the scope.
         * @param parent the parent scope.
         * @param sourceElement the source element corresponding to the new scope.
         * @param currentModuleName the current module name.
         * @return the new scope.
         */
        static TypeVariableScope newScope(final TypeVariableScope parent, final InstanceDefn sourceElement, final ModuleName currentModuleName) {
            return newScopeInternal(parent, new SourceElement[] {sourceElement.getInstanceTypeCons()}, currentModuleName);
        }
        
        /**
         * Internal helper method for constructing a new scope based on the type variables in the specified source elements.
         * @param parent the parent scope.
         * @param sourceElements the source elements containing type variables to be processed.
         * @param currentModuleName the current module name.
         * @return the new scope.
         */
        private static TypeVariableScope newScopeInternal(final TypeVariableScope parent, final SourceElement[] sourceElements, final ModuleName currentModuleName) {
            final LinkedHashMap<String, Binding<IdentifierInfo.TypeVariable>> typeVarBindings = new LinkedHashMap<String, Binding<IdentifierInfo.TypeVariable>>();
            
            // For each source element, process the type variables therein
            for (final SourceElement sourceElement : sourceElements) {
                sourceElement.accept(
                    new SourceModelTraverser<Void, Void>() {
                        @Override
                        public Void visit_Name_TypeVar(final Name.TypeVar var, final Void arg) {
                            final String typeVarName = var.getName();
                            if (parent.findTypeVar(typeVarName) == null) {
                                // If the type var has not been bound, it is the first instance of the type var name
                                // and the occurrence is considered the "binding" (or canonical occurrence) for the type variable
                                if (!typeVarBindings.containsKey(typeVarName)) {
                                    typeVarBindings.put(
                                        typeVarName,
                                        Binding.Definition.make(
                                            new IdentifierInfo.TypeVariable(typeVarName, Pair.make(currentModuleName, var.getSourceRange().getStartSourcePosition())),
                                            var,
                                            var.getSourceRange()));
                                }
                            }
                            return super.visit_Name_TypeVar(var, arg);
                        }
                    },
                    null);
            }
            return new TypeVariableScope(parent, typeVarBindings);
        }
    }

    /**
     * This class encapsulates the visitation argument used with the visitor class. It contains information necessary for
     * resolving identifiers, including the relevant symbol table and {@link LocalFunctionIdentifierGenerator}, as well as any
     * user-supplied state which may be passed along.
     * <p>
     * This class is designed as an immutable class, with update methods which produce new copies with the appropriate fields updated.
     * This means that, for example, one does not need to keep an explicitly stack of state (e.g. the symbol table) as one
     * traverses up and down the source model.
     * 
     * @param <T> the type of the user-supplied state.
     *
     * @author Joseph Wong
     */
    public static class VisitorArgument<T> {
        
        /**
         * The current scope for resolving module, qualified and unqualified names. Can *not* be null.
         */
        private final SymbolTable scope;
        
        /**
         * The current scope for resolving type variables. Can *not* be null.
         */
        private final TypeVariableScope typeVarScope;
        
        /**
         * The generator of local function identifiers for use to generate IDs for local
         * definitions encountered.
         * 
         * Can be null (if visiting source elements that do not contain local functions).
         */
        private final LocalFunctionIdentifierGenerator localFunctionIdentifierGenerator;
        
        /**
         * The generator of identifiers for case-bound and lambda-bound variables.
         * 
         * Can be null (if visiting source elements that do not contain local definitions).
         */
        // todo-jowong only needed until the compiler assigns LocalFunctionIdentifiers to all local symbols
        private final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator;
        
        /**
         * The current user-supplied state.
         * 
         * Can be null.
         */
        private final T userState;
        
        /**
         * Constructs an instance of this class.
         * 
         * @param scope
         *            the current scope for resolving module, qualified and unqualified names. Can
         *            *not* be null.
         * @param typeVarScope
         *            the current scope for resolving type variables. Can *not* be null.
         * @param localFunctionIdentifierGenerator
         *            the generator of local function identifiers for use to generate IDs for local
         *            definitions encountered. Can be null (if visiting source elements that do not
         *            contain local functions).
         * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator
         *            the generator of identifiers for case-bound and lambda-bound variables. Can be
         *            null (if visiting source elements that do not contain local definitions).
         * @param userState
         *            the current user-supplied state. Can be null.
         */
        private VisitorArgument(
            final SymbolTable scope,
            final TypeVariableScope typeVarScope,
            final LocalFunctionIdentifierGenerator localFunctionIdentifierGenerator,
            final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator,
            final T userState) {
            
            if (scope == null || typeVarScope == null) {
                throw new NullPointerException();
            }
            this.scope = scope;
            this.typeVarScope = typeVarScope;
            this.localFunctionIdentifierGenerator = localFunctionIdentifierGenerator;
            this.caseAndLambdaBoundLocalFunctionIdentifierGenerator = caseAndLambdaBoundLocalFunctionIdentifierGenerator;
            this.userState = userState;
        }
        
        /**
         * Factory method to construct a new instance of this class.
         * @param <T> the type of the user-specified state.
         * @param scope the current scope. Can *not* be null.
         * @param userState the current user-supplied state. Can be null.
         * @return a new instance of this class.
         */
        public static <T> VisitorArgument<T> make(final SymbolTable scope, final T userState) {
            return new VisitorArgument<T>(scope, new TypeVariableScope(null, new LinkedHashMap<String, Binding<IdentifierInfo.TypeVariable>>()), null, null, userState);
        }
        
        /**
         * Constructs a new visitor argument based on this one, but with an updated type variable scope.
         * @param typeVarScope the new type variable scope.
         * @return a new visitor argument.
         */
        VisitorArgument<T> updateTypeVariableScope(final TypeVariableScope typeVarScope) {
            return new VisitorArgument<T>(this.scope, typeVarScope, this.localFunctionIdentifierGenerator, this.caseAndLambdaBoundLocalFunctionIdentifierGenerator, this.userState);
        }
        
        /**
         * Constructs a new visitor argument based on this one, but with an updated scope.
         * @param scope the new scope.
         * @return a new visitor argument.
         */
        public VisitorArgument<T> updateScope(final SymbolTable scope) {
            return new VisitorArgument<T>(scope, this.typeVarScope, this.localFunctionIdentifierGenerator, this.caseAndLambdaBoundLocalFunctionIdentifierGenerator, this.userState);
        }
        
        /**
         * Constructs a new visitor argument based on this one, but with an updated scope and updated
         * local function identifier generators for the current algebraic function.
         * @param scope the new scope.
         * @param algebraicFunction the current algebraic function.
         * @return a new visitor argument.
         */
        VisitorArgument<T> updateScopeWithNewLocalFunctionIdentifierGenerator(final SymbolTable scope, final FunctionDefn.Algebraic algebraicFunction) {
            final LocalFunctionIdentifierGenerator localFunctionIdentifierGenerator = new LocalFunctionIdentifierGenerator();
            localFunctionIdentifierGenerator.reset(algebraicFunction.getName());

            final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator = new LocalFunctionIdentifierGenerator();
            caseAndLambdaBoundLocalFunctionIdentifierGenerator.reset(algebraicFunction.getName());

            return new VisitorArgument<T>(scope, this.typeVarScope, localFunctionIdentifierGenerator, caseAndLambdaBoundLocalFunctionIdentifierGenerator, this.userState);
        }
        
        /**
         * Constructs a new visitor argument based on this one, but with an updated user-supplied state.
         * @param userState the new user-supplied state.
         * @return a new visitor argument.
         */
        public VisitorArgument<T> updateUserState(final T userState) {
            return new VisitorArgument<T>(this.scope, this.typeVarScope, this.localFunctionIdentifierGenerator, this.caseAndLambdaBoundLocalFunctionIdentifierGenerator, userState);
        }

        /**
         * @return the current scope for resolving module, qualified and unqualified names. Can *not* be null. 
         */
        public SymbolTable getScope() {
            return scope;
        }
        
        /**
         * @return the current scope for resolving type variables. Can *not* be null. 
         */
        public TypeVariableScope getTypeVariableScope() {
            return typeVarScope;
        }

        /**
         * @return the generator of local function identifiers for use to generate IDs for local
         *         definitions encountered. Can be null (if visiting source elements that do not
         *         contain local functions).
         */
        LocalFunctionIdentifierGenerator getLocalFunctionIdentifierGenerator() {
            return localFunctionIdentifierGenerator;
        }
        
        /**
         * @return the generator of identifiers for case-bound and lambda-bound variables. Can be
         *         null (if visiting source elements that do not contain local definitions).
         */
        // todo-jowong only needed until the compiler assigns LocalFunctionIdentifiers to all local symbols
        LocalFunctionIdentifierGenerator getCaseAndLambdaBoundLocalFunctionIdentifierGenerator() {
            return caseAndLambdaBoundLocalFunctionIdentifierGenerator;
        }
        
        /**
         * @return the current top-level algebraic function name, or null if not currently in the scope of one.
         */
        public String getCurrentTopLevelFunctionName() {
            if (localFunctionIdentifierGenerator == null) {
                return null;
            } else {
                return localFunctionIdentifierGenerator.getCurrentFunction();
            }
        }

        /**
         * @return the current user-supplied state. Can be null. 
         */
        public T getUserState() {
            return userState;
        }
    }

    /**
     * Abstract base visitor that traverses the source model, gathers up the bindings declared
     * therein, and visits each source element with the appropriate symbol table passed in as a
     * visitation argument. This is the base class that should be extended if one wants to analyze a
     * source model with information about which symbols are in scope.
     * <p>
     * The task of gathering up <i>references</i> (non-binding occurrences) is up to the subclass
     * {@link IdentifierOccurrenceFinder}.
     * 
     * @author Joseph Wong
     * @author James Wright
     */
    /*
     * @history
     * 
     * This class is based on the class CALSourceGenerator.LambdaDefiningExprScopeAnalyzer.
     * 
     * Also, a good portion of this class's functionalities have been provided by the
     * BindingTrackingSourceModelTraverser, by James Wright.
     */
    public static class Visitor<T, R> extends SourceModelTraverser<VisitorArgument<T>, R> {

        /**
         * Encapsulates a factory object for building symbol tables representing scopes
         * for use during the visitation. Instances of this immutable class can be configured
         * differently depending on the need. 
         *
         * @author Joseph Wong
         */
        /*
         * @history
         * 
         * This class is based on the class CALSourceGenerator.LambdaDefiningExprScopeAnalyzer.Scope.
         */
        private final class ScopeBuilder {

            /**
             * Whether new scopes should be recorded by calling the appropriate handler methods.
             */
            // todo-jowong this is only needed because the argument scope of a function (both top-level algebraic and local)
            // needs to be recreated for the CALDoc associated with the type declaration of the function
            // When the type declaration becomes a component of the function definition, this field can go away
            // (because we would always record the scope)
            private final boolean shouldRecordScope;

            /**
             * Constructs an instance of this class. 
             * @param shouldRecordScope whether new scopes should be recorded by calling the appropriate handler methods.
             */
            ScopeBuilder(final boolean shouldRecordScope) {
                this.shouldRecordScope = shouldRecordScope;
            }

            /**
             * Creates a new top level scope, and potentially records the new scope.
             * @param parent the parent scope.
             * @param currentModuleNameBinding the binding for the name of the current module.
             * @param functionAndClassMethodBindings a map from unqualified names to bindings of functions and class methods.
             * @param typeConsBindings a map from unqualified names to bindings of type constructors.
             * @param dataConsBindings a map from unqualified names to bindings of data constructors.
             * @param dataConsFieldNameBindings a map from names to bindings of data constructors.
             * The name with which we associate a data cons field name is "<dataConsName>.<fieldName>".
             * @param typeClassBindings a map from unqualified names to bindings of type classes.
             * @param importedModuleNames an ordered collection of the imported module names.
             * @return a new top level scope.
             */
            private SymbolTable makeTopLevelScope(
                final SymbolTable parent,
                final Binding<IdentifierInfo.Module> currentModuleNameBinding,
                final BindingsMap<IdentifierInfo.TopLevel.FunctionOrClassMethod> functionAndClassMethodBindings,
                final BindingsMap<IdentifierInfo.TopLevel.TypeCons> typeConsBindings,
                final BindingsMap<IdentifierInfo.TopLevel.DataCons> dataConsBindings,
                final BindingsMap<IdentifierInfo.DataConsFieldName> dataConsFieldNameBindings,
                final BindingsMap<IdentifierInfo.TopLevel.TypeClass> typeClassBindings,
                final Collection<ModuleName> importedModuleNames) {

                final SymbolTable.TopLevelScope topLevelScope =
                    new SymbolTable.TopLevelScope(parent, currentModuleNameBinding, functionAndClassMethodBindings, typeConsBindings, dataConsBindings, dataConsFieldNameBindings, typeClassBindings, importedModuleNames);

                if (shouldRecordScope) {
                    handleNewTopLevelScope(topLevelScope);
                }
                return topLevelScope;
            }

            /**
             * Creates a new local scope, and potentially records the new scope.
             * @param parent the parent scope.
             * @param bindings a map from unqualified names to bindings of local variables.
             * @return a new local scope.
             */
            private SymbolTable makeLocalScope(final SymbolTable parent, final BindingsMap<IdentifierInfo.Local> bindings) {
                final SymbolTable.LocalScope localScope = new SymbolTable.LocalScope(parent, bindings);
                if (shouldRecordScope) {
                    handleNewLocalScope(localScope);
                }
                return localScope;
            }

            /**
             * Creates a new scope containing bindings for top level entities in a module definition.
             * @param parent the parent scope.
             * @param moduleDefn the source element corresponding to the new scope.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final ModuleDefn moduleDefn) {
                final BindingsMap<IdentifierInfo.TopLevel.FunctionOrClassMethod> functionAndClassMethodBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.TopLevel.TypeCons> typeConsBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.TopLevel.DataCons> dataConsBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.DataConsFieldName> dataConsFieldNameBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.TopLevel.TypeClass> typeClassBindings = BindingsMap.make();

                final LinkedHashSet<ModuleName> importedModuleNames = new LinkedHashSet<ModuleName>();

                ////
                /// Gather up the imported module names and the names in the import using clauses.
                //
                
                for (final Import importStmt : moduleDefn.getImportedModules()) {

                    final Name.Module importedModuleName = importStmt.getImportedModuleName();

                    importedModuleNames.add(SourceModel.Name.Module.toModuleName(importedModuleName));

                    final SourceModelTraverser<Void, Void> usingClauseWalker = new SourceModelTraverser<Void, Void>() {
                        @Override
                        public Void visit_Import_UsingItem_Function(final Import.UsingItem.Function usingItemFunction, final Void arg) {

                            final String[] usingNames = usingItemFunction.getUsingNames();
                            final SourceRange[] usingNameSourceRanges = usingItemFunction.getUsingNameSourceRanges();

                            for (int i = 0, n = usingNames.length; i < n; i++) {
                                final String name = usingNames[i];
                                final SourceRange sourceRange = usingNameSourceRanges[i];

                                functionAndClassMethodBindings.put(
                                    name,
                                    Binding.ImportUsingItem.make(
                                        new IdentifierInfo.TopLevel.FunctionOrClassMethod(
                                            QualifiedName.make(SourceModel.Name.Module.toModuleName(importedModuleName), name)),
                                        usingItemFunction,
                                        i,
                                        sourceRange));
                            }
                            return null;
                        }

                        @Override
                        public Void visit_Import_UsingItem_TypeConstructor(final Import.UsingItem.TypeConstructor usingItemTypeConstructor, final Void arg) {

                            final String[] usingNames = usingItemTypeConstructor.getUsingNames();
                            final SourceRange[] usingNameSourceRanges = usingItemTypeConstructor.getUsingNameSourceRanges();

                            for (int i = 0, n = usingNames.length; i < n; i++) {
                                final String name = usingNames[i];
                                final SourceRange sourceRange = usingNameSourceRanges[i];

                                typeConsBindings.put(
                                    name,
                                    Binding.ImportUsingItem.make(
                                        new IdentifierInfo.TopLevel.TypeCons(
                                            QualifiedName.make(SourceModel.Name.Module.toModuleName(importedModuleName), name)),
                                        usingItemTypeConstructor,
                                        i,
                                        sourceRange));
                            }
                            return null;
                        }

                        @Override
                        public Void visit_Import_UsingItem_DataConstructor(final Import.UsingItem.DataConstructor usingItemDataConstructor, final Void arg) {

                            final String[] usingNames = usingItemDataConstructor.getUsingNames();
                            final SourceRange[] usingNameSourceRanges = usingItemDataConstructor.getUsingNameSourceRanges();

                            for (int i = 0, n = usingNames.length; i < n; i++) {
                                final String name = usingNames[i];
                                final SourceRange sourceRange = usingNameSourceRanges[i];

                                dataConsBindings.put(
                                    name,
                                    Binding.ImportUsingItem.make(
                                        new IdentifierInfo.TopLevel.DataCons(
                                            QualifiedName.make(SourceModel.Name.Module.toModuleName(importedModuleName), name)),
                                        usingItemDataConstructor,
                                        i,
                                        sourceRange));
                            }
                            return null;
                        }

                        @Override
                        public Void visit_Import_UsingItem_TypeClass(final Import.UsingItem.TypeClass usingItemTypeClass, final Void arg) {

                            final String[] usingNames = usingItemTypeClass.getUsingNames();
                            final SourceRange[] usingNameSourceRanges = usingItemTypeClass.getUsingNameSourceRanges();

                            for (int i = 0, n = usingNames.length; i < n; i++) {
                                final String name = usingNames[i];
                                final SourceRange sourceRange = usingNameSourceRanges[i];

                                typeClassBindings.put(
                                    name,
                                    Binding.ImportUsingItem.make(
                                        new IdentifierInfo.TopLevel.TypeClass(
                                            QualifiedName.make(SourceModel.Name.Module.toModuleName(importedModuleName), name)),
                                        usingItemTypeClass,
                                        i,
                                        sourceRange));
                            }
                            return null;
                        }
                    };

                    importStmt.accept(usingClauseWalker, null);
                }

                ////
                /// Now add the top level elements (functions, type and data constructors, type classes and class methods)
                //
                
                addTopLevelElementsToBindings(moduleDefn.getTopLevelDefns(), functionAndClassMethodBindings, typeConsBindings, dataConsBindings, dataConsFieldNameBindings, typeClassBindings);

                final ModuleName moduleName = SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName());

                return makeTopLevelScope(
                    parent,
                    Binding.Definition.make(
                        new IdentifierInfo.Module(moduleName), moduleDefn.getModuleName(), moduleDefn.getModuleName().getSourceRange()),
                    functionAndClassMethodBindings, typeConsBindings, dataConsBindings, dataConsFieldNameBindings, typeClassBindings,
                    importedModuleNames);
            }

            /**
             * Creates a new scope containing bindings for top level entities.
             * @param parent the parent scope.
             * @param topLevelElements the top level source elements.
             * @return the new scope.
             */
            SymbolTable newTopLevelScope(final SymbolTable parent, final TopLevelSourceElement... topLevelElements) {
                final BindingsMap<IdentifierInfo.TopLevel.FunctionOrClassMethod> functionAndClassMethodBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.TopLevel.TypeCons> typeConsBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.TopLevel.DataCons> dataConsBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.DataConsFieldName> dataConsFieldNameBindings = BindingsMap.make();
                final BindingsMap<IdentifierInfo.TopLevel.TypeClass> typeClassBindings = BindingsMap.make();

                addTopLevelElementsToBindings(topLevelElements, functionAndClassMethodBindings, typeConsBindings, dataConsBindings, dataConsFieldNameBindings, typeClassBindings);
                return makeTopLevelScope(
                    parent,
                    Binding.External.make(new IdentifierInfo.Module(currentModuleName)),
                    functionAndClassMethodBindings, typeConsBindings, dataConsBindings, dataConsFieldNameBindings, typeClassBindings,
                    Collections.<ModuleName>emptySet());
            }

            /**
             * Processes the given top level source elements and add the names to the appropriate
             * binding maps.
             * @param topLevelElements the top level source elements.
             * @param functionAndClassMethodBindings a map from unqualified names to bindings of functions and class methods.
             * @param typeConsBindings a map from unqualified names to bindings of type constructors.
             * @param dataConsBindings a map from unqualified names to bindings of data constructors.
             * @param dataConsFieldNameBindings a map from names to bindings of data constructors.
             * The name with which we associate a data cons field name is "<dataConsName>.<fieldName>".
             * @param typeClassBindings a map from unqualified names to bindings of type classes.
             */
            private void addTopLevelElementsToBindings(
                final TopLevelSourceElement[] topLevelElements,
                final BindingsMap<IdentifierInfo.TopLevel.FunctionOrClassMethod> functionAndClassMethodBindings,
                final BindingsMap<IdentifierInfo.TopLevel.TypeCons> typeConsBindings,
                final BindingsMap<IdentifierInfo.TopLevel.DataCons> dataConsBindings,
                final BindingsMap<IdentifierInfo.DataConsFieldName> dataConsFieldNameBindings,
                final BindingsMap<IdentifierInfo.TopLevel.TypeClass> typeClassBindings) {

                // loop through the top level elements and process each one
                for (final TopLevelSourceElement element : topLevelElements) {

                    if (element instanceof FunctionDefn) {
                        ////
                        /// Process a function definition
                        //
                        
                        final FunctionDefn function = (FunctionDefn)element;

                        final IdentifierInfo.TopLevel.FunctionOrClassMethod functionIdentifier = new IdentifierInfo.TopLevel.FunctionOrClassMethod(
                            QualifiedName.make(currentModuleName, function.getName()));
                        
                        functionAndClassMethodBindings.put(
                            function.getName(),
                            Binding.Definition.make(functionIdentifier, function, function.getNameSourceRange()));
                        
                        if (function instanceof FunctionDefn.Foreign) {
                            // for a foreign function, we record the foreign descriptor
                            if (shouldRecordScope) {
                                handleForeignFunctionDescriptor(
                                    ForeignDescriptor.make(
                                        functionIdentifier,
                                        function,
                                        ((FunctionDefn.Foreign)function).getExternalNameSourceRange()));
                            }
                        }

                    } else if (element instanceof TypeConstructorDefn) {
                        ////
                        /// Process a type constructor definition
                        //
                        
                        final TypeConstructorDefn typeCons = (TypeConstructorDefn)element;

                        final IdentifierInfo.TopLevel.TypeCons typeConsIdentifier = new IdentifierInfo.TopLevel.TypeCons(
                            QualifiedName.make(currentModuleName, typeCons.getTypeConsName()));
                        
                        typeConsBindings.put(
                            typeCons.getTypeConsName(),
                            Binding.Definition.make(typeConsIdentifier, typeCons, typeCons.getSourceRangeOfName()));

                        if (typeCons instanceof TypeConstructorDefn.ForeignType) {
                            // for a foreign type, we record the foreign descriptor
                            if (shouldRecordScope) {
                                handleForeignTypeDescriptor(
                                    ForeignDescriptor.make(
                                        typeConsIdentifier,
                                        typeCons,
                                        ((TypeConstructorDefn.ForeignType)typeCons).getExternalNameSourceRange()));
                            }
                            
                        } else if (typeCons instanceof TypeConstructorDefn.AlgebraicType) {
                            final TypeConstructorDefn.AlgebraicType algebraic = (TypeConstructorDefn.AlgebraicType)typeCons;

                            for (final TypeConstructorDefn.AlgebraicType.DataConsDefn dataCons : algebraic.getDataConstructors()) {
                                // For an algebraic type, we process each data constructor and its fields
                                
                                final IdentifierInfo.TopLevel.DataCons dataConsIdentifier =
                                    new IdentifierInfo.TopLevel.DataCons(
                                        QualifiedName.make(currentModuleName, dataCons.getDataConsName()));

                                final Binding.Definition<IdentifierInfo.TopLevel.DataCons> dataConsOccurrence =
                                    Binding.Definition.make(dataConsIdentifier, dataCons, dataCons.getSourceRangeOfName());
                                
                                dataConsBindings.put(dataCons.getDataConsName(), dataConsOccurrence);

                                for (final TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument typeArg : dataCons.getTypeArgs()) {
                                    final Name.Field fieldName = typeArg.getFieldName();

                                    // the name we associate a data cons field name is <dataConsName>.<fieldName>
                                    dataConsFieldNameBindings.put(
                                        dataCons.getDataConsName() + "." + fieldName.getName().getCalSourceForm(),
                                        Binding.DataConsFieldName.make(
                                            new IdentifierInfo.DataConsFieldName(fieldName.getName(), Collections.singletonList(dataConsIdentifier)),
                                            dataConsOccurrence,
                                            fieldName,
                                            fieldName.getSourceRange()));
                                }
                            }
                        }
                    } else if (element instanceof TypeClassDefn) {
                        ////
                        /// Process a type class definition
                        //
                        
                        final TypeClassDefn typeClass = (TypeClassDefn)element;

                        typeClassBindings.put(
                            typeClass.getTypeClassName(),
                            Binding.Definition.make(
                                new IdentifierInfo.TopLevel.TypeClass(
                                    QualifiedName.make(currentModuleName, typeClass.getTypeClassName())),
                                typeClass,
                                typeClass.getSourceRangeOfName()));

                        for (final TypeClassDefn.ClassMethodDefn classMethod : typeClass.getClassMethodDefns()) {
                            // We process each class method as well
                            
                            functionAndClassMethodBindings.put(
                                classMethod.getMethodName(),
                                Binding.Definition.make(
                                    new IdentifierInfo.TopLevel.FunctionOrClassMethod(
                                        QualifiedName.make(currentModuleName, classMethod.getMethodName())),
                                    classMethod,
                                    classMethod.getSourceRangeOfName()));
                        }
                    }
                    
                    // there are other top level definitions: instances, function type declarations - those do
                    // not create bindings
                }
            }

            /**
             * Creates a new scope containing bindings for parameters in an algebraic function definition.
             * @param parent the parent scope.
             * @param algebraicFunction the source element corresponding to the new scope.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final FunctionDefn.Algebraic algebraicFunction) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                final IdentifierInfo.TopLevel.FunctionOrClassMethod functionIdentifierInfo =
                    new IdentifierInfo.TopLevel.FunctionOrClassMethod(QualifiedName.make(currentModuleName, algebraicFunction.getName()));

                for (final Parameter param : algebraicFunction.getParameters()) {
                    bindings.put(
                        param.getName(),
                        Binding.Definition.<IdentifierInfo.Local>make(
                            new IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod(param.getName(), functionIdentifierInfo),
                            param,
                            param.getSourceRangeOfNameNotIncludingPotentialPling()));
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for parameters declared *only* in the CALDoc comment of an algebraic function.
             * @param parent the parent scope.
             * @param algebraicFunction the source element corresponding to the new scope.
             * @param caldocComment the CALDoc comment. Can be null.
             * @return the new scope.
             */
            SymbolTable newScopeFromCALDoc(final SymbolTable parent, final FunctionDefn.Algebraic algebraicFunction, final CALDoc.Comment.Function caldocComment) {
                return makeTopLevelFunctionOrClassMethodScopeFromCALDoc(parent, algebraicFunction.getName(), caldocComment);
            }

            /**
             * Creates a new scope containing bindings for parameters declared in the CALDoc comment of a foreign function.
             * @param parent the parent scope.
             * @param foreignFunction the source element corresponding to the new scope.
             * @return the new scope.
             */
            SymbolTable newScopeFromCALDoc(final SymbolTable parent, final FunctionDefn.Foreign foreignFunction) {
                return makeTopLevelFunctionOrClassMethodScopeFromCALDoc(parent, foreignFunction.getName(), foreignFunction.getCALDocComment());
            }

            /**
             * Creates a new scope containing bindings for parameters declared in the CALDoc comment of a primitive function.
             * @param parent the parent scope.
             * @param primitiveFunction the source element corresponding to the new scope.
             * @return the new scope.
             */
            SymbolTable newScopeFromCALDoc(final SymbolTable parent, final FunctionDefn.Primitive primitiveFunction) {
                return makeTopLevelFunctionOrClassMethodScopeFromCALDoc(parent, primitiveFunction.getName(), primitiveFunction.getCALDocComment());
            }

            /**
             * Creates a new scope containing bindings for parameters declared in the CALDoc comment of a class method.
             * @param parent the parent scope.
             * @param classMethod the source element corresponding to the new scope.
             * @return the new scope.
             */
            SymbolTable newScopeFromCALDoc(final SymbolTable parent, final TypeClassDefn.ClassMethodDefn classMethod) {
                return makeTopLevelFunctionOrClassMethodScopeFromCALDoc(parent, classMethod.getMethodName(), classMethod.getCALDocComment());
            }

            /**
             * Internal helper method for creating a new scope containing bindings for parameters declared on in the CALDoc Comment of a top-level function or class method.
             * @param parent the parent scope.
             * @param functionOrClassMethodName the name of the function/method.
             * @param caldocComment the CALDoc comment. Can be null.
             * @return the new scope.
             */
            private SymbolTable makeTopLevelFunctionOrClassMethodScopeFromCALDoc(final SymbolTable parent, final String functionOrClassMethodName, final CALDoc.Comment caldocComment) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                if (caldocComment != null) {
                    final QualifiedName qualifiedFunctionOrClassMethodName =
                        QualifiedName.make(currentModuleName, functionOrClassMethodName);
                    
                    final IdentifierInfo.TopLevel.FunctionOrClassMethod functionIdentifierInfo =
                        new IdentifierInfo.TopLevel.FunctionOrClassMethod(qualifiedFunctionOrClassMethodName);
                    
                    final SourceModelTraverser<Void, Void> argBlocksVisitor = new SourceModelTraverser<Void, Void>() {
                        @Override
                        public Void visit_CALDoc_TaggedBlock_Arg(CALDoc.TaggedBlock.Arg argBlock, Void arg) {

                            final Name.Field argName = argBlock.getArgName();

                            if (argName.getName() instanceof FieldName.Textual) {
                                final String argNameString = argName.getName().getCalSourceForm();
                                
                                // We only create a binding for the parameter if it is not already bound
                                // (e.g. in the case of an algebraic function parameter - it can appear both
                                // in the parameter list and in the CALDoc comment - so the parameter list occurrence
                                // takes precedence, and we do not create a binding from the CALDoc occurrence)
                                
                                final Binding<? extends IdentifierInfo> existingBindingForArgName =
                                    parent.findVariableDefinition(argNameString);
                                
                                boolean alreadyBound = false;
                                if (existingBindingForArgName != null
                                    && existingBindingForArgName.getIdentifierInfo() instanceof IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod) {
                                    
                                    IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod infoForExistingBindingForArgName =
                                        (IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod)existingBindingForArgName.getIdentifierInfo();
                                    
                                    if (qualifiedFunctionOrClassMethodName.equals(infoForExistingBindingForArgName.getAssociatedFunction().getResolvedName())) {
                                        alreadyBound = true;
                                    }
                                }

                                if (!alreadyBound) {
                                    bindings.put(
                                        argNameString,
                                        Binding.Definition.<IdentifierInfo.Local>make(
                                            new IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod(argNameString, functionIdentifierInfo),
                                            argName,
                                            argName.getSourceRange()));
                                }
                            }

                            return super.visit_CALDoc_TaggedBlock_Arg(argBlock, arg);
                        }
                    };

                    caldocComment.accept(argBlocksVisitor, null);
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for parameters declared on in the CALDoc Comment of a local function.
             * @param parent the parent scope.
             * @param localFunctionIdentifierInfo the identifier info of the local function.
             * @param caldocComment the CALDoc comment. Can be null.
             * @return the new scope.
             */
            SymbolTable newScopeFromCALDoc(final SymbolTable parent, final IdentifierInfo.Local.Function localFunctionIdentifierInfo, final CALDoc.Comment.Function caldocComment) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                if (caldocComment != null) {
                    final SourceModelTraverser<Void, Void> argBlocksVisitor = new SourceModelTraverser<Void, Void>() {
                        @Override
                        public Void visit_CALDoc_TaggedBlock_Arg(CALDoc.TaggedBlock.Arg argBlock, Void arg) {

                            final Name.Field argName = argBlock.getArgName();

                            if (argName.getName() instanceof FieldName.Textual) {
                                final String argNameString = argName.getName().getCalSourceForm();
                                
                                // We only create a binding for the parameter if it is not already bound
                                // (e.g. in the case of an algebraic function parameter - it can appear both
                                // in the parameter list and in the CALDoc comment - so the parameter list occurrence
                                // takes precedence, and we do not create a binding from the CALDoc occurrence)
                                
                                final Binding<? extends IdentifierInfo> existingBindingForArgName =
                                    parent.findVariableDefinition(argNameString);
                                
                                boolean alreadyBound = false;
                                if (existingBindingForArgName != null
                                    && existingBindingForArgName.getIdentifierInfo() instanceof IdentifierInfo.Local.Parameter.LocalFunction) {
                                    
                                    IdentifierInfo.Local.Parameter.LocalFunction infoForExistingBindingForArgName =
                                        (IdentifierInfo.Local.Parameter.LocalFunction)existingBindingForArgName.getIdentifierInfo();
                                    
                                    if (localFunctionIdentifierInfo.getLocalFunctionIdentifier().equals(infoForExistingBindingForArgName.getAssociatedFunction().getLocalFunctionIdentifier())) {
                                        alreadyBound = true;
                                    }
                                }

                                if (!alreadyBound) {
                                    bindings.put(
                                        argNameString,
                                        Binding.Definition.<IdentifierInfo.Local>make(
                                            new IdentifierInfo.Local.Parameter.LocalFunction(argNameString, localFunctionIdentifierInfo),
                                            argName,
                                            argName.getSourceRange()));
                                }
                            }

                            return super.visit_CALDoc_TaggedBlock_Arg(argBlock, arg);
                        }
                    };

                    caldocComment.accept(argBlocksVisitor, null);
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for parameters declared on in the CALDoc Comment of an instance method.
             * @param parent the parent scope.
             * @param instanceMethod the source element corresponding to the new scope.
             * @return the new scope.
             */
            SymbolTable newScopeFromCALDoc(final SymbolTable parent, final InstanceDefn.InstanceMethod instanceMethod) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();
                
                final CALDoc.Comment.InstanceMethod caldocComment = instanceMethod.getCALDocComment();

                if (caldocComment != null) {
                    final SourceModelTraverser<Void, Void> argBlocksVisitor = new SourceModelTraverser<Void, Void>() {
                        @Override
                        public Void visit_CALDoc_TaggedBlock_Arg(CALDoc.TaggedBlock.Arg argBlock, Void arg) {

                            final Name.Field argName = argBlock.getArgName();

                            if (argName.getName() instanceof FieldName.Textual) {
                                final String argNameString = argName.getName().getCalSourceForm();

                                bindings.put(
                                    argNameString,
                                    Binding.Definition.<IdentifierInfo.Local>make(
                                        new IdentifierInfo.Local.Parameter.InstanceMethodCALDoc(argNameString),
                                        argName,
                                        argName.getSourceRange()));
                            }

                            return super.visit_CALDoc_TaggedBlock_Arg(argBlock, arg);
                        }
                    };

                    caldocComment.accept(argBlocksVisitor, null);
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for parameters in a lambda.
             * @param parent the parent scope.
             * @param lambda the source element corresponding to the new scope.
             * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator the identifier generator for case-bound and lambda-bound variables.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final Expr.Lambda lambda, final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                for (final Parameter param : lambda.getParameters()) {
                    bindings.put(
                        param.getName(),
                        Binding.Definition.<IdentifierInfo.Local>make(
                            new IdentifierInfo.Local.Parameter.Lambda(
                                param.getName(),
                                caseAndLambdaBoundLocalFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, param.getName())),
                            param,
                            param.getSourceRangeOfNameNotIncludingPotentialPling()));
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for parameters in a local function definition.
             * @param parent the parent scope.
             * @param localFunction the source element corresponding to the new scope.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final LocalDefn.Function.Definition localFunction, final IdentifierInfo.Local.Function localFunctionIdentifierInfo) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                for (final Parameter param : localFunction.getParameters()) {
                    bindings.put(
                        param.getName(),
                        Binding.Definition.<IdentifierInfo.Local>make(
                            new IdentifierInfo.Local.Parameter.LocalFunction(param.getName(), localFunctionIdentifierInfo),
                            param,
                            param.getSourceRangeOfNameNotIncludingPotentialPling()));
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for local variables bound in a let expression.
             * @param parent the parent scope.
             * @param letExpr the source element corresponding to the new scope.
             * @param localFunctionIdentifierGenerator the generator of local function identifiers to use.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final Expr.Let letExpr, final LocalFunctionIdentifierGenerator localFunctionIdentifierGenerator) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                /**
                 * Handles the adding of bindings for local definitions (both local functions and local pattern match declarations).
                 * This is done by walking the local definitions and adding a binding for each local function / pattern-bound variable
                 * encountered. The synthetic local function generated by the compiler for desugaring a local pattern match declaration
                 * is also taken into account.
                 * 
                 * @author Joseph Wong
                 */
                class LocallyDefinedNamesCollector extends LocalBindingsProcessor<LinkedHashSet<String>, Void> {
                    @Override
                    void processLocalDefinitionBinding(final String name, final SourceElement localDefinition, final LinkedHashSet<String> arg) {
                        // do nothing... just defer to the additional processing methods
                    }

                    @Override
                    void additionallyProcessLocalDefnFunctionDefinition(final LocalDefn.Function.Definition function, final LinkedHashSet<String> patternVarNames) {
                        final String functionName = function.getName();
                        bindings.put(
                            functionName,
                            Binding.Definition.<IdentifierInfo.Local>make(
                                new IdentifierInfo.Local.Function(
                                    functionName,
                                    localFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, functionName)),
                                function,
                                function.getNameSourceRange()));
                    }

                    @Override
                    void additionallyProcessPatternVar(final Pattern.Var var, final LinkedHashSet<String> patternVarNames) {
                        final String varName = var.getName();
                        // add to the set of pattern var names for a local pattern match decl
                        patternVarNames.add(varName);

                        bindings.put(
                            varName,
                            Binding.Definition.<IdentifierInfo.Local>make(
                                new IdentifierInfo.Local.PatternMatchVariable(
                                    varName,
                                    IdentifierInfo.Local.PatternVariableKind.regular,
                                    localFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, varName)),
                                var,
                                var.getSourceRange()));
                    }

                    @Override
                    void additionallyProcessPunnedTextualRecordFieldPattern(final FieldName.Textual fieldName, final Name.Field fieldNameElement, final LinkedHashSet<String> patternVarNames) {
                        final String varName = fieldName.getCalSourceForm();
                        // add to the set of pattern var names for a local pattern match decl
                        patternVarNames.add(varName);

                        bindings.put(
                            varName,
                            Binding.PunnedTextualRecordFieldName.<IdentifierInfo.Local>make(
                                new IdentifierInfo.Local.PatternMatchVariable(
                                    varName,
                                    IdentifierInfo.Local.PatternVariableKind.punnedRecordField,
                                    localFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, varName)),
                                fieldNameElement,
                                fieldNameElement.getSourceRange()));
                    }

                    @Override
                    void additionallyProcessPunnedTextualDataConsFieldPattern(final FieldName.Textual fieldName, final Name.Field fieldNameElement, final Name.DataCons dataConsName, final LinkedHashSet<String> patternVarNames) {
                        final String varName = fieldName.getCalSourceForm();
                        // add to the set of pattern var names for a local pattern match decl
                        patternVarNames.add(varName);

                        final Binding<IdentifierInfo.TopLevel.DataCons> dataConsBinding = parent.findDataCons(dataConsName);

                        if (dataConsBinding != null) {
                            bindings.put(
                                varName,
                                Binding.PunnedTextualDataConsFieldName.<IdentifierInfo.Local>make(
                                    new IdentifierInfo.Local.PatternMatchVariable(
                                        varName,
                                        IdentifierInfo.Local.PatternVariableKind.punnedDataConsField,
                                        localFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, varName)),
                                        Collections.singletonList(dataConsBinding),
                                        fieldNameElement,
                                        fieldNameElement.getSourceRange()));
                        }
                    }

                    /**
                     * Handle the synthetic local function which is generated by the compiler to host the defining
                     * expression of a local pattern match declaration. This is done to keep the local function identifier generator in
                     * sync with what the compiler would do.
                     * 
                     * @param patternMatchDecl the pattern match declaration.
                     * @param patternVarNames the LinkedHashSet of the pattern variable names, in source order.
                     */
                    private void handleBindingForSyntheticLocalDefinition(final LocalDefn.PatternMatch patternMatchDecl, final LinkedHashSet<String> patternVarNames) {
                        final String syntheticLocalFunctionName = FreeVariableFinder.makeTempVarNameForDesugaredLocalPatternMatchDecl(patternVarNames);
                        localFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, syntheticLocalFunctionName);
                    }

                    @Override
                    public Void visit_LocalDefn_PatternMatch_UnpackDataCons(final LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, final LinkedHashSet<String> arg) {
                        // visit only the patterns
                        final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                        super.visit_LocalDefn_PatternMatch_UnpackDataCons(unpackDataCons, patternVarNames);
                        // handle the synthetic definition last
                        handleBindingForSyntheticLocalDefinition(unpackDataCons, patternVarNames);
                        return null;
                    }

                    @Override
                    public Void visit_LocalDefn_PatternMatch_UnpackListCons(final LocalDefn.PatternMatch.UnpackListCons unpackListCons, final LinkedHashSet<String> arg) {
                        // visit only the patterns
                        final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                        super.visit_LocalDefn_PatternMatch_UnpackListCons(unpackListCons, patternVarNames);
                        // handle the synthetic definition last
                        handleBindingForSyntheticLocalDefinition(unpackListCons, patternVarNames);
                        return null;
                    }

                    @Override
                    public Void visit_LocalDefn_PatternMatch_UnpackRecord(final LocalDefn.PatternMatch.UnpackRecord unpackRecord, final LinkedHashSet<String> arg) {
                        // visit only the field patterns (and not the base record pattern - since we do not support them in local pattern match decl)
                        final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                        super.visit_LocalDefn_PatternMatch_UnpackRecord(unpackRecord, patternVarNames);
                        // handle the synthetic definition last
                        handleBindingForSyntheticLocalDefinition(unpackRecord, patternVarNames);
                        return null;
                    }

                    @Override
                    public Void visit_LocalDefn_PatternMatch_UnpackTuple(final LocalDefn.PatternMatch.UnpackTuple unpackTuple, final LinkedHashSet<String> arg) {
                        // visit only the patterns
                        final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
                        super.visit_LocalDefn_PatternMatch_UnpackTuple(unpackTuple, patternVarNames);
                        // handle the synthetic definition last
                        handleBindingForSyntheticLocalDefinition(unpackTuple, patternVarNames);
                        return null;
                    }
                }

                final LocallyDefinedNamesCollector localDefinedNamesCollector = new LocallyDefinedNamesCollector() ;

                for (final LocalDefn defn : letExpr.getLocalDefinitions()) {
                    defn.accept(localDefinedNamesCollector, null);
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for parameters in a case alternative.
             * @param parent the parent scope.
             * @param tuple the source element corresponding to the new scope.
             * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator the identifier generator for case-bound and lambda-bound variables.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final Expr.Case.Alt.UnpackTuple tuple, final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator) {
                return makeScopeFromCasePatterns(parent, tuple.getPatterns(), caseAndLambdaBoundLocalFunctionIdentifierGenerator);
            }

            /**
             * Creates a new scope containing bindings for parameters in a case alternative.
             * @param parent the parent scope.
             * @param listCons the source element corresponding to the new scope.
             * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator the identifier generator for case-bound and lambda-bound variables.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final Expr.Case.Alt.UnpackListCons listCons, final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator) {
                return makeScopeFromCasePatterns(parent, new Pattern[] {listCons.getHeadPattern(), listCons.getTailPattern()}, caseAndLambdaBoundLocalFunctionIdentifierGenerator);
            }

            /**
             * Internal helper method for creating a new scope containing bindings for parameters in a case alternative.
             * @param parent the parent scope.
             * @param patterns the pattern source elements.
             * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator the identifier generator for case-bound and lambda-bound variables.
             * @return the new scope.
             */
            private SymbolTable makeScopeFromCasePatterns(final SymbolTable parent, final Pattern[] patterns, final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                for (final Pattern pattern : patterns) {
                    addCasePatternToBindings(bindings, pattern, caseAndLambdaBoundLocalFunctionIdentifierGenerator);
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Processes a case-bound pattern and potentially add it to the bindings map.
             * @param bindings the bindings map.
             * @param pattern the case-bound pattern.
             * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator the identifier generator for case-bound and lambda-bound variables.
             */
            private void addCasePatternToBindings(final BindingsMap<IdentifierInfo.Local> bindings, final Pattern pattern, final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator) {
                if (pattern instanceof Pattern.Var) {
                    final Pattern.Var patternVariable = (Pattern.Var)pattern;
                    final String name = patternVariable.getName();

                    bindings.put(
                        name,
                        Binding.Definition.<IdentifierInfo.Local>make(
                            new IdentifierInfo.Local.CasePatternVariable(
                                name,
                                caseAndLambdaBoundLocalFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, name),
                                IdentifierInfo.Local.PatternVariableKind.regular),
                            patternVariable,
                            patternVariable.getSourceRange()));
                }
            }

            /**
             * Creates a new scope containing bindings for parameters in a case alternative.
             * @param parent the parent scope.
             * @param record the source element corresponding to the new scope.
             * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator the identifier generator for case-bound and lambda-bound variables.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final Expr.Case.Alt.UnpackRecord record, final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator) {
                final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                if (record.getBaseRecordPattern() != null) {
                    addCasePatternToBindings(bindings, record.getBaseRecordPattern(), caseAndLambdaBoundLocalFunctionIdentifierGenerator);
                }

                for (final FieldPattern fieldPattern : record.getFieldPatterns()) {
                    final Pattern pattern = fieldPattern.getPattern();

                    if (pattern != null) {
                        addCasePatternToBindings(bindings, pattern, caseAndLambdaBoundLocalFunctionIdentifierGenerator);

                    } else {
                        // a punned field pattern
                        final FieldName fieldName = fieldPattern.getFieldName().getName();
                        final String varName = fieldName.getCalSourceForm();

                        if (fieldName instanceof FieldName.Textual) {
                            bindings.put(
                                fieldName.getCalSourceForm(),
                                Binding.PunnedTextualRecordFieldName.<IdentifierInfo.Local>make(
                                    new IdentifierInfo.Local.CasePatternVariable(
                                        varName,
                                        caseAndLambdaBoundLocalFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, varName),
                                        IdentifierInfo.Local.PatternVariableKind.punnedRecordField),
                                    fieldPattern.getFieldName(),
                                    fieldPattern.getFieldName().getSourceRange()));
                        }
                    }
                }

                return makeLocalScope(parent, bindings);
            }

            /**
             * Creates a new scope containing bindings for parameters in a case alternative.
             * @param parent the parent scope.
             * @param dataCons the source element corresponding to the new scope.
             * @param caseAndLambdaBoundLocalFunctionIdentifierGenerator the identifier generator for case-bound and lambda-bound variables.
             * @return the new scope.
             */
            SymbolTable newScope(final SymbolTable parent, final Expr.Case.Alt.UnpackDataCons dataCons, final LocalFunctionIdentifierGenerator caseAndLambdaBoundLocalFunctionIdentifierGenerator) {
                final ArgBindings argBindings = dataCons.getArgBindings();

                if (argBindings instanceof ArgBindings.Matching) {
                    final BindingsMap<IdentifierInfo.Local> bindings = BindingsMap.make();

                    for (final FieldPattern fieldPattern : ((ArgBindings.Matching)argBindings).getFieldPatterns()) {
                        final Pattern pattern = fieldPattern.getPattern();

                        if (pattern != null) {
                            addCasePatternToBindings(bindings, pattern, caseAndLambdaBoundLocalFunctionIdentifierGenerator);

                        } else {
                            // a punned field pattern
                            final FieldName fieldName = fieldPattern.getFieldName().getName();
                            final String varName = fieldName.getCalSourceForm();
                            
                            if (fieldName instanceof FieldName.Textual) {
                                final List<Binding<IdentifierInfo.TopLevel.DataCons>> dataConsBindings =
                                    new ArrayList<Binding<IdentifierInfo.TopLevel.DataCons>>();
                                
                                for (final Name.DataCons dataConsName : dataCons.getDataConsNames()) {
                                    final Binding<IdentifierInfo.TopLevel.DataCons> dataConsBinding = parent.findDataCons(dataConsName);
                                    
                                    if (dataConsBinding != null) {
                                        dataConsBindings.add(dataConsBinding);
                                    }
                                }
                                
                                if (!dataConsBindings.isEmpty()) {
                                    bindings.put(
                                        fieldName.getCalSourceForm(),
                                        Binding.PunnedTextualDataConsFieldName.<IdentifierInfo.Local>make(
                                            new IdentifierInfo.Local.CasePatternVariable(
                                                varName,
                                                caseAndLambdaBoundLocalFunctionIdentifierGenerator.generateLocalFunctionIdentifier(currentModuleName, varName),
                                                IdentifierInfo.Local.PatternVariableKind.punnedDataConsField),
                                            dataConsBindings,
                                            fieldPattern.getFieldName(),
                                            fieldPattern.getFieldName().getSourceRange()));
                                }
                            }
                        }
                    }

                    return makeLocalScope(parent, bindings);

                } else if (argBindings instanceof ArgBindings.Positional) {
                    return makeScopeFromCasePatterns(parent, ((ArgBindings.Positional)argBindings).getPatterns(), caseAndLambdaBoundLocalFunctionIdentifierGenerator);

                } else {
                    throw new IllegalStateException();
                }
            }
        }
        
        ////
        /// Fields
        //
        
        /**
         * The name of the module associated with the source being visited.
         */
        private final ModuleName currentModuleName;
        
        /**
         * The main scope builder for constructing symbol tables.
         */
        private final ScopeBuilder scopeBuilder;
        
        /**
         * A scope builder for constructing symbol tables without recording the new scopes.
         */
        // todo-jowong this is only needed because the argument scope of a function (both top-level algebraic and local)
        // needs to be recreated for the CALDoc associated with the type declaration of the function
        // When the type declaration becomes a component of the function definition, this field can go away
        // (because we would always record the scope)
        private final ScopeBuilder nonRecordingScopeBuilder;
        
        ////
        /// Constructor
        //
        
        /**
         * Constructs an instance of this class.
         * @param currentModuleName the name of the module associated with the source being visited.
         */
        public Visitor(final ModuleName currentModuleName) {
            if (currentModuleName == null) {
                throw new NullPointerException();
            }
            this.currentModuleName = currentModuleName;
            this.scopeBuilder = new ScopeBuilder(true);
            this.nonRecordingScopeBuilder = new ScopeBuilder(false);
        }
        
        ////
        /// Accessors
        //
        
        /**
         * @return the name of the module associated with the source being visited.
         */
        public ModuleName getCurrentModuleName() {
            return currentModuleName;
        }
        
        ////
        /// Handler methods
        //

        /**
         * Processes the creation of a new scope.
         * @param scope the new scope.
         */
        protected void handleNewScope(final SymbolTable scope) {}
        
        /**
         * Processes the creation of a new top level scope. This default implementation delegates to {@link #handleNewScope}.
         * @param scope the new scope.
         */
        protected void handleNewTopLevelScope(final SymbolTable.TopLevelScope scope) {
            handleNewScope(scope);
        }
        
        /**
         * Processes the creation of a new local scope. This default implementation delegates to {@link #handleNewScope}.
         * @param scope the new scope.
         */
        protected void handleNewLocalScope(final SymbolTable.LocalScope scope) {
            handleNewScope(scope);
        }
        
        /**
         * Processes the creation of a new type variable scope.
         * @param scope the new scope.
         */
        protected void handleNewTypeVariableScope(final TypeVariableScope scope) {}
        
        /**
         * Processes a foreign function descriptor occurrence.
         * @param descriptorOccurrence the occurrence.
         */
        protected void handleForeignFunctionDescriptor(
            final ForeignDescriptor<IdentifierInfo.TopLevel.FunctionOrClassMethod> descriptorOccurrence) {}
        
        /**
         * Processes a foreign type descriptor occurrence.
         * @param descriptorOccurrence the occurrence.
         */
        protected void handleForeignTypeDescriptor(
            final ForeignDescriptor<IdentifierInfo.TopLevel.TypeCons> descriptorOccurrence) {}
        
        ////
        /// Visitor methods - elements which introduce new scopes
        //
        
        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_ModuleDefn(final ModuleDefn defn, final VisitorArgument<T> arg) {
            return super.visit_ModuleDefn(defn, arg.updateScope(scopeBuilder.newScope(arg.getScope(), defn)));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_FunctionDefn_Algebraic(final FunctionDefn.Algebraic algebraic, final VisitorArgument<T> arg) {
            // make a new scope with the declared arguments
            final VisitorArgument<T> newArg = makeVisitorArgumentWithAlgebraicFunctionArgumentNames(algebraic, arg, scopeBuilder);

            // we traverse the CALDoc with an augmented scope for non-lexical arguments (those defined only in the
            // CALDoc comment)
            if (algebraic.getCALDocComment() != null) {
                algebraic.getCALDocComment().accept(this, newArg.updateScope(scopeBuilder.newScopeFromCALDoc(newArg.getScope(), algebraic, algebraic.getCALDocComment())));
            }
            
            // we visit the rest of the definition with just the lexically declared argument scope
            for (final Parameter parameter : algebraic.getParameters()) {
                parameter.accept(this, newArg);
            }
            
            algebraic.getDefiningExpr().accept(this, newArg);
            return null;
        }

        /**
         * Constructs a new visitor argument based on an existing one, but with an updated scope and updated
         * local function identifier generators for the current algebraic function.
         * @param algebraic the algebraic function.
         * @param arg the existing visitor argument.
         * @param scopeBuilderToUse the scope builder to use.
         * @return the new visitor argument.
         */
        protected VisitorArgument<T> makeVisitorArgumentWithAlgebraicFunctionArgumentNames(final FunctionDefn.Algebraic algebraic, final VisitorArgument<T> arg, final ScopeBuilder scopeBuilderToUse) {
            final LocalFunctionIdentifierGenerator localFunctionIdentifierGenerator = new LocalFunctionIdentifierGenerator();
            localFunctionIdentifierGenerator.reset(algebraic.getName());
            final VisitorArgument<T> newArg = arg.updateScopeWithNewLocalFunctionIdentifierGenerator(scopeBuilderToUse.newScope(arg.getScope(), algebraic), algebraic);
            return newArg;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_FunctionTypeDeclaraction(final FunctionTypeDeclaration declaration, final VisitorArgument<T> arg) {
            // todo-jowong this special handling won't be necessary if the type declaration is made a component of the
            // algebraic function definition
            
            final Binding<? extends IdentifierInfo> functionBinding =
                arg.getScope().findVariableDefinition(declaration.getFunctionName());
            
            if (functionBinding != null && functionBinding.getSourceElement() instanceof FunctionDefn.Algebraic) {
                final FunctionDefn.Algebraic algebraicFunction = (FunctionDefn.Algebraic)functionBinding.getSourceElement();
                
                final VisitorArgument<T> newArg = makeVisitorArgumentWithAlgebraicFunctionArgumentNames(algebraicFunction, arg, nonRecordingScopeBuilder);
                
                // we traverse the CALDoc with an augmented scope for non-lexical arguments (those defined only in the
                // CALDoc comment)
                if (declaration.getCALDocComment() != null) {
                    declaration.getCALDocComment().accept(this, newArg.updateScope(scopeBuilder.newScopeFromCALDoc(newArg.getScope(), algebraicFunction, declaration.getCALDocComment())));
                }
            } else {
                // we simply traverse the CALDoc with the existing scope
                if (declaration.getCALDocComment() != null) {
                    declaration.getCALDocComment().accept(this, arg);
                }
            }
            
            declaration.getTypeSignature().accept(this, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_FunctionDefn_Foreign(final FunctionDefn.Foreign foreign, final VisitorArgument<T> arg) {
            // Only the CALDoc comment is under the new scope (which is generated by the comment itself)
            if (foreign.getCALDocComment() != null) {
                foreign.getCALDocComment().accept(this, arg.updateScope(scopeBuilder.newScopeFromCALDoc(arg.getScope(), foreign)));
            }
            // Visit the remainder of the definition
            foreign.getDeclaredType().accept(this, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_FunctionDefn_Primitive(final FunctionDefn.Primitive primitive, final VisitorArgument<T> arg) {
            // Only the CALDoc comment is under the new scope (which is generated by the comment itself)
            if (primitive.getCALDocComment() != null) {
                primitive.getCALDocComment().accept(this, arg.updateScope(scopeBuilder.newScopeFromCALDoc(arg.getScope(), primitive)));
            }
            // Visit the remainder of the definition
            primitive.getDeclaredType().accept(this, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_TypeClassDefn_ClassMethodDefn(final TypeClassDefn.ClassMethodDefn defn, final VisitorArgument<T> arg) {
            // Only the CALDoc comment is under the new scope (which is generated by the comment itself)
            if (defn.getCALDocComment() != null) {
                defn.getCALDocComment().accept(this, arg.updateScope(scopeBuilder.newScopeFromCALDoc(arg.getScope(), defn)));
            }
            
            // Visit the remainder of the definition
            defn.getTypeSignature().accept(this, arg);
            
            if (defn.getDefaultClassMethodName() != null) {
                defn.getDefaultClassMethodName().accept(this, arg);
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_InstanceDefn_InstanceMethod(final InstanceDefn.InstanceMethod method, final VisitorArgument<T> arg) {
            // Only the CALDoc comment is under the new scope (which is generated by the comment itself)
            if (method.getCALDocComment() != null) {
                method.getCALDocComment().accept(this, arg.updateScope(scopeBuilder.newScopeFromCALDoc(arg.getScope(), method)));
            }
            
            // Visit the remainder of the definition
            method.getResolvingFunctionName().accept(this, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_Expr_Lambda(final Expr.Lambda lambda, final VisitorArgument<T> arg) {
            return super.visit_Expr_Lambda(lambda, arg.updateScope(scopeBuilder.newScope(arg.getScope(), lambda, arg.getCaseAndLambdaBoundLocalFunctionIdentifierGenerator())));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_Expr_Let(final Expr.Let let, final VisitorArgument<T> arg) {
            return super.visit_Expr_Let(let, arg.updateScope(scopeBuilder.newScope(arg.getScope(), let, arg.getLocalFunctionIdentifierGenerator())));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_LocalDefn_Function_Definition(final LocalDefn.Function.Definition function, final VisitorArgument<T> arg) {
            final VisitorArgument<T> newArg = makeVisitorArgumentWithLocalFunctionArgumentNames(function, arg, scopeBuilder);
            
            final Binding<? extends IdentifierInfo> functionBinding =
                arg.getScope().findVariableDefinition(function.getName());
            
            if (functionBinding != null && functionBinding.getSourceElement() == function) {
                final IdentifierInfo.Local.Function localFunctionIdentifierInfo = (IdentifierInfo.Local.Function)functionBinding.getIdentifierInfo();
                
                // we traverse the CALDoc with an augmented scope for non-lexical arguments (those defined only in the
                // CALDoc comment)
                if (function.getCALDocComment() != null) {
                    function.getCALDocComment().accept(this, newArg.updateScope(scopeBuilder.newScopeFromCALDoc(newArg.getScope(), localFunctionIdentifierInfo, function.getCALDocComment())));
                }
            } else {
                throw new IllegalStateException("This name should have been bound in the scope");
            }
            
            // we visit the rest of the definition with just the lexically declared argument scope
            for (final Parameter parameter : function.getParameters()) {
                parameter.accept(this, newArg);
            }
            
            function.getDefiningExpr().accept(this, newArg);
            return null;
        }

        /**
         * Constructs a new visitor argument based on an existing one, but with an updated scope based on
         * the argument names of a local function.
         * @param function the local function.
         * @param arg the existing visitor argument.
         * @param scopeBuilderToUse the scope builder to use.
         * @return the new visitor argument.
         */
        protected VisitorArgument<T> makeVisitorArgumentWithLocalFunctionArgumentNames(final LocalDefn.Function.Definition function, final VisitorArgument<T> arg, final ScopeBuilder scopeBuilderToUse) {
            final IdentifierOccurrence<? extends IdentifierInfo> binding = arg.getScope().findVariableDefinition(function.getName());
            if (binding == null) {
                throw new IllegalStateException("This name should have been bound in the scope");
            }
            final VisitorArgument<T> newArg = arg.updateScope(scopeBuilderToUse.newScope(arg.getScope(), function, (IdentifierInfo.Local.Function)binding.getIdentifierInfo()));
            return newArg;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_LocalDefn_Function_TypeDeclaration(final LocalDefn.Function.TypeDeclaration declaration, final VisitorArgument<T> arg) {
            // todo-jowong this special handling won't be necessary if the type declaration is made a component of the
            // local function definition
            
            final Binding<? extends IdentifierInfo> functionBinding =
                arg.getScope().findVariableDefinition(declaration.getName());
            
            if (functionBinding != null && functionBinding.getSourceElement() instanceof LocalDefn.Function.Definition) {
                final LocalDefn.Function.Definition localFunction = (LocalDefn.Function.Definition)functionBinding.getSourceElement();
                final IdentifierInfo.Local.Function localFunctionIdentifierInfo = (IdentifierInfo.Local.Function)functionBinding.getIdentifierInfo();

                final VisitorArgument<T> newArg = makeVisitorArgumentWithLocalFunctionArgumentNames(localFunction, arg, nonRecordingScopeBuilder);
                
                // we traverse the CALDoc with an augmented scope for non-lexical arguments (those defined only in the
                // CALDoc comment)
                if (declaration.getCALDocComment() != null) {
                    declaration.getCALDocComment().accept(this, newArg.updateScope(scopeBuilder.newScopeFromCALDoc(newArg.getScope(), localFunctionIdentifierInfo, declaration.getCALDocComment())));
                }
            } else {
                // we simply traverse the CALDoc with the existing scope
                if (declaration.getCALDocComment() != null) {
                    declaration.getCALDocComment().accept(this, arg);
                }
            }
            
            declaration.getDeclaredType().accept(this, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_Expr_Case_Alt_UnpackDataCons(final Expr.Case.Alt.UnpackDataCons cons, final VisitorArgument<T> arg) {
            return super.visit_Expr_Case_Alt_UnpackDataCons(cons, updateScopeFor(cons, arg));
        }

        /**
         * Constructs a new visitor argument based on an existing one, but with an updated scope corresponding
         * to the specified case alternative.
         * @param cons the case alternative.
         * @param arg the existing visitor argument.
         * @return a new visitor argument.
         */
        protected VisitorArgument<T> updateScopeFor(final Expr.Case.Alt.UnpackDataCons cons, final VisitorArgument<T> arg) {
            return arg.updateScope(scopeBuilder.newScope(arg.getScope(), cons, arg.getCaseAndLambdaBoundLocalFunctionIdentifierGenerator()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_Expr_Case_Alt_UnpackListCons(final Expr.Case.Alt.UnpackListCons cons, final VisitorArgument<T> arg) {
            return super.visit_Expr_Case_Alt_UnpackListCons(cons, arg.updateScope(scopeBuilder.newScope(arg.getScope(), cons, arg.getCaseAndLambdaBoundLocalFunctionIdentifierGenerator())));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_Expr_Case_Alt_UnpackRecord(final Expr.Case.Alt.UnpackRecord record, final VisitorArgument<T> arg) {
            return super.visit_Expr_Case_Alt_UnpackRecord(record, arg.updateScope(scopeBuilder.newScope(arg.getScope(), record, arg.getCaseAndLambdaBoundLocalFunctionIdentifierGenerator())));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_Expr_Case_Alt_UnpackTuple(final Expr.Case.Alt.UnpackTuple tuple, final VisitorArgument<T> arg) {
            return super.visit_Expr_Case_Alt_UnpackTuple(tuple, arg.updateScope(scopeBuilder.newScope(arg.getScope(), tuple, arg.getCaseAndLambdaBoundLocalFunctionIdentifierGenerator())));
        }

        ////
        /// Visitor methods - elements which introduce new type variable scopes
        //
        
        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_InstanceDefn(final InstanceDefn defn, final VisitorArgument<T> arg) {
            final TypeVariableScope newScope = TypeVariableScope.newScope(arg.getTypeVariableScope(), defn, currentModuleName);
            handleNewTypeVariableScope(newScope);
            return super.visit_InstanceDefn(defn, arg.updateTypeVariableScope(newScope));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_TypeClassDefn(final TypeClassDefn defn, final VisitorArgument<T> arg) {
            final TypeVariableScope newScope = TypeVariableScope.newScope(arg.getTypeVariableScope(), defn, currentModuleName);
            handleNewTypeVariableScope(newScope);
            return super.visit_TypeClassDefn(defn, arg.updateTypeVariableScope(newScope));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(final TypeConstructorDefn.AlgebraicType.DataConsDefn defn, final VisitorArgument<T> arg) {
            final TypeVariableScope newScope = TypeVariableScope.newScope(arg.getTypeVariableScope(), defn, currentModuleName);
            handleNewTypeVariableScope(newScope);
            return super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg.updateTypeVariableScope(newScope));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_TypeConstructorDefn_AlgebraicType(final TypeConstructorDefn.AlgebraicType type, final VisitorArgument<T> arg) {
            final TypeVariableScope newScope = TypeVariableScope.newScope(arg.getTypeVariableScope(), type, currentModuleName);
            handleNewTypeVariableScope(newScope);
            return super.visit_TypeConstructorDefn_AlgebraicType(type, arg.updateTypeVariableScope(newScope));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_TypeSignature(final TypeSignature signature, final VisitorArgument<T> arg) {
            final TypeVariableScope newScope = TypeVariableScope.newScope(arg.getTypeVariableScope(), signature, currentModuleName);
            handleNewTypeVariableScope(newScope);
            return super.visit_TypeSignature(signature, arg.updateTypeVariableScope(newScope));
        }
    }

    /**
     * A base class for implementing algorithms that handle the processing of bindings for local definitions (both local function
     * definitions and local pattern match declarations).
     * 
     * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
     * @param <R> the return type. If the return value is not used, specify {@link Void}.
     * 
     * @author Joseph Wong
     */
    static abstract class LocalBindingsProcessor<T, R> extends SourceModelTraverser<T, R> {

        /**
         * The name of the data constructor being unpacked - only valid when visiting a field pattern.
         * This is null if the field pattern is associated with a record unpacking.
         */
        private Name.DataCons nameOfDataConsBeingUnpacked = null;

        /**
         * Processes a local definition binding.
         * @param name the name being bound.
         * @param localDefinition the source element corresponding to the definition/binding.
         * @param arg any visitation argument.
         */
        abstract void processLocalDefinitionBinding(String name, SourceElement localDefinition, T arg);

        /**
         * Performs additional processing for a local function definition.
         * @param function the local function definition.
         * @param arg any visitation argument.
         */
        void additionallyProcessLocalDefnFunctionDefinition(final LocalDefn.Function.Definition function, final T arg) {}

        /**
         * Performs additional processing for a pattern-bound variable in a local pattern match declaration.
         * @param var the pattern-bound variable.
         * @param arg any visitation argument.
         */
        void additionallyProcessPatternVar(final Pattern.Var var, final T arg) {}

        /**
         * Performs additional processing for a punned record field pattern in a local pattern match declaration.
         * @param fieldName the punned field name.
         * @param fieldNameElement the source element for the field name.
         * @param arg any visitation argument.
         */
        void additionallyProcessPunnedTextualRecordFieldPattern(final FieldName.Textual fieldName, final Name.Field fieldNameElement, final T arg) {}

        /**
         * Performs additional processing for a punned data cons field pattern in a local pattern match declaration.
         * @param fieldName the punned field name.
         * @param fieldNameElement the source element for the field name.
         * @param arg any visitation argument.
         */
        void additionallyProcessPunnedTextualDataConsFieldPattern(final FieldName.Textual fieldName, final Name.Field fieldNameElement, final Name.DataCons dataConsName, final T arg) {}

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_LocalDefn_Function_Definition(final LocalDefn.Function.Definition function, final T arg) {
            processLocalDefinitionBinding(function.getName(), function, arg); // the function defn is the bound element
            additionallyProcessLocalDefnFunctionDefinition(function, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_Pattern_Var(final Pattern.Var var, final T arg) {
            processLocalDefinitionBinding(var.getName(), var, arg); // the pattern var is the bound element
            additionallyProcessPatternVar(var, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_FieldPattern(final FieldPattern fieldPattern, final T arg) {
            // Handle punning
            if (fieldPattern.getPattern() == null) {
                // punning.

                // Textual field names become Vars of the same name.
                // Ordinal field names become wildcards ("_").
                final FieldName fieldName = fieldPattern.getFieldName().getName();
                if (fieldName instanceof FieldName.Textual) {
                    processLocalDefinitionBinding(fieldName.getCalSourceForm(), fieldPattern, arg); // the field pattern is the bound element

                    if (nameOfDataConsBeingUnpacked == null) {
                        additionallyProcessPunnedTextualRecordFieldPattern((FieldName.Textual)fieldName, fieldPattern.getFieldName(), arg);
                    } else {
                        additionallyProcessPunnedTextualDataConsFieldPattern((FieldName.Textual)fieldName, fieldPattern.getFieldName(), nameOfDataConsBeingUnpacked, arg);
                    }
                }
            }

            // call the superclass impl to reach the pattern and visit it (if it is non-null)
            return super.visit_FieldPattern(fieldPattern, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_LocalDefn_PatternMatch_UnpackDataCons(final LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, final T arg) {
            // visit only the patterns
            final Name.DataCons origNameOfDataConsBeingUnpacked = nameOfDataConsBeingUnpacked;
            nameOfDataConsBeingUnpacked = unpackDataCons.getDataConsName();
            try {
                unpackDataCons.getArgBindings().accept(this, arg);
            } finally {
                nameOfDataConsBeingUnpacked = origNameOfDataConsBeingUnpacked;
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_LocalDefn_PatternMatch_UnpackListCons(final LocalDefn.PatternMatch.UnpackListCons unpackListCons, final T arg) {
            // visit only the patterns
            unpackListCons.getHeadPattern().accept(this, arg);
            unpackListCons.getTailPattern().accept(this, arg);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_LocalDefn_PatternMatch_UnpackRecord(final LocalDefn.PatternMatch.UnpackRecord unpackRecord, final T arg) {
            // visit only the field patterns (and not the base record pattern - since we do not support them in local pattern match decl)
            final int nFieldPatterns = unpackRecord.getNFieldPatterns();
            for (int i = 0; i < nFieldPatterns; i++) {
                unpackRecord.getNthFieldPattern(i).accept(this, arg);
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public R visit_LocalDefn_PatternMatch_UnpackTuple(final LocalDefn.PatternMatch.UnpackTuple unpackTuple, final T arg) {
            // visit only the patterns
            final int nPatterns = unpackTuple.getNPatterns();
            for (int i = 0; i < nPatterns; i++) {
                unpackTuple.getNthPattern(i).accept(this, arg);
            }
            return null;
        }
    }
    
    /**
     * Factory method for constructing an empty name resolution context.
     * @param visibilityChecker an external entity visibility checker. Can be null. 
     * @return a new context.
     */
    public static Context makeEmptyContext(final Context.ExternalEntityVisibilityChecker visibilityChecker) {
        return new EmptyContext(visibilityChecker);
    }

    /**
     * Factory method for constructing an external context for resolving identifiers based on a
     * {@link ModuleTypeInfo} instance.
     * @param moduleTypeInfo the backing module type info, on which name resolutions will be based.
     * @return a new context.
     */
    public static Context makeContext(final ModuleTypeInfo moduleTypeInfo) {
        return new ModuleTypeInfoContext(moduleTypeInfo);
    }

    /**
     * Factory method for constructing an external context for resolving identifiers based on a
     * {@link CodeQualificationMap} instance.
     * @param codeQualificationMap the backing code qualification map, on which name resolutions will be based. 
     * @return a new context.
     */
    public static Context makeContext(final CodeQualificationMap codeQualificationMap) {
        return new CodeQualificationMapContext(codeQualificationMap);
    }

    /**
     * Combines two contexts into one, with one acting as the base, and the other acting as
     * the override (the override takes precedence for resolutions).
     * @param baseContext the base context.
     * @param overrideContext the override context - this has precedence over the base context.
     * @return a new context.
     */
    public static Context combineContexts(final Context baseContext, final Context overrideContext) {
        return new OverrideContext(baseContext, overrideContext);
    }
    
    /** Private constructor. Not meant to be instantiated. */
    private IdentifierResolver() {}
}
