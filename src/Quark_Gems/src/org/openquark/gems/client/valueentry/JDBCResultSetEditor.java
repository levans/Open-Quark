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
 * JDBCResultSetEditor.java
 * Created: Apr. 8 / 2003
 * By: David Mosimann
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableColumnModel;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.foreignsupport.module.DataGems.DatabaseException;
import org.openquark.cal.foreignsupport.module.DataGems.QueryResult;
import org.openquark.cal.foreignsupport.module.DataGems.RecordPlaceholder;
import org.openquark.cal.module.Cal.Data.CAL_DataGems;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.valuenode.ForeignValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;


/**
 * A ValueEditor customized for a data inspector.  It will display lists and lists of tuples.
 */
public class JDBCResultSetEditor extends ValueEditor implements LazyLoadingTableRowProvider {
    
    private static final long serialVersionUID = 8182700672556039687L;

    /**
     * This interface defines what drag operations are supported by the
     * <code>JDBCResultSetEditor</code>.  Implementors of this interface will be
     * called when a certain drag operation is initiated by the user.
     */
    public interface JDBCResultSetDragPointHandler extends ValueEditorDragPointHandler {

    /**
     * This method will be called when the user attempts to drag a list of columns
     * from the JDBC ResultSet set table.  By default, this method does nothing
     * and returns <code>false</code>.  Subclasses are encouraged to override
     * this method to define their own dragging behaviour.
     * @param dge
     * @param parentEditor
     * @param columnModel
     * @return boolean
     */
    boolean dragColumns(
        DragGestureEvent dge,
        JDBCResultSetEditor parentEditor,
        TableColumnModel columnModel);
    }

    /**
     * A custom value editor provider for the JDBCResultSetEditor.
     */
    public static class JDBCResultSetEditorProvider extends ValueEditorProvider<JDBCResultSetEditor> {

        public JDBCResultSetEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return JDBCResultSetValueNodeAdapter.canHandleValue(valueNode)
//                    || JDBCRecordListValueNodeAdapter.canHandleValue(valueNode)
                    ;
        }
        
        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public JDBCResultSetEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager, ValueNode valueNode) {
            JDBCResultSetEditor editor = new JDBCResultSetEditor(valueEditorHierarchyManager, null, getPerspective().getWorkspace());
            editor.setOwnerValueNode(valueNode);
            return editor;
        }

        /* (non-Javadoc)
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(org.openquark.gems.client.valueentry.ValueEditorHierarchyManager, org.openquark.cal.valuenode.ValueNodeBuilderHelper, org.openquark.gems.client.valueentry.ValueEditorDragManager, org.openquark.cal.valuenode.ValueNode)
         */
        @Override
        public JDBCResultSetEditor getEditorInstance(
                ValueEditorHierarchyManager valueEditorHierarchyManager,
                ValueNodeBuilderHelper valueNodeBuilderHelper,
                ValueEditorDragManager dragManager,
                ValueNode valueNode) {
            JDBCResultSetEditor editor =
                new JDBCResultSetEditor(
                    valueEditorHierarchyManager,
                    getJDBCResultSetDragPointHandler(dragManager),
                    getPerspective().getWorkspace());
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
         * A convenient method for casting the drag point handler to the type that is
         * suitable for the <code>JDBCResultSetEditor</code> to use.  If such conversion is not
         * possible, then this method should return <code>null</code>.
         * @param dragManager
         * @return JDBCResultSetDragPointHandler
         */
        private JDBCResultSetDragPointHandler getJDBCResultSetDragPointHandler(ValueEditorDragManager dragManager) {
            ValueEditorDragPointHandler handler = getDragPointHandler(dragManager);
            if (handler instanceof JDBCResultSetDragPointHandler) {
                return (JDBCResultSetDragPointHandler) handler;
            }
            return null;
        }
    }

    /**
     * A simple implementation of the JDBCQueryResultAdapter interface that is
     * able to adapt to a CAL value of type "DataGems.ResultSet".
     */    
    public static class JDBCResultSetValueNodeAdapter implements JDBCQueryResultAdapter {
        
        /**
         * CAL type used for this adapter.
         */
        private static final QualifiedName JDBC_RESULT_SET_NAME = CAL_DataGems.TypeConstructors.ResultSet;
        
        private final QueryResult resultSet;
        
        public JDBCResultSetValueNodeAdapter(ValueNode valueNode) {
            resultSet = (QueryResult) valueNode.getValue();
        }

        /**
         * @see org.openquark.gems.client.valueentry.JDBCQueryResultAdapter#getResultSet()
         */
        public QueryResult getResultSet() {
            return resultSet;
        }

        /**
         * @see org.openquark.gems.client.valueentry.JDBCQueryResultAdapter#getRow(int)
         */
        public RecordPlaceholder getRow(int index) throws DatabaseException {
            return resultSet.resultGetRecord(index+1);
        }

        /**
         * @see org.openquark.gems.client.valueentry.JDBCQueryResultAdapter#hasRow(int)
         */
        public boolean hasRow(int index) {
            return resultSet.recordAt(index+1);
        }

        /**
         * Convenient method for testing if the given expression is of type
         * "DataGems.ResultSet".
         * @param valueNode
         * @return boolean
         */
        public static boolean canHandleValue(ValueNode valueNode) {
            if (valueNode instanceof ForeignValueNode) {
                return valueNode.getTypeExpr().hasRootTypeConstructor(JDBC_RESULT_SET_NAME);
            }
            return false;
        }

    }
    
