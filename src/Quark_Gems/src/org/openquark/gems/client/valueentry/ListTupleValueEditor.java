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
 * ListTupleValueEditor.java
 * Created: Feb 16, 2001
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.AbstractRecordValueNode;
import org.openquark.cal.valuenode.ListOfCharValueNode;
import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.NTupleValueNode;
import org.openquark.cal.valuenode.RecordValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;
import org.openquark.gems.client.GemCutter;


/**
 * ValueEditor for editing lists, lists of tuples and lists of records.
 * @author Michael Cheng
 */
public class ListTupleValueEditor extends AbstractListValueEditor {
    /*
     * TODOEL: unify handling of lists of tuples and lists of records, using AbstractRecordValueNode.
     */

    /**
     * This interface defines what drag operations are supported by the
     * <code>ListTupleValueEditor</code>.  Implementors of this interface will be
     * called when a certain drag operation is initiated by the user.
     */
    public interface ListTupleValueDragPointHandler extends ValueEditorDragPointHandler {
        /**
         * This method defines the behaviour when the user attempts to drag an entire list
         * of objects out from the <code>ListTupleValueEditor</code>.  By default, this
         * method is empty and subclasses are encouraged to override this class to
         * specify their own drag handling code.
         * @param dge
         * @param parentEditor
         * @return boolean
         */
        boolean dragList(DragGestureEvent dge, ListTupleValueEditor parentEditor);

        /**
         * This method defines the behaviour when the user attempts to drag a portion
         * of a tuple from the <code>ListTupleValueEditor</code>.  By default, this
         * method is empty and subclasses are encouraged to override this class to
         * specify their own drag handling code.
         * @param dge
         * @param parentEditor
         * @param tupleElementIndex
         * @return boolean
         */
        boolean dragTupleList(
            DragGestureEvent dge,
            ListTupleValueEditor parentEditor,
            int tupleElementIndex);
    }

    /**
     * A custom value editor provider for the ListTupleValueEditor.
     */
    public static class ListTupleValueEditorProvider extends ValueEditorProvider<ListTupleValueEditor> {

        public ListTupleValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof ListValueNode && canEditListType((ListValueNode) valueNode, providerSupportInfo);
        }

        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public ListTupleValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
                                                 
            ListTupleValueEditor editor = new ListTupleValueEditor(valueEditorHierarchyManager, null);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListTupleValueEditor getEditorInstance(
                ValueEditorHierarchyManager valueEditorHierarchyManager,
                ValueNodeBuilderHelper valueNodeBuilderHelper,
                ValueEditorDragManager dragManager,
                ValueNode valueNode) {
            
            ListTupleValueEditor editor = new ListTupleValueEditor(valueEditorHierarchyManager, getListTupleDragPointHandler(dragManager));
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
         * Checks if the type of the list elements is supported by value editors.
         * @param valueNode the list value node
         * @param providerSupportInfo
         * @return true if the type of the list elements is supported
         */
        private boolean canEditListType(ListValueNode valueNode, SupportInfo providerSupportInfo) {

            TypeConsApp listTypeConsApp = valueNode.getTypeExpr().rootTypeConsApp();
            TypeExpr typeConsArg = listTypeConsApp.getArg(0);
            
            return getValueEditorManager().canEditInputType(typeConsArg, providerSupportInfo);
        }

        /**
         * A convenient method for casting the drag point handler to the type that is
         * suitable for the <code>ListTupleValueEditor</code> to use.  If such conversion is not
         * possible, then this method should return <code>null</code>.
         * @param dragManager
         * @return ListTupleDragPointHandler
         */
        private ListTupleValueDragPointHandler getListTupleDragPointHandler(ValueEditorDragManager dragManager) {
            ValueEditorDragPointHandler handler = getDragPointHandler(dragManager);
            if (handler instanceof ListTupleValueDragPointHandler) {
                return (ListTupleValueDragPointHandler) handler;
            }
            return null;
        }
    }

    private class ListTupleDragGestureListener implements DragGestureListener {
        /**
         * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
         */
        public void dragGestureRecognized(DragGestureEvent dge) {
            if (dge.getTriggerEvent() instanceof MouseEvent) {
                JTableHeader header = getTableHeader();
    
                // If we are currently resizing a column then we shouldn't initiate a drag action
                if (header.getResizingColumn() == null) {
                    TableColumnModel model = header.getColumnModel();
                    TypeExpr listElementType = getListElementType();
                    
                    if (listElementType.rootRecordType() != null) {
                        // A list of values which are records.
                        
                        if (getListElementType().isTupleType()) {
                            int xPos = ((MouseEvent)dge.getTriggerEvent()).getX();
                            dragFromListOfTuples(dge, model.getColumnIndexAtX(xPos));
                        
                        } else {
                            // A list of records which aren't tuples.
                        }
                    
                    } else {
                        // A list of values which aren't records.
                        dragFromList(dge);
                    }
                }
            }
        }

        /**
         * This should only be called if the type expression is a list of elements.
         * It will initiate a drag event appropriate for dragging the data out of the value editor.
         * @param dge DragGestureEvent - The gesture that started it all
         */        
        private void dragFromList(DragGestureEvent dge) {
            if (dragPointHandler != null) {
                dragPointHandler.dragList(dge, ListTupleValueEditor.this);
            }
        }
        
