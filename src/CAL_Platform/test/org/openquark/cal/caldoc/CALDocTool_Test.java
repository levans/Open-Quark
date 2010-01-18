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
 * CALDocTool_Test.java
 * Creation date: Oct 27, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.filter.AcceptAllModulesFilter;
import org.openquark.cal.filter.AcceptAllScopedEntitiesFilter;
import org.openquark.cal.filter.PublicEntitiesOnlyFilter;
import org.openquark.cal.filter.ScopedEntityFilter;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.LocaleUtilities;


/**
 * A set of JUnit test cases for verifying the correctness of the HTML documentation generator
 * (the CALDoc tool).
 *
 * @author Joseph Wong
 */
public class CALDocTool_Test extends TestCase {

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

        TestSuite suite = new TestSuite(CALDocTool_Test.class);

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
        // we use cal.platform.cws to have a small set of modules to test
        // but since most other tests use cal.platform.test.cws, our instance should be unshared.
        leccCALServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(MachineType.LECC, "cal.platform.cws", true);
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }
    
    /** Whether documentation generation should be tested for public entries only. */
    private static final boolean PUBLIC_ENTRIES_ONLY = true;
    
    /** A custom file generator for testing. */
    private final class CustomFileGenerator implements FileGenerator {
        /** The parent documentation generator. */
        private HTMLDocumentationGenerator parent = null;
        
        // Flags to keep track of whether certain files have been generated.
        private boolean seenMainCSS = false;
        private boolean seenMainPage = false;
        private boolean seenModuleList = false;
        private boolean seenOverviewPage = false;
        
        /** Keeps track of the set of modules whose documentation has been generated. */
        private Set<ModuleName> seenModules = new HashSet<ModuleName>();
        
        /**
         * {@inheritDoc}
         */
        public void generateTextFile(String fileName, String content, Charset charset) {
            if (fileName.equalsIgnoreCase("caldoc.css")) {
                seenMainCSS = true;
            } else if (fileName.equalsIgnoreCase("index.html")) {
                seenMainPage = true;
            } else if (fileName.equalsIgnoreCase("moduleList.html")) {
                seenModuleList = true;
            } else if (fileName.equalsIgnoreCase("overview.html")) {
                seenOverviewPage = true;
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void generateTextFile(String subdirectory, String fileName, String content, Charset charset) {
            if (subdirectory.endsWith("modules")) {
                // look for the last dot because the other dots are part of the hierarchical module name
                ModuleName moduleName = ModuleName.make(fileName.substring(0, fileName.lastIndexOf('.')));
                helpVerifyModuleDocPage(moduleName, content, parent);
                seenModules.add(moduleName);
            }
        }        
    }
    
    /**
     * Tests the CALDocTool, verifying many of the HTML files it generates.
     */
    public void testCALDocTool() {
        
        long start = System.currentTimeMillis();
        
        CustomFileGenerator fileGenerator = new CustomFileGenerator();
            
        Logger logger = Logger.getAnonymousLogger();
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        
        HTMLDocumentationGeneratorConfiguration config = new HTMLDocumentationGeneratorConfiguration(
            fileGenerator,
            new AcceptAllModulesFilter(),
            PUBLIC_ENTRIES_ONLY ? (ScopedEntityFilter)new PublicEntitiesOnlyFilter() : (ScopedEntityFilter)new AcceptAllScopedEntitiesFilter(),
            false,
            true,
            true,
            true,
            true,
            false,
            false,
            "test docs",
            "test docs",
            "test docs",
            "test docs",
            "test docs",
            LocaleUtilities.INVARIANT_LOCALE,
            logger);
        
        HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(leccCALServices.getWorkspaceManager(), config);
        fileGenerator.parent = docGenerator;
        docGenerator.generateDoc();
        
        assertTrue(fileGenerator.seenMainCSS);
        assertTrue(fileGenerator.seenMainPage);
        assertTrue(fileGenerator.seenModuleList);
        assertTrue(fileGenerator.seenOverviewPage);
        
        ModuleName[] moduleNames = leccCALServices.getWorkspaceManager().getModuleNamesInProgram();
        assertEquals(fileGenerator.seenModules, new HashSet<ModuleName>(Arrays.asList(moduleNames)));
        
        long stop = System.currentTimeMillis();
        
        if (SHOW_DEBUGGING_OUTPUT) {
            System.out.println("Time: " + ((stop - start) / 1000.0) + " seconds");
        }
    }

    /**
     * Helper method to verify a generated module documentation page.
     * @param moduleName the name of the module.
     * @param content the file's content.
     * @param parent the HTMLDocumentationGenerator.
     */
    private void helpVerifyModuleDocPage(ModuleName moduleName, String content, HTMLDocumentationGenerator parent) {
        
        StringReader sourceReader = new StringReader(content);
        
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument htmlDoc = (HTMLDocument)htmlKit.createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();
        final HTMLEditorKit.ParserCallback docCallback = htmlDoc.getReader(0);
        
        HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {

            @Override
            public void flush() throws BadLocationException {
                docCallback.flush();
            }

            @Override
            public void handleComment(char[] data, int pos) {
                docCallback.handleComment(data, pos);
            }

            @Override
            public void handleEndOfLineString(String eol) {
                docCallback.handleEndOfLineString(eol);
            }

            @Override
            public void handleEndTag(Tag t, int pos) {
                docCallback.handleEndTag(t, pos);
            }

            @Override
            public void handleError(String errorMsg, int pos) {
                docCallback.handleError(errorMsg, pos);
                if (errorMsg.startsWith("req.att") ||
                    errorMsg.startsWith("invalid.tagatt") ||
                    errorMsg.startsWith("javascript") ||
                    errorMsg.startsWith("tag.unrecognized") ||
                    errorMsg.startsWith("end.unrecognized") ||
                    // Java 5
                    errorMsg.startsWith("tag.ignore") ||
                    // Java 6 (beta2)
                    errorMsg.startsWith("unmatched.endtag script")) {
                    
                    if (SHOW_DEBUGGING_OUTPUT) {
                        System.out.println("Spurious error: " + errorMsg);
                    }
                } else {
                    fail("Failed to parse: " + errorMsg);
                }
            }

            @Override
            public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
                docCallback.handleSimpleTag(t, a, pos);
            }

            @Override
            public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
                docCallback.handleStartTag(t, a, pos);
            }

            @Override
            public void handleText(char[] data, int pos) {
                docCallback.handleText(data, pos);
            }
        };
        
        try {
            parser.parse(sourceReader, callback, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        ////
        /// Perform basic existence checks on the documented entities
        //
        
        AbstractDocumentationGenerator.DocLabelMaker labelMaker = new AbstractDocumentationGenerator.DocLabelMaker();
        
        ModuleTypeInfo moduleInfo = leccCALServices.getWorkspaceManager().getModuleTypeInfo(moduleName);
        
        HashSet<String> labels = new HashSet<String>();
        
        // Gather all the labels, which will be used as a checklist later to see whether all the labels have
        // corresponding entries generated.
        for (int i = 0, n = moduleInfo.getNFunctions(); i < n; i++) {
            Function function = moduleInfo.getNthFunction(i);
            
            if (parent.isDocForFunctionOrClassMethodGenerated(function.getName().getModuleName(), function.getName().getUnqualifiedName())) {
                labels.add(labelMaker.getLabel(function));
            }
        }
        
        for (int i = 0, n = moduleInfo.getNTypeConstructors(); i < n; i++) {
            TypeConstructor typeCons = moduleInfo.getNthTypeConstructor(i);
            
            if (parent.isDocForTypeConsGenerated(typeCons.getName().getModuleName(), typeCons.getName().getUnqualifiedName())) {
                labels.add(labelMaker.getLabel(typeCons));
                
                for (int j = 0, m = typeCons.getNDataConstructors(); j < m; j++) {
                    DataConstructor dataCons = typeCons.getNthDataConstructor(j);
                    if (parent.isDocForDataConsGenerated(dataCons.getName().getModuleName(), dataCons.getName().getUnqualifiedName())) {
                        labels.add(labelMaker.getLabel(dataCons));
                    }
                }
            }
        }
        
        for (int i = 0, n = moduleInfo.getNTypeClasses(); i < n; i++) {
            TypeClass typeClass = moduleInfo.getNthTypeClass(i);
            
            if (parent.isDocForTypeClassGenerated(typeClass.getName().getModuleName(), typeClass.getName().getUnqualifiedName())) {
                labels.add(labelMaker.getLabel(typeClass));
                
                for (int j = 0, m = typeClass.getNClassMethods(); j < m; j++) {
                    ClassMethod method = typeClass.getNthClassMethod(j);
                    if (parent.isDocForFunctionOrClassMethodGenerated(method.getName().getModuleName(), method.getName().getUnqualifiedName())) {
                        labels.add(labelMaker.getLabel(method));
                    }
                }
            }
        }
        
        for (int i = 0, n = moduleInfo.getNClassInstances(); i < n; i++) {
            ClassInstance instance = moduleInfo.getNthClassInstance(i);
            TypeClass typeClass = instance.getTypeClass();
            
            if (parent.isDocForClassInstanceGenerated(instance)) {
                labels.add(labelMaker.getLabel(instance));
                
                for (int j = 0, m = typeClass.getNClassMethods(); j < m; j++) {
                    ClassMethod method = typeClass.getNthClassMethod(j);
                    labels.add(labelMaker.getLabel(instance, method.getName().getUnqualifiedName()));
                }
            }
        }
        
        // Now loop through the generated HTML looking for the pattern id='{label}'
        Pattern pattern = Pattern.compile("id='([^']*)'");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String id = matcher.group(1);
            labels.remove(id);
        }
        
        if (!labels.isEmpty()) {
            fail("These labels are not found in the HTML: " + labels);
        }
    }
}
