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
 * EventLoop.java
 * Created: Jul 21, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.Compiler;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.WorkspaceManager;


/**
 * Example class showing several different mechanism for CAL and Java to interact.
 * <p>
 * The examples are intended to be read in a linear fashion from the beginning of the
 * file to the end- they are ordered by complexity.
 * <p>
 * Some examples show how Java clients can call CAL functions in an "event loop" style 
 * i.e. different CAL functions may be called depending upon user input between calls.
 * <p>
 * The Java client is able to supply arguments, some of which are Java
 * primitive types, and some of which are CAL values obtained from
 * previous calls to CAL functions in the event loop. The CAL function would then
 * return a result, part of which may be a Java value directly interpretable by the
 * Java code, and part may be a CAL value for use in making further CAL calls.
 * Note that the CAL values obtained as intermediate results may not be fully evaluated.
 * For example, the value could be a computation producing an infinite list.
 * <p>
 *
 * @author Bo Ilic
 */
public final class EventLoop {
    
    private final BasicCALServices calServices;
    private final ExecutionContext executionContext;
    
    private static final String WORKSPACE_FILE_NAME = "cal.platform.test.cws";   
        
    EventLoop() {
        
        calServices = BasicCALServices.make(WORKSPACE_FILE_NAME);
               
        CompilerMessageLogger messageLogger = new MessageLogger();        
        if (!calServices.compileWorkspace(null, messageLogger)) {
            System.err.println(messageLogger.toString());
        }
               
        executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();        
                               
    }
        
