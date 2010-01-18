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
 * UncheckedCALDocReferencesValidity_Test.java 
 * Creation date: Dec 8, 2006.
 * By: Joseph Wong
 */

package org.openquark.cal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.CALDocCommentTextBlockTraverser;
import org.openquark.cal.compiler.CALDocCommentTextBlockVisitor;
import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.CALDocComment.DataConsLinkSegment;
import org.openquark.cal.compiler.CALDocComment.FunctionOrClassMethodLinkSegment;
import org.openquark.cal.compiler.CALDocComment.ModuleLinkSegment;
import org.openquark.cal.compiler.CALDocComment.ModuleReference;
import org.openquark.cal.compiler.CALDocComment.ScopedEntityReference;
import org.openquark.cal.compiler.CALDocComment.TypeClassLinkSegment;
import org.openquark.cal.compiler.CALDocComment.TypeConsLinkSegment;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.WorkspaceManager;


/**
 * Contains test cases for validating the unchecked see/link references appearing
 * in CALDoc comments.
 *
 * @author Joseph Wong
 */
public class UncheckedCALDocReferencesValidity_Test extends TestCase {

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;

    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        final TestSuite suite = new TestSuite(UncheckedCALDocReferencesValidity_Test.class);

        return new TestSetup(suite) {

            @Override
            protected void setUp() {
                oneTimeSetUp();
                
            }
    
            @Override
            protected void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.libraries.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }

    public void testUncheckedCALDocReferencesValidity() {
        helpTestUncheckedCALDocReferencesValidity(leccCALServices);
    }
    