        /**
         * This should only be called for a list of tuples.  It will initiates a drag event appropriate
         * for dragging the specified tuple element of the tuple
         * @param dge DragGestureEvent - The gesture that started it all
         * @param element int - The tuple index to be dragged out
         */
        private void dragFromListOfTuples(DragGestureEvent dge, int element) {
            if (dragPointHandler != null) {
                dragPointHandler.dragTupleList(dge, ListTupleValueEditor.this, element);
            }
        }
    }
    
    /**
     * Renderer used to show empty table cells. 
     * Specifically, this is used to render the unconsolidated column cells for a record with
     * no fields. 
     * @author Iulian Radu
     */
    static class EmptyCellRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = 857633457177524459L;

        EmptyCellRenderer () {
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            
            if (isSelected) {
                setBackground(Color.LIGHT_GRAY);
            } else {
                setBackground(UIManager.getColor("Panel.background"));
            }
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    // Allow this value editor to initiate drag events
    private final ListTupleValueDragPointHandler dragPointHandler;
    
    /** Icon for the consolidate columns button */
    private final static ImageIcon consolidateColumnsIcon = new ImageIcon(GemCutter.class.getResource("/Resources/consolidateColumns.gif"));
    
    /** Icon for the add field button */
    private final static ImageIcon addColumnIcon = new ImageIcon(GemCutter.class.getResource("/Resources/addColumn.gif"));
    
    /** Icon for the remove field button */
    private final static ImageIcon deleteColumnIcon = new ImageIcon(GemCutter.class.getResource("/Resources/deleteColumn.gif"));
    
    /** 
     * Whether columns are being consolidated.  Only applicable (and potentially true) if editing a list of records or tuples.
     * If True, the list elements appear in one column; if False, tuple and record fields appear in
     * separate columns. 
     */
    private boolean consolidateColumns = false;
    
    /** Whether the editor has been initialized once */
    private boolean initializedOnce;
    
    /** Button for consolidating columns */
    private JButton consolidateColumnsButton;
    
    /** Button for adding a new record field */
    private JButton addFieldButton;
    
    /** Button for removing a record field */
    private JButton deleteFieldButton;
    
    /** Action of setting the column consolidation */
    private Action switchConsolidationAction;
    
    /** Action of adding a record field */
    private Action addFieldAction;
    
    /** Action of removing a record field */
    private Action deleteFieldAction;
    
    /** Editable header used when the table represents a list of records in non-consolidated column layout*/
    private RecordValueEditor.EditableHeader editableRecordHeader = null;
        
    /**
     * ListTupleValueEditor constructor comment.
     * @param valueEditorHierarchyManager the hierarchy manager for the editor
     * @param dragPointHandler a drag point handler if drag and drop should be enabled (can be null)
     */
    protected ListTupleValueEditor(final ValueEditorHierarchyManager valueEditorHierarchyManager, ListTupleValueDragPointHandler dragPointHandler) {
        super(valueEditorHierarchyManager);
        this.dragPointHandler = dragPointHandler;

        // Attach a listener for drag events
        
        if (dragPointHandler != null) {
            JTableHeader tableHeader = getTableHeader();
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                tableHeader,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new ListTupleDragGestureListener());
        }
        
        // Initialize our buttons
        
        getConsolidateColumnsButton().addKeyListener(new AbstractListValueEditor.ListTupleValueEditorKeyListener());
        getAddFieldButton().addKeyListener(new AbstractListValueEditor.ListTupleValueEditorKeyListener());
        getDeleteFieldButton().addKeyListener(new AbstractListValueEditor.ListTupleValueEditorKeyListener());
        getConsolidateColumnsButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        getAddFieldButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        getDeleteFieldButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        // Initialize the table model to create columns with editable headers
        
        table.setColumnModel(new RecordValueEditor.EditableHeader.EditableHeaderTableColumnModel());
        
        // Create the list of records editable header and record model 
        // (these are created here but only used for lists of records)
        
        RecordValueEditor.RecordFieldModel recordFieldModel = new RecordValueEditor.RecordFieldModel();
        RecordValueEditor.RowHeader.RowHeaderOwner rowHeaderOwner = new RecordValueEditor.RowHeader.RowHeaderOwner() {
            
            public TypeExpr getFieldType(int row) {
                FieldName fieldName = (getListElementType().rootRecordType().getHasFieldNames().get(row));
                return getListElementType().rootRecordType().getHasFieldType(fieldName);
            }
            
            public void renameField(FieldName oldName, FieldName newName) {
                
                if (getListElementType().rootRecordType() == null) {
                    throw new IllegalStateException();
                }
                
                ListValueNode listValueNode = (ListValueNode)getValueNode();
                int listSize = listValueNode.getNElements();
                
                if (listSize > 0 && listValueNode.getValueAt(0) instanceof NTupleValueNode) {
                    // list of tuple value nodes.
                    
                    // Check if renaming causes a list of tuple to be converted to a list of record.
                    // True if renaming a list of tuple, unless the name really didn't change.
                    if (!oldName.equals(newName)) {
                        
                        // Have to convert a list of NTuples to a list of records.
                        
                        // Note: It might be better to move some of this code to nTupleValueNode.transmute(),
                        //   or ValueNodeTransformer.transform().  This duplicates some work though.
                        
                        ValueNodeBuilderHelper valueNodeBuilderHelper = 
                            valueEditorHierarchyManager.getValueEditorManager().getValueNodeBuilderHelper();
                        
                        RecordValueNode.RecordValueNodeProvider recordVNProvider = 
                            new RecordValueNode.RecordValueNodeProvider(valueNodeBuilderHelper);
                        
                        // Get the new record type.
                        TypeExpr recordTypeExpr = AbstractRecordValueNode.getRecordTypeForRenamedField(getListElementType(), oldName, newName);
                        
                        // Iterate through the list and convert tuple nodes to record nodes.
                        for (int i = 0; i < listSize; i++) {
                            
                            // Get the tuple node.
                            NTupleValueNode nTupleValueNode = (NTupleValueNode)listValueNode.getValueAt(i);
                            
                            // Get the list of values.
                            List<ValueNode> fieldValueList = new ArrayList<ValueNode>(nTupleValueNode.getValue());
                            
                            // Create and set the record value node with the list of values.
                            ValueNode recordValueNode = recordVNProvider.getNodeInstance(fieldValueList, null, recordTypeExpr);
                            ((ListValueNode)getValueNode()).setValueNodeAt(i, recordValueNode);
                        }
                    
                    } else {
                        // No conversion is necessary.
                    }
                    
                } else {
                    // Empty list, or list of record value nodes.
                    
                    // Iterate through the list of record nodes and change their type expressions
                    
                    for (int i = 0; i < listSize; i++) {
                        
                        AbstractRecordValueNode recordValueNode = (AbstractRecordValueNode)((ListValueNode)getValueNode()).getValueAt(i);
                        recordValueNode = recordValueNode.renameField(oldName, newName, valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer());
                        ((ListValueNode)getValueNode()).setValueNodeAt(i, recordValueNode);
                    }
                    
                }
                
                // Now change the list element type expression and update the value node
                
                Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                List<FieldName> existingFields = getListElementType().rootRecordType().getHasFieldNames();
                for (final FieldName existingFieldName : existingFields) {
                    TypeExpr existingFieldType = getListElementType().rootRecordType().getHasFieldType(existingFieldName);
                    
                    fieldNamesToTypeMap.put(existingFieldName, existingFieldType);
                }
                TypeExpr fieldTypeExpr = fieldNamesToTypeMap.remove(oldName);
                fieldNamesToTypeMap.put(newName, fieldTypeExpr);
                
                RecordType newRecordType = TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
                ValueNodeBuilderHelper valueNodeBuilderHelper = valueEditorManager.getValueNodeBuilderHelper();
                replaceValueNode(getValueNode().transmuteValueNode(valueNodeBuilderHelper, valueEditorManager.getValueNodeTransformer(),
                    valueNodeBuilderHelper.getPreludeTypeConstants().makeListType(newRecordType)), true);
                
                // Select the column of the new field name
                
                int fieldIndex = getListElementType().rootRecordType().getHasFieldNames().indexOf(newName);
                if (getRowCount() > 0) {
                    selectCell(0, fieldIndex);
                }
            }
        };
        editableRecordHeader = new RecordValueEditor.EditableHeader(table.getColumnModel(), this, rowHeaderOwner, recordFieldModel);
        editableRecordHeader.addFocusListener(new FocusListener() {
            
            public void focusGained(FocusEvent e) {
                ListTupleValueEditor.this.clearSelection(true);
                enableButtonsForTableState();
            }
            
            public void focusLost(FocusEvent e) {
            }
        });
    }

