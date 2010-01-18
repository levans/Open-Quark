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
 * GemCodePanel.java
 * Creation date: (March 10, 2004 1:17:15 PM)
 * By: Iulian Radu
 */
package org.openquark.gems.client.caleditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.compiler.SourceRange;
import org.openquark.cal.compiler.CodeAnalyser.AnalysedIdentifier;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.gems.client.GemCutter;
import org.openquark.gems.client.ToolTipHelpers;
import org.openquark.util.Pair;



/**
 * This is an extension of the CALEditor class, providing 
 * support for interaction with code elements such as
 * supercombinators, class methods, data types, data constructors
 * type classes and arguments. The editor has the 
 * following features:
 * 
 *  - uses metadata to display descriptive tooltips for code elements
 *  - facilitates identification and resolution of ambiguous elements
 *    (these are unqualified elements which belong to multiple external modules)
 *  - provides accessibility for custom menus
 *
 * @author Iulian Radu
 */
public class AdvancedCALEditor extends CALEditor {
    
    private static final long serialVersionUID = 1971239530937184912L;

    /** List of qualified and unqualified identifiers found in the code */
    private List <AnalysedIdentifier> analysedIdentifiers;
    
    private List <AmbiguityOffset> ambiguityOffsets = new ArrayList<AmbiguityOffset>();
    
    /** Type information about the module which the code belongs to */
    private ModuleTypeInfo workingModuleTypeInfo;
    
    /** Provider for popup menus */
    private IdentifierPopupMenuProvider popupMenuProvider;

    /** The workspace in which the editor exists. */
    private final CALWorkspace workspace;
    
    /**
     * Interface to define a provider for a popup menu for interacting with identifiers.
     * @author Iulian Radu
     */
    public interface IdentifierPopupMenuProvider {
        /**
         * Get the popup menu for a given identifier.
         * @param identifier clicked identifier
         * @return JPopupMenu the popup menu for this identifier
         */
        JPopupMenu getPopupMenu(PositionlessIdentifier identifier);
    }
    
    /**
     * Class representing an analysed identifier without position.
     * @author Iulian Radu
     */
    public static final class PositionlessIdentifier {
        
        /** Unqualified name of identifier */
        private final String unqualifiedName;
        
        /** Module name as appearing in the source text, if identifier is qualified. This is null if the identifier is unqualified. */
        private final ModuleName rawModuleName;
        
        /** Resolved module name, if identifier is qualified. This is null if the identifier is unqualified. */
        private final ModuleName resolvedModuleName;
        
        /**
         * If {@link #rawModuleName} is resolvable, then this is the minimally qualified module name that resolves to the same module.
         * Otherwise, this holds the same value as {@link #rawModuleName}.
         */
        private final ModuleName minimallyQualifiedModuleName;
        
        /** Category */
        private final SourceIdentifier.Category category;
        
        /** Qualification type */
        private final CodeAnalyser.AnalysedIdentifier.QualificationType qualificationType;
        
        /** The analysed identifier which is referenced, if any */
        private final CodeAnalyser.AnalysedIdentifier referenceIdentifier; 
        
        /** 
         * Constructor
         * @param unqualifiedName identifier name
         * @param rawModuleName identifier module (null if none)
         * @param resolvedModuleName resolved identifier module (null if none)
         * @param minimallyQualifiedModuleName
         *           if {@link #rawModuleName} is resolvable, then this is the minimally qualified module name that resolves to the same module.
         *           Otherwise, this should have the same value as {@link #rawModuleName}.
         * @param category identifier category
         * @param qualificationType identifier qualification type
         */
        public PositionlessIdentifier(
                String unqualifiedName, 
                ModuleName rawModuleName, 
                ModuleName resolvedModuleName, 
                ModuleName minimallyQualifiedModuleName, 
                SourceIdentifier.Category category, CodeAnalyser.AnalysedIdentifier.QualificationType qualificationType) {
            
            if ((unqualifiedName == null) || (category == null) || (qualificationType == null)) {
                throw new NullPointerException();
            }
            
            this.unqualifiedName     = unqualifiedName;
            this.rawModuleName       = rawModuleName;
            this.resolvedModuleName  = resolvedModuleName;
            this.minimallyQualifiedModuleName = minimallyQualifiedModuleName;
            this.category            = category;
            this.qualificationType   = qualificationType;
            this.referenceIdentifier = null;
        }
        
        /** 
         * Constructor, specifying reference
         */
        public PositionlessIdentifier(CodeAnalyser.AnalysedIdentifier referenceIdentifier) {
            
            if (referenceIdentifier == null) {
                throw new NullPointerException();
            }
            
            this.unqualifiedName      = referenceIdentifier.getName();
            this.rawModuleName        = referenceIdentifier.getRawModuleName();
            this.resolvedModuleName   = referenceIdentifier.getResolvedModuleName();
            this.minimallyQualifiedModuleName = referenceIdentifier.getMinimallyQualifiedModuleName();
            this.category             = referenceIdentifier.getCategory();
            this.qualificationType    = referenceIdentifier.getQualificationType();
            this.referenceIdentifier  = referenceIdentifier;
        }
        
        /** 
         * Checks whether this object's fields are the same to another
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            if ((o == null) || !(o instanceof PositionlessIdentifier)) {
                return false;
            }
            
            return (this.unqualifiedName.equals(((PositionlessIdentifier)o).getName()))  &&
                   areMaybeModuleNamesEqual(this.rawModuleName, ((PositionlessIdentifier)o).getRawModuleName()) && 
                   areMaybeModuleNamesEqual(this.resolvedModuleName, ((PositionlessIdentifier)o).getResolvedModuleName()) && 
                   areMaybeModuleNamesEqual(this.minimallyQualifiedModuleName, ((PositionlessIdentifier)o).getMinimallyQualifiedModuleName()) && 
                   (this.category == (((PositionlessIdentifier)o).getCategory())) &&
                   (this.qualificationType == (((PositionlessIdentifier)o).getQualificationType()));
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            result = 37*result + unqualifiedName.hashCode();
            result = 37*result + rawModuleName.hashCode();
            result = 37*result + resolvedModuleName.hashCode();
            result = 37*result + minimallyQualifiedModuleName.hashCode();
            result = 37*result + category.hashCode();
            result = 37*result + qualificationType.hashCode();
            return result;
        }
        
        // Simple accessors
        
        public ModuleName getRawModuleName() {
            return rawModuleName;
        }
        
        public ModuleName getResolvedModuleName() {
            return resolvedModuleName;
        }
        
        public ModuleName getMinimallyQualifiedModuleName() {
            return minimallyQualifiedModuleName;
        }
        
        public String getName() {
            return unqualifiedName;
        }
        
        public SourceIdentifier.Category getCategory() {
            return category;
        }
        
        public CodeAnalyser.AnalysedIdentifier.QualificationType getQualificationType() {
            return qualificationType;
        }
        
        public CodeAnalyser.AnalysedIdentifier getReference() {
            return referenceIdentifier;
        }
        
        private static boolean areMaybeModuleNamesEqual(ModuleName maybeModuleName1, ModuleName maybeModuleName2) {
            if (maybeModuleName1 == null) {
                return maybeModuleName2 == null;
            } else {
                return maybeModuleName1.equals(maybeModuleName2);
            }
        }
    }
    
    
    /**
     * Class to handle mouse events on text identifiers.
     * 
     * @author Iulian Radu
     */
    private class MouseHandler extends org.openquark.gems.client.utilities.MouseClickDragAdapter {
        
