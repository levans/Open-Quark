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
 * EnumeratedDataTypeGemGenerator.java
 * Creation date: Jan 12, 2004
 * By: Iulian Radu
 */
package org.openquark.gems.client.generators;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.IdentifierUtils;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.GemCutter;
import org.openquark.gems.client.ValueRunner;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;
import org.openquark.gems.client.utilities.PreferencesHelper;
import org.openquark.gems.client.valueentry.ValueEditorManager;




/**
 * A gem generator for enumerated data types.
 * 
 * An enumeration is a non-polymorphic type, containing
 * a list of constructors with 0-arity. Enumeration types generated
 * by this generator are instances of the Eq class, and optionally of the Ord, Enum, and 
 * Bounded classes as well. 
 * 
 * @author Iulian Radu
 */
public class EnumeratedDataTypeGemGenerator implements GemGenerator {
    /** The icon to use for the generator. */
    private static final Icon GENERATOR_ICON = new ImageIcon(GemGenerator.class.getResource("/Resources/nav_typeconstructor.gif"));

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#launchGenerator(javax.swing.JFrame, org.openquark.cal.services.Perspective, org.openquark.gems.client.ValueRunner, org.openquark.gems.client.valueentry.ValueEditorManager, org.openquark.cal.compiler.TypeChecker)
     */
    public GemGenerator.GeneratedDefinitions launchGenerator(JFrame parent,
                                                         Perspective perspective,
                                                         ValueRunner valueRunner,
                                                         ValueEditorManager valueEditorManager,
                                                         TypeChecker typeChecker) {
        
        if (parent == null || perspective == null) {
            throw new NullPointerException();
        }
    
        final EnumeratedTypeGemGeneratorDialog generatorUI = new EnumeratedTypeGemGeneratorDialog(parent, perspective);
        generatorUI.setResizable(true);
        generatorUI.setVisible(true);
        
        return new GemGenerator.GeneratedDefinitions() {

            public ModuleDefn getModuleDefn() {
                return null;
            }

            public Map<String, String> getSourceElementMap() {
                return generatorUI.getSourceDefinitions();
            }
        };
    }

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorMenuName()
     */
    public String getGeneratorMenuName() {
        return GeneratorMessages.getString("ETGF_FactoryMenuName");
    }
    
    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorTitle()
     */
    public String getGeneratorTitle() {
        return GeneratorMessages.getString("ETGF_FactoryTitle");
    }

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorIcon()
     */
    public Icon getGeneratorIcon() {
        return GENERATOR_ICON;
    }

}



/**
 * This is the user interface class for either of the Java factories.
 * @author Frank Worsley
 */
class EnumeratedTypeGemGeneratorDialog extends JDialog {
    /* Dialog icons */
    
    private static final long serialVersionUID = 164116234901065344L;

    /** The icon to use for error messages. */
    private static final Icon ERROR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/error.gif"));

    /** The icon to use for warning messages. */
    private static final Icon WARNING_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/warning.gif"));
    
    /** The icon to use if everything is ok. */
    private static final Icon OK_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/checkmark.gif"));
    
    /** The up icon **/
    private static final Icon UP_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/up.gif"));
    
    /** The down icon **/
    private static final Icon DOWN_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/down.gif"));
    
    /** The enumeration icon **/
    private static final Icon ENUMERATION_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/enum.gif"));
    
    /** The correction icon **/
    private static final Icon CORRECTION_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/undo.gif"));
    
    
    /* Preference key names. */
    
    private static final String DIALOG_PROPERTIES_PREF_KEY = "dialogProperties";
        
    /* Options for derived instances. */
    /* - these instances of DerivedInstancesOption are meant to be compared against using reference equality, i.e. with == */
    
    private static final DerivedInstancesOption[] DERIVED_INSTANCES_OPTIONS =
        new DerivedInstancesOption[] { DerivedInstancesOption.EQ_ONLY_DERIVED_INSTANCES_OPTION, DerivedInstancesOption.EQ_ORD_BOUNDED_ENUM_DERIVED_INSTANCES_OPTION };
    
    /* Dialog Components */
    
    /** Text field for entering the name of the new data type */
    private JComboBox gemNameField;
    
    /** The radio button for selecting private scope. */
    private final JRadioButton privateButton = new JRadioButton(GeneratorMessages.getString("PrivateLabel"));
    
    /** The radio button for selecting public scope. */
    private final JRadioButton publicButton = new JRadioButton(GeneratorMessages.getString("PublicLabel"));

    /** The button group for the radio buttons. */
    private final ButtonGroup buttonGroup = new ButtonGroup();
    
    /** The combo box for selecting which type classes the generated enumeration should be a derived instance of. */
    private JComboBox derivedInstancesField;
    
    /** The OK button for the dialog. */
    private JButton okButton;
    
    /** The cancel button for the dialog. */
    private JButton cancelButton;
    
    /** The label for displaying status messages. */
    private final JLabel statusLabel = new TippedLabel();
    
    /** Text field for entering the enumeration value */
    private JTextField valueField;
    
    /** The list control representing the enumeration list */
    private SwapList enumListControl;
    
    /** The encapsulating scroll pane for the list control */
    private JScrollPane enumListScrollPane;
    
    /** The Add button for the dialog. */
    private JButton addButton;
    
    /** The Remove button for the dialog. */
    private JButton removeButton;
    
    /** The shift Up button for the dialog. */
    private JButton upButton;
    
    /** The shift Down button for the dialog. */
    private JButton downButton;
    
    /* End dialog components */
    
    
    /**
     * The enumeration value which is added when the "ADD" button
     * is pushed. This is a validated version of the valueField.
     */
    private String suggestedValue = "";
    
    /**
     * The last typed enumeration name
     */
    private String lastNameTyped = "";

    /**
     * The number of suggested corrections to the enumeration name
     * field, which were added to the name combo box. 
     */
    private int suggestedNameCount;
    
    /**
     * List containing names of all enumerated types within the current module.
     * Once populated, this list is ordered ascendingly. 
     */
    private List<String> allEnumerationNamesList = new ArrayList<String>();
    
    /** The perspective this UI is running in. */
    private final Perspective perspective;
    
    
    /* Error codes returned by update state methods */
    
    /** No error */
    private final int ERROR_NONE = 0;
    
    /** Entered text value already exists in CAL compilation */
    private final int ERROR_EXISTING_ENTITY = 1;
    
    /**
     * Entered text is empty
     */
    private final int ERROR_EMPTY = 3;
    
    /**
     * Entered text value is already stored in this or related controls
     */
    private final int ERROR_ALREADY_STORED = 4;
    
    /**
     * Entered text is invalid, and no suggestions possible
     */
    private final int ERROR_NO_SUGGESTIONS = 5;
    
    /**
     * Entered text is invalid, but valid strings are suggested
     */
    private final int ERROR_CORRECTED = 6;
    

    /**
     * Flag indicating wether the user is editing an existing enumeration.
     */
    private boolean editingExistingEnumeration;
    
    /**
     * Flag indicating if an enumeration was modified
     */
    private boolean modifiedEnumeration;
    
    /**
     * Flag indicating if a valid enumeration name was entered
     */
    private boolean validEnumName;
    
    /**
     * (String->String) -- source name to source code. 
     * The list of source definitions we want to create. Ordered by insertion order, so that
     * definitions we want to create first will be created first.
     */
    private final Map<String, String> sourceDefinitions = new LinkedHashMap<String, String>();
    
    /** Enforced minimum size of this dialog */
    private final Dimension minimumSize;
    
    /** Timer will refresh window size whenever the dialog is resized **/
    private final Timer resizeTimer;
    
    
    /* Inner class declarations */
    
    /**
     * Mouse wheel listener for JComboBox class. On wheel rotate,
     * this listener shifts the current selection appropriately.
     *  
     * @author Iulian Radu
     */
    public class ComboBoxWheelListener implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            int items = ((JComboBox)e.getSource()).getItemCount();
            int delta = ((JComboBox)e.getSource()).getSelectedIndex() + e.getWheelRotation();
            
