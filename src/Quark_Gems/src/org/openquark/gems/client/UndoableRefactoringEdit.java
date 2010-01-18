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
 * UndoableRefactoringEdit.java
 * Creation date: (Apr 19, 2006)
 * By: James Wright
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.Refactorer;


/**
 * An undoable edit used to undo/redo a simple refactoring operation.
 * 
 * @author James Wright
 */
public class UndoableRefactoringEdit extends AbstractUndoableEdit {
    
    private static final long serialVersionUID = -6224660924882392227L;

    private final GemCutter gemcutter;
    
    private final Refactorer refactorer;
    
    private final String presentationName;
    
    public UndoableRefactoringEdit(GemCutter gemcutter, Refactorer refactorer, String presentationName) {
        if(gemcutter == null || refactorer == null || presentationName == null) {
            throw new NullPointerException();
        }
        
        this.gemcutter = gemcutter;
        this.refactorer = refactorer;
        this.presentationName = presentationName;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getPresentationName() {
        return presentationName;
    }

    @Override
    public void redo() throws CannotRedoException  {        
        super.redo();
        
        Thread redoThread = new AbstractThreadWithSimpleModalProgressDialog("refactoring redo thread", gemcutter, GemCutterMessages.getString("RedoingTitle", presentationName), 0, 2) {
            @Override
            public void run() {
                try {
                    showMonitor();
                    setStatus(GemCutterMessages.getString("ReapplyingModificationsStatus"));
                    
                    CompilerMessageLogger messageLogger = new MessageLogger();
                    refactorer.apply(messageLogger);
                    
                    if(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        gemcutter.showCompilationErrors(messageLogger, GemCutterMessages.getString("ErrorWhileApplyingModifications"));
                        return;
                    }

                    incrementProgress();
                    setStatus(GemCutterMessages.getString("RecompilingStatus"));
                    
                    gemcutter.recompileWorkspace(true);
                    
                    if(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        gemcutter.showCompilationErrors(messageLogger, GemCutterMessages.getString("ErrorWhileRecompiling"));
                        return;
                    }

                    incrementProgress();
                    setStatus(GemCutterMessages.getString("SuccessStatus"));
                    
                    try {
                        sleep(750);
                    } catch(InterruptedException e) {
                        // who cares, really
                    }
                    
                } finally {
                    closeMonitor();
                }
            }
        };
        redoThread.start();
    }

    @Override
    public void undo() throws CannotRedoException  {        
        super.undo();
        
        Thread undoThread = new AbstractThreadWithSimpleModalProgressDialog("refactoring undo thread", gemcutter, GemCutterMessages.getString("UndoingTitle", presentationName), 0, 2) {
            @Override
            public void run() {
                try {
                    showMonitor();
                    setStatus(GemCutterMessages.getString("UndoingModificationsStatus"));
                    
                    CompilerMessageLogger messageLogger = new MessageLogger();
                    refactorer.undo(messageLogger);

                    if(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        gemcutter.showCompilationErrors(messageLogger, GemCutterMessages.getString("ErrorWhileApplyingModifications"));
                        return;
                    }

                    incrementProgress();
                    setStatus(GemCutterMessages.getString("RecompilingStatus"));
                    
                    gemcutter.recompileWorkspace(true);
                    
                    if(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        gemcutter.showCompilationErrors(messageLogger, GemCutterMessages.getString("ErrorWhileRecompiling"));
                        return;
                    }

                    incrementProgress();
                    setStatus(GemCutterMessages.getString("SuccessStatus"));
                    
                    try {
                        sleep(750);
                    } catch(InterruptedException e) {
                        // who cares, really
                    }
                    
                } finally {
                    closeMonitor();
                }
            }
        };
        undoThread.start();
    }
}
