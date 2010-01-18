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
 * JDBCResultSetTableHeader.java
 * Created: Oct 30, 2003
 * By: Kevin Sit
 */
package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openquark.gems.client.valueentry.JDBCResultSetEditor.JDBCResultSetDragPointHandler;


/**
 * This is the object which manages the header of the table embedded in a
 * <code>JDBCResultSetEditor</code>.  Users cannot reorder columns in this
 * table, only resizing is allowed.  This header also allows the user to drag
 * a column and drop it onto an external component.
 * 
 * This table header class is designed to work with <code>JDBCResultSetEditor</code> only.
 *  
 * @author ksit
 */
public class JDBCResultSetTableHeader extends JTableHeader {

    private static final long serialVersionUID = 8633791832079777878L;

    /**
     * The drag geature listener for the table header.  This listener verifies
     * that the gesture event is originated from a mouse event and register
     * the drag action with the drag source.
     */
    private class JDBCTableHeaderDragGestureListener implements DragGestureListener {

        /* (non-Javadoc)
         * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
         */
        public void dragGestureRecognized(DragGestureEvent dge) {
            
            // if the user is attempting to resize the column, then don't try to
            // start our own drag column event because it will interfere with
            // JTable's internal drag and drop mechanism!                
            if (getResizingColumn() != null) {
                return;
            }

            // no need to continue if the drag event is not triggered by a mouse
            if (!(dge.getTriggerEvent() instanceof MouseEvent)) {
                return;
            }

            // don't need to continue if there is no selected column or the
            // result set is null
            int[] indices = columnModel.getSelectedColumns();
            if (indices.length == 0 || parentEditor.getResultAdapter().getResultSet() == null) {
                return;
            }

            // start the drag now and register listeners for mouse drag motions
            // if the drag was initiated successfully
            dragPointHandler.dragColumns(dge, parentEditor, table.getColumnModel());
        }
    }
    
    /**
     * This mouse listener should be registered with the table in order to revert
     * back to row selection mode when a cell is selected.
     */
    private class JDBCTableMouseListener extends MouseAdapter {
        
        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            changeToRowSelectionMode();            

