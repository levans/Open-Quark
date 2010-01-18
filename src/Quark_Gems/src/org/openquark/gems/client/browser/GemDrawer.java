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
 * GemDrawer.java
 * Creation date: (10/25/00 8:30:36 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.browser;

import org.openquark.cal.compiler.ModuleName;

/**
 * The basic Gem drawer tree node.
 * A Gem drawer represents a store of GemTreeNodes.
 * @author Luke Evans
 */
public abstract class GemDrawer extends BrowserTreeNode {

    /**
     * Construct a new GemDrawer with a given module name.
     * @param moduleName the drawer's module name
     */
    public GemDrawer(ModuleName moduleName) {
        super(moduleName);
    }
    
    /**
     * Return the name of the contained CAL module for this drawer.
     * Note that this may differ from the humanized name for the module which is supplied by toString()
     * @return the name of the embedded module
     */
    public abstract ModuleName getModuleName();
    
    /**
     * @return whether this node is a namespace node - i.e. it does not contain children corresponding to entities.
     */
    public abstract boolean isNamespaceNode();
}
