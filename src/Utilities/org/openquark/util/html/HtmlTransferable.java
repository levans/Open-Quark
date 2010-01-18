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
 * HtmlTransferable.java
 * Created: May 31, 2004
 * By: Iulian Radu
 */
package org.openquark.util.html;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import org.openquark.util.TextEncodingUtilities;



/**
 * Transferable for HTML text. 
 * This provides HTML, RTF, and plaintext flavors of the text.
 * 
 * @author Iulian Radu
 */
public class HtmlTransferable implements Transferable {
    
    /** HTML data flavor */
    public static final DataFlavor htmlFlavor = new DataFlavor("text/html; class=java.io.InputStream", "HTML Text");
    
    /** RTF data flavor */
    public static final DataFlavor rtfFlavor = new DataFlavor("text/rtf; class=java.io.InputStream", "RTF Text");
    
    /** Flavors supported: HTML, RTF, String*/
    private static final DataFlavor[] flavors = new DataFlavor[] {
            htmlFlavor,
            rtfFlavor,
            DataFlavor.stringFlavor
    };
    
    // String data in the various formats
    
    private final String htmlString;
    private final String rtfString;
    private final String plainString; 
    
    /**
     * Constructor 
     * 
     * @param htmlString html string to hold
     */
    public HtmlTransferable(String htmlString) {
        this.htmlString = htmlString;
        
        // Build an HTML document from the string 
        
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        Document doc = htmlKit.createDefaultDocument();
        try {
            htmlKit.read(new StringReader(htmlString), doc, 0);
        } catch (Exception e) {
            throw new IllegalStateException("HtmlTransferable: Error encountered in string conversion: " + e);
        }
        
        // Get the RTF version of the string
        
        RTFEditorKit rtfKit = new RTFEditorKit();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            rtfKit.write(byteStream, doc, 0, doc.getLength());
        }  catch (Exception e) {
            throw new IllegalStateException("HtmlTransferable: Error encountered in string conversion: " + e);
        }
        this.rtfString = new String(byteStream.toByteArray());
        
        // Get the plaintext version of the string
        
        JEditorPane conversionPanel = new JEditorPane();
        EditorKit plainKit = conversionPanel.getEditorKit();
        try {
            byteStream.reset();
            plainKit.write(byteStream, doc, 0, doc.getLength());
            byteStream.close();
        }  catch (Exception e) {
            throw new IllegalStateException("HtmlTransferable: Error encountered in string conversion: " + e);
        }
        this.plainString = new String(byteStream.toByteArray());
    }
    
    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors.clone();
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {

        for (final DataFlavor element : flavors) {
            if (flavor.equals(element)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        
        if (flavor.isMimeTypeEqual(flavors[0])) {
            return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(htmlString));
            
        } else if (flavor.isMimeTypeEqual(flavors[1])) {
            return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(rtfString));
            
        } else if (flavor.isMimeTypeEqual(flavors[2])) {
            return plainString;
            
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
