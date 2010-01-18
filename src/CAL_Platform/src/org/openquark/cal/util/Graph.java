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
 * Graph.java
 * Creation date: (August 24 2000 12:12:22 PM)
 * By: Bo Ilic
 */
package org.openquark.cal.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class used to represent a directed Graph. The class has several methods that implement useful
 * graph algorithms such as calculateStronglyConnectedComponents.
 * <p>
 * The type parameter <code>T</code> this is the type of the data (usually a name like a function name or
 * a module name) encapsulated by the graph vertices.
 *
 * Creation date: (8/24/00 12:12:22 PM)
 * @author Bo Ilic
 */

public final class Graph<T> implements Cloneable {
    
    /** list of all the vertices in the graph */
    private final VertexList<T> vertexList;

    /** a helper member used during depth first traversal vertex numbering */
    private int depthFirstNumber;

    /** list of all the strongly connected components in the graph */
    private List<Component> componentList;

    /**
     * Helper class to efficiently access the strongly connected components of the Graph
     */
    public final class Component {

        //the component refers to indices [startIndex, endIndex) within a VertexList
        private final int startIndex;
        private final int endIndex;
        private final boolean cyclic;

        /**
         * Construct a Component    
         */
        public Component(int startIndex, int endIndex, boolean cyclic) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.cyclic = cyclic;
        }

        /**.
         * Returns the size of this Component
         */
        public final int size() {
            return endIndex - startIndex;
        }

        /**.
         * Returns true if this Component has more than one vertex in it, or if the single
         * vertex has an arc to itself.
         */
        public final boolean isCyclic() {
            return cyclic;
        }

        /**.
         * Returns the ith vertex in this Component
         */
        public final Vertex<T> getVertex(int i) {
            if (i < 0 || i >= size()) {
                throw new IndexOutOfBoundsException();
            }
            return vertexList.get(startIndex + i);
        }
        
