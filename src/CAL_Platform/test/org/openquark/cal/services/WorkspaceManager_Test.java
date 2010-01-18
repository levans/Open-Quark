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
 * WorkspaceManager_Test.java
 * Creation date: Mar 3, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleSourceDefinitionGroup;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelModuleSource;
import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.module.Cal.Core.CAL_Bits;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.MachineType;


/**
 * A set of JUnit test cases for the WorkspaceManager class from the services package.
 *
 * @author Joseph Wong
 */
public class WorkspaceManager_Test extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    private static WorkspaceManager workspaceManager;

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

        TestSuite suite = new TestSuite(WorkspaceManager_Test.class);

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
        
        workspaceManager = leccCALServices.getWorkspaceManager();
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        workspaceManager = null;
        leccCALServices = null;
    }

    /**
     * Constructor for WorkspaceManager_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public WorkspaceManager_Test(String name) {
        super(name);
    }

    /**
     * Tests the method WorkspaceManager.getWorkspace(), making sure that it returns
     * a non-null value.
     */
    public void testGetWorkspace_notNull() {
        assertNotNull(workspaceManager.getWorkspace());
    }

    /**
     * Tests the method WorkspaceManager.getCompiler(), making sure that it returns
     * a non-null value.
     */
    public void testGetCompiler_notNull() {
        assertNotNull(workspaceManager.getCompiler());
    }

    /**
     * Tests the method WorkspaceManager.getTypeChecker(), making sure that it returns
     * a non-null value.
     */
    public void testGetTypeChecker_notNull() {
        assertNotNull(workspaceManager.getTypeChecker());
    }

    /**
     * Tests the creation of a new module through the method WorkspaceManager.makeModule().
     */
    public void testMakeModule() {
        ModuleName moduleName = ModuleName.make("TEST_MakeModule");
        
        SourceModel.ModuleDefn moduleDefn = SourceModel.ModuleDefn.make(moduleName, new SourceModel.Import[] { SourceModel.Import.make(CAL_Prelude.MODULE_NAME) }, null);
        ModuleSourceDefinition moduleSourceDefn = new SourceModelModuleSource(moduleDefn);

        try {
            CompilerMessageLogger logger = new MessageLogger();
            Severity severity = workspaceManager.makeModule(moduleSourceDefn, logger);
            
            assertTrue(severity.compareTo(Severity.ERROR) < 0);

            //todo-jowong these assertions should be true, but are currently false because
            //            makeModule() doesn't register the newly created module with the workspace
            /*
            assertTrue(workspaceManager.containsModule(moduleName));
            */
            assertTrue(workspaceManager.hasModuleInProgram(moduleName));
            
        } finally {
            workspaceManager.removeModule(moduleName, new Status("Removal of test module"));
        }
    }

    /**
     * Tests the creation of multiple modules through the method WorkspaceManager.makeModules().
     */
    public void testMakeModules() {
        ModuleName[] moduleNames = { ModuleName.make("TEST_MakeModules1"), ModuleName.make("TEST_MakeModules2") };
        
        SourceModel.ModuleDefn moduleDefn1 = SourceModel.ModuleDefn.make(moduleNames[0], new SourceModel.Import[] { SourceModel.Import.make(CAL_Prelude.MODULE_NAME) }, null);
        ModuleSourceDefinition moduleSourceDefn1 = new SourceModelModuleSource(moduleDefn1);

        SourceModel.ModuleDefn moduleDefn2 = SourceModel.ModuleDefn.make(moduleNames[1], new SourceModel.Import[] { SourceModel.Import.make(CAL_Prelude.MODULE_NAME) }, null);
        ModuleSourceDefinition moduleSourceDefn2 = new SourceModelModuleSource(moduleDefn2);
        
        ModuleSourceDefinition[] moduleSourceDefns = { moduleSourceDefn1, moduleSourceDefn2 };
        
        ModuleSourceDefinitionGroup mdg = new ModuleSourceDefinitionGroup(moduleSourceDefns);

        try {
            CompilerMessageLogger logger = new MessageLogger();
            Severity severity = workspaceManager.makeModules(moduleNames, mdg, logger);
            
            assertTrue(severity.compareTo(Severity.ERROR) < 0);

            //todo-jowong these assertions should be true, but are currently false because
            //            makeModule() doesn't register the newly created module with the workspace
            /*
            assertTrue(workspaceManager.containsModule(moduleNames[0]));
            assertTrue(workspaceManager.containsModule(moduleNames[1]));
            */
            assertTrue(workspaceManager.hasModuleInProgram(moduleNames[0]));
            assertTrue(workspaceManager.hasModuleInProgram(moduleNames[1]));
            
        } finally {
            Status status = new Status("Removal of test modules");
            for (final ModuleName moduleName : moduleNames) {
                workspaceManager.removeModule(moduleName, status);
            }
        }
    }

    /**
     * Tests the removal of a module through the method WorkspaceManager.removeModule().
     */
    public void testRemoveModule() {
        ModuleName[] moduleNames = { ModuleName.make("TEST_RemoveModules1"), ModuleName.make("TEST_RemoveModules2") };
        
        SourceModel.ModuleDefn moduleDefn1 = SourceModel.ModuleDefn.make(moduleNames[0], new SourceModel.Import[] { SourceModel.Import.make(CAL_Prelude.MODULE_NAME) }, null);
        ModuleSourceDefinition moduleSourceDefn1 = new SourceModelModuleSource(moduleDefn1);

        SourceModel.ModuleDefn moduleDefn2 = SourceModel.ModuleDefn.make(moduleNames[1], new SourceModel.Import[] { SourceModel.Import.make(CAL_Prelude.MODULE_NAME), SourceModel.Import.make(moduleNames[0]) }, null);
        ModuleSourceDefinition moduleSourceDefn2 = new SourceModelModuleSource(moduleDefn2);
        
        ModuleSourceDefinition[] moduleSourceDefns = { moduleSourceDefn1, moduleSourceDefn2 };
        
        ModuleSourceDefinitionGroup mdg = new ModuleSourceDefinitionGroup(moduleSourceDefns);

        CompilerMessageLogger logger = new MessageLogger();
        Severity severity = workspaceManager.makeModules(moduleNames, mdg, logger);
        
        assertTrue(severity.compareTo(Severity.ERROR) < 0);
        
        Status status = new Status("Removal of test modules");
        workspaceManager.removeModule(moduleNames[0], status);
        
        assertTrue(status.isOK());
        
        assertTrue(!workspaceManager.getWorkspace().containsModule(moduleNames[0]));
        assertTrue(!workspaceManager.getWorkspace().containsModule(moduleNames[1]));
    }

    /**
     * Tests the method WorkspaceManager.containsModule() for the case where the workspace
     * does indeed have the module.
     */
    public void testContainsModule_workspaceHasModule() {
        assertTrue(workspaceManager.getWorkspace().containsModule(CAL_Prelude.MODULE_NAME));
    }
    
    /**
     * Tests the method WorkspaceManager.containsModule() for the case where the module
     * name specified refers to a non-existent module.
     */
    public void testContainsModule_nonexistentModule() {
        assertTrue(!workspaceManager.getWorkspace().containsModule(ModuleName.make("NONEXISTENT_MODULE")));
    }

    /**
     * Tests the method WorkspaceManager.getModuleNames(). The workspace should
     * have the Prelude module.
     */
    public void testGetModuleNames() {
        boolean hasPrelude = false;
        
        ModuleName[] moduleNames = workspaceManager.getWorkspace().getModuleNames();
        for (final ModuleName moduleName : moduleNames) {
            if (moduleName.equals(CAL_Prelude.MODULE_NAME)) {
                hasPrelude = true;
                break;
            }
        }
        
        assertTrue(hasPrelude);
    }

    /**
     * Tests the method WorkspaceManager.getDependentModuleNames(). The
     * workspace should have the Prelude module, and the Bits module should be one
     * of its dependents
     */
    public void testGetDependentModuleNames() {
        boolean hasMap = false;
        
        for (final ModuleName moduleName : workspaceManager.getDependentModuleNames(CAL_Prelude.MODULE_NAME)) {
            if (moduleName.equals(CAL_Bits.MODULE_NAME)) {
                hasMap = true;
                break;
            }
        }
        
        assertTrue(hasMap);
    }

    /**
     * Tests the method WorkspaceManager.getModuleTypeInfo(). The workspace
     * manager should be able to return the module type info for the Prelude
     * module.
     */
    public void testGetModuleTypeInfo() {
        ModuleTypeInfo mti = workspaceManager.getModuleTypeInfo(CAL_Prelude.MODULE_NAME);
        assertNotNull(mti);
        assertEquals(CAL_Prelude.MODULE_NAME, mti.getModuleName());
    }
    
    /**
     * Tests that WorkspaceManager.getMachineStatisticsForModule does not throw a NullPointerException
     * for a non-existent module.
     */
    public void testGetMachineStatisticsForNonExistentModule() {
        try {
            workspaceManager.getMachineStatisticsForModule(ModuleName.make("NonExistent"));
        } catch (NullPointerException e) {
            fail("NullPointerException thrown in WorkspaceManager.getMachineStatisticsForModule for non-existent module");
        }
    }

