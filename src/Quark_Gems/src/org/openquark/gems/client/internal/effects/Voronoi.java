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
 * Voronoi.java
 * Creation date: (1/31/01 9:43:20 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.internal.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * This class implements a Voronoi diagram suitable for use in creating a 'broken' effect
 * which we use on Gems to indicate that they are not complete or valid in some way.
 * <P>
 * The code in this class (in particular the sweep algorithm) was ported from Steven Fortune's C code on netlib at
 * <a href='http://www.netlib.org/voronoi/sweep2'>http://www.netlib.org/voronoi/sweep2</a>.
 * It is used here by permission of Steven Fortune, who says that "the code is in the public domain at this point". 
 * <p>
 * Creation date: (1/31/01 9:43:20 AM)
 * <p>
 * @author Luke Evans
 */
public final class Voronoi {
    private Site sites[];
    private EventHeap heap;
    private Site bottomSite;
    private float deltaX;
    private float deltaY;
    private boolean doShowHeapCircles;
    private boolean doShowTriangles;
    private boolean doShowVoronoi;
    private EdgeList edgeList;
    private float maxX;
    private float maxY;
    private float minX;
    private float minY;
    private Site newIntStar;
    /* Some variables for sweeplinealgo... */
    private Site newSite;
    private HalfEdge resultEdgeList;
    private int siteIdx;
    private float xMax;
    private float xMin;
    private float xOffs;
    private float xScale = 1.0F;
    private float yMax;
    private float yMin;
    private float yOffs;
    private float yScale = 1.0F;

    /**
     * The Edge class describes a single line (edge)
     * Creation date: (1/31/01 10:38:33 AM)
     * @author Luke Evans
     */
    public static class Edge {
        private final float a, b, c;
        Site ep1;  // mutable (ugh)
        Site ep2;  // mutable (ugh)
        private final Site reg1;
        private final Site reg2;

        /**
         * Constuct an Edge from all parameters
         * Creation date: (1/31/01 10:45:03 AM)
         */
        Edge(Site myreg1, Site myreg2, Site myep1, Site myep2, float mya, float myb, float myc) {
            reg1 = myreg1;
            reg2 = myreg2;
            ep1 = myep1;
            ep2 = myep2;
            a = mya;
            b = myb;
            c = myc;
        }
    }

    /**
     * Class for storing the currently active halfedge associated with the sweeping
     * Creation date: (1/31/01 10:43:06 AM)
     * @author Luke Evans
     */
    static class EdgeList {
        private final HalfEdge hashTable[];
        private final HalfEdge leftEnd;
        private final HalfEdge rightEnd;
        private final float minX;
        private final float deltaX;

        /**
         * Create an EdgeList from the number of sites, minimum X coord and the X delta
         * Creation date: (1/31/01 10:47:16 AM)
         * @param siteNum float number of sites
         */
        EdgeList(int siteNum, float minX, float deltaX) {
            this.minX = minX;
            this.deltaX = deltaX;

            hashTable = new HalfEdge[siteNum * 2];

            leftEnd = createEdge(null, true);
            rightEnd = createEdge(null, true);

            leftEnd.leftEdge = null;
            leftEnd.rightEdge = rightEnd;
            rightEnd.leftEdge = leftEnd;
            rightEnd.rightEdge = null;

            hashTable[0] = leftEnd;
            hashTable[hashTable.length - 1] = rightEnd;
        }

        /**
         * Create a new half edge in the list
         * Creation date: (1/31/01 10:50:30 AM)
         * @param e Edge the edge
         * @return HalfEdge
         */
        HalfEdge createEdge(Edge e, boolean isLeftToRight) {
            HalfEdge newEdge = new HalfEdge();
            newEdge.edge = e;
            newEdge.isLeftToRight = isLeftToRight;
            newEdge.hashNext = null;
            newEdge.vertex = null;
            return newEdge;
        }

        /**
         * Insert a new edge into the linked list after lb
         * Creation date: (1/31/01 12:58:24 PM)
         * @param lb HalfEdge the edge to insert after
         * @param newEdge HalfEdge the edge to insert
         */
        void insertEdge(HalfEdge lb, HalfEdge newEdge) {
            newEdge.leftEdge = lb;
            newEdge.rightEdge = lb.rightEdge;
            lb.rightEdge.leftEdge = newEdge;
            lb.rightEdge = newEdge;
        }

        /**
         * Get entry from hash table, pruning any deleted nodes
         * Creation date: (1/31/01 10:55:16 AM)
         * @param idx int the index to look up
         * @return HalfEdge the half edge at the given index
         */
        HalfEdge getHashEdge(int idx) {
            HalfEdge e;

            e = hashTable[idx];
            if (e == null)
                return null;

            if ((e.leftEdge == null) && (e.rightEdge == null)) {
                // Deleted ?
                hashTable[idx] = null;
                return null;
            }
            return e;
        }

        /**
         * Find the left bounding edge
         * Creation date: (1/31/01 10:56:35 AM)
         * @param p Site starting point (to start looking for an edge)
         * @return HalfEdge the half edge we found
         */
        HalfEdge getLeftBoundEdge(Site p) {
            int i;
            HalfEdge e;
            int idx;

            /* Use hash table to get close to desired halfedge */
            idx = (int) ((p.x - minX) / deltaX * hashTable.length);

            if (idx < 0)
                idx = 0;

            if (idx >= hashTable.length)
                idx = hashTable.length - 1;

            e = getHashEdge(idx);

            if (e == null) {
                for (i = 1; true; i++) {
                    e = getHashEdge(idx - i);
                    if (e != null)
                        break;
                    e = getHashEdge(idx + i);
                    if (e != null)
                        break;
                }
            }

            // Now search linear list of halfedges for the correct one
            if ((e == leftEnd) || ((e != rightEnd) && (right_of(e, p)))) {
                do {
                    e = e.rightEdge;
                } while ((e != rightEnd) && (right_of(e, p)));
                e = e.leftEdge;
            } else {
                do {
                    e = e.leftEdge;
                } while ((e != leftEnd) && (!right_of(e, p)));
            }

            /* Update hash table and reference counts */
            if ((idx > 0) && (idx < hashTable.length - 1))
                hashTable[idx] = e;

            return e;
        }

        /**
         * Delete a halfedge
         * Creation date: (1/31/01 10:52:55 AM)
         * @param edge HalfEdge the edge to remove 
         */
        void deleteEdge(HalfEdge edge) {
            edge.leftEdge.rightEdge = edge.rightEdge;
            edge.rightEdge.leftEdge = edge.leftEdge;

            edge.leftEdge = null;
            edge.rightEdge = null;
        }

        /**
         * Return the right half edge
         * Creation date: (1/31/01 1:49:48 PM)
         * @param e HalfEdge the edge
         * @return HalfEdge the right half edge
         */
        HalfEdge rightEdge(HalfEdge e) {
            return e.rightEdge;
        }
        /**
         * Return the left half edge
         * Creation date: (1/31/01 1:49:48 PM)
         * @param e HalfEdge the edge
         * @return HalfEdge the left half edge
         */
        HalfEdge leftEdge(HalfEdge e) {
            return e.leftEdge;
        }

        /**
         * Determine if a Site is to the right of an edge 
         * Creation date: (1/31/01 1:51:06 PM)
         * @param el HalfEdge the edge to compare p to
         * @param p Site the point to check
         * @return boolean true if p lies to the right of the edge el
         */
        boolean right_of(HalfEdge el, Site p) {
            Edge e;
            Site topSite;
            boolean right_of_site, above, fast;
            float dxp, dyp, dxs, t1, t2, t3, yl;

            e = el.edge;
            topSite = e.reg2;

            right_of_site = p.x > topSite.x;

            if (right_of_site && el.isLeftToRight) {
                return true;
            }

            if (!right_of_site && !el.isLeftToRight) {
                return false;
            }

            if (e.a == 1.0) {
                dyp = p.y - topSite.y;
                dxp = p.x - topSite.x;

                fast = false;

                if ((!right_of_site && (e.b < 0.0)) || (right_of_site && (e.b >= 0.0))) {
                    above = dyp >= e.b * dxp;
                    fast = above;
                } else {
                    above = p.x + p.y * e.b > e.c;
                    if (e.b < 0.0)
                        above = !above;
                    if (!above)
                        fast = true;
                }

                if (!fast) {
                    dxs = topSite.x - e.reg1.x;
                    above = e.b * (dxp * dxp - dyp * dyp) < dxs * dyp * (1.0 + 2.0 * dxp / dxs + e.b * e.b);

                    if (e.b < 0.0)
                        above = !above;
                }
            } else /*e.b==1.0 */ {
                yl = e.c - e.a * p.x;
                t1 = p.y - yl;
                t2 = p.x - topSite.x;
                t3 = yl - topSite.y;
                above = t1 * t1 > t2 * t2 + t3 * t3;
            }
            return (el.isLeftToRight ? above : !above);
        }
        /**
         * Return the list of edges
         * Creation date: (1/31/01 10:54:13 AM)
         * @return HalfEdge the edge list
         */
        HalfEdge getEdgeList() {
            return leftEnd;
        }
    }

    /**
     * The EventHeap is used to handle the queue of events generated by a sweep
     * Creation date: (1/31/01 10:41:31 AM)
     * @author Luke Evans
     */
    static class EventHeap {
        private HalfEdge hashTable[];
        private int count;
        private int curMinIdx;
        private float minY, deltaY;

        void outEdge(Edge e) {
        }

        /**
         * Construct an EventHead from the number of Sites, minimumY and the Y delta
         * Creation date: (1/31/01 1:57:51 PM)
         * @param siteNum int the number of Sites (points)
         * @param minY int the minimum Y value
         * @param deltaY the Y delta
         */
        EventHeap(int siteNum, float minY, float deltaY) {
            hashTable = new HalfEdge[siteNum * 4];
            count = 0;
            curMinIdx = 0;

            this.minY = minY;
            this.deltaY = deltaY;
        }

        /**
         * Find element point in hash table
         * Creation date: (1/31/01 2:04:36 PM)
         * @param e HalfEdge the half edge
         * @return the index
         */
        int hashFct(HalfEdge e) {
            int i = (int) ((e.yStar - minY) / deltaY * hashTable.length);
            if (i < 0) {
                i = 0;
            }
            if (i >= hashTable.length) {
                i = hashTable.length - 1;
            }

            if (curMinIdx > i) {
                curMinIdx = i;
            }

            return i;
        }

        /**
         * Delete an edge from the heap
         * Creation date: (1/31/01 2:01:44 PM)
         * @param e HalfEdge the edge to delete 
         */
        void deleteItem(HalfEdge e) {
            if (e.vertex != null) {
                HalfEdge item, prevItem;
                int idx;

                idx = hashFct(e);
                item = hashTable[idx];
                prevItem = null;

                while (item != e) {
                    prevItem = item;
                    item = item.hashNext;
                }
                if (prevItem == null) {
                    hashTable[idx] = item.hashNext;
                } else {
                    prevItem.hashNext = item.hashNext;
                }

                count--;
                e.vertex = null;
            }
        }

        /**
         * Insert an item into the heap
         * Creation date: (1/31/01 2:06:21 PM)
         * @param e HalfEdge the item
         * @param v Site the point
         * @param offs float the offset
         */
        void insertItem(HalfEdge e, Site v, float offs) {
            HalfEdge curItem, prevItem;

            e.vertex = v;
            e.yStar = v.y + offs;

            int idx = hashFct(e);

            prevItem = null;
            curItem = hashTable[idx];

            while ((curItem != null) && ((e.yStar > curItem.yStar) || ((e.yStar == curItem.yStar) && (v.x > curItem.vertex.x)))) {
                prevItem = curItem;
                curItem = curItem.hashNext;
            }

            e.hashNext = curItem;

            if (prevItem == null) {
                hashTable[idx] = e;
            } else {
                prevItem.hashNext = e;
            }

            if (curMinIdx > idx) {
                curMinIdx = idx;
            }

            count++;
        }

        /**
         * Check if this heap is empty
         * Creation date: (1/31/01 2:22:43 PM)
         * @return boolean true if empty
         */
        boolean isEmpty() {
            return (count == 0);
        }

        /**
         * Recalculate the minimal point
         * Creation date: (1/31/01 2:07:54 PM)
         * @return Site the minimal point
         */
        Site recalcMin() {
            if (count == 0) {
                return null;
            }

            while (hashTable[curMinIdx] == null) {
                curMinIdx++;
            }

            return new Site(hashTable[curMinIdx].vertex.x, hashTable[curMinIdx].yStar);
        }

        /**
         * Extract the current minimal edge from the heap
         * Creation date: (1/31/01 2:03:06 PM)
         * @return HalfEdge the minimal edge
         */
        HalfEdge extractMin() {
            HalfEdge curItem;
            if (count == 0) {
                return null;
            }

            recalcMin();
            curItem = hashTable[curMinIdx];
            hashTable[curMinIdx] = curItem.hashNext;
            count--;
            recalcMin(); // !!
            return curItem;
        }
    }

    /**
     * The HalfEdge class describes a vertex composed in turn of left and right edges
     * Creation date: (1/31/01 10:33:35 AM)
     * @author Luke Evans
     */
    public static class HalfEdge {
        private HalfEdge leftEdge;
        private HalfEdge rightEdge;

        private Edge edge;
        private boolean isLeftToRight;
        private Site vertex;
        private float yStar;

        private HalfEdge hashNext;

    }

    /**
     * A Site is just a point coordinate
     * Creation date: (1/31/01 10:40:06 AM)
     * @author Luke Evans
     */
    public static class Site {
        private float x, y;

        /**
         * Construct a Site from xy coords
         * Creation date: (1/31/01 2:25:09 PM)
         * @param x the X coord
         * @param y the Y coord
         */
        public Site(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Default constructor for a Voronoi
     * Creation date: (1/31/01 9:49:18 AM)
     */
    public Voronoi() {
        edgeList=new EdgeList(2, 0.0f, 1.0f);
    }

    /**
     * Draw blinking objects
     * Creation date: (1/31/01 9:50:19 AM)
     * @param thread Thread
     * @param demogc Graphics
     * @param sweep1 HalfEdge
     * @param sweep2 HalfEdge
     * @param newBisect HalfEdge
     * @param s1 Site
     * @param s2 Site
     */
    void blinkObjs(Thread thread,Graphics demogc,
                        HalfEdge sweep1,HalfEdge sweep2,
                        HalfEdge newBisect,
                        Site s1,Site s2,
//                      Site newvertex,
                        Site ni,float nr,
                        Site i1,float r1,
                        Site i2,float r2) {
        int i;

        for (i = 0; i < 8; i++) {
            if (doShowVoronoi) {
                demogc.setColor(Color.red);
                if (sweep1 != null)
                    showEdge(demogc, sweep1.edge, true, false);
                if (sweep2 != null)
                    showEdge(demogc, sweep2.edge, true, false);
                if ((newBisect != null) && (i != 0) && (i != 7))
                    showEdge(demogc, newBisect.edge, true, false);
            }
            if (doShowTriangles) {
                demogc.setColor(Color.orange);
                if (sweep1 != null)
                    showEdge(demogc, sweep1.edge, false, true);
                if (sweep2 != null)
                    showEdge(demogc, sweep2.edge, false, true);
                if ((newBisect != null) && (i != 0) && (i != 7))
                    showEdge(demogc, newBisect.edge, false, true);
            }
            demogc.setColor(Color.black);

            if (s1 != null) {
                demogc.fillOval(
                    (int)(s1.x * xScale + xOffs) - 2,
                    (int)(s1.y * yScale + yOffs) - 2,
                    5,5);
            }

            if (s2 != null) {
                demogc.fillOval(
                    (int)(s2.x * xScale + xOffs) - 2,
                    (int)(s2.y * yScale + yOffs) - 2,
                    5,5);
            }

            if ((i1 != null) && (i != 0) && (i != 7)) {
                demogc.fillOval(
                    (int)(i1.x * xScale + xOffs) - 2,
                    (int)(i1.y * yScale + yOffs) - 2,
                    5, 5);

                demogc.drawLine(
                    (int)(i1.x * xScale + xOffs),
                    (int)(i1.y * yScale + yOffs),
                    (int)(i1.x * xScale + xOffs),
                    (int)((i1.y + r1) * yScale + yOffs));

                if (doShowHeapCircles)
                    demogc.drawArc(
                        (int)((i1.x - r1) * xScale + xOffs),
                        (int)((i1.y - r1) * yScale + yOffs),
                        (int)(2.0f * r1 * xScale),
                        (int)(2.0f * r1 * yScale),
                        0, 360);
            }

            if (ni != null) {
                demogc.fillOval(
                    (int)(ni.x * xScale + xOffs) - 2,
                    (int)(ni.y * yScale + yOffs) - 2,
                    5, 5);

                demogc.drawLine(
                    (int)(ni.x * xScale + xOffs),
                    (int)(ni.y * yScale + yOffs),
                    (int)(ni.x * xScale + xOffs),
                    (int)((ni.y + nr) * yScale + yOffs));

                if (doShowHeapCircles)
                    demogc.drawArc(
                        (int)((ni.x - nr) * xScale + xOffs),
                        (int)((ni.y - nr) * yScale + yOffs),
                        (int)(2.0f * nr * xScale),
                        (int)(2.0f * nr * yScale),
                        0, 360);
            }

            if ((i2 != null) && (i != 0) && (i != 7)) {
                demogc.fillOval(
                    (int)(i2.x * xScale + xOffs) - 2,
                    (int)(i2.y * yScale + yOffs) - 2,
                    5, 5);

                demogc.drawLine(
                    (int)(i2.x * xScale + xOffs),
                    (int)(i2.y * yScale + yOffs),
                    (int)(i2.x * xScale + xOffs),
                    (int)((i2.y + r2) * yScale + yOffs));

                if (doShowHeapCircles)
                    demogc.drawArc(
                        (int)((i2.x - r2) * xScale + xOffs),
                        (int)((i2.y - r2) * yScale + yOffs),
                        (int)(2.0f * r2 * xScale),
                        (int)(2.0f * r2 * yScale),
                        0, 360);
            }
            try {
                Thread.sleep(((i & 1) == 1) ? 300 : 100);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Create a bisecting edge for points s1 and s2
     * Creation date: (1/31/01 9:53:34 AM)
     * @param s1 Site first point
     * @param s2 Site second point
     * @return Edge the bisecting edge
     */
    private Edge createBisect(Site s1, Site s2) {
        float dx, dy, a, b, c;
        Edge newedge;

        dx = s2.x - s1.x;
        dy = s2.y - s1.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            a = 1.0f;
            b = dy / dx;
            c = s1.x + s1.y * dy / dx + (dx + dy * dy / dx) * 0.5f;
        } else {
            a = dx / dy;
            b = 1.0f;
            c = s1.x * dx / dy + s1.y + (dx * dx / dy + dy) * 0.5f;
        }
        newedge = new Edge(s1, s2, null, null, a, b, c);

        return newedge;
    }

    /**
     * Determine the distances between points s and t
     * Creation date: (1/31/01 9:55:06 AM)
     * @param s Site point 1
     * @param t Site point 2
     * @return float the distance
     */
    private float dist(Site s, Site t) {
        float dx, dy;
        dx = s.x - t.x;
        dy = s.y - t.y;
        return ((float) Math.sqrt(dx * dx + dy * dy));
    }
    
    /**
     * Eliminate sites (points) with the same coordinates from the Voronoi object
     * Creation date: (1/31/01 9:56:24 AM)
     */
    private void elimDupSites() {
        int i, j, count;
        Site cursite, newsites[];

        if (sites == null)
            return;

        count = 0;

        cursite = sites[0];

        for (i = 1; i < sites.length; i++) {
            if ((Math.abs(cursite.x - sites[i].x) < 1e-6) && (Math.abs(cursite.y - sites[i].y) < 1e-6)) {
                count++;
                sites[i] = null;
            } else
                cursite = sites[i];
        }
        if (count == 0)
            return;

        newsites = new Site[sites.length - count];
        j = 0;

        for (i = 0; i < sites.length; i++) {
            if (sites[i] != null) {
                newsites[j] = sites[i];
                j++;
            }
        }
        sites = newsites;
    }
    
    /**
     * Determine the left or right endpoint of a vertex and set this into the given Site.
     * Creation date: (1/31/01 9:57:56 AM)
     * @param e Edge the vertex
     * @param isLeftToRight boolean which endpoint to consider
     * @param s Site the Site (point) to update
     */
    private void endpoint(Edge e, boolean isLeftToRight, Site s) {
        if (isLeftToRight) {
            e.ep1 = s;
            if (e.ep2 == null) {
                return;
            }
        } else {
            e.ep2 = s;
            if (e.ep1 == null) {
                return;
            }
        }

        HalfEdge halfEdge = new HalfEdge();
        halfEdge.edge = e;
        halfEdge.rightEdge = resultEdgeList;
        resultEdgeList = halfEdge;
    }
    
    /**
     * Move all the vertices which remain after all the sweep passes (sweepStep) into the final
     * Voronoi diagram.
     * Creation date: (1/31/01 10:02:44 AM)
     */
    private void finishSweepLine() {
        HalfEdge edge, nextEdge;

        edge = edgeList.leftEnd.rightEdge;
        while (edge != edgeList.rightEnd) {
            nextEdge = edge.rightEdge;
            edge.rightEdge = resultEdgeList;
            resultEdgeList = edge;
            edge = nextEdge;
        }

        edgeList.leftEnd.rightEdge = edgeList.rightEnd;
        edgeList.rightEnd.leftEdge = edgeList.leftEnd;
    }
    
    /**
     * Generate the Voronoi edges from an initial set of points (Sites)
     * Creation date: (1/31/01 10:27:57 AM)
     * @param sites the list of initial points
     */
    public void generate(Site[] sites) {
        // Initialise and see if anything needs to be done
        if (initSweepLine(sites)) {
            // Keep sweeping until there's nothing more to do
            while (sweepStep(null, null)) {/* Empty body */}
            // Copy the resulting vertices into the finished Voronoi
            finishSweepLine();
        }
    }
    /**
     * Return the half edges list
     * Creation date: (1/31/01 10:04:40 AM)
     * @return HalfEdge the linked list of half edges
     */
    private HalfEdge getHalfEdges() {   
        return resultEdgeList;  
    }
    
    /**
     * Initialise internal structures required for the sweepline algorithm which will
     * compute the Voronoi diagram for all sites in initSites.
     * Creation date: (1/31/01 10:09:48 AM)
     * @param initSites Site[] the initial set of sites (points) from which to compute Voronoi
     * @return boolean true if ready to compute, false if nothing to do
     */
    private boolean initSweepLine(Site[] initSites) {
        // Copies sites to internal array (since we sort array and eliminate
        // duplicates internally !)
        sites = new Site[initSites.length];
        System.arraycopy(initSites, 0, sites, 0, initSites.length);

        if (sites.length == 0)
            return false;

        // Sorts and eliminates duplicates
        sortSites(0, sites.length - 1);
        elimDupSites();

        minY = sites[0].y;
        maxY = sites[sites.length - 1].y;

        minX = sites[0].x;
        maxX = sites[0].x;

        for (siteIdx = 0; siteIdx < sites.length; siteIdx++) {
            if (minX > sites[siteIdx].x)
                minX = sites[siteIdx].x;
            if (maxX < sites[siteIdx].x)
                maxX = sites[siteIdx].x;
        }
        deltaY = maxY - minY;
        deltaX = maxX - minX;

        heap = new EventHeap(sites.length, minY, deltaY);

        edgeList = new EdgeList(sites.length, minX, deltaX);

        resultEdgeList = null;

        if (sites.length <= 1)
            return false;

        siteIdx = 0;

        bottomSite = sites[siteIdx];
        siteIdx++;

        // Load first site
        newSite = sites[siteIdx];
        siteIdx++;

        newIntStar = new Site(0.0f, 0.0f);

        return true;
    }
    
    /**
     * Find the intersection point between edges el1 and el2
     * Creation date: (1/31/01 10:13:24 AM)
     * @param el1 HalfEdge the first edge
     * @param el2 HalfEdge the second edge
     * @return Site the intersection point
     */
    private Site intersect(HalfEdge el1, HalfEdge el2) {
        // Finds the intersection between egde el1 and el2
        Edge e1, e2, e;
        HalfEdge el;
        float d, xint, yint;
        boolean right_of_site;

        e1 = el1.edge;
        e2 = el2.edge;

        if ((e1 == null) || (e2 == null))
            return null;

        if (e1.reg2 == e2.reg2)
            return null;

        d = e1.a * e2.b - e1.b * e2.a;
        if (-1.0e-6 < d && d < 1.0e-6)
            return null;

        xint = (e1.c * e2.b - e2.c * e1.b) / d;
        yint = (e2.c * e1.a - e1.c * e2.a) / d;

        if ((e1.reg2.y < e2.reg2.y) || ((e1.reg2.y == e2.reg2.y) && (e1.reg2.x < e2.reg2.x))) {
            el = el1;
            e = e1;
        } else {
            el = el2;
            e = e2;
        }

        right_of_site = (xint >= e.reg2.x);

        if (right_of_site == el.isLeftToRight)
            return null;

        return new Site(xint, yint);
    }

    /**
     * Utility method to return the left end of an edge
     * Creation date: (1/31/01 10:15:24 AM)
     * @param halfEdge HalfEdge the edge
     * @return Site the left end
     */
    private Site leftReg(HalfEdge halfEdge) {
        if (halfEdge.edge == null) // ???
            return bottomSite; // ???

        if (halfEdge.isLeftToRight)
            return halfEdge.edge.reg1; //[0];
        else
            return halfEdge.edge.reg2; //[1];
    }
    
    /**
     * Utility method to return the right end of an edge
     * Creation date: (1/31/01 10:15:24 AM)
     * @param halfEdge HalfEdge the edge
     * @return Site the right end
     */
    private Site rightReg(HalfEdge halfEdge) {
        if (halfEdge.edge == null)
            return bottomSite;

        if (halfEdge.isLeftToRight)
            return halfEdge.edge.reg2; //[1];
        else
            return halfEdge.edge.reg1; //[0];
    }
    
    /**
     * Display this Voronoi in the GC passed in
     * Creation date: (1/31/01 4:28:20 PM)
     * @param gc Graphics the graphics context in which to paint
     * @param showVoronoi boolean show the Voronoi edges
     * @param showTriangles boolean show the Delaunay edges
     */
    public void show(Graphics gc, boolean showVoronoi, boolean showTriangles) {
        HalfEdge edgeList = getHalfEdges();
        while (edgeList != null) {
            if ((edgeList.edge != null) && ((edgeList.edge.ep1 != null) || (edgeList.edge.ep2 != null) || (edgeList.isLeftToRight == false)))
                showEdge(gc, edgeList.edge, showVoronoi, showTriangles);

            edgeList = edgeList.rightEdge;
        }
    }
    
    /**
     * Display an edge
     * Creation date: (1/31/01 10:25:36 AM)
     */
    private void showEdge(Graphics gc, Edge edge, boolean showVoronoi, boolean showTriangles) {
        // Show an edge on the screen. If showvoronoi==true, then the voronoi line segment
        // will be shown. If showtriangls==true, then the Delaunay edge will be shown.
        if (edge == null)
            return;

        if (showVoronoi) {
            Site ep1, ep2;

            if (edge.ep1 != null)
                ep1 = edge.ep1;
            else {
                if (edge.a == 1.0f) {
                    if (edge.b >= 0)
                        ep1 = new Site(edge.c - edge.b * yMax, yMax);
                    else
                        ep1 = new Site(edge.c - edge.b * yMin, yMin);
                } else
                    ep1 = new Site(xMin, edge.c - edge.a * xMin);
            }

            if (edge.ep2 != null)
                ep2 = edge.ep2;
            else {
                if (edge.a == 1.0f) {
                    if (edge.b < 0)
                        ep2 = new Site(edge.c - edge.b * yMax, yMax);
                    else
                        ep2 = new Site(edge.c - edge.b * yMin, yMin);
                } else
                    ep2 = new Site(xMax, edge.c - edge.a * xMax);
            }

            gc.drawLine((int) (ep1.x * xScale + xOffs), (int) (ep1.y * yScale + yOffs), (int) (ep2.x * xScale + xOffs), (int) (ep2.y * yScale + yOffs));
        }
        if (showTriangles)
            gc.drawLine((int) (edge.reg1.x * xScale + xOffs), (int) (edge.reg1.y * yScale + yOffs), (int) (edge.reg2.x * xScale + xOffs), (int) (edge.reg2.y * yScale + yOffs));
    }
    
    /**
     * Perform a quicksort on all sites with respect to their Y coordinate, then X coordinate.
     * Creation date: (1/31/01 10:26:04 AM)
     * @param start int beginning index of sort range
     * @param end int end index of sort range
     */
    private void sortSites(int start, int end) {
        int i, j;
        Site v, tmp;

        if (start < end) {
            //       vi=(start+end)/2;
            v = sites[(start + end) >>> 1];    // (start + end) / 2 could overflow to a negative num.  Use unsigned shift right.

            i = start;
            j = end;

            do {
                while ((sites[i] != v) && ((sites[i].y < v.y) || ((sites[i].y == v.y) && (sites[i].x <= v.x))))
                    i++;

                while ((sites[j] != v) && ((sites[j].y > v.y) || ((sites[j].y == v.y) && (sites[j].x >= v.x))))
                    j--;

                if (i >= j)
                    break;
                tmp = sites[i];
                sites[i] = sites[j];
                sites[j] = tmp;
            }
            while (true);
            if (sites[i] != v)
                System.out.println("Error pivot element !" + String.valueOf(i) + " " + String.valueOf(start) + " " + String.valueOf(end));
            sortSites(start, i - 1);
            sortSites(i + 1, end);
        }
    }
    
    /**
     * Perform a single sweep step and indicate whether we are finished.
     * Creation date: (1/31/01 10:31:26 AM)
     * @param demoThread Thread
     * @param demoGC Graphics
     * @return true if there is more to do, false otherwise
     */
    private boolean sweepStep(Thread demoThread,Graphics demoGC) {
        // The actual sweep step. The next sweep event is read
        // and the internal structures are modified.
        // The function returns false, if there is no such sweep event available.
        // (I.e. we are done)

        Site        bot, top, temp, p, p1, v;
        boolean     isLeftToRight;
        HalfEdge    leftBound, rightBound, leftBound1, rightBound1;
        HalfEdge    bisector, bisector1;
        Edge        e;

        if (heap.isEmpty() == false) {
            newIntStar = heap.recalcMin();
        }

        if ((newSite != null) && (heap.isEmpty() || 
                (newSite.y < newIntStar.y) || 
                ((newSite.y == newIntStar.y) && (newSite.x < newIntStar.x)))) {
            
            /* new site is smallest */

            leftBound = edgeList.getLeftBoundEdge(newSite);
            rightBound = leftBound.rightEdge;

            bot = rightReg(leftBound);

            e = createBisect(bot, newSite);

            bisector = edgeList.createEdge(e, true);
            edgeList.insertEdge(leftBound, bisector);

            if ((p = intersect(leftBound, bisector)) != null) {
                heap.deleteItem(leftBound);
                heap.insertItem(leftBound, p, dist(p, newSite));
            }
            bisector1 = bisector;

            bisector = edgeList.createEdge(e, false);
            edgeList.insertEdge(bisector1, bisector);

            if ((p1 = intersect(bisector, rightBound)) != null)
                heap.insertItem(bisector, p1, dist(p1, newSite));

            if (demoThread != null) {
                blinkObjs(
                        demoThread, demoGC,
                        null, null,
                        null,
                        newSite, null,
                        null, 0.0f,
                        null, 0.0f, null, 0.0f);

                blinkObjs(
                        demoThread, demoGC,
                        leftBound, rightBound,
                        null,
                        newSite, bot,
                        null, 0.0f,
                        null, 0.0f, null, 0.0f);

                blinkObjs(
                        demoThread, demoGC,
                        leftBound, rightBound,
                        bisector,
                        newSite, bot,
                        null, 0.0f,
                        p, (p!=null) ? dist(p, newSite) : 0.0f,
                                p1, (p1!=null) ? dist(p1, newSite) : 0.0f);
            }

            if (siteIdx == sites.length)
                newSite = null;
            else {
                newSite = sites[siteIdx];
                siteIdx++;
            }
        
        } else {
            if (heap.isEmpty() != false) {
                // Now sweepevent available ?
                return false;
            }

            /* intersection is smallest */

            leftBound = heap.extractMin();

            leftBound1 = leftBound.leftEdge;
            rightBound = leftBound.rightEdge;
            rightBound1 = rightBound.rightEdge;

            bot = leftReg(leftBound);
            top = rightReg(rightBound);

            v = leftBound.vertex;

            if (demoThread != null)
                blinkObjs(
                        demoThread, demoGC,
                        leftBound, rightBound,
                        null,
                        bot, top,
                        v, leftBound.yStar-v.y,
                        null, 0.0f,
                        null, 0.0f);

            endpoint(leftBound.edge, leftBound.isLeftToRight, v);
            endpoint(rightBound.edge, rightBound.isLeftToRight, v);

            edgeList.deleteEdge(leftBound);
            heap.deleteItem(rightBound);
            edgeList.deleteEdge(rightBound);

            isLeftToRight = true;
            if (bot.y > top.y) {
                temp = bot;
                bot = top;
                top = temp;
                isLeftToRight = false;
            }
            e = createBisect(bot, top);

            bisector = edgeList.createEdge(e, isLeftToRight);
            edgeList.insertEdge(leftBound1, bisector);

            endpoint(e, !isLeftToRight, v);

            if ((p = intersect(leftBound1, bisector)) != null) {
                heap.deleteItem(leftBound1);
                heap.insertItem(leftBound1, p, dist(p,bot));
            }

            if ((p1 = intersect(bisector, rightBound1)) != null) {
                heap.insertItem(bisector, p1, dist(p1, bot));
            }

            if (demoThread != null) {
                /*
                 blinkObjs(
                 demoThread,demoGC,
                 leftBound,rightBound,
                 null,
                 bot,top,
                 null,0.0f,
                 null,0.0f,null,0.0f);
                 */
                blinkObjs(
                        demoThread, demoGC,
                        null, null, //leftBound,rightBound,
                        bisector,
                        bot, top,
                        null, 0.0f,
                        p, (p!=null) ? dist(p, bot) : 0.0f,
                                p1, (p1!=null) ? dist(p1, bot) : 0.0f);
            }
        }
        return true;
    }

    /**
     * Create a Voronoi object with seed points chosen at random to lie within a given rectangle.
     * In order to achieve a reasonable distribution of points across the area, the area is
     * subdivided into quadrants, and pointsPerQuadrant points are generated in each quadrant.
     * Notice that although all the seed points will lie within the bounding rectangle, the
     * edges generated will extend beyond this rectangle (out to the intersection points).
     * Creation date: (1/31/01 5:27:24 PM)
     * @return Voronoi the new Voronoi object
     * @param area Rectangle the bounding rectangle for points
     * @param pointsPerQuadrant int the number of points to generate in each quadrant
     */
    public static Voronoi makeRandomAreaVoronoi(Rectangle area, int pointsPerQuadrant) {
        // Create a new Voronoi
        Voronoi voronoi = new Voronoi();
        
        // Generate a set of random points across the face of the gem - to ensure a
        // reasonable spread, we'll generate some in each quarter
        int midX = (int) area.getCenterX();
        int midY = (int) area.getCenterY();
        
        // Make enough points
        int totalPoints = pointsPerQuadrant * 4;
        Voronoi.Site[] sites = new Voronoi.Site[totalPoints];
        
        // Iterate over quarter, then the number of points to do
        for (int i = 0; i < 4; i++) {
            int minX;
            int maxX;
            int minY;
            int maxY;
            float xPos, yPos;
            switch (i) {
                case 0 :
                    // NW
                    minX = area.x;
                    maxX = midX;
                    minY = area.y;
                    maxY = midY;
                    break;
                    
                case 1 :
                    // NE
                    minX = midX;
                    maxX = area.x + area.width;
                    minY = area.y;
                    maxY = midY;
                    break;

                case 2 :
                    // SW
                    minX = area.x;
                    maxX = midX;
                    minY = midY;
                    maxY = area.y + area.height;
                    break;
                   
                case 3 :
                    // SE
                    minX = midX;
                    maxX = area.x + area.width;
                    minY = midY;
                    maxY = area.y + area.height;
                    break;
                   
                default :
                    // All area
                    minX = area.x;
                    maxX = area.x + area.width;
                    minY = area.y;
                    maxY = area.y + area.height;                    
            }

            for (int j = 0; j < pointsPerQuadrant; j++) {
                xPos = ((float) ((maxX - minX) * Math.random())) + minX;
                yPos = ((float) ((maxY - minY) * Math.random())) + minY;
                sites[i * pointsPerQuadrant + j] = new Voronoi.Site(xPos, yPos);
            }
        }
        
        // Generate the Voronoi edges
        voronoi.generate(sites);
        
        // Return the object
        return voronoi;
    }
    
}
