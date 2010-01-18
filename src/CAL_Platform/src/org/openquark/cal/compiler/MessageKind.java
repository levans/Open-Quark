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
 * MessageKind.java
 * Created: October 29, 2004
 * By: Peter Cardwell
 */
package org.openquark.cal.compiler;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.openquark.cal.internal.javamodel.JavaTypeName;



/**
 * This is the base class from which all compiler message kind classes are derived.
 * Each MessageKind represents a different kind of message for the user. 
 * <p>
 * By default, a MessageKind subclass returns a localized string by using reflection
 * to look up the message in the CALMessages resource bundle with the class name as the key.
 * For example, the UnableToParseToIntegerLiteral subclass will call 
 * CALMessages.getString("UnableToParseToIntegerLiteral"). However, this behavior can be
 * overidden.
 * <p>
 * NOTE: All MessageKind subclasses are intended to be derived in this file.
 * <p>
 * It is important to note that MessageKind messages are intended to correspond to user 
 * errors and should not be used for internal coding errors (i.e. bugs in our implementation
 * of the CAL language). In that case, please just throw a run-time exception- this will be
 * caught and (if appropriate) logged as a MessageKind.Fatal.InternalCodingError.
 * 
 * @author Peter Cardwell
 */
public abstract class MessageKind {
    
    private MessageKind () {}
    
    /**
     * Normal level when reporting a user error in CAL source.
     * Compilation can proceed, but should not be able to run the resulting program.
     */
    public abstract static class Error extends MessageKind {
        
        private Error () {}
        
        @Override
        public final CompilerMessage.Severity getSeverity() {
            return CompilerMessage.Severity.ERROR;
        }
        
        public final static class DebugMessage extends Error {
            
            private final String debugMessage;
            
            public DebugMessage (String message) {
                if (message == null) {
                    throw new NullPointerException();
                }

                debugMessage = message;
            }
            
            @Override
            public String getMessage() {
                return debugMessage;
            }
        }

        /**
         * Called when a parse tree node for an integer-literal couldn't be converted to an int value because
         * the literal is out of range for the Int type.
         * 
         * For example,
         * caseOnOutOfRangeInt x =
         *     case x of
         *     1873468172648912649812764912837643 -> "5";
         *     ;
         * 
         * gives:
         * 1873468172648912649812764912837643 is outside of range for the Int type.  The valid range is -2147483648 to 2147483647 (inclusive).
         * 
         * @author Edward Lam
         */
        final static class IntLiteralOutOfRange extends Error {

            private final String literalName;

            IntLiteralOutOfRange (String literalName) throws NullPointerException {
                if (literalName == null) {
                    throw new NullPointerException();
                }

                this.literalName = literalName;
            }

            @Override
            Object[] getMessageArguments() {
                // {0} is outside of range for the Int type.  The valid range is -2147483648 to 2147483647 (inclusive).
                return new Object[] {literalName};
            }
        }
        
        final static class UnableToParseToIntegerLiteral extends Error {

            private final String literalName;

            UnableToParseToIntegerLiteral (String literalName) throws NullPointerException {
                if (literalName == null) {
                    throw new NullPointerException();
                }

                this.literalName = literalName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {literalName};
            }
        }
        
        final static class UnableToParseToFloatingPointLiteral extends Error {

            private final String literalName;

            UnableToParseToFloatingPointLiteral (String literalName) {
                if (literalName == null) {
                    throw new NullPointerException();
                }

                this.literalName = literalName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {literalName};
            }
        }
        
        final static class UnableToParseToCharacterLiteral extends Error {

            private final String literalName;

            UnableToParseToCharacterLiteral (String literalName) {
                if (literalName == null) {
                    throw new NullPointerException();
                }

                this.literalName = literalName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {literalName};
            }
        }
        
        final static class UnableToParseToStringLiteral extends Error {

            private final String literalName;

            UnableToParseToStringLiteral (String literalName) {
                if (literalName == null) {
                    throw new NullPointerException();
                }

                this.literalName = literalName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {literalName};
            }
        }
        
        /**
         * Called when creating a String literal that is too long (having a UTF-8 encoding greater than 65535 bytes in length).
         * 
         * This sort of thing can sometimes occur when using the SourceModel and attempting to pass arguments as constant 
         * values. 
         * 
         * @author Bo Ilic
         */
        final static class StringLiteralTooLong extends Error {

            private final String literalValue;

            StringLiteralTooLong (String literalValue) {
                if (literalValue == null) {
                    throw new NullPointerException();
                }

                this.literalValue = literalValue;
            }

            @Override
            Object[] getMessageArguments() {
                final String shortenedValue = literalValue.substring(0, 50) + "...";
                //"The string literal \"{0}\" is too long."
                return new Object[] {shortenedValue};
            }
        }        

        final static class FailedValidationASTParse extends Error {

            private final String message;

            FailedValidationASTParse (String message) {
                if (message == null) {
                    throw new NullPointerException();
                }

                this.message = message;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {message};
            }
        }
        
        final static class CouldNotParseExpressionInModule extends Error {
            
            private final ModuleName moduleName;
            
            CouldNotParseExpressionInModule (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }
                
                this.moduleName = moduleName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        /**
         * Used when the CMI file of a sourceless module cannot be loaded, and the Car maybe
         * invalid.
         * 
         * @author Joseph Wong
         */
        final static class CannotLoadSourcelessModuleMaybeInvalidCar extends Error {

            private final ModuleName moduleName;

            CannotLoadSourcelessModuleMaybeInvalidCar(final ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                //The CMI file of the sourceless module {0} could not be loaded. The Car file containing this module may be invalid.
                return new Object[] {moduleName.toSourceText()};
            }
        }

        /**
         * Used when the CMI file of a sourceless module cannot be loaded because a dependee
         * cannot be loaded.
         * 
         * @author Joseph Wong
         */
        final static class CannotLoadSourcelessModuleDependeeNotFound extends Error {

            private final ModuleName moduleName;
            private final ModuleName dependeeName;

            CannotLoadSourcelessModuleDependeeNotFound(final ModuleName moduleName, final ModuleName dependeeName) {
                if (moduleName == null || dependeeName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
                this.dependeeName = dependeeName;
            }

            @Override
            Object[] getMessageArguments() {
                //The CMI file of the sourceless module {0} could not be loaded because the imported module {1} cannot be found.
                return new Object[] {moduleName.toSourceText(), dependeeName.toSourceText()};
            }
        }

        /**
         * Used when the CMI file of a sourceless module cannot be loaded because it is older
         * than a dependee.
         * 
         * @author Joseph Wong
         */
        final static class CannotLoadSourcelessModuleOlderThanDependee extends Error {

            private final ModuleName moduleName;
            private final ModuleName dependeeName;

            CannotLoadSourcelessModuleOlderThanDependee(final ModuleName moduleName, final ModuleName dependeeName) {
                if (moduleName == null || dependeeName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
                this.dependeeName = dependeeName;
            }

            @Override
            Object[] getMessageArguments() {
                //The CMI file of the sourceless module {0} could not be loaded because it is out-of-date with respect to the imported module {1}.
                return new Object[] {moduleName.toSourceText(), dependeeName.toSourceText()};
            }
        }

        /**
         * Used when the CMI file of a sourceless module cannot be loaded.
         * 
         * @author Joseph Wong
         */
        final static class InvalidSourcelessModule extends Error {

            private final ModuleName moduleName;

            InvalidSourcelessModule(ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }

        
        final static class ModuleCouldNotBeParsed extends Error {

            private final ModuleName moduleName;

