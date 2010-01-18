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
 * ValueEditor.java
 * Created: Jan 13, 2001
 * By: Luke Evans
 */

package org.openquark.gems.client.valueentry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.utilities.SmoothHighlightBorder;


/**
 * The abstract base class for all ValueEditors.
 * Creation date: (1/13/2001 12:20:34 AM)
 * @author Luke Evans
 */
public abstract class ValueEditor extends JPanel {

    /** The parent ValueEditor associated with this ValueEditor. */
    private ValueEditor parentValueEditor = null;

    /** Every ValueEditor has a corresponding ValueNode for its data. */
    private ValueNode valueNode = null;
    
    /** the ValueNode that this ValueEditor is modifying */
    private ValueNode ownerValueNode = null;

    /** The manager for this value editor. */
    protected final ValueEditorManager valueEditorManager;
    
    /** The manager of the hierarchy that this valueEditor resides in */
    protected final ValueEditorHierarchyManager valueEditorHierarchyManager;

    /** The context for this ValueEditor. */
    private ValueEditorContext context = null;

    /** The List containing the ValueEditorListeners. */
    private final List<ValueEditorListener> listenerList;

    /** Indicates whether this ValueEditor and its children are editable. */
    private boolean editable = true;
    
    /** Indicates whether this editor is resizable. */
    private boolean resizable = false;

    /** Indicates whether this editor is moveable. */
    private boolean moveable = false;
    
    /** The maximum size to which this editor may be resized. */
    private Dimension maxResizeDimension = null;
    
    /** The minimum size to which this editor may be resized. */
    private Dimension minResizeDimension = null;
    
    /**
     * This is a bit of a workaround to the problem that when a component is "removed" from a container
     * and it has focus, an attempt is made to move focus to the next focusable component in the 
     * container's focus cycle.  The fix involves setting a focus policy for the TableTop that will
     * pretend the is no "next focusable component" when it sees that the component being removed
     * belongs to a Value Editor that is closing.
     * 
     * A flag to indicate if this editor is in the process of closing.  
     */
    private boolean editorIsClosing = false;

