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
 * WildcardPatternMatcher.java
 * Creation date: Sep 15, 2005.
 * By: Edward Lam
 */
package org.openquark.util;

import java.util.regex.Pattern;


/**
 * This class allows strings to matched against simple dos-style wildcard patterns.
 * In these patterns, "*" represents zero or more allowable characters, and "?" represents exactly one such character.
 * 
 * @author Edward Lam
 */
public final class WildcardPatternMatcher {

    /**
     * Get the regex pattern corresponding to the provided pattern string.
     * @param patternString the pattern string.
     * @return the corresponding regex pattern, where "*" represents zero or more allowable characters, 
     * and "?" represents exactly one such character.  Null if the pattern string is zero length.
     */
    public static Pattern getPattern(String patternString) {
        String regExpr = wildcardPatternToRegExp(patternString);
        return (regExpr.length() == 0) ? null : Pattern.compile(regExpr);
    }
    
    /**
     * Convert the wildcard pattern into a regular expression.
     * @param patternString the wildcard pattern.
     * @return the corresponding regular expression.
     */
    public static String wildcardPatternToRegExp(String patternString) {
        // Ensure the pattern is simplified.
        patternString = simplifyPattern(patternString);

        // Convert a file name pattern (using * for multiple chars and ? for a single char) to a regular expression.
        String regExpr = patternString.trim();
        
        // Replace any '\' chars with '\\'.
        regExpr = regExpr.replaceAll("\\\\", "\\\\\\\\");
        
        // Replace any '.' chars with '\.'.
        regExpr = regExpr.replaceAll("\\.", "\\\\.");
        
        // Replace any '?' chars with '.'.
        regExpr = regExpr.replaceAll("\\?", ".");
        
        // Replace any '*' chars with '.*'.
        regExpr = regExpr.replaceAll("\\*", ".*");
        
        // Replace any [, ], {, }, (, ), ^, $, +, | chars with the \ prefixed version.
        regExpr = regExpr.replaceAll("\\[", "\\\\[");
        regExpr = regExpr.replaceAll("\\]", "\\\\]");
        regExpr = regExpr.replaceAll("\\{", "\\\\{");
        regExpr = regExpr.replaceAll("\\}", "\\\\}");
        regExpr = regExpr.replaceAll("\\(", "\\\\(");
        regExpr = regExpr.replaceAll("\\)", "\\\\)");
        regExpr = regExpr.replaceAll("\\^", "\\\\^");
        regExpr = regExpr.replaceAll("\\$", "\\\\$");
        regExpr = regExpr.replaceAll("\\+", "\\\\+");
        regExpr = regExpr.replaceAll("\\|", "\\\\|");
        
        return regExpr;
    }
    
    /**
     * Match a string against a pattern.
     * @param stringToMatch the string to match.
     * @param patternString the string form of the pattern against which to match the string.
     * @return whether the string matches the pattern.
     */
    public static boolean match(String stringToMatch, String patternString) {
        // Ensure the pattern is simplified.
        patternString = simplifyPattern(patternString);

        Pattern pattern = getPattern(patternString);
        if (pattern == null) {
            return stringToMatch.length() == 0;
        }
        return pattern.matcher(stringToMatch).matches();
    }
    
    /**
     * Simplify a pattern
     * Any mix of "?" and one or more "*" is converted to the same number of ?, followed by a single "*".
     * 
     * For example:     foo***?**??*?bar
     * is converted to: foo????*bar
     * 
     * @param pattern the pattern to simplify.
     * @return the simplified equivalent of the provided pattern.
     */
    private static String simplifyPattern(String pattern) {
        if (pattern.indexOf("*?") < 0 && pattern.indexOf("**") < 0) {
            return pattern;
        }
        
        int patternLen = pattern.length();
        char[] newPatternChars = new char[patternLen];
        
        // Whether we are in a run of wildcards beginning with a "*".
        boolean inStarWildCardRun = false;
        
        // If we are in a run of wildcards beginning with a "*", the number of "?" in the run.
        int starWildCardQuestionCount = 0;
        
        // The number of chars in the new pattern.
        int newPatternCharCount = 0;
        
        for (int i = 0; i < patternLen; i++) {
            char patternChar = pattern.charAt(i);
            
            if (patternChar == '*') {
                inStarWildCardRun = true;
                
            } else if (patternChar == '?') {
                if (inStarWildCardRun) {
                    starWildCardQuestionCount++;
                } else {
                    newPatternChars[newPatternCharCount] = patternChar;
                    newPatternCharCount++;
                }
                
            } else {
                // Not a wildcard.
                
                // If this is the first non-wildcard after a run of wildcards beginning with a "*", add the simplified run.
                if (inStarWildCardRun) {
                    // Add the correct number of "?".
                    for (int j = 0; j < starWildCardQuestionCount; j++) {
                        newPatternChars[newPatternCharCount] = '?';
                        newPatternCharCount++;
                    }
                    
                    // Add a single "*"
                    newPatternChars[newPatternCharCount] = '*';
                    newPatternCharCount++;
                    
                    // Reset the wildcard trackers.
                    inStarWildCardRun = false;
                    starWildCardQuestionCount = 0;
                }
                
                newPatternChars[newPatternCharCount] = patternChar;
                newPatternCharCount++;
            }
        }
        
        // If the pattern ends with a run of wildcards beginning with a "*", add the simplified run.
        if (inStarWildCardRun) {
            // Add the correct number of "?".
            for (int j = 0; j < starWildCardQuestionCount; j++) {
                newPatternChars[newPatternCharCount] = '?';
                newPatternCharCount++;
            }
            
            // Add a single "*"
            newPatternChars[newPatternCharCount] = '*';
            newPatternCharCount++;
        }
        
        return new String(newPatternChars, 0, newPatternCharCount);
    }
}
