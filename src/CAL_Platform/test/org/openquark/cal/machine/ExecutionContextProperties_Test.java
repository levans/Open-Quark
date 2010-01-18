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
 * ExecutionContextProperties_Test.java
 * Creation date: Jun 16, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.machine;

import java.util.HashMap;
import java.util.Locale;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.module.Cal.Core.CAL_System;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.cal.services.LocaleUtilities;
import org.openquark.util.time.TimeZone;


/**
 * A set of JUnit test cases for the ExecutionContextProperties class from the machine package.
 *
 * @author Joseph Wong
 */
public class ExecutionContextProperties_Test extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    private static BasicCALServices gCALServices;

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

        TestSuite suite = new TestSuite(ExecutionContextProperties_Test.class);

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
        leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
        gCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.G, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        gCALServices = null;
    }

    /**
     * Constructor for ExecutionContextProperties_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public ExecutionContextProperties_Test(String name) {
        super(name);
    }
    
    /**
     * Tests the CAL function System.getProperty.
     */
    public void testGetProperty() throws CALExecutorException, GemCompilationException {
        
        help_testGetProperty("int", new Integer(3));
        help_testGetProperty("char", new Character('c'));
        help_testGetProperty("string", "Hello World");
        help_testGetProperty("null", null);
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("hello", new Integer(42));
        map.put("bye", "bye");
        help_testGetProperty("map", map);
        
        help_testGetProperty("class", SourceModel.class);
    }
    
    private void help_testGetProperty(String propKey, Object propValue) throws CALExecutorException, GemCompilationException {
        
        help_testGetProperty(leccCALServices, propKey, propValue);
        help_testGetProperty(gCALServices, propKey, propValue);
    }
    
    private void help_testGetProperty(BasicCALServices calServices, String propKey, Object propValue) throws CALExecutorException, GemCompilationException {
        
        ExecutionContextProperties.Builder propBuilder = new ExecutionContextProperties.Builder();
        propBuilder.setProperty(propKey, propValue);
        ExecutionContextProperties properties = propBuilder.toProperties();
        ExecutionContext ec = calServices.getWorkspaceManager().makeExecutionContext(properties);
        
        assertSame(propValue, calServices.runFunction(EntryPointSpec.make(CAL_System.Functions.getProperty), ec, new Object[] { propKey }));
        
        assertEquals(Boolean.TRUE, calServices.runFunction(EntryPointSpec.make(CAL_System.Functions.hasProperty), ec, new Object[] { propKey }));

    }
    
    /**
     * Tests that System.getProperty returns a null value when the property does not exist.
     */
    public void testGetProperty_NonExistentProperty() throws CALExecutorException, GemCompilationException {
        
        help_testGetProperty_NonExistentProperty(leccCALServices);
        help_testGetProperty_NonExistentProperty(gCALServices);
    }
    
    private void help_testGetProperty_NonExistentProperty(BasicCALServices calServices) throws CALExecutorException, GemCompilationException {
        
        assertNull(calServices.runFunction(EntryPointSpec.make(CAL_System.Functions.getProperty), new Object[] { "nonExistent" }));
        assertEquals(Boolean.FALSE, calServices.runFunction(EntryPointSpec.make(CAL_System.Functions.hasProperty), new Object[] { "nonExistent" }));

    }
    
    /**
     * Tests that the current locale cannot be set via setProperty.
     */
    public void testSetProperty_FailureCaseWithLocale() {
        boolean exceptionThrown = false;
        try {
            ExecutionContextProperties.Builder propBuilder = new ExecutionContextProperties.Builder();
            propBuilder.setProperty(ExecutionContextProperties.SYS_PROP_KEY_LOCALE, new Locale("en"));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            fail("ExecutionContextProperties.Builder.setProperty should not have accepted the key cal.locale");
        }
    }
    
    /**
     * Tests that the current time zone cannot be set via setProperty.
     */
    public void testSetProperty_FailureCaseWithTimeZone() {
        boolean exceptionThrown = false;
        try {
            ExecutionContextProperties.Builder propBuilder = new ExecutionContextProperties.Builder();
            propBuilder.setProperty(ExecutionContextProperties.SYS_PROP_KEY_TIMEZONE, TimeZone.utc());
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            fail("ExecutionContextProperties.Builder.setProperty should not have accepted the key cal.locale");
        }
    }
    
    /**
     * Tests the CAL function System.getProperty for the current locale.
     */
    public void testCurrentLocale() throws CALExecutorException, GemCompilationException {
        help_testCurrentLocale(LocaleUtilities.INVARIANT_LOCALE);
        help_testCurrentLocale(Locale.CANADA);
        help_testCurrentLocale(Locale.FRENCH);
        help_testCurrentLocale(Locale.JAPAN);
    }
    
    private void help_testCurrentLocale(Locale locale) throws CALExecutorException, GemCompilationException {
        
        help_testCurrentLocale(leccCALServices, locale);
        help_testCurrentLocale(gCALServices, locale);
    }
    
    private void help_testCurrentLocale(BasicCALServices calServices, Locale locale) throws CALExecutorException, GemCompilationException {
        ExecutionContextProperties.Builder propBuilder = new ExecutionContextProperties.Builder();
        propBuilder.setLocale(locale);
        ExecutionContextProperties properties = propBuilder.toProperties();
        ExecutionContext ec = calServices.getWorkspaceManager().makeExecutionContext(properties);
        
        assertSame(locale, calServices.runFunction(EntryPointSpec.make(CAL_System.Functions.getProperty), ec, new Object[] { ExecutionContextProperties.SYS_PROP_KEY_LOCALE }));
    }
    
    /**
     * Tests the CAL function System.getProperty for the current time zone.
     */
    public void testCurrentTimeZone() throws CALExecutorException, GemCompilationException {
        help_testCurrentTimeZone(TimeZone.local());
        help_testCurrentTimeZone(TimeZone.utc());
        help_testCurrentTimeZone(TimeZone.getInstance("America/Los_Angeles"));
    }
    
    private void help_testCurrentTimeZone(TimeZone timeZone) throws CALExecutorException, GemCompilationException {
        
        help_testCurrentTimeZone(leccCALServices, timeZone);
        help_testCurrentTimeZone(gCALServices, timeZone);
    }
    
    private void help_testCurrentTimeZone(BasicCALServices calServices, TimeZone timeZone) throws CALExecutorException, GemCompilationException {        
        ExecutionContextProperties.Builder propBuilder = new ExecutionContextProperties.Builder();
        propBuilder.setTimeZone(timeZone.getID());
        ExecutionContextProperties properties = propBuilder.toProperties();
        ExecutionContext ec = calServices.getWorkspaceManager().makeExecutionContext(properties);
        
        final java.util.TimeZone propertyValue = (java.util.TimeZone)calServices.runFunction(EntryPointSpec.make(CAL_System.Functions.getProperty), ec, new Object[] {ExecutionContextProperties.SYS_PROP_KEY_TIMEZONE});
        assertEquals(timeZone, TimeZone.getInstance(propertyValue.getID()));
    }
}
