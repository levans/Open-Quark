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
 * ResourceHelper.java
 * Creation date: July 14, 2005.
 * By: Richard Webster
 */

package org.openquark.cal.foreignsupport.module.Drawing;

import java.awt.Image;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Helper functions for loading and working with resources from CAL.
 */
public class ResourceHelper {

    /* Private constructor since all methods are static. */
    private ResourceHelper() {}
    
    /**
     * Loads an icon with the specified resource name.
     */
    public static Icon getResourceIcon(String iconResourceName) {
        if (iconResourceName == null || iconResourceName.length () == 0) {
            return null;
        }

        URL url = ResourceHelper.class.getResource("/Resources/" + iconResourceName + ".gif");
        if (url == null) {
            return null;
        }

        return new ImageIcon(url);
    }

    /**
     * Loads an image with the specified resource name.
     */
    public static Image getResourceImage(String imageResourceName) {
        if (imageResourceName == null || imageResourceName.length () == 0) {
            return null;
        }

        URL url = ResourceHelper.class.getResource("/Resources/" + imageResourceName);
        if (url == null) {
            return null;
        }

        return new ImageIcon(url).getImage();
    }
}
