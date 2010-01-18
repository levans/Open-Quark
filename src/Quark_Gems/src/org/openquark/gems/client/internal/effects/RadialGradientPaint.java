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
 * RadialGradientPaint.java
 * Creation date: Dec 2, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.internal.effects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * A paint class that implements a radial gradient paint.
 * @author Frank Worsley
 */
class RadialGradientPaint implements Paint {

    /**
     * Paint context class for the RadialGradientPaint class.
     * @author Frank Worsley
     */
    private class RadialGradientPaintContext implements PaintContext {

        /**
         * @see java.awt.PaintContext#dispose()
         */
        public void dispose() {
        }

        /**
         * @see java.awt.PaintContext#getColorModel()
         */
        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        /**
         * @see java.awt.PaintContext#getRaster(int, int, int, int)
         */
        public Raster getRaster(int x, int y, int w, int h) {

            int[] data = new int[w * h * 4];
            double r = radius.distance(0, 0);
            
            for (int dy = 0; dy < h; dy++) {
                
                for (int dx = 0; dx < w; dx++) {
                    
                    double ratio = center.distance(x + dx, y + dy) / r;
                    
                    if (ratio > 1.0) {
                        ratio = 1.0;
                    }
                    
                    int base = (dy * w + dx) * 4;
                    
                    data[base + 0] = (int) (inside.getRed()   + ratio * (outside.getRed()   - inside.getRed()));
                    data[base + 1] = (int) (inside.getGreen() + ratio * (outside.getGreen() - inside.getGreen()));
                    data[base + 2] = (int) (inside.getBlue()  + ratio * (outside.getBlue()  - inside.getBlue()));
                    data[base + 3] = (int) (inside.getAlpha() + ratio * (outside.getAlpha() - inside.getAlpha()));
                }
            }

            WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
            raster.setPixels(0, 0, w, h, data);
            
            return raster;
        }
        
    }
    
    /** The color at the center of the gradient. */
    private final Color inside;
    
    /** The color at the outside of the gradient. */
    private final Color outside;
    
    /** The point at the center of the gradient. */
    private final Point2D center;
    
    /** The point that defines the radius of the gradient. */
    private final Point2D radius;
    
    /**
     * Constructor for a new RadialGradientPaint.
     * @param center the point from which to draw the gradient
     * @param radius the radius of the gradient in the x and y dimensions
     * @param inside the color at the center of the gradient
     * @param outside the color at the ourside of the gradient
     */
    public RadialGradientPaint(Point2D center, Point2D radius, Color inside, Color outside) {
        
        if (center == null || radius == null || inside == null || outside == null) {
            throw new NullPointerException();
        }
        
        if (radius.distance(0, 0) <= 0) {
            throw new IllegalArgumentException("invalid radius");
        }
        
        this.center = center;
        this.radius = radius;
        this.inside = inside;
        this.outside = outside;
    }
    
    /**
     * @see java.awt.Paint#createContext(java.awt.image.ColorModel, java.awt.Rectangle, java.awt.geom.Rectangle2D, java.awt.geom.AffineTransform, java.awt.RenderingHints)
     */
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        
        xform.transform(radius, radius);
        xform.transform(center, center);
        
        return new RadialGradientPaintContext();
    }

    /**
     * @see java.awt.Transparency#getTransparency()
     */
    public int getTransparency() {
        int i = inside.getAlpha();
        int o = outside.getAlpha();
        return (i & o) == 0xff ? Transparency.OPAQUE : Transparency.TRANSLUCENT;
        
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("RadialGradientPaint Test");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent painter = new JComponent() {
            private static final long serialVersionUID = -2901609221977900951L;

            /**
             * @see javax.swing.JComponent#paint(java.awt.Graphics)
             */
            public void paint(Graphics g) {
                super.paint(g);
                
                RadialGradientPaint paint =
                    new RadialGradientPaint(
                        new Point2D.Double(100, 100),
                        new Point2D.Double(20, 20),
                        Color.YELLOW,
                        new Color (0xFF, 0xFF, 0x00, 0x00));
                
                Graphics2D g2 = (Graphics2D) g;
                
                g2.setPaint (paint);
                
                g2.fillRect(40, 40, 120, 120);
            }
        };
        
        painter.setPreferredSize(new Dimension (200, 200));

        frame.getContentPane().add(painter);

        frame.pack();

        frame.setVisible(true);
    }
}

