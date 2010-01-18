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
 * TypeDeclarationInserter_Test.java
 * Creation date: (Feb 22, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.Collections;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.WorkspaceManager;


public class TypeDeclarationInserter_Test extends TestCase {
    
    private static final String MinimallyQualifiedPreludeName = "Prelude";

    /** A cached reference to the CAL test services */
    private static BasicCALServices leccCALServices;

    /** Cached reference to a workspace manager */
    private static WorkspaceManager workspaceManager;
        
    /**
     * @return a test suite containing all the test cases for testing CAL import refactoring
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(TypeDeclarationInserter_Test.class);

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
        workspaceManager = leccCALServices.getWorkspaceManager();    
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        workspaceManager = null;
    }
    
    public void testToplevelRecordTypeInsertions() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "// accepts a record and returns a numeric type\n" +
            "acceptRecordReturnNum !r =\n" +
            "    case r of\n" +
            "    {a | foo} -> foo + 5;\n" +
            "    ;\n",
            // versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "// accepts a record and returns a numeric type\n" +
            "acceptRecordReturnNum :: (a\\foo, Num b) => {a | foo :: b} -> b;\n" +
            "acceptRecordReturnNum !r =\n" +
            "    case r of\n" +
            "    {a | foo} -> foo + 5;\n" +
            "    ;\n");
    }

    public void testToplevelFunctionTypeInsertions() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "    // funnily-indented\n" +
            "    // accepts a two-argument function and returns the result of applying it to two arguments\n" +
            "    accept2ArgFunction f x y =\n" +
            "        f x y;\n",
            // versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "    // funnily-indented\n" +
            "    // accepts a two-argument function and returns the result of applying it to two arguments\n" +
            "    accept2ArgFunction :: (a -> b -> c) -> a -> b -> c;\n" +
            "    accept2ArgFunction f x y =\n" +
            "        f x y;\n");
    }

    public void testToplevelTupleTypeInsertions() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "acceptTupleReturnInt tuple =\n" +
            "    case tuple of\n" +
            "    (_, x) -> x + 5 :: Int;\n" +
            "    ;\n",
            // versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "acceptTupleReturnInt :: (a, Int) -> Int;\n" +
            "acceptTupleReturnInt tuple =\n" +
            "    case tuple of\n" +
            "    (_, x) -> x + 5 :: Int;\n" +
            "    ;\n");
    }

    public void testLocalDefnSimpleTypeInsertions() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "topLevelAcceptsIntsReturnsDouble :: Int -> Int -> Double;\n" +
            "private topLevelAcceptsIntsReturnsDouble !m !n =\n" +
            "    let\n" +
            "        overloaded = m + n + (1 :: Int);\n" +
            "    in\n" +
            "        " + CAL_Prelude.MODULE_NAME + ".toDouble overloaded;\n",
            // versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded :: Double;\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "topLevelAcceptsIntsReturnsDouble :: Int -> Int -> Double;\n" +
            "private topLevelAcceptsIntsReturnsDouble !m !n =\n" +
            "    let\n" +
            "        overloaded :: Int;\n" +
            "        overloaded = m + n + (1 :: Int);\n" +
            "    in\n" +
            "        " + CAL_Prelude.MODULE_NAME + ".toDouble overloaded;\n");
    }
    
    public void testLocalDefnSkipNonGenericTypes() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded :: Double;\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "topLevelAcceptsNumsReturnsDouble :: Num a => a -> a -> Double;\n" +
            "private topLevelAcceptsNumsReturnsDouble !m !n =\n" +
            "    let\n" +
            "        localDouble = 5.0;\n" +
            "        overloaded = m + n;\n" +
            "    in\n" +
            "        (" + CAL_Prelude.MODULE_NAME + ".toDouble overloaded) + localDouble;\n", 
            // versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded :: Double;\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "topLevelAcceptsNumsReturnsDouble :: Num a => a -> a -> Double;\n" +
            "private topLevelAcceptsNumsReturnsDouble !m !n =\n" +
            "    let\n" +
            "        localDouble :: Double;\n" +
            "        localDouble = 5.0;\n" +
            "        overloaded = m + n;\n" +
            "    in\n" +
            "        (" + CAL_Prelude.MODULE_NAME + ".toDouble overloaded) + localDouble;\n"); 
    }
    
    public void testLocalDefnHandleInstantiatedNonGenericTypes() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded :: Double;\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "topLevelAcceptDeclaredIntsReturnsDouble :: Int -> Int -> Double;\n" +
            "topLevelAcceptDeclaredIntsReturnsDouble !m !n =\n" +
            "    let\n" +
            "        overloaded = m + n;\n" +
            "    in\n" +
            "        toDouble overloaded;\n",
            // versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded :: Double;\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "topLevelAcceptDeclaredIntsReturnsDouble :: Int -> Int -> Double;\n" +
            "topLevelAcceptDeclaredIntsReturnsDouble !m !n =\n" +
            "    let\n" +
            "        overloaded :: Int;\n" +
            "        overloaded = m + n;\n" +
            "    in\n" +
            "        toDouble overloaded;\n");
    }
    
    public void testLocalDefnNesting() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "tupleMaker :: Num a => a -> b -> Int -> " + CAL_Prelude.MODULE_NAME + ".String -> (a, b, Int, " + CAL_Prelude.MODULE_NAME + ".String);\n" +
            "tupleMaker w x y z =\n" +
            "    let\n" +
            "        overloaded = w + 10;\n" +
            "        arg1 = overloaded;\n" +
            "    in\n" +
            "        let\n" +
            "            arg2 = overloaded;\n" +
            "            overloaded = x;\n" +
            "        in\n" +
            "            let\n" +
            "                overloaded = y * 10;\n" +
            "                arg3 = overloaded;\n" +
            "            in\n" +
            "                let\n" +
            "                    arg4 = overloaded z;\n" +
            "                    overloaded x = z ++ x;\n" +
            "                in\n" +
            "                    (arg1, arg2, arg3, arg4);\n",
            // versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "overloaded :: Double;\n" +
            "overloaded = 1.2;\n" +
            "\n" +
            "tupleMaker :: Num a => a -> b -> Int -> " + CAL_Prelude.MODULE_NAME + ".String -> (a, b, Int, " + CAL_Prelude.MODULE_NAME + ".String);\n" +
            "tupleMaker w x y z =\n" +
            "    let\n" +
            "        overloaded = w + 10;\n" +
            "        arg1 = overloaded;\n" +
            "    in\n" +
            "        let\n" +
            "            arg2 = overloaded;\n" +
            "            overloaded = x;\n" +
            "        in\n" +
            "            let\n" +
            "                overloaded :: Int;\n" +
            "                overloaded = y * 10;\n" +
            "                \n" +
            "                arg3 :: Int;\n" +
            "                arg3 = overloaded;\n" +
            "            in\n" +
            "                let\n" +
            "                    arg4 :: " + MinimallyQualifiedPreludeName + ".String;\n" +
            "                    arg4 = overloaded z;\n" +
            "                    \n"+
            "                    overloaded :: " + MinimallyQualifiedPreludeName + ".String -> " + MinimallyQualifiedPreludeName + ".String;\n" +
            "                    overloaded x = z ++ x;\n" +
            "                in\n" +
            "                    (arg1, arg2, arg3, arg4);\n");
    }
    
    public void testNestedGenericVars() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "nestedGenericVars x =\n" +
            "    let\n" +
            "        constString = \"hello\";\n" +
            "        nested1 y =\n" +
            "            let\n" +
            "                nested2 = x + y;\n" +
            "            in\n" +
            "                nested2;\n" +
            "    in\n" +
            "        (nested1 (x + 2), constString);\n",
            //versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "nestedGenericVars :: Num a => a -> (a, " + MinimallyQualifiedPreludeName + ".String);\n" +
            "nestedGenericVars x =\n" +
            "    let\n" +
            "        constString :: " + MinimallyQualifiedPreludeName + ".String;\n" +
            "        constString = \"hello\";\n" +
            "        nested1 y =\n" +
            "            let\n" +
            "                nested2 = x + y;\n" +
            "            in\n" +
            "                nested2;\n" +
            "    in\n" +
            "        (nested1 (x + 2), constString);\n");
    }
    
    public void testLambdaHandling() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "usesLambdas =\n" + 
            "    let\n" +
            "        localLambda list = (\\ x -> x) list;\n" +
            "        otherLocal = 55 * 10.0;\n" +
            "    in\n" +
            "        localLambda [otherLocal];\n",
            //versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "usesLambdas :: [Double];\n" +
            "usesLambdas =\n" + 
            "    let\n" +
            "        localLambda :: a -> a;\n" +
            "        localLambda list = (\\ x -> x) list;\n" +
            "        \n" +
            "        otherLocal :: Double;\n" +
            "        otherLocal = 55 * 10.0;\n" +
            "    in\n" +
            "        localLambda [otherLocal];\n");
    }
    
    public void testDataconsSelectionHandling() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "usesDataconsSelection =\n" + 
            "    let\n" +
            "        stripJust x = x.Just.value;\n" +
            "        otherLocal = Just 'c';\n" +
            "    in\n" +
            "        stripJust otherLocal;\n",
            //versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "usesDataconsSelection :: " + MinimallyQualifiedPreludeName + ".Char;\n" +
            "usesDataconsSelection =\n" + 
            "    let\n" +
            "        stripJust :: Maybe a -> a;\n" +
            "        stripJust x = x.Just.value;\n" +
            "        \n" +
            "        otherLocal :: Maybe " + MinimallyQualifiedPreludeName + ".Char;\n" +
            "        otherLocal = Just 'c';\n" +
            "    in\n" +
            "        stripJust otherLocal;\n");
    }
            
    public void testRecordSelectionHandling() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "usesRecordSelection =\n" +
            "    let\n" +
            "        stripJob x = x.job;\n" +
            "        otherLocal = {job = \"jaerb\", five = 5 :: Int};\n" +
            "    in\n" +
            "        stripJob otherLocal;\n",            
            //versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "usesRecordSelection :: " + MinimallyQualifiedPreludeName + ".String;\n" +
            "usesRecordSelection =\n" +
            "    let\n" +
            "        stripJob :: a\\job => {a | job :: b} -> b;\n" +
            "        stripJob x = x.job;\n" +
            "        \n" +
            "        otherLocal :: {five :: Int, job :: " + MinimallyQualifiedPreludeName + ".String};\n" +
            "        otherLocal = {job = \"jaerb\", five = 5 :: Int};\n" +
            "    in\n" +
            "        stripJob otherLocal;\n");
    }

    public void testNestedLocalsFirst() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "nestedLocalsFirst =\n" + 
            "    let\n" +
            "        blah1 =\n" +
            "            let\n" +
            "                nestedId = \"MASHED POTATO\";\n" +
            "            in\n" +
            "                nestedId;\n" +
            "        \n" +
            "        blah2 =\n" +
            "            let\n" +
            "                nestedId = 5.0;\n" +
            "            in\n" +
            "                nestedId + 10;\n" +
            "        \n" +
            "        nestedId = 'c';\n" +
            "    in\n" +
            "        blah2;",
            //versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "nestedLocalsFirst :: Double;\n" +
            "nestedLocalsFirst =\n" + 
            "    let\n" +
            "        blah1 :: " + MinimallyQualifiedPreludeName + ".String;\n" +
            "        blah1 =\n" +
            "            let\n" +
            "                nestedId :: " + MinimallyQualifiedPreludeName + ".String;\n" +
            "                nestedId = \"MASHED POTATO\";\n" +
            "            in\n" +
            "                nestedId;\n" +
            "        \n" +
            "        blah2 :: Double;\n" +
            "        blah2 =\n" +
            "            let\n" +
            "                nestedId :: Double;\n" +
            "                nestedId = 5.0;\n" +
            "            in\n" +
            "                nestedId + 10;\n" +
            "        \n" +
            "        nestedId :: " + MinimallyQualifiedPreludeName + ".Char;\n" +
            "        nestedId = 'c';\n" +
            "    in\n" +
            "        blah2;");
    }
    
    public void testNonGenericRecordVarHandling() {
        checkRefactoringResult(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "wrapRecord r =\n" +
            "    let\n" +
            "        wrappedRecord =\n" + 
            "            if r.shouldWrap then\n" +
            "                [r]\n" +
            "            else\n" +
            "                [];\n" +
            "    in\n" +
            "        wrappedRecord;\n",
            //versus
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Double, Int, Maybe;\n" +
            "    typeClass = Num;\n" +
            "    dataConstructor = Just;\n" +
            "    function = toDouble;\n" +
            "    ;\n" +
            "\n" +
            "wrapRecord ::\n" +
            "    a\\shouldWrap =>\n" +
            "    {a | shouldWrap :: " + MinimallyQualifiedPreludeName + ".Boolean}\n" +
            "    -> [{a | shouldWrap :: " + MinimallyQualifiedPreludeName + ".Boolean}];\n" +
            "wrapRecord r =\n" +
            "    let\n" +
            "        wrappedRecord =\n" + 
            "            if r.shouldWrap then\n" +
            "                [r]\n" +
            "            else\n" +
            "                [];\n" +
            "    in\n" +
            "        wrappedRecord;\n");
    }
    
    private void checkRefactoringResult(String origModuleText, String expectedModuleText) {
        CompilerMessageLogger logger = new MessageLogger();
        TypeDeclarationInserter.RefactoringStatistics refactoringStatistics = new TypeDeclarationInserter.RefactoringStatistics();
        SourceModifier sourceModifier = TypeDeclarationInserter.getSourceModifier(workspaceManager.getWorkspace().asModuleContainer(), CALPlatformTestModuleNames.TypeDeclarationInserter_Test_Support, null, origModuleText, logger, refactoringStatistics); 
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        assertEquals(0, refactoringStatistics.getTypeDeclarationsNotAddedDueToPotentialImportConflict());
        assertEquals(Collections.<ModuleName>emptySet(), refactoringStatistics.getImportsNotAddedDueToPotentialConfict());
        
        String newModuleText = sourceModifier.apply(origModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(expectedModuleText, newModuleText);

        String undoneModuleText = sourceModifier.undo(newModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(origModuleText, undoneModuleText);
    }
}
