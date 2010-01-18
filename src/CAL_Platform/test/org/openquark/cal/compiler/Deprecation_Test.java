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
 * Deprecation_Test.java
 * Creation date: Feb 14, 2007.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;


/**
 * A set of time-consuming JUnit test cases for testing the deprecation warnings in the CAL compiler.
 *
 * @author Joseph Wong
 */
public class Deprecation_Test extends TestCase {

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

        TestSuite suite = new TestSuite(Deprecation_Test.class);

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
     * Tests the warning message for deprecated module reference.
     */
    public void testDeprecatedModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support2.toSourceText() + ";",
            MessageKind.Warning.DeprecatedModule.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated module reference - in CALDoc.
     */
    public void testDeprecatedModule_CALDoc() {
        CompilerTestUtilities.checkDefnForMultipleExpectedWarnings(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support2.toSourceText() + ";" +
            "/** @see module = " + CALPlatformTestModuleNames.Deprecation_Test_Support2.toSourceText() + " */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedModule.class, 2, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated module reference - in CALDoc.
     */
    public void testDeprecatedModule_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForMultipleExpectedWarnings(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support2.toSourceText() + ";" +
            "/** {@link " + CALPlatformTestModuleNames.Deprecation_Test_Support2.toSourceText() + "@} */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedModule.class, 2, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic type reference.
     */
    public void testDeprecatedAlgebraicType() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeConstructor = DeprecatedAlgebraicType;;" +
            "foo = Prelude.undefined :: DeprecatedAlgebraicType;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic type reference - in CALDoc.
     */
    public void testDeprecatedAlgebraicType_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeConstructor = DeprecatedAlgebraicType;;" +
            "/** @see typeConstructor = DeprecatedAlgebraicType */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic type reference - in CALDoc.
     */
    public void testDeprecatedAlgebraicType_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeConstructor = DeprecatedAlgebraicType;;" +
            "/** @see DeprecatedAlgebraicType */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic type reference - defined in the same module.
     */
    public void testDeprecatedAlgebraicType_SameModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ data DeprecatedAlgebraicType = Foo;" +
            "foo = Prelude.undefined :: DeprecatedAlgebraicType;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic type reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedAlgebraicType_SameModule_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ data DeprecatedAlgebraicType = Foo;" +
            "/** @see typeConstructor = DeprecatedAlgebraicType */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic type reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedAlgebraicType_SameModule_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ data DeprecatedAlgebraicType = Foo;" +
            "/** @see DeprecatedAlgebraicType */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign type reference.
     */
    public void testDeprecatedForeignType() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeConstructor = DeprecatedForeignType;;" +
            "foo = Prelude.undefined :: DeprecatedForeignType;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign type reference - in CALDoc.
     */
    public void testDeprecatedForeignType_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeConstructor = DeprecatedForeignType;;" +
            "/** {@link typeConstructor = DeprecatedForeignType@} */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign type reference - in CALDoc.
     */
    public void testDeprecatedForeignType_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeConstructor = DeprecatedForeignType;;" +
            "/** {@link DeprecatedForeignType@} */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign type reference - defined in the same module.
     */
    public void testDeprecatedForeignType_SameModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ data foreign unsafe import jvm private \"java.lang.Object\" public DeprecatedForeignType;" +
            "foo = Prelude.undefined :: DeprecatedForeignType;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign type reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedForeignType_SameModule_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ data foreign unsafe import jvm \"java.lang.Object\" DeprecatedForeignType;" +
            "/** {@link typeConstructor = DeprecatedForeignType@} */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign type reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedForeignType_SameModule_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ data foreign unsafe import jvm \"java.lang.Object\" DeprecatedForeignType;" +
            "/** {@link DeprecatedForeignType@} */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedType.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated data cons reference.
     */
    public void testDeprecatedDataCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using dataConstructor = DeprecatedDataCons2;;" +
            "foo = Prelude.undefined.DeprecatedDataCons2.x;",
            MessageKind.Warning.DeprecatedDataCons.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated data cons reference - in CALDoc.
     */
    public void testDeprecatedDataCons_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using dataConstructor = DeprecatedDataCons1;;" +
            "/** @see dataConstructor = DeprecatedDataCons1 */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedDataCons.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated data cons reference - in CALDoc.
     */
    public void testDeprecatedDataCons_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using dataConstructor = DeprecatedDataCons1;;" +
            "/** @see DeprecatedDataCons1 */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedDataCons.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated data cons reference - defined in the same module.
     */
    public void testDeprecatedDataCons_SameModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "data Foo = /** @deprecated */ DeprecatedDataCons x :: ();" +
            "foo = Prelude.undefined.DeprecatedDataCons.x;",
            MessageKind.Warning.DeprecatedDataCons.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated data cons reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedDataCons_SameModule_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "data Foo = /** @deprecated */ DeprecatedDataCons x :: ();" +
            "/** @see dataConstructor = DeprecatedDataCons */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedDataCons.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated data cons reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedDataCons_SameModule_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "data Foo = /** @deprecated */ DeprecatedDataCons x :: ();" +
            "/** @see DeprecatedDataCons */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedDataCons.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated type class reference.
     */
    public void testDeprecatedTypeClass() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeClass = DeprecatedClass; function = unit;;" +
            "instance DeprecatedClass () where regularMethod = unit;;",
            MessageKind.Warning.DeprecatedTypeClass.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated type class reference - in CALDoc.
     */
    public void testDeprecatedTypeClass_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeClass = DeprecatedClass;;" +
            "/** @see typeClass = DeprecatedClass */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedTypeClass.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated type class reference - in CALDoc.
     */
    public void testDeprecatedTypeClass_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using typeClass = DeprecatedClass;;" +
            "/** @see DeprecatedClass */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedTypeClass.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated type class reference - defined in the same module.
     */
    public void testDeprecatedTypeClass_SameModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ class DeprecatedClass a where;" +
            "instance DeprecatedClass () where;",
            MessageKind.Warning.DeprecatedTypeClass.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated type class reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedTypeClass_SameModule_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ class DeprecatedClass a where;" +
            "/** @see typeClass = DeprecatedClass */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedTypeClass.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated type class reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedTypeClass_SameModule_CALDoc_WithoutContextCons() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ class DeprecatedClass a where;" +
            "/** @see DeprecatedClass */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedTypeClass.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated class method reference.
     */
    public void testDeprecatedClassMethod() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using function = deprecatedMethod;;" +
            "foo = deprecatedMethod;",
            MessageKind.Warning.DeprecatedClassMethod.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated class method reference - in CALDoc.
     */
    public void testDeprecatedClassMethod_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using function = deprecatedMethod;;" +
            "/** @see deprecatedMethod */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedClassMethod.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated class method reference - defined in the same module.
     */
    public void testDeprecatedClassMethod_SameModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "class Foo a where /** @deprecated */ deprecatedMethod :: a;;" +
            "foo = deprecatedMethod;",
            MessageKind.Warning.DeprecatedClassMethod.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated class method reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedClassMethod_SameModule_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "class Foo a where /** @deprecated */ deprecatedMethod :: a;;" +
            "/** @see deprecatedMethod */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedClassMethod.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic function reference.
     */
    public void testDeprecatedAlgebraicFunction() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using function = deprecatedAlgebraicFunction;;" +
            "foo = deprecatedAlgebraicFunction;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic function reference - in CALDoc.
     */
    public void testDeprecatedAlgebraicFunction_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using function = deprecatedAlgebraicFunction;;" +
            "/** @see deprecatedAlgebraicFunction */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic function reference - defined in the same module.
     */
    public void testDeprecatedAlgebraicFunction_SameModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ deprecatedAlgebraicFunction = ();" +
            "foo = deprecatedAlgebraicFunction;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated algebraic function reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedAlgebraicFunction_SameModule_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ deprecatedAlgebraicFunction = ();" +
            "/** @see deprecatedAlgebraicFunction */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign function reference.
     */
    public void testDeprecatedForeignFunction() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using function = deprecatedForeignFunction;;" +
            "foo = deprecatedForeignFunction;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign function reference - in CALDoc.
     */
    public void testDeprecatedForeignFunction_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "import " + CALPlatformTestModuleNames.Deprecation_Test_Support1.toSourceText() + " using function = deprecatedForeignFunction;;" +
            "/** {@link deprecatedForeignFunction@} */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign function reference - defined in the same module.
     */
    public void testDeprecatedForeignFunction_SameModule() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ foreign unsafe import jvm \"method toString\" deprecatedForeignFunction :: Prelude.JObject -> Prelude.String;" +
            "foo = deprecatedForeignFunction;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
    
    /**
     * Tests the warning message for deprecated foreign function reference - defined in the same module - in CALDoc.
     */
    public void testDeprecatedForeignFunction_SameModule_CALDoc() {
        CompilerTestUtilities.checkDefnForExpectedWarning(
            "/** @deprecated */ foreign unsafe import jvm \"method toString\" deprecatedForeignFunction :: Prelude.JObject -> Prelude.String;" +
            "/** {@link deprecatedForeignFunction@} */ foo = Prelude.undefined;",
            MessageKind.Warning.DeprecatedFunction.class, leccCALServices);
    }
}
