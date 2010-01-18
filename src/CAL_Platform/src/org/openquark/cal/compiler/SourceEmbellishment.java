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
 * SourceEmbellishment.java
 * Created: Jun 6, 2007
 * By: mbyne
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import antlr.Token;


/**
 * This class is used to represent source code embellishments
 * Currently these may include comments and whitespace.
 * 
 * Embellishments are not included in the SourceModel as they
 * are hard to associate with particular source elements. For
 * example comments and line separators are often used as section 
 * breaks.
 *
 * @author Magnus Byne
 */
public abstract class SourceEmbellishment {
    private final SourceRange range;
    protected final String text;

    private SourceEmbellishment(Token token, String text) {
        this.range = new SourceRange(new SourcePosition(
                                                        token.getLine(),
                                                        token.getColumn(),
                                                        token.getFilename()),
                                     token.getText());
        this.text = text;
    }

    /**
     * returns true if the whitespace string contains an empty line
     * @param ws
     * @return true if there is a blank line in the ws
     */
    private static boolean containsBlankLine(String ws) {
        int slashN = 0;
        int slashR = 0;

        for (int i = 0; i < ws.length(); i++) {
            if (ws.charAt(i) == '\n') {
                slashN++;
            }

            if (ws.charAt(i) == '\r') {
                slashR++;
            }

            if ((slashR >= 2) || (slashN >= 2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * get the sourcerange for the embellishment
     * @return the sourcerange
     */
    public final SourceRange getSourceRange() {
        return range;
    }

    /**
     * the text of the embellishment
     */
    public final String getText() {
        return text;
    }
    
    /**
     * get the embellishment text as a list of lines, that
     * do not exceed width where possible.
     * @param width
     * @return the text as a singleton list.
     */
    public List<String> getText(int width) {
        return Collections.singletonList(text);
    }

    /**
     * Breaks a string into a list of strings, each of which is not longer
     * than the specified width where possible (if individual words are longer than 
     * width than the width will be exceeded). 
     * @param text
     * @param width
     * @return list of strings
     */
    private static List<String> wrap(String text, int width) {
        
        if (text.length() == 0) {
            return Collections.singletonList("");
        }
        
        List<String> res = new ArrayList<String>();
        int start =0;
        
        while(start < text.length()) {

            if (text.length() - start <= width) {
                res.add(text.substring(start));
                start = text.length();
            } else {
                int i = text.lastIndexOf(' ', (start+width));

                if (i < start) {
                    res.add(text.substring(start));
                    start = text.length();
                } else {
                    res.add(text.substring(start, i));
                    start = i + 1;
                }
            }
        }
        return res;
    }
    
    /** removes spaces from the end of a string*/
    private static String trimRight(String str) {
        int end = str.length()-1;
        
        while (end >= 0 && str.charAt(end) == ' ') {
            end--;
        }
        
        return str.substring(0, end + 1);
    }
    

    @Override
    public String toString() {
        return range.toString() + ": " + text;
    }
    
    /**
     * Returns true if the token represents an embellishment
     * @param token
     * @return true iff the token is an embellishment
     */
    static public boolean isEmbellishmentToken(Token token) {
        switch (token.getType()) {
        case CALTokenTypes.SL_COMMENT:
        case CALTokenTypes.ML_COMMENT:
        case CALTokenTypes.WS:
            return true;
        }

        return false;
    }

    /**
     * Factory method to create a source embellishment from a token.
     * Returns null if no embellishment is needed to represent the token
     * @param token
     * @return embellishment or null
     */
    static public SourceEmbellishment make(Token token) {
        switch (token.getType()) {
        case CALTokenTypes.SL_COMMENT:
            return new SingleLineComment(token);

        case CALTokenTypes.ML_COMMENT:
            return new MultiLineComment(token);

        case CALTokenTypes.WS:
            if (containsBlankLine(token.getText())) {
                return new BlankLine(token);
            }

            break;
        }

        if (token.getType() == CALTokenTypes.SL_COMMENT) {
            return new SingleLineComment(token);
        }

        return null;
    }

    /** this class is used to represent a blank line in the source*/
    static final public class BlankLine extends SourceEmbellishment {
        private BlankLine(Token token) {
            super(token, "");
        }
    }

    /** this is used to represent a multi-line  comment, which may span multiple lines*/
    static final public class MultiLineComment extends SourceEmbellishment {
        private MultiLineComment(Token token) {
            super(token, token.getText());
        }
        
        @Override
        public List<String> getText(int width) {
            return format(text,width);
        }
        
        /**
         * Format a multi-line comment to the specified width
         * @param text
         * @param width
         * @return a list of comment lines
         */
        static List<String> format(String text, int width) {            
            //split the input text into an array of lines.
            String[] lines = text.split("(?m)\r\n|\n|\r");
            List<String> res = new ArrayList<String>();
            
            for(final String line : lines) {
                //remove spaces and trim comment delimiters
                final String trimmedLine = line.trim().replaceAll("(^/[*] ?)|([*]/$)|(^[*] ?)", "");
                res.addAll(wrap(trimmedLine, width - 3));    
            }
            
            //add formatted comment markers to the lines
            List<String> formatted = new ArrayList<String>();
            
            if (res.size() == 1) {
                formatted.add("/* " + trimRight(res.get(0)) + "*/");
            } else {
                formatted.add("/*");
                for(int i=0; i < res.size(); i++) {
                    String line = trimRight(res.get(i));
                    if ( (i != 0 && i != res.size()-1) || line.length() > 0) {
                        formatted.add(" * " + line);
                    }
                }
                formatted.add(" */");
            }
 
            return formatted;
        }

    }

    /** this is used to represent a single line comment. */
    static final public class SingleLineComment extends SourceEmbellishment {
        private SingleLineComment(Token token) {
            super(token, token.getText());
        }
        
        @Override
        public List<String> getText(int width) {
            return format(text,width);
        }

        /**
         * Format a single line comment to the specified width
         * @param text
         * @param width
         * @return a list of comment lines
         */
        static List<String> format(String text, int width) {
            
            final String prefix;
            
            //we want to ensure split comments start with a space if the first line does
            if (text.startsWith("// ")) {
                prefix = "// ";
            } else {
                prefix = "//";
            }
                
            List<String> wrapped = wrap(text.substring(prefix.length()), width - prefix.length());
            List<String> res = new ArrayList<String>();
            
            //add the comment prefix
            for(final String line : wrapped) {
                res.add(prefix + line);
            }
            
            return res;
        }
 
    }
}
