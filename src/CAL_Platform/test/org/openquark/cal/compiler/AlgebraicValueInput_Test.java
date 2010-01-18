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
 * AlgebraicValueInput_Test.java
 * Creation date: (Dec 21, 2005)
 * By: James Wright
 */
package org.openquark.cal.compiler;

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


/**
 * Tests of the automatically-generated functions for inputting Java values
 * that are stored in an AlgebraicValue object.
 * 
 * These tests tend to focus on the failure cases where we want to check that
 * the generated functions raise an error on invalid input (such as when the
 * number of arguments isn't right for the data constructor that they specify).
 *  
 * @author James Wright
 */
public class AlgebraicValueInput_Test extends TestCase {
    
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;

    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(AlgebraicValueInput_Test.class);

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
     * @param name the name of the test
     */
    public AlgebraicValueInput_Test(String name) {
        super(name);
    }

    /**
     * Test that attempting to input an AlgebraicValue object that is malformed due to
     * having a datacons name that does not match the datacons index causes an error
     * in derived Inputable instances.
     * @throws GemCompilationException
     */
    public void testMismatchedNameAlgebraicValueInput() throws GemCompilationException {
        try {
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.Prelude_Tests, "testMismatchedNameAlgebraicValueInput"), leccCALServices);
            fail("testMismatchedNameAlgebraicValueInput should have raised an error");

        } catch (CALExecutorException e) {
            assertTrue(e.getErrorInfo() != null);
            assertTrue(e.getErrorInfo().getTopLevelFunctionName().equals(QualifiedName.make(CALPlatformTestModuleNames.Prelude_Tests, "$input$MyAlgebraicType")));
        }
    }

    /**
     * Test that attempting to input an AlgebraicValue object that is malformed due to
     * having too few datacons arguments causes an error.
     * in derived Inputable instances.
     * @throws GemCompilationException
     */
    public void testTooFewArgumentsAlgebraicValueInput() throws GemCompilationException {
        try {
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.Prelude_Tests, "testTooFewArgumentsAlgebraicValueInput"), leccCALServices);
            fail(CALPlatformTestModuleNames.Prelude_Tests + ".$input$MyAlgebraicType should have raised an error");
        } catch (CALExecutorException e) {
            assertTrue(e.getErrorInfo() != null);
            assertTrue(e.getErrorInfo().getTopLevelFunctionName().equals(QualifiedName.make(CALPlatformTestModuleNames.Prelude_Tests, "$input$MyAlgebraicType")));
        }
    }

    /**
     * Test that attempting to input an AlgebraicValue object that is malformed due to
     * having too many datacons arguments causes an error.
     * in derived Inputable instances.
     * @throws GemCompilationException
     */
    public void testTooManyArgumentsAlgebraicValueInput() throws GemCompilationException {
        try {
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.Prelude_Tests, "testTooManyArgumentsAlgebraicValueInput"), leccCALServices);
            fail(CALPlatformTestModuleNames.Prelude_Tests + ".$input$MyAlgebraicType should have raised an error");
        } catch (CALExecutorException e) {
            assertTrue(e.getErrorInfo() != null);
            assertTrue(e.getErrorInfo().getTopLevelFunctionName().equals(QualifiedName.make(CALPlatformTestModuleNames.Prelude_Tests, "$input$MyAlgebraicType")));
        }   
    }
}

