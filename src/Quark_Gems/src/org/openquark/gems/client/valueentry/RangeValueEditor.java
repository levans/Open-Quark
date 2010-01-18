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
 * RangeValueEditor.java
 * Creation date: (05/10/04 10:14:21 AM)
 * By: Iulian Radu
 */
package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeVar;
import org.openquark.cal.valuenode.RangeValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.GemCutter;
import org.openquark.util.ui.UIUtilities;


/**
 * The ValueEditor for Range value nodes.
 *  
 * This editor contains buttons for specifying range endpoint types,
 * and two child editors for the nodes of these endpoints.
 * 
 * The types of the endpoints are required to be instances of the
 * Prelude.Ord class, as dictated by the range constructor SCs. This 
 * is enforced as follows:
 * 
 *   - on creation of a range type value node:
 *      - if the range type is not specialized (ie: "Range a"), 
 *        a node is created, to be specialized by the range editor
 *      - otherwise, only valid orderable types are converted to value nodes
 *        (ie: "Range Double" is valid, whereas "Range (Baz a)" is not)
 * 
 *   - on edit of range value nodes:
 *      - ordering is enforced on the range editor node type (this
 *        is necessary for the case where the node type is "Range a")
 *      - if a context is specified (ie: if the value gem edited is connected),
 *        then the range endpoint types are specialized to the context and Ordered 
 *        (the range value node is transmuted when the connection occurs, and 
 *        thus enforced to Ord by the previous steps)
 *      - if a context is not specified, then the range value node can be "Ord a => Range a",
 *        and the range endpoint types default to an orderable parametric type ("Ord a => a")
 * 
 * @author Iulian Radu
 */
class RangeValueEditor extends StructuredValueEditor {

    private static final long serialVersionUID = 2032925507494723262L;

    /**
     * A custom value editor provider for the RangeValueEditor.
     * @author Iulian Radu
     */
    public static class RangeValueEditorProvider extends ValueEditorProvider<RangeValueEditor> {
        
        public RangeValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            if (valueNode instanceof RangeValueNode) {
                return true;
            }
            return false;
        }

        /**
         * Returns a new instance of the editor.
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public RangeValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            RangeValueEditor editor = new RangeValueEditor(valueEditorHierarchyManager);
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
    }
    
    /**
     * A listener for child editor commits, updating the value nodes in this editor.
     * @author Iulian Radu
     */
    private class ChildValueEditorListener extends ValueEditorAdapter {
        
        @Override
        public void valueCommitted(ValueEditorEvent e) {

            // Retrieve old and new node values
            
            ValueNode oldChild = e.getOldValue();
            ValueNode newChild = ((ValueEditor) e.getSource()).getValueNode();
            if (oldChild.sameValue(newChild)) {
                return;
            }

            // Apply the changes in the child node to the other nodes in this editor 
            // (ie: to the node in the other child editor, and in the parent editor)
            //
            // This mechanism allows for changes in type to ripple through the editor hierarchy. 
            
            Map<ValueNode, TypeExpr> valueNodeToUnconstrainedTypeMap = getValueNodeToUnconstrainedTypeMap();
            Map<ValueNode, ValueNode> commitValueMap = valueEditorManager.getValueNodeCommitHelper().getCommitValues(oldChild, newChild, valueNodeToUnconstrainedTypeMap);
            
            // The commitValueMap contains new value nodes which reflect the change; we now
            // have to update the proper editors with these values
            
            leftEditor.changeOwnerValue(commitValueMap.get(leftEditor.getOwnerValueNode()));
            rightEditor.changeOwnerValue(commitValueMap.get(rightEditor.getOwnerValueNode()));
            replaceValueNode(commitValueMap.get(getValueNode()), true);
            
            // Refresh the value panel, to allow for size changes due 
            // to changes in value node type.
            refreshDisplay();
        }
    }
    
    // A type expression representing "Ord a => Range a", enforced by this editor
    private final TypeConsApp ordRangeType = (TypeConsApp)valueEditorManager.getValueNodeBuilderHelper().getPerspective().getWorkingModuleTypeInfo().getVisibleFunction(RangeValueNode.RangeValueNodeProvider.RANGE_CONSTRUCTION_SC_NAME).getTypeExpr().getTypePieces()[2];
    
