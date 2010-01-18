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
 * CalPlatformIntermediateTestSuite.java
 * Created: Mar 23, 2007
 * By: mbyne
 */

package org.openquark.cal;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openquark.cal.caldoc.CALDocTool_Test;
import org.openquark.cal.compiler.DerivedInstanceFunctionGenerator_Test;
import org.openquark.cal.compiler.ErrorMessage_Test;
import org.openquark.cal.compiler.SourceModel_Test;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder_Test;
import org.openquark.cal.services.CALServicesTestUtilities;

public class CalPlatformIntermediateTestSuite  extends TestSuite {

    /**
     * Constructor for CALBasicTestSuite
     */
    private CalPlatformIntermediateTestSuite() {
        super();
    }
    
    /**
     * Constructs a test suite which contains all of the fast CAL tests.
     * Any new fast tests created for the CAL project should have
     * corresponding entries added in this method.
     * 
     * @return A test suite containing all of the CAL tests.
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
        
        suite.addTest(StandaloneJarBuilder_Test.suite());
        
        // Test that the correct information source position information is
        // put in the error message string.
        suite.addTest(ErrorMessage_Test.suite());

        // test suites containing "dynamic tests", i.e. tests that execute based
        // upon data contained in external resources, e.g. in the CAL folder
        suite.addTest(SourceModel_Test.suite());       
        
        
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
        
        // compiler package
        suite.addTestSuite(DerivedInstanceFunctionGenerator_Test.class);
        
        // caldoc package
        // the CALDocTool test uses cal.platform.cws, which is not shared.
        suite.addTest(CALDocTool_Test.suite());
        
        // run all of the cal platform tests with the G machine.
        suite.addTest(CALPlatform_G_Test.suite());

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
