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
 * ArgumentExplorer.java
 * Creation date: May 26, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.valueentry.ValueEditor;

/**
 * This explorer presents a tree-based interface for viewing and manipulating the collector arguments. 
 * Note that this is primarily a UI class. The actual manipulation of the arguments is delegated to the ArgumentExplorerOwner.
 * @author Edward Lam
 */
public class ArgumentExplorer extends JComponent {
    private static final long serialVersionUID = -2718309462263262697L;

    // Icons for the toolbar.
    private static final ImageIcon showAllArgumentsIcon = new ImageIcon(ArgumentExplorer.class.getResource("/Resources/showAllArguments.gif"));
    
    /** Constant to use when accessing/setting the selected state for an action. */
    private static final String ACTION_SELECTED_KEY = "SelectedState";

    /** The owner of the explorer, which completes most of the heavy duty work. */
    private final ArgumentExplorerOwner argumentExplorerOwner;
    
    /** The tree that is currently displayed. Note that this tree is reconstructed at each update. */
    private final ArgumentTree argumentTree;
    
    /** Wraps the explorer tree to ensure proper scrolling */
    private final JScrollPane treeScrollPane;
        
    /** The button panel with the buttons to modify the tree. */
    private JPanel buttonPanel = null;

    /** The button to toggle displaying unused arguments. */
    private JToggleButton displayUnusedArgumentsButton = null;

    /** The action for displaying unused arguments. */
    private Action displayAllArgumentsAction = null;
    
    //
    // The following are also held by the argument tree model.  
    //  They are different from the corresponding members in the model when the tree enters run mode.
    //
    
    /** The collector gem to display in the tree in edit mode. */
    private CollectorGem collectorToDisplay;
    
    /** Whether the tree should display all arguments when in edit mode. */
    private boolean displayAllArguments = false;
    
    
    /**
     * Default constructor for the ArgumentExplorer.
     * @param gemGraph the related GemGraph.
     * @param owner the owner of this explorer
     */
    public ArgumentExplorer(GemGraph gemGraph, ArgumentExplorerOwner owner) {
        if (owner == null) {
            throw new NullPointerException();
        }
        this.argumentExplorerOwner = owner; 

        // Create and set up the argument tree.
        this.argumentTree = new ArgumentTree(gemGraph, owner);
        argumentTree.setAutoscrolls(true);  

        // Create a scroll pane around the tree
        treeScrollPane = new JScrollPane(argumentTree);
        treeScrollPane.setPreferredSize(new Dimension(500,500));

        // Add the scroll pane as the centre component for the explorer
        this.setLayout(new BorderLayout());
        this.add(treeScrollPane, BorderLayout.CENTER);
        
        // Create a button panel and add it at the top.
        JPanel buttonPanel = getArgumentTreeButtonPanel();
        this.add(buttonPanel, BorderLayout.NORTH);
        
        ArgumentTreeModel argumentTreeModel = argumentTree.getArgumentTreeModel();
        this.collectorToDisplay = argumentTreeModel.getDisplayedCollector();
        this.displayAllArguments = argumentTreeModel.getDisplayUnusedArguments();
    }

    /**
     * @return Returns the argumentExplorerOwner.
     */
    public ArgumentExplorerOwner getArgumentExplorerOwner() {
        return argumentExplorerOwner;
    }

    /**
     * Enables and disables the mouse events (such that the explorer can be used purely as a view).
     * @param enabled whether to enable mouse events
     */
    public void enableMouseInputs(boolean enabled) { 
        // enable mouse inputs on the argument tree.
        argumentTree.enableMouseInputs(enabled);
    }
    
    /**
     * Display the controls for argument entry.
     * @param inputToEditorMap map from input to editor, for those inputs for which the currently
     *  running gem requires arguments.  An iterator on this map should return the inputs in the correct order.
     */
    public void showArgumentControls(Map<PartInput, ValueEditor> inputToEditorMap) {
        argumentTree.showArgumentControls(inputToEditorMap);
        
        // Temporarily change the tree display so that it focuses on the target collector.
        ArgumentTreeModel treeModel = argumentTree.getArgumentTreeModel();
        treeModel.setDisplayUnusedArguments(false);
        treeModel.setCollectorToDisplay(treeModel.getGemGraph().getTargetCollector());
        
        // Disable the button action.
        displayAllArgumentsAction.setEnabled(false);
    }
    
    /**
     * Hide the controls for argument entry.
     */
    public void hideArgumentControls() {
        argumentTree.hideArgumentControls();
        
        // Restore the tree display.
        ArgumentTreeModel treeModel = argumentTree.getArgumentTreeModel();
        treeModel.setDisplayUnusedArguments(displayAllArguments);
        treeModel.setCollectorToDisplay(collectorToDisplay);

        // Enable the button action.
        displayAllArgumentsAction.setEnabled(true);
    }

