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
 * CALCompiler_DeepTest.java
 * Creation date: Feb 24, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.File;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.util.FileSystemHelper;


/**
 * A set of time-consuming JUnit test cases for the CAL compiler.
 *
 * @author Joseph Wong
 */
public class CALCompiler_DeepTest extends TestCase {

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

        TestSuite suite = new TestSuite(CALCompiler_DeepTest.class);

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
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
    }

    /**
     * Constructor for CALCompiler_DeepTest.
     * 
     * @param name
     *            the name of the test.
     */
    public CALCompiler_DeepTest(String name) {
        super(name);
    }
    
    /**
     * Tests the source model debugging code in the CALCompiler class by compiling the workspace
     * with the debugging code turned on, and running  CAL_Platform_TestSuite.testModule.
     */
    public void testSourceModelDebuggingCode() throws GemCompilationException, CALExecutorException {
        
        BasicCALServices  privateCopyLeccServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(
            MachineType.LECC,
            "org.openquark.cal.test.workspace.CALCompiler_DeepTest.testSourceModelDebuggingCode",
            "ice.default.cws",
            null, "CALCompiler_DeepTest.testSourceModelDebuggingCode", false);
        
        try {
            ((EntryPointGenerator)privateCopyLeccServices.getWorkspaceManager().getCompiler()).setForceCodeRegen(true);        
            
            // Turn on the debugging flag in the compiler, so that each module being compiled will
            // go through a set of semantics-preserving text-to-source-model-to-text transformations
            CALCompiler.setInUnitTestDebugSourceModelMode(true);
            
            MessageLogger logger = new MessageLogger();
            privateCopyLeccServices.compileWorkspace(null, logger);
            
            if (logger.getNErrors() > 0) {
                String message = "Compilation of the workspace (with source model debugging turned on) failed with these errors:\n" + logger.toString();
                
                if (SHOW_DEBUGGING_OUTPUT) {
                    System.err.println(message);
                }
                
                // using assertEquals() here instead of fail() so that the string can be shown by double-clicking on the failure
                // in the junit view
                assertEquals("", message);
            }
            
            // Run pltform test suite and make sure that it still returns true even after all the source model
            // transformations
            runCode_TestSuite(privateCopyLeccServices);
        } finally {
            File rootDir = CALServicesTestUtilities.getWorkspaceRoot(null, "CALCompiler_DeepTest.testSourceModelDebuggingCode");
            
            boolean deleted = FileSystemHelper.delTree(rootDir);
            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                System.err.println("Workspace directory not deleted: " + rootDir);
            }
        }
    }
    
    /**
     * Helper function that runs the function  CAL_Platform_TestSuite.testModule using the specified copy
     * of BasicCALServices, which dictates the type of machine that the test is
     * run on.
     * 
     * @param calServices
     *            the copy of BasicCALServices to use in running the function
     */
    private void runCode_TestSuite(BasicCALServices calServices) throws GemCompilationException, CALExecutorException {     
        assertEquals(Boolean.TRUE,
                CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.CAL_PlatForm_Test_suite, "testModule"), calServices));
    }
}
