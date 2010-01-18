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
 * DeprecationScanner.java
 * Created: Feb 13, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements a compiler pass for finding all entities that are marked
 * as deprecated via a CALDoc deprecated block.
 *
 * @author Joseph Wong
 */
final class DeprecationScanner {
    
    /**
     * A {@link Comparator} for {@link org.openquark.cal.compiler.SourceModel.Name}s that
     * compares both the class and the source form of the names.
     */
    private static final Comparator<SourceModel.Name> SOURCE_MODEL_NAME_COMPARATOR = new Comparator<SourceModel.Name>() {
        public int compare(final SourceModel.Name a, final SourceModel.Name b) {
            final int classNameResult = a.getClass().getName().compareTo(b.getClass().getName());
            if (classNameResult != 0) {
                return classNameResult;
            } else {
                return a.toSourceText().compareTo(b.toSourceText());
            }
        }
    };
    
    /**
     * A set of {@link org.openquark.cal.compiler.SourceModel.Name}s for the deprecated entities collected so far.
     */
    private final Set<SourceModel.Name> deprecatedEntityNames = new TreeSet<SourceModel.Name>(SOURCE_MODEL_NAME_COMPARATOR);
    
    /**
     * The {@link CALCompiler} instance encapsulating this instance.
     */
    private final CALCompiler compiler;

    /**
     * This {@link SourceModelTraverser} implementation scans a CALDoc comment and makes a record of any deprecated blocks
     * that are found.
     *
     * @author Joseph Wong
     */
    private final class DeprecationRecorder extends SourceModelTraverser<Void, Void> {
        
        /**
         * The name of the entity to be recorded as deprecated if a deprecated block is found.
         */
        private final SourceModel.Name entityName;
        
