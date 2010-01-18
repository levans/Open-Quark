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
 * LanguageInfo_Test.java
 * Creation date: Mar 10, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.util.Set;

import junit.framework.TestCase;

/**
 * A set of JUnit test cases that tests the behaviour of the LanguageInfo class, in particular
 * its ability to check whether a string belongs to a particular class (e.g. a keyword, a
 * valid function name).
 *
 * @author Joseph Wong
 */
public class LanguageInfo_Test extends TestCase {

    /**
     * Constructor for LanguageInfo_Test.
     * 
     * @param name
     *            the name of the test
     */
    public LanguageInfo_Test(String name) {
        super(name);
    }
    
    
    /**
     * Tests whether the keywords held by LanguageInfo are consistent with the set defined by CALLexer.
     * We don't use CALLexer directly in LanguageInfo to avoid loading the ANTRL jar when using
     * standalone CAL jars.    
     */
    public void testKeywordConsistency() {
                      
        final Set<String> languageInfoKeywords = LanguageInfo.getCALKeywords();
            
        assertTrue (languageInfoKeywords.size() == CALLexer.getNKeywords());
        
        for (final String keyword : languageInfoKeywords) {
            assertTrue(CALLexer.isKeyword(keyword));            
        }        
    }

    /**
     * Tests for the method LanguageInfo.isKeyword() where the argument is indeed a CAL keyword.
     */
    public void testIsKeyword_trueCases() {
        assertTrue(LanguageInfo.isKeyword("case"));
        assertTrue(LanguageInfo.isKeyword("instance"));
        assertTrue(LanguageInfo.isKeyword("class"));
        assertTrue(LanguageInfo.isKeyword("unsafe"));
        assertTrue(LanguageInfo.isKeyword("let"));
        assertTrue(LanguageInfo.isKeyword("dataConstructor"));
        assertTrue(LanguageInfo.isKeyword("typeConstructor"));
        assertTrue(LanguageInfo.isKeyword("import"));
        assertTrue(LanguageInfo.isKeyword("private"));
        assertTrue(LanguageInfo.isKeyword("where"));
        assertTrue(LanguageInfo.isKeyword("data"));
        assertTrue(LanguageInfo.isKeyword("in"));
        assertTrue(LanguageInfo.isKeyword("primitive"));
        assertTrue(LanguageInfo.isKeyword("function"));
        assertTrue(LanguageInfo.isKeyword("of"));
        assertTrue(LanguageInfo.isKeyword("jvm"));
        assertTrue(LanguageInfo.isKeyword("if"));
        assertTrue(LanguageInfo.isKeyword("using"));
        assertTrue(LanguageInfo.isKeyword("public"));
        assertTrue(LanguageInfo.isKeyword("module"));
        assertTrue(LanguageInfo.isKeyword("else"));
        assertTrue(LanguageInfo.isKeyword("typeClass"));
        assertTrue(LanguageInfo.isKeyword("foreign"));
        assertTrue(LanguageInfo.isKeyword("then"));
        assertTrue(LanguageInfo.isKeyword("default"));
    }

    /**
     * Tests for the method LanguageInfo.isKeyword() where the argument is not a CAL keyword.
     */
    public void testIsKeyword_falseCases() {
        assertTrue(!LanguageInfo.isKeyword("foo"));
        assertTrue(!LanguageInfo.isKeyword("bar"));
        assertTrue(!LanguageInfo.isKeyword("foo.bar"));
        assertTrue(!LanguageInfo.isKeyword("CamelCase"));
        assertTrue(!LanguageInfo.isKeyword("UPPERCASE"));
        assertTrue(!LanguageInfo.isKeyword("_"));
        assertTrue(!LanguageInfo.isKeyword("6"));
        assertTrue(!LanguageInfo.isKeyword("\n"));
        assertTrue(!LanguageInfo.isKeyword(" "));
        assertTrue(!LanguageInfo.isKeyword(""));
        assertTrue(!LanguageInfo.isKeyword("\u3721"));
        assertTrue(!LanguageInfo.isKeyword("~"));
        assertTrue(!LanguageInfo.isKeyword("+"));
        assertTrue(!LanguageInfo.isKeyword("@"));
        assertTrue(!LanguageInfo.isKeyword("//"));
        assertTrue(!LanguageInfo.isKeyword("."));
    }
    
