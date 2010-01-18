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
 * VaultRevisionChooser.java
 * Creation date: Dec 22, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Component;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.RevisionHistory;
import org.openquark.cal.services.Vault;



/**
 * This is a dialog which allows the user to choose a revision number among those available for a resource in a given vault.
 * To use, instantiate, then show using show(), which should return the selected revision number, if any.
 * @author Edward Lam
 */
public class VaultRevisionChooser {
    
    private static final String LATEST_STRING = GemCutter.getResourceString("VRCD_Latest");
    
    /** The component which is the parent of the dialog. */
    private final Component parentComponent;
    
    /** The vault containing the source-controlled resources. */
    private final Vault vault;

    /** Whether this is a chooser for a module revision.  If false, this chooser is for a workspace declaration revision. */
    private final boolean isModuleChooser;

    /**
     * Constructor for a VaultRevisionChooser.
     * @param parentComponent the component which is the parent of the dialog, or null if none.
     * @param vault the vault containing the source-controlled resources.
     * @param isModuleChooser Whether this is a chooser for a module revision.  If false, this chooser is for a workspace declaration revision.
     */
    VaultRevisionChooser(Component parentComponent, Vault vault, boolean isModuleChooser) {
        this.parentComponent = parentComponent;
        this.vault = vault;
        this.isModuleChooser = isModuleChooser;
    }
    
    /**
     * Display the dialog.
     * @param resourceName the name of the resource whose revision should be chosen.
     * @return the selected revision, or null if the dialog was cancelled.
     */
    public Integer showDialog(String resourceName) {
        return showDialog(resourceName, -1);
    }
    
    /**
     * Display the dialog.
     * @param resourceName the name of the resource whose revision should be chosen.
     * @param selectedRevision the revision to select when the dialog is displayed, or -1 to select the first item.
     * @return the selected revision, or null if the dialog was cancelled.
     */
    public Integer showDialog(String resourceName, int selectedRevision) {
        
        RevisionHistory revisionHistory = isModuleChooser ? vault.getModuleRevisionHistory(ModuleName.make(resourceName)) : 
                                                            vault.getWorkspaceDeclarationRevisionHistory(resourceName);
        int[] availableRevisions = revisionHistory.getAvailableRevisions();

        // Construct a set of available revisions.
        Set<Integer> availableRevisionSet = new TreeSet<Integer>();
        for (final int revision : availableRevisions) {
            availableRevisionSet.add(Integer.valueOf(revision));
        }
        
        // Convert to an array in descending order of revision number.
        // The "latest" revision option comes first in the list.
        Integer[] availableRevisionArray = availableRevisionSet.toArray(new Integer[availableRevisionSet.size()]);
        
        String[] selectionValues = new String[availableRevisionArray.length + 1];
        selectionValues[0] = LATEST_STRING;
        
        for (int i = 0; i < availableRevisionArray.length; i++) {
            selectionValues[i + 1] = availableRevisionArray[availableRevisionArray.length - i - 1].toString();
        }
        
        // Get the currently selected revision
        int currentlySelectedRevisionIndex = Arrays.asList(selectionValues).indexOf((Integer.valueOf(selectedRevision)).toString());
        Object initialSelectionValue = (currentlySelectedRevisionIndex < 0) ? LATEST_STRING : selectionValues[currentlySelectedRevisionIndex];
        
        String title = GemCutter.getResourceString("VRCD_DialogTitle");
        String message = GemCutter.getResourceString("VRCD_DialogMessage");
        Object selectedRevisionObject = JOptionPane.showInputDialog(parentComponent, message, title, 
                                                                    JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelectionValue);
        
        if (selectedRevisionObject == null) {
            return null;

        } else if (selectedRevisionObject.equals(LATEST_STRING)) {
            return Integer.valueOf(-1);
        
        } else {
            return Integer.valueOf((String)selectedRevisionObject);
        }
        
    }

}
