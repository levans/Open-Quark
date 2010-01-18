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
 * LocalNameRenamer_Test.java
 * Created: Sep 29, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.Collections;
import java.util.LinkedHashMap;
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
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable.LocalScope;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;

/**
 * JUnit test cases for the {@link LocalNameRenamer}.
 *
 * @author Joseph Wong
 */
public class LocalNameRenamer_Test extends TestCase {
    
    /**
     * A copy of CAL services for use in the test cases
     */
    private static BasicCALServices leccServices;

    /**
     * Constructor for LocalNameRenamer_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public LocalNameRenamer_Test(String name) {
        super(name);
    }

    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(LocalNameRenamer_Test.class);

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
            "testFunction %z %w %y %x =\n" +
            "let\n" +
            "    alpha = case %w of\n" +
            "    " + CAL_Prelude.DataConstructors.Just.getQualifiedName() + " @a -> @a;\n" +
            "    ;\n" +
            "    @b = %x; @a = %y;\n" +
            "in\n" +
            "    " + CAL_Prelude.Functions.tuple3.getQualifiedName() + " @b @a (\n" +
            "    let\n" +
            "        @a :: " + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + ";\n" +
            "        @a = @b;\n" +
            "        b_1 = " + CAL_Prelude.DataConstructors.False.getQualifiedName() + ";\n" +
            "        beta = case %z of\n" +
            "        {_ |a@PUNa} -> @a;;\n" +
            "        gamma = case %z of\n" +
            "        {_ |b=@a} -> @a;;\n" +
            "        delta = case %z of\n" +
            "        {_ |c=@b} -> @b;;\n" +
            "        eta = case %z of\n" +
            "        {@b | foo} -> @b;;\n" +
            "        zeta = case (1,2,3) of\n" +
            "        (x, y, @z) -> (y, @z, x);;\n" +
            "        zeta2 = case (1,2,3) of\n" +
            "        (@a, @b, b_1) -> (@b, b_1, @a);;\n" +
            "        beta_lazy = let {_ |a@PUNa} = %z; in @a;\n" +
            "        gamma_lazy = let {_ |b=@a} = %z; in @a;\n" +
            "        delta_lazy = let {_ |c=@b} = %z; in @b;\n" +
            "        zeta_lazy = let (x, y, @z) = (1.0,2.0,3.0); in (y, @z, x);\n" +
            "        zeta2_lazy = let (@a, @b, b_1) = (1.0,2.0,3.0); in (@b, b_1, @a);\n" +
            "        epsilon_lazy = let Prelude.Cons y @z = ['a']; in (y, @z);\n" +
            "        epsilon2_lazy = let Prelude.Cons {head=@b, tail=b_1} = ['a']; in (@b, b_1);\n" +
            "        theta_lazy = let y:@z = ['a']; in (y, @z);\n" +
            "        theta2_lazy = let @b:b_1 = ['a']; in (@b, b_1);\n" +
            "    in\n" +
            "        " + CAL_Prelude.Functions.tuple5.getQualifiedName() + " @a @b %x %y (\n" +
            "        let\n" +
            "            /**\n" +
            "             * ##arg @a a\n" +
            "             * ##arg @b b\n" +
            "             */\n" +
            "            f @a @b = @a + @b + %x;\n" +
            "            g = \\@b -> \\@a -> @b - @a;\n" +
            "            /**\n" +
            "             * ##arg @a a\n" +
            "             */\n" +
            "            @a @a = { a = @a };\n" +
            "            thunk :: " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + ";\n" +
            "            thunk = " + CAL_Prelude.DataConstructors.True.getQualifiedName() + ";\n" +
            "        in\n" +
            "            let\n" +
            "                x = 3.0;\n" +
            "            in\n" +
            "                (x, " + CAL_Prelude.Functions.tuple4.getQualifiedName() + " %z (\n" +
            "                let\n" +
            "                    @z = 4;\n" +
            "                in\n" +
            "                    @z) (f 3 4) (@a (1 :: " + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + ")))))\n" +
    		";\n";
        
        final String oldSourceText = template
            .replaceAll("%w", "w").replaceAll("%x", "x")
            .replaceAll("%y", "y").replaceAll("%z", "z")
            .replaceAll("@a", "a").replaceAll("@PUNa", "")
            .replaceAll("@b", "b").replaceAll("@z", "z")
            .replaceAll("##", "@");
        
        final ModuleName moduleName = CALPlatformTestModuleNames.CALRenaming_Test_Support1;
        final ModuleTypeInfo moduleTypeInfo = leccServices.getWorkspaceManager().getModuleTypeInfo(moduleName);
        CompilerMessageLogger logger = new MessageLogger();
        
        final Map<IdentifierInfo.Local, String> renamings = new LinkedHashMap<IdentifierInfo.Local, String>();
        
        IdentifierOccurrenceFinder<Void> finder = new IdentifierOccurrenceFinder<Void>(moduleName) {
            @Override
            protected void handleLocalVariableBinding(Binding<IdentifierInfo.Local> binding, LocalScope scope) {
                if (binding.getIdentifierInfo() instanceof IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod) {
                    IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod paramInfo =
                        (IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod)binding.getIdentifierInfo();
                    
                    if (paramInfo.getVarName().equals("w")) {
                        renamings.put(paramInfo, "z");
                    } else if (paramInfo.getVarName().equals("x")) {
                        renamings.put(paramInfo, "a");
                    } else if (paramInfo.getVarName().equals("y")) {
                        renamings.put(paramInfo, "a_1");
                    } else if (paramInfo.getVarName().equals("z")) {
                        renamings.put(paramInfo, "b");
                    }
                }
            }
        };
        
        final SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(oldSourceText, logger);
        assertNoCompilationErrors(logger);
        
        moduleDefn.accept(finder, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(moduleTypeInfo)), FinderState.make()));
        
        String renamedSourceText = oldSourceText;
        SourceModifier sourceModifier;
        
        for (final Map.Entry<IdentifierInfo.Local, String> renaming : renamings.entrySet()) {
            
            sourceModifier = LocalNameRenamer.getSourceModifier(moduleTypeInfo, renamedSourceText, renaming.getKey(), renaming.getValue(), logger);
            assertNoCompilationErrors(logger);
            renamedSourceText = sourceModifier.apply(renamedSourceText);
        }
        
        final String newSourceText = template
            .replaceAll("%w", "z").replaceAll("%x", "a")
            .replaceAll("%y", "a_1").replaceAll("%z", "b")
            .replaceAll("@a", "a_2").replaceAll("@PUNa", " = a_2")
            .replaceAll("@b", "b_2").replaceAll("@z", "z_1")
            .replaceAll("##", "@");
        
        assertEquals(newSourceText, renamedSourceText);
    }
    
    private void assertNoCompilationErrors(final CompilerMessageLogger logger) {
        assertEquals("Compilation error", Collections.EMPTY_LIST, logger.getCompilerMessages(Severity.ERROR));
    }
}
