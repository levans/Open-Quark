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
 * ExtendedUndoableEditSupport.java
 * Creation date: (04/08/2002 12:02:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client.utilities;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 * An extension of UndoableEditSupport with additional features:
 *   1. The ability to rename the edit being built.  If the name is set multiple times, the name given will be the
 *      last name set at the lowest update level.
 *   2. The ability to discard an update.  This is useful if the current edit has no function and it is not
 *      desired to add it to the undo manager.
 * Creation date: (04/08/2002 12:02:00 PM)
 * @author Edward Lam
 */
public class ExtendedUndoableEditSupport extends UndoableEditSupport {
    
    private class RenameableCompoundEdit extends CompoundEdit {

	    private static final long serialVersionUID = -1028206902509268824L;

        /** The update level if/when the name of the compound edit was set. -1 if never set. */
        private int renameUpdateLevel;
        
        /** An edit we can use to get and set presentation names for undoable edits. */
        private AbstractUndoableEdit presentationNamingEdit = null;
    
        /**
         * Constructor for a RenameableCompoundEdit.
         * Creation date: (04/08/2002 12:28:00 PM)
         */
        private RenameableCompoundEdit() {
            super();
            renameUpdateLevel = -1;
        }

        /**
         * Set the name to display for this edit.
         * Creation date: (04/08/2002 12:04:00 PM)
         * @param newName String the new name for this edit.
         */
        private void setPresentationName(final String newName) {

            // check if the update level allows us to set the new name.
            if (renameUpdateLevel < 0 || updateLevel <= renameUpdateLevel) {

                renameUpdateLevel = updateLevel;

                presentationNamingEdit = new AbstractUndoableEdit() {
                    private static final long serialVersionUID = -48210551859313000L;

                    public String getPresentationName() {
                        return newName;
                    }
                };
            }
        }

        /**
         * Get a reasonable name for this edit.
         * Creation date: (04/08/2002 12:04:00 PM)
         * @return the name to return for this edit.
         */
        public String getPresentationName() {
            return (presentationNamingEdit != null) ? 
                    presentationNamingEdit.getPresentationName() : super.getPresentationName();
        }

        /**
         * Get a reasonable redo name for this edit.
         * Creation date: (04/08/2002 3:42:00 PM)
         * @return the redo name to return for this edit.
         */
        public String getRedoPresentationName() {
            return (presentationNamingEdit != null) ? 
                    presentationNamingEdit.getRedoPresentationName() : super.getRedoPresentationName();
        }

        /**
         * Get a reasonable undo name for this edit.
         * Creation date: (04/08/2002 3:41:00 PM)
         * @return the undo name to return for this edit.
         */
        public String getUndoPresentationName() {
            return (presentationNamingEdit != null) ? 
                    presentationNamingEdit.getUndoPresentationName() : super.getUndoPresentationName();
        }

    }

    /**
     * Constructor for an ExtendedUndoableEditSupport.
     * Creation date: (04/08/2002 2:26:00 PM)
     */
    public ExtendedUndoableEditSupport() {
        super();
    }

    /**
     * Constructor for an ExtendedUndoableEditSupport.
     * Creation date: (04/08/2002 2:26:00 PM)
     * @param r Object the "realSource" of edit events
     */
    public ExtendedUndoableEditSupport(Object r) {
        super(r);
    }

    /**
     * Returns a new CompoundEdit.  
     * Subclasses can override to return a differend implementation if desired.
     * Creation date (04/08/2002 12:17:00 PM)
     * @return CompoundEdit a new compound edit.
     * @see UndoableEditSupport#createCompoundEdit
     */
    protected CompoundEdit createCompoundEdit() {
        return new RenameableCompoundEdit();
    }

    /**
     * Set the name of the compound edit initiated with beginUpdate().  
     * This method should be called before endUpdate() is called.
     * Creation date: (04/08/2002 1:04:00 PM)
     * @param newName the new name of the edit being created.
     */
    public void setEditName(String newName){
        if (compoundEdit != null) {
            ((RenameableCompoundEdit)compoundEdit).setPresentationName(newName);
        }
    }

    /**
     * Decrement the update level.  If the update level is 0, do NOT post an event.
     * An example of when you might use this is if you have only posted edits which cancel each other, 
     * and you don't want a useless aggregate edit to show up in the undo manager.
     * Creation date: (04/09/2002 2:47:00 PM)
     */
    public synchronized void endUpdateNoPost() {
        updateLevel--;
        if (updateLevel == 0) {
            compoundEdit.end();
            compoundEdit = null;
        }
    }
}
