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
 * SourceModelUtilities_Test.java
 * Creation date: Apr 15, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import junit.framework.TestCase;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.module.Cal.Collections.CAL_Map;
import org.openquark.cal.module.Cal.Core.CAL_Bits;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * A set of JUnit test cases for verifying the correctness of the source model utility methods.
 *
 * @author Joseph Wong
 */
public class SourceModelUtilities_Test extends TestCase {

    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;
    
    /**
     * Constructor for SourceModelUtilities_Test.
     * 
     * @param name
     *            the name of the test
     */
    public SourceModelUtilities_Test(String name) {
        super(name);
    }
    
    /**
     * Tests the method SourceModelUtilities.TextParsing.parseExprIntoSourceModel
     * to see whether it can properly handle and report on invalid source text.
     */
    public void test_parseExpr_failureCase_invalidSourceText() {
        assertNull(SourceModelUtilities.TextParsing.parseExprIntoSourceModel("$$$"));
        MessageLogger logger = new MessageLogger();
        SourceModelUtilities.TextParsing.parseExprIntoSourceModel("###", logger);
        assertEquals(CompilerMessage.Severity.ERROR, logger.getMaxSeverity());
        assertTrue(logger.getNErrors() > 0);
    }

    /**
     * Tests the method SourceModelUtilities.TextParsing.parseAlgebraicFunctionDefnIntoSourceModel
     * to see whether it can properly handle and report on invalid source text.
     */
    public void test_parseFunctionDefn_failureCase_invalidSourceText() {
        assertNull(SourceModelUtilities.TextParsing.parseAlgebraicFunctionDefnIntoSourceModel("foo = $$$;"));
        MessageLogger logger = new MessageLogger();
        SourceModelUtilities.TextParsing.parseAlgebraicFunctionDefnIntoSourceModel("foo = ###;", logger);
        assertEquals(CompilerMessage.Severity.ERROR, logger.getMaxSeverity());
        assertTrue(logger.getNErrors() > 0);
    }
    
    /**
     * Tests the method SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel
     * to see whether it can properly handle and report on invalid source text.
     */
    public void test_parseModuleDefn_failureCase_invalidSourceText() {
        assertNull(SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel("module Foo; import " + CAL_Prelude.MODULE_NAME + "; foo = $$$;"));
        MessageLogger logger = new MessageLogger();
        SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel("module Foo; import " + CAL_Prelude.MODULE_NAME + "; foo = ###;", logger);
        assertEquals(CompilerMessage.Severity.ERROR, logger.getMaxSeverity());
        assertTrue(logger.getNErrors() > 0);
    }
    
    /**
     * Tests the ImportAugmenter (and indirectly the SourceModelTraverser) for correctly
     * handling a backquote operator with a qualified function name.
     */
    public void test_importAugmenter_backquoteOperatorWithFunctionName() {
        SourceModel.Import[] imports = SourceModelUtilities.ImportAugmenter.getRequiredImports(ModuleName.make("TestModule"), SourceModel.Expr.BinaryOp.BackquotedOperator.Var.make(SourceModel.Expr.Var.make(CAL_Bits.Functions.bitwiseAnd), SourceModel.Expr.makeIntValue(1), SourceModel.Expr.makeIntValue(2)));
        boolean foundBits = false;
        for (final Import requiredImport : imports) {
            if (SourceModel.Name.Module.toModuleName(requiredImport.getImportedModuleName()).equals(CAL_Bits.MODULE_NAME)) {
                foundBits = true;
            }
        }
        assertTrue("The Bits module is not in the list of required imports", foundBits);
    }

    /**
     * Tests the ImportAugmenter (and indirectly the SourceModelTraverser) for correctly
     * handling a backquote operator with a qualified data constructor.
     */
    public void test_importAugmenter_backquoteOperatorWithDataCons() {
        SourceModel.Import[] imports = SourceModelUtilities.ImportAugmenter.getRequiredImports(ModuleName.make("TestModule"), SourceModel.Expr.BinaryOp.BackquotedOperator.DataCons.make(SourceModel.Expr.DataCons.make(CALPlatformTestModuleNames.LegacyTuple, "Tuple2"), SourceModel.Expr.makeIntValue(1), SourceModel.Expr.makeIntValue(2)));
        boolean foundBits = false;
        for (final Import requiredImport : imports) {
            if (SourceModel.Name.Module.toModuleName(requiredImport.getImportedModuleName()).equals(CALPlatformTestModuleNames.LegacyTuple)) {
                foundBits = true;
            }
        }
        assertTrue("The " + CALPlatformTestModuleNames.LegacyTuple + " module is not in the list of required imports", foundBits);
    }
    
    /**
     * Tests the ImportAugmenter for correctly handling a CALDoc checked reference appearing
     * without the context keyword.
     */
    public void test_importAugmenter_CALDocCheckedReferenceWithoutContext() {
        SourceModel.Import[] imports = SourceModelUtilities.ImportAugmenter.getRequiredImports(
            ModuleName.make("TestModule"),
            SourceModel.CALDoc.CrossReference.WithoutContextCons.make(SourceModel.Name.WithoutContextCons.make(CAL_Map.MODULE_NAME, CAL_Map.TypeConstructors.Map.getUnqualifiedName()), true));
        
        boolean foundMap = false;
        for (final Import requiredImport : imports) {
            if (SourceModel.Name.Module.toModuleName(requiredImport.getImportedModuleName()).equals(CAL_Map.MODULE_NAME)) {
                foundMap = true;
            }
        }
        // The import augmenter cannot handle references without context keywords, checked or unchecked,
        // so no new imports should have been gathered.
        assertFalse("The Map module is in the list of required imports, when it shouldn't be.", foundMap);
    }
    
