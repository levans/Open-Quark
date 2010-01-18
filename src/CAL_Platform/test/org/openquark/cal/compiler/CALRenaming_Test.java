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
 * CALRenaming_Test.java
 * Creation date: Feb 28, 2005.
 * By: Peter Cardwell
 */
package org.openquark.cal.compiler;

import java.util.Collections;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.WorkspaceManager;


/**
 * A set of quick unit tests to ensure that the renaming functionality works.
 * @author Peter Cardwell
 */
public class CALRenaming_Test extends TestCase {
    
    private static final ModuleName Prelude = CAL_Prelude.MODULE_NAME;
    private static final ModuleName CALRenaming_Test_Support1 = CALPlatformTestModuleNames.CALRenaming_Test_Support1;
    private static final ModuleName CALRenaming_Test_Support2 = CALPlatformTestModuleNames.CALRenaming_Test_Support2;
    
    /**
     * A copy of CAL services for use in the test cases
     */
    private static BasicCALServices leccServices;
    
    /**
     * Constructor for CALRenaming_Test.
     * 
     * @param name
     *            the name of the test.
     */
     public CALRenaming_Test(String name) {
       super(name);
     }
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(CALRenaming_Test.class);

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
        leccServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws"); 
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccServices = null;
    }
    
    public void testBasicFunctionRenaming() {
        String sourceText1 =
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "foo :: " + Prelude + ".Num a => a -> a -> a;\n" +
            "public foo x = " + Prelude + ".add x;\n" +
            "refIntToOrdering = intToOrdering;";
        String sourceText2 = 
            "module " + CALRenaming_Test_Support2 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "import " + CALRenaming_Test_Support1 + " using\n" +
            "    function = foo;\n" +
            "    ;\n" +
            "qualifiedFooReference = " + CALRenaming_Test_Support1 + ".foo;\n" +
            "unqualifiedFooReference = foo;\n" +
            "boundBarReference bar =\n" +
            "   foo bar;\n" +
            "intToOrderingReference = intToOrdering;";
        
        WorkspaceManager workspaceManager = leccServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleTypeInfo moduleTypeInfo1 = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support1);
        ModuleTypeInfo moduleTypeInfo2 = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support2);
        
        QualifiedName oldName = QualifiedName.make(CALRenaming_Test_Support1, "foo");
        QualifiedName newName = QualifiedName.make(CALRenaming_Test_Support1, "bar");
        
        SourceModifier renamer1 = IdentifierRenamer.getSourceModifier(moduleTypeInfo1, sourceText1, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        
        SourceModifier renamer2 = IdentifierRenamer.getSourceModifier(moduleTypeInfo2, sourceText2, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        
        String newSourceText1 = renamer1.apply(sourceText1); 
        assertEquals(
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "bar :: " + Prelude + ".Num a => a -> a -> a;\n" +
            "public bar x = " + Prelude + ".add x;\n" +
            "refIntToOrdering = intToOrdering;",
            newSourceText1);

        String newSourceText2 = renamer2.apply(sourceText2);
        assertEquals(
            "module " + CALRenaming_Test_Support2 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "import " + CALRenaming_Test_Support1 + " using\n" +
            "    function = bar;\n" +
            "    ;\n" +
            "qualifiedFooReference = " + CALRenaming_Test_Support1 + ".bar;\n" +
            "unqualifiedFooReference = bar;\n" +
            "boundBarReference bar =\n" +
            "   " + CALRenaming_Test_Support1 + ".bar bar;\n" +
            "intToOrderingReference = intToOrdering;",
            newSourceText2);
    }

    /** 
     * Check that we automatically deal with a renaming that changes one using symbol 
     * to conflict with another (of the same type) 
     */
    public void testImportConflict1() {
        String sourceText = 
            "module " + CALRenaming_Test_Support2 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "import " + CALRenaming_Test_Support1 + " using\n" +
            "    function = foo;\n" +
            "    ;\n" +
            "qualifiedFooReference = " + CALRenaming_Test_Support1 + ".foo;\n" +
            "unqualifiedFooReference = foo;\n" +
            "boundBarReference bar =\n" +
            "   foo bar;\n" +
            "intToOrderingReference = intToOrdering;";
        
        WorkspaceManager workspaceManager = leccServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support2);
        
        QualifiedName oldName = QualifiedName.make(CALRenaming_Test_Support1, "foo");
        QualifiedName newName = QualifiedName.make(CALRenaming_Test_Support1, "intToOrdering");
        
        SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        
        String newSourceText = renamer.apply(sourceText);
        assertEquals(
            "module " + CALRenaming_Test_Support2 + ";\n" +
            "import " + Prelude + ";\n" +
            "import " + CALRenaming_Test_Support1 + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "qualifiedFooReference = " + CALRenaming_Test_Support1 + ".intToOrdering;\n" +
            "unqualifiedFooReference = intToOrdering;\n" +
            "boundBarReference bar =\n" +
            "   intToOrdering bar;\n" +
            "intToOrderingReference = " + Prelude + ".intToOrdering;",
            newSourceText);
    }
    
    /**
     * Check that we deal properly with renaming a function to conflict with a name that
     * is in a using clause.
     */
    public void testImportConflict2() {
        String sourceText =
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "foo :: " + Prelude + ".Num a => a -> a -> a;\n" +
            "public foo x = " + Prelude + ".add x;\n" +
            "refIntToOrdering = intToOrdering;";

        WorkspaceManager workspaceManager = leccServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support1);
        
        QualifiedName oldName = QualifiedName.make(CALRenaming_Test_Support1, "foo");
        QualifiedName newName = QualifiedName.make(CALRenaming_Test_Support1, "intToOrdering");
        
        SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        
        String newSourceText = renamer.apply(sourceText); 
        assertEquals(
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + ";\n" +
            "intToOrdering :: " + Prelude + ".Num a => a -> a -> a;\n" +
            "public intToOrdering x = " + Prelude + ".add x;\n" +
            "refIntToOrdering = " + Prelude + ".intToOrdering;",
            newSourceText);
    }
    
    /**
     * Check that we deal properly with renaming an identifier that is in a using-clause to
     * the same name as a top-level declaration in this module.
     */
    public void testImportConflict3() {
        String sourceText =
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "foo :: " + Prelude + ".Num a => a -> a -> a;\n" +
            "public foo x = " + Prelude + ".add x;\n" +
            "refIntToOrdering = intToOrdering;";

        WorkspaceManager workspaceManager = leccServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support1);
        
        QualifiedName oldName = CAL_Prelude.Functions.intToOrdering;
        QualifiedName newName = QualifiedName.make(Prelude, "foo");
        
        SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        
        String newSourceText = renamer.apply(sourceText); 
        assertEquals(
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + ";\n" +
            "foo :: " + Prelude + ".Num a => a -> a -> a;\n" +
            "public foo x = " + Prelude + ".add x;\n" +
            "refIntToOrdering = " + Prelude + ".foo;",
            newSourceText);
    }

    /** 
     * Tests that we deal properly with a conflict that gets introduced when
     * a contextless CALDoc cons reference is rendered ambiguous by renaming
     * a different kind of cons to have the same name. 
     */
    public void testCALDocConflict() {
        String sourceText =
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    dataConstructor = Cons;" +
            "    ;\n" +
            "data Alpha =\n" +
            "    A | B | C;\n" +
            "\n" +
            "/** @see module = " + CALRenaming_Test_Support1 + " */\n" +            
            "foobar :: " + Prelude + ".Double;\n" +
            "private foobar = let /** @see module = " + CALRenaming_Test_Support1 + " */ foo = 7.0; /** @see module = " + CALRenaming_Test_Support1 + " */ bar :: " + Prelude + ".Int; bar = 7; in foo;\n" +
            "/**\n" +
            " * @see Alpha, Beta, Gamma, Cons, foobar\n" +
            " */\n" +
            "data Beta =\n" +
            "    Gamma;\n";        

        WorkspaceManager workspaceManager = leccServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support1);
        
        QualifiedName oldName = QualifiedName.make(CALRenaming_Test_Support1, "Gamma");
        QualifiedName newName = QualifiedName.make(CALRenaming_Test_Support1, "Alpha");
        
        SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, Category.DATA_CONSTRUCTOR, logger);
        assertNoCompilationErrors(logger);
        
        String newSourceText = renamer.apply(sourceText); 
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    dataConstructor = Cons;" +
            "    ;\n" +
            "data Alpha =\n" +
            "    A | B | C;\n" +
            "\n" +
            "/** @see module = " + CALRenaming_Test_Support1 + " */\n" +            
            "foobar :: " + Prelude + ".Double;\n" +
            "private foobar = let /** @see module = " + CALRenaming_Test_Support1 + " */ foo = 7.0; /** @see module = " + CALRenaming_Test_Support1 + " */ bar :: " + Prelude + ".Int; bar = 7; in foo;\n" +
            "/**\n" +
            " * @see typeConstructor = Alpha\n" +
            " * @see Beta\n" +
            " * @see dataConstructor = Alpha\n" +
            " * @see Cons\n" +
            " * @see foobar\n" +
            " * \n" +
            " */\n" +
            "data Beta =\n" +
            "    Alpha;\n",        
            newSourceText);
    }
    
    public void testImportAndCALDocConflict1() {
        String sourceText =
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    dataConstructor = Cons;" +
            "    ;\n" +
            "data Alpha =\n" +
            "    A | B | C;\n" +
            "\n" +
            "/** @see module = " + CALRenaming_Test_Support1 + " */\n" +            
            "foobar :: " + Prelude + ".Double;\n" +
            "private foobar = let /** @see module = " + CALRenaming_Test_Support1 + " */ foo = 7.0; /** @see module = " + CALRenaming_Test_Support1 + " */ bar :: " + Prelude + ".Int; bar = 7; in foo;\n" +
            "/**\n" +
            " * @see Alpha, Beta, Gamma, Cons, foobar\n" +
            " */\n" +
            "data Beta =\n" +
            "    Gamma;\n";        

        WorkspaceManager workspaceManager = leccServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support1);
        
        QualifiedName oldName = QualifiedName.make(CALRenaming_Test_Support1, "Gamma");
        QualifiedName newName = QualifiedName.make(CALRenaming_Test_Support1, "Cons");
        
        SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, Category.DATA_CONSTRUCTOR, logger);
        assertNoCompilationErrors(logger);
        
        String newSourceText = renamer.apply(sourceText); 
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "data Alpha =\n" +
            "    A | B | C;\n" +
            "\n" +
            "/** @see module = " + CALRenaming_Test_Support1 + " */\n" +            
            "foobar :: " + Prelude + ".Double;\n" +
            "private foobar = let /** @see module = " + CALRenaming_Test_Support1 + " */ foo = 7.0; /** @see module = " + CALRenaming_Test_Support1 + " */ bar :: " + Prelude + ".Int; bar = 7; in foo;\n" +
            "/**\n" +
            " * @see Alpha\n" +
            " * @see Beta\n" +
            " * @see dataConstructor = Cons\n" +
            " * @see dataConstructor = Cons\n" + //TODOJ
            " * @see foobar\n" +
            " * \n" +
            " */\n" +
            "data Beta =\n" +
            "    Cons;\n",        
            newSourceText);
    }
    public void testImportAndCALDocConflict2() {
        String sourceText =
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    dataConstructor = Cons;" +
            "    ;\n" +
            "data Alpha =\n" +
            "    A | B | C;\n" +
            "\n" +
            "/** @see module = " + CALRenaming_Test_Support1 + " */\n" +            
            "foobar :: " + Prelude + ".Double;\n" +
            "private foobar = let /** @see module = " + CALRenaming_Test_Support1 + " */ foo = 7.0; /** @see module = " + CALRenaming_Test_Support1 + " */ bar :: " + Prelude + ".Int; bar = 7; in foo;\n" +
            "/**\n" +
            " * @see Alpha, Beta, Gamma, Cons, foobar\n" +
            " */\n" +
            "data Beta =\n" +
            "    Gamma;\n";        

        WorkspaceManager workspaceManager = leccServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CALRenaming_Test_Support1);
        
        QualifiedName oldName = QualifiedName.make(CALRenaming_Test_Support1, "Alpha");
        QualifiedName newName = QualifiedName.make(CALRenaming_Test_Support1, "Cons");
        
        SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, sourceText, oldName, newName, Category.TYPE_CONSTRUCTOR, logger);
        assertNoCompilationErrors(logger);
        
        String newSourceText = renamer.apply(sourceText); 
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    dataConstructor = Cons;" +
            "    ;\n" +
            "data Cons =\n" +
            "    A | B | C;\n" +
            "\n" +
            "/** @see module = " + CALRenaming_Test_Support1 + " */\n" +            
            "foobar :: " + Prelude + ".Double;\n" +
            "private foobar = let /** @see module = " + CALRenaming_Test_Support1 + " */ foo = 7.0; /** @see module = " + CALRenaming_Test_Support1 + " */ bar :: " + Prelude + ".Int; bar = 7; in foo;\n" +
            "/**\n" +
            " * @see typeConstructor = Cons\n" +
            " * @see Beta\n" +
            " * @see Gamma\n" +
            " * @see dataConstructor = Cons\n" +
            " * @see foobar\n" +
            " * \n" +
            " */\n" +
            "data Beta =\n" +
            "    Gamma;\n",        
            newSourceText);
    }
    
    /**
     * This is an example adapted from the testAllCodeGemIssues method in the
     * CALSourceGenerator_Test class in the Quark Gems project. It is intended to ensure
     * that the renaming occurs properly for all known issues that arise in renaming
     * of basic expressions. Most importantly this includes the handling of naming 
     * conflicts involving punned fields.
     */
    public void testAllCodeGemIssuesExpression() {
        final String moduleTemplate =
            "module " + CALRenaming_Test_Support1 + ";\n" +
            "import " + Prelude + " using\n" +
            "    function = intToOrdering;\n" +
            "    ;\n" +
            "%w :: " + Prelude + ".Maybe " + Prelude + ".Int;\n" +
            "%w = " + Prelude + ".Nothing;\n" +
            "%x :: " + Prelude + ".Int;\n" +
            "%x = 55;\n" +
            "%y :: " + Prelude + ".Int;\n" +
            "%y = 22;\n" +
            "%z :: {a :: " + Prelude + ".Int, b :: " + Prelude + ".Int, c :: " + Prelude + ".Int, foo :: " + Prelude + ".Int};\n" +
            "%z = {a = 10, b = 20, c = 30, foo=99};\n" +
            "data TestType = %DataCons x :: Prelude.Int y :: Prelude.Int z :: Prelude.Int;" +
            "\n" +
            "codeGemIssuesFunction =\n" +
            "    let\n" +
            "        alpha = case %w of\n" +
            "        " + Prelude + ".Just a -> a;\n" +
            "        ;\n" +
            "        b = @x; a = %y;\n" +
            "    in\n" +
            "        " + Prelude + ".tuple3 b a (\n" +
            "        let\n" +
            "            a = b;\n" +
            "            b_1 = " + Prelude + ".False;\n" +
            "            beta = case @z of\n" +
            "            {_ |a} -> a + @x;;\n" +
            "            gamma = case @z of\n" +
            "            {_ |b = a} -> a;;\n" +
            "            delta = case @z of\n" +
            "            {_ |c = b} -> b;;\n" +
            "            eta = case @z of\n" +
            "            {b | foo} -> b;;\n" +
            "            zeta = case (1,2,3) of\n" +
            "            (x, y, z) -> (y, z, x);;\n" +
            "            zeta2 = case (1,2,3) of\n" +
            "            (a, b, b1) -> (b, b1, a);;\n" +
            "            beta_lazy = let {_ |a} = @z; in a + @x;\n" +
            "            gamma_lazy = let {_ |b = a} = @z; in a;\n" +
            "            delta_lazy = let {_ |c = b} = @z; in b;\n" +
            "            zeta_lazy = let (x, y, z) = (1.0,2.0,3.0); in (y, z, x);\n" +
            "            zeta2_lazy = let (a, b, b1) = (1.0,2.0,3.0); in (b, b1, a);\n" +
            "            theta_lazy = let y:z = ['a']; in (y, z);\n" +
            "            theta2_lazy = let b:b_1 = ['a']; in (b, b_1);\n" +
            "            aleph_lazy = let %DataCons x y z = %DataCons 1 2 3; in %DataCons y z x;\n" +
            "            aleph2_lazy = let %DataCons {x=a, y=b, z=b1} = %DataCons 1 2 3; in %DataCons b b1 a;\n" +
            "        in\n" +
            "            " + Prelude + ".tuple5 a b @x %y (\n" +
            "            let\n" +
            "                /**\n" +
            "                 * @arg a a\n" +
            "                 * @arg b b\n" +
            "                 */\n" +
            "                f a b = a + b + @x;\n" +
            "                /**\n" +
            "                 * @arg a a\n" +
            "                 * @arg b b\n" +
            "                 */\n" +
            "                f2 a b = a + b + @x;\n" +
            "                g = \\b -> \\a -> b - a;\n" +
            "                a a = { a = a };\n" +
            "                thunk :: " + Prelude + ".Boolean;\n" +
            "                thunk = " + Prelude + ".True;\n" +
            "            in\n" +
            "                let\n" +
            "                    x = 3.0;\n" +
            "                in\n" +
            "                    (x, " + Prelude + ".tuple4 @z (\n" +
            "                    let\n" +
            "                        z = 4;\n" +
            "                    in\n" +
            "                        z) (f 3 4) (a (1 :: " + Prelude + ".Int)))));";

        final String oldSourceText = moduleTemplate
            .replaceAll("%w", "w").replaceAll("%x", "x")
            .replaceAll("%y", "y").replaceAll("%z", "z")
            .replaceAll("%DataCons", "DataCons")
            .replaceAll("@w", CALRenaming_Test_Support1 + ".w")
            .replaceAll("@x", CALRenaming_Test_Support1 + ".x")
            .replaceAll("@y", CALRenaming_Test_Support1 + ".y")
            .replaceAll("@z", CALRenaming_Test_Support1 + ".z");
            
        final String newSourceText = moduleTemplate
            .replaceAll("%w", "z").replaceAll("%x", "a")
            .replaceAll("%y", "a1").replaceAll("%z", "b")
            .replaceAll("%DataCons", "FooBar")
            .replaceAll("@w", CALRenaming_Test_Support1 + ".z")
            .replaceAll("@x", CALRenaming_Test_Support1 + ".a")
            .replaceAll("@y", CALRenaming_Test_Support1 + ".a1")
            .replaceAll("@z", CALRenaming_Test_Support1 + ".b");
        
        final ModuleName moduleName = CALRenaming_Test_Support1;
        final ModuleTypeInfo moduleTypeInfo = leccServices.getWorkspaceManager().getModuleTypeInfo(moduleName);
        CompilerMessageLogger logger = new MessageLogger();
        
        QualifiedName oldName;
        QualifiedName newName;
        String renamedSourceText = oldSourceText;
        SourceModifier sourceModifier;
        
        oldName = QualifiedName.make(moduleName, "z");
        newName = QualifiedName.make(moduleName, "temp");
        sourceModifier = IdentifierRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        renamedSourceText = sourceModifier.apply(renamedSourceText);
        
        
        oldName = QualifiedName.make(moduleName, "w");
        newName = QualifiedName.make(moduleName, "z");
        sourceModifier = IdentifierRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        renamedSourceText = sourceModifier.apply(renamedSourceText);
        
        oldName = QualifiedName.make(moduleName, "x");
        newName = QualifiedName.make(moduleName, "a");
        sourceModifier = IdentifierRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        renamedSourceText = sourceModifier.apply(renamedSourceText);
        
        oldName = QualifiedName.make(moduleName, "DataCons");
        newName = QualifiedName.make(moduleName, "FooBar");
        sourceModifier = IdentifierRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, oldName, newName, Category.DATA_CONSTRUCTOR, logger);
        assertNoCompilationErrors(logger);
        renamedSourceText = sourceModifier.apply(renamedSourceText);
        
        oldName = QualifiedName.make(moduleName, "y");
        newName = QualifiedName.make(moduleName, "a1");
        sourceModifier = IdentifierRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        renamedSourceText = sourceModifier.apply(renamedSourceText);
        
        oldName = QualifiedName.make(moduleName, "temp");
        newName = QualifiedName.make(moduleName, "b");
        sourceModifier = IdentifierRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, oldName, newName, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, logger);
        assertNoCompilationErrors(logger);
        renamedSourceText = sourceModifier.apply(renamedSourceText);
        
        assertEquals(newSourceText, renamedSourceText);
    }
    
    /**
     * A very basic test to make sure that module renaming works at least a little bit.
     */
    public void testRenameBasicModule() {
        final ModuleName oldModuleName = CALRenaming_Test_Support1;
        final ModuleName newModuleName = ModuleName.make("After_Renaming");
        final String moduleCodeTemplate = 
            "/** @see module = %moduleName */\n" +
            "module %moduleName;\n" +
            "import " + Prelude + ";\n" +
            "\n" +
            "/** @see module = %moduleName */\n" +            
            "public someFunction = 6.0;\n" +
            "\n" +
            "/** {@link module = %moduleName@}\n\n\n{@orderedList {@item {@unorderedList {@item {@summary {@code {@em {@link function = %moduleName.someFunction @} @} @} @} @} @} @} @}*/\n" +            
            "public someFunction2 = 6.0;\n" +
            "\n" +
            "/** {@link " + Prelude + ".Maybe@} */\n" +            
            "public someFunction3 = 6.0;\n" +
            "\n" +
            "/** @see " + Prelude + ".id */\n" +            
            "public someFunction4 = 6.0;\n" +
            "\n" +
            "/** @see module = %moduleName */\n" +            
            "foobar :: " + Prelude + ".Double;\n" +
            "private foobar = let /** @see module = %moduleName */ foo = 7.0; /** @see module = %moduleName */ bar :: " + Prelude + ".Int; bar = 7; in foo;";
        
        final String moduleCode = moduleCodeTemplate.replaceAll("%moduleName", oldModuleName.toSourceText());
        final String expectedRenamedModuleCode = moduleCodeTemplate.replaceAll("%moduleName", newModuleName.toSourceText());
        
        ModuleTypeInfo moduleTypeInfo = leccServices.getWorkspaceManager().getModuleTypeInfo(oldModuleName);
        CompilerMessageLogger logger = new MessageLogger();
        
        QualifiedName oldName = QualifiedName.make(oldModuleName, Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
        QualifiedName newName = QualifiedName.make(newModuleName, Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
        
        SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, moduleCode, oldName, newName, Category.MODULE_NAME, logger);
        String renamedModuleCode = renamer.apply(moduleCode);
        assertEquals(expectedRenamedModuleCode, renamedModuleCode);   
    }

    private void assertNoCompilationErrors(final CompilerMessageLogger logger) {
        assertEquals("Compilation error", Collections.EMPTY_LIST, logger.getCompilerMessages(Severity.ERROR));
    }
}
