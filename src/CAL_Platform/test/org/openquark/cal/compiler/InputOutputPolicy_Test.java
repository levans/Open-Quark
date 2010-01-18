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
 * InputOutputPolicy_Test.java
 * Creation date: Dec 18, 2006.
 * By: Magnus Byne
 */
package org.openquark.cal.compiler;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;


public class InputOutputPolicy_Test  extends TestCase {
    
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices calServices;
    private static ExecutionContext executionContext; 
    private static CALExecutor executor;
    private static Compiler compiler;

    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        calServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
        
        executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();
        executor = calServices.getWorkspaceManager().makeExecutor(executionContext);
        compiler = calServices.getCompiler();
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        calServices = null;
        executionContext =null;
        executor = null;
        compiler = null;
    }
       
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(InputOutputPolicy_Test.class);

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
    public InputOutputPolicy_Test(String name) {
        super(name);
    }
    
    /**
     * Test running a polymorphic function with one typed input, which
     * is sufficient to constrain the other types.
     * @throws CALExecutorException
     */
    public void testTypedInput_Test() throws CALExecutorException {
        //policies can be used there.
        CompilerMessageLogger messageLogger = new MessageLogger();
        EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.INT_INPUT_POLICY,
            InputPolicy.DEFAULT_INPUT_POLICY
        }, OutputPolicy.DEFAULT_OUTPUT_POLICY);
        
        EntryPoint add = compiler.getEntryPoint(addSpec, CAL_Prelude.MODULE_NAME, messageLogger);
        assertNotNull(add);
        Object addResult = executor.exec(add, new Object[] { new Integer(1), new Integer(2) });
        assertTrue(addResult instanceof Integer);
        assertEquals(new Integer(1 + 2), addResult);
    }
    
    /**
     * Test running a polymorphic function with typed output,
     * which is sufficient to constrain the inputs.
     * @throws CALExecutorException
     */
    public void testTypedOutput_Test() throws CALExecutorException {
        //policies can be used there.
        CompilerMessageLogger messageLogger = new MessageLogger();
        EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.DEFAULT_INPUT_POLICY,
            InputPolicy.DEFAULT_INPUT_POLICY
        }, OutputPolicy.INT_OUTPUT_POLICY);
        
        EntryPoint add = compiler.getEntryPoint(addSpec, CAL_Prelude.MODULE_NAME, messageLogger);
        assertNotNull(add);
        Object addResult = executor.exec(add, new Object[] { new Integer(1), new Integer(2) });
        assertTrue(addResult instanceof Integer);
        assertEquals(new Integer(1 + 2), addResult);
    }
    
    /**
     * Test an error is returned when attempting to create entry point for
     * a polymorphic function without supplying the type
     */
    public void testAmbiguousError_Test() {
        //policies can be used there.
        CompilerMessageLogger messageLogger = new MessageLogger();
        EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.DEFAULT_INPUT_POLICY,
            InputPolicy.DEFAULT_INPUT_POLICY
        }, OutputPolicy.DEFAULT_OUTPUT_POLICY);
        
        EntryPoint add = compiler.getEntryPoint(addSpec, CAL_Prelude.MODULE_NAME, messageLogger);

        assertNull(add); //the entry point should not be created as the output type is ambiguous
        assert(messageLogger.getNMessages() > 0);
        
        CompilerMessage error = messageLogger.getFirstError();
        assertEquals(MessageKind.Error.AmbiguousTypeSignatureInInferredType.class, error.getMessageKind().getClass());

    }
    
    /**
     * Test an error is returned when attempting to create entry point
     * with un-unifiable types.
     */
    public void testTypeIncompatabilityError_Test() {
        //policies can be used there.
        CompilerMessageLogger messageLogger = new MessageLogger();
        EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.DOUBLE_INPUT_POLICY,
            InputPolicy.DOUBLE_INPUT_POLICY
        }, OutputPolicy.INT_OUTPUT_POLICY);
        
        EntryPoint add = compiler.getEntryPoint(addSpec, CAL_Prelude.MODULE_NAME, messageLogger);

        assertNull(add); //the entry point should not be created as the input/output types do not unify
        assert(messageLogger.getNMessages() > 0);
        
        CompilerMessage error = messageLogger.getFirstError();
        assertEquals(MessageKind.Error.TypeErrorDuringApplication.class, error.getMessageKind().getClass());
        

    }
    
    
    /**
     * Test output and input of typed CAL values
     */
    public void testCALInputOutput_Test() throws CALExecutorException {
        //policies can be used there.
        CompilerMessageLogger messageLogger = new MessageLogger();
        EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.DEFAULT_INPUT_POLICY,
            InputPolicy.DEFAULT_INPUT_POLICY
        }, OutputPolicy.makeTypedCalValueOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int)));
        
        EntryPoint add = compiler.getEntryPoint(addSpec, CAL_Prelude.MODULE_NAME, messageLogger);

        //this should output 3 as a CAL internal value
        Object addResult = executor.exec(add, new Object[] { new Integer(1), new Integer(2) });
        
        EntryPointSpec addSpec2 = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.makeTypedCalValueInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int)),
            InputPolicy.INT_INPUT_POLICY
        }, OutputPolicy.INT_OUTPUT_POLICY);      

        EntryPoint add2 = compiler.getEntryPoint(addSpec2, CAL_Prelude.MODULE_NAME, messageLogger);

        //this should output 3 as a CAL internal value
        Object add2Result = executor.exec(add2, new Object[] { addResult, new Integer(2) });
       
        
        assertTrue(add2Result instanceof Integer);
        assertEquals(new Integer( (1 + 2) + 2), add2Result);
    }

    /**
     * Test output and input of untyped CAL values
     */
    public void testUnTypedCALInputOutput_Test() throws CALExecutorException {
        //policies can be used there.
        CompilerMessageLogger messageLogger = new MessageLogger();
        EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.INT_INPUT_POLICY,
            InputPolicy.DEFAULT_INPUT_POLICY
        }, OutputPolicy.CAL_VALUE_OUTPUT_POLICY);
        
        EntryPoint add = compiler.getEntryPoint(addSpec, CAL_Prelude.MODULE_NAME, messageLogger);

        //this should output 3 as a CAL internal value
        Object addResult = executor.exec(add, new Object[] { new Integer(1), new Integer(2) });
        
        EntryPointSpec addSpec2 = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
            InputPolicy.CAL_VALUE_INPUT_POLICY,
            InputPolicy.DEFAULT_INPUT_POLICY
        }, OutputPolicy.INT_OUTPUT_POLICY);      

        EntryPoint add2 = compiler.getEntryPoint(addSpec2, CAL_Prelude.MODULE_NAME, messageLogger);

        //this should add 2 to the CAL internal value representing 3
        Object add2Result = executor.exec(add2, new Object[] { addResult, new Integer(2) });
       
        
        assertTrue(add2Result instanceof Integer);
        assertEquals(new Integer( (1 + 2) + 2), add2Result);
    }


    
}
