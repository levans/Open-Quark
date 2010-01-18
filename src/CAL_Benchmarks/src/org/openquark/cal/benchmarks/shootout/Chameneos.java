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

import java.util.ArrayList;
import java.util.List;


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
 * This implements the java portion of the Chameneos benchmark. It is responsible for starting
 * and coordinating the threads. The body of the threads is implemented in CAL, see
 * Cal.Benchmarks.Shootout.ChameneosThreadBody
 * 
 *
 * @author Magnus Byne
 */
public class Chameneos {

    private static BasicCALServices calServices;


    public static void main(String args[]) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int numberOfMeetings;
        if (args.length == 1) {
            numberOfMeetings = Integer.parseInt(args[0]);
        } else {
            numberOfMeetings = 5000000;
        }

        
        //initialize th CAL workspace and get entry points for the CAL functions
        final String WORKSPACE_FILE_NAME = "cal.benchmark.cws";  
        List<EntryPoint> entryPoints;

        calServices = BasicCALServices.makeCompiled(WORKSPACE_FILE_NAME, new MessageLogger());

        CompilerMessageLogger messageLogger = new MessageLogger();        
        Compiler compiler = calServices.getCompiler();   

        ModuleName moduleName= ModuleName.make("Cal.Benchmarks.Shootout.ChameneosThreadBody");

        List<QualifiedName> functionalAgentNames = new ArrayList<QualifiedName>();
        functionalAgentNames.add(QualifiedName.make(moduleName, "chameneos"));
        functionalAgentNames.add(QualifiedName.make(moduleName, "makeEmptyRoom"));
        functionalAgentNames.add(QualifiedName.make(moduleName, "Red"));
        functionalAgentNames.add(QualifiedName.make(moduleName, "Yellow"));
        functionalAgentNames.add(QualifiedName.make(moduleName, "Blue"));

        CALExecutor executor = calServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();

        entryPoints = compiler.getEntryPoints(EntryPointSpec.buildListFromQualifiedNames(functionalAgentNames), 
            moduleName, messageLogger);
        
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
            System.exit(1);
        }

        stopWatch.stop();
        System.err.println("Startup took: " + stopWatch);
        
        try {
            stopWatch.start();

            CalMonitor meetingRoom =  (CalMonitor) executor.exec(entryPoints.get(1), new Object[] { new Integer(numberOfMeetings) });
            CalMonitor occupant = new CalMonitor();

            Object red = executor.exec(entryPoints.get(2), null);
            Object yellow = executor.exec(entryPoints.get(3), null);
            Object blue = executor.exec(entryPoints.get(4), null);

            //start the three chameneos, each with a different colors
            ChameneosThread t1 = ChameneosThread.makeChameneosThread(entryPoints.get(0), red, meetingRoom, occupant);
            ChameneosThread t2 = ChameneosThread.makeChameneosThread(entryPoints.get(0), yellow, meetingRoom, occupant);
            ChameneosThread t3 = ChameneosThread.makeChameneosThread(entryPoints.get(0), blue, meetingRoom, occupant);

            //sum the meetings of each of the chameneos
            System.out.println(t1.getResult() + t2.getResult() + t3.getResult());
            stopWatch.stop();
            System.err.println("Benchmark took: " + stopWatch);

            
        } catch (CALExecutorException ex) {
            System.err.println("Unexpected CAL compilation error: " + ex);
        }

    }     

    static class ChameneosThread implements Runnable {
        final CalMonitor occupant;
        final EntryPoint entryPoint;
        final Object color;
        final CalMonitor meetingRoom;
        Integer numMeetings;
        
        private ChameneosThread(EntryPoint entryPoint, Object color, CalMonitor meetingRoom, CalMonitor occupant) {

            this.entryPoint = entryPoint;
            this.color = color;
            this.meetingRoom = meetingRoom;
            this.occupant = occupant;
            
        }

        static ChameneosThread makeChameneosThread(EntryPoint entryPoint, Object color, CalMonitor meetingRoom, CalMonitor occupant) {
            ChameneosThread chameneosThread = new ChameneosThread(entryPoint, color, meetingRoom, occupant);
            new Thread(chameneosThread).start();
            return chameneosThread;
        }

        /**start the CAL implementation of the threadbody running*/
        public void run() {
            try {
                synchronized (this) {
                    CALExecutor executor = calServices.getWorkspaceManager().makeExecutorWithNewContextAndDefaultProperties();

                    numMeetings = (Integer) executor.exec(entryPoint, new Object[] { color, meetingRoom,  occupant, new Integer(0)});
                    notify();
                }
                //System.out.println(numMeetings);
            } catch (CALExecutorException ex) {
                System.err.println("Unexpected CAL compilation error: " + ex);
            }
        }

        /**get the numMeetings of the CAL thread body - this will block until it has completed*/
        public int getResult() {
            synchronized (this) {
                while (numMeetings == null) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        System.err.println("Unexpected interrupt");
                    }
                }
            }
            return numMeetings.intValue();
        }
    }
}
