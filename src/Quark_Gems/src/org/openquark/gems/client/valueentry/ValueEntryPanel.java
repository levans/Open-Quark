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
 * ValueEntryPanel.java
 * Creation date: (1/11/01 7:29:38 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.valueentry;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.cal.module.Cal.Utilities.CAL_Time;
import org.openquark.cal.valuenode.ColourValueNode;
import org.openquark.cal.valuenode.JTimeValueNode;
import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.RelativeDateTimeValueNode;
import org.openquark.cal.valuenode.RelativeDateValueNode;
import org.openquark.cal.valuenode.RelativeTimeValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * ValueEntryPanel can collect a value of a specific type from the user.
 * @author Luke Evans
 */
public class ValueEntryPanel extends ValueEditor {

    private static final long serialVersionUID = 7435038299787126535L;

    /** The height of the ValueEntryPanel. */
    public static final int PANEL_HEIGHT = 26;

    /** 
     * Old ValueNode.
     * This is used to check for changes in the value represented by this editor.. */
    private ValueNode oldValueNode;

    /**
     * Flag to denote that the Value is in a change transition, and NOT to
     * fire changed value events.  This can be done at the end of the value transition.
     */
    private boolean isValueTransition;
    
    /** Flag to denote that the value field is gaining focus by way of a mouse press. */
    private boolean isMouseUsed = false;
    
    /** The name of the argument that this panel is collecting a value for. */
    private String argumentName = null;

    /** Indicates whether the type icon should be displayed. */
    private boolean displayTypeIcon = true;

    /** Indicates whether the launch editor button remains when the editor loses focus. */
    private boolean alwaysShowLaunchButton = true;
    
    /**
     * The custom key listener added to the value field for certain value node types.
     * We keep track of it here so it can be removed when the value node type changes.
     */
    private KeyListener valueFieldKeyListener = null;

    /** 
     * A flag to denote whether or not the invokeLater method to display a value editor has run.
     * If this is true it has run, otherwise this is false and it is still pending.
     * While the flag is false, no editor can be launched by clicking the button or type icon.
     */
    private boolean hasDisplayed = true;

    /** The '...' button to launch a value editor. */
    private JButton ivjLaunchEditorButton = null;
    
    /** The type icon that launches the switch type value editor. */
    private JLabel ivjTypeIcon = null;
    
    /** The value field for displaying the value node text value. */
    private JTextComponent ivjValueField = null;

    /**
     * A ValueEntryPanel that mirrors another panel.
     * This panel can be used to edit the same value as the panel that it mirrors.
     * @author Edward Lam
     */
    public static class MirrorValueEntryPanel extends ValueEntryPanel {
        
        private static final long serialVersionUID = -3905172071121287511L;
        
        /** The panel mirrored by this panel */
        private final ValueEntryPanel mirroredPanel;
        
        /**
         * Constructor for a MirrorValueEntryPanel
         * @param valueEditorHierarchyManager
         * @param panelToMirror the panel to be mirrored.
         */
        public MirrorValueEntryPanel(ValueEditorHierarchyManager valueEditorHierarchyManager, ValueEntryPanel panelToMirror) {
            super(valueEditorHierarchyManager, panelToMirror.getOwnerValueNode());
            this.mirroredPanel = panelToMirror;

            // Add a listener to commit the mirrored panel when this panel is committed.
            addValueEditorListener(new ValueEditorAdapter() {
                @Override
                public void valueCommitted(ValueEditorEvent evt) {
                    MirrorValueEntryPanel.this.mirroredPanel.replaceValueNode(getValueNode(), true);
                    MirrorValueEntryPanel.this.mirroredPanel.commitValue();
                }
            });
            
            // Copy over other info.
            setContext(panelToMirror.getContext());
            setArgumentName(panelToMirror.argumentName);
        }
    }

    /**
     * A specialized mouse listener used for the purpose of activating the switching data type mechanism.
     */
    private class SwitchTypeMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {

            // If we can't switch data type then do nothing.
            if (!isEditable()) {
                return;
            }

            SwitchTypeValueEditor newEditor = getSwitchTypeValueEditor();
            newEditor.setOwnerValueNode(getValueNode());
            
            handleLaunchEditor(newEditor, getTypeIcon());
        }
    }
    
    /**
     * A mouse adapter to handle positioning the cursor when a value field is clicked.
     * Creation date: (08/14/2002 3:00:00 PM).
     * @author Steve Norton
     */
    private class ValueFieldMouseAdapter extends MouseAdapter {
        
        /** A flag to indicate that the mouse press has switched focus to the value field from another component. */
        private boolean valueFieldGainingFocus = false;
        
        @Override
        public void mousePressed(MouseEvent evt) {

            // See if the current focus owner is the value field.  
            // If it is NOT then we want to handle the caret positioning in on mouse click.
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            valueFieldGainingFocus = (focusOwner != getValueField());
            
            if (valueFieldGainingFocus) {
                isMouseUsed = true;
            }
        }
        
        @Override
        public void mouseClicked(MouseEvent evt) {

            // If the value field is gaining focus for the first time because of this mouse click
            // then we want to position the caret.
            if (valueFieldGainingFocus) {
                // If the mouse is aimed at one end or the other then use the positioning function
                // otherwise just let the default behaviour do its thing.
                Point mouseLocation = evt.getPoint();
                int textIndex = getValueField().viewToModel(mouseLocation);

                if (textIndex <= 0 || textIndex >= getValueField().getDocument().getLength()) {
                    positionValueFieldCursor();
                    evt.consume();
                }
            }
        }
    }
    
    /**
     * A key listener that gets added to the value field if the value node is for an enumerated type.
     * @author Frank Worsley
     */
    private class EnumTypeKeyListener extends KeyAdapter {
        
        /** The list of value nodes for the data constructors of the type constructor. */
        private final List<ValueNode> valueNodeList = new ArrayList<ValueNode>();
        
        public EnumTypeKeyListener() {
            
            TypeExpr typeExpr = getValueNode().getTypeExpr();
            QualifiedName typeConsName = typeExpr.rootTypeConsApp().getName();
            
            if (!valueEditorManager.getPerspective().isEnumDataType(typeConsName)) {
                throw new IllegalStateException("type is not an enumerated type: " + typeConsName);
            }
            
            DataConstructor[] dataConsArray = valueEditorManager.getPerspective().getDataConstructorsForType(typeConsName);
            
            for (final DataConstructor dataConstructor : dataConsArray) {
                
                // We have to use the value node builder helper instead of building a data constructor value node.
                // That's because although Boolean is an enumerated type, it doesn't use DataConstructorValueNodes.
                valueNodeList.add(valueEditorManager.getValueNodeBuilderHelper().buildValueNode(null, dataConstructor, typeExpr));
            }
        }
        
        @Override
        public void keyPressed(KeyEvent e) {
            
            int current = -1;
            
            for (int i = 0, size = valueNodeList.size(); i < size; i++) {
                ValueNode valueNode = valueNodeList.get(i);
                if (valueNode.sameValue(getValueNode())) {
                    current = i;
                    break;
                }
            }
            
            if (current == -1) {
                throw new IllegalStateException("invalid current value: " + getValueNode());
            }
            
            EnumeratedValueEditor editor = (EnumeratedValueEditor) getValueEditor();
            editor.setHighlightedValue(getValueNode());
            
            int keyCode = e.getKeyCode();
            
            if (keyCode == KeyEvent.VK_UP && current > 0) {
                
                ValueNode newValue = valueNodeList.get(current - 1);
                editor.setOwnerValueNode(newValue);
                handleLaunchEditor(editor, getLaunchEditorButton());
                e.consume();
                
            } else if (keyCode == KeyEvent.VK_DOWN && current < valueNodeList.size() - 1) {
                
                ValueNode newValue = valueNodeList.get(current + 1);
                editor.setOwnerValueNode(newValue);
                handleLaunchEditor(editor, getLaunchEditorButton());
                e.consume();
            
            } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
                Toolkit.getDefaultToolkit().beep();
                e.consume();
            }
        }
    }
    
    /**
     * A key listener that gets added to the value field if the value type is Char.
     * @author Frank Worsley
     */
    private class CharTypeKeyListener extends KeyAdapter {
        
        @Override
        public void keyPressed(KeyEvent e) {
            
            if ((e.getKeyCode() != KeyEvent.VK_ENTER) && (e.getKeyCode() != KeyEvent.VK_ESCAPE) && !e.isAltDown()) {
                
                if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
                    setValueTransition(true);
                }
                
                // Clear away previous char (as long as there is a valid char to replace it).
                if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                    getValueField().setText("");
                }
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            setValueTransition(false);
        }
    }
    
    /**
     * A focus listener for the components of the Value Entry Panel.
     * @author Steve Norton
     */
    private class VEPFocusListener extends FocusAdapter {
        
        @Override
        public void focusGained(FocusEvent e) {
            if (!alwaysShowLaunchButton) {
                getLaunchEditorButton().setVisible(true);
            }

            // If the text field is gaining focus from some other component then the text field's
            // caret must be positioned.  
            JTextComponent textField = getValueField();
            if (e.getSource() == textField && e.getOppositeComponent() != textField) {

                // If the mouse was used to grant focus don't update the position as the mouse listener 
                // would have already done it by now.
                if (!isMouseUsed) {
                    positionValueFieldCursor();
                }
            }
            
            // Reset the mouse used flag
            isMouseUsed = false;
        }

        @Override
        public void focusLost(FocusEvent evt) {
            if (!alwaysShowLaunchButton) {
                getLaunchEditorButton().setVisible(false);
            }
        }
    }
        
    /**
     * KeyListener used to listen for user's commit(Enter), cancel (Esc) and 
     * launch editor (Alt+l) input.
     * Register the key listener on the textfield of this ValueEntryPanel.
     * Note: If the ValueEntryPanel which is registerd with this key listener
     * is on the first level, then commit and cancel does nothing.
     */
    private class ValueEntryPanelKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {

            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                handleCommitGesture();
                evt.consume(); // Don't want the control with the focus to perform its action.

            } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                evt.consume();

            } else if ((evt.getKeyCode() == KeyEvent.VK_L) && evt.isAltDown()) {
                getLaunchEditorButton().doClick();
                evt.consume();

            } else if ((evt.getKeyCode() == KeyEvent.VK_X) && evt.isControlDown()) {

                // Only allow if editable.
                if (isEditable()) {
                    cutToClipboard();
                }
                evt.consume();

            } else if ((evt.getKeyCode() == KeyEvent.VK_C) && evt.isControlDown()) {
                copyToClipboard();
                evt.consume();

            } else if ((evt.getKeyCode() == KeyEvent.VK_V) && evt.isControlDown()) {

                // Only allow if editable.
                if (isEditable()) {
                    pasteFromClipboard();
                }
                evt.consume();
            }
        }
    }

    /**
     * Creates a new ValueEntryPanel initialized with the data in ValueNode.
     * @param valueEditorHierarchyManager
     * @param dataVN 
     */
    public ValueEntryPanel(ValueEditorHierarchyManager valueEditorHierarchyManager, ValueNode dataVN) {

        super(valueEditorHierarchyManager);
        initialize();

        // Configure this ValueEntryPanel with the params.
        setOwnerValueNode(dataVN);
        setInitialValue();
    }
    
    /**
     * Creates a new ValueEntryPanel.
     *   This editor will only be in a semi-initialized state.  
     *   setOwnerValueNode() and setInitialValue() must be called to complete initialization.
     * @param valueEditorHierarchyManager
     */
    ValueEntryPanel(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }

    /**
     * Initialize the class.
     * Note: Extra Set-up code has been added.
     */
    private void initialize() {
        try {
            enableEvents(AWTEvent.FOCUS_EVENT_MASK | AWTEvent.HIERARCHY_EVENT_MASK);
            setName("ValueEntryPanel");
            setMaximumSize(new Dimension(65536, PANEL_HEIGHT));
            setMinimumSize(new Dimension(50, PANEL_HEIGHT));
            setPreferredSize(new Dimension(400, PANEL_HEIGHT));
            setSize(50, PANEL_HEIGHT);

            setBorder(valueEditorManager.getValueEditorBorder(this));

            setLayout(new BorderLayout());
            add(getTypeIcon(), "West");
            add(getLaunchEditorButton(), "East");
            add(getValueField(), "Center");
            
            getLaunchEditorButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ValueEditor editor;
                    if (isEditable()) {
                        editor = getValueEditor();
                        
                    } else {
                        
                        // Try to get outputable only editor
                        editor = valueEditorManager.getValueEditorForValueNode(valueEditorHierarchyManager, getValueNode(), true);
                        if (editor == null) {
                            // No outputable editor. Create string value node and edit this
                            ValueNode node = valueEditorManager.getValueNodeBuilderHelper().getValueNodeForTypeExpr(valueEditorManager.getTypeCheckInfo().getTypeChecker().getTypeFromString(CAL_Prelude.TypeConstructors.String.getQualifiedName(), CAL_Prelude.MODULE_NAME, null));
                            node.setOutputJavaValue(getValueNode().getTextValue());
                            editor = valueEditorManager.getValueEditorForValueNode(valueEditorHierarchyManager, node, true);
                        }
                    }
                    handleLaunchEditor(editor, getLaunchEditorButton());
                }
            });

        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        
        setDisplayTypeIcon(valueEditorManager.showValueEntryPanelTypeIcon());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return getValueField();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
        valueChangedCheck();
        notifyValueCommitted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cancelValue() {
        changeOwnerValue(getOwnerValueNode());
    }
    
    /**
     * Return the LaunchEditorButton property value.
     * @return JButton
     */
    protected JButton getLaunchEditorButton() {
        if (ivjLaunchEditorButton == null) {
            try {
                ivjLaunchEditorButton = new JButton();
                ivjLaunchEditorButton.setName("LaunchEditorButton");
                ivjLaunchEditorButton.setToolTipText("Launch Editor (Alt+L)");
                ivjLaunchEditorButton.setText("");
                ivjLaunchEditorButton.setIcon(new ImageIcon(getClass().getResource("/Resources/ellipsis.gif")));
                ivjLaunchEditorButton.setMargin(new Insets(0, 0, 0, 0));
                ivjLaunchEditorButton.setRequestFocusEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLaunchEditorButton;
    }
    
    /**
     * Return the TypeIcon property value.
     * Note: Extra set-up code has been added.
     * @return JLabel
     */
    protected JLabel getTypeIcon() {
        if (ivjTypeIcon == null) {
            try {
                ivjTypeIcon = new JLabel();
                ivjTypeIcon.setName("TypeIcon");
                ivjTypeIcon.setIcon(new ImageIcon(getClass().getResource("/Resources/notype.gif")));

                // The value editor manager may indicate that we should not be showing borders
                if (valueEditorManager.useValueEntryPanelBorders()) {
                    ivjTypeIcon.setBorder(new EtchedBorder());
                } else {
                    ivjTypeIcon.setBorder(null);
                }

                ivjTypeIcon.setText("");

                ivjTypeIcon.addMouseListener(new SwitchTypeMouseListener());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTypeIcon;
    }
    
    /**
     * @return the value entry field for this value entry panel
     */
    protected JTextComponent getValueField() {
        
        if (ivjValueField == null) {

            TypeExpr typeExpr = getValueNode() != null ? getValueNode().getTypeExpr() : TypeExpr.makeParametricType();
            
            // Date and Time types get a special value field.
            if(typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDate) ||
               typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeTime) ||
               typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime) ||
               typeExpr.isNonParametricType(CAL_Time.TypeConstructors.Time)) {
                
                ivjValueField = new DateTimeValueEntryField(this);
    
            } else {
                
                ivjValueField = new ValueEntryField(this);
                
                // Add a mouse listener that will help with positioning the cursor.
                ivjValueField.addMouseListener(new ValueFieldMouseAdapter());
            }
         
            // Make sure that the ValueFormat has this ValueEntryPanel set.
            // Otherwise, we may miss potential data change events.
            ValueFormat valueFormat = (ValueFormat) ivjValueField.getDocument();
            valueFormat.setChecking(true);
            
            ivjValueField.addKeyListener(new ValueEntryPanelKeyListener());
        }
        
        return ivjValueField;
    }
    
    /**
     * Updates the current value field for a new value node. This removes listeners added for
     * the type of the previous value node and performs other setup for the value field appearance.
     */
    private void updateValueField() {
        
        // Check if the value field needs to be replaced.
        JTextComponent oldValueField = ivjValueField;
        ivjValueField = null;
        JTextComponent newValueField = getValueField();
        
        if (oldValueField.getClass().getName().equals(newValueField.getClass().getName())) {
            ivjValueField = oldValueField;
        } else {
            remove(oldValueField);
            add(newValueField, BorderLayout.CENTER);
            revalidate();
        }
        
        TypeExpr typeExpr = getValueNode() != null ? getValueNode().getTypeExpr() : TypeExpr.makeParametricType();
        
        // If there's an argument name, pass it on to the value field.
        if (argumentName != null) {
            ((ValueEntryField) ivjValueField).setToolTipPrefix(argumentName + ":  ");
        }
        
        // Check if we should use a border.
        if (!valueEditorManager.useValueEntryPanelBorders()) {
            ivjValueField.setBorder(null);
        }
        
        // Remove the old key listener.
        ivjValueField.removeKeyListener(valueFieldKeyListener);
        valueFieldKeyListener = null;
        
        PreludeTypeConstants typeConstants = valueEditorManager.getValueNodeBuilderHelper().getPreludeTypeConstants();
        
        // Add a special key listener for certain types.
        if (typeExpr.sameType(typeConstants.getCharType())) {
            
            valueFieldKeyListener = new CharTypeKeyListener();
            ivjValueField.addKeyListener(valueFieldKeyListener);
        
        } else if (typeExpr.rootTypeConsApp() != null) {
            
            QualifiedName typeConsName = typeExpr.rootTypeConsApp().getName();
            
            if (valueEditorManager.getPerspective().isEnumDataType(typeConsName)) {
                
                valueFieldKeyListener = new EnumTypeKeyListener();
                ivjValueField.addKeyListener(valueFieldKeyListener);
            }
        }
    }

    /**
     * Returns a FontMetrics object set with the Font of the ValueField.
     * Creation date: (24/05/01 5:02:41 PM)
     * @return FontMetrics
     */
    public FontMetrics getValueFieldFontMetrics() {
        return getFontMetrics(getValueField().getFont());
    }
    
    /**
     * Launch the given value editor using the given component to determine the launch location.
     * When this is called, the hierarchy must be in an appropriate state to support the launch 
     * of a child editor from this editor.
     * @param valueEditor the editor to launch
     * @param launcher the component launching the editor
     */
    protected void handleLaunchEditor(final ValueEditor valueEditor, final JComponent launcher) {

        if (!hasDisplayed) {
            return;
        }

        if (valueEditor != null) {
            
            if (ValueEntryPanel.this.getContext().getLeastConstrainedTypeExpr() == null) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(),
                    ValueEditorMessages.getString("VE_ContextBroken"),
                    ValueEditorMessages.getString("VE_UnableToLaunchValueEditor"),
                    JOptionPane.INFORMATION_MESSAGE);
              return;  
            } 
                
            
            // If editor is non-null, then launch it.
            hasDisplayed = false;

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    valueEditor.setContext(new ValueEditorContext() {
                        public TypeExpr getLeastConstrainedTypeExpr() {
                            return ValueEntryPanel.this.getContext().getLeastConstrainedTypeExpr();
                        }
                    });
                    
                    addChildCommitListener(valueEditor);

                    Insets editorInsets = valueEditor.getInsets();
                    Point location = launcher.getLocation();
                    location.x -= editorInsets.left;
                    location.y -= editorInsets.top;
                                        
                    valueEditorHierarchyManager.launchEditor(valueEditor, location, ValueEntryPanel.this);

                    hasDisplayed = true;
                }
            });

        } else {

            // If editor is null, display an error message informing the user that no editor was found.
             
            JOptionPane.showMessageDialog(getTopLevelAncestor(),
                                          ValueEditorMessages.getString("VE_NoValueEditor", getValueNode().getTypeExpr().toString()),
                                          ValueEditorMessages.getString("VE_UnableToLaunchValueEditor"),
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Add a listener to a new child editor so that commits to the child will also be committed to this editor.
     * @param childEditor the child editor to which to add the new listener
     */
    private void addChildCommitListener(final ValueEditor childEditor) {
        childEditor.addValueEditorListener(new ValueEditorAdapter(){
            @Override
            public void valueCommitted(ValueEditorEvent evt) {
                
                replaceValueNode(childEditor.getValueNode(), true);
                
                updateValueField();
                
                commitValue();
            }
        });
    }

    /**
     * Returns a value editor appropriate for this value node, or null if none is appropriate.
     */
    protected ValueEditor getValueEditor() {
        return valueEditorManager.getValueEditorForValueNode(valueEditorHierarchyManager, getValueNode(), false);
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
     * @see org.openquark.gems.client.valueentry.ValueEditor#refreshDisplay()
     */
    @Override
    public void refreshDisplay() {

        updateDisplayedValue();

        // Update colour.
        if (valueEditorManager.useTypeColour()) {
            Color typeColour = valueEditorManager.getTypeColour(getValueNode().getTypeExpr());
            setBackground(typeColour);
        }

        repaint();
    }
    
    /**
     * Notifies this ValueEntryPanel to update the value displayed in the text field with 
     * the value in the ValueNode.
     */
    private void updateDisplayedValue() {

        // Handle this depending on type.
        ValueNode valueNode = getValueNode();
        TypeExpr typeExpr = valueNode.getTypeExpr();

        // Note: if this is set as output, then the value field will not be a
        // DateTimeValueEntryField, but a text field!
        if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDate)) {

            DateTimeValueEntryField dateTimeVef = (DateTimeValueEntryField) getValueField();
            dateTimeVef.setFormat(DateTimeValueEntryField.RELATIVEDATE);
            dateTimeVef.setDate(((RelativeDateValueNode)valueNode).getDateValue());

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeTime)) {

            DateTimeValueEntryField dateTimeVef = (DateTimeValueEntryField) getValueField();
            dateTimeVef.setFormat(DateTimeValueEntryField.RELATIVETIME);
            dateTimeVef.setDate(((RelativeTimeValueNode)valueNode).getTimeValue());

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime)) {

            DateTimeValueEntryField dateTimeVef = (DateTimeValueEntryField) getValueField();
            dateTimeVef.setFormat(DateTimeValueEntryField.RELATIVEDATETIME);
            dateTimeVef.setDate(((RelativeDateTimeValueNode)valueNode).getDateTimeValue());

        } else if (typeExpr.isNonParametricType(CAL_Time.TypeConstructors.Time)) {

            DateTimeValueEntryField dateTimeVef = (DateTimeValueEntryField) getValueField();
            dateTimeVef.setFormat(DateTimeValueEntryField.JTIME);
            dateTimeVef.setDate(((JTimeValueNode)valueNode).getJavaDate());

        } else {

            // We do some special tool tip if the ValueNode is a ListValueNode.
            if (valueNode instanceof ListValueNode) {

                ListValueNode listValueNode = (ListValueNode)valueNode;
                String iconToolTip = "<i>" + valueEditorManager.getTypeName(typeExpr) + "</i>";
                int elementCount = listValueNode.getNElements();
                iconToolTip = iconToolTip + " (length " + elementCount + ")";
                getTypeIcon().setToolTipText("<html><body>" + iconToolTip + "</body></html>");

                // No break here.  Want to go with default stuff.
            }

            setTextValue(valueNode.getTextValue());
        }

       if (isEditable()) {
           getValueField().setForeground(valueEditorManager.isFieldEditable(typeExpr) ? Color.black : Color.gray);
       } else {
           getValueField().setForeground(Color.gray);
       }
        
        // Special handling for colour value nodes.
        if (valueNode instanceof ColourValueNode) {       

            ColourValueNode colourValueNode = (ColourValueNode) valueNode;

            Color colour = colourValueNode.getColourValue();
            Color opaqueColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue());            
            getValueField().setBackground(opaqueColour);
            getValueField().setForeground(opaqueColour);
            getValueField().setSelectedTextColor(opaqueColour);
            getValueField().setSelectionColor(opaqueColour);
            getValueField().setCaretColor(opaqueColour);
            
        } else {
            // Reset to default colours.
            JTextField textField = new JTextField();       // prototypical text field.
            getValueField().setBackground(textField.getBackground());
            getValueField().setForeground(textField.getForeground());
            getValueField().setSelectedTextColor(textField.getSelectedTextColor());
            getValueField().setSelectionColor(textField.getSelectionColor());
            getValueField().setCaretColor(textField.getCaretColor());
        }
        
        // Possible that the data value has changed.  Make a check.
        valueChangedCheck();
    }
    
    /**
     * Enables or disables the launch button based on value entry panel state.
     */
    private void updateLaunchButton() {
       
        // TODO: ValueEditorTableCell also has a version of this method; see if can 
        // use only one.
        
        if (getValueNode() == null) {
            getLaunchEditorButton().setEnabled(false);

        } else if (isEditable()) {
            PreludeTypeConstants typeConstants = valueEditorManager.getValueNodeBuilderHelper().getPreludeTypeConstants();
            // TODO: Can this be removed or also used for numbers ?
            getLaunchEditorButton().setEnabled(!getValueNode().getTypeExpr().sameType(typeConstants.getCharType()));

        } else {
            getLaunchEditorButton().setEnabled(true);
        }
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setEditable(boolean)
     */
    @Override
    public void setEditable(boolean isEditable) {

        super.setEditable(isEditable);

        // If we don't have a value node yet, then this will be done in
        // setOwnerValueNode and setInitialValue anyway.
        if (getValueNode() != null) {
            updatePreferredSize();
            updateDisplayedValue();
        }
        
        updateTypeIconColour();
        updateLaunchButton();
    }
    
    /**
     * @see org.openquark.gems.client.valueentry.ValueEditor#setInitialValue()
     */
    @Override
    public void setInitialValue() {
        updateDisplayedValue();
        updateLaunchButton();
    }
    
    /**
     * Set the name of the argument that this ValueEntryPanel is collecting a value for.
     * This is really only used as a prefix to the text field's tool tip.
     * @param name the name of the argument represented by this value entry panel.
     */
    public void setArgumentName(String name) {
        
        this.argumentName = name;
        
        JTextComponent textField = getValueField();
        if (textField instanceof ValueEntryField) {
            ((ValueEntryField)textField).setToolTipPrefix(name + ":  ");
        }
    }
    
    /**
     * Sets the value of the text field in this ValueEntryPanel.
     * Note: This method will automatically show the text at the left.
     * Note: Setting the text in the text field by this method will NOT affect the data in the 
     * ValueNode of this ValueEntryPanel (so no data value changed events will fire).
     * Creation date: (23/01/01 4:31:58 PM)
     * @param newVal String
     */
    private void setTextValue(String newVal) {

        // First, allow the edit.
        ValueFormat valueFormat = (ValueFormat) getValueField().getDocument();
        valueFormat.setChecking(false);

        getValueField().setText(newVal);

        // Now, show the left part of the text.
        getValueField().setCaretPosition(0);

        // Now, restore checking.
        valueFormat.setChecking(true);
    }
    
    /**
     * Set the type icon of this ValueEntryPanel to the named icon.
     * Creation date: (1/12/01 9:42:51 AM)
     * @param iconName String
     */
    void setTypeIcon(String iconName) {

        // Set the type icon
        getTypeIcon().setIcon(new ImageIcon(getClass().getResource(iconName)));

        updateTypeIconColour();
    }
    
    /**
     * Sets the ValueNode for this ValueEntryPanelEditor and initializes some of the UI set-up.
     * @param newValueNode 
     */
    @Override
    public void setOwnerValueNode(ValueNode newValueNode) {
        
        ValueNode oldOwnerValueNode = getOwnerValueNode();

        super.setOwnerValueNode(newValueNode);
        TypeExpr typeExpr = getValueNode().getTypeExpr();

        // if the value changed, give it a new value entry field.
        if (oldOwnerValueNode == null || !oldOwnerValueNode.sameValue(newValueNode)) {

            // Set-up this ValueEntryPanel with the data in the ValueNode.
            String iconName = valueEditorManager.getTypeIconName(typeExpr);
            this.setTypeIcon(iconName);
    
            // Show or hide the type icon, if necessary.
            setDisplayTypeIcon(valueEditorManager.showValueEntryPanelTypeIcon());

            // Recreate the value field to match the new type.
            updateValueField();
            
            // Register the focus listeners.
            Component[] componentList = this.getComponents();
            for (final Component component : componentList) {
                component.addFocusListener(new VEPFocusListener());
            }

            // Give the ValueEntryPanel a preferred starting size.
            updatePreferredSize();
        }

        getTypeIcon().setToolTipText("<html><body><i>" + valueEditorManager.getTypeName(typeExpr) + "</i></body></html>");

        if (valueEditorManager.useTypeColour()) {
            // Set the background to the type color, so that our etched border has the type color.
            setBackground(valueEditorManager.getTypeColour(newValueNode.getTypeExpr()));
        }
    }
    
    /**
     * Sets whether or not a value change check should be done.
     * If valueTransition is true, then we're in the middle of changing values,
     * and should not do the check.  The check will (should) be done at the end
     * of the valueTransition.  Don't forget to reset to false.
     * @param valueTransition 
     */
    public void setValueTransition(boolean valueTransition) {
        isValueTransition = valueTransition;
    }
    
    /**
     * Sets the preferred size of the panel according to what is displayed in the value field.
     */
    private void updatePreferredSize() {
        
        int preferredWidth = valueEditorManager.getValuePreferredWidth(getValueNode(), getValueFieldFontMetrics(), isEditable());
        preferredWidth += getValueField().getInsets().left + getValueField().getInsets().right;
        preferredWidth += getLaunchEditorButton().getPreferredSize().width;
        preferredWidth += getTypeIcon().getPreferredSize().width;
        preferredWidth += getInsets().left + getInsets().right;
        
        Dimension newPreferredSize = new Dimension(preferredWidth, PANEL_HEIGHT);
        setPreferredSize(newPreferredSize);
    }
    
    /**
     * If editable sets the type icon colour to yellow, otherwise to light gray.
     */
    private void updateTypeIconColour() {

        if (isEditable()) {
            getTypeIcon().setBackground(Color.yellow);
        } else {
            getTypeIcon().setBackground(Color.lightGray);
        }
    }
    
    /**
     * Checks if a data change has occurred, and if it did, then fire ValueEditorEvents to all listeners.
     * Note: Currently, the def'n of a "Data value change" is if the old String CAL value in the ValueNode differs
     * with the new String CAL value in the ValueNode.
     */
    public void valueChangedCheck() {

        // If we are flagged that the value is in transition, don't bother checking.
        if (isValueTransition) {
            return;
        }

        // Special case: Upon first starting up, oldValueNode is null.  
        // Just set it to the current ValueNode.  And, there's no need to fire change event (since it's not possible in this early stage).
        if (oldValueNode == null) {
            oldValueNode = getValueNode().copyValueNode();
        }

        // Now perform the value change check.
        if (!oldValueNode.sameValue(getValueNode())) {

            notifyValueChanged(oldValueNode);
            oldValueNode = getValueNode().copyValueNode();
        }
    }
    
    /**
     * Position the cursor of the value field based on the type and editability of the value.
     */
    private void positionValueFieldCursor() {

        JTextComponent textField = getValueField();
        TypeExpr typeExpr = getValueNode().getTypeExpr();

        // Just position the cursor at the beginning of the document if the text is not editable.
        // Otherwise highlight all of the text.
        if (!isEditable() || !valueEditorManager.isTextEditable(typeExpr)) { 

            textField.setCaretPosition(0);

        } else {

            Document doc = textField.getDocument();
            if (doc != null) {
                textField.setCaretPosition(getValueField().getDocument().getLength());
                textField.moveCaretPosition(0);
            }
        } 
    }
    
    /**
     * Returns a value editor which can switch the value type.
     */
    protected SwitchTypeValueEditor getSwitchTypeValueEditor() {
        return new SwitchTypeValueEditor(valueEditorHierarchyManager);
    }

    /**
     * Returns whether the type icon should be displayed in the editor.
     * This can be overridden by subclasses to return True or False regardless of the
     * displayTypeIcon member flag.
     */
    public boolean displayTypeIcon() {
        return displayTypeIcon;
    }

    /**
     * Sets whether the type icon should be displayed in the editor.
     */
    private void setDisplayTypeIcon(boolean displayTypeIcon) {
        this.displayTypeIcon = displayTypeIcon;

        getTypeIcon().setVisible(displayTypeIcon());
    }

    /**
     * Sets whether the launch button is always displayed, or only when the editor has focus.
     */
    public void setAlwaysShowLaunchButton(boolean alwaysShowLaunchButton) {
        if (alwaysShowLaunchButton != this.alwaysShowLaunchButton) {
            this.alwaysShowLaunchButton = alwaysShowLaunchButton;

            if (alwaysShowLaunchButton) {
                getLaunchEditorButton().setVisible(true);

            } else {
                getLaunchEditorButton().setVisible(hasFocus());
            }
        }
    }

    /**
     * This is used by the TableTopGemPainter to draw a fake VEP image onto the tabletop.
     * The paint() method can't be used directly since it results in weird drawing artifacts on the table top.
     * @param g the Graphics object to draw with
     */    
    public void paintOnTableTop(Graphics g) {
        paintComponent(g);
        paintBorder(g);
        paintChildren(g);
    }
    
    /**
     * We override this to set the caret color to white and deselect all text
     * when the VEP is hidden. This effectively makes the caret invisible
     * This is so that the TableTopGemPainter can paint the fake VEP ghost
     * image without the caret being visible, making it look like the text
     * field is non-focused.
     * @param visible whether or not the VEP should be visible
     */
    @Override
    public void setVisible(boolean visible) {
        
        if (visible) {
            getValueField().setCaretColor(Color.BLACK);
        } else {
            getValueField().setSelectionStart(0);
            getValueField().setSelectionEnd(0);
            getValueField().setCaretColor(Color.WHITE);
            getValueField().setCaretPosition(getValueField().getText().length());
        }
        
        super.setVisible(visible);
    }
    
    /**
     * This is used by the TableTop to determine the correct tooltip text to
     * use when the mouse is hovering over a certain part of a VEP ghost image.
     * @param location the location for which to get the tooltip text
     * @return the tooltip text for the child component at the given location
     */
    public String getToolTipText(Point location) {
        
        JComponent child = (JComponent) getComponentAt(location);
        
        if (child != null) {
            return child.getToolTipText();
        }
        
        return null;
    }
    
    /**
     * This is used by the TableTop to determine the correct cursor to use
     * when the mouse is hovering over a certain part of the VEP ghost image.
     * @param location the location for which to get a cursor
     * @return Cursor the cursor to use for the given location
     */
    public Cursor getCursor(Point location) {
        
        JComponent child = (JComponent) getComponentAt(location);
        
        if (child == getValueField()) {
            return Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
        }
        
        return Cursor.getDefaultCursor();
    }
}
