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

package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.LanguageInfo;

/**
 * Provides helper methods for creating valid CAL identifiers, and for describing the problems in proposed CAL
 * identifiers. This class is similar to LanguageInfo (and in fact was split off from it).
 *
 * @author Ulian Radu
 */
public final class IdentifierUtils {
    /**
     * Class handling validation of CAL identifiers.
     * 
     * @author Iulian Radu
     */
    private static class IdentifierValidator {

        /** Identifier which we are validating */
        private final String identifier;

        /** Position we are currently validating within the identifier */
        private int identifierIndex;

        /** Buffer building up a valid suggestion */
        private final StringBuilder suggestionBuffer;

        /** 
         * Result of this validation. While validation is in progress,
         * this structure gathers validation errors.
         */ 
        private final ValidatedIdentifier validationResult;

        /** 
         * Flag indicating case restrictions on the identifier.
         * (if true, identifier needs to start with an upper case character.)
         */
        private final boolean isConstructor;

        /**
         * Constructs object and initializes fields.
         * 
         * @param identifier string to convert from
         * @param isConstructor true if identifier should be a constructor; false otherwise
         */
        private IdentifierValidator(String identifier, boolean isConstructor) {
            validationResult = new ValidatedIdentifier();
            this.isConstructor = isConstructor;
            this.identifier = identifier;
            identifierIndex = 0;
            suggestionBuffer = new StringBuilder(identifier.length());
        }

        /**
         * Make a valid CAL identifier name, and indicate the corrections made.  This method 
         * removes all characters which are invalid and ensures camel casing on the resulting
         * identifier name. If the given string only contains invalid characters, then
         * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
         * then this method appends a number pad at the end of the identifier.
         * 
         * If isConstructor is true, the argument needs to have a capital letter at its
         * beginning in order to be valid (use this for Constructors or Data Type names).
         * 
         * @return a structure for the validated identifier name (containing validation
         * errors and its valid suggestion)
         */
        private ValidatedIdentifier makeIdentifierName() 
        {
            // Exit if identifier is null
            if (identifier == null || identifier.length() == 0) {
                validationResult.addError(ValidationStatus.WAS_EMPTY);
                return validationResult;
            }

            // Validate beginning and content of identifier

            validateIdentifierBegining();
            if (suggestionBuffer.length() == 0) {
                // All the characters are invalid
                return validationResult;
            } 
            validateIdentifierContent();

            // Now ensure identifier is not a keyword

            String newIdentifier = suggestionBuffer.toString();
            if (LanguageInfo.isKeyword(newIdentifier)) {

                validationResult.addError(ValidationStatus.EXISTING_KEYWORD);
                int i = 0;
                while (LanguageInfo.isKeyword(newIdentifier + i)) {
                    i++;
                } 
                newIdentifier = newIdentifier + i;
            }

            // Return result and suggestion (if necessary)

            if (validationResult.getNErrors() > 0) {
                validationResult.setSuggestion(newIdentifier);
            }
            return validationResult;
        }

        /**
         * Validates the beginning of an identifier by stripping any illegal characters from
         * the string. Errors are added into validationResult, and the 
         * suggestion buffer will contain the first validated character. Identifier index
         * will point to the character after the first valid character.
         * 
         * NOTE: This method should only be called via makeIdentifierName(), as it
         * modifies the existing validation result.
         */
        private void validateIdentifierBegining() {
            // keep consuming the input string until we can find the first character
            // that is a valid starting character for a CAL lexer token 
            while (identifierIndex < identifier.length()) {
                // Try to correct the first letter

                char test = identifier.charAt(identifierIndex);
                ValidationStatus e = (isConstructor ? checkCALConsStart(test) : checkCALVarStart(test));
                char c = makeValidCharacter(test,e);

                // Should we use this correction ?
                if (e != ValidationStatus.INVALID_CONTENT) {

                    // Character has been corrected and can be added
                    suggestionBuffer.append(c);
                    if (e.isError()) {
                        validationResult.addError(e);
                    }
                    break;

                } else {
                    // Letter is not and could not be corrected; log error and discard it
                    if (identifierIndex == 0) {
                        validationResult.addError(ValidationStatus.INVALID_START);
                    }
                    identifierIndex++;
                }
            }
            identifierIndex++;
        }

