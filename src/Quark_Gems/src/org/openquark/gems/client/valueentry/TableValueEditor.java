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
 * TableValueEditor.java
 * Created: Feb 28, 2001
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ValueNode;


/**
 * This is a StructuredValueEditor that uses a table to visually represent and manipulate the data.
 * 
 * This editor uses a ListSelectionListener that will edit the currently selected cell.
 * If a cell is selected and a new cell becomes selected, then the value for the previously selected
 * cell will be committed and the new cell will start editing.
 *
 * This class behaves somewhat differently from a standard JTable in that the selected cell is always being
 * edited.  There are a number of odd looking pieces of code that attempt to ensure this is always true
 * including the ListSelectionListener which will ensure that we are editing the correct cell any time the
 * selection changes. 
 * @author Michael Cheng
 * @author Frank Worsley
 */
abstract public class TableValueEditor extends StructuredValueEditor {

    /**
     * If cell selection is changed this listener will commit the value currently being edited
     * and then start editing the value in the newly selected cell (if any is selected).
     * @author Frank Worsley
     */
    private class TableListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {

            // This has to be invoked later, otherwise the newly selected cell will not always
            // end up being edited. One way to reproduce: create a list of 4 Integers. Edit the
            // first cell and enter a new value. Press the down arrow key. Notice that the value
            // was committed and the next cell is selected, but it is not being edited.

