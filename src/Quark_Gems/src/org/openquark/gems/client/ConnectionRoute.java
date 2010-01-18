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
 * ConnectionRoute.java
 * Creation date: (12/6/00 7:46:02 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A connection route describes a series of points to join to describe
 * a route from an initial point to a final point.
 * Creation date: (12/6/00 7:46:02 AM)
 * @author Luke Evans
 */
public class ConnectionRoute {
    private final List<Point> points;  // The path itself
    private int minX = 0, minY = 0, maxX = 0, maxY = 0;
    
    /**
     * Default ConnectionRoute constructor.
     */
    private ConnectionRoute() {
        super();
        points = new ArrayList<Point>(2);
    }
    
    /**
     * Construct a ConnectionRoute from start and end points.
     */
    public ConnectionRoute(Point start, Point end) {
        this();
        addPoint(start);
        addPoint(end);
        
        // Initialise the max and min (for bounds)
        minX = Math.min(start.x, end.x);
        maxX = Math.max(start.x, end.x);
        minY = Math.min(start.y, end.y);
        maxY = Math.max(start.y, end.y);
    }
    
    /**
     * Add a final leg based on a fraction extrapolation.
     * Creation date: (12/6/00 12:07:56 PM)
     */
    public void addFinalLeg(float extrapolation) {
        // Can only do this if we have more than 3 points
        int i = points.size();
        if (i < 3) {
            return;
        }
        
        // Get some points
        i--;
        Point lastPoint = points.get(i--);
        Point penultPoint = points.get(i--);
        Point antePenultPoint = points.get(i);
        
        // Work out an extrapolation of the penultimate line (scaled by the factor provided)
        Point delta = new Point(penultPoint.x - antePenultPoint.x, penultPoint.y - antePenultPoint.y);
        delta.x *= extrapolation;
        delta.y *= extrapolation;
        
        // Translate the penultPoint by this delta
        penultPoint.x += delta.x;
        penultPoint.y += delta.y;
        checkPoint(penultPoint);
        
        // Insert a new penultPoint 
        points.add(points.size() - 1, checkPoint(new Point(lastPoint.x + delta.x, lastPoint.y + delta.y)));
    }
    
    /**
     * Add a final leg based on a fixed delta amount.
     * Creation date: (12/6/00 12:07:56 PM)
     */
    public void addFinalLeg(Point delta) {
        // Can only do this if we have more than 3 points
        int i = points.size();
        if (i < 3) {
            return;
        }
        
        // Get some points
        i--;
        Point lastPoint = points.get(i--);
        Point penultPoint = points.get(i--);
        
        // Translate the penultPoint by this delta
        penultPoint.x += delta.x;
        penultPoint.y += delta.y;
        checkPoint(penultPoint);
        
        // Insert a new penultPoint 
        points.add(points.size() - 1, checkPoint(new Point(lastPoint.x + delta.x, lastPoint.y + delta.y)));
    }
    
    /**
     * Add the given point the end of the points list.
     * Creation date: (12/6/00 8:05:24 AM)
     * @param xy Point the point to add
     */
    public void addPoint(Point xy) {
        points.add(checkPoint(xy));
    }
    
    /**
     * Return the index of the point after the middle of the line.
     * Creation date: (12/6/00 8:58:00 AM)
     * @return int the index of the point
     */
    private int afterMiddle() {
        return (int) (points.size() / 2.0F + 0.5);
    }
    
    /**
     * Return the index of the point before the middle of the line.
     * Creation date: (12/6/00 8:58:00 AM)
     * @return int the index of the point
     */
    private int beforeMiddle() {
        return points.size()/2;
    }
    
    /**
     * Insert a new point after the 'middle' of the line.
     * Creation date: (12/6/00 8:07:18 AM)
     * @param xy Point the new point
     */
    public void bisectAfterMiddle(Point xy) {
        // Where is the insert point?
        int insert = afterMiddle();
        
        // Insert point
        points.add(insert, checkPoint(xy));
    }
    
    /**
     * Insert a new point after the 'middle' of the line.
     * Creation date: (12/6/00 8:07:18 AM)
     * @param xy Point the new point
     */
    public void bisectBeforeMiddle(Point xy) {
        // Where is the insert point?
        int insert = beforeMiddle();
        
        // Insert point
        points.add(insert, checkPoint(xy));
    }
    
    /**
     * Bisect with a horizontal line offset by delta from
     * the point before the centre.  If delta is zero, we divide equally
     * Creation date: (12/6/00 8:55:12 AM)
     * @param delta int the offset to the horizontal line or 0 for equal split
     */
    public void bisectWithHorizontalLine(int delta) {
        // Get points around the middle
        int afterMiddle = beforeMiddle();
        int beforeMiddle = afterMiddle -1;
        Point beforePoint = points.get(beforeMiddle);
        Point afterPoint = points.get(afterMiddle);
        
        // Generate the new points
        Point firstPoint = new Point(beforePoint);
        Point secondPoint = new Point(afterPoint);
        
        // If delta is zero work out the split
        if (delta == 0) {
            delta = (secondPoint.y - firstPoint.y) / 2;
        }    
        
        // To generate a horizontal line, we offset these points in the Y dimension
        firstPoint.y += delta;
        secondPoint.y = firstPoint.y;
        
        // Add these points
        bisectWithLine(firstPoint, secondPoint);
    }
    
    /**
     * Add the line xy1->xy2 to the middle of the connection.
     * Creation date: (12/6/00 8:33:30 AM)
     * @param xy1 Point the first point in the line
     * @param xy2 Point the second point in the line
     */
    public void bisectWithLine(Point xy1, Point xy2) {
        // Just insert the two points about the middle
        bisectBeforeMiddle(xy1);
        bisectAfterMiddle(xy2);
    }
    
    /**
     * Bisect with a vertical line offset by delta from 
     * the point before the centre.  If delta is zero, we divide equally
     * Creation date: (12/6/00 8:55:12 AM)
     * @param delta int the offset to the vertical line or 0 for equal split
     */
    public void bisectWithVerticalLine(int delta) {
        // Get points around the middle
        int afterMiddle = beforeMiddle();
        int beforeMiddle = afterMiddle -1;
        Point beforePoint = points.get(beforeMiddle);
        Point afterPoint = points.get(afterMiddle);
        
        // Generate the new points
        Point firstPoint = new Point(beforePoint);
        Point secondPoint = new Point(afterPoint);
        
        // If delta is zero work out the split
        if (delta == 0) {
            delta = (secondPoint.x - firstPoint.x) / 2;
        }    
        
        // To generate a vertical line, we offset these points in the X dimension
        firstPoint.x += delta;
        secondPoint.x = firstPoint.x;
        
        // Add these points
        bisectWithLine(firstPoint, secondPoint);
    }
    
    /**
     * Check this point for relevance to the overall bounds of the route.
     * Creation date: (12/7/00 11:14:28 AM)
     * @return Point the point (so we can treat this as a filter)
     * @param point Point the point
     */
    private Point checkPoint(Point point) {
        maxX = Math.max(point.x, maxX);
        minX = Math.min(point.x, minX);
        maxY = Math.max(point.y, maxY);
        minY = Math.min(point.y, minY);
        return point;
    }
    
    /**
     * Draw this ConnectionRoute in the given graphics context.
     * Creation date: (12/6/00 9:14:10 AM)
     * @param g2d java.awt.Graphics2D
     */
    public void draw(java.awt.Graphics2D g2d) {
        // Just draw lines between each point
        int numPoints = points.size();
        for (int i = 1; i < numPoints; i++) {
            Point fromPoint = points.get(i - 1);
            Point toPoint = points.get(i);
            
            // workaround for a Mac bug in OS X:
            // negative delta's are not drawn properly in XOR mode, so we make all delta's positive
            int maxX = Math.max(toPoint.x, fromPoint.x);
            int minX = Math.min(toPoint.x, fromPoint.x);
            int maxY = Math.max(toPoint.y, fromPoint.y);
            int minY = Math.min(toPoint.y, fromPoint.y);
            
            // use the fact that connection routes are either horizontal or vertical
            // (this works only if minX and minY are from the same point)
            g2d.drawLine(minX, minY, maxX, maxY);
            
            //            g2d.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
        }    
    }
    
    /**
     * Return the first point in the route.
     * Creation date: (12/7/00 2:10:58 PM)
     * @return Point the point
     */
    private Point firstPoint() {
        return points.get(0);
    }
    
    /**
     * Generate a square path between source and destination suitable
     * for horizontal connectors
     * Creation date: (12/7/00 1:30:56 PM)
     * @return boolean true if suitable for extra leg
     */
    public boolean genSquareRouteForHorizontalConnectors() {
        // Get the first and last points
        Point first = firstPoint();
        Point last = lastPoint();
        
        // Determine whether to split vertically or horizontally
        if (last.x >= first.x) {
            bisectWithVerticalLine(0);
            return false;
        } else {
            bisectWithHorizontalLine(0);
            return true;
        }    
    }
    
    /**
     * Generate a square path between source and destination suitable
     * for horizontal connectors.  Add a final leg of the given size.
     * Creation date: (12/7/00 1:30:56 PM)
     */
    public void genSquareRouteForHorizontalConnectors(int extraFinalLegOffset) {
        if (genSquareRouteForHorizontalConnectors()) {
            addFinalLeg(new Point(extraFinalLegOffset, 0));
        }
    }
    
    /**
     * Generate a square path between source and destination suitable
     * for vertical connectors
     * Creation date: (12/7/00 1:30:56 PM)
     * @return boolean true if suitable for extra leg
     */
    public boolean genSquareRouteForVerticalConnectors() {
        // Get the first and last points
        Point first = firstPoint();
        Point last = lastPoint();
        
        // Determine whether to split vertically or horizontally
        if (last.y >= first.y) {
            bisectWithHorizontalLine(0);
            return false;
        } else {
            bisectWithVerticalLine(0);
            return true;
        }    
    }
    
    /**
     * Generate a square path between source and destination suitable
     * for vertical connectors.  Add a final leg of the given size.
     * Creation date: (12/7/00 1:30:56 PM)
     */
    public void genSquareRouteForVerticalConnectors(int extraFinalLegOffset) {
        if (genSquareRouteForVerticalConnectors()) {
            addFinalLeg(new Point(0, extraFinalLegOffset));
        }    
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (12/7/00 11:04:28 AM)
     * @return java.awt.Rectangle
     */
    public java.awt.Rectangle getBoundingRectangle() {    
        return new java.awt.Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
    
    /**
     * Return the last point in the route
     * Creation date: (12/7/00 2:12:09 PM)
     * @return Point the point
     */
    private Point lastPoint() {
        return points.get(points.size() - 1);
    }
}
