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
 * WorkspaceManager_DeepTest.java
 * Creation date: Dec 29, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.runtime.MachineType;


/**
 * A set of deep JUnit test cases for the WorkspaceManager class from the
 * services package. Included with this set are some tests that determines
 * whether or not there is any orphaned metadata in the workspace.
 *
 * @author Joseph Wong
 */
public class WorkspaceManager_DeepTest extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    private static WorkspaceManager workspaceManager;
    private static String workspaceDeclaration;

    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = true;
    
    /**
     * @return a test suite containing all the test cases using the ICE default workspace.
     */
    public static Test suite() {
        return suiteUsingWorkspaceDeclaration("ice.default.cws");
    }
    
    /**
     * @return a test suite containing all the test cases using the specified workspace declaration.
     */
    public static Test suiteUsingWorkspaceDeclaration(final String workspaceDeclFile) {

        TestSuite suite = new TestSuite(WorkspaceManager_DeepTest.class);

        return new TestSetup(suite) {

            @Override
            protected void setUp() {
                oneTimeSetUp(workspaceDeclFile);
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
    private static void oneTimeSetUp(String workspaceDeclFile) {
        // since the workspace declaration is determined by the caller, it's best to make the instance unshared
        leccCALServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(MachineType.LECC, workspaceDeclFile, true);
        
        workspaceManager = leccCALServices.getWorkspaceManager();
        
        workspaceDeclaration = workspaceDeclFile;
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        workspaceManager = null;
        leccCALServices = null;
        workspaceDeclaration = null;
    }

    /**
     * Constructor for WorkspaceManager_DeepTest.
     * 
     * @param name
     *            the name of the test.
     */
    public WorkspaceManager_DeepTest(String name) {
        super(name);
    }

    /**
     * Tests that there is no orphaned metadata in the workspace.
     */
    public void testNoOrphanedMetadata() {
        String resourceType = WorkspaceResource.METADATA_RESOURCE_TYPE;
        
        CALWorkspace workspace = workspaceManager.getWorkspace();
        
        // (Set of ModuleName) names of all modules in the current program.
        Set<ModuleName> moduleNameSet = new HashSet<ModuleName>();

        // (Set of CALFeatureName) CALFeatureNames for all features in the current program.
        Set<CALFeatureName> programFeatureNameSet = new HashSet<CALFeatureName>();
        
        // Grab the module names and feature names.
        ModuleName[] compiledModuleNames = workspaceManager.getProgramManager().getModuleNames();
        for (final ModuleName moduleName : compiledModuleNames) {
            MetaModule metaModule = workspace.getMetaModule(moduleName);
            if (metaModule != null) {
                moduleNameSet.add(moduleName);
                programFeatureNameSet.addAll(metaModule.getFeatureNames());
            }
        }

        List<FeatureName> orphanedResourceNameList = new ArrayList<FeatureName>();
        
        // At this point, we have the names of all the modules and features.  Now check resource iterators..
        for (final ModuleName moduleName : moduleNameSet) {
            ResourceManager resourceManager = workspace.getResourceManager(moduleName, resourceType);
            ResourceStore.Module resourceStore = (ResourceStore.Module)resourceManager.getResourceStore();
            
            for (Iterator<WorkspaceResource> it2 = resourceStore.getResourceIterator(moduleName); it2.hasNext(); ) {
            
                WorkspaceResource workspaceResource = it2.next();
                FeatureName resourceFeatureName = workspaceResource.getIdentifier().getFeatureName();

                if (!programFeatureNameSet.contains(resourceFeatureName)) {
                    orphanedResourceNameList.add(resourceFeatureName);
                }
            }
        }
        
        if (!orphanedResourceNameList.isEmpty()) {
            String message = "The following features in the " + workspaceDeclaration + " workspace have metadata but no longer exist:\n" + orphanedResourceNameList.toString().replaceAll(", ", ",\n");
            
            if (SHOW_DEBUGGING_OUTPUT) {
                System.err.println(message);
            }
            
            // using assertEquals() here instead of fail() so that the string can be shown by double-clicking on the failure
            // in the junit view
            assertEquals("", message);
        }
    }
}
