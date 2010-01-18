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
 * CALPlatformBasicTestSuite.java
 * Creation date: Feb 24, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.AlgebraicValueInput_Test;
import org.openquark.cal.compiler.CALCompiler_Test;
import org.openquark.cal.compiler.CALDoc_Test;
import org.openquark.cal.compiler.CALRenaming_Test;
import org.openquark.cal.compiler.CALTypeChecker_Test;
import org.openquark.cal.compiler.DataConsFieldNameRenamer_Test;
import org.openquark.cal.compiler.DataConstructorCode_Test;
import org.openquark.cal.compiler.Deprecation_Test;
import org.openquark.cal.compiler.ImportCleaner_Test;
import org.openquark.cal.compiler.InputOutputPolicy_Test;
import org.openquark.cal.compiler.LanguageInfo_Test;
import org.openquark.cal.compiler.LocalNameRenamer_Test;
import org.openquark.cal.compiler.LocalPatternMatch_Test;
import org.openquark.cal.compiler.MessageKind_Test;
import org.openquark.cal.compiler.ModuleNameResolver_Test;
import org.openquark.cal.compiler.Optimizer_Test;
import org.openquark.cal.compiler.SourceMetricFinder_Test;
import org.openquark.cal.compiler.SourceModelCodeFormatter_Test;
import org.openquark.cal.compiler.SourceModelUtilities_Test;
import org.openquark.cal.compiler.SourcePosition_Test;
import org.openquark.cal.compiler.StringEncoder_Test;
import org.openquark.cal.compiler.TypeDeclarationInserter_Test;
import org.openquark.cal.compiler.TypeExpr_Test;
import org.openquark.cal.compiler.TypeVariableRenamer_Test;
import org.openquark.cal.internal.machine.lecc.CALClassLoader_Test;
import org.openquark.cal.internal.machine.lecc.RTSupercombinator_Test;
import org.openquark.cal.internal.machine.lecc.functions.RuntimeStringConstantsTest;
import org.openquark.cal.machine.ExecutionContextProperties_Test;
import org.openquark.cal.machine.ExecutionContext_Test;
import org.openquark.cal.services.BasicCALServices_Test;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.EntryPointCache_Test;
import org.openquark.cal.services.IdentifierUtils_Test;
import org.openquark.cal.services.WorkspaceManager_Test;
import org.openquark.cal.util.FixedSizeList_Test;


/**
 * A JUnit test suite that includes all of the "fast" tests to be performed on
 * the classes in the CAL project. This test suite is part of the cross-project
 * test suite that should be run before checking in code. Failures in this test
 * suite should be considered critical. By "fast", we mean that the tests in
 * this suite should be kept relatively small ( <5 minutes?) so that developers
 * can reasonably run the test on a regular basis and before every check-in.
 * 
 * @author Joseph Wong
 */
public class CALPlatformBasicTestSuite extends TestSuite {

    /**
     * Constructor for CALBasicTestSuite
     */
    private CALPlatformBasicTestSuite() {
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
        
        // Make sure that the optimizer is working correctly.  
        suite.addTest(Optimizer_Test.suite());
        
        // machine package
        suite.addTest(CALClassLoader_Test.suite());
        suite.addTest(ExecutionContextProperties_Test.suite());
        suite.addTest(ExecutionContext_Test.suite());
        suite.addTest(RTSupercombinator_Test.suite());
        suite.addTestSuite(RuntimeStringConstantsTest.class);
        
        // compiler package
        suite.addTest(CALCompiler_Test.suite());
        suite.addTest(ModuleNameResolver_Test.suite());
        suite.addTest(Deprecation_Test.suite());
        suite.addTest(LocalPatternMatch_Test.suite());
        suite.addTest(CALTypeChecker_Test.suite());
        suite.addTest(TypeExpr_Test.suite());
        suite.addTestSuite(LanguageInfo_Test.class);
        suite.addTestSuite(IdentifierUtils_Test.class);
        suite.addTestSuite(StringEncoder_Test.class);
        suite.addTestSuite(SourceModelUtilities_Test.class);        
        suite.addTestSuite(MessageKind_Test.class);
        suite.addTest(LocalNameRenamer_Test.suite());
        suite.addTest(TypeVariableRenamer_Test.suite());
        suite.addTest(DataConsFieldNameRenamer_Test.suite());
        suite.addTest(CALRenaming_Test.suite());
        suite.addTest(SourceMetricFinder_Test.suite());
        suite.addTest(AlgebraicValueInput_Test.suite());
        suite.addTest(DataConstructorCode_Test.suite());
        suite.addTest(CALDoc_Test.suite());
        suite.addTest(ImportCleaner_Test.suite());
        suite.addTest(TypeDeclarationInserter_Test.suite());
        suite.addTestSuite(SourcePosition_Test.class); 
        suite.addTest(SourceModelCodeFormatter_Test.suite());
        suite.addTestSuite(EntryPointCache_Test.class); 
        
        // services package
        suite.addTest(BasicCALServices_Test.suite());
        //suite.addTestSuite(EntryPointCache_Test.class);
        suite.addTest(WorkspaceManager_Test.suite());
        
        // foreignsupport package
        suite.addTestSuite(FixedSizeList_Test.class);
        

       
        //Test input output policies
        suite.addTest(InputOutputPolicy_Test.suite());

        //run all of the cal tests.
        suite.addTest(CALPlatorm_Test.suite());
        
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
               
        suite.addTestSuite(CALPlatformWorkspaceValidity_Test.class);
        
        // In the case of CAL Platform, the binding class test uses cal.platform.bindings.cws, which is
        // not shared by any other test.
        suite.addTest(JavaBindingClassTest.suite());
        
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
