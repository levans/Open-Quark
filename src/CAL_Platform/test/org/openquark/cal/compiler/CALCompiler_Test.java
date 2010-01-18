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
 * CALCompiler_Test.java
 * Creation date: Feb 8, 2007.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Assert;
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
public class CALCompiler_Test extends TestCase {

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

        TestSuite suite = new TestSuite(CALCompiler_Test.class);

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
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }
    
    /**
     * Tests the error message for unresolved module import - without suggestions.
     */
    public void testUnresolvedExternalModuleImportWithNoSuggestions() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "import Bar;",
            MessageKind.Error.UnresolvedExternalModuleImportWithNoSuggestions.class, leccCALServices);
    }
    
    /**
     * Tests the error message for unresolved module import - without one suggestion.
     */
    public void testUnresolvedExternalModuleImportWithOneSuggestion() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "import List;", // List -> Cal.Collections.List
            MessageKind.Error.UnresolvedExternalModuleImportWithOneSuggestion.class, leccCALServices);
    }
    
    /**
     * Tests the error message for unresolved module import - without multiple suggestions.
     */
    public void testUnresolvedExternalModuleImportWithMultipleSuggestions() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "import Beta;", // -> Cal.Test.General.HierarchicalModuleName.Test.Beta, ...Test.Alpha.Beta, ...Test.Alpha.Gamma.Beta
            MessageKind.Error.UnresolvedExternalModuleImportWithMultipleSuggestions.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a foreign function declaration for a class literal contains a wrong return type.
     */
    public void testClassLiteralWithWrongReturnType() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"class int[]\" intArrayClass :: Prelude.String;",
            MessageKind.Error.ForeignFunctionReturnsWrongType.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"class int[]\" intArrayClass :: Prelude.Int;",
            MessageKind.Error.ForeignFunctionReturnsWrongType.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"class java.lang.String\" stringClass :: Prelude.String;",
            MessageKind.Error.ForeignFunctionReturnsWrongType.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"class int\" intClass :: Prelude.Int;",
            MessageKind.Error.ForeignFunctionReturnsWrongType.class, leccCALServices);
    }
    
    /**
     * Tests the error message for trying to declare foreign class literals for invalid types like void[] and void[][].
     */
    public void testVoidArrayClassLiteralsError() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"class void[]\" voidArrayClass :: JClass; data foreign unsafe import jvm \"java.lang.Class\" JClass;",
            MessageKind.Error.JavaClassNotFound.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"class void[][]\" voidArrayClass :: JClass; data foreign unsafe import jvm \"java.lang.Class\" JClass;",
            MessageKind.Error.JavaClassNotFound.class, leccCALServices);
    }
    
    /**
     * Tests the error message for trying to declare foreign instanceof functions for invalid types like void[] and void[][].
     */
    public void testVoidArrayInstanceOfFunctionsError() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"instanceof void[]\" instanceof_voidArrayClass :: Prelude.JObject -> Prelude.Boolean;",
            MessageKind.Error.JavaClassNotFound.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"instanceof void[][]\" instanceof_voidArrayClass :: Prelude.JObject -> Prelude.Boolean;",
            MessageKind.Error.JavaClassNotFound.class, leccCALServices);
    }
    
    /**
     * Tests the error message for trying to declare foreign static methods/static fields/constructors for array types.
     */
    public void testForeignMemberDeclsForArrayError() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"constructor int[]\" ctor :: JIntArray; data foreign unsafe import jvm \"int[]\" JIntArray;",
            MessageKind.Error.CouldNotFindConstructorWithGivenArgumentTypes.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"constructor java.lang.String[]\" ctor :: JStringArray; data foreign unsafe import jvm \"java.lang.String[]\" JStringArray;",
            MessageKind.Error.CouldNotFindConstructorWithGivenArgumentTypes.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"static field java.lang.String[].length\" ctor :: Prelude.Int;",
            MessageKind.Error.CouldNotFindField.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foreign unsafe import jvm \"static method java.lang.String[].toString\" ctor :: Prelude.String;",
            MessageKind.Error.CouldNotFindStaticMethodWithGivenArgumentTypes.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a case expression containing a tuple pattern has more than one alternative.
     */
    public void testOnlyOnePatternAllowedInCaseExpressionWithTuplePattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = case (\"f\",\"g\") of (a,b) -> a++b; _ -> \"haha\";;",
            MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithTuplePattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a case expression containing a unit pattern has more than one alternative.
     */
    public void testOnlyOnePatternAllowedInCaseExpressionWithUnitPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = case () of () -> Prelude.True; _ -> Prelude.False;;",
            MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithUnitPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a case alternative associated with a unit pattern contains a CALDoc error in the defining expression.
     */
    public void testCALDocErrorInsideCaseAlternativeWithUnitPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = case () of () -> let /** @arg foo */ true = Prelude.True; in true;;",
            MessageKind.Error.TooManyArgTagsInCALDocComment.class, leccCALServices);
    }
    
    /**
     * Tests the error message for derived instance functions have 
     * a source position.
     */
    public void testDerivedInstanceHasErrorNumber() {
        
        List<CompilerMessage> messages = CompilerTestUtilities.compileAndGetMessages(
                ModuleName.make("InstanceErrorCheck"), 
                new String[] {
                    "data FastArray a = Base l :: a;", 
                    "data B = X x :: (FastArray Prelude.Char) deriving Prelude.Eq;"
                    },
               leccCALServices);
        
        Assert.assertEquals(2, messages.size());
        Assert.assertNotNull(messages.get(0).getSourceRange());
        Assert.assertNotNull(messages.get(1).getSourceRange());

    }
    
    /** 
     * Test compiler error message for attempt to get dictionary for nonexistent
     * instance function. 
     */
    public void testNonExistentDictionaryFunction() {
        CompilerTestUtilities.checkDefnForExpectedError(
                "import Cal.Core.Record;" +
                "getDict :: Prelude.Num r => {r} -> Record.Dictionary {r};" +
                "getDict r = Record.dictionary r \"incorrect\"; ",
                MessageKind.Error.DictionaryMethodDoesNotExist.class, leccCALServices);
    }
    
    /** 
     * Test compiler error message for attempt to get dictionary for 
     * a non string literal function
     */
    public void testDictionaryFunctionNotLiteral() {
        CompilerTestUtilities.checkDefnForExpectedError(
                "import Cal.Core.Record;" +
                "k=\"name\";" +
                "getDict :: Prelude.Num r => {r} -> Record.Dictionary {r};" +
                "getDict r = Record.dictionary r k; ",
                MessageKind.Error.RecordDictionaryArgumentsInvalid.class, leccCALServices);
    }
    
    /** 
     * Test compiler error message for attempt to get dictionary from
     * a function without a dictionary
     */
    public void testDictionaryFunctionNoDict() {
        CompilerTestUtilities.checkDefnForExpectedError(
                "import Cal.Core.Record;" +
                "getDict :: {r} -> Record.Dictionary {r};" +
                "getDict r = Record.dictionary r \"toInt\"; ",
                MessageKind.Error.RecordDictionaryMisused.class, leccCALServices);
    }
    
       
    
}
