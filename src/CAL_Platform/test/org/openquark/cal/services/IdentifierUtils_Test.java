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

package org.openquark.cal.services;

import junit.framework.TestCase;

/**
 * A set of JUnit test cases that tests the behaviour of the IndentifierUtils class, 
 * in particular its ability to make corrections and suggest proper identifiers.
 *
 * @author Joseph Wong
 */
public class IdentifierUtils_Test extends TestCase {
    
    /**
     * Constructor for LanguageInfo_Test.
     * 
     * @param name
     *            the name of the test
     */
    public IdentifierUtils_Test(String name) {
        super(name);
    }

    /**
     * Tests the method IdentifierUtils.makeValidModuleNameComponent() to make sure that it performs
     * the proper corrections on the argument string and returns a valid module name.
     */
    public void testMakeValidModuleName() {
        assertTrue(IdentifierUtils.makeValidModuleNameComponent("Int").isValid());
        assertTrue(IdentifierUtils.makeValidModuleNameComponent("CamelCase").isValid());
        assertTrue(IdentifierUtils.makeValidModuleNameComponent("UPPERCASE").isValid());
        assertTrue(IdentifierUtils.makeValidModuleNameComponent("M2").isValid());
        assertTrue(IdentifierUtils.makeValidModuleNameComponent("M_").isValid());
        
        assertEquals("Int", IdentifierUtils.makeValidModuleNameComponent("int").getSuggestion());
        assertEquals("IntPair", IdentifierUtils.makeValidModuleNameComponent("intPair").getSuggestion());
        assertEquals("M2", IdentifierUtils.makeValidModuleNameComponent("_M2").getSuggestion());
        assertEquals("M_2", IdentifierUtils.makeValidModuleNameComponent("M 2").getSuggestion());
        assertEquals("M_2_", IdentifierUtils.makeValidModuleNameComponent(" M 2 ").getSuggestion());
        assertEquals("CamelCase", IdentifierUtils.makeValidModuleNameComponent("camel,case").getSuggestion());

        assertNull(IdentifierUtils.makeValidModuleNameComponent("").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("_").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("6").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("\n").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent(" ").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("\u3721").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("~").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("+").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("@").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent("//").getSuggestion());
        assertNull(IdentifierUtils.makeValidModuleNameComponent(".").getSuggestion());
    }
    
    /**
     * Tests the method IdentifierUtils.makeValidFunctionName() to make sure that it performs
     * the proper corrections on the argument string and returns a valid function name.
     */
    public void testMakeValidFunctionName() {
        assertTrue(IdentifierUtils.makeValidFunctionName("foo").isValid());
        assertTrue(IdentifierUtils.makeValidFunctionName("bar").isValid());
        assertTrue(IdentifierUtils.makeValidFunctionName("bar2").isValid());
        assertTrue(IdentifierUtils.makeValidFunctionName("bar_").isValid());

        assertEquals("int", IdentifierUtils.makeValidFunctionName("Int").getSuggestion());
        assertEquals("intPair", IdentifierUtils.makeValidFunctionName("IntPair").getSuggestion());
        assertEquals("m2", IdentifierUtils.makeValidFunctionName("_m2").getSuggestion());
        assertEquals("m_2", IdentifierUtils.makeValidFunctionName("m 2").getSuggestion());
        assertEquals("m_2_", IdentifierUtils.makeValidFunctionName(" m 2 ").getSuggestion());
        assertEquals("camelCase", IdentifierUtils.makeValidFunctionName("Camel,case").getSuggestion());

        assertEquals("let0", IdentifierUtils.makeValidFunctionName("let").getSuggestion());
        assertEquals("class0", IdentifierUtils.makeValidFunctionName("class").getSuggestion());
        assertEquals("if0", IdentifierUtils.makeValidFunctionName("if").getSuggestion());
        
        assertNull(IdentifierUtils.makeValidFunctionName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("_").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("6").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("\n").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName(" ").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("\u3721").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("~").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("+").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("@").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName("//").getSuggestion());
        assertNull(IdentifierUtils.makeValidFunctionName(".").getSuggestion());
    }
    
