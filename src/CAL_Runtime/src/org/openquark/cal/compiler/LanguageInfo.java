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
 * LanguageInfo.java
 * Creation date (Aug 21, 2002).
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * LanguageInfo is intended to contain helpful static methods describing the
 * CAL language in general, independent of a particular run of the compiler
 * on a particular project.
 * 
 * Creation date (Aug 21, 2002).
 * @author Bo Ilic
 */
public final class LanguageInfo {  
    
    /** 
     * Set<String> keywords in the CAL language. 
     * We duplicate this data from CALLexer so that standalone CAL jars do not need to
     * load ANTLR.
     */
    static final private Set<String> calKeywords = initializeCALKeywords();
    
    /** Should be non-instantiable */
    private LanguageInfo() {}
                         
    /**  
     * Returns true if the identifier is a lexically valid CAL module name.     
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL module name.
     * 
     * @see ModuleName#maybeMake(String)
     */
    public static boolean isValidModuleName(String identifier) {
        
        // if there are no dots in the identifier, then we can just validate it as a module name component
        if (identifier.indexOf('.') < 0) {
            return isValidModuleNameComponent(identifier);
            
        } else {
            // validate each component
            String[] components = identifier.split("\\.");
            for (int i = 0, n = components.length; i < n; i++) {
                if (!isValidModuleNameComponent(components[i])) {
                    return false;
                }
            }
            
            return true;
        }
    }
    
    /**
     * Returns an array of the components of the given module name after verifying that each one is
     * a valid module name component.
     * @param moduleName a module name.
     * @return an array of the components of the given module name.
     * @throws IllegalArgumentException if one or more of the components is not valid.
     */
    public static String[] getVerifiedModuleNameComponents(String moduleName) {
        
        if (moduleName == null) {
            throw new NullPointerException();
        }
        if (moduleName.length() == 0) {
            throw new IllegalArgumentException("The module name cannot be empty.");
        }
        
        String[] components = moduleName.split("\\.");
        final int nComponents = components.length;
    
        for (int i = 0; i < nComponents; i++) {
            if (!isValidModuleNameComponent(components[i])) {
                throw new IllegalArgumentException("'" + components[i] + "', occurring in the module name '" + moduleName + "' is not a valid module name component.");
            }
        }
    
        return components;
    }

    /**  
     * Returns true if the identifier is a lexically valid CAL module name component.     
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL module name component.
     */
    public static boolean isValidModuleNameComponent(String identifier) {
        return isValidCons(identifier);
    }    
    
    /**
     * Returns true if the identifier is a lexically valid CAL function name.     
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL function name.   
     */
    public static boolean isValidFunctionName(String identifier) {
        return isValidVar(identifier);
    }
          
    /**
     * Returns true if the identifier is a lexically valid CAL type constructor name.     
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL type constructor name.
     */
    public static boolean isValidTypeConstructorName(String identifier) {
        return isValidCons(identifier);
    }
        
    /**
     * Returns true if the identifier is a lexically valid CAL type variable name.     
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL type variable name.
     */
    public static boolean isValidTypeVariableName(String identifier) {
        return isValidVar(identifier);
    }
            
    /**
     * Returns true if the identifier is a lexically valid CAL data constructor name.    
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL data constructor name.
     */
    public static boolean isValidDataConstructorName (String identifier) {
        return isValidCons(identifier);
    }
                     
    /**
     * Returns true if the identifier is a lexically valid CAL type class name.   
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL type class name.
     */
    public static boolean isValidTypeClassName(String identifier) {
        return isValidCons(identifier);
    }
            
    /** 
     * Returns true if the identifier is a lexically valid CAL class method name.  
     * @param identifier
     * @return boolean true if the identifier is a lexically valid CAL class method name.
     */
    public static boolean isValidClassMethodName(String identifier) {
        return isValidVar(identifier);
    }
            
    /**  
     * Returns True if the identifier is a lexically valid CAL field name i.e. the field names uses in 
     * records types or record values. For example, "#2", "orderDate" etc. 
     * @param identifier
     * @return boolean 
     */
    public static boolean isValidFieldName(String identifier) {
        return FieldName.isValidCalSourceForm(identifier);
    }
                  
    /**
     * Returns true if the identifier is a valid CAL CONS lexer token.
     * This means that the identifier start with an ANSI upper case letter, and is
     * followed by any number of ANSI alphabetic characters, digits, or the underscore,
     * and is also not a built-in keyword.
     * @param identifier
     * @return boolean true if the identifier is a valid CAL CONS lexer token.
     */
    private static boolean isValidCons(final String identifier) {
                       
        if (identifier == null) {
            return false;
        }
              
        int len = identifier.length();
       
        // if there are no characters, it's not valid!
        if (len < 1) {
            return false;
        }

        // check that the first letter is an upper-case letter
        if (!isCALConsStart(identifier.charAt(0))) {
            return false;
        }
      
        // only letters, digits and underscores are allowed in the rest of the name
        for (int i = 1; i < len; i++) {
            
            if (!isCALConsPart(identifier.charAt(i))) {
                return false;
            }          
        }
        
        //we use the fact that all keywords in CAL start with a lowercase letter to skip this test.
        //return !isKeyword(identifier);
        return true;
    }
    
