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
 * RecordValueEditor.java
 * Created: Jul 12, 2004
 * By: Iulian Radu
 */

package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.AbstractRecordValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;
import org.openquark.gems.client.ToolTipHelpers;
import org.openquark.util.UnsafeCast;
import org.openquark.util.ui.UIUtilities;


/**
 * The ValueEditor for Record Value Nodes.
 * 
 * This editor displays record fields tabular format, and allows editing of field values through
 * individual value entry panels. For non-record-polymorphic records, this editor also allows
 * addition and removal of record fields. Depending on the value editor context, field names
 * may also be renamed.
 * 
 * @author Iulian Radu
 */
public final class RecordValueEditor extends TableValueEditor {

    /**
     * This interface defines what drag operations are supported by the
     * <code>RecordValueEditor</code>.  Implementors of this interface will be
     * called when a certain drag operation is initiated by the user.
     */
    public interface RecordValueDragPointHandler extends ValueEditorDragPointHandler {
        
        /**
         * This method defines the behaviour when the user attempts to drag a portion
         * of a tuple from the <code>RecordValueEditor</code>.  By default, this
         * method is empty and subclasses are encouraged to override this class to
         * specify their own drag handling code.
         * @param dge
         * @param parentEditor
         * @param fieldElementIndex
         * @return boolean
         */
        boolean dragFieldItem(DragGestureEvent dge, RecordValueEditor parentEditor, int fieldElementIndex);
    }

    /**
     * A custom value editor provider for the RecordValueEditor.
     */
    public static class RecordValueEditorProvider extends ValueEditorProvider<RecordValueEditor> {

        public RecordValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return (valueNode instanceof AbstractRecordValueNode) && 
            (hasSupportedFieldTypes((AbstractRecordValueNode)valueNode, providerSupportInfo));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public RecordValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            RecordValueEditor editor = new RecordValueEditor(valueEditorHierarchyManager, null);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public RecordValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNodeBuilderHelper valueNodeBuilderHelper,
                                             ValueEditorDragManager dragManager,
                                             ValueNode valueNode) {
            RecordValueEditor editor = new RecordValueEditor(valueEditorHierarchyManager,
                                                      getListFieldDragPointHandler(dragManager));
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
         * Checks if the value nodes for the field elements are supported by value editors.
         * @param valueNode the field value node to check for
         * @return true if all element value nodes are supported
         */
        private boolean hasSupportedFieldTypes(AbstractRecordValueNode valueNode, SupportInfo providerSupportInfo) {

            // Notify the info object that the value node's type is supported..
            providerSupportInfo.markSupported(valueNode.getTypeExpr());
            
            ValueEditorManager valueEditorManager = getValueEditorManager();
            
            for (int i = 0, size = valueNode.getTypeExpr().rootRecordType().getNHasFields(); i < size; i++) {
            
                ValueNode elementValueNode = valueNode.getValueAt(i);
                
                if (!valueEditorManager.isSupportedValueNode(elementValueNode, providerSupportInfo)) {
                    return false;
                }
            }
            
            return true;
        }

        /**
         * A convenient method for casting the drag point handler to the type that is
         * suitable for the <code>RecordValueEditor</code> to use.  If such conversion is not
         * possible, then this method should return <code>null</code>.
         *
         * @param dragManager
         * @return FieldDragPointHandler
         */
        private RecordValueDragPointHandler getListFieldDragPointHandler(ValueEditorDragManager dragManager) {
            ValueEditorDragPointHandler handler = getDragPointHandler(dragManager);
            if (handler instanceof RecordValueDragPointHandler) {
                return (RecordValueDragPointHandler) handler;
            }
            return null;
        }
    }

    /**
     * A record field model holds information about the value editor record type and its context.
     * Specifically, this holds the names of 'has' and 'lacks' fields in the editor record and 
     * its context.
     * 
     * Ex: If we are editing the record (r\age,r\name) => {r|age::Double, name::String} with age=1.0, name="Roger",
     * within the context (r\age) => {r|age::a}, the model would hold the following information:
     *     contextFieldNames = age
     *     hasFieldNames = age, name
     *     lacksFieldNames = age, name
     *     
     * A single such object is intended to model the editor current record type, providing update and 
     * query services for various UI components.
     * 
     * @author Iulian Radu
     */
    static class RecordFieldModel {
        
        /**
         * Interface used to inform holders of record field models that some change
         * has occurred in the record fields.
         * @author Iulian Radu
         */
        interface ModelChangeListener {
            
            /**
             * Respond to a change in the field model.
             */
            public void recordFieldModelChanged();
            
        }
        
        /** List of listeners to be informed of changes to this model */
        private final List<ModelChangeListener> changeListeners;
        
        /** List of has field names (sorted in the order of the RecordFieldName class) */
        private final List<FieldName> hasFieldNames;
        
        /** Set of lacks field names */
        private final Set<FieldName> lacksFieldNames;
        
        /** Set of has and lacks field names from the context.
         * These are grouped together because value editors may not edit or overwrite them. */
        private final Set<FieldName> contextFieldNames;
        
        
        /** Constructor **/
        RecordFieldModel() {
            hasFieldNames = new ArrayList<FieldName>();
            lacksFieldNames = new HashSet<FieldName>();
            contextFieldNames = new HashSet<FieldName>();
            changeListeners = new ArrayList<ModelChangeListener>();
        }
        
        /** Adds the specified change listener to this model */
        public void addModelChangeListener(ModelChangeListener listener) {
            changeListeners.add(listener);
        }
        
        /** Removes the specified change listener from this model */
        public void removeModelChangeListener(ModelChangeListener listener) {
            changeListeners.remove(listener);
        }
        
        /** Notifies the listeners that this model has changed */
        private void notifyModelChanged() {
            for (final ModelChangeListener changeListener : changeListeners) {
                changeListener.recordFieldModelChanged();
            }
        }
        
        /**
         * Builds the record field information from the type of the valuenode and its context.
         * If the fields acquired differ from the previously recorded fields, the listeners are informed
         * that the model has changed.
         * @param recordTypeExpr type expression of the ValueNode record
         * @param contextRecordTypeExpr type expression of the ValueNode record context
         */
        public void initialize(RecordType recordTypeExpr, TypeExpr contextRecordTypeExpr) {
            
            // Get names of all fields 
            List<FieldName> hasFieldNames = recordTypeExpr.getHasFieldNames();            

            Set<FieldName> lacksFieldNames = recordTypeExpr.getLacksFieldsSet();
            
            // Gather the fields from the context
            Set<FieldName> contextFieldNames = new HashSet<FieldName>();
            if (contextRecordTypeExpr != null && contextRecordTypeExpr.rootRecordType() != null) {
                               
                contextFieldNames.addAll(contextRecordTypeExpr.rootRecordType().getHasFieldNames());               
                
                contextFieldNames.addAll(contextRecordTypeExpr.rootRecordType().getLacksFieldsSet());
            }
            
            // If necessary, perform updates to the model and notify listeners
            if ( !hasFieldNames.equals(this.hasFieldNames) || 
                 !lacksFieldNames.equals(this.lacksFieldNames) ||
                 !contextFieldNames.equals(this.contextFieldNames)) {
                
                this.hasFieldNames.clear();
                this.hasFieldNames.addAll(hasFieldNames);
                this.lacksFieldNames.clear();
                this.lacksFieldNames.addAll(lacksFieldNames);
                this.contextFieldNames.clear();
                this.contextFieldNames.addAll(contextFieldNames);
                
                notifyModelChanged();
            }
        }
        
        /**
         * Get the name of a record 'has field', specifying its index in the ordering of the RecordFieldName class.
         *  
         * @param index field name index
         * @return name of the specified field
         */
        public FieldName getHasFieldName(int index) {
            return hasFieldNames.get(index);
        }
        
        /**
         * @return the number of has fields in this record
         */
        public int getNHasFields() {
            return hasFieldNames.size();
        }
        
        /**
         * @param fieldName
         * @return whether the field is specified by the context (either as a has field or lacks field)
         */
        public boolean isContextField(FieldName fieldName) {
            return contextFieldNames.contains(fieldName);
        }

        /**
         * @return whether the field is specified as a 'has field' of the record 
         */
        public boolean isHasField(FieldName fieldName) {
            return hasFieldNames.contains(fieldName);
        }
        