    /**
     * Handle the case where the argument values have been cleared.
     */
    public void resetArgumentValues() {
        argumentTree.getArgumentTreeModel().targetArgumentNodesChanged();
    }
    
    /**
     * @return the JPanel with the buttons to modify the argument tree.
     */
    private JPanel getArgumentTreeButtonPanel() {
        
        if (buttonPanel == null) {
            
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            
            // Create a toolbar.
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setRollover(true);
            toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
            toolBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 2));

            // Add the buttons to the toolbar.
            toolBar.addSeparator();
            toolBar.add(getDisplayAllArgumentsButton());

            // The toolbar appears on the right.
            buttonPanel.add(toolBar, BorderLayout.EAST);
        }        
        
        return buttonPanel;
    }

    /**
     * Returns the toggle button for displaying all arguments in the explorer tree.
     * @return JToggleButton
     */
    private JToggleButton getDisplayAllArgumentsButton() {

        if (displayUnusedArgumentsButton == null) {

            displayUnusedArgumentsButton = makeNewButton(getDisplayAllArgumentsAction(), null);
            displayUnusedArgumentsButton.setSelected(argumentTree.getArgumentTreeModel().getDisplayUnusedArguments());
            
            // Add a property change listener to this action so we can change the state of
            // the button if the corresponding menu item invokes this action.
            getDisplayAllArgumentsAction().addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(ACTION_SELECTED_KEY)) {
                        boolean displayingUnusedArguments = ((Boolean) e.getNewValue()).booleanValue();
                        String toolTipId = displayingUnusedArguments ? "AE_DisplayingAllArgumentsToolTip" : "AE_NotDisplayingAllArgumentsToolTip";
                        displayUnusedArgumentsButton.getModel().setSelected(displayingUnusedArguments);
                        displayUnusedArgumentsButton.setToolTipText(GemCutter.getResourceString(toolTipId));
                    }
                }
            });
        }        

        return displayUnusedArgumentsButton; 
    }      

    /**
     * Return a new action to toggle if all arguments should be show in the explorer tree..
     * @return Action the new displayUnimportedGemsAction.
     */
    private Action getDisplayAllArgumentsAction() {
        
        if (displayAllArgumentsAction == null) {
        
            String toolTipId = argumentTree.getArgumentTreeModel().getDisplayUnusedArguments() ? 
                               "AE_DisplayingAllArgumentsToolTip" : "AE_NotDisplayingAllArgumentsToolTip";
            
            displayAllArgumentsAction = new AbstractAction (GemCutter.getResourceString("AE_DisplayAllArguments"), showAllArgumentsIcon) {
                private static final long serialVersionUID = -536379607757716250L;

                public void actionPerformed(ActionEvent evt) {
                    ArgumentTreeModel treeModel = argumentTree.getArgumentTreeModel();
                    boolean wasDisplayingAllArguments = argumentTree.getArgumentTreeModel().getDisplayedCollector() == null;
                    displayAllArguments = !wasDisplayingAllArguments;
                    treeModel.setDisplayUnusedArguments(displayAllArguments);

                    // Set the collector to display in the tree model.
                    if (!wasDisplayingAllArguments) {
                        // Display all arguments.
                        collectorToDisplay = null;
                        
                    } else {
                        // Display the target only.
                        collectorToDisplay = treeModel.getGemGraph().getTargetCollector();
                    }
                    treeModel.setCollectorToDisplay(collectorToDisplay);

                    // Update the action map.
                    putValue(ACTION_SELECTED_KEY, Boolean.valueOf(treeModel.getDisplayUnusedArguments()));
                }
            };
    
            displayAllArgumentsAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString(toolTipId));
            displayAllArgumentsAction.setEnabled(true);
        }
        
        return displayAllArgumentsAction;
    }


    /**
     * Creates a new JButton and configures it for use in the button panel.
     * 
     * TODOEL: This is copied from BrowserTreeActions.java.
     * 
     * @param action the action used to configure the JButton
     * @param buttonGroup the button group the button belongs in (can be null)
     * @return JButton
     */
    private static JToggleButton makeNewButton(Action action, ButtonGroup buttonGroup) {
        
        JToggleButton newButton = new JToggleButton();
        
        // Specify some of the customized characteristics here.
        newButton.setAction(action);
        newButton.setText(null);
        newButton.setMargin(new Insets(0, 0, 0, 0));
        
        // Clear the mnemonic so that the buttons are not activated by them.
        newButton.setMnemonic(KeyEvent.KEY_LOCATION_UNKNOWN);
        
        newButton.setBackground(new Color(0,0,0,0));
        newButton.setOpaque(false);        
        
        // Add it to the button group
        if (buttonGroup != null) {
            buttonGroup.add(newButton);
        }
        
        return newButton;
    }
}
