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
 * OverviewPanel.java
 * Creation date: (8/24/01 10:11:37 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JViewport;

/**
 * The OverviewPanel displays a map (overview) of the larger Table Top.
 * @author Luke Evans
 */
public class OverviewPanel extends JPanel {
    private static final long serialVersionUID = -6299684648858365320L;
    private final TableTopPanel tableTopPanel;

    /**
     * This mouse handler handles mouse presses within the overview panel.
     * These mouse presses move the viewport within the bounds of the overview panel.
     * @author Edward Lam
     */
    private class MouseHandler extends MouseAdapter {
        /**
         * {@inheritDoc}
         */
        @Override
        public void mousePressed(MouseEvent e) {

            // Get the viewport.
            JViewport tableTopViewport = getViewport();
            if (tableTopViewport == null) {
                return;
            }
            
            // Calculate the viewport size relative to the tabletop.
            Dimension tableTopSize = tableTopPanel.getSize();
            double overviewXRatio = getWidth() / tableTopSize.getWidth();
            double overviewYRatio = getHeight() / tableTopSize.getHeight();

            // Translate the mouse click to the corresponding coords on the TableTop.
            int tableX = (int)(e.getX() / overviewXRatio);
            int tableY = (int)(e.getY() / overviewYRatio);

            // OK, we need to generate a rectangle which contrives to have this point at the centre.  
            // The rectangle will be specified in the tabletop's coordinate space
            
            // Generate a 'virtual' viewport around this point (of the correct size)
            Dimension vpSize = tableTopViewport.getExtentSize();
            Rectangle newVP = new Rectangle(tableX - vpSize.width/2, tableY - vpSize.height/2, vpSize.width, vpSize.height);

            // Justify this rectangle w.r.t table top if required
            newVP.x -= Math.max((newVP.x + newVP.width) - tableTopSize.width, 0);
            newVP.y -= Math.max((newVP.y + newVP.height) - tableTopSize.height, 0); 
            if (newVP.x < 0) {
                newVP.x = 0;
            }
            if (newVP.y < 0) {
                newVP.y = 0;
            }

            // Ask the viewport to show this virtual viewport rectangle
            tableTopViewport.setViewPosition(newVP.getLocation());
        }
        
        /**
         * Get the viewport which holds this tabletop.
         * @return the viewport enclosing this tabletop, or null if no viewport is an ancestor of this tabletop.
         */
        private JViewport getViewport() {
            // take the viewport from the ancestry of the table top panel.
            for (Component comp = tableTopPanel.getParent(); comp != null; comp = comp.getParent()) {
                if (comp instanceof JViewport) {
                    return (JViewport)comp;
                }
            }
            return null;
        }
    }
    
    /**
     * OverviewPanel constructor.
     * @param tableTopPanel the tabletop panel for which this overview will be painted.
     */
    public OverviewPanel(TableTopPanel tableTopPanel) {
        setMinimumSize(new Dimension(150, 100));
        setPreferredSize(getMinimumSize());

        // Register this as it's own listener for a number of event classes
        addMouseListener(new MouseHandler());

        // Set the new table top    
        this.tableTopPanel = tableTopPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {   
        super.paintComponent(g);

        // Check for nothing to paint.
        Dimension tableTopSize = tableTopPanel.getSize();
        if (tableTopSize.width == 0 || tableTopSize.height == 0) {
            return;
        }
        
        // Determine overview ratios
        double overviewXRatio = getWidth() / tableTopSize.getWidth();
        double overviewYRatio = getHeight() / tableTopSize.getHeight();

        // Get the tabletop to paint itself into the overview.
        // We do this by setting an appropriate scaling factor on the graphics object.  Also antialias so things look nice.
        Graphics2D g2d = (Graphics2D)g;
        Object oldAntiAliasHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Color oldColor = g2d.getColor();
        
        g2d.scale(overviewXRatio, overviewYRatio);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        tableTopPanel.paintComponent(g2d);

        
        // Paint the extent of the visible rectangle if one exists
        g2d.setColor(Color.black);
        Rectangle visibleRect = tableTopPanel.getVisibleRect();
        g2d.drawRect(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);

        
        // Reset the graphics object.
        g2d.scale(1/overviewXRatio, 1/overviewYRatio);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasHint);
        g2d.setColor(oldColor);
    }
}
