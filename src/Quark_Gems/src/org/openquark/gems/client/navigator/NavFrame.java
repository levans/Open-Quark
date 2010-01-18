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
 * NavFrame.java
 * Creation date: Jul 4, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.metadata.CALExample;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.gems.client.GemCutter;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;
import org.openquark.gems.client.utilities.PreferencesHelper;
import org.openquark.util.UnsafeCast;
import org.openquark.util.WildcardPatternMatcher;
import org.openquark.util.ui.UIUtilities;


/**
 * This is class implements the CAL Navigator that allows the user to browse the
 * different CAL language entities and to view/edit their metadata.
 * 
 * @author Frank Worsley
 */
public class NavFrame extends JDialog {
    
    private static final long serialVersionUID = 885869144342482223L;

    /**
     * This listener updates the state of the UI if the metadata displayed
     * in the HTML viewer changes.
     * @author Frank Worsley
     */
    private class NavDocumentListener implements DocumentListener {

        public void changedUpdate(DocumentEvent e) {
            updateState();
        }

        public void insertUpdate(DocumentEvent e) {
            updateState();
        }

        public void removeUpdate(DocumentEvent e) {
            updateState();
        }
        
        private void updateState() {

            NavTreeModel model = navTree.getModel();
            NavTreeNode node = model.getNodeForAddress(viewerPane.getCurrentAddress());

            getBackButton().setEnabled(viewerPane.getBackHistorySize() > 0);
            getForwardButton().setEnabled(viewerPane.getForwardHistorySize() > 0);
            
            NavAddress address = viewerPane.getCurrentAddress();
            getEditButton().setEnabled(NavAddressHelper.getMetadata(owner, address) != null);        

            TreePath selectionPath = navTree.getSelectionPath();
            NavTreeNode selected = (selectionPath != null) ? (NavTreeNode) selectionPath.getLastPathComponent() : null;
            
            if (node != null && node != selected) {
                TreePath path = new TreePath(node.getPath());
                navTree.setSelectionPath(path);
                navTree.scrollPathToVisible(path);
            }            
        }
    }

    /**
     * This listener updates the displayed HTML content if a node is selected in the tree.
     * @author Frank Worsley
     */
    private class NavTreeSelectionListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {
        
            TreePath path = e.getNewLeadSelectionPath();
        
            if (path == null) {
                return;
            }
        
            NavTreeNode treeNode = (NavTreeNode) path.getLastPathComponent();
            
            if (viewerPane.getCurrentAddress().withAnchor(null).equals(treeNode.getAddress())) {
                return;
            }
            
