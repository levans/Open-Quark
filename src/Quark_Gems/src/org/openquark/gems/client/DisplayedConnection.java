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
 * DisplayedConnection.java
 * Creation date: (02/19/02 5:53:35 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * The graphical representation of a Connection.
 * Creation date: (02/19/02 5:53:35 PM)
 * @author Edward Lam
 */
public class DisplayedConnection implements Graph.Edge {

    /** The connection represented by this display object. */
    private final Connection connection;

    /** Displayed connection source part. */
    private final DisplayedGem.DisplayedPartOutput from;
    
    /** Displayed connection destination part. */
    private final DisplayedGem.DisplayedPartInput to;

    /** The route (bunch of points) followed by the displayed connection. */
    private transient ConnectionRoute route;

    /**
     * Construct a Connection from the source and destination parts.
     * Call only after the underlying connection is created.
     * 
     * @param source DisplayedGem.DisplayedPartOutput the source part
     * @param destination DisplayedGem.DisplayedPartInput the destination part
     */
    public DisplayedConnection(DisplayedGem.DisplayedPartOutput source, DisplayedGem.DisplayedPartInput destination) {

        from = source;
        to = destination;
        
        // grab the connection from one of the underlying parts
        connection = from.getPartConnectable().getConnection();
    }

    /**
     * Get the connection represented by this display object
     * @return Connection the connection represented by this display object
     */
    public final Connection getConnection() {
        return connection;
    }

    /**
     * Return the source (from) part.
     * @return DisplayedGem.DisplayedPartOutput the source part
     */
    public final DisplayedGem.DisplayedPartOutput getSource() {
        // Return the source part
        return from;
    }   

    /**
     * Return the destination (to) part.
     * @return DisplayedGem.DisplayedPartInput the destination part
     */
    public final DisplayedGem.DisplayedPartInput getDestination() {
        // Return the destination part
        return to;
    }

    /**
     * Get a ConnectionRoute object describing the route between
     * the source and destination of this Connection
     * @return the ConnectionRoute
     */
    public ConnectionRoute getConnectionRoute() {
        // If we don't have a cached route, we need to build one
        if (route == null) {
            // Just ask the general GemGraph version of this for the ConnectionRoute
            route = TableTop.getConnectionRoute(from, to);

            // Determine the hook size
            int hookSize = DisplayConstants.REVERSE_CONNECTION_HOOK_SIZE;

            hookSize += to.getPartInput().getInputNum() * DisplayConstants.HOOK_STEP_SIZE;  
            
            genConnectionRoute(route, hookSize);
        }
        return route;
    }

    /**
     * Generate a ConnectionRoute 
     * @param route ConnectionRoute the ConnectionRoute object which will be returned as the result
     * @param hookSize int the size of the connector's final hook leg
     * @return ConnectionRoute the ConnectionRoute
     */
    public static ConnectionRoute genConnectionRoute(ConnectionRoute route, int hookSize) {
        if (route != null) {
            // We build a horizontal connectors route for composition connections (out->in)
            // Build the horizontal connectors routing
            route.genSquareRouteForHorizontalConnectors(-hookSize);
        }
        return route;
    }

    /**
     * Purge this route, causing us to re-evaluate it later
     */
    public final void purgeRoute() {
        route = null;
    }    
    
    /**
     * Return the bounding rectangle for this connection.
     * @return Rectangle the bounds
     */
    public Rectangle getBounds() {
        // Currently, we create a temporary route and get its bounds
        return getConnectionRoute().getBoundingRectangle();
    }    

    /*
     * Methods implementing Graph.Edge ****************************************************************
     */
    
    /**
     * Get the source node of this edge.
     * @return Node
     * 
     */
    public Graph.Node getSourceNode() {
        return from.getDisplayedGem();
    }
    
    /**
     * Get the destination node of this edge.
     * @return Node
     */
    public Graph.Node getTargetNode() {
        return to.getDisplayedGem();
    }
    
    /**
     * Get the x/y coordinates of the point where this
     * edge connects to the target.
     * @return Node
     */
    public Point getTargetConnectionPoint() {
        return to.getConnectionPoint();
    }
    
    /**
     * Get the x/y coordinates of the point where this
     * edge connects to the source.
     * @return Node
     */
    public Point getSourceConnectionPoint() {
        return from.getConnectionPoint();
    }
}

