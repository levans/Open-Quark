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
 * DirectedGraphLibraryValidity_Test.java
 * Created: Oct 19, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.samples.directedgraph.DirectedGraphDemo;

/**
 * This tests the validity of the directedgraph.jar included in the project.
 *
 * @author Joseph Wong
 */
public class DirectedGraphLibraryValidity_Test extends TestCase {

    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(DirectedGraphLibraryValidity_Test.class);

        return new TestSetup(suite) {

            @Override
            protected void setUp() {
                oneTimeSetUp();
                
            }
    
            @Override
            protected void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
    }
    
    /**
     * Tests the validity of the directedgraph.jar in the project by running the included demo and seeing
     * that no exceptions or errors are thrown.
     * 
     * @throws CALExecutorException 
     * @throws IOException 
     */
    public void testValidity() throws CALExecutorException, IOException {
        final PrintStream systemOut = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream replacementSystemOut = new PrintStream(baos, true);

        try {
            // replace the system output pipe.
            System.setOut(replacementSystemOut);
            try {
                DirectedGraphDemo.main(new String[0]);

                replacementSystemOut.flush();
                baos.flush();

            } finally {
                // put back the original system output pipe.
                System.setOut(systemOut);
            }
            
        } finally {
            replacementSystemOut.close();
            baos.close();
        }
    }
}
