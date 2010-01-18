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
 * AbstractListValueEditor.java
 * Created: Feb 23, 2004
 * By: Richard Webster
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.ToolTipHelpers;


/**
 * This is a super class for value editors which display a list-like interface to the user.
 * This class should not assume that it is editing a specific type of value node (such as a ListValueNode),
 * but instead work with a AbstractListTableModel to manipulate the value.
 * @author Richard Webster
 */
public abstract class AbstractListValueEditor extends TableValueEditor {
    /**
     * KeyListener used to listen for user's commit (Enter), cancel (Esc) input. 
     * Register the key listener to the buttons of this ListTupleValueEditor. 
     */
    class ListTupleValueEditorKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {

            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                handleCommitGesture();
                evt.consume(); // Don't want the button to activate its action event.

            } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                evt.consume(); // Don't want the button to activate its action event.
            }
        }
    }

    private class IvjEventHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == AbstractListValueEditor.this.getAddButton()) {
                addButton_ActionEvents();

            } else if (e.getSource() == AbstractListValueEditor.this.getRemoveButton()) {
                removeButton_ActionEvents();
                
            } else if (e.getSource() == AbstractListValueEditor.this.getUpButton()) {
                upButton_ActionEvents();

            } else if (e.getSource() == AbstractListValueEditor.this.getDownButton()) {
                downButton_ActionEvents();
            }
        }
    }
    
    /**
     * Model for an uneditable table. Calls to isCellEditable() always return false.
     * @author Iulian Radu
     */
    private static class NoneditableTableModel extends DefaultTableModel {
        private static final long serialVersionUID = 821263469183246794L;

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }
    
    /** 
     * A simple one-column table whose row cells hold as values the row indices from another table.
     * The cells are rendered to look as header cells of the other table, and their width corresponds
     * to the other table row width, such that this object can be used as a left-hand-side table header.
     * 
     * This header attaches listeners to the table model, watching for insertions/deletions and updating
     * itself as necessary; also, a listener is attached to the table itself to watch for model replacement
     * events, in which case this object switches to listen for changes on the new model.
     * 
     * @author Iulian Radu 
     **/
    static class ListRowHeader extends JTable {
    
        private static final long serialVersionUID = 2251011377716682243L;

        /**
         * Renderer which renders cells in a similar style to the table's column header. 
         * @author Iulian Radu
         */
        private class CellRenderer extends DefaultTableCellRenderer {
             
            private static final long serialVersionUID = -5481784220610404701L;

            CellRenderer () {
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row,
                    int column) {
                
                JTableHeader tableHeader = indexedTable.getTableHeader();
                if (tableHeader != null) {
                    
                    setForeground(tableHeader.getForeground());
                    setBackground(tableHeader.getBackground());
                    setFont(tableHeader.getFont());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    
                } else {
                    
                    setForeground(UIManager.getColor("TableHeader.foreground"));
                    setBackground(UIManager.getColor("TableHeader.background"));
                    setFont(UIManager.getFont("TableHeader.font"));
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    
                }
                
                setText((value == null) ? "" : value.toString());
                return this;
            }
        }
        
        /**
         * Listener for the original table model. This updates the row index header whenever
         * there are rows added/deleted from the table.
         * @author Iulian Radu
         */
        private class ModelChangeListener implements TableModelListener {
            
            /**
             * Update the header in response to an update of the table model.
             * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
             */
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.DELETE || e.getType() == TableModelEvent.INSERT) {
                    initialize(indexedTable.getModel());
                }
            }
        }
        
        /** Table which this header is indexing */
        private final JTable indexedTable;
        
        /** Change listener for the table model */
        private final ModelChangeListener modelChangeListener;
                
        /**
         * Constructor
         * @param table which this row header attaches to
         */
        public ListRowHeader(JTable table) {
            super(new NoneditableTableModel());
            
            ((DefaultTableModel)this.getModel()).addColumn("rowCountColumn");
            this.getColumn("rowCountColumn").setCellRenderer(new CellRenderer());
            
            this.indexedTable = table;
            this.setRowHeight(table.getRowHeight());
            this.modelChangeListener = new ModelChangeListener();
            
            // In case the table model changes, switch the listener to the new model
            table.addPropertyChangeListener("model", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    ((TableModel)evt.getOldValue()).removeTableModelListener(modelChangeListener);
                    ((TableModel)evt.getNewValue()).addTableModelListener(modelChangeListener);
                    initialize(((TableModel)evt.getNewValue()));
                }
            });
        }
        
        /**
         * Initialize table with proper field names and listeners.
         * @param tableModel model to use
         */
        private void initialize(TableModel tableModel) { 
            DefaultTableModel model = ((DefaultTableModel)this.getModel());
            for (int i = 0, n = model.getRowCount(); i<n; i++) {
                model.removeRow(0);
            }
            
            // Get names of all fields and put them in the table
            for (int i = 0, n = tableModel.getRowCount(); i < n; i++) {
                List<String> rowElement = Collections.singletonList(Integer.toString(i));
                model.addRow(rowElement.toArray());
            }
        }
    }

    /** The miniums size of this editor. */
    private static final Dimension MIN_SIZE = new Dimension(200, 200);
    
    /** The maximum size of this editor. */
    private static final Dimension MAX_SIZE = new Dimension(600, 383);

    /** The minimum size if the editor is not editable. */
    private static final Dimension NOT_EDITABLE_MIN_SIZE = new Dimension(MIN_SIZE.width, 80);

    // Fields to hold on to all the UI components for this value editor
    private JButton ivjAddButton = null;
    private JPanel ivjArrowPanel = null;
    private JPanel ivjButtonPanel1 = null;
    private JPanel ivjButtonPanel2 = null;
    private JPanel ivjButtonPanel3 = null;
    private JButton ivjDownButton = null;
    private JPanel ivjEastPanel = null;
    private JLabel ivjElementIcon = null;
    private JButton ivjRemoveButton = null;
    private JPanel ivjSouthPanel = null;
    private JButton ivjUpButton = null;
    private final IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JPanel ivjCenterPanel = null;
    private JPanel ivjDivideLeftPanel = null;
    private JPanel ivjDivideRightPanel = null;
    private ListRowHeader elementIndexHeader = null;
    private JScrollPane divideLeftScrollPane = null;
    private JScrollPane divideRightScrollPane = null;
    private JSplitPane centerSplitPanel;
    private int dividerLocation = -1;
    
    /**
     * Constructor for AbstractListValueEditor.
     * @param valueEditorHierarchyManager the hierarchy manager for the editor
     */
    protected AbstractListValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }

    /**
     * Initialize the class.
     * Note: Extra set-up code has been added.
     */
    private void initialize() {
        try {
            setName("ListTupleValueEditor");
            setFocusCycleRoot(true);
            resetComponents();
            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Register the special key listener to all buttons.
        ListTupleValueEditorKeyListener ltveKeyListener = new ListTupleValueEditorKeyListener();
        getUpButton().addKeyListener(ltveKeyListener);
        getDownButton().addKeyListener(ltveKeyListener);
        getAddButton().addKeyListener(ltveKeyListener);
        getRemoveButton().addKeyListener(ltveKeyListener);

        // Set default cursor for components that should have only ever have default cursors.
        getAddButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        getRemoveButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        getUpButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        getDownButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * @see org.openquark.gems.client.valueentry.TableValueEditor#createTableModel(org.openquark.cal.valuenode.ValueNode)
     */
    @Override
    protected ValueEditorTableModel createTableModel(ValueNode valueNode) {
        return createListTableModel(valueNode);
    }

    /**
     * Returns a new model to be used for displaying and editing the data as a list.
     * Subclasses should implement this instead of createTableModel.
     * @return a new model to be used for displaying and editing the data as a list
     */
    protected abstract AbstractListTableModel createListTableModel(ValueNode valueNode);

    /**
     * Returns an interface for manipulating the contents of the list.
     * @return an interface for manipulating the contents of the list
     */
    protected AbstractListTableModel getListTableModel() {
        return (AbstractListTableModel) tableModel;
    }

    /**
     * Adds all of the subcomponents to 'this'.  The subcomponents include the centre panel showing the
     * table, the east components showing up/down arrows, and the south component showing the add/remove
     * item buttons.  If the value editor is editable (determined by calling isEditable()) then all
     * components are shown.  If the editor is not editable then only the centre component is shown for
     * viewing the data. 
     */
    protected void resetComponents() {
        // Ensure that the old components are removed.
        removeAll();
        setLayout(new BorderLayout());

        if (isEditable()) {
            // Add all the components
            add(getSouthPanel(), "South");
            add(getEastPanel(), "East");
            add(getCenterPanel(), "Center");
        } else {

            // Only add the viewing components
            add(getCenterPanel(), "Center");
                
            if (getRowCount() == 0) {                
                JLabel message = new JLabel(ValueEditorMessages.getString("VE_EmptyList"));
                message.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                message.setHorizontalAlignment(SwingConstants.CENTER);
                add(message, "South");
            }
        }

        // Validate the new layout
        validate();
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setEditable(boolean)
     */
    @Override
    public void setEditable(boolean b) {
        super.setEditable(b);

        // Reset the visible components (this may hide or show components that are only necessary when the
        // value editor is in an editable state)
        resetComponents();
    }

    /**
     * Moves the selected row up one.
     */
    protected void upButton_ActionEvents() {
        
        int selectedRow = getSelectedRow();
        int selectedColumn = getSelectedColumn();

        clearSelection(true);

        getListTableModel().moveRowUp(selectedRow);

        selectCell(selectedRow - 1, selectedColumn);
    }

    /**
     * Move the selected row one down.
     */
    protected void downButton_ActionEvents() {

        int selectedRow = getSelectedRow();
        int selectedColumn = getSelectedColumn();

        clearSelection(true);
        
        getListTableModel().moveRowDown(selectedRow);

        selectCell(selectedRow + 1, selectedColumn);
    }

    /**
     * Adds a new row with default values to the table.
     */
    protected void addButton_ActionEvents() {

        clearSelection(true);

        getListTableModel().addRow();

        // Highlight the newly added row.
        selectCell(getRowCount() - 1, getSelectedColumn());

        updateIconToolTip();
        updateRowHeaderDivider();
        
        // Ensure that the screen is updated to reflect the changes.  I don't think this should be necessary,
        // but it does fix the problem of not updating correctly and nothing else seems to...
        validate();
        repaint();
    }

    /**
     * Removes the selected row.
     */
    protected void removeButton_ActionEvents() {
        
        int oldSelectedRow = getSelectedRow();
    
        clearSelection(false);

        getListTableModel().removeRow(oldSelectedRow);

        // Handle selection, and enabling/disabling remove button.
        int rowCount = getRowCount();

        if (rowCount != 0) {
            
            int newSelectedRow = 0;

            // Update the highlighted/selected row and edited cell.
            if (rowCount == oldSelectedRow) {
                // Move highlight/selection up one row, since we just deleted the last row.
                newSelectedRow = oldSelectedRow - 1;
            } else {
                // Keep the selection in its old row.
                newSelectedRow = oldSelectedRow;
            }

            selectCell(newSelectedRow, getSelectedColumn());
        }

        enableButtonsForTableState();
        updateIconToolTip();
        updateRowHeaderDivider();

        // Ensure that the screen is updated to reflect the changes.  I don't think this should be necessary,
        // but it does fix the problem of not updating correctly and nothing else seems to...
        validate();
        repaint();
    }

    /**
     * Return the AddButton property value.
     * @return JButton
     */
    protected JButton getAddButton() {
        if (ivjAddButton == null) {
            try {
                ivjAddButton = new JButton();
                ivjAddButton.setName("AddButton");
                ivjAddButton.setMnemonic('a');
                ivjAddButton.setText(ValueEditorMessages.getString("VE_AddButtonLabel"));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAddButton;
    }

    /**
     * Return the ArrowPanel property value.
     * @return JPanel
     */
    private JPanel getArrowPanel() {
        if (ivjArrowPanel == null) {
            try {
                ivjArrowPanel = new JPanel();
                ivjArrowPanel.setName("ArrowPanel");
                ivjArrowPanel.setLayout(getArrowPanelGridLayout());
                getArrowPanel().add(getUpButton(), getUpButton().getName());
                getArrowPanel().add(getDownButton(), getDownButton().getName());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjArrowPanel;
    }

    /**
     * Return the ArrowPanelGridLayout property value.
     * @return GridLayout
     */
    private GridLayout getArrowPanelGridLayout() {
        GridLayout ivjArrowPanelGridLayout = null;
        try {
            /* Create part */
            ivjArrowPanelGridLayout = new GridLayout();
            ivjArrowPanelGridLayout.setRows(2);
            ivjArrowPanelGridLayout.setVgap(5);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjArrowPanelGridLayout;
    }

    /**
     * Return the ButtonPanel1 property value.
     * @return JPanel
     */
    private JPanel getButtonPanel1() {
        if (ivjButtonPanel1 == null) {
            try {
                ivjButtonPanel1 = new JPanel();
                ivjButtonPanel1.setName("ButtonPanel1");
                ivjButtonPanel1.setLayout(new FlowLayout());
                getButtonPanel1().add(getButtonPanel2(), getButtonPanel2().getName());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjButtonPanel1;
    }

    /**
     * Return the ButtonPanel2 property value.
     * @return JPanel
     */
    private JPanel getButtonPanel2() {
        if (ivjButtonPanel2 == null) {
            try {
                ivjButtonPanel2 = new JPanel();
                ivjButtonPanel2.setName("ButtonPanel2");
                ivjButtonPanel2.setLayout(getButtonPanel2FlowLayout());
                getButtonPanel2().add(getButtonPanel3(), getButtonPanel3().getName());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjButtonPanel2;
    }

    /**
     * Return the ButtonPanel2FlowLayout property value.
     * @return FlowLayout
     */
    private FlowLayout getButtonPanel2FlowLayout() {
        FlowLayout ivjButtonPanel2FlowLayout = null;
        try {
            /* Create part */
            ivjButtonPanel2FlowLayout = new FlowLayout();
            ivjButtonPanel2FlowLayout.setAlignment(FlowLayout.RIGHT);
            ivjButtonPanel2FlowLayout.setVgap(0);
            ivjButtonPanel2FlowLayout.setHgap(0);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjButtonPanel2FlowLayout;
    }

    /**
     * Return the ButtonPanel3 property value.
     * @return JPanel
     */
    private JPanel getButtonPanel3() {
        if (ivjButtonPanel3 == null) {
            try {
                ivjButtonPanel3 = new JPanel();
                ivjButtonPanel3.setName("ButtonPanel3");
                ivjButtonPanel3.setLayout(getButtonPanel3GridLayout());
                getButtonPanel3().add(getAddButton(), getAddButton().getName());
                getButtonPanel3().add(getRemoveButton(), getRemoveButton().getName());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjButtonPanel3;
    }

    /**
     * Return the ButtonPanel3GridLayout property value.
     * @return GridLayout
     */
    private GridLayout getButtonPanel3GridLayout() {
        GridLayout ivjButtonPanel3GridLayout = null;
        try {
            /* Create part */
            ivjButtonPanel3GridLayout = new GridLayout();
            ivjButtonPanel3GridLayout.setHgap(5);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjButtonPanel3GridLayout;
    }

    /**
     * Return the CenterPanel property value.
     * @return JPanel
     */
    protected JPanel getCenterPanel() {
        if (ivjCenterPanel == null) {
            ivjCenterPanel = createCenterPanel();
        }
        return ivjCenterPanel;
    }
    
    /**
     * @return split panel used in the center of the VEP 
     */
    protected JSplitPane getCenterSplitPanel() {

        if (centerSplitPanel == null) {
            centerSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerSplitPanel.setName("SplitCenterPanel");
            centerSplitPanel.setDividerSize(2);
            centerSplitPanel.setDividerLocation(50);
            centerSplitPanel.setLeftComponent(getDivideLeftPanel());
            centerSplitPanel.setRightComponent(getDivideRightPanel());
            centerSplitPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        }
        return centerSplitPanel;
    }
    
    /**
     * @return panel used in the center of the VEP
     */
    protected JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        centerPanel.add(getCenterSplitPanel(), BorderLayout.CENTER);
        return centerPanel;
    }
    
    /**
     * @return the panel used on the right hand side of the divider pane
     */
    protected JPanel getDivideRightPanel() {
        if (ivjDivideRightPanel == null) {
            ivjDivideRightPanel = createDivideRightPanel();
        }
        return ivjDivideRightPanel;
    }
    
    /**
     * @return new panel to use on the right hand side of the divider pane
     */
    protected JPanel createDivideRightPanel() {
        JPanel ivjDivideRightPanel = new JPanel();
        ivjDivideRightPanel.setName("DivideRightPanel");
        ivjDivideRightPanel.setLayout(new BorderLayout());
        ivjDivideRightPanel.add(getDivideRightScrollPane(), "Center");
        return ivjDivideRightPanel;
    }
    
    /**
     * @return the panel used on the left hand side of the divider pane
     */
    protected JPanel getDivideLeftPanel() {
        if (ivjDivideLeftPanel == null) {
            ivjDivideLeftPanel = createDivideLeftPanel();
        }
        return ivjDivideLeftPanel;
    }
    
    /**
     * Creates the panel on the left side of the divider pane.
     * This panel contains the column header " # ", and the left divide scroll pane.
     * 
     * @return new panel to use on the left side of the divide pane  
     */
    protected JPanel createDivideLeftPanel() {
        // Create panel for whole left side (this includes the row header and another padding label on top)
        
        JPanel leftDivisionPanel = new JPanel(new BorderLayout());
        JLabel ln = new JLabel(" # ");
        ln.setBorder(new BevelBorder(BevelBorder.RAISED, Color.GRAY, Color.WHITE, Color.GRAY, Color.LIGHT_GRAY));
        ln.setPreferredSize(new Dimension(1, table.getRowHeight() - 5));
        leftDivisionPanel.add(ln, BorderLayout.NORTH);
        leftDivisionPanel.add(getDivideLeftScrollPane(), BorderLayout.CENTER);
        leftDivisionPanel.setBorder(BorderFactory.createEmptyBorder());
        leftDivisionPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        return leftDivisionPanel;
    }
    
    /**
     * Get the scroll panel used for the left side of the divide pane.
     * This scroll panel does not contain the " # " column header (since this should not scroll).
     * 
     * The scroll panel contains the following components:
     *   - a panel containing 
     *      - the table row header (ie: a table whose cells indicate row indices)
     *      - a padding underneath the table (this becomes visible when the panel is stretched vertically
     *        past the table height)
     *   - a padding underneath the panel (this becomes visible only when there is a horizontal scrollbar 
     *     on the right side of the divider)
     * 
     * @return the left divider scroll pane (this contains the list row header by default)
     */
    private JScrollPane getDivideLeftScrollPane() {
        if (divideLeftScrollPane == null) {
            JPanel ivjDivideLeftPanel = new JPanel();
            ivjDivideLeftPanel.setName("DivideLeftPanel");
            ivjDivideLeftPanel.setLayout(new BorderLayout());
    
            // Create padding on the north and south side 
            // (if there is a horizontal scrollbar on the right side, this label will be expanded)
            JLabel ls = new JLabel(" ");
            ls.setPreferredSize(new Dimension(0,0));
            ivjDivideLeftPanel.add(ls, BorderLayout.SOUTH);
            ivjDivideLeftPanel.add(getElementIndexHeader(), BorderLayout.CENTER);
            ivjDivideLeftPanel.setBorder(BorderFactory.createEmptyBorder());
            
            // Create padding on the center side 
            // (this will show if the window is expanded vertically past the lineRowHeader height)
            JPanel rowHeaderPaddedPanel = new JPanel(new BorderLayout());
            JLabel lc = new JLabel(" ");
            // Note: If we set preferred size of this label to 0, it will not be rendered when it
            // is supposed to, manifesting a transparency bug.
            rowHeaderPaddedPanel.add(ivjDivideLeftPanel, BorderLayout.NORTH);
            rowHeaderPaddedPanel.setBorder(BorderFactory.createEmptyBorder());
    
            // Encase everything in a scroll pane so that we can scroll
            final JScrollPane rowHeaderScrollPane = new JScrollPane(rowHeaderPaddedPanel);
            rowHeaderPaddedPanel.add(lc, BorderLayout.CENTER);
            rowHeaderScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            rowHeaderScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            rowHeaderScrollPane.setBorder(BorderFactory.createEmptyBorder());
            
            divideLeftScrollPane = rowHeaderScrollPane;
            divideLeftScrollPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        return divideLeftScrollPane;
    }
    
    /**
     * @return the right scroll pane (this is the tableScrollPane by default)
     */
    private JScrollPane getDivideRightScrollPane() {
        if (divideRightScrollPane == null) {
            
            divideRightScrollPane = tableScrollPane;
            
            // Create small panel for upper right corner of main scroll pane
            
            JPanel cornerBorderPanel = new JPanel();
            cornerBorderPanel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.GRAY, Color.WHITE, Color.GRAY, Color.LIGHT_GRAY));
            tableScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, cornerBorderPanel);
            tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        }
        return divideRightScrollPane;
    }
    
    /**
     * @return the list row header
     */
    protected JTable getElementIndexHeader() {
        if (elementIndexHeader == null) {
            elementIndexHeader = new ListRowHeader(table);
            
            // The index 'header' is actually a table; it shouldn't have a column header of its own 
            elementIndexHeader.setTableHeader(null);
        }
        return elementIndexHeader;
    }

    /**
     * Returns whether an the type icon should be displayed in the editor.
     */
    protected boolean displayElementIcon () {
        // By default, display the element icon.
        return true;
    }

    /**
     * Return the DownButton property value.
     * @return JButton
     */
    protected JButton getDownButton() {
        if (ivjDownButton == null) {
            try {
                ivjDownButton = new JButton();
                ivjDownButton.setName("DownButton");
                ivjDownButton.setToolTipText(ValueEditorMessages.getString("VE_MoveElementDownToolTip"));                
                ivjDownButton.setMnemonic('d');
                ivjDownButton.setText("");
                ivjDownButton.setIcon(new ImageIcon(AbstractListValueEditor.class.getResource("/Resources/down.gif")));
                ivjDownButton.setMargin(new Insets(0, 0, 0, 0));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDownButton;
    }

    /**
     * Return the EastPanel property value.
     * @return JPanel
     */
    protected JPanel getEastPanel() {
        if (ivjEastPanel == null) {
            ivjEastPanel = createEastPanel();
        }
        return ivjEastPanel;
    }
    
    /**
     * @return new EastPanel
     */
    protected JPanel createEastPanel() {
        JPanel ivjEastPanel = new JPanel();
        ivjEastPanel.setName("EastPanel");
        ivjEastPanel.setBorder(new EtchedBorder());
        ivjEastPanel.setLayout(new FlowLayout());
        ivjEastPanel.add(getArrowPanel(), getArrowPanel().getName());
        return ivjEastPanel;
    }

    /**
     * Return the ElementIcon property value.
     * Note: Extra set-up code has been added.
     * @return JLabel
     */
    private JLabel getElementIcon() {
        if (ivjElementIcon == null) {
            try {
                ivjElementIcon = new JLabel();
                ivjElementIcon.setName("ElementIcon");
                ivjElementIcon.setIcon(new ImageIcon(getClass().getResource("/Resources/notype.gif")));
                ivjElementIcon.setText("");
                ivjElementIcon.setBorder(new EmptyBorder(0, 3, 0, 0));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjElementIcon;
    }

    /**
     * Return the RemoveButton property value.
     * @return JButton
     */
    protected JButton getRemoveButton() {
        if (ivjRemoveButton == null) {
            try {
                ivjRemoveButton = new JButton();
                ivjRemoveButton.setName("RemoveButton");
                ivjRemoveButton.setMnemonic('r');
                ivjRemoveButton.setText(ValueEditorMessages.getString("VE_RemoveButtonLabel"));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRemoveButton;
    }

    /**
     * Return the SouthPanel property value.
     * @return JPanel
     */
    protected JPanel getSouthPanel() {
        if (ivjSouthPanel == null) {
            ivjSouthPanel = createSouthPanel();
        }
        return ivjSouthPanel;
    }

    /** @return newly created SouthPanel */
    protected JPanel createSouthPanel() {
        JPanel ivjSouthPanel = new JPanel();
        ivjSouthPanel.setName("SouthPanel");
        ivjSouthPanel.setLayout(new BorderLayout());
        if (displayElementIcon()) {
            ivjSouthPanel.add(getElementIcon(), "West");
        }
        ivjSouthPanel.add(getButtonPanel1(), "East");
        return ivjSouthPanel;
    }

    /**
     * Return the UpButton property value.
     * @return JButton
     */
    protected JButton getUpButton() {
        if (ivjUpButton == null) {
            try {
                ivjUpButton = new JButton();
                ivjUpButton.setName("UpButton");
                ivjUpButton.setToolTipText(ValueEditorMessages.getString("VE_MoveElementUpToolTip"));
                ivjUpButton.setMnemonic('u');
                ivjUpButton.setText("");
                ivjUpButton.setIcon(new ImageIcon(getClass().getResource("/Resources/up.gif")));
                ivjUpButton.setMargin(new Insets(0, 0, 0, 0));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUpButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCellActivated() {

        super.handleCellActivated();
        
        enableButtonsForTableState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleElementLaunchingEditor() {

        super.handleElementLaunchingEditor();
        
        getAddButton().setEnabled(false);
        getRemoveButton().setEnabled(false);
        getUpButton().setEnabled(false);
        getDownButton().setEnabled(false);
    }

    /**
     * Enable/Disable the buttons according to the current state of the table.
     * Creation date: (03/14/02 11:21:00 AM)
     */
    protected void enableButtonsForTableState() {

        // Find out current editable state.
        boolean isEditable = isEditable();
        int selectedRow = getSelectedRow();

        if (selectedRow == -1 || getRowCount() == 0) {
            
            getAddButton().setEnabled(isEditable());
            getRemoveButton().setEnabled(false);
            getUpButton().setEnabled(false);
            getDownButton().setEnabled(false);

        } else {
    
            // Enable/disable add and remove.
            getAddButton().setEnabled(isEditable);
            getRemoveButton().setEnabled(isEditable);

            boolean isFirstRow = (selectedRow == 0);
            boolean isLastRow = (selectedRow == getRowCount() - 1);

            // Enable/disable up/down buttons.
            getUpButton().setEnabled(isEditable && !isFirstRow);
            getDownButton().setEnabled(isEditable && !isLastRow);
        }
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
     * Initializes connections
     * @exception Exception The exception description.
     */
    private void initConnections() throws Exception {
        getAddButton().addActionListener(ivjEventHandler);
        getRemoveButton().addActionListener(ivjEventHandler);
        getUpButton().addActionListener(ivjEventHandler);
        getDownButton().addActionListener(ivjEventHandler);
        
        // Scroll both table and row header when one scrolls
        getDivideLeftScrollPane().getViewport().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getDivideRightScrollPane().getViewport().setViewPosition(new Point(getDivideRightScrollPane().getViewport().getViewPosition().x, ((JViewport)e.getSource()).getViewPosition().y));
            }
        });
        getDivideRightScrollPane().getViewport().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getDivideLeftScrollPane().getViewport().setViewPosition(new Point(getDivideLeftScrollPane().getViewport().getViewPosition().x, ((JViewport)e.getSource()).getViewPosition().y));
            }
        });
    }
    
    /**
     * Update the location of the split panel divider to show the whole row index text,
     * if it is not already doing so.
     *  
     * Ex: When creating a new list, this method shifts the divider right when expanding 
     * the list from 9 to 10 rows.
     */
    protected void updateRowHeaderDivider() {
        if (elementIndexHeader == null || getCenterSplitPanel() == null) {
            return;
        }
        
        int width = SwingUtilities.computeStringWidth(elementIndexHeader.getFontMetrics(elementIndexHeader.getFont()), "" + (table.getRowCount()-1));
        width = Math.max(18, width + 10);
        if (dividerLocation < width) {
            dividerLocation = width;
            getCenterSplitPanel().setDividerLocation(dividerLocation);
        }
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {

        // If we're not editable remove the minimum height constraints.
        // That way the editor will size itself to the number of rows actually in it.
        // For example, the min size of 5 rows will not be used if there are only 2 rows
        // in the list. We still use 20 as a minimum in case the list is empty.
        Dimension minSize = isEditable() ? MIN_SIZE : NOT_EDITABLE_MIN_SIZE;
        
        initSize(minSize, MAX_SIZE);
        
        // Enlarge list row header divider to show full text of row indices, if not already doing so
        updateRowHeaderDivider();
        
        // Do some additional size tweaking below.
        // Note: We only constrain the height of the editor, not the width.
        // It should also be possible for the user to make the list wider since
        // the content in the value entry panels may not fit.
        
        if (isEditable()) {
        
            // We only care about our maximum size for the initial display.
            // Afterwards we allow the user to resize the editor however he wants.
            setMaxResizeDimension(null);
            
        } else if (getRowCount() == 0) {
            
            // No need to make the list any bigger, there's nothing in it.
            setMaxResizeDimension(NOT_EDITABLE_MIN_SIZE);
            
        } else {

            // There's some content in the list, but it's not editable.
            
            Dimension size = getSize();
            Dimension prefSize = getPreferredSize();
            
            // Note: If the center panel contains only a table with displayed column header, 
            // preferred size does not include the table header if getParent() == null. This is a
            // bug, and can be resolved by adding the table header height to the prefSize.height.
            
            // Set the maximum size to the preferred size. The user never needs
            // to make the editor any bigger since it's not editable.
            setMaxResizeDimension(new Dimension(2048, prefSize.height));

            // If the list is small then size the editor to fit it exactly.
            if (prefSize.height <= MIN_SIZE.height) {
                setMinResizeDimension(new Dimension(MIN_SIZE.width, prefSize.height));
                setSize(new Dimension(size.width, prefSize.height));
            }
        }
        
        validate();
        userHasResized();
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#refreshDisplay()
     */
    @Override
    public void refreshDisplay() {
        
        // Reselect the cell that was previously being edited.
        selectCell(getSelectedRow(), getSelectedColumn());

        // Update the displayed icon and its tooltip.
        updateIconToolTip();
        String iconName = valueEditorManager.getTypeIconName(getValueNode().getTypeExpr());
        getElementIcon().setIcon(new ImageIcon(ListTupleValueEditor.class.getResource(iconName)));

        enableButtonsForTableState();
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    protected void userHasResized() {
        
        updateColumnSizes();
    }
    
    /**
     * Updates the sizes of the table columns via the auto-resize function of the table,
     * such that either the columns are all proportionately stretched to fit screen size, 
     * or the auto-resize is turned off to allow columns to display their preferred size.
     */
    protected void updateColumnSizes() {
     
        Dimension tableScrollPaneSize = tableScrollPane.getSize();
        if (tableScrollPaneSize.width == 0) {
            
            // The sizes of editor components have not been initialized. 
            // Invoke this method later, because it depends on the size of the table scroll pane.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateColumnSizes();
                }
            });
            return;
        }
        
        // Calculate the combined width of the columns (this is the minimum size of the scroll pane 
        // when no automatic stretching occurs).
        
        int tableActualWidth = 0;
        for (int i = 0, colCount = table.getColumnCount(); i < colCount; i++) {
            tableActualWidth += table.getColumn(table.getColumnName(i)).getPreferredWidth();
        }
        
        // Now if the current view of the table is larger than the table itself, automatically resize it
        
        int newResizeMode;
        if (tableScrollPaneSize.width >= tableActualWidth) {
            newResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
            
        } else {
            newResizeMode = JTable.AUTO_RESIZE_OFF;
        }
        
        table.setAutoResizeMode(newResizeMode);
        validate();
        
    }

    /**
     * Call this whenever the icon's tooltip needs to be refreshed.
     * What happens is that the number of elements in this list will be shown
     * in the tooltip.
     * Creation date: (16/03/01 2:30:09 PM)
     */
    protected void updateIconToolTip() {

        String iconToolTip = valueEditorManager.getTypeName(getValueNode().getTypeExpr());
        int elementCount = getListTableModel().getRowCount();
        iconToolTip += ValueEditorMessages.getString("VE_OfLengthToolTip", new Integer(elementCount));        
        iconToolTip = "<html><body>" + ToolTipHelpers.wrapTextToHTMLLines(iconToolTip, this) + "</body></html>";
        getElementIcon().setToolTipText(iconToolTip);
    }
    
    /**
     * @return type expression of the list elements
     */
    protected TypeExpr getListElementType() {
        
        // Assume: the type expression contained in the value node is the list constructor
        return getValueNode().getTypeExpr().rootTypeConsApp().getArg(0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceValueNode(ValueNode newValueNode, boolean preserveInfo) {
        super.replaceValueNode(newValueNode, preserveInfo);
    
        // Update the UI
        updateIconToolTip();
        enableButtonsForTableState();
    }
}
