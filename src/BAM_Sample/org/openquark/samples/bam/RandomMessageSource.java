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
 * RandomMessageSource.java
 * Created: 7-May-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openquark.samples.bam.model.MessageSourceDescription;
import org.openquark.samples.bam.model.RandomMessageSourceDescription;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;


/**
 * This message source randomly creates messages - it is intended for demonstration purposes.
 * It creates messages of the form {SalesAmount = x, CustomerName = name}
 *  
 */
public class RandomMessageSource extends AbstractMessageSource {

    private static final String SALES_AMOUNT = "SalesAmount";

    private static final String CUSTOMER_NAME = "CustomerName";

    private static final String[] firstNames = { "Rick", "Rich", "Luke", "Bo", "Ray", "Edward", "Iulian", "Trevor",
            "David", "Kevin", "Gregory", "Hervé", "Bernard" };

    private static final String[] lastNames = { "Cameron", "Webster", "Evans", "Ilic", "Cypher", "Lam", "Radu", "Daw",
            "Mosimann", "Sit", "Dorman", "Couturier", "Liautaud" };

    private static final double meanAmount = 5000.0;

    private static final double stdDevAmount = 1000.0;

    private final String messageType;

    private boolean cancelled = false;

    /**
     * Method getMessagePropertyInfos
     * 
     * @return Returns a Collection of MessagePropertyInfos for this message source
     */
    public static Collection<MessagePropertyDescription> getMessagePropertyInfos () {
        List<MessagePropertyDescription> result = new ArrayList<MessagePropertyDescription> ();

        result.add (new MessagePropertyDescription (CUSTOMER_NAME, Message.STRING));
        result.add (new MessagePropertyDescription (SALES_AMOUNT, Message.DOUBLE));

        return result;
    }
    
    /**
     * Method createInstance
     * 
     * @param messageSourceDescription
     * 
     * @return Returns a RandomMessageSource created from the given description
     */
    static RandomMessageSource createInstance (MessageSourceDescription messageSourceDescription) {
        return new RandomMessageSource (messageSourceDescription.getName());
    }
    

    /**
     * Constructor RandomMessageSource
     * 
     * @param messageType
     */
    private RandomMessageSource (final String messageType) {
        this.messageType = messageType;
    }
    
    /**
     * @see org.openquark.samples.bam.MessageSource#start()
     */
    public boolean start () {
        run ();

        return true;
    }

    /**
     * @see org.openquark.samples.bam.MessageSource#stop()
     */
    public boolean stop () {
        cancelled = true;

        return true;
    }

    /**
     * Method run
     * 
     * 
     */
    private void run () {
        cancelled = false;

        Thread t = new Thread () {

            /**
             * @see java.lang.Thread#run()
             */
            @Override
            public void run () {
                try {
                    fireStatusChanged (STATUS_RUNNING);

                    generateMessages ();
                } finally {
                    fireStatusChanged (STATUS_IDLE);
                }
            }
        };

        t.start ();
    }

    /**
     * Method generateMessages
     * 
     *  
     */
    protected void generateMessages () {
        while (!cancelled) {
            generateOneMessage ();

            try {
                long millis = (long)(Math.random () * 1000);

                Thread.sleep (millis);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            }
        }
    }

    /**
     * Method generateOneMessage
     * 
     *  
     */
    private void generateOneMessage () {
        DefaultMessage message = new DefaultMessage (messageType);

        // make a name
        String name = getRandomString (firstNames) + " " + getRandomString (lastNames);

        message.addValue (CUSTOMER_NAME, name);

        // make an amount
        double amount = getRandomValue (meanAmount, stdDevAmount);

        message.addValue (SALES_AMOUNT, new Double (amount));

        fireMessageReceived (message);

    }

    /**
     * Method getRandomValue
     * 
     * @param meanAmount
     * @param stdDevAmount
     * 
     * @return Returns a random value from the Gaussian distribution with the
     *         given mean and standard deviation
     */
    private static double getRandomValue (double meanAmount, double stdDevAmount) {
        // Use Box-Muller transformation

        double x1 = Math.random ();
        double x2 = Math.random ();

        double y1 = Math.sqrt (-2 * Math.log (x1)) * Math.cos (2 * Math.PI * x2);

        // y1 has a mean of 0 and a standard deviation of 1

        return Math.max (meanAmount + stdDevAmount * y1, 0);
    }

    /**
     * Method getRandomString
     * 
     * @param strings
     * 
     * @return Returns a string randomly chosen from the given array
     */
    private static String getRandomString (String[] strings) {
        double r = Math.random ();

        int index = (int)(r * strings.length);

        return strings[index];
    }

    public static void main (String[] args) {
        final RandomMessageSource messageSource = RandomMessageSource.createInstance (new RandomMessageSourceDescription ("Random"));

        messageSource.addMessageListener (new MessageListener () {

            private int messagesReceived = 0;

            /**
             * @see org.openquark.samples.bam.MessageListener#messageReceived(org.openquark.samples.bam.Message)
             */
            public void messageReceived (Message message) {
                if (++messagesReceived > 20) {
                    messageSource.stop ();
                }

                System.out.println ("Message: " + message.getProperty (CUSTOMER_NAME) + " "
                        + message.getProperty (SALES_AMOUNT));
            }
        });

        messageSource.start ();
    }
}