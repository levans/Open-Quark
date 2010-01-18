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
 * InputBinding.java
 * Created: 19-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.Collection;

import org.openquark.cal.services.BasicCALServices;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemGraph;
import org.openquark.samples.bam.BindingContext;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.w3c.dom.Element;



/**
 * This is the base class for gem input bindings.
 */
public abstract class InputBinding {
   
    /**
     * Method getOutputGem
     * 
     * @param calServices
     * @param gemGraph
     * @param bindingContext
     * @return Returns the Gem that represents the value of the binding
     */
    public abstract Gem getOutputGem (BasicCALServices calServices, GemGraph gemGraph, BindingContext bindingContext);

    /**
     * Method getPresentation
     * 
     * @return a String that can be used in UI to represent the binding
     */
    public abstract String getPresentation ();

    /**
     * Method store
     * 
     * @param bindingsElem
     */
    public abstract void store (Element bindingsElem);

    /**
     * Check to see if the binding is a constant.
     * @return true if this binding is constant
     */
    public abstract boolean isConstant();
    
    /**
     * Return a collection of the metrics required by this binding
     */
    public abstract Collection<MetricDescription> getRequiredMetrics();
    
    /**
     * selects a property from a collection by name.
     * This is used by the derived classes to find the message properties they are
     * associated with when they are loaded.
     * 
     * @param propertyName
     * @param messagePropertyInfos
     * @return Returns the MessagePropertyDescription with the given name
     */
    protected static MessagePropertyDescription lookupProperty (String propertyName, Collection<MessagePropertyDescription> messagePropertyInfos) {
        for (final MessagePropertyDescription messagePropertyDescription : messagePropertyInfos) {
            if (messagePropertyDescription.name.equals(propertyName)) {
                return messagePropertyDescription;
            }
        }
        
        return null;
    }

}
