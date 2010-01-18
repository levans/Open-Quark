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
 * Created: Feb 28, 2007
 * By: Robin Salkeld
 * --!>
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.machine.StatusListener.StatusListenerAdapter;
import org.openquark.cal.machine.StatusListener.Status.Module;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.MachineType;

/**
 * This class contains test for the EntryPointCache.
 */
public class EntryPointCache_Test extends TestCase {

    private BasicCALServices calServices;
    private EntryPointCache entryPointCache;
    
    private int listCompilationCount;
    private int allowedListCompilations;
    private StatusListener listCompilationListener;
    
    private static final List<String> TEST_LIST = Arrays.asList(new String[] { "one", "two", "three "});
        
    @Override
    protected void setUp() throws Exception {
        // TODO: We should probably test both machines with this code
        calServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
        entryPointCache = new EntryPointCache(calServices.getWorkspaceManager(), 10, 5);
        
        listCompilationListener = new StatusListenerAdapter() {
            @Override
            public void setModuleStatus(Module moduleStatus, ModuleName moduleName) {
                if (moduleStatus.equals(StatusListener.SM_LOADED) && moduleName.equals(CAL_List.MODULE_NAME)) {
                    listCompilationCount++;
                    if (listCompilationCount > allowedListCompilations) {
                        fail("The List adjunct module was compiled too many times");
                    }
                }
            }
        };
        listCompilationCount = 0;
        allowedListCompilations = 0;
    }
    
    @Override
    protected void tearDown() throws Exception {
        calServices.getWorkspaceManager().removeStatusListener(listCompilationListener);
        
        calServices = null;
        entryPointCache = null;
    }
    
    private static final TypeExprDefn STRING_TYPE = TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.String);
    private static final TypeExprDefn STRING_LIST_TYPE = TypeExprDefn.List.make(STRING_TYPE);
    
    public void testTouchesThenRuns() throws CALExecutorException, GemCompilationException {
        // Touch the List.head() function
        final ExecutionContext executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();

        EntryPointSpec headString = EntryPointSpec.make(CAL_List.Functions.head, 
                OutputPolicy.makeTypedDefaultOutputPolicy(STRING_TYPE));
        entryPointCache.cacheEntryPoints(Collections.singletonList(headString));
        
        // Use a status listener to assert that the List adjunct module doesn't get recompiled
        calServices.getWorkspaceManager().addStatusListener(listCompilationListener);
        
        assertEquals("one", entryPointCache.runFunction(headString, new Object[] { TEST_LIST }, executionContext));
    }
    
    public void testMultipleRunsOfSameFunction() throws InterruptedException {
        final EntryPointSpec headString = EntryPointSpec.make(CAL_List.Functions.head, 
                OutputPolicy.makeTypedDefaultOutputPolicy(STRING_TYPE));

        // Create many threads that will use the same entry point - the first should
        // safely compile the adjunct and the rest should use it without compiling again
        final ExecutionContext executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();
        Runnable functionCallRunnable = new Runnable() {
            public void run() {
                try {
                    assertEquals("one", entryPointCache.runFunction(headString, new Object[] { TEST_LIST }, executionContext));
                } catch (CALExecutorException e) {
                    fail("Unexpected exception: " + e);
                } catch (GemCompilationException e) {
                    fail("Unexpected exception: " + e);
                }
            }
        };
        
        // Use a status listener to assert that the List adjunct module is only compiled once
        calServices.getWorkspaceManager().addStatusListener(listCompilationListener);
        allowedListCompilations++;
        
        int numThreads = 5;
        Thread[] callThreads = new Thread[numThreads];
        for (int i = 0; i < callThreads.length; i++) {
            callThreads[i] = new Thread(functionCallRunnable);
            callThreads[i].start();
        }
        
        for (Thread element : callThreads) {
            element.join();
        }
    }
    
    public void testMultipleOverlappingTouches() throws CALExecutorException, GemCompilationException {
        final EntryPointSpec headString = EntryPointSpec.make(CAL_List.Functions.head, 
                OutputPolicy.makeTypedDefaultOutputPolicy(STRING_TYPE));
        final EntryPointSpec tailString = EntryPointSpec.make(CAL_List.Functions.tail, 
                OutputPolicy.makeTypedDefaultOutputPolicy(STRING_LIST_TYPE));
        final EntryPointSpec reverseString = EntryPointSpec.make(CAL_List.Functions.reverse, 
                OutputPolicy.makeTypedDefaultOutputPolicy(STRING_LIST_TYPE));
        final EntryPointSpec lengthString = EntryPointSpec.make(CAL_List.Functions.length, 
                new InputPolicy[] { InputPolicy.makeTypedDefaultInputPolicy(STRING_LIST_TYPE)},
                OutputPolicy.DEFAULT_OUTPUT_POLICY);
        
        final ExecutionContext executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();

        // Use a status listener to assert that the List adjunct module is only compiled twice
        calServices.getWorkspaceManager().addStatusListener(listCompilationListener);
        allowedListCompilations++;
        
        // Touch the first two functions
        entryPointCache.cacheEntryPoints(Arrays.asList(new EntryPointSpec[] { headString, tailString }));
        
        // Test the first two functions - no recompiling should occur
        assertEquals("one",                             entryPointCache.runFunction(headString, new Object[] { TEST_LIST }, executionContext));
        assertEquals(TEST_LIST.subList(1, 3),           entryPointCache.runFunction(tailString, new Object[] { TEST_LIST }, executionContext));
        
        // Touch the middle two functions - the result should be having entry points for the first three
        allowedListCompilations++;
        entryPointCache.cacheEntryPoints(Arrays.asList(new EntryPointSpec[] { tailString, reverseString }));
        
        // Test the first three functions - no recompiling should occur
        assertEquals("one",                             entryPointCache.runFunction(headString, new Object[] { TEST_LIST }, executionContext));
        assertEquals(TEST_LIST.subList(1, 3),           entryPointCache.runFunction(tailString, new Object[] { TEST_LIST }, executionContext));
        List<String> reversedList = new ArrayList<String>(TEST_LIST);
        Collections.reverse(reversedList);
        assertEquals(reversedList,                      entryPointCache.runFunction(reverseString, new Object[] { TEST_LIST }, executionContext));
        
        // Call another unrelated function - this should automatically touch it and retain the first three
        allowedListCompilations++;
        assertEquals(new Integer(3),                    entryPointCache.runFunction(lengthString, new Object[] { TEST_LIST }, executionContext));
        
        // Make sure the first three are still valid
        assertEquals("one",                             entryPointCache.runFunction(headString, new Object[] { TEST_LIST }, executionContext));
        assertEquals(TEST_LIST.subList(1, 3),           entryPointCache.runFunction(tailString, new Object[] { TEST_LIST }, executionContext));
        assertEquals(reversedList,                      entryPointCache.runFunction(reverseString, new Object[] { TEST_LIST }, executionContext));
    }
}
