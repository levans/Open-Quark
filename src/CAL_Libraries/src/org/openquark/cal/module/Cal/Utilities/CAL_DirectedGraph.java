/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_DirectedGraph.java)
 * was generated from CAL module: Cal.Utilities.DirectedGraph.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.DirectedGraph module from Java code.
 *  
 * Creation date: Mon Aug 13 13:20:41 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * A directed graph of distinguishable objects.  Useful for storing partial
 * orders and finding cyclical dependencies.
 * <p>
 * <strong>Note</strong>: Maintains the insertion order of the vertices.
 * <p>
 * <strong>Note</strong>: Does not maintain the insertion order of the edges.
 * <p>
 * <strong>INVARIANT</strong>: If vertices A and B are distinct and A was inserted before B,
 * then it must be true that #A &lt; #B.
 * <p>
 * <strong>INVARIANT</strong>: If vertices A and B are not distinct, then #A = #B (i.e. no
 * duplicate vertices).
 * 
 * @author Andrew Casey
 */
public final class CAL_DirectedGraph {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.DirectedGraph");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.DirectedGraph module.
	 */
	public static final class TypeConstructors {
		/**
		 * A directed graph with distinct distinguishable (ie. <code>Cal.Core.Prelude.Eq</code>) vertices.
		 * <p>
		 * <strong>Note</strong>: Maintains the insertion order of the vertices.
		 * <p>
		 * <strong>Note</strong>: Does not maintain the insertion order of the edges.
		 * <p>
		 * Structure:
		 * <ul>
		 *  <li>
		 *   Assigns a unique integer to each vertex so that it can be manipulated
		 *   more nicely (many of the standard collections expect elements to be orderable).
		 *   <strong>Invariant:</strong> if vertex1 was inserted before vertex2, then vertex1 has a
		 *   unique integer that is less than that of vertex2.
		 *  </li>
		 *  <li>
		 *   Stores vertices in a map (ID -&gt; Vertex).
		 *  </li>
		 *  <li>
		 *   Stores edges in an adjacency list (ID -&gt; Set of IDs).
		 *  </li>
		 * </ul>
		 */
		public static final QualifiedName DirectedGraph = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "DirectedGraph");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.DirectedGraph module.
	 */
	public static final class Functions {
		/**
		 * Adds a new edge to a graph.  If the specified edge is already in the graph,
		 * then the resulting graph will be the same as the original.
		 * <p>
		 * <strong>Note</strong>: If the one or both vertices are not already in the graph, then
		 * they will be added - the first before the second.
		 * 
		 * @param oldGraph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to which the edge will be added.
		 * @param newEdge (CAL type: <code>Cal.Core.Prelude.Eq a => (a, a)</code>)
		 *          the edge to be added.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a graph containing the same vertices and edges as the original, with
		 * the possible addition of the specified edge and its endpoints.
		 */
		public static final SourceModel.Expr addEdge(SourceModel.Expr oldGraph, SourceModel.Expr newEdge) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addEdge), oldGraph, newEdge});
		}

		/**
		 * Name binding for function: addEdge.
		 * @see #addEdge(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addEdge = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "addEdge");

		/**
		 * For each pair of vertices <code>v1</code> and <code>v2</code>, add an edge <code>(v1, v2)</code>
		 * to the graph if and only if <code>existsEdgeFn v1 v2</code> returns <code>Cal.Core.Prelude.True</code>.
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to which edges will be added
		 * @param existsEdgeFn (CAL type: <code>Cal.Core.Prelude.Eq a => a -> a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate function indicating, for each ordered-pair of
		 * vertices in the graph, whether an edge should be added from one to the other.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a new graph containing all of the vertices and edges in the original
		 * graph, plus the edges induced by the specified predicate function.
		 */
		public static final SourceModel.Expr addEdges(SourceModel.Expr graph, SourceModel.Expr existsEdgeFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addEdges), graph, existsEdgeFn});
		}

		/**
		 * Name binding for function: addEdges.
		 * @see #addEdges(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addEdges = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "addEdges");

		/**
		 * Adds a new vertex to a graph.  If the specified vertex is already in the graph,
		 * then the resulting graph will be the same as the original (including the
		 * insertion order of the vertices).
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to which the vertex will be added.
		 * @param vertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex to be added.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a graph containing the same vertices and edges as the original, with
		 * the possible addition of the specified vertex.
		 */
		public static final SourceModel.Expr addVertex(SourceModel.Expr graph, SourceModel.Expr vertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addVertex), graph, vertex});
		}

		/**
		 * Name binding for function: addVertex.
		 * @see #addVertex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addVertex = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "addVertex");

		/**
		 * <code>containsEdge graph (vertex1, vertex2)</code> returns <code>Cal.Core.Prelude.True</code> if 
		 * <code>vertex1</code> and <code>vertex2</code> are vertices of <code>graph</code> and
		 * <code>(vertex1, vertex2)</code> is an edge of <code>graph</code>.
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to be checked.
		 * @param edge (CAL type: <code>Cal.Core.Prelude.Eq a => (a, a)</code>)
		 *          the value to be tested for membership in the graph.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>edge</code> is an edge of <code>graph</code>;
		 * <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr containsEdge(SourceModel.Expr graph, SourceModel.Expr edge) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.containsEdge), graph, edge});
		}

		/**
		 * Name binding for function: containsEdge.
		 * @see #containsEdge(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName containsEdge = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "containsEdge");

		/**
		 * <code>containsVertex graph vertex</code> returns <code>Cal.Core.Prelude.True</code> if <code>vertex</code>
		 * is a vertex of <code>graph</code>.
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to be checked.
		 * @param vertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be tested for membership in the graph.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>vertex</code> is a vertex of <code>graph</code>;
		 * <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr containsVertex(SourceModel.Expr graph, SourceModel.Expr vertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.containsVertex), graph, vertex});
		}

		/**
		 * Name binding for function: containsVertex.
		 * @see #containsVertex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName containsVertex = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "containsVertex");

		/**
		 * Constructs a graph containing the specified vertices and no edges.
		 * <p>
		 * <strong>Note</strong>: The order of the vertices is preserved.
		 * 
		 * @param vertices (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the vertices of the graph to be constructed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a graph containing the specified vertices and no edges.
		 */
		public static final SourceModel.Expr edgelessGraph(SourceModel.Expr vertices) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.edgelessGraph), vertices});
		}

		/**
		 * Name binding for function: edgelessGraph.
		 * @see #edgelessGraph(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName edgelessGraph = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "edgelessGraph");

		/**
		 * Constructs an empty graph.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          an empty graph.
		 */
		public static final SourceModel.Expr emptyGraph() {
			return SourceModel.Expr.Var.make(Functions.emptyGraph);
		}

		/**
		 * Name binding for function: emptyGraph.
		 * @see #emptyGraph()
		 */
		public static final QualifiedName emptyGraph = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "emptyGraph");

		/**
		 * Determines whether or not two graphs have the same vertices and whether each
		 * vertex has the same neighbours in both graphs.
		 * @param graph1 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the first graph.
		 * @param graph2 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the second graph.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the graphs have the same vertices with the same
		 * neighbours; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr equalsDirectedGraphIgnoreInsertionOrder(SourceModel.Expr graph1, SourceModel.Expr graph2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsDirectedGraphIgnoreInsertionOrder), graph1, graph2});
		}

		/**
		 * Name binding for function: equalsDirectedGraphIgnoreInsertionOrder.
		 * @see #equalsDirectedGraphIgnoreInsertionOrder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsDirectedGraphIgnoreInsertionOrder = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"equalsDirectedGraphIgnoreInsertionOrder");

		/**
		 * Determines whether or not two graphs have the same vertices, whether each vertex
		 * has the same neighbours in both graphs, and whether both graphs have the same
		 * vertex insertion order.
		 * @param graph1 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the first graph.
		 * @param graph2 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the second graph.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the graphs have the same vertices with the same
		 * neighbours and inserted in the same order; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr equalsDirectedGraphWithInsertionOrder(SourceModel.Expr graph1, SourceModel.Expr graph2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsDirectedGraphWithInsertionOrder), graph1, graph2});
		}

		/**
		 * Name binding for function: equalsDirectedGraphWithInsertionOrder.
		 * @see #equalsDirectedGraphWithInsertionOrder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsDirectedGraphWithInsertionOrder = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"equalsDirectedGraphWithInsertionOrder");

		/**
		 * Determines whether or not the specified graph contains a cycle.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.findCycle
		 * </dl>
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to seek a cycle.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the graph contains a cycle; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr existsCycle(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.existsCycle), graph});
		}

		/**
		 * Name binding for function: existsCycle.
		 * @see #existsCycle(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName existsCycle = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "existsCycle");

		/**
		 * Determines whether the graph contains a path from path from <code>startVertex</code>
		 * to <code>endVertex</code>.
		 * <p>
		 * <strong>Note</strong>: each vertex is considered to have a trivial path from itself to itself.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.findPath
		 * </dl>
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to seek a path.
		 * @param startVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex from which to search.
		 * @param endVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex to seek.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the graph contains a path from <code>startVertex</code>
		 * to <code>endVertex</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr existsPath(SourceModel.Expr graph, SourceModel.Expr startVertex, SourceModel.Expr endVertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.existsPath), graph, startVertex, endVertex});
		}

		/**
		 * Name binding for function: existsPath.
		 * @see #existsPath(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName existsPath = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "existsPath");

		/**
		 * Determines whether or not the specified graph contains a cycle reachable from
		 * the specified vertex.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.findReachableCycle
		 * </dl>
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to seek a cycle.
		 * @param startVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex from which to seek a cycle.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the graph contains a reachable cycle; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr existsReachableCycle(SourceModel.Expr graph, SourceModel.Expr startVertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.existsReachableCycle), graph, startVertex});
		}

		/**
		 * Name binding for function: existsReachableCycle.
		 * @see #existsReachableCycle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName existsReachableCycle = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"existsReachableCycle");

		/**
		 * Eliminates from the graph all elements for which <code>filterFn</code> returns
		 * <code>Cal.Core.Prelude.False</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.partition
		 * </dl>
		 * 
		 * @param filterFn (CAL type: <code>Cal.Core.Prelude.Eq a => a -> Cal.Core.Prelude.Boolean</code>)
		 *          a function which returns <code>Cal.Core.Prelude.True</code> if a vertex belongs
		 * in the subgraph and <code>Cal.Core.Prelude.False</code> otherwise.
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to filter.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          the subgraph induced by the filter function.
		 */
		public static final SourceModel.Expr filter(SourceModel.Expr filterFn, SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filter), filterFn, graph});
		}

		/**
		 * Name binding for function: filter.
		 * @see #filter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filter = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "filter");

		/**
		 * Returns a list of vertices forming a cycle (first list element is duplicated
		 * in the last position), if one exists.
		 * <p>
		 * <strong>Note</strong>: if the graph contains multiple cycles, then any one may be returned.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.findReachableCycle, Cal.Utilities.DirectedGraph.existsCycle
		 * </dl>
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to seek a cycle.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Core.Prelude.Maybe [a]</code>) 
		 *          <code>Cal.Core.Prelude.Just cycle</code> if one exists and <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr findCycle(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findCycle), graph});
		}

		/**
		 * Name binding for function: findCycle.
		 * @see #findCycle(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findCycle = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "findCycle");

		/**
		 * Returns a list of vertices forming a path from <code>startVertex</code> to 
		 * <code>endVertex</code> (inclusive), if one exists.
		 * <p>
		 * <strong>Note</strong>: if the graph contains multiple such paths, then any one may be returned.
		 * <strong>Note</strong>: each vertex is considered to have a trivial path from itself to itself.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.existsPath
		 * </dl>
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to seek a path.
		 * @param startVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex from which to search.
		 * @param endVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex to seek.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Core.Prelude.Maybe [a]</code>) 
		 *          <code>Cal.Core.Prelude.Just path</code> if one exists and <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr findPath(SourceModel.Expr graph, SourceModel.Expr startVertex, SourceModel.Expr endVertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findPath), graph, startVertex, endVertex});
		}

		/**
		 * Name binding for function: findPath.
		 * @see #findPath(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findPath = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "findPath");

		/**
		 * Returns a list of vertices forming a cycle reachable from the specified
		 * vertex (first list element is duplicated in the last position), if one exists.
		 * <p>
		 * <strong>Note</strong>: if the reachable subgraph contains multiple cycles, then any one
		 * may be returned.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.findCycle, Cal.Utilities.DirectedGraph.existsReachableCycle
		 * </dl>
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to seek a cycle.
		 * @param startVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex from which to seek a cycle.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Core.Prelude.Maybe [a]</code>) 
		 *          <code>Cal.Core.Prelude.Just cycle</code> if one exists and <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr findReachableCycle(SourceModel.Expr graph, SourceModel.Expr startVertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findReachableCycle), graph, startVertex});
		}

		/**
		 * Name binding for function: findReachableCycle.
		 * @see #findReachableCycle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findReachableCycle = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"findReachableCycle");

		/**
		 * Returns a copy of the graph that contains no cycles - within each strongly-
		 * connected component, all edges are removed and replaced with new edges
		 * enforcing the insertion order.
		 * <p>
		 * e.g. <code>cycle{B, A} -&gt; F -&gt; cycle{C, E, D}</code> becomes 
		 * <code>A -&gt; B -&gt; F -&gt; C -&gt; D -&gt; E</code> (assuming the vertices were created in 
		 * alphabetical order).
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to flatten cycles.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a copy of the specified graph with all cycles flattened.
		 */
		public static final SourceModel.Expr flattenComponents(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.flattenComponents), graph});
		}

		/**
		 * Name binding for function: flattenComponents.
		 * @see #flattenComponents(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName flattenComponents = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"flattenComponents");

		/**
		 * Fold across the entire graph in depth-first search order.
		 * <p>
		 * <strong>Note</strong>: this is simply an alias for <code>Cal.Utilities.DirectedGraph.foldInDepthFirstSearchOrder</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.foldInDepthFirstSearchOrder, Cal.Utilities.DirectedGraph.foldReachableDFS, Cal.Utilities.DirectedGraph.foldReachableInDepthFirstSearchOrder
		 * </dl>
		 * 
		 * @param startVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is visited for the first time.
		 * Guaranteed to be called exactly once per vertex.
		 * @param finishVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is finished (all children are finished).
		 * Guaranteed to be called exactly once per vertex.
		 * @param init (CAL type: <code>b</code>)
		 *          the initial accumulator value (returned directly if the graph is empty).
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to fold across.
		 * @return (CAL type: <code>b</code>) 
		 *          The accumulated value after the final vertex is finished.
		 */
		public static final SourceModel.Expr foldDFS(SourceModel.Expr startVertexFn, SourceModel.Expr finishVertexFn, SourceModel.Expr init, SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldDFS), startVertexFn, finishVertexFn, init, graph});
		}

		/**
		 * Name binding for function: foldDFS.
		 * @see #foldDFS(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldDFS = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "foldDFS");

		/**
		 * Fold across the entire graph in depth-first search order.
		 * <p>
		 * <strong>Note</strong>: for convenience, you may wish to call this function as <code>Cal.Utilities.DirectedGraph.foldDFS</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.foldDFS, Cal.Utilities.DirectedGraph.foldReachableDFS, Cal.Utilities.DirectedGraph.foldReachableInDepthFirstSearchOrder
		 * </dl>
		 * 
		 * @param startVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is visited for the first time.
		 * Guaranteed to be called exactly once per vertex.
		 * @param finishVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is finished (all children are finished).
		 * Guaranteed to be called exactly once per vertex.
		 * @param init (CAL type: <code>b</code>)
		 *          the initial accumulator value (returned directly if the graph is empty).
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to fold across.
		 * @return (CAL type: <code>b</code>) 
		 *          The accumulated value after the final vertex is finished.
		 */
		public static final SourceModel.Expr foldInDepthFirstSearchOrder(SourceModel.Expr startVertexFn, SourceModel.Expr finishVertexFn, SourceModel.Expr init, SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldInDepthFirstSearchOrder), startVertexFn, finishVertexFn, init, graph});
		}

		/**
		 * Name binding for function: foldInDepthFirstSearchOrder.
		 * @see #foldInDepthFirstSearchOrder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldInDepthFirstSearchOrder = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"foldInDepthFirstSearchOrder");

		/**
		 * Fold across graph vertices reachable from the specified root in depth-first
		 * search order.
		 * <p>
		 * <strong>Note</strong>: this is simply an alias for <code>Cal.Utilities.DirectedGraph.foldReachableInDepthFirstSearchOrder</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.foldDFS, Cal.Utilities.DirectedGraph.foldInDepthFirstSearchOrder, Cal.Utilities.DirectedGraph.foldReachableInDepthFirstSearchOrder
		 * </dl>
		 * 
		 * @param startVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex at which to begin the traversal.
		 * @param startVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is visited for the first time.
		 * Guaranteed to be called exactly once for each reachable vertex.
		 * @param finishVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is finished (all children are finished).
		 * Guaranteed to be called exactly once for each reachable vertex.
		 * @param init (CAL type: <code>b</code>)
		 *          the initial accumulator value (returned directly if the graph is empty).
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to fold across.
		 * @return (CAL type: <code>b</code>) 
		 *          The accumulated value after the final vertex is finished.
		 */
		public static final SourceModel.Expr foldReachableDFS(SourceModel.Expr startVertex, SourceModel.Expr startVertexFn, SourceModel.Expr finishVertexFn, SourceModel.Expr init, SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldReachableDFS), startVertex, startVertexFn, finishVertexFn, init, graph});
		}

		/**
		 * Name binding for function: foldReachableDFS.
		 * @see #foldReachableDFS(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldReachableDFS = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"foldReachableDFS");

		/**
		 * Fold across graph vertices reachable from the specified root in depth-first
		 * search order.
		 * <p>
		 * <strong>Note</strong>: for convenience, you may wish to call this function as <code>Cal.Utilities.DirectedGraph.foldReachableDFS</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.foldDFS, Cal.Utilities.DirectedGraph.foldInDepthFirstSearchOrder, Cal.Utilities.DirectedGraph.foldReachableDFS
		 * </dl>
		 * 
		 * @param startVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex at which to begin the traversal.
		 * @param startVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is visited for the first time.
		 * Guaranteed to be called exactly once for each reachable vertex.
		 * @param finishVertexFn (CAL type: <code>Cal.Core.Prelude.Eq a => b -> a -> b</code>)
		 *          called when a vertex is finished (all children are finished).
		 * Guaranteed to be called exactly once for each reachable vertex.
		 * @param init (CAL type: <code>b</code>)
		 *          the initial accumulator value (returned directly if the graph is empty).
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to fold across.
		 * @return (CAL type: <code>b</code>) 
		 *          The accumulated value after the final vertex is finished.
		 */
		public static final SourceModel.Expr foldReachableInDepthFirstSearchOrder(SourceModel.Expr startVertex, SourceModel.Expr startVertexFn, SourceModel.Expr finishVertexFn, SourceModel.Expr init, SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldReachableInDepthFirstSearchOrder), startVertex, startVertexFn, finishVertexFn, init, graph});
		}

		/**
		 * Name binding for function: foldReachableInDepthFirstSearchOrder.
		 * @see #foldReachableInDepthFirstSearchOrder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldReachableInDepthFirstSearchOrder = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"foldReachableInDepthFirstSearchOrder");

		/**
		 * Returns the number of edges in the specified graph.
		 * @param graph (CAL type: <code>Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph whose edge count is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of edges in the graph.
		 */
		public static final SourceModel.Expr getEdgeCount(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getEdgeCount), graph});
		}

		/**
		 * Name binding for function: getEdgeCount.
		 * @see #getEdgeCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getEdgeCount = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "getEdgeCount");

		/**
		 * Returns the list of out-neighbours of the specified vertex.  No particular
		 * order is guaranteed.
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to find neighbours.
		 * @param vertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex whose neighbours are sought.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          a list of out-neighbours of the specified vertex.
		 */
		public static final SourceModel.Expr getNeighbours(SourceModel.Expr graph, SourceModel.Expr vertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getNeighbours), graph, vertex});
		}

		/**
		 * Name binding for function: getNeighbours.
		 * @see #getNeighbours(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getNeighbours = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "getNeighbours");

		/**
		 * Returns the number of vertices in the specified graph.
		 * @param graph (CAL type: <code>Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph whose vertex count is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of vertices in the graph.
		 */
		public static final SourceModel.Expr getVertexCount(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getVertexCount), graph});
		}

		/**
		 * Name binding for function: getVertexCount.
		 * @see #getVertexCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getVertexCount = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "getVertexCount");

		/**
		 * Returns a list of the vertices in the specified graph.  No particular order
		 * is guaranteed.
		 * @param graph (CAL type: <code>Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph whose vertices are to be returned.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of vertices in the graph.
		 */
		public static final SourceModel.Expr getVertices(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getVertices), graph});
		}

		/**
		 * Name binding for function: getVertices.
		 * @see #getVertices(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getVertices = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "getVertices");

		/**
		 * Returns a list of the vertices in the specified graph.  The vertices will
		 * be ordered by insertion time.
		 * @param graph (CAL type: <code>Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph whose vertices are to be returned.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of vertices in the graph.
		 */
		public static final SourceModel.Expr getVerticesInInsertionOrder(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getVerticesInInsertionOrder), graph});
		}

		/**
		 * Name binding for function: getVerticesInInsertionOrder.
		 * @see #getVerticesInInsertionOrder(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getVerticesInInsertionOrder = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"getVerticesInInsertionOrder");

		/**
		 * Returns whether the specified graph is the empty graph (i.e. contains no
		 * vertices).
		 * @param graph (CAL type: <code>Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the graph is empty; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isEmpty(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmpty), graph});
		}

		/**
		 * Name binding for function: isEmpty.
		 * @see #isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmpty = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "isEmpty");

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
		 * @param vertices (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the vertices of the graph to be constructed.
		 * @param edges (CAL type: <code>Cal.Core.Prelude.Eq a => [(a, a)]</code>)
		 *          the edges of the graph to be constructed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a graph containing the specified vertices and edges.
		 */
		public static final SourceModel.Expr makeGraph(SourceModel.Expr vertices, SourceModel.Expr edges) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeGraph), vertices, edges});
		}

		/**
		 * Name binding for function: makeGraph.
		 * @see #makeGraph(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeGraph = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "makeGraph");

		/**
		 * Constructs a graph containing the specified vertices.  For each pair of vertices
		 * <code>v1</code> and <code>v2</code>, the graph will contain contain an edge <code>(v1, v2)</code>
		 * if and only if <code>existsEdgeFn v1 v2</code> returns <code>Cal.Core.Prelude.True</code>.
		 * <p>
		 * <strong>Note</strong>: The order of the vertices is preserved.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.addEdges
		 * </dl>
		 * 
		 * @param vertices (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the vertices of the graph to be constructed.
		 * @param existsEdgeFn (CAL type: <code>Cal.Core.Prelude.Eq a => a -> a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate function indicating, for each ordered-pair of
		 * vertices in the graph, whether an edge exists from one to the other.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a graph containing the specified vertices and the edges induced by
		 * the specified predicate function.
		 */
		public static final SourceModel.Expr makePredicateGraph(SourceModel.Expr vertices, SourceModel.Expr existsEdgeFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makePredicateGraph), vertices, existsEdgeFn});
		}

		/**
		 * Name binding for function: makePredicateGraph.
		 * @see #makePredicateGraph(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makePredicateGraph = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"makePredicateGraph");

		/**
		 * Applies the specified function to each vertex in the specified graph.
		 * <p>
		 * <strong>Note</strong>: If two vertices have the same image under the specified function,
		 * then they will be merged (retaining self-loops created during the merge).
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.mergeVertices
		 * </dl>
		 * 
		 * @param mapFn (CAL type: <code>(Cal.Core.Prelude.Eq a, Cal.Core.Prelude.Eq b) => a -> b</code>)
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq b => Cal.Utilities.DirectedGraph.DirectedGraph b</code>) 
		 *          the graph that results when the specified function is applied to each
		 * vertex in the specified graph.
		 */
		public static final SourceModel.Expr map(SourceModel.Expr mapFn, SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), mapFn, graph});
		}

		/**
		 * Name binding for function: map.
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "map");

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
		 * <strong>Note</strong>: Throws an <code>Cal.Core.Prelude.error</code> if either <code>vertex1</code> or
		 * <code>vertex2</code> is not in the graph. 
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph in which to merge the vertices.
		 * @param retainLoops (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          if vertices A and B are merged to C and the graph contains
		 * (A, B) or (B, A), then (C, C) will be added to the new graph if retainLoops
		 * is true.  Note that if the graph contains (A, A) or (B, B), then the new
		 * graph will contain (C, C) regardless of the value of retainLoops.
		 * @param vertex1 (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the first vertex to be merged (error if invalid).
		 * @param vertex2 (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the second vertex to be merged (error if invalid).
		 * @param mergedVertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the resulting vertex.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a new graph with the two vertices merged.
		 */
		public static final SourceModel.Expr mergeVertices(SourceModel.Expr graph, SourceModel.Expr retainLoops, SourceModel.Expr vertex1, SourceModel.Expr vertex2, SourceModel.Expr mergedVertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mergeVertices), graph, retainLoops, vertex1, vertex2, mergedVertex});
		}

		/**
		 * @see #mergeVertices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param graph
		 * @param retainLoops
		 * @param vertex1
		 * @param vertex2
		 * @param mergedVertex
		 * @return the SourceModel.Expr representing an application of mergeVertices
		 */
		public static final SourceModel.Expr mergeVertices(SourceModel.Expr graph, boolean retainLoops, SourceModel.Expr vertex1, SourceModel.Expr vertex2, SourceModel.Expr mergedVertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mergeVertices), graph, SourceModel.Expr.makeBooleanValue(retainLoops), vertex1, vertex2, mergedVertex});
		}

		/**
		 * Name binding for function: mergeVertices.
		 * @see #mergeVertices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mergeVertices = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "mergeVertices");

		/**
		 * Since most clients of foldDFS will not want to use all of the function arguments,
		 * this is provided as a convenient default (simply passes the accumulated value
		 * through to the next handler).
		 * @param arg_1 (CAL type: <code>a</code>)
		 * @param arg_2 (CAL type: <code>b</code>)
		 * @return (CAL type: <code>a</code>) 
		 */
		public static final SourceModel.Expr noChange(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.noChange), arg_1, arg_2});
		}

		/**
		 * Name binding for function: noChange.
		 * @see #noChange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName noChange = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "noChange");

		/**
		 * Partitions the vertices into two sets and then removes all edges from one set
		 * to the other.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.DirectedGraph.filter
		 * </dl>
		 * 
		 * @param partitionFn (CAL type: <code>Cal.Core.Prelude.Eq a => a -> Cal.Core.Prelude.Boolean</code>)
		 *          a function which returns <code>Cal.Core.Prelude.True</code> if a vertex belongs
		 * in the first subgraph and <code>Cal.Core.Prelude.False</code> if a vertex belongs in the second
		 * subgraph.
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to partition.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => (Cal.Utilities.DirectedGraph.DirectedGraph a, Cal.Utilities.DirectedGraph.DirectedGraph a)</code>) 
		 *          the two subgraphs induced by the partition function.
		 */
		public static final SourceModel.Expr partition(SourceModel.Expr partitionFn, SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.partition), partitionFn, graph});
		}

		/**
		 * Name binding for function: partition.
		 * @see #partition(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName partition = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "partition");

		/**
		 * Removes an edge from a graph.  If the specified edge is already absent from
		 * the graph (perhaps because one of the endpoints is absent from the graph),
		 * then the resulting graph will be the same as the original.
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph from which the edge will be removed.
		 * @param edge (CAL type: <code>Cal.Core.Prelude.Eq a => (a, a)</code>)
		 *          the edge to be removed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a graph containing the same vertices and edges as the original, with
		 * the possible exception of the specified edge.
		 */
		public static final SourceModel.Expr removeEdge(SourceModel.Expr graph, SourceModel.Expr edge) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeEdge), graph, edge});
		}

		/**
		 * Name binding for function: removeEdge.
		 * @see #removeEdge(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeEdge = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "removeEdge");

		/**
		 * Removes a new vertex from a graph.  If the specified vertex is already absent
		 * from the graph, then the resulting graph will be the same as the original.
		 * @param oldGraph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph from which the vertex will be removed.
		 * @param vertex (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the vertex to be removed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          a graph containing the same vertices and edges as the original, with
		 * the possible exception of the specified vertex.
		 */
		public static final SourceModel.Expr removeVertex(SourceModel.Expr oldGraph, SourceModel.Expr vertex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeVertex), oldGraph, vertex});
		}

		/**
		 * Name binding for function: removeVertex.
		 * @see #removeVertex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeVertex = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "removeVertex");

		/**
		 * Reverses all of the edges in a graph (sometimes referred to as the transpose
		 * graph).
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to reverse.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>) 
		 *          the graph with all edges reversed.
		 */
		public static final SourceModel.Expr reverse(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reverse), graph});
		}

		/**
		 * Name binding for function: reverse.
		 * @see #reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reverse = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "reverse");

		/**
		 * Returns the vertices in topological order if the graph is acyclic and in an
		 * unspecified order otherwise.  If the relative order of two vertices is not
		 * specified by the graph, then their insertion order will be used.
		 * <p>
		 * If the graph contains cycles, then calling <code>Cal.Utilities.DirectedGraph.flattenComponents</code> before
		 * <code>Cal.Utilities.DirectedGraph.stableTopologicalSort</code> will produce the desired stable order.
		 * <p>
		 * <strong>Note</strong>: this may be quite a bit slower than a normal topological sort.
		 * <p>
		 * Algorithm adapted from Exercise 22.4-5 on p. 552 of 
		 * "Introduction to Algorithms 2E"
		 * by Cormen, Leiserson, Rivest, and Stein (2002).
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to be sorted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          an ordered list of vertices.
		 */
		public static final SourceModel.Expr stableTopologicalSort(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stableTopologicalSort), graph});
		}

		/**
		 * Name binding for function: stableTopologicalSort.
		 * @see #stableTopologicalSort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stableTopologicalSort = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"stableTopologicalSort");

		/**
		 * Returns a topologically sorted list of strongly-connected components of a
		 * specified graph (i.e. if A and B are SCCs and A precedes B in the returned
		 * list, then the graph contains no edges from vertices in A to vertices in B).
		 * <p>
		 * Algorithm based on STRONGLY-CONNECTED-COMPONENTS on p. 554 of 
		 * "Introduction to Algorithms 2E"
		 * by Cormen, Leiserson, Rivest, and Stein (2002).
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to be broken into components.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [[a]]</code>) 
		 *          a topologically sorted list of strongly-connected components of the
		 * specified graph.
		 */
		public static final SourceModel.Expr stronglyConnectedComponents(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stronglyConnectedComponents), graph});
		}

		/**
		 * Name binding for function: stronglyConnectedComponents.
		 * @see #stronglyConnectedComponents(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stronglyConnectedComponents = 
			QualifiedName.make(
				CAL_DirectedGraph.MODULE_NAME, 
				"stronglyConnectedComponents");

		/**
		 * Returns the vertices of a graph in topological order if the graph is acyclic
		 * and in an unspecified order otherwise.
		 * <p>
		 * Algorithm based on TOPOLOGICAL-SORT on p. 550 of 
		 * "Introduction to Algorithms 2E"
		 * by Cormen, Leiserson, Rivest, and Stein (2002).
		 * 
		 * @param graph (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.DirectedGraph.DirectedGraph a</code>)
		 *          the graph to be sorted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          an ordered list of vertices.
		 */
		public static final SourceModel.Expr topologicalSort(SourceModel.Expr graph) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.topologicalSort), graph});
		}

		/**
		 * Name binding for function: topologicalSort.
		 * @see #topologicalSort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName topologicalSort = 
			QualifiedName.make(CAL_DirectedGraph.MODULE_NAME, "topologicalSort");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 2095724475;

}
