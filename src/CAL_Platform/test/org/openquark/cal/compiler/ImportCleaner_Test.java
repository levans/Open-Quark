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
 * ImportCleaner_Test.java
 * Creation date: (Feb 14, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Bits;
import org.openquark.cal.module.Cal.Core.CAL_Char;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.CALWorkspace;


/**
 * Tests for the ImportCleaner functionality.
 * 
 * For convenience, these tests usually include only a portion of the relevant
 * supporting CAL modules as their "module text".
 * 
 * @author James Wright
 */
public class ImportCleaner_Test extends TestCase {

    /** A cached reference to the CAL test services */
    private static BasicCALServices leccCALServices;
    
    /** A cached reference to a compiled CAL workspace */
    private static CALWorkspace workspace;
    
    /**
     * @return a test suite containing all the test cases for testing CAL import refactoring
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(ImportCleaner_Test.class);

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
        workspace = leccCALServices.getCALWorkspace();
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        workspace = null;
    }
    
    /**
     * Simple test that functions with no unqualified references are properly removed from
     * import-using clauses.
     *
     */
    public void testRedundantUsingFunctionRemoval() {
        
        String origModuleText = 
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + ";\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = head, tail, subList;\n" +
            "    ;\n" +
            "/* a comment */\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;";
        
        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support1, origModuleText, false, logger); 
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        
        String newModuleText = sourceModifier.apply(origModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + ";\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = tail;\n" +
            "    ;\n" +
            "/* a comment */\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;",
            newModuleText);

        String undoneModuleText = sourceModifier.undo(newModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(origModuleText, undoneModuleText);
    }

    /**
     * Test that comments are preserved in unmodified import statements.
     */
    public void testCommentPreservation() {
        
        String origModuleText = 
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "/* comment 1*/ import /* comment 2 */ " + CAL_Prelude.MODULE_NAME + ";\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = head, tail, subList;\n" +
            "    ;\n" +
            "/* a comment 3*/\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;";
        
        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support1, origModuleText, false, logger); 
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        
        String newModuleText = sourceModifier.apply(origModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "/* comment 1*/ import /* comment 2 */ " + CAL_Prelude.MODULE_NAME + ";\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = tail;\n" +
            "    ;\n" +
            "/* a comment 3*/\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;",
            newModuleText);
        
        String undoneModuleText = sourceModifier.undo(newModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(undoneModuleText, origModuleText);
    }
    
    /**
     * Test that we don't introduce any changes when no imports need to be altered.
     *
     */
    public void testNullRefactoring() {
        String origModuleText =
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import /*comment */ " + CAL_Prelude.MODULE_NAME + ";\n" +
            "foo = 55;";
        
        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support1, origModuleText, false, logger); 
        
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        assertEquals(0, sourceModifier.getNSourceModifications());
        
        String newModuleText = sourceModifier.apply(origModuleText);
        assertEquals(newModuleText, origModuleText);
        
        String undoneModuleText = sourceModifier.undo(newModuleText);
        assertEquals(origModuleText, undoneModuleText);
    }
    
    /**
     * Test that the removal of entire import statements works.
     *
     */
    public void testImportRemoval() {
        String origModuleText =
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support2 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + ";\n" +
            "import " + CAL_List.MODULE_NAME + ";\n" +
            "import " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + " using\n" +
            "    function = exportedFunction;  /* this whole import should vanish */\n" +
            "    typeClass = ExportedClass;\n" +
            "    ;\n" +
            "import " + CAL_Bits.MODULE_NAME + "; // This should stay, since Bits contains otherwise-invisible instances\n" +
            "import " + CAL_Char.MODULE_NAME + "; // Char imports Debug, which contains otherwise-invisible instances once " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + " is gone\n" +
            "doStrictFold = " + CAL_List.MODULE_NAME + ".foldLeftStrict;\n";
        
        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support2, origModuleText, false, logger); 
        
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        
        String newModuleText = sourceModifier.apply(origModuleText);
        assertEquals(
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support2 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + ";\n" +
            "import " + CAL_List.MODULE_NAME + ";\n" +
            "\n" +
            "import " + CAL_Bits.MODULE_NAME + "; // This should stay, since Bits contains otherwise-invisible instances\n" +
            "import " + CAL_Char.MODULE_NAME + "; // Char imports Debug, which contains otherwise-invisible instances once " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + " is gone\n" +
            "doStrictFold = " + CAL_List.MODULE_NAME + ".foldLeftStrict;\n",
            newModuleText);
    }
    
    /**
     * Test import cleanup and multi-line formatting and grouping of multiple
     * items into a single item.
     *
     */
    public void testImportUsingCleanupWithGrouping() {
        String origModuleText = 
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "function = concat, equals, error, fromJust, fst, intToString, isNothing, isEmpty;\n" +
            "function = max, not, output, seq, snd, field1, field2, field3, upFrom, upFromTo, fromMaybe, notANumber, isNotANumber, isJust, abs, multiply, min, field4;\n" +
            "dataConstructor =\n" + 
            "    Cons, EQ, False, GT, Just, LT, Left, Nil, Nothing, Right, True;" +
            "    ;\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = head, tail, subList;\n" +
            "    ;\n" +
            "/* a comment */\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;" +
            "private referencePreludeFunctions =\n" + 
            "    (concat, equals, error, fromJust, fst, intToString, isNothing, isEmpty, max, not, output, seq, snd, field1, field2, field3, upFrom, upFromTo, fromMaybe, notANumber, isNotANumber, isJust, abs, multiply, min, field4);" +
            "private referencePreludeDataconses =" + 
            "    (False, True, LT, EQ, GT, Nil, Cons, Nothing, Just, Left, Right);";

        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support1, origModuleText, false, logger); 
        
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        
        String newModuleText = sourceModifier.apply(origModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    dataConstructor =\n" +
            "        False, True, Left, Right, Nil, Cons, Nothing, Just, LT, EQ, GT;\n" +
            "    function =\n" +
            "        abs, concat, equals, error, field1, field2, field3, field4, fromJust,\n" +
            "        fromMaybe, fst, intToString, isEmpty, isJust, isNotANumber, isNothing,\n" +
            "        max, min, multiply, not, notANumber, output, seq, snd, upFrom, upFromTo;\n" +
            "    ;\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = tail;\n" +
            "    ;\n" +
            "/* a comment */\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;" +
            "private referencePreludeFunctions =\n" + 
            "    (concat, equals, error, fromJust, fst, intToString, isNothing, isEmpty, max, not, output, seq, snd, field1, field2, field3, upFrom, upFromTo, fromMaybe, notANumber, isNotANumber, isJust, abs, multiply, min, field4);" +
            "private referencePreludeDataconses =" + 
            "    (False, True, LT, EQ, GT, Nil, Cons, Nothing, Just, Left, Right);",
            newModuleText);
    }

    /**
     * Test that the preserve-items option does in fact preserve items, except those that would
     * become empty.
     *
     */
    public void testImportUsingCleanupWithoutGrouping() {
        String origModuleText = 
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "function = negate;\n" +
            "function = concat, equals, error, fromJust, fst, intToString, isNothing, isEmpty;\n" +
            "function = max, not, output, seq, snd, field1, field2, field3, upFrom, upFromTo, fromMaybe, notANumber, isNotANumber, isJust, abs, multiply, min, field4;\n" +
            "dataConstructor = Cons, EQ, False, GT, Just, LT, Left, Nil, Nothing, Right, True;" +
            "    ;\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = head, tail, subList;\n" +
            "    ;\n" +
            "/* a comment */\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;" +
            "private referencePreludeFunctions =\n" + 
            "    (concat, equals, error, fromJust, fst, intToString, isNothing, isEmpty, max, not, output, seq, snd, field1, field2, field3, upFrom, upFromTo, fromMaybe, notANumber, isNotANumber, isJust, abs, multiply, min, field4);" +
            "private referencePreludeDataconses =" + 
            "    (False, True, LT, EQ, GT, Nil, Cons, Nothing, Just, Left, Right);";

        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support1, origModuleText, true, logger); 
        
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        
        String newModuleText = sourceModifier.apply(origModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    function =\n" +
            "        concat, equals, error, fromJust, fst, intToString, isEmpty, isNothing;\n" +
            "    function =\n" +
            "        abs, field1, field2, field3, field4, fromMaybe, isJust, isNotANumber,\n" +
            "        max, min, multiply, not, notANumber, output, seq, snd, upFrom, upFromTo;\n" +
            "    dataConstructor =\n" +
            "        False, True, Left, Right, Nil, Cons, Nothing, Just, LT, EQ, GT;\n" +
            "    ;\n" +
            "import " + CAL_List.MODULE_NAME + " using\n" +
            "    function = tail;\n" +
            "    ;\n" +
            "/* a comment */\n" +
            "listRoundTrip x = " + CAL_List.MODULE_NAME + ".head x : tail x;" +
            "private referencePreludeFunctions =\n" + 
            "    (concat, equals, error, fromJust, fst, intToString, isNothing, isEmpty, max, not, output, seq, snd, field1, field2, field3, upFrom, upFromTo, fromMaybe, notANumber, isNotANumber, isJust, abs, multiply, min, field4);" +
            "private referencePreludeDataconses =" + 
            "    (False, True, LT, EQ, GT, Nil, Cons, Nothing, Just, Left, Right);",
            newModuleText);
    }
    
    /**
     * Test that unqualified references to data constructors are not sufficient to prevent
     * import-using entries for type constructors of the same name from being removed.
     *
     */
    public void testImportUsingConsNameDistinction1() {
        String origModuleText = 
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "dataConstructor = Unit;\n" +
            "typeConstructor = Unit;\n" +
            "    ;\n" +
            "refDataCons = Unit;";
        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support1, origModuleText, true, logger); 
        
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        
        String newModuleText = sourceModifier.apply(origModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    dataConstructor = Unit;\n" +
            "    ;\n" +
            "refDataCons = Unit;",
            newModuleText);
    }
    
    /**
     * Test that unqualified references to type constructors are not sufficient to prevent
     * import-using entries for data constructors of the same name from being removed.
     *
     */
    public void testImportUsingConsNameDistinction2() {
        String origModuleText = 
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "dataConstructor = Unit;\n" +
            "typeConstructor = Unit;\n" +
            "    ;\n" +
            "refTypeCons :: Unit;\n" +
            "refTypeCons = ();";
        CompilerMessageLogger logger = new MessageLogger();
        SourceModifier sourceModifier = ImportCleaner.getSourceModifier_cleanImports(workspace.asModuleContainer(), CALPlatformTestModuleNames.ImportCleaner_Test_Support1, origModuleText, true, logger); 
        
        assertTrue(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        
        String newModuleText = sourceModifier.apply(origModuleText);
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(
            "/** module-level comments */\n" +
            "module " + CALPlatformTestModuleNames.ImportCleaner_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + " using\n" +
            "    typeConstructor = Unit;\n" +
            "    ;\n" +
            "refTypeCons :: Unit;\n" +
            "refTypeCons = ();",
            newModuleText);
    }
    
}
