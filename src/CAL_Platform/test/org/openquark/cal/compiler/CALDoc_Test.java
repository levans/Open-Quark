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
 * CALDoc_Test.java
 * Creation date: Nov 28, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.StringModuleSourceDefinition;
import org.openquark.cal.services.WorkspaceManager;


/**
 * A set of JUnit test cases for verifying the correctness of the CALDoc processing in the compiler.
 *
 * @author Joseph Wong
 */
public class CALDoc_Test extends TestCase {


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
     * @return a test suite containing all the test cases.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(CALDoc_Test.class);

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
     * Tests that an inline block missing a closing tag generates an appropriate error.
     */
    public void testInlineTagBlockMissingClosingTagInCALDocComment() {
        /*
         * The test with the module level comment is a regression test for a bug in the CAL grammar's source range handling
         * (calling ParseTreeNode.addOmittedDelimiter() with a null subtree returned by the rule that failed
         * to match the closing tag).
         */
        testWithFunctionAndModuleCALDocComments("/** {@em foo */", MessageKind.Error.InlineTagBlockMissingClosingTagInCALDocComment.class);
    }

    /**
     * Tests with a function level CALDoc comment.
     * @param comment the comment to test.
     * @param errorClass the expected error.
     */
    private void testWithFunctionCALDocComment(String comment, Class<? extends MessageKind.Error> errorClass) {
        CompilerTestUtilities.checkDefnForExpectedError(comment + " bar = 3.0;", errorClass, leccCALServices);
    }

    /**
     * Tests with a module level CALDoc comment.
     * @param comment the comment to test.
     * @param errorClass the expected error.
     */
    private void testWithModuleCALDocComment(String comment, Class<? extends MessageKind.Error> errorClass) {
        ModuleName tempModuleName = CompilerTestUtilities.getNewModuleName(leccCALServices);
        String sourceDefString = comment + " module " + tempModuleName + "; import " + CAL_Prelude.MODULE_NAME + ";";
        
        ModuleSourceDefinition sourceDef = new StringModuleSourceDefinition(tempModuleName, sourceDefString);
        
        // Get the workspace manager and the logger.
        WorkspaceManager workspaceManager = leccCALServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();
        
        // Compile the module.
        workspaceManager.makeModule(sourceDef, logger);
        
        // Remove the module.
        leccCALServices.getWorkspaceManager().removeModule(tempModuleName, new Status("Remove module status."));
        
        // Check that we got an error message of the expected type.
        CompilerMessage error = logger.getFirstError();
        Assert.assertTrue("No errors logged.", error != null);
        Assert.assertEquals(errorClass, error.getMessageKind().getClass());
    }
    
    /**
     * Tests with both a module level CALDoc comment and a function level CALDoc comment.
     * @param comment the comment to test.
     * @param errorClass the expected error.
     */
    private void testWithFunctionAndModuleCALDocComments(String comment, Class<? extends MessageKind.Error> errorClass) {
        testWithFunctionCALDocComment(comment, errorClass);
        testWithModuleCALDocComment(comment, errorClass);
    }
    