    /**
     * Returns true if the identifier is a valid CAL VAR lexer token.
     * This means that the identifier start with an ANSI lower case letter or an underscore, and is
     * followed by any number of ANSI alphabetic characters, digits, or the underscore,
     * and is also not a built-in keyword.
     * @param identifier
     * @return boolean true if the identifier is a valid CAL VAR lexer token.
     */    
    private static boolean isValidVar(final String identifier) {
                     
        //it would be nicer to delegate to the lexer, but it is a pain to
        //trap skipped lexical tokens such as whitespace and comments.
        
        if (identifier == null) {
            return false;
        }
              
        int len = identifier.length();
       
        // if there are no characters, it's not valid!
        if (len < 1) {
            return false;
        }

        // check that the first letter is a lower-case letter
        if (!isCALVarStart(identifier.charAt(0))) {
            return false;
        }
      
        // only letters, digits and underscores are allowed in the rest of the name
        for (int i = 1; i < len; i++) {
            
            if (!isCALVarPart(identifier.charAt(i))) {
                return false;
            }          
        }

        return !isKeyword(identifier);
    }
    
    /**
     * Method isCALConsStart.
     * @param c character to test.
     * @return boolean true if c is a valid first character in a CONS CAL lexer token.
     */
    public static boolean isCALConsStart(char c) {
        return c >= 'A' && c <= 'Z';
    }      
    
    /**
     * Method isCALConsPart.
     * @param c character to test.
     * @return boolean true if c is a valid character, beyond the first character, in a CONS CAL lexer token.
     */
    public static boolean isCALConsPart(char c) {
        return isCALVarPart(c);
    }    
       
    /**
     * Method isCALVarStart.
     * @param c character to test.
     * @return boolean true if c is a valid first character in a VAR CAL lexer token.
     */
    public static boolean isCALVarStart(char c) {
        return c >= 'a' && c <= 'z';
    }       
    
    /**
     * Method isCALVarPart.
     * @param c character to test.
     * @return boolean true if c is a valid character, beyond the first character, in a VAR CAL lexer token.
     */
    public static boolean isCALVarPart(char c) {
        return c >= 'a' && c <= 'z' ||
               c >= 'A' && c <= 'Z' ||
               c >= '0' && c <= '9' ||
               c == '_';  
    }
      
    /**
     * Returns whether the character is lexically considered a whitespace character by CAL.  
     * @param ch the character to check.
     * @return true if the character is lexically considered a whitespace character by CAL; false otherwise.
     */
    public static boolean isCALWhitespace(char ch) {
        switch (ch) {
        case ' ':
        case '\t':
        case '\f':
        case '\r':
        case '\n':
            return true;
        default:
            return false;
        }
    }
    
    /**
     * Tests if an identifier is a CAL keyword.
     * @param identifier
     * @return true if the identifier is a CAL keyword such as "data" or "if".
     */
    public static boolean isKeyword(final String identifier) {            
        return calKeywords.contains(identifier);
    }
    
    /**     
     * @return String Set of keywords in the CAL language. The set is unmodifiable.
     */
    public static Set<String> getCALKeywords() {
        return calKeywords;
    }

    /**     
     * Initializes the CAL keywords.
     * We duplicate this data from CALLexer so that standalone CAL jars do not need to
     * load ANTLR.
     * @return Set<String> keywords in CAL. An unmodifiable set.
     */
    private static Set<String> initializeCALKeywords() {
        Set<String> keywords = new HashSet<String>();
        keywords.add("public");
        keywords.add("case");
        keywords.add("dataConstructor");
        keywords.add("where");
        keywords.add("then");
        keywords.add("module");
        keywords.add("using");
        keywords.add("unsafe");
        keywords.add("protected");
        keywords.add("class");
        keywords.add("friend");
        keywords.add("jvm");
        keywords.add("function");
        keywords.add("foreign");
        keywords.add("of");
        keywords.add("if");
        keywords.add("typeConstructor");
        keywords.add("data");
        keywords.add("private");
        keywords.add("default");
        keywords.add("primitive");
        keywords.add("instance");
        keywords.add("deriving");
        keywords.add("typeClass");
        keywords.add("else");
        keywords.add("import");
        keywords.add("in");
        keywords.add("let");
        return Collections.unmodifiableSet(keywords);
    }    
}



