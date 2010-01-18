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
 * Car_Test.java
 * Creation date: Feb 3, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.internal.machine.lecc.LECCMachineTestUtilities;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.MachineType;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.Pair;
import org.openquark.util.TextEncodingUtilities;


/**
 * A set of JUnit test cases for testing the support for Car (CAL Archive) files.
 *
 * @author Joseph Wong
 */
public class Car_Test extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices sourceLeccCALServices;
    
    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = true;
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {
        
        TestSuite suite = new TestSuite(Car_Test.class);
        
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
        sourceLeccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        sourceLeccCALServices = null;
    }
    
    /**
     * Constructor for Car_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public Car_Test(String name) {
        super(name);
    }
    
    /**
     * Tests the building of a Car (with sourceless modules) and then loading it back into another workspace.
     */
    public void testRoundTripCarBuildingAndLoading() throws IOException, CALExecutorException {
        help_testRoundTripCarBuildingAndLoading(true, sourceLeccCALServices);
    }

    /**
     * Helper function for testing the building of a Car and then loading it back into another workspace.
     * @param testSourcelessModules whether to test with sourceless modules.
     * @param calServices
     */
    static void help_testRoundTripCarBuildingAndLoading(boolean testSourcelessModules, BasicCALServices calServices) throws FileNotFoundException, IOException, CALExecutorException {
        // This test does not work when the lecc machine is in Java source mode, so just skip it.
        if (LECCMachineTestUtilities.isJavaSourceConfig()) {
            return;
        }
        
        CarBuilder.Configuration config = new CarBuilder.Configuration(calServices.getWorkspaceManager());
        config.setBuildSourcelessModule(testSourcelessModules);
        
        File carFile = new File(System.getProperty("java.io.tmpdir"), "Car_Test__testRoundTripCarBuildingAndLoading.car");
        if (carFile.exists()) {
            boolean deleted = carFile.delete();
            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                System.err.println("Previous Car file not deleted: " + carFile);
            }
        }
        
        try {
            CarBuilder.buildCar(config, carFile.getName(), carFile);
            help_testRoundTripCarBuildingAndLoading(carFile, "Car_Test__testRoundTripCarBuildingAndLoading");
            
        } finally {
            
            for (int i = 0; i < 2; i++) {
                System.gc();
                System.runFinalization();
                System.gc();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ie) {
                    if (SHOW_DEBUGGING_OUTPUT) {
                        ie.printStackTrace(System.err);
                    }
                }
            }
            
            boolean deleted = carFile.delete();
            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                System.err.println("Car file not deleted: " + carFile);
            }
        }
    }

    /**
     * Helper function for testing the building of a Car and then loading it back into another workspace.
     * This helper function exists to control the lifetimes of the various objects involved so that proper
     * cleanup may take place afterwards.
     * 
     * @param carFile
     * @param workspaceClientID the workspace client ID for the non-nullary workspace to be created
     * @throws CALExecutorException
     */
    static void help_testRoundTripCarBuildingAndLoading(File carFile, String workspaceClientID) throws CALExecutorException {
        
        Pair<WorkspaceManager, File> workspaceManagerRootPair = help_buildDiscreteWorkspaceFromCar(carFile, workspaceClientID);
        
        try {
            WorkspaceManager workspaceManager = workspaceManagerRootPair.fst();
            help_testWithPlatformTests(workspaceManager);
            
        } finally {
            File rootDir = workspaceManagerRootPair.snd();
            
            boolean deleted = FileSystemHelper.delTree(rootDir);
            if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                System.err.println("Workspace directory not deleted: " + rootDir);
            }
        }
    }

    /**
     * Helper method for building a discrete workspace from a Car.
     * @param carFile
     * @param workspaceClientID
     * @return the Pair (WorkspaceManager, workspace root directory) for the discrete workspace.
     */
    static Pair<WorkspaceManager, File> help_buildDiscreteWorkspaceFromCar(File carFile, String workspaceClientID) {
        final URI fileURI = carFile.toURI();
        final String fileName = carFile.getName();
        
        WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider = new WorkspaceDeclaration.StreamProvider() {
            
            public String getName() {
                return "Car_Test__testRoundTripCarBuildingAndLoading.cws";
            }
            
            public String getLocation() {
                return "(A test workspace decl)";
            }
            
            public String getDebugInfo(VaultRegistry vaultRegistry) {
                return "constructed programmatically";
            }

            public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
                String contents = "import car SimpleCarFile " + fileName + " \"" + fileURI + "\" main.carspec";
                return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(contents));
            }
            
        };
        
        WorkspaceManager.SourceGenerationRootOverrideProvider workspaceRootProvider = new WorkspaceManager.SourceGenerationRootOverrideProvider() {

            /**
             * {@inheritDoc}
             */
            public File getSourceGenerationRootOverride(String clientID) {
                
                String newDirName = "unittest_workspace_root";
                
                if (clientID != null) {
                    newDirName += "." + clientID;
                }
                File workspaceRoot = new File(System.getProperty("java.io.tmpdir"), newDirName);
                
                if (FileSystemHelper.ensureDirectoryExists(workspaceRoot)) {
                    return workspaceRoot;    
                } else {
                    throw new RuntimeException("The unit test workspace root directory cannot be created: " + workspaceRoot);
                }
            }

        };
        
        // delete the existing directory, just in case
        File rootDir = workspaceRootProvider.getSourceGenerationRootOverride(workspaceClientID);
        FileSystemHelper.delTree(rootDir);
        
        WorkspaceManager workspaceManager = WorkspaceManager.getWorkspaceManager(workspaceClientID, MachineType.LECC, workspaceRootProvider);
        Status compileStatus = new Status("Compile status.");
        
        // Init the workspace.
        workspaceManager.initWorkspace(workspaceDeclarationProvider, true, compileStatus);
        assertEquals(compileStatus.getSeverity(), Status.Severity.OK);
        
        MessageLogger logger = new MessageLogger();
        workspaceManager.compile(logger, false, null);
        
        if (logger.getMaxSeverity().compareTo(CompilerMessage.Severity.WARNING) > 0) {
            fail("Compilation failed:\n" + logger.toString());
        }
        
        return new Pair<WorkspaceManager, File>(workspaceManager, rootDir);
    }

    /**
     * Helper method for testing with CAL_Platform_Tests
     * @param workspaceManager
     * @throws CALExecutorException
     */
    static void help_testWithPlatformTests(WorkspaceManager workspaceManager) throws CALExecutorException {
        help_testWithFunction(workspaceManager, CALPlatformTestModuleNames.CAL_PlatForm_Test_suite, "testModule");
    }

    /**
     * @param workspaceManager
     * @param moduleName
     * @param functionName
     * @throws CALExecutorException
     */
    static void help_testWithFunction(WorkspaceManager workspaceManager, ModuleName moduleName, String functionName) throws CALExecutorException {

        CompilerMessageLogger logger = new MessageLogger();
        EntryPoint entryPoint = workspaceManager.getCompiler().getEntryPoint(
            EntryPointSpec.make(QualifiedName.make(moduleName, functionName)), 
            moduleName, 
            logger);
        
        CALExecutor executor = workspaceManager.makeExecutor(workspaceManager.makeExecutionContextWithDefaultProperties());
        
        assertEquals(Boolean.TRUE, executor.exec(entryPoint, new Object[0]));
    }
}
