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
 * CALMessageResourceBundle.java
 * Created: Apr 21, 2005
 * By: Peter Cardwell
 */

package org.openquark.cal.compiler;
import java.util.ListResourceBundle;

import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Core.CAL_Record;


/**
 * A resource bundle for messages in the CAL compiler package.
 * 
 * The keys for the resource strings are programatically generated from the class names of
 * MessageKinds because in the Release build these names will be obfuscated. This scheme will
 * ensure that no matter what the MessageKinds are renamed to by the obfuscation process, they will
 * match the keys in the resource bundle below. 
 * 
 * @author Peter Cardwell
 */
public class CALMessageResourceBundle extends ListResourceBundle {
    @Override
    public Object[][] getContents() {
        return contents;
    }
    private Object[][] contents = {
            
    { MessageKind.getClassName(MessageKind.Error.AttemptedRedefinitionOfModule.class), 
        "Attempted redefinition of module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToImportModuleIntoItself.class), 
        "Attempt to import a module {0} into itself."  } ,

    { MessageKind.getClassName(MessageKind.Error.BadASTTree.class), 
        "Bad parse tree."  } ,
       
    { MessageKind.getClassName(MessageKind.Error.SyntaxError.class), 
        "Syntax error."  } ,

    { MessageKind.getClassName(MessageKind.Error.SyntaxErrorWithParaphrase.class), 
        "Syntax error. Encountered ''{1}'' while trying to parse {0} (starting from token ''{3}''). Expected one of: [{2}]. "} ,

    { MessageKind.getClassName(MessageKind.Error.SyntaxErrorWithParaphraseSingleExpected.class), 
        "Syntax error. Encountered ''{1}'' while trying to parse {0} (starting from token ''{3}''). Expected {2}. "} ,

    { MessageKind.getClassName(MessageKind.Error.SyntaxErrorWithSuggestion.class), 
        "Syntax error. Unexpected token ''{0}''.  Was {1} intended?"  } ,

    { MessageKind.getClassName(MessageKind.Error.BadTokenStream.class), 
        "Bad token stream."  } ,

    { MessageKind.getClassName(MessageKind.Info.UnableToRecover.class), 
        "Not able to recover from previous compilation error(s)."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.InternalCodingError.class), 
        "Aborted due to internal coding error. Please contact Business Objects."  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotReadModuleSource.class), 
        "Could not read source for module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.CyclicDependenciesBetweenModules.class), 
        "Cyclic module dependencies between modules: {0}."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.CyclicClassContextDependenciesBetweenClasses.class), 
        "Cyclic class context dependencies between classes: {0}."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.ErrorWhilePackagingModule.class), 
        "Error while packaging module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.FailedValidationASTParse.class), 
        "Failed validation AST parse: {0}"  } ,

    { MessageKind.getClassName(MessageKind.Error.ModuleCouldNotBeParsed.class), 
        "Module {0} could not be parsed."  } ,

    { MessageKind.getClassName(MessageKind.Error.CannotLoadSourcelessModuleMaybeInvalidCar.class), 
        "The CMI file of the sourceless module {0} could not be loaded. The Car file containing this module may be invalid."  } ,

    { MessageKind.getClassName(MessageKind.Error.CannotLoadSourcelessModuleDependeeNotFound.class), 
        "The CMI file of the sourceless module {0} could not be loaded because the imported module {1} cannot be found."  } ,

    { MessageKind.getClassName(MessageKind.Error.CannotLoadSourcelessModuleOlderThanDependee.class), 
        "The CMI file of the sourceless module {0} could not be loaded because it is out-of-date with respect to the imported module {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidSourcelessModule.class), 
        "The CMI file of the sourceless module {0} could not be loaded."  } ,

    { MessageKind.getClassName(MessageKind.Error.ModuleCouldNotBeRead.class), 
        "Module {0} could not be read."  } ,

    { MessageKind.getClassName(MessageKind.Error.ModuleNameDoesNotCorrespondToSourceName.class), 
        "Module name {0} does not correspond to source name: {1}."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.ModuleNotInWorkspace.class), 
        "Module {0} is not in the workspace."  } ,

    { MessageKind.getClassName(MessageKind.Error.ModuleNotWriteable.class), 
        "Module {0} is not writeable."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.MoreThanOneDefinitionInANonRecursiveLet.class), 
        "Internal Coding Error- more than one definition in a non-recursive let."  } ,

    { MessageKind.getClassName(MessageKind.Info.TooManyErrors.class), 
        "Too many errors ({0}), compilation aborted."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToGenerateExpressionTree.class), 
        "Unable to generate expression tree."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToCloseModuleAndPackage.class), 
        "Unable to close module and package: {0}"  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToCloseModule.class), 
        "Unable to close module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.UnliftedLambdaExpression.class), 
        "Internal Coding Error- Unlifted lambda expression."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnresolvedExternalModuleImportWithNoSuggestions.class), 
        "Unresolved external module import {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnresolvedExternalModuleImportWithOneSuggestion.class), 
        "Unresolved external module import {0}. Did you mean {1}?"  } ,

    { MessageKind.getClassName(MessageKind.Error.UnresolvedExternalModuleImportWithMultipleSuggestions.class), 
        "Unresolved external module import {0}. Was one of these intended: {1}?"  } ,

    { MessageKind.getClassName(MessageKind.Error.ModuleMustImportOtherModule.class), 
        "Module {0} must import the {1} module."  } ,

    { MessageKind.getClassName(MessageKind.Error.AmbiguousTypeSignatureInInferredType.class), 
        "Ambiguous type signature in inferred type {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RecordDictionaryMisused.class), 
        CAL_Record.Functions.dictionary.getQualifiedName() + " function may only be called from a directly enclosing function that has exactly 1 record dictionary argument."  } ,

    { MessageKind.getClassName(MessageKind.Error.RecordDictionaryArgumentsInvalid.class), 
        CAL_Record.Functions.dictionary.getQualifiedName() + " function may only be called with a record argument and a string literal argument."  } ,

    { MessageKind.getClassName(MessageKind.Error.DictionaryMethodDoesNotExist.class), 
        "Dictionary for typeclass {0} does not contain method {1}."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.UnableToPackageSuperCombinator.class), 
        "Unable to package supercombinator: {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ConflictingNameInModule.class), 
        "Conflicting name in module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToCreateOutputValueNode.class), 
        "Unable to create an output value node for type {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToRetrieveAnOutputPolicy.class), 
        "Unable to retrieve an output policy for type {0}."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.TreeParsingError.class), 
        "Tree parsing error."  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotParseExpressionInModule.class), 
        "Could not parse expression in module {0}."  } ,


    { MessageKind.getClassName(MessageKind.Error.UnableToParseToIntegerLiteral.class), 
        "Unable to parse {0} to an integer literal."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToParseToFloatingPointLiteral.class), 
        "Unable to parse {0} to a floating point literal."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToParseToCharacterLiteral.class), 
        "Unable to parse {0} to a character literal."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToParseToStringLiteral.class), 
        "Unable to parse {0} to a string literal."  } ,
                
    { MessageKind.getClassName(MessageKind.Error.StringLiteralTooLong.class), 
        "The string literal {0} is too long."  } ,        

    { MessageKind.getClassName(MessageKind.Error.IntLiteralOutOfRange.class), 
        "{0} is outside of range for the Int type.  The valid range is " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE + " (inclusive)."  } ,

    // Deprecation warning messages
    { MessageKind.getClassName(MessageKind.Warning.DeprecatedModule.class), 
        "The module {0} is deprecated."  } ,        
        
    { MessageKind.getClassName(MessageKind.Warning.DeprecatedType.class), 
        "The type {0} is deprecated."  } ,        
        
    { MessageKind.getClassName(MessageKind.Warning.DeprecatedDataCons.class), 
        "The data constructor {0} is deprecated."  } ,        
        
    { MessageKind.getClassName(MessageKind.Warning.DeprecatedTypeClass.class), 
        "The type class {0} is deprecated."  } ,        
        
    { MessageKind.getClassName(MessageKind.Warning.DeprecatedClassMethod.class), 
        "The class method {0} is deprecated."  } ,        
        
    { MessageKind.getClassName(MessageKind.Warning.DeprecatedFunction.class), 
        "The function {0} is deprecated."  } ,        
        
    // Type Checker messages
    { MessageKind.getClassName(MessageKind.Error.TypeErrorDuringApplication.class), 
        "Type Error during an application."  } ,

    { MessageKind.getClassName(MessageKind.Error.CasePatternAndCaseConditionMustHaveSameType.class), 
        "Type Error. The case pattern and the case condition must have the same type."  } ,

    { MessageKind.getClassName(MessageKind.Error.ExpressionDoesNotMatchDataConstructorType.class), 
        "Type Error. The expression does not match the data constructor type."  } ,

    { MessageKind.getClassName(MessageKind.Error.DataConstructorPatternGroupArgumentNotTypeable.class), 
        "Type error. There is no type compatible with argument \"{0}\" for all data constructors in the group pattern."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.UnexpectedUnificationFailure.class), 
        "Internal Coding Error- unexpected unification failure."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnknownDataConstructor.class), 
        "Unknown data constructor {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnknownDataConstructorField.class), 
        "\"{1}\" is not a field of data constructor {0}. Valid field(s): {2}"} ,

    { MessageKind.getClassName(MessageKind.Error.ZeroFieldDataConstructorFieldReference.class), 
        "\"{1}\" is not a field of data constructor {0}. Data constructor {0} has no fields."} ,
        
    { MessageKind.getClassName(MessageKind.Error.ConstructorMustHaveExactlyNArgsInPattern.class), 
        "The data constructor {0} must have exactly {1} pattern {1,choice,0#arguments|1#argument|1<arguments} in its case alternative."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.ConstructorMustHaveExactlyNArgsInLocalPatternMatchDecl.class), 
        "The data constructor {0} must have exactly {1} pattern {1,choice,0#arguments|1#argument|1<arguments}."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.ConstructorMustHavePositiveArityInLocalPatternMatchDecl.class), 
        "The data constructor {0} cannot be used in a local pattern match declaration because it has no fields."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.DataConstructorArgumentsDoNotMatchDataConstructor.class), 
        "Type error. The arguments of the data constructor do not match the data constructor."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypesOfAllCaseBranchesMustBeCompatible.class), 
        "Type error. The types of all the case branches must be compatible."  } ,

    { MessageKind.getClassName(MessageKind.Error.ConditionPartOfIfThenElseMustBeBoolean.class), 
        "Type error. The condition part of an if-then-else must be a Boolean."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeOfThenAndElsePartsMustMatch.class), 
        "Type error. The type of the \"then\" and \"else\" parts of an if-then-else must match."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnknownFunctionOrVariable.class), 
        "Unknown function or variable {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnexpectedTypeClash.class), 
        "Internal coding error- unexpected type clash."  } ,

    { MessageKind.getClassName(MessageKind.Error.AllListElementsMustHaveCompatibleTypes.class), 
        "Type error. All elements of a list must have compatible types."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidRecordSelectionForField.class), 
        "Type error. Invalid record selection for field {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidRecordFieldInLocalPatternMatchDecl.class), 
        "Type error. The field {0} declared in the local pattern match declaration is missing from the type of the defining expression. The type of the expression is {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.LocalPatternMatchDeclMustHaveFields.class), 
        "Type Error. The type of the defining expression of this local pattern match declaration must have all and only the declared fields {1}. The type of the expression is {0}."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.LocalPatternMatchDeclMustAtLeastHaveFields.class), 
        "Type Error. The type of the defining expression of this local pattern match declaration must have at least the declared fields {1}. The type of the expression is {0}."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.LocalPatternMatchDeclMustHaveTupleDimension.class), 
        "Type Error. The type of the defining expression of this local pattern match declaration must be a tuple type with dimension {1}. The type of the expression is {0}."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.InvalidRecordFieldValueUpdate.class), 
        "Type error. Invalid field value update for field {0}."  } ,        
        
    { MessageKind.getClassName(MessageKind.Error.InvalidRecordExtension.class), 
        "Type error. Invalid record extension."  } ,

    { MessageKind.getClassName(MessageKind.Error.DeclaredTypeOfExpressionNotCompatibleWithInferredType.class), 
        "The declared type of the expression is not compatible with its inferred type {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType.class), 
        "The declared type of the local function {0} is not compatible with its inferred type {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.DeclaredTypeOfFunctionNotCompatibleWithInferredType.class), 
        "The declared type of the function {0} is not compatible with its inferred type {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.OrdinalFieldNameOutOfRange.class), 
        "Ordinal field name {0} is out of range. "  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeErrorApplyingOperatorToFirstArgument.class), 
        "Type error applying the operator \"{0}\" to its first argument."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeErrorApplyingOperatorToSecondArgument.class), 
        "Type error applying the operator \"{0}\" to its second argument."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeOfLocalFunctionNotCompatibleWithDefiningExpression.class), 
        "The type of the local function {0} is not compatible with its defining expression."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.TypeOfPatternBoundVariableNotCompatibleWithLocalPatternMatchDecl.class), 
        "The type of the local pattern-bound variable {0} is not compatible with the defining expression of the local pattern match declaration."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.TypeOfDesugaredDefnOfLocalPatternMatchDeclNotCompatibleWithPatternBoundVars.class), 
        "The type of the defining expression of the local pattern match declaration is not compatible with the type(s) of the corresponding pattern-bound variable(s)."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.TypeOfFunctionNotCompatibleWithDefiningExpression.class), 
        "The type of the function {0} is not compatible with its defining expression."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.ExplicitTypeableInstance.class), 
        "Explicit instance declarations for the " + CAL_Prelude.TypeClasses.Typeable.getQualifiedName() + " type class are not allowed."  } ,        
  
    { MessageKind.getClassName(MessageKind.Error.TypeConstructorAppliedToOverlyManyArgs.class), 
        "The type constructor {0} expects at most {1} type argument(s). {2} supplied."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.InstanceDeclarationKindClash.class), 
        "Invalid instance declaration; the kind of the type class {0} (i.e. {1}) is not the same as the kind of the application of the type constructor {2} to {3} type arguments (i.e. {4})."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.InstanceDeclarationKindClashInConstraint.class), 
        "Invalid instance declaration; the kind of the type variable {0} (i.e. {1}) is not the same as the kind of the constraining type class {2} (i.e. {3})." } ,          

    { MessageKind.getClassName(MessageKind.Error.RecordInstanceDeclarationKindClash.class), 
        "Invalid record instance declaration; the kind of the type class {0} (i.e. {1}) is not equal to the kind *." } ,
                        
    { MessageKind.getClassName(MessageKind.Error.ExtensionFieldsLacksConstraintsOnRecordVariable.class), 
        "The extension fields {0} must be lacks constraints on the record variable."  } ,

    { MessageKind.getClassName(MessageKind.Error.UndefinedTypeClass.class), 
        "Undefined type class {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RecordVariableNotUsedInTypeSignature.class), 
        "Record variable {0} is not used in the type signature."  } ,

    { MessageKind.getClassName(MessageKind.Error.VariableNotUsedInTypeSignature.class), 
        "Variable {0} is not used in the type signature."  } ,

    { MessageKind.getClassName(MessageKind.Error.ClassTypeVarInMethodContext.class), 
        "The class type variable {0} cannot be used in a class method context."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.ClassMethodMustUseClassTypeVariable.class), 
        "The class method {0} must use the type class type variable {1} in its type signature." } ,        

    { MessageKind.getClassName(MessageKind.Error.KindErrorInTypeDeclarationForFunction.class), 
        "Kind error in the type declaration for the function {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.KindErrorInTypeDeclarationForClassMethod.class), 
        "Kind error in the type declaration for the class method {0}."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.KindErrorInClassConstraints.class), 
        "The kinds of all classes constraining the type variable ''{0}'' must be the same. Class {1} has kind {2} while class {3} has kind {4}."  } ,        

    { MessageKind.getClassName(MessageKind.Error.KindErrorInTypeSignature.class), 
        "Kind error in the type signature."  } ,

    { MessageKind.getClassName(MessageKind.Error.RecordVariableAlreadyUsedAsTypeVariable.class), 
        "The record variable {0} is already used as a type variable."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeVariableAlreadyUsedAsRecordVariable.class), 
        "The type variable {0} is already used as a record variable."  } ,

    { MessageKind.getClassName(MessageKind.Error.NameAlreadyUsedAsFunctionName.class), 
        "The name {0} is already used as a function name in this module."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.CouldNotCreateModuleInPackage.class), 
        "Could not create a module {0} in the package."  } ,

    { MessageKind.getClassName(MessageKind.Info.LocalOverloadedPolymorphicConstant.class), 
        "Efficiency warning: local overloaded polymorphic constant {0} defined in {1} of type {2}."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidPrimitiveFunction.class), 
        "Invalid primitive function {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToRedeclarePrimitiveFunction.class), 
        "Attempt to redeclare the primitive function {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToRedefinePrimitiveFunction.class), 
        "Attempt to redefine the built-in primitive function {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.NoClassLoaderProvided.class), 
        "No classloader provided for module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ForeignDeclarationForBuiltinPrimitiveFunction.class), 
        "Foreign declaration for built-in primitive function {0}."  } ,
        
        
    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionDeclarationInvalidCalType.class), 
        "The type signature is incompatible with the ''{0}'' external descriptor."  } ,                        
        
    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionDeclarationMustHaveExactlyNArgs.class), 
        "A foreign function declaration for ''{0}'' must have exactly {1} argument(s)."  } , 
        
    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionDeclarationReferenceTypeExpected.class), 
        "The implementation type must be a Java reference type and not the Java primitive type ''{0}''."  } ,         
                   
    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionDeclarationInvalidCast.class), 
        "Cannot cast the Java type ''{0}'' to the Java type ''{1}''."} ,  
                   
    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionDeclarationMustReturnBoolean.class), 
        "A foreign function declaration for ''{0}'' must return " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + " (or a foreign type with Java implementation type boolean)."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionDeclarationInvalidInstanceOf.class), 
        "If ''expr'' has Java type ''{0}'', then ''expr instanceof {1}'' is an invalid Java expression."  } ,        
        
    { MessageKind.getClassName(MessageKind.Error.InvalidJavaTypeChangeOnLoading.class), 
        "The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."} ,          

    { MessageKind.getClassName(MessageKind.Error.AttemptToRedefineFunction.class), 
        "Attempt to redefine {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeDeclarationForBuiltInPrimitiveFuncton.class), 
        "Type declaration for built-in primitive function {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeDeclarationForForeignFunction.class), 
        "Type declaration for foreign function {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.DefinitionMissing.class), 
        "The definition of {0} is missing."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnboundTypeVariable.class), 
        "Unbound type variable {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ContextTypeVariableUndefined.class), 
        "The context type variable {0} is undefined. It must be {1}."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.TypeClassCannotIncludeItselfInClassConstraintList.class), 
        "The type class {0} cannot include itself in its list of class constraints."  } ,


    { MessageKind.getClassName(MessageKind.Error.RepeatedClassConstraint.class), 
        "Repeated class constraint {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedClassConstraintOnTypeVariable.class), 
        "Repeated class constraint {0} on the type variable {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedClassConstraintOnRecordVariable.class), 
        "Repeated class constraint {0} on the record variable {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedDefinitionInLetDeclaration.class), 
        "Repeated definition of {0} in let declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidLocalPatternMatchWildcardPattern.class), 
        "A local pattern match declaration cannot be used with just the wildcard pattern ''_''."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidLocalPatternMatchNilPattern.class), 
        "The empty list pattern ''[]'' cannot be used in a local pattern match declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidLocalPatternMatchUnitPattern.class), 
        "The unit pattern ''()'' cannot be used in a local pattern match declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidLocalPatternMatchMultipleDataConsPattern.class), 
        "Patterns with multiple data constructors cannot be used in a local pattern match declaration."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar.class), 
        "A local pattern match declaration must contain at least one pattern variable."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidLocalPatternMatchIntPattern.class), 
        "Integer patterns cannot be used in a local pattern match declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidLocalPatternMatchCharPattern.class), 
        "Character patterns cannot be used in a local pattern match declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.NonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl.class), 
        "Only the wildcard pattern ''_'' can be used as the base record pattern in a local pattern match declaration."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.RepeatedDefinitionOfClass.class), 
        "Repeated definition of the class {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedDefinitionOfClassMethod.class), 
        "Repeated definition of the class method {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedDefinitionOfDataConstructor.class), 
        "Repeated definition of the data constructor {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedDefinitionOfForeignType.class), 
        "Repeated definition of the foreign type {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedDefinitionOfType.class), 
        "Repeated definition of the type {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedFieldNameInDataConstructorDeclaration.class), 
        "Repeated field name {0} in data constructor declaration."  } ,
    
    { MessageKind.getClassName(MessageKind.Error.RepeatedFieldNameInRecordInstanceDeclaration.class), 
        "Repeated field name {0} in record instance declaration."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.FieldValueUpdateOperatorUsedInRecordLiteralValue.class), 
        "The field value update operator ':=' can not be used in a record literal value."  } ,        

    { MessageKind.getClassName(MessageKind.Error.RepeatedFieldNameInRecordLiteralValue.class), 
        "Repeated field name {0} in record literal value."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedFieldNameInFieldBindingPattern.class), 
        "Repeated field name {0} in field binding pattern."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedImportOfModule.class), 
        "Repeated import of module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedLacksFieldConstraintOnRecordVariable.class), 
        "Repeated lacks field constraint {0} on the record variable {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedOccurrenceOfFieldNameInRecordType.class), 
        "Repeated occurrence of field name {0} in record type."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedPatternInCaseExpression.class), 
        "Repeated pattern {0} in case expression."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedPatternValueInCaseExpression.class), 
        "Repeated pattern value {0} in case expression."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedPatternVariableInFieldBindingPattern.class), 
        "Repeated pattern variable {0} in field binding pattern. "  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedTypeDeclaration.class), 
        "Repeated type declaration for {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedTypeVariable.class), 
        "Repeated type variable {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedTypeVariableInInstanceDeclaration.class), 
        "Repeated type variable {0} in instance declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.RepeatedVariableUsedInBinding.class), 
        "Repeated variable {0} used in binding."  } ,
        
        
    { MessageKind.getClassName(MessageKind.Error.RepeatedFriendModuleDeclaration.class),
        "Repeated friend declaration for module {0}." },
        
    { MessageKind.getClassName(MessageKind.Error.ModuleCannotBeFriendOfItself.class),
        "This friend declaration occurs in module {0}. A module cannot be a friend of itself." },
        
    { MessageKind.getClassName(MessageKind.Error.ModuleCannotBeFriendOfImport.class),
        "The module {0} directly or indirectly imports module {1}. A module cannot have direct or indirect imports as friends."},     
          

    { MessageKind.getClassName(MessageKind.Error.DataConstructorNotDefinedInModule.class), 
        "The data constructor {0} is not defined in module {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.FunctionNotDefinedInModule.class), 
        "The function {0} is not defined in module {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeClassNotDefinedInModule.class), 
        "The type class {0} is not defined in module {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeConstructorNotDefinedInModule.class), 
        "The type constructor {0} is not defined in module {1}."  } ,
 
        
    { MessageKind.getClassName(MessageKind.Error.ClassMethodNameAlreadyUsedInImportUsingDeclaration.class), 
        "The class method name {0} is already used in the \"import {1} using function\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.DataConstructorAlreadyUsedInImportUsingDeclaration.class), 
        "The data constructor {0} is already used in the \"import {1} using dataConstructor\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.DataConstructorNameAlreadyUsedInImportUsingDeclaration.class), 
        "The data constructor name {0} is already used in the \"import {1} using dataConstructor\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionNameAlreadyUsedInImportUsingDeclaration.class), 
        "The foreign function name {0} is already used in the \"import {1} using function\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.ForeignTypeAlreadyUsedInImportUsingDeclaration.class), 
        "The foreign type name {0} is already used in the \"import {1} using typeConstructor\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.FunctionAlreadyUsedInImportUsingDeclaration.class), 
        "The function {0} is already used in the \"import {1} using function\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.FunctionNameAlreadyUsedInImportUsingDeclaration.class), 
        "The function name {0} is already used in the \"import {1} using function\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeClassAlreadyUsedInImportUsingDeclaration.class), 
        "The type class {0} is already used in the \"import {1} using typeClass\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeClassNameAlreadyUsedInImportUsingDeclaration.class), 
        "The type class name {0} is already used in the \"import {1} using typeClass\" declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeConstructorAlreadyUsedInImportUsingDeclaration.class), 
        "The type constructor {0} is already used in the \"import {1} using typeConstructor\" declaration."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.RepeatedTypeClassNameInDerivingClause.class), 
        "Repeated type class name {0} in deriving clause."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.UnsupportedTypeClassNameInDerivingClause.class), 
        "The type class {0} cannot be used in this deriving clause."  } ,        

    { MessageKind.getClassName(MessageKind.Error.TypeClassInDerivingClauseRequiresEnumerationType.class), 
        "The type class {0} in the deriving clause requires all data constructors to have arity 0."  } ,        

    { MessageKind.getClassName(MessageKind.Error.TypeClassInDerivingClauseRequiresNonPolymorphicType.class), 
        "The type class {0} in the deriving clause requires the data type to be non-polymorphic."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeClassInDerivingClauseRequiresPrimitiveOrImplementedInterface.class),    
        "The type class {0} in the deriving clause requires that the foreign type being imported either implement {1} or be a Java primitive" },    

    // Type checker messages - CALDoc
    { MessageKind.getClassName(MessageKind.Error.TooManyArgTagsInCALDocComment.class), 
        "There are too many @arg tags in this CALDoc comment."  },
        
    { MessageKind.getClassName(MessageKind.Error.InvalidArgNameInCALDocComment.class), 
        "The name {0} appearing in this @arg tag is not a valid argument name."  },
        
    { MessageKind.getClassName(MessageKind.Error.ArgNameDoesNotMatchDeclaredNameInCALDocComment.class), 
        "The name {0} appearing in this @arg tag does not match the declared name {1}."  },
        
    { MessageKind.getClassName(MessageKind.Error.SingletonTagAppearsMoreThanOnceInCALDocComment.class), 
        "The {0} tag cannot appear more than once in a CALDoc comment."  },
        
    { MessageKind.getClassName(MessageKind.Error.DisallowedTagInCALDocComment.class), 
        "The {0} tag cannot be used in this CALDoc comment."  },
        
    { MessageKind.getClassName(MessageKind.Error.UnrecognizedTagInCALDocComment.class), 
        "The {0} tag is not a recognized CALDoc tag."  },
        
    { MessageKind.getClassName(MessageKind.Error.UnrecognizedInlineTagInCALDocComment.class), 
        "The {0} tag is not a recognized CALDoc inline tag."  },
        
    { MessageKind.getClassName(MessageKind.Error.MissingSeeOrLinkBlockContextInCALDocComment.class), 
        "This CALDoc @see/@link block is missing a context declaration before the ''=''."  },
        
    { MessageKind.getClassName(MessageKind.Error.UnrecognizedSeeOrLinkBlockContextInCALDocComment.class), 
        "{0} is not a recognized context for a CALDoc @see/@link block."  },
        
    { MessageKind.getClassName(MessageKind.Error.UnrecognizedSeeOrLinkBlockReferenceInCALDocComment.class), 
        "{0} is an invalid reference in this CALDoc @see/@link block."  },
        
    { MessageKind.getClassName(MessageKind.Error.CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment.class), 
        "The CALDoc reference {0} can be resolved to more than one entity. Use the full form with the context keyword to disambiguate, as one of: {1}."  },
        
    { MessageKind.getClassName(MessageKind.Error.UncheckedCrossReferenceIsAmbiguousInCALDocComment.class), 
        "The CALDoc unchecked reference {0} is ambiguous. Use the full form with the context keyword (one of ''module'', ''typeClass'', ''typeConstructor'', ''dataConstructor'') to disambiguate."  },
        
    { MessageKind.getClassName(MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocComment.class), 
        "The CALDoc checked reference {0} cannot be resolved."  },
        
    { MessageKind.getClassName(MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion.class), 
        "The CALDoc checked reference {0} cannot be resolved. Was ''{1}'' intended?"  },
        
    { MessageKind.getClassName(MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithMultipleSuggestions.class), 
        "The CALDoc checked reference {0} cannot be resolved. Was one of these intended: {1}?"  },
        
    { MessageKind.getClassName(MessageKind.Error.UnassociatedCALDocComment.class), 
        "This CALDoc comment is not associated with any declaration."  },
        
    { MessageKind.getClassName(MessageKind.Error.CALDocCommentCannotAppearHere.class), 
        "A CALDoc comment cannot appear here."  },
        
    { MessageKind.getClassName(MessageKind.Error.LocalPatternMatchDeclCannotHaveCALDocComment.class), 
        "A local pattern match declaration cannot have a CALDoc comment. However, type declarations for the pattern-bound variables can have associated CALDoc comments."  },
        
    { MessageKind.getClassName(MessageKind.Error.CALDocCommentForAlgebraicFunctionMustAppearBeforeTypeDeclaration.class), 
        "The CALDoc comment for the {0} function must appear immediately before its associated type declaration."  },
        
    { MessageKind.getClassName(MessageKind.Error.ParagraphBreakCannotAppearHereInsideCALDocCommentInlineTag.class), 
        "A paragraph break cannot appear here inside the {0} inline tag."  },
        
    { MessageKind.getClassName(MessageKind.Error.InlineTagCannotAppearHereInsideCALDocCommentInlineTag.class), 
        "An inline tag cannot appear here, within the context of the {0} inline tag."  },
        
    { MessageKind.getClassName(MessageKind.Error.ThisParticularInlineTagCannotAppearHereInsideCALDocCommentInlineTag.class), 
        "The inline tag {0} cannot appear here, within the context of the {1} inline tag."  },
        
    { MessageKind.getClassName(MessageKind.Error.InlineTagBlockMissingClosingTagInCALDocComment.class), 
        "This inline tag is missing a corresponding closing tag ''@}''."  },
        
    // Class Instance Checker messages
    { MessageKind.getClassName(MessageKind.Error.InstanceHasMultipleVisibleDefinitions.class), 
        "The instance {0} has multiple visible definitions. One is in module {1} and the other is in module {2}."  } ,

    { MessageKind.getClassName(MessageKind.Error.InstancesOverlap.class), 
        "The instance {0} defined in module {1} overlaps with the instance {2} defined in module {3}."  } ,

    { MessageKind.getClassName(MessageKind.Error.SuperclassInstanceDeclarationMissing.class), 
        "The instance {0} must be defined (or visible) when defining the instance {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ConstraintsOnInstanceDeclarationMustImplyConstraintsOnParentInstance.class), 
        "The constraints on the instance declaration {0} must imply the constraints on the parent instance declaration {1}.\n In particular, the class constraint {2} on type variable number {3} in the parent instance is not implied."  } ,

    { MessageKind.getClassName(MessageKind.Error.MethodDefinedMoreThanOnce.class), 
        "The method {0} is defined more than once by the instance {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.MethodNotDeclaredByClass.class), 
        "The method {0} is not declared by the class {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.MethodNotDefinedByInstance.class), 
        "The method {0} is not defined by the instance {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeVariableMustOccurWithingTypeVariableArgumentsOfInstanceDeclaration.class), 
        "The type variable \"{0}\" must occur within the type variable arguments of the instance declaration and not only the context."  } ,

    { MessageKind.getClassName(MessageKind.Error.RecordInstancesOverlap.class), 
        "The instance {0} overlaps with the record instance {1} defined in module {2}."  } ,

    { MessageKind.getClassName(MessageKind.Error.DuplicateConstraint.class), 
        "Duplicate constraint {0} {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.InstanceAlreadyDefined.class), 
        "The instance {0} has been defined already in module {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.InstanceOverlapsWithInstanceInModule.class), 
        "The instance {0} overlaps with the instance {1} defined in module {2}."  } ,

    { MessageKind.getClassName(MessageKind.Error.FunctionNotDefinedInCurrentModule.class), 
        "The function {0} is not defined in the current module."  } ,

    { MessageKind.getClassName(MessageKind.Error.FunctionDoesNotExist.class), 
        "The function {0} does not exist or is not visible in the current module."  } ,

    { MessageKind.getClassName(MessageKind.Error.FunctionIsClassMethodNotResolvingFunction.class), 
        "The function {0} is a class method and thus cannot be a resolving function."  } ,


    { MessageKind.getClassName(MessageKind.Error.AmbiguousClassReference.class), 
        "Attempt to use undefined class ''{0}''. Was one of these intended: {1}?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AmbiguousDataConstructorReference.class), 
        "Attempt to use undefined data constructor ''{0}''. Was one of these intended: {1}?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AmbiguousFunctionReference.class), 
        "Attempt to use undefined function ''{0}''. Was one of these intended: {1}?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AmbiguousTypeReference.class), 
        "Attempt to use undefined type ''{0}''. Was one of these intended: {1}?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AmbiguousPartiallyQualifiedFormModuleName.class), 
        "The partially qualified module name ''{0}'' is ambiguous. Was one of these intended: {1}?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedClassSuggestion.class), 
        "Attempt to use undefined class ''{0}''. Was ''{1}'' intended?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedClass.class), 
        "Attempt to use undefined class ''{0}''."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedFunctionSuggestion.class), 
        "Attempt to use undefined function ''{0}''. Was ''{1}'' intended?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedFunction.class), 
        "Attempt to use undefined function ''{0}''."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseForeignFunctionThatFailedToLoad.class), 
        "Attempt to use the foreign function ''{0}'', whose Java implementation could not be loaded."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedTypeSuggestion.class), 
        "Attempt to use undefined type ''{0}''. Was ''{1}'' intended?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedType.class), 
        "Attempt to use undefined type ''{0}''."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseForeignTypeThatFailedToLoad.class), 
        "Attempt to use the foreign type ''{0}'', whose Java implementation could not be loaded."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedDataConstructorSuggestion.class), 
        "Attempt to use undefined data constructor ''{0}''. Was ''{1}'' intended?"  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedDataConstructor.class), 
        "Attempt to use undefined data constructor ''{0}''."  } ,

    { MessageKind.getClassName(MessageKind.Error.AttemptToUseUndefinedIdentifier.class), 
        "Attempt to use undefined identifier ''{0}''."  } ,

    { MessageKind.getClassName(MessageKind.Error.InvalidDefaultClassMethodType.class), 
        "The default class method {0} for the class method {1} must have type {2}. Instead it has type {3}."  } ,        
        
    { MessageKind.getClassName(MessageKind.Error.ResolvingFunctionForInstanceMethodHasWrongTypeSpecific.class), 
        "The resolving function {0} for the instance method {1} must have type {2}. Instead it has type {3}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ResolvingFunctionForInstanceMethodHasWrongTypeGeneral.class), 
        "The resolving function {0} for the instance method {1} must have a type that can specialize to {2}. Instead it has type {3}."  } ,


    { MessageKind.getClassName(MessageKind.Error.KindErrorInDataConstructorForType.class), 
        "Kind error in the data constructor {0} for type {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ForeignTypeHasSameNameAsBuiltInType.class), 
        "The foreign type {0} cannot have the same name as a built-in type."  } ,

    { MessageKind.getClassName(MessageKind.Error.ExternalClassNotFound.class), 
        "The external class {0} representing {1} was not found."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.ExternalClassNotAccessible.class), 
        "The Java type ''{0}'' is not accessible. It does not have public scope or is in an unnamed package."  } ,        

    { MessageKind.getClassName(MessageKind.Error.ExternalClassCouldNotBeInitialized.class), 
        "The external class {0} representing {1} could not be initialized."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeHasSameNameAsBuiltInType.class), 
        "The type {0} cannot have the same name as a built-in type."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeHasSameNameAsForeignType.class), 
        "The type {0} cannot have the same name as a foreign type."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeVariableMustAppearOnLHSOfDataDeclaration.class), 
        "The type variable {0} must appear on the left-hand side of the data declaration."  } ,

    { MessageKind.getClassName(MessageKind.Error.RecordVarsCannotAppearInDataDeclarations.class), 
        "Record variables, such as {0} cannot appear in data declarations."  } ,

    { MessageKind.getClassName(MessageKind.Error.ModuleHasNotBeenImported.class), 
        "The module {0} has not been imported into {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeDoesNotExist.class), 
        "The type {0} does not exist."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeClassNotVisible.class), 
        "The class {0} is not visible in module {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeConstructorNotVisible.class), 
        "The type {0} is not visible in module {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.DataConstructorNotVisible.class), 
        "The data constructor {0} is not visible in module {1}."  } ,        

    // Foreign Function Checker
    { MessageKind.getClassName(MessageKind.Error.ExpectingMethodFieldOrConstructor.class), 
        "\"method\", \"field\", \"constructor\", \"instanceof\" or \"class\" is expected rather than \"{0}\"."  } ,

    { MessageKind.getClassName(MessageKind.Error.ExpectingStatic.class), 
        "\"static\" is expected rather than \"{0}\"."  } ,

    { MessageKind.getClassName(MessageKind.Error.ExpectingMethodOrField.class), 
        "\"method\" or \"field\" is expected rather than \"{0}\"."  } ,
              
    { MessageKind.getClassName(MessageKind.Error.InvalidExternalNameStringFormat.class), 
        "Invalid external name string format \"{0}\". A valid example: \"static method java.lang.isUpperCase\"."  } ,

    { MessageKind.getClassName(MessageKind.Error.JavaNameMustBeFullyQualified.class), 
        "The Java name {0} must be fully qualified (i.e. include the package name)."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.JavaNameMustBeUnqualified.class), 
        "The Java name {0} must be unqualified (i.e. omit the package name)."  } ,        

    { MessageKind.getClassName(MessageKind.Error.NonStaticMethodsMustHaveInstanceAndReturnType.class), 
        "Non-static methods must have an object instance and a return type."  } ,

    { MessageKind.getClassName(MessageKind.Error.NonStaticFieldsMustHaveInstanceAndReturnType.class), 
        "Non-static fields must have an object instance and a return type."  } ,

    { MessageKind.getClassName(MessageKind.Error.FirstArgumentOfNonStaticMethodMustBeInstance.class), 
        "The first argument of a non-static method must be the object instance that the method will be applied to."  } ,

    { MessageKind.getClassName(MessageKind.Error.FirstArgumentOfNonStaticFieldMustBeInstance.class), 
        "The first argument of a non-static field must be the object instance that the field will be invoked on."  } ,

    { MessageKind.getClassName(MessageKind.Error.SecurityViolationTryingToAccess.class), 
        "Security violation trying to access {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ForeignFunctionReturnsWrongType.class), 
        "Foreign function {0} cannot return a value of type {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.FieldReturnsWrongType.class), 
        "Field {0} cannot return a value of type {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ConstructorReturnsWrongType.class), 
        "Constructor {0} cannot return a value of type {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.StaticFieldMustHaveNoArguments.class), 
        "The static field {0} must have no arguments."  } ,

    { MessageKind.getClassName(MessageKind.Error.JavaClassNotFound.class), 
        "The Java class {0} was not found."  } ,

    { MessageKind.getClassName(MessageKind.Error.JavaClassNotFoundWhileLoading.class), 
        "The Java class {0} was not found while loading {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.JavaClassCouldNotBeInitialized.class), 
        "The Java class {0} could not be initialized."  } ,

    { MessageKind.getClassName(MessageKind.Error.DependeeJavaClassNotFound.class), 
        "The Java class {0} was not found. This class is required by {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.JavaClassDefinitionCouldNotBeLoaded.class), 
        "The definition of Java class {0} could not be loaded."  } ,

    { MessageKind.getClassName(MessageKind.Error.ProblemsUsingJavaClass.class), 
        "The Java class {0} was found, but there were problems with using it.\nClass:   {1}.\nMessage: {2}."  } ,

    { MessageKind.getClassName(MessageKind.Error.ImplementationAsForeignTypeNotVisible.class), 
        "The implementation of {0} as a foreign type is not visible within this module."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeNotSupportedForForeignCalls.class), 
        "Type {0} is not supported for foreign calls."  } ,


    { MessageKind.getClassName(MessageKind.Error.CouldNotFindConstructorWithGivenArgumentTypes.class), 
        "Could not find the constructor {0} with the given argument types {1}. "  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotFindField.class), 
        "Could not find the field {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotFindMethodWithGivenArgumentTypes.class), 
        "Could not find the method {0} with given argument types {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotFindNonStaticField.class), 
        "Could not find the non-static field {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotFindNonStaticMethodWithGivenArgumentTypes.class), 
        "Could not find the non-static method {0} with given argument types {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotFindStaticField.class), 
        "Could not find the static field {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.CouldNotFindStaticMethodWithGivenArgumentTypes.class), 
        "Could not find the static method {0} with given argument types {1}."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.CouldNotFindWorkspaceDefinition.class), 
        "Could not find a workspace definition."  } ,


    // FreeVariableFinder
    { MessageKind.getClassName(MessageKind.Error.WildcardPatternMustBeFinalInCaseExpression.class), 
        "The wildcard pattern \"_\" must be the final pattern in a case expression."  } ,

    { MessageKind.getClassName(MessageKind.Error.Illegal1TuplePattern.class), 
        "Illegal 1-tuple pattern."  } ,

    { MessageKind.getClassName(MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithTuplePattern.class), 
        "Can have only one pattern in a case-expression with a tuple pattern."  } ,

    { MessageKind.getClassName(MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithUnitPattern.class), 
        "Can have only one pattern in a case-expression with (), the unit pattern."  } ,

    { MessageKind.getClassName(MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithRecordPattern.class), 
        "Can have only one pattern in a case-expression with a record pattern."  } ,

    { MessageKind.getClassName(MessageKind.Error.ClassDoesNotExistInModule.class), 
        "The class {0} does not exist in {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.TypeClassDoesNotExist.class), 
        "The class {0} does not exist."  } ,

    { MessageKind.getClassName(MessageKind.Error.DataConstructorDoesNotExistInModule.class), 
        "The data constructor {0} does not exist in {1}."  } ,

    { MessageKind.getClassName(MessageKind.Error.DataConstructorDoesNotExist.class), 
        "The data constructor {0} does not exist."  } ,

    { MessageKind.getClassName(MessageKind.Error.IdentifierDoesNotExist.class), 
        "The identifier {0} does not exist."  } ,

    { MessageKind.getClassName(MessageKind.Error.FunctionNotVisible.class), 
        "The function {0} is not visible in module {1}."  } ,
        
    { MessageKind.getClassName(MessageKind.Error.ClassMethodNotVisible.class), 
        "The class method {0} is not visible in module {1}."  } ,        


    // Program Factory
    { MessageKind.getClassName(MessageKind.Error.ErrorReadingAdjunctSource.class), 
        "Error reading adjunct source:"  } ,

    { MessageKind.getClassName(MessageKind.Error.RuntimeInputHasAmbiguousType.class), 
        "A runtime input has an ambiguous type: {0}"  } ,

    { MessageKind.getClassName(MessageKind.Error.ErrorDeterminingTargetOutputType.class), 
        "Error determining target output type. "  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToMakeInstanceTypeNotVisible.class), 
        "Unable to make type {0} an instance of {1}: the type is not visible."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToMakeInstanceNoDataConstructors.class), 
        "Unable to make type {0} an instance of {1}: there are no data constructors."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToMakeInstanceNoVisibleDataConstructors.class), 
        "Unable to make type {0} an instance of {1}: there are no visible data constructors."  } ,

    { MessageKind.getClassName(MessageKind.Error.UnableToResolveFunctionAlias.class), 
      "Unable to resolve {0} as an alias of {1} in function {2}."  } ,

    // Code Generator
    { MessageKind.getClassName(MessageKind.Error.CodeGenerationAborted.class), 
        "Code generation aborted. Error generating code for: {0}"  } ,

    { MessageKind.getClassName(MessageKind.Error.CodeGenerationAbortedWithException.class), 
        "Code generation aborted in module {0}: {1}"  } ,
 
    { MessageKind.getClassName(MessageKind.Fatal.CodeGenerationAbortedDueToInternalCodingError.class), 
        "Aborted code generation for module {0} due to internal coding error. Please contact Business Objects."  } ,

    { MessageKind.getClassName(MessageKind.Fatal.CompilationAbortedDueToInternalModuleLoadingError.class), 
        "Compilation aborted due to an internal error in loading the compiled module {0}. Please contact Business Objects. Detail: {1}"  } ,

    { MessageKind.getClassName(MessageKind.Error.DeserializedIncompatibleSchema.class), 
        "Stored schema {0} is later than current schema {1} for record: {2}."  } ,
        
    { MessageKind.getClassName(MessageKind.Fatal.UnableToRecoverFromCodeGenErrors.class), 
        "Not able to recover from previous code generation error(s) in module {0}."  } ,

    { MessageKind.getClassName(MessageKind.Error.FailedToFinalizeJavaCode.class), 
        "Failed to finalize Java code for: {0}"  } ,

        
    // SupercombinatorActivationRecord dump() messages
    { MessageKind.getClassName(MessageKind.Info.SupercombinatorInfoDumpCAF.class), 
        "{0} (a Constant Applicative Form)\nhas the following local variables:\n{1}"  } ,

    { MessageKind.getClassName(MessageKind.Info.SupercombinatorInfoDumpNonCAF.class), 
        "{0}\nhas the following local variables:\n{1}"  } ,


    // Message Formats
    // {Heading}: {Message}
    { "MessageFormat", "{0}: {1}"  } ,
    { "SourcePositionFormat", "(line {0} column {1})"  } ,
    { "SourcePositionFormatNoColumn", "(line {0})"  } ,
    { "MessageWithTypeException", "{0} Caused by: {1}"  } ,
    { "MessageWithOtherException", "{0} Detail: {2}. Caused by: {1}"  } ,
    { "MoreTokens", "... {0} more tokens" } ,
    { "AssociatedWithFunction", "Function {0}: {1}" } ,
    { "AssociatedWithTypeCons", "Type constructor {0}: {1}" } ,
    { "AssociatedWithDataCons", "Data constructor {0}: {1}" } ,
    { "AssociatedWithTypeClass", "Type class {0}: {1}" } ,
    { "AssociatedWithClassMethod", "Class method {0}: {1}" } ,
    { "AssociatedWithGeneral", "For {0}: {1}" } ,
    };
}