    /**
     * @see org.openquark.gems.client.valueentry.AbstractListValueEditor#createListTableModel(org.openquark.cal.valuenode.ValueNode)
     */
    @Override
    protected AbstractListTableModel createListTableModel(ValueNode valueNode) {
        return new ListTupleTableModel((ListValueNode) valueNode, valueEditorHierarchyManager, consolidateColumns);
    }

    /**
     * Get a map from every value node managed by this editor to its least constrained type.
     * @return Map from every value node managed by this editor to its least constrained type.
     */
    private Map<ValueNode, TypeExpr> getValueNodeToUnconstrainedTypeMap() {
        
        Map<ValueNode, TypeExpr> returnMap = new HashMap<ValueNode, TypeExpr>();
        
        // Get the value nodes for the list and the items in the list.
        ListValueNode listValueNode = (ListValueNode)getValueNode();
        List<ValueNode> listElementNodes = listValueNode.getValue();
        
        // Determine whether we are dealing with a generic list, list of tuples or list of records
        ValueNode firstChild = listElementNodes.get(0);
        boolean isListOfNTupleVNs = firstChild instanceof NTupleValueNode;        // isListRecord will also be true.
        
        boolean isListRecord = getListElementType().rootRecordType() != null;
        int tupleSize = isListOfNTupleVNs ? ((NTupleValueNode)listElementNodes.get(0)).getTupleSize() : -1;

        // Calculate new unconstrained types for the list and the elements of the list.
        TypeExpr unconstrainedListType = getContext().getLeastConstrainedTypeExpr();
        TypeExpr unconstrainedListElementType;

        if (unconstrainedListType.rootTypeVar() != null || 
            (!consolidateColumns && isListRecord && unconstrainedListType.rootTypeConsApp().getArg(0).rootTypeVar() != null)) {  // check for [a]

            // The context is parametric (ie: a or [a]), so create the least constrained type
            // depending on the list element type:
            
            if (!consolidateColumns && isListOfNTupleVNs) {
            
                // Tuple least constrained type: (a, a, ... )
                unconstrainedListElementType = TypeExpr.makeTupleType(tupleSize);
                
            } else if (!consolidateColumns && isListRecord) {
            
                // Record least constrained type: {field1 = a, field2 = b, ...}
                
                // Build this from the existing fields
                List<FieldName> fieldNames = getListElementType().rootRecordType().getHasFieldNames();
                Map<FieldName, TypeExpr> fieldNameToTypeMap = new HashMap<FieldName, TypeExpr>();
                for (final FieldName fieldName : fieldNames) {
                    fieldNameToTypeMap.put(fieldName, TypeExpr.makeParametricType());
                }
                
                unconstrainedListElementType = TypeExpr.makeNonPolymorphicRecordType(fieldNameToTypeMap);
                
            } else {
            
                // General element least constrained type: (a) 
                unconstrainedListElementType = TypeExpr.makeParametricType(); 
            }
            
            unconstrainedListType = valueEditorManager.getValueNodeBuilderHelper().getPreludeTypeConstants().makeListType(unconstrainedListElementType);

        } else {
        
            // Element type expression is bound to context
            unconstrainedListElementType = unconstrainedListType.rootTypeConsApp().getArg(0);

            //first build a field map containing all the fields from the context
            final Map<FieldName, TypeExpr> fieldMap; //map of all the fields in the context RecordFieldName -> TypeExpr
            if (unconstrainedListElementType instanceof RecordType) {
                fieldMap = ((RecordType) unconstrainedListElementType).getHasFieldsMap();
            } else {
                fieldMap = new HashMap<FieldName, TypeExpr>();
            }

            //add all the additional fields from the editor
            if (firstChild.getTypeExpr() instanceof RecordType ) {
                RecordType elementType = (RecordType ) firstChild.getTypeExpr(); // unconstrainedListElementType;
                for (int i = 0; i < elementType.getNHasFields(); i++) {
                    final FieldName fieldName = elementType.getHasFieldNames().get(i);
                    if (!fieldMap.containsKey(fieldName)) {
                        fieldMap.put(fieldName, TypeExpr.makeParametricType());
                    }
                } 
            }
            
            //make sure the element type is actually supposed to be a record - otherwise just use generic type
            if (!(firstChild.getTypeExpr() instanceof RecordType) &&
                !  (unconstrainedListElementType instanceof RecordType)) {
                unconstrainedListElementType = TypeExpr.makeParametricType();
            } else {
                unconstrainedListElementType = TypeExpr.makeNonPolymorphicRecordType(fieldMap);
            }
            unconstrainedListType = valueEditorManager.getValueNodeBuilderHelper().getPreludeTypeConstants().makeListType(unconstrainedListElementType);            
        }
        
        
        // Populate the map
        
        // List
        returnMap.put(listValueNode, unconstrainedListType);

        // List items
        for (final ValueNode listElementNode : listElementNodes) {//.iterator(); it.hasNext(); ) {
            returnMap.put(listElementNode, unconstrainedListElementType);
            
            if (!consolidateColumns) {
                
                // For tuple and record lists, we have to also put the individual elements of the list items 
                
                if (isListOfNTupleVNs) {
                    
                    NTupleValueNode currentTupleValue = (NTupleValueNode)listElementNode;
                    RecordType unconstrainedListElementRecordType = (RecordType)unconstrainedListElementType;
                    Map<FieldName, TypeExpr>  hasFieldsMap = unconstrainedListElementRecordType.getHasFieldsMap();
                    int j = 0;
                    for (final TypeExpr unconstrainedTupleElementType : hasFieldsMap.values()) {
                        ValueNode currentTupleItem = currentTupleValue.getValueAt(j);
                        returnMap.put(currentTupleItem, unconstrainedTupleElementType);
                        ++j;
                    }
                    
                } else if (isListRecord) {
                    
                    List<FieldName> contextRecordFieldNames = ((RecordType)unconstrainedListElementType).getHasFieldNames();
                    RecordValueNode currentRecordValue = (RecordValueNode)listElementNode;
                    for (int j = 0, n = currentRecordValue.getNFieldNames(); j < n; j++) {
                        ValueNode currentFieldItem = currentRecordValue.getValueAt(j);
                        
                        TypeExpr unconstrainedFieldType;
                        if (contextRecordFieldNames.contains(currentRecordValue.getFieldName(j))) {
                            
                            unconstrainedFieldType = ((RecordType)unconstrainedListElementType).getHasFieldType(currentRecordValue.getFieldName(j));
                            
                        } else {
                        
                            // This field does not exist in the context; then it was added to our list type 
                            // while the node was bound.
            
                            // TODO: This causes failure during type switching; one suggestion is to "grow"
                            // the context record here by adding this field name associated to a parametric type. 
                                             
                            unconstrainedFieldType = TypeExpr.makeParametricType();
                        }
                        
                        returnMap.put(currentFieldItem, unconstrainedFieldType);
                    }
                }
            }
        }
        
        return returnMap;
    }

