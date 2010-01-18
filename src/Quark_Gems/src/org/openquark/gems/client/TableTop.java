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
 * TableTop.java
 * Creation date: (10/18/00 12:33:25 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.undo.StateEdit;
import javax.swing.undo.UndoableEdit;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.services.Status;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.DisplayedGem.DisplayedPart;
import org.openquark.gems.client.DisplayedGem.DisplayedPartConnectable;
import org.openquark.gems.client.DisplayedGem.DisplayedPartInput;
import org.openquark.gems.client.DisplayedGem.DisplayedPartOutput;
import org.openquark.gems.client.Gem.PartConnectable;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.Gem.PartOutput;
import org.openquark.gems.client.GemGraph.InputCollectMode;
import org.openquark.gems.client.GemGraph.TraversalScope;
import org.openquark.gems.client.Graph.LayoutArranger.Tree;
import org.openquark.gems.client.utilities.ExtendedUndoableEditSupport;
import org.openquark.gems.client.valueentry.ValueEditor;
import org.openquark.gems.client.valueentry.ValueEntryPanel;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;
import org.openquark.util.html.HtmlHelper;
import org.openquark.util.html.HtmlTransferable;
import org.openquark.util.ui.ImageTransferable;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The TableTop where Gems are combined etc.
 * This class represents the TableTop model, where a displayed gem hierarchy is added on top of the Gem Graph
 * hierarchy. The display component itself is stored in the TableTopPanel
 * Creation date: (10/18/00 12:33:25 PM)
 * @author Luke Evans
 */
/**
 * @author Neil Corkum
 *
 */
public class TableTop {

    /** The gap between 2 gems, set arbitrarily */
    static final int GAP_BETWEEN_GEMS = 15;

    /** The added padding to bounds of a selection before taking a snapshot of the tabletop */
    private static final int SNAPSHOT_BOUNDARY_PAD = 5;

    private final TableTopBurnManager burnManager;

    /** The JComponent that represents the tabletop */
    private TableTopPanel tableTopPanel;

    //
    // Sundry properties
    // 
    private final GemCutter gemCutter;
    private final GemGraph gemGraph;
    private Component focusStore;
    private final ExtendedUndoableEditSupport undoableEditSupport;
    private final CollectorArgumentStateEditable collectorArgumentStateEditable;


    /** The tabletop's display context for displayed gems. */
    private final DisplayContext displayContext;

    /** The Shape provider for the tabletop. */
    private final DisplayedGemShape.ShapeProvider shapeProvider = new TableTopShapeProvider();

    /** The tabletop's listener for events on gems */
    private final GemEventListener gemEventListener;

    /** The handler for code gem edits. */
    private final CodeGemEditor.EditHandler codeGemEditHandler;

    /** The tabletop's listener on displayed gem state changes. */
    private DisplayedGemStateListener gemStateListener;

    /** Listener for connection state. */
    private DisplayedConnectionStateListener connectionStateListener;

    /** Map from gem to displayed gem. */
    private final Map<Gem, DisplayedGem> gemDisplayMap;

    /** Map from connection to displayed connection. */
    private final Map<Connection, DisplayedConnection> connectionDisplayMap;

    /** Map from input to cached value for that input, the last time the input had values entered for it from Run Mode. */
    private final Map<PartInput, ValueNode> cachedArgValueMap;

    /** The gems in the TableTop which are selected. */
    private final Set<DisplayedGem> selectedDisplayedGems = new HashSet<DisplayedGem>();

    /** The gems in the TableTop which are running. */
    private final Set<DisplayedGem> runningDisplayedGems = new HashSet<DisplayedGem>();

    /** The connections in the TableTop which are considered "bad". */
    private final Set<DisplayedConnection> badDisplayedConnections = new HashSet<DisplayedConnection>();

    /** Maps codegems to their editors.  All DisplayedCodeGems should have an editor in the map. */
    private final Map<CodeGem, CodeGemEditor> codeGemEditorMap;

    /** The code gem editor map for unplaced code gem editors.  */
    private final Map<CodeGem, CodeGemEditor> unplacedCodeGemEditorMap;

    /** The DisplayedGem that currently has focus. */
    private DisplayedGem focusedDisplayedGem;

    /** The target collector for this tabletop. */
    private final DisplayedGem targetDisplayedCollector;      // final, unless loaded.  TODOEL: remove (calculate from gem graph's target)

    /**
     * As we expand and shrink the tabletop, the position of the 'original' origin is changed.
     * In order to maintain positional validity in historical data such as undo's, we'll store all such 
     * relative to this historical origin.
     */
    private Point originalOrigin = new Point(0,0);


    /**
     * Encapsulates information that displayed gems need to determine how to display themselves.
     * We don't want displayed gems to hold a reference to the table top directly.
     * @author Frank Worsley
     */
    public class DisplayContext {

        ModuleTypeInfo getContextModuleTypeInfo() {
            return getCurrentModuleTypeInfo();
        }
    }

    /**
     * The Shape Provider for the TableTop.
     * @author Edward Lam
     */
    private class TableTopShapeProvider implements DisplayedGemShape.ShapeProvider {
        /**
         * {@inheritDoc}
         */
        public DisplayedGemShape getDisplayedGemShape(DisplayedGem displayedGem) {
            Gem gem = displayedGem.getGem();

            if (gem instanceof CodeGem || gem instanceof FunctionalAgentGem) {
                return new DisplayedGemShape.Triangular(displayedGem);

            } else if (gem instanceof CollectorGem) {
                return new DisplayedGemShape.Oval(displayedGem);

            } else if (gem instanceof RecordFieldSelectionGem) {
                return new DisplayedGemShape.Triangular(displayedGem);    

            } else if (gem instanceof RecordCreationGem) {
                return new DisplayedGemShape.Triangular(displayedGem);    

            } else if (gem instanceof ReflectorGem) {
                boolean ovalShape = (gem.getNInputs() == 0);

                if (ovalShape) {
                    return new DisplayedGemShape.Oval(displayedGem);
                } else {
                    return new DisplayedGemShape.Triangular(displayedGem);
                }

            } else if (gem instanceof ValueGem) {
                final ValueGem valueGem = (ValueGem)gem;

                // Rectangular..
                DisplayedGemShape.InnerComponentInfo info = new DisplayedGemShape.InnerComponentInfo() {

                    public Rectangle getBounds() {
                        // Check for the case where the panel is not created yet..
                        ValueEntryPanel valueEntryPanel = getTableTopPanel().getValueEntryPanel(valueGem);
                        if (valueEntryPanel == null) {
                            return new Rectangle();
                        }
                        return valueEntryPanel.getBounds();
                    }

                    public void paint(TableTop tableTop, Graphics2D g2d) {
                        // Paint a fake VEP image so that the VEP can appear below other gems that might obscure this one.
                        ValueEntryPanel valueEntryPanel = tableTop.getTableTopPanel().getValueEntryPanel(valueGem);
                        Rectangle bounds = valueEntryPanel.getBounds();
                        Graphics vepGraphics = g2d.create(bounds.x, bounds.y, bounds.width, bounds.height);

                        // For some reason you can't just say vep.paint(vepGraphics)..
                        valueEntryPanel.paintOnTableTop(vepGraphics);
                        vepGraphics.dispose();
                    }

                    public List<Rectangle> getInputNameLabelBounds() {
                        return null;
                    }
                };

                return new DisplayedGemShape.Rectangular(displayedGem, info);

            } else {
                throw new IllegalArgumentException("Unrecognized gem type: " + gem.getClass());
            }
        }        
    }

    /**
     * The TableTop's handler for code gem edits.
     * @author Edward Lam
     */
    private class TableTopCodeGemEditHandler implements CodeGemEditor.EditHandler {

        /**
         * {@inheritDoc}
         */
        public void definitionEdited(CodeGemEditor codeGemEditor, PartInput[] oldInputs, UndoableEdit codeGemEdit) {

            CodeGem codeGem = codeGemEditor.getCodeGem();

            // Get the pre-state of argument target-related changes.
            // Note that we have to keep track of targeting info for arguments which disappear 
            //   (eg. a code gem input disappearing may cause emitter inputs to disappear as well!).
            // Get the emitter and collector argument state from before the disconnection.
            StateEdit collectorArgumentStateEdit = new StateEdit(collectorArgumentStateEditable);

            // The code gem has already updated, but arguments have not.
            getGemGraph().retargetArgumentsForDefinitionChange(codeGem, oldInputs);

            // Update the code gem editor state for connectivity.
            updateForConnectivity(codeGemEditor);

            // Post the edit to the undo manager.
            undoableEditSupport.postEdit(new UndoableCodeGemDefinitionEdit(getGemGraph(), codeGemEdit, collectorArgumentStateEdit));

            // Update the tabletop for the new gem graph state.
            updateForGemGraph();
        }
    }

    /**
     * A listener for events on gems and display gems.
     * Creation date: (02/25/02 6:16:00 PM)
     * @author Edward Lam
     */
    private class GemEventListener 
    implements BurnListener, InputChangeListener, GemStateListener, NameChangeListener, TypeChangeListener,
    CodeGemDefinitionChangeListener, DisplayedGemLocationListener, DisplayedGemSizeListener, DisplayedGemStateListener, 
    DisplayedConnectionStateListener {

        /**
         * Notify listeners that the validity of a connection changed.
         * @param e ConnectionStateEvent the related event.
         */
        public void badStateChanged(DisplayedConnectionStateEvent e) {
            // just repaint the bounds
            DisplayedConnection dConn = (DisplayedConnection)e.getSource();
            getTableTopPanel().repaint(dConn.getBounds());
        }

        /**
         * Notify listeners that an input was burned.
         * @param e BurnEvent the related event.
         */
        public void burntStateChanged(BurnEvent e){
            // Repaint the input which burnt.
            getTableTopPanel().repaint(getDisplayedPartConnectable((PartInput)e.getSource()).getBounds());
        }

        /**
         * Notify listeners that the gem size changed.
         * @param e DisplayedGemSizeEvent the related event.
         */
        public void gemSizeChanged(DisplayedGemSizeEvent e){
            DisplayedGem dGemSource = (DisplayedGem)e.getSource();
            Rectangle oldBounds = e.getOldBounds();

            // repaint old and new selection bounds
            getTableTopPanel().repaint(getSelectedBounds(dGemSource, oldBounds));
            getTableTopPanel().repaint(getSelectedBounds(dGemSource));

            // update connections
            updateDisplayedConnections(dGemSource);
        }

        /**
         * Notify listeners that the gem location changed.
         * @param e DisplayedGemLocationEvent the related event.
         */
        public void gemLocationChanged(DisplayedGemLocationEvent e) {
            DisplayedGem dGemSource = (DisplayedGem)e.getSource();
            Rectangle oldBounds = e.getOldBounds();

            // the location didn't actually change
            if (oldBounds.getLocation().equals(dGemSource.getLocation())) {
                return;
            }

            // repaint old and new selection bounds
            getTableTopPanel().repaint(getSelectedBounds(dGemSource, oldBounds));
            getTableTopPanel().repaint(getSelectedBounds(dGemSource));

            // update connections
            updateDisplayedConnections(dGemSource);

            // Notify the panel if it was a value gem.
            // TODOEL: Move this method/listener (and others) to TableTopPanel.  This should not be delegated.
            if (dGemSource.getGem() instanceof ValueGem) {
                tableTopPanel.handleValueGemMoved((ValueGem)dGemSource.getGem());
            }
        }

        /**
         * {@inheritDoc}
         */
        public void nameChanged(NameChangeEvent e) {
            Gem sourceGem = (Gem)e.getSource();
            getDisplayedGem(sourceGem).sizeChanged();
        }

        /**
         * Notify listeners that the run state changed.
         * @param e DisplayedGemStateEvent  the related event.
         */
        public void runStateChanged(DisplayedGemStateEvent e) {
            DisplayedGem dGemSource = (DisplayedGem)e.getSource();

            // repaint gem selection bounds
            getTableTopPanel().repaint(getSelectedBounds(dGemSource));
        }

        /**
         * Notify listeners that the selection state changed.
         * @param e DisplayedGemStateEvent the related event.
         */
        public void selectionStateChanged(DisplayedGemStateEvent e){
            // repaint gem selection bounds
            DisplayedGem dGemSource = (DisplayedGem)e.getSource();
            getTableTopPanel().repaint(getSelectedBounds(dGemSource));
        }

        /**
         * Notify listeners that the broken state changed.
         * @param e GemStateEvent the related event.
         */
        public void brokenStateChanged(GemStateEvent e){
            // Don't have to do anything.  Repaint events are triggered by definition change.
        }

        /**
         * Notify listeners that a code gem definition has changed
         * @param e GemDefinitionEvent the related event.
         */ 
        public void codeGemDefinitionChanged(CodeGemDefinitionChangeEvent e) {
            // inputsChanged() doesn't get called if the code gem unbreaks, but has 0 inputs.
            getTableTopPanel().repaint(getDisplayedGem((CodeGem)e.getSource()).getBounds());
        }

        /**
         * {@inheritDoc}
         */
        public void inputsChanged(InputChangeEvent e) {
            Gem sourceGem = (Gem)e.getSource();
            DisplayedGem displayedGem = getDisplayedGem(sourceGem);

            // Figure out the old and new sizes.
            int oldArity = displayedGem.getNDisplayedArguments();
            int newArity = displayedGem.getGem().getNInputs();

            // Update the displayed inputs.
            displayedGem.updateDisplayedInputs();
            getTableTopPanel().repaint(displayedGem.getBounds());

            // Emit a size change event, if applicable.
            // TODOEL: any size change event will cause this listener to repaint the bounds too.
            if (oldArity != newArity) {         
                displayedGem.sizeChanged();
            }

            // connections may have moved around
            updateDisplayedConnections(displayedGem);
        }

        /**
         * {@inheritDoc}
         */
        public void typeChanged(TypeChangeEvent e) {
            // When a part's type changes, repaint for the new type.
            PartConnectable partChanged = (PartConnectable)e.getSource();
            DisplayedPartConnectable displayedPartConnectable = getDisplayedPartConnectable(partChanged);
            if (displayedPartConnectable != null) {
                getTableTopPanel().repaint(displayedPartConnectable.getBounds());
            }
        }
    }

    /**
     * A comparator that will sort DisplayedGems alphabetically.  Comparison will be based on 
     * the value of a DisplayedValueGem or the name of all the other varieties of DisplayedGems.
     */
    private static final class DisplayedGemComparator implements Comparator<DisplayedGem> {

        public int compare(DisplayedGem o1, DisplayedGem o2) {

            Gem gem1 = o1.getGem();
            Gem gem2 = o2.getGem();

            // Get the comparison text for the first argument
            String text1;
            if (gem1 instanceof ValueGem) {
                text1 = ((ValueGem)gem1).getStringValue();

            } else if (gem1 instanceof NamedGem) {
                text1 = ((NamedGem)gem1).getUnqualifiedName();

            } else {
                throw new IllegalArgumentException("Unknown DisplayedGem " + o1);
            }                    

            // Get the comparison text for the second argument                
            String text2;
            if (gem2 instanceof ValueGem) {
                text2 = ((ValueGem)gem2).getStringValue();

            } else if (gem2 instanceof NamedGem) {
                text2 = ((NamedGem)gem2).getUnqualifiedName();

            } else {
                throw new IllegalArgumentException("Unknown DisplayedGem " + o2);
            }                    

            return text1.compareTo(text2);
        }
    }

    /**
     * TableTop default constructor.
     * @param gemCutter GemCutter the gemCutter of which this table top is part.
     */
    public TableTop(GemCutter gemCutter) {
        this.gemCutter = gemCutter;

        displayContext = new DisplayContext();

        gemDisplayMap = new LinkedHashMap<Gem, DisplayedGem>();
        connectionDisplayMap = new HashMap<Connection, DisplayedConnection>();

        // Use a weak hash map to reclaim unused value nodes when partInputs no longer used
        cachedArgValueMap = new WeakHashMap<PartInput, ValueNode>();

        // Use a weak collections so that we don't have to keep track of when the displayed code gem is no longer referenced.
        codeGemEditorMap = new WeakHashMap<CodeGem, CodeGemEditor>();

        unplacedCodeGemEditorMap = new WeakHashMap<CodeGem, CodeGemEditor>();

        // Instantiate some new members
        gemGraph = new GemGraph();
        collectorArgumentStateEditable = new CollectorArgumentStateEditable(gemGraph);
        targetDisplayedCollector = createDisplayedGem(gemGraph.getTargetCollector(), new Point());

        gemEventListener = new GemEventListener();
        codeGemEditHandler = new TableTopCodeGemEditHandler();
        undoableEditSupport = new ExtendedUndoableEditSupport(this);
        burnManager = new TableTopBurnManager(this);

        // Add the gem event listener as a state change listener.
        addStateChangeListener(gemEventListener);

        // Set the TableTop's focus traversal policy here.  This policy is needed due to the fact that
        // when a component (such as a Value Editor) is removed from a container (such as the TableTop) 
        // and it has focus, the focus will be shifted to the next component in the container's focus cycle.  
        // This extra focus event interferes with the desired operation of the Value Editors so we want to 
        // prevent it.  The focus event can be prevented by pretending there isn't a next component in the 
        // focus cycle.  The Java code will first try to find the next component for the Value Editor and 
        // if it is null, it will try to find the focus cycle's default component.  If we say the default 
        // component is null then a focus event is not created.
    }

    /**
     * Set up the target such that it is in an appropriate state for a new tabletop.
     */
    void resetTargetForNewTableTop() {

        // TODOEL: make private.

        String baseName = "result";
        String targetName = baseName;
        int i = 1;
        while (gemCutter.getPerspective().getVisibleGemEntity(QualifiedName.make(gemCutter.getWorkingModuleName(), targetName)) != null) {
            targetName = baseName + i;
            i++;
        }
        getTargetCollector().setName(targetName);

        // Don't notify the undo manager since this process should not be undoable.

        // Place it at the top right corner, leaving some space around it.
        // TODOEL: If the user invokes the "new" action several times quickly in sequence, the tabletop may not have had time to 
        //   re-institute itself.  The visible rect width would be zero, resulting in the gem being placed in the top left corner.
        int x = getTableTopPanel().getVisibleRect().width - targetDisplayedCollector.getBounds().width - 2 * DisplayConstants.HALO_SIZE;
        int y = 2 * DisplayConstants.HALO_SIZE;
        Point gemLocation = new Point(x, y);

        if (!gemDisplayMap.containsKey(targetDisplayedCollector.getGem())) {
            // This is the case when the tabletop is first created.
            // Ensure that the target collector is known to the tabletop data structures..
            addGemAndUpdate(targetDisplayedCollector, gemLocation);

            // Also make sure it gets painted.
            getTableTopPanel().repaint(getSelectedBounds(targetDisplayedCollector));

        } else {
            // The tabletop exists already, but a "new" action was invoked.
            // Just reset the target location.
            doChangeGemLocationUserAction(targetDisplayedCollector, gemLocation);
        }
    }

    /**
     * Update the state of the TableTop for changes in the GemGraph model.
     * The gem graph will be retyped, arg names disambiguated, and value gem panels revalidated.
     */
    void updateForGemGraph() {
        // update types
        try {
            gemGraph.typeGemGraph(getTypeCheckInfo());
        } catch (TypeException te) {
            GemCutter.CLIENT_LOGGER.log(Level.SEVERE, "Error type checking tabletop.");
        }

        // Update the panel for new editability state.
        getTableTopPanel().revalidateValueGemPanels();
    }

    /**
     * Get the displayed gem corresponding to a model gem
     * @param gem Gem the (model) gem in question
     * @return DisplayedGem the corresponding displayed gem
     */
    final DisplayedGem getDisplayedGem(Gem gem){
        return gemDisplayMap.get(gem);
    }

    /**
     * Get all the DisplayedGems on the TableTop.
     */
    Set<DisplayedGem> getDisplayedGems() {

        Set<DisplayedGem> displayedGems = new LinkedHashSet<DisplayedGem>();

        for (final Gem gem : gemDisplayMap.keySet()) {
            displayedGems.add(gemDisplayMap.get(gem));
        }

        return displayedGems;
    }

    /**
     * Get all the DisplayedConnections on the TableTop.
     */
    Set<DisplayedConnection> getDisplayedConnections() {
        return new HashSet<DisplayedConnection>(connectionDisplayMap.values());
    }

    /**
     * Create a displayed gem at the given location.
     * @param gem the gem to wrap in a displayed gem.
     * @param location the location of the displayed gem.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedGem(Gem gem, Point location) {
        return new DisplayedGem(displayContext, shapeProvider, gem, location);
    }

    /**
     * Create a displayed gem wrapping a new code gem at the given location.
     * @param location the location of the displayed gem.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedCodeGem(Point location) {
        return createDisplayedGem(new CodeGem(), location);
    }

    /**
     * Create a displayed gem wrapping a new collector gem at the given location.
     * @param location the location of the displayed gem.
     * @param targetCollector the new collector's target collector.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedCollectorGem(Point location, CollectorGem targetCollector) {
        return createDisplayedGem(new CollectorGem(targetCollector), location);
    }

    /**
     * Create a displayed gem wrapping a new RecordFieldSelection gem at the given location.
     * @param location the location of the displayed gem.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedRecordFieldSelectionGem(Point location) {
        return createDisplayedGem(new RecordFieldSelectionGem(), location);
    }
    
    /**
     * Create a displayed gem wrapping a new RecordCreation gem at the given location.
     * @param location the location of the displayed gem.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedRecordCreationGem(Point location) {
        return createDisplayedGem(new RecordCreationGem(),location);
    }
    
    
    /**
     * Create a displayed gem wrapping a new functional agent gem at the given location.
     * @param location the location of the displayed gem.
     * @param gemEntity the entity from which to construct the gem.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedFunctionalAgentGem(Point location, GemEntity gemEntity) {
        return createDisplayedGem(new FunctionalAgentGem(gemEntity), location);
    }

    /**
     * Create a displayed gem wrapping a new reflector gem at the given location.
     * @param location the location of the displayed gem.
     * @param collectorGem the emitter gem's collector.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedReflectorGem(Point location, CollectorGem collectorGem) {
        return createDisplayedGem(new ReflectorGem(collectorGem), location);
    }

    /**
     * Create a displayed gem wrapping a new value gem at the given location.
     * @param location the location of the displayed gem.
     * @return the resulting displayed gem, suitable for display on the tabletop.
     */
    DisplayedGem createDisplayedValueGem(Point location) {
        return createDisplayedGem(new ValueGem(), location);
    }

    /**
     * Get the target for this tabletop.
     * @return the target gem for this tabletop.
     */
    public CollectorGem getTargetCollector() {
        return (CollectorGem)targetDisplayedCollector.getGem();
    }

    /**
     * Get the target for this tabletop.
     * @return the target gem for this tabletop.
     */
    public DisplayedGem getTargetDisplayedCollector() {
        return targetDisplayedCollector;
    }

    /**
     * Get the corresponding display part for a given connectable part
     * @param part the part in question
     * @return the corresponding displayed part, or none if it does not exist.
     */
    DisplayedPartConnectable getDisplayedPartConnectable(PartConnectable part) {
        DisplayedGem displayedGem = getDisplayedGem(part.getGem());

        if (part instanceof PartInput) {

            // Check that the input number is within bounds.
            int inputIndex = ((PartInput)part).getInputNum();
            if (inputIndex < displayedGem.getNDisplayedArguments()) {

                // Check that the displayed input corresponds to the input part.
                DisplayedPartInput displayedPartInput = displayedGem.getDisplayedInputPart(inputIndex);
                if (displayedPartInput.getPartInput() == part) {
                    return displayedPartInput;
                }
            }

            // We might be in an inconsistent state.  Check against all displayed input parts..
            for (int i = 0, nDisplayedArgs = displayedGem.getNDisplayedArguments(); i < nDisplayedArgs; i++) {
                DisplayedPartInput displayedPartInput = displayedGem.getDisplayedInputPart(i);
                if (displayedPartInput.getPartInput() == part) {
                    return displayedPartInput;
                }
            }

            return null;

        } else if (part instanceof PartOutput) {
            return displayedGem.getDisplayedOutputPart();

        } else {
            throw new Error("Can't get the displayed part for this part: " + part.getClass());
        }
    }

    /**
     * Return the part of a gem under this point.
     * @param xy Point the coordinate to look under
     * @return DisplayedPart the part of the Gem under coord xy, or null if none are hit
     */ 
    DisplayedPart getGemPartUnder(Point xy) {
        // Hit test for each Gem.  
        DisplayedPart hitPart = null;

        for (final Gem gem : gemDisplayMap.keySet()) {
            DisplayedGem thisGem = gemDisplayMap.get(gem);

            // Ask the gem what part of it has been hit
            DisplayedPart thisGemHitPart = thisGem.whatHit(xy);
            if (thisGemHitPart != null) {
                hitPart = thisGemHitPart;
            }   
        }    
        return hitPart;
    }

    /**
     * Return the gem under this point.
     * @param xy Point the coordinate to look under
     * @return DisplayedGem the Gem under coord xy, or null if none are hit
     */ 
    DisplayedGem getGemUnder(Point xy) {
        // Hit test for each Gem.  
        DisplayedGem gemHit = null;

        for (final Gem gem : gemDisplayMap.keySet()){
            // Get the next element for consideration
            DisplayedGem thisGem = gemDisplayMap.get(gem);

            // Now ask it if it has been hit (overall)
            if (thisGem.anyHit(xy)) {
                gemHit = thisGem;
            }   
        }    
        return gemHit;
    }

    /**
     * Get the list of currently selected Gems.
     * @return Gem[] an array of all the selected gems in the tabletop
     */ 
    public Gem[] getSelectedGems() {

        // Start a list of gems
        List<Gem> selected = new ArrayList<Gem>();

        // Consider each Gem
        for (final Gem gem : gemGraph.getGems()){
            // Get the next element for consideration
            DisplayedGem displayedGem = getDisplayedGem(gem);

            // If this is selected, add it to the list

            if (displayedGem != null  && isSelected(displayedGem)) {
                selected.add(gem);
            }  
        }
        // Make return array and return it
        Gem[] gemArray = new Gem[selected.size()];
        gemArray = selected.toArray(gemArray);

        return gemArray; 
    }

    /**
     * Get the list of currently selected Gems.
     * @return DisplayedGem[] an array of all the selected display gems in the tabletop
     */ 
    public DisplayedGem[] getSelectedDisplayedGems() {

        // Start a list of gems
        List<DisplayedGem> selected = new ArrayList<DisplayedGem>();

        // Consider each Gem
        for (final DisplayedGem displayedGem : getDisplayedGems()){
            // If this is selected, add it to the list
            if (isSelected(displayedGem)) {
                selected.add(displayedGem);
            }  
        }
        // Make return array and return it
        DisplayedGem[] gemArray = new DisplayedGem[selected.size()];
        gemArray = selected.toArray(gemArray);

        return gemArray; 
    }

    /**
     * Get the DisplayedGem that currently has focus.
     * @return DisplayedGem
     */
    DisplayedGem getFocusedDisplayedGem() {
        return focusedDisplayedGem;
    }

    /**
     * Give focus to a new DisplayedGem.
     * @param focusedDisplayedGem
     */
    void setFocusedDisplayedGem(DisplayedGem focusedDisplayedGem) {

        // Make the new gem the one with focus, but remember the old gem.
        DisplayedGem oldFocusedDisplayedGem = this.focusedDisplayedGem;
        this.focusedDisplayedGem = focusedDisplayedGem;

        if (oldFocusedDisplayedGem != null) {

            if (oldFocusedDisplayedGem.getGem() instanceof ValueGem) {
                tableTopPanel.getValueEntryPanel((ValueGem)oldFocusedDisplayedGem.getGem()).setVisible(false);
            }

            getTableTopPanel().repaint(oldFocusedDisplayedGem.getBounds());
        }

        if (focusedDisplayedGem != null) {

            if (focusedDisplayedGem.getGem() instanceof ValueGem) {
                tableTopPanel.getValueEntryPanel((ValueGem)focusedDisplayedGem.getGem()).setVisible(true);
            }

            // Reinsert new gem into the map so that it appears as the gem drawn topmost
            gemDisplayMap.remove(focusedDisplayedGem.getGem());
            gemDisplayMap.put(focusedDisplayedGem.getGem(), focusedDisplayedGem);

            // Now repaint the new gem
            getTableTopPanel().repaint(focusedDisplayedGem.getBounds());
        }
    }

    /**
     * Returns the DisplayedGem that is closest to the supplied gem in the specified direction.
     * Returns null if there is no DisplayedGem in the specified direction.
     * @param direction NavigationDirection - the direction to search
     * @param currentGem DisplayedGem - the DisplayedGem to use as the origin of the search
     * @return DisplayedGem
     */
    DisplayedGem findNearestDisplayedGem(TableTopPanel.NavigationDirection direction, DisplayedGem currentGem) {

        // If the current gem is null then simply return here.
        if (currentGem == null) {
            return null;
        }

        // First figure out which direction we are to search.
        double lowerBearingRange, upperBearingRange;

        if (direction == TableTopPanel.NavigationDirection.UP) {
            upperBearingRange = 45;
            lowerBearingRange = 315;

        } else if (direction == TableTopPanel.NavigationDirection.RIGHT) {
            lowerBearingRange = 45;
            upperBearingRange = 135;

        } else if (direction == TableTopPanel.NavigationDirection.DOWN) {
            lowerBearingRange = 135;
            upperBearingRange = 225;

        } else if (direction == TableTopPanel.NavigationDirection.LEFT) {
            lowerBearingRange = 225;
            upperBearingRange = 315;

        } else {
            // Somehow we got a weird direction as an argument
            throw new IllegalArgumentException("Unknown direction: " + direction);
        }

        DisplayedGem nextGem = null;
        double distToNextGem = -1;
        List<DisplayedGem> stackedGems = new ArrayList<DisplayedGem>(); // To track the Gems that have the same center point

        // Iterate over all the displayed gems.
        for (final DisplayedGem dGem : getDisplayedGems()) {

            // Add the current gem to the stacked gems collection so it is in the proper order if a tie breaker is
            // needed.
            if (dGem == currentGem) {
                stackedGems.add(dGem);
                continue;
            }

            double bearing = calculateBearingBetweenDisplayedGems(currentGem, dGem);

            // If the bearing is -1 then the two Gems are right on top of each other so remember this.
            if (bearing == -1) {
                stackedGems.add(dGem);
                continue;
            }

            if ((direction == TableTopPanel.NavigationDirection.UP && (bearing > lowerBearingRange || bearing <= upperBearingRange))
                    || (bearing > lowerBearingRange && bearing <= upperBearingRange)) {

                // We have found a gem that is in the right direction.
                // See if it is nearer than the last one we have been holding onto
                double dist = currentGem.getCenterPoint().distance(dGem.getCenterPoint());

                if (distToNextGem == -1 || dist < distToNextGem) {
                    nextGem = dGem;
                    distToNextGem = dist;
                }
            }
        }

        // If there are two or more gems in the stacked gems list (there should always be at least 1) we have 
        // have a tie-breaker to figure out which one to go to next
        if (stackedGems.size() > 1) {
            // Try to get the next Gem based on alphabetical order.  If we get one back then use it.
            DisplayedGem possibleNextGem = findNextGemAlphabetically(currentGem, stackedGems, direction);
            if (possibleNextGem != null) {
                nextGem = possibleNextGem;
            }
        }

        // Return the next gem (it will be null if there are no Gems in the desired direction).
        return nextGem;
    }

    /**
     * Return the next DisplayedGem in the supplied collection based on alphabetical order and direction.  
     * Directions of UP and LEFT will return the gem alphabetically before the current gem while
     * DOWN and RIGHT will return the gem alphabetically after the current gem in the supplied collection.
     * Null will be returned if there isn't a next gem.  DisplayedValueGems will be alphabetized according to
     * its actual value while the other DisplayedGems will be sorted based on their names.
     * NOTE: the current gem must be included in the collection of DisplayedGems.
     * 
     * @param currentGem find the DisplayedGem that follows this one alphabetically
     * @param gems the collection of Gems to look in
     * @param direction determines which way to go in alphabetical order to get the next Gem 
     * @return DisplayedGem
     */
    private static DisplayedGem findNextGemAlphabetically(DisplayedGem currentGem, 
            Collection<DisplayedGem> gems,
            TableTopPanel.NavigationDirection direction) {

        // Make sure the current gem is in the collection of displayed gems
        if (!gems.contains(currentGem)) {
            throw new Error("Programming Error: currentGem must be in the DisplayedGem collection");
        }

        // Copy the collection so we can play with it
        List<DisplayedGem> gemList = new ArrayList<DisplayedGem>(gems);

        // Sort the copied collection 
        Collections.sort(gemList, new DisplayedGemComparator());

        // Calculate the index of the displayed gem to return based on the index of the current gem.
        int nextIndex = -1;
        if (direction == TableTopPanel.NavigationDirection.UP || direction == TableTopPanel.NavigationDirection.LEFT) {
            nextIndex = gemList.indexOf(currentGem) - 1;

        } else if (direction == TableTopPanel.NavigationDirection.DOWN || direction == TableTopPanel.NavigationDirection.RIGHT) {
            nextIndex = gemList.indexOf(currentGem) + 1;
        }

        // Return the correct displayed gem.
        if (nextIndex >= 0 && nextIndex < gemList.size()) {
            return gemList.get(nextIndex);

        } else {
            // No Gem to return
            return null;
        }
    }

    /**
     * Returns the bearing between the center points of two DisplayedGems.  
     * The bearing is greater than or equal to 0, but less than 360 degrees.
     * 0 degrees is at the 12 o'clock position, 90 degrees is at 3 o'clock, 180 degrees is at 6 o'clock
     * and 270 degrees is at 9 o'clock.  If the center points of the two Gems are right on top of 
     * each other then -1 is returned.
     * 
     * @param fromDGem DisplayedGem - the starting DisplayedGem
     * @param toDGem DisplayedGem - the ending DisplayedGem
     * @return double - the bearing between the fromDGem and toDGem in degrees
     */
    private static double calculateBearingBetweenDisplayedGems(DisplayedGem fromDGem, DisplayedGem toDGem) {

        // Get the locations of the two DisplayedGems
        Point2D fromCP = fromDGem.getCenterPoint();
        Point2D toCP = toDGem.getCenterPoint();

        // Calculate the deltas (remember the co-ordinate system begins in the top left corner of the TableTop
        // with increasing X's going to the right and increasing Y's going down).
        double deltaX = toCP.getX() - fromCP.getX();
        double deltaY = fromCP.getY() - toCP.getY();

        // Check that the two center points are not the same.
        if (deltaX == 0 && deltaY == 0) {
            return -1;
        }

        // Find the angle and adjust for which quadrant we're in
        double angle = Math.toDegrees(Math.atan(deltaX / deltaY));
        if (toCP.getY() > fromCP.getY()){
            // We're in the lower hemisphere - angles in the lower right quadrant are -90 to 0 and
            // angles in the lower left quadrant are 0 to 90 (all rotating clockwise) 
            // so adding 180 gives us the correct bearing
            return 180 + angle;

        } else {
            if (toCP.getX() < fromCP.getX()) {
                // We're in the upper left quadrant - angles run from 0 to -90 (going counter clockwise)
                return 360 + angle;

            } else {
                // We're in the upper right quadrant - the angle is the correct bearing
                return angle;
            }
        }
    }

    /**
     * Determines where a DisplayedGem should be located.
     * @param displayedGem DisplayedGem - the DisplayedGem that needs a home.
     * @return Point
     */
    Point findAvailableDisplayedGemLocation(DisplayedGem displayedGem) {

        // Get the existing viewable TableTop area and the DisplayedGem dimensions
        Rectangle dGemBounds = displayedGem.getBounds();
        Rectangle visibleRect = getTableTopPanel().getVisibleRect();
        int visibleRectMaxX = (int)visibleRect.getMaxX();
        int visibleRectMaxY = (int)visibleRect.getMaxY();

        // Set up a variable to track where (in the Y direction) to start the next search row
        int initialFreeRowValue = 100000;
        int nextFreeRow = initialFreeRowValue;

        // Determine a rectangle where we can begin searching for a free spot
        Rectangle searchRect = new Rectangle(visibleRect.x, visibleRect.y, dGemBounds.width, dGemBounds.height);

        // Keep looping until we find a free spot or until the algorithm gives up.
        while (true) {

            // Does the search rectangle intersect any existing Gem bounds?
            Rectangle collisionRect = findDisplayedGemPositionCollisions(searchRect);

            if (collisionRect == null) {
                // No collisions were found... we found a free space
                return searchRect.getLocation();

            } else {
                // There were collisions so move our search rectangle just past the collision area
                searchRect.x = (int)collisionRect.getMaxX() + TableTopPanel.SEARCH_HORIZONTAL_SPACING;

                // Remember the lowest Y value for the collision bounds so we know a safe place to start 
                // the next search row
                nextFreeRow = (int)Math.min(nextFreeRow, collisionRect.getMaxY());
            }

            // We didn't find a free space and have moved the search rectangle.  Make sure the new rectangle 
            // will allow enough room for the DisplayedGem to fit into the visible area horizontally
            if (visibleRectMaxX < searchRect.getMaxX()) {
                // The DisplayedGem won't fit horizontally so try to search one row down

                if (visibleRectMaxY < (nextFreeRow +  searchRect.height)) {
                    // If we continue searching 1 more row down, the DisplayedGem won't fit into the viewable area
                    // vertically so just place the DisplayedGem at the origin of the visible area
                    return visibleRect.getLocation();

                } else {
                    // We can fit at least one more search row in and we are currently off the right side 
                    // of the visible area so start searching again from the left side but 1 row down
                    searchRect.x = visibleRect.x;
                    searchRect.y = nextFreeRow;

                    // Reset the variable that tracks the next free row
                    nextFreeRow = initialFreeRowValue;
                }
            }
        }
    }

    /**
     * Helper function that tests a potential DisplayedGem position for collisions with
     * existing DisplayedGems.  Returns the spanning bounds of all the DisplayedGems with which 
     * collisions occurred or null if there were no collisions.
     * @param possibleRect Rectangle - the area to check for existing DisplayedGems.
     * @return Rectangle
     */
    private Rectangle findDisplayedGemPositionCollisions(Rectangle possibleRect) {

        // Use a rectangle to track the bounds of any DisplayedGems that we collide with
        Rectangle collisionRect = null;

        // Iterate over the existing DisplayedGems and see if the possible position conflicts with anything.
        for (final Gem gem : gemDisplayMap.keySet()){
            Rectangle dGemBounds = gemDisplayMap.get(gem).getBounds();
            if (possibleRect.intersects(dGemBounds)) {

                if (collisionRect == null) {
                    collisionRect = dGemBounds;
                } else {
                    collisionRect.add(dGemBounds);
                }
            }
        }
        return collisionRect;
    }

    /**
     * Get the code gem editor associated with a code gem.
     * @param codeGem the code gem whose code editor to return.  This may be null if a code editor
     * has not yet been created (ie. never shown).
     * @return CodeGemEditor the code editor associated with this displayed code gem
     */
    CodeGemEditor getCodeGemEditor(CodeGem codeGem) {
        return codeGemEditorMap.get(codeGem);
    }

    /**
     * Returns false if the proposed name already exists on the desktop as
     * a collector or a code gem, Returns true otherwise.
     * @param name
     * @param gem the gem to be renamed
     */
    boolean isAvailableCodeOrCollectorName(String name, Gem gem) {

        // must check that the name is not the name of another collector
        Set<CollectorGem> matchingCollectors = gemGraph.getCollectorsForName(name);
        for (final CollectorGem matchingCollector : matchingCollectors) {
            if (matchingCollector != gem) {
                return false;
            }
        }

        // Find all the names on the tabletop and see if the proposed name is among them    
        // and that the owner of the name is not the gem to be renamed.
        Set<Gem> allGems = gemGraph.getGems();
        Set<String> otherCodeGems = new HashSet<String>();
        for (final Gem nextGem : allGems) {
            if (nextGem instanceof CodeGem &&  nextGem != gem) {
                otherCodeGems.add(((CodeGem)nextGem).getUnqualifiedName());
            }
        }
        return (!otherCodeGems.contains(name));
    }


    /**
     * Returns whether or not a code gem's editor is visible.
     * @param codeGem the code gem whose code editor is in question.
     * @return boolean true if the code editor is visible (of if it's null).
     */
    boolean isCodeEditorVisible(CodeGem codeGem) {

        CodeGemEditor codeGemEditor = getCodeGemEditor(codeGem);
        if (codeGemEditor == null) {
            return false;
        }
        return codeGemEditor.isVisible();
    }    

    /**
     * Get a new code gem editor for a given codegem.
     * @param codeGem the code gem whose code editor should be shown/hidden
     * @return a new CodeGemEditor for the code gem.
     */
    private CodeGemEditor getNewCodeGemEditor(final CodeGem codeGem) {

        // Create the code editor
        CodeGemEditor codeGemEditor = new CodeGemEditor(gemCutter, codeGem, codeGemEditHandler, gemCutter.getPerspective(), 
                gemGraph, gemCutter.getNavigatorOwner(), false, gemCutter.getCodeGemAnalyser());

        // Set it up
        String title = GemCutterMessages.getString("CGE_EditorTitle", codeGem.getUnqualifiedName());
        codeGemEditor.setName(title);
        codeGemEditor.setTitle(title);        
        codeGemEditor.setResizable(true);

        // Alter the default behaviour of the editor so that it does nothing on closing and
        // handle the closing here
        codeGemEditor.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        codeGemEditor.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                showCodeGemEditor(codeGem, false);
            }
        });

        // Add a listener to the code editor that will cause the associated code gem to be 
        // repainted when the editor is shown or hidden (so that the open editor indicator is painted).
        codeGemEditor.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent evt) {
                CodeGemEditor codeEditor = (CodeGemEditor)evt.getComponent();
                DisplayedGem displayedCodeGem = getDisplayedGem(codeEditor.getCodeGem());
                getTableTopPanel().repaint(getSelectedBounds(displayedCodeGem));
            }

            @Override
            public void componentHidden(ComponentEvent evt) {
                CodeGemEditor codeEditor = (CodeGemEditor)evt.getComponent();
                DisplayedGem displayedCodeGem = getDisplayedGem(codeEditor.getCodeGem());
                // May be null if the code gem is being deleted.
                if (displayedCodeGem != null) {
                    getTableTopPanel().repaint(getSelectedBounds(displayedCodeGem));
                }
            }
        });

        codeGemEditor.getGlassPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                gemCutter.enterGUIState(GemCutter.GUIState.EDIT);
            }
        });

        // Add a listener to the code editor that will cause the associated code gem to be repainted
        // when the editor is activated or deactivated (so the proper open editor indicator is displayed);
        codeGemEditor.addWindowListener(new WindowAdapter() {

            @Override
            public void windowActivated(WindowEvent evt) {

                if (gemCutter.getGUIState() == GemCutter.GUIState.ADD_GEM) {
                    gemCutter.enterGUIState(GemCutter.GUIState.EDIT);
                }

                CodeGemEditor codeEditor = (CodeGemEditor)evt.getWindow();
                DisplayedGem codeGem = getDisplayedGem(codeEditor.getCodeGem());
                getTableTopPanel().repaint(getSelectedBounds(codeGem));
            }

            @Override
            public void windowDeactivated(WindowEvent evt) {
                CodeGemEditor codeEditor = (CodeGemEditor)evt.getWindow();
                DisplayedGem codeGem = getDisplayedGem(codeEditor.getCodeGem());
                getTableTopPanel().repaint(getSelectedBounds(codeGem));
            }
        });

        return codeGemEditor;
    }

    /**
     * Show or hide a CodeGem's codeEditor.
     * @param codeGem the code gem whose code editor should be shown/hidden
     * @param show boolean show it if true, otherwise hide if it is showing
     */
    void showCodeGemEditor(CodeGem codeGem, boolean show) {

        if (show) {

            // If this is the first time we're displaying the editor, set its position to be right 
            // below the code gem.
            CodeGemEditor codeGemEditor = unplacedCodeGemEditorMap.remove(codeGem);
            if (codeGemEditor != null) {
                codeGemEditor.doSyntaxSmarts();
                Rectangle gemBounds = getDisplayedGem(codeGem).getBounds();
                Point editorPt = new Point(gemBounds.x + 15, gemBounds.y + gemBounds.height);

                SwingUtilities.convertPointToScreen(editorPt, TableTop.this.getTableTopPanel());
                codeGemEditor.setLocation(editorPt);
            }

            // Get the code gem editor, even if it's already placed.
            codeGemEditor = codeGemEditorMap.get(codeGem);

            // Make sure the editor is completely visible on the screen when it is opened.  Make sure the bottom, 
            // right, left, and top edges are visible in this order to ensure that the top left 
            // corner will always be visible.
            Rectangle editorBounds = codeGemEditor.getBounds();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            if (screenSize.height < editorBounds.getMaxY()) {
                editorBounds.y -= (editorBounds.getMaxY() - screenSize.height);
            }

            if (screenSize.width < editorBounds.getMaxX()) {
                editorBounds.x -= (editorBounds.getMaxX() - screenSize.width);
            }

            if (editorBounds.x < 0) {
                editorBounds.x = 0;
            }

            if (editorBounds.y < 0) {
                editorBounds.y = 0;
            }

            codeGemEditor.setBounds(editorBounds);

            // Now show it
            codeGemEditor.setVisible(true);

        } else {

            // Only bother if there's something to do
            if (isCodeEditorVisible(codeGem)) {
                // Hide the code editor and move to the back
                CodeGemEditor codeEditor = getCodeGemEditor(codeGem);
                codeEditor.setVisible(false);
            }                
        }
    }

    /**
     * Will close/hide all the code gem editors.
     */
    void hideAllCodeGemEditors() {

        for (final DisplayedGem displayedGem : getDisplayedGems()) {
            Gem gem = displayedGem.getGem();

            if (gem instanceof CodeGem) {
                showCodeGemEditor((CodeGem)gem, false);
            }
        }       
    }

    /**
     * Displays a text field from which a collector's name may be edited
     * @param collectorGem
     */
    void displayLetNameEditor(CollectorGem collectorGem) {
        TableTopPanel.EditableGemNameField nameField = 
            getTableTopPanel().new EditableGemNameField(collectorGem.getUnqualifiedName(), collectorGem);
        getTableTopPanel().add(nameField);

        // calculate the "right" place to put it
        Rectangle bodyBounds = getDisplayedGem(collectorGem).getDisplayedGemShape().getBodyBounds();
        Rectangle fieldBounds = nameField.getBounds();

        int widthDif = bodyBounds.width - fieldBounds.width;
        int heightDif = bodyBounds.height - fieldBounds.height;

        int newX = bodyBounds.x + widthDif / 2;
        int newY = bodyBounds.y + heightDif / 2;

        nameField.setLocation(newX, newY);
        nameField.requestFocus();

        // ensure the cursor is visible
        nameField.scrollCaretToVisible();

    }

    /**
     * Displays a text field from which a code gem's name may be edited
     * @param codeGem 
     */
    void displayCodeNameEditor(CodeGem codeGem) {
        TableTopPanel.EditableGemNameField nameField = 
            getTableTopPanel().new EditableGemNameField(codeGem.getUnqualifiedName(), codeGem);
        getTableTopPanel().add(nameField);

        nameField.setLocation(getEditorPositionForTriangularGem(codeGem, nameField));
        nameField.requestFocus();

        // ensure the cursor is visible
        nameField.scrollCaretToVisible();        
    }

    /**
     * Displays an editor for the field extracted by an RecordFieldSelection Gem 
     * @param recordFieldSelectionGem 
     * @return the newly created field editor, or null id the editor has nothing to edit
     */
    JComponent displayRecordFieldSelectionEditor(final RecordFieldSelectionGem recordFieldSelectionGem) {
        // get correct editor to display
        final JComponent fieldEditor = RecordFieldSelectionGemFieldNameEditor.makeEditor(recordFieldSelectionGem, this);

        if (fieldEditor != null) {
            getTableTopPanel().add(fieldEditor);

            // display editor
            fieldEditor.setLocation(getEditorPositionForTriangularGem(recordFieldSelectionGem, fieldEditor));
            fieldEditor.requestFocus();
            
            
        }
        return fieldEditor;
    }
    
    /**
     * Get position to place editor for triangular gem field.
     * @param gem 
     * @param field editor for RecordFieldSelection gem field
     */
    Point getEditorPositionForTriangularGem(Gem gem, JComponent field) {
        // calculate the "right" place to put it
        Rectangle bodyBounds = getDisplayedGem(gem).getDisplayedGemShape().getBodyBounds();
        Rectangle fieldBounds = field.getBounds();

        int heightDif = bodyBounds.height - fieldBounds.height;

        int newX = bodyBounds.x + DisplayConstants.INPUT_OUTPUT_LABEL_MARGIN * 2;// + dotSpace;
        int newY = bodyBounds.y + heightDif / 2;
        
        return new Point(newX, newY);
    }
    
    /**
     * Display an editor which the field name may be edited
     * @param rcGem 
     * @param fieldToRename the field to be renamed
     */
    void displayFieldRenameEditor(RecordCreationGem rcGem, FieldName fieldToRename){
        RecordFieldRenameEditor fieldEditor = new RecordFieldRenameEditor(rcGem, fieldToRename, this);
        
        if(fieldEditor != null){
            getTableTopPanel().add(fieldEditor);
           
            // Calculate the location to place the editor
            int index = rcGem.getFieldIndex(fieldToRename);
            Point connectPt = getDisplayedGem(rcGem).getDisplayedGemShape().getInputConnectPoint(index);
    
            int inBoundWidth = getDisplayedGem(rcGem).getDisplayedGemShape().getInBounds().width;
            Rectangle fieldBounds = fieldEditor.getBounds();
            
            int newX = connectPt.x + inBoundWidth + DisplayConstants.BEVEL_WIDTH_X + DisplayConstants.INPUT_OUTPUT_LABEL_MARGIN / 2;
            int newY = connectPt.y - fieldBounds.height / 2 ;
           
            fieldEditor.setLocation(new Point(newX, newY));
            fieldEditor.requestFocus();
            
        }
    }

    /**
     * Find space underneath the preferred location to insert the gems specified in 'insertions'
     * @param preferredLocation
     * @param insertions
     * @return Point
     */
    Point findSpaceUnderneathFor(Point preferredLocation, DisplayedGem[] insertions) {

        // Get the bounds of this insertion
        int j = 0;
        Rectangle rectangle = null;
        for (j = 0; j < insertions.length; j++) {
            if (insertions[j] != null) {
                rectangle = insertions[j].getBounds();
                break;
            } 
        }
        for (; j < insertions.length; j ++) {
            if (insertions[j] != null) {
                rectangle.add(insertions[j].getBounds());
            }
        }
        if (rectangle == null) {
            return preferredLocation;
        }

        // the closest location is the preferred location!
        Point closestLocation = preferredLocation;

        // The set of displayed gems on the table top
        Set<DisplayedGem> displayedGems = getDisplayedGems();

        // We only want to consider the gems that are not being inserted
        displayedGems.removeAll(Arrays.asList(insertions));

        List<Rectangle> gemsUnderneath = new ArrayList<Rectangle>();

        // the farthest right x coordinate in the bounds
        int rightX = preferredLocation.x + rectangle.width;        

        // sort them in order of their y coordinates 
        for (final DisplayedGem displayedGem : displayedGems) {
            Rectangle gemBounds = displayedGem.getBounds();
            if ((gemBounds.x < rightX && (gemBounds.x + gemBounds.width) > preferredLocation.x) &&
                    (gemBounds.y >= preferredLocation.y )) {
                gemsUnderneath.add(gemBounds);
            }
        }

        // Use this comparator
        Comparator<Rectangle> boundsComparator = new Comparator<Rectangle>() {
            /**
             * @see Comparator#compare(Object, Object)
             */
            public int compare(Rectangle o1, Rectangle o2) {
                return o1.y - o2.y;
            }
        };    

        // ... use the built in merge-sort function
        Collections.sort(gemsUnderneath, boundsComparator);

        int size =  gemsUnderneath.size();

        if (size > 0) {
            Rectangle rect = gemsUnderneath.get(0);

            if ((rect.y - closestLocation.y) >= rectangle.height +10) {
                return closestLocation;
            }

            // Take the first available slot where the gems would fit.
            for (int i = 0; i < size; i++) { 
                rect = gemsUnderneath.get(i);
                closestLocation = new Point(closestLocation.x,  rect.y + rect.height + 10);
                if (i == (size - 1)) {
                    break;
                } else {
                    Rectangle rect2 = gemsUnderneath.get(i + 1);
                    if ((rect2.y - closestLocation.y) >= (rectangle.height + 10)) {
                        break;
                    }
                }
            }
        }
        return closestLocation;
    }

    /**
     * Get the GemGraph associated with this TableTop
     * @return GemGraph the gem graph associated with this Manager
     */
    GemGraph getGemGraph() {
        return gemGraph;
    }

    /**
     * Returns the reference point that used to be the initial origin
     * @return Point
     */
    Point getOriginalOrigin() {
        return originalOrigin;
    }

    /**
     * Get TypeCheckInfo for the tabletop.
     * @return TypeCheckInfo the typecheck info for the current state of the tabletop
     */
    TypeCheckInfo getTypeCheckInfo() {
        return gemCutter.getTypeCheckInfo();
    }

    /**
     * Get the ModuleTypeInfo for the current module.
     * @return ModuleTypeInfo.
     */
    ModuleTypeInfo getCurrentModuleTypeInfo() {
        return getTypeCheckInfo().getModuleTypeInfo();
    }

    /**
     * Get the intellicut manager.
     * @return IntellicutManager the intellicut manager.
     */
    IntellicutManager getIntellicutManager() {
        return gemCutter.getIntellicutManager();
    }    

    /**
     * @return the table top panel that displays the table top
     */
    public TableTopPanel getTableTopPanel() {
        if (tableTopPanel == null) {
            tableTopPanel = new TableTopPanel(this, gemCutter);
        }
        return tableTopPanel; 
    }

    /**
     * Set the running state of the tabletop.
     * @param running boolean whether or not we are in the run state
     */
    void setRunning(boolean running) {
        if (running) {
            // put a gray cast on the tabletop
            getTableTopPanel().setBackground(Color.lightGray);
        } else {
            // not running
            getTableTopPanel().setBackground(Color.white);
        }

        // Disable mouse events if entering the run state (gems shouldn't be allowed to move)
        // Otherwise enable them.
        getTableTopPanel().enableMouseEvents(!running);
    }

    /**
     * Ensure the target collector is located correctly for its docked state and the size of the TableTop.
     */
    void checkTargetDockLocation() {

//      DisplayedGem targetCollector = getTargetDisplayedCollector();
//      if (targetCollector == null) {
//      return;
//      }

//      // Get the TableTop bounds and the current location of the TargetGem
//      Rectangle ttRect = getTableTopPanel().getVisibleRect();
//      Point targetLoc = getTargetDisplayedCollector().getLocation();
//      if (targetDocked) {
//      // Make sure its at the dock location
//      int haloPlusBlur = HALO_SIZE + HALO_BLUR_SIZE;
//      Point docLoc = new Point(ttRect.width - targetCollector.getDimensions().width - haloPlusBlur, haloPlusBlur);
//      if (targetLoc.x != docLoc.x || targetLoc.y != docLoc.y) {
//      // Need to relocate the target and get it to repaint correctly
//      targetCollector.setLocation(docLoc);
//      }   

//      }
    }



    /**
     * Save the currently focused component in the table top, if any.
     */
    void saveFocus() {
        // just store the focus owner into the focus store
        focusStore = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    }

    /**
     * Restore the focus to the stored focused component.
     */
    void restoreFocus() {

        if (focusStore != null) {
            // Need special action if we're dealing with focus in a ValueEditor.
            Component searchAncestor = focusStore;

            while (searchAncestor != null) {

                if (searchAncestor instanceof ValueEditor) {

                    gemCutter.getValueEditorHierarchyManager().activateEditor((ValueEditor)searchAncestor);
                    focusStore = null;
                    return;
                }
                searchAncestor = searchAncestor.getParent();
            }   

            // Else, just request focus.            
            focusStore.requestFocus();  

            focusStore = null;
        }
    }

    /**
     * Obtain the undoable edit support object maintained by the tabletop.
     * This method is intended to be called only by classes which are closely related to the tabletop, so that
     * they have a way to notify the tabletop that the coming edits can be aggregated, and can be given a
     * certain name.  Normal edits should be posted to the gemCutter's undo manager.
     * For instance, this is called by the intellicut panel to aggregate intellicut-add gem edits 
     * (add the selected gem, connect it up..)
     * @return ExtendedUndoableEditSupport the undoable edit support object maintained by the tabletop.
     */
    ExtendedUndoableEditSupport getUndoableEditSupport() {
        return undoableEditSupport;
    }

    /**
     * Set the cached value node for an input.
     * @param arg PartInput the input for which to cache the value
     * @param vn ValueNode the value node to cache.
     */
    void cacheArgValue(PartInput arg, ValueNode vn){
        cachedArgValueMap.put(arg, vn);
    }

    /**
     * Get the cached value node for this sink.
     * @param arg PartInput the input for which a value is cached
     * @return ValueNode the cached value node or null if none
     */
    final ValueNode getCachedValue(PartInput arg){
        return cachedArgValueMap.get(arg);
    }

    /**
     * Clear cached arguments.
     * @param displayedGem DisplayedGem the gem for which to clear the cached values
     */
    void clearCachedArguments(DisplayedGem displayedGem) {

        // grab unbound input parts on the tree rooted by the connected gem
        List<PartInput> inputParts = displayedGem.getTargetArguments();

        // iterate over the gathered input parts.
        for (final PartInput inputPart : inputParts) {
            // clear the cached type
            cacheArgValue(inputPart, null);
        }
    }

    /**
     * Get the type colour for a connectable displayed part
     * @return Color the type colour
     */
    Color getTypeColour(DisplayedPartConnectable part) {
        // Colour is black if connected
        if (part.isConnected()) {
            return getDefaultLineColor();
        }   

        // Return the input type colour
        TypeColourManager typeColourManager = gemCutter.getTypeColourManager();
        return (typeColourManager == null) ? 
                getDefaultLineColor() : 
                    typeColourManager.getTypeColour(part.getPartConnectable().getType());
    }

    private Color getDefaultLineColor() {
        return getTableTopPanel().isPhotoLook() ? Color.BLACK : SystemColor.textText;
    }

    /**
     * Update the displayed connections for a gem.  Unpaint any old connections, paint new ones.
     * @param displayedGem DisplayedGem the gem whose connections to repaint
     */
    private final void updateDisplayedConnections(DisplayedGem displayedGem) {
        List<DisplayedPartConnectable> connectableParts = displayedGem.getConnectableDisplayedParts();

        for (final DisplayedPartConnectable part : connectableParts) {
            DisplayedConnection displayedConnection = part.getDisplayedConnection();
            if (displayedConnection != null) {
                // unpaint the old connection (recall: painting performs XOR's) if any
                part.repaintConnection(getTableTopPanel());

                // clear any cached connection
                part.purgeConnectionRoute();

                // recalculates a new connection and repaints it (if any)
                part.repaintConnection(getTableTopPanel());
            }
        }
    }

    /**
     * Get a new ConnectionRoute between the two given parts.
     * @param from DisplayedPartConnectable the source part
     * @param to DisplayedPartConnectable the destination part
     * @return ConnectionRoute the new route
     */ 
    static ConnectionRoute getConnectionRoute(DisplayedPartConnectable from, DisplayedPartConnectable to) {

        // Get the start and end points for the route
        return new ConnectionRoute(from.getConnectionPoint(), to.getConnectionPoint());
    }

    /**
     * Get the bounds of the GemGraph.
     * Note: this method iterates over all the gems (ie. O(n) in the number of gems..)
     * @return Rectangle the bounds of the GemGraph.
     */ 
    private Rectangle getGemGraphBounds() {

        return getGemBounds(getDisplayedGems());
    }

    /**
     * Get the bounds of the selected gems
     * Note: this method iterates over all the gems (ie. O(n) in the number of gems..)
     * @return Rectangle the bounds of the selected gems
     */
    private Rectangle getSelectedGemsBounds() {
        return getGemBounds(Arrays.asList(getSelectedDisplayedGems()));
    }

    /**
     * Get the bounds of the specified gems
     * Note: this method iterates over all the gems (ie. O(n) in the number of gems..)
     * @param gems the gems to include in the bound
     * @return Rectangle the bounds of the gems
     */
    private Rectangle getGemBounds(Collection<DisplayedGem> gems) {

        // Declare the rectangle
        Rectangle rect = null;

        if (!gems.isEmpty()){
            for (final DisplayedGem displayedGem : gems) {
                if (rect == null){
                    rect = displayedGem.getBounds();
                } else {
                    rect.add(displayedGem.getBounds());
                }
            }
        } else {
            // No gems - return a trivial rectangle.  Maybe this should never happen?
            rect = new Rectangle();
        }

        return rect; 
    }

    /**
     * Determine the Gem's bounds when selected.
     * 
     * @param displayedGem the gem for which to calculate bounds
     * @return Rectangle the bounds of the Gem when selected
     */
    private static Rectangle getSelectedBounds(DisplayedGem displayedGem) {
        return getSelectedBounds(displayedGem, displayedGem.getBounds());
    }

    /**
     * Determine the Gem's bounds when selected.
     * This calculates the amount by which a set of bounds must be expanded, depending on the
     * type of gem which is passed in.
     * 
     * Note that Gems whose selection 'halos' lie entirely within one or more of the dimensions of
     * their overall bounds (getBounds()) will not need to expand the selection rectangle in those 
     * dimensions.  For instance an InputOutputGem has provision for both an input and output 
     * area and hence only needs to grow in the Y dimension.
     * 
     * @param displayedGem the gem for which to calculate bounds
     * @param bounds the bounds from which to calculate.
     * @return Rectangle the bounds of the Gem when selected
     */
    private static Rectangle getSelectedBounds(DisplayedGem displayedGem, Rectangle bounds) {
        Gem gem = displayedGem.getGem();

        // Get the regular bounds and add a contribution to the width and height caused by the 'glow'
        Rectangle selBounds = new Rectangle(bounds);
        selBounds.y -= DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE;
        selBounds.height += (DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE) * 2;

        // Grow to the right if there is no output
        if (gem.getOutputPart() == null) {
            selBounds.width += DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE;
        }

        // Grow to the left if there are no inputs
        if (gem.getInputParts().length == 0) {
            selBounds.x -= DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE;
            selBounds.width += DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE;
        }

        return selBounds;
    }

    /**
     * Add an undoable edit listener.
     * @param uel 
     */
    public void addUndoableEditListener(javax.swing.event.UndoableEditListener uel) {
        undoableEditSupport.addUndoableEditListener(uel);
    }

    /**
     * Remove an undoable edit listener.
     * @param uel 
     */
    public void removeUndoableEditListener(javax.swing.event.UndoableEditListener uel) {
        undoableEditSupport.removeUndoableEditListener(uel);
    }

    /**
     * Adds the specified state change listener to receive state change events from this gem .
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the state listener.
     */
    public synchronized void addStateChangeListener(DisplayedGemStateListener l) {
        if (l == null) {
            return;
        }
        gemStateListener = GemEventMulticaster.add(gemStateListener, l);
    }

    /**
     * Removes the specified state change listener so that it no longer receives state change events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the state listener.
     */
    public synchronized void removeStateChangeListener(DisplayedGemStateListener  l) {
        if (l == null) {
            return;
        }
        gemStateListener = GemEventMulticaster.remove(gemStateListener, l);
    }

    /**
     * Adds the specified connection state listener to receive connection state events from this input.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the connection state listener.
     */
    public synchronized void addConnectionStateListener(DisplayedConnectionStateListener l) {
        if (l == null) {
            return;
        }
        connectionStateListener = GemEventMulticaster.add(connectionStateListener, l);
    }

    /**
     * Removes the specified connection state listener so that it no longer receives connection state events.
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the connection state listener
     */
    public synchronized void removeConnectionStateListener(DisplayedConnectionStateListener l) {
        if (l == null) {
            return;
        }
        connectionStateListener = GemEventMulticaster.remove(connectionStateListener, l);
    }


    /*
     * Methods for actions                               ********************************************
     */

    /**
     * Add a gem.
     * @param displayedGem DisplayedGem the gem to add
     * @param addPoint Point the location to add the gem
     */
    void addGem(DisplayedGem displayedGem, Point addPoint) {

        // Set the location of the Gem
        displayedGem.setLocation(addPoint);

        // Add the Gem to the Gem graph.
        Gem gemModel = displayedGem.getGem();
        if (displayedGem != targetDisplayedCollector) {
            gemGraph.addGem(gemModel);
        }

        // Now handle the creation of the displayed gem, and adding of the gem to the gem graph.
        handleDisplayedGemAdded(displayedGem);

        // TODOEL: TEMP.  Reassign the collector's input argument to be the target, if this is a new gem.
        if (gemModel instanceof CollectorGem) {
            CollectorGem collectorGem = (CollectorGem)gemModel;
            Gem.PartInput collectingPart = collectorGem.getCollectingPart();

            if (collectorGem != getTargetCollector() && collectorGem.getTargetArguments().contains(collectingPart)) {
                retargetInputArgument(collectingPart, getTargetCollector(), -1);
            }
        }
    }

    /**
     * Add a gem and update the tabletop for the addition.
     * @param displayedGem DisplayedGem the gem to add
     * @param addPoint Point the location to add the gem
     */
    private void addGemAndUpdate(DisplayedGem displayedGem, Point addPoint) {
        addGem(displayedGem, addPoint);

        // Update the tabletop for the new gem graph state.
        updateForGemGraph();

        resizeForGems();
    }

    /**
     * Handle the addition of a displayed gem to the tabletop.
     * This method performs setup / initialization of tabletop data structures.
     * @param displayedGem the displayed gem being added.
     */
    private void handleDisplayedGemAdded(DisplayedGem displayedGem) {

        // Add the Gem to the display map.
        Gem gemModel = displayedGem.getGem();
        gemDisplayMap.put(gemModel, displayedGem);

        // Get the TableTop to repaint the area where we added a Gem
        if (displayedGem != targetDisplayedCollector) {
            getTableTopPanel().repaint(getSelectedBounds(displayedGem));
        }

        // Clear the GemCutter's adding gem if it had any.
        gemCutter.setAddingDisplayedGem(null);

        // add the event listener as a listener on various gem events
        displayedGem.addLocationChangeListener(gemEventListener);
        displayedGem.addSizeChangeListener(gemEventListener);
        displayedGem.addLocationChangeListener(getTableTopPanel().getGemPainter());
        displayedGem.addSizeChangeListener(getTableTopPanel().getGemPainter());

        gemModel.addBurnListener(gemEventListener);
        gemModel.addInputChangeListener(gemEventListener);
        gemModel.addNameChangeListener(gemEventListener);
        gemModel.addStateChangeListener(gemEventListener);
        gemModel.addTypeChangeListener(gemEventListener);

        gemModel.addStateChangeListener(gemCutter.getTargetRunnableListener());

        // Special handling for Value Gems (need to display its valueEntryPanel)
        if (gemModel instanceof ValueGem) {
            tableTopPanel.handleValueGemAdded((ValueGem)gemModel);

        } else if (gemModel instanceof CodeGem) {

            CodeGem codeGem = (CodeGem)gemModel;
            codeGem.addDefinitionChangeListener(gemEventListener);

            // Create a code gem editor if there isn't one already.
            CodeGemEditor codeGemEditor = codeGemEditorMap.get(codeGem);
            if (codeGemEditor == null) {

                codeGemEditor = getNewCodeGemEditor(codeGem);
                codeGemEditorMap.put(codeGem, codeGemEditor);
                unplacedCodeGemEditorMap.put(codeGem, codeGemEditor);
            }
        }
    }

    /**
     * Handle the situation where the value gem's value editor has had its value committed.
     *   Other value gems will be type switched as necessary, and updates will be posted to the undo manager.
     *   This method does not make any assumptions about whether the value gem in question has already had its value updated.
     *   No action will be taken if the old and new values are the same value.
     * @param committedValueGem the value gem in question
     * @param oldValue the value before the change.
     * @param newValue the value after the change.
     */
    void handleValueGemCommitted(ValueGem committedValueGem, ValueNode oldValue, ValueNode newValue) {

        // check for nothing to do.
        if (oldValue.sameValue(newValue)) {
            return;
        }

        undoableEditSupport.beginUpdate();

        // Get the new types of changed value gems.
        Map<ValueGem, ValueNode> valueGemToNewValueMap = gemGraph.getValueGemSwitchValues(committedValueGem, oldValue, newValue, getTypeCheckInfo());

        // Update the valuegems with their new types and post any value edits to the undo manager.
        boolean valueGemChangesPosted = false;
        for (final Map.Entry<ValueGem, ValueNode> mapEntry : valueGemToNewValueMap.entrySet()) {
            ValueGem valueGemChanged = mapEntry.getKey();
            ValueNode valueGemChangedValue = mapEntry.getValue();

            ValueNode preChangeValue = (valueGemChanged == committedValueGem) ? oldValue : valueGemChanged.getValueNode();

            if (!preChangeValue.sameValue(valueGemChangedValue)) {
                valueGemChanged.changeValue(valueGemChangedValue);
                undoableEditSupport.postEdit(new UndoableValueChangeEdit(valueGemChanged, preChangeValue));
                valueGemChangesPosted = true;
            }
        }

        // Post edits if any.
        if (valueGemChangesPosted) {
            // Update the tabletop for the new gem graph state.
            updateForGemGraph();

            undoableEditSupport.endUpdate();

        } else {
            undoableEditSupport.endUpdateNoPost();
        }
    }

    /**
     * Update the code gems and their editors with respect to any text or type changes.
     */
    void updateCodeGemEditors() {
        for (final CodeGemEditor codeGemEditor : codeGemEditorMap.values()) { 
            codeGemEditor.doSyntaxSmarts();
            updateForConnectivity(codeGemEditor);
        }
    }

    /**
     * Reset the tabletop to be completely clean.
     */
    private void blankTableTop() {

        // Remove all gems.  This also does auxiliary clean up such as hiding code panels.
        Set<Gem> gemSet = gemGraph.getGems();
        doDeleteGemsUserAction(gemSet);

        // Update the tabletop for the new gem graph state.
        updateForGemGraph();

        // reinitialize some members
        cachedArgValueMap.clear();
        getTableTopPanel().resetState();

        // reset the metadata for the table top target collector
        FunctionMetadata oldMetadata = getTargetCollector().getDesignMetadata();
        getTargetCollector().clearDesignMetadata();
        // post an undoable edit for this reseting of the metadata
        getUndoableEditSupport().postEdit(new UndoableChangeCollectorDesignMetadataEdit(this, getTargetCollector(), oldMetadata));
    }

    /**
     * Change a gem's location.
     * @param displayedGem the gem to move
     * @param newLocation the new location of the gem.
     */
    void changeGemLocation(DisplayedGem displayedGem, Point newLocation) {
        // Change the gem location
        displayedGem.setLocation(newLocation);
    }

    /**
     * Make a connection.
     * @param srcPart the source part
     * @param destPart the destination part
     */
    void connect(PartOutput srcPart, PartInput destPart) {
        // Defer to the other connect().
        connect(new Connection(srcPart, destPart));
    }

    /**
     * Make a connection
     * @param connection the connection to make.
     */
    private void connect(Connection connection) {

        // Make the connection
        gemGraph.connectGems(connection);

        DisplayedConnection displayedConnection = handleConnectionAdded(connection);

        // repaint parts
        getTableTopPanel().repaint(displayedConnection.getBounds());
        getTableTopPanel().repaint(displayedConnection.getSource().getBounds());
        getTableTopPanel().repaint(displayedConnection.getDestination().getBounds());
    }

    /**
     * Handle the addition of a connection to the tabletop.
     * This method performs setup / initialization of tabletop data structures.
     * @param connection the connection being added.
     * @return DisplayedConnection the displayed connection which was created.
     */
    private DisplayedConnection handleConnectionAdded(Connection connection) {
        // Create a new displayed connection.
        DisplayedPartOutput dSource = (DisplayedPartOutput)getDisplayedPartConnectable(connection.getSource());
        DisplayedPartInput dDest = (DisplayedPartInput)getDisplayedPartConnectable(connection.getDestination());
        DisplayedConnection displayedConnection = new DisplayedConnection(dSource, dDest);

        // bind connections
        dSource.bindDisplayedConnection(displayedConnection);
        dDest.bindDisplayedConnection(displayedConnection);

        // Add to the map.
        connectionDisplayMap.put(displayedConnection.getConnection(), displayedConnection);

        // Add the connection listener
        addConnectionStateListener(gemEventListener);

        return displayedConnection;
    }

    /**
     * Update the given code gem editor's state according to the validity of its connections.
     * @param codeGemEditor
     */
    private void updateForConnectivity(CodeGemEditor codeGemEditor) {
        codeGemEditor.updateForConnectivity(getTypeCheckInfo(), gemCutter.getValueEditorManager());
    }

    /**
     * Get the map from value gem to its current value node.
     * @return Map from value gem to its value node.
     */
    private Map<ValueGem, ValueNode> getValueGemToValueMap() {
        Map<ValueGem, ValueNode> valueGemToValueMap = new HashMap<ValueGem, ValueNode>();

        for (final Gem gem : gemGraph.getGems()) {
            if (gem instanceof ValueGem) {
                ValueGem valueGem = (ValueGem)gem;
                valueGemToValueMap.put(valueGem, valueGem.getValueNode());
            }
        }
        return valueGemToValueMap;
    }

    /**
     * Given a map from value gem to its former value node, post value change edits to the undo manager for any values that changed.
     * @param valueGemToOldValueMap Map from value gem to its former value node.
     * @return boolean whether any value gem changes were posted.
     */
    private boolean postValueGemChanges(Map<ValueGem, ValueNode> valueGemToOldValueMap) {
        boolean valueGemChangesPosted = false;

        // Post a ValueChangeEdit for each value gem that changed.
        for (final Map.Entry<ValueGem, ValueNode> mapEntry : valueGemToOldValueMap.entrySet()) {
            ValueGem valueGem = mapEntry.getKey();
            ValueNode oldValue = mapEntry.getValue();

            if (!oldValue.sameValue(valueGem.getValueNode())) {
                undoableEditSupport.postEdit(new UndoableValueChangeEdit(valueGem, oldValue));
                valueGemChangesPosted = true;
            }
        }

        return valueGemChangesPosted;
    }

    /**
     * Perform the necessary steps resulting from a user-initiated disconnect action.
     * @param connection the connection to disconnect
     */
    void disconnect(Connection connection) {
        // do the disconnection
        gemGraph.disconnectGems(connection);

        // remove from map
        DisplayedConnection displayedConnection = connectionDisplayMap.remove(connection);

        // disconnect the displayed parts
        displayedConnection.getSource().bindDisplayedConnection(null);
        displayedConnection.getDestination().bindDisplayedConnection(null);

        // Repaint the disconnected bounds.
        getTableTopPanel().repaint(displayedConnection.getBounds());
        getTableTopPanel().repaint(displayedConnection.getSource().getBounds());
        getTableTopPanel().repaint(displayedConnection.getDestination().getBounds());
    }
    
    
    /**
     * Perform the steps necessary to result in a split connection action with the specified collector
     * and emitter gems.  
     * @param conn the connection to be split
     * @param collGem new collector gem to connect to the original connection's src part
     * @param emitGem new emitter gem to connect to the original connection's dest part
     */
    void splitConnectionWith(Connection conn, DisplayedGem collGem, DisplayedGem emitGem){
        
        DisplayedConnection displayConn = connectionDisplayMap.get(conn);
     
        // Disconnect without updating arguments in gemGraph
        gemGraph.setArgumentUpdatingDisabled(true);
        disconnect(conn);  
        
        // Add the collector and emitter gems to tabletop
        addGem(collGem, displayConn.getSourceConnectionPoint());
        addGem(emitGem, displayConn.getTargetConnectionPoint());
        tidyNewGemPair(displayConn, collGem, emitGem);
       
        connect(conn.getSource(), collGem.getGem().getInputPart(0));
        connect(emitGem.getGem().getOutputPart(), conn.getDestination());
        
        gemGraph.setArgumentUpdatingDisabled(false);
        updateForGemGraph();
        resizeForGems();
        
    }
    
    /**
     * Place the new collector/emitter gem pair as close to the 2 existing gems as possible or 
     * further apart if the gems overlap or look out of place(ie. the collector is to the right of the emitter 
     * within the lengths of the gems). Existing gems are anchored.
     * Note: This method cannot guarantee the new gems will not overlap with other gems on the table top.
     * 
     * @param displayConn the original connection to be split
     * @param newCollectorGem new collector gem
     * @param newEmitterGem new emitter gem
     */    
    private void tidyNewGemPair(DisplayedConnection displayConn, DisplayedGem newCollectorGem, DisplayedGem newEmitterGem) {

        // Location of the connection points
        final Point srcPos = displayConn.getSourceConnectionPoint();
        final Point destPos = displayConn.getTargetConnectionPoint();

        final Rectangle collectorRect = newCollectorGem.getBounds();
        final Rectangle emitterRect = newEmitterGem.getBounds();

        // Initially try to place the new gems close to the existing gems. These positions maybe change below.
        Point collGemPosition = new Point(srcPos.x + GAP_BETWEEN_GEMS, srcPos.y - collectorRect.height / 2);
        Point emitGemPosition = new Point(destPos.x - GAP_BETWEEN_GEMS * 6, destPos.y - emitterRect.height / 2);
        collectorRect.setLocation(collGemPosition);
        emitterRect.setLocation(emitGemPosition);

        // (x,y) of the rectangle's top left corner (top left corner of the table top is (0,0))
        int colRectX = collectorRect.x;
        int colRectY = collectorRect.y;
        int emitRectX = emitterRect.x;
        int emitRectY = emitterRect.y;

        int yDiff = Math.abs(colRectY - emitRectY);

        // If the new gems overlap or look out of place (collector gem to the right of the emitter gem when there is overlapping
        // of the gems' lengths), rearrange them
        if (collectorRect.intersects(emitterRect) || (colRectX > emitRectX && yDiff < collectorRect.height)) {

            // Stack the gems vertically
            int avgY = (colRectY + emitRectY) / 2;
            int avgX = (colRectX + emitRectX) / 2;
            colRectX = avgX;
            emitRectX = avgX;

            // See which new gem should be placed on top
            if (colRectY < emitRectY) {
                colRectY = avgY - GAP_BETWEEN_GEMS / 2 - collectorRect.height;
                emitRectY = avgY + GAP_BETWEEN_GEMS / 2;

            } else {
                emitRectY = avgY - GAP_BETWEEN_GEMS / 2 - emitterRect.height;
                colRectY = avgY + GAP_BETWEEN_GEMS / 2;
            }

            //--------- If limited horizontal space, try moving the pair further away from the source and destination gems

            // Moving the collector gem towards the right and above or below the destination gem
            int connSourceX = displayConn.getSourceConnectionPoint().x;
            if (avgX < connSourceX) {
                colRectX = connSourceX + GAP_BETWEEN_GEMS;
                Rectangle destGem = displayConn.getDestination().getDisplayedGem().getBounds();
                
                // Move the collector gem above the destination gem if it is already higher, else move it below 
                if (colRectY < emitRectY) {
                    colRectY = destGem.y - collectorRect.height - GAP_BETWEEN_GEMS;

                } else {
                    colRectY = destGem.y + destGem.height + GAP_BETWEEN_GEMS;
                }
            }

            // Moving the emitter gem towards the left and try to move above or below the source gem
            if (avgX + emitterRect.width > displayConn.getTargetConnectionPoint().x) {
                emitRectX = displayConn.getTargetConnectionPoint().x - emitterRect.width - GAP_BETWEEN_GEMS;
                Rectangle srcGem = displayConn.getSource().getDisplayedGem().getBounds();

                // Move the emitter gem below the source gem if it is already lower, else move it above
                if (colRectY < emitRectY) {
                    emitRectY = srcGem.y + srcGem.height + GAP_BETWEEN_GEMS;

                } else {
                    emitRectY = srcGem.y - emitterRect.height - GAP_BETWEEN_GEMS;
                }
            }

            // Set the new location for the pair
            collGemPosition = new Point(colRectX, colRectY);
            emitGemPosition = new Point(emitRectX, emitRectY);
        }
        changeGemLocation(newEmitterGem, emitGemPosition);
        changeGemLocation(newCollectorGem, collGemPosition);
    }

    
    /**
     * Remove a given gem from the tabletop.
     * @param gemToDelete the gem to remove
     */
    void deleteGem(Gem gemToDelete) {

        DisplayedGem displayedGem = gemDisplayMap.get(gemToDelete);

        // Clear the focused displayed gem if necessary
        if (displayedGem == getFocusedDisplayedGem()) {
            setFocusedDisplayedGem(null);
        }

        // Hide any code gem editor
        if (gemToDelete instanceof CodeGem) {
            showCodeGemEditor(((CodeGem)gemToDelete), false);
            ((CodeGem)gemToDelete).removeDefinitionChangeListener(gemEventListener);
        }

        // Notify the tableTop panel if a value gem was deleted.
        if (gemToDelete instanceof ValueGem) {
            tableTopPanel.handleValueGemRemoved((ValueGem)gemToDelete);
        } 

        // Now actually delete the gems.
        gemGraph.removeGem(gemToDelete);
        gemDisplayMap.remove(gemToDelete);
        selectedDisplayedGems.remove(gemToDelete);
        runningDisplayedGems.remove(gemToDelete);

        // Update any menu buttons which depend on displayed gems
        checkCopySpecialMenu();

        // Repaint the gem area without the gem
        getTableTopPanel().repaint(getSelectedBounds(displayedGem));

        // remove the event listener as a listener on various gem events
        displayedGem.removeLocationChangeListener(gemEventListener);
        displayedGem.removeSizeChangeListener(gemEventListener);
        displayedGem.removeLocationChangeListener(getTableTopPanel().getGemPainter());
        displayedGem.removeSizeChangeListener(getTableTopPanel().getGemPainter());

        gemToDelete.removeBurnListener(gemEventListener);
        gemToDelete.removeInputChangeListener(gemEventListener);
        gemToDelete.removeNameChangeListener(gemEventListener);
        gemToDelete.removeStateChangeListener(gemEventListener);
        gemToDelete.removeTypeChangeListener(gemEventListener);

        gemToDelete.removeStateChangeListener(gemCutter.getTargetRunnableListener());        
    }

    /**
     * Moves all gems x distance and y distance.
     * Note: Also invalidates all previous connections (since they're now off).
     * @param x int
     * @param y int
     */
    void moveAllGems(int x, int y) {

        // Consider each Gem
        for (final DisplayedGem displayedGem : getDisplayedGems()) {
            Point oldPT = displayedGem.getLocation();
            Point newPT = new Point(oldPT.x + x, oldPT.y + y);
            changeGemLocation(displayedGem, newPT);

        }

        // The connections routes are invalid now.
        for (final DisplayedConnection displayedConnection : connectionDisplayMap.values()) {
            displayedConnection.purgeRoute();
        }

        // move all the value entry panels (temporarily disabled - todoKW: fix VEP painting bug) 
        // moveVEPs(x, y);
        Point newOrigin = new Point(originalOrigin.x + x, originalOrigin.y + y);
        originalOrigin = newOrigin;
    }

    /**
     * Find out whether or not the given gem is running.
     * @param displayedGem 
     * @return whether or not the given gem is running.
     */
    public final boolean isRunning(DisplayedGem displayedGem){
        return runningDisplayedGems.contains(displayedGem);
    }

    /**
     * Set the running state of a given gem.
     * @param displayedGem the gem in question.
     * @param newState the new running state of the gem.
     */
    void setRunning(DisplayedGem displayedGem, boolean newState){
        boolean running = isRunning(displayedGem);
        if (running != newState) {

            if (newState) {
                runningDisplayedGems.add(displayedGem);
            } else {
                runningDisplayedGems.remove(displayedGem);
            }

            if (gemStateListener != null) {
                gemStateListener.runStateChanged(
                        new DisplayedGemStateEvent(displayedGem, displayedGem.getBounds(), DisplayedGemStateEvent.EventType.RUNNING));
            }
        }
    }

    /**
     * Set the bad state of a connection
     * @param newState the new bad state of the Gem
     */ 
    final void setBadDisplayedConnection(DisplayedConnection displayedConnection, boolean newState) {
        final boolean selected = isBadDisplayedConnection(displayedConnection);
        if (selected != newState) {           

            if (newState) {
                badDisplayedConnections.add(displayedConnection);
            } else {
                badDisplayedConnections.remove(displayedConnection);
            }

            if (connectionStateListener != null) {
                connectionStateListener.badStateChanged(
                        new DisplayedConnectionStateEvent(displayedConnection, displayedConnection.getBounds(), DisplayedConnectionStateEvent.EventType.BAD));
            }
        }
    }

    /**
     * Return whether the given connection is currently considered bad.
     * @param displayedConnection the connection in question
     * @return whether the given connection is currently considered bad.
     */
    public final boolean isBadDisplayedConnection(DisplayedConnection displayedConnection) {
        return badDisplayedConnections.contains(displayedConnection);
    }

    /**
     * Set the selection state of this Gem
     * @param newState the new selection state of the Gem
     */ 
    private final void setSelectionState(DisplayedGem displayedGem, boolean newState) {
        final boolean selected = isSelected(displayedGem);
        if (selected != newState) {           

            if (newState) {
                selectedDisplayedGems.add(displayedGem);
            } else {
                selectedDisplayedGems.remove(displayedGem);
            }

            if (gemStateListener != null) {
                gemStateListener.selectionStateChanged(
                        new DisplayedGemStateEvent(displayedGem, displayedGem.getBounds(), DisplayedGemStateEvent.EventType.SELECTION));
            }
        }
    }

    /**
     * Toggle the selection state of this Gem
     * @return the resulting state of the Gem (after toggling)
     */
    final boolean toggleSelected(DisplayedGem displayedGem) {
        boolean selected = !isSelected(displayedGem);
        setSelectionState(displayedGem, selected);
        checkSelectionButtons();
        return selected;
    }

    /**
     * Return whether the given gem is currently selected.
     * @param displayedGem the gem in question
     * @return whether the given gem is currently selected.
     */
    public final boolean isSelected(DisplayedGem displayedGem) {
        return selectedDisplayedGems.contains(displayedGem);
    }

    /**
     * Select the given Gem in the UI, or deselect all Gems.
     * @param gem the Gem to select (or null to deselect all)
     * @param singleton whether to deselect all other Gems (true) or select this Gem in addition to others
     */
    void selectGem(Gem gem, boolean singleton) {
        selectDisplayedGem(getDisplayedGem(gem), singleton);
    }

    /**
     * Select the given Gem in the UI, or deselect all Gems.
     * @param displayedGem the displayed Gem to select (or null to deselect all)
     * @param singleton whether to deselect all other Gems (true) or select this Gem in addition to others
     */
    void selectDisplayedGem(DisplayedGem displayedGem, boolean singleton) {

        // If gem is null, or singleton is set, we deselect everything
        if (singleton || (displayedGem == null)) {
            // Deselect each Gem in the graph
            for (final DisplayedGem dGem : getDisplayedGems()){
                if (isSelected(dGem)) {
                    setSelectionState(dGem, false);
                }   
            }
        }

        // Now select this Gem
        if (displayedGem != null) {
            setSelectionState(displayedGem, true);
        }

        checkSelectionButtons();
    }

    /**
     * Goes thru the list of gems, and if a gem is not selected, then selects it.
     * Note: The target gem will only be considered for selection if it's undocked.
     */ 
    void selectAllGems() {

        // We want to give focus to the Gem nearest the top left corner of the TableTop
        // so keep track of which is closest as we select each one.
        double dist = -1;
        DisplayedGem gemToFocusOn = null;

        for (final DisplayedGem thisGem : getDisplayedGems()){
            setSelectionState(thisGem, true);

            // Do the distance checking here.
            double thisDist = thisGem.getCenterPoint().distance(0, 0);
            if (dist < 0 || thisDist < dist) {
                dist = thisDist;
                gemToFocusOn = thisGem;
            }
        }

        checkSelectionButtons();

        // Actually focus on a Gem here
        setFocusedDisplayedGem(gemToFocusOn);
    }

    /**
     * Select gems in the UI according to the select area and select mode.
     * @param hitRect the select area
     * @param selectMode SelectMode the select mode
     */
    void selectGems(Rectangle2D hitRect, TableTopPanel.SelectMode selectMode) {

        // If the select mode is replace, first deselect all gems
        if (selectMode == TableTopPanel.SelectMode.REPLACE_SELECT) {
            selectDisplayedGem(null, true);
        }

        // For each DisplayedGem, check for intersection with the hitRect and
        // change its selection state accordingly
        for (final DisplayedGem displayedGem : getDisplayedGems()) {

            // The gem is hit if its center point lies within the hit rectangle
            if (hitRect.contains(displayedGem.getCenterPoint())) {

                if (selectMode == TableTopPanel.SelectMode.TOGGLE) {
                    // Toggle state
                    toggleSelected(displayedGem);

                } else {
                    // Select
                    setSelectionState(displayedGem, true);
                }               
            }   
        }

        checkSelectionButtons();
    }

    /**
     * Ensures that buttons dependent on gem selection are in their proper
     * state (eg: copy/cut/delete are enabled when gems are selected)
     */
    void checkSelectionButtons() {
        checkCutCopyDeleteValid();
        checkCopySpecialMenu();
    }

    /** 
     * Checks whether something has been selected and enables/disables
     * the copy, cut and delete buttons
     */
    void checkCutCopyDeleteValid() {
        boolean itemsSelected = !selectedDisplayedGems.isEmpty();
        gemCutter.getCutAction().setEnabled(itemsSelected);
        gemCutter.getCopyAction().setEnabled(itemsSelected);
        gemCutter.getDeleteAction().setEnabled(itemsSelected);
    }

    /**
     * Checks whether something has been selected, and renames and enables the 
     * "Copy Special" submenu items accordingly.
     */
    void checkCopySpecialMenu() {
        // Determine gems affected by the "Copy As" menu (ie: selected gems,
        // or all tabletop displayed gems)

        Set<DisplayedGem> copySpecialGems;
        if (!selectedDisplayedGems.isEmpty()) {

            // The menu will look at selected gems
            copySpecialGems = selectedDisplayedGems;
            gemCutter.getCopySpecialImageAction().putValue(Action.NAME, GemCutter.getResourceString("CopySpecialSelectionImage"));

        } else {
            // The menu will look at all tabletop gems 
            copySpecialGems = getDisplayedGems();
            gemCutter.getCopySpecialImageAction().putValue(Action.NAME, GemCutter.getResourceString("CopySpecialTableTopImage"));
        }

        // Now determine if any code gems are included in the set of gems, and enable/disable
        // the Copy As Text button

        boolean includesCodeGems = false;
        for (final DisplayedGem displayedGem : copySpecialGems) {
            if (displayedGem.getGem() instanceof CodeGem) {
                includesCodeGems = true;
                break;
            }
        }
        gemCutter.getCopySpecialTextAction().setEnabled(includesCodeGems);
    }

    /**
     * Completes the actions necessary to rename a codeGem
     * @param codeGem
     * @param newName
     */
    void renameCodeGem(CodeGem codeGem, String newName) {

        codeGem.setName(newName);

        // and update the title in the editor
        CodeGemEditor codeGemEditor = getCodeGemEditor(codeGem);
        String newEditorTitle = GemCutterMessages.getString("CGE_EditorTitle", newName);
        codeGemEditor.setName(newEditorTitle);
        codeGemEditor.setTitle(newEditorTitle);

        // redraw the gem
        getTableTopPanel().repaint();
    }

    /**
     * Ensure the TableTop is sized appropriately for the gems which appear on it.
     * For now this only ensures that it's big enough - no cropping is performed.
     */
    void resizeForGems() {

        // make sure it's not the (docked) target that resizes the TableTop
        checkTargetDockLocation();

        // get the GemGraph bounds
        Rectangle gemGraphBounds = getGemGraphBounds();
        int xMaxGemGraph = gemGraphBounds.x + gemGraphBounds.width;
        int yMaxGemGraph = gemGraphBounds.y + gemGraphBounds.height;

        // get the current tabletop bounds
        java.awt.Dimension currentBounds = getTableTopPanel().getSize();
        int xMaxCurrent = currentBounds.width;
        int yMaxCurrent = currentBounds.height;

        // beyond bottom or right edge?
        if (xMaxGemGraph > xMaxCurrent || yMaxGemGraph > yMaxCurrent) {
            int xMaxNew = Math.max(xMaxGemGraph, xMaxCurrent);
            int yMaxNew = Math.max(yMaxGemGraph, yMaxCurrent);

            java.awt.Dimension newSize = new java.awt.Dimension(xMaxNew, yMaxNew);
            getTableTopPanel().setSize(newSize);
            getTableTopPanel().setPreferredSize(newSize);      // triggers revalidation
        }

        // beyond top or left edge?
        if (gemGraphBounds.x < 0 || gemGraphBounds.y < 0) {

            Rectangle visibleRect = getTableTopPanel().getVisibleRect();

            int moveDistanceX = Math.max(0, -(gemGraphBounds.x));
            int moveDistanceY = Math.max(0, -(gemGraphBounds.y));
            moveAllGems(moveDistanceX, moveDistanceY);

            java.awt.Dimension newSize = new java.awt.Dimension(getTableTopPanel().getWidth() + moveDistanceX, getTableTopPanel().getHeight() + moveDistanceY);
            getTableTopPanel().setSize(newSize);
            getTableTopPanel().setPreferredSize(newSize);

            // scroll back the old bottom right corner so we don't "jump" the viewport
            Rectangle newVisibleRect = 
                new Rectangle(visibleRect.width + moveDistanceX, visibleRect.height + moveDistanceY, 0, 0);
            getTableTopPanel().scrollRectToVisible(newVisibleRect);
        }

    }

    /*
     * Methods for user actions                          ********************************************
     */

    /**
     * Completes the work necessary to do a user copy action.
     */
    void doCopyUserAction() {

        /// Take the selected gems and make a copy on the clipboard
        DisplayedGem selectedDisplayedGems[] = getSelectedDisplayedGems();
        Clipboard clipboard = gemCutter.getClipboard();
        DisplayedGemSelection displayedGemSelection = new DisplayedGemSelection(selectedDisplayedGems, gemCutter);
        clipboard.setContents(displayedGemSelection, displayedGemSelection);

        // Now we know the paste action is possible
        gemCutter.getPasteAction().setEnabled(true);
    }

    /**
     * Takes a snapshot of the whole tabletop and stores this as an image 
     * on the system clipboard. 
     */
    void doCopySpecialImageAction() {

        TableTopPanel tableTopPanel = TableTop.this.getTableTopPanel();

        // Determine the bounds of the snapshot area 
        Rectangle bounds;
        if (getSelectedGems().length > 0) {

            // We have a selection, so will image this area plus a border
            bounds = getSelectedGemsBounds();
            int dx = Math.max(0, bounds.x - SNAPSHOT_BOUNDARY_PAD) - bounds.x;
            int dy = Math.max(0, bounds.y - SNAPSHOT_BOUNDARY_PAD) - bounds.y;
            bounds.x += dx;      // move origin
            bounds.width -= dx;  // and widen size
            bounds.y += dy;
            bounds.height -= dy;
            bounds.width  = Math.min(bounds.x + bounds.width  + SNAPSHOT_BOUNDARY_PAD, tableTopPanel.getWidth()) - bounds.x;
            bounds.height = Math.min(bounds.y + bounds.height + SNAPSHOT_BOUNDARY_PAD, tableTopPanel.getHeight()) - bounds.y;

        } else {
            // No selection, so image the whole table top
            bounds = new Rectangle(0, 0, tableTopPanel.getWidth(), tableTopPanel.getHeight());
        }

        // Take snapshot of the required area into a new image
        BufferedImage newImage = new BufferedImage(
                bounds.width  + 1,
                bounds.height + 1,
                BufferedImage.TYPE_INT_ARGB_PRE
        );
        Graphics2D g2d = newImage.createGraphics();
        g2d.setClip(new Rectangle(0, 0, bounds.width, bounds.height));
        g2d.translate(-bounds.x + 1, -bounds.y + 1);
        tableTopPanel.paintComponent(g2d);

        // Put a border around the image
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.translate(bounds.x - 1, bounds.y - 1);
        g2d.drawRect(0, 0, bounds.width, bounds.height);
        g2d.dispose();

        // Send this to the clipboard
        Transferable imageTransferable = new ImageTransferable(newImage);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(imageTransferable, null);

        // For platforms which have a selection clipboard (such as X11), store the image there also
        clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
        if (clipboard != null) {
            clipboard.setContents(imageTransferable, null);
        }
    }

    /**
     * Copies all code from tabletop code gems, as HTML text
     * on the system clipboard.
     */
    void doCopySpecialTextAction() {

        // Select gems and order by name

        List<DisplayedGem> gemList = new ArrayList<DisplayedGem>();
        gemList.addAll(Arrays.asList(getSelectedDisplayedGems()));
        if (gemList.size() == 0) {
            // If no gems are selected, will look through all gems
            gemList.addAll(getDisplayedGems());
        }
        Collections.sort(gemList, new DisplayedGemComparator());

        // Build html string with contents of selected code gems

        boolean cgFound = false;
        String htmlText = "<html><body>";
        for (final DisplayedGem displayedGem : gemList) {
            if (displayedGem.getGem() instanceof CodeGem) {
                CodeGem gem = (CodeGem)displayedGem.getGem();
                htmlText += "\n<p><b>" + HtmlHelper.htmlEncode(gem.getUnqualifiedName()) + ":</b><br>";
                htmlText += HtmlHelper.htmlEncode(gem.getVisibleCode()).replaceAll("\n", "<br>").replaceAll(" ","&nbsp;");
                htmlText += "</p><br><br>";
                cgFound = true;
            }
        }
        htmlText += "</body></html>";

        // Send text to clipboard

        if (!cgFound) {
            return;
        }

        Transferable htmlTransferable = new HtmlTransferable(htmlText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(htmlTransferable, null);

        // For platforms which have a selection clipboard (such as X11), store the text there also
        clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
        if (clipboard != null) {
            clipboard.setContents(htmlTransferable, null);
        }
    }

    /**
     * Copies the source for the target gem to the clipboard.
     */
    void doCopySpecialTargetSourceAction() {

        // Get target source text

        Transferable stringTransferable = 
            new StringSelection(targetDisplayedCollector.getTarget().getTargetDef(null, gemCutter.getPerspective().getWorkingModuleTypeInfo()).toString().replaceAll("\r\n","\n"));

        // Send text to clipboard

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringTransferable, null);

        // For platforms which have a selection clipboard (such as X11), store the text there also
        clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
        if (clipboard != null) {
            clipboard.setContents(stringTransferable, null);
        }
    }

    /** 
     * Completes the work necessary to do a user cut action.
     */
    void doCutUserAction() {
        // Group as one action
        getUndoableEditSupport().beginUpdate();
        getUndoableEditSupport().setEditName(GemCutter.getResourceString("UndoText_Cut"));
        DisplayedGem[] selectedDisplayedGems = getSelectedDisplayedGems();

        // To cut, we simply copy and then delete, and group it all as one 'undo gesture'
        Clipboard clipboard = gemCutter.getClipboard();
        DisplayedGemSelection displayedGemSelection = new DisplayedGemSelection(selectedDisplayedGems, gemCutter);
        clipboard.setContents(displayedGemSelection, displayedGemSelection);
        handleDeleteSelectedGemsGesture();
        getUndoableEditSupport().endUpdate();
        gemCutter.getPasteAction().setEnabled(true);
    }

    /**
     * Completes the paste user action
     * @param displayedGemSelection
     */
    void doPasteUserAction(Transferable displayedGemSelection) {
        doPasteUserAction(displayedGemSelection, getTableTopPanel().getPasteLocation());
    }

    /**
     * Completes the paste action. Takes the transferable specified and adds it to the tabletop
     * at the location specified in the parameter
     * @param displayedGemSelection
     * @param location
     */
    void doPasteUserAction(Transferable displayedGemSelection, Point location) {
        Pair<DisplayedGem[], Set<Connection>> displayedGemsConnectionPair = null;
        DisplayedGem[] displayedGems = null;
        try {
            // Grab the data from the transferable
            displayedGemsConnectionPair = UnsafeCast.<Pair<DisplayedGem[], Set<Connection>>>unsafeCast(displayedGemSelection.getTransferData(DisplayedGemSelection.CONNECTION_PAIR_FLAVOR));                            
            displayedGems = displayedGemsConnectionPair.fst();

            if (location == null) {
                // If the location is null, we find the first available spot beneath the originally cut/copied gems
                Point preferredLocation = (Point)displayedGemSelection.getTransferData(DisplayedGemSelection.ORIGINAL_LOCATION_FLAVOR);                            
                location = findSpaceUnderneathFor(preferredLocation, displayedGems);
            }

        } catch (UnsupportedFlavorException e) {
            // Flavour not supported.
            return;

        } catch (IOException e) {
            // Data no longer available in the requested flavour.
            return;
        }                            
        Set<Connection> connectionSet = new LinkedHashSet<Connection>(displayedGemsConnectionPair.snd());

        // Get the pre-state of argument target-related changes.
        StateEdit collectorArgumentStateEdit = new StateEdit(collectorArgumentStateEditable);

        // Get the categories of gem to add to the gem graph.
        Set<Gem> rootsToAdd = new HashSet<Gem>();
        Set<DisplayedGem> displayedCollectorsToAdd = new HashSet<DisplayedGem>();
        Set<DisplayedGem> displayedNonCollectorsToAdd = new HashSet<DisplayedGem>();

        for (final DisplayedGem displayedGem : displayedGems) {
            Gem gem = displayedGem.getGem();
            rootsToAdd.add(gem.getRootGem());

            if (gem instanceof CollectorGem) {
                displayedCollectorsToAdd.add(displayedGem);

                // Also retarget untargeted collectors to the target gem.
                if (((CollectorGem)gem).getTargetCollectorGem() == null) {
                    ((CollectorGem)gem).setTargetCollector(getTargetCollector());
                }
            } else {
                displayedNonCollectorsToAdd.add(displayedGem);
            }
        }

        // Handle gems which can't be pasted.
        // Note: if this section is changed, canPasteToGemGraph() might also need to be updated.
        for (Iterator<DisplayedGem> it = displayedNonCollectorsToAdd.iterator(); it.hasNext(); ) {
            DisplayedGem displayedNonCollectorGem = it.next();
            
            boolean removeGem = false;

            Gem gem = displayedNonCollectorGem.getGem();
            if (gem instanceof ReflectorGem) {
                CollectorGem reflectorCollector = ((ReflectorGem)displayedNonCollectorGem.getGem()).getCollector();

                // Don't paste this reflector gem if its collector will not be on the tabletop after the paste..
                if (!(rootsToAdd.contains(reflectorCollector) || gemGraph.hasGem(reflectorCollector))) {
                    removeGem = true;
                }
            } else if (gem instanceof FunctionalAgentGem) {
                // Can't paste if the entity has changed (as a result of recompilation).
                GemEntity pastingGemEntity = ((FunctionalAgentGem)gem).getGemEntity();
                GemEntity expectedGemEntity = gemCutter.getPerspective().getVisibleGemEntity(pastingGemEntity.getName());

                if (pastingGemEntity != expectedGemEntity) {
                    removeGem = true;
                }
            }

            if (removeGem) {
                // Remove the gem.
                it.remove();
                rootsToAdd.remove(gem);                // ... remove from rootsToAdd in case the gem is a root.

                // Disconnect and remove connections.
                Connection outputConnection = gem.getOutputPart().getConnection();
                if (outputConnection != null) {
                    outputConnection.getSource().bindConnection(null);
                    outputConnection.getDestination().bindConnection(null);

                    connectionSet.remove(outputConnection);
                }
                for (int i = 0, nInputParts = gem.getNInputs(); i < nInputParts; i++) {
                    PartInput inputPart = gem.getInputPart(i);
                    Connection inputConnection = inputPart.getConnection();
                    if (inputConnection != null) {
                        inputPart.bindConnection(null);
                        inputConnection.getSource().bindConnection(null);

                        connectionSet.remove(inputConnection);
                    }
                }
            }
        }

        // After pruning the gems to add, we might not have anything left to do.
        if (rootsToAdd.isEmpty()) {
            return;
        }

        // Add the gems to the GemGraph
        gemGraph.addSubtrees(rootsToAdd);

        // Now notify the tabletop of the gem and connection additions.
        // We should post edits for collector additions first to avoid screwing up undo.
        List<DisplayedGem> orderedDisplayedGems = new ArrayList<DisplayedGem>(displayedCollectorsToAdd);
        orderedDisplayedGems.addAll(displayedNonCollectorsToAdd);

        for (final DisplayedGem displayedGem : orderedDisplayedGems) {
            // Update the location.
            Point oldLocation = displayedGem.getLocation();
            Point newLocation = new Point(oldLocation.x + location.x, oldLocation.y + location.y);
            displayedGem.setLocation(newLocation);

            // Notify the tabletop.
            handleDisplayedGemAdded(displayedGem);
        }

        for (final Connection connection : connectionSet) {
            // Notify the tabletop
            handleConnectionAdded(connection);
        }

        // Handle selections..
        // First clear the selection.
        selectDisplayedGem(null, false);

        // Keep track of the selection rectangle..
        Rectangle selectedGemsBounds = null;

        // Now select all pasted gems.
        for (int i = 0; i < displayedGems.length; i++) {
            DisplayedGem ithDisplayedGem = displayedGems[i];
            selectDisplayedGem(ithDisplayedGem, false);

            if (i == 0) {
                selectedGemsBounds = ithDisplayedGem.getBounds();
            } else {
                selectedGemsBounds.add(ithDisplayedGem.getBounds());
            }
        }

        // Ensure the gems are visible.
        if (selectedGemsBounds != null) {
            getTableTopPanel().scrollRectToVisible(selectedGemsBounds);
        }

        // Update the tabletop for the new gem graph state.
        updateForGemGraph();

        // Post an edit for the paste operation.
        undoableEditSupport.postEdit(new UndoablePasteDisplayedGemsEdit(TableTop.this, orderedDisplayedGems, connectionSet, collectorArgumentStateEdit));
    }

    /**
     * @param transferable the transferable to paste.
     * @return whether any of the current contents of the clipboard can be pasted on to the gem graph.
     * This returns true if the clipboard contains any gems which can be pasted.
     */
    boolean canPasteToGemGraph(Transferable transferable) {

        if (transferable != null) {
            DisplayedGem[] displayedGems;
            try {
                // Grab the data from the transferable
                Pair <DisplayedGem[],Set<Connection>> displayedGemsConnectionPair = UnsafeCast.<Pair<DisplayedGem[],Set<Connection>>>unsafeCast(transferable.getTransferData(DisplayedGemSelection.CONNECTION_PAIR_FLAVOR));
                displayedGems = displayedGemsConnectionPair.fst();

            } catch (UnsupportedFlavorException e) {
                // Flavour not supported.
                return false;

            } catch (IOException e) {
                // Data no longer available in the requested flavour.
                return false;
            }                            

            // If the gems are all reflectors, and their collectors don't exist in the gem graph, false.
            // Otherwise true.
            for (final DisplayedGem dGem : displayedGems) {
                Gem ithGem =  dGem.getGem();
                if (ithGem instanceof ReflectorGem) {
                    // We can paste this reflector if its collector is in the gem graph.
                    if (getGemGraph().hasGem(((ReflectorGem)ithGem).getCollector())) {
                        return true;
                    }
                } else if (ithGem instanceof FunctionalAgentGem) {
                    // We can paste this functional agent if its entity is the expected entity.
                    GemEntity pastingGemEntity = ((FunctionalAgentGem)ithGem).getGemEntity();
                    GemEntity expectedGemEntity = gemCutter.getPerspective().getVisibleGemEntity(pastingGemEntity.getName());

                    if (pastingGemEntity == expectedGemEntity) {
                        return true;
                    }

                } else {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Do the work necessary to carry out a user-initiated action to add a gem.
     * It will be placed at the next available location as determined by the TableTop.
     * @param displayedGem DisplayedGem the gem to add
     */
    void doAddGemUserAction(DisplayedGem displayedGem) {

        if (displayedGem != null) {

            // Need to make sure a collector has been assigned a name so that it can be properly positioned.
            if (displayedGem.getGem() instanceof CollectorGem) {
                CollectorGem cGem = (CollectorGem)displayedGem.getGem();
                gemGraph.assignDefaultName(cGem);
            }

            doAddGemUserAction(displayedGem, findAvailableDisplayedGemLocation(displayedGem));
        }
    }

    /**
     * Do the work necessary to carry out a user-initiated action to add a gem.
     * @param displayedGem DisplayedGem the gem to add
     * @param addPoint Point the location to add the gem
     */
    void doAddGemUserAction(DisplayedGem displayedGem, Point addPoint){

        addGemAndUpdate(displayedGem, addPoint);

        // Notify undo managers of the edit.
        undoableEditSupport.postEdit(new UndoableAddDisplayedGemEdit(TableTop.this, displayedGem));
    }

    /**
     * Shrinks the tabletop to the maximum required by the gem boundaries
     */
    void doShrinkTableTopUserAction() {

        // Gets the bounds of the current gems
        Rectangle gemGraphBounds = getGemGraphBounds();

        Rectangle visibleRect = getTableTopPanel().getVisibleRect();

        // Move everything to the top left corner, and then shrink
        int moveX = -gemGraphBounds.x;
        int moveY = -gemGraphBounds.y;

        visibleRect.x += moveX;
        visibleRect.y += moveY;

        // Aggregate changes
        undoableEditSupport.beginUpdate();
        undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_FitTableTop"));

        moveAllGems(moveX, moveY);

        getTableTopPanel().setSize(gemGraphBounds.width, gemGraphBounds.height);

        getTableTopPanel().setPreferredSize(new Dimension(gemGraphBounds.width, gemGraphBounds.height));

        // move from top left corner to top right corner.
        int xOffset = visibleRect.width < gemGraphBounds.width ? -10 : (visibleRect.width - gemGraphBounds.width) - 10;
        int yOffset = 10;

        moveAllGems(xOffset, yOffset);

        viewFocusedGem();
        resizeForGems();
        undoableEditSupport.endUpdateNoPost();

    }    

    /** 
     * Moves the window to center on the 'focused' gem if possible
     * If not possible, then move to top left corner of tabletop
     */
    private void viewFocusedGem() { 
        DisplayedGem focusedGem = getFocusedDisplayedGem();
        Rectangle visRect = getTableTopPanel().getVisibleRect();
        int width = visRect.width;
        int height = visRect.height;
        int x;
        int y;

        if (focusedGem!= null) {
            x = focusedGem.getLocation().x - width / 2;
            y = focusedGem.getLocation().y - height / 2;

            if (x < 0) {
                x = 0;
            }

            if (y < 0) {
                y = 0;
            }

        } else {
            x = 0;
            y = 0;
        }


        Rectangle newVisRect = new Rectangle (x, y, width, height);
        getTableTopPanel().scrollRectToVisible(newVisRect);
    }

    /**
     * Begin logging every positional change on all the gems. (Should be used
     * in conjunction with endPositionUpdate(DisplayedGemLocationListener)
     * @return DisplayedGemLocationListener the listener that was added on.the gems
     */
    private DisplayedGemLocationListener beginPositionUpdate() {
        // Create a listener to post gem translation edits
        DisplayedGemLocationListener locationListener = new DisplayedGemLocationListener() {
            public void gemLocationChanged(DisplayedGemLocationEvent e) {
                undoableEditSupport.postEdit(new UndoableChangeGemLocationEdit(
                        TableTop.this, (DisplayedGem)e.getSource(), e.getOldBounds().getLocation()));
            }
        };

        // Set the listener on all the displayed gems.
        for (final DisplayedGem displayedGem : getDisplayedGems()) {
            displayedGem.addLocationChangeListener(locationListener);
        }
        return locationListener;
    }

    /**
     * Remove the listener that was added on the gem
     * @param locationListener the listener to be removed
     */
    private void endPositionUpdate(DisplayedGemLocationListener locationListener) {

        // Remove the listener from all the displayed gems.
        for (final DisplayedGem displayedGem : getDisplayedGems()) {
            displayedGem.removeLocationChangeListener(locationListener);
        }
    }

    /**
     * Do the work necessary to carry out a user-initiated action to arrange 
     * the selected gems in the gem graph.
     * @param layoutArranger the object used to rearrange
     */
    private void arrangeSelectionUserAction(Graph.LayoutArranger layoutArranger) {

        // Increment the update level for the edit undo.  This will aggregate the gem translations.
        DisplayedGemLocationListener locationListener = beginPositionUpdate();

        layoutArranger.arrangeGraph();  

        // The connections routes are invalid now.
        for (final DisplayedConnection displayedConnection : connectionDisplayMap.values()) {
            displayedConnection.purgeRoute();
        }
        resizeForGems();

        // Decrement the update level.  This will post the edit if the level is zero.
        endPositionUpdate(locationListener); 
    }

    /**
     * Do the work necessary to carry out a user-initiated action to arrange, align and space out 
     * the gems on the gemGraph. This aligns all gems with the specified node
     * and ensures that no selected gems were overlapped. 
     * @param layoutArranger
     * @param refNode (the node that the gems will be aligned to)
     */
    void doTidyUserAction(Graph.LayoutArranger layoutArranger, Graph.Node refNode) {

        // Increment the update level for the edit undo.  This will aggregate the gem translations.
        undoableEditSupport.beginUpdate();
        DisplayedGemLocationListener locationListener = beginPositionUpdate();
        undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_Tidy_Gems"));

        // Get the trees we're dealing with
        List<Tree> trees = layoutArranger.getTrees();

        if (trees.isEmpty()) {
            return;
        }

        if (refNode == null) {
            refNode = trees.iterator().next().getRoot();
        }

        Point referenceLocation = refNode.getLocation();

        // Arrange the trees
        arrangeSelectionUserAction(layoutArranger);

        // Now we sort the trees from highest to lowest
        Graph.LayoutArranger.sortTreesOnYOrder(trees);

        // keep track of the difference between the coordinates
        int differenceInY = 0, differenceInX = 0;
        for (final Tree tree : trees) {

            // adjust the top right corner coordinate such that the refNode doesn't move
            Set<Graph.Node> nodes = new HashSet<Graph.Node>();
            tree.getNodes(nodes);

            // If it contains the node, then we want to keep use the right bound of this tree as our reference point.
            if (nodes.contains(refNode)) {
                Point nodeLocation = refNode.getLocation();
                int nodeYOffset = (nodeLocation.y - tree.getBounds().y);
                differenceInY += nodeYOffset;
                Rectangle rootBounds = tree.getRoot().getBounds();
                differenceInX = rootBounds.x + rootBounds.width - nodeLocation.x; 
                break;
            }
            differenceInY += tree.getBounds().height + 20;
        }

        // The upper right corner of the selected trees
        int highY = referenceLocation.y - differenceInY;
        int rightX = referenceLocation.x + differenceInX;

        for (final Tree tree : trees) {
            tree.setLocation(rightX - tree.getWidth(), highY);
            highY += tree.getHeight() + 20;
        }

        // Decrement the update level.  This will post the edit if the level is zero.
        undoableEditSupport.endUpdate();
        resizeForGems();
        endPositionUpdate(locationListener);   
    }

    /**
     * Tidy TableTop Function. This function aligns all the roots of the gem trees on the tabletop.
     * It is a composition of three user actions. Select All, Tidy Selection, Fit TableTop
     */
    void doTidyTableTopAction() {
        // Increment the update level for the edit undo.  This will aggregate the gem translations.
        undoableEditSupport.beginUpdate();
        undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_TidyTableTop"));
        selectAllGems();

        Graph.LayoutArranger layoutArranger = new Graph.LayoutArranger(getSelectedDisplayedGems());
        doTidyUserAction(layoutArranger, null);
        doShrinkTableTopUserAction();
        resizeForGems();

        // Decrement the update level.  This will post the edit if the level is zero.
        undoableEditSupport.endUpdate();
    }

    /**
     * Do the work necessary to carry out a user-initiated action to change a gem's location.
     * @param displayedGem the gem to move
     * @param newLocation the new location of the gem.
     */
    void doChangeGemLocationUserAction(DisplayedGem displayedGem, Point newLocation) {
        Rectangle oldBounds = displayedGem.getBounds();

        // the location won't actually change
        if (oldBounds.getLocation().equals(newLocation)) {
            return;
        }

        // Change the gem location
        changeGemLocation(displayedGem, newLocation);

        // Notify undo managers of the edit.
        undoableEditSupport.postEdit(new UndoableChangeGemLocationEdit(TableTop.this, displayedGem, oldBounds.getLocation()));
    }

    /**
     * Do the work necessary to carry out a user-initiated action to make a connection.
     * Note: a connection may not necessarily made if it results in an invalid GemGraph.
     * @param srcPart the source part
     * @param destPart the destination part
     * @return Connection the connection which was made, or null if a connection could not be made.
     */
    Connection doConnectIfValidUserAction(PartOutput srcPart, PartInput destPart) {
        // Return null if we can't connect.
        Connection connection = new Connection(srcPart, destPart);
        if (!gemGraph.canConnect(connection, getTypeCheckInfo())) {
            return null;
        }

        undoableEditSupport.beginUpdate();

        // Get the emitter and collector argument state from before the disconnection.
        StateEdit collectorArgumentState = new StateEdit(collectorArgumentStateEditable);

        // Get value gem values before the connection, and connect.
        Map<ValueGem, ValueNode> valueGemToValueMap = getValueGemToValueMap();
        connect(connection);

        // Re-type to update value gems.
        try {
            getGemGraph().typeGemGraph(getTypeCheckInfo());
        } catch (TypeException e) {
            // Shouldn't happen.  What now??
            GemCutter.CLIENT_LOGGER.log(Level.SEVERE, "Error connecting gems.");
            e.printStackTrace();
        }

        // Notify undo managers of any changes.
        postValueGemChanges(valueGemToValueMap);
        
        undoableEditSupport.postEdit(new UndoableConnectGemsEdit(TableTop.this, connection, collectorArgumentState));

        undoableEditSupport.endUpdate();

        // Update connectivity info for any connected code gems.
        if (srcPart.getGem() instanceof CodeGem) {
            updateForConnectivity(getCodeGemEditor((CodeGem)srcPart.getGem()));
        }
        if (destPart.getGem() instanceof CodeGem) {
            updateForConnectivity(getCodeGemEditor((CodeGem)destPart.getGem()));
        }

        return connection;
    }

    /**
     * Do the work necessary to carry out a user-initiated action to delete given gems from the TableTop.
     * Note: emitters for collectors in the set will also be deleted!
     * @param deleteSet the set of gems to delete
     */
    private void doDeleteGemsUserAction(Set<Gem> deleteSet) {

        Set<Gem> gemsToDelete = new HashSet<Gem>(deleteSet);

        // make sure we don't delete the target
        gemsToDelete.remove(getTargetCollector());

        if (gemsToDelete.isEmpty()) {
            return;
        }

        // make sure we stop intellicut        
        getIntellicutManager().stopIntellicut();

        // make sure popup menus go away if visible
        getTableTopPanel().hidePopup();

        // collectors to delete - we must delete last to preserve invariant that emitters cannot exist without collectors
        Set<Gem> selectedCollectors = new HashSet<Gem>(gemsToDelete);
        selectedCollectors.retainAll(gemGraph.getCollectors());

        // Make sure all associated emitters are also in the set of gems to delete.
        for (final Gem collectorGem : selectedCollectors) {
            gemsToDelete.addAll(((CollectorGem)collectorGem).getReflectors());
        }

        // Increment the update level for the edit undo.  This will aggregate edits (disconnections and deletions).
        undoableEditSupport.beginUpdate();

        DisplayedGem firstDisplayedGem = getDisplayedGem((Gem)gemsToDelete.toArray()[0]);
        String editName = (gemsToDelete.size() != 1) ? GemCutterMessages.getString("UndoText_DeleteGems") : GemCutterMessages.getString("UndoText_Delete", firstDisplayedGem.getDisplayText());

        undoableEditSupport.setEditName(editName);

        // now disconnect all gems, and delete all non-collector gems
        for (final Gem aGem : gemsToDelete) {

            // delete connections
            doDisconnectGemConnectionsUserAction(aGem);

            // remove if it's not a collector
            if (!(aGem instanceof CollectorGem)) {
                DisplayedGem displayedGem = getDisplayedGem(aGem);
                deleteGem(aGem);

                // Notify undo managers of the edit.
                undoableEditSupport.postEdit(new UndoableDeleteDisplayedGemEdit(TableTop.this, displayedGem));
            }
        }

        // delete collectors last
        for (final Gem aGem : selectedCollectors) {
            DisplayedGem displayedGem = getDisplayedGem(aGem);
            deleteGem(aGem);

            // Notify undo managers of the edit.
            undoableEditSupport.postEdit(new UndoableDeleteDisplayedGemEdit(TableTop.this, displayedGem));
        }

        // Decrement the update level.  This will post the edit if the level is zero.
        undoableEditSupport.endUpdate();
    }

    /**
     * Handle the gesture where the user attempts to disconnect two gem parts.
     * @param connection the connection to disconnect.
     */
    void handleDisconnectGesture(Connection connection) {
        doDisconnectUserAction(connection);
        updateForGemGraph();
    }

    /**
     * Do the work necessary to carry out a user-initiated action to disconnect a connection.
     * @param connection the connection to disconnect
     */
    void doDisconnectUserAction(Connection connection) {

        // Increment the update level to aggregate any disconnection edits.
        undoableEditSupport.beginUpdate();

        // Get the emitter and collector argument state from before the disconnection.
        StateEdit collectorArgumentState = new StateEdit(collectorArgumentStateEditable);

        // Now actually perform the disconnection
        disconnect(connection);        

        // Notify undo managers of the disconnection.
        undoableEditSupport.postEdit(new UndoableDisconnectGemsEdit(TableTop.this, connection, collectorArgumentState));

        // If the destination input is an unused code gem input, update its model, and post an edit for its state change.
        Gem destGem = connection.getDestination().getGem();
        if (destGem instanceof CodeGem) {
            CodeGemEditor destCodeGemEditor = getCodeGemEditor((CodeGem)destGem);

            if (destCodeGemEditor.isUnusedArg(connection.getDestination()) ||
                    destGem.isBroken() && ((CodeGem)destGem).getCodeResultType() != null) {   // check for incompatible connection.

                StateEdit stateEdit = new StateEdit(destCodeGemEditor, GemCutter.getResourceString("UndoText_DisconnectGems"));

                destCodeGemEditor.doSyntaxSmarts();
                updateForConnectivity(destCodeGemEditor);

                stateEdit.end();
                undoableEditSupport.postEdit(stateEdit);
            }
        }

        // Update code gem editors for connectivity.
        for (final CodeGemEditor codeGemEditor : codeGemEditorMap.values()) { 
            updateForConnectivity(codeGemEditor);
        }

        // Decrement the update level.  This will post the edit if the level is zero.
        undoableEditSupport.endUpdate();
    }

    /**
     * Do the work necessary to carry out a user-initiated action to disconnect all of a gem's connections.
     * @param gemToDisconnect Gem the gem on which to operate
     */
    private void doDisconnectGemConnectionsUserAction(Gem gemToDisconnect) {

        // Increment the update level to aggregate any disconnection edits.
        undoableEditSupport.beginUpdate();

        List<PartConnectable> connectableParts = gemToDisconnect.getConnectableParts();

        // disconnect any connectable parts which are actually connected
        for (final PartConnectable part : connectableParts) {
            if (part.isConnected()) {
                doDisconnectUserAction(part.getConnection());
            }
        }

        // Decrement the update level.  This will post the edit if the level is zero.
        undoableEditSupport.endUpdate();
    }

    
    /**
     * Do the work necessary to carry out a user-initiated action to split a connection into a collector/emitter pair
     * @param connection the connection to be replaced
     */
    void doSplitConnectionUserAction(Connection connection) {
       
        // The target of the new collector needs to be either #1 or #2 depending on which has the narrowest scope:
        // #1. the narrowest scoped targeted collector in its subtree.(the narrowest possible is the root collector for the tree)
        // For example: 
        //      private retargetExample list =
        //            let
        //                 filterfunc value = value > Cal.Utilities.Summary.average list;
        //            in
        //                 Cal.Collections.List.filter filterfunc list
        //           ;
        // The value collector is targeted to filterfunc while list is targeted to retargetExample.
        // In order to split the connection between " > " and filterfunc, the new collector will need to target
        // filterfunc, the narrower of the 2 target collectors for value and list. 
        // 
        // #2. the narrowest target of any unbound, unburnt input of the descendant forest that is equal/wider than the root collector. 
        // A very simple example: 
        //      private target x =
        //          let
        //              collGem x_1 = Cal.Core.Prelude.id (Cal.Core.Prelude.abs x_1)
        //          in
        //              collGem x
        //          ;
        // Argument x_1 is targeted to collGem. Therefore to split the connection between abs and id gems, the new collector needs to 
        // target collGem as well.  In this case, collGem is also the root collector for the tree.

        
        final Gem srcGem = connection.getSource().getGem();
        final Gem destGem = connection.getDestination().getGem();
       
        CollectorGem targetForNewCollector = getTargetCollector();  // initially set as the tabletop's target collector
        int currentTargetDepth = GemGraph.getCollectorDepth(targetForNewCollector); // Should be 0 for widest scope
        
        // If destination gem is the target collector for the tabletop, skip the checks.
        if (!destGem.equals(targetForNewCollector)) {

            // #1. Find all the reflectors in the subtree and their corresponding collectors. 
            // From the set, find the targeted collector with the deepest/narrowest scope.
            Set<ReflectorGem> emitterGemsInSubTree = UnsafeCast.<Set<ReflectorGem>>unsafeCast(GemGraph.obtainSubTreeGems(srcGem, ReflectorGem.class));
            Set<CollectorGem> collectorGemsInSubTree = new HashSet<CollectorGem>();

            if (!emitterGemsInSubTree.isEmpty()) {
                for (final ReflectorGem rGem : emitterGemsInSubTree) {
                    collectorGemsInSubTree.add(rGem.getCollector());
                }

                for (final CollectorGem cGem : collectorGemsInSubTree) {
                    int cGemDepth = GemGraph.getCollectorDepth(cGem);
                    int targetDepth = cGemDepth - 1;

                    if (targetDepth > currentTargetDepth) {
                        targetForNewCollector = cGem.getTargetCollectorGem();
                        currentTargetDepth = targetDepth;
                    }
                }
            }

            // #2. Find the targeted collectors of all the unburnt, unbound inputs in the descendant forest. 
            // Set that as the target for the new collector if the depth is in-between the previously found target collector in #1 and
            // the root gem of the tree. 
            
            // The narrowest target that the new collector can retarget to is the root collector for the tree. This is so the new
            // collector would have the necessary visibility. Thus the rootCollectorDepth is also the maximum depth for the target. 
            final CollectorGem rootCollector = destGem.getRootCollectorGem();
            final int rootCollectorDepth;
            if (rootCollector == null) {
                rootCollectorDepth = 0;
            } else {
                rootCollectorDepth = GemGraph.getCollectorDepth(rootCollector); 
            }

            // Skip the check if the current new collector's target is already at maximum depth
            if (rootCollectorDepth > currentTargetDepth) {
                
                List<PartInput> descendantInputs = GemGraph.obtainUnboundDescendantInputs(srcGem, TraversalScope.FOREST, InputCollectMode.UNBURNT_ONLY);
                for (final PartInput input : descendantInputs) {
                    CollectorGem inputTarget = GemGraph.getInputArgumentTarget(input);
                    final int targetDepth;
                    
                    if (inputTarget == null) {
                        targetDepth = 0;
                    } else {
                        targetDepth = GemGraph.getCollectorDepth(inputTarget);
                    }

                    if (rootCollectorDepth >= targetDepth && targetDepth > currentTargetDepth) {
                        targetForNewCollector = inputTarget;
                        currentTargetDepth = targetDepth;
                    }
                }
            }
        }
        
        undoableEditSupport.beginUpdate();
        DisplayedGem newCollectorGem = createDisplayedCollectorGem(new Point(0, 0), targetForNewCollector);
        DisplayedGem newEmitterGem = createDisplayedReflectorGem(new Point(0, 0), (CollectorGem) newCollectorGem.getGem());
        
        StateEdit collectorArgumentState = new StateEdit(collectorArgumentStateEditable);
        
        // Do the actual split action with the new gems
        splitConnectionWith(connection, newCollectorGem, newEmitterGem);
        
        // Display the edit box for the collector name for user input
        displayLetNameEditor((CollectorGem) newCollectorGem.getGem());
        
        // Notify undo managers of the disconnection.
        getUndoableEditSupport().setEditName(GemCutter.getResourceString("UndoText_SplitConnection"));
        undoableEditSupport.postEdit(new UndoableSplitConnectionEdit(TableTop.this, connection, newCollectorGem, newEmitterGem, collectorArgumentState));
        undoableEditSupport.endUpdate();
    }
    
    
    /**
     * Clear the tabletop, and add an initial gem.
     */
    void doNewTableTopUserAction() {

        // Increment the update level for the edit undo.  This will aggregate the edits taken to clear the tabletop.
        undoableEditSupport.beginUpdate();
        undoableEditSupport.setEditName(GemCutter.getResourceString("UndoText_ClearTableTop"));

        // Clean the tabletop.
        blankTableTop();

        // Adds the initial collector gem.
        resetTargetForNewTableTop();

        getTableTopPanel().repaint();

        // Decrement the update level.  This will post the edit if the level is zero.
        undoableEditSupport.endUpdate();
    }

    /**
     * Select the subtree of the gems specified
     * @param gems
     */
    List<DisplayedGem> doSelectSubtreeUser(Gem[] gems) {
        List<DisplayedGem> displayedGems = new ArrayList<DisplayedGem>();

        for (final Gem gem : gems) {
            Set<Gem> subTree = GemGraph.obtainSubTree(gem);
            for (final Gem subTreeGem : subTree) {
                DisplayedGem displayedGem = getDisplayedGem(subTreeGem);
                displayedGems.add(displayedGem);
                selectDisplayedGem(displayedGem, false);
            }
        }
        return displayedGems;
    }

    /**
     * Handle the gesture where the user attempts to retarget an input to a different collector
     * @param inputArgument the argument to retarget.
     * @param targetableCollector the collector to which the argument will be retargeted.
     */
    void handleRetargetInputArgumentGesture(Gem.PartInput inputArgument, CollectorGem targetableCollector) {
        handleRetargetInputArgumentGesture(inputArgument, targetableCollector, -1);
    }

    /**
     * Handle the gesture where the user attempts to retarget an input to a different collector
     * @param inputArgument the argument to retarget.
     * @param targetableCollector the collector to which the argument will be retargeted.
     * @param addIndex the index at which the retargeted argument will be placed, or -1 to add to the end.
     */
    void handleRetargetInputArgumentGesture(Gem.PartInput inputArgument, CollectorGem targetableCollector, int addIndex) {
        // If retargeting an argument to a collector which encloses the root (or is the root), don't retarget the collector.
        // Otherwise, retarget the collector to the argument target.

        getUndoableEditSupport().beginUpdate();

        // If not retargeting to an enclosing collector, retarget the root collector to the targetable collector.
        CollectorGem rootCollectorGem = inputArgument.getGem().getRootCollectorGem();
        if (!targetableCollector.enclosesCollector(rootCollectorGem)) {
            doRetargetCollectorUserAction(rootCollectorGem, targetableCollector);
        }

        // Retarget the argument.
        doRetargetInputArgumentUserAction(inputArgument, targetableCollector, addIndex);

        getUndoableEditSupport().endUpdate();
    }

    /**
     * Retarget an input argument from one collector to another.
     * @param inputArgument the input argument to retarget.
     * @param enclosingCollector the collector to which the input will be retargeted.
     * @param addIndex the index at which the retargeted argument will be placed, or -1 to add to the end.
     */
    void doRetargetInputArgumentUserAction(Gem.PartInput inputArgument, CollectorGem enclosingCollector, int addIndex) {
        CollectorGem oldTarget = GemGraph.getInputArgumentTarget(inputArgument);

        // retarget the argument.
        int oldArgIndex = retargetInputArgument(inputArgument, enclosingCollector, addIndex);

        // Notify the undo manager.
        getUndoableEditSupport().postEdit(new UndoableRetargetInputArgumentEdit(this, inputArgument, oldTarget, enclosingCollector, oldArgIndex));
    }

    /**
     * Retarget an input argument from one collector to another.
     * @param inputArgument the argument to retarget.
     * @param enclosingCollector the collector to which the input will be retargeted.
     * @param addIndex the index at which the retargeted argument will be placed, or -1 to add to the end.
     * @return the old argument index, or -1 if there was no old target.
     */
    int retargetInputArgument(Gem.PartInput inputArgument, CollectorGem enclosingCollector, int addIndex) {
        // Defer to the gem graph method.
        int oldIndex = gemGraph.retargetInputArgument(inputArgument, enclosingCollector, addIndex);

        getTableTopPanel().repaint(getDisplayedPartConnectable(inputArgument).getBounds());

        return oldIndex;
    }

    /**
     * Handle the gesture where the user attempts to retarget a collector to a different collector
     * @param collectorToRetarget the collector to retarget.
     * @param newTarget the collector to which the collector will be retargeted.
     */
    void handleRetargetCollectorGesture(CollectorGem collectorToRetarget, CollectorGem newTarget) {
        // Perform the action.
        doRetargetCollectorUserAction(collectorToRetarget, newTarget);
    }

    /**
     * Retarget a collector from one collector to another.
     * @param collectorToRetarget the collector to retarget.
     * @param newTarget the collector to which the collector will be retargeted.
     */
    void doRetargetCollectorUserAction(CollectorGem collectorToRetarget, CollectorGem newTarget) {
        CollectorGem oldTarget = collectorToRetarget.getTargetCollectorGem();

        // retarget the collector.
        collectorToRetarget.setTargetCollector(newTarget);
        getTableTopPanel().repaint(getDisplayedGem(collectorToRetarget).getBounds());

        // Notify the undo manager.
        getUndoableEditSupport().postEdit(new UndoableRetargetCollectorEdit(this, collectorToRetarget, oldTarget, newTarget));
    }


    /**
     * Do an user-initiated add record field action
     * @param rcGem the gem to add a new field to
     */
    void doAddRecordFieldUserAction (RecordCreationGem rcGem) {
        // Get the current state of the gem before the action
        Map<String, PartInput> prevStateMap = rcGem.getFieldNameToInputMap();
        
        rcGem.addNewFields(1);
        updateForGemGraph();
        getUndoableEditSupport().postEdit(new UndoableModifyRecordFieldEdit(rcGem, prevStateMap));
    }
    
    
    /**
     * Do an user-initiated delete field action 
     * @param rcGem the gem to delete a field from
     * @param fieldToDelete the field to be removed
     */
    void doDeleteRecordFieldUserAction (RecordCreationGem rcGem, String fieldToDelete) {
        // Get the current state of the gem before the action
        Map<String, PartInput> prevStateMap = rcGem.getFieldNameToInputMap();
       
        rcGem.deleteField(fieldToDelete);
        updateForGemGraph();
        getUndoableEditSupport().postEdit(new UndoableModifyRecordFieldEdit(rcGem, prevStateMap));
    }
    
    
    /**
     * Update the field in a record field selection gem and create an undoable edit
     * This will always mark the field as fixed.
     * @param gem
     * @param newFieldName the field name to set the gem to 
     */
    private void updateRecordFieldSelectionGem(RecordFieldSelectionGem gem, FieldName newFieldName) {
        String oldFieldName = gem.getFieldNameString();
        boolean oldFixed = gem.isFieldFixed();
        
        gem.setFieldName(newFieldName.getCalSourceForm());
        gem.setFieldFixed(true);
        
        undoableEditSupport.postEdit(new UndoableChangeFieldSelectionEdit(this, gem, oldFieldName, oldFixed));
    }
    
    
    /**
     * Handle the gesture where the user attempts to connect two gem parts.
     * A dialog may be shown if the attempted connection appears to be ok, but violates global type constraints.
     * @param srcPart the source part
     * @param destPart DisplayedPart the destination part
     * @return Connection the connection which was made, or null if a connection could not be made.
     */
    Connection handleConnectGemPartsGesture(PartConnectable srcPart, PartInput destPart) {

        Connection newConnection = null;        
        boolean canConnect = false;
        FieldName fieldName;
        
        undoableEditSupport.beginUpdate();
        
        //this is a record field selection gem for which the field has been updated by the connection
        RecordFieldSelectionGem modifiedRecordFieldSelectionGem=null;
        
        ModuleTypeInfo currentModuleTypeInfo = getCurrentModuleTypeInfo();

        //is connection possible as a normal connection or value gem connection
        canConnect = GemGraph.isCompositionConnectionValid(srcPart, destPart, currentModuleTypeInfo) ||
                     GemGraph.isDefaultableValueGemSource(srcPart, destPart, gemCutter.getConnectionContext());
           
        //connection possible by modifying the record field selection field?
        if (!canConnect &&
            ((fieldName=GemGraph.isValidConnectionToRecordFieldSelection(srcPart, destPart, currentModuleTypeInfo)) != null)) {
           
            
            modifiedRecordFieldSelectionGem=(RecordFieldSelectionGem)destPart.getGem();
            updateRecordFieldSelectionGem(modifiedRecordFieldSelectionGem, fieldName);
            canConnect = true;
        } 
        
        //burn connection possible by modifying the record field selection field?
        if (!canConnect &&
            ((fieldName=GemGraph.isValidConnectionFromBurntRecordFieldSelection(srcPart, destPart, currentModuleTypeInfo)) !=null)) {

            modifiedRecordFieldSelectionGem = (RecordFieldSelectionGem)srcPart.getGem();
            updateRecordFieldSelectionGem(modifiedRecordFieldSelectionGem, fieldName);
            canConnect = true;
        }
        
        if (canConnect) {
            // Make the connection
            newConnection = doConnectIfValidUserAction((PartOutput)srcPart, destPart);

            if (newConnection == null) {
                showCannotConnectDialog();
            }
        }

        if (newConnection != null) {
            updateForGemGraph();

            //if a record field selection gem was updated show the user the field selection editor
            if (modifiedRecordFieldSelectionGem != null) {
                final RecordFieldSelectionGem gem = modifiedRecordFieldSelectionGem;
                undoableEditSupport.beginUpdate();
                
                //we must invoke the editor to run in the background so that its focus etc are not affected by the current connect operation
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        final JComponent editor = displayRecordFieldSelectionEditor(gem);

                        //we attach a listener to find out when the editor is finished in order to finalize the undoable edit operation
                        editor.addFocusListener(new FocusListener() {
                            public void focusGained(FocusEvent e) {}
                            
                            /** when the focus is lost it implies the editor is closing, any changes are committed so we can end the compound edit*/
                            public void focusLost(FocusEvent e) {
                                undoableEditSupport.endUpdate();
                                editor.removeFocusListener(this);
                            }
                        });
                        
                    }
                });
            }
        }        
        undoableEditSupport.endUpdate();
        
        return newConnection;
    }

    /**
     * Show a dialog explaining why a connection can't be made..
     */
    void showCannotConnectDialog() {
        // the connection violates global type constraints.
        String title = GemCutter.getResourceString("CannotConnectDialogTitle");
        String message = GemCutter.getResourceString("CannotConnectErrorMessage");
        JOptionPane.showMessageDialog(TableTop.this.getTableTopPanel(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Autoconnect using intellicut, if possible.
     * @param part the part which we should attempt to autoconnect using intellicut
     * @return boolean whether a connection was made
     */
    boolean handleIntellicutAutoConnectGesture(DisplayedPartConnectable part) {
        return gemCutter.getIntellicutManager().attemptIntellicutAutoConnect(part);
    }

    /**
     * Start intellicut mode if appropriate
     * @param partClicked DisplayedPart the part in question
     * @return boolean whether the intellicut was started
     */
    boolean maybeStartIntellicutMode(DisplayedPart partClicked){

        if (!(partClicked instanceof DisplayedPartConnectable)) {
            return false;
        }

        // start intellicut mode if sink or source part, and not a burnt input
        PartConnectable part = ((DisplayedPartConnectable) partClicked).getPartConnectable();
        if ((part instanceof PartInput && !((PartInput)part).isBurnt()) || 
                (part instanceof PartOutput)) {

            if (!part.isConnected()) {

                // Special Case: We do not start Intellicut if the input part is out of range of the TableTop.
                if (((DisplayedPartConnectable) partClicked).getConnectionPoint().getY() <= 0) {
                    return false;
                }           

                // Sometimes (Eg: Broken Code gem), the TypeExpr is null, and should not have intellicut used on it.
                if (part.getType() == null) {
                    return false;
                }

                // Proceed with Intellicut mode.
                getIntellicutManager().startIntellicutModeForTableTop((DisplayedPartConnectable) partClicked);

                return true;
            }
        }
        return false;
    }

    /**
     * Handle the gesture to delete a given gem from the TableTop.
     * A dialog may pop up asking for input, if collectors are selected but not their associated emitters.
     * @param gemToDelete Gem the gem to delete
     */
    void handleDeleteGemGesture(Gem gemToDelete) {
        Set<Gem> gemsToDelete = new HashSet<Gem>();
        gemsToDelete.add(gemToDelete);

        // defer to the more general method.
        handleDeleteGemsGesture(gemsToDelete);
    }

    /**
     * Handle the gesture to delete selected gems from the TableTop.
     * A dialog may pop up asking for input, if collectors are selected but not their associated emitters.
     */
    void handleDeleteSelectedGemsGesture() {
        // defer to the more general method.
        Set<Gem> selectedGemsSet = new HashSet<Gem>(Arrays.asList(getSelectedGems()));
        handleDeleteGemsGesture(selectedGemsSet);
    }

    /**
     * returns the current manager that handles all the burning
     * @return TableTopBurnManager
     */
    TableTopBurnManager getBurnManager() { 
        return burnManager;
    }

    /**
     * Handle the gesture to delete gems from the TableTop.
     * A dialog may pop up asking for input, if collectors are selected but not their associated emitters.
     * @param deleteSet the set of gems to delete
     */
    void handleDeleteGemsGesture(Set<Gem> deleteSet) {

        // make sure we don't delete the target
        deleteSet.remove(getTargetCollector());

        // make sure we stop intellicut        
        getIntellicutManager().stopIntellicut();

        // make sure popup menus go away if visible
        getTableTopPanel().hidePopup();

        Set<Gem> gemsToDelete = new HashSet<Gem>(deleteSet);

        // collectors to delete - we must delete last to preserve invariant that emitters cannot exist without collectors
        Set<Gem> selectedCollectors = new HashSet<Gem>(deleteSet);
        selectedCollectors.retainAll(gemGraph.getCollectors());

        // Take appropriate action if we need to warn the user
        boolean deleteCollectorsWithEmitters = checkDeleteGemWarning(UnsafeCast.<Set<CollectorGem>>unsafeCast(selectedCollectors), gemsToDelete);
        if (!deleteCollectorsWithEmitters) {
            return;
        }    

        // go through all the gems, adding all associated emitter and reflector gems to the set to delete
        for (final Gem collectorGem : selectedCollectors) {
            gemsToDelete.addAll(((CollectorGem)collectorGem).getReflectors());
        }

        // Delete the gems.
        doDeleteGemsUserAction(gemsToDelete);

        // update gem graph
        updateForGemGraph();

    }

    /**
     * Checks for the situation where a collector has been selected for deletion but one of its associated unselected
     * emitters is not selected and also connected to something.  If so, display a dialog and prompt for the right 
     * thing to do.
     * @param selectedCollectors the set of selected collectors
     * @param selectedGemsSet the set of selected gems
     * @return boolean whether the user wishes to continue with the deletion.
     */
    private boolean checkDeleteGemWarning(Set<CollectorGem> selectedCollectors, Set<Gem> selectedGemsSet) {

        // Keep track of whether we warned the user that collector gems would be deleted
        boolean warnCollectorDelete = false;
        boolean deleteCollectorsWithEmitters = true;

        // first iterate through the set of collectors, and find out if there are any with associated
        // emitters or reflectors that are connected but not selected, so that we can warn the user if necessary
        for (Iterator<CollectorGem> selectedIt = selectedCollectors.iterator(); selectedIt.hasNext() && !warnCollectorDelete; ) {

            // Get the next element for consideration
            CollectorGem collectorGem = selectedIt.next();

            // iterate through the associated emitters, checking if they are connected and not selected
            Set<ReflectorGem> reflectors = collectorGem.getReflectors();
            for (final ReflectorGem reflectorGem : reflectors) {
                if (reflectorGem.isConnected() && !(selectedGemsSet.contains(reflectorGem))) {
                    warnCollectorDelete = true;
                    break;
                }
            }
        }
        // if necessary, warn the user if there are associated emitters which are connected, as they will be deleted
        if (warnCollectorDelete) {
            // Formulate the warning.
            String titleString = GemCutter.getResourceString("WarningDialogTitle");
            String message = GemCutter.getResourceString("DeleteCollectorWarning");

            // Show the warning.  See if the user really wants to delete collector and emitters
            int option = JOptionPane.showConfirmDialog(getTableTopPanel(), message, titleString, 
                    JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);

            // if the user answered no, don't delete collectors with emitters
            if (option == JOptionPane.NO_OPTION) {
                deleteCollectorsWithEmitters = false;
            }
        }
        return deleteCollectorsWithEmitters;
    }

    /*
     * Methods supporting XMLPersistable                 ********************************************
     */

    /**
     * Attach the saved form of this object as a child XML node.
     * @param parentNode Node the node that will be the parent of the generated XML.  The generated XML will 
     * be appended as a subtree of this node.  
     * Note: parentNode must be a node type that can accept children (eg. an Element or a DocumentFragment)
     */
    public void saveXML(Node parentNode) {

        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the TableTop element
        Element resultElement = document.createElement(GemPersistenceConstants.TABLETOP_TAG);
        parentNode.appendChild(resultElement);

        // Create a new gem context.
        GemContext gemContext = new GemContext();

        // Create a document fragment to hold the gem elements
        DocumentFragment documentFragment = document.createDocumentFragment();


        // Get the gem graph gems, separate these into collectors and other gems.
        Set<CollectorGem> collectorSet = gemGraph.getCollectors();

        Set<Gem> otherGems = new HashSet<Gem>(gemGraph.getGems());
        otherGems.removeAll(collectorSet);

        // Organize the collector gems so that outer collectors come before inner ones.
        Set<CollectorGem> orderedCollectorSet = GemGraph.getOrderedCollectorSet(collectorSet);

        // Attach gem elements to the doc fragment - collectors first, then other gems.
        for (final CollectorGem collectorGem : orderedCollectorSet) {
            getDisplayedGem(collectorGem).saveXML(documentFragment, gemContext);
        }

        for (final Gem otherGem : otherGems) {
            getDisplayedGem(otherGem).saveXML(documentFragment, gemContext);
        }


        // Now attach the gem elements        
        resultElement.appendChild(documentFragment);

        // Attach connection elements
        Collection<DisplayedConnection> displayedConnections = connectionDisplayMap.values();
        for (final DisplayedConnection displayedConnection : displayedConnections) {

            displayedConnection.getConnection().saveXML(resultElement, gemContext);
        }

        // Now add Tabletop-specific info
        // For now, there isn't any.  Maybe you could save some metadata here.  Trivial stuff like type colours?
    }

    
    /**
     * Return whether the element given corresponds to a displayed gem.
     * @param node
     * @return boolean
     */
    private boolean isDisplayedGemElement(Node node) {
        return GemCutterPersistenceHelper.isDisplayedGemElement(node);
    }
    
    /**
     * Load this object's state.
     * @param element the element at the head of the subtree which represents the object to reconstruct.
     * @param perspective the perspective into which the load should occur.
     * @param loadStatus the ongoing status of the load.
     */
    public void loadXML(Element element, Perspective perspective, Status loadStatus) {

        try {
            XMLPersistenceHelper.checkTag(element, GemPersistenceConstants.TABLETOP_TAG);

        } catch (BadXMLDocumentException bxde) {
            loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_TableTopLoadFailure"), bxde));

        }

        // Make sure the tabletop is clear before we continue further.
        blankTableTop();

        // Temporarily set the target's name to something invalid, to avoid name collisions with any gems being loaded.
        gemGraph.getTargetCollector().setName("!tempTargetLoadName!");

        // Also disable automatic gem graph reflector updating.
        gemGraph.setArgumentUpdatingDisabled(true);

        // The set of loaded collector gems.
        Set<Gem> collectorGemSet = new HashSet<Gem>();

        // We instantiate emitters last, as they cannot be instantiated without their associated collectors.
        Set<Element> emitterElements = new HashSet<Element>();

        // A new context in which the gems are loaded.
        GemContext gemContext = new GemContext();
        Argument.LoadInfo loadInfo = new Argument.LoadInfo();

        // Set of IDs of gem elements which couldn't be instantiated
        Set<String> unknownGemIds = new HashSet<String>();

        try {
            // Instantiate displayed gem children, except for emitters (which need collectors to be instantiated first)
            boolean targetWasLoaded = false;
            Node childNode;
            for (childNode = element.getFirstChild(); isDisplayedGemElement(childNode); childNode = childNode.getNextSibling()) {

                Element displayedGemElement = (Element)childNode;

                // Get the gem node.
                Node gemNode = displayedGemElement.getFirstChild();
                try {
                    XMLPersistenceHelper.checkIsElement(gemNode);
                } catch (BadXMLDocumentException e) {
                    loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_ErrorLoadingFromNode") + gemNode.getLocalName(), e));
                }

                // instantiate gem node
                Class<?> gemClass = getGemClass((Element)gemNode);

                if (gemClass == null) {
                    // unrecognized type
                    String gemId = Gem.getGemId(displayedGemElement);
                    if (gemId != null) {
                        unknownGemIds.add(gemId);
                    }

                    loadStatus.add(new Status(Status.Severity.WARNING, GemCutter.getResourceString("SOM_UnrecognizedGemType") + displayedGemElement.getLocalName(), null));

                } else if (gemClass == ReflectorGem.class) {
                    // emitter gem - save for later
                    emitterElements.add(displayedGemElement);

                } else {
                    // a gem which we should instantiate now.  Do this if possible.
                    DisplayedGem displayedGem = getDisplayedGem(displayedGemElement, gemContext, loadInfo, perspective, loadStatus);

                    if (displayedGem == null) {
                        // Couldn't instantiate.
                        String gemId = Gem.getGemId(displayedGemElement);

                        if (gemId != null) {
                            unknownGemIds.add(gemId);
                        }
                        continue;
                    }

                    Gem gem = displayedGem.getGem();
                    if (!targetWasLoaded && gem instanceof CollectorGem) {
                        // HACK(?): reassign things so the gem graph target looks like the loaded target.
                        CollectorGem newTarget = (CollectorGem)gem;
                        CollectorGem gemGraphTarget = gemGraph.getTargetCollector();

                        gemGraphTarget.setName(newTarget.getUnqualifiedName());
                        targetDisplayedCollector.setLocation(displayedGem.getLocation());
                        gemContext.addGem(gemGraphTarget, gemContext.getIdentifier(newTarget, false));
                        loadInfo.remapGem(newTarget, gemGraphTarget);
                        targetWasLoaded = true;

                    } else {
                        // Add the gem to the tabletop
                        addGem(displayedGem, displayedGem.getLocation());
                    }

                    // Add to the collector map if it's a collector
                    if (gem instanceof CollectorGem) {
                        collectorGemSet.add(gem);
                    }
                }
            }

            if (!targetWasLoaded) {
                loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_TargetDidntLoad"), null));
                gemGraph.getTargetCollector().setName("target");  // Just set to something valid..
            }


            // Instantiate displayed emitters.
            for (final Element emitterElement : emitterElements) {

                DisplayedGem displayedGem = getDisplayedGem(emitterElement, gemContext, loadInfo, perspective, loadStatus);

                if (displayedGem == null) {
                    // Couldn't instantiate.
                    String gemId = Gem.getGemId(emitterElement);

                    if (gemId != null) {
                        unknownGemIds.add(gemId);
                    }
                    continue;
                }

                // Add the gem to the tabletop
                addGem(displayedGem, displayedGem.getLocation());
            }


            // update collector and reflector argument info.
            Set<CollectorGem> collectorSet = gemGraph.getCollectors();
            for (final CollectorGem collectorGem : collectorSet) {
                try {
                    collectorGem.loadArguments(loadInfo, gemContext);
                } catch (BadXMLDocumentException bdxe) {
                    loadStatus.add(new Status(Status.Severity.WARNING, GemCutter.getResourceString("SOM_ErrorLoadingCollector"), bdxe));
                }
            }
            for (final Gem gem : gemGraph.getGems()) {
                if (gem instanceof ReflectorGem) {
                    try {
                        ((ReflectorGem)gem).loadArguments(loadInfo, gemContext);
                    } catch (BadXMLDocumentException bdxe) {
                        loadStatus.add(new Status(Status.Severity.WARNING, GemCutter.getResourceString("SOM_ErrorLoadingEmitter"), bdxe));
                    }
                }
            }

            // Instantiate connections.
            while (childNode != null && childNode instanceof Element && Connection.isConnectionElement((Element)childNode)) {

                Element connectionElement = (Element)childNode;

                // Make the connection.  Skip connecting unknown gems.
                Connection connection = null;
                try {
                    connection = Connection.elementToConnection(connectionElement, gemContext, unknownGemIds);

                } catch (BadXMLDocumentException bxde) {
                    loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_CouldntMakeConnection"), bxde));
                }

                if (connection != null) {
                    Gem.PartOutput fromPart = connection.getSource();
                    Gem.PartInput toPart = connection.getDestination();

                    if (fromPart == null || toPart == null) {
                        loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("CouldntMakeConnection"), null));

                    } else {
                        // Attempt the connection
                        try {
                            connect(connection);
                        } catch (Exception e) {
                            loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("CouldntMakeConnection"), e));
                        }
                    }
                }

                // get the next element
                childNode = childNode.getNextSibling();
            }

            // Make sure the inputs targets actually make sense.
            gemGraph.validateInputTargets();

            // Update reflectors.
            for (final CollectorGem gem : collectorSet) {
                gem.updateReflectedInputs();
            }

            // Now that we have instantiated as much of the gem graph as we can, 
            //   carry out some validation to ensure that the current gem graph is valid.
            boolean gemGraphValid = true;
            try {
                gemGraph.typeGemGraph(getTypeCheckInfo());

            } catch (TypeException te) {
                gemGraphValid = false;
                loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_InvalidGemGraph"), null));
            }

            // If the gem graph is not valid, disconnect value gems, since they are a big culprit.
            if (!gemGraphValid) {

                // The biggest culprit is value gems.  Disconnect them all.
                for (final Connection connection : gemGraph.getConnections()) {
                    if (connection.getSource().getGem() instanceof ValueGem) {
                        disconnect(connection);
                    }
                }

                // Make sure the inputs targets actually make sense.
                gemGraph.validateInputTargets();

                // Update reflectors.
                for (final CollectorGem gem : collectorSet) {
                    gem.updateReflectedInputs();
                }

                // try again..
                gemGraphValid = true;
                try {
                    gemGraph.typeGemGraph(getTypeCheckInfo());
                } catch (TypeException te) {
                    gemGraphValid = false;
                }
            }

            // If the gem graph is still not valid, disconnect the remaining connections.
            // This should almost always work.
            if (!gemGraphValid) {
                // Disconnect remaining connections.
                for (final Connection connection : gemGraph.getConnections()) {
                    disconnect(connection);
                }

                // Make sure the inputs targets actually make sense.
                gemGraph.validateInputTargets();

                // Update reflectors.
                for (final CollectorGem gem : collectorSet) {
                    gem.updateReflectedInputs();
                }

                try {
                    // Type check..
                    gemGraph.typeGemGraph(getTypeCheckInfo());
                } catch (TypeException e) {
                    GemCutter.CLIENT_LOGGER.log(Level.SEVERE, GemCutter.getResourceString("SOM_CantConstructLoadState"));
                    doNewTableTopUserAction();
                }
            }


            // sanity test..
            if (GemCutterPersistenceHelper.isDisplayedGemElement(childNode)) {
                BadXMLDocumentException bxde = new BadXMLDocumentException(childNode, "Gem elements must appear before connection elements.");
                loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_InvalidXMLstructure"), bxde));
            }

        } finally {
            // Try to recover from unexpected exceptions..

            // Now make sure the tabletop is actually big enough to hold all the gems.
            resizeForGems();

            // Re-enable automatic gem graph reflector updating.
            gemGraph.setArgumentUpdatingDisabled(false);

            // Activate codegem smarts.
            updateCodeGemEditors();

            // Say that all burned inputs are manually burned.
            Set<Gem> gemSet = gemGraph.getRoots();
            for (final Gem rootGem : gemSet) {
                List<PartInput> burntInputs = GemGraph.obtainUnboundDescendantInputs(rootGem, GemGraph.TraversalScope.TREE, GemGraph.InputCollectMode.BURNT_ONLY);
                burnManager.getManuallyBurntSet().addAll(burntInputs);
            }
        }
    }

    /**
     * Get the identifier for the first gem element (by preorder traversal) descending from a given element.
     * @param gemAncestorElement the ancestor element of the gem to id.
     * @return the gem's identifier, or null if a gem descendant cannot be found with an appropriate attribute.
     */
    public static String getGemId(Element gemAncestorElement) {

        // Get the descendant nodes with the gem tag.
        NodeList nodeList = gemAncestorElement.getElementsByTagName(GemPersistenceConstants.GEM_TAG);

        // Iterate over them, looking for an element with the id attribute.
        int nNodes = nodeList.getLength();
        for (int i = 0; i < nNodes; i++) {

            Node node = nodeList.item(i);
            if (node instanceof Element) {

                String gemId = ((Element)node).getAttribute(GemPersistenceConstants.GEM_ID_ATTR);
                if (!gemId.equals("")) {
                    return gemId;
                }
            }
        }
        return null;
    }


    /**
     * Get the class of gem represented by the given element.
     * @param gemElement Element the element representing the gem in question.
     * @return Class the class represented by the element, or null if unknown.
     */
    private static Class<? extends Gem> getGemClass(Element gemElement) {

        String tagName = gemElement.getLocalName();
        if (tagName.equals(GemPersistenceConstants.CODE_GEM_TAG)) {
            return CodeGem.class;

        } else if (tagName.equals(GemPersistenceConstants.COLLECTOR_GEM_TAG)) {
            return CollectorGem.class;

        } else if (tagName.equals(GemPersistenceConstants.EMITTER_GEM_TAG)) {
            return ReflectorGem.class;
            
        } else if (tagName.equals(GemPersistenceConstants.RECORD_FIELD_SELECTION_GEM_TAG)) {
            return RecordFieldSelectionGem.class;

        } else if (tagName.equals(GemPersistenceConstants.FUNCTIONAL_AGENT_GEM_TAG)) {
            return FunctionalAgentGem.class;

        } else if (tagName.equals(GemPersistenceConstants.VALUE_GEM_TAG)) {
            return ValueGem.class;

        } else if (tagName.equals(GemPersistenceConstants.RECORD_CREATION_GEM_TAG)) {
            return RecordCreationGem.class;

        } else {
            // Unknown gem type
            return null;
        } 
    }

    /**
     * Attach the saved form of the given displayed gem as a child XML node.
     * @param parentNode Node the node that will be the parent of the generated XML.  
     *   The generated XML will be appended as a subtree of this node.  
     * @param displayedGemToSave the displayed gem to save.
     * Note: parentNode must be a node type that can accept children (eg. an Element or a DocumentFragment)
     * @param gemContext the context in which the gem is saved.
     */
    public void saveDisplayedGem(DisplayedGem displayedGemToSave, Node parentNode, GemContext gemContext) {

        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the displayed gem element
        Element resultElement = document.createElement(GemPersistenceConstants.DISPLAYED_GEM_TAG);
        parentNode.appendChild(resultElement);

        // Add an element for the info for the underlying gem.
        displayedGemToSave.getGem().saveXML(resultElement, gemContext);

        // Add a child location element
        Element locationElement = GemCutterPersistenceHelper.pointToElement(displayedGemToSave.getLocation(), document);
        resultElement.appendChild(locationElement);
    }

    /**
     * Get the displayed gem represented by the given element.
     * @param displayedGemElement Element the element representing the displayed gem to instantiate.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     * @param perspective the perspective in which the load is taking place.
     * @param loadStatus the ongoing status of the load.
     * @return DisplayedGem the displayed gem represented by the given element.  Null if the gem couldn't be instantiated.
     */
    private DisplayedGem getDisplayedGem(Element displayedGemElement, GemContext gemContext, Argument.LoadInfo loadInfo, 
            Perspective perspective, Status loadStatus) {

        try {
            XMLPersistenceHelper.checkTag(displayedGemElement, GemPersistenceConstants.DISPLAYED_GEM_TAG);

            // Get the gem node.
            Node gemNode = displayedGemElement.getFirstChild();
            XMLPersistenceHelper.checkIsElement(gemNode);
            Element gemElement = (Element)gemNode;

            // Determine the location
            Node locationNode = gemNode.getNextSibling();
            XMLPersistenceHelper.checkIsElement(locationNode);

            Point location;
            try {
                location = GemCutterPersistenceHelper.elementToPoint((Element)locationNode);

            } catch (BadXMLDocumentException bxde) {
                loadStatus.add(new Status(Status.Severity.WARNING, GemCutter.getResourceString("SOM_CantLoadLocation"), bxde));
                location = new Point();              // default location
            }

            Class<? extends Gem> gemClass = getGemClass(gemElement);
            Gem loadedGem;

            if (gemClass == CodeGem.class) {
                loadedGem = CodeGem.getFromXML(gemElement, gemContext, gemCutter.getCodeGemAnalyser());

            } else if (gemClass == CollectorGem.class) {
                loadedGem = CollectorGem.getFromXML(gemElement, gemContext, loadInfo);
                
            } else if (gemClass == RecordFieldSelectionGem.class) {
                loadedGem = RecordFieldSelectionGem.getFromXML(gemElement, gemContext, loadInfo);

            } else if (gemClass == FunctionalAgentGem.class) {
                loadedGem = FunctionalAgentGem.getFromXML(gemElement, gemContext, gemCutter.getWorkspace());

            } else if (gemClass == ReflectorGem.class) {
                loadedGem = ReflectorGem.getFromXML(gemElement, gemContext, loadInfo);

            } else if (gemClass == ValueGem.class) {
                loadedGem = ValueGem.getFromXML(gemElement, gemContext, gemCutter.getValueRunner(), perspective.getWorkingModuleName());

            } else if (gemClass == RecordCreationGem.class) {
                loadedGem = RecordCreationGem.getFromXML(gemElement, gemContext, loadInfo);

            } else {
                throw new BadXMLDocumentException(gemElement, "Unhandled gem class: " + gemClass);
            }

            return createDisplayedGem(loadedGem, location);

        } catch (BadXMLDocumentException bxde) {
            loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_CantInstantiateGem"), bxde));
        }

        // Not one of the types we know about.        
        return null;
    }


    /**
     * Adds a gem graph listener that will listen to macroscopic changes such as connections and additions of gems
     * @param gemGraphChangeListener the listener to add
     */
    void addGemGraphChangeListener(GemGraphChangeListener gemGraphChangeListener) {
        gemGraph.addGraphChangeListener(gemGraphChangeListener);
    }
}

