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
 * NavigatorAdapter.java
 * Creation date: Jul 23, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.TreeNode;
import javax.swing.undo.StateEdit;
import javax.swing.undo.StateEditable;

import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.explorer.TableTopExplorer;
import org.openquark.gems.client.navigator.NavAddress;
import org.openquark.gems.client.navigator.NavAddressHelper;
import org.openquark.gems.client.navigator.NavFrame;
import org.openquark.gems.client.navigator.NavFrameOwner;
import org.openquark.gems.client.navigator.NavAddress.NavAddressMethod;
import org.openquark.gems.client.utilities.ExtendedUndoableEditSupport;
import org.openquark.util.Pair;


/**
 * The GemCutter's implementation of the NavFrameOwner interface. This class is basically
 * a thin wrapper around the NavFrame class. It delegates to an instance of NavFrame for all
 * functionality and simply adds undo support on top of NavFrame.
 * 
 * @author Frank Worsley
 */
class NavigatorAdapter implements NavFrameOwner {

    /**
     * Implements a state editable for metadata objects.
     * @author Frank Worsley
     */
    private class MetadataStateEditable implements StateEditable {

        /** The metadata key in the state maps. */
        private static final String METADATA_STATE_KEY = "MetadataStateKey"; 

        /** The address of the metadata object this editable is for. */
        private final NavAddress address;

        /**
         * Constructs a new state editable for the given metadata object.
         * @param address the address of the metadata object
         */
        public MetadataStateEditable(NavAddress address) {

            if (address == null) {
                throw new NullPointerException();
            }
        
            this.address = address;
        }

        /**
         * @see javax.swing.undo.StateEditable#storeState(java.util.Hashtable)
         */
        public void storeState(Hashtable<Object, Object> state) {
            CALFeatureMetadata metadata = NavAddressHelper.getMetadata(NavigatorAdapter.this, address);            
            state.put(getKey(), metadata);
        }

