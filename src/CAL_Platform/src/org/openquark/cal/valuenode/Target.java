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
 * Target.java
 * Creation date: (11/05/01 11:39:38 AM)
 * By: Edward Lam
 */
package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.TypeExpr;


/**
 * An interface to a target that can be run by a target runner.
 * @author Edward Lam
 */
public abstract class Target {
    /*
     * TODOEL: When we can handle running an adjunct which has the same name as an existing sc in the current module, 
     *   remove the name-disambiguation code.
     */
    
    /** The default name to use for the code generated for the target sc. */
    private final String defaultSCName;
    
    /**
     * A simple Target with no arguments.
     * @author Edward Lam
     */
    public static class SimpleTarget extends Target {
        
        /** The default name of the sc to use. */
        private static final String defaultSCName = CALRunner.TEST_SC_NAME;
        
        /** The body of the target definition. */
        private final SourceModel.Expr scBody;

        /**
         * Constructor for a SimpleTarget
         * @param scBody the body of the target sc defn.
         */
        public SimpleTarget(String scBody) {
            this(SourceModelUtilities.TextParsing.parseExprIntoSourceModel(scBody));
        }
        
        /**
         * Constructor for a SimpleTarget
         * @param scBody the body of the target sc defn.
         */
        public SimpleTarget(SourceModel.Expr scBody) {
            // Just use a default defaultName.
            super(defaultSCName);
            
            // SCDefn: name = body;
            this.scBody = scBody;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SourceModel.FunctionDefn getSCDefn(String scName) {
            return SourceModel.FunctionDefn.Algebraic.make(scName, Scope.PRIVATE, null, scBody);
        }
    }
    
    /**
     * Constructor for a Target.
     * @param defaultName the default name to use for the generated target's definition.
     *   This name will be used as the basis for the name passed to getSCDefn().
     */
    protected Target(String defaultName) {
        this.defaultSCName = defaultName;
    }

    /**
     * Get the name that will be used for the generated sc.
     * @return the name that will be used for the generated sc.
     */
    protected String getTargetName(ModuleTypeInfo currentModuleTypeInfo) {
        // The default implementation attempts to disambiguate the name with respect to the current module.
        return getSCName(defaultSCName, currentModuleTypeInfo);
    }
    
    /**
     * Get the SCDefinition.
     * @param scName the name of the sc.
     * @return the SCDefinition.
     */
    protected abstract SourceModel.FunctionDefn getSCDefn(String scName);
    
    /**
     * Return the target definition as specified by the Gem tree rooted at the Target.
     * This will include the sc declaration (if any) plus its definition.
     * @param declaredType the declared type, if any.  If null, a type declaration will not be included.
     * @param currentModuleTypeInfo if null, the target's default name may be used (the name of the target will not be changed).
     * @return AdjunctSource the definition of the supercombinator
     */
    public AdjunctSource.FromSourceModel getTargetDef(TypeExpr declaredType, ModuleTypeInfo currentModuleTypeInfo) {
        
        return new AdjunctSource.FromSourceModel(
            getTargetSource(declaredType, currentModuleTypeInfo).toArray(new SourceModel.TopLevelSourceElement[0]));
    }

    /**
     * Return the target definition as a list of top level source models
     * This will include the sc declaration (if any) plus its definition.
     * @param declaredType the declared type, if any.  If null, a type declaration will not be included.
     * @param currentModuleTypeInfo if null, the target's default name may be used (the name of the target will not be changed).
     * @return list of source model elements
     */
    public List<SourceModel.TopLevelSourceElement> getTargetSource(TypeExpr declaredType, ModuleTypeInfo currentModuleTypeInfo) {
        List<SourceModel.TopLevelSourceElement> scDef = new ArrayList<SourceModel.TopLevelSourceElement>();
        
        // Append the declared type if any.
        String scName = getTargetName(currentModuleTypeInfo);
        if (declaredType != null) {
            scDef.add(SourceModel.FunctionTypeDeclaration.make(scName, declaredType.toSourceModel()));
        }
        
        // Append the body..
        scDef.add(getSCDefn(scName));
        
    
        return scDef;
    }
    /**
     * Get a non-conflicting sc name to use.
     *   The generated name of the target will be based on defaultName, but disambiguated such that its name does not conflict with
     *   other sc's in the current module.
     * @param defaultName the default name to use for the generated target's definition.
     * @param currentModuleTypeInfo the ModuleTypeInfo for the module in which the target will be defined.
     *   If null, the target's default name may be used (the name of the target will not be changed).
     * @return a name to use for the sc.
     */
    private static String getSCName(String defaultName, ModuleTypeInfo currentModuleTypeInfo) {
        if (currentModuleTypeInfo == null) {
            return defaultName;
        }
        
        // Generate a name for the sc which does not conflict with any of the sc's in the current module.
        String candidateSCName = defaultName;
        int nextIndex = 2;
        while (currentModuleTypeInfo.getFunctionalAgent(candidateSCName) != null) {
            candidateSCName = defaultName + "_" + nextIndex;
            nextIndex++;
        }
        
        return candidateSCName;
    }
}
