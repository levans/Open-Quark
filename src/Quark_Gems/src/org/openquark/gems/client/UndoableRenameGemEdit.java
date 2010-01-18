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
 * UndoableRenameGemEdit.java
 * Creation date: (09/21/04)
 * By: Peter Cardwell
 */
package org.openquark.gems.client;

import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.services.Status;


/**
 * An undoable edit used to undo the renaming of gems in the workspace.
 * @author Peter Cardwell
 */
public class UndoableRenameGemEdit extends AbstractUndoableEdit {
    
    private static final long serialVersionUID = 1124621509777321845L;
    private final ModuleName moduleName;
    private final String oldName;
    private final String newName;
    private final GemCutter gemCutter;
    private final RenameRefactoringDialog.EntityType entityType;
    
    /**
     * Constructor for an UndoableRenameGemEdit.
     * @param oldName The name of the gem before the event
     * @param newName The name of the gem after the event
     */
    public UndoableRenameGemEdit(String oldName, String newName, ModuleName moduleName, RenameRefactoringDialog.EntityType entityType, GemCutter gemCutter) {
        super();
        this.oldName = oldName;
        this.newName = newName;
        this.moduleName = moduleName;
        this.gemCutter = gemCutter;        
        this.entityType = entityType;
    }
    
    /**
     * A reasonable name for this edit.
     * @return the presentation name for this edit
     */
    @Override
    public String getPresentationName() {
        return GemCutterMessages.getString("UndoText_Rename", oldName);
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() throws CannotRedoException  {        
        super.redo();
        
        RenameRefactoringDialog renameDialog = new RenameRefactoringDialog(gemCutter, gemCutter.getWorkspaceManager(), gemCutter.getPerspective(), (QualifiedName.make(moduleName, oldName)).getQualifiedName(), newName, entityType, true, false);        
        renameDialog.setTitle(GemCutter.getResourceString("UndoText_RedoRenameDialogTitle"));
        
        // Display refactoring dialog and recompile dirty modules if any changes are made
        
        RenameRefactoringDialog.Result renameResult = renameDialog.display();
        
        if(renameResult != null){
            QualifiedName qualifiedFromName = QualifiedName.makeFromCompoundName(renameResult.getFromName());
            QualifiedName qualifiedToName = QualifiedName.makeFromCompoundName(renameResult.getToName());
            
            try {
                GemCutterRenameUpdater designUpdater = new GemCutterRenameUpdater(new Status("GemGraph update status"), gemCutter.getTypeChecker(), qualifiedToName, qualifiedFromName, renameResult.getCategory());
                designUpdater.updateTableTop(gemCutter);
            } catch( GemEntityNotPresentException fanpe) {
                // Couldn't reload the functional agent for one of the gems.               
                JOptionPane.showMessageDialog(gemCutter, "Error reloading functional agent: " + fanpe.getEntityName(), 
                        "Reload error", JOptionPane.WARNING_MESSAGE);
            }
            gemCutter.recompileWorkspace(true);
        } else {
            throw new CannotRedoException();
        }
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() throws CannotUndoException {        
        super.undo();
        
        RenameRefactoringDialog renameDialog = new RenameRefactoringDialog(gemCutter, gemCutter.getWorkspaceManager(), gemCutter.getPerspective(), (QualifiedName.make(moduleName, newName)).getQualifiedName(), oldName, entityType, true, false);        
        renameDialog.setTitle(GemCutter.getResourceString("UndoText_UndoRenameDialogTitle"));
        
        // Display refactoring dialog and recompile dirty modules if any changes are made
        
        RenameRefactoringDialog.Result renameResult = renameDialog.display();
        if(renameResult != null){
            QualifiedName qualifiedFromName = QualifiedName.makeFromCompoundName(renameResult.getFromName());
            QualifiedName qualifiedToName = QualifiedName.makeFromCompoundName(renameResult.getToName());
            
            try {
                GemCutterRenameUpdater designUpdater = new GemCutterRenameUpdater(new Status("GemGraph update status"), gemCutter.getTypeChecker(), qualifiedToName, qualifiedFromName, renameResult.getCategory());
                designUpdater.updateTableTop(gemCutter);
            } catch( GemEntityNotPresentException fanpe) {
                // Couldn't reload the functional agent for one of the gems.               
                JOptionPane.showMessageDialog(gemCutter, "Error reloading functional agent: " + fanpe.getEntityName(), 
                        "Reload error", JOptionPane.WARNING_MESSAGE);
            }
            gemCutter.recompileWorkspace(true);
        } else {
            throw new CannotUndoException();
        }        
    }

}