    // Icons for the open/closed/unbounded toggle buttons
    
    private static final ImageIcon openLIcon = new ImageIcon(GemCutter.class.getResource("/Resources/intervalOpen.gif"));
    private static final ImageIcon closedLIcon = new ImageIcon(GemCutter.class.getResource("/Resources/intervalClosed.gif"));
    private static final ImageIcon unboundedLIcon = new ImageIcon(GemCutter.class.getResource("/Resources/arrow.gif"));
    private static final ImageIcon openRIcon = new ImageIcon(UIUtilities.flipImage(openLIcon.getImage(), UIUtilities.FlipOrientation.Horizontal));
    private static final ImageIcon closedRIcon = new ImageIcon(UIUtilities.flipImage(closedLIcon.getImage(), UIUtilities.FlipOrientation.Horizontal));
    private static final ImageIcon unboundedRIcon = new ImageIcon(UIUtilities.flipImage(unboundedLIcon.getImage(), UIUtilities.FlipOrientation.Horizontal));
    
    /** Panel containing the value entry UI components */ 
    private final JPanel mainPanel = new JPanel();
    
    /** Value editor for the left endpoint node (set by initializeValue())*/
    private ValueEditor leftEditor = null;
    /** Value editor for the right endpoint node (set by initializeValue())*/
    private ValueEditor rightEditor = null;
    
    /** Label displayed if no left endpoint present */
    private final JLabel leftLabel = new JLabel(ValueEditorMessages.getString("RVE_NoLowerBound"));
    /** Label displayed if no right endpoint present */
    private final JLabel rightLabel = new JLabel(ValueEditorMessages.getString("RVE_NoUpperBound"));
    
    /** 
     * The minimum dimension of the left label after border has been applied, 
     * before being resized to match the start editor
     */
    private Dimension leftLabelMinSize = null;
    /** 
     * The minimum dimension of the right label after border has been applied, 
     * before being resized to match the start editor
     */
    private Dimension rightLabelMinSize = null;
    
    /** Button for indicating open interval for the start endpoint */  
    private final JToggleButton openLToggle = new JToggleButton();
    /** Button for indicating closed interval for the start endpoint */
    private final JToggleButton closedLToggle = new JToggleButton();
    /** Button for indicating no lower bound */
    private final JToggleButton unboundLToggle = new JToggleButton();
    
    /** Button for indicating open interval for the end endpoint */
    private final JToggleButton openRToggle = new JToggleButton();
    /** Button for indicating closed interval for the end endpoint */
    private final JToggleButton closedRToggle = new JToggleButton();
    /** Button for indicating no upper bound */
    private final JToggleButton unboundRToggle = new JToggleButton();    
    
    
    /**
     * Constructor.
     * 
     * This method initializes buttons and labels, but does not create
     * the start/end editors, nor lay out the components. The rest of UI initializing
     * is done via the initializeValue() method once a value node has been assigned 
     * to this editor.
     * 
     * @param valueEditorHierarchyManager
     */
    protected RangeValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        
        setName("RangeValueEditor");
        setLayout(new java.awt.BorderLayout());
        add(mainPanel, "Center");
        setFocusCycleRoot(true);
        
        // Initialize labels  
        
        leftLabel.setBorder(BorderFactory.createEtchedBorder());
        leftLabel.setToolTipText(ValueEditorMessages.getString("RVE_Tip_UnboundedLeftInterval"));
        rightLabel.setBorder(BorderFactory.createEtchedBorder());
        rightLabel.setToolTipText(ValueEditorMessages.getString("RVE_Tip_UnboundedRightInterval"));
        rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        leftLabelMinSize = leftLabel.getMinimumSize();
        rightLabelMinSize = rightLabel.getMinimumSize();
        
        // Initialize buttons
        
        openLToggle.setIcon(openLIcon);
        closedLToggle.setIcon(closedLIcon);
        unboundLToggle.setIcon(unboundedLIcon);
        
        openLToggle.setToolTipText(ValueEditorMessages.getString("RVE_Tip_OpenLeftInterval"));
        closedLToggle.setToolTipText(ValueEditorMessages.getString("RVE_Tip_ClosedLeftInterval"));
        unboundLToggle.setToolTipText(ValueEditorMessages.getString("RVE_Tip_UnboundedLeftInterval"));
        
