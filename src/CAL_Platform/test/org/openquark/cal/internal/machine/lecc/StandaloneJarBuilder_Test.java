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
 * StandaloneJarBuilder_Test.java
 * Created: Jun 5, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.internal.machine.lecc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.InternalProblemException;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.InvalidConfigurationException;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.LibraryClassSpec;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.MainClassSpec;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;

/**
 * A set of JUnit test cases that tests the {@link StandaloneJarBuilder} and the jar it creates.
 *
 * @author Joseph Wong
 */
public class StandaloneJarBuilder_Test extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;

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

        TestSuite suite = new TestSuite(StandaloneJarBuilder_Test.class);

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
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.lazyArgs
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_lazyArgs() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "lazyArgs"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasLazyArgs
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasLazyArgs() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasLazyArgs"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasAliasLazyArgs
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasAliasLazyArgs() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasAliasLazyArgs"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasAliasAliasLazyArgs
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasAliasAliasLazyArgs() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasAliasAliasLazyArgs"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.strictArgs
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_strictArgs() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "strictArgs"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasStrictArgs
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasStrictArgs() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasStrictArgs"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.cafEntryPoint
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_cafEntryPoint() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "cafEntryPoint"), "true");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasCafEntryPoint
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasCafEntryPoint() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasCafEntryPoint"), "true");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.mutualRecursion1
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_mutualRecursion1() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "mutualRecursion1"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasMutualRecursion1
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasMutualRecursion1() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasMutualRecursion1"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.mutualRecursion3
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_mutualRecursion3() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "mutualRecursion3"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasMutualRecursion3
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasMutualRecursion3() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasMutualRecursion3"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.cafMutualRecursion1
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_cafMutualRecursion1() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "cafMutualRecursion1"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasCafMutualRecursion1
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasCafMutualRecursion1() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasCafMutualRecursion1"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.cafMutualRecursion3
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_cafMutualRecursion3() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "cafMutualRecursion3"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.aliasCafMutualRecursion3
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_aliasCafMutualRecursion3() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "aliasCafMutualRecursion3"), "Hello World");
    }

    /**
     * Tests the building and running of a standalone jar based on StandaloneJarBuilder_Test_Support.testForeignClass
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_testForeignClass() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support", "testForeignClass"), "Cal.Test.JUnitSupport.StandaloneJarBuilder_Test_Support.JDummy");
    }     

    /**
     * Tests the building and running of a standalone jar based on M2.runExceptionHandlerCodeGenTest.
     * <p>
     * This is a regression test for a bytecode generation bug where invalid exception table entries
     * were generated in some circumstances when a foreign call is made inside a tail-recursive
     * loop, and the foreign method is declared to throw a checked exception.
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this
     * test case when there are other threads running.
     */
    public void testStandaloneJar_M2_runExceptionHandlerCodeGenTest() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.General.M2", "runExceptionHandlerCodeGenTest"), "M2.exceptionHandlerCodeGenTest passed.");
    }

    /**
     * Tests the building and running of a standalone jar based on CAL_Platform_TestSuite.main
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     */
    public void testStandaloneJar_CAL_Platform_TestSuite() throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        helpTestStandaloneJar(QualifiedName.make("Cal.Test.CAL_Platform_TestSuite", "main"), "true");
    }

    /**
     * Tests the building and running of a standalone jar based on the specified entry point.
     * <p>
     * Note that because this test modifies <code>System.out</code>, it is not safe to run this test case
     * when there are other threads running.
     * 
     * @param entryPointName the name of the entry point.
     * @param expectedOutput the expected output.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws MalformedURLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InternalProblemException 
     * @throws InvalidConfigurationException 
     * @throws UnableToResolveForeignEntityException 
     */
    private void helpTestStandaloneJar(final QualifiedName entryPointName, final String expectedOutput) throws IOException, ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        
        final String mainClassName = "org.openquark.cal.test.Test$Launcher";
        
        final File outputFile = File.createTempFile("testStandaloneJar", ".jar");
        try {
            StandaloneJarBuilder.buildStandaloneJar(outputFile, null, Collections.singletonList(MainClassSpec.make(mainClassName, entryPointName)), Collections.<LibraryClassSpec>emptyList(), leccCALServices.getWorkspaceManager(), StandaloneJarBuilder.DefaultMonitor.INSTANCE);
            
            final URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {outputFile.toURI().toURL()}, getClass().getClassLoader());
            
            final Class<?> mainClass = Class.forName(mainClassName, true, urlClassLoader);
            
            final Method mainMethod = mainClass.getMethod("main", new Class[] {String[].class});
            
            final PrintStream systemOut = System.out;
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final PrintStream replacementSystemOut = new PrintStream(baos, true);

            try {
                // replace the system output pipe.
                System.setOut(replacementSystemOut);
                try {
                    mainMethod.invoke(null, new Object[] {new String[0]});

                    replacementSystemOut.flush();
                    baos.flush();

                    assertEquals(expectedOutput, baos.toString().trim());

                } finally {
                    // put back the original system output pipe.
                    System.setOut(systemOut);
                }
                
            } finally {
                replacementSystemOut.close();
                baos.close();
            }
            
        } finally {
            outputFile.delete();
            outputFile.deleteOnExit();
        }
    }
}
