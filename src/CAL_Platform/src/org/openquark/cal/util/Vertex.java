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
 * Vertex.java
 * Created: Aug 24, 2000
 * By: Bo Ilic
 */
package org.openquark.cal.util;

/**
 * A class representing a named vertex in a directed graph. Includes some helper member fields which
 * are useful in various graph algorithms.
 * <p>
 * The type parameter <code>T</code> this is the type of the data (usually a name like a function name or
 * a module name) encapsulated by the graph vertex.
 *
 * Creation date: (8/24/00 11:50:24 AM)
 * @author Bo Ilic
 */
public class Vertex<T> {

    /** whether the vertex has been visited during a traversal */
    private boolean isVisited;

    /** an index assigned to this vertex during a depth first traversal of the graph */
    private int depthFirstNumber;

    /** a list of the vertices which are connected to this vertex by a single arc */
    private VertexList<T> adjacentVertices;

    /** a unique identifying name for the vertex */
    private final T name;

    /** identifies the strongly connected component that the vertex belongs to */
    private int componentNumber;
    
    /**
     * Constructs a vertex as a member-wise copy of another vertex.
     * Creation date: (8/24/00 5:07:16 PM)
     * @param otherVertex
     */
    public Vertex(Vertex<T> otherVertex) {
        adjacentVertices = otherVertex.adjacentVertices;
        componentNumber = otherVertex.componentNumber;
        depthFirstNumber = otherVertex.depthFirstNumber;
        isVisited = otherVertex.isVisited;
        name = otherVertex.name;
    }
    
    /**
     * Constructs a vertex with a given identifying name. The adjacentVertices need to be set later.
     * Creation date: (8/28/00 4:30:16 PM)
     * @param name
     */
    public Vertex(T name) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        adjacentVertices = null;
        componentNumber = 0;
        depthFirstNumber = 0;
        isVisited = false;
    }
    
    /**   
     * @return a list of the vertices which are connected to this vertex by a single arc.
     */
    public VertexList<T> getAdjacentVertices() {
        return adjacentVertices;
    }
    
    /**    
     * @return T a unique identifying name for the vertex
     */
    public T getName() {
        return name;
    }    
    
    /**    
     * @return the strongly connected component that the vertex belongs to
     */
    int getComponentNumber() {
        return componentNumber;
    }
    
    /**    
     * @return an index assigned to this vertex during a depth first traversal of the graph
     */
    int getDepthFirstNumber() {
        return depthFirstNumber;
    }
    
    /**     
     * @return whether the vertex has been visited during a traversal
     */
    boolean getIsVisited() {
        return isVisited;
    }
      
       
    void setAdjacentVertices(VertexList<T> newAdjacentVertices) {
        adjacentVertices = newAdjacentVertices;
    }
       
    void setComponentNumber(int newComponentNumber) {
        componentNumber = newComponentNumber;
    }
        
    void setDepthFirstNumber(int newDepthFirstNumber) {
        depthFirstNumber = newDepthFirstNumber;
    }
       
    void setIsVisited(boolean newIsVisited) {
        isVisited = newIsVisited;
    }
        
    /**
     * Creates a textual representation of the vertex of the form:
     * componentNumber depthFirstNumber vertexName {adjacentVertexName1,... adjacentVertexNameN}
     *
     *  * Note: the curly brackets are used to indicate that the dependees are unordered.
     * 
     * Creation date: (8/29/00 11:45:44 AM)
     * @return String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(Integer.valueOf(componentNumber).toString());
        sb.append(' ');
        sb.append(Integer.valueOf(depthFirstNumber).toString());
        sb.append(' ');
        sb.append(name);
        sb.append(" {");
        for (int i = 0, nAdjacent = adjacentVertices.size(); i < nAdjacent; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(adjacentVertices.get(i).getName());
        }
        sb.append('}');

        return sb.toString();
    }
}