        /**
         * Constructs an instance of this class.
         * @param entityName the name of the entity to be recorded as deprecated if a deprecated block is found.
         */
        DeprecationRecorder(final SourceModel.Name entityName) {
            if (entityName == null) {
                throw new NullPointerException();
            }
            this.entityName = entityName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_CALDoc_TaggedBlock_Deprecated(SourceModel.CALDoc.TaggedBlock.Deprecated deprecatedBlock, Void arg) {
            deprecatedEntityNames.add(entityName);
            // no need to visit the subtree, so just return
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_CALDoc_TextBlock(SourceModel.CALDoc.TextBlock block, Void arg) {
            // Optimization - do not bother with text blocks and their contents
            return null;
        }
    }
    
    /**
     * This {@link SourceModelTraverser} implementation scans a module definition and processes its top level definitions
     * for potential deprecation.
     *
     * @author Joseph Wong
     */
    private final class DeprecatedDefinitionsFinder extends SourceModelTraverser<Void, Void> {
        
        /**
         * The name of the module currently being processed. To be set via a call to {@link #visit_ModuleDefn}.
         */
        private SourceModel.Name.Module currentModuleName;

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_FunctionDefn_Algebraic(final SourceModel.FunctionDefn.Algebraic defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.Function.make(currentModuleName, defn.getName()));
            // no need to visit the subtree, so just return
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_FunctionDefn_Foreign(final SourceModel.FunctionDefn.Foreign defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.Function.make(currentModuleName, defn.getName()));
            // no need to visit the subtree, so just return
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_FunctionDefn_Primitive(final SourceModel.FunctionDefn.Primitive defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.Function.make(currentModuleName, defn.getName()));
            // no need to visit the subtree, so just return
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_ModuleDefn(final SourceModel.ModuleDefn defn, Void arg) {
            currentModuleName = defn.getModuleName();
            
            processCALDocComment(defn.getCALDocComment(), defn.getModuleName());
            // call the superclass implementation to visit the subtree
            return super.visit_ModuleDefn(defn, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeClassDefn_ClassMethodDefn(final SourceModel.TypeClassDefn.ClassMethodDefn defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.Function.make(currentModuleName, defn.getMethodName()));
            // no need to visit the subtree, so just return
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeClassDefn(final SourceModel.TypeClassDefn defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.TypeClass.make(currentModuleName, defn.getTypeClassName()));
            // call the superclass implementation to visit the subtree (for class methods)
            return super.visit_TypeClassDefn(defn, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(final SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.DataCons.make(currentModuleName, defn.getDataConsName()));
            // no need to visit the subtree, so just return
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType(final SourceModel.TypeConstructorDefn.AlgebraicType defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.TypeCons.make(currentModuleName, defn.getTypeConsName()));
            // call the superclass implementation to visit the subtree (for data cons)
            return super.visit_TypeConstructorDefn_AlgebraicType(defn, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_TypeConstructorDefn_ForeignType(final SourceModel.TypeConstructorDefn.ForeignType defn, final Void arg) {
            processCALDocComment(defn.getCALDocComment(), SourceModel.Name.TypeCons.make(currentModuleName, defn.getTypeConsName()));
            // no need to visit the subtree, so just return
            return null;
        }
        
        /**
         * Processes a CALDoc comment associated with the named entity by scanning it for deprecation blocks.
         * @param docComment the CALDoc comment to be processed.
         * @param entityName the name of the entity associated with the comment.
         */
        private void processCALDocComment(final SourceModel.CALDoc docComment, final SourceModel.Name entityName) {
            if (docComment != null) {
                docComment.accept(new DeprecationRecorder(entityName), null);
            }
        }
    }
    
    /**
     * Constructs an instance of this class.
     * @param compiler the {@link CALCompiler} instance encapsulating this instance.
     */
    DeprecationScanner(final CALCompiler compiler) {
        if (compiler == null) {
            throw new NullPointerException();
        }
        this.compiler = compiler;
    }
    
    /**
     * Returns the type info for the named module.
     * @param moduleName a module name.
     * @return the corresponding type info.
     */
    private ModuleTypeInfo getModuleTypeInfo(ModuleName moduleName) {
        return compiler.getPackager().getModuleTypeInfo(moduleName);
    }

    /**
     * Processes a module definition, scanning it for deprecated definitions and recording them.
     * @param moduleDefn the module definition.
     */
    void processModule(final SourceModel.ModuleDefn moduleDefn) {
        moduleDefn.accept(new DeprecatedDefinitionsFinder(), null);
    }
    
    /**
     * Returns whether the named module is deprecated (either having been recorded as deprecated, or is deprecated
     * by virtue of having a deprecated block in the CALDoc comment of the associated {@link ModuleTypeInfo} object).
     * 
     * @param moduleName a module name.
     * @return true if the named module is deprecated, false otherwise.
     */
    boolean isModuleDeprecated(final ModuleName moduleName) {
        final boolean inSet = deprecatedEntityNames.contains(SourceModel.Name.Module.make(moduleName));
        if (inSet) {
            return true;
        }
        
        final ModuleTypeInfo moduleTypeInfo = getModuleTypeInfo(moduleName);
        if (moduleTypeInfo != null) {
            return moduleTypeInfo.isDeprecated();
        }
        
        return false;
    }
    
    /**
     * Returns whether the named type is deprecated (either having been recorded as deprecated, or is deprecated
     * by virtue of having a deprecated block in the CALDoc comment of the associated {@link TypeConstructor} object).
     * 
     * @param typeName a type constructor name.
     * @return true if the named type is deprecated, false otherwise.
     */
    boolean isTypeDeprecated(final QualifiedName typeName) {
        final boolean inSet = deprecatedEntityNames.contains(SourceModel.Name.TypeCons.make(typeName));
        if (inSet) {
            return true;
        }
        
        final ModuleTypeInfo moduleTypeInfo = getModuleTypeInfo(typeName.getModuleName());
        if (moduleTypeInfo != null) {
            final TypeConstructor typeCons = moduleTypeInfo.getTypeConstructor(typeName.getUnqualifiedName());
            if (typeCons != null) {
                return typeCons.isDeprecated();
            }
        }
        
        return false;
    }
    
    /**
     * Returns whether the named data constructor is deprecated (either having been recorded as deprecated, or is deprecated
     * by virtue of having a deprecated block in the CALDoc comment of the associated {@link DataConstructor} object).
     * 
     * @param dataConsName a data constructor name.
     * @return true if the named data constructor is deprecated, false otherwise.
     */
    boolean isDataConsDeprecated(final QualifiedName dataConsName) {
        final boolean inSet = deprecatedEntityNames.contains(SourceModel.Name.DataCons.make(dataConsName));
        if (inSet) {
            return true;
        }
        
        final ModuleTypeInfo moduleTypeInfo = getModuleTypeInfo(dataConsName.getModuleName());
        if (moduleTypeInfo != null) {
            final DataConstructor dataCons = moduleTypeInfo.getDataConstructor(dataConsName.getUnqualifiedName());
            if (dataCons != null) {
                return dataCons.isDeprecated();
            }
        }
        
        return false;
    }
    
    /**
     * Returns whether the named type class is deprecated (either having been recorded as deprecated, or is deprecated
     * by virtue of having a deprecated block in the CALDoc comment of the associated {@link TypeClass} object).
     * 
     * @param typeClassName a type class name.
     * @return true if the named type class is deprecated, false otherwise.
     */
    boolean isTypeClassDeprecated(final QualifiedName typeClassName) {
        final boolean inSet = deprecatedEntityNames.contains(SourceModel.Name.TypeClass.make(typeClassName));
        if (inSet) {
            return true;
        }
        
        final ModuleTypeInfo moduleTypeInfo = getModuleTypeInfo(typeClassName.getModuleName());
        if (moduleTypeInfo != null) {
            final TypeClass typeClass = moduleTypeInfo.getTypeClass(typeClassName.getUnqualifiedName());
            if (typeClass != null) {
                return typeClass.isDeprecated();
            }
        }
        
        return false;
    }
    
    /**
     * Returns whether the named function or class method is deprecated (either having been recorded as deprecated, or is deprecated
     * by virtue of having a deprecated block in the CALDoc comment of the associated {@link FunctionalAgent} object).
     * 
     * @param functionOrClassMethodName the name of a function or class method.
     * @return true if the named function or class method is deprecated, false otherwise.
     */
    boolean isFunctionOrClassMethodDeprecated(final QualifiedName functionOrClassMethodName) {
        final boolean inSet = deprecatedEntityNames.contains(SourceModel.Name.Function.make(functionOrClassMethodName));
        if (inSet) {
            return true;
        }
        
        final ModuleTypeInfo moduleTypeInfo = getModuleTypeInfo(functionOrClassMethodName.getModuleName());
        if (moduleTypeInfo != null) {
            final FunctionalAgent functionOrClassMethod = moduleTypeInfo.getFunctionOrClassMethod(functionOrClassMethodName.getUnqualifiedName());
            if (functionOrClassMethod != null) {
                return functionOrClassMethod.isDeprecated();
            }
        }
        
        return false;
    }
}