        /**
         * @return whether the field is specified in the record lacks fields
         */
        public boolean isLacksField(FieldName fieldName) {
            return lacksFieldNames.contains(fieldName);
        }
    }
    
    /**
     * A simple one-column table whose cells display the fields of the value editor record.
     * 
     * The table cells indicate field name, type and editability. Fields names not bound by the 
     * editor context are considered renameable, and thus specialized text editors are provided 
     * for these cells.
     * 
     * A record field model, representing the editor record type, is monitored by this object. 
     * 
     * @author Iulian Radu
     */
    static class RowHeader extends JTable implements RecordFieldModel.ModelChangeListener {
        
        private static final long serialVersionUID = -4067610194584025529L;

        /**
         * Interface used by the header to communicate with its owner when a field
         * is renamed, or when type information is required.
         * 
         * @author Iulian Radu
         */
        public interface RowHeaderOwner {
            
            /** 
             * Retrieve the type of the record field at the specified (RecordFieldName ordered) index.
             * @return field type expression
             */
            public TypeExpr getFieldType(int index);
            
            /** 
             * Perform actions necessary to rename the record field oldName to the newName
             * 
             * @param oldName original name of the field
             * @param newName new name of the field
             */
            public void renameField(FieldName oldName, FieldName newName);
        }
        
        /**
         * Cell renderer for the record field name cells.
         * These cells display field name, type icon, and editability.
         * 
         * @author Iulian Radu
         */
        static class CellRenderer extends DefaultTableCellRenderer {
            
            private static final long serialVersionUID = 2226106228605244181L;

            /** Editor which this cell renderer belongs to (this is used to determine cell editability and layout) */
            private final ValueEditor recordEditor;
            
            /** Header owner of the renderer */
            private final RowHeaderOwner headerOwner;
            
            /** Whether the fields are laid out horizontally.
             * If true, record fields are displayed on individual rows; otherwise, they are
             * displayed in individual columns */
            private final boolean horizontalLayout;
            
            /** The record field model representing the rendered fields */ 
            private final RecordFieldModel fieldModel;
            
            /**
             * Constructor
             * 
             * @param recordEditor value editor owning this cell renderer
             * @param horizontalLayout whether the fields are laid out horizontally (if true, fields are displayed on individual rows)
             * @param headerOwner owner of the row header, to be queried about field type
             * @param fieldModel field model, queried about field editability
             */
            public CellRenderer (ValueEditor recordEditor, boolean horizontalLayout, RowHeaderOwner headerOwner, RecordFieldModel fieldModel) {
                if ((recordEditor == null) || (headerOwner == null)) {
                    throw new NullPointerException();
                }
                this.headerOwner = headerOwner;
                this.recordEditor = recordEditor;
                this.horizontalLayout = horizontalLayout;
                this.fieldModel = fieldModel;
            }
            
            /**
             * Retrieves a label used for rendering a field name and its type icon. 
             * The label will have a dark gray background if the field is not editable. 
             * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                if (table != null && table.getTableHeader() != null) {
            
                    JTableHeader header = table.getTableHeader();
                    
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                    
                } else {
                    
                    setForeground(UIManager.getColor("TableHeader.foreground"));
                    setBackground(UIManager.getColor("TableHeader.background"));
                    setFont(UIManager.getFont("TableHeader.font"));
                    
                }                

                String displayText = (value == null) ? "" : value.toString();
                setText(displayText);
                
                boolean isNotEditable = fieldModel.isContextField(fieldModel.getHasFieldName(horizontalLayout? row : column)) && recordEditor.isEditable();
                if (isNotEditable) {
                    
                    setForeground(Color.GRAY);
                    setBorder(new BevelBorder(BevelBorder.RAISED, Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.LIGHT_GRAY));
                    
                } else {
                    
                    Color background = getBackground();
                    setBackground(UIUtilities.brightenColor(background, 0.85));
                    setBorder(new BevelBorder(BevelBorder.RAISED, Color.WHITE, getBackground(), Color.GRAY, getBackground()));
                }
                
                setHorizontalAlignment(SwingConstants.LEFT);
                TypeExpr typeExpr = headerOwner.getFieldType(horizontalLayout ? row : column);
                String iconName = recordEditor.valueEditorManager.getTypeIconName(typeExpr);
                ImageIcon typeIcon = new ImageIcon(getClass().getResource(iconName));
                setIcon(typeIcon);
                
                String typeExprTip = typeExpr.toString();
                typeExprTip = ToolTipHelpers.wrapTextToHTMLLines(typeExprTip, this);
                String tooltip = "<html><body><b>" + displayText + "</b> :: <i>" + typeExprTip + "</i>";
                if (isNotEditable) {
                    tooltip += ValueEditorMessages.getString("VE_ContextName");
                }
                tooltip += "</body></html>";
                setToolTipText(tooltip);
                
                return this;
            }
        }
        
        /**
         * Table model for the RowHeader table. 
         * This maps the field names from the record model to a table model, and properly indicates
         * editability of fields.
         *  
         * @author Iulian Radu
         */
        private static class HeaderTableModel extends DefaultTableModel {
            
            private static final long serialVersionUID = -3290644335698498773L;

            /** If true, the layout is horizontal and the fields are stored in rows; else, fields are in columns*/
            private final boolean horizontalLayout;
            
            /** Record field model which this header represents */
            private final RecordFieldModel fieldModel;
            
            /** Constructor */
            HeaderTableModel(boolean horizontalLayout, RecordFieldModel fieldModel) {
                super();
                this.horizontalLayout = horizontalLayout;
                this.fieldModel = fieldModel;
            }
            
            /** Queries the field model and indicates whether a field can be edited */
            @Override
            public boolean isCellEditable(int row, int col) {
                return !fieldModel.isContextField(fieldModel.getHasFieldName(horizontalLayout? row : col));
            }
        }
        
        /**
         * Cell editor for record field names; uses a specialized text field which changes
         * color if the record cannot contain a field with the specified value.
         * 
         * Though the same editor is used for all table cells, the editor uses only one text 
         * field as input component, and thus assumes that only one of its cells is edited at 
         * a time.
         * 
         * @author Iulian Radu
         */
        static class FieldNameCellEditor extends DefaultCellEditor {

            /**
             * Text field for editing cells identifying field names.
             * If entering an invalid field name, this field changes its color and updates
             * its tooltip accordingly.
             * 
             * @author Iulian Radu
             */
            private static class CellEditorTextField extends JTextField {
                
                private static final long serialVersionUID = 5681713375183368462L;

                /** Model holding information about the record fields */
                private final RecordFieldModel recordFieldModel;
                
                /** Whether the name in this component is proper for a record field */
                private boolean properFieldName = true;
                
                /** Stores a copy of the original field name which is edited (ie: is set proper to editing start) */
                private String editedFieldName = "";
                
                /** Stores the number of the row being edited */
                private int editedRow = -1;
                
                /** Stores the number of the column being edited */
                private int editedColumn = -1;
                
                /** If true, the layout is horizontal and the fields are displayed on individual rows; 
                 * else, fields are displayed in individual columns*/
                private final boolean horizontalLayout;
                
                /** Whether this text field is currently used for editing */
                private boolean isEditing = false;
                
                /**
                 * Listener for changes in the caret of this text component. Depending on the text
                 * typed, this modifies the text color and tooltip to indicate field name validity.
                 * 
                 * This type of listener is used because of difficulties with key listeners in table cell editors.
                 */
                private final CaretListener caretListener = new CaretListener() {
                    public void caretUpdate(CaretEvent e) {
                        
                        String myText = CellEditorTextField.this.getText();
                        FieldName myTextAsFieldName = FieldName.make(myText);
                        boolean validName = myTextAsFieldName != null;
                        
                        boolean isExistingHasField = validName ? recordFieldModel.isHasField(myTextAsFieldName) : false;
                        boolean isContextLessField = validName ? recordFieldModel.isContextField(myTextAsFieldName) : false;                        
                        
                        if (!editedFieldName.equals(myText) && (!validName || isContextLessField || isExistingHasField)) {
                            
                            properFieldName = false;
                            setForeground(new Color(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), Color.GRAY.getAlpha() - 50));
                            
                            //todoBI these resources must be localized. 
                            if (isExistingHasField) {                                
                                setToolTipText(myText + ValueEditorMessages.getString("VE_FieldAlreadyContained"));
                                
                            } else if (isContextLessField) {
                                setToolTipText(myText + ValueEditorMessages.getString("VE_FieldMustBeExcluded"));
                                
                            } else if (!validName) {
                                setToolTipText(myText + ValueEditorMessages.getString("VE_InvalidFieldName"));
                            }
                            
                        } else {
                            
                            setForeground(Color.BLACK);
                            setToolTipText(myText);
                            properFieldName = true;
                        }
                    }
                };
                    
