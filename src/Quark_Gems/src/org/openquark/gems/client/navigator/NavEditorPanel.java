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
 * NavEditorPanel.java
 * Creation date: Jul 8, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.ClassMethodMetadata;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.InstanceMethodMetadata;
import org.openquark.gems.client.navigator.NavAddress.NavAddressMethod;


/**
 * This class implements the metadata editing component for the CAL navigator.
 * It dynamically builds an editing UI depending on what kind of metadata is being
 * edited. The UI consists of editing sections embedded within the component. 
 * Sections can be expanded by the user to reveal additional metadata attributes
 * that can be edited.
 * 
 * This component is completely independent of the actual NavFrame class and
 * can be used by any class implementing the NavFrameOwner interface.
 * 
 * @author Frank Worsley
 */
class NavEditorPanel extends JPanel implements Scrollable {
    
    private static final long serialVersionUID = 4454976555509376719L;

    /**
     * The focus traversal policy for the editor panel. It ensures that the top
     * editor section will receive initial and default focus.
     * @author Frank Worsley
     */
    private class EditorFocusTraversalPolicy extends LayoutFocusTraversalPolicy {
    
        private static final long serialVersionUID = 3928878956202325352L;

        @Override
        public Component getDefaultComponent(Container c) {
            return editorSections.get(0);
        }
    
        @Override
        public Component getFirstComponent(Container c) {
            return editorSections.get(0);
        }
        
        @Override
        public Component getLastComponent(Container c) {
            return editorSections.get(editorSections.size() - 1);
        }
    }
    
    /**
     * A listener that listens to the component that has focus. If the focused component
     * is an editor section or the child of an editor section it will give the editor
     * section a focused look.
     * @author Frank Worsley
     */
    private class EditorFocusChangeListener implements PropertyChangeListener {
        
        /** The editor section that currently has focus. */
        private NavEditorSection currentFocusedSection = null;
        
        /** The editor section that last had focused (current section if a section currently has focus). */
        private Component lastFocusedSection = null;
                
        /** The component that last had focus. */
        private Component lastFocusedComponent = null;

        public void propertyChange(PropertyChangeEvent evt) {
           
            Component newFocusOwner = (Component) evt.getNewValue();
            Component parent = newFocusOwner;
            
            // Check if the new focus owner is a child of an editor section.
            // If it is then give the section the focused look.            
            while (parent != null) {
                
                if (parent instanceof NavEditorSection) {

                    if (newFocusOwner instanceof NavEditorSection &&
                        newFocusOwner != lastFocusedSection) {

                        // If it's a new section make sure the top title is visible
                        if (lastFocusedSection != newFocusOwner) {
                            JComponent expander = ((NavEditorSection) newFocusOwner).getExpander();
                            expander.scrollRectToVisible(expander.getBounds());
                        }

                        lastFocusedSection = newFocusOwner;
                    
                    } else if (newFocusOwner != lastFocusedComponent) {

                        // We need to convert the coordinates and scroll directly in the
                        // editor pane since some editors may be viewports themselves
                        // and will then try to do the scrolling (ie: JTextField).
                        Rectangle bounds = newFocusOwner.getBounds();
                        Rectangle parentBounds = SwingUtilities.convertRectangle(newFocusOwner.getParent(), bounds, NavEditorPanel.this);
                        scrollRectToVisible(parentBounds);
                        lastFocusedComponent = newFocusOwner;
                    }
                    
                    if (currentFocusedSection != null && parent != currentFocusedSection) {
                        currentFocusedSection.setFocusedLook(false);
                    }
                    
                    if (currentFocusedSection != parent) {
                        currentFocusedSection = (NavEditorSection) parent;
                        currentFocusedSection.setFocusedLook(true);
                    }
                    
                    return;
                }
                
                parent = parent.getParent();
            }

            // clear the previous section's focused look
            if (currentFocusedSection != null) {
                currentFocusedSection.setFocusedLook(false);
                currentFocusedSection = null;
            }
        }
        
        /**
         * Restore the focus to the component of the editor panel that was last focused.
         */
        public void restoreSavedFocus() {
            
            // We try the last component, section and then just do the default component. We have to
            // check if the parent is still this editor panel since editors/sections may have been
            // removed from the panel since the focus was last saved.
            
            if (lastFocusedComponent != null && SwingUtilities.isDescendingFrom(lastFocusedComponent, NavEditorPanel.this)) {
                lastFocusedComponent.requestFocus();
                
            } else if (lastFocusedSection != null && SwingUtilities.isDescendingFrom(lastFocusedSection, NavEditorPanel.this)) {
                lastFocusedSection.requestFocus();
                
            } else {
                getFocusTraversalPolicy().getDefaultComponent(NavEditorPanel.this).requestFocus();
            }
        }
    }    