    /**
     * Turn on various test programs by editing the main method. Useful VM arguments for running this are:
     * 
     * -Dorg.openquark.cal.machine.lecc.output_directory="D:\dev\EventLoop_lecc_bytecode"
     * -Xmx256m
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        usage();
        
        if (args.length != 1) {            
            return;
        }
        
        String command = args[0];
        System.out.println("\nDemo program: " + command + "\n");
        
        if (command.equalsIgnoreCase("getNthPrime")) { 
            EventLoop eventLoop = new EventLoop();
            eventLoop.getNthPrime(5);
        
        } else if (command.equalsIgnoreCase("primesIterator")) {
            EventLoop eventLoop = new EventLoop();
            eventLoop.primesIterator();
            
        } else if (command.equalsIgnoreCase("twoListIterators")) {
            EventLoop eventLoop = new EventLoop();
            eventLoop.twoListIterators();                
            
        } else if (command.equalsIgnoreCase("clientPrimePuller")) {
            EventLoop eventLoop = new EventLoop();
            eventLoop.clientPrimePuller();
                                          
        } else if (command.equalsIgnoreCase("clientTwoListPuller")) {
            EventLoop eventLoop = new EventLoop();            
            eventLoop.clientTwoListPuller();  
           
        } else if (command.equalsIgnoreCase("touchlessTwoListPuller")) {
            EventLoop eventLoop = new EventLoop();
            eventLoop.touchlessTwoListPuller();                    
                                           
        } else {
            System.out.println("invalid demo program choice");
        }
    }
    
    private static void usage() {
        System.out.println("EventLoop usage: valid command line arguments are one of");
        System.out.println();
        System.out.println("getNthPrime               --reuse of a single EntryPoint over multiple executions");
        System.out.println();
        System.out.println("primesIterator            --client side lazy evaluation of a single CAL list using a Java iterator");
        System.out.println("twoListIterators          --intertwined multiple client side CAL list lazy evaluation using two Java iterators");
        System.out.println();
        System.out.println("clientPrimePuller         --client side lazy evaluation of a single CAL list");        
        System.out.println("clientTwoListPuller       --multiple lazy lazy evaluation. Multiple EntryPoints");
        System.out.println("touchlessTwoListPuller    --use of EntryPointSpec and IO policies to avoid marshaling functions in CAL");
        
    }

    /**
     * Calls M2.getNthPrime, first with n = 5000, then 5001, etc, and then terminates.
     * This demonstrates reuse of the EntryPoint over multiple executions.
     * In particular, once the EntryPoint has been created, there is no further compilation
     * when evaluating the entry point at different arguments.
     * 
     * @param nTimes number of times to call the function in a row. First with 5000, then 5001, etc   
     */    
    private void getNthPrime(int nTimes) {
        
        //sample run:
        
        //Demo program: getNthPrime
        //
        //the result of getNthPrime 5000 is 48619
        //the result of getNthPrime 5001 is 48623
        //the result of getNthPrime 5002 is 48647
        //the result of getNthPrime 5003 is 48649
        //the result of getNthPrime 5004 is 48661
               
        CompilerMessageLogger messageLogger = new MessageLogger();        
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();   
        
        EntryPoint entryPoint = compiler.getEntryPoint(
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M2, "getNthPrime")), 
            CALPlatformTestModuleNames.M2, messageLogger);
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }
        CALExecutor executor = workspaceManager.makeExecutor(executionContext);
        
        try {
            for (int i = 0; i < nTimes; ++i) {
                Object result = executor.exec(entryPoint, new Object[] {new Integer(5000 + i)});
                System.out.println("the result of getNthPrime " + (5000 + i) + " is " + result);
            }
        } catch (CALExecutorException executorException) {
            System.out.println("Execution terminated with an executor exception:" + executorException);
        }                                  
    }

    /**
     * Illustrates calling a function that produces a lazy CAL list (M1.allPrimes), and then
     * having the Java client code traverse the list lazily using a java.util.Iterator. 
     * <p>
     * Once the EntryPoint is created, there is no further compilation, although the CAL evaluator
     * is involved in evaluating each subsequent element of the list as the Java iterator is traversed.
     * <p>
     * Similar to clientPrimePuller, but uses the List.toJIterator function to simplify
     * client traversal of the CAL list. This approach is perhaps less general than the approach
     * in clientPrimePuller, but is simpler to use.
     */
    private void primesIterator() {    
        
        //sample run:
        
        //Demo program: primesIterator
        //
        //How many more primes would you like? (enter q to quit)
        //3
        //the next 3 primes are [2, 3, 5]
        //How many more primes would you like? (enter q to quit)
        //4
        //the next 4 primes are [7, 11, 13, 17]
        //How many more primes would you like? (enter q to quit)
        //q        

        CompilerMessageLogger messageLogger = new MessageLogger(); 
        
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();    
       
        CALExecutor executor = workspaceManager.makeExecutor(executionContext);  
        
        //the below function represent a model of what is created when using EntryPointSpec classes i.e.
        //explicit input and output policies. 
        
        //allPrimesAdjunct :: Prelude.JObject;
        //allPrimesAdjunct = (Prelude.output # List.toJIterator) M1.allPrimes;            

        EntryPointSpec allPrimesEntryPointSpec =
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M1, "allPrimes"), null, OutputPolicy.ITERATOR_OUTPUT_POLICY);        
        
        EntryPoint allPrimesEntryPoint =
            compiler.getEntryPoint(allPrimesEntryPointSpec, CALPlatformTestModuleNames.M1, messageLogger);
        
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }
  
        try {
            Iterator<?> primesIterator = (Iterator<?>)executor.exec(allPrimesEntryPoint, null);
            
            BufferedReader inBuff = new BufferedReader(new BufferedReader(new java.io.InputStreamReader(System.in)));
                               
            while (true) {
                
                System.out.println("How many more primes would you like? (enter q to quit)");
                String command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nPrimesMore = 1;
                    try {
                        nPrimesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm extremely sorry, but I did not understand you. I'll assume one more prime.");
                    }                    
                    
                    List<Object> nextNPrimes = new ArrayList<Object>(nPrimesMore);
                    for (int i = 0; i < nPrimesMore; ++i) {
                        nextNPrimes.add(primesIterator.next());
                    }
                                              
                    System.out.println("the next " + nPrimesMore + " primes are " + nextNPrimes);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the allPrimes CAF).
                    workspaceManager.resetCachedResults(executionContext);                                                       
                }                                                                                               
            }
            
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (CALExecutorException executorException) {
            System.out.println("Execution terminated with an executor exception:" + executorException);
        }
    }
    
    /**
     * Illustrates calling two different CAL functions producing lazy CAL lists, and then
     * having the Java client code traverse the lists lazily using two different java.util.Iterators.
     * The traversals are interspersed with each other.
     * <p>
     * Once the EntryPoints are created, there is no further compilation, although the CAL evaluator is involved
     * in evaluating each subsequent element of the two lists as the Java iterators are traversed.
     * <p>
     * Similar to touchlessTwoListPuller but uses the List.toJIterator function to simplify
     * client traversal of the two CAL lists. This approach is perhaps less general than the approach
     * in touchlessTwoListPuller, but is simpler to use.
     */
    private void twoListIterators() {             
        
        //sample run
            
        //  Demo program: twoListIterators
        //  
        //  How many more primes would you like? (enter q to quit)
        //  5
        //  the next 5 primes are [2, 3, 5, 7, 11]
        //  How many more names would you like? (enter q to quit)
        //  3
        //  the next 3 names are [Alexander, Andy, Anton]
        //  How many more primes would you like? (enter q to quit)
        //  3
        //  the next 3 primes are [13, 17, 19]
        //  How many more names would you like? (enter q to quit)
        //  4
        //  the next 4 names are [Frank, Fred, Helen, Linda]
        //  How many more primes would you like? (enter q to quit)
        //  q
                        
        CompilerMessageLogger messageLogger = new MessageLogger(); 
        
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();            
        
        CALExecutor executor = workspaceManager.makeExecutor(executionContext);                
        
        List<EntryPointSpec> entryPointSpecs = new ArrayList<EntryPointSpec>(); //list of EntryPointSpec
        
        //the below function represent a model of what is created when using EntryPointSpec classes i.e.
        //explicit input and output policies. 
        
        //allPrimesAdjunct :: Prelude.JObject;
        //allPrimesAdjunct = (Prelude.output # List.toJIterator) M1.allPrimes;           
        
        EntryPointSpec allPrimesEntryPointSpec =
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M1, "allPrimes"), null, OutputPolicy.ITERATOR_OUTPUT_POLICY);
        EntryPointSpec stringListEntryPointSpec =
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M2, "stringList"), null, OutputPolicy.ITERATOR_OUTPUT_POLICY);       
                      
        entryPointSpecs.add(allPrimesEntryPointSpec);
        entryPointSpecs.add(stringListEntryPointSpec);        
        compiler.getEntryPoints(entryPointSpecs, CALPlatformTestModuleNames.M2, messageLogger);
                       
        List<EntryPoint> entryPoints =
            compiler.getEntryPoints(entryPointSpecs, CALPlatformTestModuleNames.M2, messageLogger);
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }        
        EntryPoint allPrimesEntryPoint = entryPoints.get(0);
        EntryPoint stringListEntryPoint = entryPoints.get(1);        
        
        try {
            Iterator<?> primesIterator = (Iterator<?>)executor.exec(allPrimesEntryPoint, null);
            Iterator<?> namesIterator = (Iterator<?>)executor.exec(stringListEntryPoint, null);     
                           
            BufferedReader inBuff = new BufferedReader(new BufferedReader(new java.io.InputStreamReader(System.in)));
                               
            while (true) {
                
                System.out.println("How many more primes would you like? (enter q to quit)");
                String command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nPrimesMore = 1;
                    try {
                        nPrimesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm extremely sorry, but I did not understand you. I'll assume one more prime.");
                    }                    
                    
                    List<Object> nextNPrimes = new ArrayList<Object>(nPrimesMore);
                    for (int i = 0; i < nPrimesMore; ++i) {
                        nextNPrimes.add(primesIterator.next());
                    }
                                              
                    System.out.println("the next " + nPrimesMore + " primes are " + nextNPrimes);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the allPrimes CAF).
                    workspaceManager.resetCachedResults(executionContext);                                                       
                } 
                
                System.out.println("How many more names would you like? (enter q to quit)");
                command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nNamesMore = 1;
                    try {
                        nNamesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm surpassingly sorry, but I did not understand you. I'll assume one more name.");
                    }
                                                          
                    List<Object> nextNNames = new ArrayList<Object>(nNamesMore);
                    for (int i = 0; i < nNamesMore; ++i) {
                        nextNNames.add(namesIterator.next());
                    }                   
                    System.out.println("the next " + nNamesMore + " names are " + nextNNames);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the Prelude.stringList CAF).
                    workspaceManager.resetCachedResults(executionContext);                      
                }                                                
            }
            
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (CALExecutorException executorException) {
            System.out.println("Execution terminated with an executor exception:" + executorException);
        }
    }    
    
    /**
     * Demonstrates an interactive, stateful CAL program with a Java client
     * lazily exploring the results of CAL function. Each "event loop" asks the
     * user how many more primes they want to see. The CAL function that is run
     * produces a Java list with the next number of primes the user asked for,
     * and a CAL list with the remaining primes (this is an infinite list, but
     * since CAL is lazy that is OK). This list of remaining primes is then
     * passed back in subsequent calls.
     */
    private void clientPrimePuller() {  
        
        //Demo program: clientPrimePuller
        //
        //How many more primes would you like? (enter q to quit)
        //4
        //the next 4 are [2, 3, 5, 7]
        //How many more primes would you like? (enter q to quit)
        //6
        //the next 6 are [11, 13, 17, 19, 23, 29]
        //How many more primes would you like? (enter q to quit)
        //2
        //the next 2 are [31, 37]
        //How many more primes would you like? (enter q to quit)
        //q
               
        CompilerMessageLogger messageLogger = new MessageLogger(); 
        
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();     
        
        CALExecutor executor = workspaceManager.makeExecutor(executionContext);
        
        EntryPoint allPrimesExternalEntryPoint =
            compiler.getEntryPoint(
                EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M2, "allPrimesExternal")), 
                CALPlatformTestModuleNames.M2, messageLogger);
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }
              
        try {
            
            CalValue remainingPrimesCalValue = (CalValue)executor.exec(allPrimesExternalEntryPoint, null); 
            
            EntryPoint nextNPrimesExternalEntryPoint =
                compiler.getEntryPoint(
                    EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M2, "nextNPrimesExternal")), 
                    CALPlatformTestModuleNames.M2, messageLogger);
            
            BufferedReader inBuff = new BufferedReader(new BufferedReader(new java.io.InputStreamReader(System.in)));           

            while (true) {
                
                System.out.println("How many more primes would you like? (enter q to quit)");
                String command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nPrimesMore = 1;
                    try {
                        nPrimesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm extremely sorry, but I did not understand you. I'll assume one more prime.");
                    }
                                                             
                    //a java.util.List with 2 elements. The first is a java.util.List of nPrimesMore primes. The second is a CalValue
                    //containing the remaining primes (as a lazy internal CAL list).
                    List<?> result = (List<?>)executor.exec(nextNPrimesExternalEntryPoint, new Object[] {remainingPrimesCalValue, new Integer(nPrimesMore)});
                    List<?> nextNPrimes = (List<?>)result.get(0);
                    remainingPrimesCalValue = (CalValue)result.get(1);
                    System.out.println("the next " + nPrimesMore + " are " + nextNPrimes);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the allPrimes CAF).
                    workspaceManager.resetCachedResults(executionContext);                                                            
                }                
            }
        
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (CALExecutorException executorException) {
            System.out.println("Execution terminated with an executor exception:" + executorException);
            return;
        }                                                  
    }
       
    /**
     * Demonstrates clients pulling values out of 2 different lists:
     * a CAL list of primes
     * a CAL list of Strings
     * 
     * All EntryPoints are created once and for all at the beginning.    
     */
    private void clientTwoListPuller() {
        
        //sample run:
        
        //Demo program: clientTwoListPuller
        //
        //How many more primes would you like? (enter q to quit)
        //4
        //the next 4 primes are [2, 3, 5, 7]
        //How many more names would you like? (enter q to quit)
        //3
        //the next 3 names are [Alexander, Andy, Anton]
        //How many more primes would you like? (enter q to quit)
        //6
        //the next 6 primes are [11, 13, 17, 19, 23, 29]
        //How many more names would you like? (enter q to quit)
        //2
        //the next 2 names are [Frank, Fred]
        //How many more primes would you like? (enter q to quit)
        //q       
        
        CompilerMessageLogger messageLogger = new MessageLogger(); 
        
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();     
        
        CALExecutor executor = workspaceManager.makeExecutor(executionContext);                
        
        List<QualifiedName> entryPointNames = new ArrayList<QualifiedName>(); //list of QualifiedName
        entryPointNames.add(QualifiedName.make(CALPlatformTestModuleNames.M2, "allPrimesExternal"));
        entryPointNames.add(QualifiedName.make(CALPlatformTestModuleNames.M2, "stringListExternal"));
        entryPointNames.add(QualifiedName.make(CALPlatformTestModuleNames.M2, "takeNExternal_ListOfInt"));
        entryPointNames.add(QualifiedName.make(CALPlatformTestModuleNames.M2, "takeNExternal_ListOfString"));                       
        
        List<EntryPoint> entryPoints =
            compiler.getEntryPoints(
                EntryPointSpec.buildListFromQualifiedNames(entryPointNames), 
                CALPlatformTestModuleNames.M2, 
                messageLogger);
        
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }
        EntryPoint allPrimesExternal = entryPoints.get(0);
        EntryPoint stringListExternal = entryPoints.get(1);
        EntryPoint takeNExternal_ListOfInt = entryPoints.get(2);
        EntryPoint takeNExternal_ListOfString = entryPoints.get(3);
        
        try {
            CalValue remainingPrimesCalValue = (CalValue)executor.exec(allPrimesExternal, null);
            CalValue remainingNamesCalValue = (CalValue)executor.exec(stringListExternal, null);     
                           
            BufferedReader inBuff = new BufferedReader(new BufferedReader(new java.io.InputStreamReader(System.in)));
                               
            while (true) {
                
                System.out.println("How many more primes would you like? (enter q to quit)");
                String command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nPrimesMore = 1;
                    try {
                        nPrimesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm extremely sorry, but I did not understand you. I'll assume one more prime.");
                    }                    
                     
                    List<?> result = (List<?>)executor.exec(takeNExternal_ListOfInt, 
                            new Object[] {remainingPrimesCalValue, new Integer(nPrimesMore)});
                   
                    //a java.util.List with 2 elements. The first is a java.util.List of nPrimesMore primes. The second is a CalValue
                    //containing the remaining primes (as a lazy internal CAL list).                      
                    List<?> nextNPrimes = (List<?>)result.get(0);
                    remainingPrimesCalValue = (CalValue)result.get(1);                      
                    System.out.println("the next " + nPrimesMore + " primes are " + nextNPrimes);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the allPrimes CAF).
                    workspaceManager.resetCachedResults(executionContext);                                                       
                } 
                
                System.out.println("How many more names would you like? (enter q to quit)");
                command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nNamesMore = 1;
                    try {
                        nNamesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm surpassingly sorry, but I did not understand you. I'll assume one more name.");
                    }
                                      
                    List<?> result = (List<?>)executor.exec(takeNExternal_ListOfString, 
                            new Object[] {remainingNamesCalValue, new Integer(nNamesMore)});
                   
                    //a java.util.List with 2 elements. The first is a java.util.List of nNamesMore names. The second is a CalValue
                    //containing the remaining names (as a CAL list).                      
                    List<?> nextNNames = (List<?>)result.get(0);
                    remainingNamesCalValue = (CalValue)result.get(1);                      
                    System.out.println("the next " + nNamesMore + " names are " + nextNNames);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the Prelude.stringList CAF).
                    workspaceManager.resetCachedResults(executionContext);                      
                }                                
                
            }
        
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (CALExecutorException executorException) {
            System.out.println("Execution terminated with an executor exception:" + executorException);
        }
    }
    
    /**
     * Demonstrates clients pulling values out of CAL lists with no special marshaling
     * wrappers added in a CAL module. All EntryPoints are created once and for all at the beginning.     
     */
    private void touchlessTwoListPuller() {  
        
        //sample run
            
        //  Demo program: touchlessTwoListPuller
        //  
        //  How many more primes would you like? (enter q to quit)
        //  5
        //  the next 5 primes are [2, 3, 5, 7, 11]
        //  How many more names would you like? (enter q to quit)
        //  3
        //  the next 3 names are [Alexander, Andy, Anton]
        //  How many more primes would you like? (enter q to quit)
        //  3
        //  the next 3 primes are [13, 17, 19]
        //  How many more names would you like? (enter q to quit)
        //  4
        //  the next 4 names are [Frank, Fred, Helen, Linda]
        //  How many more primes would you like? (enter q to quit)
        //  q
                        
        CompilerMessageLogger messageLogger = new MessageLogger(); 
        
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();           
        
        CALExecutor executor = workspaceManager.makeExecutor(executionContext);                
        
        List<EntryPointSpec> entryPointSpecs = new ArrayList<EntryPointSpec>(); //list of EntryPointSpec
        
        //the below 2 functions represent a model of what is created when using EntryPointSpec classes i.e.
        //explicit input and output policies. 
        
        //allPrimesAdjunct :: Prelude.JObject;
        //allPrimesAdjunct = (\x -> Prelude.output ((Prelude.unsafeCoerce x) ::  Prelude.CalValue)) M1.allPrimes;
        //
        //takeNIntAdjunct :: Prelude.JObject -> Prelude.JObject -> Prelude.JObject;
        //takeNIntAdjunct list nToTake =
        //    (\x -> Prelude.output ((Prelude.unsafeCoerce x) :: ([Int], CalValue)))
        //        (takeN 
        //            ((\x -> (Prelude.unsafeCoerce ((Prelude.input x) :: Prelude.CalValue)) :: [Prelude.Int]) list)
        //            ((Prelude.input :: Prelude.JObject -> Prelude.Int) nToTake)
        //         );
        
        EntryPointSpec allPrimesEntryPointSpec =
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M1, "allPrimes"), new InputPolicy[] {}, OutputPolicy.CAL_VALUE_OUTPUT_POLICY);
        EntryPointSpec stringListEntryPointSpec =
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M2, "stringList"), new InputPolicy[] {}, OutputPolicy.CAL_VALUE_OUTPUT_POLICY);
        
        String Prelude_Int = CAL_Prelude.TypeConstructors.Int.getQualifiedName();
        String Prelude_CalValue = CAL_Prelude.TypeConstructors.CalValue.getQualifiedName();
        String Prelude_String = CAL_Prelude.TypeConstructors.String.getQualifiedName();
        String Prelude_unsafeCoerce = CAL_Prelude.Functions.unsafeCoerce.getQualifiedName();
        String Prelude_output = CAL_Prelude.Functions.output.getQualifiedName();
        
        SourceModel.TypeExprDefn listOfIntType = SourceModel.TypeExprDefn.List.make(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int));
        EntryPointSpec takeNListOfInt =
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M2, "takeN"), new InputPolicy[] {
            InputPolicy.makeTypedCalValueInputPolicy(listOfIntType),
            InputPolicy.DEFAULT_INPUT_POLICY
            }, OutputPolicy.make("(\\x -> " + Prelude_output + " ((" + Prelude_unsafeCoerce + " x) :: ([" + Prelude_Int + "], " + Prelude_CalValue + ")))")); 

        
        SourceModel.TypeExprDefn listOfStringType = SourceModel.TypeExprDefn.List.make(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.String));
        EntryPointSpec takeNListOfString =
            EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M2, "takeN"), new InputPolicy[] {
            InputPolicy.makeTypedCalValueInputPolicy(listOfStringType),
            InputPolicy.DEFAULT_INPUT_POLICY
        }, OutputPolicy.make("(\\x -> " + Prelude_output + " ((" + Prelude_unsafeCoerce + " x) :: ([" + Prelude_String + "], " + Prelude_CalValue + ")))"));        
                      
        entryPointSpecs.add(allPrimesEntryPointSpec);
        entryPointSpecs.add(stringListEntryPointSpec);
        entryPointSpecs.add(takeNListOfInt);
        entryPointSpecs.add(takeNListOfString);        
                       
        List<EntryPoint> entryPoints =
            compiler.getEntryPoints(entryPointSpecs, CALPlatformTestModuleNames.M2, messageLogger);
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }
        EntryPoint allPrimesEntryPoint = entryPoints.get(0);
        EntryPoint stringListEntryPoint = entryPoints.get(1);
        EntryPoint takeNListOfIntEntryPoint = entryPoints.get(2);
        EntryPoint takeNListOfStringEntryPoint = entryPoints.get(3);
        
        try {
            CalValue remainingPrimesCalValue = (CalValue)executor.exec(allPrimesEntryPoint, null);
            CalValue remainingNamesCalValue = (CalValue)executor.exec(stringListEntryPoint, null);     
                           
            BufferedReader inBuff = new BufferedReader(new BufferedReader(new java.io.InputStreamReader(System.in)));
                               
            while (true) {
                
                System.out.println("How many more primes would you like? (enter q to quit)");
                String command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nPrimesMore = 1;
                    try {
                        nPrimesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm extremely sorry, but I did not understand you. I'll assume one more prime.");
                    }                    
                     
                    List<?> result = (List<?>)executor.exec(takeNListOfIntEntryPoint, 
                            new Object[] {remainingPrimesCalValue, new Integer(nPrimesMore)});
                   
                    //a java.util.List with 2 elements. The first is a java.util.List of nPrimesMore primes. The second is a CalValue
                    //containing the remaining primes (as a lazy internal CAL list).                      
                    List<?> nextNPrimes = (List<?>)result.get(0);
                    remainingPrimesCalValue = (CalValue)result.get(1);                      
                    System.out.println("the next " + nPrimesMore + " primes are " + nextNPrimes);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the allPrimes CAF).
                    workspaceManager.resetCachedResults(executionContext);                                                       
                } 
                
                System.out.println("How many more names would you like? (enter q to quit)");
                command = inBuff.readLine().trim();
                               
                if (command.startsWith("q")) {
                    break;
                    
                } else {
                    int nNamesMore = 1;
                    try {
                        nNamesMore = Integer.parseInt(command);                       
                    } catch (NumberFormatException nfe) {
                        System.out.println("I'm surpassingly sorry, but I did not understand you. I'll assume one more name.");
                    }
                                      
                    List<?> result = (List<?>)executor.exec(takeNListOfStringEntryPoint, 
                            new Object[] {remainingNamesCalValue, new Integer(nNamesMore)});
                   
                    //a java.util.List with 2 elements. The first is a java.util.List of nNamesMore names. The second is a CalValue
                    //containing the remaining names (as a lazy internal CAL list).                      
                    List<?> nextNNames = (List<?>)result.get(0);
                    remainingNamesCalValue = (CalValue)result.get(1);                      
                    System.out.println("the next " + nNamesMore + " names are " + nextNNames);
                    
                    //we can reset cached CAFs here if we like. (Works without this as well. The point here is to not hold onto
                    //the Prelude.stringList CAF).
                    workspaceManager.resetCachedResults(executionContext);                      
                }                                                
            }
            
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (CALExecutorException executorException) {
            System.out.println("Execution terminated with an executor exception:" + executorException);
        }
    }
                
}

