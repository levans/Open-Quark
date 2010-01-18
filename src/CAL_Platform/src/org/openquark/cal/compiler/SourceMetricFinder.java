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
 * SourceDependencyFinder.java
 * Creation date: (Mar 24, 2005)
 * By: Jawright
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.Expr.Application;
import org.openquark.cal.compiler.SourceModel.Expr.BinaryOp;
import org.openquark.cal.compiler.SourceModel.Expr.DataCons;
import org.openquark.cal.compiler.SourceModel.Expr.Lambda;
import org.openquark.cal.compiler.SourceModel.Expr.Let;
import org.openquark.cal.compiler.SourceModel.Expr.Parenthesized;
import org.openquark.cal.compiler.SourceModel.Expr.SelectDataConsField;
import org.openquark.cal.compiler.SourceModel.Expr.UnaryOp;
import org.openquark.cal.compiler.SourceModel.Expr.Unit;
import org.openquark.cal.compiler.SourceModel.Expr.Var;
import org.openquark.cal.compiler.SourceModel.Expr.BinaryOp.Apply;
import org.openquark.cal.compiler.SourceModel.Expr.BinaryOp.BackquotedOperator;
import org.openquark.cal.compiler.SourceModel.Expr.BinaryOp.Compose;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackDataCons;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackListCons;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackListNil;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt.UnpackUnit;
import org.openquark.cal.compiler.SourceModel.Expr.UnaryOp.Negate;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Algebraic;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Foreign;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Primitive;
import org.openquark.cal.compiler.SourceModel.Import.UsingItem;
import org.openquark.cal.compiler.SourceModel.InstanceDefn.InstanceMethod;
import org.openquark.cal.compiler.SourceModel.InstanceDefn.InstanceTypeCons.TypeCons;
import org.openquark.cal.compiler.SourceModel.LocalDefn.Function.Definition;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn.ClassMethodDefn;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.AlgebraicType;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.ForeignType;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn;
import org.openquark.cal.filter.AcceptAllQualifiedNamesFilter;
import org.openquark.cal.filter.QualifiedNameFilter;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.util.ArrayStack;
import org.openquark.util.Pair;


/**
 * Provides methods for finding various metrics associated with CAL modules.
 * 
 * Currently implemented metrics:
 * 
 *    - Reference frequency: Each gem is given an associated reference frequency count, which
 *                           represents the number of times that the gem is referred to in the
 *                           body of other functions.  The raw data collected are the number of
 *                           times each function refers to its dependees (referencees?).  These
 *                           raw data data are aggregated by the ModuleSourceMetrics and 
 *                           WorkspaceSourceMetricsManager classes into the workspace-level
 *                           reference frequency counts that are provided to clients.
 *    
 *    - Compositional frequency: Each pair of gems (A,B) is given an associated frequency count which
 *                               represents the number of times the output of B is passed directly as
 *                               an argument of A.
 *      
 *    - Lint warnings: The source is scanned for potential optimization issues and a list of
 *                     warning objects is returned to the client.
 * 
 *    - Reference and application locations: The source is scanned for references to or applications of
 *                                           a specified QualifiedName, and a list of SourcePositions is
 *                                           returned.   
 * 
 * Creation date: (Mar 24, 2005)
 * @author Jawright
 */
abstract class SourceMetricFinder {

    /** This class should never be instantiated */
    private SourceMetricFinder() {
    }

    /**
     * Provides functionality that is common to most of the SourceModelTraversers in 
     * the SourceMetricFinder class. 
     * 
     * Creation date: (Jun 27, 2005)
     * @author Jawright
     */
    private static abstract class MetricVisitor extends BindingTrackingSourceModelTraverser<Void> {

        /** The name of the module being processed */
        private final ModuleName currentModule;
        
        /** Information about the module being processed */
        private final ModuleTypeInfo moduleTypeInfo;
        
        private MetricVisitor(ModuleTypeInfo moduleTypeInfo) {
            
            if (moduleTypeInfo == null) {
                throw new NullPointerException("MetricVisitor needs a non-null moduleTypeInfo to operate upon");
            }

            this.moduleTypeInfo = moduleTypeInfo;
            this.currentModule = moduleTypeInfo.getModuleName();
        }
        
        /**
         * Resolves the given module name in the context of the module being processed based on module name resolution rules
         * and returns the name of the module the given name resolves to. If the original name
         * is not unambiguously resolvable, then the original name is returned.
         * 
         * @param moduleName the module name to be resolved. Cannot be null.
         * @return the name of the module the original name resolves to. If the original name
         *         is not unambiguously resolvable, then the original name is returned.
         */
        private ModuleName resolveModuleName(Name.Module moduleName) {
            ModuleNameResolver.ResolutionResult resolution = moduleTypeInfo.getModuleNameResolver().resolve(SourceModel.Name.Module.toModuleName(moduleName));
            return resolution.getResolvedModuleName();
        }
        
        /**
         * Check whether a variable reference counts as a reference to a dependee
         * @param varName The variable reference to check
         * @return true if varName refers to a dependee, false otherwise
         */
        private boolean isDependeeReference(Name.Function varName) {
            
            // References to the current function don't count (ie, a function
            // cannot be a dependee of itself).  All unqualified references to
            // the current function name are disqualified, since they either refer
            // to the current function or to a lexically-scoped variable, neither of
            // which counts as a dependee.
            String currentFunctionName = getCurrentFunction();
            if(currentFunctionName != null &&
               currentFunctionName.equals(varName.getUnqualifiedName()) &&
               (varName.getModuleName() == null || resolveModuleName(varName.getModuleName()).equals(currentModule))) {
                return false;
            }
            
            // All other explicitly-qualified references always count as dependees
            // (because they are guaranteed to be toplevel functional agents)
            if(varName.getModuleName() != null) {
                return true;
            }            
            
            // Unqualified references that match lexcially-scoped variables are not dependees
            if(isBound(varName.getUnqualifiedName())) {
                return false;
            }
            
            // Unqualified references that do not match lexically-scoped
            // variables count as dependees.
            return true;
        }
        
        /**
         * Looks up the named functional agent in the relevant ModuleTypeInfo object and returns it.
         * @param agentName The name (qualified or not) of the functional agent to look up.
         * @return The FunctionalAgent for the functional agent specified by entityName
         */
        private FunctionalAgent getFunctionalAgent(Name.Qualifiable agentName) {
            
            if(!(agentName instanceof Name.DataCons || agentName instanceof Name.Function)) {
                return null;
            }
            
            // Explicitly qualified, non-imported agent
            if (agentName.getModuleName() != null && resolveModuleName(agentName.getModuleName()).equals(currentModule)) {
                return moduleTypeInfo.getFunctionalAgent(agentName.getUnqualifiedName());
            }
            
            // Explicitly qualified, imported agent
            else if (agentName.getModuleName() != null) {
                ModuleTypeInfo externalModuleInfo = moduleTypeInfo.getDependeeModuleTypeInfo(resolveModuleName(agentName.getModuleName()));
                if(externalModuleInfo == null) {
                    //  Some sort of compilation problem
                    return null;
                }
                
                return externalModuleInfo.getFunctionalAgent(agentName.getUnqualifiedName());
            }
            
            // Unqualified, non-imported agent
            else if (agentName.getModuleName() == null && moduleTypeInfo.getFunctionalAgent(agentName.getUnqualifiedName()) != null) {
                return moduleTypeInfo.getFunctionalAgent(agentName.getUnqualifiedName());
            }
            
            // Unqualified, imported function
            else {
                ModuleName usingModuleName = moduleTypeInfo.getModuleOfUsingFunctionOrClassMethod(agentName.getUnqualifiedName());
                if (usingModuleName == null) {
                    usingModuleName = moduleTypeInfo.getModuleOfUsingDataConstructor(agentName.getUnqualifiedName());
                }
                
                ModuleTypeInfo externalModuleInfo = moduleTypeInfo.getDependeeModuleTypeInfo(usingModuleName);
                if(externalModuleInfo == null) {
                    // Some sort of compilation problem
                    return null;
                }
                
                return externalModuleInfo.getFunctionalAgent(agentName.getUnqualifiedName());
            }
        }
                
        /** If the provided expression is a Var or DataCons expression, returns the name of the referenced
         * function or datacons.
         * @param expr A Var or DataCons expression
         * @return unqualified name of the function or datacons referred to by the expression
         */
        private Name.Qualifiable getFunctionalAgentName(Expr expr) {
            if(expr instanceof Var) {
                return ((Var)expr).getVarName(); 
            } else if(expr instanceof DataCons) {
                return ((DataCons)expr).getDataConsName();
            } else {
                return null;
            }
        }
    }
    
    /**
     * This is an implementation of SourceModelTraverser that gathers the raw data for the reference-frequency metric.
     * Raw data are collected in a single traversal and stored into the associated EnvEntities.
     * 
     * Creation date: (Mar 24, 2005)
     * @author Jawright
     */
    private static final class ReferenceFrequencyFinder extends MetricVisitor {
        
        /**
         * Map from (dependee, dependent) pair to number of times dependent references dependee
         */
        private final Map<Pair<QualifiedName, QualifiedName>, Integer> dependeeMap = new HashMap<Pair<QualifiedName, QualifiedName>, Integer>();
        
        /**
         * Set of QualifiedNames from imported modules that occur in this module.  
         */
        private final Set<QualifiedName> importedNameOccurrences = new HashSet<QualifiedName>();
        
        /** Filter for deciding whether a function is to be visited */
        private final QualifiedNameFilter functionFilter;
        
        /** When true, functions that are skipped (because they match the excludeFunctionRegexp)
         * will have their names dumped to the system console.  When false, such functions will
         * be skipped silently.
         */
        private final boolean traceSkippedFunctions;
        
        /**
         * Construct a ReferenceFrequencyFinder
         * @param moduleTypeInfo ModuleTypeInfo of module to be scanned
         * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
         * @param traceSkippedFunctions When true, each skipped function will have its name dumped to the console.
         *                               When false, skipped functions will be skipped silently 
         */
        private ReferenceFrequencyFinder(ModuleTypeInfo moduleTypeInfo, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions) {
            super(moduleTypeInfo);
            if (functionFilter == null) {
                throw new NullPointerException();
            }
            this.functionFilter = functionFilter;
            this.traceSkippedFunctions = traceSkippedFunctions;
        }
        
        /** {@inheritDoc} */
        // Don't visit functions matching the exclusion regexp
        @Override
        public Void visit_FunctionDefn_Algebraic(Algebraic algebraic, Object arg) {
            
            if(!functionFilter.acceptQualifiedName(QualifiedName.make(super.currentModule, algebraic.getName()))) {
                if(traceSkippedFunctions) {
                    System.out.println("Skipping test function " + super.currentModule + "." + algebraic.getName());
                }
                return null;
            }
            
            return super.visit_FunctionDefn_Algebraic(algebraic, arg);
        }
        
        /** {@inheritDoc} */
        // A local pattern match declaration with a data cons pattern counts as a reference to the data constructor.
        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackDataCons(LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, Object arg) {
            Name.DataCons dataName = unpackDataCons.getDataConsName();
            recordDependeeReference(dataName);
            
            return super.visit_LocalDefn_PatternMatch_UnpackDataCons(unpackDataCons, arg);
        }

        /** {@inheritDoc} */
        // A local pattern match declaration with a Cons (:) pattern counts as a reference to Prelude.Cons.
        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackListCons(LocalDefn.PatternMatch.UnpackListCons unpackListCons, Object arg) {
            recordDependeeReference(Name.DataCons.make(CAL_Prelude.DataConstructors.Cons));
            return super.visit_LocalDefn_PatternMatch_UnpackListCons(unpackListCons, arg);
        }

        /** {@inheritDoc} */
        // Using a Cons (:) pattern in a case alternative counts as a reference to Prelude.Cons.
        @Override
        public Void visit_Expr_Case_Alt_UnpackListCons(UnpackListCons cons, Object arg) {
            recordDependeeReference(Name.DataCons.make(CAL_Prelude.DataConstructors.Cons));
            return super.visit_Expr_Case_Alt_UnpackListCons(cons, arg);
        }