        openLToggle.setMargin(new Insets(0, 0, 0, 0));
        closedLToggle.setMargin(new Insets(0, 0, 0, 0));
        unboundLToggle.setMargin(new Insets(0, 0, 0, 0));
        
        ButtonGroup leftGroup = new ButtonGroup();
        leftGroup.add(openLToggle);
        leftGroup.add(closedLToggle);
        leftGroup.add(unboundLToggle);
        
        openRToggle.setIcon(openRIcon);
        closedRToggle.setIcon(closedRIcon);
        unboundRToggle.setIcon(unboundedRIcon);
        
        openRToggle.setToolTipText(ValueEditorMessages.getString("RVE_Tip_OpenRightInterval"));
        closedRToggle.setToolTipText(ValueEditorMessages.getString("RVE_Tip_ClosedRightInterval"));
        unboundRToggle.setToolTipText(ValueEditorMessages.getString("RVE_Tip_UnboundedRightInterval"));
        
        openRToggle.setMargin(new Insets(0, 0, 0, 0));
        closedRToggle.setMargin(new Insets(0, 0, 0, 0));
        unboundRToggle.setMargin(new Insets(0, 0, 0, 0));
        
        ButtonGroup rightGroup = new ButtonGroup();
        rightGroup.add(openRToggle);
        rightGroup.add(closedRToggle);
        rightGroup.add(unboundRToggle);

        // Add listeners for showing/hiding editors when 'unbounded' type buttons are toggled
        
