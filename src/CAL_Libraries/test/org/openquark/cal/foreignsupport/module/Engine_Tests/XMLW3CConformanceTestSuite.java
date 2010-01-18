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
 * XMLW3CConformanceTestSuite.java
 * Creation date: September 2007.
 * By: Malcolm Sharpe
 */
package org.openquark.cal.foreignsupport.module.Engine_Tests;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.*;

import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.w3c.dom.*;

import junit.framework.*;

/**
 * A test harness for running the XML W3C Conformance Test Suite on
 * the module Cal.Experimental.Utilities.XmlParser.Engine.<p>
 * 
 * To run the conformance tests, first download the test suite, and then run this
 * class with the property org.openquark.cal.foreignsupport.module.Engine_Tests.xmlconfPath
 * set to the path to xmlconf.xml, which is part of the test suite.<p>
 * 
 * This class is designed for the 10 December 2003 version of the test suite, and as
 * such contains a few hard-coded fixes for minor bugs in that version of the suite.
 * This is particularly needed for tests which involve reading external entities, since
 * they are fairly often mislabelled.<p>
 * 
 * @see <a href="http://www.w3.org/XML/Test/">XML W3C Conformance Test Suite</a>
 *
 * @author Malcolm Sharpe
 */
public class XMLW3CConformanceTestSuite {
    private static final String XMLCONF_PATH_PROP = "org.openquark.cal.foreignsupport.module.Engine_Tests.xmlconfPath";

    /** The path to xmlconf.xml. */
    private static final String XMLCONF_PATH = System.getProperty(XMLCONF_PATH_PROP); 
    
    /**
     * Create a test suite by parsing the XML test description file, "xmlconf.xml".
     * Its path is given by {@link #XMLCONF_PATH}.
     * 
     * For details, see {@link #recursivelyCreateTest}.
     * 
     * @return a test suite automatically generated from "xmlconf.xml".
     * @throws Exception
     */
    public static Test suite() throws Exception {
        if (XMLCONF_PATH == null) throw new NullPointerException(XMLCONF_PATH_PROP+" property must be set when running the conformance suite"); 
        
        // Parse "xmlconf.xml".
        URI baseUri = new File(XMLCONF_PATH).getParentFile().toURI();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document doc = builder.parse(XMLCONF_PATH);
        
        // Recursively create the suite.
        return recursivelyCreateTest(doc.getDocumentElement(), baseUri);
    }
    
