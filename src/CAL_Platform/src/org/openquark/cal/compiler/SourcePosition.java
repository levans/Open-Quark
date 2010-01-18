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
 * SourcePosition.java
 * Creation date (Sep 4, 2002).
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.Comparator;

/**
 * Identifies a position within a CAL source stream and (optionally) the source stream itself. Useful for error reporting.
 * This class is intended to be an immutable value class.
 * Creation date (Sep 4, 2002).
 * @author Bo Ilic
 */
public final class SourcePosition {
   
    /**
     * The source line of the source position.
     */
    private final int line;
    /**
     * The source column of the source position.
     */
    private final int column;
    /**
     * The name of the source.
     */
    private final String sourceName;
    
    /**
     * Constructs an instance of this class.
     * @param line the source line of the source position.
     * @param column the source column of the source position.
     * @param sourceName the name of the source.
     */
    SourcePosition (int line, int column, String sourceName) {
        
        if (line == -1 && column == -1) {
            //SourcePosition objects constructed from unpositioned Antlr errors have (line, column) == (-1, -1)
            line = 0;
            column = 0;
        } else if (line < 0 || column < 0) {
            throw new IllegalArgumentException();
        }
                       
        this.line = line;
        this.column = column;
        this.sourceName = sourceName;
    }
    
    /**
     * Constructs an instance of this class.
     * @param line the source line of the source position.
     * @param column the source column of the source position.
     * @param moduleName the name of the source, which is a module.
     */
    SourcePosition(int line, int column, ModuleName moduleName) {
        this(line, column, moduleName.toSourceText());
    }
    
    /**
     * Constructs an instance of this class, with null as the source name.
     * @param line the source line of the source position.
     * @param column the source column of the source position.
     */
    SourcePosition(int line, int column) {
        this(line, column, (String)null);
    }

    /** @return line number within the stream where the source is located. The first line is line 1. Uninitialized is 0.*/
    public int getLine() {
        return line;
    }
    
    /** @return column number within the stream where the source is located. The first column is column 1. Uninitialized is 0.*/
    public int getColumn() {
        return column;
    }
    
    /**
     * Get the name of the source identified by this source position.
     * @return the name of the source, or null if none.
     */
    public String getSourceName() {
        return sourceName;
    }
      
    /**
     * Computes the zero-based index into the sourceText that this SourcePosition specifies.
     * This method is expensive to call since it traverses sourceText, counting newlines.
     * @param sourceText source text for which this SourcePosition object identifies a position.
     *        (this changes when we calculate offsets via previous source positions)
     * @return int zero-based index into the sourceText that this SourcePosition specifies.
     * @throws IllegalStateException if this SourcePosition has an uninitialized line or column
     * @throws IllegalArgumentException if the sourceText is not consistent with this SourcePosition
     *      e.g. doesn't have enough lines.
     */
    public int getPosition(String sourceText) {
        return getPosition(sourceText, 1);
    }
    
    /**
     * Computes the zero-based index into the sourceText that this SourcePosition specifies.
     * This method is expensive to call since it traverses sourceText, counting newlines.
     * @param sourceText source text for which this SourcePosition object identifies a position.
     * @param startColumn indicates the column that the sourceText begins with
     *                     (may not be 1 for the optimized case where we're only scanning from a
     *                     previous known SourcePosition rather than from the start of a block of text)
     * @return int zero-based index into the sourceText that this SourcePosition specifies.
     * @throws IllegalStateException if this SourcePosition has an uninitialized line or column
     * @throws IllegalArgumentException if the sourceText is not consistent with this SourcePosition
     *      e.g. doesn't have enough lines.
     */
    private int getPosition(String sourceText, int startColumn) {
        
        if (line == 0 || column == 0) {
            throw new IllegalStateException();
        }
                
        int currentNewlinePos = -1;
        for (int currentLine = 1; currentLine < line; ++currentLine) {
            
            currentNewlinePos = sourceText.indexOf('\n', currentNewlinePos + 1);
            if (currentNewlinePos == -1) {
                //the sourceText doesn't have enough newlines in it to be consistent with the line number of this SourcePosition
                String msg = "Invalid position: " + toString() + "\nstartColumn: " + startColumn + "\nsourceText length:" + sourceText.length() + "\nsourceText:\n" + sourceText;
                throw new IllegalArgumentException(msg);
            }
        }

        // This will track the position within the string buffer
        int pos = currentNewlinePos;
        int curColumn = 0; 
        if(pos == -1) {
            // Same line as previous position.
            // So the first char of sourceText has position startColumn
            pos = 0;
            curColumn = startColumn;
        }
        
        while(pos < sourceText.length() && curColumn < column) {
            curColumn += columnWidth(curColumn, sourceText.charAt(pos));
            pos++;
        }
        
        //make sure that we can actually index into sourceText at the position returned.
        //(if curColumn < column, then we left the loop above because the pos was too large).
        if (curColumn < column) {
            //the sourceText is not long enough
            String msg = "Invalid position: " + toString() + "\nstartColumn: " + startColumn + "\nsourceText length:" + sourceText.length() + "\nsourceText:\n" + sourceText;
            throw new IllegalArgumentException(msg);
        }
        
        return pos;
    }
     
