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
 * DisplayedGemShape.java
 * Creation date: Mar 26, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A DisplayedGemShape represents the shape/morphology/appearance of a displayed gem on the TableTop.
 * @author Edward Lam
 */
public abstract class DisplayedGemShape {
    
    /** An image from which to create an unscaled graphics context. */
    private static BufferedImage dummyImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);

    /** A Rendering hints object that directs a graphics object to render at the highest quality. */
    private static final RenderingHints RENDERING_HINTS_HIGH_QUALITY = new RenderingHints(null);
    
    static {
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /** The displayed gem represented by this shape. */
    protected final DisplayedGem displayedGem;
    
    /** Information on whatever is supposed to fit inside the shape. */
    protected final InnerComponentInfo innerComponentInfo;

    // Cached members
    private transient Rectangle cachedBounds;      // Cached overall bounds of the Gem
    private transient Rectangle cachedBodyBounds;  // Cached body area bounds of the Gem


    /**
     * An InnerComponentInfo contains information on the image which is supposed to fit inside the shape representing the displayed gem.
     * @author Edward Lam
     */
    interface InnerComponentInfo {
        /**
         * Get the bounds of the inner component.
         * @return the bounds.
         */
        Rectangle getBounds();
        
        /**
         * Get the bounds of the inner component where there are multiple name labels drawn
         * @return a list of rectangles or null if not applicable. 
         */
        List<Rectangle> getInputNameLabelBounds();

        /**
         * Paint the inner component in the displayed gem.
         * @param tableTop
         * @param g2d
         */
        void paint(TableTop tableTop, Graphics2D g2d);
    }
    
    /**
     * A ShapeProvider indicates a shape for a displayed gem.
     * @author Edward Lam
     */
    interface ShapeProvider {
        /**
         * Get the displayed gem shape.
         * @param displayedGem the displayed gem for which the shape should be provided.
         * @return the displayed gem's shape.
         */
        public DisplayedGemShape getDisplayedGemShape(DisplayedGem displayedGem);
    }
    
    /**
     * A DisplayedGemShape representing an Oval/Pill shape.
     * @author Edward Lam
     */
    public static class Oval extends DisplayedGemShape {
        /**
         * Constructor for a Oval DisplayedGemShape.
         * @param displayedGem
         */
        Oval(DisplayedGem displayedGem) {
            super(displayedGem, TableTopGemPainter.getBoldNameInfo(displayedGem));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Point getInputConnectPoint(int inputIndex) {
            if (inputIndex == 0 && canHaveInputs(displayedGem)) {
                // The input is in the middle of the left edge..
                Rectangle inRect = getInBounds();
                int centreY = (int)inRect.getCenterY();
                return new Point(inRect.x, centreY);

            } else {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Shape getBodyShape() {
            Rectangle bodyBounds = getBodyBounds();
            
            // The main Gem rectangle - a rectangle with round corners
            double arcSize = bodyBounds.height / 2.0;
            Shape bodyShape = new RoundRectangle2D.Double(bodyBounds.getX(), bodyBounds.getY(), 
                                                          bodyBounds.getWidth() - 1.0, bodyBounds.getHeight() - 1.0, 
                                                          arcSize, arcSize);
            return bodyShape;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getDimensions() {
            // Get the graphics context for the context we will be drawing into
            Graphics g = getGraphics();

            // The X dimension is based on the size of the text for the name, plus some margins
            g.setFont(GemCutterPaintHelper.getBoldFont());
            FontMetrics fm = g.getFontMetrics();

            // Calculate width
            int connectingAreaWidth = 0;
            if (canHaveInputs(displayedGem)) {
                connectingAreaWidth += DisplayConstants.INPUT_AREA_WIDTH;
            } 
            if (hasOutput(displayedGem)) {
                connectingAreaWidth += DisplayConstants.OUTPUT_AREA_WIDTH;
            }
            int gemWidth = fm.stringWidth(displayedGem.getDisplayText()) + 2 * DisplayConstants.LET_LABEL_MARGIN + connectingAreaWidth;

            // Calculate height
            int gemHeight = DisplayConstants.ARGUMENT_SPACING;
            g.dispose();

            // Return the dimensions
            return new Dimension(gemWidth, gemHeight);
        }
    }
    
    
    /**
     * A DisplayedGemShape representing an Triangle shape.
     * @author Edward Lam
     */
    public static class Triangular extends DisplayedGemShape {

        /**
         * Constructor for a Triangular DisplayedGemShape.
         * @param displayedGem
         */
        Triangular(DisplayedGem displayedGem) {
            super(displayedGem, TableTopGemPainter.getNameTapeLabelInfo(displayedGem));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Point getInputConnectPoint(int arg) {
            Rectangle inRect = getInBounds();
            int y = inRect.y + DisplayConstants.ARGUMENT_SPACING * (arg + 1);
            return new Point(inRect.x, y);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getDimensions() {
            
            // Get the graphics context for the context we will be drawing into
            Graphics g = getGraphics();

            // The X dimension is based on the size of the text for the name, plus some margins
            g.setFont(GemCutterPaintHelper.getTitleFont());
            FontMetrics fm = g.getFontMetrics();
            g.dispose();

            // Calculate width
            String displayText = displayedGem.getDisplayText();
            int gemWidth = fm.stringWidth(displayText) + 8 * DisplayConstants.INPUT_OUTPUT_LABEL_MARGIN + DisplayConstants.BEVEL_WIDTH_X + DisplayConstants.BEVEL_WIDTH_Y + 2;
            if (canHaveInputs(displayedGem)) {
                gemWidth += DisplayConstants.INPUT_AREA_WIDTH;
            }
            if (hasOutput(displayedGem)) {
                gemWidth += DisplayConstants.OUTPUT_AREA_WIDTH;
            }

            // Calculate height
            int gemHeight = (Math.max(displayedGem.getGem().getNInputs(), 1) + 1) * DisplayConstants.ARGUMENT_SPACING;

            return new Dimension(gemWidth, gemHeight);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Shape getBodyShape() {
            Rectangle bodyBounds = getBodyBounds();
            float centreY = (float)bodyBounds.getCenterY();

            // The main Gem triangle 
            Polygon gemTri = new Polygon();
            gemTri.addPoint(bodyBounds.x, bodyBounds.y);
            gemTri.addPoint(bodyBounds.x + bodyBounds.width, (int) centreY);
            gemTri.addPoint(bodyBounds.x, bodyBounds.y + bodyBounds.height);
            return gemTri;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Point2D getCenterPoint() {
            // Note that for a triangle, the centroid is simply the average of its three vertices..
            // Thus, the weighted center is a third of the distance from the thicker side.
            Rectangle bodyBounds = getBodyBounds();
            double centreX = bodyBounds.getX() + (bodyBounds.getWidth() / 3.0);
            return new Point2D.Double(centreX, bodyBounds.getCenterY());
        }
    }
    
    /**
     * A DisplayedGemShape representing an Rectangular shape.
     * @author Edward Lam
     */
    public static class Rectangular extends DisplayedGemShape {
        /**
         * Constructor for a Rectangular DisplayedGemShape.
         * @param displayedGem
         * @param innerComponentInfo
         */
        Rectangular(DisplayedGem displayedGem, InnerComponentInfo innerComponentInfo) {
            super(displayedGem, innerComponentInfo);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Point getInputConnectPoint(int inputIndex) {
            if (inputIndex == 0 && canHaveInputs(displayedGem)) {
                Rectangle inRect = getInBounds();
                int centreY = (int)inRect.getCenterY();
                return new Point(inRect.x, centreY);

            } else {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Shape getBodyShape() {
            Rectangle bodyBounds = getBodyBounds();

            // The main Gem rectangle 
            Polygon gemRect = new Polygon();
            gemRect.addPoint(bodyBounds.x, bodyBounds.y);
            gemRect.addPoint(bodyBounds.x + bodyBounds.width, bodyBounds.y);
            gemRect.addPoint(bodyBounds.x + bodyBounds.width, bodyBounds.y + bodyBounds.height);
            gemRect.addPoint(bodyBounds.x, bodyBounds.y + bodyBounds.height);
            return gemRect;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getDimensions() {
            
            Rectangle innerComponentBounds = innerComponentInfo.getBounds();

            // Calculate width
            int gemWidth = innerComponentBounds.width + DisplayConstants.BEVEL_WIDTH_X * 2 + 1;
            if (canHaveInputs(displayedGem)) {
                gemWidth += DisplayConstants.INPUT_AREA_WIDTH;
            }
            if (hasOutput(displayedGem)) {
                gemWidth += DisplayConstants.OUTPUT_AREA_WIDTH;
            }

            // Calculate height
            int gemHeight = innerComponentBounds.height + DisplayConstants.BEVEL_WIDTH_Y * 2 + 1;

            // Return the dimensions
            return new Dimension(gemWidth, gemHeight);
        }            
    }


    /**
     * Constructor for a DisplayedGemShape
     * @param displayedGem the gem being displayed.
     * @param innerComponentInfo the info on the component which should fit inside the shape of the gem.
     */
    DisplayedGemShape(DisplayedGem displayedGem, InnerComponentInfo innerComponentInfo) {
        if (displayedGem == null || innerComponentInfo == null) {
            throw new NullPointerException("Arguments must not be null.");
        }
        this.displayedGem = displayedGem;
        this.innerComponentInfo = innerComponentInfo;
    }
    
    /**
     * Get information on the image which is supposed to fit inside the shape representing the displayed gem
     * @return the inner component info..
     */
    public InnerComponentInfo getInnerComponentInfo() {
        return innerComponentInfo;
    }

    /**
     * Determine the shape's dimensions.  
     * @return the dimensions of the gem.
     */
    public abstract Dimension getDimensions();

    /**
     * Return a Shape representing this Gem's body.
     * @return Shape the body shape
     */
    public abstract Shape getBodyShape();
    
    /**
     * Get the Shape representing the body shape of this Gem, scaled by an appropriate amount
     * @param scaleFactor the scaling factor of the shape.
     * @return Shape the body shape, scaled by scaleFactor
     */
    Shape getScaledBodyShape(double scaleFactor) {
        Shape bodyShape = getBodyShape();
        Point2D centroid = getCenterPoint();

        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
        double transformedX = centroid.getX() / scaleFactor;
        double transformedY = centroid.getY() / scaleFactor;
        scaleTransform.translate(transformedX - centroid.getX(), transformedY - centroid.getY());
        
        return scaleTransform.createTransformedShape(bodyShape);
    }
    
    /**
     * Get a fresh, unscaled graphics object from which device-free (user-space) calculations can be performed.
     * @return an unscaled graphics object.
     */
    static Graphics getGraphics() {
        return dummyImage.getGraphics();
    }

    /**
     * Return a copy of the Gem's bounds.
     * @return Rectangle the bounds of the Gem
     */
    public final Rectangle getBounds() {
        // If we don't already have have a cached bounds, calculate this now
        if (cachedBounds == null) {
            // The bounds are the combination of location and size
            cachedBounds = new Rectangle(displayedGem.getLocation(), getDimensions());
        }
        return new Rectangle(cachedBounds);
    }

    /**
     * Determine the Gem's input area bounds.
     * Note that even if the gem currently has no input, if there exists a potential for input 
     *   (eg. 0-input DataConstructors or CodeGems)
     * @return Rectangle the bounds of the input area, or null if there cannot be any inputs.
     */
    public Rectangle getInBounds() {
        if (canHaveInputs(displayedGem)) {
            // The bounds are the same as the full size, less a lot of it's width
            Rectangle inBounds = getBounds();
            inBounds.width = DisplayConstants.INPUT_AREA_WIDTH;
            return inBounds;

        } else {
            return null;
        }
    }

    /**
     * Determine the Gem's output area bounds.
     * @return Rectangle the bounds of the output area, or null if there is no output.
     */
    public final Rectangle getOutBounds() {
        // For now, just return the output area - should be finer grained!
        if (hasOutput(displayedGem)) {
            // The bounds are the same as the full size, less a lot of it's width
            Rectangle outBounds = getBounds();
            outBounds.x += outBounds.width - DisplayConstants.OUTPUT_AREA_WIDTH;
            outBounds.width = DisplayConstants.OUTPUT_AREA_WIDTH;
            return outBounds;

        } else {
            return null;
        }
    }

    /**
     * Determine the Gem's body area bounds.
     * @return Rectangle the bounds of the body section
     */
    public final Rectangle getBodyBounds() {
        // If we don't already have have a cached bounds, calculate this now
        if (cachedBodyBounds == null) {
            // The bounds are the same as the full size, minus some of the width for connecting bits
            cachedBodyBounds = new Rectangle(getBounds());
            
            if (hasOutput(displayedGem)) {
                cachedBodyBounds.width -= DisplayConstants.OUTPUT_AREA_WIDTH;
            }
            
            if (canHaveInputs(displayedGem)) {
                cachedBodyBounds.x += DisplayConstants.INPUT_AREA_WIDTH;
                cachedBodyBounds.width -= DisplayConstants.INPUT_AREA_WIDTH;
            }

        }
        return new Rectangle(cachedBodyBounds);
    }
    
    /**
     * Clear bound objects.
     */
    void purgeCachedBounds() {
        cachedBounds = null;
        cachedBodyBounds = null;
    }

    /**
     * Get the weighted center point of this Gem's body.
     * In mathematics, this is known as the body shape's "centroid".
     * @return the centroid of the gem body.
     */
    public Point2D getCenterPoint() {
        // by default, the point will be in the centre of the body bounds.
        Rectangle bounds = getBodyBounds();
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    /**
     * Get the point at which connects to the nth input are made
     * @param inputIndex the argument number
     * @return the input point
     */
    public abstract Point getInputConnectPoint(int inputIndex);

    /**
     * Get the point at which connects to the output are made
     * @return the output point
     */
    public Point getOutputConnectPoint() {
        // For now, the connection points for outputs can all be calculated in the same way.
        Rectangle outRect = getOutBounds();
        float centreY = (float) outRect.getCenterY();
        return new Point(outRect.x + outRect.width, (int)centreY);
    }

    /**
     * Return an Shape representing this Gem's nth input.
     * @param bindPoint the location of the bind point
     * @return Shape the shape of the bind point
     */
    static Shape getInputShape(Point bindPoint) {

        // Where does this blob go vertically
        int x = bindPoint.x;
        int y = bindPoint.y;

        // Where does this blob go vertically
        return new Ellipse2D.Double(x + DisplayConstants.BIND_POINT_RADIUS * 0.5, y - DisplayConstants.BIND_POINT_RADIUS, DisplayConstants.BIND_POINT_RADIUS * 2, DisplayConstants.BIND_POINT_RADIUS * 2);
    }

    /**
     * Return a polygon (arrow) representing this Gem's output.
     * @return Polygon the output polygon
     */
    Polygon getOutputShape() {
        Point connectPoint = getOutputConnectPoint();
        int x = connectPoint.x;
        int y = connectPoint.y;
        
        Polygon arrow = new Polygon();
        arrow.addPoint(x - DisplayConstants.OUT_ARROW_SIZE, y - DisplayConstants.OUT_ARROW_SIZE);
        arrow.addPoint(x, y);
        arrow.addPoint(x - DisplayConstants.OUT_ARROW_SIZE, y + DisplayConstants.OUT_ARROW_SIZE);
        return arrow;
    }

    /**
     * Return a dithered (larger) shape representing this Gem's output.
     * Use this to determine whether a mouse click is 'close' enough.
     * @return Polygon the output polygon
     */
    private Shape getDitheredOutputShape() {
        if (hasOutput(displayedGem)) {
            Point connectPoint = getOutputConnectPoint();
            int x = connectPoint.x;
            int y = connectPoint.y;

            int arrowSize = DisplayConstants.OUT_ARROW_SIZE + DisplayConstants.DITHER_FACTOR;
            
            Polygon arrow = new Polygon();
            arrow.addPoint(x + (int)(DisplayConstants.DITHER_FACTOR * 0.75) - arrowSize, y - arrowSize);
            arrow.addPoint(x + (int)(DisplayConstants.DITHER_FACTOR * 0.75), y);
            arrow.addPoint(x + (int)(DisplayConstants.DITHER_FACTOR * 0.75) - arrowSize, y + arrowSize);
            return arrow;

        } else {
            return null;
        }
    }

    /**
     * Return whether a given gem can have any inputs.
     * Note that this is different from whether a given gem actually does have any inputs.
     * eg. a code gem may or may not have inputs, but calling this method on that code gem will still always true.
     * @param displayedGem the gem in question.
     * @return whether the given gem can have any inputs.
     */
    private static boolean canHaveInputs(DisplayedGem displayedGem) {
        return !(displayedGem.getGem() instanceof ValueGem);
    }

    /**
     * Return whether a given gem has an output part.
     * @param displayedGem the gem in question.
     * @return whether the given gem has an output.
     */
    private static boolean hasOutput(DisplayedGem displayedGem) {
        return !(displayedGem.getGem() instanceof CollectorGem);
    }

    /**
     * Determine if the body is under the given point.
     * @param xy Point the point to test for
     * @return boolean whether we hit
     */
    public final boolean bodyHit(Point xy) {
        // Does this hit the body
        return getBodyShape().contains(xy);
    }

    /**
     * Determine if the output blob is under the given point.
     * @param xy Point the point to test for
     * @return boolean whether we hit
     */
    public final boolean outputHit(Point xy){
        if (hasOutput(displayedGem)) {
            // Test the blob, allow for 'close' hits.
            return getDitheredOutputShape().contains(xy);

        } else {
            return false;
        }
    }

    /**
     * Determine if (which) input blob under the given point.
     * @param xy Point the point to test for
     * @return int which input we hit (-1 for none)
     */
    public final int inputHit(Point xy) {
        // Some useful constants..
        // The input circle is drawn with the given radius.  We allow clickability within a certain dither amount from the circle.
        double radialDither = DisplayConstants.DITHER_FACTOR/2;
        double ditheredRadius = DisplayConstants.BIND_POINT_RADIUS + radialDither;
        
        if (canHaveInputs(displayedGem)) {

            // Iterate over the inputs.
            int nInputs = displayedGem.getNDisplayedArguments();
            for (int i = 0; i < nInputs; i++) {

                // Get the connect point.
                Point connectPoint = getInputConnectPoint(i);

                // test for distance..
                double distance = Math.sqrt(Math.pow(xy.getX() - connectPoint.getX(), 2) + Math.pow(xy.getY() - connectPoint.getY(), 2));
                if (distance <= ditheredRadius) {
                    return i;
                }
            }
        }
        
        return -1;
    }

    /**
     * Determine if any input name tag is under the given point
     * @return int index of the input which we hit, -1 for none(no name tags at all or none were clicked)
     */ 
    public final int inputNameTagHit(Point xy){

        List<Rectangle> nameTagBounds = innerComponentInfo.getInputNameLabelBounds();
        
        if (nameTagBounds != null){

            for (int i = 0, nRects = nameTagBounds.size(); i < nRects ; i ++) { 
                Rectangle nameTagBound = nameTagBounds.get(i); 
                
                if (nameTagBound.contains(xy)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
}


