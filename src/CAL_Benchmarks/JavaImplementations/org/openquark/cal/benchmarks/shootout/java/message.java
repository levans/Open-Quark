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
This code has been taken from the computer language shootout web site
(http://shootout.alioth.debian.org/) under the BSD license stated below. 
It is included with Open Quark only for comparison with 
the Open Quark benchmark implementations.

The modifications from the original are restricted to:
packaging it within the Open Quark package hierarchy,
minimal changes to make it compile with Java 1.4.
timing has been added so that the time without startup can be reported.
 
Revised BSD license

This is a specific instance of the Open Source Initiative (OSI) BSD license template
http://www.opensource.org/licenses/bsd-license.php


Copyright 2004,2005,2006 Brent Fulgham
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

   Neither the name of "The Computer Language Shootout Benchmarks" nor the name of "Computer Language Shootout" nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


package org.openquark.cal.benchmarks.shootout.java;
/* The Great Computer Language Shootout
http://shootout.alioth.debian.org/

contributed by James McIlree
 */


import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.benchmarks.shootout.StopWatch;

public class message {
    public static final int numberOfThreads = 500;
    public static int numberOfMessagesToSend;
    public static StopWatch stopWatch = new StopWatch();
    
    public static void main(String args[]) {
        numberOfMessagesToSend = Integer.parseInt(args[0]);

        stopWatch.start();
        
        MessageThread chain = null;
        for (int i=0; i<numberOfThreads; i++){
            chain = new MessageThread(chain);
            new Thread(chain).start();
        }

        for (int i=0; i<numberOfMessagesToSend; i++) chain.enqueue(new Integer(0));
    }
    
    static class MessageThread implements Runnable {
        MessageThread nextThread;
        List list = new ArrayList(4);

        MessageThread(MessageThread nextThread){
            this.nextThread = nextThread;
        }

        public void run() {
            if (nextThread != null)
                while (true) nextThread.enqueue(dequeue());
            else {
                int sum = 0;
                int finalSum = message.numberOfThreads * message.numberOfMessagesToSend;
                while (sum < finalSum)
                    sum += dequeue().intValue();

                System.out.println(sum);
                
                stopWatch.stop();
                System.err.println("benchmark took: " + stopWatch);
                
                
                System.exit(0);
            }
        }

        public void enqueue(Integer message)
        {
            synchronized(list) {
                list.add(new Integer(message.intValue() + 1));
                if (list.size() == 1) {
                    list.notify();
                }
            }
        }

        public Integer dequeue()
        {
            synchronized(list) {
                while(list.size() == 0) {
                    try { list.wait(); } catch (Exception e) {}
                }
                return (Integer)list.remove(0);
            }
        }
    }
}