        /**
         * Constructor for the Mouse Handler
         */
        private MouseHandler() {
        }
        
        /**
         * Surrogate method for mouseClicked.  Called only when our definition of click occurs.
         * @param e MouseEvent the relevant event
         * @return boolean true if the click was a double click
         */
        @Override
        public boolean mouseReallyClicked(MouseEvent e){

            boolean doubleClicked = super.mouseReallyClicked(e);
            
            return doubleClicked;
        }
        
        /**
         * If mouse event triggers popup, show it
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            maybeShowPopup(e);
        }
        
        /**
         * If mouse event triggers popup, show it
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            maybeShowPopup(e);
        }
        
        /**
         * Show the popup, if the given mouse event is the popup trigger.
         * @param e the mouse event.
         */
        private void maybeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger() && popupMenuProvider != null) {

                // Get the popup menu for the current identifier.
                CodeAnalyser.AnalysedIdentifier identifier = getIdentifierAtPoint(e.getPoint());
                if (identifier == null) {
                    // not pointing to anything valid
                    return;
                }
                
                PositionlessIdentifier strippedIdentifier = 
                    new PositionlessIdentifier(identifier);
                
                if (popupMenuProvider != null) {
                    JPopupMenu menu = popupMenuProvider.getPopupMenu(strippedIdentifier);
                    if (menu != null) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        }
    }
    
    /** 
     * A convenience class for locating a portion of cal code within
     * the buffer of the text editor. 
     */
    public static class EditorLocation {
        
        /** Start offset of the portion */
        private final int start;
        
        /** End offset of the portion */
        private final int end;
        
        /** Line number of the portion */
        private final int lineNumber;
        
        private EditorLocation(int start, int end, int lineNumber) {
            
            if (start < 0 || end < 0 || lineNumber < 0) {
                throw new IllegalArgumentException();
            }
            
            if (start > end) {
                throw new IllegalArgumentException();
            }
            
            this.start = start;
            this.end = end;
            this.lineNumber = lineNumber;
        }
        
        public int getStartOffset() {
            return start;
        }
        
        public int getEndOffset() {
            return end;
        }
        
        public int getLineNumber() {
            return lineNumber;
        }
    }
    
    /** 
     * A convenience class for storing an ambiguous identifier and its position.
     * @author Iulian Radu
     */
    public static class AmbiguityOffset {
        
        private final CodeAnalyser.AnalysedIdentifier identifier;
        private final EditorLocation ambiguityLocation;
        
        private AmbiguityOffset(CodeAnalyser.AnalysedIdentifier identifier, EditorLocation offset) {
            
            if (identifier == null) {
                throw new NullPointerException();
            }
            
            this.identifier = identifier;
            this.ambiguityLocation = offset;
        }
        
        public int getStartOffset() {
            return ambiguityLocation.getStartOffset();
        }
        
        public int getEndOffset() {
            return ambiguityLocation.getEndOffset();
        }
        
        public int getLineNumber() {
            return ambiguityLocation.getLineNumber();
        }
        
        public CodeAnalyser.AnalysedIdentifier getIdentifier() {
            return identifier;
        }
    }
    
    /**
     * This class draws a little squiggly underline as the highlight. It is used
     * to indicate the location of code errors and ambiguities.
     * 
     * NOTE: This class assumes that the highlight is on one line. It doesn't work
     * for hightlights that span multiple lines.
     * @author Frank Worsley
     */
    public abstract static class UnderlineHighlightPainter implements Highlighter.HighlightPainter {

        /** The color of the line to paint. */
        public abstract Color getLineColor();

        /**
         * @see javax.swing.text.Highlighter.HighlightPainter#paint(java.awt.Graphics, int, int, java.awt.Shape, javax.swing.text.JTextComponent)
         */
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {

            try {
                
                TextUI textUI = c.getUI();
                Rectangle start = textUI.modelToView(c, p0);
                Rectangle end = textUI.modelToView(c, p1);

                g.setColor(getLineColor());

                // This is the y coordinate of the bottom of the area we can draw in
                int y = start.y + start.height - 1;
                
                // Draws a squiggly line. We draw at least two squiggles.
                int width = 1;
                int height = 1;
                int x = start.x;
                int count = 0;
                
                do {
                    g.drawLine(x, y, x + width, y);
                    g.drawLine(x + width + 1, y - height, x + 2*width + 1, y - height);
                    x += 2*width + 2;
                    count++;
                } while (x < end.x - width || count < 2);
                
                // Draws a simple straight line
                //g.drawLine(start.x, y, end.x, y);

                // Draws a bounding box
                //g.drawRect(start.x - 2, start.y, end.x - start.x + 4, start.height - 1);

            } catch (BadLocationException e) {
                throw new IllegalStateException("invalid location drawing highlight");
            }
        }
    }
    
    /** Highlight painter for ambiguities */
    class AmbiguityUnderlineHighlightPainter extends UnderlineHighlightPainter {

        private final Color LINE_COLOR = Color.BLUE;
        @Override
        public Color getLineColor() {
            return LINE_COLOR;
        }
    }
    
    /**
     * Maintains highlights over the specified symbols in the current editor.
     * @author Iulian Radu
     */
    public class SymbolHighlighter {
        
        /** Default highlighting color */
        private Color highlightColor = new Color(255,255,70,150);
        
        /** Highlighter used to draw our highlights */
        private final Highlighter highlighter;
        
        /** References to highlights for specified identifiers */
        private final List<Object> highlights = new ArrayList<Object>();
        
        /** Identifiers to highlight */
        private final List<AnalysedIdentifier> identifiers;
        
        
        /** Constructor */
        public SymbolHighlighter(List<AnalysedIdentifier> identifiers) {
            if (identifiers == null) {
                throw new NullPointerException();
            }
            
            this.highlighter = getHighlighter();
            this.identifiers = identifiers;
        }
        
        /** 
         * Set the highlight color as specified
         * Note: This does not update the color of existing applied highlights
         * 
         * @param color new highlight bar color
         */
        public void setHighlightColor(Color color) {
            highlightColor = color;
        }
        
