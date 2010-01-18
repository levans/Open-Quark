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
 * MonitorJobDescription.java
 * Created: 15-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.samples.bam.MessageSourceFactory;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This class is used to describe a job. The description is composed of a message source and
 * the set of triggers and and actions that define the message processing. 
 */
public class MonitorJobDescription {

 
    /**
     *  This interface must be implemented by clients that wish to receive 
     *  notifications when the job description is modified. 
     */
    public static interface MonitorJobDescriptionListener {

        void triggerAdded (TriggerDescription triggerDescription);

        void triggerReplaced (TriggerDescription oldTriggerDescription, TriggerDescription triggerDescription);
        
        void triggerRemoved (TriggerDescription triggerDescription);

        void actionAdded (ActionDescription actionDescription);


        void actionReplaced (ActionDescription oldActionDescription, ActionDescription actionDescription);

        void actionRemoved (ActionDescription actionDescription);

    }

    private MessageSourceDescription messageSourceDescription;

    private List<MessagePropertyDescription> messagePropertyDescriptions; //used to cache the message property descriptions

    private List<TriggerDescription> triggerDescriptions = new ArrayList<TriggerDescription> (); // of TriggerDescriptions

    private List<ActionDescription> actionDescriptions = new ArrayList<ActionDescription> (); // of ActionDescriptions
    
    private List<MonitorJobDescriptionListener> listeners = new ArrayList<MonitorJobDescriptionListener> (); //clients that receive modify notifications
    
    public MonitorJobDescription (MessageSourceDescription messageSourceDescription) {
        jobId = "job" + ++jobNum;
        this.messageSourceDescription = messageSourceDescription;
        this.messagePropertyDescriptions = new ArrayList<MessagePropertyDescription> (messageSourceDescription.getMessagePropertyDescriptions());
    }

    private static int jobNum=0;
    
    /**
     * A unique ID associated with this job
     */
    private final String jobId;
    
    /**
     * Constructor MonitorJobDescription
     */    
    private MonitorJobDescription () {
        jobId = "Job" + ++jobNum;
    }

    /**
     * get the job id for this job
     * @return the unique job id
     */
    public String getJobId() {
        return jobId;
    }
    
    public MessageSourceDescription getMessageSourceDescription () {
        return messageSourceDescription;
    }

    public Collection<MessagePropertyDescription> getMessagePropertyDescriptions () {
        return Collections.unmodifiableCollection (messagePropertyDescriptions);
    }

    public List<TriggerDescription> getTriggerDescriptions () {
        return Collections.unmodifiableList (triggerDescriptions);
    }

    public void addTrigger (TriggerDescription triggerDescription) {
        triggerDescriptions.add (triggerDescription);

        fireTriggerAdded (triggerDescription);
    }
    
    public void replaceTriggerDescription (int n, TriggerDescription triggerDescription) {
        if (n >= triggerDescriptions.size()) {
            throw new IllegalArgumentException ("Trigger index out of range: " + n);
        }
        
        TriggerDescription oldTriggerDescription = triggerDescriptions.get(n);
        
        triggerDescriptions.set(n, triggerDescription);
        
        fireTriggerReplaced (oldTriggerDescription, triggerDescription);
    }

    public void removeTrigger (TriggerDescription triggerDescription) {
        triggerDescriptions.remove(triggerDescription);

        fireTriggerRemoved (triggerDescription);
    }

    public List<ActionDescription> getActionDescriptions () {
        return Collections.unmodifiableList (actionDescriptions);
    }

    public void addAction (ActionDescription actionDescription) {
        actionDescriptions.add (actionDescription);

        fireActionAdded (actionDescription);
    }

    public void replaceActionDescription (int n, ActionDescription actionDescription) {
        if (n >= actionDescriptions.size()) {
            throw new IllegalArgumentException ("Action index out of range: " + n);
        }
        
        ActionDescription oldActionDescription = actionDescriptions.get(n);
        
        actionDescriptions.set(n, actionDescription);
        
        fireActionReplaced (oldActionDescription, actionDescription);
    }


    public void removeAction (ActionDescription actionDescription) {
        actionDescriptions.remove(actionDescription);

        fireActionRemoved (actionDescription);
    }
    
