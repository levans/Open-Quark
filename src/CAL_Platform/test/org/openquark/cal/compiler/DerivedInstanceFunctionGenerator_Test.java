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
 * DerivedInstanceFunctionGenerator_Test.java
 * Creation date: Apr 25, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.FileSystemHelper;


/**
 * A set of test cases for the {@link DerivedInstanceFunctionGenerator}.
 *
 * @author Joseph Wong
 */
public class DerivedInstanceFunctionGenerator_Test extends TestCase {

    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;

    /**
     * Tests the method {@link DerivedInstanceFunctionGenerator#makeAlgebraicTypeInstanceFunctions}
     * by using it to generate some instances for a programmatic module, then inserting them into the
     * module and compiling it again, and finally running some CAL tests on the instances.
     * @throws CALExecutorException 
     * @throws GemCompilationException 
     */
    public void testMakeAlgebraicTypeInstanceFunctions() throws GemCompilationException, CALExecutorException {
        
        BasicCALServices privateCopyLeccServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(
            MachineType.LECC,
            "org.openquark.cal.test.workspace.DerivedInstanceFunctionGenerator_Test.testMakeAlgebraicTypeInstanceFunctions",
            "ice.default.cws",
            null, "DIFG_Test", false); // we use an abbreviated name here so that the path names are not too long (for running in lecc java source mode)
        
        try {
            MessageLogger logger = new MessageLogger();
            
            privateCopyLeccServices.compileWorkspace(null, logger);
            if (logger.getNErrors() > 0) {
                Assert.fail("Compilation of workspace failed: " + logger.getCompilerMessages());
            }
            
            final ModuleName moduleName = ModuleName.make("Test_MakeAlgebraicTypeInstanceFunctions");
            
            SourceModel.ModuleDefn moduleDefn =
                SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(
                    "module " + moduleName + ";\n" +
                    "import " + CAL_Prelude.MODULE_NAME + ";\n" +
                    "import " + CAL_Debug.MODULE_NAME + ";\n" +
                    "import " + CALPlatformTestModuleNames.DerivedInstanceFunctionGenerator_Test_Support + ";\n" +
                    "data Unary = U1;\n" +
                    "data Binary = B1 | B2;\n" +
                    "data Ternary = T1 | T2 | T3;\n" +
                    "data MyWrap = MyWrap #1 :: " + CAL_Prelude.MODULE_NAME + ".Char;\n" +
                    "data MyBottomMiddleTop a = Bottom | Middle arg :: a | Top;\n" +
                    "data MyFooBarBaz a b = MyFoo arg :: a | MyBar | MyBaz arg:: b;\n" +
                    "data MyTuple3B a b c = MyTuple3B\n" + 
                    "        #1       :: a\n" + 
                    "        field1   :: b\n" + 
                    "        field1_1 :: c;\n",
                    logger);
            
            if (logger.getNErrors() > 0) {
                Assert.fail("Parsing of module definition failed: " + logger.getCompilerMessages());
            }
            
            WorkspaceManager workspaceManager = privateCopyLeccServices.getWorkspaceManager();
            
            workspaceManager.makeModule(new SourceModelModuleSource(moduleDefn), logger);
            if (logger.getNErrors() > 0) {
                Assert.fail("Compilation of test module (before adding instances) failed: " + logger.getCompilerMessages());
            }
            
            String[] typeNames = new String[] {
                "Unary", "Binary", "Ternary", "MyWrap", "MyBottomMiddleTop", "MyFooBarBaz", "MyTuple3B"
            };
            
            SourceModel.Expr[][] correspTestValues = new SourceModel.Expr[][] {
                parseExprs(new String[] {"U1"}),
                parseExprs(new String[] {"B1", "B2"}),
                parseExprs(new String[] {"T1", "T2", "T3"}),
                parseExprs(new String[] {"MyWrap 'a'", "MyWrap 'b'", "MyWrap 'c'"}),
                parseExprs(new String[] {"Bottom", "Middle 'a'", "Middle 'b'", "Middle 'c'", "Top"}),
                parseExprs(new String[] {"MyFoo 'a'", "MyFoo 'b'", "MyFoo 'c'", "MyBar", "MyBaz 'a'", "MyBaz 'b'", "MyBaz 'c'"}),
                parseExprs(new String[] {
                    "MyTuple3B 'a' 'a' 'a'",
                    "MyTuple3B 'a' 'a' 'b'",
                    "MyTuple3B 'a' 'b' 'a'",
                    "MyTuple3B 'a' 'b' 'b'",
                    "MyTuple3B 'b' 'a' 'a'",
                    "MyTuple3B 'b' 'a' 'b'",
                    "MyTuple3B 'b' 'b' 'a'",
                    "MyTuple3B 'b' 'b' 'b'"})
            };
            
            SourceModel.ModuleDefn compactImplModuleDefn = addInstancesAndTests(moduleDefn, workspaceManager, typeNames, correspTestValues, true, "testAllCompact");
            
            if (SHOW_DEBUGGING_OUTPUT) {
                System.out.println("Module with compact instance implementations:");
                System.out.println(compactImplModuleDefn);
            }
            
            workspaceManager.makeModule(new SourceModelModuleSource(compactImplModuleDefn), logger);
            if (logger.getNErrors() > 0) {
                Assert.fail("Compilation of compact instance implementations failed: " + logger.getCompilerMessages());
            }
            
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(moduleName, "testAllCompact"), privateCopyLeccServices);
            
            SourceModel.ModuleDefn efficientImplModuleDefn = addInstancesAndTests(moduleDefn, workspaceManager, typeNames, correspTestValues, false, "testAllEfficient");
            
            if (SHOW_DEBUGGING_OUTPUT) {
                System.out.println("Module with efficient instance implementations:");
                System.out.println(efficientImplModuleDefn);
            }
            
            workspaceManager.makeModule(new SourceModelModuleSource(efficientImplModuleDefn), logger);
            if (logger.getNErrors() > 0) {
                Assert.fail("Compilation of efficient instance implementations failed: " + logger.getCompilerMessages());
            }

            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(moduleName, "testAllEfficient"), privateCopyLeccServices);
        
        } finally {
            File rootDir =  CALServicesTestUtilities.getWorkspaceRoot(null, "DIFG_Test");
            
            boolean deleted = FileSystemHelper.delTree(rootDir);
            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                System.err.println("Workspace directory not deleted: " + rootDir);
            }
        }
    }

    /**
     * Adds instances and tests of the specified types to the given module definition.
     * @return a new module definition with the additional instances.
     */
    private SourceModel.ModuleDefn addInstancesAndTests(SourceModel.ModuleDefn moduleDefn, WorkspaceManager workspaceManager, String[] typeNames, SourceModel.Expr[][] correspTestValues, boolean generateCompactImpl, String testFunctionName) {
        
        List<SourceModel.TopLevelSourceElement> newDefns = new ArrayList<TopLevelSourceElement>(Arrays.asList(moduleDefn.getTopLevelDefns()));

        SourceModel.Expr testDefn = SourceModel.Expr.makeBooleanValue(true);
        
        ModuleTypeInfo debugModuleTypeInfo = workspaceManager.getModuleTypeInfo(CAL_Debug.MODULE_NAME);
        
        for (int i = 0; i < typeNames.length; i++) {
            ModuleTypeInfo moduleTypeInfo = CALServicesTestUtilities.getUnrestrictedModuleTypeInfo(workspaceManager, SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName()));
            TypeConstructor typeCons = moduleTypeInfo.getTypeConstructor(typeNames[i]);
            
            newDefns.addAll(Arrays.asList(
                DerivedInstanceFunctionGenerator.makeAlgebraicTypeInstanceFunctions(moduleTypeInfo, typeCons, generateCompactImpl, debugModuleTypeInfo)));
            
            testDefn = SourceModel.Expr.BinaryOp.And.make(
                testDefn,
                SourceModel.Expr.makeGemCall(
                    QualifiedName.make(CALPlatformTestModuleNames.DerivedInstanceFunctionGenerator_Test_Support, "testOrdInstanceWithValuesInAccendingOrder"),
                    SourceModel.Expr.List.make(correspTestValues[i])));
        }
        
        SourceModel.FunctionDefn.Algebraic testAll = SourceModel.FunctionDefn.Algebraic.make(testFunctionName, Scope.PUBLIC, new SourceModel.Parameter[0], testDefn);
        
        newDefns.add(testAll);
        
        return SourceModel.ModuleDefn.make(
            moduleDefn.getCALDocComment(),
            moduleDefn.getModuleName(),
            moduleDefn.getImportedModules(),
            moduleDefn.getFriendModules(),
            newDefns.toArray(new SourceModel.TopLevelSourceElement[0]));
    }
    
    /**
     * @return an array of source model expressions representing the textual expressions.
     */
    private SourceModel.Expr[] parseExprs(String[] textExprs) {
        SourceModel.Expr[] sourceModelExprs = new SourceModel.Expr[textExprs.length];
        
        for (int i = 0; i < textExprs.length; i++) {
            sourceModelExprs[i] = SourceModelUtilities.TextParsing.parseExprIntoSourceModel(textExprs[i]);
        }
        
        return sourceModelExprs;
    }
}
