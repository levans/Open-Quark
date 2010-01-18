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

import org.openquark.cal.compiler.Compiler;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.services.BasicCALServices;

/**
 * This implements the java portion of the Messages (cheap-concurrency) benchmark. It is responsible for starting
 * and coordinating the threads. The body of the threads is implemented in CAL, see
 * Cal.Benchmarks.Shootout.MessagesThreadBody
 * 
 *
 * @author Magnus Byne
 */
public class Messages {

    public static final int numberOfThreads = 500;
    public static int numberOfMessagesToSend;
    private static final String WORKSPACE_FILE_NAME = "cal.benchmark.cws";  
    private static BasicCALServices calServices;
    private static EntryPoint threadBody;

    public static void main(String args[]) {
        
        if (args.length == 1) {
            numberOfMessagesToSend = Integer.parseInt(args[0]);
        } else {
            numberOfMessagesToSend = 15000;
        }
     
        StopWatch stopWatch = new StopWatch();
        
        stopWatch.start();
        
        calServices = BasicCALServices.makeCompiled(WORKSPACE_FILE_NAME, new MessageLogger());
        
        CompilerMessageLogger messageLogger = new MessageLogger();        
        Compiler compiler = calServices.getCompiler();   
        
        ModuleName moduleName= ModuleName.make("Cal.Benchmarks.Shootout.MessageThreadBody");
        
        threadBody = compiler.getEntryPoint(
            EntryPointSpec.make(QualifiedName.make("Cal.Benchmarks.Shootout.MessageThreadBody", "threadBody")), 
            moduleName, messageLogger);
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }

        stopWatch.stop();
        System.err.println("startup took: " + stopWatch);
        
        stopWatch.start();
        
        SynchronizedQueue input = new SynchronizedQueue(4);
        SynchronizedQueue output=input;
        for (int i=0; i<numberOfThreads; i++){
            output = MessageThread.makeMessageThread(output);
        }
        
        //start feeding the thread chain
        new FeederThread(input, numberOfMessagesToSend);

        //sum the output from the final queue
        int sum = 0;
        for (int i=0; i<numberOfMessagesToSend; i++) {
            sum += output.dequeue();
        }

        System.out.println(sum);
        
        stopWatch.stop();
        System.err.println("benchmark took: " + stopWatch);
    }
    
    static class FeederThread implements Runnable {
        private final int n;
        private SynchronizedQueue input;
        
        public FeederThread(SynchronizedQueue input, int n) {
            this.n = n;
            this.input = input;
            new Thread(this).start();
        }
        public void run() {
            for(int i=0; i< n; i++) {
                input.enqueue(0);
            }
            
            //shutdown threads
            input.enqueue(-1);
        }
    }
    
    static class MessageThread implements Runnable {
        
        SynchronizedQueue input, output;
        final CALExecutor executor;

        private MessageThread(SynchronizedQueue input) {
            this.input = input;
            this.output = new SynchronizedQueue(4);
            
            executor = calServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();
        }
        
        static SynchronizedQueue makeMessageThread(SynchronizedQueue inputQueue) {
            MessageThread messageThread = new MessageThread(inputQueue);
            new Thread(messageThread).start();
            return messageThread.output;
        }

        public void run() {
            try {
                executor.exec(threadBody, new Object[] { input, output });
            } catch (CALExecutorException ex) {
                System.err.println("Unexpected CAL compilation error: " + ex);
            }
        }
        

    }
}
