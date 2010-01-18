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
 * Creation date: (02/15/02 12:47:22 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A class used to represent a directed Graph.
 * For now this is used to enable graph rearrangement.
 *
 * Creation date: (02/15/02 12:47:22 PM)
 * @author Edward Lam
 */
class Graph {
    
    /**
     * The interface for all generic node type information
     * Creation date: (11/7/01 9:37:37 AM)
     * @author Luke Evans
     */
    interface Node {
        /**
         * Set the location of this Node
         * Creation date: (12/5/00 10:21:00 PM)
         * @param newXY Point the new location
         */
        public void setLocation(Point newXY);
        
        /**
         * Get the location of this Gem
         * Creation date: (12/5/00 10:21:00 PM)
         * @return newXY Point the location
         */
        public Point getLocation();
        
        /**
         * Get the degree of this graph node.
         * Creation date: (11/8/01 3:45:00 PM)
         * @return double
         */
        public double degree ();
        
        /**
         * Get an iterator over the incoming edges.
         * Creation date: (11/8/01 3:45:00 PM)
         * @param validNodes the nodes to be considered
         * @return Iterator
         */
        public Iterator<Edge> getInEdges (List<Node> validNodes);
        
        /**
         * Get an iterator over the outgoing edges.
         * Creation date: (11/8/01 3:45:00 PM)
         * @param validNodes the nodes to be considered
         * @return Iterator
         */
        public Iterator<Edge> getOutEdges (List<Node> validNodes);
        
        /**
         * Get the bounds of the node.
         */
        public java.awt.Rectangle getBounds();
        
        public String getDisplayText ();
        
    }
    
    /**
     * The interface for all generic edge type information
     * Creation date: (11/8/01 9:37:37 AM)
     * @author Raymond Cypher
     */
    interface Edge {
        /**
         * Get the source node of this edge.
         * Creation date: (11/8/01 9:37:37 AM)
         * @return Node
         */
        public Node getSourceNode ();
        
        /**
         * Get the destination node of this edge.
         * Creation date: (11/8/01 9:37:37 AM)
         * @return Node
         */
        public Node getTargetNode ();
        
        /**
         * Get the x/y coordinates of the point where this
         * edge connects to the target.
         * Creation date: (11/8/01 9:37:37 AM)
         * @return Node
         */
        public Point getTargetConnectionPoint ();
        
        /**
         * Get the x/y coordinates of the point where this
         * edge connects to the source.
         * Creation date: (11/8/01 9:37:37 AM)
         * @return Node
         */
        public Point getSourceConnectionPoint ();
        
    }
    
    /**
     * LayoutArranger attempts to tidy the GemGraph by 'recrystalising it'.
     * @author Raymond Cypher
     */
    public static class LayoutArranger {
        
        // all the nodes to be arranged
        private final Node [] nodes;
        
        // The 'gaps' between each gem.
        private static final int hGap = DisplayConstants.HALO_BLUR_SIZE * 4;
        private static final int vGap = DisplayConstants.HALO_BLUR_SIZE * 2;
        
        // The roots and trees stored in layout arranger.
        private final List<Node> roots = new ArrayList<Node>();
        
        /** the list of nodes that the trees contain */
        private final List<Node> validNodes;
        
        /**
         * Default Constructor for LayoutArranger
         * @param nodes the list of validNodes
         */
        public LayoutArranger (Node[] nodes) {
            
            this.validNodes = Arrays.asList(nodes);
            
            this.nodes = nodes.clone();
        }
        
        /**
         * This function sorts the tree objects in the given list in order of their Y
         * coordinates. So the tree with the lowest Y coordinate is placed first
         * @param trees the trees to be sorted
         */
        static void sortTreesOnYOrder(List<Tree> trees) {
            
            Comparator<Tree> treeComparator = new Comparator<Tree>() {
                /**
                 * @see java.util.Comparator#compare(Object, Object)
                 */
                public int compare(Tree tree1, Tree tree2) {
                    Rectangle bounds1 = tree1.getBounds();
                    Rectangle bounds2 = tree2.getBounds();
                    return bounds1.y - bounds2.y;
                }
            };
            
            Collections.sort(trees, treeComparator);
        }
        
        /**
         * This function finds all the roots in the validNode Set
         */
        private void findRoots() {
            for (int i = 0; i < nodes.length; ++i) {
                if (!nodes [i].getOutEdges(validNodes).hasNext()) {
                    roots.add (nodes [i]);
                }
            }
        }
        
        /**
         * This function rearranges the graph so that it will be 'neater'
         */
        void arrangeGraph () {
            findRoots();
            
            try {
                // Builds the trees, and them in one function
                createAndRearrangeTrees();
            } catch (Exception e) {
                System.out.println ("Exception arranging trees: " + e);
            }
        }
        
        /**
         * Creates the tree and add them to the vector
         */
        private void createAndRearrangeTrees () {
            for (int i = 0; i < roots.size (); ++i) {
                Node gn = roots.get (i);
                new Tree (gn, true);        // The constructor arranges the tree..
            }
        }
        
        /**
         * Creates the tree and add them to the vector
         * @return List
         */
        List<Tree> getTrees () {
            List<Tree> existingTrees = new ArrayList<Tree>(); 
            findRoots();
            
            for (int i = 0; i < roots.size (); ++i) {
                Node gn = roots.get (i);
                existingTrees.add(new Tree(gn, false));
            }
            
            return existingTrees;
        }
        
        
        /**
         * A tree class that stores the structure of a tree. Used in the LayoutArranger for rearranging trees.]
         * Note: data is stored recursively. Each tree is stored with reference to subtrees. 
         * @author ?
         * Date Created: ?
         */
        class Tree {
            
            // To store more information regarding the tree.
            private final List<Tree> subTrees = new ArrayList<Tree> ();
            
            private final Node root;
            
            /** The complete bounds of this tree */
            private Rectangle bounds = null;        
            
            /**
             * Default constructor for the tree structure
             * @param root
             * @param rearrangeSubTree
             */
            Tree (Node root, boolean rearrangeSubTree) {
                this.root = root;
                
                for (Iterator<Edge> ins = root.getInEdges(validNodes); ins.hasNext(); ) {
                    Edge gc = ins.next ();
                    Node sub = gc.getSourceNode ();
                    
                    subTrees.add (new Tree(sub, rearrangeSubTree));
                }
                
                if (rearrangeSubTree) {
                    arrange ();
                } 
            }
            
            /**
             * returns all the nodes in this tree
             * @param nodes (the set that the nodes will be stored in)
             */
            void getNodes(Set<Node> nodes) {
                for (int i = 0, size = subTrees.size(); i < size; i++) {
                    Tree current = subTrees.get(i);
                    current.getNodes(nodes);
                }
                nodes.add(root);
            }
            
            /**
             * @return the root of the tree.
             */
            public Node getRoot() {
                return root;
            }
            
            /**
             * get the width of the trees
             * @return int
             */
            int getWidth () {
                return getBounds().width;
            }       
            
            /**
             * get the height of the trees
             * @return int
             */
            int getHeight () {
                return getBounds().height;
            }
            
            /**
             * get the bounds of the trees
             * @return Rectangle
             */
            Rectangle getBounds () {
                bounds = root.getBounds();
                for (int i = 0; i < subTrees.size(); i ++) {
                    bounds.add(subTrees.get(i).getBounds());
                }
                return bounds;
            }
            
            /**
             * Rearranges the tree
             */
            void arrange () {
                bounds = arrangeChildren ();
            }
            
            /**
             * Sets position of the root of the tree, and moves all its nodes respectively
             * @param x the x coordinate of the root node
             * @param y the y coordinate fo the root node
             */
            void setLocation (int x, int y) {
                
                // if the bounds have not been defined yet, then arrange the trees first
                if (bounds == null) {
                    getBounds();
                }
                int dx = x - bounds.x;
                int dy = y - bounds.y;
                move (dx, dy);
            }
            
            /**
             * Aligns the root of the tree with its children.
             * @param subTree the subtree to be aligned
             */
            void lineUpWithTarget (Tree subTree) {
                
                Node node = subTree.root;
                
                Iterator<Edge> oe = node.getOutEdges(validNodes);
                if (!oe.hasNext()) {
                    return;
                }
                //System.out.println ("lineUpWithTarget (" + node.getDisplayText () + ")");
                
                Edge oe1 = oe.next();
                //System.out.println (" sn bounds = " + node.getBounds () + ", tn bounds = " + target.getBounds ());
                //System.out.println ("    src conn pt = " + oe1.getSourceConnectionPoint () + " - target conn pt = " + oe1.getTargetConnectionPoint ());
                
                int vc1 = oe1.getSourceConnectionPoint ().y;
                int vc2 = oe1.getTargetConnectionPoint ().y;
                int deltaY = vc2 - vc1;
                
                int hc1 = oe1.getSourceConnectionPoint ().x;
                int hc2 = oe1.getTargetConnectionPoint ().x - hGap; 
                int deltaX = hc2 - hc1;
                
                subTree.move (deltaX, deltaY);
            }
            
            /**
             * Move the tree by the given deltas.
             * Creation date: (11/23/01 1:50:16 PM)
             * @param dx int
             * @param dy int
             */
            void move (int dx, int dy) {
                Point p = root.getLocation ();
                p.x += dx;
                p.y += dy;
                root.setLocation (p);
                
                bounds.x += dx;
                bounds.y += dy;
                
                Iterator<Tree> i = subTrees.iterator ();
                while (i.hasNext ()) {
                    Tree subTree = i.next ();
                    subTree.move (dx, dy);
                }
            }
            
            /**
             * Arranges the children of the given node and returns the bounds of the
             * tree represented by the node.
             * @return Rectangle
             */     
            Rectangle arrangeChildren () {
                
                int nSubTrees = subTrees.size ();
                
                if (nSubTrees == 0) {
                    return root.getBounds ();
                }
                
                int indent = -7;
                int nIndents;
                if ((nSubTrees % 2) == 0) {
                    nIndents = (nSubTrees - 2) / 2;
                } else {
                    nIndents = (nSubTrees - 1) / 2;
                }
                
                int upperLoopStart = -1;
                int lowerLoopStart = -1;
                double ceiling = -1;
                double floor = -1;
                
                int middleIndex = ((int)(nSubTrees / 2f + 0.5f)) - 1;    
                Tree middleSub = subTrees.get (middleIndex);
                lineUpWithTarget (middleSub);
                double middleSubBottom = middleSub.getBounds ().getMaxY();
                
                if ((nSubTrees % 2) == 0) {
                    
                    // Arrange the two middle nodes.
                    
                    Tree middleSub2 = subTrees.get (middleIndex + 1);
                    lineUpWithTarget (middleSub2);
                    
                    double middleSub2Top = middleSub2.getBounds ().getMinY();
                    
                    // Do they overlap?
                    if (middleSub2Top < (middleSubBottom + vGap)) {
                        
                        double diff = ((middleSubBottom + vGap) - middleSub2Top) /2;
                        middleSub.move (nIndents * indent, -1 * (int)diff);
                        middleSub2.move (nIndents * indent, (int)diff);
                    } else {
                        middleSub.move (nIndents * indent, 0);
                        middleSub2.move (nIndents * indent, 0);
                    }       
                    
                    // If the two subtrees are of different depth try to snug them up.
                    int msd = middleSub.getBounds ().x;
                    int ms2d = middleSub2.getBounds ().x;
                    int gap = 0;
                    if (msd > ms2d) {
                        gap = middleSub2.getUpperBoundAt (msd) - (int)(middleSub.getBounds ().getMaxY ());
                    } else {
                        gap = middleSub2.getBounds ().y - middleSub.getLowerBoundAt (ms2d);
                    }
                    
                    if (gap > vGap) {
                        gap -= vGap;
                        middleSub.move (0, gap / 2);
                        middleSub2.move (0, -1 * (gap / 2));
                    } 
                    
                    floor = middleSub2.getBounds ().getMaxY () + vGap;
                    ceiling = middleSub.getBounds ().getMinY () - vGap;
                    
                    upperLoopStart = middleIndex - 1;
                    lowerLoopStart = middleIndex + 2;
                    
                } else {
                    middleSub.move (nIndents * indent, 0);
                    
                    floor = middleSub.getBounds ().getMaxY () + vGap;
                    ceiling = middleSub.getBounds ().getMinY () - vGap;
                    
                    upperLoopStart = middleIndex - 1;
                    lowerLoopStart = middleIndex + 1;
                }
                
                // Now do the upper loop.  i.e. move up through the child connections
                // starting from the middle.
                int indentM = nIndents - 1;
                
                for (int i = upperLoopStart; i >= 0; --i) {
                    Tree subTree = subTrees.get (i);
                    lineUpWithTarget (subTree);
                    
                    int dx = indentM * indent;
                    indentM --;
                    
                    int dy = 0;             
                    if (subTree.getBounds ().getMaxY () > ceiling) {
                        dy = (int)(ceiling - subTree.getBounds ().getMaxY ());
                    } 
                    
                    subTree.move (dx, dy);
                    
                    
                    if (i + 1 < subTrees.size ()) {
                        int subTreeBottom = subTree.getBounds ().y + subTree.getBounds ().height;
                        int subTreeLeft = subTree.getBounds ().x;
                        int highestAtLeft = Integer.MAX_VALUE;
                        for (int j = i + 1; j <= upperLoopStart + 1; ++j) {
                            Tree prevTree = subTrees.get (j);
                            int ub = prevTree.getUpperBoundAt (subTreeLeft);
                            if (ub < highestAtLeft) {
                                highestAtLeft = ub;
                            }
                        }
                        
                        int gap = highestAtLeft - subTreeBottom;
                        if (gap > vGap) {
                            gap -= vGap;
                            subTree.move (0, gap);
                        }
                    }
                    
                    ceiling = subTree.getBounds ().getMinY () - vGap;
                }
                
                // Now do the lower loop.
                indentM = nIndents - 1;
                for (int i = lowerLoopStart; i < subTrees.size (); ++i) {
                    Tree subTree = subTrees.get (i);
                    lineUpWithTarget (subTree);
                    
                    int dx = indentM * indent;
                    indentM --;
                    
                    int dy = 0;             
                    if (subTree.getBounds ().getMinY () < floor) {
                        dy = (int)(floor - subTree.getBounds ().getMinY ());
                    } 
                    
                    subTree.move (dx, dy);
                    
                    if (i > 1) {
                        int subTreeTop = subTree.getBounds ().y;
                        int subTreeLeft = subTree.getBounds ().x;
                        int lowestAtLeft = Integer.MIN_VALUE;
                        for (int j = i - 1; j >= lowerLoopStart - 1; --j) {
                            Tree prevTree = subTrees.get (j);
                            int lb = prevTree.getLowerBoundAt (subTreeLeft);
                            if (lb > lowestAtLeft) {
                                lowestAtLeft = lb;
                            }
                        }
                        
                        int gap = subTreeTop - lowestAtLeft;
                        if (gap > vGap) {
                            gap -= vGap;
                            subTree.move (0, -1 * gap);
                        }
                    }
                    
                    floor = subTree.getBounds ().getMaxY () + vGap;
                }
                
                int top = Integer.MAX_VALUE;
                int bottom = Integer.MIN_VALUE;
                int left = Integer.MAX_VALUE;
                for (int i = 0; i < subTrees.size (); ++i) {
                    Tree t = subTrees.get (i);
                    Rectangle b = t.getBounds ();
                    if (b.x < left) {
                        left = b.x;
                    }
                    if (b.y < top) {
                        top = b.y;
                    }
                    if (b.y + b.height > bottom) {
                        bottom = (b.y + b.height);
                    }
                }
                
                int right = (int)root.getBounds().getMaxX ();
                
                if (top > root.getBounds ().y) {
                    top = root.getBounds ().y;
                }
                if (bottom < (int)root.getBounds().getMaxY ()) {
                    bottom = (int)root.getBounds().getMaxY ();
                }
                
                return new Rectangle (left, top, right - left, bottom - top);
            }
            
            /**
             * Determines the upper bound of the rectangle bounding
             * the subtree out to the the given x-coordinate.
             * @param x int
             */
            int getUpperBoundAt (int x) {
                Rectangle b = root.getBounds ();
                if (b.x < x || subTrees.size () == 0) {
                    return b.y;
                }
                
                Tree subTree = subTrees.get (0);
                int upper = subTree.getUpperBoundAt (x);
                
                if (upper > b.y) {
                    return b.y;
                }
                
                return upper;
            }
            
            /**
             * Determines the lower bound of the rectangle bounding
             * the subtree out to the the given x-coordinate.
             * @param x int
             */
            int getLowerBoundAt (int x) {
                Rectangle b = root.getBounds ();
                if (b.x < x || subTrees.size () == 0) {
                    return (b.y + b.height);
                }
                
                Tree subTree = subTrees.get (subTrees.size () - 1);
                int lower = subTree.getLowerBoundAt (x);
                
                if (lower < (b.y + b.height)) {
                    return (b.y + b.height);
                }
                
                return lower;
            }

        }
    }
    
}
