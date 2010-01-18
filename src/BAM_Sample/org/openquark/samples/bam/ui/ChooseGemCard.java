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
 * ChooseGemCard.java
 * Created: 2-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openquark.cal.services.GemEntity;
import org.openquark.samples.bam.MonitorApp;
import org.openquark.util.ui.WizardCard;



/**
 * 
 * 
 */
abstract class ChooseGemCard extends WizardCard {
    
    private static class ListItem {
        private final GemEntity gemEntity;
        
        ListItem (GemEntity gemEntity) {
            this.gemEntity = gemEntity;
        }
        
        
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return gemEntity.getName().getQualifiedName();
        }
    }
    
    private final MonitorApp app;
    private final ConfigureGemWizardState wizardState;
    
    private JList gemList;

    protected ChooseGemCard (MonitorApp app, ConfigureGemWizardState wizardState) {
        this.app = app;
        this.wizardState = wizardState;
    }
    
    /**
     * Method getApp
     * 
     * @return the MonitorApp
     */
    protected MonitorApp getApp () {
        return app;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getMainPanel()
     */
    @Override
    protected JComponent getMainPanel () {
        JPanel result = new JPanel (new GridBagLayout ());
        
        GridBagConstraints constraints = new GridBagConstraints ();
        
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill   = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        
        result.add (new JScrollPane (getGemList()), constraints);
        
        return result;
    }

    /**
     * Method getGemList
     * 
     * @return a JList of GemEntities that can be used as triggers
     */
    private JList getGemList () {
        if (gemList == null) {
            Collection<GemEntity> availableGems = getAvailableGems();
            
            DefaultListModel listModel = new DefaultListModel ();
            
            for (final GemEntity gemEntity : availableGems) {
                listModel.addElement(new ListItem (gemEntity));
            }
            
            gemList = new JList (listModel);
            
            gemList.setSelectedIndex(0);
        }
        
        return gemList;
    }

    /**
     * Method getAvailableGems
     * 
     * @return the Collection of gems to be offered to the user
     */
    protected abstract Collection<GemEntity> getAvailableGems () ;

    /**
     * @see org.openquark.util.ui.WizardCard#initControls()
     */
    @Override
    protected boolean initControls () {
        return true;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#commitChanges()
     */
    @Override
    protected boolean commitChanges () {
        GemEntity gemEntity = getSelectedGem();
        
        if (gemEntity != null) {
            wizardState.setGemEntity(gemEntity);
            
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method getSelectedGem
     * 
     * @return the GemEntity of the currently selected item in the gem list
     */
    GemEntity getSelectedGem () {
        if (gemList != null) {
            Object selectedValue = gemList.getSelectedValue();
            
            if (selectedValue instanceof ListItem) {
                return ((ListItem)selectedValue).gemEntity;
            }
        }
        
        return null;
    }
}