    /**
     * Generate a test from the test descriptions descended from the given element.
     * If the element is a TESTSUITE or TESTCASES element, a {@link TestSuite} is
     * created with a name corresponding to the element's PROFILE attribute. If the
     * element is a TEST element, an {@link XMLParserTest} is created, which handles
     * the execution of the test.
     * 
     * @param testElement the element from which to generate a test or test suite.
     * @param baseUri the physical location of testElement's parent.
     * @return the test or test suite generated from the element.
     * @throws URISyntaxException
     */
    private static Test recursivelyCreateTest(Element testElement, URI baseUri) throws URISyntaxException {
        if (!testElement.getTagName().equals("TEST")) {
            // The element is either TESTSUITE or TESTCASES, but they can
            // be treated uniformly.
            String profile = testElement.getAttribute("PROFILE");
            String xmlBase = testElement.getAttribute("xml:base");
            
            // Unfortunately, the test description files seem to misuse xml:base.
            // To work around it, append a '/' if both: there isn't one already
            // and this is a relative URI.
            if (!new URI(xmlBase).isAbsolute() && xmlBase.length() > 0 && xmlBase.charAt(xmlBase.length() - 1) != '/') {
                xmlBase += '/';
            }
            
            NodeList children = testElement.getChildNodes();
            TestSuite suite = new TestSuite(profile);
            
            // Traverse each element child.
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                
                if (!(child instanceof Element)) continue;
                Element childElement = (Element)child;
                
                // Only use TEST elements for XML 1.0.
                if (childElement.hasAttribute("RECOMMENDATION") &&
                    !"XML1.0".equals(childElement.getAttribute("RECOMMENDATION"))) continue;
                
                // Optional error test cases are ignored.
                if ("error".equals(childElement.getAttribute("TYPE"))) continue;
                
                suite.addTest(recursivelyCreateTest(childElement, baseUri.resolve(xmlBase)));
            }
            
            return suite;
        } else {
            fixTestBugs(testElement);
            
            // The element is TEST, so create the test.
            URI relUri = new URI(testElement.getAttribute("URI"));
            
            URI uri = baseUri.resolve(relUri);
            String path = new File(uri).getPath();

            return new XMLParserTest(testElement, path, baseUri);
        }
    }
    
    /**
     * Hardcoded fixes for minor bugs in the 10 December 2003 version of
     * the XML W3C Conformance Test Suite. These bugs are almost all mislabelling
     * of entity use. That only matters for non-validating parsers that do not read
     * external entities, which are rare, so that is likely why they have not been
     * fixed yet. The one other bug is an output test that does not conform to the
     * canonical XML form, so we ignore its output.
     * 
     * @param testElement the test element to fix.
     */
    private static void fixTestBugs(Element testElement) {
        String uri = testElement.getAttribute("URI");
        
        if ("not-wf/P32/ibm32n09.xml".equals(uri)) {
            testElement.setAttribute("ENTITIES", "none");
        } else if ("not-wf/P68/ibm68n06.xml".equals(uri)) {
            testElement.setAttribute("ENTITIES", "none");
        } else if ("not-wf/not-sa03.xml".equals(uri)) {
            testElement.setAttribute("ENTITIES", "none");
        } else if ("not-wf/sa/081.xml".equals(uri)) {
            testElement.setAttribute("ENTITIES", "none");
        } else if ("not-wf/sa/082.xml".equals(uri)) {
            testElement.setAttribute("ENTITIES", "none");
        } else if ("not-wf/sa/185.xml".equals(uri)) {
            testElement.setAttribute("ENTITIES", "none");
        } else if ("not-wf/not-sa/002.xml".equals(uri)) {
            testElement.setAttribute("ENTITIES", "none");
        } else if ("valid/P29/ibm29v01.xml".equals(uri)) {
            testElement.removeAttribute("OUTPUT");
        }
    }
}

/**
 * A test of the XML parser on a single input file.
 * 
 * @author Malcolm Sharpe
 */
class XMLParserTest extends TestCase {
    public XMLParserTest(Element testElement, String path, URI baseUri) {
        super(testElement.getAttribute("ID") +
                " -- " +
                (("none".equals(testElement.getAttribute("ENTITIES")) ||
                  !"not-wf".equals(testElement.getAttribute("TYPE"))) ? testElement.getAttribute("TYPE") : "invalid") +
                " -- " +
                testElement.getAttribute("SECTIONS"));
        
        this.testElement = testElement;
        this.path = path;
        this.baseUri = baseUri;
    }
    
    private Element testElement;
    private String path;
    private URI baseUri;
    
