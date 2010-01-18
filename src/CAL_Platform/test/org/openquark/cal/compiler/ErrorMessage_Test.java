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
 * ErrorMessage_Test.java
 * Created: Jun 23, 2005
 * By: Greg McClement
 */
package org.openquark.cal.compiler;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ErrorInfo;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.GemCompilationException;


/**
 * The error messages now contain information about the position of the error/trace/assert/undefined call
 * in the source code. This test check that the correct position is being reported.
 * 
 * @author Greg McClement
 *
 */

public class ErrorMessage_Test extends TestCase {
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

        TestSuite suite = new TestSuite(ErrorMessage_Test.class);

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
     * Constructor for ErrorMessage_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public ErrorMessage_Test(String name) {
        super(name);
    }
    
    /**
     * Tests the source model debugging code in the CALCompiler class by compiling the workspace
     * with the debugging code turned on, and running tests.
     */
    public void testErrorMessage_LECC() throws GemCompilationException, CALExecutorException {
        
        // Turn on the debugging flag in the compiler, so that each module being compiled will
        // go through a set of semantics-preserving text-to-source-model-to-text transformations
        CALCompiler.setInUnitTestDebugSourceModelMode(false);
        
        BasicCALServices privateCopyLeccServices = BasicCALServices.make(
            "org.openquark.cal.test.workspace",
            "ice.default.cws",
            null);
        
        ((EntryPointGenerator)privateCopyLeccServices.getWorkspaceManager().getCompiler()).setForceCodeRegen(true);        

        try {            
            MessageLogger logger = new MessageLogger();
            privateCopyLeccServices.compileWorkspace(null, logger);
            assertEquals(logger.getNErrors(), 0);

            addSourceModelCode( privateCopyLeccServices );
            
            // Run tests and make sure that it still returns true even after all the source model
            // transformations
            runCode_test(privateCopyLeccServices);
            
        } finally {
            // Restore the workspace by compiling the original module definitions
            // without the source model transformations
            
            // Turn off the debugging flag before recompilation
            CALCompiler.setInUnitTestDebugSourceModelMode(false);
            
            ((EntryPointGenerator)privateCopyLeccServices.getWorkspaceManager().getCompiler()).setForceCodeRegen(true);        

            privateCopyLeccServices.compileWorkspace(null, new MessageLogger());
        }
        
    }
    
    /**
     * Runs the platform function using the g machine.
     */
    public void testErrorMessage_gMachine() throws GemCompilationException, CALExecutorException {
        // Not working yet
        BasicCALServices bcs = CALServicesTestUtilities.getCommonCALServices(MachineType.G, "cal.platform.test.cws");
        addSourceModelCode( bcs );
        runCode_test( bcs );
    }

    private void addSourceModelCode(BasicCALServices bcs){
        // Use the source model to add code to the workspace. Try making errors off that.
        String myFunModule = "module MyFunModule; import " + CAL_Prelude.MODULE_NAME + "; public myFunFunction = if " + CAL_Prelude.MODULE_NAME + ".False then " + CAL_Prelude.MODULE_NAME + ".True else " + CAL_Prelude.MODULE_NAME + ".error \"myFunError\";";
        
        SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(myFunModule);
        ModuleSourceDefinition moduleSourceDefn = new SourceModelModuleSource(moduleDefn);
        
        CompilerMessageLogger logger = new MessageLogger();
        Severity severity = bcs.getWorkspaceManager().makeModule(moduleSourceDefn, logger);
        assertTrue(severity.compareTo(Severity.ERROR) < 0);
    }
    
    /**
     * Helper function that runs test functions using the specified copy
     * of BasicCALServices, which dictates the type of machine that the test is
     * run on.
     * 
     * @param calServices
     *            the copy of BasicCALServices to use in running the function
     */
    private void runCode_test(BasicCALServices calServices) throws GemCompilationException, CALExecutorException {
        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "k1"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "k1"), errorInfo.getTopLevelFunctionName());
            assertEquals(81, errorInfo.getLine());
            assertEquals(5, errorInfo.getColumn());
        }
        
        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "k2"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // The compiler seems to mess up source positions so I can use the exact position
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "k2"), errorInfo.getTopLevelFunctionName());
            assertEquals(86, errorInfo.getLine());
            assertEquals(5, errorInfo.getColumn());
        }
        
        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "k3"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // The compiler seems to mess up source positions so I can use the exact position
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "k3"), errorInfo.getTopLevelFunctionName());
            assertEquals(96, errorInfo.getLine());
            assertEquals(5, errorInfo.getColumn());
        }
        
        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "j"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "j"), errorInfo.getTopLevelFunctionName());
            assertEquals(105, errorInfo.getLine());
            assertEquals(12, errorInfo.getColumn());
        }
                
        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "m"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "m"), errorInfo.getTopLevelFunctionName());
            assertEquals(118, errorInfo.getLine());
            assertEquals(9, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "n"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "n"), errorInfo.getTopLevelFunctionName());
            assertEquals(122, errorInfo.getLine());
            assertEquals(8, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "o"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "o"), errorInfo.getTopLevelFunctionName());
            assertEquals(132, errorInfo.getLine());
            assertEquals(9, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "p"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "p"), errorInfo.getTopLevelFunctionName());
            assertEquals(136, errorInfo.getLine());
            assertEquals(10, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "q"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "q"), errorInfo.getTopLevelFunctionName());
            assertEquals(143, errorInfo.getLine());
            assertEquals(10, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "r"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "r"), errorInfo.getTopLevelFunctionName());
            assertEquals(149, errorInfo.getLine());
            assertEquals(13, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "s"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "s"), errorInfo.getTopLevelFunctionName());
            assertEquals(156, errorInfo.getLine());
            assertEquals(9, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "t"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "t"), errorInfo.getTopLevelFunctionName());
            assertEquals(165, errorInfo.getLine());
            assertEquals(8, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "testCases"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "testCases"), errorInfo.getTopLevelFunctionName());
            assertEquals(178, errorInfo.getLine());
            assertEquals(17, errorInfo.getColumn());
        }

        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "testCasesRecord"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "testCasesRecord"), errorInfo.getTopLevelFunctionName());
            assertEquals(187, errorInfo.getLine());
            assertEquals(9, errorInfo.getColumn());
        }
        
        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.ErrorTest, "f"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            assertTrue( -1 != e.toString().indexOf("hi") );
        }
        
        try{        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(ModuleName.make("MyFunModule"), "myFunFunction"), calServices);
            fail( "This should have failed" );
        }
        catch(CALExecutorException.ExternalException.ErrorFunctionException e){
            // This does not have position information because the call is evaluated lazily.
            ErrorInfo errorInfo = e.getErrorInfo();
            assertNotNull(errorInfo);
            // The compiler seems to mess up source positions so I can use the exact position
            assertEquals(QualifiedName.make(ModuleName.make("MyFunModule"), "myFunFunction"), errorInfo.getTopLevelFunctionName());
//                assertEquals(72, errorInfo.getLine());
//                assertEquals(5, errorInfo.getColumn());
        }                
    }
    
}
