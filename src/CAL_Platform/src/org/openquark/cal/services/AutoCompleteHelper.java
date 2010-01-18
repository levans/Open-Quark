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
 * AutoCompleteHelper.java
 * Created: Mar 26, 2007
 * By: Greg McClement
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.util.Pair;

/**
 * Helper class for performing auto-completions in editors of cal source code.
 * This is currently used by the CAL Eclipse plug-in and the GEM cutter.
 * 
 * @author Greg McClement
 */
public class AutoCompleteHelper{
    /**
     * The container of the string that is being auto-completed
     */
    private final Document doc;

    public interface Document{
        char getChar(int offset);
        String get(int startIndex, int length);
    }

    public AutoCompleteHelper(Document doc){
        this.doc = doc;
    }

    /**
     * Get the last uncompleted word. For example "ab" in "Cal.Core.Prelude.ab". This will return
     * "" for strings such as "Cal.Core.Prelude."
     */
    public String getLastIncompleteIdentifier(int offset){
        int n = offset-1;
        for (; n >= 0; n--) {
            final char c = doc.getChar(n);
            if (!LanguageInfo.isCALVarPart(c)){
                return doc.get(n + 1, offset-n-1);
            }
        }
        // scanned the identifier to the start of the file
        return doc.get(n + 1, offset-n-1);
    }


    /**
     * Starts at the current position and scans until the start of the string or a
     * non-space character is found. Returns the index of the non-whitespace
     * character or -1 if the scan goes to the start of the string.
     */
    private int skipSpaces(int offset){
        while(offset >= 0){
            char c = doc.getChar(offset);
            if (!Character.isWhitespace(c)){
                return offset;
            }
            offset--;
        }
        return offset;
    }

    /**
     * Scans from the current position until the start of a CAL identifier. Returns the index
     * of the first character of the identifier or -1 if the scan goes past the start of the
     * string.
     */
    private int getStartOfIdentifier(int offset){
        while(offset > 0){
            char c = doc.getChar(--offset);
            if (!LanguageInfo.isCALVarPart(c)){
                return offset + 1;
            }
        }
        return offset;
    }

    /**
     * Get the scoping information before the last uncompleted word. For example, this returns
     * "Cal.Core.Prelude" for the string "Cal.Core.Prelude.abs". The return value is an array.
     * The first value is a string representing the scoping ("Cal.Core.Prelude"), then second value
     * is an array that contains the start offset of each part of the hierarchical name. This
     * is helpful for determine what part of the string to replace during auto-completion.
     */
    public Pair<String, List<Integer>> getIdentifierScoping(int offset) {
        // The start position of each component in the scoping part.
        ArrayList<Integer> componentPositions = new ArrayList<Integer>();
        // Skip the last incompleted word
        char c;
        int startOfLastIndentifier = getStartOfIdentifier(offset);
        componentPositions.add(Integer.valueOf(startOfLastIndentifier));

        StringBuilder stringBuilder = new StringBuilder();

        while (true) {
            offset = skipSpaces(startOfLastIndentifier - 1);
            if (offset < 0){
                break;
            }
            c = doc.getChar(offset);
            if (c == '.'){
                final int endOfNextIdentifier = skipSpaces(offset-1);
                if (endOfNextIdentifier < 0){
                    break;
                }
                final int startOfNextIdentifier = getStartOfIdentifier(endOfNextIdentifier);
                if (startOfLastIndentifier < 0){
                    break;
                }
                if (stringBuilder.length() > 0){
                    stringBuilder.insert(0, '.');
                }
                stringBuilder.insert(0, doc.get(startOfNextIdentifier, endOfNextIdentifier - startOfNextIdentifier + 1));
                startOfLastIndentifier = startOfNextIdentifier;
                componentPositions.add(0, Integer.valueOf(startOfLastIndentifier));
            }
            else{
                break;
            }
        }

        return new Pair<String, List<Integer>>(stringBuilder.toString(), componentPositions);
    }
}

