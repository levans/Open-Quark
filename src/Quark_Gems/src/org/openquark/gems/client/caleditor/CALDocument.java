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
 * CALDocument.java
 * Creation date: (1/18/01 4:04:26 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client.caleditor;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.openquark.cal.compiler.CALMultiplexedLexer;

/**
 * CALDocument is a document representing CAL source.
 * Creation date: (1/18/01 4:04:26 PM)
 * @author Luke Evans
 */
public class CALDocument extends javax.swing.text.PlainDocument {
    
    private static final long serialVersionUID = -6481758112928802591L;

    /**
     * Keys to be used in AttributeSet's holding a value of Token.
     */
    
    // Key to indicate a complex comment.  i.e. the comment starts/ends
    // part way through the line, or the line contains multiple comments.
    // The value stored will be an object of type CCommentInfo.
    static final Object CCommentAttribute = new AttributeKey();
    
    // Key for attribute to indicate a line that is all comment.  The stored
    // value will be the same object as the key.
    static final Object CommentAttribute = new AttributeKey();
    static final Object TestCommentAttribute = new AttributeKey();
    
    static class AttributeKey {
        
        private AttributeKey() {
        }
        
        @Override
        public String toString() {
            return "comment";
        }
        
    }
    
    static class CCommentInfo {
        // Store a collection of Point objects.
        // Each point will contain the start/end offset
        // of a comment.
        public Point[] comments;
        
        public CCommentInfo(Point[] comments) {
            this.comments = comments;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < comments.length; ++i) {
                sb.append(comments[i] + " ");
            }
            return sb.toString();
        }
    }
    
    /**
     * The Scanner class is a wrapper around our standard CALLexer.
     * This will be used to scan over portions of the text in the viewport
     * (on a paint) when we need to ascertain what the lexemes are in order
     * to decide on the style in which to paint them.
     *
     * The wrapper is necessary currently, as we have to recreate the lexer
     * everytime as there appears to be no easy way to keep resetting the
     * input stream.  We can make this much ligherweight in future by NOT
     * constructing a new CALLexer every time we're called upon to consider a
     * new portion of the document.  If this can happen, then we should make
     * Scanner a subclass of CALScanner for cleanliness too.
     *
     * We also implement some local utility methods here.  There's a nasty one
     * amongst them, namely getLexerPos which converts between the CALLexer's
     * token positions (given in line/column pairs) and character offsets from
     * the position at which we started the lexer. This is a rather inefficient
     * conversion as it requires fetching all the text buffer form the beginning
     * of the lexer position to the end, and then counting all the characters 
     * up to the line/column position held in a token.  This can only be achieved
     * by stepping through a character at a time.
     * 
     * getLexerPos is used by getStartOffset and getEndOffset to return
     * text buffer offsets for a given token. 
     *
     * Creation date: (1/23/2001 10:09:41 PM)
     * @author Luke Evans
     */
    public class Scanner {
        // The CALMultiplexedLexer we will use. Currently containership rather than inheritance
        // as we may need to recreate the lexer
        private CALMultiplexedLexer lexer;
        int p0;
        
        Scanner() {
            // TODOEL - the lexer is constructed with a null value for the compiler.
            //   This means that we will miss any compiler messages.
            //super(new DocumentInputStream(0, getLength()));
            lexer = new CALMultiplexedLexer(null, new DocumentReader(0, getLength()), null);
            //scanComments = true;
        }
        
        /**
         * Sets the range of the scanner.  This should be called
         * to reinitialize the scanner to the desired range of
         * coverage.
         */
        public void setRange(int p0, int p1) {
            // TODOEL - the lexer is constructed with a null value for the compiler.
            //   This means that we will miss any compiler messages.
            //useInputStream(new DocumentInputStream(p0, p1));
            lexer = new CALMultiplexedLexer(null, new DocumentReader(p0, p1), null);
            this.p0 = p0;
        }
        
        /**
         * Get a buffer position based on a line/column tuple.
         * We have to have this routine as we can't currently see an absolute buffer position
         * from the lexer, but instead only line/col positions.
         * Creation date: (1/22/01 5:57:55 PM)
         * @param line int the line number
         * @param column int the column number
         * @return int the buffer position
         */
        public int getLexerPos(int line, int column) {
            // We start from p0 and count the number of characters in each line, then
            // add the column number
            int offset = 0;
            String buffer = null;
            try {
                // Retrieve all the characters, from where the lexer's start point is, to
                // the end of the document, in order to do the character counting
                buffer = CALDocument.this.getText(p0, CALDocument.this.getLength() - p0);
            } catch (javax.swing.text.BadLocationException e) {
                IllegalStateException ise = new IllegalStateException();
                ise.initCause(e);
                throw ise;
            }
            int pos = -1;
            
            final int bufSize = buffer.length();
            
            // Line counts are 1 based!
            for (; line > 1; line--) {
                // Add all the characters in this line to offset                
                while (++pos < bufSize) {
                    offset++;
                    if (buffer.charAt(pos) == '\n') {
                        break;
                    }
                }
            }
            
            //note the commented out line below doesn't work because of tabs!
            //offset += column - 1;
            
            final int tabSize = lexer.getTabSize();            
            //column counts are 1 based.
            int currentColumnPos = 1;           
            while (++pos < bufSize && currentColumnPos < column) {  
                
                offset++;
                
                if (buffer.charAt(pos) != '\t') {
                    currentColumnPos++;
                } else { 
                    //tabs can consume from 1 to tabSize columns (a tab character moves the column to the next tab stop)
                    currentColumnPos =  (((currentColumnPos-1)/tabSize) + 1) * tabSize + 1;              
                }                                                      
            }
            
            // Return the full offset
            return offset;
        }
        
        /**
         * This fetches the starting location of the given
         * token in the document.
         */
        public final int getStartOffset(antlr.Token tok) {
            // No offset if no token yet!
            int begOffs = 0;
            if (tok != null) {
                begOffs = getLexerPos(tok.getLine(), tok.getColumn());
            }
            return p0 + begOffs;
        }
        
        /**
         * This fetches the starting location of the current
         * token in the document.
         */
        public final int getStartOffset() {
            antlr.Token tok = lexer.getTokenObject();
            // Use the more general routine
            return getStartOffset(tok);
        }
        
        /**
         * This fetches the ending location of the current
         * token in the document.  
         */
        public final int getEndOffset() {
            antlr.Token tok = lexer.getTokenObject();
            // No length if no token!
            int tokLength = 0;
            if (tok != null) {
                String tokText = tok.getText();
                // Some tokens (like EOF have null images)
                if (tokText != null) {
                    // Get the length of this token
                    tokLength = tokText.length();
                }
            }
            return getStartOffset(tok) + tokLength;
        }
        
        /**
         * Scan over one token, indicate if successful
         * Creation date: (1/22/01 1:55:15 PM)
         * @return boolean true if not at end of document, false otherwise (and on error)
         */
        public boolean scan() {
            try {
                if (lexer.nextToken().getType() != org.openquark.cal.compiler.CALTokenTypes.EOF) {
                    return true;
                } else {
                    return false;
                }
            } catch (antlr.TokenStreamException e) {
                // We failed to lex the input for some reason.
                // No big deal, but we're not likely to be able to parse the
                // code in this state, and may want to indicate this in the
                // future to the owner
                return false;
            }
        }
        
        /**
         * Return the CALToken for the given lexer position
         * Creation date: (1/22/01 2:01:47 PM)
         * @return CALToken the token including formatting directives
         */
        public antlr.Token getToken() {
            // Return the token from the lexer
            return lexer.getTokenObject();
        }
    }
    
    /**
     * Class to provide Reader functionality from a portion of a
     * Document.
     */
    class DocumentReader extends java.io.Reader {
        
        javax.swing.text.Segment segment;
        int p1; // end position
        int pos; // pos in document
        int index; // index into array of the segment
        
        public DocumentReader(int p0, int p1) {
            this.segment = new javax.swing.text.Segment();
            this.p1 = Math.min(getLength(), p1);
            pos = p0;
            try {
                loadSegment(1024);
            } catch (java.io.IOException ioe) {
                throw new Error("unexpected: " + ioe);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int read() throws java.io.IOException {
            int endIndex = index;
            if (endIndex >= segment.offset + segment.count) {
                if (pos >= p1) {
                    // no more data
                    return -1;
                }
                loadSegment(1024);
            }
            return segment.array[index++];
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int endIndex = index + len - 1;
            final int availableLength;
            
            if (endIndex >= segment.offset + segment.count) {
                if (pos >= p1) {
                    // no more data
                    return -1;
                }
                int nCharsBuffered = loadSegment(1024 + len);
                availableLength = Math.min(len, nCharsBuffered);
            } else {
                availableLength = len;
            }
            
            System.arraycopy(segment, index, cbuf, off, availableLength);
            index += availableLength;
            
            return availableLength;
        }
        
        int loadSegment(int nCharsToBuffer) throws java.io.IOException {
            try {
                nCharsToBuffer = Math.min(nCharsToBuffer, p1 - pos);
                getText(pos, nCharsToBuffer, segment);
                pos += nCharsToBuffer;
                index = segment.offset;
                return nCharsToBuffer;
            } catch (javax.swing.text.BadLocationException e) {
                throw new java.io.IOException("Bad location");
            }
        }

        @Override
        public void close() throws IOException {
            // We do not need to close anything.
        }
    }
    
    /**
     * CALDocument constructor comment.
     */
    public CALDocument() {
        // Construct with a GapContent storage object
        super(new javax.swing.text.GapContent(1024));
        
        // Set the default tab size for CALDocuments to 4
        putProperty("tabSize", Integer.valueOf(4));
    }
    /**
     * CALDocument constructor comment.
     * @param c javax.swing.text.AbstractDocument.Content
     */
    protected CALDocument(javax.swing.text.AbstractDocument.Content c) {
        super(c);
    }
    /**
     * Create a lexical analyzer for this document.
     */
    public Scanner createScanner() {
        return new Scanner();
    }
    
    /**
     * Fetch a reasonable location to start scanning
     * given the desired start location.  This allows
     * for adjustments needed to accomodate multiline
     * comments.
     */
    public int getScannerStart(int p) {
        javax.swing.text.Element elem = getDefaultRootElement();
        int lineNum = elem.getElementIndex(p);
        javax.swing.text.Element line = elem.getElement(lineNum);
        javax.swing.text.AttributeSet a = line.getAttributes();
        while (a.isDefined(CommentAttribute) && lineNum > 0) {
            lineNum -= 1;
            line = elem.getElement(lineNum);
            a = line.getAttributes();
        }
        return line.getStartOffset();
    }
    /**
     * Updates document structure as a result of text insertion.  This
     * will happen within a write lock.  The superclass behavior of
     * updating the line map is executed followed by marking any comment
     * areas that should backtracked before scanning.
     *
     * @param chng the change event
     * @param attr the set of attributes
     */
    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, javax.swing.text.AttributeSet attr) {
        //System.out.println ("before insertUpdate: " );
        //checkAttributes ();
        super.insertUpdate(chng, attr);
        //System.out.println ("after insertUpdate: " );
        //checkAttributes ();
        
        int lastChangedLine = updateCommentMarks(chng);
        //System.out.println ("lastChangedLine = " + lastChangedLine);
        changeCommentLines(chng, lastChangedLine);
    }
    
    /**
     * Updates any document structure as a result of text removal.
     * This will happen within a write lock.  The superclass behavior of
     * updating the line map is executed followed by placing a lexical
     * update command on the analyzer queue. Note: this is called before
     * the text is actually removed.
     *
     * @param chng the change event
     */
    @Override
    protected void removeUpdate(DefaultDocumentEvent chng) {
        super.removeUpdate(chng);
    }
    
    /**
     * Updates any document structure as a result of text removal.  This
     * method is called after the text has been removed from the Content.
     * This will happen within a write lock. If a subclass
     * of this class reimplements this method, it should delegate to the
     * superclass as well.
     *
     * @param chng a description of the change
     */
    @Override
    protected void postRemoveUpdate(AbstractDocument.DefaultDocumentEvent chng) {
        super.postRemoveUpdate(chng);
        //checkAttributes ();
        int lastChangedLine = updateCommentMarks(chng);
        //System.out.println ("lastChangedLine = " + lastChangedLine);
        
        changeCommentLines(chng, lastChangedLine);
    }
    
    protected void changeCommentLines(AbstractDocument.DefaultDocumentEvent chng, int lastChangedLine) {
        int offset = chng.getOffset();
        Element root = getDefaultRootElement();
        int lineIndex = root.getElementIndex(offset);
        
        for (int i = lineIndex + 1; i <= lastChangedLine; ++i) {
            Element line = root.getElement(i);
            int lineStart = line.getStartOffset();
            AbstractDocument.DefaultDocumentEvent otherChange = new AbstractDocument.DefaultDocumentEvent(lineStart, 0, DocumentEvent.EventType.REMOVE);
            fireRemoveUpdate(otherChange);
        }
    }
    
    /**
     * Marks lines as comments.
     * Assumes that each sub-element under the root element
     * corresponds to a line of text.
     * 
     * @param chng DocumentEvent
     * @return int Indicates the last line that has a change due to the commenting.
     */
    protected int updateCommentMarks(DocumentEvent chng) {
        //System.out.println ("update comment marks: " );
        //checkAttributes ();
        
        // update comment marks
        javax.swing.text.Element root = getDefaultRootElement();
        int offset = chng.getOffset();
        int endOffset = chng.getLength() + offset;
        int lineIndex = root.getElementIndex(offset);
        int endLineIndex = root.getElementIndex(endOffset);
        int lastChangedLine = lineIndex;
        int signalledLine = lineIndex;
        if (endLineIndex < signalledLine + 1) {
            endLineIndex = signalledLine + 1;
        }
        
        //System.out.println ("offset = " + offset + ", length = " + chng.getLength () + ", endOffset = " + endOffset);
        //System.out.println ("lineIndex = " + lineIndex + ", endLineIndex = " + endLineIndex + ", nLines = " + root.getElementCount());
        boolean inComment = false;
        lineIndex -= 2;
        if (lineIndex < 0) {
            lineIndex = 0;
        }
        
        if (lineIndex > 0) {
            Element prevLine = root.getElement(lineIndex - 1);
            javax.swing.text.MutableAttributeSet a = (javax.swing.text.MutableAttributeSet) prevLine.getAttributes();
            if (a != null && a.isDefined(CommentAttribute)) {
                inComment = true;
            }
        }
        
        boolean changeToLine = true;
        
        for (int i = lineIndex;(changeToLine || i <= endLineIndex) && i < root.getElementCount(); ++i) {
            //System.out.println ("commenting line " + i);
            
            changeToLine = false;
            
            javax.swing.text.Element elem = root.getElement(i);
            int p0 = elem.getStartOffset();
            int p1 = elem.getEndOffset();
            String s;
            try {
                s = getText(p0, p1 - p0);
            } catch (javax.swing.text.BadLocationException bl) {
                s = null;
            }
            //System.out.println("elem: " + p0 + " - " + p1 + ", " + s);                
            
            // Clear any existing comment info for the line.            
            javax.swing.text.MutableAttributeSet a = (javax.swing.text.MutableAttributeSet) elem.getAttributes();
            
            boolean prevOpenComment = a.isDefined(CommentAttribute);
            CCommentInfo prevInfo = (CCommentInfo) a.getAttribute(CCommentAttribute);
            a.removeAttribute(CommentAttribute);
            a.removeAttribute(CCommentAttribute);
            
            a.addAttribute(TestCommentAttribute, TestCommentAttribute);
            
            // Get a set of points indicating comment ranges in the line.
            Point[] points = scanLineForComments(s, inComment);
            if (points != null) {
                // The values in the points returned are relative to the beginning of the line of
                // text.  We need to update them so that the values represent offsets relative to
                // the beginning of the document.
                inComment = false;
                
                if (prevInfo == null || prevInfo.comments.length != points.length) {
                    changeToLine = true;
                }
                
                for (int j = 0; j < points.length; ++j) {
                    Point p = points[j];
                    if (p.y == -1) {
                        p.y = p1 - p0;
                        inComment = true;
                        // Attribute the line to indicate that there is still an 'open' comment
                        // at the end of the line.
                        a.addAttribute(CommentAttribute, CommentAttribute);
                        if (inComment != prevOpenComment) {
                            changeToLine = true;
                        }
                    }
                    
                    if (!changeToLine && (p.x != prevInfo.comments[j].x || p.y != prevInfo.comments[j].y)) {
                        changeToLine = true;
                    }
                }
                
                a.addAttribute(CCommentAttribute, new CCommentInfo(points));
            } else {
                if (prevInfo != null || inComment != prevOpenComment) {
                    changeToLine = true;
                }
                
                // No start/end of comments found in line.
                if (inComment) {
                    // If we are already in a comment then the whole line
                    // is a comment.
                    points = new Point[1];
                    points[0] = new Point(p0, p1);
                    a.addAttribute(CCommentAttribute, new CCommentInfo(points));
                    a.addAttribute(CommentAttribute, CommentAttribute);
                }
            }
            
            if (changeToLine && i > lastChangedLine) {
                lastChangedLine = i;
            }
            
        }
        //System.out.println ("after update comment marks: " );
        //checkAttributes ();
        
        return lastChangedLine;
    }
    
    /** 
     * Scans a line of text building up information on which portions of the line
     * are comments.
     * 
     * @param line A string containing the content of the line.
     * @param inComment A boolean flag indicating that this line is a continuation of an existing comment.
     */
    Point[] scanLineForComments(String line, boolean inComment) {
        //System.out.println ("scanLineForComments (String line = " + line + ", boolean inComment = " + inComment + ")");
        
        // The comment info for a line consists of a set of Point objects indicating the
        // start and end of a comment range in the line.  An end value of -1 means 'to the end of the line'.        
        List<Point> vPoints = new ArrayList<Point>();
        
        // First find the range of any /* */ comments.        
        int index = (inComment) ? 0 : line.indexOf("/*");
        while (index >= 0) {
            // Actually want to move back to include any leading
            // whitespace.
            while (index - 1 >= 0 && Character.isWhitespace(line.charAt(index - 1))) {
                index--;
            }
            
            //System.out.println ("cc start at " + index);
            
            Point p = new Point(index, -1);
            vPoints.add(p);
            
            // Look for end of comment.
            int endIndex = -1;
            if (index == 0 && inComment) {
                endIndex = line.indexOf("*/", index);
            } else {
                endIndex = line.indexOf("*/", index + 2);
            }
            
            if (endIndex != -1) {
                p.y = endIndex + 2;
                //System.out.println ("cc end at " + endIndex);
                
                index = line.indexOf("/*", endIndex);
            } else {
                index = -1;
            }
        }
        
        // Now find any locations where a single line comment start is in the line.
        int lastFound = -1;
        List<Point> lcPoints = new ArrayList<Point>();
        while ((index = line.indexOf("//", lastFound)) >= 0) {
            lastFound = index + 2;
            // Actually want to move back to include any leading
            // whitespace.
            while (index - 1 >= 0 && Character.isWhitespace(line.charAt(index - 1))) {
                index--;
            }
            
            lcPoints.add(new Point(index, line.length()));
            //System.out.println ("lc start at " + index);
            
        }
        
        // Now we want to remove any // comments that start inside a /* */ comment.
        // Then we remove any /* */ comments that come after a //.
        if (!vPoints.isEmpty() && !lcPoints.isEmpty()) {
            
            for (int i = lcPoints.size() - 1; i >= 0; --i) {
                Point lcPoint = lcPoints.get(i);
                // See if this is inside any /* */ ranges.
                for (int j = 0, nVPoints = vPoints.size(); j < nVPoints; ++j) {
                    Point vPoint = vPoints.get(j);
                    if (lcPoint.x > vPoint.x && lcPoint.x < vPoint.y) {
                        lcPoints.remove(i);
                        break;
                    }
                }
            }
            
            // If there are any // comment points left we only need to worry about the first one.
            if (!lcPoints.isEmpty()) {
                Point lcPoint = lcPoints.get(0);
                
                // Now if there is a valid // comment point we want to remove any /* */
                // comment ranges that come after it.
                for (int i = vPoints.size() - 1; i >= 0; --i) {
                    Point p = vPoints.get(i);
                    if (lcPoint.x < p.x) {
                        vPoints.remove(i);
                    }
                }
                
                vPoints.add(lcPoint);
            }
            
        } else {
            if (!lcPoints.isEmpty()) {
                vPoints.add(lcPoints.get(0));
            }
        }
        
        if (!vPoints.isEmpty()) {
            return vPoints.toArray(new Point[vPoints.size()]);
        }
        
        return null;
    }
    
    /** 
     * Gets the line element which contains the given offset.
     * @param offset int
     * @return Element
     */
    protected Element getLineElementForOffset(int offset) {
        Element root = getDefaultRootElement();
        int lineIndex = root.getElementIndex(offset);
        return root.getElement(lineIndex);
    }
    
    public void checkAttributes() {
        javax.swing.text.Element root = getDefaultRootElement();
        
        for (int i = 0; i < root.getElementCount(); ++i) {
            
            javax.swing.text.Element elem = root.getElement(i);
            javax.swing.text.MutableAttributeSet a = (javax.swing.text.MutableAttributeSet) elem.getAttributes();
            boolean c = a.isDefined(CommentAttribute);
            boolean cc = a.isDefined(CCommentAttribute);
            boolean ct = a.isDefined(TestCommentAttribute);
            
            System.out.println("    line " + i + ": c = " + c + ", cc = " + cc + ", ct = " + ct);
        }
    }
    
    /**
     * Removes some content from the document.
     * Removing content causes a write lock to be held while the
     * actual changes are taking place.  Observers are notified
     * of the change on the thread that called this method.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.
     * 
     * @param offs the starting offset >= 0
     * @param len the number of characters to remove >= 0
     * @exception BadLocationException  the given remove position is not a valid 
     *   position within the document
     * @see javax.swing.text.Document#remove
     */
    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
    }
    
    /**
     * Inserts some content into the document.
     * Inserting content causes a write lock to be held while the
     * actual changes are taking place, followed by notification
     * to the observers on the thread that grabbed the write lock.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.
     *
     * @param offs the starting offset >= 0
     * @param str the string to insert; does nothing with null/empty strings
     * @param a the attributes for the inserted content
     * @exception BadLocationException  the given insert position is not a valid 
     *   position within the document
     * @see javax.swing.text.Document#insertString
     */
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offs, str, a);
        
    }
    
}