    /** The navigator owner that owns this editor panel. */
    private final NavFrameOwner owner;

    /** The metadata object being edited by this editor panel. */
    private final CALFeatureMetadata metadata;
    
    /** The address of the object whose metadata is being edited. */
    private final NavAddress address;

    /** The editor sections contained in this editor panel in the order they appear. */
    private final List<NavEditorSection> editorSections = new ArrayList<NavEditorSection>();

    /** The JPanel that contains the editor sections. */
    private final JPanel sectionPanel = new JPanel();
    
    /** The argument editor section, if editing entity metadata. */
    private NavEditorSection argumentSection = null;
    
    /** Whether or not the values stored by the editors in the editor panel have changed. */
    private boolean hasChanged = false;

    /** The label for displaying the name of the metadata being edited. */
    private final JLabel titleLabel = new JLabel();
    
    /** The label for displaying the type of the metadata being edited. */
    private final JLabel typeLabel = new JLabel();
    
    /** The label to display the type of the argument for which editing argument metadata. */
    private final JLabel argTypeLabel = new JLabel();
    
    /** The focus listener that is used to create the focus effects of the editor sections. */
    private EditorFocusChangeListener focusListener;

    /**
     * Constructs a new editor panel to edit the given metadata object.
     * @param owner the owner of this editor panel
     * @param address the address of the metadata to edit
     */
    public NavEditorPanel(NavFrameOwner owner, NavAddress address) {
        
        if (owner == null || address == null) {
            throw new NullPointerException();
        }

        this.owner = owner;
        this.address = address;
        this.metadata = NavAddressHelper.getMetadata(owner, address);
        
        if (metadata == null) {
            throw new IllegalArgumentException("no metadata associated with address: " + address);
        }
        
        initialize();
    }
    
    /**
     * Initializes the UI of the editor panel by creating default components
     * and adding editor sections relevant to the metadata being edited.
     */
    private void initialize() {
        
        // Setup the basic properties
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());

