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
 * RTSupercombinator_Test.java
 * Created: Jun 7, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.internal.machine.lecc;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.functions.NRecordFromJListPrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordFromJMapPrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordToJListPrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordToJRecordValuePrimitive;
import org.openquark.cal.internal.machine.g.functions.NShowRecord;
import org.openquark.cal.internal.module.Cal.Utilities.CAL_QuickCheck_internal;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.functions.RTArbitraryRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTCoArbitraryRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTCompareRecord;
import org.openquark.cal.internal.runtime.lecc.functions.RTEqualsRecord;
import org.openquark.cal.internal.runtime.lecc.functions.RTNotEqualsRecord;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_QuickCheck;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;

/**
 * A set of JUnit test cases that tests the {@link RTSupercombinator} class and some of its subclasses.
 *
 * @author Joseph Wong
 */
public class RTSupercombinator_Test extends TestCase {

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

        TestSuite suite = new TestSuite(RTSupercombinator_Test.class);

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
     * Tests the continued validity of the hand-written RTSupercombinator subclasses by validating that certain
     * type classes have only one class method and no superclasses.
     * 
     * See the corresponding G-machine tests done in:
     * <ul>
     * <li>{@link NRecordFromJListPrimitive#checkImplementation}
     * <li>{@link NRecordFromJMapPrimitive#checkInputRecordImplementation}
     * <li>{@link NRecordToJListPrimitive#checkImplementation}
     * <li>{@link NRecordToJRecordValuePrimitive#checkImplementation}
     * <li>{@link NShowRecord#checkShowRecordImplementation}
     * </ul>
     */
    public void testIsSingleMethodRootClass() {
        assertTrue(isSingleMethodRootClass(CAL_Prelude.TypeClasses.Inputable));
        assertTrue(isSingleMethodRootClass(CAL_Prelude.TypeClasses.Outputable));
        assertTrue(isSingleMethodRootClass(CAL_Debug.TypeClasses.Show));
    }
    
    /**
     * Tests the continued validity of the hand-written RTSupercombinator subclasses by validating that certain
     * class methods have particular indices within the corresponding run-time dictionaries.
     * 
     * See the supercombinators:
     * <ul>
     * <li>{@link RTArbitraryRecordPrimitive}
     * <li>{@link RTCoArbitraryRecordPrimitive}
     * <li>{@link RTCompareRecord}
     * <li>{@link RTNotEqualsRecord}
     * <li>{@link RTEqualsRecord}
     * </ul>
     */
    public void testClassMethodDictionaryIndex() {
        assertEquals(0, classMethodDictionaryIndex(CAL_Prelude.Functions.equals));
        assertEquals(1, classMethodDictionaryIndex(CAL_Prelude.Functions.notEquals));
        assertEquals(5, classMethodDictionaryIndex(CAL_Prelude.Functions.compare));
        assertEquals(1, classMethodDictionaryIndex(CAL_QuickCheck.Functions.coarbitrary));
        assertEquals(2, classMethodDictionaryIndex(CAL_QuickCheck_internal.Functions.generateInstance));
    }

    /**   
     * A helper function used to ensure the continued validity of the hand-written RTSupercombinator subclasses.  
     * @param typeClassName
     * @return true if the typeClassName is a type class with only a single class method and
     *   no superclasses.
     */
    private static boolean isSingleMethodRootClass(final QualifiedName typeClassName) {
        return leccCALServices.getModuleTypeInfo(typeClassName.getModuleName())
            .getTypeClass(typeClassName.getUnqualifiedName())
            .internal_isSingleMethodRootClass();
    }
    
    /**
     * A helper function used to ensure the continued validity of the hand-written RTSupercombinator subclasses. 
     * @param classMethodName
     * @return the index within the run-time dictionary of the given class method.
     */
    private static int classMethodDictionaryIndex(final QualifiedName classMethodName) {
        return leccCALServices.getModuleTypeInfo(classMethodName.getModuleName())
            .getClassMethod(classMethodName.getUnqualifiedName())
            .internal_getDictionaryIndex();
    }
}
