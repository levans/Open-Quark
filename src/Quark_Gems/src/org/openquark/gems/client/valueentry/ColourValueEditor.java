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
 * ColourValueEditor.java
 * Creation date: (06/03/01 1:27:53 PM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.awt.event.MouseAdapter;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import org.openquark.cal.valuenode.ColourValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * The ValueEditor for Colours (made up of RGB).
 * Creation date: (06/03/01 1:27:53 PM)
 * @author Michael Cheng
 */
class ColourValueEditor extends ValueEditor {
    
    private static final long serialVersionUID = 985431548979212895L;

    /**
     * A custom value editor provider for the ColourValueEditor.
     */
    public static class ColourValueEditorProvider extends ValueEditorProvider<ColourValueEditor> {
        
        public ColourValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof ColourValueNode;
        }

        /**
         * Returns a new instance of the editor.
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public ColourValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            ColourValueEditor editor = new ColourValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
        
    }

    private JColorChooser ivjColourChooser = null;
    private JPanel ivjIntermediatePanel = null;
    
    /**
     * ColourValueEditor constructor.
     * @param valueEditorHierarchyManager
     */
    protected ColourValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }
    
    /**
     * Initialize the class.
     * Note: Extra set-up code has been added.
     */
    private void initialize() {
        try {
            setName("ColourValueEditor");
            setLayout(new java.awt.BorderLayout());
            add(getIntermediatePanel(), "Center");

        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        // To save space, get rid of some of the options for colour.
        // Get rid of the preview panel.    
        getColourChooser().setPreviewPanel(new JPanel());

        // HACK!! "workaround" for a bug (id: 4759306) in jdk1.4, 1.4.1 -- remove the preview panel manually.
        Component[] components = getColourChooser().getComponents();
        for (final Component component : components) {

            if (component instanceof JComponent) {

                Border border = ((JComponent)component).getBorder();
                if (border instanceof TitledBorder && ((TitledBorder)border).getTitle().equals("Preview")) {
                    getColourChooser().remove(component);
                    break;
                }
            }
        }

        // Remove the last 2 panels.
        AbstractColorChooserPanel[] colorChooserPanels = getColourChooser().getChooserPanels();
        if (colorChooserPanels.length >= 3) {
            getColourChooser().removeChooserPanel(colorChooserPanels[colorChooserPanels.length - 1]);
            getColourChooser().removeChooserPanel(colorChooserPanels[colorChooserPanels.length - 2]);
        }

        // Make sure that no mouse clicks 'leak' thru 
        // (meaning that the component under this ColourValueEditor may get the mouse click).
        addMouseListener(new MouseAdapter() {});

        // Make sure that the user's commit and cancel keyboard input is watched.
        getColourChooser().addKeyListener(new ValueEditorKeyListener());
        
        setSize(getPreferredSize());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
        ValueNode returnVN = new ColourValueNode(getColourChooser().getColor(), getValueNode().getTypeExpr());
        replaceValueNode(returnVN, false);
        notifyValueCommitted();
    }
    
    /**
     * Return the ColourChooser property value.
     * @return JColorChooser
     */
    private JColorChooser getColourChooser() {
        if (ivjColourChooser == null) {
            try {
                ivjColourChooser = new JColorChooser();
                ivjColourChooser.setName("ColourChooser");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjColourChooser;
    }
    
    /**
     * Returns the ColourValueNode containing the data for this ColourValueEditor.
     * Creation date: (04/07/01 10:25:59 AM)
     * @return ColourValueNode
     */
    public ColourValueNode getColourValueNode() {
        return (ColourValueNode) getValueNode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return getColourChooser();
    }
    
    /**
     * Return the IntermediatePanel property value.
     * @return JPanel
     */
    private JPanel getIntermediatePanel() {
        if (ivjIntermediatePanel == null) {
            try {
                ivjIntermediatePanel = new JPanel();
                ivjIntermediatePanel.setName("IntermediatePanel");
                ivjIntermediatePanel.setLayout(new java.awt.BorderLayout());
                getIntermediatePanel().add(getColourChooser(), "Center");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIntermediatePanel;
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
     * Sets the initial value/colour in the colour chooser.
     * Creation date: (06/03/01 1:27:53 PM)
     */
    @Override
    public void setInitialValue() {
        getColourChooser().setColor(getColourValueNode().getColourValue());
    }
}
