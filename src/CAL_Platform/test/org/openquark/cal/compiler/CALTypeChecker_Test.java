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
 * CALTypeChecker_Test.java
 * Creation date: April 17, 2007.
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;


/**
 * A set of time-consuming JUnit test cases for the CAL compiler.
 *
 * @author Joseph Wong
 */
public class CALTypeChecker_Test extends TestCase {

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    
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

        TestSuite suite = new TestSuite(CALTypeChecker_Test.class);

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
        leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }
    
    /**
     * Tests the error message for a declared type of a top level function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfFunctionNotCompatibleWithInferredType_aaa() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "aaa :: Prelude.Num a => Prelude.Maybe a -> Prelude.Int;\n" + 
            "aaa maybeNum = \n" + 
            "    let\n" + 
            "        x = bbb;\n" + 
            "    in\n" + 
            "        case maybeNum of\n" + 
            "        Prelude.Nothing -> (\\x -> x + 0) 0;\n" + 
            "        Prelude.Just num -> Prelude.toInt num;\n" + 
            "        ;\n" + 
            "\n" + 
            "bbb :: Prelude.Int;\n" + 
            "bbb = \n" + 
            "    aaa (Prelude.Nothing :: Prelude.Maybe Prelude.Double);",
            MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a local function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfLocalFunctionNotCompatibleWithInferredType_aaa() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "topLevelFunction = let\n" +
            "aaa :: Prelude.Num a => Prelude.Maybe a -> Prelude.Int;\n" + 
            "aaa maybeNum = \n" + 
            "    let\n" + 
            "        x = bbb;\n" + 
            "    in\n" + 
            "        case maybeNum of\n" + 
            "        Prelude.Nothing -> (\\x -> x + 0) 0;\n" + 
            "        Prelude.Just num -> Prelude.toInt num;\n" + 
            "        ;\n" + 
            "\n" + 
            "bbb :: Prelude.Int;\n" + 
            "bbb = \n" + 
            "    aaa (Prelude.Nothing :: Prelude.Maybe Prelude.Double);\n" +
            "in ();",
            MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a top level function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfFunctionNotCompatibleWithInferredType_lt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "lt :: (Prelude.Ord a) => a -> a -> Prelude.Boolean;\n" + 
            "lt x y =\n" + 
            "    let\n" + 
            "        foo = lt7 0.0 1.0;\n" + 
            "    in\n" + 
            "        x < y;\n" + 
            "\n" + 
            "lt2 = lt;\n" + 
            "lt3 = lt2;\n" + 
            "lt4 = lt3;\n" + 
            "lt5 = lt4;\n" + 
            "lt6 = lt5;\n" + 
            "lt7 = lt6;",
            MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a local function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfLocalFunctionNotCompatibleWithInferredType_lt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "topLevelFunction = let\n" +
            "lt :: (Prelude.Ord a) => a -> a -> Prelude.Boolean;\n" + 
            "lt x y =\n" + 
            "    let\n" + 
            "        foo = lt7 0.0 1.0;\n" + 
            "    in\n" + 
            "        x < y;\n" + 
            "\n" + 
            "lt2 = lt;\n" + 
            "lt3 = lt2;\n" + 
            "lt4 = lt3;\n" + 
            "lt5 = lt4;\n" + 
            "lt6 = lt5;\n" + 
            "lt7 = lt6;\n" +
            "in ();",
            MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a top level function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfFunctionNotCompatibleWithInferredType_xlt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "xlt x y z =\n" + 
            "    let\n" + 
            "        foo = xlt7 0.0 1.0 Prelude.undefined;\n" + 
            "    in\n" + 
            "        x < y;\n" + 
            "\n" + 
            "xlt2 = xlt;\n" + 
            "xlt3 :: (Prelude.Appendable a) => Prelude.Double -> Prelude.Double -> a -> Prelude.Boolean;\n" + 
            "xlt3 = xlt2;\n" + 
            "xlt4 :: (Prelude.Num a) => Prelude.Double -> Prelude.Double -> a -> Prelude.Boolean;\n" + 
            "xlt4 = xlt3;\n" + 
            "xlt5 = xlt4;\n" + 
            "xlt6 = xlt5;\n" + 
            "xlt7 = xlt6;",
            MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a local function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfLocalFunctionNotCompatibleWithInferredType_xlt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "topLevelFunction = let\n" +
            "xlt x y z =\n" + 
            "    let\n" + 
            "        foo = xlt7 0.0 1.0 Prelude.undefined;\n" + 
            "    in\n" + 
            "        x < y;\n" + 
            "\n" + 
            "xlt2 = xlt;\n" + 
            "xlt3 :: (Prelude.Appendable a) => Prelude.Double -> Prelude.Double -> a -> Prelude.Boolean;\n" + 
            "xlt3 = xlt2;\n" + 
            "xlt4 :: (Prelude.Num a) => Prelude.Double -> Prelude.Double -> a -> Prelude.Boolean;\n" + 
            "xlt4 = xlt3;\n" + 
            "xlt5 = xlt4;\n" + 
            "xlt6 = xlt5;\n" + 
            "xlt7 = xlt6;\n" +
            "in ();",
            MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a top level function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfFunctionNotCompatibleWithInferredType_gt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "gt :: (Prelude.Num a) => a -> a -> Prelude.Boolean;\n" + 
            "gt x y =\n" + 
            "    let\n" + 
            "        foo = gt3 0 1;\n" + 
            "    in\n" + 
            "        x > y;\n" + 
            "\n" + 
            "gt2 :: Prelude.Int -> Prelude.Int -> Prelude.Boolean;\n" + 
            "gt2 = gt;\n" + 
            "gt3 = gt2;",
            MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a local function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfLocalFunctionNotCompatibleWithInferredType_gt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "topLevelFunction = let\n" +
            "gt :: (Prelude.Num a) => a -> a -> Prelude.Boolean;\n" + 
            "gt x y =\n" + 
            "    let\n" + 
            "        foo = gt3 0 1;\n" + 
            "    in\n" + 
            "        x > y;\n" + 
            "\n" + 
            "gt2 :: Prelude.Int -> Prelude.Int -> Prelude.Boolean;\n" + 
            "gt2 = gt;\n" + 
            "gt3 = gt2;\n" +
            "in ();",
            MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a top level function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfFunctionNotCompatibleWithInferredType_xgt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "xgt :: (Prelude.Num a) => a -> b -> Prelude.Boolean;\n" + 
            "xgt x y =\n" + 
            "    let\n" + 
            "        foo = xgt3 0 1;\n" + 
            "    in\n" + 
            "        x > y;\n" + 
            "\n" + 
            "xgt2 :: Prelude.Int -> Prelude.Int -> Prelude.Boolean;\n" + 
            "xgt2 = xgt;\n" + 
            "xgt3 = xgt2;",
            MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a local function not compatible with its inferred type, for
     * a case of mutually recursive functions.
     */
    public void testDeclaredTypeOfLocalFunctionNotCompatibleWithInferredType_xgt() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "topLevelFunction = let\n" +
            "xgt :: (Prelude.Num a) => a -> b -> Prelude.Boolean;\n" + 
            "xgt x y =\n" + 
            "    let\n" + 
            "        foo = xgt3 0 1;\n" + 
            "    in\n" + 
            "        x > y;\n" + 
            "\n" + 
            "xgt2 :: Prelude.Int -> Prelude.Int -> Prelude.Boolean;\n" + 
            "xgt2 = xgt;\n" + 
            "xgt3 = xgt2;\n" +
            "in ();",
            MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a top level function not compatible with its inferred type, for
     * a case of having strongly-connected components of size 1.
     */
    public void testDeclaredTypeOfFunctionNotCompatibleWithInferredType_sizeOneComponents() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo :: (Prelude.Num a) => b -> a;\n" +
            "foo x = 1.0;\n" +
            "bar :: ();\n" +
            "bar = [];",
            MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for a declared type of a local function not compatible with its inferred type, for
     * a case of having strongly-connected components of size 1.
     */
    public void testDeclaredTypeOfLocalFunctionNotCompatibleWithInferredType_sizeOneComponents() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "topLevelFunction = let\n" +
            "foo :: (Prelude.Num a) => b -> a;\n" +
            "foo x = 1.0;\n" +
            "bar :: ();\n" +
            "bar = [];\n" +
            "in ();",
            MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType.class, leccCALServices);
    }
}
