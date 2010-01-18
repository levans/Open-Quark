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
 * SourceRange.java
 * Creation date: (Feb 13, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

/**
 * Identifies a continuous range of positions in a CAL source stream.
 * This is an immutable value class.
 * 
 * @author Jawright
 */
public final class SourceRange {

    private final SourcePosition startSourcePosition;
    private final SourcePosition endSourcePosition;
    
    SourceRange(SourcePosition startPosition, SourcePosition endPosition) {
        if(startPosition == null || endPosition == null) {
            throw new NullPointerException();
        }
        
        if(!equalsObject(startPosition.getSourceName(), endPosition.getSourceName())) {
            throw new IllegalArgumentException();
        }
        
        if(SourcePosition.compareByPosition.compare(startPosition, endPosition) > 0) {
            throw new IllegalArgumentException();
        }
        
        startSourcePosition = startPosition;
        endSourcePosition = endPosition;
    }

    SourceRange(SourcePosition startPosition, String rangeText) {
        
        if(startPosition == null || rangeText == null) {
            throw new NullPointerException();
        }

        startSourcePosition = startPosition;
        endSourcePosition = startPosition.offsetPositionByText(rangeText);
    }
    
    // SourceRange that is just associated with a file.
    SourceRange(String sourceName) {
        startSourcePosition = new SourcePosition(-1, -1, sourceName);
        endSourcePosition = new SourcePosition(-1, -1, sourceName);
    }

    /**
     * Constructor for a SourceRange associated with a module but having no source positions.
     * @param moduleName the name of the module with which the SourceRange is associated
     */
    SourceRange(ModuleName moduleName) {
        this(moduleName.toSourceText());
    }
    
    /**
     * @return true only if both arguments are null, or both are not null, and obj.equals(obj2) is true.
     */
    private static boolean equalsObject(final Object obj1, final Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        }
        
        if (obj2 == null) {
            return false;
        }
        
        return obj1.equals(obj2);
    }
    
    /** @return SourcePosition of the start of the range (inclusive) */
    public SourcePosition getStartSourcePosition() {
        return startSourcePosition;
    }
    
    /** @return SourcePosition of the end of the range (exclusive) */
    public SourcePosition getEndSourcePosition() {
        return endSourcePosition;
    }
    
    /** @return Line number in the stream where the range begins (inclusive) */
    public int getStartLine() {
        return startSourcePosition.getLine();
    }

    /** @return Column number in the stream where the range begins (inclusive) */
    public int getStartColumn() {
        return startSourcePosition.getColumn();
    }
    
    /** @return Line number in the stream where the range ends (exclusive) */
    public int getEndLine() {
        return endSourcePosition.getLine();
    }
    
    /** @return Column number in the stream where the range ends (exclusive) */
    public int getEndColumn() {
        return endSourcePosition.getColumn();
    }
    
    /** @return Optional name of the source stream (this can be null) */
    public String getSourceName() {
        return startSourcePosition.getSourceName();
    }
    
    @Override
    public String toString() {
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("source: ");
        stringBuilder.append(startSourcePosition.getSourceName());
        stringBuilder.append(" (line ");
        stringBuilder.append(startSourcePosition.getLine());
        stringBuilder.append(" column ");
        stringBuilder.append(startSourcePosition.getColumn());
        stringBuilder.append(") to (line ");
        stringBuilder.append(endSourcePosition.getLine());
        stringBuilder.append(" column ");
        stringBuilder.append(endSourcePosition.getColumn());
        stringBuilder.append(")");
        
        return stringBuilder.toString();
    }
    
    /**
     * Returns whether this source range contains the given source position.
     * @param pos the source position to check.
     * @return true if this source range contains the given source position; false otherwise.
     */
    public boolean containsPosition(SourcePosition pos) {
        return 
            SourcePosition.compareByPosition.compare(pos, startSourcePosition) >= 0 &&
            SourcePosition.compareByPosition.compare(pos, endSourcePosition) < 0;
    }
    
    /**
     * Returns whether this source range contains the given source position. Includes both the start
     * and the end column.
     * @param pos the source position to check.
     * @return true if this source range contains the given source position; false otherwise.
     */
    public boolean containsPositionInclusive(SourcePosition pos) {
        return 
            SourcePosition.compareByPosition.compare(pos, startSourcePosition) >= 0 &&
            SourcePosition.compareByPosition.compare(pos, endSourcePosition) <= 0;
    }
    
    /**
     * return true if the sourcerange is contained entirely within this source
     * range.
     * 
     * @param sourceRange
     * @return true if the sourceRange is contained
     */
    public boolean contains(SourceRange sourceRange) {
        return containsPositionInclusive(sourceRange.getStartSourcePosition())
                && containsPositionInclusive(sourceRange.getEndSourcePosition());
    }
    
    /**
     * Returns true if this SourceRange overlaps with the given SourceRange.
     * 
     * @param sourceRange
     *            the source range to check for overlap
     */
    public boolean overlaps(SourceRange sourceRange){
        if (containsPositionInclusive(sourceRange.getStartSourcePosition())){
            return true;
        }
        if (containsPositionInclusive(sourceRange.getEndSourcePosition())){
            return true;
        }
        if (sourceRange.containsPositionInclusive(getStartSourcePosition())){
            return true;
        }
        if (sourceRange.containsPositionInclusive(getEndSourcePosition())){
            return true;
        }
        return false;
    }
 
    /**
     * @param sourceText sourceText that the source range refers to
     * @return the text in the sourceText string covered by this source range.
     */
    public String getSelection(String sourceText){
        final int startIndex = getStartSourcePosition().getPosition(sourceText);
        final int endIndex = getEndSourcePosition().getPosition(sourceText);
        
        return sourceText.substring(startIndex, endIndex);
    }
}