//    /**
//     * A slighly more complex implementation of the JDBCQueryResultAdapter interface
//     * that is able to adapt to a CAL value of type "[DataGems.DatabaseRecord]".
//     */    
//    public static class JDBCRecordListValueNodeAdapter implements JDBCQueryResultAdapter {
//
//        /**
//         * CAL type used for this adapter.
//         */
//        private static final QualifiedName JDBC_RECORD_NAME = QualifiedName.make("DataGems", "DatabaseRecord");
//        
//        private QueryResult resultSet;
//        private ListValueNode listValueNode;
//        
//        public JDBCRecordListValueNodeAdapter(ValueNode valueNode) {
//            listValueNode = (ListValueNode) valueNode;
//            if (hasRow(0)) {
//                resultSet = getRow(0).getRecordSet();
//            }
//        }
//
//        /* (non-Javadoc)
//         * @see org.openquark.gems.client.valueentry.JDBCQueryResultAdapter#getResultSet()
//         */
//        public QueryResult getResultSet() {
//            return resultSet;
//        }
//
//        /* (non-Javadoc)
//         * @see org.openquark.gems.client.valueentry.JDBCQueryResultAdapter#getRow(int)
//         */
//        public RecordPlaceholder getRow(int index) {
//            ValueNode node = listValueNode.getValueAt(index);
//            return (RecordPlaceholder) node.getValue();
//        }
//
//        /* (non-Javadoc)
//         * @see org.openquark.gems.client.valueentry.JDBCQueryResultAdapter#hasRow(int)
//         */
//        public boolean hasRow(int index) {
//            return index >= 0 && index < listValueNode.getNElements();
//        }
//
//        /**
//         * Convenient method for testing if the given expression is of type "[DataGems.DatabaseRecord]".
//         * @param valueNode
//         * @return boolean
//         */
//        public static boolean canHandleValue(ValueNode valueNode) {
//            if (valueNode instanceof ListValueNode) {
//                TypeConsApp typeConsApp = valueNode.getTypeExpr().rootTypeConsApp();
//                if (typeConsApp.getNArgs() == 1) {
//                    return typeConsApp.getArg(0).isNonParametricType(JDBC_RECORD_NAME);                    
//                }
//            }
//            return false;
//        }
//    }

    /**
     * A simple variation of the lazy loading table that uses the string representation
     * of a cell's value as its tooltip text.
     */    
    private static class JDBCResultSetTable extends LazyLoadingTable {

        private static final long serialVersionUID = 6785306966736775696L;

        public JDBCResultSetTable(LazyLoadingTableRowProvider provider) {
            super(provider);
        }

        /* (non-Javadoc)
         * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
         */
        @Override
        public String getToolTipText(MouseEvent event) {
            Point p = event.getPoint();
            int row = rowAtPoint(p);
            int column = columnAtPoint(p);
            if (row >= 0 && column >= 0) {
                Object value = getValueAt(row, column);
                return value == null ? "(no value)" : value.toString(); // TODO internationalization
            } else {
                return super.getToolTipText(event);
            }
        }
    }
    
    protected final LazyLoadingTable table;
    protected final JDBCResultSetDragPointHandler dragPointHandler;
    protected CALWorkspace workspace;

    private JScrollPane scrollPane;
    
    /**
     * Query result adapter used for this editor.  This adapter will be refreshed
     * whenever the owner value node is changed.
     */
    protected JDBCQueryResultAdapter adapter;

    /**
     * JDBCResultSetEditor constructor.  Intended to be used by the value editor
     * provider only. 
     * 
     * @param valueEditorHierarchyManager
     * @param dragPointHandler
     * @param workspace
     */
    protected JDBCResultSetEditor(
            ValueEditorHierarchyManager valueEditorHierarchyManager,
            JDBCResultSetDragPointHandler dragPointHandler,
            CALWorkspace workspace) {

        super(valueEditorHierarchyManager);
        this.dragPointHandler = dragPointHandler;
        this.workspace = workspace;

        // Create the JTable to show the records
        table = new JDBCResultSetTable(this);

        // Initialize the components 
        initialize();
    }
    
    /**
     * Do some initialization work
     */
    private void initialize() {
        try {
            setName("JDBCResultSetEditor");
            setBorder(new EtchedBorder());
            setLayout(new BorderLayout());
            add(getScrollPane(), BorderLayout.CENTER);
            validate();
            setSize(getPreferredSize());
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    /**
     * Returns the query result adapter used for this editor.  Please note that this
     * value can never be null.
     * @return JDBCQueryResultAdapter
     */
    public JDBCQueryResultAdapter getResultAdapter() {
        return adapter;
    }
    
    /**
     * Converts the width, in terms of the number of character(s), to points
     * based on the default font. 
     * @param width
     * @return double
     */
    public double convertWidthInCharToPoint(int width) {
        // TODO this method for estimating the average width of the characters
        // supported by the font might not be accurate, but it should be okay
        // for now
        Rectangle2D rec = getFont().getStringBounds(
            String.valueOf("m"),
            new FontRenderContext(null, true, false));
        return rec.getMaxX() * width;
    }

    /**
     * Returns the component that should be assigned focus when the control first appears
     */
    @Override
    public Component getDefaultFocusComponent() {
        return getScrollPane();
    }

    /**
     * Return the JScrollPane1 property value.
     * @return JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            try {
                scrollPane = new JScrollPane(table);
                scrollPane.setName("JScrollPane1");
                scrollPane.setOpaque(true);
                scrollPane.setBorder(new EtchedBorder());
                scrollPane.setBackground(new Color(204, 204, 204));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return scrollPane;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Sets the initial value.  This is used to update the UI to reflect the current value
     */
    @Override
    public void setInitialValue() {
        getScrollPane().getViewport().add(table);

        // If there is a row, set edit mode on the first cell and select first row.
        ListSelectionModel lsm = table.getSelectionModel();
        if (table.getRowCount() > 0 && lsm.isSelectionEmpty()) {
            lsm.setSelectionInterval(0, 0);
        }

        // Ensure that the containers update correctly to reflect the newly added table component
        doLayout();
    }

    /**
     * Sets the ValueNode for this editor.  The editor will be reinitialized with the new value
     * @param newValueNode 
     */
    @Override
    public void setOwnerValueNode(ValueNode newValueNode) {
        // Set the new value node
        super.setOwnerValueNode(newValueNode);
        
        // pick the appropriate adapter for the value node and fill the table with
        // new information
        if (JDBCResultSetValueNodeAdapter.canHandleValue(newValueNode)) {
            adapter = new JDBCResultSetValueNodeAdapter(newValueNode);
            initializeTable();
        }
//        else if (JDBCRecordListValueNodeAdapter.canHandleValue(newValueNode)) {
//            adapter = new JDBCRecordListValueNodeAdapter(newValueNode);
//            initializeTable();
//        }
    }

    /* (non-Javadoc)
     * @see org.openquark.gems.client.valueentry.LazyLoadingTableRowProvider#hasRow(int)
     */
    public boolean hasRow(int row) {
        return adapter.hasRow(row);
    }

    /* (non-Javadoc)
     * @see org.openquark.gems.client.valueentry.LazyLoadingTableRowProvider#loadRow(int)
     */
    public Object[] loadRow(int row) {
        if (adapter.hasRow(row)) {
            try {
                RecordPlaceholder record = adapter.getRow(row);
                int size = adapter.getResultSet().getColumnCount();
                Object data[] = new Object[size];
                for (int i = 0; i < size; ++i) {
                    data[i] = record.extractObject(i+1);
                }
                return data;
            } catch (DatabaseException sqle) {
                // FIXME Log error
            }
        }
        return null;
    }

    private void initializeTable() {

        // Some variables we want to set and use inside the try/catch blocks
        LazyLoadingTableModel model = new LazyLoadingTableModel();
        TableColumnModel columnModel = table.getColumnModel();
        table.setTableHeader(new JDBCResultSetTableHeader(columnModel, this, dragPointHandler));
        QueryResult recordSet;
        int nColumns = 0;

        boolean failed = false;        
        try {
            // Get some information from the Record Set
            recordSet = adapter.getResultSet();
            if (recordSet != null) {
                // Set the appropriate columns into the table (note that this is 1-based not 0-based)
                nColumns = recordSet.getColumnCount();
                for (int i = 1; i <= nColumns; ++i) {
                    model.addColumn(recordSet.getColumnName(i));
                }
            } else {
                // cannot get the result set object: no column list
                failed = true;
            }
        } catch (DatabaseException e) {
            // FIXME Log this error
            failed = true;
        } finally {
            // If we fail to setup the headers then we're hooped.  Set an error message and return.
            if (failed) {
                model = new LazyLoadingTableModel();
                // TODO: localize the string 
                model.addColumn("Failed to Browse Data");
                table.setModel(model);
                return;
            }
        }

        // We have finished building up the model so we need to set it into the table
        model.setGloballyEditable(false);
        table.setModel(model);

        // Resize the columns to make the UI look nicer.
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.doLayout();
    }

    /**
     * The JDBCResultSetEditor can only be used for output.  The values can not be edited
     * @return boolean
     */
    @Override
    public boolean isEditable() {
        return false;
    }

}
