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
 * TupleValueEditor.java
 * Created: Feb 13, 2002
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.NTupleValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;


/**
 * A TupleValueEditor displays a list (in order) of the ValueEntryPanels representing the elements of the tuple.
 * @author Michael Cheng
 */
public class TupleValueEditor extends TableValueEditor {

    private static final long serialVersionUID = 8431190568858778573L;

    /**
     * This interface defines what drag operations are supported by the
     * <code>TupleValueEditor</code>.  Implementors of this interface will be
     * called when a certain drag operation is initiated by the user.
     */
    public interface TupleValueDragPointHandler extends ValueEditorDragPointHandler {
    /**
     * This method defines the behaviour when the user attempts to drag a portion
     * of a tuple from the <code>TupleValueEditor</code>.  By default, this
     * method is empty and subclasses are encouraged to override this class to
     * specify their own drag handling code.
     * @param dge
     * @param parentEditor
     * @param tupleElementIndex
     * @return boolean
     */
    boolean dragTupleItem(DragGestureEvent dge,
                          TupleValueEditor parentEditor,
                          int tupleElementIndex);
    }

    /**
     * A custom value editor provider for the TupleValueEditor.
     */
    public static class TupleValueEditorProvider extends ValueEditorProvider<TupleValueEditor> {

        public TupleValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof NTupleValueNode && hasSupportedTupleTypes((NTupleValueNode)valueNode, providerSupportInfo);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TupleValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            TupleValueEditor editor = new TupleValueEditor(valueEditorHierarchyManager, null);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TupleValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNodeBuilderHelper valueNodeBuilderHelper,
                                             ValueEditorDragManager dragManager,
                                             ValueNode valueNode) {
            TupleValueEditor editor = new TupleValueEditor(valueEditorHierarchyManager,
                                                      getListTupleDragPointHandler(dragManager));
            editor.setOwnerValueNode(valueNode);
            return editor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean usableForOutput() {
            return true;
        }
        
        /**
         * Checks if the value nodes for the tuple elements are supported by value editors.
         * @param valueNode the tuple value node to check for
         * @return true if all element value nodes are supported
         */
        private boolean hasSupportedTupleTypes(NTupleValueNode valueNode, SupportInfo providerSupportInfo) {

            // Notify the info object that the value node's type is supported..
            providerSupportInfo.markSupported(valueNode.getTypeExpr());
            
            ValueEditorManager valueEditorManager = getValueEditorManager();
            
            for (int i = 0, size = valueNode.getTupleSize(); i < size; i++) {
            
                ValueNode elementValueNode = valueNode.getValueAt(i);
                
                if (!valueEditorManager.isSupportedValueNode(elementValueNode, providerSupportInfo)) {
                    return false;
                }
            }
            
            return true;
        }

        /**
         * A convenient method for casting the drag point handler to the type that is
         * suitable for the <code>TupleValueEditor</code> to use.  If such conversion is not
         * possible, then this method should return <code>null</code>.
         * @param dragManager
         * @return TupleDragPointHandler
         */
        private TupleValueDragPointHandler getListTupleDragPointHandler(ValueEditorDragManager dragManager) {
            ValueEditorDragPointHandler handler = getDragPointHandler(dragManager);
            if (handler instanceof TupleValueDragPointHandler) {
                return (TupleValueDragPointHandler) handler;
            }
            return null;
        }
    }

    /**
     * Listens to drag events from the tuple table header an delegates to the drag point handler
     * to do the actual work.
     */
    private class TupleDragGestureListener implements DragGestureListener {
        
        /**
         * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
         */
        public void dragGestureRecognized(DragGestureEvent dge) {
            if (dge.getTriggerEvent() instanceof MouseEvent) {
                JTableHeader header = getTableHeader();
                TableColumnModel model = header.getColumnModel();

                int xPos = ((MouseEvent)dge.getTriggerEvent()).getX();
                if (dragPointHandler != null) {
                    dragPointHandler.dragTupleItem(dge,
                                                   TupleValueEditor.this,
                                                   model.getColumnIndexAtX(xPos));
                }
            }
        }
    }

    /** The minimum size of this editor. */
    private static final Dimension MIN_SIZE = new Dimension(200, -1);
    
    /** The maximum size of this editor. */
    private static final Dimension MAX_SIZE = new Dimension(600, -1);

    // Allow this value editor to initiate drag events
    private final TupleValueDragPointHandler dragPointHandler;