            // Don't proceed if the value is still adjusting (the check is duplicated again later, but there
            // is no point adding the event to the event queue if it will just be rejected later)
            if (!e.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(new Runnable() {
                    
                    public void run() {
                        if (table.getSelectionModel().getValueIsAdjusting() || 
                            table.getColumnModel().getSelectionModel().getValueIsAdjusting()) {
                                
                            // Don't do anything if the value is still adjusting. Otherwise this can cause
                            // exceptions if the user clicks on a cell and drags the mouse. The BasicTableUI
                            // will try to repost mouse events to the cell editor, which may not be visible 
                            // on the screen as we change cell editing here. This results in an 
                            // IllegalStateException and then a NullPointerException.
                            
                            return;
                        }
                        
                        int selectedRow = table.getSelectedRow();
                        int selectedColumn = table.getSelectedColumn();
                        
                        // Ensure that the row and column we are editing is valid
                        if (selectedRow != -1 && selectedColumn != -1 &&
                            valueEditorHierarchyManager.existsInHierarchy(TableValueEditor.this)) {
                            scrollToSelectedRow();
                                
                            // If the user has selected a new cell and this editor still exists in the editor 
                            // hierarchy, then start editing the newly selected cell.  It's possible that we
                            // are already editing this cell so don't re-edit the same cell (which can do
                            // strange things to the UI by redrawing the same component multiple times)
                            if (selectedRow != table.getEditingRow() || selectedColumn != table.getEditingColumn()) {
                                table.editCellAt(selectedRow, selectedColumn);
                                handleCellActivated();
                                table.revalidate();
                            }
                        }
                    }
                });
            }
        }
    }
    
    /**
     * Will edit the previous or next cell if the user presses the up or down arrow keys.
     * @author Frank Worsley
     */
    private class UpDownTabKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int keyCode = e.getKeyCode();
            
            int editRow = getSelectedRow();
            int editColumn = getSelectedColumn();
            int nRows = table.getRowCount();
    
            if (keyCode == KeyEvent.VK_UP && editRow > 0) {
                selectCell(editRow - 1, editColumn);                
                e.consume();
                
            } else if (keyCode == KeyEvent.VK_DOWN && editRow < nRows - 1) {
                selectCell(editRow + 1, editColumn);                
                e.consume();
            }
        }
    }

    /** The table used by this editor. */ 
    protected final JTable table;

    /** The table cell editor used by the table. */
    private final ValueEditor tableCellEditor;
    
    /** The scroll pane the table is placed into. */
    protected final JScrollPane tableScrollPane;
    
    /** The model used by the table. */
    protected ValueEditorTableModel tableModel;

    /**
     * TableValueEditor constructor comment.
     * @param valueEditorHierarchyManager
     */
    protected TableValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {

        super(valueEditorHierarchyManager);

        // Ensure that the table rows are the correct size.  I think this is bad, but PANEL_HEIGHT is
        // supposed to include the size of the border so we may need to subtract the size of the border
        // if it won't be drawn.
        Border border = valueEditorManager.getValueEditorBorder(this);
        if (valueEditorManager.useValueEntryPanelBorders() || border == null) {
            table = createTable(ValueEntryPanel.PANEL_HEIGHT);
        } else {
            Insets insets = border.getBorderInsets(this);
            table = createTable(ValueEntryPanel.PANEL_HEIGHT - insets.top - insets.bottom);
        }
        
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setCursor(Cursor.getDefaultCursor());
        
        tableCellEditor = createTableCellEditor();
        tableCellEditor.setEditable(isEditable());
        
        table.setDefaultEditor(ValueNode.class, (TableCellEditor)tableCellEditor);
        
        ListSelectionListener selectionListener = createListSelectionListener();
        table.getSelectionModel().addListSelectionListener(selectionListener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
        
        table.addKeyListener(new UpDownTabKeyListener());        

        tableCellEditor.setContext(new ValueEditorContext() {
            public TypeExpr getLeastConstrainedTypeExpr() {
                ValueEditor parentEditor = tableCellEditor.getParentValueEditor();
                TypeExpr leastConstrainedParentType = parentEditor.getContext().getLeastConstrainedTypeExpr();
                
                // The parent editor's editing (non-owner) value node is the owner value node for the child (ie. the table).
                TypeExpr parentType = parentEditor.getValueNode().getTypeExpr();
                TypeExpr childTypeToFind = tableCellEditor.getOwnerValueNode().getTypeExpr();
                
                TypeExpr resultType = parentType.getCorrespondingTypeExpr(leastConstrainedParentType, childTypeToFind);
                return (resultType == null) ? TypeExpr.makeParametricType() : resultType;
            }
        });

        // Ensure that commits in the child are reflected in the table.
        tableCellEditor.addValueEditorListener(new ValueEditorAdapter() {
            
            @Override
            public void valueCommitted(ValueEditorEvent evt) {
                
                int editRow = table.getEditingRow();
                int editColumn = table.getEditingColumn();

                if (editRow == -1 || editColumn == -1) {
                    return;
                }

                // Only commit the child value if there really is a cell at this location.
                // If the user presses the remove button multiple times it can happen that the cell is gone
                // from the model, but we still try to commit its value.
                if (tableModel.getRowCount() > editRow && tableModel.getColumnCount() > editColumn) {
                    
                    ValueNode oldValueNode = (ValueNode) tableModel.getValueAt(editRow, editColumn);
                    ValueNode newValueNode = tableCellEditor.getValueNode();
                    
                    if (oldValueNode.sameValue(newValueNode)) {
                        return;
                    }
                    
                    saveSize();
                    
                    commitChildChanges(oldValueNode, newValueNode);

                    // Refresh the display to account for changes in the child value.
                    refreshDisplay();
                    
                    restoreSavedSize();
                }
            }
        });
    }
    
    /**
     * Creates the JTable that will be used by the value editor. 
     * @return JTable
     */
    private static JTable createTable(int rowHeight) {
        
        JTable table = new JTable();

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setRowMargin(0);
        table.setRowHeight(rowHeight);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        
        return table;
    }        

    /**
     * Creates the table model to use by this table editor. Subclasses implement this to return
     * the proper table model they want to use.
     * @param valueNode the value node to create the table model for
     * @return ValueEditorTableModel
     */
    protected abstract ValueEditorTableModel createTableModel(ValueNode valueNode);
    
    /**
     * Creates the cell editor for this table editor. Subclasses can override this to return
     * a customized cell editor.  Although not part of the type declaration it is required that the return
     * type implement the TableCellEditor interface.  Failure to do so will result in class cast exceptions.
     * It would be nice if this was enforced by the type, but that is difficult to do because of the structure
     * of the value editor heirarchy
     * @return a ValueEditor that implements the TableCellEditor interface
     */
    protected ValueEditor createTableCellEditor() {
        return new ValueEditorTableCellEditor(this, valueEditorHierarchyManager);
    }

    /**
     * Creates the table header renderer with the given icon.
     * @param typeIcon the icon for the header renderer
     * @return TableCellRenderer
     */
    protected static TableCellRenderer createTableHeaderRenderer(ImageIcon typeIcon) {
        
        DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 6442249477330477442L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                if (table != null) {
            
                    JTableHeader header = table.getTableHeader();
            
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                }

                setText((value == null) ? "" : value.toString());
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                
                return this;
            }
        };

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setIcon(typeIcon);

        return label;
    }
    
    /**
     * Creates the list selection listener to add to the table. By default this creates a
     * listener that will edit whatever cell is currently selected. If no cell is selected
     * it will stop editing. Override this to return a custom selection listener.
     * @return ListSelectionListener
     */
    private ListSelectionListener createListSelectionListener() {
        return new TableListSelectionListener();
    }
    
    /**
     * Stops editing the current cell if one is being edited. Does nothing if no cell
     * is currently being edited.
     * @param commit whether or not to commit the value
     */
    private void stopEditing(boolean commit) {
        
        TableCellEditor cellEditor = table.getCellEditor();
        
        if (cellEditor != null) {
            
            if (commit) {
                cellEditor.stopCellEditing();
            } else {
                cellEditor.cancelCellEditing();
            }
            
            table.removeEditor();
        }
    }
    
    /**
     * @return the table header used by the table
     */
    protected final JTableHeader getTableHeader() {
        return table.getTableHeader();
    }
    
    /**
     * @return the currently selected row which is also the row being edited.
     */
    protected int getSelectedRow() {
        return table.getSelectedRow();
    }
    
    /**
     * @return the currently selected column which is also the column being edited.
     */
    protected int getSelectedColumn() {
        return table.getSelectedColumn();
    }
    
    /**
     * @return the number of rows in the table
     */
    protected final int getRowCount() {
        return table.getRowCount();
    }
    
    /**
     * @return the number of columns in the table
     */
    protected final int getColumnCount() {
        return table.getColumnCount();
    }
    
    /**
     * Call this method when selected row changes.
     * If the selected row is still in view, then do nothing.
     * Else, if the selected row is no longer in view, then scroll to it.
     */
    protected final void scrollToSelectedRow() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {

            Rectangle rect = table.getCellRect(selectedRow, 0, true);

            JViewport viewport = tableScrollPane.getViewport();
            int viewportTop = viewport.getViewPosition().y;
            int viewportHeight = viewport.getExtentSize().height;
            int viewportBottom = viewportHeight + viewportTop;

            // Determine if the selected row is in view, and if not, then make sure viewport shows it.
            if (rect.getY() < viewportTop) {
                Point pointSelected = new Point((int) rect.getX(), (int) rect.getY());
                viewport.setViewPosition(pointSelected);
            } else if (rect.getY() > viewportBottom - table.getRowHeight()) {
                Point pointSelected = new Point((int) rect.getX(), (int) rect.getY() - viewportHeight + table.getRowHeight());
                viewport.setViewPosition(pointSelected);
            }
        }
    }
    
    /**
     * Convenience method to select the cell at the given row and column.
     * This will also result in the selected cell to become the edited cell
     * and the previously edited value to be committed.
     * @param rowNum the row number
     * @param colNum the column number
     */
    protected final void selectCell(int rowNum, int colNum) {
        
        // A concrete cell has to be selected, you cannot just select
        // a row or a column. Therefore, if one of them is -1, default it to
        // the first row or column.
        if (colNum == -1) {
            colNum = 0;
        }
        if (rowNum == -1) {
            rowNum = 0;
        }
                
        ListSelectionModel rowModel = table.getSelectionModel();
        ListSelectionModel colModel = table.getColumnModel().getSelectionModel();
        colModel.setSelectionInterval(colNum, colNum);
        rowModel.setSelectionInterval(rowNum, rowNum);
    }
    
    /**
     * Clears the selection and causes editing to stop.
     * @param commit whether or not to commit the current value
     */
    protected final void clearSelection(boolean commit) {
        stopEditing(commit);
        table.clearSelection();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {

        stopEditing(true);
        
        saveSize();
        
        notifyValueCommitted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cancelValue() {
        stopEditing(false);
        notifyValueCanceled();        
    }

    /**
     * Commits the child's changes to the parent.
     * This method is only called if the value of oldChild is different from newChild.
     * For important implementation notes, see the comment at the top of the TableValueEditor class.
     * @param oldChild the old child value
     * @param newChild the new child value
     */
    public abstract void commitChildChanges(ValueNode oldChild, ValueNode newChild);
    
    /**
     * When a cell has been activated, there might be set-up issues that need to be done.
     * By default this method makes the editor resizable and enables the scrollbars.
     * Subclasses should override this to perform any additional setup needed.
     */
    public void handleCellActivated() {
        setResizable(true);
        tableScrollPane.getVerticalScrollBar().setEnabled(true);
        tableScrollPane.getHorizontalScrollBar().setEnabled(true);
    }

    /**
     * Performs any setup needed when a child editor is launched.
     * By default this disables resizability and the scrollbars.
     * Subclasses should override this to perform any additional setup needed.
     */
    @Override
    public void handleElementLaunchingEditor() {
        setResizable(false);
        tableScrollPane.getVerticalScrollBar().setEnabled(false);
        tableScrollPane.getHorizontalScrollBar().setEnabled(false);
    }

    /**
     * Will give each column a ValueEditorTableCellRenderer set to the correct type.
     * Note: Call this method only after initializing the table, elementTypeArray, and typeColourManager.
     */   
    protected void initializeTableCellRenderers() {

        TypeExpr typeExpr = getValueNode().getTypeExpr();

        TableColumnModel tableColumnModel = table.getColumnModel();
        boolean displayElementNumber = typeExpr.isListType();        

        for (int i = 0, columnCount = tableColumnModel.getColumnCount(); i < columnCount; i++) {

            TypeExpr elementTypeExpr = tableModel.getElementType(i);
            ValueEditorTableCellRenderer cellRenderer =
                    new ValueEditorTableCellRenderer(displayElementNumber,
                                                     elementTypeExpr,
                                                     valueEditorManager);
            
            cellRenderer.setEditable(isEditable());

            // Give the column headers an icon of the type that they represent.
            String iconName = valueEditorManager.getTypeIconName(elementTypeExpr);
            ImageIcon typeIcon = showTypeIconInColumnHeading(i) ? new ImageIcon(TableValueEditor.class.getResource(iconName)) : null;
            TableCellRenderer headerRenderer = createTableHeaderRenderer(typeIcon);

            TableColumn tableColumn = tableColumnModel.getColumn(i);
            tableColumn.setCellRenderer(cellRenderer);
            tableColumn.setHeaderRenderer(headerRenderer);            
        }
    }

    /**
     * Initialized the size of this value editor. If there is a saved size, then that is used.
     * Otherwise the preferred size of the editor is used and all table columns are sized according to
     * the preferred width of the type they represent. If the passed in maximum size is null or the width/height
     * contains negative values, then the preferred size will be used as the maximum size for that value.
     * The same goes for the minimum size.
     * @param minSize the desired minimum size of the editor
     * @param maxSize the desired maximum size of the editor
     */
    protected final void initSize(Dimension minSize, Dimension maxSize) {

        if (!restoreSavedSize()) {

            // If there is no saved size, then use the preferred size and give the columns preferred widths.
            
            for (int i = 0, nElements = tableModel.getNElements(); i < nElements; ++i) {           

                TypeExpr innerTypeExpr = tableModel.getElementType(i);
                
                int colWidth;
                if (tableCellEditor instanceof ValueEntryPanel) {
                    colWidth = valueEditorManager.getTypePreferredWidth(innerTypeExpr, ((ValueEntryPanel)tableCellEditor).getValueFieldFontMetrics());

                    // The type width doesn't include the width of the VEP button/icon/border.
                    // Therefore we add 55 to correct for it.
                    colWidth += 55;
                } else {
                    colWidth = (int)tableCellEditor.getPreferredSize().getWidth(); 
                }
                
                TableColumn tableColumn = table.getColumnModel().getColumn(i);
                tableColumn.setPreferredWidth(colWidth);
            }
           
            Dimension tableSize = table.getPreferredSize();
            table.setPreferredScrollableViewportSize(new Dimension(tableSize.width, tableSize.height + 1));

            validate();

            Dimension prefSize = getPreferredSize();
            
            // For some reason header size is not included if parent is null.
            if (getParent() == null && table.getTableHeader() != null) {
                prefSize.height += table.getTableHeader().getPreferredSize().height;
            }
            
            setSize(prefSize);
        }
        
        // Now setup the maximum/minimum size.
        Dimension prefSize = getPreferredSize();

        // For some reason header size is not included if parent is null.
        if (getParent() == null && table.getTableHeader() != null) {
            prefSize.height += table.getTableHeader().getPreferredSize().height;
        }

        Dimension realMinSize = new Dimension(0, 0);
        realMinSize.width = minSize != null && minSize.width > -1 ? minSize.width : prefSize.width;
        realMinSize.height = minSize != null && minSize.height > -1 ? minSize.height : prefSize.height;
        
        Dimension realMaxSize = new Dimension(0, 0);
        realMaxSize.width = maxSize != null && maxSize.width > -1 ? maxSize.width : prefSize.width;
        realMaxSize.height = maxSize != null && maxSize.height > -1 ? maxSize.height : prefSize.height;
        
        setMaxResizeDimension(realMaxSize);
        setMinResizeDimension(realMinSize);
        
        Dimension currentSize = getSize();
        currentSize.width = ValueEditorManager.clamp(realMinSize.width, currentSize.width, realMaxSize.width);
        currentSize.height = ValueEditorManager.clamp(realMinSize.height, currentSize.height, realMaxSize.height);
        setSize(currentSize);
        
        revalidate();
        repaint();
    }
    
    /**
     * Loads the specified saved size by resizing UI components with the
     * dimensions stored in the size information.
     * @param sizeInfo structure containing size information
     */
    protected void loadSavedSize(ValueEditor.Info sizeInfo) {
        
        // Restore the saved size.
        List<Integer> columnWidthList = sizeInfo.getComponentWidths();
        Dimension savedSize = sizeInfo.getEditorSize();

        // Set sizes for the columns
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0, listCount = columnWidthList.size(), tableColumnCount = table.getColumnCount(); 
             i < listCount && i < tableColumnCount; 
             i++) {
            Integer colWidth = columnWidthList.get(i);
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(colWidth.intValue());
        }

        Dimension tableSize = table.getPreferredSize();
        table.setPreferredScrollableViewportSize(new Dimension(tableSize.width, tableSize.height + 1));
            
        setSize(savedSize);
    }
    
    /**
     * Restores the saved size, if there is any saved size.
     * @return true if saved size was restored, false if there was no saved size
     */
    private boolean restoreSavedSize() {
        
        ValueEditor.Info info = valueEditorManager.getInfo(getOwnerValueNode());

        if (info == null) {
            return false;
        }
       
        loadSavedSize(info);
            
        return true;
    }
    
    /**
     * Retrieve a size information structure describing the current size of the
     * editor and its columns.
     * @return ValueEditor.Info
     */
    protected ValueEditor.Info getSaveSize() {
        // Going with our set default when table empty (no row) policy.
        if (table.getRowCount() == 0) {
            return null;
        }

        List<Integer> columnWidthList = new ArrayList<Integer>();

        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0, colCount = columnModel.getColumnCount(); i < colCount; i++) {

            TableColumn column = columnModel.getColumn(i);
            Integer width = Integer.valueOf(column.getWidth());
            columnWidthList.add(width);
        }
        
        return new ValueEditor.Info(getSize(), columnWidthList);
    }
    
    /**
     * Saves the current size information of the value editor and associates it with the
     * owner value node. This includes the overall editor size and the widths of the 
     * individual columns.
     */
    private void saveSize() {

        valueEditorManager.associateInfo(getOwnerValueNode(), getSaveSize());
    }
    
    /**
     * Returns whether a type icon should be displayed in the specified column heading of the table.
     * By default this returns true for all columns.
     * @param columnN the column number
     * @return whether the type icon should be displayed for the given column
     */
    protected boolean showTypeIconInColumnHeading(int columnN) {
        return true;
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setEditable(boolean)
     */
    @Override
    public void setEditable(boolean b) {
        super.setEditable(b);
        tableCellEditor.setEditable(b);
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#replaceValueNode(org.openquark.cal.valuenode.ValueNode, boolean)
     */
    @Override
    public void replaceValueNode(ValueNode newValueNode, boolean preserveInfo) {

        super.replaceValueNode(newValueNode, preserveInfo);

        // Get the existing table selection so we can preserve it
        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();
        
        // Update the model
        setTableModel(createTableModel(newValueNode));
        
        // Restore the selection
        ListSelectionModel rowModel = table.getSelectionModel();
        rowModel.setSelectionInterval(selectedRow, selectedRow);
        ListSelectionModel colModel = table.getColumnModel().getSelectionModel();
        colModel.setSelectionInterval(selectedCol, selectedCol);

        initializeTableCellRenderers();
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setOwnerValueNode(org.openquark.cal.valuenode.ValueNode)
     */
    @Override
    public void setOwnerValueNode(ValueNode newValueNode) {

        super.setOwnerValueNode(newValueNode);

        setTableModel(createTableModel(getValueNode()));
        
        initializeTableCellRenderers();     
    }
    
    /**
     * Set the table model used by this editor's table
     * @param tableModel
     */
    protected void setTableModel(ValueEditorTableModel tableModel) {
        this.tableModel = tableModel;
        table.setModel(tableModel);
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#getDefaultFocusComponent()
     */
    @Override
    public Component getDefaultFocusComponent() {
        if (table.getCellEditor() != null) {
            return tableCellEditor;
        } else if (tableModel.getNElements() > 0) {
            return table;
        } else {
            return this;
        }
    }
}