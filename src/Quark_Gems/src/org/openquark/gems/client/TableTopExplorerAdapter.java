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
 * TableTopExplorerAdapter.java
 * Creation date: Jan 16th 2003
 * By: Ken Wong
 */
package org.openquark.gems.client;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.UndoableEditSupport;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.DisplayedGem.DisplayedPartConnectable;
import org.openquark.gems.client.DisplayedGem.DisplayedPartInput;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.explorer.CALNameEditor;
import org.openquark.gems.client.explorer.ExplorerGemNameEditor;
import org.openquark.gems.client.explorer.ExplorerTree;
import org.openquark.gems.client.explorer.TableTopExplorer;
import org.openquark.gems.client.explorer.TableTopExplorerOwner;
import org.openquark.gems.client.utilities.ExtendedUndoableEditSupport;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;
import org.openquark.gems.client.valueentry.MetadataRunner;
import org.openquark.gems.client.valueentry.ValueEditorContext;
import org.openquark.gems.client.valueentry.ValueEditorManager;


/**
 * This class is the 'owner' of the TableTop Explorer used in the GemCutter. However, generally speaking,
 * this class just piggybacks off of the tabletop for its functionality.
 * @author Ken Wong
 */
class TableTopExplorerAdapter implements TableTopExplorerOwner, DisplayedGemStateListener {

    /**
     * A tree cell editor component for editing gem names.
     * @author Frank Worsley
     */
    private class CodeAndCollectorNameEditor extends CALNameEditor {
        private static final long serialVersionUID = -1115390462294213694L;

        private CodeAndCollectorNameEditor(Gem gem) {
            super(gem);
        }

        /**
         * Returns whether a name is a valid name for this field
         * @param name String the name to check for validity 
         */
        @Override
        protected boolean isValidName(String name) {
            if (!(super.isValidName(name))) {
                return false;
            }
            return isAvailableCodeOrCollectorName(name, getGem());
        }

        @Override
        protected void renameGem(String newName, String oldName, boolean commit) {
            Gem gem = getGem();
            if (gem instanceof CodeGem) {
                renameCodeGem((CodeGem) gem, newName, oldName, false);
            } else if (gem instanceof CollectorGem) {
                renameCollectorGem((CollectorGem) gem, newName, oldName, false);
            }
        }

        @Override
        protected FontRenderContext getFontRenderContext() {
            return ((Graphics2D)tableTopExplorer.getGraphics()).getFontRenderContext();
        }
    }

    /**
     * A listener that adds support for manual burning and editing value gems. This
     * is done by double-clicking on a gem or an input. This listener will also
     * stop intellicut or possibly add a gem if the mouse is pressed.
     */
    private class ExplorerMouseClickListener extends MouseClickDragAdapter {

        @Override
        public boolean mouseReallyClicked(MouseEvent e) {

            boolean doubleClicked = super.mouseReallyClicked(e);

            if (doubleClicked) {

                Object userObject = tableTopExplorer.getExplorerTree().getUserObjectAt(e.getPoint());

                if (userObject instanceof Gem.PartInput) {
                    doBurnInputAction((Gem.PartInput)userObject);
                } else if (userObject instanceof ValueGem) {
                    if (userObject instanceof ValueGem) {
                        tableTopExplorer.editValueGem((ValueGem)userObject);                            
                    }
                }
            }

            e.consume();

            return doubleClicked;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            gemCutter.getIntellicutManager().stopIntellicut();
            maybeAddGem();
        }        
    }

    /**
     * A listener for updating the drag icon as the user moves the mouse over the explorer tree.
     * @author Frank Worsley
     */
    private class ExplorerMouseMotionListener extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {

