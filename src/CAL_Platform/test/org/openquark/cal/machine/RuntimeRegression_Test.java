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
 * RuntimeRegression_Test.java
 * Creation date: June 1, 2005.
 * By: Raymond Cypher
 */
package org.openquark.cal.machine;

import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.Compiler;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.WorkspaceManager;


/**
 * A set of unit tests to be run to verify correct behaviour of the
 * CAL runtime.
 * @author RCypher
 */
public class RuntimeRegression_Test extends TestCase {

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    private static BasicCALServices gCALServices;

    private static final boolean test_g_machine = true;
    private static final boolean test_lecc_machine = true;
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(RuntimeRegression_Test.class);

        return new TestSetup(suite) {

            protected void setUp() {
                oneTimeSetUp();

            }

            protected void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        try {
            if (test_lecc_machine) {
                leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
            }

            if (test_g_machine) {
                gCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.G, "cal.platform.test.cws");
            }
        } catch (Exception e) {
            System.out.println("Failed to compile workspaces: " + e.getLocalizedMessage());
        } catch (Error e) {
            System.out.println("Failed to compile workspaces: " + e.getLocalizedMessage());
        }
    }

    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        gCALServices = null;
    }

    /**
     * Constructor for RuntimeRegression_Test.
     *
     * @param name
     *            the name of the test.
     */
    public RuntimeRegression_Test(String name) {
        super(name);
    }

    /**
     * Runs the specified function without any arguments.
     *
     * @param funcName
     *            the function to be run
     * @param calServices
     *            the copy of BasicCALServices to use
     * @param context
     * @return the result from running the function
     * @throws CALExecutorException
     */
    private Object runCAF(QualifiedName funcName, BasicCALServices calServices, ExecutionContext context)
        throws CALExecutorException {
        CompilerMessageLogger ml = new MessageLogger ();

        // Compile it, indicating that this is an adjunct
        EntryPoint targetEntryPoint =
            calServices.getWorkspaceManager().getCompiler().getEntryPoint(
                EntryPointSpec.make(funcName), funcName.getModuleName(), ml);

        if (targetEntryPoint == null) {
            return null;
        }

        if (context == null) {
            context = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();
        }

        CALExecutor runtime = calServices.getWorkspaceManager().makeExecutor(context);
        CALExecutorException exception = null;

        Object result = null;
        try {
            result = runtime.exec (targetEntryPoint, new Object[]{});
        } catch (CALExecutorException e) {
            exception = e;
        }

        calServices.getWorkspaceManager().resetCachedResults(runtime.getContext());
        runtime = null;


        if (exception != null) {
            throw exception;
        }

        return result;
    }


    /**
     * Test invoking a non-zero arity run target.
     */
    public void testInvokingNonCAF_lecc () {
        if (test_lecc_machine) {
            WorkspaceManager workspaceManager = leccCALServices.getWorkspaceManager();
            Compiler compiler = leccCALServices.getCompiler();

            CompilerMessageLogger messageLogger = new MessageLogger();
            EntryPoint entryPoint = compiler.getEntryPoint(
                EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "nonCAFEntryPoint")),
                CALPlatformTestModuleNames.RuntimeRegression, messageLogger);
            if (messageLogger.getNMessages() > 0) {
                System.err.println(messageLogger.toString());
            }

            final ExecutionContext executionContext = leccCALServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();

            CALExecutor executor = workspaceManager.makeExecutor(executionContext);
            try {
                Object result = executor.exec(entryPoint, new Object[] { new Integer(1), new Integer(2)});
                assertTrue(((Integer)result).intValue() == 6);
            } catch (CALExecutorException e) {
                fail(e.toString());
            }
        }
    }

    /**
     * Test invoking a non-zero arity run target.
     */
    public void testInvokingNonCAF_g () {
        if (test_g_machine) {
            WorkspaceManager workspaceManager = gCALServices.getWorkspaceManager();
            Compiler compiler = gCALServices.getCompiler();

            CompilerMessageLogger messageLogger = new MessageLogger();
            EntryPoint entryPoint = compiler.getEntryPoint(
                EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "nonCAFEntryPoint")),
                CALPlatformTestModuleNames.RuntimeRegression, messageLogger);
            if (messageLogger.getNMessages() > 0) {
                System.err.println(messageLogger.toString());
            }

            final ExecutionContext executionContext = gCALServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();

            CALExecutor executor = workspaceManager.makeExecutor(executionContext);
            try {
                Object result = executor.exec(entryPoint, new Object[] { new Integer(1), new Integer(2)});
                assertTrue(((Integer)result).intValue() == 6);
            } catch (CALExecutorException e) {
                fail(e.toString());
            }
        }
    }

    /**
     * Test debug functions for listing CAFs and showing CAF space usage.
     * @throws CALExecutorException
     */
    public void testCAFSpaceUsage_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testSpaceUsageAliasCAF"), leccCALServices, null);
            assertNotNull(result);
            result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testSpaceUsageLiteralCAF"), leccCALServices, null);
            assertNotNull(result);
            result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testSpaceUsageAliasLiteralCAF"), leccCALServices, null);
            assertNotNull(result);
