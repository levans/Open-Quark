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
 * CALEditorKit.java
 * Creation date: (1/18/01 6:31:37 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client.caleditor;

/**
 * The EditorKit (encapsulation of editor behaviours) which is used by the CALEditor
 * Creation date: (1/18/01 6:31:37 PM)
 * @author Luke Evans
 */
public class CALEditorKit extends javax.swing.text.DefaultEditorKit {
	private static final long serialVersionUID = 5388954800282605796L;
    private CALStyleContext styles;
    /**
     * CALEditorKit constructor comment.
     */
    public CALEditorKit() {
        super();
    }
    /**
     * Create a fresh document storage model for our CAL source.
     * Creation date: (1/18/01 6:36:03 PM)
     * @return javax.swing.text.Document the CAL source document
     */
    @Override
    public javax.swing.text.Document createDefaultDocument() {
        return new CALDocument();
    }
    /**
     * Get the MIME type of the data that this kit will handle.
     * We will handle a straight forward plain text format, of content "calsource"
     * Creation date: (1/18/01 6:33:19 PM)
     * @return java.lang.String
     */
    @Override
    public String getContentType() {
        return "text/calsource";
    }
    /**
     * Return the set of styles used when editing CAL source.
     * Creation date: (1/18/01 6:47:37 PM)
     * @return org.openquark.gems.client.CALStyleContext the styles
     */
    public CALStyleContext getStylePreferences() {
        // Return the singleton styles object, create it if needbe
        if (styles == null) {
            styles = new CALStyleContext();
        }
        return styles;
    }
    /**
     * Get a factory which can produce views of our CAL document (handled by this kit).
     * Creation date: (1/18/01 6:52:42 PM)
     * @return javax.swing.text.ViewFactory
     */
    @Override
    public javax.swing.text.ViewFactory getViewFactory() {
        // We use the CALStyleContext to act as a factory
        return getStylePreferences();
    }
    /**
     * Set the style preferences in use by this CALEditorKit.
     * Creation date: (1/18/01 6:50:53 PM)
     * @param styles org.openquark.gems.client.CALStyleContext the styles to use
     */
    public void setStylePreferences(CALStyleContext styles) {
        this.styles = styles;
    }
}
