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

package org.openquark.cal.benchmarks.shootout;

/**
 * The CAL Monitor can be used to pass values between CAL programs that are running
 * concurrently.
 *
 * @author Magnus Byne
 */
public class CalMonitor {

    private Object data = null;
    private boolean empty = true;
    
    /**
     * create a monitor with an initial value
     * @param value
     */
    public CalMonitor(Object value) {
        data = value;
        empty = false;
    }
    
    /**
     * create an empty monitor
     */
    public CalMonitor() {
    }

    /**
     * put a value, this may block if there is already a value.
     * @param value
     */
    public void put(Object value) {
        synchronized (this) {
            while (!empty) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.err.println("Unexpected interrupt");
                }
            }
            data = value;    
            empty= false;
            notifyAll();
        }
    }

    /**
     * get a value, this will block if the monitor is empty
     * @return the value
     */
    public Object get() {
        Object res;
        synchronized (this) {
            while (empty) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.err.println("Unexpected interrupt");
                }
            }
            res= data;
            empty= true;
            notifyAll();
        }
        return res;
    }
}    





