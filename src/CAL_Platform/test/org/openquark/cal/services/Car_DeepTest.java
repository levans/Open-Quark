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
 * Car_DeepTest.java
 * Creation date: Feb 3, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.internal.machine.lecc.LECCMachineTestUtilities;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.MachineType;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.Pair;


/**
 * A set of JUnit test cases for testing the support for Car (CAL Archive) files.
 *
 * @author Joseph Wong
 */
public class Car_DeepTest extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices sourceLeccCALServices;
    
    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = true;
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {
        
        TestSuite suite = new TestSuite(Car_DeepTest.class);
        
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
        sourceLeccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        sourceLeccCALServices = null;
    }
    
    /**
     * Constructor for Car_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public Car_DeepTest(String name) {
        super(name);
    }
    
    /**
     * Tests the building of a Car (with -keepsource option) and then loading it back into another workspace.
     */
    public void testKeepSourceRoundTripCarBuildingAndLoading() throws IOException, CALExecutorException {
        Car_Test.help_testRoundTripCarBuildingAndLoading(false, sourceLeccCALServices);
    }

    /**
     * Tests the building of a Car and then loading it back into another workspace on multiple threads.
     */
    public void testRoundTripCarBuildingAndMultithreadedLoading() throws IOException, CALExecutorException {
        // This test does not work when the lecc machine is in Java source mode, so just skip it.
        if (LECCMachineTestUtilities.isJavaSourceConfig()) {
            return;
        }
        
        // We build a Car for the minimal workspace containing Prelude_Tests
        CALWorkspace.DependencyFinder depFinder = sourceLeccCALServices.getCALWorkspace().getDependencyFinder(Collections.singleton(CALPlatformTestModuleNames.Prelude_Tests));
        
        Set<ModuleName> moduleNames = new HashSet<ModuleName>();
        moduleNames.addAll(depFinder.getRootSet());
        moduleNames.addAll(depFinder.getImportedModulesSet());
        
        CarBuilder.Configuration config = new CarBuilder.Configuration(sourceLeccCALServices.getWorkspaceManager(), moduleNames.toArray(new ModuleName[moduleNames.size()]));
        
        File carFile = new File(System.getProperty("java.io.tmpdir"), "Car_DeepTest__testRoundTripCarBuildingAndMultithreadedLoading.car");
        if (carFile.exists()) {
            boolean deleted = carFile.delete();
            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                System.err.println("Previous Car file not deleted: " + carFile);
            }
        }
        
        try {
            CarBuilder.buildCar(config, carFile.getName(), carFile);
            help_testMultithreadedLoading(carFile, 5);
            
        } finally {
            
            for (int i = 0; i < 2; i++) {
                System.gc();
                System.runFinalization();
                System.gc();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ie) {
                    if (SHOW_DEBUGGING_OUTPUT) {
                        ie.printStackTrace(System.err);
                    }
                }
            }
            
            boolean deleted = carFile.delete();
            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                System.err.println("Car file not deleted: " + carFile);
            }
        }
    }

    /**
     * Helper method for launching multiple threads to load the same Car file.
     * @throws CALExecutorException
     */
    private void help_testMultithreadedLoading(final File carFile, int numThreads) throws CALExecutorException {
        Thread[] threads = new Thread[numThreads];        

        final List<CALExecutorException> calExecutorExceptionFromThreads = new ArrayList<CALExecutorException>();
        final List<AssertionFailedError> assertionFailedErrorFromThreads = new ArrayList<AssertionFailedError>();
        
        for (int i = 0; i < numThreads; i++) {
            final int threadID = i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        Pair<WorkspaceManager, File> workspaceManagerRootPair = Car_Test.help_buildDiscreteWorkspaceFromCar(carFile, "Car_DeepTest__testRoundTripCarBuildingAndMultithreadedLoading_Thread" + threadID);
                        
                        try {
                            WorkspaceManager workspaceManager = workspaceManagerRootPair.fst();
                            Car_Test.help_testWithFunction(workspaceManager, CALPlatformTestModuleNames.Prelude_Tests, "testModule");
                            
                            // on the first thread, launch more threads to run parallel compilations on this workspace
                            if (threadID == 0) {
                                MultiThreadedExecution_DeepTest.help_testMultithreadCompilation(20, 20, 5, 1, workspaceManager);
                            }
                        } finally {
                            File rootDir = workspaceManagerRootPair.snd();
                            
                            boolean deleted = FileSystemHelper.delTree(rootDir);
                            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                                System.err.println("Workspace directory not deleted: " + rootDir);
                            }
                        }
                        
                    } catch (InterruptedException e) {
                        if (SHOW_DEBUGGING_OUTPUT) {
                            e.printStackTrace(System.err);
                        }
                    } catch (AssertionFailedError e) {
                        assertionFailedErrorFromThreads.add(e);
                    } catch (CALExecutorException e) {
                        calExecutorExceptionFromThreads.add(e);
                    }
                }
            };
            threads[i].start();
        }
        
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                if (SHOW_DEBUGGING_OUTPUT) {
                    e.printStackTrace(System.err);
                }
            }
        }
        
        for (int i = 0, n = assertionFailedErrorFromThreads.size(); i < n; i++) {
            throw assertionFailedErrorFromThreads.get(i);
        }
        
        for (int i = 0, n = calExecutorExceptionFromThreads.size(); i < n; i++) {
            throw calExecutorExceptionFromThreads.get(i);
        }
    }
}
