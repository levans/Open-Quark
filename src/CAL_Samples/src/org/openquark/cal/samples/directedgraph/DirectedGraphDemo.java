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
 * DirectedGraphDemo.java
 * Created: Oct 15, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.samples.directedgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.DebugSupport;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.lecc.StandaloneRuntime;
import org.openquark.util.Pair;

/**
 * This class contains some demos of the functionality exposed via the {@link ImmutableDirectedGraph} class, which
 * is a Java-generics friendly facade on top of the Java API provided by the standalone CAL library JAR built on
 * top of the API module {@code Cal.Samples.DirectedGraphLibrary}.
 * <p>
 * Some examples are based on the work in the CAL module {@code Cal.Test.Utilities.DirectedGraph_Tests}.
 * <p>
 * For more information on the generation and use of standalone JARs, please refer to the document
 * "Using Quark with Standalone JARs" included in the distribution.
 * 
 * @see "Using Quark with Standalone JARs"
 * @see ImmutableDirectedGraph
 * @see org.openquark.cal.samples.directedgraph.DirectedGraphLibrary
 *
 * @author Andrew Casey
 * @author Joseph Wong
 */
public final class DirectedGraphDemo {
    
    /** An execution context for the various demos. */
    private static final ExecutionContext executionContext = StandaloneRuntime.makeExecutionContext(DirectedGraphDemo.class);
    
    /**
     * The main method.
     * @param args command line arguments.
     */
    public static void main(final String[] args) throws CALExecutorException {
        
        simpleGraphDemo();
        factorsAndMultiplesDemo();
        cycleTests();
    }

    /**
     * Some simple demos of the directed graph class.
     */
    private static void simpleGraphDemo() throws CALExecutorException {
        
        // Construct a graph by starting with an empty graph and adding edges and vertices to it.
        final ImmutableDirectedGraph<String> graph1 = ImmutableDirectedGraph
            .<String>emptyGraph(executionContext)
            .addEdge(Pair.make("one", "two"))
            .addVertex("three");
        
        // graph2 is graph1 with one more edge - this does not modify graph1
        final ImmutableDirectedGraph<String> graph2 = graph1.addEdge(Pair.make("two", "three"));
        
        // Let's look at graph2, both via its toString() implementation
        System.out.println(graph2);
        // ...and via the support to show the structure of a CalValue
        System.out.println(DebugSupport.showInternal(graph2.getCalValue()));
        
        // removeEdge does not modify graph1
        System.out.println(graph1.removeEdge(Pair.make("one", "two")));
        // ...as can be witnessed by displaying graph1
        System.out.println(graph1);
    }

    /**
     * A demo based on constructing graphs of integers where edges correspond to the divisibility relation.
     */
    private static void factorsAndMultiplesDemo() throws CALExecutorException {
        
        // Make a list of the Integers 1..100 as vertices
        final List<Integer> ints = new ArrayList<Integer>();
        for (int i = 1; i <= 100; i++) {
            ints.add(Integer.valueOf(i));
        }
        
        // Construct a graph based on the vertices, and an edge a->b if b is a factor of a.
        final ImmutableDirectedGraph<Integer> factorsGraph = ImmutableDirectedGraph
            .makePredicateGraph(
                ints,
                new Predicate<Pair<Integer, Integer>>() {
                    public boolean apply(Pair<Integer, Integer> value) {
                        return value.fst().intValue() % value.snd().intValue() == 0;
                    }},
                executionContext);
        
        System.out.println(factorsGraph);
        
        // Reversing the graph, we now have a graph with edges a->b for each pair (a,b) where b is a multiple of a.
        final ImmutableDirectedGraph<Integer> multiplesGraph = factorsGraph.reverse();
        
        System.out.println(multiplesGraph);
        
        // We filter the multiplesGraph to contain only vertices that are perfect squares
        final ImmutableDirectedGraph<Integer> squaresGraph = multiplesGraph.filter(new Predicate<Integer>() {
            public boolean apply(Integer vertex) {
                // a perfect square has an odd number of factors
                try {
                    return factorsGraph.getNeighbours(vertex).size() % 2 == 1;
                } catch (CALExecutorException e) {
                    throw new IllegalStateException(e);
                }
            }});
        
        System.out.println(squaresGraph);
    }
    
    /**
     * Tests graphs with and without cycles
     */
    @SuppressWarnings("unchecked")
    private static void cycleTests() throws CALExecutorException {
        
        final ImmutableDirectedGraph<Integer> noCycles = ImmutableDirectedGraph
            .makeGraph(
                Collections.<Integer>emptyList(),
                Arrays.asList(Pair.make(1, 2), Pair.make(2, 3), Pair.make(1, 3)),
                executionContext);
        
        final ImmutableDirectedGraph<Integer> lengthOneCycle = ImmutableDirectedGraph
            .makeGraph(
                Collections.<Integer>emptyList(),
                Arrays.asList(Pair.make(1, 1)),
                executionContext);
        
        final ImmutableDirectedGraph<Integer> lengthTwoCycle = ImmutableDirectedGraph
            .makeGraph(
                Collections.<Integer>emptyList(),
                Arrays.asList(Pair.make(1, 2), Pair.make(2, 1)),
                executionContext);
        
        System.out.println("noCycle.findCycle(): " + noCycles.findCycle());
        System.out.println("oneCycle.findCycle(): " + lengthOneCycle.findCycle());
        System.out.println("twoCycle.findCycle(): " + lengthTwoCycle.findCycle());
    }
}
