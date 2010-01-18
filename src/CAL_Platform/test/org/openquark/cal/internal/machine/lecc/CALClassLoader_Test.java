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
 * CALClassLoader_Test.java
 * Creation date: May 9, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.internal.machine.lecc;


import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.WorkspaceManager;


/**
 * A set of JUnit test cases that tests the behaviour of the CALClassLoader class.
 *
 * @author Edward Lam
 */
public class CALClassLoader_Test extends TestCase {

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;

    /**
     * @return a test suite containing all the test cases for this test suite.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(CALClassLoader_Test.class);

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
        leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }
    
    /**
     * Constructor for CALClassLoader_Test.
     * 
     * @param name the name of the test
     */
    public CALClassLoader_Test(String name) {
        super(name);
    }

    /**
     * Tests that a lecc .class file is loaded by the correct CALClassLoader (module or adjunct loader).
     * 
     * Compiles an adjunct in a module which depends on the prelude module.
     *   The adjunct depends on a function in the current module, which in turn depends on a function in the prelude.
     * Verifies that the adjunct comes from the adjunct class loader.
     * Verifies that the adjunct's parent is the module class loader.
     * Verifies that the current module function is found by a module class loader.
     * Verifies that the prelude function is found by a module class loader.
     * 
     */
    public void testCorrectLoaderChosen() {
        
        ModuleName adjunctModuleName = CALPlatformTestModuleNames.M1;
        String adjunctUnqualifiedName = "testAdjunct";
        QualifiedName qualifiedAdjunctName = QualifiedName.make(adjunctModuleName, adjunctUnqualifiedName);
        QualifiedName qualifiedDependeeFunctionName = QualifiedName.make(adjunctModuleName, "callNot");
        QualifiedName qualifiedPreludeDependeeName = CAL_Prelude.Functions.not;
        
        // Create the adjunct source.
        SourceModel.Expr funcArg = SourceModel.Expr.makeBooleanValue(false);
        SourceModel.Expr funcExpr = SourceModel.Expr.makeGemCall(qualifiedDependeeFunctionName, funcArg);
        
        AdjunctSource source = new AdjunctSource.FromSourceModel(
                SourceModel.FunctionDefn.Algebraic.make(adjunctUnqualifiedName, Scope.PUBLIC, null, funcExpr));
        

        WorkspaceManager workspaceManager = leccCALServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        
        // Compile the adjunct definition.        
        EntryPointSpec entryPointSpec = EntryPointSpec.make(qualifiedAdjunctName, new InputPolicy[0], OutputPolicy.DEFAULT_OUTPUT_POLICY);
        workspaceManager.getCompiler().getEntryPoint (source, entryPointSpec, adjunctModuleName,  logger);

        if (logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            fail("Compilation failed: " + logger.getFirstError().getMessage());
        }
        

        // Get the classloader for module with the adjunct we compiled.
        LECCModule module = (LECCModule)workspaceManager.getModuleTypeInfo(adjunctModuleName).getModule();
        CALClassLoader adjunctModuleClassLoader = module.getClassLoader();
        
        // Check that it's an adjunct classloader..
        checkIsAdjunctClassLoader(adjunctModuleClassLoader);
        
        
        // Get the class object for the adjunct.
        Class<?> adjunctClass = getClassForFunction(qualifiedAdjunctName, adjunctModuleClassLoader, module);
        
        // Check that that classloader used to load the adjunct class is the adjunct classLoader
        assertSame(adjunctClass.getClassLoader(), adjunctModuleClassLoader);
        
        
        // Get the class object for the dependee function in the same module.
        Class<?> dependeeFunctionClass = getClassForFunction(qualifiedDependeeFunctionName, adjunctModuleClassLoader, module);
        
        // Check that that classloader used to load the function class is the adjunct classLoader's parent.
        ClassLoader dependeeFunctionClassLoader = dependeeFunctionClass.getClassLoader();
        assertSame(dependeeFunctionClassLoader, adjunctModuleClassLoader.getParent());
        
        // Check that it's a module class loader.
        checkIsModuleClassLoader(dependeeFunctionClassLoader);

        
        // Get the class object for the dependee function in the prelude module.
        Class<?> preludeDependeeClass = getClassForFunction(qualifiedPreludeDependeeName, adjunctModuleClassLoader, module);
        
        // Check that that classloader used to load the function class is not the adjunct classLoader's parent.
        ClassLoader preludeDependeeClassLoader = preludeDependeeClass.getClassLoader();
        assertTrue(preludeDependeeClassLoader != adjunctModuleClassLoader.getParent());
        
        // Check that it's a module class loader.
        checkIsModuleClassLoader(preludeDependeeClassLoader);

    }
    
    /**
     * Check whether the given class loader is an adjunct classloader.
     * @param calClassLoader the classloader to check.
     * @throws junit.framework.AssertionFailedError
     */
    private void checkIsAdjunctClassLoader(CALClassLoader calClassLoader) {
        // The adjunct class loader's parent is the module classloader.
        ClassLoader parentClassLoader = calClassLoader.getParent();
        assertTrue(parentClassLoader instanceof CALClassLoader);
        checkIsModuleClassLoader(parentClassLoader);
    }
    /**
     * Check whether the given class loader is a module classloader.
     * @param classLoader the classloader to check.
     * @throws junit.framework.AssertionFailedError
     */
    private void checkIsModuleClassLoader(ClassLoader classLoader) {
        // Must be an instance of CALClassLoader.
        assertTrue(classLoader instanceof CALClassLoader);
        
        // The parent is non-null and not a cal classloader.
        ClassLoader parentClassLoader = classLoader.getParent();
        assertNotNull(parentClassLoader);
        assertTrue(!(parentClassLoader instanceof CALClassLoader));
    }
    
    /**
     * Get the class for the given cal function.
     * @param functionName the name of the cal function.
     * @param classLoader the classloader from which to retrieve the class.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @throws junit.framework.AssertionFailedError if the class could not be found.
     */
    private static Class<?> getClassForFunction(QualifiedName functionName, ClassLoader classLoader, LECCModule module) {
        // Get the class object for the dependee function in the prelude module.
        String functionClassName = CALToJavaNames.createFullClassNameFromSC(functionName, module);
        
        try {
            return classLoader.loadClass(functionClassName);
        
        } catch (ClassNotFoundException e) {
            assertTrue("Class not found: " + e.getMessage(), false);
        }
        
        return null;    // This line shouldn't be reachable?!
        
    }
    
}