            if (gemCutter.getGUIState() == GemCutter.GUIState.ADD_GEM) {

                Object userObject = tableTopExplorer.getExplorerTree().getUserObjectAt(e.getPoint(), (JComponent) e.getSource());
                DisplayedGem addingDisplayedGem = gemCutter.getAddingDisplayedGem();
                Gem addingGem = addingDisplayedGem == null ? null : addingDisplayedGem.getGem();

                if (userObject instanceof Gem.PartInput) {

                    AutoburnLogic.AutoburnUnifyStatus burnStatus = null;

                    if (addingDisplayedGem != null) {                    
                        burnStatus = canConnect(addingGem.getOutputPart(), (Gem.PartInput) userObject);
                    }

                    if (addingDisplayedGem == null || burnStatus.isAutoConnectable()) {
                        gemCutter.getGlassPane().setCursor(GemCutter.getCursorForAddGem(addingGem));
                    } else {
                        gemCutter.getGlassPane().setCursor(DragSource.DefaultLinkNoDrop);
                    }

                    tableTopExplorer.selectInput((Gem.PartInput) userObject);

                } else {
                    tableTopExplorer.selectRoot();
                    gemCutter.getGlassPane().setCursor(GemCutter.getCursorForAddGem(addingGem));
                }
            }
        }
    }

    /** Our reference to the gemCutter */
    private final GemCutter gemCutter;

    /** The tableTopExplorer instance that this adapter owns */
    private TableTopExplorer tableTopExplorer = null;

    /**
     * Default constructor for the TableTopExplorer Adapter
     * @param gemCutter
     */
    TableTopExplorerAdapter(GemCutter gemCutter) {

        if (gemCutter == null) {
            throw new NullPointerException();
        }

        this.gemCutter = gemCutter;
    }

    /**
     * Returns the tableTopExplorer (create one if one doesn't exist)
     * @return TableTopExplorer
     */
    TableTopExplorer getTableTopExplorer() {

        if (tableTopExplorer == null) {

            tableTopExplorer = new TableTopExplorer(this, GemCutter.getResourceString("TTX_Root_Node"));

            tableTopExplorer.getExplorerTree().addMouseListener(new ExplorerMouseClickListener());
            tableTopExplorer.getExplorerTree().addMouseMotionListener(new ExplorerMouseMotionListener());

            // Although technically the explorer tree can support multiple selection, in the GemCutter
            // we only support single selection. This is because the TableTop has no concept of selected
            // inputs and enabling multiple selection screws up Explorer-TableTop synchronization.
            tableTopExplorer.getExplorerTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

            gemCutter.getTableTop().addGemGraphChangeListener(tableTopExplorer);
        }

        return tableTopExplorer;
    }

    /**
     * @see TableTopExplorerOwner#getPopupMenu(Gem[])
     */
    public JPopupMenu getPopupMenu(Gem[] gems) {

        if (gemCutter.getGUIState() == GemCutter.GUIState.RUN) {
            return gemCutter.getTableTopPanel().getRunModePopupMenu();

        } else if (gemCutter.getGUIState() != GemCutter.GUIState.EDIT) {
            return null;
        }

        if (gems.length == 1) {

            // Display the same menu as the table top, if there is only one gem.
            DisplayedGem dGem = gemCutter.getTableTop().getDisplayedGem(gems[0]);

            return gemCutter.getTableTopPanel().getGemPopupMenu(dGem, false);
        }

        throw new UnsupportedOperationException();
    }

    /**
     * @see TableTopExplorerOwner#getPopupMenu(Gem.PartInput[])
     */
    public JPopupMenu getPopupMenu(Gem.PartInput[] inputs) {

        if (gemCutter.getGUIState() == GemCutter.GUIState.RUN) {
            return gemCutter.getTableTopPanel().getRunModePopupMenu();

        } else if (gemCutter.getGUIState() != GemCutter.GUIState.EDIT) {
            return null;
        }

        if (inputs.length == 1) {

            // Display the same menu as the table top, if there is only one input.
            DisplayedGem dGem = gemCutter.getTableTop().getDisplayedGem(inputs[0].getGem());
            DisplayedPartInput dInput = dGem.getDisplayedInputPart(inputs[0].getInputNum());

            JPopupMenu popupMenu = gemCutter.getTableTopPanel().getGemPartPopupMenu(dInput, false);

            // Add our Intellicut menu item first.
            popupMenu.add(GemCutter.makeNewMenuItem(getIntellicutAction(inputs[0])), 0);
            popupMenu.add(new JSeparator(SwingConstants.HORIZONTAL), 1);

            return popupMenu;
        }

        throw new UnsupportedOperationException();
    }

    /**
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#getPopupMenu()
     */
    public JPopupMenu getPopupMenu() { 

        if (gemCutter.getGUIState() == GemCutter.GUIState.RUN) {
            return gemCutter.getTableTopPanel().getRunModePopupMenu();

        } else if (gemCutter.getGUIState() != GemCutter.GUIState.EDIT){
            return null;
        }

        JPopupMenu popupMenu = gemCutter.getTableTopPanel().getNonGemPopupMenu(false);

        // Add our own "Add Gem" menu item first.
        popupMenu.add(GemCutter.makeNewMenuItem(getAddGemAction()), 0);

        return popupMenu;
    }

    /**
     * @see TableTopExplorerOwner#selectGem(Gem, boolean)
     */
    public void selectGem(Gem gem, boolean isSingleton) {

        TableTop tableTop = gemCutter.getTableTop();

        if (gem == null) {
            tableTop.selectGem(null, true);
            return;
        }

        DisplayedGem displayedGem = tableTop.getDisplayedGem(gem);

        // we must do this check because sometimes the explorer and the tabletop get
        // out of sync. And so the gem in the explorer might not be on the tabletop.
        if (displayedGem != null) {

            if (!tableTop.isSelected(displayedGem)) {
                tableTop.selectGem(gem, isSingleton);
            }

            tableTop.setFocusedDisplayedGem(displayedGem);

            gemCutter.getTableTopPanel().scrollRectToVisible(displayedGem.getBounds());
        }
    }

    /**
     * @see TableTopExplorerOwner#connect(Gem.PartOutput, Gem.PartInput)
     */
    public void connect(Gem.PartOutput source, Gem.PartInput destination) {

        AutoburnLogic.AutoburnUnifyStatus burnStatus = canConnect (source, destination);

        // If we can burn unambiguously then do it
        if (burnStatus.isUnambiguous()) {
            gemCutter.getTableTop().getBurnManager().handleAutoburnGemGesture(source.getGem(), destination.getType(),true);
        } 

        TableTop tableTop = gemCutter.getTableTop();
        // for connection, we want to move the connected gem to an appropriate place on the tabletop too.

        Gem sourceGem = source.getGem();
        Gem destinationGem = destination.getGem();

        if (!tableTop.getGemGraph().getGems().contains(sourceGem)) {
            tableTop.doAddGemUserAction(tableTop.createDisplayedGem(sourceGem, new Point()));
        }

        Connection connection = tableTop.handleConnectGemPartsGesture(source, destination);
        if (connection == null) {
            return;
        }

        // so we cheat a bit =)
        // we create a layout arranger object that allows us to do a tidy operation on only the source and destination
        DisplayedGem dest = tableTop.getDisplayedGem(destinationGem);

        Gem.PartInput[] inputs = destinationGem.getInputParts();

        Map<DisplayedGem, Point> connectedGemsToLocation = new HashMap<DisplayedGem, Point>();

        for (final PartInput input : inputs) {
            if (input.isConnected()) {
                DisplayedGem displayedGem = tableTop.getDisplayedGem(input.getConnection().getSource().getGem());
                connectedGemsToLocation.put(displayedGem, displayedGem.getLocation());
            }
        }

        DisplayedGem[] displayedGems = new DisplayedGem[connectedGemsToLocation.size() + 1];

        connectedGemsToLocation.keySet().toArray(displayedGems);

        displayedGems[connectedGemsToLocation.size()] = dest;

        Graph.LayoutArranger layoutArranger = new Graph.LayoutArranger(displayedGems);

        tableTop.doTidyUserAction(layoutArranger, dest); 

        for (int i = 0; i < displayedGems.length - 1; i++) {

            Set<Gem> subTree = GemGraph.obtainSubTree(displayedGems[i].getGem());
            subTree.remove(displayedGems[i].getGem());
            Point oldLocation = connectedGemsToLocation.get(displayedGems[i]);

            // Then we move the connected subtree of the source, so that it looks like the entire tree was moved.
            Point newLocation = displayedGems[i].getLocation();
            Point offset = new Point(newLocation.x - oldLocation.x, newLocation.y - oldLocation.y);

            for (final Gem gem : subTree) {
                DisplayedGem displayedGem = tableTop.getDisplayedGem(gem);
                Point oldSpot = displayedGem.getLocation();
                Point moveGemTo = new Point(oldSpot.x + offset.x, oldSpot.y + offset.y);

                tableTop.doChangeGemLocationUserAction(displayedGem, moveGemTo);
            }
        }
    }

    /**
     * Add a gem to the tabletop if appropriate. This should be called from mousePressed().
     */
    private void maybeAddGem() {

        if (gemCutter.getGUIState() == GemCutter.GUIState.ADD_GEM) {

            ExplorerTree explorerTree = tableTopExplorer.getExplorerTree();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();
            Object userObject = treeNode != null ? treeNode.getUserObject() : null;

            if (gemCutter.getAddingDisplayedGem() == null) {

                // This means we have to invoke Intellicut to add a gem.

                if (treeNode == null) {
                    treeNode = (DefaultMutableTreeNode) explorerTree.getModel().getRoot();
                    explorerTree.setSelectionPath(new TreePath(treeNode.getPath()));
                }

                Rectangle location = getIntellicutLocation(treeNode); 
                DisplayedPartConnectable displayedPart = null;

                if (userObject instanceof Gem.PartInput) {
                    displayedPart = gemCutter.getTableTop().getDisplayedPartConnectable((Gem.PartInput) userObject);
                }

                gemCutter.getIntellicutManager().startIntellicutMode(displayedPart, location, false, null, explorerTree);

                gemCutter.enterGUIState(GemCutter.GUIState.EDIT);

            } else {

                // Here we're adding a concrete gem.

                Gem.PartInput connectTo = null;

                if (userObject instanceof Gem.PartInput) {

                    tableTopExplorer.selectInput((Gem.PartInput)userObject);
                    AutoburnLogic.AutoburnUnifyStatus burnStatus = canConnect(gemCutter.getAddingDisplayedGem().getGem().getOutputPart(), (Gem.PartInput)userObject);

                    if (burnStatus.isAutoConnectable()) {
                        connectTo = (Gem.PartInput) userObject;
                    } else {
                        return;
                    }
                }

                beginUndoableEdit();

                DisplayedGem displayedGemToAdd = gemCutter.getAddingDisplayedGem();
                Gem gemToAdd = displayedGemToAdd.getGem();
                gemCutter.getTableTop().doAddGemUserAction(displayedGemToAdd);

                gemCutter.enterGUIState(GemCutter.GUIState.EDIT);

                if (gemToAdd instanceof CodeGem || gemToAdd instanceof CollectorGem) {
                    tableTopExplorer.renameGem(gemToAdd);
                }

                if (connectTo != null) {
                    connect(gemToAdd.getOutputPart(), connectTo);
                    setUndoableName(GemCutter.getResourceString("TTX_Undo_ConnectNew"));
                } else {
                    setUndoableName(GemCutter.getResourceString("TTX_Undo_AddNew"));
                }

                endUndoableEdit();
            }
        }
    }

    /**
     * @see TableTopExplorerOwner#disconnect(Connection)
     */
    public void disconnect (Connection connection) {
        TableTop tableTop = gemCutter.getTableTop();
        tableTop.handleDisconnectGesture(connection);
        tableTop.getBurnManager().doUnburnAutomaticallyBurnedInputsUserAction(connection.getSource().getGem());
    }

    /**
     * @see TableTopExplorerOwner#canConnect(Gem.PartOutput, Gem.PartInput)
     */
    public AutoburnLogic.AutoburnUnifyStatus canConnect(Gem.PartOutput source, Gem.PartInput destination) {

        if ((source == null) || (destination == null) || (source.getGem().isBroken()) || destination.isBurnt()) {
            return AutoburnLogic.AutoburnUnifyStatus.NOT_POSSIBLE;

        } else if (!GemGraph.arePartsConnectableIfDisconnected(source, destination)) {
            return AutoburnLogic.AutoburnUnifyStatus.NOT_POSSIBLE;

        } else if (source.getGem() instanceof ValueGem){

            if (GemGraph.isDefaultableValueGemSource(source, destination, gemCutter.getConnectionContext())) {
                return AutoburnLogic.AutoburnUnifyStatus.NOT_NECESSARY;

            } else if (GemGraph.isCompositionConnectionValidIfDisconnected(source, destination, gemCutter.getTypeCheckInfo())) {
                return AutoburnLogic.AutoburnUnifyStatus.NOT_NECESSARY;

            } else {
                return AutoburnLogic.AutoburnUnifyStatus.NOT_POSSIBLE;
            }

        } else {

            Connection connection = source.getConnection();
            if (connection != null) {
                source.bindConnection(null);
                destination.bindConnection(null);   
            }

            AutoburnLogic.AutoburnUnifyStatus autoburnStatus = AutoburnLogic.getAutoburnInfo(destination.getType(), 
                    source.getGem(), gemCutter.getTypeCheckInfo()).getAutoburnUnifyStatus();

            if (connection != null) {
                source.bindConnection(connection);
                connection.getDestination().bindConnection(connection);
            }

            return autoburnStatus;
        }
    }

    /**
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#canConnect(org.openquark.cal.services.GemEntity, org.openquark.gems.client.Gem.PartInput)
     */
    public AutoburnLogic.AutoburnUnifyStatus canConnect(GemEntity source, Gem.PartInput destination) {

        if (source == null) {
            return AutoburnLogic.AutoburnUnifyStatus.NOT_POSSIBLE;
        }

        if (destination == null || destination.isBurnt()) {
            return AutoburnLogic.AutoburnUnifyStatus.NOT_POSSIBLE;
        }

        return AutoburnLogic.getAutoburnInfo(destination.getType(), source, gemCutter.getTypeCheckInfo()).getAutoburnUnifyStatus();
    }

    /**
     * @see TableTopExplorerOwner#getDeleteGemAction(Gem)
     */
    public Action getDeleteGemAction(Gem gem) {
        return null;
    }

    /**
     * @see TableTopExplorerOwner#getRoots()
     */
    public Set<Gem> getRoots() { 
        TableTop tableTop = gemCutter.getTableTop();

        Set<Gem> roots = tableTop.getGemGraph().getRoots();
        return roots;
    }

    /** 
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#addGems(GemEntity[])
     */
    public Gem[] addGems(GemEntity[] gemEntities) {

        TableTop tableTop = gemCutter.getTableTop();

        Gem[] gems = new Gem[gemEntities.length];
        DisplayedGem[] displayedGems = new DisplayedGem[gemEntities.length];
        for (int i = 0; i < displayedGems.length; i ++ ) {
            DisplayedGem displayedGem = tableTop.createDisplayedFunctionalAgentGem(new Point(), gemEntities[i]);
            tableTop.doAddGemUserAction(displayedGem);
            gems[i] = displayedGem.getGem();
            displayedGems[i] = displayedGem;
        }
        // Use the layout arranger to line them up
        Graph.LayoutArranger layoutArranger = new Graph.LayoutArranger(displayedGems);
        tableTop.doTidyUserAction(layoutArranger, null);
        return gems;
    }

    /**
     * This method uses the gem cutter undoable support to begin a new edit operation.  It must be matched by a 
     * call to endUndoableEdit()
     */
    public void beginUndoableEdit() {
        getUndoableEditSupport().beginUpdate();
    }

    /**
     * This method uses the gem cutter undoable support to end an edit operation.  It must be called after
     * begineUndoableEdit()
     */
    public void endUndoableEdit() {
        getUndoableEditSupport().endUpdate();
    }

    /**
     * Sets the name of the current undoable operation.
     * @param editName String - The name to be used for the command (may be used in UI)
     */
    public void setUndoableName(String editName) {
        UndoableEditSupport undoableEditSupport = getUndoableEditSupport();
        if (undoableEditSupport instanceof ExtendedUndoableEditSupport) {
            ((ExtendedUndoableEditSupport)undoableEditSupport).setEditName(editName);
        }
    }

    /**
     * see org.openquark.gems.client.explorer.TableTopExplorerOwner#getUndoableEditSupport()
     */
    private UndoableEditSupport getUndoableEditSupport() { 
        return gemCutter.getTableTop().getUndoableEditSupport();
    }

    /**
     * Burns this input
     * @param input
     * @return boolean
     */
    public boolean doBurnInputAction(Gem.PartInput input){
        TableTop tableTop = gemCutter.getTableTop();
        DisplayedGem.DisplayedPart displayedPart = tableTop.getDisplayedPartConnectable(input);
        return tableTop.getBurnManager().handleBurnInputGesture(displayedPart);
    }

    /**
     * @see org.openquark.gems.client.DisplayedGemStateListener#runStateChanged(DisplayedGemStateEvent)
     */
    public void runStateChanged(DisplayedGemStateEvent e) {}

    /**
     * @see org.openquark.gems.client.DisplayedGemStateListener#selectionStateChanged(DisplayedGemStateEvent)
     * Currently not available since Explorer currently only supports single selection.
     */
    public void selectionStateChanged(DisplayedGemStateEvent e) {
        DisplayedGem displayedGem =  (DisplayedGem) e.getSource();

        // Only select the gem in the explorer if the user has not already selected one
        // of the gem's inputs. This ensures that if the user clicks on an input of the gem
        // the selection will stay on the input and not move to the gem itself.
        if (gemCutter.getTableTop().isSelected(displayedGem)) {

            DefaultMutableTreeNode gemNode = tableTopExplorer.getGemNode(displayedGem.getGem());
            TreePath newSelectionPath = tableTopExplorer.getExplorerTree().getSelectionPath();
            TreePath oldSelectionPath = tableTopExplorer.getOldSelectionPath();
            TreePath pathToGemNode = gemNode != null ? new TreePath(gemNode.getPath()) : null;

            if (pathToGemNode == null) {
                // This means the gem was deleted and as a result its selection changed.
                // In that case don't do anything at all.
                return;

            } else if (pathToGemNode.isDescendant(newSelectionPath)) {
                // This happens if we have the root node selected and click on an input.
                // In this case don't change the selection, since we want the input to stay selected.
                return;

            } else if (oldSelectionPath == null) {
                tableTopExplorer.selectGem(displayedGem.getGem());

            } else if (pathToGemNode.isDescendant(newSelectionPath)) {
                // Do nothing.

            } else if (!pathToGemNode.isDescendant(oldSelectionPath)) {
                tableTopExplorer.selectGem(displayedGem.getGem());

            } else {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) oldSelectionPath.getLastPathComponent();

                if (!(selectedNode.getUserObject() instanceof Gem.PartInput)) {
                    tableTopExplorer.selectGem(displayedGem.getGem());

                } else {

                    // Now make sure the input is actually selected. The selection state changes here are a little
                    // complicated. Assuming that originally a gem is selected in the tree, this happens:
                    //
                    // 1. First gem is de-selected when you click on the input of another gem in the tree.
                    //    That causes this method to be called with the old gem (which now is not selected)
                    //    and hence we clear the selection for the tree (the outermost else-if clause below).
                    //
                    // 2. Select the gem whose input was clicked. This will cause this method to be called again.
                    //    This time with the new selected gem and the code will end up here.
                    //
                    // 3. Select the input in the tree (what we do below). This will again cause the new
                    //    gem to be selected, but because it already is selected it will not fire another
                    //    selectionStateChanged event. Therefore the code wont cause an infinite loop.
                    //
                    // Note that steps 1 & 2 happen as a result of gem selection changing in the selectGem() method
                    // which is called by the TableTopExplorer's TreeSelectionListener because you clicked the tree.
                    //
                    // Any questions? ;-)

                    tableTopExplorer.getExplorerTree().setSelectionPath(oldSelectionPath);
                }
            }

        } else if (gemCutter.getTableTop().getSelectedGems().length == 0) {

            // The gem may be selected, then the user clicks on the Table Top node
            // and the gem will be unselected. Then we get here and say selectGem(null).
            // That will clear *all* selection paths and will force the user to click the
            // TableTop node twice to get it to stay selected. To prevent that we reselect
            // the selected node if it is different from the gem node.

            TreePath newSelectionPath = tableTopExplorer.getExplorerTree().getSelectionPath();
            DefaultMutableTreeNode gemNode = tableTopExplorer.getGemNode(displayedGem.getGem());
            TreePath gemNodePath = gemNode != null ? new TreePath(gemNode.getPath()) : null;

            tableTopExplorer.selectGem(null);

            if (newSelectionPath != null) {

                Object userObject = ((DefaultMutableTreeNode) newSelectionPath.getLastPathComponent()).getUserObject();
                boolean isGemInput = userObject instanceof Gem.PartInput && ((Gem.PartInput) userObject).getGem() == displayedGem.getGem();

                if (gemNode == null || (!gemNodePath.equals(newSelectionPath) && !isGemInput)) {
                    tableTopExplorer.getExplorerTree().setSelectionPath(newSelectionPath);
                }
            }
        }
    }

    /**
     * @return the Action that adds a new gem to the table top
     */
    private Action getAddGemAction() {

        Action addGemAction = new AbstractAction (GemCutter.getResourceString("PopItem_AddGem"),
                new ImageIcon(getClass().getResource("/Resources/addNewGem.gif"))) {

            private static final long serialVersionUID = 5114481077769168106L;

            public void actionPerformed(ActionEvent evt) {

                ExplorerTree explorerTree = tableTopExplorer.getExplorerTree();
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();

                if (treeNode == null) {
                    treeNode = (DefaultMutableTreeNode) explorerTree.getModel().getRoot();
                    explorerTree.setSelectionPath(new TreePath(treeNode.getPath()));
                }

                Rectangle location = getIntellicutLocation(treeNode);

                gemCutter.getIntellicutManager().startIntellicutMode(null, location, false, null, explorerTree);
            }
        };

        addGemAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_INTELLICUT));
        addGemAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_INTELLICUT);

        return addGemAction;
    }

    /**
     * @param inputPart the part input to start intellicut for, or null if intellicut should be started
     * for the table top
     * @return the action for starting Intellicut for an input part of the table top
     */
    private Action getIntellicutAction(final Gem.PartInput inputPart) {

        Action intellicutAction = new AbstractAction(GemCutter.getResourceString("PopItem_Intellicut"),
                new ImageIcon(getClass().getResource("/Resources/intellicut.gif"))) {

            private static final long serialVersionUID = 4980938816525148088L;

            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode treeNode = tableTopExplorer.getInputNode(inputPart);
                DisplayedPartConnectable displayedPart = gemCutter.getTableTop().getDisplayedPartConnectable(inputPart);
                Rectangle location = getIntellicutLocation(treeNode);
                gemCutter.getIntellicutManager().startIntellicutMode(displayedPart, location, false, null, tableTopExplorer.getExplorerTree());
            }
        };

        intellicutAction.setEnabled(inputPart == null || (!inputPart.isBurnt() && !inputPart.isConnected()));
        intellicutAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_INTELLICUT);

        return intellicutAction;
    }

    /**
     * @param name the CAL name that we want to check if it can be assigned to the gem without causing
     * a name conflict
     * @param gem the gem which will be assigned the CAL name.
     * @return true if the gem can be renamed to the specfied name without causing a name conflict.  If
     * the specified name is the same as the gems current name then true is returned since it can be
     * assigned the same name without causing a conflict.
     */
    public boolean isAvailableCodeOrCollectorName(String name, Gem gem) {
        TableTop tableTop = gemCutter.getTableTop();
        return tableTop.isAvailableCodeOrCollectorName(name, gem);
    }

    /**
     * Used to rename a code gem.  If the commit flag is true then the change is posted to the undoable
     * edit support.  If the commit flag is false then the change still occurs, but the edit is not 
     * posted.  Also, if the newName and oldName are equivalent then then the edit is never posted
     * regardless of the commit flag.
     * @param codeGem CodeGem - The code gem to rename
     * @param newName String - The new name for the gem
     * @param oldName String - The old name for the gem
     * @param commit boolean - Whether the undo support should be committed
     */
    public void renameCodeGem(CodeGem codeGem, String newName, String oldName, boolean commit) {
        TableTop tableTop = gemCutter.getTableTop();

        tableTop.renameCodeGem(codeGem, newName);

        // the text size wouldn't change on commit so no need to repaint let gems

        // Notify the undo manager of the name change, if any

        if (!codeGem.getUnqualifiedName().equals(oldName) && commit) {
            getUndoableEditSupport().postEdit(new UndoableChangeCodeGemNameEdit(tableTop, codeGem, oldName));
        }
    }

    /**
     * Used to rename a collector gem.  If the commit flag is true then the change is posted to the undoable
     * edit support.  If the commit flag is false then the change still occurs, but the edit is not 
     * posted.  Also, if the newName and oldName are equivalent then then the edit is never posted
     * regardless of the commit flag.
     * @param collectorGem CollectorGem - The code gem to rename
     * @param newName String - The new name for the gem
     * @param oldName String - The old name for the gem
     * @param commit boolean - Whether the undo support should be committed
     */
    public void renameCollectorGem(CollectorGem collectorGem, String newName, String oldName, boolean commit) {
        TableTop tableTop = gemCutter.getTableTop();
        collectorGem.setName(newName);
        tableTop.resizeForGems();

        // The text size wouldn't change on commit so no need to repaint let gems

        // Notify the undo manager of the name change, if any
        if (!collectorGem.getUnqualifiedName().equals(oldName) && commit) {
            getUndoableEditSupport().postEdit(new UndoableChangeCollectorNameEdit(tableTop, collectorGem, oldName));
        }
    }

    /**
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#isDNDEnabled()
     */    
    public boolean isDNDEnabled() {
        return true;
    }

    /**
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#getValueEditorManager()
     */
    public ValueEditorManager getValueEditorManager() {
        return gemCutter.getValueEditorManager();
    }

    /**
     * {@inheritDoc}
     */
    public ValueEditorContext getValueEditorContext(final ValueGem valueGem) {
        return new ValueEditorContext() {
            public TypeExpr getLeastConstrainedTypeExpr() {
                return gemCutter.getTableTop().getGemGraph().getLeastConstrainedValueType(valueGem, gemCutter.getTypeCheckInfo());
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public ValueEditorContext getValueEditorContext(PartInput partInput) {
        return null;
    }

    /**
     * @see TableTopExplorerOwner#getValueNode(ValueGem)
     */
    public ValueNode getValueNode(ValueGem valueGem){
        return valueGem.getValueNode();
    }

    /**
     * @see TableTopExplorerOwner#changeValueNode(ValueGem, ValueNode)
     */
    public void changeValueNode(ValueGem valueGem, ValueNode valueNode) {

        ValueNode oldValue = valueGem.getValueNode();
        if (valueNode.sameValue(oldValue)) {
            return;
        }
        gemCutter.getTableTop().handleValueGemCommitted(valueGem, oldValue, valueNode);
    }

    /**
     * @see TableTopExplorerOwner#getHTMLFormattedMetadata(Gem.PartInput)
     */
    public String getHTMLFormattedMetadata(Gem.PartInput input) {
        TableTop tableTop = gemCutter.getTableTop();
        ScopedEntityNamingPolicy namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(tableTop.getCurrentModuleTypeInfo());               
        return ToolTipHelpers.getPartToolTip(input, tableTop.getGemGraph(), namingPolicy, tableTopExplorer.getExplorerTree());
    }

    /**
     * @see TableTopExplorerOwner#getHTMLFormattedFunctionalAgentGemDescription(org.openquark.gems.client.FunctionalAgentGem)
     */
    public String getHTMLFormattedFunctionalAgentGemDescription(FunctionalAgentGem gem) {
        return ToolTipHelpers.getFunctionalAgentToolTip(gem, tableTopExplorer.getExplorerTree(), GemCutter.getLocaleFromPreferences());
    }

    /**
     * @see TableTopExplorerOwner#getValueNode(Gem.PartInput)
     */
    public ValueNode getValueNode(Gem.PartInput inputPart) {
        return null;
    }

    /**
     * @see TableTopExplorerOwner#canEditInputsAsValues()
     */
    public boolean canEditInputsAsValues() {
        return false;
    }

    /**
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#highlightInput(Gem.PartInput)
     */
    public boolean highlightInput(Gem.PartInput input) {
        return false;
    }

    /**
     * @see TableTopExplorerOwner#changeValueNode(Gem.PartInput, ValueNode)
     */
    public void changeValueNode(Gem.PartInput partInput, ValueNode valueNode) {
    }

    /**
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#hasPhotoLook()
     */
    public boolean hasPhotoLook() {
        return gemCutter.getTableTop().getTableTopPanel().isPhotoLook();
    }

    /**
     * @param treeNode the tree node to get the intellicut position for
     * @return the position at which intellicut should be displayed for a given node
     */
    private Rectangle getIntellicutLocation(DefaultMutableTreeNode treeNode) {

        ExplorerTree explorerTree = tableTopExplorer.getExplorerTree();
        Rectangle location = explorerTree.getPathBounds(new TreePath(treeNode.getPath()));

        if (treeNode == explorerTree.getModel().getRoot()) {
            // For the root node we want to display Intellicut off to the right.
            // This is so it doesn't obscure the rest of the tree.
            location.x += location.width + 3;
            location.y += 3;
            location.height = 0;
            location.width = 0;

        } else {
            // Normal nodes get the Intellicut list right below them.
            location.x += 3;
            location.y += location.height + 3;
            location.width = 0;
            location.height = 0;
        }

        return location;
    }

    /**
     * This will display the intellicut menu if the explorer has focus and an input is selected.
     * This is called by the GemCutter if the user activates the Intellicut keyboard shortcut.
     * @return true if menu was displayed, false otherwise
     */
    boolean maybeDisplayIntellicut() {

        ExplorerTree explorerTree = getTableTopExplorer().getExplorerTree();

        if (explorerTree.isFocusOwner()) {

            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();

            if (treeNode == null) {
                treeNode = (DefaultMutableTreeNode) explorerTree.getModel().getRoot();
            }                

            Rectangle location = getIntellicutLocation(treeNode);
            Object userObject = treeNode.getUserObject();

            if (userObject instanceof Gem.PartInput) {

                Gem.PartInput input = (Gem.PartInput) userObject;

                if (input.isBurnt() || input.isConnected()) {
                    return false;
                }

                DisplayedPartConnectable displayedPart = gemCutter.getTableTop().getDisplayedPartConnectable(input);
                gemCutter.getIntellicutManager().startIntellicutMode(displayedPart, location, false, null, explorerTree);
                return true;

            } else {

                gemCutter.getIntellicutManager().startIntellicutMode(null, location, false, null, explorerTree);
                return true;
            }
        }

        return false;
    }

    /**
     * @see org.openquark.gems.client.explorer.TableTopExplorerOwner#getCurrentModuleTypeInfo()
     */
    public ModuleTypeInfo getCurrentModuleTypeInfo() {
        return gemCutter.getPerspective().getWorkingModuleTypeInfo();
    }

    /**
     * Returns a metadata runner for the specified gem.  The metadata runner may be different for every
     * gem so this method needs to be called everytime metadata needs to be run for a new gem. 
     * @param gem
     * @return A helper object that can calculate metadata for the specified gem.  This can be null if
     * no metadata can be calculated for the specified gem.
     */
    public MetadataRunner getMetadataRunner(Gem gem) {
        return null;
    }

    /**
     * Returns an editor customized for editing gem names for the current client
     * @param gem
     * @return A gem name editor customized for editing the specified gem
     */
    public ExplorerGemNameEditor getGemNameEditor(Gem gem) {
        return new CodeAndCollectorNameEditor(gem);
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeString(final TypeExpr typeExpr) {
        final ScopedEntityNamingPolicy namingPolicy;
        final ModuleTypeInfo currentModuleTypeInfo = getCurrentModuleTypeInfo();
        if (currentModuleTypeInfo == null) {
            namingPolicy = ScopedEntityNamingPolicy.FULLY_QUALIFIED;
        } else {
            namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(currentModuleTypeInfo);
        }
        
        return gemCutter.getTableTop().getGemGraph().getTypeString(typeExpr, namingPolicy);
    }
}
