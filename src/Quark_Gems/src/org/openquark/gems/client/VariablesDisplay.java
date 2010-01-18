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
 * VariablesDisplay.java
 * Creation date: (09/07/01 1:37:20 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import org.openquark.gems.client.caleditor.AdvancedCALEditor;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;



/**
 * The JPanel displaying the free variables within a code gem editor
 * @author Iulian Radu
 */
public class VariablesDisplay extends JPanel {
    
    private static final long serialVersionUID = -959269253038046741L;

    /**
     * Interface for a listener which wishes to be informed of various panel events
     * Creation date: (Jul 15, 2002 3:20:12 PM)
     * @author Edward Lam
     */
    public interface PanelEventListener {

        /**
         * Notify the listener that a panel shifted to another position.
         * @param argIndex the index of the shifted panel.
         * @param shiftAmount the amount by which the panel was shifted.
         * +ve numbers increase its index, -ve numbers decrease it.
         */
        public void panelShifted(int argIndex, int shiftAmount);

        /**
         * Notify the listener that a panel's type icon was double-clicked.
         * @param variablePanel the variable panel that was double-clicked.
         */
        public void panelTypeIconDoubleClicked(VariablePanel variablePanel);
    }
    
    /**
     * The JList Component that displays the variables.
     * Creation date: (09/07/01 1:37:20 PM)
     * @author Edward Lam
     */
    class VariablesDisplayList extends JList {

        private static final long serialVersionUID = -3427641675953452151L;
        /** Reference to a MouseHandler */
        private final MouseHandler mouseHandler;
        
        /**
         * A trivial implementation of ListCellRenderer.
         * Creation date: (04/07/2001 3:57:15 PM)
         * @author Edward Lam
         */
        public class VariablesDisplayRenderer implements ListCellRenderer {
            
            /**
             * Return an object that can paint a VariablePanel (ie. itself).
             * @param list Jlist - The JList we're painting.
             * @param value Object - The value returned by list.getModel().getElementAt(index).
             * @param index int - The cell's index.
             * @param isSelected boolean - True if the specified cell was selected.
             * @param cellHasFocus boolean - True if the specified cell has the focus.
             * @return Component the component capable of rendering the list item
             */
            public Component getListCellRendererComponent(JList list, Object value, int index, 
                                                          boolean isSelected, boolean cellHasFocus) {
                // the list objects should all be VariablePanel objects
                VariablePanel varPan = (VariablePanel)value;

                varPan.setSelected(isSelected);

                // gray out the variable panel if it's disabled
                varPan.setGrayed(!varPan.isEnabled());
                
                return varPan;
            }
        }

        /**
         * Inner class to handle mouse and drag&drop events on the Variables Display.
         * 
         * Reordering of panels within this display is handled by this class, while 
         * item transfer between the display and external components is directed to
         * native drag & drop, making use of transfer handlers.
         *  
         */
        private class MouseHandler extends MouseClickDragAdapter implements DropTargetListener {
            
            /** The index of the variable panel clicked, if any. */
            private int panelNumClicked;

            /** The position of the separator between panels. */
            private int separatorPosition = -1;

            /**
             * Constructor for the Mouse Handler
             */
            private MouseHandler() {
            }

            /**
             * Move the drag mode into the aborted state.
             */
            @Override
            protected void abortDrag() {
                super.abortDrag();
                clearSelection();
                panelClicked = null;
                exitDragState(null);
                getDropTarget().getDropTargetContext().dropComplete(false);
            }

            /**
             * Invoked when a mouse button has been pressed on a component.
             */
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                panelClicked = getClickedPanel(e);
                // clear the selection if we clicked on a blank area
                if (panelClicked == null) {
                    clearSelection();
                }
                maybeShowPopup(e);
            }
            
            /**
             * If mouse event triggers popup, show it
             * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }
            
            /**
             * Surrogate method for mouseClicked.  Called only when our definition of click occurs.
             * @param e MouseEvent the relevant event
             * @return boolean true if the click was a double click
             */
            @Override
            public boolean mouseReallyClicked(MouseEvent e){

                boolean doubleClicked = super.mouseReallyClicked(e);
                panelClicked = getClickedPanel(e);

                // clear the selection if we clicked on a blank area
                if (panelClicked == null) {
                    clearSelection();

                // otherwise, if we double clicked a panel's type icon, notify the listener
                } else if (doubleClicked && checkTypeIconHit(e)) {
                    notifyPanelTypeIconDoubleClicked(panelClicked);   
                }                
                
                return doubleClicked;
            }
            
            /**
             * Carry out setup appropriate to enter the drag state.
             * Principal effect is to change dragMode as appropriate.
             * @param e MouseEvent the mouse event which triggered entry into the drag state.
             */
            @Override
            public void enterDragState(MouseEvent e) {
                super.enterDragState(e);
                
                // Nothing hit - deselect everything
                clearSelection();

                // ignore anything that is not a left mouse button
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                // Get the part which the user clicked    
                panelClicked = getClickedPanel(e);

                // Did they hit anything?
                if (panelClicked != null) {
                    // gray out the clicked panel and, give drag control to drag&drop 
                    panelClicked.setEnabled(false);
                    exitDragState(e);
                    getTransferHandler().exportAsDrag(VariablesDisplayList.this, e, TransferHandler.MOVE);
                    
                } else {
                    abortDrag();
                    return;
                }            
            }

            /**
             * Get the Variable panel which was clicked.
             * @param e MouseEvent the related mouse event
             * @return VariablePanel the VariablePanel which was clicked
             */
            private VariablePanel getClickedPanel(MouseEvent e) {

                // check that there is a panel clicked
                Point clickLocation = e.getPoint();
                panelNumClicked = locationToIndex(clickLocation);

                if (-1 < panelNumClicked && getCellBounds(panelNumClicked, panelNumClicked).contains(clickLocation)) {
                    return (VariablePanel)getModel().getElementAt(panelNumClicked);
                } else {
                    return null;
                }
            }
            
            /**
             * Find out whether a mouse event's location corresponds to a panel's type icon
             * @param e MouseEvent the relevant event
             * @return boolean whether a panel's type icon was hit.
             */
            private boolean checkTypeIconHit(MouseEvent e) {
                VariablePanel varPan = getClickedPanel(e);
                if (varPan == null) {
                    return false;
                }
                
                // We have to translate the point from the JList's coordinate system to the label's
                // Unfortunately, the JList doesn't actually contain labels (since it uses a renderer instead)
                //   so we have to do this manually.
                Point clickPoint = e.getPoint();
                int panelIndex = locationToIndex(clickPoint);
                Rectangle panelBounds = getCellBounds(panelIndex, panelIndex);
                Point convertedPoint = new Point(clickPoint.x - panelBounds.x, clickPoint.y - panelBounds.y);

                return varPan.checkIconHit(convertedPoint);
            }

            /**
             * Convert a location in the JList to the index of the separator location to which it corresponds.
             * @param p Point the location
             * @return int the index of the separator location to which the point corresponds.  -1 if it doesn't correspond
             * to a sensible location.
             */
            private int locationToSeparatorIndex(Point p) {
                // first check to see if the point is outside the variables display
                Rectangle visibleRect = getVisibleRect();
                if (!visibleRect.contains(p)) {
                    return -1;
                }
                // index is the number of panel midpoints above p
                int index = 0;
                int numVarPanels = getModel().getSize();
                while (index < numVarPanels) {
                    Rectangle rect = getCellBounds(index, index);

                    // compare with the Y coordinate halfway down varPan
                    if (p.getY() < (rect.getY() + rect.getHeight() / 2)) {
                        break;
                    }
                    index++;
                }

                // can only place the separator among other arguments
                int maxIndex = numArgVarPanels;
                if (index > maxIndex) {
                    index = maxIndex;
                }

                return index;
            }

            /**
             * Draw or undraw a separator between elements in the JList
             * @param index int the index at which to draw the separator
             * @param undraw boolean true to undraw, false to draw
             * @return int the index at whcih the separator was really drawn
             */
            private int drawSeparator(int index, boolean undraw) {
                if (index < 0) {
                    // don't draw
                    return -1;
                }
                // Get a graphics object
                Graphics2D g2d = (Graphics2D)getGraphics();    

                // Find the appropriate color and enter paint mode
                if (undraw) {
                    g2d.setColor(getBackground());
                } else {
                    g2d.setColor(Color.black);
                }
                g2d.setPaintMode();

                // preliminary setup
                int numVarPanels = getModel().getSize();
                if (numVarPanels < index) {
                    index = numVarPanels;
                }

                // declare a rectangle for the main part of the separator
                Rectangle rect;
                if (index < numVarPanels) {
                    rect = getCellBounds(index, index);
                } else {
                    // below the last element in the list - move to the bottom of the cell
                    rect = getCellBounds(index - 1, index - 1);
                    rect.y += rect.height;
                }

                // adjust the height and y-coordinate of the separator
                rect.height = 1;
                rect.y -= 1;
                rect.x -=1;

                // an additional bleb at the front
                Polygon poly = new Polygon();
                poly.addPoint(rect.x, rect.y-4);
                poly.addPoint(rect.x, rect.y+rect.height+4);
                poly.addPoint(rect.x+4, rect.y+(rect.height/2));

                // Draw the separator
                g2d.draw(rect);
                g2d.fill(poly);

                // Free the graphics object
                g2d.dispose();

                return index;
            }

            /**
             * Update the displayed position of the separator.
             * @param index int the new position of the separator
             */
            void updateSeparator(int index) {
                // preliminary setup
                int numVarPanels = getModel().getSize();
                if (numVarPanels < index) {
                    index = numVarPanels;
                }

                // check if anything to do
                if (separatorPosition == index) {
                    return;
                }

                // Turn off separator at last position
                if (separatorPosition > -1) {
                    drawSeparator(separatorPosition, true);
                }

                // Turn on separator at this position
                drawSeparator(index, false);

                // update separator position
                separatorPosition = index;
            }

            /**
             * Notify the listener that a panel shifted.
             * @param argIndex the index of the shifted panel .
             * @param shiftAmount the amount by which the panel was shifted.
             * +ve numbers increase its index, -ve numbers decrease it.
             */
            private void notifyPanelShifted(int argIndex, int shiftAmount) {
                for (int i = 0; i < panelEventListeners.size(); i++) {
                    panelEventListeners.get(i).panelShifted(argIndex, shiftAmount);
                }
            }
            
            /**
             * Notify the listener that a panel's type icon was double-clicked.
             * @param variablePanel the variable panel whose type icon was double-clicked.
             */
            private void notifyPanelTypeIconDoubleClicked(VariablePanel variablePanel) {
                for (int i = 0; i < panelEventListeners.size(); i++) {
                    panelEventListeners.get(i).panelTypeIconDoubleClicked(variablePanel);
                }
            }
            
            /**
             * Show the popup, if the given mouse event is the popup trigger.
             * @param e the mouse event.
             */
            private void maybeShowPopup(MouseEvent e) {
    
                if (e.isPopupTrigger() && popupProvider != null) {
    
                    // Get the popup menu for the current identifier
                    if (panelClicked == null) {
                        return;
                    }
                    JPopupMenu menu = popupProvider.getPopupMenu(panelClicked.getIdentifier());
                    if (menu == null) {
                        return;
                    }
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            /**
             * Paint the separator.
             */
            public void paintSeparator() {
                SwingUtilities.invokeLater(new Thread() {
                    @Override
                    public void run() {
                        // It's possible that the user stopped dragging before this Thread was run.  
                        // In which case, we don't need to re-draw the separator.
                        if (isUsefulDragMode(dragMode)) {
                            drawSeparator(separatorPosition, false);
                        }
                    }
                });
            }

            /**
             * Invoked by drag&drop when a drop operation occurs.
             * If the transferable item dragged is not produced by this display,
             * then the external TransferHandler deals with it. Otherwise, a panel
             * shift operation is performed.
             * 
             * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
             */
            public void drop(DropTargetDropEvent dtde){
                
                // Undraw the separator, and restore cursor regardless of what will happen
                updateSeparator(-1);
                setCursor(null);
                
                // See if the transferable data was created by this variable display
                
                Transferable t = dtde.getTransferable();
                try {
                    if (((CodeGemEditor.VariablePanelTransferable.VariableStructure)
                            t.getTransferData(CodeGemEditor.VariablePanelTransferable.variableStructureFlavor)).getParentDisplayList() 
                            != VariablesDisplayList.this) {
                        
                        // Transferable was generated by a Variable Display other than ours;
                        // do not accept this.
                        dtde.rejectDrop();
                        return;
                    }
                } catch (UnsupportedFlavorException e) {
                    // Transferable does not have variable structure, so it must be external
                    defaultDrop(dtde);
                    return;
                } catch (IOException e) {
                    // Data no longer available in requested flavor; let the transfer handler deal with this
                    defaultDrop(dtde);
                    return;
                }
                
                
                // Transferable item was generated by this Variable Display
                // So we will internally handle variable reordering.
                
                // Where are we now?
                int index = locationToSeparatorIndex(dtde.getLocation());

                // If we've finished dragging.  Finish up and do the appropriate moves
                
                // PanelClicked is a valid panel only if drag operation was started inside
                // this Variables Display.
                
                if (panelClicked != null) {
                    
                    // re-enable the panel
                    panelClicked.setEnabled(true);
                    
                    // A panel may be dragged even if reorderingAllowed is false, since 
                    // it may be moved to qualifications display. Now we reorder only
                    // if allowed.
                    if (!reorderingAllowed) {
                        clearSelection();
                        panelClicked = null;
                        repaint();
                        return;
                    }
                    
                    // if it's not a valid place to drop -> just go back to the old order
                    if (index < 0) {
                        index = panelNumClicked;
                    }

                    // Now do all the reorder logic to the inputs.  Do nothing if nothing changed.
                    List<Object> listData = getListData();
                    if (index != panelNumClicked && index != panelNumClicked + 1) {
                        int shiftAmount;
                        listData.remove(panelNumClicked);
                        if (index > panelNumClicked) {
                            listData.add(index - 1, panelClicked);
                            shiftAmount = index - panelNumClicked - 1;
                        } else {
                            listData.add(index, panelClicked);
                            shiftAmount = index - panelNumClicked;
                        }
                        setListData(listData.toArray());
                        notifyPanelShifted(panelNumClicked, shiftAmount);
                    }
                }

                clearSelection();
                panelClicked = null;
                repaint();
            }
            
            /**
             * Invoked by drag&drop when the user drags an item outside of the display component.
             * This method clears the display of any drag indicators.
             * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
             */
            public void dragExit(DropTargetEvent dte){
                // We are either dragging out of the window, or a drag operation has finished
                // Cleanup panel

                updateSeparator(-1);
                setCursor(null);
                clearSelection();
                repaint();
                
            }
            
            /**
             * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
             */
            public void dropActionChanged(DropTargetDragEvent dtde){
                
            }
            
            /**
             * Invoked by drag&drop when the user drags an item over the display component.
             * If the user is dragging an argument, this method displays the list separator
             * indicating a new spot for the argument. 
             * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
             */
            public void dragOver(DropTargetDragEvent dtde){
                
                // If item transfered is not acceptable, reject it

                // We are dragging an external item; accept it if it can be imported via transfer handler
                if (externalDragFlavor) {
                    if (!getTransferHandler().canImport(VariablesDisplayList.this, dtde.getCurrentDataFlavors())) {
                        dtde.rejectDrag();
                    }
                    return;
                }
                
                // We are dragging one of our panels (allowed since it may be moved to qualifications display)
                // but we cannot drop it because reordering is not allowed
                if ((panelClicked != null) && (! reorderingAllowed )) {
                    dtde.rejectDrag();
                    return;
                }
                
                // Item is acceptable; prepare the display
                
                // looks nicer if we clear the selection
                clearSelection();

                // Update the on-screen separator
                int newIndex = locationToSeparatorIndex(dtde.getLocation());
                updateSeparator(newIndex);

                // set the cursor to indicate droppability
                if (newIndex > -1){
                    //setCursor(DragSource.DefaultMoveDrop);
                    
                } else {
                    //setCursor(DragSource.DefaultMoveNoDrop);
                    dtde.rejectDrag();
                }
            }
            /**
             * Invoked by drag&drop when the user drags an item into the display component.
             * This method accepts drags of items created by Variable Displays, and lets the
             * external transfer handler deal with foreign item types. 
             * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
             */
            public void dragEnter(DropTargetDragEvent dtde){
                
                // If dragged item transferable is not something a Variable Display produced,
                if (!dtde.getCurrentDataFlavorsAsList().contains(
                        CodeGemEditor.VariablePanelTransferable.variableStructureFlavor)) {
                    
                    // Deal with it via the panel transfer handler
                    externalDragFlavor = true;
                    defaultDragEnter(dtde);
                    return;
                }
                
                // We might have produced the transferable being dragged,
                // so we will handle it internally.
                // Note: We might not have produced this transferable, even if it is our flavour;
                //       We can only check the owner from transferable contents in the Drop operation,
                //       so we will allow the drag to go on until then.
                
                externalDragFlavor = false;
                dtde.acceptDrag(dtde.getDropAction());
            }
            
            /**
             * This is used by default drag&drop methods listed below.
             * It holds true if the item dragged can be imported; false if not
             */
            private boolean canImport = false;
            
            /**
             * Indicates whether the item dragged has an internal flavour (ie: has been
             * created by a Variable Display).
             */
            private boolean externalDragFlavor = true;
            
            /**
             * Default handler for dragEnter(); delegates control through external transfer handler.
             * @see javax.swing.TransferHandler 
             */
            public void defaultDragEnter(DropTargetDragEvent e) {
                DataFlavor[] flavors = e.getCurrentDataFlavors();

                JComponent c = (JComponent)e.getDropTargetContext().getComponent();
                TransferHandler importer = c.getTransferHandler();
                
                if (importer != null && importer.canImport(c, flavors)) {
                    canImport = true;
                } else {
                    canImport = false;
                }
                
                int dropAction = e.getDropAction();
                
                if (canImport) {
                    e.acceptDrag(dropAction);
                } else {
                    e.rejectDrag();
                }
            }

            /**
             * Default handler for dragOver(); delegates control through external transfer handler.
             * @see javax.swing.TransferHandler
             */
            public void defaultDragOver(DropTargetDragEvent e) {
                int dropAction = e.getDropAction();
                
                if (canImport) {
                    e.acceptDrag(dropAction);
                } else {
                    e.rejectDrag();
                }
            }

            /**
             * Default handler for drop(); delegates control through external transfer handler.
             * @see javax.swing.TransferHandler
             */
            public void defaultDrop(DropTargetDropEvent e) {
                int dropAction = e.getDropAction();

                JComponent c = (JComponent)e.getDropTargetContext().getComponent();
                TransferHandler importer = c.getTransferHandler();

                if (canImport && importer != null) {
                    e.acceptDrop(dropAction);
                    
                    try {
                        Transferable t = e.getTransferable();
                        e.dropComplete(importer.importData(c, t));
                    } catch (RuntimeException re) {
                        e.dropComplete(false);
                    }
                } else {
                    e.rejectDrop();
                }
            }

            /**
             * Default handler for dragActionChanged(); delegates control through external transfer handler.
             * @see javax.swing.TransferHandler
             */
            public void defaultDropActionChanged(DropTargetDragEvent e) {
                int dropAction = e.getDropAction();
                
                if (canImport) {
                    e.acceptDrag(dropAction);
                } else {
                    e.rejectDrag();
                }
            }
        }

        /**
         * Default constructor for a VariablesDisplay.
         */
        VariablesDisplayList() {
            super();

            setName("VariablesDisplay");
            setModel(new DefaultListModel());
            setAlignmentY(Component.TOP_ALIGNMENT);
            setCellRenderer(new VariablesDisplayRenderer());
            setBackground(new Color(239,239,239));
            setPreferredSize(new Dimension(25, 101));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setSize(25, 191);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            // add the mouse listener
            mouseHandler = new MouseHandler();
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            
            setDropTarget(new DropTarget(this, mouseHandler));
        }
        
        /**
         * Cleans up variables display after a dragged 
         * item was dropped. This should be called by the 
         * external drag&drop TransferHandler after an item
         * was exported from this display. 
         */
        void externalDropComplete() {
            
            if (panelClicked != null) {
                // Display still believes it is dragging; this means
                // the drop operation has happened on an external component
                
                panelClicked.setEnabled(true);
                panelClicked = null;
            }
            setCursor(null);
            clearSelection();
            repaint();
        }
        
        /** Retrieves the panel currently handled by the mouse */
        VariablePanel getClickedPanel() {
            return panelClicked;
        }

        /**
         * Paint the Variables Display area.
         * @param g Graphics the graphics object to use.
         */
        @Override
        public void paintComponent(Graphics g) {
            // JList
            super.paintComponent(g);
            
            // paint the separator
            mouseHandler.paintSeparator();
        }
    }
    
    /** List contained within this display */
    private final VariablesDisplayList variablesDisplayList = new VariablesDisplayList();
    
    /** Reference to listeners on argument reorder events.  */
    private final List<PanelEventListener> panelEventListeners = new ArrayList<PanelEventListener>();

    /** Whether reordering of the variables is allowed (eg. not allowed if it's broken..). */
    private boolean reorderingAllowed = false;

    /** The number of variable panels which represent arguments.  Needed for arg reordering.  */
    private int numArgVarPanels = 0;
    
    /** The VariablePanel which is handled by the mouse (null if none). */
    private VariablePanel panelClicked;
    
    /** Label displaying title */
    private final JLabel titleLabel = new JLabel();
    
    /** Provider for popup menus for this display */
    private AdvancedCALEditor.IdentifierPopupMenuProvider popupProvider;
    
    /** Constructor for the display */
    public VariablesDisplay() {
        setLayout(new BorderLayout());
        
        JScrollPane variablesScrollPane = new JScrollPane();
        variablesScrollPane.setName("JScrollPane1");
        variablesScrollPane.setPreferredSize(new Dimension(175, 131));
        variablesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        variablesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        variablesScrollPane.setViewportView(variablesDisplayList);
        this.add(variablesScrollPane,"Center");
        variablesDisplayList.setBorder(BorderFactory.createEmptyBorder());
        variablesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        titleLabel.setText(GemCutter.getResourceString("CEP_Arguments_Title"));
        titleLabel.setBorder(new EmptyBorder(0,7,0,0));
        this.add(titleLabel,"North");
    }
    
    /** 
     * Sets the display title
     * @param title
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    /**
     * Get the tooltip for an element in the list.
     * @param e MouseEvent the related mouse event
     * @return the associated tooltip
     */
    @Override
    public String getToolTipText(MouseEvent e) {

        int index = variablesDisplayList.locationToIndex(e.getPoint());
        if (-1 < index) {
            VariablePanel varPan = (VariablePanel)variablesDisplayList.getModel().getElementAt(index);
            return varPan.getToolTipText();

        } else {
            return null;
        }
    }
    
    /**
     * Update the variable panels displayed by this JList
     * @param argVarPanels the variable panels which correspond to arguments
     * @param reorderingAllowed whether reordering is allowed.
     */
    void updateVariablePanels(VariablePanel[] argVarPanels, boolean reorderingAllowed) {

        this.reorderingAllowed = reorderingAllowed;
        this.numArgVarPanels = argVarPanels.length;

        // update the JList to show the new data
        variablesDisplayList.setListData(argVarPanels.clone());
        updatePreferredSize();
        
        // Validate everything (for now - maybe restrict later)
        validate();
        
        // Repaint the variablesDisplay
        repaint();
    }

    /**
     * Update a variable panel displayed by this JList
     * @param panelIndex the index of the panel to update.
     * @param updatedVariablePanel the updated panel.
     */
    void updateVariablePanel(int panelIndex, VariablePanel updatedVariablePanel) {
        VariablePanel originalPanel = (VariablePanel)variablesDisplayList.getModel().getElementAt(panelIndex);
        if (updatedVariablePanel.sameInfo(originalPanel)) {
            return;
        }
        List<Object> listData = getListData();
        listData.set(panelIndex, updatedVariablePanel);
        
        // update the JList to show the new data
        variablesDisplayList.setListData(listData.toArray());
        updatePreferredSize();
        
        // Validate everything (for now - maybe restrict later)
        validate();
        
        // Repaint the variablesDisplay
        repaint();
    }
    
    /**
     * Return a new list containing the data in this JList.
     * @return List this JList's list data.
     */
    private List<Object> getListData() {

        ListModel listModel = variablesDisplayList.getModel();
        int numElements = listModel.getSize();

        ArrayList<Object> listData = new ArrayList<Object>(numElements);
        for (int i = 0; i < numElements; i++) {
            listData.add(listModel.getElementAt(i));
        }
        
        return listData;
    }

    /**
     * Add the listener for panel events in this component.
     * @param newPanelEventListener the new PanelEventListener.
     */
    void addPanelEventListener(PanelEventListener newPanelEventListener) {
        this.panelEventListeners.add(newPanelEventListener);
    }
    
    /**
     * Selects the panel corresponding to the specified argument name
     * @param argumentName
     */
    public void selectPanelForArgument(String argumentName) {
        List<Object> panels = getListData();
        for (int i = 0; i < panels.size(); i++) {
            if (((VariablePanel)panels.get(i)).getIdentifier().getName().equals(argumentName)) {
                
                // Panel found
                variablesDisplayList.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * Update the preferred size of the JList according to the sizes of the component variable panels
     */
    private void updatePreferredSize() {
        // we only care about the height
        int numVarPanels = variablesDisplayList.getModel().getSize();
        if (numVarPanels > 0) {
            Rectangle rect = variablesDisplayList.getCellBounds(0, numVarPanels - 1);
            variablesDisplayList.setPreferredSize(new Dimension(0, rect.height));
        } else {
            variablesDisplayList.setPreferredSize(new Dimension(0, 0));
        }
    }
 
    void clearSelection() {
        variablesDisplayList.clearSelection();
    }
    
    void setDragEnabled(boolean enabled) {
        variablesDisplayList.setDragEnabled(enabled);
    }
    
    void setListTransferHandler(TransferHandler handler) {
        variablesDisplayList.setTransferHandler(handler);
    }
    
    VariablesDisplayList getListComponent() {
        return variablesDisplayList;
    }
    
    void addListFocusListener(FocusListener listener) {
        variablesDisplayList.addFocusListener(listener);
    }
    
    void setPopupMenuProvider(AdvancedCALEditor.IdentifierPopupMenuProvider popupProvider) {
        this.popupProvider = popupProvider;
    }
}