        @Override
        public String toString() {
            
            StringBuilder result = new StringBuilder("[");
            
            for (int i = 0, size = size(); i < size; ++ i) {
                if (i > 0) {
                    result.append(", ");                    
                }
                result.append(getVertex(i).getName());
            }
            result.append("]");
            
            return result.toString();
        }
    }
    
    /**
     * Constructs a graph from the vertexBuilderList initialization information. Note that
     * vertexBuilderList is assumed to determine a well formed graph.
     * What this means is:
     * a. scName is different for each VertexBuilder object in the list.
     * b. if a name occurs in the dependeeNames list of a VertexBuilder object, then it also occurs as
     *    a scName for some VertexBuilder object in vertexBuilderList.
     * c. the dependeeNames set of a VertexBuilder object does not have duplicated names.
     *
     * Creation date: (8/28/00 4:56:01 PM)
     * @param vertexBuilderList VertexBuilderList
     */
    public Graph(VertexBuilderList<T> vertexBuilderList) {
        vertexList = new VertexList<T>();
        Map<T, Vertex<T>> nameToVertexMap = new HashMap<T, Vertex<T>>();

        int nFunctions = vertexBuilderList.size();

        for (int i = 0; i < nFunctions; ++i) {
            VertexBuilder<T> vb = vertexBuilderList.get(i);
            T vertexName = vb.getName();
            Vertex<T> v = new Vertex<T>(vertexName);
            vertexList.add(v);
            nameToVertexMap.put(vertexName, v);
        }

        for (int i = 0; i < nFunctions; ++i) {

            VertexBuilder<T> vb = vertexBuilderList.get(i);
            Vertex<T> sourceVertex = nameToVertexMap.get(vb.getName());
            VertexList<T> adjacentVertices = new VertexList<T>();

            
            for (final T dependeeName : vb.getDependeeNames()) {
                Vertex<T> o = nameToVertexMap.get(dependeeName);
                if (o != null) {
                    adjacentVertices.add(o);
                }
            }

            sourceVertex.setAdjacentVertices(adjacentVertices);
        }
    }

   
    /**
     * Creates a new graph, making a copy of vertexList.
     *
     * @param vertexListToCopy
     */
    private Graph(VertexList<T> vertexListToCopy) {

        vertexList = new VertexList<T>();
        Map<Vertex<T>, Vertex<T>> map = new HashMap<Vertex<T>, Vertex<T>>();

        int nVertices = vertexListToCopy.size();

        for (int i = 0; i < nVertices; ++i) {

            Vertex<T> v1 = vertexListToCopy.get(i);
            Vertex<T> v2 = new Vertex<T>(v1);
            v2.setAdjacentVertices(null);
            vertexList.add(v2);
            map.put(v1, v2);
        }

        // Fix up the adjacent vertices in the copy.

        for (int i = 0; i < nVertices; ++i) {
            VertexList<T> arcs1 = vertexListToCopy.get(i).getAdjacentVertices();
            VertexList<T> arcs2 = new VertexList<T>();

            for (int j = 0, nArcs = arcs1.size(); j < nArcs; ++j) {
                Vertex<T> arc = map.get(arcs1.get(j));
                if (arc != null) {
                    arcs2.add(arc);
                }
            }

            vertexList.get(i).setAdjacentVertices(arcs2);
        }

    }
    
    /**
     * Returns a copy of the graph with vertices in the same strongly connected component listed
     * sequentially, and the strongly connected components ordered topologically.
     *
     * Two vertices v1 and v2 are in the same strongly connected component if there is a path from
     * v1 to v2 and from v2 to v1. Topological ordering means that if v1 and v2 are not in the
     * same strongly connected component, and there is an arc from v1 to v2, then v1 is to the right
     * of v2 in the list of vertices.
     *
     * The algorithm used is from
     * Data Structures and Algorithms, Alfred Aho, John Hopcroft, Jeffrey Ullman, 1983 pg 222-226.
     *
     * Creation date: (8/24/00 6:14:26 PM)
     * @return stronglyconnectedcomponents.Graph
     */
    public final Graph<T> calculateStronglyConnectedComponents() {

        depthFirstSearchNumbering();

        Graph<T> g = reverse();

        {
            final Comparator<Vertex<T>> c = new Comparator<Vertex<T>>() {
    
                public int compare(Vertex<T> vertex1, Vertex<T> vertex2) {
                    final int n1 = vertex1.getDepthFirstNumber();
                    final int n2 = vertex2.getDepthFirstNumber();
    
                    if (n1 > n2) {
                        return -1;
                    } else if (n1 < n2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
    
            // Sort the vertex list in descending order of depth first number        
            java.util.Collections.sort(g.vertexList, c);
        }

        int nComponents = g.markComponents();

        // reflect the component numbers so they correspond to the order of declaration for
        // functions. For example, I x = x; main = I 2. Then I should have component #1
        // and main have component #2.

        for (int i = 0, nVertices = g.vertexList.size(); i < nVertices; ++i) {
            Vertex<T> v = g.vertexList.get(i);
            v.setComponentNumber(nComponents - v.getComponentNumber() + 1);
        }

        // For simple examples, the vertices appear to be ordered sequentially by component
        // number, but this is not true in general. Thus we need to sort them again.

        {
            final Comparator<Vertex<T>> c = new Comparator<Vertex<T>>() {
    
                public int compare(Vertex<T> vertex1, Vertex<T> vertex2) {
                    final int n1 = vertex1.getComponentNumber();
                    final int n2 = vertex2.getComponentNumber();
    
                    if (n1 < n2) {
                        return -1;
                    } else if (n1 > n2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
    
            java.util.Collections.sort(g.vertexList, c);
        }

        g.makeComponentList();

        return g;
    }
    
    /**
     * Clones the graph.
     *
     * Creation date: (8/24/00 5:11:17 PM)
     * @return Object
     */
    @Override
    public Graph<T> clone() {

        Graph<T> g = new Graph<T>(vertexList);
        g.depthFirstNumber = depthFirstNumber;

        return g;
    }
    
    /**
     * Number the vertices of the graph by their order of visitation in a depth
     * first search.
     *
     * Creation date: (8/24/00 12:19:02 PM)
     */
    public final void depthFirstSearchNumbering() {

        int nVertices = vertexList.size();

        for (int i = 0; i < nVertices; ++i) {
            vertexList.get(i).setIsVisited(false);
        }

        depthFirstNumber = 0;

        for (int i = 0; i < nVertices; ++i) {
            Vertex<T> v = vertexList.get(i);
            if (!v.getIsVisited()) {
                depthFirstSearchNumbering(v);
            }
        }

    }
    
    /**
     * A helper method for doing the depth first search numbering of the graph.
     *
     * Creation date: (8/24/00 12:31:02 PM)
     * @param v stronglyconnectedcomponents.Vertex
     */
    private final void depthFirstSearchNumbering(Vertex<T> v) {

        v.setIsVisited(true);

        VertexList<T> adjacentVertices = v.getAdjacentVertices();

        for (int i = 0, nAdjacent = adjacentVertices.size(); i < nAdjacent; ++i) {
            Vertex<T> adjacent = adjacentVertices.get(i);

            if (!adjacent.getIsVisited()) {
                depthFirstSearchNumbering(adjacent);
            }
        }

        ++depthFirstNumber;
        v.setDepthFirstNumber(depthFirstNumber);
    }
    
    /**
     * Get the names of vertices dependent (directly or indirectly) on a given vertex.
     * @param vertexName the name of the vertex for which to find dependents.
     * @return (Set of String) the set of names of vertices dependent on the given vertex.  This set includes vertexName itself.
     */
    public final Set<T> getDependentVertexNames(T vertexName) {

        // Get the vertex corresponding to vertexName.
        Vertex<T> dependeeVertex = null;
        for (int i = 0, nVertices = vertexList.size(); i < nVertices; ++i) {
            Vertex<T> vertex = vertexList.get(i);
           
            if (vertex.getName().equals(vertexName)) {
                dependeeVertex = vertex;
                break;
            }
        }

        if (dependeeVertex == null) {
            throw new IllegalArgumentException("Could not find vertex named " + vertexName);
        }
        
        return getDependentNamesHelper(dependeeVertex, new HashSet<T>());
    }
    
    /**
     * Helper method for getDependentVertexNames
     * @param dependeeVertex
     * @param dependentNamesSet (Set of String) the set of names of dependents collected so far
     * @return the dependentNamesSet after the method is done with it
     */
    private final Set<T> getDependentNamesHelper(Vertex<T> dependeeVertex, Set<T> dependentNamesSet) {

        T vertexName = dependeeVertex.getName();
        if (!dependentNamesSet.add(vertexName)) {
            return dependentNamesSet;
        }
        
        VertexList<T> adjacentVertices = dependeeVertex.getAdjacentVertices();

        for (int i = 0, nAdjacent = adjacentVertices.size(); i < nAdjacent; ++i) {
            Vertex<T> adjacentVertex = adjacentVertices.get(i);
            getDependentNamesHelper(adjacentVertex, dependentNamesSet);
        }

        return dependentNamesSet;
    }
    
    /**
     * Returns the number of strongly connected components in this graph. Assumes that
     * calculateStronglyConnectedComponents has been called since the last graph update.
     * Creation date: (1/18/01 4:15:45 PM)
     * @return int number of strongly connected components in this graph
     */
    public final int getNStronglyConnectedComponents() {
        int nVertices = vertexList.size();
        if (nVertices == 0) {
            return 0;
        }

        return vertexList.get(nVertices - 1).getComponentNumber();
    }
    
    /**
     * Zero indexed accessor to a component.
     * Creation date: (1/18/01 5:22:49 PM)
     * @return Component 
     * @param componentN int
     */
    public final Component getStronglyConnectedComponent(int componentN) {
        return componentList.get(componentN);
    }
    
    /**
     * Returns a list of the vertices in this graph.
     *
     * Creation date: (9/1/00 9:45:57 AM)
     * @return VertexList
     */
    public final VertexList<T> getVertexList() {
        return vertexList;
    }
    
    /**
     * Some test cases for debugging Graph and its supporting classes.
     * Creation date: (8/28/00 4:26:18 PM)
     * @param args
     */
    public static void main(String[] args) {

        VertexBuilderList<String> v1 = new VertexBuilderList<String>();
        v1.add(new VertexBuilder<String>("a", new String[] { "b" }));
        v1.add(new VertexBuilder<String>("b", new String[] { "c" }));
        v1.add(new VertexBuilder<String>("c", new String[] { "a" }));
        v1.add(new VertexBuilder<String>("d", new String[] { "a" }));
        v1.add(new VertexBuilder<String>("e", new String[] { "c" }));
        // a is a duplicate. It is removed when the VertexBuilder object is constructed.
        v1.add(new VertexBuilder<String>("f", new String[] { "a", "g", "h", "a" }));
        v1.add(new VertexBuilder<String>("g", new String[] { "b", "f" }));
        // h has an arc to itself
        v1.add(new VertexBuilder<String>("h", new String[] { "d", "h" }));

        System.out.println(v1.makesValidGraph() ? "Input forms a valid graph\n" : "Input does not form a valid graph\n");
        Graph<String> g1 = new Graph<String>(v1);
        g1.depthFirstSearchNumbering();
        System.out.println(g1.toString());
        System.out.println(g1.reverse().toString());
        System.out.println(g1.calculateStronglyConnectedComponents().toString());

    }
    
    /**
     * Initializes the componentList member variable to allow easy and efficient access to the strongly connected
     * connected components of this Graph.
     * Creation date: (1/18/01 5:00:22 PM)
     */
    private final void makeComponentList() {

        int nVertices = vertexList.size();
        int nComponents = getNStronglyConnectedComponents();
        componentList = new ArrayList<Component>(nComponents);

        int startComponentIndex = 0;
        int nextStartComponentIndex = 0;

        for (int i = 0; i < nComponents; ++i) {

            // component number i + 1 has indices [startComponentIndex, nextStartComponentIndex)

            startComponentIndex = nextStartComponentIndex;
            nextStartComponentIndex = startComponentIndex;

            while (nextStartComponentIndex < nVertices && i + 1 == vertexList.get(nextStartComponentIndex).getComponentNumber()) {

                ++nextStartComponentIndex;
            }

            // components of size 1 that do not depend upon themselves are not cyclic

            Vertex<T> v = vertexList.get(startComponentIndex);
            boolean isCyclic = nextStartComponentIndex - startComponentIndex > 1 || v.getAdjacentVertices().contains(v);

            componentList.add(new Component(startComponentIndex, nextStartComponentIndex, isCyclic));
        }
    }
    
    /**
     * Marks the components of the reversed graph with their strongly connected component number.
     * @return the number of components.
     */
    private final int markComponents() {

        int nVertices = vertexList.size();

        for (int i = 0; i < nVertices; ++i) {
            vertexList.get(i).setIsVisited(false);
        }

        int componentNumber = 0;

        for (int i = 0; i < nVertices; ++i) {
            Vertex<T> v = vertexList.get(i);
            if (!v.getIsVisited()) {
                ++componentNumber;
                markComponents(v, componentNumber);
            }
        }
        
        return componentNumber;
    }
    
    /**
     * A helper function for markComponents ().
     *
     * @param v a vertex in the component.
     * @param componentNumber the component number with which to mark this component.
     */
    private final void markComponents(Vertex<T> v, int componentNumber) {

        v.setIsVisited(true);

        VertexList<T> adjacentVertices = v.getAdjacentVertices();

        for (int i = 0, nAdjacent = adjacentVertices.size(); i < nAdjacent; ++i) {
            Vertex<T> adjacent = adjacentVertices.get(i);

            if (!adjacent.getIsVisited()) {
                markComponents(adjacent, componentNumber);
            }
        }

        v.setComponentNumber(componentNumber);
    }
    
    /**
     * Returns the graph in which all arcs in the original graph have been reversed.
     *
     * Creation date: (8/24/00 12:53:22 PM)
     * @return Graph
     */
    public final Graph<T> reverse() {

        Graph<T> g = this.clone();

        int nVertices = vertexList.size();

        Map<Vertex<T>, Vertex<T>> map = new HashMap<Vertex<T>, Vertex<T>>();

        for (int i = 0; i < nVertices; ++i) {

            Vertex<T> v1 = vertexList.get(i);
            Vertex<T> v2 = g.vertexList.get(i);
            v2.setAdjacentVertices(new VertexList<T>());
            map.put(v1, v2);
        }

        for (int i = 0; i < nVertices; ++i) {

            Vertex<T> v1 = vertexList.get(i);
            VertexList<T> arcs = v1.getAdjacentVertices();
            for (int j = 0, nArcs = arcs.size(); j < nArcs; ++j) {

                Vertex<T> v2 = map.get(arcs.get(j));
                v2.getAdjacentVertices().add(map.get(v1));
            }
        }

        return g;
    }
    
    /**
     * Provides a string representation of the graph.
     *
     * Creation date: (8/28/00 5:18:27 PM)
     * @return String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0, nVertices = vertexList.size(); i < nVertices; ++i) {

            sb.append(vertexList.get(i).toString());
            sb.append('\n');
        }

        return sb.toString();
    }
}