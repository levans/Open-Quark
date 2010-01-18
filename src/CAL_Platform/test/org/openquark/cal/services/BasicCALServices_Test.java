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
 * BasicCALServices_Test.java
 * Creation date: Feb 24, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.CompilerTestUtilities;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelModuleSource;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.internal.machine.EntryPointImpl;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.module.Cal.Collections.CAL_Array;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.MachineType;


/**
 * A set of JUnit test cases for the BasicCALServices class from the services package.
 *
 * @author Joseph Wong
 */
public class BasicCALServices_Test extends TestCase {
    
    private static final String Prelude_Num = CAL_Prelude.TypeClasses.Num.getQualifiedName();
    
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

        TestSuite suite = new TestSuite(BasicCALServices_Test.class);

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
     * Constructor for BasicCALServices_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public BasicCALServices_Test(String name) {
        super(name);
    }
    
    /**
     * Tests the finding of gems with a particular type through the method
     * BasicCALServices.findGemsOfType().
     */
    public void testFindGemsOfType() {
        help_testFindGemsOfType(leccCALServices);
        help_testFindGemsOfType(gCALServices);
    }
    
    private void help_testFindGemsOfType(BasicCALServices calServices) {
        Collection<GemEntity> gems = calServices.findGemsOfType(CALServicesTestUtilities.DEFAULT_UNIT_TEST_MODULE, Prelude_Num + " a => a -> a -> a", true);
        
        Iterator<GemEntity> it = gems.iterator();
        
        boolean hasSubtract = false;
        String lastGemQualifiedName = "";
        
        while (it.hasNext()) {
            GemEntity gem = it.next();
            if (gem.getName().equals(CAL_Prelude.Functions.subtract)) {
                hasSubtract = true;
            }
            String gemQualifiedName = gem.getName().getQualifiedName(); 
            assertTrue(gemQualifiedName.compareToIgnoreCase(lastGemQualifiedName) >= 0);
            lastGemQualifiedName = gemQualifiedName;
        }
        
        assertTrue(hasSubtract);
    }
    
    /**
     * Tests the finding of gems with a particular return type through the method
     * BasicCALServices.findGemsByReturnType().
     */
    public void testFindGemsByReturnType() {
        help_testFindGemsByReturnType(leccCALServices);
        help_testFindGemsByReturnType(gCALServices);
    }

    private void help_testFindGemsByReturnType(BasicCALServices calServices) {
        Collection<GemEntity> gems = calServices.findGemsByReturnType(CALServicesTestUtilities.DEFAULT_UNIT_TEST_MODULE, Prelude_Num + " a => a", true);
        
        Iterator<GemEntity> it = gems.iterator();
        
        boolean hasSubtract = false;
        String lastGemQualifiedName = "";
        
        while (it.hasNext()) {
            GemEntity gem = it.next();
            if (gem.getName().equals(CAL_Prelude.Functions.subtract)) {
                hasSubtract = true;
            }
            String gemQualifiedName = gem.getName().getQualifiedName(); 
            assertTrue(gemQualifiedName.compareToIgnoreCase(lastGemQualifiedName) >= 0);
            lastGemQualifiedName = gemQualifiedName;
        }
        
        assertTrue(hasSubtract);        
    }

    /**
     * Runs the expression (1 :: Int) + (2 :: Int), and compares the result
     * of BasicCALServices' runCode with the expected result, namely the
     * java.lang.Integer representation of 3.
     */
    public void testRunCode_IntReturnType() throws GemCompilationException, CALExecutorException {
        help_testRunCode_IntReturnType(leccCALServices);
        help_testRunCode_IntReturnType(gCALServices);
    }
        
    private void help_testRunCode_IntReturnType(BasicCALServices calServices) throws GemCompilationException, CALExecutorException {
        assertEquals(new Integer(3),
            CALServicesTestUtilities.runExpr(
                "IntReturnType",
                SourceModel.Expr.BinaryOp.Add.make(
                    SourceModel.Expr.makeIntValue(1), SourceModel.Expr.makeIntValue(2)),
                calServices));
    }

    /**
     * Runs the expression 1.0 + 2.0, and compares the result
     * of BasicCALServices' runCode with the expected result, namely the
     * java.lang.Double representation of 3.0.
     */
    public void testRunCode_DoubleReturnType() throws GemCompilationException, CALExecutorException {
        help_testRunCode_DoubleReturnType(leccCALServices);
        help_testRunCode_DoubleReturnType(gCALServices);
    }
        
