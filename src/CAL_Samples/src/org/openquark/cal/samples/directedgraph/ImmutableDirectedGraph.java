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
 * ImmutableDirectedGraph.java
 * Created: Oct 12, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.samples.directedgraph;

import java.util.List;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.util.Pair;

/**
 * This class represents an immutable directed graph. It is intended to showcase the
 * use of CAL modules via standalone library JARs. The functionality of this class is
 * provided by the underlying CAL module {@code Cal.Utilities.DirectedGraph}, is
 * exposed via the API module {@code Cal.Samples.DirectedGraphLibrary}.
 * 
 * @see org.openquark.cal.samples.directedgraph.DirectedGraphLibrary
 *
 * @author Andrew Casey
 * @author Joseph Wong
 */
public final class ImmutableDirectedGraph<T> {

    /**
     * The underlying CalValue of the graph.
     */
    private final CalValue graph;
    
    /**
     * The execution context associated with the graph.
     */
    private final ExecutionContext executionContext;
    
    /**
     * Constructs an instance with the graph represented as a CalValue.
     * @param graph the underlying graph.
     * @param executionContext the execution context associated with the graph.
     */
    private ImmutableDirectedGraph(final CalValue graph, final ExecutionContext executionContext) {
        if (graph == null || executionContext == null) {
            throw new NullPointerException();
        }
        this.graph = graph;
        this.executionContext = executionContext;
    }
    
    /**
     * @return the underlying CalValue of the graph.
     */
    public CalValue getCalValue() {
        return graph;
    }

    /**
     * @return the execution context associated with the graph.
     */
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * Factory method.
     * @param graph the underlying graph.
     * @return a new instance with the given graph and the existing execution context. 
     */
    private <V> ImmutableDirectedGraph<V> makeUpdated(final CalValue graph) {
        return new ImmutableDirectedGraph<V>(graph, executionContext);
    }
    
    /**
     * Factory method.
     * @param graph the underlying graph.
     * @param executionContext the execution context associated with the graph.
     * @return a new instance with the given graph and the existing execution context. 
     */
    private static <V> ImmutableDirectedGraph<V> make(final CalValue graph, final ExecutionContext executionContext) {
        return new ImmutableDirectedGraph<V>(graph, executionContext);
    }
    
    /**
     * Checks that the two graphs are constructed using the same execution context.
     * @param graph1 the first graph.
     * @param graph2 the second graph.
     */
    private static void checkExecutionContexts(final ImmutableDirectedGraph<?> graph1, final ImmutableDirectedGraph<?> graph2) {
        if (graph1.executionContext != graph2.executionContext) {
            throw new IllegalArgumentException("The graphs are constructed with different execution contexts");
        }
    }
    
