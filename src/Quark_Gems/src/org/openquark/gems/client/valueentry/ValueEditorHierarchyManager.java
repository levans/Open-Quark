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
 * ValueEditorHierarchyManager.java
 * Creation date: April 3rd 03
 * By: Ken Wong
 */
package org.openquark.gems.client.valueentry;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * This class is responsible for maintaining the hierarchy of ValueEditors.
 * The existence of this class allows the possibility of having multiple hierarchies for a single ValueEditorManager.
 * So in a single apps, if there are multiple deployment of ValueEntry for similar purposes, they can share the same
 * ValueEditorManager (which allows the creation and management of individual ValueEntryPanels) while maintaining fine grain
 * control each individual hierarchy.
 * @author Ken Wong
 */
public class ValueEditorHierarchyManager {
    
    /** The ValueEditorManager that will be providing the resources for this HierarchyManager to manage its hierarchy */
    private final ValueEditorManager valueEditorManager;
    
    /**
     * The Container which holds all of the ValueEditors.
     * If not set, value editors will be placed in the JLayeredPane.
     */
    private final JComponent valueEditorContainer;
        
    /** The list of the top level editor panels. */
    private final List<ValueEditor> topEditorPanels = new ArrayList<ValueEditor>();
    
    /**
     * This, and the currentEditor gives the ValueEditor Hierarchy.
     * The Value Editors in this stack trace a path from the first ValueEditor, thru its children, to the currentEditor.
     * Note: The currentEditor value is not stored in this stack.  It will be stored
     * in this stack if it launches a new ValueEditor, and so its child will be the new currentEditor.
     */
    private final Stack<ValueEditor> currentEditorStack = new Stack<ValueEditor>();

    /** There can be only one editor open (with the focus) across all ValueEditors managed by a hierarchy manager. */
    private ValueEditor currentEditor = null;
    
    /** This listener listens for mouse clicks and dismisses value editors accordingly. 
     *  This listener will be installed when the first top-level value editor is added to the hierarchy,
     *    and will be removed if the last top-level value editor is removed from the hierarchy. */
    private final AWTEventListener hierarchyEventListener;
    
    /** The commit cancel handler for this hierarchy, if any. */
    private HierarchyCommitCancelHandler hierarchyCommitCancelHandler = null;

    private final boolean useFocusListeners;
    
    /**
     * This interface allows specialization the behaviour of the hierarchy in the event of a top-level "hard" commit
     *   or cancel event from a top-level value editor.
     * For example, some applications may be waiting on data entered from this hierarchy, in which case a "hard" commit or cancel
     *   will indicate that the user has finished entering data.
     * @author Edward Lam
     */
    public static interface HierarchyCommitCancelHandler {
        /**
         * Handle "hard" commits or cancels from a top-level value editor in this hierarchy.
         *   This occurs when a top-level editor handles a commit or cancel gesture (such as the <Enter> key) or
         *   editors are commited by mouse click which doesn't hit any editors.
         * @param commit whether a commit or a cancel was performed.
         *   True = commit, false = cancel.
         */
        public abstract void handleHierarchyCommitCancel(boolean commit);
    }    
    
    /**
     * An AWTEventListener installed by the hierarchy manager. It listens to mouse pressed
     * and hierarchy changed events to update the value editor hierarchy if stuff happens.
     * @author Frank Worsley
     */
    private class HierarchyEventListener implements AWTEventListener {

        /**
         * @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
         */
        public void eventDispatched(AWTEvent e) {

            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                processMousePressedEvent((MouseEvent) e);
                
            } else if (e.getID() == HierarchyEvent.HIERARCHY_CHANGED) {
                processHierarchyChangedEvent((HierarchyEvent) e);
            }
        }
        
