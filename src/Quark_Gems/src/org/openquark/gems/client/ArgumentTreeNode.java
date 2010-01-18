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
 * ArgumentTreeNode.java
 * Creation date: May 26, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * The TreeNode class used by the ArgumentTreeModel.
 * @author Edward Lam
 */
public abstract class ArgumentTreeNode extends DefaultMutableTreeNode {
    
    /**
     * Constructor for an ArgumentTreeNode
     * @param userObject
     * @param allowsChildren
     */
    ArgumentTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }
    
    /**
     * The TreeNode class used to represent a collector gem.
     * @author Edward Lam
     */
    public static class CollectorNode extends ArgumentTreeNode {
        private static final long serialVersionUID = -7223399304620549059L;

        /**
         * Constructor for a CollectorNode.
         * @param collectorGem
         */
        CollectorNode(CollectorGem collectorGem) {
            super(collectorGem, true);
        }
        
        /**
         * @return the collector gem associated with this node.
         */
        CollectorGem getCollectorGem() {
            return (CollectorGem)getUserObject();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return ((CollectorGem)getUserObject()).getUnqualifiedName();
        }
    }
    
    /**
     * The TreeNode class used to represent an argument.
     * @author Edward Lam
     */
    public static class ArgumentNode extends ArgumentTreeNode {
        private static final long serialVersionUID = -1894659350579182028L;

        /**
         * Constructor for an ArgumentNode.
         * @param input
         */
        ArgumentNode(Gem.PartInput input) {
            super(input, false);
        }

        /**
         * Get the argument associated with this node.
         * @return Gem.PartInput
         */
        Gem.PartInput getArgument() {
            return (Gem.PartInput)getUserObject();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return ((Gem.PartInput)getUserObject()).getNameString();
        }
    }
    
    /**
     * The root TreeNode.
     * @author Edward Lam
     */
    public static class RootNode extends ArgumentTreeNode {
        private static final long serialVersionUID = -3264092692345922722L;

        /**
         * Constructor for a RootNode.
         */
        RootNode() {
            super(null, true);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "RootNode";
        }
    }
}
