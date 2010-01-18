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
 * NavViewerPane.java
 * Creation date: Jul 28, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.RepaintManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * The metadata HTML viewer component. This class takes care of displaying metadata in
 * HTML form. It handles listening to hyperlinks and displaying the correct metadata
 * if a hyperlink is clicked. On top of that it provides a page history and printing support.
 * 
 * Use a document change listener to find out whenever the displayed page changes. A new event
 * is fired if a new page is loaded or if the user clicks on an anchored link and the page
 * location changes as a result of that.
 * 
 * @author Frank Worsley
 */
public class NavViewerPane extends JEditorPane implements Printable {
   
    private static final long serialVersionUID = -9104792276977818071L;

    /**
     * This listener listens for links being clicked on and performs the correct
     * action for the given address of the link.
     * @author Frank Worsley
     */
    private class NavHyperlinkListener implements HyperlinkListener {

        /**
         * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
         */
        public void hyperlinkUpdate(HyperlinkEvent e) {

            // We only react to clicked events, we don't want to do anything if
            // the mouse simply hovers over a link.
            if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
                return;
            }
            
            // If the hyperlink refers to a standard URL (e.g. http, ftp), then e.getURL() will
            // return a non-null value.
            if (e.getURL() != null) {
                // Since we cannot handle such a URL, for the time being we simply ignore it.
            } else {
                
                // We have to use the String form of the url since navigator urls are not
                // really valid urls which means that e.getURL() will return null.
                String url = e.getDescription();
                
                if (url.startsWith("#")) {
                    String anchor = url.substring(1);
                    displayMetadata(currentAddress.withAnchor(anchor));
                    
                } else {
                    displayMetadata(NavAddress.getAddress(url)); 
                }
            }
        }
    }
    
    /** The maximum size of the history lists. */
    private static final int MAX_HISTORY_SIZE = 50;    
    
    /** The history list of URLs we can go back to by pressing the back button. */
    private final List<NavAddress> backHistory = new ArrayList<NavAddress>();

    /** The history list of URLs we can go forward to by pressing the forward button. */
    private final List<NavAddress> forwardHistory = new ArrayList<NavAddress>();
    
    /** The address of the current page being viewed. */
    private NavAddress currentAddress = null;

    /** The owner that is using this editor pane. */
    private final NavFrameOwner owner;

    /**
     * Constructs a new editor pane with the given navigator owner.
     * @param owner the navigator owner using this editor pane
     */
    public NavViewerPane(NavFrameOwner owner) {
        this.owner = owner;
        
        NavEditorKit editorKit = new NavEditorKit();
        NavHtmlDocument doc = (NavHtmlDocument) editorKit.createDefaultDocument();
        
        setEditorKit(editorKit);
        setDocument(doc);
        setEditable(false);
        
        addHyperlinkListener(new NavHyperlinkListener());
    }
    
    /**
     * Display the metadata for the given url.
     * @param url the url to display metadata for
     */
    public void displayMetadata(NavAddress url) {
        
        // update the history if we are going to a new location
        if (!url.equals(currentAddress) && currentAddress != null) {
            updateHistory(backHistory, currentAddress);
        }
        
        displayMetadata(url, true);
    }
    
    /**
     * Display the metadata for the given CAL url.
     * @param url the url to display metadata for
     * @param clearForwardHistory if true the forward history will be cleared
     */
    private void displayMetadata(NavAddress url, boolean clearForwardHistory) {

        if (clearForwardHistory) {
            forwardHistory.clear();
        }

        // update the current url
        NavAddress oldUrl = currentAddress;
        currentAddress = url;
        
        if (oldUrl == null || !oldUrl.withAnchor(null).equals(currentAddress.withAnchor(null))) {
            
            // only reload the url if we really have to since it might be costly
            // for example, reloading search results if we just clicked on an anchor would be bad
            setText(NavHtmlFactory.getPage(owner, url));
            
        } else {
            
            // if we don't have to reload the page it must mean we the user clicked on
            // an anchor and we moved to a different page position
            NavHtmlDocument doc = (NavHtmlDocument) getDocument();
            doc.firePagePositionChanged();
        }
        
        // scroll to anchor if there is one, otherwise scroll to top
        if (url.getAnchor() != null) {
            scrollToReference(url.getAnchor());
        } else {
            setCaretPosition(0);
            scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        }
    }
    
    /**
     * Refreshes the displayed metadata to the latest information from the metadata.
     */
    public void refresh() {
        setText(NavHtmlFactory.getPage(owner, currentAddress));

        if (currentAddress.getAnchor() != null) {
            scrollToReference(currentAddress.getAnchor());
        } else {
            setCaretPosition(0);
            scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        }
    }
    
    /**
     * @return the url of the currently displayed page.
     */
    public NavAddress getCurrentAddress() {
        return currentAddress;
    }

    /**
     * Display the page that is next in the forward history. 
     */
    public void goForward() {

        if (forwardHistory.size() == 0) {
            return;
        }
        
        updateHistory(backHistory, currentAddress);
        NavAddress url = forwardHistory.remove(0);
        displayMetadata(url, false);
    }

    /**
     * Display the page that is next in the backward history.
     */    
    public void goBack() {

        if (backHistory.size() == 0) {
            return;
        }
        
        updateHistory(forwardHistory, currentAddress);
        NavAddress url = backHistory.remove(0);
        displayMetadata(url, false);
    }

    /**
     * Updates the given history list with the given url.
     * @param history the history list to update
     * @param url the url to add to the list
     */
    private void updateHistory(List<NavAddress> history, NavAddress url) {
        
        if (url != null) {
            history.add(0, url);
            if (history.size() > MAX_HISTORY_SIZE) {
                history.remove(MAX_HISTORY_SIZE);
            }
        }        
    }
    
    /**
     * @return the size of the forward history list
     */
    public int getForwardHistorySize() {
        return forwardHistory.size();
    }
    
    /**
     * @return the size of the backward history list
     */
    public int getBackHistorySize() {
        return backHistory.size();
    }
    
    /**
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    public int print(Graphics g, PageFormat pf, int pageIndex) {
         
        Graphics2D g2 = (Graphics2D) g; 
        
        //set default foreground color to black
        g2.setColor(Color.BLACK); 
                                              
        // turn of double-buffering since we are printing and not drawing on the screen
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
         
        // get document size
        Dimension d = this.getSize(); 
        double panelWidth  = d.width; 
        double panelHeight = d.height;
        
        // get size of the printer page 
        double pageHeight = pf.getImageableHeight(); 
        double pageWidth  = pf.getImageableWidth(); 
        
        // figure out the total number of pages
        double scale = pageWidth/panelWidth;
        int totalNumPages = (int)Math.ceil(scale * panelHeight / pageHeight);
                                             
        // stop printing once we reach the last page
        if(pageIndex >= totalNumPages) {
           return Printable.NO_SUCH_PAGE; 
        } 
                                         
        // shift Graphics to line up with beginning of the printable region 
        g2.translate(pf.getImageableX(), pf.getImageableY()); 
        
        // shift Graphics to line up with beginning of the page to print 
        g2.translate(0f, -pageIndex * pageHeight); 
        
        // scale the page so it fits the width of the printer page
        g2.scale(scale, scale); 
        
        // paint the page using the printer's graphics
        paint(g2);
        
        // turn double-buffering back on
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
                
        return Printable.PAGE_EXISTS; 
    }
}

/**
 * Subclasses the HTMLEditorKit class to return a NavHtmlDocument as the default document.
 * 
 * @author Frank Worsley
 */
