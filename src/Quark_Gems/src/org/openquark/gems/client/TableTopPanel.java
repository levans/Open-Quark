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
 * TableTopPanel.java
 * Creation date: Dec 18th 2002
 * By: Ken Wong
 */
package org.openquark.gems.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.DisplayedGem.DisplayedPart;
import org.openquark.gems.client.DisplayedGem.DisplayedPartBody;
import org.openquark.gems.client.DisplayedGem.DisplayedPartConnectable;
import org.openquark.gems.client.DisplayedGem.DisplayedPartInput;
import org.openquark.gems.client.DisplayedGem.DisplayedPartOutput;
import org.openquark.gems.client.Gem.PartConnectable;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.GemCutter.GUIState;
import org.openquark.gems.client.utilities.ExtendedUndoManager;
import org.openquark.gems.client.utilities.ExtendedUndoableEditSupport;
import org.openquark.gems.client.utilities.MouseClickDragListener;
import org.openquark.gems.client.utilities.MouseClickDragListener.DragMode;
import org.openquark.gems.client.valueentry.ValueEditorAdapter;
import org.openquark.gems.client.valueentry.ValueEditorContext;
import org.openquark.gems.client.valueentry.ValueEditorEvent;
import org.openquark.gems.client.valueentry.ValueEditorHierarchyManager;
import org.openquark.gems.client.valueentry.ValueEditorManager;
import org.openquark.gems.client.valueentry.ValueEntryPanel;
import org.openquark.util.UnsafeCast;
import org.openquark.util.ui.UIUtilities;


/**
 * This class represents the JPanel that is responsible for displaying the contents on the tabletop. 
 * Most of the work is done in the TableTop class, and so this class serves primarily as a UI class.
 * 
 * Note: many functions in this class were moved from the TableTop class.
 * 
 * @author Ken Wong
 */
public class TableTopPanel extends JLayeredPane implements Scrollable {
    
    /*
     * Static members -------------------------------------------------------
     */
    
    private static final long serialVersionUID = 3138348301033889627L;

    /** Whether the system supports drag images natively (otherwise we must render our own). */
    static final boolean DRAG_IMAGE_SUPPORTED = DragSource.isDragImageSupported();
    
    /** The minimum horizontal spacing between gems that are placed with the automatic placement algorithm. */
    static final int SEARCH_HORIZONTAL_SPACING = 8;

    /*
     * Cursors and images
     */
    static final ImageIcon blankImageIconSmall;
    static final ImageIcon burnImageIconSmall;
    static final ImageIcon burnNoParkImageIconSmall;
    static final ImageIcon burnQuestionImageIconSmall;
    static final ImageIcon connectImageIconSmall;
    static final ImageIcon connectNoParkImageIconSmall;
    static final ImageIcon collectorImageIconSmall;
    static final ImageIcon emitterImageIconSmall;
    static final ImageIcon reflectorImageIconSmall;
    static final ImageIcon valueGemImageIconSmall;

    public static final Cursor burnCursor;
    public static final Cursor burnNoParkCursor;
    public static final Cursor burnQuestionCursor;
    public static final Cursor cloneGemCursor;
    public static final Cursor connectCursor;
    public static final Cursor connectNoParkCursor;
    