    /**
     * Computes the minimal set of metrics required by the job's triggers and actions
     * @return set of required metrics
     */
    public Collection<MetricDescription> getMetricDescriptions () {
        Set<MetricDescription> metrics=new HashSet<MetricDescription>();
        
        Collection<TriggerDescription> triggers=getTriggerDescriptions();
        for (final TriggerDescription td : triggers) {
            for(int i=0; i <  td.getBindingCount(); i++) {
                metrics.addAll(td.getNthBinding(i).getRequiredMetrics());
            }
        }
        
        Collection<ActionDescription> actions=getActionDescriptions();
        for (final ActionDescription td : actions) {
            for(int i=0; i <  td.getBindingCount(); i++) {
                metrics.addAll(td.getNthBinding(i).getRequiredMetrics());
            }
        }
        
        return metrics;
    }
    

    /**
     * Method makeTestInstance
     *  
     */
    public static MonitorJobDescription makeTestInstance () {
        MessageSourceDescription messageSourceDescription = new TextFileMessageSourceDescription ("Customer Sales", "Resources/testfile.txt");

        MonitorJobDescription result = new MonitorJobDescription (messageSourceDescription);

        MessagePropertyDescription namePropertyInfo = result.messagePropertyDescriptions.get(0);
        MessagePropertyDescription amountPropertyInfo = result.messagePropertyDescriptions.get(1);

        MetricDescription metricDescription = new MetricDescription (
            QualifiedName.make("Cal.Samples.BusinessActivityMonitor.BAM", "average") , amountPropertyInfo);
        
        ArrayList<InputBinding> triggerInputBindings = new ArrayList<InputBinding> ();

        triggerInputBindings.add (new MetricBinding (metricDescription));
        triggerInputBindings.add (new ConstantBinding (new Double (4500.00)));
        triggerInputBindings.add (new PropertyBinding (amountPropertyInfo));

        result.triggerDescriptions.add (new TriggerDescription ("Cal.Samples.BusinessActivityMonitor.BAM.outsideOfRange", triggerInputBindings));

        ArrayList<InputBinding> actionInputBindings = new ArrayList<InputBinding> ();

        actionInputBindings.add (new ConstantBinding ("joe@bloggs.com"));
        actionInputBindings.add (new ConstantBinding ("Sales out of range!"));
        actionInputBindings.add (makeEmailBodyBinding (namePropertyInfo, amountPropertyInfo));

        result.actionDescriptions.add (new ActionDescription ("Cal.Samples.BusinessActivityMonitor.BAM.sendSimulatedEmail", actionInputBindings));

        return result;
    }

    /**
     * Method makeEmailBodyBinding
     * 
     * @return Returns an InputBinding for the body of an email message
     */
    private static InputBinding makeEmailBodyBinding (MessagePropertyDescription namePropertyInfo, MessagePropertyDescription amountPropertyInfo) {
        List<InputBinding> inputsList = new ArrayList<InputBinding> ();
        
        inputsList.add (new PropertyBinding (namePropertyInfo));
        inputsList.add (new PropertyBinding (amountPropertyInfo));
        
        return new TemplateStringBinding ("The sales amount for customer {0} (${1}) is out of range", inputsList);
    }

    /**
     * Method makeAnotherTestInstance
     *  
     */
    public static MonitorJobDescription makeAnotherTestInstance () {
        MessageSourceDescription messageSourceDescription = new TextFileMessageSourceDescription ("More Customer Sales", "Resources/testfile.txt");

        MonitorJobDescription result = new MonitorJobDescription (messageSourceDescription);

        // outsideOfSigmas arg stdDev threshold mean

        ArrayList<InputBinding> triggerInputBindings = new ArrayList<InputBinding> ();

        triggerInputBindings.add (new PropertyBinding (result.messagePropertyDescriptions.get(1)));
        triggerInputBindings.add (new ConstantBinding (new Double (750.00)));
        triggerInputBindings.add (new ConstantBinding (new Double (2)));
        triggerInputBindings.add (new ConstantBinding (new Double (3000.00)));

        result.triggerDescriptions.add (new TriggerDescription ("Cal.Samples.BusinessActivityMonitor.BAM.outsideOfSigmas", triggerInputBindings));

        ArrayList<InputBinding> actionInputBindings = new ArrayList<InputBinding> ();

        actionInputBindings.add (new ConstantBinding ("joe@bloggs.com"));
        actionInputBindings.add (new ConstantBinding ("Sales outside of sigmas!"));
        actionInputBindings.add (new PropertyBinding (result.messagePropertyDescriptions.get(0)));

        result.actionDescriptions.add (new ActionDescription ("Cal.Samples.BusinessActivityMonitor.BAM.sendSimulatedEmail", actionInputBindings));

        return result;
    }

