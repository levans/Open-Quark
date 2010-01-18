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
 * MonitorGemFilter.java
 * Created: 20-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.util.Set;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ScopedEntityMetadata;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.GemFilter;



/**
 * This is the base class for the Gem filters (trigger,action, metric) in BAM.
 */
public abstract class MonitorGemFilter extends GemFilter {

    /**
     * Method hasAttribute
     * 
     * Helper method for subclasses.
     * 
     * @param metadata
     * @param attributeName
     * @return Returns true iff the metadata has a non-empty value for the given attribute
     */
    protected static boolean hasAttribute (ScopedEntityMetadata metadata, String attributeName) {
        String value = metadata.getAttribute(attributeName);
        
        return value != null && value.length() != 0;
    }
    
    /**
     * @see org.openquark.cal.services.GemFilter#select(GemEntity)
     */
    @Override
    public boolean select (GemEntity gemEntity) {
        return checkSignature (gemEntity)
            && checkMetadata (gemEntity);
    }

    /**
     * Method checkSignature
     * 
     * @param gemEntity
     * @return Returns true iff the signature of the gem is suitable. In practice, this means that the gem returns Boolean.
     */
    protected boolean checkSignature (GemEntity gemEntity) {
        TypeExpr gemTypeExpr = gemEntity.getTypeExpr();
        
        return gemTypeExpr.getResultType().isNonParametricType(CAL_Prelude.TypeConstructors.Boolean);
    }

    /**
     * Method checkMetadata
     * 
     * @param gemEntity
     * @return Returns true iff the metadata of the gem is suitable.
     */
    private boolean checkMetadata (GemEntity gemEntity) {
        ScopedEntityMetadata metadata = gemEntity.getMetadata(GemManager.getLocaleForMetadata());
        
        return metadata != null && checkMetadata(metadata);
    }

    /**
     * Method checkMetadata
     * 
     * Override this method in subclasses to check the metadata. 
     * The subclass can assume that the metadata is not null.
     * 
     * @param metadata
     * @return Returns true iff the metadata is suitable.
     */
    protected abstract boolean checkMetadata (ScopedEntityMetadata metadata);

    /**
     * Gets the set of gems that match the filter
     * @return the set of matching Gems
     */
    public Set<GemEntity> getMatchingGems() {
        return MonitorApp.getInstance().getCalServices().getMatchingGems(this);
    }
}
