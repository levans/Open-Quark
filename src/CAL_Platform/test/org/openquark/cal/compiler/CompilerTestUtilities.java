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
 * CompilerTestUtilities.java
 * Creation date: Mar 2, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.openquark.cal.compiler.CompilerMessage.AbortCompilation;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.StringModuleSourceDefinition;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.General;


/**
 * A set of helper methods for use in JUnit test cases that need to access functionalities
 * provided by package-scoped classes/methods in the CAL compiler package.
 *
 * @author Joseph Wong
 */
public class CompilerTestUtilities {
    
    
    /**
     * Helper method to convert a string containing a CAL module definition
     * into the corresponding parse tree. Exceptions thrown by the
     * compiler are caught by this method, and it returns null when the
     * parsing fails.
     * 
     * @param moduleDefnStr
     *            the string containing the CAL module definition
     * @return the corresponding parse tree, or null if the compiler
     *         cannot parse the supplied argument as a module definition
     */
    static ParseTreeNode parseModuleDefnIntoParseTreeNode(String moduleDefnStr) {
        return parseModuleDefnIntoParseTreeNode(moduleDefnStr, null);
    }
    /**
     * Helper method to convert a string containing a CAL module definition
     * into the corresponding parse tree. Exceptions thrown by the
     * compiler are caught by this method, and it returns null when the
     * parsing fails.
     * 
     * @param moduleDefnStr
     *            the string containing the CAL module definition
     * @param embellishments - collection in which to store source embellishments
     *      encounter when parsing source.
     * @return the corresponding parse tree, or null if the compiler
     *         cannot parse the supplied argument as a module definition
     */
    static ParseTreeNode parseModuleDefnIntoParseTreeNode(String moduleDefnStr, Collection<SourceEmbellishment> embellishments) {
        
        CALCompiler compiler = new CALCompiler();
        
        // Use a compiler-specific logger, so that fatal errors abort compilation.
        CompilerMessageLogger compileLogger = new MessageLogger(true);
        compiler.setCompilerMessageLogger(compileLogger);

        CALParser parser = freshParser(compiler, new StringReader(moduleDefnStr), embellishments);
        CALTreeParser treeParser = new CALTreeParser(compiler);
        try {
            parser.module();
            ParseTreeNode moduleDefnNode = (ParseTreeNode)parser.getAST();
            treeParser.module(moduleDefnNode);
            
            return moduleDefnNode;
            
        } catch (antlr.RecognitionException e) {           
            // Recognition (syntax) error
        } catch (antlr.TokenStreamException e) {
            // Bad token stream.  Maybe a bad token (eg. a stray "$" sign)
        } catch (AbortCompilation e) {
            //compilation aborted    
        } catch (Exception e) {
            // Major failure - internal coding error
        } 
        
        return null;
    }
        
    /**
     * Construct a new parser state from the given reader. The returned parser
     * will have its lexer and stream selector set, and will be configured for
     * ASTNodes of type ParseTreeNode. Note that tree nodes created by this
     * parser will not have any source name (filename) info.
     * 
     * @param compiler
     *            The compiler. This will be used for message logging and for
     *            parser access to its stream selector.
     * @param reader
     *            the reader from which to parse.
     * @return CALParser a new parser configured for the given args.
     * 
     * (see CALTypeChecker.freshParser(CALCompiler, java.io.Reader) )
     */
    private static final CALParser freshParser(CALCompiler compiler, java.io.Reader reader, Collection<SourceEmbellishment> embellishments) {
    
        // Make a multiplexed lexer
        CALMultiplexedLexer lexer = new CALMultiplexedLexer(compiler, reader, embellishments);
        
        // Create a parser, it gets its tokens from the multiplexed lexer
        CALParser parser = new CALParser(compiler, lexer);
    
        String treeNodeClassName = ParseTreeNode.class.getName();
        parser.setASTNodeClass(treeNodeClassName);
    
        return parser;
    }
    
    /**
     * Returns whether the message kind represents an internal coding error.
     * @param messageKind the message kind to check
     * @return true iff the message kind represents an internal coding error.
     */
    public static boolean isInternalCodingError(MessageKind messageKind) {
        return messageKind instanceof MessageKind.Fatal.InternalCodingError;
    }
    
    /**
     * Get the name of a new module which doesn't exist in the program
     * @param calServices the cal services object on which to generate the module name.
     * @return the name of a module which doesn't exist in the program.
     */
    public static ModuleName getNewModuleName(BasicCALServices calServices) {
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Set<ModuleName> moduleNames = new HashSet<ModuleName>(Arrays.asList(workspaceManager.getModuleNamesInProgram()));
        
        String baseName = "CompilerTestModule";
        ModuleName moduleName = ModuleName.make(baseName);
        int index = 1;
        while (moduleNames.contains(moduleName)){
            moduleName = ModuleName.make(baseName + "_" + index);
            index++;
        }
        return moduleName;
    }

