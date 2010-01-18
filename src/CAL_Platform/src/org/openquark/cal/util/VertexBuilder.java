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
 * VertexBuilder.java
 * Creation date: (August 28, 2000)
 * By: Bo Ilic
 */
package org.openquark.cal.util;

import java.util.HashSet;
import java.util.Set;

/**
 * A helper class used for creating vertices that are part of graphs. If vertices a and b
 * both have dependee c, then in the actual graph, there must be only 1 vertex c object.
 * The vertex builder allows you to refer to the dependees by name "c", and have them
 * built into the vertices of a graph latter.
 * <p>
 * The type parameter <code>T</code> this is the type of the data (usually a name like a function name or
 * a module name) encapsulated by the graph vertices.
 *
 * Creation date: (8/28/00 4:42:04 PM)
 * @author Bo Ilic
 */
public class VertexBuilder<T> {
    
    /** The name of the vertex which will be constructed. */
    private final T name;

    /** The (T) names of the immediate dependees of this vertex */
    private final Set<T> dependeeNames;
    
    /**
     * Insert the method's description here.
     * Creation date: (8/28/00 4:45:28 PM)
     * @param name
     * @param dependeeNamesArray
     */
    public VertexBuilder(T name, T[] dependeeNamesArray) {
        if (name == null || dependeeNamesArray == null) {
            throw new NullPointerException();
        }
        
        this.name = name;
        dependeeNames = new HashSet<T>();

        for (int i = 0, nDependees = dependeeNamesArray.length; i < nDependees; ++i) {
            dependeeNames.add(dependeeNamesArray[i]);
        }
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (8/28/00 4:45:28 PM)
     * @param name
     * @param dependeeNames
     */
    public VertexBuilder(T name, Set<T> dependeeNames) {
        if (name == null || dependeeNames == null) {
            throw new NullPointerException();
        }
                
        this.name = name;
        this.dependeeNames = dependeeNames;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (8/28/00 4:43:25 PM)
     * @return Set
     */
    public final Set<T> getDependeeNames() {
        return dependeeNames;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (8/28/00 4:42:34 PM)
     * @return String
     */
    public final T getName() {
        return name;
    }    
    
    /**
     * Creates a textual representation of the VertexBuilder node.
     * vertexName {dependeeName1,... dependeeNameN}
     *
     * Note: the curly brackets are used to indicate that the dependees are unordered.
     * 
     * @return String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(name.toString());
             
        sb.append(" {");
        int i = 0;
        for (final T dependeeName : dependeeNames) {
            
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(dependeeName);
            
            ++i;
        }
        sb.append('}');

        return sb.toString();
    }
}