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
 * AbstractListTableModel.java
 * Created: ??
 * By: Richard Webster
 */
package org.openquark.gems.client.valueentry;

/**
 * Common base class for list value editor models.
 * @author Richard Webster
 */
public abstract class AbstractListTableModel extends ValueEditorTableModel {
    /**
     * Constructor for AbstractListTableModel.
     */
    public AbstractListTableModel(ValueEditorManager valueEditorManager) {
        super(valueEditorManager);
    }

    /**
     * Returns the number of rows to display in the list editor.
     */
    public abstract int getRowCount();

    /**
     * Adds an item to the list.
     */
    public abstract void addRow();

    /**
     * Moves the specified row one down.
     * Note: Make sure that there is another row under it.
     */
    public abstract void moveRowDown(int row);

    /**
     * Moves the specified row one up.
     * Note: Make sure that there is another row above it.
     */
    public abstract void moveRowUp(int row);

    /**
     * Removes the specified item from the list.
     */
    public abstract void removeRow(int row);
}
