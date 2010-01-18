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
 * CALSyntaxStyleListener.java
 * Creation date: (1/30/01 8:02:53 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.caleditor;

/**
 * A listener interface for objects wishing to engage in the process of assigning styles
 * to syntax elements directly, rather than relying on static assignments for scan codes.
 * Creation date: (1/30/01 8:02:53 AM)
 * @author Luke Evans
 */
public interface CALSyntaxStyleListener {
    /**
     * Lookup the font for a given scanCode.
     * Creation date: (1/30/01 8:05:40 AM)
     * @return java.awt.Font the font to apply
     * @param scanCode int the scan code (token type)
     * @param image String the token image
     * @param suggestedFont java.awt.Font the suggested font (default or user set) or null
     */
    public java.awt.Font fontLookup(int scanCode, String image, java.awt.Font suggestedFont);
    
    /**
     * Lookup the foreground colour for a given scanCode.
     * Creation date: (1/30/01 8:05:40 AM)
     * @return Style the font to apply
     * @param scanCode int the scan code (token type)
     * @param image String the token image
     * @param suggestedColour java.awt.Color the suggested colour (default or user set) or null
     */
    public java.awt.Color foreColourLookup(int scanCode, String image, java.awt.Color suggestedColour);
    
    /**
     * Lookup the style for a given scanCode.
     * Creation date: (1/30/01 8:05:40 AM)
     * @return javax.swing.text.Style the style to apply
     * @param scanCode int the scan code (token type)
     * @param image String the token image 
     * @param suggestedStyle java.awt.Color the suggested style (default or user set) or null
     */
    public javax.swing.text.Style styleLookup(int scanCode, String image, javax.swing.text.Style suggestedStyle);
}
