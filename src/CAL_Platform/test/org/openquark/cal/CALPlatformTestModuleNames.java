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
 * CALPlatformTestModuleNames.java
 * Creation date: Dec 6, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal;

import org.openquark.cal.compiler.ModuleName;

/**
 * This class contains constants for the names of some test modules in CAL Platform.
 *
 * @author Joseph Wong
 */
public final class CALPlatformTestModuleNames {

    /** This class is not meant to be instantiated. */
    private CALPlatformTestModuleNames() {}

    public static final ModuleName Prelude_Tests = ModuleName.make("Cal.Test.Core.Prelude_Tests");
    public static final ModuleName Array_Tests = ModuleName.make("Cal.Test.Core.Array_Tests");
    public static final ModuleName CALRenaming_Test_Support1 = ModuleName.make("Cal.Test.JUnitSupport.CALRenaming_Test_Support1");
    public static final ModuleName CALRenaming_Test_Support2 = ModuleName.make("Cal.Test.JUnitSupport.CALRenaming_Test_Support2");
    public static final ModuleName ImportCleaner_Test_Support1 = ModuleName.make("Cal.Test.JUnitSupport.ImportCleaner_Test_Support1");
    public static final ModuleName ImportCleaner_Test_Support2 = ModuleName.make("Cal.Test.JUnitSupport.ImportCleaner_Test_Support2");
    public static final ModuleName LegacyTuple = ModuleName.make("Cal.Test.General.LegacyTuple");
    public static final ModuleName M1 = ModuleName.make("Cal.Test.General.M1");
    public static final ModuleName M2 = ModuleName.make("Cal.Test.General.M2");
    public static final ModuleName M3 = ModuleName.make("Cal.Test.General.M3");
    public static final ModuleName RuntimeRegression = ModuleName.make("Cal.Test.General.RuntimeRegression");
    public static final ModuleName SourceMetricFinder_Test_Support = ModuleName.make("Cal.Test.JUnitSupport.SourceMetricFinder_Test_Support");
    public static final ModuleName TypeDeclarationInserter_Test_Support = ModuleName.make("Cal.Test.JUnitSupport.TypeDeclarationInserter_Test_Support");
    public static final ModuleName ErrorTest = ModuleName.make("Cal.Test.JUnitSupport.ErrorTest");
    public static final ModuleName CALDocTest = ModuleName.make("Cal.Test.General.CALDocTest");
    public static final ModuleName Memoize_Tests = ModuleName.make("Cal.Test.Core.Memoize_Tests");
    public static final ModuleName DerivedInstanceFunctionGenerator_Test_Support = ModuleName.make("Cal.Test.JUnitSupport.DerivedInstanceFunctionGenerator_Test_Support");
    public static final ModuleName Deprecation_Test_Support1 = ModuleName.make("Cal.Test.JUnitSupport.Deprecation_Test_Support1");
    public static final ModuleName Deprecation_Test_Support2 = ModuleName.make("Cal.Test.JUnitSupport.Deprecation_Test_Support2");
    public static final ModuleName CAL_PlatForm_Test_suite = ModuleName.make("Cal.Test.CAL_Platform_TestSuite");

}
