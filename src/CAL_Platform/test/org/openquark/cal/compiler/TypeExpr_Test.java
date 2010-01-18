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
 * TypeExpr_Test.java
 * Creation date: Mar 1, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.GemEntity;
import org.openquark.util.NakedByteArrayOutputStream;


/**
 * A set of JUnit test cases for verifying the correctness of the type expression classes.
 *
 * @author Joseph Wong
 */
public class TypeExpr_Test extends TestCase {
    
    private static final String Prelude_Boolean = CAL_Prelude.TypeConstructors.Boolean.getQualifiedName();
    private static final String Prelude_Char = CAL_Prelude.TypeConstructors.Char.getQualifiedName();
    private static final String Prelude_Int = CAL_Prelude.TypeConstructors.Int.getQualifiedName();
    private static final String Prelude_Double = CAL_Prelude.TypeConstructors.Double.getQualifiedName();
    private static final String Prelude_String = CAL_Prelude.TypeConstructors.String.getQualifiedName();
    private static final String Prelude_Function = CAL_Prelude.TypeConstructors.Function.getQualifiedName();
    private static final String Prelude_JObject = CAL_Prelude.TypeConstructors.JObject.getQualifiedName();
    private static final String Prelude_Maybe = CAL_Prelude.TypeConstructors.Maybe.getQualifiedName();
    private static final String Prelude_Either = CAL_Prelude.TypeConstructors.Either.getQualifiedName();