        /** {@inheritDoc} */
        // Using a data constructor in a case alternative pattern counts as a reference to the data constructor.
        @Override
        public Void visit_Expr_Case_Alt_UnpackDataCons(UnpackDataCons cons, Object arg) {

            for (int i = 0, nDataConsNames = cons.getNDataConsNames(); i < nDataConsNames; i++) {
                Name.DataCons dataConsName = cons.getNthDataConsName(i);
                recordDependeeReference(dataConsName);
            }
            
            return super.visit_Expr_Case_Alt_UnpackDataCons(cons, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Case_Alt_UnpackListNil(UnpackListNil nil, Object arg) {
            recordDependeeReference(Name.DataCons.make(CAL_Prelude.DataConstructors.Nil));
            return super.visit_Expr_Case_Alt_UnpackListNil(nil, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Case_Alt_UnpackUnit(UnpackUnit unit, Object arg) {
            recordDependeeReference(Name.DataCons.make(CAL_Prelude.DataConstructors.Unit));
            return super.visit_Expr_Case_Alt_UnpackUnit(unit, arg);
        }

        /** {@inheritDoc} */
        // Field selection from a dataCons-valued expression counts as a reference to the data constructor.
        @Override
        public Void visit_Expr_SelectDataConsField(SelectDataConsField field, Object arg) {
            Name.DataCons dataName = field.getDataConsName();
            recordDependeeReference(dataName);
            
            return super.visit_Expr_SelectDataConsField(field, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_DataCons(DataCons cons, Object arg) {
            recordDependeeReference(cons.getDataConsName());
            return super.visit_Expr_DataCons(cons, arg);
        }        
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Var(Var var, Object arg) {
            
            if(super.isDependeeReference(var.getVarName())) {
                recordDependeeReference(var.getVarName());
            }
            
            return super.visit_Expr_Var(var, arg);
        }

        // Binary operations are translated to textual form and then
        // recorded as dependees 
        /** {@inheritDoc} */
        @Override
        protected Void visit_Expr_BinaryOp_Helper(BinaryOp binop, Object arg) {
            QualifiedName textualName = OperatorInfo.getTextualName(binop.getOpText());

            // Some operators represent function/method applications, and some (:) represent data constructor applications
            if (textualName.lowercaseFirstChar()) {
                recordDependeeReference(SourceModel.Name.Function.make(textualName));
            } else {
                recordDependeeReference(SourceModel.Name.DataCons.make(textualName));
            }
            return super.visit_Expr_BinaryOp_Helper(binop, arg);
        }
        
        /** {@inheritDoc} */
        // Unary negation is also translated to textual form and then recorded
        // as a dependee
        @Override
        public Void visit_Expr_UnaryOp_Negate(Negate negate, Object arg) {
            recordDependeeReference(SourceModel.Name.Function.make(CAL_Prelude.Functions.negate));
            return super.visit_Expr_UnaryOp_Negate(negate, arg);
        }
        
        /** {@inheritDoc} */
        // The Unit datacons () is translated to textual form and recorded as a dependee
        @Override
        public Void visit_Expr_Unit(Unit unit, Object arg) {
            recordDependeeReference(SourceModel.Name.DataCons.make(CAL_Prelude.DataConstructors.Unit));
            return super.visit_Expr_Unit(unit, arg);
        }
        
        /** {@inheritDoc} */
        // [] is a reference to Prelude.Nil
        @Override
        public Void visit_Expr_List(SourceModel.Expr.List list, Object arg) {
            if(list.getNElements() == 0) {
                recordDependeeReference(SourceModel.Name.DataCons.make(CAL_Prelude.DataConstructors.Nil));
            }
            return super.visit_Expr_List(list, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_Function(UsingItem.Function usingItemFunction, Object arg) {

            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("ReferenceFrequencyFinder.visitImportUsingItemFunction expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemFunction.getUsingNames();
            for (final String usingName : usingNames) {
                recordImportedNameOccurrence(QualifiedName.make(importedModuleName, usingName));
            }
            
            return super.visit_Import_UsingItem_Function(usingItemFunction, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_DataConstructor(UsingItem.DataConstructor usingItemDataConstructor, Object arg) {

            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("ReferenceFrequencyFinder.visitImportUsingItemDataConstructor expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemDataConstructor.getUsingNames();
            for (final String usingName : usingNames) {
                recordImportedNameOccurrence(QualifiedName.make(importedModuleName, usingName));
            }
            
            return super.visit_Import_UsingItem_DataConstructor(usingItemDataConstructor, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_TypeConstructor(UsingItem.TypeConstructor usingItemTypeConstructor, Object arg) {

            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("ReferenceFrequencyFinder.visitImportUsingItemTypeConstructor expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemTypeConstructor.getUsingNames();
            for (final String usingName : usingNames) {
                recordImportedNameOccurrence(QualifiedName.make(importedModuleName, usingName));
            }
            
            return super.visit_Import_UsingItem_TypeConstructor(usingItemTypeConstructor, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_TypeClass(UsingItem.TypeClass usingItemTypeClass, Object arg) {

            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("ReferenceFrequencyFinder.visitImportUsingItemTypeClass expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemTypeClass.getUsingNames();
            for (final String usingName : usingNames) {
                recordImportedNameOccurrence(QualifiedName.make(importedModuleName, usingName));
            }
            
            return super.visit_Import_UsingItem_TypeClass(usingItemTypeClass, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Constraint_TypeClass(SourceModel.Constraint.TypeClass typeClass, Object arg) {

            recordImportedNameOccurrence(typeClass.getTypeClassName());
            return super.visit_Constraint_TypeClass(typeClass, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_Function(TypeExprDefn.Function function, Object arg) {

            recordImportedNameOccurrence(CAL_Prelude.TypeConstructors.Function);
            return super.visit_TypeExprDefn_Function(function, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_List(TypeExprDefn.List list, Object arg) {

            recordImportedNameOccurrence(CAL_Prelude.TypeConstructors.List);
            return super.visit_TypeExprDefn_List(list, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_TypeCons(TypeExprDefn.TypeCons cons, Object arg) {
            
            recordImportedNameOccurrence(cons.getTypeConsName());
            return super.visit_TypeExprDefn_TypeCons(cons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_Unit(TypeExprDefn.Unit unit, Object arg) {
            
            recordImportedNameOccurrence(CAL_Prelude.TypeConstructors.Unit);
            return super.visit_TypeExprDefn_Unit(unit, arg);
        }

        /**
         * Add an entry to the internal tracking set noting that the gem specified by entityName
         * is referred to by the current function.
         * @param entityName Name of the gem referred to by the current function
         */
        private void recordDependeeReference(Name.Qualifiable entityName) {
            FunctionalAgent dependeeEntity = super.getFunctionalAgent(entityName);

            if(dependeeEntity != null) {
                Function currentEntity = super.moduleTypeInfo.getFunction(getCurrentFunction());
                Pair<QualifiedName, QualifiedName> key = new Pair<QualifiedName, QualifiedName>(dependeeEntity.getName(), currentEntity.getName());
                
                if(dependeeMap.get(key) != null) {
                    Integer oldFrequency = dependeeMap.get(key);
                    dependeeMap.put(key, Integer.valueOf(oldFrequency.intValue() + 1));
                } else {
                    dependeeMap.put(key, Integer.valueOf(1));
                }
            }
        
            recordImportedNameOccurrence(entityName);
        }
        
        /**
         * Add an entry to the importedNameOccurrences Set, but not to the dependeeMap.
         * Does nothing if referenceName is from the current module
         * @param occurrenceName QualifiedName to add to tracking
         */
        private void recordImportedNameOccurrence(QualifiedName occurrenceName) {
            
            if(occurrenceName == null) {
                return;
            }
            
            if(occurrenceName.getModuleName().equals(super.currentModule)) {
                return;
            }
            
            importedNameOccurrences.add(occurrenceName);
        }
        
        /**
         * Add an entry to the importedNameOccurrences Set, but not to the dependeeMap.
         * Does nothing if referenceName is from the current module
         * @param occurrenceName Name to add to tracking
         */
        private void recordImportedNameOccurrence(Name.Qualifiable occurrenceName) {
            
            // Short-circuit return won't catch all the cases, but it'll allow us to avoid
            // doing a "using" lookup in some cases.  The QualifiedName-accepting version that
            // we delegate to will catch any same-module cases that fall through this check.
            if(occurrenceName.getModuleName() != null && super.resolveModuleName(occurrenceName.getModuleName()).equals(super.currentModule)) {
                return;
            }
            
            QualifiedName qualifiedName = getQualifiedName(occurrenceName, super.moduleTypeInfo.getModuleNameResolver());
            recordImportedNameOccurrence(qualifiedName);
        }
        
        /**
         * @return A Map from (dependee, dependent) to number of times dependent references dependee
         */
        Map<Pair<QualifiedName, QualifiedName>, Integer> getDependeeMap() {
            return Collections.unmodifiableMap(dependeeMap);
        }
        
        /**
         * @return The Set of names imported from other modules that occur in this module.
         */
        Set<QualifiedName> getImportedNameOccurrences() {
            return Collections.unmodifiableSet(importedNameOccurrences);
        }
    }
        
    /**
     * A SourceModelTraverser that scans a single module for compositional frequencies.
     * Compositional frequency measures how often the return value from one gem is passed
     * directly as an argument to another gem.
     * 
     * For example, in the following module:
     * 
     *      module Foo;
     *      import Prelude;
     * 
     *      bar = 50;
     *      baz arg = 20 + arg;
     * 
     *      quux = Prelude.add bar (baz 54);
     * 
     *      quuux val = Prelude.add (baz 10) (baz val);
     * 
     * The compositional frequency of (Prelude.add, Foo.bar) is 1 (since the output of bar is
     * passed directly to add once in quux).  
     * The compositional frequency of (Prelude.add, Foo.baz) is 3 (since the output of baz is
     * passed directly to add once in quux and twice in quuux).
     * 
     * Use this class by instantiating it bound to a module and then passing in a SourceModel
     * representing the same module to visitModuleDefn.  After the module has been walked, calling
     * getCompositionalFrequencyMap will return a Map from gem pair to compositional frequency of the
     * pair.
     * 
     * The module to walk and all the modules that it imports is assumed to have already been 
     * successfully compiled.
     * 
     * This class has no external side effects.  It is mutable (its state is different after it has
     * walked a module). 
     * 
     * Creation date: (Jun 27, 2005)
     * @author Jawright
     */
    private static final class CompositionalFrequencyFinder extends MetricVisitor {
        
        /**
         * Map from (consumer, producer) Pair to compositional frequency of the pair.
         */  
        private final Map<Pair<QualifiedName, QualifiedName>, Integer> compositionalFrequencyMap = new HashMap<Pair<QualifiedName, QualifiedName>, Integer>();
        
        /** Filter for deciding whether a function is to be visited */
        private final QualifiedNameFilter functionFilter;
        
        /** When true, functions that are skipped (because they match the excludeFunctionRegexp)
         * will have their names dumped to the system console.  When false, such functions will
         * be skipped silently.
         */
        private final boolean traceSkippedFunctions;
        
        private static final QualifiedName COMPOSE = CAL_Prelude.Functions.compose;
        private static final QualifiedName APPLY = CAL_Prelude.Functions.apply;
        
        /**
         * Construct a CompositionalFrequencyFinder for a specific module.
         * @param moduleTypeInfo ModuleTypeInfo for the module to scan for compositional relationships
         * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
         * @param traceSkippedFunctions When true, each skipped function will have its name dumped to the console.
         *                               When false, skipped functions will be skipped silently 
         */
        private CompositionalFrequencyFinder(ModuleTypeInfo moduleTypeInfo, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions) {
            super(moduleTypeInfo);
            if (functionFilter == null) {
                throw new NullPointerException();
            }
            this.functionFilter = functionFilter;
            this.traceSkippedFunctions = traceSkippedFunctions;
        }
        
        /**
         * @return Map from (consumer, producer) to the compositional frequency of the pair.
         */
        Map<Pair<QualifiedName, QualifiedName>, Integer> getCompositionalFrequencyMap() {
            return Collections.unmodifiableMap(compositionalFrequencyMap);
        }
        
        /** {@inheritDoc} */
        // Don't visit functions matching the exclusion regexp
        @Override
        public Void visit_FunctionDefn_Algebraic(Algebraic algebraic, Object arg) {
            
            if(!functionFilter.acceptQualifiedName(QualifiedName.make(super.currentModule, algebraic.getName()))) {
                if(traceSkippedFunctions) {
                    System.out.println("Skipping test function " + super.currentModule + "." + algebraic.getName());
                }
                return null;
            }
            
            return super.visit_FunctionDefn_Algebraic(algebraic, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Application(Application application, Object arg) {

            Expr consumerExpr = application.getNthExpression(0);
            Name.Qualifiable consumerName = getTopLevelConsumerName(consumerExpr);
            FunctionalAgent consumerEntity = null;
            
            if(consumerName != null && consumerExpr instanceof Var) {
                consumerEntity = super.getFunctionalAgent(consumerName);
            }
            
            // Process the standard case where an expression with a statically-identifiable consumer name
            // accepts some arguments
            if(consumerName != null) {
                for(int i = 1; i < application.getNExpressions(); i++) {
                    processArgumentExpression(consumerName, i, application.getNthExpression(i));
                }
            }
            
            // Special cases for apply and compose calls
            if(consumerEntity != null) {
                
                // `apply arg1 arg2` is effectively `arg1 arg2`
                if(consumerEntity.getName().equals(APPLY)) {
                    Expr effectiveConsumer = application.getNthExpression(1);
                    Name.Qualifiable effectiveConsumerName = getTopLevelConsumerName(effectiveConsumer);
                    
                    if(effectiveConsumerName != null) {
                        processArgumentExpression(effectiveConsumerName, 1, application.getNthExpression(2));
                    }
                }
                
                // `compose arg1 arg2 arg3 ... argn` is effectively `arg1 (arg2 arg3 ... argn)`
                if(consumerEntity.getName().equals(COMPOSE)) {
                    Expr effectiveConsumer1 = application.getNthExpression(1);
                    Name.Qualifiable effectiveConsumer1Name = getTopLevelConsumerName(effectiveConsumer1);
                    Expr effectiveConsumer2 = application.getNthExpression(2);
                    Name.Qualifiable effectiveConsumer2Name = getTopLevelConsumerName(effectiveConsumer2);
                    
                    if(effectiveConsumer1Name != null) {
                        processArgumentExpression(effectiveConsumer1Name, 1, effectiveConsumer2);
                    }
                    
                    if(effectiveConsumer2Name != null) {
                        for(int i = 3; i < application.getNExpressions(); i++) {
                            processArgumentExpression(effectiveConsumer2Name, i, application.getNthExpression(i));
                        }
                    }
                }
            }
            
            return super.visit_Expr_Application(application, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_BinaryOp_BackquotedOperator_Var(BackquotedOperator.Var backquotedOperator, Object arg) {
            Expr consumerExpr = backquotedOperator.getOperatorVarExpr();
            Name.Qualifiable consumerName = getTopLevelConsumerName(consumerExpr);

            processArgumentExpression(consumerName, 1, backquotedOperator.getLeftExpr());
            processArgumentExpression(consumerName, 2, backquotedOperator.getRightExpr());
            
            return super.visit_Expr_BinaryOp_BackquotedOperator_Var(backquotedOperator, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_BinaryOp_BackquotedOperator_DataCons(BackquotedOperator.DataCons backquotedOperator, Object arg) {
            Expr consumerExpr = backquotedOperator.getOperatorDataConsExpr();
            Name.Qualifiable consumerName = getTopLevelConsumerName(consumerExpr);

            processArgumentExpression(consumerName, 1, backquotedOperator.getLeftExpr());
            processArgumentExpression(consumerName, 2, backquotedOperator.getRightExpr());
            
            return super.visit_Expr_BinaryOp_BackquotedOperator_DataCons(backquotedOperator, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_BinaryOp_Apply(Apply apply, Object arg) {
            Name.Qualifiable consumerName = getTopLevelConsumerName(apply.getLeftExpr());
            
            // `leftArg $ rightArg` is effectively `leftArg rightArg`
            if(consumerName != null) {
                processArgumentExpression(consumerName, 1, apply.getRightExpr());
            }
            
            return super.visit_Expr_BinaryOp_Apply(apply, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_BinaryOp_Compose(Compose compose, Object arg) {
            Name.Qualifiable consumerName = getTopLevelConsumerName(compose.getLeftExpr());
            
            if(consumerName != null) {
                processArgumentExpression(consumerName, 1, compose.getRightExpr());
            }
            
            return super.visit_Expr_BinaryOp_Compose(compose, arg);
        }
        
        /**
         * Returns the name of the consumer represented by an expression if it is possible to 
         * statically determine this.  So, for DataCons and Var expressions, it just returns
         * the name (if it isn't the name of a bound local variable).  These represent the base
         * cases.
         * 
         * For compose expressions, it is sometimes also possible to statically determine
         * the name of a consumer that will pop out of the expression.
         * 
         * @param arg  An expression which may represent a consumer
         * @return Name of the consumer represented by the expression if statically determinable,
         *          or null otherwise.  
         */
        private Name.Qualifiable getTopLevelConsumerName(Expr arg) {
            
            Expr potentialConsumer;
            //we ignore paren expressions and just consider their contents            
            if (arg instanceof Parenthesized) {
                potentialConsumer = ((Parenthesized)arg).getExpression();
            } else {
                potentialConsumer = arg;
            }
            
            // Standard `foo x` case
            if(potentialConsumer instanceof Var) {
                Name.Function consumerName = ((Var)potentialConsumer).getVarName(); 
                
                if(super.isDependeeReference(consumerName)) {
                    return consumerName;
                } else {
                    return null;
                }

            // Standard `Just x` case    
            } else if(potentialConsumer instanceof DataCons) {
                return ((DataCons)potentialConsumer).getDataConsName();
            
            // Special case for expressions of the form `(compose foo bar) baz`
            } else if(potentialConsumer instanceof Application) {
                Application app = (Application)potentialConsumer;
                Name.Qualifiable consumerName = getTopLevelConsumerName(app.getNthExpression(0));
                
                if(consumerName != null) {
                    FunctionalAgent consumer = super.getFunctionalAgent(consumerName);
                    
                    if(consumer.getName().equals(COMPOSE) && app.getNExpressions() == 3) {
                        return getTopLevelConsumerName(app.getNthExpression(2));
                    }
                }
            
            // Special case for expressions of the form `(foo # bar) baz`
            } else if(potentialConsumer instanceof BinaryOp.Compose) {
                BinaryOp.Compose composeOp = (BinaryOp.Compose)potentialConsumer;
                return getTopLevelConsumerName(composeOp.getRightExpr());
            
            }
            
            return null;
        }
        
        /**
         * Record that the functional agent specified by consumerName consumes the expression
         * argumentExpression as its argumentNumberth parameter.  If either consumerName or 
         * argumentExpression refers to a local function definition, then no action is taken.
         * @param consumerName
         * @param argumentNumber
         * @param argument
         */
        private void processArgumentExpression(Name.Qualifiable consumerName, int argumentNumber, Expr argument) {
            
            FunctionalAgent producer = null;
            FunctionalAgent consumer = super.getFunctionalAgent(consumerName);
            
            Expr argumentExpression;
            //we ignore paren expressions and just consider their contents            
            if (argument instanceof Parenthesized) {
                argumentExpression = ((Parenthesized)argument).getExpression();
            } else {
                argumentExpression = argument;
            }
            
            if(argumentExpression instanceof Var) {
                Var var = (Var)argumentExpression;
                if (super.isDependeeReference(var.getVarName())) {
                    producer = super.getFunctionalAgent(var.getVarName());
                }
            }
            
            else if (argumentExpression instanceof DataCons) {
                DataCons cons = (DataCons)argumentExpression;
                producer = super.getFunctionalAgent(cons.getDataConsName());
            }
            
            else if (argumentExpression instanceof Application) {
                Application app = (Application)argumentExpression;
                processArgumentExpression(consumerName, argumentNumber, app.getNthExpression(0));
                return;
            }
            
            else if (argumentExpression instanceof BinaryOp.Apply) {
                BinaryOp.Apply app = (BinaryOp.Apply)argumentExpression;
                processArgumentExpression(consumerName, argumentNumber, app.getLeftExpr());
                return;
            }
            
            else if (argumentExpression instanceof BinaryOp.Compose) {
                BinaryOp.Compose composeOp = (BinaryOp.Compose)argumentExpression;
                processArgumentExpression(consumerName, argumentNumber, composeOp.getLeftExpr());
                return;
            }
            
            if(producer != null && consumer != null) {
                recordComposition(consumer.getName(), producer.getName());
            }
        }
        
        private void recordComposition(QualifiedName consumer, QualifiedName producer) {
            Pair<QualifiedName, QualifiedName> key = new Pair<QualifiedName, QualifiedName>(consumer, producer);
            Integer frequency = compositionalFrequencyMap.get(key);
            if(frequency != null) {
                compositionalFrequencyMap.put(key, Integer.valueOf(frequency.intValue() + 1));
            } else {
                compositionalFrequencyMap.put(key, Integer.valueOf(1));
            }
        }
    }
    
    /**
     * Represents a warning about a piece of code flagged by the lint process.
     * This is an immutable class.
     * 
     * Creation date: (Jul 8, 2005)
     * @author Jawright
     */
    static final class LintWarning {
        
        /** Typesafe enum representing the type of the warning */
        static final class WarningType {
            
            /** Name of this warning type */
            private final String name;
            
            /** Private constructor for a warning type */
            private WarningType(String name) {
                this.name = name; 
            }
            
            /** {@inheritDoc} */
            @Override
            public String toString() {
                return name;
            }
            
            static final WarningType REDUNDANT_LAMBDA = new WarningType("Possibly redundant lambda");
            static final WarningType UNPLINGED_PRIMITIVE_ARG = new WarningType("Unplinged primitive lexical argument");
            static final WarningType UNUSED_PRIVATE_FUNCTION = new WarningType("Unreferenced private function");
            static final WarningType MISMATCHED_ALIAS_PLINGS = new WarningType("Alias function's arguments do not have same strictness as aliased function/data constructor");
            static final WarningType UNREFERENCED_LET_VARIABLE = new WarningType("Unreferenced let variable");
        }
        
        /** Construct a new LintWarning 
         * 
         * @param warningType Type of problem that we are warning about
         * @param flaggedElement Object representing the SourceElement that caused the warning.  Normally this
         *                        this will just be the SourceElement, but occasionally we may want to custom-print
         *                        the flagged element (eg, for brevity), so a String may also be passed in.
         * @param sourceRange SourceRange of the element that caused the warning
         * @param moduleName Name of the module that the source element causing the warning occurs in
         * @param functionName Name of the top-level function that the source element that caused the warning occurs in. 
         */
        private LintWarning(WarningType warningType, Object flaggedElement, SourceRange sourceRange, 
                    ModuleName moduleName, String functionName) {
            
            if(warningType == null) {
                throw new IllegalArgumentException("LintWarning: warningType must not be null");
            }
            
            if(flaggedElement == null) {
                throw new IllegalArgumentException("LintWarning: flaggedElement must not be null");
            }
            
            if(functionName == null) {
                throw new IllegalArgumentException("LintWarning: functionName must not be null");
            }
            
            if(moduleName == null) {
                throw new IllegalArgumentException("LintWarning: moduleName must not be null");
            }
            
            // (no check for sourcePosition because it is allowed to be null)
            
            this.warningType = warningType;
            this.flaggedElement = flaggedElement;
            this.functionName = QualifiedName.make(moduleName, functionName);
            this.sourceRange = sourceRange;
        }
        
        /** The type of warning */
        private final WarningType warningType;
        
        /** A printable representation of the flagged expression.  This is usually just the 
         * SourceElement that caused the warning, but it will occasionally be a String representing some
         * subset of the element (e.g., the lefthand side of an algebraic function definition). */
        private final Object flaggedElement;
        
        /** The position range of the flagged expression */
        private final SourceRange sourceRange; 
        
        /** The name of the top-level function in which the error occurs */
        private final QualifiedName functionName;
        
        /**
         * @return a string representation of the source element that caused the warning
         */
        String getFlaggedElement() {
            return flaggedElement.toString();
        }

        /**
         * @return the top-level function in which the flagged expression occurs
         */
        QualifiedName getFunctionName() {
            return functionName;
        }
        
        /**
         * @return the start position of the flagged expression, if available, or null if not available
         */
        SourcePosition getSourcePosition() {
            if(sourceRange != null) {
                return sourceRange.getStartSourcePosition();
            } else {
                return null;
            }
        }
        
        /**
         * @return the location of the flagged expression, if available, or null if not available
         */
        SourceRange getSourceRange() {
            return sourceRange;
        }
        
        /**
         * @return the type of the warning
         */
        WarningType getWarningType() {
            return warningType;
        }
    
        /** {@inheritDoc} */
        @Override
        public String toString() {
            if(sourceRange != null) { 
                return warningType + " in " + functionName + " " + sourceRange + ": " + flaggedElement;
            } else {
                return warningType + " in " + functionName + ": " + flaggedElement;
            }
        }
    }
    
    /**
     * A SourceModelTraverser that scans a single module for various style problems that
     * are statically detectable.  Currently scans for the following problems:
     * 
     *   - Redundant lambdas
     *   - Unreferenced private functions
     *   - Lexical arguments of primitive types that are unplinged
     *   - Alias functions whose arguments are plinged incompatibly from the aliased function
     *   - Let variables that are never referenced. 
     * 
     * Warnings are accumulated as a list of LintWarnings, which can be retrieved using the 
     * getWarningList method.
     * 
     * The module to walk and all the modules that it imports is assumed to have already been 
     * successfully compiled.
     * 
     * The let-variable check relies on the assumption that the component SourceElements of the 
     * SourceModel that we walk are not shared (ie, it assumes that the graph is a tree).
     * This assumption can be enforced by copying the incoming tree using the SourceModelCopier;
     * we don't perform that (expensive) step currently because the SourceModelBuilder does 
     * generate trees, and those are the only models that we're currently checking.  
     *  
     * 
     * Creation date: (Jul 4, 2005)
     * @author Jawright
     */
    private static class LintWalker extends MetricVisitor {
        
        /** List of warnings found so far */
        private final List<LintWarning> warningList = new ArrayList<LintWarning>();
        
        /** Filter for deciding which functions will be processed. */
        private final QualifiedNameFilter functionFilter;
        
        /** Set of functions in the module with at least one reference to them within the module. */
        private final Set<QualifiedName> referencedFunctions = new HashSet<QualifiedName>();
        
        /** Map from unqualified function or datacons name to array of parameter strictness */
        private final Map<String, boolean[]> parameterStrictnessMap = new HashMap<String, boolean[]>();
        
        /** Set of SourceElements of bound names whose names have been referenced */
        private final Set<SourceModel.SourceElement> referencedBoundNames = new HashSet<SourceModel.SourceElement>();
        
        /** When true, dump the names of skipped functions to the console */
        private final boolean traceSkippedFunctions;
        
        /** When true, return warnings about unplinged function arguments with primitive types. */
        private final boolean includeUnplingedPrimitiveArgs;
        
        /** When true, return warnings about potentially redundant lambdas */
        private final boolean includeRedundantLambdas;
        
        /** When true, return warnings about unreferenced private functions */
        private final boolean includeUnusedPrivates;
        
        /** When true, return warnings about alias functions whose argument strictness does not exactly match that of the wrapped function. */
        private final boolean includeMismatchedAliasPlings;

        /** When true, return warnings about unused let variables */
        private final boolean includeUnreferencedLetVariables;
        
        /**
         * Construct a LintWalker
         * @param moduleTypeInfo moduleTypeInfo for the module that will be walked
         * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
         * @param traceSkippedFunctions If true, all functions that are skipped will have their names
         *                               dumped to the console.
         * @param includeUnplingedPrimitiveArgs
         * @param includeRedundantLambdas
         * @param includeUnusedPrivates
         * @param includeMismatchedAliasPlings
         * @param includeUnreferencedLetVariables
         */
        private LintWalker(ModuleTypeInfo moduleTypeInfo, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions,
                           boolean includeUnplingedPrimitiveArgs, boolean includeRedundantLambdas, boolean includeUnusedPrivates, boolean includeMismatchedAliasPlings, boolean includeUnreferencedLetVariables) {
            super(moduleTypeInfo);
            if (functionFilter == null) {
                throw new NullPointerException();
            }
            this.functionFilter = functionFilter;
            this.traceSkippedFunctions = traceSkippedFunctions;
            this.includeUnplingedPrimitiveArgs = includeUnplingedPrimitiveArgs;
            this.includeRedundantLambdas = includeRedundantLambdas;
            this.includeUnusedPrivates = includeUnusedPrivates;
            this.includeMismatchedAliasPlings = includeMismatchedAliasPlings; 
            this.includeUnreferencedLetVariables = includeUnreferencedLetVariables;
            
            if(includeUnusedPrivates) {
                computeReferencedFunctions();
            }
        }
        /** get an expressions without parens */    
        private Expr getExprWithoutParen(Expr expr) {
            if (expr instanceof Parenthesized) {
                return ((Parenthesized)expr).getExpression();
            } else {
                return expr;
            }
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_ModuleDefn(ModuleDefn defn, Object arg) {

            // We do a pretraversal to find out about parameter strictness
            if(includeMismatchedAliasPlings) {
                ParameterStrictnessWalker strictnessWalker = new ParameterStrictnessWalker();
                strictnessWalker.visit_ModuleDefn(defn, null);
                parameterStrictnessMap.putAll(strictnessWalker.getStrictnessMap());
            }
            
            return super.visit_ModuleDefn(defn, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Lambda(Lambda lambda, Object arg) {
            boolean isRedundant = false;

            Expr lambdaExp = getExprWithoutParen(lambda.getDefiningExpr());
            
            if(lambdaExp instanceof Application) {
                Application definingExpr = (Application)lambdaExp;
                isRedundant = (definingExpr.getNExpressions() >= lambda.getNParameters() + 1);
                
                // Walk backwards along the parameters so that we catch cases like 
                // (\x -> map add x) 
                int i = lambda.getNParameters() - 1;
                int j = definingExpr.getNExpressions() - 1;
                for(;isRedundant && i >= 0 && j >= 1;i--, j--) {
                    
                    if(!isParameterVar(lambda.getNthParameter(i), definingExpr.getNthExpression(j))) {
                        isRedundant = false;
                    }
                }
                
            } else if(lambdaExp instanceof BinaryOp) {
                BinaryOp definingExpr = (BinaryOp)lambdaExp;
                
                if(lambda.getNParameters() == 2 && 
                   isParameterVar(lambda.getNthParameter(0), definingExpr.getLeftExpr()) &&
                   isParameterVar(lambda.getNthParameter(1), definingExpr.getRightExpr())) {
                    
                    isRedundant = true;
                    
                } else if(lambda.getNParameters() == 1 &&
                   isParameterVar(lambda.getNthParameter(0), definingExpr.getRightExpr()) &&
                   !containsParameterReference(lambda.getNthParameter(0), definingExpr.getLeftExpr())) {
                    
                    isRedundant = true;
                }
            
            } else if(lambdaExp instanceof UnaryOp) {
                UnaryOp definingExpr = (UnaryOp)lambdaExp;
                if(lambda.getNParameters() == 1 &&
                   isParameterVar(lambda.getNthParameter(0), definingExpr.getExpr())) {
                    
                    isRedundant = true;
                }
            }
            
            if(isRedundant && includeRedundantLambdas) {
                warningList.add(new LintWarning(LintWarning.WarningType.REDUNDANT_LAMBDA, lambda, lambda.getSourceRange(), super.currentModule, getCurrentFunction()));
            }
            
            return super.visit_Expr_Lambda(lambda, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionDefn_Algebraic(Algebraic algebraic, Object arg) {
            
            String functionName = algebraic.getName();
            Function functionEntity = super.moduleTypeInfo.getFunction(functionName);
            
            // Don't process functions that match the exclusion expression
            if(!functionFilter.acceptQualifiedName(QualifiedName.make(super.currentModule, algebraic.getName()))) {
                if(traceSkippedFunctions) {
                    System.out.println("Skipping test function " + super.currentModule + "." + algebraic.getName());
                }
                return null;
            }
            
            if(includeUnplingedPrimitiveArgs) {
                checkForUnplingedPrimitives(algebraic, functionName, functionEntity);
            }
            
            if(includeUnusedPrivates) {
                checkForUnusedPrivates(algebraic, functionName, functionEntity);
            }
            
            if(includeMismatchedAliasPlings) {
                checkForMismatchedAliasPlings(algebraic, functionName, functionEntity);
            }
            
            return super.visit_FunctionDefn_Algebraic(algebraic, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionDefn_Foreign(FunctionDefn.Foreign foreign, Object arg) {
            
            String functionName = foreign.getName();
            Function functionEntity = super.moduleTypeInfo.getFunction(functionName);
            
            // Don't process functions that match the exclusion expression
            if(!functionFilter.acceptQualifiedName(QualifiedName.make(super.currentModule, foreign.getName()))) {
                if(traceSkippedFunctions) {
                    System.out.println("Skipping test function " + super.currentModule + "." + foreign.getName());
                }
                return null;
            }
            
            if(includeUnusedPrivates) {
                checkForUnusedPrivates(foreign, functionName, functionEntity);
            }
            
            return super.visit_FunctionDefn_Foreign(foreign, arg);
        }
        
        /** {@inheritDoc} */
        /*  Unused let-variable checks
         *  
         *  We first call the super-method, which will visit each subexpression
         *  of the let-expression with bindings fully set up.  Our override of
         *  visitFunctionName adds the referenced SourceElement to the set
         *  referencedBoundNames each time it encounters a reference to a bound
         *  name during this traversal of the subexpressions.
         *  
         *  So, when the super-method returns, we have a set containing the 
         *  SourceElement of each bound name that was referenced in the 
         *  subexpressions.  We iterate over each of our local definitions;
         *  if the local definition is an element of the referencedBoundNames
         *  set, we remove it (since it scopes out after we return).
         *  If the local defninition is NOT contained in referencedBoundNames,
         *  we spit out a warning about an unreferenced let variable.
         */
        @Override
        public Void visit_Expr_Let(Let let, Object arg) {
            final Void ret = super.visit_Expr_Let(let, arg);
            
            final ModuleName currentModule = super.currentModule;
            
            /**
             * Handles the identification of unused let variables.
             * @author Joseph Wong
             */
            class UnusedLetVariablesCollector extends SourceModelTraverser<Void, Void> {

                @Override
                public Void visit_LocalDefn_Function_Definition(final Definition function, final Void arg) {
                    if(referencedBoundNames.contains(function)) {
                        referencedBoundNames.remove(function);
                    
                    } else if(includeUnreferencedLetVariables) {
                        warningList.add(new LintWarning(LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, function.getName(), function.getSourceRange(), currentModule, getCurrentFunction()));
                    }
                    return null;
                }

                @Override
                public Void visit_Pattern_Var(final Pattern.Var var, final Void arg) {
                    if(referencedBoundNames.contains(var)) {
                        referencedBoundNames.remove(var);
                    
                    } else if(includeUnreferencedLetVariables) {
                        warningList.add(new LintWarning(LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, var.getName(), var.getSourceRange(), currentModule, getCurrentFunction()));
                    }
                    return null;
                }

                @Override
                public Void visit_FieldPattern(final FieldPattern fieldPattern, final Void arg) {
                    // Handle punning
                    if (fieldPattern.getPattern() == null) {
                        // punning.
                        
                        // Textual field names become Vars of the same name.
                        // Ordinal field names become wildcards ("_").
                        final FieldName fieldName = fieldPattern.getFieldName().getName();
                        if (fieldName instanceof FieldName.Textual) {
                            if(referencedBoundNames.contains(fieldPattern)) {
                                referencedBoundNames.remove(fieldPattern);
                            
                            } else if(includeUnreferencedLetVariables) {
                                warningList.add(new LintWarning(LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, fieldName.getCalSourceForm(), fieldPattern.getSourceRange(), currentModule, getCurrentFunction()));
                            }
                        }
                    }
                    
                    // call the superclass impl to reach the pattern and visit it (if it is non-null)
                    return super.visit_FieldPattern(fieldPattern, arg);
                }
                
                @Override
                public Void visit_LocalDefn_PatternMatch_UnpackDataCons(final LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, final Void arg) {
                    // visit only the patterns
                    unpackDataCons.getArgBindings().accept(this, arg);
                    return null;
                }

                @Override
                public Void visit_LocalDefn_PatternMatch_UnpackListCons(final LocalDefn.PatternMatch.UnpackListCons unpackListCons, final Void arg) {
                    // visit only the patterns
                    unpackListCons.getHeadPattern().accept(this, arg);
                    unpackListCons.getTailPattern().accept(this, arg);
                    return null;
                }

                @Override
                public Void visit_LocalDefn_PatternMatch_UnpackRecord(final LocalDefn.PatternMatch.UnpackRecord unpackRecord, final Void arg) {
                    // visit only the field patterns (and not the base record pattern - since we do not support them in local pattern match decl)
                    final int nFieldPatterns = unpackRecord.getNFieldPatterns();
                    for (int i = 0; i < nFieldPatterns; i++) {
                        unpackRecord.getNthFieldPattern(i).accept(this, arg);
                    }
                    return null;
                }

                @Override
                public Void visit_LocalDefn_PatternMatch_UnpackTuple(final LocalDefn.PatternMatch.UnpackTuple unpackTuple, final Void arg) {
                    // visit only the patterns
                    final int nPatterns = unpackTuple.getNPatterns();
                    for (int i = 0; i < nPatterns; i++) {
                        unpackTuple.getNthPattern(i).accept(this, arg);
                    }
                    return null;
                }
            }

            // Run the collector through the defintions
            final UnusedLetVariablesCollector unusedLetVariablesCollector = new UnusedLetVariablesCollector();
            for(int i = 0; i < let.getNLocalDefinitions(); i++) {
                let.getNthLocalDefinition(i).accept(unusedLetVariablesCollector, null);
            }
            
            return ret;
        }


        /** {@inheritDoc} */
        @Override
        public Void visit_Name_Function(Name.Function function, Object arg) {
            
            if(!function.isQualified()) {
                SourceModel.SourceElement boundElement = getBoundElement(function.getUnqualifiedName()); 
                if(boundElement != null) {
                    referencedBoundNames.add(boundElement);
                }
            }

            return super.visit_Name_Function(function, arg);
        }


        /**
         * Check whether a function is private and unreferenced.  If it is, add an appropriate
         * warning to the list.
         * 
         * @param functionDefn SourceModel of the function to check
         * @param functionName name of the function to check
         * @param functionEntity Function corresponding to the function to check
         */
        private void checkForUnusedPrivates(FunctionDefn functionDefn, String functionName, Function functionEntity) {
            if(functionEntity.getScope() == Scope.PRIVATE && !referencedFunctions.contains(functionEntity.getName())) {
                warningList.add(new LintWarning(LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, leftHandString(functionDefn), functionDefn.getNameSourceRange(), super.currentModule, functionName));
            }
        }

        /**
         * Check whether a function has any lexical arguments of primitive type that are not plinged.
         * If it does, then add an approriate LintWarning to the list.
         *  
         * @param algebraic SourceModel of the function to check 
         * @param functionName name of the function to check
         * @param functionEntity Function corresponding to the function to check
         */
        private void checkForUnplingedPrimitives(Algebraic algebraic, String functionName, Function functionEntity) {
            TypeExpr[] arguments = functionEntity.getTypeExpr().getTypePieces();
            
            for(int i = 0; i < arguments.length - 1 && i < algebraic.getNParameters(); i++) {
                Parameter parameter = algebraic.getNthParameter(i);
                
                if(!parameter.isStrict() && isPrimitiveType(arguments[i])) {
                    warningList.add(new LintWarning(LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, parameter, parameter.getSourceRangeOfNameNotIncludingPotentialPling(), super.currentModule, functionName));
                }
            }
        }
        
        /**
         * Check whether a function is an alias function whose plings do not match those of the aliased function
         * @param algebraic SourceModel of the function to check
         * @param functionName name of the function to check
         * @param functionEntity Function corresponding to the function to check
         */
        private void checkForMismatchedAliasPlings(Algebraic algebraic, String functionName, Function functionEntity) {
            
            Expr definingExpr = algebraic.getDefiningExpr();
            Expr aliasedEntityExpr = null;
            
            boolean isPotentialAlias = false;
            if(definingExpr instanceof Application) {
                Application application = (Application)definingExpr;
                aliasedEntityExpr = application.getNthExpression(0);
                
                // An alias is an application of a function or data constructor to exactly the same
                // arguments in exactly the same order.
                if((aliasedEntityExpr instanceof DataCons || aliasedEntityExpr instanceof Var) 
                   && application.getNExpressions() == algebraic.getNParameters() + 1) {
                    
                    isPotentialAlias = true;
                    for(int i = 1; isPotentialAlias && i < application.getNExpressions(); i++) {
                        if(!isParameterVar(algebraic.getNthParameter(i - 1), application.getNthExpression(i))) {
                            isPotentialAlias = false;
                        }
                    }
                }
            }

            if(isPotentialAlias) {
                String aliasedEntityName = null;
                if(super.getFunctionalAgentName(aliasedEntityExpr) != null) {
                    aliasedEntityName = super.getFunctionalAgentName(aliasedEntityExpr).getUnqualifiedName();
                }
                final boolean[] aliasedEntityStrictness = parameterStrictnessMap.get(aliasedEntityName);
                
                // If we can't find the wrapped entity, we can hardly check its argument strictness
                if(aliasedEntityStrictness == null) {
                    return;
                }
                
                // Alias functions and wrapped functions must have precisely the same arity as each other. 
                if( aliasedEntityStrictness.length != algebraic.getNParameters()) {
                    return;
                }
                
                // Alias functions and aliased functions must have precisely the same
                // plinging UP TO the last plinged argument of the alias function.
                // ie, it's okay for the aliased function to have extra plings, so long
                // as they are to the right of the alias function's last pling.
                
                int lastPlingOnAlias = algebraic.getNParameters() - 1;
                for(; lastPlingOnAlias >= 0; lastPlingOnAlias--) {
                    if(algebraic.getNthParameter(lastPlingOnAlias).isStrict()) {
                        break;
                    }
                }
                
                for (int i = 0; i <= lastPlingOnAlias; i++) {
                    if (aliasedEntityStrictness[i] != algebraic.getNthParameter(i).isStrict()) {
                        warningList.add(new LintWarning(LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, algebraic, algebraic.getNameSourceRange(), super.currentModule, algebraic.getName()));
                        return;
                    }
                }
            }
        }

        /**
         * @param type TypeExpr to check
         * @return true if type is a primitive type suitable for unboxing
         */
        private static boolean isPrimitiveType(TypeExpr type) {
            return type.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean) ||                            
                   isForeignPrimitiveDataType(type) ||
                   isEnumDataType(type);
                   
        }

        /**
         * Returns true if type is an enumeration type that is suitable for unboxing.
         * This function is intended to mirror the results of SCJavaDefn.isEnumDataType
         * (which is not visible in the compiler package).
         * 
         * @param type TypeExpr to check
         * @return true if type is an enumeration data type
         */
        private static boolean isEnumDataType(TypeExpr type) {

            TypeConsApp typeConsApp = type.rootTypeConsApp();
            if(typeConsApp == null) { 
                return false;
            }
            
            return TypeExpr.isEnumType(typeConsApp.getRoot());
        }
        
        /**
         * @param type TypeExpr to check
         * @return true if type is a foreign type that represents a primitive,
         *          or false otherwise.
         */
        private static boolean isForeignPrimitiveDataType(TypeExpr type) {

            TypeConsApp typeConsApp = type.rootTypeConsApp();
            if(typeConsApp == null) { 
                return false;
            }
            
            ForeignTypeInfo foreignTypeInfo = typeConsApp.getForeignTypeInfo();
            if(foreignTypeInfo == null) {
                return false;
            }
            
            try {
                return foreignTypeInfo.getForeignType().isPrimitive();
            } catch (UnableToResolveForeignEntityException e) {
                // The foreign Java type cannot be resolved, so we print out the error message, and return the default value (false)
                System.out.println(e.getCompilerMessage().toString());
                return false;
            }
        }
        
        /**
         * @param parameter A parameter
         * @param expression Expression to check
         * @return true if expression is a Var that refers to Parameter (assuming that there are
         *          no intermediate same-name bindings between parameter's scope and expression's).
         */
        private boolean isParameterVar(Parameter parameter, Expr expression) {
            if(expression instanceof Var) {
                Var var = (Var)expression;
                
                return (var.getVarName().getUnqualifiedName().equals(parameter.getName()) &&
                        (var.getVarName().getModuleName() == null || super.resolveModuleName(var.getVarName().getModuleName()).equals(super.currentModule)));
                
            } 
            
            return false;
        }
        
        /**
         * @param functionDefn  An algebraic function definition
         * @return A string representing the left-hand side of the definition
         */
        private String leftHandString(FunctionDefn functionDefn) {
            if (functionDefn instanceof Algebraic) {
                Algebraic algebraic = (Algebraic)functionDefn;

                StringBuilder buffer = new StringBuilder(algebraic.getScope().toString());
                buffer.append(' ');
                buffer.append(algebraic.getName());

                for(int i = 0; i < algebraic.getNParameters(); i++) {
                    buffer.append(' ');
                    buffer.append(algebraic.getNthParameter(i));
                }

                buffer.append(" = ...");

                return buffer.toString();
                
            } else if (functionDefn instanceof Foreign) {
                Foreign foreign = (Foreign)functionDefn;
                
                final StringBuilder buffer = new StringBuilder();
                
                buffer.append("foreign unsafe import jvm ");                

                buffer.append(StringEncoder.encodeString(foreign.getExternalName()));

                if (foreign.isScopeExplicitlySpecified()) {
                    buffer.append(' ').append(foreign.getScope().toString());
                }
                buffer.append(' ').append(foreign.getName()).append(" :: ");
                
                buffer.append("...");
                
                return buffer.toString();

            } else {
                return functionDefn.toString();
            }
        }
        
        /**
         * Helper class for discovering whether an expression contains a reference
         * to a parameter.  The parameter is assumed to be in scope at the level
         * of expression (although expression itself may contain lets or whatnot 
         * that mask the target parameter).
         * 
         * Creation date: (Jul 5, 2005)
         * @author Jawright
         */
        private class ParameterReferenceFinder extends MetricVisitor {
            
            /** True if we encounter a variable referring to targetParameter */
            private boolean foundReference = false;
            
            /** The parameter that we are trying to find references to */
            private final Parameter targetParameter;
            
            /**
             * Construct a ParameterReferenceFinder
             * @param targetParameter Parameter to search for
             */
            ParameterReferenceFinder(Parameter targetParameter, ModuleTypeInfo moduleTypeInfo) {
                super(moduleTypeInfo);
                this.targetParameter = targetParameter;
            }
            
            /**
             * @return true if a variable referring to the targetParameter was found
             */
            boolean getFoundReference() {
                return foundReference;
            }
            
            @Override
            public Void visit_Expr_Var(Var var, Object arg) {
                
                if(!isBound(targetParameter.getName()) && 
                   isParameterVar(targetParameter, var)) {
                    foundReference = true;
                }
                
                return super.visit_Expr_Var(var, arg);
            }
        }
        
        /**
         * Helper class for accumulating the strictness of each parameter of each top-level function in a module.
         * 
         * Creation date: (Jul 14, 2005)
         * @author Jawright
         */
        private class ParameterStrictnessWalker extends SourceModelTraverser<Void, Void> {
            /** Map from unqualified functional agent to its strictness array (array of isStrict flags for each parameter) */
            private final Map<String, boolean[]> strictnessMap = new HashMap<String, boolean[]>();
            
            /** @return Map from unqualified functional agent to its strictness array (array of isStrict flags for each parameter) */
            Map<String, boolean[]> getStrictnessMap() {
                return Collections.unmodifiableMap(strictnessMap);
            }
            
            /** {@inheritDoc} */
            @Override
            public Void visit_FunctionDefn_Algebraic(Algebraic algebraic, Void arg) {

                boolean[] parameterStrictness = new boolean[algebraic.getNParameters()];
                for(int i = 0; i < algebraic.getNParameters(); i++) {
                    parameterStrictness[i] = algebraic.getNthParameter(i).isStrict();
                }
                
                strictnessMap.put(algebraic.getName(), parameterStrictness);
                
                return super.visit_FunctionDefn_Algebraic(algebraic, arg);
            }
            
            /** {@inheritDoc} */
            @Override
            public Void visit_FunctionDefn_Foreign(Foreign foreign, Void arg) {
                
                int arity = getArity(foreign.getDeclaredType().getTypeExprDefn());
                boolean[] parameterStrictness = new boolean[arity];
                for(int i = 0; i < arity; i++) {
                    parameterStrictness[i] = true;  // Foreign functions are fully strict
                }
                strictnessMap.put(foreign.getName(), parameterStrictness);
                
                return super.visit_FunctionDefn_Foreign(foreign, arg);
            }
            
            @Override
            public Void visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(DataConsDefn defn, Void arg) {

                boolean[] parameterStrictness = new boolean[defn.getNTypeArgs()];
                for(int i = 0; i < defn.getNTypeArgs(); i++) {
                    parameterStrictness[i] = defn.getNthTypeArg(i).isStrict();
                }
                strictnessMap.put(defn.getDataConsName(), parameterStrictness);
                
                return super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg);
            }

            /**
             * Returns the arity of the function represented by typeExprDefn. e.g., a function of
             * type (Foo -> Bar -> Baz) has arity of 2. 
             * @param typeExprDefn A TypeExprDefn
             * @return int the arity of typeExprDefn 
             */
            private int getArity(TypeExprDefn typeExprDefn) {
                if(typeExprDefn instanceof TypeExprDefn.Function) {
                    TypeExprDefn.Function functionType = (TypeExprDefn.Function)typeExprDefn; 
                    return 1 + getArity(functionType.getCodomain());
                } else {
                    return 0;
                }
            }
        }
        
        /**
         * Checks whether expression contains a reference to the specified parameter.  Parameter
         * is assumed to be in scope at the level of expression.  Subexpressions that 
         * make bindings that hide parameter will be handled correctly.
         * @param parameter The parameter to check for references to
         * @param expression The expression to check
         * @return true if expression contains references to parameter
         */
        private boolean containsParameterReference(final Parameter parameter, Expr expression) {
            ParameterReferenceFinder parameterReferenceFinder = new ParameterReferenceFinder(parameter, super.moduleTypeInfo); 
            
            expression.accept(parameterReferenceFinder, ArrayStack.make());
            return parameterReferenceFinder.getFoundReference();
        }
    
        /**
         * Fill the referencedFunctions Set with every function in the current module 
         * that has been called from within the current module. 
         *
         */
        private void computeReferencedFunctions() {

            // First find all of the private functions that get referenced as default methods of some type class
            for(int i = 0; i < super.moduleTypeInfo.getNTypeClasses(); i++) {
                TypeClass typeClass = super.moduleTypeInfo.getNthTypeClass(i);
                
                for(int j = 0; j < typeClass.getNClassMethods(); j++) {
                    ClassMethod classMethod = typeClass.getNthClassMethod(j);
                    final QualifiedName defaultClassMethodName = classMethod.getDefaultClassMethodName();
                    if (defaultClassMethodName != null) {
                        referencedFunctions.add(defaultClassMethodName);
                    }
                }
            }
            
            // Then find all of the private functions that get referenced as methods of some instance 
            for(int i = 0; i < super.moduleTypeInfo.getNClassInstances(); i++) {
                ClassInstance instance = super.moduleTypeInfo.getNthClassInstance(i);
                
                for(int j = 0; j < instance.getNInstanceMethods(); j++) {
                    QualifiedName methodName = instance.getInstanceMethod(j);
                    if (methodName != null) {
                        //will be null in the case when an instance does not define the class method i.e. the intent is to use
                        //the default class method
                        referencedFunctions.add(methodName);
                    }
                }
            }
            
            // Now walk over all of the functions in the module checking who references whom
            for(int i = 0; i < super.moduleTypeInfo.getNFunctions(); i++) {
                Function function = super.moduleTypeInfo.getNthFunction(i);
                
                for (final QualifiedName dependeeName : function.getDependeeToFrequencyMap().keySet()) {
                    if(dependeeName.getModuleName().equals(super.currentModule)) {
                        referencedFunctions.add(dependeeName);
                    }
                }            
            }
        }
        
        /**
         * @return The list of warnings found by this traversal
         */
        List<LintWarning> getWarningList() {
            return Collections.unmodifiableList(warningList);
        }
    }
        
    /** Typesafe enum representing the possible types of search */
    static final class SearchType {
        
        /** Name of this search type */
        private final String name;
        
        /** Private constructor for a warning type */
        private SearchType(String name) {
            this.name = name; 
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name;
        }

        static final SearchType ALL = new SearchType("Find all occurrences of an entity");
        static final SearchType REFERENCES = new SearchType("Find references in expressions to a gem");
        static final SearchType DEFINITION = new SearchType("Find all top-level definitions of a type or gem");
        static final SearchType INSTANCES = new SearchType("Find instances of a type class");
        static final SearchType CLASSES = new SearchType("Find all classes that a type is an instance of");
        static final SearchType POSITION = new SearchType("Find the entity defined at the given location");
        static final SearchType REFERENCES_CONSTRUCTIONS = new SearchType("Find all constructions of the given data type.");
        static final SearchType SOURCE_TEXT = new SearchType("Find the source text for the entity defined at the given location");
    }
        
    /**
     * A SourceModelTraverser used to perform searches on a particular QualifiedName.  Behaviour
     * is defined for each type in the SearchType enumeration.
     * 
     * Creation date: (Jul 28, 2005)
     * @author Jawright
     */
    private static final class SearchWalker extends MetricVisitor {
        
        /** List of QualifiedNames of the entities to search for. Will be null if the search type is POSITION. */
        private final List<? extends org.openquark.cal.compiler.Name> targetNames;
        
        /** Type of search to perform */
        private final SearchType searchType;
        
        /** List of locations that have matched the search criteria */
        private final List<SearchResult.Precise> searchResults = new ArrayList<SearchResult.Precise>();
        
        /**
         * The position of the symbol to search for. Only set if the searchType is POSITION.
         */
        private final SourcePosition searchPosition;
        
        /**
         * Construct a new SearchWalker
         * @param moduleTypeInfo ModuleTypeInfo of module to search.  It is assumed to have been compiled already.
         * @param targetNames List of QualifiedName of the entities to search for
         * @param searchType Type of search to perform (i.e., for references, for applications, etc.)
         * @param searchPosition The position of the symbol to search for. This will only be set if the searchType is position.
         */
        private SearchWalker(ModuleTypeInfo moduleTypeInfo, List<? extends org.openquark.cal.compiler.Name> targetNames, SearchType searchType, SourcePosition searchPosition) {
            super(moduleTypeInfo);
            
            if(targetNames == null && searchType != SearchType.POSITION) {
                throw new IllegalArgumentException("SearchWalker must be given a non-null list of targetNames");
            }
            if(searchType == SearchType.POSITION && searchPosition == null){
                throw new IllegalArgumentException("SearchWalker must be given a non-null sourcePosition if the search type is by position");
            }
            
            this.targetNames = targetNames;
            this.searchType = searchType;
            this.searchPosition = searchPosition;
        }

        @Override
        public Void visit_Name_Module(Name.Module moduleName, Object arg) {
            final SourceRange sourceRange = moduleName.getSourceRange();
            if (searchType == SearchType.POSITION){
                if (sourceRange.containsPositionInclusive(searchPosition)){
                    addSearchResult(sourceRange, super.resolveModuleName(moduleName), SourceIdentifier.Category.MODULE_NAME);
                }
            }
            else{
                final ModuleName candidateName = SourceModel.Name.Module.toModuleName(moduleName);
                for (final org.openquark.cal.compiler.Name targetName : targetNames) {
                    if(targetName.equals(candidateName)) {
                        addSearchResult(sourceRange, candidateName, SourceIdentifier.Category.MODULE_NAME);
                    }
                }
            }
            return super.visit_Name_Module(moduleName, arg); 
        }

        /** (@inheritDoc} */
        @Override
        public Void visit_Import(Import importStmt, Object arg) {
            final SourceRange sourceRange = importStmt.getImportedModuleName().getSourceRange();
            if (searchType == SearchType.POSITION){
                if (sourceRange.containsPositionInclusive(searchPosition)){
                    addSearchResult(sourceRange, SourceModel.Name.Module.toModuleName(importStmt.getImportedModuleName()), SourceIdentifier.Category.MODULE_NAME);
                }
            }
            else{
                final ModuleName candidateName = SourceModel.Name.Module.toModuleName(importStmt.getImportedModuleName());
                for (final org.openquark.cal.compiler.Name targetName : targetNames) {
                    if(targetName.equals(candidateName)) {
                        addSearchResult(sourceRange, candidateName, SourceIdentifier.Category.MODULE_NAME);
                    }
                }
            }

            return super.visit_Import(importStmt, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_Function(UsingItem.Function usingItemFunction, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("SearchWalker.visitImportUsingItemFunction expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemFunction.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemFunction.getUsingNameSourceRanges();

            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                    checkReference(QualifiedName.make(importedModuleName, usingNames[i]), usingNameSourceRanges[i], SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                }
            }
            
            return super.visit_Import_UsingItem_Function(usingItemFunction, arg);
        }        
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_DataConstructor(UsingItem.DataConstructor usingItemDataConstructor, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("SearchWalker.visitImportUsingItemDataConstructor expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemDataConstructor.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemDataConstructor.getUsingNameSourceRanges();

            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                    checkReference(QualifiedName.make(importedModuleName, usingNames[i]), usingNameSourceRanges[i], SourceIdentifier.Category.DATA_CONSTRUCTOR);
                }
            }
            
            return super.visit_Import_UsingItem_DataConstructor(usingItemDataConstructor, arg);
        }        
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_TypeConstructor(UsingItem.TypeConstructor usingItemTypeConstructor, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("SearchWalker.visitImportUsingItemTypeConstructor expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemTypeConstructor.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemTypeConstructor.getUsingNameSourceRanges();

            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                    checkReference(QualifiedName.make(importedModuleName, usingNames[i]), usingNameSourceRanges[i], SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                }
            }
            
            return super.visit_Import_UsingItem_TypeConstructor(usingItemTypeConstructor, arg);
        }        
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Import_UsingItem_TypeClass(UsingItem.TypeClass usingItemTypeClass, Object arg) {
            if(arg == null || !(arg instanceof ModuleName)) {
                throw new IllegalArgumentException("SearchWalker.visitImportUsingItemTypeClass expects to be passed a module name as its arg");
            }
            
            ModuleName importedModuleName = (ModuleName)arg;
            String[] usingNames = usingItemTypeClass.getUsingNames();
            SourceRange[] usingNameSourceRanges = usingItemTypeClass.getUsingNameSourceRanges();

            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                    checkReference(QualifiedName.make(importedModuleName, usingNames[i]), usingNameSourceRanges[i], SourceIdentifier.Category.TYPE_CLASS);
                }
            }
            
            return super.visit_Import_UsingItem_TypeClass(usingItemTypeClass, arg);
        }        
        
        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionDefn_Algebraic(Algebraic algebraic, Object arg) {

            if (searchType == SearchType.POSITION){
                if (algebraic.getNameSourceRange().containsPositionInclusive(searchPosition)){
                    addSearchResult(algebraic.getNameSourceRange(), toQualifiedName(algebraic.getName()), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if (
                                algebraic.getName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())){
                            if(
                                    searchType == SearchType.DEFINITION || 
                                    searchType == SearchType.ALL ||
                                    searchType == SearchType.POSITION
                            ){
                                addSearchResult(algebraic.getNameSourceRange(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                            else if (searchType == SearchType.SOURCE_TEXT){
                                addSearchResult(algebraic.getSourceRange(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                        }
                    }
                }
            }

            return super.visit_FunctionDefn_Algebraic(algebraic, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionDefn_Primitive(Primitive primitive, Object arg) {

            if (searchType == SearchType.POSITION){
                if (primitive.getNameSourceRange().containsPositionInclusive(searchPosition)){
                    addSearchResult(primitive.getNameSourceRange(), toQualifiedName(primitive.getName()), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if (primitive.getName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            if(searchType == SearchType.DEFINITION || 
                               searchType == SearchType.ALL || 
                               searchType == SearchType.POSITION){
                                addSearchResult(primitive.getNameSourceRange(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                            else if (searchType == SearchType.SOURCE_TEXT){
                                addSearchResult(primitive.getSourceRangeExcludingCaldoc(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                        }
                    }
                }
            }
            
            return super.visit_FunctionDefn_Primitive(primitive, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_Function(InstanceDefn.InstanceTypeCons.Function function, Object arg) {
            if((searchType == SearchType.CLASSES || searchType == SearchType.ALL || searchType == SearchType.POSITION)) {
                checkReference(CAL_Prelude.TypeConstructors.Function, function.getOperatorSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }
            return super.visit_InstanceDefn_InstanceTypeCons_Function(function, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_List(InstanceDefn.InstanceTypeCons.List list, Object arg) {
            if((searchType == SearchType.CLASSES || searchType == SearchType.ALL || searchType == SearchType.POSITION)) {
                checkReference(CAL_Prelude.TypeConstructors.List, list.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }
            return super.visit_InstanceDefn_InstanceTypeCons_List(list, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_TypeCons(TypeCons cons, Object arg) {
            if((searchType == SearchType.CLASSES || searchType == SearchType.ALL || searchType == SearchType.POSITION)) {
                checkReference(cons.getTypeConsName(), cons.getSourceRangeOfName(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }
            return super.visit_InstanceDefn_InstanceTypeCons_TypeCons(cons, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_Unit(InstanceDefn.InstanceTypeCons.Unit unit, Object arg) {
            if((searchType == SearchType.CLASSES || searchType == SearchType.ALL || searchType == SearchType.POSITION)) {
                checkReference(CAL_Prelude.TypeConstructors.Unit, unit.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }
            return super.visit_InstanceDefn_InstanceTypeCons_Unit(unit, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_InstanceDefn(InstanceDefn defn, Object arg) {

            // Override default visitation order to ensure that constraints
            // are visited before the typeclass or typecons.  This ensures
            // that results will be found in textual order.
            if (defn.getCALDocComment() != null) {
                defn.getCALDocComment().accept(this, arg);
            }

            final int nConstraints = defn.getNConstraints();
            for (int i = 0; i < nConstraints; i++) {
                defn.getNthConstraint(i).accept(this, arg);
            }
    
            if(searchType == SearchType.INSTANCES || searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                Name.TypeClass typeClassName = defn.getTypeClassName(); 
                checkReference(typeClassName, typeClassName.getSourceRange(), SourceIdentifier.Category.TYPE_CLASS);
            } 
            
            defn.getTypeClassName().accept(this, arg);
            defn.getInstanceTypeCons().accept(this, arg);
    
            final int nInstanceMethods = defn.getNInstanceMethods();
            for (int i = 0; i < nInstanceMethods; i++) {
                defn.getNthInstanceMethod(i).accept(this, arg);
            }
    
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_InstanceDefn_InstanceMethod(InstanceMethod method, Object arg) {

            if(searchType == SearchType.REFERENCES || searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(method.getResolvingFunctionName(), method.getSourceRangeOfResolvingFunctionName(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
            }
            
            return super.visit_InstanceDefn_InstanceMethod(method, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_TypeClassDefn(TypeClassDefn defn, Object arg) {
            
            if (searchType == SearchType.POSITION){
                if (defn.getSourceRangeOfName().containsPositionInclusive(searchPosition)){
                    addSearchResult(defn.getSourceRangeOfName(), toQualifiedName(defn.getTypeClassName()), SourceIdentifier.Category.TYPE_CLASS);
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if (defn.getTypeClassName().equals(targetName.getUnqualifiedName()) &&
                            super.currentModule.equals(targetName.getModuleName())){
                            if (searchType == SearchType.DEFINITION || searchType == SearchType.ALL || searchType == SearchType.POSITION){
                                addSearchResult(defn.getSourceRangeOfName(), targetName, SourceIdentifier.Category.TYPE_CLASS);
                            }
                            else if (searchType == SearchType.SOURCE_TEXT){
                                addSearchResult(defn.getSourceRangeOfDefn(), targetName, SourceIdentifier.Category.TYPE_CLASS);
                            }
                        }
                    }
                }
            }
            
            return super.visit_TypeClassDefn(defn, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_TypeClassDefn_ClassMethodDefn(ClassMethodDefn defn, Object arg) {

            if (searchType == SearchType.POSITION){
                if (defn.getSourceRangeOfName() != null){
                    if (defn.getSourceRangeOfName().containsPositionInclusive(searchPosition)){
                        addSearchResult(defn.getSourceRangeOfName(), toQualifiedName(defn.getMethodName()), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                    }
                }
                if (defn.getDefaultClassMethodName() != null){
                    if (defn.getDefaultClassMethodName().getSourceRange().containsPositionInclusive(searchPosition)){
                        addSearchResult(defn.getDefaultClassMethodName().getSourceRange(), toQualifiedName(defn.getDefaultClassMethodName()), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                    }
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if (defn.getMethodName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            if(searchType == SearchType.DEFINITION || searchType == SearchType.ALL || searchType == SearchType.POSITION){
                                addSearchResult(defn.getSourceRangeOfName(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                            else if (searchType == SearchType.SOURCE_TEXT){
                                addSearchResult(defn.getSourceRangeOfClassDefn(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                        }
                    }
                }
                
                if (searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                    Name.Function defaultClassMethodName = defn.getDefaultClassMethodName();
                    if (defaultClassMethodName != null) {
                        checkReference(defaultClassMethodName, defaultClassMethodName.getSourceRange(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                    }
                }
            }

            return super.visit_TypeClassDefn_ClassMethodDefn(defn, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionTypeDeclaraction(FunctionTypeDeclaration declaration, Object arg) {        
            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(QualifiedName.make(super.currentModule, declaration.getFunctionName()), declaration.getSourceRangeOfName(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
            }
            
            return super.visit_FunctionTypeDeclaraction(declaration, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Constraint_TypeClass(SourceModel.Constraint.TypeClass typeClass, Object arg) {
            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(typeClass.getTypeClassName(), typeClass.getTypeClassName().getSourceRange(), SourceIdentifier.Category.TYPE_CLASS);
            }
            return super.visit_Constraint_TypeClass(typeClass, arg);
        }

        
        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_Function(TypeExprDefn.Function function, Object arg) {
            // Force an in-order traversal rather than relying on the super method to
            // make the recursive calls.  Doing an in-order traversal will ensure that the
            // the results are returned in source-order without needing to be explicitly sorted.
            function.getDomain().accept(this, arg);

            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(CAL_Prelude.TypeConstructors.Function, function.getOperatorSourceRange(), null);
            }

            function.getCodomain().accept(this, arg);
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_List(TypeExprDefn.List list, Object arg) {
            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(CAL_Prelude.TypeConstructors.List, list.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }
            return super.visit_TypeExprDefn_List(list, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_TypeCons(TypeExprDefn.TypeCons cons, Object arg) {
            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(cons.getTypeConsName(), cons.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }
            return super.visit_TypeExprDefn_TypeCons(cons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeExprDefn_Unit(TypeExprDefn.Unit unit, Object arg) {
            if(searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(CAL_Prelude.TypeConstructors.Unit, unit.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }
            return super.visit_TypeExprDefn_Unit(unit, arg);
        }

        private QualifiedName toQualifiedName(String unqualifiedName){
            return QualifiedName.make(super.currentModule, unqualifiedName);
        }
        
        private QualifiedName toQualifiedName(Name.Function name){
            if (name.getModuleName() == null){
                return toQualifiedName(name.getUnqualifiedName());
            }
            else{
                return QualifiedName.make(super.resolveModuleName(name.getModuleName()), name.getUnqualifiedName());
            }
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType(AlgebraicType type, Object arg) {            
            if (searchType == SearchType.POSITION){
                if(type.getSourceRangeOfName().containsPositionInclusive(searchPosition)){
                    addSearchResult(type.getSourceRangeOfName(), toQualifiedName(type.getTypeConsName()), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                }
                
                Name.TypeClass[] derivedTypeClasses = type.getDerivingClauseTypeClassNames();
                for (final SourceModel.Name.TypeClass typeClassName : derivedTypeClasses) {
                    checkReference(typeClassName, typeClassName.getSourceRange(), SourceIdentifier.Category.TYPE_CLASS);
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if((searchType == SearchType.DEFINITION || searchType == SearchType.ALL) &&
                                type.getTypeConsName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            addSearchResult(type.getSourceRangeOfName(), targetName, SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                        }

                        if((searchType == SearchType.SOURCE_TEXT) &&
                            type.getTypeConsName().equals(targetName.getUnqualifiedName()) &&
                            super.currentModule.equals(targetName.getModuleName())) {
                            addSearchResult(type.getSourceRangeOfDefn(), targetName, SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                        }

                        if(searchType == SearchType.INSTANCES || searchType == SearchType.ALL) { 
                            Name.TypeClass[] derivedTypeClasses = type.getDerivingClauseTypeClassNames();
                            for (final Name.TypeClass typeClassName : derivedTypeClasses) {
                                checkReference(typeClassName, typeClassName.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                            }
                        } 

                        if((searchType == SearchType.CLASSES || searchType == SearchType.ALL) &&
                                type.getTypeConsName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            Name.TypeClass[] derivedTypeClasses = type.getDerivingClauseTypeClassNames();
                            for (final Name.TypeClass derivedTypeClass : derivedTypeClasses) {
                                addSearchResult(derivedTypeClass.getSourceRange(), targetName, SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                            }
                        }
                    }
                }
            }
            
            return super.visit_TypeConstructorDefn_AlgebraicType(type, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(DataConsDefn defn, Object arg) {

            if (searchType == SearchType.POSITION){
                if (defn.getSourceRangeOfName().containsPositionInclusive(searchPosition)){
                    addSearchResult(defn.getSourceRangeOfName(), toQualifiedName(defn.getDataConsName()), SourceIdentifier.Category.DATA_CONSTRUCTOR);
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if (defn.getDataConsName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())){
                            if(searchType == SearchType.DEFINITION || searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                                addSearchResult(defn.getSourceRangeOfName(), targetName, SourceIdentifier.Category.DATA_CONSTRUCTOR);
                            }
                            else if (searchType == SearchType.SOURCE_TEXT){
                                addSearchResult(defn.getSourceRangeOfDefn(), targetName, SourceIdentifier.Category.DATA_CONSTRUCTOR);
                            }
                        }
                    }
                }
            }
            
            return super.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(defn, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_FunctionDefn_Foreign(Foreign foreign, Object arg) {
            if (searchType == SearchType.POSITION){
                // TODO in the future have the goto definition find the definition in the java code. 
                // That would be cool.
                if (foreign.getNameSourceRange().containsPositionInclusive(searchPosition)){
                    addSearchResult(foreign.getNameSourceRange(), toQualifiedName(foreign.getName()), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                }
                
                if (foreign.getExternalNameSourceRange().containsPositionInclusive(searchPosition)){
                    addSearchResult(foreign.getSourceRange(), toQualifiedName(foreign.getName()), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, true);                
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if (foreign.getName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            if((searchType == SearchType.DEFINITION || searchType == SearchType.ALL || searchType == SearchType.POSITION)) {
                                addSearchResult(foreign.getNameSourceRange(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                            else if (searchType == SearchType.SOURCE_TEXT){
                                addSearchResult(foreign.getSourceRangeExcludingCaldoc(), targetName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                            }
                        }
                    }
                }
            }
            return super.visit_FunctionDefn_Foreign(foreign, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_TypeConstructorDefn_ForeignType(ForeignType type, Object arg) {
            if (searchType == SearchType.POSITION){          
                if (type.getSourceRangeOfName().containsPositionInclusive(searchPosition)){
                    addSearchResult(type.getSourceRangeOfName(), toQualifiedName(type.getTypeConsName()), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                }
                
                if (type.getExternalNameSourceRange().containsPositionInclusive(searchPosition)){
                    addSearchResult(type.getSourceRangeOfName(), toQualifiedName(type.getTypeConsName()), SourceIdentifier.Category.TYPE_CONSTRUCTOR, true);                
                }
                
                // TODO in the future have the goto definition find the definition in the java code. 
                // That would be cool.
                for(int i = 0; i < type.getNDerivingClauseTypeClassNames(); i++) {
                    Name.TypeClass typeClassName = type.getDerivingClauseTypeClassName(i);
                    checkReference(typeClassName, typeClassName.getSourceRange(), SourceIdentifier.Category.TYPE_CLASS);
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if((searchType == SearchType.DEFINITION || searchType == SearchType.ALL || searchType == SearchType.POSITION) &&
                                type.getTypeConsName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            addSearchResult(type.getSourceRangeOfName(), targetName, SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                        }

                        if((searchType == SearchType.SOURCE_TEXT) &&
                                type.getTypeConsName().equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            addSearchResult(type.getSourceRangeOfDefn(), targetName, SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                        }
                        
                        if(searchType == SearchType.INSTANCES || searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                            for(int i = 0; i < type.getNDerivingClauseTypeClassNames(); i++) {
                                Name.TypeClass typeClassName = type.getDerivingClauseTypeClassName(i);
                                checkReference(typeClassName, typeClassName.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                            }
                        }

                        if((searchType == SearchType.CLASSES || searchType == SearchType.ALL || searchType == SearchType.POSITION) &&
                                targetName.getUnqualifiedName().equals(type.getTypeConsName()) &&
                                targetName.getModuleName().equals(super.currentModule)) {
                            for(int i = 0; i < type.getNDerivingClauseTypeClassNames(); i++) {
                                final Name.TypeClass typeClass = type.getDerivingClauseTypeClassName(i);
                                checkReference(typeClass, typeClass.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR);
                            }
                        }
                    }
                }
            }
            
            return super.visit_TypeConstructorDefn_ForeignType(type, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_DataCons(DataCons cons, Object arg) {
            
            // No work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.REFERENCES_CONSTRUCTIONS && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_Expr_DataCons(cons, arg);
            }
            
            Name.Qualifiable candidateName = cons.getDataConsName();
            
            // If this is a reference to one of the targetNames, add it to the list
            checkReference(candidateName, cons.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            
            return super.visit_Expr_DataCons(cons, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackDataCons(LocalDefn.PatternMatch.UnpackDataCons unpackDataCons, Object arg) {
            
            // Process the pattern-bound variables first
            processLocalPatternMatchDeclarationPattern(unpackDataCons.getArgBindings());
            
            // No further work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_LocalDefn_PatternMatch_UnpackDataCons(unpackDataCons, arg);
            }

            Name.Qualifiable candidateName = unpackDataCons.getDataConsName();    

            // If this is a reference to one of the targetNames, add it to the list
            checkReference(candidateName, candidateName.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);

            return super.visit_LocalDefn_PatternMatch_UnpackDataCons(unpackDataCons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackListCons(LocalDefn.PatternMatch.UnpackListCons unpackListCons, Object arg) {
            
            // Process the pattern-bound variables first
            processLocalPatternMatchDeclarationPattern(unpackListCons.getHeadPattern());
            processLocalPatternMatchDeclarationPattern(unpackListCons.getTailPattern());
            
            // No further work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_LocalDefn_PatternMatch_UnpackListCons(unpackListCons, arg);
            }
            
            checkReference(CAL_Prelude.DataConstructors.Cons, unpackListCons.getOperatorSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            
            return super.visit_LocalDefn_PatternMatch_UnpackListCons(unpackListCons, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Case_Alt_UnpackDataCons(UnpackDataCons cons, Object arg) {
            
            // No work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_Expr_Case_Alt_UnpackDataCons(cons, arg);
            }
            
            for(int i = 0; i < cons.getNDataConsNames(); i++) {
                Name.Qualifiable candidateName = cons.getNthDataConsName(i);    
                
                // If this is a reference to one of the targetNames, add it to the list
                checkReference(candidateName, candidateName.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            }
            
            return super.visit_Expr_Case_Alt_UnpackDataCons(cons, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Case_Alt_UnpackListNil(UnpackListNil nil, Object arg) {

            // No work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_Expr_Case_Alt_UnpackListNil(nil, arg);
            }
            
            checkReference(CAL_Prelude.DataConstructors.Nil, nil.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            
            return super.visit_Expr_Case_Alt_UnpackListNil(nil, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Case_Alt_UnpackListCons(UnpackListCons cons, Object arg) {
            // No work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_Expr_Case_Alt_UnpackListCons(cons, arg);
            }
            
            checkReference(CAL_Prelude.DataConstructors.Cons, cons.getOperatorSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            
            return super.visit_Expr_Case_Alt_UnpackListCons(cons, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Case_Alt_UnpackUnit(UnpackUnit unit, Object arg) {
            // No work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_Expr_Case_Alt_UnpackUnit(unit, arg);
            }
            
            checkReference(CAL_Prelude.DataConstructors.Unit, unit.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            
            return super.visit_Expr_Case_Alt_UnpackUnit(unit, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_SelectDataConsField(SelectDataConsField field, Object arg) {

            // No work to do here unless we're looking for references
            if(searchType != SearchType.REFERENCES && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_Expr_SelectDataConsField(field, arg);
            }
            
            Name.Qualifiable candidateName = field.getDataConsName();
            
            // If this is a reference to targetName, add it to the list
            checkReference(candidateName, field.getSourceRangeOfName(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            
            return super.visit_Expr_SelectDataConsField(field, arg);
        }
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Var(Var var, Object arg) {
            
            // No work to do here unless we're looking for references 
            if(searchType != SearchType.REFERENCES && searchType != SearchType.REFERENCES_CONSTRUCTIONS && searchType != SearchType.ALL && searchType != SearchType.POSITION) {
                return super.visit_Expr_Var(var, arg);
            }
            
            Name.Qualifiable candidateName = var.getVarName();

            // figure out the category of this symbol
            SourceIdentifier.Category category = SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD;
            if (candidateName.getModuleName() == null || SourceModel.Name.Module.toModuleName(candidateName.getModuleName()).equals(getModuleName())){
                if (getBoundLocalFunctionIdentifier(candidateName.getUnqualifiedName()) != null){
                    category = SourceIdentifier.Category.LOCAL_VARIABLE;
                }
            }
            // If this is a reference to targetName, add it to the list
            checkReference(candidateName, var.getSourceRange(), category);
            
            return super.visit_Expr_Var(var, arg);
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_BinaryOp_BackquotedOperator_DataCons(org.openquark.cal.compiler.SourceModel.Expr.BinaryOp.BackquotedOperator.DataCons backquotedOperator, Object arg) {
            // Force an in-order traversal rather than relying on the super method to
            // make the recursive calls.  Doing an in-order traversal will ensure that the
            // the results are returned in source-order without needing to be explicitly sorted.
            backquotedOperator.getLeftExpr().accept(this, arg);
            backquotedOperator.getOperatorDataConsExpr().accept(this, arg);
            backquotedOperator.getRightExpr().accept(this, arg);
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_BinaryOp_BackquotedOperator_Var(org.openquark.cal.compiler.SourceModel.Expr.BinaryOp.BackquotedOperator.Var backquotedOperator, Object arg) {
            // Force an in-order traversal rather than relying on the super method to
            // make the recursive calls.  Doing an in-order traversal will ensure that the
            // the results are returned in source-order without needing to be explicitly sorted.
            backquotedOperator.getLeftExpr().accept(this, arg);
            backquotedOperator.getOperatorVarExpr().accept(this, arg);
            backquotedOperator.getRightExpr().accept(this, arg);
            return null;
        }
        
        /** {@inheritDoc} */
        @Override
        protected Void visit_Expr_BinaryOp_Helper(BinaryOp binop, Object arg) {
            
            // Force an in-order traversal rather than relying on the super method to
            // make the recursive calls.  Doing an in-order traversal will ensure that the
            // the results are returned in source-order without needing to be explicitly sorted.
            binop.getLeftExpr().accept(this, arg);
            
            if(searchType == SearchType.REFERENCES || searchType == SearchType.REFERENCES_CONSTRUCTIONS || searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(OperatorInfo.getTextualName(binop.getOpText()), binop.getOperatorSourceRange(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
            }
            
            binop.getRightExpr().accept(this, arg);
            return null;
        }
        
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_List(org.openquark.cal.compiler.SourceModel.Expr.List list, Object arg) {
            
            if(searchType == SearchType.REFERENCES || searchType == SearchType.REFERENCES_CONSTRUCTIONS || searchType == SearchType.ALL || searchType == SearchType.POSITION) {

                // All empty list literals reference the Prelude.Nil datacons
                if(list.getNElements() == 0) {
                    checkReference(CAL_Prelude.DataConstructors.Nil, list.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
                }
            }
            return super.visit_Expr_List(list, arg);
        }

        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_UnaryOp_Negate(Negate negate, Object arg) {
            if(searchType == SearchType.REFERENCES || searchType == SearchType.REFERENCES_CONSTRUCTIONS || searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(CAL_Prelude.Functions.negate, negate.getSourceRange(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
            }
            return super.visit_Expr_UnaryOp_Negate(negate, arg);
        }
         
        /** {@inheritDoc} */
        @Override
        public Void visit_Expr_Unit(Unit unit, Object arg) {
            if(searchType == SearchType.REFERENCES || searchType == SearchType.REFERENCES_CONSTRUCTIONS || searchType == SearchType.ALL || searchType == SearchType.POSITION) {
                checkReference(CAL_Prelude.DataConstructors.Unit, unit.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR);
            }
            return super.visit_Expr_Unit(unit, arg);
        }
        
        /**
         * Check whether candidateName refers to any of the targetNames.
         * If it does, then add the specified source position to sourcePositions.
         * @param candidateName Name to check
         * @param sourceRange SourceRange to record if candidateName is a match
         */
        private void checkReference(QualifiedName candidateName, SourceRange sourceRange, SourceIdentifier.Category category) {
            
            // Skip bound names
            if(candidateName.getModuleName().equals(super.currentModule) &&
               isBound(candidateName.getUnqualifiedName())) {
                return;
            }
            
            if (searchType == SearchType.POSITION){
                if (sourceRange.containsPositionInclusive(searchPosition)){
                    addSearchResult(sourceRange, candidateName, category);
                    return;
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        if(targetName.equals(candidateName)) {
                            addSearchResult(sourceRange, candidateName, category);
                            return;
                        }
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_Parameter(Parameter parameter, Object arg) {
            if (searchType == SearchType.POSITION){
                if (parameter.getSourceRangeOfNameNotIncludingPotentialPling() != null){
                    if (parameter.getSourceRangeOfNameNotIncludingPotentialPling().containsPositionInclusive(searchPosition)){
                        final LocalFunctionIdentifier lfi = getBoundLocalFunctionIdentifier(parameter.getName());
                        if (lfi != null){
                            final QualifiedName resolvedCandidateName = QualifiedName.makeFromCompoundName(lfi.toString());
                            addSearchResult(parameter.getSourceRangeOfNameNotIncludingPotentialPling(), resolvedCandidateName, SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION);
                        }
                    }
                }
            }

            return super.visit_Parameter(parameter, arg);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_LocalDefn_Function_TypeDeclaration(
                LocalDefn.Function.TypeDeclaration declaration, Object arg) {
            if (searchType == SearchType.POSITION){
                if (declaration.getNameSourceRange() != null){
                    if (declaration.getNameSourceRange().containsPositionInclusive(searchPosition)){
                        final LocalFunctionIdentifier lfi = getBoundLocalFunctionIdentifier(declaration.getName());
                        if (lfi != null){
                            final QualifiedName resolvedCandidateName = QualifiedName.makeFromCompoundName(lfi.toString());
                            addSearchResult(declaration.getNameSourceRange(), resolvedCandidateName, SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION);
                        }
                    }
                }
            }
            return super.visit_LocalDefn_Function_TypeDeclaration(declaration, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackRecord(final LocalDefn.PatternMatch.UnpackRecord unpackRecord, final Object arg) {
            // Process the pattern-bound variables first
            final int nFieldPatterns = unpackRecord.getNFieldPatterns();
            for (int i = 0; i < nFieldPatterns; i++) {
                processLocalPatternMatchDeclarationPattern(unpackRecord.getNthFieldPattern(i));
            }
            
            return super.visit_LocalDefn_PatternMatch_UnpackRecord(unpackRecord, arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackTuple(final LocalDefn.PatternMatch.UnpackTuple unpackTuple, final Object arg) {
            // Process the pattern-bound variables first
            final int nPatterns = unpackTuple.getNPatterns();
            for (int i = 0; i < nPatterns; i++) {
                processLocalPatternMatchDeclarationPattern(unpackTuple.getNthPattern(i));
            }
            
            return super.visit_LocalDefn_PatternMatch_UnpackTuple(unpackTuple, arg);
        }
        
        /**
         * Processes a Pattern, a FieldPattern or an ArgBindings element contained in a local
         * pattern match declaration for the pattern-bound variables defined therein.
         * @param element the element to process.
         */
        private void processLocalPatternMatchDeclarationPattern(final SourceModel.SourceElement element) {
            /**
             * A helper class for processing locally bound names for the purpose of identifying search hits and location of
             * local definitions.
             * @author Joseph Wong
             */
            class LocalNamesProcessor extends SourceModelTraverser<Void, Void> {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public Void visit_Pattern_Var(final Pattern.Var var, final Void arg) {
                    processLocallyBoundName(var.getName(), var.getSourceRange());
                    return null;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Void visit_FieldPattern(final FieldPattern fieldPattern, final Void arg) {
                    // Handle punning
                    if (fieldPattern.getPattern() == null) {
                        // punning.
                        
                        // Textual field names become Vars of the same name.
                        // Ordinal field names become wildcards ("_").
                        final FieldName fieldName = fieldPattern.getFieldName().getName();
                        if (fieldName instanceof FieldName.Textual) {
                            processLocallyBoundName(fieldName.getCalSourceForm(), fieldPattern.getFieldName().getSourceRange());
                        }
                    }
                    
                    // call the superclass impl to reach the pattern and visit it (if it is non-null)
                    return super.visit_FieldPattern(fieldPattern, arg);
                }
            }
            
            // run the LocalNamesProcessor on the element
            element.accept(new LocalNamesProcessor(), null);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Void visit_LocalDefn_Function_Definition(final LocalDefn.Function.Definition function, final Object arg) {
            // Process the local function binding first
            processLocallyBoundName(function.getName(), function.getNameSourceRange());
            
            return super.visit_LocalDefn_Function_Definition(function, arg);
        }
        
        /**
         * Processes a locally defined name (either a local function definition, or a local pattern-bound variable).
         * @param name the locally defined name.
         * @param nameSourceRange the source range of the name. Can be null.
         */
        private void processLocallyBoundName(final String name, final SourceRange nameSourceRange) {
            //todo-jowong rewrite the position search as a separate walker to handle searching through all identifiers
            if (searchType == SearchType.POSITION){
                if (nameSourceRange != null){
                    if (nameSourceRange.containsPositionInclusive(searchPosition)){
                        final LocalFunctionIdentifier lfi = getBoundLocalFunctionIdentifier(name);
                        if (lfi != null){
                            final QualifiedName resolvedCandidateName = QualifiedName.makeFromCompoundName(lfi.toString());
                            addSearchResult(nameSourceRange, resolvedCandidateName, SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION);
                        }
                    }
                }
            }
            else{
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        final LocalFunctionIdentifier lfi = getBoundLocalFunctionIdentifier(name);
                        if (lfi != null){
                            final QualifiedName resolvedCandidateName = QualifiedName.makeFromCompoundName(lfi.toString());
                            if((searchType == SearchType.DEFINITION || searchType == SearchType.ALL || searchType == SearchType.POSITION) &&
                                    resolvedCandidateName.getUnqualifiedName().equals(targetName.getUnqualifiedName()) &&
                                    resolvedCandidateName.getModuleName().equals(targetName.getModuleName())) {
                                addSearchResult(nameSourceRange, targetName, SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION);
                            }
                            else if((searchType == SearchType.SOURCE_TEXT) &&
                                    resolvedCandidateName.getUnqualifiedName().equals(targetName.getUnqualifiedName()) &&
                                    resolvedCandidateName.getModuleName().equals(targetName.getModuleName())) {
                                SourceModel.SourceElement se = getBoundElement(name);
                                if (se != null){                                
                                    addSearchResult(se.getSourceRange(), targetName, SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION);
                                }
                            }
                        }
                        else if((searchType == SearchType.DEFINITION || searchType == SearchType.ALL || searchType == SearchType.POSITION) &&
                                name.equals(targetName.getUnqualifiedName()) &&
                                super.currentModule.equals(targetName.getModuleName())) {
                            addSearchResult(nameSourceRange, targetName, SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION);
                        }
                    }
                }
            }
        }
        
        /**
         * Check whether candidateName refers to any of the targetNames.  
         * If it does, then add the specified source position to sourcePositions.
         * @param candidateName Name to check
         * 
         * @param sourceRange SourceRange to add to searchResults if candidateName is a match
         */
        private void checkReference(Name.Qualifiable candidateName, SourceRange sourceRange, SourceIdentifier.Category category) {
            
            // Null names can sometimes get passed in, but they are obviously not a match
            if(candidateName == null) {
                return;
            }
            
            if (searchType == SearchType.POSITION){
                if (sourceRange.containsPositionInclusive(searchPosition)){
                    
                    if (candidateName.getModuleName() == null) {
                        
                        // We have an unqualified name.  If it is not currently bound, then
                        // look up the FunctionalAgent and compare the QualifiedNames.
                        if(!isBound(candidateName.getUnqualifiedName())) {
                            ScopedEntity candidateEntity = getScopedEntityFromUnboundCandidateName(candidateName);
                            
                            if(candidateEntity != null) {
                                addSearchResult(sourceRange, candidateEntity.getName(), category);
                            }
                        }
                        else{
                            LocalFunctionIdentifier lfi = getBoundLocalFunctionIdentifier(candidateName.getUnqualifiedName());
                            if (lfi != null){
                                final QualifiedName resolvedCandidateName = QualifiedName.makeFromCompoundName(lfi.toString());
                                addSearchResult(sourceRange, resolvedCandidateName, category);
                            }
                        }
                        
                    } else {
                        final QualifiedName resolvedCandidateName = QualifiedName.make(super.resolveModuleName(candidateName.getModuleName()), candidateName.getUnqualifiedName());
                        addSearchResult(sourceRange, resolvedCandidateName, category);
                    }
                }

            } else {
                for (final org.openquark.cal.compiler.Name next : targetNames) {
                    if (next instanceof QualifiedName){
                        QualifiedName targetName = (QualifiedName) next;

                        // If the gem being checked has a different unqualified name, we can do a trivial reject
                        if(!candidateName.getUnqualifiedName().equals(targetName.getUnqualifiedName())) {
                            continue;
                        }

                        // If the gem being applied is fully-qualified, then we can do a simple check without looking up the FunctionalAgent
                        if(candidateName.getModuleName() != null) {
                            if(super.resolveModuleName(candidateName.getModuleName()).equals(targetName.getModuleName())) {
                                addSearchResult(sourceRange, targetName, category);
                            }

                            continue;
                        }

                        // Otherwise, we have an unqualified name.  If it is not currently bound, then
                        // look up the FunctionalAgent and compare the QualifiedNames.
                        if (!isBound(candidateName.getUnqualifiedName())) {
                            ScopedEntity candidateEntity = getScopedEntityFromUnboundCandidateName(candidateName);

                            if(candidateEntity == null) {
                                continue;
                            }

                            if(candidateEntity.getName().equals(targetName)) {
                                addSearchResult(sourceRange, targetName, category);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Fetches the named entity (function/data cons/method/type/class).
         * @param candidateName the name of the entity.
         * @return the entity named, or null if no such entity exists.
         */
        private ScopedEntity getScopedEntityFromUnboundCandidateName(Name.Qualifiable candidateName) {
            String unqualifiedCandidate = candidateName.getUnqualifiedName();
            ScopedEntity candidateEntity = super.getFunctionalAgent(candidateName);
            
            // Wasn't a functional agent.  Maybe a type.
            if(candidateEntity == null && candidateName instanceof Name.TypeCons) {
                candidateEntity = super.moduleTypeInfo.getTypeConstructor(unqualifiedCandidate);
                
                if(candidateEntity == null) {
                    ModuleName dependeeModuleName = super.moduleTypeInfo.getModuleOfUsingTypeConstructor(unqualifiedCandidate);
                    ModuleTypeInfo dependeeModuleTypeInfo = super.moduleTypeInfo.getDependeeModuleTypeInfo(dependeeModuleName);
                    if(dependeeModuleTypeInfo != null) {
                        candidateEntity = dependeeModuleTypeInfo.getTypeConstructor(unqualifiedCandidate);
                    }
                }
            }
            
            // Maybe a type class.
            if(candidateEntity == null && candidateName instanceof Name.TypeClass) {
                candidateEntity = super.moduleTypeInfo.getTypeClass(unqualifiedCandidate);
                
                if(candidateEntity == null) {
                    ModuleName dependeeModuleName = super.moduleTypeInfo.getModuleOfUsingTypeClass(unqualifiedCandidate);
                    ModuleTypeInfo dependeeModuleTypeInfo = super.moduleTypeInfo.getDependeeModuleTypeInfo(dependeeModuleName);
                    if(dependeeModuleTypeInfo != null) {
                        candidateEntity = dependeeModuleTypeInfo.getTypeClass(unqualifiedCandidate);
                    }
                }
            }
            
            return candidateEntity;
        }

        /**
         * Adds the specified sourceRange to searchResults if it contains a source name,
         * or (if it does not) a SourceRange augmented by the name of the current module. 
         * @param sourceRange SourceRange to add (possibly in augmented form)
         * @param name QualifiedName that was found
         */
        private void addSearchResult(SourceRange sourceRange, org.openquark.cal.compiler.Name name, SourceIdentifier.Category category, boolean refersToJavaSource) {
            if(sourceRange == null || sourceRange.getSourceName() != null) {
                // TODO this is really wrong - SearchResult.Precise REQUIRES a non-null sourceRange
                searchResults.add(new SearchResult.Precise(sourceRange, name, category, refersToJavaSource));
            } else {
                SourcePosition startPosition = new SourcePosition(sourceRange.getStartLine(), sourceRange.getStartColumn(), super.currentModule);
                SourcePosition endPosition = new SourcePosition(sourceRange.getEndLine(), sourceRange.getEndColumn(), super.currentModule);
                searchResults.add(new SearchResult.Precise(new SourceRange(startPosition, endPosition), name, category, refersToJavaSource));
            }
        }
        
        /**
         * Adds the specified sourceRange to searchResults if it contains a source name,
         * or (if it does not) a SourceRange augmented by the name of the current module. 
         * @param sourceRange SourceRange to add (possibly in augmented form)
         * @param name QualifiedName that was found
         */
        private void addSearchResult(SourceRange sourceRange, org.openquark.cal.compiler.Name name, SourceIdentifier.Category category) {
            addSearchResult(sourceRange, name, category, false);
        }
        
        /**
         * @return List of all the search hits
         */
        List<SearchResult.Precise> getSearchResults() {
            return Collections.unmodifiableList(searchResults);
        }
    }
    
    /**
     * Performs the compile-time collection of source metric data and updates the appropriate
     * compiler structures with the raw data from the results.  Collects data for:
     * 
     * - Reference frequency metric.  Collects information on which top-level gems are referenced 
     *   by each top-level function in the module.  In this example function, foo has one reference 
     *   to baz and two to quux, but none to bar:
     *      foo x =
     *          let
     *              bar y = quux y;
     *          in
     *              quux (bar (baz x));
     *              
     * - Module references: Collects a list of all QualifiedNames from other modules that this
     *   module refers to.
     * 
     * Note that this method assumes that the specified module and all it imported modules have all been compiled 
     * already and that ModuleTypeInfo objects are therefore available for all of them.
     * 
     * This method call MODIFIES the raw data sets of the specified module's FunctionEntities as a side effect, 
     * so it should only be called as a part of the compilation process.
     *  
     * @param moduleDefn SourceModel of the module to process
     * @param moduleTypeInfo The typeinfo object for the module specified by moduleSourceDefn
     */
    static void updateRawMetricData(SourceModel.ModuleDefn moduleDefn, ModuleTypeInfo moduleTypeInfo) {

        // Compute the set of references from this module
        ReferenceFrequencyFinder finder = new ReferenceFrequencyFinder(moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false);
        finder.visit_ModuleDefn(moduleDefn, null);
        Map<Pair<QualifiedName, QualifiedName>, Integer> dependeeMap = finder.getDependeeMap();
        
        // Add each reference to the appropriate Function
        for (final Map.Entry<Pair<QualifiedName, QualifiedName>, Integer> entry : dependeeMap.entrySet()) {
            Pair<QualifiedName, QualifiedName> referencePair = entry.getKey();
            Integer frequency = entry.getValue();
            
            QualifiedName dependeeName = referencePair.fst();
            QualifiedName dependentName = referencePair.snd();
            
            // Every dependent was found in this scan, so it must be a member of this module
            Function dependentEntity = moduleTypeInfo.getFunction(dependentName.getUnqualifiedName());
            dependentEntity.addDependee(dependeeName, frequency.intValue());
        }
        
        moduleTypeInfo.setImportedNameOccurrences(finder.getImportedNameOccurrences());
    }
    
    /**
     * Finds all the pairs of (referred_to_gem, referred_by_function) in the provided module and returns
     * them in a Set of Pairs of QualifiedNames.  This is the raw data used to calculate the reference
     * frequency heuristic.
     * 
     * This function has no external side effects.
     *  
     * @param moduleDefn SourceModel of the module to process
     * @param moduleTypeInfo ModuleTypeInfo object corresponding to the module specified by moduleSourceDefn. 
     * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
     * @param traceSkippedFunctions When true, each skipped function will have its name dumped to the console.
     *                               When false, skipped functions will be skipped silently      
     * @return List of (dependee, dependent) pairs (one for each reference).
     */
    static Map<Pair<QualifiedName, QualifiedName>, Integer> computeReferenceFrequencies(SourceModel.ModuleDefn moduleDefn, ModuleTypeInfo moduleTypeInfo, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions) {
        ReferenceFrequencyFinder finder = new ReferenceFrequencyFinder(moduleTypeInfo, functionFilter, traceSkippedFunctions);
        finder.visit_ModuleDefn(moduleDefn, null);
        return finder.getDependeeMap();
    }
    
    /**
     * Scans the provided module for instances where the output of one gem is used
     * directly as the input of another.  Each (consumer, producer) pair is associated with a count
     * of the number of times that the output of producer is used as input to consumer.  This metric
     * is called the "compositional frequency" of (consumer, producer).
     * 
     * For example, in the following module:
     * 
     *      module Foo;
     *      import Prelude;
     * 
     *      bar = 50;
     *      baz arg = 20 + arg;
     * 
     *      quux = Prelude.add bar (baz 54);
     * 
     *      quuux val = Prelude.add (baz 10) (baz val);
     * 
     * The compositional frequency of (Prelude.add, Foo.bar) is 1 (since the output of bar is
     * passed directly to add once in quux).  
     * The compositional frequency of (Prelude.add, Foo.baz) is 3 (since the output of baz is
     * passed directly to add once in quux and twice in quuux).
     *  
     * @param moduleDefn A SourceModel of a module to scan
     * @param moduleTypeInfo The ModuleTypeInfo object corresponding to the moduleDefn's module
     * @param functionFilter Filter for deciding which functions will be included in the frequency counts. Cannot be null.
     * @param traceSkippedFunctions When true, each skipped function will have its name dumped to the console.
     *                               When false, skipped functions will be skipped silently 
     * @return A map from (consumer, producer) to
     * the number of times the output of producer is used as the input to consumer. 
     */
    static Map<Pair<QualifiedName, QualifiedName>, Integer> computeCompositionalFrequencies(SourceModel.ModuleDefn moduleDefn, ModuleTypeInfo moduleTypeInfo, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions) {
        CompositionalFrequencyFinder finder = new CompositionalFrequencyFinder(moduleTypeInfo, functionFilter, traceSkippedFunctions);
        finder.visit_ModuleDefn(moduleDefn, null);
        return finder.getCompositionalFrequencyMap();
    }
    
    /**
     * Scans the provided module for questionable expressions (currently only finds redundant lambdas).
     * Dumps a warning to the console for each flagged expression.
     * 
     * @param moduleDefn A SourceModel of a module to scan
     * @param moduleTypeInfo The ModuleTypeInfo object corresponding to the moduleDefn's module
     * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
     * @param traceSkippedFunctions When true, each skipped function will have its name dumped to the console.
     *                               When false, skipped functions will be skipped silently 
     * @param includeUnplingedPrimitives When true, check for unplinged arguments of primitive type
     * @param includeRedundantLambdas When true, check for possibly-redundant lambdas
     * @param includeUnusedPrivates When true, check for unused private top-level functions.
     * @param includeMismatchedAliasPlings When true, check for alias functions whose plinging does not 
     *                                        match that of the underlying data constructor / foreign function exactly.
     * @param includeUnreferencedLetVariables When true, check for let variables that are not referenced                                        
     * @return List of warnings found for this module
     */
    static List<LintWarning> computeLintWarnings(SourceModel.ModuleDefn moduleDefn, ModuleTypeInfo moduleTypeInfo, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions,
                                    boolean includeUnplingedPrimitives, boolean includeRedundantLambdas, boolean includeUnusedPrivates, boolean includeMismatchedAliasPlings, boolean includeUnreferencedLetVariables) {
        
        LintWalker walker = new LintWalker(moduleTypeInfo, functionFilter, traceSkippedFunctions,
                                           includeUnplingedPrimitives, includeRedundantLambdas, includeUnusedPrivates, includeMismatchedAliasPlings, includeUnreferencedLetVariables);
        walker.visit_ModuleDefn(moduleDefn, null);
        
        return walker.getWarningList();
    }

    /**
     * Searches a module for references, definitions, or instances, and returns a list of SearchResults
     * representing the hits.
     * 
     * @param moduleDefn SourceModel.ModuleDefn of the module to search
     * @param moduleTypeInfo ModuleTypeInfo corresponding to the moduleDefn's module
     * @param searchType SearchType type of search to perform
     * @param targetGemNames List of QualifiedNames of the entities to search for
     * @return List of SourcePositions of the hits
     */
    static List<SearchResult.Precise> performSearch(SourceModel.ModuleDefn moduleDefn, ModuleTypeInfo moduleTypeInfo, SearchType searchType, List<? extends org.openquark.cal.compiler.Name> targetGemNames) {
        
        SearchWalker searchWalker = new SearchWalker(moduleTypeInfo, targetGemNames, searchType, null);
        
        searchWalker.visit_ModuleDefn(moduleDefn, null);
        return searchWalker.getSearchResults();
    }
    
    /**
     * Searches a module for a SourcePosition, and returns a list of SearchResults
     * representing the hits.
     * 
     * @param moduleDefn SourceModel.ModuleDefn of the module to search
     * @param moduleTypeInfo ModuleTypeInfo corresponding to the moduleDefn's module
     * @return List of SourcePositions of the hits
     */
    static List<SearchResult.Precise> findSymbolAt(SourceModel.ModuleDefn moduleDefn, ModuleTypeInfo moduleTypeInfo, SourcePosition position) {        
        SearchWalker searchWalker = new SearchWalker(moduleTypeInfo, null, SearchType.POSITION, position);        
        searchWalker.visit_ModuleDefn(moduleDefn, null);
        return searchWalker.getSearchResults();
    }

    /**
     * @param defn the top level element to get the name source range of.
     * @return the source range of the name of the given top level element. If not 
     * available the source range of the entire element is returned. Otherwise null
     * is returned.
     */
    private static SourceRange getScopedEntityNameSourceRange(TopLevelSourceElement defn){
        SourceRange result = null;
        if (defn instanceof SourceModel.FunctionDefn){
            SourceModel.FunctionDefn functionDefn = (FunctionDefn) defn;
            result = functionDefn.getNameSourceRange();
        }
        else if (defn instanceof SourceModel.TypeConstructorDefn){
            SourceModel.TypeConstructorDefn typeConstructorDefn = (SourceModel.TypeConstructorDefn) defn;
            result = typeConstructorDefn.getSourceRangeOfName();
        }
        else if (defn instanceof SourceModel.InstanceDefn){
            SourceModel.InstanceDefn instanceDefn = (SourceModel.InstanceDefn) defn;
            result = instanceDefn.getSourceRangeOfName();
        }
        else if (defn instanceof SourceModel.TypeClassDefn){
            SourceModel.TypeClassDefn typeClassDefn = (SourceModel.TypeClassDefn) defn;
            result = typeClassDefn.getSourceRangeOfName();
        }
        
        if (result == null){
            return defn.getSourceRangeOfDefn();
        }
        else{
            return result;
        }
    }
    
    /**
     * Get the next top level element definition that is defined after the given position.
     * @param moduleDefn the source model to search
     * @param position the position to look after
     * @return the next top level element defined after the given position. This may be null.
     */
    static SourceRange findNextTopLevelElement(SourceModel.ModuleDefn moduleDefn, SourcePosition position){
        final int nTopLevelDefns = moduleDefn.getNTopLevelDefns();
        SourceRange next = null;
        for (int i = 0; i < nTopLevelDefns; i++) {
            TopLevelSourceElement nthTopLevelDefn = moduleDefn.getNthTopLevelDefn(i);
            // skip the type declarations
            if (nthTopLevelDefn instanceof SourceModel.FunctionTypeDeclaration){
                continue;
            }
            SourceRange testSourcePosition = getScopedEntityNameSourceRange(nthTopLevelDefn);
            if (testSourcePosition.getStartSourcePosition().isAfter(position)){
                if (next == null){
                    next = testSourcePosition;
                }
                else if (testSourcePosition.getStartSourcePosition().isBefore(next.getStartSourcePosition())){
                    next = testSourcePosition;
                }
            }
        }
        
        if (next != null){
            return next;
        }
        else{
            return null;
        }
    }

    /**
     * Get the previous top level element definition that is defined before the given position.
     * @param moduleDefn the source model to search
     * @param position the position to look after
     * @return the previous top level element defined before the given position. This may be null.
     */
    static SourceRange findPreviousTopLevelElement(SourceModel.ModuleDefn moduleDefn, SourcePosition position){
        final int nTopLevelDefns = moduleDefn.getNTopLevelDefns();
        SourceRange next = null;
        for (int i = 0; i < nTopLevelDefns; i++) {
            TopLevelSourceElement nthTopLevelDefn = moduleDefn.getNthTopLevelDefn(i);
            // skip the type declarations
            if (nthTopLevelDefn instanceof SourceModel.FunctionTypeDeclaration){
                continue;
            }
            SourceRange testSourcePosition = getScopedEntityNameSourceRange(nthTopLevelDefn);
            if (testSourcePosition.getStartSourcePosition().isBefore(position)){
                if (next == null){
                    next = testSourcePosition;
                }
                else if (testSourcePosition.getStartSourcePosition().isAfter(next.getStartSourcePosition())){
                    next = testSourcePosition;
                }
            }
        }
        
        if (next != null){
            return next;
        }
        else{
            return null;
        }
    }

    /**
     * Finds the inner most definition containing the given position. This will return 
     * null if there is no inner definition containing the given position.
     * @return the innermost definition containing the given position or null if there is no such definition. 
     */
    private static Pair<SourceElement, SourceRange> findInnermostDef(LocalDefn.Function.Definition def, SourcePosition position){
        SourceRange next = null;
        SourceElement element = null;
        if (def.getDefiningExpr() instanceof SourceModel.Expr.Let){
            SourceModel.Expr.Let let = (SourceModel.Expr.Let) def.getDefiningExpr();
            for(LocalDefn localDefn : let.getLocalDefinitions()){
                SourceRange testSourcePosition = localDefn.getSourceRange();
                if (testSourcePosition.containsPosition(position)){
                    if (next == null || next.contains(testSourcePosition)){
                        next = testSourcePosition;
                        element = localDefn;

                        if (localDefn instanceof LocalDefn.Function){
                            SourceModel.LocalDefn.Function localFunction = (SourceModel.LocalDefn.Function) localDefn;

                            // if there is a type declaration then use that to put the
                            // cal doc in front of
                            for(LocalDefn localTypeDefn : let.getLocalDefinitions()){                                    
                                if (localTypeDefn instanceof SourceModel.LocalDefn.Function.TypeDeclaration){
                                    SourceModel.LocalDefn.Function.TypeDeclaration type = (SourceModel.LocalDefn.Function.TypeDeclaration) localTypeDefn;
                                    if (type.getName().equals(localFunction.getName())){
                                        element = type;      
                                        break;
                                    }
                                }
                            }
                        }
                        
                        if (localDefn instanceof LocalDefn.Function.Definition){
                            LocalDefn.Function.Definition def2 = (Definition) localDefn;
                            Pair<SourceElement, SourceRange> innerMatch = findInnermostDef(def2, position);
                            if (innerMatch != null){
                                return innerMatch;
                            }
                        }
                        
                        return new Pair<SourceElement, SourceRange>(element, next);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the source element definition that is at the given position.
     * @param moduleDefn the source model to search
     * @param position the position to look after
     * @return A pair where the first element is the source element that 
     * contains the given position and the second element is the 
     * source range of the element. This may be null.
     */
    static Pair<SourceElement, SourceRange> findContainingSourceElement(SourceModel.ModuleDefn moduleDefn, SourcePosition position){
        final int nTopLevelDefns = moduleDefn.getNTopLevelDefns();
        SourceRange next = null;
        SourceElement element = null;
        for (int i = 0; i < nTopLevelDefns; i++) {
            TopLevelSourceElement nthTopLevelDefn = moduleDefn.getNthTopLevelDefn(i);
            // Check for matching data constructor
            if (nthTopLevelDefn instanceof SourceModel.TypeConstructorDefn.AlgebraicType){
                SourceModel.TypeConstructorDefn.AlgebraicType algebraicType = (AlgebraicType) nthTopLevelDefn;
                for(DataConsDefn dataConsDefn : algebraicType.getDataConstructors()){
                    SourceRange testSourcePosition = dataConsDefn.getSourceRangeOfDefn();
                    if (testSourcePosition.containsPosition(position)){
                        if (next == null || next.contains(testSourcePosition)){
                            next = testSourcePosition;
                            element = dataConsDefn;
                            return new Pair<SourceElement, SourceRange>(element, element.getSourceRange());
                        }
                    }
                }
            }
            // check for matching class method
            else if (nthTopLevelDefn instanceof SourceModel.TypeClassDefn){
                SourceModel.TypeClassDefn typeClassDefn = (SourceModel.TypeClassDefn) nthTopLevelDefn;
                for(ClassMethodDefn classMethodDefn : typeClassDefn.getClassMethodDefns()){
                    SourceRange testSourcePosition = classMethodDefn.getSourceRangeOfClassDefn();
                    if (testSourcePosition.containsPosition(position)){
                        if (next == null || next.contains(testSourcePosition)){
                            next = testSourcePosition;
                            element = classMethodDefn;
                            return new Pair<SourceElement, SourceRange>(element, element.getSourceRange());
                        }
                    }
                }
            }
            // check for let expressions 
            else if (nthTopLevelDefn instanceof SourceModel.FunctionDefn.Algebraic){
                SourceModel.FunctionDefn.Algebraic algebraic = (SourceModel.FunctionDefn.Algebraic) nthTopLevelDefn;
                if (algebraic.getDefiningExpr() instanceof SourceModel.Expr.Let){
                    SourceModel.Expr.Let let = (SourceModel.Expr.Let) algebraic.getDefiningExpr();
                    for(LocalDefn localDefn : let.getLocalDefinitions()){
                        SourceRange testSourcePosition = localDefn.getSourceRange();
                        if (testSourcePosition.containsPosition(position)){
                            if (next == null || next.contains(testSourcePosition)){
                                next = testSourcePosition;
                                element = localDefn;

                                if (localDefn instanceof LocalDefn.Function){
                                    SourceModel.LocalDefn.Function localFunction = (SourceModel.LocalDefn.Function) localDefn;
                                    
                                    // if there is a type declaration then use that to put the
                                    // cal doc in front of
                                    for(LocalDefn localTypeDefn : let.getLocalDefinitions()){                                    
                                        if (localTypeDefn instanceof SourceModel.LocalDefn.Function.TypeDeclaration){
                                            SourceModel.LocalDefn.Function.TypeDeclaration type = (SourceModel.LocalDefn.Function.TypeDeclaration) localTypeDefn;
                                            if (type.getName().equals(localFunction.getName())){
                                                element = type;                                                
                                            }
                                        }
                                    }
                                }
                                if (localDefn instanceof LocalDefn.Function.Definition){
                                    LocalDefn.Function.Definition def = (Definition) localDefn;
                                    Pair<SourceElement, SourceRange> innerMatch = findInnermostDef(def, position);
                                    if (innerMatch != null){
                                        element = innerMatch.fst();
                                        next = innerMatch.snd();
                                    }
                                }
                            }
                        }
                    }
                    if (element != null){
                        break;
                    }
                }
                
            }

            // check the current element
            SourceRange testSourcePosition = nthTopLevelDefn.getSourceRangeOfDefn();
            if (testSourcePosition.containsPosition(position)){
                if (next == null || next.contains(testSourcePosition)){
                    next = testSourcePosition;
                    element = nthTopLevelDefn;
                }
            }
        }

        if (element != null){
            // Find type declaration if any
            if (element instanceof SourceModel.FunctionDefn){
                SourceModel.FunctionDefn functionDefn = (FunctionDefn) element;
                String name = functionDefn.getName();
                for (int i = 0; i < nTopLevelDefns; i++) {
                    TopLevelSourceElement nthTopLevelDefn = moduleDefn.getNthTopLevelDefn(i);
                    if (nthTopLevelDefn instanceof FunctionTypeDeclaration){
                        FunctionTypeDeclaration type = (FunctionTypeDeclaration) nthTopLevelDefn;
                        if (type.getFunctionName().equals(name)){
                            element = type;
                            break;
                        }
                    }
                }
            }
            
            return new Pair<SourceElement, SourceRange>(element, element.getSourceRange());
        }
        else{
            if (moduleDefn.getSourceRange().containsPosition(position)){
                return new Pair<SourceElement, SourceRange>(moduleDefn, moduleDefn.getSourceRange());
            }
            return null;
        }
    }
}
