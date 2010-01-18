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
 * Creation date: Oct 19, 2006.
 * By: Edward Lam
 */
package org.openquark.cal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.internal.javamodel.JavaBindingGenerator;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.DefaultWorkspaceDeclarationProvider;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceDeclaration;
import org.openquark.cal.services.WorkspaceLoader;


/**
 * This class tests that an up-to-date Java binding class has been generated
 * for all the non-test modules in CAL_Platform.
 * @author Raymond Cypher
 */
public class JavaBindingClassTest extends TestCase {

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;
    
    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        try {
            // No one else uses the cal.platform.bindings.cws, so it should be an unshared instance
            leccCALServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(MachineType.LECC, "cal.platform.bindings.cws", true);
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

        TestSuite suite = new TestSuite(JavaBindingClassTest.class);

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
    public JavaBindingClassTest(String name) {
        super(name);
    }    
    
    public void testJavaBindingClasses () throws UnableToResolveForeignEntityException {
        ModuleName moduleNames[] = leccCALServices.getWorkspaceManager().getModuleNamesInProgram();
        ModuleName baseModuleNames[] = new ModuleName[0];
        
        helpTestJavaBindingClasses(leccCALServices, moduleNames, baseModuleNames, true, "org.openquark.cal");
    }

    public static void helpTestJavaBindingClasses(BasicCALServices calServices, ModuleName[] moduleNames, ModuleName[] baseModuleNames, boolean checkNonPublicBindings, String basePackage) throws UnableToResolveForeignEntityException {
        Set<ModuleName> moduleNamesSet = new HashSet<ModuleName>(Arrays.asList(moduleNames));
        moduleNamesSet.removeAll(Arrays.asList(baseModuleNames));
            
        List<ModuleName> failedModules = new ArrayList<ModuleName>();
        List<String> failedModulePackages = new ArrayList<String>();
        List<Boolean> failedModuleInternal = new ArrayList<Boolean>();
        
        for (final ModuleName moduleName : moduleNamesSet) {
            ModuleTypeInfo moduleTypeInfo = calServices.getWorkspaceManager().getModuleTypeInfo(moduleName);

            String targetPackage = basePackage + ".module";
            if (moduleName.toSourceText().startsWith("Cal.Internal.Optimizer")) {
                targetPackage = basePackage + ".internal.module";
            }
            if (!JavaBindingGenerator.checkJavaBindingClass(moduleTypeInfo, targetPackage, true, true)) {
                failedModules.add(moduleName);
                failedModulePackages.add(targetPackage);
                failedModuleInternal.add(Boolean.FALSE);
            }
            
            if (checkNonPublicBindings) {
                targetPackage = basePackage + ".internal.module";
                if (!JavaBindingGenerator.checkJavaBindingClass(moduleTypeInfo, targetPackage, false, true)) {
                    failedModules.add(moduleName);
                    failedModulePackages.add(targetPackage);
                    failedModuleInternal.add(Boolean.TRUE);
                }
            }
        }
        
        if (failedModules.size() > 0) {
            // Build up an appropriate error message.
            StringBuilder errorMessage = new StringBuilder("The following Java binding classes are out of date.\r\n");
            for (int i = 0, n = failedModules.size(); i < n; ++i) {
                if (i > 0) {
                    errorMessage.append(", ");
                }
                
                String failedModuleTargetPackage = failedModulePackages.get(i);
                ModuleName failedModuleName = failedModules.get(i);
                boolean failedModuleIsInternal = failedModuleInternal.get(i).booleanValue();
                
                String fullyQualifiedClassName =
                    JavaBindingGenerator.getPackageName(failedModuleTargetPackage, failedModuleName) + "." +
                    JavaBindingGenerator.getClassName(failedModuleName, !failedModuleIsInternal);
                
                errorMessage.append(fullyQualifiedClassName);
            }
            errorMessage.append(".\r\n\r\n");
            
            errorMessage.append("The following commands can be run in ICE to update the binding classes:\r\n");
            for (int i = 0, n = failedModules.size(); i < n; ++i) {
                errorMessage.append(":javaBinding " + failedModules.get(i) + " " + failedModulePackages.get(i) + " " + (failedModuleInternal.get(i).booleanValue() ? "internal" : "") + "\r\n");
            }
            errorMessage.append("\r\nYou may need to force Eclipse to refresh after re-generating the binding classes.");
            errorMessage.append("\r\nYou may also need to update the ICE script used to build the binding classes for this project.");
            
            System.out.println(errorMessage.toString());
            fail(errorMessage.toString());
        }
    }
    
    public static ModuleName[] getModuleNamesInBaseWorkspace(final String baseWorkspaceName, final BasicCALServices sourceBasicCALServices) {
        WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider = 
            DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider(baseWorkspaceName);
        
        Status status = new Status("Reading workspace declarations");
        
        Set<ModuleName> baseModuleNamesSet = WorkspaceLoader.getStoredModuleNames(workspaceDeclarationProvider, sourceBasicCALServices.getCALWorkspace(), status);

        ModuleName[] baseModuleNames = baseModuleNamesSet.toArray(new ModuleName[baseModuleNamesSet.size()]);
        return baseModuleNames;
    }
}