    /**
     * Tests the method IdentifierUtils.makeValidTypeConstructorName() to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid type constructor name.
     */
    public void testMakeValidTypeConstructorName() {
        assertTrue(IdentifierUtils.makeValidTypeConstructorName("Int").isValid());
        assertTrue(IdentifierUtils.makeValidTypeConstructorName("CamelCase").isValid());
        assertTrue(IdentifierUtils.makeValidTypeConstructorName("UPPERCASE").isValid());
        assertTrue(IdentifierUtils.makeValidTypeConstructorName("M2").isValid());
        assertTrue(IdentifierUtils.makeValidTypeConstructorName("M_").isValid());
        
        assertEquals("Int", IdentifierUtils.makeValidTypeConstructorName("int").getSuggestion());
        assertEquals("IntPair", IdentifierUtils.makeValidTypeConstructorName("intPair").getSuggestion());
        assertEquals("M2", IdentifierUtils.makeValidTypeConstructorName("_M2").getSuggestion());
        assertEquals("M_2", IdentifierUtils.makeValidTypeConstructorName("M 2").getSuggestion());
        assertEquals("M_2_", IdentifierUtils.makeValidTypeConstructorName(" M 2 ").getSuggestion());
        assertEquals("CamelCase", IdentifierUtils.makeValidTypeConstructorName("camel,case").getSuggestion());

        assertNull(IdentifierUtils.makeValidTypeConstructorName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("_").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("6").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("\n").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName(" ").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("\u3721").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("~").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("+").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("@").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName("//").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeConstructorName(".").getSuggestion());
    }
    
    /**
     * Tests the method IdentifierUtils.makeValidTypeVariableName() to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid type variable name.
     */
    public void testMakeValidTypeVariableName() {
        assertTrue(IdentifierUtils.makeValidTypeVariableName("foo").isValid());
        assertTrue(IdentifierUtils.makeValidTypeVariableName("bar").isValid());
        assertTrue(IdentifierUtils.makeValidTypeVariableName("bar2").isValid());
        assertTrue(IdentifierUtils.makeValidTypeVariableName("bar_").isValid());

        assertEquals("int", IdentifierUtils.makeValidTypeVariableName("Int").getSuggestion());
        assertEquals("intPair", IdentifierUtils.makeValidTypeVariableName("IntPair").getSuggestion());
        assertEquals("m2", IdentifierUtils.makeValidTypeVariableName("_m2").getSuggestion());
        assertEquals("m_2", IdentifierUtils.makeValidTypeVariableName("m 2").getSuggestion());
        assertEquals("m_2_", IdentifierUtils.makeValidTypeVariableName(" m 2 ").getSuggestion());
        assertEquals("camelCase", IdentifierUtils.makeValidTypeVariableName("Camel,case").getSuggestion());

        assertEquals("let0", IdentifierUtils.makeValidTypeVariableName("let").getSuggestion());
        assertEquals("class0", IdentifierUtils.makeValidTypeVariableName("class").getSuggestion());
        assertEquals("if0", IdentifierUtils.makeValidTypeVariableName("if").getSuggestion());
        
        assertNull(IdentifierUtils.makeValidTypeVariableName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("_").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("6").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("\n").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName(" ").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("\u3721").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("~").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("+").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("@").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName("//").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeVariableName(".").getSuggestion());
    }
    
    /**
     * Tests the method IdentifierUtils.makeValidDataConstructorName() to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid data constructor name.
     */
    public void testMakeValidDataConstructorName() {
        assertTrue(IdentifierUtils.makeValidDataConstructorName("Int").isValid());
        assertTrue(IdentifierUtils.makeValidDataConstructorName("CamelCase").isValid());
        assertTrue(IdentifierUtils.makeValidDataConstructorName("UPPERCASE").isValid());
        assertTrue(IdentifierUtils.makeValidDataConstructorName("M2").isValid());
        assertTrue(IdentifierUtils.makeValidDataConstructorName("M_").isValid());
        
        assertEquals("Int", IdentifierUtils.makeValidDataConstructorName("int").getSuggestion());
        assertEquals("IntPair", IdentifierUtils.makeValidDataConstructorName("intPair").getSuggestion());
        assertEquals("M2", IdentifierUtils.makeValidDataConstructorName("_M2").getSuggestion());
        assertEquals("M_2", IdentifierUtils.makeValidDataConstructorName("M 2").getSuggestion());
        assertEquals("M_2_", IdentifierUtils.makeValidDataConstructorName(" M 2 ").getSuggestion());
        assertEquals("CamelCase", IdentifierUtils.makeValidDataConstructorName("camel,case").getSuggestion());

        assertNull(IdentifierUtils.makeValidDataConstructorName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("_").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("6").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("\n").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName(" ").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("\u3721").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("~").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("+").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("@").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName("//").getSuggestion());
        assertNull(IdentifierUtils.makeValidDataConstructorName(".").getSuggestion());
    }
    
