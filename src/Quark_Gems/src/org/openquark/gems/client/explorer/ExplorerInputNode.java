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
 * ExplorerInputNode.java
 * Creation date: Dec. 31st 2002
 * By: Ken Wong
 */
 
package org.openquark.gems.client.explorer;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openquark.gems.client.Gem;


/**
 * These are the nodes used in the TableTopExplorer. Currently, they are only used for the inputs that are being stored
 * @author Ken Wong
 * Creation Date: Jan 2nd
 */
class ExplorerInputNode extends DefaultMutableTreeNode{

    private static final long serialVersionUID = -3084937467024174020L;

    /**
     * Constructor for ExplorerGemNode.
     * This constructor should not be called explicitly if the node created is to be used in the Explorer
     * Instead use createNewExplorerGemNode() in TableTopExplorer.
     * (see TableTopExplorer.createNewExplorerGemNode(Gem.PartInput) )
     * @param input
     */
    public ExplorerInputNode(Gem.PartInput input) {
       super(input);
       setAllowsChildren(true);
       
    }
    
    /**
     * Returns the PartInput user object
     * @return Gem.PartInput
     */
    Gem.PartInput getPartInput() {
        return (Gem.PartInput)userObject;
    }
}
