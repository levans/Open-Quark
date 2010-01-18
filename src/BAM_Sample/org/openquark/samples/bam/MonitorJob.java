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
 * MonitorJob.java
 * Created: 15-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;


/**
 * This class is used for running a single Job.
 * A Job consists of a single message source and
 * associated triggers and actions.
 */
class MonitorJob extends Thread  {
    
    private final Logger logger = Logger.getLogger("BAM");

    /**
     * This class is used to buffer messages between the CAL logic and the MessageSource
     * 
     * The GEM code uses the Iterator interface to get messages
     * The MessageSource uses the MessageListerner interface to insert messages as they arrive
     *
     * @author Magnus Byne
     */
    private class MessageBuffer implements  Iterator<List<Object>>, MessageListener, MessageSource.StatusListener {
        LinkedList<Message> queue = new LinkedList<Message>();
    
        /**
         * this is called by the messageSource to add a message to the buffer.
         * {@inheritDoc}
         */
        public synchronized void messageReceived(Message msg) {
            queue.addLast(msg);
            notify();
        }

        /**
         * This is called by the messageSource when its status changes - we notify the consumer
         * in case the source has finished
         * {@inheritDoc}
         */
        public synchronized void statusChanged(int newStatus) {
            notify();
        }
        
        /**
         *  Waits until a message is available in the buffer,
         *  or returns false if there are no more messages and the messageSource is not running
         * {@inheritDoc}
         */
        public synchronized boolean hasNext() {
            while (queue.isEmpty()) {
                try {
                    if (messageSource.isRunning()) {
                        wait();
                    } else {
                        return false;
                    }
                } catch (InterruptedException e) {
                    return false;
                }
            }
            return true;
        }

        /**
         * gets the next message from the buffer
         * throws a NoSuchElementException if no more messages are available
         * {@inheritDoc}
         */
        public synchronized List<Object> next () {
            
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            // create a list from the message properties 
            // this is the java representation of the CAL message tuple 
            // the order of the elements in the tuple must match the order expected by the GEM graph
            // both are defined by the order of the message property descriptions in the job description 
            Collection<MessagePropertyDescription> propertyInfos = jobDescription.getMessagePropertyDescriptions();
            List<Object> messageProperties = new ArrayList<Object>();
            Message message=queue.removeFirst();

            for (final MessagePropertyDescription propertyInfo : propertyInfos) {
                messageProperties.add(message.getProperty(propertyInfo.name));
            }
            
            return messageProperties;
        }
        
        /**
         * The iterator remove method is not supported.
         * {@inheritDoc}
         */
        public void remove() {
            throw new  UnsupportedOperationException();  
        }
    }
       
    private final MessageBuffer messageBuffer = new MessageBuffer();
    private final MonitorJobDescription jobDescription;
    private final MessageSource messageSource;    
    private final EntryPointSpec entryPointSpec;
    
    
    /**
     * Construct a monitor job
     * @param jobDescription this describes the job
     * @param messageSource this is the message source that the job will process messages from
     */
    public MonitorJob (MonitorJobDescription jobDescription, MessageSource messageSource, EntryPointSpec entryPointSpec) {
        this.jobDescription = jobDescription;
        this.entryPointSpec = entryPointSpec;
        this.messageSource = messageSource;
        
        //set the thread name to the job id
        this.setName(jobDescription.getJobId());
        
        //set up the message buffer so that it will receive messages and status notifications from the message source
        messageSource.addMessageListener(messageBuffer);
        messageSource.addStatusListener(messageBuffer);
    }
    
    /**
     * this starts the job processing messages from the message buffer
     * {@inheritDoc}
     */
    @Override
    public void run () {
        try {

            //this starts the CAL processing of the message buffer 
            Iterator<?> calResult =
                (Iterator<?>) MonitorApp.getInstance().getCalServices().runFunction(entryPointSpec, new Object[] { messageBuffer });
                
            //this retrieves the results from CAL - a Boolean value is returned for each
            //message which indicates whether or not actions were performed for the message
            while (calResult.hasNext()) {
                logger.info (getName() + " result: " + calResult.next());
            }

        } catch (GemCompilationException ex) {
            logger.warning(getName() + " CAL Compilation error: " + ex.getMessage());
        } catch (CALExecutorException ex) {
            logger.warning(getName() + " CAL Execution error: " + ex.getMessage());
        } 
    }
    
    /**
     * Get the job description associated with the job
     * @return Returns the jobDescription.
     */
    MonitorJobDescription getJobDescription () {
        return jobDescription;
    }
    
    /**
     * Get the message source associated with the job
     * @return the job's message source
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }
    
}