        /** Apply highlights to the specified identifiers */
        public void applyHighlights() {
            removeHighlights();
            try {
                for (final AnalysedIdentifier referenceIdentifier : identifiers) {
                    SourceRange referenceIdentifierOffsetRange = referenceIdentifier.getOffsetRange();
                    AdvancedCALEditor.EditorLocation editorLoc = getEditorTokenOffset(referenceIdentifierOffsetRange.getStartLine(), referenceIdentifierOffsetRange.getStartColumn(),
                            referenceIdentifier.getName().length());
                    int posStart = editorLoc.getStartOffset();
                    int posEnd = editorLoc.getEndOffset();
                    Object highlightRef = highlighter.addHighlight(posStart, posEnd, new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
                    highlights.add(highlightRef);
                }
            } catch (BadLocationException ex) {
                throw new IllegalStateException("Cannot highlight variable because position is invalid");
            }
        }
        
        /** Remove all applied highlights */
        public void removeHighlights() {
            for (int i = 0; i < highlights.size(); i ++) {
                highlighter.removeHighlight(highlights.get(i));
            }
        }
        
        /** 
         * Pads all highlights as specified.
         * The highlighted sections will constrain to fit the text buffer size.
         * 
         * @param leftPad characters to pad on the left side
         * @param rightPad characters to pad on the right side
         */
        void padHighlights(int leftPad, int rightPad) {
            
            for (int i = 0; i < highlights.size(); i ++) {
                Highlight highlight = (Highlight)highlights.get(i);
                Highlight newHighlight = getPaddedHighlight(highlight, leftPad, rightPad);
                highlights.set(i, newHighlight);
            }
        }
        
        /** 
         * Pads the highlight at the specified position
         * The highlighted section will constrain to fit the text buffer size.
         * 
         * @param highlightPos position contained within the highlight
         * @param leftPad characters to pad on the left side
         * @param rightPad characters to pad on the right side
         */
        void padHighlight(int highlightPos, int leftPad, int rightPad) {
            for (int i = 0; i < highlights.size(); i ++) {
                Highlight highlight = (Highlight)highlights.get(i);
                if ((highlight.getStartOffset() <= highlightPos) && (highlightPos <= highlight.getEndOffset())) {
                    Highlight newHighlight = getPaddedHighlight(highlight, leftPad, rightPad);
                    highlights.set(i, newHighlight);
                }
            }
        }
        
        /** 
         * Pads the highlight as specified.
         * The highlighted section will constrain to fit the text buffer size.
         * 
         * @param highlight highlight to be padded 
         * @param leftPad characters to pad on the left side
         * @param rightPad characters to add on the right side
         */
        private Highlight getPaddedHighlight(Highlight highlight, int leftPad, int rightPad) {
            
            int textBufferSize = getText().length();
            
            HighlightPainter painter = highlight.getPainter();
            int posStart = Math.max(0, highlight.getStartOffset() - leftPad);
            int posEnd = Math.min(textBufferSize, highlight.getEndOffset() + rightPad);
            
            try {
                highlighter.removeHighlight(highlight);
                highlight = (Highlight)highlighter.addHighlight(posStart, posEnd, painter);
                return highlight;
            } catch (BadLocationException ex) {
                throw new IllegalStateException("Cannot highlight variable because position is invalid");
            }
        }
    }
    
    /**
     * Listener notified when symbol renaming completes.
     * @author Iulian Radu
     */
    public interface SymbolRenamerListener {
        public void renameDone(String oldName, String newName);
        public void renameCanceled(String oldName);
    }
    
    /**
     * This class handles user interaction with the editor while a symbol is being renamed. 
     * @author Iulian Radu
     */
    class SymbolRenamer {
        
        /** List of identifiers to be renamed */
        private final List<AnalysedIdentifier> symbolPositions;
        
        /** Old name of the symbols */
        private final String oldName;
        
        /** New name, updated as user types */
        private String newName;
        
        /** Caret position within the name */
        private int caretPos; 
        
        /** The identifier where user is typing; updates to this name are handled by the editor itself */
        private final AnalysedIdentifier startIdentifier;
        
        /** Listener notified when renaming completes */
        private final SymbolRenamerListener renamerListener;
        
        // Listeners to the editor previous to entering renaming mode. 
        // These are removed once renaming starts, and put back once it completes.
        private KeyListener[] oldKeyListeners;
        private MouseListener[] oldMouseListeners;
        
        /** Highlighters for the references and definition of the renamed identifiers */ 
        private final SymbolHighlighter referenceHighlighter;
        private final SymbolHighlighter definitionHighlighter;
        
        /**
         * Mouse listener; finishes editing if mouse is clicked.
         */
        private final MouseListener cancelMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                done();
            }
        };
        
        /**
         * Focus listener; finishes editing if focus is switched somewhere else
         */
        private final FocusListener focusListener = new FocusListener() {
            public void focusGained(FocusEvent e) {
            }
            public void focusLost(FocusEvent e) {
                done();
            }
        };
        
        /**
         * Key listener for the editor while in rename mode.
         * As the user types, all identifier references are updated to match keystrokes. Caret
         * navigation is restriced to the editing section.
         * 
         * Implementation note: 
         *   Associated to the editor are at least 3 key listeners, invoked in order of appearance:
         *       - a top level document listener inserting typed alphanumeric characters into 
         *         the editor
         *       - this listener
         *       - an editing listener, handling editor navigation (eg: arrow keys) and deletion 
         */
        private final KeyListener renameKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                
                int keyCode = e.getKeyCode();
                
                if (e.isControlDown() || e.isAltDown()) {
                    e.consume();
                    return;
                    
                } else if (keyCode == KeyEvent.VK_ESCAPE) {
                    // Exit cancelling
                    cancel();
                    e.consume();
                    return;
                    
                } else if (keyCode == KeyEvent.VK_ENTER) {
                    // Exit ok
                    done();
                    e.consume();
                    return;
                    
                } else if ( (keyCode == KeyEvent.VK_UP) ||
                            (keyCode == KeyEvent.VK_DOWN) ||
                            (keyCode == KeyEvent.VK_PAGE_UP) ||
                            (keyCode == KeyEvent.VK_PAGE_DOWN)) {
             
                    // Don't respond to these       
                    e.consume();  
                    return;
                
                } else if (keyCode == KeyEvent.VK_HOME) {
                    setCaretPosition(getCaretPosition() - caretPos);
                    caretPos = 0;
                    e.consume();
                    return;
                    
                } else if (keyCode == KeyEvent.VK_END) {
                    setCaretPosition(getCaretPosition() + (newName.length() - caretPos));
                    caretPos = newName.length();
                    e.consume();
                    return;
                    
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    if (getCaretPosition() > 0 && caretPos > 0) {
                        caretPos--;
                    } else {
                        e.consume();
                    }
                    return;
                    
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    if (getCaretPosition() < getText().length() && caretPos < newName.length()) {
                        caretPos++;
                    } else {
                        e.consume();
                    }
                    return;
                    
                } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    if (getCaretPosition() <= 0 || caretPos <= 0) {
                        // If we are back-spacing beyond our identifier space, 
                        // insert a character to consume so the editor is not changed
                        
                        select(getCaretPosition(), getCaretPosition());
                        replaceSelection("?");
                        return;
                    }
                } else if (keyCode == KeyEvent.VK_DELETE) {
                    if (!(getCaretPosition() < getText().length() && caretPos < newName.length())) {
                        // We are deleting at the edge of the identifier space
                        
                        e.consume();
                        return;
                    }
                } else if (keyCode == KeyEvent.VK_INSERT) {
                    e.consume();
                    return;
                    
                } 
                
                // Now, insert the typed character (or delete characters) from each edited section
                