        // Install a focus policy to always give default focus to the topmost editor section
        focusListener = new EditorFocusChangeListener();
        addFocusListener();
        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new EditorFocusTraversalPolicy());
        
        // Add the title labels
        typeLabel.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 2));
        titleLabel.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 12));
        argTypeLabel.setFont(getFont().deriveFont(Font.ITALIC, getFont().getSize() + 12));
        Box vbox = Box.createVerticalBox();
        Box hbox = Box.createHorizontalBox();
        hbox.add(typeLabel);
        hbox.add(Box.createHorizontalGlue());
        vbox.add(hbox);
        hbox = Box.createHorizontalBox();
        hbox.add(titleLabel);
        hbox.add(argTypeLabel);
        hbox.add(Box.createHorizontalGlue());
        vbox.add(hbox);
        add(vbox, BorderLayout.NORTH);
        
        // Add the panel for the editor sections
        sectionPanel.setOpaque(false);
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        add(sectionPanel, BorderLayout.CENTER);
        
        // Sucks up extra vertical space at the bottom of the section panel
        sectionPanel.add(Box.createVerticalGlue());
        
        // Add the basic metadata editing section
        addSection(new NavFeatureEditorSection(this));

        // Add entity metadata editing sections
        if (metadata instanceof FunctionalAgentMetadata || metadata instanceof InstanceMethodMetadata) {
            addSection(new NavGemEntityEditorSection(this));
            
            boolean hasReturnValue = metadata instanceof FunctionMetadata || metadata instanceof ClassMethodMetadata || metadata instanceof InstanceMethodMetadata;
            
            argumentSection = new NavEntityArgumentEditorSection(this, hasReturnValue); 
            addSection(argumentSection);
            
            addSection(new NavExampleEditorSection(this));
        }

        // Add an argument editing section
        if (metadata instanceof ArgumentMetadata) {
            addSection(new NavArgumentEditorSection(this));
        }
        
        // Add a custom attribute section
        addSection(new NavCustomAttributeEditorSection(this));
        
        // Load the metadata
        revert();
        
        // Add the finishing touches
        updateTitleLabel();
        editorSections.get(0).setExpanded(true);
    }

    /**
     * Install a listener for focus events to handle focusing the editing sections.
     */    
    public void addFocusListener() {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener("focusOwner", focusListener);
    }

    /**
     * Removes the focus listener installed by the editor panel. This must be called
     * when the editor panel is disposed off, otherwise it will leak a focus listener.
     */
    private void removeFocusListener() {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.removePropertyChangeListener("focusOwner", focusListener);        
    }

    /**
     * Sets focus on the component of the editor panel that should have initial focus.
     */    
    public void setInitialFocus() {
        focusListener.restoreSavedFocus();
    }

    /**
     * Updates the text displayed in the title label to match what is stored
     * in the metadata.
     */    
    private void updateTitleLabel() {

        argTypeLabel.setText(null);
        typeLabel.setText(getTypeString());
        titleLabel.setText(NavAddressHelper.getDisplayText(owner, address, ScopedEntityNamingPolicy.UNQUALIFIED));
        
        if (address.getParameter(NavAddress.ARGUMENT_PARAMETER) != null) {
            
            int argumentNumber = Integer.parseInt(address.getParameter(NavAddress.ARGUMENT_PARAMETER));
            ModuleTypeInfo moduleInfo = owner.getPerspective().getWorkingModuleTypeInfo();
            ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(moduleInfo);
            NavAddress parentAddress;
            if (address.getMethod() == NavAddress.INSTANCE_METHOD_METHOD) {
                parentAddress = NavAddress.getAddress(address.toFeatureName()); // this strips out the &argument=n parameter
            } else {
                parentAddress = address.withAllStripped();
            }
            String[] typeStrings = NavAddressHelper.getTypeStrings(owner, parentAddress, namingPolicy);
            argTypeLabel.setText(" :: " + typeStrings[argumentNumber]);
        }
    }
    
    /**
     * @return the string to use for the type label
     */
    private String getTypeString() {
        
        NavAddressMethod method = address.getMethod();
        
        if (address.getParameter(NavAddress.ARGUMENT_PARAMETER) != null) {
            
            if (method == NavAddress.INSTANCE_METHOD_METHOD) {
                NavAddress parentAddress = NavAddress.getAddress(address.toFeatureName()); // this strips out the &argument=n parameter
                String parentName = NavAddressHelper.getDisplayText(owner, parentAddress);
                
                return NavigatorMessages.getString("NAV_EditingInstanceMethodArg_Header", parentName);
                
            } else {
                String parentName = NavAddressHelper.getDisplayText(owner, address.withAllStripped());
                
                if (method == NavAddress.FUNCTION_METHOD) {
                    return NavigatorMessages.getString("NAV_EditingFunctionArg_Header", parentName);
                    
                } else if (method == NavAddress.CLASS_METHOD_METHOD) {
                    return NavigatorMessages.getString("NAV_EditingClassMethodArg_Header", parentName);
                    
                } else if (method == NavAddress.DATA_CONSTRUCTOR_METHOD) {
                    return NavigatorMessages.getString("NAV_EditingConstructorArg_Header", parentName);
                    
                } else if (method == NavAddress.COLLECTOR_METHOD) {
                    return NavigatorMessages.getString("NAV_EditingCollectorArg_Header", parentName);
                    
                } else {
                    return NavigatorMessages.getString("NAV_EditingGenericArg_Header");
                }
            }
            
        } else if (method == NavAddress.COLLECTOR_METHOD) {
            return NavigatorMessages.getString("NAV_EditingCollector_Header");
        
        } else if (method == NavAddress.MODULE_METHOD) {
            return NavigatorMessages.getString("NAV_EditingModule_Header");
        
        } else if (method == NavAddress.FUNCTION_METHOD) {
            return NavigatorMessages.getString("NAV_EditingFunction_Header");
        
        } else if (method == NavAddress.TYPE_CLASS_METHOD) {
            return NavigatorMessages.getString("NAV_EditingClass_Header");
        
        } else if (method == NavAddress.TYPE_CONSTRUCTOR_METHOD) {
            return NavigatorMessages.getString("NAV_EditingType_Header");
        
        } else if (method == NavAddress.DATA_CONSTRUCTOR_METHOD) {
            return NavigatorMessages.getString("NAV_EditingConstructor_Header");
        
        } else if (method == NavAddress.CLASS_METHOD_METHOD) {
            return NavigatorMessages.getString("NAV_EditingClassMethod_Header");

        } else if (method == NavAddress.CLASS_INSTANCE_METHOD) {
            return NavigatorMessages.getString("NAV_EditingClassInstance_Header");
            
        } else if (method == NavAddress.INSTANCE_METHOD_METHOD) {
            return NavigatorMessages.getString("NAV_EditingInstanceMethod_Header");

        } else {
            throw new IllegalArgumentException("address type not supported: " + method);
        }
    }
    
    /**
     * Adds a new editor section to this editor panel.
     * @param section the new section to add
     */
    void addSection(NavEditorSection section) {
        editorSections.add(section);
        sectionPanel.add(Box.createVerticalStrut(10));
        sectionPanel.add(section);
    }

    /**
     * Removes all editor sections from this editor panel.
     */    
    void removeAllSections() {
        sectionPanel.removeAll();
        sectionPanel.add(Box.createVerticalGlue());
        editorSections.clear();        
    }
    
    /**
     * Called when the values stored by the editors in the given section have changed.
     * @param section the section whose editors changed
     */
    void sectionChanged(NavEditorSection section) {
        hasChanged = true;
    }
    
    /**
     * @return true if the editor sections in this editor panel have changed since the metadata was last saved
     */
    public boolean hasChanged() {
        return hasChanged;
    }
    
    /**
     * Validates all sections in the editor panel.
     * @return true if all values are valid, false otherwise
     */
    public boolean checkValues() {

        boolean hasErrors = false;

        for (final NavEditorSection section : editorSections) {
            if (!section.doValidate()) {
                hasErrors = true;
            }
        }
        
        return !hasErrors;
    }

    /**
     * Refreshes the metadata being edited. This will display the newly refreshed metadata
     * in the edit boxes, overwriting any changes made by the user.
     */    
    public void refresh() {
        NavAddressHelper.refreshMetadata(owner, address, metadata);
        revert();
    }
    
    /**
     * This will revert the displayed values to the stored values from the metadata object.
     */    
     private void revert() {
        
        for (final NavEditorSection section : editorSections) {
            section.doRevert();
            section.doValidate();
        }

        hasChanged = false;
        updateTitleLabel();
    }

    /**
     * This method tells the editor panel that the editing process should stop.
     * It causes the focus listener to be removed and the editComplete method of the owner
     * of this editor panel to be invoked.
     */    
    void stopEditing() {
        removeFocusListener();
        owner.editComplete(address);
    }

    /**
     * Saves the values stored in the editors of all sections back into the metadata
     * object being edited and saves the metadata back to permanent storage.
     */
    public boolean save() {
        
        // Make sure all sections are valid
        if (!checkValues()) {
            return false;
        }
        
        // Save each section
        for (final NavEditorSection section : editorSections) {
            section.doSave();
        }
        
        // Now try to permanently store the metadata.
        if (saveMetadata()) {
            
            owner.metadataChanged(address);

            hasChanged = false;
            updateTitleLabel();
            
            return true;
        }

        return false;
    }
    
    /**
     * @return a metadata object for the metadata currently being displayed in the editors.
     * The metadata will have been saved into the object that is returned, but it was not 
     * saved to permanent storage.
     */
    public CALFeatureMetadata getEditedMetadata() {
        
        // Back up the current metadata.
        CALFeatureMetadata currentMetadata = metadata.copy();
        
        // Save each section
        for (final NavEditorSection section : editorSections) {
            section.doSave();
        }

        // Restore the saved metadata.
        CALFeatureMetadata editedMetadata = metadata.copy();
        currentMetadata.copyTo(metadata);
        
        return editedMetadata;
    }
    
    /**
     * Sets the metadata currently being displayed in the editor fields to the values stored in 
     * the given metadata object. The new values will not be saved to permanent storage.
     * @param editedMetadata the new metadata to be edited
     */
    public void setEditedMetadata(CALFeatureMetadata editedMetadata) {
        editedMetadata.copyTo(metadata);
        revert();
        hasChanged = true;
    }
    
    /**
     * Saves the metadata object being edited.
     * @return true if saved successfully, false otherwise
     */
    private boolean saveMetadata() {
        return NavAddressHelper.saveMetadata(owner, address, metadata);
    }
    
    /**
     * @return the navigator owner that is using this editor panel
     */
    NavFrameOwner getNavigatorOwner() {
        return owner;
    }
    
    /**
     * @return the address of the metadata object being edited
     */
    NavAddress getAddress() {
        return address;
    }
    
    /**
     * @return the metadata object being edited by this editor panel
     */
    CALFeatureMetadata getMetadata() {
        return metadata;
    }

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
        return true;
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
        return 150;
    }

    /**
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 50;
    }
}
