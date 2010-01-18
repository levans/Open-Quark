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
 * GemTreeNode.java
 * Creation date: (08/02/01 9:58:49 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.browser;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.GemEntity;


/**
 * A GemTreeNode represents a Gem in the BrowserTree.
 * This tree node will always be a leaf, and its user object is of type GemEntity.
 * @author Michael Cheng
 */
public class GemTreeNode extends BrowserTreeNode {
   
    private static final long serialVersionUID = 3468793658707956884L;
    /**
     * The module type info for the current working module. The browser tree is
     * rebuilt if the working module is changed, so it is ok to store this here.
     */
    private final ModuleTypeInfo currentModuleTypeInfo;

    /**
     * Constructor for a GemTreeNode
     * @param gemEntity the entity represented by this node
     * @param currentModuleTypeInfo ModuleTypeInfo for module to use for calculating gem's name
     */
    public GemTreeNode(GemEntity gemEntity, ModuleTypeInfo currentModuleTypeInfo) {
        super(gemEntity, false);
        this.currentModuleTypeInfo = currentModuleTypeInfo;
    }

    /**
     * @see org.openquark.gems.client.browser.BrowserTreeNode#getDisplayedString()
     */
    @Override
    public String getDisplayedString() {
        
        GemEntity gemEntity = (GemEntity)getUserObject();
        
        if (getParent() instanceof GemDrawer) {
            // If this node is inside its module's folder, just show the unqualified name.
            return gemEntity.getAdaptedName(ScopedEntityNamingPolicy.UNQUALIFIED);
        
        } else {
            ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(currentModuleTypeInfo);
            return gemEntity.getAdaptedName(namingPolicy);
        }
    }
    
    /**
     * Returns true if this node's gemEntity has a saved design associated with it, 
     * or false otherwise. This method should be used in preference to manually getting 
     * the GemEntity and calling hasDesign() on it, because this method uses a cached value 
     * whereas GemEntity.hasDesign() computes the value by looking in the gemDesign store, 
     * which can be expensive.
     */
    public boolean hasDesign() {
        GemEntity gemEntity = (GemEntity)getUserObject();
        return gemEntity.hasDesign();
    }
}
