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
 * CALSourceGenerator_Test.java
 * Creation date: Feb 15, 2005.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import java.util.Collections;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.gems.client.services.GemFactory;


/**
 * JUnit test cases for the CALSourceGenerator class from the compiler package.
 * While CALSourceGenerator resides in the CAL project, these test cases need to
 * live in the Quark_Gems project because they require the use of code gems, which 
 * is a Quark Gems concept.
 * <p>
 * 
 * In particular, the test cases appearing in this class includes a set of tests
 * for the generation of CAL source for code gems, where the algorithm takes
 * special care to either minimize or completely eliminate the use of lambdas
 * for wrapping up code gems.
 * <p>
 * 
 * @author Joseph Wong
 */
public class CALSourceGenerator_Test extends TestCase {
    
    /**
     * used to create gems for the test cases
     */
    private static GemFactory gemFactory;
    
    /**
     * A copy of the CAL services used in the test cases
     */
    private static BasicCALServices calServices;
    
    private static final ModuleName testModule = CALPlatformTestModuleNames.M2;

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

        TestSuite suite = new TestSuite(CALSourceGenerator_Test.class);

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
        calServices = BasicCALServices.make(GemCutter.GEMCUTTER_PROP_WORKSPACE_FILE, "cal.platform.test.cws", null);                   
        calServices.compileWorkspace(null, new MessageLogger());        
        gemFactory = new GemFactory(calServices);
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        calServices = null;
        gemFactory = null;
    }

    /**
     * Constructor for CALSourceGenerator_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public CALSourceGenerator_Test(String name) {
        super(name);
    }
    
    /**
     * Creates a code gem from the supplied CAL code.
     * 
     * @param code
     *            the source code for the gem.
     * @return the code gem.
     */
    private static CodeGem createCodeGem(String code) {
        CodeAnalyser codeAnalyser = new CodeAnalyser(
            calServices.getTypeChecker(),
            calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
            true,
            false);

        return new CodeGem(codeAnalyser, code, Collections.<String>emptySet());
    }
    
    /**
     * Renames one of the gem's inputs.
     * 
     * @param gem
     *            the gem.
     * @param pos
     *            the index for the input to be renamed.
     * @param oldName
     *            the original argument name for the input.
     * @param newName
     *            the new argument name for the input.
     */
    private static void renameArg(Gem gem, int pos, String oldName, String newName) {
        Gem.PartInput input = gem.getInputPart(pos);
        assertTrue(input.getArgumentName().getBaseName().equals(oldName));
        input.setArgumentName(new ArgumentName(newName));
    }
    
    /**
     * Burns one of the gem's inputs.
     * 
     * @param gem
     *            the gem.
     * @param pos
     *            the index for the input to be burnt.
     * @param name
     *            the argument name for the input to be burnt.
     */
    private static void burnArg(Gem gem, int pos, String name) {
        Gem.PartInput input = gem.getInputPart(pos);
        assertTrue(input.getArgumentName().getBaseName().equals(name));
        input.setBurnt(true);
    }

    /**
     * Reorders the arguments on a code gem to a new permutation.
     * 
     * @param codeGem
     *            the code gem whose arguments are to be reordered.
     * @param newPermutation
     *            the new permutation of the arguments.
     */
    private static void reorderArgs(CodeGem codeGem, int[] newPermutation) {
        Argument.NameTypePair[] currentArgs = codeGem.getArguments();
        
        if (codeGem.getCodeResultType() == null) {
            throw new IllegalStateException(
            "Attempt to reorder inputs on a broken gem.");
        }
        
        // fill in an array that tracks which inputs are included in
        // newInputNums
        boolean[] inputsGiven = new boolean[newPermutation.length];
        for (final int inputNum : newPermutation) {
            if (inputNum < 0 || inputNum >= newPermutation.length) {
                throw new IllegalArgumentException(
                "Input nums must be in the range 0 to (numInputs-1) inclusive.");
            }
            inputsGiven[inputNum] = true;
        }
        
        // now check that all the inputs are accounted for
        for (final boolean inputGiven : inputsGiven) {
            if (!inputGiven) {
                throw new IllegalArgumentException(
                "Attempt to reorder inputs to a state without all the inputs.");
            }
        }
        
        // declare the new args array
        int numArgs = newPermutation.length;
        Argument.NameTypePair[] newArgs = new Argument.NameTypePair[numArgs];
        
        // fill in the new args array
        for (int i = 0; i < numArgs; i++) {
            newArgs[i] = currentArgs[newPermutation[i]];
        }
        
        // Everything else should be the same..
        boolean wasBroken = codeGem.isBroken();
        codeGem.definitionUpdate(
            codeGem.getCode(), newArgs,
            codeGem.getCodeResultType(), codeGem.getArgNameToInputMap(),
            codeGem.getQualificationMap(), codeGem.getVisibleCode());
        
        codeGem.setBroken(wasBroken);
    }
    
    /**
     * Creates a source model for a top-level function definition from CAL code.
     * 
     * @param args
     *            a space-delimited list of the arguments for the function.
     * @param body
     *            the text of the body of the function.
     * @return the source model for the function definition.
     */
    private static SourceElement createFunctionDefn(String args, String body) {
        
        return SourceModelUtilities.TextParsing.parseAlgebraicFunctionDefnIntoSourceModel(
            "private result " + args + " = " + body + ";");
    }
    
    /**
     * Sets up a connection between the source gem's output and one of the sink
     * gem's inputs.
     * 
     * @param gemGraph
     *            the gem graph containing the source and sink gems.
     * @param source
     *            the source gem.
     * @param sink
     *            the sink gem.
     * @param inputPos
     *            the index for the input on the sink gem to be connected.
     * @param inputName
     *            the argument name for the input on the sink gem to be
     *            connected.
     */
    private static void connect(GemGraph gemGraph, Gem source, Gem sink, int inputPos, String inputName) {
        Gem.PartOutput output = source.getOutputPart();
        Gem.PartInput input = sink.getInputPart(inputPos);
        assertTrue(input.getArgumentName().getBaseName().equals(inputName));
        
        try {
            gemGraph.connectGems(output, input);
            gemGraph.typeGemGraph(calServices.getTypeCheckInfo(testModule));
        } catch (IllegalArgumentException e) {
            fail("Failed to connect the gems");
        } catch (TypeException e) {
            fail("The gem graph fails to typecheck");
        }
    }
    
    /**
     * Asserts that the CAL code generated for the supplied gem is identical to
     * the CAL code generated for the supplied source model element. If it isn't
     * it throws an AssertionFailedError.
     * 
     * @param gem
     *            the gem.
     * @param element
     *            the source model element.
     */
    private static void assertFunctionTextEquals(Gem gem, SourceElement element) {
        String text1 = element.toSourceText();
        String text2 = CALSourceGenerator.getFunctionText("result", gem, Scope.PRIVATE);
        
        if (SHOW_DEBUGGING_OUTPUT) {
            if (text1.equals(text2)) {
                System.out.println("Function texts equal:\n" + text1 + "\n" + text2);
            } else {
                System.err.println("Function texts not equal:\n" + text1 + "\n" + text2);
            }
        }
        
        assertEquals(text1, text2);
    }
    
    ////=============================================================
    /// Test cases
    //

    /////-------------------------------------------------------
    //// Test input burning, gem connections, argument renaming,
    ///  and argument reordering for the code gem "x + y"
    //
    
    public void testSimpleCodeGem() {
        CodeGem gem = createCodeGem("x + y");
        
        SourceElement element = createFunctionDefn("x y", "x + y");
        
        assertFunctionTextEquals(gem, element);
    }
    
    public void testSimpleCodeGem_ArgReordering() {
        CodeGem gem = createCodeGem("x + y");
        reorderArgs(gem, new int[] {1, 0});
        
        SourceElement element = createFunctionDefn("y x", "x + y");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testSimpleCodeGem_ArgRenaming() {
        String code = "%x + %y";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%y", "y"));
        
        renameArg(gem, 0, "x", "a");
        renameArg(gem, 1, "y", "b");
        
        SourceElement element = createFunctionDefn("a b", code
            .replaceAll("%x", "a").replaceAll("%y", "b"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    public void testSimpleCodeGem_ArgRenaming_ArgReordering() {
        String code = "%x + %y";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%y", "y"));
        
        renameArg(gem, 0, "x", "a");
        renameArg(gem, 1, "y", "b");
        reorderArgs(gem, new int[] {1, 0});
        
        SourceElement element = createFunctionDefn("b a", code
            .replaceAll("%x", "a").replaceAll("%y", "b"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    public void testSimpleCodeGem_BurntInput1() {
        CodeGem gem = createCodeGem("x + y");
        burnArg(gem, 1, "y");
        
        SourceElement element = createFunctionDefn("x", "\\y -> x + y");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testSimpleCodeGem_BurntInput0() {
        CodeGem gem = createCodeGem("x + y");
        burnArg(gem, 0, "x");
        
        SourceElement element = createFunctionDefn("y", "\\x -> x + y");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testSimpleCodeGem_BurntInput0_ArgReordering() {
        CodeGem gem = createCodeGem("x + y");
        burnArg(gem, 0, "x");
        reorderArgs(gem, new int[] {1, 0});
        
        SourceElement element = createFunctionDefn("y", "\\x -> x + y");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testSimpleCodeGem_BurntAllInputs() {
        CodeGem gem = createCodeGem("x + y");
        burnArg(gem, 0, "x");
        burnArg(gem, 1, "y");
        
        SourceElement element = createFunctionDefn("", "\\x y -> x + y");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testSimpleCodeGem_BurntAllInputs_ArgReordering() {
        CodeGem gem = createCodeGem("x + y");
        burnArg(gem, 0, "x");
        burnArg(gem, 1, "y");
        reorderArgs(gem, new int[] {1, 0});
        
        SourceElement element = createFunctionDefn("", "\\y x -> x + y");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testSimpleCodeGem_BurntInput0_Input1Connected() {
        GemGraph gemGraph = new GemGraph();
        
        CodeGem gem = createCodeGem("x + y");
        gemGraph.addGem(gem);
        
        burnArg(gem, 0, "x");
        
        Gem absGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.abs);
        gemGraph.addGem(absGem);
        
        connect(gemGraph, absGem, gem, 1, "y");
        
        SourceElement element = createFunctionDefn("x_1", "\\x -> (\\y -> x + y) (" + CAL_Prelude.Functions.abs.getQualifiedName() + " x_1)");
        
        assertFunctionTextEquals(gem, element);
    }
    
    /////-------------------------------------------------
    //// Test input burning, gem connections, and argument
    ///  reordering for the code gem "(a, b, c, d)"
    //
    
    public void testTuple4CodeGem_Input1Connected() {
        GemGraph gemGraph = new GemGraph();
        
        CodeGem gem = createCodeGem("(a, b, c, d)");
        gemGraph.addGem(gem);
        
        Gem absGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.abs);
        gemGraph.addGem(absGem);
        
        connect(gemGraph, absGem, gem, 1, "b");
        
        SourceElement element = createFunctionDefn("a x c d", "(\\b -> (a, b, c, d)) (" + CAL_Prelude.Functions.abs.getQualifiedName() + " x)");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testTuple4CodeGem_BurntTwoInputs() {
        CodeGem gem = createCodeGem("(a, b, c, d)");
        burnArg(gem, 0, "a");
        burnArg(gem, 2, "c");
        
        SourceElement element = createFunctionDefn("b d", "\\a c -> (a, b, c, d)");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testTuple4CodeGem_BurntTwoInputs_ArgReordering1() {
        CodeGem gem = createCodeGem("(a, b, c, d)");
        burnArg(gem, 0, "a");
        burnArg(gem, 2, "c");
        reorderArgs(gem, new int[] {1, 3, 0, 2});
        
        SourceElement element = createFunctionDefn("b d", "\\a c -> (a, b, c, d)");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testTuple4CodeGem_BurntTwoInputs_ArgReordering2() {
        CodeGem gem = createCodeGem("(a, b, c, d)");
        burnArg(gem, 0, "a");
        burnArg(gem, 2, "c");
        reorderArgs(gem, new int[] {2, 0, 3, 1});
        
        SourceElement element = createFunctionDefn("d b", "\\c a -> (a, b, c, d)");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testTuple4CodeGem_BurntTwoInputs_Input1Connected() {
        GemGraph gemGraph = new GemGraph();
        
        CodeGem gem = createCodeGem("(a, b, c, d)");
        gemGraph.addGem(gem);
        
        burnArg(gem, 0, "a");
        burnArg(gem, 2, "c");
        
        Gem absGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.abs);
        gemGraph.addGem(absGem);
        
        connect(gemGraph, absGem, gem, 1, "b");
        
        SourceElement element = createFunctionDefn("x d", "\\a c -> (\\b -> (a, b, c, d)) (" + CAL_Prelude.Functions.abs.getQualifiedName() + " x)");
        
        assertFunctionTextEquals(gem, element);
    }

    public void testTuple4CodeGem_OutputConnected() {
        GemGraph gemGraph = new GemGraph();
        
        CodeGem gem = createCodeGem("(a, b, c, d)");
        gemGraph.addGem(gem);
               
        Gem list2Gem = gemFactory.makeFunctionalAgentGem(CAL_List.Functions.list2);
        gemGraph.addGem(list2Gem);
        
        connect(gemGraph, gem, list2Gem, 1, "item2");
        
        SourceElement element = createFunctionDefn("item1 a b c d", CAL_List.Functions.list2.getQualifiedName() + " item1 (a, b, c, d)");
        
        assertFunctionTextEquals(list2Gem, element);
    }

    public void testTuple4CodeGem_BurntTwoInputs_OutputConnected() {
        GemGraph gemGraph = new GemGraph();
        
        CodeGem gem = createCodeGem("(a, b, c, d)");
        gemGraph.addGem(gem);
               
        burnArg(gem, 0, "a");
        burnArg(gem, 2, "c");
        reorderArgs(gem, new int[] {2, 3, 0, 1});

        Gem list2Gem = gemFactory.makeFunctionalAgentGem(CAL_List.Functions.list2);
        gemGraph.addGem(list2Gem);
        
        connect(gemGraph, gem, list2Gem, 1, "item2");
        
        SourceElement element = createFunctionDefn("item1 d b", CAL_List.Functions.list2.getQualifiedName() + " item1 (\\c a -> (a, b, c, d))");
        
        assertFunctionTextEquals(list2Gem, element);
    }

    public void testTuple4CodeGem_BurntTwoInputs_OutputConnected_ArgReordering() {
        GemGraph gemGraph = new GemGraph();
        
        CodeGem gem = createCodeGem("(a, b, c, d)");
        gemGraph.addGem(gem);
               
        burnArg(gem, 0, "a");
        burnArg(gem, 2, "c");

        Gem list2Gem = gemFactory.makeFunctionalAgentGem(CAL_List.Functions.list2);
        gemGraph.addGem(list2Gem);
        
        connect(gemGraph, gem, list2Gem, 1, "item2");
        
        SourceElement element = createFunctionDefn("item1 b d", CAL_List.Functions.list2.getQualifiedName() + " item1 (\\a c -> (a, b, c, d))");
        
        assertFunctionTextEquals(list2Gem, element);
    }

    ////---------------------------------
    /// Simple cases with let expressions
    //
    
    public void testCodeGemWithLet() {
        String code = "let b = x; a = y; in (b, a, x, y)";
        
        CodeGem gem = createCodeGem(code);
        
        SourceElement element = createFunctionDefn("x y", code);
        
        assertFunctionTextEquals(gem, element);
    }
    
    public void testCodeGemWithLet_ArgReordering() {
        String code = "let b = x; a = y; in (b, a, x, y)";
        
        CodeGem gem = createCodeGem(code);
        reorderArgs(gem, new int[] {1, 0});
        
        SourceElement element = createFunctionDefn("y x", code);
        
        assertFunctionTextEquals(gem, element);
    }

    ////------------------
    /// Local let bindings
    //
    
    public void testLetBindingShadowingOriginalParam() {
        String code = "let foo = %x; in let x = 3.0; in x";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingOriginalParam2() {
        String code = "let foo = x; in let x = 3.0; in x";
        
        CodeGem gem = createCodeGem(code);
                
        SourceElement element = createFunctionDefn("x", code);
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let @x = 3.0; bar = %z; in @x";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingRenamedParam() {
        String code = "let foo = %x; in let @y = 3.0; in @y";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingRenamedParamWithBackquotedOperator() {
        String code = "let @a = " + CAL_Prelude.Functions.add.getQualifiedName() + "; in 1.0 `@a` %x";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x").replaceAll("@a","a"));
        
        renameArg(gem, 0, "x", "a");
        
        SourceElement element = createFunctionDefn("a", code
            .replaceAll("%x", "a").replaceAll("@a", "a_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let @y = 3.0; y_1 = 4.0; in @y";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let @y = 3.0; bar = y_1; in @y";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let @y = 3.0; bar = %z; in @y";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLetBindingShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let @y = 3.0; bar = %z; baz = y_2; in @y";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }

    ////---------------------
    /// Local lambda bindings
    //
    
    public void testLambdaBindingShadowingOriginalParam() {
        String code = "let foo = %x; in (\\x -> x) 3.0";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLambdaBindingShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in (\\@x bar -> @x) 3.0 %z";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLambdaBindingShadowingRenamedParam() {
        String code = "let foo = %x; in (\\@y -> @y) 3.0";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLambdaBindingShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in (\\@y y_1 -> @y) 3.0 4.0";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLambdaBindingShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in (\\@y bar -> @y) 3.0 y_1";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLambdaBindingShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in (\\@y bar -> @y) 3.0 %z";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLambdaBindingShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in (\\@y bar baz -> @y) 3.0 %z y_2";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }

    ////---------------------------------
    /// Local function parameter bindings
    //
    
    public void testLocalDefnParamShadowingOriginalParam() {
        String code = "let foo = %x; in let thunk x = x; in thunk 3.0";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLocalDefnParamShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let thunk @x bar = @x; in thunk 3.0 %z";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLocalDefnParamShadowingRenamedParam() {
        String code = "let foo = %x; in let thunk @y = @y; in 3.0";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLocalDefnParamShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let thunk @y y_1 = @y; in thunk 3.0 4.0";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLocalDefnParamShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let thunk @y bar = @y; in thunk 3.0 y_1";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLocalDefnParamShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let thunk @y bar = @y; in thunk 3.0 %z";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testLocalDefnParamShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let thunk @y bar baz = @y; in thunk 3.0 %z y_2";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    ////------------------------------------------
    /// Case alternative data constructor patterns
    //
    
    public void testCaseAltDataConsPatternShadowingOriginalParam() {
        String code = "let foo = %x; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " x -> x;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @x -> @x;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @y -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let y_1 = 4.0; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @y -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = y_1; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @y -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @y -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = y_2; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @y ->@y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    ////------------------------------------------------
    /// Case alternative data constructor field patterns
    //
    
    public void testCaseAltDataConsFieldPatternShadowingOriginalParam() {
        String code = "let foo = %x; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value=x} -> x;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsFieldPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value=@x} -> @x;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsFieldPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsFieldPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let y_1 = 4.0; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsFieldPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = y_1; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsFieldPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsFieldPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = y_2; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value=@y} ->@y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    ////-----------------------------------------------
    /// Case alternative data constructor punned fields
    //
    
    public void testCaseAltDataConsPunnedFieldPatternShadowingOriginalParam() {
        String code = "let foo = %value; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value} -> value;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%value", "value"));
        
        renameArg(gem, 0, "value", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%value", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPunnedFieldPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %value; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value@PUNvalue} -> @value;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%value", "value").replaceAll("%z", "z")
            .replaceAll("@value", "value").replaceAll("@PUNvalue", ""));
        
        renameArg(gem, 0, "value", "y");
        renameArg(gem, 1, "z", "value");
        
        SourceElement element = createFunctionDefn("y value", code
            .replaceAll("%value", "y").replaceAll("%z", "value")
            .replaceAll("@value", "value_1").replaceAll("@PUNvalue", "=value_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPunnedFieldPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value@PUNvalue} -> @value;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@value", "value")
            .replaceAll("@PUNvalue", ""));
        
        renameArg(gem, 0, "x", "value");
        
        SourceElement element = createFunctionDefn("value", code
            .replaceAll("%x", "value").replaceAll("@value", "value_1")
            .replaceAll("@PUNvalue", "=value_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPunnedFieldPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let value_1 = 4.0; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value@PUNvalue} -> @value;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@value", "value")
            .replaceAll("@PUNvalue", ""));
        
        renameArg(gem, 0, "x", "value");
        
        SourceElement element = createFunctionDefn("value", code
            .replaceAll("%x", "value").replaceAll("@value", "value_2")
            .replaceAll("@PUNvalue", "=value_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPunnedFieldPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = value_1; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value@PUNvalue} -> @value;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@value", "value")
            .replaceAll("@PUNvalue", ""));
        
        renameArg(gem, 0, "x", "value");
        
        SourceElement element = createFunctionDefn("value value_1", code
            .replaceAll("%x", "value").replaceAll("@value", "value_2")
            .replaceAll("@PUNvalue", "=value_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPunnedFieldPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value@PUNvalue} -> @value;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@value", "value").replaceAll("@PUNvalue", ""));
        
        renameArg(gem, 0, "x", "value");
        renameArg(gem, 1, "z", "value_1");
        
        SourceElement element = createFunctionDefn("value value_1", code
            .replaceAll("%x", "value").replaceAll("%z", "value_1")
            .replaceAll("@value", "value_2").replaceAll("@PUNvalue", "=value_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltDataConsPunnedFieldPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = value_2; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " 3.0) of " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " {value@PUNvalue} -> @value;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@value", "value").replaceAll("@PUNvalue", ""));
        
        renameArg(gem, 0, "x", "value");
        renameArg(gem, 1, "z", "value_1");
        
        SourceElement element = createFunctionDefn("value value_1 value_2", code
            .replaceAll("%x", "value").replaceAll("%z", "value_1")
            .replaceAll("@value", "value_3").replaceAll("@PUNvalue", "=value_3"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    ////-------------------------------------------
    /// Case alternative tuple constructor patterns
    //
    
    public void testCaseAltTupleConsPatternShadowingOriginalParam() {
        String code = "let foo = %x; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + ", 3.0) of (_, x) -> x;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltTupleConsPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + ", 3.0) of (_, @x) -> @x;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltTupleConsPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + ", 3.0) of (_, @y) -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltTupleConsPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let y_1 = 4.0; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + ", 3.0) of (_, @y) -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltTupleConsPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = y_1; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + ", 3.0) of (_, @y) -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltTupleConsPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + ", 3.0) of (_, @y) -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltTupleConsPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = y_2; in case (" + CAL_Prelude.DataConstructors.Just.getQualifiedName() + ", 3.0) of (_, @y) ->@y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }

    ////------------------------------------------
    /// Case alternative list constructor patterns
    //
    
    public void testCaseAltListConsPatternShadowingOriginalParam() {
        String code = "let foo = %x; in case [3.0] of x:_ -> x;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltListConsPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case [3.0] of @x:_ -> @x;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltListConsPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case [3.0] of @y:_ -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltListConsPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let y_1 = 4.0; in case [3.0] of @y:_ -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltListConsPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = y_1; in case [3.0] of @y:_ -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltListConsPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case [3.0] of @y:_ -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltListConsPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = y_2; in case [3.0] of @y:_ ->@y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }

    ////--------------------------------------------
    /// Case alternative record constructor patterns
    //
    
    public void testCaseAltRecordPatternShadowingOriginalParam() {
        String code = "let foo = %x; in case {y=3.0} of {y=x} -> x;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case {y=3.0} of {y=@x} -> @x;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case {y=3.0} of {y=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let y_1 = 4.0; in case {y=3.0} of {y=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = y_1; in case {y=3.0} of {y=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case {y=3.0} of {y=@y} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = y_2; in case {y=3.0} of {y=@y} ->@y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }

    ////-------------------------------------------------
    /// Case alternative record constructor punned fields
    //
    
    public void testCaseAltRecordPunnedFieldPatternShadowingOriginalParam() {
        String code = "let foo = %x; in case {x=3.0} of {x} -> x;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPunnedFieldPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case {x=3.0} of {x@PUNx} -> @x;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x").replaceAll("@PUNx", ""));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1").replaceAll("@PUNx", "=x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPunnedFieldPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case {y=3.0} of {y@PUNy} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y")
            .replaceAll("@PUNy", ""));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1")
            .replaceAll("@PUNy", "=y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPunnedFieldPatternShadowingRenamedParam2() {
        String code = "case %q of {p@PUNp} -> @p + (let q = 6.0; in @p);";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%q", "q").replaceAll("@p", "p")
            .replaceAll("@PUNp", ""));
        
        renameArg(gem, 0, "q", "p");
        
        SourceElement element = createFunctionDefn("p", code
            .replaceAll("%q", "p").replaceAll("@p", "p_1")
            .replaceAll("@PUNp", "=p_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPunnedFieldPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let y_1 = 4.0; in case {y=3.0} of {y@PUNy} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y")
            .replaceAll("@PUNy", ""));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2")
            .replaceAll("@PUNy", "=y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPunnedFieldPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = y_1; in case {y=3.0} of {y@PUNy} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y")
            .replaceAll("@PUNy", ""));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2")
            .replaceAll("@PUNy", "=y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPunnedFieldPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case {y=3.0} of {y@PUNy} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y").replaceAll("@PUNy", ""));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2").replaceAll("@PUNy", "=y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordPunnedFieldPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = y_2; in case {y=3.0} of {y@PUNy} ->@y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y").replaceAll("@PUNy", ""));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3").replaceAll("@PUNy", "=y_3"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    ////-------------------------------------------------------
    /// Case alternative record constructor base field patterns
    //
    
    public void testCaseAltRecordBaseFieldPatternShadowingOriginalParam() {
        String code = "let foo = %x; in case {y=3.0} of {x|y=qux} -> x;";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code.replaceAll("%x", "y"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordBaseFieldPatternShadowingOriginalParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case {y=3.0} of {@x|y=qux} -> @x;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@x", "x"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "x");
        
        SourceElement element = createFunctionDefn("y x", code
            .replaceAll("%x", "y").replaceAll("%z", "x")
            .replaceAll("@x", "x_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordBaseFieldPatternShadowingRenamedParam() {
        String code = "let foo = %x; in case {y=3.0} of {@y|y=qux} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_1"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordBaseFieldPatternShadowingRenamedParamCollidingWithLocalDefn() {
        String code = "let foo = %x; in let y_1 = 4.0; in case {y=3.0} of {@y|y=qux} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordBaseFieldPatternShadowingRenamedParamCollidingWithAnotherOriginalParam() {
        String code = "let foo = %x; in let bar = y_1; in case {y=3.0} of {@y|y=qux} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordBaseFieldPatternShadowingRenamedParamCollidingWithAnotherRenamedParam() {
        String code = "let foo = %x; in let bar = %z; in case {y=3.0} of {@y|y=qux} -> @y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_2"));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testCaseAltRecordBaseFieldPatternShadowingRenamedParamCollidingWithOriginalAndRenamedParams() {
        String code = "let foo = %x; in let bar = %z; baz = y_2; in case {y=3.0} of {@y|y=qux} ->@y;";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("%z", "z")
            .replaceAll("@y", "y"));
        
        renameArg(gem, 0, "x", "y");
        renameArg(gem, 1, "z", "y_1");
        
        SourceElement element = createFunctionDefn("y y_1 y_2", code
            .replaceAll("%x", "y").replaceAll("%z", "y_1")
            .replaceAll("@y", "y_3"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    ////--------------------------------------
    /// Test references to top-level functions
    //
    
    public void testUnqualifiedReferenceToTopLevelFunction() {
        String code = "@abs %x";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%x", "x").replaceAll("@abs", "abs"));
        
        renameArg(gem, 0, "x", "abs");
        
        SourceElement element = createFunctionDefn("abs", code
            .replaceAll("%x", "abs").replaceAll("@abs", CAL_Prelude.Functions.abs.getQualifiedName()));
        
        assertFunctionTextEquals(gem, element);
    }

    public void testQualifiedReferenceToTopLevelFunction() {
        String code = CAL_Prelude.Functions.abs.getQualifiedName() + " %x";
        
        CodeGem gem = createCodeGem(code.replaceAll("%x", "x"));
        
        renameArg(gem, 0, "x", "abs");
        
        SourceElement element = createFunctionDefn("abs", code.replaceAll("%x", "abs"));
        
        assertFunctionTextEquals(gem, element);
    }

    ////-------------------
    /// All code gem issues
    //
    
    public void testAllCodeGemIssues() {
        String code =
            "let" +
            "    alpha = case %w of" +
            "    " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @a -> @a;" +
            "    ;" +
            "    @b = %x; @a = %y;" +
            "in" +
            "    " + CAL_Prelude.Functions.tuple3.getQualifiedName() + " @b @a (" +
            "    let" +
            "        @a :: " + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + ";" +
            "        @a = @b;" +
            "        b_1 = " + CAL_Prelude.DataConstructors.False.getQualifiedName() + ";" +
            "        beta = case %z of" +
            "        {_ |a@PUNa} -> @a;;" +
            "        gamma = case %z of" +
            "        {_ |b=@a} -> @a;;" +
            "        delta = case %z of" +
            "        {_ |c=@b} -> @b;;" +
            "        eta = case %z of" +
            "        {@b | foo} -> @b;;" +
            "        zeta = case (1,2,3) of" +
            "        (x, y, @z) -> (y, @z, x);;" +
            "        zeta2 = case (1,2,3) of" +
            "        (@a, @b, b_1) -> (@b, b_1, @a);;" +
            "        beta_lazy = let {_ |a@PUNa} = %z; in @a;\n" +
            "        gamma_lazy = let {_ |b=@a} = %z; in @a;\n" +
            "        delta_lazy = let {_ |c=@b} = %z; in @b;\n" +
            "        zeta_lazy = let (x, y, @z) = (1.0,2.0,3.0); in (y, @z, x);\n" +
            "        zeta2_lazy = let (@a, @b, b_1) = (1.0,2.0,3.0); in (@b, b_1, @a);\n" +
            "        epsilon_lazy = let Prelude.Cons y @z = ['a']; in (y, @z);\n" +
            "        epsilon2_lazy = let Prelude.Cons {head=@b, tail=b_1} = ['a']; in (@b, b_1);\n" +
            "        theta_lazy = let y:@z = ['a']; in (y, @z);\n" +
            "        theta2_lazy = let @b:b_1 = ['a']; in (@b, b_1);\n" +
            "    in" +
            "        " + CAL_Prelude.Functions.tuple5.getQualifiedName() + " @a @b %x %y (" +
            "        let" +
            "            /**\n" +
            "             * ##arg @a a\n" +
            "             * ##arg @b b\n" +
            "             */\n" +
            "            f @a @b = @a + @b + %x;" +
            "            g = \\@b -> \\@a -> @b - @a;" +
            "            /**\n" +
            "             * ##arg @a a\n" +
            "             */\n" +
            "            @a @a = { a = @a };" +
            "            thunk :: " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + ";" +
            "            thunk = " + CAL_Prelude.DataConstructors.True.getQualifiedName() + ";" +
            "        in" +
            "            let" +
            "                x = 3.0;" +
            "            in" +
            "                (x, " + CAL_Prelude.Functions.tuple4.getQualifiedName() + " %z (" +
            "                let" +
            "                    @z = 4;" +
            "                in" +
            "                    @z) (f 3 4) (@a (1 :: " + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + ")))))";
        
        CodeGem gem = createCodeGem(code
            .replaceAll("%w", "w").replaceAll("%x", "x")
            .replaceAll("%y", "y").replaceAll("%z", "z")
            .replaceAll("@a", "a").replaceAll("@PUNa", "")
            .replaceAll("@b", "b").replaceAll("@z", "z")
            .replaceAll("##", "@"));
        
        burnArg(gem, 0, "w");
        burnArg(gem, 1, "x");
        renameArg(gem, 0, "w", "z");
        renameArg(gem, 1, "x", "a");
        renameArg(gem, 2, "y", "a_1");
        renameArg(gem, 3, "z", "b");
        reorderArgs(gem, new int[] {3, 1, 2, 0});
        
        SourceElement element = createFunctionDefn("b a_1", "\\a z -> " + code
            .replaceAll("%w", "z").replaceAll("%x", "a")
            .replaceAll("%y", "a_1").replaceAll("%z", "b")
            .replaceAll("@a", "a_2").replaceAll("@PUNa", "=a_2")
            .replaceAll("@b", "b_2").replaceAll("@z", "z_1")
            .replaceAll("##", "@"));
        
        assertFunctionTextEquals(gem, element);
    }
    
    ////--------------------------------------
    /// Prelude.apply and Prelude.compose gems
    //
    
    /**
     * Tests the generation of code for the Prelude.apply gem where no inputs are burnt.
     */
    public void testApplyGem() {
        Gem applyGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        
        SourceElement element = createFunctionDefn("functionToApply argument", "functionToApply argument");
        
        assertFunctionTextEquals(applyGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.apply gem where the first input is burnt.
     */
    public void testApplyGem_BurntInput0() {
        Gem applyGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        burnArg(applyGem, 0, "functionToApply");
        
        SourceElement element = createFunctionDefn("argument", "\\functionToApply -> functionToApply argument");
        
        assertFunctionTextEquals(applyGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.apply gem where the second input is burnt.
     */
    public void testApplyGem_BurntInput1() {
        Gem applyGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        burnArg(applyGem, 1, "argument");
        
        SourceElement element = createFunctionDefn("functionToApply", "functionToApply");
        
        assertFunctionTextEquals(applyGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.apply gem where both inputs are burnt.
     */
    public void testApplyGem_BurntAllInputs() {
        Gem applyGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        burnArg(applyGem, 0, "functionToApply");
        burnArg(applyGem, 1, "argument");
        
        SourceElement element = createFunctionDefn("", CAL_Prelude.Functions.apply.getQualifiedName());
        
        assertFunctionTextEquals(applyGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.apply gem where its first
     * input is another Prelude.apply gem.
     */
    public void testApplyGem_ChainedOnInput0() {
        GemGraph gemGraph = new GemGraph();
        
        Gem applyGem1 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        gemGraph.addGem(applyGem1);
        
        Gem applyGem2 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        gemGraph.addGem(applyGem2);
        
        connect(gemGraph, applyGem2, applyGem1, 0, "functionToApply");
        
        SourceElement element = createFunctionDefn("functionToApply argument_1 argument_2", "functionToApply argument_1 argument_2");
        
        assertFunctionTextEquals(applyGem1, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.apply gem where its second
     * input is another Prelude.apply gem.
     */
    public void testApplyGem_ChainedOnInput1() {
        GemGraph gemGraph = new GemGraph();
        
        Gem applyGem1 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        gemGraph.addGem(applyGem1);
        
        Gem applyGem2 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        gemGraph.addGem(applyGem2);
        
        connect(gemGraph, applyGem2, applyGem1, 1, "argument");
        
        SourceElement element = createFunctionDefn("functionToApply_1 functionToApply_2 argument", "functionToApply_1 (functionToApply_2 argument)");
        
        assertFunctionTextEquals(applyGem1, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where no inputs are burnt.
     */
    public void testComposeGem() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        
        SourceElement element = createFunctionDefn("f g x", "(f # g) x");
        
        assertFunctionTextEquals(composeGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where the first input is burnt.
     */
    public void testComposeGem_BurntInput0() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        burnArg(composeGem, 0, "f");
        
        SourceElement element = createFunctionDefn("g x", "\\f -> (f # g) x");
        
        assertFunctionTextEquals(composeGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where the second input is burnt.
     */
    public void testComposeGem_BurntInput1() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        burnArg(composeGem, 1, "g");
        
        SourceElement element = createFunctionDefn("f x", "\\g -> (f # g) x");
        
        assertFunctionTextEquals(composeGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where the third input is burnt.
     */
    public void testComposeGem_BurntInput2() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        burnArg(composeGem, 2, "x");
        
        SourceElement element = createFunctionDefn("f g", "f # g");
        
        assertFunctionTextEquals(composeGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where the first and second inputs are burnt.
     */
    public void testComposeGem_BurntInputs0_and_1() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        burnArg(composeGem, 0, "f");
        burnArg(composeGem, 1, "g");
        
        SourceElement element = createFunctionDefn("x", "\\f g -> (f # g) x");
        
        assertFunctionTextEquals(composeGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where the first and third inputs are burnt.
     */
    public void testComposeGem_BurntInputs0_and_2() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        burnArg(composeGem, 0, "f");
        burnArg(composeGem, 2, "x");
        
        SourceElement element = createFunctionDefn("g", "\\f -> f # g");
        
        assertFunctionTextEquals(composeGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where the second and third inputs are burnt.
     */
    public void testComposeGem_BurntInputs1_and_2() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        burnArg(composeGem, 1, "g");
        burnArg(composeGem, 2, "x");
        
        SourceElement element = createFunctionDefn("f", CAL_Prelude.Functions.compose.getQualifiedName() + " f");
        
        assertFunctionTextEquals(composeGem, element);
    }

    /**
     * Tests the generation of code for the Prelude.compose gem where all three inputs are burnt.
     */
    public void testComposeGem_BurntAllInputs() {
        Gem composeGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        burnArg(composeGem, 0, "f");
        burnArg(composeGem, 1, "g");
        burnArg(composeGem, 2, "x");
        
        SourceElement element = createFunctionDefn("", CAL_Prelude.Functions.compose.getQualifiedName());
        
        assertFunctionTextEquals(composeGem, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where its first
     * input is another Prelude.compose gem whose third input is burnt.
     */
    public void testComposeGem_ChainedOnInput0() {
        GemGraph gemGraph = new GemGraph();
        
        Gem composeGem1 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        gemGraph.addGem(composeGem1);
        
        Gem composeGem2 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        gemGraph.addGem(composeGem2);
        burnArg(composeGem2, 2, "x");
        
        connect(gemGraph, composeGem2, composeGem1, 0, "f");
        
        SourceElement element = createFunctionDefn("f g_1 g_2 x_2", "((f # g_1) # g_2) x_2");
        
        assertFunctionTextEquals(composeGem1, element);
    }
    
    /**
     * Tests the generation of code for the Prelude.compose gem where its second
     * input is another Prelude.compose gem whose third input is burnt.
     */
    public void testComposeGem_ChainedOnInput1() {
        GemGraph gemGraph = new GemGraph();
        
        Gem composeGem1 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        gemGraph.addGem(composeGem1);
        
        Gem composeGem2 = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.compose);
        gemGraph.addGem(composeGem2);
        burnArg(composeGem2, 2, "x");
        
        connect(gemGraph, composeGem2, composeGem1, 1, "g");
        
        SourceElement element = createFunctionDefn("f_1 f_2 g x_2", "(f_1 # f_2 # g) x_2");
        
        assertFunctionTextEquals(composeGem1, element);
    }
}