        /**
         * Validates the content of an identifier by stripping any illegal 
         * characters from the string. Errors are added into validationResult, 
         * and the result string builder will contain a validated identifier.
         * 
         * NOTE: This method should only be called via makeIdentifierName(), as it
         * modifies the existing validation result. 
         */
        private void validateIdentifierContent() {

            // Will only store invalid content error once
            boolean indicatedBadContent = false;

            // Flag indicates that the preceding letter was invalid, so the next valid letter must be upper cased.
            // Never true if inCapsStart is true.
            boolean camelCase = false;

            // If the identifier starts with a string of upper-case letters, these are all lower-cased, 
            //  except for the last letter if it precedes a lower-case letter.
            // This flag is true until a non-upper case letter is encountered.
            // eg. FOOBar -> fooBar
            boolean inCapsStart = LanguageInfo.isCALConsStart(identifier.charAt(identifierIndex - 1));  // never true if camelCase is true

            for (int size = identifier.length(); identifierIndex < size; identifierIndex++) {
                char test = identifier.charAt(identifierIndex);
                ValidationStatus e = (isConstructor ? checkCALConsPart(test) : checkCALVarPart(test));
                char c = makeValidCharacter(test,e);

                if (e.isError() && !indicatedBadContent) {
                    validationResult.addError(e);
                    indicatedBadContent = true;
                }

                if (e == ValidationStatus.INVALID_CONTENT) {
                    // Strip invalid content and replace next valid letter by upper case
                    camelCase = true;
                    inCapsStart = false;

                    // If we encountered a space char, suggest underscore as a replacement instead of just stripping the character.
                    if (Character.isSpaceChar(test)) {
                        suggestionBuffer.append('_');
                    }

                } else {
                    if (camelCase) {
                        // The previous letter was invalid.
                        c = Character.toUpperCase(c);
                        camelCase = false;

                    } else if (inCapsStart) {
                        if (LanguageInfo.isCALConsStart(test)) {
                            // This letter is upper case, and all preceding letters in the identifier were also upper case.

                            if (identifierIndex + 1 >= size || LanguageInfo.isCALConsStart(identifier.charAt(identifierIndex + 1))) {
                                // This is the last letter, or the next letter is also upper case.
                                c = Character.toLowerCase(c);
                            } else {
                                // There's another letter, and it isn't upper case.
                                inCapsStart = false;
                            }
                        } else {
                            // This letter isn't upper case.
                            inCapsStart = false;
                        }
                    }
                    suggestionBuffer.append(c);
                }
            }
        }
    }

    /**
     * Class enumerating status of an identifier validation.
     * 
     * @author Iulian Radu
     */
    public static final class ValidationStatus {

        private final String enumType;

        private ValidationStatus(String enumType) {
            if (enumType == null) {
                throw new NullPointerException();
            }
            this.enumType = enumType;
        }

        /**
         * @return true if the validation returned an error
         */
        public boolean isError() {
            return (this != ValidationStatus.NO_ERROR);
        }

        /**
         * Enumeration indicating that no error has occurred
         */
        private static final ValidationStatus NO_ERROR = new ValidationStatus("NO_ERROR");

        /**
         * Error indicating that specified identifier was empty
         */
        public static final ValidationStatus WAS_EMPTY = new ValidationStatus("WAS_EMPTY");

        /**
         * Error indicating illegal characters at the start of identifier
         */
        public static final ValidationStatus INVALID_START = new ValidationStatus("INVALID_START");

        /**
         * Error indicating that the identifier is already a keyword
         */
        public static final ValidationStatus EXISTING_KEYWORD = new ValidationStatus("EXISTING_KEYWORD");

        /**
         * Error indicating that a character needs to be upper case
         */
        public static final ValidationStatus NEED_UPPER = new ValidationStatus("NEED_UPPER");

        /**
         * Error indicating that a character needs to be lower case
         */
        public static final ValidationStatus NEED_LOWER = new ValidationStatus("NEED_LOWER");

        /**
         * Error indicating that a character is invalid
         */
        public static final ValidationStatus INVALID_CONTENT = new ValidationStatus("INVALID_CONTENT");

        /**
         * Converts enumeration to string.
         */
        @Override
        public String toString() {
            return enumType;
        }
    }

    /**
     * Class used for holding result of validation operations.
     * This holds an indicator whether a validation was successful,
     * the suggested correction (if any), and a list of errors encountered.
     * 
     * @author Iulian Radu
     */
    public static class ValidatedIdentifier {
        /**
         * Indicates whether specified validation succeeded
         */
        private boolean valid;

        /**
         * Holds suggested valid identifier value.
         * If no suggestion could be created, this is null.
         */
        private String suggestion;

