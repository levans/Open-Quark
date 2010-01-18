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
 * MessageSource.java
 * Created: 5-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;


/**
 * The message source interface provides control over a message source, for
 * starting and stopping the message source, and add listeners for new messages
 * and changes to message source status.
 */
interface MessageSource {
    
    final int STATUS_IDLE = 0;
    final int STATUS_RUNNING = 1;
    
    interface StatusListener {
    
        void statusChanged (int newStatus);
        
    }
    
    /**
     * Add a listener which is notified when a message arrives
     * @param listener
     */
    void addMessageListener (MessageListener listener);
    
    /**
     * Remove message listener
     * @param listener
     */
    void removeMessageListener (MessageListener listener);
    
    /**
     * Adds a status listener, which is notified whenever the message source status changes
     * @param listener
     */
    void addStatusListener (StatusListener listener);
    
    /**
     * remove a status listener
     * @param listener
     */
    void removeStatusListener (StatusListener listener);
    
    /**
     * true iff the message source is running
     */
    boolean isRunning();
    
    boolean start ();
    
    boolean stop ();

}
