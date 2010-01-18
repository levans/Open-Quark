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
 * SourceModelCodeFormatter_Test.java
 * Created: May 30, 2007
 * By: mbyne
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.Status;
import org.openquark.util.General;

public class SourceModelCodeFormatter_Test  extends TestCase {
    /**
     * A copy of CAL services for use in the test cases
     */
    private static BasicCALServices leccServices;
    
    /**
     * Constructor for SourceModelUtilities_Test.
     * 
     * @param name
     *            the name of the test
     */
    public SourceModelCodeFormatter_Test(String name) {
        super(name);
    }
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(SourceModelCodeFormatter_Test.class);

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
        leccServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws"); 
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccServices = null;
    }
    
        
    /**
     * Tests to make sure the source code formatter style has not changed
     */
    public void testSourceCodeFormatterStyle() {
        CompilerMessageLogger logger = new MessageLogger();
        
        ModuleSourceDefinition module = CALServicesTestUtilities.getModuleSourceDefinitionGroup(leccServices).getModuleSource(ModuleName.make("Cal.Test.JUnitSupport.FormatterTestModule"));
        String src = module.getSourceText(new Status("reading source for comparison"), logger);

        CompilerMessageLogger ml= new MessageLogger();
        
        //reformat the source
        List<SourceEmbellishment> embellishments = new ArrayList<SourceEmbellishment>();
        SourceModel.ModuleDefn sourceModel = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(src, false, ml, embellishments);
   
        //format the source
        String formatted = SourceModelCodeFormatter.formatCode(
            sourceModel,
            SourceModelCodeFormatter.DEFAULT_OPTIONS,
            embellishments
        );
        CompilerTestUtilities.assertEqualsCanonicalLineFeed(src, formatted);

    }

    public void testPartialFormatting_ByFunctionName() {
        String src = "module Foo; import Prelude;\nf=1.0;\nf2=2.0;";

        PrettyPrintRefactorer rf = new PrettyPrintRefactorer(src, null, Collections.singleton("f"));
        
        SourceModifier modifer = rf.getModifier();
        
        assertNotNull(modifer);
        assertEquals(1, modifer.getNSourceModifications());

        String res = modifer.apply(src);
        
        assertEqualsCanonicalLineFeed("module Foo; import Prelude;\nf = 1.0;\nf2=2.0;", res);
    }

    public void testPartialFormatting_ByLines() {
        String src = "module Foo; import Prelude;\nf=1.0;\nf2=2.0;";

        PrettyPrintRefactorer rf = 
            new PrettyPrintRefactorer(src, 
                    new SourceRange(new SourcePosition(2,1), new SourcePosition(3,1)), Collections.<String>emptySet());
        
        SourceModifier modifer = rf.getModifier();
        
        assertNotNull(modifer);
        assertEquals(1, modifer.getNSourceModifications());

        String res = modifer.apply(src);
        
        assertEqualsCanonicalLineFeed("module Foo; import Prelude;\nf = 1.0;\nf2=2.0;", res);
    }


    /** here we test formatting of a large module 
     * by a line range. This checks formatting of all the individual elements and makes
     * sure the replacements are valid and that there are no errors when replacing
     * text, for example extra/missing ';'
     */
    public void testPartialFormatting() {
        CompilerMessageLogger logger = new MessageLogger();

        ModuleSourceDefinition module = CALServicesTestUtilities.getModuleSourceDefinitionGroup(leccServices).getModuleSource(ModuleName.make("Cal.Test.JUnitSupport.FormatterTestModule"));
        String src = module.getSourceText(new Status("reading source for comparison"), logger);

        PrettyPrintRefactorer rf = 
            new PrettyPrintRefactorer(src, 
                    new SourceRange(new SourcePosition(1,1), new SourcePosition(99999,1)), Collections.<String>emptySet());
        
        SourceModifier modifer = rf.getModifier();
        
        String res = modifer.apply(src);
        assertNotNull(modifer);
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

}