    /**
     * A more efficient version of getPosition that uses known "(line, column) == position" information
     * to compute a subsequent position.
     * @param sourceText
     * @param priorSourcePosition SourcePosition object that getPosition (sourceText) would return priorGetPosition.  
     * @param priorGetPosition
     * @return int
     */  
    public int getPosition(String sourceText, SourcePosition priorSourcePosition, int priorGetPosition) {
        if (line == 0 || column == 0) {
            throw new IllegalStateException();
        }
        
        int priorLine = priorSourcePosition.getLine();
        int priorColumn = priorSourcePosition.getColumn();
        
        int lineDiff = line - priorLine;        
        if (lineDiff < 0) {
            //the known line and column must occur in the source text prior to this SourcePosition
            throw new IllegalArgumentException();
        } 

        SourcePosition offsetSourcePosition;                
        if (lineDiff == 0) {
            int columnDiff = column - priorColumn;
            if (columnDiff < 0) {
                //the known line and column must occur in the source text prior to this SourcePosition
                throw new IllegalArgumentException();
            }  
            
            offsetSourcePosition = new SourcePosition(1, column);
                      
        } else {
            
            offsetSourcePosition = new SourcePosition(lineDiff + 1, column);
        }

        String offsetSourceText = sourceText.substring(priorGetPosition);       
        
        return priorGetPosition + offsetSourcePosition.getPosition(offsetSourceText, priorColumn);                  
    }                         
    
    /**
     * Calculates an exclusive end position for the offsetText assuming that it starts at this SourcePosition.
     * @param offsetText The text of the range to calculate an end position for
     * @return an exclusive end position for the rangeText assuming that it starts at startPosition
     */
    public SourcePosition offsetPositionByText(String offsetText) {
        
        int endLine = line;
        int endColumn = column;
        
        for(int idx = 0; idx < offsetText.length(); idx++) {
            char currentChar = offsetText.charAt(idx);
            
            if(currentChar == '\n') {
                endLine ++;
                endColumn = 1;
            
            } else {
                endColumn += columnWidth(endColumn, currentChar);
            }
        }
        
        return new SourcePosition(endLine, endColumn, sourceName);
    }    
    
    /**
     * Calculate the width of a character (ie, the number of columns that it consumes)
     * @param charColumnPosition 1-based column position of offsetChar
     * @param offsetChar Character to calculate the width of
     * @return The number of column positions that this character consumes
     */
    public static int columnWidth(int charColumnPosition, char offsetChar) {

        // tab size used for the source code (this affects the line/column to buffer position translation)
        final int sourceTabSize = CALMultiplexedLexer.TAB_SIZE;

        int zeroBasedColumn = charColumnPosition - 1;
        
        // Tab characters advance to the next tab stop, which can take between 1 and sourceTabSize columns
        if(offsetChar == '\t') {
            int gapFromLastTabStop = zeroBasedColumn % sourceTabSize;
            return sourceTabSize - gapFromLastTabStop;
        }
        
        return 1;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return (sourceName != null) ?
            "source: " + sourceName + ": (line " + line + " column " + column + ")" :
            "(line " + line + " column " + column + ")";
    }    
    
    /**
     * Comparator object to order Source Positions by increasing position.
     * 
     * Two positions are considered in increasing order if the first position
     * is on a line prior to the second position; if the positions are on the same
     * line, they are ordered if the first position's column is before the second.
     */
    public  static final Comparator<SourcePosition> compareByPosition = new CompareByPosition();
    private static class CompareByPosition implements Comparator<SourcePosition> {
        
        /** {@inheritDoc} */
        public int compare(SourcePosition sourcePos1, SourcePosition sourcePos2) {

            if ((sourcePos1 == null) || (sourcePos2 == null)) {
                throw new IllegalArgumentException();
            }
            
            int line1 = sourcePos1.getLine();
            int line2 = sourcePos2.getLine();
            
            int compareLines = compareInts (line1, line2);
            if (compareLines != 0) {
                // Different lines; order by lines
                return compareLines;
                
            } else {
                // Positions on same line; order by column
                int column1 = sourcePos1.getColumn();
                int column2 = sourcePos2.getColumn();
            
                return compareInts (column1, column2);
            }
                                
        }
        
        private static int compareInts(int i1, int i2) {
            //note: it is a cute trick to return i1 - i2, but this doesn't work
            //for all ints due to overflow in int arithmetic.                   
            return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
        }
    }
    
    /**
     * Is this position after the given position.
     * @param position the position to compare to
     * @return true if this position is after the given position.
     */
    public boolean isAfter(SourcePosition position){
        if (line > position.line){
            return true;
        }
        if (line < position.line){
            return false;
        }
        
        return column > position.column;
    }
    
    /**
     * Is this position after the given position.
     * @param position the position to compare to
     * @return true if this position is after the given position.
     */
    public boolean isBefore(SourcePosition position){
        if (line < position.line){
            return true;
        }
        if (line > position.line){
            return false;
        }
        
        return column < position.column;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SourcePosition) {
            return equals((SourcePosition)obj);
        } else {
            return false;
        }
    }
    
    /**
     * Returns whether the other source position equals this one.
     * @param other the other source position.
     * @return true if the source positions are equal; false otherwise.
     */
    public boolean equals(final SourcePosition other) {
        return
            other != null
            && line == other.line
            && column == other.column
            && (sourceName == null ? other.sourceName == null : sourceName.equals(other.sourceName));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + line;
        result = 37 * result + column;
        result = 37 * result + (sourceName == null ? 0 : sourceName.hashCode());
        return result;
    }
}