    /**
     * Run the XML parser on a single input file, and determine whether the
     * parser correctly identified its input as well-formed or not well-formed.
     * In addition, if there is output, ensure that the parser's canonicalized
     * output matches it. 
     */
    public void runTest() throws Exception {
        String type = testElement.getAttribute("TYPE");
        String entities = testElement.getAttribute("ENTITIES");
        
        File tempFile = File.createTempFile("output", ".xml");
        tempFile.deleteOnExit();
        
        if ("not-wf".equals(type) && "none".equals(entities)) {
            // We expect isWellFormed to return false.
            boolean iswf = isWellFormed(path);
            
            assertFalse("document should not be considered well-formed", iswf);
        } else if ("invalid".equals(type) || "valid".equals(type) || "not-wf".equals(type)) {
            // We expect isWellFormed to return true. In the case of not-wf, this is
            // because we do not read external entities.
            // Furthermore, there may be an OUTPUT or OUTPUT3 attribute
            // associated with this test.
            
            // If this test references external entities, there's a good chance that we
            // can't write useful output, since the attribute defaults we see will be
            // different than what a validating parser sees.
            boolean canWriteUsefulOutput = "none".equals(entities);
            
            String referenceOutputPath = null;
            if (canWriteUsefulOutput && testElement.hasAttribute("OUTPUT")) {
                parseAndWriteSecondXmlCanonicalForm(path, tempFile.getPath());
                referenceOutputPath = testElement.getAttribute("OUTPUT");
            } else if (canWriteUsefulOutput && testElement.hasAttribute("OUTPUT3")) {
                // At the time of writing, no tests use the OUTPUT3 attribute, so
                // there is no need to support the Third XML Canonical Form.
                throw new Exception("Third XML Canonical Form not supported");
            } else {
                assertTrue("document should be considered well-formed", isWellFormed(path));
            }
            
            // Check that output is correct.
            if (referenceOutputPath != null) {
                referenceOutputPath = new File(baseUri.resolve(referenceOutputPath)).getPath();
                
                assertTrue("Reference output file does not exist: "+referenceOutputPath, new File(referenceOutputPath).exists());
                assertTrue("Temporary output file does not exist: "+tempFile.getPath(), tempFile.exists());
                
                BufferedInputStream bis1 = new BufferedInputStream(
                        new FileInputStream(referenceOutputPath));
                BufferedInputStream bis2 = new BufferedInputStream(
                        new FileInputStream(tempFile.getPath()));
                
                while (true) {
                    int b1 = bis1.read();
                    int b2 = bis2.read();
                    
                    assertEquals("Outputs differ.", b1, b2);
                    
                    if (b1 == -1) break;
                }
            }
        } else {
            // Should not happen.
            assert false;
        }
    }
    
    private static boolean isWellFormed(String path) throws Exception {
        BasicCALServices cal = getCALServices();

        EntryPointSpec isWellFormedEntrySpec =
            EntryPointSpec.make(QualifiedName.make(ModuleName.make("Cal.Test.Experimental.Utilities.XmlParserEngine_Tests"),
                    "isWellFormed"));
        
        Object result = cal.runFunction(isWellFormedEntrySpec, new Object[] { path });
        
        assert result instanceof Boolean;
        return ((Boolean)result).booleanValue();
   }
    
    private static void parseAndWriteSecondXmlCanonicalForm(String inPath, String outPath) throws Exception {
        BasicCALServices cal = getCALServices();
        
        EntryPointSpec parseAndWriteSecondXmlCanonicalFormEntrySpec =
            EntryPointSpec.make(QualifiedName.make(ModuleName.make("Cal.Test.Experimental.Utilities.XmlParserEngine_Tests"),
                    "parseAndWriteSecondXmlCanonicalForm"));
        
        cal.runFunction(parseAndWriteSecondXmlCanonicalFormEntrySpec, new Object[] { inPath, outPath });
    }
    
    // CAL interop.
    /** Property which when set will override the default workspace file. */
    private static final String WORKSPACE_FILE_PROPERTY = "xmlparser.test.workspace";
    /** Location of the default XML parser workspace file. */
    private static final String DEFAULT_WORKSPACE_FILE = "cal.libraries.test.cws";
    /** Whether or not to use a nullary workspace. */
    private static final boolean USE_NULLARY_WORKSPACE = true;
    /** The default workspace client id. */
    private static final String DEFAULT_WORKSPACE_CLIENT_ID = USE_NULLARY_WORKSPACE ? null : "xmlparser.test";

    /**
     * Rather than reinitialize the CAL service multiple times, we use the CAL test
     * utilities which will cache the CAL services for us.
     * @return A CAL services object for the XML parser tests.
     */
    public static BasicCALServices getCALServices() {
        return CALServicesTestUtilities.getCommonCALServices(WORKSPACE_FILE_PROPERTY,
                                                             DEFAULT_WORKSPACE_FILE,
                                                             DEFAULT_WORKSPACE_CLIENT_ID);
    }
}