    private static final long serialVersionUID = 8120550072675651096L;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void commitChildChanges(ValueNode oldChild, ValueNode newChild) {
        
        // Get the copy of the current value node, type switched if necessary.
        
        ListValueNode oldValueNode = (ListValueNode)getValueNode();
        ListValueNode newValueNode;
        if (!oldChild.getTypeExpr().sameType(newChild.getTypeExpr())) {
    
            Map<ValueNode, TypeExpr> valueNodeToUnconstrainedTypeMap = getValueNodeToUnconstrainedTypeMap();
            Map<ValueNode, ValueNode> commitValueMap = valueEditorManager.getValueNodeCommitHelper().getCommitValues(oldChild, newChild, valueNodeToUnconstrainedTypeMap);
            
            PreludeTypeConstants typeConstants = valueEditorManager.getPreludeTypeConstants();
            TypeExpr charType = typeConstants.getCharType();
            ValueNode newValueNodeFromMap = commitValueMap.get(oldValueNode);

            // HACK: if it's a ListOfCharValueNode, convert it to a ListValueNode.
            //   What we need is a way to guarantee the type of value node that is returned by getCommitValues().
            //   This is not possible with the current form of transmuteValueNode() though.
            if (newValueNodeFromMap instanceof ListOfCharValueNode) {
                ListOfCharValueNode charListValueNode = (ListOfCharValueNode)newValueNodeFromMap;
                char[] charListValueArray = charListValueNode.getStringValue().toCharArray();

                ArrayList<ValueNode> newListValue = new ArrayList<ValueNode>(charListValueArray.length);
                for (final char charListValue : charListValueArray) {
                    newListValue.add(new LiteralValueNode(Character.valueOf(charListValue), charType));
                }
                
                newValueNode = new ListValueNode(newListValue, typeConstants.getCharListType(), new LiteralValueNode(new Character('a'), charType));
                replaceValueNode(newValueNode, true);
                return;

            } else {
                newValueNode = (ListValueNode)newValueNodeFromMap;
            }

        } else {
            newValueNode = (ListValueNode)oldValueNode.copyValueNode();
        }        
        
        
        // Modify the new value node so that the old child is replaced by the new child.
        // Note that the cell editor may now be editing a different row so we have to search for the row that changed
        //   This can happen if one clicks from an editor for one cell to another cell 
        //   (eg. in a list of colours, from a colour value editor for one cell, onto the cell editor for another cell.)

        List<ValueNode> oldChildrenList = oldValueNode.getValue();
        List<ValueNode> newChildrenList = newValueNode.getValue();

        boolean isListRecord = getListElementType().rootRecordType() != null;
        
        if (consolidateColumns || !isListRecord) {
            for (int i = 0, listSize = newValueNode.getNElements(); i < listSize; i++) {
                if (oldChildrenList.get(i) == oldChild) {
                    TypeExpr childType = (newChildrenList.get(i)).getTypeExpr();
                    newValueNode.setValueNodeAt(i, newChild.copyValueNode(childType));
                    break;
                }
            }

        } else {
            // isListRecord == true, ie. must be a list of records (or tuples).
            AbstractRecordValueNode firstChild = (AbstractRecordValueNode)oldChildrenList.get(0);
            
            // To find the child that changed, for each value in the list, have to iterate through the tuple/record.
            int listSize = newValueNode.getNElements();
            
            int childrenSize = firstChild.getNFieldNames(); 

            // TODO: Note that new record value nodes may have more/less fields than the old nodes,
            // because of type switch
            
          listItem:
            for (int i = 0; i < listSize; i++) {
                AbstractRecordValueNode currentValue = (AbstractRecordValueNode)oldChildrenList.get(i);

                for (int j = 0; j < childrenSize; j++) {
                    if (currentValue.getValueAt(j) == oldChild) {
                        AbstractRecordValueNode newListItemValue = (AbstractRecordValueNode)newChildrenList.get(i);
                        newListItemValue.setValueNodeAt(j, newChild.copyValueNode());
                        break listItem;
                    }
                }
            }
        }

        // Set the value node
        replaceValueNode(newValueNode, true);
        
        // Update UI
        userHasResized();
    }
    
