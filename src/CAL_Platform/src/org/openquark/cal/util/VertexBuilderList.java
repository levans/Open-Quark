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
 * VertexBuilderList.java
 * Creation date: (August 29, 2000)
 * By: Bo Ilic
 */
package org.openquark.cal.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A list of VertexBuilder objects that can be used to easily construct a graph.
 * Creation date: (8/29/00 2:46:48 PM)
 * <p>
 * The type parameter <code>T</code> this is the type of the data (usually a name like a function name or
 * a module name) encapsulated by the graph vertices.
 * @author Bo Ilic
 */
public final class VertexBuilderList<T> {
    
    private final List<VertexBuilder<T>> list;
    
    public VertexBuilderList() {
        list = new ArrayList<VertexBuilder<T>>();
    }
        
    public void add(VertexBuilder<T> vertexWithConnections) {
        list.add (vertexWithConnections);
    }
    
    public VertexBuilder<T> get(int i) {
        return list.get(i);
    }
    
    public void clear() {
        list.clear();
    }
    
    public int size() {
        return list.size();
    }
                    
    public boolean makesValidGraph () {
        return makesValidGraph (null);
    }
    /**
     * Checks that this VertexBuilderList can be used to make a well-defined Graph object.
     *
     * Creation date: (8/30/00 1:26:11 PM)
     * @param moduleFunctionNameSet - A set of the names of functions defined in the current module.
     * @return boolean
     */
    public boolean makesValidGraph(Set<T> moduleFunctionNameSet) {

        //Check that there are no VertexBuilder objects with the same vertex names. 
        //(This corresponds to redefining a sc in the case of sc dependency analysis).

        Set<T> vertexNamesSet = new HashSet<T>();
        int nVertices = list.size();
        for (int i = 0; i < nVertices; ++i) {
            
            T vertexName = get(i).getName();
            if (!vertexNamesSet.add(vertexName)) {
                return false;
            }
        }

        //Check that every dependeeName actually is either a vertexName for some vertex or
        // a name contained in the list of SC names for the current module.
        //(This corresponds to using an undefined sc within your sc definition).               
        for (int i = 0; i < nVertices; ++i) {
            
            VertexBuilder<T> vertex = get(i);
            Set<T> dependeeNamesSet = vertex.getDependeeNames();

            if (!vertexNamesSet.containsAll(dependeeNamesSet) && (moduleFunctionNameSet == null || !moduleFunctionNameSet.containsAll(dependeeNamesSet))) {
                return false;
            }
        }

        return true;
    }
    
    @Override
    public String toString() {
        
        StringBuilder result = new StringBuilder();
        for (int i = 0, n = list.size(); i < n; ++i) {
            result.append(list.get(i));
            if (i < n - 1) {
                result.append('\n');
            }
        }
        
        return result.toString();
    }
}