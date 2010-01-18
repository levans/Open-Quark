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
 * IntellicutPanelOwner.java
 * Creation date: Mar. 4, 2003
 * By: David Mosimann
 */
package org.openquark.gems.client;


import javax.swing.JPopupMenu;

import org.openquark.util.Messages;


/**
 * This interface defines the methods needed to adapt the Intellicut panel to a particular environment.
 */
public interface IntellicutPanelOwner {
    /**
     * Takes the currently selected gem in the list, drops it onto the TableTop and connects
     * it to the intellicut part.
     * Note: Assumes that there is always an item selected in the list.
     * Creation date: (19/04/01 9:55:49 AM)
     */
    void connectSelectedGem();

    /**
     * Returns the popup of the intellicut list, only if the popup doesn't already exist
     * @return JPopupMenu
     */
    JPopupMenu getIntellicutPopupMenu();

    /**
     * Stops the intellicut panel.  This will be called when the panel loses focus or is closed in some
     * fashion.  When this method is finished, the UI component of the panel should be closed.
     */
    void stopIntellicutPanel();  
    
    /**
     * @return the message bundle that contains the IntellicutPanel resource strings
     */
    Messages getMessages();

}
