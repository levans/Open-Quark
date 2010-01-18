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
 * SourceModifier.java
 * Creation date: (Feb 17, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openquark.util.Pair;

/**
 * Contains a set of SourceModifications to apply or undo to source text.
 *
 * This class is a generalization of the Renamer class, which it replaces.
 * 
 * @author James Wright
 * @author Bo Ilic
 */
final class SourceModifier {

    /** The SourceModifications to apply */
    private final List<SourceModification> sourceModifications = new ArrayList<SourceModification>();
    
    /** The SourceModifications to perform in order to undo */
    private final List<SourceModification> undoModifications = new ArrayList<SourceModification>();
    
    /** 
     * True whenever the sourceModifications field contain potentially unsorted data.
     * 
     * The standard usage pattern is to add a bunch of SourceModifications and then
     * call apply, at which point the list will be sorted.  Afterward, there may be
     * subsequent calls to apply and undo without any new data having been added; in
     * those cases it is unnecessary to sort the list again.  
     */
    private boolean sourceModificationsNeedsSort = false;
    
    SourceModifier() {
    }

    /**
     * @param sourceModification SourceModification to add to the list of modifications
     *                            to apply.
     */
    void addSourceModification(SourceModification sourceModification) {
        sourceModifications.add(sourceModification);
        sourceModificationsNeedsSort = true;
    }
    
    /**
     * Applies a list of SourceModifications to oldSourceText and returns the modified text.
     * The inverseModifications list is updated to contain a list of inverses of the modifications
     * that were performed/
     * This method factors out the common functionality between apply and undo.
     * @param oldSourceText String to modify
     * @param sourceModifications List (SourceModification) of modifications to apply
     * @param inverseModifications List (SourceModification) This list will be updated to contain inverse
     *         modifications of the modifications that were applied.
     * @return Modified text
     */
    private static String performModifications(final String oldSourceText, List<SourceModification> sourceModifications, List<SourceModification> inverseModifications) {
        
        //the oldSourceText is a rough approximation of the size of the newSourceText...
        StringBuilder newSourceText = new StringBuilder (oldSourceText.length());

        SourcePosition lastPositionInOld = new SourcePosition(1, 1);
        int lastIndexInOld = 0;
        int copiedUpToIndexInOld = 0;
        
        SourcePosition lastPositionInNew = new SourcePosition(1, 1);
        int lastIndexInNew = 0;
        
        for (final SourceModification sourceModification : sourceModifications) {
           
            SourcePosition modPositionInOld = sourceModification.getSourcePosition();
        
            int modIndexInOld = modPositionInOld.getPosition(oldSourceText, lastPositionInOld, lastIndexInOld);                                 
            if (modIndexInOld == -1) {
                throw new IllegalArgumentException();
            }
            
            // append the stuff between the previous modification and the current modification from the old source text
            String interimText = oldSourceText.substring(copiedUpToIndexInOld, modIndexInOld);
            newSourceText.append(interimText);
            
            // Append the actual modification
            String newText = sourceModification.getNewText();
            newSourceText.append(newText);
            
            copiedUpToIndexInOld = modIndexInOld + sourceModification.getOldText().length(); 

            lastPositionInOld = modPositionInOld;
            lastIndexInOld = modIndexInOld;
            
            // Record the inverse modification if requested
            if (inverseModifications != null) {
                int modIndexInNew = lastIndexInNew + interimText.length();
                SourcePosition modPositionInNew = lastPositionInNew.offsetPositionByText(interimText);
                
                inverseModifications.add(sourceModification.getInverse(modPositionInNew));
                
                lastIndexInNew = modIndexInNew + newText.length();
                lastPositionInNew = modPositionInNew.offsetPositionByText(newText);
                sourceModification.setNewSourcePosition(modPositionInNew);                    
            }
        }
        
        // Copy the last chunk of text after the final modification
        newSourceText.append(oldSourceText.substring(copiedUpToIndexInOld));
        
        return newSourceText.toString();
    }

    /**
     * Applies a list of SourceModifications to the given module.
     * @param moduleName the name of the module being modified.
     * @param sourceManager contains the source to modify
     * @param sourceModifications List (SourceModification) of modifications to apply
     */
    private static void performModifications(ModuleName moduleName, ModuleContainer.ISourceManager2 sourceManager, List<SourceModification> sourceModifications) {
        // Figure out what the current offsets to the modification positions are. This assumes
        // no overlap of positions in the source modification. 
        final int[] offsets = new int[sourceModifications.size()];
        {
            int i = 0;
            for (final SourceModification sourceModification : sourceModifications) {
                offsets[i] = sourceManager.getOffset(moduleName, sourceModification.getSourcePosition());
                ++i;
            }
        }
        
        // Perform the modifications
        int i = 0;
        // how much the offsets should be shifted due to source changes.
        int shift = 0;
        for (final SourceModification sourceModification : sourceModifications) {
            final int offset = offsets[i++];
            
            int startIndex = offset + shift;
            final String oldText = sourceModification.getOldText();
            final String newText = sourceModification.getNewText();
            sourceManager.saveSource(moduleName, startIndex, startIndex + oldText.length(), newText);
            Pair<Integer, Integer> newPosition = sourceManager.getLineAndColumn(moduleName, startIndex);
            sourceModification.setNewSourcePosition(new SourcePosition(newPosition.fst(), newPosition.snd()));
            shift += newText.length() - oldText.length();
        }
    }
    
    /** 
     * Applies the previously-added SourceModifications to the provided sourceText.
     * 
     * @param sourceText The source text to apply the modifications to
     * @return containing the results of applying the modifications
     */
    String apply(String sourceText) {

        ensureSortedSourceModifications();
        undoModifications.clear();
        return performModifications(sourceText, sourceModifications, undoModifications);
    }

    /** 
     * Applies the previously-added SourceModifications to the provided sourceText.
     * @param moduleName the name of the module being modified.
     * @param sourceManager contains the source to modify
     */
    void apply(ModuleName moduleName, ModuleContainer.ISourceManager2 sourceManager) {
        ensureSortedSourceModifications();
        performModifications(moduleName, sourceManager, sourceModifications);
    }
    
    
    /**
     * Undoes the previously-applied SourceModifications to the provided sourceText.
     * 
     * The modifications must have been previously applied.  An attempt to call
     * undo before apply will result in an IllegalArgumentException.
     * 
     * @param sourceText The source text to apply the undo operation to
     * @return containing the results of undoing the operation
     */
    String undo(String sourceText) {
        
        if(undoModifications.size() != sourceModifications.size()) {
            throw new IllegalArgumentException();
        }
        
        return performModifications(sourceText, undoModifications, null);
    }
    
    /**
     * Remove all the modifications contained by this SourceModifier.
     */
    void clearAllModifications() {
        sourceModifications.clear();
        undoModifications.clear();
        sourceModificationsNeedsSort = false;
    }
    
    /**
     * @return The number of source modifications that this SourceModifier currently
     * contains.
     */
    int getNSourceModifications() {
        return sourceModifications.size();
    }
    
    /** Ensure that the sourceModifications list is sorted by SourcePosition */
    private void ensureSortedSourceModifications() {
        if(!sourceModificationsNeedsSort) {
            return;
        }
        
        Collections.sort(sourceModifications, SourceModification.compareByPosition);
        sourceModificationsNeedsSort = false;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return sourceModifications.toString();
    }
}
