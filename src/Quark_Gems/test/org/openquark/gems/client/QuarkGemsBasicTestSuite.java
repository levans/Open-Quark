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
 * QuarkGemsBasicTestSuite.java
 * Creation date: Feb 23, 2005.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openquark.cal.services.CALServicesTestUtilities;

/**
 * A JUnit test suite that includes all of the "fast" tests to be performed on
 * the classes in the Quark Gems project. This test suite is part of the cross-project
 * test suite that should be run before checking in code. Failures in this test
 * suite should be considered critical. By "fast", we mean that the tests in
 * this suite should be kept relatively small ( <5 minutes?) so that developers
 * can reasonably run the test on a regular basis and before every check-in.
 * 
 * @author Joseph Wong
 */
public class QuarkGemsBasicTestSuite extends TestSuite {

    /**
     * Constructor for QuarkGemsBasicTestSuite
     */
    private QuarkGemsBasicTestSuite() {
        super();
    }

    /**
     * Constructs a test suite which contains all of the fast Quark_Gems tests.
     * Any new fast tests created for the Quark_Gems project should have
     * corresponding entries added in this method.
     * 
     * @return A test suite containing all of the Quark_Gems tests.
     */
    public static Test suite() {
        // Create a new test suite.
        TestSuite suite = new TestSuite();
        
        suite.addTest(servicesSharingSuite());
        suite.addTest(nonSharingSuite());
        
        // ****************************************************************************
        //
        // DO NOT ADD MORE TESTS HERE....
        //
        // Add test cases to one of servicesSharingSuite or nonSharingSuite
        // depending on whether or not they can use a shared BasicCALServices or not
        //
        // ****************************************************************************
        
        // Return the completed suite
        return suite;
    }

    private static Test servicesSharingSuite() {
        // Create a new test suite.
        TestSuite suite = new TestSuite();

        // Add each test case/suite that tests classes of the Quark_Gems project
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
        
        suite.addTest(Autoburn_Test.suite());
        suite.addTest(CALSourceGenerator_Test.suite());
        
        // Return the completed suite
        return new TestSetup(suite) {
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
    }
    
    private static Test nonSharingSuite() {
        // Create a new test suite.
        TestSuite suite = new TestSuite();
        
        suite.addTestSuite(QuarkGemsWorkspaceValidity_Test.class);

        // Return the completed suite
        return new TestSetup(suite) {
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
    }
}