    private void help_testRunCode_DoubleReturnType(BasicCALServices calServices) throws GemCompilationException, CALExecutorException {
        assertEquals(new Double(3.0),
            CALServicesTestUtilities.runExpr(
                "DoubleReturnType",
                SourceModel.Expr.BinaryOp.Add.make(
                    SourceModel.Expr.makeDoubleValue(1.0), SourceModel.Expr.makeDoubleValue(2.0)),
                calServices));
    }

    /**
     * Runs the expression 'H', and compares the result
     * of BasicCALServices' runCode with the expected result, namely the
     * java.lang.Character representation of 'H'
     */
    public void testRunCode_CharReturnType() throws GemCompilationException, CALExecutorException {
        help_testRunCode_CharReturnType(leccCALServices);
        help_testRunCode_CharReturnType(gCALServices);
    }
        
    private void help_testRunCode_CharReturnType(BasicCALServices calServices) throws GemCompilationException, CALExecutorException {
        assertEquals(new Character('H'),
            CALServicesTestUtilities.runExpr(
                "CharReturnType",
                SourceModel.Expr.Literal.Char.make('H'),
                calServices));
    }

    /**
     * Runs the expression "Hello World!", and compares the result
     * of BasicCALServices' runCode with the expected result, namely the
     * java string "Hello World!"
     */
    public void testRunCode_StringReturnType() throws GemCompilationException, CALExecutorException {
        help_testRunCode_StringReturnType(leccCALServices);
        help_testRunCode_StringReturnType(gCALServices);
    }
        
    private void help_testRunCode_StringReturnType(BasicCALServices calServices) throws GemCompilationException, CALExecutorException {
        assertEquals("Hello World!",
            CALServicesTestUtilities.runExpr(
                "StringReturnType",
                SourceModel.Expr.Literal.StringLit.make("Hello World!"),
                calServices));
    }

    /**
     * Runs the expression [1 :: Int, 2 :: Int], and compares the result
     * of BasicCALServices' runCode with the expected result, namely a
     * java.util.List containing the elements 1 and 2 represented by
     * java.lang.Integers.
     */
    public void testRunCode_ListReturnType() throws GemCompilationException, CALExecutorException {
        help_testRunCode_ListReturnType(leccCALServices);
        help_testRunCode_ListReturnType(gCALServices);
    }
        
    private void help_testRunCode_ListReturnType(BasicCALServices calServices) throws GemCompilationException, CALExecutorException {
        assertEquals(Arrays.asList(new Integer[] {new Integer(1), new Integer(2)}),
            CALServicesTestUtilities.runExpr(
                "ListReturnType",
                SourceModel.Expr.List.make(new SourceModel.Expr[] {
                    SourceModel.Expr.makeIntValue(1), SourceModel.Expr.makeIntValue(2) }),
                calServices));
    }
    
    /**
     * Runs the expression Array_Tests.multiDimArray, and compares the result
     * of ProrgamEvaluator's eval with the expected result, namely a java.util.List
     * containing the 3 java.util.Lists of boxed integers: {1,2,3}, {4,5,6}, {7,8,9}.
     */
    public void testProgramEvaluatorEval_MultiDimArray() throws CALExecutorException, GemCompilationException {
        help_testProgramEvaluatorEval_MultiDimArray(leccCALServices);
        help_testProgramEvaluatorEval_MultiDimArray(gCALServices);
    }
    