        /**
         * The list of ValidationStatus objects detailing the 
         * errors encountered. The errors are stored in 
         * the order they were added.
         */
        private List<ValidationStatus> errors = new ArrayList<ValidationStatus>();


        /**
         * Constructor
         */
        private ValidatedIdentifier() {
            valid = true;
            suggestion = null;
            errors.clear();
        }

        /**
         * @return suggested value
         */
        public String getSuggestion() {
            return suggestion;
        }

        /**
         * Sets the suggested value as specified
         * @param newSuggestion
         */
        private void setSuggestion(String newSuggestion) {
            suggestion = newSuggestion;
        }

        /**
         * @return true if suggestion exists; false if not
         */
        public boolean hasSuggestion() {
            return suggestion!=null;
        }

        /**
         * Retrieves the specified error
         * 
         * @param index error number
         * @return specified error
         */
        public ValidationStatus getNthError(int index) {
            return errors.get(index);
        }

        /**
         * Adds specified error to the existing error list 
         * 
         * @param error error to add
         */
        private void addError(ValidationStatus error) {
            if (error.isError()) {
                setValid(false);
                errors.add(error);
            }
        }

        /**
         * Retrieves the number of errors encountered
         * 
         * @return number of errors
         */
        public int getNErrors() {
            return errors.size();
        }

        /**
         * @return true if validation was successful; false if not
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Sets the value of valid field.
         * @param newValid
         */
        private void setValid(boolean newValid) {
            valid = newValid;
        }
    }

    /**
     * Make a valid CAL module name component, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * module name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated module name component (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidModuleNameComponent(String identifier) {
        return makeValidatedIdentifier(identifier, true);
    }

    /**
     * Make a valid CAL module name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * module name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated module name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidModuleName(String identifier) {

        String[] components = identifier.split("\\.");
        ValidatedIdentifier result = new ValidatedIdentifier();
        StringBuilder finalSuggestion = new StringBuilder();

        for (int i = 0; i < components.length; i++) {
            ValidatedIdentifier validatedComponent = new IdentifierValidator(components[i], true).makeIdentifierName();

            if (validatedComponent.hasSuggestion()) {
                if (i > 0) {
                    finalSuggestion.append('.');
                }
                finalSuggestion.append(validatedComponent.getSuggestion());
            }

            // if the validated component has no suggestion, we simply skip the component

            if (!validatedComponent.isValid()) {
                result.setValid(false);
            }

            for (int j = 0; j < validatedComponent.getNErrors(); j++) {
                result.addError(validatedComponent.getNthError(j));
            }
        }

        if (finalSuggestion.length() > 0) {
            result.setSuggestion(finalSuggestion.toString());
        }

        return result;
    }

    /**
     * Make a valid CAL function name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * function name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated function name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidFunctionName(String identifier) {
        return makeValidatedIdentifier(identifier, false);
    }

    /**
     * Make a valid CAL type constructor name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * type constructor name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated type constructor name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidTypeConstructorName(String identifier) {
        return makeValidatedIdentifier(identifier, true);
    }

    /**
     * Make a valid CAL type variable name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * type variable name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated type variable name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidTypeVariableName(String identifier) {
        return makeValidatedIdentifier(identifier, false);
    }

    /**
     * Make a valid CAL data constructor name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * data constructor name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated data constructor name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidDataConstructorName(String identifier) {
        return makeValidatedIdentifier(identifier, true);
    }

    /**
     * Make a valid CAL type class name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * type class name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated type class name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidTypeClassName(String identifier) {
        return makeValidatedIdentifier(identifier, true);
    }

    /**
     * Make a valid CAL class method name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * class method name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * @param identifier the string to convert from
     * @return a structure for the validated class method name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidClassMethodName(String identifier) {
        return makeValidatedIdentifier(identifier, false);
    }

    /**
     * Make a valid CAL identifier name from an arbitrary string.  This method removes all 
     * characters which are invalid, except for space characters, which may be replaced with underscores.
     * If the given string only contains invalid characters, then <code>null</code> is returned.  
     * If the output string is in fact a CAL keyword, then this method appends a number 
     * pad at the end of the identifier.
     * 
     * @param nameToConvert the string to convert from
     * @param isConstructor True for a constructor, false for a var.
     * 
     * @return the valid identifier name
     */
    public static String makeIdentifierName(String nameToConvert, boolean isConstructor) {
        ValidatedIdentifier validatedIdentifier = makeValidatedIdentifier(nameToConvert, isConstructor);
        return validatedIdentifier.isValid() ? nameToConvert : validatedIdentifier.getSuggestion();
    }