//    /**
//     * Tests the serialization and deserialization of modules to files.  After
//     * we compile a workspace and serialize it to a known location, we should
//     * be able to load it again without error.
//     */
//    public void testLoadWorkspace() throws FileNotFoundException, IOException {
//        
//        // Ensure that the existing, shared workspace has been compiled
//        MessageLogger ml = new MessageLogger();
//        workspaceManager.compile(ml, true, null);
//        
//        assertTrue (ml.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
//        
//        // Serialize out the modules of the workspace
//        String tmpDir = System.getProperty("java.io.tmpdir");
//        File persistentLocation = new File(tmpDir, "WorkspaceManager_Test.testLoadWorkspace");
//        persistentLocation.mkdirs(); // Ensure that target directory exists
//        
//        WorkspaceManager wm = null;
//        try {
//            for (Iterator it = workspaceManager.getModuleNames(); it.hasNext();) {
//                String moduleName = (String) it.next();
//                
//                ModuleTypeInfo mti = workspaceManager.getModuleTypeInfo(moduleName);
//                Module m = mti.getModule();
//                
//                File f = new File(persistentLocation, moduleName + ".cmf");
//                FileOutputStream fos = new FileOutputStream(f, false);
//                RecordOutputStream ros = new RecordOutputStream(fos);
//                
//                m.write(ros);
//                
//                ros.close();
//                fos.flush();
//                fos.close();
//            }
//            
//            // Create a fresh workspace to load the modules into
//            WorkspaceDeclaration.StreamProvider streamProvider = 
//                DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider("ice.default.cws");
//            wm = new WorkspaceManager("junit", true);
//            Status initStatus = new Status("Loading workspace");
//            wm.initWorkspace(streamProvider, false, initStatus);
//            
//            assertTrue(initStatus.getSeverity().compareTo(Status.Severity.WARNING) < 0);
//            
//            // Attempt to load the modules back in from disk
//            wm.load(persistentLocation, ml);
//            assertTrue (ml.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0);
//            
//            // check the module CALDoc for Prelude
//            ModuleTypeInfo preludeTypeInfo = wm.getModuleTypeInfo("Prelude");
//            assertNotNull(preludeTypeInfo.getCALDocComment());
//            
//            // check the function CALDoc for Prelude.either
//            assertNotNull(preludeTypeInfo.getFunction("either").getCALDocComment());
//            
//            // check the foreign function CALDoc for String.equalsStringIgnoreCase
//            assertNotNull(stringTypeInfo.getFunction("equalsStringIgnoreCase").getCALDocComment());
//            
//            // check the primitive function CALDoc for Prelude.seq
//            assertNotNull(preludeTypeInfo.getFunction("seq").getCALDocComment());
//            
//            // check the type constructor CALDoc for Prelude.Ordering
//            assertNotNull(preludeTypeInfo.getTypeConstructor("Ordering").getCALDocComment());
//            
//            // check the foreign type CALDoc for Prelude.JObject
//            assertNotNull(preludeTypeInfo.getTypeConstructor("JObject").getCALDocComment());
//            
//            // check the data constructor CALDoc for Prelude.Just
//            assertNotNull(preludeTypeInfo.getDataConstructor("Just").getCALDocComment());
//            
//            // check the type class CALDoc for Prelude.Bounded
//            assertNotNull(preludeTypeInfo.getTypeClass("Bounded").getCALDocComment());
//            
//            // check the class method CALDoc for Prelude.greaterThan
//            assertNotNull(preludeTypeInfo.getClassMethod("greaterThan").getCALDocComment());
//            
//            // check the instance CALDoc for Bounded Int
//            ClassInstance boundedInt = preludeTypeInfo.getClassInstance(ClassInstanceIdentifier.make(QualifiedName.makePreludeName("Bounded"), "Prelude.Int"));
//            assertNotNull(boundedInt.getCALDocComment());
//            
//            // check the instance method CALDoc for Bounded Int minBound
//            assertNotNull(boundedInt.getMethodCALDocComment("minBound"));
//            
//        } finally {
//            // FIXME gc needed since the load() operation does not explicitly close the FileInputStreams it uses
//            System.gc();
//            FileSystemHelper.delTree(persistentLocation);
//            if (wm != null) {
//                File workspaceRoot = wm.getWorkspace().getWorkspaceRoot();
//                if (workspaceRoot != null) {
//                    if (workspaceRoot.getName().endsWith("junit")) {
//                        FileSystemHelper.delTree(workspaceRoot);                        
//                    }
//                }
//            }
//        }
//    }
}