    private void help_testProgramEvaluatorEval_MultiDimArray(BasicCALServices calServices) throws CALExecutorException, GemCompilationException {
        List<?> result = (List<?>) calServices.runFunction(
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.Array_Tests, "multiDimArray")), null);
        
        List<?> expected = Arrays.asList(new Object[]{
            Arrays.asList(new Object[]{new Integer(1), new Integer(2), new Integer(3)}),
            Arrays.asList(new Object[]{new Integer(4), new Integer(5), new Integer(6)}),
            Arrays.asList(new Object[]{new Integer(7), new Integer(8), new Integer(9)})});
        
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);      
    }

    /**
     * Runs the expression Array.outputPrimitive (Array.subscript Array_Tests.multiDimArray 0),
     * and compares the result of ProrgamEvaluator's eval with the expected result, namely an int[]
     * containing {1,2,3}.
     */
    public void testProgramEvaluatorEval_OutputPrimtiveArray() throws CALExecutorException {
        help_testProgramEvaluatorEval_OutputPrimitiveArray(leccCALServices);
        help_testProgramEvaluatorEval_OutputPrimitiveArray(gCALServices);
    }
    
    private void help_testProgramEvaluatorEval_OutputPrimitiveArray(BasicCALServices calServices) throws CALExecutorException {
        String functionName = "tempFunc";
        
        CompilerMessageLogger logger = new MessageLogger();
        
        EntryPoint entryPoint = calServices.getCompiler().getEntryPoint(
            new AdjunctSource.FromSourceModel(SourceModel.FunctionDefn.Algebraic.make(
            functionName, Scope.PRIVATE, null,
            SourceModel.Expr.makeGemCall(CAL_Array.Functions.outputPrimitive,
                SourceModel.Expr.makeGemCall(CAL_Array.Functions.subscript,
                    SourceModel.Expr.makeGemCall(QualifiedName.make(CALPlatformTestModuleNames.Array_Tests, "multiDimArray")),
                    SourceModel.Expr.makeIntValue(0))))),
        EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.Array_Tests, functionName)), CALPlatformTestModuleNames.Array_Tests, logger);
        CALExecutor executor = calServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();
          

        int[] result = (int[]) executor.exec(entryPoint, new Object[0]);
        int[] expected = new int[]{1,2,3};
        
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }
    }

    /**
     * Runs the expression (Array.subscript Array_Tests.multiDimArray 0),
     * and compares the result of ProrgamEvaluator's eval with the expected result, namely a java.util.List
     * containing the boxed integers {1,2,3}.
     */
    public void testProgramEvaluatorEval_OutputNonPrimtiveArray() throws CALExecutorException {
        help_testProgramEvaluatorEval_OutputNonPrimitiveArray(leccCALServices);
        help_testProgramEvaluatorEval_OutputNonPrimitiveArray(gCALServices);
    }
    
    private void help_testProgramEvaluatorEval_OutputNonPrimitiveArray(BasicCALServices calServices) throws CALExecutorException {
        String functionName = "tempFunc";
                
        CompilerMessageLogger logger = new MessageLogger();
        EntryPoint entryPoint = calServices.getCompiler().getEntryPoint(
            new AdjunctSource.FromSourceModel(
                SourceModel.FunctionDefn.Algebraic.make(
                    functionName, Scope.PRIVATE, null,
                    SourceModel.Expr.makeGemCall(CAL_Array.Functions.subscript,
                        SourceModel.Expr.makeGemCall(QualifiedName.make(CALPlatformTestModuleNames.Array_Tests, "multiDimArray")),
                        SourceModel.Expr.makeIntValue(0)))), 
        EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.Array_Tests, functionName)), CALPlatformTestModuleNames.Array_Tests, logger);


        CALExecutor executor = calServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();

        List<?> result = (List<?>) executor.exec(entryPoint, new Object[0]);
        List<?> expected = Arrays.asList(new Object[]{new Integer(1), new Integer(2), new Integer(3)});
        
        assertEquals(expected.size(), result.size());        
        assertEquals(expected, result);       
    }
    
    /**
     * Tests the behavior of the compiler when a new module to be compiled specifies
     * an unknown module as an import.
     */
    public void testUnknownImport() {
        help_testUnknownImport(leccCALServices);
        help_testUnknownImport(gCALServices);
    }
    
    private void help_testUnknownImport(BasicCALServices calServices) {
        MessageLogger logger = new MessageLogger();
        SourceModelModuleSource moduleDefn = new SourceModelModuleSource(SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel("module Foo; import Prelude; import UNKNOWN_Module;", logger));
        
        assertEquals(logger.getNErrors(), 0);
        
        calServices.addNewModule(moduleDefn, logger);
        
        if (logger.getNErrors() > 0) {
            List<CompilerMessage> messages = logger.getCompilerMessages();
            for (int i = 0, n = messages.size(); i < n; i++) {
                CompilerMessage message = messages.get(i);
                if (CompilerTestUtilities.isInternalCodingError(message.getMessageKind())) {
                     fail("Unexpected: " + message);
                }
            }
        } else {
            fail("Expected error, but there were none.");
        }
    }
    
    /**
     * Tests the executor's handling of an entry point based on an enumeration data cons.
     * @throws CALExecutorException
     */
    public void testEnumDataConsEntryPoint() {
        final EntryPoint gt = new EntryPointImpl(CAL_Prelude.DataConstructors.GT);
        final CALExecutor executor = leccCALServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();
        
        try {
            executor.exec(gt, new Object[0]);
        } catch (CALExecutorException e) {
            assertTrue(e.getMessage().contains("Attempt to call getOpaqueValue() on class: org.openquark.cal.internal.runtime.lecc.RTData$CAL_Int"));
            return;
        }
        fail("exception expected");
    }
    
}
