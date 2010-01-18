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
 * CALDeepTestSuite.java
 * Creation date: Feb 24, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.CALCompiler_DeepTest;
import org.openquark.cal.compiler.CALRenaming_DeepTest;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.Car_DeepTest;
import org.openquark.cal.services.Car_Test;
import org.openquark.cal.services.MultiThreadedBasicCalServices_DeepTest;
import org.openquark.cal.services.MultiThreadedExecution_DeepTest;
import org.openquark.cal.services.WorkspaceManager_DeepTest;


/**
 * A JUnit test suite that will run all automatable unit tests for the CAL
 * project not considered to be part of the basic (a.k.a. sanity) test suite.
 * This test suite is meant to include all time-consuming tests, and should be
 * run after the build is run.
 * 
 * @author Joseph Wong
 */
public class CALDeepTestSuite extends TestSuite {

    /**
     * Constructor for CALDeepTestSuite
     */
    private CALDeepTestSuite() {
        super();
    }

    /**
     * Constructs a test suite which contains all of the time-consuming CAL
     * tests. Any new time-consuming tests created for the CAL project should
     * have corresponding entries added in this method.
     * 
     * @return A test suite containing all of the CAL tests.
     */
    public static Test suite() {
        return suiteUsingWorkspaceDeclaration("ice.default.cws");
    }
    
    /**
     * @return a test suite containing all the test cases using the specified workspace declaration.
     */
    public static Test suiteUsingWorkspaceDeclaration(final String workspaceDeclFile) {
        // Create a new test suite.
        TestSuite suite = new TestSuite();

        // Add each test case/suite that tests classes of the CAL project
        //
        // For test case classes that do not define a suite() method,
        // use the standard idiom:
        //
        //    suite.addTestSuite(SomeClass_Test.class);
        //
        // to add all of the class's testXXX methods into the suite.
        //
        // Meanwhile, for any test class that defines a suite() method,
        // call that to obtain a custom test suite, i.e.:
        //
        //    suite.addTest(SomeClass_Test.suite());
        //
        // instead of using the standard idiom so that any custom setup
        // needed by the test class would be preserved
        

        //*** For tests that can make use of a shared BasicCALServices instance
        {
            TestSuite servicesSharingSuite = new TestSuite();

            servicesSharingSuite.addTest(Car_Test.suite());
            servicesSharingSuite.addTest(Car_DeepTest.suite());
            
            TestSetup servicesSharingSetup = new TestSetup(servicesSharingSuite) {
                /**
                 * Perform setup for the entire suite.
                 */
                @Override
                protected void setUp() {
                    // nothing to do
                }
        
                /**
                 * Perform tear down for the entire suite.
                 */
                @Override
                protected void tearDown() {
                    CALServicesTestUtilities.flushCommonCALServicesCache();
                }            
            };
            
            suite.addTest(servicesSharingSetup);
        }
        
        //*** For tests that can *not* make use of a shared BasicCALServices instance
        {
            TestSuite nonSharingSuite = new TestSuite();
            
            nonSharingSuite.addTest(MultiThreadedExecution_DeepTest.suite());
            nonSharingSuite.addTest(CALCompiler_DeepTest.suite());
            nonSharingSuite.addTest(WorkspaceManager_DeepTest.suiteUsingWorkspaceDeclaration(workspaceDeclFile));
            nonSharingSuite.addTestSuite(CALRenaming_DeepTest.class);
            nonSharingSuite.addTest(MultiThreadedBasicCalServices_DeepTest.suite());
            
            suite.addTest(nonSharingSuite);
        }

        // Return the completed suite
        return suite;
    }
}