    /**
     * Tests the method IdentifierUtils.makeValidTypeClassName() to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid type class name.
     */
    public void testMakeValidTypeClassName() {
        assertTrue(IdentifierUtils.makeValidTypeClassName("Int").isValid());
        assertTrue(IdentifierUtils.makeValidTypeClassName("CamelCase").isValid());
        assertTrue(IdentifierUtils.makeValidTypeClassName("UPPERCASE").isValid());
        assertTrue(IdentifierUtils.makeValidTypeClassName("M2").isValid());
        assertTrue(IdentifierUtils.makeValidTypeClassName("M_").isValid());
        
        assertEquals("Int", IdentifierUtils.makeValidTypeClassName("int").getSuggestion());
        assertEquals("IntPair", IdentifierUtils.makeValidTypeClassName("intPair").getSuggestion());
        assertEquals("M2", IdentifierUtils.makeValidTypeClassName("_M2").getSuggestion());
        assertEquals("M_2", IdentifierUtils.makeValidTypeClassName("M 2").getSuggestion());
        assertEquals("M_2_", IdentifierUtils.makeValidTypeClassName(" M 2 ").getSuggestion());
        assertEquals("CamelCase", IdentifierUtils.makeValidTypeClassName("camel,case").getSuggestion());

        assertNull(IdentifierUtils.makeValidTypeClassName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("_").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("6").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("\n").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName(" ").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("\u3721").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("~").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("+").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("@").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName("//").getSuggestion());
        assertNull(IdentifierUtils.makeValidTypeClassName(".").getSuggestion());
    }
    
    /**
     * Tests the method IdentifierUtils.makeValidClassMethodName() to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid class method name.
     */
    public void testMakeValidClassMethodName() {
        assertTrue(IdentifierUtils.makeValidClassMethodName("foo").isValid());
        assertTrue(IdentifierUtils.makeValidClassMethodName("bar").isValid());
        assertTrue(IdentifierUtils.makeValidClassMethodName("bar2").isValid());
        assertTrue(IdentifierUtils.makeValidClassMethodName("bar_").isValid());

        assertEquals("int", IdentifierUtils.makeValidClassMethodName("Int").getSuggestion());
        assertEquals("intPair", IdentifierUtils.makeValidClassMethodName("IntPair").getSuggestion());
        assertEquals("m2", IdentifierUtils.makeValidClassMethodName("_m2").getSuggestion());
        assertEquals("m_2", IdentifierUtils.makeValidClassMethodName("m 2").getSuggestion());
        assertEquals("m_2_", IdentifierUtils.makeValidClassMethodName(" m 2 ").getSuggestion());
        assertEquals("camelCase", IdentifierUtils.makeValidClassMethodName("Camel,case").getSuggestion());

        assertEquals("let0", IdentifierUtils.makeValidClassMethodName("let").getSuggestion());
        assertEquals("class0", IdentifierUtils.makeValidClassMethodName("class").getSuggestion());
        assertEquals("if0", IdentifierUtils.makeValidClassMethodName("if").getSuggestion());
        
        assertNull(IdentifierUtils.makeValidClassMethodName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("_").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("6").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("\n").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName(" ").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("\u3721").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("~").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("+").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("@").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName("//").getSuggestion());
        assertNull(IdentifierUtils.makeValidClassMethodName(".").getSuggestion());
    }
    
    /*
     * Class under test for String makeIdentifierName(String)
     */
    /**
     * Tests the method LanguageInfo.makeIdentifierName(String) to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid identifier name.
     */
    public void testMakeIdentifierName_String() {
        assertEquals("foo", IdentifierUtils.makeIdentifierName("foo"));
        assertEquals("bar", IdentifierUtils.makeIdentifierName("bar"));
        assertEquals("bar2", IdentifierUtils.makeIdentifierName("bar2"));
        assertEquals("bar_", IdentifierUtils.makeIdentifierName("bar_"));
        
        assertEquals("int", IdentifierUtils.makeIdentifierName("Int"));
        assertEquals("intPair", IdentifierUtils.makeIdentifierName("IntPair"));
        assertEquals("m2", IdentifierUtils.makeIdentifierName("_m2"));
        assertEquals("m_2", IdentifierUtils.makeIdentifierName("m 2"));
        assertEquals("m_2_", IdentifierUtils.makeIdentifierName(" m 2 "));
        assertEquals("camelCase", IdentifierUtils.makeIdentifierName("Camel,case"));

        assertEquals("let0", IdentifierUtils.makeIdentifierName("let"));
        assertEquals("class0", IdentifierUtils.makeIdentifierName("class"));
        assertEquals("if0", IdentifierUtils.makeIdentifierName("if"));
        
        assertNull(IdentifierUtils.makeIdentifierName(""));
        assertNull(IdentifierUtils.makeIdentifierName(""));
        assertNull(IdentifierUtils.makeIdentifierName("_"));
        assertNull(IdentifierUtils.makeIdentifierName("6"));
        assertNull(IdentifierUtils.makeIdentifierName("\n"));
        assertNull(IdentifierUtils.makeIdentifierName(" "));
        assertNull(IdentifierUtils.makeIdentifierName(""));
        assertNull(IdentifierUtils.makeIdentifierName("\u3721"));
        assertNull(IdentifierUtils.makeIdentifierName("~"));
        assertNull(IdentifierUtils.makeIdentifierName("+"));
        assertNull(IdentifierUtils.makeIdentifierName("@"));
        assertNull(IdentifierUtils.makeIdentifierName("//"));
        assertNull(IdentifierUtils.makeIdentifierName("."));
    }