    // Initialize the small images and the cursors
    static {
        // First get some useful reference objects.
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension bestsize = tk.getBestCursorSize(32,32);
        
        // Get the images.  Use ImageIcon to ensure that they're loaded.
        ImageIcon burnImageIcon          = new ImageIcon(TableTop.class.getResource("/Resources/cursorBurn.gif"));
        ImageIcon burnNoParkImageIcon    = new ImageIcon(TableTop.class.getResource("/Resources/cursorBurnNoPark.gif"));
        ImageIcon burnQuestionImageIcon  = new ImageIcon(TableTop.class.getResource("/Resources/cursorBurnQuestion.gif"));
        ImageIcon cloneCursorImageIcon   = new ImageIcon(GemCutter.class.getResource("/Resources/cursorCloneGem.gif"));
        ImageIcon connectImageIcon       = new ImageIcon(GemCutter.class.getResource("/Resources/cursorConnect.gif"));
        ImageIcon connectNoParkImageIcon = new ImageIcon(GemCutter.class.getResource("/Resources/cursorConnectNoPark.gif"));
        ImageIcon collectorImageIcon     = new ImageIcon(GemCutter.class.getResource("/Resources/collector.gif"));
        ImageIcon emitterImageIcon       = new ImageIcon(GemCutter.class.getResource("/Resources/emitter.gif"));
        ImageIcon reflectorImageIcon     = new ImageIcon(GemCutter.class.getResource("/Resources/reflector.gif"));
        ImageIcon valueGemImageIcon      = new ImageIcon(GemCutter.class.getResource("/Resources/constant.gif"));
        
        System.setProperty("gemcutter.photolook", "true");
        
        // These small image icons are used by Intellicut
        int smallWidth = 12;
        int smallHeight = 12;
        blankImageIconSmall         = new ImageIcon(new BufferedImage(smallWidth, smallHeight, BufferedImage.TYPE_INT_ARGB));
        burnImageIconSmall          = new ImageIcon(burnImageIcon.getImage().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH));
        burnNoParkImageIconSmall    = new ImageIcon(burnNoParkImageIcon.getImage().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH));
        burnQuestionImageIconSmall  = new ImageIcon(burnQuestionImageIcon.getImage().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH));
        connectImageIconSmall       = new ImageIcon(connectImageIcon.getImage().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH));
        connectNoParkImageIconSmall = new ImageIcon(connectNoParkImageIcon.getImage().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH));
        collectorImageIconSmall     = new ImageIcon(collectorImageIcon.getImage().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH));
        emitterImageIconSmall       = new ImageIcon(UIUtilities.cropImage(UIUtilities.shiftImage(new ImageIcon(  emitterImageIcon.getImage().getScaledInstance(smallWidth - 1, smallHeight - 1, Image.SCALE_SMOOTH)).getImage(), 2, 3), 1, 0, 0, 2));
        reflectorImageIconSmall     = new ImageIcon(UIUtilities.cropImage(UIUtilities.shiftImage(new ImageIcon(reflectorImageIcon.getImage().getScaledInstance(smallWidth - 1, smallHeight - 2, Image.SCALE_SMOOTH)).getImage(), 1, 3), 0, 0, 0, 1));
        valueGemImageIconSmall      = new ImageIcon(valueGemImageIcon.getImage().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH));

        // Do we support custom cursors?
        if (bestsize.width > 15) {
            // declare the hotspot for the cursor, some useful constants. 
            // hotSpot values must be less than the Dimension returned by getBestCursorSize
            Point burnHotSpot = new Point(8, 15);
            Point connectHotSpot = new Point(1, 1);
            
            // Scale the images to the cursor size.
            BufferedImage scaledBurnCursorImage = GemCutterPaintHelper.getResizedImage(burnImageIcon.getImage(), bestsize);
            BufferedImage scaledBurnNoParkCursorImage = GemCutterPaintHelper.getResizedImage(burnNoParkImageIcon.getImage(), bestsize);
            BufferedImage scaledCloneCursorImage = GemCutterPaintHelper.getResizedImage(cloneCursorImageIcon.getImage(), bestsize);
            BufferedImage scaledBurnQuestionCursorImage = GemCutterPaintHelper.getResizedImage(burnQuestionImageIcon.getImage(), bestsize);
            BufferedImage scaledConnectCursorImage = GemCutterPaintHelper.getResizedImage(connectImageIcon.getImage(), bestsize);
            BufferedImage scaledConnectNoParkCursorImage = GemCutterPaintHelper.getResizedImage(connectNoParkImageIcon.getImage(), bestsize);

            // define the cursors
            burnCursor = tk.createCustomCursor(scaledBurnCursorImage, burnHotSpot, "BurnCursor");
            burnNoParkCursor = tk.createCustomCursor(scaledBurnNoParkCursorImage, burnHotSpot, "BurnNoParkCursor");
            burnQuestionCursor = tk.createCustomCursor(scaledBurnQuestionCursorImage, burnHotSpot, "BurnQuestionCursor");
            cloneGemCursor = tk.createCustomCursor(scaledCloneCursorImage, connectHotSpot, "CloneGemCursor");
            connectCursor = tk.createCustomCursor(scaledConnectCursorImage, connectHotSpot, "ConnectCursor");
            connectNoParkCursor = tk.createCustomCursor(scaledConnectNoParkCursorImage, connectHotSpot, "ConnectNoParkCursor");
            
        } else {
            // Platform don't support custom cursors.
            // Linux is probably the only platform that doesn't support custom cursors.
            burnCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            burnNoParkCursor = DragSource.DefaultLinkNoDrop;
            burnQuestionCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            cloneGemCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            connectCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            connectNoParkCursor = DragSource.DefaultLinkNoDrop;
        }
    }
    
    /** Our own reference to gemCutter. */
    private final GemCutter gemCutter;
  
    /** Our own reference to tableTop. */
    private final TableTop tableTop;
    
    /** The handler for mouse events in edit mode. */
    private final TableTopMouseHandler tableTopMouseHandler;
    
    /** The handler for mouse event in run mode. */
    private final RunModeMouseHandler runModeMouseHandler;

    /** The painter for the TableTop. */
    private final TableTopGemPainter gemPainter;
    
    /**Map from value gem to the value entry panel used to edit its value. */
    private final Map<ValueGem, ValueEntryPanel> valueGemPanelMap;
    
    /**
     * The popup menu currently being shown by the table top. This is either
     * the table top popup, gem part popup, gem popup or run mode popup.
     */
    private JPopupMenu currentPopupMenu = null;
    
    /** The location the popup menu has displayed at or will be displayed at. */
    private Point currentPopupLocation = new Point();       // ensure this is never null..
    
    /** Whether or not a popup menu is allowed to be shown. */
    private boolean popupShouldShow = true;
    
    /** The location at which gems should be pasted if paste was invoked from a popup menu. */
    private Point pasteLocation = null;
    
    /** The table top background image. */
    private BufferedImage backgroundImage = null;
    
    /** The x offset of the background image used to create the illusion of 'scrolling' */
    private int backgroundImageOriginOffsetX = 0;
    
    /** The y offset of the background image used to create the illusion of 'scrolling' */
    private int backgroundImageOriginOffsetY = 0;
    
    /** The DisplayedGem that is used as the anchor for selections (mouse or keyboard) using the SHIFT key. */
    private DisplayedGem shiftSelectionAnchorGem = null;

    /**
     * Flag to indicate that a paint is occurring, and not to do any drawings on the TableTop
     * until the full paint is finished (flag reset).
     */
    private int isPainting;
    

    /**
     * Draw action enum pattern.
     * Creation date: (31/08/2001 10:58:43 AM)
     * @author Edward Lam
     */
    static final class DrawAction {
        static final DrawAction DRAW   = new DrawAction ();
        static final DrawAction UNDRAW = new DrawAction ();
        static final DrawAction REDRAW = new DrawAction ();

        /**
         * Constructor for a draw action
         */
        private DrawAction() {
        }
    }


    /**
     * Select Mode enum pattern to describe the different selection modes that can
     * arise while selecting gems with the mouse or keyboard.
     * Creation date: (12/04/01 11:21:43 AM)
     * @author Edward Lam
     */
    static final class SelectMode {
        static final SelectMode REPLACE_SELECT = new SelectMode (); // Regular
        static final SelectMode TOGGLE = new SelectMode ();         // Control meta
        static final SelectMode SELECT = new SelectMode ();         // Shift meta

        /**
         * Constructor for a drag select mode
         */
        private SelectMode() {
        }
    }

    /**
     * An action used to add a new reflector for a collector to the table top.
     * This is used by the 'Add Other Emitter' menu item for the non-gem
     * table top menu.
     */
    private class AddReflectorAction extends AbstractAction {
        
        private static final long serialVersionUID = -7296061875095016427L;
        private final CollectorGem collector;
        
        public AddReflectorAction(CollectorGem collector) {
            super(collector.getUnqualifiedName());
            this.collector = collector;
        }
        
        public void actionPerformed(ActionEvent evt) {
            DisplayedGem dGem = tableTop.createDisplayedReflectorGem(currentPopupLocation, collector);
            ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
            
            editSupport.beginUpdate();
            tableTop.doAddGemUserAction(dGem, currentPopupLocation);
            editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
            editSupport.endUpdate();
        }
    }

    /**
     * An editable text field that accepts valid CAL identifiers for variable names
     * Creation date: (10/29/01 4:04:00 PM)
     * @author Edward Lam
     */
    class EditableGemNameField extends EditableIdentifierNameField.VariableName {

        private static final long serialVersionUID = -500686040771757761L;

        /** The gem to which this refers */
        private final Gem gem;

        /** The name of the before the name gets changed. */
        private String oldName;

        /** Keeps track of whether this component has been removed from the tableTop */ // is there a better way??
        private boolean removed = false;

        /** The undo manager for this text field. */
        private ExtendedUndoManager undoManager;
        
        /**
         * Constructor for a new EditableGemNameField.
         * @param initialText the text initially displayed in this field
         * @param gem the gem to edit the name for
         */
        EditableGemNameField(String initialText, Gem gem) {
            super();
            this.gem = gem;            // we have to set this before we call isValidName() ...
            initialize(initialText);
        }
        
        /**
         * Initializes this text field
         * @param initialText
         */
        private void initialize(String initialText) {
            
            oldName = gem instanceof CollectorGem ? ((CollectorGem)gem).getUnqualifiedName() : ((CodeGem)gem).getUnqualifiedName();
            
            // Hopefully, the initial text is a valid name!
            if (!isValidName(initialText)) {
                throw new IllegalArgumentException("Programming Error: attempting to initialize the name of a variable with invalid name: " + initialText);
            }
            
            // set the initial text
            setInitialText(initialText);
            setText(initialText);
            
            // set the font of the text
            setFont(gem instanceof CodeGem ? GemCutterPaintHelper.getTitleFont() : GemCutterPaintHelper.getBoldFont());
            
            // update the size of the text area to reflect the size of the text
            updateSize();
            
            // starts out with all text selected
            selectAll();
            
            // set up the undo manager
            undoManager = new ExtendedUndoManager();
            getDocument().addUndoableEditListener(undoManager);
            
            // moving focus away commits the text entered and closes this component
            addFocusListener(new FocusAdapter(){
                public void focusLost(FocusEvent e) {
                    commitText();
                }
            });
            
            // intercept some key events            
            addKeyListener(new KeyAdapter(){
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    
                    // pressing "ESC" cancels text entry and closes this component
                    if (keyCode == KeyEvent.VK_ESCAPE) {
                        cancelEntry();
                    }
                    
                    KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
                    
                    // handle undo and redo
                    if (keyStroke.equals(GemCutterActionKeys.ACCELERATOR_UNDO)) {
                        if (undoManager.canUndo()) {
                            undoManager.undo();
                            textChanged();
                        }
                        e.consume();
                        
                    } else if (keyStroke.equals(GemCutterActionKeys.ACCELERATOR_REDO)) {
                        if (undoManager.canRedo()) {
                            undoManager.redo();
                            textChanged();
                        }
                        e.consume();
                        
                    } else if (keyStroke.equals(GemCutterActionKeys.ACCELERATOR_ARRANGE_GRAPH) ||
                            keyStroke.equals(GemCutterActionKeys.ACCELERATOR_NEW)) {
                        
                        // We have to intercept accelerators for these so that the GemCutter 
                        // doesn't get screwed up when the text field doesn't match the action result.
                        e.consume();
                    }
                }
            });
            
            // ensure the cursor is visible when it moves
            addCaretListener(new CaretListener(){
                public void caretUpdate(CaretEvent e){
                    // just ensure the caret is visible
                    scrollCaretToVisible();
                }
            });
        }

        /**
         * Cancel text entry (press "ESC" ..)
         */
        protected void cancelEntry(){
            // Revert to the last valid name
            setText(getInitialText());
            
            // What to do, what to do..
            textCommittedInvalid();
        }

        /**
         * Close this window (if not already gone..)
         */
        synchronized void closeField(){
            // check if we've removed this already
            if (!removed) {
                removed = true;
                TableTopPanel.this.remove(this);
                TableTopPanel.this.repaint(EditableGemNameField.this.getBounds());

                // we have to do this or else you can just keep typing (..!)
                setEnabled(false);
                
                // Update the tabletop for the new gem graph state.
                //  Among other things, this will ensure arg name disambiguation with respect to the new collector name.
                tableTop.updateForGemGraph();
            }
            // trigger a focusLost() on this component if it had focus
            TableTopPanel.this.requestFocus();
        }

        /**
         * If this has been placed in the tabletop, make sure the caret is visible
         */
        void scrollCaretToVisible(){
            if (TableTopPanel.this.isAncestorOf(this)) {
                int dotPos = getCaret().getDot();
                try {
                    Rectangle caretRect = modelToView(dotPos);
                    caretRect.width += 1;
                    Rectangle convertedRect = 
                        SwingUtilities.convertRectangle(this, caretRect, TableTopPanel.this);
                    TableTopPanel.this.scrollRectToVisible(convertedRect);
                    
                } catch (BadLocationException e) {
                    // Nowhere to scroll.  Oh well.
                }
            }
        }

        /**
         * Notify that the text of this text field has changed.  Called upon insertUpdate() and remove() completion.
         * Eg. If the current result is not valid, maybe do something about it (like warn the user somehow..)
         */
        protected void textChanged(){
            super.textChanged();
            
            tableTop.resizeForGems();

            // ensure the caret is visible
            scrollCaretToVisible();
            
        }

        /**
         * Returns whether a name is a valid name for this field
         * @param name the name to check for validity 
         */
        protected boolean isValidName(String name){
            if (!(super.isValidName(name))) {
                return false;
            }

            return tableTop.isAvailableCodeOrCollectorName(name, gem);
        }

        /**
         * Take appropriate action if the result of the text change is invalid.
         */
        protected void textChangeInvalid(){
            // Signal the user.
            setForeground(Color.lightGray);

            // update the gem name to display the new text (despite being invalid)
            updateGemName(getText());

            // set a tooltip saying that the text is invalid
            String text = GemCutter.getResourceString("ToolTip_InvalidVariableName");
            String[] lines = ToolTipHelpers.splitTextIntoLines(text, 300, getFont(), ((Graphics2D)TableTopPanel.this.getGraphics()).getFontRenderContext());
            text = "<html>" + lines [0];
            for (int i = 1; i < lines.length; i++) {
                text += "<br>" + lines[i];
            }
            setToolTipText(text + "</html>");

            // update the text field to reflect the new size of the text
            updateSize();
        }

        /**
         * Take appropriate action if the result of the text change is valid.
         */
        protected void textChangeValid(){
            // do validation checking - paint text colors differently depending on the result
            setForeground(Color.black);

            // update the gem name to display the new text
            updateGemName(getText());

            // clear any tooltip saying that the text is invalid
            setToolTipText(null);
            
            // update the text field to reflect the new size of the text
            updateSize();
        }

        /**
         * Take appropriate action if the text committed is valid.
         */
        protected void textCommittedInvalid(){
            // the text is already reverted.  Update the gem name to reflect this.
            String revertedText = getText();
            updateGemName(revertedText);
            
            // close this component
            closeField();
        }

        /**
         * Take appropriate action if the text committed is valid.
         */
        protected void textCommittedValid(){
            // Update the gem name.
            String committedText = getText();
            updateGemName(committedText);

            // close this component
            closeField();

            // the text size wouldn't change on commit so no need to repaint let gems
            
            // Notify the undo manager of the name change, if any
            String newName = gem instanceof CollectorGem ? ((CollectorGem)gem).getUnqualifiedName() : ((CodeGem)gem).getUnqualifiedName();
            if (!newName.equals(oldName)) {
                if (gem instanceof CollectorGem) {
                    tableTop.getUndoableEditSupport().postEdit(new UndoableChangeCollectorNameEdit(tableTop, (CollectorGem)gem, oldName));
                    
                } else if (gem instanceof CodeGem) {
                    tableTop.getUndoableEditSupport().postEdit(new UndoableChangeCodeGemNameEdit(tableTop, (CodeGem)gem, oldName));
                }
            }
        }

        /**
         * Update the name of the gem represented by this text field.
         * @param newName String the new name for the let gem
         */
        private void updateGemName(String newName){
            if (gem instanceof CodeGem) {
                tableTop.renameCodeGem((CodeGem)gem, newName);
            } else {
                ((CollectorGem)gem).setName(newName);
            }
        }

        /**
         * Update the size of this field.
         */
        private void updateSize(){
            Insets insets = getInsets();

            // The X dimension is based on the size of the text for the name (plus some margins)
            FontMetrics fm = getFontMetrics(getFont());

            // Calculate width and height
            int newWidth = fm.stringWidth(getText()) + insets.right + insets.left + 1;
            int newHeight = fm.getHeight();

            setSize(new Dimension(newWidth, newHeight));
        }
    }


    /**
     * Listener which is invoked when the keyboard is used..
     * Creation date: (12/09/2001 9:37:19 AM)
     * @author Edward Lam
     */
    class KeyStrokeHandler extends KeyAdapter {
        
        /**
         * Called when a key is released
         * @param evt
         */
        public void keyReleased(KeyEvent evt) {
            // If the user is dragging, and Ctrl is released, then we want to get out of the Ctrl-drag mode
            if (evt.getKeyCode() == KeyEvent.VK_CONTROL) {
                if (tableTopMouseHandler.isGemDragging()) {
                    tableTopMouseHandler.setDragMode(TableTopDragMode.GEMDRAGGING);
                }
            }
        }
        
        /**
         * Called when a key is pressed.  
         * @param evt KeyEvent the related KeyEvent
         */
        public void keyPressed(KeyEvent evt) {

            // Only pay attention to this key event if none of the popup menus are open
            if (currentPopupMenu != null && currentPopupMenu.isVisible()) {
                return;
            }
            
            NavigationDirection navDirection = null;
            DisplayedGem focusedGem = tableTop.getFocusedDisplayedGem();

            switch (evt.getKeyCode()) {
            
            case KeyEvent.VK_CONTROL:
                if (tableTopMouseHandler.isGemDragging()) {
                    tableTopMouseHandler.setDragMode(TableTopDragMode.CTRLDRAGGING);
                }
                break;
                
            case KeyEvent.VK_UP:
                navDirection = NavigationDirection.UP;
                break;
                
            case KeyEvent.VK_DOWN:
                navDirection = NavigationDirection.DOWN;
                break;
                
            case KeyEvent.VK_LEFT:
                navDirection = NavigationDirection.LEFT;
                break;
                
            case KeyEvent.VK_RIGHT:
                navDirection = NavigationDirection.RIGHT;
                break;
                
            case KeyEvent.VK_SPACE:
                if (focusedGem != null) {
                    
                    // What we do depends on the modifiers
                    if (evt.isControlDown() && evt.isShiftDown()) {
                        
                        // Singleton select the focused Gem when both CTRL + SHIFT are used with the SPACE
                        // and update the shift selection anchor Gem
                        tableTop.selectDisplayedGem(focusedGem, true);
                        shiftSelectionAnchorGem = focusedGem;
                        
                    } else if (evt.isControlDown() || !tableTop.isSelected(focusedGem)) {
                        
                        // Toggle the selection state of the focused gem (if there is one)
                        // and make the focused gem the new selection anchor.
                        tableTop.toggleSelected(focusedGem);
                        shiftSelectionAnchorGem = focusedGem;
                    }
                }
                return;
                
            case KeyEvent.VK_ESCAPE:
                // todoSN - This may be temporary, although it may be good to clear the selection with Escape.
                //            Wait to see what happens once proper focus switching is working in the GemCutter.
                // Clear any selections and return focus to the TableTop
                if (!tableTopMouseHandler.isUsefulDragMode(tableTopMouseHandler.getDragMode())) {
                    tableTop.selectDisplayedGem(null, true);
                    shiftSelectionAnchorGem = null;
                    tableTop.setFocusedDisplayedGem(null);
                    requestFocus();
                    return;
                } else {
                    Graphics2D g2d = (Graphics2D)getGraphics();
                    tableTopMouseHandler.drawDragGhost(TableTopPanel.DrawAction.UNDRAW, TableTopPanel.SelectMode.REPLACE_SELECT, g2d);
                    g2d.dispose();
                    tableTopMouseHandler.setDragMode(TableTopDragMode.USELESS);
                    break;
                }
                
            default:
                return;
            }
            
            // If the navigation direction is not null and there is a
            // focused Gem we know an arrow was pressed for navigation
            if (navDirection != null && focusedGem != null) {

                // Get the next gem to gain focus
                DisplayedGem nextGem = tableTop.findNearestDisplayedGem(navDirection, focusedGem);
                
                // Did we find a Gem in the right direction?
                if (nextGem != null) {

                    // Decide how to handle the change of focus.
                    if (evt.isShiftDown()) {
                        // If the selection anchor is null then we are just starting to select
                        // some range of Gems.
                        if (shiftSelectionAnchorGem == null) {
                            shiftSelectionAnchorGem = nextGem;
                        }
                        
                        Rectangle2D rect = getRectangleForDisplayedGems(shiftSelectionAnchorGem, nextGem);
                        tableTop.selectGems(rect, TableTopPanel.SelectMode.REPLACE_SELECT);
                        
                    } else if (!evt.isControlDown()) {
                        // No shift key and no CTRL key so singleton select the next Gem 
                        // and update the selection anchor.
                        tableTop.selectDisplayedGem(nextGem, true);
                        shiftSelectionAnchorGem = nextGem;
                    }
                    
                    // Shift the focus
                    scrollRectToVisible(nextGem.getBounds());
                    tableTop.setFocusedDisplayedGem(nextGem);
                }
                
                // Consume the key event so that the scroll bars don't get the arrow key event
                evt.consume();
            }
        }
    }


    /**
     * Navigation direction enum pattern.
     * Creation date: (04/09/2002 12:19:00 PM).
     * @author Steve Norton
     */
    static final class NavigationDirection {
        
        final String direction;
        
        static final NavigationDirection UP = new NavigationDirection("Up");
        static final NavigationDirection DOWN = new NavigationDirection("Down");
        static final NavigationDirection LEFT = new NavigationDirection("Left");
        static final NavigationDirection RIGHT = new NavigationDirection("Right");
        
        /**
         * Constructor for a navigation direction.
         */
        private NavigationDirection(String direction) {

            this.direction = direction;
        }
    }


    /**
     * Mouse handler for when the gem cutter is in run mode.
     * Creation date: Oct 09th 2002
     * @author Ken Wong
     */
    private class RunModeMouseHandler extends MouseAdapter { 
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
    }


    /**
     * Component listener for the TableTop
     * Creation date: (12/14/01 10:28:43 AM)
     * @author Edward Lam
     */
    class TableTopComponentListener extends ComponentAdapter {
        /**
         * Invoked when the component's size changes.
         */
        public void componentResized(ComponentEvent e) {
            // If the TargetGem is docked, move it to the top-right corner of the component
            tableTop.checkTargetDockLocation();
            
            // Repaint the overview.
            gemCutter.getOverviewPanel().repaint();
        }
    }


    /**
     * Handler for drag and drop events.
     * Creation date: (03/15/2002 2:03:35 PM)
     * @author Edward Lam
     */
    class TableTopDragAndDropHandler implements DropTargetListener {

        /** The image that shows up when dragging.  
         * Null if not dragging-and-dropping or if there is no drag image set. */
        private BufferedImage dragImage = null;
        
        /** The offset of the mouse from the image origin while dragging. */
        private Point mousePointOffset = new Point(0, 0);
        
        /** The bounds of the last drawn drag ghost. */
        private final Rectangle lastGhostRect = new Rectangle();
        
        /**
         * Constructor for a drag-and-drop handler
         */
        TableTopDragAndDropHandler() {
        }

        /*
         * Methods implementing DropTargetListener ************************************************************
         */
        
        /**
         * Called when a drag operation has encountered the <code>DropTarget</code>.
         * <P>
         * @param dtde the <code>DropTargetDragEvent</code> 
         */
        public void dragEnter(DropTargetDragEvent dtde) {
            DataFlavor SCDF = GemEntitySelection.getEntityListDF();
            if (dtde.isDataFlavorSupported(SCDF)) {
                dtde.acceptDrag(dtde.getDropAction());

                // Set the image to drag and its offset
                dragImage = gemCutter.getBrowserTree().getDragImage();
                mousePointOffset = gemCutter.getBrowserTree().getDragOffset();

            } else {
                dtde.rejectDrag();
            }
            
            // HACK: Make up a dummy mouse event and let the ToolTipManager know that the mouse has entered
            // the table top.  We need to do this because a normal mouse entered event does not get fired when we
            // enter via DnD.  This seems to have the unfortunate effect of confusing the tool tip manager so that
            // it doesn't display tool tips when it should.  Notifying the manager when the mouse enters
            // via DnD seems to avoid the confusion.
            
            MouseEvent mouseEvt = new MouseEvent(TableTopPanel.this, MouseEvent.MOUSE_ENTERED, 0, 0, dtde.getLocation().x, dtde.getLocation().y, 1, false);
            ToolTipManager.sharedInstance().mouseEntered(mouseEvt);
            
        }
        
        /**
         * The drag operation has departed the <code>DropTarget</code> without dropping.
         * <P>
         * @param dte the <code>DropTargetEvent</code> 
         */
        public void dragExit(DropTargetEvent dte) {
            // If necessary, erase the last ghost image
            if (!TableTopPanel.DRAG_IMAGE_SUPPORTED) {
                paintImmediately(lastGhostRect.getBounds());
            }
        }
        
        /**
         * Called when a drag operation is ongoing on the <code>DropTarget</code>.
         * <P>
         * @param dtde the <code>DropTargetDragEvent</code> 
         */
        public void dragOver(DropTargetDragEvent dtde){
            if (!dtde.isDataFlavorSupported(GemEntitySelection.getEntityListDF())) {
                dtde.rejectDrag();
                return;
            } 
            dtde.acceptDrag(dtde.getDropAction());
            
            // draw a ghost image
            // To draw the drag image:
            //   First, repaint the real estate the drag image last occupied. 
            //    Note that simply calling repaint() won't work because it effectively delays the repainting, 
            //    possibly until after you have drawn the new drag image, and therefore, erases all or part of it. 
            //    You really must paint the area immediately, using the, you guessed it, paintImmediately() method.
            //
            //   Second, you draw the ghost image in its new location. 
            //    Note that you draw the image the same distance away from the mouse pointer as when the node 
            //    was first clicked.
            
            if (!TableTopPanel.DRAG_IMAGE_SUPPORTED && dragImage != null) {
                Graphics2D g2d = (Graphics2D)getGraphics();

                // Calculate new location
                Point mouseLocation = dtde.getLocation();
                int newX = mouseLocation.x - mousePointOffset.x;
                int newY = mouseLocation.y - mousePointOffset.y;
                
                // Update if the location changed
                if (newX != lastGhostRect.x || newY != lastGhostRect.y) {
                    // Erase the last ghost image and cue line
                    paintImmediately(lastGhostRect.getBounds());    
                    
                    // Remember where you are about to draw the new ghost image
                    lastGhostRect.setBounds(newX, newY, dragImage.getWidth(), dragImage.getHeight());
                    
                    // Draw the ghost image
                    g2d.drawImage(dragImage, 
                            AffineTransform.getTranslateInstance(lastGhostRect.getX(), lastGhostRect.getY()), 
                            null);
                }
                
                g2d.dispose();
            }
            
        }
        
        /**
         * The drag operation has terminated with a drop on this <code>DropTarget</code>.
         * This method is responsible for undertaking the transfer of the data associated with the
         * gesture. The <code>DropTargetDropEvent</code> provides a means to obtain a <code>Transferable</code>
         * object that represents the data object(s) to be transfered.<P>
         * From this method, the <code>DropTargetListener</code> shall accept or reject the drop via the   
         * acceptDrop(int dropAction) or rejectDrop() methods of the <code>DropTargetDropEvent</code> parameter.
         * <P>
         * Subsequent to acceptDrop(), but not before, <code>DropTargetDropEvent</code>'s getTransferable()
         * method may be invoked, and data transfer may be performed via the returned <code>Transferable</code>'s 
         * getTransferData() method.
         * <P>
         * At the completion of a drop, an implementation of this method is required to signal the success/failure
         * of the drop by passing an appropriate <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
         * dropComplete(boolean success) method.
         * <P>
         * Note: The actual processing of the data transfer is not required to finish before this method returns. 
         * It may be deferred until later.
         * <P>
         * @param dtde the <code>DropTargetDropEvent</code> 
         */
        public void drop(DropTargetDropEvent dtde) {
            try {
                // Get the transferable object
                Transferable trans = dtde.getTransferable();
                
                // We currently only accept our special DataFlavor
                DataFlavor SCDF = GemEntitySelection.getEntityListDF();
                if (trans.isDataFlavorSupported(SCDF)) {
                    dtde.acceptDrop(dtde.getDropAction());
                    
                    // Create Gem objects for items dropped in from the gem browser
                    List<Object> scs = UnsafeCast.<List<Object>>unsafeCast(trans.getTransferData(SCDF));

                    // Get the drop location
                    Point dropXY = dtde.getLocation();

                    int scsLen = scs.size();
                    if (scsLen < 1) {
                        return;
                    }

                    // Increment the update level for the edit undo.  This will aggregate the drops.
                    tableTop.getUndoableEditSupport().beginUpdate();

                    // Now create the gems.  Start out with the first one
                    GemEntity gemEntity = (GemEntity)scs.get(0);
                    DisplayedGem dGem = tableTop.createDisplayedFunctionalAgentGem(dropXY, gemEntity);
                    
                    // Adjust so that the the middle of the first gem appears under the pointer.
                    Rectangle dGemBounds = dGem.getBounds();
                    int halfGemWidth = dGemBounds.width / 2;
                    int halfGemHeight = dGemBounds.height / 2;
                    dropXY.translate(-halfGemWidth, -halfGemHeight);

                    // Now add the gem.
                    dGem.setLocation(dropXY);
                    tableTop.doAddGemUserAction(dGem, dropXY);

                    // Now all the other gems (if any).
                    for (int i = 1; i < scsLen; i++) {
                        gemEntity = (GemEntity)scs.get(i);                

                        // Move the dropXY a bit - the previous displayed gem's height plus some constant factor.
                        dropXY.translate(0, dGem.getBounds().height + DisplayConstants.MULTI_DROP_OFFSET);

                        // Create a new gem
                        dGem = tableTop.createDisplayedFunctionalAgentGem(dropXY, gemEntity);

                        // Now add the gem.
                        tableTop.doAddGemUserAction(dGem, dropXY);
                    }

                    // make sure the table top is large enough to hold any dropped gems
                    tableTop.resizeForGems();
                    
                    // Override the default undo name if more than one gem dropped.
                    if (scsLen > 1) {
                        tableTop.getUndoableEditSupport().setEditName(GemCutter.getResourceString("UndoText_AddGems"));
                    }

                    // Decrement the update level.  This will post the edit if the level is zero.
                    tableTop.getUndoableEditSupport().endUpdate();

                } else {
                    // We don't understand this
                    dtde.rejectDrop();
                }

            } catch (UnsupportedFlavorException ufe) {
                // Bad data flavour in drop - should never happen!
                String msgText = GemCutter.getResourceString("BadDropDataFlavour") + "\n" + ufe;
                JOptionPane.showMessageDialog(TableTopPanel.this, msgText, GemCutter.getResourceString("WindowTitle"), JOptionPane.ERROR_MESSAGE);
                dtde.rejectDrop();

            } catch (java.io.IOException ioe) {
                // Other dodginess has occurred
                String msgText = GemCutter.getResourceString("BadDropIO") + "\n" + ioe;
                JOptionPane.showMessageDialog(TableTopPanel.this, msgText, GemCutter.getResourceString("WindowTitle"), JOptionPane.ERROR_MESSAGE);
                dtde.rejectDrop();

            } finally {

                // Say that we're done with the drag and dropping thingy
                dtde.getDropTargetContext().dropComplete(true);

                // reset drag image info
                // so we don't accidentally reuse old info if one source supplies an image and another does not.
                dragImage = null;
                mousePointOffset.move(0,0);

            }   
        }
        
        /**
         * Called if the user has modified the current drop gesture.
         * <P>
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        public void dropActionChanged(DropTargetDragEvent dtde) {
            DataFlavor SCDF = GemEntitySelection.getEntityListDF();
            if (dtde.isDataFlavorSupported(SCDF)) {
                dtde.acceptDrag(dtde.getDropAction());
            } else {
                dtde.rejectDrag();
            }
        }
        
    }


    /**
     * Drag action enum pattern.
     * Creation date: (31/08/2001 10:58:43 AM)
     * @author Edward Lam
     */
    private static final class TableTopDragMode extends MouseClickDragListener.DragMode {
        /*
         * GEMDRAGGING      - dragging gems around the tabletop.
         * CTRLDRAGGING     - 'cloning' function
         * CONNECTING       - connecting a gem
         * DISCONNECTING    - disconnecting a gem.  Connecting to another gem is allowed.
         * SELECTING        - selecting gems
         * USELESS          - dragging but with no effect
         */
        private static final DragMode GEMDRAGGING       = new TableTopDragMode ("Gem Dragging");
        private static final DragMode CTRLDRAGGING      = new TableTopDragMode ("Ctrl - Gem Dragging");  
        private static final DragMode CONNECTING        = new TableTopDragMode ("Connecting");  
        private static final DragMode DISCONNECTING     = new TableTopDragMode ("Disconnecting");
        private static final DragMode SELECTING         = new TableTopDragMode ("Selecting");   
        private static final DragMode USELESS           = new TableTopDragMode ("Useless");     

        /**
         * Constructor for a drag mode.
         */
        private TableTopDragMode(String name) {
            super(name);
        }
    }




    
    /**
     * Event listener for the TableTop
     * Creation date: (12/04/01 3:21:43 PM)
     * @author Edward Lam
     */
    class TableTopMouseHandler extends MouseClickDragListener {

        /**
         * Class to hold info for redrawing
         * Creation date: (12/17/01 12:18:43 PM)
         * @author Edward Lam
         */
        private class RedrawInfo {
            final Point pressedAt;
            final Point dragPos;
            final DragMode dragMode;
            final DisplayedGem[] dragList;
            final SelectMode selectMode;
            
            /**
             * Constructor
             */
            RedrawInfo(Point pressedAt, Point dragPos, DragMode dragMode, 
                    DisplayedGem[] dragList, SelectMode selectMode){
                this.pressedAt = pressedAt;
                this.dragPos = dragPos;
                this.dragMode = dragMode;
                this.dragList = dragList;
                this.selectMode = selectMode;
            }
        }

        //
        // Mouse states and state associated with it -------------------------------------------------------
        //
        
        /** The gem that we pressed on and are dragging. */
        private DisplayedGem clickGem;

        /** The displayed input or output where the connection drag originated. */
        private DisplayedPartConnectable connectionDragAnchorPart;

        /** If disconnecting, the part that was disconnected. */
        private DisplayedPartConnectable disconnectedDisplayedPart = null;

        /** What the last select mode was. */        
        private SelectMode lastSelectMode;

        /** List of Gems being dragged. */
        private DisplayedGem[] dragList;

        /** The last position for dragging. */
        private Point dragPos;

        /** The clip area present for the last drag. */
        private Shape lastDragClipArea;
        
        /** The colour present for the last drag. */
        private Color lastDragColour;
        
        /** Information pertaining to the most recent draw (if any). */
        private RedrawInfo redrawInfo = null;
        
        /** Whether a drag operation started by pressing & dragging over a VEP. */
        private boolean dragStartedOverVEP = false;

        // Maybe uncomment when disconnection happens properly.
//        /** (PartInput->AutoburnLogic.BurnStatus) If CONNECTING or DISCONNECTING, map from inputs which have been automatically burnt or unburnt, 
//         *  but have not have had edits committed (since the action may be canceled) to their burn state before the action was initiated */
//        private Map transientInputToOldBurnStateMap = new HashMap();
//
        /**
         * Constructor for a TableTopMouseHandler
         */
        TableTopMouseHandler() {
            super();
        }
        
        /**
         * Returns the drag mode
         * @return DragMode
         */
        DragMode getDragMode() {
            return dragMode;
        }

        /**
         * Move the drag mode into the aborted state.
         */
        protected void abortDrag() {

            try {
                // Finish the ghost drawing (undraw the last lot)
                Graphics2D g2d = (Graphics2D)getGraphics();   
                drawDragGhost(DrawAction.UNDRAW, lastSelectMode, g2d);
                g2d.dispose();
                
                // if we're connecting, undo automatically burned inputs on the source
                if (dragMode == TableTopDragMode.CONNECTING || dragMode == TableTopDragMode.DISCONNECTING) {
                    DisplayedGem burnGem = connectionDragAnchorPart.getDisplayedGem();
                    tableTop.getBurnManager().doUnburnAutomaticallyBurnedInputsUserAction(burnGem.getGem());
                    
                    // Decrement the update level.  This will post the edit if the level is zero.
                    tableTop.getUndoableEditSupport().endUpdate();
                }

            } finally {

                // Set our drag states appropriately
                super.abortDrag();
                tableTop.selectDisplayedGem(null, false);
                tableTop.setFocusedDisplayedGem(null);
                setCursor(null);
            }
        }

        /**
         * Assuming that we are in the TableTopDragMode.CONNECTING state, change the tabletop state to take into account
         * the current drag position.  This changes the cursor, and may attempt to carry out or undo autoburn 
         * on the connection source.
         * @param where Point The mouse location to check.
         */
        private void changeStateForConnecting(Point where) {
            
            // Check if we are over a part that is not yet bound to another part.
            DisplayedGem.DisplayedPart partUnder = tableTop.getGemPartUnder(where);

            // Make sure we are trying to connect inputs to outputs and vice versa.
            if ((connectionDragAnchorPart instanceof DisplayedPartOutput && partUnder instanceof DisplayedPartInput) || 
                    (connectionDragAnchorPart instanceof DisplayedPartInput && partUnder instanceof DisplayedPartOutput)){

                // Assign the source and sink parts depending what we are hovering over.
                PartConnectable sourcePart;
                PartInput sinkPart;
                
                if (connectionDragAnchorPart instanceof DisplayedPartOutput) {
                    sourcePart = connectionDragAnchorPart.getPartConnectable();
                    sinkPart = ((DisplayedPartInput)partUnder).getPartInput();
                    
                } else {
                    sourcePart = ((DisplayedPartConnectable)partUnder).getPartConnectable();
                    sinkPart = ((DisplayedPartInput)connectionDragAnchorPart).getPartInput();
                }
                
                Gem sourceGem = sourcePart.getGem();
                Gem sinkGem = sinkPart.getGem();
                
                if (sourceGem instanceof ValueGem) {
                    
                    // Check if a value gem can be connected.
                    // They get special treatment since autoburning doesn't apply to value gems.
                    
                    ModuleTypeInfo currentModuleTypeInfo = tableTop.getCurrentModuleTypeInfo();
                    ValueEditorManager valueEditorManager = gemCutter.getValueEditorManager();
                    
                    if (!valueEditorManager.canInputDefaultValue(sinkPart.getType())) {
                        setCursor(connectNoParkCursor);
                        
                    } else if (GemGraph.isCompositionConnectionValid(sourcePart, sinkPart, currentModuleTypeInfo)) {
                        setCursor(connectCursor);
                        
                    } else if (GemGraph.isDefaultableValueGemSource(sourcePart, sinkPart, gemCutter.getConnectionContext())) {
                        setCursor(connectCursor);
                        
                    } else {
                        setCursor(connectNoParkCursor);
                    }
                } else if (sinkGem instanceof RecordFieldSelectionGem && !((RecordFieldSelectionGem)sinkGem).isFieldFixed()) {
                    if (GemGraph.isValidConnectionToRecordFieldSelection(sourcePart, sinkPart, tableTop.getCurrentModuleTypeInfo()) != null) {
                        setCursor(connectCursor);
                    } else {
                        setCursor(connectNoParkCursor);
                    }
                } else if (GemGraph.arePartsConnectable(sourcePart, sinkPart)) {

                    // Check if the parts can be connected either through burning or direct connection.
                    // Even if they can be connected without autoburning we want to at least try burning them.
                    // Why? Because it might be better to burn the gem than just connecting it. We want to
                    // at least consider that possibility and recommend the best choice to the user.

                    if (tableTop.getBurnManager().getAutoburnLastResult() == AutoburnLogic.AutoburnAction.BURNED) {

                        // If the gem has already been burnt previously then keep showing the burn cursor.
                        // Don't try and burn the gem again since that will just show the connect cursor,
                        // since the autoburn logic already sees the gem as being burnt.

                        setCursor(burnCursor);
                        
                    } else if (GemGraph.isConnectionValid(sourcePart, sinkPart)) {
                        
                        // Try making a connection using autoburning.
                        
                        AutoburnLogic.AutoburnAction autoBurnResult = tableTop.getBurnManager().handleAutoburnGemGesture(sourceGem, sinkPart.getType(), true);
                        
                        if (autoBurnResult == AutoburnLogic.AutoburnAction.BURNED) {
                            setCursor(burnCursor);
                            
                        } else if (autoBurnResult == AutoburnLogic.AutoburnAction.MULTIPLE) {
                            setCursor(burnQuestionCursor);
                            
                        } else if (autoBurnResult == AutoburnLogic.AutoburnAction.IMPOSSIBLE) {
                            setCursor(connectNoParkCursor);
                            
                        } else {
                            setCursor(connectCursor); 
                        }

                    } else {
                        
                        // Won't connect through autoburning.
                        setCursor(connectNoParkCursor);
                    }

                } else {

                    // The connection as a whole is invalid (eg. destination already connected).
                    setCursor(connectNoParkCursor);
                }

            } else {
                
                // We're not trying to connect a sink. Reset the cursor.
                setCursor(null);

                // If we aren't disconnecting, we should undo any autoburn we previously performed (if any).
                if (dragMode != TableTopDragMode.DISCONNECTING && tableTop.getBurnManager().getAutoburnLastResult() == AutoburnLogic.AutoburnAction.BURNED) {
                    tableTop.getBurnManager().handleAutoburnGemGesture(connectionDragAnchorPart.getGem(), null, false);
                }
            }
        }

        /**
         * Check whether the tabletop needs expanding, and take care of the expansion grunt work if necessary.
         * Note that the "where" parameter may be modified during the execution of this method, to take
         * into account the new coordinates in an expanded tabletop.
         * @param where Point The mouse location to check.
         * @return boolean Whether the tabletop expanded.
         */
        private boolean checkExpand(Point where) {
            
            Rectangle visibleRect = getVisibleRect();

            // if the point is in the visible bounds of the tabletop, we're ok.
            if (visibleRect.contains(where)) {
                return false;
            }
            
            // otherwise, we may need to expand. Declare the expand flag.
            boolean expand = false;
            
            // the dimension of the new tabletop if we expand
            Dimension dim = new Dimension();
            
            int whereX = where.x;
            int whereY = where.y;

            //
            // x-axis expansion
            //
            if (whereX < 0) {
                
                // expand left
                expand = true;
                int moveDistance = -(whereX);

                // Translate the gemgraph and tabletop points to the right
                tableTop.moveAllGems(moveDistance, 0);
                pressedAt.x += moveDistance;
                where.x += moveDistance;
                backgroundImageOriginOffsetX += moveDistance;
                
                // update the last clip bounds so that the old drag ghosts will be undrawn properly
                if (lastDragClipArea != null) {
                    Rectangle rect = lastDragClipArea.getBounds();
                    rect.x += moveDistance;
                    lastDragClipArea = rect;
                }

                dim = new Dimension(getWidth() + moveDistance, getHeight());

            } else if (whereX > getSize().width) {

                // Expand right.
                expand = true;
                int expandDistanceX = whereX - getSize().width;
                dim = new Dimension(getWidth() + expandDistanceX, getHeight());
            }   
            
            //
            // y-axis expansion
            //
            if (whereY < 0) {

                // Expand up        
                expand = true;                  
                int moveDistance = -(whereY);

                // Translate the gemgraph and tabletop points down
                tableTop.moveAllGems(0, moveDistance);
                pressedAt.y += moveDistance;
                where.y += moveDistance;
                backgroundImageOriginOffsetY += moveDistance;
                
                // update the last clip bounds so that the old drag ghosts will be undrawn properly
                if (lastDragClipArea != null) {
                    Rectangle rect = lastDragClipArea.getBounds();
                    rect.y += moveDistance;
                    lastDragClipArea = rect;
                }

                dim = new Dimension(getWidth(), getHeight() + moveDistance);
                
            } else if (whereY > getSize().height) {

                // Expand down.
                expand = true;
                
                int expandDistanceY = whereY - getSize().height + 1;    // + 1 so we can see the last pixel
                dim = new Dimension(getWidth(), getHeight() + expandDistanceY);
            } 

            
            // now take appropriate action now that we decided whether or not to expand
            if (expand) {
                // Set the size of the tabletop.  This also invokes revalidation since it's a JContainer.
                setSize(dim);           
                setPreferredSize(dim);
                revalidate();
            }
            
            return expand;
        }
        
        /**
         * Draw the appropriate drag ghost according to the current drag mode 
         *   - the drag ghost may be a connection line, gem drag ghost, or drag selection outline.
         *
         * @param drawAction the current draw action. If this is DrawAction.REDRAW, then dragAction and dragSelectMode
         * do not have to be provided.
         * @param selectMode the mode to draw the drag ghost in. This can be one of three modes:
         *  <br>REPLACE_SELECT:     Solid line - select only enclosed Gems
         *  <br>TOGGLE:             Wavy line - toggle selection of enclosed Gems
         *  <br>SELECT:             Dotted line - select enclosed Gems, preserving ones already selected
         *  <br>This parameter can be ignored if the drag action is not drag selecting.
         * @param graphics Graphics2D the graphics context to use.
         * 
         */
        synchronized void drawDragGhost(DrawAction drawAction, SelectMode selectMode, Graphics2D graphics) {
            
            Point pressedAt, dragPos;
            DragMode dragMode;
            DisplayedGem[] dragList;

            // set draw info based on whether or not we are redrawing.
            if (drawAction == DrawAction.REDRAW) {
                if (redrawInfo == null) {
                    return;         // nothing to redraw
                }

                // reload the draw info
                pressedAt = redrawInfo.pressedAt;
                dragPos = redrawInfo.dragPos;
                dragMode = redrawInfo.dragMode;
                dragList = redrawInfo.dragList;
                selectMode = redrawInfo.selectMode;

            } else {
                try {
                    // there's a chance that click and drag positions could be modified from the event dispatch thread
                    // and that this method is called from the painting thread.  Thus, click and drag positions could be
                    // concurrently used and modified in the middle of this thread!  Thus we clone the points locally:
                    pressedAt = (Point)this.pressedAt.clone();
                    dragPos   = (Point)this.dragPos.clone();
                    dragMode = this.dragMode;
                    dragList = this.dragList;
                } catch (NullPointerException npe) {
                    // nothing to (un)draw..
                    return;
                }
            }
            
            // check for nothing to do
            if (lastDragClipArea == null && drawAction == DrawAction.UNDRAW) {
                redrawInfo = null;
                return;
            }
            
            // Get the current graphics context
            Graphics2D g2d = (Graphics2D)getGraphics();
            
            // clip further, and update our clip areas, if necessary
            if (drawAction == DrawAction.DRAW || drawAction == DrawAction.REDRAW) {
                
                Shape graphicsClip = graphics.getClip();
                
                if (graphicsClip != null) {
                    // clip to the clip area derived from the passed in graphics object
                    g2d.setClip(graphicsClip);
                } else {
                    // make sure there is a clip, so that the check for null above doesn't fail inappropriately on undraw
                    g2d.setClip(getVisibleRect());
                }
                
                // update the latest clip area
                lastDragClipArea = g2d.getClip();
                
                // update redraw info
                redrawInfo = new RedrawInfo(pressedAt, dragPos, dragMode, dragList, selectMode);
                
            } else if (drawAction == DrawAction.UNDRAW) {
                
                // use the clip area present when the ghost was last drawn
                g2d.setClip(lastDragClipArea);
                
                // update the latest clip area
                lastDragClipArea = null;
                
                // clear redraw info if we're undrawing, since there will be nothing to redraw
                redrawInfo = null;
                
            }
            
            // Enter XOR mode
            g2d.setXORMode(Color.white);
            
            // Now (un)draw the drag ghost according to the drag action indicated
            if (dragMode == TableTopDragMode.GEMDRAGGING || dragMode == TableTopDragMode.CTRLDRAGGING) {
                
                // For each gem, draw a ghost
                for (final DisplayedGem dGem : dragList) {
                    // The ghost is the gem body translated by the drag offset
                    Shape ghost = dGem.getDisplayedGemShape().getBodyShape();
                    
                    // Translate into the correct location (how we do this depends on the type)
                    int translateX = dragPos.x - pressedAt.x;
                    int translateY = dragPos.y - pressedAt.y;
                    if (ghost instanceof Polygon) {
                        ((Polygon)ghost).translate(translateX, translateY);
                    } else if (ghost instanceof RectangularShape) {
                        RectangularShape rectGhost = (RectangularShape)ghost;
                        Rectangle bounds = rectGhost.getBounds();
                        rectGhost.setFrame(bounds.x + translateX, bounds.y + translateY, 
                                bounds.getWidth(), bounds.getHeight());
                    }
                    
                    g2d.setStroke(new BasicStroke((float) 2.0));    
                    g2d.draw(ghost);
                }
                
            } else if (dragMode == TableTopDragMode.CONNECTING || dragMode == TableTopDragMode.DISCONNECTING) {
                
                // Figure out which point is the beginning point of the connection based on the type of
                // the part where the connection originated.
                Point fromPoint, toPoint;
                if (tableTop.getGemPartUnder(pressedAt) instanceof DisplayedPartOutput) {
                    fromPoint = pressedAt;
                    toPoint = dragPos;
                    
                } else {
                    fromPoint = dragPos;
                    toPoint = pressedAt;
                }

                ConnectionRoute route = new ConnectionRoute(fromPoint, toPoint);
                DisplayedConnection.genConnectionRoute(route, DisplayConstants.REVERSE_CONNECTION_HOOK_SIZE);
                
                // set, save the draw color
                if (drawAction == DrawAction.DRAW || drawAction == DrawAction.REDRAW) {
                    // Set colour to the appropriate colour for the output type implied.
                    Color connectColour = tableTop.getTypeColour(connectionDragAnchorPart);
                    g2d.setColor(connectColour);

                    // save the last drag colour so we can undraw later in the same colour
                    lastDragColour = connectColour;

                } else {
                    // undraw in the last drag colour
                    g2d.setColor(lastDragColour);
                }
                
                // Draw a connection line
                route.draw(g2d);
                
            } else if (dragMode == TableTopDragMode.SELECTING) {
                
                // Set stroke characteristics.  Depends on mode
                if (selectMode == SelectMode.TOGGLE) {
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    //g2d.setStroke(new com.sun.glf.goodies.WaveStroke(1, 5, 2));
                    
                } else if (selectMode == SelectMode.SELECT) {
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 8f, new float[]{6f, 6f}, 0f));
                    
                } else {
                    // the default case: DragSelectMode.REPLACE_SELECT:
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    //g2d.setStroke(new com.sun.glf.goodies.TextStroke("Select", new Font("dialog", Font.PLAIN, 12), true, 0));
                }
                
                // It turns out drawing four lines is significantly faster than calling drawRect(int, int, int, int)
                // This is most noticeable in xor mode (which we use for this drag ghost)
                g2d.drawLine(pressedAt.x, pressedAt.y, pressedAt.x, dragPos.y);
                g2d.drawLine(pressedAt.x, pressedAt.y, dragPos.x, pressedAt.y );
                g2d.drawLine(pressedAt.x, dragPos.y, dragPos.x, dragPos.y);
                g2d.drawLine(dragPos.x, pressedAt.y, dragPos.x, dragPos.y);
                
            }
            
            // dispose the graphics object
            g2d.dispose();
        }
        
        /**
         * Carry out setup appropriate to enter the drag state.  Principal effect is to change dragMode as appropriate.
         * @param e MouseEvent the mouse event which triggered entry into the drag state.
         */
        public void enterDragState(MouseEvent e) {

            // Store the focus state and move the focus to the table top for now
            tableTop.saveFocus();
            requestFocus();
            
            // popups should not be shown in most drag states
            popupShouldShow = false;
            
            // find the drag mode appropriate to the place on which we initiated the drag
            DragMode nextMode = getDragModeForDragOrigin();
            ExtendedUndoableEditSupport undoableEditSupport = tableTop.getUndoableEditSupport();

            if (nextMode == TableTopDragMode.GEMDRAGGING && SwingUtilities.isLeftMouseButton(e)) {

                if (e.isControlDown()) {
                    // Ensure that the gem beneath the cursor is selected when we begin the drag.
                    DisplayedGem clickedGem = tableTop.getGemUnder(pressedAt);
                    tableTop.selectDisplayedGem(clickedGem, false);
                    tableTop.setFocusedDisplayedGem(clickedGem);
                    shiftSelectionAnchorGem = clickedGem;
                    
                    repaint();
                    nextMode = TableTopDragMode.CTRLDRAGGING;
                }

                // dragging gems around
                setDragMode(nextMode);
                
                // Create the dragList of selected Gems
                dragList = tableTop.getSelectedDisplayedGems();
                
            } else if (nextMode == TableTopDragMode.CONNECTING) {

                // connecting up gems
                dragMode = nextMode;

                // Increment the update level to aggregate any burns with connection change edits.
                undoableEditSupport.beginUpdate();
                
            } else if (nextMode == TableTopDragMode.DISCONNECTING) {
                
                // disconnecting gems
                dragMode = nextMode;

                // Increment the update level to aggregate any unburns with connection change edits.
                undoableEditSupport.beginUpdate();
                
                // Get the part which the user pressed.  Should be connectable!
                DisplayedPartConnectable partPressed = 
                    (DisplayedPartConnectable)tableTop.getGemPartUnder(pressedAt);

                // Keep track of the disconnected part.
                disconnectedDisplayedPart = partPressed;

                // Get the gem connection to disconnect.
                DisplayedConnection disconnectConn = partPressed.getDisplayedConnection();
                
                // Adjust the apparent click point to be the point of the arrow of the part that is
                // not being disconnected.
                if (partPressed instanceof DisplayedPartOutput) {
                    connectionDragAnchorPart = disconnectConn.getDestination();
                } else {
                    connectionDragAnchorPart = disconnectConn.getSource();
                }
                
                pressedAt = connectionDragAnchorPart.getConnectionPoint();                      
                
                // Indicate to the user that a drag disconnect is possible.
                setCursor(connectCursor);             

                // Disconnect the connection.
                tableTop.handleDisconnectGesture(disconnectConn.getConnection());

                // Undo any autoburns if we disconnected an output
                if (partPressed instanceof DisplayedPartOutput) {
                    DisplayedGem burnGem = partPressed.getDisplayedGem();
                    tableTop.getBurnManager().doUnburnAutomaticallyBurnedInputsUserAction(burnGem.getGem());
                }

            } else if (nextMode == TableTopDragMode.SELECTING && SwingUtilities.isLeftMouseButton(e)) {
                
                // drag selecting
                dragMode = nextMode;
                
            } else {
                
                // not dragging really, so we can display popups
                dragMode = TableTopDragMode.USELESS;
                popupShouldShow = true;
            }
            

        }       

        /**
         * Carry out setup appropriate to exit the drag state.  
         * Undraw the drag ghost and carry out translating/connecting/selecting
         * @param e MouseEvent the mouse event which caused an exit from the drag state
         */
        public void exitDragState(MouseEvent e) {

            // Finish the ghost drawing (undraw the last lot)
            if (isUsefulDragMode(dragMode)) {
                Graphics2D g2d = (Graphics2D)getGraphics();   
                drawDragGhost(DrawAction.UNDRAW, SelectMode.REPLACE_SELECT, g2d);
                g2d.dispose();
            }
            
            // Where are we now?  
            // We use dragPos instead of e.getPoint() so that the gem drops where the drag ghost shows it.
            Point where = dragPos;
            ExtendedUndoableEditSupport undoableEditSupport = tableTop.getUndoableEditSupport();

            // Now do whatever based on the drag mode
            if (dragMode == TableTopDragMode.CTRLDRAGGING) {
                
                // Ctrl Drag is used as a clone function
                // We want to group this stuff as one single action
                undoableEditSupport.beginUpdate();
                undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_CopyDrag"));
                DisplayedGemSelection displayedGemSelection = new DisplayedGemSelection(dragList, gemCutter);
                Rectangle rect = dragList[0].getBounds();
                for (int i = 1; i < dragList.length; i++) {
                    rect.add(dragList[i].getBounds());
                }
                int x = where.x - (pressedAt.x - rect.x);
                int y = where.y - (pressedAt.y - rect.y);
                
                tableTop.doPasteUserAction(displayedGemSelection, new Point(x, y));
                undoableEditSupport.endUpdate();

            } else if (dragMode == TableTopDragMode.GEMDRAGGING) {
                
                // Increment the update level for the edit undo.  This will aggregate the gem translations.
                undoableEditSupport.beginUpdate();
                if (dragList.length > 0) {
                    undoableEditSupport.setEditName(dragList.length > 1 ? GemCutter.getResourceString("UndoText_MoveGems") : 
                                                                          GemCutter.getResourceString("UndoText_MoveGem"));
                }

                // Do the move for each selected gem
                for (final DisplayedGem displayedGem : dragList) {
                    Point newGemLocation = displayedGem.getLocation();
                    newGemLocation.translate(where.x - pressedAt.x, where.y - pressedAt.y);
                    
                    // Perform the translation
                    tableTop.doChangeGemLocationUserAction(displayedGem, newGemLocation);
                }

                // Decrement the update level.  This will post the edit if the level is zero.
                undoableEditSupport.endUpdate();
                
            } else if (dragMode == TableTopDragMode.CONNECTING || dragMode == TableTopDragMode.DISCONNECTING) {

                // see if we can connect anything
                DisplayedPart partUnder = tableTop.getGemPartUnder(where);

                boolean connected = false;
                if (partUnder != null) {
                    
                    Connection newConnection = null;
                    if (connectionDragAnchorPart instanceof DisplayedPartOutput && partUnder instanceof DisplayedPartInput) {
                        newConnection = tableTop.handleConnectGemPartsGesture(connectionDragAnchorPart.getPartConnectable(), 
                                                                              ((DisplayedPartInput)partUnder).getPartInput());
                    } else if (partUnder instanceof DisplayedPartConnectable && connectionDragAnchorPart instanceof DisplayedPartInput) {
                        newConnection = tableTop.handleConnectGemPartsGesture(((DisplayedPartConnectable)partUnder).getPartConnectable(), 
                                                                              ((DisplayedPartInput)connectionDragAnchorPart).getPartInput());
                    }
                    
                    connected = (newConnection != null);
                }

                // Undo any autoburns if we didn't connect anything
                if (!connected && connectionDragAnchorPart instanceof DisplayedPartOutput) {
                    DisplayedGem burnGem = connectionDragAnchorPart.getDisplayedGem();
                    tableTop.getBurnManager().doUnburnAutomaticallyBurnedInputsUserAction(burnGem.getGem());
                }

                if (!connected && dragMode == TableTopDragMode.CONNECTING) {
                    // Don't post the edit if connecting and nothing happened.
                    undoableEditSupport.endUpdateNoPost();

                } else if (connected && dragMode == TableTopDragMode.DISCONNECTING && disconnectedDisplayedPart == partUnder){
                    // Also don't post the edit if all we did was reconnect a part that we disconnected.
                    undoableEditSupport.endUpdateNoPost();

                } else {
                    
                    if (dragMode == TableTopDragMode.CONNECTING) {
                        undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_ConnectGems"));

                    } else {
                        undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_DisconnectGems"));
                    }

                    // Decrement the update level, possibly triggering the edit to be posted.
                    undoableEditSupport.endUpdate();
                }

            } else if (dragMode == TableTopDragMode.SELECTING && pressedAt != null) {

                // calculate the bounds of the select area      
                Rectangle hitRect = new Rectangle(pressedAt);
                hitRect.add(where);

                // Perform appropriate selection operation for each intersecting gem.
                // We need to use the TOGGLE selection mode if the drag was started with the
                // CTRL modifier or the SELECT selection mode if the drag was started with the
                // SHIFT modifier... otherwise just use the last mode.  The SHIFT modifier seems to 
                // mask the CTRL modifier in this instance so look for that first here.
                SelectMode selMode;
                if (dragInitiatedWithSHIFT) {
                    selMode = SelectMode.SELECT;
                    
                } else if (dragInitiatedWithCTRL) {
                    selMode = SelectMode.TOGGLE;
                    
                } else {
                    selMode = lastSelectMode;
                }
                
                tableTop.selectGems(hitRect, selMode);
                
                // todoSN - the Windows Desktop does not alter the focused icon when drag selection is
                // performed so we won't here either.  Unfortunately, this may result in no gems having
                // focus if a gem is not specifically clicked so we will give focus on a drag if the 
                // existing focused gem is null.  This is probably just a temporary fix for this issue.
                // If the existing focused gem is null give focus to the Gem that is closest to the point 
                // where dragging started and inside the drag rectangle.
                DisplayedGem[] selectedGems = tableTop.getSelectedDisplayedGems();
                double dist = -1;
                DisplayedGem gemToFocusOn = null;

                for (final DisplayedGem dGem : selectedGems) {

                    Point2D centrePoint = dGem.getCenterPoint();
                    if (hitRect.contains(centrePoint)) {
                        double thisDist = pressedAt.distance(centrePoint);

                        if (dist < 0 || thisDist < dist) {
                            dist = thisDist;
                            gemToFocusOn = dGem;
                        }
                    }
                }

                // Actually set the focus here if we found a gem to give focus to.  If the user is
                // replace selecting then we need to update the focus no matter what.
                if (tableTop.getFocusedDisplayedGem() == null
                        && (gemToFocusOn != null || lastSelectMode == SelectMode.REPLACE_SELECT)) {

                    tableTop.setFocusedDisplayedGem(gemToFocusOn);
                }
                
                // Only update the selection anchor if the SHIFT and CTRL modifiers are NOT used on 
                // mouse button release.
                if (!e.isShiftDown() && !e.isControlDown()) {
                    shiftSelectionAnchorGem = gemToFocusOn;
                }
            }

            // Dragging is finished.  Reset dragMode.
            dragMode = DragMode.NOTDRAGGING;
        }       

        /**
         * Get the drag mode appropriate to the origin of the current drag
         * @return DragMode the drag mode appropriate to the origin of the current drag.
         */
        private DragMode getDragModeForDragOrigin() {

            // Get the part which the user pressed  
            DisplayedPart partPressed = null;
            if (pressedAt != null) {
                partPressed = tableTop.getGemPartUnder(pressedAt);
            }

            // default is selecting (if not dragging or composing)
            DragMode returnMode = TableTopDragMode.SELECTING;

            // Did they hit anything?
            if (partPressed != null) {
                
                if (partPressed instanceof DisplayedPartConnectable && 
                        ((DisplayedPartConnectable)partPressed).getPartConnectable().isConnected()){
                    // Disconnecting
                    returnMode = TableTopDragMode.DISCONNECTING;

                } else if (partPressed instanceof DisplayedPartBody) {
                    // Dragging gem(s)
                    returnMode = TableTopDragMode.GEMDRAGGING;              
                    
                } else if (partPressed instanceof DisplayedPartConnectable) {
                    // We got an input or output.
                    returnMode = TableTopDragMode.CONNECTING;
                    
                } 
            }
            return returnMode;
        }

        /**
         * Get the select mode appropriate to the modifiers on the mouse event
         * @param e MouseEvent the related mouse event
         * @return SelectMode the selection mode appropriate to the modifiers on the mouse event
         */
        private SelectMode getSelectModeForEvent(MouseEvent e) {
            
            if (e.isShiftDown()) {
                return SelectMode.SELECT;

            } else if (e.isControlDown()) {
                return SelectMode.TOGGLE;

            } else {
                return SelectMode.REPLACE_SELECT;
            }       

        }

        /**
         * Whether this drag mode actually enables accomplishing anything.
         *   Gem dragging, connecting, disconnecting, and selecting are useful.
         *   Aborted, useless, and not-dragging states are not useful.
         * @param mode DragMode the DragMode to check
         * @return boolean true if accomplishing anything with this drag.  
         */
        protected final boolean isUsefulDragMode(DragMode mode) {
            return (mode == TableTopDragMode.GEMDRAGGING ||
                    mode == TableTopDragMode.CONNECTING ||
                    mode == TableTopDragMode.DISCONNECTING ||
                    mode == TableTopDragMode.SELECTING ||
                    mode == TableTopDragMode.CTRLDRAGGING);
        }

        /**
         * If the current dragmode is either GEMDRAGGING or CTRLDRAGGING,
         * then it is considered a 'gemdragging' action.
         * @return boolean
         */
        boolean isGemDragging() {
            return ((dragMode == TableTopDragMode.GEMDRAGGING) || (dragMode == TableTopDragMode.CTRLDRAGGING));
        }

        void setDragMode(DragMode mode) {
            if (mode == TableTopDragMode.CTRLDRAGGING) {
                setCursor(cloneGemCursor);
            } else {
                setCursor(null);
            }
            dragMode = mode;
        }

        /**
         * Add a gem to the tabletop if appropriate.
         * This should be called from mousePressed()
         * @return boolean true only if a gem was added to the tabletop
         */
        private boolean maybeAddGem() {

            // If we are adding a Gem, click position indicates the position of the Gem
            if (gemCutter.getGUIState() == GemCutter.GUIState.ADD_GEM) {

                // Tell the GemCutter where to add the gem
                DisplayedGem addingGem = gemCutter.getAddingDisplayedGem();
                if (addingGem != null) {
                    tableTop.doAddGemUserAction(addingGem, pressedAt);
                    
                } else {
                    DisplayedPart part = tableTop.getGemPartUnder(pressedAt);
                    boolean showedIntellicutForPart = false;
                    
                    // Check if the user clicked on a part and if we should start Intellicut for that.
                    if (part instanceof DisplayedPartConnectable) {
                        showedIntellicutForPart = tableTop.maybeStartIntellicutMode(part);
                    }
                    
                    // Just show Intellicut for the table top if we didn't show it for a part
                    if (!showedIntellicutForPart) {
                        
                        // If there was a part use it's bounds as the display rect. That way if the user clicks on
                        // a gem body part, the list wont obscure the part.
                        Rectangle displayRect = part != null ? part.getBounds() : new Rectangle(pressedAt);
                        
                        // Use the lower-right of the display rect as the drop point. That way if the user clicks
                        // a gem body part the new gem will appear next to the old one, not over it.
                        Point dropPoint = new Point(displayRect.x + displayRect.width, displayRect.y + displayRect.height);
                        
                        gemCutter.getIntellicutManager().startIntellicutModeForTableTop(displayRect, dropPoint);
                    }
                }
                
                // Back to edit mode
                gemCutter.enterGUIState(GemCutter.GUIState.EDIT);
                
                // Disallow further action (eg. drag)
                abortDrag();
                
                // We added a gem
                return true;
            }
            return false;
        }

        /**
         * Invoked when a mouse button is pressed on a component and then 
         * dragged.  Mouse drag events will continue to be delivered to
         * the component where the first originated until the mouse button is
         * released (regardless of whether the mouse position is within the
         * bounds of the component).
         */
        public void mouseDragged(MouseEvent e) {
            
            try {
                // If we're still painting, or we bailed out, do nothing.
                if (isPainting != 0 || dragMode == DragMode.ABORTED) {
                    return;
                }
                
                // If needed forward the mouse event to the vep.
                if (dragStartedOverVEP && valueEntryPanelHit(e.getPoint())) {
                    forwardMouseEvent (getValueEntryPanel((ValueGem)clickGem.getGem()), e);
                    return;
                }
                
                if (dragStartedOverVEP) {
                    return;
                }
                
                // Defer to the superclass method
                super.mouseDragged(e);

            } catch (Throwable t) {
                // some error occurred.  Treat this as an aborted drag.
                abortDrag();
                t.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * {@inheritDoc}
         */
        public void mouseExited(MouseEvent e) {
        }

        /**
         * Invoked when the mouse has been moved on a component
         * (with no buttons no down).
         */
        public void mouseMoved(MouseEvent e) {
            
            // Test if we are over a gem part
            DisplayedPart partUnder = tableTop.getGemPartUnder(e.getPoint());
            
            // If we are dragging over a VEP make sure to display the correct cursor.
            if (valueEntryPanelHit(e.getPoint())) {
                ValueEntryPanel vep = getValueEntryPanel((ValueGem)partUnder.getGem());
                Point vepPoint = SwingUtilities.convertPoint(TableTopPanel.this, e.getPoint(), vep);
                setCursor(vep.getCursor(vepPoint));
                
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
            
            // display some help maybe
            if (gemCutter.getGUIState() == GemCutter.GUIState.EDIT) {

                // unconnected connectable parts of a non-broken gem
                if (partUnder instanceof DisplayedPartConnectable &&                                // connectable
                    !((DisplayedPartConnectable)partUnder).getPartConnectable().isConnected() &&    // not connected
                    !((partUnder.getGem().getRootGem() != null) &&                                  // not ancestor of a broken forest
                      GemGraph.isAncestorOfBrokenGemForest(partUnder.getGem().getRootGem()))) {
                    
                    if (partUnder instanceof DisplayedPartInput) {
                        // double click to burn/unburn an unconnected input
                        gemCutter.getStatusMessageDisplayer().setMessageFromResource(TableTopPanel.this, "SM_DblClickBurn", StatusMessageDisplayer.MessageType.PERSISTENT);
                        
                    } else {
                        gemCutter.getStatusMessageDisplayer().clearMessage(TableTopPanel.this);
                    }

                } else {
                    gemCutter.getStatusMessageDisplayer().clearMessage(TableTopPanel.this);
                }

            } else {
                // not in edit mode
                gemCutter.getStatusMessageDisplayer().clearMessage(TableTopPanel.this);
            }
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         */
        public void mousePressed(MouseEvent e){
            try {
                // move focus to the tabletop
                requestFocus();

                dragStartedOverVEP = false;
                
                // A mousePress should stop Intellicut.
                IntellicutManager.IntellicutMode prevIntellicutMode = tableTop.getIntellicutManager().getIntellicutMode();
                tableTop.getIntellicutManager().stopIntellicut();

                // Ignore clicks unless we're editing or adding gems
                GemCutter.GUIState GUIState = gemCutter.getGUIState();
                if ((GUIState != GemCutter.GUIState.EDIT) && (GUIState != GemCutter.GUIState.ADD_GEM)) {
                    return;
                }
                
                // Call the superclass method
                super.mousePressed(e);
                
                // see if this resulted in an aborted drag
                if (dragMode == DragMode.ABORTED) {
                    return;
                }

                // add a gem if appropriate
                if (maybeAddGem()) {
                    return;
                }
                
                DisplayedPart partPressed = tableTop.getGemPartUnder(pressedAt);
                
                // Now do whatever based on what was pressed on
                mousePressedOn(e, partPressed, prevIntellicutMode);
                
                // Added for Linux compatibility (KDE popups are shown on mouse down)
                if (!isUsefulDragMode(dragMode)) {
                    maybeShowPopup(e);
                }

            } catch (Throwable t) {
                // some error occurred.  Treat this as an aborted drag.
                abortDrag();
                t.printStackTrace();
            }
        }

        /**
         * Take action based on what was pressed.
         * @param e the relevant event
         * @param partPressed the part which was pressed
         * @param prevIntellicutMode the intellicut mode before the press occurred
         */
        public void mousePressedOn(MouseEvent e, DisplayedPart partPressed, IntellicutManager.IntellicutMode prevIntellicutMode) {
            
            // Did they hit anything?
            if (partPressed != null) {

                if (partPressed instanceof DisplayedPartBody) {
                    // Make sure the popup menus for running gems are closed.  Their
                    // ability to alter focus and selection of gems on the TableTop
                    // interferes with the selection and focus shifting done here because 
                    // they don't actually close until after we are done.
                    gemCutter.closeRunPopupMenus();
                    
                    // Update the gem which was clicked on.
                    clickGem = partPressed.getDisplayedGem();
                    
                    // Selection state changes depend on keyboard modifiers
                    if (e.isControlDown() && e.isShiftDown()) {
                        
                        // Only worry about the left button here
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            
                            // If the selection anchor is null use the clicked Gem
                            if (shiftSelectionAnchorGem == null) {
                                shiftSelectionAnchorGem = clickGem;
                            }
                            
                            // Select all the Gems from the selection anchor to this Gem
                            // and update the focused Gem.  This selection set should union with
                            // any existing selection set.  DO NOT update the selection anchor
                            Rectangle2D rectangle = getRectangleForDisplayedGems(shiftSelectionAnchorGem, clickGem);
                            tableTop.selectGems(rectangle, SelectMode.SELECT);
                            tableTop.setFocusedDisplayedGem(clickGem);
                        } 
                        
                    } else if (e.isControlDown()) {
                        // Do nothing

                    } else if (e.isShiftDown()) {
                        
                        // Only worry about the left button on shift+presses here.  We'll have to 
                        // worry about the shift+right button  in the 'on clicking' event
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            
                            // If the shift selection anchor is null use the clicked Gem
                            if (shiftSelectionAnchorGem == null) {
                                shiftSelectionAnchorGem = clickGem;
                            }
                            
                            // Select all the Gems from the selection anchor to this Gem
                            // and update the focused Gem.  This new selection set should REPLACE
                            // any existing selection set.  DO NOT update the selection anchor
                            Rectangle2D rectangle = getRectangleForDisplayedGems(shiftSelectionAnchorGem, clickGem);
                            tableTop.selectGems(rectangle, SelectMode.REPLACE_SELECT);
                            tableTop.setFocusedDisplayedGem(clickGem);
                        } 
                        
                    } else {
                        // Gem is selected, all others deselected, unless the Gem
                        // is already selected (in which case this is a NOP for now - it will be handled
                        // by mouseReallyClicked() later on)
                        if (!tableTop.isSelected(clickGem)) {
                            
                            tableTop.selectDisplayedGem(clickGem, true);
                        }

                        // Give the Gem that was just clicked the focus and
                        // reset the selection anchor
                        tableTop.setFocusedDisplayedGem(clickGem);
                        shiftSelectionAnchorGem = clickGem;
                        
                        // If needed forward the mouse event to the vep.
                        if (valueEntryPanelHit(e.getPoint())) {
                            forwardMouseEvent (getValueEntryPanel((ValueGem)clickGem.getGem()), e);
                            dragStartedOverVEP = true;
                        }
                    }
                    
                    //Create the dragList of selected Gems
                    dragList = tableTop.getSelectedDisplayedGems();
                    
                } else if (partPressed instanceof DisplayedPartOutput) {

                    DisplayedPartOutput outPart = (DisplayedPartOutput)partPressed;
                    
                    // If we were in intellicut mode part sink, possibly auto connect it.
                    if (prevIntellicutMode == IntellicutManager.IntellicutMode.PART_INPUT &&
                            tableTop.handleIntellicutAutoConnectGesture(outPart)) {

                        // disable dragging if we autoconnected
                        setDragMode(DragMode.ABORTED);
                        return;
                    }
                    
                    // We could be starting a drag.  Adjust the apparent press point to be the point of the arrow
                    pressedAt = outPart.getConnectionPoint();
                    
                    // Set the source part
                    connectionDragAnchorPart = (DisplayedPartConnectable) partPressed;
                    
                } else if (partPressed instanceof DisplayedPartInput) {
                    
                    DisplayedPartInput sinkPart = (DisplayedPartInput)partPressed;
                    
                    // If we were in intellicut mode part source, possibly auto connect it.
                    if (prevIntellicutMode == IntellicutManager.IntellicutMode.PART_OUTPUT &&
                            tableTop.handleIntellicutAutoConnectGesture(sinkPart)) {

                        // disable dragging if we autoconnected
                        setDragMode(DragMode.ABORTED);
                        return;
                    }
                    
                    // We could be starting a drag.  Adjust the apparent press point to be the connection point
                    pressedAt = sinkPart.getConnectionPoint();
                    
                    // Set the source part
                    connectionDragAnchorPart = (DisplayedPartConnectable) partPressed;
                }
                
            } else {        
                // Nothing hit - deselect everything unless a meta key is pressed
                // Selection state changes depend on shift state
                if (!(e.isShiftDown() || e.isControlDown())) {
                    tableTop.selectDisplayedGem(null, false);
                    tableTop.setFocusedDisplayedGem(null);
                    shiftSelectionAnchorGem = null;
                }   
            }   
        }

        /**
         * Surrogate method for mouseClicked.  Called only when our definition of click occurs.
         * @param e MouseEvent the relevant event
         * @return boolean true if the click was a double click
         */
        public boolean mouseReallyClicked(MouseEvent e) {
            
            // call the superclass method
            boolean doubleClicked = super.mouseReallyClicked(e);

            // If needed forward the mouse event to the vep.
            if (valueEntryPanelHit(e.getPoint())) {
                forwardMouseEvent (getValueEntryPanel((ValueGem)clickGem.getGem()), e);
                return doubleClicked;
            }
            
            // Test if we hit any part of the gem
            DisplayedPart partClicked = tableTop.getGemPartUnder(e.getPoint());
            if (partClicked != null) {
                
                // if the user double left clicks on an input part, burn if appropriate
                if (SwingUtilities.isLeftMouseButton(e) && doubleClicked) {
                    tableTop.getBurnManager().handleBurnInputGesture(partClicked);
                }
                
                // Get the gem which was clicked on
                DisplayedGem displayedGemClicked = tableTop.getGemUnder(e.getPoint());
                Gem gemClicked = displayedGemClicked.getGem();

                // If the part clicked was a body part... could be one of several things to do
                if (partClicked instanceof DisplayedPartBody) {
                    
                    // If this is a double left click we want to open the editors for the CodeGem and CollectorGem.
                    // todoSN - should the ValueGem editor be given focus here?
                    if (doubleClicked && SwingUtilities.isLeftMouseButton(e)) {
                        
                        if (gemClicked instanceof CodeGem) {
                            tableTop.showCodeGemEditor((CodeGem)displayedGemClicked.getGem(), true);
                            
                        } else if (gemClicked instanceof CollectorGem) {
                            if (tableTop.isSelected(displayedGemClicked)) {
                                tableTop.displayLetNameEditor((CollectorGem)gemClicked);
                            }
                        } else if (gemClicked instanceof RecordFieldSelectionGem) {
                            Action action = getChangeRecordSelectionFieldAction(gemClicked);
                            if (action.isEnabled()) {
                                // action will only be enabled when RecordFieldSelection Gem can be edited,
                                // ie. when it is not connected to any broken gems
                                tableTop.displayRecordFieldSelectionEditor((RecordFieldSelectionGem)gemClicked);
                            }
                        } 
                        else if (gemClicked instanceof RecordCreationGem){
                            // find the field that was clicked on
                            int fieldIndex = displayedGemClicked.getDisplayedGemShape().inputNameTagHit(e.getPoint());

                            if (fieldIndex != -1) {
                                FieldName fieldToRename = ((RecordCreationGem)gemClicked).getFieldName(fieldIndex);
                                Action action = getRenameRecordFieldAction((RecordCreationGem)gemClicked, fieldToRename.getCalSourceForm());

                                if (action.isEnabled()) {

                                    tableTop.displayFieldRenameEditor((RecordCreationGem)gemClicked, fieldToRename);
                                }
                            }
                        }
                    }
                    
                    // Handle the single click cases
                    // NOTE: The CTRL+SHIFT modifiers work the same as just the SHIFT modifier so we
                    //        don't need to do anything special for the combination, however it is important
                    //        that the SHIFT portion of the if statement come before the CTRL portion so
                    //        that it gets executed when the SHIFT+CTRL combo is used.
                    if (e.isShiftDown()) {
                        
                        // Only worry about the right button here.  
                        if (SwingUtilities.isRightMouseButton(e)) {
                            // If the clicked gem is not selected then singleton select it, but do NOT give it focus.
                            if (!tableTop.isSelected(clickGem)) {
                                tableTop.selectDisplayedGem(clickGem, true);
                            }
                            
                            // todoSN - Right now we don't have a permanent focused Gem so if it is null here assign one
                            if (tableTop.getFocusedDisplayedGem() == null) {
                                tableTop.setFocusedDisplayedGem(clickGem);
                            }
                        }                            
                        
                    } else if (e.isControlDown()) {

                        // Worry about the left button here.  
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            // Toggle the selection state of this Gem, give focus to it and make it the shift selection anchor
                            tableTop.toggleSelected(clickGem);
                            tableTop.setFocusedDisplayedGem(clickGem);
                            shiftSelectionAnchorGem = clickGem;
                        }    
                        
                    } else {
                        // There were no modifiers!  Only worry about the left button here.  The right mouse
                        // button will trigger the context menu on the button release event.
                        if (SwingUtilities.isLeftMouseButton(e)) {

                            // When there is a selection set and the clicked Gem is in that set
                            // we need to un-select all but the clicked gem here.
                            DisplayedGem[] selectedGems = tableTop.getSelectedDisplayedGems();
                            
                            if (selectedGems != null && selectedGems.length > 1) {
                                for (final DisplayedGem dGem : selectedGems) {

                                    if (dGem != displayedGemClicked) {
                                        tableTop.selectDisplayedGem(dGem, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            return doubleClicked;
        }

        /**
         * Surrogate method for mouseDragged.  Called only when our definition of drag occurs.
         * Note that the setup for the transition from pressed to dragged is carried out in the mouseDragged() method.
         * @param e MouseEvent the relevant event
         * @param where Point the (possibly adjusted from e) coordinates of the drag
         * @param wasDragging boolean True: this is a continuation of a drag.  False: first call upon transition
         * from pressed to drag.
         */
        public void mouseReallyDragged(MouseEvent e, Point where, boolean wasDragging) {

            if (isGemDragging()) {
                
                // expand the tabletop if necessary.  This also translates "where" into the new coordinates.
                checkExpand(where);
                
                // Must do the target gem relocating here. Or else we will have repainting problems later.
                tableTop.checkTargetDockLocation();

                // update the drag position, making sure that the new point is visible.
                Rectangle visibleRect = new Rectangle(where);
                updateDragPosition(where, visibleRect, wasDragging);
                
            } else if (dragMode == TableTopDragMode.CONNECTING || dragMode == TableTopDragMode.DISCONNECTING) {
                
                // update the apparent click point in case the source moved (eg. the connected gem morphs)
                pressedAt = connectionDragAnchorPart.getConnectionPoint();

                // update the drag position, making sure that the new point is visible.
                Rectangle visibleRect = new Rectangle(where);
                updateDragPosition(where, visibleRect, wasDragging);
                
                // update the tabletop state to take into account the present drag position while connecting
                changeStateForConnecting(where);

                // undo autoburns if we're dragging away from an output
                DisplayedPart displayedPartUnder = tableTop.getGemPartUnder(where);
                Gem autoBurnLastGem = tableTop.getBurnManager().getAutoburnLastGem();
                if (dragMode == TableTopDragMode.DISCONNECTING && 
                        connectionDragAnchorPart instanceof DisplayedPartInput && 
                        autoBurnLastGem != null &&
                        (displayedPartUnder == null || displayedPartUnder.getGem() != autoBurnLastGem) &&
                        tableTop.getBurnManager().getAutoburnLastResult() == AutoburnLogic.AutoburnAction.BURNED) {
                    tableTop.getBurnManager().doUnburnAutomaticallyBurnedInputsUserAction(autoBurnLastGem);
                }

                // clear the status message
                gemCutter.getStatusMessageDisplayer().clearMessage(TableTopPanel.this);
                
            } else if (dragMode == TableTopDragMode.SELECTING) {
                
                // update the drag position, making sure that the new point is visible.
                Rectangle visibleRect = new Rectangle(where);
                updateDragPosition(where, visibleRect, wasDragging);

                // Determine the next drag sel mode
                lastSelectMode = getSelectModeForEvent(e);
                
            }
        }

        /**
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(MouseEvent e) {
            
            // show popup menu if appropriate
            if (!isUsefulDragMode(dragMode)) {
                maybeShowPopup(e);
            }
            
            // Ignore clicks unless we're editing
            if (gemCutter.getGUIState() != GemCutter.GUIState.EDIT) {
                return;
            }

            // If needed forward the mouse event to the vep.
            if (valueEntryPanelHit(e.getPoint())) {
                forwardMouseEvent (getValueEntryPanel((ValueGem)clickGem.getGem()), e);
            }

            try {
                // defer to the superclass method to do click/drag
                super.mouseReleased(e);         

            } finally {
                // Clear press/drag state
                resetMouseStates();

                // Restore focus if we took it away
                tableTop.restoreFocus();
                
                // resize the tabletop if necessary, to take into account any new gem state
                tableTop.resizeForGems();
                
                // if the menu was not to be shown due to a drag, we can show it again now
                // that the mouse has been released
                popupShouldShow = true;
            }
        }

        /**
         * Forward the given MouseEvent to the specified component by generating a new fake
         * event and posting it on the system event queue.
         * @param parent the parent component to forward the event to
         * @param e the MouseEvent to forward
         */
        private void forwardMouseEvent(Component parent, MouseEvent e) {

            popupShouldShow = false;
            
            EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
            Point newLocation = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), parent);
            Component newSource = parent.getComponentAt(newLocation);

            if (newSource == null) {
                // Happens if you click on the border of a component.
                return;
            }

            newLocation = SwingUtilities.convertPoint(parent, newLocation, newSource);
            
            queue.postEvent(new MouseEvent(newSource,
                    e.getID(),
                    System.currentTimeMillis(),
                    e.getModifiers(),
                    newLocation.x,
                    newLocation.y,
                    e.getClickCount(),
                    e.isPopupTrigger(),
                    e.getButton()));
        }
        
        /**
         * Determine if a value entry panel is under the given point.
         * @param point the click point
         * @return true if the value entry panel of a displayed value gem was hit.
         **/
        private boolean valueEntryPanelHit(Point point) {
            DisplayedGem displayedGem = tableTop.getGemUnder(point);
            
            return !isUsefulDragMode(getDragMode()) && 
                displayedGem != null &&
                displayedGem.getGem() instanceof ValueGem && 
                getValueEntryPanel((ValueGem)displayedGem.getGem()).getBounds().contains(point);
        }

        /**
         * Repaint the drag ghost (if any)
         */
        private void repaintDrag(Graphics2D g2d) {

//            class DragRepainter implements Runnable{
//
//                Graphics2D g2d;
//                RedrawInfo redrawInfo;
//
//                DragRepainter(Graphics2D g2d, RedrawInfo redrawInfo) {
//                    this.g2d = g2d;
//                    this.redrawInfo = redrawInfo;       // save a copy of the present redraw info
//                }
//                public void run() {
//                    // only repaint the drag connection line if there hasn't been another
//                    // drag ghost painted in the meantime (ie. redraw info hasn't been updated..)
//                    if (TableTopMouseHandler.this.redrawInfo == redrawInfo) {
//                        drawDragGhost(DrawAction.REDRAW, null, g2d);
//                    }
//                }
//            }

            // Say that the last drag ghost is in the undrawn state so that intervening drags will only
            // draw (rather than first undraw).
            lastDragClipArea = null;

            // Invoke later because we want other painting to finish before painting the drag.
            // Otherwise, for some reason (maybe because of double buffering?) the drag ghost disappears
            // when repaint returns.
            //SwingUtilities.invokeLater(new DragRepainter(g2d, redrawInfo));
            
        }

        /**
         * Reset mouse states
         */
        void resetMouseStates(){
            // Exit the pressed state
            pressedAt = null;
            
            // Dragging is finished.  Reset dragPos and dragMode
            dragPos = null;
            setDragMode(DragMode.NOTDRAGGING);

            // Reset the cursor
            setCursor(null);

            // invalidate autoburn attempt state
            tableTop.getBurnManager().invalidateAutoburnState();
        }

        /**
         * Update the state of the tabletop to reflect the new mouse drag position.
         * This takes care of undrawing and drawing old and new drag ghosts, as well as updating dragPos, and prevDragPos and
         * lastScrollDistanceX/Y if necessary.
         * @param newDragPos Point The new mouse drag position
         * @param visibleRect Rectangle The rectangle which we would like to see after the position update
         * @param wasDragging boolean if we were dragging before (and therefore must undraw the old drag ghost)
         */
        private void updateDragPosition(Point newDragPos, Rectangle visibleRect, boolean wasDragging) {
            
            // signal that we're painting
            isPainting++;
            
            // get the current graphics context
            Graphics2D g2d = (Graphics2D)getGraphics();
            
            // Turn off ghosts at last position.  
            // This is performed before scrollRect..() to avoid having a ghost in the middle of the screen.
            if (wasDragging){
                drawDragGhost(DrawAction.UNDRAW, SelectMode.REPLACE_SELECT, g2d);
            }

            // ensure the new ghost is visible      
            scrollRectToVisible(visibleRect);

            // This position is new drag position
            dragPos = newDragPos;
            
            // Turn on ghosts at this position
            g2d.setClip(getVisibleRect());
            drawDragGhost(DrawAction.DRAW, SelectMode.REPLACE_SELECT, g2d);
            
            // dispose our graphics object
            g2d.dispose();
            
            // we're no longer painting
            isPainting--;
        }
    }
    
    /**
     * Default constructor for this class.
     * @param tableTop
     * @param gemCutter
     */
    TableTopPanel(TableTop tableTop, GemCutter gemCutter) {
        
        this.tableTop = tableTop;
        this.gemCutter = gemCutter;
        this.valueGemPanelMap = new WeakHashMap<ValueGem, ValueEntryPanel>();
        this.gemPainter = new TableTopGemPainter(tableTop);
        this.isPainting = 0;

        // Register listeners for a number of event classes
        this.tableTopMouseHandler = new TableTopMouseHandler();
        this.runModeMouseHandler = new RunModeMouseHandler();
        
        addMouseListener(tableTopMouseHandler);
        addMouseMotionListener(tableTopMouseHandler);
        addComponentListener(new TableTopComponentListener());
        addKeyListener(new KeyStrokeHandler());
        
        // Register a drop target listener
        DropTargetListener dropTargetListener = new TableTopDragAndDropHandler();
        setDropTarget(new DropTarget(this, dropTargetListener));
        
        setToolTipText(GemCutter.getResourceString("TableTopToolTip"));
        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
    }
    
    
    /**
     * Enable or disable mouse events on the TableTop, allowing or disallowing gems from being moved.
     * @param notRunning whether to enable or disable mouse events
     */
    void enableMouseEvents(boolean notRunning) {
        if (notRunning) {
            removeMouseListener(runModeMouseHandler);
            addMouseListener(tableTopMouseHandler);
            addMouseMotionListener(tableTopMouseHandler);

        } else {
            addMouseListener(runModeMouseHandler);
            removeMouseListener(tableTopMouseHandler);
            removeMouseMotionListener(tableTopMouseHandler);
        }
    }
    
    /**
     * Handle the addition of a value gem to the tableTop.
     * This adds a value entry panel for use in editing the value for a value gem.
     * @param valueGem the value gem in question.
     */
    void handleValueGemAdded(ValueGem valueGem) {
        
        ValueEntryPanel valueEntryPanel = getValueEntryPanel(valueGem);
        
        // Initially not visible until the user selects the gem.
        valueEntryPanel.setVisible(false);
        
        // Update the position of the panel.
        updateValueGemPanelLocation(valueGem);

        // Add the panel to this component..
        TableTopPanel.this.add(valueEntryPanel);

        // Have to reset closing flag if the gem has been previously placed (eg. add gem, undo, add gem again).
        valueEntryPanel.setEditorIsClosing(false);

        // Add to the hierarchy manager.
        gemCutter.getValueEditorHierarchyManager().addTopValueEditor(valueEntryPanel);
        TableTopPanel.this.revalidate();
    }
    
    /**
     * Handle the removal of a value gem from the tableTop.
     * @param valueGem the value gem which was removed.
     */
    void handleValueGemRemoved(ValueGem valueGem) {
        gemCutter.getValueEditorHierarchyManager().removeValueEditor(getValueEntryPanel(valueGem), true);
    }
    
    /**
     * Handle the situation where a value gem was moved.
     * @param valueGem the value gem which was moved.
     */
    void handleValueGemMoved(ValueGem valueGem) {
        updateValueGemPanelLocation(valueGem);
    }
    
    /**
     * Update the location of the value gem's value entry panel to match the gem location.
     * @param valueGem the value gem whose panel should have its location updated.
     */
    private void updateValueGemPanelLocation(ValueGem valueGem) {
        Point currentLocation = tableTop.getDisplayedGem(valueGem).getLocation();
        int vepX = currentLocation.x + DisplayConstants.BEVEL_WIDTH_X + 1;
        int vepY = currentLocation.y + DisplayConstants.BEVEL_WIDTH_Y + 1;
        getValueEntryPanel(valueGem).setLocation(new Point(vepX, vepY));
    }
    
    /**
     * Get the value entry panel used to edit the value for a given value gem.
     * @param valueGem the value gem in question.
     * @return the value entry panel used to edit the value gem's value.
     */
    ValueEntryPanel getValueEntryPanel(final ValueGem valueGem) {

        // Lazily create the value panel on demand.
        if (!valueGemPanelMap.containsKey(valueGem)) {
            
            final ValueEditorHierarchyManager valueEditorHierarchyManager = gemCutter.getValueEditorHierarchyManager();
            ValueEditorManager valueEditorManager = valueEditorHierarchyManager.getValueEditorManager();
            ValueNode valueNode = valueGem.getValueNode();
    
            // Create the value entry panel.
            final ValueEntryPanel valueEntryPanel = 
                (ValueEntryPanel)valueEditorManager.getValueEditorDirector().getRootValueEditor(valueEditorHierarchyManager,
                                                                                                valueNode, null, 0, null);
            
            // Add it to the map.
            valueGemPanelMap.put(valueGem, valueEntryPanel);
            
            // add a listener to propagate changes in the value gem to the VEP.
            valueGem.addValueChangeListener(new ValueGemChangeListener() {
                public void valueChanged(ValueGemChangeEvent e) {
                    ValueGem valueGem = (ValueGem)e.getSource();
                    valueEditorHierarchyManager.collapseHierarchy(valueEntryPanel, false);
                    valueEntryPanel.changeOwnerValue(valueGem.getValueNode());
                    valueEntryPanel.setSize(valueEntryPanel.getPreferredSize());
                    valueEntryPanel.revalidate();
                }
            });
    
            // Set size of the panel.
            valueEntryPanel.setSize(valueEntryPanel.getPreferredSize());
            
            // Add a listener to propagate changes in the VEP to the value gem.
            valueEntryPanel.addValueEditorListener(new ValueEditorAdapter() {
                public void valueCommitted(ValueEditorEvent evt) {
                    ValueNode oldValue = evt.getOldValue();
                    ValueNode newValue = ((ValueEntryPanel)evt.getSource()).getValueNode();
    
                    if (!oldValue.sameValue(newValue)) {
                        valueGem.changeValue(newValue);
                    }
                }
            });
    
            // Add a listener so that a change in the size of the VEP will trigger a change the size of the displayed gem.
            valueEntryPanel.addComponentListener(new ComponentAdapter() {
    
                // change the size of the displayed value gem if the VEP is resized
                public void componentResized(ComponentEvent e) {
                    tableTop.getDisplayedGem(valueGem).sizeChanged();
                }
    
                // Re-position displayed gem if the VEP moves
                // This will happen if the VEP size changes (as a result of a type change), and its size is clamped to the parent's bounds.
                // eg. stick a new VEP on the right edge of the TableTop, and change its type to String.
                public void componentMoved(ComponentEvent e) {
                    Point vepLocation = valueEntryPanel.getLocation();
                    int newX = vepLocation.x - DisplayConstants.BEVEL_WIDTH_X - 1;
                    int newY = vepLocation.y - DisplayConstants.BEVEL_WIDTH_Y - 1;
    
                    Point newPoint = new Point(newX, newY);
                    tableTop.getDisplayedGem(valueGem).setLocation(newPoint);
                }
            });
    
            // Set the vep's context for type switching.
            valueEntryPanel.setContext(new ValueEditorContext() {
                public TypeExpr getLeastConstrainedTypeExpr() {
                    return tableTop.getGemGraph().getLeastConstrainedValueType(valueGem, tableTop.getTypeCheckInfo());
                }
            });
            
            // Set it up so that VEP commits are handled as user edits.
            valueEntryPanel.addValueEditorListener(new ValueEditorAdapter() {
                public void valueCommitted(ValueEditorEvent evt) {
                    tableTop.handleValueGemCommitted(valueGem, evt.getOldValue(), valueEntryPanel.getValueNode());
                }
            });
        }

        return valueGemPanelMap.get(valueGem);
    }
    
    /**
     * Sets whether or not the ValueGems should be enabled/editable.
     * @param enable true to enable, false to disable.
     */
    void setValueGemsEnabled(boolean enable) {
        Set<Gem> gemSet = tableTop.getGemGraph().getGems();
        
        for (final ValueGem valueGem : valueGemPanelMap.keySet()) {
            if (gemSet.contains(valueGem)) {
                ValueEntryPanel vep = getValueEntryPanel(valueGem);
                vep.setEditable(enable);

                // Make sure the value panels change colour accordingly.
                repaint(vep.getBounds());
            }
        }
    }
    
    /**
     * Revalidates the value gems on the tabletop after a connection
     * (eg. if you connect an add gem to 2 value gems, then you specialize one, 
     *   then this method will ensure that the other's appearance gets updated as well
     */
    void revalidateValueGemPanels() {
        Set<Gem> gemSet = tableTop.getGemGraph().getGems();
        
        for (final ValueGem valueGem : valueGemPanelMap.keySet()) {
            if (gemSet.contains(valueGem)) {
                ValueEntryPanel valueEntryPanel = getValueEntryPanel(valueGem);
                
                valueEntryPanel.refreshDisplay();
    
                // If a value gem is connected to a broken code gem, its least constrained type
                // will be null. In that case disable editing it.
                valueEntryPanel.setEditable(valueEntryPanel.getContext().getLeastConstrainedTypeExpr() != null);
    
                // Repaint the value gem ghost image to display the right value entry panel.
                repaint(tableTop.getDisplayedGem(valueGem).getBounds());
            }
        }
    }    

    /**
     * Hides the popup menu
     */
    void hidePopup() {
        if (currentPopupMenu != null) {
            currentPopupMenu.setVisible(false);
        }
    }
    
    /**
     * Sets the background for the tabletop
     * @param image
     */
    void setBackground(BufferedImage image) {
        this.backgroundImage = image;
        repaint();
    }

    /**
     * Paint the TableTop.
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        
        if (backgroundImage != null) {
            
            // Paint a tiled image          
            Rectangle bounds = g.getClipBounds();

            // Determine which tiled instances intersect with bounds and draw them
            int imageWidth = backgroundImage.getWidth();
            int imageHeight = backgroundImage.getHeight();
            backgroundImageOriginOffsetX = backgroundImageOriginOffsetX % imageWidth;
            backgroundImageOriginOffsetY = backgroundImageOriginOffsetY % imageHeight;

            if (backgroundImageOriginOffsetX > 0) {
                backgroundImageOriginOffsetX -= imageWidth;
            }
            if (backgroundImageOriginOffsetY > 0) {
                backgroundImageOriginOffsetY-=imageHeight;
            }
            
            int offsetX = (bounds.x / imageWidth) * imageWidth + backgroundImageOriginOffsetX;
            int offsetY = (bounds.y / imageHeight) * imageHeight + backgroundImageOriginOffsetY;

            for (int yRegistration = offsetY; yRegistration < bounds.y + bounds.height; yRegistration += imageHeight) {
                for (int xRegistration = offsetX; xRegistration < bounds.x + bounds.width; xRegistration += imageWidth) {
                    g.drawImage(backgroundImage, xRegistration, yRegistration, null);
                }
            }
            
        } else {
            // Just draw a plain background.
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Signal that we're still painting.
        isPainting++;

        // Get a Graphics2D object to paint some of the components with
        Graphics2D g2d = (Graphics2D) g;

        // Call the graph to paint itself into this graphics context
        // All Gems get painted, and the links established between their 'ports' (output and inputs)
        // Links will be colourised depending on their validity and type compatibility
        paintGemGraph(g2d);

        // paint the drag ghost
        tableTopMouseHandler.repaintDrag(g2d);

        // we're no longer painting
        isPainting--;
    }

    /**
     * Paint this GemGraph within the graphics context passed in.
     * @param g2d Graphics2D the graphics context
     */
    public void paintGemGraph(Graphics2D g2d) {
        
        // Paint each Gem in the 'graph', and the edges between them
        
        // Start with the connections
        for (final DisplayedConnection displayedConnection : tableTop.getDisplayedConnections()) {
            gemPainter.paintConnection(displayedConnection, g2d);
        }
        
        // Now the intellicut lines
        tableTop.getIntellicutManager().paintIntellicutLines(g2d);
        
        // Now the Gems themselves
        for (final DisplayedGem displayedGem : tableTop.getDisplayedGems()) {
            gemPainter.paintGem(displayedGem, g2d);
        }
    }

    /**
     * {@inheritDoc}
     * Overriden to spot when updates are forced and cause the overview to update.
     */
    public void repaint() { 
        super.repaint();
        
        // Update the overview
        gemCutter.getOverviewPanel().repaint();
    }
    
    /**
     * {@inheritDoc}
     * Overriden to spot when updates are forced and cause the overview to update.
     */
    public void repaint(Rectangle rect) {   
        super.repaint(rect);
        
        // Update the overview
        gemCutter.getOverviewPanel().repaint();
    }
    
    /**
     * Resets the state of the mouse handler, painting and VEP's
     */
    void resetState() {
        
        // reset mouse states (in case there was an exception)
        tableTopMouseHandler.resetMouseStates();
        
        // Remove children, in case there are any orphaned VEPs
        removeAll();
        
        // Painting stuff
        isPainting = 0;
        repaint();
    }
    
    /**
     * If the mouse event is a popup trigger and a popup menu should show, then this method
     * will display the appropriate popup menu for the location clicked on.
     * @param e the related mouse event
     */
    void maybeShowPopup(MouseEvent e) {
        
        if (!popupShouldShow || !e.isPopupTrigger()) {
            return;
        }
        
        if (gemCutter.getGUIState() == GUIState.RUN) {
            currentPopupMenu = getRunModePopupMenu();
            currentPopupMenu.show(this, e.getX(), e.getY());
            return;
        }

        currentPopupLocation = e.getPoint();
        
        DisplayedGem gem = tableTop.getGemUnder(currentPopupLocation);
        DisplayedPart part = tableTop.getGemPartUnder(currentPopupLocation);
        
        if (part instanceof DisplayedPartBody) {
            
            currentPopupMenu = getGemPopupMenu(gem, true);
            
        } else if (part instanceof DisplayedPartConnectable) {
            
            if (e.isControlDown()) {
                tableTop.maybeStartIntellicutMode(part);
            } else {
                currentPopupMenu = getGemPartPopupMenu((DisplayedPartConnectable) part, true);
            }
            
        } else if (part == null) {
            
            if (e.isControlDown()) {
                tableTop.getIntellicutManager().startIntellicutModeForTableTop(new Rectangle(currentPopupLocation), currentPopupLocation);
            } else {
                currentPopupMenu = getNonGemPopupMenu(true);
            }
            
        } else {
            throw new IllegalStateException("unknown part for popup menu");
        }
        
        currentPopupMenu.show(this, e.getX(), e.getY());
    }

    /**
     * @return the popup menu to show for the table top while the GemCutter is in run mode
     */
    JPopupMenu getRunModePopupMenu() {
        
        JPopupMenu runModePopupMenu = new JPopupMenu();
        
        runModePopupMenu.add(GemCutter.makeNewMenuItem(gemCutter.getResumeRunAction()));
        runModePopupMenu.add(GemCutter.makeNewMenuItem(gemCutter.getStopAction()));
        runModePopupMenu.add(GemCutter.makeNewMenuItem(gemCutter.getResetAction()));

        return runModePopupMenu;
    }
    
    /**
     * @return the JPopupMenu to show when the user right clicks on the table top, not on a gem or part
     * @param forTableTop whether the popup menu is for the table top and should include table top specific items
     * 
     * <BR><BR>todoFW: find a better way to share popup menu code with the TableTopExplorer
     */
    JPopupMenu getNonGemPopupMenu(boolean forTableTop) {
        
        JPopupMenu nonGemPopupMenu = new JPopupMenu();

        final JMenuItem addReflectorMenuItem = GemCutter.makeNewMenuItem(getAddReflectorAction());
        final JMenu addOtherReflectorMenu = GemCutter.makeNewMenu(GemCutter.getResourceString("PopItem_AddOtherReflector"));

        if (forTableTop) {
            nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getAddGemAction()));
        }
        
        nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getAddValueGemAction()));
        nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getAddCodeGemAction()));
        nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getAddCollectorAction()));
        nonGemPopupMenu.add(addReflectorMenuItem);
        nonGemPopupMenu.add(addOtherReflectorMenu);
        nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getAddRecordCreationGemAction()));
        nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getAddRecordFieldSelectionGemAction()));
        nonGemPopupMenu.addSeparator();
        nonGemPopupMenu.add(getCopySpecialMenu());
        nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getPasteGemsAction()));
        
        if (forTableTop) {
            nonGemPopupMenu.addSeparator();
            nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getTidyTableTopAction()));
            nonGemPopupMenu.add(GemCutter.makeNewMenuItem(getFitTableTopAction()));
        }
        
        // This listener enables/disables the add emitter item and resets the popup location
        nonGemPopupMenu.addPopupMenuListener(new PopupMenuListener() {
            
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                
                // Get the names of all the collectors.
                List<String> sortedReflectorNames = new ArrayList<String>();
                Map<String, CollectorGem> nameToCollectorMap = new HashMap<String, CollectorGem>();
                
                for (final Gem gem : gemCutter.getTableTop().getGemGraph().getCollectors()) {
                    CollectorGem collectorGem = (CollectorGem)gem;
                    nameToCollectorMap.put(collectorGem.getUnqualifiedName(), collectorGem);
                    sortedReflectorNames.add(collectorGem.getUnqualifiedName());
                }
                
                Collections.sort(sortedReflectorNames, String.CASE_INSENSITIVE_ORDER);
                
                // Update the text of the add reflector menu item
                CollectorGem reflectorCollector = gemCutter.getCollectorForAddingReflector();
                addReflectorMenuItem.setEnabled(reflectorCollector != null);
                
                if (reflectorCollector != null) {
                    addReflectorMenuItem.setText(GemCutter.getResourceString("PopItem_AddReflectorFor") + reflectorCollector.getUnqualifiedName());
                } else {
                    addReflectorMenuItem.setText(GemCutter.getResourceString("PopItem_AddReflector"));
                }
                addOtherReflectorMenu.removeAll();
                addOtherReflectorMenu.setEnabled(sortedReflectorNames.size() > 0);                
                for (final String reflectorName : sortedReflectorNames) {
                    reflectorCollector = nameToCollectorMap.get(reflectorName);
                    addOtherReflectorMenu.add(GemCutter.makeNewMenuItem(new AddReflectorAction(reflectorCollector)));
                }
            }
            
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        
        return nonGemPopupMenu;
    }
    
    /**
     * @return the "Copy Special" menu, identical to that of the gem cutter.
     */
    private JMenu getCopySpecialMenu() {
        JMenu originalMenu = gemCutter.getCopySpecialMenu();
        JMenu copySpecialMenu = GemCutter.makeNewMenu(originalMenu.getText());
        int items = originalMenu.getItemCount();
        for (int i = 0; i < items; i++) {
            copySpecialMenu.add(GemCutter.makeNewMenuItem(originalMenu.getItem(i).getAction()));
        }
        return copySpecialMenu;
    }

    /**
     * @param part the part that was clicked on
     * @param forTableTop whether the menu is for the table top and should include table top specific items
     * @return the JPopupMenu to show for the given part
     */
    JPopupMenu getGemPartPopupMenu(DisplayedPartConnectable part, boolean forTableTop) {
        
        JPopupMenu partPopupMenu = new JPopupMenu();
        JSeparator separator = new JSeparator();
        
        if (forTableTop) {
            partPopupMenu.add(GemCutter.makeNewMenuItem(getIntellicutAction(part)));
            partPopupMenu.add(separator);
        }
        
        if (part instanceof DisplayedPartInput) {
            DisplayedPartInput inputPart = (DisplayedPartInput) part;
            
            if (!inputPart.isConnected()) {
                partPopupMenu.add(GemCutter.makeNewMenuItem(getConnectValueGemAction(inputPart)));
                
                if (!(inputPart.getPartInput().getGem() instanceof CollectorGem)) {
                    
                    if (inputPart.getPartInput().isBurnt()) {
                        partPopupMenu.add(GemCutter.makeNewMenuItem(getUnburnAction(inputPart)));
                        
                    } else {
                        partPopupMenu.add(GemCutter.makeNewMenuItem(getBurnAction(inputPart)));
                    }
                }
            }
            
            // Create popup menu item for retargeting inputs.
            final JMenu retargetInputMenu = GemCutter.makeNewMenu(GemCutter.getResourceString("PopItem_RetargetInput"));
            partPopupMenu.add(retargetInputMenu);
            
            // Get the targetable collectors, enable the menu item if not empty and if the input is not connected.
            List<CollectorGem> targetableCollectorList = getTargetableCollectors(inputPart.getPartInput());
            retargetInputMenu.setEnabled(!targetableCollectorList.isEmpty() && !inputPart.isConnected());
            
            CollectorGem currentArgTarget = GemGraph.getInputArgumentTarget(inputPart.getPartInput());

            // Create submenu items for retargeting to an enclosing collector.
            for (final CollectorGem targetableCollector : targetableCollectorList) {
                
                // Make the new menu item.
                JMenuItem newMenuItem = GemCutter.makeNewMenuItem(getRetargetInputArgumentAction(inputPart.getPartInput(), targetableCollector));
                
                // Enable if its not already targeted to this collector.
                newMenuItem.setEnabled(targetableCollector != currentArgTarget);
                
                // Add the menu item.
                retargetInputMenu.add(newMenuItem);
            }

            
        } else if (part instanceof DisplayedPartOutput) {
            DisplayedPartOutput outputPart = (DisplayedPartOutput) part;
            
            if (!outputPart.isConnected()) {
                partPopupMenu.add(GemCutter.makeNewMenuItem(getConnectCollectorAction(outputPart)));
            }
        }
        
        if (part.isConnected()) {
            partPopupMenu.add(GemCutter.makeNewMenuItem(getDisconnectAction(part)));          
            partPopupMenu.add(GemCutter.makeNewMenuItem(getSplitConnectionAction(part)));
        }
        
        // If there is nothing after the separator then remove it.
        if (partPopupMenu.getComponentCount() == 2) {
            partPopupMenu.remove(separator);
        }
        
        return partPopupMenu;
    }
    
    /**
     * @param partInput an input
     * @return Map from Collector gem which can be targeted to the collector gem to which the collector
     *   at the root of the input's gem subtree must be retargeted if the input is retargeted to the first collector gem.
     */
    private List<CollectorGem> getTargetableCollectors(PartInput partInput) {
        
        // Get the collector gem at the root of the gem tree to which the input is connected.
        CollectorGem inputGemRoot = partInput.getGem().getRootCollectorGem();
        
        // Can't retarget if the root is not a collector gem.
        if (inputGemRoot == null) {
            return Collections.emptyList();
        }

        // The input can be targeted to the root gem, or any of its enclosing collectors.
        // So, the targetable collectors are the root, any collectors targetable by the root, and any collectors enclosing those.
        
        // We have to ignore the input under consideration, otherwise the call to getTargetableCollectors() will see that the argument 
        //   is targeting whatever it's targeting, and use that as a constraint.
        Set<CollectorGem> targetableCollectorSet = new HashSet<CollectorGem>();
        for (final CollectorGem targetableCollector  : getTargetableCollectors(inputGemRoot, null, partInput)) {
            targetableCollectorSet.addAll(GemGraph.obtainEnclosingCollectors(targetableCollector));
        }

        // The input can always be targeted at the root gem...
        targetableCollectorSet.add(inputGemRoot);
        
        
        // Convert to an array, then sort.
        // First, the target gem.  Then, all other collectors, ordered alphabetically by name.
        CollectorGem[] targetableCollectorArray = 
            targetableCollectorSet.toArray(new CollectorGem[targetableCollectorSet.size()]);
        
        final CollectorGem gemGraphTarget = tableTop.getGemGraph().getTargetCollector();
        Arrays.sort(targetableCollectorArray, new Comparator<CollectorGem>() {

            public int compare(CollectorGem o1, CollectorGem o2) {
                if (o1 == gemGraphTarget) {
                    return -1;
                }
                if (o2 == gemGraphTarget) {
                    return 1;
                }
                return o1.getUnqualifiedName().compareTo(o2.getUnqualifiedName());
            }
        });
        
        // Return as a list.
        return Arrays.asList(targetableCollectorArray);
    }
    
    /**
     * @param collectorGem a collector gem
     * @param collectorsToCheck if non-null, only the collectors in this set will be checked if they are targetable.
     *   If null, all collectors in the gem graph will be checked.
     * @param inputToIgnore the input to exclude from consideration, if any.  Null if none.
     * @return the collector gems to which the given collector gem may validly be targeted.
     */
    private List<CollectorGem> getTargetableCollectors(CollectorGem collectorGem, Set<CollectorGem> collectorsToCheck, PartInput inputToIgnore) {
        
        // Targetable collectors must satisfy a set of conditions:
        // Collectors:
        //   No circular collector dependencies.
        //   If there are any reflectors attached to the same subtree as reflectors for the retargeting collector gem,
        //     the collector at the root of the tree must be able to access the definitions for those reflectors, as well
        //     as the definition of the retargeting collector.  If there is no collector at the root of the tree,
        //     it must be possible to create a collector which can see all definitions.
        // Arguments:
        //   Arguments on dependee trees which target collectors at outer scopes must still be able to target those collectors after retarget.

        // Can't retarget the target gem.
        CollectorGem targetCollector = collectorGem.getTargetCollectorGem();
        if (targetCollector == null) {
            return Collections.emptyList();
        }
        
        //
        // Get root collectors for emitters for any collectors enclosed by the retargeting collector gem, 
        //   where the root collector encloses and is not equal to the collector being retargeted.
        //  ie. the collectors at the roots of the subtrees to which the emitters are connected, 
        //   if the root collector encloses and is not equal to the retargeting collector.
        // 
        Set<CollectorGem> collectorSet = tableTop.getGemGraph().getCollectors();
        Set<CollectorGem> enclosedCollectorGemReflectorStrictlyEnclosingRootCollectorSet = new HashSet<CollectorGem>();

        for (final Gem gem : collectorSet) {
            CollectorGem nextCollector = (CollectorGem)gem;

            if (collectorGem.enclosesCollector(nextCollector)) {
                for (final ReflectorGem reflectorGem : nextCollector.getReflectors()) {
                    CollectorGem rootCollectorGem = reflectorGem.getRootCollectorGem();
                    if (rootCollectorGem != null && rootCollectorGem != collectorGem && rootCollectorGem.enclosesCollector(collectorGem)) {
                        enclosedCollectorGemReflectorStrictlyEnclosingRootCollectorSet.add(rootCollectorGem);
                    }
                }
            }
        }

        //
        // Get the innermost enclosing collector which has targeting arguments from inputs which exist on subtrees whose root 
        //   collectors are enclosed by collectorGem.
        // This will be used to check the condition that arguments on dependee trees which target collectors at enclosing scopes 
        //   must still be able to target those collectors.
        //
        CollectorGem innermostEnclosingCollectorWithEnclosedTargetingArguments = null;

        enclosingCollectorLoop:
            for (CollectorGem enclosingCollector = targetCollector; enclosingCollector != null; enclosingCollector = enclosingCollector.getTargetCollectorGem()) {
                for (final PartInput targetArgument : enclosingCollector.getTargetArguments()) {
                    CollectorGem rootCollectorGem = targetArgument.getGem().getRootCollectorGem();

                    // Check for the input to ignore..
                    if (targetArgument == inputToIgnore) {
                        continue;
                    }

                    if (rootCollectorGem == null) {
                        // Shouldn't be able to retarget an input on a subtree not rooted in a collector.
                        GemCutter.CLIENT_LOGGER.log(Level.WARNING, "Targeting input has no root collector: " + targetArgument);
                        continue;
                    }

                    if (collectorGem.enclosesCollector(rootCollectorGem)) {
                        innermostEnclosingCollectorWithEnclosedTargetingArguments = enclosingCollector;
                        break enclosingCollectorLoop;
                    }
                }
            }
        
        // For each subtree, not rooted by a collector, which has a reflector whose 
        //   collector is enclosed by collectorGem, the set of targets of (non-collectorGem-enclosed) collectors for the other 
        //   reflectors on that subtree.
        //
        // If two collectors have the same target, then they are siblings.
        // 
        // This will be used to check the condition that:
        //   For each subtree not rooted in a collector, we must be able to create a collector for the root which can 
        //   see all the collectors for the reflectors in that subtree.
        Set<Set<CollectorGem>> nonCollectorGemEnclosedSameSubtreeReflectorCollectorParentSets = new HashSet<Set<CollectorGem>>();
        {
            // This could be faster...
            Set<Gem> uncollectedRoots = tableTop.getGemGraph().getRoots();
            uncollectedRoots.removeAll(collectorSet);

            for (final Gem uncollectedRoot : uncollectedRoots) { 
                // Get reflectors which exist on the same subtree as the collector's reflector.
                Set<ReflectorGem> subTreeReflectors = UnsafeCast.<Set<ReflectorGem>>unsafeCast(GemGraph.obtainSubTreeGems(uncollectedRoot, ReflectorGem.class));

                // Calculate the set of collectors for reflectors in the subtree, where the collectors are not 
                //   enclosed by collector gem.
                Set<CollectorGem> nonEnclosedReflectorCollectorParentSet = new HashSet<CollectorGem>();

                for (final ReflectorGem gem : subTreeReflectors) {
                    CollectorGem subtreeReflectorCollector = gem.getCollector();

                    if (collectorGem.enclosesCollector(subtreeReflectorCollector)) {
                        // Ensure the set is added to the set of collector sets only if there is a reflector collector which
                        //   is enclosed by collector gem.
                        nonCollectorGemEnclosedSameSubtreeReflectorCollectorParentSets.add(nonEnclosedReflectorCollectorParentSet);

                    } else {
                        // Add to the set of non-enclosed reflector collectors.
                        CollectorGem subtreeReflectorCollectorTarget = subtreeReflectorCollector.getTargetCollectorGem();
                        if (subtreeReflectorCollectorTarget != null) {
                            nonEnclosedReflectorCollectorParentSet.add(subtreeReflectorCollectorTarget);
                        }
                    }
                }
            }
            // Note that we may have added an empty set..
        }

        
        //
        // Iterate over the collectors in the set to check, checking each to see if it can be targeted.
        //
        if (collectorsToCheck == null) {
            collectorsToCheck = tableTop.getGemGraph().getCollectors();
        }
        Set<CollectorGem> targetableCollectors = new HashSet<CollectorGem>();

        targetableGemCandidateLoop : 
            for (final CollectorGem targetableCollectorCandidate : collectorsToCheck) {

                // Guard against circular collector dependencies: disallow retargeting to an enclosed collector (or itself).
                // Check that collectorGem is not an ancestor of targetableGemCandidate.
                if (collectorGem.enclosesCollector(targetableCollectorCandidate)) {
                    continue targetableGemCandidateLoop;
                }

                // Arguments on dependee trees which target collectors at outer scopes (wrt collectorGem) must still be able to target those collectors.
                // In practice, this means we only have to check that (the innermost enclosing collector which has targeting arguments which exist on 
                //   subtrees whose root collectors are enclosed by collectorGem) will still enclose collectorGem.
                if (innermostEnclosingCollectorWithEnclosedTargetingArguments != null &&
                        !innermostEnclosingCollectorWithEnclosedTargetingArguments.enclosesCollector(targetableCollectorCandidate)) {

                    continue targetableGemCandidateLoop;
                }

                // Reflectors for collectors enclosed by the retargeting collector gem must be visible to their collector root.
                // So: for a tree with a reflector for such a collector,
                //   if the collector root is also enclosed, relative visibility will not change by retargeting.
                //   if the collector root is not also enclosed, it must have targetableCollectorCandidate as an ancestor or sibling.

                for (final CollectorGem reflectorRootCollector : enclosedCollectorGemReflectorStrictlyEnclosingRootCollectorSet) {
                    boolean reflectorRootCollectorSeesCandidate = false;

                    // Check if they have the same parent.
                    if (reflectorRootCollector.getTargetCollectorGem() == targetCollector) {
                        reflectorRootCollectorSeesCandidate = true;
                    }

                    // Check if the candidate is an ancestor.
                    reflectorRootCollectorSeesCandidate |= targetableCollectorCandidate.enclosesCollector(reflectorRootCollector);

                    if (!reflectorRootCollectorSeesCandidate) {
                        // The targetable collector candidate isn't visible to the root of this tree.
                        continue targetableGemCandidateLoop;
                    }
                }
            
            // If there are any reflectors attached to the same subtree as reflectors for the retargeting collector gem,
            // the collector at the root of the tree must be able to access the definitions for those reflectors, as well
            // as the definition of the retargeting collector.
            // For subtrees rooted in collectors, the previous check is sufficient.
            // For subtrees not rooted in collectors, we check that there is a collector which can see both definitions.  ie. 
            //   If there is a reflector in the subtree whose collector is enclosed by collectorGem (or is collectorGem), 
            //     any reflector in the subtree whose collector is not enclosed by collectorGem must be a 
            //     sibling of collectorGem or its ancestors (before and) after retargeting.
            //   If there is no reflector in the subtree whose collector is enclosed by collectorGem, there is nothing 
            //     to worry about, since this means relative collector visibilities won't change as a result of the retargeting.
            //
            // For one collector to be visible to another, it must be the same collector, a sibling, an ancestor, 
            //   a sibling of an ancestor, or a child
            // This can be simplified to: its target must enclose the other collector.
            // So, in order to be able to define a collector which can see all the reflector definitions in an unrooted subtree,
            //   the parents of the reflectors' collectors must form an ancestor (enclosement) chain.
            // Since we know that the collectors enclosed by the collector to retarget are not changed with respect to each other,
            //   we only have to check that the collector to which to retarget is part of the ancestor chain.
            
                for (final Set<CollectorGem> nonCollectorGemEnclosedSameSubtreeReflectorCollectorParentSet : nonCollectorGemEnclosedSameSubtreeReflectorCollectorParentSets) {
                    CollectorGem candidateTarget = targetableCollectorCandidate.getTargetCollectorGem();

                    Set<CollectorGem> setToCheck = new HashSet<CollectorGem>(nonCollectorGemEnclosedSameSubtreeReflectorCollectorParentSet);
                    if (candidateTarget != null) {
                        setToCheck.add(candidateTarget);
                    }

                    if (!GemGraph.formsAncestorChain(setToCheck)) {
                        continue targetableGemCandidateLoop;
                    }
                }

                // If we're here, the targetable gem candidate satisfies all the above constraints.
                targetableCollectors.add(targetableCollectorCandidate);
            }

        //
        // Now put the collectors in order.
        // The target gem always comes first.  Then all other collectors, sorted by name.
        //
        
        // If the gem graph target is targetable, note this and remove.
        CollectorGem gemGraphTarget = tableTop.getGemGraph().getTargetCollector();
        boolean hasGemGraphTarget = targetableCollectors.remove(gemGraphTarget);

        // Order all other collectors by name.
        CollectorGem[] collectorsArray = targetableCollectors.toArray(new CollectorGem[targetableCollectors.size()]);
        Arrays.sort(collectorsArray, new Comparator<CollectorGem>() {
            public int compare(CollectorGem o1, CollectorGem o2) {
                return o1.getUnqualifiedName().compareTo(o2.getUnqualifiedName());
            }

        });

        // Compose the list.
        List<CollectorGem> targetableCollectorsList = new ArrayList<CollectorGem>(collectorsArray.length + (hasGemGraphTarget ? 1 : 0));
        if (hasGemGraphTarget) {
            targetableCollectorsList.add(gemGraphTarget);
        }
        targetableCollectorsList.addAll(Arrays.asList(collectorsArray));

        return targetableCollectorsList;
    }

    /**
     * @param displayedGem the gem to show the popup menu for
     * @param forTableTop whether the menu is for the table top and should include table top specific items
     * @return the JPopupMenu to show for the given gem
     */
    JPopupMenu getGemPopupMenu(DisplayedGem displayedGem, boolean forTableTop) {
        
        JPopupMenu gemPopupMenu = new JPopupMenu();

        gemPopupMenu.add(GemCutter.makeNewMenuItem(getRunGemAction(displayedGem)));
        gemPopupMenu.add(GemCutter.makeNewMenuItem(getDeleteGemsAction()));
        gemPopupMenu.addSeparator();
        gemPopupMenu.add(GemCutter.makeNewMenuItem(gemCutter.getCutAction()));
        gemPopupMenu.add(GemCutter.makeNewMenuItem(gemCutter.getCopyAction()));
        gemPopupMenu.add(getCopySpecialMenu());

        if (forTableTop) {
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getSelectSubTreeAction()));
            gemPopupMenu.addSeparator();
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getTidySelectionAction(displayedGem)));
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getTidySubTreeAction(displayedGem)));
        }
        
        Gem gem = displayedGem.getGem();
        
        if (gem instanceof FunctionalAgentGem) {
            gemPopupMenu.addSeparator();
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getViewPropertiesAction((FunctionalAgentGem)gem)));
            
        } else if (gem instanceof CodeGem) {
            gemPopupMenu.addSeparator();
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getRenameGemAction(gem)));
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getEditCodeGemAction((CodeGem)gem)));
        
        } else if (gem instanceof RecordFieldSelectionGem) {
            gemPopupMenu.addSeparator();
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getChangeRecordSelectionFieldAction(gem)));
            

        } else if (gem instanceof RecordCreationGem) {
            gemPopupMenu.addSeparator();

            // If a recordCreationGem is connected @ output, disable all menu items
            boolean shouldEnable = !gem.getOutputPart().isConnected();
            
            // ADD field menu item
            JMenuItem addRecordFieldMenu = GemCutter.makeNewMenuItem(getAddNewRecordFieldAction(gem));
            addRecordFieldMenu.setEnabled(shouldEnable);
            gemPopupMenu.add(addRecordFieldMenu);
            
            // get all the fields
            List<String> allFields = ((RecordCreationGem)gem).getCopyOfFieldsList();

            // DELETE field submenu 
            final JMenu deleteRecordFieldMenu = GemCutter.makeNewMenu(GemCutter.getResourceString("PopItem_DeleteRecordField"));
            gemPopupMenu.add(deleteRecordFieldMenu);

            List<String> deletableFields = ((RecordCreationGem)gem).getDeletableFields(tableTop);
            deleteRecordFieldMenu.setEnabled(shouldEnable);

            // RENAME field submenu
            final JMenu renameRecordFieldMenu = GemCutter.makeNewMenu(GemCutter.getResourceString("PopItem_RenameRecordField"));
            gemPopupMenu.add(renameRecordFieldMenu);
            
            List<String> renamableFields = ((RecordCreationGem)gem).getRenamableFields(tableTop);
            renameRecordFieldMenu.setEnabled(shouldEnable);

            // Add the submenu items
            for (final String field : allFields) {
                // DELETABLE fields for submenu items, enable only if the field is deletable
                JMenuItem deleteMenuItem = GemCutter.makeNewMenuItem(getDeleteRecordFieldAction((RecordCreationGem)gem, field));

                deleteMenuItem.setEnabled(deletableFields.contains(field));
                deleteRecordFieldMenu.add(deleteMenuItem);

                // RENAMABLE fields for submenu items enable only if the field is renamable
                JMenuItem renameMenuItem = GemCutter.makeNewMenuItem(getRenameRecordFieldAction((RecordCreationGem)gem, field));
                renameMenuItem.setEnabled(renamableFields.contains(field));
                renameRecordFieldMenu.add(renameMenuItem);

            } 

            // If disabled, set tool tips to indicate why
            if(!shouldEnable) {
                addRecordFieldMenu.setToolTipText(GemCutter.getResourceString("CannotModifyFields_tooltip", "add"));
                deleteRecordFieldMenu.setToolTipText(GemCutter.getResourceString("CannotModifyFields_tooltip", "delete"));
                renameRecordFieldMenu.setToolTipText(GemCutter.getResourceString("CannotModifyFields_tooltip", "rename"));
            }
            
        } else if (gem instanceof CollectorGem) {
            CollectorGem collectorGem = (CollectorGem)gem;
            CollectorGem targetCollector = collectorGem.getTargetCollectorGem();
            
            gemPopupMenu.addSeparator();
            
            
            // Create popup menu item for retargeting collectors.
            final JMenu retargetCollectorMenu = GemCutter.makeNewMenu(GemCutter.getResourceString("PopItem_RetargetCollector"));
            gemPopupMenu.add(retargetCollectorMenu);
            
            // Get the targetable collectors, enable the menu item if not empty.
            List<CollectorGem> targetableCollectors = getTargetableCollectors(collectorGem, null, null);
            retargetCollectorMenu.setEnabled(!targetableCollectors.isEmpty());
            
            // Create submenu items for retargeting to a collector.
            for (final CollectorGem targetableCollector : targetableCollectors) {
                // Make the new menu item.
                JMenuItem newMenuItem = GemCutter.makeNewMenuItem(getRetargetCollectorAction(collectorGem, targetableCollector));
                
                // Enable if its not already targeted to this collector.
                newMenuItem.setEnabled(targetableCollector != targetCollector);
                
                // Add the menu item.
                retargetCollectorMenu.add(newMenuItem);
            }
            
            // Create other menu items.
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getRenameGemAction(gem)));
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getAddReflectorAction(collectorGem)));
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getSaveGemAction(collectorGem)));
            gemPopupMenu.add(GemCutter.makeNewMenuItem(getEditPropertiesAction(collectorGem)));
        }
        
        return gemPopupMenu;
    }

    /**
     * @param part the part the action is for
     * @return the action to disconnect a connected gem part
     */    
    Action getDisconnectAction(final DisplayedPartConnectable part) {
        
        Action disconnectAction = new AbstractAction (GemCutter.getResourceString("PopItem_Disconnect")) {
            
            private static final long serialVersionUID = 2309882084879614674L;

            public void actionPerformed(ActionEvent evt) {
                Connection connection = part.getPartConnectable().getConnection();
                
                // Start the update now so that any auto-unburning is part of the same edit.
                tableTop.getUndoableEditSupport().beginUpdate();
                
                tableTop.handleDisconnectGesture(connection);
                
                if (part instanceof DisplayedPartOutput) {
                    tableTop.getBurnManager().doUnburnAutomaticallyBurnedInputsUserAction(part.getGem());
                } else {
                    tableTop.getBurnManager().doUnburnAutomaticallyBurnedInputsUserAction(connection.getSource().getGem());
                }

                tableTop.getUndoableEditSupport().setEditName(GemCutter.getResourceString("UndoText_DisconnectGems"));                
                tableTop.getUndoableEditSupport().endUpdate();
            }
        };
        
        disconnectAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_DISCONNECT));
        
        return disconnectAction;    
    }

    /**
     * @param part a displayed output or input part with the connection to be split
     * @return the action to split a connection into a collector / emitter pair
     */
    Action getSplitConnectionAction(final DisplayedPartConnectable part) {

        Action splitConnectionAction = new AbstractAction(GemCutter.getResourceString("PopItem_SplitConnection")) {

            private static final long serialVersionUID = 3698559866440806289L;

            public void actionPerformed(ActionEvent evt) {
                Connection connection = part.getPartConnectable().getConnection();
                tableTop.doSplitConnectionUserAction(connection);
            }
        };

        splitConnectionAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_SPLITCONNECTION));
        return splitConnectionAction;    
    }

    
    /**
     * @param partInput the part to connect a value gem to
     * @return a new Action for connection a ValueGem to the given part
     */
    Action getConnectValueGemAction(final DisplayedPartInput partInput) {

        Action connectAction = new AbstractAction(GemCutter.getResourceString("PopItem_ConnectValueGem"),
                new ImageIcon(getClass().getResource("/Resources/constant.gif"))) {
            
            private static final long serialVersionUID = 6931521030141991559L;

            public void actionPerformed(ActionEvent evt) {
                DisplayedGem dGem = tableTop.createDisplayedValueGem(new Point());
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
                
                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, partInput.getConnectionPoint());
                tidyAsConnected(dGem.getDisplayedOutputPart(), partInput, false);
                
                tableTop.handleConnectGemPartsGesture(dGem.getGem().getOutputPart(), partInput.getPartInput());

                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
            }                                              
        };

        // Check if a value gem can be connected here        
        DisplayedGem displayedValueGem = tableTop.createDisplayedValueGem(new Point());
        boolean connectable = GemGraph.isDefaultableValueGemSource(displayedValueGem.getDisplayedOutputPart().getPartOutput(), partInput.getPartInput(), gemCutter.getConnectionContext());
        connectAction.setEnabled(connectable);
        
        return connectAction;
    }

    /**
     * @param inputPart the input part whose argument is being retargeted.
     * @param targetableCollector the collector to which the argument is being retargeted.
     * @return a new Action to retarget the input's argument to the given collector.
     */
    Action getRetargetInputArgumentAction(final Gem.PartInput inputPart, final CollectorGem targetableCollector) {

        Action retargetInputArgumentAction = new AbstractAction(targetableCollector.getUnqualifiedName()) {
            
            private static final long serialVersionUID = 9119710886417993470L;

            public void actionPerformed(ActionEvent evt) {
                tableTop.handleRetargetInputArgumentGesture(inputPart, targetableCollector);
            }                                              
        };

        retargetInputArgumentAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_RETARGET_INPUT));
        
        return retargetInputArgumentAction;
    }
    
    /**
     * @param collectorGemToTarget the input part whose argument is being retargeted.
     * @param targetableCollector the collector to which the argument is being retargeted.
     * @return a new Action to retarget the input's argument to the given collector.
     */
    Action getRetargetCollectorAction(final CollectorGem collectorGemToTarget, final CollectorGem targetableCollector) {

        Action retargetInputArgumentAction = new AbstractAction(targetableCollector.getUnqualifiedName()) {
            
            private static final long serialVersionUID = -1932705179986107636L;

            public void actionPerformed(ActionEvent evt) {
                tableTop.handleRetargetCollectorGesture(collectorGemToTarget, targetableCollector);
            }                                              
        };

        retargetInputArgumentAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_RETARGET_COLLECTOR));
        
        return retargetInputArgumentAction;
    }
    
    /**
     * @param displayedPartOutput the part to connect a collector to
     * @return a new action for connecting a collector to the given part
     */
    Action getConnectCollectorAction(final DisplayedPartOutput displayedPartOutput) {

        Action connectAction = new AbstractAction(GemCutter.getResourceString("PopItem_ConnectCollector"),
                new ImageIcon(getClass().getResource("/Resources/collector.gif"))) {
            
            private static final long serialVersionUID = -7979201959532611095L;

            public void actionPerformed(ActionEvent evt) {
                // Calculate the target for the collector gem to create.
                // The friendliest thing to do is to minimize the depth of the collector, so this will be the 
                //   parent of the deepest collector among reflectors in the subtree.
                int targetingCollectorMaxDepth = 1;     // depth of collectors targeting collectorToTarget.
                CollectorGem collectorToTarget = tableTop.getTargetCollector();

                Set<ReflectorGem> subtreeReflectors = UnsafeCast.<Set<ReflectorGem>>unsafeCast(GemGraph.obtainSubTreeGems(displayedPartOutput.getGem(), ReflectorGem.class));
                
                for (final ReflectorGem subtreeReflector : subtreeReflectors) {
                    CollectorGem subtreeReflectorCollector = subtreeReflector.getCollector();
                    
                    int subtreeReflectorCollectorDepth = GemGraph.getCollectorDepth(subtreeReflectorCollector);
                    if (subtreeReflectorCollectorDepth > targetingCollectorMaxDepth) {
                        targetingCollectorMaxDepth = subtreeReflectorCollectorDepth;
                        collectorToTarget = subtreeReflectorCollector.getTargetCollectorGem();
                    }
                }
                
                // Now actually create, add, and connect the collector.
                DisplayedGem dGem = tableTop.createDisplayedCollectorGem(new Point(0, 0), collectorToTarget);
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
                
                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, displayedPartOutput.getConnectionPoint());
                tidyAsConnected(displayedPartOutput, dGem.getDisplayedInputPart(0), true);

                tableTop.handleConnectGemPartsGesture(displayedPartOutput.getPartOutput(), dGem.getGem().getInputPart(0));

                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
                
                tableTop.displayLetNameEditor((CollectorGem)dGem.getGem());
            }                                              
        };
        
        return connectAction;
    }
    
    /**
     * Tidy up two gems as though they were connected.
     * @param displayedPartOutput the output of one of the gems.
     * @param displayedPartInput the input of the other gem.
     * @param anchorOutput whether the output should be the anchor.  
     *   If false, the input will be the anchor for tidying (ie. will not move in the tidy operation).
     */
    private void tidyAsConnected(DisplayedPartOutput displayedPartOutput, DisplayedPartInput displayedPartInput, boolean anchorOutput) {

        // Save old connection info.
        DisplayedConnection oldOutputConnection = displayedPartOutput.getDisplayedConnection();
        DisplayedConnection oldInputConnection = displayedPartInput.getDisplayedConnection();

        // Create a temporary connection between the parts.
        DisplayedConnection tempConnection = new DisplayedConnection(displayedPartOutput, displayedPartInput);
        displayedPartInput.bindDisplayedConnection(tempConnection);
        displayedPartOutput.bindDisplayedConnection(tempConnection);
        
        // tidy the temporary connection..
        DisplayedGem[] displayedGems = {displayedPartInput.getDisplayedGem(), displayedPartOutput.getDisplayedGem()};
        Graph.LayoutArranger layoutArranger = new Graph.LayoutArranger(displayedGems);
        DisplayedPartConnectable anchorPart = anchorOutput ? (DisplayedPartConnectable)displayedPartOutput : displayedPartInput;
        tableTop.doTidyUserAction(layoutArranger, anchorPart.getDisplayedGem());
        
        // Restore the old connection info.
        displayedPartOutput.bindDisplayedConnection(oldOutputConnection);
        displayedPartInput.bindDisplayedConnection(oldInputConnection);
    }
    
    /**
     * Returns the action to burn a gem part.
     * @param part the gem part to burn
     * @return Action
     */
    Action getBurnAction(final DisplayedPartInput part) {

        Action burnAction = new AbstractAction (GemCutter.getResourceString("PopItem_Burn"),
                new ImageIcon(getClass().getResource("/Resources/burnMenuIcon.gif"))) {
            
            private static final long serialVersionUID = -4017522251797979086L;

            public void actionPerformed(ActionEvent evt) {
                tableTop.getBurnManager().handleBurnInputGesture(part);
            }
        };
        
        burnAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_BURN));
        
        return burnAction;        
    }

    /**
     * Returns the action to unburn a gem part.
     * @param part the gem part to burn
     * @return Action
     */
    Action getUnburnAction(final DisplayedPartInput part) {

        Action unburnAction = new AbstractAction (GemCutter.getResourceString("PopItem_Unburn"),
                new ImageIcon(getClass().getResource("/Resources/unburnMenuIcon.gif"))) {
            
            private static final long serialVersionUID = -1510333509310634733L;

            public void actionPerformed(ActionEvent evt) {
                tableTop.getBurnManager().handleBurnInputGesture(part);
            }
        };

        unburnAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_UNBURN));
        
        return unburnAction;        
    }
    
    /**
     * Returns the action that adds a value gem.
     * @return Action
     */
    private Action getAddValueGemAction() {
        
        Action addValueGemAction = new AbstractAction (GemCutter.getResourceString("PopItem_AddValueGem"),
                new ImageIcon(getClass().getResource("/Resources/constant.gif"))) {
            
            private static final long serialVersionUID = 6775526511235880852L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tableTop = gemCutter.getTableTop();
                DisplayedGem dGem = tableTop.createDisplayedValueGem(currentPopupLocation);
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
                
                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, currentPopupLocation);
                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
            }
        };
        
        addValueGemAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_ADD_VALUE_GEM));
        
        return addValueGemAction;
    }

    /**
     * Returns the action that adds a new gem (aka Intellicut).
     * @return Action
     */
    private Action getAddGemAction() {
        
        Action addGemAction = new AbstractAction (GemCutter.getResourceString("PopItem_AddGem"),
                new ImageIcon(getClass().getResource("/Resources/addNewGem.gif"))) {

            private static final long serialVersionUID = 2943954914796413469L;

            public void actionPerformed(ActionEvent evt) {
                gemCutter.getIntellicutManager().startIntellicutModeForTableTop(new Rectangle(currentPopupLocation), currentPopupLocation);
            }
        };
        
        addGemAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_INTELLICUT));
        addGemAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_INTELLICUT);
        
        return addGemAction;
    }

    /**
     * Returns the action that adds a code gem.
     * @return Action
     */
    private Action getAddCodeGemAction() {
        
        Action addCodeGemAction = new AbstractAction (GemCutter.getResourceString("PopItem_AddCodeGem"),
                new ImageIcon(getClass().getResource("/Resources/code.gif"))) {
            
            private static final long serialVersionUID = 9129526613956299918L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tableTop = gemCutter.getTableTop();
                DisplayedGem dGem = tableTop.createDisplayedCodeGem(currentPopupLocation);
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
                
                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, currentPopupLocation);
                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
            }
        };
        
        addCodeGemAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_ADD_CODE_GEM));
        
        return addCodeGemAction;
    }

    /**
     * Returns the action that adds a new collector.
     * @return Action
     */
    private Action getAddCollectorAction() {
        
        Action addCollectorAction = new AbstractAction (GemCutter.getResourceString("PopItem_AddCollector"),
                new ImageIcon(getClass().getResource("/Resources/collector.gif"))) {
            
            private static final long serialVersionUID = 6261574181526925026L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tableTop = gemCutter.getTableTop();
                DisplayedGem dGem = tableTop.createDisplayedCollectorGem(currentPopupLocation, tableTop.getTargetCollector());
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
                
                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, currentPopupLocation);
                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
                
                tableTop.displayLetNameEditor((CollectorGem)dGem.getGem());
            }
        };
        
        addCollectorAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_ADD_COLLECTOR_GEM));
        
        return addCollectorAction;
    }
    
    /**
     * Returns the action that adds a RecordFieldSelection gem.
     * @return Action
     */
    private Action getAddRecordFieldSelectionGemAction() {
        
        Action AddRecordFieldSelectionGemAction = new AbstractAction (GemCutter.getResourceString("PopItem_AddRecordFieldSelectionGem"),
                new ImageIcon(getClass().getResource("/Resources/recordFieldSelectionGem.gif"))) {
            
            private static final long serialVersionUID = -642626553800845519L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tableTop = gemCutter.getTableTop();
                DisplayedGem dGem = tableTop.createDisplayedRecordFieldSelectionGem(currentPopupLocation);
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
                
                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, currentPopupLocation);
                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
            }
        };
        
        AddRecordFieldSelectionGemAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_ADD_RECORD_FIELD_SELECTION_GEM));

        return AddRecordFieldSelectionGemAction;
    }

    /**
     * Returns the action that adds a new RecordCreationGem
     * @return Action
     */    
    private Action getAddRecordCreationGemAction() {

        Action AddRecordCreationGemAction = new AbstractAction (GemCutter.getResourceString("PopItem_AddRecordCreationGem"),
                new ImageIcon(getClass().getResource("/Resources/recordCreationGem.gif"))) {

            private static final long serialVersionUID = -1527976012193744771L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tableTop = gemCutter.getTableTop();
                DisplayedGem dGem = tableTop.createDisplayedRecordCreationGem(currentPopupLocation);
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();

                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, currentPopupLocation);
                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
            }
        };

        AddRecordCreationGemAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_ADD_RECORD_CREATION_GEM));

        return AddRecordCreationGemAction;
    }
    
    
    /**
     * Returns the action that adds a new reflector.
     * @return Action
     */
    private Action getAddReflectorAction() {
        
        Action addReflectorAction = new AbstractAction(GemCutter.getResourceString("PopItem_AddReflector"),
                new ImageIcon(getClass().getResource("/Resources/reflector.gif"))) {
            
            private static final long serialVersionUID = -1527976012193744771L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tableTop = gemCutter.getTableTop();
                DisplayedGem dGem = tableTop.createDisplayedReflectorGem(currentPopupLocation, gemCutter.getCollectorForAddingReflector());
                ExtendedUndoableEditSupport editSupport = tableTop.getUndoableEditSupport();
                
                editSupport.beginUpdate();
                tableTop.doAddGemUserAction(dGem, currentPopupLocation);
                editSupport.setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));
                editSupport.endUpdate();
            }
        };
        
        addReflectorAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_ADD_REFLECTOR_GEM));
        
        return addReflectorAction;
    }
    
    /**
     * Returns the action that tidies the table top.
     * @return Action
     */
    private Action getTidyTableTopAction() {
        
        Action tidyTableTopAction = new AbstractAction (GemCutter.getResourceString("ArrangeGraph")) {
            
            private static final long serialVersionUID = -5117623881659632367L;

            public void actionPerformed(ActionEvent evt) {
                
                tableTop.doTidyTableTopAction();
                repaint ();
            }
        };
        
        tidyTableTopAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_ARRANGE_GRAPH));
        tidyTableTopAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_ARRANGE_GRAPH);
        
        return tidyTableTopAction;
    }
    
    /**
     * Returns the action that fits the table top.
     * @return Action
     */
    private Action getFitTableTopAction() {
        
        Action fitTableTopAction = new AbstractAction (GemCutter.getResourceString("FitTableTop")) {
            
            private static final long serialVersionUID = 8996494637943723837L;

            public void actionPerformed(ActionEvent evt) {
                
                tableTop.doShrinkTableTopUserAction();
                repaint();
            }
        };
        
        fitTableTopAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_FIT_TABLETOP);
        fitTableTopAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_FIT_TABLETOP));
        
        return fitTableTopAction;
    }    
    
    /**
     * Returns the Intellicut popup menu action.
     * @return Action
     */
    private Action getIntellicutAction(final DisplayedPartConnectable part) {
        
        Action intellicutAction = new AbstractAction(GemCutter.getResourceString("PopItem_Intellicut"),
                new ImageIcon(getClass().getResource("/Resources/intellicut.gif"))) {
            
            private static final long serialVersionUID = 3589489782252599609L;

            public void actionPerformed(ActionEvent e) {
                
                if (part != null) {
                    tableTop.maybeStartIntellicutMode(part);
                } else {
                    tableTop.getIntellicutManager().startIntellicutModeForTableTop(new Rectangle(currentPopupLocation), currentPopupLocation);
                }
            }
        };
        
        boolean enabled = true;
        
        if (part != null) {

            boolean burnt = false;
            if (part instanceof DisplayedPartInput) {
                burnt = ((DisplayedPartInput) part).getPartInput().isBurnt();
            }
            
            enabled = part.getPartConnectable().getType() != null && !part.isConnected() && !burnt;
        }
        
        intellicutAction.setEnabled(enabled);
        intellicutAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_INTELLICUT);
        intellicutAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_INTELLICUT));
        
        return intellicutAction;
    }
    
    /**
     * @return the paste gems action
     */
    private Action getPasteGemsAction() {
        
        Action action = new AbstractAction(GemCutter.getResourceString("Paste"),
                new ImageIcon(GemCutter.class.getResource("/Resources/paste.gif"))) {

            private static final long serialVersionUID = -4338435545421989629L;

            public void actionPerformed(ActionEvent e) {
                pasteLocation = currentPopupLocation;
                gemCutter.pasteFromClipboard();
                pasteLocation = null;
            }
        };
        
        action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_PASTE));
        action.setEnabled(gemCutter.getPasteAction().isEnabled());
        
        return action;
    }
    
    /**
     * @param gem the gem the action is for
     * @return the run gem action
     */
    private Action getRunGemAction(final DisplayedGem gem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_RunGem"),
                new ImageIcon(GemCutter.class.getResource("/Resources/play.gif"))) {

            private static final long serialVersionUID = 8017728923330766355L;

            public void actionPerformed(ActionEvent e) {
                gemCutter.runTarget(gem);
            }
        };
        
        action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_RUN));
        action.setEnabled(gem.getGem().isRunnable());
        
        return action;
    }

    /**
     * @return the delete gems action
     */
    private Action getDeleteGemsAction() {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_DeleteGem")) {

            private static final long serialVersionUID = 6190720497415948604L;

            public void actionPerformed(ActionEvent e) {
                Set<Gem> selectedGems = new HashSet<Gem>(Arrays.asList(tableTop.getSelectedGems()));
                tableTop.handleDeleteGemsGesture(selectedGems);
            }
        };
        
        action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_DELETE));
        
        Gem[] selectedGems = tableTop.getSelectedGems();
        if (selectedGems.length > 1) {
            action.putValue(Action.NAME, GemCutter.getResourceString("PopItem_DeleteGems"));
        } else {
            action.putValue(Action.NAME, GemCutter.getResourceString("PopItem_DeleteGem"));

            boolean selectedTargetOnly = selectedGems.length != 0 && selectedGems[0] == tableTop.getTargetCollector();
            action.setEnabled(!selectedTargetOnly);
        }
        
        return action;
    }
    
    /**
     * @return the select gem subtree action
     */
    private Action getSelectSubTreeAction() {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_SelectSubTree")) {

            private static final long serialVersionUID = 5395741198879471469L;

            public void actionPerformed(ActionEvent e) {
                tableTop.doSelectSubtreeUser(tableTop.getSelectedGems());
            }
        };
        
        return action;
    }
    
    /**
     * @param gem the gem the action is for
     * @return the tidy selection action
     */
    private Action getTidySelectionAction(final DisplayedGem gem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_TidySelection")) {

            private static final long serialVersionUID = -8127271684299167786L;

            public void actionPerformed(ActionEvent e) {
                tableTop.doTidyUserAction(new Graph.LayoutArranger(tableTop.getSelectedDisplayedGems()), gem);
            }
        };
        
        action.setEnabled(tableTop.getSelectedDisplayedGems().length > 1);
        
        return action;
    }

    /**
     * @param gem the gem the action is for
     * @return the tidy sub trees action
     */
    private Action getTidySubTreeAction(final DisplayedGem gem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_TidySubtrees")) {

            private static final long serialVersionUID = 7701234830348136274L;

            public void actionPerformed(ActionEvent e) {
                Gem[] gems = tableTop.getSelectedGems();
                List<DisplayedGem> displayedGems = tableTop.doSelectSubtreeUser(gems);
                DisplayedGem[] validNodes = new DisplayedGem[displayedGems.size()];
                tableTop.doTidyUserAction(new Graph.LayoutArranger(displayedGems.toArray(validNodes)), gem);
            }
        };
        
        // Only enable the action if an input is actually connected.
        boolean enabled = false;
        
        Gem.PartInput[] inputs = gem.getGem().getInputParts();
        for (final PartInput input : inputs) {
            if (input.isConnected()) {
                enabled = true;
                break;
            }
        }

        action.setEnabled(enabled);
        
        return action;
    }

    /**
     * @param collectorGem the gem the action is for
     * @return the save gem action
     */
    private Action getSaveGemAction(final CollectorGem collectorGem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_SaveGem"),
                new ImageIcon(GemCutter.class.getResource("/Resources/save.gif"))) {

            private static final long serialVersionUID = -5967804767355281406L;

            public void actionPerformed(ActionEvent e) {
                gemCutter.saveGem();
            }
        };
        
        action.setEnabled(collectorGem.isRunnable());
        
        return action;
    }
    
    /**
     * @param gem the gem the action is for
     * @return the rename gem action
     */
    private Action getRenameGemAction(final Gem gem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_RenameGem")) {

            private static final long serialVersionUID = -1805921578643387361L;

            public void actionPerformed(ActionEvent e) {
                if (gem instanceof CodeGem) {
                    tableTop.displayCodeNameEditor((CodeGem)gem);
                } else if (gem instanceof CollectorGem) {
                    tableTop.displayLetNameEditor((CollectorGem)gem);
                } else {
                    throw new IllegalArgumentException("can only rename code or collector gems");
                }
            }
        };
        
        return action;
    }
    
    /**
     * Gets the action for changing the record field to be extracted.
     * @param gem the gem the action is for
     * @return the change extracted field action
     */
    private Action getChangeRecordSelectionFieldAction(final Gem gem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_ChangeSelectedField")) {
            
            private static final long serialVersionUID = 2446643701742147405L;

            public void actionPerformed(ActionEvent e) {
                if (gem instanceof RecordFieldSelectionGem) {
                    tableTop.displayRecordFieldSelectionEditor((RecordFieldSelectionGem)gem);
                } else {
                    throw new IllegalArgumentException("can only change extracted field on RecordFieldSelection gems");
                }
            }
        };
        
        if (gem instanceof RecordFieldSelectionGem) {
            //prevent editing of the gem when the tree is broken as it could lead to a more inconsistent state
            if ( GemGraph.isAncestorOfBrokenGemForest(gem.getRootGem())) {
                action.setEnabled(false);
            }
        }
        
        return action;
    }
    
    
    /**
     * Gets the action for renaming a record field
     * @param gem the gem the action is for
     * @return the renaming field action
     */
    private Action getRenameRecordFieldAction(final Gem gem, final String fieldToRename) {

        Action action = new AbstractAction(fieldToRename.toString()) {
            private static final long serialVersionUID = 806578875701283074L;

            public void actionPerformed(ActionEvent e) {
                if (gem instanceof RecordCreationGem) {
                    tableTop.displayFieldRenameEditor((RecordCreationGem)gem, FieldName.make(fieldToRename));
                } else {
                    throw new IllegalArgumentException("Can only rename field name on a RecordCreationGem");
                }
            }
        };

        if (gem instanceof RecordCreationGem) {
            //prevent editing of the gem when the output is connected
            if (gem.getOutputPart().isConnected()) {
                action.setEnabled(false);
                
            }
        }
        
        return action;
    }
    
  
    /**
     * Gets the action for adding a new record field
     * @param gem the gem the action is for
     * @return the adding new record field action
     */
    private Action getAddNewRecordFieldAction(final Gem gem) {

        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_AddNewRecordField")) {
            private static final long serialVersionUID = -1676013303676395830L;

            public void actionPerformed(ActionEvent e) {
                if (gem instanceof RecordCreationGem) {
                    tableTop.doAddRecordFieldUserAction((RecordCreationGem)gem);

                } else {
                    throw new IllegalArgumentException("can only add new field on RecordCreation gems");
                }
            }
        };
        
        return action;
    }
    
    
    /**
     * Gets the action for deleting an existing record field
     * @param gem the gem the action is for
     * @param fieldToDelete the field to be deleted
     * @return the deleting existing field action
     */
    private Action getDeleteRecordFieldAction(final Gem gem, final String fieldToDelete) {

        Action action = new AbstractAction(fieldToDelete.toString()) {
            private static final long serialVersionUID = -959693970346246044L;

            public void actionPerformed(ActionEvent e) {
                if (gem instanceof RecordCreationGem) {
                    tableTop.doDeleteRecordFieldUserAction((RecordCreationGem)gem, fieldToDelete);
                }
            }
        };

        return action;
    }
    
    
    
    /**
     * @param collectorGem the collector gem the action is for
     * @return the add reflector gem action
     */
    private Action getAddReflectorAction(final CollectorGem collectorGem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_AddReflector"),
                new ImageIcon(GemCutter.class.getResource("/Resources/reflector.gif"))) {

            private static final long serialVersionUID = 6212576237546675112L;

            public void actionPerformed(ActionEvent e) {
                DisplayedGem eGem = tableTop.createDisplayedReflectorGem(new Point(10, 10), collectorGem);
                gemCutter.setAddingDisplayedGem(eGem);
                gemCutter.enterGUIState(GemCutter.GUIState.ADD_GEM);
            }
        };
        
        return action;
    }
    
    /**
     * @param collectorGem the collector gem the action is for
     * @return the edit properties action
     */
    private Action getEditPropertiesAction(final CollectorGem collectorGem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_EditGemProperties"),
                new ImageIcon(GemCutter.class.getResource("/Resources/nav_edit.gif"))) {

            private static final long serialVersionUID = -7376813996995048934L;

            public void actionPerformed(ActionEvent e) {
                gemCutter.getNavigatorOwner().editMetadata(collectorGem);
            }
        };
        
        action.setEnabled(collectorGem.isConnected());
        
        return action;
    }

    /**
     * @param faGem the functional agent gem the action is for
     * @return the view properties action
     */
    private Action getViewPropertiesAction(final FunctionalAgentGem faGem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_ViewGemProperties")) {

            private static final long serialVersionUID = 945262478582661871L;

            public void actionPerformed(ActionEvent e) {
                GemEntity gemEntity = faGem.getGemEntity();
                gemCutter.getNavigatorOwner().displayMetadata(gemEntity, true);
            }
        };
        
        return action;
    }
    
    /**
     * @param codeGem the code gem the action is for
     * @return the edit code gem action
     */
    private Action getEditCodeGemAction(final CodeGem codeGem) {
        
        Action action = new AbstractAction(GemCutter.getResourceString("PopItem_OpenCodeEditor"),
                new ImageIcon(GemCutter.class.getResource("/Resources/selectedCodeEditorOpen.gif"))) {

            private static final long serialVersionUID = 1866715362157650129L;

            public void actionPerformed(ActionEvent e) {
                tableTop.showCodeGemEditor(codeGem, !tableTop.isCodeEditorVisible(codeGem));
            }
        };
        
        if (tableTop.isCodeEditorVisible(codeGem)) {
            action.putValue(Action.NAME, GemCutter.getResourceString("PopItem_CloseCodeEditor"));
        } else {
            action.putValue(Action.NAME, GemCutter.getResourceString("PopItem_OpenCodeEditor"));
        }
        
        return action;
    }        
    
    /**
     * This will display the intellicut menu by the currently selected gem 
     * or if no gem is selected in the top-left of the table top. This is called
     * by the GemCutter if the user presses the intellicut keyboard shortcut.
     */
    void displayIntellicut() {
        
        DisplayedGem target = tableTop.getFocusedDisplayedGem();
        Rectangle menuLocation = null;
        
        if (target == null) {
            Rectangle visible = getVisibleRect();
            menuLocation = new Rectangle(visible.x + 10, visible.y + 10);
        } else {
            menuLocation = target.getBounds();
        }
        
        tableTop.getIntellicutManager().startIntellicutModeForTableTop(menuLocation);
    }
    
    /**
     * Get the tooltip text when over the TableTop.
     * @return the tooltip text to display
     * @param mouseEvent where we are now
     */
    public String getToolTipText(MouseEvent mouseEvent) {
        // The toolTip to return (default is no tool tip).
        String toolTip = null;
        
        // Where are we now?
        Point where = mouseEvent.getPoint();
        
        // Check for tooltip hotspots   
        DisplayedPart displayedPart = tableTop.getGemPartUnder(where);

        // The naming policy to use for tooltips
        ScopedEntityNamingPolicy namingPolicy;
        ModuleTypeInfo currentModuleTypeInfo = tableTop.getCurrentModuleTypeInfo();
        if (currentModuleTypeInfo == null) {
            namingPolicy = ScopedEntityNamingPolicy.FULLY_QUALIFIED;
        } else {
            namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(currentModuleTypeInfo);
        }

        // If it is an input or an output, then we deal with it here.
        if (displayedPart != null && displayedPart instanceof DisplayedPartConnectable) {
            PartConnectable part = ((DisplayedPartConnectable)displayedPart).getPartConnectable();
            toolTip = ToolTipHelpers.getPartToolTip(part, tableTop.getGemGraph(), namingPolicy, this);
            
        } else if (displayedPart instanceof DisplayedPartBody) {
            
            Gem gem = ((DisplayedPartBody) displayedPart).getGem();
            
            if (gem instanceof FunctionalAgentGem) {
                toolTip = ToolTipHelpers.getFunctionalAgentToolTip((FunctionalAgentGem) gem, this, GemCutter.getLocaleFromPreferences());

            } else if (gem instanceof CollectorGem) {
                CollectorGem collectorGem = (CollectorGem)gem;
                CollectorGem targetGem = collectorGem.getTargetCollectorGem();
                String targetGemString = (targetGem == null) ? GemCutterMessages.getString("NullCollectorTarget") : targetGem.getUnqualifiedName();
                
                StringBuilder text = new StringBuilder("<html>");
                text.append(GemCutterMessages.getString("CollectorTargetToolTip", targetGemString));
                text.append("<br>");
                text.append(GemCutterMessages.getString("ResultTypeToolTip", tableTop.getGemGraph().getTypeString(gem.getResultType(), namingPolicy)));
                text.append("</html>");
                toolTip = text.toString();

            } else if (gem instanceof ValueGem) {
                ValueEntryPanel vep = getValueEntryPanel((ValueGem)gem);
                Point vepPoint = SwingUtilities.convertPoint(this, where, vep);
                toolTip = vep.getToolTipText(vepPoint);
            }
        }
        
        return toolTip; 
    }
    
    /**
     * returns the gempainter used to paint the gem graph
     * @return TableTopGemPainter
     */
    TableTopGemPainter getGemPainter() { 
        return gemPainter;
    }
    
    /**
     * Returns the preferredSize of a JViewport whose view is this Scrollable
     * @return Dimension
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Returns the "block" increment for scrolling in the specified direction.
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
    }

    /**
     * Return true if a viewport should always force the height of this Scrollable to match the height of the viewport. 
     * Currently, always returns false unless this scrollable's height is less than the Viewport's height.
     */
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
        }
        
        return false;
    }

    /**
     * Return true if a viewport should always force the width of this Scrollable to match the width of the viewport. 
     * Currently, always returns false unless this scrollable's width is less than the Viewport's width.
     */
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
        }
        
        return false;
    }

    /**
     * Returns the "unit" increment for scrolling in the specified direction.
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        // TEMP: Gotta figure out an optimal unit increment.
        return 30;
    }
    
    /**
     * @return the location at which new gems should be pasted if paste was invoked form a popup menu
     * This will be null if paste was not invoked from a popup menu and gems should be pasted at a default
     * location. 
     */
    Point getPasteLocation(){
        return pasteLocation;
    }
    
    /**
     * Return the rectangle formed by the two specified Gems.
     * The rectangle is defined by the centre points of the two Gems.
     * @param fromGem DisplayedGem - the Gem that is the origin of the rectangle
     * @param toGem DisplayedGem - the Gem that closes the rectangle
     * @return Rectangle
     */
    private static Rectangle2D getRectangleForDisplayedGems(DisplayedGem fromGem, DisplayedGem toGem) {
        Point2D fromGemCentrePoint = fromGem.getCenterPoint();
        Rectangle2D rect = new Rectangle2D.Double(fromGemCentrePoint.getX(), fromGemCentrePoint.getY(), 0, 0);
        rect.add(toGem.getCenterPoint());

        return rect;
    }

    /**
     * returns whether tabletop is in photolook.
     * @return boolean
     */
    boolean isPhotoLook() { 
        return gemCutter.isPhotoLook();
    }
}
