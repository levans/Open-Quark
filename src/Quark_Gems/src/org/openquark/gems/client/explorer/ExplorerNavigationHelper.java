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
 * ExplorerNavigationHelper.java
 * Creation date: Dec 4th 2003
 * By: David Mosimann
 */
package org.openquark.gems.client.explorer;

import org.openquark.gems.client.Gem;

/**
 * This interface is used allow the explorer tree to work with a navigation helper.  The purpose of the
 * navigation helper is to indicate that certain gems can be focussed on and should be highlighted in
 * some way.  The navigation helper can also take action when the explorer tree attempts to focus on
 * a particular gem in the tree.
 * It is expected that the explorer tree will represent navigation as hyperlinks and that the navigation
 * helper will treat 'focussable' as the ability to click on a gem to be taken to associated gems.  For
 * example, focussing on an emitter will take the user to the collector gem.
 */
public interface ExplorerNavigationHelper {
    /**
     * Called by the explorer tree to ask the helper whether the gem can be focussed on.
     * @param gem The gem to check.  Should not be null.
     * @return Returns true if the gem can be focussed on which may cause UI changes including different
     * rendering and mouse over cursors.
     */
    public boolean isFocusable(Gem gem);
    
    /**
     * Called by the explorer tree to tell the helper that it should do whatever changes are necessary
     * to focus on the desired gem.
     * @param gem The gem that should be focussed on.  This gem must be non-null and if passed into
     * focussable() it must return true.
     */
    public void focusOn(Gem gem);
}