    /**
     * KeyListener used to listen for user's commit (Enter) and cancel (Esc) input.
     * Add it to components of ValueEditors when you want the above functionality.
     */
    public class ValueEditorKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {

            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                handleCommitGesture();
                evt.consume(); // Don't want the control with the focus to perform its action.

            } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                evt.consume();
            }
        }
    }

    /**
     * Contains info regarding the ValueEditors associated with a particular ValueNode.
     * Currently, it has the size data of the ValueEditors using a particular ValueNode.
     */
    public static class Info {

        private final Dimension editorSize;
        private final List<Integer> componentWidths;

        /**
         * Constructor for a ValueEditor.Info with size only.
         * @param size the size of the ValueEditor.
         */
        public Info(Dimension size) {
            this(size, null);
        }
        
        /**
         * Constructor for a ValueEditor.Info with size and component widths.
         * @param editorSize the size of the ValueEditor.
         * @param componentWidths the widths of components of the value editor, or null if none.
         */
        public Info(Dimension editorSize, List<Integer> componentWidths) {
            this.editorSize = new Dimension(editorSize);
            this.componentWidths = componentWidths == null ? null : new ArrayList<Integer>(componentWidths);
        }
        
        /**
         * @return the size of the ValueEditor.
         */
        public Dimension getEditorSize() {
            return new Dimension(editorSize);
        }
        
        /**
         * @return the widths of components of the value editor, or null if none.
         */
        public List<Integer> getComponentWidths() {
            return componentWidths == null ? null : Collections.unmodifiableList(componentWidths);
        }

    }

    /**
     * Creates a new ValueEditor.
     * @param valueEditorHierarchyManager
     */
    protected ValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        
        if (valueEditorHierarchyManager == null) {
            throw new NullPointerException("valueEditorHierarchyManager must not be null.");
        }
        
        this.valueEditorHierarchyManager = valueEditorHierarchyManager;
        this.valueEditorManager = valueEditorHierarchyManager.getValueEditorManager();

        // Use a synchronized list.
        listenerList = new Vector<ValueEditorListener>();
        
        if (valueEditorManager.useTypeColour()) {
            setOpaque(false);
            setBackground(getBackground());
        }
        
        setBorder(valueEditorManager.getValueEditorBorder(this));
        
        MouseInputAdapter resizeListener = new ValueEditorResizeMouseListener(this);
        addMouseListener(resizeListener);
        addMouseMotionListener(resizeListener);
    }
    
    /**
     * Get the component which by default has focus.
     * This will be called, for instance, when the editor is activated
     * @return Component the default component to receive focus, or null if none.
     */
    public abstract Component getDefaultFocusComponent();

    /**
     * Notify the value editor that it has been activated.
     * Some value editors may want to perform some setup, such as enabling/disabling buttons.
     * This function is called if the editor is activated - either as a result of the hierarchy collapsing
     * to it or because the user explicitly clicks on it.
     */
    public void editorActivated() {
    }

    /**
     * Commit the value node currently under edit in this editor.
     */
    protected void commitValue() {
        // default: do nothing (valuenode is already set).
        notifyValueCommitted();
    }

    /**
     * Cancel the edit of the value node currently under edit in this editor.
     */
    protected void cancelValue() {
        // default: do nothing.
        notifyValueCanceled();
    }
    
    /**
     * Notify the hierarchy manager that this editor has received a "commit" request.
     *   Examples include pressing the <ENTER> key or pressing an "OK" button.
     * This method will handle closing the editor and committing the value as necessary.
     */
    protected void handleCommitGesture() {
        valueEditorHierarchyManager.handleCommitGesture(this);
    }

    /**
     * Notify the hierarchy manager that this editor received a "cancel" request.
     *   Examples include pressing the <ESC> key or pressing an "Cancel" button.
     * This method will handle closing the editor and canceling the value as necessary.
     */
    protected void handleCancelGesture() {
        valueEditorHierarchyManager.handleCancelGesture(this);
    }

    /**
     * Makes a copy of the ValueNode in this ValueEditor to the clip board.
     */
    public void copyToClipboard() {
        valueEditorManager.copyToClipboard(getValueNode());
    }

    /**
     * Makes a copy of the ValueNode in this ValueEditor to the clipboard.
     * Also erases the value in this ValueEditor (values are defaulted).
     */
    public void cutToClipboard() {

        copyToClipboard();

        ValueNode newVN = valueEditorManager.getValueNodeBuilderHelper().getValueNodeForTypeExpr(getValueNode().getTypeExpr());
        setOwnerValueNode(newVN);
        setInitialValue();
        revalidate();
        valueEditorHierarchyManager.activateCurrentEditor();
    }

    /**
     * If there is a value in the clipboard and that value matches the data type
     * in this ValueEditor, then sets the value in this ValueEditor to the value
     * in the clipboard.
     */
    public void pasteFromClipboard() {

        ValueNode clipboardValueNode = valueEditorManager.getClipboardValue();

        // First, need to place check for 'compatible' types.
        // Also, if there's no clipboard value, then no point going on.
        if ((clipboardValueNode == null) || !clipboardValueNode.getTypeExpr().sameType(getValueNode().getTypeExpr())) {
            // Type incompatible.  Do nothing.
            return;
        }

        setOwnerValueNode(clipboardValueNode);
        setInitialValue();
        revalidate();
        valueEditorHierarchyManager.activateCurrentEditor();
    }

    /**
     * Returns the parent editor (the editor which spawned this editor) if any.
     * Creation date: (03/14/02 12:25:53 PM)
     * @return ValueEditor the parent value editor, or null if none.
     */
    public final ValueEditor getParentValueEditor() {
        return parentValueEditor;
    }

    /**
     * Returns the ValueNode for this ValueEditor.
     * @return ValueNode
     */
    public final ValueNode getValueNode() {
        return valueNode;
    }

    /**
     * Checks whether or not this ValueEditor (or its parts [Eg: textfield, button]) has focus. 
     * @return boolean True if this ValueEditor(or its parts) has focus.  False otherwise.
     */
    public boolean hasOverallFocus() {
                
        // Traverse up the component hierarchy from the current focus owner.
        // Note that we can't simply stop at the first ValueEditor, since a value editor
        //   may be composed of other value editors.
        
        for (Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
             focusOwner != null;
             focusOwner = focusOwner.getParent()) {

            if (focusOwner == ValueEditor.this) {
                return true;
            }
        }
        
        return false;
        
    }
    
    
    /**
     * Returns whether this editor or any of its childrens holds the focus.
     * @return boolean
     */
    public boolean childrenHasFocus() {
        ValueEditor child = valueEditorHierarchyManager.getChildEditor(this);
        if (hasOverallFocus()) {
            return true;
        }
        
        if (child != null) {
            return child.childrenHasFocus();
        }
        
        return false;
    }

    /**
     * Returns True if this editor is in the process of closing and False otherwise.
     * @return boolean
     */
    public boolean isEditorClosing() {
        return editorIsClosing;
    }

    /**
     * Notify this editor that it is about to be closed.  This function simply sets the 
     * editorIsClosing member variable to the specified boolean value.
     * TODO: reduce scope.
     * todoFW: remove this -- there is a better way (and I've found it) !
     * @param isClosing boolean should be True if the editor is closing and False otherwise.
     */
    public void setEditorIsClosing(boolean isClosing) {
        editorIsClosing = isClosing;
    }
    
    /**
     * Refreshes the ValueEditor to display the latest data.
     * Note: The default implementation is to do nothing.
     * All subclasses that need this will have to override this method.
     * This method is called if the editor that is in the hierarchy below this editor is closed
     * and this editor in turn becomes the current editor. It is also called when an editor that was
     * previously not showing on screen is now shown on screen.
     */
    public void refreshDisplay() {
    }

    /**
     * Replaces the current valueNode with newValueNode.
     * Notice you are replacing the current working copy of the valueNode, not the ownerValueNode
     * @param newValueNode 
     * @param preserveInfo 
     */
    public void replaceValueNode(ValueNode newValueNode, boolean preserveInfo) {

        if (preserveInfo) {
            // Put in new entry (null info preserves nothing..).
            Info info = valueEditorManager.getInfo(this.valueNode);
            valueEditorManager.associateInfo(newValueNode, info);
        }

        this.valueNode = newValueNode;
    }

    /**
     * Change the value represented by this value editor.
     *   This value editor must be of the correct type to edit the new value.
     * @param newValue the new owner value node to be represented.
     */
    public final void changeOwnerValue(ValueNode newValue) {
        // Now set the data as appropriate
        setOwnerValueNode(newValue);
        setInitialValue();
        
        revalidate();
    }

    /**
     * Sets the parentValueEditor for this ValueEditor.
     * Creation date: (03/14/2002 12:52:45 PM)
     * @param newParentValueEditor ValueEditor The new parent of this ValueEditor.  
     */
    final public void setParentValueEditor(ValueEditor newParentValueEditor) {
        parentValueEditor = newParentValueEditor;
    }

    /**
     * Call this method to make the ValueEditor initialize its values.
     * Note: Usually call this method after adding the ValueEditor to the display, setting its ValueNode,
     * and correctly updating the currentEditor and the currentEditorStack, since some
     * implementations of this method will require that pre-condition.
     */
    abstract public void setInitialValue();

    /**
     * Sets the ownerValueNode for this ValueEditor.
     * Also initializes the background/border colour of this ValueEditor.
     * @param newValueNode 
     */
    public void setOwnerValueNode(ValueNode newValueNode) {
        
        this.ownerValueNode = newValueNode;
        this.valueNode = newValueNode.copyValueNode();

        if (getBorder() instanceof SmoothHighlightBorder) {
            Color typeColor = valueEditorManager.getTypeColour(valueNode.getTypeExpr());
            setBorder(new SmoothHighlightBorder(typeColor, moveable));
        }
    }

    /**
     * Get the context for this value editor.
     * @return ValueEditorContext
     */
    public ValueEditorContext getContext() {
        
        // If there's no context yet, just create an immutable context.
        if (context == null) {
            context = new ValueEditorContext() {
                public TypeExpr getLeastConstrainedTypeExpr() {
                    return getValueNode().getTypeExpr();
                }
            };
        }
        return context;
    }

    /**
     * Set the context for this ValueEditor.
     * @param context
     */
    public void setContext(ValueEditorContext context) {
        this.context = context;
    }

    /**
     * Set the editable state for the ValueEditor.
     * Normally a value editor is always editable.  However, in some
     * cases we may want a non-editable value editor.  For example:
     * A ValueEntryPanel containing a large a textual result would be too
     * small to show all the text and there is no provision for scrolling.
     * The user can call up the StringValueEditor, which does scroll, in
     * order to more easily read all the text.  But we don't want them 
     * trying to edit the result.
     */
    public void setEditable(boolean b) {
        editable = b;
    }

    /**
     * Return whether this ValueEditor is editable, based on the editable flags for this Value Editor and
     * its ancestors (if any).
     * @return boolean whether or not this ValueEditor is editable.
     */
    public boolean isEditable() {
        // Not switchable if this or any ancestors are ValueEditors which aren't editable
        for (ValueEditor editor = this; editor != null; editor = editor.getParentValueEditor()) {
            if (!editor.editable) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Overide the setSize() method so that in addition to setting the editor size it will also 
     * make sure the editor doesn't resize beyond the bounds of its parent.
     * @param width the new width of the editor
     * @param height the new height of the editor
     */
    @Override
    public void setSize(int width, int height) {

        Component parent = getParent();
        
        if (parent != null && parent.getWidth() > 0 && parent.getHeight() > 0) {
            
            Point location = getLocation();
            Rectangle parentRect = new Rectangle(parent.getWidth(), parent.getHeight());
            
            int x = getX();
            int y = getY();
            
            if (parent instanceof JComponent) {
                parentRect = ((JComponent) parent).getVisibleRect();
            }
            
            if (location.x + width > parentRect.x + parentRect.width) {
                
                // Check if we can move editor to the left.
                int diff = location.x + width - parentRect.x - parentRect.width;
                x = getX() - diff;
                
                if (x < parentRect.x) {
                    width -= diff - parentRect.x + x;
                    x = parentRect.x;
                }
            }
            
            if (location.y + height > parentRect.y + parentRect.height) {
                
                // Check if we can move editor up.
                int diff = location.y + height - parentRect.y - parentRect.height;
                y = getY() - diff;
                
                if (y < parentRect.y) {
                    height -= diff - parentRect.y + y;
                    y = parentRect.y;
                }
            }
            
            setLocation(x, y);
        }

        super.setSize(width, height);
    }
    
    /**
     * @see #setSize(int, int)
     */
    @Override
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }
    
    /**
     * Overrides setLocation to ensure that the editor is completely within the bounds of its parent.
     * If the new location places it outside parent bounds, then the location will be adjusted.
     * @param x the new x location
     * @param y the new y location
     */
    @Override
    public void setLocation(int x, int y) {
        
        Component parent = getParent();
        
        if (parent != null && parent.getWidth() > 0 && parent.getHeight() > 0) {
            
            Rectangle parentRect = new Rectangle(parent.getWidth(), parent.getHeight());
            
            x = ValueEditorManager.clamp(parentRect.x, x, parentRect.x + parentRect.width - getWidth());
            y = ValueEditorManager.clamp(parentRect.y, y, parentRect.y + parentRect.height - getHeight());
        }
        
        super.setLocation(x, y);
    }
    
    /**
     * @see #setLocation(int, int)
     */
    @Override
    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    /**
     * Get the hierarchy manager managing this value editor.
     * @return ValueEditorHierarchyManager
     */
    public ValueEditorHierarchyManager getValueEditorHierarchyManager() {
        return valueEditorHierarchyManager;
    }

    /**
     * Returns the ownerValueNode
     * @return ValueNode
     */
    public ValueNode getOwnerValueNode() {
        return ownerValueNode;
    }

    /**
     * Adds a listener to listen for data value changed events.
     * Note: Currently, the def'n of a "Data value change" is if the old String value in the ValueNode differs
     * with the new String value in the ValueNode.
     * Note: Will do nothing if listener is null.
     * @param listener
     */
    public void addValueEditorListener(ValueEditorListener listener) {
        if (listener != null) {
            listenerList.add(listener);
        }
    }
    
    /**
     * Removes the listener from the list of listeners to be notified upon a ValueEditorEvent.
     * @param listener 
     */
    public void removeValueEditorListener(ValueEditorListener listener) {
        listenerList.remove(listener);
    }
    
    /**
     * Notify listeners that the value of the edit in progress has been committed.
     */
    protected void notifyValueCommitted() {
        ValueNode oldValue = getOwnerValueNode();
        for (int i = 0, listenerCount = listenerList.size(); i < listenerCount; i++) {
            ValueEditorListener listener = listenerList.get(i);
            listener.valueCommitted(new ValueEditorEvent(this, oldValue));
        }
    }

    /**
     * Notify listeners that the edit in progress has been canceled.
     */
    protected void notifyValueCanceled() {
        for (int i = 0, listenerCount = listenerList.size(); i < listenerCount; i++) {
            ValueEditorListener listener = listenerList.get(i);
            listener.valueCanceled(new ValueEditorEvent(this, null));
        }
    }

    /**
     * Notify listeners that the value of the edit in progress has changed.
     */
    protected void notifyValueChanged(ValueNode oldValueNode) {
        for (int i = 0, listenerCount = listenerList.size(); i < listenerCount; i++) {
            ValueEditorListener listener = listenerList.get(i);
            listener.valueChanged(new ValueEditorEvent(this, oldValueNode));
        }
    }
    
    /**
     * We override this to now draw in a background beneath the border, so that it
     * can be partly transparent.
     * @param g the graphics object to draw with
     */
    @Override
    public void paintComponent(Graphics g) {
        Insets insets = getInsets();
        g.setColor(getBackground());
        g.fillRect(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);
    }
    
    /**
     * Sets whether the editor is moveable. If it is moveable, then the border will be
     * drawn with a little title bar (if the editor is using the default editor border).
     * @param moveable whether or not this editor should be moveable
     */
    public void setMoveable(boolean moveable) {

        this.moveable = moveable;
        
        if (getBorder() instanceof SmoothHighlightBorder) {
            
            Color borderColor = getBackground();
            
            if (valueNode != null) {
                borderColor = valueEditorManager.getTypeColour(valueNode.getTypeExpr());
            }

            setBorder(new SmoothHighlightBorder(borderColor, moveable));
        }
    }
    
    /**
     * @return whether or not this editor is moveable
     */
    public boolean isMoveable() {
        return moveable;
    }
    
    /**
     * @param resizable whether or not this editor should be resizable
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }
    
    /**
     * @return whether or not this editor is resizable
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * This method is called if the user manually resizes the value editor.
     * Subclasses can implement this to perform special actions they may require.
     */
    protected void userHasResized() {
    }
    
    /**
     * Sets the maximum size to which an editor may be resized. We use this instead
     * of Swing's setMaximumSize so that we don't interfere with the managed layout
     * of ValueEditors in the DataInspector.
     * @param maxResizeDimension the maximum size to which the editor can be resized
     */
    public void setMaxResizeDimension(Dimension maxResizeDimension) {
        this.maxResizeDimension = maxResizeDimension;
    }

    /**
     * Sets the minimum size to which an editor may be resized. We use this instead
     * of Swing's setMinimumSize so that we don't interfere with the managed layout
     * of ValueEditors in the DataInspector.
     * @param minResizeDimension the minimum size to which the editor can be resized
     */
    public void setMinResizeDimension(Dimension minResizeDimension) {
        this.minResizeDimension = minResizeDimension;
    }
    
    /**
     * @return the maximum size to which the editor can be resized
     */
    protected Dimension getMaxResizeDimension() {
        return maxResizeDimension;
    }
    
    /**
     * @return the minimum size to which the editor can be resized
     */    
    protected Dimension getMinResizeDimension() {
        return minResizeDimension;
    }
}