            if (items == 0) {
                delta = -1;
            } else {
                if (delta < 0) {
                    delta = 0;
                } else if (delta > items - 1) {
                    delta = items - 1;
                }
            }
            
            ((JComboBox)e.getSource()).setSelectedIndex(delta);
        }
    }
    
    
    /**
     * Extended JList class implementing capable of handling 
     * shift up/down and mouse drag operations,
     * 
     * Uses DefaultListModel for list models.
     * 
     * @author Iulian Radu
     */
    public class SwapList extends JList {
        
        private static final long serialVersionUID = -2905640590624599682L;
        /** Flag indicating if mouse is really dragging an item */
        private boolean isDragging = false;
        
        /**
         * If list gains focus and nothing is selected, select first
         * item if it exists.
         */
        private class ListFocusListener extends FocusAdapter {
            @Override
            public void focusGained(FocusEvent e) {
                int len = getModel().getSize();
                int selected = getSelectedIndex();
                if (len > 0 && (selected < 0 ||selected > len - 1)) {
                    setSelectedIndex(0);
                }
            }
        }
        
        /**
         * Key listener:
         * Listen for DELETE keypress, and remove selected item on action.
         * Listen for ESCAPE keypress, and abort drag or discard dialog on action.
         */
        private class ListKeyListener extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    removeSelected();
                    
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (!enumListControl.isDragging) {
                        dispose();
                    } else {
                        // Abort drag process
                        isDragging = false;
                        setCursor(null);
                        mouseHandler.updateSeparator(-1);
                        repaint();
                    }
                }
            }
        }
        
        /**
         * Inner class to handle mouse drag events for swapping items on the list.
         * Creation date: (20/01/04 9:48 AM)
         * @author Iulian Radu
         */
        private class MouseHandler extends org.openquark.gems.client.utilities.MouseClickDragAdapter {
            
            /** The index of the item clicked, if any. */
            private int indexClicked;

            /** The position of the separator between panels. */
            private int separatorPosition = -1;
            
            
            /**
             * Constructor for the Mouse Handler
             */
            private MouseHandler() {
                isDragging = false;
            }

            /**
             * Move the drag mode into the aborted state.
             */
            @Override
            protected void abortDrag() {
                super.abortDrag();
                isDragging = false;
                setCursor(DragSource.DefaultMoveNoDrop);
                clearSelection();
                indexClicked = -1;
            }

            /**
             * Select clicked item, and cancel drag if second button pushed.
             */
            @Override
            public void mousePressed(MouseEvent e){
                
                indexClicked = ((JList)e.getSource()).locationToIndex(e.getPoint());
                
                // Are dragging and second button clicked; abort
                if ((e.getButton() == MouseEvent.BUTTON3) && isDragging) {
                    isDragging = false;
                    setCursor(null);
                    updateSeparator(-1);
                    repaint();
                }
            }
            
            /**
             * Select clicked item.
             * @return boolean true if the click was a double click
             */
            @Override
            public boolean mouseReallyClicked(MouseEvent e){

                
                boolean doubleClicked = super.mouseReallyClicked(e);
                indexClicked = ((JList)e.getSource()).locationToIndex(e.getPoint());
                
                return doubleClicked;
            }
            
            /**
             * On drag motion, update separator.
             * 
             * @param e MouseEvent the relevant event
             * @param where Point the (possibly adjusted from e) coordinates of the drag
             * @param wasDragging boolean True: this is a continuation of a drag.  False: first call upon transition
             * from pressed to drag.
             */
            @Override
            public void mouseReallyDragged(MouseEvent e, Point where, boolean wasDragging) {
                if (isDragging) {
                    // ignore anything that is not a left mouse button
                    if (!SwingUtilities.isLeftMouseButton(e)) {
                        return;
                    }
                    
                    // looks nicer if we clear the selection
                    setSelectedIndex(indexClicked);
                    
                    // Update the on-screen separator
                    int newIndex = locationToSeparatorIndex(e.getPoint());
                    if (((JList)e.getSource()).getModel().getSize() > 0) {
                        updateSeparator(newIndex);
                    }

                    int i = ((JList)e.getSource()).locationToIndex(e.getPoint());
                    if (i > -1 && i < ((JList)e.getSource()).getModel().getSize()){
                        setCursor(DragSource.DefaultMoveDrop);
                    } else {
                        setCursor(DragSource.DefaultMoveNoDrop);
                    }
                }
            }
            
            /**
             * Carry out setup appropriate to enter the drag state.
             * @param e MouseEvent the mouse event which triggered entry into the drag state.
             */
            @Override
            public void enterDragState(MouseEvent e) {

                super.enterDragState(e);

                // ignore anything that is not a left mouse button
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
                
                // Get the part which the user clicked    
                indexClicked = ((JList)e.getSource()).locationToIndex(e.getPoint());
                isDragging = true;
            }
            
            /**
             * Carry out setup appropriate to exit the drag state.  
             * Dragged item is swapped into the finished drag position.
             * @param e MouseEvent the mouse event which caused an exit from the drag state
             */
            @Override
            public void exitDragState(MouseEvent e) {

                super.exitDragState(e);
                
                if (isDragging) {
                    
                    isDragging = false;
                    setCursor(null);
                    
                    // Where are we now?
                    int index = locationToSeparatorIndex(e.getPoint());
                    
                    // If we've finished dragging.  Finish up and do the appropriate moves
                    // Undraw the last separator
                    updateSeparator(-1);
                    
                    if (indexClicked != -1) {
                        
                        // if it's not a valid place to drop -> just go back to the old order
                        if (index < 0) {
                            index = indexClicked;
                        }
                        
                        // Do nothing if nothing changed.
                        if (index != indexClicked) {
                            DefaultListModel model = (DefaultListModel)getModel();
                            Object item = model.getElementAt(indexClicked);
                            model.remove(indexClicked);
                            
                            if (index > indexClicked) {
                                index = index-1;
                            }
                            if (index >= model.getSize()) {
                                model.addElement(item);
                            } else {
                                model.add(index,item);
                            }
                        }
                        setSelectedIndex(index);
                    } else {
                        // Clicked another mouse button, set selected to original
                        setSelectedIndex(indexClicked);
                    }
                    
                    setCursor(null);
                    indexClicked = -1;
                    repaint();
                }
            }
            
            /**
             * Convert a location in the JList to the index of the separator location to which it corresponds.
             * Creation date: (09/07/2001 6:16:28 PM)
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
                int maxIndex = numVarPanels;
                if (index > maxIndex) {
                    index = maxIndex;
                }

                return index;
            }
            
            /**
             * Draw or undraw a separator between elements in the JList
             * Creation date: (09/07/2001 6:15:57 PM)
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
             * Paint the separator.
             * Creation date: (09/07/2001 6:17:55 PM)
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
             * Update the displayed position of the separator.
             * Creation date: (12/07/2001 1:26:51 PM)
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
        }
        
        
        /** Constructor */
        public SwapList() {
            super();
            initialize();
        }
        
        /** 
         * Constructor
         * 
         * @param l list model to use
         */
        public SwapList(DefaultListModel l) {
            super(l);
            initialize();
        }
        
        /**
         * Initializes listeners for this object
         */
        private void initialize() {
            super.addKeyListener(new ListKeyListener());
            super.addFocusListener(new ListFocusListener());
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }
        
        /**
         * Shifts up the selected element
         * @return true if shift was successful, false if not
         */
        public boolean shiftUp() {
            int i = getSelectedIndex();
            
            if (i > 0) {
                return swapElements(i,i-1);
            } else {
                return false;
            }
        }
        
        /**
         * Shifts down the selected element
         * 
         * @return true if shift was successful, false if not
         */
        public boolean shiftDown() {
            int i = getSelectedIndex();
            
            if (i >= 0 && i < (super.getModel().getSize() - 1)) {
                return swapElements(i, i+1);
            } else {
                return false;
            }
        }
        
        /**
         * Swaps elements at the specified indices.
         * 
         * @param i index of first element
         * @param j index of second element
         * @return true if swap was successful; false if not
         */
        public boolean swapElements(int i, int j) {
            
            DefaultListModel m = (DefaultListModel) super.getModel();
            if ((i >= 0 && i <= (m.getSize() - 1)) &&
                (j >= 0 && j <= (m.getSize() - 1)) && (i != j)) {

                Object s = m.get(i);
                m.set(i, m.get(j));
                m.set(j, s);
                setSelectedIndex(j);
                return true;
                
            } else {
                return false;
            }
        }
        
        /**
         * Removes the currently selected item, if any.
         * 
         * @return true if remove was successful, false otherwise
         */
        public boolean removeSelected() {
            int i = getSelectedIndex();
            
            if (i >= 0 && super.getModel().getSize() > 0) {
                DefaultListModel m = (DefaultListModel) super.getModel();
                
                m.remove(i);
                if (m.getSize() > 0) {
                    this.setSelectedIndex((i == 0)? i : i-1);
                }
                
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * The tooltip reflects the value of the list item which the mouse
         * is pointing to. 
         */
        @Override
        public String getToolTipText(MouseEvent e) {
            
            int i = super.locationToIndex(e.getPoint());
            if (i >= 0 && i < super.getModel().getSize()) {
                return (String)super.getModel().getElementAt(i);
            } else {
                return "";
            }
        }
        
        /**
         * Handler for mouse events on the list
         */
        private MouseHandler mouseHandler = new MouseHandler();
        
        /**
         * Paint the Variables Display area.
         * Creation date: (09/07/2001 5:24:45 PM)
         * @param g Graphics the graphics object to use.
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            mouseHandler.paintSeparator();
        }
    }
    
    /**
     * Item renderer for the enumeration name combo box.
     * Keeps track of a list of items which should be displayed
     * with special icons (ie: existing enumerations).
     * 
     * @author Iulian Radu
     */
    private class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        
        private static final long serialVersionUID = 1050231330699652507L;
        /** 
         * List of existing enumeration names. Rendered items that
         * are contained within this list will have special icons
         */
        List<String> existentList;
        
        /** Constructor */
        public ComboBoxRenderer() {
            super();
            initialize(null);
        }
        
        /** Constructor */
        public ComboBoxRenderer(List<String> specialList) {
            super();
            if (specialList == null) {
                throw new NullPointerException();
            }
            initialize(specialList);
        }
        
        /** Set component attributes */
        private void initialize(List<String> list) {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(TOP);
            existentList = list;
        }
        
        /**
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            Icon icon;
            if ((existentList != null) && (existentList.contains(value))) {
                icon = ENUMERATION_ICON;
            } else {
                icon = CORRECTION_ICON;
            }
            String text = (String) value;
            
            setIcon(icon);
            setText(text);
            setFont(list.getFont());
            
            return this;
        }
    }
    
    /**
     * Label which automatically updates its tooltip when its text changes.
     * 
     * @author Iulian Radu
     */
    public class TippedLabel extends JLabel {
        private static final long serialVersionUID = 7910010835431701644L;
        /** Currently displayed tooltip */
        private JToolTip myTip;
        
        /** Create and save tooltip */
        @Override
        public JToolTip createToolTip() {
            myTip = super.createToolTip();
            return myTip;
        }
        
        /** Set text on the displayed tooltip also */
        @Override
        public void setToolTipText(String text) {
            super.setToolTipText(text);
            if (myTip != null) {
                myTip.setTipText(text);
                myTip.setSize(myTip.getPreferredSize());
            }
            return;
        }
    }
    
    
    /* Method declarations */

    /**
     * Constructor for a new generator ui.
     * @param parent the parent of the dialog
     * @param perspective the perspective the UI should use
     */
    public EnumeratedTypeGemGeneratorDialog(JFrame parent, Perspective perspective) {
        super(parent, true);
        
        if (perspective == null) {
            throw new NullPointerException();
        }
        this.perspective = perspective;
        
        // Initialize dialog components and add
        // listener to Cancel the dialog if the user presses ESC
        
        KeyListener dismissKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        };
        getOkButton().addKeyListener(dismissKeyListener);
        getCancelButton().addKeyListener(dismissKeyListener);
        privateButton.addKeyListener(dismissKeyListener);
        publicButton.addKeyListener(dismissKeyListener);
        getAddButton().addKeyListener(dismissKeyListener);
        getRemoveButton().addKeyListener(dismissKeyListener);
        getUpButton().addKeyListener(dismissKeyListener);
        getDownButton().addKeyListener(dismissKeyListener);
        getValueField().addKeyListener(dismissKeyListener);
        getValueField().setColumns(10);
        ((JTextField)getNameField().getEditor().getEditorComponent()).setColumns(10);
        getValueList();
        
        setTitle(GeneratorMessages.getString("ETGF_GenerateTypeTitle"));
        populateEnumerations();
        filterEnumerationsCombo("");
        gemNameField.setSelectedIndex(-1);
        getDerivedInstancesField().setSelectedItem(DerivedInstancesOption.EQ_ONLY_DERIVED_INSTANCES_OPTION);
        ((JTextField)gemNameField.getEditor().getEditorComponent()).setColumns(25);
        updateAllStates(false);
        ((DefaultListModel)enumListControl.getModel()).clear();
        
        suggestedNameCount = 0;
        editingExistingEnumeration = false;
        modifiedEnumeration = false;
        validEnumName = false;
        
        // Make the type name field have default focus
        
        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            private static final long serialVersionUID = -9147781078212867922L;
            @Override
            public Component getDefaultComponent(Container c) {
                return gemNameField;
            }
            @Override
            public Component getFirstComponent(Container c) {
                return gemNameField;
            }
        });
        
        // Default button is Add
        
        getRootPane().setDefaultButton(getAddButton());
        
        // Add items to dialog and pack
        
        getContentPane().setLayout(new BorderLayout());
        JPanel mainPanel = getMainPanel();
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        buttonGroup.add(publicButton);
        buttonGroup.add(privateButton);
        buttonGroup.setSelected(publicButton.getModel(), true);
        pack();
        
        // Ensure minimum size is maintained
        
        minimumSize = getSize();
        
        // Timer will check if the dialog size is under or equal to
        // minimum size, and simulate size change in order to
        // refresh window to current size. This simulation is needed
        // because on systems where window contents are displayed while
        // a window resizes, the size of the dialog may become out of sync
        // with its actual window. (refer to java bug: 4450706)
        resizeTimer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Dimension size = EnumeratedTypeGemGeneratorDialog.this.getSize();
                        if ((size.width <= minimumSize.width) ||
                            (size.height <= minimumSize.height)) {
                            
                            // The window of the dialog is only resized if
                            // the dialog size actually changes. So, simulate
                            // a size change.
                            setSize(new Dimension(size.width, size.height+1));
                            setSize(size);
                        }
                    }
                });
        resizeTimer.restart();
        resizeTimer.setRepeats(false);
        addComponentListener(new ComponentAdapter() {
            // Listener called whenever dialog size changes 
            // (eg: due to manual window resized, or setSize call)
            @Override
            public void componentResized(ComponentEvent e) {
                boolean wasBadSize = enforceMinimumSize(minimumSize);
                validate();
                if (wasBadSize) {
                    resizeTimer.restart();
                }
            }
        });
        
        // Handle loading / saving of preferences
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userNodeForPackage(EnumeratedTypeGemGeneratorDialog.class);
                PreferencesHelper.putDialogProperties(prefs, DIALOG_PROPERTIES_PREF_KEY, EnumeratedTypeGemGeneratorDialog.this);
            }
        });
        Preferences prefs = Preferences.userNodeForPackage(EnumeratedTypeGemGeneratorDialog.class);
        PreferencesHelper.getDialogProperties(prefs, DIALOG_PROPERTIES_PREF_KEY, this, new Dimension(625, 445), new Point(50, 50));
    }
    
    /**
     * If the window is smaller then its minimum size then the
     * size is set to the minimum size.
     * 
     * @return true if minimum dialog size has been enforced; false if not
     */
    private boolean enforceMinimumSize(Dimension minSize) {

        boolean wasBadSize = false;
        Dimension size = getSize();
        
        if (size.height < minSize.height) {
            size.height = minSize.height;
            wasBadSize = true;
        }
        if (size.width  < minSize.width) {
            size.width = minSize.width;
            wasBadSize = true;
        }
        
        setSize(size);
        return wasBadSize;
    }
    
    
    /**
     * Updates states of all components.
     * 
     * @param typedName true if update called due to keyboard event on
     * enumeration name combo box
     */
    private void updateAllStates(boolean typedName) {

        String statusText = GeneratorMessages.getString("ETGF_OkMessage");
        String statusToolTipText = statusText;
        Icon statusIcon = OK_ICON;

        boolean okEnabled = true;
        boolean addEnabled = true;
        boolean upEnabled = true;
        boolean downEnabled = true;
        boolean removeEnabled = true;
        
        // Update the Name field and OK button

        switch (updateEnumNameState(typedName)) {
            case ERROR_NONE :
            {
                validEnumName = true;
                break;
            }
            case ERROR_EMPTY :
            {
                String message = GeneratorMessages.getString("ETGF_TypeSomething");
                statusText = message;
                statusToolTipText = message;
                statusIcon = ERROR_ICON;
                okEnabled = false;
                validEnumName = false;
                break;
            }
            case ERROR_CORRECTED :
            {
                statusText = statusLabel.getText();
                statusToolTipText = statusLabel.getToolTipText();
                statusIcon = ERROR_ICON;
                okEnabled = false;
                validEnumName = false;
                break;
            }
            case ERROR_NO_SUGGESTIONS :
            {
                statusText = GeneratorMessages.getString("ETGF_InvalidType");
                statusToolTipText = statusLabel.getToolTipText();
                statusIcon = ERROR_ICON;
                okEnabled = false;
                validEnumName = false;
                break;
            }
            case ERROR_EXISTING_ENTITY :
            {
                String message = GeneratorMessages.getString("ETGF_DataTypeExists");
                statusText = message;
                statusToolTipText = message;
                statusIcon = WARNING_ICON;
                okEnabled = true;
                validEnumName = true;
                break;
            }
        }

        // Update Value field and Add button

        switch (updateValueNameState()) {
            case ERROR_NONE :
                break;
            case ERROR_CORRECTED :
            {
                if (statusIcon != ERROR_ICON) {
                    statusText = statusLabel.getText();
                    statusToolTipText = statusLabel.getToolTipText();
                    statusIcon = ERROR_ICON;
                }
                addEnabled = true;
                break;
            }
            case ERROR_EMPTY :
            {
                addEnabled = false;
                break;
            }
            case ERROR_NO_SUGGESTIONS :
            {
                if (statusIcon != ERROR_ICON) {
                    statusText = GeneratorMessages.getString("ETGF_InvalidValue");
                    statusToolTipText = statusLabel.getToolTipText();
                    statusIcon = ERROR_ICON;
                }
                addEnabled = false;
                break;
            }
            case ERROR_ALREADY_STORED :
            {
                if (statusIcon != ERROR_ICON) {
                    String errors = GeneratorMessages.getString("ETGF_RepeatedEnumeration");
                    statusText = errors;
                    statusToolTipText = "<html><body>" + errors + "</body></html>";
                    statusIcon = ERROR_ICON;
                }
                addEnabled = false;
                break;
            }
            case ERROR_EXISTING_ENTITY :
            {
                if (statusIcon != ERROR_ICON) {
                    statusText = statusLabel.getText();
                    statusToolTipText = statusLabel.getToolTipText();
                    statusIcon = ERROR_ICON;
                }
                addEnabled = false;
                break;
            }
        }

        // Update enumeration value list

        switch (updateValueListState(typedName)) {
            case ERROR_NONE :
                break;
            case ERROR_EMPTY :
            {
                if (statusIcon != ERROR_ICON) {
                    String message = GeneratorMessages.getString("ETGF_EnumerationNeeded");
                    statusText = message;
                    statusToolTipText = message;
                    statusIcon = ERROR_ICON;
                }
                okEnabled = false;
                break;
            }
        }

        statusLabel.setText(statusText);
        statusLabel.setToolTipText(statusToolTipText);
        statusLabel.setIcon(statusIcon);

        getOkButton().setEnabled(okEnabled);
        getAddButton().setEnabled(addEnabled);
        getUpButton().setEnabled(upEnabled);
        getDownButton().setEnabled(downEnabled);
        getRemoveButton().setEnabled(removeEnabled);

        // Update rest of buttons

        updateOtherButtonStates();

    }
    
    /**
     * Adds the suggested enumeration value string to the 
     * enumeration value list, and updates affected components
     *
     */
    private void addSuggestedValueToList() {
        
        modifiedEnumeration = true;
        ((DefaultListModel)enumListControl.getModel()).addElement(suggestedValue);
        valueField.setText("");
        updateAllStates(false); 
        valueField.requestFocus();
    }
    
    /**
     * Returns status of enumeration value list, and updates the list elements
     * if an existing enumeration was selected.
     * 
     * @param justTyped true if the state was changed while typing; false if not
     * @return error code indicating status of update operation
     */
    private int updateValueListState(boolean justTyped) {
        
        if (justTyped) {
            // If we are not modifying an enumeration
            if (!modifiedEnumeration) {
                
                if (editingExistingEnumeration) {
                    // Was looking at existing enumeration, so clear the fields
                    ((DefaultListModel)enumListControl.getModel()).clear();
                }
                
                // See if we are looking at existing enumeration, and repopulate if so
                editingExistingEnumeration = 
                        retrieveEnumerationValues(((JTextField)gemNameField.getEditor().getEditorComponent()).getText());
                if (editingExistingEnumeration) {
                    validEnumName = true;
                }
            }
        }
        
        // Check that at least one enumeration value has been added to the list
    
        if (((DefaultListModel)enumListControl.getModel()).getSize() < 1) {
            return ERROR_EMPTY;
            
        } else {
            return ERROR_NONE;
        }
    }
    
    /**
     * Updates the state of the Ok button to only be enabled if the user has entered
     * all required information. Also updates the information message displayed.
     *
     * @param justTyped true if the state was changed while typing; false if not 
     * @return error code indicating status of update operation
     */
    private int updateEnumNameState(boolean justTyped) {

        String gemName = ((JTextField)gemNameField.getEditor().getEditorComponent()).getText();
        
        // Check for empty string
        if (gemName == null || gemName.length()==0) {
            return ERROR_EMPTY;
        }
        
        // Check that typed value is not already in list
        if (!justTyped) {
            for (int i = 0; i < gemNameField.getItemCount(); i++) {
                if (gemName.equals(gemNameField.getItemAt(i))) {
                    if (i < suggestedNameCount) {
                        return ERROR_NONE;
                    } else {    
                        return ERROR_EXISTING_ENTITY;
                    }
                }
            }
        } else {
            justTyped = false;
        }
        
        // Validate and correct value name
        // The errors text will hold description of all errors
        // encountered while correcting.

        String errors = "";
        IdentifierUtils.ValidatedIdentifier validationResult = IdentifierUtils.makeValidatedIdentifier(gemName,true);
        if (!validationResult.isValid()) {
            
            // Convert error codes to strings and append to tooltip
            
            String firstError = validationErrorForTypeToString(validationResult.getNthError(0));
            errors = firstError+"<p>";
            for (int i = 1; i < validationResult.getNErrors(); i++) {
                errors += validationErrorForTypeToString(validationResult.getNthError(i))+"<p>";
            }
            
            // Add suggestions to value combo box, filtering the already existing
            // enumerations (if by chance a suggested enumeration exists in the module,
            // it will already be populated in the combo box)
            
            String filterString = gemName;
            String addItem = null;
            if (validationResult.hasSuggestion()) {
                filterString = validationResult.getSuggestion();
                if (perspective.getWorkingModuleTypeInfo().getTypeConstructor(validationResult.getSuggestion())!=null) {
                    // Suggestion is already in list
                } else {
                    addItem = validationResult.getSuggestion();
                }
                
            } else {
                addToNameCombo(null, "");
                statusLabel.setToolTipText("<html><body>" + errors + "</body></html>");
                return ERROR_NO_SUGGESTIONS;
            }
            addToNameCombo(addItem, filterString);
            
            // Report error
            
            statusLabel.setText(firstError);
            errors = errors + GeneratorMessages.getString("ETGF_Suggestion") + filterString + "<p>";
            statusLabel.setToolTipText("<html><body>" + errors + "</body></html>");
            return ERROR_CORRECTED;
        }
        
        // Check if data type with the given name already exists
        
        if (perspective.getWorkingModuleTypeInfo().getTypeConstructor(gemName) != null) {
            addToNameCombo(null, gemName);
            return ERROR_EXISTING_ENTITY;
        }
        
        // This component is ok
        
        addToNameCombo(null,gemName);
        return ERROR_NONE;
    }
    
    /**
     * Adds a value to the enumeration name combo box, and refreshes the
     * combo box after the list items have been filtered.
     * 
     * @param newItem value to be added to list (can be null)
     * @param filterString string to filter items by
     */
    private void addToNameCombo(String newItem, String filterString) {

        String editorValue = ((JTextField)gemNameField.getEditor().getEditorComponent()).getText();
        int ss = ((JTextField)gemNameField.getEditor().getEditorComponent()).getSelectionStart();
        int se = ((JTextField)gemNameField.getEditor().getEditorComponent()).getSelectionEnd();
        int cp = ((JTextField)gemNameField.getEditor().getEditorComponent()).getCaretPosition();
        boolean vis = gemNameField.getEditor().getEditorComponent().hasFocus();
        
        gemNameField.setPopupVisible(false);
        suggestedNameCount = 0;
        gemNameField.removeAllItems();
        if (newItem != null) {
            suggestedNameCount = 1;
            gemNameField.addItem(newItem);
        }
        filterEnumerationsCombo(filterString);
        gemNameField.setSelectedIndex(-1);
        if (gemNameField.getItemCount() <= 0) {
            vis = false;
        }
        
        gemNameField.setPopupVisible(vis);
        gemNameField.getEditor().setItem(editorValue);
        ((JTextField)gemNameField.getEditor().getEditorComponent()).select(ss, se);
        ((JTextField)gemNameField.getEditor().getEditorComponent()).moveCaretPosition(cp);
    }

    
    /**
     * Updates the state of the Add button to only be enabled if the user has entered
     * all required information. Also updates the information message displayed.
     * 
     * @return error code indicating status of update operation
     */
    private int updateValueNameState() {

        String valueName = valueField.getText();

        // Check for Empty string
        if (valueName == null || valueName.length() == 0) {
            return ERROR_EMPTY;
        }

        IdentifierUtils.ValidatedIdentifier validationResult = IdentifierUtils.makeValidatedIdentifier(valueName,true);
        
        if (validationResult.isValid()) {
            
            // The constructor string has valid syntax. Now check that suggestion does  
            // not belong to another type, and has not been added to our value list.
            
            suggestedValue = valueName;
            return checkSuggestedValue(suggestedValue);
        } 
        
        // Validate and correct value name
        // The errors text will hold description of all errors
        // encountered while correcting.

        // Convert error codes to strings and append to tooltip
        
        String firstError = validationErrorForValueToString(validationResult.getNthError(0));
        String errors = firstError+"<p>";
        int errorCount = validationResult.getNErrors();
        for (int i = 1; i < errorCount; i++) {
            errors += validationErrorForValueToString(validationResult.getNthError(i)) + "<p>";
        }
        
        // Check that suggestion does not belong to another
        // type, and has not been added to our value list.
        
        if (!validationResult.hasSuggestion() || 
            (checkSuggestedValue(validationResult.getSuggestion()) != ERROR_NONE)) {
            
            statusLabel.setToolTipText("<html><body>" + errors + "</body></html>");
            return ERROR_NO_SUGGESTIONS;
        }
            
        suggestedValue = validationResult.getSuggestion();
        statusLabel.setText(firstError);
        errors = errors + GeneratorMessages.getString("ETGF_Suggestion") + suggestedValue + "<p>";
        statusLabel.setToolTipText("<html><body>" + errors + "</body></html>");
        return ERROR_CORRECTED;
    }
    
    /**
     * Checks that the suggested enumeration value does not exist as a constructor
     * and has not been already added to the value list.
     * 
     * @param valueName suggested value
     * @return ERROR_NONE if no error;
     *         ERROR_ALREADY_STORED if the value is contained in the list
     *         ERROR_EXISTING_ENTITY if the value is already a constructor
     */
    private int checkSuggestedValue(String valueName) {
        if (valueName == null) {
            throw new NullPointerException();
        }
        
        // Check if specified value has already been added to value list

        if (((DefaultListModel)enumListControl.getModel()).contains(valueName)) {
            return ERROR_ALREADY_STORED;
        }
        
        // Check if specified value is not already a constructor
        
        DataConstructor constr = 
            perspective.getWorkingModuleTypeInfo().getDataConstructor(valueName);        
        if (constr == null) {
            return ERROR_NONE;
        }
        
        QualifiedName typeName = constr.getTypeConstructor().getName();
        if (!typeName.getUnqualifiedName().equals( ((JTextField)gemNameField.getEditor().getEditorComponent()).getText() )) {
            
            String statusMessage = "<html><body>"+
                GeneratorMessages.getString("ETGF_ExistingConstructorFor")+
                typeName.getUnqualifiedName()+
                "</body></html>";
                
            statusLabel.setToolTipText(statusMessage);
            statusLabel.setText(statusMessage);
            return ERROR_EXISTING_ENTITY;
        }
        
        return ERROR_NONE;
    }

    /**
     * Updates the state of Up,Down, and Remove buttons based on 
     * the enumListControl selection.
     */
    private void updateOtherButtonStates() {
        int index = enumListControl.getSelectedIndex();
        int maxIndex = ((DefaultListModel)enumListControl.getModel()).getSize() - 1;
        
        getRemoveButton().setEnabled(index >= 0 && index <= maxIndex);
        getUpButton().setEnabled    (index >  0 && index <= maxIndex);
        getDownButton().setEnabled  (index >= 0 && index <  maxIndex);
    }
    
    /**
     * Translates ValidationError enumerations, for type validation, into error 
     * message strings.
     * 
     * @param error to decode
     * @return error message string
     */
    private String validationErrorForTypeToString(IdentifierUtils.ValidationStatus error) {
        if (error == null) {
            throw new NullPointerException();
        }
        
        
        if (error == IdentifierUtils.ValidationStatus.INVALID_CONTENT) {
            return GeneratorMessages.getString("ETGF_IllegalTypeContentCharacters");
            
        } else if (error == IdentifierUtils.ValidationStatus.INVALID_START) {
            return GeneratorMessages.getString("ETGF_IllegalTypeStartCharacters");
            
        } else if (error == IdentifierUtils.ValidationStatus.NEED_UPPER) {
            return GeneratorMessages.getString("ETGF_CorrectionTypeUpperStart");
            
        } else if (error == IdentifierUtils.ValidationStatus.EXISTING_KEYWORD) {
            return GeneratorMessages.getString("ETGF_ExistingKeyword");
            
        } else {
            return GeneratorMessages.getString("ETGF_UnknownError");
        }
    }
    
    /**
     * Translates ValidationError enumerations, for value validation, into error 
     * message strings.
     * 
     * @param error to decode
     * @return error message string
     */
    private String validationErrorForValueToString(IdentifierUtils.ValidationStatus error) {
        if (error == null) {
            throw new NullPointerException();
        }
        
        if (error == IdentifierUtils.ValidationStatus.INVALID_CONTENT) {
            return GeneratorMessages.getString("ETGF_IllegalValueContentCharacters");
            
        } else if (error == IdentifierUtils.ValidationStatus.INVALID_START) {
            return GeneratorMessages.getString("ETGF_IllegalValueStartCharacters");
            
        } else if (error == IdentifierUtils.ValidationStatus.NEED_UPPER) {
            return GeneratorMessages.getString("ETGF_CorrectionValueUpperStart");
            
        } else if (error == IdentifierUtils.ValidationStatus.EXISTING_KEYWORD) {
            return GeneratorMessages.getString("ETGF_ExistingKeyword");
            
        } else {
            return GeneratorMessages.getString("ETGF_UnknownError");
        }
    }
    
    /**
     * @return the main panel that shows the contents of the dialog
     */
    private JPanel getMainPanel() {
        
        JPanel javaPanel = new JPanel();
        
        javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        javaPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(5, 5, 10, 5);
        javaPanel.add(statusLabel, constraints);
        statusLabel.setFont(getFont().deriveFont(Font.BOLD));
        statusLabel.setMaximumSize(statusLabel.getSize());
        
        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);
        javaPanel.add(new JLabel(GeneratorMessages.getString("ETGF_GemNameHeader")), constraints);
        
        constraints.gridx = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;        
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        javaPanel.add(getNameField(), constraints);
        
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.LINE_END;
        javaPanel.add(new JLabel(""), constraints);

        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        javaPanel.add(new JLabel(GeneratorMessages.getString("ETGF_VisibilityHeader")), constraints);
        
        constraints.gridx = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;        
        javaPanel.add(publicButton, constraints);

        constraints.gridx = 3;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;        
        javaPanel.add(privateButton, constraints);
        
        constraints.gridx = 4;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        javaPanel.add(new JLabel(""), constraints);
        
        constraints.gridx = 1;
        constraints.weightx = 0; 
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        javaPanel.add(new JLabel(GeneratorMessages.getString("ETGF_InstancesHeader")), constraints);
        
        constraints.gridx = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;        
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        javaPanel.add(getDerivedInstancesField(), constraints);
        
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.LINE_END;
        javaPanel.add(new JLabel(""), constraints);
        
        constraints.gridx = 1;
        constraints.weightx = 0; 
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        javaPanel.add(new JLabel(GeneratorMessages.getString("ETGF_EnumerationHeader")), constraints);
        
        constraints.gridx = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        javaPanel.add(getValueField(), constraints);
        
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.LINE_END;
        javaPanel.add(new JLabel(""), constraints);
        
        
        constraints.gridx = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridheight = 6;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.BOTH;
        if (enumListScrollPane == null) {
            getValueList();
        }
        javaPanel.add(enumListScrollPane, constraints);
                
        // Arrows go into separate right panel
        {
            JPanel p2 = new JPanel();
            p2.setLayout(new GridBagLayout());
      
            constraints.insets = new Insets(5, 5, 0, 5);
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.gridheight = 1;
            constraints.gridwidth = 1;
            p2.add(getUpButton(),constraints);
            
            constraints.gridx = GridBagConstraints.RELATIVE;
            constraints.gridy = 1;
            constraints.weightx = 1;
            constraints.weighty = 0;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.LINE_END;
            p2.add(new JLabel(""),constraints);
            
            constraints.gridx = 1;
            constraints.gridy++;
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.gridheight = 1;
            constraints.gridwidth = 1;
            p2.add(getDownButton(), constraints);

            constraints.insets = new Insets(0, 0, 5, 0);            
            constraints.gridx = GridBagConstraints.RELATIVE;
            constraints.gridy = 5;
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.gridwidth = 1;
            javaPanel.add(p2,constraints);
        }
        
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy++;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        javaPanel.add(getAddButton(), constraints);
        addButton.setMinimumSize(new Dimension(200,200));
        
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy++;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        javaPanel.add(getRemoveButton(), constraints);
        
        
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy++;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        javaPanel.add(new JLabel(""), constraints);
        
        return javaPanel;
    }
    
    /**
     * @return the panel that contains the buttons at the bottom of the dialog
     */
    private JPanel getButtonPanel() {
        
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(getOkButton());
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(getCancelButton());
        
        return buttonPanel;
    }
    
    /**
     * @return initialized enumeration value text field
     */
    private JTextField getValueField() {
        if (valueField == null) {

            valueField = new JTextField();
            valueField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    updateAllStates(false);
                 
                    if ((e.getKeyCode() == KeyEvent.VK_ENTER) && getAddButton().isEnabled()) {
                        
                        // Pushed ENTER, and possible to push Add; add value to list
                        addSuggestedValueToList();
                        return;
                    }
                }
            });
            valueField.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    // User selected an item from dropdown list; update suggestedValue to use
                    // (the dropdown is populated with only valid values)
                    
                    suggestedValue = valueField.getText();
                    updateAllStates(false);
                }
            });
        }
        return valueField; 
    }
    
    /**
     * @return initialized enumeration name combo box
     */
    private JComboBox getNameField() {
        if (gemNameField == null) {
            
            gemNameField = new JComboBox();
            gemNameField.setEditable(true);
            gemNameField.getEditor().setItem(null);
            gemNameField.setSelectedItem(null);
            gemNameField.addMouseWheelListener(new ComboBoxWheelListener());
            
            gemNameField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        if (gemNameField.isPopupVisible()) {
                            // Ignore it, processed by combobox to hide popup
                        } else {
                            dispose();
                        }
                        return;
                    }
                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                    
                    int selectedIndex = gemNameField.getSelectedIndex();
                    
                    // If ENTER pushed and name is valid, jump to value field
                    
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (validEnumName) {
                            if ((selectedIndex > -1) && (selectedIndex < suggestedNameCount)) {
                                ((JTextField)gemNameField.getEditor().getEditorComponent()).setText(
                                        (String)gemNameField.getItemAt(selectedIndex));
                                validEnumName = true;
                            }
                            lastNameTyped =
                                ((JTextField)gemNameField.getEditor().getEditorComponent()).getText();
                            valueField.requestFocus();
                            
                        } else {
                            // Select first item if possible
                            if (gemNameField.getItemCount() > 0) {
                                 
                                ((JTextField)gemNameField.getEditor().getEditorComponent()).setText(
                                        (String)gemNameField.getItemAt(0));

                                validEnumName = true;
                                lastNameTyped =
                                ((JTextField)gemNameField.getEditor().getEditorComponent()).getText();
                                valueField.requestFocus();
                            }
                        }
                        
                        return;
                    }
                    
                    // If ESC pushed, put back last typed text
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        gemNameField.setSelectedIndex(-1);
                        ((JTextField)gemNameField.getEditor().getEditorComponent()).setText(lastNameTyped);
                        updateAllStates(true);
                        gemNameField.hidePopup();
                        return;
                    }
                    
                    // Update states, and display whole list if nothing typed
                    String name = ((JTextField)gemNameField.getEditor().getEditorComponent()).getText();
                    updateAllStates((e.getKeyChar() != KeyEvent.CHAR_UNDEFINED));
                    if ((name == null) || (name.length() == 0)) {
                        gemNameField.setPopupVisible(false);
                        suggestedNameCount = 0;
                        gemNameField.removeAllItems();
                        filterEnumerationsCombo("");
                        gemNameField.setSelectedIndex(-1);
                        gemNameField.setPopupVisible(true);
                    }
                    
                    if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                        lastNameTyped = name;
                    }
                }
            });
            
            gemNameField.addItemListener(new ItemListener() { 
                public void itemStateChanged(ItemEvent e) {
                    // User selected an item from dropdown list; update list of enumeration values
                    // Note: the dropdown is populated with only valid values

                    boolean canSwitch = editingExistingEnumeration && !modifiedEnumeration;
                    if (canSwitch) {
                        // Was editing existing enumeration, so clear the fields
                        ((DefaultListModel)enumListControl.getModel()).clear();
                    }
                    if (!modifiedEnumeration) {
                        editingExistingEnumeration = 
                            retrieveEnumerationValues(((JTextField)gemNameField.getEditor().getEditorComponent()).getText());
                    }
                    
                    updateAllStates(false);
                    
                    // Do not highlight text
                    ((JTextField)gemNameField.getEditor().getEditorComponent()).select(((JTextField)gemNameField.getEditor().getEditorComponent()).getText().length(),0);
                }
            });
            
            gemNameField.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (validEnumName) {
                        // Valid name selected, so remove any suggestions
                        for (int i = 0; i < suggestedNameCount; i++) {
                            gemNameField.removeItemAt(0);
                        }
                        suggestedNameCount = 0;
                    }
                }
            });
        }
        gemNameField.setRenderer(new ComboBoxRenderer(allEnumerationNamesList));
        return gemNameField;
    }
    
    /**
     * A type-safe enumeration of the combo box options for the derived instances field in the dialog box.
     *
     * @author Joseph Wong
     */
    private static class DerivedInstancesOption {
        
        /**
         * The DerivedInstancesOption instance representing the option for generating only a derived Eq instance.
         */
        private static final DerivedInstancesOption EQ_ONLY_DERIVED_INSTANCES_OPTION =
            new EnumeratedTypeGemGeneratorDialog.DerivedInstancesOption(new QualifiedName[] { CAL_Prelude.TypeClasses.Eq});
            
        /**
         * The DerivedInstancesOption instance representing the option for generating the full complement of derived instances (i.e. Eq, Ord, Bounded, Enum).
         */
        private static final DerivedInstancesOption EQ_ORD_BOUNDED_ENUM_DERIVED_INSTANCES_OPTION =
            new EnumeratedTypeGemGeneratorDialog.DerivedInstancesOption(new QualifiedName[] { CAL_Prelude.TypeClasses.Eq, CAL_Prelude.TypeClasses.Ord, CAL_Prelude.TypeClasses.Bounded, CAL_Prelude.TypeClasses.Enum });
        
        /**
         * The string representation of the list of type class names to be displayed in the combo box.
         */
        private final String stringRep;
        
        /**
         * Constructs a DerivedInstancesOption instance representing the specified list of type class names.
         * @param classNames the type class names represented by this option.
         */
        private DerivedInstancesOption(QualifiedName[] classNames) {
            StringBuilder buf = new StringBuilder(classNames[0].getQualifiedName());
            
            for (int i = 1; i < classNames.length; i++) {
                buf.append(", ").append(classNames[i].getQualifiedName());
            }
            
            this.stringRep = buf.toString();
        }
        
        /**
         * @return the string representation of this option to be display in the combo box.
         */
        @Override
        public String toString() {
            return stringRep;
        }
    }
    
    /**
     * @return initialized derived instances combo box
     */
    private JComboBox getDerivedInstancesField() {
        if (derivedInstancesField == null) {
            
            derivedInstancesField = new JComboBox(DERIVED_INSTANCES_OPTIONS);
            derivedInstancesField.setEditable(false);
        }
        return derivedInstancesField;
    }
    
    /**
     * Adapter object in charge of managing focus for the
     * enumeration value list. It deselects the list if focus
     * is lost to a component other than a control button.
     */
    private FocusAdapter valueListDeselector = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            
            if ((e.getOppositeComponent() != getOkButton()) &&
                (e.getOppositeComponent() != getCancelButton()) &&
                (e.getOppositeComponent() != getAddButton()) &&
                (e.getOppositeComponent() != getRemoveButton()) &&
                (e.getOppositeComponent() != getUpButton()) &&
                (e.getOppositeComponent() != getDownButton()) &&
                (e.getOppositeComponent() != getValueList())) {
            
                getValueList().clearSelection();
            }
        }
    };
    
    /**
     * @return initialized value list control
     */
    private SwapList getValueList() {
        if (enumListControl == null) {
            
            enumListControl = new SwapList(new DefaultListModel());
            enumListControl.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    
                    // Shift items with ALT
                    if (e.isAltDown()) {
                        if (e.getKeyCode() == KeyEvent.VK_UP) {
                            enumListControl.shiftUp();
                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            enumListControl.shiftDown();
                        }
                    }
                    
                    updateAllStates(false); 
                }
            });
            enumListControl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            enumListControl.setVisibleRowCount(5);
            enumListControl.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    updateOtherButtonStates();                
                }
            });
            enumListScrollPane = new JScrollPane(enumListControl);
            enumListControl.addFocusListener(valueListDeselector);
            
            // Create mouse adapter which, on drag, sets the modified flag
            MouseClickDragAdapter newMouseHandler = new MouseClickDragAdapter() {
                @Override
                public void mouseReallyDragged(MouseEvent e, Point where, boolean wasDragging) {
                    if (enumListControl.getModel().getSize() > 0) {
                        modifiedEnumeration = true;
                    }
                }};
            enumListControl.addMouseListener(newMouseHandler);
            enumListControl.addMouseMotionListener(newMouseHandler);
        }
        return enumListControl;
    }
    
    /**
     * @return the OK button for the dialog
     */
    private JButton getOkButton() {
        
        if (okButton == null) {
            
            Action okAction = new AbstractAction(GeneratorMessages.getString("ETGF_OK")) {
                private static final long serialVersionUID = -5422279201196503598L;

                public void actionPerformed(ActionEvent e) {
                    generateSource();
                    Preferences prefs = Preferences.userNodeForPackage(EnumeratedTypeGemGeneratorDialog.class);
                    PreferencesHelper.putDialogProperties(prefs, DIALOG_PROPERTIES_PREF_KEY, EnumeratedTypeGemGeneratorDialog.this);
                    dispose();
                }
            };
            
            okButton = new JButton(okAction);
            okButton.addFocusListener(valueListDeselector);
            okButton.setPreferredSize(getCancelButton().getPreferredSize());
            okButton.setMnemonic(KeyEvent.VK_O);
        }
        
        return okButton;
    }
    
    /**
     * @return the cancel button for the dialog
     */
    private JButton getCancelButton() {
        
        if (cancelButton == null) {
            
            Action cancelAction = new AbstractAction(GeneratorMessages.getString("ETGF_Cancel")) {
                private static final long serialVersionUID = 3129182448714213946L;

                public void actionPerformed(ActionEvent e) {
                    Preferences prefs = Preferences.userNodeForPackage(EnumeratedTypeGemGeneratorDialog.class);
                    PreferencesHelper.putDialogProperties(prefs, DIALOG_PROPERTIES_PREF_KEY, EnumeratedTypeGemGeneratorDialog.this);
                    dispose();
                }
            };
            
            cancelButton = new JButton(cancelAction);
            cancelButton.setMnemonic(KeyEvent.VK_C);
            cancelButton.addFocusListener(valueListDeselector);
        }
        
        return cancelButton;
    }
    
    /**
     * @return the Add button for the dialog
     */
    private JButton getAddButton() {
        
        if (addButton == null) {
            
            Action addAction = new AbstractAction(GeneratorMessages.getString("ETGF_Add")) {
                private static final long serialVersionUID = -4808960566839453171L;

                public void actionPerformed(ActionEvent e) {
                    addSuggestedValueToList();
                }
            };
            
            addButton = new JButton(addAction);
            addButton.setMnemonic(KeyEvent.VK_A);
            addButton.addFocusListener(valueListDeselector);
        }
        
        return addButton;
    }
    
    /**
     * @return the REMOVE button for the dialog
     */
    private JButton getRemoveButton() {
        
        if (removeButton == null) {
            
            Action removeAction = new AbstractAction(GeneratorMessages.getString("ETGF_Remove")) {
                private static final long serialVersionUID = -7713372366025001184L;

                public void actionPerformed(ActionEvent e) {
                    enumListControl.removeSelected();
                    updateAllStates(false);
                    modifiedEnumeration = true;
                }
            };
            
            removeButton = new JButton(removeAction);
            removeButton.addFocusListener(valueListDeselector);
            removeButton.setMnemonic(KeyEvent.VK_R);
        }
        
        return removeButton;
    }
    
    /**
     * @return the DOWN button for the dialog
     */
    private JButton getDownButton() {
        
        if (downButton == null) {
            
            Action downAction = new AbstractAction() {
                private static final long serialVersionUID = -8750386517784977237L;

                public void actionPerformed(ActionEvent e) {
                   enumListControl.shiftDown();
                   modifiedEnumeration = true;
                }
            };
            
            downButton = new JButton(downAction);
            downButton.setIcon(DOWN_ICON);
            downButton.setMargin(new Insets(0, 0, 0, 0));
            downButton.addFocusListener(valueListDeselector);
        }
        
        return downButton;
    }
    
    /**
     * @return the UP button for the dialog
     */
    private JButton getUpButton() {
        
        if (upButton == null) {
            
            Action upAction = new AbstractAction() {
                private static final long serialVersionUID = -6879585081593228032L;

                public void actionPerformed(ActionEvent e) {
                    enumListControl.shiftUp();
                    modifiedEnumeration = true;
                }
            };
            
            upButton = new JButton(upAction);
            upButton.setIcon(UP_ICON);
            upButton.setMargin(new Insets(0, 0, 0, 0));
            upButton.addFocusListener(valueListDeselector);
        }
        
        return upButton;
    }
    
    /**
     * Filters the enumeration names in the current module to begin with 
     * the typed prefix, and introduces them in the enumeration name combo box.
     * 
     * The combo box is cleared of old items.
     */
    private void filterEnumerationsCombo(String filterString) {
        
        if (filterString == null) {
            filterString = "";
        }
        int ss = ((JTextField)gemNameField.getEditor().getEditorComponent()).getSelectionStart();
        int se = ((JTextField)gemNameField.getEditor().getEditorComponent()).getSelectionEnd();
        int cp = ((JTextField)gemNameField.getEditor().getEditorComponent()).getCaretPosition();
        
        // Read non-suggested items into a list and filter as we go
        for (int i = 0; i < allEnumerationNamesList.size(); i++) {
            String item = allEnumerationNamesList.get(i);
            if (item.toUpperCase().startsWith( filterString.toUpperCase() )) {
                gemNameField.addItem(item);
            }
        }
        
        gemNameField.getEditor().setItem(filterString);
        ((JTextField)gemNameField.getEditor().getEditorComponent()).select(ss, se);
        ((JTextField)gemNameField.getEditor().getEditorComponent()).moveCaretPosition(cp);
    }
    
    /**
     * Generates the source definitions for the data type 
     */
    private void generateSource() {
        
        String gemName = ((JTextField)gemNameField.getEditor().getEditorComponent()).getText();
        String genComment = GeneratorMessages.getString("ETGF_CALDeclComment") + gemName + "\n";
        StringBuilder source = new StringBuilder(genComment);
        Scope visibility = (publicButton.getModel().isSelected()) ? Scope.PUBLIC : Scope.PRIVATE;
        boolean eqInstanceOnly = (derivedInstancesField.getSelectedItem() == DerivedInstancesOption.EQ_ONLY_DERIVED_INSTANCES_OPTION);
        DefaultListModel enumListModel = ((DefaultListModel)enumListControl.getModel());
        
        String[] enumValues = new String[enumListModel.getSize()];
        for (int i = 0, n = enumListModel.getSize(); i < n; i++) {
            enumValues[i] = (String)enumListModel.get(i);
        }
        
        SourceModelUtilities.Enumeration enumObject = new SourceModelUtilities.Enumeration(QualifiedName.make(perspective.getWorkingModuleName(), gemName), visibility, enumValues, eqInstanceOnly);
        TopLevelSourceElement[] sourceElements = enumObject.toSourceElements();
        
        for (int i = 0, n = sourceElements.length; i < n; i++) {
            source.append(sourceElements[i].toSourceText());
            source.append("\n");
            if( !(sourceElements[i] instanceof FunctionTypeDeclaration) && i < n-1 ) {
                source.append("\n");
            }            
        }
        
        sourceDefinitions.put(gemName, source.toString());
    }
    
    /**
     * @return the new source definitions that should be created
     */
    public Map<String, String> getSourceDefinitions() {
        return sourceDefinitions;
    }
    
    /**
     * Populates the allEnumerationNamesList with  
     * existing enumerations (types with arity 0) from the
     * current module.
     *
     */
    private void populateEnumerations() {
        int numTypes = 
                perspective.getWorkingModuleTypeInfo().getNTypeConstructors();
        
        for (int i = 0; i < numTypes; i++) {
            
            TypeConstructor typeCons =
                    perspective.getWorkingModuleTypeInfo().getNthTypeConstructor(i);

            try {
                if (perspective.isEnumDataType(typeCons.getName()) && 
                    perspective.getWorkspace().checkDefinitionContent(perspective.getWorkingModuleName(), 
                                                                      typeCons.getName().getUnqualifiedName(), 
                                                                      GeneratorMessages.getString("ETGF_CALDeclComment"))) {
                    
                    allEnumerationNamesList.add(typeCons.getName().getUnqualifiedName());
                }
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, GeneratorMessages.getString("ETGF_ErrorReadingEnums") + ex.getLocalizedMessage(), 
                                             GeneratorMessages.getString("ETGF_PopulateFailed"), JOptionPane.ERROR_MESSAGE);
            }
        }
        
        Collections.sort(allEnumerationNamesList);
    }
    
    
    /**
     * If the specified enumeration exists, the dialog is populated
     * with the enumeration values and the derived instances settings.
     * 
     * @param enumName enumeration name
     * @return true if specified enumeration exists; false if not
     */
    private boolean retrieveEnumerationValues(String enumName) {
        ModuleTypeInfo workingModuleTypeInfo = perspective.getWorkingModuleTypeInfo();
        
        TypeConstructor typeCons = workingModuleTypeInfo.getTypeConstructor(enumName);
        
        if (typeCons != null) {
            ((DefaultListModel)enumListControl.getModel()).clear();
            
            if(typeCons.getScope() == Scope.PUBLIC) {
                publicButton.doClick();
            } else {
                privateButton.doClick();
            }
            
            int constructors = typeCons.getNDataConstructors();
            for (int i = 0; i < constructors; i++) {
                QualifiedName constructorName = typeCons.getNthDataConstructor(i).getName();
                ((DefaultListModel)enumListControl.getModel()).addElement(constructorName.getUnqualifiedName());
            }
            
            // We need to determine whether the existing enumeration has only a derived Eq instance,
            // or the full complement of derived instances (i.e. Eq, Ord, Bounded, Enum), so that
            // the correct selection can be shown in the derived instances combo box.
            
            TypeClass ordTypeClass = workingModuleTypeInfo.getVisibleTypeClass(CAL_Prelude.TypeClasses.Ord);
            ClassInstance ordInstance = workingModuleTypeInfo.getVisibleClassInstance(ordTypeClass, typeCons);
            
            TypeClass boundedTypeClass = workingModuleTypeInfo.getVisibleTypeClass(CAL_Prelude.TypeClasses.Bounded);
            ClassInstance boundedInstance = workingModuleTypeInfo.getVisibleClassInstance(boundedTypeClass, typeCons);
            
            TypeClass enumTypeClass = workingModuleTypeInfo.getVisibleTypeClass(CAL_Prelude.TypeClasses.Enum);
            ClassInstance enumInstance = workingModuleTypeInfo.getVisibleClassInstance(enumTypeClass, typeCons);
            
            if (ordInstance == null && boundedInstance == null && enumInstance == null) {
                getDerivedInstancesField().setSelectedItem(DerivedInstancesOption.EQ_ONLY_DERIVED_INSTANCES_OPTION);
            } else {
                getDerivedInstancesField().setSelectedItem(DerivedInstancesOption.EQ_ORD_BOUNDED_ENUM_DERIVED_INSTANCES_OPTION);                
            }
            return true;
            
        } else {
            return false;
        }
    }
}