                if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                    // Not an insertable character, so stop.
                    // Note: backspace and delete characters do not fall under this category
                    return;
                }
                
                if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    // For backspace, we remove the character to the left of the caret
                    newName = newName.substring(0, caretPos-1) + newName.substring(caretPos);
                    
                } else if (keyCode == KeyEvent.VK_DELETE) {
                    // For delete, we remove the character to the right of the caret
                    newName = newName.substring(0, caretPos) + newName.substring(caretPos + 1);
                    
                } else {
                    
                    // Insert character at the caret
                    if (caretPos == 0) {
                        // Highlight one character before the editing position, so that the inserted char
                        // will be highlighted
                        referenceHighlighter.padHighlight(getCaretPosition(), 1, 0);
                        definitionHighlighter.padHighlight(getCaretPosition(), 1, 0);
                        SwingUtilities.invokeLater(new Runnable() {
                            // And after insertion, remove highlight to original size
                            public void run() {
                                referenceHighlighter.padHighlight(getCaretPosition(), -1, 0);
                                definitionHighlighter.padHighlight(getCaretPosition(), -1, 0);
                            }
                        });
                        e.consume();
                    }
                    newName = newName.substring(0, caretPos) + e.getKeyChar() + newName.substring(caretPos);
                    caretPos++;
                }
                
                // Save position of the current editor caret
                int editorCaretPos = getCaretPosition();
                
                // Count the number of identifiers passed
                int identifiers = 0;
                
                // Keep track of which line we are scanning
                int currentLine = 0;
                
                // Add a padding to the highlights, in case we insert at the beginning of
                // the highlight (in this case, the highlight would not grow since characters
                // are inserted before the highlight start position). This will be reverted
                // after all identifiers are modified.
                referenceHighlighter.padHighlights(1, 0);
                definitionHighlighter.padHighlights(1, 0);
                    
                try {
                    for (final AnalysedIdentifier identifier : symbolPositions) {
                    
                        SourceRange identifierOffsetRange = identifier.getOffsetRange();
                        
                        // Get original position of each identifier 
                        int pos = (convertPositionToEditorOffset(identifierOffsetRange.getStartLine(), identifierOffsetRange.getStartColumn()));
                        if (currentLine != identifierOffsetRange.getStartLine()) {
                            // The convertPositionToEditorOffset() uses the start offset of the identifier line
                            // so we only account for identifier on our line, and trust that the editor will
                            // offset position due to insertions at higher lines
                            currentLine = identifierOffsetRange.getStartLine();
                            identifiers = 0;
                        }
                        // Each position is offset due to modifications to the identifiers before it
                        int posOffset = identifiers*(newName.length() - oldName.length()) + caretPos;
                        pos = pos + posOffset;
                        
                        identifiers++; 
                        if (identifier == startIdentifier) {
                            // Don't do anything since the editor will do editing for us here
                            continue;
                        }
                        
                        SourceRange startIdentifierOffsetRange = startIdentifier.getOffsetRange();
                        
                        if (keyCode == KeyEvent.VK_BACK_SPACE) {
                            if ((startIdentifierOffsetRange.getStartLine() == currentLine) && (startIdentifierOffsetRange.getStartColumn() < identifierOffsetRange.getStartColumn())) {
                                pos++;
                            }
                            // Backspace eats the character left of the caret
                            select(pos-1, pos);
                            replaceSelection("");
                            if ((startIdentifierOffsetRange.getStartLine() > currentLine) || ((startIdentifierOffsetRange.getStartLine() == currentLine) &&  (startIdentifierOffsetRange.getStartColumn() > identifierOffsetRange.getStartColumn()))) {
                                editorCaretPos--;
                            }
                            
                        } else if (keyCode == KeyEvent.VK_DELETE) {
                            if ((startIdentifierOffsetRange.getStartLine() == currentLine) && (startIdentifierOffsetRange.getStartColumn() < identifierOffsetRange.getStartColumn())) {
                                pos++;
                            }
                            // Delete eats the character right of the caret
                            select(pos, pos+1);
                            replaceSelection("");
                            if ((startIdentifierOffsetRange.getStartLine() > currentLine) || ((startIdentifierOffsetRange.getStartLine() == currentLine) &&  (startIdentifierOffsetRange.getStartColumn() > identifierOffsetRange.getStartColumn()))) {
                                editorCaretPos--;
                            }

                        } else {
                            pos -= 1;
                            if ((startIdentifierOffsetRange.getStartLine() == currentLine) && (startIdentifierOffsetRange.getStartColumn() < identifierOffsetRange.getStartColumn())) {
                                pos--;
                            }
                            // Insert at caret position
                            select(pos, pos);
                            replaceSelection(Character.toString(e.getKeyChar()));
                            if ((startIdentifierOffsetRange.getStartLine() > currentLine) || ((startIdentifierOffsetRange.getStartLine() == currentLine) &&  (startIdentifierOffsetRange.getStartColumn() > identifierOffsetRange.getStartColumn()))) {
                                editorCaretPos++;
                            }
                        } 
                    }
                } catch (BadLocationException ex) {
                    throw new IllegalStateException("Identifier contains illegal position in editor.");
                }
                
                // Move caret back because of backspace
                if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    caretPos--;
                }
                
                // Shrink the highlights
                referenceHighlighter.padHighlights(-1,0);
                definitionHighlighter.padHighlights(-1,0);
                
                // Put back the editor caret to the selected identifier
                setCaretPosition(editorCaretPos);
            }
        };

        /**
         * Constructor
         * @param startIdentifier identifier where editing starts
         * @param renamerListener listener for rename completion
         */
        SymbolRenamer(AnalysedIdentifier startIdentifier, SymbolRenamerListener renamerListener) {
            if (startIdentifier == null) {
                throw new NullPointerException();
            }
            if ((startIdentifier.getCategory() != SourceIdentifier.Category.LOCAL_VARIABLE) && 
                (startIdentifier.getCategory() != SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION)) {
                throw new UnsupportedOperationException();
            }
            
            this.startIdentifier = startIdentifier;
            this.renamerListener = renamerListener;
            
            oldName = startIdentifier.getName();
            newName = oldName;
            caretPos = oldName.length();
            
            // Calculate which identifiers will be modified and highlight
            Pair<SymbolHighlighter, SymbolHighlighter> highlighters = createLocalVariableHighlighters(startIdentifier);
            definitionHighlighter = highlighters.snd();
            referenceHighlighter = highlighters.fst();
            symbolPositions = findRelatedIdentifiers(startIdentifier, true);
        }
        
        /**
         * Enter rename mode.
         * After saving editor state, this method replaces existing listeners with our own.
         */
        void start() {
            
            // Put caret at the end of the identifier being edited
            try {
                SourceRange startIdentifierOffsetRange = startIdentifier.getOffsetRange();
                setCaretPosition(convertPositionToEditorOffset(startIdentifierOffsetRange.getStartLine(), startIdentifierOffsetRange.getStartColumn()) + caretPos);
            } catch (BadLocationException ex) {
                throw new IllegalStateException("Identifier contains illegal position in editor.");
            }
            
            // Remove listeners from the editor and add our own
            
            oldKeyListeners = getKeyListeners();
            for (final KeyListener oldKeyListener : oldKeyListeners) {
                removeKeyListener(oldKeyListener);
            }
            addKeyListener(renameKeyListener);
            
            oldMouseListeners = getMouseListeners();
            for (final MouseListener oldMouseListener : oldMouseListeners) {
                removeMouseListener(oldMouseListener);
            }
            addMouseListener(cancelMouseListener);
            
            addFocusListener(focusListener);
            
            // Highlight symbols
            
            referenceHighlighter.applyHighlights();
            definitionHighlighter.applyHighlights();
        }
        
        /**
         * Exit renaming mode. 
         * This restores the editor listeners to their previous state.
         */
        void done() {
            finish();
            
            // Inform rename listener that we are finished
            if (renamerListener != null) {
                renamerListener.renameDone(oldName, newName);
            }
        }
        
        void finish() { 
        
            // Put back editor listeners
            
            removeKeyListener(renameKeyListener);
            for (final KeyListener oldKeyListener : oldKeyListeners) {
                addKeyListener(oldKeyListener);
            }
            
            removeMouseListener(cancelMouseListener);
            for (final MouseListener oldMouseListener : oldMouseListeners) {
                addMouseListener(oldMouseListener);
            }
            
            removeFocusListener(focusListener);
            
            referenceHighlighter.removeHighlights();
            definitionHighlighter.removeHighlights();
        }
        
        /**
         * Cancel editing.
         * This traverses affected symbol positions and restores them to the original form.
         * 
         * Note: This assumes that identifiers are ordered by source position
         */
        void cancel() {
            
            // Revert the identifiers to their original name
            
            for (final AnalysedIdentifier identifier : symbolPositions) {
                try {
                    SourceRange identifierOffsetRange = identifier.getOffsetRange();
                    int pos = (convertPositionToEditorOffset(identifierOffsetRange.getStartLine(), identifierOffsetRange.getStartColumn()));
                    select(pos, pos + newName.length());
                    replaceSelection(oldName);
                } catch (BadLocationException ex) {
                    throw new IllegalStateException("Identifier contains illegal position in editor.");
                }
            }
            
            finish();
            
            // Inform rename listener that we are finished
            if (renamerListener != null) {
                renamerListener.renameCanceled(oldName);
            }
        }
    }
    
    
    /** Constructor with module */
    public AdvancedCALEditor(ModuleTypeInfo workingModuleTypeInfo, CALWorkspace workspace) {
        this.ambiguityOffsets = new ArrayList<AmbiguityOffset>();
        this.analysedIdentifiers = new ArrayList<AnalysedIdentifier>();
        this.workingModuleTypeInfo = workingModuleTypeInfo;
        this.workspace = workspace;
        
        // Add mouse listener
        addMouseListener(new MouseHandler());
        
        setToolTipText("AdvancedCALEditor");
    }
    
    /** Retrieves tooltip text from the metadata of the identifier pointed to. */ 
    @Override
    public String getToolTipText(MouseEvent e) {

        // Get identifier and check that we have a tooltipable one
        
        CodeAnalyser.AnalysedIdentifier identifier = getIdentifierAtPoint(e.getPoint());
        if (identifier == null) {
            // Not pointing to an identifier
            return null;
        }
        if ((!identifierIsArgument(identifier)) &&
            (!identifier.getQualificationType().isResolvedTopLevelSymbol()) &&
            (!identifierIsLocalVariable(identifier)) &&
            (identifier.getQualificationType() != CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedAmbiguousTopLevelSymbol)) {
            
            // Not argument, not successfully qualified, and not ambiguity, don't display tip
            return null;
        }
        
        if (identifierIsLocalVariable(identifier)) {
            
            // Find definition of this local variable
            
            CodeAnalyser.AnalysedIdentifier definitionIdentifier;
            if (identifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION) {
                definitionIdentifier = identifier;
            } else {
                definitionIdentifier = identifier.getDefinitionIdentifier();
            }
            
            // Now find the code expression of the definition
            
            String expression = "";
            try {
                SourceRange definitionIdentifierOffsetRange = definitionIdentifier.getOffsetRange();
                expression = getExpressionFrom(definitionIdentifierOffsetRange.getStartLine(), definitionIdentifierOffsetRange.getStartColumn());
            } catch (BadLocationException ex) {
                throw new IllegalStateException("Expression gathering tried to go past end of buffer.");
            }
            return "<html><body>" + 
                   ToolTipHelpers.wrapTextToHTMLLines(GemCutter.getResourceString("CEP_LocalVariable") + ": " + expression + " ..", this) +
                   "</body></html>";
        }
        
        if (identifierIsArgument(identifier)) {
            return GemCutter.getResourceString("CEP_Argument");
        }
        
        if (identifier.getQualificationType() == CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedAmbiguousTopLevelSymbol) {
            return GemCutter.getResourceString("CEP_Ambiguity");
        }

        // Properly qualified top level identifier; get its metadata
        return getMetadataToolTipText(identifier.getName(), identifier.getResolvedModuleName(), identifier.getCategory(), workingModuleTypeInfo);
    }
    
    /** 
     * Retrieve formatted metadata tooltip for the specified qualification.
     * 
     * @param unqualifiedName unqualified name of identifier
     * @param moduleName identifier module name. Can be null.
     * @param type identifier type
     * @param workingModuleTypeInfo type info for the current module.
     * @return metadata tooltip text
     */
    public String getMetadataToolTipText(String unqualifiedName, ModuleName moduleName, SourceIdentifier.Category type, 
                                         ModuleTypeInfo workingModuleTypeInfo) {
        
        return getMetadataToolTipText(unqualifiedName, moduleName, type, workingModuleTypeInfo, workspace, this);
    }
    
    /** 
     * Retrieve formatted metadata tooltip for the specified qualification.
     * 
     * @param unqualifiedName unqualified name of identifier
     * @param moduleName identifier module name. Can be null.
     * @param type identifier type
     * @param workingModuleTypeInfo type info for the current module.
     * @param workspace the related workspace.
     * @param parent component displaying the tooltip
     * @return metadata tooltip text
     */
    public static String getMetadataToolTipText(String unqualifiedName, ModuleName moduleName, SourceIdentifier.Category type, 
                                                ModuleTypeInfo workingModuleTypeInfo, CALWorkspace workspace, JComponent parent) {
        
        if (moduleName == null) {
            // Entity could not be found
            return GemCutter.getResourceString("CEP_UnknownSymbol");
        }
        
        QualifiedName qualifiedName = QualifiedName.make(moduleName, unqualifiedName);
        
        ScopedEntity entity = CodeAnalyser.getVisibleModuleEntity(qualifiedName, type, workingModuleTypeInfo);
        if (entity != null) {
            ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(workingModuleTypeInfo);
            return ToolTipHelpers.getEntityToolTip(entity, namingPolicy, workspace, parent);

        } else {
            // Entity could not be found
            return GemCutter.getResourceString("CEP_UnknownSymbol");
        }
    }
    
    /**
     * @return the tab size of used in this document
     */
    public int getTabSize() {
        return ((Integer) this.getDocument().getProperty(PlainDocument.tabSizeAttribute)).intValue();
    }
    
    /**
     * Retrieves the identifier that the mouse is pointing to.
     * 
     * @param p location of identifier
     * @return source identifier that the point refers to; null if none
     */
    CodeAnalyser.AnalysedIdentifier getIdentifierAtPoint(Point p) {
        try {
            
            int textOffset = getUI().viewToModel(this, p);
            
            // See how closely the returned text offset actually matches the cursor position.
            // If the mouse hovers anywhere to the right of the last character on a line, then
            // the returned offset will be the offset of that character. This is because that
            // character is closest to the mouse position.
            Rectangle offsetRect = getUI().modelToView(this, textOffset);
            
            // The offset rectangle is very small, we need to make it a 
            // little bigger so that it is not to difficult to get a tooltip.
            offsetRect.x -= 5;
            offsetRect.y -= 5;
            offsetRect.width += 10;
            offsetRect.height += 10;
            
            if (!offsetRect.contains(p)) {
                // If the mouse is just hovering on empty space to the right
                // of a line, then don't show a tooltip.
                return null;
            }
            
            return getIdentifierAtPosition(textOffset);
        } catch (BadLocationException ex) {
            throw new IllegalStateException("bad location converting point to identifier");
        }
    }
    
    /** 
     * @param textOffset text offset of the item
     * @return analysed identifier that the offset refers to, or null if none is found.
     */
    public CodeAnalyser.AnalysedIdentifier getIdentifierAtPosition(int textOffset) {
        if (analysedIdentifiers == null) {
            // Code was not parsed properly
            return null;
        }
        
        try {
            
            // Try and see if we can resolve an identifier using the token we are hovering over.
            
            int indexLine = getDocument().getRootElements()[0].getElementIndex(textOffset);
            Element lineElement = getDocument().getRootElements()[0].getElement(indexLine);
            
            if (lineElement == null) {
                return null;
            }
            
            // Find column where mouse is pointing
            int mouseColumn = textOffset - lineElement.getStartOffset() + 1;
            
            // Adjust startColumn and endColumn for tabs
            String text = getText(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset());
            int tabSize = getTabSize();
            int actualColumn = 1;
            for (int i = 0; i < mouseColumn; i ++) {
                if (text.charAt(i) != '\t') {
                    actualColumn++;
                } else { 
                    //tabs can consume from 1 to tabSize columns (a tab character moves the column to the next tab stop)
                    int jump = (((actualColumn-1)/tabSize) + 1) * tabSize + 1 - actualColumn;
                    actualColumn += jump;
                }
            }
            mouseColumn = actualColumn - 1;
            
            // Iterate through our known source identifiers, and find the
            // identifier we are pointing to. If the item is not contained
            // in the list, we do not have type information about it and ignore it.
            
            for (final AnalysedIdentifier identifier : analysedIdentifiers) {
                // Check position of identifier name 
                
                // The positions are shifted by 1 because identifier positions 
                // are 1-based, while Document positions are 0-based.
                
                SourceRange identifierOffsetRange = identifier.getOffsetRange();
                int column = identifierOffsetRange.getStartColumn() - 1;

                int identifierEndLine = identifierOffsetRange.getEndLine() - 1;

                if ((identifierEndLine == indexLine) && 
                    (column < mouseColumn) && 
                    (mouseColumn <= column + identifier.getName().length() + 1)) {
                    
                    return identifier;
                }
                
                // Check position of identifier module name
                // This is done separately because an identifier name
                // may be separated from its module name (eg: "Prelude    .    not")
                
                if (identifier.hasRawModuleSourceRange()) {
                
                    SourceRange identifierOffsetModuleNameRange = identifier.getOffsetModuleNameRange();
                    
                    int startLine = identifierOffsetModuleNameRange.getStartLine() - 1;
                    int startColumn = identifierOffsetModuleNameRange.getStartColumn() - 1;
                    int endLine = identifierOffsetModuleNameRange.getEndLine() - 1;
                    int endColumn = identifierOffsetModuleNameRange.getEndColumn() - 1;
                    
                    if ((startLine <= indexLine) && (indexLine <= endLine) && 
                        (startColumn < mouseColumn) && (mouseColumn <= endColumn)) { 
                        
                        return identifier;
                    }
                }
            }
            
            // None of the identifiers matched our position
            return null;
            
        } catch (BadLocationException ex) {
            throw new IllegalStateException("bad location converting text offset to identifier");
        }
    }
    
    /** Indicates whether the identifier is an argument */
    private boolean identifierIsArgument(CodeAnalyser.AnalysedIdentifier identifier) {
        return identifier.getQualificationType() == CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedArgument;
    }
    
    /** Indicates whether the identifier is a local variable */
    private boolean identifierIsLocalVariable(CodeAnalyser.AnalysedIdentifier identifier) {
        return identifier.getQualificationType() == CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedLocalVariable;
    }
    
    /**
     * Sets the source identifiers 
     * @param analysedIdentifiers
     */
    public void setSourceIdentifiers(List<AnalysedIdentifier> analysedIdentifiers) {
        this.analysedIdentifiers = analysedIdentifiers;
    }
    
    /** 
     * Sets the module type info used
     * @param moduleTypeInfo
     */
    public void setModuleTypeInfo(ModuleTypeInfo moduleTypeInfo) {
        this.workingModuleTypeInfo = moduleTypeInfo;
    }
    
    /**
     * Adds an indicator for an ambiguous identifier
     * @param identifier
     */
    private void addAmbiguityIndicator(CodeAnalyser.AnalysedIdentifier identifier) {
        
        if (identifier.getQualificationType() != CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedAmbiguousTopLevelSymbol) {
            throw new IllegalArgumentException();
        }
        
        AmbiguityOffset offset = getAmbiguityOffset(identifier);
        if (offset == null) {
            return;
        }
        
        ambiguityOffsets.add(offset);
        
        try {
            Highlighter highlighter = getHighlighter();
            highlighter.addHighlight(offset.getStartOffset(), offset.getEndOffset(), new AmbiguityUnderlineHighlightPainter());
            
        } catch (BadLocationException ex) {
            throw new IllegalStateException("bad location adding highlight");
        }
    }
    
    /**
     * Clears all ambiguity indicators.
     */
    private void clearAmbiguityIndicators() {
        
        Highlighter highlighter = getHighlighter();
        Highlight[] highlights = highlighter.getHighlights();
        
        for (final Highlight highlight : highlights) {
            if (highlight.getPainter() instanceof AmbiguityUnderlineHighlightPainter) {
                highlighter.removeHighlight(highlight);
            }
        }

        ambiguityOffsets.clear();
    }    
    
    /**
     * Clears ambiguity identifiers, then repopulates the
     * list of ambiguities from the analyzed identifiers.
     */
    public void updateAmbiguityIndicators() {

        clearAmbiguityIndicators();
        
        if (analysedIdentifiers == null) {
            return;
        }
        
        for (final AnalysedIdentifier identifier : analysedIdentifiers) {
            if (identifier.getQualificationType() == CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedAmbiguousTopLevelSymbol) {
                addAmbiguityIndicator(identifier);
            }
        }
    }

    /**
     * Converts the source position of the identifier into a text offset, length, and line number.
     * @param identifier the analysed identifie rto convert the source position for.
     */
    private AmbiguityOffset getAmbiguityOffset(CodeAnalyser.AnalysedIdentifier identifier) {

        try {

            SourceRange identifierOffsetRange = identifier.getOffsetRange();
            EditorLocation offset = getEditorTokenOffset(identifierOffsetRange.getStartLine(), identifierOffsetRange.getStartColumn(), identifier.getName().length());
            return new AmbiguityOffset(identifier, offset);
            
        } catch (BadLocationException ex) {
            throw new IllegalArgumentException("invalid location trying to convert ambiguous identifier source position");
        }
    }
    
    /**
     * Retrieves the list of ambiguities highlighted in the editor
     * @return List of ambiguity offsets
     */
    public List<AmbiguityOffset> getAmbiguityOffsets() {
        return ambiguityOffsets;
    }
    
    /**
     * Returns list of identifiers contained by the 
     * specified offsets of editor selection. 
     * 
     * Note: identifiers are not adjusted to match the selection offsets
     * 
     * @param offsetStart
     * @param offsetEnd
     * @return identifiers within the selected text portion
     */
    public List<AnalysedIdentifier> getSelectedIdentifiers(int offsetStart, int offsetEnd) {
        List<AnalysedIdentifier> containedIdentifiers = new ArrayList<AnalysedIdentifier>(); 
        if (analysedIdentifiers == null) {
            return containedIdentifiers;
        }
        try {
            
            for (final AnalysedIdentifier identifier : analysedIdentifiers) {
                
                SourceRange identifierOffsetRange = identifier.getOffsetRange();
                int identifierStartOffset = convertPositionToEditorOffset(identifierOffsetRange.getStartLine(), identifierOffsetRange.getStartColumn());
                int identifierEndOffset = identifierStartOffset + identifier.getName().length();
                if (identifierStartOffset >= offsetStart && identifierEndOffset <= offsetEnd) {
                    containedIdentifiers.add(identifier); 
                }
            }
            
        } catch (BadLocationException ex) {
            throw new IllegalArgumentException("invalid location trying to find identifiers in selection");
        }
        return containedIdentifiers;
    }
    
    /**
     * Returns the fully qualified text which can be produced from the specified
     * editor text.
     * 
     * @param offsetStart
     * @param offsetEnd
     * @param codeAnalyser
     * @return fully qualified text (including local symbols)
     */
    public String getQualifiedCodeText(int offsetStart, int offsetEnd, CodeAnalyser codeAnalyser) {
        
        // Select the text from the start of the buffer to the selection end; this is so
        // that the code analyser can match identifier positions within code.
        
        String codeText;
        try {
            codeText = getDocument().getText(0, offsetEnd);
        } catch (BadLocationException e) {
            throw new IllegalArgumentException("invalid location trying to find identifiers in selection");
        }
        
        // Qualify the code 
        
        List<AnalysedIdentifier> identifiers = getSelectedIdentifiers(offsetStart, offsetEnd);
        String qualifiedFullText = codeAnalyser.replaceUnqualifiedSymbols(codeText, identifiers, true);
        
        // Now Strip away the beginning, since we analyzed too much of the code text
        
        return qualifiedFullText.substring(offsetStart);
    }
    
    /**
     * Converts a code position into an editor buffer offset
     * Note: This method assumes the position is valid (ie: it does
     * not occur on a nonexistent line, and the column number exists
     * on the specified line)
     * 
     * @param lineNumber identifier code line
     * @param columnNumber identifier code column
     * @return editor buffter offset corresponding to the specified position
     */
    private int convertPositionToEditorOffset(int lineNumber, int columnNumber) throws BadLocationException  {
        lineNumber--;
        Element lineElement = getDocument().getRootElements()[0].getElement(lineNumber);
        int offset = columnNumber;
        
        // Adjust offset for tabs in this line
        String wholeLine = getText(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset());
        int currentColumnPos = 1;
        int pos = -1;
        int newOffset = offset;
        int tabSize = getTabSize();
        while (++pos < wholeLine.length() && currentColumnPos < offset) {  
            if (wholeLine.charAt(pos) != '\t') {
                currentColumnPos++;
            } else { 
                //tabs can consume from 1 to tabSize columns (a tab character moves the column to the next tab stop)
                int jump = (((currentColumnPos-1)/tabSize) + 1) * tabSize + 1 - currentColumnPos;
                newOffset = newOffset - jump + 1;
                currentColumnPos += jump;
            }                                                      
        }
        offset = newOffset + lineElement.getStartOffset();

        // Shift offset left by 1, since it was a column offset; now it will be treated
        // as buffer offset
        offset--;
        
        return offset;
    }
    
    /**
     * Retrieves a code expression starting from the specified position.
     *
     * This method gathers characters following the position until either a ";", or an unopened
     * close bracket ")" is reached. Contents of parentheses, string literals or comments are ignored.
     *
     * @param codeLine
     * @param codeColumn 
     */
    private String getExpressionFrom(int codeLine, int codeColumn) throws BadLocationException {
        int start = convertPositionToEditorOffset(codeLine, codeColumn);
        int end = getText().length();
        
        String buffer = "";
        
        int i = start;
        char c;
        
        int bracketCount = 0;
        boolean inBigComment = false; /* .. */
        boolean inLineComment = false; // ..
        boolean inPreComment = false; //   "/"
        boolean inPreEndBigComment = false; //   "*"
        boolean inString = false; // "...
        boolean inStringSpecial = false; // " \...
        boolean inCharString = false; // '..
        
        boolean keepGoing = true;
        while (keepGoing && (i<end-1)) {
            c = getText(i, 1).charAt(0);
            
            if (inLineComment) {
                switch (c) {
                    case '\n':
                    {
                        inLineComment = false;
                        break;
                    }
                }
                
            } else if (inBigComment) {
                
                switch (c) {
                    case '*':
                    {
                        inPreEndBigComment = true;
                        break;
                    }
                    case '/':
                    {
                        if (inPreEndBigComment) {
                            inPreEndBigComment = false;
                            inBigComment = false;
                        }
                    }
                    default:
                    {
                        inPreEndBigComment = false;
                    }
                }
                
            } else if (inPreComment) {
                
                switch (c) {
                    case '*':
                    {
                        inBigComment = true;
                        break;
                    }
                    case '/':
                    {
                        inLineComment = true;
                        break;
                    }
                }
                inPreComment = false;
                
            } else if (inString) {
                
                if (!inStringSpecial) {
                    switch (c) {
                        case '\\':
                        {
                            inStringSpecial = true;
                            break;
                        }
                        case '"':
                        {
                            inString = false;
                            break;
                        }
                    }
                } else {
                    inStringSpecial = false;
                }
                
            } else if (inCharString) {
                if (!inStringSpecial) {
                    switch (c) {
                        case '\\':
                        {
                            inStringSpecial = true;
                            break;
                        }
                        case '\'':
                        {
                            inCharString = false;
                            break;
                        }
                    }
                } else {
                    inStringSpecial = false;
                }
            } else {
            
                switch (c) {
                    case '(':
                    {
                        bracketCount++;
                        break;
                    }
                        
                    case ')':
                    {
                        if (bracketCount == 0) {
                            keepGoing = false;
                        }
                        bracketCount--;
                        break;
                    }
                    
                    case '"':
                    {
                        inString = true;
                        break;
                    }
                    
                    case '/':
                    {
                        inPreComment = true;
                        break;
                    }
                    case ';':
                    {
                        if (bracketCount == 0) {
                            keepGoing = false;
                        }
                        break;
                    }
                    case '\'':
                    {
                        inCharString = true;
                        break;
                    }
                }
            }
            
            if (keepGoing) {
                buffer += c;
            }
            i++;
        }
        
        return buffer;
    }
    
    /** 
     * Given a code line and column, calculates the position  of a code token
     * within the editor text buffer.
     * 
     * @param codeLine
     * @param codeColumn
     * @param length length of text to select (-1 will auto-detect end bound)
     * @return buffer location of the selected token
     * @throws BadLocationException if supplied location is invalid
     */
    public EditorLocation getEditorTokenOffset(int codeLine, int codeColumn, int length) throws BadLocationException {

        int lineNumber = -1;
        int start = -1;
        int end = -1;
        
        lineNumber = codeLine;
        
        Element rootElement = getDocument().getRootElements()[0];
        Element lineElement = null;
        int numberOfLines = rootElement.getElementCount();
        int offset = -1;
        
        if (lineNumber > numberOfLines) {

            // Before we pass the source to the CodeGem to be type checked, we add a \n 
            // at the bottom to make sure the parsing is done correctly. So, if the error
            // occurs on that new line, it really means the error is at the end of the 
            // last line of the actual document. Here we check for that and correct for it.
            lineNumber -= 2;
            
            if (lineNumber == numberOfLines) {
                
                // When the CodeGem type checks the source it adds a semicolon and
                // additional newline at the bottom. If the user starts a multi-line comment,
                // but doesn't terminate it, then the compiler will include the terminating
                // semicolon in the line count. That means in the case of an unterminated
                // comment, it can happen that we need to decrease the line count by one more.
                lineNumber--;
            }

            lineElement = rootElement.getElement(lineNumber);
            offset = lineElement.getEndOffset() - lineElement.getStartOffset() - 1;
            
        } else {
            offset = convertPositionToEditorOffset(lineNumber, codeColumn);
            lineNumber--;
            lineElement = rootElement.getElement(lineNumber);
            offset -= lineElement.getStartOffset();
        }

        String wholeLine = getText(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset());
        if (offset == wholeLine.length()) {
            offset--;
        }
        
        if (length == -1) {
            // Get the starting index of the token. Add 1 to it since we don't want to
            // include the actual space or newline in the highlight.
            start = Math.max(wholeLine.lastIndexOf(" ", offset), wholeLine.lastIndexOf("\t", offset)) + 1;
            if (start == 0) {
                start = wholeLine.lastIndexOf("\n", offset) + 1;
            }
            
            // Now find the end index of the token.
        
            int endSpaceIndex = wholeLine.indexOf(" ",  offset);
            int endTabIndex   = wholeLine.indexOf("\t", offset);
            if (endSpaceIndex != -1) {
                end = (endTabIndex != -1 ? Math.min(endSpaceIndex, endTabIndex) : endSpaceIndex);
            } else {
                end = endTabIndex;
            }
            
            if (end == -1) {
                
                end = wholeLine.indexOf("\n", offset);
                
                if (end == -1) {
                    end = wholeLine.length() - 1;
                }
            }
        } else {
            
            start = offset;
            
            int eolIndex = wholeLine.indexOf("\n", offset);
            if (eolIndex == -1) {
                eolIndex = wholeLine.length() - 1;
            }
            end = start + length;
            if (end > eolIndex) {
                end = eolIndex;
            }
        }

        // Since we add 1 to the start position, it can happen that start > end,
        // if the error occurs on a space at the very end of the line. Check for that here.
        if (start == end + 1) {
            start--;
        }

        // Add the line offset to the positions.
        start += lineElement.getStartOffset();            
        end += lineElement.getStartOffset();
        
        return new EditorLocation(start, end, lineNumber);
    }
    
    /** 
     * Set the provider for popup menus 
     * @param popupProvider
     */
    public void setPopupMenuProvider(IdentifierPopupMenuProvider popupProvider) {
        this.popupMenuProvider = popupProvider;
    }
    
    /**
     * @return provider for popup menus
     */
    public IdentifierPopupMenuProvider getPopupMenuProvider() {
        return popupMenuProvider;
    }
    
    
    /**
     * Causes the editor to switch interaction mode to rename the specified identifier.
     * @param identifier identifier to rename
     */
    public void enterRenameMode(CodeAnalyser.AnalysedIdentifier identifier, SymbolRenamerListener renameListener) {
        if (identifier == null) {
            throw new NullPointerException();
        }
        
        if ((identifier.getCategory() != SourceIdentifier.Category.LOCAL_VARIABLE) && (identifier.getCategory() != SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION)) {
            throw new UnsupportedOperationException();
        }
        
        SymbolRenamer renamer = new SymbolRenamer(identifier, renameListener);
        renamer.start();
    }
    
    /**
     * Create highlighters for a local variable reference or definition.
     * 
     * If the uniformHighlightAllRelated flag is True, then all references and definition of this variable
     * are highlighted with the same color. Otherwise, if the identifier is a reference then different colors
     * are used for highlighting the definition and references.  
     *  
     * @param identifier local variable to highlight
     * @return Pair of highlighters for references and definitions respectively
     */
    public Pair<SymbolHighlighter, SymbolHighlighter> createLocalVariableHighlighters(CodeAnalyser.AnalysedIdentifier identifier) {
        
        List<AnalysedIdentifier> variableReferences = findRelatedIdentifiers(identifier, true);
        
        // The list will contain references and a definition; extract the definition and highlight in a
        // different color
        CodeAnalyser.AnalysedIdentifier variableDefinition = null;
        for (int i = 0, n = variableReferences.size(); i < n; i++) {
            CodeAnalyser.AnalysedIdentifier var = variableReferences.get(i);
            if (var.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION) {
                variableDefinition = var;
                variableReferences.remove(i);
                break;
            }
        }
        if (variableDefinition == null) {
            throw new IllegalStateException("AdvancedCALEditor: Local variable does not have definition");
        }
        List<AnalysedIdentifier> variableDefinitions = new ArrayList<AnalysedIdentifier>();
        variableDefinitions.add(variableDefinition);
        
        SymbolHighlighter referenceHighlighter = new SymbolHighlighter(variableReferences);
        SymbolHighlighter definitionHighlighter = new SymbolHighlighter(variableDefinitions);
        definitionHighlighter.setHighlightColor(new Color(255,200,70,170));
            
        return new Pair<SymbolHighlighter, SymbolHighlighter>(referenceHighlighter, definitionHighlighter);
    }
    
    /**
     * Produce a list of identifiers which relate to the specified identifier (ie: these identifiers
     * either define or are defined by the identifier) 
     * 
     * If the selectAllRelated flag is True, then all references and definition of this identifier
     * are listed. Otherwise, if the identifier is a reference then only the definition is listed
     * if the identifier is a definition, only its references are listed.
     * 
     * Limitation: Currently this method only handles local variable identifiers
     */
    private List<AnalysedIdentifier> findRelatedIdentifiers(AnalysedIdentifier identifier, boolean selectAllRelated) {

        List<AnalysedIdentifier> selectedIdentifiers = new ArrayList<AnalysedIdentifier>();
        if (identifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE) {
            
            // This is a local variable, retrieve its definition
            
            AnalysedIdentifier definitionIdentifier = identifier.getDefinitionIdentifier();
            
            if (!selectAllRelated) {
                // Highlight just its definition
                selectedIdentifiers.add(definitionIdentifier);
                
            } else {
                
                // Find the definition and all references to this definition and highlight
                for (final AnalysedIdentifier codeIdentifier : analysedIdentifiers) {
                    if ((codeIdentifier == definitionIdentifier) ||
                        (codeIdentifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE &&
                         codeIdentifier.getDefinitionIdentifier() == definitionIdentifier)) {
                         
                        selectedIdentifiers.add(codeIdentifier);
                    }
                }
            }
            
        } else if (identifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION) {
            // This is a variable definition; highlight its references
            
            for (final AnalysedIdentifier codeIdentifier : analysedIdentifiers) {
                if ( (selectAllRelated && codeIdentifier == identifier) ||
                     (codeIdentifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE &&
                      codeIdentifier.getDefinitionIdentifier() == identifier)) {
                  
                    selectedIdentifiers.add(codeIdentifier);
                }
            }
            
        } else {
            
           throw new UnsupportedOperationException("Identifier category not supported");
        }
        
        return selectedIdentifiers;
    }
}