    /**
     * Tests the ImportAugmenter for correctly handling a CALDoc unchecked reference appearing
     * without the context keyword.
     */
    public void test_importAugmenter_CALDocUncheckedReferenceWithoutContext() {
        SourceModel.Import[] imports = SourceModelUtilities.ImportAugmenter.getRequiredImports(
            ModuleName.make("TestModule"),
            SourceModel.CALDoc.CrossReference.WithoutContextCons.make(SourceModel.Name.WithoutContextCons.make(CAL_Map.MODULE_NAME, CAL_Map.TypeConstructors.Map.getUnqualifiedName()), false));
        
        boolean foundMap = false;
        for (final Import requiredImport : imports) {
            if (SourceModel.Name.Module.toModuleName(requiredImport.getImportedModuleName()).equals(CAL_Map.MODULE_NAME)) {
                foundMap = true;
            }
        }
        assertFalse("The Map module is in the list of required imports, when it shouldn't be.", foundMap);
    }
    
    /**
     * Tests the Enumeration generator for correctly generating a type constructor definition
     * with the appropriate deriving clause, with all the derived instances (Eq, Ord, Bounded, Enum).
     */
    public void testEnumerationGenerationForDerivingClause_AllDerivedInstances() {
        
        SourceModelUtilities.Enumeration enumeration = new SourceModelUtilities.Enumeration(QualifiedName.make(ModuleName.make("TestModule"), "TestEnum"), Scope.PRIVATE, new String[] {"Foo", "Bar", "Baz"}, false);
        
        SourceModel.TopLevelSourceElement[] elements = enumeration.toSourceElements();
        
        boolean derivesEq = false;
        boolean derivesOrd = false;
        boolean derivesBounded = false;
        boolean derivesEnum = false;
        
        for (final TopLevelSourceElement element : elements) {
            if (element instanceof SourceModel.TypeConstructorDefn) {
                SourceModel.TypeConstructorDefn typeCons = (SourceModel.TypeConstructorDefn)element;
                
                int nDerivingClauseTypeClassNames = typeCons.getNDerivingClauseTypeClassNames();
                
                for (int j = 0; j < nDerivingClauseTypeClassNames; j++) {
                    SourceModel.Name.TypeClass name = typeCons.getDerivingClauseTypeClassName(j);
                    if (CAL_Prelude.MODULE_NAME.equals(SourceModel.Name.Module.maybeToModuleName(name.getModuleName()))) {
                        if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Eq.getUnqualifiedName())) {
                            derivesEq = true;
                        } else if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Ord.getUnqualifiedName())) {
                            derivesOrd = true;
                        } else if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Bounded.getUnqualifiedName())) {
                            derivesBounded = true;
                        } else if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Enum.getUnqualifiedName())) {
                            derivesEnum = true;
                        }
                    }
                }
            }
        }
        
        assertTrue("The generated enumeration does not derive Eq", derivesEq);
        assertTrue("The generated enumeration does not derive Ord", derivesOrd);
        assertTrue("The generated enumeration does not derive Bounded", derivesBounded);
        assertTrue("The generated enumeration does not derive Enum", derivesEnum);
    }

    /**
     * Tests the Enumeration generator for correctly generating a type constructor definition
     * with the appropriate deriving clause, with only the Eq instance.
     */
    public void testEnumerationGenerationForDerivingClause_EqInstanceOnly() {
        
        SourceModelUtilities.Enumeration enumeration = new SourceModelUtilities.Enumeration(QualifiedName.make(ModuleName.make("TestModule"), "TestEnum"), Scope.PRIVATE, new String[] {"Foo", "Bar", "Baz"}, true);
        
        SourceModel.TopLevelSourceElement[] elements = enumeration.toSourceElements();
        
        boolean derivesEq = false;
        boolean derivesOrd = false;
        boolean derivesBounded = false;
        boolean derivesEnum = false;
        
        for (final TopLevelSourceElement element : elements) {
            if (element instanceof SourceModel.TypeConstructorDefn) {
                SourceModel.TypeConstructorDefn typeCons = (SourceModel.TypeConstructorDefn)element;
                
                int nDerivingClauseTypeClassNames = typeCons.getNDerivingClauseTypeClassNames();
                
                for (int j = 0; j < nDerivingClauseTypeClassNames; j++) {
                    SourceModel.Name.TypeClass name = typeCons.getDerivingClauseTypeClassName(j);
                    if (CAL_Prelude.MODULE_NAME.equals(SourceModel.Name.Module.maybeToModuleName(name.getModuleName()))) {
                        if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Eq.getUnqualifiedName())) {
                            derivesEq = true;
                        } else if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Ord.getUnqualifiedName())) {
                            derivesOrd = true;
                        } else if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Bounded.getUnqualifiedName())) {
                            derivesBounded = true;
                        } else if (name.getUnqualifiedName().equals(CAL_Prelude.TypeClasses.Enum.getUnqualifiedName())) {
                            derivesEnum = true;
                        }
                    }
                }
            }
        }
        
        assertTrue("The generated enumeration does not derive Eq", derivesEq);
        assertFalse("The generated enumeration derives Ord", derivesOrd);
        assertFalse("The generated enumeration derives Bounded", derivesBounded);
        assertFalse("The generated enumeration derives Enum", derivesEnum);
    }

}
