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
 * StatusMessageDisplayer.java
 * Creation date: (27/08/2001 2:08:02 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

/**
 * An interface for components which can display a status message to the user.
 * Really only the GemCutter.
 * Creation date: (27/08/2001 2:08:02 PM)
 * @author Edward Lam
 */
public interface StatusMessageDisplayer {
    /**
     * Message Type enum pattern.
     * @author Edward Lam
     */
    public static final class MessageType {
        private final String typeString;

        private MessageType(String s) {
            typeString = s;
        }
        @Override
        public String toString() {
            return typeString;
        }

        /** A message which persists until specifically cleared by its owner. */
        public static final MessageType PERSISTENT = new MessageType("PERSISTENT");
        
        /** A message which persists until overwritten by any other message. */
        public static final MessageType DEFERENTIAL = new MessageType("DEFERENTIAL");
        
        /** A message which persists until a limited amount of time has elapsed, or until overwritten by any other message. */
        public static final MessageType TRANSIENT = new MessageType("TRANSIENT");
    }
    
    /**
     * Clear the status message.
     * Creation date: (12/11/01 3:59:55 PM)
     * @param requestor Object the object requesting the message to clear.
     */
    public void clearMessage(Object requestor);
    /**
     * Display the given message.
     * @param requestor the object requesting the message to display.
     * @param message the message to display.
     * @param messageType the message type
     */
    public void setMessage(Object requestor, String message, MessageType messageType);
    /**
     * Given the name of a resource, display the associated string.
     * Creation date: (12/11/01 3:34:55 PM)
     * @param requestor Object the object requesting the message to display.
     * @param resourceName String the name of the resource
     * @param messageType the message type
     */
    public void setMessageFromResource(Object requestor, String resourceName, MessageType messageType);
    /**
     * Given the name of a resource, display the associated string, plus some additional text.
     * Creation date: (12/11/01 3:34:55 PM)
     * @param requestor Object the object requesting the message to clear.
     * @param resourceName String the name of the resource
     * @param resourceArgument text to present as an argument to the resource message, or null for none.
     * @param messageType the message type
     */
    public void setMessageFromResource(Object requestor, String resourceName, String resourceArgument, MessageType messageType);
}