    private static final String Prelude_Eq = CAL_Prelude.TypeClasses.Eq.getQualifiedName();
    private static final String Prelude_Num = CAL_Prelude.TypeClasses.Num.getQualifiedName();
    private static final String Prelude_Enum = CAL_Prelude.TypeClasses.Enum.getQualifiedName();
    private static final String Prelude_Ord = CAL_Prelude.TypeClasses.Ord.getQualifiedName();
    private static final String Prelude_Inputable = CAL_Prelude.TypeClasses.Inputable.getQualifiedName();
    private static final String Prelude_Outputable = CAL_Prelude.TypeClasses.Outputable.getQualifiedName();

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    
    /**
     * The default module for all tests
     */
    private static final ModuleName testModule = CALServicesTestUtilities.DEFAULT_UNIT_TEST_MODULE;

    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(TypeExpr_Test.class);

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
     * Get the type info for the testing module
     * @return the test module type info
     */
    private static ModuleTypeInfo getTestModuleTypeInfo() {
        return leccCALServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo();     
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
     * Constructor for TypeExpr_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public TypeExpr_Test(String name) {
        super(name);
    }
    
    /**
     * A helper function to show that algorithms work with instantiated type variables.
     * @param instanceType
     * @return TypeVar
     */
    private static TypeVar makeInstantiatedTypeVar(TypeExpr instanceType) {
        TypeVar result = new TypeVar();
        result.setInstance(instanceType);
        return result;
    }
    
    private static void assertTypeArrayEquals(TypeExpr[] ta1, TypeExpr[] ta2) {
        int length = ta1.length;
        assertEquals(length, ta2.length);
        for (int i = 0; i < length; ++i) {
            assertTrue(ta1[i] == ta2[i]);
        }
    }
    
    /**
     * Various tests of the TypeApp class. This was introduced to support higher kinded type variables.    
     * 
     * This particular test checks that the multiple ways of encoding the type Int -> Char result in 
     * consistent answers for various TypeExpr api functions.
     * 
     * It also makes some checks on an unsaturated application of Prelude.Function i.e. Function Int.
     */
    public void testTypeApp1() {
        
        TypeExpr intType = leccCALServices.getTypeFromString(testModule, Prelude_Int);
        TypeExpr charType = leccCALServices.getTypeFromString(testModule, Prelude_Char);
        
        //3 basic ways of encoding the type Int -> Char
        TypeExpr intToChar1 =
            new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[]{intType, charType});           
        TypeExpr intToChar2 =
            new TypeApp(new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[]{intType}), charType);
        TypeExpr intToChar3 =
            new TypeApp(new TypeApp(new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[0]), intType), charType);
        
        //a few more ways, with instantiated type variables
        TypeExpr intToChar4 =
            makeInstantiatedTypeVar(new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[]{makeInstantiatedTypeVar(intType), makeInstantiatedTypeVar(charType)}));           
        TypeExpr intToChar5 =
            makeInstantiatedTypeVar(new TypeApp(makeInstantiatedTypeVar(new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[]{makeInstantiatedTypeVar(intType)})), makeInstantiatedTypeVar(charType)));
        TypeExpr intToChar6 =
            makeInstantiatedTypeVar(new TypeApp(makeInstantiatedTypeVar(new TypeApp(makeInstantiatedTypeVar(new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[0])), makeInstantiatedTypeVar(intType))), makeInstantiatedTypeVar(charType)));           
                     
        assertEquals(Prelude_Int + " -> " + Prelude_Char, intToChar1.toString());
        assertEquals(Prelude_Int + " -> " + Prelude_Char, intToChar2.toString());
        assertEquals(Prelude_Int + " -> " + Prelude_Char, intToChar3.toString());
        assertEquals(Prelude_Int + " -> " + Prelude_Char, intToChar4.toString());
        assertEquals(Prelude_Int + " -> " + Prelude_Char, intToChar5.toString());
        assertEquals(Prelude_Int + " -> " + Prelude_Char, intToChar6.toString());        
               
        assertEquals(1, intToChar1.getArity());
        assertEquals(1, intToChar2.getArity());
        assertEquals(1, intToChar3.getArity());
        assertEquals(1, intToChar4.getArity());
        assertEquals(1, intToChar5.getArity());
        assertEquals(1, intToChar6.getArity());   
             
        assertTrue(intToChar1.isFunctionType());
        assertTrue(intToChar2.isFunctionType());
        assertTrue(intToChar3.isFunctionType());
        assertTrue(intToChar4.isFunctionType());
        assertTrue(intToChar5.isFunctionType());
        assertTrue(intToChar6.isFunctionType());
        
        TypeExpr[] intToCharPieces = new TypeExpr[] {intType, charType};
        assertTypeArrayEquals(intToChar1.getTypePieces(), intToCharPieces); 
        assertTypeArrayEquals(intToChar2.getTypePieces(), intToCharPieces); 
        assertTypeArrayEquals(intToChar3.getTypePieces(), intToCharPieces); 
        assertTypeArrayEquals(intToChar4.getTypePieces(), intToCharPieces); 
        assertTypeArrayEquals(intToChar5.getTypePieces(), intToCharPieces); 
        assertTypeArrayEquals(intToChar6.getTypePieces(), intToCharPieces);   
        
        ModuleTypeInfo moduleTypeInfo = getTestModuleTypeInfo();
        
        assertTrue(TypeExpr.canUnifyType(intToChar1, intToChar2, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intToChar1, intToChar3, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intToChar2, intToChar3, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intToChar4, intToChar5, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intToChar4, intToChar6, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intToChar5, intToChar6, moduleTypeInfo));    
        
        assertTrue(TypeExpr.canPatternMatch(intToChar1, intToChar2, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar1, intToChar3, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar2, intToChar3, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar4, intToChar5, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar4, intToChar6, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar5, intToChar6, moduleTypeInfo));          
        assertTrue(TypeExpr.canPatternMatch(intToChar2, intToChar1, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar3, intToChar1, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar3, intToChar2, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar5, intToChar4, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar6, intToChar4, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intToChar6, intToChar5, moduleTypeInfo));    
        
        //2 ways of representing the type Function Int
        TypeExpr intTo1 =
            new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[]{intType});           
        TypeExpr intTo2 =
            new TypeApp(new TypeConsApp(TypeConstructor.FUNCTION, new TypeExpr[0]), intType);   
        
        assertEquals(Prelude_Function + " " + Prelude_Int, intTo1.toString());
        assertEquals(Prelude_Function + " " + Prelude_Int, intTo2.toString());
        
        assertEquals(0, intTo1.getArity());
        assertEquals(0, intTo2.getArity());
        
        assertFalse(intTo1.isFunctionType());
        assertFalse(intTo2.isFunctionType());
        
        assertTypeArrayEquals(intTo1.getTypePieces(), new TypeExpr[] {intTo1}); 
        assertTypeArrayEquals(intTo2.getTypePieces(), new TypeExpr[] {intTo2});
        
        assertTrue(TypeExpr.canUnifyType(intTo1, intTo2, moduleTypeInfo));
                             
        assertTrue(TypeExpr.canPatternMatch(intTo1, intTo2, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intTo2, intTo1, moduleTypeInfo));
        
        assertFalse(TypeExpr.canUnifyType(intTo1, intToChar1, moduleTypeInfo));
        assertFalse(TypeExpr.canUnifyType(intTo1, intToChar2, moduleTypeInfo));
        assertFalse(TypeExpr.canUnifyType(intTo1, intToChar3, moduleTypeInfo));
        assertFalse(TypeExpr.canUnifyType(intTo2, intToChar1, moduleTypeInfo));
        assertFalse(TypeExpr.canUnifyType(intTo2, intToChar2, moduleTypeInfo));
        assertFalse(TypeExpr.canUnifyType(intTo2, intToChar3, moduleTypeInfo)); 
        
        assertFalse(TypeExpr.canPatternMatch(intTo1, intToChar1, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intTo1, intToChar2, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intTo1, intToChar3, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intTo2, intToChar1, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intTo2, intToChar2, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intTo2, intToChar3, moduleTypeInfo)); 
        
        assertFalse(TypeExpr.canPatternMatch(intToChar1, intTo1, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intToChar2, intTo2, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intToChar3, intTo1, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intToChar1, intTo2, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intToChar2, intTo1, moduleTypeInfo));
        assertFalse(TypeExpr.canPatternMatch(intToChar3, intTo2, moduleTypeInfo));         
        
        
    }
    
    /**
     * Various tests of the TypeApp class. This was introduced to support higher kinded type variables.    
     * 
     * This particular test checks that the multiple ways of encoding the type [Int] result in 
     * consistent answers for various TypeExpr api functions.
     */
    public void testTypeApp2() {
        TypeExpr intType = leccCALServices.getTypeFromString(testModule, Prelude_Int);
        ModuleTypeInfo moduleTypeInfo = getTestModuleTypeInfo();
        TypeConstructor listTypeCons = moduleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.List);
      
        //2 basic ways of encoding the type Int -> Char
        TypeExpr intList1 =
            new TypeConsApp(listTypeCons, new TypeExpr[]{intType});           
        TypeExpr intList2 =
            new TypeApp(new TypeConsApp(listTypeCons, new TypeExpr[0]), intType);
     
        //a few more ways, with instantiated type variables
        TypeExpr intList3 =
            makeInstantiatedTypeVar(new TypeConsApp(listTypeCons, new TypeExpr[]{makeInstantiatedTypeVar(intType)}));           
        TypeExpr intList4 =
            makeInstantiatedTypeVar(new TypeApp(makeInstantiatedTypeVar(new TypeConsApp(listTypeCons, new TypeExpr[0])), makeInstantiatedTypeVar(intType)));
        
        assertEquals("[" + Prelude_Int + "]", intList1.toString());
        assertEquals("[" + Prelude_Int + "]", intList2.toString());
        assertEquals("[" + Prelude_Int + "]", intList3.toString());
        assertEquals("[" + Prelude_Int + "]", intList4.toString());
                      
        assertEquals(0, intList1.getArity());
        assertEquals(0, intList2.getArity());
        assertEquals(0, intList3.getArity());
        assertEquals(0, intList4.getArity());
                              
        assertTrue(TypeExpr.canUnifyType(intList1, intList2, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intList1, intList3, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intList1, intList4, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intList2, intList3, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intList2, intList4, moduleTypeInfo));
        assertTrue(TypeExpr.canUnifyType(intList3, intList4, moduleTypeInfo));    
        
        assertTrue(TypeExpr.canPatternMatch(intList1, intList2, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList1, intList3, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList1, intList4, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList2, intList3, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList2, intList4, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList3, intList4, moduleTypeInfo));    
        
        assertTrue(TypeExpr.canPatternMatch(intList2, intList1, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList3, intList1, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList4, intList1, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList3, intList2, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList4, intList2, moduleTypeInfo));
        assertTrue(TypeExpr.canPatternMatch(intList4, intList3, moduleTypeInfo));             
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match Int with Double.
     */
    public void testCanPatternMatch_failureCase_Int_Double() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int),
            leccCALServices.getTypeFromString(testModule, Prelude_Double),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match Int->Double with a->a.
     * 
     * Regression test for bug identified in changelist 60611.
     */
    public void testCanPatternMatch_failureCase_Int2Double_A2A() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
            leccCALServices.getTypeFromString(testModule, "a -> a"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match Int->Double with Num a => a->a.
     */
    public void testCanPatternMatch_failureCase_Int2Double_NumA2A() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> a"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching Int with Int.
     */
    public void testCanPatternMatch_successCase_Int_Int() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int),
            leccCALServices.getTypeFromString(testModule, Prelude_Int),
            getTestModuleTypeInfo()));
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching Int with Num a => a.
     */
    public void testCanPatternMatch_successCase_Int_Num() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int),
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching Int->Double with (Num a, Num b) => a->b.
     */
    public void testCanPatternMatch_successCase_Int2Double_NumA2NumB() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
            leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching Int->Int with (Num a, Num b) => a->b.
     */
    public void testCanPatternMatch_successCase_Int2Int_NumA2NumB() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
            leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching Int->Double with Num a => a->b.
     */
    public void testCanPatternMatch_successCase_Int2Double_NumA2B() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> b"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching {#3 :: Int}->Double with {#3 :: a}->b.
     */
    public void testCanPatternMatch_successCase_RecordWithIntField_RecordWithParametricField() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
            leccCALServices.getTypeFromString(testModule, "{#3 :: a} -> b"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching (r\#3, r\foo) => {r | #3 :: Int}->Double with r\#3 => {r | #3 :: a}->b.
     */
    public void testCanPatternMatch_successCase_ParametricRecordWithIntField_ParametricRecordWithParametricField() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
            leccCALServices.getTypeFromString(testModule, "r\\#3 => {r | #3 :: a} -> b"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching (r\#3, r\foo) => {r | #3 :: Int}->{r | foo :: Double} with
     * (r\#3, r\foo) => {r | #3 :: a}->{r | foo :: b}.
     */
    public void testCanPatternMatch_successCase_SharedBaseRecordVar() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: a} -> {r | foo :: b}"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching (r\#3, r\foo) => {r | #3 :: Int}->{r | foo :: Double} with
     * itself.
     */
    public void testCanPatternMatch_successCase_SharedBaseRecordVar2() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching c->c with a->b.
     */
    public void testCanPatternMatch_successCase_C2C_A2B() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "c -> c"),
            leccCALServices.getTypeFromString(testModule, "a -> b"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching Num a => a with Eq a => a.
     */
    public void testCanPatternMatch_successCase_Num_Eq() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
            leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a"),
            getTestModuleTypeInfo()));        
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return true,
     * for pattern matching [String] -> Boolean with Eq a => a -> Boolean.
     * 
     * Regression test for bug identified in changelist 181772.
     */
    public void testCanPatternMatch_successCase_ListOfString2Boolean_EqA2Boolean() {
        assertTrue(TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "[" + Prelude_String + "] -> " + Prelude_Boolean),
            leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a -> " + Prelude_Boolean),
            getTestModuleTypeInfo()));        
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match [JObject] -> Boolean with Eq a => a -> Boolean.
     * 
     * Regression test for bug identified in changelist 181772.
     */
    public void testCanPatternMatch_failureCase_ListOfObject2Boolean_EqA2Boolean() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "[" + Prelude_JObject + "] -> " + Prelude_Boolean),
            leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a -> " + Prelude_Boolean),
            getTestModuleTypeInfo()));        
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match a\abc => {a} -> {abc :: String} with
     * a\abc => {a} -> {a | abc :: String}.
     * 
     * Regression test for bug identified in changelist 157605.
     */
    public void testCanPatternMatch_failureCase_PolymorphicRecord1() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "a\\abc => {a} -> {abc :: " + Prelude_String + "}"),
            leccCALServices.getTypeFromString(testModule, "a\\abc => {a} -> {a | abc :: " + Prelude_String + "}"),
            getTestModuleTypeInfo()));        
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match r\felix => {r | felix :: a} -> {r} with
     * r\felix => {r | felix :: a} -> a.
     * 
     * Regression test for bug identified in changelist 157605.
     */
    public void testCanPatternMatch_failureCase_PolymorphicRecord2() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "r\\felix => {r | felix :: a} -> {r}"),
            leccCALServices.getTypeFromString(testModule, "r\\felix => {r | felix :: a} -> a"),
            getTestModuleTypeInfo()));        
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match r\felix => {r | felix :: a} -> Char with
     * r\felix => {r | felix :: a} -> a.
     * 
     * Regression test for bug identified in changelist 157605.
     */
    public void testCanPatternMatch_failureCase_PolymorphicRecord3() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "r\\felix => {r | felix :: a} -> " + Prelude_Char),
            leccCALServices.getTypeFromString(testModule, "r\\felix => {r | felix :: a} -> a"),
            getTestModuleTypeInfo()));        
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match [a] -> Char with [a] -> a.
     * 
     * Regression test for bug identified in changelist 157605.
     */
    public void testCanPatternMatch_failureCase_ListOfA2Char_ListOfA2A() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "[a] -> " + Prelude_Char),
            leccCALServices.getTypeFromString(testModule, "[a] -> a"),
            getTestModuleTypeInfo()));        
    }

    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match a -> b with a -> a.
     * 
     * Regression test for bug identified in changelist 157605.
     */
    public void testCanPatternMatch_failureCase_A2B_A2A() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "a -> b"),
            leccCALServices.getTypeFromString(testModule, "a -> a"),
            getTestModuleTypeInfo()));        
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatch() for a case where it should return false,
     * for failing to pattern match (a,b) -> b with (c,d) -> c.
     * 
     * Regression test for bug identified in changelist 62801.
     */
    public void testCanPatternMatch_failureCase_TupleAB2B_TupleCD2C() {
        assertTrue(!TypeExpr.canPatternMatch(
            leccCALServices.getTypeFromString(testModule, "(a,b) -> b"),
            leccCALServices.getTypeFromString(testModule, "(c,d) -> c"),
            getTestModuleTypeInfo()));                
    }
    
    /**
     * Tests to make sure that a base record variable must have the proper lacks constraints.
     * 
     * Regression test for bug identified in changelist 207861.
     */
    public void testGetTypeFromString_failureCase_PolymorphicRecordMissingLacksConstraint() {
        assertNull(leccCALServices.getTypeFromString(testModule, "{r | field1 :: Double}"));
    }
    
    /**
     * Tests the package-scope method TypeExpr.patternMatch() for a case where it should throw
     * a TypeException, for failing to pattern match [b] with a, where a is a non-generic variable.
     * 
     * Regression test for bug identified in changelist 87040.
     * 
     * This test case arises in real life for certain situations involving local type declarations,
     * for example, in:
     * 
     * test x =
     *    let
     *       y :: [a];
     *       y = x;
     *    in
     *       y;
     */
    public void testPatternMatch_failureCase_nonGenericVar() {
        TypeVar typeVarA = new TypeVar();
        
        boolean caughtException = false;
        try {
            TypeExpr.patternMatch(
                leccCALServices.getTypeFromString(testModule, "[b]"),
                typeVarA,
                NonGenericVars.extend(null, typeVarA),
                getTestModuleTypeInfo());
        } catch (TypeException e) {
            caughtException = true;
        }
        
        assertTrue(caughtException);
    }
    
    /**
     * Tests the package-scope method TypeExpr.patternMatch() for a case where it should throw
     * a TypeException, for failing to pattern match a base record variable with a non-generic
     * variable.
     * 
     * Regression test for bug identified in changelist 248484.
     * 
     * This test case arises in real life for certain situations involving local type declarations,
     * for example, in:
     * 
     * gb s =    
     * let
     *    y :: (r\#1, r\#2) => {r | #1 :: Prelude.Double, #2 :: Prelude.Char};                
     *    y = {s | #1 = 10.0, #2 = 'y'};
     * in 
     *    y;  
     */
    public void testPatternMatch_failureCase_nonGenericVar_Record() {
        HashSet<FieldName> lacksSet = new HashSet<FieldName>();
        lacksSet.add(FieldName.makeOrdinalField(1));
        lacksSet.add(FieldName.makeOrdinalField(2));
        RecordVar lacks12 = RecordVar.makeRecordVar(null, lacksSet, new TreeSet<TypeClass>(), false);
        RecordVar recordVar = RecordVar.makePolymorphicRecordVar(null);
        recordVar.setInstance(new RecordType(lacks12, Collections.<FieldName, TypeExpr>emptyMap()));
        TypeVar typeVar = new TypeVar();
        RecordType recordType = new RecordType(recordVar, Collections.<FieldName, TypeExpr>emptyMap());
        typeVar.setInstance(recordType);
        
        RecordVar inferredRecordVar = RecordVar.makePolymorphicRecordVar(null);
        inferredRecordVar.setInstance(recordType);
        HashMap<FieldName, TypeExpr> fieldsMap = new HashMap<FieldName, TypeExpr>();
        fieldsMap.put(FieldName.makeOrdinalField(1), leccCALServices.getTypeFromString(testModule, Prelude_Double));
        fieldsMap.put(FieldName.makeOrdinalField(2), leccCALServices.getTypeFromString(testModule, Prelude_Char));
        RecordType inferredType = new RecordType(inferredRecordVar, fieldsMap);
        
        boolean caughtException = false;
        try {
            TypeExpr.patternMatch(
                leccCALServices.getTypeFromString(testModule, "(a\\#1, a\\#2) => {a | #1 :: " + Prelude_Double + ", #2 :: " + Prelude_Char + "}"),
                inferredType,
                NonGenericVars.extend(null, typeVar),
                getTestModuleTypeInfo());
        } catch (TypeException e) {
            caughtException = true;
        }
        
        assertTrue(caughtException);
    }

    /**
     * Tests the method TypeExpr.canPatternMatchPieces() for a case where it should return false,
     * for failing to pattern match Int->Double with Num a => a->a.
     */
    public void testCanPatternMatchPieces_failureCase_Int2Double_NumA2A() {
        assertTrue(!TypeExpr.canPatternMatchPieces(
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Int),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double)
            },
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> a")
            },
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatchPieces() for a case where it should return true,
     * for pattern matching two arrays of types that can be pattern-matched componentwise.
     */
    public void testCanPatternMatchPieces_successCase() {
        assertTrue(TypeExpr.canPatternMatchPieces(
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Int),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                leccCALServices.getTypeFromString(testModule, "c -> c"),
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a")
            },
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> b"),
                leccCALServices.getTypeFromString(testModule, "{#3 :: a} -> b"),
                leccCALServices.getTypeFromString(testModule, "r\\#3 => {r | #3 :: a} -> b"),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: a} -> {r | foo :: b}"),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                leccCALServices.getTypeFromString(testModule, "a -> b"),
                leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a")
            },
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatchPieces() for a case where it should return true,
     * for a case where there is a null value in the first argument array.
     */
    public void testCanPatternMatchPieces_successCase_nullValueInFirstArgumentArray() {
        // (null values are accepted in the first argument array)
        assertTrue(TypeExpr.canPatternMatchPieces(
            new TypeExpr[] { null },
            new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canPatternMatchPieces() for a case where it should return true,
     * for a case where the second argument array is longer than the first argument array.
     */
    public void testCanPatternMatchPieces_successCase_secondArgumentArrayLonger() {
        // (the second argument array can be longer than the first argument array)
        assertTrue(TypeExpr.canPatternMatchPieces(
            new TypeExpr[] { },
            new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
            getTestModuleTypeInfo()));        
    }

    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return false,
     * for failing to unify Int with Double.
     */
    public void testCanUnifyType_failureCase_Int_Double() {
        assertTrue(!TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, Prelude_Int),
            leccCALServices.getTypeFromString(testModule, Prelude_Double),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return false,
     * for failing to unify Num a => a->a with Int->Double.
     */
    public void testCanUnifyType_failureCase_NumA2A_Int2Double() {
        assertTrue(!TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> a"),
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying Num a => a with Int.
     */
    public void testCanUnifyType_successCase_NumA_Int() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
            leccCALServices.getTypeFromString(testModule, Prelude_Int),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying (Num a, Num b) => a->b with Int->Double.
     */
    public void testCanUnifyType_successCase_NumA2NumB_Int2Double() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying (Num a, Num b) => a->b with Int->Int.
     */
    public void testCanUnifyType_successCase_NumA2NumB_Int2Int() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying Num a => a->b with Int->Double.
     */
    public void testCanUnifyType_successCase_NumA2B_Int2Double() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> b"),
            leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying {#3 :: a}->b with {#3 :: Int}->Double .
     */
    public void testCanUnifyType_successCase_RecordWithParametricField_RecordWithIntField() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, "{#3 :: a} -> b"),
            leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying r\#3 => {r | #3 :: a}->b with (r\#3, r\foo) => {r | #3 :: Int}->Double.
     */
    public void testCanUnifyType_successCase_ParametricRecordWithParametricField_ParametricRecordWithIntField() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, "r\\#3 => {r | #3 :: a} -> b"),
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying (r\#3, r\foo) => {r | #3 :: a}->{r | foo :: b} with
     * (r\#3, r\foo) => {r | #3 :: Int}->{r | foo :: Double}.
     */
    public void testCanUnifyType_successCase_SharedBaseRecordVar() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: a} -> {r | foo :: b}"),
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying (r\#3, r\foo) => {r | #3 :: Int}->{r | foo :: Double} with
     * itself.
     */
    public void testCanUnifyType_successCase_SharedBaseRecordVar2() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
            leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying a->b with c->c.
     */
    public void testCanUnifyType_successCase_A2B_C2C() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, "a -> b"),
            leccCALServices.getTypeFromString(testModule, "c -> c"),
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return true,
     * for unifying Eq a => a with Num a => a.
     */
    public void testCanUnifyType_successCase_Eq_Num() {
        assertTrue(TypeExpr.canUnifyType(
            leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a"),
            leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
            getTestModuleTypeInfo()));        
    }
    
    /**
     * Tests the method TypeExpr.canUnifyType() for a case where it should return false,
     * for failing to unify (a, Int) with (Char, a).
     * 
     * Regression test for bug identified in changelist 115495.
     */
    public void testCanUnifyType_failureCase_TupleAInt_TupleCharA() {
        TypeExpr typeVarA = TypeExpr.makeParametricType();

        assertTrue(!TypeExpr.canUnifyType(
            TypeExpr.makeFunType(typeVarA, leccCALServices.getTypeFromString(testModule, Prelude_Int)),
            TypeExpr.makeFunType(leccCALServices.getTypeFromString(testModule, Prelude_Char), typeVarA),
            getTestModuleTypeInfo()));
    }

    /**
     * Tests the method TypeExpr.canUnifyTypePieces() for a case where it should return false,
     * for failing to unify (a, Int) with (Char, a).
     */
    public void testCanUnifyTypePieces_failureCase_TupleAInt_TupleCharA() {
        TypeExpr typeVarA = TypeExpr.makeParametricType();

        assertTrue(!TypeExpr.canUnifyTypePieces(
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                TypeExpr.makeFunType(typeVarA, leccCALServices.getTypeFromString(testModule, Prelude_Int)),
            },
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Int),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                TypeExpr.makeFunType(leccCALServices.getTypeFromString(testModule, Prelude_Char), typeVarA),
            },
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyTypePieces() for a case where it should return true,
     * for unifying two arrays of types that are componentwise unifyable.
     */
    public void testCanUnifyTypePieces_successCase() {
        assertTrue(TypeExpr.canUnifyTypePieces(
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> b"),
                leccCALServices.getTypeFromString(testModule, "{#3 :: a} -> b"),
                leccCALServices.getTypeFromString(testModule, "r\\#3 => {r | #3 :: a} -> b"),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: a} -> {r | foo :: b}"),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                leccCALServices.getTypeFromString(testModule, "a -> b"),
                leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a")
            },
            new TypeExpr[] {
                leccCALServices.getTypeFromString(testModule, Prelude_Int),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
                leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                leccCALServices.getTypeFromString(testModule, "c -> c"),
                leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a")
            },
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyTypePieces() for a case where it should return true,
     * for a case where there is a null value in the first argument array.
     */
    public void testCanUnifyTypePieces_successCase_nullValueInFirstArgumentArray() {
        assertTrue(TypeExpr.canUnifyTypePieces(
            new TypeExpr[] { null },
            new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
            getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.canUnifyTypePieces() for a case where it should return true,
     * for a case where the second argument array is longer than the first argument array.
     */
    public void testCanUnifyTypePieces_successCase_secondArgumentArrayLonger() {
        assertTrue(TypeExpr.canUnifyTypePieces(
            new TypeExpr[] { },
            new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
            getTestModuleTypeInfo()));        
    }

    /** 
     * Helper function for the type closeness tests.
     * 
     * @param typeStr1 Type signature string
     * @param typeStr2 Type signature string
     * @return The type closeness of the two types
     */
    private static int typeCloseness(String typeStr1, String typeStr2) {
        return TypeExpr.getTypeCloseness(
                    leccCALServices.getTypeFromString(testModule, typeStr1),
                    leccCALServices.getTypeFromString(testModule, typeStr2),
                    getTestModuleTypeInfo());
    }
    
    /** 
     * Helper function for type closeness tests.  Asserts that type closeness of
     * typeStr1's type to typeStr2's type is assertedCloseness, and then asserts that
     * the type closeness of typeStr2's type to typeStr1's type is also assertedCloseness.
     * This provides a convenient way for us to verify the reflexivity of type closeness
     * at the same time as we verify other properties of the type closeness heuristic.
     * 
     * @param typeStr1 Type signature string
     * @param typeStr2 Type signature string
     * @param assertedCloseness Asserted value of typecloseness of typeStr1's type to 
     *                           typeStr2's type (and vice versa).
     */ 
    private static void assertTypeCloseness(String typeStr1, String typeStr2, int assertedCloseness) {
        assertEquals(assertedCloseness, typeCloseness(typeStr1, typeStr2));
        assertEquals(assertedCloseness, typeCloseness(typeStr2, typeStr1));
    }
    
    /**
     * Tests the method TypeExpr.getTypeCloseness() for a case where the types can
     * be unified.
     */
    public void testGetTypeCloseness_Unifyable_NumA_Int() {
        assertTrue(typeCloseness(Prelude_Num + " a => a", Prelude_Int) >= 0);
        assertTrue(typeCloseness(Prelude_Int, Prelude_Num + " a => a") >= 0);
    }
    
    /**
     * Tests the method TypeExpr.getTypeCloseness() for a case where the types can
     * not be unified.
     */
    public void testGetTypeCloseness_NotUnifyable_NumA_String() {
        assertTrue(typeCloseness(Prelude_Num + " a => a", Prelude_String) < 0);
        assertTrue(typeCloseness(Prelude_String, Prelude_Num + " a => a") < 0);
    }
    
    /**
     * Tests the method TypeExpr.getTypeCloseness() for a case where the types can
     * be unified because the first argument is an unconstrained type variable.
     */
    public void testGetTypeCloseness_Unifyable_A_Int() {
        assertTrue(typeCloseness("a", Prelude_Int) >= 0);
        assertTrue(typeCloseness(Prelude_Int, "a") >= 0);
    }
    
    /**
     * Tests the method TypeExpr.getTypeCloseness() for returning a measure of
     * type closeness. In this test, the pair of types [Num a => a, Int] is closer
     * to each other than the pair [a, Int], and getTypeCloseness() should therefore
     * return a higher value for the former pair than the value returned for the latter
     * pair.
     */
    public void testGetTypeCloseness_RelativeComparison() {
        assertTrue(typeCloseness(Prelude_Num + " a => a", Prelude_Int) >
                   typeCloseness("a", Prelude_Int));
        assertTrue(typeCloseness(Prelude_Int, Prelude_Num + " a => a") >
                   typeCloseness(Prelude_Int, "a"));
    }

    /** 
     * Verifies that the type closeness heuristic gives the specific numeric values 
     * that we expect.  
     *
     * The intuition behind the heuristic is that it represents how many "coincidences"
     * there are between two types.  So every time we encounter two things that are the
     * same without being totally uninstantiated type variables, we add a point.
     * 
     * The current heuristic gives 1 point for each time it matches:
     * 
     *   - a type constructor
     *   - a record type
     *   - a record field name that was present in both "original" types
     * 
     * to something other than an uninstantiated variable.
     * 
     * There are a couple of subtleties here:
     * 
     * 1. Matching a constrained type variable to a type that meets the contraint
     *    is just as good as matching the type itself.  So for example the type
     *    closeness of "Int" to "Num a => a" is 1, just like the closeness of 
     *    "Int" to "Int".
     * 2. Variables are instantiated as the type closeness check proceeds.  For
     *    example, the type closeness of "a -> a" to "Int -> Int" is 2:
     *      - 1 point for matching "->" type constructor
     *      - 0 points for matching first a to first Int, since a is uninstantiated
     *      - 1 point for matching second a to second Int, because by that point a 
     *        has been instantiated to Int.
     * 3. Field types (in records) are sometimes matched against intermediate types,
     *    but field names are only matched against original types.  For example,
     *    when calculating the closeness of "Eq a => a" to "{x :: Int, y :: Char}",
     *    the type closeness is 3:
     *      - First we replace "Eq a => a" with the intermediate type
     *        "(Eq b, Eq c) => {x :: b, y :: c}"
     *      - 1 point for matching record types
     *      - 1 point for matching type "Eq b => b" to Int
     *      - 1 point for matching type "Eq c => c" to Char
     *      - 0 points for matching field names (x and y), because the field names
     *        were not present in both original types  
     */
    public void testGetTypeCloseness_ExpectedValues() {

        // 0 points for matching Int to a, because a is uninstantiated
        assertTypeCloseness("Int", "a", 0);

        // 0 points for matching Num a => a to b, because b is uninstantiated
        assertTypeCloseness("Num a => a", "b", 0);
        
        // 1 point for matching Int to Int
        assertTypeCloseness("Int", "Int", 1);
        
        // 0 points for matching Num a => a to Enum a => a, because neither class is
        // a superclass of the other.
        assertTypeCloseness("Num a => a", "Enum a => a", 0);
        
        // 1 point for matching Int to a class that it is an instance of
        assertTypeCloseness("Num a => a", "Int", 1);
        assertTypeCloseness("Ord a => a", "Int", 1);
        
        // 1 point for matching Num a => a to Num b => b, because Num b => b is an 
        // instance of Num a no matter what Num type we pick for it.
        assertTypeCloseness("Num a => a", "Num b => b", 1);
        
        // 1 point for matching Int to any number of classes that it is an instance of
        assertTypeCloseness("(Enum a, Num a) => a", "Int", 1);
        
        // 1 point for matching one type class to another that it is a superclass of
        assertTypeCloseness("Num a => a", "Ord a => a", 1);
        
        // 0 points for matching record with uninstantiated variable
        assertTypeCloseness("{x :: Double, y :: String}", "a", 0);
        assertTypeCloseness("{x :: a, y :: b}", "c", 0);
        
        // 1 point for matching record type
        assertTypeCloseness("{x :: Double, y :: String}", "{a}", 1);
        assertTypeCloseness("{x :: a, y :: b}", "{c}", 1);
        
        // 1 point for matching record type, 1 for each field name
        assertTypeCloseness("{x :: Int, y :: String}", "{x :: c, y :: d}", 3);
        assertTypeCloseness("{x :: a, y :: b}", "{x :: c, y :: d}", 3);

        // 1 point for matching record type, 1 for each field name, 1 for each field type
        assertTypeCloseness("{x :: Double, y :: Double}", "(a\\x, a\\y) => {a | x :: Double, y :: Double}", 5);
        
        // 1 point for matching record type, 1 for each field name, 1 for matching x field type, 0 for matching y field type
        assertTypeCloseness("{x :: Double, y :: Double}", "(a\\x, a\\y) => {a | x :: Double, y :: a}", 4);
        
        // 1 point for matching record type, 1 for field name x, 1 for matching x field type
        assertTypeCloseness("{x :: Double, y :: Double}", "(a\\x) => {a | x :: Double}", 3);

        // 1 point for matching record type, 1 for field name x
        assertTypeCloseness("{x :: Double, y :: Double}", "(a\\x) => {a | x :: b}", 2);
        assertTypeCloseness("{x :: a, y :: b}", "(c\\x) => {c | x :: d}", 2);

        // 1 point for matching record type, 1 for field name x, 1 for matching x type (Num b => b with Double)
        assertTypeCloseness("{x :: Double, y :: Double}", "(a\\x, Num b) => {a | x :: b}", 3);
        
        // 1 point for matching record type
        assertTypeCloseness("{x :: Double, y :: Double}", "{a}", 1);
        
        // Right-hand record is expanded to have x and y fields with Eq types.
        // 1 point for matching record type, 1 point each for matching Int and String to Eq
        // 0 points for the field names; only field names that match before unification are given a point. 
        assertTypeCloseness("{x :: Int, y :: String}" , "Eq a => {a}", 3);     
        
        // a gets instantiated to (Eq b, Eq c) => {x :: b, y :: c}, at which point we give
        // 1 point for matching record type, 1 point each for matching Int and String
        // 0 points for the field names; only field names that are present in the original type (rather than the intermediate
        // constructed type) are given a point. 
        assertTypeCloseness("{x :: Int, y :: String}" , "Eq a => a", 3);     
        
        // Right-hand record is expanded to have x and y fields with Eq types.
        // 1 point for matching record type, 0 points for matching a and b (since they are uninistantiated and unconstrained)
        // 0 points for the field names; only field names that are present in the original type (rather than the intermediate
        // constructed type) are given a point. 
        assertTypeCloseness("{x :: a, y :: b}" , "Eq c => {c}", 1);     
        
        // c gets instantiated to (Eq b, Eq c) => {x :: b, y :: c}, at which point we give
        // 1 point for matching record type, 0 points for matching a and b (since they are uninistantiated and unconstrained)
        // 0 points for the field names; only field names that are present in the original type (rather than the intermediate
        // constructed type) are given a point. 
        assertTypeCloseness("{x :: a, y :: b}" , "Eq c => c", 1);     
        
        // b is instantiated to (Ord c) => {c}, at which point we give
        // 1 point for matching record type, one point for matching Ord c to Eq a (Ord is a subclass of Eq)
        assertTypeCloseness("Eq a => {a}", "Ord b => b", 2); 
        
        // b is instantiated to Typeable c => {c}, at which point we give
        // 1 point for matching record type, 0 points for matching Ord a with Typeable c (Ord and Typeable are unrelated)
        assertTypeCloseness("Ord a => {a}", "Typeable b => b", 1);

        // 1 point for matching record type, 0 points for matching Ord a with Typeable b (Ord and Typeable are unrelated)
        assertTypeCloseness("Ord a => {a}", "Typeable b => {b}", 1);
        
        // 1 point for matching function type
        assertTypeCloseness("a->b", "c->d", 1);

        // 1 point for matching function type
        // Note that the first a and b are not the same type variable as the second a and b, even though they use the same letters).
        assertTypeCloseness("a->b", "a->b", 1);
        
        // 1 point for matching function type, 1 point for matching second b (which has been instantiated to a)
        assertTypeCloseness("a->a", "b->b", 2);
        
        // a in second type is not the same a as in the first type.  Referring to second a as a':
        // 1 point for matching function type, 1 point for matching second a' (which has been instantiated to a)
        assertTypeCloseness("a->a", "a->a", 2);
        
        // 2 points for matching function types, 1 point for matching second and third b (which have been instantiated a)
        assertTypeCloseness("a->a->a", "b->b->b", 4);

        // This used to be 1 when tuples were a distinct type constructor from records.  We'd give 1
        // point for the Tuple3 type constructor matching.
        // Now that tuples are a special case of records, we also give 1 point each for matching
        // the field names (#1, #2, #3), so the expected value is 4.
        assertTypeCloseness("(a,b,c)", "(d,e,f)", 4);
        
        // 1 point for matching record type, 1 point for matching each field name (#1,#2,#3), and 1 point
        // for matching second e (which has been instantiated to b)
        assertTypeCloseness("(a,b,b)", "(d,e,e)", 5);
        
        // 1 point for matching record type, one point for each field name (#1,#2,#3), and 3 points for
        // matching second b (which has been instantiated to Char->String) to Char->String (1 point for
        // matching function type, 1 for Char, and 1 for String).
        assertTypeCloseness("(a,b,b)", "(Int,Char->String,Char->String)", 7);
        
        // 1 point for matching record type, one point for each field name (#1,#2,#3), 2 points for
        // matching function types on last 2 elements, 1 point for matching second b (which has been
        // instantiated to Char), 1 point for matching second c (which has been instantiated to c).
        assertTypeCloseness("(a,b->c,b->c)", "(Int,Char->String,Char->String)", 8);
        
        // 1 point for matching record type, 1 point for matching outer field names (#1,#2), 1 point
        // for matching record type of second element, 1 point for each of the second element's 
        // field names (#1,#2).
        assertTypeCloseness("(a,(b,c))", "(Int, (Char,Char))", 6);
        
        // a gets expanded into (Eq b, Eq c) => (b,c) at which point we give
        // 1 point for matching record type, 1 point each for matching Int and Char to Eq
        // 0 points for the field names; only field names that match before unification are given a point. 
        assertTypeCloseness("(Int,Char)", "Eq a => a", 3);

        // a gets expanded into (Eq b, Eq c, d) => (b,c,d) at which point we give
        // 1 point for matching record type, 1 point each for matching Int, String, and Char to Eq
        // 0 points for the field names; only field names that match before unification are given a point. 
        assertTypeCloseness("(Int,String,Char)", "Eq a => a", 4);

        // 1 point for matching record types, 1 point each for matching Int and Char to Eq, 
        // 1 point each for field names (#1, #2) since they are both present in original type signature. 
        assertTypeCloseness("(Int,Char)", "(Eq a, Eq b) => (a,b)", 5);
        
        // 1 point for matching record types, 1 point each for matching Int and Char to Eq, 
        // 1 point each for field names (#1, #2, #3) since they are both present in original type signature. 
        assertTypeCloseness("(Int,String,Char)", "(Eq a, Eq b, Eq c) => (a,b,c)", 7);
        
        // a is uninstantiated, so no points
        assertTypeCloseness("Maybe Int", "a", 0);
        
        // 1 point for matching Maybe
        assertTypeCloseness("Maybe Int", "Maybe a", 1);
        
        // 1 point for matching Maybe, 1 point for matching Int
        assertTypeCloseness("Maybe Int", "Maybe Int", 2);
        
        // 1 point for matching Either
        assertTypeCloseness("Either Int Int", "Either a b", 1);
        
        // 1 point for matching Either, 1 point for matching second a (instantiated to Int)
        assertTypeCloseness("Either Int Int", "Either a a", 2);
        
        // 1 point for matching Either, 1 point each for matching Int to Num a => a
        assertTypeCloseness("Either Int Int", "Num a => Either a a", 3);
        
        // 1 point for matching Either, 1 point for matching Int to Num a => a, 1 point for matching Int to Int
        assertTypeCloseness("Either Int Int", "Num a => Either a Int", 3);
        
        // 1 point for matching Either, 1 point for matching Int to Int
        assertTypeCloseness("Either Int Int", "Either a Int", 2);
        
        // a gets expanded to (Eq b,c) => Either b c, at which point we give
        // 1 point for matching Either, 1 point for matching Eq b to Int, 1 point for matching Eq c to Char
        assertTypeCloseness("Either Int Char", "Eq a => a", 3);
    }

    /**
     * Test that we treat coincidences in name (ie, two signatures happen to use the variable 'a'
     * even though they don't mean the same a) differently from variables that are actually the
     * same (ie, two signatures use the variable 'a' because they really mean the variable 'a') for
     * the purposes of type closeness calculations.
     */
    public void testGetTypeCloseness_RelatedTypeVariables() {
        TypeExpr a = TypeExpr.makeParametricType();
        TypeExpr aToa = TypeExpr.makeFunType(a, a);
        TypeExpr bTob = aToa.copyTypeExpr();
        
        assertEquals(3, TypeExpr.getTypeCloseness(aToa, aToa, getTestModuleTypeInfo()));
        assertEquals(2, TypeExpr.getTypeCloseness(aToa, bTob, getTestModuleTypeInfo()));
    }
    
    /**
     * Tests the method TypeExpr.makeFunType().
     */
    public void testMakeFunType() {
        TypeExpr typeDomain = leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Outputable + " a) => " + Prelude_Maybe + " a -> a");
        TypeExpr typeCodomain = leccCALServices.getTypeFromString(testModule, "(" + Prelude_Eq + " a, " + Prelude_Outputable + " a) => a -> " + Prelude_Maybe + " a");
        TypeExpr typeFun = TypeExpr.makeFunType(typeDomain, typeCodomain);
        assertEquals(typeFun.toString(), "(" + Prelude_Num + " a, " + Prelude_Outputable + " a, " + Prelude_Eq + " b, " + Prelude_Outputable + " b) => (" + Prelude_Maybe + " a -> a) -> b -> " + Prelude_Maybe + " b");
    }

    /**
     * Tests the method PreludeTypeConstants.makeListType() for an unconstrained element type.
     */
    public void testMakeListType_UnconstrainedElementType() {
        
        TypeExpr typeInt = leccCALServices.getTypeFromString(testModule, Prelude_Int);
        TypeExpr typeListInt = leccCALServices.getPreludeTypeConstants().makeListType(typeInt);
        assertEquals(typeListInt.toString(), "[" + Prelude_Int + "]");
    }
     
    /**
     * Tests the method PreludeTypeConstants.makeListType() for a constrained element type.
     */
    public void testMakeListType_ConstrainedElementType() {
        
        TypeExpr typeComplexElement = leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Outputable + " a) => " + Prelude_Maybe + " a -> a");
        TypeExpr typeListOfComplexElements = leccCALServices.getPreludeTypeConstants().makeListType(typeComplexElement);
        assertEquals(typeListOfComplexElements.toString(), "(" + Prelude_Num + " a, " + Prelude_Outputable + " a) => [" + Prelude_Maybe + " a -> a]");
    }

    /**
     * Tests the method TypeExpr.makeNonParametricType().
     */
    public void testMakeNonParametricType() {
        
        TypeConstructor intEntity = leccCALServices.getTypeFromString(testModule, Prelude_Int).rootTypeConsApp().getRoot();
        TypeExpr typeInt = TypeExpr.makeNonParametricType(intEntity);
        assertEquals(typeInt.toString(), Prelude_Int);
    }

    /**
     * Tests the creation of a non-parametric algebraic type.
     */
    public void testNonParametricAlgebraicType() {
        
        TypeExpr typeMaybeInt = leccCALServices.getTypeFromString(testModule, Prelude_Maybe + " " + Prelude_Int);
        assertEquals(typeMaybeInt.toString(), Prelude_Maybe + " " + Prelude_Int);
    }

    /**
     * Tests the method TypeExpr.makeParametricType().
     */
    public void testMakeParametricType() {
        
        TypeExpr typeA = TypeExpr.makeParametricType();
        assertEquals(typeA.toString(), "a");
    }
    
    /**
     * Tests the creation of a parametric algebraic type.
     */
    public void testParametricAlgebraicType() {
        
        TypeExpr typeMaybeA = leccCALServices.getTypeFromString(testModule, Prelude_Maybe + " a");
        assertEquals(typeMaybeA.toString(), Prelude_Maybe + " a");
    }

    /**
     * Tests the method TypeExpr.makeTupleDataConsType().
     */
    public void testMakeTupleDataConsType() {
        
        TypeExpr typeTuple3 = TypeExpr.makeTupleDataConsType(3);
        assertEquals(typeTuple3.toString(), "a -> b -> c -> (a, b, c)");
    }

    /**
     * Tests the method TypeExpr.makeTupleType(int).
     */
    public void testMakeTupleType_int() {
        
        TypeExpr type3Tuple = TypeExpr.makeTupleType(3);
        assertEquals(type3Tuple.toString(), "(a, b, c)");
    }

    /**
     * Tests the method TypeExpr.makeTupleType(List).
     */
    public void testMakeTupleType_List() {
        
        TypeExpr type1 = leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => [a]");
        TypeExpr type2 = leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => [a]");
        TypeExpr type3 = leccCALServices.getTypeFromString(testModule, Prelude_Either + " (" + Prelude_Either + " a b) " + Prelude_Int);

        TypeExpr type3Tuple = TypeExpr.makeTupleType(Arrays.asList(new TypeExpr[] {type1, type2, type3}));
        assertEquals(type3Tuple.toString(), "(" + Prelude_Num + " a, " + Prelude_Num + " b) => ([a], [b], " + Prelude_Either + " (" + Prelude_Either + " c d) " + Prelude_Int + ")");
    }

    /**
     * Tests the method TypeExpr.makeFreeRecordType().
     */
    public void testMakeFreeRecordType() {
        
        TypeExpr typeFreeRecord = TypeExpr.makeFreeRecordType(new HashSet<FieldName>(Arrays.asList(new FieldName[] {
            FieldName.makeTextualField("a"), FieldName.makeOrdinalField(5), FieldName.makeTextualField("b"), FieldName.makeOrdinalField(3)})));
        
        assertEquals(typeFreeRecord.toString(), "(a\\#3, a\\#5, a\\a, a\\b) => {a | #3 :: b, #5 :: c, a :: d, b :: e}");
    }

    /**
     * Tests the method TypeExpr.makePolymorphicRecordType().
     */
    public void testMakePolymorphicRecordType() {
        
        Map<FieldName, TypeExpr> fieldNameToTypeMap = new HashMap<FieldName, TypeExpr>();
        fieldNameToTypeMap.put(FieldName.makeTextualField("a"), leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => [a]"));
        fieldNameToTypeMap.put(FieldName.makeOrdinalField(5), leccCALServices.getTypeFromString(testModule, Prelude_Either + " (" + Prelude_Either + " a b) " + Prelude_Int));
        fieldNameToTypeMap.put(FieldName.makeTextualField("b"), leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => [a]"));
        fieldNameToTypeMap.put(FieldName.makeOrdinalField(3), TypeExpr.makeParametricType());
        
        TypeExpr typePolymorphicRecord = TypeExpr.makePolymorphicRecordType(fieldNameToTypeMap);
        
        assertEquals(typePolymorphicRecord.toString(), "(a\\#3, a\\#5, a\\a, a\\b, " + Prelude_Num + " e, " + Prelude_Num + " f) => {a | #3 :: b, #5 :: " + Prelude_Either + " (" + Prelude_Either + " c d) " + Prelude_Int + ", a :: [e], b :: [f]}");
    }
    
    /**
     * Tests the method TypeExpr.makePolymorphicRecordType() when the argument is an empty map.
     */
    public void testMakePolymorphicRecordType_EmptyMapArgument() {
        assertEquals(TypeExpr.makePolymorphicRecordType(Collections.<FieldName, TypeExpr>emptyMap()).toString(), "{a}");
    }

    /**
     * Tests the method TypeExpr.makeNonPolymorphicRecordType().
     */
    public void testMakeNonPolymorphicRecordType() {
        
        Map<FieldName, TypeExpr> fieldNameToTypeMap = new HashMap<FieldName, TypeExpr>();
        fieldNameToTypeMap.put(FieldName.makeTextualField("a"), leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => [a]"));
        fieldNameToTypeMap.put(FieldName.makeOrdinalField(5), leccCALServices.getTypeFromString(testModule, Prelude_Either + " (" + Prelude_Either + " a b) " + Prelude_Int));
        fieldNameToTypeMap.put(FieldName.makeTextualField("b"), leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => [a]"));
        fieldNameToTypeMap.put(FieldName.makeOrdinalField(3), leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b -> a -> b"));

        TypeExpr typeNonPolymorphicRecord = TypeExpr.makeNonPolymorphicRecordType(fieldNameToTypeMap);
        
        assertEquals(typeNonPolymorphicRecord.toString(), "(" + Prelude_Num + " a, " + Prelude_Num + " b, " + Prelude_Num + " e, " + Prelude_Num + " f) => {#3 :: a -> b -> a -> b, #5 :: " + Prelude_Either + " (" + Prelude_Either + " c d) " + Prelude_Int + ", a :: [e], b :: [f]}");
    }
    
    /**
     * Tests the method TypeExpr.makeNonPolymorphicRecordType() when the argument is an empty map.
     */
    public void testMakeNonPolymorphicRecordType_EmptyMapArgument() {
        assertEquals(TypeExpr.makeNonPolymorphicRecordType(Collections.<FieldName, TypeExpr>emptyMap()).toString(), "{}");
    }

    /**
     * Tests the method TypeExpr.patternMatchPieces() for its handling of type variables
     * that appear in more than one element of one/both of the argument arrays. This test
     * case's intent is to verify that:
     * 
     * patternMatchPieces([Int->Double, a, (a, Double, Boolean)], [a->b, a, (a, b, c)])
     *   = [Int->Double, Int, (Int, Double, Boolean)]
     */
    public void testPatternMatchPieces_successCase_Int2Double_A_TupleADoubleBoolean__A2B_A_TupleABC() throws TypeException {
        TypeExpr typeVarA = TypeExpr.makeParametricType();
        TypeExpr typeVarB = TypeExpr.makeParametricType();
        TypeExpr typeVarC = TypeExpr.makeParametricType();
        
        TypeExpr typeBoolean = leccCALServices.getTypeFromString(testModule, Prelude_Boolean);
        TypeExpr typeInt = leccCALServices.getTypeFromString(testModule, Prelude_Int);
        TypeExpr typeDouble = leccCALServices.getTypeFromString(testModule, Prelude_Double);
        
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeInt, typeDouble),
                    typeInt,
                    TypeExpr.makeTupleType(Arrays.asList(new TypeExpr[] {typeInt, typeDouble, typeBoolean}))
                }).toString(),
            Arrays.asList(TypeExpr.patternMatchPieces(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeInt, typeDouble),
                    typeVarA,
                    TypeExpr.makeTupleType(Arrays.asList(new TypeExpr[] {typeVarA, typeDouble, typeBoolean}))
                },
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeVarA, typeVarB),
                    typeVarA,
                    TypeExpr.makeTupleType(Arrays.asList(new TypeExpr[] {typeVarA, typeVarB, typeVarC}))
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.patternMatchPieces() for its handling of type variables
     * that appear in more than one element of one/both of the argument arrays, and also
     * for its handling of the second argument array being longer than the first. This test
     * case's intent is to verify that:
     * 
     * patternMatchPieces([Int], [a, a->Char]) = [Int, Int->Char]
     */
    public void testPatternMatchPieces_successCase_Int__A_A2Char() throws TypeException {
        TypeExpr typeVarA = TypeExpr.makeParametricType();
                      
        TypeExpr typeChar = leccCALServices.getTypeFromString(testModule, Prelude_Char);
        TypeExpr typeInt = leccCALServices.getTypeFromString(testModule, Prelude_Int);
    
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    typeInt,
                    TypeExpr.makeFunType(typeInt, typeChar)
                }).toString(),
            Arrays.asList(TypeExpr.patternMatchPieces(
                new TypeExpr[] {
                    typeInt,
                },
                new TypeExpr[] {
                    typeVarA,
                    TypeExpr.makeFunType(typeVarA, typeChar)
                },
                getTestModuleTypeInfo())).toString());
    }

    /**
     * Tests the method TypeExpr.patternMatchPieces() for its handling of type variables
     * that appear in more than one element of one/both of the argument arrays, and also
     * for its handling of the second argument array being longer than the first. This test
     * case's intent is to verify that:
     * 
     * patternMatchPieces([c->c], [a->b, [a->b]]) = [d->d, [d->d]]
     */
    public void testPatternMatchPieces_successCase_C2C__A2B_ListOfA2B() throws TypeException {
        TypeExpr typeVarA = TypeExpr.makeParametricType();
        TypeExpr typeVarB = TypeExpr.makeParametricType();
        TypeExpr typeVarC = TypeExpr.makeParametricType();
        TypeExpr typeVarD = TypeExpr.makeParametricType();
                       
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeVarD, typeVarD),
                    leccCALServices.getPreludeTypeConstants().makeListType(TypeExpr.makeFunType(typeVarD, typeVarD))
                }).toString(),
            Arrays.asList(TypeExpr.patternMatchPieces(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeVarC, typeVarC),
                },
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeVarA, typeVarB),
                    leccCALServices.getPreludeTypeConstants().makeListType(TypeExpr.makeFunType(typeVarA, typeVarB))
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.patternMatchPieces() for its handling of type variables
     * that appear in more than one element of one/both of the argument arrays, and also
     * for its handling of first argument array containing null values. This test
     * case's intent is to verify that:
     * 
     * patternMatchPieces([null, Int], [a->Char, a]) = [Int->Char, Int]
     */
    public void testPatternMatchPieces_successCase_Null_Int__A2Char_A() throws TypeException {
        TypeExpr typeVarA = TypeExpr.makeParametricType();
        
        TypeExpr typeChar = leccCALServices.getTypeFromString(testModule, Prelude_Char);
        TypeExpr typeInt = leccCALServices.getTypeFromString(testModule, Prelude_Int);        
       
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeInt, typeChar),
                    typeInt
                }).toString(),
            Arrays.asList(TypeExpr.patternMatchPieces(
                new TypeExpr[] {
                    null,
                    typeInt
                },
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeVarA, typeChar),
                    typeVarA
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.patternMatchPieces() for pattern matching
     * two arrays of types that can be pattern-matched componentwise.
     */
    public void testPatternMatchPieces_successCase_Simple() throws TypeException {
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "c -> c"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a")
                }).toString(),
            Arrays.asList(TypeExpr.patternMatchPieces(
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "c -> c"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a")
                },
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                    leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                    leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> b"),
                    leccCALServices.getTypeFromString(testModule, "{#3 :: a} -> b"),
                    leccCALServices.getTypeFromString(testModule, "r\\#3 => {r | #3 :: a} -> b"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: a} -> {r | foo :: b}"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "a -> b"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a")
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.patternMatchPieces() for its handling of first argument
     * array containing null values.
     */
    public void testPatternMatchPieces_nullValueInFirstArgumentArray() throws TypeException {
        assertEquals(
            Arrays.asList(
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") }).toString(),
            Arrays.asList(TypeExpr.patternMatchPieces(
                new TypeExpr[] { null },
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.patternMatchPieces() for its handling of the second
     * argument array being longer than the first.
     */
    public void testPatternMatchPieces_successCase_secondArgumentArrayLonger() throws TypeException {
        assertEquals(
            Arrays.asList(
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") }).toString(),
            Arrays.asList(TypeExpr.patternMatchPieces(
                new TypeExpr[] { },
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.patternMatchPieces() for throwing a TypeException on
     * failing to pattern-match Int->Double with Num a => a->a.
     */
    public void testPatternMatchPieces_failureCase_Int2Double_NumA2A() {
        boolean exceptionCaught = false;
        try {
            TypeExpr.patternMatchPieces(
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double)
                },
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                    leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> a")
                },
                getTestModuleTypeInfo());
        } catch (TypeException e) {
            exceptionCaught = true;
        }
        
        assertTrue(exceptionCaught);
    }
    
    /**
     * Tests the method TypeExpr.unifyTypePieces() for its handling of type variables
     * that appear in more than one element of one/both of the argument arrays. This test
     * case's intent is to verify that:
     * 
     * unifyTypePieces([a->b, a, (a, b, Boolean)], [Int->Double, a, (a, c, d)])
     *   = [Int->Double, Int, (Int, Double, Boolean)]
     */
    public void testUnifyTypePieces_successCase_A2B_A_TupleABBoolean__Int2Double_A_TupleACD() throws TypeException {
        TypeExpr typeVarA = TypeExpr.makeParametricType();
        TypeExpr typeVarB = TypeExpr.makeParametricType();
        TypeExpr typeVarC = TypeExpr.makeParametricType();
        TypeExpr typeVarD = TypeExpr.makeParametricType();
              
        TypeExpr typeBoolean = leccCALServices.getTypeFromString(testModule, Prelude_Boolean);       
        TypeExpr typeInt = leccCALServices.getTypeFromString(testModule, Prelude_Int);  
        TypeExpr typeDouble = leccCALServices.getTypeFromString(testModule, Prelude_Double);        
        
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeInt, typeDouble),
                    typeInt,
                    TypeExpr.makeTupleType(Arrays.asList(new TypeExpr[] {typeInt, typeDouble, typeBoolean}))
                }).toString(),
            Arrays.asList(TypeExpr.unifyTypePieces(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeVarA, typeVarB),
                    typeVarA,
                    TypeExpr.makeTupleType(Arrays.asList(new TypeExpr[] {typeVarA, typeVarB, typeBoolean}))
                },
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeInt, typeDouble),
                    typeVarA,
                    TypeExpr.makeTupleType(Arrays.asList(new TypeExpr[] {typeVarA, typeVarC, typeVarD}))
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.unifyTypePieces() for its handling of type variables that
     * are shared between the two arguments, and also for its handling of the second argument
     * array being longer than the first. This test case's intent is to verify that:
     * 
     * unifyTypePieces([a], [Int, a->Char]) = [Int, Int->Char]
     */
    public void testUnifyTypePieces_successCase_A__Int_A2Char() throws TypeException {    
        TypeExpr typeVarA = TypeExpr.makeParametricType();
                
        TypeExpr typeChar = leccCALServices.getTypeFromString(testModule, Prelude_Char);
        TypeExpr typeInt = leccCALServices.getTypeFromString(testModule, Prelude_Int);  
                                
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    typeInt,
                    TypeExpr.makeFunType(typeInt, typeChar)
                }).toString(),
            Arrays.asList(TypeExpr.unifyTypePieces(
                new TypeExpr[] {
                    typeVarA,
                },
                new TypeExpr[] {
                    typeInt,
                    TypeExpr.makeFunType(typeVarA, typeChar)
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.unifyTypePieces() for its handling of type variables that
     * are shared between the two arguments, and also for its handling of first argument array
     * containing null values. This test case's intent is to verify that:
     * 
     * unifyTypePieces([null, a], [a->Char, Int]) = [Int->Char, Int]
     */
    public void testUnifyTypePieces_successCase_Null_A__A2Char_Int() throws TypeException {
        TypeExpr typeVarA = TypeExpr.makeParametricType();
               
        TypeExpr typeChar = leccCALServices.getTypeFromString(testModule, Prelude_Char);
        TypeExpr typeInt = leccCALServices.getTypeFromString(testModule, Prelude_Int);          
        
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeInt, typeChar),
                    typeInt
                }).toString(),
            Arrays.asList(TypeExpr.unifyTypePieces(
                new TypeExpr[] {
                    null,
                    typeVarA
                },
                new TypeExpr[] {
                    TypeExpr.makeFunType(typeVarA, typeChar),
                    typeInt
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.unifyTypePieces() for unifying
     * two arrays of types that are componentwise unifyable.
     */
    public void testUnifyTypePieces_successCase_Simple() throws TypeException {
        assertEquals(
            Arrays.asList(
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "c -> c"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a")
                }).toString(),
            Arrays.asList(TypeExpr.unifyTypePieces(
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                    leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                    leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a -> b"),
                    leccCALServices.getTypeFromString(testModule, "{#3 :: a} -> b"),
                    leccCALServices.getTypeFromString(testModule, "r\\#3 => {r | #3 :: a} -> b"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: a} -> {r | foo :: b}"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "a -> b"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Eq + " a => a")
                },
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "{#3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> " + Prelude_Double),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "(r\\#3, r\\foo) => {r | #3 :: " + Prelude_Int + "} -> {r | foo :: " + Prelude_Double + "}"),
                    leccCALServices.getTypeFromString(testModule, "c -> c"),
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a")
                },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.unifyTypePieces() for its handling of first argument
     * array containing null values.
     */
    public void testUnifyTypePieces_successCase_nullValueInFirstArgumentArray() throws TypeException {
        assertEquals(
            Arrays.asList(
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") }).toString(),
            Arrays.asList(TypeExpr.unifyTypePieces(
                new TypeExpr[] { null },
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.unifyTypePieces() for its handling of the second
     * argument array being longer than the first.
     */
    public void testUnifyTypePieces_successCase_secondArgumentArrayLonger() throws TypeException {
        assertEquals(
            Arrays.asList(
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") }).toString(),
            Arrays.asList(TypeExpr.unifyTypePieces(
                new TypeExpr[] { },
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
                getTestModuleTypeInfo())).toString());
    }
    
    /**
     * Tests the method TypeExpr.unifyTypePieces() for its of 2 uninstantiated constrained type
     * variables, one appearing in the first argument, and the other in the second argument. In
     * particular, we check that Enum a => a and Num a => a unify to (Enum a, Num a) => a.
     * 
     * Regression test for bug identified in changelist 49949.
     */
    public void testUnifyTypePieces_successCase_EnumA_NumA() throws TypeException {
        assertEquals(
            Arrays.asList(
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, "(" + Prelude_Enum + " a, " + Prelude_Num + " a) => a") }).toString(),
            Arrays.asList(TypeExpr.unifyTypePieces(
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Enum + " a => a") },
                new TypeExpr[] { leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a") },
                getTestModuleTypeInfo())).toString());
    }

    /**
     * Tests the method TypeExpr.unifyTypePieces() for throwing a TypeException on
     * failing to unify (a, Int) with (Char, a).
     */
    public void testUnifyTypePieces_failureCase_TupleAInt_TupleCharA() {
        TypeExpr typeVarA = TypeExpr.makeParametricType();
        
        boolean exceptionCaught = false;
        try {
            TypeExpr.unifyTypePieces(
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Int),
                    leccCALServices.getTypeFromString(testModule, Prelude_Int + " -> " + Prelude_Double),
                    TypeExpr.makeFunType(typeVarA, leccCALServices.getTypeFromString(testModule, Prelude_Int)),
                },
                new TypeExpr[] {
                    leccCALServices.getTypeFromString(testModule, Prelude_Num + " a => a"),
                    leccCALServices.getTypeFromString(testModule, "(" + Prelude_Num + " a, " + Prelude_Num + " b) => a -> b"),
                    TypeExpr.makeFunType(leccCALServices.getTypeFromString(testModule, Prelude_Char), typeVarA),
                },
                getTestModuleTypeInfo());
        } catch (TypeException e) {
            exceptionCaught = true;
        }
        
        assertTrue(exceptionCaught);
    }

    /**
     * Tests the method TypeExpr.toString() for converting a TypeExpr into a string with
     * only fully-qualified names.
     */
    public void testToString_Qualified() {
        String typeString = "(a\\#3, a\\#5, a\\a, a\\b, a\\z, " + Prelude_Num + " e, " + Prelude_Num + " f, g\\name) => {a | #3 :: b, #5 :: " + Prelude_Either + " (" + Prelude_Either + " c d) " + Prelude_Int + ", a :: [(e -> f) -> d -> (c, d) -> [[(b, b)]]], b :: [f], z :: {g | name :: " + Prelude_String + "}}";
        
        assertEquals(typeString, leccCALServices.getTypeFromString(testModule, typeString).toString());
    }

    /**
     * Tests the method TypeExpr.toString() for converting a TypeExpr into a string with
     * only unqualified names.
     */
    public void testToString_Unqualified() {
        String typeString = "(a\\#3, a\\#5, a\\a, a\\b, a\\z, " + Prelude_Num + " e, " + Prelude_Num + " f, g\\name) => {a | #3 :: b, #5 :: " + Prelude_Either + " (" + Prelude_Either + " c d) " + Prelude_Int + ", a :: [(e -> f) -> d -> (c, d) -> [[(b, b)]]], b :: [f], z :: {g | name :: " + Prelude_String + "}}";
        String unqualifiedTypeString = "(a\\#3, a\\#5, a\\a, a\\b, a\\z, Num e, Num f, g\\name) => {a | #3 :: b, #5 :: Either (Either c d) Int, a :: [(e -> f) -> d -> (c, d) -> [[(b, b)]]], b :: [f], z :: {g | name :: String}}";
        
        assertEquals(unqualifiedTypeString, leccCALServices.getTypeFromString(testModule, typeString).toString(ScopedEntityNamingPolicy.UNQUALIFIED));
    }
    
    /**
     * Tests the method TypeExpr.toString() for converting a TypeExpr into a string with
     * names that are unqualified unless they are ambiguous. Here, we use M3 as
     * the target module, where the type definition of TestTypeShadowing shadows that by the same
     * name in the M1 module. This constitutes an ambiguity, and hence both
     * M3.TestTypeShadowing and M1.TestTypeShadowing will be rendered as fully qualified names, whereas
     * an unambiguous name like Prelude.Int will simply be rendered as Int.
     */
    public void testToString_UnqualifiedUnlessAmbiguous() {
        ModuleTypeInfo m3ModuleTypeInfo = leccCALServices.getWorkspaceManager().getWorkspace().getMetaModule(CALPlatformTestModuleNames.M3).getTypeInfo();

        String typeString = CALPlatformTestModuleNames.M3 + ".TestTypeShadowing -> " + CALPlatformTestModuleNames.M1 + ".TestTypeShadowing -> " + Prelude_Int;
        
        String m1 = m3ModuleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(CALPlatformTestModuleNames.M1).toSourceText();
        String m3 = m3ModuleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(CALPlatformTestModuleNames.M3).toSourceText();
        
        String unqualifiedUnlessAmbiguousTypeString = m3 + ".TestTypeShadowing -> " + m1 + ".TestTypeShadowing -> Int";
        
        assertEquals(unqualifiedUnlessAmbiguousTypeString, leccCALServices.getTypeChecker().getTypeFromString(typeString, CALPlatformTestModuleNames.M3, null).toString(new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(m3ModuleTypeInfo)));
    }

    /**
     * Tests the method TypeExpr.toSourceModel() for its TypeExpr-to-source-model conversion functionality.
     */
    public void testToSourceModel() {
        String typeString = "(a\\#3, a\\#5, a\\a, a\\b, a\\z, " + Prelude_Num + " e, " + Prelude_Num + " f, g\\name) => {a | #3 :: b, #5 :: " + Prelude_Either + " (" + Prelude_Either + " c d) " + Prelude_Int + ", a :: [(e -> f) -> d -> (c, d) -> [[(b, b)]]], b :: [f], z :: {g | name :: " + Prelude_String + "}}";
        
        assertEquals(
                typeString.replaceAll("[\n\r \t]*", ""), 
                leccCALServices.getTypeFromString(testModule, typeString).toSourceModel().toSourceText().replaceAll("[\n\r \t]*", ""));
    }
    
    /**
     * A regression test for TypeExpr.toSourceModel().
     */
    public void testToSourceModelNotFavoringPreferredNames() {
        final GemEntity entity = leccCALServices.getGemEntity(QualifiedName.make("Cal.Test.JUnitSupport.TypeExpr_Test_Support", "allPairs"));
        
        final String typeString = entity.getTypeExpr().toString(false, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        final SourceModel.TypeSignature typeSourceModel = entity.getTypeExpr().toSourceModel(false, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        final String typeStringFromSourceModel = typeSourceModel.toSourceText();
        
        assertEquals(typeString, typeStringFromSourceModel);
    }
    
    /**
     * A regression test for TypeExpr.toSourceModel().
     */
    public void testToSourceModelFavoringPreferredNames() {
        final GemEntity entity = leccCALServices.getGemEntity(QualifiedName.make("Cal.Test.JUnitSupport.TypeExpr_Test_Support", "allPairs"));
        
        final String typeString = entity.getTypeExpr().toString(true, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        final SourceModel.TypeSignature typeSourceModel = entity.getTypeExpr().toSourceModel(true, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        final String typeStringFromSourceModel = typeSourceModel.toSourceText();
        
        assertEquals(typeString, typeStringFromSourceModel);
    }
    
    /**
     * Serializes inTypeExpr into an in-memory buffer, and then deserializes from
     * the same buffer and returns the result.
     * @param inTypeExpr
     * @return TypeExpr
     * @throws IOException
     */
    private TypeExpr serializeDeserialize(TypeExpr inTypeExpr) throws IOException {
        NakedByteArrayOutputStream bos = new NakedByteArrayOutputStream(128);
        RecordOutputStream ros = new RecordOutputStream(bos);
        inTypeExpr.write(ros);
        ros.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.getByteArray());
        RecordInputStream ris = new RecordInputStream(bis);

        CompilerMessageLogger msgLogger = new MessageLogger();
        
        TypeExpr outTypeExpr = TypeExpr.load(ris, getTestModuleTypeInfo(), msgLogger);
        ris.close();
        
        assertTrue(msgLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
        return outTypeExpr;
    }
    
    /**
     * Converts typeExprString to a TypeExpr, serializes it out, serializes it back in,
     * and asserts that the result is equivalent (ie, has an identical String representation)
     * to the original. 
     * @param typeExprString
     * @throws IOException
     */
    private void checkRoundTripSerialization(String typeExprString)  throws IOException {
        TypeExpr typeExpr = leccCALServices.getTypeFromString(testModule, typeExprString);
        assertEquals(typeExpr.toString(), serializeDeserialize(typeExpr).toString());
    }
    
    /**
     * Check that we properly serialize and deserialize TypeExprs that contain multiple
     * instances of record vars with type-class constraints
     * @throws IOException
     */
    public void testSerialization_classConstrainedRecordVar() throws IOException {
        checkRoundTripSerialization(Prelude_Eq + " r => {r} -> {r}");
        checkRoundTripSerialization("(" + Prelude_Ord + " r, " + Prelude_Eq + " s) => ({r}, {r}, {s}, {s}) -> ({r}, {s})");
    }
    
    /**
     * Check that we properly serialize and deserialize TypeExprs that contain multiple
     * instances of record vars with lacks-field constraints
     * @throws IOException
     */
    public void testSerialization_lacksConstrainedRecordVar() throws IOException {
        checkRoundTripSerialization("r\\foo => {r | foo} -> {r}");
        checkRoundTripSerialization("(r\\#1, s\\#2, t\\#3) => {r | #1 :: a} -> {s | #2 :: b} -> {t | #3 :: b} -> (a, b, b)");
        checkRoundTripSerialization("(r\\foo, s\\bar) => " + Prelude_Maybe + " {r} -> " + Prelude_Maybe + " {s} -> ({r}, {s})");  
    }
    
    /**
     * Check that we serialize the special NO_FIELDS case properly
     */
    public void testSerializaton_noFields() throws IOException {
        checkRoundTripSerialization("{foo :: a, bar :: b} -> ({}, {foo :: a}, {bar :: b})");
        checkRoundTripSerialization(Prelude_Num + " a => {foo :: a, bar :: b} -> ({}, {foo :: a}, {bar :: b})");
        checkRoundTripSerialization("{foo :: " + Prelude_Maybe + " a, bar :: " + Prelude_Maybe + " b} -> ({}, {foo :: " + Prelude_Maybe + " a}, {bar :: " + Prelude_Maybe + " b}, {foo :: " + Prelude_Maybe + " a, bar :: " + Prelude_Maybe + " b})");
    }
    
    /**
     * Check that we serialize TypeConstructors properly 
     */
    public void testSerialization_typeConstructor() throws IOException {
        checkRoundTripSerialization(Prelude_Maybe + " " + Prelude_String);
        checkRoundTripSerialization(Prelude_Either + " " + Prelude_String + " " + Prelude_String);
        checkRoundTripSerialization(Prelude_Either + " " + Prelude_String + " " + Prelude_Char);
        checkRoundTripSerialization(Prelude_Either + " a a");
        checkRoundTripSerialization(Prelude_Either + " a b");
        checkRoundTripSerialization(Prelude_Either + " (" + Prelude_Either + " a a) (" + Prelude_Either + " a b)");

        checkRoundTripSerialization(Prelude_Num + " a => " + Prelude_Either + " (" + Prelude_Either + " a a) (" + Prelude_Either + " a b)");
        checkRoundTripSerialization("(" + Prelude_Num + " a, " + Prelude_Num + " b) => " + Prelude_Either + " (" + Prelude_Either + " a a) (" + Prelude_Either + " a b)");
        checkRoundTripSerialization("(" + Prelude_Num + " a, " + Prelude_Inputable + " a, " + Prelude_Outputable + " a) => " + Prelude_Either + " (" + Prelude_Either + " a a) (" + Prelude_Either + " a b)");
    }
}
