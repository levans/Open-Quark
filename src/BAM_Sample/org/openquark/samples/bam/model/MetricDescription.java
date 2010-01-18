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
 * MetricDescription.java
 * Created: 30-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.text.MessageFormat;
import java.util.Collection;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * A metric is described by a Message Property and the Metric Gem that 
 * is applied to it.
 */
public class MetricDescription {
    
    //this is the name of the gem that is used to compute the metric
    private final QualifiedName gemName;    
    
    //this is the property that the metric is for
    private final MessagePropertyDescription propertyDescription;
    
    /**
     * Construct a Metric Description
     * @param gemName the name of the gem used to compute the metric
     * @param propertyDescription the message property for which the metric should be computed
     */
    public MetricDescription (QualifiedName gemName,
                              MessagePropertyDescription propertyDescription) {
        this.gemName = gemName;
        this.propertyDescription = propertyDescription;
    }

    //define equality so that we metric description can be used in Sets
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
 
        MetricDescription other = (MetricDescription) obj;
        
        return (getInternalName().equals(other.getInternalName()));       
    }
    
    //define hashcode so metric description can be used effectively in Set
    @Override
    public int hashCode() {
        return getInternalName().hashCode();
    }
    
    
    /**
     * Get the name that is used internally for the instance of the metric in the gem graph
     */
    public String getInternalName() {
         return propertyDescription.name + "_" + gemName;
    }
    
    /**
     * the name of the gem that is used to compute the metric
     * @return the qualified gem name
     */
    public QualifiedName getGemName() {
        return gemName;
    }
    
    /**
     * @return Returns the propertyDescription associated with this metric
     */
    public MessagePropertyDescription getPropertyDescription () {
        return propertyDescription;
    }
    
    /**
     * Returns a description string in the form "metric (property)", which is suitable for display to the user
     * 
     * @return metric description
     */
    public String getDescription () {
        String metricName = gemName.getQualifiedName();
        String propertyName = propertyDescription.name;
        
        return MessageFormat.format("{0} ({1})", new Object [] { metricName, propertyName });
    }
    

 
    //
    // Serialisation
    //

    /**
     * Method store
     * 
     * @param parentElem
     */
    public void store (Element parentElem) {
        Document document = parentElem.getOwnerDocument();
        
        Element metricDescriptionElem = document.createElement(MonitorSaveConstants.MetricDescription);
        parentElem.appendChild(metricDescriptionElem);
        
        metricDescriptionElem.setAttribute(MonitorSaveConstants.PropertyNameAttr, getPropertyDescription().name);
        metricDescriptionElem.setAttribute(MonitorSaveConstants.MetricNameAttr, gemName.getQualifiedName());
    }

    /**
     * Method Load
     * 
     * @param metricDescriptionElem
     * @param messagePropertyInfos
     * 
     * @return Returns a MetricDescription loaded from the given element
     */
    public static MetricDescription Load (Element metricDescriptionElem, Collection<MessagePropertyDescription> messagePropertyInfos) {
        String propertyName = metricDescriptionElem.getAttribute(MonitorSaveConstants.PropertyNameAttr);
        String gemName   = metricDescriptionElem.getAttribute(MonitorSaveConstants.MetricNameAttr);
        
        MessagePropertyDescription propertyInfo = lookup(propertyName, messagePropertyInfos);

       
        return new MetricDescription (QualifiedName.makeFromCompoundName(gemName), propertyInfo);
    }

    /**
     * Method lookup a property by name - used to find the message property when
     * the metric description is loaded
     * 
     * @param propertyName
     * @param messagePropertyDescriptions
     * 
     * @return Returns the MessagePropertyInfo with the given name
     */
    private static MessagePropertyDescription lookup (String propertyName, Collection<MessagePropertyDescription> messagePropertyDescriptions) {
        for (final MessagePropertyDescription messagePropertyDescription : messagePropertyDescriptions) {
            if (messagePropertyDescription.name.equals(propertyName)) {
                return messagePropertyDescription;
            }
        }
        
        return null;
    }

}
