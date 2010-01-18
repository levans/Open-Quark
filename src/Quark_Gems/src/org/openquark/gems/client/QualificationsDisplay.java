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
 * QualificationsDisplay.java
 * Creation date: (Feb 20, 2004 1:37:20 PM)
 * By: Iulian Radu
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.compiler.CodeAnalyser.AnalysedIdentifier;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.gems.client.caleditor.AdvancedCALEditor;
import org.openquark.gems.client.caleditor.AdvancedCALEditor.PositionlessIdentifier;


public class QualificationsDisplay extends JPanel {

    private static final long serialVersionUID = 1203014794470429073L;

    /**
     * Interface for a listener which wishes to be informed of various panel events
     * 
     * @author Iulian Radu
     */
    public interface PanelEventListener {

        /**
         * Notify the listener that a panel's type icon was double-clicked.
         * 
         * @param qualificationPanel the panel that was double-clicked.
         */
        public void panelTypeIconDoubleClicked(QualificationPanel qualificationPanel);
        
        /**
         * Notify the listener that a panel's module label was double-clicked.
         * 
         * @param qualificationPanel the panel that was double-clicked.
         * @param mousePoint the point where the mouse is
         */
        public void panelModuleLabelDoubleClicked(QualificationPanel qualificationPanel, Point mousePoint);
    }

    /**
     * Comparator object to order Qualification Panels by their type, 
     * sub-ordered by ascending alphabetical name. 
     */
    private static class PanelTypeComparator implements Comparator<QualificationPanel> {
        private static final Map<Category, Integer> orderMap = new HashMap<Category, Integer>();
        
        static {
            orderMap.put(SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,     Integer.valueOf(1));
            orderMap.put(SourceIdentifier.Category.DATA_CONSTRUCTOR, Integer.valueOf(2));
            orderMap.put(SourceIdentifier.Category.TYPE_CONSTRUCTOR, Integer.valueOf(3));
            orderMap.put(SourceIdentifier.Category.TYPE_CLASS,       Integer.valueOf(4));
        }
        
        public int compare(QualificationPanel panel1, QualificationPanel panel2) {
            SourceIdentifier.Category panel1Form = panel1.getIdentifier().getCategory();
            SourceIdentifier.Category panel2Form = panel2.getIdentifier().getCategory();
            
            if (panel1Form == panel2Form) {
                // Forms are the same; order by identifier name string
                return panel1.getIdentifier().getName().compareTo(panel2.getIdentifier().getName());
            } else {
                // Forms differ; compare by their integer representation
                return orderMap.get(panel1Form).compareTo(orderMap.get(panel2Form));
            }
        }
    }
    
    /**
     * The JList Component that displays the qualifications in the CodeGem panel.
     * 
     * @author Iulian Radu
     */
    class QualificationsDisplayList extends JList {
        private static final long serialVersionUID = 3833006693516784243L;
    
        /** Reference to a MouseHandler */
        private final MouseHandler mouseHandler;
        
        /**
         * Handler for drag and drop events. The default JList behavior
         * while dragging is to change selection to the list item pointed to.
         * This handler is created to resist selection change.
         * 
         * @author Iulian Radu
         */
        private class DragHandler implements DropTargetListener {
    
            /** Constructor */
            public DragHandler() {
            }
            
            /**
             * This is used by default drag&drop methods listed below.
             * It holds true if the item dragged can be imported; false if not
             */
            private boolean canImport = false;
    
