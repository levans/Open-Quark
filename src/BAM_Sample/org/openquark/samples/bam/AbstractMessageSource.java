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
 * AbstractMessageSource.java
 * Created: 5-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 *  
 */
abstract class AbstractMessageSource implements MessageSource {

    private List<MessageListener> messageListeners = new ArrayList<MessageListener> ();

    private List<StatusListener> statusListeners = new ArrayList<StatusListener> ();
    
    private int status=STATUS_IDLE;

    /**
     * @see org.openquark.samples.bam.MessageSource#addMessageListener(org.openquark.samples.bam.MessageListener)
     */
    public void addMessageListener (MessageListener listener) {
        if (messageListeners.contains (listener)) {
            throw new IllegalArgumentException ("Listener already registered");
        }

        messageListeners.add (listener);
    }

    /**
     * @see org.openquark.samples.bam.MessageSource#removeMessageListener(org.openquark.samples.bam.MessageListener)
     */
    public void removeMessageListener (MessageListener listener) {
        if (!messageListeners.contains (listener)) {
            throw new IllegalArgumentException ("Listener not registered");
        }

        messageListeners.remove (listener);
    }

    /**
     * @see org.openquark.samples.bam.MessageSource#addStatusListener(org.openquark.samples.bam.MessageSource.StatusListener)
     */
    public void addStatusListener (StatusListener listener) {
        if (statusListeners.contains (listener)) {
            throw new IllegalArgumentException ("Listener already registered");
        }

        statusListeners.add (listener);
    }

    /**
     * @see org.openquark.samples.bam.MessageSource#removeStatusListener(org.openquark.samples.bam.MessageSource.StatusListener)
     */
    public void removeStatusListener (StatusListener listener) {
        if (!statusListeners.contains (listener)) {
            throw new IllegalArgumentException ("Listener not registered");
        }

        statusListeners.remove (listener);
    }

    protected void fireMessageReceived (Message message) {
        Iterator<MessageListener> listenerIter = messageListeners.iterator ();

        while (listenerIter.hasNext ()) {
            MessageListener listener = listenerIter.next ();

            listener.messageReceived (message);
        }
    }

    /**
     * Check if the message source is running.
     * @return true if the message source is running
     */
    public boolean isRunning() {
        return status == STATUS_RUNNING;
    }
    
    protected void fireStatusChanged (int newStatus) {
        status = newStatus;
        Iterator<StatusListener> listenerIter = statusListeners.iterator ();

        while (listenerIter.hasNext ()) {
            StatusListener listener = listenerIter.next ();

            listener.statusChanged (newStatus);
        }
    }
}
