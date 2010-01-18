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
 * GemCutterPaintHelper.java
 * Creation date: Jan 19, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * This class contains a number of utility methods to aid in manipulating and painting shapes and images.
 * @author Edward Lam
 */
public class GemCutterPaintHelper {

    private static Font boldFont = new Font("sansserif", Font.BOLD, 12);
    private static Font titleFont = new Font("serif", Font.PLAIN, 12);

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

    
    //
    // Not intended to be instantiated.
    //
    private GemCutterPaintHelper() {
    }

    /**
     * Get a resized image.
     * The original image will appear in the resized image, but will be padded or cropped as appropriate.
     * @param originalImage the original image
     * @param newSize Dimension the size to which to pad/crop.
     * @return BufferedImage the resulting image.
     */
    public static BufferedImage getResizedImage(Image originalImage, Dimension newSize) {
        // construct the buffered image
        BufferedImage bImage = new BufferedImage(newSize.width, newSize.height, BufferedImage.TYPE_INT_ARGB);
    
        // obtain its graphics
        Graphics2D g2d = bImage.createGraphics();
    
        // draw the original image into the new BufferedImage.
        g2d.drawImage(originalImage, null, null);
    
        // dispose the graphics object
        g2d.dispose();
    
        return bImage;
    }

    /**
     * Get a scaled image.
     * The original image will be scaled so that it fits the new dimensions.
     * @param originalImage the original image
     * @param newSize Dimension the size to which to scale.
     * @return BufferedImage the resulting image.
     */
    public static BufferedImage getScaledImage(Image originalImage, Dimension newSize) {
        // construct the buffered image
        BufferedImage bImage = new BufferedImage(newSize.width, newSize.height, BufferedImage.TYPE_INT_ARGB);
        
        // obtain its graphics
        Graphics2D g2d = bImage.createGraphics();
        g2d.setRenderingHints(RENDERING_HINTS_HIGH_QUALITY);
        
        // Create a scale transform.
        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(newSize.getWidth() / originalWidth, newSize.getHeight() / originalHeight);
        g2d.setTransform(scaleTransform);
        
        // draw the original image into the new BufferedImage.
        g2d.drawImage(originalImage, null, null);
        
        // dispose the graphics object
        g2d.dispose();
        
        return bImage;
    }
    
    /**
     * Add an alpha-channel to the given colour.
     * @param colour the colour for which the alpha-channel will be set.
     * @return the corresponding colour with an appropriate alpha-channel value.
     */
    static Color getAlphaColour(Color colour, int alpha) {
        return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
    }
    
    /**
     * Get the bold font for the TableTop.
     * @return the TableTop bold font.
     */
    static Font getBoldFont() {
        return boldFont;    
    }

    /**
     * Get the title font for the TableTop.
     * @return the TableTop title font.
     */
    static Font getTitleFont() {
        return titleFont;
    }


}