    //
    // Listener management
    //

    public void addJobDescriptionListener (MonitorJobDescriptionListener listener) {
        listeners.add (listener);
    }

    public void removeJobDescriptionListener (MonitorJobDescriptionListener listener) {
        listeners.remove (listener);
    }

    private void fireTriggerAdded (TriggerDescription triggerDescription) {
        List<MonitorJobDescriptionListener> tempList = new ArrayList<MonitorJobDescriptionListener> (listeners);

        for (final MonitorJobDescriptionListener listener : tempList) {
            listener.triggerAdded (triggerDescription);
        }
    }

    /**
     * Method fireTriggerReplaced
     * 
     * @param oldTriggerDescription
     * @param triggerDescription
     */
    private void fireTriggerReplaced (TriggerDescription oldTriggerDescription, TriggerDescription triggerDescription) {
        List<MonitorJobDescriptionListener> tempList = new ArrayList<MonitorJobDescriptionListener> (listeners);

        for (final MonitorJobDescriptionListener listener : tempList) {
            listener.triggerReplaced (oldTriggerDescription, triggerDescription);
        }
    }


    private void fireTriggerRemoved (TriggerDescription triggerDescription) {
        List<MonitorJobDescriptionListener> tempList = new ArrayList<MonitorJobDescriptionListener> (listeners);

        for (final MonitorJobDescriptionListener listener : tempList) {
            listener.triggerRemoved (triggerDescription);
        }
    }

    private void fireActionAdded (ActionDescription actionDescription) {
        List<MonitorJobDescriptionListener> tempList = new ArrayList<MonitorJobDescriptionListener> (listeners);

        for (final MonitorJobDescriptionListener listener : tempList) {
            listener.actionAdded (actionDescription);
        }
    }

    /**
     * Method fireActionReplaced
     * 
     * @param oldActionDescription
     * @param actionDescription
     */
    private void fireActionReplaced (ActionDescription oldActionDescription, ActionDescription actionDescription) {
        List<MonitorJobDescriptionListener> tempList = new ArrayList<MonitorJobDescriptionListener> (listeners);

        for (final MonitorJobDescriptionListener listener : tempList) {
            listener.actionReplaced (oldActionDescription, actionDescription);
        }
    }
    
    private void fireActionRemoved (ActionDescription actionDescription) {
        List<MonitorJobDescriptionListener> tempList = new ArrayList<MonitorJobDescriptionListener> (listeners);

        for (final MonitorJobDescriptionListener listener : tempList) {
            listener.actionRemoved (actionDescription);
        }
    }
    
    //
    // Serialisation
    //

    /**
     * Method store
     * 
     * @param parentElement
     */
    void store (Element parentElement) {
        Document document = parentElement.getOwnerDocument ();

        // Construct a new element for the job description.
        Element jobDescriptionElem = document.createElement (MonitorSaveConstants.JobDescription);
        parentElement.appendChild (jobDescriptionElem);

        storeMessageSourceDescription (jobDescriptionElem);
        storeTriggerDescriptions (jobDescriptionElem);
        storeActionDescriptions (jobDescriptionElem);
    }

    /**
     * Method storeMessageSourceDescription
     * 
     * @param jobDescriptionElem
     */
    private void storeMessageSourceDescription (Element jobDescriptionElem) {
        Document document = jobDescriptionElem.getOwnerDocument ();

        // Construct a new element for the job description.
        Element messageSourceElem = document.createElement (MonitorSaveConstants.MessageSource);
        jobDescriptionElem.appendChild (messageSourceElem);
        
        messageSourceDescription.store (messageSourceElem);
    }

