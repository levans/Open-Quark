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
 * CALSupport.java
 * Created: 9-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import javax.swing.JOptionPane;

import org.openquark.samples.bam.ui.PseudoEmailFrame;



/**
 * The methods in this class are used by the CAL actions.
 * This is how CAL provides feedback through the Java UI
 */
public class CALSupport {

    /*
     * This displays a message box
     */
    public static void messageBox (String message) {
        JOptionPane.showMessageDialog(null, message, "BAM Sample", JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * This simulates sending an email - it displays the contents of the email
     * that would be send in a special frame
     */
    public static boolean sendEmail (String toList, String subject, String message) {
        StringBuilder stringBuilder = new StringBuilder ();
        
        stringBuilder.append("You've got mail!\n\n");
        stringBuilder.append("To: " + toList + "\n\n");
        stringBuilder.append("Subject: " + subject + "\n\n");
        stringBuilder.append(message);
        
        System.out.println ("Email message: " + stringBuilder.toString());
        
        PseudoEmailFrame.addMessage(toList, subject, message);
        
        return true;
    }
    
    /*
     * this writes a message to the application's log
     */
    public static boolean sendToLog (String message) {
       MonitorApp.getInstance().log(message);
 
       return true;
    }
}
