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
 * NavFrameOwner.java
 * Creation date: Jul 14, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;

import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.ValueRunner;


/**
 * Adapter classes for the CAL Navigator need to implement this interface.
 * @author Frank Worsley
 */
public interface NavFrameOwner {

    /**
     * @return the JFrame the CAL Navigator should be parented to
     */
    public JFrame getParent();
    
    /**
     * @return the TypeChecker to use for checking example expressions
     */
    public TypeChecker getTypeChecker();
    
    /**
     * @return the ValueRunner to use for evaluating example expressions
     */
    public ValueRunner getValueRunner();
    
    /**
     * @return the Perspective from which to load CAL entities
     */
    public Perspective getPerspective();
    
    /**
     * @param name the name of the collector
     * @return the collector with the given name
     */
    public CollectorGem getCollector(String name);
    
    /**
     * Display the navigator.
     * @param newNavigator true if a new window should be opened if there is no existing window
     */
    public void displayNavigator(boolean newNavigator);
    
    /**
     * Display the metadata object for the given address.
     * @param address the address of the metadata object to display
     * @param newViewer true if a new window should be opened
     */
    public void displayMetadata(NavAddress address, boolean newViewer);

    /**
     * Edit the metadata object for the given address.
     * @param address the address of the metadata object to edit
     */
    public void editMetadata(NavAddress address);
    
    /**
     * Called when editing the metadata for the given address has finished.
     * @param address the address of the entity whose metadata was edited
     */
    public void editComplete(NavAddress address);
    
    /**
     * Called when the metadata for the given address has changed during editing and has been saved.
     * @param address the address of the entity whose metadata was edited
     */
    public void metadataChanged(NavAddress address);
    
    /**
     * Called to search the workspace for the given string.
     * @param searchString the string to search for
     * @return a List of NavAddress with matches for the search
     */
    public List<NavAddress> searchMetadata(String searchString);
    
    /**
     * Returns the Locale to be used for accessing metadata.
     * @return the Locale to be used.
     */
    public Locale getLocaleForMetadata();
}