    /**
     * @return the action for switching consolidating of columns
     */
    protected Action getSwitchConsolidationAction() {
        if (switchConsolidationAction == null) {
            switchConsolidationAction = new AbstractAction("SwitchConsolidation") {
                
                private static final long serialVersionUID = -2706186354624718070L;

                public void actionPerformed(ActionEvent evt) {
                    
                    // Perform switch
                    consolidateColumns = !consolidateColumns;
                    ((ListTupleTableModel)table.getModel()).setConsolidatingColumns(consolidateColumns);
                    
                    // Update UI
                    updateColumnSizes();
                    initializeTableCellRenderers();
                    enableButtonsForTableState();
                }
            };
            switchConsolidationAction.putValue(Action.SHORT_DESCRIPTION, ValueEditorMessages.getString("VE_ColumnConsolidation"));
        }
        return switchConsolidationAction;
    }
    
    /**
     * @return the action for adding a new record field
     */
    protected Action getAddFieldAction() {
        if (addFieldAction == null) {
            addFieldAction = new AbstractAction("AddField") {
                
                private static final long serialVersionUID = -4498524901433254375L;

                public void actionPerformed(ActionEvent evt) {
                    
                    boolean isBasePolymorphicRecord = !isContextNonRecordPolymorphic();
                    if (!isBasePolymorphicRecord) {
                        throw new IllegalStateException("Cannot add field to non-polymorphic record");
                    }
                    
                    // Find a name for the field, which does not conflict with the existing has or lacks fields
                    
                    List<FieldName> hasFields = getListElementType().rootRecordType().getHasFieldNames();
                    Set<FieldName> lacksFields = getListElementType().rootRecordType().getLacksFieldsSet();
                    
                    FieldName newFieldName = RecordValueEditor.getNewFieldName(hasFields, lacksFields);                   
                    
                    // Create new record type
                    
                    Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                    for (final FieldName hasFieldName : hasFields) {
                        TypeExpr hasFieldType = getListElementType().rootRecordType().getHasFieldType(hasFieldName);
                        
                        fieldNamesToTypeMap.put(hasFieldName, hasFieldType);
                    }
                    fieldNamesToTypeMap.put(newFieldName, TypeExpr.makeParametricType());

                    RecordType newRecordType = TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
                    ValueNodeBuilderHelper valueNodeBuilderHelper = valueEditorManager.getValueNodeBuilderHelper();
                    replaceValueNode(getValueNode().transmuteValueNode(valueNodeBuilderHelper, valueEditorManager.getValueNodeTransformer(),
                        valueNodeBuilderHelper.getPreludeTypeConstants().makeListType(newRecordType)), true);
                    
                    // Update column sizes after switch
                    
                    userHasResized();
                    
                    // Select the new field
                    int fieldIndex = getListElementType().rootRecordType().getHasFieldNames().indexOf(newFieldName);
                    if (getRowCount() > 0) {
                        selectCell(0, fieldIndex);
                    }
                }
            };
            addFieldAction.putValue(Action.SHORT_DESCRIPTION, ValueEditorMessages.getString("VE_AddRecordField"));
        }
        return addFieldAction;
    }
    