class NavEditorKit extends HTMLEditorKit {

    private static final long serialVersionUID = -2979397588969833890L;

    /**
     * @see javax.swing.text.EditorKit#createDefaultDocument()
     */
    @Override
    public Document createDefaultDocument() {
        
        StyleSheet styles = getStyleSheet();
        NavHtmlDocument doc = new NavHtmlDocument(styles);

        doc.setParser(getParser());
        doc.setAsynchronousLoadPriority(4);
        doc.setTokenThreshold(100);

        return doc;        
    }

}

/**
 * Subclasses the HTMLDocument class to allow us to fire dummy document changed event,
 * if the position of the page changes after the user clicks on an anchor link.
 * This is needed because the NavFrame class uses a document change listener to update
 * it's UI state if the displayed page changes.
 * 
 * @author Frank Worsley
 */
class NavHtmlDocument extends HTMLDocument {
    
    private static final long serialVersionUID = -1661993424567523051L;

    /**
     * Construct a new document with the given styles.
     * @param styles the styles to use
     */
    public NavHtmlDocument(StyleSheet styles) {
        super(styles);
    }
    
    /**
     * Fire a dummy document change event to trigger document listeners that depends
     * on document change events to update their UI.
     */
    public void firePagePositionChanged() {
        fireChangedUpdate(new DefaultDocumentEvent(0, 0, DocumentEvent.EventType.CHANGE));
    }
}