        /**
         * Processes a HierarchyEvent of type DISPLAYABILITY_CHANGED & SHOWING_CHANGED.
         * This event is dispatched when a component is added to another component that is visible
         * and parented and as a result the visibility of the added component changes.
         * If this happens to a value editor we want to refresh its display, since that means the
         * editor has been parented and made visible for the first time.
         * @param evt the hierarchy event
         */
        private void processHierarchyChangedEvent(HierarchyEvent evt) {
            
            Component changedComponent = evt.getChanged();
            
            if ((evt.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 &&
                (evt.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 &&
                 changedComponent instanceof ValueEditor && 
                 changedComponent.isShowing() &&
                 isManagingEditor((ValueEditor) changedComponent)) {
                
                final ValueEditor changedEditor = (ValueEditor) changedComponent;
                
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        changedEditor.refreshDisplay();
                    }
                });
            }
        }
        
        /**
         * Processes a MouseEvent of type MOUSE_PRESSED.
         * @param evt the mouse event
         */
        private void processMousePressedEvent(MouseEvent evt) {
            
            // Do nothing if there are no editors in the current hierarchy.
            if (currentEditor == null) {
                return;
            }

            // Figure out the "real" container on which to perform hit testing.
            Container hitTestContainer = valueEditorContainer;
            JRootPane rootPane = SwingUtilities.getRootPane(valueEditorContainer);
            if (rootPane != null) {
                if (valueEditorContainer == rootPane ||
                    valueEditorContainer == rootPane.getContentPane() ||
                    valueEditorContainer == rootPane.getGlassPane() ||
                    valueEditorContainer == rootPane.getLayeredPane()) {

                    hitTestContainer = rootPane.getParent();
                }
            }
                    
            // Ignore if the component hit isn't in the value editor container (if any).
            Component componentHit = evt.getComponent();
            if (hitTestContainer != null && !SwingUtilities.isDescendingFrom(componentHit, hitTestContainer)) {
                return;
            }
    
            // Ignore if the component descends from a popup menu (eg. from a combo box drop-down).
            if (descendsFromPopupMenu(componentHit)) {
                return;
            }
    
            // Ignore if there's a modal dialog showing, and it's not the window in which the valueEditorContainer (if any) exists.
            //  This fixes the problem with bringing up a JColorChooser from the FormatPaletteValueEditor.
            Window componentWindow = componentHit instanceof Window ? (Window) componentHit : SwingUtilities.getWindowAncestor(componentHit);
            Window containerWindow = valueEditorContainer == null ? null : SwingUtilities.getWindowAncestor(valueEditorContainer);
            if (componentWindow != containerWindow && componentWindow instanceof Dialog && ((Dialog)componentWindow).isModal()) {
                return;
            }

            // Ignore if not managing this editor.
            ValueEditor editorHit = getValueEditorForComponent(componentHit);
            if (editorHit != null && !isManagingEditor(editorHit)) {
                return;
            }
    
            if (editorHit != currentEditor) {
                activateEditor(editorHit);
            }

            // If no editor was clicked, this is considered a "hard" commit.
            if (editorHit == null) {
                notifyCommitCancelHandler(true);
            }
        }
    }
    
    /**
     * Default ValueEditorHierarchyManager constructor.
     *   Value editors will be placed in the JLayeredPane.
     * @param valueEditorManager
     */
    public ValueEditorHierarchyManager(ValueEditorManager valueEditorManager) {
        this(valueEditorManager, null);
    }
    
    /**
     * ValueEditorHierarchyManager constructor.
     * @param valueEditorManager
     * @param container the container in which value editors exist.
     * If null, value editors will be placed in the JLayeredPane of the parent frame of the top level value editor. 
     */
    public ValueEditorHierarchyManager(ValueEditorManager valueEditorManager, JComponent container) {
        this(valueEditorManager, container, true);
    }
    
    /**
     * ValueEditorHierarchyManager constructor.
     * @param valueEditorManager
     * @param container the container in which value editors exist.
     * @param useFocusListeners whether the hierarchy manager should use focus listeners to collapse
     * the top level value editors.
     * If null, value editors will be placed in the JLayeredPane of the parent frame of the top level value editor. 
     */
    public ValueEditorHierarchyManager(ValueEditorManager valueEditorManager,
                                       JComponent container,
                                       boolean useFocusListeners) {
        this.valueEditorManager = valueEditorManager;
        this.valueEditorContainer = container;
        this.useFocusListeners = useFocusListeners;

        // Only create the hierarchy listener if we want to use it to listen to focus events
        if (useFocusListeners) {
            this.hierarchyEventListener = new HierarchyEventListener();
        } else {
            this.hierarchyEventListener = null;
        }
    }

    /**
     * Set the hierarchy commit/cancel handler for this hierarchy.
     * @param newHandler the new commit/cancel handler.
     */
    public void setHierarchyCommitCancelHandler(HierarchyCommitCancelHandler newHandler) {
        this.hierarchyCommitCancelHandler = newHandler;
    }
    
    /**
     * Determine whether a given component has a JPopupMenu as an ancestor in its component hierarchy.
     * @param component the component in question
     * @return whether the component descends from a JPopupMenu
     */
    private boolean descendsFromPopupMenu(Component component) {
        for (Component testComponent = component; testComponent != null; testComponent = testComponent.getParent()) {
            if (testComponent instanceof JPopupMenu) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return whether the given editor exists in the current editor hierarchy.
     * @param editor the editor to check
     * @return boolean whether the given editor exists in the current editor hierarchy.
     */
    boolean existsInHierarchy(ValueEditor editor) {
        return editor == currentEditor || currentEditorStack.contains(editor);
    }

    /**
     * Get the child editor (if any) of a value editor in the current hierarchy.
     * @param parentValueEditor the editor whose current child will be returned.
     * @return ValueEditor the child editor.  Returns null if the parent is null,
     * has no child, or does not exist in the hierarchy.
     */
    ValueEditor getChildEditor(ValueEditor parentValueEditor) {

        if (parentValueEditor == null) {
            return null;
        }

        // Get the index of the parent in the hierarchy.
        int parentIndex = currentEditorStack.indexOf(parentValueEditor);
        if (parentIndex < 0) {
            return null;
        }

        // Get the child in the hierarchy.
        if (parentIndex == currentEditorStack.size() - 1) {
            return currentEditor;
        }
        
        return currentEditorStack.get(parentIndex + 1);
    }

    /**
     * Returns a list that contains all instances of ValueEditors that are being displayed
     * @return List
     */
    public List<ValueEditor> getTopValueEditors() {
        return new ArrayList<ValueEditor>(topEditorPanels);
    }
    
    /**
     * Adds a new top level ValueEditor to the ValueEditorHierarchy.
     * If the Hierarchy only contains this newly added ValueEditor, then this ValueEditor
     * will be the currentEditor and given focus. Note that the new editor must be parented
     * to an existing component. This is because setInitialValue will be called on the new
     * editor which may require a valid Graphics object.
     * Note: the current hierarchy will be collapsed.
     * @param newValueEditor ValueEditor
     */
    public void addTopValueEditor(ValueEditor newValueEditor) {
        // Safety check.  The newEditor should not be in a closing/closed state
        if (newValueEditor.isEditorClosing()) {
            throw new IllegalArgumentException("Cannot launch an editor that is closing");
        }
        
        topEditorPanels.add(newValueEditor);
        collapseHierarchy(null, true);
        
        // Ensure that the editor has been initialized (could change preferred size)
        // TODO: This should be called by the constructor / setValueNode() and setInitialValue()
        // should be private so that clients of the value editor system don't need to worry about it.
        newValueEditor.setInitialValue();
        
        if (topEditorPanels.size() == 1) {
            
            // install the dismiss event listener
            if (useFocusListeners) {
                Toolkit.getDefaultToolkit().addAWTEventListener(hierarchyEventListener, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.HIERARCHY_EVENT_MASK);
            }

            // newValueEditor is our first top level ValueEditor.
            activateEditor(newValueEditor);
        }
    }

    /**
     * Removes a top level ValueEditor from the ValueEditorHierarchy.
     * If the removed top level ValueEditor has descendent ValueEditors open, then these
     * descendent ValueEditors will be closed, and focus will be given to another
     * top level ValueEditor (if there is one).
     * @param removeValueEditor
     */
    private void removeTopValueEditor(ValueEditor removeValueEditor) {

        // Checking to see if the to be removed ValueEditor has descendent ValueEditors.
        ValueEditor topLevelEditor =
                (currentEditorStack.isEmpty()) ? currentEditor : currentEditorStack.get(0);

        // Check if we're removing the editor at the top of the current editor stack.
        if (topLevelEditor == removeValueEditor) {

            // Clear away any editor stack, committing data values.
            collapseHierarchy(null, true);

            // Remove the removeValueEditor from the top level panels.
            // Note: We did not remove the removeValueEditor earlier because the above
            //       commit may still need removeValueEditor to be in topEditorPanels.
            topEditorPanels.remove(removeValueEditor);

            // Activate another top level ValueEditor (if there is one)
            if (!topEditorPanels.isEmpty()) {
                activateEditor(topEditorPanels.get(0));
            }

        } else {
            // Remove the removeValueEditor from the top level panels.
            topEditorPanels.remove(removeValueEditor);
        }

        if (topEditorPanels.isEmpty()) {
            // remove the dismiss event listener
            if (useFocusListeners) {
                Toolkit.getDefaultToolkit().removeAWTEventListener(hierarchyEventListener);
            }

            // update the current editor.
            setCurrentEditor(null);
        }
    }

    /**
     * Close a given value editor and remove it from this manager.
     * @param valueEditor the value editor to close and remove from this manager.
     * @param commit if true editor is committed before it is removed, otherwise it is cancelled
     */
    public void removeValueEditor(ValueEditor valueEditor, boolean commit) {
        removeValueEditors(Collections.singletonList(valueEditor), commit);
    }

    /**
     * Close a number of value editors and remove them from this manager.
     * @param valueEditorList the value editors to remove.
     * @param commit if true editor is committed before it is removed, otherwise it is cancelled
     */
    public void removeValueEditors(List<? extends ValueEditor> valueEditorList, boolean commit) {
        Set<ValueEditor> topValueEditorSet = new HashSet<ValueEditor>(topEditorPanels);
        for (int i = 0, numPanels = valueEditorList.size(); i < numPanels; i++) {
            // Remove this panel and its descendants from the display
            ValueEditor editor = valueEditorList.get(i);
            
            if (!isManagingEditor(editor)) {
                throw new IllegalArgumentException("Attempt to remove a value editor which is not managed by this hierarchy manager.");
            }
            
            closeEditor(editor, commit);
            
            if (topValueEditorSet.contains(editor)) {
                removeTopValueEditor(editor);
                topValueEditorSet.remove(editor);
            }
        }
    }

    /**
     * If there are value editors in the value editor hierarchy, then the current editor will receive focus.
     */
    public void activateCurrentEditor() {
        activateEditor(currentEditor);
    }

    /**
     * Make a given editor the current editor, and give it focus.
     * @param editorToActivate the editor to activate, or null to clear the current editor.
     */
    public void activateEditor(ValueEditor editorToActivate) {

        if (editorToActivate != currentEditor) {
            makeCurrentEditor(editorToActivate);
        }

        // If editorToActivate is null and there are top-level editors, then makeCurrentEditor
        // will make one of the top-level editors the new current editor. In that case we don't
        // want to activate that editor. Therefore make sure the current editor is the one to activate.
        if (currentEditor != null && editorToActivate != null) {
            
            currentEditor.editorActivated();

            // Get the default focus component to request focus.
            Component focusComponent = currentEditor.getDefaultFocusComponent();
            if (focusComponent != null) {
                focusComponent.requestFocusInWindow();
            }
        }
    }

    /**
     * Sets the currentEditor.
     * @param currentEditor The currentEditor to set, or null to clear.
     */
    private void setCurrentEditor(ValueEditor currentEditor) {
        this.currentEditor = currentEditor;
    }
    
    /**
     * Morph the editor hierarchy so that the specified editor will be the current editor.
     *   Any editors which are closed will have their values commited to the parent.
     * @param editor the editor to make into the current editor, or null to collapse the current hierarchy.
     *   The hierarchy will be collapsed as appropriate, and if necessary control will be switched among sibling editors
     *     of a StructuredValueEditor.
     *   Note: the specified editor should be managed by this hierarchy manager.
     */
    private void makeCurrentEditor(ValueEditor editor) {

        ValueEditor previousCurrentEditor = currentEditor;

        if (editor != null) {
            
            ValueEditor childEditor = getChildEditor(editor);

            if (editor instanceof StructuredValueEditor && childEditor != null) {
                // Collapse to the child editor if it's a structured value editor.
                collapseHierarchy(childEditor, true);

            } else if (!existsInHierarchy(editor) && existsInHierarchy(editor.getParentValueEditor())) {
                // If the editor to activate is not in the hierarchy, but its parent is in the 
                //   hierarchy, then we are switching among the component editors of the parent.
                collapseHierarchy(getChildEditor(editor.getParentValueEditor()), true);
                setCurrentEditor(editor);

            } else if (editor != null && topEditorPanels.contains(editor)) {
                // If switching to a top-level editor (possibly not in this hierarchy), collapse all then switch.
                collapseHierarchy(null, true);
                setCurrentEditor(editor);

            } else {
                // collapse to the editor which was specified (if it's in this hierarchy).
                collapseHierarchy(editor, true);
                setCurrentEditor(editor);
            }

        } else {
            // dismiss all open editors
            collapseHierarchy(null, true);
        }
        
        // This is required to commit the following - 
        // 1) Values for top-level VEPs when clicking off their text fields.
        //   Non top-level editors commit when they close.. but top-level vep's often don't close.
        // 2) child value entry panels which don't close (eg. in an AlmostSumAbstractValueEditor) when clicking off them.
        if (previousCurrentEditor != null && previousCurrentEditor != editor && !previousCurrentEditor.isEditorClosing()) {
            previousCurrentEditor.commitValue();
        }
                    
        // Note that the hierarchy also updates the current editor on closeEditor() as necessary.
    }

    /**
     * Determine whether a given editor is managed by this hierarchy manager.
     * @param valueEditor the value editor in question
     * @return whether the editor is managed by this hierarchy manager.
     */
    private boolean isManagingEditor(ValueEditor valueEditor) {
        
        // Just find out whether the top-most editor is a member of the top editor panel list.
        ValueEditor topMostEditor = valueEditor;
        while (topMostEditor.getParentValueEditor() != null) {
            topMostEditor = topMostEditor.getParentValueEditor();
        }

        return topEditorPanels.contains(topMostEditor);
    }
    
    /**
     * Add a value editor to the current value editor hierarchy.
     * This is normally used when a value editor itself uses value editors without launching them.
     * Use launchEditor() instead, to launch an editor.
     * @param newCurrentEditor the new current value editor
     * @param parentEditor the parent of the newCurrentEditor.  
     * This will be added to the hierarchy as well, if it hasn't already been added.
     */
    public void addEditorToHierarchy(ValueEditor newCurrentEditor, ValueEditor parentEditor) {
        prepareEditor(newCurrentEditor, parentEditor);
        activateCurrentEditor();
    }

    /**
     * Launch a new value editor.
     * @param newEditor the editor to launch.
     * @param location The location to launch the newEditor, in the parent's coordinate system.
     * @param parentEditor The parent editor of the editor being launched.
     * @return boolean whether an editor was launched.  This may return false if, for instance,
     * the manager is unable to obtain a top level ancestor for the parent.
     */
    public boolean launchEditor(ValueEditor newEditor, Point location, ValueEditor parentEditor) {
        // Safety check.  The newEditor should not be in a closing/closed state
        if (newEditor.isEditorClosing()) {
            throw new IllegalArgumentException("Cannot launch an editor that is closing");
        }

        // This is a work around to prevent the null pointer exception that can sometimes arise when
        // a VEP is being used in a list or tuple editor and the "..." button has been pressed
        // to open a child editor and then the Switch Type icon is pressed without closing the child
        // editor first.  The problem can also occur when the switch type editor is opened first and
        // then the "..." button is pressed.  What happens is that the TableCellEditor (a subclass of
        // the ValueEditor) is constantly added and removed to the table while the editors are
        // opening and closing.  It seems that when the buttons/icons are pressed as described above
        // the timing is such that the cell editor has been removed from the table and as such has no
        // parent (and therefore no top level parent).  When we try to get the layered pane belonging
        // to the parent, we get a null pointer exception and the new editor is not opened.  This
        // little fix stops trying to display the new editor if it sees that there is no top level parent.
        Container parentContainer = parentEditor.getParent();
        while (parentContainer != null && !(parentContainer instanceof JFrame)) {
            parentContainer = parentContainer.getParent();
        }
        if (parentContainer == null) {
            // Reset the flag so that the switch type icon can be clicked again
            return false;
        }

        // Collapse the hierarchy to the parent - this will close any existing child editors.
        collapseHierarchy(parentEditor, true);

        // Tell the 'youngest' parent StructuredValueEditor that this element is launching a ValueEditor.
        // Note: Quite possible that there are no such 'youngest' parent.
        for (Container ancestor = parentEditor.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor instanceof StructuredValueEditor) {
                StructuredValueEditor sve = (StructuredValueEditor) ancestor;
                sve.handleElementLaunchingEditor();
                break;
            }
        }

        // Prepare the editor to be added to the hierarchy.
        prepareEditor(newEditor, parentEditor);

        // Place the value editor on the screen        
        displayValueEditor(newEditor, parentEditor, location);

        // Activate the editor.
        activateCurrentEditor();

        return true;
    }
    
    /**
     * Displays the value editor on the screen.  The new editor provided should be initialized and needs
     * to be added to a visible container so that the user can see it.
     * @param newEditor A valid, fully initialized value editor
     * @param parentEditor The parent value editor if needed.  In this implementation the frame containing
     * the parent editor will be used to place the new editor.
     * @param mouseLocation The absolute coordinates of the desired launch location.  This could come
     * from the mouse position or some similar value.  
     */
    protected void displayValueEditor(ValueEditor newEditor,
                                      ValueEditor parentEditor,
                                      Point mouseLocation) {
        // Get the parent container
        Container parentContainer = parentEditor.getParent();
        while (parentContainer != null && !(parentContainer instanceof JFrame)) {
            parentContainer = parentContainer.getParent();
        }
        
        // Add the editor to the value editor container.
        // If no editor container has been provided, use the layered pane of the parent of the top most editor.
        
        JComponent editorContainer = valueEditorContainer != null ? valueEditorContainer : 
                                     parentContainer != null ? ((JFrame)parentContainer).getLayeredPane() : 
                                     null;

        Point finalLocation = getNewEditorLocation(mouseLocation, newEditor, parentEditor, editorContainer);
        newEditor.setLocation(finalLocation);         
        
        if (editorContainer instanceof JLayeredPane) {
            
            JLayeredPane jlp = (JLayeredPane) editorContainer; 
            jlp.setLayer(newEditor, JLayeredPane.PALETTE_LAYER.intValue(), 1);
            jlp.add(newEditor);
            jlp.moveToFront(newEditor);
            jlp.revalidate();
            
        } else {
            editorContainer.add(newEditor, 0);
            editorContainer.revalidate();
        }
    }
    
    /**
     * Prepares a new editor to be added to the current hierarchy and displayed on screen.
     * @param newEditor the new editor to be added
     * @param parentEditor the parent of the new editor
     */
    private void prepareEditor(ValueEditor newEditor, ValueEditor parentEditor) {
        
        // Set the new editor characteristics.
        newEditor.setParentValueEditor(parentEditor);
        newEditor.setEditable(parentEditor.isEditable());

        // Update the editor hierarchy.
        // Do this before setting the initial value since the hierarchy might need
        // to be up to date for things that happen while setting the initial value.
        if (currentEditorStack.empty() || currentEditorStack.peek() != parentEditor) {
            
            if (currentEditorStack.contains(parentEditor)) {
                throw new IllegalStateException("parent editor of new editor is not at top of editor stack");
            }
            
            currentEditorStack.push(parentEditor);
        }        

        // Make the new editor the current editor.
        setCurrentEditor(newEditor);

        // Make sure the editors starting value is initialized.
        newEditor.setInitialValue();
    }

    /**
     * Calculates the location on screen where a new editor should appear.
     * If the editor is too large to appear at the desired location it will 
     * calculate a more appropriate location.
     */
    private Point getNewEditorLocation (Point desiredLocation, ValueEditor newEditor, ValueEditor parentEditor, JComponent editorContainer) {
    
        Point location = SwingUtilities.convertPoint(parentEditor, desiredLocation, editorContainer);
        boolean editorTooWide = false;

        // Use the visible rectangle instead of the container size, in case the
        // container is embedded in a scrollpane.
        Rectangle containerRect = editorContainer.getVisibleRect();
        
        // Check if the editor is too wide. If it is too wide move
        // it over so it fits inside its container.
        if (location.x + newEditor.getWidth() > containerRect.x + containerRect.width) {

            editorTooWide = true;

            int relativeOffset = parentEditor.getWidth() - desiredLocation.x;
            
            int newX = location.x
                        - newEditor.getWidth()
                        + relativeOffset;

            location.x = newX > 0 ? newX : 0;
            location.y += newEditor.getInsets().top;

            // If we move the editor to the left also move it
            // up so it doesn't cover its parent editor
            location.y += parentEditor.getHeight();
        }

        // Now check if the editor is too long. If it is too long
        // move it up so that it fits inside its container.
        if (location.y + newEditor.getHeight() > containerRect.y + containerRect.height) {

            int newY = location.y
                        - newEditor.getHeight()
                        + newEditor.getInsets().top
                        + parentEditor.getHeight();

            // If the editor is too wide make sure it stays
            // moved up above the parent editor so it doesn't
            // cover it's parent.
            if (editorTooWide) {
                newY -= 2 * parentEditor.getHeight();
                newY -= newEditor.getInsets().top;
            }

            location.y = newY > 0 ? newY : 0;
        }
        
        return location;
    }
    
    /**
     * Get the value editor which contains a given component
     * @param component the component to check.
     * @return ValueEditor the closest value editor ancestor to the given component,
     *   If the component is a value editor, returns the component.
     *   Returns null if there is no value editor ancestor or if component is null.
     */
    private static ValueEditor getValueEditorForComponent(Component component) {
        for (Component parent = component; parent != null; parent = parent.getParent()) {
            if (parent instanceof ValueEditor) {
                return (ValueEditor)parent;
            }
        }
        return null;
    }
    
    /**
     * Collapses the hierarchy to the active top-level editor and makes sure that all editors,
     * including the top-level editor, have their values committed. This is different from collapseHierarchy,
     * since that method only collapses to the given editor, but does not commit it.
     * @param commit whether to commit the editors
     */
    public void commitHierarchy(boolean commit) {
        
        collapseHierarchy(null, commit);

        // Make sure the top-level editor is committed too.            
        if (currentEditor != null) {
            if (commit) {
                currentEditor.handleCommitGesture();
            } else {
                currentEditor.handleCancelGesture();
            }
        }
    }
    
    /**
     * Collapse the current editor stack to a given editor.  This calls closeEditor() in turn on 
     *   the editors to be closed.  Focus will not be directly modified by this method.
     *   Note: clients will have to refreshDisplay() if stale data is currently displayed.
     * @param targetEditor collapsing will end at this editor.  
     *   If this is null, the current hierarchy will be completely collapsed to its top-level editor.
     *   If the target editor is not on the stack, no collapsing will take place.
     * @param commit Whether to commit values on collapse.
     */
    public void collapseHierarchy(ValueEditor targetEditor, boolean commit) {
        
        if (currentEditorStack.empty()) {
            return;
        }

        // Ignore if the target editor is not in the editor stack.
        if (targetEditor != null && !(currentEditorStack.contains(targetEditor))) {
            return;
        }
        
        if (targetEditor == null) {
            targetEditor = currentEditorStack.get(0);
        }

        // Collapse to the child editor if it's a structured value editor.
        ValueEditor childEditor = getChildEditor(targetEditor);
        if (targetEditor instanceof StructuredValueEditor && childEditor != null) {
            collapseHierarchy(childEditor, commit);
            return;
        }

        while (!currentEditorStack.empty()) {
            
            // As we close editors the current editor will be updated.
            if (currentEditor == targetEditor) {
                break;
            }
            
            if (currentEditor.isEditorClosing()) {
                // HACK: fixes a bug where you instantiate the first element of a pair, and commit -> calls this twice..
                return;
                // throw new IllegalStateException("Attempt to close an editor that is already closing");
            }
            
            closeEditor(currentEditor, commit);
        }
    }
    
    /**
     * Removes a ValueEditor from display.  Any children will be closed as well.
     * Note: this will not update the top value editors.  removeValueEditor(s) () should be called instead if this is desired.
     * @param editorToClose the editor to close.
     * @param commit whether to commit the currently entered value.
     */
    private void closeEditor(ValueEditor editorToClose, boolean commit) {
        
        if (currentEditorStack.contains(editorToClose)) {
            // Close child editors first.
            collapseHierarchy(editorToClose.getParentValueEditor(), commit);
            
            // Collapsing to parent will would call close on this editor, unless parent is null.
            if (editorToClose.getParentValueEditor() != null) {
                return;
            }
            
            // If this is a structured value editor, the child editor will not have been closed on collapse.
            //   Call close on the child editor, then go on to close this editor.
            ValueEditor childEditor = getChildEditor(editorToClose);
            if (childEditor != null) {
                closeEditor(childEditor, commit);
            }
        } 

        // check for editor closing: fixes a bug where changing the type of a parametric value in the scope causes an infinite loop
        if (editorToClose.isEditorClosing()) {
            return;
        }

        // Indicate that this editor is closing
        editorToClose.setEditorIsClosing(true);

        // Commit or cancel the edit.
        if (commit && editorToClose.isEditable()) {
            editorToClose.commitValue();
        } else {
            editorToClose.cancelValue();
        }

        // Remove this value editor.
        Container parentContainer = editorToClose.getParent();
        if (parentContainer != null) {
            parentContainer.remove(editorToClose);
        }
        if (parentContainer instanceof JComponent) {
            ((JComponent)parentContainer).repaint(editorToClose.getBounds());
        } else if (parentContainer != null) {
            parentContainer.validate();
        }

        // handle the editor being closed..
        handleEditorClosed(editorToClose);
    }

    /**
     * Update the active editor, current editor, and editor stack when an editor is closed.
     * @param closedEditor the editor that closed.
     */
    private void handleEditorClosed(ValueEditor closedEditor) {

        if (closedEditor == currentEditor) {
            
            // Update the hierarchy - the current editor and the editor stack.
            setCurrentEditor(currentEditorStack.isEmpty() ? null : currentEditorStack.pop());
            
            if (currentEditor != null) {
                currentEditor.refreshDisplay();
            }
            
        } else if (currentEditorStack.contains(closedEditor)) {
            throw new IllegalStateException("Child editors must close before their parents.");

        } else {
            // An editor was closed that is not in the editor hierarchy.
            // Do nothing.
        }
    }

    /**
     * Notify the hierarchy manager that the editor in question has received a "commit" request.
     *   Examples include pressing the <ENTER> key or pressing an "OK" button.
     * This method will handle closing the editor and committing the value as necessary.
     * @param editor the editor which received the request.
     */
    void handleCommitGesture(ValueEditor editor) {

        ValueEditor parentEditor = editor.getParentValueEditor();
        ValueEditor editorToCommit = parentEditor instanceof StructuredValueEditor ? parentEditor : editor;

        if (editorToCommit.getParentValueEditor() != null) {
            closeEditor(editorToCommit, true);

        } else {
            editorToCommit.commitValue();   // top-level editor - don't close
            notifyCommitCancelHandler(true);
        }

        activateCurrentEditor();
    }

    /**
     * Notify the hierarchy manager that the editor in question has received a "cancel" request.
     *   Examples include pressing the <ESC> key or pressing an "Cancel" button.
     * This method will handle closing the editor and canceling the value as necessary.
     * @param editor the editor which received the request.
     */
    void handleCancelGesture(ValueEditor editor) {

        ValueEditor parentEditor = editor.getParentValueEditor();
        ValueEditor editorToCancel = parentEditor instanceof StructuredValueEditor ? parentEditor : editor;

        if (editorToCancel.getParentValueEditor() != null) {
            closeEditor(editorToCancel, false);

        } else {
            editorToCancel.cancelValue();   // top-level editor - don't close
            notifyCommitCancelHandler(false);
        }
        
        activateCurrentEditor();
    }
    
    /**
     * Notify the hierarchy commit/cancel handler that a hard commit/cancel event has taken place.
     * @param commit whether commit or cancel happened.  true = commit, false = cancel.
     */
    private void notifyCommitCancelHandler(boolean commit) {
        if (hierarchyCommitCancelHandler != null) {
            hierarchyCommitCancelHandler.handleHierarchyCommitCancel(commit);
        }
    }
    
    /**
     * Returns the ValueEditorManager that this Hierarchy Manager is using
     * @return ValueEditorManager
     */
    public ValueEditorManager getValueEditorManager() {
        return valueEditorManager;
    }
}