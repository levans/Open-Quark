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
 * NavEditorPanel.java
 * Creation date: Oct 18, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.utilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

/**
 * A smooth, partly transparent border with an optional color highlight for the value editors
 * and the Intellicut panel.
 * @author Frank Worsley
 */
public class SmoothHighlightBorder implements Border {

    /** The size of the border. If you change this you MUST update the getShades() method. */
    private static final int SIZE = 8;
    
    /** Height of the drag area at the top of the border. */
    private static final int TITLE_BAR_HEIGHT = 6;
    
    /** The insets of the border without a title bar. */
    public static final Insets NORMAL_INSETS = new Insets(SIZE, SIZE, SIZE, SIZE);
    
    /** The insets of the border with a title bar. */
    private static final Insets TITLE_BAR_INSETS = new Insets(SIZE + TITLE_BAR_HEIGHT, SIZE, SIZE, SIZE);
        
    /** The highlight colour to use. */
    private final Color highlight;
    
    /** Whether to draw a title bar. */
    private final boolean drawTitleBar;
        
    /** The background colour of the component when the shades were last calculated. */
    private Color lastBackground = null;
        
    /** The shade colours that were last calculated. */
    private Color[] shades;

    /**
     * Constructs a new IntellicutPanelBorder with the given highlight colour.
     * @param highlight the highlight colour
     * @param drawTitleBar whether to draw a small titlebar at the top of the border
     */
    public SmoothHighlightBorder(Color highlight, boolean drawTitleBar) {
            
        if (highlight == null) {
            throw new NullPointerException();
        }
            
        this.highlight = highlight;
        this.drawTitleBar = drawTitleBar;
    }
        
    /**
     * Calculates the shades of colour we want to use for shading from the component background,
     * to the highlight colour and then to transparency. The shades are only re-calculated if the
     * background colour changes. The inner shade is at index 0, the most outer shade at index 7.
     * @param background the component background colour
     * @return an array of shade colours from inside to outside
     */
    private Color[] getShades(Color background) {

        if (shades != null && background.equals(lastBackground)) {
            return shades;
        }

        lastBackground = background;

        shades = new Color[SIZE];

        // Figure out the inital color we fade to. From that color we fade to complete transparency.
        // If the highlight is a light color we want to fade to a dark color and vice versa.
        // We say the highlight is a light color if two RGB values are over 128.

        boolean redOver = highlight.getRed() >= 128;
        boolean greenOver = highlight.getGreen() >= 128;
        boolean blueOver = highlight.getBlue() >= 128;

        int iRed = -1;
        int iGreen = -1;
        int iBlue = -1;

        if ((redOver && greenOver) || (redOver && blueOver) || (greenOver && blueOver)) {
            iRed = iGreen = iBlue = 50;
        } else {
            iRed = iGreen = iBlue = 150;
        }

        Color initialFade = new Color(iRed, iGreen, iBlue, highlight.getAlpha() / 2);
        Color finalFade = new Color(iRed, iGreen, iBlue, 0);

        // Fade from component background to the higlight color over a 3 pixel range.

        int rDiff = (highlight.getRed() - background.getRed()) / 3;
        int gDiff = (highlight.getGreen() - background.getGreen()) / 3;
        int bDiff = (highlight.getBlue() - background.getBlue()) / 3;
        int aDiff = (highlight.getAlpha() - background.getAlpha()) / 3;
            
        for (int i = 1; i <= 3; i++) {
            shades[8 - i] = new Color(background.getRed()   + rDiff * i,
                                      background.getGreen() + gDiff * i,
                                      background.getBlue()  + bDiff * i,
                                      background.getAlpha() + aDiff * i);
        }
            
        // The 4th pixel is the initial fade color.
            
        shades[4] = initialFade;
            
        // Then fade from inital fade to transparency over 4 pixels.
            
        rDiff = (finalFade.getRed()   - initialFade.getRed())   / 4;
        gDiff = (finalFade.getGreen() - initialFade.getGreen()) / 4;
        bDiff = (finalFade.getBlue()  - initialFade.getBlue())  / 4;
        aDiff = (finalFade.getAlpha() - initialFade.getAlpha()) / 4;
            
        for (int i = 1; i <= 4; i++) {
            shades[4 - i] = new Color(initialFade.getRed()   + rDiff * i,
                                      initialFade.getGreen() + gDiff * i,
                                      initialFade.getBlue()  + bDiff * i,
                                      initialFade.getAlpha() + aDiff * i);
        }
            
        return shades;
    }

    /**
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Color[] shades = getShades(c.getBackground());

        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = buffer.getGraphics();

        // Setup some constant values.
        int cx0 = x + SIZE + 1;
        int cy0 = y + SIZE + 1;

        int cx1 = x + width - SIZE - 2;
        int cy1 = y + height - SIZE - 2;
            
        // Setup values that will be manipulated in the loop.
        int x1 = x + width - 1;
        int y1 = y + height - 1;
            
        int x2 = x + width - 2*SIZE - 1;
        int y2 = y + height - 2*SIZE - 1;

        for (int i = 0; i < SIZE; i++) {

            // We draw the outermost lines first, then move inwards.

            Color color = shades[i];
            bg.setColor(color);
                
            // draw top & bottom
            bg.drawLine(cx0, y, cx1, y);
            bg.drawLine(cx0, y1, cx1, y1);
                
            // draw left & right
            bg.drawLine(x, cy0, x, cy1);
            bg.drawLine(x1, cy0, x1, cy1);

            int arcSize = 2 * (SIZE - i);
                
            // draw top-left & right corners
            bg.drawArc(x, y, arcSize, arcSize, 180, -90);
            bg.drawArc(x2, y, arcSize, arcSize, 0, 90);
                
            // draw bottom-left & right corners
            bg.drawArc(x, y2, arcSize, arcSize, 180, 90);
            bg.drawArc(x2, y2, arcSize, arcSize, 0, -90);
                
            // update coordinates
            x++;
            y++;
                
            x1--;
            y1--;
                
            y2++;
            x2++;
        }
        
        if (drawTitleBar) {
            // Draw the title bar.
            bg.setColor(highlight);
            bg.fillRect(SIZE, SIZE, width - 2 * SIZE, TITLE_BAR_HEIGHT);
            
            bg.setColor(shades[SIZE - 1]);
            bg.drawRect(SIZE - 1, SIZE - 1, width - 2 * SIZE + 1, TITLE_BAR_HEIGHT);
        }
        
        // Draw the image to the main graphics context
        g.drawImage(buffer, 0, 0, null);

        bg.dispose();
        buffer.flush();
    }

    /**
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    public Insets getBorderInsets(Component c) {
        return drawTitleBar ? (Insets) TITLE_BAR_INSETS.clone() : (Insets) NORMAL_INSETS.clone();
    }
    
    /**
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    public boolean isBorderOpaque() {
        return false;
    }
}