    /*
     * Class under test for ValidatedIdentifier makeIdentifierName(String, boolean)
     */
    /**
     * Tests the method IdentifierUtils.makeIdentifierName(String, boolean) to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid identifier name with the first character in uppercase.
     */
    public void testMakeIdentifierName_StringBoolean_isConstructor() {
        assertTrue(IdentifierUtils.makeValidatedIdentifier("Int", true).isValid());
        assertTrue(IdentifierUtils.makeValidatedIdentifier("CamelCase", true).isValid());
        assertTrue(IdentifierUtils.makeValidatedIdentifier("UPPERCASE", true).isValid());
        assertTrue(IdentifierUtils.makeValidatedIdentifier("M2", true).isValid());
        assertTrue(IdentifierUtils.makeValidatedIdentifier("M_", true).isValid());
        
        assertEquals("Int", IdentifierUtils.makeValidatedIdentifier("int", true).getSuggestion());
        assertEquals("IntPair", IdentifierUtils.makeValidatedIdentifier("intPair", true).getSuggestion());
        assertEquals("M2", IdentifierUtils.makeValidatedIdentifier("_M2", true).getSuggestion());
        assertEquals("M_2", IdentifierUtils.makeValidatedIdentifier("M 2", true).getSuggestion());
        assertEquals("M_2_", IdentifierUtils.makeValidatedIdentifier(" M 2 ", true).getSuggestion());
        assertEquals("CamelCase", IdentifierUtils.makeValidatedIdentifier("camel,case", true).getSuggestion());

        assertNull(IdentifierUtils.makeValidatedIdentifier("", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("_", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("6", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("\n", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier(" ", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("\u3721", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("~", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("+", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("@", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("//", true).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier(".", true).getSuggestion());
    }

    /**
     * Tests the method IdentifierUtils.makeIdentifierName(String, boolean) to make sure
     * that it performs the proper corrections on the argument string and returns
     * a valid identifier name with the first character in lowercase.
     */
    public void testMakeIdentifierName_StringBoolean_notConstructor() {
        assertTrue(IdentifierUtils.makeValidatedIdentifier("foo", false).isValid());
        assertTrue(IdentifierUtils.makeValidatedIdentifier("bar", false).isValid());
        assertTrue(IdentifierUtils.makeValidatedIdentifier("bar2", false).isValid());
        assertTrue(IdentifierUtils.makeValidatedIdentifier("bar_", false).isValid());

        assertEquals("int", IdentifierUtils.makeValidatedIdentifier("Int", false).getSuggestion());
        assertEquals("intPair", IdentifierUtils.makeValidatedIdentifier("IntPair", false).getSuggestion());
        assertEquals("m2", IdentifierUtils.makeValidatedIdentifier("_m2", false).getSuggestion());
        assertEquals("m_2", IdentifierUtils.makeValidatedIdentifier("m 2", false).getSuggestion());
        assertEquals("m_2_", IdentifierUtils.makeValidatedIdentifier(" m 2 ", false).getSuggestion());
        assertEquals("camelCase", IdentifierUtils.makeValidatedIdentifier("Camel,case", false).getSuggestion());

        assertEquals("let0", IdentifierUtils.makeValidatedIdentifier("let", false).getSuggestion());
        assertEquals("class0", IdentifierUtils.makeValidatedIdentifier("class", false).getSuggestion());
        assertEquals("if0", IdentifierUtils.makeValidatedIdentifier("if", false).getSuggestion());
        
        assertNull(IdentifierUtils.makeValidatedIdentifier("", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("_", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("6", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("\n", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier(" ", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("\u3721", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("~", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("+", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("@", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier("//", false).getSuggestion());
        assertNull(IdentifierUtils.makeValidatedIdentifier(".", false).getSuggestion());
    }                
}
