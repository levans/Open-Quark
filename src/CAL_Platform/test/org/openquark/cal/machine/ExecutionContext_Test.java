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
 * ExecutionContext_Test.java
 * Creation date: Jun 19, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.machine;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.module.Cal.Core.CAL_System;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.Cleanable;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;


/**
 * A set of JUnit test cases for the ExecutionContext class from the machine package.
 *
 * @author Joseph Wong
 */
public class ExecutionContext_Test extends TestCase {

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
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

        TestSuite suite = new TestSuite(ExecutionContext_Test.class);

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
        leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
        gCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.G, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        gCALServices = null;
    }

    /**
     * Constructor for ExecutionContext_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public ExecutionContext_Test(String name) {
        super(name);
    }
    
    /**
     * Tests the cleanup hook suppoprt.
     */
    public void testCleanupHook() throws CALExecutorException {
        
        help_testCleanupHook(leccCALServices);
        help_testCleanupHook(gCALServices);
    }
    
    /**
     * A test Cleanable instance.
     */
    private static final class TestCleanable implements Cleanable {
        
        boolean cleanupCalled = false;
        /**
         * {@inheritDoc}
         */
        public void cleanup() {
            cleanupCalled = true;
        }
    }
    
    /**
     * Helper method for testCleanupHook.
     * @param calServices the BasicCALServices to test with.
     * @throws CALExecutorException
     */
    private void help_testCleanupHook(BasicCALServices calServices) throws CALExecutorException {
        
        TestCleanable hook = new TestCleanable();
        
        ExecutionContextProperties.Builder propBuilder = new ExecutionContextProperties.Builder();
        propBuilder.setProperty("hook", hook);
        ExecutionContextProperties properties = propBuilder.toProperties();
        ExecutionContext executionContext = calServices.getWorkspaceManager().makeExecutionContext(properties);
        
        EntryPoint entryPoint = calServices.getCompiler().getEntryPoint(
            new AdjunctSource.FromText("private tempFunc = System.registerCleanable ((input (System.getProperty \"hook\")) :: Cleanable);"), 
            EntryPointSpec.make(QualifiedName.make(CAL_System.MODULE_NAME, "tempFunc")), 
            CAL_System.MODULE_NAME, 
            new MessageLogger());
        
        CALExecutor executor = calServices.getWorkspaceManager().makeExecutor(executionContext);
        
        executor.exec(entryPoint, new Object[0]);
        calServices.getWorkspaceManager().resetCachedResults(executor.getContext());
        
        assertTrue(calServices.getWorkspaceManager().getMachineType() + " machine: The cleanup hook should have been called, but was not.", hook.cleanupCalled);
    }
}
