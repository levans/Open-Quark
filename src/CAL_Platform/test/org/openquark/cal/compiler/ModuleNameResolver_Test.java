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
 * ModuleNameResolver_Test.java
 * Creation date: Nov 24, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.util.Arrays;
import java.util.HashSet;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;


/**
 * A set of JUnit test cases for the module name resolver.
 *
 * @author Joseph Wong
 */
public class ModuleNameResolver_Test extends TestCase {

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
     * @return a test suite containing all the test cases.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(ModuleNameResolver_Test.class);

        return new TestSetup(suite) {

            protected void setUp() {
                oneTimeSetUp();
                
            }
    
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
     * Tests the {@link ModuleNameResolver#resolve} method of a standalone resolver with the standard example.
     * 
     * <p>
     * Suppose we have a module:
     * <pre>
     * module W.X.Y.Z;
     * 
     * import Y.Z;
     * import Z;
     * import A.B.C.D.E;
     * import P.C.D.E;
     * import D.E;
     * </pre>
     * 
     * <p>
     * Here are the set of name resolutions:
     * <table>
     * <tr><td> <b>Module Name</b> <td> <b>Resolves to...</b>
     * <tr><td> Z                  <td> Z
     * <tr><td> Y.Z                <td> Y.Z
     * <tr><td> X.Y.Z              <td> W.X.Y.Z (the current module)
     * <tr><td> W.X.Y.Z            <td> W.X.Y.Z
     * <tr><td> E                  <td> Ambiguous (A.B.C.D.E, P.C.D.E, D.E)
     * <tr><td> D.E                <td> D.E
     * <tr><td> C.D.E              <td> Ambiguous (A.B.C.D.E, P.C.D.E)
     * <tr><td> B.C.D.E            <td> A.B.C.D.E
     * <tr><td> P.C.D.E            <td> P.C.D.E
     * <tr><td> A.B.C.D.E          <td> A.B.C.D.E
     * </table>
     */
    public void testStandAloneResolver_resolve() {
        
        ModuleName moduleName = ModuleName.make("W.X.Y.Z");
        ModuleName[] imports = new ModuleName[] { ModuleName.make("Y.Z"), ModuleName.make("Z"), ModuleName.make("A.B.C.D.E"), ModuleName.make("P.C.D.E"), ModuleName.make("D.E") };
        
        ModuleNameResolver resolver = ModuleNameResolver.make(moduleName, new HashSet<ModuleName>(Arrays.asList(imports)));
        
        ModuleNameResolver.ResolutionResult resolution;
        
        resolution = resolver.resolve(ModuleName.make("Z"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Z"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("Y.Z"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Y.Z"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("X.Y.Z"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("W.X.Y.Z"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("W.X.Y.Z"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("W.X.Y.Z"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("E"));
        assertTrue(resolution.isAmbiguous());
        assertEquals(ModuleName.make("E"), resolution.getResolvedModuleName());
        assertEquals(
            new HashSet<ModuleName>(Arrays.asList(new ModuleName[] {ModuleName.make("A.B.C.D.E"), ModuleName.make("P.C.D.E"), ModuleName.make("D.E")})),
            new HashSet<ModuleName>(Arrays.asList(resolution.getPotentialMatches())));
        
        resolution = resolver.resolve(ModuleName.make("D.E"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("D.E"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("C.D.E"));
        assertTrue(resolution.isAmbiguous());
        assertEquals(ModuleName.make("C.D.E"), resolution.getResolvedModuleName());
        assertEquals(
            new HashSet<ModuleName>(Arrays.asList(new ModuleName[] {ModuleName.make("A.B.C.D.E"), ModuleName.make("P.C.D.E")})),
            new HashSet<ModuleName>(Arrays.asList(resolution.getPotentialMatches())));
        
        resolution = resolver.resolve(ModuleName.make("B.C.D.E"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("A.B.C.D.E"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("P.C.D.E"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("P.C.D.E"), resolution.getResolvedModuleName());

        resolution = resolver.resolve(ModuleName.make("A.B.C.D.E"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("A.B.C.D.E"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("Q"));
        assertTrue(resolution.isUnknown());
        assertEquals(ModuleName.make("Q"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("P.Q"));
        assertTrue(resolution.isUnknown());
        assertEquals(ModuleName.make("P.Q"), resolution.getResolvedModuleName());
    }
    
    /**
     * Tests the {@link ModuleNameResolver#getMinimallyQualifiedModuleName} method of a standalone resolver with the standard example.
     * 
     * <p>
     * Suppose we have a module:
     * <pre>
     * module W.X.Y.Z;
     * 
     * import Y.Z;
     * import Z;
     * import A.B.C.D.E;
     * import P.C.D.E;
     * import D.E;
     * </pre>
     * 
     * <p>
     * Here are the minimally qualified names:
     * <table>
     * <tr><td> <b>Module Name</b> <td> <b>Minimally qualified form...</b>
     * <tr><td> Z                  <td> Z
     * <tr><td> Y.Z                <td> Y.Z
     * <tr><td> X.Y.Z              <td> X.Y.Z (the current module)
     * <tr><td> W.X.Y.Z            <td> X.Y.Z
     * <tr><td> E                  <td> E (Ambiguous)
     * <tr><td> D.E                <td> D.E
     * <tr><td> C.D.E              <td> C.D.E (Ambiguous)
     * <tr><td> B.C.D.E            <td> B.C.D.E
     * <tr><td> P.C.D.E            <td> P.C.D.E
     * <tr><td> A.B.C.D.E          <td> B.C.D.E
     * </table>
     */
    public void testStandAloneResolver_getMinimallyQualifiedModuleName() {
        
        ModuleName moduleName = ModuleName.make("W.X.Y.Z");
        ModuleName[] imports = new ModuleName[] { ModuleName.make("Y.Z"), ModuleName.make("Z"), ModuleName.make("A.B.C.D.E"), ModuleName.make("P.C.D.E"), ModuleName.make("D.E") };
        
        ModuleNameResolver resolver = ModuleNameResolver.make(moduleName, new HashSet<ModuleName>(Arrays.asList(imports)));
        
        assertEquals(ModuleName.make("Z"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Z")));
        assertEquals(ModuleName.make("Y.Z"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Y.Z")));
        assertEquals(ModuleName.make("X.Y.Z"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("X.Y.Z")));
        assertEquals(ModuleName.make("X.Y.Z"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("W.X.Y.Z")));
        assertEquals(ModuleName.make("E"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("E"))); // ambiguous
        assertEquals(ModuleName.make("D.E"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("D.E")));
        assertEquals(ModuleName.make("C.D.E"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("C.D.E"))); // ambiguous
        assertEquals(ModuleName.make("B.C.D.E"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("B.C.D.E")));
        assertEquals(ModuleName.make("P.C.D.E"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("P.C.D.E")));
        assertEquals(ModuleName.make("B.C.D.E"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("A.B.C.D.E")));
        
        assertEquals(ModuleName.make("Q"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Q"))); // unknown
        assertEquals(ModuleName.make("P.Q"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("P.Q"))); // unknown
    }
    
    /**
     * Tests the {@link ModuleNameResolver#makeRenameMapping} method of a standalone resolver with the example:
     * <p>
     * If A.B is renamed to C.D.E.F, and the original module X imports A.B, X.D.E.F and E.F, we build the mapping:
     * <p>
     * E.F -> E.F
     * <br>
     * D.E.F -> X.D.E.F
     * <p>
     * The handling of A.B -> C.D.E.F and B -> C.D.E.F is to be taken care of by the renamer itself
     * (because that should simply be part of its resolution code)
     */ 
    public void testStandAloneResolver_makeRenameMapping() {

        ModuleName moduleName = ModuleName.make("X");
        ModuleName[] imports = new ModuleName[] { ModuleName.make("A.B"), ModuleName.make("X.D.E.F"), ModuleName.make("E.F") };
        
        ModuleNameResolver resolver = ModuleNameResolver.make(moduleName, new HashSet<ModuleName>(Arrays.asList(imports)));

        ModuleNameResolver.RenameMapping renameMapping = resolver.makeRenameMapping(ModuleName.make("A.B"), ModuleName.make("C.D.E.F"));
        
        assertEquals(ModuleName.make("E.F"), renameMapping.getNewNameForModule(ModuleName.make("E.F")));
        assertEquals(ModuleName.make("X.D.E.F"), renameMapping.getNewNameForModule(ModuleName.make("D.E.F")));
    }
    
    /**
     * Tests the {@link ModuleNameResolver#willAdditionalModuleImportProduceConflict} method of a standalone resolver with the standard example.
     * 
     * <p>
     * Suppose we have a module:
     * <pre>
     * module W.X.Y.Z;
     * 
     * import Y.Z;
     * import Z;
     * import A.B.C.D.E;
     * import P.C.D.E;
     * import D.E;
     * </pre>
     */ 
    public void testStandAloneResolver_willAdditionalModuleImportProduceConflict() {
        
        ModuleName moduleName = ModuleName.make("W.X.Y.Z");
        ModuleName[] imports = new ModuleName[] { ModuleName.make("Y.Z"), ModuleName.make("Z"), ModuleName.make("A.B.C.D.E"), ModuleName.make("P.C.D.E"), ModuleName.make("D.E") };
        
        ModuleNameResolver resolver = ModuleNameResolver.make(moduleName, new HashSet<ModuleName>(Arrays.asList(imports)));

        assertTrue(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("X.Y.Z"))); // X.Y.Z originally resolved to W.X.Y.Z
        assertTrue(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("B.C.D.E"))); // B.C.D.E originally resolved to A.B.C.D.E
        
        assertTrue(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.X.Y.Z"))); // introduces ambiguity at X.Y.Z
        assertTrue(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.W.X.Y.Z"))); // introduces ambiguity at X.Y.Z
        assertTrue(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.B.C.D.E"))); // introduces ambiguity at B.C.D.E
        assertTrue(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.A.B.C.D.E"))); // introduces ambiguity at B.C.D.E
        
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.E"))); // E already ambiguous
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.R.E"))); // E already ambiguous
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.C.D.E"))); // C.D.E already ambiguous
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.R.C.D.E"))); // C.D.E already ambiguous
        
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.Z")));
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.Y.Z")));
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.P.C.D.E")));
        assertFalse(resolver.willAdditionalModuleImportProduceConflict(ModuleName.make("Q.D.E")));
    }
    
    /**
     * Tests the {@link ModuleNameResolver#resolve} method of the resolver for the Alpha.Beta module.
     */
    public void testAlphaBetaResolver_resolve() {
        
        ModuleTypeInfo moduleTypeInfo = leccCALServices.getWorkspaceManager().getModuleTypeInfo(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha.Beta"));
        
        ModuleNameResolver resolver = moduleTypeInfo.getModuleNameResolver();
        
        ModuleNameResolver.ResolutionResult resolution;
        
        resolution = resolver.resolve(ModuleName.make("Alpha"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("Beta"));
        assertTrue(resolution.isAmbiguous());
        assertEquals(ModuleName.make("Beta"), resolution.getResolvedModuleName());
        assertEquals(
            new HashSet<ModuleName>(Arrays.asList(new ModuleName[] {ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha.Beta"), ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Beta")})),
            new HashSet<ModuleName>(Arrays.asList(resolution.getPotentialMatches())));
        
        resolution = resolver.resolve(ModuleName.make("Test.Alpha"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("HierarchicalModuleName.Test.Alpha"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("Test.Beta"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Beta"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("HierarchicalModuleName.Test.Beta"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Beta"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("Alpha.Beta"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha.Beta"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("Test.Alpha.Beta"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha.Beta"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("HierarchicalModuleName.Test.Alpha.Beta"));
        assertTrue(resolution.isKnownUnambiguous());
        assertEquals(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha.Beta"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("Q"));
        assertTrue(resolution.isUnknown());
        assertEquals(ModuleName.make("Q"), resolution.getResolvedModuleName());
        
        resolution = resolver.resolve(ModuleName.make("P.Q"));
        assertTrue(resolution.isUnknown());
        assertEquals(ModuleName.make("P.Q"), resolution.getResolvedModuleName());
    }
    
    /**
     * Tests the {@link ModuleNameResolver#getMinimallyQualifiedModuleName} method of the resolver for the Alpha.Beta module.
     */
    public void testAlphaBetaResolver_getMinimallyQualifiedModuleName() {
        
        ModuleTypeInfo moduleTypeInfo = leccCALServices.getWorkspaceManager().getModuleTypeInfo(ModuleName.make("Cal.Test.General.HierarchicalModuleName.Test.Alpha.Beta"));
        
        ModuleNameResolver resolver = moduleTypeInfo.getModuleNameResolver();
        
        assertEquals(ModuleName.make("Alpha"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Alpha")));
        assertEquals(ModuleName.make("Beta"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Beta")));
        assertEquals(ModuleName.make("Test.Beta"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Test.Beta")));
        assertEquals(ModuleName.make("Alpha.Beta"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Alpha.Beta")));
        
        assertEquals(ModuleName.make("Alpha"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("HierarchicalModuleName.Test.Alpha")));
        assertEquals(ModuleName.make("Test.Beta"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("HierarchicalModuleName.Test.Beta")));
        assertEquals(ModuleName.make("Alpha.Beta"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("HierarchicalModuleName.Test.Alpha.Beta")));
        
        assertEquals(ModuleName.make("Q"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("Q"))); // unknown
        assertEquals(ModuleName.make("P.Q"), resolver.getMinimallyQualifiedModuleName(ModuleName.make("P.Q"))); // unknown
    }    
}