//            List<?> cafs = (List<?>)runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testListingDebugCAFs"), leccCALServices, null);
//            boolean foundPrelude = false;
//            for (int i = 0, n = cafs.size(); i < n && !foundPrelude; ++i) {
//                foundPrelude = (((String)cafs.get(i)).indexOf(CAL_Prelude.MODULE_NAME.toSourceText()) >= 0);
//            }
//            assertTrue(!foundPrelude);

//            List<?> cafs = (List<?>)runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testListingDebugCAFsWithDependees"), leccCALServices, null);
//            boolean foundPrelude = false;
//            for (int i = 0, n = cafs.size(); i < n && !foundPrelude; ++i) {
//                foundPrelude = (((String)cafs.get(i)).indexOf(CAL_Prelude.MODULE_NAME.toSourceText()) >= 0);
//            }
//            assertTrue(foundPrelude);
        }
    }
    public void testCAFSpaceUsage_g () throws CALExecutorException {
        if (test_g_machine) {
            Object result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testSpaceUsageAliasCAF"), gCALServices, null);
            assertNotNull(result);
            result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testSpaceUsageLiteralCAF"), gCALServices, null);
            assertNotNull(result);
            result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testSpaceUsageAliasLiteralCAF"), gCALServices, null);
            assertNotNull(result);
            List<?> cafs = (List<?>)runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testListingDebugCAFs"), gCALServices, null);
            boolean foundPrelude = false;
            for (int i = 0, n = cafs.size(); i < n && !foundPrelude; ++i) {
                foundPrelude = (((String)cafs.get(i)).indexOf(CAL_Prelude.MODULE_NAME.toSourceText()) >= 0);
            }
            assertTrue(!foundPrelude);

            cafs = (List<?>)runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testListingDebugCAFsWithDependees"), gCALServices, null);
            foundPrelude = false;
            for (int i = 0, n = cafs.size(); i < n && !foundPrelude; ++i) {
                foundPrelude = (((String)cafs.get(i)).indexOf(CAL_Prelude.MODULE_NAME.toSourceText()) >= 0);
            }
            assertTrue(foundPrelude);
        }
    }


    public void testInvalidCoercion_lecc ()  {
        if (leccCALServices != null) {
            CompilerMessageLogger logger = new MessageLogger();
            AdjunctSource as = new AdjunctSource.FromText("testRunTarget = jObjectToInteger (unsafeCoerce (1 :: Int));");
            EntryPoint entryPoint =
                leccCALServices.getCompiler().getEntryPoint(
                    as,
                    EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testRunTarget")),
                    CALPlatformTestModuleNames.RuntimeRegression,
                    logger);
            CALExecutor runtime = leccCALServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();
            try {
                runtime.exec (entryPoint, new Object[]{});
            } catch (Exception e) {
                String causeMessage = e.getCause().getCause().getLocalizedMessage();
                int i = causeMessage.indexOf("mismatch");
                assertTrue(i >= 0);
            } catch (Error e) {
                String causeMessage = e.getCause().getLocalizedMessage();
                int i = causeMessage.indexOf("mismatch");
                assertTrue(i >= 0);
            }
        }
    }


    /**
     * Execute RuntimeRegression.spaceTest1
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest1_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest1"), leccCALServices, null);
            assertEquals(new Double(5.0000005E13), result);
        }
    }

    public void testSpaceTest1_g() throws CALExecutorException {
        if(test_g_machine) {
            // There is a known issue in the g-machine which currently prevents this test
            // from completing.  At this point in time it is expected to fail with an
            // out of memory error.
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest1g"), gCALServices, null);
            assertEquals(new Double(4.5000015E12), result);
        }
    }


    /**
     * Execute RuntimeRegression.spaceTest2
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest2_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest2"), leccCALServices, null);
            assertEquals(new Character('a'), result);
        }
    }
    public void testSpaceTest2_g() throws CALExecutorException {
        if(test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest2g"), gCALServices, null);
            assertEquals(new Character('a'), result);
        }
    }

    /**
     * Execute RuntimeRegression.spaceTest3
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest3_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest3"), leccCALServices, null);
            assertEquals(new Double(1.0E7), result);
        }
    }
    public void testSpaceTest3_g() throws CALExecutorException {
        if(test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest3g"), gCALServices, null);
            assertEquals(new Double(1000000.0), result);
        }
    }

    /**
     * Execute RuntimeRegression.spaceTest4
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest4_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest4"), leccCALServices, null);
            assertEquals(new Double(5000000.5), result);
        }
    }
    public void testSpaceTest4_g() throws CALExecutorException {
        if(test_g_machine){
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest4g"), gCALServices, null);
            assertEquals(new Double(500000.5), result);
        }
    }

    /**
     * Execute RuntimeRegression.spaceTest5
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest5_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest5"), leccCALServices, null);
            assertEquals(new Double(5000000.5), result);
        }
    }
    public void testSpaceTest5_g() throws CALExecutorException {
        if(test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest5g"), gCALServices, null);
            assertEquals(new Double(500000.5), result);
        }
    }

    /**
     * Execute RuntimeRegression.spaceTest6
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest6_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest6"), leccCALServices, null);
            assertEquals(Boolean.TRUE, result);
        }
    }

    /**
     * Execute RuntimeRegression.spaceTest7
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest7_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest7"), leccCALServices, null);
            assertEquals("True", result);
        }
    }

    /**
     * Execute RuntimeRegression.spaceTest8
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest8_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest8"), leccCALServices, null);
            assertEquals(Boolean.TRUE, result);
        }
    }

    /**
     * Execute RuntimeRegression.spaceTest9
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest9_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest9"), leccCALServices, null);
            assertEquals(Boolean.TRUE, result);
        }
    }


    /**
     * Execute RuntimeRegression.spaceTest11
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testSpaceTest11_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest11"), leccCALServices, null);
            assertEquals(Boolean.TRUE, result);
        }
    }

    public void testSpaceTest11_g() throws CALExecutorException {
        if(test_g_machine) {
            // Currently there is a known bug in the g-machine that prevents this test from completing successfully.
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "spaceTest11g"), gCALServices, null);
            assertEquals(Boolean.TRUE, result);
        }
    }

    /**
     * Execute RuntimeRegression.testPartialApplications
     * We are checking that the function runs to completion and produces the expected value.
     * @throws CALExecutorException
     */
    public void testPartialApplications_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            help_testPartialApplications(leccCALServices);
        }
    }
    public void testPartialApplications_g() throws CALExecutorException {
        if(test_g_machine) {
            help_testPartialApplications(gCALServices);
        }
    }

    private void help_testPartialApplications(BasicCALServices calServices) throws CALExecutorException {
        Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testPartialApplications"), calServices, null);
        assertEquals(Boolean.TRUE, result);
    }

    /**
     * Test transient memoization.  i.e. memoization which only exists for the duration of a function
     * evaluation.  This tests memoization of a single argument function.
     * @throws CALExecutorException
     */
    public void testMemoization_Transient_one_param_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            help_testMemoization_Transient(leccCALServices, "instanceCacheTest");
        }
    }
    public void testMemoization_Transient_one_param_g() throws CALExecutorException {
        if (test_g_machine) {
            help_testMemoization_Transient(gCALServices, "instanceCacheTest");
        }
    }

    /**
     * Test transient memoization.  i.e. memoization which only exists for the duration of a function
     * evaluation.  This tests memoization of a four argument function.
     * @throws CALExecutorException
     */
    public void testMemoization_Transient_four_param_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            help_testMemoization_Transient(leccCALServices, "instanceCacheTestForMultipleParams");
        }
    }
    public void testMemoization_Transient_four_param_g() throws CALExecutorException {
        if (test_g_machine) {
            help_testMemoization_Transient(gCALServices, "instanceCacheTestForMultipleParams");
        }
    }

    /**
     * Test transient memoization.  i.e. memoization which only exists for the duration of a function
     * evaluation.
     * @param calServices
     * @param function
     * @throws CALExecutorException
     */
    private void help_testMemoization_Transient(BasicCALServices calServices, String function) throws CALExecutorException {
        Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.Memoize_Tests, function), calServices, null);
        assertEquals(Boolean.TRUE, result);
    }

    /**
     * Test context scope memoization.  i.e. memoization which exists for the duration of an
     * execution context.
     * @throws CALExecutorException
     */
    public void testMemoization_Context_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            help_testMemoization_Context(leccCALServices);
        }
    }
    public void testMemoization_Context_g() throws CALExecutorException {
        if (test_g_machine) {
            help_testMemoization_Context(gCALServices);
        }
    }

    /**
     * Test context scope memoization.  i.e. memoization which exists for the duration of an
     * execution context.
     * @param calServices
     * @throws CALExecutorException
     */
    private void help_testMemoization_Context(BasicCALServices calServices) throws CALExecutorException {
        Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.Memoize_Tests, "multiInstanceCacheTestForFourParams"), calServices, null);
        assertEquals(Boolean.TRUE, result);
    }

    /**
     * Test CAF cacheing.  We want to verify that there is a different CAF instance
     * for each execution context.
     * We do this by wrapping a zero arity foreign function in a CAF.  The foreign function
     * returns a different value each time it is called.
     * @throws CALExecutorException
     */
    public void testCAF_Cacheing_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            help_testCAF_Cacheing(leccCALServices);
        }
    }
    public void testCAF_Cacheing_g() throws CALExecutorException {
        if (test_g_machine) {
            help_testCAF_Cacheing(gCALServices);
        }
    }

    private void help_testCAF_Cacheing (BasicCALServices calServices) throws CALExecutorException {
        CompilerMessageLogger ml = new MessageLogger ();

        // Compile it, indicating that this is an adjunct
        EntryPoint targetEntryPoint =
            calServices.getWorkspaceManager().getCompiler().getEntryPoint(
                EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "getCallCountCAF")),
                CALPlatformTestModuleNames.RuntimeRegression, ml);

        if (targetEntryPoint == null) {
            fail();
        }

        // Create an executor and run the CAF twice.
        CALExecutor runtime = calServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();

        Object result1 = runtime.exec (targetEntryPoint, new Object[]{});
        Object result2 = runtime.exec (targetEntryPoint, new Object[]{});

        // Create a second executor (and execution context) and run the CAF twice more.
        runtime = calServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();
        Object result3 = runtime.exec (targetEntryPoint, new Object[]{});
        Object result4 = runtime.exec (targetEntryPoint, new Object[]{});

        // The two results should be different, since each run has its own execution context.
        assertTrue(result1.equals(result2));
        assertTrue(result3.equals(result4));
        assertTrue(!result1.equals(result3));
    }

    /**
     * Run Cal_PlatformTestSuite.testModule.
     * This function is the general purpose regression testing function
     * designed to exercise as many language/runtime features in CAL as possible.
     * @throws CALExecutorException
     */
    public void test_platform_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            help_testPlatform(leccCALServices);
        }
    }
    public void test_pptlatform_g () throws CALExecutorException {
        if (test_g_machine) {
            help_testPlatform(gCALServices);
        }
    }

    private void help_testPlatform (BasicCALServices calServices) throws CALExecutorException {
        Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.CAL_PlatForm_Test_suite, "testModule"), calServices, null);
        assertEquals(Boolean.TRUE, result);
    }


    /**
     * Test automatically generated input/output instances.
     * @throws CALExecutorException
     */
    public void testDerivedIO_lecc() throws CALExecutorException {
        if (test_lecc_machine) {
            help_testDerivedIO(leccCALServices);
        }
    }
    public void testDerivedIO_g() throws CALExecutorException {
        if (test_g_machine) {
            help_testDerivedIO(gCALServices);
        }
    }

    private void help_testDerivedIO (BasicCALServices calServices) throws CALExecutorException {
        // The first three test functions in M2 should all complete succesfully and return true.
        Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.M2, "testOutThenInMyInt"), calServices, null);
        assertEquals(Boolean.TRUE, result);

        result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.M2, "testOutThenInMyLong"), calServices, null);
        assertEquals(Boolean.TRUE, result);

        result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.M2, "testMyString"), calServices, null);
        assertEquals(Boolean.TRUE, result);

        // The next three functions should all fail with a class cast exception.
        // runCAF will throw a CALExecutorException in the case of a failure in
        // evaluation.
        try {
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.M2, "testOutThenIn3"), calServices, null);
            assertTrue(false);
        } catch (CALExecutorException e) {
            Throwable cause = e.getCause();
            assertTrue (cause instanceof ClassCastException);
        }

        try {
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.M2, "testOutThenIn4"), calServices, null);
            assertTrue(false);
        } catch (CALExecutorException e) {
            Throwable cause = e.getCause();
            assertTrue (cause instanceof ClassCastException);
        }

        try {
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.M2, "testMyString2"), calServices, null);
            assertTrue(false);
        } catch (CALExecutorException e) {
            Throwable cause = e.getCause();
            assertTrue (cause instanceof ClassCastException);
        }
    }

    /**
     * Test resolution of alias functions with different strictness.
     */
    public void testAliasingWithStrictness_lecc() {
        if (test_lecc_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias1").getAliasOf() != null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias2").getAliasOf() != null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias3").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias4").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias5").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias6").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias7").getAliasOf() != null);
        }
    }

    /**
     * Test a function aliasing a function with the same name from a different module.
     */
    public void testAliasingWithSameName_lecc() {
        if (test_lecc_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("filter").getAliasOf() != null);
        }
    }

    /**
     * Test a CAF aliasing a non-CAF.
     */
    public void testAliasingWithCAF_to_nonCAF_lecc() {
        if (test_lecc_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasingCAF").getAliasOf() != null);
        }
    }

    /**
     * Test CAFs aliasing CAFs defined as a literal value.
     */
    public void testAliasingWithCAF_to_Literal_lecc() {
        if (test_lecc_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("literalCAF").getLiteralValue() != null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF").getLiteralValue() != null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF2").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF2").getLiteralValue() != null);
        }
    }

    /**
     * Test a CAF aliasing a zero arity and non-zero arity foreign function.
     */
    public void testAliasingWithCAF_to_ForeignFunction_lecc () {
        if (test_lecc_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("appendStringNoCase").getAliasOf().equals(CAL_Prelude_internal.Functions.appendString));
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedForeignCAF").getAliasOf() == null);
        }
    }

    /**
     * Test resolution of alias functions with different strictness.
     */
    public void testAliasingWithStrictness_g() {
        if (test_g_machine) {
            assertTrue(gCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias1").getAliasOf() != null);
            assertTrue(gCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias2").getAliasOf() != null);
            assertTrue(gCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias3").getAliasOf() == null);
            assertTrue(gCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias4").getAliasOf() == null);
            assertTrue(gCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias5").getAliasOf() == null);
            assertTrue(gCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias6").getAliasOf() == null);
            assertTrue(gCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("alias7").getAliasOf() != null);
        }
    }

    /**
     * Test a function aliasing a function with the same name from a different module.
     */
    public void testAliasingWithSameName_g() {
        if (test_g_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("filter").getAliasOf() != null);
        }
    }

    /**
     * Test a CAF aliasing a non-CAF.
     */
    public void testAliasingWithCAF_to_nonCAF_g() {
        if (test_g_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasingCAF").getAliasOf() != null);
        }
    }

    /**
     * Test CAFs aliasing CAFs defined as a literal value.
     */
    public void testAliasingWithCAF_to_Literal_g() {
        if (test_g_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("literalCAF").getLiteralValue() != null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF").getLiteralValue() != null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF2").getAliasOf() == null);
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedLiteralCAF2").getLiteralValue() != null);
        }
    }

    /**
     * Test a CAF aliasing a zero arity and non-zero arity foreign function.
     */
    public void testAliasingWithCAF_to_ForeignFunction_g () {
        if (test_g_machine) {
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("appendStringNoCase").getAliasOf().equals(CAL_Prelude_internal.Functions.appendString));
            assertTrue(leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.RuntimeRegression).getModule().getFunction("aliasedForeignCAF").getAliasOf() == null);
        }
    }

    public void test64KClassFileLimit_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "sixtyFourKbLimit"), leccCALServices, null);
            assertEquals(new Double(199.0), result);

        }
    }

    public void test64KClassFileLimit_g () throws CALExecutorException {
        if (test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "sixtyFourKbLimit"), gCALServices, null);
            assertEquals(new Double(199.0), result);

        }
    }

    public void testExcerciseLetVars_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "exerciseLetVars"), leccCALServices, null);
            assertEquals(Boolean.TRUE, result);

        }
    }

    public void testExcerciseLetVars_g () throws CALExecutorException {
        if (test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "exerciseLetVars"), gCALServices, null);
            assertEquals(Boolean.TRUE, result);

        }
    }

    /**
     * Test using input and output to convert between JObject and unboxed values
     * when dealing with literals.
     * @throws CALExecutorException
     */
    public void testIOForStrictLiteralValues_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictIntObject"), leccCALServices, null);
            assertEquals(result, Boolean.FALSE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictCharObject"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictBooleanObject"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictLongObject"), leccCALServices, null);
            assertEquals(result, new Long(2));

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictByteObject"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictDoubleObject"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictFloatObject"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictForeignObject"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);
        }
    }

    public void testIOForStrictLiteralValues_g () throws CALExecutorException {
        if (test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictIntObject"), gCALServices, null);
            assertEquals(result, Boolean.FALSE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictCharObject"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictBooleanObject"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictLongObject"), gCALServices, null);
            assertEquals(result, new Long(2));

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictByteObject"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictDoubleObject"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictFloatObject"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictForeignObject"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);
        }
    }

    public void testIOForStrictForeignValuesFromPrimitive_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictJObjectFromForeignInt"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictJObjectFromPrimOpInt"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictJObjectFromLiteralInt"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);

        }
    }

    public void testIOForStrictForeignValuesFromPrimitive_g () throws CALExecutorException {
        if (test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictJObjectFromForeignInt"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictJObjectFromPrimOpInt"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "testStrictJObjectFromLiteralInt"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);

        }
    }

    /**
     * Test a case where the ordering of arguments in a tail recursive function has caused problems.
     * @throws CALExecutorException
     */
    public void testTailRecursiveArgumentOrdering1_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "recursiveArgOrderingTest"), leccCALServices, null);
            assertEquals(result, Boolean.TRUE);
        }
    }
    public void testTailRecursiveArgumentOrdering1_g () throws CALExecutorException {
        if (test_g_machine) {
            Object result = runCAF (QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "recursiveArgOrderingTest"), gCALServices, null);
            assertEquals(result, Boolean.TRUE);
        }
    }

    /**
     * Run tests of the behavior of the Prelude.seq primitive.
     * @throws CALExecutorException
     */
    public void testSeq_lecc () throws CALExecutorException {
        if (test_lecc_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest1"), leccCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest2"), leccCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest3"), leccCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest4"), leccCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest5"), leccCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqTest1"), leccCALServices, null);
            assertEquals(result, new Double(0.0));
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqTest2"), leccCALServices, null);
            assertEquals(result, new Double(0.0));
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqTest3"), leccCALServices, null);
            assertEquals(result, new Double(7.0));

        }
    }
    public void testSeq_g () throws CALExecutorException {
        if (test_g_machine) {
            Object result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest1"), gCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest2"), gCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest3"), gCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest4"), gCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqAssociativityTest5"), gCALServices, null);
            assertEquals(result, "a, b, c");
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqTest1"), gCALServices, null);
            assertEquals(result, new Double(0.0));
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqTest2"), gCALServices, null);
            assertEquals(result, new Double(0.0));
            result = runCAF(QualifiedName.make(CALPlatformTestModuleNames.RuntimeRegression, "seqTest3"), gCALServices, null);
            assertEquals(result, new Double(7.0));

        }
    }

    /**
     * Tests the error message generated when trying to reduce a circular record definition
     * or circular let variable definition in the lecc machine.
     * circularLetVarDefinition =
     *   let
     *       a = b;
     *       b = c;
     *       c = a;
     *   in
     *       a + b + c + 1.0;
     *
     * circularRecordDefinition =
     *    let
     *        (a, b, c) = (c, a, b);
     *    in
     *        a + b + c + 1.0;
     *
     */
    public void testCircularRecordAndLetVarInLECC () {
        if (test_lecc_machine) {
            try {
                runCAF (QualifiedName.make(CALPlatformTestModuleNames.M2, "circularLetVarDefinition"), leccCALServices, null);
                fail ("Unexpected result from M2.circularLetVarDef");
            } catch (CALExecutorException e) {
                String message = e.getCause().getMessage();
                assertEquals(message, "Invalid reduction state in indirection.  This is probably caused by a circular let variable definition.");
            }
            try {
                runCAF (QualifiedName.make(CALPlatformTestModuleNames.M2, "circularRecordDefinition"), leccCALServices, null);
                fail ("Unexpected result from M2.circularLetVarDef");
            } catch (CALExecutorException e) {
                String message = e.getCause().getMessage();
                assertEquals(message, "Invalid reduction state in record selection.  This is probably caused by a circular record definition.");
            }
        }
    }
}