    /**
     * Tests that an inline block appearing in an inappropriate location generates an appropriate error.
     */
    public void testInlineTagCannotAppearHereInsideCALDocCommentInlineTag() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@url {@em http://www.businessobjects.com @}@} */",
            MessageKind.Error.InlineTagCannotAppearHereInsideCALDocCommentInlineTag.class);
    }
    
    /**
     * Tests that an inline block appearing in an inappropriate location generates an appropriate error.
     */
    public void testThisParticularInlineTagCannotAppearHereInsideCALDocCommentInlineTag() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@summary {@unorderedList {@item foo@} @}@} */",
            MessageKind.Error.ThisParticularInlineTagCannotAppearHereInsideCALDocCommentInlineTag.class);
    }
    
    /**
     * Tests that a paragraph break appearing in an inappropriate location generates an appropriate error.
     */
    public void testParagraphBreakCannotAppearHereInsideCALDocCommentInlineTag() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@em paragraph1\n\n paragraph2\n\n paragraph3 @} */",
            MessageKind.Error.ParagraphBreakCannotAppearHereInsideCALDocCommentInlineTag.class);
    }
    
    /**
     * Tests that when a CALDoc comment for an algebraic function with an associated type delcaration appears immediately before
     * the function definition rather than immediately before the type declaration, the compiler generates an appropriate error.
     */
    public void testCALDocCommentForAlgebraicFunctionMustAppearBeforeTypeDeclaration() {
        CompilerTestUtilities.checkDefnForExpectedError(
            new String[] {
                "foo :: () -> ();",
                "/**",
                " * @arg dummy    a dummy variable", 
                " */", 
                "public foo dummy = ();"
            },
            MessageKind.Error.CALDocCommentForAlgebraicFunctionMustAppearBeforeTypeDeclaration.class, leccCALServices);
    }
    
    /**
     * Tests that a CALDoc comment appearing in an inappropriate location generates an appropriate error.
     */
    public void testCALDocCommentCannotAppearHere() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "public foo /** bad caldoc * / dummy = 7.0;",
            MessageKind.Error.CALDocCommentCannotAppearHere.class, leccCALServices);
    }
    
    /**
     * Tests that an unassociated CALDoc comment generates an appropriate error.
     */
    public void testUnassociatedCALDocComment() {
        CompilerTestUtilities.checkDefnForExpectedError(
            new String[] {
                "/**",
                " * @arg dummy    a dummy variable", 
                " */",
                "/***/",
                "foo :: () -> ();", 
                "public foo dummy = ();"
            },
            MessageKind.Error.UnassociatedCALDocComment.class, leccCALServices);
    }
    
    /**
     * Tests that an unrecognized see block reference in a CALDoc comment generates an appropriate error.
     */
    public void testUnrecognizedSeeOrLinkBlockReferenceInCALDocComment_SeeBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** @see module = !" + CAL_Prelude.MODULE_NAME + " */",
            MessageKind.Error.UnrecognizedSeeOrLinkBlockReferenceInCALDocComment.class);
    }
    
    /**
     * Tests that an unrecognized link block reference in a CALDoc comment generates an appropriate error.
     */
    public void testUnrecognizedSeeOrLinkBlockReferenceInCALDocComment_LinkBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@link module = !" + CAL_Prelude.MODULE_NAME + "@} */",
            MessageKind.Error.UnrecognizedSeeOrLinkBlockReferenceInCALDocComment.class);
    }
    
    /**
     * Tests that an unrecognized see block context in a CALDoc comment generates an appropriate error.
     */
    public void testUnrecognizedSeeOrLinkBlockContextInCALDocComment_SeeBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** @see foobar = " + CAL_Prelude.Functions.id.getQualifiedName() + " */",
            MessageKind.Error.UnrecognizedSeeOrLinkBlockContextInCALDocComment.class);
    }
    
    /**
     * Tests that an unrecognized link block context in a CALDoc comment generates an appropriate error.
     */
    public void testUnrecognizedSeeOrLinkBlockContextInCALDocComment_LinkBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@link foobar = " + CAL_Prelude.Functions.id.getQualifiedName() + "@} */",
            MessageKind.Error.UnrecognizedSeeOrLinkBlockContextInCALDocComment.class);
    }
    
    /**
     * Tests that a missing see block context in a CALDoc comment generates an appropriate error.
     */
    public void testMissingSeeOrLinkBlockContextInCALDocComment_SeeBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** @see = " + CAL_Prelude.Functions.id.getQualifiedName() + " */",
            MessageKind.Error.MissingSeeOrLinkBlockContextInCALDocComment.class);
    }
    
    /**
     * Tests that a missing link block context in a CALDoc comment generates an appropriate error.
     */
    public void testMissingSeeOrLinkBlockContextInCALDocComment_LinkBlock() {
        /*
         * The test with the module level comment is a regression test for a bug in the CAL grammar's source range handling
         * (calling ParseTreeNode.addOmittedDelimiter() on a null inline block content node).
         */
        testWithFunctionAndModuleCALDocComments(
            "/** {@link = " + CAL_Prelude.Functions.id.getQualifiedName() + "@} */",
            MessageKind.Error.MissingSeeOrLinkBlockContextInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc cross reference in a "@see" block is resolvable to more
     * than one entity (each of a different kind), the compiler generates an appropriate error.
     */
    public void testCrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment_SeeBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** @see " + CAL_Prelude.TypeConstructors.Unit.getQualifiedName() + " */",
            MessageKind.Error.CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc cross reference in a "@link" block is resolvable to more
     * than one entity (each of a different kind), the compiler generates an appropriate error.
     */
    public void testCrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment_LinkBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@link " + CAL_Prelude.TypeConstructors.Unit.getQualifiedName() + "@} */",
            MessageKind.Error.CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc cross reference in a "@see" block is resolvable to more
     * than one entity (each of a different kind), the compiler generates an appropriate error.
     */
    public void testCrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment_SeeBlock_HierarchicalModuleName() {
        CompilerTestUtilities.checkDefnForExpectedError(
            ModuleName.make("Cal.Internal.JUnitSupport.TempModule.Alpha.Beta"),
            new String[] {
                "import Cal.Test.General.HierarchicalModuleName.Test.Alpha;",
                "import Cal.Test.General.HierarchicalModuleName.Test.Beta;",
                "/** @see Alpha.Beta */ bar = 3.0;"
            },
            MessageKind.Error.CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment.class,
            leccCALServices);
    }
    
    /**
     * Tests that when a CALDoc cross reference in a "@link" block is resolvable to more
     * than one entity (each of a different kind), the compiler generates an appropriate error.
     */
    public void testCrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment_LinkBlock_HierarchicalModuleName() {
        CompilerTestUtilities.checkDefnForExpectedError(
            ModuleName.make("Cal.Internal.JUnitSupport.TempModule.Alpha.Beta"),
            new String[] {
                "import Cal.Test.General.HierarchicalModuleName.Test.Alpha;",
                "import Cal.Test.General.HierarchicalModuleName.Test.Beta;",
                "/** {@link Alpha.Beta@} */ bar = 3.0;"
            },
            MessageKind.Error.CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment.class,
            leccCALServices);
    }
    
    /**
     * Tests that when a CALDoc unchecked cross reference in a "@see" appears without a context keyword,
     * making it ambiguous, the compiler generates an appropriate error.
     */
    public void testUncheckedCrossReferenceIsAmbiguousInCALDocComment_SeeBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** @see \"" + CAL_Prelude.MODULE_NAME + "\" */",
            MessageKind.Error.UncheckedCrossReferenceIsAmbiguousInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc unchecked cross reference in a "@link" appears without a context keyword,
     * making it ambiguous, the compiler generates an appropriate error.
     */
    public void testUncheckedCrossReferenceIsAmbiguousInCALDocComment_LinkBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@link \"" + CAL_Prelude.MODULE_NAME + "\"@} */",
            MessageKind.Error.UncheckedCrossReferenceIsAmbiguousInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc checked cross reference in a "@see" or "@link" block does not resolve to any
     * known entity, the compiler generates an appropriate error.
     */
    public void testCheckedCrossReferenceCannotBeResolvedInCALDocComment_SeeBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** @see LaLaLa */",
            MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc checked cross reference in a "@see" or "@link" block does not resolve to any
     * known entity, the compiler generates an appropriate error.
     */
    public void testCheckedCrossReferenceCannotBeResolvedInCALDocComment_LinkBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@link LaLaLa@} */",
            MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc checked cross reference in a "@see" or "@link" block does not resolve to any
     * known entity, the compiler generates an appropriate error with a single suggestion.
     */
    public void testCheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion_SeeBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** @see False */",
            MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion.class);
    }
    
    /**
     * Tests that when a CALDoc checked cross reference in a "@see" or "@link" block does not resolve to any
     * known entity, the compiler generates an appropriate error with a single suggestion.
     */
    public void testCheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion_LinkBlock() {
        testWithFunctionAndModuleCALDocComments(
            "/** {@link False@} */",
            MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion.class);
    }
    
    /**
     * Tests that an unrecognized tag in a CALDoc comment generates an appropriate error.
     */
    public void testUnrecognizedTagInCALDocComment() {
        testWithFunctionAndModuleCALDocComments(
            "/** @lalala foo */",
            MessageKind.Error.UnrecognizedTagInCALDocComment.class);
    }
    
    /**
     * Tests that an unrecognized inline tag in a CALDoc comment generates an appropriate error.
     */
    public void testUnrecognizedInlineTagInCALDocComment() {
        /*
         * The test with the module level comment is a regression test for a bug in the CAL grammar's source range handling
         * (calling ParseTreeNode.addOmittedDelimiter() with a null subtree returned by the rule that failed
         * to match the opening tag).
         */
        testWithFunctionAndModuleCALDocComments(
            "/** {@lalala foo@} */",
            MessageKind.Error.UnrecognizedInlineTagInCALDocComment.class);
    }
    
    /**
     * Tests that when a CALDoc comment contains a tag that cannot be used with the comment, e.g. an "@arg" tag in
     * a type constructor comment, the compiler generates an appropriate error.
     */
    public void testDisallowedTagInCALDocComment() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "/** @arg something */ data Foo = Bar somethiing::();",
            MessageKind.Error.DisallowedTagInCALDocComment.class, leccCALServices);
    }
    
    /**
     * Tests that when a CALDoc comment contains multiple instances of a tag that can only appear once per CALDoc comment,
     * the compiler generates an appropriate error.
     */
    public void testSingletonTagAppearsMoreThanOnceInCALDocComment() {
        CompilerTestUtilities.checkDefnForExpectedError(
            new String[] {
                "/**",
                " * @version 1", 
                " * @version 2", 
                " */",
                "foo :: () -> ();", 
                "public foo dummy = ();"
            },
            MessageKind.Error.SingletonTagAppearsMoreThanOnceInCALDocComment.class, leccCALServices);
    }
    
    /**
     * Tests that when a CALDoc "@arg" tag contains an argument name that does not match the declared name,
     * the compiler generates an appropriate error.
     */
    public void testArgNameDoesNotMatchDeclaredNameInCALDocComment() {
        CompilerTestUtilities.checkDefnForExpectedError(
            new String[] {
                "data Foo =",
                "/**",
                " * @arg foo the foo", 
                " * @arg quz the quz", 
                " */",
                "Foo foo::() bar::();"
            },
            MessageKind.Error.ArgNameDoesNotMatchDeclaredNameInCALDocComment.class, leccCALServices);
    }
    
    /**
     * Tests that when a CALDoc "@arg" tag contains an argument name that is not valid, the compiler generates an appropriate error.
     */
    public void testInvalidArgNameInCALDocComment() {
        CompilerTestUtilities.checkDefnForExpectedError(
            new String[] {
                "/**",
                " * @arg foo the foo", 
                " * @arg #2 the bar", 
                " */",
                "baz foo bar = 3.0;"
            },
            MessageKind.Error.InvalidArgNameInCALDocComment.class, leccCALServices);
    }
    
    /**
     * Tests that when a CALDoc comment contains more "@arg" tags than there are arguments for the function/data constructor,
     * the compiler generates an appropriate error.
     */
    public void testTooManyArgTagsInCALDocComment() {
        CompilerTestUtilities.checkDefnForExpectedError(
            new String[] {
                "data Foo =",
                "/**",
                " * @arg foo the foo", 
                " * @arg bar the bar", 
                " * @arg baz the baz", 
                " */",
                "Foo foo::() bar::();"
            },
            MessageKind.Error.TooManyArgTagsInCALDocComment.class, leccCALServices);
    }
    
    /**
     * Tests that the module CALDoc comment for the CALDocTest module is compiled into a CALDocComment
     * with the expected structure.
     * 
     * This test relies on the CALDocTest module's module CALDoc comment to be of a specific form. If that
     * comment is modified, then so should this test!
     */
    public void testCALDocTestModuleComment() {
        CALDocComment comment = leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.CALDocTest).getCALDocComment();
        assertNotNull(comment);
        
        /// check summary
        //
        CALDocComment.TextBlock summary = comment.getSummary();
        assertNotNull(summary);
        assertEquals(2, summary.getNParagraphs());
        
        CALDocComment.Paragraph summaryPara1 = summary.getNthParagraph(0);
        assertTrue(summaryPara1 instanceof CALDocComment.TextParagraph);
        
        CALDocComment.TextParagraph summaryTextPara1 = (CALDocComment.TextParagraph)summaryPara1;
        assertEquals(3, summaryTextPara1.getNSegments());
        assertTrue(summaryTextPara1.getNthSegment(1) instanceof CALDocComment.EmphasizedSegment);
        
        CALDocComment.Paragraph summaryPara2 = summary.getNthParagraph(1);
        assertTrue(summaryPara2 instanceof CALDocComment.TextParagraph);
        
        CALDocComment.TextParagraph summaryTextPara2 = (CALDocComment.TextParagraph)summaryPara2;
        assertEquals(1, summaryTextPara2.getNSegments());
        assertTrue(summaryTextPara2.getNthSegment(0) instanceof CALDocComment.EmphasizedSegment);
        
        /// check main description
        //
        CALDocComment.TextBlock mainDesc = comment.getDescriptionBlock();
        assertEquals(10, mainDesc.getNParagraphs());
        
        // 2nd paragraph of main description
        CALDocComment.Paragraph para2 = mainDesc.getNthParagraph(1);
        assertTrue(para2 instanceof CALDocComment.TextParagraph);
        
        CALDocComment.TextParagraph textPara2 = (CALDocComment.TextParagraph)para2;
        assertEquals(3, textPara2.getNSegments());
        assertTrue(textPara2.getNthSegment(0) instanceof CALDocComment.PlainTextSegment);
        assertTrue(textPara2.getNthSegment(1) instanceof CALDocComment.URLSegment);
        
        // 4th paragraph of main description
        CALDocComment.Paragraph para4 = mainDesc.getNthParagraph(3);
        assertTrue(para4 instanceof CALDocComment.TextParagraph);
        
        CALDocComment.TextParagraph textPara4 = (CALDocComment.TextParagraph)para4;
        assertEquals(2, textPara4.getNSegments());
        assertTrue(textPara4.getNthSegment(0) instanceof CALDocComment.URLSegment);
        assertTrue(textPara4.getNthSegment(1) instanceof CALDocComment.PlainTextSegment);
        
        // 5th paragraph of main description
        CALDocComment.Paragraph para5 = mainDesc.getNthParagraph(4);
        assertTrue(para5 instanceof CALDocComment.TextParagraph);
        
        CALDocComment.TextParagraph textPara5 = (CALDocComment.TextParagraph)para5;
        assertEquals(2, textPara5.getNSegments());
        assertTrue(textPara5.getNthSegment(0) instanceof CALDocComment.FunctionOrClassMethodLinkSegment);
        
        // 6th paragraph of main description
        CALDocComment.Paragraph para6 = mainDesc.getNthParagraph(5);
        assertTrue(para6 instanceof CALDocComment.TextParagraph);
        
        CALDocComment.TextParagraph textPara6 = (CALDocComment.TextParagraph)para6;
        assertEquals(2, textPara6.getNSegments());
        assertTrue(textPara6.getNthSegment(0) instanceof CALDocComment.CodeSegment);
        
        // 8th paragraph of main description
        CALDocComment.Paragraph para8 = mainDesc.getNthParagraph(7);
        assertTrue(para8 instanceof CALDocComment.ListParagraph);
        
        CALDocComment.ListParagraph listPara8 = (CALDocComment.ListParagraph)para8;
        assertEquals(2, listPara8.getNItems());
        
        // 2nd list item
        CALDocComment.ListItem item2 = listPara8.getNthItem(1);
        CALDocComment.TextBlock item2Content = item2.getContent();
        assertEquals(2, item2Content.getNParagraphs());
        
        // 2nd paragraph, 2nd list item
        CALDocComment.Paragraph item2Para2 = item2Content.getNthParagraph(1);
        assertTrue(item2Para2 instanceof CALDocComment.ListParagraph);
        
        CALDocComment.ListParagraph item2ListPara2 = (CALDocComment.ListParagraph)item2Para2;
        assertEquals(1, item2ListPara2.getNItems());
        
        CALDocComment.ListItem item2NestedItem = item2ListPara2.getNthItem(0);
        CALDocComment.TextBlock item2NestedItemContent = item2NestedItem.getContent();
        assertEquals(1, item2NestedItemContent.getNParagraphs());
        
        CALDocComment.Paragraph item2NestedItemPara = item2NestedItemContent.getNthParagraph(0);
        assertTrue(item2NestedItemPara instanceof CALDocComment.TextParagraph);
        
        CALDocComment.TextParagraph item2NestedItemTextPara = (CALDocComment.TextParagraph)item2NestedItemPara;
        assertEquals(2, item2NestedItemTextPara.getNSegments());
        assertTrue(item2NestedItemTextPara.getNthSegment(0) instanceof CALDocComment.EmphasizedSegment);
    }
    
    /**
     * Generate a boilerplate module comment.
     * @param moduleName the name of the module
     */
    private static SourceModel.CALDoc.TextBlock getModuleCommentBoilerplate(ModuleName moduleName) {
        String commentText1 =
            "\n" +
            " Test.cal.\n" +
            " \n" +
            " ";
        
        SourceModel.CALDoc.TextSegment.InlineTag.Summary commentSegment2 =
            SourceModel.CALDoc.TextSegment.InlineTag.Summary.make(
                SourceModel.CALDoc.TextBlock.make(
                    new SourceModel.CALDoc.TextSegment.TopLevel[] {
                        SourceModel.CALDoc.TextSegment.Plain.make("This module was automatically generated by JFit.")
                    }));
        
        String commentText3 =
            "\n" +
            " \n" +
            " Creation Date: January 1, 1980\n";
        
        return SourceModel.CALDoc.TextBlock.make(
            new SourceModel.CALDoc.TextSegment.TopLevel[] {
                SourceModel.CALDoc.TextSegment.Plain.make(commentText1),
                commentSegment2,
                SourceModel.CALDoc.TextSegment.Plain.make(commentText3)
            });
    }
    
    /**
     * Generate the CALDoc comment for a module.
     * @param moduleName the name of the module.
     * @return the source model for the auto-generated comment.
     */
    private static SourceModel.CALDoc.Comment.Module getModuleCALDoc(ModuleName moduleName) {
        
        // Get the boilerplate strings.
        SourceModel.CALDoc.TextBlock moduleText = getModuleCommentBoilerplate(moduleName);
        
        // Add a tagged block for the author tag, if the "user.name" property is defined.
        SourceModel.CALDoc.TaggedBlock taggedBlocks[] = null;
        
        String userNameProperty = "Joseph Wong";
        if (userNameProperty != null) {
            SourceModel.CALDoc.TextBlock authorBlock =
                SourceModel.CALDoc.TextBlock.make(
                    new SourceModel.CALDoc.TextSegment.TopLevel[] {
                        SourceModel.CALDoc.TextSegment.Plain.make(userNameProperty)
                    });
            
            taggedBlocks = new SourceModel.CALDoc.TaggedBlock[] {
                    SourceModel.CALDoc.TaggedBlock.Author.make(authorBlock)
            };
        }
        
        return SourceModel.CALDoc.Comment.Module.make(moduleText, taggedBlocks);
    }
    
    /**
     * Regression test for bug in the text generated from a programmatically-constructed piece of CALDoc
     * with inline tags.
     */
    public void testProgrammaticallyConstructedCALDoc() {
        final SourceModel.CALDoc.Comment.Module caldoc = getModuleCALDoc(ModuleName.make("Test"));
        final String caldocString = caldoc.toSourceText();
        
        // Make sure that inline tags all have a space after them
        assertTrue("This comment is malformed:\n" + caldocString, caldocString.contains("{@summary This module"));
        assertTrue("This comment is malformed:\n" + caldocString, caldocString.contains("@author Joseph Wong"));
    }
}