    /**
     * @return the action of removing a record field
     */
    protected Action getDeleteFieldAction() {
        if (deleteFieldAction == null) {
            deleteFieldAction = new AbstractAction("DeleteField") {
                
                private static final long serialVersionUID = -1055163203967443486L;

                public void actionPerformed(ActionEvent evt) {
                    
                    boolean isBasePolymorphicRecord = !isContextNonRecordPolymorphic();
                    if (!isBasePolymorphicRecord) {
                        throw new IllegalStateException("Cannot add field to non-polymorphic record");
                    }
                    
                    int col = getSelectedColumn();
                    
                    if (col == -1) {
                        return;
                    }
                    
                    List<FieldName> existingFields = getListElementType().rootRecordType().getHasFieldNames();
                    FieldName fieldName = existingFields.get(col);
                    
                    // Create new record type lacking the field
                    
                    Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                    if (!existingFields.remove(fieldName)) {
                        throw new IllegalStateException();
                    }
                    for (final FieldName existingFieldName : existingFields) {
                        TypeExpr existingFieldType = getListElementType().rootRecordType().getHasFieldType(existingFieldName);
                        fieldNamesToTypeMap.put(existingFieldName, existingFieldType);
                    }
                    RecordType newRecordType = TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
                    
                    ValueNodeBuilderHelper valueNodeBuilderHelper = valueEditorManager.getValueNodeBuilderHelper();
                    replaceValueNode(getValueNode().transmuteValueNode(valueNodeBuilderHelper, valueEditorManager.getValueNodeTransformer(),
                        valueNodeBuilderHelper.getPreludeTypeConstants().makeListType(newRecordType)), true);
                    
                    userHasResized();
                    
                    // Handle selection
                    int colCount = getColumnCount();
                    if (colCount != 0) {
                        
                        int newSelectedCol = 0;

                        // Update the highlighted/selected column and edited cell.
                        if (colCount == col) {
                            // Move highlight/selection left one column, since we just deleted the last column.
                            newSelectedCol = col - 1;
                        } else {
                            // Keep the selection in its old column.
                            newSelectedCol = col;
                        }

                        selectCell(getSelectedRow(), newSelectedCol);
                    }
                }
            };
            deleteFieldAction.putValue(Action.SHORT_DESCRIPTION, "Remove Record Field");
        }
        return deleteFieldAction;
    }
    
    /**
     * @return the button for adding a record field
     */
    protected JButton getAddFieldButton() {
        if (addFieldButton == null) {
            addFieldButton = new JButton(getAddFieldAction());
            addFieldButton.setName("AddFieldButton");
            addFieldButton.setText("");
            addFieldButton.setIcon(addColumnIcon);
            addFieldButton.setMargin(new Insets(0, 0, 0, 0));
        }
        return addFieldButton;
    }
    
    /**
     * @return the button for removing a record field
     */
    protected JButton getDeleteFieldButton() {
        if (deleteFieldButton == null) {
            deleteFieldButton = new JButton(getDeleteFieldAction());
            deleteFieldButton.setName("DeleteFieldButton");
            deleteFieldButton.setText("");
            deleteFieldButton.setIcon(deleteColumnIcon);
            deleteFieldButton.setMargin(new Insets(0, 0, 0, 0));
        }
        return deleteFieldButton;
    }
    
