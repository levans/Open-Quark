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
 * ExplorerRootNode.java
 * Creation date: Jan 21st 2003.
 * By: Ken Wong
 */

package org.openquark.gems.client.explorer;

import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openquark.gems.client.CodeGem;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.gems.client.ValueGem;


/**
 * This node is used as the root of the TableTopExplorer
 * @author Ken Wong
 * Creation Date: Jan 20th 2003
 */
class ExplorerRootNode extends DefaultMutableTreeNode {
    
    private static final long serialVersionUID = -2565881309745988573L;

    /** The icon used for this node */
    static final ImageIcon rootNodeIcon = new ImageIcon(Object.class.getResource("/Resources/gemcutter_16.gif"));
    
    /** The comparator used to sort the tree */
    private static final Comparator<Gem> rootSorter = new Comparator<Gem>() {
        /**
         * @see java.util.Comparator#compare(Object, Object)
         * We want to emulate the behavior of the run menu, so we give the Collectors priority
         * in sorting. 
         */
        public int compare(Gem o1, Gem o2) {
            String name1 = o1.toString();
            String name2 = o2.toString();
            if (o1 instanceof FunctionalAgentGem) {
                FunctionalAgentGem functionalAgentGem = (FunctionalAgentGem) o1;
                name1 = functionalAgentGem.getName().getUnqualifiedName();
            } else if (o1 instanceof CodeGem) {
                name1 = ((CodeGem)o1).getUnqualifiedName();
            } else if (o1 instanceof ValueGem) {
                name1 = "value";
            } else if (o1 instanceof CollectorGem) {
                name1 = ((CollectorGem)o1).getUnqualifiedName();
                if (o2 instanceof CollectorGem) {
                    name2 = ((CollectorGem) o2).getUnqualifiedName();
                    return name1.compareTo(name2);
                } else {
                    return -1;
                }
                
            } else if (o1 instanceof ReflectorGem) {
                name1 = ((ReflectorGem)o1).getUnqualifiedName();
                if (o2 instanceof ReflectorGem) {
                    name2 = ((ReflectorGem) o2).getUnqualifiedName();
                    return name1.compareTo(name2);
                } else {
                    return 1;
                }
                
            }
            
            if (o2 instanceof FunctionalAgentGem) {
                FunctionalAgentGem functionalAgentGem = (FunctionalAgentGem) o2;
                name2 = functionalAgentGem.getName().getUnqualifiedName();
            } else if (o2 instanceof CodeGem) {
                name2 = ((CodeGem)o2).getUnqualifiedName();
            } else if (o2 instanceof ValueGem) {
                name2 = "value";
            } else if (o2 instanceof CollectorGem) {
                return 1;
            } else if (o2 instanceof ReflectorGem) {
                return -1;
            }
            
            return name1.compareTo(name2);
        }
    };

    /**
     * Constructor for ExplorerRootNode.
     * @param rootNodeName
     */
    public ExplorerRootNode(String rootNodeName) {
        super(rootNodeName);
    }
    
    /**
     * @see javax.swing.tree.DefaultMutableTreeNode#add(javax.swing.tree.MutableTreeNode)
     * This method searches the children (using binary search) and inserts the gem in the right place
     */
    public void add(ExplorerGemNode gemNode){
        Gem gem1 = gemNode.getGem();
        int lowerBound = 0;
        int upperBound = getChildCount();
        while (upperBound - lowerBound > 3) {
            int i = (upperBound + lowerBound) / 2;
            int results =  rootSorter.compare(gem1, ((ExplorerGemNode)getChildAt(i)).getGem());
            if (results > 0) {
                lowerBound = i;
            } else {
                upperBound = i;
            }
        }
        insertSearchingFrom(gemNode, lowerBound);
    }
    
    /**
     * Inserts the gems in the right place based on the rootSorter, starting the search from the 
     * i specified
     * @param gemNode
     * @param startingIndex the starting index
     */
    private void insertSearchingFrom(ExplorerGemNode gemNode, int startingIndex){
        Gem gem = gemNode.getGem();
        
        for (int size = getChildCount(); startingIndex < size; startingIndex++) {
            if (rootSorter.compare(gem, ((ExplorerGemNode)getChildAt(startingIndex)).getGem()) < 0) {
                break;
            }
        }
        insert(gemNode, startingIndex);
     }
}
