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
 * DisplayedGem.java
 * Creation date: (02/18/02 6:27:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.valuenode.CALRunner;
import org.openquark.cal.valuenode.Target;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.TableTop.DisplayContext;
import org.openquark.util.EmptyIterator;
import org.openquark.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The representation of a gem extended with notions of location and size
 * @author Edward Lam
 */
public class DisplayedGem implements Graph.Node {

    /** provides context information for calculating display properties of this gem. */
    private final DisplayContext displayContext;
    
    /** The gem (model) which is represented by this DisplayedGem. Should be final once assigned. */
    private final Gem gem;

    /** The body part which you can see */
    private final DisplayedPartBody displayedBodyPart;

    /** The displayed output part */
    private DisplayedPartOutput displayedOutputPart;

    /** The array of displayed input parts */
    private DisplayedPartInput[] displayedInputParts;

    /** Gem location */
    private Point location;
    
    /** Gem shape.  Only intended to be mutated by implementing class.  */
    private DisplayedGemShape displayedGemShape;
    
    /** The provider for the displayed gem's shape. */
    private final DisplayedGemShape.ShapeProvider shapeProvider;

    /** 
     * Keys for fields in map used with state editable interface 
     */
    private static final String DISPLAYED_INPUT_PARTS_KEY = "DisplayedInputPartsStateKey";
    
    // Listeners
    private DisplayedGemLocationListener gemLocationListener;   // listeners for displayed gem location changes
    private DisplayedGemSizeListener gemSizeListener;           // listeners for displayed gem size changes


    /**
     * Construct a Gem from an initial location
     * @param displayContext provides context information for calculating display properties of this gem
     * @param shapeProvider the provider for this gem's shape.
     * @param gem Gem the gem (model) which is represented by this DisplayedGem
     * @param location Point the initial location of the displayed gem
     */
    public DisplayedGem(DisplayContext displayContext, DisplayedGemShape.ShapeProvider shapeProvider, Gem gem, Point location) {
        
        if (displayContext == null || gem == null || location == null) {
            throw new NullPointerException();
        }
        
        this.displayContext = displayContext;
        this.shapeProvider = shapeProvider;
        this.gem = gem;
        this.location = new Point(location);
        
        // create parts for input, output, and body
        displayedBodyPart = new DisplayedPartBody();

        Gem.PartOutput outputPartModel = gem.getOutputPart();
        if (outputPartModel != null) {
            displayedOutputPart = new DisplayedPartOutput(outputPartModel);
        }

        int nArgs = gem.getNInputs();
        displayedInputParts = new DisplayedPartInput[nArgs];
        for (int i = 0; i < nArgs; i++) {
            displayedInputParts[i] = new DisplayedPartInput(gem.getInputPart(i));
        }
    }

    /**
     * Get the gem.
     * @return Gem the gem to which this displayed part belongs
     */
    public final Gem getGem() {
        return gem;
    }

    /**
     * Get the DisplayedGemShape.
     * @return the displayedGemShape.
     */
    public final DisplayedGemShape getDisplayedGemShape() {
        if (displayedGemShape == null) {
            displayedGemShape = shapeProvider.getDisplayedGemShape(this);
        }
        return displayedGemShape;
    }

    /**
     * Get a Shape representing the body shape of this Gem
     * @return Shape the body Shape
     */
    public final Shape getBodyShape() {
        return getDisplayedGemShape().getBodyShape();
    }
    
    /**
     * Returns the displayed output part.
     * @return DisplayedPartOutput
     */
    public final DisplayedPartOutput getDisplayedOutputPart() {
        return displayedOutputPart;
    }    

    /**
     * Returns the displayed input part.
     * @param n int the index of the displayed input to return
     * @return DisplayedPartInput
     */
    public final DisplayedPartInput getDisplayedInputPart(int n) {
        return displayedInputParts[n];
    }
    
    /**
     * Get the displayed body part.
     * @return DisplayedPartBody the body part
     */
    public final DisplayedPartBody getDisplayedBodyPart() {
        return displayedBodyPart;       
    }

    /**
     * Get the connectable displayed parts on this gem.
     * 
     * @return List the list of connectable parts on this gem.
     * The order is: input parts first (in order), then output part.
     */
    public final List<DisplayedPartConnectable> getConnectableDisplayedParts() {
        List<DisplayedPartConnectable> connectableParts = new ArrayList<DisplayedPartConnectable>();

        if (displayedInputParts != null) {
            connectableParts.addAll(Arrays.asList(displayedInputParts));
        }

        if (displayedOutputPart != null) {
            connectableParts.add(displayedOutputPart);
        }
        return connectableParts;
    }

    /**
     * Get the number of displayed inputs on this gem.
     * 
     * @return int the number of displayed part in this displayed gem
     */
    final int getNDisplayedArguments() {
        return displayedInputParts == null ? 0 : displayedInputParts.length;
    }

    /**
     * Update the displayed inputs on a displayed gem to reflect any input changes in the underlying gem.
     */
    void updateDisplayedInputs() {
        int nArgs = gem.getNInputs();

        // create a map from input to displayed input for the current displayed inputs
        Map<PartInput, DisplayedPartInput> inputDisplayMap = new HashMap<PartInput, DisplayedPartInput>();
        int nDisplayedArgs = displayedInputParts.length;
        for (int i = 0; i < nDisplayedArgs; i++) {
            DisplayedPartInput dInput = displayedInputParts[i];
            inputDisplayMap.put(dInput.getPartInput(), dInput);
        }

        // create an array for the new displayed inputs
        DisplayedPartInput[] newDisplayedInputs = new DisplayedPartInput[nArgs];
        
        // update the new displayed inputs based on the updated gem model
        for (int i = 0; i < nArgs; i++) {
            Gem.PartInput input = gem.getInputPart(i);
            DisplayedPartInput dInput = inputDisplayMap.get(input);
            
            // A new input - must create a new displayed input
            if (dInput == null) {
                dInput = new DisplayedPartInput(input);
            }

            // update the corresponding new displayed input
            newDisplayedInputs[i] = dInput;
        }
        
        // switch the parts over
        displayedInputParts = newDisplayedInputs;
    }

    /**
     * Set the location of this Gem
     * @param newXY Point the new location
     */
    public void setLocation(Point newXY) {
        // Set the location and delete the cached bounds
        if (!location.equals(newXY)) {
            location = new Point(newXY);

            Rectangle oldBounds = getBounds();
            getDisplayedGemShape().purgeCachedBounds();

            // notify listeners
            if (gemLocationListener != null) {
                gemLocationListener.gemLocationChanged(new DisplayedGemLocationEvent(DisplayedGem.this, oldBounds));
            }
        }
    }

    /**
     * Get the location of this Gem
     * @return Point
     */
    public final Point getLocation() {
        // Get the location and return
        return new Point(location);
    }
    
    /**
     * Get the weighted center point of this Gem's body.
     * In mathematics, this is known as the body shape's "centroid".
     * @return the centroid of the gem body.
     */
    public final Point2D getCenterPoint() {
        return getDisplayedGemShape().getCenterPoint();
    }

    /**
     * Is any part of this DisplayedGem under the given point.
     * @param xy Point the point to test for
     * @return boolean whether we hit
     */
    final boolean anyHit(Point xy) {
        return whatHit(xy) != null;
    }

    /**
     * Determine what part of this Gem has been hit
     * @param xy Point the point to test for
     * @return PartConnectable what we hit or null for nothing
     */
    final DisplayedPart whatHit(Point xy) {
        // Test for body hit
        DisplayedGemShape shape = getDisplayedGemShape();
        if (shape.bodyHit(xy)) {
            return getDisplayedBodyPart();
        }

        // Test for output arrow hit
        if (shape.outputHit(xy)) {
            return getDisplayedOutputPart();
        }
        // Test for input blob hit
        int blobNum = shape.inputHit(xy);
        if (blobNum >= 0) {
            return getDisplayedInputPart(blobNum);
        }

        // Nothing was hit
        return null;
    }    

    /**
     * Return the Gem's bounds.
     * @return the bounds of the Gem
     */
    public final Rectangle getBounds() {
        return getDisplayedGemShape().getBounds();
    }

    /**
     * Handle a gem size change.
     */
    void sizeChanged() {
        Rectangle oldBounds = getBounds();
        
        // Null out the displayed gem shape reference.  It will be instantiated the next time it is needed (with fresh bounds..).
        displayedGemShape = null;

        // notify listeners
        if (gemSizeListener != null) {
            gemSizeListener.gemSizeChanged(new DisplayedGemSizeEvent(DisplayedGem.this, oldBounds));
        }
    }    

    /**
     * The textual representation of the gem used for display.
     * @return the String to display
     */
    public final String getDisplayText() {
        if (gem instanceof CodeGem) {
            return ((CodeGem)gem).getUnqualifiedName();

        } else if (gem instanceof CollectorGem) {
            return ((CollectorGem)gem).getUnqualifiedName();

        } else if (gem instanceof RecordFieldSelectionGem) {
            return ((RecordFieldSelectionGem)gem).getDisplayedText();

        } else if (gem instanceof RecordCreationGem) {
            return ((RecordCreationGem)gem).getDisplayName();

        } else if (gem instanceof FunctionalAgentGem) {
            ModuleTypeInfo moduleTypeInfo = displayContext.getContextModuleTypeInfo();
            ScopedEntityNamingPolicy namingPolicy = moduleTypeInfo == null ? 
                                                    namingPolicy = ScopedEntityNamingPolicy.FULLY_QUALIFIED :
                                                    new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(moduleTypeInfo);
            return ((FunctionalAgentGem)gem).getGemEntity().getAdaptedName(namingPolicy);      
        
        } else if (gem instanceof ReflectorGem) {
            return ((ReflectorGem)gem).getUnqualifiedName();
        
        } else if (gem instanceof ValueGem) {
            return GemCutter.getResourceString("ValueGemDisplayText");
        
        } else {
            throw new IllegalArgumentException("Unknown gem class: " + this.getClass());
        }
        
    }

    /**
     * Describe this Gem
     * @return the description
     */
    @Override
    public final String toString() {
        return "DisplayedGem.  Location: (" + location + ").  Gem: " + gem;
    }   

    /*
     * Methods implementing Graph.Node ********************************************************************
     */

    /**
     * Return the degree of this gem. i.e. how many connections it supports.
     * @return double
     */
    public final double degree() {
        return getConnectableDisplayedParts().size();
    }       

    /**
     * Get an iterator over the incoming edges.
     * @param validNodes the list of nodes that you would count as incoming edges
     *                   this is used for selective graph traversal
     * @return Iterator
     */
    public final Iterator<Graph.Edge> getInEdges(List<Graph.Node> validNodes) {
        int nDisplayedArgs = getNDisplayedArguments();
        if (nDisplayedArgs == 0) {
            return EmptyIterator.emptyIterator();
        }
        
        List<Graph.Edge> inEdgeList = new ArrayList<Graph.Edge>();
        for (int i = 0; i < nDisplayedArgs; ++i) {
            DisplayedConnection dConn = getDisplayedInputPart(i).getDisplayedConnection();
            if (dConn != null) {
                // We only count the edge if the node is valid
                if (validNodes.contains(dConn.getSourceNode())) {
                    inEdgeList.add (dConn);
                }
            }
        }
        
        return inEdgeList.iterator();
    }

    /**
     * Get an iterator over the outgoing edges.
     * @param validNodes the list of nodes that you want to count
     * @return Iterator
     */
    public final Iterator<Graph.Edge> getOutEdges(List<Graph.Node> validNodes) {
        
        // get the output part
        DisplayedGem.DisplayedPartOutput partOutput = getDisplayedOutputPart();
        
        // get the connection. If there is no connection or no partOutput, then there are no out edges
        DisplayedConnection displayedConnection = partOutput == null ? null : partOutput.getDisplayedConnection();
        if (displayedConnection == null) {
            return EmptyIterator.emptyIterator();
        }
        
        Graph.Node node = displayedConnection.getTargetNode();
        
        // if the node is not valid, then we don't count it
        if (!validNodes.contains(node)) {
            return EmptyIterator.emptyIterator();
        }

        List<Graph.Edge> outEdgeList = new ArrayList<Graph.Edge>();
        outEdgeList.add (getDisplayedOutputPart().getDisplayedConnection());
        return outEdgeList.iterator();
    }

    /*
     * Methods implementing Target     ****************************************************************
     */

    /**
     * @return a target corresponding to this DisplayedGem.
     */
    public Target getTarget() {
        
        // if this gem is a Collector Gem then use its name else use the general name
        String targetName = (gem instanceof CollectorGem) ? ((CollectorGem)gem).getUnqualifiedName() : CALRunner.TEST_SC_NAME;

        return new Target(targetName) {
            @Override
            protected SourceModel.FunctionDefn getSCDefn(String scName) {
                // Build the new SC definition and return
                return CALSourceGenerator.getFunctionSourceModel(scName, gem, Scope.PRIVATE);
            }
        };
    }

    /**
     * Return the arguments as would be required by current definition of the Gem tree at the Target.
     * @return the list of arguments required by the target.
     */
    public final List<PartInput> getTargetArguments() {
        return gem.getTargetInputs();
    }

    /*
     * Methods supporting XMLPersistable                 ********************************************
     */

    /**
     * Attach the saved form of this object as a child XML node.
     * @param parentNode Node the node that will be the parent of the generated XML.  
     *   The generated XML will be appended as a subtree of this node.  
     * Note: parentNode must be a node type that can accept children (eg. an Element or a DocumentFragment)
     * @param gemContext the context in which the gem is saved.
     */
    public void saveXML(Node parentNode, GemContext gemContext) {
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the displayed gem element
        Element resultElement = document.createElement(GemPersistenceConstants.DISPLAYED_GEM_TAG);
        parentNode.appendChild(resultElement);

        // Add an element for the info for the underlying gem.
        gem.saveXML(resultElement, gemContext);

        // Add a child location element
        Element locationElement = GemCutterPersistenceHelper.pointToElement(getLocation(), document);
        resultElement.appendChild(locationElement);
    }

    /*
     * Methods supporting undo.StateEditable ********************************************
     */

    /**
     * Restore the stored displayed input state.
     * @param state Hashtable the stored state
     */
    void restoreDisplayedInputState(Hashtable<Pair<DisplayedGem, String>, DisplayedPartInput[]> state) {

        DisplayedPartInput[] displayedInputParts = 
            state.get(new Pair<DisplayedGem, String>(DisplayedGem.this, DISPLAYED_INPUT_PARTS_KEY));

        if (displayedInputParts != null) {
            this.displayedInputParts = displayedInputParts.clone();
        }
    }

    /**
     * Save the displayed input state.
     * @param state Hashtable the table in which to store the displayed input state
     */
    void storeDisplayedInputState(Hashtable<Pair<DisplayedGem, String>, DisplayedPartInput[]> state) {

        state.put(new Pair<DisplayedGem, String>(DisplayedGem.this, DISPLAYED_INPUT_PARTS_KEY), displayedInputParts.clone());
    }

    /*
     * Methods to handle listeners     ****************************************************************
     */

    /**
     * Adds the specified location change listener to receive location change events from this gem .
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the location listener.
     */
    public synchronized void addLocationChangeListener(DisplayedGemLocationListener l) {
        if (l == null) {
            return;
        }
        gemLocationListener = GemEventMulticaster.add(gemLocationListener, l);
    }

    /**
     * Removes the specified location change listener so that it no longer receives location change events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the location listener.
     */
    public synchronized void removeLocationChangeListener(DisplayedGemLocationListener  l) {
        if (l == null) {
            return;
        }
        gemLocationListener = GemEventMulticaster.remove(gemLocationListener, l);
    }

    /**
     * Adds the specified size change listener to receive size change events from this gem .
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the size listener.
     */
    public synchronized void addSizeChangeListener(DisplayedGemSizeListener l) {
        if (l == null) {
            return;
        }
        gemSizeListener = GemEventMulticaster.add(gemSizeListener, l);
    }

    /**
     * Removes the specified size change listener so that it no longer receives size change events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the size listener.
     */
    public synchronized void removeSizeChangeListener(DisplayedGemSizeListener  l) {
        if (l == null) {
            return;
        }
        gemSizeListener = GemEventMulticaster.remove(gemSizeListener, l);
    }

    /**
     * A part of a Gem
     * @author Edward Lam
     */
    public abstract class DisplayedPart {

        /**
         * Default constructor for a DisplayedPart
         */
        public DisplayedPart() {
        }

        /**
         * Get the gem to which this displayed part belongs.
         * @return Gem the gem to which this displayed part belongs
         */
        public Gem getGem() {
            return gem;
        }

        /**
         * Get the displayed gem of which this is part
         * @return DisplayedGem the displayed gem to which this displayed part belongs
         */
        public DisplayedGem getDisplayedGem() {
            return DisplayedGem.this;
        }

        /**
         * Get the bounds of this part
         * @return Rectangle the bounds
         */
        public abstract Rectangle getBounds();
    }

    /**
     * A connectable PartConnectable has both a type and a bind point for a Connector.
     * @author Edward Lam
     */
    public abstract class DisplayedPartConnectable extends DisplayedPart {

        /** the PartConnectable represented by this DisplayedPartConnectable */
        private final Gem.PartConnectable partConnectable;

        /** the displayed connection associated with this connectable part*/
        private DisplayedConnection dConnection;

        /**
         * Default constructor for a DisplayedPartConnectable
         */
        DisplayedPartConnectable(Gem.PartConnectable partConnectable) {
            super();
            this.partConnectable = partConnectable;
        }

        /**
         * Get the connectable part.
         * @return Gem.PartConnectable the part to which this displayed part belongs
         */
        public final Gem.PartConnectable getPartConnectable() {
            return partConnectable;
        }

        /**
         * Get the connection point for this part
         * @return the point at which components will connect
         */
        public abstract Point getConnectionPoint();

        /**
         * Repaint this PartConnectable's connection (if it has one!)
         * @param c JComponent the component to paint on  
         */
        public void repaintConnection(JComponent c) {
            if (isConnected()) {
                c.repaint(dConnection.getBounds());
            }
        }
        
        /**
         * Bind a connection to this part.
         * @param dConn DisplayedConnection the connection to connect 
         */
        public void bindDisplayedConnection(DisplayedConnection dConn) {
            dConnection = dConn;
        }   

        /**
         * Get the displayed connection at this part.
         * @return conn DisplayedConnection the connection bound to this part
         */
        public final DisplayedConnection getDisplayedConnection() {
            return dConnection;
        }   

        /**
         * Check if this part is connected.
         * @return boolean whether it is already connected
         */
        public final boolean isConnected() {
            return (dConnection != null);
        }
        
        /**
         * Purge this PartConnectable's connection route (if it has one)
         */
        public void purgeConnectionRoute() {
            if (dConnection != null) {
                dConnection.purgeRoute();
            }
        }

        /**
         * Get the source of this edge.
         * @return Graph.Node
         */
        public final Graph.Node getSourceNode() {
            if (isConnected()) {
                return dConnection.getSourceNode();
            }
            
            return null;
        }

        /**
         * Get the destination of this edge.
         * @return Graph.Node
         */
        public final Graph.Node getTargetNode() {
            if (isConnected()) {
                return dConnection.getTargetNode();
            }
            
            return null;
        }
    }

    /**
     * The PartConnectable representing the body of the Gem
     * @author Edward Lam
     */
    public class DisplayedPartBody extends DisplayedPart {

        /**
         * Default constructor for a DisplayedPartBody
         */
        private DisplayedPartBody() {
        }

        /**
         * Get the bounds of this part
         * @return Rectangle the bounds
         */
        @Override
        public final Rectangle getBounds() {
            return getDisplayedGemShape().getBodyBounds();
        }
    }

    /**
     * A PartInput is a connectable part which is a sink (destination).
     * @author Edward Lam
     */
    public class DisplayedPartInput extends DisplayedPartConnectable {
        
        /**
         * Default constructor for a DisplayedPartInput
         * @param partInput the input corresponding to this displayed input.
         */
        public DisplayedPartInput(Gem.PartInput partInput) {
            super(partInput);
        }

        /**
         * Get the input part.
         * @return Gem.PartInput the part to which this displayed part belongs
         */
        public final Gem.PartInput getPartInput() {
            return (Gem.PartInput)getPartConnectable();
        }

        /**
         * Get the connection point for this part
         * @return the point at which components will connect
         */
        @Override
        public Point getConnectionPoint() {
            return getDisplayedGemShape().getInputConnectPoint(getPartInput().getInputNum());
        }

        /**
         * Get the bounds of this part foo.
         * @return Rectangle the bounds
         */
        @Override
        public Rectangle getBounds() {
            return getDisplayedGemShape().getInBounds();
        }
    }

    /**
     * A PartOutput is a connectable part which is a source/output.
     * @author Edward Lam
     */
    public class DisplayedPartOutput extends DisplayedPartConnectable {

        /**
         * Default constructor for a DisplayedPartOutput
         * @param partOutput the output corresponding to this displayed output.
         */
        DisplayedPartOutput(Gem.PartOutput partOutput) {
            super(partOutput);
        }

        /**
         * Get the output part.
         * @return Gem.PartOutput the part to which this displayed part belongs
         */
        public final Gem.PartOutput getPartOutput() {
            return (Gem.PartOutput)getPartConnectable();
        }

        /**
         * Get the connection point for this part
         * @return the point at which components will connect
         */
        @Override
        public Point getConnectionPoint() {
            return getDisplayedGemShape().getOutputConnectPoint();
        }

        /**
         * Get the bounds of this part
         * @return Rectangle the bounds
         */
        @Override
        public Rectangle getBounds() {
            return getDisplayedGemShape().getOutBounds();
        }
    }
}