            ModuleCouldNotBeParsed (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class ModuleNotWriteable extends Error {

            private final ModuleName moduleName;

            ModuleNotWriteable (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class ModuleCouldNotBeRead extends Error {

            private final ModuleName moduleName;

            ModuleCouldNotBeRead (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class CouldNotReadModuleSource extends Error {

            private final ModuleName moduleName;

            CouldNotReadModuleSource (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class ModuleNameDoesNotCorrespondToSourceName extends Error {

            private final ModuleName moduleName;
            private final ModuleName moduleSourceName;

            ModuleNameDoesNotCorrespondToSourceName (ModuleName moduleName, ModuleName moduleSourceName) {
                if (moduleName == null || moduleSourceName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
                this.moduleSourceName = moduleSourceName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName, moduleSourceName};
            }
        }
        
        final static class AttemptedRedefinitionOfModule extends Error {

            private final ModuleName moduleName;

            AttemptedRedefinitionOfModule (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class UnableToCloseModuleAndPackage extends Error {

            private final String message;

            UnableToCloseModuleAndPackage (String message) {
                if (message == null) {
                    throw new NullPointerException();
                }

                this.message = message;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {message};
            }
        }
        
        final static class UnableToCloseModule extends Error {
            
            private final ModuleName moduleName;
            
            UnableToCloseModule (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }
                
                this.moduleName = moduleName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class AttemptToImportModuleIntoItself extends Error {

            private final ModuleName moduleName;

            AttemptToImportModuleIntoItself (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class UnresolvedExternalModuleImportWithNoSuggestions extends Error {

            private final ModuleName moduleName;

            UnresolvedExternalModuleImportWithNoSuggestions (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        /**
         * Used when an imported module name cannot be resolved, but we have one module name to suggest.
         *
         * @author Joseph Wong
         */
        final static class UnresolvedExternalModuleImportWithOneSuggestion extends Error {

            private final ModuleName moduleName;
            
            private final ModuleName suggestion;

            UnresolvedExternalModuleImportWithOneSuggestion (final ModuleName moduleName, final ModuleName suggestion) {
                if (moduleName == null || suggestion == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
                this.suggestion = suggestion;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName, suggestion};
            }
        }
        
        /**
         * Used when an imported module name cannot be resolved, but we have multiple module names to suggest.
         *
         * @author Joseph Wong
         */
        final static class UnresolvedExternalModuleImportWithMultipleSuggestions extends Error {

            private final ModuleName moduleName;
            
            private final ModuleName[] suggestions;

            UnresolvedExternalModuleImportWithMultipleSuggestions (final ModuleName moduleName, final ModuleName[] suggestions) {
                if (moduleName == null || suggestions == null) {
                    throw new NullPointerException();
                }

                if (suggestions.length == 0) {
                    throw new IllegalArgumentException("this message is for unresolved module imports with at least 1 suggestion");
                }

                this.moduleName = moduleName;
                this.suggestions = suggestions.clone();
                
                // Make sure we list the possibilities in alphabetical order
                Arrays.sort(this.suggestions);
            }

            @Override
            Object[] getMessageArguments() {
                
                StringBuilder suggestedNamesBuffer = new StringBuilder(suggestions[0].toSourceText());
                for(int i = 1; i < suggestions.length; i++) {
                    suggestedNamesBuffer.append(", ");
                    suggestedNamesBuffer.append(suggestions[i]);
                }
                
                return new Object[] {moduleName, suggestedNamesBuffer.toString()};
            }
        }
        
        final static class RepeatedImportOfModule extends Error {

            private final ModuleName moduleName;

            RepeatedImportOfModule (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class ModuleMustImportOtherModule extends Error {

            private final ModuleName importer;
            private final ModuleName importee;

            ModuleMustImportOtherModule (ModuleName importer, ModuleName importee) {
                if (importer == null || importee == null) {
                    throw new NullPointerException();
                }

                this.importer = importer;
                this.importee = importee;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {importer, importee};
            }
        }
        
        final static class CyclicDependenciesBetweenModules extends Error {

            private final String cyclicNames;

            CyclicDependenciesBetweenModules (String cyclicNames) {
                if (cyclicNames == null) {
                    throw new NullPointerException();
                }

                this.cyclicNames = cyclicNames;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {cyclicNames};
            }
        }
       
        /**
         * Called when a data constructor is not decomposed in a case alternative with the right number of arguments.
         * 
         * For example,         
         * incorrectNOfPatternVariables x =
         *     case x of
         *     Just v w -> v + 2.0;
         *     Nothing -> 1.0;
         *     ;
         * 
         * gives:
         * The data constructor Prelude.Just must have exactly 1 pattern argument(s) in its case alternative.        
         */
        final static class ConstructorMustHaveExactlyNArgsInPattern extends Error {

            private final DataConstructor dataCons;           

            ConstructorMustHaveExactlyNArgsInPattern (DataConstructor dataCons) {
                if (dataCons == null) {
                    throw new NullPointerException();
                }
                
                this.dataCons = dataCons;                
            }

            @Override
            Object[] getMessageArguments() {
                //"The data constructor {0} must have exactly {1} pattern argument(s) in its case alternative."
                return new Object[] {dataCons.getName().getQualifiedName(), Integer.valueOf(dataCons.getArity())};
            }
        }
        
        final static class UnknownFunctionOrVariable extends Error {

            private final String identifier;

            UnknownFunctionOrVariable (String identifier) {
                if (identifier == null) {
                    throw new NullPointerException();
                }

                this.identifier = identifier;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {identifier};
            }
        }
        
        final static class UnknownDataConstructor extends Error {

            private final String identifier;

            UnknownDataConstructor (String identifier) {
                if (identifier == null) {
                    throw new NullPointerException();
                }

                this.identifier = identifier;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {identifier};
            }
        }
        
        /**
         * Called when a data constructor case alt or local pattern match decl using matching notation attempts to bind a field which doesn't exist.
         * Also called when a data constructor field selection refers to a field which doesn't exist.
         * 
         * For example,
         * 
         * data Foo = 
         *     private Foo 
         *         bar::Int;
         * 
         * functionWhichUsesUnknownDataConstructorArgument x =
         *     case x of
         *     Foo {unknownArg} -> unknownArg;
         *     ;
         * 
         * or
         * functionWhichUsesUnknownDataConstructorArgument2 x = x.Foo.unknownArg;
         * 
         * gives:
         * "unknownArg" is not a valid argument for data constructor Foo.  Valid argument(s): bar"
         */
        final static class UnknownDataConstructorField extends Error {

            private final DataConstructor dataConstructor;
            private final FieldName fieldName;

            UnknownDataConstructorField (DataConstructor dataConstructor, FieldName fieldName) {
                if (dataConstructor == null || fieldName == null) {
                    throw new NullPointerException();
                }

                this.dataConstructor = dataConstructor;
                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                String dcName = dataConstructor.getName().getQualifiedName();
                
                StringBuilder fieldNameListBuffer = new StringBuilder();
                int arity = dataConstructor.getArity();
                for (int i = 0; i < arity; i++) {
                    if (i > 0) {
                        fieldNameListBuffer.append(", ");
                    }
                    FieldName nthFieldName = dataConstructor.getNthFieldName(i);
                    fieldNameListBuffer.append(nthFieldName.getCalSourceForm());
                }

                //"\"{1}\" is not a field of data constructor {0}.  Valid fields(s): {2}"
                return new Object[] {dcName, fieldName.getCalSourceForm(), fieldNameListBuffer.toString()};
            }
        }
        
        /**
         * A special case of UnknownDataConstructorField when the referenced data constructor has no fields.
         * 
         * For example,
         * 
         * data Foo = private Foo;
         * 
         * functionWhichUsesZeroFieldDataConstructorArgument x =
         *     case x of
         *     Foo {unknownArg} -> unknownArg;
         *     ;
         * 
         * or
         * functionWhichUsesUnknownDataConstructorArgument2 x = x.Foo.unknownArg;
         * 
         * gives:
         * "unknownArg" is not a field of data constructor Foo. Data constructor Foo has no fields."
         */
        final static class ZeroFieldDataConstructorFieldReference extends Error {

            private final DataConstructor dataConstructor;
            private final FieldName fieldName;

            ZeroFieldDataConstructorFieldReference (DataConstructor dataConstructor, FieldName fieldName) {
                if (dataConstructor == null || fieldName == null) {
                    throw new NullPointerException();
                }

                this.dataConstructor = dataConstructor;
                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                String dcName = dataConstructor.getName().getQualifiedName();
                
                //"\"{1}\" is not a field of data constructor {0}. Data constructor {0} has no fields."
                return new Object[] {dcName, fieldName.getCalSourceForm()};
            }
        }
        
        final static class InvalidRecordSelectionForField extends Error {

            private final FieldName fieldName;

            InvalidRecordSelectionForField (FieldName fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                //"Type error. Invalid record selection for field {0}." 
                return new Object[] {fieldName.getCalSourceForm()};
            }
        }
        
        /**
         * Used when a field declared in a local pattern match declaration is missing from the type of the defining expression.
         * 
         * For example:
         * <pre>
         * foo6b = let {_|y} = {a = "foo", b = "bar"}; in y;
         * </pre>
         * 
         * gives:
         * Type error. The field y declared in the local pattern match declaration is missing from the type of the defining expression.
         *
         * @author Joseph Wong
         */
        final static class InvalidRecordFieldInLocalPatternMatchDecl extends Error {

            private final FieldName fieldName;
            private final TypeExpr typeExpr;

            InvalidRecordFieldInLocalPatternMatchDecl(final FieldName fieldName, final TypeExpr typeExpr) {
                if (fieldName == null || typeExpr == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
                this.typeExpr = typeExpr;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName.getCalSourceForm(), typeExpr.toString()};
            }
        }
        
        final static class DeclaredTypeOfExpressionNotCompatibleWithInferredType extends Error {

            private final String inferredType;

            DeclaredTypeOfExpressionNotCompatibleWithInferredType (String inferredType) {
                if (inferredType == null) {
                    throw new NullPointerException();
                }

                this.inferredType = inferredType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {inferredType};
            }
        }
        
        final static class OrdinalFieldNameOutOfRange extends Error {

            private final String fieldName;

            OrdinalFieldNameOutOfRange (String fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName};
            }
        }
        
        final static class TypeErrorApplyingOperatorToFirstArgument extends Error {

            private final String operatorName;

            TypeErrorApplyingOperatorToFirstArgument (String operatorName) {
                if (operatorName == null) {
                    throw new NullPointerException();
                }

                this.operatorName = operatorName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {operatorName};
            }
        }
        
        final static class TypeErrorApplyingOperatorToSecondArgument extends Error {

            private final String operatorName;

            TypeErrorApplyingOperatorToSecondArgument (String operatorName) {
                if (operatorName == null) {
                    throw new NullPointerException();
                }

                this.operatorName = operatorName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {operatorName};
            }
        }
        
        final static class DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType extends Error {

            private final String functionName;
            private final String inferredType;

            DeclaredTypeOfLocalFunctionNotCompatibleWithInferredType (String functionName, String inferredType) {
                if (functionName == null || inferredType == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.inferredType = inferredType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, inferredType};
            }
        }
        
        final static class TypeOfLocalFunctionNotCompatibleWithDefiningExpression extends Error {

            private final String functionName;

            TypeOfLocalFunctionNotCompatibleWithDefiningExpression (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class TypeOfFunctionNotCompatibleWithDefiningExpression extends Error {

            private final String functionName;

            TypeOfFunctionNotCompatibleWithDefiningExpression (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        /**
         * Type constructor instance declarations for the Typeable type class are automatically generated by the compiler
         * whenever possible. However, for types whose type variable arguments are not of kind *, such
         * an instance declaration is invalid. Thus a user could theoretically create an instance declaration
         * for the type, causing a security violation since the Typeable instances are used for Dynamic support.
         * 
         * In addition, even for the instances where an explicit declaration would create an overlapping instance
         * error, this message is easier to understand.
         * 
         * For example, in the following fragment, the compiler does not generate an instance declaration for
         * Type1 so the user supplied instance declaration would not give an overlapping instance error. Thus,
         * we explicitly trap this case as an error.
         * 
         * data Type1 a b = MakeType1 field :: (a b); //a has kind * -> *. 
         * instance Prelude.Typeable (Type1 a b) where
         *    typeOf = typeOfType1;
         *    ;         
         * typeOfType1 :: Type1 a b -> Prelude.TypeRep;
         * typeOfType1 x = (Prelude.typeOf (Prelude.undefined :: Int));
         *
         * @author Bo Ilic
         */
        final static class ExplicitTypeableInstance extends Error {
            
            ExplicitTypeableInstance() {}
            
        }
               
        /**
         * Called when a type constructor, such is applied to overly many type arguments.
         * 
         * For example,
         * 
         * incorrectNTypeConsArgs :: Prelude.Either Int Char Int -> Boolean;
         * incorrectNTypeConsArgs x = True;
         * 
         * results in:
         * The type constructor Prelude.Either expects at most 2 type argument(s). 3 supplied.
         * (with the caret position at Prelude.Either).     
         * 
         * Note that it is not an error for a type constructor to be applied to too few arguments,
         * although it may cause an error in the surrounding context due to kind checking. In fact,
         * applying a type constructor to overly many arguments will also cause a kind checking error,
         * but this error message is easier to understand.
         * 
         * @author Bo Ilic
         */
        final static class TypeConstructorAppliedToOverlyManyArgs extends Error {

            private final QualifiedName typeConsName;
            private final int numExpectedArgs;
            private final int numSuppliedArgs;

            TypeConstructorAppliedToOverlyManyArgs (QualifiedName typeConsName, int numExpectedArgs, int numSuppliedArgs) {                
                if (typeConsName == null) {
                    throw new NullPointerException();
                }
                
                this.typeConsName = typeConsName;
                this.numExpectedArgs = numExpectedArgs;
                this.numSuppliedArgs = numSuppliedArgs;
            }

            @Override
            Object[] getMessageArguments() {
                // "The type constructor {0} expects at most {1} type argument(s). {2} supplied."
                return new Object[] {typeConsName.getQualifiedName(), Integer.valueOf(numExpectedArgs), Integer.valueOf (numSuppliedArgs)};
            }
        }
        
        /**
         * In an instance declaration
         * instance (constraints) => C (T a1 ... am) where ...
         * 
         * the kind of the type class C must equals the kind of the type application (T a1 ... am).
         * 
         * For example
         * instance Functor (Maybe a) where ...
         * will produce this sort of error.
         * 
         * @author Bo Ilic
         */
        final static class InstanceDeclarationKindClash extends Error {
            
            private final TypeClass typeClass;
            private final TypeConstructor typeCons;
            private final int nTypeArgs;
            private final KindExpr appliedTypeConsKind;
            
            InstanceDeclarationKindClash(TypeClass typeClass, TypeConstructor typeCons, int nTypeArgs, KindExpr appliedTypeConsKind) {
                if (typeClass == null || typeCons == null || appliedTypeConsKind == null) {
                    throw new NullPointerException();
                }
                this.typeClass = typeClass;
                this.typeCons = typeCons;
                this.nTypeArgs = nTypeArgs;
                this.appliedTypeConsKind = appliedTypeConsKind;
            }
            
            @Override
            Object[] getMessageArguments() {
                // "Invalid instance declaration; the kind of the type class {0} (i.e. {1}) is not the same as the kind of the application of the type constructor {2} to {3} type arguments (i.e. {4})."
                return new Object[] {
                    typeClass.getName().getQualifiedName(),
                    typeClass.getKindExpr(),
                    typeCons.getName().getQualifiedName(),
                    Integer.valueOf(nTypeArgs),
                    appliedTypeConsKind};
            }            
        }
        
        /**
         * In an instance declaration
         * instance (constraints) => C (T a1 ... am) where ...
         * where Ci aj appears as a constraint, then the kind of Ci must equals the kind of aj.
         * 
         * The kinds of the type variables a1 ... am are determined from the kind of T.
         *         
         * For example
         * instance (Functor a) => Eq (MyMaybe a) where ...
         * will produce this sort of error (where MyMaybe is a clone of the Maybe type. We use a clone because otherwise we would
         * get an overlapping instance definition error).
         * 
         * @author Bo Ilic
         */
        final static class InstanceDeclarationKindClashInConstraint extends Error {
            
            private final String typeVarName;
            private final KindExpr typeVarKind;
            private final TypeClass constraintTypeClass;
           
            InstanceDeclarationKindClashInConstraint(String typeVarName, KindExpr typeVarKind, TypeClass constraintTypeClass) {
                if (typeVarName == null || typeVarKind == null || constraintTypeClass == null) {
                    throw new NullPointerException();
                }
                this.typeVarName = typeVarName;
                this.typeVarKind = typeVarKind;
                this.constraintTypeClass = constraintTypeClass;             
            }
            
            @Override
            Object[] getMessageArguments() {
                //"Invalid instance declaration; the kind of the type variable {0} (i.e. {1}) is not the same as the kind of the constraining type class {2} (i.e. {3})."
                return new Object[] {
                    typeVarName,
                    typeVarKind,
                    constraintTypeClass.getName(),
                    constraintTypeClass.getKindExpr()};
            }            
        }
        
        /**
         * In an instance declaration
         * instance (constraints) => C {r} where ...
         * 
         * the kind of the type class C must be *.
         * 
         * For example
         * instance Functor {r} where ...
         * will produce this sort of error.
         * 
         * @author Bo Ilic
         */
        final static class RecordInstanceDeclarationKindClash extends Error {
            
            private final TypeClass typeClass;          
            
            RecordInstanceDeclarationKindClash(TypeClass typeClass) {
                if (typeClass == null) {
                    throw new NullPointerException();
                }
                this.typeClass = typeClass;                
            }
            
            @Override
            Object[] getMessageArguments() {
                // "Invalid record instance declaration; the kind of the type class {0} (i.e. {1}) is not equal to the kind *."
                return new Object[] {
                    typeClass.getName().getQualifiedName(),
                    typeClass.getKindExpr()};
            }            
        }        
        
        final static class ExtensionFieldsLacksConstraintsOnRecordVariable extends Error {

            private final String extensionFields;

            ExtensionFieldsLacksConstraintsOnRecordVariable (String extensionFields) {
                if (extensionFields == null) {
                    throw new NullPointerException();
                }

                this.extensionFields = extensionFields;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {extensionFields};
            }
        }
        
        final static class UndefinedTypeClass extends Error {

            private final String typeClassName;

            UndefinedTypeClass (String typeClassName) {
                if (typeClassName == null) {
                    throw new NullPointerException();
                }

                this.typeClassName = typeClassName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeClassName};
            }
        }
        
        /**
         * When a class constraint on a single type variable appears a repeated number of times.
         * For example,
         * 
         * incorrectClassConstraintOnTypeVar :: (Eq a, Eq b, Eq a) => a -> b -> Int;
         * incorrectClassConstraintOnTypeVar = Prelude.undefined;    
         * 
         * produces the error:
         * Repeated class constraint Prelude.Eq on the type variable a.         
         */
        final static class RepeatedClassConstraintOnTypeVariable extends Error {

            private final QualifiedName className;
            private final String varName;

            RepeatedClassConstraintOnTypeVariable (QualifiedName className, String varName) {
                if (className == null || varName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                //"Repeated class constraint {0} on the type variable {1}."
                return new Object[] {className.getQualifiedName(), varName};
            }
        }
        
        final static class RepeatedClassConstraintOnRecordVariable extends Error {

            private final String className;
            private final String varName;

            RepeatedClassConstraintOnRecordVariable (String className, String varName) {
                if (className == null || varName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className, varName};
            }
        }
        
        final static class RecordVariableNotUsedInTypeSignature extends Error {

            private final String varName;

            RecordVariableNotUsedInTypeSignature (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        final static class RepeatedLacksFieldConstraintOnRecordVariable extends Error {

            private final String fieldName;
            private final String varName;

            RepeatedLacksFieldConstraintOnRecordVariable (String fieldName, String varName) {
                if (fieldName == null || varName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName, varName};
            }
        }
        
        final static class VariableNotUsedInTypeSignature extends Error {

            private final String varName;

            VariableNotUsedInTypeSignature (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
               
        /**
         * Class methods declarations must not involve class type variables within their context
         * e.g. 
         * class Foo a where
         *    foo :: (Show a) => a -> Int;
         *    ;
         *    
         * will result in this sort of error, since the additional Show constraint on the class type variable 'a'
         * is not allowed.
         *        
         * @author Bo Ilic
         */
        final static class ClassTypeVarInMethodContext extends Error {

            private final String classTypeVarName;

            ClassTypeVarInMethodContext (String classTypeVarName) {
                if (classTypeVarName == null) {
                    throw new NullPointerException();
                }

                this.classTypeVarName = classTypeVarName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The class type variable {0} cannot be used in a class method context." 
                return new Object[] {classTypeVarName};
            }
        }
        
        /**
         * Called when a class method's type signature does not involve the class type var.
         * For example, for the class
         * class Foo a where
         *    method1 :: method1Type;
         *    method2 :: method2Type;
         *    ;
         * method1Type and method2Type must each involve the type variable 'a'.
         * 
         * @author Bo Ilic
         */
        final static class ClassMethodMustUseClassTypeVariable extends Error {

            private final String methodName;
            private final String classTypeVarName;

            ClassMethodMustUseClassTypeVariable (String methodName, String classTypeVarName) {
                if (methodName == null || classTypeVarName == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.classTypeVarName = classTypeVarName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The class method {0} must use the type class type variable {1} in its type signature." 
                return new Object[] {methodName, classTypeVarName};
            }
        }        
        
        final static class KindErrorInTypeDeclarationForFunction extends Error {

            private final String functionName;

            KindErrorInTypeDeclarationForFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class KindErrorInTypeDeclarationForClassMethod extends Error {

            private final String methodName;

            KindErrorInTypeDeclarationForClassMethod (String methodName) {
                if (methodName == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName};
            }
        }
        
        /**
         * Called when the kinds of all the classes listed as constraints in a type class declaration or
         * a function declaration for a given type variable are not the same
         * For example,
         * class (Eq a, Functor a) => Foo a where ...
         * f :: (Eq a, Functor a) => ... 
         * will both result in this error being generated.
         * @author Bo Ilic
         */               
        final static class KindErrorInClassConstraints extends Error {
            
            private final String typeVarName;
            private final TypeClass typeClass1;
            private final TypeClass typeClass2;
            
            KindErrorInClassConstraints(String typeVarName, TypeClass typeClass1, TypeClass typeClass2) {
                if (typeVarName == null || typeClass1 == null || typeClass2 == null) {
                    throw new NullPointerException();
                }
                
                this.typeVarName = typeVarName;
                this.typeClass1 = typeClass1;
                this.typeClass2 = typeClass2;                
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The kinds of all classes constraining the type variable '{0}' must be the same. Class {1} has kind {2} while class {3} has kind {4}."
                return new Object[] {
                        typeVarName,
                        typeClass1.getName().getQualifiedName(),
                        typeClass1.getKindExpr().toString(),
                        typeClass2.getName().getQualifiedName(),
                        typeClass2.getKindExpr().toString()};
            }
        }
        
        final static class RecordVariableAlreadyUsedAsTypeVariable extends Error {

            private final String recordVarName;

            RecordVariableAlreadyUsedAsTypeVariable (String recordVarName) {
                if (recordVarName == null) {
                    throw new NullPointerException();
                }

                this.recordVarName = recordVarName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {recordVarName};
            }
        }
        
        
        final static class TypeVariableAlreadyUsedAsRecordVariable extends Error {

            private final String varName;

            TypeVariableAlreadyUsedAsRecordVariable (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
       
        final static class RepeatedOccurrenceOfFieldNameInRecordType extends Error {

            private final String fieldName;

            RepeatedOccurrenceOfFieldNameInRecordType (String fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName};
            }
        }
        
        final static class FunctionAlreadyUsedInImportUsingDeclaration extends Error {

            private final String functionName;
            private final ModuleName importedModuleName;

            FunctionAlreadyUsedInImportUsingDeclaration (String functionName, ModuleName importedModuleName) {
                if (functionName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, importedModuleName};
            }
        }
        
        final static class FunctionNotDefinedInModule extends Error {

            private final String functionName;
            private final ModuleName importedModuleName;

            FunctionNotDefinedInModule (String functionName, ModuleName importedModuleName) {
                if (functionName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, importedModuleName};
            }
        }
               
        final static class ClassMethodNameAlreadyUsedInImportUsingDeclaration extends Error {

            private final String methodName;
            private final ModuleName importedModuleName;

            ClassMethodNameAlreadyUsedInImportUsingDeclaration (String methodName, ModuleName importedModuleName) {
                if (methodName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName, importedModuleName};
            }
        }
        
        final static class DataConstructorAlreadyUsedInImportUsingDeclaration extends Error {

            private final String consName;
            private final ModuleName importedModuleName;

            DataConstructorAlreadyUsedInImportUsingDeclaration (String consName, ModuleName importedModuleName) {
                if (consName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, importedModuleName};
            }
        }
        
        final static class DataConstructorNotDefinedInModule extends Error {

            private final String dataConsName;
            private final ModuleName importedModuleName;

            DataConstructorNotDefinedInModule (String dataConsName, ModuleName importedModuleName) {
                if (dataConsName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.dataConsName = dataConsName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The data constructor {0} is not defined in module {1}."
                return new Object[] {dataConsName, importedModuleName};
            }
        }        
        
        final static class TypeConstructorAlreadyUsedInImportUsingDeclaration extends Error {

            private final String consName;
            private final ModuleName importedModuleName;

            TypeConstructorAlreadyUsedInImportUsingDeclaration (String consName, ModuleName importedModuleName) {
                if (consName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, importedModuleName};
            }
        }
        
        final static class TypeConstructorNotDefinedInModule extends Error {

            private final String consName;
            private final ModuleName importedModuleName;

            TypeConstructorNotDefinedInModule (String consName, ModuleName importedModuleName) {
                if (consName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, importedModuleName};
            }
        }        
        
        final static class TypeClassAlreadyUsedInImportUsingDeclaration extends Error {

            private final String className;
            private final ModuleName importedModuleName;

            TypeClassAlreadyUsedInImportUsingDeclaration (String className, ModuleName importedModuleName) {
                if (className == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className, importedModuleName};
            }
        }
        
        final static class TypeClassNameAlreadyUsedInImportUsingDeclaration extends Error {

            private final String className;
            private final ModuleName importedModuleName;

            TypeClassNameAlreadyUsedInImportUsingDeclaration (String className, ModuleName importedModuleName) {
                if (className == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className, importedModuleName};
            }
        }
        
        final static class TypeClassNotDefinedInModule extends Error {

            private final String className;
            private final ModuleName importedModuleName;

            TypeClassNotDefinedInModule (String className, ModuleName importedModuleName) {
                if (className == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className, importedModuleName};
            }
        }        
        
        final static class AttemptToRedeclarePrimitiveFunction extends Error {

            private final String functionName;

            AttemptToRedeclarePrimitiveFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        /**
         * Called when a ForeignContextProvider is asked to provide a classloader to load classes for a module, and the provided returns null
         *   (signalling that it doesn't recognize the module).
         * 
         * For example,
         * In Eclipse, a ForeignContextProvider is provided.  But if this provider is asked by the compiler to provide a classloader for module 
         *   "Bar" which isn't recognized by Eclipse, 
         * 
         * gives:
         * No classloader provided for module Bar.
         * 
         * @author Edward Lam
         */
        public static class NoClassLoaderProvided extends Error {
            private final ModuleName module;

            NoClassLoaderProvided(ModuleName module) {
                if (module == null) {
                    throw new NullPointerException();
                }

                this.module = module;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Object[] getMessageArguments() {
                // No classloader provided for module {0}.
                return new Object[] {module};
            }

        }

        final static class ForeignDeclarationForBuiltinPrimitiveFunction extends Error {

            private final String functionName;

            ForeignDeclarationForBuiltinPrimitiveFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        /**
         * This is a catch-all error message for foreign function declarations where the CAL type is
         * not compatible with the declaration and we do not have a more specific error. 
         * 
         * For example, this currently occurs for:
         * foreign unsafe import jvm "newArray" :: Int -> Char;
         * 
         * @author Bo Ilic
         */
        final static class ForeignFunctionDeclarationInvalidCalType extends Error {
            
            private final String javaOpName;
            
            ForeignFunctionDeclarationInvalidCalType (String javaOpName) {
                if (javaOpName == null) {
                    throw new NullPointerException();
                }
                this.javaOpName = javaOpName;              
            } 
            
            @Override
            Object[] getMessageArguments() {
                //"The type signature is incompatible with the ''{0}'' external descriptor."
                return new Object[] {javaOpName};
            }            
        }        
        
        
        /**
         * Called when a foreign function declaration for a special jvm operator (such as "cast",
         * "instanceof", "isNull", "isNotNull" does not have the correct number of arguments
         * e.g.
         * foreign unsafe import jvm "cast" public castIntToChar :: Int -> Int -> Char; 
         * 
         * @author Bo Ilic
         */
        final static class ForeignFunctionDeclarationMustHaveExactlyNArgs extends Error {
            
            private final String javaOpName;
            private final int expectedNArgs;

            ForeignFunctionDeclarationMustHaveExactlyNArgs (String javaOpName, int expectedNArgs) {
                if (javaOpName == null) {
                    throw new NullPointerException();
                }
                this.javaOpName = javaOpName;
                this.expectedNArgs = expectedNArgs;
            } 
            
            @Override
            Object[] getMessageArguments() {
                //"A foreign function declaration for ''{0}'' must have exactly {1} argument(s)."
                return new Object[] {javaOpName, String.valueOf(expectedNArgs)};
            }            
        } 
            

        /**
         * Called when a foreign function declaration for a special jvm operator (such as "instanceof",
         * "null", "isNull", "isNotNull") is expecting a reference Java type argument but is instead given
         * one with a primitive Java type.
         * 
         * @author Bo Ilic
         */
        final static class ForeignFunctionDeclarationReferenceTypeExpected extends Error {
        
            private final String javaTypeName;          
        
            ForeignFunctionDeclarationReferenceTypeExpected (Class<?> javaTypeName) {
                if (javaTypeName == null) {
                    throw new NullPointerException();
                }
                this.javaTypeName = JavaTypeName.getFullJavaSourceName(javaTypeName);                
            } 
        
            @Override
            Object[] getMessageArguments() {
                //"The implementation type must be a Java reference type and not the Java primitive type ''{0}''."                
                return new Object[] {javaTypeName};
            }            
        } 
        
        /**
         * Called when a foreign function declaration for a cast attempt to cast between invalid
         * foreign types e.g.
         * <ol>
         *    <li> between a reference type and a primitive type
         *    <li> between the boolean primitive type and another type, except boolean itself
         *    <li> involving the void primitive type
         *    <li> a statically determinable invalid reference cast e.g. java.lang.String to java.lang.Integer.
         * </ol>
         * 
         * @author Bo Ilic
         */
        final static class ForeignFunctionDeclarationInvalidCast extends Error {
            
            private final String fromJavaTypeName;
            private final String toJavaTypeName;

            ForeignFunctionDeclarationInvalidCast (Class<?> fromJavaType, Class<?> toJavaType) {
                if (fromJavaType == null || toJavaType == null) {
                    throw new NullPointerException();
                }
                
                //use JavaTypeName.getFullJavaSourceName to convert types like "[I" to the more readable "int[]"
                this.fromJavaTypeName = JavaTypeName.getFullJavaSourceName(fromJavaType);
                this.toJavaTypeName = JavaTypeName.getFullJavaSourceName(toJavaType);
            }  
            
            @Override
            Object[] getMessageArguments() {
                //"Cannot cast the Java type ''{0}'' to the Java type ''{1}''."
                return new Object[] {fromJavaTypeName, toJavaTypeName};
            }            
        }             
        
        /**
         * Called when a foreign function declaration for an "instanceof", "isNull" or "isNotNull" does not return Prelude.Boolean or
         * a foreign type having implementation type boolean.
         * @author Bo Ilic
         */
        final static class ForeignFunctionDeclarationMustReturnBoolean extends Error {
            
            private final String javaOpName;

            ForeignFunctionDeclarationMustReturnBoolean (String javaOpName) {
                if (javaOpName == null) {
                    throw new NullPointerException();
                }
                
                this.javaOpName = javaOpName;
            } 
            
            @Override
            Object[] getMessageArguments() {
                //"A foreign function declaration for ''{0}'' must return Cal.Core.Prelude.Boolean (or a foreign type with Java implementation type boolean)."
                return new Object[] {javaOpName};
            }            
        }
        
        /**
         * Called when 'expr instanceof T' (where expr has Java type S) is invalid Java.
         * Essentially the rule is that S and T must be non-primitive types, and it must
         * be valid to write the Java cast ((T)expr).         
         * 
         * @author Bo Ilic
         */
        final static class ForeignFunctionDeclarationInvalidInstanceOf extends Error {

            /** the Java type name of expr in "expr instanceof T" */
            private final String argumentTypeName;
            /** the Java type name of T in the expression "expr instanceof T" */
            private final String instanceOfTypeName;

            ForeignFunctionDeclarationInvalidInstanceOf (Class<?> argumentType, Class<?> instanceOfType) {
                if (argumentType == null || instanceOfType == null) {
                    throw new NullPointerException();
                }

                //use JavaTypeName.getFullJavaSourceName to convert types like "[I" to the more readable "int[]"
                this.argumentTypeName = JavaTypeName.getFullJavaSourceName(argumentType);
                this.instanceOfTypeName = JavaTypeName.getFullJavaSourceName(instanceOfType);
            }  

            @Override
            Object[] getMessageArguments() {
                //"If ''expr'' has Java type ''{0}'', then ''expr instanceof {1}'' is an invalid Java expression."
                return new Object[] {argumentTypeName, instanceOfTypeName};
            }            
        }        
        
        /**
         * Called when loading a ForeignFunctionInfo.Cast or InstanceOf from a compiled CAL module, and the underlying
         * Java types have changed in the meantime to make the resulting compiled code invalid, or at least require
         * recompilation.
         * 
         * For example, in some cases, if an underlying Java type is changed to be "final", a cast between types will become
         * a Java compile-time error and this error can be triggered.
         *  
         * @author Bo Ilic
         */
        final static class InvalidJavaTypeChangeOnLoading extends Error {
            
            private final QualifiedName calFunctionName;
                      
            InvalidJavaTypeChangeOnLoading (QualifiedName calFunctionName) {
                if (calFunctionName == null) {
                    throw new NullPointerException();
                }
                
                this.calFunctionName = calFunctionName;
            }  
            
            @Override
            Object[] getMessageArguments() {
                //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                return new Object[] {calFunctionName};
            }            
        }                  
        
        final static class AttemptToRedefineFunction extends Error {

            private final String functionName;

            AttemptToRedefineFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class ForeignFunctionNameAlreadyUsedInImportUsingDeclaration extends Error {

            private final String functionName;
            private final ModuleName importedModuleName;

            ForeignFunctionNameAlreadyUsedInImportUsingDeclaration (String functionName, ModuleName importedModuleName) {
                if (functionName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, importedModuleName};
            }
        }               
        
        final static class AttemptToRedefinePrimitiveFunction extends Error {

            private final String functionName;

            AttemptToRedefinePrimitiveFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class FunctionNameAlreadyUsedInImportUsingDeclaration extends Error {

            private final String functionName;
            private final ModuleName importedModuleName;

            FunctionNameAlreadyUsedInImportUsingDeclaration (String functionName, ModuleName importedModuleName) {
                if (functionName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, importedModuleName};
            }
        }
        
        final static class TypeDeclarationForBuiltInPrimitiveFuncton extends Error {

            private final String functionName;

            TypeDeclarationForBuiltInPrimitiveFuncton (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class TypeDeclarationForForeignFunction extends Error {

            private final String functionName;

            TypeDeclarationForForeignFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class DefinitionMissing extends Error {

            private final String declaredName;

            DefinitionMissing (String declaredName) {
                if (declaredName == null) {
                    throw new NullPointerException();
                }

                this.declaredName = declaredName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {declaredName};
            }
        }
        
        final static class RepeatedTypeDeclaration extends Error {

            private final String declaredName;

            RepeatedTypeDeclaration (String declaredName) {
                if (declaredName == null) {
                    throw new NullPointerException();
                }

                this.declaredName = declaredName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {declaredName};
            }
        }
        
        final static class DeclaredTypeOfFunctionNotCompatibleWithInferredType extends Error {

            private final String functionName;
            private final String inferredType;

            DeclaredTypeOfFunctionNotCompatibleWithInferredType (String functionName, String inferredType) {
                if (functionName == null || inferredType == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.inferredType = inferredType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, inferredType};
            }
        }
        
        final static class InstanceHasMultipleVisibleDefinitions extends Error {

            private final String instanceName;
            private final ModuleName firstModule;
            private final ModuleName secondModule;

            InstanceHasMultipleVisibleDefinitions (String instanceName, ModuleName firstModule, ModuleName secondModule) {
                if (instanceName == null || firstModule == null || secondModule == null) {
                    throw new NullPointerException();
                }

                this.instanceName = instanceName;
                this.firstModule = firstModule;
                this.secondModule = secondModule;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {instanceName, firstModule, secondModule};
            }
        }
        
        final static class InstancesOverlap extends Error {

            private final String instanceName;
            private final ModuleName moduleName;
            private final String otherInstanceName;
            private final ModuleName otherModuleName;

            InstancesOverlap (String instanceName, ModuleName moduleName, String otherInstanceName, ModuleName otherModuleName) {
                if (instanceName == null || moduleName == null || otherInstanceName == null || otherModuleName == null) {
                    throw new NullPointerException();
                }

                this.instanceName = instanceName;
                this.moduleName = moduleName;
                this.otherInstanceName = otherInstanceName;
                this.otherModuleName = otherModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {instanceName, moduleName, otherInstanceName, otherModuleName};
            }
        }
        
        final static class SuperclassInstanceDeclarationMissing extends Error {

            private final String parentName;
            private final String instanceName;

            SuperclassInstanceDeclarationMissing (String parentName, String instanceName) {
                if (parentName == null || instanceName == null) {
                    throw new NullPointerException();
                }

                this.parentName = parentName;
                this.instanceName = instanceName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {parentName, instanceName};
            }
        }
        
        final static class ConstraintsOnInstanceDeclarationMustImplyConstraintsOnParentInstance extends Error {

            private final String classDeclName;
            private final String parentDeclName;
            private final String constraint;
            private final int varNumber;

            ConstraintsOnInstanceDeclarationMustImplyConstraintsOnParentInstance (String classDeclName, String parentDeclName, String constraint, int varNumber) {
                
                if (classDeclName == null || parentDeclName == null || constraint == null) {
                    throw new NullPointerException();
                }
                
                this.classDeclName = classDeclName;
                this.parentDeclName = parentDeclName;
                this.constraint = constraint;
                this.varNumber = varNumber;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {classDeclName, parentDeclName, constraint, Integer.valueOf(varNumber)};
            }
        }
        
        final static class MethodDefinedMoreThanOnce extends Error {

            private final String methodName;
            private final String instanceName;

            MethodDefinedMoreThanOnce (String methodName, String instanceName) {
                if (methodName == null || instanceName == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.instanceName = instanceName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName, instanceName};
            }
        }
        
        final static class MethodNotDeclaredByClass extends Error {

            private final String methodName;
            private final String className;

            MethodNotDeclaredByClass (String methodName, String className) {
                if (methodName == null || className == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.className = className;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName, className};
            }
        }
        
        final static class MethodNotDefinedByInstance extends Error {

            private final String methodName;
            private final String instanceName;

            MethodNotDefinedByInstance (String methodName, String instanceName) {
                if (methodName == null || instanceName == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.instanceName = instanceName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName, instanceName};
            }
        }      
        
        final static class RepeatedTypeVariableInInstanceDeclaration extends Error {

            private final String varName;

            RepeatedTypeVariableInInstanceDeclaration (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        final static class TypeVariableMustOccurWithingTypeVariableArgumentsOfInstanceDeclaration extends Error {

            private final String varName;

            TypeVariableMustOccurWithingTypeVariableArgumentsOfInstanceDeclaration (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        final static class DuplicateConstraint extends Error {

            private final String constraintName;
            private final String varName;

            DuplicateConstraint (String constraintName, String varName) {
                if (constraintName == null || varName == null) {
                    throw new NullPointerException();
                }

                this.constraintName = constraintName;
                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {constraintName, varName};
            }
        }
        
        final static class InstanceAlreadyDefined extends Error {

            private final String instanceName;
            private final ModuleName moduleName;

            InstanceAlreadyDefined (String instanceName, ModuleName moduleName) {
                if (instanceName == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.instanceName = instanceName;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {instanceName, moduleName};
            }
        }
        
        final static class InstanceOverlapsWithInstanceInModule extends Error {

            private final String instanceName;
            private final String otherInstanceName;
            private final ModuleName moduleName;

            InstanceOverlapsWithInstanceInModule (String instanceName, String otherInstanceName, ModuleName moduleName) {
                if (instanceName == null || otherInstanceName == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.instanceName = instanceName;
                this.otherInstanceName = otherInstanceName;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {instanceName, otherInstanceName, moduleName};
            }
        }
        
        final static class FunctionNotDefinedInCurrentModule extends Error {

            private final String functionName;

            FunctionNotDefinedInCurrentModule (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class FunctionDoesNotExist extends Error {

            private final String functionName;

            FunctionDoesNotExist (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class FunctionIsClassMethodNotResolvingFunction extends Error {

            private final String functionName;

            FunctionIsClassMethodNotResolvingFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        /**
         * Called when an unqualified reference is made to a class that is public in 2 or more imported modules,
         * where that class's name is not present in any using clause.
         * 
         * Ex:
         *  In M2.cal:
         *      ... public class Identifiable a ...
         *  In Debug.cal:
         *      ... public class Identifiable a ...
         *  In Foo.cal:
         *      import M2;
         *      import Debug;
         * 
         *      instance Identifiable Bar ... 
         * 
         *  gives:
         * 
         *  The reference to the class Identifiable is ambiguous. It could mean any of Debug.Identifiable, M2.Identifiable.
         */
        final static class AmbiguousClassReference extends Error {

            private final String className;
            private final List<QualifiedName> candidateEntityNames;

            /**
             * @param className String Unqualified class name that was ambiguous
             * @param candidateEntityNames List (QualifiedName) of possible candidates 
             *                              (must have >= 2 elements; otherwise use AttemptToUseUndefinedClassSuggestion) 
             */
            AmbiguousClassReference (String className, List<QualifiedName> candidateEntityNames) {
                if (className == null || candidateEntityNames == null) {
                    throw new NullPointerException();
                }
                
                if (candidateEntityNames.size() < 2) {
                    throw new IllegalArgumentException("this message is for ambiguous unqualified names that could refer to 2 or more entities");
                }

                this.className = className;
                this.candidateEntityNames = candidateEntityNames;
            }

            @Override
            Object[] getMessageArguments() {
                
                // Make sure we list the possibilities in alphabetical order
                QualifiedName[] qualifiedNames = candidateEntityNames.<QualifiedName>toArray(new QualifiedName[0]);
                Arrays.sort(qualifiedNames);
                
                StringBuilder qualifiedNamesBuffer = new StringBuilder(qualifiedNames[0].toString());
                for(int i = 1; i < qualifiedNames.length; i++) {
                    qualifiedNamesBuffer.append(", ");
                    qualifiedNamesBuffer.append(qualifiedNames[i].toString());
                }
                
                return new Object[] {className, qualifiedNamesBuffer.toString()};
            }
        }
        
        /**
         * Called when an unqualified reference is made to a function that is public in 2 or more imported modules,
         * where that function's name is not present in any using clause.
         * 
         * Ex:
         *  In M2.cal:
         *      ... public foo x =  ...
         *  In Debug.cal:
         *      ... public foo x =  ...
         *  In Foo.cal:
         *      import M2;
         *      import Debug;
         * 
         *      bar y = foo y; 
         * 
         *  gives:
         * 
         *  The reference to the function foo is ambiguous. It could mean any of Debug.foo, M2.foo.
         */
        final static class AmbiguousFunctionReference extends Error {

            private final String functionName;
            private final List<QualifiedName> candidateFunctionNames;

            /**
             * @param functionName String Unqualified function name that was ambiguous
             * @param candidateFunctionNames List (QualifiedName) of possible candidates 
             *                              (must have >= 2 elements; otherwise use AttemptToUseUndefinedFunctionSuggestion) 
             */
            AmbiguousFunctionReference (String functionName, List<QualifiedName> candidateFunctionNames) {
                if (functionName == null || candidateFunctionNames == null) {
                    throw new NullPointerException();
                }

                if (candidateFunctionNames.size() < 2) {
                    throw new IllegalArgumentException("this message is for ambiguous unqualified names that could refer to 2 or more entities");
                }
                
                this.functionName = functionName;
                this.candidateFunctionNames = candidateFunctionNames;
            }

            @Override
            Object[] getMessageArguments() {
                
                // Make sure we list the possibilities in alphabetical order
                QualifiedName[] qualifiedNames = candidateFunctionNames.<QualifiedName>toArray(new QualifiedName[0]);
                Arrays.sort(qualifiedNames);                
                
                StringBuilder qualifiedNamesBuffer = new StringBuilder(qualifiedNames[0].toString());
                for(int i = 1; i < qualifiedNames.length; i++) {
                    qualifiedNamesBuffer.append(", ");
                    qualifiedNamesBuffer.append(qualifiedNames[i].toString());
                }
                
                return new Object[] {functionName, qualifiedNamesBuffer.toString() };
            }
        }
        
        /**
         * Called when an unqualified reference is made to a type that is public in 2 or more imported modules,
         * where that type's name is not present in any using clause.
         * 
         * Ex:
         *  In M2.cal:
         *      ... data public Foo =  ...
         *  In Debug.cal:
         *      ... data public Foo =  ...
         *  In Foo.cal:
         *      import M2;
         *      import Debug;
         * 
         *      bar :: Foo -> Foo;      
         * 
         *  gives:
         * 
         *  The reference to the type Foo is ambiguous. It could mean any of Debug.Foo, M2.Foo.
         */
        final static class AmbiguousTypeReference extends Error {

            private final String typeName;
            private final List<QualifiedName> candidateTypeNames;
            
            /**
             * @param typeName String Unqualified type name that was ambiguous
             * @param candidateTypeNames List (QualifiedName) of possible candidates 
             *                              (must have >= 2 elements; otherwise use AttemptToUseUndefinedTypeSuggestion) 
             */
            AmbiguousTypeReference (String typeName, List<QualifiedName> candidateTypeNames) {
                if (typeName == null || candidateTypeNames == null) {
                    throw new NullPointerException();
                }

                if (candidateTypeNames.size() < 2) {
                    throw new IllegalArgumentException("this message is for ambiguous unqualified names that could refer to 2 or more entities");
                }

                this.typeName = typeName;
                this.candidateTypeNames = candidateTypeNames;
            }

            @Override
            Object[] getMessageArguments() {
                
                // Make sure we list the possibilities in alphabetical order
                QualifiedName[] qualifiedNames = candidateTypeNames.<QualifiedName>toArray(new QualifiedName[0]);
                Arrays.sort(qualifiedNames);                
                
                StringBuilder qualifiedNamesBuffer = new StringBuilder(qualifiedNames[0].toString());
                for(int i = 1; i < qualifiedNames.length; i++) {
                    qualifiedNamesBuffer.append(", ");
                    qualifiedNamesBuffer.append(qualifiedNames[i].toString());
                }
                
                return new Object[] {typeName, qualifiedNamesBuffer.toString()};
            }
        }
        
        /**
         * Called when an unqualified reference is made to a data constructor that is public in 2 or more imported modules,
         * where that data constructor's name is not present in any using clause.
         * 
         * Ex:
         *  In M2.cal:
         *      ... data public Foo =  Bar ...
         *  In Debug.cal:
         *      ... data public Foo =  Bar ...
         *  In Foo.cal:
         *      import M2;
         *      import Debug;
         * 
         *      baz = Bar;
         * 
         *  gives:
         * 
         *  The reference to the data constructor Bar is ambiguous. It could mean any of Bar, M2.Bar.
         */
        final static class AmbiguousDataConstructorReference extends Error {

            private final String consName;
            private final List<QualifiedName> candidateDataConsNames;

            /**
             * @param consName String Unqualified data constructor name that was ambiguous
             * @param candidateDataConsNames List (QualifiedName) of possible candidates 
             *                              (must have >= 2 elements; otherwise use AttemptToUseUndefinedDataConsSuggestion) 
             */
            AmbiguousDataConstructorReference (String consName, List<QualifiedName> candidateDataConsNames) {
                if (consName == null || candidateDataConsNames == null) {
                    throw new NullPointerException();
                }

                if (candidateDataConsNames.size() < 2) {
                    throw new IllegalArgumentException("this message is for ambiguous unqualified names that could refer to 2 or more entities");
                }

                this.consName = consName;
                this.candidateDataConsNames = candidateDataConsNames;
            }

            @Override
            Object[] getMessageArguments() {
                
                // Make sure we list the possibilities in alphabetical order
                QualifiedName[] qualifiedNames = candidateDataConsNames.<QualifiedName>toArray(new QualifiedName[0]);
                Arrays.sort(qualifiedNames);                
                
                StringBuilder qualifiedNamesBuffer = new StringBuilder(qualifiedNames[0].toString());
                for(int i = 1; i < qualifiedNames.length; i++) {
                    qualifiedNamesBuffer.append(", ");
                    qualifiedNamesBuffer.append(qualifiedNames[i].toString());
                }
                
                return new Object[] {consName, qualifiedNamesBuffer.toString()};
            }
        }
        
        /**
         * Used when a partially qualified module name is ambiguous (i.e. it can potentially refer to 2 or more modules).
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
         * 
         * ...and the module names E and C.D.E would be considered ambiguous.
         * 
         * <p>
         * For example, an appearance of the partially qualified module name E would give:
         * <p>
         * The partially qualified module name "E" is ambiguous. Was one of these intended: A.B.C.D.E, D.E, P.C.D.E?
         * 
         * @author Joseph Wong
         */
        final static class AmbiguousPartiallyQualifiedFormModuleName extends Error {

            private final ModuleName ambiguousModuleName;
            private final ModuleName[] candidateFullyQualifiedModuleNames;

            /**
             * @param ambiguousModuleName data constructor name that was ambiguous
             * @param candidateFullyQualifiedModuleNames possible candidates (must have >= 2 elements) 
             */
            AmbiguousPartiallyQualifiedFormModuleName(ModuleName ambiguousModuleName, ModuleName[] candidateFullyQualifiedModuleNames) {
                if (ambiguousModuleName == null || candidateFullyQualifiedModuleNames == null) {
                    throw new NullPointerException();
                }

                if (candidateFullyQualifiedModuleNames.length < 2) {
                    throw new IllegalArgumentException("this message is for ambiguous partially qualified module names that could refer to 2 or more modules");
                }

                this.ambiguousModuleName = ambiguousModuleName;
                this.candidateFullyQualifiedModuleNames = candidateFullyQualifiedModuleNames.clone();
                
                // Make sure we list the possibilities in alphabetical order
                Arrays.sort(this.candidateFullyQualifiedModuleNames);
            }

            @Override
            Object[] getMessageArguments() {
                
                StringBuilder candidateNamesBuffer = new StringBuilder(candidateFullyQualifiedModuleNames[0].toSourceText());
                for(int i = 1; i < candidateFullyQualifiedModuleNames.length; i++) {
                    candidateNamesBuffer.append(", ");
                    candidateNamesBuffer.append(candidateFullyQualifiedModuleNames[i]);
                }
                
                return new Object[] {ambiguousModuleName, candidateNamesBuffer.toString()};
            }
        }
        
        final static class AttemptToUseUndefinedClassSuggestion extends Error {

            private final String className;
            private final QualifiedName suggestion;

            AttemptToUseUndefinedClassSuggestion (String className, QualifiedName suggestion) {
                if (className == null || suggestion == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.suggestion = suggestion;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className, suggestion.getQualifiedName()};
            }
        }
        
        final static class AttemptToUseUndefinedClass extends Error {

            private final String className;

            AttemptToUseUndefinedClass (String className) {
                if (className == null) {
                    throw new NullPointerException();
                }

                this.className = className;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className};
            }
        }
        
        
        final static class AttemptToUseUndefinedTypeSuggestion extends Error {

            private final String typeName;
            private final QualifiedName suggestion;

            AttemptToUseUndefinedTypeSuggestion (String typeName, QualifiedName suggestion) {
                if (typeName == null || suggestion == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
                this.suggestion = suggestion;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName, suggestion.getQualifiedName()};
            }
        }
        
        /**
         * Used for an attempt to use a foreign type that failed to load from the serialized format.
         * 
         * An example message is:
         * Attempt to use the foreign type "Foo.Bar", whose Java implementation could not be loaded.
         *
         * @author Joseph Wong
         */
        final static class AttemptToUseForeignTypeThatFailedToLoad extends Error {
            
            private final String typeName;

            AttemptToUseForeignTypeThatFailedToLoad (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                //Attempt to use the foreign type ''{0}'', whose Java implementation could not be loaded.
                return new Object[] {typeName};
            }
        }
        
        final static class AttemptToUseUndefinedFunctionSuggestion extends Error {

            private final String functionName;
            private final QualifiedName suggestion;

            AttemptToUseUndefinedFunctionSuggestion (String functionName, QualifiedName suggestion) {
                if (functionName == null || suggestion == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.suggestion = suggestion;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, suggestion.getQualifiedName()};
            }
        }
        
        final static class AttemptToUseUndefinedFunction extends Error {

            private final String functionName;

            AttemptToUseUndefinedFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        /**
         * Used for an attempt to use a foreign foreign that failed to load from the serialized format.
         *
         * An example message is:
         * Attempt to use the foreign function "Foo.someFunction", whose Java implementation could not be loaded.
         *
         * @author Joseph Wong
         */
        final static class AttemptToUseForeignFunctionThatFailedToLoad extends Error {
            
            private final String functionName;

            AttemptToUseForeignFunctionThatFailedToLoad (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                //Attempt to use the foreign function ''{0}'', whose Java implementation could not be loaded.
                return new Object[] {functionName};
            }
        }
        
        final static class AttemptToUseUndefinedDataConstructorSuggestion extends Error {

            private final String consName;
            private final QualifiedName suggestion;

            AttemptToUseUndefinedDataConstructorSuggestion (String consName, QualifiedName suggestion) {
                if (consName == null || suggestion == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.suggestion = suggestion;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, suggestion.getQualifiedName()};
            }
        }
        
        final static class AttemptToUseUndefinedDataConstructor extends Error {

            private final String consName;

            AttemptToUseUndefinedDataConstructor (String consName) {
                if (consName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName};
            }
        }
        
        final static class AttemptToUseUndefinedIdentifier extends Error {

            private final String identName;

            AttemptToUseUndefinedIdentifier (String identName) {
                if (identName == null) {
                    throw new NullPointerException();
                }

                this.identName = identName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {identName};
            }
        }
        
        
        final static class IdentifierDoesNotExist extends Error {

            private final String identName;

            IdentifierDoesNotExist (String identName) {
                if (identName == null) {
                    throw new NullPointerException();
                }

                this.identName = identName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {identName};
            }
        }
        
        final static class FunctionNotVisible extends Error {

            /** name of the function we are looking for. */
            private final Function function;
            
            /** module from which we are trying to resolve the function. */
            private final ModuleName moduleName;

            FunctionNotVisible (Function function, ModuleName moduleName) {
                if (function == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.function = function;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The function {0} is not visible in module {1}."
                return new Object[] {function.getName().getQualifiedName(), moduleName};
            }
        }
        
        final static class ClassMethodNotVisible extends Error {

            /** name of the class method we are looking for. */
            private final ClassMethod classMethod;
            
            /** module from which we are trying to resolve the class method. */
            private final ModuleName moduleName;

            ClassMethodNotVisible (ClassMethod classMethod, ModuleName moduleName) {
                if (classMethod == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.classMethod = classMethod;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The class method {0} is not visible in module {1}."
                return new Object[] {classMethod.getName().getQualifiedName(), moduleName};
            }
        }
        
        /**
         * Called when a default class method does not have the exact same type as the class method that it
         * is a default for.
         * 
         * @author Bo Ilic
         */
        final static class InvalidDefaultClassMethodType extends Error {
            
            private final QualifiedName functionName;
            private final String methodName;
            private final String requiredType;
            private final String actualType;
            
            InvalidDefaultClassMethodType (QualifiedName functionName, String methodName, String requiredType, String actualType) {
                if (functionName == null || methodName == null || requiredType == null || actualType == null) {
                    throw new NullPointerException();
                }
                
                this.functionName = functionName;
                this.methodName = methodName;
                this.requiredType = requiredType;
                this.actualType = actualType;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The default class method {0} for the class method {1} must have type {2}. Instead it has type {3}."
                return new Object[] {functionName, methodName, requiredType, actualType};
            }
        }        
                
        
        final static class ResolvingFunctionForInstanceMethodHasWrongTypeSpecific extends Error {

            private final String functionName;
            private final String methodName;
            private final String requiredType;
            private final String actualType;

            ResolvingFunctionForInstanceMethodHasWrongTypeSpecific (String functionName, String methodName, String requiredType, String actualType) {
                if (functionName == null || methodName == null || requiredType == null || actualType == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.methodName = methodName;
                this.requiredType = requiredType;
                this.actualType = actualType;
            }

            @Override
            Object[] getMessageArguments() {
                //"The resolving function {0} for the instance method {1} must have type {2}. Instead it has type {3}."
                return new Object[] {functionName, methodName, requiredType, actualType};
            }
        }
        
        final static class ResolvingFunctionForInstanceMethodHasWrongTypeGeneral extends Error {

            private final String functionName;
            private final String methodName;
            private final String requiredType;
            private final String actualType;

            ResolvingFunctionForInstanceMethodHasWrongTypeGeneral (String functionName, String methodName, String requiredType, String actualType) {
                if (functionName == null || methodName == null || requiredType == null || actualType == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.methodName = methodName;
                this.requiredType = requiredType;
                this.actualType = actualType;
            }

            @Override
            Object[] getMessageArguments() {
                //"The resolving function {0} for the instance method {1} must have a type that can specialize to {2}. Instead it has type {3}."
                return new Object[] {functionName, methodName, requiredType, actualType};
            }
        }
        
        final static class RepeatedTypeVariable extends Error {

            private final String varName;

            RepeatedTypeVariable (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        final static class KindErrorInDataConstructorForType extends Error {

            private final String consName;
            private final String typeName;

            KindErrorInDataConstructorForType (String consName, String typeName) {
                if (consName == null || typeName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, typeName};
            }
        }
        
        final static class RepeatedDefinitionOfForeignType extends Error {

            private final String typeName;

            RepeatedDefinitionOfForeignType (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }
        
        final static class ForeignTypeAlreadyUsedInImportUsingDeclaration extends Error {

            private final String typeName;
            private final ModuleName importedModuleName;

            ForeignTypeAlreadyUsedInImportUsingDeclaration (String typeName, ModuleName importedModuleName) {
                if (typeName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName, importedModuleName};
            }
        }
        
        final static class DataConstructorNameAlreadyUsedInImportUsingDeclaration extends Error {

            private final String consName;
            private final ModuleName importedModuleName;

            DataConstructorNameAlreadyUsedInImportUsingDeclaration (String consName, ModuleName importedModuleName) {
                if (consName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.importedModuleName = importedModuleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, importedModuleName};
            }
        }
        
        final static class ForeignTypeHasSameNameAsBuiltInType extends Error {

            private final String typeName;

            ForeignTypeHasSameNameAsBuiltInType (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }
        
        final static class ExternalClassNotFound extends Error {

            private final String className;
            private final String typeName;

            ExternalClassNotFound (String className, String typeName) {
                if (className == null || typeName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The external class {0} representing {1} was not found."
                return new Object[] {className, typeName};
            }
        }
        
        /**
         * Called for foreign data declarations where the Java class or interface is not public in scope.
         * 
         * @author Bo Ilic
         */
        final static class ExternalClassNotAccessible extends Error {
            
            private final Class<?> externalClass;           
            
            ExternalClassNotAccessible (Class<?> externalClass) {
                if (externalClass == null) {
                    throw new NullPointerException();
                }
                
                this.externalClass = externalClass;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The Java type ''{0}'' is not accessible. It does not have public scope or is in an unnamed package."
                return new Object[] {externalClass};
            }
        }        
        
        final static class ExternalClassCouldNotBeInitialized extends Error {

            private final String className;
            private final String typeName;

            ExternalClassCouldNotBeInitialized (String className, String typeName) {
                if (className == null || typeName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className, typeName};
            }
        }
        
        final static class TypeHasSameNameAsBuiltInType extends Error {

            private final String typeName;

            TypeHasSameNameAsBuiltInType (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }
        
        final static class TypeHasSameNameAsForeignType extends Error {

            private final String typeName;

            TypeHasSameNameAsForeignType (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }
        
        final static class RepeatedDefinitionOfType extends Error {

            private final String typeName;

            RepeatedDefinitionOfType (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }        
        
        final static class RepeatedDefinitionOfDataConstructor extends Error {

            private final String consName;

            RepeatedDefinitionOfDataConstructor (String consName) {
                if (consName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName};
            }
        }
        
        final static class TypeVariableMustAppearOnLHSOfDataDeclaration extends Error {

            private final String varName;

            TypeVariableMustAppearOnLHSOfDataDeclaration (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        final static class RecordVarsCannotAppearInDataDeclarations extends Error {

            private final String varName;

            RecordVarsCannotAppearInDataDeclarations (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        final static class UnboundTypeVariable extends Error {

            private final String varName;

            UnboundTypeVariable (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        final static class AttemptToUseUndefinedType extends Error {

            private final String typeName;

            AttemptToUseUndefinedType (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }
        
        final static class ModuleHasNotBeenImported extends Error {

            private final ModuleName unimportedModule;
            private final ModuleName currentModule;

            ModuleHasNotBeenImported (ModuleName unimportedModule, ModuleName currentModule) {
                if (unimportedModule == null || currentModule == null) {
                    throw new NullPointerException();
                }

                this.unimportedModule = unimportedModule;
                this.currentModule = currentModule;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {unimportedModule, currentModule};
            }
        }
        
        final static class TypeDoesNotExist extends Error {

            private final String typeName;

            TypeDoesNotExist (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }
        
        final static class TypeConstructorNotVisible extends Error {

            /** name of the type constructor we are looking for. */
            private final TypeConstructor typeCons;
            
            /** module from which we are trying to resolve the type class. */
            private final ModuleName moduleName;

            TypeConstructorNotVisible (TypeConstructor typeCons, ModuleName moduleName) {
                if (typeCons == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.typeCons = typeCons;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The type {0} is not visible in module {1}."
                return new Object[] {typeCons.getName().getQualifiedName(), moduleName};
            }
        }
        
        final static class ExpectingMethodFieldOrConstructor extends Error {

            private final String actualKeyword;

            ExpectingMethodFieldOrConstructor (String actualKeyword) {
                if (actualKeyword == null) {
                    throw new NullPointerException();
                }

                this.actualKeyword = actualKeyword;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {actualKeyword};
            }
        }
        
        final static class ExpectingStatic extends Error {

            private final String actualKeyword;

            ExpectingStatic (String actualKeyword) {
                if (actualKeyword == null) {
                    throw new NullPointerException();
                }

                this.actualKeyword = actualKeyword;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {actualKeyword};
            }
        }
        
        final static class ExpectingMethodOrField extends Error {

            private final String actualKeyword;

            ExpectingMethodOrField (String actualKeyword) {
                if (actualKeyword == null) {
                    throw new NullPointerException();
                }

                this.actualKeyword = actualKeyword;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {actualKeyword};
            }
        }
        
        final static class InvalidExternalNameStringFormat extends Error {

            private final String stringFormat;

            InvalidExternalNameStringFormat (String stringFormat) {
                if (stringFormat == null) {
                    throw new NullPointerException();
                }

                this.stringFormat = stringFormat;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {stringFormat};
            }
        }
        
        /**
         * Called in foreign function declarations for static methods and static fields when an unqualified Java name is given.
         * 
         * @author Bo Ilic
         */
        final static class JavaNameMustBeFullyQualified extends Error {

            private final String javaName;

            JavaNameMustBeFullyQualified (String javaName) {
                if (javaName == null) {
                    throw new NullPointerException();
                }

                this.javaName = javaName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The Java name {0} must be fully qualified (i.e. include the package name)."
                return new Object[] {javaName};
            }
        }
        
        /**
         * Called in foreign function declarations for non-static methods and non-static fields when a qualified Java name is given.
         * 
         * @author Bo Ilic
         */
        final static class JavaNameMustBeUnqualified extends Error {

            private final String javaName;

            JavaNameMustBeUnqualified (String javaName) {
                if (javaName == null) {
                    throw new NullPointerException();
                }

                this.javaName = javaName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The Java name {0} must be unqualified (i.e. omit the package name)." 
                return new Object[] {javaName};
            }
        }        
        
        final static class CouldNotFindMethodWithGivenArgumentTypes extends Error {

            private final String methodName;
            private final String argumentTypes;

            CouldNotFindMethodWithGivenArgumentTypes (String methodName, String argumentTypes) {
                if (methodName == null || argumentTypes == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.argumentTypes = argumentTypes;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName, argumentTypes};
            }
        }
       
        final static class SecurityViolationTryingToAccess extends Error {

            private final String memberName;

            SecurityViolationTryingToAccess (String memberName) {
                if (memberName == null) {
                    throw new NullPointerException();
                }

                this.memberName = memberName;
            }
            @Override
            Object[] getMessageArguments() {
                // Security violation trying to access {0}.
                return new Object[] {memberName};
            }
        }
        
        final static class CouldNotFindStaticMethodWithGivenArgumentTypes extends Error {

            private final String methodName;
            private final String argumentTypes;

            CouldNotFindStaticMethodWithGivenArgumentTypes (String methodName, String argumentTypes) {
                if (methodName == null || argumentTypes == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.argumentTypes = argumentTypes;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName, argumentTypes};
            }
        }
        
        final static class CouldNotFindNonStaticMethodWithGivenArgumentTypes extends Error {

            private final String methodName;
            private final String argumentTypes;

            CouldNotFindNonStaticMethodWithGivenArgumentTypes (String methodName, String argumentTypes) {
                if (methodName == null || argumentTypes == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
                this.argumentTypes = argumentTypes;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName, argumentTypes};
            }
        }
        
        final static class ForeignFunctionReturnsWrongType extends Error {

            private final String functionName;
            private final String returnType;

            ForeignFunctionReturnsWrongType (String functionName, String returnType) {
                if (functionName == null || returnType == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
                this.returnType = returnType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName, returnType};
            }
        }
        
        final static class StaticFieldMustHaveNoArguments extends Error {

            private final String fieldName;

            StaticFieldMustHaveNoArguments (String fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName};
            }
        }
        
        final static class CouldNotFindField extends Error {

            private final String fieldName;

            CouldNotFindField (String fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName};
            }
        }
        
        final static class CouldNotFindStaticField extends Error {

            private final String fieldName;

            CouldNotFindStaticField (String fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName};
            }
        }
        
        final static class CouldNotFindNonStaticField extends Error {

            private final String fieldName;

            CouldNotFindNonStaticField (String fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName};
            }
        }
        
        final static class FieldReturnsWrongType extends Error {

            private final String fieldName;
            private final String returnType;

            FieldReturnsWrongType (String fieldName, String returnType) {
                if (fieldName == null || returnType == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
                this.returnType = returnType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName, returnType};
            }
        }
        
        final static class ConstructorReturnsWrongType extends Error {

            private final String consName;
            private final String returnType;

            ConstructorReturnsWrongType (String consName, String returnType) {
                if (consName == null || returnType == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.returnType = returnType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, returnType};
            }
        }
        
        final static class CouldNotFindConstructorWithGivenArgumentTypes extends Error {

            private final String consName;
            private final String typeList;

            CouldNotFindConstructorWithGivenArgumentTypes (String consName, String typeList) {
                if (consName == null || typeList == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.typeList = typeList;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName, typeList};
            }
        }
        
        final static class JavaClassNotFound extends Error {

            private final String javaClass;

            JavaClassNotFound (String javaClass) {
                if (javaClass == null) {
                    throw new NullPointerException();
                }

                this.javaClass = javaClass;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {javaClass};
            }
        }

        /**
         * Called when an attempt to load a class fails during deserialization.
         * ie. ClassNotFoundException.
         * 
         * For example,
         * Deserialization of ForeignFunctionInfo for foreign function Foo.Bar which refers to non-existent class com.Foo
         * 
         * gives:
         * The Java class com.Foo was not found while loading Foo.Bar.
         * 
         * @author Edward Lam
         */
        final static class JavaClassNotFoundWhileLoading extends Error {

            private final String javaClass;
            private final String recordLoadName;

            JavaClassNotFoundWhileLoading (String javaClass, String recordLoadName) {
                if (javaClass == null || recordLoadName == null) {
                    throw new NullPointerException();
                }

                this.javaClass = javaClass;
                this.recordLoadName = recordLoadName;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Object[] getMessageArguments() {
                // The Java class {0} was not found while loading {1}.
                return new Object[] {javaClass, recordLoadName};
            }
        }

        /**
         * Called when a class instantiated using Class.forName() could not be initialized.
         * ie. ExceptionInInitializerError.
         * 
         * For example,
         * Class.forName() is called on a class com.Foo which contains:
         *     static {
         *         Object foo = null;
         *         foo.hashCode();
         *     }
         * 
         * gives:
         * The Java class com.Foo could not be initialized.
         * 
         * @author Edward Lam
         */
        final static class JavaClassCouldNotBeInitialized extends Error {

            private final String className;

            JavaClassCouldNotBeInitialized (String className) {
                if (className == null) {
                    throw new NullPointerException();
                }

                this.className = className;
            }

            @Override
            Object[] getMessageArguments() {
                // The Java class {0} could not be initialized.
                return new Object[] {className};
            }
        }
        
        /**
         * Called when a class required by a class instantiated using class.forName() was not found.
         * ie. NoClassDefFoundError.
         * 
         * For example,
         * - Java class c1 depends on Java class c2.
         * - ForeignFunctionChecker: Class.forName(fullyQualifiedName_c1), but c2 is not on the classpath.
         * 
         * gives:
         * The Java class c2 was not found.  This class is required by c1.
         * 
         * @author Edward Lam
         */
        final static class DependeeJavaClassNotFound extends Error {

            private final String dependeeJavaClass;
            private final String javaClass;

            DependeeJavaClassNotFound (String dependeeJavaClass, String javaClass) {
                if (dependeeJavaClass == null || javaClass == null) {
                    throw new NullPointerException();
                }

                this.dependeeJavaClass = dependeeJavaClass;
                this.javaClass = javaClass;
            }

            @Override
            Object[] getMessageArguments() {
                // The Java class {0} was not found.  This class is required by {1}.
                return new Object[] {dependeeJavaClass, javaClass};
            }
        }
        
        /**
         * Called when a class required by a class instantiated using class.forName() was not found.
         * ie. NoClassDefFoundError.
         * 
         * For example,
         * - Java class c1 depends on Java class c2.
         * - ForeignFunctionChecker: Class.forName(fullyQualifiedName_c1), but c2 is not on the classpath.
         * 
         * gives:
         * The Java class c2 was not found.  This class is required by c1.
         * 
         * @author Edward Lam
         */
        /**
         * Called when a class could not be loaded using Class.forName().
         * This is used for a NoClassDefFoundError with a null message.
         * 
         * For example,
         * Class.forName() is called twice on a class com.Foo which contains:
         *     static {
         *         Object foo = null;
         *         foo.hashCode();
         *     }
         * 
         * The first call results in an ExceptionInInitializerError.
         * The second call gives:
         * The definition of Java class com.Foo could not be loaded.
         * 
         * @author Edward Lam
         */
        final static class JavaClassDefinitionCouldNotBeLoaded extends Error {

            private final String className;

            JavaClassDefinitionCouldNotBeLoaded(String className) {
                if (className == null) {
                    throw new NullPointerException();
                }

                this.className = className;
            }

            @Override
            Object[] getMessageArguments() {
                // The definition of Java class {0} could not be loaded.
                return new Object[] {className};
            }
        }
        
        final static class ProblemsUsingJavaClass extends Error {

            private final String javaClass;
            private final LinkageError e;

            ProblemsUsingJavaClass (String javaClass, LinkageError e) {
                this.e = e;
                if (javaClass == null || e == null) {
                    throw new NullPointerException();
                }

                this.javaClass = javaClass;
            }

            @Override
            Object[] getMessageArguments() {
                String errorClassName = e.getClass().getName();
                String message = e.getMessage();
                return new Object[] {javaClass, errorClassName, message};
            }
        }
        
        final static class ImplementationAsForeignTypeNotVisible extends Error {

            private final String calType;

            ImplementationAsForeignTypeNotVisible (String calType) {
                if (calType == null) {
                    throw new NullPointerException();
                }

                this.calType = calType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {calType};
            }
        }
        
        final static class TypeNotSupportedForForeignCalls extends Error {

            private final String calType;

            TypeNotSupportedForForeignCalls (String calType) {
                if (calType == null) {
                    throw new NullPointerException();
                }

                this.calType = calType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {calType};
            }
        }
        
        final static class RepeatedVariableUsedInBinding extends Error {

            private final String varName;

            RepeatedVariableUsedInBinding (String varName) {
                if (varName == null) {
                    throw new NullPointerException();
                }

                this.varName = varName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {varName};
            }
        }
        
        /**
         * Called when a record literal uses a field value updated operator e.g.
         * {colour = "red", height := 2.0}
         * The field value update operator ":=" can only be used for a record modification
         * i.e. there must be a "|".
         * 
         * @author Bo Ilic
         */
        final static class FieldValueUpdateOperatorUsedInRecordLiteralValue extends Error {
          
            FieldValueUpdateOperatorUsedInRecordLiteralValue () {              
            }

            @Override
            Object[] getMessageArguments() {
                //"The field value update operator ':=' can not be used in a record literal value."
                return new Object[] {};
            }
        }        
        
        /**
         * Called when a record literal (or record modification) uses a repeated field name e.g.
         * {colour = "red", height = 2.0, colour = "blue"}
         * {rec | colour := "red", height := 2.0, colour = "blue"}
         * 
         * @author Bo Ilic
         */
        final static class RepeatedFieldNameInRecordLiteralValue extends Error {

            private final FieldName fieldName;

            RepeatedFieldNameInRecordLiteralValue (FieldName fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                //"Repeated field name {0} in record literal value."
                return new Object[] {fieldName.getCalSourceForm()};
            }
        }
        
        /**
         * Called when a data declaration contains a data constructor which reuses a field name.
         * 
         * For example,
         * 
         * data Foo = 
         *     private Foo 
         *         bar::Int
         *         bar::Double;
         *     ;
         * 
         * gives:
         * Repeated field name bar in data constructor declaration.
         */
        final static class RepeatedFieldNameInDataConstructorDeclaration extends Error {

            private final FieldName fieldName;

            RepeatedFieldNameInDataConstructorDeclaration (FieldName fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                //"Repeated field name {0} in data constructor declaration."
                return new Object[] {fieldName.getCalSourceForm()};
            }
        }
        
        final static class RepeatedPatternInCaseExpression extends Error {

            private final String pattern;

            RepeatedPatternInCaseExpression (String pattern) {
                if (pattern == null) {
                    throw new NullPointerException();
                }

                this.pattern = pattern;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {pattern};
            }
        }
        
        /**
         * Called when a case expression contains alt unpack patterns on the same value.
         * 
         * For example,
         * 
         * case foo of
         *     (1 | 1) -> True;
         *     ;
         * 
         * gives:
         * Repeated pattern value 1 in case expression.
         * 
         * @author Edward Lam
         */
        final static class RepeatedPatternValueInCaseExpression extends Error {

            private final String pattern;

            RepeatedPatternValueInCaseExpression (String pattern) {
                if (pattern == null) {
                    throw new NullPointerException();
                }

                this.pattern = pattern;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {pattern};
            }
        }
        
        /**
         * Called when a data constructor case alt using matching notation attempts to bind a field name more than once.
         * 
         * For example,
         * 
         * data Foo = 
         *     private Foo 
         *         bar::Int
         *         baz::Double;
         * 
         * functionUsingRepeatedFieldNameBinding x =
         *     case x of
         *         Foo {bar=qux, bar=quux} = 2.0;
         *     ;
         * 
         * gives:
         * Repeated field name bar in field binding pattern.
         */
        final static class RepeatedFieldNameInFieldBindingPattern extends Error {

            private final FieldName fieldName;

            RepeatedFieldNameInFieldBindingPattern (FieldName fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                //"Repeated field name {0} in field binding pattern."
                return new Object[] {fieldName};
            }
        }
        
        /**
         * Called when a data constructor case alt using matching notation attempts to bind to a pattern variable more than once.
         * 
         * For example,
         * 
         * data Foo = 
         *     private Foo 
         *         bar::Int
         *         baz::Double;
         * 
         * functionUsingRepeatedPatternVarBinding x =
         *     case x of
         *         Foo {bar=qux, baz=qux} = 2.0;
         *     ;
         * 
         * gives:
         * Repeated pattern variable qux in field binding pattern.
         */
        final static class RepeatedPatternVariableInFieldBindingPattern extends Error {

            private final String patternVar;

            RepeatedPatternVariableInFieldBindingPattern (String patternVar) {
                if (patternVar == null) {
                    throw new NullPointerException();
                }

                this.patternVar = patternVar;
            }

            @Override
            Object[] getMessageArguments() {
                //"Repeated pattern variable {0} in field binding pattern. "
                return new Object[] {patternVar};
            }
        }
        
        final static class RepeatedDefinitionInLetDeclaration extends Error {

            private final String token;

            RepeatedDefinitionInLetDeclaration (String token) {
                if (token == null) {
                    throw new NullPointerException();
                }

                this.token = token;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {token};
            }
        }
        
        /**
         * Used when a local pattern match declaration is used with a multiple data consturctor pattern.
         * 
         * For example,
         * <pre>
         * foo = let (Prelude.Left|Prelude.Right) {value} = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * Patterns with multiple data constructors cannot be used in a local pattern match declaration.
         *
         * @author Joseph Wong
         */
        final static class InvalidLocalPatternMatchMultipleDataConsPattern extends Error {
            InvalidLocalPatternMatchMultipleDataConsPattern() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with an empty list [] pattern.
         * 
         * For example,
         * <pre>
         * foo = let [] = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * The empty list pattern ''[]'' cannot be used in a local pattern match declaration.
         *
         * @author Joseph Wong
         */
        final static class InvalidLocalPatternMatchNilPattern extends Error {
            InvalidLocalPatternMatchNilPattern() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with a unit () pattern.
         * 
         * For example,
         * <pre>
         * foo = let () = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * The unit pattern ''()'' cannot be used in a local pattern match declaration.
         *
         * @author Joseph Wong
         */
        final static class InvalidLocalPatternMatchUnitPattern extends Error {
            InvalidLocalPatternMatchUnitPattern() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with an integer pattern.
         * 
         * For example,
         * <pre>
         * foo = let -42 = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * Integer patterns cannot be used in a local pattern match declaration.
         *
         * @author Joseph Wong
         */
        final static class InvalidLocalPatternMatchIntPattern extends Error {
            InvalidLocalPatternMatchIntPattern() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with a character pattern.
         * 
         * For example,
         * <pre>
         * foo = let ('X'|'Y') = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * Character patterns cannot be used in a local pattern match declaration.
         *
         * @author Joseph Wong
         */
        final static class InvalidLocalPatternMatchCharPattern extends Error {
            InvalidLocalPatternMatchCharPattern() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with just a wildcard pattern.
         * 
         * For example,
         * <pre>
         * foo = let _ = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * A local pattern match declaration cannot be used with just the wildcard pattern ''_''.
         *
         * @author Joseph Wong
         */
        final static class InvalidLocalPatternMatchWildcardPattern extends Error {
            InvalidLocalPatternMatchWildcardPattern() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with a pattern that contains no pattern variables.
         * 
         * For example,
         * <pre>
         * foo = let Prelude.Just {} = Prelude.undefined; in 3.0;
         * </pre>
         * or
         * <pre>
         * foo = let {#1} = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * A local pattern match declaration must contain at least one pattern variable.
         *
         * @author Joseph Wong
         */
        final static class LocalPatternMatchDeclMustContainAtLeastOnePatternVar extends Error {
            LocalPatternMatchDeclMustContainAtLeastOnePatternVar() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with a base record pattern that is not the wildcard pattern.
         * 
         * For example,
         * <pre>
         * foo = let {r|a} = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * Only the wildcard pattern ''_'' can be used as the base record pattern in a local pattern match declaration.
         *
         * @author Joseph Wong
         */
        final static class NonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl extends Error{
            NonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl() {}
        }
        
        /**
         * Used when a local pattern match declaration is used with a data constructor pattern with the wrong number of
         * positional patterns.
         * 
         * For example,
         * <pre>
         * foo = let Prelude.Cons _ = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * The data constructor Cal.Core.Prelude.Cons must have exactly 2 pattern arguments.
         *
         * @author Joseph Wong
         */
        final static class ConstructorMustHaveExactlyNArgsInLocalPatternMatchDecl extends Error {

            private final DataConstructor dataCons;           

            ConstructorMustHaveExactlyNArgsInLocalPatternMatchDecl(final DataConstructor dataCons) {
                if (dataCons == null) {
                    throw new NullPointerException();
                }
                
                this.dataCons = dataCons;                
            }

            @Override
            Object[] getMessageArguments() {
                //"The data constructor {0} must have exactly {1} pattern argument(s)."
                return new Object[] {dataCons.getName().getQualifiedName(), Integer.valueOf(dataCons.getArity())};
            }
        }
        
        /**
         * Used when a local pattern match declaration is used with a data constructor pattern where the data constructor
         * has an arity of 0.
         * 
         * For example,
         * <pre>
         * foo = let Prelude.Nil = Prelude.undefined; in 3.0;
         * </pre>
         * 
         * gives:
         * The data constructor Cal.Core.Prelude.Nil cannot be used in a local pattern match declaration because it has no fields.
         *
         * @author Joseph Wong
         */
        final static class ConstructorMustHavePositiveArityInLocalPatternMatchDecl extends Error {

            private final DataConstructor dataCons;           

            ConstructorMustHavePositiveArityInLocalPatternMatchDecl(final DataConstructor dataCons) {
                if (dataCons == null) {
                    throw new NullPointerException();
                }
                
                this.dataCons = dataCons;                
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {dataCons.getName().getQualifiedName()};
            }
        }
        
        /**
         * Used when the type of a local pattern-bound variable is not compatible with the defining expression of the local pattern match declaration.
         * 
         * For example,
         * <pre>
         * foo2 = let a:b = [Prelude.fromJust b]; in a;
         * </pre>
         * 
         * gives:
         * The type of the local pattern-bound variable b is not compatible with the defining expression of the local pattern match declaration.
         *
         * @author Joseph Wong
         */
        final static class TypeOfPatternBoundVariableNotCompatibleWithLocalPatternMatchDecl extends Error {

            private final String patternVarName;

            TypeOfPatternBoundVariableNotCompatibleWithLocalPatternMatchDecl(final String patternVarName) {
                if (patternVarName == null) {
                    throw new NullPointerException();
                }

                this.patternVarName = patternVarName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {patternVarName};
            }
        }
        
        /**
         * Used when the type of the defining expression of the local pattern match declaration is not compatible with the type(s) of the corresponding pattern-bound variable(s).
         * 
         * For example,
         * <pre>
         * foo19 = let {ax, b2} = (b2, ax); in (ax, b2);
         * </pre>
         * 
         * gives:
         * The type of the defining expression of the local pattern match declaration is not compatible with the type(s) of the corresponding pattern-bound variable(s).
         *
         * @author Joseph Wong
         */
        final static class TypeOfDesugaredDefnOfLocalPatternMatchDeclNotCompatibleWithPatternBoundVars extends Error {
            TypeOfDesugaredDefnOfLocalPatternMatchDeclNotCompatibleWithPatternBoundVars() {}
        }
        
        /**
         * Used when the type the defining expression of this local pattern match declaration does not have all and only the declared fields.
         * 
         * For example,
         * <pre>
         * foo6 = let {a} = {a = "foo", b = "bar"}; in a;
         * </pre>
         * 
         * gives:
         * Type Error. The type of the defining expression of this local pattern match declaration must have all and only the declared fields {a}.
         *
         * @author Joseph Wong
         */
        final static class LocalPatternMatchDeclMustHaveFields extends Error  {
            
            private final TypeExpr typeExpr;
            private final SortedSet<FieldName> fieldNames;
            
            LocalPatternMatchDeclMustHaveFields(final TypeExpr typeExpr, final SortedSet<FieldName> fieldNames) {
                if (typeExpr == null || fieldNames == null) {
                    throw new NullPointerException();
                }

                this.typeExpr = typeExpr;
                this.fieldNames = fieldNames;
            }

            @Override
            Object[] getMessageArguments() {
                final StringBuilder fieldNamesBuffer = new StringBuilder("{");
                boolean isFirst = true;
                for (final FieldName fieldName : fieldNames) {                    
                    if (!isFirst) {
                        fieldNamesBuffer.append(", ");
                    }
                    fieldNamesBuffer.append(fieldName);
                    isFirst = false;
                }
                fieldNamesBuffer.append("}");
                
                return new Object[] {typeExpr.toString(), fieldNamesBuffer.toString()};
            }
        }

        /**
         * Used when the type the defining expression of this local pattern match declaration does not have at least the declared fields.
         * 
         * For example,
         * <pre>
         * foo6x = let {_|#1,a} = {a = "foo"}; in a;
         * </pre>
         * 
         * gives:
         * Type Error. The type of the defining expression of this local pattern match declaration must have at least the declared fields {#1, a}.
         *
         * @author Joseph Wong
         */
        final static class LocalPatternMatchDeclMustAtLeastHaveFields extends Error  {
            
            private final TypeExpr typeExpr;
            private final SortedSet<FieldName> fieldNames;
            
            LocalPatternMatchDeclMustAtLeastHaveFields(final TypeExpr typeExpr, final SortedSet<FieldName> fieldNames) {
                if (typeExpr == null || fieldNames == null) {
                    throw new NullPointerException();
                }

                this.typeExpr = typeExpr;
                this.fieldNames = fieldNames;
            }

            @Override
            Object[] getMessageArguments() {
                final StringBuilder fieldNamesBuffer = new StringBuilder("{");
                boolean isFirst = true;
                for (final FieldName fieldName : fieldNames) {                   
                    if (!isFirst) {
                        fieldNamesBuffer.append(", ");
                    }
                    fieldNamesBuffer.append(fieldName);
                    isFirst = false;
                }
                fieldNamesBuffer.append("}");
                
                return new Object[] {typeExpr.toString(), fieldNamesBuffer.toString()};
            }
        }
        
        /**
         * Used when the type of the defining expression of this local pattern match declaration is not a tuple type with the declared dimension.
         * 
         * For example,
         * <pre>
         * foo7 = let (a, b) = ('a', 'b', 'c'); in a;
         * </pre>
         * 
         * gives:
         * Type Error. The type of the defining expression of this local pattern match declaration must be a tuple type with dimension 2.
         *
         * @author Joseph Wong
         */
        final static class LocalPatternMatchDeclMustHaveTupleDimension extends Error  {
            
            private final TypeExpr typeExpr;
            private final int tupleDimension;
            
            LocalPatternMatchDeclMustHaveTupleDimension(final TypeExpr typeExpr, final int tupleDimension) {
                if (typeExpr == null) {
                    throw new NullPointerException();
                }

                this.typeExpr = typeExpr;
                this.tupleDimension = tupleDimension;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeExpr.toString(), Integer.valueOf(tupleDimension)};
            }
        }
        
        final static class DataConstructorDoesNotExist extends Error {

            private final QualifiedName consName;

            DataConstructorDoesNotExist (QualifiedName consName) {
                if (consName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName.getQualifiedName()};
            }
        }
        
        final static class DataConstructorDoesNotExistInModule extends Error {

            private final QualifiedName consName;
            private final ModuleName moduleName;

            DataConstructorDoesNotExistInModule (QualifiedName consName, ModuleName moduleName) {
                if (consName == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.consName = consName;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {consName.getQualifiedName(), moduleName};
            }
        }
        
        final static class DataConstructorNotVisible extends Error {

            /** name of the data constructor we are looking for */
            private final DataConstructor dataCons;
            
            /** module from which we are trying to resolve the data constructor. */
            private final ModuleName moduleName;

            DataConstructorNotVisible (DataConstructor dataCons, ModuleName moduleName) {
                if (dataCons == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.dataCons = dataCons;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The data constructor {0} is not visible in module {1}."
                return new Object[] {dataCons.getName().getQualifiedName(), moduleName};
            }
        }
        
        final static class RuntimeInputHasAmbiguousType extends Error {

            private final String expr;

            RuntimeInputHasAmbiguousType (String expr) {
                if (expr == null) {
                    throw new NullPointerException();
                }

                this.expr = expr;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {expr};
            }
        }
        
        final static class UnableToMakeInstanceTypeNotVisible extends Error {

            private final String typeName;
            private final String typeClass;

            UnableToMakeInstanceTypeNotVisible (String typeName, String typeClass) {
                if (typeName == null || typeClass == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
                this.typeClass = typeClass;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName, typeClass};
            }
        }
        
        final static class UnableToMakeInstanceNoDataConstructors extends Error {

            private final String typeName;
            private final String typeClass;

            UnableToMakeInstanceNoDataConstructors (String typeName, String typeClass) {
                if (typeName == null || typeClass == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
                this.typeClass = typeClass;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName, typeClass};
            }
        }
        
        final static class UnableToMakeInstanceNoVisibleDataConstructors extends Error {

            private final String typeName;
            private final String typeClass;

            UnableToMakeInstanceNoVisibleDataConstructors (String typeName, String typeClass) {
                if (typeName == null || typeClass == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
                this.typeClass = typeClass;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName, typeClass};
            }
        }
        
        final static class ConflictingNameInModule extends Error {

            private final ModuleName moduleName;

            ConflictingNameInModule (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class RepeatedDefinitionOfClass extends Error {

            private final String className;

            RepeatedDefinitionOfClass (String className) {
                if (className == null) {
                    throw new NullPointerException();
                }

                this.className = className;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className};
            }
        }
        
        final static class NameAlreadyUsedAsFunctionName extends Error {

            private final String name;

            NameAlreadyUsedAsFunctionName (String name) {
                if (name == null) {
                    throw new NullPointerException();
                }

                this.name = name;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {name};
            }
        }
        
        final static class RepeatedDefinitionOfClassMethod extends Error {

            private final String methodName;

            RepeatedDefinitionOfClassMethod (String methodName) {
                if (methodName == null) {
                    throw new NullPointerException();
                }

                this.methodName = methodName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {methodName};
            }
        }
        
        final static class RepeatedClassConstraint extends Error {

            private final String constraint;

            RepeatedClassConstraint (String constraint) {
                if (constraint == null) {
                    throw new NullPointerException();
                }

                this.constraint = constraint;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {constraint};
            }
        }
        
        final static class ContextTypeVariableUndefined extends Error {

            private final String typeVar;
            private final String requiredName;

            ContextTypeVariableUndefined (String typeVar, String requiredName) {
                if (typeVar == null || requiredName == null) {
                    throw new NullPointerException();
                }

                this.typeVar = typeVar;
                this.requiredName = requiredName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeVar, requiredName};
            }
        }
        
        final static class ClassDoesNotExistInModule extends Error {

            private final String className;
            private final ModuleName moduleName;

            ClassDoesNotExistInModule (String className, ModuleName moduleName) {
                if (className == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.className = className;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className, moduleName};
            }
        }
        
        final static class TypeClassDoesNotExist extends Error {

            private final String className;

            TypeClassDoesNotExist (String className) {
                if (className == null) {
                    throw new NullPointerException();
                }

                this.className = className;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {className};
            }
        }
        
        final static class TypeClassNotVisible extends Error {            
                       
            /** name of the type class we are looking for. */
            private final TypeClass typeClass;
            
            /** module from which we are trying to resolve the type class. */
            private final ModuleName moduleName;

            TypeClassNotVisible (TypeClass typeClass, ModuleName moduleName) {
                if (typeClass == null || moduleName == null) {
                    throw new NullPointerException();
                }

                this.typeClass = typeClass;
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                //"The class {0} is not visible in module {1}."
                return new Object[] {typeClass.getName().getQualifiedName(), moduleName};
            }
        }
        
        public final static class CodeGenerationAborted extends Error {
            
            private final String codeLabel;
            
            public CodeGenerationAborted (String codeLabel) {
                if (codeLabel == null) {
                    throw new NullPointerException();
                }

                this.codeLabel = codeLabel;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {codeLabel};
            }
        }
        
        public final static class CodeGenerationAbortedWithException extends Error {
            
            private final ModuleName moduleName;
            private final String exceptionText;
            
            public CodeGenerationAbortedWithException (ModuleName moduleName, Throwable t) {
                if (moduleName == null || t == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
                this.exceptionText = t.toString();
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName, exceptionText};
            }
        }
        
        public final static class FailedToFinalizeJavaCode extends Error {
            
            private final ModuleName moduleName;
            
            public FailedToFinalizeJavaCode (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }                
        }
        
        /** this message is used when the an attempt is made to
         * call the dictionary function from a function that does
         * not have a single dictionary argument
         * 
         * @author mbyne
         */
        final static class RecordDictionaryMisused extends Error {
        }

        /** 
         * this message is used when the an attempt is made to
         * call the dictionary function
         * with invalid arguments.
         * 
         * @author mbyne
         */
        final static class RecordDictionaryArgumentsInvalid extends Error {
        }

        /**
         * This compiler message is used when an attempt is made
         * to create a record dictionary from a instance function that
         * does not exist
         * 
         * @author mbyne
         *
         */
        final static class DictionaryMethodDoesNotExist extends Error {
            private final String typeClass, method;

            DictionaryMethodDoesNotExist (String typeClass, String method) {
                if (typeClass == null || method == null) {
                    throw new NullPointerException();
                }

                this.typeClass = typeClass;
                this.method = method;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeClass, method};
            }
        }
       
        
        final static class AmbiguousTypeSignatureInInferredType extends Error {

            private final String typeName;

            AmbiguousTypeSignatureInInferredType (String typeName) {
                if (typeName == null) {
                    throw new NullPointerException();
                }

                this.typeName = typeName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeName};
            }
        }
        
        final static class UnableToPackageSuperCombinator extends Error {

            private final String superCombinatorStr;

            UnableToPackageSuperCombinator (String superCombinatorStr) {
                if (superCombinatorStr == null) {
                    throw new NullPointerException();
                }

                this.superCombinatorStr = superCombinatorStr;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {superCombinatorStr};
            }
        }
        
        public final static class UnableToCreateOutputValueNode extends Error {
            
            private final String type;
            
            public UnableToCreateOutputValueNode (String type) {
                if (type == null) {
                    throw new NullPointerException();
                }

                this.type = type;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {type};
            }
        }
        
        public final static class UnableToRetrieveAnOutputPolicy extends Error {
            
            private final String type;
            
            public UnableToRetrieveAnOutputPolicy (String type) {
                if (type == null) {
                    throw new NullPointerException();
                }

                this.type = type;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {type};
            }
        }
        
        public final static class UnableToResolveFunctionAlias extends Error {
            private final String containingFunctionName;
            private final String functionName;
            private final String aliasName;
            
            public UnableToResolveFunctionAlias (String containingFunctionName, String functionName, String aliasName) {
                this.containingFunctionName = containingFunctionName;
                this.functionName = functionName;
                this.aliasName = aliasName;
            }
            
            @Override
            Object[] getMessageArguments () {
                return new Object[]{this.containingFunctionName, this.functionName, this.aliasName};
            }
        }
        
        final static class RecordInstancesOverlap extends Error {

            private final String firstInstance;
            private final String otherInstance;
            private final ModuleName otherModule;

            RecordInstancesOverlap (String firstInstance, String otherInstance, ModuleName otherModule) {
                if (firstInstance == null || otherInstance == null || otherModule == null) {
                    throw new NullPointerException();
                }

                this.firstInstance = firstInstance;
                this.otherInstance = otherInstance;
                this.otherModule = otherModule;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {firstInstance, otherInstance, otherModule};
            }
        }
        
        final static class RepeatedFieldNameInRecordInstanceDeclaration extends Error {

            private final String fieldName;

            RepeatedFieldNameInRecordInstanceDeclaration (String fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {fieldName};
            }
        }
               
        final static class ErrorDeterminingTargetOutputType extends Error  {
            ErrorDeterminingTargetOutputType () {}
        }         
        
        final static class ErrorReadingAdjunctSource extends Error  {
            ErrorReadingAdjunctSource () {}
        }
        
        final static class OnlyOnePatternAllowedInCaseExpressionWithRecordPattern extends Error  {
            OnlyOnePatternAllowedInCaseExpressionWithRecordPattern () {}
        }
        
        /**
         * Used when a case expression containing a () unit pattern has more than one alternative.
         * @author Joseph Wong
         */
        final static class OnlyOnePatternAllowedInCaseExpressionWithUnitPattern extends Error  {
            OnlyOnePatternAllowedInCaseExpressionWithUnitPattern() {}
        }
        
        /**
         * Used when a case expression containing a tuple pattern has more than one alternative.
         * @author Joseph Wong
         */
        final static class OnlyOnePatternAllowedInCaseExpressionWithTuplePattern extends Error  {
            OnlyOnePatternAllowedInCaseExpressionWithTuplePattern() {}
        }
        
        final static class Illegal1TuplePattern extends Error  {
            Illegal1TuplePattern () {}
        }
        
        final static class WildcardPatternMustBeFinalInCaseExpression extends Error  {
            WildcardPatternMustBeFinalInCaseExpression () {}
        }
        
        final static class FirstArgumentOfNonStaticFieldMustBeInstance extends Error  {
            FirstArgumentOfNonStaticFieldMustBeInstance () {}
        }
        
        final static class FirstArgumentOfNonStaticMethodMustBeInstance extends Error  {
            FirstArgumentOfNonStaticMethodMustBeInstance () {}
        }
        
        final static class NonStaticFieldsMustHaveInstanceAndReturnType extends Error  {
            NonStaticFieldsMustHaveInstanceAndReturnType () {}
        }
        
        final static class NonStaticMethodsMustHaveInstanceAndReturnType extends Error  {
            NonStaticMethodsMustHaveInstanceAndReturnType () {}
        }
        
        final static class InvalidPrimitiveFunction extends Error  {
            private final String functionName;

            InvalidPrimitiveFunction (String functionName) {
                if (functionName == null) {
                    throw new NullPointerException();
                }

                this.functionName = functionName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {functionName};
            }
        }
        
        final static class KindErrorInTypeSignature extends Error  {
            KindErrorInTypeSignature () {}
        }              
        
        /**
         * Called when there is a type-clash using the record field update operator ":=".
         * This can happen for 2 reasons:
         * a) the base record does not contain the field being updated e.g. {{} | name := "Bob"}
         * b) the types of the field being updated is not compatible with the type of the field in the base record 
         *    e.g. {name = "Bob" | name := Just "Roger"}
         *    
         * @author Bo Ilic
         */
        final static class InvalidRecordFieldValueUpdate extends Error {  
            private final FieldName fieldName; 
                     
            InvalidRecordFieldValueUpdate (FieldName fieldName) {
                if (fieldName == null) {
                    throw new NullPointerException();
                }

                this.fieldName = fieldName;
            }

            @Override
            Object[] getMessageArguments() {
                //"Type error. Invalid field value update for field {0}." 
                return new Object[] {fieldName.getCalSourceForm()};
            }            
        }
        
        final static class InvalidRecordExtension extends Error  {
            InvalidRecordExtension () {}
        }
        
        final static class AllListElementsMustHaveCompatibleTypes extends Error  {
            AllListElementsMustHaveCompatibleTypes () {}
        }
        
        final static class UnexpectedTypeClash extends Error  {
            UnexpectedTypeClash () {}
        }
        
        final static class TypeErrorDuringApplication extends Error  {
            TypeErrorDuringApplication () {}
        }
        
        final static class TypeOfThenAndElsePartsMustMatch extends Error  {
            TypeOfThenAndElsePartsMustMatch () {}
        }
        
        final static class ConditionPartOfIfThenElseMustBeBoolean extends Error  {
            ConditionPartOfIfThenElseMustBeBoolean () {}
        }
        
        final static class TypesOfAllCaseBranchesMustBeCompatible extends Error  {
            TypesOfAllCaseBranchesMustBeCompatible () {}
        }
        
        final static class DataConstructorArgumentsDoNotMatchDataConstructor extends Error  {
            DataConstructorArgumentsDoNotMatchDataConstructor () {}
        }
        
        final static class CasePatternAndCaseConditionMustHaveSameType extends Error  {
            CasePatternAndCaseConditionMustHaveSameType () {}
        }
        
        /**
         * Called when a case alt unpack pattern using pattern group syntax uses an argument which cannot
         * be assigned a type.
         * 
         * For example,
         * data Foo = 
         *   Bar  arg :: Int |
         *   Baz  arg :: Double;
         * 
         * case foo of
         *     (Bar | Baz) arg -> arg;
         *     ;
         * 
         * gives:
         * Type error. There is no type compatible with argument arg for all data constructors in the group pattern.
         * 
         * @author Edward Lam
         */
        final static class DataConstructorPatternGroupArgumentNotTypeable extends Error {
            private final String argName;

            DataConstructorPatternGroupArgumentNotTypeable(String argName) {
                this.argName = argName;
            }
            
            @Override
            Object[] getMessageArguments() {
                // Type error. There is no type compatible with argument {0} for all data constructors in the group pattern.
                return new Object[] {argName};
            }
        }
        
        /**
         * Called when a data constructor field selection indicates a data constructor whose type is
         * not compatible with the expression indicated by the selection.
         * 
         * For example,
         * (1.0).Cons.head
         * 
         * gives:
         * Type Error. The expression does not match the data constructor type.
         */
        final static class ExpressionDoesNotMatchDataConstructorType extends Error  {
            // Type Error. The expression does not match the data constructor type.
            ExpressionDoesNotMatchDataConstructorType () {}
        }
        
        final static class SyntaxError extends Error  {
            SyntaxError () {}
        }
        
        /**
         * Called when the parser encounters a syntax error and is able to provide
         * additional information about the source element that was being parsed
         * when an error was encountered, and which token caused the error.
         * 
         * This code:
         *  
         *      public filter keepIfTrueFunction !list =
         *          Prelude.filter keepIfTrueFunction !list;
         * 
         * will produce this message:
         * 
         *      Syntax error. Encountered '!' while trying to parse a valid defining expression for a top-level function (starting from token 'Prelude'). Expected one of: [EOF, an identifier starting with a capital letter, an identifier starting with a lowercase letter, '=', ',', DOT, ';', '::', '(', ')', ... 29 more tokens]. 
         * 
         * @author James Wright
         */
        final static class SyntaxErrorWithParaphrase extends Error {
            
            /** The maximum number of tokens to report before truncating the message */
            private static final int MAX_REPORTED_TOKENS = Integer.MAX_VALUE;
            
            /** A string that describes what the parser was waiting for */  
            private final String paraphrase; 
            
            /** The token that caused the parser to signal an error */
            private final String errorToken;
            
            /** Array of tokens that would have been acceptable */
            private final String[] expectedTokens;
            
            /** The first token of the context where the error was encountered */ 
            private final String contextToken;

            /**
             * @param errorToken the token that caused the parser to signal an error 
             *                    ('!' in the example above).  This parameter must not be null.
             * @param expectedTokens Array of tokens that would have been acceptable.   This parameter must not be null.
             * @param contextToken the first token of the context described by paraphrase (This will generally
             *                      be an earlier token than errorToken)  ('Prelude' in the example above)
             *                      This parameter must not be null.
             * @param paraphrase A string describing the entity that the parser was attempting to match
             *                    when it encountered an error. ('a valid defining expression for a top-level function' in the example above)
             *                    This parameter must not be null.
             */
            SyntaxErrorWithParaphrase(String errorToken, String[] expectedTokens, String contextToken, String paraphrase) {
                if(errorToken == null || expectedTokens == null || contextToken == null || paraphrase == null) {
                    throw new NullPointerException();
                }
                
                this.errorToken = errorToken;
                this.expectedTokens = expectedTokens;
                this.contextToken = contextToken;
                this.paraphrase = paraphrase;
            }
            
            @Override
            Object[] getMessageArguments() {

                StringBuilder expectedTokensBuffer = new StringBuilder();
                for(int i = 0; i < expectedTokens.length && i < MAX_REPORTED_TOKENS; i++) {
                    if(i != 0) {
                        expectedTokensBuffer.append(", ");
                    }
                    expectedTokensBuffer.append(expectedTokens[i]);
                }
                
                if(expectedTokens.length > MAX_REPORTED_TOKENS) {
                    expectedTokensBuffer.append(", ");
                    expectedTokensBuffer.append(CALMessages.getString("MoreTokens", Integer.valueOf(expectedTokens.length - MAX_REPORTED_TOKENS)));
                }
                
                return new Object[] {this.paraphrase, this.errorToken, expectedTokensBuffer.toString(), this.contextToken};
            }
        }
        
        /**
         * A version of SyntaxErrorWithParaphrase that is useful for when we were only expecting
         * a single token.
         * 
         * @author James Wright
         */
        final static class SyntaxErrorWithParaphraseSingleExpected extends Error {
            
            /** A string that describes what the parser was waiting for */  
            private final String paraphrase; 
            
            /** The token that caused the parser to signal an error */
            private final String errorToken;
            
            /** The token that would have been acceptable */
            private final String expectedToken;
            
            /** The first token of the context where the error was encountered */ 
            private final String contextToken;

            /**
             * @param errorToken the token that caused the parser to signal an error 
             *                    ('!' in the example above).  This parameter must not be null.
             * @param expectedToken Token that would have been acceptable.  This parameter must not be null.
             * @param contextToken the first token of the context described by paraphrase (This will generally
             *                      be an earlier token than errorToken)  ('Prelude' in the example above)
             *                      This parameter must not be null.
             * @param paraphrase A string describing the entity that the parser was attempting to match
             *                    when it encountered an error. ('a valid defining expression for a top-level function' in the example above)
             *                    This parameter must not be null.
             */
            SyntaxErrorWithParaphraseSingleExpected(String errorToken, String expectedToken, String contextToken, String paraphrase) {
                if(errorToken == null || expectedToken == null || contextToken == null || paraphrase == null) {
                    throw new NullPointerException();
                }
                
                this.errorToken = errorToken;
                this.expectedToken = expectedToken;
                this.contextToken = contextToken;
                this.paraphrase = paraphrase;
            }
            
            @Override
            Object[] getMessageArguments() {

                return new Object[] {this.paraphrase, this.errorToken, expectedToken, this.contextToken};
            }
        }
        
        /**
         * Called when the parser encounters a syntax error and is able to provide
         * a suggestion about what might have been intended.
         * 
         * @author Jawright
         */
        final static class SyntaxErrorWithSuggestion extends Error {
            
            /** A string that describes the suggested new token */  
            private final String suggestion; 
            
            /** The unexpected token that caused the error */ 
            private final String unexpectedToken;
            
            /**
             * @param unexpectedToken String The unexpected token that caused the error
             * @param suggestion String Describes the suggested new token
             */
            SyntaxErrorWithSuggestion(String unexpectedToken, String suggestion) {
                this.unexpectedToken = unexpectedToken;
                this.suggestion = suggestion;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {this.unexpectedToken, this.suggestion};
            }
        }
        
        final static class BadTokenStream extends Error  {
            BadTokenStream () {}
        }
        
        final static class UnableToGenerateExpressionTree extends Error  {
            UnableToGenerateExpressionTree () {}
        }
        
        final static class BadASTTree extends Error  {
            BadASTTree () {}
        }
        
        /**
         * For example, for the deriving clause:
         * data data public Maybe a = public Nothing | public Just a deriving Eq, Ord, Prelude.Eq;
         * we should get this error because Prelude.Eq occurs twice.
         * 
         * @author Bo Ilic
         */
        final static class RepeatedTypeClassNameInDerivingClause extends Error {
           private final QualifiedName typeClassName;

            RepeatedTypeClassNameInDerivingClause (QualifiedName typeClassName) {
                if (typeClassName == null) {
                    throw new NullPointerException();
                }

                this.typeClassName = typeClassName;
            }

            @Override
            Object[] getMessageArguments() {
                //"Repeated type class name {0} in deriving clause."
                return new Object[] {typeClassName.getQualifiedName()};
            }            
        }
        
        /**
         * For example, for the deriving clause:
         * data public Maybe a = public Nothing | public Just a deriving Num;
         * we should get this error because Prelude.Num is not one of the classes for
         * which instances can be automatically derived.
         * 
         * @author Bo Ilic
         */
        final static class UnsupportedTypeClassNameInDerivingClause extends Error {
            private final QualifiedName typeClassName;
            
            UnsupportedTypeClassNameInDerivingClause (QualifiedName typeClassName) {
                if (typeClassName == null) {
                    throw new NullPointerException();
                }
                
                this.typeClassName = typeClassName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The type class {0} cannot be used in this deriving clause."
                return new Object[] {typeClassName.getQualifiedName()};
            }            
        }
        
        /**
         * For example, for the deriving clause:
         * data public Maybe a = public Nothing | public Just a deriving Enum;
         * we should get this error because Prelude.Enum requires the type being
         * declared to be an Enumeration type (i.e. all its data constructors take
         * no arguments).
         * 
         * @author Joseph Wong
         */
        final static class TypeClassInDerivingClauseRequiresEnumerationType extends Error {
            private final QualifiedName typeClassName;
            
            TypeClassInDerivingClauseRequiresEnumerationType(QualifiedName typeClassName) {
                if (typeClassName == null) {
                    throw new NullPointerException();
                }
                
                this.typeClassName = typeClassName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The type class {0} in the deriving clause requires all data constructors to have arity 0."
                return new Object[] {typeClassName.getQualifiedName()};
            }            
        }

        /**
         * For example, for the deriving clause:
         * data public Foobar a = public Foo | public Bar deriving Enum;
         * we should get this error because Prelude.Enum requires the type being
         * declared to be a non-polymorphic type.
         * 
         * @author Joseph Wong
         */
        final static class TypeClassInDerivingClauseRequiresNonPolymorphicType extends Error {
            private final QualifiedName typeClassName;
            
            TypeClassInDerivingClauseRequiresNonPolymorphicType(QualifiedName typeClassName) {
                if (typeClassName == null) {
                    throw new NullPointerException();
                }
                
                this.typeClassName = typeClassName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The type class {0} in the deriving clause requires the data type to be non-polymorphic."
                return new Object[] {typeClassName.getQualifiedName()};
            }            
        }
        
        /**
         * For example, for the deriving clause:
         *   data foreign unsafe import jvm private "javax.swing.JList"
         *     private SwingJList deriving Ord;
         * we should get this error because Prelude.Ord can only be derived for
         * foreign types if the foreign type implements Comparable or is a Java
         * primitive type.
         * 
         * Creation date: (Aug 2, 2005)
         * @author James Wright
         */
        final static class TypeClassInDerivingClauseRequiresPrimitiveOrImplementedInterface extends Error {
            private final QualifiedName typeClassName;
            private final Class<?> requiredAncestor;
            
            TypeClassInDerivingClauseRequiresPrimitiveOrImplementedInterface(QualifiedName typeClassName, Class<?> requiredAncestor) {
                if (typeClassName == null || requiredAncestor == null) {
                    throw new NullPointerException();
                }

                this.typeClassName = typeClassName;
                this.requiredAncestor = requiredAncestor;
            }
            
            @Override
            Object[] getMessageArguments() {
                // "The type class {0} in the deriving clause requires that the foreign type being imported either implement {1} or be a Java primitive" 
                return new Object[] {typeClassName.getQualifiedName(), requiredAncestor};
            }
        }
        
        /**
         * Called when there is a repeated declaration of a friend module e.g.
         * module Prelude;
         * friend List;
         * friend String;
         * friend List;
         *          
         * @author Bo Ilic
         */
        final static class RepeatedFriendModuleDeclaration extends Error {
            private final ModuleName moduleName;
            
            RepeatedFriendModuleDeclaration (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }
                
                this.moduleName = moduleName;
            }
            
            @Override
            Object[] getMessageArguments() {
                 //"Repeated friend declaration for module {0}."
                return new Object[] {moduleName};
            }            
        }
        
        /**
         * Called when a module declares itself as a friend e.g.
         * module Foo;
         * import Prelude;
         * friend Foo;
         * 
         * @author Bo Ilic
         */
        final static class ModuleCannotBeFriendOfItself extends Error {
            private final ModuleName moduleName;
            
            ModuleCannotBeFriendOfItself (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }
                
                this.moduleName = moduleName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"This friend declaration occurs in module {0}. A module cannot be a friend of itself." 
                return new Object[] {moduleName};
            }                                   
        }
        
        /**
         * Called when a module declares a friend that is one of its imports e.g.
         * module Foo;
         * import Prelude;
         * import Bar;
         * friend Bar;
         * 
         * Note: also called for indirect imports i.e. a module in the import chain of the given module.
         * 
         * @author Bo Ilic
         */
        final static class ModuleCannotBeFriendOfImport extends Error {
            private final ModuleName moduleName;
            private final ModuleName importedModuleName;
            
            ModuleCannotBeFriendOfImport (ModuleName moduleName, ModuleName importedModuleName) {
                if (moduleName == null || importedModuleName == null) {
                    throw new NullPointerException();
                }
                
                this.moduleName = moduleName;
                this.importedModuleName = importedModuleName;
            }
            
            @Override
            Object[] getMessageArguments() {
                 //"The module {0} directly or indirectly imports module {1}. A module cannot have direct or indirect imports as friends."
                return new Object[] {moduleName, importedModuleName};
            }                                   
        }        
        
        /**
         * Used when a CALDoc comment contains more "@arg" tags than there are arguments for the function/data constructor.
         * 
         * For example,
         * 
         * data Foo = 
         *     /**
         *      * @arg foo   the foo
         *      * @arg bar   the bar
         *      * @arg baz   the baz
         *      * /
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * There are too many @arg tags in this CALDoc comment.
         *
         * @author Joseph Wong
         */
        final static class TooManyArgTagsInCALDocComment extends Error {
            
            TooManyArgTagsInCALDocComment() {}
            
            @Override
            Object[] getMessageArguments() {
                //"There are too many @arg tags in this CALDoc comment."
                return new Object[] {};
            }            
        }
        
        /**
         * Used when a CALDoc "@arg" tag contains an argument name that is not valid.
         * 
         * For example,
         * 
         *     /**
         *      * @arg foo   the foo
         *      * @arg #2    the bar
         *      * /
         *     private baz foo bar = 3.0;  
         * 
         * gives:
         * The name #2 appearing in this @arg tag is not a valid argument name.
         *
         * @author Joseph Wong
         */
        final static class InvalidArgNameInCALDocComment extends Error {
            private final String argName;
            
            InvalidArgNameInCALDocComment(String argName) {
                this.argName = argName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The name {0} appearing in this @arg tag is not a valid argument name."
                return new Object[] {argName};
            }            
        }
        
        /**
         * Used when a CALDoc "@arg" tag contains an argument name that does not match the declared name.
         * 
         * For example,
         * 
         * data Foo = 
         *     /**
         *      * @arg foo   the foo
         *      * @arg qux   the qux
         *      * /
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The name qux appearing in this @arg tag does not match the declared name bar.
         *
         * @author Joseph Wong
         */
        final static class ArgNameDoesNotMatchDeclaredNameInCALDocComment extends Error {
            private final String argName;
            private final String declaredName;
            
            ArgNameDoesNotMatchDeclaredNameInCALDocComment(String argName, String declaredName) {
                this.argName = argName;
                this.declaredName = declaredName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The name {0} appearing in this @arg tag does not match the declared name {1}."
                return new Object[] {argName, declaredName};
            }            
        }
        
        /**
         * Used when a CALDoc comment contains multiple instances of a tag that can only appear once per CALDoc comment.
         * 
         * For example,
         * 
         * /**
         *  * @version 2
         *  * @version 5
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The @version tag cannot appear more than once in a CALDoc comment.
         *
         * @author Joseph Wong
         */
        final static class SingletonTagAppearsMoreThanOnceInCALDocComment extends Error {
            private final String tagName;
            
            SingletonTagAppearsMoreThanOnceInCALDocComment(String tagName) {
                this.tagName = tagName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The {0} tag cannot appear more than once in a CALDoc comment."
                return new Object[] {tagName};
            }            
        }
        
        /**
         * Used when a CALDoc comment contains a tag that cannot be used with the comment, e.g. an "@arg" tag in
         * a type constructor comment.
         * 
         * For example,
         * 
         * /**
         *  * @arg something
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The @arg tag cannot be used in this CALDoc comment.
         *
         * @author Joseph Wong
         */
        final static class DisallowedTagInCALDocComment extends Error {
            private final String tagName;
            
            DisallowedTagInCALDocComment(String tagName) {
                this.tagName = tagName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The {0} tag cannot be used in this CALDoc comment."
                return new Object[] {tagName};
            }            
        }
        
        /**
         * Used when a CALDoc comment contains an unrecognized tag.
         * 
         * For example,
         * 
         * /**
         *  * at-lalala something
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The tag at-lalala is not a recognized CALDoc tag.
         *
         * @author Joseph Wong
         */
        final static class UnrecognizedTagInCALDocComment extends Error {
            private final String tagName;
            
            UnrecognizedTagInCALDocComment(String tagName) {
                this.tagName = tagName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The tag {0} is not a recognized CALDoc tag."
                return new Object[] {tagName};
            }            
        }
        
        /**
         * Used when a CALDoc comment contains an unrecognized inline tag.
         * 
         * For example,
         * 
         * /**
         *  * {at-lalala something}
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The tag {at-lalala} is not a recognized CALDoc inline tag.
         *
         * @author Joseph Wong
         */
        final static class UnrecognizedInlineTagInCALDocComment extends Error {
            private final String tagName;
            
            UnrecognizedInlineTagInCALDocComment(String tagName) {
                this.tagName = tagName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The tag {0} is not a recognized CALDoc inline tag."
                return new Object[] {tagName};
            }            
        }
        
        /**
         * Used when a CALDoc comment "@see" or "@link" block is missing the context declaration.
         * 
         * For example,
         * 
         * /**
         *  * (at)see = Prelude.id
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * This CALDoc @see/@link block is missing a context declaration before the '='.
         *
         * @author Joseph Wong
         */
        final static class MissingSeeOrLinkBlockContextInCALDocComment extends Error {
            MissingSeeOrLinkBlockContextInCALDocComment() {}
        }
        
        /**
         * Used when a CALDoc comment "@see" or "@link" block contains an unrecognized context.
         * 
         * For example,
         * 
         * /**
         *  * (at)see foobar = cool
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * foobar is not a recognized context for a CALDoc @see/@link block.
         *
         * @author Joseph Wong
         */
        final static class UnrecognizedSeeOrLinkBlockContextInCALDocComment extends Error {
            private final String contextName;
            
            UnrecognizedSeeOrLinkBlockContextInCALDocComment(String contextName) {
                this.contextName = contextName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"{0} is not a recognized context for a CALDoc @see/@link block."
                return new Object[] {contextName};
            }            
        }
        
        /**
         * Used when a CALDoc comment "@see" or "@link" block contains an unrecognized reference.
         * 
         * For example,
         * 
         * /**
         *  * (at)see module = !Prelude
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * !Prelude is an invalid reference in this CALDoc @see/@link block.
         *
         * @author Joseph Wong
         */
        final static class UnrecognizedSeeOrLinkBlockReferenceInCALDocComment extends Error {
            private final String refName;
            
            UnrecognizedSeeOrLinkBlockReferenceInCALDocComment(String refName) {
                this.refName = refName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"{0} is an invalid reference in this CALDoc @see/@link block."
                return new Object[] {refName};
            }            
        }
        
        /**
         * Used when a CALDoc cross reference in a "@see" or "@link" block is resolvable to more
         * than one entity (each of a different kind).
         * 
         * For example,
         * 
         * /**
         *  * {at-link File.FileName at-}
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The reference File.FileName can be resolved to more than one entity. Use the full form with the context keyword to disambiguate, as one of: {at-link typeConstructor = File.FileName at-}, {at-link dataConstructor = File.FileName at-}.
         *
         * @author Joseph Wong
         */
        final static class CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment extends Error {
            private final String refName;
            private final String[] alternatives;
            
            CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment(String refName, String[] alternatives) {
                this.refName = refName;
                this.alternatives = alternatives.clone();
            }
            
            @Override
            Object[] getMessageArguments() {
                StringBuilder alternativesBuffer = new StringBuilder();
                for (int i = 0; i < alternatives.length; i++) {
                    if (i > 0) {
                        alternativesBuffer.append(", ");
                    }
                    alternativesBuffer.append(alternatives[i]);
                }
                
                //"The reference {0} can be resolved to more than one entity. Use the full form with the context keyword to disambiguate, as one of: {1}."
                return new Object[] {refName, alternativesBuffer.toString()};
            }
        }
        
        /**
         * Used when a CALDoc unchecked cross reference in a "@see" or "@link" appears without a context keyword,
         * making it ambiguous.
         * 
         * For example,
         * 
         * /**
         *  * {at-link "Prelude" at-}
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The unchecked reference Prelude is ambiguous. Use the full form with the context keyword (one of 'module', 'typeClass', 'typeConstructor', 'dataConstructor') to disambiguate.
         *
         * @author Joseph Wong
         */
        final static class UncheckedCrossReferenceIsAmbiguousInCALDocComment extends Error {
            private final String refName;
            
            UncheckedCrossReferenceIsAmbiguousInCALDocComment(String refName) {
                this.refName = refName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The unchecked reference {0} is ambiguous. Use the full form with the context keyword (one of ''module'', ''typeClass'', ''typeConstructor'', ''dataConstructor'') to disambiguate."
                return new Object[] {refName};
            }            
        }
        
        /**
         * Used when a CALDoc checked cross reference in a "@see" or "@link" block does not resolve to any
         * known entity.
         * 
         * For example,
         * 
         * /**
         *  * {at-link LaLaLa at-}
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The CALDoc checked reference LaLaLa cannot be resolved.
         *
         * @author Joseph Wong
         */
        final static class CheckedCrossReferenceCannotBeResolvedInCALDocComment extends Error {
            private final String refName;
            
            CheckedCrossReferenceCannotBeResolvedInCALDocComment(String refName) {
                this.refName = refName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The CALDoc checked reference {0} cannot be resolved."
                return new Object[] {refName};
            }            
        }
        
        /**
         * Used when a CALDoc checked cross reference in a "@see" or "@link" block does not resolve to any
         * known entity, but there is a single suggestion that the compiler is able to produce.
         * 
         * For example,
         * 
         * /**
         *  * {at-link False at-}
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The CALDoc checked reference False cannot be resolved. Was 'Prelude.False' intended?
         *
         * @author Joseph Wong
         */
        final static class CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion extends Error {
            private final String refName;
            private final QualifiedName candidate;
            
            CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion(String refName, QualifiedName candidate) {
                this.refName = refName;
                this.candidate = candidate;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The CALDoc checked reference {0} cannot be resolved. Was ''{1}'' intended?"
                return new Object[] {refName, candidate.getQualifiedName()};
            }            
        }
        
        /**
         * Used when a CALDoc checked cross reference in a "@see" or "@link" block does not resolve to any
         * known entity, but there are multiple suggestions that the compiler is able to produce.
         * 
         * For example,
         * 
         * /**
         *  * {at-link LaLaLa at-}
         *  * /
         * data Foo = 
         *     private Foo 
         *         foo::Int
         *         bar::Double
         *     ;
         * 
         * gives:
         * The CALDoc checked reference LaLaLa cannot be resolved. Was one of these intended: ModuleA.LaLaLa, ModuleB.LaLaLa?
         *
         * @author Joseph Wong
         */
        final static class CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithMultipleSuggestions extends Error {
            private final String refName;
            private final QualifiedName[] candidates;
            
            CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithMultipleSuggestions(String refName, QualifiedName[] candidates) {
                this.refName = refName;
                this.candidates = candidates.clone();
            }
            
            @Override
            Object[] getMessageArguments() {
                StringBuilder alternativesBuffer = new StringBuilder();
                for (int i = 0; i < candidates.length; i++) {
                    if (i > 0) {
                        alternativesBuffer.append(", ");
                    }
                    alternativesBuffer.append(candidates[i].getQualifiedName());
                }
                
                //"The CALDoc checked reference {0} cannot be resolved. Was one of these intended: {1}?
                return new Object[] {refName, alternativesBuffer.toString()};
            }            
        }
        
        /**
         * Used when a CALDoc comment is not associated with any declaration.
         * 
         * For example,
         * 
         * /**
         *  * @arg dummy    a dummy variable
         *  * /
         * /*** /
         * foo :: Prelude.Int -> Prelude.Double;
         * public foo dummy = 7.0;
         * 
         * gives:
         * This CALDoc comment is not associated with any declaration.
         *
         * @author Joseph Wong
         */
        final static class UnassociatedCALDocComment extends Error {
            UnassociatedCALDocComment() {}
        }
        
        /**
         * Used when a CALDoc comment cannot appear at a particular location.
         * 
         * For example,
         * 
         * public foo /** bad caldoc * / dummy = 7.0;
         * 
         * gives:
         * A CALDoc comment cannot appear here.
         *
         * @author Joseph Wong
         */
        final static class CALDocCommentCannotAppearHere extends Error {
            CALDocCommentCannotAppearHere() {}
        }
        
        /**
         * Used when a CALDoc comment is associated with a local pattern match declaration, which is not allowed.
         * 
         * For example,
         * 
         * let
         * /** bad caldoc * /
         * (a, a_2) = (x, y);
         * in
         * (a, a_2, x)
         * 
         * gives:
         * A local pattern match declaration cannot have a CALDoc comment. However, type declarations for the pattern-bound variables can have associated CALDoc comments. 
         *
         * @author Joseph Wong
         */
        final static class LocalPatternMatchDeclCannotHaveCALDocComment extends Error {
            LocalPatternMatchDeclCannotHaveCALDocComment() {}
        }
        
        /**
         * Used when a CALDoc comment for an algebraic function with an associated type delcaration appears immediately before
         * the function definition rather than immediately before the type declaration.
         * 
         * For example,
         * 
         * foo :: Prelude.Int -> Prelude.Double;
         * /**
         *  * @arg dummy    a dummy variable
         *  * /
         * public foo dummy = 7.0;
         * 
         * gives:
         * The CALDoc comment for the foo function must appear immediately before its associated type declaration.
         *
         * @author Joseph Wong
         */
        final static class CALDocCommentForAlgebraicFunctionMustAppearBeforeTypeDeclaration extends Error {
            private final String functionName;
            
            CALDocCommentForAlgebraicFunctionMustAppearBeforeTypeDeclaration(String functionName) {
                this.functionName = functionName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The CALDoc comment for the {0} function must appear immediately before its associated type declaration."
                return new Object[] {functionName};
            }            
        }
        
        /**
         * Used when a paragraph break appears within a CALDoc comment in a situation where it is forbidden.
         * 
         * For example,
         * 
         * /**
         *  * {at-em paragraph 1
         *  *
         *  * paragraph 2
         *  *
         *  * pargraph 3 at-}
         *  * /
         * 
         * gives (replacing 'at-' with '@'):
         * A paragraph break cannot appear here inside the {at-em} inline tag.
         *
         * @author Joseph Wong
         */
        final static class ParagraphBreakCannotAppearHereInsideCALDocCommentInlineTag extends Error {
            private final String tagName;
            
            ParagraphBreakCannotAppearHereInsideCALDocCommentInlineTag(String tagName) {
                this.tagName = tagName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"A paragraph break cannot appear here inside the {0} inline tag."
                return new Object[] {tagName};
            }            
        }
        
        /**
         * Used when an inline tag appears within a CALDoc comment in a situation where it is forbidden.
         * 
         * For example,
         * 
         * /**
         *  * {at-url {at-em http://localhost at-} at-}
         *  * /
         *
         * gives (replacing 'at-' with '@'):
         * An inline tag cannot appear here, within the context of the {at-url} inline tag. 
         *
         * @author Joseph Wong
         */
        final static class InlineTagCannotAppearHereInsideCALDocCommentInlineTag extends Error {
            private final String tagName;
            
            InlineTagCannotAppearHereInsideCALDocCommentInlineTag(String tagName) {
                this.tagName = tagName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"An inline tag cannot appear here, within the context of the {0} inline tag."
                return new Object[] {tagName};
            }            
        }
        
        /**
         * Used when a particular inline tag appears within a CALDoc comment in a situation where it is forbidden.
         * 
         * For example,
         * 
         * /**
         *  * {at-summary {at-orderedList {at-item foo at-} at-} at-}
         *  * /
         *
         * gives (replacing 'at-' with '@'):
         * The inline tag {at-orderedList} cannot appear here, within the context of the {at-summary} inline tag. 
         *
         * @author Joseph Wong
         */
        final static class ThisParticularInlineTagCannotAppearHereInsideCALDocCommentInlineTag extends Error {
            private final String badTagName;
            private final String contextTagName;
            
            ThisParticularInlineTagCannotAppearHereInsideCALDocCommentInlineTag(String badTagName, String contextTagName) {
                this.badTagName = badTagName;
                this.contextTagName = contextTagName;
            }
            
            @Override
            Object[] getMessageArguments() {
                //"The inline tag {0} cannot appear here, within the context of the {1} inline tag."
                return new Object[] {badTagName, contextTagName};
            }            
        }
        
        /**
         * Used when a CALDoc inline tag appears without a corresponding closing tag.
         * 
         * For example,
         * 
         *  /**
         *   * {at-em foo
         *   * /
         *
         * gives (replacing 'at-' with '@'):
         * This inline tag is missing a corresponding closing tag '@}'.
         *
         * @author Joseph Wong
         */
        final static class InlineTagBlockMissingClosingTagInCALDocComment extends Error {
            InlineTagBlockMissingClosingTagInCALDocComment() {}
        }
        
        /**
         * Used during deserialization of compiled module info when the deserialized record is
         * incompatible with the current schema.
         * 
         * This can happen if using the current version of CAL to deserialize a module compiled with a later (future) version.
         * 
         * @author Edward Lam
         */
        final static class DeserializedIncompatibleSchema extends Error {
            
            /** the schema of the saved record. */
            private final int savedSchema;
            
            /** the currently-known schema number. */
            private final int currentSchema;

            /** The internal name of the record.*/
            private final String internalName;
            
            DeserializedIncompatibleSchema(int savedSchema, int currentSchema, String internalName) {
                this.savedSchema = savedSchema;
                this.currentSchema = currentSchema;
                this.internalName = internalName;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            Object[] getMessageArguments() {
                //"Stored schema {0} is later than current schema {1}.  Internal name: {2}."
                return new Object[] {Integer.valueOf(savedSchema), Integer.valueOf(currentSchema), internalName};
            }
            
        }
    }    
    
    /**
     * Fatal errors should result in compilation immediately stopping.
     * For example, this level is used for programming errors in the compiler.
     */
    public abstract static class Fatal extends MessageKind {
        
        private Fatal() {}
        
        @Override
        public final CompilerMessage.Severity getSeverity() {
            return CompilerMessage.Severity.FATAL;
        }
                
        final static class DebugMessage extends Fatal {
            
            private final String debugMessage;
            
            DebugMessage (String message) {
                if (message == null) {
                    throw new NullPointerException();
                }

                debugMessage = message;
            }
            
            @Override
            public String getMessage() {
                return debugMessage;
            }
        }
        
        final static class ErrorWhilePackagingModule extends Fatal {

            private final ModuleName moduleName;

            ErrorWhilePackagingModule (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        final static class ModuleNotInWorkspace extends Fatal {

            private final ModuleName moduleName;

            ModuleNotInWorkspace (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
               
        final static class CouldNotCreateModuleInPackage extends Fatal {

            private final ModuleName moduleName;

            CouldNotCreateModuleInPackage (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
                      
        final static class CyclicClassContextDependenciesBetweenClasses extends Fatal {

            private final String classList;

            CyclicClassContextDependenciesBetweenClasses (String classList) {
                if (classList == null) {
                    throw new NullPointerException();
                }

                this.classList = classList;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {classList};
            }
        }
        
        final static class TypeClassCannotIncludeItselfInClassConstraintList extends Fatal {

            private final String typeClass;

            TypeClassCannotIncludeItselfInClassConstraintList (String typeClass) {
                if (typeClass == null) {
                    throw new NullPointerException();
                }

                this.typeClass = typeClass;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {typeClass};
            }
        }
        
        public final static class UnableToRecoverFromCodeGenErrors extends Fatal {
            
            private final ModuleName moduleName;
            
            public UnableToRecoverFromCodeGenErrors (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }
                
                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        public final static class CodeGenerationAbortedDueToInternalCodingError extends Fatal {
            
            private final ModuleName moduleName;
            
            public CodeGenerationAbortedDueToInternalCodingError (ModuleName moduleName) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }

                this.moduleName = moduleName;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        /**
         * Called when we are loading a compiled module and we catch an exception where a compiler message has not
         * been previously logged i.e. there is an unexpected failure in loading. This is an internal error.         
         */
        final static class CompilationAbortedDueToInternalModuleLoadingError extends Fatal {
            private final ModuleName moduleName;
            private final String detail;
            
            CompilationAbortedDueToInternalModuleLoadingError (ModuleName moduleName, String detail) {
                if (moduleName == null) {
                    throw new NullPointerException();
                }
                this.moduleName = moduleName;
                this.detail = detail;
            }
            
            @Override
            Object[] getMessageArguments () {
                //"Compilation aborted due to an internal error in loading the compiled module {0}. Please contact Business Objects. Detail: {1}"
                return new Object[] {moduleName, detail};
            }
        }
        
        final static class TreeParsingError extends Fatal  {
            TreeParsingError () {}
        }
        
        public final static class CouldNotFindWorkspaceDefinition extends Fatal  {
            public CouldNotFindWorkspaceDefinition () {}
        }
        
        final static class UnexpectedUnificationFailure extends Fatal  {
            UnexpectedUnificationFailure () {}
        }

        final static class InternalCodingError extends Fatal  {
            InternalCodingError () {}
        }
        
        final static class MoreThanOneDefinitionInANonRecursiveLet extends Fatal  {
            MoreThanOneDefinitionInANonRecursiveLet () {}
        }
        
        final static class UnliftedLambdaExpression extends Fatal  {
            UnliftedLambdaExpression () {}
        }
                       
    }
    
    /**
     * A problem that does not prevent compilation or running of the resulting program,
     * but nevertheless may be of interest to the user.
     */
    public abstract static class Warning extends MessageKind {
        
        private Warning() {}
        
        @Override
        public final CompilerMessage.Severity getSeverity() {
            return CompilerMessage.Severity.WARNING;
        }
        
        /**
         * Used when the code contains a reference to a deprecated module.
         *
         * @author Joseph Wong
         */
        final static class DeprecatedModule extends Warning {
            
            private final ModuleName moduleName;
            
            DeprecatedModule(ModuleName moduleName) {
                this.moduleName = moduleName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {moduleName};
            }
        }
        
        /**
         * Used when the code contains a reference to a deprecated type.
         *
         * @author Joseph Wong
         */
        final static class DeprecatedType extends Warning {
            
            private final QualifiedName qualifiedName;
            
            DeprecatedType(QualifiedName qualifiedName) {
                this.qualifiedName = qualifiedName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {qualifiedName};
            }
        }
        
        /**
         * Used when the code contains a reference to a deprecated data constructor.
         *
         * @author Joseph Wong
         */
        final static class DeprecatedDataCons extends Warning {
            
            private final QualifiedName qualifiedName;
            
            DeprecatedDataCons(QualifiedName qualifiedName) {
                this.qualifiedName = qualifiedName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {qualifiedName};
            }
        }
        
        /**
         * Used when the code contains a reference to a deprecated type class.
         *
         * @author Joseph Wong
         */
        final static class DeprecatedTypeClass extends Warning {
            
            private final QualifiedName qualifiedName;
            
            DeprecatedTypeClass(QualifiedName qualifiedName) {
                this.qualifiedName = qualifiedName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {qualifiedName};
            }
        }
        
        /**
         * Used when the code contains a reference to a deprecated class method.
         *
         * @author Joseph Wong
         */
        final static class DeprecatedClassMethod extends Warning {
            
            private final QualifiedName qualifiedName;
            
            DeprecatedClassMethod(QualifiedName qualifiedName) {
                this.qualifiedName = qualifiedName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {qualifiedName};
            }
        }
        
        /**
         * Used when the code contains a reference to a deprecated function.
         *
         * @author Joseph Wong
         */
        final static class DeprecatedFunction extends Warning {
            
            private final QualifiedName qualifiedName;
            
            DeprecatedFunction(QualifiedName qualifiedName) {
                this.qualifiedName = qualifiedName;
            }
            
            @Override
            Object[] getMessageArguments() {
                return new Object[] {qualifiedName};
            }
        }
        
        public final static class DebugMessage extends Warning {
            
            private final String debugMessage;
            
            public DebugMessage (String message) {
                debugMessage = message;
            }
            
            @Override
            public String getMessage() {
                return debugMessage;
            }
        }        
    }
    
    /** Purely informational, and not a problem of any sort. */ 
    public abstract static class Info extends MessageKind {
        
        private Info() {}
        
        @Override
        public final CompilerMessage.Severity getSeverity() {
            return CompilerMessage.Severity.INFO;
        }
        
        public final static class DebugMessage extends Info {
            
            private final String debugMessage;
            
            public DebugMessage (String message) {
                debugMessage = message;
            }
            
            @Override
            public String getMessage() {
                return debugMessage;
            }
        }
        
        final static class UnableToRecover extends Info {
            UnableToRecover () {}
        }
        
        final static class TooManyErrors extends Info {

            private final int numberOfErrors;

            TooManyErrors (int numberOfErrors) {
                this.numberOfErrors = numberOfErrors;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {Integer.valueOf(numberOfErrors)};
            }
        }
        
        final static class LocalOverloadedPolymorphicConstant extends Info {

            private final String constantName;
            private final String functionName;
            private final String functionType;

            LocalOverloadedPolymorphicConstant (String constantName, String functionName, String functionType) {
                if (constantName == null || functionName == null || functionType == null) {
                    throw new NullPointerException();
                }

                this.constantName = constantName;
                this.functionName = functionName;
                this.functionType = functionType;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {constantName, functionName, functionType};
            }
        }
        
        final static class SupercombinatorInfoDumpCAF extends Info {

            private final String superCombinator;
            private final String formalParameters;

            SupercombinatorInfoDumpCAF (String superCombinator, String formalParameters) {
                if (superCombinator == null || formalParameters == null) {
                    throw new NullPointerException();
                }

                this.superCombinator = superCombinator;
                this.formalParameters = formalParameters;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {superCombinator, formalParameters};
            }
        }
        
        final static class SupercombinatorInfoDumpNonCAF extends Info {

            private final String superCombinator;
            private final String formalParameters;

            SupercombinatorInfoDumpNonCAF (String superCombinator, String formalParameters) {
                if (superCombinator == null || formalParameters == null) {
                    throw new NullPointerException();
                }

                this.superCombinator = superCombinator;
                this.formalParameters = formalParameters;
            }

            @Override
            Object[] getMessageArguments() {
                return new Object[] {superCombinator, formalParameters};
            }
        }              
    }
    
    Object[] getMessageArguments() {
        return new Object[0];
    }
    
    public abstract CompilerMessage.Severity getSeverity();
    
    /**
     * Given a class, return its unqualified name.
     * @param klass The class to get the name of
     * @return The unqualified name of the given class.
     */
    static String getClassName(Class<?> klass) {
        String className = klass.getName();
        int lastDollarSignPosition = className.lastIndexOf("$")+1;
        int lastDotPosition = className.lastIndexOf(".")+1;
        if(lastDollarSignPosition > 0) {
            return className.substring(lastDollarSignPosition);
        } else {
            // If the string does not have a dot, this will return the
            // entire string, since lastDotPosition will be 0
            return className.substring(lastDotPosition);
        }
    }
    
    /**
     * @return the unqualified name of the MessageKind instance's class. 
     */
    private String getMessageKindName() {
        return getClassName(this.getClass());
    }    
    
    /**      
     * @return The localized message associated with the message kind.
     */
    public String getMessage() {
        return CALMessages.getString(getMessageKindName(), getMessageArguments());    
    }

}
