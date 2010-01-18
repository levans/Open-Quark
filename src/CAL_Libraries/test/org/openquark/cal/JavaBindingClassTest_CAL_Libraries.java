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
 * JavaBindingClassTest.java
 * Creation date: Oct 30, 2006.
 * By: Raymond Cypher
 */
package org.openquark.cal;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;


/**
 * This class tests that Java binding files exist for the public members of
 * the non-test modules in CAL_Libraries, and that the bindings are up-to-date.
 * 
 * @author Raymond Cypher
 */
public class JavaBindingClassTest_CAL_Libraries extends TestCase {

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    
    /**
     * Flag indicating that binding classes for non-public members should
     * be checked.
     */
    private static final boolean CHECK_NON_PUBLIC_BINDINGS = false;
    
    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        try {
            leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.libraries.cws");
        } catch (Exception e) {
            System.out.println("Failed to compile workspaces: " + e.getLocalizedMessage());
        } catch (Error e) {
            System.out.println("Failed to compile workspaces: " + e.getLocalizedMessage());
        }
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }

    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(JavaBindingClassTest_CAL_Libraries.class);

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
     * Constructor for JavaBindingClassTest.
     * 
     * @param name
     *            the name of the test.
     */
    public JavaBindingClassTest_CAL_Libraries(String name) {
        super(name);
    }
    
    public void testJavaBindingClasses () throws UnableToResolveForeignEntityException {
        ModuleName[] moduleNames = leccCALServices.getWorkspaceManager().getModuleNamesInProgram();
        ModuleName[] baseModuleNames = JavaBindingClassTest.getModuleNamesInBaseWorkspace("cal.platform.cws", leccCALServices);
        
        JavaBindingClassTest.helpTestJavaBindingClasses(leccCALServices, moduleNames, baseModuleNames, CHECK_NON_PUBLIC_BINDINGS, "org.openquark.cal");
    }
}