    /**
     * Method storeTriggerDescriptions
     * 
     * @param jobDescriptionElem
     */
    private void storeTriggerDescriptions (Element jobDescriptionElem) {
        Document document = jobDescriptionElem.getOwnerDocument ();

        // Construct a new element for the trigger descriptions.
        Element triggersElem = document.createElement (MonitorSaveConstants.TriggerDescriptions);
        jobDescriptionElem.appendChild (triggersElem);
        
        for (final TriggerDescription triggerDescription : triggerDescriptions) {
            triggerDescription.store (triggersElem);
        }
    }

    /**
     * Method storeActionDescriptions
     * 
     * @param jobDescriptionElem
     */
    private void storeActionDescriptions (Element jobDescriptionElem) {
        Document document = jobDescriptionElem.getOwnerDocument ();

        // Construct a new element for the action descriptions.
        Element actionsElem = document.createElement (MonitorSaveConstants.ActionDescriptions);
        jobDescriptionElem.appendChild (actionsElem);
        
        for (final ActionDescription actionDescription : actionDescriptions) {
            actionDescription.store (actionsElem);
        }
    }

    /**
     * Method Load
     * 
     * @param element
     * @return Returns a MonitorJobDescription loaded from the given XML element
     */
    public static MonitorJobDescription Load (Element element) throws InvalidFileFormat, BadXMLDocumentException {
        if (element.getTagName().equals (MonitorSaveConstants.JobDescription)) {
            MonitorJobDescription jobDescription = new MonitorJobDescription ();
            
            jobDescription.load (element);
            
            return jobDescription;
        }
        
        return null;
    }

    /**
     * Method load
     * 
     * @param jobDescriptionElem
     */
    private void load (Element jobDescriptionElem) throws InvalidFileFormat, BadXMLDocumentException {
        loadMessageSource (jobDescriptionElem);        
        loadTriggerDescriptions (jobDescriptionElem, messagePropertyDescriptions);
        loadActionDescriptions (jobDescriptionElem);
    }

    /**
     * Method loadMessageSource
     * 
     * @param jobDescriptionElem
     * @throws InvalidFileFormat
     */
    private void loadMessageSource (Element jobDescriptionElem) throws InvalidFileFormat {
        Element messageSourceElem = XMLPersistenceHelper.getChildElement(jobDescriptionElem, MonitorSaveConstants.MessageSource);
        
        if (messageSourceElem == null) {
            throw new InvalidFileFormat ("MessageSource element missing");
        }
        
        messageSourceDescription = MessageSourceFactory.Load (messageSourceElem);
        messagePropertyDescriptions = new ArrayList<MessagePropertyDescription> (messageSourceDescription.getMessagePropertyDescriptions());
    }

    /**
     * Method loadTriggerDescriptions
     * 
     * @param jobDescriptionElem
     */
    private void loadTriggerDescriptions (Element jobDescriptionElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws InvalidFileFormat, BadXMLDocumentException {
        Element triggersElem = XMLPersistenceHelper.getChildElement(jobDescriptionElem, MonitorSaveConstants.TriggerDescriptions);
        
        if (triggersElem == null) {
            throw new InvalidFileFormat ("TriggerDescriptions element missing");
        }
        
        List<Element> triggerElems = XMLPersistenceHelper.getChildElements(triggersElem);
        
        for (final Element triggerElem : triggerElems) {
            triggerDescriptions.add (TriggerDescription.Load (triggerElem, messagePropertyInfos));
        }
    }

    /**
     * Method loadActionDescriptions
     * 
     * @param jobDescriptionElem
     */
    private void loadActionDescriptions (Element jobDescriptionElem) throws BadXMLDocumentException, InvalidFileFormat {
        Element actionsElem = XMLPersistenceHelper.getChildElement(jobDescriptionElem, MonitorSaveConstants.ActionDescriptions);
        
        if (actionsElem == null) {
            throw new InvalidFileFormat ("ActionDescriptions element missing");
        }
        
        List<Element> actionElems = XMLPersistenceHelper.getChildElements(actionsElem);
        
        for (final Element actionElem : actionElems) {
            actionDescriptions.add (ActionDescription.Load (actionElem, messagePropertyDescriptions));
        }
    }

}