    /**
     * Tests for the method LanguageInfo.isValidModuleNameComponent() where the argument is a valid module name.
     */
    public void testIsValidModuleNameComponent_trueCases() {
        assertTrue(LanguageInfo.isValidModuleNameComponent("Prelude"));
        assertTrue(LanguageInfo.isValidModuleNameComponent("CamelCase"));
        assertTrue(LanguageInfo.isValidModuleNameComponent("UPPERCASE"));
        assertTrue(LanguageInfo.isValidModuleNameComponent("M2"));
        assertTrue(LanguageInfo.isValidModuleNameComponent("M_"));
    }
    
    /**
     * Tests for the method LanguageInfo.isValidModuleNameComponent() where the argument is not a valid module name.
     */
    public void testIsValidModuleNameComponent_falseCases() {
        assertTrue(!LanguageInfo.isValidModuleNameComponent("if"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("foo"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("bar"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("Foo.bar"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("M@"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("M "));
        assertTrue(!LanguageInfo.isValidModuleNameComponent(""));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("_"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("6"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("\n"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent(" "));
        assertTrue(!LanguageInfo.isValidModuleNameComponent(""));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("\u3721"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("~"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("+"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("@"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("//"));
        assertTrue(!LanguageInfo.isValidModuleNameComponent("."));
    }
   
    /**
     * Tests for the method LanguageInfo.isValidFunctionName() where the argument is a valid function name.
     */
    public void testIsValidFunctionName_trueCases() {
        assertTrue(LanguageInfo.isValidFunctionName("foo"));
        assertTrue(LanguageInfo.isValidFunctionName("bar"));
        assertTrue(LanguageInfo.isValidFunctionName("bar2"));
        assertTrue(LanguageInfo.isValidFunctionName("bar_"));
    }

    /**
     * Tests for the method LanguageInfo.isValidFunctionName() where the argument is not a valid function name.
     */
    public void testIsValidFunctionName_falseCases() {
        assertTrue(!LanguageInfo.isValidFunctionName("if"));
        assertTrue(!LanguageInfo.isValidFunctionName("foo.bar"));
        assertTrue(!LanguageInfo.isValidFunctionName("m@"));
        assertTrue(!LanguageInfo.isValidFunctionName("m "));
        assertTrue(!LanguageInfo.isValidFunctionName(""));
        assertTrue(!LanguageInfo.isValidFunctionName("_"));
        assertTrue(!LanguageInfo.isValidFunctionName("6"));
        assertTrue(!LanguageInfo.isValidFunctionName("\n"));
        assertTrue(!LanguageInfo.isValidFunctionName(" "));
        assertTrue(!LanguageInfo.isValidFunctionName(""));
        assertTrue(!LanguageInfo.isValidFunctionName("\u3721"));
        assertTrue(!LanguageInfo.isValidFunctionName("~"));
        assertTrue(!LanguageInfo.isValidFunctionName("+"));
        assertTrue(!LanguageInfo.isValidFunctionName("@"));
        assertTrue(!LanguageInfo.isValidFunctionName("//"));
        assertTrue(!LanguageInfo.isValidFunctionName("."));
    }    

    /**
     * Tests for the method LanguageInfo.isValidTypeConstructorName() where the
     * argument is a valid type constructor name.
     */
    public void testIsValidTypeConstructorName_trueCases() {
        assertTrue(LanguageInfo.isValidTypeConstructorName("Int"));
        assertTrue(LanguageInfo.isValidTypeConstructorName("CamelCase"));
        assertTrue(LanguageInfo.isValidTypeConstructorName("UPPERCASE"));
        assertTrue(LanguageInfo.isValidTypeConstructorName("M2"));
        assertTrue(LanguageInfo.isValidTypeConstructorName("M_"));
    }
    
    /**
     * Tests for the method LanguageInfo.isValidTypeConstructorName() where the
     * argument is not a valid type constructor name.
     */
    public void testIsValidTypeConstructorName_falseCases() {
        assertTrue(!LanguageInfo.isValidTypeConstructorName("if"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("foo"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("bar"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("Foo.bar"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("M@"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("M "));
        assertTrue(!LanguageInfo.isValidTypeConstructorName(""));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("_"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("6"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("\n"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName(" "));
        assertTrue(!LanguageInfo.isValidTypeConstructorName(""));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("\u3721"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("~"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("+"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("@"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("//"));
        assertTrue(!LanguageInfo.isValidTypeConstructorName("."));
    }
    
    /**
     * Tests for the method LanguageInfo.isValidTypeVariableName() where the
     * argument is a valid type variable name.
     */
    public void testIsValidTypeVariableName_trueCases() {
        assertTrue(LanguageInfo.isValidTypeVariableName("foo"));
        assertTrue(LanguageInfo.isValidTypeVariableName("bar"));
        assertTrue(LanguageInfo.isValidTypeVariableName("bar2"));
        assertTrue(LanguageInfo.isValidTypeVariableName("bar_"));
    }

    /**
     * Tests for the method LanguageInfo.isValidTypeVariableName() where the
     * argument is not a valid type variable name.
     */
    public void testIsValidTypeVariableName_falseCases() {
        assertTrue(!LanguageInfo.isValidTypeVariableName("if"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("foo.bar"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("m@"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("m "));
        assertTrue(!LanguageInfo.isValidTypeVariableName(""));
        assertTrue(!LanguageInfo.isValidTypeVariableName("_"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("6"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("\n"));
        assertTrue(!LanguageInfo.isValidTypeVariableName(" "));
        assertTrue(!LanguageInfo.isValidTypeVariableName(""));
        assertTrue(!LanguageInfo.isValidTypeVariableName("\u3721"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("~"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("+"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("@"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("//"));
        assertTrue(!LanguageInfo.isValidTypeVariableName("."));
    }

    /**
     * Tests for the method LanguageInfo.isValidDataConstructorName() where the
     * argument is a valid data constructor name.
     */
    public void testIsValidDataConstructorName_trueCases() {
        assertTrue(LanguageInfo.isValidDataConstructorName("Int"));
        assertTrue(LanguageInfo.isValidDataConstructorName("CamelCase"));
        assertTrue(LanguageInfo.isValidDataConstructorName("UPPERCASE"));
        assertTrue(LanguageInfo.isValidDataConstructorName("M2"));
        assertTrue(LanguageInfo.isValidDataConstructorName("M_"));
    }

    /**
     * Tests for the method LanguageInfo.isValidDataConstructorName() where the
     * argument is not a valid data constructor name.
     */
    public void testIsValidDataConstructorName_falseCases() {
        assertTrue(!LanguageInfo.isValidDataConstructorName("if"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("foo"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("bar"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("Foo.bar"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("M@"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("M "));
        assertTrue(!LanguageInfo.isValidDataConstructorName(""));
        assertTrue(!LanguageInfo.isValidDataConstructorName("_"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("6"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("\n"));
        assertTrue(!LanguageInfo.isValidDataConstructorName(" "));
        assertTrue(!LanguageInfo.isValidDataConstructorName(""));
        assertTrue(!LanguageInfo.isValidDataConstructorName("\u3721"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("~"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("+"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("@"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("//"));
        assertTrue(!LanguageInfo.isValidDataConstructorName("."));
    }
    
    /**
     * Tests for the method LanguageInfo.isValidTypeClassName() where the
     * argument is a valid type class name.
     */
    public void testIsValidTypeClassName_trueCases() {
        assertTrue(LanguageInfo.isValidTypeClassName("Int"));
        assertTrue(LanguageInfo.isValidTypeClassName("CamelCase"));
        assertTrue(LanguageInfo.isValidTypeClassName("UPPERCASE"));
        assertTrue(LanguageInfo.isValidTypeClassName("M2"));
        assertTrue(LanguageInfo.isValidTypeClassName("M_"));
    }

    /**
     * Tests for the method LanguageInfo.isValidTypeClassName() where the
     * argument is not a valid type class name.
     */
    public void testIsValidTypeClassName_falseCases() {
        assertTrue(!LanguageInfo.isValidTypeClassName("if"));
        assertTrue(!LanguageInfo.isValidTypeClassName("foo"));
        assertTrue(!LanguageInfo.isValidTypeClassName("bar"));
        assertTrue(!LanguageInfo.isValidTypeClassName("Foo.bar"));
        assertTrue(!LanguageInfo.isValidTypeClassName("M@"));
        assertTrue(!LanguageInfo.isValidTypeClassName("M "));
        assertTrue(!LanguageInfo.isValidTypeClassName(""));
        assertTrue(!LanguageInfo.isValidTypeClassName("_"));
        assertTrue(!LanguageInfo.isValidTypeClassName("6"));
        assertTrue(!LanguageInfo.isValidTypeClassName("\n"));
        assertTrue(!LanguageInfo.isValidTypeClassName(" "));
        assertTrue(!LanguageInfo.isValidTypeClassName(""));
        assertTrue(!LanguageInfo.isValidTypeClassName("\u3721"));
        assertTrue(!LanguageInfo.isValidTypeClassName("~"));
        assertTrue(!LanguageInfo.isValidTypeClassName("+"));
        assertTrue(!LanguageInfo.isValidTypeClassName("@"));
        assertTrue(!LanguageInfo.isValidTypeClassName("//"));
        assertTrue(!LanguageInfo.isValidTypeClassName("."));
    }    

    /**
     * Tests for the method LanguageInfo.isValidClassMethodName() where the
     * argument is a valid class method name.
     */
    public void testIsValidClassMethodName_trueCases() {
        assertTrue(LanguageInfo.isValidClassMethodName("foo"));
        assertTrue(LanguageInfo.isValidClassMethodName("bar"));
        assertTrue(LanguageInfo.isValidClassMethodName("bar2"));
        assertTrue(LanguageInfo.isValidClassMethodName("bar_"));
    }

    /**
     * Tests for the method LanguageInfo.isValidClassMethodName() where the
     * argument is not a valid class method name.
     */
    public void testIsValidClassMethodName_falseCases() {
        assertTrue(!LanguageInfo.isValidClassMethodName("if"));
        assertTrue(!LanguageInfo.isValidClassMethodName("foo.bar"));
        assertTrue(!LanguageInfo.isValidClassMethodName("m@"));
        assertTrue(!LanguageInfo.isValidClassMethodName("m "));
        assertTrue(!LanguageInfo.isValidClassMethodName(""));
        assertTrue(!LanguageInfo.isValidClassMethodName("_"));
        assertTrue(!LanguageInfo.isValidClassMethodName("6"));
        assertTrue(!LanguageInfo.isValidClassMethodName("\n"));
        assertTrue(!LanguageInfo.isValidClassMethodName(" "));
        assertTrue(!LanguageInfo.isValidClassMethodName(""));
        assertTrue(!LanguageInfo.isValidClassMethodName("\u3721"));
        assertTrue(!LanguageInfo.isValidClassMethodName("~"));
        assertTrue(!LanguageInfo.isValidClassMethodName("+"));
        assertTrue(!LanguageInfo.isValidClassMethodName("@"));
        assertTrue(!LanguageInfo.isValidClassMethodName("//"));
        assertTrue(!LanguageInfo.isValidClassMethodName("."));
    }

    /**
     * Tests for the method LanguageInfo.isValidFieldName() where the
     * argument is a valid field name.
     */
    public void testIsValidFieldName_trueCases() {
        assertTrue(LanguageInfo.isValidFieldName("foo"));
        assertTrue(LanguageInfo.isValidFieldName("bar"));
        assertTrue(LanguageInfo.isValidFieldName("bar2"));
        assertTrue(LanguageInfo.isValidFieldName("bar_"));
        
        assertTrue(LanguageInfo.isValidFieldName("#1"));
        assertTrue(LanguageInfo.isValidFieldName("#75"));
        assertTrue(LanguageInfo.isValidFieldName("#" + Integer.MAX_VALUE));
    }

    /**
     * Tests for the method LanguageInfo.isValidFieldName() where the
     * argument is not a valid field name.
     */
    public void testIsValidFieldName_falseCases() {
        assertTrue(!LanguageInfo.isValidFieldName("if"));
        assertTrue(!LanguageInfo.isValidFieldName("foo.bar"));
        assertTrue(!LanguageInfo.isValidFieldName("m@"));
        assertTrue(!LanguageInfo.isValidFieldName("m "));
        assertTrue(!LanguageInfo.isValidFieldName(""));
        assertTrue(!LanguageInfo.isValidFieldName("_"));
        assertTrue(!LanguageInfo.isValidFieldName("6"));
        assertTrue(!LanguageInfo.isValidFieldName("\n"));
        assertTrue(!LanguageInfo.isValidFieldName(" "));
        assertTrue(!LanguageInfo.isValidFieldName(""));
        assertTrue(!LanguageInfo.isValidFieldName("\u3721"));
        assertTrue(!LanguageInfo.isValidFieldName("~"));
        assertTrue(!LanguageInfo.isValidFieldName("+"));
        assertTrue(!LanguageInfo.isValidFieldName("@"));
        assertTrue(!LanguageInfo.isValidFieldName("//"));
        assertTrue(!LanguageInfo.isValidFieldName("."));

        assertTrue(!LanguageInfo.isValidFieldName("#"));
        assertTrue(!LanguageInfo.isValidFieldName("#a"));
        assertTrue(!LanguageInfo.isValidFieldName("#3 "));
        assertTrue(!LanguageInfo.isValidFieldName("#3.0"));
        assertTrue(!LanguageInfo.isValidFieldName("# "));
        assertTrue(!LanguageInfo.isValidFieldName("#123456789012345678901234567890"));
        assertTrue(!LanguageInfo.isValidFieldName("#-3"));
    }


}