        /**
         * @see javax.swing.undo.StateEditable#restoreState(java.util.Hashtable)
         */
        public void restoreState(Hashtable<?, ?> state) {

            CALFeatureMetadata savedState = (CALFeatureMetadata) state.get(getKey());
        
            if (savedState != null) {
            
                boolean success = NavAddressHelper.saveMetadata(NavigatorAdapter.this, address, savedState);
                
                if (!success) {
                    throw new IllegalStateException("failed to restore saved metadata state");
                }

                // Update the state edit if the currently edited metadata was changed.
                if (address.equals(currentEditAddress)) {
                    stateEdit = new StateEdit(new MetadataStateEditable(address));
                }

                // Check the table top names once the undo completes.
                // We need to do it after the undo so GemGraph checking is enabled again.
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateInputNames(address);
                        nameCheckTableTop();
                        navFrame.refreshMetadata(address);
                    }
                });
            }
        }
    
        /**
         * @return the key to use in the state maps for the metadata
         */
        private Object getKey() {
            return new Pair<NavAddress, String>(address, METADATA_STATE_KEY);
        }
    }

    
    /** The undoable edit support for the navigator adapter. */
    private final ExtendedUndoableEditSupport undoableEditSupport;

    /** The GemCutter this adapter is for. */
    private final GemCutter gemCutter;

    /** The navigator frame this adapter is using. */
    private NavFrame navFrame = null;

    /** The state edit for the metadata object being edited. */
    private StateEdit stateEdit = null;
    
    /** The address of the metadata object currently being edited. */
    private NavAddress currentEditAddress = null;

    /**
     * Constructor for a new navigator adapter for a GemCutter.
     * @param gemCutter the GemCutter this adapter is for
     */
    public NavigatorAdapter(GemCutter gemCutter) {
        
        if (gemCutter == null) {
            throw new NullPointerException();
        }
        
        this.gemCutter = gemCutter;
        this.undoableEditSupport = new ExtendedUndoableEditSupport(this);
    }

    /**
     * Add an undoable edit listener.
     * @param listener the listener to add
     */
    public void addUndoableEditListener(UndoableEditListener listener) {
        undoableEditSupport.addUndoableEditListener(listener);
    }
    
    /**
     * Remove an undoable edit listener.
     * @param listener the listener to remove
     */
    public void removeUndoableEditListener(UndoableEditListener listener) {
        undoableEditSupport.removeUndoableEditListener(listener);
    }

    /**
     * @return whether or not the navigator instance if visible on screen
     */
    private boolean navigatorCanView() {
        return navFrame.isVisible() && !navFrame.isEditing();
    }

    /**
     * Refreshed the navigatior tree by reloading the CAL entities from the perspective.
     * This has to be done after a gem is saved so that the new gem shows up in the tree.
     */
    public void refresh() {
        
        if (navFrame == null) {
            navFrame = new NavFrame(this);
        }
        
        navFrame.refreshTree();
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#getParent()
     */
    public JFrame getParent() {
        return gemCutter;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#getTypeChecker()
     */
    public TypeChecker getTypeChecker() {
        return gemCutter.getTypeChecker();
    }

    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#getValueRunner()
     */
    public ValueRunner getValueRunner() {
        return gemCutter.getValueRunner();
    }

    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#getPerspective()
     */
    public Perspective getPerspective() {
        return gemCutter.getPerspective();
    }

    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#getCollector(java.lang.String)
     */
    public CollectorGem getCollector(String name) {
        Set<CollectorGem> collectors = gemCutter.getTableTop().getGemGraph().getCollectorsForName(name);
        return (collectors.size() == 0) ? null : collectors.iterator().next();
    }

    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#displayNavigator(boolean)
     */
    public void displayNavigator(boolean newNavigator) {
        navFrame.setVisible(true);
        navFrame.refreshViewer();
        navFrame.requestFocus();
    }

    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#displayMetadata(org.openquark.gems.client.navigator.NavAddress, boolean)
     */
    public void displayMetadata(NavAddress address, boolean newViewer) {
        if (navigatorCanView() || newViewer) {
            navFrame.displayMetadata(address);
        }
    }

    /**
     * Display metadata for the given entity.
     * @param entity the entity to display metadata for
     * @param newViewer true if a new window should be opened
     */
    public void displayMetadata(GemEntity entity, boolean newViewer) {
        displayMetadata(NavAddress.getAddress(entity), newViewer);
    }

    /**
     * Display metadata for the given entity.
     * @param entity the entity to display metadata for
     * @param newViewer true if a new window should be opened
     */
    public void displayMetadata(ScopedEntity entity, boolean newViewer) {
        displayMetadata(NavAddress.getAddress(entity), newViewer);
    }

    /**
     * Display metadata for the given module.
     * @param module the module to display metadata for
     * @param newViewer true if a new window should be opened
     */
    public void displayMetadata(MetaModule module, boolean newViewer) {
        displayMetadata(NavAddress.getAddress(module), newViewer);
    }
    
    /**
     * Display metadata for the given collector.
     * @param collector the collector to display metadata for
     * @param newViewer true if a new window should be opened
     */
    public void displayMetadata(CollectorGem collector, boolean newViewer) {
        displayMetadata(NavAddress.getAddress(collector), newViewer);
    }

    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#editMetadata(org.openquark.gems.client.navigator.NavAddress)
     */
    public void editMetadata(NavAddress address) {
        navFrame.editMetadata(address);
        currentEditAddress = address;
        stateEdit = new StateEdit(new MetadataStateEditable(address));
    }

    /**
     * Edit metadata for the given entity.
     * @param entity the entity to edit metadata for
     */
    public void editMetadata(ScopedEntity entity) {
        editMetadata(NavAddress.getAddress(entity));
    }

    /**
     * Edit metadata for the given module.
     * @param module the module to edit metadata for
     */
    public void editMetadata(MetaModule module) {
        editMetadata(NavAddress.getAddress(module));
    }

    /**
     * Edit metadata for the given collector.
     * @param collector the collector to edit metadata for
     */
    public void editMetadata(CollectorGem collector) {
        editMetadata(NavAddress.getAddress(collector));
    }
    
    /**
     * Edit argument metadata for the given entity.
     * @param entity the entity to edit argument metadata for
     * @param argNum the number of the argument whose metadata to edit
     */
    public void editMetadata(GemEntity entity, int argNum) {
        NavAddress address = NavAddress.getAddress(entity).withParameter(NavAddress.ARGUMENT_PARAMETER, Integer.toString(argNum));
        editMetadata(address);
    }
    
    /**
     * Edit argument metadata for the given collector.
     * @param collector the collector to edit argument metadata for
     * @param argNum the number of the argument whose metadata to edit
     */    
    public void editMetadata(CollectorGem collector, int argNum) {
        NavAddress address = NavAddress.getAddress(collector).withParameter(NavAddress.ARGUMENT_PARAMETER, Integer.toString(argNum));
        editMetadata(address);
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#editComplete(org.openquark.gems.client.navigator.NavAddress)
     */
    public void editComplete(NavAddress address) {

        if (!address.equals(currentEditAddress)) {
            throw new IllegalStateException("address for completed edit does not equal current edit address");
        }
        
        currentEditAddress = null;
        stateEdit = null;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#metadataChanged(org.openquark.gems.client.navigator.NavAddress)
     */
    public void metadataChanged(NavAddress address) {

        if (!address.equals(currentEditAddress)) {
            throw new IllegalStateException("address for changed edit does not equal current edit address");
        }
        
        stateEdit.end();
        
        undoableEditSupport.beginUpdate();
        undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_ChangeGemProperties"));
        undoableEditSupport.postEdit(stateEdit);
        undoableEditSupport.endUpdate();

        stateEdit = new StateEdit(new MetadataStateEditable(address));
        
        updateInputNames(address);
        nameCheckTableTop();
        
        navFrame.refreshMetadata(currentEditAddress);
    }
    
    /**
     * Updates the names of any PartInputs affected by the metadata changing for the 
     * entity with the given address. Does nothing if no updates are required.
     * @param address the address of the entity whose metadata changed
     */
    private void updateInputNames(NavAddress address) {

        NavAddressMethod method = address.getMethod();
        
        if (method != NavAddress.FUNCTION_METHOD &&
            method != NavAddress.CLASS_METHOD_METHOD &&
            method != NavAddress.DATA_CONSTRUCTOR_METHOD) {
            
            return;
        }
        
        // If this was a functional agent, then we need to update its gems on the table top.
        GemEntity entity = getPerspective().getWorkspace().getGemEntity(address.toFeatureName().toQualifiedName());
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata) NavAddressHelper.getMetadata(this, address.withAllStripped());
        ArgumentMetadata[] argMetadata = metadata.getArguments();
        
        for (final Gem gem : gemCutter.getTableTop().getGemGraph().getGems()) {
            
            if (gem instanceof FunctionalAgentGem) {
                
                GemEntity gemEntity = ((FunctionalAgentGem) gem).getGemEntity();
                
                if (gemEntity.equals(entity)) {

                    // We need to update the inputs of this gem
                    PartInput[] inputs = gem.getInputParts();
                    
                    for (int i = 0; i < inputs.length; i++) {
                        
                        // Update the original name for this input.
                        String displayName = i < argMetadata.length ? argMetadata[i].getDisplayName() : null;
                        String nameFromCode = i < entity.getNNamedArguments() ? entity.getNamedArgument(i) : null;
                        
                        inputs[i].setOriginalInputName(displayName != null ? displayName : nameFromCode);
                    }
                }
            }
        }
    }
    
    /**
     * Name checks the table top to disambiguate any input names that may have become ambiguous
     * as a result of the metadata changing. Also updates the TableTopExplorer to correctly
     * display the new input names.
     */
    private void nameCheckTableTop() {

        // Update the tabletop for the new gem graph state.  Among other things, this will ensure arg name disambiguation.
        gemCutter.getTableTop().updateForGemGraph();
        
        // Since we don't know which input names actually changed as a result of
        // name checking the table top, we simply have to update all nodes in the explorer.
        
        TableTopExplorer tableTopExplorer = gemCutter.getTableTopExplorer();
        Set<DisplayedGem> displayedGemSet = gemCutter.getTableTop().getDisplayedGems();
        
        for (final DisplayedGem gem : displayedGemSet) {
            
            PartInput[] inputs = gem.getGem().getInputParts();
            
            for (final PartInput input : inputs) {
                TreeNode node = tableTopExplorer.getInputNode(input);
                
                if (node != null) {
                    tableTopExplorer.getExplorerTreeModel().nodeChanged(node);
                }
            }
        }
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavFrameOwner#searchMetadata(java.lang.String)
     */
    public List<NavAddress> searchMetadata(String searchString) {
        return navFrame.searchMetadata(searchString);
    }
    
    /**
     * Tests all metadata examples.
     */
    public void testMetadataExamples() {        
        String conciseTitle = "Concise Output?";
        String conciseMessage = "Would you like to suppress the output of successful metadata tests?";
        int conciseResponse = JOptionPane.showOptionDialog(gemCutter,
                conciseMessage,
                conciseTitle,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null); 
        
        // If the user closed the dialog or hit cancel, do nothing
        if (conciseResponse == JOptionPane.CANCEL_OPTION || conciseResponse == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String autoTitle = "Skip non-automatic examples?";
        String autoMessage = "Would you like to run only those examples set to run automatically?";
        int autoResponse = JOptionPane.showOptionDialog(gemCutter,
                autoMessage,
                autoTitle,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null); 
        
        // If the user closed the dialog or hit cancel, do nothing
        if (autoResponse == JOptionPane.CANCEL_OPTION || autoResponse == JOptionPane.CLOSED_OPTION) {
            return;
        }
        
        navFrame.testMetadataExamples(conciseResponse == JOptionPane.YES_OPTION, autoResponse==JOptionPane.YES_OPTION);
    }

    /**
     * {@inheritDoc}
     */
    public Locale getLocaleForMetadata() {
        return GemCutter.getLocaleFromPreferences();
    }
}