        unboundLToggle.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // State changes may occur before editors are created 
                // these do not affect the panel appearance and are ignored.
                if (leftEditor != null) {
                    boolean startVisible = leftEditor.isVisible();
                    boolean togglingVisible = (unboundLToggle.getSelectedObjects() == null);
                    
                    if (startVisible != togglingVisible) {
                        leftEditor.setVisible(togglingVisible);
                        leftLabel.setVisible(!togglingVisible);
                    }
                }
            }
        });
        unboundRToggle.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // State changes may occur before editors are created 
                // these do not affect the panel appearance and are ignored
                if (rightEditor != null) {
                    boolean startVisible = rightEditor.isVisible();
                    boolean togglingVisible = (unboundRToggle.getSelectedObjects() == null);
                    
                    if (startVisible != togglingVisible) {
                        rightEditor.setVisible(togglingVisible);
                        rightLabel.setVisible(!togglingVisible);
                    }
                }
            }
        });
        
        // Add listeners to revert back to a closed interval if an  
        // 'unbounded endpoint' label is clicked
        
        leftLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (leftLabel.isEnabled()) {
                    closedLToggle.setSelected(true);
                }
            }
        });
        
        rightLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (leftLabel.isEnabled()) {
                    closedRToggle.setSelected(true);
                }
            }
        });
        
        // Make sure that the user's commit and cancel keyboard input is watched.
        
        KeyListener keyListener = new ValueEditorKeyListener();
        mainPanel.addKeyListener(keyListener);
        leftLabel.addKeyListener(keyListener);
        rightLabel.addKeyListener(keyListener);
        unboundRToggle.addKeyListener(keyListener);
        closedRToggle.addKeyListener(keyListener);
        openRToggle.addKeyListener(keyListener);
        unboundLToggle.addKeyListener(keyListener);
        closedLToggle.addKeyListener(keyListener);
        openLToggle.addKeyListener(keyListener);
    }
    
    /**
     * Commits the editor value node.
     * @see org.openquark.gems.client.valueentry.ValueEditor#commitValue()
     */
    @Override
    protected void commitValue() {
    
        // The value node of this editor will be updated to contain the value nodes
        // of the child editors as range endpoints, and the current form from the
        // editor settings.
        
        ValueNode start = leftEditor.getValueNode();  
        ValueNode end = rightEditor.getValueNode();
        ValueNode returnVN = new RangeValueNode(getValueNode().getTypeExpr(), getRangeForm(), start, end);
        replaceValueNode(returnVN, false);
        
        notifyValueCommitted();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return mainPanel;
    }
    
    /**
     * Sets the UI components of the main panel.
     */
    private void initializeMainPanel() {
        
        mainPanel.setName("IntermediatePanel");
       
        // Set the toggle buttons and their container toolbars
        
        KeyListener keyListener = new ValueEditorKeyListener();
       
        JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        toolBar1.setRollover(true);
        toolBar1.add(unboundLToggle);
        toolBar1.add(closedLToggle);
        toolBar1.add(openLToggle);
        toolBar1.addKeyListener(keyListener);
       
        JToolBar toolBar2 = new JToolBar();
        toolBar2.setFloatable(false);
        toolBar2.setRollover(true);
        toolBar2.add(openRToggle);
        toolBar2.add(closedRToggle);
        toolBar2.add(unboundRToggle);
        toolBar2.addKeyListener(keyListener);
        
        // Initialize the labels and their sizes
        
        resizeEndpointLabels();
        
        // Now lay out the items on the main panel
        
        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());
       
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.weightx = 1.0;
            constraints.weighty = 0.0;
            constraints.insets = new Insets(3,3,2,2);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(leftEditor, constraints);
            mainPanel.add(leftLabel, constraints);
        }
       
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 3;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.weightx = 1.0;
            constraints.weighty = 0.0;
            constraints.insets = new Insets(3,2,2,3);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(rightEditor, constraints);
            mainPanel.add(rightLabel, constraints);
        }
        
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.weightx = 0.0;
            constraints.weighty = 1.0;
            constraints.insets = new Insets(2,3,3,2);
            constraints.fill = GridBagConstraints.NONE;
            mainPanel.add(toolBar1, constraints);
        }
        
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 4;
            constraints.gridy = 1;
            constraints.anchor = GridBagConstraints.LINE_END;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.insets = new Insets(2,2,3,3);
            constraints.fill = GridBagConstraints.NONE;
            mainPanel.add(toolBar2, constraints);
        }
        
        setMaxResizeDimension(new Dimension(2048, getPreferredSize().height));
    }

    /**
     * Sets the sizes of the endpoint editors and labels, so that label text  
     * is fully visible, and the editors are of at least minimum size.
     */
    private void resizeEndpointLabels() {
        
        // Set minimum size on the left label/editor so that neither are squished
        Dimension editorSize = leftEditor.getPreferredSize();
        if (editorSize == null) {
            editorSize = leftEditor.getMinimumSize();
        }
        Dimension labelSize = leftLabelMinSize;
        int width = Math.max(editorSize.width, labelSize.width);
        int height = Math.max(editorSize.height, labelSize.height);
        leftLabel.setMinimumSize(new Dimension(width, height));
        leftEditor.setMinResizeDimension(new Dimension(width, height));
        
        // Set the preferred size of the left label/editor
        editorSize = leftEditor.getPreferredSize();
        labelSize = leftLabel.getMinimumSize();
        width = Math.max(editorSize.width, labelSize.width);
        height = Math.max(editorSize.height, labelSize.height);
        leftLabel.setPreferredSize(new Dimension(width, height));
        leftEditor.setPreferredSize(new Dimension(width, height));
        
        // Set minimum size on the right label/editor 
        editorSize = rightEditor.getPreferredSize();
        if (editorSize == null) {
            editorSize = rightEditor.getMinimumSize();
        }
        labelSize = rightLabelMinSize;//.getMinimumSize();
        width = Math.max(editorSize.width, labelSize.width);
        height = Math.max(editorSize.height, labelSize.height);
        rightLabel.setMinimumSize(new Dimension(width, height));
        rightEditor.setMinResizeDimension(new Dimension(width, height));
        
        // Set the preferred size of the right label/editor
        editorSize = rightEditor.getPreferredSize();
        labelSize = rightLabel.getMinimumSize();
        width = Math.max(editorSize.width, labelSize.width);
        height = Math.max(editorSize.height, labelSize.height);
        rightLabel.setPreferredSize(new Dimension(width, height));
        rightEditor.setPreferredSize(new Dimension(width, height));
        
    }
    
    /**
     * Sets the initial value of this editor. 
     * This method creates the value entry panels for the endpoint nodes, and 
     * initializes the panel displayed by this editor.
     */
    @Override
    public void setInitialValue() {
        
        // The range value editor supports Ordinal Range types, so the 
        // following ensures that the range value node is an instance of
        // Ordinal prior to being edited. 
        
        enforceOrdType();
        
        // Now initialize the panel
        
        // Create child value entry panels for the left/right endpoints
        
        RangeValueNode rangeNode = getRangeValueNode();
        leftEditor = makeChildValueEntryPanel(rangeNode.getLeftEndpoint());
        rightEditor = makeChildValueEntryPanel(rangeNode.getRightEndpoint());
        
        // Define the context for the child editors. The least constrained type expression
        // is the expression of the type constructor argument (ie: this is 'Double' if
        // the editor value node type is 'Range Double')
        ValueEditorContext context = new ValueEditorContext() {
            public TypeExpr getLeastConstrainedTypeExpr() {

                TypeExpr leastConstrainedParentType = getContext().getLeastConstrainedTypeExpr();
                if (leastConstrainedParentType instanceof TypeVar) {
                    // Range type is parametric (ie: value gem was not bound to connection)
                    
                    // The editors will handle types unifying with "Ord a => a", the valid type for
                    // of the range constructor.
                    TypeExpr ordType = ordRangeType.getArg(0);
                    return ordType;
        
                } else {
                    // The parent is not parametric, so the child must fit the parent constraint.
                    // Here also check and enforce ordering 
                    
                    TypeExpr childExpr = ((TypeConsApp)leastConstrainedParentType).getArg(0);
                    TypeExpr newType = null;
                    try {
                        newType = TypeExpr.unify(ordRangeType.getArg(0), childExpr, getValueEditorHierarchyManager().getValueEditorManager().getPerspective().getWorkingModuleTypeInfo());
                    } catch (TypeException e) {
                        throw new IllegalStateException("RangeValueEditor: Failed to unify type '" + childExpr + "' with '" + ordRangeType + "'.");
                    }
                    
                    return newType;
                }
            }
        };
        leftEditor.setContext(context);
        rightEditor.setContext(context);
        
        // Add commit listeners to the child editors     
        
        ChildValueEditorListener editorListener = new ChildValueEditorListener();
        leftEditor.addValueEditorListener(editorListener);
        rightEditor.addValueEditorListener(editorListener);
        
        // Set up the UI components
        
        initializeMainPanel();
        showRangeForm();

        // Set the appropriate editor panel size
        
        validate();
        Dimension prefSize = getPreferredSize();
        setMinResizeDimension(prefSize);
        setSize(prefSize);      
    }
    
    /**
     * Refresh the editor display by resetting the size of the editor to 
     * match its components. 
     *  
     * @see org.openquark.gems.client.valueentry.ValueEditor#refreshDisplay()
     */
    @Override
    public void refreshDisplay() {
        resizeEndpointLabels();
        setMinResizeDimension(getPreferredSize());
        if (getPreferredSize().width > getSize().width) {
            setSize(new Dimension(getPreferredSize().width, getSize().height));
        }
        if (getPreferredSize().height > getSize().height) {
            setSize(new Dimension(getSize().width, getPreferredSize().height));
        }
    }
    
    /**
     * Sets the UI component settings according to the range form
     * (ie: sets the visibility of the endpoint editors, and selects
     * the appropriate toggle buttons)
     */
    private void showRangeForm() {

        RangeValueNode.Form rangeForm = getRangeValueNode().getForm();
        
        // Enable/disable editors
        leftEditor.setVisible(rangeForm.hasLeftBound());
        rightEditor.setVisible(rangeForm.hasRightBound());
        
        // Set left endpoint buttons
        if (!rangeForm.hasLeftBound()) {
            unboundLToggle.setSelected(true);
        } else if (rangeForm.includesLeftBound()) {
            closedLToggle.setSelected(true);
        } else {
            openLToggle.setSelected(true);
        }
        
        // Set right endpoint buttons
        if (!rangeForm.hasRightBound()) {
            unboundRToggle.setSelected(true);
        } else if (rangeForm.includesRightBound()) {
            closedRToggle.setSelected(true);
        } else {
            openRToggle.setSelected(true);
        }
        
        refreshDisplay();
    }
    
    /**
     * @return the range form representing the current UI button settings
     */
    private RangeValueNode.Form getRangeForm() {
        
        return new RangeValueNode.Form(
                unboundLToggle.getSelectedObjects() == null, 
                unboundRToggle.getSelectedObjects() == null, 
                closedLToggle.getSelectedObjects() != null, 
                closedRToggle.getSelectedObjects() != null); 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditable(boolean editable) {
        if (leftEditor != null) {
            leftEditor.setEditable(editable);
            rightEditor.setEditable(editable);
        }
        leftLabel.setEnabled(editable);
        rightLabel.setEnabled(editable);
        unboundLToggle.setEnabled(editable);
        unboundRToggle.setEnabled(editable);
        openLToggle.setEnabled(editable);
        openRToggle.setEnabled(editable);
        closedLToggle.setEnabled(editable);
        closedRToggle.setEnabled(editable);
    }
    
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#editorActivated()
     */
    @Override
    public void editorActivated() {
        valueEditorHierarchyManager.addEditorToHierarchy(leftEditor, this);
        setResizable(true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleElementLaunchingEditor() {
    }
    
    /**
     * @return the range value node currently edited
     */
    public RangeValueNode getRangeValueNode() {
        return (RangeValueNode)getValueNode();
    }
    
    /**
     * Create a child value entry panel to handle the specified value node.
     * @param valueNode the value node for the value entry panel
     * @return a value entry panel to be used in this editor
     */
    private ValueEntryPanel makeChildValueEntryPanel(ValueNode valueNode) {
        ValueEntryPanel childPanel = new ValueEntryPanel(valueEditorHierarchyManager, valueNode);
        childPanel.setAlwaysShowLaunchButton(false);
        childPanel.setParentValueEditor(this);
        return childPanel;
    }
    
    /**
     * Enforces the type expression of the editor value node to be an instance of Ord.
     * This type change only occurs when the node has been instantiated with type "Range a";
     * when this is not so, the check is enforced in RangeValueNodeProvider.
     */
    private void enforceOrdType() {
        
        // Retrieve the default type expression of the parent editor node.
        // This is retrieved from the type of the supercombinator constructing
        // the range entity, and will be "Ord a => Range a".
        TypeExpr ordParametric = ordRangeType;
        
        // Now, unify this with the current value node type. This will (1) specialize
        // a fully parametric type (ie: Range a) to be an instance of Ord, and (2) check that
        // if the type is already specialized (ex: Range Double), the type is actually orderable
        // (this check is enforced in RangeValueNode).
        TypeExpr newType = null;
        try {
            newType = TypeExpr.unify(ordParametric, getValueNode().getTypeExpr(), getValueEditorHierarchyManager().getValueEditorManager().getPerspective().getWorkingModuleTypeInfo());
        } catch (TypeException e) {
            throw new IllegalStateException("RangeValueEditor: Failed to unify type '" + getValueNode().getTypeExpr() + "' with '" + ordParametric + "'.");
        }
        
        // Store the ordered type as our type
        this.replaceValueNode(getValueNode().transmuteValueNode(valueEditorManager.getValueNodeBuilderHelper(), valueEditorManager.getValueNodeTransformer(), newType), false);
    }
    
    /**
     * Get a map from owner value node to type for ValueEntryPanels for the nodes in this editor.
     * @return Map from every value node managed by this editor to its least constrained type.
     */
    private Map<ValueNode, TypeExpr> getValueNodeToUnconstrainedTypeMap() {
        
        Map<ValueNode, TypeExpr> returnMap = new HashMap<ValueNode, TypeExpr>();

        // Calculate the type of the parent value editor node 
        
        TypeExpr contextType = getContext().getLeastConstrainedTypeExpr();
        if (contextType.rootTypeVar() != null) {
            // ValueGem is not bound to a context, so the type should be the parametric range type 
            
            // Retrieve the default type expression of the parent editor node.
            //
            // This is retrieved from the type of the supercombinator constructing
            // the range entity, and will be "Ord a => Range a".
            contextType = ordRangeType;
        }
                    
        // Populate the map
        
        // With our node and type
        returnMap.put(getValueNode(), contextType);
                    
        // With the child editor nodes and types
        TypeExpr childType = contextType.rootTypeConsApp().getArg(0); 
        returnMap.put(leftEditor.getOwnerValueNode(), childType);
        returnMap.put(rightEditor.getOwnerValueNode(), childType);

        return returnMap;
    }
}
