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
 * PropertyBindingCombo.java
 * Created: 27-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.util.Collection;

import javax.swing.JComboBox;

import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;


/**
 * 
 *  
 */
class PropertyBindingCombo extends JComboBox {

    private static final long serialVersionUID = 1428040731828280739L;

    public static class PropertyItem {

        final MessagePropertyDescription propertyInfo;

        /**
         * Constructor PropertyItem
         * 
         * @param propertyInfo
         */
        PropertyItem (final MessagePropertyDescription propertyInfo) {
            this.propertyInfo = propertyInfo;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return propertyInfo.name;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals (Object obj) {
            if (obj instanceof PropertyItem) {
                return propertyInfo.equals (((PropertyItem)obj).propertyInfo);
            }

            return false;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode () {
            return propertyInfo.hashCode ();
        }
    }

    /**
     * Method initialise
     * 
     * @param propertyInfos
     */
    void initialise (Collection<MessagePropertyDescription> propertyInfos) {
        for (final MessagePropertyDescription propertyInfo : propertyInfos) {
            addInfo (propertyInfo);
        }
    }

    /**
     * Method initialise
     * 
     * @param propertyInfos
     * @param dataType
     */
    void initialise (Collection<MessagePropertyDescription> propertyInfos, int dataType) {
        for (final MessagePropertyDescription propertyInfo : propertyInfos) {
            if (propertyInfo.dataType == dataType) {
                addInfo (propertyInfo);
            }
        }
    }

    /**
     * Method addInfo
     * 
     * @param info
     */
    void addInfo (MessagePropertyDescription info) {
        addItem (new PropertyItem (info));
    }

    /**
     * Method setSelectedInfo
     * 
     * @param info
     */
    void setSelectedInfo (MessagePropertyDescription info) {
        setSelectedItem (new PropertyItem (info));
    }

    /**
     * Method getSelectedInfo
     * 
     * @return Returns the MessagePropertyInfo of the selected item, or null
     */
    MessagePropertyDescription getSelectedInfo () {
        Object selectedItem = getSelectedItem ();

        if (selectedItem instanceof PropertyItem) {
            return ((PropertyItem)selectedItem).propertyInfo;
        }

        return null;
    }
}