    /**
     * Check that a given set of outer defns gives a compile error when compiled to a new module.
     * @param outerDefnText the text of the outer defns.
     * @param expectedErrorClass the class of the expected error.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedError(String outerDefnText, Class<? extends MessageKind.Error> expectedErrorClass, BasicCALServices calServices) {
        checkDefnForExpectedError(new String[]{outerDefnText}, expectedErrorClass, calServices);
    }

    /**
     * Check that a given set of outer defns gives a compile error when compiled to a new module.
     * @param outerDefnTextLines the lines of text of the outer defns.
     * @param expectedErrorClass the class of the expected error.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedError(String[] outerDefnTextLines, Class<? extends MessageKind.Error> expectedErrorClass, BasicCALServices calServices) {
        ModuleName moduleName = getNewModuleName(calServices);
        checkDefnForExpectedError(moduleName, outerDefnTextLines, expectedErrorClass, calServices);
    }
    
    /**
     * Check that a given set of outer defns gives a compile error when compiled to a module.
     * @param moduleName the name of the module.
     * @param outerDefnText the text of the outer defns.
     * @param expectedErrorClass the class of the expected error.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedError(ModuleName moduleName, String outerDefnText, Class<? extends MessageKind.Error> expectedErrorClass, BasicCALServices calServices) {
        checkDefnForExpectedError(moduleName, new String[]{outerDefnText}, expectedErrorClass, calServices);
    }

    /**
     * Check that a given set of outer defns gives a compile error when compiled to a module.
     * @param moduleName the name of the module.
     * @param outerDefnTextLines the lines of text of the outer defns.
     * @param expectedErrorClass the class of the expected error.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedError(ModuleName moduleName, String[] outerDefnTextLines, Class<? extends MessageKind.Error> expectedErrorClass, BasicCALServices calServices) {
        String outerDefnText = concatLines(outerDefnTextLines);
        CompilerMessageLogger logger = compileAndRemoveModule(moduleName, outerDefnText, calServices);
        
        // Check that we got an error message of the expected type.
        List<CompilerMessage> messages = logger.getCompilerMessages(CompilerMessage.Severity.ERROR);
        Assert.assertTrue("No errors logged.", !messages.isEmpty());

        Set<Class<? extends MessageKind>> messageKindClasses = new HashSet<Class<? extends MessageKind>>();
        for (int i = 0, n = messages.size(); i < n; i++) {
            CompilerMessage message = messages.get(i);
            messageKindClasses.add(message.getMessageKind().getClass());
        }
        
        Assert.assertTrue("expected: <" + expectedErrorClass + "> but was: <" + messageKindClasses + ">", messageKindClasses.contains(expectedErrorClass));
    }

    /**
     * Compile outer defn and return the messages
     * @param moduleName
     * @param outerDefnTextLines
     * @param calServices
     * @return list of compiler messages
     */
    static List<CompilerMessage> compileAndGetMessages(ModuleName moduleName, 
                                                       String[] outerDefnTextLines, 
                                                       BasicCALServices calServices) {
        String outerDefnText = concatLines(outerDefnTextLines);
        CompilerMessageLogger logger = compileAndRemoveModule(moduleName, outerDefnText, calServices);
        
        // return the error messages
        return logger.getCompilerMessages(CompilerMessage.Severity.ERROR);
    }
        
    
    /**
     * Check that a given set of outer defns gives a compile warning when compiled to a new module.
     * @param outerDefnText the text of the outer defns.
     * @param expectedWarningClass the class of the expected warning.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedWarning(String outerDefnText, Class<? extends MessageKind.Warning> expectedWarningClass, BasicCALServices calServices) {
        checkDefnForExpectedWarning(new String[]{outerDefnText}, expectedWarningClass, calServices);
    }

    /**
     * Check that a given set of outer defns gives a compile warning when compiled to a new module.
     * @param outerDefnTextLines the lines of text of the outer defns.
     * @param expectedWarningClass the class of the expected warning.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedWarning(String[] outerDefnTextLines, Class<? extends MessageKind.Warning> expectedWarningClass, BasicCALServices calServices) {
        final ModuleName moduleName = getNewModuleName(calServices);
        checkDefnForExpectedWarning(moduleName, outerDefnTextLines, expectedWarningClass, calServices);
    }
    
    /**
     * Check that a given set of outer defns gives a compile warning when compiled to a module.
     * @param moduleName the name of the module.
     * @param outerDefnText the text of the outer defns.
     * @param expectedWarningClass the class of the expected warning.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedWarning(ModuleName moduleName, String outerDefnText, Class<? extends MessageKind.Warning> expectedWarningClass, BasicCALServices calServices) {
        checkDefnForExpectedWarning(moduleName, new String[]{outerDefnText}, expectedWarningClass, calServices);
    }

    /**
     * Check that a given set of outer defns gives a compile warning when compiled to a module.
     * @param moduleName the name of the module.
     * @param outerDefnTextLines the lines of text of the outer defns.
     * @param expectedWarningClass the class of the expected warning.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForExpectedWarning(ModuleName moduleName, String[] outerDefnTextLines, Class<? extends MessageKind.Warning> expectedWarningClass, BasicCALServices calServices) {
        final String outerDefnText = concatLines(outerDefnTextLines);
        final CompilerMessageLogger logger = compileAndRemoveModule(moduleName, outerDefnText, calServices);
        
        // Check that we got an warning message of the expected type.
        final List<CompilerMessage> messages = logger.getCompilerMessages();
        
        CompilerMessage warning = null;
        for (int i = 0, n = messages.size(); i < n; i++) {
            CompilerMessage message = messages.get(i);
            if (message.getSeverity() == CompilerMessage.Severity.WARNING) {
                warning = message;
                break;
            }
        }
        
        Assert.assertEquals("Unexpected errors logged.", Collections.EMPTY_LIST, logger.getCompilerMessages(CompilerMessage.Severity.ERROR));
        Assert.assertTrue("No warnings logged.", warning != null);
        Assert.assertEquals(expectedWarningClass, warning.getMessageKind().getClass());
    }

    /**
     * Check that a given set of outer defns gives a compile warning when compiled to a new module.
     * @param outerDefnText the text of the outer defns.
     * @param expectedWarningClass the class of the expected warning.
     * @param nWarningsExpected the number of warnings of that class to be expected.
     * @param calServices the CAL services object in use in the test case.
     */
    static void checkDefnForMultipleExpectedWarnings(String outerDefnText, Class<? extends MessageKind.Warning> expectedWarningClass, int nWarningsExpected, BasicCALServices calServices) {
        final ModuleName moduleName = getNewModuleName(calServices);
        final CompilerMessageLogger logger = compileAndRemoveModule(moduleName, outerDefnText, calServices);
        
        // Check that we got an warning message of the expected type.
        final List<CompilerMessage> messages = logger.getCompilerMessages();
        
        Assert.assertEquals("Unexpected errors logged.", Collections.EMPTY_LIST, logger.getCompilerMessages(CompilerMessage.Severity.ERROR));
        
        int nWarningsEncountered = 0;
        for (int i = 0, n = messages.size(); i < n; i++) {
            CompilerMessage message = messages.get(i);
            if (message.getSeverity() == CompilerMessage.Severity.WARNING) {
                if (message.getMessageKind().getClass().equals(expectedWarningClass)) {
                    nWarningsEncountered++;
                } else {
                    Assert.assertEquals("Unexpected warning class", expectedWarningClass, message.getMessageKind().getClass());
                }
            }
        }
        
        Assert.assertEquals("Mismatch between number of warnings expected and number of warnings encountered", nWarningsExpected, nWarningsEncountered);
    }
        
 
    /**
     * Asserts that two strings are equal after converting them both to
     * the platform line feed representation
     * @param expected
     * @param actual
     */
    static void assertEqualsCanonicalLineFeed(String expected, String actual) {
        Assert.assertEquals(General.toPlatformLineSeparators(expected), General.toPlatformLineSeparators(actual));
    }
    
    /**
     * Concatenate an array of strings into lines of text delimited with newlines.
     * @param lines the string to consider as lines of text.
     * @return the concatenated string.
     */
    private static String concatLines(String[] lines) {
        StringBuilder sb = new StringBuilder();
        for (final String line : lines) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Compile a new module containing a module definition, an import statement for the Prelude, and the given text.
     * @param moduleName the name of the module.
     * @param outerDefnText the text of the definitions in the module.
     * @param calServices the CAL services object in use in the test case.
     * @return a logger which logged the results of the compile.
     */
    private static CompilerMessageLogger compileAndRemoveModule(ModuleName moduleName, String outerDefnText, BasicCALServices calServices) {
        String sourceDefString = "module " + moduleName + ";\n";
        sourceDefString += "import " + CAL_Prelude.MODULE_NAME + ";\n";
        sourceDefString += outerDefnText;
        
        ModuleSourceDefinition sourceDef = new StringModuleSourceDefinition(moduleName, sourceDefString);
        
        // Get the workspace manager and the logger.
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();

        // Compile the module.
        workspaceManager.makeModule(sourceDef, logger);

        // Remove the module.
        calServices.getWorkspaceManager().removeModule(moduleName, new Status("Remove module status."));

        return logger;
    }
    

}
