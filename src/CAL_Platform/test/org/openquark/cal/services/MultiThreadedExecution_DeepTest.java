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
 * MultiThreadedExecution_DeepTest.java
 * Creation date: Mar 28, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelModuleSource;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.util.Pair;


/**
 * A set of deep JUnit test cases for performing multi-threaded execution 
 * of entry points.
 * 
 * @author Joseph Wong
 */
public class MultiThreadedExecution_DeepTest extends TestCase {
    /** A copy of CAL services associated with a LECC machine for use in the test cases. */
    private static BasicCALServices leccCALServices;
    /** A copy of CAL services associated with a G machine for use in the test cases. */
    private static BasicCALServices gCALServices;

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

        TestSuite suite = new TestSuite(MultiThreadedExecution_DeepTest.class);

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
        // We really only need the Prelude module, so we use the smallest workspace currently available, i.e. cal.platform.cws
        // ...but we cannot share it (no other test can make use of it) 
        leccCALServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(MachineType.LECC, "cal.platform.cws", true);
        
        // no other test currently requires the G machine version, so our instance should be unshared
        gCALServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(MachineType.G, "cal.platform.cws", true);
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        gCALServices = null;
    }


    /**
     * Constructor for MultiThreadedExecution_DeepTest.
     * @param name the name of the test.
     */
    public MultiThreadedExecution_DeepTest(String name) {
        super(name);
    }
    
    /**
     * Compiles and executes a test function for a particular thread using the specified
     * Workspace manager and target module.
     * @param workspaceManager the workspace manager to use
     * @param targetModule the target module
     * @param threadID the name of the thread
     * @param iterID the current iteration of the thread
     * @param numEvalTries the number of times to try evaluating the compiled function
     * @param asserts a List of pairs of objects that should be equals.
     */
    private static void help_compileEvalTestFunctionForThread(WorkspaceManager workspaceManager, ModuleName targetModule, int threadID, int iterID, int numEvalTries, List asserts) {
        
        String expectedValue = "Thread " + threadID + " iteration " + iterID;

        try {
            String functionName = "tempFunc" + threadID + "_" + iterID;
            CompilerMessageLogger logger = new MessageLogger();
            
            EntryPoint entryPoint = workspaceManager.getCompiler().getEntryPoint(
                new AdjunctSource.FromSourceModel(
                    SourceModel.FunctionDefn.Algebraic.make(
                        functionName, Scope.PRIVATE, null,
                        SourceModel.Expr.makeStringValue(expectedValue))),
                EntryPointSpec.make(QualifiedName.make(targetModule, functionName)),  
                targetModule, logger);
            CALExecutor executor = workspaceManager.makeExecutorWithNewContextAndDefaultProperties();
             
            try {
                for (int i = 0; i < numEvalTries; i++) {
                    multithreadedAssertEquals(expectedValue, executor.exec(entryPoint, new Object[0]), asserts);
                }
            } finally {
                workspaceManager.resetCachedResults(executor.getContext());
            }
        } catch (Throwable t) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            multithreadedAssertEquals("No exceptions for " + expectedValue, sw.toString(), asserts);
        }
    }

    /**
     * Compiles and executes a test function for a particular thread using the specified
     * workspaceManager and target module.
     * @param workspaceManager the workspace manager to use
     * @param targetModule the target module
     * @param definingExpr the defining expression of the test function
     * @param expectedValue the expected return value
     * @param threadID the name of the thread
     * @param iterID the current iteration of the thread
     * @param numEvalTries the number of times to try evaluating the compiled function
     * @param asserts a List of pairs of objects that should be equals.
     */
    private static void help_compileEvalDependentTestFunctionForThread(WorkspaceManager workspaceManager, ModuleName targetModule, String definingExpr, Object expectedValue, int threadID, int iterID, int numEvalTries, List asserts) {
        
        try {
            String functionName = "tempFunc" + threadID + "_" + iterID;
            
            CompilerMessageLogger logger = new MessageLogger();
            
            EntryPoint entryPoint = workspaceManager.getCompiler().getEntryPoint(
                new AdjunctSource.FromSourceModel(
                    SourceModel.FunctionDefn.Algebraic.make(
                        functionName, Scope.PRIVATE, null,
                        SourceModelUtilities.TextParsing.parseExprIntoSourceModel(definingExpr))),
                EntryPointSpec.make(QualifiedName.make(targetModule, functionName)), 
                targetModule, logger);
            
            CALExecutor executor = workspaceManager.makeExecutorWithNewContextAndDefaultProperties();

            try {
                for (int i = 0; i < numEvalTries; i++) {
                    multithreadedAssertEquals(expectedValue, executor.exec(entryPoint, new Object[0]), asserts);
                }
            } finally {
                workspaceManager.resetCachedResults(executor.getContext());
            }
        } catch (Throwable t) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            multithreadedAssertEquals("No exceptions for " + expectedValue, sw.toString(), asserts);
        }
    }
    
    /**
     * Queue up the specified pair of objects to be compared using assert equals on
     * the main testing thread later on.
     * 
     * @param expectedValue the expected value
     * @param object the value obtained from running the test
     * @param asserts a List of pairs of objects that should be equals.
     */
    private static void multithreadedAssertEquals(Object expectedValue, Object object, List asserts) {
        synchronized (asserts) {
            if (SHOW_DEBUGGING_OUTPUT) {
                System.out.println("Adding pair to asserts list: expected=" + expectedValue + " actual=" + object);
            }
            asserts.add(new Pair(expectedValue, object));
        }
    }
    
    /**
     * Iterate through the list of objects to be compared and compare them using
     * assertEquals.
     * @param asserts a List of pairs of objects that should be equals.
     */
    private static void assertAll(List asserts) {
        synchronized (asserts) {
            for (int i = 0, n = asserts.size(); i < n; i++) {
                Pair pair = (Pair)asserts.get(i);
                assertEquals(pair.fst(), pair.snd());
            }
        }
    }

    /**
     * Tests multithreaded compilation using the specified copy of BasicCALServices.
     * @param numThreads the number of threads to launch.
     * @param numCompilationsPerThread the number of compilations to perform on each thread.
     * @param numEvalTries the number of times to try evaluating a compiled function
     * @param workspaceManager the copy of the workspace manager
     * @throws InterruptedException if the testing thread is interrupted
     */
    static void help_testMultithreadCompilation(int numThreads, final int numCompilationsPerThread, final int numEvalTries, final int numDependentModules, final WorkspaceManager workspaceManager) throws InterruptedException {
        Thread[] threads = new Thread[numThreads];        

        // A list of pairs of objects that should be equals. The pairs are to be compared
        // using assertEquals.
        final List assertList = Collections.synchronizedList(new ArrayList());
        
        for (int i = 0; i < numThreads; i++) {
            final int threadID = i;
            threads[i] = new Thread() {
                public void run() {
                    ModuleName moduleName = ModuleName.make("TempModule" + threadID);
                    MessageLogger messageLogger = new MessageLogger();
                    workspaceManager.makeModule(new SourceModelModuleSource(SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel("module " + moduleName + "; import " + CAL_Prelude.MODULE_NAME + "; public foo x = (\\y -> x + x + y) 3.0; public bar = foo;")), messageLogger);
                    
                    if (messageLogger.getNErrors() > 0) {
                        multithreadedAssertEquals("No compilation errors", messageLogger.toString(), assertList);
                    } else {
                        for (int k = 0; k < numDependentModules; k++) {
                            String moduleName2 = "TempModule" + threadID + "_" + k;
                            workspaceManager.makeModule(new SourceModelModuleSource(SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel("module " + moduleName2 + "; import " + CAL_Prelude.MODULE_NAME + "; import " + moduleName + ";")), messageLogger);
                        }
                        if (messageLogger.getNErrors() > 0) {
                            multithreadedAssertEquals("No compilation errors", messageLogger.toString(), assertList);
                        } else {
                            for (int j = 0; j < numCompilationsPerThread; j++) {
                                help_compileEvalTestFunctionForThread(workspaceManager, moduleName, threadID, j, numEvalTries, assertList);
                                
                                for (int k = 0; k < numDependentModules; k++) {
                                    ModuleName moduleName2 = ModuleName.make("TempModule" + threadID + "_" + k);
                                    help_compileEvalDependentTestFunctionForThread(workspaceManager, moduleName2, moduleName + ".bar 5.0", new Double(13.0), threadID, j, numEvalTries, assertList);
                                }
                            }
                        }
                    }
                }
            };
            threads[i].start();
        }
        
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
        
        assertAll(assertList);
    }
    
    /**
     * Tests multithreaded compilation using the LECC machine.
     * @throws InterruptedException if the testing thread is interrupted
     */
    public void testMultithreadedCompilationWithLECC() throws InterruptedException {
        help_testMultithreadCompilation(20, 20, 5, 1, leccCALServices.getWorkspaceManager());
    }

    /**
     * Tests multithreaded compilation using the G machine.
     * @throws InterruptedException if the testing thread is interrupted
     */
    public void testMultithreadedCompilationWithGMachine() throws InterruptedException {
        help_testMultithreadCompilation(20, 20, 5, 1, gCALServices.getWorkspaceManager());
    }

    /**
     * Tests multithreaded module compilation using the specified copy of BasicCALServices.
     * @param numThreads the number of threads to launch.
     * @param numCompilationsPerThread the number of compilations to perform on each thread.
     * @param workspaceManager the copy of the workspace manager
     * @throws InterruptedException if the testing thread is interrupted
     */
    static void help_testMultithreadModuleCompilation(int numThreads, final int numCompilationsPerThread, final WorkspaceManager workspaceManager) throws InterruptedException {
        Thread[] threads = new Thread[numThreads];        

        final List exceptionList = Collections.synchronizedList(new ArrayList());

        for (int i = 0; i < numThreads; i++) {
            final int threadID = i;
            threads[i] = new Thread() {
                public void run() {
                    String moduleName = "TempMultithreadedModule" + threadID;
                    for (int j = 0; j < numCompilationsPerThread; j++) {
                        MessageLogger messageLogger = new MessageLogger();
                        try {
                            workspaceManager.makeModule(new SourceModelModuleSource(SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel("module " + moduleName + "; import Prelude; public foo x = (\\y -> x + x + y) 3.0; public bar = foo;")), messageLogger);
                        } catch (Exception e) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            exceptionList.add(sw.toString());
                        }
                    }
                }
            };
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
        
        assertEquals(Collections.EMPTY_LIST, exceptionList);
    }
    
    /**
     * Tests multithreaded module compilation using the LECC machine.
     * @throws InterruptedException if the testing thread is interrupted
     */
    public void testMultithreadedModuleCompilationWithLECC() throws InterruptedException {
        help_testMultithreadModuleCompilation(20, 20, leccCALServices.getWorkspaceManager());
    }

    /**
     * Tests multithreaded module compilation using the G machine.
     * @throws InterruptedException if the testing thread is interrupted
     */
    public void testMultithreadedModuleCompilationWithGMachine() throws InterruptedException {
        help_testMultithreadModuleCompilation(20, 20, gCALServices.getWorkspaceManager());
    }
}
