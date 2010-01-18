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

import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;


/**
 * A set of deep JUnit test cases for performing multi-threaded execution 
 * using basicCalServices.
 * 
 * @author mbyne
 */
public class MultiThreadedBasicCalServices_DeepTest extends TestCase {
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

        TestSuite suite = new TestSuite(MultiThreadedBasicCalServices_DeepTest.class);

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
    public MultiThreadedBasicCalServices_DeepTest(String name) {
        super(name);
    }
    
 


    /**
     * Tests multithreaded use of runFunction with specified copy of
     * BasicCALServices. Each thread uses a number of different entry points
     * that are differentiated by the input policies. When there are more unique
     * entry points than the cache size, entry points will have to be recreated -
     * this will be happening from multiple threads so it will stress the
     * locking mechanism of the entry point cache.
     * 
     * @param numThreads
     *            the number of threads to launch.
     * @param numCompilationsPerThread
     *            the number of compilations to perform on each thread.
     * @param basicCalServices
     *            the copy of the workspace manager
     * @throws InterruptedException
     *             if the testing thread is interrupted
     */
    static void help_testMultithreadRunFunction(int numThreads, final int numCompilationsPerThread, final int numTimesToRun, final BasicCALServices basicCalServices) throws InterruptedException {
        Thread[] threads = new Thread[numThreads];        

        final List<String> exceptionList = Collections.synchronizedList(new ArrayList<String>());

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread() {
                public void run() {
                    for (int j = 0; j < numCompilationsPerThread; j++) {

                        try {

                            //Make entry point specs - there will be numCompilationsPerThread unique
                            //entry point specs
                            EntryPointSpec spec = 
                                EntryPointSpec.make(CAL_Prelude.Functions.add, new InputPolicy[] { 
                                        InputPolicy.makeWithMarshaler(SourceModelUtilities.TextParsing.parseExprIntoSourceModel("add ("+j*2+"::Int) # Prelude.input ", new MessageLogger())),
                                        InputPolicy.DEFAULT_INPUT_POLICY}, 
                                        OutputPolicy.DEFAULT_OUTPUT_POLICY);

                            for(int k =0; k < numTimesToRun; k++) {
                                Integer addResult = (Integer) basicCalServices.runFunction(spec, new Integer[] {j, j/2});

                                if (addResult.intValue() != j*2 + j + j/2 ) {
                                    throw new Exception("Result incorrect: expected " + (j*2 + j + j/2) + ", but got " + addResult);
                                }
                            }

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
        help_testMultithreadRunFunction(20, 15, 200, leccCALServices);
    }

    /**
     * Tests multithreaded module compilation using the G machine.
     * @throws InterruptedException if the testing thread is interrupted
     */
    public void testMultithreadedModuleCompilationWithGMachine() throws InterruptedException {
        help_testMultithreadRunFunction(15, 15, 20, gCALServices);
    }
}
