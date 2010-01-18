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
 * TypeVariableRenamer_Test.java
 * Created: Sep 29, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.compiler.IdentifierOccurrence.Binding;
import org.openquark.cal.compiler.IdentifierOccurrenceFinder.FinderState;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable;
import org.openquark.cal.compiler.IdentifierResolver.TypeVariableScope;
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.util.Pair;

/**
 * JUnit test cases for the {@link TypeVariableRenamer}.
 *
 * @author Joseph Wong
 */
public class TypeVariableRenamer_Test extends TestCase {

    
    /**
     * A copy of CAL services for use in the test cases
     */
    private static BasicCALServices leccServices;

    /**
     * Constructor for TypeVariableRenamer_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public TypeVariableRenamer_Test(String name) {
        super(name);
    }

    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(TypeVariableRenamer_Test.class);

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
    public void testAllShadowingIssues() {
        String template =
            "module " + CALPlatformTestModuleNames.CALRenaming_Test_Support1 + ";\n" +
            "import " + CAL_Prelude.MODULE_NAME + ";\n" +
            "class (Prelude.Ord %c) => C1 %c where\n" +
            "   m1a :: %c -> @w -> %c;\n" +
            "   m1b :: %c;\n" +
            "   ;\n" +
            "class C2 @b where\n" +
            "   m2a :: @b -> %x;\n" +
            "   m2b :: @b -> b_1;\n" +
            "   ;\n" +
            "data T @e %f =\n" +
            "   D1 foo::@e bar::%f |\n" +
            "   D2 foo::%f bar::(@e->%f->@e)\n" +
            "   ;\n" +
            "func :: %p -> @q -> r;\n" +
            "func =\n" +
            "   let" +
            "       inner :: p -> q_1;\n" +
            "       inner = Prelude.undefined;\n" +
            "   in\n" +
            "       Prelude.undefined;\n";
        
        final String oldSourceText = template
            .replaceAll("%c", "c").replaceAll("@w", "w")
            .replaceAll("%x", "x").replaceAll("@b", "b")
            .replaceAll("%f", "f").replaceAll("@e", "e")
            .replaceAll("%p", "p").replaceAll("@q", "q").replaceAll("##", "@");
        
        final ModuleName moduleName = CALPlatformTestModuleNames.CALRenaming_Test_Support1;
        final ModuleTypeInfo moduleTypeInfo = leccServices.getWorkspaceManager().getModuleTypeInfo(moduleName);
        CompilerMessageLogger logger = new MessageLogger();

        // Because type variable identifier infos are invalidated under source range, therefore
        // there need to be recalculated between renaming passes
        
        final List<Pair<String, String>> targets = new ArrayList<Pair<String, String>>();
        targets.add(Pair.make("c", "w"));
        targets.add(Pair.make("x", "b"));
        targets.add(Pair.make("f", "e"));
        targets.add(Pair.make("p", "q"));
        
        String renamedSourceText = oldSourceText;
        SourceModifier sourceModifier;
        
        for (final Pair<String, String> target : targets) {
            final Map<IdentifierInfo.TypeVariable, String> renamings = new LinkedHashMap<IdentifierInfo.TypeVariable, String>();

            IdentifierOccurrenceFinder<Void> finder = new IdentifierOccurrenceFinder<Void>(moduleName) {
                private boolean seenAlready = false;
                @Override
                protected void handleTypeVariableBinding(Binding<IdentifierInfo.TypeVariable> binding, TypeVariableScope scope) {
                    final IdentifierInfo.TypeVariable identifierInfo = binding.getIdentifierInfo();
                    final String typeVarName = identifierInfo.getTypeVarName();

                    if (typeVarName.equals(target.fst()) && !seenAlready) {
                        seenAlready = true;
                        renamings.put(identifierInfo, target.snd());
                    }
                }
            };

            final SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(renamedSourceText, logger);
            assertNoCompilationErrors(logger);

            moduleDefn.accept(finder, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(moduleTypeInfo)), FinderState.make()));

            for (final Map.Entry<IdentifierInfo.TypeVariable, String> renaming : renamings.entrySet()) {

                sourceModifier = TypeVariableRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, renaming.getKey(), renaming.getValue(), logger);
                assertNoCompilationErrors(logger);
                renamedSourceText = sourceModifier.apply(renamedSourceText);
            }
        }
        
        final String newSourceText = template
            .replaceAll("%c", "w").replaceAll("@w", "w_1")
            .replaceAll("%x", "b").replaceAll("@b", "b_2")
            .replaceAll("%f", "e").replaceAll("@e", "e_1")
            .replaceAll("%p", "q").replaceAll("@q", "q_1").replaceAll("##", "@");
        
        assertEquals(newSourceText, renamedSourceText);
    }
    
    private void assertNoCompilationErrors(final CompilerMessageLogger logger) {
        assertEquals("Compilation error", Collections.EMPTY_LIST, logger.getCompilerMessages(Severity.ERROR));
    }
}