                /** Constructor */
                CellEditorTextField(RecordFieldModel recordFieldModel, boolean horizontalLayout) {
                    this.recordFieldModel = recordFieldModel;
                    this.addCaretListener(caretListener);
                    this.horizontalLayout = horizontalLayout;
                }
                
                /** Prepare for starting an edit. This initializes the editing row/column and field name */
                void initializeEdit(int row, int column) {
                    if (recordFieldModel == null) {
                        throw new IllegalStateException();
                    }
                    
                    this.editedFieldName = recordFieldModel.getHasFieldName(horizontalLayout ? row : column).getCalSourceForm();
                    this.editedRow = row;
                    this.editedColumn = column;
                    this.isEditing = true;
                    
                    setForeground(Color.BLACK);
                }
                
                /** Indicate that editing has stopped */
                void finishedEdit() {
                    this.isEditing = false;
                }

                /** Indicates whether the field name is proper */
                boolean isValidFieldName() {
                    return properFieldName;
                }

                /** @return the name of the edited field (ie: value prior to editing) */
                public String getEditedFieldName() {
                    return editedFieldName;
                }
                
                /** @return the index of the row edited */
                public int getEditedRow() {
                    return editedRow;
                }
                
                /** @return the index of the column edited */
                public int getEditedColumn() {
                    return editedColumn;
                }
                
                /** @return whether this text field is performing editing */
                public boolean isEditing() {
                    return isEditing;
                }
            }
            
            /** Constructor */
            FieldNameCellEditor(RecordFieldModel recordFieldModel, boolean horizontalLayout) {
                super(new CellEditorTextField(recordFieldModel, horizontalLayout));
                
                // Add listener for the field to stop editing on focus lost / escape key
                this.getComponent().addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent e) {
                    }
                    public void focusLost(FocusEvent e) {
                        FieldNameCellEditor.this.stopCellEditing();
                    }
                });
                this.getComponent().addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        
                        int keyCode = e.getKeyCode();
                        
