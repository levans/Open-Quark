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
 * LocalPatternMatch_Test.java
 * Created: Mar 22, 2007
 * By: JoWong
 */

package org.openquark.cal.compiler;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;

/**
 * A set of time-consuming JUnit test cases for the local pattern match syntax.
 *
 * @author Joseph Wong
 */
public class LocalPatternMatch_Test extends TestCase {

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

        TestSuite suite = new TestSuite(LocalPatternMatch_Test.class);

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
     * Tests the error message for when a local pattern match declaration is used with a multiple data consturctor pattern.
     */
    public void testInvalidLocalPatternMatchMultipleDataConsPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let (Prelude.Left|Prelude.Right) {value} = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchMultipleDataConsPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with an empty list [] pattern.
     */
    public void testInvalidLocalPatternMatchNilPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let [] = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchNilPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with a unit () pattern.
     */
    public void testInvalidLocalPatternMatchUnitPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let () = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchUnitPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with an integer pattern.
     */
    public void testInvalidLocalPatternMatchIntPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let 42 = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchIntPattern.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let -42 = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchIntPattern.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let (-42|42) = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchIntPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with a character pattern.
     */
    public void testInvalidLocalPatternMatchCharPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let 'X' = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchCharPattern.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let ('X'|'Y') = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchCharPattern.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo17 = let 'c' 1 = 4; in 3;",
            MessageKind.Error.InvalidLocalPatternMatchCharPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with just a wildcard pattern.
     */
    public void testInvalidLocalPatternMatchWildcardPattern() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let _ = Prelude.undefined; in 3.0;",
            MessageKind.Error.InvalidLocalPatternMatchWildcardPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with a pattern that contains no pattern variables.
     */
    public void testLocalPatternMatchDeclMustContainAtLeastOnePatternVar() {
        // data cons pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Just {} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Just _ = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Just {value=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons _ _ = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons {head=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons {tail=_, head=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        // colon pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let _:_ = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        // tuple pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let (_, _) = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let (_, _, _, _, _) = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        // record pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {_|} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {_|a=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {a=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {#1=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {_|a=_, #1=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {a=_, #1=_} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        // punned ordinal fields become wildcards
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {#1} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {#1, #2} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with a base record pattern that is not the wildcard pattern.
     */
    public void testNonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {r|a} = Prelude.undefined; in 3.0;",
            MessageKind.Error.NonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {r|#1=x} = Prelude.undefined; in 3.0;",
            MessageKind.Error.NonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {r|a, #1=x} = Prelude.undefined; in 3.0;",
            MessageKind.Error.NonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with a data constructor pattern with the wrong number of
     * positional patterns.
     */
    public void testConstructorMustHaveExactlyNArgsInLocalPatternMatchDecl() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Just = Prelude.undefined; in 3.0;",
            MessageKind.Error.ConstructorMustHaveExactlyNArgsInLocalPatternMatchDecl.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons _ = Prelude.undefined; in 3.0;",
            MessageKind.Error.ConstructorMustHaveExactlyNArgsInLocalPatternMatchDecl.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration is used with a data constructor pattern where the data constructor
     * has an arity of 0.
     */
    public void testConstructorMustHavePositiveArityInLocalPatternMatchDecl() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Nil = Prelude.undefined; in 3.0;",
            MessageKind.Error.ConstructorMustHavePositiveArityInLocalPatternMatchDecl.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a data constructor local pattern match declaration using matching notation attempts to bind a field which doesn't exist.
     */
    public void testUnknownDataConstructorField() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo18 = let Prelude.Cons {foo} = [1]; in foo;",
            MessageKind.Error.UnknownDataConstructorField.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration contains a binds a pattern variable more than once.
     */
    public void testRepeatedVariableUsedInBinding() {
        // data cons pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons x x = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedVariableUsedInBinding.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons {head=x, tail=x} = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedPatternVariableInFieldBindingPattern.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons {head, tail=head} = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedVariableUsedInBinding.class, leccCALServices);

        // colon pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let x:x = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedVariableUsedInBinding.class, leccCALServices);
        
        // tuple pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let (x, x) = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedVariableUsedInBinding.class, leccCALServices);

        // record pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {x, y=x} = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedPatternVariableInFieldBindingPattern.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let {#3=x, y=x} = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedPatternVariableInFieldBindingPattern.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a local pattern match declaration binds a pattern variable that is also defined
     * elsewhere in the let declaration.
     */
    public void testRepeatedDefinitionInLetDeclaration() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let (x, y) = Prelude.undefined; x = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedDefinitionInLetDeclaration.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let x = Prelude.undefined; (x, y) = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedDefinitionInLetDeclaration.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let x:z = Prelude.undefined; (x, y) = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedDefinitionInLetDeclaration.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let Prelude.Cons x z = Prelude.undefined; {x, y} = Prelude.undefined; in 3.0;",
            MessageKind.Error.RepeatedDefinitionInLetDeclaration.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when the type of a local pattern-bound variable is not compatible with the defining expression of the local pattern match declaration.
     */
    public void testTypeOfPatternBoundVariableNotCompatibleWithLocalPatternMatchDecl() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo2 = let a:b = [Prelude.fromJust b]; in a;",
            MessageKind.Error.TypeOfPatternBoundVariableNotCompatibleWithLocalPatternMatchDecl.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when the type of the defining expression of the local pattern match declaration is not compatible with the type(s) of the corresponding pattern-bound variable(s).
     */
    public void testTypeOfDesugaredDefnOfLocalPatternMatchDeclNotCompatibleWithPatternBoundVars() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo19 = let {ax, b2} = (b2, ax); in (ax, b2);",
            MessageKind.Error.TypeOfDesugaredDefnOfLocalPatternMatchDeclNotCompatibleWithPatternBoundVars.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when the type the defining expression of this local pattern match declaration does not have all and only the declared fields.
     */
    public void testLocalPatternMatchDeclMustHaveFields() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo6 = let {a} = {a = \"foo\", b = \"bar\"}; in a;",
            MessageKind.Error.LocalPatternMatchDeclMustHaveFields.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo6a = let {x} = {a = \"foo\", b = \"bar\"}; in x;",
            MessageKind.Error.LocalPatternMatchDeclMustHaveFields.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo7b = let {#1 = a} = ('a', 'b', 'c'); in a;",
            MessageKind.Error.LocalPatternMatchDeclMustHaveFields.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when the type the defining expression of this local pattern match declaration does not have at least the declared fields.
     */
    public void testLocalPatternMatchDeclMustAtLeastHaveFields() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo6x = let {_|#1,a} = {a = \"foo\"}; in a;",
            MessageKind.Error.LocalPatternMatchDeclMustAtLeastHaveFields.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when the type of the defining expression of this local pattern match declaration is not a tuple type with the declared dimension.
     */
    public void testLocalPatternMatchDeclMustHaveTupleDimension() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo7 = let (a, b) = ('a', 'b', 'c'); in a;",
            MessageKind.Error.LocalPatternMatchDeclMustHaveTupleDimension.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo7a = let {#1 = a, #2 = b} = ('a', 'b', 'c'); in a;",
            MessageKind.Error.LocalPatternMatchDeclMustHaveTupleDimension.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo7c = let (a, b, c, d) = ('a', 'b', 'c'); in a;",
            MessageKind.Error.LocalPatternMatchDeclMustHaveTupleDimension.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a field declared in a local pattern match declaration is missing from the type of the defining expression.
     */
    public void testInvalidRecordFieldInLocalPatternMatchDecl() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo6b = let {_|y} = {a = \"foo\", b = \"bar\"}; in y;",
            MessageKind.Error.InvalidRecordFieldInLocalPatternMatchDecl.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo7c = let (a, b, c, d) = ('a', 'b', 'c'); in a;",
            MessageKind.Error.InvalidRecordFieldInLocalPatternMatchDecl.class, leccCALServices);

        CompilerTestUtilities.checkDefnForExpectedError(
            "foo7d = let {a, b} = {a = 'a'}; in a;",
            MessageKind.Error.InvalidRecordFieldInLocalPatternMatchDecl.class, leccCALServices);
    }
    
    /**
     * Tests that a non-polymorphic record pattern (or a tuple pattern) is able to constrain the type of the RHS.
     */
    public void testNonPolymorphicRecordPatternConstrainingTypeOfRHS() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "test = (\\t -> let (a,b) = t; in (a,b)) (1.0, 2.0, 3.0);",
            MessageKind.Error.TypeErrorDuringApplication.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "test = (\\r -> let {a} = r; in a) {a='a', b='b'};",
            MessageKind.Error.TypeErrorDuringApplication.class, leccCALServices);
    }
    
    /**
     * Tests that a polymorphic record pattern is able to constrain the type of the RHS.
     */
    public void testPolymorphicRecordPatternConstrainingTypeOfRHS() {
        CompilerTestUtilities.checkDefnForExpectedError(
            "test = (\\r -> let {_|#1,#2,a} = r; in a) ('x', 'y');",
            MessageKind.Error.TypeErrorDuringApplication.class, leccCALServices);
        
        CompilerTestUtilities.checkDefnForExpectedError(
            "test = (\\r -> let {_|#1,a} = r; in a) {a='a'};",
            MessageKind.Error.TypeErrorDuringApplication.class, leccCALServices);
    }
    
    /**
     * Tests the error message for when a CALDoc comment is associated with a local pattern match declaration, which is not allowed.
     */
    public void testLocalPatternMatchDeclCannotHaveCALDocComment() {
        // data cons pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let /** foo */ Prelude.Cons _ x = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclCannotHaveCALDocComment.class, leccCALServices);

        // colon pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let /** foo */ _:y = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclCannotHaveCALDocComment.class, leccCALServices);

        // tuple pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let /** foo */ (_, _, z) = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclCannotHaveCALDocComment.class, leccCALServices);

        // record pattern
        CompilerTestUtilities.checkDefnForExpectedError(
            "foo = let /** foo */ {_|a} = Prelude.undefined; in 3.0;",
            MessageKind.Error.LocalPatternMatchDeclCannotHaveCALDocComment.class, leccCALServices);
    }
}
