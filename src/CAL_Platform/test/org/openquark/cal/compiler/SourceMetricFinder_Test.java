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
 * SourceMetricFinder_Test.java
 * Creation date: (Jul 7, 2005)
 * By: Jawright
 */
package org.openquark.cal.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.SourceMetricFinder.LintWarning;
import org.openquark.cal.filter.AcceptAllQualifiedNamesFilter;
import org.openquark.cal.filter.RegExpBasedUnqualifiedNameFilter;
import org.openquark.cal.module.Cal.Collections.CAL_Array;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Core.CAL_String;
import org.openquark.cal.module.Cal.Utilities.CAL_StringNoCase;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.Status;
import org.openquark.util.Pair;


/**
 * A set of JUnit tests for the SourceMetricFinder class.
 * Creation date: (Jul 7, 2005)
 * @author Jawright
 */
public class SourceMetricFinder_Test extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;

    /** A cached reference to the workspace's metrics manager */
    private static SourceMetrics workspaceSourceMetrics;
    
    /** A cached copy of the SourceModel for the support module */
    private static SourceModel.ModuleDefn moduleDefn;         
    
    /** A cached copy of the ModuleTypeInfo for the support module */
    private static ModuleTypeInfo moduleTypeInfo;
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(SourceMetricFinder_Test.class);

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
        workspaceSourceMetrics = leccCALServices.getCALWorkspace().getSourceMetrics();
        String moduleSource = readModuleSource(leccCALServices.getCALWorkspace().getSourceDefinition(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support));
        moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(moduleSource);
        moduleTypeInfo = leccCALServices.getWorkspaceManager().getModuleTypeInfo(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support);
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        workspaceSourceMetrics = null;
        moduleDefn = null;
        moduleTypeInfo = null;
    }
    
    /**
     * Constructor for SourceMetricFinder_Test.
     * 
     * @param name
     *            the name of the test
     */
    public SourceMetricFinder_Test(String name) {
        super(name);
    }

    /**
     * Tests that the reference frequency metrics behave the way that we expect in the basic cases.
     */
    public void testReferenceFrequencyMetric() {
        assertEquals(0, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf0")));
        assertEquals(1, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf1")));
        assertEquals(2, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf2")));
        assertEquals(3, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf3")));
        assertEquals(2, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf4")));
    }

    /**
     * Test that we correctly handle calls embedded in operator calls
     */
    public void testReferenceFrequencyOperatorHandling() {
        assertEquals(3, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf5")));
    }
    
    /** Test that we correctly handle scoping issues from lambda expressions and local functions */
    public void testReferenceFrequencyLambdaAndLocalFunctionHandling() {
        assertEquals(4, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf6")));
    }
    
    /** Test that functions called using the backquoted-operator syntax are counted */
    public void testReferenceFrequencyBackquoteHandling() {
        assertEquals(1, workspaceSourceMetrics.getGemReferenceFrequency(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "leaf7")));
    }
    
    /** Test that regular expression filtering is working */
    public void testReferenceFrequencyRegexpFiltering() {
        Map<Pair<QualifiedName, QualifiedName>, Integer> unfilteredMap = SourceMetricFinder.computeReferenceFrequencies(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false);
        Map<Pair<QualifiedName, QualifiedName>, Integer> filteredMap = SourceMetricFinder.computeReferenceFrequencies(moduleDefn, moduleTypeInfo, new RegExpBasedUnqualifiedNameFilter(".*Examples", true), false);

        int unfilteredFrequency = 0;
        int filteredFrequency = 0;
        
        // Extract reference frequency for leaf8
        for (final Pair<QualifiedName, QualifiedName> key : unfilteredMap.keySet()) {
            String dependeeName = key.fst().getUnqualifiedName();
            if(dependeeName.equals("leaf8")) {
                Integer frequency = unfilteredMap.get(key);
                unfilteredFrequency += frequency.intValue();
            }
        }

        for (final Pair<QualifiedName, QualifiedName> key : filteredMap.keySet()) {
            String dependeeName = key.fst().getUnqualifiedName();
            if(dependeeName.equals("leaf8")) {
                Integer frequency = unfilteredMap.get(key);
                filteredFrequency += frequency.intValue();
            }
        }

        assertEquals(2, unfilteredFrequency);
        assertEquals(1, filteredFrequency);
    }
    
    /**
     * Check that the compositional frequency metric behaves basically as we expect. 
     */
    public void testCompositionalFrequencyMetric() {
        Map<Pair<QualifiedName, QualifiedName>, Integer> compositionalFrequencies = SourceMetricFinder.computeCompositionalFrequencies(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false);
        assertEquals(1, getCompositionalFrequency(compositionalFrequencies, "callerC1", "mangleValue"));
        assertEquals(2, getCompositionalFrequency(compositionalFrequencies, "mangleValue", "callerC1"));
        assertEquals(3, getCompositionalFrequency(compositionalFrequencies, "stompValue", "callerC1"));
    }
    
    /**  Check that we handle Prelude.apply in both its function and operator ($) forms */ 
    public void testCompositionFrequencyApplyHandling() {
        Map<Pair<QualifiedName, QualifiedName>, Integer> compositionalFrequencies = SourceMetricFinder.computeCompositionalFrequencies(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false);
        assertEquals(1, getCompositionalFrequency(compositionalFrequencies, "mangleValue", "callerC2"));
        assertEquals(1, getCompositionalFrequency(compositionalFrequencies, "callerC2", "mangleValue"));
        assertEquals(2, getCompositionalFrequency(compositionalFrequencies, "callerC2a", "mangleValue"));
    }
    
    /**  Check that we handle Prelude.compose in both its function and operator (#) forms */ 
    public void testCompositionFrequencyComposeHandling() {
        Map<Pair<QualifiedName, QualifiedName>, Integer> compositionalFrequencies = SourceMetricFinder.computeCompositionalFrequencies(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false);
        assertEquals(2, getCompositionalFrequency(compositionalFrequencies, "mangleValue", "callerC3"));
        assertEquals(2, getCompositionalFrequency(compositionalFrequencies, "callerC3", "mangleValue"));
        assertEquals(1, getCompositionalFrequency(compositionalFrequencies, "callerC3", "stompValue"));
    }
    
    /** Test that we deal properly with lambdas */
    public void testCompositionFrequencyLambdaHandling() {
        Map<Pair<QualifiedName, QualifiedName>, Integer> compositionalFrequencies = SourceMetricFinder.computeCompositionalFrequencies(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false);
        assertEquals(3, getCompositionalFrequency(compositionalFrequencies, "stompValue", "callerC4"));
        assertEquals(1, getCompositionalFrequency(compositionalFrequencies, "callerC4", "mangleValue"));
        assertEquals(0, getCompositionalFrequency(compositionalFrequencies, "mangleValue", "callerC4"));
    }

    /** Test regexp-filtering in compositional-frequency computation */
    public void testCompositionalFrequencyRegexpFiltering() {
        Map<Pair<QualifiedName, QualifiedName>, Integer> unfilteredFrequencies = SourceMetricFinder.computeCompositionalFrequencies(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false);
        Map<Pair<QualifiedName, QualifiedName>, Integer> filteredFrequencies = SourceMetricFinder.computeCompositionalFrequencies(moduleDefn, moduleTypeInfo, new RegExpBasedUnqualifiedNameFilter("(.*test.*)|(.*Examples.*)", true), false);

        assertEquals(1, getCompositionalFrequency(filteredFrequencies, "callerC5", "mangleValue"));
        assertEquals(3, getCompositionalFrequency(unfilteredFrequencies, "callerC5", "mangleValue"));
    }
    
    /** Test redundant-lambda detection */
    public void testLintWalkerRedundantLambdas() {
        List<LintWarning> warningList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, false, true, false, false, false);
        
        assertTrue(functionFlagged("l1", LintWarning.WarningType.REDUNDANT_LAMBDA, warningList));
        assertTrue(functionFlagged("l2", LintWarning.WarningType.REDUNDANT_LAMBDA, warningList));
        
        assertFalse(functionFlagged("l3", LintWarning.WarningType.REDUNDANT_LAMBDA, warningList));
        
        assertTrue(functionFlagged("l4", LintWarning.WarningType.REDUNDANT_LAMBDA, warningList));
        assertTrue(functionFlagged("l5", LintWarning.WarningType.REDUNDANT_LAMBDA, warningList));
        assertTrue(functionFlagged("l6", LintWarning.WarningType.REDUNDANT_LAMBDA, warningList));
        
        assertFalse(functionFlagged("l7", LintWarning.WarningType.REDUNDANT_LAMBDA, warningList));
    }
    
    /** Test unplinged-primitive-arg detection */
    public void testLintWalkerUnplingedPrimitveArgs() {
        List<LintWarning> warningList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, true, false, false, false, false);
        
        assertTrue(functionFlagged("fullyUnplingedPrimitives", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
        assertTrue(functionFlagged("partiallyUnplingedPrimitives1", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
        assertTrue(functionFlagged("partiallyUnplingedPrimitives2", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
        assertTrue(functionFlagged("partiallyUnplingedPrimitives3", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
        assertTrue(functionFlagged("partiallyUnplingedPrimitives4", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
        assertTrue(functionFlagged("partiallyUnplingedPrimitives5", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
        assertTrue(functionFlagged("partiallyUnplingedPrimitives6", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));

        assertFalse(functionFlagged("fullyUnplingedConstrained", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
        assertFalse(functionFlagged("fullyUnplingedAlgebraic", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, warningList));
    }
    
    /** Test unreferenced-private-function detection */
    public void testLintWalkerUnusedPrivates() {
        List<LintWarning> warningList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, false, false, true, false, false);
        
        assertFalse(functionFlagged("unreferencedPublicFunction", LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, warningList));
        assertFalse(functionFlagged("referenced1", LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, warningList));
        assertFalse(functionFlagged("referenced2", LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, warningList));
        assertFalse(functionFlagged("referenced3", LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, warningList));
        assertFalse(functionFlagged("referenced4", LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, warningList));
        
        assertTrue(functionFlagged("unreferencedPrivate1", LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, warningList));
        assertTrue(functionFlagged("unreferencedPrivate2", LintWarning.WarningType.UNUSED_PRIVATE_FUNCTION, warningList));
    }
    
    /** Test mismatched-plings-on-alias-functions detection */
    public void testLintWalkerMismatchedAliasPlings() {
        List<LintWarning> warningList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, false, false, false, true, false);
        
        assertFalse(functionFlagged("alias1", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias2", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias3", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias4", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias5", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias6", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias7", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias8", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias9", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias10", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("alias11", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        
        assertTrue(functionFlagged("badAlias1", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertTrue(functionFlagged("badAlias2", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertTrue(functionFlagged("badAlias3", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertTrue(functionFlagged("badAlias4", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertTrue(functionFlagged("badAlias5", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertTrue(functionFlagged("badAlias6", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertTrue(functionFlagged("badAlias7", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));

        assertFalse(functionFlagged("partialAlias1", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("partialAlias2", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("partialAlias3", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("partialAlias4", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("partialAlias5", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));

        assertFalse(functionFlagged("notAlias1", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("notAlias2", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("notAlias3", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("notAlias4", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("notAlias5", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
        assertFalse(functionFlagged("notAlias6", LintWarning.WarningType.MISMATCHED_ALIAS_PLINGS, warningList));
    }
    
    /** Test the detection of unreferenced let variables */
    public void testUnreferencedLetVariableDetection() {
        List<LintWarning> warningList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, false, false, false, false, true);
        
        assertTrue(functionFlagged("unusedLetVars1", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars2", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars3", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars4", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars5", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars6", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars7", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars8", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars9", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars10", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars11", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertTrue(functionFlagged("unusedLetVars12", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        
        assertFalse(functionFlagged("usesLetVar1", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertFalse(functionFlagged("usesLetVar2", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertFalse(functionFlagged("usesLetVar3", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertFalse(functionFlagged("usesLetVar4", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
        assertFalse(functionFlagged("usesLetVar5", LintWarning.WarningType.UNREFERENCED_LET_VARIABLE, warningList));
    }
    
    /** Test that SourcePositions are correctly recorded for all warning types */
    public void testSourcePositions() {
        List<LintWarning> warningList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, true, true, false, false, false);
        LintWarning[] veryLintyWarnings = new LintWarning[3];
        
        int i = 0;
        for(Iterator<LintWarning> it = warningList.iterator(); it.hasNext() && i < 3;) {
            LintWarning warning = it.next();
            
            if(warning.getFunctionName().getUnqualifiedName().equals("veryLintyFunction")) {
                veryLintyWarnings[i] = warning;
                i++;
            }
        }
        
        assertEquals(3, i);
        assertTrue(veryLintyWarnings[0].getSourcePosition() != null);
        assertTrue(veryLintyWarnings[1].getSourcePosition() != null);
        assertTrue(veryLintyWarnings[2].getSourcePosition() != null);
        
        // The two unplinged-primitive warnings should point at the same line
        assertEquals(veryLintyWarnings[0].getSourcePosition().getLine(), veryLintyWarnings[1].getSourcePosition().getLine());
        
        // The second argument is 14 characters from the first
        assertEquals(veryLintyWarnings[0].getSourcePosition().getColumn() + 14, veryLintyWarnings[1].getSourcePosition().getColumn());
        
        // The redundant-lambda warning should occur on the next line from the parameter warnings
        assertEquals(veryLintyWarnings[0].getSourcePosition().getLine() + 1, veryLintyWarnings[2].getSourcePosition().getLine());
        
        // The lambda begins at the same column as the first parameter
        assertEquals(veryLintyWarnings[0].getSourcePosition().getColumn(), veryLintyWarnings[2].getSourcePosition().getColumn());
    }
    
    /** Test that warning filtering is working */
    public void testWarningTypeFilter() {
        List<LintWarning> lambdasOnlyList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, false, true, false, false, false);
        List<LintWarning> plingsOnlyList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, true, false, false, false, false);

        assertTrue(lambdasOnlyList.size() > 0);

        for (final LintWarning warning : lambdasOnlyList) {
            assertTrue(warning.getWarningType() == LintWarning.WarningType.REDUNDANT_LAMBDA);
        }

        assertTrue(plingsOnlyList.size() > 0);
        
        for (final LintWarning warning : plingsOnlyList) {
            assertTrue(warning.getWarningType() == LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG);
        }
    }
    
    /** Test that function regular expression filtering is working */
    public void testLintWalkerRegexpFiltering() {
        List<LintWarning> unfilteredWarningsList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new AcceptAllQualifiedNamesFilter(), false, true, false, false, false, false);
        List<LintWarning> filteredWarningsList = SourceMetricFinder.computeLintWarnings(moduleDefn, moduleTypeInfo, new RegExpBasedUnqualifiedNameFilter("very.*", true), false, true, false, false, false, false);

        assertTrue(functionFlagged("veryLintyFunction", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, unfilteredWarningsList));
        assertFalse(functionFlagged("veryLintyFunction", LintWarning.WarningType.UNPLINGED_PRIMITIVE_ARG, filteredWarningsList));
    }
    
    /** Test the basic operation of search */
    public void testSearchWalker() {
        QualifiedName sillyFunctionName = QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "sillyFunction");
        List<SearchResult.Precise> fcnDefnList = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.DEFINITION, Collections.singletonList(sillyFunctionName));
        
        // We will make various assertions about source positions relative to the position of sillyFunction's definition
        assertEquals(1, fcnDefnList.size());
        SearchResult.Precise fcnDefnResult = fcnDefnList.get(0);
        assertEquals(1, fcnDefnResult.getSourcePosition().getColumn());
        int baseLine = fcnDefnResult.getSourcePosition().getLine();
        
        List<SearchResult.Precise> refList = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.REFERENCES, Collections.singletonList(sillyFunctionName));
        assertEquals(3, refList.size());
        
        assertEquals(baseLine + 4,  line(refList.get(0)));
        assertEquals(6,             column(refList.get(0)));
        assertEquals(baseLine + 4,  line(refList.get(1)));
        assertEquals(28,            column(refList.get(1)));
        assertEquals(baseLine + 14, line(refList.get(2)));
        assertEquals(29,            column(refList.get(2)));
        
        QualifiedName equalsName = CAL_Prelude.Functions.equals;
        List<SearchResult.Precise> equalsRefList = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.REFERENCES, Collections.singletonList(equalsName));

        assertEquals(2, equalsRefList.size());
        assertEquals(baseLine + 18, line(equalsRefList.get(0)));
        assertEquals(16,            column(equalsRefList.get(0)));
        assertEquals(baseLine + 19, line(equalsRefList.get(1)));
        assertEquals(19, column(equalsRefList.get(1)));
        
        QualifiedName sillyDataTypeName = QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "SillyDataType");
        List<SearchResult.Precise> sillyDataTypeDefnList = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.DEFINITION, Collections.singletonList(sillyDataTypeName));

        assertEquals(2, sillyDataTypeDefnList.size());
        assertEquals(baseLine + 21, line(sillyDataTypeDefnList.get(0)));
        assertEquals(14,            column(sillyDataTypeDefnList.get(0)));
        assertEquals(baseLine + 22, line(sillyDataTypeDefnList.get(1)));
        assertEquals(5,             column(sillyDataTypeDefnList.get(1)));
        
        QualifiedName identifiableName = QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "Identifiable");
        List<SearchResult.Precise> identifiableDefnList = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.DEFINITION, Collections.singletonList(identifiableName));

        assertEquals(1, identifiableDefnList.size());
        assertEquals(baseLine + 26, line(identifiableDefnList.get(0)));
        assertEquals(7,             column(identifiableDefnList.get(0)));
        
        List<SearchResult.Precise> identifiableInstances = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.INSTANCES, Collections.singletonList(identifiableName));
        List<SearchResult.Precise> sillyDataTypeClasses = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.CLASSES, Collections.singletonList(sillyDataTypeName));
        assertEquals(1, identifiableInstances.size());
        assertEquals(2, sillyDataTypeClasses.size());
        assertEquals(baseLine + 24, line(sillyDataTypeClasses.get(0)));
        assertEquals(14,            column(sillyDataTypeClasses.get(0)));
        
        // only instance of Identifiable is the second class to which SillyDataType belongs
        assertEquals(line(identifiableInstances.get(0)), line(sillyDataTypeClasses.get(1)));
        assertEquals(column(identifiableInstances.get(0)), 10);
        
        QualifiedName sillyDataconsName = QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "Silly");
        List<SearchResult.Precise> sillyDataconsDefnList = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.DEFINITION, Collections.singletonList(sillyDataconsName));
        assertEquals(1, sillyDataconsDefnList.size());
        assertEquals(baseLine + 23, line(sillyDataconsDefnList.get(0)));
        assertEquals(5,             column(sillyDataconsDefnList.get(0)));
        
        List<SearchResult.Precise> sillyDataconsRefs = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.REFERENCES, Collections.singletonList(sillyDataconsName));
        assertEquals(3, sillyDataconsRefs.size());
        assertEquals(baseLine + 35, line(sillyDataconsRefs.get(0)));
        assertEquals(9,             column(sillyDataconsRefs.get(0)));
        assertEquals(baseLine + 39, line(sillyDataconsRefs.get(1)));
        assertEquals(6,             column(sillyDataconsRefs.get(1)));
        assertEquals(baseLine + 39, line(sillyDataconsRefs.get(2)));
        assertEquals(40,            column(sillyDataconsRefs.get(2)));

        List<QualifiedName> toListNames = new ArrayList<QualifiedName>(2);
        toListNames.add(CAL_Array.Functions.toList);
        toListNames.add(CAL_String.Functions.toList);
        List<SearchResult.Precise> toListRefs = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.REFERENCES, toListNames);
        assertEquals(2, toListRefs.size());
        assertEquals(baseLine + 56, line(toListRefs.get(0)));
        assertEquals(15,            column(toListRefs.get(0)));
        assertEquals(baseLine + 57, line(toListRefs.get(1)));
        assertEquals(16,            column(toListRefs.get(1)));
        
        List<SearchResult.Precise> idReferences = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.REFERENCES, Collections.singletonList(CAL_Prelude.Functions.id));
        assertEquals(1, idReferences.size());
        assertEquals(baseLine + 31, line(idReferences.get(0)));
        assertEquals(24,            column(idReferences.get(0)));

        // Test that we deal correctly with identical names in different namespaces
        // (eg, typeconses and dataconses with the same name).
        List<SearchResult.Precise> crossConsResults = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.DEFINITION, Collections.singletonList(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "ConsName1")));
        
        assertTrue(crossConsResults.size() == 2);
        assertEquals(baseLine + 60, line(crossConsResults.get(0)));
        assertEquals(14, column(crossConsResults.get(0)));
        assertEquals(baseLine + 65, line(crossConsResults.get(1)));
        assertEquals(5, column(crossConsResults.get(1)));
        
        // Test special-notation type constructor support
        List<SearchResult.Precise> listAndFcnInstances = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.CLASSES, Arrays.asList(new QualifiedName[] {CAL_Prelude.TypeConstructors.Function, CAL_Prelude.TypeConstructors.List}));
        List<SearchResult.Precise> constantableInstances = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.INSTANCES, Collections.singletonList(QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, "Constantable")));
        
        // The only List and Function instances in SourceMetricFinder_Test_Support are of Constantable,
        // which has no other instances.
        assertEquals(2, listAndFcnInstances.size());
        assertEquals(2, constantableInstances.size());
        for(int i = 0; i < listAndFcnInstances.size(); i++) {
            assertEquals(line(listAndFcnInstances.get(i)), line(constantableInstances.get(i)));
            // We don't check columns for equality, because in one case it will be point
            // to the type whereas in the other case it will point to the class.
        }
        
        // Test special-notation list data constructor support
        List<SearchResult.Precise> listReferences = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.REFERENCES, Arrays.asList(new QualifiedName[] {CAL_Prelude.DataConstructors.Cons, CAL_Prelude.DataConstructors.Nil}));
        assertEquals(7, listReferences.size());
        // The first four references are part of the linter test and don't have
        // guaranteed positions, so we skip them
        assertEquals(baseLine + 71, line(listReferences.get(4)));
        assertEquals(5,             column(listReferences.get(4)));
        assertEquals(baseLine + 72, line(listReferences.get(5)));
        assertEquals(7,             column(listReferences.get(5)));
        assertEquals(baseLine + 77, line(listReferences.get(6)));
        assertEquals(9,             column(listReferences.get(6)));
    
        // Test searching of using clauses
        List<SearchResult.Precise> fromStringOccurences = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.ALL, Collections.singletonList(CAL_StringNoCase.Functions.fromString));
        assertEquals(1, fromStringOccurences.size());
        assertEquals(54, line(fromStringOccurences.get(0)));
        assertEquals(16, column(fromStringOccurences.get(0)));
        
        // Test searching of signatures
        List<SearchResult.Precise> objectAndStringNoCaseOccurrences = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.ALL, Arrays.asList(new QualifiedName[] {CAL_Prelude.TypeConstructors.JObject, CAL_StringNoCase.TypeConstructors.StringNoCase}));
        assertEquals(4, objectAndStringNoCaseOccurrences.size());
        assertEquals(baseLine + 82, line(objectAndStringNoCaseOccurrences.get(0)));
        assertEquals(20,            column(objectAndStringNoCaseOccurrences.get(0)));
        assertEquals(baseLine + 85, line(objectAndStringNoCaseOccurrences.get(1)));
        assertEquals(27,            column(objectAndStringNoCaseOccurrences.get(1)));
        assertEquals(baseLine + 86, line(objectAndStringNoCaseOccurrences.get(2)));
        assertEquals(42,            column(objectAndStringNoCaseOccurrences.get(2)));
        assertEquals(baseLine + 91, line(objectAndStringNoCaseOccurrences.get(3)));
        assertEquals(28,            column(objectAndStringNoCaseOccurrences.get(3)));
    }
    
    /** 
     * Test that search results always come back in source order.  
     * SourceMetricFinder_Test_Support.outOfOrderBinaryExpression and 
     * SourceMetricFinder_Test_Support.outOfOrderBackquotedExpression have been explicitly
     * constructed to make this test fail if we are traversing the source tree in pre-order 
     * or post-order.  
     */
    public void testSearchResultsOrder() {
        List<SearchResult.Precise> addResults = SourceMetricFinder.performSearch(moduleDefn, moduleTypeInfo, SourceMetricFinder.SearchType.REFERENCES, Collections.singletonList(CAL_Prelude.Functions.add));
        assertTrue(addResults.size() > 0);
        
        SourcePosition previousPosition = null;
        for (final SearchResult.Precise preciseResult : addResults) {
            SourcePosition currentPosition = preciseResult.getSourcePosition();
            if(previousPosition != null) {
                assertTrue(SourcePosition.compareByPosition.compare(previousPosition, currentPosition) < 0);
            }
            previousPosition = currentPosition;
        }
        
        List<SearchResult> rootResults = workspaceSourceMetrics.findDefinition("*Call*", new MessageLogger());
        assertTrue(rootResults.size() > 0);
        
        // Results from a workspace-level find should be both ordered and grouped by module
        Set<String> previousModules = new HashSet<String>();
        previousPosition = null;
        for (final SearchResult searchResult : rootResults) {
            SourcePosition currentPosition = ((SearchResult.Precise)searchResult).getSourcePosition();
            if(previousPosition != null) {
                if(previousPosition.getSourceName().equals(currentPosition.getSourceName())) {
                    assertTrue(SourcePosition.compareByPosition.compare(previousPosition, currentPosition) < 0);
                } else {
                    assertFalse(previousModules.contains(currentPosition.getSourceName()));
                    previousModules.add(currentPosition.getSourceName());
                }
                
            }
            previousPosition = currentPosition;
        }
    }
    
    /**
     * Test that search is case-insensitive.
     */
    public void testSearchCaseInsensitivity() {

        MessageLogger messageLogger = new MessageLogger();
        
        List<SearchResult> leafDefns = workspaceSourceMetrics.findDefinition("sourcemetricfinder_test_support.lEaF*", messageLogger);
        assertTrue(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.INFO) <= 0);
        assertEquals(9, leafDefns.size());
        int i = 0;
        for(Iterator<SearchResult> it = leafDefns.iterator(); it.hasNext(); i++) {
            SearchResult searchResult = it.next();
            String expectedUnqualifiedName = "leaf" + i;
            final QualifiedName qn = (QualifiedName) searchResult.getName();
            assertTrue(qn.getUnqualifiedName().equals(expectedUnqualifiedName));
            assertTrue(qn.getModuleName().equals(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support));
        }
        
        List<SearchResult> leafReferences = workspaceSourceMetrics.findReferences("sourceMETRICfinder_test_SUPPORT.LeAf?", messageLogger);
        assertTrue(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.INFO) <= 0);
        assertEquals(18, leafReferences.size());
        int prevNumber = 1;
        for (final SearchResult searchResult : leafReferences) {
            final QualifiedName qn = (QualifiedName) searchResult.getName();
            assertTrue(qn.getModuleName().equals(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support));
            int currentNumber = Integer.parseInt("" + qn.getUnqualifiedName().charAt(4));
            assertTrue(currentNumber == prevNumber || currentNumber == prevNumber + 1);
            prevNumber = currentNumber;
        }
    }
    
    /**
     * Test that the search for a foriegn data type returns the correct results.
     */
    public void testSearchForForeignDateType() {

        MessageLogger messageLogger = new MessageLogger();
        
        List<SearchResult> leafDefns = workspaceSourceMetrics.findDefinition("sourcemetricfinder_test_support.JFile", messageLogger);
        assertTrue(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.INFO) <= 0);
        assertEquals(1, leafDefns.size());
        int i = 0;
        for(Iterator<SearchResult> it = leafDefns.iterator(); it.hasNext(); i++) {
            SearchResult searchResult = it.next();
            String expectedUnqualifiedName = "JFile";
            final QualifiedName qn = (QualifiedName) searchResult.getName();
            assertTrue(qn.getUnqualifiedName().equals(expectedUnqualifiedName));
            assertTrue(qn.getModuleName().equals(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support));
        }
    }
    
    private static int line(SearchResult.Precise searchResult) {
        return searchResult.getSourcePosition().getLine();
    }
    
    private static int column(SearchResult.Precise searchResult) {
        return searchResult.getSourcePosition().getColumn();
    }
    
    /**
     * Helper function; scans a list of warnings for a warning of a given type in a given function.  
     * @param functionName String Function name to look for
     * @param warningType LintWarning.WarningType Type of warning to look for
     * @param warningList List (LintWarning) 
     * @return true when warningList contains a warning of type warningType for a function named functionName
     */
    private static boolean functionFlagged(String functionName, LintWarning.WarningType warningType, List<LintWarning> warningList) {
        for (final LintWarning warning : warningList) {
            if(warning.getWarningType() == warningType && warning.getFunctionName().getUnqualifiedName().equals(functionName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Helper function for looking up the compositional frequency of two private functions in the frequency map.
     * @param compositionalFrequencyMap Map to look up the frequency from
     * @param consumerUnqualifiedName Unqualified name of a consumer function in the SourceMetricFinder_Test_Support module
     * @param producerUnqualifiedName Unqualified name of a producer function in the SourceMetricFinder_Test_Support module
     * @return If the specified (consumer, producer) pair is a key in the specified map, then returns the associated compositional frequency.
     *          otherwise, returns 0. 
     */
    private static int getCompositionalFrequency(Map<Pair<QualifiedName, QualifiedName>, Integer> compositionalFrequencyMap, String consumerUnqualifiedName, String producerUnqualifiedName) {
        QualifiedName consumerKey = QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, consumerUnqualifiedName);
        QualifiedName producerKey = QualifiedName.make(CALPlatformTestModuleNames.SourceMetricFinder_Test_Support, producerUnqualifiedName);
        Integer value = compositionalFrequencyMap.get(new Pair<QualifiedName, QualifiedName>(consumerKey, producerKey));
        if(value != null) {
            return value.intValue();
        } else {
            return 0;
        }
    }
    
    /**
     * @param moduleSourceDefn The source definition to read the source text for
     * @return The source text of a module as a single String
     */
    private static String readModuleSource(ModuleSourceDefinition moduleSourceDefn) {

        Reader reader = moduleSourceDefn.getSourceReader(new Status("reading module source"));
        if (reader == null) {
            System.err.println("Failed to read module " + moduleSourceDefn.getModuleName());
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        StringBuilder stringBuf = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuf.append(line).append('\n');
            }
        } catch (IOException e) {
            System.err.println("Failed to read module " + moduleSourceDefn.getModuleName());
            return null;
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
            }
        }
        
        return stringBuf.toString(); 
    }
}