                        if (keyCode == KeyEvent.VK_ESCAPE) {
                            FieldNameCellEditor.this.cancelCellEditing();
                            return;
                        }
                    }
                });
            }
            
            private static final long serialVersionUID = -2392810011163295473L;
            
            /** {@inheritDoc} */
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                ((CellEditorTextField)editorComponent).initializeEdit(row, column);
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
            
            /**
             * Adds the specified focus listener to the editor component
             * @param listener
             */
            public void addFocusListener(FocusListener listener) {
                editorComponent.addFocusListener(listener);
            }
        }

        /** Listener for field name editor cells */
        private final CellEditorListener editorListener;
        
        /** The VEP which contains this row header (used for UI layout)*/
        private final ValueEditor recordEditor;
        
        /** Header owner for this row header */
        private final RowHeaderOwner headerOwner;
        
        /** Model holding the record field names */
        private final RecordFieldModel recordFieldModel;
        
        /** 
         * Constructor
         * 
         * @param recordEditor the value editor owning this row header
         * @param table the table whose rows we are heading
         * @param headerOwner owner of this header, responsible for performing renames
         * @param recordFieldModel field model represented
         */
        RowHeader(ValueEditor recordEditor, JTable table, RowHeaderOwner headerOwner, RecordFieldModel recordFieldModel) {
            super();
            setModel(new HeaderTableModel(true, recordFieldModel));
            this.setRowHeight(table.getRowHeight());
            ((DefaultTableModel)this.getModel()).addColumn("fieldName");
            
            TableCellRenderer cellRenderer = new CellRenderer(recordEditor, true, headerOwner, recordFieldModel);
            TableCellEditor   cellEditor = new FieldNameCellEditor(recordFieldModel, true);
            ((FieldNameCellEditor)cellEditor).setClickCountToStart(1);
            this.getColumn("fieldName").setCellRenderer(cellRenderer);
            this.getColumn("fieldName").setCellEditor(cellEditor);
            
            this.recordEditor = recordEditor;
            this.headerOwner = headerOwner;
            this.recordFieldModel = recordFieldModel;
            recordFieldModel.addModelChangeListener(this);
            
            // Add listener to the editor to perform renaming on commit
            
            editorListener = new CellEditorListener() {
                public void editingCanceled(ChangeEvent e) {
                    FieldNameCellEditor.CellEditorTextField textField = (FieldNameCellEditor.CellEditorTextField)((FieldNameCellEditor)e.getSource()).getComponent();
                    textField.finishedEdit();
                }
                public void editingStopped(ChangeEvent e) {
                    
                    FieldNameCellEditor.CellEditorTextField textField = (FieldNameCellEditor.CellEditorTextField)((FieldNameCellEditor)e.getSource()).getComponent();
                    if (!textField.isEditing()) {
                        return;
                    }
                    
                    int row = textField.getEditedRow();
                    int col = textField.getEditedColumn();
                    
                    textField.finishedEdit();
                    if ((row == -1) || (col == -1)) {
                        return;
                    }
                    
                    FieldName fieldName = RowHeader.this.recordFieldModel.getHasFieldName(row);
                    if (!textField.isValidFieldName()) {
                        setValueAt(fieldName.getCalSourceForm(), row, col);
                        return;
                    }
                    
                    String newFieldNameAsString = textField.getText();
                    if (newFieldNameAsString.equals(fieldName.getCalSourceForm())) {
                        return;
                    }
                    
                    RowHeader.this.headerOwner.renameField(fieldName, FieldName.make(newFieldNameAsString));
                    RowHeader.this.recordEditor.refreshDisplay();
                }
            };
        }
        
        /**
         * Adds the specified focus listener to the table and cell editor 
         * @param listener
         */
        public void addEditorFocusListener(FocusListener listener) {
            ((FieldNameCellEditor)this.getColumn("fieldName").getCellEditor()).addFocusListener(listener);
        }
        
        /**
         * Update the header table with the proper field names, in response to a model update.
         */
        public void recordFieldModelChanged() {
            
            // Remove all table rows
            DefaultTableModel model = ((DefaultTableModel)this.getModel());
            for (int i = 0, n = model.getRowCount(); i<n; i++) {
                model.removeRow(0);
            }
            
            // Get names of all fields and put them in the table
            for (int i = 0, n = recordFieldModel.getNHasFields(); i < n; i++) {
                List<FieldName> rowElement = Collections.singletonList(recordFieldModel.getHasFieldName(i));
                model.addRow(rowElement.toArray());
            }
            
            // Set the cell edit listeners of each row header cell; note that the number of cells may have
            // increased and reordered, so this editor needs to be set to the new cells this way:
            for (int i = 0, n = recordFieldModel.getNHasFields(); i < n; i++) {
                getCellEditor(i, 0).removeCellEditorListener(editorListener);
                getCellEditor(i, 0).addCellEditorListener(editorListener);
            }
        }
    }
    
    /**
     * Class for rendering cells in a table where each row contains a different type
     * expression. 
     * 
     * This creates a list of ValueEditorTableCellRenderers for rendering each
     * type, and simply delegates rendering to the appropriate one based on the row requested.
     * 
     * @author Iulian Radu
     */
    static class RowCellRenderer implements TableCellRenderer {
        
        private final List <ValueEditorTableCellRenderer> rowRenderers = new ArrayList<ValueEditorTableCellRenderer>();
        
        /**
         * Constructor
         * 
         * @param displayElementNumber whether to display element number in the renderer tooltip
         * @param rowTypes types of each element, per row
         * @param valueEditorManager
         */
        public RowCellRenderer(boolean displayElementNumber,
                                    TypeExpr[] rowTypes,
                                    ValueEditorManager valueEditorManager) {
            
            for (final TypeExpr rowType : rowTypes) {
                ValueEditorTableCellRenderer cellRenderer = new ValueEditorTableCellRenderer(displayElementNumber, rowType, valueEditorManager);
                rowRenderers.add(cellRenderer);
            }
        }

        /**
         * Set editability of the rendered cells
         * @param editable whether the cells are editable
         */
        public void setEditable(boolean editable) {
            for (int i = 0, n = rowRenderers.size(); i < n; i++) {
                ValueEditorTableCellRenderer cellRenderer = rowRenderers.get(i);
                cellRenderer.setEditable(editable);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            boolean isSelectedColumn = false; 
            
            // Call the respective renderer for this row
            
            // XXX: Note that the renderer uses its ROW to display a tooltip indicating the index of the element;
            // since in this layout the elements are displayed in columns, we pass the current column as the
            // renderer row.
            
            return rowRenderers.get(row).getTableCellRendererComponent(table, value, isSelectedColumn, hasFocus, column, column);
        }
    }
    
    /**
     * Table header which allows editing of column header cells.
     * 
     * This header is specialized for editing record fields, and it
     * uses the same renderers and cell editors as the regular RowHeader.
     */
    static class EditableHeader extends JTableHeader implements CellEditorListener, RecordFieldModel.ModelChangeListener {
        
        /**
         * Column model for editable columns. This ensures that when a column is added, 
         * it is converted to an editable column before being added to the model.
         * 
         * @author Iulian Radu
         */
        static class EditableHeaderTableColumnModel extends DefaultTableColumnModel {

            private static final long serialVersionUID = -1608980349246553152L;

            @Override
            public void addColumn(TableColumn column) {
                
                EditableHeaderTableColumn newColumn = new EditableHeaderTableColumn();
                newColumn.copyValuesFrom(column);
                
                super.addColumn(newColumn);
            }
        }
        
        /**
         * Represents a table column with an editable header.
         */
        static class EditableHeaderTableColumn extends TableColumn {
            
            private static final long serialVersionUID = 8939477802657777095L;

            /** Whether the column is editable */
            protected boolean isHeaderEditable;
            
            /** Editor for this column; null if none */
            protected TableCellEditor headerEditor;
            
            /** Constructor */
            public EditableHeaderTableColumn() {
                isHeaderEditable = true;
            }
            
            /**
             * Set editability of this column's header
             * @param isEditable
             */
            public void setHeaderEditable(boolean isEditable) {
                isHeaderEditable = isEditable;
            }
            
            /**
             * @return whether the column header may be edited
             */
            public boolean isHeaderEditable() {
                return isHeaderEditable;
            }
            
            /**
             * Copy the values of the base column into ours
             * @param base column to copy
             */
            void copyValuesFrom(TableColumn base) {
                modelIndex = base.getModelIndex();
                identifier = base.getIdentifier();
                width = base.getWidth();
                minWidth = base.getMinWidth();
                setPreferredWidth(base.getPreferredWidth());
                maxWidth = base.getMaxWidth();
                headerRenderer = base.getHeaderRenderer();
                headerValue = base.getHeaderValue();
                cellRenderer = base.getCellRenderer();
                cellEditor = base.getCellEditor();
                isResizable = base.getResizable();
            }
        }

        /**
         * UI for the editable table header
         */
        class EditableHeaderUI extends BasicTableHeaderUI {
            
            /**
             * @see javax.swing.plaf.basic.BasicTableHeaderUI#createMouseInputListener()
             */
            @Override
            protected MouseInputListener createMouseInputListener() {
                return new MouseInputHandler((EditableHeader) header);
            }
            
            /**
             * Handler for mouse input
             */
            public class MouseInputHandler extends BasicTableHeaderUI.MouseInputHandler {
                private Component dispatchComponent;
                protected EditableHeader header;
                
                /** Constructor */
                public MouseInputHandler(EditableHeader header) {
                    this.header = header;
                }
                
                /**
                 * Sets the dispatch component to the object pointed to by the mouse event.
                 */
                private void setDispatchComponent(MouseEvent e) {
                    Component editorComponent = header.getEditorComponent();
                    Point p = e.getPoint();
                    Point p2 = SwingUtilities.convertPoint(header, p, editorComponent);
                    dispatchComponent = SwingUtilities.getDeepestComponentAt(
                            editorComponent, p2.x, p2.y);
                }
                
                /** Post the mouse event to the dispatch component */
                private boolean repostEvent(MouseEvent e) {
                    if (dispatchComponent == null) {
                        return false;
                    }
                    MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e,
                            dispatchComponent);
                    dispatchComponent.dispatchEvent(e2);
                    return true;
                }
                
                /** 
                 * Edit the header column on mouse click
                 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
                 */
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!SwingUtilities.isLeftMouseButton(e)) {
                        return;
                    }
                    super.mousePressed(e);
                    if (header.getResizingColumn() == null) {
                        Point p = e.getPoint();
                        TableColumnModel columnModel = header.getColumnModel();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        if (index != -1) {
                            header.processEvent(new FocusEvent(e.getComponent(), FocusEvent.FOCUS_GAINED));
                            if (header.editCellAt(index, e)) {                                
                                setDispatchComponent(e);
                                repostEvent(e);
                            }
                            header.getTable().scrollRectToVisible(header.getHeaderRect(index));                            
                        }
                    }
                }
                
                /**
                 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
                 */
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    if (!SwingUtilities.isLeftMouseButton(e)) {
                        return;
                    }
                    repostEvent(e);
                    dispatchComponent = null;
                }
            }
        }
        
        private static final long serialVersionUID = 6343949193489544440L;
        
        /** Holds index of the column currently being edited */
        private int editingColumn;
        
        /** Index of the column currently selected on the header (ie: the last column edited) */
        private int selectedColumn;
        
        /** The table cell renderer for displaying header cells */
        private final RowHeader.CellRenderer cellRenderer;
        
        /** The table cell editor used to edit header cells */ 
        private final RowHeader.FieldNameCellEditor cellEditor;
        
        /** Editor component displayed for doing the editing (ie: the editing text field) */
        protected Component editorComp;
        
        /** Header owner, used for notification of field renames */
        private final RowHeader.RowHeaderOwner headerOwner;
        
        /** Model holding information about record fields. */
        private final RecordFieldModel recordFieldModel;
        
        /** 
         * Constructor 
         * 
         * @param columnModel column model of the table we are attaching to
         * @param recordEditor value editor owning this header
         * @param headerOwner owner of the header, performing renames
         * @param recordFieldModel field model of the record edited
         */
        public EditableHeader(TableColumnModel columnModel, ValueEditor recordEditor, RowHeader.RowHeaderOwner headerOwner, RecordFieldModel recordFieldModel) {
            super(columnModel);
            setReorderingAllowed(false);
            
            this.headerOwner = headerOwner;
            this.recordFieldModel = recordFieldModel;
            recordFieldModel.addModelChangeListener(this);
            this.cellRenderer = new RowHeader.CellRenderer(recordEditor, false, headerOwner, recordFieldModel);
            
            cellEditor = new RowHeader.FieldNameCellEditor(recordFieldModel, false);
            cellEditor.setClickCountToStart(1);
            cellEditor.addCellEditorListener(this);
            
            this.setFocusable(true);
        }
        
        /**
         * @see javax.swing.JComponent#updateUI()
         */
        @Override
        public void updateUI() {
            setUI(new EditableHeaderUI());
            resizeAndRepaint();
            invalidate();
        }
        
        /**
         * Initialize the cell editor and renderer for the table columns. 
         * Use this when the field model or editor layout changes.
         */
        public void recordFieldModelChanged() {
            updateColumnHeaders();
        }
        
        /**
         * Updates the headers of the model columns to have proper renderer and editability.
         */
        private void updateColumnHeaders() {
            for (int i = 0, n = getColumnModel().getColumnCount(); i < n; i++) {
                EditableHeaderTableColumn column = (EditableHeaderTableColumn)getColumnModel().getColumn(i);
                column.setHeaderEditable(!recordFieldModel.isContextField(FieldName.make((String)column.getHeaderValue())));
                column.setHeaderRenderer(cellRenderer);
            }
        }
        
        /**
         * Sets the column model and updates the headers of the columns.
         * @see javax.swing.table.JTableHeader#setColumnModel(javax.swing.table.TableColumnModel)
         */
        @Override
        public void setColumnModel(TableColumnModel columnModel) {
            super.setColumnModel(columnModel);
            updateColumnHeaders();
        }
        
        /**
         * Sets the table and updates the headers of the columns.
         * @see javax.swing.table.JTableHeader#setTable(javax.swing.JTable)
         */
        @Override
        public void setTable(JTable table) {
            super.setTable(table);
            updateColumnHeaders();
        }
        
        /**
         * Invoked when an event invokes editing of a certain cell
         * @param index index of header cell
         * @param e invoker event
         * @return true if editing should start; false if not
         */
        public boolean editCellAt(int index, EventObject e) {
            
            if (cellEditor != null && (isEditing() && !cellEditor.stopCellEditing())) {
                return false;
            }
            if (!isCellEditable(index)) {
                return false;
            }
            
            TableCellEditor editor = cellEditor;
            if (editor != null && editor.isCellEditable(e)) {
                editorComp = prepareEditor(editor, index);
                ((RowHeader.FieldNameCellEditor.CellEditorTextField)((RowHeader.FieldNameCellEditor)editor).getComponent()).initializeEdit(0, index);
                editorComp.setBounds(getHeaderRect(index));
                add(editorComp);
                editorComp.validate();
                setEditingColumn(index);
                setSelectedColumn(index);
                return true;
            }
            return false;
        }
        
        /**
         * Indicates if the cell is editable
         * @param index index of header cell
         * @return true if editable
         */
        public boolean isCellEditable(int index) {
            if (getReorderingAllowed()) {
                return false;
            }
            int columnIndex = columnModel.getColumn(index).getModelIndex();
            EditableHeaderTableColumn col = (EditableHeaderTableColumn) columnModel
                    .getColumn(columnIndex);
            return col.isHeaderEditable();
        }
        
        /**
         * Prepare the editor for editing
         * @param editor cell editor
         * @param index index of the field we are about to edit
         * @return component which will do the actual editing
         */
        private Component prepareEditor(TableCellEditor editor, int index) {
            Object value = columnModel.getColumn(index).getHeaderValue();
            boolean isSelected = true;
            JTable table = getTable();
            Component comp = editor.getTableCellEditorComponent(table, value,
                    isSelected, 0, index);  
            if (comp instanceof JComponent) {
                ((JComponent)comp).setNextFocusableComponent(this);
            }
            return comp;
        }
        
        /** 
         * @return the component doing the editing
         */
        private Component getEditorComponent() {
            return editorComp;
        }
        
        /**
         * Set index of the current column being edited 
         * @param aColumn
         */
        private void setEditingColumn(int aColumn) {
            editingColumn = aColumn;
        }
        
        /**
         * Set index of the selected column
         * @param aColumn
         */
        private void setSelectedColumn(int aColumn) {
            selectedColumn = aColumn;
        }
        
        /** 
         * @return index of the column being edited
         */
        private int getEditingColumn() {
            return editingColumn;
        }
        
        /**
         * @return index of the last edited column
         */
        public int getSelectedColumn() {
            return selectedColumn;
        }
        
        /**
         * @return the record field model observed
         */
        public RecordFieldModel getRecordFieldModel() {
            return recordFieldModel;
        }
        
        /**
         * Removes the editor component and the area it covers
         */
        private void removeEditorRect() {
            if (editorComp != null) {
                remove(editorComp);
                int index = getEditingColumn();
                Rectangle cellRect = getHeaderRect(index);
                setEditingColumn(-1);
                editorComp = null;
                repaint(cellRect);
            }
        }
        
        /**
         * @return whether the editor is currently editing a column
         */
        private boolean isEditing() {
            return (getEditingColumn() != -1);
        }

        /**
         * {@inheritDoc}
         */
        public void editingStopped(ChangeEvent e) {
            
            TableCellEditor editor = cellEditor;
            if (editor != null && getEditorComponent() != null) {
                int index = getEditingColumn();
                removeEditorRect();
                
                // Now update the field name
                if (index == -1) {
                    return;
                }
                
                FieldName oldFieldName = recordFieldModel.getHasFieldName(index);
                RowHeader.FieldNameCellEditor.CellEditorTextField textField = (RowHeader.FieldNameCellEditor.CellEditorTextField)((RowHeader.FieldNameCellEditor)e.getSource()).getComponent();
                String newFieldNameAsString = textField.getText();
                
                if (!textField.isValidFieldName() || newFieldNameAsString.equals(oldFieldName.getCalSourceForm())) {
                    columnModel.getColumn(index).setHeaderValue(oldFieldName.getCalSourceForm());
                    return;
                }
                
                headerOwner.renameField(oldFieldName, FieldName.make(newFieldNameAsString)); 
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void editingCanceled(ChangeEvent e) {
            removeEditorRect();
        }
    }
    
    private static final long serialVersionUID = -2376095137973719167L;
    
    /** The minimum size of this editor */
    private static final Dimension MIN_SIZE = new Dimension(200, -1);
    
    /** The maximum size of this editor. */
    private static final Dimension MAX_SIZE = new Dimension(600, 400);
    
    /** 
     * The default prefix for a new record field name. 
     * If clashing with an existing field name, this will be appended with an incremental numeral 
     * until the clash is resolved. In effect, we are always creating ordinal field names.
     */
    static final String DEFAULT_NEW_FIELD_PREFIX = "#";    
    
    /**
     * Whether the layout of this editor is allowed to be switchable between horizontal and vertical.
     * The default is verticalLayout, where fields correspond to rows.
     */
    private static final boolean LAYOUT_SWITCH_ALLOWED = false;

    /** Action for adding a record field */
    private Action addAction;
    
    /** Action for removing a record field */
    private Action deleteAction;

    /** Table indicating record fields as row headers */
    private final RowHeader rowHeader;
    
    /** Scroll pane for the row header */
    private final JScrollPane rowHeaderScrollPane;
    
    /** Indicates if this editor has been initialized once */
    private boolean initializedOnce = false;
    
    /** Main panel holding row header scroll pane on the left side, and table on the right */  
    private final JSplitPane dividerPanel;
    
    /** Icon indicating the record type */
    private JLabel typeIcon;

    /** Whether the record is a record polymorphic type, (ie: whether record fields may be added/removed) */
    private boolean isBasePolymorphicRecord = true;

    /** Panel containing the editor buttons */
    private final JPanel buttonPanel;
    
    /** Panel replacing the divider panel, when the record has no fields */
    private final JPanel noFieldsPanel;
    
    /** 
     * Whether the table orientation is vertical. 
     * If true, fields are displayed in individual columns; otherwise, they are
     * displayed in individual rows.
     */
    private boolean verticalLayout = false;
    
    /** Action for switching between horizontal and vertical layout */
    private Action switchAction;
    
    /** Model which tracks the fields in the record type, observed by components representing such fields */
    private final RecordFieldModel recordFieldModel;
    
    
    /**
     * RecordValueEditor constructor 
     * @param valueEditorHierarchyManager
     * @param dragPointHandler
     */
    protected RecordValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager,
            RecordValueDragPointHandler dragPointHandler) {

        super(valueEditorHierarchyManager);
        this.recordFieldModel = new RecordFieldModel();
        
        // Initialize button panel
        
        buttonPanel = new JPanel(new GridBagLayout());
        JButton addButton = new JButton(getAddAction());
        JButton deleteButton = new JButton(getDeleteAction());
        
        {
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                buttonPanel.add(getTypeIcon(), constraints);
            }
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 1.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                buttonPanel.add(new JLabel(), constraints);
            }
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 2;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5,5,5,5);
                buttonPanel.add(addButton, constraints);
            }
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 3;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5,5,5,5);
                buttonPanel.add(deleteButton, constraints);
            }
            
            // Put the Switch button only if layout switching is enabled
            if (LAYOUT_SWITCH_ALLOWED) {
                
                JButton switchButton = new JButton(getSwitchAction());
                
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 4;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5,5,5,5);
                buttonPanel.add(switchButton, constraints);
            }
        }
        
        // Initialize main panel and headers
        
        dividerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        dividerPanel.setDividerSize(2);
        
        // Create header listener for renames
        
        RowHeader.RowHeaderOwner rowHeaderOwner = new RowHeader.RowHeaderOwner() {
            
            public TypeExpr getFieldType(int row) {
                return ((AbstractRecordValueNode)getValueNode()).getFieldTypeExpr(((AbstractRecordValueNode)getValueNode()).getFieldName(row));
            }
            
            public void renameField(FieldName oldName, FieldName newName) {

                replaceValueNode(((AbstractRecordValueNode)getValueNode()).renameField(oldName, newName, valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer()), true);
                
                int fieldIndex = getValueNode().getTypeExpr().rootRecordType().getHasFieldNames().indexOf(newName);
                selectCell(fieldIndex, 0);
            }
        };
        
        // Create row header
        
        rowHeader = new RowHeader(this, table, rowHeaderOwner, recordFieldModel);
        rowHeader.addEditorFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                RecordValueEditor.this.clearSelection(true);
                enableButtons();
            }
            public void focusLost(FocusEvent e) {
            }
        });
        
        // Create panels and padding for the row header
        
        // The row header scroll panel (which is the left component of the dividePanel) contains the 
        // following components:
        //   - (rowHeaderPaddedInnerPanel) a panel containing 
        //      - (rowHeader) the table row header (ie: a table whose cells indicate row indices)
        //      - (l1) a label padding underneath the table (this becomes visible when the panel is stretched vertically
        //        past the table height)
        //   - (l2) a label padding underneath the panel (this becomes visible only when there is a horizontal scrollbar 
        //     on the right side of the divider)
        
        JPanel rowHeaderPaddedPanel = new JPanel(new BorderLayout());
        JPanel rowHeaderPaddedInnerPanel = new JPanel(new BorderLayout());
        rowHeaderPaddedInnerPanel.add(rowHeader, BorderLayout.NORTH);
        JLabel l1 = new JLabel(" ");
        l1.setPreferredSize(new Dimension(0,0));
        rowHeaderPaddedInnerPanel.add(l1, BorderLayout.CENTER);
        
        rowHeaderPaddedPanel.add(rowHeaderPaddedInnerPanel, BorderLayout.CENTER);
        rowHeaderPaddedPanel.setBorder(BorderFactory.createEmptyBorder());
        rowHeaderScrollPane = new JScrollPane(rowHeaderPaddedPanel);
        
        JLabel l2 = new JLabel(" ");
        l2.setPreferredSize(new Dimension(0,0));
        rowHeaderPaddedPanel.add(l2, BorderLayout.SOUTH);
        rowHeaderScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rowHeaderScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        rowHeaderScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rowHeaderScrollPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        table.setTableHeader(null);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Place the row header on the left and table scroll pane on the right
        
        dividerPanel.setRightComponent(tableScrollPane);
        dividerPanel.setLeftComponent(rowHeaderScrollPane);
        dividerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Add listeners to scroll both table and row header when one scrolls
        
        rowHeaderScrollPane.getViewport().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                tableScrollPane.getViewport().setViewPosition(new Point(tableScrollPane.getViewport().getViewPosition().x, ((JViewport)e.getSource()).getViewPosition().y));
            }
        });
        tableScrollPane.getViewport().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                rowHeaderScrollPane.getViewport().setViewPosition(new Point(rowHeaderScrollPane.getViewport().getViewPosition().x, ((JViewport)e.getSource()).getViewPosition().y));
            }
        });
        
        // Attach a listener for drag events
        
        if (dragPointHandler != null) {
            JTableHeader tableHeader = getTableHeader();
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                tableHeader,
                DnDConstants.ACTION_COPY_OR_MOVE,
                null);
        }
        
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
        
        noFieldsPanel = new JPanel(new BorderLayout());
        JLabel noFieldsLabel = new JLabel(ValueEditorMessages.getString("VE_NoFieldsLabel"));
        noFieldsLabel.setPreferredSize(new Dimension(noFieldsLabel.getPreferredSize().width, table.getRowHeight() + 5));
        noFieldsLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        noFieldsPanel.add(noFieldsLabel, BorderLayout.CENTER);
        
        add(noFieldsPanel, BorderLayout.CENTER);
        add(dividerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Note: If the editor type is a records which is non-record-polymorphic 
     * then this method ensures that the type becomes record-polymorphic if the context allows.
     *   Ex: If the record type is {age::Double} and context is a, the type is transmuted to (r\age)=>{r|age::Double}
     * 
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {
        
        if (!initializedOnce) {
            initSize(MIN_SIZE, MAX_SIZE);
            initializedOnce = true;
        }
        
        // If we are not constrained by the context, make sure our type is not record polymorphic,
        // This is an allowed operation because we are actually specializing the type.
        
        // TODO: Enforce non-record-polymorphic regardless of context.
        // Because the CAL value produced by the value node is always non-record-polymorphic, the
        // type of this value entry panel should be consistent. 
        //
        // This is currently not possible because the type switching mechanism does not
        // properly update a record type switch form record-polymorphic to non-record-polymorphic.
        
        TypeExpr contextRecordType = getContext().getLeastConstrainedTypeExpr();
        if (contextRecordType.rootTypeVar() != null) {
         
            if (getValueNode().getTypeExpr().rootRecordType().isRecordPolymorphic()) {
            
                // Not constrained by context, so create new type expression according to the fields we have
                
                Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                List<FieldName> hasFields = getValueNode().getTypeExpr().rootRecordType().getHasFieldNames();
                for (final FieldName hasFieldName : hasFields) {
                    TypeExpr hasFieldType = getValueNode().getTypeExpr().rootRecordType().getHasFieldType(hasFieldName);
                    
                    fieldNamesToTypeMap.put(hasFieldName, hasFieldType);
                }
                
                RecordType newRecordType = TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
                
                replaceValueNode(getValueNode().transmuteValueNode(valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer(), newRecordType), true);
                notifyValueChanged(getValueNode());
            }
        }
        
        isBasePolymorphicRecord = contextRecordType.rootTypeVar() != null ? true : contextRecordType.rootRecordType().isRecordPolymorphic();
        setButtonPanelVisible(isBasePolymorphicRecord);
        
        // Update the field model
        recordFieldModel.initialize((RecordType)getValueNode().getTypeExpr(), getContext().getLeastConstrainedTypeExpr());
        
        // Update the UI
        initializeUI();
        
        refreshMinMaxDimensions();
    }
    
    /**
     * Refresh the minimum and maximum resize dimensions of this editor, according to the
     * number of fields displayed.
     */
    private void refreshMinMaxDimensions() {
        // Initialize max/min size
        
        tableScrollPane.setPreferredSize(table.getPreferredSize());
        
        setMaxResizeDimension(new Dimension(2048, Math.min(MAX_SIZE.height, getPreferredSize().height)));
        setMinResizeDimension(new Dimension(Math.min(getMaxResizeDimension().width, getMinimumSize().width), 
                                            Math.min(getMaxResizeDimension().height, Math.max(MIN_SIZE.height, getPreferredSize().height))));
        

        Dimension minResizeDimension = getMinResizeDimension();
        Dimension maxResizeDimension = getMaxResizeDimension();
        
        Dimension currentSize = getSize();
        currentSize.width = ValueEditorManager.clamp(minResizeDimension.width, currentSize.width, maxResizeDimension.width);
        currentSize.height = ValueEditorManager.clamp(minResizeDimension.height, currentSize.height, maxResizeDimension.height);
        setSize(currentSize);
        
        validate();
    }
    
    /**
     * Initialize UI components. This adds the appropriate UI components to the
     * value editor, depending on the number of fields and orientation.
     */
    private void initializeUI() {
        
        if (((AbstractRecordValueNode)getValueNode()).getNFieldNames() > 0) {
            
            // We have some fields in the record
            
            if (!verticalLayout) {
                
                // Displaying fields on rows, via rowHeader
                
                dividerPanel.setRightComponent(tableScrollPane);
                
                // Set minimum size for the header

                int maxWidth = 0;
                List<FieldName> fieldNames = getValueNode().getTypeExpr().rootRecordType().getHasFieldNames();
                for (final FieldName fieldName : fieldNames) {
                    int width = SwingUtilities.computeStringWidth(rowHeader.getFontMetrics(rowHeader.getFont()), fieldName.getCalSourceForm());
                    maxWidth = Math.max(maxWidth, width);
                }
                maxWidth += 30; // longest (to display) field + 30
                
                // Initialize divider
                
                int horizontalDividerLocation = Math.max(rowHeader.getMinimumSize().width, maxWidth);
                dividerPanel.setDividerLocation(horizontalDividerLocation);
                
                // Make sure when we have some fields, we don't display the "No Fields" panel
                
                noFieldsPanel.setVisible(false);
                dividerPanel.setVisible(true);
                
                remove(noFieldsPanel);
                add(dividerPanel, BorderLayout.CENTER);
                
            } else {
                
                // Displaying fields in columns
                
                remove(dividerPanel);
                add(tableScrollPane, BorderLayout.CENTER);
                
            }
        } else {

            // Make sure when we have no fields, we display the "No Fields" panel
            
            noFieldsPanel.setVisible(true);
            dividerPanel.setVisible(false);
            
            remove(dividerPanel);
            add(noFieldsPanel, BorderLayout.CENTER);
        }
        enableButtons();
        
        validate();
    }
    
    /**
     * Set visibility of the button panel as specified
     */
    private void setButtonPanelVisible(boolean visible) {
        for (int i = 0, n = buttonPanel.getComponentCount(); i < n; i++) {
            buttonPanel.getComponent(i).setVisible(visible);
        }
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#refreshDisplay()
     */
    @Override
    public void refreshDisplay() {
        
        // Reselect the cell that was previously being edited.
        selectCell(getSelectedRow(), getSelectedColumn());
        
        updateTypeIcon();
    }
    
    /** Disable the remove button if a field is not removable/selected */
    @Override
    public void handleCellActivated() {

        super.handleCellActivated();
        enableButtons();
    }
    
    /** Enables or disables the remove button if the selected field can be edited */
    public void enableButtons() {
        
        int selectedRow = getSelectedRow();
        if (selectedRow == -1) {
            selectedRow = rowHeader.getSelectedRow();
        }
        if (selectedRow != -1 && table.getRowCount() > selectedRow && rowHeader.getColumnCount() > 0 && rowHeader.getRowCount() > 0 && rowHeader.isCellEditable(selectedRow, 0)) {
            getDeleteAction().setEnabled(true);
            
        } else {
            getDeleteAction().setEnabled(false);
        }
    }

    /**
     * @see org.openquark.gems.client.valueentry.TableValueEditor#createTableModel(org.openquark.cal.valuenode.ValueNode)
     */
    @Override
    protected ValueEditorTableModel createTableModel(ValueNode valueNode) {
        return new RecordTableModel((AbstractRecordValueNode)valueNode, valueEditorHierarchyManager, verticalLayout);
    }
    
    /**
     * Get a map from every value node managed by this editor to its least constrained type.
     * @return Map from every value node managed by this editor to its least constrained type.
     */
    private Map<ValueNode, TypeExpr> getValueNodeToUnconstrainedTypeMap() {
        
        //Map to hold the result of this function ValueNode->TypeExpr
        final Map<ValueNode, TypeExpr> resultMap = new HashMap<ValueNode, TypeExpr>();
        
        final AbstractRecordValueNode currentValueNode = (AbstractRecordValueNode)getValueNode();
        final List<FieldName> hasFieldNames = ((RecordType)currentValueNode.getTypeExpr()).getHasFieldNames();
        final int numFields = currentValueNode.getTypeExpr().rootRecordType().getNHasFields();

       
        // get the context least constrained type
        final TypeExpr contextLeastConstrainedType = getContext().getLeastConstrainedTypeExpr();
        
        //build one that includes all of the new fields as well.
        final Map<FieldName, TypeExpr> contextFieldsMap; //map of all the fields in the context RecordFieldName -> TypeExpr
        if (contextLeastConstrainedType instanceof RecordType) {
            contextFieldsMap = ((RecordType) contextLeastConstrainedType).getHasFieldsMap();
        } else {
            contextFieldsMap = new HashMap<FieldName, TypeExpr>();
        }

        final Map<FieldName, TypeExpr> allFieldsMap = new HashMap<FieldName, TypeExpr>(); //map containing all the context fields and fields added with the value editor, RecordFieldName -> TypeExpr

        for (int i = 0; i < numFields; i++) {
            final FieldName fieldName = hasFieldNames.get(i);
            if (contextFieldsMap.containsKey(fieldName)) {
                allFieldsMap.put(fieldName, contextFieldsMap.get(fieldName));
            } else {
                allFieldsMap.put(fieldName, TypeExpr.makeParametricType());
            }
        } 
        final RecordType leastConstrainedRecordType = TypeExpr.makePolymorphicRecordType(allFieldsMap);

        
        // add the record node itself to the result map
        resultMap.put(currentValueNode, leastConstrainedRecordType);

        // add all the record field nodes to the result map
        for (int j = 0; j < numFields; j++) {
            ValueNode currentFieldNode = currentValueNode.getValueAt(j);
            FieldName fieldName = hasFieldNames.get(j);
            
            TypeExpr leastConstrainedRecordElementType = leastConstrainedRecordType.getHasFieldType(fieldName);
                            
            resultMap.put(currentFieldNode, leastConstrainedRecordElementType);
        }
       
        return resultMap;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void commitChildChanges(ValueNode oldChild, ValueNode newChild) {
                
        // Get the copy of the current value node, type switched if necessary.
        
        ValueNode oldValueNode = getValueNode();
        AbstractRecordValueNode newValueNode;
        if (!oldChild.getTypeExpr().sameType(newChild.getTypeExpr())) {
            
            // A child is switching type. So calculate new types/values of all our field nodes
            
            Map<ValueNode, TypeExpr> valueNodeToUnconstrainedTypeMap = getValueNodeToUnconstrainedTypeMap();
            Map<ValueNode, ValueNode> commitValueMap = valueEditorManager.getValueNodeCommitHelper().getCommitValues(oldChild, newChild, valueNodeToUnconstrainedTypeMap);
            
            newValueNode = (AbstractRecordValueNode)commitValueMap.get(oldValueNode);

        } else {
            newValueNode = (AbstractRecordValueNode)oldValueNode.copyValueNode();
        }        
        
        // Update the values of the children to match the updated value
        
        List<ValueNode> currentChildrenList = UnsafeCast.<List<ValueNode>>unsafeCast(oldValueNode.getValue());          // unsafe.
        for (int i = 0, listSize = newValueNode.getTypeExpr().rootRecordType().getNHasFields(); i < listSize; i++) {
            if (currentChildrenList.get(i) == oldChild) {
                TypeExpr childType = newValueNode.getValueAt(i).getTypeExpr();
                newValueNode.setValueNodeAt(i, newChild.copyValueNode(childType));
                break;
            }
        }

        // Set the value node
        replaceValueNode(newValueNode, true);
    }
    
    /**
     * @return action for switching layout
     */ 
    private Action getSwitchAction() {
        if (switchAction == null) {
            switchAction = new AbstractAction("SWITCH") {
                                                        
                private static final long serialVersionUID = 1676926437073351056L;

                public void actionPerformed(ActionEvent evt) {
                    
                    int selectedRow = getSelectedRow();
                    int selectedColumn = getSelectedColumn();
                    
                    verticalLayout = !verticalLayout;
                    
                    performUpdatesForSwitch();
                    replaceValueNode(getValueNode(), true);
                    
                    initializeUI();
                    userHasResized();
                    
                    selectCell(selectedColumn, selectedRow);
                }
            };
        }
        return switchAction;
    }
    
    /**
     * Updates UI components after a layout switch is performed
     */
    private void performUpdatesForSwitch() {
        
        // Update the model with the proper layout
        
        setTableModel(createTableModel(getValueNode()));
        
        // Initialize table header
        
        initializeTableCellRenderers();
        
        validate();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void userHasResized() {
     
        if (!verticalLayout) {
            return;
        }
        
        Dimension tableScrollPaneSize = tableScrollPane.getSize();
        int newResizeMode;
        if (tableScrollPaneSize.width >= (75 * table.getColumnCount())) {
            newResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
        } else {
            newResizeMode = JTable.AUTO_RESIZE_OFF;
        }
        if (newResizeMode != table.getAutoResizeMode()) {
            table.setAutoResizeMode(newResizeMode);
            dividerPanel.revalidate();
        }
    }
    
    /**
     * @return action for adding a record field
     */
    private Action getAddAction() {
        if (addAction == null) {
            addAction = new AbstractAction(ValueEditorMessages.getString("VE_AddButtonLabel")) {
                                                        
                private static final long serialVersionUID = 1229365986103881376L;

                public void actionPerformed(ActionEvent evt) {
                    
                    if (!isBasePolymorphicRecord) {
                        System.out.println("Cannot add field - not polymorphic record");
                        return;
                    }
                    
                    // Create a field name which does not conflict with existing or lacks fields
                    
                    List<FieldName> hasFields = getValueNode().getTypeExpr().rootRecordType().getHasFieldNames();
                    Set<FieldName> lacksFields = getValueNode().getTypeExpr().rootRecordType().getLacksFieldsSet();
                    
                    FieldName newFieldName = RecordValueEditor.getNewFieldName(hasFields, lacksFields);
                                                                                                
                    // Create new record type having the existing fields plus a new one
                    
                    Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                    for (final FieldName hasFieldName : hasFields) {
                        TypeExpr hasFieldType = getValueNode().getTypeExpr().rootRecordType().getHasFieldType(hasFieldName);
                        
                        fieldNamesToTypeMap.put(hasFieldName, hasFieldType);
                    }
                    fieldNamesToTypeMap.put(newFieldName, TypeExpr.makeParametricType());

                    RecordType newRecordType = TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
                    
                    replaceValueNode(getValueNode().transmuteValueNode(valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer(), newRecordType), true);
                    
                    // Select the new field
                    
                    int fieldIndex = getValueNode().getTypeExpr().rootRecordType().getHasFieldNames().indexOf(newFieldName);
                    selectCell(fieldIndex, 0);
                }
            };
            addAction.putValue(Action.SHORT_DESCRIPTION, ValueEditorMessages.getString("VE_AddRecordField"));
            
        }
        return addAction;
    }
    
    /**
     * @return action for removing a record field
     */
    private Action getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new AbstractAction(ValueEditorMessages.getString("VE_RemoveButtonLabel")) {
                                                        
                private static final long serialVersionUID = -5743671496229434840L;

                public void actionPerformed(ActionEvent evt) {
                    
                    if (!isBasePolymorphicRecord) {
                        throw new IllegalStateException();
                    }
                    
                    // Determine the index of the deleted field
                    
                    int row = getSelectedRow();
                    int col = getSelectedColumn();
                    if (row == -1) {
                        row = rowHeader.getSelectedRow();
                        col = rowHeader.getSelectedColumn();
                    }
                    if ((row == -1) || (col == -1)) {
                        return;
                    }
                    
                    if (!rowHeader.getModel().isCellEditable(row, 0)) {
                        throw new IllegalStateException();
                    }
                    
                    FieldName fieldName = ((AbstractRecordValueNode)getValueNode()).getFieldName(row);
                    
                    // Create new record type lacking the field
                    
                    Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                    List<FieldName> hasFields = getValueNode().getTypeExpr().rootRecordType().getHasFieldNames();
                    if (!hasFields.remove(fieldName)) {
                        throw new IllegalStateException();
                    }
                    for (final FieldName hasFieldName : hasFields) {
                        TypeExpr hasFieldType = getValueNode().getTypeExpr().rootRecordType().getHasFieldType(hasFieldName);
                        
                        fieldNamesToTypeMap.put(hasFieldName, hasFieldType);
                    }
                    
                    RecordType newRecordType = TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
                    
                    replaceValueNode(getValueNode().transmuteValueNode(valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer(), newRecordType), true);
                    
                    clearSelection(true);
                    enableButtons();
                    
                    clearSelection(false);

                    // Handle selection
                    int rowCount = getRowCount();
                    if (rowCount != 0) {
                        
                        int newSelectedRow = 0;

                        // Update the highlighted/selected row and edited cell.
                        if (rowCount == row) {
                            // Move highlight/selection up one row, since we just deleted the last row.
                            newSelectedRow = row - 1;
                        } else {
                            // Keep the selection in its old row.
                            newSelectedRow = row;
                        }

                        selectCell(newSelectedRow, 0);
                    }
                }
            };
            deleteAction.putValue(Action.SHORT_DESCRIPTION, ValueEditorMessages.getString("VE_RemoveRecordField"));
        }
        return deleteAction;
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setEditable(boolean)
     */
    @Override
    public void setEditable(boolean editable) {
        getAddAction().setEnabled(editable);
        getDeleteAction().setEnabled(editable);
        setButtonPanelVisible(isBasePolymorphicRecord);
    }
    
    /**
     * Updates the icon and tooltip of the editor type icon.
     */
    private void updateTypeIcon() {
        TypeExpr typeExpr = getValueNode().getTypeExpr();
        String iconName = valueEditorManager.getTypeIconName(typeExpr);
        getTypeIcon().setIcon(new ImageIcon(ListTupleValueEditor.class.getResource(iconName)));
        getTypeIcon().setToolTipText("<html><body>" + ToolTipHelpers.wrapTextToHTMLLines(typeExpr.toString(), getTypeIcon()) + "</body></html>");
    }
    
    /**
     * Return the ElementIcon property value.
     * Note: Extra set-up code has been added.
     * @return JLabel
     */
    private JLabel getTypeIcon() {
        if (typeIcon == null) {
            typeIcon = new JLabel();
            typeIcon.setIcon(new ImageIcon(getClass().getResource("/Resources/notype.gif")));
            typeIcon.setBorder(new EmptyBorder(0, 3, 0, 0));
        }
        return typeIcon;
    }
    
    /**
     * Initializes table cell and column renderers according to the layout orientation.
     * @see org.openquark.gems.client.valueentry.TableValueEditor#initializeTableCellRenderers()
     */   
    @Override
    protected void initializeTableCellRenderers() {

        if (verticalLayout) {
            
            // We use the superclass if the layout is vertical
            super.initializeTableCellRenderers();
            return;
        }
        
        TypeExpr typeExpr = getValueNode().getTypeExpr();

        TableColumnModel tableColumnModel = table.getColumnModel();
        TypeExpr[] elementTypeExprArray = ((RecordTableModel)tableModel).getRowElementTypeExprArray();
        boolean displayElementNumber = typeExpr.isListType();
        
        // Set row renderer to have different types for each row
        
        RowCellRenderer cellRenderer = new RowCellRenderer(displayElementNumber, elementTypeExprArray, getValueEditorHierarchyManager().getValueEditorManager());
        cellRenderer.setEditable(isEditable());
        for (int i = 0, colCount = table.getColumnCount(); i < colCount; i++) {
            TableColumn tableColumn = tableColumnModel.getColumn(i);
            tableColumn.setCellRenderer(cellRenderer);
        }
        
        // Set columns to have no header
        
        for (int i = 0, columnCount = tableColumnModel.getColumnCount(); i < columnCount; i++) {

            TableCellRenderer headerRenderer = 
                createTableHeaderRenderer(null);

            TableColumn tableColumn = tableColumnModel.getColumn(i);
            tableColumn.setHeaderRenderer(headerRenderer);            
        }
    }
    
    /**
     * Replace the value node and perform necessary UI updates.
     * @see org.openquark.gems.client.valueentry.ValueEditor#replaceValueNode(org.openquark.cal.valuenode.ValueNode, boolean)
     */
    @Override
    public void replaceValueNode(ValueNode newValueNode, boolean preserveInfo) {

        super.replaceValueNode(newValueNode, preserveInfo);
        
        // Update the field model
        recordFieldModel.initialize((RecordType)getValueNode().getTypeExpr(), getContext().getLeastConstrainedTypeExpr());
        
        // Update the UI
        initializeUI();
        refreshMinMaxDimensions();
        enableButtons();
        updateTypeIcon();
    }
    
    /**
     * A helper function to create a new RecordFieldName that does not duplicate an existing
     * hasField or lacksField. The algorithm is to try successive ordinal field names '#1', '#2', '#3', '#4', ...
     * until a new one is found.
     * @param hasFields 
     * @param lacksFields 
     * @return RecordFieldName
     */
    static FieldName getNewFieldName(Collection<FieldName> hasFields, Collection<FieldName> lacksFields) {
                        
        for (int i = 1; true; ++i) {
            
            FieldName fieldName = FieldName.make(RecordValueEditor.DEFAULT_NEW_FIELD_PREFIX + i);
            
            if (fieldName != null &&
                !hasFields.contains(fieldName) &&
                !lacksFields.contains(fieldName)) {
                
                return fieldName;
            }
        }
    }
}
