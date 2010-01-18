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
 * DataConsFieldNameRenamer_Test.java
 * Created: Sep 29, 2007
 * By: JoWong
 */

package org.openquark.cal.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.util.Pair;

/**
 * JUnit test cases for the {@link DataConsFieldNameRenamer}.
 *
 * @author Joseph Wong
 */
public class DataConsFieldNameRenamer_Test extends TestCase {
    
    /**
     * A copy of CAL services for use in the test cases
     */
    private static BasicCALServices leccServices;

    /**
     * Constructor for DataConsFieldNameRenamer.
     * 
     * @param name
     *            the name of the test.
     */
    public DataConsFieldNameRenamer_Test(String name) {
        super(name);
    }

    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(DataConsFieldNameRenamer_Test.class);

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
    
    public void testAllRenamingIssues() {
        String template =
            "module " + CALPlatformTestModuleNames.CALRenaming_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + ";\n" +
            "data T =\n" +
            "   DC1\n" +
            "       %a :: ()\n" +
            "       b :: () |\n" +
            "   DC2\n" +
            "       %a :: () |\n" +
            "   DC3\n" +
            "       a :: [()]\n" +
            "       %c :: () |\n" +
            "   DC4\n" +
            "       %#4 :: ()\n" +
            "   ;\n" +
            "func =\n" +
            "   case Prelude.undefined of\n" +
            "   (DC1|DC2) {%a@PUNa} -> a;\n" +
            "   DC3 {%c=a} -> ();\n" +
            "   DC4 {%#4@PUN#4} -> ();\n" +
            "   ;\n";
        
        final String oldSourceText = template
            .replaceAll("%a", "a").replaceAll("@PUNa", "")
            .replaceAll("%c", "c")
            .replaceAll("%#4", "#4").replaceAll("@PUN#4", "");
        
        final ModuleName moduleName = CALPlatformTestModuleNames.CALRenaming_Test_Support1;
        final ModuleTypeInfo moduleTypeInfo = leccServices.getWorkspaceManager().getModuleTypeInfo(moduleName);
        CompilerMessageLogger logger = new MessageLogger();
        
        final Map<IdentifierInfo.DataConsFieldName, Pair<Set<IdentifierInfo.TopLevel.DataCons>, FieldName>> renamings =
            new LinkedHashMap<IdentifierInfo.DataConsFieldName, Pair<Set<IdentifierInfo.TopLevel.DataCons>, FieldName>>();
        
        final IdentifierInfo.TopLevel.DataCons dc1 = new IdentifierInfo.TopLevel.DataCons(QualifiedName.make(moduleName, "DC1"));
        final IdentifierInfo.TopLevel.DataCons dc2 = new IdentifierInfo.TopLevel.DataCons(QualifiedName.make(moduleName, "DC2"));
        final IdentifierInfo.TopLevel.DataCons dc3 = new IdentifierInfo.TopLevel.DataCons(QualifiedName.make(moduleName, "DC3"));
        final IdentifierInfo.TopLevel.DataCons dc4 = new IdentifierInfo.TopLevel.DataCons(QualifiedName.make(moduleName, "DC4"));
        
        final Set<IdentifierInfo.TopLevel.DataCons> dc1dc2 = new HashSet<IdentifierInfo.TopLevel.DataCons>(Arrays.asList(dc1, dc2));
        
        renamings.put(new IdentifierInfo.DataConsFieldName(FieldName.make("a"), Collections.singleton(dc1)), Pair.make(dc1dc2, FieldName.make("c")));
        renamings.put(new IdentifierInfo.DataConsFieldName(FieldName.make("c"), Collections.singleton(dc3)), Pair.make(Collections.singleton(dc3), FieldName.make("#3")));
        renamings.put(new IdentifierInfo.DataConsFieldName(FieldName.make("#4"), Collections.singleton(dc4)), Pair.make(Collections.singleton(dc4), FieldName.make("z")));
        
        String renamedSourceText = oldSourceText;
        SourceModifier sourceModifier;
        
        for (final Map.Entry<IdentifierInfo.DataConsFieldName, Pair<Set<IdentifierInfo.TopLevel.DataCons>, FieldName>> renaming : renamings.entrySet()) {
            
            sourceModifier = DataConsFieldNameRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, renaming.getValue().fst(), renaming.getKey().getFieldName(), renaming.getValue().snd(), logger);
            assertNoCompilationErrors(logger);
            renamedSourceText = sourceModifier.apply(renamedSourceText);
        }
        
        final String newSourceText = template
            .replaceAll("%a", "c").replaceAll("@PUNa", " = a")
            .replaceAll("%c", "#3")
            .replaceAll("%#4", "z").replaceAll("@PUN#4", " = _");
        
        assertEquals(newSourceText, renamedSourceText);
    }
    
    private void assertNoCompilationErrors(final CompilerMessageLogger logger) {
        assertEquals("Compilation error", Collections.EMPTY_LIST, logger.getCompilerMessages(Severity.ERROR));
    }

}
