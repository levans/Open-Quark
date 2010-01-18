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
 * MonitorSidePanel.java
 * Created: 23-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * 
 *  
 */
class MonitorSidePanel extends JPanel {

    private static final long serialVersionUID = 7871504525097686459L;

    private final ImageIcon sidebarMain;

    private final ImageIcon sidebarTop;

    MonitorSidePanel () {
        sidebarMain = loadIcon ("BAMSidebarMain.png");
        sidebarTop  = loadIcon ("BAMSidebarTop.png");

        if (sidebarMain != null) {
            setPreferredSize (new Dimension (sidebarMain.getIconWidth (), sidebarMain
                    .getIconHeight ()));
        }
    }
    
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent (Graphics g) {
        if (sidebarMain == null || sidebarTop == null) {
            super.paintComponent(g);
            
            return;
        }
        
        Dimension panelSize = getSize ();
        
        int x = 0;
        int y = panelSize.height - sidebarMain.getIconHeight();
        
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawImage (sidebarMain.getImage(), x, y, sidebarMain.getIconWidth(), sidebarMain.getIconHeight(), null);
        
        while (y > 0) {
            y -= sidebarTop.getIconHeight();
            
            g2.drawImage (sidebarTop.getImage(), x, y, sidebarTop.getIconWidth(), sidebarTop.getIconHeight(), null);
        }
    }

    /**
     * Method loadIcon
     * 
     * @param iconFileName
     * @return Returns an AimageIcon loaded from the given resource file
     */
    private ImageIcon loadIcon (String iconFileName) {
        String iconFilePath = "/Resources/" + iconFileName; //$NON-NLS-1$ 

        URL url = getClass ().getResource (iconFilePath);

        if (url != null) {
            return new ImageIcon (url);
        } else {
            return null;
        }
    }
}