    /**
     * Make a valid CAL identifier name from an arbitrary string.  This method removes
     * all characters which are invalid and it replaces all space characters with
     * underscores.  If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method simply append a static text string "_0" at the end of
     * the output.
     * 
     * @param identifier the string to convert from
     * @return the valid identifier name
     */
    public static String makeIdentifierName(String identifier) {
        return makeIdentifierName(identifier, false);
    }

    /**
     * Checks that the specified character is valid for 
     * the start of a CAL constructor identifier, and returns an error if not.
     * 
     * @param c character to convert
     * @return NO_ERROR if no error; 
     *         NEED_UPPER if character needs to be upper cased
     *         INVALID_CONTENT if character cannot be corrected by changing case
     */
    static private ValidationStatus checkCALConsStart(char c) {
        if (LanguageInfo.isCALConsStart(c)) {
            return ValidationStatus.NO_ERROR;

        } else if (c >= 'a' && c <= 'z') {
            return ValidationStatus.NEED_UPPER;

        } else {
            return ValidationStatus.INVALID_CONTENT;
        }
    }

    /**
     * Checks that the specified character is valid for 
     * the content of a CAL constructor, and returns an error if not.
     * 
     * @param c character to convert
     * @return NO_ERROR if no error; 
     *         INVALID_CONTENT if character cannot be corrected by changing case 
     */
    static private ValidationStatus checkCALConsPart(char c) {
        return checkCALVarPart(c);
    }

    /**
     * Checks that the specified character is valid for 
     * the start of a CAL variable identifier, and returns an error if not.
     * 
     * @param c character to convert
     * @return NO_ERROR if no error; 
     *         NEED_LOWER if character needs to be lower cased
     *         INVALID_CONTENT if character cannot be corrected by a case change 
     */
    static private ValidationStatus checkCALVarStart(char c) {
        if (LanguageInfo.isCALVarStart(c)) {
            return ValidationStatus.NO_ERROR;

        } else if (c >= 'A' && c <= 'Z') {
            return ValidationStatus.NEED_LOWER;

        } else {
            return ValidationStatus.INVALID_CONTENT;
        }
    }

    /**
     * Checks that the specified character is valid for 
     * the start of a CAL constructor, and returns an error if not.
     * 
     * @param c character to convert
     * @return NO_ERROR if no error; 
     *         INVALID_CONTENT if character cannot be corrected by case change
     */
    static private ValidationStatus checkCALVarPart(char c) {
        if (LanguageInfo.isCALVarPart(c)) {
            return ValidationStatus.NO_ERROR;

        } else {
            return ValidationStatus.INVALID_CONTENT;
        }
    }

    /**
     * Converts the specified character into a valid identifier
     * character by taking into account the validation error.
     * 
     * @param c character to be corrected
     * @param e error from validating the character
     * @return valid character or \0 if unknown error
     */
    static private char makeValidCharacter(char c, ValidationStatus e) {
        if (e == ValidationStatus.NO_ERROR) {
            return c;

        } else if (e == ValidationStatus.NEED_UPPER) {
            return Character.toUpperCase(c);

        } else if (e == ValidationStatus.NEED_LOWER) {
            return Character.toLowerCase(c);

        } else if (e == ValidationStatus.INVALID_CONTENT) {
            return '_';

        } else if (e == null) {
            throw new NullPointerException();

        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Make a valid CAL identifier name, and indicate the corrections made.  This method 
     * removes all characters which are invalid and ensures camel casing on the resulting
     * identifier name. If the given string only contains invalid characters, then
     * <code>null</code> is returned.  If the output string is in fact a CAL keyword,
     * then this method appends a number pad at the end of the identifier.
     * 
     * If isConstructor is true, the argument needs to have a capital letter at its
     * beginning in order to be valid (use this for Constructors or Data Type names).
     * 
     * @param identifier the string to convert from
     * @param isConstructor true if identifier should be a constructor; false otherwise
     * @return a structure for the validated identifier name (containing validation
     * errors and its valid suggestion)
     */
    public static ValidatedIdentifier makeValidatedIdentifier(
        String identifier, 
        boolean isConstructor) {
        IdentifierValidator validator = new IdentifierValidator(identifier, isConstructor);
        return validator.makeIdentifierName();
    }

}