    public void helpTestUncheckedCALDocReferencesValidity(final BasicCALServices calServices) {
        
        final WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        
        final ModuleName[] moduleNames = workspaceManager.getModuleNamesInProgram();
        
        final List<String> brokenReferences = new ArrayList<String>();
        
        for (final ModuleName moduleName : moduleNames) {
            if (moduleName.equals(CALPlatformTestModuleNames.CALDocTest)) {
                // CALDocTest is allowed to have unresolved references (because it tests CALDoc!)
                continue;
            }
            
            final ModuleTypeInfo moduleInfo = calServices.getWorkspaceManager().getModuleTypeInfo(moduleName);
            
            // Gather all the CALDoc comments in the module
            
            final Map<String, CALDocComment> caldocComments = new LinkedHashMap<String, CALDocComment>();
            
            caldocComments.put("module " + moduleName, moduleInfo.getCALDocComment());
            
            for (int i = 0, n = moduleInfo.getNFunctions(); i < n; i++) {
                final Function function = moduleInfo.getNthFunction(i);
                caldocComments.put("function " + function.getName().getQualifiedName(), function.getCALDocComment());
            }
            
            for (int i = 0, n = moduleInfo.getNTypeConstructors(); i < n; i++) {
                final TypeConstructor typeCons = moduleInfo.getNthTypeConstructor(i);
                caldocComments.put("type cons " + typeCons.getName().getQualifiedName(), typeCons.getCALDocComment());

                for (int j = 0, m = typeCons.getNDataConstructors(); j < m; j++) {
                    final DataConstructor dataCons = typeCons.getNthDataConstructor(j);
                    caldocComments.put("data cons " + dataCons.getName().getQualifiedName(), dataCons.getCALDocComment());
                }
            }
            
            for (int i = 0, n = moduleInfo.getNTypeClasses(); i < n; i++) {
                final TypeClass typeClass = moduleInfo.getNthTypeClass(i);
                caldocComments.put("class " + typeClass.getName().getQualifiedName(), typeClass.getCALDocComment());

                for (int j = 0, m = typeClass.getNClassMethods(); j < m; j++) {
                    final ClassMethod method = typeClass.getNthClassMethod(j);
                    caldocComments.put("class method " + method.getName().getQualifiedName(), method.getCALDocComment());
                }
            }
            
            for (int i = 0, n = moduleInfo.getNClassInstances(); i < n; i++) {
                final ClassInstance instance = moduleInfo.getNthClassInstance(i);
                final TypeClass typeClass = instance.getTypeClass();
                
                caldocComments.put("instance " + instance.getNameWithContext(), instance.getCALDocComment());

                for (int j = 0, m = typeClass.getNClassMethods(); j < m; j++) {
                    final ClassMethod method = typeClass.getNthClassMethod(j);
                    caldocComments.put(
                        "instance method " + instance.getNameWithContext() + " " + method.getName().getQualifiedName(),
                        method.getCALDocComment());
                }
            }
            
            // Then visit each comment verifying the validity of unchecked references
            
            final CALDocCommentTextBlockVisitor<String, Void> visitor = new CALDocCommentTextBlockTraverser<String, Void>() {

                /** {@inheritDoc} */
                @Override
                public Void visitDataConsLinkSegment(DataConsLinkSegment segment, String arg) {
                    validateGemReference(segment.getReference(), arg, workspaceManager, brokenReferences);
                    return super.visitDataConsLinkSegment(segment, arg);
                }

                /** {@inheritDoc} */
                @Override
                public Void visitFunctionOrClassMethodLinkSegment(FunctionOrClassMethodLinkSegment segment, String arg) {
                    validateGemReference(segment.getReference(), arg, workspaceManager, brokenReferences);
                    return super.visitFunctionOrClassMethodLinkSegment(segment, arg);
                }

                /** {@inheritDoc} */
                @Override
                public Void visitModuleLinkSegment(ModuleLinkSegment segment, String arg) {
                    validateModuleReference(segment.getReference(), arg, workspaceManager, brokenReferences);
                    return super.visitModuleLinkSegment(segment, arg);
                }

                /** {@inheritDoc} */
                @Override
                public Void visitTypeClassLinkSegment(TypeClassLinkSegment segment, String arg) {
                    validateTypeClassReference(segment.getReference(), arg, workspaceManager, brokenReferences);
                    return super.visitTypeClassLinkSegment(segment, arg);
                }

                /** {@inheritDoc} */
                @Override
                public Void visitTypeConsLinkSegment(TypeConsLinkSegment segment, String arg) {
                    validateTypeConsReference(segment.getReference(), arg, workspaceManager, brokenReferences);
                    return super.visitTypeConsLinkSegment(segment, arg);
                }

            };
            
            for (final Map.Entry<String, CALDocComment> entry : caldocComments.entrySet()) {
                
                final String entityName = entry.getKey();
                final CALDocComment comment = entry.getValue();
                
                if (comment != null) {
                    if (comment.getDescriptionBlock() != null) {
                        comment.getDescriptionBlock().accept(visitor, entityName);
                    }
                    
                    if (comment.getDeprecatedBlock() != null) {
                        comment.getDeprecatedBlock().accept(visitor, entityName);
                    }

                    for (int j = 0, m = comment.getNArgBlocks(); j < m; j++) {
                        if (comment.getNthArgBlock(j).getTextBlock() != null) {
                            comment.getNthArgBlock(j).getTextBlock().accept(visitor, entityName);
                        }
                    }
                    
                    for (int j = 0, m = comment.getNAuthorBlocks(); j < m; j++) {
                        if (comment.getNthAuthorBlock(j) != null) {
                            comment.getNthAuthorBlock(j).accept(visitor, entityName);
                        }
                    }
                    
                    for (int j = 0, m = comment.getNDataConstructorReferences(); j < m; j++) {
                        if (comment.getNthDataConstructorReference(j) != null) {
                            validateGemReference(comment.getNthDataConstructorReference(j), entityName, workspaceManager, brokenReferences);
                        }
                    }
                    for (int j = 0, m = comment.getNFunctionOrClassMethodReferences(); j < m; j++) {
                        if (comment.getNthFunctionOrClassMethodReference(j) != null) {
                            validateGemReference(comment.getNthFunctionOrClassMethodReference(j), entityName, workspaceManager, brokenReferences);
                        }
                    }
                    
                    for (int j = 0, m = comment.getNModuleReferences(); j < m; j++) {
                        if (comment.getNthModuleReference(j) != null) {
                            validateModuleReference(comment.getNthModuleReference(j), entityName, workspaceManager, brokenReferences);
                        }
                    }
                    
                    for (int j = 0, m = comment.getNTypeClassReferences(); j < m; j++) {
                        if (comment.getNthTypeClassReference(j) != null) {
                            validateTypeClassReference(comment.getNthTypeClassReference(j), entityName, workspaceManager, brokenReferences);
                        }
                    }
                    
                    for (int j = 0, m = comment.getNTypeConstructorReferences(); j < m; j++) {
                        if (comment.getNthTypeConstructorReference(j) != null) {
                            validateTypeConsReference(comment.getNthTypeConstructorReference(j), entityName, workspaceManager, brokenReferences);
                        }
                    }

                    if (comment.getReturnBlock() != null) {
                        comment.getReturnBlock().accept(visitor, entityName);
                    }
                    
                    if (comment.getSummary() != null) {
                        comment.getSummary().accept(visitor, entityName);
                    }
                    
                    if (comment.getVersionBlock() != null) {
                        comment.getVersionBlock().accept(visitor, entityName);
                    }

                }
            }
        }
        
        if (!brokenReferences.isEmpty()) {
            final StringBuffer failureString = new StringBuffer("The following unchecked CALDoc references cannot be resolved:\n\n");
            
            for (final String brokenReference : brokenReferences) {
                failureString.append(brokenReference).append('\n');
            }
            
            fail(failureString.toString());
        }
    }
    