    /**
     * Adds a new edge to a graph.  If the specified edge is already in the graph,
     * then the resulting graph will be the same as the original.
     * <p>
     * <strong>Note</strong>: If the one or both vertices are not already in the graph, then
     * they will be added - the first before the second.
     * 
     * @param newEdge
     *          the edge to be added.
     * @return 
     *          a graph containing the same vertices and edges as the original, with
     * the possible addition of the specified edge and its endpoints.
     */
    public ImmutableDirectedGraph<T> addEdge(final Pair<T, T> newEdge) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.addEdge(graph, newEdge, executionContext));
    }

    /**
     * For each pair of vertices <code>v1</code> and <code>v2</code>, add an edge <code>(v1, v2)</code>
     * to the graph if and only if <code>existsEdgeFn v1 v2</code> returns <code>True</code>.
     * @param existsEdgeFn
     *          a predicate function indicating, for each ordered-pair of
     * vertices in the graph, whether an edge should be added from one to the other.
     * @return 
     *          a new graph containing all of the vertices and edges in the original
     * graph, plus the edges induced by the specified predicate function.
     */
    public ImmutableDirectedGraph<T> addEdges(final Predicate<Pair<T, T>> existsEdgeFn) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.addEdges(graph, existsEdgeFn, executionContext));
    }

    /**
     * Adds a new vertex to a graph.  If the specified vertex is already in the graph,
     * then the resulting graph will be the same as the original (including the
     * insertion order of the vertices).
     * @param vertex
     *          the vertex to be added.
     * @return 
     *          a graph containing the same vertices and edges as the original, with
     * the possible addition of the specified vertex.
     */
    public ImmutableDirectedGraph<T> addVertex(final T vertex) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.addVertex(graph, vertex, executionContext));
    }

    /**
     * <code>containsEdge graph (vertex1, vertex2)</code> returns <code>True</code> if 
     * <code>vertex1</code> and <code>vertex2</code> are vertices of <code>graph</code> and
     * <code>(vertex1, vertex2)</code> is an edge of <code>graph</code>.
     * @param edge
     *          the value to be tested for membership in the graph.
     * @return 
     *          <code>True</code> if <code>edge</code> is an edge of <code>graph</code>;
     * <code>False</code> otherwise.
     */
    public boolean containsEdge(final Pair<T, T> edge) throws CALExecutorException {
        return DirectedGraphLibrary.containsEdge(graph, edge, executionContext);
    }

    /**
     * <code>containsVertex graph vertex</code> returns <code>True</code> if <code>vertex</code>
     * is a vertex of <code>graph</code>.
     * @param vertex
     *          the value to be tested for membership in the graph.
     * @return 
     *          <code>True</code> if <code>vertex</code> is a vertex of <code>graph</code>;
     * <code>False</code> otherwise.
     */
    public boolean containsVertex(final T vertex) throws CALExecutorException {
        return DirectedGraphLibrary.containsVertex(graph, vertex, executionContext);
    }

    /**
     * Constructs a graph containing the specified vertices and no edges.
     * <p>
     * <strong>Note</strong>: The order of the vertices is preserved.
     * 
     * @param vertices
     *          the vertices of the graph to be constructed.
     * @return
     *          a graph containing the specified vertices and no edges.
     */
    public static <V> ImmutableDirectedGraph<V> edgelessGraph(final List<V> vertices, final ExecutionContext executionContext) throws CALExecutorException {
        return make(DirectedGraphLibrary.edgelessGraph(vertices, executionContext), executionContext);
    }

    /**
     * Constructs an empty graph.
     * @return (CAL type: {@code Cal.Utilities.DirectedGraph.DirectedGraph Vertex}) 
     *          an empty graph.
     */
    public static <V> ImmutableDirectedGraph<V> emptyGraph(final ExecutionContext executionContext) throws CALExecutorException {
        return make(DirectedGraphLibrary.emptyGraph(executionContext), executionContext);
    }

    /**
     * Determines whether or not two graphs have the same vertices and whether each
     * vertex has the same neighbours in both graphs.
     * @param graph1
     *          the first graph.
     * @param graph2
     *          the second graph.
     * @return 
     *          <code>True</code> if the graphs have the same vertices with the same
     * neighbours; <code>False</code> otherwise.
     */
    public static <V> boolean equalsDirectedGraphIgnoreInsertionOrder(final ImmutableDirectedGraph<V> graph1, final ImmutableDirectedGraph<V> graph2) throws CALExecutorException {
        checkExecutionContexts(graph1, graph2);
        return DirectedGraphLibrary.equalsDirectedGraphIgnoreInsertionOrder(graph1.graph, graph2.graph, graph1.executionContext);
    }

    /**
     * Determines whether or not two graphs have the same vertices, whether each vertex
     * has the same neighbours in both graphs, and whether both graphs have the same
     * vertex insertion order.
     * @param graph1
     *          the first graph.
     * @param graph2
     *          the second graph.
     * @return 
     *          <code>True</code> if the graphs have the same vertices with the same
     * neighbours and inserted in the same order; <code>False</code> otherwise.
     */
    public static <V> boolean equalsDirectedGraphWithInsertionOrder(final ImmutableDirectedGraph<V> graph1, final ImmutableDirectedGraph<V> graph2) throws CALExecutorException {
        checkExecutionContexts(graph1, graph2);
        return DirectedGraphLibrary.equalsDirectedGraphWithInsertionOrder(graph1.graph, graph2.graph, graph1.executionContext);
    }

    /**
     * Determines whether or not the specified graph contains a cycle.
     * 
     * @see #findCycle
     * 
     * @return 
     *          <code>True</code> if the graph contains a cycle; <code>False</code> otherwise.
     */
    public boolean existsCycle() throws CALExecutorException {
        return DirectedGraphLibrary.existsCycle(graph, executionContext);
    }

    /**
     * Determines whether the graph contains a path from path from <code>startVertex</code>
     * to <code>endVertex</code>.
     * <p>
     * <strong>Note</strong>: each vertex is considered to have a trivial path from itself to itself.
     * 
     * 
     * @see #findPath
     * 
     * @param startVertex
     *          the vertex from which to search.
     * @param endVertex
     *          the vertex to seek.
     * @return 
     *          <code>True</code> if the graph contains a path from <code>startVertex</code>
     * to <code>endVertex</code>; <code>False</code> otherwise.
     */
    public boolean existsPath(final T startVertex, final T endVertex) throws CALExecutorException {
        return DirectedGraphLibrary.existsPath(graph, startVertex, endVertex, executionContext);
    }

    /**
     * Determines whether or not the specified graph contains a cycle reachable from
     * the specified vertex.
     * 
     * @see #findReachableCycle
     * 
     * @param startVertex
     *          the vertex from which to seek a cycle.
     * @return 
     *          <code>True</code> if the graph contains a reachable cycle; <code>False</code> otherwise.
     */
    public boolean existsReachableCycle(final T startVertex) throws CALExecutorException {
        return DirectedGraphLibrary.existsReachableCycle(graph, startVertex, executionContext);
    }

    /**
     * Eliminates from the graph all elements for which <code>filterFn</code> returns
     * <code>False</code>.
     * 
     * @see #partition
     * 
     * @param filterFn
     *          a function which returns <code>True</code> if a vertex belongs
     * in the subgraph and <code>False</code> otherwise.
     * @return 
     *          the subgraph induced by the filter function.
     */
    public ImmutableDirectedGraph<T> filter(final Predicate<T> filterFn) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.filter(filterFn, graph, executionContext));
    }

    /**
     * Returns a list of vertices forming a cycle (first list element is duplicated
     * in the last position), if one exists.
     * <p>
     * <strong>Note</strong>: if the graph contains multiple cycles, then any one may be returned.
     * 
     * 
     * @see #findReachableCycle
     * @see #existsCycle
     * 
     * @return 
     *          the cycle if one exists, or <code>null</code> otherwise.
     */
    @SuppressWarnings("unchecked")
    public List<T> findCycle() throws CALExecutorException {
        return DirectedGraphLibrary.findCycle(graph, executionContext);
    }

    /**
     * Returns a list of vertices forming a path from <code>startVertex</code> to 
     * <code>endVertex</code> (inclusive), if one exists.
     * <p>
     * <strong>Note</strong>: if the graph contains multiple such paths, then any one may be returned.
     * <strong>Note</strong>: each vertex is considered to have a trivial path from itself to itself.
     * 
     * 
     * @see #existsPath
     * 
     * @param startVertex
     *          the vertex from which to search.
     * @param endVertex
     *          the vertex to seek.
     * @return 
     *          the path if one exists, or <code>null</code> otherwise.
     */
    @SuppressWarnings("unchecked")
    public List<T> findPath(final T startVertex, final T endVertex) throws CALExecutorException {
        return DirectedGraphLibrary.findPath(graph, startVertex, endVertex, executionContext);
    }

    /**
     * Returns a list of vertices forming a cycle reachable from the specified
     * vertex (first list element is duplicated in the last position), if one exists.
     * <p>
     * <strong>Note</strong>: if the reachable subgraph contains multiple cycles, then any one
     * may be returned.
     * 
     * 
     * @see #findCycle
     * @see #existsReachableCycle
     * 
     * @param startVertex
     *          the vertex from which to seek a cycle.
     * @return 
     *          the cycle if one exists, or <code>null</code> otherwise.
     */
    @SuppressWarnings("unchecked")
    public List<T> findReachableCycle(final T startVertex) throws CALExecutorException {
        return DirectedGraphLibrary.findReachableCycle(graph, startVertex, executionContext);
    }

    /**
     * Returns a copy of the graph that contains no cycles - within each strongly-
     * connected component, all edges are removed and replaced with new edges
     * enforcing the insertion order.
     * <p>
     * e.g. <code>cycle{B, A} -&gt; F -&gt; cycle{C, E, D}</code> becomes 
     * <code>A -&gt; B -&gt; F -&gt; C -&gt; D -&gt; E</code> (assuming the vertices were created in 
     * alphabetical order).
     * 
     * @return 
     *          a copy of the specified graph with all cycles flattened.
     */
    public ImmutableDirectedGraph<T> flattenComponents() throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.flattenComponents(graph, executionContext));
    }

    /**
     * Fold across the entire graph in depth-first search order.
     * <p>
     * <strong>Note</strong>: this is simply an alias for {@link #foldInDepthFirstSearchOrder foldInDepthFirstSearchOrder}.
     * 
     * 
     * @see #foldInDepthFirstSearchOrder
     * @see #foldReachableDFS
     * @see #foldReachableInDepthFirstSearchOrder
     * 
     * @param startVertexFn
     *          called when a vertex is visited for the first time.
     * Guaranteed to be called exactly once per vertex.
     * @param finishVertexFn
     *          called when a vertex is finished (all children are finished).
     * Guaranteed to be called exactly once per vertex.
     * @param init
     *          the initial accumulator value (returned directly if the graph is empty).
     * @return 
     *          The accumulated value after the final vertex is finished.
     */
    @SuppressWarnings("unchecked")
    public <A> A foldDFS(final BinaryFunction<A, T, A> startVertexFn, final BinaryFunction<A, T, A> finishVertexFn, final A init) throws CALExecutorException {
        return (A)DirectedGraphLibrary.foldDFS(startVertexFn, finishVertexFn, init, graph, executionContext);
    }

    /**
     * Fold across the entire graph in depth-first search order.
     * <p>
     * <strong>Note</strong>: for convenience, you may wish to call this function as {@link #foldDFS foldDFS}.
     * 
     * 
     * @see #foldDFS
     * @see #foldReachableDFS
     * @see #foldReachableInDepthFirstSearchOrder
     * 
     * @param startVertexFn
     *          called when a vertex is visited for the first time.
     * Guaranteed to be called exactly once per vertex.
     * @param finishVertexFn
     *          called when a vertex is finished (all children are finished).
     * Guaranteed to be called exactly once per vertex.
     * @param init
     *          the initial accumulator value (returned directly if the graph is empty).
     * @return 
     *          The accumulated value after the final vertex is finished.
     */
    @SuppressWarnings("unchecked")
    public <A> A foldInDepthFirstSearchOrder(final BinaryFunction<A, T, A> startVertexFn, final BinaryFunction<A, T, A> finishVertexFn, final A init) throws CALExecutorException {
        return (A)DirectedGraphLibrary.foldInDepthFirstSearchOrder(startVertexFn, finishVertexFn, init, graph, executionContext);
    }

    /**
     * Fold across graph vertices reachable from the specified root in depth-first
     * search order.
     * <p>
     * <strong>Note</strong>: this is simply an alias for {@link #foldReachableInDepthFirstSearchOrder foldReachableInDepthFirstSearchOrder}.
     * 
     * 
     * @see #foldDFS
     * @see #foldInDepthFirstSearchOrder
     * @see #foldReachableInDepthFirstSearchOrder
     * 
     * @param startVertex
     *          the vertex at which to begin the traversal.
     * @param startVertexFn
     *          called when a vertex is visited for the first time.
     * Guaranteed to be called exactly once for each reachable vertex.
     * @param finishVertexFn
     *          called when a vertex is finished (all children are finished).
     * Guaranteed to be called exactly once for each reachable vertex.
     * @param init
     *          the initial accumulator value (returned directly if the graph is empty).
     * @return 
     *          The accumulated value after the final vertex is finished.
     */
    @SuppressWarnings("unchecked")
    public <A> A foldReachableDFS(final T startVertex, final BinaryFunction<A, T, A> startVertexFn, final BinaryFunction<A, T, A> finishVertexFn, final A init) throws CALExecutorException {
        return (A)DirectedGraphLibrary.foldReachableDFS(startVertex, startVertexFn, finishVertexFn, init, graph, executionContext);
    }

    /**
     * Fold across graph vertices reachable from the specified root in depth-first
     * search order.
     * <p>
     * <strong>Note</strong>: for convenience, you may wish to call this function as {@link #foldReachableDFS foldReachableDFS}.
     * 
     * 
     * @see #foldDFS
     * @see #foldInDepthFirstSearchOrder
     * @see #foldReachableDFS
     * 
     * @param startVertex
     *          the vertex at which to begin the traversal.
     * @param startVertexFn
     *          called when a vertex is visited for the first time.
     * Guaranteed to be called exactly once for each reachable vertex.
     * @param finishVertexFn
     *          called when a vertex is finished (all children are finished).
     * Guaranteed to be called exactly once for each reachable vertex.
     * @param init
     *          the initial accumulator value (returned directly if the graph is empty).
     * @return 
     *          The accumulated value after the final vertex is finished.
     */
    @SuppressWarnings("unchecked")
    public <A> A foldReachableInDepthFirstSearchOrder(final T startVertex, final BinaryFunction<A, T, A> startVertexFn, final BinaryFunction<A, T, A> finishVertexFn, final A init) throws CALExecutorException {
        return (A)DirectedGraphLibrary.foldReachableInDepthFirstSearchOrder(startVertex, startVertexFn, finishVertexFn, init, graph, executionContext);
    }

    /**
     * Returns the number of edges in the specified graph.
     * @return 
     *          the number of edges in the graph.
     */
    public int getEdgeCount() throws CALExecutorException {
        return DirectedGraphLibrary.getEdgeCount(graph, executionContext);
    }

    /**
     * Returns the list of out-neighbours of the specified vertex.  No particular
     * order is guaranteed.
     * @param vertex
     *          the vertex whose neighbours are sought.
     * @return 
     *          a list of out-neighbours of the specified vertex.
     */
    @SuppressWarnings("unchecked")
    public List<T> getNeighbours(final T vertex) throws CALExecutorException {
        return DirectedGraphLibrary.getNeighbours(graph, vertex, executionContext);
    }

    /**
     * Returns the number of vertices in the specified graph.
     * @return 
     *          the number of vertices in the graph.
     */
    public int getVertexCount() throws CALExecutorException {
        return DirectedGraphLibrary.getVertexCount(graph, executionContext);
    }

    /**
     * Returns a list of the vertices in the specified graph.  No particular order
     * is guaranteed.
     * @return 
     *          a list of vertices in the graph.
     */
    @SuppressWarnings("unchecked")
    public List<T> getVertices() throws CALExecutorException {
        return DirectedGraphLibrary.getVertices(graph, executionContext);
    }

    /**
     * Returns a list of the vertices in the specified graph.  The vertices will
     * be ordered by insertion time.
     * @return 
     *          a list of vertices in the graph.
     */
    @SuppressWarnings("unchecked")
    public List<T> getVerticesInInsertionOrder() throws CALExecutorException {
        return DirectedGraphLibrary.getVerticesInInsertionOrder(graph, executionContext);
    }

    /**
     * Returns whether the specified graph is the empty graph (i.e. contains no
     * vertices).
     * @return  
     *          <code>True</code> if the graph is empty; <code>False</code> otherwise.
     */
    public boolean isEmpty() throws CALExecutorException {
        return DirectedGraphLibrary.isEmpty(graph, executionContext);
    }

    /**
     * Constructs a graph containing the specified vertices and edges.
     * <p>
     * <strong>Note</strong>: If an edge <code>(v1, v2)</code> is specified and if either
     * <code>v1</code> or <code>v2</code> is not in the list of vertices, then it will be
     * added to the graph anyway.  
     * <p>
     * <strong>Note</strong>: The insertion order will be determined by the order in which
     * such vertices are encountered while adding edges to the graph.  For example, 
     * <code>getVerticesInInsertionOrder (makeGraph [v1, v2] [(v1, v3), (v4, v2)])</code>
     * will return <code>[v1, v2, v3, v4]</code>.
     * 
     * @param vertices
     *          the vertices of the graph to be constructed.
     * @param edges
     *          the edges of the graph to be constructed.
     * @return 
     *          a graph containing the specified vertices and edges.
     */
    public static <V> ImmutableDirectedGraph<V> makeGraph(final List<V> vertices, final List<Pair<V, V>> edges, final ExecutionContext executionContext) throws CALExecutorException {
        return make(DirectedGraphLibrary.makeGraph(vertices, edges, executionContext), executionContext);
    }

    /**
     * Constructs a graph containing the specified vertices.  For each pair of vertices
     * <code>v1</code> and <code>v2</code>, the graph will contain contain an edge <code>(v1, v2)</code>
     * if and only if <code>existsEdgeFn v1 v2</code> returns <code>True</code>.
     * <p>
     * <strong>Note</strong>: The order of the vertices is preserved.
     * 
     * 
     * @see #addEdges
     * 
     * @param vertices
     *          the vertices of the graph to be constructed.
     * @param existsEdgeFn
     *          a predicate function indicating, for each ordered-pair of
     * vertices in the graph, whether an edge exists from one to the other.
     * @return 
     *          a graph containing the specified vertices and the edges induced by
     * the specified predicate function.
     */
    public static <V> ImmutableDirectedGraph<V> makePredicateGraph(final List<V> vertices, final Predicate<Pair<V, V>> existsEdgeFn, final ExecutionContext executionContext) throws CALExecutorException {
        return make(DirectedGraphLibrary.makePredicateGraph(vertices, existsEdgeFn, executionContext), executionContext);
    }

    /**
     * Applies the specified function to each vertex in the specified graph.
     * <p>
     * <strong>Note</strong>: If two vertices have the same image under the specified function,
     * then they will be merged (retaining self-loops created during the merge).
     * 
     * 
     * @see #mergeVertices
     * 
     * @param mapFn the mapping function.
     * @return 
     *          the graph that results when the specified function is applied to each
     * vertex in the specified graph.
     */
    public <V> ImmutableDirectedGraph<V> map(final UnaryFunction<T, V> mapFn) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.map(mapFn, graph, executionContext));
    }

    /**
     * Merges two vertices of a graph.  
     * <code>mergeVertices graph retainLoops vertex1 vertex2 mergedVertex</code>
     * results in a graph satisfying:
     * <ul>
     *  <li>
     *   <code>vertex1</code> is removed from the graph
     *  </li>
     *  <li>
     *   <code>vertex2</code> is removed from the graph
     *  </li>
     *  <li>
     *   <code>mergedVertex</code> is added to the graph
     *  </li>
     *  <li>
     *   Edges are transformed (modulo <code>retainLoops</code>):
     *   <ul>
     *    <li>
     *     <code>(A, ?)</code> -&gt; <code>(C, ?)</code>
     *    </li>
     *    <li>
     *     <code>(?, A)</code> -&gt; <code>(?, C)</code>
     *    </li>
     *    <li>
     *     <code>(B, ?)</code> -&gt; <code>(C, ?)</code>
     *    </li>
     *    <li>
     *     <code>(?, B)</code> -&gt; <code>(?, C)</code>
     *    </li>
     *   </ul>
     *  </li>
     * </ul>
     * <p>
     *  
     * <p>
     * <strong>Side effect</strong>: If the merged vertex is already contained in the graph, then
     * its insertion order will not change.  Otherwise, the merged vertex will
     * acquire the insertion order of the first vertex argument (note: argument 1,
     * not vertex with earlier insertion time).
     * <p>
     * <strong>Note</strong>: Throws an <code>error</code> if either <code>vertex1</code> or
     * <code>vertex2</code> is not in the graph. 
     * 
     * @param retainLoops
     *          if vertices A and B are merged to C and the graph contains
     * (A, B) or (B, A), then (C, C) will be added to the new graph if retainLoops
     * is true.  Note that if the graph contains (A, A) or (B, B), then the new
     * graph will contain (C, C) regardless of the value of retainLoops.
     * @param vertex1
     *          the first vertex to be merged (error if invalid).
     * @param vertex2
     *          the second vertex to be merged (error if invalid).
     * @param mergedVertex
     *          the resulting vertex.
     * @return 
     *          a new graph with the two vertices merged.
     */
    public ImmutableDirectedGraph<T> mergeVertices(final boolean retainLoops, final T vertex1, final T vertex2, final T mergedVertex) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.mergeVertices(graph, retainLoops, vertex1, vertex2, mergedVertex, executionContext));
    }

    /**
     * Partitions the vertices into two sets and then removes all edges from one set
     * to the other.
     * 
     * @see #filter
     * 
     * @param partitionFn
     *          a function which returns <code>True</code> if a vertex belongs
     * in the first subgraph and <code>False</code> if a vertex belongs in the second
     * subgraph.
     * @return 
     *          the two subgraphs induced by the partition function.
     */
    @SuppressWarnings("unchecked")
    public Pair<ImmutableDirectedGraph<T>, ImmutableDirectedGraph<T>> partition(final Predicate<T> partitionFn) throws CALExecutorException {
        return DirectedGraphLibrary.partition(partitionFn, graph, executionContext);
    }

    /**
     * Removes an edge from a graph.  If the specified edge is already absent from
     * the graph (perhaps because one of the endpoints is absent from the graph),
     * then the resulting graph will be the same as the original.
     * @param edge
     *          the edge to be removed.
     * @return 
     *          a graph containing the same vertices and edges as the original, with
     * the possible exception of the specified edge.
     */
    public ImmutableDirectedGraph<T> removeEdge(final Pair<T, T> edge) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.removeEdge(graph, edge, executionContext));
    }

    /**
     * Removes a new vertex from a graph.  If the specified vertex is already absent
     * from the graph, then the resulting graph will be the same as the original.
     * @param vertex
     *          the vertex to be removed.
     * @return 
     *          a graph containing the same vertices and edges as the original, with
     * the possible exception of the specified vertex.
     */
    public ImmutableDirectedGraph<T> removeVertex(final T vertex) throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.removeVertex(graph, vertex, executionContext));
    }

    /**
     * Reverses all of the edges in a graph (sometimes referred to as the transpose
     * graph).
     * @return 
     *          the graph with all edges reversed.
     */
    public ImmutableDirectedGraph<T> reverse() throws CALExecutorException {
        return makeUpdated(DirectedGraphLibrary.reverse(graph, executionContext));
    }

    /**
     * Returns a string representation of the graph.  The graph is traversed in depth-first
     * order and each vertex is displayed with a list of its children.  Any vertex
     * that has previously been encountered will be shown in angle brackets and
     * not expanded (i.e. its children will not be shown).  For example:
     * <p>
     * 
     * <pre> vertex1 {
     *    child_1 {
     *      grandchild_1_1 {
     *      }
     *      grandchild_1_2 {
     *      }
     *    child_2 {
     *      grandchild_2_1 {
     *      }
     *      &lt;grandchild_1_1&gt;        &lt;-- Details omitted since repeated
     *    }
     *  }
     *  vertex2 {
     *    ...
     *  }
     * </pre>
     * 
     * 
     * @return 
     *          a string representation of the graph.
     */
    public java.lang.String showDirectedGraph() throws CALExecutorException {
        return DirectedGraphLibrary.showDirectedGraph(graph, executionContext);
    }

    /**
     * Returns the source vertex of the given edge.
     * @param edge
     *          the edge.
     * @return 
     *          the source vertex of the given edge.
     */
    public static <V> V sourceVertex(final Pair<V, V> edge) {
        return edge.fst();
    }

    /**
     * Returns the vertices in topological order if the graph is acyclic and in an
     * unspecified order otherwise.  If the relative order of two vertices is not
     * specified by the graph, then their insertion order will be used.
     * <p>
     * If the graph contains cycles, then calling {@link #flattenComponents flattenComponents} before
     * {@link #stableTopologicalSort stableTopologicalSort} will produce the desired stable order.
     * <p>
     * <strong>Note</strong>: this may be quite a bit slower than a normal topological sort.
     * <p>
     * Algorithm adapted from Exercise 22.4-5 on p. 552 of 
     * "Introduction to Algorithms 2E"
     * by Cormen, Leiserson, Rivest, and Stein (2002).
     * 
     * @return 
     *          an ordered list of vertices.
     */
    @SuppressWarnings("unchecked")
    public List<T> stableTopologicalSort() throws CALExecutorException {
        return DirectedGraphLibrary.stableTopologicalSort(graph, executionContext);
    }

    /**
     * Returns a topologically sorted list of strongly-connected components of a
     * specified graph (i.e. if A and B are SCCs and A precedes B in the returned
     * list, then the graph contains no edges from vertices in A to vertices in B).
     * <p>
     * Algorithm based on STRONGLY-CONNECTED-COMPONENTS on p. 554 of 
     * "Introduction to Algorithms 2E"
     * by Cormen, Leiserson, Rivest, and Stein (2002).
     * 
     * @return 
     *          a topologically sorted list of strongly-connected components of the
     * specified graph.
     */
    @SuppressWarnings("unchecked")
    public List<List<T>> stronglyConnectedComponents() throws CALExecutorException {
        return DirectedGraphLibrary.stronglyConnectedComponents(graph, executionContext);
    }

    /**
     * Returns the target vertex of the given edge.
     * @param edge
     *          the edge.
     * @return 
     *          the target vertex of the given edge.
     */
    public static <V> V targetVertex(final Pair<V, V> edge) {
        return edge.snd();
    }

    /**
     * Returns the vertices of a graph in topological order if the graph is acyclic
     * and in an unspecified order otherwise.
     * <p>
     * Algorithm based on TOPOLOGICAL-SORT on p. 550 of 
     * "Introduction to Algorithms 2E"
     * by Cormen, Leiserson, Rivest, and Stein (2002).
     * 
     * @return 
     *          an ordered list of vertices.
     */
    @SuppressWarnings("unchecked")
    public List<T> topologicalSort() throws CALExecutorException {
        return DirectedGraphLibrary.topologicalSort(graph, executionContext);
    }
    
    /**
     * Since most clients of foldDFS will not want to use all of the function arguments,
     * this is provided as a convenient default (simply passes the accumulated value
     * through to the next handler).
     */
    public static <A, V> BinaryFunction<A, V, A> noChange() {
        return new BinaryFunction<A, V, A>() {
            public A apply(final A arg1, final V arg2) {
                return arg1;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        try {
            return showDirectedGraph();
        } catch (CALExecutorException e) {
            throw new IllegalStateException(e);
        }
    }
}
