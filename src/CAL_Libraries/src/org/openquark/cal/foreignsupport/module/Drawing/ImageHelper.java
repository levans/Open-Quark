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
 * ImageHelper.java
 * Creation date: Sept 19, 2005.
 * By: Richard Webster
 */

package org.openquark.cal.foreignsupport.module.Drawing;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Helper functions for working with images from CAL.
 */
public class ImageHelper {
    
    /* Private constructor since all methods are static. */
    private ImageHelper() {}

    /**
     * Draws an entire image (full size) at the specified coordinates.
     */
    public static Graphics2D drawImage(Image img, double x, double y, Graphics2D graphics) {
        graphics.drawImage(img, round(x), round(y), null);
        return graphics;
    }

    /**
     * Draws an entire image (full size) at the specified coordinates.
     */
    public static Graphics2D drawImage(Image img, double x, double y, double w, double h, Graphics2D graphics) {
        graphics.drawImage(img, round(x), round(y), round(w), round(h), null);
        return graphics;
    }

    /**
     * @return an image for the specified icon.
     */
    public static Image getIconImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            ImageIcon imageIcon = (ImageIcon) icon;
            return imageIcon.getImage();
        } else {
            Image image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            icon.paintIcon(null, g2, 0, 0);
            
            icon = new ImageIcon(image);
            g2.dispose();

            return image;
        }
    }

    /**
     * @return the width of an image.
     */
    public static int getImageWidth(Image image) {
        return image.getWidth(null);
    }

    /**
     * @return the height of an image.
     */
    public static int getImageHeight(Image image) {
        return image.getHeight(null);
    }

    /**
     * @return a Graphics2D for an image.
     */
    public static Graphics2D getImageGraphics(Image image) {
        return (Graphics2D) image.getGraphics();
    }
    
    /*
     * @return the value, rounded to the nearest integer.
     */
    private static int round(double val) {
        return (int) Math.round(val);
    }
}
