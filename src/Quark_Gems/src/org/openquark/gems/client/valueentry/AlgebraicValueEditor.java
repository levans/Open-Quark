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
 * AlgebraicValueEditor.java
 * Creation date: Oct 22, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.valueentry;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.PolymorphicVarContext;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeVar;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.valuenode.DataConstructorValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.ToolTipHelpers;


/**
 * A value editor for editing an algebraic type. It allows the user to see all supported data
 * constructors of the algebraic type, select a data constructor and enter values for the
 * data constructor arguments.
 * @author Frank Worsley
 */
public class AlgebraicValueEditor extends StructuredValueEditor {

    private static final long serialVersionUID = 3772409667881908123L;

    /**
     * A custom value editor provider for the AlgebraicValueEditor.
     */
    public static class AlgebraicValueEditorProvider extends ValueEditorProvider<AlgebraicValueEditor> {
        
        public AlgebraicValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof DataConstructorValueNode && 
                   hasSupportedChildren((DataConstructorValueNode) valueNode, providerSupportInfo);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AlgebraicValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager, ValueNode valueNode) {
            AlgebraicValueEditor editor = new AlgebraicValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }

        /**
         * Checks if all children of a data constructor value node are supported by value editors.
         * @param valueNode the data constructor value node
         * @param supportInfo
         * @return true if all children are supported by value editors
         */        
        private boolean hasSupportedChildren(DataConstructorValueNode valueNode, SupportInfo supportInfo) {
            
            // Notify the info object that the value node's type is supported..
            supportInfo.markSupported(valueNode.getTypeExpr());
            
            ValueEditorManager valueEditorManager = getValueEditorManager();
        
            // Return false if any of the children are unsupported.
            for (final ValueNode childValueNode : valueNode.getChildrenList()) {
                if (!valueEditorManager.isSupportedValueNode(childValueNode, supportInfo)) {
                    return false;
                }
            }
            
            return true;
        }
    }
    
    /**
     * A listener that updates the values nodes of the child editors if a child editor is committed.
     * @author Frank Worsley
     */
    private class ChildValueEditorListener extends ValueEditorAdapter {
        
        @Override
        public void valueCommitted(ValueEditorEvent e) {
            
            ValueEditor source = (ValueEditor) e.getSource();
            ValueNode oldChildValueNode = e.getOldValue();
            ValueNode newChildValueNode = source.getValueNode();
            
            if (!isEditable() || oldChildValueNode.sameValue(newChildValueNode)) {
                return;
            }

            // Get the commit value map and update the editor panel value nodes.
            Map<ValueNode, TypeExpr> valueNodeToUnconstrainedTypeMap = getValueNodeToUnconstrainedTypeMap();
            Map<ValueNode, ValueNode> commitValueMap = valueEditorManager.getValueNodeCommitHelper().getCommitValues(oldChildValueNode, newChildValueNode, valueNodeToUnconstrainedTypeMap);
            
            for (final DataConstructorEditorPanel editorPanel : editorPanelList) {
                editorPanel.updateValueNode(commitValueMap);
            }

            // Replace the current value node with the value node of the active panel.
            ValueNode newValueNode = null;
            if (editorPanelList.size() == 1) {
                newValueNode = editorPanelList.get(0).getValueNode().copyValueNode();
            } else {
                newValueNode = focusChangeListener.getFocusedPanel().getValueNode().copyValueNode();
            }
            
            replaceValueNode(newValueNode, true);

            refreshDisplay();
        }
    }

    /**
     * A property change listener that tracks the currently focused editor panel.
     * It updates the focused look of the editor panels, if the focused panel changes.
     * @author Frank Worsley
     */
    private class FocusChangeListener implements PropertyChangeListener {

        /** The currently focused editor panel. */
        DataConstructorEditorPanel currentFocusedPanel = null;

        /**
         * @return the currently focused editor panel.
         */
        public DataConstructorEditorPanel getFocusedPanel() {
            return currentFocusedPanel;
        }
        
        public void setFocusedPanel(DataConstructorEditorPanel focusedPanel) {
            
            if (currentFocusedPanel != null) {
                currentFocusedPanel.setFocusedLook(false);
            }
            
            currentFocusedPanel = focusedPanel;
            currentFocusedPanel.setFocusedLook(true);
        }

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt) {

            if (!isEditable()) {
                return;
            }

            Component focusedValueEntryPanel = null;
            Component newFocusOwner = (Component) evt.getNewValue();
            
            while (newFocusOwner != null) {
                
                if (newFocusOwner instanceof ValueEntryPanel) {
                    focusedValueEntryPanel = newFocusOwner; 
                }
                
                if (newFocusOwner instanceof DataConstructorEditorPanel &&
                    ((DataConstructorEditorPanel) newFocusOwner).getParentEditor() == AlgebraicValueEditor.this) {

                    if (newFocusOwner == currentFocusedPanel) {
                        
                        // If there is a newly focused value entry panel, then scroll into view.
                        if (focusedValueEntryPanel != null) {
                            Rectangle bounds = SwingUtilities.convertRectangle(focusedValueEntryPanel.getParent(), focusedValueEntryPanel.getBounds(), currentFocusedPanel);
                            currentFocusedPanel.scrollRectToVisible(bounds);
                        }
                        
                        return;
                    }
                    
                    if (currentFocusedPanel != null) {
                        currentFocusedPanel.setFocusedLook(false);
                    }
                    
                    currentFocusedPanel = (DataConstructorEditorPanel) newFocusOwner;
                    currentFocusedPanel.setFocusedLook(true);
                    
                    return;
                }
                
                newFocusOwner = newFocusOwner.getParent();
            }
        }
    }
    
    /**
     * A specialized panel for embedding the data constructor editor panels. It implements
     * the scrollable interface to scroll the desired amount for data constructor editors.
     * @author Frank Worsley
     */
    private class EditorContentPanel extends JPanel implements Scrollable {
        
        private static final long serialVersionUID = -137516687784049682L;

        /**
         * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
         */
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        /**
         * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
         */
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        /**
         * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
         */
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        /**
         * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
         */
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 2 * getScrollableUnitIncrement(visibleRect, orientation, direction);
        }

        /**
         * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
         */
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

            int editorHeight = editorPanelList.get(0).getPreferredSize().height; 
            int separatorHeight = (new JSeparator(SwingConstants.HORIZONTAL)).getPreferredSize().height;

            if (orientation == SwingConstants.VERTICAL) {
                return editorHeight + separatorHeight;
            }
    
            return 30;
        }
    }
    
    /**
     * A key listener for committing/canceling and navigating the editor panels.
     * @author Frank Worsley
     */
    private class EditorPanelKeyListener extends KeyAdapter {
        
        @Override
        public void keyPressed(KeyEvent e) {
            
            int keyCode = e.getKeyCode();
            int index = editorPanelList.indexOf(e.getSource());
            
            if (keyCode == KeyEvent.VK_ENTER) {
                handleCommitGesture();
                e.consume();
            
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                e.consume();
            
            } else if (keyCode == KeyEvent.VK_UP && index > 0) {
                editorPanelList.get(index - 1).requestFocusInWindow();
                e.consume();
                
            } else if (keyCode == KeyEvent.VK_DOWN && index < editorPanelList.size() - 1) {
                editorPanelList.get(index + 1).requestFocusInWindow();
                e.consume();
            }
        }
    }

    /** The maximum initial width at which the editor should display. */
    private static final int MAX_INITIAL_WIDTH = 600;
    
    /** The maximum initial number of items the editor should display. */
    private static final int MAX_INITIAL_ROWS = 10;

    /** The list of data constructor editor panels. One panel for each supported constructor. */
    private final List<DataConstructorEditorPanel> editorPanelList = new ArrayList<DataConstructorEditorPanel>();

    /** The main panel that displays the controls of this editor. */
    private final EditorContentPanel contentPanel = new EditorContentPanel();
    
    /** The scroll pane for the content panel. */
    private final JScrollPane contentScrollPane = new JScrollPane(contentPanel);

    /** The focus change listener that updates the focused look of the editor panels. */
    private final FocusChangeListener focusChangeListener = new FocusChangeListener();
    
    /** The length in pixels of the data constructor with the longest name. */
    private int longestNameLength = 0;

    /**
     * Constructs a new AlgebraicValueEditor.
     * @param valueEditorHierarchyManager
     */
    public AlgebraicValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);

        setFocusCycleRoot(true);
        setLayout(new BorderLayout());
        setResizable(true);
        add(contentScrollPane, BorderLayout.CENTER);

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        contentScrollPane.getHorizontalScrollBar().setCursor(Cursor.getDefaultCursor());
        contentScrollPane.getVerticalScrollBar().setCursor(Cursor.getDefaultCursor());
        
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener("permanentFocusOwner", focusChangeListener); 
    }
    
    /**
     * Removes the focus change listener that is installed by this editor.
     */
    private void removeFocusListener() {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.removePropertyChangeListener("permanentFocusOwner", focusChangeListener);
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setEditable(boolean)
     */
    @Override
    public void setEditable(boolean editable) {
        
        super.setEditable(editable);

        for (final DataConstructorEditorPanel dataConstructorEditorPanel : editorPanelList) {
            dataConstructorEditorPanel.setArgumentEditorsEditable(editable);
        }
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#commitValue()
     */
    @Override
    public void commitValue() {
        
        removeFocusListener();
        
        // Make sure we are using the correct value node.
        if (focusChangeListener.getFocusedPanel() != null) {
            replaceValueNode(focusChangeListener.getFocusedPanel().getValueNode().copyValueNode(), true);
        }
        
        super.commitValue();
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#cancelValue()
     */
    @Override
    public void cancelValue() {
        removeFocusListener();
        super.cancelValue();
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#getDefaultFocusComponent()
     */
    @Override
    public Component getDefaultFocusComponent() {
        return null;
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.StructuredValueEditor#handleElementLaunchingEditor()
     */
    @Override
    protected void handleElementLaunchingEditor() {
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {
        
        DataConstructorValueNode valueNode = getDataConstructorValueNode();
        TypeExpr valueNodeTypeExpr = valueNode.getTypeExpr();
        
        QualifiedName typeConstructorName = valueNode.getTypeExpr().rootTypeConsApp().getName();
        DataConstructor[] dataConsList = valueEditorManager.getPerspective().getDataConstructorsForType(typeConstructorName);

        DataConstructor currentDataCons = valueNode.getDataConstructor();

        ValueEditorListener childListener = new ChildValueEditorListener();
        KeyAdapter editorPanelKeyListener = new EditorPanelKeyListener();
        
        boolean haveAddedPanel = false;
        FontMetrics fontMetrics = getFontMetrics(getFont().deriveFont(Font.BOLD));
        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(valueEditorManager.getPerspective().getWorkingModuleTypeInfo());

        // Remove components in case this is called twice.
        contentPanel.removeAll();

        // Create an editor panel for each supported data constructor.
        for (final DataConstructor dataCons : dataConsList) {

            DataConstructorValueNode newValueNode = null;

            if (currentDataCons.getName().equals(dataCons.getName())) {
                
                // If this is the current data constructor, just transmute the value node.
                newValueNode = (DataConstructorValueNode) valueNode.transmuteValueNode(valueEditorManager.getValueNodeBuilderHelper(),
                                                                                       valueEditorManager.getValueNodeTransformer(),
                                                                                       valueNodeTypeExpr);
            } else {

                // Create a new value node.  newValueNode will be null if the data constructor is not supported.
                newValueNode = valueEditorManager.getValueNodeBuilderHelper().
                        getValueNodeProvider(DataConstructorValueNode.class).getNodeInstance(null, dataCons, valueNodeTypeExpr);
            }

            // Create a data constructor editor panel if the data constructor is supported.    
            if (newValueNode != null) {

                DataConstructorEditorPanel editorPanel = new DataConstructorEditorPanel(this, newValueNode);
                editorPanel.setFocusedLook(false);
                editorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                editorPanel.addArgumentEditorListener(childListener);
                editorPanel.addKeyListener(editorPanelKeyListener);

                if (haveAddedPanel) {
                    contentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
                }

                editorPanelList.add(editorPanel);                
                contentPanel.add(editorPanel);
                
                haveAddedPanel = true;

                int titleLength = fontMetrics.stringWidth(dataCons.getAdaptedName(namingPolicy));
                if (titleLength > longestNameLength) {
                    longestNameLength = titleLength;
                }
            }
        }
        
        // Make the width of all title labels the same so that the editor panels align.
        for (final DataConstructorEditorPanel dataConstructorEditorPanel : editorPanelList) {
            dataConstructorEditorPanel.setTitleLabelWidth(longestNameLength);
        }
        
        // If we have only one child editor, remove the focus listener.
        // Also make the only editor's value entry panels visible, but give it the normal background.
        if (editorPanelList.size() == 1) {
            removeFocusListener();
            DataConstructorEditorPanel childEditor = editorPanelList.get(0);
            childEditor.setFocusedLook(false);
            childEditor.setArgumentEditorsVisible(true);
        }
        
        resetSize();
    }
    
    /**
     * Resets the current size and min/max sizes of this value editor.
     */
    private void resetSize() {
        
        validate();
        
        // Figure out the height needed for various components.
        int editorHeight = editorPanelList.get(0).getPreferredSize().height;
        int separatorHeight = (new JSeparator(SwingConstants.HORIZONTAL)).getPreferredSize().height;
        int scrollBarHeight = (new JScrollBar(Adjustable.HORIZONTAL)).getPreferredSize().height;
        int scrollBarWidth = (new JScrollBar(Adjustable.VERTICAL)).getPreferredSize().width;

        // Figure out the maximum initial height in pixels.        
        int maxInitialHeight = MAX_INITIAL_ROWS * editorHeight + (MAX_INITIAL_ROWS - 1) * separatorHeight;

        Dimension prefSize = getPreferredSize();        
        int bestHeight = maxInitialHeight;
        int bestWidth = prefSize.width;
        
        if (editorPanelList.size() <= MAX_INITIAL_ROWS) {
            bestHeight = prefSize.height;
        } else {
            bestWidth += scrollBarHeight;
            prefSize.height += scrollBarHeight;
        }
        
        if (bestWidth > MAX_INITIAL_WIDTH) {
            
            Dimension size = getSize();
            
            if (size.width > MAX_INITIAL_WIDTH) {
                bestWidth = size.width < prefSize.width ? size.width : prefSize.width;
            } else {
                bestWidth = MAX_INITIAL_WIDTH;
            }
            
            bestHeight += scrollBarWidth;
            prefSize.height += scrollBarWidth;
        }
        
        // Setup the sizes of the editor.
        Dimension bestSize = new Dimension(bestWidth, bestHeight);
        setSize(bestSize);
        setMaxResizeDimension(prefSize);
        setMinResizeDimension(new Dimension(longestNameLength + scrollBarWidth + 30, editorHeight + scrollBarHeight));
        
        revalidate();
    }

    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#editorActivated()
     */
    @Override
    public void editorActivated() {

        DataConstructor dataCons = getDataConstructor();
        
        for (final DataConstructorEditorPanel childEditor : editorPanelList) {

            if (dataCons.getName().equals(childEditor.getDataConstructor().getName())) {
                focusChangeListener.setFocusedPanel(childEditor);
                break;
            }
        }
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#refreshDisplay()
     */
    @Override
    public void refreshDisplay() {
        
        PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make();
        
        for (final DataConstructorEditorPanel childEditor : editorPanelList) {
            childEditor.refreshDisplay(polymorphicVarContext);
        }
        
        resetSize();
    }

    /**
     * @return this editor's value node casted to a DataConstructorValueNode
     */
    private DataConstructorValueNode getDataConstructorValueNode() {
        return (DataConstructorValueNode) getValueNode();
    }
    
    /**
     * @return the data constructor used by this editor's data constructor value node
     */
    private DataConstructor getDataConstructor() {
        return getDataConstructorValueNode().getDataConstructor();
    }

    /**
     * Get a map from owner value node to type for the child editors used in this editor.
     * @return Map For the owner value node of each child editor used by this editor.
     */
    private Map<ValueNode, TypeExpr> getValueNodeToUnconstrainedTypeMap() {
        
        Map<ValueNode, TypeExpr> returnMap = new HashMap<ValueNode, TypeExpr>();

        TypeExpr leastConstrainedType = getContext().getLeastConstrainedTypeExpr();
        if (leastConstrainedType.rootTypeVar() != null) {
            leastConstrainedType = getDataConstructor().getTypeExpr().getResultType();
        }
        
        returnMap.put(getValueNode(), leastConstrainedType);
                
        for (final DataConstructorEditorPanel childEditor : editorPanelList) {

            returnMap.putAll(childEditor.getValueNodeToUnconstrainedTypeMap(leastConstrainedType));
        }

        return returnMap;
    }
}

/**
 * A panel that embeds value entry panels within itself and handles editing of 
 * data constructor argument values.
 * @author Frank Worsley
 */
class DataConstructorEditorPanel extends JPanel {
    
    private static final long serialVersionUID = -759944179399335050L;

    /**
     * A panel that embeds a value entry panel and a label. If the focused look is set then this
     * panel will display the value entry panel. Otherwise it will display the place holder label.
     * @author Frank Worsley
     */
    private class ArgumentEditorPanel extends JPanel {

        private static final long serialVersionUID = 2659046723149452021L;

        /** The label we display instead of the value entry panel is the editor is not focused. */
        private final JLabel placeHolderLabel = new JLabel();
        
        /** The value entry panel for this argument editor panel. */
        private final ValueEntryPanel valueEntryPanel;
        
        public ArgumentEditorPanel(ValueEntryPanel valueEntryPanel) {
            
            if (valueEntryPanel == null) {
                throw new NullPointerException();
            }
            
            this.valueEntryPanel = valueEntryPanel;

            setLayout(new BorderLayout());
            setCursor(Cursor.getDefaultCursor());
            setOpaque(true);

            // Initially we are in the focused look and the VEP is shown.
            add(valueEntryPanel);
                        
            // Setup the placeholder label.
            placeHolderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeHolderLabel.setHorizontalAlignment(SwingConstants.CENTER);
            placeHolderLabel.setFont(TYPE_LABEL_FONT);
            placeHolderLabel.setForeground(Color.DARK_GRAY);
            placeHolderLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            placeHolderLabel.setCursor(Cursor.getDefaultCursor());

            // Activate the value entry panel this label is for is the user clicks on it.
            placeHolderLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    valueEntryPanelToFocus = ArgumentEditorPanel.this.valueEntryPanel;
                    requestFocusInWindow();
                }
            });
            
            // Finally make sure we only grow as big as the VEP needs to be.
            setPreferredSize(valueEntryPanel.getPreferredSize());
            setMaximumSize(getPreferredSize());
        }

        /**
         * @return the value entry panel used by this argument panel.
         */
        public ValueEntryPanel getValueEntryPanel() {
            return valueEntryPanel;
        }
        
        /**
         * Sets the focused look of this argument panel.
         * @param focused whether to use the focused look
         */
        public void setFocusedLook(boolean focused) {
            setValueEntryPanelVisible(focused);
            setBackground(focused ? FOCUSED_BACKGROUND_COLOR : NORMAL_BACKGROUND_COLOR);
        }
        
        /**
         * Sets whether or not the argument value entry panels are visible.
         * @param visible whether or not to show the VEPs
         */
        public void setValueEntryPanelVisible(boolean visible) {

            if (visible) {
                remove(placeHolderLabel);
                add(valueEntryPanel);
            } else {
                remove (valueEntryPanel);
                add(placeHolderLabel);
            }
            
            setBorder(visible ? BorderFactory.createEmptyBorder(1, 1, 1, 1) : BorderFactory.createLineBorder(valueEntryPanel.getBackground(), 1));            

            revalidate();
        }
        
        /**
         * Updates the text and tooltip of the placeholder label.
         * @param polymorphicVarContext used for toString'ing the argument TypeExpr
         */
        public void refreshDisplay(PolymorphicVarContext polymorphicVarContext) {
            
            TypeExpr leastConstrainedType = valueEntryPanel.getContext().getLeastConstrainedTypeExpr();
            TypeExpr actualType = valueEntryPanel.getValueNode().getTypeExpr();
            
            // Show the more specialized of the two types..
            if (actualType instanceof TypeVar) {
                placeHolderLabel.setText(leastConstrainedType.toString(polymorphicVarContext, namingPolicy));
            } else {
                placeHolderLabel.setText(actualType.toString(polymorphicVarContext, namingPolicy));
            }
            
            if (getBorder() instanceof LineBorder) {
                setBorder(BorderFactory.createLineBorder(valueEntryPanel.getBackground(), 1));
            }
        }
    }

    /** The font to use for labels that display type signatures. */
    private static final Font TYPE_LABEL_FONT = Font.decode("courier-italic-12");

    /** The normal background color of the editor panel. */
    private static final Color NORMAL_BACKGROUND_COLOR = UIManager.getColor("Panel.background");
    
    /** The normal foreground color of the editor panel. */
    private static final Color NORMAL_FOREGROUND_COLOR = UIManager.getColor("Panel.foreground");

    /** The focused background color of the editor panel. */
    private static final Color FOCUSED_BACKGROUND_COLOR = UIManager.getColor("List.selectionBackground");

    /** The focused foreground color of the editor panel. */
    private static final Color FOCUSED_FOREGROUND_COLOR = UIManager.getColor("List.selectionForeground");

    /** The normal border of the editor panel. */
    private static final Border NORMAL_BORDER = BorderFactory.createLineBorder(NORMAL_BACKGROUND_COLOR, 2);

    /** The focused border of the editor panel. */
    private static final Border FOCUSED_BORDER = BorderFactory.createLineBorder(FOCUSED_BACKGROUND_COLOR, 2);

    /** The naming policy used by this editor. */
    private final ScopedEntityNamingPolicy namingPolicy;
    
    /** The title label that displays the data constructor name. */
    private final JLabel titleLabel = new JLabel();
    
    /** The parent editor this panel belongs to. */
    private final StructuredValueEditor parentEditor;

    /** The array that holds on to the editor panels for the constructor arguments. */
    private ArgumentEditorPanel[] editorPanels = null;

    /** The value entry panel the user clicked on and that should be focused. */
    private ValueEntryPanel valueEntryPanelToFocus = null;
    
    /** The data constructor value node used by this editor panel. */
    private DataConstructorValueNode valueNode = null;
    
    /**
     * Constructs a new editor panel.
     * @param parentEditor the value editor this panel is for
     * @param valueNode the value node this panel is for
     */
    public DataConstructorEditorPanel(StructuredValueEditor parentEditor, DataConstructorValueNode valueNode) {
        
        if (parentEditor == null || valueNode == null) {
            throw new NullPointerException();
        }
        
        this.parentEditor = parentEditor;
        this.valueNode = valueNode;
        
        namingPolicy = new UnqualifiedUnlessAmbiguous(parentEditor.valueEditorManager.getPerspective().getWorkingModuleTypeInfo());
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(NORMAL_BACKGROUND_COLOR);
        setBorder(NORMAL_BORDER);
        setCursor(Cursor.getDefaultCursor());
        setFocusable(true);
        
        initialize();
    }
    
    /**
     * Initializes the components of the editor panel.
     */
    private void initialize() {

        titleLabel.setText(getDataConstructor().getAdaptedName(namingPolicy));
        titleLabel.setFont(getFont().deriveFont(Font.BOLD));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
        titleLabel.setForeground(NORMAL_FOREGROUND_COLOR);
        titleLabel.setCursor(Cursor.getDefaultCursor());
        add(titleLabel);

        Dimension prefSize = titleLabel.getPreferredSize();
        titleLabel.setPreferredSize(new Dimension(prefSize.width, ValueEntryPanel.PANEL_HEIGHT));
                       
        List<ValueNode> childValueNodes = valueNode.getChildrenList();
        int size = childValueNodes.size();

        // Add an argument editor panel for each argument.        
        editorPanels = new ArgumentEditorPanel[size];
                
        for (int i = 0; i < size; i++) {
            
            ValueNode childValueNode = childValueNodes.get(i);
            
            ValueEntryPanel vep = new ValueEntryPanel(parentEditor.valueEditorHierarchyManager);
                
            vep.setContext(getValueEntryPanelContext(i));
            vep.setParentValueEditor(parentEditor);
            vep.setOwnerValueNode(childValueNode);
                
            editorPanels[i] = new ArgumentEditorPanel(vep);
            
            add(Box.createHorizontalStrut(5));
            add(editorPanels[i]);
        }
       
        add(Box.createHorizontalGlue());
    
        // Focus this panel if the user clicks on a component.
        MouseAdapter focusMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        };
        
        addMouseListener(focusMouseListener);
        titleLabel.addMouseListener(focusMouseListener);    
    }

    /**
     * Update the tooltips. Note: we can only do this when the panel is parented,
     * since the ToolTipHelpers functions need a Graphics context to make font size calculations.
     * A graphics context is only available once a component has been parented.
     * @param polymorphicVarContext used to toString() the TypeExpr of the arguments
     */
    public void refreshDisplay(PolymorphicVarContext polymorphicVarContext) {
        
        for (final ArgumentEditorPanel editorPanel : editorPanels) {
            editorPanel.refreshDisplay(polymorphicVarContext);
        }
        
        DataConstructor dataConstructor = getDataConstructor();
        titleLabel.setToolTipText(ToolTipHelpers.getEntityToolTip(dataConstructor, namingPolicy, parentEditor.valueEditorManager.getWorkspace(), this));
    }
    
    /**
     * Sets the focused look of this editor panel. If the editor is focused it will display
     * value entry panels for the data constructor argument values and use a different background
     * color. If focused is true then the first value entry panel will be added to the editor hierarchy.
     * @param focused whether to have the focused look
     */
    public void setFocusedLook(boolean focused) {
        
        setBorder(focused ? FOCUSED_BORDER : NORMAL_BORDER);
        setBackground(focused ? FOCUSED_BACKGROUND_COLOR : NORMAL_BACKGROUND_COLOR);
          
        titleLabel.setForeground(focused ? FOCUSED_FOREGROUND_COLOR : NORMAL_FOREGROUND_COLOR);
        
        for (int i = 0; editorPanels != null && i < editorPanels.length; i++) {
            editorPanels[i].setFocusedLook(focused);
        }
        
        if (focused && parentEditor.isEditable()) {
            addChildEditorToHierarchy();
        }
    }
    
    /**
     * Sets whether or not the argument value entry panels are visible.
     * @param visible whether or not to show the VEPs
     */
    public void setArgumentEditorsVisible(boolean visible) {

        for (int i = 0; editorPanels != null && i < editorPanels.length; i++) {
            editorPanels[i].setValueEntryPanelVisible(visible);
        }
        
        if (visible) {
            addChildEditorToHierarchy();
        }
    }

    /**
     * Sets the editable state of the argument editor panels.
     * @param editable whether or not the argument editor panels should be editable
     */    
    public void setArgumentEditorsEditable(boolean editable) {
        for (int i = 0; editorPanels != null && i < editorPanels.length; i++) {
            editorPanels[i].getValueEntryPanel().setEditable(editable);
        }
    }

    /**
     * Adds the value entry panel that was clicked on to the value editor hierarchy. If no
     * value entry panel was clicked on it adds the first value entry panel. If there are no
     * value entry panels it simply requests focus.
     * This method is called by setFocusedLook and setArgumentEditorsVisible. 
     */
    private void addChildEditorToHierarchy() {
        
        if (valueEntryPanelToFocus == null && editorPanels.length > 0) {
            valueEntryPanelToFocus = editorPanels[0].getValueEntryPanel();
        }
        
        if (valueEntryPanelToFocus != null) {
            
            parentEditor.valueEditorHierarchyManager.addEditorToHierarchy(valueEntryPanelToFocus, parentEditor);
            
            if (editorPanels.length > 0 && valueEntryPanelToFocus == editorPanels[0].getValueEntryPanel()) {
                
                // If the first value entry panel is being focused, then scroll to the start of the 
                // editor panel, so that the data constructor name is visible.
                scrollRectToVisible(new Rectangle(0, 0, 1, 1));
                
            } else {
                
                // Otherwise just scroll the value entry panel itself into view.
                Rectangle bounds = SwingUtilities.convertRectangle(valueEntryPanelToFocus.getParent(), valueEntryPanelToFocus.getBounds(), this);
                scrollRectToVisible(bounds);                
            }
            
            valueEntryPanelToFocus = null;
            
        } else {
            
            // Scroll to the start of the panel so the data constructor name is visible.
            scrollRectToVisible(new Rectangle(0, 0, 1, 1));            
            requestFocusInWindow();
        }
    }
    
    /**
     * Sets the preferred width of the title label. Use this function if you embed several
     * data constructor editor panels and want to align them.
     * @param width the new preferred width
     */
    public void setTitleLabelWidth(int width) {
        Dimension prefSize = titleLabel.getPreferredSize();
        Insets insets = titleLabel.getInsets();
        titleLabel.setPreferredSize(new Dimension(width + insets.left + insets.right,
                                                  prefSize.height + insets.top + insets.bottom));
    }    
    
    /**
     * Adds a listener to all value entry panels used to edit the data constructor argument values.
     * @param listener the listener to add
     */
    public void addArgumentEditorListener(ValueEditorListener listener) {
        for (final ArgumentEditorPanel editorPanel : editorPanels) {
            editorPanel.getValueEntryPanel().addValueEditorListener(listener);
        }
    }
    
    /**
     * @return the value editor context used by the value entry panels in this editor
     * @param argNum the argument number that the panel is for
     */
    private ValueEditorContext getValueEntryPanelContext(final int argNum) {
        
        return new ValueEditorContext() {

            public TypeExpr getLeastConstrainedTypeExpr() {

                DataConstructor dataConstructor = getDataConstructor();
                TypeExpr leastConstrainedParentType = parentEditor.getContext().getLeastConstrainedTypeExpr();
        
                if (leastConstrainedParentType instanceof TypeVar) {
            
                    // The parent is parametric, that means the child can be whatever it wants.
                    return dataConstructor.getTypeExpr().getTypePieces()[argNum];
        
                } else {
            
                    // The parent is not parametric, so the child must fit the parent constraint.
                    return TypeExpr.getComponentTypeExpr(leastConstrainedParentType, argNum, dataConstructor);
                }
            }
        };
    }
    
    /**
     * @return the data constructor used by this editor's data constructor value node
     */
    public DataConstructor getDataConstructor() {
        return valueNode.getDataConstructor();
    }
    
    /**
     * @return the value node being edited by this panel
     */
    public DataConstructorValueNode getValueNode() {
        return valueNode;
    }
    
    /**
     * @return the parent editor of this panel
     */
    public StructuredValueEditor getParentEditor() {
        return parentEditor;
    }
    
    /**
     * Updates the value node of this panel and the child value nodes of its editor panels using
     * the given map of committed values.
     * Called by AlgebraicValueEditor's child commit listener.
     * @param commitValueMap a committed value map from a ValueNodeCommitHelper
     */
    void updateValueNode(Map<ValueNode, ValueNode> commitValueMap) {

        // Get the new data constructor value node.
        DataConstructorValueNode newValueNode = (DataConstructorValueNode) commitValueMap.get(valueNode);
            
        if (newValueNode == null) {
            newValueNode = (DataConstructorValueNode) valueNode.copyValueNode();
        }
            
        List<ValueNode> newValueNodeChildren = new ArrayList<ValueNode>(newValueNode.getChildrenList());
                        
        // Iterate over the argument panels and update their value nodes.
        for (int i = 0; i < editorPanels.length; i++) {
                
            ValueEntryPanel vep = editorPanels[i].getValueEntryPanel();

            ValueNode oldChildValueNode = vep.getOwnerValueNode();
            ValueNode newChildValueNode = commitValueMap.get(oldChildValueNode);

            if (newChildValueNode != null) {

                vep.changeOwnerValue(newChildValueNode);
                vep.validate();
                
                // The preferred size may have changed if the value node type changed.
                editorPanels[i].setPreferredSize(vep.getPreferredSize());

                // Make sure we have the correct value nodes in the children list.
                newValueNodeChildren.set(i, newChildValueNode);
            }
        }
        
        newValueNode.setChildrenList(newValueNodeChildren);
        
        valueNode = newValueNode;
    }
    
    /**
     * Builds a map from value node to type expression for the value node this panel is editing
     * and the owner value nodes of each value entry panel.
     * @param leastConstrainedType the least constrained type that is allowed
     * @return the resulting map
     */
    Map<ValueNode, TypeExpr> getValueNodeToUnconstrainedTypeMap(TypeExpr leastConstrainedType) {

        Map<ValueNode, TypeExpr> returnMap = new HashMap<ValueNode, TypeExpr>();
        
        DataConstructor dataCons = getDataConstructor();
        
        returnMap.put(valueNode, leastConstrainedType);

        for (int i = 0; i < editorPanels.length; i++) {

            ValueNode argValueNode = editorPanels[i].getValueEntryPanel().getOwnerValueNode();

            TypeExpr argType = TypeExpr.getComponentTypeExpr(leastConstrainedType, i, dataCons);

            returnMap.put(argValueNode, argType);
        }

        return returnMap;
    } 
}
