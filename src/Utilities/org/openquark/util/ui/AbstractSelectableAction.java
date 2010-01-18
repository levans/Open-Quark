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
 * AbstractSelectableAction.java
 * Created: 16-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;


/**
 * 
 * 
 */
public abstract class AbstractSelectableAction extends AbstractAction {

    public static final String SELECTED_PROPERTY = "selected";  //$NON-NLS-1$

    private boolean selected = false;

    public AbstractSelectableAction (String name) {
        super (name);
    }

    /**
     * Method isSelected
     * 
     * @return boolean
     */
    public boolean isSelected() {
        return selected;
    }
        
    /**
     * Method setSelected
     * 
     * @param selected
     */
    public void setSelected (boolean selected) {
        if (selected != this.selected) {
            boolean wasSelected = this.selected;
            
            this.selected = selected;
            
            firePropertyChange(SELECTED_PROPERTY, Boolean.valueOf(wasSelected), Boolean.valueOf(selected));
        }
    }

    /**
     * Method addSelectedListener
     * 
     * @param button
     */
     public void addSelectedListener(final AbstractButton button) {
        addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == SELECTED_PROPERTY) {
                    Boolean selectedState = (Boolean) evt.getNewValue();
                    button.setSelected(selectedState.booleanValue());
                    button.repaint();
                }
            }
        });
    }

}