    /**
     * TupleValueEditor constructor comment.
     * @param valueEditorHierarchyManager
     * @param dragPointHandler
     */
    protected TupleValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager,
                               TupleValueDragPointHandler dragPointHandler) {

        super(valueEditorHierarchyManager);
        this.dragPointHandler = dragPointHandler;
        
        setLayout(new BorderLayout());
        add(tableScrollPane, BorderLayout.CENTER);

        // Attach a listener for drag events
        if (dragPointHandler != null) {
            JTableHeader tableHeader = getTableHeader();
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                tableHeader,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new TupleDragGestureListener());
        }
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {
        
        initSize(MIN_SIZE, MAX_SIZE);
        
        // We only care about maximum width for the initial display.
        // After that we allow the user to resize the editor to any width.
        setMaxResizeDimension(new Dimension(2048, getSize().height));
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#refreshDisplay()
     */
    @Override
    public void refreshDisplay() {
        
        // Reselect the cell that was previously being edited.
        selectCell(getSelectedRow(), getSelectedColumn());
    }

    /**
     * @see org.openquark.gems.client.valueentry.TableValueEditor#createTableModel(org.openquark.cal.valuenode.ValueNode)
     */
    @Override
    protected ValueEditorTableModel createTableModel(ValueNode valueNode) {
        return new TupleTableModel((NTupleValueNode) valueNode, valueEditorHierarchyManager);
    }
    
    /**
     * Get a map from every value node managed by this editor to its least constrained type.
     * @return Map from every value node managed by this editor to its least constrained type.
     */
    private Map<ValueNode, TypeExpr> getValueNodeToUnconstrainedTypeMap() {
        
        Map<ValueNode, TypeExpr> returnMap = new HashMap<ValueNode, TypeExpr>();
        
        // Get the value nodes for the tuple and the items in the tuple.
        NTupleValueNode currentTupleValue = (NTupleValueNode)getValueNode();
        int tupleSize = currentTupleValue.getTupleSize();

        // Populate the map
        
        // Tuple
        TypeExpr unconstrainedTupleType = getContext().getLeastConstrainedTypeExpr();
        if (unconstrainedTupleType.rootTypeVar() != null) {
            // not constrained by the context to be a tuple
            unconstrainedTupleType = TypeExpr.makeTupleType(tupleSize);
        }
        returnMap.put(currentTupleValue, unconstrainedTupleType);
        
        // Tuple elements
        Map<FieldName, TypeExpr> hasFieldsMap = unconstrainedTupleType.rootRecordType().getHasFieldsMap();
        int j = 0;
        for (final TypeExpr unconstrainedTupleElementType : hasFieldsMap.values()) {
            ValueNode currentTupleItem = currentTupleValue.getValueAt(j);
            returnMap.put(currentTupleItem, unconstrainedTupleElementType);
            ++j;
        }

        return returnMap;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void commitChildChanges(ValueNode oldChild, ValueNode newChild) {
                
        // Get the copy of the current value node, type switched if necessary.
        
        NTupleValueNode oldValueNode = (NTupleValueNode)getValueNode();
        NTupleValueNode newValueNode;
        if (!oldChild.getTypeExpr().sameType(newChild.getTypeExpr())) {
            Map<ValueNode, TypeExpr> valueNodeToUnconstrainedTypeMap = getValueNodeToUnconstrainedTypeMap();
            Map<ValueNode, ValueNode>  commitValueMap = valueEditorManager.getValueNodeCommitHelper().getCommitValues(oldChild, newChild, valueNodeToUnconstrainedTypeMap);
            
            newValueNode = (NTupleValueNode)commitValueMap.get(oldValueNode);

        } else {
            newValueNode = (NTupleValueNode)oldValueNode.copyValueNode();
        }        
        
        // Modify the new value node so that the old child is replaced by the new child.
        // Note that the cell editor may now be editing a different column so we have to search for the row that changed
        //   This can happen if one clicks from an editor for one cell to another cell 
        //   (eg. in a list of colours, from a colour value editor for one cell, onto the cell editor for another cell.)

        List<ValueNode> currentChildrenList = oldValueNode.getValue();

        for (int i = 0, listSize = newValueNode.getTupleSize(); i < listSize; i++) {
            if (currentChildrenList.get(i) == oldChild) {
                TypeExpr childType = newValueNode.getValueAt(i).getTypeExpr();
                newValueNode.setValueNodeAt(i, newChild.copyValueNode(childType));
                break;
            }
        }

        // Set the value node
        replaceValueNode(newValueNode, true);
    }
}
