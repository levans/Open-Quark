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
 * EditBindingsDialog.java
 * Created: 8-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Frame;
import java.util.List;

import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemEntity;
import org.openquark.samples.bam.MonitorApp;
import org.openquark.samples.bam.model.ActionDescription;
import org.openquark.samples.bam.model.BoundGemDescription;
import org.openquark.samples.bam.model.InputBinding;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.TriggerDescription;
import org.openquark.util.ui.WizardCard;
import org.openquark.util.ui.WizardCardDialog;



/**
 * 
 * 
 */
class EditBindingsDialog extends WizardCardDialog {
    
    private static final long serialVersionUID = -7235633532337351930L;
    private final MonitorApp app;
    private final MonitorJobDescription jobDescription;
    private final BoundGemDescription boundGemDescription;
    private final int usage;
    
    private BindInputsCard bindInputsCard;
    

    /**
     * Constructor EditBindingsDialog
     * 
     * @param owner
     * @param app
     * @param jobDescription
     * @param boundGemDescription
     * @param usage
     */
    public EditBindingsDialog (Frame owner, MonitorApp app, MonitorJobDescription jobDescription, BoundGemDescription boundGemDescription, int usage) {
        super (owner, "Edit Bindings");
        
        this.app = app;
        this.jobDescription = jobDescription;
        this.boundGemDescription = boundGemDescription;
        this.usage = usage;
    }

    /**
     * @see org.openquark.util.ui.WizardCardDialog#makeCard()
     */
    @Override
    protected WizardCard makeCard () {
        BasicCALServices calServices = app.getCalServices();
        
        GemEntity gemEntity = calServices.getGemEntity(boundGemDescription.getQualifiedName());
        
        bindInputsCard = new BindInputsCard (app, jobDescription, gemEntity, boundGemDescription.getInputBindings (), usage);
        
        return bindInputsCard;
    }

    
    /**
     * @see org.openquark.util.ui.DialogBase#onOK()
     */
    @Override
    protected boolean onOK () {
        int index = getIndexForReplace();
        
        if (index == -1) {
            return false;
        }
        
        String gemName = boundGemDescription.getGemName();
        
        List<InputBinding> inputBindings = bindInputsCard.getInputBindings ();
        
        if (usage == BindInputsCard.FOR_TRIGGER) {
            jobDescription.replaceTriggerDescription(getIndexForReplace(), new TriggerDescription (gemName, inputBindings));
        } else if (usage == BindInputsCard.FOR_ACTION) {
            jobDescription.replaceActionDescription(getIndexForReplace(), new ActionDescription (gemName, inputBindings));
        } else {
            return false;
        }
        
        return true;
    }
    
    private int getIndexForReplace () {
        if (usage == BindInputsCard.FOR_TRIGGER) {
            List<TriggerDescription> triggerDescriptions = jobDescription.getTriggerDescriptions();
            
            return triggerDescriptions.indexOf(boundGemDescription);
        } else if (usage == BindInputsCard.FOR_ACTION) {
            List<ActionDescription> triggerDescriptions = jobDescription.getActionDescriptions();
            
            return triggerDescriptions.indexOf(boundGemDescription);
        }

        return -1;
    }
}