            // resolve the mouse location to a row index
            int index = table.rowAtPoint(e.getPoint());
            if (index < 0) {
                return;
            }
            table.addRowSelectionInterval(index, index);
        }

    }
    
    /**
     * This mouse listener should be registered with the table header component
     * to pick up the Ctrl+Button and Shift+Button drag and drop gesture events.
     * When this listener is first called, it will register the JDBC table mouse
     * listener with the parent table.  This listener also sets the column
     * selection mode in the table.
     */
    private class JDBCTableHeaderMouseListener extends MouseAdapter {
        
        private boolean selectionEventHandled;
        
        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            // run the common code first
            int colIndex = mouseEventOccured(e);
            if (colIndex < 0) {
                return;
            }
            
            // ignore the mouse-pressed event if the column is already in the current selection.
            // This is necessary because we don't want to lose the selection if the user
            // holds the mouse button down (i.e. starts to drag) without pressing the
            // Ctrl key or the Shift key. 
            selectionEventHandled = false;
            if (!table.isColumnSelected(colIndex)) {
                if (isMultiSelectGesture(e)) {
                    handleMultiSelectMouseGesture(e, colIndex);
                } else {
                    handleSingleSelectMouseGesture(e, colIndex);
                }
                // the flag to the true so that we don't need to handle the selection gesture
                // in the mouse release event
                selectionEventHandled = true;
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            // run the common code first
            int colIndex = mouseEventOccured(e);
            if (colIndex < 0) {
                return;
            }

            // if the selection is already handled at the time when the mouse button is
            // pressed, then we should not handle it again in here
            if (!selectionEventHandled) {
                if (isMultiSelectGesture(e)) {
                    handleMultiSelectMouseGesture(e, colIndex);
                } else {
                    handleSingleSelectMouseGesture(e, colIndex);
                }
            }
        }
        
        /**
         * This method is called whenever there is a mouse event occured.
         * @param e
         * @return int The index of the column that corresponds to the mouse click location.
         */
        private int mouseEventOccured(MouseEvent e) {
            // register a listener in the parent table, so that we can re-enable
            // the row selection mode
            if (tableMouseListener == null) {
                tableMouseListener = new JDBCTableMouseListener();
                table.addMouseListener(tableMouseListener);
            }

            // resolve the mouse location to a column index
            int index = JDBCResultSetTableHeader.this.columnAtPoint(e.getPoint());
            if (index < 0) {
                return -1;
            } else {
                // set up the table to accept column selection, otherwise we cannot
                // highlight selected columns.
                changeToColumnSelectionModel();
                return index;
            }
        }
    }
    
    /**
     * A table header cell render that supports PLAF and it displays the table
     * header value as a tool tip as well.  This is useful for showing the name
     * of a really long string.
     */
    private static class TooltipEnabledCellRenderer extends DefaultTableCellRenderer implements UIResource {
        private static final long serialVersionUID = 5308446012366212919L;

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
            }
            String str = (value == null ? "" : value.toString()); 
            setText(str);
            setToolTipText(str);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return this;
        }
    }

    /**
     * Parent editor that creates this header.
     */
    private final JDBCResultSetEditor parentEditor;
    /**
     * Drag manager associated with this component.
     */
    private final JDBCResultSetDragPointHandler dragPointHandler;
    /**
     * Drag manager associated with this component.
     */
    private MouseListener headerMouseListener;
    /**
     * Drag manager associated with this component.
     */
    private MouseListener tableMouseListener;

    /**
     * Variables used to keep track of selection range indices.  Do NOT modify the values of
     * these two variables!  Use <code>beginRangeSelection</code> or <code>endRangeSelection</code>
     * to manipulate these variables implicitly.
     */
    private int rangeBeginIndex = -1;
    private int rangeEndIndex = -1;

    public JDBCResultSetTableHeader(
            TableColumnModel cm,
            JDBCResultSetEditor parentEditor,
            JDBCResultSetDragPointHandler dragPointHandler) {

        super(cm);
        this.parentEditor = parentEditor;
        this.dragPointHandler = dragPointHandler;
        initializeDragAndDrop();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.JTableHeader#setReorderingAllowed(boolean)
     */
    @Override
    public void setReorderingAllowed(boolean reorderingAllowed) {
        // do nothing: users cannot re-enable the resizing option
    }

    /* (non-Javadoc)
     * @see javax.swing.table.JTableHeader#setResizingColumn(javax.swing.table.TableColumn)
     */
    @Override
    public void setResizingColumn(TableColumn aColumn) {
        super.setResizingColumn(aColumn);
        if (aColumn != null) {
            changeToRowSelectionMode();
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.JTableHeader#createDefaultRenderer()
     */
    @Override
    protected TableCellRenderer createDefaultRenderer() {
        return new TooltipEnabledCellRenderer();
    }

    /**
     * Initializes the drag and drop system to allow dragging this component and dropping
     * it to an external entity.
     */
    private void initializeDragAndDrop() { 
        
        // in order to support drag and drop, we have to disable reordering because JTable's
        // reordering is implemented using a mouse pressed listener, which conflicts with
        // the standard drag and drop API
        super.setReorderingAllowed(false);

        // initialize the component as a drag source
        if (dragPointHandler != null) {
            // we have to use the COPY_OR_MOVE action because the workspace drop
            // target only recognizes the MOVE command, but the default gesture
            // listener links the Ctrl+Button1 gesture to the COPY command
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                this,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new JDBCTableHeaderDragGestureListener());
        }

        if (headerMouseListener == null) {        
            headerMouseListener = new JDBCTableHeaderMouseListener(); 
            addMouseListener(headerMouseListener);
        }
    }
    
    /**
     * Returns true if the given mouse event is a mouse gesture that selects multiple
     * columns from the table.
     * @param e
     * @return boolean
     */
    private boolean isMultiSelectGesture(MouseEvent e) {
        return (e.getModifiers() & (InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)) > 0;
    }

    /**
     * Performs the selection operation on a single column.  Caller is responsible
     * for ensuring the validity of the given column index.
     * @param e
     * @param colIndex
     */    
    private void handleSingleSelectMouseGesture(MouseEvent e, int colIndex) {
        table.clearSelection();
        beginRangeSelection(colIndex);
    }

    /**
     * Performs the selection operation on more than one columns.  Caller is responsible
     * for ensuring the validity of the given column index.
     * @param e
     * @param colIndex
     */    
    private void handleMultiSelectMouseGesture(MouseEvent e, int colIndex) {
        int modifiers = e.getModifiers();
        
        // the Shift key gesture overrides the Ctrl key: even if both Ctrl and Shift keys
        // are pressed, we are still doing a range selection (i.e. multiple ranges).
        if ((modifiers & InputEvent.SHIFT_MASK) > 0) {        
            if (rangeBeginIndex >= 0) {
                // this is a special case: the user had already selected a range, however,
                // the user did not let go of the Shift key and clicked on another column.
                // This action should cause the original selection to be cancelled.
                if (rangeEndIndex >= 0) {
                    table.removeColumnSelectionInterval(rangeBeginIndex, rangeEndIndex);
                }
                endRangeSelection(colIndex);            
            } else {
                // only invoked when this method is first called: since the begin index
                // is not set, then we fall back to single selection mode even if the mouse
                // event is a multiselect gesture
                handleSingleSelectMouseGesture(e, colIndex);
            }
        } else if ((modifiers & InputEvent.CTRL_MASK) > 0) {
            // if the given index already exists in the selection model, then remove it
            // from the selection model.  Otherwise, we assume that the user is trying
            // to start a new range selection.
            if (table.isColumnSelected(colIndex)) {
                table.removeColumnSelectionInterval(colIndex, colIndex);
            } else {
                beginRangeSelection(colIndex);
            }
        }
    }

    /**
     * Marks the beginning of a range selection.  This method attempts to add the
     * column, referenced by the index, to the selection model and updates the
     * range selection indices.
     * @param index
     */    
    private void beginRangeSelection(int index) {
        table.addColumnSelectionInterval(index, index);
        rangeBeginIndex = index;
        rangeEndIndex = -1;
    }
    
    /**
     * Marks the end of a range selection.  This method attempts to add the range
     * of columns to the selection model and updates the range selection indices.
     * @param index
     */
    private void endRangeSelection(int index) {
        if (rangeBeginIndex >= 0) {
            table.addColumnSelectionInterval(rangeBeginIndex, index);
            rangeEndIndex = index;
        }
    }
    
    /**
     * A convenient method for switching the table to row selection mode.  Cannot
     * cannot this method in the constructor.
     */
    private void changeToRowSelectionMode() {
        table.clearSelection();
        table.setRowSelectionAllowed(true);
        table.getColumnModel().setColumnSelectionAllowed(false);
    }
    
    /**
     * A convenient method for switching the table to column selection mode.  Cannot
     * cannot this method in the constructor.
     */
    private void changeToColumnSelectionModel() {
        table.setRowSelectionAllowed(false);
        table.getColumnModel().setColumnSelectionAllowed(true);
    }

}