            stopEditing(true);
            viewerPane.displayMetadata(treeNode.getAddress());
        }
    }
    
    /**
     * A mouse listener for the navigation tree that displays copy/paste menu items for
     * easily duplicating metadata.
     */
    private class NavTreeMouseListener extends MouseAdapter {
        
        public void mousePressed(MouseEvent e) {
            
            // Select the node if the user presses the right mouse button.
            if (SwingUtilities.isRightMouseButton(e)) {
                
                TreePath nodePath = navTree.getPathForLocation(e.getX(), e.getY());
                
                if (nodePath != null) {
                    navTree.setSelectionPath(nodePath);
                }
            }
            
            maybeShowPopupMenu(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopupMenu(e);
        }
        
        private void maybeShowPopupMenu(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.add(getCopyMenuItem());
                popupMenu.show(navTree, e.getX(), e.getY());
            }
        }
    }
    
    /* Mnemonics for the toolbar buttons. */
    private static final Integer FLAT_ABBREVIATED_DISPLAY_MODE_MNEMONIC = Integer.valueOf(KeyEvent.VK_F);
    private static final Integer FLAT_FULLY_QUALIFIED_DISPLAY_MODE_MNEMONIC = Integer.valueOf(KeyEvent.VK_Q);
    private static final Integer HIERARCHICAL_DISPLAY_MODE_MNEMONIC = Integer.valueOf(KeyEvent.VK_H);
    
    private static final Integer GO_BACK_MNEMONIC      = Integer.valueOf(KeyEvent.VK_LEFT);
    private static final Integer GO_FORWARD_MNEMONIC   = Integer.valueOf(KeyEvent.VK_RIGHT);
    private static final Integer TOGGLE_PANEL_MNEMONIC = Integer.valueOf(KeyEvent.VK_N);
    private static final Integer EDIT_MNEMONIC         = Integer.valueOf(KeyEvent.VK_E);
    private static final Integer EDIT_PARENT_MNEMONIC  = Integer.valueOf(KeyEvent.VK_LEFT);
    private static final Integer SAVE_MNEMONIC         = Integer.valueOf(KeyEvent.VK_S);
    private static final Integer CLOSE_MNEMONIC        = Integer.valueOf(KeyEvent.VK_C);
    private static final Integer PRINT_MNEMONIC        = Integer.valueOf(KeyEvent.VK_P);
    
    /* Preference key names. */
    private static final String DIALOG_PROPERTIES_PREF_KEY = "dialogProperties";
    private static final String DIVIDER_LOCATION_PREF_KEY = "dividerLocation";
    private static final String NAV_TREE_DISPLAY_MODE = "navTreeDisplayMode";
    
    /* Preference default values. */
    private static final int DIVIDER_LOCATION_DEFAULT = 1;
    
    /** The minimum size for the window if the side pane is hidden. */
    private static final Dimension MIN_SIZE_HIDDEN = new Dimension(470, 445);
    
    /** The minimum size for the window if the side pane is shown. */
    private static final Dimension MIN_SIZE_SHOWN = new Dimension(625, 445);

    /** The owner of the navigator frame. */
    private final NavFrameOwner owner;

    /** The editor that is used to display the HTML content. */
    private final NavViewerPane viewerPane;
    
    /** The scroll pane that houses the HTML display or metadata editing component. */
    private final JScrollPane navScrollPane;

    /** The split pane that holds the side panel and HTML viewer. */
    private final JSplitPane splitPane;
    
    /** The toolbar at the top of the main viewing area. */
    private JToolBar toolBar;

    /** The saved location of the split pane divider. */
    private int savedDividerLocation = -1;
    
    /** The toolbar at the top of the navigation tree. */
    private JToolBar navTreeToolBar;
    
    /** The button group for the display mode buttons for the nav tree. */
    private ButtonGroup navTreeDisplayModeButtonGroup;

    /** The flat abbreviated display mode button for the nav tree toolbar. */
    private JToggleButton flatAbbreviatedDisplayModeButton;
    
    /** The flat fully qualified display mode button for the nav tree toolbar. */
    private JToggleButton flatFullyQualifiedDisplayModeButton;
    
    /** The hierarchical display mode button for the nav tree toolbar. */
    private JToggleButton hierarchicalDisplayModeButton;
    
    /** The navigation tree that displays the CAL entities. */
    private NavTree navTree = null;
    
    /** If currently editing metadata, this is the editor panel being used. */
    private NavEditorPanel editorPanel = null;
    
    /** If editing argument metadata, this is the editor panel that was editing the parent metadata. */
    private NavEditorPanel parentEditorPanel = null;
    
    /** The back button for the navigation toolbar. */
    private JButton backButton = null;
    
    /** The forward button for the navigation toolbar. */
    private JButton forwardButton = null;

    /** The print button for the navigation toolbar. */
    private JButton printButton = null;
    
    /** The edit button for the navigation toolbar. */
    private JButton editButton = null;
    
    /** The save button for the navigation toolbar. */
    private JButton saveButton = null;
    
    /** The done button for the navigation toolbar. */
    private JButton closeButton = null;
    
    /** The edit parent button for the navigation toolbar. */
    private JButton editParentButton = null;
    
    /** The toggle button for hiding/showing the navigation tree. */
    private JToggleButton showTreeButton = null;
    
    /** The copy metadata button. */
    private JButton copyButton = null;
    
    /** The paste metadata button. */
    private JButton pasteButton = null;
    
    /** The panel that contains the search text field and related components. */
    private JPanel searchPanel = null;
    
    /** The search text field for the search panel. */
    private JTextField searchField = null;
    
    /** The metadata that was copied by the user. */
    private CALFeatureMetadata copiedMetadata = null;

    /**
     * Constructs a new NavFrame object.
     * @param owner the owner frame of this navigator dialog
     */
    public NavFrame(NavFrameOwner owner) {
                              
        super(owner.getParent(), NavigatorMessages.getString("NAV_PropertiesBrowser_Header"));

        this.owner = owner;

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
        splitPane.setResizeWeight(0);
        splitPane.setOneTouchExpandable(true);
        
        final JEditorPane headerPane = getHeaderPane();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPane, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Create the JPanel that houses the HTML viewer
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(getToolBar(), BorderLayout.NORTH);

        // Create the viewer component.
        viewerPane = new NavViewerPane(owner);
        viewerPane.getDocument().addDocumentListener(new NavDocumentListener());
        navScrollPane = new JScrollPane(viewerPane);
        navScrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(navScrollPane, BorderLayout.CENTER);

        // Create the navigation tree
        navTree = new NavTree();
        navTree.addTreeSelectionListener(new NavTreeSelectionListener());
        navTree.addMouseListener(new NavTreeMouseListener());
        final JScrollPane navTreeScrollPane = new JScrollPane(navTree);
        final JPanel navTreePanel = new JPanel(new BorderLayout());
        navTreePanel.add(getNavTreeToolBar(), BorderLayout.NORTH);
        navTreePanel.add(navTreeScrollPane, BorderLayout.CENTER);

        // Setup the split pane. Setting the initial divider location for the split
        // pane seems to be not working properly. The only way I could make the navigation
        // tree show up with an appropriate inital size is to set its minimum size to that.
        navTreePanel.setMinimumSize(new Dimension(180, 445));
        splitPane.setLeftComponent(navTreePanel);
        splitPane.setRightComponent(mainPanel);
                
        // Add a resize listener to the scrollpane to detect when the divider changes location
        navScrollPane.addComponentListener(new ComponentAdapter() {
            
            public void componentResized(ComponentEvent e) {
                
                // enforce minimum window size
                int location = splitPane.getDividerLocation();
                Dimension minSize = (location > 2) ? MIN_SIZE_SHOWN : MIN_SIZE_HIDDEN;
                setValidSize(minSize);
                validate();
                
                // remember the divider location
                if (splitPane.getDividerLocation() > 1) {
                    savedDividerLocation = splitPane.getDividerLocation();
                }
                
                // update toggle button
                getShowTreeButton().setSelected(splitPane.getDividerLocation() > 1);
            }
        });
        
        // Add a window listener to stop editing when the window is closed
        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent e) {
                
                if (isEditing()) {
                    stopEditing(false);            
                }
                
                Preferences prefs = getPreferences();
                PreferencesHelper.putDialogProperties(prefs, DIALOG_PROPERTIES_PREF_KEY, NavFrame.this);

                prefs.putInt(DIVIDER_LOCATION_PREF_KEY, splitPane.getDividerLocation());
            }
            
            public void windowOpened(WindowEvent e) {
                // Sometimes the header pane doesn't redraw when the window appears
                // so we force it to repaint here
                headerPane.repaint();
            }            
        });

        // Display the start page
        viewerPane.displayMetadata(NavAddress.getRootAddress(NavAddress.WORKSPACE_METHOD));
        
        Preferences prefs = getPreferences();
        
        PreferencesHelper.getDialogProperties(prefs, DIALOG_PROPERTIES_PREF_KEY, this, MIN_SIZE_SHOWN, new Point(50, 50));
 
        splitPane.setDividerLocation(prefs.getInt(DIVIDER_LOCATION_PREF_KEY, DIVIDER_LOCATION_DEFAULT));
        
        TreeViewDisplayMode navTreeDisplayMode = TreeViewDisplayMode.fromName(prefs.get(NAV_TREE_DISPLAY_MODE, TreeViewDisplayMode.FLAT_ABBREVIATED.getName()));
        setNavTreeDisplayModeButtonStates(navTreeDisplayMode);
    }

    /**
     * @return the preferences instance for this class.
     */
    private Preferences getPreferences() {
        return Preferences.userNodeForPackage(NavFrame.class);
    }

    /**
     * Overridden to enforce a minimum size for the dialog.
     */    
    public void doLayout() {
        
        // enforce a different minimum size depending on the window state
        int location = splitPane.getDividerLocation();
        Dimension minSize = (location > 2) ? MIN_SIZE_SHOWN : MIN_SIZE_HIDDEN;
        setValidSize(minSize);
        
        super.doLayout();
    }
    
    /**
     * If the window is smaller then its minimum size then the
     * size is set to the minimum size.
     */
    private void setValidSize(Dimension minSize) {

        Dimension size = getSize();
            
        if (size.height < minSize.height) {
            size.height = minSize.height;
        }
        if (size.width  < minSize.width) {
            size.width = minSize.width;
        }
            
        if (!size.equals(getSize())) {
            setSize(size);
        }
    }
    
    /**
     * Refreshes the navigation tree by reloading the tree model from the perspective.
     * This should be called if a new CAL entity is added to the perspective, so that
     * it appears in the navigation tree.
     */
    public void refreshTree() {
        final NavTreeModel navModel = new NavTreeModel();
        navModel.load(owner.getPerspective(), getNavTreeDisplayMode());
        
        // JTree.setModel() causes the UI to be updated.
        // Ensure this happens on the AWT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                navTree.setModel(navModel);
                navTree.collapseToModules();
            }
        });
    }

    /**
     * Refreshes the viewer pane to display the latest metadata. This should be called
     * when displaying the properties browser, to be sure it displays the correct information.
     */
    public void refreshViewer() {
        if (!isEditing()) {
            viewerPane.refresh();
        }
    }
    
    /**
     * Called when the metadata being displayed or edited should be refreshed.
     * This usually happens as a result of an undo or redo operation. This will only
     * perform the actual refresh operation if the metadata currently being viewed
     * or edited matches the url of the metadata that has changed.
     * @param address the address that identifies the metadata object to refresh
     */
    public void refreshMetadata(NavAddress address) {

        NavAddress viewerAddress = viewerPane.getCurrentAddress().withAllStripped();

        if (isEditing()) {

            NavAddress editAddress = editorPanel.getAddress(); 
           
            // See below why we always refresh for collectors.
            
            if (address.withAllStripped().equals(editAddress.withAllStripped()) ||
                editAddress.getMethod() == NavAddress.COLLECTOR_METHOD) {
                
                editorPanel.refresh();
            }

        } else if (address.withAllStripped().equals(viewerAddress.withAllStripped()) ||
                   viewerAddress.getMethod() == NavAddress.COLLECTOR_METHOD) {
            
            // Refresh the viewer pane if the address is the same or if we are viewing collector metadata.
            // That's because some of the collector argument names may depend on the argument names of
            // a gem that could have changed, so we need to always refresh for collectors.
            viewerPane.refresh();
        }
    }    
    
    /**
     * @return the owner of this navigator frame
     */
    public NavFrameOwner getNavigatorOwner() {
        return owner;
    }

    /**
     * Sets the side panel to be visible or hidden. A hidden side panel can be expanded again.
     * If you want to disable expansion of the side panel you have to remove it.
     * @param sidePanelVisible true if the side panel should be shown, false for it to be hidden
     */
    public void setSidePanelVisible(boolean sidePanelVisible) {
        
        if (sidePanelVisible) {
            splitPane.setDividerLocation(savedDividerLocation > 1 ? savedDividerLocation : -1);
        } else {
            savedDividerLocation = splitPane.getDividerLocation();
            splitPane.setDividerLocation(1);
        }
    }
    
    /**
     * Display the metadata for the given address.
     * @param address the address to display metadata for
     */    
    public void displayMetadata(NavAddress address) {

        stopEditing(true);
            
        NavTreeModel treeModel = navTree.getModel();
        NavTreeNode metadataNode = treeModel.getNodeForAddress(address);
        
        if (metadataNode != null) {
            TreePath selectionPath = new TreePath(metadataNode.getPath());
        
            if (!selectionPath.equals(navTree.getSelectionPath())) {
                // The metadata display will be updated as the new node is selected
                navTree.setSelectionPath(selectionPath);
                navTree.scrollPathToVisible(selectionPath);
            } else {
                // If the node is already selected, just refresh the viewer
                viewerPane.displayMetadata(address);
                viewerPane.refresh();
            }
        
        } else {
            // We don't seem to have a node for this metadata. That means it must be
            // a collector. So just directly tell the viewpane what to display.
            viewerPane.displayMetadata(address);
            viewerPane.refresh();                
        }
            
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Edit the metadata object for the given address.
     * @param address the address of the metadata object to edit
     */
    public void editMetadata(NavAddress address) {
        
        boolean alreadyEditing = isEditing();

        if (alreadyEditing && address.equals(editorPanel.getAddress())) {
            return;
        }
        
        saveChanges();
        
        // if we are going back to editing the parent metadata, reuse the editing panel
        if (parentEditorPanel != null) {
            
            NavAddress parentAddress = parentEditorPanel.getAddress();
            
            if (address.equals(parentAddress)) {
                editorPanel = parentEditorPanel;
                editorPanel.addFocusListener();
                editorPanel.refresh();
            } else {
                editorPanel = new NavEditorPanel(owner, address);
            }

            parentEditorPanel = null;
            
        } else if (address.getParameter(NavAddress.ARGUMENT_PARAMETER) != null) {
            parentEditorPanel = editorPanel;
            editorPanel = new NavEditorPanel(owner, address);
            
        } else {
            parentEditorPanel = null;
            editorPanel = new NavEditorPanel(owner, address);
        }
        
        if (alreadyEditing) {
            resetToolBarForEditing();
            navScrollPane.setViewportView(editorPanel);
            editorPanel.setInitialFocus();
        } else {
            startEditing();
        }
    }

    /**
     * Perform a simple case-insensitive substring search on all nodes in the navigation tree.
     * @param searchString the string to search for
     * @return a List of NavAddresses for the nodes that matched the search string
     */
    public List<NavAddress> searchMetadata(String searchString) {

        // indicate in the search field that we are searching
        String text = searchField.getText();
        Font font = searchField.getFont();
        searchField.setCaretColor(Color.LIGHT_GRAY);
        searchField.setBackground(Color.LIGHT_GRAY);
        searchField.setFont(font.deriveFont(Font.ITALIC));
        searchField.setText(NavigatorMessages.getString("NAV_SearchingFor_Message", searchString));
        searchField.paintImmediately(searchField.getBounds());
        
        // perform the actual search
        List<NavAddress> results = new ArrayList<NavAddress>();
        NavTreeModel navModel = navTree.getModel();
        Enumeration<NavTreeNode> nodes = UnsafeCast.unsafeCast(navModel.getRoot().breadthFirstEnumeration());   // ~ unsafe
        
        Pattern searchPattern = Pattern.compile(WildcardPatternMatcher.wildcardPatternToRegExp(searchString), Pattern.CASE_INSENSITIVE);
        
        while (nodes.hasMoreElements()) {
            
            NavTreeNode node = nodes.nextElement();
            String nodeName = node.toString();
            
            // we use find() instead of matches() on the Matcher since we want to simply find a matching subsequence
            // and not whether the *whole* string matches the pattern.
            if (searchPattern.matcher(nodeName).find()) {
                results.add(node.getAddress());
            }
        }
        
        // reset the search field
        searchField.setFont(font);
        searchField.setText(text);
        searchField.setCaretColor(Color.BLACK);
        searchField.setBackground(Color.WHITE);        
        
        return results;        
    }
    
    /**
     * Tests metadata examples (either just those with evaluatExample set, or all
     * of them) and prints the results to stdout.
     * @param surpressSuccesses If true, does not output results of successfully run examples
     * @param skipNonAutoEvaluableExamples If true, runs only those examples with evaluateExample set to true  
     */
    public void testMetadataExamples(boolean surpressSuccesses, boolean skipNonAutoEvaluableExamples) {
        
        StringBuilder buffer = new StringBuilder();
        NavTreeModel navModel = navTree.getModel();
        Enumeration<NavTreeNode> nodes = UnsafeCast.unsafeCast(navModel.getRoot().breadthFirstEnumeration());   // ~ unsafe

        int agentsPassed = 0;
        int agentsFailed = 0;
        int examplesPassed = 0;
        int examplesFailed = 0;
        
        buffer.append("\n-- Testing Metadata Examples --\n\n");        
        System.out.println(buffer); // output the buffer and flush its contents        
        buffer.setLength(0);
        
        if (surpressSuccesses) {
            owner.getValueRunner().setShowConsoleInfo(false);            
        }
        
        while (nodes.hasMoreElements()) {
            
            NavTreeNode node = nodes.nextElement();

            if (node instanceof NavEntityNode) {
                
                NavAddress address = node.getAddress();
                CALFeatureMetadata metadata = NavAddressHelper.getMetadata(owner, address);
                
                if (metadata instanceof FunctionalAgentMetadata) {

                    boolean failed = false;
                    FunctionalAgentMetadata faMetadata = (FunctionalAgentMetadata) metadata;
                    CALExample[] examples = faMetadata.getExamples();
                    
                    for (int i = 0; i < examples.length; i++) {

                        if (skipNonAutoEvaluableExamples && !examples[i].evaluateExample()) {
                            continue;
                        }
                        
                        StringBuilder result = new StringBuilder();
                        boolean success = EditorHelper.evaluateExpression(owner, examples[i].getExpression(), result);
                        
                        if (!success) {

                            if (!failed) {
                                QualifiedName name = faMetadata.getFeatureName().toQualifiedName();
                                buffer.append (name.getQualifiedName() + " ...\n");
                                failed = true;
                            }
                            
                            examplesFailed++;
                            
                            buffer.append("\n");
                            buffer.append("   EXAMPLE FAILED\n");
                            buffer.append("   Description:    " + examples[i].getDescription() + "\n");
                            buffer.append("   Module Context: " + examples[i].getExpression().getModuleContext() + "\n");
                            buffer.append("   Expression:     " + examples[i].getExpression().getExpressionText() + "\n");
                            buffer.append("   Result:         " + result.toString() + "\n");
                        
                        } else {
                            examplesPassed++;
                        }
                    }
                    
                    if (!failed) {
                        agentsPassed++;
                    } else {
                        buffer.append("\n");
                        agentsFailed++;
                    } 
                }
            }
        }
        
        if (surpressSuccesses) {
            owner.getValueRunner().setShowConsoleInfo(true);            
        }
        
        if (examplesFailed == 0) {
            buffer.append("No examples failed.\n");
        }
        
        buffer.append("\n");
        buffer.append("-- Testing Summary --\n\n");
        buffer.append("Examples Tested: " + (examplesPassed + examplesFailed) + "\n");
        buffer.append("Passed: " + examplesPassed + " -- Failed: " + examplesFailed + "\n");
        buffer.append("\n");
        buffer.append("Functional Agents Tested: " + (agentsPassed + agentsFailed) + "\n");
        buffer.append("Passed: " + agentsPassed + " -- Failed: " + agentsFailed + "\n");
        
        System.out.println(buffer);        
    }
        
    /**
     * @return true if the navigator is currently editing metadata
     */
    public boolean isEditing() {
        return editorPanel != null;
    }
    
    /**
     * Saves changes made in the editor panel if there were any changes.
     */
    private void saveChanges() {
        
        if (isEditing() && editorPanel.hasChanged()) {

            if (!editorPanel.checkValues()) {
                JOptionPane.showMessageDialog(this, NavigatorMessages.getString("NAV_InvalidValues_Message"), NavigatorMessages.getString("NAV_InvalidValues_Header"), JOptionPane.ERROR_MESSAGE);
            
            } else if (!editorPanel.save()) {
                JOptionPane.showMessageDialog(this, NavigatorMessages.getString("NAV_ErrorSaving_Message"), NavigatorMessages.getString("NAV_ErrorSaving_Header"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Prompts the user to save changes if any changes have been made.
     * If no changes have been made, does nothing.
     */
    private void promptForSaveChanges() {
        if (isEditing() && editorPanel.hasChanged()) {
            String title = NavigatorMessages.getString("NAV_SavePrompt_Header");
            String message = NavigatorMessages.getString("NAV_SavePrompt_Message", NavAddressHelper.getDisplayText(owner, editorPanel.getAddress()));
            int response = JOptionPane.showOptionDialog(this,
                    message,
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, null, null); 
            if (response == JOptionPane.YES_OPTION) {
                saveChanges();
            }
        }
    }
    
    /**
     * Stops editing metadata and returns to viewing the metadata that was edited.
     * Does nothing if the navigator is not currently editing.
     * @param autoSave If true, automatically saves changes; if false, prompts the user.
     */
    public void stopEditing(boolean autoSave) {
        
        if (isEditing()) {
            
            if(autoSave) {
                saveChanges();
            } else {
                promptForSaveChanges();
            }
            
            editorPanel.stopEditing();
            
            toolBar.removeAll();
            toolBar.add(getBackButton());
            toolBar.add(getForwardButton());
            toolBar.addSeparator();
            toolBar.add(getShowTreeButton());
            toolBar.addSeparator();
            toolBar.add(getPrintButton());
            toolBar.add(getEditButton());
            toolBar.addSeparator();
            toolBar.add(getSearchPanel());
            toolBar.repaint();

            getEditButton().getModel().setRollover(false);
            
            NavAddress address = editorPanel.getAddress();
            
            editorPanel = null;
            parentEditorPanel = null;
            displayMetadata(address);
            navScrollPane.setViewportView(viewerPane);
            navScrollPane.requestFocusInWindow();
        }
    }
    
    /**
     * Start the editing process by setting the viewport to the editor panel and
     * updating the other UI components.
     */
    private void startEditing() {

        navScrollPane.setViewportView(editorPanel);
        resetToolBarForEditing();

        if (!isVisible()) {
            setVisible(true);
        }

        // make sure the editor panel ends up with the focus
        editorPanel.setInitialFocus();
    }

    /**
     * Resets the toolbar to display the correct buttons for whatever
     * type of metadata is being edited.
     */    
    private void resetToolBarForEditing() {

        toolBar.removeAll();

        // We can't permanently remove this button from the toolbar. If the user uses the 
        // Alt.<- key combination to activate the button and it is removed from the toolbar
        // then the Swing event dispatch thread will throw an exception trying to determine the
        // parent of the button. So just make the button invisible if it is not needed.
        getEditParentButton().setVisible(false);
        toolBar.add(getEditParentButton());

        if (editorPanel.getAddress().getParameter(NavAddress.ARGUMENT_PARAMETER) != null) {
            getEditParentButton().setVisible(true);
            toolBar.addSeparator();
        }
        
        toolBar.add(getShowTreeButton());
        toolBar.addSeparator();
        toolBar.add(getCopyButton());
        toolBar.add(getPasteButton());
        toolBar.addSeparator();
        toolBar.add(getCloseButton());
        toolBar.add(getSaveButton());
        toolBar.repaint();
        
        getCloseButton().getModel().setRollover(false);
        getEditParentButton().getModel().setRollover(false);
    }

    /**
     * @return a new toolbar to display at the top of the navigation tree
     */
    private JToolBar getNavTreeToolBar() {
       
        if (navTreeToolBar == null) {
            
            navTreeToolBar = new JToolBar();
            navTreeToolBar.setRollover(true);
            navTreeToolBar.setFloatable(false);
            
            navTreeToolBar.add(getFlatAbbreviatedDisplayModeButton());
            navTreeToolBar.add(getFlatFullyQualifiedDisplayModeButton());
            navTreeToolBar.add(getHierarchicalDisplayModeButton());
            
            navTreeDisplayModeButtonGroup = new ButtonGroup();
            navTreeDisplayModeButtonGroup.add(getFlatAbbreviatedDisplayModeButton());
            navTreeDisplayModeButtonGroup.add(getFlatFullyQualifiedDisplayModeButton());
            navTreeDisplayModeButtonGroup.add(getHierarchicalDisplayModeButton());
        }
        
        return navTreeToolBar;
    }
    
    /**
     * @return the flat abbreviated display mode button for the nav tree toolbar.
     */    
    private JToggleButton getFlatAbbreviatedDisplayModeButton() {
        
        if (flatAbbreviatedDisplayModeButton == null) {
            
            Action flatAbbreviatedDisplayModeAction = new AbstractAction(NavigatorMessages.getString("NAV_FlatAbbreviatedDisplayModeButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/nav_flatAbbrev.gif"))) {
    
                private static final long serialVersionUID = 5455343245437448702L;

                public void actionPerformed(ActionEvent e) {
                    setNavTreeDisplayMode(TreeViewDisplayMode.FLAT_ABBREVIATED);            
                }
            };

            flatAbbreviatedDisplayModeAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_FlatAbbreviatedDisplayModeButtonToolTip"));
            flatAbbreviatedDisplayModeAction.putValue(Action.MNEMONIC_KEY, FLAT_ABBREVIATED_DISPLAY_MODE_MNEMONIC);
            
            flatAbbreviatedDisplayModeButton = new JToggleButton(flatAbbreviatedDisplayModeAction);
            flatAbbreviatedDisplayModeButton.setText("");
        }
        
        return flatAbbreviatedDisplayModeButton;
    }

    /**
     * @return the flat abbreviated display mode button for the nav tree toolbar.
     */    
    private JToggleButton getFlatFullyQualifiedDisplayModeButton() {
        
        if (flatFullyQualifiedDisplayModeButton == null) {
            
            Action flatFullyQualifiedDisplayModeAction = new AbstractAction(NavigatorMessages.getString("NAV_FlatFullyQualifiedDisplayModeButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/nav_flat.gif"))) {
    
                private static final long serialVersionUID = -8596519172745276785L;

                public void actionPerformed(ActionEvent e) {
                    setNavTreeDisplayMode(TreeViewDisplayMode.FLAT_FULLY_QUALIFIED);            
                }
            };

            flatFullyQualifiedDisplayModeAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_FlatFullyQualifiedDisplayModeButtonToolTip"));
            flatFullyQualifiedDisplayModeAction.putValue(Action.MNEMONIC_KEY, FLAT_FULLY_QUALIFIED_DISPLAY_MODE_MNEMONIC);
            
            flatFullyQualifiedDisplayModeButton = new JToggleButton(flatFullyQualifiedDisplayModeAction);
            flatFullyQualifiedDisplayModeButton.setText("");
        }
        
        return flatFullyQualifiedDisplayModeButton;
    }

    /**
     * @return the hierarchical display mode button for the nav tree toolbar.
     */    
    private JToggleButton getHierarchicalDisplayModeButton() {
        
        if (hierarchicalDisplayModeButton == null) {
            
            Action hierarchicalDisplayModeAction = new AbstractAction(NavigatorMessages.getString("NAV_HierarchicalDisplayModeButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/nav_hierarchical.gif"))) {
    
                private static final long serialVersionUID = -7344771354219236965L;

                public void actionPerformed(ActionEvent e) {
                    setNavTreeDisplayMode(TreeViewDisplayMode.HIERARCHICAL);            
                }
            };

            hierarchicalDisplayModeAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_HierarchicalDisplayModeButtonToolTip"));
            hierarchicalDisplayModeAction.putValue(Action.MNEMONIC_KEY, HIERARCHICAL_DISPLAY_MODE_MNEMONIC);
            
            hierarchicalDisplayModeButton = new JToggleButton(hierarchicalDisplayModeAction);
            hierarchicalDisplayModeButton.setText("");
        }
        
        return hierarchicalDisplayModeButton;
    }

    /**
     * @return a new toolbar to display at the top of the HTML viewer
     */
    private JToolBar getToolBar() {
       
        if (toolBar == null) {
            
            toolBar = new JToolBar();
            toolBar.setRollover(true);
            toolBar.setFloatable(false);
            
            toolBar.add(getBackButton());
            toolBar.add(getForwardButton());
            toolBar.addSeparator();
            toolBar.add(getShowTreeButton());
            toolBar.addSeparator();
            toolBar.add(getPrintButton());
            toolBar.add(getEditButton());
            
            toolBar.addSeparator();
            toolBar.add(getSearchPanel());
        }
        
        return toolBar;
    }
    
    /**
     * @return the panel that contains the search text field and related components
     */
    private JPanel getSearchPanel() {
        
        if (searchPanel == null) {
            
            searchPanel = new JPanel();
            searchField = new JTextField();
            
            searchField.setToolTipText(NavigatorMessages.getString("NAV_SearchLabelToolTip"));
            
            searchField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && searchField.getText().trim().length() != 0) {
                        viewerPane.displayMetadata(NavAddress.getSearchAddress(searchField.getText()));
                    }
                }
            });
            
            Box vbox = Box.createVerticalBox();
            vbox.add(Box.createVerticalStrut(2));
            vbox.add(searchField);
            vbox.add(Box.createVerticalStrut(2));
                       
            searchPanel.setLayout(new BorderLayout());
            searchPanel.add(new JLabel(NavigatorMessages.getString("NAV_SearchLabel")), BorderLayout.WEST);
            searchPanel.add(vbox);
        }
        
        return searchPanel;
    }

    /**
     * @return the back button for the navigation toolbar
     */    
    private JButton getBackButton() {
        
        if (backButton == null) {
            
            Action backAction = new AbstractAction(NavigatorMessages.getString("NAV_BackButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/leftArrow.gif"))) {
    
                private static final long serialVersionUID = 1072389882311854589L;

                public void actionPerformed(ActionEvent e) {
                    stopEditing(true);
                    viewerPane.goBack();
                }
            };

            backAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_BackButtonToolTip"));
            backAction.putValue(Action.MNEMONIC_KEY, GO_BACK_MNEMONIC);
            
            backButton = new JButton(backAction);
        }
        
        return backButton;
    }

    /**
     * @return the back button for the navigation toolbar
     */    
    private JButton getForwardButton() {
        
        if (forwardButton == null) {
            
            Action forwardAction = new AbstractAction(NavigatorMessages.getString("NAV_ForwardButton"),
                                                      new ImageIcon(GemCutter.class.getResource("/Resources/rightArrow.gif"))) {
    
                private static final long serialVersionUID = 4429725727823775928L;

                public void actionPerformed(ActionEvent e) {
                    stopEditing(true);
                    viewerPane.goForward();
                }
            };

            forwardAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_ForwardButtonToolTip"));
            forwardAction.putValue(Action.MNEMONIC_KEY, GO_FORWARD_MNEMONIC);
                        
            forwardButton = new JButton(forwardAction);
        }
        
        return forwardButton;
    }

    /**
     * @return the print button for the navigation toolbar
     */    
    private JButton getPrintButton() {
        
        if (printButton == null) {
            
            Action printAction = new AbstractAction(NavigatorMessages.getString("NAV_PrintButton"),
                                                    new ImageIcon(GemCutter.class.getResource("/Resources/print.gif"))) {
    
                private static final long serialVersionUID = 5666754017212100780L;

                public void actionPerformed(ActionEvent e) {
                    
                    PrinterJob printerJob = PrinterJob.getPrinterJob();
            
                    if (printerJob.printDialog()) {

                        try {
                            printerJob.setPrintable(viewerPane);
                            printerJob.print();
                    
                        } catch (PrinterException ex) {
                            JOptionPane.showMessageDialog(NavFrame.this, ex.getLocalizedMessage(),
                                                          NavigatorMessages.getString("NAV_PrintError_Header"),
                                                          JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            };
            
            printAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_PrintButtonToolTip"));
            printAction.putValue(Action.MNEMONIC_KEY, PRINT_MNEMONIC);
            
            printButton = new JButton(printAction);
        }
        
        return printButton;
    }

    /**
     * @return the edit button for the navigation toolbar
     */    
    private JButton getEditButton() {
        
        if (editButton == null) {

            Action editAction = new AbstractAction(NavigatorMessages.getString("NAV_EditButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/nav_edit.gif"))) {
    
                private static final long serialVersionUID = -1584426840280415158L;

                public void actionPerformed(ActionEvent e) {
                    owner.editMetadata(viewerPane.getCurrentAddress());
                }
            };
            
            editAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_EditButtonToolTip"));
            editAction.putValue(Action.MNEMONIC_KEY, EDIT_MNEMONIC);
            
            editButton = new JButton(editAction);
        }
        
        return editButton;
    }
    
    /**
     * @return the button to return to editing the parent metadata
     */
    private JButton getEditParentButton() {
        
        if (editParentButton == null) {
            
            Action editParentAction = new AbstractAction(NavigatorMessages.getString("NAV_EditParentButton"),
                                                         new ImageIcon(GemCutter.class.getResource("/Resources/leftArrow.gif"))) {
    
                private static final long serialVersionUID = -259012751726184443L;

                public void actionPerformed(ActionEvent e) {
                    owner.editMetadata(editorPanel.getAddress().withAllStripped());
                }
            };
            
            editParentAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_EditParentButtonToolTip"));
            editParentAction.putValue(Action.MNEMONIC_KEY, EDIT_PARENT_MNEMONIC);
            
            editParentButton = new JButton(editParentAction);
        }
        
        return editParentButton;
    }

    /**
     * @return the save button for the navigation toolbar
     */
    private JButton getSaveButton() {
        
        if (saveButton == null) {
            
            Action saveAction = new AbstractAction(NavigatorMessages.getString("NAV_SaveButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/save.gif"))) {
    
                private static final long serialVersionUID = -6248180609589398157L;

                public void actionPerformed(ActionEvent e) {
                    saveChanges();
                }
            };
    
            saveAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_SaveButtonToolTip"));
            saveAction.putValue(Action.MNEMONIC_KEY, SAVE_MNEMONIC);
            
            saveButton = new JButton(saveAction);
        }
        
        return saveButton;
    }
    
    /**
     * @return the stop editing button for the navigation toolbar
     */
    private JButton getCloseButton() {
        
        if (closeButton == null) {
            
            Action closeAction = new AbstractAction(NavigatorMessages.getString("NAV_CloseButton"),
                                                    new ImageIcon(GemCutter.class.getResource("/Resources/checkmark.gif"))) {
                                                        
                private static final long serialVersionUID = -6951821908195739490L;

                public void actionPerformed(ActionEvent e) {
                    stopEditing(true);
                }
            };
    
            closeAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_CloseButtonToolTip"));
            closeAction.putValue(Action.MNEMONIC_KEY, CLOSE_MNEMONIC);
            
            closeButton = new JButton(closeAction);
        }
        
        return closeButton;
    }
    
    /**
     * @return the toggle button for showing/hiding the navigation tree
     */
    private JToggleButton getShowTreeButton() {
        
        if (showTreeButton == null) {
            
            Action showAction = new AbstractAction(NavigatorMessages.getString("NAV_NavigationPanelButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/catByModule.gif"))) {
                                                       
                private static final long serialVersionUID = -6780509043470798286L;

                public void actionPerformed(ActionEvent e) {
                    setSidePanelVisible(showTreeButton.isSelected());
                }
            };
            
            showAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_NavigationPanelButtonToolTip"));
            showAction.putValue(Action.MNEMONIC_KEY, TOGGLE_PANEL_MNEMONIC);
            
            showTreeButton = new JToggleButton(showAction);
        }
        
        return showTreeButton;
    }

    /**
     * @return the copy metadata button for copying metadata
     */
    private JButton getCopyButton() {
        
        if (copyButton == null) {
            
            Action copyAction = new AbstractAction(NavigatorMessages.getString("NAV_CopyButton"),
                                                   new ImageIcon(GemCutter.class.getResource("/Resources/copy.gif"))) {
                                                       
                private static final long serialVersionUID = 4519220031949265137L;

                public void actionPerformed(ActionEvent e) {
                    if (isEditing()) {
                        copiedMetadata = editorPanel.getEditedMetadata();
                        getPasteButton().setEnabled(true);
                    }
                }
            };
            
            copyAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_CopyButtonToolTip"));
            copyAction.setEnabled(true);
            
            copyButton = new JButton(copyAction);
        }
        
        return copyButton;
    }
    
    /**
     * @return the paste metadata button for pasting metadata
     */
    private JButton getPasteButton() {
        
        if (pasteButton == null) {
            
            Action pasteAction = new AbstractAction(NavigatorMessages.getString("NAV_ReplaceButton"),
                                                    new ImageIcon(GemCutter.class.getResource("/Resources/paste.gif"))) {
                                                       
                private static final long serialVersionUID = 1192026908334065285L;

                public void actionPerformed(ActionEvent e) {
                    if (isEditing() && copiedMetadata != null) {
                        editorPanel.setEditedMetadata(copiedMetadata);
                    }
                }
            };
            
            pasteAction.putValue(Action.SHORT_DESCRIPTION, NavigatorMessages.getString("NAV_ReplaceButtonToolTip"));
            pasteAction.setEnabled(false);
            
            pasteButton = new JButton(pasteAction);
        }
        
        return pasteButton;
    }
    
    /**
     * @return a new copy menu item for the navigation tree popup menu
     */
    private JMenuItem getCopyMenuItem() {

        Action copyAction = new AbstractAction(NavigatorMessages.getString("NAV_CopyMenuItem"),
                                                new ImageIcon(GemCutter.class.getResource("/Resources/copy.gif"))) {
            
            private static final long serialVersionUID = 6561547024209053533L;

            public void actionPerformed(ActionEvent e) {
                NavTreeNode selectedNode = (NavTreeNode) navTree.getSelectionPath().getLastPathComponent();
                NavAddress address = selectedNode.getAddress();
                copiedMetadata = NavAddressHelper.getMetadata(owner, address);
                getPasteButton().setEnabled(true);
            }
        };
        
        TreePath selectionPath = navTree.getSelectionPath();
        NavTreeNode selectedNode = selectionPath != null ? (NavTreeNode) selectionPath.getLastPathComponent() : null;
        NavAddress address = selectedNode != null ? selectedNode.getAddress() : null;

        copyAction.setEnabled(selectedNode != null && NavAddressHelper.getMetadata(owner, address) != null);
        
        return UIUtilities.makeNewMenuItem(copyAction);
    }
    
    /**
     * @return the pane that displays the company logo at the top of the window
     */
    private JEditorPane getHeaderPane() {
        
        // Form the header HTML
        StringBuilder header = new StringBuilder();
        String headerBackground = GemCutter.class.getResource("/Resources/bobj_header_background.png").toString();
        String headerImage = GemCutter.class.getResource("/Resources/bobj_header.png").toString();
        String altBackground = GemCutter.class.getResource("/Resources/bobj_header_background_alt.jpg").toString();

        header.append("<html><body bgcolor='#FFFFFF'>");
        header.append("<table border='0' bgcolor='#FFFFFF' width='100%' cellpadding='0' cellspacing='0'><tr>");
        header.append("<td align='left' valign='top' background='" + headerBackground + "'>&nbsp;&nbsp;<font size='5'><b><i>");
        header.append(NavigatorMessages.getString("NAV_PropertiesBrowser_Header"));
        header.append("</i></b></font></td>");
        header.append("<td align='right' width='279' background='" + altBackground + "'><img src='" + headerImage + "' border='0'></td>");
        header.append("</tr></table>");
        header.append("</body></html>");

        // Create the editor pane                
        JEditorPane headerPane = new JEditorPane();
        HTMLEditorKit editorKit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();

        headerPane.setEditorKit(editorKit);
        headerPane.setDocument(doc);
        headerPane.setEditable(false);
        headerPane.setText(header.toString());
        headerPane.setBorder(null);
    
        // The exact minimum width/height needed to display the header.
        headerPane.setMinimumSize(new Dimension(430, 42));
        headerPane.setPreferredSize(new Dimension(430, 42));
    
        return headerPane; 
    }
    
    /**
     * Sets the display mode for the navigation tree, refreshes its display, and saves
     * out the preference.
     * @param navTreeDisplayMode the display mode.
     */
    private void setNavTreeDisplayMode(final TreeViewDisplayMode navTreeDisplayMode) {
        
        NavTreeModel model = navTree.getModel();
        model.load(owner.getPerspective(), navTreeDisplayMode);
        navTree.collapseToModules();
        
        NavTreeNode node = model.getNodeForAddress(viewerPane.getCurrentAddress());
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            navTree.setSelectionPath(path);
            navTree.scrollPathToVisible(path);
        }
        
        // save the preference
        getPreferences().put(NAV_TREE_DISPLAY_MODE, navTreeDisplayMode.getName());
        
        // set the button states
        setNavTreeDisplayModeButtonStates(navTreeDisplayMode);
    }

    /**
     * Sets the states of the buttons in the nav tree toolbar.
     * @param navTreeDisplayMode the display mode.
     */
    private void setNavTreeDisplayModeButtonStates(final TreeViewDisplayMode navTreeDisplayMode) {
        
        if (navTreeDisplayMode == TreeViewDisplayMode.FLAT_ABBREVIATED) {
            getFlatAbbreviatedDisplayModeButton().setSelected(true);
            
        } else if (navTreeDisplayMode == TreeViewDisplayMode.FLAT_FULLY_QUALIFIED) {
            getFlatFullyQualifiedDisplayModeButton().setSelected(true);
            
        } else if (navTreeDisplayMode == TreeViewDisplayMode.HIERARCHICAL) {
            getHierarchicalDisplayModeButton().setSelected(true);
            
        } else {
            throw new IllegalArgumentException("Unrecognized TreeViewDisplayMode: " + navTreeDisplayMode);
        }
    }
    
    /**
     * @return the display mode of the navigation tree.
     */
    private TreeViewDisplayMode getNavTreeDisplayMode() {
        if (getFlatAbbreviatedDisplayModeButton().isSelected()) {
            return TreeViewDisplayMode.FLAT_ABBREVIATED;
            
        } else if (getFlatFullyQualifiedDisplayModeButton().isSelected()) {
            return TreeViewDisplayMode.FLAT_FULLY_QUALIFIED;
            
        } else if (getHierarchicalDisplayModeButton().isSelected()) {
            return TreeViewDisplayMode.HIERARCHICAL;
            
        } else {
            // this is the default mode
            return TreeViewDisplayMode.FLAT_ABBREVIATED;
        }
    }
}