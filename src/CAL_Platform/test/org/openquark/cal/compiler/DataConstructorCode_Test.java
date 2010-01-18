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
 * DataConstructorCode_Test.java
 * Creation date: Jul 13, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.compiler;

import java.util.Arrays;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.StringModuleSourceDefinition;
import org.openquark.cal.services.WorkspaceManager;


/**
 * A set of JUnit test cases for verifying the static analysis of data constructors in .cal code.
 * Note: these tests test for failure cases -- we can include cases which should succeed as part of actual module code (eg. M2.cal).
 *
 * @author Edward Lam
 */
public class DataConstructorCode_Test extends TestCase {
    
    private static final ModuleName Prelude = CAL_Prelude.MODULE_NAME;

    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;

    private static final ModuleName DEFAULT_TEST_MODULE_NAME = ModuleName.make("DataConstructorCodeTestModule");
    private static final String DEFAULT_TEST_FUNCTION_NAME = "dataConstructorCodeTestFunction";
    private static final String DEFAULT_DATA_CONS_NAME = "DataConstructorCodeTestType";
    
    private static final SourceModel.Name.DataCons DEFAULT_DATA_CONS_QUALIFIED_NAME = 
            SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME));
    
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(DataConstructorCode_Test.class);

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
     * Constructor for DataConstructorCode_Test.
     * 
     * @param name
     *            the name of the test.
     */
    public DataConstructorCode_Test(String name) {
        super(name);
    }

    /**
     * Tests that an argument name can't be used twice for a data constructor.
     */
    public void testRepeatedDataConstructorArgumentNameFails() {
        // Field names {foo, foo, bar} -- field name "foo" is repeated.
        String[] textualFieldNames = new String[] {"foo", "foo", "bar"};
        
        // Create the type constructor with the data cons definition.
        SourceModel.TypeConstructorDefn typeConsDefn = getSingleDataConstructorTypeConstructorDefinition(textualFieldNames);
        
        // Compile as a new module.
        CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, new SourceModel.TopLevelSourceElement[]{typeConsDefn});
        
        // Check that we got an error message of the expected type.
        CompilerMessage error = logger.getFirstError();
        assertTrue("No errors logged.", error != null);
        assertEquals(MessageKind.Error.RepeatedFieldNameInDataConstructorDeclaration.class, error.getMessageKind().getClass());
    }
    
    
    /**
     * Tests that an error is given for unknown field names when using the matching notation for data constructor unpacking.
     * eg. where SomeDataCons does not have an argument named "unknownDataConsArg":
     *     myFunction arg = case arg of SomeDataCons {unknownDataConsArg = patternVar} -> patternVar;;
     */
    public void testMatchingUnknownDataConstructorArgument() {
        // Create the type constructor definition..
        String[] textualFieldNames = new String[] {"foo", "bar"};
        SourceModel.TypeConstructorDefn typeConsDefn = getSingleDataConstructorTypeConstructorDefinition(textualFieldNames);

        // Create a case alt for a data constructor using the matching notation, where a field name is unknown.
        FieldName fieldName = FieldName.make("unknownFieldName");
        SourceModel.Pattern pattern = SourceModel.Pattern.Var.make("unusedPatternVar");
        SourceModel.FieldPattern fieldPattern = SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fieldName), pattern);
        SourceModel.FieldPattern[] fieldPatterns = new SourceModel.FieldPattern[] {fieldPattern};
        
        SourceModel.Expr.Case.Alt caseAlt = 
            SourceModel.Expr.Case.Alt.UnpackDataCons.make(DEFAULT_DATA_CONS_QUALIFIED_NAME, fieldPatterns, SourceModel.Expr.makeBooleanValue(true));

        // Put into a case expression (conditional upon "var"), wrap with a function.
        SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
        SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
        SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
        
        // Compile our elements to a new module.
        SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
        CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
        
        // Check that we got an error message of the expected type.
        CompilerMessage error = logger.getFirstError();
        assertTrue("No errors logged.", error != null);
        assertEquals(MessageKind.Error.UnknownDataConstructorField.class, error.getMessageKind().getClass());
    }

    /**
     * Tests that an error is given for repeated field names when using the matching notation for data constructor unpacking.
     * eg. myFunction arg = case arg of SomeDataCons {repeatedFieldName = patternVar1, repeatedFieldName = patternVar2} -> True;;
     */
    public void testRepeatedFieldNameInFieldBinding() {
        // Create the type constructor definition..
        String[] textualFieldNames = new String[] {"foo", "bar"};
        SourceModel.TypeConstructorDefn typeConsDefn = getSingleDataConstructorTypeConstructorDefinition(textualFieldNames);

        // Create a case alt for a data constructor using the matching notation, with two field patterns where the field names are the same.
        FieldName repeatedFieldName = FieldName.make("foo");
        SourceModel.Pattern pattern1 = SourceModel.Pattern.Var.make("unusedPatternVar1");
        SourceModel.Pattern pattern2 = SourceModel.Pattern.Var.make("unusedPatternVar2");
        
        SourceModel.FieldPattern fieldPattern1 = SourceModel.FieldPattern.make(SourceModel.Name.Field.make(repeatedFieldName), pattern1);
        SourceModel.FieldPattern fieldPattern2 = SourceModel.FieldPattern.make(SourceModel.Name.Field.make(repeatedFieldName), pattern2);
        SourceModel.FieldPattern[] fieldPatterns = new SourceModel.FieldPattern[] {fieldPattern1, fieldPattern2};
        
        SourceModel.Expr.Case.Alt caseAlt = 
            SourceModel.Expr.Case.Alt.UnpackDataCons.make(DEFAULT_DATA_CONS_QUALIFIED_NAME, fieldPatterns, SourceModel.Expr.makeBooleanValue(true));

        // Put into a case expression (conditional upon "var")
        SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(SourceModel.Expr.Var.makeUnqualified("var"), 
                                                                    new SourceModel.Expr.Case.Alt[]{caseAlt});
        
        // wrap with a function.
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
        SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
        
        // Compile our elements to a new module.
        SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
        CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);

        
        // Check that we got a single error message of the expected type.
        CompilerMessage error = logger.getFirstError();
        assertTrue("No errors logged.", error != null);
        assertEquals(MessageKind.Error.RepeatedFieldNameInFieldBindingPattern.class, error.getMessageKind().getClass());
    }
    // repeated pattern variable in field binding.

    /**
     * Tests that an error is given for repeated pattern variables when using the matching notation for data constructor unpacking.
     * eg. myFunction arg = case arg of SomeDataCons {dataConsArg1 = repeatedPatternVar, dataConsArg2 = repeatedPatternVar} -> True;;
     */
    public void testRepeatedPatternVarInFieldBinding() {
        // Create the type constructor definition..
        String[] textualFieldNames = new String[] {"foo", "bar"};
        SourceModel.TypeConstructorDefn typeConsDefn = getSingleDataConstructorTypeConstructorDefinition(textualFieldNames);

        // Create a case alt for a data constructor using the matching notation, with two field patterns where the field names are the same.
        FieldName fieldName1 = FieldName.make("foo");
        FieldName fieldName2 = FieldName.make("bar");
        SourceModel.Pattern repeatedPattern = SourceModel.Pattern.Var.make("repeatedPatternVar");
        
        SourceModel.FieldPattern fieldPattern1 = SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fieldName1), repeatedPattern);
        SourceModel.FieldPattern fieldPattern2 = SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fieldName2), repeatedPattern);
        SourceModel.FieldPattern[] fieldPatterns = new SourceModel.FieldPattern[] {fieldPattern1, fieldPattern2};
        
        SourceModel.Expr.Case.Alt caseAlt = 
            SourceModel.Expr.Case.Alt.UnpackDataCons.make(DEFAULT_DATA_CONS_QUALIFIED_NAME, fieldPatterns, SourceModel.Expr.makeBooleanValue(true));

        // Put into a case expression (conditional upon "var")
        SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(SourceModel.Expr.Var.makeUnqualified("var"), 
                                                                    new SourceModel.Expr.Case.Alt[]{caseAlt});
        
        // wrap with a function.
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
        SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
        
        // Compile our elements to a new module.
        SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
        CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);

        
        // Check that we got an error message of the expected type.
        CompilerMessage error = logger.getFirstError();
        assertTrue("No errors logged.", error != null);
        assertEquals(MessageKind.Error.RepeatedPatternVariableInFieldBindingPattern.class, error.getMessageKind().getClass());
    }
    

    /**
     * Helper function to get a source model for a type constructor consisting of a single data constructor with the given fields.
     * Each of the fields will have the Unit type, so the type constructor will take a single parameter of type Unit.
     * 
     * @param textualFieldNames the names of the fields.  These must be valid textual field names.
     * @return the source model for the corresponding type constructor.
     */
    private static SourceModel.TypeConstructorDefn getSingleDataConstructorTypeConstructorDefinition(String[] textualFieldNames) {
        // Create the field names.
        FieldName[] fieldNames = new FieldName[textualFieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = FieldName.make(textualFieldNames[i]);
        }

        // Create the type arguments.
        DataConsDefn.TypeArgument[] typeArguments = new DataConsDefn.TypeArgument[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            typeArguments[i] = DataConsDefn.TypeArgument.make(SourceModel.Name.Field.make(fieldNames[i]), SourceModel.TypeExprDefn.Unit.make(), false);
        }
        
        // Create the data constructor definition.
        DataConsDefn dataConsDefnWithRepeatedArgName = DataConsDefn.make(DEFAULT_DATA_CONS_NAME, Scope.PRIVATE, typeArguments);
        
        // Create the type constructor with the data cons definition.
        SourceModel.TypeConstructorDefn typeConsDefn = 
            SourceModel.TypeConstructorDefn.AlgebraicType.make(DEFAULT_DATA_CONS_NAME, Scope.PRIVATE, null, 
                                                               new DataConsDefn[]{dataConsDefnWithRepeatedArgName}, 
                                                               SourceModel.TypeConstructorDefn.NO_DERIVING_CLAUSE);
        
        return typeConsDefn;
    }
    
    /**
     * Check that a given set of outer defns gives a compile error when compiled to a module.
     * @param outerDefnTextLines the lines of text of the outer defns.
     * @param expectedErrorClass the class of the expected error.
     */
    private static void checkDefnForExpectedError(String[] outerDefnTextLines, Class<? extends MessageKind.Error> expectedErrorClass) {
        CompilerTestUtilities.checkDefnForExpectedError(outerDefnTextLines, expectedErrorClass, leccCALServices);
    }
    
    /**
     * Compile a new module containing only the provided top-level source elements, plus required imports, and remove it afterwards.
     * @param moduleName the name of the module.
     * @param topLevelSourceElements the top-level source elements to include.
     * @return a logger which logged the results of the compile.
     */
    private static CompilerMessageLogger compileAndRemoveModuleForTopLevelSourceElements(ModuleName moduleName, SourceModel.TopLevelSourceElement[] topLevelSourceElements) {
        // Get the workspace manager and the logger.
        WorkspaceManager workspaceManager = leccCALServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();

        // Get the module defn.
        SourceModel.ModuleDefn moduleDefnWithoutImports = SourceModel.ModuleDefn.make(moduleName, new SourceModel.Import[0], topLevelSourceElements);
        SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.ImportAugmenter.augmentWithImports(moduleDefnWithoutImports);

        // Compile the module.
        SourceModelModuleSource moduleSource = new SourceModelModuleSource(moduleDefn);
        workspaceManager.makeModule(moduleSource, logger);

        // Remove the module.
        leccCALServices.getWorkspaceManager().removeModule(DEFAULT_TEST_MODULE_NAME, new Status("Remove module status."));

        return logger;
    }
    
    /**
     * Test that a data constructor argument without a field name gives an error.
     */
    public void testUnnamedDCArgError() {
        // Get the workspace manager and the logger.
        WorkspaceManager workspaceManager = leccCALServices.getWorkspaceManager();
        CompilerMessageLogger logger = new MessageLogger();

        ModuleName moduleName = DEFAULT_TEST_MODULE_NAME;
        final String moduleCode =
            "module " + DEFAULT_TEST_MODULE_NAME + ";" +
            "import " + Prelude + ";" +
            
            "data Foo = Foo " + Prelude + ".Int;";

        // Compile the module.
        ModuleSourceDefinition moduleSourceDef = new StringModuleSourceDefinition(moduleName, moduleCode);
        workspaceManager.makeModule(moduleSourceDef, logger);

        // Remove the module.
        leccCALServices.getWorkspaceManager().removeModule(DEFAULT_TEST_MODULE_NAME, new Status("Remove module status."));
        
        // Check that we got an error message of the expected type.
        // Note: this can only check for a syntax error.  However, there are many ways in which a syntax error can occur.
        CompilerMessage error = logger.getFirstError();
        assertTrue("No errors logged.", error != null);
        assertEquals(MessageKind.Error.SyntaxErrorWithParaphrase.class, error.getMessageKind().getClass());
    }

    /**
     * Check that an error is given if, in a data constructor field selection expression, the dataCons-valued expression
     *   doesn't evaluate to the expected data constructor.
     *   
     * ie. "[].Cons.head" should give a runtime error, since the data constructor for "[]" is "Nil".
     * 
     */
    public void testUnexpectedDCForSelectDCFieldExpr() throws GemCompilationException {

        try {        
            CALServicesTestUtilities.runNamedFunction(QualifiedName.make(CALPlatformTestModuleNames.M2, "selectDCFieldShouldFail"), leccCALServices);
            fail("This should have failed");
            
        } catch (CALExecutorException e){
            // Can't check position - no position information because the call is evaluated lazily.
        }

    }
    
    /**
     * Check for an error when the field name is not valid for a selectDCField expression.
     */
    public void testUnknownDCFieldNameInSelectDCFieldExpr() {
        {
            CompilerMessageLogger logger = compileSelectDCExprFromSingleElementDoubleList("hd");
            
            // Check that we got an error message of the expected type.
            CompilerMessage error = logger.getFirstError();
            assertTrue("No errors logged.", error != null);
            assertEquals(MessageKind.Error.UnknownDataConstructorField.class, error.getMessageKind().getClass());
        }
        
        {
            CompilerMessageLogger logger = compileSelectDCExprFromSingleElementDoubleList("#1");
            
            // Check that we got an error message of the expected type.
            CompilerMessage error = logger.getFirstError();
            assertTrue("No errors logged.", error != null);
            assertEquals(MessageKind.Error.UnknownDataConstructorField.class, error.getMessageKind().getClass());
        }
    }
    
    /**
     * A helper method for tests of errors in field selection.
     * Creates and compiles a function of the form:
     *   someFunctionName = [1.0].Cons.(calSourceFormFieldName);
     * 
     * @param calSourceFormFieldName the cal source form of the field to select.
     * @return the message logger resulting from the compile
     */
    private CompilerMessageLogger compileSelectDCExprFromSingleElementDoubleList(String calSourceFormFieldName) {
        
        SourceModel.Expr dataConsValuedExpr = SourceModel.Expr.List.make(new SourceModel.Expr[]{SourceModel.Expr.makeDoubleValue(1.0)});
        SourceModel.Name.DataCons dataConsName = SourceModel.Name.DataCons.make(Prelude, CAL_Prelude.DataConstructors.Cons.getUnqualifiedName());
        
        SourceModel.Expr selectDCExpr = SourceModel.Expr.SelectDataConsField.make(dataConsValuedExpr, dataConsName, SourceModel.Name.Field.make(FieldName.make(calSourceFormFieldName)));
        
        SourceModel.FunctionDefn functionDefn = 
            SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, null, selectDCExpr);

        // Compile our elements to a new module.
        SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{functionDefn};
        CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);

        return logger;
    }

    /**
     * Check for an error when an incorrect number of args appears in a case alt using pattern group syntax.
     */
    public void testIncorrectNArgsWithPatternGroup() {
        String[][] dcFieldNames = new String[][] {
                {"foo", "bar"},
                {"foo"},
                {"qux"}
        };
        
        SourceModel.TypeConstructorDefn typeConsDefn = getTypeConstructorDefinition(dcFieldNames);
        
        /* 
         * foo var = 
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) foo bar -> True;
         *     ;
         */
        {
            SourceModel.Name.DataCons[] dataConsNames = new SourceModel.Name.DataCons[] {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
            };
            
            // Create a case alt using positional notation, where the names are the same.
            SourceModel.Pattern.Var[] patterns = {
                    SourceModel.Pattern.Var.make("foo"), 
                    SourceModel.Pattern.Var.make("bar")
            };
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, patterns, SourceModel.Expr.makeBooleanValue(true));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            CompilerMessage error = logger.getFirstError();
            assertTrue("No errors logged.", error != null);
            assertEquals(MessageKind.Error.ConstructorMustHaveExactlyNArgsInPattern.class, error.getMessageKind().getClass());
        }
    }
    
    /**
     * Check for an error when a repeated pattern appears in a case alt using pattern group syntax.
     */
    public void testRepeatedPatternWithPatternGroup() {
        
        String[][] dcFieldNames = new String[][] {
                {"foo", "bar"},
                {"baz"},
                {"qux"}
        };
        
        SourceModel.TypeConstructorDefn typeConsDefn = getTypeConstructorDefinition(dcFieldNames);
        
        /* 
         * Repeated within an individual group.
         * 
         * foo var = 
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)0 ) {} -> True;
         *     ;
         */
        {
            SourceModel.Name.DataCons[] dataConsNames = {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0))
            };
            
            SourceModel.FieldPattern[] fieldPatterns = SourceModel.FieldPattern.NO_FIELD_PATTERNS;
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, fieldPatterns, SourceModel.Expr.makeBooleanValue(true));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            CompilerMessage error = logger.getFirstError();
            assertTrue("No errors logged.", error != null);
            assertEquals(MessageKind.Error.RepeatedPatternInCaseExpression.class, error.getMessageKind().getClass());
        }

        /* 
         * Repeated among separate groups.
         * 
         * foo var = 
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) {} -> True;
         *     ( (DEFAULT_DATA_CONS_NAME)2 | (DEFAULT_DATA_CONS_NAME)1 ) {} -> False;
         *     ;
         */
        {
            SourceModel.Name.DataCons[] dataConsNames1 = {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
            };
            SourceModel.Name.DataCons[] dataConsNames2 = {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 2)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
            };
            
            SourceModel.FieldPattern[] fieldPatterns = SourceModel.FieldPattern.NO_FIELD_PATTERNS;
            
            SourceModel.Expr.Case.Alt caseAlt1 = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames1, fieldPatterns, SourceModel.Expr.makeBooleanValue(true));
            SourceModel.Expr.Case.Alt caseAlt2 = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames2, fieldPatterns, SourceModel.Expr.makeBooleanValue(false));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt1, caseAlt2});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            List<CompilerMessage> errorList = logger.getCompilerMessages();
            assertFalse("No errors logged.", errorList.isEmpty());
            CompilerMessage error = errorList.get(0);
            assertEquals(MessageKind.Error.RepeatedPatternInCaseExpression.class, error.getMessageKind().getClass());
        }

    }
    
    /**
     * Check for an error when a repeated var name appears in a case alt using pattern group syntax.
     */
    public void testRepeatedVarNameWithPatternGroup() {
        String[][] dcFieldNames = new String[][] {
                {"foo", "bar"},
                {"foo", "baz"},
                {"qux"}
        };
        
        SourceModel.TypeConstructorDefn typeConsDefn = getTypeConstructorDefinition(dcFieldNames);
        
        /* 
         * foo var = 
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) foo foo -> True;
         *     ;
         */
        {
            SourceModel.Name.DataCons[] dataConsNames = new SourceModel.Name.DataCons[] {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
            };
            
            // Create a case alt using positional notation, where the names are the same.
            SourceModel.Pattern.Var[] patterns = {
                    SourceModel.Pattern.Var.make("foo"), 
                    SourceModel.Pattern.Var.make("foo")
            };
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, patterns, SourceModel.Expr.makeBooleanValue(true));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            CompilerMessage error = logger.getFirstError();
            assertTrue("No errors logged.", error != null);
            assertEquals(MessageKind.Error.RepeatedVariableUsedInBinding.class, error.getMessageKind().getClass());
        }

        /* 
         * foo var = 
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) {foo, foo} -> True;
         *     ;
         */
        {
            SourceModel.Name.DataCons[] dataConsNames = new SourceModel.Name.DataCons[] {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
            };
            
            // Create a case alt using matching notation, where the names are the same.
            FieldName fooFieldName = FieldName.make("foo");
            SourceModel.FieldPattern[] patterns = {
                    SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fooFieldName), null), 
                    SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fooFieldName), null)
            };
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, patterns, SourceModel.Expr.makeBooleanValue(true));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            List<CompilerMessage> errorList = logger.getCompilerMessages();
            assertFalse("No errors logged.", errorList.isEmpty());
            CompilerMessage error = errorList.get(0);
            assertEquals(MessageKind.Error.RepeatedFieldNameInFieldBindingPattern.class, error.getMessageKind().getClass());
        }
    }
    
    /**
     * Check for an error when a non-existent var name appears in a case alt using pattern group syntax.
     */
    public void testNonExistentVarWithPatternGroup() {
        
        String[][] dcFieldNames = new String[][] {
                {"foo", "bar"},
                {"foo"},
                {"qux"}
        };
        
        SourceModel.TypeConstructorDefn typeConsDefn = getTypeConstructorDefinition(dcFieldNames);

        /* 
         * Exists - should be ok.
         * 
         * foo var = 
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) {foo} -> foo;
         *     ;
         */
        {
            SourceModel.Name.DataCons[] dataConsNames = {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
            };
            
            FieldName fieldName = FieldName.make("foo");
            SourceModel.FieldPattern[] fieldPatterns = {SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fieldName), null)};
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, fieldPatterns, SourceModel.Expr.Var.makeUnqualified("foo"));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            List<CompilerMessage> errorList = logger.getCompilerMessages();
            assertTrue("Errors logged.", errorList.isEmpty());
        }
        
        /* 
         * Doesn't exist..
         * 
         * foo var = 
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) {bar} -> bar;
         *     ;
         */
        {
            SourceModel.Name.DataCons[] dataConsNames = {
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                    SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
            };
            
            FieldName fieldName = FieldName.make("bar");
            SourceModel.FieldPattern[] fieldPatterns = {SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fieldName), null)};
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, fieldPatterns, SourceModel.Expr.Var.makeUnqualified("bar"));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            List<CompilerMessage> errorList = logger.getCompilerMessages();
            assertFalse("No errors logged.", errorList.isEmpty());
            CompilerMessage error = errorList.get(0);
            assertEquals(MessageKind.Error.UnknownDataConstructorField.class, error.getMessageKind().getClass());
        }
    }
    
    /**
     * Check for an error when a case alt unpack pattern using pattern group syntax uses an argument which cannot
     * be assigned a type.
     */
    public void testDataConstructorPatternGroupArgumentNotTypeable() {
        String[][] dcFieldNames = new String[][] {
                {"arg"},
                {"arg"}
        };
        
        SourceModel.TypeExprDefn[][] dcFieldTypes = new SourceModel.TypeExprDefn[][] {
                {SourceModel.TypeExprDefn.TypeCons.make(Prelude, CAL_Prelude.TypeConstructors.Int.getUnqualifiedName())},
                {SourceModel.TypeExprDefn.TypeCons.make(Prelude, CAL_Prelude.TypeConstructors.Double.getUnqualifiedName())}
        };
        
        SourceModel.TypeConstructorDefn typeConsDefn = getTypeConstructorDefinition(dcFieldNames, dcFieldTypes);

        SourceModel.Name.DataCons[] dataConsNames = {
                SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 0)),
                SourceModel.Name.DataCons.make(QualifiedName.make(DEFAULT_TEST_MODULE_NAME, DEFAULT_DATA_CONS_NAME + 1))
        };
        
        SourceModel.Pattern argPattern = SourceModel.Pattern.Var.make("arg");
        
        /*
         * foo var =
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) arg -> arg;
         *     ;
         */
        {
            SourceModel.Pattern[] argPatterns = {argPattern};
            
            SourceModel.Expr altExpr = SourceModel.Expr.Var.makeUnqualified("arg");
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, argPatterns, altExpr);
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            CompilerMessage error = logger.getFirstError();
            assertTrue("No errors logged.", error != null);
            assertEquals(MessageKind.Error.DataConstructorPatternGroupArgumentNotTypeable.class, error.getMessageKind().getClass());
        }

        /*
         * foo var =
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) arg -> True;
         *     ;
         * 
         * arg is unused, but we still should check whether its type is unifiable for all dc's.
         */
        {
            SourceModel.Pattern[] argPatterns = {argPattern};
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, argPatterns, SourceModel.Expr.makeBooleanValue(true));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            List<CompilerMessage> errorList = logger.getCompilerMessages();
            assertFalse("No errors logged.", errorList.isEmpty());
            CompilerMessage error = errorList.get(0);
            assertEquals(MessageKind.Error.DataConstructorPatternGroupArgumentNotTypeable.class, error.getMessageKind().getClass());
        }

        /*
         * foo var =
         *     case var of
         *     ( (DEFAULT_DATA_CONS_NAME)0 | (DEFAULT_DATA_CONS_NAME)1 ) {arg} -> True;
         *     ;
         */
        {
            FieldName fieldName = FieldName.make("arg");
            SourceModel.FieldPattern[] fieldPatterns = {SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fieldName), argPattern)};
            
            SourceModel.Expr.Case.Alt caseAlt = 
                SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsNames, fieldPatterns, SourceModel.Expr.makeBooleanValue(true));
            
            // Put into a case expression (conditional upon "var"), wrap with a function.
            SourceModel.Expr.Var varExpr = SourceModel.Expr.Var.makeUnqualified("var");
            SourceModel.Expr.Case caseExpr = SourceModel.Expr.Case.make(varExpr, new SourceModel.Expr.Case.Alt[]{caseAlt});
            
            SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {SourceModel.Parameter.make("var", false)};
            SourceModel.FunctionDefn functionDefn = 
                SourceModel.FunctionDefn.Algebraic.make(DEFAULT_TEST_FUNCTION_NAME, Scope.PRIVATE, parameters, caseExpr);
            
            // Compile our elements to a new module.
            SourceModel.TopLevelSourceElement[] topLevelSourceElements = new SourceModel.TopLevelSourceElement[]{typeConsDefn, functionDefn};
            CompilerMessageLogger logger = compileAndRemoveModuleForTopLevelSourceElements(DEFAULT_TEST_MODULE_NAME, topLevelSourceElements);
            
            // Check that we got a single error message of the expected type.
            List<CompilerMessage> errorList = logger.getCompilerMessages();
            assertFalse("No errors logged.", errorList.isEmpty());
            CompilerMessage error = errorList.get(0);
            assertEquals(MessageKind.Error.DataConstructorPatternGroupArgumentNotTypeable.class, error.getMessageKind().getClass());
        }
    }
    
    /**
     * Helper function to get a source model for a type constructor consisting of a number of data constructors with given fields.
     * Each of the fields will have the Unit type.
     * 
     * The definition will be:
     * data private (DEFAULT_DATA_CONS_NAME) =
     *   private (DEFAULT_DATA_CONS_NAME)0 
     *       fieldNamesSourceFormArray[0][0] :: ()
     *       fieldNamesSourceFormArray[0][1] :: () 
     *       ...
     *   private (DEFAULT_DATA_CONS_NAME)1 
     *       fieldNamesSourceFormArray[1][0] :: ()
     *       fieldNamesSourceFormArray[1][1] :: () 
     *       ...
     *   private (DEFAULT_DATA_CONS_NAME)2
     *       fieldNamesSourceFormArray[2][0] :: ()
     *       fieldNamesSourceFormArray[2][1] :: () 
     *       ...
     *   ...
     * 
     * @param fieldNamesSourceFormArray the names of the fields.  These must be valid cal source forms.
     *   The string array at index n will give the names of the fields for data constructor n.
     * @return the source model for the corresponding type constructor.
     */
    private static SourceModel.TypeConstructorDefn getTypeConstructorDefinition(String[][] fieldNamesSourceFormArray) {
        
        // Create an array for corresponding type expr defns.
        int nDCDefs = fieldNamesSourceFormArray.length;
        SourceModel.TypeExprDefn[][] fieldTypesArray = new SourceModel.TypeExprDefn[nDCDefs][];
        
        // Fill the arrays with the unit type.
        for (int i = 0; i < nDCDefs; i++) {
            String[] fieldNamesSourceForm = fieldNamesSourceFormArray[i];
            SourceModel.TypeExprDefn[] typeExprDefnArray = new SourceModel.TypeExprDefn[fieldNamesSourceForm.length];
            fieldTypesArray[i] = typeExprDefnArray;
            
            Arrays.fill(typeExprDefnArray, SourceModel.TypeExprDefn.Unit.make());
        }

        // Defer to the more general method.
        return getTypeConstructorDefinition(fieldNamesSourceFormArray, fieldTypesArray);
    }

    /**
     * Helper function to get a source model for a type constructor consisting of a number of data constructors with given fields and types.
     * The types should not be parametrized -- ie. the resulting data constructor should have no arrows in its type.
     * 
     * The definition will be:
     * data private (DEFAULT_DATA_CONS_NAME) =
     *   private (DEFAULT_DATA_CONS_NAME)0 
     *       fieldNamesSourceFormArray[0][0] :: fieldTypesArray[0][0]
     *       fieldNamesSourceFormArray[0][1] :: fieldTypesArray[0][1]
     *       ...
     *   private (DEFAULT_DATA_CONS_NAME)1 
     *       fieldNamesSourceFormArray[1][0] :: fieldTypesArray[1][0]
     *       fieldNamesSourceFormArray[1][1] :: fieldTypesArray[1][1]
     *       ...
     *   private (DEFAULT_DATA_CONS_NAME)2
     *       fieldNamesSourceFormArray[2][0] :: fieldTypesArray[2][0]
     *       fieldNamesSourceFormArray[2][1] :: fieldTypesArray[2][1]
     *       ...
     *   ...
     * 
     * @param fieldNamesSourceFormArray the names of the fields.  These must be valid cal source forms.
     *   The string array at index n will give the names of the fields for data constructor n.
     * @param fieldTypesArray the types of the fields.  This should have the same structure as fieldNamesSourceFormArray.
     *   The argument at fieldTypesArray[i][j] will be the type of the argument corresponding to fieldNamesSourceFormArray[i][j].
     * @return the source model for the corresponding type constructor.
     */
    private static SourceModel.TypeConstructorDefn getTypeConstructorDefinition(
            String[][] fieldNamesSourceFormArray, SourceModel.TypeExprDefn[][] fieldTypesArray) {
        
        int nDCDefs = fieldNamesSourceFormArray.length;
        DataConsDefn[] dcDefs = new DataConsDefn[nDCDefs];
        
        for (int i = 0; i < nDCDefs; i++) {
            String[] fieldNamesSourceForm = fieldNamesSourceFormArray[i];
            
            // Create the field names.
            FieldName[] fieldNames = new FieldName[fieldNamesSourceForm.length];
            for (int j = 0; j < fieldNames.length; j++) {
                fieldNames[j] = FieldName.make(fieldNamesSourceForm[j]);
            }
            
            // Create the type arguments.
            DataConsDefn.TypeArgument[] typeArguments = new DataConsDefn.TypeArgument[fieldNames.length];
            for (int j = 0; j < fieldNames.length; j++) {
                typeArguments[j] = DataConsDefn.TypeArgument.make(SourceModel.Name.Field.make(fieldNames[j]), fieldTypesArray[i][j], false);
            }
            
            // Create the data constructor definition.
            String dcName = DEFAULT_DATA_CONS_NAME + i;
            dcDefs[i] = DataConsDefn.make(dcName, Scope.PRIVATE, typeArguments);
        }

        // Create the type constructor with the data cons definition.
        SourceModel.TypeConstructorDefn typeConsDefn = 
            SourceModel.TypeConstructorDefn.AlgebraicType.make(DEFAULT_DATA_CONS_NAME, Scope.PRIVATE, null, 
                                                               dcDefs, SourceModel.TypeConstructorDefn.NO_DERIVING_CLAUSE);
        
        return typeConsDefn;
    }
    
    /**
     * Check for an error when a case alt unpack pattern on ints uses the same int.
     */
    public void testIntCaseCollision() {
        {
            String[] outerDefnText = {
                    "testIntCase x =        ",
                    "    case x of          ",
                    "   (1 | 2)     -> " + Prelude + ".True;",
                    "    1          -> " + Prelude + ".True;",
                    "    ;"
            };
            
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
        {
            String[] outerDefnText = {
                    "testIntCase x =        ",
                    "    case x of          ",
                    "   (1 | 1)     -> " + Prelude + ".True;",
                    "    ;"
            };
            
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
        {
            String[] outerDefnText = {
                    "testIntCase x =",
                    "    case x of",
                    "    -0         -> " + Prelude + ".True;",
                    "    0          -> " + Prelude + ".True;",
                    "    ;"
            };

            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
    }
    
    /**
     * Check for an error when a case alt unpack pattern on ints uses an integer literal out of range of the Int type.
     */
    public void testIntLiteralOutOfRange() {
        {
            String[] outerDefnText = {
                    "testIntCase x =",
                    "    case x of",
                    "    - 1873468172648912649812764912837643       -> " + Prelude + ".True;",
                    "    ;"
            };
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.IntLiteralOutOfRange.class);
        }
    }
    
    /**
     * Check for an error when a case alt unpack pattern on chars uses the same char.
     */
    public void testCharCaseCollision() {
        {
            String[] outerDefnText = {
                    "testCharCase x =   ",
                    "    case x of      ",
                    "    ('a' | 'b')  -> " + Prelude + ".True;",
                    "     'a'         -> " + Prelude + ".True;",
                    "     ;"
            };
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
        {
            String[] outerDefnText = {
                    "testCharCase x =   ",
                    "    case x of      ",
                    "    ('a' | 'a')  -> " + Prelude + ".True;",
                    "    ; "
            };
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
        {
            String[] outerDefnText = {
                    "testCharCase x =   ",
                    "    case x of      ",
                    "    '\045'       -> " + Prelude + ".True;",
                    "    '%'          -> " + Prelude + ".True;",
                    "    ; "
            };
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
        {
            String[] outerDefnText = {
                    "testCharCase x =   ",
                    "    case x of      ",
                    "    '\u0025'     -> " + Prelude + ".True;",
                    "    '%'          -> " + Prelude + ".True;",
                    "    ; "
            };
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
        {
            String[] outerDefnText = {
                    "testCharCase x =   ",
                    "    case x of      ",
                    "    '\045'       -> " + Prelude + ".True;",
                    "    '\u0025'     -> " + Prelude + ".True;",
                    "    ; "
            };
            checkDefnForExpectedError(outerDefnText, MessageKind.Error.RepeatedPatternValueInCaseExpression.class);
        }
        
    }
}