    private void validateGemReference(final ScopedEntityReference reference, final String entityName, final WorkspaceManager workspaceManager, final List<String> brokenReferences) {
        if (!reference.isChecked()) {
            final ModuleTypeInfo referencedModuleInfo = workspaceManager.getModuleTypeInfo(reference.getName().getModuleName());
            if (referencedModuleInfo == null || referencedModuleInfo.getFunctionalAgent(reference.getName().getUnqualifiedName()) == null) {
                brokenReferences.add("in CALDoc for " + entityName + ": " + reference);
            }
        }
    }
    
    private void validateTypeConsReference(final ScopedEntityReference reference, final String entityName, final WorkspaceManager workspaceManager, final List<String> brokenReferences) {
        if (!reference.isChecked()) {
            final ModuleTypeInfo referencedModuleInfo = workspaceManager.getModuleTypeInfo(reference.getName().getModuleName());
            if (referencedModuleInfo == null || referencedModuleInfo.getTypeConstructor(reference.getName().getUnqualifiedName()) == null) {
                brokenReferences.add("in CALDoc for " + entityName + ": " + reference);
            }
        }
    }
    
    private void validateTypeClassReference(final ScopedEntityReference reference, final String entityName, final WorkspaceManager workspaceManager, final List<String> brokenReferences) {
        if (!reference.isChecked()) {
            final ModuleTypeInfo referencedModuleInfo = workspaceManager.getModuleTypeInfo(reference.getName().getModuleName());
            if (referencedModuleInfo == null || referencedModuleInfo.getTypeClass(reference.getName().getUnqualifiedName()) == null) {
                brokenReferences.add("in CALDoc for " + entityName + ": " + reference);
            }
        }
    }
    
    private void validateModuleReference(final ModuleReference reference, final String entityName, final WorkspaceManager workspaceManager, final List<String> brokenReferences) {
        if (!reference.isChecked()) {
            if (workspaceManager.getModuleTypeInfo(reference.getName()) == null) {
                brokenReferences.add("in CALDoc for " + entityName + ": " + reference);
            }
        }
    }
}
