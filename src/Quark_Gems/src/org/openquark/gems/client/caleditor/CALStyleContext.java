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
 * CALStyleContext.java
 * Creation date: (1/18/01 6:39:38 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client.caleditor;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;

import org.openquark.cal.compiler.CALTokenTypes;

/**
 * The collection of styles used by the CALEditor for chromacoding (syntax highlighting)
 * the CAL source in CALEditor.  We also act as the factory for creating editor views from
 * the CAL MIME type (as encapsulated in the CALEditorKit).
 * Creation date: (1/18/01 6:39:38 PM)
 * @author Luke Evans
 */
public class CALStyleContext extends javax.swing.text.StyleContext implements javax.swing.text.ViewFactory {
    
    private static final long serialVersionUID = 8476223245936819970L;

    /**
     * The styles representing the actual token types.
     */
    private final javax.swing.text.Style[] tokenStyles;
    
    /**
     * Cache of foreground colours to represent the 
     * various tokens.
     */
    transient private java.awt.Color[] tokenColours;
    
    /**
     * Cache of fonts to represent the various tokens.
     */
    transient private java.awt.Font[] tokenFonts;
    
    /**
     *    The current style listener.  Currently *unicast*
     */
    transient private CALSyntaxStyleListener styleListener;
    
    /**
     * This View uses lexical elements to determine the style of text elements which
     * it renders.  Taken from an example on www.javasoft.com in the Swing Connection 
     * Creation date: (1/18/01 7:00:20 PM)
     * @author Luke Evans
     */
    class CALView extends javax.swing.text.PlainView {
        
        CALDocument.Scanner lexer;
        boolean lexerValid;
        
        /**
         * Construct a simple colorized view of CAL source text.
         */
        CALView(javax.swing.text.Element elem) {
            super(elem);
            CALDocument doc = (CALDocument) getDocument();
            lexer = doc.createScanner();
            lexerValid = false;
        }
        
        /**
         * Renders using the given rendering surface and area 
         * on that surface.  This is implemented to invalidate
         * the lexical scanner after rendering so that the next
         * request to drawUnselectedText will set a new range
         * for the scanner.
         *
         * @param g the rendering surface to use
         * @param a the allocated region to render into
         *
         * @see javax.swing.text.View#paint
         */
        @Override
        public void paint(java.awt.Graphics g, java.awt.Shape a) {
            super.paint(g, a);
            lexerValid = false;
        }
        
        /**
         * Renders the given range in the model as normal unselected
         * text.  This is implemented to paint colors based upon the
         * token-to-color translations.  To reduce the number of calls
         * to the Graphics object, text is batched up until a color
         * change is detected or the entire requested range has been
         * reached.  Note: this function assumes that all the text
         * in the specified range falls on a single line.
         *
         * @param g the graphics context
         * @param x the starting X coordinate
         * @param y the starting Y coordinate
         * @param p0 the beginning position in the model
         * @param p1 the ending position in the model
         * @return the location of the end of the range
         * @exception javax.swing.text.BadLocationException if the range is invalid
         */
        @Override
        protected int drawUnselectedText(java.awt.Graphics g, int x, int y, int p0, int p1) throws javax.swing.text.BadLocationException {
            CALDocument doc = (CALDocument) getDocument();
            java.awt.Color last = null;
            //if (p0 > 0) {
            //System.out.println ("drawUnselectedText (x = " + x + ", y = " + y + ", p0 = " + p0 + ", p1 = " + p1 + ")");
            //}
            
            // First check for lines tagged as comments
            boolean comment = false;
            Element line = doc.getLineElementForOffset(p0);
            //javax.swing.text.Element root = doc.getDefaultRootElement();
            //int lineNum = root.getElementIndex(p0);
            
            CALDocument.CCommentInfo cInfo = null;
            
            if (line != null) {
                AttributeSet att = line.getAttributes();
                if (att != null) {
                    // Grab the info about complex comments in this line if it exists.
                    cInfo = (CALDocument.CCommentInfo) att.getAttribute(CALDocument.CCommentAttribute);
                    /*
                     if (att.isDefined (doc.CommentAttribute)) {
                     comment = true;
                     Color fg = getForeground(CALTokenTypes.SL_COMMENT, "");
                     g.setColor (fg);
                     javax.swing.text.Segment text = getLineBuffer();
                     //System.out.println ("trying to print line. " + lineStart + ", " + lineEnd);
                      doc.getText(p0, p1 - p0, text);
                      //System.out.println("text for linew = " + text);                        
                       x = javax.swing.text.Utilities.drawTabbedText(text, x, y, g, this, p0);
                       }
                       */
                }
            }
            
            if (!comment) {
                
                int mark = p0;
                Point curComment = null;
                
                class BufferedToken {
                    public antlr.Token token = null;
                    public int endOffset = -1;
                    BufferedToken(antlr.Token token, int endOffset) {
                        this.token = token;
                        this.endOffset = endOffset;
                    }
                }
                
                List<BufferedToken> bufferedTokens = new ArrayList<BufferedToken>();
                
                for (; p0 < p1;) {
                    
                    java.awt.Color fg = null;
                    int p = p1;
                    
                    // Check if we are in a comment sub-section.  If we are
                    // draw from the current point to the end of the comment section.
                    if (cInfo != null && curComment == null) {
                        for (int i = 0; i < cInfo.comments.length; ++i) {
                            
                            if (p0 >= cInfo.comments[i].x + line.getStartOffset() && p0 < cInfo.comments[i].y + line.getStartOffset()) {
                                fg = getForeground(CALTokenTypes.SL_COMMENT, "");
                                p = cInfo.comments[i].y + line.getStartOffset();
                                break;
                            }
                        }
                    }
                    
                    if (fg == null) {
                        
                        antlr.Token token = null;
                        
                        if (bufferedTokens.isEmpty()) {
                            if (updateScanner(p0)) {
                                token = lexer.getToken();
                                p = Math.min(lexer.getEndOffset(), p1);
                            }
                            
                        } else {
                            BufferedToken bt = bufferedTokens.remove(0);
                            p = Math.min(bt.endOffset, p1);
                            token = bt.token;
                        }
                        
                        p = (p <= p0) ? p1 : p;
                        
                        if (token == null) {
                            // For some reason something went wrong with the lexing and we didn't get a token.
                            // This can happen when the user is in the middle of typing a string (e.g. - "abc).
                            // Just default to black text.
                            fg = java.awt.Color.black;
                            
                        } else {
                            // Look ahead to see if we are dealing with a compound name.
                            if (token.getType() == CALTokenTypes.VAR_ID || token.getType() == CALTokenTypes.CONS_ID) {
                                
                                boolean lookAhead = true;
                                boolean foundCompound = false;
                                
                                StringBuilder sb = new StringBuilder();
                                sb.append(token.getText());
                                
                                while (lookAhead) {
                                    lookAhead = false;
                                    int la = p;
                                    
                                    antlr.Token nt = null;
                                    int nl = -1;
                                    if (updateScanner(la)) {
                                        nl = lexer.getEndOffset();
                                        nt = lexer.getToken();
                                    }
                                    
                                    bufferedTokens.add(new BufferedToken(nt, nl));
                                    
                                    if (nt != null && nt.getType() == CALTokenTypes.DOT) {
                                        la = nl;
                                        
                                        antlr.Token nt2 = null;
                                        if (updateScanner(la)) {
                                            nl = lexer.getEndOffset();
                                            nt2 = lexer.getToken();
                                        }
                                        
                                        bufferedTokens.add(new BufferedToken(nt2, nl));
                                        
                                        if (nt2 != null && (nt2.getType() == CALTokenTypes.CONS_ID || nt2.getType() == CALTokenTypes.VAR_ID)) {
                                            
                                            sb.append(nt.getText());
                                            sb.append(nt2.getText());
                                            p = nl;
                                            
                                            // Want to continue scanning forward because a
                                            // compound name can have multiple qualifiers.
                                            lookAhead = true;
                                            
                                            // Mark that we have found a compund with at least
                                            // on qualifer.
                                            foundCompound = true;
                                            
                                            // Remove the two tokens we just buffered.
                                            bufferedTokens.remove(0);
                                            bufferedTokens.remove(0);
                                        }
                                    }
                                }
                                
                                if (foundCompound) {
                                    // The compound name will be in the string builder.
                                    fg = getForeground(CALTokenTypes.VAR_ID, sb.toString());
                                }
                            }
                            
                            if (fg == null) {
                                fg = getForeground(token.getType(), token.getText());
                            }
                        }
                    }
                    
                    if (fg != last && last != null) {
                        // color change, flush what we have
                        g.setColor(last);
                        javax.swing.text.Segment text = getLineBuffer();
                        doc.getText(mark, p0 - mark, text);
                        x = javax.swing.text.Utilities.drawTabbedText(text, x, y, g, this, mark);
                        mark = p0;
                    }
                    last = fg;
                    p0 = p;
                }
                
                if (mark < p1) {
                    // flush remaining
                    if (last != null) {
                        g.setColor(last);
                    }
                    
                    javax.swing.text.Segment text = getLineBuffer();
                    doc.getText(mark, p1 - mark, text);
                    x = javax.swing.text.Utilities.drawTabbedText(text, x, y, g, this, mark);
                }
            }
            
            return x;
        }
        
        @Override
        protected int drawSelectedText(java.awt.Graphics g, int x, int y, int p0, int p1) throws javax.swing.text.BadLocationException {
            //System.out.println ("drawSelectedText (x = " + x + ", y = " + y + ", p0 = " + p0 + ", p1 = " + p1 + ")");
            return super.drawSelectedText(g, x, y, p0, p1);
        }
        
        /**
         * Update the scanner (if necessary) to point to the appropriate
         * token for the given start position needed for rendering.
         * @param p int - update the scanner to this starting position
         * @return boolean - true if update was successful or false if errors
         */
        boolean updateScanner(int p) {
            try {
                if (!lexerValid) {
                    CALDocument doc = (CALDocument) getDocument();
                    lexer.setRange(doc.getScannerStart(p), doc.getLength());
                    lexerValid = true;
                }
                while (lexer.getEndOffset() <= p) {
                    if (!lexer.scan()) {
                        // Oops, at the end of this stream (shouldn't happen unless
                        // we have very peculiar lexemes!)
                        return false;
                    }
                }
                return true;
                
            } catch (Throwable e) {
                // Can't adjust scanner... calling logic
                // Will simply render the remaining text.
                e.printStackTrace();
                return false;
            }
        }
        
    }
    
    /**
     * Constructs a set of styles to represent java lexical 
     * tokens.  By default there are no colors or fonts specified.
     */
    public CALStyleContext() {
        super();
        //javax.swing.text.Style root = getStyle(DEFAULT_STYLE);
        tokenStyles = new javax.swing.text.Style[CALTokenTypes.EXPONENT + 1];
        
        // Set up some detault colours for CAL syntax
        tokenColours = new java.awt.Color[CALTokenTypes.EXPONENT + 1];
        
        // Keywords
        //see the CALLexer constructor for all the keywords
        //todoBI this can get seriously out of date and affect the colour coding of tokens in the code gem.
        java.awt.Color keywordColour = java.awt.Color.blue;
        
        tokenColours[CALTokenTypes.LITERAL_case] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_of] = keywordColour;        
        tokenColours[CALTokenTypes.LITERAL_if] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_then] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_else] = keywordColour;            
        tokenColours[CALTokenTypes.LITERAL_let] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_in] = keywordColour;
        
        tokenColours[CALTokenTypes.LITERAL_module] = keywordColour;    
        tokenColours[CALTokenTypes.LITERAL_friend] = keywordColour;   
        tokenColours[CALTokenTypes.LITERAL_import] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_using] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_function] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_dataConstructor] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_typeConstructor] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_typeClass] = keywordColour;        
        
        tokenColours[CALTokenTypes.LITERAL_public] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_protected] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_private] = keywordColour;
        
        tokenColours[CALTokenTypes.LITERAL_data] = keywordColour;        
        tokenColours[CALTokenTypes.LITERAL_foreign] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_unsafe] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_jvm] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_deriving] = keywordColour;
               
        tokenColours[CALTokenTypes.LITERAL_instance] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_class] = keywordColour;        
        tokenColours[CALTokenTypes.LITERAL_where] = keywordColour;
        tokenColours[CALTokenTypes.LITERAL_default] = keywordColour;
        
        tokenColours[CALTokenTypes.LITERAL_primitive] = keywordColour;
        
        
        // Literal numbers
        java.awt.Color literalColour = java.awt.Color.green.darker().darker();
        tokenColours[CALTokenTypes.CHAR_LITERAL] = literalColour;
        tokenColours[CALTokenTypes.INTEGER_LITERAL] = literalColour;
        tokenColours[CALTokenTypes.FLOAT_LITERAL] = literalColour;
        tokenColours[CALTokenTypes.STRING_LITERAL] = literalColour;
        
        // Operators within expressions
        java.awt.Color operatorColour = java.awt.Color.blue;
        tokenColours[CALTokenTypes.ASTERISK] = operatorColour;
        tokenColours[CALTokenTypes.PLUS] = operatorColour;
        tokenColours[CALTokenTypes.MINUS] = operatorColour;
        tokenColours[CALTokenTypes.SOLIDUS] = operatorColour;
        tokenColours[CALTokenTypes.PERCENT] = operatorColour;
        tokenColours[CALTokenTypes.AMPERSANDAMPERSAND] = operatorColour;
        tokenColours[CALTokenTypes.BARBAR] = operatorColour;
        tokenColours[CALTokenTypes.PLUSPLUS] = operatorColour;
        tokenColours[CALTokenTypes.EQUALSEQUALS] = operatorColour;
        tokenColours[CALTokenTypes.GREATER_THAN] = operatorColour;
        tokenColours[CALTokenTypes.GREATER_THAN_OR_EQUALS] = operatorColour;
        tokenColours[CALTokenTypes.LESS_THAN] = operatorColour;
        tokenColours[CALTokenTypes.LESS_THAN_OR_EQUALS] = operatorColour;
        tokenColours[CALTokenTypes.NOT_EQUALS] = operatorColour;
        tokenColours[CALTokenTypes.COLON] = operatorColour;
        tokenColours[CALTokenTypes.POUND] = operatorColour;
        tokenColours[CALTokenTypes.DOLLAR] = operatorColour;
        tokenColours[CALTokenTypes.BACKQUOTE] = operatorColour;
        
        // Separator syntax
        java.awt.Color separatorColour = java.awt.Color.black;
        tokenColours[CALTokenTypes.SEMICOLON] = separatorColour;
        tokenColours[CALTokenTypes.EQUALS] = separatorColour;
        tokenColours[CALTokenTypes.COLONCOLON] = separatorColour;
        tokenColours[CALTokenTypes.RARROW] = separatorColour;
        tokenColours[CALTokenTypes.BAR] = separatorColour;
        
        // Comments
        Color commentColour = Color.getHSBColor(118f / 360f, 0.77f, 0.74f);
        
        tokenColours[CALTokenTypes.SL_COMMENT] = commentColour;
        tokenColours[CALTokenTypes.ML_COMMENT] = commentColour;
        
        // Set up some default styles for CAL
        //javax.swing.text.Style parent = getStyle("numbers");
        //if (parent == null) {
        //parent = addStyle("numbers", root);
        //}    
        //javax.swing.text.Style s = addStyle(null, parent);
        //javax.swing.text.AttributeSet as = new javax.swing.text.AttributeSet();
        //as.
        //s.addAttributes();
        //s.addAttribute(null, null);  // TODO!
        //tokenStyles[CALTokenTypes.FLOAT_LITERAL] = s;
        
        // OR...
        // For each scan value we're interested in...
        // javax.swing.text.Style s = getStyleForScanValue(scan_number_or_manifest);
        // StyleConstants.setForeground(s, thisColour);
        // ...
        
    }
    /**
     * Add a style listener to the editor.
     * Creation date: (1/30/01 8:27:46 AM)
     * @param listener org.openquark.gems.client.caleditor.CALSyntaxStyleListener the listener to add
     * @exception java.util.TooManyListenersException we can't accept this listener.
     */
    public void addCALSyntaxStyleListener(CALSyntaxStyleListener listener) throws java.util.TooManyListenersException {
        // We're currently unicast only
        if (styleListener != null) {
            throw new java.util.TooManyListenersException("Can't add multiple listeners to unicast CALSyntaxStyleListener producer");
        }
        styleListener = listener;
    }
    /**
     * Creates a view from the given structural element of a
     * document.
     *
     * @param elem  the piece of the document to build a view of
     * @return the view
     * @see javax.swing.text.View
     */
    public javax.swing.text.View create(javax.swing.text.Element elem) {
        return new CALView(elem);
    }
    /**
     * Fetch the font to use for a lexical
     * token with the given scan value.
     */
    public java.awt.Font getFont(int code, String image) {
        java.awt.Font suggestedFont = null;
        if (tokenFonts == null) {
            tokenFonts = new java.awt.Font[CALTokenTypes.EXPONENT + 1];
        }
        if (code < tokenFonts.length) {
            java.awt.Font f = tokenFonts[code];
            if (f == null) {
                javax.swing.text.Style s = tokenStyles[code];
                if (s != null) {
                    f = getFont(s);
                } else {
                    f = null;
                }
            }
            suggestedFont = f;
        }
        
        // Check with listener if we have one
        if (styleListener != null) {
            suggestedFont = styleListener.fontLookup(code, image, suggestedFont);
        }
        // Return whatever the result was
        return suggestedFont;
    }
    /**
     * Fetch the foreground colour to use for a lexical
     * token with the given value.
     *
     * @param code int the scan code (token type) of the token
     * @param image String the image of the token
     * @return java.awt.Color the colour to use
     */
    public java.awt.Color getForeground(int code, String image) {
        //System.out.println ("getForeground(code = " + code + ", image = " + image + ")");
        java.awt.Color suggestedColour = java.awt.Color.black;
        if (tokenColours == null) {
            tokenColours = new java.awt.Color[CALTokenTypes.EXPONENT + 1];
        }
        if ((code >= 0) && (code < tokenColours.length)) {
            java.awt.Color c = tokenColours[code];
            if (c == null) {
                javax.swing.text.Style s = tokenStyles[code];
                if (s != null) {
                    c = javax.swing.text.StyleConstants.getForeground(s);
                } else {
                    c = java.awt.Color.black;
                }
            }
            suggestedColour = c;
        }
        // Check with the listener if we have one and is not a comment.
        if (styleListener != null && code != CALTokenTypes.SL_COMMENT && code != CALTokenTypes.ML_COMMENT) {
            suggestedColour = styleListener.foreColourLookup(code, image, suggestedColour);
        }
        
        // Return the result
        return suggestedColour;
    }
    /**
     * Fetches the attribute set to use for the given
     * scan code.  The set is stored in a table to
     * facilitate relatively fast access to use in 
     * conjunction with the scanner.
     */
    public javax.swing.text.Style getStyleForScanValue(int code, String image) {
        javax.swing.text.Style suggestedStyle = null;
        if (code < tokenStyles.length) {
            suggestedStyle = tokenStyles[code];
        }
        // Check with the listener if we have one
        if (styleListener != null) {
            suggestedStyle = styleListener.styleLookup(code, image, suggestedStyle);
        }
        
        // Return the result
        return suggestedStyle;
    }
    /**
     * Remove this style listener.
     * Creation date: (1/30/01 8:28:22 AM)
     * @param listener org.openquark.gems.client.caleditor.CALSyntaxStyleListener the listener to remove
     */
    public void removeCALSyntaxStyleListener(CALSyntaxStyleListener listener) {
        if (styleListener == listener) {
            styleListener = null;
        }
    }
}