            /**
             * Default handler for dragEnter(); delegates control through external transfer handler.
             * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
             */
            public void dragEnter(DropTargetDragEvent e) {
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
             * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
             */
            public void dragOver(DropTargetDragEvent e) {
                int dropAction = e.getDropAction();
                
                if (canImport) {
                    e.acceptDrag(dropAction);
                } else {
                    e.rejectDrag();
                }
            }
    
            /**
             * Default handler for drop(); delegates control through external transfer handler.
             * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
             */
            public void drop(DropTargetDropEvent e) {
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
             * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
             */
            public void dropActionChanged(DropTargetDragEvent e) {
                int dropAction = e.getDropAction();
                
                if (canImport) {
                    e.acceptDrag(dropAction);
                } else {
                    e.rejectDrag();
                }
            }
            
            /**
             * Default handler for dragExit(); does nothing
             * @param e
             */
            public void dragExit(DropTargetEvent e) {
                
            }
        }
        
        /**
         * Inner class to handle mouse events on the Display.
         * 
         * @author Iulian Radu
         */
        private class MouseHandler extends org.openquark.gems.client.utilities.MouseClickDragAdapter {
            
            /** The index of the panel clicked, if any. */
            private int panelNumClicked;
    
            /**
             * Constructor for the Mouse Handler
             */
            private MouseHandler() {
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
             * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e); 
            }
        
            /**
             * Find out whether a mouse event's location corresponds to a panel's type icon
             * @param e MouseEvent the relevant event
             * @return boolean whether a panel's type icon was hit.
             */
            private boolean checkTypeIconHit(MouseEvent e) {
                QualificationPanel varPan = getClickedPanel(e);
                if (varPan == null) {
                    return false;
                }
                
                return varPan.checkIconHit(listToPanelPoint(e.getPoint()));
            }
            
            /**
             * Find out whether a mouse event's location corresponds to a panel's module label
             * @param e MouseEvent the relevant event
             * @return boolean whether a panel's module label was hit.
             */
            private boolean checkModuleLabelHit(MouseEvent e) {
                QualificationPanel varPan = getClickedPanel(e);
                if (varPan == null) {
                    return false;
                }
                
                return varPan.checkModuleLabelHit(listToPanelPoint(e.getPoint()));
            }
            
            /**
             * Converts the specified point on the display list to a point
             * relative to the selected panel.
             * @param p point in coordinates of the Qualifications Display
             * @return point in coordinates of the Qualification Panel
             */
            private Point listToPanelPoint(Point p) {
                // We have to translate the point from the JList's coordinate system to the panel's
                int panelIndex = locationToIndex(p);
                Rectangle panelBounds = getCellBounds(panelIndex, panelIndex);
                Point convertedPoint = new Point(p.x - panelBounds.x, p.y - panelBounds.y);
                return convertedPoint;
            }
            
            /**
             * @see org.openquark.gems.client.utilities.MouseClickDragListener#abortDrag()
             */
            @Override
            public void abortDrag() {
                super.abortDrag();
                clearSelection();
                panelClicked = null;
                exitDragState(null);
                getDropTarget().getDropTargetContext().dropComplete(false);
            }
            
            /**
             * Surrogate method for mouseClicked.  Called only when our definition of click occurs.
             * @param e MouseEvent the relevant event
             * @return boolean true if the click was a double click
             */
            @Override
            public boolean mouseReallyClicked(MouseEvent e){
    
                boolean doubleClicked = super.mouseReallyClicked(e);
                
                // clear the selection if we clicked on a blank area
                if (panelClicked == null) {
                    clearSelection();
    
                // otherwise, if we double clicked a panel's type icon or label , notify the appropriate listener
                } else if (doubleClicked && checkTypeIconHit(e)) {
                    notifyPanelTypeIconDoubleClicked(panelClicked);
                
                } else if (doubleClicked && checkModuleLabelHit(e)) {
                    notifyPanelModuleLabelDoubleClicked(panelClicked, e.getPoint());
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
                
                // ignore anything that is not a left mouse button
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
    
                // Did they hit anything?
                if ((panelClicked != null) && getDragEnabled()) {
                    // exit the normal mouse drag and, give control to drag&drop 
                    clearSelection();
                    panelClicked.setEnabled(false);
                    
                    exitDragState(e);
                    getTransferHandler().exportAsDrag(QualificationsDisplayList.this, e, TransferHandler.MOVE);
                } else {
                    abortDrag();
                    return;
                }
            }
            
            /**
             * Get the panel which was clicked.
             * @param e MouseEvent the related mouse event
             * @return QualificationPanel the Panel which was clicked
             */
            private QualificationPanel getClickedPanel(MouseEvent e) {
    
                // check that there is a panel clicked
                Point clickLocation = e.getPoint();
                panelNumClicked = locationToIndex(clickLocation);
    
                if (-1 < panelNumClicked && getCellBounds(panelNumClicked, panelNumClicked).contains(clickLocation)) {
                    return (QualificationPanel)getModel().getElementAt(panelNumClicked);
                } else {
                    return null;
                }
            }
            
            /**
             * Notify the listener that a panel's type icon was double-clicked.
             * @param panel the panel whose type icon was double-clicked.
             */
            private void notifyPanelTypeIconDoubleClicked(QualificationPanel panel) {
                for (int i = 0; i < panelEventListeners.size(); i++) {
                    panelEventListeners.get(i).panelTypeIconDoubleClicked(panel);
                }
            }
            
            /**
             * Notify the listeners that a panel's module label was double-clicked.
             * @param panel the panel whose module label was double-clicked.
             */
            private void notifyPanelModuleLabelDoubleClicked(QualificationPanel panel, Point mousePoint) {
                for (int i = 0; i < panelEventListeners.size(); i++) {
                    panelEventListeners.get(i).panelModuleLabelDoubleClicked(panel, mousePoint);
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
        }
    
        /**
         * A trivial implementation of ListCellRenderer.
         * Creation date: (04/07/2001 3:57:15 PM)
         * @author Edward Lam
         */
         public class QualificationsDisplayRenderer implements ListCellRenderer {
             
            /**
             * Return an object that can paint a QualificationPanel (ie. itself).
             * Creation date: (04/07/2001 3:30:13 PM)
             * @param list Jlist - The JList we're painting.
             * @param value Object - The value returned by list.getModel().getElementAt(index).
             * @param index int - The cell's index.
             * @param isSelected boolean - True if the specified cell was selected.
             * @param cellHasFocus boolean - True if the specified cell has the focus.
             * @return Component the component capable of rendering the list item
             */
            public Component getListCellRendererComponent(JList list, Object value, int index, 
                                                                   boolean isSelected, boolean cellHasFocus) {
                // the list objects should all be QualificationPanel objects
                QualificationPanel pan = (QualificationPanel)value;
    
                pan.setSelected(isSelected);
    
                // gray out the panel if it's disabled
                pan.setGrayed(!pan.isEnabled());
                
                return pan;
            }
        }
        
        /**
         * Default constructor for a QualificationsDisplay.
         */
        
        public QualificationsDisplayList() {
            super();
    
            setName("QualificationsDisplay");
            setModel(new DefaultListModel());
            setAlignmentY(Component.TOP_ALIGNMENT);
            setCellRenderer(new QualificationsDisplayRenderer());
            setBackground(new Color(239,239,239));
            setPreferredSize(new Dimension(25, 101));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setSize(25, 191);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
            // add the mouse listener
            mouseHandler = new MouseHandler();
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            
            // set this list to be a drop target and set its dnd handler
            setDropTarget(new DropTarget(this, new DragHandler()));
        }
        
        /**
         * Cleans up qualifications display after a dragged 
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
        
        /**
         * @return the panel that was last clicked by the mouse.
         */
        QualificationPanel getClickedPanel() {
            return panelClicked;
        }
    }
    
    /** Comparator of panels by type */
    public static final PanelTypeComparator panelTypeComparator = new PanelTypeComparator();
    
    /** List contained within this display */
    private final QualificationsDisplayList qualificationsDisplayList;
    
    /** Title label for this panel */
    private final JLabel titleLabel = new JLabel();
    
    /** The Panel which was clicked. */
    private QualificationPanel panelClicked;
    
    /** Reference to listeners on events regarding mouse clicks and module changes.  */
    private final List<PanelEventListener> panelEventListeners = new ArrayList<PanelEventListener>();
    
    /** Provider for popup menus for this display */
    private AdvancedCALEditor.IdentifierPopupMenuProvider popupProvider;

    /** The current workspace */
    private final CALWorkspace workspace;
    
    /** Constructor for the display */
    public QualificationsDisplay(CALWorkspace workspace) {
        this.workspace = workspace;
        setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setName("JScrollPane1");
        scrollPane.setPreferredSize(new Dimension(175, 131));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        qualificationsDisplayList = new QualificationsDisplayList();
        scrollPane.setViewportView(qualificationsDisplayList);
        this.add(scrollPane,"Center");
        qualificationsDisplayList.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        titleLabel.setText(GemCutter.getResourceString("CEP_Functions_Title"));
        titleLabel.setBorder(new EmptyBorder(0,7,0,0));
        titleLabel.setOpaque(true);
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
     * 
     * @param e MouseEvent the related mouse event
     * @return the associated tooltip
     */
    @Override
    public String getToolTipText(MouseEvent e) {

        int index = qualificationsDisplayList.locationToIndex(e.getPoint());
        if (-1 < index) {
            QualificationPanel varPan = (QualificationPanel)qualificationsDisplayList.getModel().getElementAt(index);
            return varPan.getToolTipText();

        } else {
            return null;
        }
    }
    
    /**
     * Update the panels displayed by this JList
     * 
     * @param faVarPanels the panels 
     */
    private void updateQualificationPanels(List<QualificationPanel> faVarPanels) {

        // update the JList to show the new data
        qualificationsDisplayList.setListData(faVarPanels.toArray());
        updatePreferredSize();
    
        // Validate everything (for now - maybe restrict later)
        validate();
    
        // Repaint the Display
        qualificationsDisplayList.repaint();
    }

    /**
     * Return a new list containing the data in this JList.
     * 
     * @return this JList's list data.
     */
    private List<QualificationPanel> getListData() {

        ListModel listModel = qualificationsDisplayList.getModel();
        int numElements = listModel.getSize();

        ArrayList<QualificationPanel> listData = new ArrayList<QualificationPanel>(numElements);
        for (int i = 0; i < numElements; i++) {
            listData.add((QualificationPanel)listModel.getElementAt(i));
        }
        
        return listData;
    }

    /**
     * Adds a listener for panel events in this component.
     * 
     * @param newPanelEventListener the new PanelEventListener.
     */
    public void addPanelEventListener(PanelEventListener newPanelEventListener) {
        panelEventListeners.add(newPanelEventListener);
    }
    
    /**
     * Update the preferred size of the JList according to the sizes of the component panels
     * 
     */
    private void updatePreferredSize() {
        // we only care about the height
        int numVarPanels = qualificationsDisplayList.getModel().getSize();
        if (numVarPanels > 0) {
            Rectangle rect = qualificationsDisplayList.getCellBounds(0, numVarPanels - 1);
            qualificationsDisplayList.setPreferredSize(new Dimension(0, rect.height));
        } else {
            qualificationsDisplayList.setPreferredSize(new Dimension(0, 0));
        }
    }
    
    /**
     * Generates qualifications panels within this display, corresponding to
     * qualifications which occur in code and in the auto-qualification map
     * 
     * @param qualificationMap
     * @param identifiers
     * @param moduleTypeInfo
     */
    public void generateQualificationPanels(CodeQualificationMap qualificationMap, List<CodeAnalyser.AnalysedIdentifier> identifiers, ModuleTypeInfo moduleTypeInfo) {
        
        // Generate panels
        List<QualificationPanel> newPanels = generateExternalQualificationsPanels(identifiers, moduleTypeInfo);
        newPanels.addAll(generateMappedQualificationsPanels(qualificationMap, moduleTypeInfo));
        
        // Sort the list
        Collections.sort(newPanels, panelTypeComparator);
        
        updateQualificationPanels(newPanels);
        
        // Set title accordingly
        setTitle(getProperTitle(identifiers));
    }
    
    /**
     * Gets the proper title for this panel, based on what identifier types it is representing
     * @param identifiers List 
     */
    public String getProperTitle(List<AnalysedIdentifier> identifiers) {
        if (identifiers == null) {
            return GemCutter.getResourceString("CEP_Functions_Title");
        }
        boolean functions = false;
        boolean types = false;
        for (final AnalysedIdentifier identifier : identifiers) {
            if ((identifier.getQualificationType() == CodeAnalyser.AnalysedIdentifier.QualificationType.QualifiedResolvedTopLevelSymbol) || 
                (identifier.getQualificationType() == CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol)) {
                // Only look at identifiers shown by this display
                
                if ((identifier.getCategory() == SourceIdentifier.Category.DATA_CONSTRUCTOR) ||
                    (identifier.getCategory() == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD)) {
                    functions = true;
                } else {
                    types = true;
                }
            }
            
            if (functions && types) {
                break;
            }
        }
        
        if (functions && types) {
            return GemCutter.getResourceString("CEP_FunctionsAndTypes_Title");
        } else if (types) {
            return GemCutter.getResourceString("CEP_Types_Title");
        } else {
            return GemCutter.getResourceString("CEP_Functions_Title");
        }
    }
    
    /**
     * Generate panels for identifiers qualified through map
     * @param qualificationMap
     * @param moduleTypeInfo
     * @return panels representing identifiers qualified through the map
     */
    private List<QualificationPanel> generateMappedQualificationsPanels(CodeQualificationMap qualificationMap, ModuleTypeInfo moduleTypeInfo) {
        // Create panels with mapped items
        
        List<QualificationPanel> newPanels = new ArrayList<QualificationPanel>();
        
        newPanels.addAll(generateMappedQualificationPanelsForType(
                qualificationMap, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, moduleTypeInfo));
        
        newPanels.addAll(generateMappedQualificationPanelsForType(
                qualificationMap, SourceIdentifier.Category.TYPE_CLASS, moduleTypeInfo));
        
        newPanels.addAll(generateMappedQualificationPanelsForType(
                qualificationMap, SourceIdentifier.Category.TYPE_CONSTRUCTOR, moduleTypeInfo));
        
        newPanels.addAll(generateMappedQualificationPanelsForType(
                qualificationMap, SourceIdentifier.Category.DATA_CONSTRUCTOR, moduleTypeInfo));
        
        return newPanels;
    }
    
    /**
     * Generate qualification panels representing mapped identifiers of the specified category.
     *  
     * @param qualificationMap qualification map to extract unqualified names of identifiers
     * @param type identifiers all belong to this category
     * @param moduleTypeInfo type info for the current module
     * @return list of qualification panels created from the identifiers
     */
    private List<QualificationPanel> generateMappedQualificationPanelsForType(
            CodeQualificationMap qualificationMap, SourceIdentifier.Category type, ModuleTypeInfo moduleTypeInfo) {
        
        List<String> unqualifiedIdentifiers = new ArrayList<String>(qualificationMap.getUnqualifiedNames(type));
        List<QualificationPanel> newPanels = new ArrayList<QualificationPanel>();
        for (final String unqualifiedName : unqualifiedIdentifiers) {
            ModuleName moduleName = qualificationMap.getQualifiedName(unqualifiedName, type).getModuleName();
            ModuleName minimallyQualifiedModuleName = moduleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            
            QualificationPanel newPanel = new QualificationPanel(
                    new AdvancedCALEditor.PositionlessIdentifier(unqualifiedName, moduleName, moduleName, minimallyQualifiedModuleName, type, CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol),
                    getTypeToolTipText(QualifiedName.make(moduleName, unqualifiedName), type, moduleTypeInfo),
                    CodeAnalyser.getModulesContainingIdentifier(unqualifiedName, type, moduleTypeInfo).size() > 1);
            newPanels.add(newPanel);
        }
        return newPanels;
    }
    
    /**
     * Generate panels for externally qualified identifiers.
     * 
     * @param identifiers identifiers from code analysis, null if none was performed
     * @param moduleTypeInfo 
     */ 
    private List<QualificationPanel> generateExternalQualificationsPanels(List<AnalysedIdentifier> identifiers, ModuleTypeInfo moduleTypeInfo) {
        
        List<QualificationPanel> newPanels = new ArrayList<QualificationPanel>();
        if (identifiers == null) {
            return newPanels;
        }
        
        // Iterate through the source identifiers, and add resolved code qualifications within each map
        
        // Create set of PositionlessIdentifiers of qualifications in code
        Set<PositionlessIdentifier> qualifiedIdentifiers = new HashSet<PositionlessIdentifier>();
        
        for (final AnalysedIdentifier identifier : identifiers) {
            if (identifier.getQualificationType() == CodeAnalyser.AnalysedIdentifier.QualificationType.QualifiedResolvedTopLevelSymbol) {
                qualifiedIdentifiers.add(new AdvancedCALEditor.PositionlessIdentifier(identifier.getName(), identifier.getRawModuleName(), identifier.getResolvedModuleName(), identifier.getMinimallyQualifiedModuleName(), identifier.getCategory(), identifier.getQualificationType()));
            }
        }
        
        // Now add panels
        for (final PositionlessIdentifier identifier : qualifiedIdentifiers) {
            ModuleName resolvedModuleNameOrNull = identifier.getResolvedModuleName();
            if (resolvedModuleNameOrNull == null) {
                continue;
            }
            QualificationPanel newPanel = new QualificationPanel(
                        identifier,
                        getTypeToolTipText(QualifiedName.make(resolvedModuleNameOrNull, identifier.getName()), identifier.getCategory(), moduleTypeInfo), 
                        false);
            newPanels.add(newPanel);
        }
        return newPanels;
    }
    
    /**
     * Selects the panel corresponding to the specified identifier.
     * Clears selection if identifier not found.
     * @param identifier
     */
    public void selectPanelForIdentifier(AdvancedCALEditor.PositionlessIdentifier identifier) {
        List<QualificationPanel> panels = getListData();
        for (int i = 0; i < panels.size(); i++) {
            QualificationPanel panel = panels.get(i);
            if (identifier.equals(panel.getIdentifier())) {
                qualificationsDisplayList.setSelectedIndex(i);
                return;
            }
        }
        qualificationsDisplayList.clearSelection();
    }
    
    /**
     * Returns tooltip describing an entity with the specified
     * qualified name, and belonging to a panel of the given form.
     * 
     * @param qualifiedName
     * @param form 
     * @param currentModuleTypeInfo type information of the current module
     * @return tooltip text
     */
    private String getTypeToolTipText(QualifiedName qualifiedName, SourceIdentifier.Category form, ModuleTypeInfo currentModuleTypeInfo) {
        return AdvancedCALEditor.getMetadataToolTipText(qualifiedName.getUnqualifiedName(), qualifiedName.getModuleName(), 
                                                        form, currentModuleTypeInfo, workspace, this);
    }
    
    /**
     * Clear list selection
     */
    public void clearSelection() {
        qualificationsDisplayList.clearSelection();
    }
    
    /**
     * Enable drag and drop
     * @param enabled
     */
    void setDragEnabled(boolean enabled) {
        qualificationsDisplayList.setDragEnabled(enabled);
    }
    
    /**
     * Set the transfer handler for the display list
     * @param handler
     */
    void setListTransferHandler(TransferHandler handler) {
        qualificationsDisplayList.setTransferHandler(handler);
    }
 
    /**
     * @return the display list component
     */
    QualificationsDisplayList getListComponent() {
        return qualificationsDisplayList;
    }
    
    /**
     * Add a focus listener to the list component
     * @param listener
     */
    void addListFocusListener(FocusListener listener) {
        qualificationsDisplayList.addFocusListener(listener);
    }
    
    /**
     * Set popup provider for display menus
     * @param popupProvider
     */
    public void setPopupMenuProvider(AdvancedCALEditor.IdentifierPopupMenuProvider popupProvider) {
        this.popupProvider = popupProvider;
    }
    
    /**
     * @return provider for popup menus
     */
    public AdvancedCALEditor.IdentifierPopupMenuProvider getPopupMenuProvider() {
        return popupProvider;
    }
 }
