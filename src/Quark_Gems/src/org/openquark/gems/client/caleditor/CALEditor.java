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
 * CALEditor.java
 * Creation date: (1/18/01 1:55:49 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client.caleditor;

import java.awt.Font;

/**
 * The CALEditor is a JEditorPane for editing CAL source.
 * It comes ready-made with a CAL chroma-coder and document type specifically designed for
 * CAL source content.
 * Creation date: (1/18/01 1:55:49 PM)
 * @author Luke Evans
 */
public abstract class CALEditor extends javax.swing.JEditorPane {
    private CALEditorKit CALKit;

    /**
     * CALEditor constructor comment.
     */
    public CALEditor() {
        super();
        // Set up the editor correctly, we'll declare the CALEditorKit as handling the
        // "text/calsource" MIME type
        CALKit = new CALEditorKit();
        setEditorKitForContentType("text/calsource", CALKit);
        setContentType("text/calsource");
        setFont(new java.awt.Font("monospaced", Font.PLAIN, 12));
        setBackground(java.awt.Color.white);
        setEditable(true);
        
        // Set up some special styles (comments)?
        // CALStyleContext styles = CALKit.getStylePreferences();
        
        // ...TODO, stuff like...
        // Style s = styles.getStyleForScanValue(scan_index_or_manifest);
        // StyleConstants.setForeground(s, myColour);
        // ...
        
    }

    /**
     * CALEditor constructor comment.
     * @param url java.lang.String
     * @exception java.io.IOException The exception description.
     */
    public CALEditor(String url) throws java.io.IOException {
        super(url);
    }

    /**
     * CALEditor constructor comment.
     * @param type java.lang.String
     * @param text java.lang.String
     */
    public CALEditor(String type, String text) {
        super(type, text);
    }
    /**
     * CALEditor constructor comment.
     * @param initialPage java.net.URL
     * @exception java.io.IOException The exception description.
     */
    public CALEditor(java.net.URL initialPage) throws java.io.IOException {
        super(initialPage);
    }
    /**
     * Add a style listener to the editor.
     * Creation date: (1/30/01 8:27:46 AM)
     * @param listener org.openquark.gems.client.caleditor.CALSyntaxStyleListener the listener to add
     * @exception java.util.TooManyListenersException we can't accept this listener.
     */
    public void addCALSyntaxStyleListener(CALSyntaxStyleListener listener) throws java.util.TooManyListenersException {
        // Just pass to the CALStyleContext
        CALKit.getStylePreferences().addCALSyntaxStyleListener(listener);
    }
    /**
     * Remove this style listener.
     * Creation date: (1/30/01 8:28:22 AM)
     * @param listener org.openquark.gems.client.caleditor.CALSyntaxStyleListener the listener to remove
     */
    public void removeCALSyntaxStyleListener(CALSyntaxStyleListener listener) {
        // Just pass to the CALStyleContext
        CALKit.getStylePreferences().removeCALSyntaxStyleListener(listener);
    }
}