    /**
     * @return the button for consolidating columns
     */
    protected JButton getConsolidateColumnsButton() {
        if (consolidateColumnsButton == null) {
            consolidateColumnsButton = new JButton(getSwitchConsolidationAction());
            consolidateColumnsButton.setName("ConsolidateColumnsButton");
            consolidateColumnsButton.setMnemonic('u');
            consolidateColumnsButton.setText("");
            consolidateColumnsButton.setIcon(consolidateColumnsIcon);
            consolidateColumnsButton.setMargin(new Insets(0, 0, 0, 0));
        }
        return consolidateColumnsButton;
    }
    
    /**
     * @return a new panel for the east side of the editor, containing arrows and field addition buttons
     */
    @Override
    protected JPanel createEastPanel() {
        
        JPanel ivjEastPanel = new JPanel();
        ivjEastPanel.setName("EastPanel");
        ivjEastPanel.setBorder(new EtchedBorder());
        ivjEastPanel.setLayout(new FlowLayout());
        
        JPanel ivjArrowPanel = new JPanel();
        ivjArrowPanel.setName("ArrowPanel");
        ivjArrowPanel.setLayout(new BoxLayout(ivjArrowPanel, BoxLayout.Y_AXIS));
        
        ivjArrowPanel.setLayout(new GridBagLayout());
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.weightx = 0.0;
            constraints.weighty = 0.0;
            constraints.insets = new Insets(0,0,2,0);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            ivjArrowPanel.add(getAddFieldButton(), constraints);
        }
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.weightx = 0.0;
            constraints.weighty = 0.0;
            constraints.insets = new Insets(2,0,10,0);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            ivjArrowPanel.add(getDeleteFieldButton(), constraints);
        }
        
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 1;
            constraints.weightx = 0.0;
            constraints.weighty = 0.0;
            constraints.insets = new Insets(2,0,2,0);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            ivjArrowPanel.add(getUpButton(), constraints);
        }
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 1;
            constraints.weightx = 0.0;
            constraints.weighty = 0.0;
            constraints.insets = new Insets(2,0,2,0);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            ivjArrowPanel.add(getDownButton(), constraints);
        }
        
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 1;
            constraints.weightx = 0.0;
            constraints.weighty = 0.0;
            constraints.insets = new Insets(10,0,10,0);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            ivjArrowPanel.add(getConsolidateColumnsButton(), constraints);
        }
        
        ivjEastPanel.add(ivjArrowPanel, ivjArrowPanel.getName());
        return ivjEastPanel;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    protected void initializeTableCellRenderers() {
        
        // Initialize table header and cell renderers
        
        updateTableHeader();
        super.initializeTableCellRenderers();
        
        // And reset column header renderers (these are set to default renderer by initializeTableCellRenderers)
        
        if (!consolidateColumns && getListElementType().rootRecordType() != null) {
            if (getListElementType().rootRecordType().getNHasFields() == 0) {
                
                TableCellRenderer emptyCellRenderer = new EmptyCellRenderer();
                TableColumnModel columnModel = table.getColumnModel();
                for (int i = 0, n = columnModel.getColumnCount(); i < n; i++ ) {
                    TableColumn column = columnModel.getColumn(i);
                    column.setCellRenderer(emptyCellRenderer);
                }
                
            } else {
                editableRecordHeader.setColumnModel(table.getColumnModel());
            }
        }
    }
    
    /**
     * Overwrites method to enable/disable buttons for record field modification.
     * @see org.openquark.gems.client.valueentry.AbstractListValueEditor#enableButtonsForTableState()
     */
    @Override
    protected void enableButtonsForTableState() {
        super.enableButtonsForTableState();
        
        boolean isRecordList = getListElementType().rootRecordType() != null;
        boolean isTupleList = getListElementType().isTupleType();
        
        getAddFieldButton().setVisible(isRecordList);
        getDeleteFieldButton().setVisible(isRecordList);
        
        if (isRecordList) {
            getAddFieldAction().setEnabled(!consolidateColumns);
            getDeleteFieldAction().setEnabled(!consolidateColumns);
        }
        
        getConsolidateColumnsButton().setVisible(isRecordList || isTupleList);
        
        if (!consolidateColumns && getListElementType().rootRecordType() != null) {
            enableRecordFieldAddRemoveButtons();
        }
    }
    
    /** Enables or disables the remove button if a field can be edited */
    protected void enableRecordFieldAddRemoveButtons() {
        
        if (getListElementType().rootRecordType() == null) {
            throw new IllegalStateException("Attempting to enable column add/remove buttons for non-record type");
        }
        
        boolean isBasePolymorphicRecord = !isContextNonRecordPolymorphic();
        
        getDeleteFieldAction().setEnabled(isBasePolymorphicRecord);
        getAddFieldAction().setEnabled(isBasePolymorphicRecord);
        
        int selectedFieldIndex = getSelectedColumn();
        if ( getListElementType().rootRecordType().getNHasFields() != 0 &&
             selectedFieldIndex != -1 && 
             selectedFieldIndex < getColumnCount() && 
             ((RecordValueEditor.EditableHeader)table.getTableHeader()).isCellEditable(selectedFieldIndex)) {
            
            getDeleteFieldAction().setEnabled(true);
        } else {
            getDeleteFieldAction().setEnabled(false);
        }
    }
    
    /**
     * @return whether the context is a list of non-record-polymoprhic records.
     * 
     * Ex: if the context is [(r\a) => {r | age::a}], returns False
     * Ex: if the context is [a], returns True
     */
    private boolean isContextNonRecordPolymorphic() {
        TypeExpr leastConstrainedContextType = getContext().getLeastConstrainedTypeExpr();
        return (leastConstrainedContextType.rootTypeConsApp() != null &&
            leastConstrainedContextType.rootTypeConsApp().getArg(0).rootRecordType() != null &&
            !leastConstrainedContextType.rootTypeConsApp().getArg(0).rootRecordType().isRecordPolymorphic());
    }
    
    /**
     * @return index of the field currently selected (either through the table or header)
     */
    @Override
    protected int getSelectedColumn() {
        
        int selectedIndex = super.getSelectedColumn();
        if (selectedIndex == -1) {
            
            // No cell selected; the column header editor may be selected
            JTableHeader tableHeader = table.getTableHeader(); 
            if (tableHeader instanceof RecordValueEditor.EditableHeader) {
                selectedIndex = ((RecordValueEditor.EditableHeader)tableHeader).getSelectedColumn();
            }
        }
        return selectedIndex;
    }
    
    /**
     * Performs updates to the table header, enabling editing only for lists of records.
     */
    protected void updateTableHeader() {
        
        JTableHeader newHeader;
        if (!consolidateColumns && getListElementType().rootRecordType() != null && getListElementType().rootRecordType().getNHasFields() > 0) {
            
            // Table is modeling a list of records, with non-consolidated columns which should have
            // editable headers
            
            TypeExpr leastConstrainedType = getContext().getLeastConstrainedTypeExpr();
            if (leastConstrainedType != null && leastConstrainedType.rootTypeConsApp() != null) {
                leastConstrainedType = leastConstrainedType.rootTypeConsApp().getArg(0).rootRecordType();
            }
            editableRecordHeader.getRecordFieldModel().initialize(((TypeConsApp)getValueNode().getTypeExpr()).getArg(0).rootRecordType(), leastConstrainedType);
            
            newHeader = editableRecordHeader;
            
        } else {
            
            // Table header should be non editable
            
            newHeader = new JTableHeader(table.getColumnModel());
            newHeader.setReorderingAllowed(false);
        }
        
        table.setTableHeader(newHeader);
        tableScrollPane.setColumnHeaderView(newHeader);
        newHeader.repaint();
        
        if (dragPointHandler != null) {
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                newHeader,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new ListTupleDragGestureListener());
        }

    }
    
    /**
     * Perform UI updates necessary when setting initial value.
     * 
     * Note: If the list elements are records which are non-record-polymorphic 
     * then this method ensures that the type becomes record-polymorphic if the context allows.
     *   Ex: If the list type is [{age::Double}] and context is [a], the type is transmuted to (r\age)=>[{r|age::Double}]
     * 
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {
        
        super.setInitialValue();
        
        if (getListElementType().rootRecordType() != null) {
            
            // If we have a list of records in an unconstrained context, make sure the list element types
            // are not record polymorphic
            // This is an allowed operation because we are actually specializing the list element type.
            
            // TODO: Enforce non-record-polymorphic regardless of context.
            // The CAL value produced by the value node is always non-record-polymorphic, thus the
            // type of the list value node should be consistent with this. 
            //
            // This is currently not possible because the type switching mechanism does not
            // properly update a record type switch form record-polymorphic to non-record-polymorphic.
            
            TypeExpr unconstrainedListType = getContext().getLeastConstrainedTypeExpr();
            
            if (unconstrainedListType.rootTypeVar() != null || 
                unconstrainedListType.rootTypeConsApp().getArg(0).rootTypeVar() != null) {

                // Not constrained by context, so create new type expression according to the fields we have
                
                if (getListElementType().rootRecordType().isRecordPolymorphic()) {
                
                    Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
                    List<FieldName> existingFields = getListElementType().rootRecordType().getHasFieldNames();
                    for (final FieldName existingFieldName : existingFields) {
                        TypeExpr existingFieldType = getListElementType().rootRecordType().getHasFieldType(existingFieldName);
                        fieldNamesToTypeMap.put(existingFieldName, existingFieldType);
                    }
                    
                    RecordType newRecordType = TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
                    
                    ValueNodeBuilderHelper valueNodeBuilderHelper = valueEditorManager.getValueNodeBuilderHelper();
                    replaceValueNode(getValueNode().transmuteValueNode(valueNodeBuilderHelper, valueEditorManager.getValueNodeTransformer(),
                        valueNodeBuilderHelper.getPreludeTypeConstants().makeListType(newRecordType)), true);
                    notifyValueChanged(getValueNode());
                }
            }
        }
        
        updateTableHeader();
        enableButtonsForTableState();
        
        if (!initializedOnce) {
            initializedOnce = true;
        }
    }
    
    /**
     * Overwrite loadSavedSize() to resize columns only if the editor has not been 
     * previously initialized.
     * 
     * @see org.openquark.gems.client.valueentry.TableValueEditor#loadSavedSize(org.openquark.gems.client.valueentry.ValueEditor.Info)
     */
    @Override
    protected void loadSavedSize(ValueEditor.Info sizeInfo) {
        if (initializedOnce) {
            return;
        }
        super.loadSavedSize(sizeInfo);
    }
}
