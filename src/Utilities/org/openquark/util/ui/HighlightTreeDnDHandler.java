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
 * HighlightTreeDnDHandler.java
 * Creation date: Jun 8, 2004.
 * By: Edward Lam
 */
package org.openquark.util.ui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;

import javax.swing.tree.TreePath;


/**
 * An interface for callbacks from the HighlightTree providing info about drag-and-drop.
 * @author Edward Lam
 */
public interface HighlightTreeDnDHandler {
    
    /**
     * Get the tree path (if any) corresponding to a drop at the location of the given drag event.
     * @param dtde the drag event.
     * @return If non-null, the tree path which would accept the drop.
     * If null, the drag corresponds to a drop between two nodes.
     */
    TreePath getPathForDrop(DropTargetDragEvent dtde);
    
    /**
     * @param flavors the flavors in the transferable.
     * @return whether the tree can accept data of one of the given flavors.
     */
    boolean canAcceptDataFlavor(DataFlavor[] flavors);

    /**
     * Returns a DnDConstant to reflect what drop actions could take place given the arguments
     * @param transferFlavors DataFlavor[] - What flavours the drag source can provide
     * @param dropAction int - What action the drag is providing
     * @param path TreePath - A path specifying the tree node that will accept the drop
     * @param location
     * @return int the action(s) that the drag can perform
     */
    int getDropActions(DataFlavor[] transferFlavors, int dropAction, TreePath path, Point location);

    /**
     * Attempts to perform a drop operation on the specified tree path.  If forceDialog is true the
     * the user may be prompted to provide input, otherwise the dialog will only be shown if there is not
     * an obvious way to perform the drop without input.
     * @param transferable
     * @param mousePoint the point of the drop, in the coordinate system of the tree.
     * @param path - A path specifying the tree node that will accept the drop
     * @param forceDialog - Whether the UI dialog should be shown even if not strictly necessary
     * @return boolean Returns whether or not the drop was successful
     */
    boolean doDrop(Transferable transferable, Point mousePoint, TreePath path, boolean forceDialog);

    /**
     * Attempts to perform a drop operation above or below the specified row.
     * This will be called on drop, if getDropAction() is not ACTION_NONE and shouldDropOnNode() is false.
     * @param transferable the relevant transferable.
     * @param dropNodePath the TreePath on which the drop occurred.
     * @param upperHalf whether the drop occurred on the upper half of the last path component.
     * @param onIcon whether the drop occurred on the icon.
     * @return whether or not the drop was successful
     */
    boolean doDrop(Transferable transferable, TreePath dropNodePath, boolean upperHalf, boolean onIcon);
    
}
